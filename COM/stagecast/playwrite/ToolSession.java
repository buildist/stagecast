/* ToolSession - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.View;

public class ToolSession
    implements Debug.Constants, ScrollableArea.AutoScrollReason
{
    public static final int SHIFT_MASK = 1;
    public static final int CONTROL_MASK = 2;
    public static final int META_MASK = 4;
    public static final int ALT_MASK = 8;
    private static ToolSession currentSession = null;
    Object data;
    Tool toolType;
    Image image;
    int mouseDownX;
    int mouseDownY;
    int mouseX;
    int mouseY;
    ToolSource source;
    ToolDestination destination;
    int modifiers;
    View sourceView;
    ViewGlue destinationView;
    ToolView toolView;
    boolean isAccepting;
    boolean _resetFlag;
    boolean accepted;
    Point _hotSpot = new Point(0, 0);
    
    static void cancelCurrentSession() {
	if (currentSession != null)
	    currentSession.cancelSession(false);
    }
    
    public ToolSession(ToolSource source, Image image, int mouseX, int mouseY,
		       Tool toolType, Object data) {
	sourceView = source.sourceView(this);
	this.source = source;
	this.image = image;
	this.mouseX = mouseX;
	this.mouseY = mouseY;
	this.toolType = toolType;
	this.data = data;
	destination = null;
	_resetFlag = false;
	toolView = new ToolView(this, image);
	Selection.unselectAll();
	currentSession = this;
	ScrollableArea.addAutoScrollReason(this);
    }
    
    public boolean isAccepting() {
	return isAccepting;
    }
    
    public void cancelSession(boolean optionContinue) {
	if (currentSession != null) {
	    ScrollableArea.removeAutoScrollReason(this);
	    if (!optionContinue) {
		toolView.hide();
		toolView = null;
		currentSession = null;
		source.sessionEnded(this);
	    }
	    if (destinationView == null)
		Debug.print("debug.tool", "destinationView was null");
	    else {
		destinationView.unhilite();
		if (destination != null)
		    destination.toolReleased(this);
	    }
	}
    }
    
    public void resetSession(Image image, Tool toolType, Object data) {
	this.toolType = toolType;
	this.data = data;
	this.image = image;
	toolView.setImage(image);
	toolView.sizeTo(image.width(), image.height());
	_resetFlag = true;
    }
    
    public void resetSession(PlaywriteView dragView, Tool toolType) {
	this.toolType = toolType;
	data = dragView.getModelObject();
	image = dragView.getDragImage();
	toolView.setImageFrom(dragView);
	_resetFlag = true;
    }
    
    public void setHotSpot(int hotX, int hotY) {
	_hotSpot.x = hotX;
	_hotSpot.y = hotY;
	toolView.setHotSpot(hotX, hotY);
    }
    
    public void resetSession() {
	_resetFlag = true;
    }
    
    public boolean wasReset() {
	return _resetFlag;
    }
    
    void resetTargetView() {
	toolView.setTargetView(null);
    }
    
    public Image image() {
	return image;
    }
    
    public Object data() {
	return data;
    }
    
    public void setData(Object data) {
	this.data = data;
    }
    
    public Tool toolType() {
	return toolType;
    }
    
    public void setToolType(Tool toolType) {
	this.toolType = toolType;
    }
    
    public ToolSource source() {
	return source;
    }
    
    public ToolDestination destination() {
	return destination;
    }
    
    public Point destinationMousePoint() {
	return new Point(mouseX - _hotSpot.x, mouseY - _hotSpot.y);
    }
    
    void convertMousePoint(View sourceView, View destView) {
	Point pt = sourceView.convertToView(destView, mouseX, mouseY);
	mouseX = pt.x;
	mouseY = pt.y;
    }
    
    public int toolModifiers() {
	return modifiers;
    }
    
    public boolean isShiftKeyDown() {
	return (modifiers & 0x1) != 0;
    }
    
    public boolean isControlKeyDown() {
	return (modifiers & 0x2) != 0;
    }
    
    public boolean isMetaKeyDown() {
	return (modifiers & 0x4) != 0;
    }
    
    public boolean isAltKeyDown() {
	return (modifiers & 0x8) != 0;
    }
    
    public View destinationView() {
	return destinationView.view();
    }
    
    private boolean isInView(View view, MouseEvent event) {
	return view.containsPoint(event.x, event.y);
    }
    
    private void updateModifiers(MouseEvent currentEvent) {
	modifiers = 0;
	if (currentEvent != null) {
	    if (currentEvent.isShiftKeyDown())
		modifiers++;
	    if (currentEvent.isControlKeyDown())
		modifiers += 2;
	    if (currentEvent.isMetaKeyDown())
		modifiers += 4;
	    if (currentEvent.isAltKeyDown())
		modifiers += 8;
	}
    }
    
    void mouseEntered(View view, MouseEvent event) {
	if (toolView != null) {
	    mouseX = event.x;
	    mouseY = event.y;
	    destinationView = (ViewGlue) view;
	    if (view instanceof ViewGlue
		&& (toolType.getScrollerMapping() || isInView(view, event))) {
		destination
		    = ((ViewGlue) view).acceptsTool(this, mouseX, mouseY);
		if (destination != null) {
		    destinationView = (ViewGlue) view;
		    isAccepting = destination.toolEntered(this);
		    if (isAccepting) {
			destinationView.hilite();
			view.draw();
		    }
		}
	    } else
		destination = null;
	}
    }
    
    void mouseMoved(MouseEvent event) {
	if (toolView != null) {
	    if (destination != null) {
		mouseX = event.x;
		mouseY = event.y;
		isAccepting = destination.toolMoved(this);
	    } else if (destinationView != null)
		mouseEntered((View) destinationView, event);
	}
    }
    
    void mouseDown(MouseEvent event) {
	accepted = false;
	mouseX = event.x;
	mouseY = event.y;
	updateModifiers(event);
	if (destination != null && isAccepting) {
	    PlaywriteRoot.getMainRootView().setFocusedView(null);
	    accepted = destination.toolClicked(this);
	}
	if (accepted) {
	    source.toolWasAccepted(this);
	    Selection.hideModalView();
	} else {
	    source.toolWasRejected(this);
	    if (currentSession != null) {
		RootView rv = PlaywriteRoot.getMainRootView();
		Point clickPoint = rv.mousePoint();
		View clickView
		    = toolView.findViewAt(clickPoint.x, clickPoint.y);
		if (clickView instanceof ToolButton) {
		    cancelSession(false);
		    rv.setMouseView(clickView);
		    clickView.mouseDown(event);
		    return;
		}
	    }
	}
	if (!_resetFlag && !toolType.isDragEnabled() && !optionEvent(event)) {
	    cancelSession(false);
	    if (!accepted)
		warnToolUser();
	}
    }
    
    void mouseDragged(MouseEvent event) {
	if (toolView != null && !_resetFlag) {
	    mouseX = event.x;
	    mouseY = event.y;
	    updateModifiers(event);
	    if (destination != null && isAccepting && !optionEvent(event)) {
		destination.toolDragged(this);
		accepted = true;
	    }
	}
    }
    
    void mouseUp(MouseEvent event) {
	if (toolView != null) {
	    if (_resetFlag)
		_resetFlag = false;
	    else {
		cancelSession(optionEvent(event));
		if (!accepted)
		    warnToolUser();
	    }
	}
    }
    
    void mouseExited(MouseEvent event) {
	if (toolView != null) {
	    mouseX = event.x;
	    mouseY = event.y;
	    if (destination != null)
		destination.toolExited(this);
	    destination = null;
	    if (destinationView != null && isAccepting)
		destinationView.unhilite();
	    destinationView = null;
	}
    }
    
    private void warnToolUser() {
	String msg = toolType.getWarningMsg();
	if (msg == null)
	    PlaywriteSystem.beep();
	else
	    PlaywriteDialog.warning(msg);
    }
    
    private boolean optionEvent(MouseEvent event) {
	return event.isAltKeyDown() && toolType.isOptionClickEnabled();
    }
}
