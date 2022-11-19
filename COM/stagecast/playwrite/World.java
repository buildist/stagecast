/* World - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.EventLoop;
import COM.stagecast.ifc.netscape.application.FileChooser;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Menu;
import COM.stagecast.ifc.netscape.application.MenuItem;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Sort;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;
import COM.stagecast.simmer.PacketProcessor;

public class World
    implements Debug.Constants, Externalizable, Lockable, ModificationAware,
	       Named, PlaywriteSystem.Properties, ResourceIDs.AboutWindowIDs,
	       ResourceIDs.ColorIDs, ResourceIDs.CommandIDs,
	       ResourceIDs.DialogIDs, ResourceIDs.InstanceNameIDs,
	       ResourceIDs.RootIDs, ResourceIDs.SplashScreenIDs,
	       ResourceIDs.ToolIDs, ResourceIDs.WorldIDs,
	       ResourceIDs.WorldVariableIDs, ResourceIDs.WorldViewIDs,
	       Runnable, Target, VariableOwner, Worldly
{
    static final int MAX_VISIBLE_STAGES = 2;
    private static final int VIEWDATA_VERSION = 2;
    static final int SLOW_SPEED = 0;
    static final int MEDIUM_SPEED = 1;
    static final int FAST_SPEED = 2;
    static final int FULL_SPEED = 3;
    static final int DEFAULT_RATE = 10;
    static final long SLOW_DIVISOR = 3L;
    static final long FAST_MULTIPLIER = 3L;
    static final int MAX_FRAMES_PER_SEC = 50;
    static final int DEFAULT_HISTORY_SIZE = 2000;
    public static final String SYS_WORLD_NAME_VARIABLE_ID
	= "Stagecast.World:name".intern();
    public static final String SYS_ENABLE_SOUND_VARIABLE_ID
	= "Stagecast.World:play_sounds".intern();
    public static final String SYS_SHOW_ALL_ACTIONS_VARIABLE_ID
	= "Stagecast.World:show_all_actions".intern();
    public static final String SYS_RUN_ALL_STAGES_VARIABLE_ID
	= "Stagecast.World:run_all_stages".intern();
    public static final String SYS_ENABLE_GRID_VARIABLE_ID
	= "Stagecast.World:grid".intern();
    public static final String SYS_CENTER_FOLLOW_ME_VARIABLE_ID
	= "Stagecast.World:center_follow_me".intern();
    public static final String SYS_FOLLOW_ME_VARIABLE_ID
	= "Stagecast.World:follow_me".intern();
    public static final String SYS_SPEED_VARIABLE_ID
	= "Stagecast.World:speed".intern();
    public static final String SYS_FRAME_RATE_VARIABLE_ID
	= "Stagecast.World:frame_rate".intern();
    public static final String SYS_WINDOW_COLOR_VARIABLE_ID
	= "Stagecast.World:window_color".intern();
    public static final String SYS_WORLD_LOCKED_VARIABLE_ID
	= "Stagecast.World:locked".intern();
    public static final String SYS_SIM_AHEAD_VARIABLE_ID
	= "Stagecase.World:sim_ahead".intern();
    public static final String SYS_ENVIRONMENT_VARIABLE_ID
	= "Stagecase.World:env".intern();
    public static final String SYS_APPLET_PARAMETER_VARIABLE_ID
	= "Stagecase.World:applet_param".intern();
    public static final String CLEAR_DEBUG
	= "Stagecast.World:clear debug".intern();
    static final String SYS_HISTORY_SIZE_VARIABLE_ID
	= "Stagecast.World:history_size".intern();
    public static final Object OPENING = new String("Opening");
    public static final Object STOPPED = new String("Stopped");
    public static final Object RUNNING = new String("Running");
    public static final Object RECORDING = new String("Recording");
    public static final Object EDITING = new String("Editing");
    public static final Object DEBUGGING = new String("Debugging");
    public static final Object EDIT_DEBUGGING = new String("Edit/Debug");
    public static final Object CLOSING = new String("Closing");
    public static final Object RUN = new String("RUN!");
    public static final Object RECORD_RULE = new String("RECORD!");
    public static final Object EDIT_RULE = new String("EDIT!");
    public static final Object BREAKPOINT = new String("BREAK!");
    public static final Object STOP = new String("STOP!");
    public static final Object DONE = new String("DONE!");
    public static final Object CANCEL = new String("CANCEL!");
    public static final Object CLOSE_WORLD = new String("CLOSE!");
    static final String SHOW_STAGE_VARS = "SSV";
    static final String DATA_FOLDER_NAME = "data";
    static final String MAKE_FOLLOW_ME_VISIBLE = "make followMe visible";
    public static final String WORLD_NUMBER_OF_VISIBLE_REGIONS_ACTION_ID
	= "Stagecast.World:action.num_visi_regions";
    public static final String WORLD_SET_VISIBLE_STAGE_ACTION_ID
	= "Stagecast.Stage:action.set_visi_stage";
    public static final String WORLD_RESET_ACTION_ID
	= "Stagecast.Stage:action.reset";
    private static String _pictureDirectory;
    private static String _backgroundDirectory;
    private static String _soundDirectory;
    private static String _worldDirectory;
    private static String _uploadConfigDirectory;
    static final ColorValue defaultColor;
    private static final String INTERNAL_LOADSTATE_COMMAND = "LOADSTATE";
    private static int[] frameRateOverride;
    static final int storeVersion = 12;
    static final long serialVersionUID = -3819410108756919602L;
    private static final int MAX_SPLAT_IMAGES = 10;
    static final int sFormatNumber = 3;
    private String author = "";
    private String comment = "";
    private Password _password = null;
    private String _originalCreatorVersion;
    private XYCharContainer prototypes;
    private XYCharContainer specialPrototypes = null;
    private XYContainer stages;
    private XYContainer jars;
    private XYContainer sounds;
    private XYCharContainer _timeoutBox;
    private XYContainer _backgrounds;
    private VariableList globalVariables;
    private Vector visibleStageList = new Vector(2) {
	public String toString() {
	    String s = " { ";
	    for (int i = 0; i < this.size(); i++)
		s += this.elementAt(i) + ", ";
	    s += " } ";
	    return s;
	}
    };
    private boolean _controlPanelIsVisible;
    private boolean _sidelineIsVisible;
    private ViewData _viewData;
    private int prototypeCounter = 0;
    private int stageNameCounter = 1;
    private RuleEditor _ruleEditor = PlaywriteRoot.getRuleEditor();
    private transient WorldDrawingModule _drawingModule
	= new WorldDrawingModuleImp();
    private transient PlaywriteRoot _root = null;
    private transient long _createTime;
    private transient PlaywriteLoader _loader = null;
    private transient StateMachine worldState = null;
    private transient WorldWindow _worldWindow = null;
    private transient WorldView _worldView = null;
    private transient boolean modifiedFlag = false;
    private transient boolean _isEmpty = true;
    private transient int versionNumber = 1;
    private transient int _fsVersion = PlaywriteRoot.getObjectStoreVersion();
    private transient boolean isRunningNow = false;
    private transient boolean wantToRun = false;
    private transient boolean stepping = true;
    private transient boolean directionForward = true;
    private transient ActionRingBuffer historyList
	= new ActionRingBuffer(2000);
    private transient Vector activeEvents = new Vector(5);
    private transient Vector pendingActiveEvents = new Vector(5);
    private transient Thread worldThread = null;
    private transient volatile boolean worldThreadDone = false;
    private transient volatile boolean blocked;
    private transient long frameMillis;
    private transient long frameSlop;
    private transient volatile long nextFrameTime;
    private transient int ticks = 0;
    private transient boolean _suspendTicks = false;
    private transient int nextSplatImage = 0;
    private transient Menu systemMenu = null;
    private transient ClockView clockView = null;
    private transient byte[] snapshotBuffer;
    private transient boolean freerun = false;
    private transient boolean _reorderViewFlag = true;
    private transient File _sourceFile = null;
    private transient ZipFile _mediaSource = null;
    private transient String _name = null;
    private transient Stage _followMeStageStart = null;
    private transient int _nestedSave = 0;
    private transient String _creatorVersion;
    private transient UpdateManager _historyWatchers;
    private transient UpdateManager _clockWatcherManager = null;
    private transient Vector _requiredPlugins = null;
    private transient Vector _prevRequiredPlugins = null;
    private transient PluginRegistry _embeddedPlugins = null;
    private transient Hashtable _uploadSites = new Hashtable();
    private transient Target _utilityObject;
    private transient boolean _ruleMatched;
    private transient ObjectSieve _objectSieve = null;
    private transient VariableSieve _variableSieve = null;
    private transient ActionSieve _actionSieve = null;
    private transient PacketProcessor _packetProcessor = null;
    private transient long startTime;
    private transient long totalTime;
    private transient long startCycle;
    private transient long deltaCycle;
    private transient long totalCycle;
    private transient long maxCycle;
    private transient long minCycle;
    private transient long cycles;
    private transient long startDraw;
    private transient long totalDraw;
    private transient int maxItems = 10;
    private transient GeneralizedCharacter[] _boundGCs
	= new GeneralizedCharacter[maxItems];
    private transient int _boundGCsize = 0;
    private transient boolean[] boundValues = new boolean[maxItems];
    private transient int maxTicks = -1;
    private transient boolean _forceUpdateAfterEveryChange = false;
    private transient boolean _hasWaitedForTick;
    private transient boolean _closing = false;
    private transient boolean _recordingSuspended = false;
    private transient boolean _resetFlag = false;
    private transient boolean _resetOnStop = false;
    private transient boolean _forceStopFlag = false;
    private transient int _badCharacters = 0;
    private transient Variable showEachActionVar;
    private transient Variable runStagesVar;
    private transient Variable followMeVar;
    private transient Exception _killerException;
    private static String _environment;
    transient Stage _currentStageDRHack = null;
    private Vector _callFromWorldThread = new Vector();
    private Stage NULL_STAGE = new Stage() {
	public String toString() {
	    return "null stage";
	}
    };
    
    public static class PerformCommandTuple
    {
	Target target;
	String command;
	Object data;
	
	public PerformCommandTuple(Target target, String command,
				   Object data) {
	    this.target = target;
	    this.command = command;
	    this.data = data;
	}
    }
    
    private static class LockedAccessor extends Variable.StandardDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    if (value != null && value instanceof Boolean) {
		World world = (World) owner;
		if (((Boolean) value).booleanValue()) {
		    Password pwd = PlaywriteRoot.askForPassword();
		    if (pwd != null && !pwd.isEmpty()) {
			world._password = pwd;
			return value;
		    }
		} else if (PlaywriteRoot.checkPassword(world._password))
		    return value;
	    }
	    return Variable.ILLEGAL_VALUE;
	}
    }
    
    private static class FollowMeAccessor
	extends Variable.StandardDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    if (value == null)
		return value;
	    if (value instanceof CharacterInstance
		&& ((CharacterInstance) value).getWorld() == owner.getWorld())
		return value;
	    return Variable.ILLEGAL_VALUE;
	}
    }
    
    private static class FrameAccessor extends Variable.NumberDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    value = super.constrainDirectValue(variable, owner, value);
	    if (value instanceof Number) {
		int val = ((Number) value).intValue();
		if (val < 1)
		    val = 1;
		else if (val > 50)
		    val = 50;
		return new Integer(val);
	    }
	    return Variable.ILLEGAL_VALUE;
	}
    }
    
    private static class SimAheadAccessor extends Variable.NumberDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    value = super.constrainDirectValue(variable, owner, value);
	    if (value instanceof Number) {
		int val = ((Number) value).intValue();
		if (val < -1)
		    val = -1;
		return new Integer(val);
	    }
	    return Variable.ILLEGAL_VALUE;
	}
    }
    
    private static class EnvironmentAccessor implements VariableDirectAccessor
    {
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    /* empty */
	}
	
	public Object getDirectValue(Variable variable, VariableOwner owner) {
	    World world = (World) owner;
	    if (World._environment == null) {
		Object[] params
		    = { PlaywriteRoot.isServer() ? "remote" : "local",
			PlaywriteRoot.getShortProductName(),
			PlaywriteRoot.getVersionNumber() };
		World._environment
		    = Resource.getTextAndFormat("WEnvFmtID", params);
	    }
	    return World._environment;
	}
	
	public Object mapUnboundDirect(Variable variable,
				       VariableOwner owner) {
	    return getDirectValue(variable, owner);
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    return Variable.ILLEGAL_VALUE;
	}
    }
    
    static {
	if (PlaywriteRoot.isAuthoring()) {
	    _pictureDirectory = FileIO.checkDirectory("W iopd");
	    _backgroundDirectory = FileIO.checkDirectory("W iobd");
	    _soundDirectory = FileIO.checkDirectory("W iosd");
	    _uploadConfigDirectory = FileIO.checkDirectory("W iousd");
	}
	if (PlaywriteRoot.isApplication()) {
	    _worldDirectory = FileIO.checkDirectory("W iowd");
	    _worldDirectory
		= PlaywriteSystem.getApplicationProperty("world_directory",
							 _worldDirectory);
	}
	defaultColor = new ColorValue(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR,
				      "Def CID");
	frameRateOverride = null;
    }
    
    static void initStatics() {
	boolean proVisible = PlaywriteRoot.isProfessional();
	new Variable(SYS_WORLD_NAME_VARIABLE_ID, "WNamVarID", false);
	new BooleanVariable(SYS_ENABLE_SOUND_VARIABLE_ID, "WPSTVarID",
			    "WPSFVarID");
	new BooleanVariable(SYS_SHOW_ALL_ACTIONS_VARIABLE_ID, "WSATVarID",
			    "WSAFVarID");
	new BooleanVariable(SYS_RUN_ALL_STAGES_VARIABLE_ID, "WXASTVarID",
			    "WXASFVarID");
	new BooleanVariable(SYS_ENABLE_GRID_VARIABLE_ID, "WSGTVarID",
			    "WSGFVarID");
	new BooleanVariable(SYS_CENTER_FOLLOW_ME_VARIABLE_ID,
			    "WCenterFollowMeTVarID", "WCenterFollowMeFVarID");
	new Variable(SYS_FOLLOW_ME_VARIABLE_ID, "WFMVarID",
		     new FollowMeAccessor(), true);
	new Variable(SYS_SPEED_VARIABLE_ID, "WSVarID", false);
	new Variable(SYS_FRAME_RATE_VARIABLE_ID, "WFRVarID",
		     new FrameAccessor(), true);
	new Variable(SYS_HISTORY_SIZE_VARIABLE_ID, "WHSVarID",
		     new Variable.NumberDirectAccessor(),
		     !PlaywriteRoot.isFinalBuild());
	new Variable(SYS_SIM_AHEAD_VARIABLE_ID, "WSimVarID",
		     new SimAheadAccessor(), proVisible);
	new Variable(SYS_ENVIRONMENT_VARIABLE_ID, "WEnvVarID",
		     new EnvironmentAccessor(), proVisible);
	Variable vv
	    = new Variable(SYS_APPLET_PARAMETER_VARIABLE_ID, "WAppParamID",
			   new Variable.StringDirectAccessor(), proVisible);
	vv.setTransient(true);
	new BooleanVariable(SYS_WORLD_LOCKED_VARIABLE_ID, "WLockedVarID",
			    "WUnockedVarID", new LockedAccessor(), false);
	Vector colors = new Vector(3);
	colors.addElement(defaultColor);
	Variable v = new ColorVariable(SYS_WINDOW_COLOR_VARIABLE_ID, "WCVarID",
				       colors);
	v.setVisible(false);
	String frameProp
	    = PlaywriteSystem.getApplicationProperty("frame_rate_override",
						     "");
	int[] frames = new int[4];
	StringTokenizer st = new StringTokenizer(frameProp, ",");
	int frame = 0;
	while (st.hasMoreTokens()) {
	    String rate = st.nextToken();
	    try {
		frames[frame++] = Integer.parseInt(rate);
	    } catch (Exception e) {
		Debug.stackTrace(e);
		frame = 0;
		break;
	    }
	}
	if (frame == 4)
	    frameRateOverride = frames;
	else if (frame != 0)
	    Debug.print(true, "Illegal frame override: ", frameProp);
    }
    
    public static World createAndStartWorld(boolean initialize) {
	World world = new World(initialize);
	world.startWorldThread();
	return world;
    }
    
    World(boolean initialize) {
	_root = PlaywriteRoot.app();
	_createTime = System.currentTimeMillis();
	setCreatorVersion(PlaywriteRoot.getProductName() + " "
			  + PlaywriteRoot.getVersionString());
	setAuthor(PlaywriteRoot.app().getPreference
		  ("RegisteredUser", Resource.getText("about atem")));
	if (initialize)
	    setName(PlaywriteRoot.app().getNewWorldName());
	else
	    setOriginalCreatorVersion(null);
	if (PlaywriteRoot.isAuthoring()) {
	    try {
		this.getClass();
		Class utilClass
		    = Class.forName("COM.stagecast.playwrite.WorldUtility");
		_utilityObject = (Target) utilClass.newInstance();
		_utilityObject.performCommand("SET_WORLD", this);
	    } catch (Exception exception) {
		Debug.print(true, "Couldn't load world utility");
	    }
	}
	setBlocked();
	worldState = new StateMachine(OPENING, this);
	worldState.addTransitions(OPENING, new Object[] { STOP },
				  new Object[] { STOPPED });
	worldState.addTransitions(STOPPED,
				  new Object[] { STOP, RUN, RECORD_RULE,
						 EDIT_RULE, CLOSE_WORLD },
				  new Object[] { STOPPED, RUNNING, RECORDING,
						 EDITING, CLOSING });
	worldState.addTransitions(RUNNING,
				  new Object[] { RUN, STOP, BREAKPOINT },
				  new Object[] { RUNNING, STOPPED,
						 DEBUGGING });
	worldState.addTransitions(RECORDING, new Object[] { DONE, CANCEL },
				  new Object[] { STOPPED, STOPPED });
	worldState.addTransitions(EDITING, new Object[] { DONE, CANCEL },
				  new Object[] { STOPPED, STOPPED });
	worldState.addTransitions(DEBUGGING,
				  new Object[] { RUN, EDIT_RULE, CLOSE_WORLD },
				  new Object[] { RUNNING, EDIT_DEBUGGING,
						 CLOSING });
	worldState.addTransitions(EDIT_DEBUGGING,
				  new Object[] { DONE, CANCEL },
				  new Object[] { STOPPED, STOPPED });
	worldState.addTransitions(CLOSING, null, null);
	if (PlaywriteRoot.isServer()) {
	    AbstractSieve.Notifier notifier
		= AbstractSieve.getStashedNotifier();
	    _objectSieve = new ObjectSieve(notifier);
	    _objectSieve.creation(this);
	    _variableSieve = new VariableSieve(notifier);
	    _variableSieve.ownerTypeIsInteresting(CharacterInstance.class);
	    _variableSieve.ownerTypeIsInteresting(TextCharacterInstance.class);
	    _variableSieve.ownerTypeIsInteresting(DoorInstance.class);
	    _variableSieve.ownerTypeIsInteresting(Stage.class);
	    _variableSieve.ownerTypeIsInteresting(World.class);
	    _actionSieve = new ActionSieve(notifier);
	}
	prototypes = new XYCharContainer(this, CharacterPrototype.class);
	specialPrototypes
	    = new XYCharContainer(this, CharacterPrototype.class, 3, true);
	stages = new XYContainer(this, Stage.class);
	sounds = new XYContainer(this, PlaywriteSound.class);
	sounds.add(PlaywriteSound.nullSound);
	jars = new XYContainer(this, Jar.class);
	_timeoutBox
	    = new XYCharContainer(this, CharacterInstance.class, 2, true);
	_backgrounds = new XYContainer(this, BackgroundImage.class);
	globalVariables = new VariableList(this);
	initializeVariables();
	_historyWatchers = new UpdateManager();
	if (initialize)
	    addDefaultContents();
	comment = Resource.getText("about ctem");
	setModified(false);
	resetStatistics();
	_viewData = new ViewData();
	if (!PlaywriteRoot.isServer())
	    initializeSystemMenu();
    }
    
    public void startWorldThread() {
	if (!PlaywriteRoot.isServer()) {
	    setWorldThread(new Thread(Thread.currentThread().getThreadGroup(),
				      new FatalErrorNotifier(this)));
	    worldThread.setPriority(worldThread.getPriority() - 1);
	    nextFrameTime = 0L;
	    worldThread.start();
	    while (nextFrameTime == 0L) {
		try {
		    Thread.sleep(10L);
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    }
	}
    }
    
    protected void handleLockingStateHasChanged(Boolean newLockingState) {
	if (getWorldView() != null) {
	    boolean locked = newLockingState.booleanValue();
	    Vector disabledTools = new Vector(5);
	    disabledTools.addElement(Tool.newCharacterTool);
	    disabledTools.addElement(Tool.editAppearanceTool);
	    disabledTools.addElement(Tool.newRuleTool);
	    disabledTools.addElement(Tool.copyLoadTool);
	    disabledTools.addElement(Tool.deleteTool);
	    WorldView _worldView = getWorldView();
	    WorldWindow _worldWindow = (WorldWindow) _worldView.window();
	    ControlPanelView cpView = _worldView.getControlPanelView();
	    Vector buttons = Util.getAllButtons(_worldWindow);
	    buttons.addElementsIfAbsent(Util.getAllButtons(cpView));
	    if (locked) {
		PlaywriteRoot.app();
		Vector internalWindows
		    = PlaywriteRoot.getMainRootView().internalWindows();
		Vector closeWindows = new Vector();
		String aboutWorldWindowTitle
		    = Resource.getTextAndFormat("about wint",
						new Object[] { getName() });
		for (int i = 0; i < internalWindows.size(); i++) {
		    if (internalWindows.elementAt(i)
			instanceof PlaywriteWindow) {
			PlaywriteWindow pw
			    = (PlaywriteWindow) internalWindows.elementAt(i);
			if (pw.getWorld() == this && pw != _worldWindow
			    && !pw.title().equals(aboutWorldWindowTitle))
			    closeWindows.addElement(pw);
		    }
		}
		for (int i = 0; i < closeWindows.size(); i++)
		    ((PlaywriteWindow) closeWindows.elementAt(i)).close();
		closeWindows.removeAllElements();
		_worldView.hideSidelines();
	    }
	    for (int i = 0; i < buttons.size(); i++) {
		Object button = buttons.elementAt(i);
		if (button instanceof ToolButton) {
		    if (disabledTools
			    .contains(((ToolButton) button).getTool()))
			((ToolButton) button).setTutorialDisabled(locked);
		} else if (((PlaywriteButton) button).command()
			       .equals("WW Show Sidelines"))
		    ((PlaywriteButton) button).setTutorialDisabled(locked);
	    }
	    MenuItem menuItem = getMenuItem("command pw");
	    if (menuItem != null) {
		if (locked)
		    menuItem
			.setTitle(Resource.getText("command unprotect world"));
		else
		    menuItem.setTitle(Resource.getText("command pw"));
	    }
	}
    }
    
    public World() {
	this(false);
    }
    
    void addDefaultContents() {
	_root.addRegisteredPrototypes(this);
	Stage stage = new Stage(this);
    }
    
    protected void finalize() throws Throwable {
	try {
	    super.finalize();
	} finally {
	    setMediaSource(null);
	}
    }
    
    public final String getName() {
	return _name;
    }
    
    public final void setName(String name) {
	_name = name;
	if (_worldWindow != null)
	    _worldWindow.setTitle(_name);
    }
    
    public final String getAuthor() {
	return author;
    }
    
    final void setAuthor(String s) {
	author = s;
    }
    
    public final String getComment() {
	return comment;
    }
    
    final void setComment(String s) {
	comment = s;
    }
    
    public final long getCreateTime() {
	return _createTime;
    }
    
    public final WorldWindow getWindow() {
	return _worldWindow;
    }
    
    final Menu getSystemMenu() {
	return systemMenu;
    }
    
    final int getVersionNumber() {
	return versionNumber;
    }
    
    final void setVersionNumber(int i) {
	versionNumber = i;
    }
    
    final Vector getActiveEvents() {
	return activeEvents;
    }
    
    final boolean isStepping() {
	return stepping;
    }
    
    public boolean isModified() {
	return modifiedFlag;
    }
    
    public void setModified(boolean b) {
	modifiedFlag = b;
	if (b)
	    _isEmpty = false;
    }
    
    final byte[] getSnapshotBuffer() {
	return snapshotBuffer;
    }
    
    public Target getUtilityObject() {
	return _utilityObject;
    }
    
    public void setWorldThread(Thread t) {
	ASSERT.isTrue(t == null ^ worldThread == null);
	worldThread = t;
	worldThreadDone = t == null;
    }
    
    public final Thread getWorldThread() {
	return worldThread;
    }
    
    public void setPacketProcessor(PacketProcessor p) {
	_packetProcessor = p;
    }
    
    final RuleEditor getRuleEditor() {
	return _ruleEditor;
    }
    
    final void setRuleEditor(RuleEditor re) {
	_ruleEditor = re;
    }
    
    public final XYCharContainer getPrototypes() {
	return prototypes;
    }
    
    public final XYCharContainer getSpecialPrototypes() {
	return specialPrototypes;
    }
    
    public final XYContainer getStages() {
	return stages;
    }
    
    public final XYContainer getSounds() {
	return sounds;
    }
    
    public final XYContainer getJars() {
	return jars;
    }
    
    public final XYCharContainer getTimeout() {
	return _timeoutBox;
    }
    
    public final XYContainer getBackgrounds() {
	return _backgrounds;
    }
    
    public final Stage getFirstVisibleStage() {
	return (Stage) visibleStageList.firstElement();
    }
    
    final void setClockView(ClockView cv) {
	clockView = cv;
    }
    
    final int getPrototypeCounter() {
	return prototypeCounter;
    }
    
    final void incrementPrototypeCounter() {
	prototypeCounter++;
    }
    
    final int getStageNameCounter() {
	return stageNameCounter;
    }
    
    static final String getPictureDirectory() {
	return _pictureDirectory;
    }
    
    static final void setPictureDirectory(String dir) {
	_pictureDirectory = dir;
    }
    
    static final String getBackgroundDirectory() {
	return _backgroundDirectory;
    }
    
    static final void setBackgroundDirectory(String dir) {
	_backgroundDirectory = dir;
    }
    
    static final String getSoundDirectory() {
	return _soundDirectory;
    }
    
    static final void setSoundDirectory(String dir) {
	_soundDirectory = dir;
    }
    
    public static final String getWorldDirectory() {
	return _worldDirectory;
    }
    
    public static final void setWorldDirectory(String dir) {
	_worldDirectory = dir;
    }
    
    public static final String getUploadConfigDirectory() {
	return _uploadConfigDirectory;
    }
    
    public final Hashtable getUploadSites() {
	return _uploadSites;
    }
    
    public final Properties getPropsForUploadSite(String site) {
	return new Properties((Properties) _uploadSites.get(site));
    }
    
    final void setSourceFile(File src) {
	_sourceFile = new File(src.getAbsolutePath());
	setName(Util.dePercentString(Util.getFilePart(src.getName())));
    }
    
    final File getSourceFile() {
	return _sourceFile;
    }
    
    public final String getSourceFileName() {
	return _sourceFile == null ? null : _sourceFile.getPath();
    }
    
    final void setMediaSource(ZipFile src) {
	if (Debug.lookup("debug.world")) {
	    String current
		= _mediaSource == null ? null : _mediaSource.getName();
	    String future = src == null ? null : src.getName();
	    Debug.print(true, "Existing media source: " + current,
			" set to: " + future);
	}
	if (_mediaSource != null) {
	    try {
		_mediaSource.close();
	    } catch (Throwable throwable) {
		/* empty */
	    }
	}
	_mediaSource = src;
    }
    
    final ZipFile getMediaSource() {
	return _mediaSource;
    }
    
    final void setCreatorVersion(String version) {
	_creatorVersion = version;
	if (_originalCreatorVersion == null)
	    setOriginalCreatorVersion(version);
    }
    
    final String getCreatorVersion() {
	return _creatorVersion;
    }
    
    final void setOriginalCreatorVersion(String version) {
	_originalCreatorVersion = version;
    }
    
    final String getOriginalCreatorVersion() {
	return _originalCreatorVersion;
    }
    
    final int getFSVersion() {
	return _fsVersion;
    }
    
    final void setFSVersion(int num) {
	_fsVersion = num;
    }
    
    public final ViewData getViewData() {
	return _viewData;
    }
    
    final boolean areClockTicksSuspended() {
	return _suspendTicks;
    }
    
    final void suspendClockTicks(boolean b) {
	_suspendTicks = b;
    }
    
    public final boolean updateAfterEveryChange() {
	return (_forceUpdateAfterEveryChange
		|| ((Boolean) showEachActionVar.getValue(this))
		       .booleanValue());
    }
    
    final void setForceUpdateAfterEveryChange(boolean force) {
	_forceUpdateAfterEveryChange = force;
    }
    
    final boolean runAllStages() {
	return ((Boolean) runStagesVar.getValue(this)).booleanValue();
    }
    
    final CharacterInstance getMainCharacter() {
	return (CharacterInstance) followMeVar.getValue(this);
    }
    
    final void setMainCharacter(CharacterInstance ch) {
	followMeVar.setValue(this, ch);
    }
    
    final int getSpeed() {
	return ((Number) Variable.systemVariable
			     (SYS_SPEED_VARIABLE_ID, this).getValue(this))
		   .intValue();
    }
    
    final void setSpeed(int speed) {
	Variable.systemVariable(SYS_SPEED_VARIABLE_ID, this)
	    .setValue(this, new Integer(speed));
    }
    
    final int getFrameRate() {
	return ((Number) Variable.systemVariable
			     (SYS_FRAME_RATE_VARIABLE_ID, this).getValue(this))
		   .intValue();
    }
    
    final void setFrameRate(int rate) {
	Variable.systemVariable(SYS_FRAME_RATE_VARIABLE_ID, this)
	    .setValue(this, new Integer(rate));
    }
    
    public final int getSimAhead() {
	return ((Number) Variable.systemVariable
			     (SYS_SIM_AHEAD_VARIABLE_ID, this).getValue(this))
		   .intValue();
    }
    
    public final void setSimAhead(int simAhead) {
	Variable.systemVariable(SYS_SIM_AHEAD_VARIABLE_ID, this)
	    .setValue(this, new Integer(simAhead));
    }
    
    final Color getColor() {
	return PlaywriteWindow.DEFAULT_BACKGROUND_COLOR;
    }
    
    final Color getLightColor() {
	return getColor().lighterColor();
    }
    
    final Color getDarkColor() {
	return getColor().darkerColor();
    }
    
    final boolean getCenterFollowMe() {
	return ((Boolean)
		Variable.systemVariable
		    (SYS_CENTER_FOLLOW_ME_VARIABLE_ID, this).getValue(this))
		   .booleanValue();
    }
    
    public final boolean isClosing() {
	return _closing;
    }
    
    final void suspendRecording(boolean b) {
	_recordingSuspended = b;
    }
    
    final boolean isRecordingSuspended() {
	return _recordingSuspended;
    }
    
    final PlaywriteLoader getLoader() {
	return PlaywriteRoot.app()._loader;
    }
    
    final PluginRegistry getEmbeddedPlugins() {
	return _embeddedPlugins;
    }
    
    final void setEmbeddedPlugins(PluginRegistry reg) {
	_embeddedPlugins = reg;
    }
    
    final void addRequiredPlugin(String plugin) {
	if (_requiredPlugins == null)
	    _requiredPlugins = new Vector(2);
	_requiredPlugins.addElementIfAbsent(plugin);
    }
    
    final Vector getRequiredPlugins() {
	return _requiredPlugins;
    }
    
    public final ObjectSieve getObjectSieve() {
	return _objectSieve;
    }
    
    public final VariableSieve getVariableSieve() {
	return _variableSieve;
    }
    
    public final ActionSieve getActionSieve() {
	return _actionSieve;
    }
    
    final void setRuleMatched() {
	_ruleMatched = true;
    }
    
    public final void incrementBadCharacterCount() {
	_badCharacters++;
    }
    
    final Stage getCurrentStageDRHack() {
	return _currentStageDRHack;
    }
    
    final void setCurrentStageDRHack(Stage stage) {
	_currentStageDRHack = stage;
    }
    
    PlaywriteWindow createWindow() {
	Size size = PlaywriteRoot.getRootWindowSize();
	_worldWindow = new WorldWindow(0, 0, size.width, size.height, this);
	_worldWindow.setDisablable(true);
	_worldWindow.setRootView(PlaywriteRoot.getMainRootView());
	_worldWindow.setTitle(getName());
	_worldWindow.setCloseable(false);
	_worldView = new WorldView(_worldWindow, this);
	_worldView.setVertResizeInstruction(16);
	_worldView.setHorizResizeInstruction(2);
	if (_viewData.getData("WW Show Menu") == null) {
	    Stage theOneAndOnly = (Stage) stages.getContents().nextElement();
	    showStage(theOneAndOnly);
	    Size s
		= _worldView.getWindowSizeForStageSize(theOneAndOnly
							   .desiredWidth(),
						       theOneAndOnly
							   .desiredHeight());
	    _worldWindow.sizeTo(s.width, s.height);
	} else {
	    if (_controlPanelIsVisible)
		_worldView.showControlPanel();
	    else
		_worldView.hideControlPanel();
	    if (_sidelineIsVisible)
		_worldView.showSidelines();
	    else
		_worldView.hideSidelines();
	    setViewData(_viewData.getData("WW Show Menu"));
	}
	if (isLocked())
	    handleLockingStateHasChanged(Boolean.TRUE);
	if (PlaywriteSystem.getApplicationPropertyAsBoolean("drawing_enabled",
							    true))
	    setDrawingModule(new AuthorWorldDrawingModule(this));
	else
	    setDrawingModule(new AuthorWorldDrawingModule(this) {
		public void handleDrawEvent(boolean updateStages) {
		    super.handleDrawEvent(false);
		}
	    });
	return _worldWindow;
    }
    
    public int desiredWidth() {
	int result = 0;
	if (getWorldView() != null) {
	    int stageWidth = 0;
	    Stage stage = getStageAtIndex(0);
	    if (stage != null)
		stageWidth = stage.desiredWidth();
	    else
		stageWidth = 640;
	    int scrollerWidth = 2 * ScrollableArea.SCROLL_ARROW_WIDTH;
	    int borderWidth = (_worldWindow.border().leftMargin()
			       + _worldWindow.border().rightMargin());
	    result = stageWidth + scrollerWidth + borderWidth;
	}
	return result;
    }
    
    public int desiredHeight() {
	int result = 0;
	if (getWorldView() != null) {
	    int stageHeight = 0;
	    Stage stage = getStageAtIndex(0);
	    if (stage != null)
		stageHeight = stage.desiredHeight();
	    else
		stageHeight = 448;
	    int scrollerHeight = 2 * ScrollableArea.SCROLL_ARROW_WIDTH;
	    int borderHeight = (_worldWindow.border().topMargin()
				+ _worldWindow.border().bottomMargin());
	    result
		= (stageHeight + _worldView.getControlPanelScroller().height()
		   + scrollerHeight + borderHeight);
	}
	return result;
    }
    
    void oneClockCycle() {
	if (directionForward) {
	    Enumeration activeStages = null;
	    Stage stage = null;
	    beginClockCycle();
	    if (runAllStages())
		activeStages = stages.getContents();
	    else if (visibleStageList.size() == 1)
		stage = (Stage) visibleStageList.elementAt(0);
	    else
		activeStages = visibleStageList.elements();
	    if (activeStages == null)
		executeStage(stage);
	    else {
		while (activeStages.hasMoreElements()) {
		    stage = (Stage) activeStages.nextElement();
		    executeStage(stage);
		}
	    }
	    endClockCycle();
	} else if (historyList.isEmpty())
	    stopWorld();
	else {
	    if (!activeEvents.isEmpty() || !pendingActiveEvents.isEmpty()) {
		activeEvents = new Vector(5);
		pendingActiveEvents = new Vector(5);
	    }
	    undoActions();
	}
    }
    
    private final void executeStage(Stage stage$) {
	stage$.execute(isStepping() && updateAfterEveryChange());
    }
    
    private void beginClockCycle() {
	startCycle = System.currentTimeMillis();
	if (!_suspendTicks)
	    tick();
	CharacterInstance followed = getMainCharacter();
	if (followed != null && followed.getContainer() instanceof Stage)
	    _followMeStageStart = (Stage) followed.getContainer();
	else
	    _followMeStageStart = null;
    }
    
    private void endClockCycle() {
	deltaCycle = System.currentTimeMillis() - startCycle;
	if (deltaCycle > maxCycle)
	    maxCycle = deltaCycle;
	if (deltaCycle < minCycle)
	    minCycle = deltaCycle;
	totalCycle += deltaCycle;
	cycles++;
	if (!activeEvents.isEmpty() || !pendingActiveEvents.isEmpty()) {
	    activeEvents.removeAllElements();
	    Vector newPending = new Vector(5);
	    for (int i = 0; i < pendingActiveEvents.size(); i++) {
		PlaywriteEvent ev
		    = (PlaywriteEvent) pendingActiveEvents.elementAt(i);
		if (ev.getClockTick() <= ticks)
		    activeEvents.addElement(ev);
		else
		    newPending.addElement(ev);
	    }
	    pendingActiveEvents = newPending;
	}
	if (isRunning())
	    checkMakeFollowMeVisible();
    }
    
    private void checkMakeFollowMeVisible() {
	if (isRunning()) {
	    CharacterInstance followMe = getMainCharacter();
	    if (followMe != null && followMe.getContainer() instanceof Stage) {
		Stage stage = (Stage) followMe.getContainer();
		PlaywriteRoot.app();
		if (!PlaywriteRoot.isServer())
		    getWorld().addSyncAction(this, "make followMe visible",
					     followMe);
		else
		    makeCharacterVisible(followMe);
	    }
	}
    }
    
    private void makeCharacterVisible(CharacterInstance followMe) {
	Stage stage = (Stage) followMe.getContainer();
	int stageViewIndex = getStageViewIndex(_followMeStageStart);
	if (stageViewIndex < 0)
	    stageViewIndex = 0;
	if (!stage.isViewed())
	    executeAction(new SwitchStageAction(stage, stageViewIndex),
			  _followMeStageStart, 0, 0);
	if (_worldView != null) {
	    BoardView currentBoardView = _worldView.getStageView(stage);
	    int oldX = currentBoardView.bounds.x;
	    int oldY = currentBoardView.bounds.y;
	    CharacterView followMeView = currentBoardView.getViewFor(followMe);
	    followMeView.didMoveBy(0, 0);
	    if (currentBoardView.bounds.x != oldX
		|| currentBoardView.bounds.y != oldY)
		currentBoardView.setDirty(true);
	}
    }
    
    public void run() {
	try {
	    _run();
	} catch (RuntimeException e) {
	    _killerException = e;
	    throw e;
	}
    }
    
    public Exception getKillerException() {
	return _killerException;
    }
    
    private void _run() {
	nextFrameTime = 9223372036854775807L;
	while (!worldThreadDone) {
	    performWorldThreadCommands();
	    blockForClock();
	    if (_packetProcessor != null)
		_packetProcessor.processPackets(false);
	    if (wantToRun) {
		synchronized (activeEvents) {
		    isRunningNow = true;
		}
		_hasWaitedForTick = false;
		_ruleMatched = false;
		oneClockCycle();
		if (updateAfterEveryChange() && _hasWaitedForTick)
		    unblock();
		_drawingModule.requestDrawStages();
		isRunningNow = false;
		if (wantToRun && !_ruleMatched)
		    blockForActivity();
		else if (PlaywriteRoot.isServer())
		    blockForClient();
	    }
	    if (!worldThreadDone
		&& (!wantToRun || stepping && !isSuspendedForDebug())) {
		setBlocked();
		nextFrameTime = 9223372036854775807L;
		resetGeneralizedCharacters();
		_root.performCommandAndWait(new Target() {
		    public void performCommand(String s, Object data) {
			World.this.changeStateToStopped();
			screenRefresh();
			if (_resetOnStop && !_closing)
			    World.this.resetAndRestart();
		    }
		}, null, null);
	    }
	}
    }
    
    private void changeStateToStopped() {
	wantToRun = false;
	changeState(STOP);
	totalTime = System.currentTimeMillis() - startTime;
	printStatistics();
    }
    
    private void resetAndRestart() {
	_resetOnStop = false;
	loadState();
	if (!stepping)
	    runForward();
    }
    
    final boolean isRunning() {
	return isRunningNow || wantToRun;
    }
    
    final boolean wantsToRun() {
	return wantToRun;
    }
    
    final boolean isRunningForward() {
	return isRunning() && directionForward;
    }
    
    public void performInWorldThread(Target target, String command,
				     Object data, boolean cascade) {
	if (isRunning()) {
	    synchronized (_callFromWorldThread) {
		int size = _callFromWorldThread.size();
		if (cascade) {
		    for (int i = 0; i < size; i++) {
			PerformCommandTuple pct
			    = ((PerformCommandTuple)
			       _callFromWorldThread.elementAt(i));
			if (target == pct.target
			    && command.equals(pct.command))
			    return;
		    }
		}
		PerformCommandTuple tuple
		    = new PerformCommandTuple(target, command, data);
		_callFromWorldThread.addElement(tuple);
	    }
	} else
	    PlaywriteRoot.app().performCommandAndWait(target, command, data);
    }
    
    private void performWorldThreadCommands() {
	synchronized (_callFromWorldThread) {
	    int size = _callFromWorldThread.size();
	    for (int i = 0; i < size; i++) {
		PerformCommandTuple pct
		    = (PerformCommandTuple) _callFromWorldThread.elementAt(i);
		pct.target.performCommand(pct.command, pct.data);
	    }
	    _callFromWorldThread.removeAllElements();
	}
    }
    
    synchronized void blockForClock() {
	if (worldThread != null)
	    ASSERT.isIdentical(Thread.currentThread(), worldThread);
	for (/**/; blocked && !worldThreadDone; blocked = false) {
	    long timeNow;
	    for (timeNow = System.currentTimeMillis();
		 timeNow + frameSlop < nextFrameTime;
		 timeNow = System.currentTimeMillis()) {
		long sleepTime = (nextFrameTime - timeNow) / 3L;
		if (sleepTime > 0L) {
		    try {
			this.wait(sleepTime);
		    } catch (InterruptedException interruptedexception) {
			/* empty */
		    }
		}
	    }
	    nextFrameTime += frameMillis;
	    if (nextFrameTime < timeNow)
		nextFrameTime = timeNow + frameMillis;
	}
	if (!freerun)
	    blocked = true;
    }
    
    synchronized void blockForClient() {
	if (worldThread != null)
	    ASSERT.isIdentical(Thread.currentThread(), worldThread);
	while (blocked && !worldThreadDone && isRunning()) {
	    if (_packetProcessor != null)
		_packetProcessor.processPackets(true);
	}
    }
    
    synchronized void blockForActivity() {
	boolean isServer = PlaywriteRoot.isServer();
	if (isServer && !stepping && !isSuspendedForDebug()
	    && directionForward) {
	    while (activeEvents.isEmpty() && !worldThreadDone && isRunning()) {
		if (isServer) {
		    setBlocked();
		    blockForClient();
		} else {
		    try {
			this.wait(0L);
		    } catch (InterruptedException interruptedexception) {
			/* empty */
		    }
		}
	    }
	}
    }
    
    public synchronized void setBlocked() {
	blocked = true;
    }
    
    public synchronized void unblock() {
	blocked = false;
	this.notifyAll();
    }
    
    public final void markWorldThreadDone() {
	worldThreadDone = true;
	wantToRun = false;
	nextFrameTime = 0L;
	unblock();
	changeStateToStopped();
    }
    
    boolean isSuspendedForDebug() {
	return getState() == DEBUGGING;
    }
    
    synchronized void suspendForDebug() {
	Debug.print("debug.debugging", "World:suspending");
	Target t = new Target() {
	    public void performCommand(String s, Object data) {
		changeState(World.BREAKPOINT);
	    }
	};
	PlaywriteRoot.app().performCommandAndWait(t, null, null);
	while (isSuspendedForDebug()) {
	    try {
		this.wait();
	    } catch (InterruptedException interruptedexception) {
		Debug.print("debug.debugging", "World suspension interrupted");
	    }
	}
	Debug.print("debug.debugging", "World:resuming");
    }
    
    synchronized void clearDebug() {
	Debug.print("debug.debugging", "World:clearing debug");
	changeState(RUN);
	this.notifyAll();
    }
    
    public Object getState() {
	return worldState.getState();
    }
    
    public void changeState(Object transition) {
	worldState.changeState(transition);
    }
    
    boolean legalTransition(Object transition) {
	return worldState.isLegal(transition);
    }
    
    public void addStateWatcher(StateWatcher w) {
	worldState.addWatcher(w);
    }
    
    public void removeStateWatcher(StateWatcher w) {
	worldState.removeWatcher(w);
    }
    
    public boolean inWorldThread() {
	return Thread.currentThread() == worldThread;
    }
    
    public void addClockWatcher(Watcher w) {
	if (_clockWatcherManager == null)
	    _clockWatcherManager = new UpdateManager();
	_clockWatcherManager.add(w);
    }
    
    public void removeClockWatcher(Watcher w) {
	if (_clockWatcherManager != null)
	    _clockWatcherManager.remove(w);
    }
    
    public int getTime() {
	return ticks;
    }
    
    int tick() {
	if (!isSuspendedForDebug()) {
	    ticks++;
	    updateClock();
	    if (--maxTicks == 0)
		stopWorld();
	}
	return ticks;
    }
    
    private int untick() {
	ticks--;
	updateClock();
	return ticks;
    }
    
    private void updateClock() {
	if (clockView != null)
	    clockView.drawTick();
	if (_clockWatcherManager != null)
	    _clockWatcherManager.update(this, null);
    }
    
    void computeSpeedParams() {
	PlaywriteRoot.app();
	int speed;
	if (PlaywriteRoot.isServer())
	    speed = 3;
	else
	    speed = getSpeed();
	long frameRate = (long) getFrameRate();
	if (frameRateOverride == null) {
	    frameMillis = 1000L / frameRate;
	    switch (speed) {
	    case 0:
		frameMillis = frameMillis * 3L;
		freerun = false;
		break;
	    case 1:
		freerun = false;
		break;
	    case 2:
		frameMillis = frameMillis / 3L;
		freerun = false;
		break;
	    case 3:
		frameMillis = 0L;
		freerun = true;
		break;
	    default:
		break;
	    }
	} else {
	    freerun = false;
	    frameRate = (long) frameRateOverride[speed];
	    frameMillis = 1000L / frameRate;
	}
	frameSlop = frameMillis * 10L / 100L;
    }
    
    final boolean timeIsForward() {
	return directionForward;
    }
    
    final boolean timeIsBackward() {
	return directionForward ^ true;
    }
    
    void runForward() {
	resetStatistics();
	startTime = System.currentTimeMillis();
	resetSelectionState();
	setRunFlags(true, false);
	if (isSuspendedForDebug())
	    clearDebug();
	maxTicks = _root.getMaxCycles();
    }
    
    public final void resetSelectionState() {
	PlaywriteRoot.app();
	if (!PlaywriteRoot.isServer())
	    Selection.resetGlobalState();
    }
    
    void runBackward() {
	if (!isSuspendedForDebug()) {
	    if (getState() == RUNNING && directionForward)
		forceStop();
	    startTime = 0L;
	    resetSelectionState();
	    setRunFlags(false, false);
	}
    }
    
    void stepForward() {
	startTime = 0L;
	resetSelectionState();
	setRunFlags(true, true);
    }
    
    void stepBackward() {
	if (!isSuspendedForDebug()) {
	    if (getState() == RUNNING && directionForward)
		forceStop();
	    startTime = 0L;
	    resetSelectionState();
	    setRunFlags(false, true);
	}
    }
    
    private void setRunFlags(boolean fwd, boolean step) {
	directionForward = fwd;
	stepping = step;
	wantToRun = true;
	nextFrameTime = System.currentTimeMillis();
	changeState(RUN);
	unblock();
    }
    
    public void stopWorld() {
	if (!isSuspendedForDebug()) {
	    wantToRun = false;
	    unblock();
	}
    }
    
    void setForceStopFlag(boolean b) {
	_forceStopFlag = b;
    }
    
    public boolean wantsForceStop() {
	return _forceStopFlag;
    }
    
    public boolean hasPreviousCopy(ReferencedObject obj) {
	if (obj.getWorld() == this)
	    return false;
	ReferencedObject dup = null;
	if (obj instanceof Jar)
	    dup = findCopy((Jar) obj);
	else if (obj instanceof Stage)
	    dup = findCopy((Stage) obj);
	else if (obj instanceof CharacterPrototype)
	    dup = findCopy((CharacterPrototype) obj);
	else if (obj instanceof Appearance)
	    dup = findCopy((Appearance) obj);
	else if (obj instanceof PlaywriteSound)
	    dup = findCopy((PlaywriteSound) obj);
	if (dup == null)
	    return false;
	String clName = obj.getClass().getName();
	String srcObj = obj.toString();
	String destWorld = getName();
	String srcWorld = obj.getWorld().getName();
	String destObj = dup.toString();
	int dotPos = clName.lastIndexOf(".");
	clName = clName.substring(dotPos + 1).toLowerCase();
	if (clName.equals("jar"))
	    clName = Resource.getText("REFOBJ jar ID");
	else if (clName.equals("stage"))
	    clName = Resource.getText("REFOBJ stg ID");
	else if (clName.equals("characterprototype"))
	    clName = Resource.getText("REFOBJ pro ID");
	else if (clName.equals("appearance"))
	    clName = Resource.getText("REFOBJ app ID");
	else if (clName.equals("playwritesound"))
	    clName = Resource.getText("REFOBJ sou ID");
	String msg
	    = Resource.getTextAndFormat("dialog dce",
					new Object[] { clName, srcObj,
						       destWorld, srcWorld,
						       destObj });
	PlaywriteDialog.warning(msg, true);
	return true;
    }
    
    void add(CharacterPrototype prototype) {
	if (!contains(prototype)) {
	    if (prototype.isSpecial())
		specialPrototypes.add(prototype);
	    else {
		prototypes.add(prototype);
		incrementPrototypeCounter();
	    }
	}
    }
    
    void remove(CharacterPrototype prototype) {
	if (prototype.isSpecial())
	    specialPrototypes.deleteCharacter(prototype);
	else
	    prototypes.deleteCharacter(prototype);
    }
    
    boolean contains(CharacterPrototype prototype) {
	if (prototype.isSpecial())
	    return specialPrototypes.contains(prototype);
	return prototypes.contains(prototype);
    }
    
    CharacterPrototype makeNewPrototype() {
	return makeNewPrototype(getFirstVisibleStage().getSquareSize());
    }
    
    CharacterPrototype makeNewPrototype(int squareSize) {
	if (PlaywriteRoot.hasAuthoringLimits()
	    && evalLimitForClassReached(CharacterPrototype.class, 1)) {
	    evalLimitDialog(CharacterPrototype.class);
	    return null;
	}
	Bitmap image
	    = Resource.getImage("CP new character",
				(new Object[]
				 { new Integer(getSplatImageCount()) }));
	nextSplatImage++;
	if (nextSplatImage >= 10)
	    nextSplatImage = 0;
	Rect size = new Rect(0, 0, squareSize, squareSize);
	Util.scaleRectToImageProportion(size, image);
	if (image.width() != size.width || image.height() != size.height) {
	    Bitmap temp
		= BitmapManager.createScaledBitmapManager(image, size.width,
							  size.height);
	    image.flush();
	    image = temp;
	}
	Appearance appear = new Appearance(null, image, squareSize,
					   new Shape(1, 1, new Point(1, 1)));
	CharacterPrototype prototype
	    = new CharacterPrototype(this, null, appear);
	return prototype;
    }
    
    int getSplatImageCount() {
	return nextSplatImage;
    }
    
    CharacterPrototype findCopy(CharacterPrototype prototype) {
	Enumeration e = prototypes.getContents();
	while (e.hasMoreElements()) {
	    CharacterPrototype p = (CharacterPrototype) e.nextElement();
	    if (p.isCopyOf(prototype))
		return p;
	}
	Enumeration e_6_ = specialPrototypes.getContents();
	while (e_6_.hasMoreElements()) {
	    CharacterPrototype p = (CharacterPrototype) e_6_.nextElement();
	    if (p.isCopyOf(prototype))
		return p;
	}
	return null;
    }
    
    Appearance findCopy(Appearance app) {
	CharacterPrototype p = null;
	Appearance theCopy = null;
	Enumeration e = prototypes.getContents();
	while (e.hasMoreElements()) {
	    p = (CharacterPrototype) e.nextElement();
	    theCopy = p.findCopy(app);
	    if (theCopy != null)
		return theCopy;
	}
	Enumeration e_7_ = specialPrototypes.getContents();
	while (e_7_.hasMoreElements()) {
	    p = (CharacterPrototype) e_7_.nextElement();
	    theCopy = p.findCopy(app);
	    if (theCopy != null)
		return theCopy;
	}
	return null;
    }
    
    boolean ruleRefersTo(ReferencedObject obj, String typeID) {
	int count = getWorld().countRulesReferringTo(obj);
	if (count == 0)
	    return false;
	Object[] params = { new Integer(count), Resource.getText(typeID) };
	PlaywriteDialog dialog
	    = new PlaywriteDialog(Resource.getTextAndFormat("dialog rrt",
							    params),
				  "command sr", "command c");
	if (dialog.getAnswer().equals("command sr")) {
	    PlaywriteRoot.markBusy();
	    final Rule rule = getWorld().findRuleReferringTo(obj);
	    rule.setRuleEditorObject(rule.findReferenceTo(obj));
	    rule.showInCharacterWindow(rule.getOwner());
	    PlaywriteView itemView = null;
	    if (!(rule instanceof Pretest))
		itemView = rule.getView(rule.getOwner());
	    PlaywriteRoot.clearBusy();
	    if (itemView != null) {
		Target thunk = new Target() {
		    public void performCommand(String command, Object agr) {
			Selection.select(rule, ((View) agr).superview());
		    }
		};
		Application.application().performCommandLater(thunk, null,
							      itemView);
	    }
	}
	return true;
    }
    
    int countRulesReferringTo(final ReferencedObject obj) {
	RuleListItem.IterationProcessor ruleUpdater = new RuleListItem.IterationProcessor() {
	    public Object processItem(RuleListItem item, Object lastValue) {
		Point point = (Point) lastValue;
		if (item.refersTo(obj))
		    point.x++;
		return point;
	    }
	    
	    public boolean done(Object lastValue) {
		return false;
	    }
	};
	Point point = new Point(0, 0);
	if (obj instanceof CharacterPrototype)
	    iterateOverRules(ruleUpdater, point, (CharacterPrototype) obj);
	else
	    iterateOverRules(ruleUpdater, point, null);
	return point.x;
    }
    
    Rule findRuleReferringTo(final ReferencedObject obj) {
	RuleListItem.IterationProcessor ruleFinder = new RuleListItem.IterationProcessor() {
	    public Object processItem(RuleListItem item, Object lastValue) {
		if (item.refersTo(obj) && item instanceof Rule)
		    return item;
		return null;
	    }
	    
	    public boolean done(Object lastValue) {
		return lastValue != null;
	    }
	};
	Rule rule;
	if (obj instanceof CharacterPrototype)
	    rule = (Rule) iterateOverRules(ruleFinder, null,
					   (CharacterPrototype) obj);
	else
	    rule = (Rule) iterateOverRules(ruleFinder, null, null);
	return rule;
    }
    
    Object iterateOverRules(RuleListItem.IterationProcessor updater,
			    Object lastValue) {
	return iterateOverRules(updater, lastValue, null);
    }
    
    Object iterateOverRules(RuleListItem.IterationProcessor updater,
			    Object lastValue, CharacterPrototype ignoreThis) {
	Enumeration e = prototypes.getContents();
	while (e.hasMoreElements()) {
	    CharacterPrototype prototype
		= (CharacterPrototype) e.nextElement();
	    if (prototype != ignoreThis) {
		lastValue = prototype.getMainSubroutine().iterate(updater,
								  lastValue);
		if (updater.done(lastValue))
		    return lastValue;
	    }
	}
	Enumeration e_16_ = specialPrototypes.getContents();
	while (e_16_.hasMoreElements()) {
	    CharacterPrototype prototype
		= (CharacterPrototype) e_16_.nextElement();
	    if (prototype != ignoreThis) {
		lastValue = prototype.getMainSubroutine().iterate(updater,
								  lastValue);
		if (updater.done(lastValue))
		    return lastValue;
	    }
	}
	return lastValue;
    }
    
    private void initializeVariables() {
	Variable v
	    = Variable.newSystemVariable(SYS_ENABLE_SOUND_VARIABLE_ID, this);
	v.setValue(this, Boolean.TRUE);
	add(v);
	showEachActionVar
	    = Variable.newSystemVariable(SYS_SHOW_ALL_ACTIONS_VARIABLE_ID,
					 this);
	showEachActionVar.setValue(this, Boolean.TRUE);
	add(showEachActionVar);
	add(runStagesVar
	    = Variable.newSystemVariable(SYS_RUN_ALL_STAGES_VARIABLE_ID,
					 this));
	runStagesVar.setValue(this, Boolean.FALSE);
	add(runStagesVar);
	v = Variable.newSystemVariable(SYS_ENABLE_GRID_VARIABLE_ID, this);
	v.setValue(this, Boolean.FALSE);
	v.addValueWatcher(this, new Watcher() {
	    public void update(Object var, Object data) {
		addSyncAction(World.this, World.SYS_ENABLE_GRID_VARIABLE_ID,
			      null);
	    }
	});
	add(v);
	v = Variable.newSystemVariable(SYS_CENTER_FOLLOW_ME_VARIABLE_ID, this);
	v.setValue(this, Boolean.FALSE);
	add(v);
	add(v = Variable.newSystemVariable(SYS_WINDOW_COLOR_VARIABLE_ID,
					   this));
	ColorValue val = defaultColor;
	v.setValue(this, val);
	add(v = Variable.newSystemVariable(SYS_FRAME_RATE_VARIABLE_ID, this));
	setFrameRate(10);
	v.addValueWatcher(this, new Watcher() {
	    public void update(Object var, Object data) {
		computeSpeedParams();
	    }
	});
	add(v = Variable.newSystemVariable(SYS_SIM_AHEAD_VARIABLE_ID, this));
	setSimAhead(4);
	v.addValueWatcher(this, new Watcher() {
	    public void update(Object var, Object data) {
		/* empty */
	    }
	});
	add(Variable.newSystemVariable(SYS_ENVIRONMENT_VARIABLE_ID, this));
	add(Variable.newSystemVariable(SYS_APPLET_PARAMETER_VARIABLE_ID,
				       this));
	add(v = Variable.newSystemVariable(SYS_WORLD_LOCKED_VARIABLE_ID,
					   this));
	v.setValue(this, Boolean.FALSE);
	Watcher w = new Watcher() {
	    public void update(Object var, Object data) {
		handleLockingStateHasChanged((Boolean) data);
	    }
	};
	v.addValueWatcher(this, w);
	followMeVar
	    = Variable.newSystemVariable(SYS_FOLLOW_ME_VARIABLE_ID, this);
	add(followMeVar);
	add(v = Variable.newSystemVariable(SYS_HISTORY_SIZE_VARIABLE_ID,
					   this));
	v.setValue(this, new Integer(2000));
	if (!PlaywriteRoot.isFinalBuild())
	    v.addValueWatcher(this, new Watcher() {
		public void update(Object var, Object data) {
		    int newSize = ((Number) data).intValue();
		    historyList = new ActionRingBuffer(newSize);
		    resetHistoryList();
		}
	    });
	add(Variable.newSystemVariable(SYS_WORLD_NAME_VARIABLE_ID, this));
	add(v = Variable.newSystemVariable(SYS_SPEED_VARIABLE_ID, this));
	setSpeed(2);
	v.addValueWatcher(this, new Watcher() {
	    public void update(Object var, Object data) {
		computeSpeedParams();
	    }
	});
	computeSpeedParams();
    }
    
    void add(Variable variable) {
	globalVariables.add(variable);
    }
    
    void add(Variable variable, Point loc) {
	globalVariables.addAt(variable, loc);
    }
    
    Variable findCopy(Variable variable) {
	Enumeration e = globalVariables.elements();
	while (e.hasMoreElements()) {
	    Variable myVar = (Variable) e.nextElement();
	    if (myVar.isCopyOf(variable))
		return myVar;
	}
	return null;
    }
    
    void add(PlaywriteSound sound) {
	sounds.add(sound);
    }
    
    void remove(PlaywriteSound sound) {
	sounds.remove(sound);
    }
    
    PlaywriteSound findCopy(PlaywriteSound sound) {
	if (ObjectProxy.indexFor(sound) >= 0)
	    return sound;
	Enumeration e = sounds.getContents();
	while (e.hasMoreElements()) {
	    PlaywriteSound s = (PlaywriteSound) e.nextElement();
	    if (s.isCopyOf(sound))
		return s;
	}
	return null;
    }
    
    boolean isOkToDrop(Object obj) {
	boolean result = true;
	if (PlaywriteRoot.hasAuthoringLimits() && obj instanceof Worldly
	    && ((Worldly) obj).getWorld() != this) {
	    if (obj instanceof CocoaCharacter) {
		CocoaCharacter ch = (CocoaCharacter) obj;
		if (willBeAdded(ch.getPrototype()))
		    result = isOkToCopyWithDialog(obj);
	    } else if (obj instanceof Stage)
		result = isOkToCopyWithDialog(obj);
	}
	return result;
    }
    
    final boolean isOkToCopyWithDialog(Object obj) {
	Class failClass = isOkToCopy(obj);
	if (failClass != null) {
	    evalLimitDialog(failClass);
	    return false;
	}
	return true;
    }
    
    final Class isOkToCopy(Object obj) {
	Class result = null;
	if (PlaywriteRoot.hasAuthoringLimits()) {
	    if (obj instanceof Stage) {
		int numberOfPrototypesAdded
		    = countPrototypesAdded((Stage) obj);
		int numberOfRulesAdded = countRulesAdded((Stage) obj);
		if (evalLimitForClassReached(Stage.class, 1))
		    result = Stage.class;
		else if (numberOfPrototypesAdded > 0
			 && evalLimitForClassReached(CharacterPrototype.class,
						     numberOfPrototypesAdded))
		    result = CharacterPrototype.class;
		else if (numberOfRulesAdded > 0
			 && evalLimitForClassReached(Rule.class,
						     numberOfRulesAdded))
		    result = Rule.class;
	    } else if (obj instanceof CharacterPrototype) {
		int numberOfRulesAdded
		    = ((CharacterPrototype) obj).getMainSubroutine()
			  .getRuleCount();
		if (evalLimitForClassReached(obj.getClass(), 1))
		    result = CharacterPrototype.class;
		else if (numberOfRulesAdded > 0
			 && evalLimitForClassReached(Rule.class,
						     numberOfRulesAdded))
		    result = Rule.class;
	    } else if (obj instanceof RuleListItem) {
		int numberOfRulesAdded = ((RuleListItem) obj).getRuleCount();
		if (numberOfRulesAdded > 0
		    && evalLimitForClassReached(Rule.class,
						numberOfRulesAdded))
		    result = Rule.class;
	    } else if (obj instanceof CharacterInstance
		       && willBeAdded(((CharacterInstance) obj)
					  .getPrototype()))
		result = isOkToCopy(((CharacterInstance) obj).getPrototype());
	}
	return result;
    }
    
    boolean evalLimitForObjectReached(Object obj) {
	if (obj instanceof Proxy && ((Proxy) obj).isProxy())
	    return false;
	if (obj instanceof Door && numberOfDoors() % 2 != 0)
	    return false;
	return evalLimitForClassReached(obj.getClass(), 1);
    }
    
    private int numberOfDoors() {
	Enumeration e = getSpecialPrototypes().getContents();
	int result = 0;
	while (e.hasMoreElements()) {
	    Proxy p = (Proxy) e.nextElement();
	    if (p instanceof Door)
		result++;
	}
	return result;
    }
    
    boolean evalLimitForClassReached(Class c) {
	return evalLimitForClassReached(c, 1);
    }
    
    boolean evalLimitForClassReached(Class c, int numberOfAdditionalObjects) {
	if (Tutorial.isTutorialRunning())
	    return false;
	int totalProtoLimit = 6;
	int stageLimit = 2;
	int ruleLimit = 10;
	if (CharacterPrototype.class.isAssignableFrom(c)) {
	    int protoCount = sizeWithoutProxies(getPrototypes());
	    boolean isRegularPrototype = CharacterPrototype.class == c;
	    if (((numberOfAdditionalObjects + protoCount
		  + sizeWithoutProxies(getSpecialPrototypes()))
		 > totalProtoLimit)
		|| (isRegularPrototype
		    && numberOfAdditionalObjects + protoCount > 3))
		return true;
	} else if (c == Stage.class) {
	    if (numberOfAdditionalObjects + sizeWithoutProxies(getStages())
		> stageLimit)
		return true;
	} else if (c == Rule.class
		   && (numberOfAdditionalObjects + getNumberOfRulesInWorld()
		       > ruleLimit))
	    return true;
	return false;
    }
    
    private int sizeWithoutProxies(XYContainer cont) {
	Enumeration e = cont.getContents();
	int result = 0;
	while (e.hasMoreElements()) {
	    Proxy p = (Proxy) e.nextElement();
	    if (!p.isProxy() && p.isVisible())
		result++;
	}
	return result;
    }
    
    int getNumberOfStagesInWorld() {
	return sizeWithoutProxies(stages);
    }
    
    int getNumberOfPrototypesInWorld() {
	return (sizeWithoutProxies(getPrototypes())
		+ sizeWithoutProxies(getSpecialPrototypes()));
    }
    
    int getNumberOfRulesInWorld() {
	int size = 0;
	Enumeration e = getPrototypes().getContents();
	while (e.hasMoreElements()) {
	    CharacterPrototype prototype
		= (CharacterPrototype) e.nextElement();
	    if (prototype.isVisible())
		size += prototype.getMainSubroutine().getRuleCount();
	}
	Enumeration e_29_ = getSpecialPrototypes().getContents();
	while (e_29_.hasMoreElements()) {
	    CharacterPrototype prototype
		= (CharacterPrototype) e_29_.nextElement();
	    if (prototype.isVisible())
		size += prototype.getMainSubroutine().getRuleCount();
	}
	return size;
    }
    
    private int countPrototypesAdded(Stage stage) {
	return prototypesAdded(stage).size();
    }
    
    private Vector prototypesAdded(Stage stage) {
	Vector characters = stage.getCharacters();
	Vector newPrototypes = new Vector(10);
	for (int i = 0; i < characters.size(); i++) {
	    CharacterPrototype prototype
		= ((CharacterInstance) characters.elementAt(i)).getPrototype();
	    if (willBeAdded(prototype)
		&& !newPrototypes.containsIdentical(prototype))
		newPrototypes.addElement(prototype);
	}
	return newPrototypes;
    }
    
    private int countRulesAdded(Stage stage) {
	int count = 0;
	Vector newPrototypes = prototypesAdded(stage);
	for (int i = 0; i < newPrototypes.size(); i++) {
	    CharacterPrototype prototype
		= (CharacterPrototype) newPrototypes.elementAt(i);
	    count += prototype.getMainSubroutine().getRuleCount();
	}
	return count;
    }
    
    private boolean willBeAdded(CharacterPrototype prototype) {
	return (prototype.getWorld() != this
		&& (findCopy(prototype) == null
		    || findCopy(prototype).isProxy()));
    }
    
    public void evalLimitDialog(Class c) {
	String resID = null;
	int number = -1;
	if (CharacterPrototype.class.isAssignableFrom(c)) {
	    resID = "dialog eval limit prototype";
	    number = 3;
	} else if (c == Stage.class) {
	    resID = "dialog eval limit stage";
	    number = 2;
	} else if (c == Rule.class) {
	    resID = "dialog eval limit rule";
	    number = 10;
	}
	if (number != -1) {
	    String evalType = Resource.getText("root ptd");
	    Object[] param = { evalType, new Integer(number) };
	    PlaywriteDialog dialog
		= new PlaywriteDialog(Resource.getTextAndFormat(resID, param),
				      "command ok");
	    PlaywriteRoot.app().performCommandLater(PlaywriteRoot.app(),
						    "show modal dialog",
						    dialog);
	}
    }
    
    void add(BackgroundImage bg) {
	_backgrounds.add(bg);
    }
    
    void remove(BackgroundImage bg) {
	_backgrounds.remove(bg);
    }
    
    BackgroundImage findCopy(BackgroundImage bg) {
	if (ObjectProxy.indexFor(bg) >= 0)
	    return bg;
	Enumeration e = _backgrounds.getContents();
	while (e.hasMoreElements()) {
	    BackgroundImage image = (BackgroundImage) e.nextElement();
	    if (image.isCopyOf(bg))
		return image;
	}
	return null;
    }
    
    void add(Jar jar) {
	jars.add(jar);
    }
    
    void remove(Jar jar) {
	jars.remove(jar);
    }
    
    Jar findCopy(Jar jar) {
	Enumeration e = jars.getContents();
	while (e.hasMoreElements()) {
	    Jar j = (Jar) e.nextElement();
	    if (j.isCopyOf(jar))
		return j;
	}
	return null;
    }
    
    Stage findCopy(Stage s) {
	Enumeration e = stages.getContents();
	while (e.hasMoreElements()) {
	    Stage stage = (Stage) e.nextElement();
	    if (stage.isCopyOf(s))
		return stage;
	}
	return null;
    }
    
    Stage findStageNamedDR(String name) {
	Enumeration e = stages.getContents();
	while (e.hasMoreElements()) {
	    Stage stage = (Stage) e.nextElement();
	    if (stage.getName().equalsIgnoreCase(name))
		return stage;
	}
	return null;
    }
    
    public boolean isValid() {
	if (worldState == null)
	    return false;
	return true;
    }
    
    public void setDrawingModule(WorldDrawingModule m) {
	_drawingModule = m;
    }
    
    public final void addSyncAction(Target target, String command,
				    Object data) {
	_drawingModule.addSyncAction(target, command, data);
    }
    
    public final boolean isInSyncPhase() {
	return _drawingModule.isInSyncPhase();
    }
    
    public final void screenRefresh() {
	_drawingModule.screenRefresh();
    }
    
    public final void forceRepaint() {
	_drawingModule.forceRepaint();
    }
    
    void addStage(Stage stage) {
	stages.add(stage);
	stageNameCounter++;
	if (_currentStageDRHack == null)
	    setCurrentStageDRHack(stage);
    }
    
    void removeFromVisibleStages(Stage stage) {
	int index = visibleStageList.indexOfIdentical(stage);
	while (getState() != CLOSING
	       && (index = visibleStageList.indexOfIdentical(stage)) != -1)
	    switchStage(null, index);
    }
    
    void showStage(Stage stage) {
	Stage switchTo = stage;
	if (switchTo == null)
	    switchTo = NULL_STAGE;
	visibleStageList.addElement(switchTo);
	if (getActionSieve() != null) {
	    getActionSieve().action(this,
				    "Stagecast.World:action.num_visi_regions",
				    new Integer(visibleStageList.size()));
	    getActionSieve().action(this,
				    "Stagecast.Stage:action.set_visi_stage",
				    (new Object[]
				     { stage,
				       new Integer(visibleStageList.size()
						   - 1),
				       new Double(1.0) }));
	}
	if (_worldView != null)
	    _worldView.addVisibleStageView(stage);
    }
    
    public int getNumberOfVisibleStages() {
	return visibleStageList.size();
    }
    
    boolean isStageVisible(Stage stage) {
	return visibleStageList.containsIdentical(stage);
    }
    
    void splitStage(int nStages) {
	if (nStages > 2)
	    nStages = 2;
	if (nStages > getNumberOfVisibleStages()) {
	    for (int i = getNumberOfVisibleStages(); i < nStages; i++)
		showStage(null);
	} else {
	    for (int i = getNumberOfVisibleStages(); i > nStages; i--)
		removeVisibleStage();
	}
    }
    
    void addVisibleStages(Stage[] stages) {
	for (int i = 0; i < stages.length; i++)
	    showStage(stages[i]);
    }
    
    void switchStage(Stage stage, int index) {
	if (stage == null || !stage.isProxy()) {
	    Stage switchTo = stage;
	    if (switchTo == null)
		switchTo = NULL_STAGE;
	    visibleStageList.setElementAt(switchTo, index);
	    if (_worldView != null)
		_worldView.setVisibleStageView(stage, index);
	    if (getActionSieve() != null)
		getActionSieve().action
		    (this, "Stagecast.Stage:action.set_visi_stage",
		     new Object[] { stage, new Integer(index),
				    new Double(-1.0) });
	    if (PlaywriteRoot.isAuthoring() && Tutorial.getTutorial() != null)
		Tutorial.getTutorial().stageSwitched(stage.getName());
	    if (getWorld().getCurrentStageDRHack() != null)
		getWorld().setCurrentStageDRHack(stage);
	}
    }
    
    public void removeVisibleStage() {
	removeVisibleStage(visibleStageList.size() - 1);
    }
    
    private void removeVisibleStage(int index) {
	visibleStageList.removeElementAt(index);
	if (_worldView != null)
	    _worldView.removeVisibleStageView(index);
	if (getActionSieve() != null) {
	    for (int i = index; i < visibleStageList.size(); i++)
		getActionSieve().action
		    (this, "Stagecast.Stage:action.set_visi_stage",
		     new Object[] { visibleStageList.elementAt(i),
				    new Integer(i), new Double(1.0) });
	    getActionSieve().action(this,
				    "Stagecast.World:action.num_visi_regions",
				    new Integer(visibleStageList.size()));
	}
    }
    
    Stage getStageAtIndex(int index) {
	if (index < visibleStageList.size())
	    return (Stage) visibleStageList.elementAt(index);
	return null;
    }
    
    int getStageViewIndex(Stage stage) {
	return visibleStageList.indexOfIdentical(stage);
    }
    
    Stage getStageFocusDRHack() {
	if (_currentStageDRHack == null)
	    return (Stage) stages.getContents().nextElement();
	return _currentStageDRHack;
    }
    
    void removeAllStagesDRHack() {
	stages.removeAll();
	visibleStageList.removeAllElements();
	_currentStageDRHack = null;
    }
    
    Vector getViewDataVectorFromView() {
	Vector v = new Vector(10);
	v.addElement(new Integer(2));
	Rect winBounds = _worldWindow.getUserBounds();
	v.addElement(new Integer(winBounds.x));
	v.addElement(new Integer(winBounds.y));
	Size s = _worldView.getStageSize();
	v.addElement(new Integer(s.width));
	v.addElement(new Integer(s.height));
	double[] splits = _worldView.getSplitReal();
	for (int i = 0; i < visibleStageList.size(); i++) {
	    Stage stage = (Stage) visibleStageList.elementAt(i);
	    if (stage == NULL_STAGE) {
		v.addElement(new Integer(0));
		v.addElement(new Double(splits[i]));
		v.addElement(new Integer(0));
		v.addElement(new Integer(0));
	    } else {
		v.addElement(stage);
		v.addElement(new Double(splits[i]));
		ScrollableArea stageScroll
		    = ((ScrollableArea)
		       _worldView.getMultiStageView().viewAt(i));
		v.addElement(new Integer(stageScroll.getContentView().x()));
		v.addElement(new Integer(stageScroll.getContentView().y()));
	    }
	}
	return v;
    }
    
    public void setViewData(Vector data) {
	int version = ((Integer) data.elementAt(0)).intValue();
	if (version > 2)
	    throw new UnknownVersionError(this.getClass(), version, 2);
	if (version == 1) {
	    for (int i = 6; i < data.size(); i += 4)
		data.setElementAt(new Double(((Integer) data.elementAt(i))
						 .doubleValue() / 100.0),
				  i);
	}
	if (data.size() <= 5) {
	    Debug.print
		(true,
		 "This saved world contained no information about visible stages!");
	    showStage(getStageFocusDRHack());
	} else {
	    double[] splits = new double[(data.size() - 5) / 4];
	    int ix = 0;
	    for (int i = 5; i < data.size(); i += 4) {
		if (data.elementAt(i) instanceof Stage)
		    showStage((Stage) data.elementAt(i));
		else
		    showStage(null);
		splits[ix++] = ((Double) data.elementAt(i + 1)).doubleValue();
	    }
	    if (_worldView != null)
		_worldView.setSplitReal(splits);
	    else if (getActionSieve() != null) {
		for (int i = 0; i < splits.length; i++)
		    getActionSieve().action
			(this, "Stagecast.Stage:action.set_visi_stage",
			 new Object[] { visibleStageList.elementAt(i),
					new Integer(i),
					new Double(splits[i]) });
	    }
	}
	int x = 0;
	int y = 0;
	int width = ((Integer) data.elementAt(3)).intValue();
	int height = ((Integer) data.elementAt(4)).intValue();
	if (width == 0 || height == 0) {
	    width = desiredWidth();
	    height = desiredHeight();
	}
	if (_worldWindow != null) {
	    Size s = _worldView.getWindowSizeForStageSize(width, height);
	    _worldWindow.setBounds(x, y, s.width, s.height);
	}
	int viewIx = 0;
	for (int i = 7; i < data.size(); i += 4) {
	    if (data.elementAt(i - 2) instanceof Stage) {
		x = ((Integer) data.elementAt(i)).intValue();
		y = ((Integer) data.elementAt(i + 1)).intValue();
		if (_worldView != null)
		    ((ScrollableArea)
		     _worldView.getMultiStageView().viewAt(viewIx))
			.scrollTo(x, y);
	    }
	    viewIx++;
	}
    }
    
    void showSidelines() {
	_worldView.showSidelines();
    }
    
    void hideSidelines() {
	_worldView.hideSidelines();
    }
    
    Object executeAction(RuleAction action, CharacterContainer container,
			 int baseX, int baseY) {
	Object result;
	if (container != null) {
	    synchronized (container) {
		result = action.execute(container, baseX, baseY);
	    }
	} else
	    result = action.execute(container, baseX, baseY);
	if (result == RuleAction.SUCCESS) {
	    action.setClockTick(getTime());
	    if (!PlaywriteRoot.isServer())
		historyList.add(action);
	    if (Thread.currentThread() == worldThread
		&& updateAfterEveryChange() && action.wantsRepaint()) {
		long tDraw = System.currentTimeMillis();
		_hasWaitedForTick = true;
		forceRepaint();
		tDraw = System.currentTimeMillis() - tDraw;
		startCycle = startCycle + tDraw;
	    }
	}
	return result;
    }
    
    void doManualAction(RuleAction action, CharacterContainer container) {
	Object state = getState();
	if (isRunning())
	    executeInContainer(action, container, 0, 0);
	else if (state == EDITING || state == EDIT_DEBUGGING) {
	    RuleEditor.recordAction(action);
	    action.updateAfterBoard(RuleEditor.getAfterBoard());
	} else if (state == RECORDING && !isRecordingSuspended()) {
	    RuleEditor.recordAction(action);
	    Point base = RuleEditor.getSelfLocation();
	    executeInContainer(action, RuleEditor.getSelfContainer(), base.x,
			       base.y);
	    action.updateAfterBoard(RuleEditor.getAfterBoard());
	} else
	    executeInContainer(action, container, 0, 0);
	setModified(true);
	if (!isRunning())
	    _historyWatchers.update(this, null);
    }
    
    void doManualAction(RuleAction action) {
	doManualAction(action, null);
    }
    
    private void executeInContainer(RuleAction action,
				    CharacterContainer container, int baseX,
				    int baseY) {
	beginClockCycle();
	executeAction(action, container, baseX, baseY);
	endClockCycle();
    }
    
    void undoActions(int currentTick) {
	while (!historyList.isEmpty()) {
	    RuleAction action = historyList.removeLast();
	    if (action.getClockTick() >= currentTick) {
		action.undo();
		if (updateAfterEveryChange() && action.wantsRepaint())
		    forceRepaint();
	    } else {
		historyList.add(action);
		break;
	    }
	}
	if (!isRunning())
	    _historyWatchers.update(this, null);
    }
    
    void undoActions() {
	if (!historyList.isEmpty()) {
	    int currentTick = historyList.lastClockTick();
	    undoActions(currentTick);
	    ticks = currentTick - 1;
	    updateClock();
	}
    }
    
    GeneralizedCharacter doCreateAction
	(CharacterPrototype ch, CharacterContainer container, int h, int v) {
	GeneralizedCharacter gch;
	if (ch.getWorld() == this)
	    gch = new GeneralizedCharacter(ch);
	else {
	    CharacterPrototype newPrototype
		= (CharacterPrototype) ch.copy(this);
	    gch = new GeneralizedCharacter(newPrototype);
	}
	doManualAction(new CreateAction(gch, h, v, -1), container);
	return gch;
    }
    
    CharacterInstance doMoveAction
	(CharacterInstance ch, CharacterContainer container, int h, int v) {
	if (ch.getWorld() == this) {
	    if (ch.getH() == h && ch.getV() == v
		&& ch.getContainer() == container)
		return ch;
	    GeneralizedCharacter gch = new GeneralizedCharacter(ch);
	    doManualAction(new MoveAction(gch, container, h, v));
	    return ch;
	}
	CharacterInstance newInstance = (CharacterInstance) ch.copy(this);
	newInstance.setName(ch.getName());
	container.add(newInstance, h, v, -1);
	return newInstance;
    }
    
    public void queue(MouseEvent ifcEvent, BoardView stageView) {
	int h = stageView.squareH(ifcEvent.x);
	int v = stageView.squareV(ifcEvent.y);
	queue(ifcEvent, (Stage) stageView.getBoard(), h, v);
    }
    
    public void queue(KeyEvent event) {
	queue(event, getTime());
    }
    
    public void queue(KeyEvent event, int etick) {
	makePending(new PlaywriteEvent(event, 2, etick));
    }
    
    public void queue(MouseEvent event, Stage stage, int h, int v) {
	queue(event, stage, h, v, getTime());
    }
    
    public void queue(MouseEvent event, Stage stage, int h, int v, int etick) {
	makePending(new PlaywriteEvent(event, stage, h, v, 1, etick));
    }
    
    private void makePending(PlaywriteEvent evt) {
	synchronized (activeEvents) {
	    if (!isRunningNow) {
		activeEvents.addElement(evt);
		synchronized (this) {
		    this.notifyAll();
		}
		return;
	    }
	}
	synchronized (pendingActiveEvents) {
	    pendingActiveEvents.addElement(evt);
	}
    }
    
    private void initializeSystemMenu() {
	boolean skipNextSeparator = false;
	systemMenu = new Menu(false);
	int index = 0;
	String aboutString
	    = Resource.getTextAndFormat("command ap",
					(new Object[]
					 { PlaywriteRoot.getProductName() }));
	PlaywriteMenuItem item
	    = new PlaywriteMenuItem(aboutString, "command ap", this);
	item.setStateWatcher(null);
	systemMenu.addItemAt(item, index++);
	if (getLoader() != null) {
	    item = new PlaywriteMenuItem(Resource.getText("command api"),
					 "command api", this);
	    item.setStateWatcher(null);
	    systemMenu.addItemAt(item, index++);
	}
	item = new PlaywriteMenuItem(Resource.getText("command aw"),
				     "command aw", this);
	item.setStateWatcher(null);
	systemMenu.addItemAt(item, index++);
	if (PlaywriteRoot.isApplication()) {
	    MenuItem separator = new MenuItem();
	    separator.setSeparator(true);
	    systemMenu.addItemAt(separator, index++);
	    if (PlaywriteRoot.isAuthoring())
		systemMenu.addItemAt
		    (new PlaywriteMenuItem(Resource.getText("command nw"), 'n',
					   "command nw", this),
		     index++);
	    if (PlaywriteRoot.isProfessional())
		systemMenu.addItemAt
		    (new PlaywriteMenuItem(Resource.getText("command oaw"),
					   'o', "command oaw", this),
		     index++);
	    else {
		systemMenu.addItemAt
		    (new PlaywriteMenuItem(Resource.getText("command ow"), 'o',
					   "command ow", this),
		     index++);
		systemMenu.addItemAt
		    (new PlaywriteMenuItem(Resource.getText("command osw"),
					   "command osw", this),
		     index++);
	    }
	    systemMenu.addItemAt
		(new PlaywriteMenuItem(Resource.getText("command cw"),
				       "command cw", this),
		 index++);
	}
	if (PlaywriteRoot.isAuthoring()) {
	    if (PlaywriteRoot.isApplication()) {
		final PlaywriteMenuItem saveWorldMenuItem
		    = new PlaywriteMenuItem(Resource.getText("command sw"),
					    's', "command sw", this);
		saveWorldMenuItem.setStateWatcher(new StateWatcher() {
		    private boolean prevEnable = true;
		    
		    public void stateChanged(Object target, Object oldState,
					     Object transition,
					     Object newState) {
			if (oldState == World.STOPPED
			    && (newState == World.RECORDING
				|| newState == World.EDITING
				|| newState == World.RUNNING)) {
			    prevEnable = saveWorldMenuItem.isEnabled();
			    saveWorldMenuItem.setEnabled(false);
			}
			if (newState == World.STOPPED
			    && (oldState == World.RECORDING
				|| oldState == World.EDITING
				|| oldState == World.RUNNING))
			    saveWorldMenuItem.setEnabled(prevEnable);
		    }
		});
		systemMenu.addItemAt(saveWorldMenuItem, index++);
		systemMenu.addItemAt
		    (new PlaywriteMenuItem(Resource.getText("command swa"),
					   "command swa", this),
		     index++);
	    }
	    MenuItem separator = new MenuItem();
	    separator.setSeparator(true);
	    systemMenu.addItemAt(separator, index++);
	    if (PlaywriteRoot.isApplication()) {
		systemMenu.addItemAt
		    (new PlaywriteMenuItem(Resource.getText("command ss"),
					   "command ss", this),
		     index++);
		systemMenu.addItemAt
		    (new PlaywriteMenuItem(Resource.getText("make snapshot"),
					   "make snapshot", this),
		     index++);
		PlaywriteMenuItem[] upload
		    = PlaywriteRoot.isApplication() ? makeUploadMenus() : null;
		if (upload != null && upload.length > 0) {
		    MenuItem uploadMenu = (systemMenu.addItemWithSubmenu
					   (Resource.getText("command swp")));
		    index++;
		    for (int i = 0; i < upload.length; i++)
			uploadMenu.submenu().addItemAt(upload[i], i);
		}
		if (PlaywriteRoot.isProfessional()) {
		    String menuText
			= (Resource.getTextAndFormat
			   ("command lc",
			    (new Object[]
			     { PlaywriteRoot.getShortProductName() })));
		    PlaywriteMenuItem learnMenuItem
			= new PlaywriteMenuItem(menuText, "command lc", this);
		    systemMenu.addItemAt(learnMenuItem, index++);
		}
	    } else
		skipNextSeparator = true;
	} else if (PlaywriteRoot.isApplication())
	    systemMenu.addItemAt
		(new PlaywriteMenuItem(Resource.getText("command swa"), 's',
				       "command swa", this),
		 index++);
	if (!skipNextSeparator) {
	    MenuItem separator = new MenuItem();
	    separator.setSeparator(true);
	    systemMenu.addItemAt(separator, index++);
	}
	Vector builders = PlaywriteRoot.getExtensionMenuBuilders();
	if (builders.size() > 0) {
	    for (int i = 0; i < builders.size(); i++) {
		MenuBuilder mb = (MenuBuilder) builders.elementAt(i);
		try {
		    index = mb.addMenuItems(this, systemMenu, index);
		} catch (Throwable t) {
		    Debug.print(true, "Error adding menu item in extension");
		    Debug.stackTrace(t);
		}
	    }
	    MenuItem separator = new MenuItem();
	    separator.setSeparator(true);
	    systemMenu.addItemAt(separator, index++);
	}
	if (PlaywriteRoot.isApplication()) {
	    final PlaywriteMenuItem protectMenu
		= new PlaywriteMenuItem(Resource.getText("command pw"),
					"command pw", this);
	    protectMenu.setStateWatcher(new StateWatcher() {
		public void stateChanged(Object target, Object oldState,
					 Object transition, Object newState) {
		    if (oldState == World.STOPPED
			&& (newState == World.RECORDING
			    || newState == World.EDITING
			    || newState == World.RUNNING))
			protectMenu.setEnabled(false);
		    if (newState == World.STOPPED
			&& (oldState == World.RECORDING
			    || oldState == World.EDITING
			    || oldState == World.RUNNING
			    || oldState == World.OPENING))
			protectMenu
			    .setEnabled(Tutorial.isTutorialRunning() ^ true);
		}
	    });
	    systemMenu.addItemAt(protectMenu, index++);
	    MenuItem separator = new MenuItem();
	    separator.setSeparator(true);
	    systemMenu.addItemAt(separator, index++);
	}
	final PlaywriteMenuItem resetMenuItem
	    = new PlaywriteMenuItem(Resource.getText("command reset world"),
				    "command reset world", this);
	resetMenuItem.setStateWatcher(new StateWatcher() {
	    private boolean prevEnable = true;
	    
	    public void stateChanged(Object target, Object oldState,
				     Object transition, Object newState) {
		if (oldState == World.STOPPED
		    && (newState == World.RECORDING
			|| newState == World.EDITING)) {
		    prevEnable = resetMenuItem.isEnabled();
		    resetMenuItem.setEnabled(true);
		}
		if (newState == World.STOPPED
		    && (oldState == World.RECORDING
			|| oldState == World.EDITING
			|| oldState == World.RUNNING))
		    resetMenuItem.setEnabled(prevEnable);
	    }
	});
	systemMenu.addItemAt(resetMenuItem, index++);
	if (PlaywriteRoot.isAuthoring())
	    resetMenuItem.setEnabled(false);
	if (PlaywriteRoot.isApplication()) {
	    item = new PlaywriteMenuItem(Resource.getText("command q"), 'q',
					 "command q", this);
	    item.setStateWatcher(null);
	    systemMenu.addItemAt(item, index++);
	}
	if (Debug.lookup("debug.statistics")) {
	    MenuItem separator = new MenuItem();
	    separator.setSeparator(true);
	    systemMenu.addItemAt(separator, index++);
	    systemMenu.addItemAt(new PlaywriteMenuItem("statistics",
						       "debug.statistics",
						       this),
				 index++);
	}
    }
    
    private PlaywriteMenuItem[] makeUploadMenus() {
	File uploadSiteDir = new File(_uploadConfigDirectory);
	String[] files = uploadSiteDir.list(new FilenameFilter() {
	    public boolean accept(File f, String name) {
		return name.endsWith(".site");
	    }
	});
	if (files != null) {
	    for (int i = 0; i < files.length; i++) {
		Properties props = new Properties();
		try {
		    FileInputStream fis
			= new FileInputStream(new File(uploadSiteDir,
						       files[i]));
		    props.load(fis);
		    fis.close();
		} catch (IOException e) {
		    Debug.print(true, "Property file load failed: ", e);
		}
		String siteName = props.getProperty("Name");
		if (siteName != null && siteName.length() > 0)
		    _uploadSites.put(siteName, props);
	    }
	}
	Properties props = new Properties();
	String otherTag = Resource.getText("command swpo");
	props.put("Name", otherTag);
	_uploadSites.put(otherTag, props);
	Object[] keys = _uploadSites.keysArray();
	Sort.sortStrings(keys, 0, keys.length, true, true);
	PlaywriteMenuItem[] menus = new PlaywriteMenuItem[keys.length];
	for (int i = 0; i < keys.length; i++)
	    menus[i]
		= new PlaywriteMenuItem((String) keys[i], "command swp", this);
	return menus;
    }
    
    void setMenuCommandEnabled(String command, boolean enabled) {
	MenuItem item = getMenuItem(command);
	if (item != null)
	    item.setEnabled(enabled);
    }
    
    boolean isMenuCommandEnabled(String command) {
	MenuItem item = getMenuItem(command);
	if (item != null)
	    return item.isEnabled();
	return false;
    }
    
    MenuItem getMenuItem(String command) {
	if (systemMenu != null) {
	    for (int i = 0; i < systemMenu.itemCount(); i++) {
		if (command.equals(systemMenu.itemAt(i).command()))
		    return systemMenu.itemAt(i);
	    }
	}
	return null;
    }
    
    void addHistoryWatcher(Watcher w) {
	_historyWatchers.add(w);
    }
    
    void removeHistoryWatcher(Watcher w) {
	_historyWatchers.remove(w);
    }
    
    boolean historyListIsEmpty() {
	return historyList.isEmpty();
    }
    
    void resetHistoryList() {
	historyList.reset();
	_historyWatchers.update(this, null);
    }
    
    void referencedObjectWasDeleted() {
	resetHistoryList();
	setMenuCommandEnabled("command reset world", false);
	clearState();
    }
    
    boolean prepareToClose() {
	_closing = true;
	if (PlaywriteRoot.isAuthoring()) {
	    if (RuleEditor.isRecordingOrEditing())
		RuleEditor.getRuleEditor().performCommand("command c", this);
	    if (!PlaywriteRoot.getAppearanceEditorController()
		     .prepareForClose()) {
		_closing = false;
		return false;
	    }
	}
	if (getState() == RUNNING && !forceStop())
	    _closing = false;
	else if (isModified()) {
	    PlaywriteDialog dialog
		= new PlaywriteDialog("dialog sc", "command s", "command ds",
				      "command c");
	    String answer = dialog.getAnswer();
	    if (answer.equals("command s")) {
		if (!saveToDisk(false))
		    _closing = false;
	    } else if (answer.equals("command c"))
		_closing = false;
	}
	return _closing;
    }
    
    void close() {
	if (!_closing)
	    Debug.print(true, "DANGER: Unexpected close");
	long closeTime = System.currentTimeMillis();
	markWorldThreadDone();
	try {
	    if (worldThread != null)
		worldThread.join(5000L);
	} catch (InterruptedException interruptedexception) {
	    Debug.print(true, "[unable to rejoin world thread]");
	} catch (Throwable t) {
	    Debug.stackTrace(t);
	}
	worldThread = null;
	Debug.print("debug.world",
		    "Thread cleanup " + (System.currentTimeMillis()
					 - closeTime),
		    "ms");
	closeTime = System.currentTimeMillis();
	resetSelectionState();
	changeState(CLOSE_WORLD);
	Debug.print("debug.world",
		    "State changed to CLOSING " + (System.currentTimeMillis()
						   - closeTime),
		    "ms");
	closeTime = System.currentTimeMillis();
	activeEvents = null;
	pendingActiveEvents = null;
	PlaywriteRoot.resetDoubleClicking();
	setMediaSource(null);
	Debug.print("debug.world",
		    "File cleanup " + (System.currentTimeMillis() - closeTime),
		    "ms");
	closeTime = System.currentTimeMillis();
	sounds.removeAll();
	sounds = null;
	_backgrounds.removeAll();
	_backgrounds = null;
	jars.removeAll();
	jars = null;
	_timeoutBox.removeAll();
	_timeoutBox = null;
	Debug.print("debug.world",
		    ("Sound/Background/Jar cleanup "
		     + (System.currentTimeMillis() - closeTime)),
		    "ms");
	closeTime = System.currentTimeMillis();
	prototypes.removeAll();
	prototypes = null;
	specialPrototypes.removeAll();
	specialPrototypes = null;
	Debug.print("debug.world",
		    "Prototype cleanup " + (System.currentTimeMillis()
					    - closeTime),
		    "ms");
	closeTime = System.currentTimeMillis();
	stages.removeAll();
	stages = null;
	_currentStageDRHack = null;
	_followMeStageStart = null;
	Debug.print("debug.world",
		    "Stage cleanup " + (System.currentTimeMillis()
					- closeTime),
		    "ms");
	closeTime = System.currentTimeMillis();
	Variable.deleteOwner(this);
	Debug.print("debug.world",
		    "Variable cleanup " + (System.currentTimeMillis()
					   - closeTime),
		    "ms");
	closeTime = System.currentTimeMillis();
	resetHistoryList();
	historyList = null;
	_historyWatchers = null;
	_boundGCs = null;
	if (systemMenu != null) {
	    for (int i = 0; i < systemMenu.itemCount(); i++)
		systemMenu.itemAt(i).setTarget(null);
	}
	systemMenu = null;
	_worldWindow = null;
	_worldView = null;
	clockView = null;
	globalVariables = null;
	visibleStageList = null;
	_followMeStageStart = null;
	_viewData = null;
	worldState = null;
	_drawingModule.close();
	_drawingModule = null;
	if (PlaywriteSystem.isMacintosh()) {
	    BitmapManager.printStatistics();
	    Util.printStatistics();
	}
	BitmapManager.shrinkCache();
	Debug.print("debug.world",
		    "Final cleanup " + (System.currentTimeMillis()
					- closeTime),
		    "ms");
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("RUN_FORWARD"))
	    runForward();
	else if (command.equals(CLEAR_DEBUG))
	    clearDebug();
	else if (command.equals(SYS_ENABLE_GRID_VARIABLE_ID)) {
	    Enumeration stages = getStages().getContents();
	    while (stages.hasMoreElements())
		((Stage) stages.nextElement()).invalidateScreen(true);
	} else if (command.equals("make followMe visible")) {
	    if (!isClosing())
		makeCharacterVisible((CharacterInstance) data);
	} else if (command.equals("command ap"))
	    _root.aboutProduct(this);
	else if (command.equals("command api"))
	    _root.aboutPlugins(this);
	else if (command.equals("command aw"))
	    _worldView.about();
	else if (command.equals("command reset world")) {
	    if (getWorldView() != null)
		getWorldView().getControlPanelView().performCommand("CP reset",
								    this);
	    else
		stopAndReset();
	} else if (command.equals("command lc")) {
	    if (_root.closeWorld(this, false))
		_root.performCommand("OT", null);
	} else if (command.equals("command nw"))
	    _root.newWorld(this);
	else if (command.equals("command ow")) {
	    if (_root.closeWorld(this, false))
		_root.openWorld(null);
	} else if (command.equals("command osw"))
	    _root.openWorld(null);
	else if (command.equals("command oaw")) {
	    if (_isEmpty && _root.numberOfWorlds() == 1)
		_root.closeWorld(this, false);
	    _root.openWorld(null);
	} else if (command.equals("command cw")) {
	    boolean reformat = (_root.numberOfWorlds() == 1
				|| !PlaywriteRoot.isProfessional());
	    _root.closeWorld(this, reformat);
	} else if (command.equals("command sw")) {
	    PlaywriteRoot.getMainRootView().setFocusedView(null);
	    saveToDisk(false);
	} else if (command.equals("command swa")) {
	    PlaywriteRoot.getMainRootView().setFocusedView(null);
	    saveToDisk(true);
	} else if (command.equals("command pw"))
	    setLocked(isLocked() ^ true);
	else if (command.equals("command q")) {
	    if (getState() == RECORDING || getState() == EDITING)
		doManualAction(new QuitAction());
	    else
		_root.quit();
	} else if (command == "LOADSTATE")
	    loadState();
	else if (command.equals("debug.statistics")) {
	    BitmapManager.printStatistics();
	    Util.printStatistics();
	} else if (_utilityObject != null)
	    _utilityObject.performCommand(command, data);
	else
	    throw new PlaywriteInternalError("unknown command: " + command);
    }
    
    public World getWorld() {
	return this;
    }
    
    public Enumeration getVariables() {
	return globalVariables.elements();
    }
    
    public VariableList getVariableList() {
	return globalVariables;
    }
    
    public VariableOwner getVariableListOwner() {
	return this;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return false;
    }
    
    public boolean affectsDisplay(Variable variable) {
	return false;
    }
    
    public WorldView getWorldView() {
	return _worldView;
    }
    
    public PlaywriteView createIconView() {
	Bitmap worldIcon = Resource.getImage("WorldIcon");
	PlaywriteView view
	    = new PlaywriteView(0, 0, worldIcon.width(), worldIcon.height());
	view.setImage(worldIcon);
	return view;
    }
    
    public PlaywriteView createView() {
	return createIconView();
    }
    
    void markGeneralizedCharacter(GeneralizedCharacter newGC) {
	if (_boundGCsize == _boundGCs.length) {
	    GeneralizedCharacter[] newTable
		= new GeneralizedCharacter[_boundGCs.length + 10];
	    System.arraycopy(_boundGCs, 0, newTable, 0, _boundGCs.length);
	    _boundGCs = newTable;
	}
	_boundGCs[_boundGCsize++] = newGC;
    }
    
    void unmarkGeneralizedCharacter(GeneralizedCharacter gc) {
	int i = _boundGCsize;
	while (i-- > 0) {
	    if (_boundGCs[i] == gc) {
		_boundGCs[i] = null;
		break;
	    }
	}
    }
    
    void resetGeneralizedCharacters() {
	GeneralizedCharacter.resetAll(_boundGCs, _boundGCsize);
	_boundGCsize = 0;
    }
    
    boolean[] resetValues(Vector characterInstances) {
	int n = characterInstances.size();
	if (n > maxItems) {
	    maxItems = n;
	    boundValues = new boolean[maxItems];
	}
	for (int i = 0; i < n; i++)
	    boundValues[i]
		= (((CharacterInstance) characterInstances.elementAt(i))
		   ._boundBy) != null;
	return boundValues;
    }
    
    public void setLocked(boolean lock) {
	Variable.setSystemValue(SYS_WORLD_LOCKED_VARIABLE_ID, this,
				lock ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isLocked() {
	return ((Boolean)
		Variable.getSystemValue(SYS_WORLD_LOCKED_VARIABLE_ID, this))
		   .booleanValue();
    }
    
    private boolean warnIfSaveRestricted() {
	if (PlaywriteRoot.hasSaveRestrictions()) {
	    String evalType = Resource.getText("root ptd");
	    String msg = Resource.getTextAndFormat("dialog eval cant save",
						   new Object[] { evalType });
	    PlaywriteDialog.warning(msg, true);
	    return true;
	}
	return false;
    }
    
    boolean saveToDisk(boolean doDialog) {
	String extension = Resource.getText("W ex");
	File diskFile = null;
	File tempFile = null;
	FileOutputStream tempOs = null;
	if (warnIfSaveRestricted())
	    return true;
	if (getFSVersion() == 1) {
	    PlaywriteDialog dlg
		= new PlaywriteDialog("dialog scon", "command ok",
				      "command c");
	    String answer = dlg.getAnswerModally();
	    if (answer == "command c")
		return false;
	}
	Debug.print("debug.objectstore", "Saving file ");
	if (getState() == RUNNING && !forceStop())
	    return false;
	if (RuleEditor.isRecordingOrEditing())
	    RuleEditor.getRuleEditor().performCommand("command c", this);
	if (_sourceFile == null)
	    doDialog = true;
	do {
	    if (doDialog) {
		String message = Resource.getText("command swa");
		FileChooser fc
		    = new FileChooser(PlaywriteRoot.getMainRootView(), message,
				      1);
		if (_sourceFile == null) {
		    fc.setDirectory(getWorldDirectory());
		    fc.setFile(getName() + extension);
		} else {
		    fc.setDirectory(_sourceFile.getParent());
		    String fName = Util.dePercentString(_sourceFile.getName());
		    if (getFSVersion() != PlaywriteRoot.getObjectStoreVersion()
			&& fName.endsWith(".world"))
			fName
			    = (fName.substring(0, fName.lastIndexOf(".world"))
			       + extension);
		    if (fName.endsWith(".cco"))
			fName = (fName.substring(0, fName.lastIndexOf(".cco"))
				 + extension);
		    PlaywriteRoot.app();
		    if (PlaywriteRoot.isPlayer()) {
			boolean hadExtension = fName.endsWith(extension);
			if (hadExtension)
			    fName = fName.substring(0, (fName.length()
							- extension.length()));
			fName = (Resource.getTextAndFormat
				 ("save as name generator",
				  new Object[] { fName }));
			if (hadExtension)
			    fName += (String) extension;
		    }
		    fc.setFile(fName);
		}
		fc.showModally();
		if (fc.file() == null)
		    return false;
		setWorldDirectory(fc.directory());
		String fName = fc.file();
		if (PlaywriteSystem.isWindows()
		    && !fc.file().endsWith(extension))
		    fName += (String) extension;
		setSourceFile(new File(fc.directory(), fName));
	    }
	    doDialog = false;
	    if (_sourceFile.exists() && !_sourceFile.canWrite())
		doDialog = true;
	    else {
		tempFile = Util.createTempFile(_sourceFile.getParent());
		try {
		    tempOs = new FileOutputStream(tempFile);
		} catch (IOException ioexception) {
		    doDialog = true;
		}
	    }
	    if (doDialog) {
		PlaywriteDialog msg
		    = new PlaywriteDialog("dialog cstr", "command ok");
		msg.getAnswer();
	    }
	} while (doDialog);
	PlaywriteRoot.openProgress("splash sw");
	boolean saved = false;
	try {
	    saved = saveObjects(tempOs, true);
	    PlaywriteRoot.getMainRootView().disableDrawing();
	} finally {
	    try {
		tempOs.close();
	    } catch (IOException ioexception) {
		saved = false;
	    }
	    if (saved) {
		if (_sourceFile.exists()) {
		    setMediaSource(null);
		    _sourceFile.delete();
		}
		tempFile.renameTo(_sourceFile);
		try {
		    _fsVersion = PlaywriteRoot.getObjectStoreVersion();
		    setMediaSource(new ZipFile(_sourceFile));
		} catch (ZipException e) {
		    Debug.stackTrace(e);
		} catch (IOException e) {
		    Debug.stackTrace(e);
		}
	    } else
		tempFile.delete();
	    PlaywriteRoot.getMainRootView().reenableDrawing();
	    PlaywriteRoot.closeProgress();
	}
	tempOs = null;
	if (!saved) {
	    PlaywriteDialog msg
		= new PlaywriteDialog("dialog sf", "command ok");
	    msg.getAnswer();
	}
	if (saved && _requiredPlugins != null) {
	    StringBuffer msg = new StringBuffer(256);
	    boolean doPluginDialog = false;
	    if (_prevRequiredPlugins == null
		|| _prevRequiredPlugins.size() != _requiredPlugins.size())
		doPluginDialog = true;
	    msg.append(Resource.getText("dialog rpl"));
	    for (int i = 0; i < _requiredPlugins.size(); i++) {
		String plugin = (String) _requiredPlugins.elementAt(i);
		msg.append(plugin);
		if (i < _requiredPlugins.size() - 1)
		    msg.append(", ");
		if (_prevRequiredPlugins != null
		    && !_prevRequiredPlugins.contains(plugin))
		    doPluginDialog = true;
	    }
	    _prevRequiredPlugins = _requiredPlugins;
	    _requiredPlugins = null;
	    if (doPluginDialog) {
		PlaywriteDialog dlg
		    = new PlaywriteDialog(msg.toString(), "command ok");
		dlg.getAnswer();
	    }
	}
	if (saved) {
	    setMenuCommandEnabled("command sw", true);
	    saveState();
	    setMenuCommandEnabled("command reset world", true);
	}
	return saved;
    }
    
    private boolean forceStop() {
	stopWorld();
	EventLoop eventLoop = Application.application().eventLoop();
	while (getState() == RUNNING) {
	    COM.stagecast.ifc.netscape.application.Event event
		= eventLoop.peekNextEvent();
	    if (event == null) {
		try {
		    Thread.sleep(1000L);
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    } else
		eventLoop.processEvent(eventLoop.getNextEvent());
	}
	if (getState() == STOPPED)
	    return true;
	PlaywriteDialog dialog
	    = new PlaywriteDialog("dialog sw", "command ok");
	dialog.getAnswer();
	return false;
    }
    
    void checkHygiene() {
	StringBuffer msg = new StringBuffer(200);
	int hasMessage = 0;
	msg.append("This world contains data from a beta version of Creator ");
	msg.append
	    ("that was not completely deleted.  You must manually delete ");
	msg.append("items in the following drawer(s):");
	if (prototypes.itemNamed("<deleted>") != null) {
	    msg.append(hasMessage > 0 ? ", " : "    ");
	    msg.append("Characters");
	    hasMessage++;
	}
	if (specialPrototypes.itemNamed("<deleted>") != null) {
	    msg.append(hasMessage > 0 ? ", " : "    ");
	    msg.append("Specials");
	    hasMessage++;
	}
	if (stages.itemNamed("<deleted>") != null) {
	    msg.append(hasMessage > 0 ? ", " : "    ");
	    msg.append("Stages");
	    hasMessage++;
	}
	if (sounds.itemNamed("<deleted>") != null) {
	    msg.append(hasMessage > 0 ? ", " : "    ");
	    msg.append("Sounds");
	    hasMessage++;
	}
	if (jars.itemNamed("<deleted>") != null) {
	    msg.append(hasMessage > 0 ? ", " : "    ");
	    msg.append("Jars");
	    hasMessage++;
	}
	if (hasMessage > 0) {
	    PlaywriteDialog dlg
		= new PlaywriteDialog(msg.toString(), "command ok");
	    dlg.getAnswer();
	}
    }
    
    void validateCharacters() {
	if (_badCharacters > 0)
	    PlaywriteDialog.warning
		(Resource.getTextAndFormat("dialog bad cv",
					   (new Object[]
					    { new Integer(_badCharacters) })),
		 true);
    }
    
    void validateAppearances() {
	Vector badAppearances = Appearance.getMalformedAppearances();
	while (!badAppearances.isEmpty()) {
	    Appearance badAppearance
		= (Appearance) badAppearances.firstElement();
	    if (badAppearance.getOwner() == null
		|| !badAppearance.getOwner().hasAppearance(badAppearance)) {
		StringBuffer message = new StringBuffer(256);
		int count = countRulesReferringTo(badAppearance);
		Object[] params;
		String template;
		if (count == 0) {
		    params = new Object[] { badAppearance.getName() };
		    template = "dialog banr";
		} else {
		    params = new Object[] { badAppearance.getName(),
					    new Integer(count) };
		    template = "dialog baar";
		}
		message.append(Resource.getTextAndFormat(template, params));
		params = new Object[] { null, null, null };
		for (int i = 1; i <= count; i++) {
		    Rule rule = findRuleReferringTo(badAppearance);
		    params[0] = new Integer(i);
		    params[1] = rule.toString();
		    params[2] = rule.getOwner();
		    message.append(Resource.getTextAndFormat("dialog barr",
							     params));
		    rule.delete();
		}
		badAppearance.delete();
		PlaywriteDialog.warning(message.toString(), true);
	    }
	    badAppearances.removeElementIdentical(badAppearance);
	}
    }
    
    public void saveState() {
	try {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    RestartOutStream ros = new RestartOutStream(baos, this);
	    snapshotBuffer = null;
	    _nestedSave = 0;
	    ros.writeInt(getTime());
	    ros.writePrologue();
	    writeExternal(ros);
	    int n = getStages().getContentsTable().count();
	    ros.writeInt(n);
	    Enumeration e = getStages().getContents();
	    while (e.hasMoreElements()) {
		Stage s = (Stage) e.nextElement();
		ros.writeObject(s);
		s.getVariableList().writeListValues(ros, s);
		ros.writeVector(s.getCharacters());
		ros.writeVector(s.getActiveCharacters());
	    }
	    Vector timeOutCharacters = new Vector();
	    Vector timeOutCharLocations = new Vector();
	    Enumeration enum = _timeoutBox.getContents();
	    while (enum.hasMoreElements()) {
		Contained ch = (Contained) enum.nextElement();
		timeOutCharacters.addElement(ch);
		timeOutCharLocations.addElement(_timeoutBox.getLocation(ch));
	    }
	    ros.writeVector(timeOutCharacters);
	    ros.writeVector(timeOutCharLocations);
	    ros.writeVector(getWorldView() == null ? null
			    : getViewDataVectorFromView());
	    ros.flush();
	    snapshotBuffer = baos.toByteArray();
	} catch (IOException ioexception) {
	    System.out.println("Player: failed to save the state");
	}
    }
    
    void clearState() {
	snapshotBuffer = null;
	RestartProxy.emptyOut(this);
    }
    
    boolean hasSavedState() {
	return snapshotBuffer != null;
    }
    
    public void loadState() {
	if (PlaywriteRoot.app().inEventThread()) {
	    if (hasSavedState()) {
		boolean modifedFlag = isModified();
		PlaywriteRoot.markBusy();
		PlaywriteRoot.app();
		if (!PlaywriteRoot.isServer())
		    Selection.hideModalView();
		_drawingModule.disableStageDrawing();
		try {
		    _resetFlag = true;
		    while (getNumberOfVisibleStages() > 0)
			removeVisibleStage();
		    WorldInStream wis = new WorldInStream(this);
		    int time = wis.readInt();
		    ticks = time;
		    if (clockView != null)
			clockView.setDirty(true);
		    ActionSieve sieve = getActionSieve();
		    if (sieve != null)
			sieve.action(this, "Stagecast.Stage:action.reset",
				     new Integer(time));
		    Enumeration protos = getPrototypes().getContents();
		    while (protos.hasMoreElements())
			((CharacterPrototype) protos.nextElement())
			    .deleteAllInstances();
		    protos = getSpecialPrototypes().getContents();
		    while (protos.hasMoreElements())
			((CharacterPrototype) protos.nextElement())
			    .deleteAllInstances();
		    wis.readPrologue();
		    readExternal(wis);
		    int n = wis.readInt();
		    for (int i = 0; i < n; i++) {
			Stage s = (Stage) wis.readObject();
			s.getVariableList().readListValuesAndNotify(wis, s);
			s.setCharsToLoad(wis.readVector());
			s.rebuildCharacterList();
			s.rebuildActiveList(wis.readVector(), true);
			s.updateActiveCharacterList();
		    }
		    Vector timeOutCharacters = wis.readVector();
		    Vector timeOutCharLocations = wis.readVector();
		    for (int i = 0; i < timeOutCharacters.size(); i++) {
			Point location
			    = (Point) timeOutCharLocations.elementAt(i);
			_timeoutBox.add(((Contained)
					 timeOutCharacters.elementAt(i)),
					location.x, location.y);
		    }
		    Vector viewData = wis.readVector();
		    if (viewData != null) {
			setViewData(viewData);
			if (getWorldView() != null)
			    PlaywriteRoot.app().rearrangeWindows();
		    }
		    resetHistoryList();
		} catch (ClassNotFoundException e) {
		    System.out
			.println("Player: failed to load the state: " + e);
		} catch (IOException e) {
		    System.out
			.println("Player: failed to load the state: " + e);
		} catch (Throwable t) {
		    Debug.stackTrace(t);
		} finally {
		    _resetFlag = false;
		}
		_drawingModule.reenableStageDrawing();
		PlaywriteRoot.clearBusy();
		setModified(modifedFlag);
		if (_worldWindow != null) {
		    _worldWindow.setDirty(true);
		    _worldView.layoutView(0, 0);
		}
	    }
	} else
	    Application.application().performCommandAndWait(this, "LOADSTATE",
							    null);
    }
    
    boolean saveObjects(OutputStream os, boolean useForMedia) {
	if (warnIfSaveRestricted())
	    return true;
	PlaywriteRoot.markBusy();
	boolean success = false;
	_nestedSave = 0;
	try {
	    WorldOutStream wos = WorldZipFile.writeWorld(os, this);
	    if (useForMedia)
		wos.updateStreamingProducers();
	    setModified(false);
	    success = true;
	} catch (IOException e) {
	    Debug.stackTrace(e);
	} finally {
	    PlaywriteRoot.clearBusy();
	}
	Debug.print("debug.objectstore", "Saving completed");
	return success;
    }
    
    public boolean saveObjects(OutputStream os) {
	return saveObjects(os, false);
    }
    
    private void resetStatistics() {
	startTime = 0L;
	totalTime = 0L;
	startCycle = 0L;
	deltaCycle = 0L;
	totalCycle = 0L;
	maxCycle = 0L;
	minCycle = 1000000L;
	cycles = 0L;
	startDraw = 0L;
	totalDraw = 0L;
    }
    
    private void printStatistics() {
	float fcycle = (float) totalCycle;
	float ftime = (float) totalTime;
	float fdraw = (float) totalDraw;
	if (startTime != 0L && cycles != 0L) {
	    Debug.print("debug.statistics", "Executed  ",
			String.valueOf(cycles), " clock cycles");
	    Debug.print("debug.statistics", "Min cycle ",
			String.valueOf(minCycle), " msec");
	    Debug.print("debug.statistics", "Avg cycle ",
			String.valueOf(totalCycle / cycles), " msec");
	    Debug.print("debug.statistics", "Max cycle ",
			String.valueOf(maxCycle), " msec");
	    Debug.print("debug.statistics", "Total time in exec loop ",
			String.valueOf((double) fcycle / 1000.0), " sec");
	    Debug.print("debug.statistics", "Avg redraw ",
			String.valueOf(fdraw / (float) cycles), " msec");
	    Debug.print("debug.statistics", "Total redraw time ",
			String.valueOf((double) fdraw / 1000.0), " sec");
	    Debug.print("debug.statistics", "Ratio of redraw:exec loop: ",
			String.valueOf(fdraw / fcycle));
	    Debug.print("debug.statistics", "Total elapsed time: ",
			String.valueOf((double) ftime / 1000.0));
	    Debug.print("debug.statistics", "Frame rate: ",
			String.valueOf((double) (float) cycles * 1000.0
				       / (double) ftime));
	    Debug.print("debug.statistics", "");
	    resetStatistics();
	}
    }
    
    void prepareToWrite() {
	Vector unused = new Vector(10);
	Enumeration specials = getSpecialPrototypes().getContents();
	while (specials.hasMoreElements()) {
	    CharacterPrototype special
		= (CharacterPrototype) specials.nextElement();
	    if (special.numberOfInstances() == 0 && !special.isModified()
		&& countRulesReferringTo(special) == 0) {
		Debug.print("debug.objectstore", "Skipping special character ",
			    special);
		unused.addElement(special);
	    }
	}
	getSpecialPrototypes().temporaryRemove(unused);
	unused.removeAllElements();
	getBackgrounds().clearOut();
    }
    
    void worldIsWritten() {
	getSpecialPrototypes().restoreTempRemoved();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	int incr = (75 - PlaywriteRoot.getProgress()) / 6;
	if (++_nestedSave > 1)
	    throw new PlaywriteInternalError("Illegal reference to world "
					     + this);
	String vstring = (PlaywriteRoot.getVersionString()
			  + PlaywriteRoot.getBuildString());
	ASSERT.isNotNull(vstring);
	ASSERT.isNotNull(author);
	ASSERT.isNotNull(comment);
	ASSERT.isNotNull(globalVariables);
	ASSERT.isNotNull(stages);
	ASSERT.isNotNull(sounds);
	ASSERT.isNotNull(prototypes);
	ASSERT.isNotNull(specialPrototypes);
	ASSERT.isNotNull(jars);
	ASSERT.isNotNull(_backgrounds);
	ASSERT.isNotNull(_timeoutBox);
	out.writeUTF(vstring);
	out.writeUTF(author);
	out.writeUTF(comment);
	out.writeUTF(_originalCreatorVersion);
	out.writeObject(_password);
	PlaywriteRoot.incrementProgress(incr, 75);
	globalVariables.writeContents(out);
	getVariableList().writeListValues(out, this);
	Debug.print("debug.objectstore", "Writing stages - ",
		    Debug.mem("debug.objectstore"));
	out.writeObject(stages);
	out.writeInt(stageNameCounter);
	PlaywriteRoot.incrementProgress(incr, 75);
	Debug.print("debug.objectstore", "Writing sounds - ",
		    Debug.mem("debug.objectstore"));
	out.writeObject(sounds);
	PlaywriteRoot.incrementProgress(incr, 75);
	Debug.print("debug.objectstore", "Writing prototypes - ",
		    Debug.mem("debug.objectstore"));
	out.writeObject(prototypes);
	out.writeInt(prototypeCounter);
	PlaywriteRoot.incrementProgress(incr, 75);
	Debug.print("debug.objectstore", "Writing specials - ",
		    Debug.mem("debug.objectstore"));
	out.writeObject(specialPrototypes);
	Debug.print("debug.objectstore", "Writing jars - ",
		    Debug.mem("debug.objectstore"));
	out.writeObject(jars);
	Debug.print("debug.objectstore", "Writing backgrounds - ",
		    Debug.mem("debug.objectstore"));
	out.writeObject(_backgrounds);
	Debug.print("debug.objectstore", "Writing timeout region - ",
		    Debug.mem("debug.objectstore"));
	out.writeObject(_timeoutBox);
	out.writeBoolean(_worldView == null ? true
			 : _worldView.controlPanelIsVisible());
	out.writeBoolean(_worldView == null ? true
			 : _worldView.sidelineIsVisible());
	PlaywriteRoot.incrementProgress(incr, 75);
	Debug.print("debug.objectstore", "Writing instances - ",
		    Debug.mem("debug.objectstore"));
	Vector chars = new Vector(50);
	Enumeration items = getStages().getContents();
	while (items.hasMoreElements())
	    chars.addElements(((Stage) items.nextElement()).getCharacters());
	((WorldOutStream) out).writeVector(chars);
	PlaywriteRoot.incrementProgress(incr, 75);
	if (_worldView != null)
	    _viewData.putData("WW Show Menu", getViewDataVectorFromView());
	out.writeObject(_viewData);
	Debug.print("debug.objectstore", "Writing world completed - ",
		    Debug.mem("debug.objectstore"));
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(World.class);
	int incr = (80 - PlaywriteRoot.getProgress()) / 8;
	String fromVersion = in.readUTF();
	Debug.print("debug.objectstore", "World created in: ", fromVersion);
	((WorldInStream) in).setTargetWorld(this);
	PlaywriteRoot.incrementProgress(incr, 80);
	_isEmpty = false;
	author = in.readUTF();
	comment = in.readUTF();
	switch (version) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	case 9:
	    in.readBoolean();
	    in.readObject();
	    break;
	case 10:
	    _password = (Password) in.readObject();
	    break;
	case 11:
	case 12:
	    _originalCreatorVersion = in.readUTF();
	    _password = (Password) in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 12);
	}
	PlaywriteRoot.incrementProgress(incr, 80);
	switch (version) {
	case 1:
	    setSpeed(in.readInt());
	    break;
	case 2:
	case 3:
	    setSpeed(in.readInt());
	    globalVariables.readContents(in);
	    break;
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	    globalVariables.readContents(in);
	    Variable.systemVariable(SYS_WINDOW_COLOR_VARIABLE_ID, this)
		.setValue(this, defaultColor);
	    break;
	case 9:
	case 10:
	case 11:
	case 12:
	    globalVariables.readContents(in);
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 12);
	}
	if (_resetFlag)
	    getVariableList().readListValuesAndNotify(in, this);
	else
	    getVariableList().readListValues(in, this);
	computeSpeedParams();
	PlaywriteRoot.incrementProgress(incr, 80);
	Debug.print("debug.objectstore", "Reading stages - ",
		    Debug.mem("debug.objectstore"));
	stages = (XYContainer) in.readObject();
	stageNameCounter = in.readInt();
	PlaywriteRoot.incrementProgress(incr, 80);
	Debug.print("debug.objectstore", "Reading sounds - ",
		    Debug.mem("debug.objectstore"));
	sounds = (XYContainer) in.readObject();
	PlaywriteRoot.incrementProgress(incr, 80);
	Debug.print("debug.objectstore", "Reading prototypes - ",
		    Debug.mem("debug.objectstore"));
	prototypes = (XYCharContainer) in.readObject();
	prototypeCounter = in.readInt();
	PlaywriteRoot.incrementProgress(incr, 80);
	Debug.print("debug.objectstore", "Reading specials - ",
		    Debug.mem("debug.objectstore"));
	specialPrototypes = (XYCharContainer) in.readObject();
	Debug.print("debug.objectstore", "Reading jars - ",
		    Debug.mem("debug.objectstore"));
	jars = (XYContainer) in.readObject();
	PlaywriteRoot.setProgress(80);
	switch (version) {
	case 6:
	case 7:
	case 8:
	case 9:
	case 10:
	case 11:
	case 12:
	    Debug.print("debug.objectstore", "Reading backgrounds - ",
			Debug.mem("debug.objectstore"));
	    in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 12);
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	    /* empty */
	}
	PlaywriteRoot.incrementProgress(incr, 80);
	switch (version) {
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	case 9:
	case 10:
	case 11:
	case 12:
	    _timeoutBox = (XYCharContainer) in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 12);
	case 1:
	case 2:
	    /* empty */
	}
	Vector vdVec = null;
	switch (version) {
	case 1:
	case 2:
	case 3:
	case 4: {
	    _controlPanelIsVisible = true;
	    _sidelineIsVisible = false;
	    int visibleStageCount = in.readInt();
	    for (int i = 0; i < visibleStageCount; i++)
		visibleStageList.addElement(in.readObject());
	    vdVec = new Vector(10);
	    vdVec.addElement(new Integer(1));
	    vdVec.addElement(new Integer(0));
	    vdVec.addElement(new Integer(0));
	    vdVec.addElement(new Integer(0));
	    vdVec.addElement(new Integer(0));
	    for (int i = 0; i < visibleStageCount; i++) {
		vdVec.addElement(visibleStageList.elementAt(i));
		vdVec.addElement(new Integer(100 / visibleStageCount));
		vdVec.addElement(new Integer(0));
		vdVec.addElement(new Integer(0));
	    }
	    break;
	}
	case 5:
	case 6:
	    _controlPanelIsVisible = in.readBoolean();
	    _sidelineIsVisible = in.readBoolean();
	    vdVec = ((WorldInStream) in).readVector();
	    break;
	case 7:
	    _controlPanelIsVisible = in.readBoolean();
	    _sidelineIsVisible = in.readBoolean();
	    vdVec = ((WorldInStream) in).readVector();
	    ((WorldInStream) in).readVector();
	    break;
	case 8:
	case 9:
	case 10:
	case 11:
	case 12:
	    _controlPanelIsVisible = in.readBoolean();
	    _sidelineIsVisible = in.readBoolean();
	    ((WorldInStream) in).readVector();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 12);
	}
	switch (version) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	    if (vdVec != null) {
		_viewData = new ViewData();
		_viewData.putData("WW Show Menu", vdVec);
	    }
	    break;
	case 8:
	case 9:
	case 10:
	case 11:
	case 12:
	    _viewData = (ViewData) in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 12);
	}
	switch (version) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	case 9:
	case 10:
	case 11: {
	    vdVec = _viewData.getData("WW Show Menu");
	    int width = ((Integer) vdVec.elementAt(3)).intValue();
	    int height = ((Integer) vdVec.elementAt(4)).intValue();
	    int topMargin = 41;
	    int leftMargin = 14;
	    int rightMargin = _sidelineIsVisible ? 109 : 14;
	    int bottomMargin = _controlPanelIsVisible ? 58 : 14;
	    int stageWidth = width - (leftMargin + rightMargin);
	    int stageHeight = height - (bottomMargin + topMargin);
	    vdVec.setElementAt(new Integer(stageWidth), 3);
	    vdVec.setElementAt(new Integer(stageHeight), 4);
	    break;
	}
	default:
	    throw new UnknownVersionError(this.getClass(), version, 12);
	case 12:
	    /* empty */
	}
	Variable.discardCocoaFixTable();
	((WorldInStream) in).addFinishedReadingWatcher(new Watcher() {
	    public void update(Object o1, Object o2) {
		World.this.fixV1Specials();
		_root.addRegisteredPrototypes(World.this);
	    }
	});
	CocoaCharacter.notifyReadCompleted((WorldInStream) in,
					   getPrototypes().getContentArray());
	CocoaCharacter.notifyReadCompleted((WorldInStream) in,
					   getSpecialPrototypes()
					       .getContentArray());
	PlaywriteRoot.app();
	if (PlaywriteRoot.isServer())
	    setViewData(_viewData.getData("WW Show Menu"));
    }
    
    private void fixV1Specials() {
	XYCharContainer spDrawer = getSpecialPrototypes();
	Vector replace = new Vector(5);
	Enumeration specials = spDrawer.getContents();
	while (specials.hasMoreElements()) {
	    CharacterPrototype special
		= (CharacterPrototype) specials.nextElement();
	    if (special.numberOfInstances() == 0
		&& countRulesReferringTo(special) == 0)
		replace.addElement(special);
	}
	for (int i = 0; i < replace.size(); i++)
	    spDrawer.remove((CharacterPrototype) replace.elementAt(i));
    }
    
    void stopAndReset() {
	_resetOnStop = true;
	stopWorld();
    }
    
    public String toString() {
	String result = null;
	try {
	    result = "<World '" + getName() + "'>";
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
    
    public static void dumpVariables(World w) {
	XYContainer[] protos = { w.getPrototypes(), w.getSpecialPrototypes() };
	for (int i = 0; i < protos.length; i++) {
	    Enumeration e = protos[i].getContents();
	    while (e.hasMoreElements()) {
		CharacterPrototype proto
		    = (CharacterPrototype) e.nextElement();
		Variable.dumpVariablesFor(proto);
		Enumeration en = proto.getInstances();
		while (en.hasMoreElements()) {
		    CharacterInstance ch
			= (CharacterInstance) en.nextElement();
		    Variable.dumpVariablesFor(ch);
		}
	    }
	}
	Debug.print(true, "---------------");
	Debug.print(true, "");
    }
}
