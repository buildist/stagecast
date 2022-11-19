/* DragSession - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class DragSession
{
    String dataType;
    Object data;
    Image image;
    int initialX;
    int initialY;
    int mouseDownX;
    int mouseDownY;
    int mouseX;
    int mouseY;
    DragSource source;
    DragDestination destination;
    DragView dragView;
    int modifiers;
    RootView rootView;
    View sourceView;
    View destinationView;
    boolean isAccepting;
    public static final int SHIFT_MASK = 1;
    public static final int CONTROL_MASK = 2;
    public static final int META_MASK = 4;
    public static final int ALT_MASK = 8;
    
    public DragSession(DragSource dragsource, Image image, int i, int i_0_,
		       int i_1_, int i_2_, String string, Object object,
		       boolean bool) {
	Point point = new Point();
	Point point_3_ = new Point();
	sourceView = dragsource.sourceView(this);
	rootView = sourceView.rootView();
	sourceView.convertToView(null, i, i_0_, point);
	sourceView.convertToView(null, i_1_, i_2_, point_3_);
	if (rootView.windowClipView() != null) {
	    rootView.convertPointToView(rootView.windowClipView(), point,
					point);
	    rootView.convertPointToView(rootView.windowClipView(), point_3_,
					point_3_);
	}
	source = dragsource;
	this.image = image;
	initialX = point.x;
	initialY = point.y;
	mouseDownX = point_3_.x;
	mouseDownY = point_3_.y;
	dataType = string;
	data = object;
	if (bool)
	    dragView = new DragView(this);
    }
    
    public DragSession(DragSource dragsource, Image image, int i, int i_4_,
		       int i_5_, int i_6_, String string, Object object) {
	this(dragsource, image, i, i_4_, i_5_, i_6_, string, object, true);
    }
    
    public Object data() {
	return data;
    }
    
    public void setData(Object object) {
	data = object;
    }
    
    public String dataType() {
	return dataType;
    }
    
    public void setDataType(String string) {
	dataType = string;
    }
    
    public DragSource source() {
	return source;
    }
    
    public DragDestination destination() {
	return destination;
    }
    
    public int dragModifiers() {
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
    
    void updateModifiers(MouseEvent mouseevent) {
	modifiers = 0;
	if (mouseevent != null) {
	    if (mouseevent.isShiftKeyDown())
		modifiers++;
	    if (mouseevent.isControlKeyDown())
		modifiers += 2;
	    if (mouseevent.isMetaKeyDown())
		modifiers += 4;
	    if (mouseevent.isAltKeyDown())
		modifiers += 8;
	}
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	DragDestination dragdestination = null;
	Point point = new Point(mouseevent.x, mouseevent.y);
	if (dragView != null)
	    dragView.convertPointToView(null, point, point);
	int i = point.x;
	int i_7_ = point.y;
	updateModifiers(mouseevent);
	View view = rootView.viewForMouse(i, i_7_);
	if (rootView.viewExcludedFromModalSession(view))
	    view = null;
	if (view != null) {
	    Point point_8_ = Point.newPoint();
	    rootView.convertToView(view, i, i_7_, point_8_);
	    for (dragdestination
		     = view.acceptsDrag(this, point_8_.x, point_8_.y);
		 dragdestination == null && view._superview != null;
		 dragdestination
		     = view.acceptsDrag(this, point_8_.x, point_8_.y)) {
		point_8_.x += view.bounds.x;
		point_8_.y += view.bounds.y;
		view = view._superview;
	    }
	    Point.returnPoint(point_8_);
	}
	if (destination == null && dragdestination != null) {
	    destination = dragdestination;
	    destinationView = view;
	    isAccepting = destination.dragEntered(this);
	} else if (destination != null && dragdestination == null) {
	    destination.dragExited(this);
	    destination = null;
	    destinationView = null;
	} else if (destination != dragdestination) {
	    destination.dragExited(this);
	    destination = dragdestination;
	    destinationView = view;
	    destination.dragEntered(this);
	} else if (destination != null)
	    isAccepting = destination.dragMoved(this);
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	boolean bool = false;
	boolean bool_9_ = false;
	try {
	    updateModifiers(mouseevent);
	    if (destination != null && isAccepting)
		bool = destination.dragDropped(this);
	    if (bool)
		source.dragWasAccepted(this);
	    else
		bool_9_ = source.dragWasRejected(this);
	} finally {
	    isAccepting = false;
	    if (dragView != null) {
		if (bool_9_)
		    dragView.startAnimatingRejectedDrag();
		else
		    dragView.stopDragging();
	    }
	}
    }
    
    public View destinationView() {
	return destinationView;
    }
    
    public Rect destinationBounds() {
	if (destinationView == null)
	    return null;
	Rect rect = absoluteBounds();
	rootView.convertRectToView(destinationView, rect, rect);
	return rect;
    }
    
    public Rect absoluteBounds() {
	Rect rect = new Rect(0, 0, image.width(), image.height());
	if (dragView != null)
	    dragView.convertRectToView(null, rect, rect);
	return rect;
    }
    
    public Point absoluteMousePoint() {
	if (dragView != null) {
	    Point point = new Point(dragView._lastX, dragView._lastY);
	    dragView.superview().convertPointToView(null, point, point);
	    return point;
	}
	return new Point(0, 0);
    }
    
    public Point destinationMousePoint() {
	if (destinationView == null)
	    return null;
	if (dragView != null) {
	    Point point = new Point(dragView._lastX, dragView._lastY);
	    dragView.superview().convertPointToView(destinationView, point,
						    point);
	    return point;
	}
	return new Point(0, 0);
    }
    
    public boolean destinationIsAccepting() {
	return isAccepting;
    }
}
