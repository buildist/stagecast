/* AbstractStreamProducer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

public abstract class AbstractStreamProducer implements StreamProducer
{
    private boolean _deflated = false;
    
    public static StreamProducer createStreamProducer(InputStream is,
						      boolean deflated) {
	TempFileChunkManager mgr = PlaywriteRoot.getTempManager();
	String name = mgr.fillNewEntry(is);
	return new TempStreamProducer(name, deflated);
    }
    
    public static StreamProducer createStreamProducer(byte[] data,
						      boolean deflated) {
	return createStreamProducer(new ByteArrayInputStream(data), deflated);
    }
    
    public static StreamProducer createStreamProducer(File srcFile) {
	try {
	    return createStreamProducer(new FileInputStream(srcFile), false);
	} catch (FileNotFoundException e) {
	    Debug.stackTrace(e);
	    return null;
	}
    }
    
    protected AbstractStreamProducer(boolean deflated) {
	_deflated = deflated;
    }
    
    protected void setDeflated(boolean flag) {
	_deflated = flag;
    }
    
    protected abstract InputStream generateStream() throws IOException;
    
    public final InputStream makeInputStream() throws IOException {
	InputStream baseStream = generateStream();
	return (_deflated ? (InputStream) new InflaterInputStream(baseStream)
		: baseStream);
    }
    
    public final boolean isDeflated() {
	return _deflated;
    }
    
    public byte[] getDataChunk() throws IOException {
	return (_deflated ? Util.streamToByteArray(makeInputStream())
		: getRawDataChunk());
    }
    
    public byte[] getRawDataChunk() throws IOException {
	return Util.streamToByteArray(generateStream());
    }
    
    public StreamProducer copy() throws IOException {
	return createStreamProducer(generateStream(), _deflated);
    }
}
