/* PlaywriteView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.EmptyBorder;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.LayoutManager;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PlaywriteView extends View
    implements Debug.Constants, MouseTransparency, ResourceIDs.DialogIDs,
	       Target, ToolDestination, ToolTipable, ViewGlue
{
    static final String DRAG_COMMAND = "DRAG";
    static final String GETINFO_COMMAND = "get info";
    private static final boolean mouseDefault = false;
    private static final Point tempPoint = new Point(0, 0);
    private static final boolean CHECK_THREAD = false;
    static int garbageCount = 0;
    static int viewCount = 0;
    private Image image;
    private Color backgroundColor;
    private Border border = BezelBorder.groovedBezel();
    private int imageDisplayStyle = 0;
    private boolean transparent = false;
    private boolean mouseTransparency = false;
    private Vector delegatedEvents = null;
    private Vector commands = null;
    private Vector targets = null;
    private boolean _wantsToBeDragged = false;
    private PlaywriteView _dragDelegate = null;
    private Hashtable supportsDragOut = null;
    private Hashtable supportsDragIn = null;
    private Hashtable supportsTool = null;
    private Point _dragPoint;
    private Image dragImage;
    private boolean dragImageDirty;
    private String name;
    private PlaywriteView grayLayer;
    private LayoutManager disabledLayout;
    private Object model;
    private int _cursor;
    private boolean hilited;
    private String toolTipText;
    private boolean wantsAutoScrollEvents;
    private Vector _borderControls;
    private boolean _autoEditable;
    
    public PlaywriteView() {
	_dragPoint = new Point(0, 0);
	dragImageDirty = true;
	grayLayer = null;
	disabledLayout = null;
	model = this;
	_cursor = 0;
	hilited = false;
	toolTipText = null;
	wantsAutoScrollEvents = false;
	_borderControls = null;
	init();
    }
    
    public PlaywriteView(Rect boundsRect) {
	super(boundsRect);
	_dragPoint = new Point(0, 0);
	dragImageDirty = true;
	grayLayer = null;
	disabledLayout = null;
	model = this;
	_cursor = 0;
	hilited = false;
	toolTipText = null;
	wantsAutoScrollEvents = false;
	_borderControls = null;
	init();
    }
    
    public PlaywriteView(int x, int y, int w, int h) {
	super(x, y, w, h);
	_dragPoint = new Point(0, 0);
	dragImageDirty = true;
	grayLayer = null;
	disabledLayout = null;
	model = this;
	_cursor = 0;
	hilited = false;
	toolTipText = null;
	wantsAutoScrollEvents = false;
	_borderControls = null;
	init();
    }
    
    public PlaywriteView(Image image) {
	super(0, 0, image.width(), image.height());
	_dragPoint = new Point(0, 0);
	dragImageDirty = true;
	grayLayer = null;
	disabledLayout = null;
	model = this;
	_cursor = 0;
	hilited = false;
	toolTipText = null;
	wantsAutoScrollEvents = false;
	_borderControls = null;
	setBorder(null);
	setTransparent(true);
	setImage(image);
	setImageDisplayStyle(0);
	this.setMinSize(image.width(), image.height());
	viewCount++;
    }
    
    private void init() {
	setTransparent(false);
	this.setBuffered(false);
	setBorder(null);
	viewCount++;
	setBackgroundColor(Util.defaultColor);
    }
    
    final String getName() {
	return name;
    }
    
    final void setName(String s) {
	name = s;
    }
    
    final PlaywriteView getGrayLayer() {
	return grayLayer;
    }
    
    final void setGrayLayer(PlaywriteView view) {
	grayLayer = view;
    }
    
    final boolean isDisabled() {
	return grayLayer != null;
    }
    
    Point getDragPoint() {
	return _dragPoint;
    }
    
    void setDragPoint(Point p) {
	_dragPoint = p;
    }
    
    final void setDragPoint(int x, int y) {
	_dragPoint.x = x;
	_dragPoint.y = y;
    }
    
    public Vector getBorderControls() {
	return _borderControls;
    }
    
    public boolean getMouseTransparency() {
	return mouseTransparency;
    }
    
    public void setMouseTransparency(boolean sendToAncestralContainer) {
	mouseTransparency = sendToAncestralContainer;
    }
    
    public View viewForMouse(int x, int y) {
	return super.viewForMouse(x, y);
    }
    
    protected Bitmap createBuffer() {
	Bitmap oldBuffer = this.drawingBuffer();
	if (oldBuffer != null)
	    oldBuffer.flush();
	return BitmapManager.createBitmapManager(this.width(), this.height());
    }
    
    public boolean wantsAutoScrollEvents() {
	return wantsAutoScrollEvents;
    }
    
    public void setWantsAutoScrollEvents(boolean b) {
	wantsAutoScrollEvents = b;
    }
    
    public int cursorForPoint(int x, int y) {
	return _cursor;
    }
    
    public void setCursor(int cursor) {
	_cursor = cursor;
    }
    
    public int getCursor() {
	return _cursor;
    }
    
    public void keyDown(KeyEvent event) {
	Debug.print("debug.view", "view ", this, " got key event: ", event);
	switch (event.key) {
	case 105:
	    if (event.isMetaKeyDown()) {
		Debug.print("debug.view", getInfo());
		break;
	    }
	    /* fall through */
	default:
	    super.keyDown(event);
	}
    }
    
    public void setImage(Image image) {
	if (!(image instanceof LazyBitmap) && image instanceof Bitmap)
	    Util.loadImageData((Bitmap) image);
	_setImage(image);
	setDragImageDirty();
    }
    
    public void emptyOut() {
	Vector subviews = (Vector) this.subviews().clone();
	for (int i = 0; i < subviews.size(); i++)
	    ((View) subviews.elementAt(i)).removeFromSuperview();
    }
    
    public void removeFromSuperview() {
	super.removeFromSuperview();
    }
    
    public void temporarilyRemoveFromSuperview() {
	super.removeFromSuperview();
    }
    
    public void recursivelyRemoveFromSuperview() {
	garbageCount++;
	removeFromSuperview();
	Vector subviews = (Vector) this.subviews().clone();
	for (int i = 0; i < subviews.size(); i++) {
	    View view = (View) subviews.elementAt(i);
	    if (view instanceof PlaywriteView)
		((PlaywriteView) view).recursivelyRemoveFromSuperview();
	    else
		view.removeFromSuperview();
	}
    }
    
    public void addSubview(View subview) {
	if (subview instanceof PlaywriteView) {
	    PlaywriteView pwSubview = (PlaywriteView) subview;
	    ExtendedDragSource ds
		= allowsDragOutOf(pwSubview.getModelObject().getClass());
	    if (ds != null) {
		pwSubview._wantsToBeDragged = true;
		pwSubview._dragDelegate = this;
		pwSubview.setWantsAutoScrollEvents(true);
	    }
	}
	super.addSubview(subview);
	dragImageDirty = true;
    }
    
    void throwIfInWorldThread() {
	COM.stagecast.ifc.netscape.application.Window window = this.window();
	if (!(this instanceof BoardView)) {
	    if (window instanceof PlaywriteWindow) {
		World world = ((PlaywriteWindow) window).getWorld();
		if (world != null && world.inWorldThread()) {
		    System.out.println(this);
		    Debug.stackTrace();
		}
	    }
	}
    }
    
    protected void removeSubview(View view) {
	super.removeSubview(view);
	setDragImageDirty();
	if (getBorderControls() != null)
	    getBorderControls().removeElement(view);
    }
    
    public void subviewDidResize(View subview) {
	super.subviewDidResize(subview);
	setDragImageDirty();
    }
    
    public boolean mouseDown(MouseEvent event) {
	setDragPoint(event.x, event.y);
	if (eventDelegated(event))
	    Debug.print("debug.view", "view is delegating mouse down event: ",
			this);
	else
	    Debug.print("debug.view", "view handled mouse down event: ", this);
	if (mouseTransparency) {
	    Debug.print("debug.view", "view is transparent to mouse clicks: ",
			this);
	    return (this.superview().mouseDown
		    (this.convertEventToView(this.superview(), event)));
	}
	Selection.resetGlobalState(event);
	this.setFocusedView();
	ToolTips.notifyMouseDown();
	if (event.clickCount() == 2 && _autoEditable == true
	    && getModelObject() != null)
	    PlaywriteRoot.app().activateEditorFor(this);
	return true;
    }
    
    public void setAutoEditable(boolean autoEditable) {
	_autoEditable = autoEditable;
    }
    
    public void mouseUp(MouseEvent event) {
	eventDelegated(event);
	if (mouseTransparency)
	    this.superview().mouseUp(this.convertEventToView(this.superview(),
							     event));
    }
    
    public void mouseDragged(MouseEvent event) {
	Point p = getDragPoint();
	if (p != null) {
	    int dx = event.x - p.x;
	    int dy = event.y - p.y;
	    if (dx * dx + dy * dy <= 16)
		return;
	}
	if (wantsAutoScrollEvents
	    && !this.containsPointInVisibleRect(event.x, event.y))
	    this.scrollRectToVisible(new Rect(event.x, event.y, 1, 1));
	if (_wantsToBeDragged)
	    _dragDelegate.performCommand("DRAG", this);
	else
	    eventDelegated(event);
	if (mouseTransparency)
	    this.superview().mouseDragged
		(this.convertEventToView(this.superview(), event));
    }
    
    public void mouseEntered(MouseEvent event) {
	eventDelegated(event);
	if (mouseTransparency)
	    this.superview().mouseEntered
		(this.convertEventToView(this.superview(), event));
	else
	    ToolTips.notifyEntered(this);
    }
    
    public void mouseExited(MouseEvent event) {
	eventDelegated(event);
	if (mouseTransparency)
	    this.superview()
		.mouseExited(this.convertEventToView(this.superview(), event));
	else
	    ToolTips.notifyExited(this);
    }
    
    public String toString() {
	String str = null;
	try {
	    str = (name == null ? super.toString()
		   : "<PlaywriteView '" + name + "'>");
	    str += "[";
	    if (this.subviews().size() < 5) {
		Enumeration e = this.subviews().elements();
		while (e.hasMoreElements())
		    str += e.nextElement().toString() + ", ";
	    } else
		str += String.valueOf(this.subviews().size()) + " subviews";
	    str += "]";
	} catch (Exception exception) {
	    str = super.toString();
	}
	return str;
    }
    
    public void startFocus() {
	Debug.print("debug.view", this, " got the focus");
    }
    
    void selectModel(MouseEvent event) {
	Object model = getModelObject();
	if (model instanceof Selectable) {
	    if (event != null && event.isShiftKeyDown())
		Selection.addToSelection((Selectable) model, this.superview());
	    else
		Selection.select((Selectable) model, this.superview());
	    this.setFocusedView();
	} else if (event != null && event.isShiftKeyDown())
	    PlaywriteDialog.warning("dialog ns");
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("get info"))
	    Debug.print("debug.view", getInfo());
	else if (command.equals("DRAG")) {
	    PlaywriteView view = (PlaywriteView) data;
	    Image dragImage = view.getDragImage();
	    ExtendedDragSource ds
		= allowsDragOutOf(view.getModelObject().getClass());
	    view.convertPointToView(this, view.getDragPoint(), tempPoint);
	    if (ds.prepareToDrag(data))
		new AutoDragSession(ds, dragImage, view.x(), view.y(),
				    tempPoint.x, tempPoint.y, null, data);
	} else
	    throw new PlaywriteInternalError("Bad command: " + command);
    }
    
    public final void setDirty() {
	this.setDirty(true);
    }
    
    public void addDirtyRect(Rect rect) {
	super.addDirtyRect(rect);
	if (rect != null && !this.localBounds().contains(rect)
	    && this.superview() != null) {
	    rect = new Rect(rect);
	    rect.x += bounds.x;
	    rect.y += bounds.y;
	    this.superview().addDirtyRect(rect);
	}
    }
    
    public final void setEventDelegate(int eventType, int mask, int clickCount,
				       String command, Target target) {
	MouseEvent event = new MouseEvent(0L, eventType, 0, 0, mask);
	event.setClickCount(clickCount);
	addDelegate(event, command, target);
    }
    
    private void addDelegate(MouseEvent event, String command, Target target) {
	if (delegatedEvents == null) {
	    delegatedEvents = new Vector(1);
	    commands = new Vector(1);
	    targets = new Vector(1);
	} else {
	    for (int i = 0; i < delegatedEvents.size(); i++) {
		if (eventsMatch(event,
				(MouseEvent) delegatedEvents.elementAt(i))) {
		    commands.replaceElementAt(i, command);
		    targets.replaceElementAt(i, target);
		    return;
		}
	    }
	}
	if (event.modifiers() == 0) {
	    delegatedEvents.addElement(event);
	    commands.addElement(command);
	    targets.addElement(target);
	} else {
	    delegatedEvents.insertElementAt(event, 0);
	    commands.insertElementAt(command, 0);
	    targets.insertElementAt(target, 0);
	}
    }
    
    private boolean eventsMatch(MouseEvent test, MouseEvent model) {
	if (test.type() == model.type()
	    && test.clickCount() == model.clickCount()) {
	    if (model.modifiers() == 0)
		return true;
	    Debug.print("debug.view", "event mods: " + test.modifiers());
	    return (model.isAltKeyDown() == test.isAltKeyDown()
		    && model.isControlKeyDown() == test.isControlKeyDown()
		    && model.isMetaKeyDown() == test.isMetaKeyDown()
		    && model.isShiftKeyDown() == test.isShiftKeyDown());
	}
	return false;
    }
    
    private boolean eventDelegated(MouseEvent event) {
	if (delegatedEvents == null)
	    return false;
	for (int i = 0; i < delegatedEvents.size(); i++) {
	    if (eventsMatch(event,
			    (MouseEvent) delegatedEvents.elementAt(i))) {
		((Target) targets.elementAt(i))
		    .performCommand((String) commands.elementAt(i), this);
		return true;
	    }
	}
	return false;
    }
    
    String getInfo() {
	String infomania = toString() + "\n";
	if (delegatedEvents != null) {
	    for (int i = 0; i < delegatedEvents.size(); i++) {
		MouseEvent event = (MouseEvent) delegatedEvents.elementAt(i);
		int eventType = event.type();
		infomania += " Mouse";
		switch (eventType) {
		case -1:
		    infomania += "Down ";
		    break;
		case -3:
		    infomania += "Up ";
		    break;
		case -2:
		    infomania += "Dragged ";
		    break;
		case -4:
		    infomania += "Entered ";
		    break;
		case -5:
		    infomania += "Moved ";
		    break;
		case -6:
		    infomania += "Exited ";
		    break;
		}
		infomania += event.clickCount() + " clicks delegated to ";
		infomania += (targets.elementAt(i).toString() + " command "
			      + commands.elementAt(i) + "\n");
	    }
	    return infomania;
	}
	return toString();
    }
    
    public ToolDestination acceptsTool(ToolSession session, int x, int y) {
	if (isDisabled())
	    return null;
	if (this.window() != null
	    && this.window() instanceof PlaywriteWindow) {
	    PlaywriteWindow win = (PlaywriteWindow) this.window();
	    if (win.getWorld() != null
		&& win.getWorld().getState() == World.RUNNING)
		return null;
	}
	return (ToolDestination) (supportsTool == null ? null
				  : supportsTool.get(session.toolType()));
    }
    
    public boolean supportsTool(Tool tool) {
	if (supportsTool == null)
	    return false;
	return supportsTool.get(tool) != null;
    }
    
    private boolean inAfterBoardView(View v) {
	for (View view = v.superview(); view != null;
	     view = view.superview()) {
	    if (view instanceof AfterBoardView)
		return true;
	}
	return false;
    }
    
    public void hilite() {
	if (!isHilited()) {
	    hilited = true;
	    setDirty();
	}
    }
    
    public void unhilite() {
	if (isHilited()) {
	    hilited = false;
	    setDirty();
	}
    }
    
    public boolean isHilited() {
	return hilited;
    }
    
    public Object getModelObject() {
	return model;
    }
    
    public void setModelObject(Object obj) {
	model = obj;
    }
    
    public final View view() {
	return this;
    }
    
    public void discard() {
	int size = this.subviews().size();
	for (int i = 0; i < size; i++) {
	    View view = (View) this.subviews().elementAt(i);
	    if (view instanceof ViewGlue)
		((ViewGlue) view).discard();
	}
	if (image != null && image instanceof Bitmap) {
	    ((Bitmap) image).flush();
	    image = null;
	}
	if (delegatedEvents != null) {
	    delegatedEvents.removeAllElements();
	    delegatedEvents = null;
	}
	if (commands != null) {
	    commands.removeAllElements();
	    commands = null;
	}
	if (targets != null) {
	    targets.removeAllElements();
	    targets = null;
	}
	if (supportsDragOut != null) {
	    supportsDragOut.clear();
	    supportsDragOut = null;
	}
	if (supportsDragIn != null) {
	    supportsDragIn.clear();
	    supportsDragIn = null;
	}
	if (supportsTool != null) {
	    supportsTool.clear();
	    supportsTool = null;
	}
	_dragDelegate = null;
	grayLayer = null;
	disabledLayout = null;
    }
    
    public final void willBecomeSelected() {
	/* empty */
    }
    
    public final void willBecomeUnselected() {
	/* empty */
    }
    
    public void setToolTipText(String s) {
	toolTipText = s;
    }
    
    public String getToolTipText() {
	return toolTipText;
    }
    
    public void allowTool(Tool tool, ToolDestination dest) {
	if (supportsTool == null)
	    supportsTool = new Hashtable(5);
	supportsTool.put(tool, dest);
    }
    
    public ToolDestination disallowTool(Tool tool) {
	if (supportsTool != null)
	    return (ToolDestination) supportsTool.remove(tool);
	return null;
    }
    
    public boolean toolEntered(ToolSession session) {
	return true;
    }
    
    public boolean toolMoved(ToolSession session) {
	return true;
    }
    
    public void toolExited(ToolSession session) {
	/* empty */
    }
    
    public boolean toolClicked(ToolSession session) {
	return false;
    }
    
    public void toolDragged(ToolSession session) {
	/* empty */
    }
    
    public void toolReleased(ToolSession session) {
	/* empty */
    }
    
    public Image getDragImage() {
	if (dragImage == null || dragImageDirty) {
	    dragImage = createDragImage();
	    dragImageDirty = false;
	}
	return dragImage;
    }
    
    public void setDragImage(Image image) {
	dragImageDirty = false;
	dragImage = image;
    }
    
    public Image createDragImage() {
	boolean wasHilited = hilited;
	if (hilited)
	    unhilite();
	Image returnImage = Util.makeBitmapFromView(this);
	return returnImage;
    }
    
    public void setDragImageDirty() {
	dragImageDirty = true;
    }
    
    public void allowDragInto(Class dropModelClass, DragDestination dest) {
	if (supportsDragIn == null)
	    supportsDragIn = new Hashtable(5);
	supportsDragIn.put(dropModelClass, dest);
    }
    
    public DragDestination allowsDragInto(Class dropModelClass) {
	if (supportsDragIn == null)
	    return null;
	for (/**/; dropModelClass != null;
	     dropModelClass = dropModelClass.getSuperclass()) {
	    DragDestination dest
		= (DragDestination) supportsDragIn.get(dropModelClass);
	    if (dest != null)
		return dest;
	}
	return null;
    }
    
    public void allowDragOutOf(Class dragModelClass, ExtendedDragSource ds) {
	if (supportsDragOut == null)
	    supportsDragOut = new Hashtable(5);
	supportsDragOut.put(dragModelClass, ds);
    }
    
    public ExtendedDragSource allowsDragOutOf(Class dragModelClass) {
	if (supportsDragOut == null)
	    return null;
	for (/**/; dragModelClass != null;
	     dragModelClass = dragModelClass.getSuperclass()) {
	    ExtendedDragSource src
		= (ExtendedDragSource) supportsDragOut.get(dragModelClass);
	    if (src != null)
		return src;
	}
	return null;
    }
    
    public DragDestination acceptsDrag(DragSession ds, int x, int y) {
	if (isDisabled())
	    return null;
	Object model = modelObjectBeingDragged(ds);
	return allowsDragInto(model.getClass());
    }
    
    public final PlaywriteView viewBeingDragged(DragSession ds) {
	return (PlaywriteView) ds.data();
    }
    
    public final Object modelObjectBeingDragged(DragSession ds) {
	return ((PlaywriteView) ds.data()).getModelObject();
    }
    
    public int bottom() {
	return bounds.y + bounds.height;
    }
    
    public int right() {
	return bounds.x + bounds.width;
    }
    
    public int top() {
	return bounds.y;
    }
    
    public int left() {
	return bounds.x;
    }
    
    public Size minSize() {
	Size basesize = super.minSize();
	Vector subviews = this.subviews();
	int maxx = 0;
	int maxy = 0;
	if (basesize.width != 0 || basesize.height != 0)
	    return basesize;
	this.layoutView(0, 0);
	int count = subviews.count();
	for (int i = 0; i < count; i++) {
	    View v = (View) subviews.elementAt(i);
	    if (v.bounds().maxX() > maxx)
		maxx = v.bounds().maxX();
	    if (v.bounds().maxY() > maxy)
		maxy = v.bounds().maxY();
	}
	basesize.width = maxx;
	basesize.height = maxy;
	if (border != null) {
	    basesize.width += border.rightMargin();
	    basesize.height += border.bottomMargin();
	}
	return basesize;
    }
    
    public Rect interiorRect() {
	Rect tmpRect;
	if (border != null)
	    tmpRect = border.interiorRect(0, 0, this.width(), this.height());
	else
	    tmpRect = new Rect(0, 0, this.width(), this.height());
	return tmpRect;
    }
    
    public void setBackgroundColor(Color aColor) {
	backgroundColor = aColor;
	this.setDirty(true);
    }
    
    public Color backgroundColor() {
	return backgroundColor;
    }
    
    public void setBorder(Border newBorder) {
	if (newBorder == null)
	    newBorder = EmptyBorder.emptyBorder();
	border = newBorder;
	this.setDirty(true);
    }
    
    public Border border() {
	return border;
    }
    
    private void _setImage(Image anImage) {
	image = anImage;
	this.setDirty(true);
    }
    
    public Image image() {
	return image;
    }
    
    public void setImageDisplayStyle(int aStyle) {
	if (aStyle != 0 && aStyle != 2 && aStyle != 1)
	    throw new InconsistencyException("Unknown image display style: "
					     + aStyle);
	imageDisplayStyle = aStyle;
	this.setDirty(true);
    }
    
    public int imageDisplayStyle() {
	return imageDisplayStyle;
    }
    
    public void setTransparent(boolean flag) {
	transparent = flag;
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public void drawViewBackground(Graphics g) {
	if (!transparent
	    && (image == null
		|| imageDisplayStyle == 0 && backgroundColor != null)) {
	    Rect tmpRect;
	    if (image == null)
		tmpRect = new Rect();
	    else
		tmpRect = new Rect(0, 0, image.width(), image.height());
	    if (!tmpRect.contains(bounds)) {
		tmpRect.setBounds(0, 0, this.width(), this.height());
		g.setColor(backgroundColor);
		g.fillRect(tmpRect);
	    }
	}
	if (image != null)
	    image.drawWithStyle(g, 0, 0, this.width(), this.height(),
				imageDisplayStyle);
    }
    
    public void drawView(Graphics g) {
	drawViewBackground(g);
    }
    
    public void drawSubviews(Graphics g) {
	super.drawSubviews(g);
	drawViewBorder(g);
    }
    
    public void drawHilite(Graphics g) {
	g.setColor(Util.HIGHLIGHT_COLOR);
	g.drawRect(0, 0, this.width(), this.height());
	g.drawRect(1, 1, this.width() - 2, this.height() - 2);
    }
    
    public void drawViewBorder(Graphics g) {
	if (border != null)
	    border.drawInRect(g, 0, 0, this.width(), this.height());
	if (getBorderControls() != null) {
	    g.pushState();
	    int i = getBorderControls().size();
	    while (i-- > 0) {
		View tempView = (View) getBorderControls().elementAt(i);
		g.translate(tempView.x(), tempView.y());
		tempView.drawView(g);
		g.translate(-tempView.x(), -tempView.y());
	    }
	    g.popState();
	}
	if (hilited)
	    drawHilite(g);
    }
    
    public void addBorderControl(View borderControl) {
	if (getBorderControls() == null)
	    _borderControls = new Vector(2);
	_borderControls.addElement(borderControl);
	addSubview(borderControl);
    }
    
    public void didSizeBy(int deltaWidth, int deltaHeight) {
	super.didSizeBy(deltaWidth, deltaHeight);
	if (grayLayer != null)
	    grayLayer.sizeBy(deltaWidth, deltaHeight);
    }
    
    protected void finalize() throws Throwable {
	garbageCount--;
	viewCount--;
	Debug.print("debug.gc",
		    (String.valueOf(garbageCount)
		     + " Reclaiming PlaywriteView "),
		    this, " (" + viewCount, " remain)");
	super.finalize();
    }
    
    void disable() {
	if (!isDisabled()) {
	    grayLayer = new GrayLayer(this.width(), this.height());
	    disabledLayout = this.layoutManager();
	    this.setLayoutManager(null);
	    addSubview(grayLayer);
	    this.setDirty(true);
	}
    }
    
    void enable() {
	if (isDisabled()) {
	    grayLayer.removeFromSuperview();
	    grayLayer = null;
	    this.setLayoutManager(disabledLayout);
	    disabledLayout = null;
	    this.setDirty(true);
	}
    }
}
