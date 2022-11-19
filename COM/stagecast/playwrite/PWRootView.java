/* PWRootView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Event;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

public class PWRootView extends RootView
{
    public static String COMMAND_DIRTYVIEW
	= "COM.stagecast.playwrite.PWRootView:DIRTYVIEW".intern();
    public static String COMMAND_CLEANVIEW
	= "COM.stagecast.playwrite.PWRootView:CLEANVIEW".intern();
    Vector _targets;
    private TransparentView _tv;
    boolean _mouseDownState;
    
    public PWRootView() {
	_targets = new Vector();
	_tv = null;
	_mouseDownState = false;
    }
    
    public PWRootView(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public PWRootView(int x, int y, int width, int height) {
	super(x, y, width, height);
	_targets = new Vector();
	_tv = null;
	_mouseDownState = false;
    }
    
    public void processEvent(Event event) {
	try {
	    _processEvent(event);
	} catch (Throwable t) {
	    if (t instanceof ThreadDeath)
		throw (ThreadDeath) t;
	    PlaywriteRoot.fatalError(t);
	}
    }
    
    private void _processEvent(Event event) {
	if (event instanceof MouseEvent && event.type() == -1) {
	    View lastMouseView = this.mouseView();
	    if (_mouseDownState && lastMouseView instanceof InternalWindow) {
		lastMouseView.mouseUp(new MouseEvent(0L, -3, 0, 0, 0));
		_mouseDownState = false;
	    } else
		_mouseDownState = true;
	    super.processEvent(event);
	} else
	    super.processEvent(event);
	if (event instanceof MouseEvent && event.type() == -3)
	    _mouseDownState = false;
    }
    
    protected void markDirty(View view) {
	super.markDirty(view);
	updateTargets(COMMAND_DIRTYVIEW, view);
    }
    
    protected void markClean(View view) {
	super.markClean(view);
	updateTargets(COMMAND_CLEANVIEW, view);
    }
    
    public void addTarget(Target target) {
	_targets.addElement(target);
    }
    
    public void removeTarget(Target target) {
	_targets.removeElement(target);
    }
    
    private void updateTargets(String command, Object data) {
	for (int i = 0; i < _targets.size(); i++)
	    ((Target) _targets.elementAt(i)).performCommand(command, data);
    }
}
