/* FatalErrorNotifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class FatalErrorNotifier implements Runnable
{
    private Runnable _runnable;
    
    public FatalErrorNotifier(Runnable runnable) {
	_runnable = runnable;
    }
    
    public void run() {
	try {
	    _runnable.run();
	} catch (Throwable t) {
	    if (t instanceof ThreadDeath)
		throw (ThreadDeath) t;
	    PlaywriteRoot.fatalError(t);
	}
    }
}
