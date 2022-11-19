/* ResourceStreamProducer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.InputStream;

public class ResourceStreamProducer extends AbstractStreamProducer
{
    private Class _class;
    private String _resName;
    
    public ResourceStreamProducer(Class classForLoader, String resName) {
	super(false);
	_class = classForLoader;
	_resName = resName;
    }
    
    protected InputStream generateStream() throws IOException {
	InputStream is = _class.getResourceAsStream(_resName);
	if (is == null) {
	    Debug.print(true, "Missing resource: ", _resName);
	    Debug.stackTrace();
	}
	return is;
    }
}
