/* WorldZipFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import COM.stagecast.ifc.netscape.application.FoundationApplet;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class WorldZipFile extends ZipFile
    implements ResourceIDs.CommandIDs, Debug.Constants
{
    static final String WORLD_ENTRY = "iworld";
    static final String OLD_WORLD_ENTRY = "world";
    static final String PLUGIN_INFO_ENTRY = ".plugins";
    private static Hashtable appletExtensions = new Hashtable();
    
    public static interface DataSource
    {
	public File getFile();
	
	public InputStream sourceToStream() throws IOException;
    }
    
    public static class FileSource implements DataSource
    {
	private File _file;
	
	public FileSource(File file) {
	    _file = file;
	}
	
	public File getFile() {
	    return _file;
	}
	
	public InputStream sourceToStream() throws IOException {
	    return new FileInputStream(_file);
	}
    }
    
    public static class URLSource implements DataSource
    {
	private URL _url;
	
	public URLSource(URL url) {
	    _url = url;
	}
	
	public File getFile() {
	    return null;
	}
	
	public InputStream sourceToStream() throws IOException {
	    return _url.openStream();
	}
    }
    
    static boolean validateContent(InputStream in) {
	boolean valid = false;
	in.mark(2);
	try {
	    byte[] buf = new byte[2];
	    int count = in.read(buf);
	    valid = count == 2 && buf[0] == 80 && buf[1] == 75;
	} catch (IOException ioexception) {
	    /* empty */
	} finally {
	    try {
		in.reset();
	    } catch (IOException ioexception) {
		/* empty */
	    }
	}
	return valid;
    }
    
    static WorldOutStream writeWorld(OutputStream out, World world)
	throws IOException {
	ZipOutputStream zip = new ZipOutputStream(out);
	WorldOutStream wos = null;
	zip.setMethod(0);
	long startTime = System.currentTimeMillis();
	long totalTime = 0L;
	BitmapManager.enforceMinCacheLimits();
	try {
	    WorldOutStream.setV1ConvertFlag(world.getFSVersion() == 1);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    wos = new WorldOutStream(new DeflaterOutputStream(baos), world);
	    wos.writeWorld();
	    wos.flush();
	    wos.close();
	    byte[] worldbits = baos.toByteArray();
	    CRC32 crc = new CRC32();
	    crc.update(worldbits, 0, worldbits.length);
	    baos = null;
	    ZipEntry entry = new ZipEntry("iworld");
	    entry.setMethod(0);
	    entry.setCrc(crc.getValue());
	    entry.setSize((long) worldbits.length);
	    zip.putNextEntry(entry);
	    zip.write(worldbits, 0, worldbits.length);
	    zip.closeEntry();
	    long time = System.currentTimeMillis() - startTime;
	    totalTime += time;
	    Debug.print("debug.statistics",
			"World written in " + (double) (float) time / 1000.0,
			" seconds");
	    startTime = System.currentTimeMillis();
	    wos.writeExtensions(zip);
	    time = System.currentTimeMillis() - startTime;
	    totalTime += time;
	    Debug.print("debug.statistics",
			("Extensions written in "
			 + (double) (float) time / 1000.0),
			" seconds");
	    startTime = System.currentTimeMillis();
	    wos.writePluginInfo(zip);
	    wos.writeMedia(zip);
	    zip.finish();
	    out.flush();
	} finally {
	    BitmapManager.enforceDefaultCacheLimits();
	}
	long time = System.currentTimeMillis() - startTime;
	totalTime += time;
	Debug.print("debug.statistics",
		    "Media written in " + (double) (float) time / 1000.0,
		    " seconds");
	Debug.print("debug.statistics",
		    ("=Total file written in "
		     + (double) (float) totalTime / 1000.0),
		    " seconds=");
	return wos;
    }
    
    public static World readData(DataSource source, String name)
	throws IOException {
	World world = null;
	InputStream in = null;
	long startTime = System.currentTimeMillis();
	in = new BufferedInputStream(source.sourceToStream(), 8192);
	if (validateContent(in)) {
	    File srcFile = source.getFile();
	    if (srcFile == null) {
		world = loadFromStream(in, source, startTime);
		if (world != null)
		    world.setName(name);
	    } else {
		in.close();
		world = loadFromZip(srcFile);
	    }
	} else if (DRInputStream.validateContent(in))
	    world = loadFromDRx(in);
	long totalTime = System.currentTimeMillis() - startTime;
	Debug.print("debug.statistics",
		    "File read in " + (double) (float) totalTime / 1000.0,
		    " seconds");
	return world;
    }
    
    public static World readFile(File file) throws IOException {
	return readData(new FileSource(file), file.getName());
    }
    
    public static World readURL(URL url) throws IOException {
	return readData(new URLSource(url),
			Util.dePercentString(url.getFile()));
    }
    
    private static World loadFromStream(InputStream in, DataSource source,
					long startTime) throws IOException {
	Debug.print("debug.world", "Stream in data file");
	Hashtable mediaNameMap = new Hashtable(50);
	WorldZipStream wzs = new WorldZipStream(in, mediaNameMap);
	wzs.readMedia();
	PlaywriteRoot.setProgress(50);
	long totalTime = System.currentTimeMillis() - startTime;
	Debug.print("debug.statistics",
		    "Media pass: " + (double) (float) totalTime / 1000.0,
		    " seconds");
	String tempName = (String) mediaNameMap.get(".plugins");
	if (tempName != null && !PlaywriteRoot.isApplication())
	    initPlugins(tempName);
	World world = wzs.readWorld();
	in.close();
	return world;
    }
    
    private static World loadFromZip(File srcFile) throws IOException {
	World world = null;
	WorldZipFile wz = new WorldZipFile(srcFile);
	try {
	    world = wz.readWorld();
	    if (world != null)
		world.setSourceFile(srcFile);
	} finally {
	    if (world == null)
		wz.close();
	}
	return world;
    }
    
    private static World loadFromDRx(InputStream in) throws IOException {
	World world = World.createAndStartWorld(true);
	world.removeAllStagesDRHack();
	try {
	    DRInputStream dris = new DRInputStream(in, world);
	    dris.readWorld();
	    world.setCreatorVersion(world.getCreatorVersion()
				    + " (translated from Cocoa)");
	    world.setMenuCommandEnabled("command sw", false);
	} catch (RecoverableException recoverable) {
	    try {
		world.close();
	    } catch (Throwable throwable) {
		/* empty */
	    }
	    world = null;
	    throw recoverable;
	} catch (Exception e) {
	    try {
		world.close();
	    } catch (Throwable throwable) {
		/* empty */
	    }
	    world = null;
	    Debug.stackTrace(e);
	} finally {
	    in.close();
	}
	return world;
    }
    
    private static void initPlugins(String tempEntry) throws IOException {
	InputStream is
	    = new TempStreamProducer(tempEntry, false).generateStream();
	BufferedReader br = new BufferedReader(new InputStreamReader(is));
	String line = null;
	while ((line = br.readLine()) != null) {
	    int breaker = line.indexOf(",");
	    String name = line.substring(0, breaker);
	    String className = line.substring(breaker + 1);
	    boolean ok = false;
	    try {
		Debug.print(true, className);
		Class cl = ((FoundationApplet) PlaywriteRoot.app().applet())
			       .classForName(className);
		if (appletExtensions.get(cl) != null)
		    ok = true;
		else {
		    appletExtensions.put(cl, cl);
		    ok = (PluginRegistry.invokeExtensionInit
			  (cl, new PluginRegistry.ExtensionID(name)));
		}
	    } catch (ClassNotFoundException classnotfoundexception) {
		throw new ExtensionMissingException(name, (String) null);
	    }
	    if (!ok)
		throw new ExtensionMissingException(name, (String) null);
	}
    }
    
    WorldZipFile(File file) throws IOException {
	super(file);
    }
    
    World readWorld() throws IOException {
	ZipEntry worldEntry = this.getEntry("iworld");
	InputStream inStream;
	if (worldEntry == null) {
	    worldEntry = this.getEntry("world");
	    if (worldEntry == null)
		return null;
	    inStream = this.getInputStream(worldEntry);
	} else
	    inStream
		= new InflaterInputStream(this.getInputStream(worldEntry));
	WorldInStream wis = new WorldInStream(inStream, this);
	World world = wis.readWorld();
	wis.close();
	return world;
    }
}
