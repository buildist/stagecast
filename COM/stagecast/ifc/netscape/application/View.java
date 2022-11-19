/* View - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class View
{
    View _superview;
    Size _minSize;
    Bitmap drawingBuffer;
    private Vector subviews;
    private Vector kbdOrder;
    LayoutManager layoutManager;
    Hashtable _keyboardBindings;
    public Rect bounds = new Rect();
    Rect dirtyRect;
    byte resizeInstr = 4;
    int drawingDisabled = 0;
    boolean autoResizeSubviews = true;
    boolean buffered;
    boolean drawingBufferValid;
    boolean drawingBufferIsBitCache;
    boolean isDirty;
    boolean needFocus;
    boolean focusPaused;
    boolean wantsKeyboardArrow;
    public static final int RIGHT_MARGIN_CAN_CHANGE = 0;
    public static final int LEFT_MARGIN_CAN_CHANGE = 1;
    public static final int WIDTH_CAN_CHANGE = 2;
    public static final int CENTER_HORIZ = 32;
    public static final int BOTTOM_MARGIN_CAN_CHANGE = 4;
    public static final int TOP_MARGIN_CAN_CHANGE = 8;
    public static final int HEIGHT_CAN_CHANGE = 16;
    public static final int CENTER_VERT = 64;
    private static final int DEFAULT_RESIZE_INSTR = 4;
    private static final int VERT_MASK = 92;
    private static final int HORZ_MASK = 35;
    private static final String KBD_COMMAND_KEY = "kbdCmd";
    private static final String KBD_WHEN = "when";
    private static final String KBD_DATA_KEY = "kbdData";
    static final int DEFAULT_CURSOR = -1;
    public static final int ARROW_CURSOR = 0;
    public static final int CROSSHAIR_CURSOR = 1;
    public static final int TEXT_CURSOR = 2;
    public static final int WAIT_CURSOR = 3;
    public static final int SW_RESIZE_CURSOR = 4;
    public static final int SE_RESIZE_CURSOR = 5;
    public static final int NW_RESIZE_CURSOR = 6;
    public static final int NE_RESIZE_CURSOR = 7;
    public static final int N_RESIZE_CURSOR = 8;
    public static final int S_RESIZE_CURSOR = 9;
    public static final int W_RESIZE_CURSOR = 10;
    public static final int E_RESIZE_CURSOR = 11;
    public static final int HAND_CURSOR = 12;
    public static final int MOVE_CURSOR = 13;
    public static final int WHEN_SELECTED = 0;
    public static final int WHEN_IN_MAIN_WINDOW = 1;
    public static final int ALWAYS = 2;
    
    public View() {
	this(0, 0, 0, 0);
    }
    
    public View(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public View(int i, int i_0_, int i_1_, int i_2_) {
	_setBounds(i, i_0_, i_1_, i_2_);
    }
    
    void _setBounds(int i, int i_3_, int i_4_, int i_5_) {
	bounds.setBounds(i, i_3_, i_4_, i_5_);
    }
    
    int subviewCount() {
	if (subviews == null)
	    return 0;
	return subviews.count();
    }
    
    public Vector subviews() {
	if (subviews == null)
	    subviews = new Vector();
	return subviews;
    }
    
    public Vector peersForSubview(View view_6_) {
	if (subviewCount() == 0 || !subviews().contains(view_6_))
	    return new Vector();
	Vector vector = new Vector();
	vector.addElementsIfAbsent(subviews);
	return vector;
    }
    
    public boolean descendsFrom(View view_7_) {
	if (view_7_ == this)
	    return true;
	if (_superview == null || view_7_ == null)
	    return false;
	if (_superview == view_7_)
	    return true;
	return _superview.descendsFrom(view_7_);
    }
    
    public InternalWindow window() {
	if (_superview == null)
	    return null;
	return _superview.window();
    }
    
    public Rect bounds() {
	return new Rect(bounds);
    }
    
    public int x() {
	return bounds.x;
    }
    
    public int y() {
	return bounds.y;
    }
    
    public int width() {
	return bounds.width;
    }
    
    public int height() {
	return bounds.height;
    }
    
    public View superview() {
	return _superview;
    }
    
    public void setHorizResizeInstruction(int i) {
	if (i != 0 && i != 1 && i != 2 && i != 32)
	    throw new IllegalArgumentException
		      ("invalid horz resize instruction " + i);
	resizeInstr &= 0x5c;
	resizeInstr |= i;
    }
    
    public int horizResizeInstruction() {
	return resizeInstr & 0x23;
    }
    
    public void setVertResizeInstruction(int i) {
	if (i != 4 && i != 8 && i != 16 && i != 64)
	    throw new IllegalArgumentException
		      ("invalid vert resize instruction " + i);
	resizeInstr &= 0x23;
	resizeInstr |= i;
    }
    
    public int vertResizeInstruction() {
	return resizeInstr & 0x5c;
    }
    
    public boolean wantsAutoscrollEvents() {
	return false;
    }
    
    public DragDestination acceptsDrag(DragSession dragsession, int i,
				       int i_8_) {
	return null;
    }
    
    public void setAutoResizeSubviews(boolean bool) {
	autoResizeSubviews = bool;
    }
    
    public boolean doesAutoResizeSubviews() {
	return autoResizeSubviews;
    }
    
    public void didMoveBy(int i, int i_9_) {
	/* empty */
    }
    
    public void didSizeBy(int i, int i_10_) {
	if (autoResizeSubviews)
	    layoutView(i, i_10_);
    }
    
    public void setBounds(Rect rect) {
	setBounds(rect.x, rect.y, rect.width, rect.height);
    }
    
    public void setBounds(int i, int i_11_, int i_12_, int i_13_) {
	int i_14_ = i - bounds.x;
	int i_15_ = i_11_ - bounds.y;
	int i_16_ = i_12_ - bounds.width;
	int i_17_ = i_13_ - bounds.height;
	boolean bool = i_14_ != 0 || i_15_ != 0;
	boolean bool_18_ = i_16_ != 0 || i_17_ != 0;
	if (bool || bool_18_) {
	    _setBounds(i, i_11_, i_12_, i_13_);
	    if (buffered && bool_18_) {
		if (drawingBuffer != null)
		    drawingBuffer.flush();
		if (i_12_ > 0 && i_13_ > 0)
		    drawingBuffer = createBuffer();
		else
		    drawingBuffer = null;
		drawingBufferValid = false;
	    }
	    if (bool) {
		if (_superview != null)
		    _superview.subviewDidMove(this);
		didMoveBy(i_14_, i_15_);
	    }
	    if (bool_18_) {
		if (_superview != null)
		    _superview.subviewDidResize(this);
		didSizeBy(i_16_, i_17_);
	    }
	}
    }
    
    public void moveBy(int i, int i_19_) {
	setBounds(bounds.x + i, bounds.y + i_19_, bounds.width, bounds.height);
    }
    
    public void moveTo(int i, int i_20_) {
	setBounds(i, i_20_, bounds.width, bounds.height);
    }
    
    public void sizeBy(int i, int i_21_) {
	setBounds(bounds.x, bounds.y, bounds.width + i, bounds.height + i_21_);
    }
    
    public void sizeTo(int i, int i_22_) {
	setBounds(bounds.x, bounds.y, i, i_22_);
    }
    
    public void setMinSize(int i, int i_23_) {
	if (i == -1 || i_23_ == -1)
	    _minSize = null;
	else
	    _minSize = new Size(i, i_23_);
    }
    
    public Size minSize() {
	if (_minSize != null)
	    return new Size(_minSize);
	return new Size();
    }
    
    public void sizeToMinSize() {
	Size size = minSize();
	sizeTo(size.width, size.height);
    }
    
    public void subviewDidResize(View view_24_) {
	if (_superview != null)
	    _superview.subviewDidResize(view_24_);
    }
    
    public void subviewDidMove(View view_25_) {
	if (_superview != null)
	    _superview.subviewDidMove(view_25_);
    }
    
    private void setSuperview(View view_26_) {
	_superview = view_26_;
	ancestorWasAddedToViewHierarchy(view_26_);
	RootView rootview = rootView();
	if (rootview != null) {
	    rootview.updateCursorLater();
	    rootview.viewHierarchyChanged();
	}
    }
    
    public void addSubview(View view_27_) {
	if (view_27_ != null) {
	    invalidateKeyboardSelectionOrder();
	    if (subviews == null)
		subviews = new Vector();
	    else if (subviews.contains(view_27_))
		return;
	    subviews.addElement(view_27_);
	    view_27_.setSuperview(this);
	    if (layoutManager != null)
		layoutManager.addSubview(view_27_);
	}
    }
    
    protected void ancestorWasAddedToViewHierarchy(View view_28_) {
	if (buffered)
	    setBuffered(true);
	if (needFocus)
	    setFocusedView();
	int i = subviewCount();
	while (i-- > 0) {
	    View view_29_ = (View) subviews.elementAt(i);
	    view_29_.ancestorWasAddedToViewHierarchy(view_28_);
	}
    }
    
    protected void removeSubview(View view_30_) {
	invalidateKeyboardSelectionOrder();
	if (subviews != null)
	    subviews.removeElement(view_30_);
	if (layoutManager != null)
	    layoutManager.removeSubview(view_30_);
    }
    
    public void removeFromSuperview() {
	if (_superview != null) {
	    RootView rootview = rootView();
	    if (rootview != null)
		rootview.updateCursorLater();
	    ancestorWillRemoveFromViewHierarchy(this);
	    _superview.removeSubview(this);
	    _superview = null;
	    if (rootview != null)
		rootview.viewHierarchyChanged();
	}
    }
    
    protected void ancestorWillRemoveFromViewHierarchy(View view_31_) {
	if (drawingBuffer != null) {
	    drawingBuffer.flush();
	    drawingBuffer = null;
	}
	RootView rootview = rootView();
	if (rootview != null) {
	    if (rootview.mouseView() == this)
		rootview.setMouseView(null);
	    if (rootview._moveView == this)
		rootview._moveView = null;
	}
	if (isDirty)
	    setDirty(false);
	int i = subviewCount();
	while (i-- > 0) {
	    View view_32_ = (View) subviews.elementAt(i);
	    view_32_.ancestorWillRemoveFromViewHierarchy(view_31_);
	}
    }
    
    public boolean containsPoint(int i, int i_33_) {
	return Rect.contains(0, 0, width(), height(), i, i_33_);
    }
    
    public boolean containsPointInVisibleRect(int i, int i_34_) {
	Rect rect = Rect.newRect();
	computeVisibleRect(rect);
	boolean bool = rect.contains(i, i_34_);
	Rect.returnRect(rect);
	return bool;
    }
    
    View _viewForRect(Rect rect, View view_35_) {
	View view_36_ = null;
	if (rect == null)
	    return null;
	Rect rect_37_;
	if (view_35_ != null) {
	    if (!bounds.contains(rect))
		return null;
	    rect_37_ = Rect.newRect();
	    view_35_.convertRectToView(this, rect, rect_37_);
	} else
	    rect_37_ = Rect.newRect(rect);
	int i = subviewCount();
	while (i-- > 0) {
	    View view_38_ = (View) subviews.elementAt(i);
	    view_36_ = view_38_._viewForRect(rect_37_, this);
	    if (view_36_ != null)
		break;
	}
	Rect.returnRect(rect_37_);
	if (view_36_ != null)
	    return view_36_;
	if (isTransparent() && view_35_ != null)
	    return null;
	return this;
    }
    
    public View viewForMouse(int i, int i_39_) {
	Object object = null;
	Object object_40_ = null;
	if (!containsPoint(i, i_39_))
	    return null;
	int i_41_ = subviewCount();
	while (i_41_-- > 0) {
	    View view_42_ = (View) subviews.elementAt(i_41_);
	    if (!(view_42_ instanceof InternalWindow)) {
		View view_43_
		    = view_42_.viewForMouse(i - view_42_.bounds.x,
					    i_39_ - view_42_.bounds.y);
		if (view_43_ != null)
		    return view_43_;
	    }
	}
	return this;
    }
    
    public int cursorForPoint(int i, int i_44_) {
	return 0;
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	return false;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	/* empty */
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	/* empty */
    }
    
    public void mouseEntered(MouseEvent mouseevent) {
	/* empty */
    }
    
    public void mouseMoved(MouseEvent mouseevent) {
	/* empty */
    }
    
    public void mouseExited(MouseEvent mouseevent) {
	/* empty */
    }
    
    public void keyDown(KeyEvent keyevent) {
	if (_superview != null)
	    _superview.keyDown(keyevent);
    }
    
    public void keyUp(KeyEvent keyevent) {
	if (_superview != null)
	    _superview.keyUp(keyevent);
    }
    
    public void keyTyped(KeyEvent keyevent) {
	if (_superview != null)
	    _superview.keyTyped(keyevent);
    }
    
    View scrollingView() {
	if (_superview != null)
	    return _superview.scrollingView();
	return null;
    }
    
    public void scrollRectToVisible(Rect rect) {
	if (_superview != null)
	    _superview.scrollRectToVisible(convertRectToView(_superview,
							     rect));
    }
    
    public void disableDrawing() {
	drawingDisabled++;
    }
    
    public void reenableDrawing() {
	drawingDisabled--;
	if (drawingDisabled < 0)
	    drawingDisabled = 0;
    }
    
    public boolean isDrawingEnabled() {
	return drawingDisabled == 0;
    }
    
    public boolean isInViewHierarchy() {
	RootView rootview = rootView();
	return rootview != null && rootview.isVisible();
    }
    
    public RootView rootView() {
	if (_superview == null)
	    return null;
	return _superview.rootView();
    }
    
    public boolean canDraw() {
	if (drawingDisabled > 0 || _superview == null)
	    return false;
	return _superview.canDraw();
    }
    
    public void computeVisibleRect(Rect rect) {
	if (_superview == null)
	    rect.setBounds(0, 0, width(), height());
	else {
	    _superview.computeVisibleRect(rect);
	    _superview.convertRectToView(this, rect, rect);
	    rect.intersectWith(0, 0, width(), height());
	}
    }
    
    public boolean isTransparent() {
	return true;
    }
    
    public boolean wantsMouseEventCoalescing() {
	return true;
    }
    
    View opaqueAncestor() {
	if (isTransparent() && _superview != null)
	    return _superview.opaqueAncestor();
	return this;
    }
    
    public Rect dirtyRect() {
	return (dirtyRect == null ? new Rect(0, 0, bounds.width, bounds.height)
		: new Rect(dirtyRect));
    }
    
    public void addDirtyRect(Rect rect) {
	if (rect == null)
	    setDirty(true);
	else if (isDirty) {
	    if (dirtyRect != null)
		dirtyRect.unionWith(rect);
	} else {
	    RootView rootview = rootView();
	    if (rootview != null) {
		dirtyRect = new Rect(rect);
		rootview.markDirty(this);
		isDirty = true;
	    }
	}
    }
    
    public void setDirty(boolean bool) {
	if (bool) {
	    if (!isDirty) {
		RootView rootview = rootView();
		if (rootview != null) {
		    rootview.markDirty(this);
		    isDirty = true;
		}
	    }
	} else if (isDirty) {
	    RootView rootview = rootView();
	    if (rootview != null) {
		rootview.markClean(this);
		isDirty = false;
	    }
	}
	dirtyRect = null;
    }
    
    public boolean isDirty() {
	return isDirty;
    }
    
    public void drawView(Graphics graphics) {
	/* empty */
    }
    
    public void drawSubviews(Graphics graphics) {
	Rect rect = null;
	if (drawingDisabled <= 0) {
	    Rect rect_45_ = graphics.clipRect();
	    int i = subviewCount();
	    for (int i_46_ = 0; i_46_ < i; i_46_++) {
		View view_47_ = (View) subviews.elementAt(i_46_);
		boolean bool = view_47_.isDrawingEnabled();
		if (rect == null)
		    rect = Rect.newRect();
		convertRectToView(view_47_, rect_45_, rect);
		rect.intersectWith(0, 0, view_47_.bounds.width,
				   view_47_.bounds.height);
		if (bool && !rect.isEmpty())
		    view_47_._drawView(graphics, rect, false);
	    }
	    if (rect != null)
		Rect.returnRect(rect);
	}
    }
    
    void _drawView(Graphics graphics, Rect rect, boolean bool) {
	if (drawingDisabled <= 0) {
	    graphics.pushState();
	    graphics.setDebug(this);
	    if (!bool)
		graphics.translate(bounds.x, bounds.y);
	    if (rect == null) {
		Rect rect_48_ = Rect.newRect(0, 0, width(), height());
		graphics.setClipRect(rect_48_);
		Rect.returnRect(rect_48_);
	    } else
		graphics.setClipRect(rect);
	    if (drawingBuffer != null && bool && !graphics.isDrawingBuffer())
		drawingBuffer.drawAt(graphics, 0, 0);
	    else {
		drawView(graphics);
		drawSubviews(graphics);
	    }
	    graphics.popState();
	}
    }
    
    void clipAndDrawView(Graphics graphics, Rect rect) {
	Rect rect_49_;
	if (graphics.isDrawingBuffer())
	    rect_49_ = Rect.newRect(0, 0, width(), height());
	else {
	    rect_49_ = Rect.newRect();
	    computeVisibleRect(rect_49_);
	}
	rect_49_.intersectWith(rect);
	rect = rect_49_;
	Vector vector;
	if ((this == rootView() && rect.x == 0 && rect.y == 0
	     && rect.width == width() && rect.height == height())
	    || graphics.isDrawingBuffer())
	    vector = null;
	else
	    vector = rootView().windowRects(convertRectToView(null, rect),
					    window());
	if (vector == null || vector.isEmpty()) {
	    _drawView(graphics, rect, true);
	    Rect.returnRect(rect);
	} else {
	    Vector vector_50_ = VectorCache.newVector();
	    Vector vector_51_ = VectorCache.newVector();
	    rect.x += absoluteX();
	    rect.y += absoluteY();
	    vector_51_.addElement(rect);
	    Vector vector_52_ = VectorCache.newVector();
	    int i = vector.count();
	    while (i-- > 0) {
		Rect rect_53_ = (Rect) vector.elementAt(i);
		int i_54_ = vector_51_.count();
		while (i_54_-- > 0) {
		    Rect rect_55_ = (Rect) vector_51_.elementAt(i_54_);
		    rect_55_.computeDisunionRects(rect_53_, vector_50_);
		    if (!vector_50_.isEmpty()) {
			vector_52_.addElementsIfAbsent(vector_50_);
			vector_50_.removeAllElements();
		    } else if (!rect_53_.contains(rect_55_)) {
			vector_52_.addElement(rect_55_);
			vector_51_.removeElement(rect_55_);
		    }
		}
		Vector vector_56_ = vector_51_;
		vector_51_ = vector_52_;
		vector_52_ = vector_56_;
		Rect.returnRects(vector_52_);
	    }
	    VectorCache.returnVector(vector_50_);
	    VectorCache.returnVector(vector_52_);
	    int i_57_ = vector_51_.count();
	    for (i = 0; i < i_57_; i++) {
		Rect rect_58_ = (Rect) vector_51_.elementAt(i);
		int i_59_ = i_57_;
		while (i_59_-- > 0) {
		    Rect rect_60_ = (Rect) vector_51_.elementAt(i_59_);
		    if (rect_60_ != rect_58_ && rect_58_.contains(rect_60_)) {
			Rect.returnRect((Rect)
					vector_51_.removeElementAt(i_59_));
			i_57_--;
			i = -1;
			break;
		    }
		}
	    }
	    i = vector_51_.count();
	    while (i-- > 0) {
		rect = (Rect) vector_51_.elementAt(i);
		if (!rect.isEmpty()) {
		    rect.x -= absoluteX();
		    rect.y -= absoluteY();
		    _drawView(graphics, rect, true);
		}
	    }
	    Rect.returnRects(vector_51_);
	    VectorCache.returnVector(vector_51_);
	}
	Rect.returnRects(vector);
	VectorCache.returnVector(vector);
    }
    
    void _draw(Graphics graphics, Rect rect) {
	boolean bool = (drawingBuffer != null && !graphics.isDrawingBuffer()
			? false : isTransparent());
	if (bool && !(this instanceof InternalWindow)) {
	    Rect rect_61_ = Rect.newRect();
	    View view_62_ = opaqueAncestor();
	    convertRectToView(view_62_, rect, rect_61_);
	    graphics.pushState();
	    graphics.translate(rect.x - rect_61_.x, rect.y - rect_61_.y);
	    view_62_.draw(graphics, rect_61_);
	    Rect.returnRect(rect_61_);
	    graphics.popState();
	} else {
	    updateInvalidDrawingBuffers(rect);
	    clipAndDrawView(graphics, rect);
	}
    }
    
    public void draw(Graphics graphics, Rect rect) {
	if (rect != null) {
	    rect = Rect.newRect(rect);
	    rect.intersectWith(0, 0, width(), height());
	    if (rect.isEmpty()) {
		Rect.returnRect(rect);
		return;
	    }
	} else
	    rect = Rect.newRect(0, 0, width(), height());
	boolean bool = canDraw();
	if (graphics == null || !graphics.isDrawingBuffer()) {
	    if (drawingBuffer == null) {
		if (!bool) {
		    Rect.returnRect(rect);
		    return;
		}
		View view_63_ = ancestorWithDrawingBuffer();
		if (view_63_ != null && view_63_ != this) {
		    Rect rect_64_ = Rect.newRect();
		    computeVisibleRect(rect_64_);
		    rect_64_.intersectWith(rect);
		    convertRectToView(view_63_, rect_64_, rect_64_);
		    view_63_.drawingBufferValid = false;
		    if (graphics == null) {
			graphics = view_63_.createGraphics();
			view_63_.draw(graphics, rect_64_);
			graphics.dispose();
			Object object = null;
		    } else {
			Point point = new Point(0, 0);
			convertPointToView(view_63_, point, point);
			graphics.pushState();
			try {
			    graphics.translate(-point.x, -point.y);
			    view_63_.draw(graphics, rect_64_);
			} finally {
			    graphics.popState();
			}
		    }
		    Rect.returnRect(rect_64_);
		    Rect.returnRect(rect);
		    return;
		}
	    } else if (!drawingBufferIsBitCache)
		updateDrawingBuffer(rect);
	    Rect rect_65_ = convertRectToView(rootView(), rect);
	    rootView().redrawTransparentWindows(rect_65_, window());
	}
	if (graphics != null && graphics.isDrawingBuffer() || bool) {
	    if (graphics == null) {
		graphics = createGraphics();
		_draw(graphics, rect);
		graphics.dispose();
		Object object = null;
	    } else
		_draw(graphics, rect);
	}
	Rect.returnRect(rect);
    }
    
    public void draw(Rect rect) {
	if (isInViewHierarchy())
	    draw(null, rect);
    }
    
    public void draw() {
	if (isInViewHierarchy())
	    draw(null, null);
    }
    
    public void setBuffered(boolean bool) {
	buffered = bool;
	if (bool && drawingBuffer == null) {
	    if (bounds.width != 0 && bounds.height != 0)
		drawingBuffer = createBuffer();
	    drawingBufferValid = false;
	} else if (!bool && drawingBuffer != null) {
	    drawingBuffer.flush();
	    drawingBuffer = null;
	}
    }
    
    public boolean isBuffered() {
	return buffered;
    }
    
    public Bitmap drawingBuffer() {
	return drawingBuffer;
    }
    
    void updateDrawingBuffer(Rect rect) {
	if (rect.intersects(0, 0, width(), height())) {
	    if (drawingBuffer != null) {
		synchronized (drawingBuffer) {
		    Graphics graphics = drawingBuffer.createGraphics();
		    drawingBufferValid = true;
		    graphics.setDebugOptions(shouldDebugGraphics());
		    draw(graphics, rect);
		    if (!canDraw())
			drawingBufferValid = false;
		    graphics.dispose();
		    Object object = null;
		}
	    }
	}
    }
    
    void updateInvalidDrawingBuffers(Rect rect) {
	Rect rect_66_ = null;
	int i = subviewCount();
	while (i-- > 0) {
	    View view_67_ = (View) subviews.elementAt(i);
	    if (rect_66_ == null)
		rect_66_ = Rect.newRect();
	    convertRectToView(view_67_, rect, rect_66_);
	    if (rect_66_.intersects(0, 0, view_67_.width(),
				    view_67_.height())) {
		if (view_67_.drawingBuffer != null
		    && !view_67_.drawingBufferValid)
		    view_67_.updateDrawingBuffer(rect_66_);
		view_67_.updateInvalidDrawingBuffers(rect_66_);
	    }
	}
	if (rect_66_ != null)
	    Rect.returnRect(rect_66_);
    }
    
    View ancestorWithDrawingBuffer() {
	if (drawingBuffer != null)
	    return this;
	if (_superview == null)
	    return null;
	return _superview.ancestorWithDrawingBuffer();
    }
    
    void _startFocus() {
	if (focusPaused) {
	    focusPaused = false;
	    resumeFocus();
	} else
	    startFocus();
    }
    
    void _stopFocus() {
	focusPaused = false;
	stopFocus();
    }
    
    void _pauseFocus() {
	focusPaused = true;
	pauseFocus();
    }
    
    public void startFocus() {
	/* empty */
    }
    
    public void stopFocus() {
	/* empty */
    }
    
    public void pauseFocus() {
	/* empty */
    }
    
    public void resumeFocus() {
	/* empty */
    }
    
    void setFocusedView(View view_68_) {
	if (_superview != null)
	    _superview.setFocusedView(view_68_);
    }
    
    public void setFocusedView() {
	if (_superview != null && (isInViewHierarchy() || window() != null)) {
	    _superview.setFocusedView(this);
	    needFocus = false;
	} else
	    needFocus = true;
    }
    
    Application application() {
	return Application.application();
    }
    
    public void convertToView(View view_69_, int i, int i_70_, Point point) {
	int i_71_ = i;
	int i_72_ = i_70_;
	if (_superview == view_69_) {
	    i_71_ += bounds.x;
	    i_72_ += bounds.y;
	} else if (view_69_ != null && view_69_._superview == this) {
	    i_71_ -= view_69_.bounds.x;
	    i_72_ -= view_69_.bounds.y;
	} else {
	    View view_73_;
	    for (view_73_ = this; view_73_._superview != null;
		 view_73_ = view_73_._superview) {
		i_71_ += view_73_.bounds.x;
		i_72_ += view_73_.bounds.y;
	    }
	    if (view_69_ != null) {
		View view_74_;
		for (view_74_ = view_69_; view_74_._superview != null;
		     view_74_ = view_74_._superview) {
		    i_71_ -= view_74_.bounds.x;
		    i_72_ -= view_74_.bounds.y;
		}
		if (view_73_ != view_74_)
		    throw new InconsistencyException("Can't convert between "
						     + this + " and "
						     + view_69_
						     + ", no common ancestor");
	    }
	}
	point.x = i_71_;
	point.y = i_72_;
    }
    
    public Point convertToView(View view_75_, int i, int i_76_) {
	Point point = new Point();
	convertToView(view_75_, i, i_76_, point);
	return point;
    }
    
    public void convertRectToView(View view_77_, Rect rect, Rect rect_78_) {
	Point point = Point.newPoint();
	convertToView(view_77_, rect.x, rect.y, point);
	rect_78_.setBounds(point.x, point.y, rect.width, rect.height);
	Point.returnPoint(point);
    }
    
    public void convertPointToView(View view_79_, Point point,
				   Point point_80_) {
	convertToView(view_79_, point.x, point.y, point_80_);
    }
    
    public Rect convertRectToView(View view_81_, Rect rect) {
	Rect rect_82_ = new Rect();
	convertRectToView(view_81_, rect, rect_82_);
	return rect_82_;
    }
    
    public Point convertPointToView(View view_83_, Point point) {
	Point point_84_ = new Point();
	convertPointToView(view_83_, point, point_84_);
	return point_84_;
    }
    
    public MouseEvent convertEventToView(View view_85_,
					 MouseEvent mouseevent) {
	Point point = Point.newPoint();
	MouseEvent mouseevent_86_ = (MouseEvent) mouseevent.clone();
	convertToView(view_85_, mouseevent.x, mouseevent.y, point);
	mouseevent_86_.x = point.x;
	mouseevent_86_.y = point.y;
	Point.returnPoint(point);
	return mouseevent_86_;
    }
    
    public void setGraphicsDebugOptions(int i) {
	Graphics.setViewDebug(this, i);
    }
    
    public int graphicsDebugOptions() {
	return Graphics.viewDebug(this);
    }
    
    int shouldDebugGraphics() {
	return Graphics.shouldViewDebug(this);
    }
    
    int absoluteX() {
	int i = 0;
	for (View view_87_ = this; view_87_ != null;
	     view_87_ = view_87_._superview)
	    i += view_87_.bounds.x;
	return i;
    }
    
    int absoluteY() {
	int i = 0;
	for (View view_88_ = this; view_88_ != null;
	     view_88_ = view_88_._superview)
	    i += view_88_.bounds.y;
	return i;
    }
    
    public void setLayoutManager(LayoutManager layoutmanager) {
	layoutManager = layoutmanager;
    }
    
    public LayoutManager layoutManager() {
	return layoutManager;
    }
    
    public void layoutView(int i, int i_89_) {
	if (layoutManager == null)
	    relativeLayoutView(i, i_89_);
	else
	    layoutManager.layoutView(this, i, i_89_);
    }
    
    private void relativeLayoutView(int i, int i_90_) {
	int i_91_ = subviewCount();
	while (i_91_-- > 0) {
	    View view_92_ = (View) subviews.elementAt(i_91_);
	    int i_93_ = view_92_.bounds.x;
	    int i_94_ = view_92_.bounds.y;
	    int i_95_ = view_92_.bounds.width;
	    int i_96_ = view_92_.bounds.height;
	    switch (view_92_.horizResizeInstruction()) {
	    case 1:
		i_93_ += i;
		break;
	    case 2:
		i_95_ += i;
		break;
	    case 32:
		i_93_ = (bounds.width - view_92_.bounds.width) / 2;
		break;
	    default:
		throw new InconsistencyException
			  ("invalid horz resize instruction: "
			   + view_92_.horizResizeInstruction());
	    case 0:
		/* empty */
	    }
	    switch (view_92_.vertResizeInstruction()) {
	    case 8:
		i_94_ += i_90_;
		break;
	    case 16:
		i_96_ += i_90_;
		break;
	    case 64:
		i_94_ = (bounds.height - view_92_.bounds.height) / 2;
		break;
	    default:
		throw new InconsistencyException
			  ("invalid vert resize instruction: "
			   + view_92_.vertResizeInstruction());
	    case 4:
		/* empty */
	    }
	    view_92_.setBounds(i_93_, i_94_, i_95_, i_96_);
	}
    }
    
    public Rect localBounds() {
	return new Rect(0, 0, bounds.width, bounds.height);
    }
    
    public Graphics createGraphics() {
	return Graphics.newGraphics(this);
    }
    
    protected Bitmap createBuffer() {
	return new Bitmap(width(), height());
    }
    
    public String toString() {
	return super.toString() + bounds.toString();
    }
    
    public boolean canBecomeSelectedView() {
	return false;
    }
    
    public boolean hidesSubviewsFromKeyboard() {
	return false;
    }
    
    public View nextSelectableView() {
	return null;
    }
    
    public View previousSelectableView() {
	return null;
    }
    
    public void invalidateKeyboardSelectionOrder() {
	kbdOrder = null;
    }
    
    public void willBecomeSelected() {
	wantsKeyboardArrow = true;
    }
    
    public void willBecomeUnselected() {
	wantsKeyboardArrow = false;
    }
    
    public void setCommandForKey(String string, Object object, int i,
				 int i_97_, int i_98_) {
	KeyStroke keystroke = new KeyStroke(i, i_97_);
	if (_keyboardBindings == null)
	    _keyboardBindings = new Hashtable();
	if (string == null)
	    _keyboardBindings.remove(keystroke);
	else {
	    Hashtable hashtable = new Hashtable();
	    hashtable.put("kbdCmd", string);
	    hashtable.put("when", String.valueOf(i_98_));
	    if (object != null)
		hashtable.put("kbdData", object);
	    _keyboardBindings.put(keystroke, hashtable);
	}
    }
    
    public void setCommandForKey(String string, int i, int i_99_) {
	setCommandForKey(string, this, i, 0, i_99_);
    }
    
    public void removeCommandForKey(int i) {
	setCommandForKey(null, null, i, 0, 0);
    }
    
    public void removeAllCommandsForKeys() {
	_keyboardBindings = null;
    }
    
    boolean hasKeyboardBindings() {
	if (_keyboardBindings != null && _keyboardBindings.count() > 0)
	    return true;
	return false;
    }
    
    boolean performCommandForKeyStroke(KeyStroke keystroke, int i) {
	if (!(this instanceof Target))
	    return false;
	if (_keyboardBindings != null) {
	    Enumeration enumeration = _keyboardBindings.keys();
	    while (enumeration.hasMoreElements()) {
		KeyStroke keystroke_100_
		    = (KeyStroke) enumeration.nextElement();
		if (keystroke_100_.equals(keystroke)) {
		    boolean bool = false;
		    Hashtable hashtable
			= (Hashtable) _keyboardBindings.get(keystroke_100_);
		    String string = (String) hashtable.get("when");
		    int i_101_ = Integer.parseInt(string);
		    switch (i_101_) {
		    case 0:
			if (i == 0)
			    bool = true;
			break;
		    case 1:
			if (i == 0 || i == 1)
			    bool = true;
			break;
		    case 2:
			bool = true;
			break;
		    default:
			throw new InconsistencyException("Wrong condition:"
							 + i_101_);
		    }
		    if (bool) {
			String string_102_ = (String) hashtable.get("kbdCmd");
			((Target) this).performCommand(string_102_,
						       hashtable
							   .get("kbdData"));
			return true;
		    }
		}
	    }
	}
	return false;
    }
    
    public Rect keyboardRect() {
	return localBounds();
    }
    
    View _firstSubview(Vector vector) {
	if (vector.count() == 0)
	    return null;
	View view_103_ = (View) vector.elementAt(0);
	int i = view_103_.y();
	int i_104_ = 1;
	for (int i_105_ = vector.count(); i_104_ < i_105_; i_104_++) {
	    View view_106_ = (View) vector.elementAt(i_104_);
	    if (view_106_.y() < i) {
		view_103_ = view_106_;
		i = view_106_.y();
	    }
	}
	View view_107_ = view_103_;
	int i_108_ = view_103_.x();
	i_104_ = 0;
	for (int i_109_ = vector.count(); i_104_ < i_109_; i_104_++) {
	    View view_110_ = (View) vector.elementAt(i_104_);
	    if (view_110_ != view_107_
		&& (int) Math.sqrt((double) ((view_110_.y() - view_107_.y())
					     * (view_110_.y()
						- view_107_.y()))) <= 10
		&& view_110_.x() < i_108_) {
		view_107_ = view_110_;
		i_108_ = view_110_.x();
	    }
	}
	return view_107_;
    }
    
    private void validateKeyboardOrder() {
	if (kbdOrder == null) {
	    Vector vector = new Vector();
	    int i = 0;
	    for (int i_111_ = subviews.count(); i < i_111_; i++)
		vector.addElement(subviews.elementAt(i));
	    kbdOrder = new Vector();
	    while (vector.count() > 0) {
		View view_112_ = _firstSubview(vector);
		kbdOrder.addElement(view_112_);
		vector.removeElement(view_112_);
		View view_113_;
		for (/**/;
		     (view_113_ = view_112_.nextSelectableView()) != null;
		     view_112_ = view_113_) {
		    if (vector.indexOfIdentical(view_113_) == -1)
			break;
		    kbdOrder.addElement(view_113_);
		    vector.removeElement(view_113_);
		}
	    }
	}
    }
    
    View firstSubview() {
	validateKeyboardOrder();
	if (kbdOrder.count() > 0)
	    return (View) kbdOrder.elementAt(0);
	return null;
    }
    
    View lastSubview() {
	validateKeyboardOrder();
	if (kbdOrder.count() > 0)
	    return (View) kbdOrder.elementAt(kbdOrder.count() - 1);
	return null;
    }
    
    View viewAfter(View view_114_) {
	validateKeyboardOrder();
	int i = kbdOrder.indexOfIdentical(view_114_);
	if (i != -1 && i < kbdOrder.count() - 1)
	    return (View) kbdOrder.elementAt(i + 1);
	return null;
    }
    
    View viewBefore(View view_115_) {
	validateKeyboardOrder();
	int i = kbdOrder.indexOfIdentical(view_115_);
	if (i > 0)
	    return (View) kbdOrder.elementAt(i - 1);
	return null;
    }
    
    boolean wantsKeyboardArrow() {
	return wantsKeyboardArrow;
    }
    
    void getDirtyRect(Rect rect) {
	if (isDirty() && dirtyRect == null) {
	    if (rect.isEmpty())
		rect.setBounds(bounds);
	    else
		rect.unionWith(bounds);
	} else {
	    if (dirtyRect != null) {
		if (rect.isEmpty())
		    rect.setBounds(dirtyRect.x + bounds.x,
				   dirtyRect.y + bounds.y, dirtyRect.width,
				   dirtyRect.height);
		else
		    rect.unionWith(dirtyRect.x + bounds.x,
				   dirtyRect.y + bounds.y, dirtyRect.width,
				   dirtyRect.height);
	    }
	    int i = subviewCount();
	    if (i != 0) {
		rect.moveBy(-bounds.x, -bounds.y);
		while (i-- > 0)
		    ((View) subviews.elementAt(i)).getDirtyRect(rect);
		rect.moveBy(bounds.x, bounds.y);
	    }
	}
    }
}
