/* JarView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.ScrollView;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFieldOwner;
import COM.stagecast.ifc.netscape.application.View;

class JarView extends PlaywriteView
    implements Debug.Constants, DragDestination, ExtendedDragSource,
	       TextFieldOwner
{
    static final int WIDTH = 120;
    static final int HEIGHT = 140;
    PlaywriteTextField titleView;
    PlaywriteView contentView;
    
    JarView(Jar jar) {
	super(0, 0, 120, 140);
	this.setModelObject(jar);
	createTitleView();
	createContentView();
	this.layoutView(0, 0);
	this.setDirty(true);
    }
    
    public Jar getJar() {
	return (Jar) this.getModelObject();
    }
    
    void createTitleView() {
	String title = getJar().getName();
	Size titleSize = Util.stringSize(Util.varTitleFont, title);
	titleView = new PlaywriteTextField() {
	    public View viewForMouse(int x, int y) {
		String s = this.stringValue();
		int minVal = this.xPositionOfCharacter(0);
		int maxVal
		    = this.xPositionOfCharacter(this.stringValue().length()
						- 1);
		if (maxVal - minVal < 20) {
		    minVal = this.width() / 2 - 10;
		    maxVal = this.width() / 2 + 10;
		}
		if (x < minVal || x > maxVal)
		    return null;
		return super.viewForMouse(x, y);
	    }
	};
	titleView.setBorder(null);
	titleView.setTransparent(false);
	titleView.setBackgroundColor(Color.yellow);
	titleView.setFont(Util.varTitleFont);
	titleView.setJustification(1);
	titleView.setWrapsContents(false);
	titleView.setUserEditable(true);
	titleView.setStringValue(title);
	titleView.setOwner(this);
	titleView.sizeTo(this.width(), titleSize.height);
	titleView.setVertResizeInstruction(4);
	titleView.setHorizResizeInstruction(2);
	this.addSubview(titleView);
    }
    
    void createContentView() {
	contentView = new XYContainerView(getJar(), this.width(),
					  this.height()) {
	    public void setProperties() {
		super.setProperties();
		this.setToolsAreDraggable(false);
		this.allowDragOutOf(Object.class, JarView.this);
		this.allowTool(Tool.copyPlaceTool, JarView.this);
	    }
	    
	    public DragDestination allowsDragInto(Class dropModelClass) {
		return JarView.this;
	    }
	    
	    public ExtendedDragSource allowsDragOutOf(Class dragModelClass) {
		return JarView.this;
	    }
	};
	((XYContainerView) contentView).init();
	contentView.setBackgroundColor(Color.white);
	ScrollableArea scroller
	    = new ScrollableArea(this.width(),
				 this.height() - titleView.height(),
				 contentView, true, true);
	scroller.setAllowSmallContentView(false);
	scroller.setBackgroundColor(Color.white);
	scroller.getScrollView().setTransparent(true);
	scroller.setHorizontalScrollAmount(5);
	scroller.setVerticalScrollAmount(5);
	scroller.moveTo(0, titleView.height());
	scroller.setVertResizeInstruction(16);
	scroller.setHorizResizeInstruction(2);
	this.addSubview(scroller);
    }
    
    public void discard() {
	super.discard();
	getJar().removeView(this);
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
    
    public boolean mouseDown(MouseEvent event) {
	if (super.mouseDown(event)) {
	    this.selectModel(event);
	    return true;
	}
	return false;
    }
    
    public void textEditingDidBegin(TextField tf) {
	/* empty */
    }
    
    public void textWasModified(TextField tf) {
	tf.sizeTo(120, tf.height());
	tf.setDirty(true);
    }
    
    public boolean textEditingWillEnd(TextField tf, int endCondition,
				      boolean changed) {
	return true;
    }
    
    public void textEditingDidEnd(TextField tf, int endCondition,
				  boolean changed) {
	getJar().setName(tf.stringValue());
	getJar().getWorld().setModified(true);
	this.sizeToMinSize();
	this.setDragImageDirty();
    }
    
    public boolean prepareToDrag(Object data) {
	World world = getJar().getWorld();
	if (world.isRunning() && !world.isSuspendedForDebug())
	    return false;
	return ((PlaywriteView) data).getModelObject() instanceof Bindable;
    }
    
    public void dragWasAccepted(DragSession session) {
	/* empty */
    }
    
    public boolean dragWasRejected(DragSession session) {
	return true;
    }
    
    public View sourceView(DragSession session) {
	return contentView;
    }
    
    public boolean dragDropped(DragSession ds) {
	Jar jar = getJar();
	Contained target = (Contained) this.modelObjectBeingDragged(ds);
	Point dragPoint = ((PlaywriteView) ds.data()).getDragPoint();
	Point loc = ds.destinationMousePoint();
	boolean success = false;
	Selection.unselectAll();
	unhilite();
	loc.moveBy(-dragPoint.x, -dragPoint.y);
	if (ds.source() == this) {
	    contentView.addSubview(this.viewBeingDragged(ds));
	    jar.moveTo(target, loc.x, loc.y);
	    jar.getWorld().setModified(true);
	    success = true;
	} else
	    success = addToJar(target, loc);
	if (success && target instanceof Selectable)
	    Selection.addToSelection((Selectable) target, this);
	return success;
    }
    
    public boolean dragEntered(DragSession ds) {
	if (ds.destinationView() != contentView)
	    return false;
	Object target = this.modelObjectBeingDragged(ds);
	if (target instanceof Bindable) {
	    if (target instanceof Jar)
		return false;
	    if (!(target instanceof Contained))
		return false;
	} else if (!(target instanceof CocoaCharacter))
	    return false;
	if (ds.source() == this) {
	    PlaywriteView view = this.viewBeingDragged(ds);
	    view.removeFromSuperview();
	    this.addDirtyRect(view.bounds());
	}
	hilite();
	return true;
    }
    
    public void dragExited(DragSession ds) {
	if (ds.source() == this) {
	    PlaywriteView view = this.viewBeingDragged(ds);
	    contentView.addSubview(view);
	    this.addDirtyRect(view.bounds());
	}
	unhilite();
    }
    
    public boolean dragMoved(DragSession ds) {
	return this.modelObjectBeingDragged(ds) instanceof Contained;
    }
    
    public boolean toolClicked(ToolSession session) {
	Point loc = session.destinationMousePoint();
	Image image = session.image();
	loc.moveBy(-image.width() / 2, -image.height() / 2);
	boolean result = addToJar(session.data(), loc);
	if (!session.wasReset())
	    ToolSession.cancelCurrentSession();
	return result;
    }
    
    private boolean addToJar(Object obj, Point loc) {
	Jar jar = getJar();
	Contained jarObject = getLegalJarObject(obj);
	if (jarObject == null)
	    return false;
	jar.add(jarObject, loc.x, loc.y);
	jar.getWorld().setModified(true);
	return true;
    }
    
    private Contained getLegalJarObject(Object obj) {
	if (obj instanceof Worldly
	    && ((Worldly) obj).getWorld() != getJar().getWorld())
	    return null;
	Contained jarObject;
	if (obj instanceof CocoaCharacter)
	    jarObject = ((CocoaCharacter) obj).getPrototype();
	else {
	    if (obj instanceof Jar) {
		jarObject = (Jar) obj;
		return null;
	    }
	    return null;
	}
	if (getJar().allowAdd(jarObject))
	    return jarObject;
	return null;
    }
}
