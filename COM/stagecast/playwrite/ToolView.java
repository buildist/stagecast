/* ToolView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.View;

public class ToolView extends InternalWindow
{
    private ToolSession _session;
    private Image _image;
    private Rect _oldRect;
    private RootView _rootView;
    private View _targetView;
    private boolean _searching;
    private boolean _dragging;
    
    public ToolView(ToolSession session, Image image) {
	super(0, 0, 0, image.width(), image.height());
	this.setTransparent(true);
	this.setResizable(false);
	this.setLayer(400);
	this.setCanBecomeMain(false);
	setHotSpot(bounds.width / 2, bounds.height / 2);
	_searching = false;
	_oldRect = this.bounds();
	_session = session;
	_image = image;
	_targetView = null;
	_dragging = false;
	this.show();
	super.mouseDown(new MouseEvent(0L, -1, bounds.width / 2,
				       bounds.height / 2, 0));
	this.rootView().setMouseView(this);
    }
    
    final Image getImage() {
	return _image;
    }
    
    final void setImage(Image i) {
	_image = i;
	this.sizeTo(_image.width(), _image.height());
	super.mouseDown(new MouseEvent(0L, -1, bounds.width / 2,
				       bounds.height / 2, 0));
    }
    
    final void setImageFrom(PlaywriteView pwView) {
	_image = pwView.getDragImage();
	this.sizeTo(_image.width(), _image.height());
	Point dragPoint = pwView.getDragPoint();
	super.mouseDown(new MouseEvent(0L, -1, dragPoint.x, dragPoint.y, 0));
    }
    
    final void setTargetView(View view) {
	_targetView = view;
    }
    
    final ToolSession getToolSession() {
	return _session;
    }
    
    public void drawView(Graphics g) {
	_image.drawAt(g, 0, 0);
    }
    
    public void setHotSpot(int hotX, int hotY) {
	Point cursorLoc = PlaywriteRoot.getMainRootView().mousePoint();
	this.moveTo(cursorLoc.x - hotX, cursorLoc.y - hotY);
    }
    
    public void mouseMoved(MouseEvent event) {
	Point loc = this.rootView().mousePoint();
	event.setType(-2);
	super.mouseDragged(event);
	checkMouseTarget(event, loc.x, loc.y);
    }
    
    public void mouseExited(MouseEvent event) {
	Point loc = this.rootView().mousePoint();
	event.setType(-2);
	super.mouseDragged(event);
	checkMouseTarget(event, loc.x, loc.y);
    }
    
    public boolean mouseDown(MouseEvent event) {
	Point loc = this.rootView().mousePoint();
	checkMouseTarget(event, loc.x, loc.y);
	_session.mouseDown(event);
	return true;
    }
    
    public void mouseDragged(MouseEvent event) {
	_dragging = true;
	Point loc = this.rootView().mousePoint();
	super.mouseDragged(event);
	checkMouseTarget(event, loc.x, loc.y);
	_session.mouseDragged(event);
    }
    
    public void mouseUp(MouseEvent event) {
	_dragging = false;
	Point loc = this.rootView().mousePoint();
	event.setType(-2);
	super.mouseDragged(event);
	_session.mouseUp(event);
    }
    
    public View viewForMouse(int x, int y) {
	if (_searching)
	    return null;
	return this;
    }
    
    public synchronized View findViewAt(int x, int y) {
	_searching = true;
	View target = this.rootView().viewForMouse(x, y);
	_searching = false;
	return target;
    }
    
    private synchronized View findTargetView(int x, int y) {
	for (View target = findViewAt(x, y); target != null;
	     target = target.superview()) {
	    if (target instanceof ViewGlue
		&& ((ViewGlue) target).acceptsTool(_session, x, y) != null)
		return target;
	}
	return null;
    }
    
    private void resetEventForView(MouseEvent event, int type, int x, int y) {
	Point loc = new Point(x, y);
	Point viewPoint = this.rootView().convertPointToView(_targetView, loc);
	event.setType(type);
	event.x = viewPoint.x;
	event.y = viewPoint.y;
    }
    
    private void checkMouseTarget(MouseEvent event, int x, int y) {
	View newTarget = findTargetView(x, y);
	if (newTarget != _targetView) {
	    if (_targetView != null && _targetView.isInViewHierarchy()) {
		resetEventForView(event, -6, x, y);
		_session.mouseExited(event);
	    }
	    _targetView = newTarget;
	    if (_targetView != null) {
		resetEventForView(event, -4, x, y);
		_session.mouseEntered(_targetView, event);
	    }
	} else if (_dragging) {
	    resetEventForView(event, -2, x, y);
	    _session.mouseDragged(event);
	} else {
	    resetEventForView(event, -5, x, y);
	    _session.mouseMoved(event);
	}
    }
}
