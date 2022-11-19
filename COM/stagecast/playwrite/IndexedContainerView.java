/* IndexedContainerView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

class IndexedContainerView extends PlaywriteView
    implements ExtendedDragSource, DragDestination, ToolDestination,
	       Debug.Constants, Enableable
{
    private Vector subviews;
    private int leftMargin;
    private int rightMargin;
    private int topMargin;
    private int bottomMargin;
    private View widestSubview;
    private int currentSubviewWidth;
    private int spacing;
    private boolean resizingLocally;
    private boolean startingUp;
    private boolean shuttingDown;
    private int insertionBarY;
    private int oldInsertionBarY;
    private PlaywriteView insertPoint;
    private Color insertionBarColor = Color.black;
    private ToolHandler.ToolAdder toolAdder;
    private boolean _enabled;
    
    IndexedContainerView(IndexedContainer model, Class ioClass,
			 ToolHandler.ToolAdder theToolAdder,
			 boolean enableViews) {
	this.setModelObject(model);
	toolAdder = theToolAdder;
	if (ioClass == null)
	    Debug.print
		("debug.indexed.container",
		 "warning: ioClass not passed to IndexedContainerView constructor");
	else {
	    this.allowDragInto(ioClass, this);
	    this.allowDragOutOf(ioClass, this);
	}
	subviews = new Vector(model.getNumberOfElements() + 1);
	leftMargin = 10;
	rightMargin = 10;
	topMargin = 10;
	bottomMargin = 10;
	resizingLocally = false;
	spacing = 10;
	currentSubviewWidth = 0;
	widestSubview = null;
	startingUp = true;
	Enumeration contents = model.getElements();
	while (contents.hasMoreElements()) {
	    Indexed nextModel = (Indexed) contents.nextElement();
	    PlaywriteView newSubview = nextModel.createView();
	    if (newSubview != null) {
		if (newSubview instanceof Enableable)
		    ((Enableable) newSubview).setEnabled(enableViews);
		addSubview(newSubview);
		nextModel.setView(newSubview);
	    }
	}
	startingUp = false;
	if (subviews.size() != 0)
	    shrinkToFit((PlaywriteView) subviews.firstElement());
	this.allowTool(Tool.copyPlaceTool, this);
    }
    
    public IndexedContainer getModel() {
	return (IndexedContainer) this.getModelObject();
    }
    
    public void discard() {
	shuttingDown = true;
	super.discard();
	if (getModel() != null)
	    getModel().viewDiscarded(this);
	subviews = null;
	insertPoint = null;
	shuttingDown = false;
    }
    
    public void addSubview(View view) {
	if (!(view instanceof PlaywriteView)) {
	    if (view != null)
		Debug.print("debug.indexed.container", "non-playwriteView ",
			    view.getClass(),
			    " added to IndexedContainerView. ignored");
	} else {
	    PlaywriteView subview = (PlaywriteView) view;
	    if (subviews.containsIdentical(subview))
		subviews.removeElementIdentical(subview);
	    if (!(subview.getModelObject() instanceof Indexed))
		Debug.print("debug.indexed.container", "non-Indexed model ",
			    subview.getModelObject().getClass(),
			    " added to IndexedContainerView. ignored");
	    else {
		if (toolAdder != null)
		    toolAdder.addTools(subview);
		if (startingUp) {
		    subviews.addElement(view);
		    super.addSubview(view);
		} else {
		    Indexed modelObject = (Indexed) subview.getModelObject();
		    int insertionPoint = modelObject.getIndex();
		    if (subviews.size() == 0)
			subviews.addElement(subview);
		    else {
			Indexed placer
			    = getModel().getElementAt(insertionPoint + 1);
			if (placer == null)
			    subviews.addElement(subview);
			else {
			    for (int i = 0; i < subviews.size(); i++) {
				PlaywriteView test
				    = (PlaywriteView) subviews.elementAt(i);
				if (test.getModelObject() == placer) {
				    subviews.insertElementAt(subview, i);
				    break;
				}
			    }
			}
		    }
		    shrinkToFit(subview);
		    super.addSubview(subview);
		    if (insertionPoint == getModel().getNumberOfElements() - 1)
			this.scrollRectToVisible(subview.bounds);
		}
	    }
	}
    }
    
    public void removeSubview(View view) {
	if (shuttingDown || subviews == null)
	    super.removeSubview(view);
	else {
	    int lastIndex = subviews.indexOfIdentical(view);
	    if (lastIndex == -1)
		Debug.print
		    ("debug.indexed.container",
		     "IndexContainerView doesn't have the view you want to remove!");
	    else {
		beginResizingLocally();
		this.superview().superview().setDirty(true);
		super.removeSubview(view);
		subviews.removeElementIdentical(view);
		if (view == widestSubview) {
		    widestSubview = null;
		    currentSubviewWidth = 0;
		}
		int subsize = subviews.size();
		if (subsize == 0) {
		    this.setMinSize(0, 0);
		    this.sizeToMinSize();
		}
		if (lastIndex == subviews.size())
		    shrinkToFit((PlaywriteView) subviews.lastElement());
		else
		    shrinkToFit((PlaywriteView) subviews.elementAt(lastIndex));
		endResizingLocally();
	    }
	}
    }
    
    private void shrinkToFit(PlaywriteView firstView) {
	if (firstView == null) {
	    this.setMinSize(0, 0);
	    this.sizeToMinSize();
	} else {
	    Size firstViewMinSize = firstView.minSize();
	    int numSubviews = subviews.size();
	    if (currentSubviewWidth == 0
		|| (firstView == widestSubview
		    && firstViewMinSize.width < currentSubviewWidth)) {
		firstView = (PlaywriteView) subviews.firstElement();
		widestSubview = null;
		currentSubviewWidth = 0;
		for (int i = 0; i < numSubviews; i++) {
		    PlaywriteView nextView
			= (PlaywriteView) subviews.elementAt(i);
		    if (nextView.minSize().width > currentSubviewWidth) {
			currentSubviewWidth = nextView.minSize().width;
			widestSubview = nextView;
		    }
		}
	    } else if (firstViewMinSize.width > currentSubviewWidth) {
		currentSubviewWidth = firstViewMinSize.width;
		widestSubview = firstView;
		firstView = (PlaywriteView) subviews.firstElement();
	    }
	    beginResizingLocally();
	    int startIndex = subviews.indexOf(firstView);
	    int currentY = 0;
	    if (startIndex == 0)
		currentY = topMargin;
	    else
		currentY = ((PlaywriteView) subviews.elementAt(startIndex - 1))
			       .bounds.maxY() + spacing;
	    for (int i = startIndex; i < numSubviews; i++) {
		PlaywriteView currentView
		    = (PlaywriteView) subviews.elementAt(i);
		currentView.setBounds(leftMargin, currentY,
				      currentSubviewWidth,
				      currentView.minSize().height);
		currentY = currentView.bounds.maxY() + spacing;
	    }
	    currentY = currentY - spacing + bottomMargin;
	    this.setMinSize(currentSubviewWidth + leftMargin + rightMargin,
			    currentY);
	    endResizingLocally();
	    this.sizeToMinSize();
	    this.scrollRectToVisible(this.localBounds());
	}
    }
    
    private void beginResizingLocally() {
	resizingLocally = true;
	this.disableDrawing();
    }
    
    private void endResizingLocally() {
	resizingLocally = false;
	this.reenableDrawing();
    }
    
    public void subviewDidResize(View view) {
	if (!resizingLocally && !startingUp) {
	    if (subviews.contains(view)) {
		shrinkToFit((PlaywriteView) view);
		super.subviewDidResize(view);
		this.sizeToMinSize();
		this.scrollRectToVisible(this.localBounds());
	    } else
		super.subviewDidResize(view);
	}
    }
    
    public void layoutView(int dw, int dh) {
	if (!resizingLocally && !startingUp)
	    super.layoutView(dw, dh);
    }
    
    public boolean isEnabled() {
	return _enabled;
    }
    
    public void setEnabled(boolean enabled) {
	_enabled = enabled;
	if (subviews != null) {
	    this.disableDrawing();
	    int i = subviews.count();
	    while (i-- > 0) {
		Object subview = subviews.elementAt(i);
		if (subview instanceof Enableable)
		    ((Enableable) subview).setEnabled(enabled);
	    }
	    this.reenableDrawing();
	    this.setDirty(true);
	}
    }
    
    public boolean prepareToDrag(Object data) {
	Object whatever = ((PlaywriteView) data).getModelObject();
	if (whatever instanceof Indexed)
	    return getModel().permitDrag((Indexed) whatever);
	return true;
    }
    
    public void dragWasAccepted(DragSession session) {
	collectDragSessionView(session);
	resetHiliting();
    }
    
    public boolean dragWasRejected(DragSession session) {
	Indexed indexed = cleanSession(session);
	collectDragSessionView(session);
	if (indexed != null) {
	    Debug.print("debug.indexed.container", "adding it back in!");
	    getModel().insertElementAt(indexed, indexed.getIndex());
	}
	resetHiliting();
	return false;
    }
    
    public View sourceView(DragSession session) {
	return this;
    }
    
    private void collectDragSessionView(DragSession session) {
	if (session.data() instanceof PlaywriteView) {
	    PlaywriteView draggedView = (PlaywriteView) session.data();
	    if (draggedView.isInViewHierarchy())
		Debug.print("debug.indexed.container",
			    "collectDragSession: view is in Hierarchy!");
	    else if (draggedView.getModelObject() != null) {
		draggedView.setModelObject(null);
		draggedView.discard();
	    }
	}
    }
    
    private Indexed cleanSession(DragSession session) {
	Indexed indexed = null;
	if (session.data() instanceof PlaywriteView) {
	    PlaywriteView draggedView = (PlaywriteView) session.data();
	    if (draggedView.isInViewHierarchy())
		draggedView.removeFromSuperview();
	    if (session.destination() != null && session.destination() == this)
		hiliteForPoint(session);
	    Object model = draggedView.getModelObject();
	    if (model instanceof Indexed) {
		indexed = (Indexed) model;
		indexed.removeFromContainer();
	    }
	}
	return indexed;
    }
    
    public boolean dragEntered(DragSession session) {
	return cleanSession(session) != null;
    }
    
    public void dragExited(DragSession session) {
	cleanSession(session);
	resetHiliting();
    }
    
    public boolean dragMoved(DragSession session) {
	return cleanSession(session) != null;
    }
    
    boolean insertObjectAtHilite(Object droppedObject) {
	if (droppedObject == null || !(droppedObject instanceof Indexed)) {
	    Debug.print("debug.indexed.container",
			"insertObjectAtHilite: this should never happen");
	    return false;
	}
	Indexed indexed = (Indexed) droppedObject;
	if (indexed.getIndexedContainer() != null) {
	    Debug.print
		("debug.indexed.container",
		 "bad call to insertObjectAtHilite: object has container!");
	    Debug.stackTrace();
	    return false;
	}
	int newIndex = -1;
	if (insertPoint != null)
	    newIndex = ((Indexed) insertPoint.getModelObject()).getIndex();
	getModel().insertElementAt(indexed, newIndex);
	getModel().userModified(indexed);
	insertionBarY = 0;
	insertPoint = null;
	return true;
    }
    
    public boolean dragDropped(DragSession session) {
	Indexed indexed = cleanSession(session);
	if (indexed != null)
	    return insertObjectAtHilite(indexed);
	return false;
    }
    
    private boolean acceptsToolSession(ToolSession session) {
	if (session.data() == null)
	    return false;
	if (session.toolType() == Tool.copyPlaceTool
	    && this.allowsDragInto(session.data().getClass()) != null) {
	    hiliteForPoint(session);
	    return true;
	}
	return false;
    }
    
    Object makeCopy(Object original) {
	if (original instanceof Copyable)
	    return ((Copyable) original).copy();
	return null;
    }
    
    public boolean toolEntered(ToolSession session) {
	return acceptsToolSession(session);
    }
    
    public boolean toolMoved(ToolSession session) {
	return acceptsToolSession(session);
    }
    
    public void toolExited(ToolSession session) {
	resetHiliting();
    }
    
    public boolean toolClicked(ToolSession session) {
	if (acceptsToolSession(session)) {
	    if (session.toolType() == Tool.copyPlaceTool) {
		Object modelObject = session.data();
		if (modelObject instanceof Copyable) {
		    boolean success
			= insertObjectAtHilite(makeCopy(modelObject));
		    resetHiliting();
		    return success;
		}
		return false;
	    }
	    return false;
	}
	return false;
    }
    
    public void toolDragged(ToolSession session) {
	/* empty */
    }
    
    public void toolReleased(ToolSession session) {
	/* empty */
    }
    
    void resetHiliting() {
	insertionBarY = 0;
	insertPoint = null;
	this.unhilite();
    }
    
    public void setInsertionBarColor(Color color) {
	insertionBarColor = color;
    }
    
    private Rect getInsertionBarDirtyRect(int y) {
	return new Rect(0, y - 3, this.width(), 7);
    }
    
    public void drawViewBackground(Graphics g) {
	super.drawViewBackground(g);
	if (oldInsertionBarY != 0 && oldInsertionBarY != insertionBarY) {
	    g.setColor(this.backgroundColor());
	    g.fillRect(0, insertionBarY - 3, this.width(), 5);
	    oldInsertionBarY = insertionBarY;
	}
	if (insertionBarY != 0) {
	    g.setColor(insertionBarColor);
	    g.fillRect(15, insertionBarY - 1, this.width(), 3);
	    g.drawLine(0, insertionBarY, 9, insertionBarY);
	    g.drawLine(5, insertionBarY - 3, 8, insertionBarY);
	    g.drawLine(5, insertionBarY + 3, 8, insertionBarY);
	}
    }
    
    boolean hiliteForPoint(Point point) {
	int oldibarY = insertionBarY;
	boolean returning = false;
	if (point == null) {
	    Debug.print("debug.indexed.container",
			" null point passed to hiliteForPoint");
	    Debug.stackTrace("debug.indexed.container");
	    resetHiliting();
	    returning = false;
	} else if (subviews.size() == 0) {
	    insertionBarY = 0;
	    this.hilite();
	    returning = true;
	} else {
	    int y = point.y;
	    int subviewCount = subviews.size();
	    PlaywriteView subview = null;
	    PlaywriteView subview2 = null;
	    for (int i = 0; i < subviewCount; i++) {
		subview = (PlaywriteView) subviews.elementAt(i);
		if (subview.bounds.contains(point)) {
		    if (point.y < subview.bounds.midY()) {
			insertPoint = subview;
			if (i == 0)
			    insertionBarY = subview.y() / 2;
			else {
			    subview2
				= (PlaywriteView) subviews.elementAt(i - 1);
			    insertionBarY = (subview2.bounds.maxY()
					     + (subview.y()
						- subview2.bounds.maxY()) / 2);
			}
		    } else if (i == subviewCount - 1) {
			insertionBarY = (subview.bounds.maxY()
					 + (subview.superview().height()
					    - subview.bounds.maxY()) / 2);
			insertPoint = null;
		    } else {
			subview2 = (PlaywriteView) subviews.elementAt(i + 1);
			insertPoint = subview2;
			insertionBarY
			    = (subview.bounds.maxY()
			       + (subview2.y() - subview.bounds.maxY()) / 2);
		    }
		    this.hilite();
		    returning = true;
		}
	    }
	}
	if (oldibarY != insertionBarY) {
	    this.draw(getInsertionBarDirtyRect(oldibarY));
	    this.draw(getInsertionBarDirtyRect(insertionBarY));
	}
	return returning;
    }
    
    final boolean hiliteForPoint(DragSession dragSession) {
	return hiliteForPoint(dragSession.destinationMousePoint());
    }
    
    final boolean hiliteForPoint(ToolSession toolSession) {
	return hiliteForPoint(toolSession.destinationMousePoint());
    }
}
