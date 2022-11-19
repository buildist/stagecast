/* Tutorial - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.File;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.Vector;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.Timer;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.application.Window;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class Tutorial
    implements Debug.Constants, ResourceIDs.CharacterWindowIDs,
	       ResourceIDs.CommandIDs, ResourceIDs.ControlPanelIDs,
	       ResourceIDs.DrawerIDs, ResourceIDs.RuleEditorIDs,
	       ResourceIDs.SplashScreenIDs, ResourceIDs.TutorialWindowIDs,
	       ResourceIDs.WorldViewIDs, StateWatcher, Target, Watcher
{
    static final String TUTORIAL_WORLD_DIRECTORY = getTutorialDirectory();
    static final String TUTORIAL_DIRECTORY = "tutorial";
    static final String DEFAULT_BASENAME = "lesson";
    static final String SYS_TUTORIAL_FILE_VARIABLE_ID
	= "Stagecast.Tutorial:file_variable".intern();
    static final String startString = "<!--";
    static final String endString = "-->";
    static final String TIMERCOMMAND = "TIMERCOMMAND";
    static final int SHORTDELAY = 300;
    static final int LONGDELAY = 700;
    static final int REPEATSHORT = 4;
    static final String LOC_CONTROL_PANEL = "ControlPanel";
    static final String LOC_RULE_MAKER = "RuleMaker";
    static final String LOC_CHARACTER_WINDOW = "CharacterWindow";
    static final String LOC_PICTURE_PAINTER = "PicturePainter";
    static final String LOC_DRAWER = "Drawer";
    static final String LOC_STAGE = "Stage";
    static final String LOC_STAGE_AND_RULEEDITOR = "both";
    private static final String EXECUTE_COMMANDS = "execute commands";
    private static Tutorial _tutorial = null;
    private static final String[] DISABLED_MENU_COMMANDS
	= { "command ss", "command reset world", "command nw", "command ow",
	    "command osw" };
    private static final String[] SAVE_MENU_COMMANDS
	= { "command sw", "command swa", "command swp" };
    private TutorialWindow _tutorialWindow;
    private TutorialAgent _tutorialAgent = null;
    private World _world = null;
    private Timer _flashTimer;
    private int _timerCount = 0;
    private Hashtable _toolLookup = new Hashtable(6);
    private Hashtable _stateLookup = new Hashtable(6);
    private StringMappingTable _buttonLookup = new StringMappingTable();
    private COM.stagecast.ifc.netscape.util.Vector _onState
	= new COM.stagecast.ifc.netscape.util.Vector(1);
    private COM.stagecast.ifc.netscape.util.Vector _doThis
	= new COM.stagecast.ifc.netscape.util.Vector(1);
    private Flashable _flashingThing;
    private COM.stagecast.ifc.netscape.util.Vector _flashingThings
	= new COM.stagecast.ifc.netscape.util.Vector(2);
    private COM.stagecast.ifc.netscape.util.Vector _goToPageNumbers
	= new COM.stagecast.ifc.netscape.util.Vector(1);
    private COM.stagecast.ifc.netscape.util.Vector _triggerStageNames
	= new COM.stagecast.ifc.netscape.util.Vector(1);
    private Rule _testRule = null;
    private Rule _currentRule = null;
    private int _failPageNumber = -1;
    private int _nextPageNumber = -1;
    private int _prevPageNumber = -1;
    private Size _charWinSize = null;
    private Size _ruleMakerSize = null;
    private Size _picturePainterSize = null;
    private boolean _doubleClickDisabled = false;
    private boolean _saveEnabled = true;
    private COM.stagecast.ifc.netscape.util.Vector _windowDataVector
	= new COM.stagecast.ifc.netscape.util.Vector(10);
    private COM.stagecast.ifc.netscape.util.Vector _imageMaps
	= new COM.stagecast.ifc.netscape.util.Vector(2);
    
    static class AfterBoardHandleFlasher
    {
	AfterBoardHandle handle;
	boolean[] mask;
    }
    
    static class TutorialWindowData
    {
	String windowName;
	int minScreenSize;
	Rect rect;
    }
    
    private static class ImageMap
    {
	Rect rect;
	String urlString;
	
	ImageMap(int x, int y, int width, int height, String url) {
	    rect = new Rect(x, y, width, height);
	    urlString = url;
	}
    }
    
    private class StringMappingTable
    {
	COM.stagecast.ifc.netscape.util.Vector keys
	    = new COM.stagecast.ifc.netscape.util.Vector(10);
	COM.stagecast.ifc.netscape.util.Vector values
	    = new COM.stagecast.ifc.netscape.util.Vector(10);
	
	void put(String key, String value) {
	    keys.addElement(key);
	    values.addElement(value);
	}
	
	String get(String key) {
	    if (key != null) {
		for (int i = 0; i < keys.size(); i++) {
		    if (key.equalsIgnoreCase((String) keys.elementAt(i)))
			return (String) values.elementAt(i);
		}
	    }
	    return null;
	}
    }
    
    private static String getTutorialDirectory() {
	String result = null;
	if (PlaywriteRoot.isApplication()) {
	    String classPath = System.getProperty("java.class.path");
	    String lowerCaseCP = classPath.toLowerCase();
	    Debug.print("debug.tutorial", "classpath: ", classPath);
	    int endIndex = lowerCaseCP.indexOf("tutorial.jar");
	    if (endIndex == -1) {
		endIndex = lowerCaseCP.indexOf("tutorial");
		if (endIndex != -1)
		    endIndex += "tutorial".length();
	    }
	    if (endIndex != -1) {
		int startIndex
		    = classPath.lastIndexOf(File.pathSeparator, endIndex) + 1;
		if (startIndex == -1)
		    startIndex = 0;
		if (endIndex > startIndex) {
		    result = classPath.substring(startIndex, endIndex);
		    if (!result.endsWith(File.separator))
			result += File.separator;
		}
	    }
	}
	if (result != null)
	    Debug.print("debug.tutorial", "tutorial path: ", result);
	else
	    Debug.print("debug.tutorial", "tutorial path not found!");
	return result;
    }
    
    static void initStatics() {
	new Variable(SYS_TUTORIAL_FILE_VARIABLE_ID, "tutorial file", true);
    }
    
    Tutorial(World w) {
	_tutorial = this;
	_world = w;
	init();
    }
    
    void init() {
	PlaywriteRoot.markBusy();
	for (int i = 0; i < DISABLED_MENU_COMMANDS.length; i++)
	    _world.setMenuCommandEnabled(DISABLED_MENU_COMMANDS[i], false);
	CharacterWindow.setTutorialOverride(true);
	CharacterWindow.setVariablesOpenState(false);
	RuleEditor re = RuleEditor.getRuleEditor();
	sizeWorldGloveTight();
	if (re != null)
	    re.closeTestsAndActions();
	PlaywriteWindow worldWindow = _world.getWindow();
	Rect worldWindowRect = worldWindow.bounds();
	Size screenSize = PlaywriteRoot.getRootWindowSize();
	_toolLookup.put("NEW_CHARACTER", Tool.newCharacterTool);
	_toolLookup.put("EDIT_APPEARANCE", Tool.editAppearanceTool);
	_toolLookup.put("NEW_RULE", Tool.newRuleTool);
	_toolLookup.put("COPY_LOAD", Tool.copyLoadTool);
	_toolLookup.put("DELETE", Tool.deleteTool);
	_toolLookup.put("ruleMakerDontCare", RuleEditor.dontCareTool);
	_toolLookup.put("ruleMakerMouseTest", RuleEditor.mouseClickTool);
	_toolLookup.put("ruleMakerCopy", Tool.copyLoadTool);
	_toolLookup.put("ruleMakerDelete", Tool.deleteTool);
	_toolLookup.put("NEWVARIABLE", VariableListView.newVariableTool);
	_stateLookup.put("Play", World.RUNNING);
	_stateLookup.put("Stop", World.STOPPED);
	_stateLookup.put("Record", World.RECORDING);
	_stateLookup.put("RuleMakerDone", World.DONE);
	_stateLookup.put("RuleMakerCancel", World.CANCEL);
	_buttonLookup.put("PlayR", "CP rewind");
	_buttonLookup.put("StepR", "CP step back");
	_buttonLookup.put("Stop", "CP stop");
	_buttonLookup.put("Step", "CP step forward");
	_buttonLookup.put("Play", "CP Play");
	_buttonLookup.put("Turtle", "CP SLOW");
	_buttonLookup.put("Speed", "CP MEDIUM");
	_buttonLookup.put("Bunny", "CP FAST");
	_buttonLookup.put("SpeedFull", "CP MAX");
	_buttonLookup.put("characterWindowDone", PlaywriteWindow.CLOSE);
	_buttonLookup.put("NEWSUB", "NEWSUB");
	_buttonLookup.put("NEWCOMMENT", "NEWCOMMENT");
	_buttonLookup.put("NEWPRETEST", "NEWPRETEST");
	_buttonLookup.put("DISABLERULE", "DISABLERULE");
	_buttonLookup.put("BREAKPOINT", "BREAKPOINT");
	_buttonLookup.put("STEPRULE", "STEPRULE");
	_buttonLookup.put("sidelineTab", "WW Show Sidelines");
	_buttonLookup.put("creatorMenu", "WW Show Menu");
	_buttonLookup.put("controlPanelTab", "WW Show Control Panel");
	_buttonLookup.put("stageSplit", "SD Toggle Stage Split");
	_buttonLookup.put("ruleMakerDone", PlaywriteWindow.CLOSE);
	_buttonLookup.put("ruleMakerCancel", "command c");
	_buttonLookup.put("ruleMakerEdit", "RE er");
	_buttonLookup.put("ruleMakerTest", "RE tr");
	_buttonLookup.put("ruleMakerExamine", "RE e");
	_buttonLookup.put("ruleMakerShowVariables", "RE sv");
	_buttonLookup.put("ruleMakerTestTab", "RE st");
	_buttonLookup.put("ruleMakerActionTab", "RE sa");
	_buttonLookup.put("ruleMakerKeyTest", "RE kt");
	_buttonLookup.put("ruleMakerAndIf", "RE nt");
	_buttonLookup.put("ruleMakerPut", "RE pa");
	_buttonLookup.put("ruleMakerPutCalc", "RE pca");
	_buttonLookup.put("picturePainerZoom", "special");
	_buttonLookup.put("done", "command done");
	_buttonLookup.put("cancel", "command c");
	_buttonLookup.put("nextButton", "tutorial n");
	_buttonLookup.put("prevButton", "tutorial p");
	_tutorialAgent = new TutorialAgent();
	_world.addStateWatcher(this);
	if (Variable.systemVariable(SYS_TUTORIAL_FILE_VARIABLE_ID, _world)
	    == null) {
	    Variable v
		= Variable.newSystemVariable(SYS_TUTORIAL_FILE_VARIABLE_ID,
					     _world);
	    _world.add(v);
	    v.setValue(_world, "lesson");
	}
	Variable v
	    = Variable.systemVariable(SYS_TUTORIAL_FILE_VARIABLE_ID, _world);
	v.addValueWatcher(_world, this);
	String lesson = v.getValue(_world).toString();
	_tutorialWindow = new TutorialWindow(this, _world, lesson);
	int displayWindowHeight = _tutorialWindow.height();
	int remains = screenSize.height - displayWindowHeight;
	_tutorialWindow.tutorialMove(2, worldWindowRect);
	setSaveEnabled(false);
	_world.setMenuCommandEnabled("command pw", false);
	_tutorialWindow.loadTutorialPage();
	_tutorialWindow.show();
	PlaywriteRoot.clearBusy();
    }
    
    public static Tutorial getTutorial() {
	return _tutorial;
    }
    
    public static boolean isTutorialRunning() {
	return _tutorial != null;
    }
    
    public WorldView getWorldView() {
	return _world.getWorldView();
    }
    
    public int getNextPageNumber() {
	return _nextPageNumber;
    }
    
    public void setNextPageNumber(int n) {
	_nextPageNumber = n;
    }
    
    public int getPrevPageNumber() {
	return _prevPageNumber;
    }
    
    public void setPrevPageNumber(int n) {
	_prevPageNumber = n;
    }
    
    public void sizeWorldGloveTight() {
	Stage s = _world.getFirstVisibleStage();
	Size winSize
	    = getWorldView().getWindowSizeForStageSize(s.desiredWidth(),
						       s.desiredHeight());
	Rect rootRect = PlaywriteRoot.getMainRootViewBounds();
	if (rootRect.width > winSize.width && rootRect.height > winSize.height)
	    _world.getWindow().sizeTo(winSize.width, winSize.height);
    }
    
    public static boolean allowMenuCommandEnable(String menuCommand) {
	if (isTutorialRunning()) {
	    for (int i = 0; i < SAVE_MENU_COMMANDS.length; i++) {
		if (menuCommand == SAVE_MENU_COMMANDS[i])
		    return getTutorial().isSaveEnabled();
	    }
	    for (int i = 0; i < DISABLED_MENU_COMMANDS.length; i++) {
		if (menuCommand == DISABLED_MENU_COMMANDS[i])
		    return false;
	    }
	}
	return true;
    }
    
    public Rect getWindowBounds(String loc) {
	return getWindowBounds(loc,
			       PlaywriteRoot.getMainRootView().bounds.width);
    }
    
    private Rect getWindowBounds(String loc, int screenWidth) {
	int minW = 0;
	Rect result = null;
	int bestIndex = -1;
	int defaultIndex = -1;
	for (int i = 0; i < _windowDataVector.size(); i++) {
	    TutorialWindowData data
		= (TutorialWindowData) _windowDataVector.elementAt(i);
	    if (data.windowName == loc) {
		if (data.minScreenSize == -1)
		    defaultIndex = i;
		else if (screenWidth >= data.minScreenSize
			 && data.minScreenSize > minW) {
		    bestIndex = i;
		    minW = data.minScreenSize;
		}
	    }
	}
	if (bestIndex == -1)
	    bestIndex = defaultIndex;
	if (bestIndex != -1) {
	    TutorialWindowData data
		= (TutorialWindowData) _windowDataVector.elementAt(bestIndex);
	    result = new Rect(data.rect);
	    if (data.rect.x == -1)
		result.moveTo(screenWidth - data.rect.width, 0);
	}
	return result;
    }
    
    public boolean doubleClickDisabled() {
	return _doubleClickDisabled;
    }
    
    public boolean wantToTestRule() {
	return _testRule != null;
    }
    
    public boolean testRule(Rule currentRule) {
	if (_testRule == null)
	    return true;
	boolean result = compareRules(currentRule, _testRule);
	if (!result)
	    _tutorialWindow.goToPage(_failPageNumber);
	return result;
    }
    
    public synchronized void close() {
	Variable.systemVariable(SYS_TUTORIAL_FILE_VARIABLE_ID, _world)
	    .removeValueWatcher(_world, this);
	_tutorial = null;
	_tutorialWindow = null;
	_world.removeStateWatcher(this);
	_world = null;
	if (_flashTimer != null)
	    _flashTimer.stop();
	_flashTimer = null;
	_flashingThing = null;
	_goToPageNumbers.removeAllElements();
	_goToPageNumbers = null;
	_triggerStageNames.removeAllElements();
	_triggerStageNames = null;
	if (_tutorialAgent != null) {
	    _tutorialAgent.destroyWindow();
	    _tutorialAgent.hide();
	}
	_tutorialAgent = null;
	_onState.removeAllElements();
	_onState = null;
	_doThis.removeAllElements();
	_doThis = null;
	CharacterWindow.setTutorialOverride(false);
    }
    
    public void stageSwitched(String stageName) {
	int pageNumber = -1;
	int index = -1;
	Debug.print("debug.tutorial", "switching to stage ", stageName);
	for (int i = 0; i < _triggerStageNames.size(); i++) {
	    if (stageName.equalsIgnoreCase((String)
					   _triggerStageNames.elementAt(i)))
		index = i;
	}
	Debug.print("debug.tutorial", ".. index == ", index);
	if (index != -1) {
	    try {
		pageNumber = Integer.parseInt((String) _goToPageNumbers
							   .elementAt(index));
	    } catch (NumberFormatException numberformatexception) {
		/* empty */
	    }
	    Debug.print("debug.tutorial", "page number ", pageNumber);
	    if (pageNumber != -1)
		_tutorialWindow.goToPage(pageNumber);
	}
    }
    
    public void executeCommands(String commands) {
	if (!PlaywriteRoot.app().inEventThread())
	    Application.application()
		.performCommandAndWait(this, "execute commands", commands);
	else
	    executeCommandsInDrawingThread(commands);
    }
    
    private void executeCommandsInDrawingThread(String commands) {
	StringTokenizer lineTokenizer = new StringTokenizer(commands, "\n\r;");
	String command = new String();
	COM.stagecast.ifc.netscape.util.Vector args
	    = new COM.stagecast.ifc.netscape.util.Vector(1);
	String arg = new String();
	int ttype = 0;
	ASSERT.isInEventThread();
	while (lineTokenizer.hasMoreTokens()) {
	    String token = lineTokenizer.nextToken();
	    StreamTokenizer cmdLine
		= new StreamTokenizer(new StringReader(token));
	    cmdLine.quoteChar(34);
	    cmdLine.whitespaceChars(32, 32);
	    args = new COM.stagecast.ifc.netscape.util.Vector(1);
	    try {
		ttype = cmdLine.nextToken();
	    } catch (java.io.IOException ioexception) {
		/* empty */
	    }
	    command = cmdLine.sval;
	    if (ttype != -1) {
		Debug.print("debug.tutorial", "executing command " + token);
		try {
		    ttype = cmdLine.nextToken();
		} catch (java.io.IOException ioexception) {
		    /* empty */
		}
		while (ttype != -1) {
		    arg = cmdLine.sval;
		    if (arg == null)
			arg = String.valueOf((int) cmdLine.nval);
		    args.addElement(arg);
		    try {
			ttype = cmdLine.nextToken();
		    } catch (java.io.IOException ioexception) {
			/* empty */
		    }
		}
		if (command.equalsIgnoreCase("disableControlPanelButtons"))
		    disableControlPanelButtons();
		else if (command.equalsIgnoreCase("enableControlPanelButtons"))
		    enableControlPanelButtons();
		else if (command.equalsIgnoreCase("nonDeletable")) {
		    CocoaCharacter c
			= getCharacterNamed(_world.getFirstVisibleStage(),
					    (String) args.firstElement());
		    if (c != null)
			c.setDeletable(false);
		} else if (command.equalsIgnoreCase("flashCharacter")) {
		    String location = null;
		    String name = (String) args.firstElement();
		    if (args.size() > 1)
			location = (String) args.elementAt(1);
		    CharacterView cv = null;
		    if (location == null || location.equalsIgnoreCase("Stage"))
			location = "Stage";
		    else if (location.equalsIgnoreCase("RuleMaker"))
			location = "RuleMaker";
		    else
			_tutorialWindow.tutorialError
			    ("unknown location for flashCharacter: " + location
			     + " (i know: " + "Stage" + ", " + "RuleMaker"
			     + ")");
		    if (RuleEditor.isRecordingOrEditing())
			cv = getAfterBoardCharacterView(name, location);
		    else if (!"RuleMaker".equalsIgnoreCase(location)) {
			CharacterInstance ci = getCharacterNamed(name);
			cv = getCharacterView(ci,
					      _world.getFirstVisibleStage());
		    }
		    if (cv != null)
			requestFlash(cv);
		} else if (command.equalsIgnoreCase("disableNextButton"))
		    _tutorialWindow.enableNextButton(false);
		else if (command.equalsIgnoreCase("disablePreviousButton"))
		    _tutorialWindow.enablePrevButton(false);
		else if (command.equalsIgnoreCase("enableNextButton"))
		    _tutorialWindow.enableNextButton(true);
		else if (command.equalsIgnoreCase("enablePreviousButton"))
		    _tutorialWindow.enablePrevButton(true);
		else if (command.equalsIgnoreCase("enableAllButtons")
			 || command.equalsIgnoreCase("disableAllButtons")) {
		    boolean enable
			= command.equalsIgnoreCase("enableAllButtons");
		    String loc = (String) args.elementAt(0);
		    String charName = null;
		    if (args.size() > 1)
			charName = (String) args.elementAt(1);
		    View locView = getViewForLocation(loc, charName);
		    if (locView != null) {
			COM.stagecast.ifc.netscape.util.Vector buttons
			    = Util.getAllButtons(locView);
			for (int i = 0; i < buttons.size(); i++) {
			    PlaywriteButton button
				= (PlaywriteButton) buttons.elementAt(i);
			    button.setTutorialDisabled(enable ^ true);
			}
		    } else
			Debug.print("debug.tutorial",
				    "did not find location: ", loc, " for ",
				    charName);
		} else if (command.lastIndexOf("Button") != -1) {
		    String buttonName = (String) args.firstElement();
		    String loc = null;
		    String charName = null;
		    if (args.size() > 1)
			loc = (String) args.elementAt(1);
		    if (args.size() > 2)
			charName = (String) args.elementAt(2);
		    PlaywriteButton pb = getButton(buttonName, loc, charName);
		    if (pb != null) {
			if (command.equalsIgnoreCase("flashButton"))
			    requestFlash(pb);
			else if (command.equalsIgnoreCase("enableButton"))
			    pb.setTutorialDisabled(false);
			else if (command.equalsIgnoreCase("disableButton"))
			    pb.setTutorialDisabled(true);
		    } else
			Debug.print("debug.tutorial", "did not find button: ",
				    buttonName, " in ", loc, " for ",
				    charName);
		} else if (command.equalsIgnoreCase("flashHandles")) {
		    if (args.size() == 0)
			_tutorialWindow.tutorialError
			    ("flashHandles needs arguments ( flashHandles [Stage | RuleMaker ] 1 ...)");
		    String location = (String) args.elementAt(0);
		    int startIndex = 1;
		    if (!location.equalsIgnoreCase("Stage")
			&& !location.equalsIgnoreCase("RuleMaker")) {
			startIndex = 0;
			location = "Stage";
		    }
		    boolean[] mask = new boolean[4];
		    for (int k = startIndex; k < args.size(); k++) {
			try {
			    int index
				= Integer.parseInt((String) args.elementAt(k));
			    mask[index] = true;
			} catch (NumberFormatException numberformatexception) {
			    _tutorialWindow.tutorialError
				("flashHandles: " + (String) args.elementAt(k)
				 + "is not a valid number. handles are numbered from 1..4");
			}
		    }
		    AfterBoardHandleFlasher flasher
			= new AfterBoardHandleFlasher();
		    flasher.handle = getAfterBoardHandle(location);
		    if (flasher.handle != null) {
			flasher.mask = mask;
			requestFlash(flasher);
		    }
		} else if (command.equalsIgnoreCase("disableDoubleClick"))
		    _doubleClickDisabled = true;
		else if (command.equalsIgnoreCase("goToPage")) {
		    try {
			int pageNumber
			    = Integer.parseInt((String) args.elementAt(0));
			_tutorialWindow.goToPage(pageNumber);
		    } catch (NumberFormatException numberformatexception) {
			_tutorialWindow.tutorialError("error in goToPage: "
						      + args.elementAt(0)
						      + " is not a number!");
		    }
		} else if (command.equalsIgnoreCase("hideSystemMenu"))
		    _world.getWorldView().hideSystemMenu();
		else if (command.equalsIgnoreCase("nextPageNumber")
			 || command.equalsIgnoreCase("prevPageNumber")) {
		    try {
			int num = Integer.parseInt((String) args.elementAt(0));
			if (command.equalsIgnoreCase("prevPageNumber"))
			    setPrevPageNumber(num);
			else
			    setNextPageNumber(num);
		    } catch (NumberFormatException numberformatexception) {
			_tutorialWindow.tutorialError("error in " + command
						      + ": "
						      + args.elementAt(0)
						      + " is not a number!");
		    }
		} else if (command.equalsIgnoreCase("onEvent")
			   || command.equalsIgnoreCase("onState")) {
		    String eventName = (String) args.removeFirstElement();
		    COM.stagecast.ifc.netscape.util.Vector v
			= new COM.stagecast.ifc.netscape.util.Vector(1);
		    v.addElements(args);
		    registerEventCommand(eventName, v,
					 command.equalsIgnoreCase("onState"));
		} else if (command.equalsIgnoreCase("pageSwitchAction")) {
		    _triggerStageNames.addElement(args.elementAt(0));
		    _goToPageNumbers.addElement(args.elementAt(1));
		} else if (command.equalsIgnoreCase("playSound")) {
		    if (!_tutorialWindow.playSound((String) args.elementAt(0)))
			_tutorialWindow.tutorialError
			    ("playSound " + args.elementAt(0)
			     + " failed. the file is either not there or in the wrong format (must be .au).");
		} else if (command.equalsIgnoreCase("ruleRecordKludge")) {
		    if (args.size() != 0) {
			CharacterInstance ci
			    = getCharacterNamed((String) args.elementAt(0));
			CharacterView cv
			    = getViewForCharacter(_world
						      .getFirstVisibleStage(),
						  ci);
			if (ci != null && cv != null) {
			    COM.stagecast.ifc.netscape.util.Vector v
				= (new COM.stagecast.ifc.netscape.util.Vector
				   (2));
			    PlaywriteButton ruleRec
				= getButton("NEW_RULE", "ControlPanel", null);
			    if (ruleRec.isEnabled()) {
				String s = "cmd Animate";
				v.addElement(s);
				Point p = (ruleRec.convertToView
					   (PlaywriteRoot.getMainRootView(),
					    ruleRec.width() / 2,
					    ruleRec.height() / 2));
				v.addElement(p);
				_tutorialAgent.addCommand(v);
				v = (new COM.stagecast.ifc.netscape.util.Vector
				     (2));
				s = "clickOn";
				v.addElement(s);
				v.addElement(ruleRec);
				_tutorialAgent.addCommand(v);
				v = (new COM.stagecast.ifc.netscape.util.Vector
				     (2));
				s = "dragToolTo";
				v.addElement(s);
				p = cv.convertToView(PlaywriteRoot
							 .getMainRootView(),
						     cv.width() / 2,
						     cv.height() / 2);
				v.addElement(p);
				_tutorialAgent.addCommand(v);
				v = (new COM.stagecast.ifc.netscape.util.Vector
				     (2));
				s = "clickTool";
				v.addElement(s);
				v.addElement(cv);
				_tutorialAgent.addCommand(v);
				v = (new COM.stagecast.ifc.netscape.util.Vector
				     (2));
				s = "dragABHandle";
				v.addElement(s);
				v.addElement(new Point(1, 0));
				_tutorialAgent.addCommand(v);
				v = (new COM.stagecast.ifc.netscape.util.Vector
				     (3));
				s = "dragABChar";
				v.addElement(s);
				v.addElement(ci);
				v.addElement(new Point(1, 0));
				_tutorialAgent.addCommand(v);
				v = (new COM.stagecast.ifc.netscape.util.Vector
				     (1));
				s = "pushDoneInRuleMaker";
				v.addElement(s);
				_tutorialAgent.addCommand(v);
			    } else
				Debug.print
				    ("debug.tutorial.agent",
				     "rule tool disabled. no record right rule agent.");
			} else
			    Debug.print(true,
					"didn't find character instance ", ci,
					" ", cv, " ",
					_world.getFirstVisibleStage());
		    }
		} else if (command.equalsIgnoreCase("showAgent")) {
		    COM.stagecast.ifc.netscape.util.Vector v
			= new COM.stagecast.ifc.netscape.util.Vector(1);
		    String s = "show";
		    v.addElement(s);
		    _tutorialAgent.addCommand(v);
		} else if (command.equalsIgnoreCase("hideAgent")) {
		    COM.stagecast.ifc.netscape.util.Vector v
			= new COM.stagecast.ifc.netscape.util.Vector(1);
		    String s = "hide";
		    v.addElement(s);
		    _tutorialAgent.addCommand(v);
		} else if (command.equalsIgnoreCase("moveAgent")) {
		    COM.stagecast.ifc.netscape.util.Vector v
			= new COM.stagecast.ifc.netscape.util.Vector(3);
		    String s = "cmd Animate";
		    v.addElement(s);
		    int x = Integer.parseInt((String) args.elementAt(0));
		    int y = Integer.parseInt((String) args.elementAt(1));
		    v.addElement(new Point(x, y));
		    _tutorialAgent.addCommand(v);
		} else if (command.equalsIgnoreCase("resizeWindow")) {
		    int width = Integer.parseInt((String) args.elementAt(0));
		    int height = Integer.parseInt((String) args.elementAt(1));
		    _tutorialWindow.tutorialResize(width, height,
						   _world.getWindow()
						       .bounds());
		    if (args.size() > 2) {
			String whereTo = (String) args.elementAt(2);
			if (whereTo.equalsIgnoreCase("UnderWorld"))
			    _tutorialWindow
				.tutorialMove(0, _world.getWindow().bounds());
			else if (whereTo.equalsIgnoreCase("CoverWorld"))
			    _tutorialWindow
				.tutorialMove(1, _world.getWindow().bounds());
			else if (whereTo.equalsIgnoreCase("Auto"))
			    _tutorialWindow
				.tutorialMove(2, _world.getWindow().bounds());
		    } else
			_tutorialWindow
			    .tutorialMove(2, _world.getWindow().bounds());
		} else if (command.equalsIgnoreCase("windowUnderWorld"))
		    _tutorialWindow.tutorialMove(0,
						 _world.getWindow().bounds());
		else if (command.equalsIgnoreCase("windowCoverWorld"))
		    _tutorialWindow.tutorialMove(1,
						 _world.getWindow().bounds());
		else if (command.equalsIgnoreCase("setAgentHotspot")) {
		    int index = Integer.parseInt((String) args.elementAt(0));
		    int x = Integer.parseInt((String) args.elementAt(1));
		    int y = Integer.parseInt((String) args.elementAt(2));
		    _tutorialAgent.setHotSpot(index, new Point(x, y));
		} else if (command.equalsIgnoreCase("setAgentImage")) {
		    int index = Integer.parseInt((String) args.elementAt(0));
		    String imageName = (String) args.elementAt(1);
		    _tutorialAgent.setImage(index, imageName);
		    if (args.size() > 2) {
			int x = Integer.parseInt((String) args.elementAt(2));
			int y = Integer.parseInt((String) args.elementAt(3));
			_tutorialAgent.setHotSpot(index, new Point(x, y));
		    }
		} else if (command.equalsIgnoreCase("setToolTipDelay")) {
		    int newDelay
			= Integer.parseInt((String) args.elementAt(0));
		    ToolTips.setToolTipDelay(newDelay);
		} else if (command.equalsIgnoreCase("enableSave"))
		    setSaveEnabled(true);
		else if (command.equalsIgnoreCase("disableSave"))
		    setSaveEnabled(false);
		else if (command.equalsIgnoreCase("defineImageMap")) {
		    int x = Integer.parseInt((String) args.elementAt(0));
		    int y = Integer.parseInt((String) args.elementAt(1));
		    int width = Integer.parseInt((String) args.elementAt(2));
		    int height = Integer.parseInt((String) args.elementAt(3));
		    String relativeURL = (String) args.elementAt(4);
		    defineImageMap(x, y, width, height, relativeURL);
		} else if (command.equalsIgnoreCase("setWindowSize")) {
		    TutorialWindowData data = new TutorialWindowData();
		    String loc = (String) args.elementAt(0);
		    if (loc.equalsIgnoreCase("CharacterWindow"))
			data.windowName = "CharacterWindow";
		    else if (loc.equalsIgnoreCase("RuleMaker"))
			data.windowName = "RuleMaker";
		    else if (loc.equalsIgnoreCase("PicturePainter"))
			data.windowName = "PicturePainter";
		    else
			_tutorialWindow.tutorialError
			    ("setWindowSize: unknown location: " + loc);
		    data.minScreenSize = -1;
		    if (args.size() == 3)
			data.rect
			    = new Rect(-1, -1,
				       Integer.parseInt((String)
							args.elementAt(1)),
				       Integer.parseInt((String)
							args.elementAt(2)));
		    else if (args.size() == 5)
			data.rect
			    = (new Rect
			       (Integer.parseInt((String) args.elementAt(1)),
				Integer.parseInt((String) args.elementAt(2)),
				Integer.parseInt((String) args.elementAt(3)),
				Integer.parseInt((String) args.elementAt(4))));
		    else if (args.size() == 6) {
			data.minScreenSize
			    = Integer.parseInt((String) args.elementAt(1));
			data.rect
			    = (new Rect
			       (Integer.parseInt((String) args.elementAt(2)),
				Integer.parseInt((String) args.elementAt(3)),
				Integer.parseInt((String) args.elementAt(4)),
				Integer.parseInt((String) args.elementAt(5))));
		    }
		    _windowDataVector.addElement(data);
		    RootView root = PlaywriteRoot.getMainRootView();
		    COM.stagecast.ifc.netscape.util.Vector windows
			= root.internalWindows();
		    AppearanceEditorController aec
			= PlaywriteRoot.getAppearanceEditorController();
		    for (int i = 0; i < windows.size(); i++) {
			Object win = windows.elementAt(i);
			if ((win instanceof CharacterWindow
			     && data.windowName == "CharacterWindow")
			    || (win instanceof RuleEditor
				&& data.windowName == "RuleMaker")
			    || (aec.isEditorWindow((Window) win)
				&& data.windowName == "PicturePainter"))
			    ((PlaywriteWindow) win).boundify();
		    }
		} else if (command.equalsIgnoreCase("TestRule")) {
		    String charName = (String) args.elementAt(0);
		    String ruleName = (String) args.elementAt(1);
		    _failPageNumber
			= Integer.parseInt((String) args.elementAt(2));
		    CharacterPrototype proto = getPrototype(charName);
		    if (proto == null)
			Debug.print("debug.tutorial",
				    ("TestRule: prototype " + charName
				     + " not found"));
		    else {
			_testRule = getRuleFromPrototype(proto, ruleName);
			if (_testRule == null)
			    Debug.print("debug.tutorial",
					("TestRule: rule " + ruleName
					 + " in prototype " + proto
					 + " not found"));
		    }
		} else if (command.equalsIgnoreCase("hidePrototype")) {
		    String charName = (String) args.elementAt(0);
		    CharacterPrototype proto = getPrototype(charName);
		    if (proto == null)
			Debug.print("debug.tutorial",
				    ("hidePrototype: prototype " + charName
				     + " not found"));
		    else
			proto.setVisibility(false);
		} else if (command.equalsIgnoreCase("showPrototype")) {
		    String charName = (String) args.elementAt(0);
		    CharacterPrototype proto = getPrototype(charName);
		    if (proto == null)
			Debug.print("debug.tutorial",
				    ("hidePrototype: prototype " + charName
				     + " not found"));
		    else
			proto.setVisibility(true);
		} else if (command.equalsIgnoreCase("hideSpecials")
			   || command.equalsIgnoreCase("showSpecials")) {
		    boolean vis = command.toLowerCase().startsWith("show");
		    String name = (String) args.elementAt(0);
		    Enumeration protos
			= _world.getSpecialPrototypes().getContents();
		    if ("all".equalsIgnoreCase(name)) {
			while (protos.hasMoreElements()) {
			    CharacterPrototype proto
				= (CharacterPrototype) protos.nextElement();
			    if (!(proto instanceof DoorPrototype)
				|| !((DoorPrototype) proto).isDestinationEnd())
				proto.setVisibility(vis);
			}
		    } else {
			CharacterPrototype proto = null;
			while (protos.hasMoreElements()) {
			    CharacterPrototype temp
				= (CharacterPrototype) protos.nextElement();
			    if ((!(temp instanceof DoorPrototype)
				 || !((DoorPrototype) temp).isDestinationEnd())
				&& temp.getClass().getName().equals(name)) {
				proto = temp;
				break;
			    }
			}
			if (proto == null)
			    Debug.print("debug.tutorial",
					("hideSpecials:  " + name
					 + " not found"));
			else
			    proto.setVisibility(vis);
		    }
		} else if (command.equalsIgnoreCase("allowRuleExtensions"))
		    RuleEditor.setExtensionElementsActive(true);
		else
		    Debug.print("debug.tutorial",
				"Tutorial: unknown command: ", command);
	    }
	}
    }
    
    public void didLoadPage() {
	_tutorialAgent.executeCommands();
    }
    
    public void startOver() {
	_tutorialWindow.goToPage(1);
    }
    
    private void registerEventCommand
	(String eventName, COM.stagecast.ifc.netscape.util.Vector command,
	 boolean checkNow) {
	Object state = _stateLookup.get(eventName);
	if (state != null) {
	    _onState.addElement(state);
	    String cmd = "";
	    for (int i = 0; i < command.size(); i++)
		cmd += "\"" + command.elementAt(i) + "\"" + " ";
	    Debug.print("debug.tutorial", "adding command ", cmd,
			" for event ", eventName,
			", " + state + " checkNow=" + checkNow);
	    _doThis.addElement(cmd);
	    if (checkNow)
		stateChanged(this, null, null, _world.getState());
	} else
	    _tutorialWindow.tutorialError("unknown event: " + eventName);
    }
    
    private void defineImageMap(int x, int y, int width, int height,
				String relativeURL) {
	_imageMaps.addElement(new ImageMap(x, y, width, height, relativeURL));
    }
    
    public String getURLFromImageMap(int x, int y) {
	int size = _imageMaps.size();
	for (int i = 0; i < size; i++) {
	    if (((ImageMap) _imageMaps.elementAt(i)).rect.contains(x, y))
		return ((ImageMap) _imageMaps.elementAt(i)).urlString;
	}
	return null;
    }
    
    private void setSaveEnabled(boolean b) {
	for (int i = 0; i < SAVE_MENU_COMMANDS.length; i++)
	    _world.setMenuCommandEnabled(SAVE_MENU_COMMANDS[i], b);
	_saveEnabled = b;
    }
    
    private boolean isSaveEnabled() {
	return _saveEnabled;
    }
    
    private CharacterView getViewForCharacter(Board b,
					      CocoaCharacter character) {
	if (b.getViews().size() == 0)
	    return null;
	View v = (View) b.getViews().firstElement();
	COM.stagecast.ifc.netscape.util.Vector subviews = v.subviews();
	for (int i = 0; i < subviews.size(); i++) {
	    View sv = (View) subviews.elementAt(i);
	    if (sv instanceof CharacterView
		&& ((CharacterView) sv).getCharacter() == character)
		return (CharacterView) sv;
	}
	return null;
    }
    
    public void reset() {
	if (_tutorialAgent != null) {
	    _tutorialAgent.reset();
	    PlaywriteRoot.app().performCommandLater(_tutorialAgent, "hide",
						    null);
	}
	enableControlPanelButtons();
	_tutorialWindow.enableNextButton(true);
	_tutorialWindow.enablePrevButton(true);
	_goToPageNumbers = new COM.stagecast.ifc.netscape.util.Vector(1);
	_triggerStageNames = new COM.stagecast.ifc.netscape.util.Vector(1);
	_flashingThings.removeAllElements();
	if (_flashTimer != null) {
	    _flashTimer.stop();
	    _flashTimer = null;
	    if (_flashingThing != null) {
		_flashingThing.stopFlashing();
		_flashingThing = null;
	    }
	}
	_doubleClickDisabled = false;
	_onState.removeAllElements();
	_doThis.removeAllElements();
	_imageMaps.removeAllElements();
	_nextPageNumber = -1;
	_prevPageNumber = -1;
	_testRule = null;
	_currentRule = null;
	if (_windowDataVector != null)
	    _windowDataVector.removeAllElements();
    }
    
    public PlaywriteButton getButtonNamed(String buttonName, View parentView) {
	COM.stagecast.ifc.netscape.util.Vector buttons
	    = Util.getAllButtons(parentView);
	String buttonCommand = _buttonLookup.get(buttonName);
	if (buttonCommand == "tutorial n")
	    return _tutorialWindow.getNextButton();
	if (buttonCommand == "tutorial p")
	    return _tutorialWindow.getPrevButton();
	if (buttonCommand != null || _toolLookup.get(buttonName) != null) {
	    for (int i = 0; i < buttons.size(); i++) {
		PlaywriteButton test = (PlaywriteButton) buttons.elementAt(i);
		if (test instanceof ToolButton) {
		    if (_toolLookup.get(buttonName)
			== ((ToolButton) test).getTool())
			return test;
		    if (buttonCommand != null
			&& buttonCommand.equals(test.command()))
			return test;
		} else if (buttonCommand != null
			   && buttonCommand.equals(test.command()))
		    return test;
	    }
	} else
	    Debug.print("debug.tutorial", "unknown name: ", buttonName);
	return null;
    }
    
    private View getViewForLocation(String location, String arg) {
	View parentView = null;
	if (location == null)
	    parentView = PlaywriteRoot.getMainRootView();
	else if (location.equalsIgnoreCase("ControlPanel"))
	    parentView = _world.getWorldView().getControlPanelView();
	else if (location.equalsIgnoreCase("RuleMaker")) {
	    if (RuleEditor.getRuleEditor() != null
		&& RuleEditor.isRecordingOrEditing())
		parentView = RuleEditor.getRuleEditor();
	    else if (RuleEditor.getRuleEditor() != null)
		Debug.print
		    ("debug.tutorial",
		     "getViewForLocation: rule editor not in recording mode");
	    else
		Debug.print("debug.tutorial",
			    "getViewForLocation: rule editor null");
	} else if (location.equalsIgnoreCase("CharacterWindow")) {
	    CocoaCharacter character = getCharacterNamed(arg);
	    if (character == null) {
		Debug.print("debug.tutorial", "character ", arg, " not found");
		return null;
	    }
	    parentView = character.getEditor();
	} else if (location.equalsIgnoreCase("PicturePainter")) {
	    RootView rootView = PlaywriteRoot.getMainRootView();
	    COM.stagecast.ifc.netscape.util.Vector v
		= rootView.internalWindows();
	    AppearanceEditorController aec
		= PlaywriteRoot.getAppearanceEditorController();
	    for (int i = 0; i < v.size(); i++) {
		InternalWindow win = (InternalWindow) v.elementAt(i);
		if (aec.isEditorWindow(win)) {
		    PlaywriteWindow pwwin = (PlaywriteWindow) win;
		    CocoaCharacter editChar
			= (CocoaCharacter) pwwin.getModelObject();
		    if (arg != null
			&& arg.equalsIgnoreCase(editChar.getName())) {
			parentView = win;
			break;
		    }
		}
	    }
	} else {
	    Debug.print("debug.tutorial", "unknown location: ", location);
	    PlaywriteSystem.beep();
	}
	return parentView;
    }
    
    private PlaywriteButton getButton(String command, String location,
				      String arg) {
	View parentView = getViewForLocation(location, arg);
	if (parentView != null)
	    return getButtonNamed(command, parentView);
	Debug.print("debug.tutorial", "view for location ", location,
		    " with arg ", arg, " not found");
	return null;
    }
    
    private PlaywriteButton getCharButton
	(COM.stagecast.ifc.netscape.util.Vector args) {
	PlaywriteButton result = null;
	String charName = (String) args.elementAt(0);
	CocoaCharacter character = getCharacterNamed(charName);
	if (character == null) {
	    Debug.print("debug.tutorial", "character ", charName,
			" not found");
	    return null;
	}
	CharacterWindow cw = character.getEditor();
	if (cw == null) {
	    character.edit();
	    cw = character.getEditor();
	}
	if (args.size() < 2) {
	    Debug.print("debug.tutorial", "not enough arguments...");
	    return null;
	}
	String buttonName = (String) args.elementAt(1);
	COM.stagecast.ifc.netscape.util.Vector v = Util.getAllButtons(cw);
	for (int i = 0; i < v.size(); i++) {
	    if (buttonName.equalsIgnoreCase(((PlaywriteButton) v.elementAt(i))
						.command()))
		result = (PlaywriteButton) v.elementAt(i);
	}
	return result;
    }
    
    private void enableControlPanelButtons() {
	ControlPanelView cp = _world.getWorldView().getControlPanelView();
	COM.stagecast.ifc.netscape.util.Vector buttons
	    = Util.getAllButtons(cp);
	for (int i = 0; i < buttons.size(); i++)
	    ((PlaywriteButton) buttons.elementAt(i))
		.setTutorialDisabled(false);
    }
    
    private void disableControlPanelButtons() {
	ControlPanelView cp = _world.getWorldView().getControlPanelView();
	COM.stagecast.ifc.netscape.util.Vector buttons
	    = Util.getAllButtons(cp);
	for (int i = 0; i < buttons.size(); i++)
	    ((PlaywriteButton) buttons.elementAt(i)).setTutorialDisabled(true);
    }
    
    private CharacterView getCharacterView(CocoaCharacter ch, Board board) {
	CharacterView result = null;
	if (board != null && ch != null) {
	    BoardView bv = (BoardView) board.getViews().firstElement();
	    result = getCharacterView(ch, bv);
	}
	return result;
    }
    
    private CharacterView getCharacterView(CocoaCharacter ch,
					   BoardView boardView) {
	CharacterView result = null;
	Enumeration e = boardView.subviews().elements();
	while (e.hasMoreElements()) {
	    View view = (View) e.nextElement();
	    if (view instanceof CharacterView
		&& ((CharacterView) view).getModelObject() == ch) {
		result = (CharacterView) view;
		break;
	    }
	}
	return result;
    }
    
    private CharacterInstance getCharacterNamed(String name) {
	Stage stage = _world.getFirstVisibleStage();
	if (stage != null)
	    return (CharacterInstance) getCharacterNamed(stage, name);
	return null;
    }
    
    private CocoaCharacter getCharacterNamed(Board board, String name) {
	CocoaCharacter result = null;
	synchronized (board.getCharacters()) {
	    COM.stagecast.ifc.netscape.util.Vector characters
		= board.getCharacters();
	    for (int i = 0; i < characters.size(); i++) {
		CocoaCharacter ch = (CocoaCharacter) characters.elementAt(i);
		if (name.equalsIgnoreCase(ch.getName())) {
		    result = ch;
		    break;
		}
	    }
	}
	return result;
    }
    
    private CharacterPrototype getPrototype(String name) {
	CharacterPrototype proto = null;
	XYCharContainer protoDrawer = _world.getPrototypes();
	Enumeration protos = protoDrawer.getContents();
	while (protos.hasMoreElements()) {
	    proto = (CharacterPrototype) protos.nextElement();
	    if (proto.getName().equalsIgnoreCase(name))
		return proto;
	}
	return null;
    }
    
    private Rule getRuleFromPrototype(CharacterPrototype proto,
				      String ruleName) {
	COM.stagecast.ifc.netscape.util.Vector rules = proto.getRules();
	for (int i = 0; i < rules.size(); i++) {
	    if (ruleName
		    .equalsIgnoreCase(((Rule) rules.elementAt(i)).getName()))
		return (Rule) rules.elementAt(i);
	}
	return null;
    }
    
    private CharacterView getAfterBoardCharacterView(String name,
						     String location) {
	CharacterView result = null;
	AfterBoard ab = RuleEditor.getAfterBoard();
	COM.stagecast.ifc.netscape.util.Vector views = ab.getViews();
	View abView = null;
	for (int i = 0; i < views.size(); i++) {
	    View v = (View) views.elementAt(i);
	    if (location.equalsIgnoreCase("Stage")
		&& v.superview() instanceof BoardView) {
		abView = v;
		break;
	    }
	    if (location.equalsIgnoreCase("RuleMaker")
		&& !(v.superview() instanceof BoardView)) {
		abView = v;
		break;
	    }
	}
	if (abView != null) {
	    CocoaCharacter ch = getCharacterNamed(ab, name);
	    result = getCharacterView(ch, (BoardView) abView);
	}
	return result;
    }
    
    AfterBoardHandle getAfterBoardHandle(String location) {
	Vector spotlights = AfterBoardHandle.getSpotlights();
	AfterBoardHandle abh = null;
	if (spotlights.size() == 0)
	    return null;
	Debug.print("debug.tutorial", "size of spotlights: ",
		    spotlights.size());
	synchronized (spotlights) {
	    for (int i = 0; i < spotlights.size(); i++) {
		abh = (AfterBoardHandle) spotlights.elementAt(i);
		if ((location.equalsIgnoreCase("Stage")
		     && abh.superview() instanceof BoardView)
		    || (location.equalsIgnoreCase("RuleMaker")
			&& !(abh.superview() instanceof BoardView)))
		    break;
	    }
	}
	return abh;
    }
    
    private void requestFlash(Object f) {
	if (f instanceof String)
	    Debug.print("debug.tutorial", ("ignoring request to flash " + f
					   + " since the world is running"));
	else if (f != null) {
	    if (_flashTimer != null)
		_flashingThings.addElement(f);
	    else {
		startFlashing(f);
		_flashTimer = new Timer(this, "TIMERCOMMAND", 300);
		_flashTimer.start();
	    }
	}
    }
    
    private void startFlashing(Object f) {
	if (f instanceof AfterBoardHandleFlasher) {
	    Debug.print("debug.tutorial", "start flashing (abh)" + f);
	    AfterBoardHandleFlasher flasher = (AfterBoardHandleFlasher) f;
	    _flashingThing = flasher.handle;
	    flasher.handle.startFlashing(flasher.mask);
	} else if (f instanceof Flashable) {
	    Debug.print("debug.tutorial", "start flashing " + f);
	    _flashingThing = (Flashable) f;
	    _flashingThing.startFlashing();
	}
    }
    
    private boolean compareRules(Rule r1, Rule r2) {
	GeneralizedCharacter self1 = r1.getSelf();
	GeneralizedCharacter self2 = r2.getSelf();
	BeforeBoard bb1 = r1.getBeforeBoard();
	BeforeBoard bb2 = r2.getBeforeBoard();
	if (bb1.numberOfColumns() != bb2.numberOfColumns()
	    || bb1.numberOfRows() != bb2.numberOfRows()) {
	    Debug.print("debug.tutorial",
			"compareRules: wrong number of squares!");
	    return false;
	}
	if (!compareCharactersOnBoard(bb1, bb2, self1, self2)) {
	    Debug.print("debug.tutorial",
			"compareRules: before boards different");
	    return false;
	}
	AfterBoard ab1 = AfterBoard.createFromRule(r1, bb1);
	AfterBoard ab2 = AfterBoard.createFromRule(r2, bb2);
	if (!compareCharactersOnBoard(ab1, ab2, self1, self2)) {
	    Debug.print("debug.tutorial",
			"compareRules: after boards different");
	    return false;
	}
	return true;
    }
    
    private boolean compareCharactersOnBoard(Board b1, Board b2,
					     GeneralizedCharacter self1,
					     GeneralizedCharacter self2) {
	if (b1.numberOfColumns() != b2.numberOfColumns()
	    || b1.numberOfRows() != b2.numberOfRows())
	    return false;
	for (int i = 1; i <= b1.numberOfColumns(); i++) {
	    for (int j = 1; j <= b1.numberOfRows(); j++) {
		COM.stagecast.ifc.netscape.util.Vector s1 = b1.getSquare(i, j);
		COM.stagecast.ifc.netscape.util.Vector s2 = b2.getSquare(i, j);
		if (!matchSquares(s1, s2, self1, self2))
		    return false;
	    }
	}
	return true;
    }
    
    private boolean matchSquares(COM.stagecast.ifc.netscape.util.Vector v1,
				 COM.stagecast.ifc.netscape.util.Vector v2,
				 GeneralizedCharacter self1,
				 GeneralizedCharacter self2) {
	GeneralizedCharacter gch = null;
	if (v1.size() != v2.size())
	    return false;
	int size = v1.size();
	COM.stagecast.ifc.netscape.util.Vector v2Clone
	    = (COM.stagecast.ifc.netscape.util.Vector) v2.clone();
	GeneralizedCharacter used = new GeneralizedCharacter();
	for (int i = 0; i < size; i++) {
	    CocoaCharacter ch = (CocoaCharacter) v1.elementAt(i);
	    if (ch instanceof GeneralizedCharacter)
		gch = (GeneralizedCharacter) ch;
	    else if (ch instanceof GCAlias)
		gch = ((GCAlias) ch).findOriginal();
	    if (gch == self1) {
		if (ch instanceof GeneralizedCharacter) {
		    int index = v2Clone.indexOf(self2);
		    if (index == -1)
			return false;
		    v2Clone.replaceElementAt(index, used);
		} else {
		    boolean found = false;
		    for (int k = 0; k < v2Clone.size(); k++) {
			if (v2Clone.elementAt(k) != used) {
			    GeneralizedCharacter gen
				= ((GCAlias) v2Clone.elementAt(k))
				      .findOriginal();
			    if (gen == self2) {
				found = true;
				v2Clone.replaceElementAt(k, used);
				break;
			    }
			}
		    }
		    if (!found)
			return false;
		}
	    } else if (!replaceMatchingPrototype(ch, v2Clone, used))
		return false;
	}
	return true;
    }
    
    private boolean replaceMatchingPrototype
	(CocoaCharacter original, COM.stagecast.ifc.netscape.util.Vector v,
	 CocoaCharacter placeHolder) {
	int size = v.size();
	for (int i = 0; i < size; i++) {
	    Object o = v.elementAt(i);
	    if (o != placeHolder) {
		CocoaCharacter ch = (CocoaCharacter) o;
		if (original.getPrototype() == ch.getPrototype()) {
		    v.replaceElementAt(i, placeHolder);
		    return true;
		}
	    }
	}
	return false;
    }
    
    public synchronized void performCommand(String command, Object data) {
	if (_tutorial != null) {
	    if (command.equals("execute commands"))
		executeCommandsInDrawingThread((String) data);
	    if (command.equals("TIMERCOMMAND")) {
		if (_flashingThing.isFlashing()) {
		    if (_timerCount == 0)
			_flashTimer.setDelay(300);
		    _timerCount++;
		    if (_timerCount >= 4) {
			_flashTimer.setDelay(700);
			_timerCount = 0;
		    }
		    if (_flashingThing.isHilited())
			_flashingThing.unhilite();
		    else
			_flashingThing.hilite();
		} else {
		    _flashingThing.unhilite();
		    if (!_flashingThings.isEmpty()) {
			Object f = _flashingThings.removeFirstElement();
			startFlashing(f);
		    } else {
			_flashTimer.stop();
			_flashTimer = null;
		    }
		}
	    }
	}
    }
    
    public void update(Object target, Object value) {
	if (target instanceof Variable) {
	    Variable variable = (Variable) target;
	    if (variable.isSystemType(SYS_TUTORIAL_FILE_VARIABLE_ID)) {
		_tutorialWindow.setNewBaseName
		    (Variable.getSystemValue
			 (SYS_TUTORIAL_FILE_VARIABLE_ID, _world).toString());
		_tutorialWindow.loadTutorialPage();
	    }
	}
    }
    
    public void stateChanged(Object target, Object oldState, Object transition,
			     Object newState) {
	Debug.print("debug.tutorial",
		    "update state " + transition + "->" + newState);
	if (newState == World.CLOSING)
	    close();
	else if (!_world.isClosing() && !PlaywriteRoot.appletIsClosing()) {
	    String commandString = new String();
	    if (transition != null) {
		for (int index = _onState.indexOfIdentical(transition);
		     index != -1;
		     index = _onState.indexOfIdentical(transition, index + 1))
		    commandString += (String) _doThis.elementAt(index) + "\n";
	    }
	    for (int index = _onState.indexOfIdentical(newState); index != -1;
		 index = _onState.indexOfIdentical(newState, index + 1))
		commandString += (String) _doThis.elementAt(index) + "\n";
	    executeCommands(commandString);
	}
    }
}
