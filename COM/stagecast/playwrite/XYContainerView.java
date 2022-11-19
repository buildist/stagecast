/* XYContainerView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.ScrollView;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

class XYContainerView extends RubberBandView
    implements XYViewer, ExtendedDragSource, DragDestination, Hilitable,
	       ToolDestination, Target, Worldly
{
    static final int X_MARGIN = 10;
    static final int Y_MARGIN = 10;
    static final String EDIT = "EDIT";
    static final String POPUP = "POPUP";
    private boolean _toolsAreDraggable = true;
    
    XYContainerView(XYContainer container, int width, int height) {
	super(0, 0, width, height);
	this.setModelObject(container);
	container.addView(this);
    }
    
    void init() {
	setProperties();
	populateView();
    }
    
    private XYContainer getContainer() {
	return (XYContainer) this.getModelObject();
    }
    
    public World getWorld() {
	return getContainer().getWorld();
    }
    
    void setToolsAreDraggable(boolean flag) {
	_toolsAreDraggable = flag;
    }
    
    private void populateView() {
	XYContainer container = getContainer();
	Enumeration items = container.getContents();
	while (items.hasMoreElements()) {
	    Contained item = (Contained) items.nextElement();
	    Point loc = container.getLocation(item);
	    if (loc.x != -1 && loc.y != -1) {
		PlaywriteView view = makeModelView(item);
		if (view != null) {
		    setViewProperties(view);
		    setPosition(view, loc);
		    this.addSubview(view);
		}
	    }
	}
	Enumeration items_0_ = container.getContents();
	while (items_0_.hasMoreElements()) {
	    Contained item = (Contained) items_0_.nextElement();
	    Point loc = container.getLocation(item);
	    if (loc.x == -1 || loc.y == -1) {
		PlaywriteView view = makeModelView(item);
		if (view != null) {
		    setViewProperties(view);
		    setPosition(view, loc);
		    this.addSubview(view);
		}
	    }
	}
	this.setDirty(true);
    }
    
    public void discard() {
	((XYContainer) this.getModelObject()).removeView(this);
	super.discard();
    }
    
    public void hilite() {
	View superview = this.superview();
	if (superview instanceof ScrollView)
	    ((PlaywriteView) superview.superview()).hilite();
	else
	    super.hilite();
    }
    
    public void unhilite() {
	View superview = this.superview();
	if (superview instanceof ScrollView)
	    ((PlaywriteView) superview.superview()).unhilite();
	else
	    super.unhilite();
    }
    
    public void setProperties() {
	Class dragtype = getContainer().getContentType();
	this.allowDragInto(dragtype, this);
	this.allowDragOutOf(dragtype, this);
	if (PlaywriteRoot.isAuthoring())
	    this.allowTool(Tool.copyPlaceTool, this);
    }
    
    protected PlaywriteView makeModelView(Contained model) {
	if (model instanceof Visible && !((Visible) model).isVisible())
	    return null;
	PlaywriteView modelView;
	if (model instanceof FirstClassValue)
	    modelView = ((FirstClassValue) model).createIconView();
	else if (model instanceof IconModel)
	    modelView = new Icon((IconModel) model);
	else
	    modelView = ((Viewable) model).createView();
	modelView.setModelObject(model);
	if (modelView instanceof Icon && getContainer().isPrimaryContainer()) {
	    Icon icon = (Icon) modelView;
	    icon.setEditable(true);
	    icon.setSelectsModel(true);
	}
	if (modelView instanceof Icon && getContainer() instanceof Jar)
	    ((Icon) modelView).setSelectsModel(true);
	return modelView;
    }
    
    void setViewProperties(PlaywriteView view) {
	view.setCursor(12);
	if (PlaywriteRoot.isAuthoring()) {
	    Object model = view.getModelObject();
	    if (model instanceof Copyable)
		view.allowTool(Tool.copyLoadTool, this);
	    if (model instanceof Editable)
		view.setEventDelegate(-1, 0, 2, "EDIT", this);
	    if (model instanceof CocoaCharacter
		&& !(model instanceof SpecialPrototype))
		view.setEventDelegate(-1, 4, 1, "POPUP", this);
	    view.allowTool(Tool.deleteTool, this);
	}
    }
    
    private PlaywriteView viewForModel(Contained model) {
	ASSERT.isInEventThread();
	Vector subviews = this.subviews();
	for (int i = 0; i < subviews.size(); i++) {
	    PlaywriteView view = (PlaywriteView) subviews.elementAt(i);
	    if (view.getModelObject() == model)
		return view;
	}
	return null;
    }
    
    private void setPosition(PlaywriteView view, Point loc) {
	ASSERT.isInEventThread();
	if (loc.x == -1 || loc.y == -1) {
	    Rect visibleRect = new Rect();
	    this.computeVisibleRect(visibleRect);
	    Rect newBounds = view.bounds();
	    Vector views = this.subviews();
	    newBounds.moveTo(0, 0);
	    boolean overlap = true;
	    do {
		if (newBounds.maxX() > visibleRect.width
		    && visibleRect.width > newBounds.width) {
		    newBounds.y += newBounds.height + 10;
		    newBounds.x = 0;
		} else {
		    overlap = false;
		    for (int i = 0; i < views.size(); i++) {
			View v = (View) views.elementAt(i);
			if (v.bounds.intersects(newBounds)) {
			    if (visibleRect.width > newBounds.width)
				newBounds.x = v.bounds.maxX() + 10;
			    else
				newBounds.y += newBounds.height + 10;
			    overlap = true;
			    break;
			}
		    }
		}
	    } while (overlap);
	    view.moveTo(newBounds.x, newBounds.y);
	    ((XYContainer) this.getModelObject()).moveTo
		((Contained) view.getModelObject(), newBounds.x, newBounds.y);
	} else
	    view.moveTo(loc.x, loc.y);
    }
    
    public void itemAdded(Contained item, Point location) {
	PlaywriteView view = makeModelView(item);
	if (view != null) {
	    setViewProperties(view);
	    setPosition(view, location);
	    this.addSubview(view);
	    this.addDirtyRect(view.bounds());
	}
    }
    
    public void itemRemoved(Contained item) {
	PlaywriteView view = viewForModel(item);
	if (view != null) {
	    view.removeFromSuperview();
	    this.addDirtyRect(view.bounds());
	    view.discard();
	}
    }
    
    public void itemUpdated(Contained item) {
	PlaywriteView view = viewForModel(item);
	if (view == null)
	    itemAdded(item, getContainer().getLocation(item));
	else
	    this.addDirtyRect(view.bounds());
    }
    
    public void itemMoved(Contained item, Point location) {
	PlaywriteView view = viewForModel(item);
	if (view != null) {
	    this.addDirtyRect(view.bounds());
	    view.moveTo(location.x, location.y);
	    this.addDirtyRect(view.bounds());
	}
    }
    
    public boolean prepareToDrag(Object data) {
	return !getWorld().isRunning() || getWorld().isSuspendedForDebug();
    }
    
    public void dragWasAccepted(DragSession session) {
	/* empty */
    }
    
    public boolean dragWasRejected(DragSession session) {
	return true;
    }
    
    public View sourceView(DragSession session) {
	return this;
    }
    
    public DragDestination acceptsDrag(DragSession ds, int x, int y) {
	if (getWorld().getState() == World.RUNNING)
	    return null;
	return super.acceptsDrag(ds, x, y);
    }
    
    public boolean dragDropped(DragSession session) {
	Object obj = this.modelObjectBeingDragged(session);
	Rect imageRect = session.destinationBounds();
	Point loc = new Point(imageRect.x, imageRect.y);
	XYContainer container = getContainer();
	Selection.unselectAll();
	unhilite();
	if (session.source() == this) {
	    this.addSubview(this.viewBeingDragged(session));
	    container.moveTo((Contained) obj, loc.x, loc.y);
	} else {
	    if (!getWorld().isOkToCopyWithDialog(obj))
		return false;
	    if (obj instanceof ReferencedObject
		&& getWorld().hasPreviousCopy((ReferencedObject) obj))
		return false;
	    if (copyToContainer(obj, loc.x, loc.y) == false)
		return false;
	}
	if (obj instanceof Selectable)
	    Selection.addToSelection((Selectable) obj, this);
	getWorld().setModified(true);
	return true;
    }
    
    public boolean dragEntered(DragSession session) {
	if (session.source() == this) {
	    PlaywriteView view = this.viewBeingDragged(session);
	    view.removeFromSuperview();
	    this.addDirtyRect(view.bounds());
	}
	hilite();
	return true;
    }
    
    public void dragExited(DragSession session) {
	if (session.source() == this) {
	    PlaywriteView view = this.viewBeingDragged(session);
	    this.addSubview(view);
	    this.addDirtyRect(view.bounds());
	}
	unhilite();
    }
    
    public boolean dragMoved(DragSession session) {
	return true;
    }
    
    public boolean toolClicked(ToolSession session) {
	Point dest = session.destinationMousePoint();
	PlaywriteView targetView = (PlaywriteView) session.destinationView();
	Tool toolType = session.toolType();
	XYContainer container = getContainer();
	boolean result = false;
	boolean markChanged = false;
	if (toolType == Tool.copyPlaceTool) {
	    if (getWorld().isOkToCopyWithDialog(session.data())) {
		result = copyToContainer(session.data(), dest.x, dest.y);
		markChanged = result;
	    }
	} else if (toolType == Tool.copyLoadTool) {
	    Contained target = (Contained) targetView.getModelObject();
	    session.resetSession(targetView.getDragImage(), Tool.copyPlaceTool,
				 target);
	    result = true;
	} else if (toolType == Tool.deleteTool) {
	    Object obj = targetView.getModelObject();
	    if (obj instanceof Selectable) {
		Selection.select((Selectable) obj, this);
		Selection.deleteSelection();
		result = true;
		markChanged = true;
	    }
	} else
	    return false;
	if (markChanged)
	    getWorld().setModified(true);
	if (!_toolsAreDraggable && !session.wasReset())
	    ToolSession.cancelCurrentSession();
	return result;
    }
    
    public void toolDragged(ToolSession session) {
	PlaywriteView targetView = (PlaywriteView) session.destinationView();
	XYContainer container = getContainer();
	Tool toolType = session.toolType();
	if (toolType == Tool.deleteTool) {
	    Object obj = targetView.getModelObject();
	    if (obj instanceof Selectable) {
		Selection.select((Selectable) obj, this);
		Selection.deleteSelection();
	    }
	} else
	    return;
	getWorld().setModified(true);
	if (!_toolsAreDraggable)
	    ToolSession.cancelCurrentSession();
    }
    
    public void performCommand(String command, Object data) {
	if ("EDIT".equals(command)) {
	    PlaywriteRoot.markBusy();
	    ((Editable) ((PlaywriteView) data).getModelObject()).edit();
	    PlaywriteRoot.clearBusy();
	} else if ("POPUP".equals(command)) {
	    CocoaCharacter ch
		= (CocoaCharacter) ((PlaywriteView) data).getModelObject();
	    Point pt = ((PlaywriteView) data).rootView().mousePoint();
	    EnumeratedVariableEditor.displayPopupFor((ch.getPrototype()
						      .appearanceVar),
						     ch, this, pt);
	} else
	    super.performCommand(command, data);
    }
    
    boolean copyToContainer(Object obj, int x, int y) {
	XYContainer container = getContainer();
	boolean result = false;
	if (obj instanceof Contained && obj instanceof Copyable
	    && container.allowContentType(obj.getClass())) {
	    Contained copy = container.copyForAdd((Contained) obj);
	    if (copy instanceof ReferencedObject
		&& (((ReferencedObject) obj).getWorld()
		    != ((ReferencedObject) copy).getWorld()))
		result = true;
	    if (container.allowAdd(copy) || copy.getContainer() == container) {
		container.add(copy, x, y);
		result = true;
	    }
	}
	return result;
    }
}
