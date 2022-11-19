/* TempStreamProducer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.InputStream;

public class TempStreamProducer extends AbstractStreamProducer
{
    private String _id;
    
    TempStreamProducer(String id, boolean deflated) {
	super(deflated);
	_id = id;
    }
    
    TempStreamProducer(World world, String id) {
	this(id, false);
    }
    
    public String getID() {
	return _id;
    }
    
    protected InputStream generateStream() {
	try {
	    return PlaywriteRoot.getTempManager().getEntry(_id);
	} catch (IOException e) {
	    Debug.stackTrace(e);
	    return null;
	}
    }
}
