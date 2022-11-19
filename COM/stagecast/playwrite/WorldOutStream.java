/* WorldOutStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import Acme.JPM.Encoders.GifEncoder;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.jpeg.JpegEncoder;

public class WorldOutStream extends ObjectOutputStream
    implements PlaywriteSystem.Properties, Debug.Constants
{
    public static final int GIF_FORMAT = 0;
    public static final int JPEG_FORMAT = 1;
    public static final int PIXMAP_FORMAT = 2;
    private static boolean v1_convert = false;
    private Vector _mediaProxies;
    private final World _world;
    private String _saveVersion;
    
    static void setV1ConvertFlag(boolean flag) {
	v1_convert = flag;
    }
    
    public static String getStorableData(BitmapManager bitmap, MediaProxy mp) {
	StreamProducer sp = bitmap.getMediaSource();
	boolean gotData = false;
	if (bitmap.getSourceFormat() == 1) {
	    gotData = false;
	    try {
		ASSERT.isNotNull(sp);
		Debug.print("debug.objectstore.media",
			    "Storing existing GIF/JPEG");
		mp.setData(sp.getRawDataChunk());
		gotData = true;
	    } catch (IOException e) {
		Debug.print(true, "No data for proxy ", mp.getName(), ": ", e);
	    }
	    if (gotData)
		return "IMAGE";
	}
	if (sp instanceof MediaStreamProducer && !v1_convert) {
	    MediaStreamProducer msp = (MediaStreamProducer) sp;
	    try {
		gotData = false;
		Debug.print("debug.objectstore.media",
			    "Storing existing media representation");
		byte[] data = msp.getRawDataChunk();
		if (!msp.isDeflated())
		    data = (ChunkStreamProducer.streamToDeflatedChunk
			    (new ByteArrayInputStream(data)));
		mp.setData(data);
		if (data != null)
		    gotData = true;
	    } catch (IOException e) {
		Debug.print(true, "No data for proxy ", mp.getName(), ": ", e);
	    }
	    if (gotData)
		return "RAWBITS";
	}
	int[] pixmap = new int[bitmap.width() * bitmap.height()];
	boolean grabbed = bitmap.grabPixels(pixmap);
	ASSERT.isTrue(grabbed);
	boolean hasTransparency = false;
	int colorCount = 0;
	Hashtable colorsUsed = new Hashtable(200);
	for (int i = 0; i < pixmap.length; i++) {
	    int color = pixmap[i];
	    hasTransparency = hasTransparency | color >>> 24 < 128;
	    Integer cint = new Integer(color);
	    if (!colorsUsed.contains(cint)) {
		colorCount++;
		colorsUsed.put(cint, cint);
	    }
	}
	Debug.print("debug.objectstore.media", "Image ", mp.getName(),
		    " uses " + colorCount, " colors - ",
		    hasTransparency ? "transparent" : "non-transparent");
	Debug.print("debug.objectstore.media", "    width: " + bitmap.width(),
		    " height: " + bitmap.height());
	byte[] mediaData;
	String format;
	boolean compress;
	if (colorCount < 256) {
	    Debug.print("debug.objectstore.media", "Converting image to GIF");
	    pixmap = null;
	    mediaData = getGifBits(bitmap);
	    format = "IMAGE";
	    compress = false;
	} else if (hasTransparency) {
	    Debug.print("debug.objectstore.media",
			"Converting image to CompressedPixmap");
	    mediaData = pixmapToBytes(pixmap);
	    format = "RAWBITS";
	    compress = true;
	} else {
	    Debug.print("debug.objectstore.media", "Converting image to JPEG");
	    pixmap = null;
	    mediaData = getJpegBits(bitmap);
	    format = "IMAGE";
	    compress = false;
	}
	if (compress)
	    mediaData = deflateMediaData(mediaData);
	mp.setData(mediaData);
	Debug.print("debug.objectstore.media",
		    "    data length: " + mediaData.length);
	return format;
    }
    
    public static String getStorableData(SystemSound snd, MediaProxy mp) {
	StreamProducer sp = snd.getMediaSource();
	byte[] mediaData = null;
	try {
	    mediaData = sp.getRawDataChunk();
	    if (!sp.isDeflated())
		mediaData = deflateMediaData(mediaData);
	} catch (IOException e) {
	    Debug.print(true, "No media data for ", mp.getName(), ": ", e);
	}
	mp.setData(mediaData);
	return "AU";
    }
    
    private static byte[] deflateMediaData(byte[] rawData) {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	try {
	    DeflaterOutputStream dos = new DeflaterOutputStream(baos);
	    dos.write(rawData);
	    dos.close();
	    baos.close();
	    Debug.print("debug.objectstore.media",
			"Deflating " + rawData.length,
			" to " + baos.toByteArray().length);
	} catch (IOException e) {
	    Debug.stackTrace(e);
	}
	return baos.toByteArray();
    }
    
    private static byte[] getGifBits(Bitmap bitmap) {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	try {
	    GifEncoder ge = new GifEncoder(bitmap.awtImage(), baos);
	    ge.encode();
	    baos.close();
	} catch (IOException ioexception) {
	    Debug.print(true, "GIF encoding failed: e");
	}
	return baos.toByteArray();
    }
    
    private static byte[] getJpegBits(Bitmap bitmap) {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	try {
	    JpegEncoder je = new JpegEncoder(bitmap.awtImage(), 70, baos);
	    je.Compress();
	    baos.close();
	} catch (IOException ioexception) {
	    Debug.print(true, "JPEG encoding failed: e");
	}
	return baos.toByteArray();
    }
    
    private static byte[] pixmapToBytes(int[] pixmap) {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	try {
	    for (int i = 0; i < pixmap.length; i++) {
		int color = pixmap[i];
		baos.write(color >> 24 & 0xff);
		baos.write(color >> 16 & 0xff);
		baos.write(color >> 8 & 0xff);
		baos.write(color & 0xff);
	    }
	    baos.close();
	} catch (IOException ioexception) {
	    Debug.print(true, "int[] to byte[] failed: e");
	}
	return baos.toByteArray();
    }
    
    WorldOutStream(OutputStream out, World world) throws IOException {
	super(out);
	this.enableReplaceObject(true);
	_mediaProxies = new Vector(50);
	_world = world;
	createPluginRegistry();
	try {
	    Method m = this.getClass().getMethod("useProtocolVersion",
						 new Class[] { Integer.TYPE });
	    Field f = this.getClass().getField("PROTOCOL_VERSION_1");
	    m.invoke(this, new Object[] { new Integer(f.getInt(this)) });
	} catch (NoSuchMethodException nosuchmethodexception) {
	    /* empty */
	} catch (NoSuchFieldException e) {
	    Debug.print(true,
			"Found method but not protocol version constant");
	    throw new PlaywriteInternalError(e.toString());
	} catch (SecurityException securityexception) {
	    /* empty */
	} catch (IllegalAccessException e) {
	    Debug.print(true, "Output stream access error: ", e);
	    Debug.print(true, e);
	} catch (InvocationTargetException e) {
	    Debug.print(true, "Bad target for useProtocolVersion: ", e);
	    Debug.print(true, e);
	}
    }
    
    void createPluginRegistry() {
	_world.setEmbeddedPlugins(new PluginRegistry());
    }
    
    void writeWorld() throws IOException {
	_world.prepareToWrite();
	try {
	    Debug.print("debug.objectstore", "Writing prologue - ",
			Debug.mem("debug.objectstore"));
	    writePrologue();
	    Debug.print("debug.objectstore", "Writing world - ",
			Debug.mem("debug.objectstore"));
	    this.writeObject(_world);
	    Debug.print("debug.objectstore", "Writing rules - ",
			Debug.mem("debug.objectstore"));
	    Enumeration items = _world.getPrototypes().getContents();
	    while (items.hasMoreElements()) {
		CharacterPrototype proto
		    = (CharacterPrototype) items.nextElement();
		Subroutine sub = proto.getMainSubroutine();
		this.writeObject(sub);
	    }
	    items = _world.getSpecialPrototypes().getContents();
	    while (items.hasMoreElements()) {
		CharacterPrototype proto
		    = (CharacterPrototype) items.nextElement();
		Subroutine sub = proto.getMainSubroutine();
		this.writeObject(sub);
	    }
	    _world.setCreatorVersion(_saveVersion);
	    Debug.print("debug.objectstore", "Writing end marker -  ",
			Debug.mem("debug.objectstore"));
	    this.writeObject("*End of world*");
	} finally {
	    _world.worldIsWritten();
	}
    }
    
    void writeExtensions(ZipOutputStream zip) throws IOException {
	Debug.print("debug.objectstore", "Writing extensions - ",
		    Debug.mem("debug.objectstore"));
	_world.getEmbeddedPlugins().writeAsZip(zip);
    }
    
    void writePluginInfo(ZipOutputStream zip) throws IOException {
	PluginRegistry reg = _world.getEmbeddedPlugins();
	Vector plugins = _world.getRequiredPlugins();
	if (plugins != null && plugins.size() >= 1) {
	    Debug.print("debug.objectstore", "Writing plugin list");
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintWriter pw = new PrintWriter(baos);
	    for (int i = 0; i < plugins.size(); i++) {
		String name = (String) plugins.elementAt(i);
		String className = reg.baseClassForExtension(name);
		pw.print(name);
		pw.print(",");
		pw.println(className);
	    }
	    pw.close();
	    storeMediaData(zip, new ZipEntry(".plugins"), baos.toByteArray());
	}
    }
    
    void writeMedia(ZipOutputStream zip) throws IOException {
	int count = 0;
	int incr = 0;
	Debug.print("debug.objectstore", "Writing media - ",
		    Debug.mem("debug.objectstore"));
	count = _mediaProxies.size();
	if (count > 0)
	    incr = (100 - PlaywriteRoot.getProgress()) * 10 / count;
	int tenx = 0;
	for (int i = 0; i < count; i++) {
	    MediaProxy proxy = (MediaProxy) _mediaProxies.elementAt(i);
	    StreamedMediaItem item = proxy.getMedia();
	    String name = proxy.getName();
	    byte[] mediaData = proxy.getData();
	    try {
		storeMediaData(zip, new ZipEntry(name), mediaData);
	    } catch (Error e) {
		Debug.print(true, "Entry ", name, " will be invalid");
		Debug.stackTrace(e);
		throw e;
	    }
	    zip.closeEntry();
	    if (++tenx % 10 == 0)
		PlaywriteRoot.incrementProgress(incr, 100);
	}
	Debug.print("debug.objectstore", "Media complete - ",
		    Debug.mem("debug.objectstore"));
    }
    
    private void storeMediaData(ZipOutputStream zip, ZipEntry entry,
				byte[] mediaData) throws IOException {
	CRC32 crc = new CRC32();
	crc.update(mediaData, 0, mediaData.length);
	entry.setMethod(0);
	entry.setCrc(crc.getValue());
	entry.setSize((long) mediaData.length);
	zip.putNextEntry(entry);
	zip.write(mediaData);
    }
    
    void updateStreamingProducers() {
	Debug.print(true, "Setting producers to ", _world);
	for (int i = 0; i < _mediaProxies.size(); i++) {
	    MediaProxy mp = (MediaProxy) _mediaProxies.elementAt(i);
	    StreamedMediaItem item = mp.getMedia();
	    StreamProducer sp = item.getMediaSource();
	    if (sp instanceof MediaStreamProducer) {
		((MediaStreamProducer) sp).updateSource(_world, mp.getName(),
							mp.getName()
							    .startsWith("i"));
		if (item instanceof BitmapManager)
		    ((BitmapManager) item)
			.resetDelegate(sp, "IMAGE".equals(mp.getFormat()));
	    }
	}
	_mediaProxies.removeAllElements();
    }
    
    protected void annotateClass(Class cl) throws IOException {
	if (!PlaywriteRoot.getClassRegistry().isRegistered(cl)) {
	    if (cl.isPrimitive() || cl.isArray() || cl == String.class
		|| cl == Boolean.class || Number.class.isAssignableFrom(cl))
		return;
	    throw new PlaywriteInternalError
		      ("Attempt to store unregistered class " + cl);
	}
	PlaywriteLoader _loader = _world.getLoader();
	if (_loader != null) {
	    PluginRegistry plugins = _loader.getPlugins();
	    String resourceName = cl.getName().replace('.', '/') + ".class";
	    java.util.zip.ZipFile src = plugins.resourceSource(resourceName);
	    if (src != null) {
		plugins.copyToRegistry(resourceName,
				       _world.getEmbeddedPlugins());
		if (!plugins.isEmbedded(resourceName))
		    _world.addRequiredPlugin(plugins.pluginName(resourceName));
	    }
	}
    }
    
    protected Object replaceObject(Object obj) {
	if (obj == null)
	    return null;
	StorageProxy sp = StorageProxy.proxyFor(obj);
	if (sp != null)
	    return sp;
	VariableProxy vp = VariableProxy.proxyFor(obj);
	if (vp != null)
	    return vp;
	if (obj instanceof Worldly) {
	    try {
		World w = ((Worldly) obj).getWorld();
		if (w != _world)
		    Debug.print(true, "Object ", obj, " returns getWorld() = ",
				((Worldly) obj).getWorld());
	    } catch (Throwable t) {
		Debug.print(true, "Object ", obj, " threw exception ", t,
			    " attempting getWorld()");
	    }
	}
	MediaProxy mp = MediaProxy.proxyFor(obj);
	if (mp != null) {
	    _mediaProxies.addElement(mp);
	    Debug.print("debug.objectstore.detail", "  writing media proxy: ",
			mp.getName());
	    return mp;
	}
	Debug.print("debug.objectstore.detail", "  writing object: ", obj);
	return obj;
    }
    
    void saveItems(Enumeration items) throws IOException {
	while (items.hasMoreElements())
	    this.writeObject(items.nextElement());
    }
    
    void writePrologue() throws IOException {
	_saveVersion = (PlaywriteRoot.getProductName() + " "
			+ PlaywriteRoot.getVersionString());
	this.writeUTF(_saveVersion);
	this.writeInt(PlaywriteRoot.getObjectStoreVersion());
	ClassRegistry.dumpVersionRegistry(PlaywriteRoot.getClassRegistry(),
					  this);
    }
    
    void writeVector(Vector v) throws IOException {
	if (v == null)
	    this.writeInt(-1);
	else {
	    int size = v.size();
	    this.writeInt(size);
	    for (int i = 0; i < size; i++)
		this.writeObject(v.elementAt(i));
	}
    }
    
    void writeHashtable(Hashtable h, boolean hasPoints) throws IOException {
	int size = h.size();
	this.writeInt(size);
	this.writeBoolean(hasPoints);
	Enumeration keys = h.keys();
	while (keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    this.writeObject(key);
	    if (hasPoints) {
		Point pt = (Point) h.get(key);
		this.writeInt(pt.x);
		this.writeInt(pt.y);
	    } else
		this.writeObject(h.get(key));
	}
    }
}
