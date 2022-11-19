/* PrivilegedAppletBridge - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.security.AccessController;
import java.security.PrivilegedAction;

import COM.stagecast.ifc.netscape.application.FoundationApplet;

public abstract class PrivilegedAppletBridge extends FoundationApplet
{
    public void run() {
	try {
	    AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    superrun();
		    return null;
		}
	    });
	} catch (Throwable t) {
	    if (t instanceof ThreadDeath)
		throw (ThreadDeath) t;
	    final Throwable fatal = t;
	    AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    PlaywriteRoot.fatalError(fatal);
		    return null;
		}
	    });
	}
    }
    
    void superrun() {
	super.run();
    }
}
