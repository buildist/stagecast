/* MediaStreamProducer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MediaStreamProducer extends AbstractStreamProducer
{
    private World _world;
    private String _id;
    
    MediaStreamProducer(World world, String id, boolean deflated) {
	super(deflated);
	updateSource(world, id, deflated);
    }
    
    MediaStreamProducer(World world, String id) {
	this(world, id, false);
    }
    
    void updateSource(World world, String id, boolean deflated) {
	_world = world;
	_id = id;
	this.setDeflated(deflated);
    }
    
    String getMediaID() {
	return _id;
    }
    
    protected InputStream generateStream() {
	ZipFile zip = null;
	try {
	    zip = _world.getMediaSource();
	    return zip.getInputStream(zip.getEntry(_id));
	} catch (Exception e) {
	    Debug.print(true, "Error fetching zip id: " + _id,
			" from file: " + zip.getName());
	    Debug.stackTrace(e);
	    return null;
	}
    }
    
    public byte[] getRawDataChunk() throws IOException {
	ZipFile zip = null;
	try {
	    zip = _world.getMediaSource();
	    ZipEntry entry = zip.getEntry(_id);
	    return Util.streamToByteArray(zip.getInputStream(entry),
					  entry.getSize());
	} catch (Exception e) {
	    Debug.print(true, "Error fetching zip id: " + _id,
			" from file: " + zip.getName());
	    Debug.stackTrace(e);
	    return null;
	}
    }
    
    public byte[] getDataChunk() throws IOException {
	if (this.isDeflated()) {
	    byte[] compressed = getRawDataChunk();
	    InputStream is = (new InflaterInputStream
			      (new ByteArrayInputStream(compressed)));
	    return Util.streamToByteArray(is);
	}
	return getRawDataChunk();
    }
}
