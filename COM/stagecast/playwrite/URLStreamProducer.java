/* URLStreamProducer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class URLStreamProducer extends AbstractStreamProducer
{
    private URL _url;
    
    URLStreamProducer(URL url, boolean deflated) {
	super(deflated);
	_url = url;
    }
    
    URLStreamProducer(URL url) {
	this(url, false);
    }
    
    protected InputStream generateStream() throws IOException {
	return _url.openStream();
    }
}
