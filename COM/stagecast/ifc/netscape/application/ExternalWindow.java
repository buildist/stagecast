/* ExternalWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.Toolkit;

import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class ExternalWindow implements Window, ApplicationObserver
{
    java.awt.Window awtWindow;
    private FoundationPanel panel;
    private WindowOwner owner;
    private int type;
    private Size minimumSize;
    private String title;
    private Rect bounds;
    private boolean resizable = true;
    private boolean visible = false;
    private boolean hideOnPause = true;
    private boolean showOnResume = false;
    private boolean containsDocument = false;
    private boolean waitingForInvalidation = false;
    Menu menu;
    MenuView menuView;
    
    public ExternalWindow() {
	this(1);
    }
    
    private Frame firstRootViewParentFrame() {
	Application application = Application.application();
	if (application != null) {
	    RootView rootview = application.firstRootView();
	    if (rootview != null) {
		FoundationPanel foundationpanel = rootview.panel();
		java.awt.Container container;
		for (container = foundationpanel.getParent();
		     container != null && !(container instanceof Frame);
		     container = container.getParent()) {
		    /* empty */
		}
		if (container != null)
		    return (Frame) container;
	    }
	}
	return appletParentFrame();
    }
    
    private Frame appletParentFrame() {
	java.applet.Applet applet = AWTCompatibility.awtApplet();
	java.awt.Container container;
	if (applet != null) {
	    for (container = applet.getParent(); container != null;
		 container = container.getParent()) {
		if (container instanceof Frame)
		    break;
	    }
	} else
	    return null;
	return (Frame) container;
    }
    
    private synchronized void validateAWTWindow(int i, boolean bool) {
	if (waitingForInvalidation)
	    waitingForInvalidation = false;
	if (awtWindow == null) {
	    Application application = Application.application();
	    RootView rootview = panel.rootView();
	    if (bool) {
		FoundationDialog foundationdialog = createDialog();
		foundationdialog.setExternalWindow(this);
		awtWindow = foundationdialog;
	    } else if (i == 1) {
		FoundationFrame foundationframe = createFrame();
		foundationframe.setExternalWindow(this);
		awtWindow = foundationframe;
	    } else {
		FoundationWindow foundationwindow = createWindow();
		foundationwindow.setExternalWindow(this);
		awtWindow = foundationwindow;
	    }
	    if (awtWindow instanceof Dialog)
		((Dialog) awtWindow).setResizable(resizable);
	    else if (awtWindow instanceof FoundationFrame)
		((FoundationFrame) awtWindow).setResizable(resizable);
	    awtWindow.addNotify();
	    awtWindow.add(panel);
	    awtWindow.reshape(bounds.x, bounds.y, bounds.width, bounds.height);
	    awtWindow.layout();
	    if (i == 1) {
		if (awtWindow instanceof Dialog)
		    ((Dialog) awtWindow).setTitle(title);
		else
		    ((FoundationFrame) awtWindow).setTitle(title);
	    }
	    if (menu != null)
		((FoundationFrame) awtWindow).setMenuBar(menu.awtMenuBar());
	}
    }
    
    synchronized void invalidateAWTWindow() {
	if (waitingForInvalidation)
	    _invalidateAWTWindow();
	waitingForInvalidation = false;
    }
    
    void _invalidateAWTWindow() {
	if (awtWindow != null) {
	    bounds = bounds();
	    awtWindow.remove(panel);
	    awtWindow.dispose();
	    awtWindow = null;
	}
    }
    
    public ExternalWindow(int i) {
	Application application = Application.application();
	title = "";
	type = i;
	panel = createPanel();
	bounds = new Rect(0, 0, 0, 0);
	setBounds(0, 0, 150, 150);
	Application.application().addObserver(this);
    }
    
    public void setTitle(String string) {
	if (string == null)
	    string = "";
	title = string;
	if (awtWindow != null && type == 1) {
	    if (awtWindow instanceof Dialog)
		((Dialog) awtWindow).setTitle(title);
	    else
		((FoundationFrame) awtWindow).setTitle(title);
	}
    }
    
    public String title() {
	return title;
    }
    
    public void show() {
	validateAWTWindow(type, false);
	if (owner == null || owner.windowWillShow(this)) {
	    awtWindow.show();
	    panel.rootView.setVisible(true);
	    visible = true;
	    showOnResume = false;
	    awtWindow.toFront();
	    if (owner != null)
		owner.windowDidShow(this);
	}
    }
    
    public void showModally() {
	Application application = Application.application();
	EventLoop eventloop = Application.application().eventLoop();
	if (type == 0)
	    throw new InconsistencyException
		      ("Cannot run blank windows modally");
	if (owner == null || owner.windowWillShow(this)) {
	    validateAWTWindow(type, true);
	    ModalDialogManager modaldialogmanager
		= new ModalDialogManager((Dialog) awtWindow);
	    modaldialogmanager.show();
	    showOnResume = false;
	    panel.rootView.setVisible(true);
	    visible = true;
	    if (owner != null)
		owner.windowDidShow(this);
	    application.beginModalSessionForView(rootView());
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
	    application.endModalSessionForView(rootView());
	}
    }
    
    public void hide() {
	if (awtWindow != null) {
	    if (owner == null || owner.windowWillHide(this)) {
		if (containsDocument() && isCurrentDocument())
		    Application.application()
			.chooseNextCurrentDocumentWindow(this);
		awtWindow.hide();
		visible = false;
		panel.rootView.setVisible(false);
		showOnResume = false;
		if (owner != null)
		    owner.windowDidHide(this);
		WindowInvalidationAgent windowinvalidationagent
		    = new WindowInvalidationAgent(this);
		waitingForInvalidation = true;
		windowinvalidationagent.run();
	    }
	}
    }
    
    public boolean isVisible() {
	return visible;
    }
    
    public void dispose() {
	RootView rootview = rootView();
	Application application = rootview.application();
	if (containsDocument() && isCurrentDocument())
	    application.chooseNextCurrentDocumentWindow(this);
	visible = false;
	_invalidateAWTWindow();
	application.removeObserver(this);
	application.removeRootView(rootview);
	panel.rootView.setVisible(false);
	panel.rootView = null;
    }
    
    public void setMenu(Menu menu) {
	this.menu = menu;
	if (!this.menu.isTopLevel())
	    throw new InconsistencyException("menu must be main menu");
	this.menu.setApplication(rootView().application());
	MenuBar menubar = this.menu.awtMenuBar();
	if (awtWindow != null)
	    ((FoundationFrame) awtWindow).setMenuBar(menubar);
    }
    
    public Menu menu() {
	return menu;
    }
    
    public void setMenuView(MenuView menuview) {
	if (menuview == null || menuview != menuView) {
	    if (menuView != null)
		menuView.removeFromSuperview();
	    menuView = menuview;
	    int i = rootView().bounds.x;
	    int i_0_ = rootView().bounds.y;
	    int i_1_ = rootView().bounds.width;
	    int i_2_ = menuView.height();
	    menuView.setBounds(i, i_0_, i_1_, i_2_);
	    addSubview(menuView);
	}
    }
    
    public MenuView menuView() {
	return menuView;
    }
    
    public RootView rootView() {
	return panel.rootView;
    }
    
    Application application() {
	return Application.application();
    }
    
    public void setOwner(WindowOwner windowowner) {
	owner = windowowner;
    }
    
    public WindowOwner owner() {
	return owner;
    }
    
    void didBecomeMain() {
	if (owner != null)
	    owner.windowDidBecomeMain(this);
	if (containsDocument())
	    Application.application().makeCurrentDocumentWindow(this);
    }
    
    void didResignMain() {
	if (owner != null)
	    owner.windowDidResignMain(this);
    }
    
    public Size contentSize() {
	RootView rootview = rootView();
	if (rootview == null)
	    return null;
	return new Size(rootview.bounds.width, rootview.bounds.height);
    }
    
    public void addSubview(View view) {
	RootView rootview = rootView();
	if (rootview != null)
	    rootview.addSubview(view);
    }
    
    public void setBounds(int i, int i_3_, int i_4_, int i_5_) {
	boolean bool = false;
	if (owner != null && (bounds.width != i_4_ || bounds.height != i_5_)) {
	    Size size = new Size(i_4_ - bounds.width, i_5_ - bounds.height);
	    owner.windowWillSizeBy(this, size);
	    i_4_ = bounds.width + size.width;
	    i_5_ = bounds.height + size.height;
	}
	if (bounds.x != i || bounds.y != i_3_ || bounds.width != i_4_
	    || bounds.height != i_5_) {
	    if (bounds.width != i_4_ || bounds.height != i_5_)
		bool = true;
	    bounds.setBounds(i, i_3_, i_4_, i_5_);
	    if (awtWindow != null) {
		awtWindow.reshape(i, i_3_, i_4_, i_5_);
		Rectangle rectangle = awtWindow.bounds();
		bounds.setBounds(rectangle.x, rectangle.y, rectangle.width,
				 rectangle.height);
		awtWindow.layout();
	    }
	}
	if (bool && awtWindow == null) {
	    validateAWTWindow(type, false);
	    _invalidateAWTWindow();
	}
    }
    
    void validateBounds() {
	if (awtWindow != null) {
	    java.awt.Point point = awtWindow.location();
	    Dimension dimension = awtWindow.size();
	    Rect rect = new Rect(point.x, point.y, dimension.width,
				 dimension.height);
	    if (!rect.equals(bounds)) {
		Size size = new Size(rect.width - bounds.width,
				     rect.height - bounds.height);
		if (owner != null)
		    owner.windowWillSizeBy(this, size);
		bounds.setBounds(rect.x, rect.y, rect.width, rect.height);
	    }
	}
    }
    
    public void setBounds(Rect rect) {
	setBounds(rect.x, rect.y, rect.width, rect.height);
    }
    
    public void sizeTo(int i, int i_6_) {
	setBounds(bounds.x, bounds.y, i, i_6_);
    }
    
    public void sizeBy(int i, int i_7_) {
	setBounds(bounds.x, bounds.y, bounds.width + i, bounds.height + i_7_);
    }
    
    public void moveBy(int i, int i_8_) {
	setBounds(bounds.x + i, bounds.y + i_8_, bounds.width, bounds.height);
    }
    
    public void center() {
	Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	Rect rect = new Rect(bounds());
	rect.x
	    = (int) Math.floor((double) (dimension.width - rect.width) / 2.0);
	rect.y = (int) Math.floor((double) (dimension.height - rect.height)
				  / 2.0);
	setBounds(rect);
    }
    
    public void moveTo(int i, int i_9_) {
	setBounds(i, i_9_, bounds.width, bounds.height);
    }
    
    public Size windowSizeForContentSize(int i, int i_10_) {
	boolean bool = awtWindow != null;
	if (!bool)
	    validateAWTWindow(type, false);
	Insets insets = awtWindow.insets();
	if (!bool)
	    _invalidateAWTWindow();
	return new Size(i + insets.left + insets.right,
			i_10_ + insets.top + insets.bottom);
    }
    
    public View viewForMouse(int i, int i_11_) {
	return rootView().viewForMouse(i, i_11_);
    }
    
    public void setMinSize(int i, int i_12_) {
	minimumSize = new Size(i, i_12_);
    }
    
    public Size minSize() {
	return minimumSize;
    }
    
    public Rect bounds() {
	if (awtWindow != null) {
	    java.awt.Point point = awtWindow.location();
	    Dimension dimension = awtWindow.size();
	    return new Rect(point.x, point.y, dimension.width,
			    dimension.height);
	}
	return new Rect(bounds);
    }
    
    public void setResizable(boolean bool) {
	resizable = bool;
	if (awtWindow != null)
	    throw new InconsistencyException
		      ("Cannot call setResizable on a visible external window");
    }
    
    public boolean isResizable() {
	return resizable;
    }
    
    public FoundationPanel panel() {
	return panel;
    }
    
    public void setContainsDocument(boolean bool) {
	containsDocument = bool;
	if (bool == false
	    && Application.application().currentDocumentWindow() == this)
	    Application.application().chooseNextCurrentDocumentWindow(this);
	else if (bool == true
		 && Application.application().firstRootView() == rootView())
	    Application.application().makeCurrentDocumentWindow(this);
    }
    
    public boolean containsDocument() {
	return containsDocument;
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
    
    protected FoundationDialog createDialog() {
	return new FoundationDialog(firstRootViewParentFrame(), true);
    }
    
    protected FoundationFrame createFrame() {
	return new FoundationFrame();
    }
    
    protected FoundationWindow createWindow() {
	return new FoundationWindow(appletParentFrame());
    }
    
    protected FoundationPanel createPanel() {
	return new FoundationPanel();
    }
    
    public void applicationDidStart(Application application) {
	/* empty */
    }
    
    public void applicationDidStop(Application application) {
	dispose();
    }
    
    public void focusDidChange(Application application, View view) {
	/* empty */
    }
    
    public void currentDocumentDidChange(Application application,
					 Window window) {
	/* empty */
    }
    
    public void applicationDidPause(Application application) {
	if (hideOnPause && visible) {
	    hide();
	    showOnResume = true;
	}
    }
    
    public void applicationDidResume(Application application) {
	if (showOnResume)
	    show();
    }
    
    public void setHidesWhenPaused(boolean bool) {
	hideOnPause = bool;
    }
    
    public boolean hidesWhenPaused() {
	return hideOnPause;
    }
    
    public void performCommand(String string, Object object) {
	if ("show".equals(string))
	    show();
	else if ("hide".equals(string))
	    hide();
	else
	    throw new NoSuchMethodError("unknown command: " + string);
    }
    
    public void moveToFront() {
	if (isVisible())
	    awtWindow.toFront();
    }
    
    public void moveToBack() {
	if (isVisible())
	    awtWindow.toBack();
    }
}
