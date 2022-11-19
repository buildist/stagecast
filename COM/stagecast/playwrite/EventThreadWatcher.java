/* EventThreadWatcher - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Target;

public class EventThreadWatcher implements Watcher
{
    private Watcher _watcher;
    private boolean _wait;
    
    public EventThreadWatcher(Watcher watcher) {
	this(watcher, false);
    }
    
    public EventThreadWatcher(Watcher watcher, boolean wait) {
	_watcher = watcher;
	_wait = wait;
    }
    
    public void update(final Object updateTarget, final Object updateValue) {
	if (PlaywriteRoot.app().inEventThread())
	    _watcher.update(updateTarget, updateValue);
	else {
	    Target target = new Target() {
		public void performCommand(String s, Object o) {
		    _watcher.update(updateTarget, updateValue);
		}
	    };
	    if (_wait)
		PlaywriteRoot.app().performCommandAndWait(target, null, null);
	    else
		PlaywriteRoot.app().performCommandLater(target, null, null);
	}
    }
}
