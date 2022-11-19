/* PlaywriteRoot - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import COM.stagecast.creator.PlatformStartup;
import COM.stagecast.ifc.netscape.application.AWTCompatibility;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.ContainerView;
import COM.stagecast.ifc.netscape.application.Event;
import COM.stagecast.ifc.netscape.application.FileChooser;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.FoundationApplet;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.Menu;
import COM.stagecast.ifc.netscape.application.MenuItem;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.application.Window;
import COM.stagecast.ifc.netscape.application.WindowOwner;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;
import COM.stagecast.unaryoperators.UnaryOperators;

public abstract class PlaywriteRoot extends Application
    implements Debug.Constants, PlaywriteSystem.Properties,
	       ResourceIDs.AboutWindowIDs, ResourceIDs.CommandIDs,
	       ResourceIDs.DialogIDs, ResourceIDs.RegistrationIDs,
	       ResourceIDs.RootIDs, ResourceIDs.SplashScreenIDs,
	       ResourceIDs.TutorialSplashScreenIDs, ResourceIDs.WorldViewIDs,
	       Target, WindowOwner
{
    protected static final int MAGIC_NUMBER = 41497;
    protected static final boolean NON_ROMAN_BUILD = false;
    protected static final String DEVELOPMENT_RELEASE = "d";
    protected static final String ALPHA_RELEASE = "a";
    protected static final String BETA_RELEASE = "b";
    protected static final String PRE_RELEASE = "EA";
    protected static final String FINAL_RELEASE = "f";
    protected static final int STD_CONFIG = 0;
    protected static final int ENABLE_EVAL_LIMITS = 1;
    protected static final int ENABLE_SAVE_RESTRICTIONS = 2;
    protected static final int ENABLE_DEMO_TIMEOUT = 4;
    static final int PRERELEASE_TIMEOUT_MONTH = 4;
    static final int PRERELEASE_TIMEOUT_DAY = 1;
    static final int PRERELEASE_TIMEOUT_YEAR = 2002;
    static final int DEMO_ACTIVE_DAYS = 120;
    static final int DEMO_WARNING_DAYS = 15;
    public static final int EVAL_RULE_LIMIT = 10;
    public static final int EVAL_PROTOTYPE_LIMIT = 3;
    public static final int EVAL_SPECIAL_LIMIT = 3;
    public static final int EVAL_STAGE_LIMIT = 2;
    public static final String NEW_WORLD = "NW";
    public static final String OPEN_WORLD = "OW";
    public static final String OPEN_SPECIFIED_WORLD = "OSW";
    public static final String OPEN_TUTORIAL = "OT";
    public static final String PRINT_WORLD = "PW";
    public static final String SHOW_ABOUT = "SA";
    public static final String EXIT = "EX";
    static final String WARNING = "wng";
    static final String SHOW_MODAL_DIALOG = "show modal dialog";
    static final String REGISTERED_USER = "RegisteredUser";
    static final String SERIAL_NUMBER = "SerialNumber";
    static final String EVAL_FIRST_USE = "EvalFirstUse";
    static int[][] SPLASH_BUTTON_LOC
	= { { 304, 191 }, { 312, 235 }, { 320, 279 }, { 329, 323 } };
    static final int SPLASH_BUTTON_LABEL_COLOR = 203;
    static final int SPLASH_BUTTON_LABEL_FONT_SIZE = 24;
    private static final int MAX_OPEN_WORLDS = 20000;
    private static final Color BACKGROUND_COLOR = Color.black;
    private static final int OBJECT_STORE_VERSION = 2;
    private static final MessageFormat PREFS_FILE_TEMPLATE
	= new MessageFormat("{0}.prefs");
    private static Boolean _isServer = null;
    private static Object initLock = new Object();
    private static int instanceCount = 0;
    private static int authoringInstanceCount = 0;
    private static byte[] preAlloc = null;
    private static Hashtable applicationCloset = new Hashtable();
    private static AppearanceEditorController appearanceEditorController
	= null;
    private ToolSession _activeTool = null;
    protected Vector _worlds = new Vector(2);
    protected File _initialWorld = null;
    private Vector _characterExtensions = new Vector(4);
    private Vector _extensionMenuBuilders = new Vector(4);
    private ClassRegistry _classRegistry = new ClassRegistry();
    private InternalWindow _splashScreenWindow = null;
    private Bitmap _splashScreen;
    private View _splashView = null;
    private TutorialSplashScreen _tutorialSplashScreen = null;
    private RegistrationDialog _registrationDialog = null;
    private Label _copyrightNotice;
    private Label _trademarkNotice;
    private PlaywriteWindow _aboutWindow = null;
    private PlaywriteWindow _aboutPluginWindow = null;
    private ProgressDialog _progress = null;
    private int _progressDefaultIncr;
    private int _maxCycles = -1;
    private Worldly _badWorldly = null;
    private boolean _wantsSync = false;
    private long _lastMouseDownTime = 0L;
    private Object _lastMouseDownObject = null;
    private String _lastWarningMsg = null;
    private Thread _eventThread;
    private Date _launchDate = null;
    private boolean _validated = false;
    private Properties _prefs = null;
    private File _prefsFile = null;
    private static KeyEvent _lastCommandKeyEvent;
    private static String _pasteBoard;
    private static String _lastDeletion;
    private boolean _inWindowWillHide = false;
    PlaywriteLoader _loader;
    private Hashtable _classToEditorMap = new Hashtable(10);
    private Vector _editors = new Vector();
    private ProtocolFactory _protocolFactory = new ProtocolFactory();
    private TempFileChunkManager _tempManager;
    private int _newWorldCounter = 0;
    private boolean _isClosing = false;
    private static String[] classesToLoad
	= { "World", "WorldView", "WorldWindow", "Stage", "BoardView",
	    "ControlPanelView", "SidelineView", "DrawerWindow",
	    "CharacterInstance", "CharacterView", "GeneralizedCharacter",
	    "DisplayVariable", "Rule", "BindTest", "MoveAction",
	    "CommentScrap", "Tool", "ToolView", "ToolButton", "Selection",
	    "ToolTipWindow" };
    private static final String MOD34 = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final int[][] offsets
	= { { 1, 2, 5, 0, 4, 3 }, { 1, 4, 0, 5, 3, 2 }, { 1, 4, 2, 3, 0, 5 },
	    { 1, 0, 3, 5, 2, 4 }, { 1, 3, 2, 4, 5, 0 } };
    
    public static final String getVersionString() {
	return app()._getVersionString();
    }
    
    public static final String getVersionNumber() {
	String vn = app()._getVersionNumber();
	if (hasDemoRestrictions())
	    vn += " EVAL";
	return vn;
    }
    
    public static final String getCompatibleVersionNumber() {
	return app()._getCompatibleVersionNumber();
    }
    
    public static final String getProductName() {
	return "Stagecast " + getShortProductName();
    }
    
    public static final String getShortProductName() {
	return app()._getProductName();
    }
    
    public static final String getProductType() {
	return app()._getProductType();
    }
    
    public static final String getBuildString() {
	return app()._getBuildString();
    }
    
    public static final boolean isFinalBuild() {
	return ("f".equals(app()._getReleaseType())
		|| Debug.lookup("debug.final"));
    }
    
    public static final boolean isCustomerBuild() {
	return app()._isCustomerBuild();
    }
    
    public static final int getEvalFlags() {
	return isAuthoring() ? app()._getEvalFlags() : 0;
    }
    
    public static final boolean hasAuthoringLimits() {
	return (getEvalFlags() & 0x1) != 0;
    }
    
    public static final boolean hasSaveRestrictions() {
	return (getEvalFlags() & 0x2) != 0;
    }
    
    public static final boolean hasTimeout() {
	return (getEvalFlags() & 0x4) != 0;
    }
    
    public static final boolean hasDemoRestrictions() {
	return getEvalFlags() != 0;
    }
    
    public static final boolean isSerialProtected() {
	return app()._isSerialProtected();
    }
    
    public static final TempFileChunkManager getTempManager() {
	return app()._tempManager;
    }
    
    protected abstract String _getVersionString();
    
    protected abstract String _getCompatibleVersionNumber();
    
    protected abstract String _getVersionNumber();
    
    protected abstract String _getProductName();
    
    protected abstract String _getProductType();
    
    protected abstract String _getReleaseType();
    
    protected abstract String _getBuildString();
    
    protected abstract boolean _isCustomerBuild();
    
    protected abstract int _getEvalFlags();
    
    protected abstract boolean _isSerialProtected();
    
    protected abstract void applicationExec();
    
    protected String preferredAppearanceEditor() {
	return "COM.stagecast.playwrite.NullAppearanceEditor";
    }
    
    protected boolean getAuthoringFlag() {
	return false;
    }
    
    protected boolean getServerFlag() {
	return false;
    }
    
    protected boolean getProfessionalFlag() {
	return false;
    }
    
    protected int getMaxOpenWorlds() {
	return 1;
    }
    
    public static final PlaywriteRoot app() {
	return (PlaywriteRoot) Application.application();
    }
    
    public static final boolean isAuthoring() {
	return app().getAuthoringFlag();
    }
    
    public static final boolean isPlayer() {
	return isAuthoring() ^ true;
    }
    
    public static final boolean isServer() {
	if (_isServer == null)
	    _isServer = new Boolean(app().getServerFlag());
	return _isServer.booleanValue();
    }
    
    public static final boolean isProfessional() {
	return PlaywriteSystem.getApplicationPropertyAsBoolean("pro_versioon",
							       false);
    }
    
    static final ToolSession getActiveTool() {
	return app()._activeTool;
    }
    
    static final void setActiveTool(ToolSession tool) {
	app()._activeTool = tool;
    }
    
    static final String getLastWarning() {
	return app()._lastWarningMsg;
    }
    
    static final void setLastWarning(String msg) {
	app()._lastWarningMsg = msg;
    }
    
    public static final ClassRegistry getClassRegistry() {
	return app()._classRegistry;
    }
    
    static final int getObjectStoreVersion() {
	return 2;
    }
    
    public static final boolean isApplication() {
	return Application.application().isApplet() ^ true;
    }
    
    public static final Vector getWorlds() {
	return app()._worlds;
    }
    
    public static final Date getLaunchDateTime() {
	return app()._launchDate;
    }
    
    public static final ProtocolFactory getProtocolFactory() {
	return app()._protocolFactory;
    }
    
    static final RuleEditor getRuleEditor() {
	if (isAuthoring())
	    return RuleEditor.getRuleEditor();
	return null;
    }
    
    static final void setBadWorldly(Worldly badWorldly) {
	app()._badWorldly = badWorldly;
    }
    
    static final void setWantsSync(boolean sync) {
	app()._wantsSync = sync;
    }
    
    static final KeyEvent getLastCommandKeyEvent() {
	return _lastCommandKeyEvent;
    }
    
    static final String getPasteBoard() {
	return _pasteBoard;
    }
    
    static final void setPasteBoard(String pasteBoard) {
	_pasteBoard = pasteBoard;
    }
    
    static final String getLastDeletion() {
	return _lastDeletion;
    }
    
    static final void setLastDeletion(String lastDeletion) {
	_lastDeletion = lastDeletion;
    }
    
    public static final void putInCloset(String key, Object value) {
	applicationCloset.put(key, value);
    }
    
    public static final Object lookInCloset(String key) {
	return applicationCloset.get(key);
    }
    
    public static final Object removeFromCloset(String key) {
	return applicationCloset.remove(key);
    }
    
    public static final AppearanceEditorController getAppearanceEditorController
	() {
	return appearanceEditorController;
    }
    
    protected void finalize() throws Throwable {
	try {
	    super.finalize();
	} finally {
	    String tempFilePath = FileIO.getTempDir();
	    if (tempFilePath != null) {
		File tempFileDirectory = new File(tempFilePath);
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
			return (name.startsWith("CREATOR.")
				&& name.endsWith(".tmp"));
		    }
		};
		String[] tempFiles = tempFileDirectory.list(filter);
		for (int i = 0; i < tempFiles.length; i++) {
		    File tempFile = new File(tempFileDirectory, tempFiles[i]);
		    boolean success = tempFile.delete();
		    if (success == false)
			Debug.print
			    (true,
			     ("PlaywriteRoot.finalize(): Unable to delete "
			      + tempFile.getAbsolutePath()));
		}
	    }
	}
    }
    
    public static final void markBusy() {
	PlaywriteRoot root = app();
	if (root.mainRootView() != null)
	    root.mainRootView().setOverrideCursor(3);
    }
    
    public static final void clearBusy() {
	PlaywriteRoot root = app();
	if (root.mainRootView() != null)
	    root.mainRootView().removeOverrideCursor();
    }
    
    public static final void openProgress(String titleID) {
	if (!isServer()) {
	    PlaywriteRoot root = app();
	    if (root._progress != null)
		root._progress.hide();
	    root._progress
		= new ProgressDialog(200, 50, Resource.getText(titleID));
	    root._progress.setFillColor(new Color(50, 90, 140));
	    root._progress.show();
	}
    }
    
    public static final int getProgress() {
	PlaywriteRoot root = app();
	if (root._progress == null)
	    return 0;
	return root._progress.getPercentDone();
    }
    
    static final ProgressDialog getProgressDialog() {
	PlaywriteRoot root = app();
	return root._progress;
    }
    
    public static final void setProgress(int percent) {
	PlaywriteRoot root = app();
	if (root._progress != null)
	    root._progress.setPercentDone(percent);
    }
    
    public static final void incrementProgress(int percent, int max) {
	PlaywriteRoot root = app();
	if (root._progress != null) {
	    int current = root._progress.getPercentDone();
	    root._progress.setPercentDone(Math.min(max, current + percent));
	}
    }
    
    public static final void setProgressTotal(int total) {
	PlaywriteRoot root = app();
	if (root._progress != null)
	    root._progress.setTotalCount(total);
    }
    
    public static final void incrementProgress(int completed) {
	PlaywriteRoot root = app();
	if (root._progress != null)
	    root._progress.incrementTotalDone(completed);
    }
    
    public static final void setProgressDefaultIncr(int incr) {
	app()._progressDefaultIncr = incr;
    }
    
    public static final int getProgressDefaultIncr() {
	return app()._progressDefaultIncr;
    }
    
    public static final void incrementProgress() {
	incrementProgress(app()._progressDefaultIncr);
    }
    
    public static final void closeProgress() {
	PlaywriteRoot root = app();
	if (root._progress != null) {
	    root._progress.hide();
	    root._progress = null;
	}
    }
    
    public static void fatalError(Throwable t) {
	try {
	    Debug.stackTrace(t);
	    if (t instanceof OutOfMemoryError)
		BitmapManager.enforceMinCacheLimits();
	    makeFatalErrorThread().start();
	} catch (Throwable t2) {
	    t.printStackTrace();
	    t2.printStackTrace();
	    if (isApplication())
		System.exit(0);
	}
    }
    
    static final Password askForPassword() {
	PlaywriteDialog dialog
	    = new PlaywriteDialog("dialog sp", "dialog set password confirm",
				  null, false);
	if (dialog.getAnswer() != "command c") {
	    String pw = dialog.getTypedText();
	    return new Password(pw, (new Object[]
				     { Password.ATTRIBUTE_CUSTOM_ENCODE }));
	}
	return null;
    }
    
    static final boolean checkPassword(Password pwd) {
	if (pwd == null || pwd.isEmpty())
	    return true;
	PlaywriteDialog dialog = new PlaywriteDialog("dialog gp", false);
	if (dialog.getAnswer() != "command c") {
	    String pw = dialog.getTypedText();
	    if (pwd.check(pw))
		return true;
	    PlaywriteDialog.warning("dialog pi", true);
	}
	return false;
    }
    
    static void disableAllWindows(World targetWorld) {
	Vector windows = getMainRootView().internalWindows();
	for (int i = 0; i < windows.size(); i++) {
	    Object window = windows.elementAt(i);
	    if (window instanceof PlaywriteWindow) {
		PlaywriteWindow win = (PlaywriteWindow) window;
		if (win.getWorld() == targetWorld)
		    win.disable();
		else
		    win.disableWindow();
	    } else
		Debug.print("debug.world", window,
			    " is not a PlaywriteWindow");
	}
    }
    
    static void enableAllWindows(World targetWorld) {
	Vector windows = getMainRootView().internalWindows();
	for (int i = 0; i < windows.size(); i++) {
	    Object window = windows.elementAt(i);
	    if (window instanceof PlaywriteWindow) {
		PlaywriteWindow win = (PlaywriteWindow) window;
		if (win.getWorld() == targetWorld)
		    win.enable();
		else
		    win.enableWindow();
	    }
	}
    }
    
    public static final RootView getMainRootView() {
	return app().mainRootView();
    }
    
    public static Size getRootWindowSize() {
	RootView rootView = getMainRootView();
	return new Size(rootView.width(), rootView.height());
    }
    
    public static final Rect getMainRootViewBounds() {
	PlaywriteRoot root = app();
	RootView rv = root.mainRootView();
	if (rv == null)
	    return null;
	Rect rvBounds = root.mainRootView().bounds();
	rvBounds.sizeBy(-5, -5);
	return rvBounds;
    }
    
    public static final void updateDisplay() {
	getMainRootView().drawDirtyViews();
    }
    
    protected FoundationApplet createApplet() {
	FoundationApplet applet = new PWFoundationApplet();
	Frame awtFrame = new Frame();
	awtFrame.add(applet);
	awtFrame.addNotify();
	applet.addNotify();
	applet.setStub(new PWFoundationAppletStub());
	return applet;
    }
    
    protected void main(String[] args, int magic) {
	try {
	    _main(args, magic);
	} catch (Throwable t) {
	    if (t instanceof ThreadDeath)
		throw (ThreadDeath) t;
	    t.printStackTrace();
	    Application application = Application.application();
	    if (application != null && application.mainRootView() != null)
		new FatalErrorAWTDialog(AWTCompatibility.awtFrameForRootView
					(application.mainRootView()));
	    else
		new FatalErrorAWTDialog(new Frame());
	}
    }
    
    private void _main(String[] args, int magic) {
	if (magic != 41497)
	    quit();
	else {
	    if (PlaywriteSystem.isMRJ_2_0())
		Compiler.disable();
	    PlaywriteSystem.loadProperties();
	    String className = (PlaywriteSystem.isMacintosh()
				? "COM.stagecast.creator.MacStartup" : "");
	    className
		= PlaywriteSystem.getApplicationProperty("native_startup",
							 className);
	    if (className.length() > 0
		&& !className.toUpperCase().equals("NONE")) {
		try {
		    Class startupClass = Class.forName(className);
		    PlatformStartup starter
			= (PlatformStartup) startupClass.newInstance();
		    starter.platformInit();
		} catch (Throwable t) {
		    Debug.stackTrace(t);
		}
	    }
	    if (PlaywriteSystem.isMacintosh())
		preAlloc = new byte[4194304];
	    if (!isServer())
		setupRootView();
	    if (args.length > 0) {
		_initialWorld = new File(args[0]);
		if (!_initialWorld.exists())
		    _initialWorld = null;
	    }
	    run();
	}
    }
    
    public void setupRootView() {
	COM.stagecast.ifc.netscape.application.ExternalWindow drawSpace
	    = new PWExternalWindow();
	drawSpace.setOwner(app());
	app().setMainRootView(drawSpace.rootView());
	Size size = PlaywriteSystem.getScreenSize();
	String winSize
	    = PlaywriteSystem.getApplicationProperty("window_size_override",
						     "");
	if (winSize != null && winSize.length() > 5) {
	    int comma = winSize.indexOf(",");
	    String swidth = winSize.substring(0, comma);
	    String sheight = winSize.substring(comma + 1);
	    size.width = Integer.parseInt(swidth);
	    size.height = Integer.parseInt(sheight);
	}
	drawSpace.setTitle(Resource.getText(getProductName()));
	drawSpace.sizeTo(size.width, size.height);
	drawSpace.setOwner(app());
	drawSpace.show();
	if (isProfessional())
	    displaySplashScreen(false);
	else
	    openProgress("startup progress bar");
    }
    
    protected void drawAllDirtyViews() {
	super.drawAllDirtyViews();
	if (_wantsSync) {
	    PlaywriteSystem.getToolkit().sync();
	    _wantsSync = false;
	}
    }
    
    public void init() {
	if (PlaywriteSystem.isMRJ_2_0())
	    Compiler.disable();
	synchronized (initLock) {
	    try {
		_launchDate = new Date();
		instanceCount++;
		if (isAuthoring())
		    authoringInstanceCount++;
		super.init();
		this.setKeyboardUIEnabled(false);
		checkMaxCycle();
		Debug.print("debug.world", "init() called " + instanceCount);
		setProgress(10);
		if (!isServer()) {
		    checkScreenDepth();
		    if (this.mainRootView() != null)
			this.mainRootView().setColor(BACKGROUND_COLOR);
		}
		preAlloc = null;
		Util.suggestGC();
		fileSystemInit();
		_tempManager = new TempFileChunkManager();
	    } catch (ExceptionInInitializerError e) {
		Debug.print(true, "init exception: ", e);
		Throwable t = e.getException();
		t.printStackTrace();
		System.exit(0);
	    }
	    if (instanceCount == 1) {
		StorageProxy.registerHelper(BuiltinProxyTable.helper);
		try {
		    BitmapManager.initStatics();
		    Util.initStatics();
		    setProgress(20);
		    PlaywriteSound.initStatics();
		    setProgress(30);
		    BackgroundImage.initStatics();
		    World.initStatics();
		    setProgress(50);
		    UnaryOperators.initStatics();
		} catch (ExceptionInInitializerError e) {
		    Debug.print(true, "init exception: ", e);
		    Throwable t = e.getException();
		    t.printStackTrace();
		    System.exit(0);
		}
		if (!isServer()) {
		    ColorValue.initStatics();
		    Tool.initStatics();
		    ToolButton.initStatics();
		    ScrollableArea.initStatics();
		    PlaywriteBorder.initStatics();
		}
	    }
	    if (isAuthoring() && authoringInstanceCount == 1) {
		AfterBoardHandle.initStatics();
		Slot.initStatics();
		CharacterWindow.initStatics();
		Tutorial.initStatics();
		RuleEditor.initStatics();
		RuleEditor.initExtensions();
		loadAppearanceEditor();
	    }
	    if (instanceCount == 1) {
		try {
		    Appearance.initStatics();
		    CocoaCharacter.initStatics();
		    Stage.initStatics();
		    setProgress(55);
		    SpecialPrototype.staticInit();
		    DoorPrototype.staticInit();
		    TextCharacterPrototype.staticInit();
		    ObjectProxy.initStatics();
		} catch (ExceptionInInitializerError e) {
		    Debug.print(true, "init exception: ", e);
		    Throwable t = e.getException();
		    t.printStackTrace();
		    System.exit(0);
		}
	    }
	    if (isAuthoring()) {
		PlaywriteWindow wind = RuleEditor.getRuleEditor();
		if (wind != null)
		    wind.setRootView(getMainRootView());
	    }
	    _classRegistry.init();
	    setProgress(60);
	    if (!isServer() && !isProfessional() && isApplication()
		&& _initialWorld == null)
		displaySplashScreen(false);
	    DoorPrototype.initExtension();
	    TextCharacterPrototype.initExtension();
	    setProgress(75);
	    if (Debug.lookup("debug.gc") || Debug.lookup("debug.memory"))
		createMemoryWindow();
	    markBusy();
	    if (!isServer()) {
		ToolTips.createToolTips();
		Selection.createSelectionObject();
	    }
	    setProgress(90);
	}
	if (isApplication()) {
	    _loader = new PlaywriteLoader();
	    try {
		_loader.registerAndLoadPlugins();
	    } catch (RecoverableException e) {
		closeProgress();
		e.showDialog();
	    }
	}
	setProgress(100);
	closeProgress();
	clearBusy();
	_validated = validateVersion();
	eventSystemInit();
	if (Debug.lookup("debug.rhino"))
	    launchJavascript();
	if (isApplication())
	    applicationExec();
    }
    
    public void appletStarted() {
	Debug.print("debug.world", "Calling 'appletStarted'");
	_isClosing = false;
	super.appletStarted();
	displayApplet();
    }
    
    public void appletStopped() {
	Debug.print("debug.world", "Calling 'appletStopped'");
	_isClosing = true;
	try {
	    RootView rv = getMainRootView();
	    Object[] windows = rv.internalWindows().elementArray();
	    for (int i = 0; i < windows.length; i++) {
		InternalWindow wind = (InternalWindow) windows[i];
		if (wind instanceof TutorialWindow)
		    ((TutorialWindow) wind).close();
		else
		    wind.hide();
	    }
	    if (isAuthoring()) {
		PlaywriteWindow wind = RuleEditor.getRuleEditor();
		if (wind != null)
		    wind.setRootView(null);
	    }
	    super.appletStopped();
	} catch (ThreadDeath td) {
	    throw td;
	} catch (Throwable t) {
	    Debug.print(true, "Error stopping applet");
	    Debug.stackTrace(t);
	}
    }
    
    public static boolean appletIsClosing() {
	return app()._isClosing && !isApplication();
    }
    
    public void run() {
	_eventThread = Thread.currentThread();
	super.run();
    }
    
    public void handleEventLoopException(Exception e) {
	if (isServer())
	    Debug.stackTrace(e);
	else
	    handleFatalException();
    }
    
    public boolean inEventThread() {
	if (_eventThread == null)
	    throw new PlaywriteInternalError("Event thread not set");
	return Thread.currentThread() == _eventThread;
    }
    
    public void performCommandAndWait(Target target, String command,
				      Object data) {
	if (inEventThread())
	    target.performCommand(command, data);
	else
	    super.performCommandAndWait(target, command, data);
    }
    
    public void cleanup() {
	Debug.print("debug.world", "Calling 'cleanup'");
	if (!isServer())
	    Selection.destroySelectionObject();
	ToolTips.destroyToolTips();
	_eventThread = null;
	super.cleanup();
    }
    
    private void loadAppearanceEditor() {
	String controllerClass = preferredAppearanceEditor();
	controllerClass
	    = PlaywriteSystem.getApplicationProperty("appearance_editor",
						     controllerClass);
	try {
	    Class cls = Class.forName(controllerClass);
	    appearanceEditorController
		= (AppearanceEditorController) cls.newInstance();
	} catch (Exception e) {
	    fatalError(e);
	}
    }
    
    private void fileSystemInit() {
	_prefs = new Properties();
	FileIO.initialize();
	loadPreferences();
    }
    
    private boolean validateVersion() {
	if (isFinalBuild()) {
	    if (isPlayer())
		return true;
	    return checkUsageClock() && checkSerialNumber();
	}
	Calendar today = Calendar.getInstance();
	Calendar expire = Calendar.getInstance();
	expire.set(2002, 3, 1);
	if (today.after(expire)) {
	    String msg
		= Resource.getTextAndFormat("dialog cto",
					    new Object[] { getProductType(),
							   getProductName() });
	    if (isServer())
		System.out.println(msg);
	    else {
		PlaywriteDialog dlg = new PlaywriteDialog(msg, "command ok");
		dlg.getAnswer();
	    }
	    return false;
	}
	return true;
    }
    
    private void eventSystemInit() {
	if (!this.isApplet()) {
	    SysEvent.initialize();
	    SysEvent.setTargetFor(SysEvent.EVENT_OPEN_FILE, this);
	    SysEvent.setTargetFor(SysEvent.EVENT_PRINT_FILE, this);
	    SysEvent.setTargetFor(SysEvent.EVENT_ABOUT_APP, this);
	    SysEvent.setTargetFor(SysEvent.EVENT_EXIT_APP, this);
	}
    }
    
    private void preloadClasses() {
	try {
	    for (int i = 0; i < classesToLoad.length; i++)
		Class.forName("COM.stagecast.playwrite." + classesToLoad[i]);
	} catch (Exception e) {
	    Debug.print(true, e);
	}
    }
    
    private void loadPreferences() {
	String prefFolder = FileIO.getUserDir();
	_prefsFile
	    = new File(prefFolder,
		       PREFS_FILE_TEMPLATE
			   .format(new Object[] { getShortProductName() }));
	_prefs = new Properties();
	try {
	    if (_prefsFile.exists()) {
		FileInputStream fis = new FileInputStream(_prefsFile);
		_prefs.load(fis);
		fis.close();
	    }
	} catch (Throwable throwable) {
	    Debug.print(true, "Unable to read prefs file");
	}
    }
    
    private void savePreferences() {
	try {
	    FileOutputStream fos = new FileOutputStream(_prefsFile);
	    _prefs.save(fos, Resource.getText("root pd"));
	    fos.close();
	    FileIO.setFileType(_prefsFile, 3);
	} catch (Throwable t) {
	    Debug.print(true, "Error writing prefs: " + t);
	}
    }
    
    String getPreference(String key, String defaultVal) {
	return _prefs.getProperty(key, defaultVal);
    }
    
    void setPreference(String key, String value) {
	_prefs.put(key, value);
	savePreferences();
    }
    
    protected void checkScreenDepth() {
	int depth = Toolkit.getDefaultToolkit().getColorModel().getPixelSize();
	if (depth == 24) {
	    Bitmap temp = BitmapManager.createBitmapManager(1, 1);
	    Graphics g = temp.createGraphics();
	    g.setColor(Color.red);
	    g.fillRect(0, 0, 1, 1);
	    g.dispose();
	    int[] pixels = new int[1];
	    boolean success = temp.grabPixels(pixels);
	    temp.flush();
	    ASSERT.isTrue(success, "grabPixels");
	    ColorModel model = ColorModel.getRGBdefault();
	    int red = model.getRed(pixels[0]);
	    int green = model.getGreen(pixels[0]);
	    int blue = model.getBlue(pixels[0]);
	    if (red - green < 100 || red - blue < 100)
		depth = 32;
	}
	Debug.print("debug.image",
		    ("PlaywriteRoot.checkScreenDepth(): Color depth = "
		     + depth));
	if (isApplication() == true && PlaywriteSystem.isWindows() == true
	    && depth == 4) {
	    Object[] params = { getProductName() };
	    PlaywriteDialog dialog
		= new PlaywriteDialog(Resource.getTextAndFormat("dialog cd1t",
								params),
				      "dialog cd2b2");
	    String answer = dialog.getAnswer();
	    System.exit(0);
	}
	boolean warnUser = false;
	warnUser = (warnUser == true ? warnUser
		    : (isApplication() == true
		       && (PlaywriteSystem.isMacintosh() ^ true) == true
		       && PlaywriteSystem.isJava_1_2_x() && depth == 8));
	warnUser = (warnUser == true ? warnUser
		    : (isApplication() == true
		       && PlaywriteSystem.isWindows() == true
		       && PlaywriteSystem.isJava_1_1_x() && depth == 8));
	warnUser = (warnUser == true ? warnUser
		    : (isApplication() == true
		       && PlaywriteSystem.isWindows() == true
		       && PlaywriteSystem.isJava_1_1_x() && depth == 32));
	if (warnUser == true) {
	    int jvmVersion = 2;
	    if (System.getProperty("java.version").equals("1.2")
		|| System.getProperty("java.version").equals("1.2.1"))
		jvmVersion = 1;
	    else if (PlaywriteSystem.isJava_1_1_x())
		jvmVersion = 0;
	    Object[] params = { getProductName(), new Integer(depth),
				new Integer(jvmVersion) };
	    PlaywriteDialog dialog
		= new PlaywriteDialog(Resource.getTextAndFormat("dialog cd2t",
								params),
				      "dialog cd2b1", "dialog cd2b2");
	    String answer = dialog.getAnswer();
	    if (answer.equals("dialog cd2b2"))
		System.exit(0);
	}
	if (isApplication() == true && PlaywriteSystem.isWindows() == true
	    && PlaywriteSystem.isJava_1_2_x()
	    && (System.getProperty("java.version").equals("1.2")
		|| System.getProperty("java.version").equals("1.2.1"))
	    && depth == 16) {
	    Object[] params = { getProductName() };
	    PlaywriteDialog dialog
		= new PlaywriteDialog(Resource.getTextAndFormat("dialog cd3t",
								params),
				      "dialog cd3b1", "dialog cd3b2");
	    String answer = dialog.getAnswer();
	    if (answer.equals("dialog cd3b2"))
		System.exit(0);
	}
    }
    
    private boolean checkUsageClock() {
	if (!hasTimeout())
	    return true;
	DateFormat dateFormat = DateFormat.getDateInstance(2);
	Calendar today = Calendar.getInstance();
	String clockStart = getPreference("EvalFirstUse", "");
	if (clockStart.equals("")) {
	    clockStart = dateFormat.format(today.getTime());
	    setPreference("EvalFirstUse", clockStart);
	    Calendar expire = (Calendar) today.clone();
	    expire.add(5, 120);
	    String msg
		= (Resource.getTextAndFormat
		   ("dialog cwe",
		    new Object[] { getProductType(), getProductName(),
				   dateFormat.format(expire.getTime()) }));
	    PlaywriteDialog dlg = new PlaywriteDialog(msg, "command ok");
	    dlg.getAnswer();
	} else {
	    ParsePosition pp = new ParsePosition(0);
	    Date firstUse = dateFormat.parse(clockStart, pp);
	    Calendar expire = Calendar.getInstance();
	    expire.setTime(firstUse);
	    expire.add(5, 120);
	    if (today.after(expire)) {
		String msg = Resource.getTextAndFormat("dialog cto",
						       (new Object[]
							{ getProductType(),
							  getProductName() }));
		PlaywriteDialog dlg = new PlaywriteDialog(msg, "command ok");
		dlg.getAnswer();
		return false;
	    }
	    Calendar expWarn = (Calendar) expire.clone();
	    expWarn.add(5, -15);
	    if (today.after(expWarn)) {
		String msg
		    = (Resource.getTextAndFormat
		       ("dialog cew",
			new Object[] { getProductType(), getProductName(),
				       dateFormat.format(expire.getTime()) }));
		PlaywriteDialog dlg = new PlaywriteDialog(msg, "command ok");
		dlg.getAnswer();
	    }
	}
	return true;
    }
    
    private boolean checkSerialNumber() {
	if (!isSerialProtected())
	    return true;
	if (!serialNumberIsValid(getPreference("SerialNumber", ""))) {
	    _registrationDialog = new RegistrationDialog();
	    _registrationDialog.showModally();
	    _registrationDialog = null;
	}
	return serialNumberIsValid(getPreference("SerialNumber", ""));
    }
    
    private static long mod34ToLong(String str) {
	long val = 0L;
	long exponent = 1L;
	int digit = 0;
	for (int i = str.length() - 1; i >= 0; i--) {
	    digit
		= "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ".indexOf(str.charAt(i));
	    if (digit < 0)
		return -1L;
	    val += (long) digit * exponent;
	    exponent *= 34L;
	}
	return val;
    }
    
    private static String longToMod34(long n) {
	long exponent = 1L;
	int digit = 0;
	StringBuffer val = new StringBuffer(8);
	if (n == 0L)
	    return "0";
	for (/**/; exponent <= n; exponent *= 34L) {
	    /* empty */
	}
	if (exponent != n)
	    exponent /= 34L;
	for (/**/; exponent >= 1L; exponent /= 34L) {
	    digit = (int) (n / exponent);
	    val.append("0123456789ABCDEFGHJKLMNPQRSTUVWXYZ"
			   .substring(digit, digit + 1));
	    n %= exponent;
	}
	return val.toString();
    }
    
    boolean serialNumberIsValid(String serial) {
	if (!isSerialProtected())
	    return true;
	serial = serial.trim().toUpperCase();
	serial = serial.replace('I', '1');
	serial = serial.replace('O', '0');
	if (serial.indexOf('-') < 0) {
	    if (serial.length() != 14 && serial.length() != 17)
		return false;
	    String temp
		= (serial.substring(0, 4) + "-" + serial.substring(4, 8) + "-"
		   + serial.substring(8, 11) + "-");
	    if (serial.length() == 14)
		temp += serial.substring(11);
	    else
		temp += serial.substring(11, 14) + "-" + serial.substring(14);
	    serial = temp;
	}
	if (serial.length() != 17 && serial.length() != 21)
	    return false;
	if (!serial.substring(4, 5).equals("-")
	    || !serial.substring(9, 10).equals("-")
	    || !serial.substring(13, 14).equals("-"))
	    return false;
	if (serial.length() == 21 && !serial.substring(17, 18).equals("-"))
	    return false;
	if (!serial.substring(0, 1).equals("C"))
	    return false;
	String ver = getVersionNumber();
	if (Integer.parseInt(serial.substring(5, 7))
	    != Integer.parseInt(ver.substring(0, ver.indexOf('.'))))
	    return false;
	String seq = serial.substring(10, 13) + serial.substring(14, 17);
	String d0 = seq.substring(1, 2);
	int[] selector = offsets[(int) (mod34ToLong(d0) % 5L)];
	d0 = seq.substring(selector[0], selector[0] + 1);
	String d1 = seq.substring(selector[1], selector[1] + 1);
	String d2 = seq.substring(selector[2], selector[2] + 1);
	String d3 = seq.substring(selector[3], selector[3] + 1);
	String d4 = seq.substring(selector[4], selector[4] + 1);
	String dc = seq.substring(selector[5], selector[5] + 1);
	long sequence = mod34ToLong(d4 + d3 + d2 + d1 + d0);
	if (sequence < 50000L)
	    return false;
	if ((sequence - 50000L) % 35L != 0L)
	    return false;
	if (serial.length() == 21) {
	    long timeout = mod34ToLong(serial.substring(18));
	    int month = (int) timeout / 100;
	    int year = 1999 + (int) timeout - month * 100;
	    Calendar targetDate = Calendar.getInstance();
	    targetDate.set(year, month - 1, 1, 0, 0, 0);
	    Date productTimeout = targetDate.getTime();
	    if (_launchDate.equals(productTimeout)
		|| _launchDate.after(productTimeout)) {
		setPreference("SerialNumber", "");
		PlaywriteDialog dlg
		    = new PlaywriteDialog("dialog sne", "command ok");
		dlg.getAnswer();
		return false;
	    }
	}
	long checkAlpha = 0L;
	for (int i = 0; i <= 9; i++) {
	    String digit = serial.substring(i, i + 1);
	    checkAlpha
		= checkAlpha + (digit.equals("-") ? 0L : mod34ToLong(digit));
	}
	long checkSequence = (mod34ToLong(d4) + mod34ToLong(d3)
			      ^ 2L + mod34ToLong(d2) + mod34ToLong(d1)
			      ^ 3L + mod34ToLong(d0));
	long checkTimeout;
	if (serial.length() == 21)
	    checkTimeout = (mod34ToLong(serial.substring(18, 19))
			    + mod34ToLong(serial.substring(19, 20))
			    + mod34ToLong(serial.substring(20, 21)));
	else
	    checkTimeout = 0L;
	long checksum = (checkAlpha + checkSequence + checkTimeout) % 34L;
	if (checksum < 0L)
	    checksum = 0L;
	if (checksum != mod34ToLong(dc))
	    return false;
	setPreference("EvalFirstUse", "");
	return true;
    }
    
    private void launchJavascript() {
	try {
	    Class scriptMain
		= Class.forName("org.mozilla.javascript.tools.shell.Main");
	    Class stringArray = (new String[0]).getClass();
	    final Method entryPoint
		= scriptMain.getMethod("main", new Class[] { stringArray });
	    new Thread(Thread.currentThread()
			   .getThreadGroup(), new Runnable() {
		public void run() {
		    try {
			entryPoint.invoke(null,
					  new Object[] { new String[0] });
		    } catch (Exception exception) {
			/* empty */
		    }
		}
	    }, "Javascript shell").start();
	} catch (Throwable t) {
	    Debug.print(true, "Unable to start Javascript: ", t);
	}
    }
    
    public final boolean handleExtendedKeyEvent() {
	return true;
    }
    
    public void willProcessEvent(Event event) {
	RootView rootView = this.mainRootView();
	InternalWindow mainWindow = rootView.mainWindow();
	if (event instanceof KeyEvent && event.type() == -11) {
	    KeyEvent keyEvent = (KeyEvent) event.clone();
	    boolean worldIsMainWindow = false;
	    boolean isCommandKey;
	    if (PlaywriteSystem.isMacintosh())
		isCommandKey = keyEvent.isMetaKeyDown() && keyEvent.key > 32;
	    else
		isCommandKey = keyEvent.isControlKeyDown();
	    if ((keyEvent.key == 127 || keyEvent.key == 8)
		&& this.modalView() == null)
		Selection.deleteSelection();
	    else if (isCommandKey && this.modalView() == null
		     && keyEvent.type() == -11) {
		ToolTips.resetTootTips();
		World world = null;
		if (mainWindow != null
		    && mainWindow instanceof PlaywriteWindow) {
		    world = ((PlaywriteWindow) mainWindow).getWorld();
		    if (mainWindow instanceof WorldWindow)
			worldIsMainWindow = true;
		}
		if (world == null && _worlds.size() == 1)
		    world = (World) _worlds.firstElement();
		keyEvent.key = Character.toUpperCase((char) keyEvent.key);
		if (!keyEvent.isControlKeyDown()) {
		    keyEvent.modifiers = keyEvent.modifiers | 0x2;
		    keyEvent.key -= 64;
		}
		int normalizedCommandKey = keyEvent.key + 64;
		boolean processed = false;
		switch (normalizedCommandKey) {
		case 87:
		case 119:
		    if (!worldIsMainWindow && mainWindow != null) {
			if (mainWindow instanceof PlaywriteWindow
			    && !(mainWindow instanceof TutorialWindow)) {
			    if (((PlaywriteWindow) mainWindow).hasClosed())
				break;
			    rootView.setFocusedView(null);
			    Application.application().performCommandLater
				(mainWindow, PlaywriteWindow.CLOSE, null,
				 true);
			}
		    } else if (world != null && isApplication())
			world.performCommand("command cw", null);
		    processed = true;
		    break;
		case 78:
		case 110:
		    if (_splashScreenWindow != null
			&& _splashScreenWindow.isVisible() && isAuthoring()) {
			performCommand("NW", null);
			processed = true;
		    }
		    break;
		case 79:
		case 111:
		    if (_splashScreenWindow != null
			&& _splashScreenWindow.isVisible()) {
			performCommand("OW", null);
			processed = true;
		    }
		    break;
		case 81:
		case 113:
		    if (isApplication()) {
			rootView.setFocusedView(null);
			quit();
			processed = true;
		    }
		    break;
		}
		if (!processed) {
		    _lastCommandKeyEvent = keyEvent;
		    if (world != null)
			world.getSystemMenu().handleCommandKeyEvent(keyEvent);
		    else if (_worlds.size() == 2) {
			Menu menu
			    = ((World) _worlds.firstElement()).getSystemMenu();
			for (int i = 0; i < menu.itemCount(); i++) {
			    MenuItem item = menu.itemAt(i);
			    if (item.commandKey() == normalizedCommandKey) {
				PlaywriteDialog.warning
				    (Resource.getText
				     ("dialog no world window active"));
				break;
			    }
			}
		    }
		}
	    }
	}
    }
    
    public void didProcessEvent(Event event) {
	if (event instanceof KeyEvent) {
	    _lastCommandKeyEvent = null;
	    KeyEvent keyEvent = (KeyEvent) event;
	    if (keyEvent.type() != -12) {
		if (keyEvent.key != 0) {
		    app();
		    InternalWindow mainWindow = getMainRootView().mainWindow();
		    if (mainWindow instanceof PlaywriteWindow) {
			ScrollableArea mainScroller
			    = ((PlaywriteWindow) mainWindow).getMainScroller();
			if (mainScroller != null)
			    mainScroller.keyDown(keyEvent);
		    }
		}
	    }
	}
    }
    
    public static void registerCharacterPrototype(Class protoClass) {
	if (CharacterPrototype.class.isAssignableFrom(protoClass))
	    app()._characterExtensions.addElementIfAbsent(protoClass);
	else
	    throw new PlaywriteInternalError("Illegal registration: "
					     + protoClass
					     + " is not a legal prototype");
    }
    
    public static void deregisterPrototype(Class protoClass) {
	boolean removed = app()._characterExtensions.removeElement(protoClass);
	ASSERT.isTrue(removed);
    }
    
    void addRegisteredPrototypes(World world) {
	Vector classesInUse = new Vector(10);
	Enumeration specials = world.getSpecialPrototypes().getContents();
	while (specials.hasMoreElements())
	    classesInUse.addElementIfAbsent(specials.nextElement().getClass());
	Enumeration protoClasses = _characterExtensions.elements();
	while (protoClasses.hasMoreElements()) {
	    Class cls = (Class) protoClasses.nextElement();
	    if (!classesInUse.contains(cls)) {
		try {
		    CharacterPrototype cp
			= (CharacterPrototype) cls.newInstance();
		    cp.init(world);
		} catch (Exception e) {
		    Debug.print(true, "Couldn't create an instance of ", cls);
		    Debug.stackTrace(e);
		    if (e instanceof InvocationTargetException) {
			Debug.print(true, "TargetException: ");
			Debug.stackTrace(((InvocationTargetException) e)
					     .getTargetException());
		    }
		}
	    }
	}
    }
    
    public static void registerExtensionMenuBuilder(MenuBuilder mb) {
	app()._extensionMenuBuilders.addElementIfAbsent(mb);
    }
    
    public static Vector getExtensionMenuBuilders() {
	return app()._extensionMenuBuilders;
    }
    
    public Vector registerEditor(EditorController editorController) {
	Vector result = null;
	ASSERT.isNotNull(editorController);
	Class[] editedClasses = editorController.getEditedClasses();
	for (int i = 0; i < editedClasses.length; i++) {
	    Class editedClass = editedClasses[i];
	    if (_classToEditorMap.get(editedClass) == null)
		_classToEditorMap.put(editedClass, editorController);
	    else {
		if (result == null)
		    result = new Vector();
		result.addElementIfAbsent(editedClass);
	    }
	}
	_editors.addElementIfAbsent(editorController);
	return result;
    }
    
    public boolean activateEditorFor(PlaywriteView view) {
	boolean result = false;
	Object modelObject = view.getModelObject();
	if (modelObject != null) {
	    EditorController editorController
		= getEditorController(modelObject);
	    if (editorController != null) {
		EditorController.Editor editor
		    = editorController.getEditorFor(modelObject);
		if (editor instanceof InternalWindow) {
		    InternalWindow edwin = (InternalWindow) editor;
		    if (!edwin.isMain())
			edwin.moveToFront();
		    result = true;
		} else
		    result
			= editorController.displayEditorFor(modelObject, view);
	    }
	}
	return result;
    }
    
    public EditorController.Editor getEditorFor(Object foo) {
	EditorController.Editor editor = null;
	EditorController editorController = getEditorController(foo);
	if (editorController != null)
	    editor = editorController.getEditorFor(foo);
	return editor;
    }
    
    public EditorController getEditorController(Object object) {
	EditorController editorController
	    = (EditorController) _classToEditorMap.get(object.getClass());
	if (editorController == null) {
	    Vector potentialEditors = new Vector();
	    for (int i = 0; i < _editors.size(); i++) {
		EditorController test
		    = (EditorController) _editors.elementAt(i);
		if (test.canEdit(object))
		    potentialEditors.addElementIfAbsent(test);
	    }
	    if (potentialEditors.size() > 1)
		Debug.print(true, "developer error:  multiple editors for ",
			    object.getClass().getName());
	    if (potentialEditors.size() >= 1)
		editorController
		    = (EditorController) potentialEditors.elementAt(0);
	}
	return editorController;
    }
    
    void aboutProduct(World world) {
	if (_aboutWindow != null && _aboutWindow.isVisible())
	    _aboutWindow.moveToFront();
	else if (_aboutWindow != null && _aboutWindow.getTitleBar() != null)
	    _aboutWindow.show();
	else {
	    Size screenSize = getRootWindowSize();
	    int width = Math.min(350, screenSize.width);
	    int height = screenSize.height - 20;
	    int x = Math.min(100, (screenSize.width - width) / 2);
	    _aboutWindow = new PlaywriteWindow(x, 10, width, height, world) {
		void changeWindowColor(Color color) {
		    for (int i = 0; i < this.subviews().size(); i++) {
			View view = (View) this.subviews().elementAt(i);
			if (view instanceof ScrollableArea)
			    ((ScrollableArea) view)
				.setBackgroundColor(color.lighterColor());
		    }
		    super.changeWindowColor(color, color.lighterColor());
		}
	    };
	    _aboutWindow.setTitle
		(Resource.getTextAndFormat("about wint",
					   new Object[] { getProductName() }));
	    width = _aboutWindow.contentSize().width;
	    height = _aboutWindow.contentSize().height;
	    StringBuffer text = new StringBuffer(500);
	    TextView contentView = new TextView(0, 0, width, height);
	    contentView.setEditable(false);
	    contentView.setHorizResizeInstruction(2);
	    contentView.setVertResizeInstruction(16);
	    text.append(getProductName());
	    text.append(" ");
	    text.append(getVersionString());
	    text.append("\n");
	    text.append(getBuildString());
	    text.append("\n\n");
	    if (isAuthoring() && isSerialProtected()) {
		text.append(Resource.getText("reg nl"));
		text.append(getPreference("RegisteredUser", ""));
		text.append("\n");
		text.append(Resource.getText("reg sl"));
		text.append(getPreference("SerialNumber", getProductType()));
		text.append("\n\n");
	    }
	    text.append(Resource.getTextAndFormat
			("about b", new Object[] { getShortProductName() }));
	    text.append(Resource.getText("about bc"));
	    contentView.setString(text.toString());
	    ScrollableArea scroller
		= new ScrollableArea(width, height, contentView, false, true);
	    scroller.setBuffered(true);
	    scroller.setVerticalScrollAmount(20);
	    scroller.setHorizResizeInstruction(2);
	    scroller.setVertResizeInstruction(16);
	    _aboutWindow.addSubview(scroller);
	    _aboutWindow.changeWindowColor(world.getColor());
	    _aboutWindow.show();
	}
    }
    
    void aboutPlugins(World world) {
	if (_aboutPluginWindow != null && _aboutPluginWindow.isVisible())
	    _aboutPluginWindow.moveToFront();
	else {
	    Size screenSize = getRootWindowSize();
	    int width = Math.min(350, screenSize.width);
	    int height = screenSize.height / 2;
	    int x = Math.min(100, (screenSize.width - width) / 2);
	    _aboutPluginWindow = new PlaywriteWindow(x, 25, width, height,
						     world) {
		void changeWindowColor(Color color) {
		    for (int i = 0; i < this.subviews().size(); i++) {
			View view = (View) this.subviews().elementAt(i);
			if (view instanceof ScrollableArea)
			    ((ScrollableArea) view)
				.setBackgroundColor(color.lighterColor());
		    }
		    super.changeWindowColor(color, color.lighterColor());
		}
		
		public void hide() {
		    super.hide();
		    _aboutPluginWindow = null;
		}
	    };
	    _aboutPluginWindow.setTitle(Resource.getText("about pt"));
	    width = _aboutPluginWindow.contentSize().width;
	    height = _aboutPluginWindow.contentSize().height;
	    StringBuffer text = new StringBuffer(500);
	    TextView contentView = new TextView(0, 0, width, height);
	    contentView.setEditable(false);
	    contentView.setHorizResizeInstruction(2);
	    contentView.setVertResizeInstruction(16);
	    if (world.getLoader() == null)
		contentView.setString("");
	    else
		contentView
		    .setString(world.getLoader().getPlugins().aboutText());
	    ScrollableArea scroller
		= new ScrollableArea(width, height, contentView, false, true);
	    scroller.setBuffered(true);
	    scroller.setVerticalScrollAmount(20);
	    scroller.setHorizResizeInstruction(2);
	    scroller.setVertResizeInstruction(16);
	    _aboutPluginWindow.addSubview(scroller);
	    _aboutPluginWindow.changeWindowColor(world.getColor());
	    _aboutPluginWindow.show();
	}
    }
    
    World newWorld(World oldWorld) {
	World w = null;
	markBusy();
	try {
	    if (closeIfNecessary(oldWorld)) {
		w = World.createAndStartWorld(true);
		addAndShowWorld(w);
	    }
	} finally {
	    clearBusy();
	    displayOrRemoveSplashScreen();
	}
	return w;
    }
    
    public String getNewWorldName() {
	String result;
	if (_newWorldCounter == 0)
	    result = Resource.getText("WW Title");
	else
	    result = (Resource.getTextAndFormat
		      ("WW Title Generator",
		       new Object[] { new Integer(_newWorldCounter) }));
	_newWorldCounter++;
	return result;
    }
    
    public void addAndShowWorld(World world) {
	_worlds.addElement(world);
	world.changeState(World.STOP);
	updateOpenSecondWorldMenuCommands();
	showWorld(world);
    }
    
    public void showWorld(World world) {
	PlaywriteWindow worldWindow = world.createWindow();
	rearrangeWindows();
	worldWindow.show();
    }
    
    boolean closeIfNecessary(World oldWorld) {
	if (_worlds.size() >= getMaxOpenWorlds())
	    return closeWorld(oldWorld, false);
	return true;
    }
    
    protected void rearrangeWindows() {
	closeProgress();
	layoutWorlds();
    }
    
    protected void layoutWorlds() {
	if (numberOfWorlds() >= 1) {
	    Size screenSize = getRootWindowSize();
	    World world = (World) _worlds.firstElement();
	    WorldWindow worldWindow = world.getWindow();
	    Rect newBounds = worldWindow.getUserBounds();
	    worldWindow.setSystemBounds(newBounds.x, newBounds.y,
					Math.min(newBounds.width,
						 screenSize.width),
					Math.min(newBounds.height,
						 screenSize.height));
	}
    }
    
    public void openWorld(World oldWorld) {
	FileChooser chooser
	    = new FileChooser(this.mainRootView(),
			      Resource.getText("dialog caw"), 0);
	if (World.getWorldDirectory() != null)
	    chooser.setDirectory(World.getWorldDirectory());
	chooser.showModally();
	if (chooser.file() == null)
	    displayOrRemoveSplashScreen();
	else {
	    World.setWorldDirectory(chooser.directory());
	    openWorld(oldWorld, chooser.directory(), chooser.file());
	}
    }
    
    public World openWorld(World oldWorld, String directory, String file) {
	World newWorld = null;
	if (!closeIfNecessary(oldWorld)) {
	    displayOrRemoveSplashScreen();
	    return null;
	}
	Debug.print("debug.commands", "opening ", directory, file);
	openProgress("splash lw");
	File userFile = new File(directory, file);
	newWorld = loadDataFile(userFile);
	if (newWorld == null) {
	    closeProgress();
	    displayOrRemoveSplashScreen();
	    return null;
	}
	newWorld.setMenuCommandEnabled("command sw",
				       (newWorld.getFSVersion()
					== getObjectStoreVersion()));
	if (newWorld.getWorldThread() == null)
	    newWorld.startWorldThread();
	displayOpenWorld(newWorld);
	newWorld.validateAppearances();
	newWorld.validateCharacters();
	return newWorld;
    }
    
    private void displayOpenWorld(World newWorld) {
	markBusy();
	newWorld.setModified(false);
	long time = System.currentTimeMillis();
	addAndShowWorld(newWorld);
	displayOrRemoveSplashScreen();
	if (isAuthoring())
	    newWorld.checkHygiene();
	newWorld.saveState();
	newWorld.setMenuCommandEnabled("command reset world", true);
	time = System.currentTimeMillis() - time;
	Debug.print("debug.statistics", "Display prep time: " + time, "ms");
	clearBusy();
	if (newWorld.getWorldView() != null)
	    newWorld.getWorldView().setFocusedView();
    }
    
    public boolean closeWorld(World world, boolean reformat) {
	if (world == null)
	    return true;
	if (world.getState() == World.CLOSING)
	    return false;
	boolean isTutorialWorld
	    = isAuthoring() && Tutorial.getTutorial() != null;
	if (isPlayer() || isTutorialWorld || Debug.lookup("debug.quickexit"))
	    world.setModified(false);
	if (!world.prepareToClose() && !isServer())
	    return false;
	boolean prevActive = BitmapManager.MemoryCleanupGnome.setActive(false);
	try {
	    world.close();
	} catch (Exception e) {
	    Debug.print(true, "Error in closing...should be OK to continue");
	    Debug.stackTrace(e);
	} finally {
	    world.setMediaSource(null);
	    BitmapManager.MemoryCleanupGnome.setActive(prevActive);
	}
	_worlds.removeElementIdentical(world);
	updateOpenSecondWorldMenuCommands();
	if (reformat) {
	    if (_worlds.size() == 0) {
		if (isProfessional())
		    displayOrRemoveSplashScreen();
		else if (isTutorialWorld && loadTutorial())
		    displayTutorialScreen();
		else
		    displaySplashScreen(true);
	    } else
		rearrangeWindows();
	}
	return true;
    }
    
    boolean closeAllWorlds() {
	boolean done = true;
	for (int i = _worlds.size() - 1; i >= 0; i--) {
	    World world = (World) _worlds.elementAt(i);
	    done = done & closeWorld(world, i == 0);
	}
	return done;
    }
    
    protected void prepareToQuit() {
	/* empty */
    }
    
    public final void quit() {
	if (closeAllWorlds()) {
	    prepareToQuit();
	    if (_loader != null)
		_loader.getPlugins().close();
	    BitmapManager.printStatistics();
	    Util.printStatistics();
	    getTempManager().done();
	    System.exit(0);
	}
    }
    
    void displaySplashScreen(boolean enable) {
	if (!isServer()) {
	    if (_aboutWindow != null)
		_aboutWindow.hide();
	    String id = "splash screen player image";
	    if (isAuthoring())
		id = (isProfessional() ? "splash screen pro image"
		      : "splash screen authoring image");
	    _splashScreen = Resource.getImage(id);
	    _splashScreenWindow = new InternalWindow(0, 0, 0,
						     _splashScreen.width(),
						     _splashScreen.height()) {
		protected void willMoveTo(Point newPoint) {
		    newPoint.x = 0;
		    newPoint.y = 0;
		    super.willMoveTo(newPoint);
		}
	    };
	    _splashScreenWindow.contentView().setBackgroundColor(Color.black);
	    _splashScreenWindow.setResizable(false);
	    _splashScreenWindow.setHorizResizeInstruction(2);
	    _splashScreenWindow.setVertResizeInstruction(16);
	    PlaywriteView spContents
		= new PlaywriteView(_splashScreenWindow.contentView()
					.bounds());
	    spContents.setHorizResizeInstruction(2);
	    spContents.setVertResizeInstruction(16);
	    spContents.setImage(Resource.getImage("splash screen tile"));
	    spContents.setImageDisplayStyle(2);
	    _splashScreenWindow.addSubview(spContents);
	    _splashScreenWindow.contentView().layoutView(0, 0);
	    _splashView = new PlaywriteView(_splashScreen);
	    _splashView.setVertResizeInstruction(64);
	    _splashView.setHorizResizeInstruction(32);
	    spContents.addSubview(_splashView);
	    spContents.layoutView(0, 0);
	    Font labelFont = new Font("Serif", 1, 24);
	    if (isApplication() && !isProfessional()) {
		Label label
		    = new Label(Resource.getText("splash q"), labelFont);
		label.setColor(new Color(203, 203, 203));
		label.sizeToMinSize();
		ButtonLabel labeledButton
		    = createQuitButtonLabel("splash quit", label);
		labeledButton.moveTo(SPLASH_BUTTON_LOC[3][0],
				     SPLASH_BUTTON_LOC[3][1]);
		_splashView.addSubview(labeledButton);
	    }
	    _trademarkNotice
		= new Label(" " + Resource.getText("splash trademark notice"),
			    new Font("Sans", 0, 9));
	    Color trademarkColor = Color.lightGray;
	    _trademarkNotice.setColor(trademarkColor);
	    _trademarkNotice.moveTo(2, (_splashScreenWindow.height()
					- _trademarkNotice.height() - 2));
	    _trademarkNotice.setVertResizeInstruction(8);
	    _copyrightNotice
		= new Label((" " + getVersionNumber()
			     + Resource.getText("splash copyright notice")),
			    new Font("Sans", 0, 9));
	    _copyrightNotice.setColor(trademarkColor);
	    _copyrightNotice.moveTo(2, (_trademarkNotice.y()
					- _copyrightNotice.height() + 2));
	    _copyrightNotice.setVertResizeInstruction(8);
	    spContents.addSubview(_copyrightNotice);
	    spContents.addSubview(_trademarkNotice);
	    View rootView = this.mainRootView();
	    Size winSize = new Size(rootView.width(), rootView.height());
	    _splashScreenWindow.sizeTo(winSize.width, winSize.height);
	    if (enable && !isProfessional())
		enableSplashScreen();
	    _splashScreenWindow.show();
	}
    }
    
    private Button addSplashButton(String upID, String downID, String command,
				   int locIndex) {
	Bitmap upImage = Resource.getButtonImage(upID);
	Bitmap downImage = Resource.getAltButtonImage(downID);
	Button button = new RolloverButton(upImage, downImage, command, this);
	button.moveTo(SPLASH_BUTTON_LOC[locIndex][0],
		      SPLASH_BUTTON_LOC[locIndex][1]);
	_splashView.addSubview(button);
	button.setDirty(true);
	return button;
    }
    
    private Label addSplashLabel(String labelText, Font labelFont,
				 Color labelColor, Button button) {
	Label label = new Label(labelText, labelFont);
	label.setColor(labelColor);
	label.sizeToMinSize();
	label.moveTo(button.x() + button.width() + 10,
		     button.y() + (button.height() - label.height()) / 2);
	_splashView.addSubview(label);
	label.setDirty(true);
	return label;
    }
    
    protected void enableSplashScreen() {
	if (!isServer() && _validated) {
	    Font labelFont = new Font("Serif", 1, 24);
	    Color labelColor = new Color(203, 203, 203);
	    if (isAuthoring()) {
		Button button
		    = addSplashButton("splash new", "splash new", "NW", 0);
		Label label = addSplashLabel(Resource.getText("splash cw"),
					     labelFont, labelColor, button);
	    }
	    if (isApplication()) {
		Button button
		    = addSplashButton("splash open", "splash open", "OW", 1);
		Label label = addSplashLabel(Resource.getText("splash ow"),
					     labelFont, labelColor, button);
	    }
	    if (isAuthoring()) {
		Button button
		    = addSplashButton("splash learn", "splash learn", "OT", 2);
		Label label
		    = (addSplashLabel
		       (Resource.getTextAndFormat("splash ot",
						  (new Object[]
						   { getShortProductName() })),
			labelFont, labelColor, button));
	    }
	}
    }
    
    private void discardSplashScreen() {
	if (_splashScreenWindow != null) {
	    _splashScreen.flush();
	    _splashScreen = null;
	    _splashView = null;
	    _splashScreenWindow = null;
	}
    }
    
    private boolean loadTutorial() {
	RootView rootView = this.mainRootView();
	Size winSize = new Size(rootView.width(), rootView.height());
	if (_tutorialSplashScreen == null)
	    _tutorialSplashScreen = new TutorialSplashScreen();
	boolean result = _tutorialSplashScreen.init();
	return result;
    }
    
    private void displayTutorialScreen() {
	if (_tutorialSplashScreen != null)
	    _tutorialSplashScreen.show();
    }
    
    public final int numberOfWorlds() {
	return _worlds.size();
    }
    
    public void updateOpenSecondWorldMenuCommands() {
	int numberOfWorlds = _worlds.size();
	for (int i = 0; i < numberOfWorlds; i++) {
	    World world = (World) _worlds.elementAt(i);
	    world.setMenuCommandEnabled("command osw",
					(numberOfWorlds > 1
					 || world.isRunning()) ^ true);
	}
    }
    
    public void displayOrRemoveSplashScreen() {
	if (numberOfWorlds() == 0) {
	    if (isProfessional())
		performCommand("NW", null);
	    else if (_splashScreenWindow == null)
		displaySplashScreen(true);
	    else
		_splashScreenWindow.show();
	} else
	    discardSplashScreen();
    }
    
    private void repaint() {
	this.mainRootView().panel().repaint();
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("NW")) {
	    if (_validated) {
		if (_splashScreenWindow != null)
		    _splashScreenWindow.hide();
		World world = newWorld(null);
	    }
	} else if (command.equals("OW")) {
	    if (_validated) {
		_splashScreenWindow.hide();
		openWorld(null);
	    }
	} else if (command.equals("OT")) {
	    if (_validated) {
		if (loadTutorial()) {
		    if (_splashScreenWindow != null)
			_splashScreenWindow.hide();
		    discardSplashScreen();
		    displayTutorialScreen();
		} else
		    displayOrRemoveSplashScreen();
	    }
	} else if (command.equals("OSW")) {
	    if (_validated) {
		if (_splashScreenWindow != null)
		    _splashScreenWindow.hide();
		World worldToClose
		    = (World) (numberOfWorlds() >= getMaxOpenWorlds()
			       ? _worlds.firstElement() : null);
		openWorld(worldToClose, ((File) data).getParent(),
			  ((File) data).getName());
	    }
	} else if (command.equals("SA"))
	    aboutProduct(new World());
	else if (command.equals("PW"))
	    Debug.print(true, "Print not supported at this time.");
	else if (command.equals("quit action"))
	    quit();
	else if (command.equals("EX"))
	    quit();
	else if (command.equals("wng") && data instanceof String) {
	    PlaywriteDialog dialog
		= new PlaywriteDialog((String) data, "command ok");
	    dialog.getAnswer();
	} else if (command.equals("show modal dialog"))
	    ((PlaywriteDialog) data).showModally();
	else if (command.equals("DO_EXTENSION_MENU")) {
	    try {
		MenuItem mi = (MenuItem) data;
		mi = (MenuItem) mi.data();
		Debug.print(true, "Menu item is: ", mi);
		WorldWindow win
		    = (WorldWindow) this.mainRootView().mainWindow();
		Debug.print(true, "Main window is: ", win);
		mi.setData(win.getWorld());
		mi.sendCommand();
	    } catch (RecoverableException re) {
		re.showDialog();
	    } catch (Throwable t) {
		Debug.stackTrace(t);
	    }
	} else if (command.equals(SysEvent.EVENT_OPEN_FILE))
	    this.performCommandLater(this, "OSW", data);
	else if (command.equals(SysEvent.EVENT_PRINT_FILE))
	    this.performCommandLater(this, "PW", data);
	else if (command.equals(SysEvent.EVENT_ABOUT_APP))
	    this.performCommandLater(this, "SA", data);
	else if (command.equals(SysEvent.EVENT_EXIT_APP))
	    this.performCommandLater(this, "EX", data);
    }
    
    public boolean windowWillShow(Window window) {
	return true;
    }
    
    public void windowDidShow(Window window) {
	/* empty */
    }
    
    public boolean windowWillHide(Window window) {
	boolean result = false;
	if (!_inWindowWillHide) {
	    _inWindowWillHide = true;
	    if (PlaywriteSystem.isMacintosh()) {
		if (_splashScreenWindow != null
		    || Debug.lookup("debug.quickexit")) {
		    quit();
		    result = true;
		} else {
		    if (_tutorialSplashScreen != null)
			_tutorialSplashScreen.hide();
		    closeAllWorlds();
		}
	    } else {
		quit();
		result = false;
	    }
	    _inWindowWillHide = false;
	}
	return result;
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
	if (_registrationDialog != null)
	    _registrationDialog.moveBy(size.width / 2, size.height / 2);
    }
    
    void fakeMouseEvent(MouseEvent e) {
	e.setProcessor(getMainRootView());
	this.eventLoop().addEvent(e);
    }
    
    static boolean isDoubleClick(long time, Object obj) {
	PlaywriteRoot root = app();
	return (time - root._lastMouseDownTime < 1000L
		&& root._lastMouseDownObject == obj);
    }
    
    static void setDoubleClicking(long time, Object obj) {
	PlaywriteRoot root = app();
	root._lastMouseDownTime = time;
	root._lastMouseDownObject = obj;
    }
    
    static void resetDoubleClicking() {
	setDoubleClicking(0L, null);
    }
    
    private void checkMaxCycle() {
	_maxCycles
	    = PlaywriteSystem.getApplicationPropertyAsInt("max_cyles", -1);
	if (_maxCycles > 0)
	    Debug.print(true, "maxCycles " + _maxCycles);
    }
    
    int getMaxCycles() {
	return _maxCycles;
    }
    
    private void displayApplet() {
	World world = null;
	String ostore
	    = PlaywriteSystem.getApplicationProperty("startup_world", null);
	boolean isTutorial
	    = PlaywriteSystem
		  .getApplicationPropertyAsBoolean("startup_tutorial", false);
	String error = null;
	if (ostore != null) {
	    openProgress("splash lw");
	    try {
		world = loadAppletDataFile(ostore);
	    } catch (MalformedURLException malformedurlexception) {
		error = Resource.getText("dialog burl") + ostore;
	    } catch (IOException ioexception) {
		error = Resource.getText("dialog cc") + ostore;
	    }
	    if (world != null) {
		addAndShowWorld(world);
		world.saveState();
		world.setModified(false);
	    }
	}
	closeProgress();
	if (world == null && !isAuthoring())
	    displaySplashScreen(true);
	if (error != null)
	    PlaywriteDialog.warning(error, true);
	if (world != null) {
	    getAppletParameters(world);
	    world.startWorldThread();
	    if (isTutorial)
		new Tutorial(world);
	    else if (!isAuthoring())
		world.runForward();
	} else if (isAuthoring())
	    getAppletParameters(newWorld(null));
    }
    
    private void getAppletParameters(World world) {
	String param = PlaywriteSystem.getSystemProperty("global variable");
	if (world != null && param != null)
	    Variable.systemVariable
		(World.SYS_APPLET_PARAMETER_VARIABLE_ID, world)
		.setValue(world, param);
    }
    
    protected InputStream openAppletWorldStream(URL url) throws IOException {
	throw new IOException
		  ("This method is only valid when called while running as an applet");
    }
    
    private World loadAppletDataFile(String fName)
	throws MalformedURLException {
	String name = Util.dePercentString(Util.getFilePart(fName));
	URL codebase = AWTCompatibility.awtApplet().getCodeBase();
	final URL url
	    = new URL(codebase, (codebase.toString().startsWith("file:")
				 ? fName : Util.safeURLStr(fName)));
	WorldZipFile.DataSource src = new WorldZipFile.DataSource() {
	    public File getFile() {
		return null;
	    }
	    
	    public InputStream sourceToStream() throws IOException {
		return openAppletWorldStream(url);
	    }
	    
	    public String toString() {
		return url.toString();
	    }
	};
	return loadData(src, name);
    }
    
    public World loadURLDataFile(URL url, String fName)
	throws MalformedURLException {
	String name = Util.dePercentString(Util.getFilePart(fName));
	return loadData(new WorldZipFile.URLSource(url), name);
    }
    
    public World loadDataFile(File file) {
	return loadData(new WorldZipFile.FileSource(file), file.getName());
    }
    
    private World loadData(WorldZipFile.DataSource src, String name) {
	String error = null;
	World world = null;
	RecoverableException recoverable = null;
	markBusy();
	try {
	    world = WorldZipFile.readData(src, name);
	} catch (SecurityException e) {
	    error = Resource.getText("dialog na") + src;
	    Debug.stackTrace(e);
	} catch (java.io.FileNotFoundException filenotfoundexception) {
	    recoverable = new RecoverableException("dialog cff",
						   new Object[] { name });
	} catch (IOException e) {
	    error = Resource.getText("dialog ioe");
	    Debug.stackTrace(e);
	} catch (UnknownVersionError e) {
	    error = Resource.getText("dialog ve");
	    Debug.stackTrace(e);
	} catch (RecoverableException re) {
	    recoverable = re;
	} catch (OutOfMemoryError e) {
	    throw e;
	} catch (Throwable t) {
	    error = Resource.getText("dialog ue");
	    Debug.stackTrace(t);
	} finally {
	    clearBusy();
	}
	if (error == null && world == null)
	    error = Resource.getText("dialog uf");
	if (recoverable != null) {
	    recoverable.showDialog();
	    return null;
	}
	if (error != null) {
	    PlaywriteDialog.warning(error, true);
	    return null;
	}
	return world;
    }
    
    ButtonLabel createQuitButtonLabel(String buttonResourceId, Label label) {
	COM.stagecast.ifc.netscape.application.Image upImage
	    = Resource.getButtonImage(buttonResourceId);
	COM.stagecast.ifc.netscape.application.Image downImage
	    = Resource.getAltButtonImage(buttonResourceId);
	PlaywriteButton button
	    = new RolloverButton(upImage, downImage, "EX", this);
	ButtonLabel labeledButton = new ButtonLabel(button, label, 5);
	labeledButton.setTransparent(true);
	return labeledButton;
    }
    
    void createMemoryWindow() {
	InternalWindow iw = new InternalWindow();
	Font font = new Font("Sans-serif", 0, 9);
	iw.setCloseable(false);
	Size sz = iw.windowSizeForContentSize(96, 20);
	iw.sizeTo(sz.width, sz.height);
	iw.setTitle("RAM");
	iw.setResizable(false);
	iw.setLayer(100);
	TextField tf = new TextField(0, 0, sz.width, 10);
	tf.setFont(font);
	tf.setBorder(null);
	tf.setEditable(false);
	tf.setJustification(1);
	iw.addSubview(tf);
	ContainerView cv = new ContainerView(0, 10, 0, 10);
	cv.setBackgroundColor(Color.yellow);
	iw.addSubview(cv);
	TextField tf2 = new TextField(0, 10, sz.width, 10) {
	    public boolean mouseDown(MouseEvent event) {
		boolean result = super.mouseDown(event);
		markBusy();
		Util.suggestGC();
		clearBusy();
		return result;
	    }
	};
	tf2.setFont(font);
	tf2.setBorder(null);
	tf2.setEditable(false);
	tf2.setTransparent(true);
	tf2.setJustification(0);
	iw.addSubview(tf2);
	MemoryUpdater mu = new MemoryUpdater(tf, tf2, cv);
	iw.show();
    }
    
    private static void handleFatalException() {
	String answer = null;
	try {
	    if (!isServer()) {
		PlaywriteDialog dialog;
	    while_8_:
		do {
		    if (app().isApplet())
			dialog = new PlaywriteDialog("dialog fatal 3 text",
						     "dialog fatal 3 button");
		    else {
			do {
			    if (!Tutorial.isTutorialRunning()) {
				app();
				if (!isPlayer())
				    break;
			    }
			    dialog = (new PlaywriteDialog
				      ((PlaywriteSystem.isMacintosh()
					? "dialog fatal 2 text mac"
					: "dialog fatal 2 text"),
				       "dialog fatal 2 button"));
			    break while_8_;
			} while (false);
			dialog
			    = new PlaywriteDialog((PlaywriteSystem
						       .isMacintosh()
						   ? "dialog fatal 1 text mac"
						   : "dialog fatal 1 text"),
						  "dialog fatal 1 button 1",
						  "dialog fatal 1 button 2");
		    }
		} while (false);
		answer = dialog.getAnswer();
	    }
	    if (answer.equals("dialog fatal 1 button 1")) {
		Enumeration worlds = app()._worlds.elements();
		while (worlds.hasMoreElements() == true) {
		    try {
			World world = (World) worlds.nextElement();
			world.saveToDisk(true);
		    } catch (Throwable t) {
			t.printStackTrace();
		    }
		}
	    }
	} catch (Throwable t) {
	    t.printStackTrace();
	} finally {
	    app();
	    if (isApplication())
		System.exit(0);
	}
    }
    
    static Thread makeFatalErrorThread() {
	return new Thread((Thread.currentThread().getThreadGroup
			   ()), new FatalErrorNotifier(new Runnable() {
	    public void run() {
		handleFatalException();
	    }
	}));
    }
}
