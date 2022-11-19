/* MediaProxy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

class MediaProxy implements Externalizable, Resolvable
{
    static final int BITMAP_MEDIA = 1;
    static final int SYSTEM_SOUND_MEDIA = 2;
    static final String RAW_BITMAP_FORMAT = "RAWBITS";
    static final String NATIVE_BITMAP_FORMAT = "IMAGE";
    static final String SOUND_FORMAT = "AU";
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108753642802L;
    private static int nextID = 0;
    private String _name;
    private int _mediaType;
    private String _mediaFormat;
    private int _width;
    private int _height;
    private transient StreamedMediaItem _mediaObject;
    private transient byte[] _mediaData;
    
    static MediaProxy proxyFor(Object obj) {
	if (obj instanceof StreamedMediaItem)
	    return new MediaProxy((StreamedMediaItem) obj);
	return null;
    }
    
    private MediaProxy(StreamedMediaItem mediaObj) {
	boolean compressed = false;
	int id = ++nextID;
	if (mediaObj instanceof BitmapManager) {
	    BitmapManager bmp = (BitmapManager) mediaObj;
	    _width = bmp.width();
	    _height = bmp.height();
	    _mediaType = 1;
	    _mediaFormat = WorldOutStream.getStorableData(bmp, this);
	    compressed = _mediaFormat != "IMAGE";
	} else if (mediaObj instanceof SystemSound) {
	    _mediaType = 2;
	    _mediaFormat
		= WorldOutStream.getStorableData((SystemSound) mediaObj, this);
	    compressed = true;
	}
	_name = (compressed ? "i" : "") + "MP" + id;
	_mediaObject = mediaObj;
    }
    
    public MediaProxy() {
	/* empty */
    }
    
    String getName() {
	return _name;
    }
    
    StreamedMediaItem getMedia() {
	return _mediaObject;
    }
    
    boolean isCompressed() {
	return _name.charAt(0) == 'i';
    }
    
    void setData(byte[] data) {
	_mediaData = data;
    }
    
    byte[] getData() {
	return _mediaData;
    }
    
    String getFormat() {
	return _mediaFormat;
    }
    
    public Object resolve(WorldBuilder wb) {
	java.util.zip.ZipFile zip = wb.getMediaContainer();
	StreamProducer src = null;
	if (zip == null) {
	    String oldName = _name;
	    _name = wb.mapName(_name);
	    src = new TempStreamProducer(_name, oldName.startsWith("i"));
	} else
	    src = new MediaStreamProducer(wb.getTargetWorld(), _name,
					  _name.startsWith("i"));
	Object resolved;
	switch (_mediaType) {
	case 1:
	    if ("IMAGE".equals(_mediaFormat))
		resolved = BitmapManager.createNativeBitmapManager(src, _width,
								   _height);
	    else
		resolved
		    = BitmapManager.createBitmapManager(src, _width, _height);
	    break;
	case 2:
	    resolved = new SystemSound(src, _name);
	    break;
	default:
	    resolved = null;
	}
	ASSERT.isNotNull(resolved, "Invalid MediaProxy type");
	ObjectSieve sieve = wb.getTargetWorld().getObjectSieve();
	if (sieve != null)
	    sieve.creation(resolved);
	return resolved;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeUTF(_name);
	out.writeInt(_mediaType);
	out.writeUTF(_mediaFormat);
	out.writeInt(_width);
	out.writeInt(_height);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_name = in.readUTF();
	_mediaType = in.readInt();
	_mediaFormat = in.readUTF();
	_width = in.readInt();
	_height = in.readInt();
    }
}
