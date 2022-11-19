/* InternalWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class InternalWindow extends View implements Window
{
    RootView rootView;
    WindowOwner _owner;
    WindowContentView _contentView;
    View _focusedView;
    View _defaultSelectedView;
    Button _closeButton;
    MenuView menuView;
    Font _titleFont;
    String _title = "";
    Border _border;
    int _layer;
    int _type = 1;
    int _lastX;
    int _lastY;
    public int _resizePart = 0;
    boolean _closeable;
    boolean _resizable;
    boolean _canBecomeMain = true;
    boolean _containsDocument = false;
    boolean _drewOnLastDrag;
    boolean _drawToBackingStore;
    boolean _onscreen = true;
    boolean _createdDrawingBuffer;
    boolean transparent = false;
    boolean scrollToVisible;
    private boolean _blankBorderOverride;
    static Vector _resizeWindowVector = new Vector();
    static final int ABOVE = 0;
    static final int BEHIND = 1;
    public static final int NO_PART = 0;
    public static final int LEFT_PART = 1;
    public static final int MIDDLE_PART = 2;
    public static final int RIGHT_PART = 3;
    public static final int DEFAULT_LAYER = 0;
    public static final int PALETTE_LAYER = 100;
    public static final int MODAL_LAYER = 200;
    public static final int POPUP_LAYER = 300;
    public static final int DRAG_LAYER = 400;
    public static final int IGNORE_WINDOW_CLIPVIEW_LAYER = 500;
    
    public InternalWindow() {
	this(0, 0, 0, 0);
    }
    
    public InternalWindow(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public InternalWindow(int i, int i_0_, int i_1_, int i_2_) {
	this(1, i, i_0_, i_1_, i_2_);
    }
    
    public InternalWindow(int i, int i_3_, int i_4_, int i_5_, int i_6_) {
	super(i_3_, i_4_, i_5_, i_6_);
	rootView = this.application().mainRootView();
	_titleFont = Font.fontNamed("Helvetica", 1, 12);
	_contentView = new WindowContentView(0, 0, 1, 1);
	_contentView.setHorizResizeInstruction(2);
	_contentView.setVertResizeInstruction(16);
	addSubviewToWindow(_contentView);
	_layer = 0;
	_border = new InternalWindowBorder(this);
	layoutParts();
	setType(i);
	_defaultSelectedView = this;
    }
    
    int menuViewHeight() {
	if (menuView == null)
	    return 0;
	return menuView.height();
    }
    
    int titleBarMargin() {
	if (_type == 0 && (_border == null || !_blankBorderOverride))
	    return 0;
	return _border.topMargin();
    }
    
    int leftBorderMargin() {
	if (_type == 0 && (_border == null || !_blankBorderOverride))
	    return 0;
	return _border.leftMargin();
    }
    
    int rightBorderMargin() {
	if (_type == 0 && (_border == null || !_blankBorderOverride))
	    return 0;
	return _border.rightMargin();
    }
    
    int bottomBorderMargin() {
	if (_type == 0 && (_border == null || !_blankBorderOverride))
	    return 0;
	return _border.bottomMargin();
    }
    
    public WindowContentView contentView() {
	return _contentView;
    }
    
    public Size contentSize() {
	if (_contentView == null)
	    return null;
	return new Size(_contentView.bounds.width, _contentView.bounds.height);
    }
    
    public void layoutParts() {
	if (_contentView != null) {
	    _contentView.setAutoResizeSubviews(false);
	    _contentView.setBounds(leftBorderMargin(),
				   titleBarMargin() + menuViewHeight(),
				   bounds.width - (leftBorderMargin()
						   + rightBorderMargin()),
				   bounds.height - (titleBarMargin()
						    + menuViewHeight()
						    + bottomBorderMargin()));
	    _contentView.setAutoResizeSubviews(true);
	}
	if (_closeButton != null) {
	    _closeButton.removeFromSuperview();
	    _closeButton.moveTo(0, 2 + (titleBarMargin() - 4
					- _closeButton.bounds.height) / 2);
	    if (_closeable)
		addSubview(_closeButton);
	}
    }
    
    public void addSubview(View view) {
	if (view == _contentView || view == _closeButton)
	    addSubviewToWindow(view);
	else if (_contentView != null)
	    _contentView.addSubview(view);
    }
    
    public void addSubviewToWindow(View view) {
	super.addSubview(view);
    }
    
    public void setRootView(RootView rootview) {
	if (rootView != rootview && rootView != null && _superview != null)
	    rootView.removeWindow(this);
	rootView = rootview;
    }
    
    public void show() {
	if (rootView == null)
	    throw new InconsistencyException
		      ("Can't show Window.  No RootView");
	if (_owner == null || _owner.windowWillShow(this)) {
	    if (_superview == null)
		rootView.addWindowRelativeTo(this, 0, null);
	    else
		rootView.makeWindowVisible(this, 0, null);
	    if (_owner != null)
		_owner.windowDidShow(this);
	}
    }
    
    public void showModally() {
	Application application = Application.application();
	EventLoop eventloop = application.eventLoop();
	int i = layer();
	if (i < 200)
	    setLayer(200);
	show();
	this.rootView()._setMainWindow(this);
	application.beginModalSessionForView(this);
	application.drawAllDirtyViews();
	while (isVisible()) {
	    Event event = eventloop.getNextEvent();
	    try {
		eventloop.processEvent(event);
	    } catch (Exception exception) {
		System.err.println("Uncaught Exception.");
		exception.printStackTrace(System.err);
		System.err.println("Restarting modal EventLoop.");
	    }
	}
	application.endModalSessionForView(this);
	if (i != layer())
	    setLayer(i);
    }
    
    public void showInFrontOf(InternalWindow internalwindow_7_) {
	if (internalwindow_7_.rootView != rootView) {
	    setRootView(internalwindow_7_.rootView);
	    rootView.addWindowRelativeTo(this, 0, internalwindow_7_);
	} else if (_superview == null)
	    rootView.addWindowRelativeTo(this, 0, internalwindow_7_);
	else
	    rootView.makeWindowVisible(this, 0, internalwindow_7_);
    }
    
    public void showBehind(InternalWindow internalwindow_8_) {
	if (internalwindow_8_.rootView != rootView) {
	    setRootView(internalwindow_8_.rootView);
	    rootView.addWindowRelativeTo(this, 1, internalwindow_8_);
	} else if (_superview == null)
	    rootView.addWindowRelativeTo(this, 1, internalwindow_8_);
    }
    
    public void moveToFront() {
	if (isVisible()) {
	    InternalWindow internalwindow_9_
		= rootView.frontWindowWithLayer(layer());
	    if (internalwindow_9_ != null && internalwindow_9_ != this)
		rootView.makeWindowVisible(this, 0, internalwindow_9_);
	}
    }
    
    public void moveToBack() {
	if (isVisible()) {
	    InternalWindow internalwindow_10_
		= rootView.backWindowWithLayer(layer());
	    if (internalwindow_10_ != null && internalwindow_10_ != this)
		rootView.makeWindowVisible(this, 1, internalwindow_10_);
	}
    }
    
    public void hide() {
	if (isVisible() && (_owner == null || _owner.windowWillHide(this))) {
	    if (containsDocument() && isCurrentDocument())
		Application.application()
		    .chooseNextCurrentDocumentWindow(this);
	    RootView rootview = this.rootView();
	    if (rootview != null)
		rootview.removeWindow(this);
	    if (_owner != null)
		_owner.windowDidHide(this);
	}
    }
    
    public void setCanBecomeMain(boolean bool) {
	_canBecomeMain = bool;
	if (isMain() && !_canBecomeMain && rootView != null)
	    rootView._setMainWindow(null);
    }
    
    public boolean canBecomeMain() {
	return _canBecomeMain;
    }
    
    public boolean isVisible() {
	return _superview != null;
    }
    
    public void setOnscreenAtStartup(boolean bool) {
	_onscreen = bool;
    }
    
    public boolean onscreenAtStartup() {
	return _onscreen;
    }
    
    public boolean isMain() {
	return rootView != null && rootView.mainWindow() == this;
    }
    
    protected Button createCloseButton() {
	Button button = new Button(0, 0, 1, 1);
	button.setImage
	    (Bitmap.bitmapNamed("netscape/application/CloseButton.gif"));
	button.setAltImage
	    (Bitmap.bitmapNamed("netscape/application/CloseButtonActive.gif"));
	button.setTransparent(true);
	button.sizeToMinSize();
	button.setHorizResizeInstruction(0);
	button.setVertResizeInstruction(4);
	button.moveTo(0,
		      2 + (titleBarMargin() - 4 - button.bounds.height) / 2);
	button.setTarget(this);
	button.setCommand("hide");
	button.removeAllCommandsForKeys();
	return button;
    }
    
    public void setCloseable(boolean bool) {
	_closeable = bool;
	if (_type == 0)
	    _closeable = false;
	if (_closeable && _closeButton == null)
	    _closeButton = createCloseButton();
	if (_closeable)
	    addSubviewToWindow(_closeButton);
	else if (_closeButton != null)
	    _closeButton.removeFromSuperview();
    }
    
    public boolean isCloseable() {
	return _closeable;
    }
    
    public void setResizable(boolean bool) {
	if (bool != _resizable) {
	    _resizable = bool;
	    if (_resizable) {
		_contentView.setHorizResizeInstruction(2);
		_contentView.setVertResizeInstruction(16);
	    }
	    if (_type == 1) {
		drawBottomBorder();
		layoutParts();
	    }
	}
    }
    
    public boolean isResizable() {
	return _resizable;
    }
    
    public int resizePartWidth() {
	if (_border instanceof InternalWindowBorder)
	    return ((InternalWindowBorder) _border).resizePartWidth();
	return 0;
    }
    
    public Size windowSizeForContentSize(int i, int i_11_) {
	return new Size(i + leftBorderMargin() + rightBorderMargin(),
			(i_11_ + titleBarMargin() + menuViewHeight()
			 + bottomBorderMargin()));
    }
    
    public void setTitle(String string) {
	if (_title == null || string == null || !_title.equals(string)) {
	    _title = string;
	    drawTitleBar();
	}
    }
    
    public String title() {
	return _title;
    }
    
    public void setBorder(Border border) {
	_border = border;
    }
    
    public Border border() {
	return _border;
    }
    
    public void setLayer(int i) {
	_layer = i;
    }
    
    public int layer() {
	return _layer;
    }
    
    public Size minSize() {
	if (_minSize != null)
	    return new Size(_minSize);
	this.setMinSize(resizePartWidth() * 2 + 1,
			titleBarMargin() + menuViewHeight() + 2);
	if (_type == 0 && (_border == null || !_blankBorderOverride)) {
	    _minSize.width = 0;
	    _minSize.height = 0;
	    return _minSize;
	}
	if (_minSize.width < leftBorderMargin() + rightBorderMargin())
	    _minSize.width = leftBorderMargin() + rightBorderMargin();
	if (_minSize.height
	    < titleBarMargin() + menuViewHeight() + bottomBorderMargin())
	    _minSize.height
		= titleBarMargin() + menuViewHeight() + bottomBorderMargin();
	return new Size(_minSize);
    }
    
    public void setOwner(WindowOwner windowowner) {
	_owner = windowowner;
    }
    
    public WindowOwner owner() {
	return _owner;
    }
    
    public void setMenuView(MenuView menuview) {
	if (menuview == null || menuview != menuView) {
	    if (menuView != null)
		menuView.removeFromSuperview();
	    menuView = menuview;
	    int i = leftBorderMargin();
	    int i_12_ = titleBarMargin();
	    int i_13_
		= bounds.width - (leftBorderMargin() + rightBorderMargin());
	    int i_14_ = menuView.height();
	    if (i_14_ == 0)
		i_14_ = menuView.minSize().height;
	    menuView.setBounds(i, i_12_, i_13_, i_14_);
	    addSubviewToWindow(menuView);
	    layoutParts();
	}
    }
    
    public MenuView menuView() {
	return menuView;
    }
    
    public boolean isPointInBorder(int i, int i_15_) {
	if (_type == 0)
	    return Rect.contains(0, 0, bounds.width, bounds.height, i, i_15_);
	if (_resizable && i_15_ > bounds.height - bottomBorderMargin()) {
	    if (i < resizePartWidth())
		_resizePart = 1;
	    else if (i > bounds.width - resizePartWidth())
		_resizePart = 3;
	    else
		_resizePart = 2;
	    return true;
	}
	if (i_15_ > titleBarMargin())
	    return false;
	return true;
    }
    
    public View viewForMouse(int i, int i_16_) {
	View view = super.viewForMouse(i, i_16_);
	if (_type == 0 && view == _contentView)
	    view = this;
	return view;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
	if (transparent) {
	    _contentView.setTransparent(true);
	    this.setBuffered(true);
	} else {
	    this.setBuffered(false);
	    _contentView.setTransparent(false);
	}
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public void setType(int i) {
	_type = i;
	if (_type == 0) {
	    setCanBecomeMain(false);
	    setCloseable(false);
	}
	layoutParts();
    }
    
    public void setBlankBorderOverride(boolean bool) {
	_blankBorderOverride = bool;
    }
    
    public int type() {
	return _type;
    }
    
    void updateDrawingBuffer() {
	boolean bool = false;
	if (drawingBuffer != null) {
	    if (rootView == null)
		throw new InconsistencyException
			  ("Can't draw window - no RootView");
	    Rect rect = Rect.newRect();
	    rootView.disableWindowsAbove(this, true);
	    this.reenableDrawing();
	    Graphics graphics = drawingBuffer.createGraphics();
	    graphics.setDebugOptions(this.shouldDebugGraphics());
	    this.superview().convertRectToView(null, bounds, rect);
	    graphics.pushState();
	    graphics.translate(-rect.x, -rect.y);
	    rootView.draw(graphics, rect);
	    graphics.popState();
	    int i = (isTransparent() ? rootView.windows.count()
		     : rootView.windows.indexOf(this));
	    int i_17_ = 0;
	    int i_18_ = i;
	    while (i_18_-- > 0 && i_17_ == 0) {
		InternalWindow internalwindow_19_
		    = (InternalWindow) rootView.windows.elementAt(i_18_);
		if (!internalwindow_19_.isTransparent()
		    && internalwindow_19_.bounds.contains(bounds))
		    i_17_ = i_18_;
	    }
	    if (i_17_ == 0) {
		View view = rootView.viewWithBuffer(rootView, rect);
		if (view != null) {
		    Rect rect_20_
			= Rect.newRect(0, 0, this.width(), this.height());
		    this.convertRectToView(view, rect_20_, rect_20_);
		    graphics.pushState();
		    graphics.translate(-rect_20_.x, -rect_20_.y);
		    view.draw(graphics, rect_20_);
		    graphics.popState();
		    Rect.returnRect(rect_20_);
		}
	    }
	    for (i_18_ = i_17_; i_18_ < i; i_18_++) {
		InternalWindow internalwindow_21_
		    = (InternalWindow) rootView.windows.elementAt(i_18_);
		if (internalwindow_21_.bounds.intersects(bounds)) {
		    rect.setBounds(0, 0, this.width(), this.height());
		    this.convertRectToView(internalwindow_21_, rect, rect);
		    graphics.pushState();
		    graphics.translate(-rect.x, -rect.y);
		    internalwindow_21_.draw(graphics, rect);
		    graphics.popState();
		}
	    }
	    Rect.returnRect(rect);
	    rootView.disableWindowsAbove(this, false);
	    this.reenableDrawing();
	    graphics.dispose();
	}
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	rootView.makeWindowVisible(this, 0, null);
	if (!isPointInBorder(mouseevent.x, mouseevent.y))
	    return false;
	_lastX = mouseevent.x + bounds.x;
	_lastY = mouseevent.y + bounds.y;
	if (_resizePart != 0) {
	    InternalWindow internalwindow_22_
		= new InternalWindow(bounds.x, bounds.y, 1, bounds.height);
	    internalwindow_22_.setType(0);
	    internalwindow_22_._contentView.setTransparent(false);
	    internalwindow_22_._contentView.setBackgroundColor(Color.darkGray);
	    internalwindow_22_.setLayer(400);
	    internalwindow_22_.setVertResizeInstruction(16);
	    internalwindow_22_.setRootView(this.rootView());
	    internalwindow_22_.show();
	    _resizeWindowVector.addElement(internalwindow_22_);
	    internalwindow_22_
		= new InternalWindow(bounds.maxX() - 1, bounds.y, 1,
				     bounds.height);
	    internalwindow_22_.setType(0);
	    internalwindow_22_._contentView.setTransparent(false);
	    internalwindow_22_._contentView.setBackgroundColor(Color.darkGray);
	    internalwindow_22_.setLayer(400);
	    internalwindow_22_.setVertResizeInstruction(16);
	    internalwindow_22_.setRootView(this.rootView());
	    internalwindow_22_.show();
	    _resizeWindowVector.addElement(internalwindow_22_);
	    internalwindow_22_ = new InternalWindow(bounds.x + 1, bounds.y,
						    bounds.width - 2, 1);
	    internalwindow_22_.setType(0);
	    internalwindow_22_._contentView.setTransparent(false);
	    internalwindow_22_._contentView.setBackgroundColor(Color.darkGray);
	    internalwindow_22_.setLayer(400);
	    internalwindow_22_.setHorizResizeInstruction(2);
	    internalwindow_22_.setRootView(this.rootView());
	    internalwindow_22_.show();
	    _resizeWindowVector.addElement(internalwindow_22_);
	    internalwindow_22_
		= new InternalWindow(bounds.x + 1, bounds.maxY() - 1,
				     bounds.width - 2, 1);
	    internalwindow_22_.setType(0);
	    internalwindow_22_._contentView.setTransparent(false);
	    internalwindow_22_._contentView.setBackgroundColor(Color.darkGray);
	    internalwindow_22_.setLayer(400);
	    internalwindow_22_.setHorizResizeInstruction(2);
	    internalwindow_22_.setRootView(this.rootView());
	    internalwindow_22_.show();
	    _resizeWindowVector.addElement(internalwindow_22_);
	    return true;
	}
	return true;
    }
    
    public void setBounds(int i, int i_23_, int i_24_, int i_25_) {
	int i_26_ = i - bounds.x;
	int i_27_ = i_23_ - bounds.y;
	int i_28_ = i_24_ - bounds.width;
	int i_29_ = i_25_ - bounds.height;
	if (isVisible())
	    moveByAndSizeBy(i_26_, i_27_, i_28_, i_29_);
	else {
	    _moveBy(i_26_, i_27_);
	    _sizeBy(i_28_, i_29_);
	    this._setBounds(i, i_23_, i_24_, i_25_);
	}
    }
    
    private void _super_moveBy(int i, int i_30_) {
	if (i != 0 || i_30_ != 0) {
	    this._setBounds(bounds.x + i, bounds.y + i_30_, bounds.width,
			    bounds.height);
	    if (_superview != null)
		_superview.subviewDidMove(this);
	    this.didMoveBy(i, i_30_);
	}
    }
    
    private void _super_sizeBy(int i, int i_31_) {
	if (i != 0 || i_31_ != 0) {
	    this._setBounds(bounds.x, bounds.y, bounds.width + i,
			    bounds.height + i_31_);
	    if (buffered) {
		if (bounds.width != 0 && bounds.height != 0) {
		    drawingBuffer = new Bitmap(bounds.width, bounds.height);
		    drawingBufferValid = false;
		} else if (drawingBuffer != null) {
		    drawingBuffer.flush();
		    drawingBuffer = null;
		}
	    }
	    this.disableDrawing();
	    if (_superview != null)
		_superview.subviewDidResize(this);
	    super.didSizeBy(i, i_31_);
	    this.reenableDrawing();
	}
    }
    
    protected void willMoveTo(Point point) {
	/* empty */
    }
    
    private void _moveBy(int i, int i_32_) {
	if (i != 0 || i_32_ != 0) {
	    Point point = Point.newPoint(bounds.x + i, bounds.y + i_32_);
	    willMoveTo(point);
	    i = point.x - bounds.x;
	    i_32_ = point.y - bounds.y;
	    Point.returnPoint(point);
	    if (!isVisible())
		_super_moveBy(i, i_32_);
	    else {
		_lastX = bounds.x;
		_lastY = bounds.y;
		MouseEvent mouseevent
		    = new MouseEvent(0L, -2, _lastX + i, _lastY + i_32_, 0);
		mouseDragged(mouseevent);
	    }
	}
    }
    
    void _checkSize(Size size) {
	Size size_33_ = minSize();
	if (bounds.width + size.width < size_33_.width)
	    size.width = size_33_.width - bounds.width;
	if (bounds.height + size.height < size_33_.height)
	    size.height = size_33_.height - bounds.height;
	if (_owner != null)
	    _owner.windowWillSizeBy(this, size);
    }
    
    private void _sizeBy(int i, int i_34_) {
	if (i != 0 || i_34_ != 0) {
	    Size size = Size.newSize(i, i_34_);
	    _checkSize(size);
	    i = size.width;
	    i_34_ = size.height;
	    Size.returnSize(size);
	    if (!isVisible()) {
		_super_sizeBy(i, i_34_);
		layoutParts();
	    } else {
		Rect rect = Rect.newRect(bounds);
		_super_sizeBy(i, i_34_);
		layoutParts();
		if (this.canDraw()) {
		    rect.unionWith(bounds);
		    this.superview().convertRectToView(null, rect, rect);
		    rootView.redraw(rect);
		}
		Rect.returnRect(rect);
	    }
	}
    }
    
    private void moveByAndSizeBy(int i, int i_35_, int i_36_, int i_37_) {
	if (i != 0 || i_35_ != 0 || i_36_ != 0 || i_37_ != 0) {
	    Size size = Size.newSize(i_36_, i_37_);
	    _checkSize(size);
	    i_36_ = size.width;
	    i_37_ = size.height;
	    Size.returnSize(size);
	    Point point = Point.newPoint(bounds.x + i, bounds.y + i_35_);
	    willMoveTo(point);
	    i = point.x - bounds.x;
	    i_35_ = point.y - bounds.y;
	    Point.returnPoint(point);
	    if (!isVisible()) {
		_super_moveBy(i, i_35_);
		_super_sizeBy(i_36_, i_37_);
		layoutParts();
	    } else {
		Rect rect = Rect.newRect(bounds);
		_super_moveBy(i, i_35_);
		_super_sizeBy(i_36_, i_37_);
		if (i_36_ != 0 || i_37_ != 0)
		    layoutParts();
		rect.unionWith(bounds);
		if (this.superview() != rootView)
		    this.superview().convertRectToView(rootView, rect, rect);
		if (isTransparent()) {
		    this.disableDrawing();
		    rootView.redraw(rect);
		    this.reenableDrawing();
		    this.draw();
		} else
		    rootView.redraw(rect);
		Rect.returnRect(rect);
	    }
	}
    }
    
    private void moveToAndSizeTo(int i, int i_38_, int i_39_, int i_40_) {
	moveByAndSizeBy(i - bounds.x, i_38_ - bounds.y, i_39_ - bounds.width,
			i_40_ - bounds.height);
    }
    
    public void setScrollsToVisible(boolean bool) {
	scrollToVisible = bool;
    }
    
    public boolean scrollsToVisible() {
	return scrollToVisible;
    }
    
    public void scrollRectToVisible(Rect rect) {
	int i = 0;
	int i_41_ = 0;
	if (scrollToVisible) {
	    Rect rect_42_ = Rect.newRect();
	    this.computeVisibleRect(rect_42_);
	    if (rect_42_.width == bounds.width
		&& rect_42_.height == bounds.height)
		Rect.returnRect(rect_42_);
	    else {
		if (!rect_42_.contains(rect)) {
		    this.convertRectToView(null, rect_42_, rect_42_);
		    Rect rect_43_ = Rect.newRect();
		    this.convertRectToView(null, rect, rect_43_);
		    Rect rect_44_
			= Rect.newRect(0, 0, this.width(), this.height());
		    this.convertRectToView(null, rect_44_, rect_44_);
		    if (rect_43_.x < rect_42_.x && rect_42_.x > rect_44_.x)
			i = rect_42_.x - rect_43_.x;
		    else if (rect_43_.maxX() > rect_42_.maxX()
			     && rect_42_.maxX() < rect_44_.maxX())
			i = rect_42_.maxX() - rect_43_.maxX();
		    if (rect_43_.y < rect_42_.y && rect_42_.y > rect_44_.y)
			i_41_ = rect_42_.y - rect_43_.y;
		    else if (rect_43_.maxY() > rect_42_.maxY()
			     && rect_42_.maxY() < rect_44_.maxY())
			i_41_ = rect_42_.maxY() - rect_43_.maxY();
		    Rect rect_45_ = this.rootView().bounds;
		    if (i > 0 && rect_44_.x + i > 3)
			i = 3 - rect_44_.x;
		    else if (i < 0
			     && rect_44_.maxX() + i < rect_45_.maxX() - 3)
			i = rect_45_.maxX() - 3 - rect_44_.maxX();
		    if (i_41_ > 0 && rect_44_.y + i_41_ > 3)
			i_41_ = 3 - rect_44_.y;
		    else if (i_41_ < 0
			     && rect_44_.maxY() + i_41_ < rect_45_.maxY() - 3)
			i_41_ = rect_45_.maxY() - 3 - rect_44_.maxY();
		    this.moveBy(i, i_41_);
		    Rect.returnRect(rect_43_);
		    Rect.returnRect(rect_44_);
		}
		Rect.returnRect(rect_42_);
	    }
	}
    }
    
    public void subviewDidResize() {
	/* empty */
    }
    
    public void center() {
	Rect rect = rootView.bounds;
	Rect rect_46_ = new Rect((rect.width - bounds.width) / 2,
				 (rect.height - bounds.height) / 2,
				 bounds.width, bounds.height);
	if (rect_46_.y < 0)
	    rect_46_.y = 0;
	this.setBounds(rect_46_);
    }
    
    void mouseResizeDrag(MouseEvent mouseevent) {
	boolean bool = false;
	boolean bool_47_ = false;
	mouseevent.x += bounds.x;
	mouseevent.y += bounds.y;
	if (_resizePart == 2)
	    mouseevent.x = _lastX;
	Rect rect = Rect.newRect(bounds);
	if (_resizePart == 1) {
	    rect.moveBy(mouseevent.x - _lastX, 0);
	    rect.sizeBy(_lastX - mouseevent.x, mouseevent.y - _lastY);
	} else
	    rect.sizeBy(mouseevent.x - _lastX, mouseevent.y - _lastY);
	if (rect.height > this.superview().height() - rect.y)
	    rect.sizeBy(0, this.superview().height() - rect.height - rect.y);
	Size size = Size.newSize(rect.width - bounds.width,
				 rect.height - bounds.height);
	_checkSize(size);
	if (_resizePart == 1) {
	    if (rect.x > bounds.x + bounds.width)
		rect.moveBy(bounds.x - rect.x - size.width, 0);
	    else
		rect.moveBy(rect.width - bounds.width - size.width, 0);
	}
	rect.sizeBy(size.width - (rect.width - bounds.width),
		    size.height - (rect.height - bounds.height));
	Size.returnSize(size);
	InternalWindow internalwindow_48_
	    = (InternalWindow) _resizeWindowVector.elementAt(0);
	int i = rect.height - internalwindow_48_.bounds.height;
	if (_resizePart == 1) {
	    if (i < 0) {
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(1);
		internalwindow_48_.sizeTo(1, rect.height);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(2);
		internalwindow_48_.moveToAndSizeTo(rect.x + 1, rect.y,
						   rect.width - 2, 1);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(3);
		internalwindow_48_.moveToAndSizeTo(rect.x + 1, rect.maxY() - 1,
						   rect.width - 2, 1);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(0);
		internalwindow_48_.moveToAndSizeTo(rect.x, rect.y, 1,
						   rect.height);
	    } else {
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(0);
		internalwindow_48_.moveToAndSizeTo(rect.x, rect.y, 1,
						   rect.height);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(3);
		internalwindow_48_.moveToAndSizeTo(rect.x + 1, rect.maxY() - 1,
						   rect.width - 2, 1);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(2);
		internalwindow_48_.moveToAndSizeTo(rect.x + 1, rect.y,
						   rect.width - 2, 1);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(1);
		internalwindow_48_.sizeTo(1, rect.height);
	    }
	} else if (_resizePart == 2) {
	    if (i < 0) {
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(1);
		internalwindow_48_.sizeTo(1, rect.height);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(0);
		internalwindow_48_.sizeTo(1, rect.height);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(3);
		internalwindow_48_.moveToAndSizeTo(rect.x + 1, rect.maxY() - 1,
						   (internalwindow_48_.bounds
						    .width),
						   (internalwindow_48_.bounds
						    .height));
	    } else {
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(3);
		internalwindow_48_.moveToAndSizeTo(rect.x + 1, rect.maxY() - 1,
						   (internalwindow_48_.bounds
						    .width),
						   (internalwindow_48_.bounds
						    .height));
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(1);
		internalwindow_48_.sizeTo(1, rect.height);
		internalwindow_48_
		    = (InternalWindow) _resizeWindowVector.elementAt(0);
		internalwindow_48_.sizeTo(1, rect.height);
	    }
	} else if (i < 0) {
	    internalwindow_48_
		= (InternalWindow) _resizeWindowVector.elementAt(0);
	    internalwindow_48_.sizeTo(1, rect.height);
	    internalwindow_48_
		= (InternalWindow) _resizeWindowVector.elementAt(2);
	    internalwindow_48_.sizeTo(rect.width - 2, 1);
	    internalwindow_48_
		= (InternalWindow) _resizeWindowVector.elementAt(3);
	    internalwindow_48_.moveToAndSizeTo(rect.x + 1, rect.maxY() - 1,
					       rect.width - 2, 1);
	    internalwindow_48_
		= (InternalWindow) _resizeWindowVector.elementAt(1);
	    internalwindow_48_.moveToAndSizeTo(rect.maxX() - 1, rect.y, 1,
					       rect.height);
	} else {
	    internalwindow_48_
		= (InternalWindow) _resizeWindowVector.elementAt(1);
	    internalwindow_48_.moveToAndSizeTo(rect.maxX() - 1, rect.y, 1,
					       rect.height);
	    internalwindow_48_
		= (InternalWindow) _resizeWindowVector.elementAt(3);
	    internalwindow_48_.moveToAndSizeTo(rect.x + 1, rect.maxY() - 1,
					       rect.width - 2, 1);
	    internalwindow_48_
		= (InternalWindow) _resizeWindowVector.elementAt(2);
	    internalwindow_48_.sizeTo(rect.width - 2, 1);
	    internalwindow_48_
		= (InternalWindow) _resizeWindowVector.elementAt(0);
	    internalwindow_48_.sizeTo(1, rect.height);
	}
	Rect.returnRect(rect);
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	if (_resizePart != 0)
	    mouseResizeDrag(mouseevent);
	else {
	    int i = mouseevent.x + bounds.x;
	    int i_49_ = mouseevent.y + bounds.y;
	    int i_50_;
	    if (_type == 0)
		i_50_ = -5;
	    else
		i_50_ = -titleBarMargin() + 5;
	    int i_51_ = this.superview().height() - 5;
	    int i_52_ = bounds.y + i_49_ - _lastY;
	    if (i_52_ < i_50_)
		i_49_ = i_50_ + _lastY - bounds.y;
	    else if (i_52_ > i_51_)
		i_49_ = i_51_ + _lastY - bounds.y;
	    int i_53_ = -bounds.width + 5;
	    int i_54_ = this.superview().width() - 5;
	    int i_55_ = bounds.x + i - _lastX;
	    if (i_55_ < i_53_)
		i = i_53_ + _lastX - bounds.x;
	    else if (i_55_ > i_54_)
		i = i_54_ + _lastX - bounds.x;
	    Point point = Point.newPoint(bounds.x + i - _lastX,
					 bounds.y + i_49_ - _lastY);
	    willMoveTo(point);
	    i = point.x + _lastX - bounds.x;
	    i_49_ = point.y + _lastY - bounds.y;
	    Point.returnPoint(point);
	    Rect rect = Rect.newRect();
	    this.computeVisibleRect(rect);
	    Rect rect_56_ = Rect.newRect();
	    this.convertRectToView(null, rect, rect_56_);
	    if (!isTransparent() && drawingBuffer == null) {
		_createdDrawingBuffer = true;
		this.setBuffered(true);
		_drawToBackingStore = true;
		Graphics graphics = drawingBuffer.createGraphics();
		graphics.setDebugOptions(this.shouldDebugGraphics());
		draw(graphics, null);
		graphics.dispose();
		Object object = null;
		_drawToBackingStore = false;
	    }
	    _super_moveBy(i - _lastX, i_49_ - _lastY);
	    if (!isTransparent()) {
		if (drawingBuffer != null)
		    drawingBufferIsBitCache = true;
		this.draw();
		if (drawingBuffer != null)
		    drawingBufferIsBitCache = false;
	    }
	    if (isTransparent())
		this.draw();
	    rootView.disableWindowsAbove(this, true);
	    rootView.redraw(rect_56_);
	    rootView.disableWindowsAbove(this, false);
	    _lastX = i;
	    _lastY = i_49_;
	    rect_56_.unionWith(this.superview().convertRectToView(null,
								  bounds));
	    rootView.redrawTransparentWindows(rect_56_, this);
	    Rect.returnRect(rect);
	}
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	if (_resizePart != 0) {
	    InternalWindow internalwindow_57_
		= (InternalWindow) _resizeWindowVector.elementAt(0);
	    InternalWindow internalwindow_58_
		= (InternalWindow) _resizeWindowVector.elementAt(1);
	    if (_resizePart == 1)
		moveByAndSizeBy(internalwindow_57_.bounds.x - bounds.x, 0,
				(internalwindow_58_.bounds.x
				 - internalwindow_57_.bounds.x - bounds.width
				 + 1),
				(internalwindow_57_.bounds.height
				 - bounds.height));
	    else
		this.sizeTo((internalwindow_58_.bounds.x
			     - internalwindow_57_.bounds.x + 1),
			    internalwindow_57_.bounds.height);
	    int i = _resizeWindowVector.count();
	    while (i-- > 0) {
		internalwindow_57_
		    = (InternalWindow) _resizeWindowVector.elementAt(i);
		internalwindow_57_.hide();
	    }
	    _resizeWindowVector.removeAllElements();
	    _resizePart = 0;
	}
	if (_createdDrawingBuffer) {
	    this.setBuffered(false);
	    _createdDrawingBuffer = false;
	}
    }
    
    public boolean isResizing() {
	return _resizePart != 0;
    }
    
    public int resizePart() {
	return _resizePart;
    }
    
    void _drawLine(Graphics graphics, int i, int i_59_, int i_60_, Color color,
		   Color color_61_) {
	graphics.setColor(color);
	for (int i_62_ = i; i_62_ <= i_59_; i_62_ += 2)
	    graphics.drawLine(i_62_, i_60_, i_62_, i_60_);
	graphics.setColor(color_61_);
	for (int i_63_ = i + 1; i_63_ <= i_59_; i_63_ += 2)
	    graphics.drawLine(i_63_, i_60_, i_63_, i_60_);
    }
    
    public void drawView(Graphics graphics) {
	if ((drawingBuffer == null || graphics.isDrawingBuffer())
	    && (_type != 0 || _border != null && _blankBorderOverride))
	    _border.drawInRect(graphics, 0, 0, bounds.width, bounds.height);
    }
    
    public void drawTitleBar() {
	if (_type != 0 || _border != null && _blankBorderOverride) {
	    Rect rect = Rect.newRect(0, 0, bounds.width, titleBarMargin());
	    this.draw(rect);
	    Rect.returnRect(rect);
	}
    }
    
    public void drawBottomBorder() {
	if (_type != 0 || _border != null && _blankBorderOverride) {
	    Rect rect = Rect.newRect(0, bounds.height - bottomBorderMargin(),
				     bounds.width, bottomBorderMargin());
	    this.draw(rect);
	    Rect.returnRect(rect);
	}
    }
    
    public void draw(Graphics graphics, Rect rect) {
	if (isTransparent()
	    && (graphics == null || !graphics.isDrawingBuffer())
	    && rootView._redrawTransWindows) {
	    View view = null;
	    if (view == null || view.isTransparent())
		updateDrawingBuffer();
	}
	super.draw(graphics, rect);
    }
    
    public void didBecomeMain() {
	drawTitleBar();
	if (_owner != null)
	    _owner.windowDidBecomeMain(this);
	rootView.setFocusedView(focusedView(), false);
	if (containsDocument())
	    Application.application().makeCurrentDocumentWindow(this);
    }
    
    public void didResignMain() {
	drawTitleBar();
	if (_owner != null)
	    _owner.windowDidResignMain(this);
	if (rootView != null)
	    rootView.setFocusedView(null, false);
    }
    
    public void setContainsDocument(boolean bool) {
	_containsDocument = bool;
    }
    
    public boolean containsDocument() {
	return _containsDocument;
    }
    
    public void setCanBecomeDocument(boolean bool) {
	setContainsDocument(bool);
    }
    
    public boolean canBecomeDocument() {
	return containsDocument();
    }
    
    public void didBecomeCurrentDocument() {
	/* empty */
    }
    
    public void didResignCurrentDocument() {
	/* empty */
    }
    
    public boolean isCurrentDocument() {
	if (Application.application().currentDocumentWindow() == this)
	    return true;
	return false;
    }
    
    public void setFocusedView(View view) {
	_focusedView = view;
	if (rootView != null) {
	    if (rootView.mainWindow() == this && view != null)
		rootView.makeWindowVisible(this, 0, null);
	    rootView.setFocusedView(focusedView());
	}
    }
    
    public View focusedView() {
	if (_focusedView != null && _focusedView.descendsFrom(this))
	    return _focusedView;
	_focusedView = null;
	return _focusedView;
    }
    
    public String toString() {
	if (_title != null)
	    return "InternalWindow (" + _title + ")";
	return super.toString();
    }
    
    View ancestorWithDrawingBuffer() {
	if (drawingBuffer != null)
	    return this;
	return null;
    }
    
    public InternalWindow window() {
	return this;
    }
    
    public Font font() {
	return _titleFont;
    }
    
    public void performCommand(String string, Object object) {
	if ("show".equals(string))
	    show();
	else if ("hide".equals(string))
	    hide();
	else
	    throw new NoSuchMethodError("unknown command: " + string);
    }
    
    public boolean hidesSubviewsFromKeyboard() {
	return true;
    }
    
    public void setDefaultSelectedView(View view) {
	_defaultSelectedView = view;
    }
    
    public View defaultSelectedView() {
	return _defaultSelectedView;
    }
    
    public boolean canBecomeSelectedView() {
	if (isMain())
	    return true;
	return false;
    }
    
    boolean wantsKeyboardArrow() {
	return false;
    }
}
