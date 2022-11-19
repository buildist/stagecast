/* RestartOutStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.OutputStream;

class RestartOutStream extends WorldOutStream
{
    private World _world;
    
    RestartOutStream(OutputStream out, World world) throws IOException {
	super(out, world);
	_world = world;
    }
    
    protected Object replaceObject(Object obj) {
	Object proxy = RestartProxy.proxyFor(_world, obj);
	if (proxy == null)
	    proxy = super.replaceObject(obj);
	return proxy == null ? obj : proxy;
    }
    
    protected void annotateClass(Class cl) throws IOException {
	/* empty */
    }
    
    void createPluginRegistry() {
	/* empty */
    }
}
