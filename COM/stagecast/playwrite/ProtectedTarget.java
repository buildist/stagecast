/* ProtectedTarget - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Target;

class ProtectedTarget implements Target
{
    private Target _target;
    
    ProtectedTarget(Target target) {
	_target = target;
    }
    
    public void performCommand(String command, Object data) {
	try {
	    _target.performCommand(command, data);
	} catch (ThreadDeath td) {
	    Debug.print(true, "ProtectedTarget: Shutting down thread");
	    throw td;
	} catch (Throwable t) {
	    Debug.print(true, "ProtectedTarget: Invoking fatal error");
	    PlaywriteRoot.fatalError(t);
	}
    }
}
