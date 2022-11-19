/* PlaywriteWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.Date;

import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.InternalWindowBorder;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.application.Window;
import COM.stagecast.ifc.netscape.application.WindowOwner;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PlaywriteWindow extends InternalWindow
    implements Debug.Constants, ResourceIDs.LookAndFeelIDs, Worldly
{
    static String CLOSE = "CLOSE_WINDOW";
    private static final boolean CHECK_THREAD = false;
    public static final Color DEFAULT_BACKGROUND_COLOR = new Color(76, 76, 76);
    private World _world = null;
    private TitleBar titleBar = null;
    private PlaywriteButton closeButton = null;
    private boolean allowDisable = false;
    private boolean _allowDestroy = true;
    private View _defaultKeyView;
    private StateWatcher _worldWatcher;
    private ScrollableArea _mainScroller;
    private GrayLayer _grayLayer;
    private Color _baseColor = DEFAULT_BACKGROUND_COLOR;
    private Color _contentsColor = DEFAULT_BACKGROUND_COLOR;
    private Object _model;
    
    public static class DefaultOwner implements WindowOwner
    {
	public boolean windowWillShow(Window window) {
	    return true;
	}
	
	public void windowDidShow(Window window) {
	    /* empty */
	}
	
	public boolean windowWillHide(Window window) {
	    return true;
	}
	
	public void windowDidHide(Window window) {
	    /* empty */
	}
	
	public void windowDidBecomeMain(Window window) {
	    /* empty */
	}
	
	public void windowDidResignMain(Window window) {
	    /* empty */
	}
	
	public void windowWillSizeBy(Window window, Size size) {
	    /* empty */
	}
    }
    
    public PlaywriteWindow(int x, int y, int w, int h, World world) {
	super(x, y, w, h);
	init(world);
    }
    
    public PlaywriteWindow(Rect bounds, World world) {
	this(bounds.x, bounds.y, bounds.width, bounds.height, world);
    }
    
    private void init(World world) {
	ASSERT.isNotNull(world);
	_world = world;
	int topY = 0;
	topY = PlaywriteBorder.TOP_BORDER.height();
	this.setResizable(true);
	this.setBorder(new PlaywriteBorder(this));
	titleBar = new TitleBar(this);
	titleBar.moveTo(this.border().rightMargin(), topY);
	titleBar.sizeTo((this.width() - this.border().rightMargin()
			 - this.border().leftMargin()),
			titleBar.height());
	this.addSubviewToWindow(titleBar);
	setCloseable(true);
	this.setBorder(new PlaywriteBorder(this));
	this.layoutParts();
	if (getWorldWatcher() == null)
	    setWorldWatcher(createWorldWatcher());
	setBaseColor(DEFAULT_BACKGROUND_COLOR);
	setContentsColor(DEFAULT_BACKGROUND_COLOR);
	titleBar.changeWindowColor(DEFAULT_BACKGROUND_COLOR);
	this.contentView().setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
	_defaultKeyView = this;
    }
    
    static RootView getRootView() {
	return PlaywriteRoot.getMainRootView();
    }
    
    final void setDisablable(boolean flag) {
	allowDisable = flag;
    }
    
    Color getBaseColor() {
	return _baseColor;
    }
    
    final Color getContentsColor() {
	return _contentsColor;
    }
    
    final void setBaseColor(Color color) {
	_baseColor = color;
    }
    
    final void setContentsColor(Color contentsColor) {
	_contentsColor = contentsColor;
    }
    
    public final PlaywriteButton getCloseButton() {
	return closeButton;
    }
    
    public final TitleBar getTitleBar() {
	return titleBar;
    }
    
    public final Object getModelObject() {
	return _model;
    }
    
    public final void setModelObject(Object obj) {
	_model = obj;
    }
    
    public void setDefaultKeyView(View view) {
	_defaultKeyView = view;
    }
    
    public View getDefaultKeyView() {
	return _defaultKeyView;
    }
    
    public void worldStateChanged(Object who, Object oldState,
				  Object transition, Object newState) {
	if (newState == World.CLOSING)
	    close();
    }
    
    public void setWorldWatcher(StateWatcher stateWatcher) {
	if (_world != null && _worldWatcher != null)
	    _world.removeStateWatcher(_worldWatcher);
	_worldWatcher = stateWatcher;
	if (_world != null && _worldWatcher != null)
	    _world.addStateWatcher(_worldWatcher);
    }
    
    public StateWatcher getWorldWatcher() {
	return _worldWatcher;
    }
    
    public StateWatcher createWorldWatcher() {
	return new StateWatcher() {
	    public void stateChanged(Object who, Object oldState,
				     Object transition, Object newState) {
		worldStateChanged(who, oldState, transition, newState);
	    }
	};
    }
    
    public final World getWorld() {
	return _world;
    }
    
    public void setWorld(World world) {
	World oldWorld = getWorld();
	if (world != oldWorld) {
	    StateWatcher stateWatcher = getWorldWatcher();
	    if (stateWatcher != null && oldWorld != null)
		oldWorld.removeStateWatcher(stateWatcher);
	    _world = world;
	    setWorldWatcher(stateWatcher);
	}
    }
    
    public ScrollableArea getMainScroller() {
	return _mainScroller;
    }
    
    public void setMainScroller(ScrollableArea scrollableArea) {
	_mainScroller = scrollableArea;
    }
    
    public boolean getAllowDestroy() {
	return _allowDestroy;
    }
    
    public void setAllowDestroy(boolean flag) {
	_allowDestroy = flag;
    }
    
    public boolean hasClosed() {
	return titleBar == null;
    }
    
    public boolean isPointInBorder(int x, int y) {
	if (y >= (this.height()
		  - ((InternalWindowBorder) this.border()).resizePartWidth())
	    && (x <= this.border().leftMargin()
		|| x >= this.width() - this.border().rightMargin()))
	    y = this.height() - 1;
	else
	    y++;
	return super.isPointInBorder(x, y);
    }
    
    public int cursorForPoint(int x, int y) {
	int resizePartWidth
	    = ((InternalWindowBorder) this.border()).resizePartWidth();
	if (y >= this.height() - resizePartWidth) {
	    if (x <= resizePartWidth)
		return 4;
	    if (x >= this.width() - resizePartWidth)
		return 5;
	}
	return super.cursorForPoint(x, y);
    }
    
    boolean boundify() {
	Rect rvBounds = PlaywriteRoot.getMainRootViewBounds();
	if (rvBounds == null)
	    return false;
	int inset = 5;
	rvBounds.sizeBy(-5, -5);
	int dx = 0;
	int dy = 0;
	int dw = 0;
	int dh = 0;
	if (bounds.x < rvBounds.x)
	    dx = rvBounds.x - bounds.x;
	if (bounds.y < rvBounds.y)
	    dy = rvBounds.y - bounds.y;
	if (bounds.width > rvBounds.width)
	    dw = rvBounds.width - bounds.width;
	if (bounds.height > rvBounds.height)
	    dh = rvBounds.height - bounds.height;
	Size minSize = this.minSize();
	if (minSize != null) {
	    if (bounds.width + dw < minSize.width)
		dw = minSize.width - bounds.width;
	    if (bounds.height + dh < minSize.height)
		dh = minSize.height - bounds.height;
	}
	if (dw != 0 || dh != 0)
	    this.sizeBy(dw, dh);
	if (bounds.maxX() > rvBounds.maxX())
	    dx = rvBounds.maxX() - bounds.maxX();
	if (bounds.maxY() > rvBounds.maxY())
	    dy = rvBounds.maxY() - bounds.maxY();
	if (dx != 0 || dy != 0)
	    this.moveBy(dx, dy);
	return true;
    }
    
    public void show() {
	boolean didBound = boundify();
	super.show();
	if (!didBound)
	    boundify();
	if (!PlaywriteRoot.isPlayer() && allowDisable
	    && RuleEditor.isRecordingOrEditing())
	    disable();
    }
    
    public final void setCloseable(boolean isCloseable) {
	if (closeButton == null && isCloseable) {
	    closeButton = (PlaywriteButton) createCloseButton();
	    titleBar.addSubviewLeft(closeButton);
	} else if (closeButton != null && !isCloseable) {
	    closeButton.removeFromSuperview();
	    closeButton = null;
	}
    }
    
    public boolean isCloseable() {
	return closeButton != null;
    }
    
    public void setTitle(String name) {
	getTitleBar().setTitle(name);
	super.setTitle(name);
    }
    
    public void didBecomeMain() {
	this.moveToFront();
	getTitleBar().activate();
	super.didBecomeMain();
    }
    
    public void didResignMain() {
	getTitleBar().deactivate();
	super.didResignMain();
    }
    
    public void addSubview(View subview) {
	super.addSubview(subview);
	if (subview instanceof ScrollableArea && getMainScroller() == null)
	    setMainScroller((ScrollableArea) subview);
    }
    
    void throwIfInWorldThread() {
	Window window = this.window();
	if (window instanceof PlaywriteWindow) {
	    World world = ((PlaywriteWindow) window).getWorld();
	    if (world != null && world.inWorldThread()) {
		System.out.println(this);
		Debug.stackTrace();
	    }
	}
    }
    
    protected void removeSubview(View view) {
	super.removeSubview(view);
    }
    
    public void destroyWindow() {
	if (_allowDestroy) {
	    if (hasClosed()) {
		Debug.print
		    ("debug.pwwindow",
		     "destroy called a second time on PlaywriteWindow!");
		Debug.stackTrace("debug.pwwindow");
	    } else {
		titleBar.discard();
		Util.detachSubviews(titleBar);
		View cv = this.contentView();
		int size = cv.subviews().size();
		for (int i = 0; i < size; i++) {
		    View view = (View) cv.subviews().elementAt(i);
		    if (view instanceof ViewGlue)
			((ViewGlue) view).discard();
		}
		Util.detachSubviews(this.contentView());
		titleBar = null;
		closeButton = null;
		if (_world != null)
		    _world.removeStateWatcher(_worldWatcher);
		_world = null;
		_defaultKeyView = null;
	    }
	}
    }
    
    public void subviewDidResize(View subview) {
	super.subviewDidResize();
	if (subview == titleBar)
	    this.layoutParts();
    }
    
    public void resizeToAvoidView(View view, int extra) {
	if (view != null && view.superview() != null) {
	    Rect viewBounds
		= view.superview().convertRectToView(null, view.bounds);
	    if (bounds.x <= viewBounds.maxX()
		&& bounds.maxY() >= viewBounds.y) {
		int dy = viewBounds.y - bounds.maxY() - extra;
		if (this.height() + dy >= this.minSize().height)
		    this.sizeBy(0, dy);
	    }
	}
    }
    
    protected Button createCloseButton() {
	PlaywriteButton closeButton
	    = PlaywriteButton.createFromResource("Win close", true);
	closeButton.setCommand(CLOSE);
	closeButton.setTarget(this);
	return closeButton;
    }
    
    protected void finalize() throws Throwable {
	Debug.print("debug.gc", "Reclaiming PlaywriteWindow ", this.title());
	super.finalize();
    }
    
    void disable() {
	if (allowDisable) {
	    Vector subviews = this.contentView().subviews();
	    for (int i = 0; i < subviews.size(); i++) {
		View view = (View) subviews.elementAt(i);
		if (view instanceof PlaywriteView)
		    ((PlaywriteView) view).disable();
		else if (view instanceof PlaywriteButton)
		    ((PlaywriteButton) view).setEnabled(false);
	    }
	}
    }
    
    void enable() {
	if (allowDisable) {
	    Vector subviews = this.contentView().subviews();
	    for (int i = 0; i < subviews.size(); i++) {
		View view = (View) subviews.elementAt(i);
		if (view instanceof PlaywriteView)
		    ((PlaywriteView) view).enable();
		else if (view instanceof PlaywriteButton)
		    ((PlaywriteButton) view).setEnabled(true);
	    }
	}
    }
    
    void disableWindow() {
	if (_grayLayer == null) {
	    this.setCanBecomeMain(false);
	    _grayLayer = new GrayLayer(this.width(), this.height());
	    this.addSubviewToWindow(_grayLayer);
	    this.setDirty(true);
	}
    }
    
    void enableWindow() {
	if (_grayLayer != null) {
	    this.setCanBecomeMain(true);
	    _grayLayer.removeFromSuperview();
	    _grayLayer = null;
	    this.setDirty(true);
	}
    }
    
    public void hide() {
	if (this.rootView() != null) {
	    View focus = this.rootView().focusedView();
	    if (focus != null)
		focus.stopFocus();
	}
	super.hide();
    }
    
    public void close() {
	Selection.unselectAll();
	boolean prevActive = BitmapManager.MemoryCleanupGnome.setActive(false);
	try {
	    hide();
	    destroyWindow();
	} finally {
	    BitmapManager.MemoryCleanupGnome.setActive(prevActive);
	}
    }
    
    void changeWindowColor(Color color) {
	changeWindowColor(color, color);
    }
    
    void changeWindowColor(Color color, Color contentsColor) {
	if (color != getBaseColor() || contentsColor != getContentsColor()) {
	    setBaseColor(color);
	    setContentsColor(contentsColor);
	    this.disableDrawing();
	    titleBar.changeWindowColor(color);
	    this.contentView().setBackgroundColor(contentsColor);
	    if (this.border() instanceof PlaywriteBorder)
		((PlaywriteBorder) this.border())
		    .setResizeColor(color.darkerColor());
	    this.setDirty(true);
	    this.reenableDrawing();
	}
    }
    
    public void setMainWindow() {
	MouseEvent mouseEvent
	    = new MouseEvent(new Date().getTime(), -1, bounds.x, bounds.y, 0);
	PlaywriteRoot.app().fakeMouseEvent(mouseEvent);
    }
    
    public void performCommand(String command, Object data) {
	if (CLOSE.equals(command))
	    close();
    }
}
