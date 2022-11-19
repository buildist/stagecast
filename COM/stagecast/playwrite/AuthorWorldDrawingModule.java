/* AuthorWorldDrawingModule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Event;
import COM.stagecast.ifc.netscape.application.EventFilter;
import COM.stagecast.ifc.netscape.application.EventProcessor;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.util.Vector;

class AuthorWorldDrawingModule implements WorldDrawingModule
{
    private Event drawEvent = new DrawEvent(new EventProcessor() {
	public void processEvent(Event ev) {
	    handleDrawEvent(true);
	}
    });
    private DrawEvent refreshEvent = new DrawEvent(new EventProcessor() {
	public void processEvent(Event ev) {
	    handleDrawEvent(false);
	}
    });
    private World _world;
    private Vector _syncTargets = new Vector(64);
    private Vector _syncCommands = new Vector(64);
    private Vector _syncData = new Vector(64);
    private static final Object SYNC_NULL = new Object();
    private boolean _inSyncPhase = false;
    
    private class DrawEvent extends Event
    {
	DrawEvent(EventProcessor processor) {
	    this.setProcessor(processor);
	}
    }
    
    public AuthorWorldDrawingModule(World w) {
	_world = w;
    }
    
    public boolean isInSyncPhase() {
	return _world.getState() != World.RUNNING || _inSyncPhase;
    }
    
    public synchronized void addSyncAction(Target target, String command,
					   Object data) {
	if (_world.inWorldThread() || !isInSyncPhase()) {
	    _syncTargets.addElement(target);
	    _syncCommands
		.addElement(command == null ? (Object) SYNC_NULL : command);
	    _syncData.addElement(data == null ? SYNC_NULL : data);
	} else
	    target.performCommand(command, data);
    }
    
    private synchronized void executeSyncActions() {
	ASSERT.isInEventThread();
	int size = _syncTargets.size();
	for (int i = 0; i < size; i++) {
	    Object command = _syncCommands.elementAt(i);
	    Object data = _syncData.elementAt(i);
	    if (command == SYNC_NULL)
		command = null;
	    if (data == SYNC_NULL)
		data = null;
	    ((Target) _syncTargets.elementAt(i))
		.performCommand((String) command, data);
	}
	_syncTargets.removeAllElements();
	_syncCommands.removeAllElements();
	_syncData.removeAllElements();
    }
    
    public void handleDrawEvent(boolean updateStages) {
	_inSyncPhase = true;
	WorldView worldView = _world.getWorldView();
	if (worldView != null && worldView.isInViewHierarchy()
	    && _world.isRunning()) {
	    COM.stagecast.ifc.netscape.application.View currentFocusedView
		= PlaywriteRoot.getMainRootView().focusedView();
	    if (!(currentFocusedView instanceof TextView)
		&& !(currentFocusedView instanceof TextField))
		worldView.setFocusedView();
	}
	executeSyncActions();
	if (updateStages) {
	    for (int i = 0; i < worldView.numberOfStageViews(); i++) {
		BoardView view = worldView.getStageView(i);
		if (view != null)
		    view.drawToBuffer(false);
	    }
	}
	PlaywriteRoot.setWantsSync(true);
	_inSyncPhase = false;
    }
    
    public void screenRefresh() {
	if (_world.inWorldThread())
	    Application.application().eventLoop()
		.addEventAndWait(refreshEvent);
	else
	    Application.application().eventLoop().addEvent(refreshEvent);
    }
    
    public void forceRepaint() {
	if (_world.isRunning() && !_world.isSuspendedForDebug()) {
	    _world.blockForClock();
	    requestDrawStages();
	}
    }
    
    public void requestDrawStages() {
	if (PlaywriteRoot.app().inEventThread())
	    handleDrawEvent(true);
	else
	    Application.application().eventLoop().addEventAndWait(drawEvent);
    }
    
    public void disableStageDrawing() {
	ASSERT.isTrue(_world.isRunning() ^ true);
	for (int i = 0; i < _world.getWorldView().numberOfStageViews(); i++) {
	    BoardView view = _world.getWorldView().getStageView(i);
	    if (view != null)
		view.disableDrawing();
	}
    }
    
    public void reenableStageDrawing() {
	ASSERT.isTrue(_world.isRunning() ^ true);
	for (int i = 0; i < _world.getWorldView().numberOfStageViews(); i++) {
	    BoardView view = _world.getWorldView().getStageView(i);
	    if (view != null)
		view.reenableDrawing();
	}
    }
    
    public void close() {
	synchronized (this) {
	    _syncTargets.removeAllElements();
	    _syncCommands.removeAllElements();
	    _syncData.removeAllElements();
	    _syncTargets = null;
	    _syncCommands = null;
	    _syncData = null;
	}
	PlaywriteRoot.app().eventLoop().filterEvents(new EventFilter() {
	    public Object filterEvents(Vector events) {
		int i = events.size();
		while (--i > 0) {
		    Object event = events.elementAt(i);
		    if (event == drawEvent || event == refreshEvent)
			events.removeElementAt(i);
		}
		return null;
	    }
	});
    }
}
