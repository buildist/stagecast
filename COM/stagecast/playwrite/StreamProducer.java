/* StreamProducer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.InputStream;

public interface StreamProducer
{
    public InputStream makeInputStream() throws IOException;
    
    public byte[] getDataChunk() throws IOException;
    
    public boolean isDeflated();
    
    public byte[] getRawDataChunk() throws IOException;
    
    public StreamProducer copy() throws IOException;
}
