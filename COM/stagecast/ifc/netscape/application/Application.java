/* Application - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Frame;
import java.awt.MediaTracker;
import java.io.InputStream;
import java.net.URL;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class Application implements Runnable, EventProcessor
{
    static Hashtable groupToApplication = new Hashtable();
    static final String _releaseName = "IFC 1.1.2";
    static Clipboard clipboard;
    static Object clipboardLock = new Object();
    Applet applet;
    AppletResources _appResources;
    EventLoop eventLoop;
    Vector _languageVector;
    Vector rootViews;
    RootView mainRootView;
    boolean didCreateApplet;
    Vector _modalVector;
    Vector observers;
    Vector activeMenuViews;
    boolean jdkMouseEventHackEnabled;
    MediaTracker tracker;
    int bitmapCount;
    Hashtable bitmapByName;
    Hashtable soundByName;
    Hashtable fontByName;
    TimerQueue timerQueue;
    Object cleanupLock;
    boolean isPaused;
    boolean _kbdUIEnabled;
    Window currentDocumentWindow;
    KeyboardArrow keyboardArrow;
    public static final int TOP_LEFT_POSITION = 0;
    public static final int BOTTOM_LEFT_POSITION = 1;
    public static final int TOP_RIGHT_POSITION = 2;
    public static final int BOTTOM_RIGHT_POSITION = 3;
    static final int FIRST_POSITION = 0;
    static final int LAST_POSITION = 3;
    static final int arrowXOffset = 0;
    static final int arrowYOffset = 0;
    
    public Application() {
	eventLoop = new EventLoop();
	rootViews = new Vector();
	_modalVector = new Vector();
	observers = new Vector();
	activeMenuViews = new Vector();
	jdkMouseEventHackEnabled = true;
	tracker = null;
	bitmapCount = 0;
	bitmapByName = new Hashtable();
	soundByName = new Hashtable();
	fontByName = new Hashtable();
	_kbdUIEnabled = true;
	groupToApplication.put(Thread.currentThread().getThreadGroup(), this);
	FoundationApplet foundationapplet = FoundationApplet.applet();
	if (foundationapplet == null) {
	    foundationapplet = createApplet();
	    foundationapplet.setApplication(this);
	    didCreateApplet = true;
	} else {
	    isPaused = true;
	    foundationapplet.setupCanvas(this);
	}
	applet = foundationapplet;
	_appResources = new AppletResources(this, codeBase());
	timerQueue = new TimerQueue();
	eventLoop.application = this;
    }
    
    public static String releaseName() {
	return "IFC 1.1.2";
    }
    
    public Application(Applet applet) {
	eventLoop = new EventLoop();
	rootViews = new Vector();
	_modalVector = new Vector();
	observers = new Vector();
	activeMenuViews = new Vector();
	jdkMouseEventHackEnabled = true;
	tracker = null;
	bitmapCount = 0;
	bitmapByName = new Hashtable();
	soundByName = new Hashtable();
	fontByName = new Hashtable();
	_kbdUIEnabled = true;
	groupToApplication.put(Thread.currentThread().getThreadGroup(), this);
	this.applet = applet;
	_appResources = new AppletResources(this, codeBase());
	timerQueue = new TimerQueue();
	appletStarted();
	eventLoop.application = this;
    }
    
    public static Application application() {
	ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();
	Application application
	    = (Application) groupToApplication.get(threadgroup);
	if (application == null)
	    application = FoundationApplet.currentApplication();
	return application;
    }
    
    public void init() {
	/* empty */
    }
    
    public void cleanup() {
	Enumeration enumeration = groupToApplication.keys();
	applicationDidStop();
	if (applet instanceof FoundationApplet)
	    ((FoundationApplet) applet).cleanup();
	if (didCreateApplet) {
	    ((FoundationApplet) applet).destroyFromIFC();
	    if (applet.getParent() != null)
		applet.getParent().remove(applet);
	}
	while (enumeration.hasMoreElements()) {
	    ThreadGroup threadgroup = (ThreadGroup) enumeration.nextElement();
	    Application application_0_
		= (Application) groupToApplication.get(threadgroup);
	    if (application_0_ == this) {
		groupToApplication.remove(threadgroup);
		break;
	    }
	}
	timerQueue = null;
	observers.removeAllElements();
	eventLoop.application = null;
    }
    
    public void stopRunning() {
	eventLoop.stopRunning();
    }
    
    void stopRunningForAWT() {
	synchronized (cleanupLock) {
	    eventLoop.stopRunning();
	    for (;;) {
		try {
		    cleanupLock.wait();
		    break;
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    }
	}
    }
    
    public void run() {
	cleanupLock = new Object();
	applicationDidStart();
	init();
	eventLoop.run();
	cleanup();
	synchronized (cleanupLock) {
	    cleanupLock.notify();
	}
    }
    
    public void handleEventLoopException(Exception exception) {
	/* empty */
    }
    
    public EventLoop eventLoop() {
	return eventLoop;
    }
    
    Vector languagePreferences() {
	if (_languageVector == null)
	    _languageVector = new Vector();
	return _languageVector;
    }
    
    void syncGraphics() {
	/* empty */
    }
    
    InputStream streamForInterface(String string) {
	return _appResources.streamForInterface(string);
    }
    
    InputStream streamForResourceOfType(String string, String string_1_) {
	return _appResources.streamForResourceOfType(string, string_1_);
    }
    
    public boolean isApplet() {
	return didCreateApplet ^ true;
    }
    
    InputStream streamForRelativePath(String string) {
	URL url;
	try {
	    url = new URL(codeBase(), string);
	} catch (Exception exception) {
	    System.err
		.println("Application.streamForRelativePath() - " + exception);
	    url = null;
	}
	if (url == null)
	    return null;
	InputStream inputstream;
	try {
	    inputstream = url.openStream();
	} catch (Exception exception) {
	    System.err.println
		("Application.streamForURL() - Trouble retrieving URL " + url
		 + " : " + exception);
	    inputstream = null;
	}
	return inputstream;
    }
    
    AppletContext getAppletContext() {
	return applet == null ? null : applet.getAppletContext();
    }
    
    public URL codeBase() {
	return applet.getCodeBase();
    }
    
    public String parameterNamed(String string) {
	return applet == null ? null : applet.getParameter(string);
    }
    
    public RootView mainRootView() {
	return mainRootView;
    }
    
    public void setMainRootView(RootView rootview) {
	addRootView(rootview);
	mainRootView = rootview;
    }
    
    RootView firstRootView() {
	return (RootView) rootViews.lastElement();
    }
    
    public Vector rootViews() {
	return rootViews;
    }
    
    public Vector externalWindows() {
	int i = rootViews.count();
	Vector vector = new Vector();
	for (int i_2_ = 0; i_2_ < i; i_2_++) {
	    RootView rootview = (RootView) rootViews.elementAt(i_2_);
	    ExternalWindow externalwindow = rootview.externalWindow();
	    if (externalwindow != null)
		vector.addElement(externalwindow);
	}
	return vector;
    }
    
    void addRootView(RootView rootview) {
	if (!rootViews.contains(rootview)) {
	    rootViews.insertElementAt(rootview, 0);
	    rootview.setApplication(this);
	}
    }
    
    void removeRootView(RootView rootview) {
	rootViews.removeElement(rootview);
	rootview.setApplication(null);
	if (rootViews.count() > 0)
	    ((RootView) rootViews.lastElement()).didBecomeFirstRootView();
    }
    
    void makeFirstRootView(RootView rootview) {
	if (rootViews.indexOf(rootview) != -1) {
	    if (rootViews.lastElement() != rootview) {
		RootView rootview_3_ = (RootView) rootViews.lastElement();
		rootViews.removeElement(rootview);
		rootViews.addElement(rootview);
		if (rootview_3_ != null)
		    rootview_3_.didResignFirstRootView();
		rootview.didBecomeFirstRootView();
	    }
	}
    }
    
    Frame frame() {
	java.awt.Container container;
	for (container = applet;
	     container != null && !(container instanceof Frame);
	     container = container.getParent()) {
	    /* empty */
	}
	if (container != null)
	    return (Frame) container;
	return null;
    }
    
    synchronized MediaTracker mediaTracker() {
	if (tracker == null)
	    tracker = new MediaTracker(applet);
	return tracker;
    }
    
    synchronized int nextBitmapNumber() {
	return bitmapCount++;
    }
    
    synchronized TimerQueue timerQueue() {
	return timerQueue;
    }
    
    void addActiveMenuView(MenuView menuview) {
	activeMenuViews.addElementIfAbsent(menuview);
    }
    
    void removeActiveMenuView(MenuView menuview) {
	activeMenuViews.removeElement(menuview);
    }
    
    public void willProcessEvent(Event event) {
	/* empty */
    }
    
    void willProcessInternalEvent(Event event) {
	if (activeMenuViews.count() != 0 && event.type() == -1) {
	    MenuView menuview = (MenuView) activeMenuViews.lastElement();
	    menuview.mouseWillDown((MouseEvent) event);
	}
    }
    
    public void didProcessEvent(Event event) {
	/* empty */
    }
    
    void didProcessInternalEvent(Event event) {
	drawAllDirtyViews();
    }
    
    protected void drawAllDirtyViews() {
	int i = rootViews.count();
	for (int i_4_ = 0; i_4_ < i; i_4_++) {
	    RootView rootview = (RootView) rootViews.elementAt(i_4_);
	    rootview.drawDirtyViews();
	    rootview._updateCursorAndMoveView();
	}
    }
    
    boolean isMac() {
	String string = System.getProperty("os.name");
	if (string != null && string.startsWith("Mac"))
	    return true;
	return false;
    }
    
    public void keyDown(KeyEvent keyevent) {
	boolean bool = false;
	RootView rootview = firstRootView();
	if (rootview != null)
	    bool = rootview.processKeyboardEvent(keyevent, false);
    }
    
    public void keyTyped(KeyEvent keyevent) {
	/* empty */
    }
    
    public void keyUp(KeyEvent keyevent) {
	/* empty */
    }
    
    protected void beginModalSessionForView(View view) {
	if (view == null)
	    throw new InconsistencyException
		      ("beginModalSessionForView called with null view");
	_modalVector.addElement(view);
	RootView rootview = view.rootView();
	if (rootview != null)
	    rootview.updateCursor();
    }
    
    protected void endModalSessionForView(View view) {
	if (view != _modalVector.lastElement())
	    throw new InconsistencyException
		      ("endModalSessionForView called for a view that is not the last modal view");
	_modalVector.removeLastElement();
	RootView rootview = view.rootView();
	if (rootview != null) {
	    rootview.updateCursor();
	    rootview.validateSelectedView();
	}
    }
    
    public View modalView() {
	if (_modalVector.count() > 0)
	    return (View) _modalVector.lastElement();
	return null;
    }
    
    boolean isModalViewShowing() {
	if (_modalVector.count() == 0)
	    return false;
	return true;
    }
    
    public void performCommandAndWait(Target target, String string,
				      Object object) {
	CommandEvent commandevent = new CommandEvent(target, string, object);
	eventLoop.addEventAndWait(commandevent);
    }
    
    public void performCommandLater(Target target, String string,
				    Object object, boolean bool) {
	CommandEvent commandevent = new CommandEvent(target, string, object);
	if (bool)
	    eventLoop.filterEvents(new CommandFilter(commandevent.target,
						     commandevent.command,
						     object));
	eventLoop.addEvent(commandevent);
    }
    
    public void performCommandLater(Target target, String string,
				    Object object) {
	performCommandLater(target, string, object, false);
    }
    
    protected FoundationApplet createApplet() {
	Frame frame = new Frame();
	FoundationApplet foundationapplet = new FoundationApplet();
	frame.add(foundationapplet);
	frame.addNotify();
	foundationapplet.addNotify();
	foundationapplet.setStub(new FoundationAppletStub());
	return foundationapplet;
    }
    
    String exceptionHeader() {
	return "Uncaught exception.  IFC release: IFC 1.1.2";
    }
    
    public void addObserver(ApplicationObserver applicationobserver) {
	observers.addElementIfAbsent(applicationobserver);
    }
    
    public void removeObserver(ApplicationObserver applicationobserver) {
	observers.removeElement(applicationobserver);
    }
    
    public void appletStarted() {
	int i = observers.count();
	isPaused = false;
	while (i-- > 0) {
	    ApplicationObserver applicationobserver
		= (ApplicationObserver) observers.elementAt(i);
	    applicationobserver.applicationDidResume(this);
	}
	i = rootViews.count();
	while (i-- > 0) {
	    RootView rootview = (RootView) rootViews.elementAt(i);
	    if (rootview.externalWindow() == null)
		rootview.setVisible(true);
	}
    }
    
    public void appletStopped() {
	int i = observers.count();
	isPaused = true;
	while (i-- > 0) {
	    ApplicationObserver applicationobserver
		= (ApplicationObserver) observers.elementAt(i);
	    applicationobserver.applicationDidPause(this);
	}
	i = rootViews.count();
	while (i-- > 0) {
	    RootView rootview = (RootView) rootViews.elementAt(i);
	    if (rootview.externalWindow() == null)
		rootview.setVisible(false);
	}
    }
    
    void applicationDidStart() {
	int i = observers.count();
	while (i-- > 0) {
	    ApplicationObserver applicationobserver
		= (ApplicationObserver) observers.elementAt(i);
	    applicationobserver.applicationDidStart(this);
	}
    }
    
    void applicationDidStop() {
	int i = observers.count();
	while (i-- > 0) {
	    ApplicationObserver applicationobserver
		= (ApplicationObserver) observers.elementAt(i);
	    applicationobserver.applicationDidStop(this);
	}
    }
    
    public void processEvent(Event event) {
	if (event instanceof ApplicationEvent) {
	    if (event.type == -26)
		appletStopped();
	    else if (event.type == -27)
		appletStarted();
	}
    }
    
    public boolean isRunning() {
	return eventLoop.isRunning();
    }
    
    public boolean isPaused() {
	return isPaused;
    }
    
    static Clipboard clipboard() {
	synchronized (clipboardLock) {
	    if (clipboard == null) {
		clipboard = JDK11AirLock.clipboard();
		if (clipboard == null)
		    clipboard = new TextBag();
	    }
	    return clipboard;
	}
    }
    
    public void makeCurrentDocumentWindow(Window window) {
	if (window != null && !window.containsDocument())
	    throw new InconsistencyException
		      ("makeCurrentDocumentWindow: window is not a document");
	if (currentDocumentWindow != null) {
	    currentDocumentWindow.didResignCurrentDocument();
	    currentDocumentWindow = null;
	}
	if (window != null) {
	    currentDocumentWindow = window;
	    currentDocumentWindow.didBecomeCurrentDocument();
	}
	int i = observers.count();
	while (i-- > 0) {
	    ApplicationObserver applicationobserver
		= (ApplicationObserver) observers.elementAt(i);
	    try {
		applicationobserver
		    .currentDocumentDidChange(this, currentDocumentWindow);
	    } catch (IncompatibleClassChangeError incompatibleclasschangeerror) {
		/* empty */
	    }
	}
    }
    
    public Window currentDocumentWindow() {
	return currentDocumentWindow;
    }
    
    public void chooseNextCurrentDocumentWindow(Window window) {
	Window window_5_ = null;
	if (window == null || window instanceof ExternalWindow) {
	    window_5_ = _chooseNextExternalWindowWithDocument((ExternalWindow)
							      window);
	    if (window_5_ == null && mainRootView() != null)
		window_5_
		    = _chooseNextInternalWindowWithDocument(mainRootView(),
							    null);
	} else if (((InternalWindow) window).rootView() != null)
	    window_5_ = _chooseNextInternalWindowWithDocument(((InternalWindow)
							       window)
								  .rootView(),
							      ((InternalWindow)
							       window));
	makeCurrentDocumentWindow(window_5_);
    }
    
    Window _chooseNextInternalWindowWithDocument
	(RootView rootview, InternalWindow internalwindow) {
	Vector vector = rootview.internalWindows();
	for (int i = vector.count() - 1; i >= 0; i--) {
	    InternalWindow internalwindow_6_
		= (InternalWindow) vector.elementAt(i);
	    if (internalwindow_6_ != internalwindow
		&& internalwindow_6_.containsDocument())
		return internalwindow_6_;
	}
	return null;
    }
    
    Window _chooseNextExternalWindowWithDocument
	(ExternalWindow externalwindow) {
	Vector vector = externalWindows();
	for (int i = vector.count() - 1; i >= 0; i--) {
	    ExternalWindow externalwindow_7_
		= (ExternalWindow) vector.elementAt(i);
	    if (externalwindow_7_ != externalwindow
		&& externalwindow_7_.containsDocument())
		return externalwindow_7_;
	}
	return null;
    }
    
    void focusChanged(View view) {
	int i = observers.count();
	while (i-- > 0) {
	    ApplicationObserver applicationobserver
		= (ApplicationObserver) observers.elementAt(i);
	    try {
		applicationobserver.focusDidChange(this, view);
	    } catch (IncompatibleClassChangeError incompatibleclasschangeerror) {
		/* empty */
	    }
	}
    }
    
    KeyboardArrow keyboardArrow() {
	if (keyboardArrow == null)
	    keyboardArrow = new KeyboardArrow();
	return keyboardArrow;
    }
    
    public int keyboardArrowPosition(View view) {
	RootView rootview = view.rootView();
	Rect rect = rootview.localBounds();
	Point point = new Point();
	Rect rect_8_ = new Rect();
	view.computeVisibleRect(rect_8_);
	rect_8_.intersectWith(view.keyboardRect());
	view.convertRectToView(rootview, rect_8_, rect_8_);
	for (int i = 0; i <= 3; i++) {
	    Image image = keyboardArrowImage(i);
	    Point point_9_ = keyboardArrowHotSpot(i);
	    point = keyboardArrowLocation(view, i);
	    point.x -= point_9_.x;
	    point.y -= point_9_.y;
	    if (rect.contains(new Rect(point.x, point.y, image.width(),
				       image.height())))
		return i;
	}
	return 0;
    }
    
    public Image keyboardArrowImage(int i) {
	switch (i) {
	case 0:
	    return Bitmap.bitmapNamed("netscape/application/topLeftArrow.gif");
	case 2:
	    return Bitmap
		       .bitmapNamed("netscape/application/topRightArrow.gif");
	case 3:
	    return (Bitmap.bitmapNamed
		    ("netscape/application/bottomRightArrow.gif"));
	case 1:
	    return (Bitmap.bitmapNamed
		    ("netscape/application/bottomLeftArrow.gif"));
	default:
	    return null;
	}
    }
    
    public Point keyboardArrowHotSpot(int i) {
	switch (i) {
	case 0:
	    return new Point(8, 12);
	case 2:
	    return new Point(0, 12);
	case 3:
	    return new Point(0, 0);
	case 1:
	    return new Point(8, 0);
	default:
	    return null;
	}
    }
    
    public Point keyboardArrowLocation(View view, int i) {
	RootView rootview = view.rootView();
	Rect rect = rootview.localBounds();
	Rect rect_10_ = view.localBounds();
	rect_10_ = new Rect();
	view.computeVisibleRect(rect_10_);
	rect_10_.intersectWith(view.keyboardRect());
	if (rect_10_.width == 0 || rect_10_.height == 0)
	    return new Point(2147483647, 2147483647);
	view.convertRectToView(view.rootView(), rect_10_, rect_10_);
	switch (i) {
	case 0:
	    return new Point(rect_10_.x, rect_10_.y);
	case 2:
	    return new Point(rect_10_.x + rect_10_.width, rect_10_.y);
	case 1:
	    return new Point(rect_10_.x, rect_10_.y + rect_10_.height);
	case 3:
	    return new Point(rect_10_.x + rect_10_.width,
			     rect_10_.y + rect_10_.height);
	default:
	    throw new InconsistencyException("Unknown position " + i);
	}
    }
    
    public void setKeyboardUIEnabled(boolean bool) {
	_kbdUIEnabled = bool;
    }
    
    public boolean isKeyboardUIEnabled() {
	return _kbdUIEnabled;
    }
    
    public Applet applet() {
	return applet;
    }
    
    public AppletResources appletResources() {
	return _appResources;
    }
    
    public static String clipboardText() {
	return clipboard().text();
    }
    
    public static void setClipboardText(String string) {
	clipboard().setText(string);
    }
    
    public void setHandleJDK11MouseEvents(boolean bool) {
	jdkMouseEventHackEnabled = bool;
    }
    
    public boolean handleJDK11MouseEvents() {
	return jdkMouseEventHackEnabled;
    }
    
    public boolean handleExtendedKeyEvent() {
	return false;
    }
    
    public Class classForName(String string) throws ClassNotFoundException {
	Applet applet = applet();
	if (applet instanceof FoundationApplet)
	    return ((FoundationApplet) applet).classForName(string);
	return Class.forName(string);
    }
}
