/* ChunkStreamProducer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterOutputStream;

public class ChunkStreamProducer extends AbstractStreamProducer
{
    private byte[] _chunk;
    
    public static byte[] streamToDeflatedChunk(InputStream is)
	throws IOException {
	byte[] buffer = new byte[4096];
	int count = 0;
	ByteArrayOutputStream result = new ByteArrayOutputStream();
	DeflaterOutputStream dest = new DeflaterOutputStream(result);
	while (count >= 0) {
	    count = is.read(buffer);
	    if (count > 0)
		dest.write(buffer, 0, count);
	}
	dest.close();
	result.close();
	return result.toByteArray();
    }
    
    ChunkStreamProducer(byte[] chunk, boolean deflated) {
	super(deflated);
	_chunk = chunk;
    }
    
    ChunkStreamProducer(byte[] chunk) {
	this(chunk, false);
    }
    
    protected InputStream generateStream() {
	return new ByteArrayInputStream(_chunk);
    }
    
    public byte[] getRawDataChunk() {
	return _chunk;
    }
}
