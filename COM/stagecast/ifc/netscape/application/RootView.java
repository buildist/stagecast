/* RootView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class RootView extends View implements EventProcessor, ExtendedTarget
{
    Color _backgroundColor;
    Image _image;
    FoundationPanel panel;
    Application application;
    Timer _autoscrollTimer;
    ColorChooser colorChooser;
    FontChooser fontChooser;
    InternalWindow _mainWindow;
    View _mouseView;
    View _moveView;
    View _focusedView;
    View _windowClipView;
    View _mouseClickView;
    View _rootViewFocusedView;
    View _selectedView;
    View _defaultSelectedView;
    Vector windows = new Vector();
    long _lastClickTime;
    int _clickCount;
    int _mouseX;
    int _mouseY;
    int _currentCursor;
    int _viewCursor;
    int _overrideCursor = -1;
    int mouseDownCount;
    int _imageDisplayStyle;
    Vector dirtyViews = new Vector();
    boolean _redrawTransWindows = true;
    boolean recomputeCursor;
    boolean recomputeMoveView;
    boolean isVisible;
    boolean redrawAll = true;
    Vector componentViews;
    MouseFilter mouseFilter = new MouseFilter();
    static Vector _commands = new Vector();
    static final String VALIDATE_SELECTED_VIEW = "validateSelectedView";
    
    static {
	_commands.addElement("showFontChooser");
	_commands.addElement("showColorChooser");
	_commands.addElement("newFontSelection");
	_commands.addElement("validateSelectedView");
    }
    
    public RootView() {
	this(0, 0, 0, 0);
    }
    
    public RootView(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public RootView(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	_backgroundColor = Color.gray;
	application = Application.application();
	_defaultSelectedView = this;
    }
    
    void addWindowRelativeTo(InternalWindow internalwindow, int i,
			     InternalWindow internalwindow_3_) {
	if (internalwindow != null) {
	    if (_windowClipView != null && internalwindow.layer() < 500)
		_windowClipView.addSubview(internalwindow);
	    else
		this.addSubview(internalwindow);
	    makeWindowVisible(internalwindow, i, internalwindow_3_);
	}
    }
    
    void removeWindow(InternalWindow internalwindow) {
	if (internalwindow != null) {
	    Rect rect = absoluteWindowBounds(internalwindow);
	    internalwindow.removeFromSuperview();
	    int i = windows.indexOf(internalwindow) - 1;
	    if (i < 0)
		i = windows.indexOf(internalwindow);
	    windows.removeElement(internalwindow);
	    if (internalwindow == _mainWindow) {
		InternalWindow internalwindow_4_ = null;
		for (i = windows.count() - 1; i >= 0; i--) {
		    if (((InternalWindow) windows.elementAt(i))
			    .canBecomeMain()) {
			internalwindow_4_
			    = (InternalWindow) windows.elementAt(i);
			break;
		    }
		}
		_setMainWindow(internalwindow_4_);
	    }
	    if (canDraw()) {
		redraw(rect);
		redrawTransparentWindows(rect, null);
	    }
	    if (!(internalwindow instanceof KeyboardArrow))
		validateSelectedView();
	    createMouseEnterLater();
	}
    }
    
    public Vector internalWindows() {
	return windows;
    }
    
    public InternalWindow mainWindow() {
	return _mainWindow;
    }
    
    void _setMainWindow(InternalWindow internalwindow) {
	if (_mainWindow != internalwindow
	    && (internalwindow == null || internalwindow.canBecomeMain())) {
	    InternalWindow internalwindow_5_ = _mainWindow;
	    _mainWindow = internalwindow;
	    if (internalwindow_5_ != null)
		internalwindow_5_.didResignMain();
	    else if (_focusedView != null && !isInWindow(_focusedView)) {
		_rootViewFocusedView = _focusedView;
		setFocusedView(null, false);
	    }
	    if (_mainWindow != null)
		_mainWindow.didBecomeMain();
	    else if (rootViewFocusedView() != null) {
		setFocusedView(rootViewFocusedView(), false);
		_rootViewFocusedView = null;
	    }
	    validateSelectedView();
	    createMouseEnterLater();
	}
    }
    
    InternalWindow frontWindowWithLayer(int i) {
	for (int i_6_ = windows.count() - 1; i_6_ >= 0; i_6_--) {
	    InternalWindow internalwindow
		= (InternalWindow) windows.elementAt(i_6_);
	    if (internalwindow.layer() == i)
		return internalwindow;
	}
	return null;
    }
    
    InternalWindow backWindowWithLayer(int i) {
	int i_7_ = windows.count();
	for (int i_8_ = 0; i_8_ < i_7_; i_8_++) {
	    InternalWindow internalwindow
		= (InternalWindow) windows.elementAt(i_8_);
	    if (internalwindow.layer() == i)
		return internalwindow;
	}
	return null;
    }
    
    void makeWindowVisible(InternalWindow internalwindow, int i,
			   InternalWindow internalwindow_9_) {
	InternalWindow internalwindow_10_ = null;
	if (internalwindow != null && internalwindow.descendsFrom(this)) {
	    int i_11_ = windows.indexOf(internalwindow);
	    int i_12_ = internalwindow.layer();
	    if (internalwindow_9_ != null) {
		if (internalwindow_9_.layer() > i_12_) {
		    internalwindow_9_ = null;
		    i = 0;
		} else if (internalwindow_9_.layer() < i_12_) {
		    internalwindow_9_ = null;
		    i = 1;
		}
	    }
	    windows.removeElement(internalwindow);
	    if (internalwindow_9_ != null) {
		boolean bool;
		if (i == 0)
		    bool = windows.insertElementAfter(internalwindow,
						      internalwindow_9_);
		else
		    bool = windows.insertElementBefore(internalwindow,
						       internalwindow_9_);
		if (bool) {
		    _setMainWindow(internalwindow);
		    if (i_11_ != windows.indexOf(internalwindow)) {
			internalwindow.draw();
			updateTransWindows(internalwindow);
			if (i == 1)
			    updateWindowsAbove(internalwindow);
		    }
		    return;
		}
		Object object = null;
	    }
	    int i_13_ = windows.count();
	    while (i_13_-- > 0) {
		internalwindow_10_ = (InternalWindow) windows.elementAt(i_13_);
		if (internalwindow_10_.layer() <= i_12_
		    && internalwindow_10_.layer() <= i_12_)
		    break;
	    }
	    boolean bool;
	    if (internalwindow_10_ == null) {
		windows.insertElementAt(internalwindow, 0);
		bool = true;
	    } else if (internalwindow_10_.layer() > i_12_)
		bool = windows.insertElementBefore(internalwindow,
						   internalwindow_10_);
	    else
		bool = windows.insertElementAfter(internalwindow,
						  internalwindow_10_);
	    if (!bool)
		windows.insertElementAt(internalwindow, 0);
	    _setMainWindow(internalwindow);
	    if (i_11_ != windows.indexOf(internalwindow)) {
		internalwindow.draw();
		updateTransWindows(internalwindow);
	    }
	}
    }
    
    void updateWindowsAbove(InternalWindow internalwindow) {
	Rect rect = absoluteWindowBounds(internalwindow);
	int i = windows.indexOf(internalwindow);
	Rect rect_14_ = new Rect();
	int i_15_ = i + 1;
	for (int i_16_ = windows.count(); i_15_ < i_16_; i_15_++) {
	    InternalWindow internalwindow_17_
		= (InternalWindow) windows.elementAt(i_15_);
	    Rect rect_18_ = absoluteWindowBounds(internalwindow_17_);
	    if (rect.intersects(rect_18_)) {
		rect_14_.setBounds(rect);
		rect_14_.intersectWith(rect_18_);
		this.convertRectToView(internalwindow_17_, rect_14_, rect_14_);
		internalwindow_17_.addDirtyRect(rect_14_);
	    }
	}
    }
    
    void updateTransWindows(InternalWindow internalwindow) {
	if (internalwindow != null) {
	    int i = windows.indexOf(internalwindow);
	    Rect rect = internalwindow.superview()
			    .convertRectToView(this, internalwindow.bounds);
	    for (int i_19_ = 0; i_19_ < i; i_19_++) {
		InternalWindow internalwindow_20_
		    = (InternalWindow) windows.elementAt(i_19_);
		if (internalwindow_20_.isTransparent()) {
		    Rect rect_21_ = absoluteWindowBounds(internalwindow_20_);
		    if (rect_21_.intersects(rect) || rect.intersects(rect_21_))
			internalwindow_20_.updateDrawingBuffer();
		}
	    }
	}
    }
    
    void disableWindowsAbove(InternalWindow internalwindow, boolean bool) {
	if (internalwindow != null) {
	    int i = windows.indexOf(internalwindow);
	    if (i != -1) {
		for (int i_22_ = windows.count(); i < i_22_; i++) {
		    InternalWindow internalwindow_23_
			= (InternalWindow) windows.elementAt(i);
		    if (bool)
			internalwindow_23_.disableDrawing();
		    else
			internalwindow_23_.reenableDrawing();
		}
	    }
	}
    }
    
    Vector windowRects(Rect rect, InternalWindow internalwindow) {
	Vector vector = null;
	int i = windows.count();
	int i_24_;
	if (internalwindow != null)
	    i_24_ = windows.indexOf(internalwindow) + 1;
	else
	    i_24_ = 0;
	for (/**/; i_24_ < i; i_24_++) {
	    InternalWindow internalwindow_25_
		= (InternalWindow) windows.elementAt(i_24_);
	    Rect rect_26_ = absoluteWindowBounds(internalwindow_25_);
	    if (rect_26_.intersects(rect) || rect.intersects(rect_26_)) {
		if (vector == null)
		    vector = VectorCache.newVector();
		vector.addElement(new Rect(rect_26_));
	    }
	}
	return vector;
    }
    
    void setRedrawTransparentWindows(boolean bool) {
	_redrawTransWindows = bool;
    }
    
    public void redrawTransparentWindows(Rect rect,
					 InternalWindow internalwindow) {
	redrawTransparentWindows(null, rect, internalwindow);
    }
    
    public void redrawTransparentWindows(Graphics graphics, Rect rect,
					 InternalWindow internalwindow) {
	Rect rect_27_ = null;
	if (_redrawTransWindows) {
	    int i = windows.count();
	    int i_28_;
	    if (internalwindow != null)
		i_28_ = windows.indexOf(internalwindow) + 1;
	    else
		i_28_ = 0;
	    for (/**/; i_28_ < i; i_28_++) {
		InternalWindow internalwindow_29_
		    = (InternalWindow) windows.elementAt(i_28_);
		if (internalwindow_29_.isTransparent()) {
		    Rect rect_30_ = absoluteWindowBounds(internalwindow_29_);
		    if (rect_30_.intersects(rect)) {
			if (rect_27_ == null)
			    rect_27_ = Rect.newRect();
			this.convertRectToView(internalwindow_29_, rect,
					       rect_27_);
			internalwindow_29_.draw(graphics, rect_27_);
		    }
		}
	    }
	    if (rect_27_ != null)
		Rect.returnRect(rect_27_);
	}
    }
    
    void paint(ApplicationEvent applicationevent) {
	UpdateFilter updatefilter = new UpdateFilter(applicationevent.rect());
	updatefilter.rootView = this;
	try {
	    Thread.sleep(100L);
	} catch (InterruptedException interruptedexception) {
	    /* empty */
	}
	application.eventLoop().filterEvents(updatefilter);
	if (redrawAll) {
	    Rect rect = Rect.newRect(0, 0, bounds.width, bounds.height);
	    redraw(rect);
	    redrawTransparentWindows(rect, null);
	    Rect.returnRect(rect);
	} else {
	    redraw(updatefilter._rect);
	    redrawTransparentWindows(updatefilter._rect, null);
	}
	AWTCompatibility.awtToolkit().sync();
    }
    
    void print(ApplicationEvent applicationevent) {
	Rect rect = new Rect(0, 0, this.width(), this.height());
	Graphics graphics = new Graphics(rect, applicationevent.graphics());
	redraw(graphics, rect);
	redrawTransparentWindows(graphics, rect, null);
    }
    
    void resize(ApplicationEvent applicationevent) {
	ResizeFilter resizefilter = new ResizeFilter();
	try {
	    Thread.sleep(50L);
	} catch (InterruptedException interruptedexception) {
	    /* empty */
	}
	resizefilter.lastEvent = applicationevent;
	application.eventLoop().filterEvents(resizefilter);
	this.sizeTo(resizefilter.lastEvent.rect().width,
		    resizefilter.lastEvent.rect().height);
    }
    
    public void setMouseView(View view) {
	_mouseView = view;
    }
    
    public View mouseView() {
	return _mouseView;
    }
    
    public View viewForMouse(int i, int i_31_) {
	View view = null;
	int i_32_ = windows.count();
	InternalWindow internalwindow;
	Rect rect;
	for (/**/; i_32_-- > 0 && view == null;
	     view = internalwindow.viewForMouse(i - rect.x, i_31_ - rect.y)) {
	    internalwindow = (InternalWindow) windows.elementAt(i_32_);
	    rect = absoluteWindowBounds(internalwindow);
	}
	if (view != null)
	    return view;
	return super.viewForMouse(i, i_31_);
    }
    
    void __mouseDown(MouseEvent mouseevent) {
	View view = _mouseView;
	View view_33_ = _focusedView;
	_mouseDown(mouseevent);
	View view_34_ = _mouseView;
	View view_35_ = _focusedView;
	if (application.modalView() == null
	    && (view_33_ instanceof TextView || view_33_ instanceof TextField)
	    && view_34_ != view && view_33_ == view_35_)
	    setFocusedView(view_34_);
    }
    
    void _mouseDown(MouseEvent mouseevent) {
	mouseDownCount++;
	if (mouseDownCount <= 1) {
	    View view = viewForMouse(mouseevent.x, mouseevent.y);
	    if (view == null)
		_mouseView = null;
	    else {
		long l = mouseevent.timeStamp;
		if (_mouseClickView == view && l - _lastClickTime < 250L)
		    _clickCount++;
		else
		    _clickCount = 1;
		_lastClickTime = l;
		mouseevent.setClickCount(_clickCount);
		_mouseView = _mouseClickView = view;
		if (!viewExcludedFromModalSession(view)) {
		    if (!(_mouseView instanceof InternalWindow)) {
			InternalWindow internalwindow = _mouseView.window();
			if (!(_mouseView instanceof MenuView)
			    || internalwindow != null)
			    _setMainWindow(internalwindow);
		    }
		    mouseevent.x -= _mouseView.absoluteX();
		    mouseevent.y -= _mouseView.absoluteY();
		    View view_36_ = _mouseView;
		    if (!_mouseView.mouseDown(mouseevent)
			&& view_36_ == _mouseView)
			_mouseView = null;
		}
	    }
	}
    }
    
    public boolean isVisible() {
	return isVisible;
    }
    
    void setVisible(boolean bool) {
	if (isVisible != bool) {
	    isVisible = bool;
	    if (isVisible) {
		this.ancestorWasAddedToViewHierarchy(this);
		if (_focusedView != null) {
		    _focusedView._startFocus();
		    Application.application().focusChanged(_focusedView);
		}
	    } else {
		this.ancestorWillRemoveFromViewHierarchy(this);
		if (_focusedView != null)
		    _focusedView._pauseFocus();
	    }
	}
    }
    
    void _mouseDrag(MouseEvent mouseevent) {
	if (!viewExcludedFromModalSession(_mouseView)) {
	    if (_mouseView != null) {
		boolean bool
		    = _mouseView.containsPointInVisibleRect(mouseevent.x,
							    mouseevent.y);
		if (_mouseView.wantsAutoscrollEvents() && !bool) {
		    if (_autoscrollTimer == null) {
			Autoscroller autoscroller = new Autoscroller();
			_autoscrollTimer
			    = new Timer(autoscroller, "autoscroll", 100);
			_autoscrollTimer.start();
			_autoscrollTimer.setData(_mouseView);
			autoscroller.setEvent
			    (_mouseView.convertEventToView(null, mouseevent));
		    } else {
			Autoscroller autoscroller
			    = (Autoscroller) _autoscrollTimer.target();
			autoscroller.setEvent
			    (_mouseView.convertEventToView(null, mouseevent));
		    }
		} else {
		    if (_autoscrollTimer != null) {
			_autoscrollTimer.stop();
			_autoscrollTimer = null;
		    }
		    mouseevent.setClickCount(_clickCount);
		    _mouseView.mouseDragged(mouseevent);
		}
	    } else if (_autoscrollTimer != null) {
		_autoscrollTimer.stop();
		_autoscrollTimer = null;
	    }
	}
    }
    
    void _mouseUp(MouseEvent mouseevent) {
	mouseDownCount--;
	if (mouseDownCount <= 0 && !viewExcludedFromModalSession(_mouseView)) {
	    if (_mouseView != null) {
		mouseevent.setClickCount(_clickCount);
		_mouseView.mouseUp(mouseevent);
	    }
	    if (_autoscrollTimer != null) {
		_autoscrollTimer.stop();
		_autoscrollTimer = null;
	    }
	    View view = viewForMouse(_mouseX, _mouseY);
	    if (view != _moveView)
		createMouseEnter();
	    _mouseClickView = _mouseView;
	    _mouseView = null;
	}
    }
    
    void createMouseEnter() {
	MouseEvent mouseevent = new MouseEvent(System.currentTimeMillis(), -4,
					       _mouseX, _mouseY, 0);
	_mouseEnter(mouseevent);
    }
    
    void createMouseEnterLater() {
	if (_mouseView == null)
	    recomputeMoveView = true;
    }
    
    void _mouseEnter(MouseEvent mouseevent) {
	_mouseMove(mouseevent);
    }
    
    void _mouseMove(MouseEvent mouseevent) {
	View view = viewForMouse(mouseevent.x, mouseevent.y);
	if (!viewExcludedFromModalSession(view)) {
	    if (view == _moveView) {
		if (_moveView == null)
		    return;
		MouseEvent mouseevent_37_
		    = this.convertEventToView(_moveView, mouseevent);
		_moveView.mouseMoved(mouseevent_37_);
	    } else {
		View view_38_ = _moveView;
		_moveView = view;
		if (view_38_ != null) {
		    MouseEvent mouseevent_39_
			= this.convertEventToView(view_38_, mouseevent);
		    mouseevent_39_.setType(-6);
		    view_38_.mouseExited(mouseevent_39_);
		}
		if (_moveView != null) {
		    MouseEvent mouseevent_40_
			= this.convertEventToView(_moveView, mouseevent);
		    mouseevent_40_.setType(-4);
		    _moveView.mouseEntered(mouseevent_40_);
		}
	    }
	    updateCursorLater();
	}
    }
    
    void _mouseExit(MouseEvent mouseevent) {
	if (_moveView != null) {
	    _moveView.mouseExited(this.convertEventToView(_moveView,
							  mouseevent));
	    _moveView = null;
	    updateCursorLater();
	}
    }
    
    void flushCursor() {
	int i;
	if (_overrideCursor != -1)
	    i = _overrideCursor;
	else
	    i = _viewCursor;
	if (i != _currentCursor) {
	    panel.setCursor(i);
	    _currentCursor = i;
	}
    }
    
    void computeCursor() {
	View view = viewForMouse(_mouseX, _mouseY);
	if (view != null) {
	    Point point = Point.newPoint();
	    this.convertToView(view, _mouseX, _mouseY, point);
	    _viewCursor = view.cursorForPoint(point.x, point.y);
	    Point.returnPoint(point);
	} else
	    _viewCursor = 0;
	flushCursor();
    }
    
    public int cursor() {
	return _currentCursor;
    }
    
    public void setOverrideCursor(int i) {
	if (i < -1 || i > 13)
	    throw new InconsistencyException("Unknown cursor type: " + i);
	if (_overrideCursor != i) {
	    _overrideCursor = i;
	    flushCursor();
	}
    }
    
    public void removeOverrideCursor() {
	setOverrideCursor(-1);
    }
    
    public void updateCursor() {
	computeCursor();
    }
    
    public void updateCursorLater() {
	recomputeCursor = true;
    }
    
    void _updateCursorAndMoveView() {
	if (recomputeMoveView) {
	    createMouseEnter();
	    recomputeMoveView = false;
	    recomputeCursor = false;
	} else if (recomputeCursor) {
	    computeCursor();
	    recomputeCursor = false;
	}
    }
    
    void _keyDown(KeyEvent keyevent) {
	boolean bool = false;
	Object object = null;
	View view = _focusedView;
	ExternalWindow externalwindow = externalWindow();
	if (externalwindow != null && externalwindow.menu() != null) {
	    if (JDK11AirLock.menuShortcutExists()) {
		MenuItem menuitem
		    = externalwindow.menu().itemForKeyEvent(keyevent);
		if (menuitem != null)
		    bool = true;
	    } else
		bool = externalwindow.menu().handleCommandKeyEvent(keyevent);
	}
	if (!bool) {
	    if (!processKeyboardEvent(keyevent, true)) {
		if (view != null && !viewExcludedFromModalSession(view))
		    view.keyDown(keyevent);
		else
		    application().keyDown(keyevent);
	    }
	}
    }
    
    void _keyUp(KeyEvent keyevent) {
	View view = _focusedView;
	if (view != null && !viewExcludedFromModalSession(view))
	    view.keyUp(keyevent);
	else
	    application().keyUp(keyevent);
    }
    
    void _keyTyped(KeyEvent keyevent) {
	View view = _focusedView;
	if (view != null && !viewExcludedFromModalSession(view))
	    view.keyTyped(keyevent);
	else
	    application().keyTyped(keyevent);
    }
    
    public void showColorChooser() {
	colorChooser().show();
    }
    
    public ColorChooser colorChooser() {
	if (colorChooser == null) {
	    InternalWindow internalwindow = new InternalWindow(0, 0, 10, 10);
	    internalwindow.setRootView(this);
	    colorChooser = new ColorChooser();
	    colorChooser.setWindow(internalwindow);
	    internalwindow.center();
	}
	return colorChooser;
    }
    
    public void showFontChooser() {
	fontChooser().show();
    }
    
    public FontChooser fontChooser() {
	if (fontChooser == null) {
	    InternalWindow internalwindow = new InternalWindow(0, 0, 1, 1);
	    internalwindow.setRootView(this);
	    fontChooser = new FontChooser();
	    fontChooser.setWindow(internalwindow);
	    internalwindow.center();
	}
	return fontChooser;
    }
    
    public ExternalWindow externalWindow() {
	for (java.awt.Container container = panel; container != null;
	     container = container.getParent()) {
	    if (container instanceof FoundationFrame)
		return ((FoundationFrame) container).externalWindow;
	    if (container instanceof FoundationDialog)
		return ((FoundationDialog) container).externalWindow;
	    if (container instanceof FoundationWindow)
		return ((FoundationWindow) container).externalWindow;
	}
	return null;
    }
    
    MouseEvent removeMouseEvents(MouseEvent mouseevent) {
	MouseEvent mouseevent_41_
	    = (MouseEvent) application.eventLoop().filterEvents(mouseFilter);
	return mouseevent_41_ != null ? mouseevent_41_ : mouseevent;
    }
    
    void _convertMouseEventToMouseView(MouseEvent mouseevent) {
	if (_mouseView != null && mouseevent != null) {
	    Point point = Point.newPoint();
	    this.convertToView(_mouseView, mouseevent.x, mouseevent.y, point);
	    mouseevent.x = point.x;
	    mouseevent.y = point.y;
	    Point.returnPoint(point);
	}
    }
    
    public void processEvent(Event event) {
	if (application != null) {
	    if (event instanceof MouseEvent) {
		MouseEvent mouseevent = (MouseEvent) event;
		int i = mouseevent.type;
		if (i == -5 && mouseDownCount > 0 && _mouseView != null) {
		    MouseEvent mouseevent_42_
			= this.convertEventToView(_mouseView, mouseevent);
		    mouseevent_42_.setType(-3);
		    _mouseUp(mouseevent_42_);
		}
		if (i == -2 && _mouseView != null
		    && _mouseView.wantsMouseEventCoalescing()) {
		    mouseevent = removeMouseEvents(mouseevent);
		    i = mouseevent.type();
		} else if (i == -5 && _moveView != null
			   && _moveView.wantsMouseEventCoalescing()) {
		    mouseevent = removeMouseEvents(mouseevent);
		    i = mouseevent.type();
		}
		_mouseX = mouseevent.x;
		_mouseY = mouseevent.y;
		switch (i) {
		case -1:
		    if (application.firstRootView() != this)
			application.makeFirstRootView(this);
		    __mouseDown(mouseevent);
		    break;
		case -2:
		    _convertMouseEventToMouseView(mouseevent);
		    _mouseDrag(mouseevent);
		    break;
		case -3:
		    _convertMouseEventToMouseView(mouseevent);
		    _mouseUp(mouseevent);
		    break;
		case -4:
		    if (_mouseView == null)
			_mouseEnter(mouseevent);
		    break;
		case -5:
		    _mouseMove(mouseevent);
		    break;
		case -6:
		    if (_mouseView == null)
			_mouseExit(mouseevent);
		    break;
		default:
		    break;
		}
	    } else if (event instanceof KeyEvent) {
		KeyEvent keyevent = (KeyEvent) event;
		if (application.firstRootView() != this)
		    application.makeFirstRootView(this);
		if (keyevent.type == -11)
		    _keyDown(keyevent);
		else if (keyevent.type == -12)
		    _keyUp(keyevent);
		else
		    _keyTyped(keyevent);
	    } else if (event instanceof ApplicationEvent) {
		ExternalWindow externalwindow = null;
		switch (event.type) {
		case -21:
		    application.makeFirstRootView(this);
		    if (externalwindow != null)
			externalwindow.didBecomeMain();
		    if (_focusedView != null)
			_focusedView.resumeFocus();
		    break;
		case -22:
		    externalwindow = externalWindow();
		    if (externalwindow != null)
			externalwindow.didResignMain();
		    if (_mainWindow != null)
			_setMainWindow(null);
		    if (_focusedView != null)
			_focusedView._pauseFocus();
		    break;
		case -23:
		    if (isVisible)
			paint((ApplicationEvent) event);
		    break;
		case -24:
		    if (!this.bounds()
			     .equals(((ApplicationEvent) event).rect())
			&& this.bounds()
			       .contains(((ApplicationEvent) event).rect())) {
			ApplicationEvent applicationevent
			    = new ApplicationEvent();
			applicationevent.data
			    = ((ApplicationEvent) event).rect();
			applicationevent.type = -23;
			applicationevent.setProcessor(this);
			if (application() != null
			    && application().eventLoop() != null)
			    application().eventLoop()
				.addEvent(applicationevent);
		    }
		    resize((ApplicationEvent) event);
		    externalwindow = externalWindow();
		    if (externalwindow != null)
			externalwindow.validateBounds();
		    break;
		case -28:
		    print((ApplicationEvent) event);
		    break;
		}
	    }
	}
    }
    
    void setFocusedView(View view, boolean bool) {
	if (view != _focusedView) {
	    if (_focusedView != null) {
		View view_43_ = _focusedView;
		_focusedView = null;
		if (bool)
		    view_43_._stopFocus();
		else
		    view_43_._pauseFocus();
	    } else
		_focusedView = null;
	    if (_focusedView == null) {
		_focusedView = view;
		if (_focusedView != null)
		    _focusedView._startFocus();
	    }
	    Application.application().focusChanged(_focusedView);
	    Application.application()
		.performCommandLater(this, "validateSelectedView", null, true);
	}
    }
    
    public void setFocusedView(View view) {
	setFocusedView(view, true);
    }
    
    public View focusedView() {
	return _focusedView;
    }
    
    public void performCommand(String string, Object object) {
	if ("showFontChooser".equals(string))
	    showFontChooser();
	else if ("showColorChooser".equals(string))
	    showColorChooser();
	else if ("newFontSelection".equals(string)) {
	    if (fontChooser != null)
		fontChooser.setFont((Font) object);
	} else if ("validateSelectedView".equals(string))
	    validateSelectedView();
	else
	    throw new NoSuchMethodError("unknown command: " + string);
    }
    
    public boolean canPerformCommand(String string) {
	return _commands.contains(string);
    }
    
    public void setColor(Color color) {
	_backgroundColor = color;
    }
    
    public Color color() {
	return _backgroundColor;
    }
    
    public void setImage(Image image) {
	_image = image;
    }
    
    public Image image() {
	return _image;
    }
    
    public void setImageDisplayStyle(int i) {
	if (i != 0 && i != 2 && i != 1)
	    throw new InconsistencyException("Unknown image display style: "
					     + i);
	_imageDisplayStyle = i;
    }
    
    public int imageDisplayStyle() {
	return _imageDisplayStyle;
    }
    
    public boolean isTransparent() {
	return false;
    }
    
    public void drawView(Graphics graphics) {
	if ((_image == null || (_imageDisplayStyle == 0
				&& (_image.width() < bounds.width
				    || _image.height() < bounds.height)))
	    && _backgroundColor != null) {
	    graphics.setColor(_backgroundColor);
	    graphics.fillRect(graphics.clipRect());
	}
	if (_image != null)
	    _image.drawWithStyle(graphics, 0, 0, bounds.width, bounds.height,
				 _imageDisplayStyle);
    }
    
    public void draw(Graphics graphics, Rect rect) {
	Vector vector = new Vector();
	int i = windows.count();
	while (i-- > 0) {
	    InternalWindow internalwindow
		= (InternalWindow) windows.elementAt(i);
	    if (internalwindow.isDrawingEnabled()) {
		internalwindow.disableDrawing();
		vector.addElement(internalwindow);
	    }
	}
	super.draw(graphics, rect);
	i = vector.count();
	while (i-- > 0) {
	    InternalWindow internalwindow
		= (InternalWindow) vector.elementAt(i);
	    internalwindow.reenableDrawing();
	}
    }
    
    View viewWithBuffer(View view, Rect rect) {
	View view_44_ = null;
	int i = view.subviewCount();
	if (i == 0)
	    return null;
	Vector vector = view.subviews();
	Rect rect_45_ = Rect.newRect(0, 0, rect.width, rect.height);
	while (i-- > 0) {
	    View view_46_ = (View) vector.elementAt(i);
	    if (!(view_46_ instanceof InternalWindow)
		&& view_46_.bounds.contains(rect)) {
		if (view_46_.isBuffered()) {
		    Rect.returnRect(rect_45_);
		    return view_46_;
		}
		view_46_.isTransparent();
		rect_45_.x = rect.x - view_46_.bounds.x;
		rect_45_.y = rect.y - view_46_.bounds.y;
		View view_47_ = viewWithBuffer(view_46_, rect_45_);
		if (view_47_ != null) {
		    Rect.returnRect(rect_45_);
		    return view_47_;
		}
		if (view_44_ != null) {
		    Rect.returnRect(rect_45_);
		    return view_44_;
		}
	    }
	}
	Rect.returnRect(rect_45_);
	return null;
    }
    
    void redraw(Graphics graphics, Rect rect) {
	Rect rect_48_ = Rect.newRect(0, 0, rect.width, rect.height);
	if (rect == null)
	    rect = new Rect(0, 0, bounds.width, bounds.height);
	Vector vector = VectorCache.newVector();
	setRedrawTransparentWindows(false);
	int i = windows.count();
	boolean bool = false;
	int i_49_ = i;
	while (i_49_-- > 0 && !bool) {
	    InternalWindow internalwindow
		= (InternalWindow) windows.elementAt(i_49_);
	    Rect rect_50_ = absoluteWindowBounds(internalwindow);
	    if (rect_50_.intersects(rect)) {
		vector.addElement(internalwindow);
		if (!internalwindow.isTransparent() && rect_50_.contains(rect))
		    bool = true;
	    }
	}
	if (!bool) {
	    View view = this._viewForRect(rect, null);
	    if (view != null) {
		this.convertRectToView(view, rect, rect_48_);
		graphics.pushState();
		graphics.translate(rect.x - rect_48_.x, rect.y - rect_48_.y);
		view.draw(graphics, rect_48_);
		graphics.popState();
	    } else
		draw(graphics, rect);
	}
	i = vector.count();
	for (i_49_ = 0; i_49_ < i; i_49_++) {
	    InternalWindow internalwindow
		= (InternalWindow) vector.elementAt(i_49_);
	    View view = internalwindow.superview();
	    this.convertRectToView(view, rect, rect_48_);
	    View view_51_ = internalwindow._viewForRect(rect_48_, view);
	    if (view_51_ == null)
		view_51_ = internalwindow;
	    this.convertRectToView(view_51_, rect, rect_48_);
	    graphics.pushState();
	    graphics.translate(rect.x - rect_48_.x, rect.y - rect_48_.y);
	    view_51_.draw(graphics, rect_48_);
	    graphics.popState();
	}
	setRedrawTransparentWindows(true);
	Rect.returnRect(rect_48_);
	VectorCache.returnVector(vector);
    }
    
    public void redraw(Rect rect) {
	Graphics graphics = this.createGraphics();
	redraw(graphics, rect);
	graphics.dispose();
    }
    
    void redraw() {
	Graphics graphics = this.createGraphics();
	Rect rect = Rect.newRect(0, 0, bounds.width, bounds.height);
	redraw(graphics, rect);
	Rect.returnRect(rect);
	graphics.dispose();
    }
    
    protected synchronized void markDirty(View view) {
	if (dirtyViews != null)
	    dirtyViews.addElement(view);
	else
	    throw new InconsistencyException
		      ("Don't dirty a View while the list of dirty views is being drawn!");
    }
    
    protected synchronized void markClean(View view) {
	if (dirtyViews != null)
	    dirtyViews.removeElement(view);
    }
    
    public synchronized void resetDirtyViews() {
	Vector vector = dirtyViews;
	dirtyViews = null;
	int i = vector.count();
	for (int i_52_ = 0; i_52_ < i; i_52_++) {
	    View view = (View) vector.elementAt(i_52_);
	    view.setDirty(false);
	}
	vector.removeAllElements();
	dirtyViews = vector;
    }
    
    public synchronized void drawDirtyViews() {
	int i = dirtyViews.count();
	if (i != 0) {
	    Vector vector = dirtyViews;
	    try {
		dirtyViews = null;
		Vector vector_53_ = new Vector(i);
		Rect rect = new Rect();
		for (int i_54_ = 0; i_54_ < i; i_54_++) {
		    View view = (View) vector.elementAt(i_54_);
		    collectDirtyViews(view, vector_53_, rect);
		}
		i = vector_53_.count();
		for (int i_55_ = 0; i_55_ < i; i_55_++) {
		    View view = (View) vector_53_.elementAt(i_55_);
		    view.draw(view.dirtyRect);
		}
	    } finally {
		dirtyViews = vector;
		resetDirtyViews();
	    }
	}
    }
    
    void collectDirtyViews(View view, Vector vector, Rect rect) {
	View view_57_;
	View view_56_ = view_57_ = view;
	int i_58_;
	int i = i_58_ = 0;
	int i_60_;
	int i_59_ = i_60_ = 0;
	rect.setBounds(0, 0, view_56_.width(), view_56_.height());
	if (view.dirtyRect != null)
	    rect.intersectWith(view.dirtyRect);
	if (!rect.isEmpty()) {
	    do {
		i += view_56_.bounds.x;
		i_59_ += view_56_.bounds.y;
		rect.moveBy(view_56_.bounds.x, view_56_.bounds.y);
		view_56_ = view_56_.superview();
		if (view_56_ != null) {
		    rect.intersectWith(0, 0, view_56_.width(),
				       view_56_.height());
		    if (rect.isEmpty())
			return;
		    if (view_56_.isDirty()) {
			view_57_ = view_56_;
			i_58_ = i;
			i_60_ = i_59_;
		    }
		}
	    } while (view_56_ != null
		     && !(view_56_ instanceof InternalWindow));
	    if (view != view_57_) {
		rect.moveBy(i_58_ - i, i_60_ - i_59_);
		view_57_.addDirtyRect(rect);
	    }
	    if (!vector.containsIdentical(view_57_))
		vector.addElement(view_57_);
	}
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	return false;
    }
    
    public RootView rootView() {
	if (panel == null)
	    return super.rootView();
	return this;
    }
    
    void setPanel(FoundationPanel foundationpanel) {
	panel = foundationpanel;
    }
    
    public FoundationPanel panel() {
	return panel;
    }
    
    Application application() {
	return application;
    }
    
    void setApplication(Application application) {
	this.application = application;
    }
    
    public void setWindowClipView(View view) {
	_windowClipView = view;
    }
    
    public View windowClipView() {
	return _windowClipView;
    }
    
    void addComponentView(AWTComponentView awtcomponentview) {
	if (componentViews == null)
	    componentViews = new Vector();
	componentViews.addElement(awtcomponentview);
	awtcomponentview.setComponentBounds();
	panel.add(awtcomponentview.component);
    }
    
    void removeComponentView(AWTComponentView awtcomponentview) {
	if (componentViews == null)
	    componentViews = new Vector();
	componentViews.removeElement(awtcomponentview);
	panel.remove(awtcomponentview.component);
    }
    
    private final void subviewDidResizeOrMove(View view) {
	if (componentViews != null) {
	    int i = componentViews.count();
	    for (int i_61_ = 0; i_61_ < i; i_61_++) {
		AWTComponentView awtcomponentview
		    = (AWTComponentView) componentViews.elementAt(i_61_);
		if (awtcomponentview.descendsFrom(view))
		    awtcomponentview.setComponentBounds();
	    }
	}
	createMouseEnterLater();
	if (application != null) {
	    KeyboardArrow keyboardarrow = application.keyboardArrow();
	    View view_62_ = keyboardarrow.view();
	    if (view_62_ != null && view_62_.rootView() == this)
		updateArrowLocation(keyboardarrow);
	}
    }
    
    public void subviewDidResize(View view) {
	subviewDidResizeOrMove(view);
    }
    
    public void subviewDidMove(View view) {
	subviewDidResizeOrMove(view);
    }
    
    public boolean canDraw() {
	if (panel == null || panel.getGraphics() == null
	    || panel.getParent() == null)
	    return false;
	return isVisible;
    }
    
    public Point mousePoint() {
	return new Point(_mouseX, _mouseY);
    }
    
    public boolean viewExcludedFromModalSession(View view) {
	if (view == null)
	    return true;
	View view_63_ = Application.application().modalView();
	if (view_63_ != null && !view.descendsFrom(view_63_)) {
	    if (view instanceof DragView || view instanceof InternalWindow)
		return false;
	    return true;
	}
	return false;
    }
    
    public void setRedrawAll(boolean bool) {
	redrawAll = bool;
    }
    
    public boolean redrawAll() {
	return redrawAll;
    }
    
    Rect absoluteWindowBounds(InternalWindow internalwindow) {
	if (internalwindow.superview() != this)
	    return internalwindow.superview()
		       .convertRectToView(this, internalwindow.bounds);
	return internalwindow.bounds;
    }
    
    void viewHierarchyChanged() {
	if (_focusedView != null && !_focusedView.descendsFrom(this))
	    setFocusedView(null);
	Application.application()
	    .performCommandLater(this, "validateSelectedView", null, true);
    }
    
    private boolean isInWindow(View view) {
	if (view == null)
	    return false;
	if (view instanceof InternalWindow)
	    return true;
	View view_64_ = view.superview();
	do {
	    if (view_64_ == this)
		return false;
	    if (view_64_ instanceof InternalWindow)
		return true;
	    view_64_ = view_64_.superview();
	} while (view_64_ != null);
	return false;
    }
    
    public boolean mouseStillDown() {
	if (mouseDownCount > 0)
	    return true;
	return false;
    }
    
    View rootViewFocusedView() {
	if (_rootViewFocusedView != null) {
	    if (_rootViewFocusedView.descendsFrom(this))
		return _rootViewFocusedView;
	    _rootViewFocusedView = null;
	}
	return null;
    }
    
    public void setDefaultSelectedView(View view) {
	_defaultSelectedView = view;
    }
    
    public View defaultSelectedView() {
	return _defaultSelectedView;
    }
    
    public void selectView(View view, boolean bool) {
	View view_65_ = keyboardRootView();
	if ((application == null || application.isKeyboardUIEnabled())
	    && _selectedView != view_65_ && _selectedView != null) {
	    if (bool)
		setFocusedView(null);
	    if (_focusedView == null && view.canBecomeSelectedView()
		&& view.descendsFrom(view_65_)) {
		View view_66_ = view.superview();
		if (view_66_ != view_65_) {
		    do {
			if (view_66_.hidesSubviewsFromKeyboard())
			    view = view_66_;
			view_66_ = view_66_.superview();
		    } while (view_66_ != view_65_);
		}
		makeSelectedView(view);
	    } else if (view_65_ instanceof RootView)
		((RootView) view_65_).setDefaultSelectedView(view);
	    else if (view_65_ instanceof InternalWindow)
		((InternalWindow) view_65_).setDefaultSelectedView(view);
	}
    }
    
    public void selectViewAfter(View view) {
	Object object = null;
	View view_67_ = findNextView(view, keyboardRootView(), true);
	makeSelectedView(view_67_);
    }
    
    public void selectViewBefore(View view) {
	Object object = null;
	View view_68_ = findNextView(view, keyboardRootView(), false);
	makeSelectedView(view_68_);
    }
    
    void didBecomeFirstRootView() {
	ExternalWindow externalwindow = externalWindow();
	if (externalwindow != null)
	    externalwindow.didBecomeMain();
	if (_focusedView != null) {
	    _focusedView._startFocus();
	    Application.application().focusChanged(_focusedView);
	}
	application.performCommandLater(this, "validateSelectedView", null,
					true);
    }
    
    void didResignFirstRootView() {
	ExternalWindow externalwindow = externalWindow();
	if (externalwindow != null)
	    externalwindow.didResignMain();
	if (_mainWindow != null)
	    _setMainWindow(null);
	if (_focusedView != null)
	    _focusedView._pauseFocus();
	application.performCommandLater(this, "validateSelectedView", null,
					true);
    }
    
    void makeSelectedView(View view) {
	if (application != null && application.isKeyboardUIEnabled()) {
	    View view_69_ = keyboardRootView();
	    if (view != _selectedView) {
		if (_selectedView != null) {
		    _selectedView.willBecomeUnselected();
		    _selectedView = null;
		}
		_selectedView = view;
		if (_selectedView != null) {
		    if (view_69_ instanceof RootView)
			((RootView) view_69_)
			    .setDefaultSelectedView(_selectedView);
		    else if (view_69_ instanceof InternalWindow)
			((InternalWindow) view_69_)
			    .setDefaultSelectedView(_selectedView);
		    _selectedView.scrollRectToVisible(new Rect(0, 0,
							       _selectedView
								   .width(),
							       _selectedView
								   .height()));
		    _selectedView.willBecomeSelected();
		}
		validateKeyboardArrow();
	    }
	}
    }
    
    void validateKeyboardArrow() {
	if (application != null && application.isKeyboardUIEnabled()) {
	    Object object = null;
	    View view;
	    if (_selectedView != null && _selectedView.wantsKeyboardArrow())
		view = _selectedView;
	    else if (_focusedView != null
		     && _focusedView.canBecomeSelectedView()
		     && _focusedView.wantsKeyboardArrow())
		view = _focusedView;
	    else
		view = null;
	    if (view != null)
		showKeyboardArrowForView(view);
	    else
		hideKeyboardArrow();
	}
    }
    
    void validateSelectedView() {
	if (application == null || application.isKeyboardUIEnabled()) {
	    if (_focusedView != null
		|| (application != null
		    && application.firstRootView() != this)) {
		if (_selectedView != null)
		    makeSelectedView(null);
	    } else {
		View view = keyboardRootView();
		if (_selectedView != null
		    && _selectedView.descendsFrom(view)) {
		    boolean bool = false;
		    if ((!(_selectedView instanceof InternalWindow)
			 && !(_selectedView instanceof RootView))
			|| _selectedView == view) {
			if (_selectedView != view) {
			    for (View view_70_ = _selectedView.superview();
				 view_70_ != null && view_70_ != view;
				 view_70_ = view_70_.superview()) {
				if (view_70_.hidesSubviewsFromKeyboard()) {
				    bool = true;
				    break;
				}
			    }
			}
			if (!bool)
			    return;
			makeSelectedView(null);
		    }
		}
		View view_71_;
		if (view instanceof RootView)
		    view_71_ = ((RootView) view).defaultSelectedView();
		else if (view instanceof InternalWindow)
		    view_71_ = ((InternalWindow) view).defaultSelectedView();
		else
		    view_71_ = null;
		if (view_71_ != null && view_71_.descendsFrom(view))
		    makeSelectedView(view_71_);
		else
		    selectNextSelectableView();
	    }
	    validateKeyboardArrow();
	}
    }
    
    void selectNextSelectableView() {
	View view = keyboardRootView();
	Object object = null;
	View view_72_;
	if (_selectedView != null && _selectedView.descendsFrom(view))
	    view_72_ = findNextView(_selectedView, view, true);
	else
	    view_72_ = findNextView(null, view, true);
	makeSelectedView(view_72_);
    }
    
    void selectPreviousSelectableView() {
	View view = keyboardRootView();
	Object object = null;
	View view_73_;
	if (_selectedView != null && _selectedView.descendsFrom(view))
	    view_73_ = findNextView(_selectedView, view, false);
	else
	    view_73_ = findNextView(null, view, false);
	makeSelectedView(view_73_);
    }
    
    boolean processKeyboardEvent(KeyEvent keyevent, boolean bool) {
	if (application != null && application.isKeyboardUIEnabled() == false
	    || this.subviews().count() == 0)
	    return false;
	if (bool && !keyevent.isControlKeyDown())
	    return false;
	if (!bool && keyevent.isControlKeyDown())
	    return false;
	View view = keyboardRootView();
	validateSelectedView();
	if (!bool && (keyevent.isTabKey() || keyevent.isBackTabKey())) {
	    if (keyevent.isBackTabKey())
		selectPreviousSelectableView();
	    else
		selectNextSelectableView();
	    return true;
	}
	KeyStroke keystroke = new KeyStroke(keyevent);
	if (_selectedView != null) {
	    if (_selectedView.performCommandForKeyStroke(keystroke, 0))
		return true;
	    if (_selectedView != view) {
		for (View view_74_ = _selectedView.superview();
		     view_74_ != view; view_74_ = view_74_.superview()) {
		    if (view_74_.performCommandForKeyStroke(keystroke, 1))
			return true;
		}
	    }
	}
	View view_75_ = view;
	do {
	    view_75_ = nextView(view_75_, view, true, true, false);
	    if (view_75_.performCommandForKeyStroke(keystroke, 1))
		return true;
	} while (view_75_ != view);
	view_75_ = this;
	do {
	    view_75_ = nextView(view_75_, this, true, true, true);
	    if (view_75_.performCommandForKeyStroke(keystroke, 2))
		return true;
	} while (view_75_ != this);
	return false;
    }
    
    private View nextView(View view, View view_76_, boolean bool,
			  boolean bool_77_, boolean bool_78_) {
	Object object = null;
	if (view == view_76_ && bool) {
	    Vector vector = view_76_.subviews();
	    if (vector.count() > 0) {
		if (bool_77_)
		    return view_76_.firstSubview();
		return view_76_.lastSubview();
	    }
	    return null;
	}
	if (bool
	    && (!view.hidesSubviewsFromKeyboard()
		|| bool_78_ && view instanceof InternalWindow)
	    && view.subviews().count() > 0) {
	    if (bool_77_)
		return view.firstSubview();
	    return view.lastSubview();
	}
	View view_79_ = view.superview();
	if (bool_77_) {
	    View view_80_ = view_79_.viewAfter(view);
	    if (view_80_ != null)
		return view_80_;
	} else {
	    View view_81_ = view_79_.viewBefore(view);
	    if (view_81_ != null)
		return view_81_;
	}
	if (view_79_ == view_76_)
	    return view_76_;
	return nextView(view_79_, view_76_, false, bool_77_, bool_78_);
    }
    
    private View findNextView(View view, View view_82_, boolean bool) {
	if (view_82_ == null)
	    return null;
	if (view == null)
	    return view_82_;
	View view_84_;
	View view_83_ = view_84_ = view;
	do
	    view_83_ = nextView(view_83_, view_82_, true, bool, false);
	while (view_83_ != null && view_83_ != view_84_
	       && !view_83_.canBecomeSelectedView());
	return view_83_;
    }
    
    private View keyboardRootView() {
	View view = Application.application().modalView();
	if (view != null && view.isInViewHierarchy())
	    return view;
	InternalWindow internalwindow = mainWindow();
	if (internalwindow != null)
	    return internalwindow;
	if (application != null && application.firstRootView() != null)
	    return application.firstRootView();
	return this;
    }
    
    void showKeyboardArrowForView(View view) {
	if (application != null) {
	    KeyboardArrow keyboardarrow = application.keyboardArrow();
	    if (keyboardarrow.view() != view) {
		keyboardarrow.setRootView(this);
		keyboardarrow.setView(view);
		updateArrowLocation(keyboardarrow);
		keyboardarrow.show();
	    }
	}
    }
    
    void updateArrowLocation(KeyboardArrow keyboardarrow) {
	View view = keyboardarrow.view();
	int i = application.keyboardArrowPosition(view);
	Image image = application.keyboardArrowImage(i);
	Point point = application.keyboardArrowHotSpot(i);
	Point point_85_ = application.keyboardArrowLocation(view, i);
	point_85_.x -= point.x;
	point_85_.y -= point.y;
	if (windowClipView() != null)
	    this.convertPointToView(windowClipView(), point_85_, point_85_);
	keyboardarrow.setImage(image);
	keyboardarrow.moveTo(point_85_.x, point_85_.y);
    }
    
    void hideKeyboardArrow() {
	if (application != null) {
	    KeyboardArrow keyboardarrow = application.keyboardArrow();
	    if (keyboardarrow.rootView() == this) {
		keyboardarrow.hide();
		keyboardarrow.setRootView(null);
		keyboardarrow.setView(null);
	    }
	}
    }
    
    public boolean canBecomeSelectedView() {
	return true;
    }
    
    boolean wantsKeyboardArrow() {
	return false;
    }
    
    public void adjustForExpectedMouseDownCount() {
	if (application.jdkMouseEventHackEnabled) {
	    if (mouseDownCount < 1) {
		MouseEvent mouseevent = new MouseEvent(0L, -1, -1, -1, 0);
		while (mouseDownCount < 1)
		    _mouseDown(mouseevent);
	    }
	}
    }
}
