/* CharacterWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class CharacterWindow extends PlaywriteWindow
    implements Debug.Constants, ResourceIDs.CharacterWindowIDs,
	       ResourceIDs.ControlPanelIDs, Target, ToolSource, Watcher
{
    private static final int RULE_SCROLL_AMOUNT = 50;
    private static final int DEFAULT_WIDTH = 300;
    static final String SHOW_INSTANCES = "SHI";
    static final String UNSHOW_INSTANCES = "USHI";
    static final String SWITCH_OUTLINING = "OUTLINE";
    static final String NEW_SUBROUTINE = "NEWSUB";
    static final String NEW_COMMENT = "NEWCOMMENT";
    static final String NEW_PRETEST = "NEWPRETEST";
    static final String DISABLE_RULE = "DISABLERULE";
    static final String SET_BREAKPOINT = "BREAKPOINT";
    static final String STEP_RULE = "STEPRULE";
    static final String SEPARATOR_DRAG = "sd";
    static final String SEPARATOR_DRAGBEGIN = "sdb";
    static final String SEPARATOR_DRAGEND = "sde";
    static final String SHOW_STEP_BUTTON = "show step button";
    private static final String CHANGE_STEP_TO_PLAY = "chgstptply";
    private static final String RESET_STEP = "resetstep";
    static Tool commentTool;
    static Tool pretestTool;
    static Tool disableTool;
    static Tool breakpointTool;
    private static CharacterWindow _lastCW_ = null;
    private static boolean _tutorialVarsOpen_ = false;
    private static boolean _tutorialOverride_ = false;
    private static CharacterWindow steppingWindow = null;
    private PlaywriteButton newVariableButton;
    private PlaywriteButton newSubButton;
    private PlaywriteButton newPretestButton;
    private PlaywriteButton breakpointButton;
    private CocoaCharacter character;
    private Icon characterView;
    private SubroutineScrap ruleView;
    private ScrollableArea ruleViewScroller;
    private PlaywriteView separatorButtonView;
    private RootView rootView;
    private PlaywriteButton opener;
    private VariableListView variableListView;
    private ScrollableArea variablesViewScroller;
    private PlaywriteButton stepRuleButton;
    private boolean stepRule = false;
    private boolean _allowClose = true;
    private boolean _variablesOpen = true;
    private boolean _dragging = false;
    private boolean _wasOffscreen = false;
    private int numNewSubs = 1;
    private static final int controlMargin = 5;
    private int controlAreaBottom = 0;
    private final int minimumRuleViewHeight = 70;
    private int minimumVariableAreaHeight;
    private int minRuleViewBottom;
    private Vector disableViews = new Vector(5);
    private boolean worldIsExecuting = false;
    
    static void initStatics() {
	commentTool = Tool.createTool("cwNCmt", "cwNCmtBtn");
	commentTool.setScrollerMappingEnabled(true);
	commentTool.setWarningResource("cwCWarn");
	pretestTool = Tool.createTool("cwPre", "cwPreBtn");
	pretestTool.setWarningResource("cwPWarn");
	disableTool = Tool.createTool("cwDis", "cwDisBtn");
	disableTool.setOptionClickEnabled(true);
	disableTool.setWarningResource("cwDWarn");
	breakpointTool = Tool.createTool("cwBreak", "cwBreakBtn");
	breakpointTool.setOptionClickEnabled(true);
	breakpointTool.setWarningResource("cwBpWarn");
    }
    
    static void setVariablesOpenState(boolean b) {
	_tutorialVarsOpen_ = b;
    }
    
    static void setTutorialOverride(boolean b) {
	_tutorialOverride_ = b;
    }
    
    CharacterWindow(CocoaCharacter character) {
	super(PlaywriteWindow.getRootView().width() - 300, 0, 300,
	      PlaywriteWindow.getRootView().height(), character.getWorld());
	init(character);
    }
    
    private void init(CocoaCharacter character) {
	this.character = character;
	if (character != null) {
	    characterView = new Icon(character);
	    characterView.setShowName(false);
	    characterView.setEventDelegate(-1, 0, 1, "SHI", this);
	    characterView.setEventDelegate(-3, 0, 1, "USHI", this);
	    this.getTitleBar().addSubviewLeft(characterView);
	    this.getTitleBar().setDirty(true);
	    Variable.systemVariable
		(CocoaCharacter.SYS_NAME_VARIABLE_ID, character)
		.addValueWatcher(character, this);
	    this.setTitle(character.getName());
	}
	int cw = this.contentSize().width;
	int ch = this.contentSize().height;
	opener = Util.createHorizHandle("cwTogV", this);
	String resourceID1 = "HandleV";
	String resourceID2 = "HandleH";
	opener.setImage(Resource.getAltButtonImage(resourceID1));
	opener.setAltImage(Resource.getAltButtonImage(resourceID2));
	opener.setHorizResizeInstruction(0);
	opener.setVertResizeInstruction(8);
	opener.setLoweredColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	opener.setRaisedColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	int sz = Math.max(opener.bounds.width, opener.bounds.height);
	opener.sizeTo(sz, sz);
	PlaywriteView separatorView
	    = new PlaywriteView(Resource.getImage("cwSeparator"));
	separatorView.setImageDisplayStyle(1);
	separatorView.sizeTo(cw, separatorView.height());
	separatorView.setHorizResizeInstruction(2);
	separatorView.setVertResizeInstruction(8);
	int resizeCursor;
	if (PlaywriteSystem.isMRJ_2_0())
	    resizeCursor = 12;
	else
	    resizeCursor = 8;
	separatorView.setCursor(resizeCursor);
	separatorView.setEventDelegate(-1, 0, 1, "sdb", this);
	separatorView.setEventDelegate(-3, 0, 1, "sde", this);
	separatorView.setEventDelegate(-2, 0, 1, "sd", this);
	separatorButtonView
	    = new PlaywriteView(0, ch / 2, cw,
				separatorView.height() + opener.height());
	separatorButtonView.setHorizResizeInstruction(2);
	separatorButtonView.setVertResizeInstruction(8);
	opener.moveTo(0, 0);
	separatorButtonView.addSubview(opener);
	separatorView.moveTo(0, opener.height());
	separatorButtonView.addSubview(separatorView);
	separatorButtonView.layoutView(0, 0);
	separatorButtonView
	    .setBackgroundColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	separatorView.layoutView(0, 0);
	this.addSubview(separatorButtonView);
	PlaywriteButton disableRuleButton
	    = new ToolButton(disableTool, "DISABLERULE", this);
	disableRuleButton.moveTo(cw - disableRuleButton.width() - 5, 5);
	disableRuleButton.setHorizResizeInstruction(1);
	disableRuleButton.setVertResizeInstruction(4);
	disableRuleButton.setToolTipText(Resource.getToolTip("cwDis"));
	this.addSubview(disableRuleButton);
	disableViews.addElement(disableRuleButton);
	breakpointButton = new ToolButton(breakpointTool, "BREAKPOINT", this);
	breakpointButton
	    .moveTo(disableRuleButton.x() - breakpointButton.width() - 5, 5);
	breakpointButton.setHorizResizeInstruction(1);
	breakpointButton.setVertResizeInstruction(4);
	breakpointButton.setToolTipText(Resource.getToolTip("cwBreak"));
	this.addSubview(breakpointButton);
	if (character instanceof CharacterPrototype)
	    breakpointButton.setEnabled(false);
	controlAreaBottom = breakpointButton.bounds.maxY();
	stepRuleButton = PlaywriteButton.createFromResource("cwStepBtn",
							    "STEPRULE", this);
	stepRuleButton
	    .moveTo(breakpointButton.x() - stepRuleButton.width() - 5, 5);
	stepRuleButton.setHorizResizeInstruction(0);
	stepRuleButton.setVertResizeInstruction(4);
	newSubButton = PlaywriteButton.createFromResource("cwNewSubBtn",
							  "NEWSUB", this);
	newSubButton.moveTo(5, 5 + (disableRuleButton.height()
				    - newSubButton.height()) / 2);
	newSubButton.setHorizResizeInstruction(0);
	newSubButton.setVertResizeInstruction(4);
	this.addSubview(newSubButton);
	disableViews.addElement(newSubButton);
	PlaywriteButton newCommentButton
	    = new ToolButton(commentTool, "NEWCOMMENT", this);
	newCommentButton.moveTo(newSubButton.bounds.maxX() + 5, 5);
	newCommentButton.setHorizResizeInstruction(0);
	newCommentButton.setVertResizeInstruction(4);
	newCommentButton.setToolTipText(Resource.getToolTip("cwNCmt"));
	this.addSubview(newCommentButton);
	disableViews.addElement(newCommentButton);
	newPretestButton = new ToolButton(pretestTool, "NEWPRETEST", this);
	newPretestButton.moveTo(newCommentButton.bounds.maxX() + 5, 5);
	newPretestButton.setHorizResizeInstruction(0);
	newPretestButton.setVertResizeInstruction(4);
	newPretestButton.setToolTipText(Resource.getToolTip("cwPre"));
	this.addSubview(newPretestButton);
	if (character instanceof CharacterPrototype)
	    newPretestButton.setEnabled(false);
	else
	    disableViews.addElement(newPretestButton);
	int w = cw * 2;
	int h = ch * 2;
	Debug.print("debug.character.window", "generating ruleViews for ",
		    character);
	ruleView = ((SubroutineScrap)
		    character.getMainSubroutine().createScrap(character));
	Debug.print("debug.character.window",
		    "finished generating ruleViews for ", character);
	this.contentView().setBuffered(true);
	ruleViewScroller
	    = new ScrollableArea(w / 2, h / 2 - separatorButtonView.height(),
				 ruleView, true, true);
	ruleViewScroller.setHorizontalScrollAmount(50);
	ruleViewScroller.setVerticalScrollAmount(50);
	ruleViewScroller.moveTo(0, controlAreaBottom);
	this.addSubview(ruleViewScroller);
	variablesViewScroller
	    = new ScrollableArea(w / 2, h / 2 - separatorButtonView.height(),
				 null, true, true);
	variablesViewScroller.setHorizontalScrollAmount(25);
	variablesViewScroller.setVerticalScrollAmount(25);
	variablesViewScroller.setAllowSmallContentView(false);
	newVariableButton = VariableListView.newVariableTool.makeButton();
	newVariableButton.moveTo(5, 5);
	newVariableButton.setHorizResizeInstruction(0);
	newVariableButton.setVertResizeInstruction(4);
	newVariableButton.setEnabled(true);
	this.addSubview(newVariableButton);
	this.addSubview(variablesViewScroller);
	variablesViewScroller.moveTo(0, ch);
	int vaHeight = character.getPrototype().getVariablesAreaHeight();
	if (_tutorialOverride_)
	    _variablesOpen = _tutorialVarsOpen_;
	else
	    _variablesOpen = vaHeight != 0;
	if (_variablesOpen)
	    moveSeparatorTo(this.contentSize().height - vaHeight, false);
	else
	    performCommand("cwHideV", opener);
	this.contentView().layoutView(0, 0);
	layoutParts();
	minimumVariableAreaHeight
	    = newVariableButton.height() + separatorView.height() + 30;
	changeWindowColor(this.getWorld().getColor());
	int minWidth = (this.width() - breakpointButton.x()
			+ stepRuleButton.width() + newPretestButton.x() + 20);
	int minHeight = (separatorView.height() + this.getTitleBar().height()
			 + controlAreaBottom + 70);
	this.setMinSize(minWidth, minHeight);
	minRuleViewBottom = ruleViewScroller.y() + 70;
	show();
	moveSeparatorTo((this.contentSize().height
			 - character.getPrototype().getVariablesAreaHeight()),
			false);
	ruleView.createContentsView();
    }
    
    public boolean mouseDown(MouseEvent event) {
	_wasOffscreen
	    = this.rootView().bounds().contains(this.bounds()) ^ true;
	return super.mouseDown(event);
    }
    
    public void mouseDragged(MouseEvent event) {
	super.mouseDragged(event);
	_dragging = true;
    }
    
    public void mouseUp(MouseEvent event) {
	super.mouseUp(event);
	if (_dragging && _wasOffscreen)
	    this.setDirty(true);
	_dragging = false;
	_wasOffscreen = false;
    }
    
    void changeWindowColor(Color color) {
	Color lightColor = color;
	ruleViewScroller.changeWindowColor(color, lightColor);
	variablesViewScroller.changeWindowColor(color, lightColor);
	this.contentView().setBackgroundColor(color);
	super.changeWindowColor(color, lightColor);
    }
    
    private VariableListView createVariableListView() {
	if (variableListView == null) {
	    variableListView
		= new VariableListView(character, this.contentSize().width,
				       100);
	    variableListView.sizeToMinSize();
	    variablesViewScroller.getScrollView()
		.setContentView(variableListView);
	}
	return variableListView;
    }
    
    public void setVariableEditorEnabled(Variable v, boolean enabled) {
	createVariableListView().setVariableEditorEnabled(v, enabled);
    }
    
    final CocoaCharacter getCharacter() {
	return character;
    }
    
    final ScrollableArea getRuleScroller() {
	return ruleViewScroller;
    }
    
    final boolean worldIsExecuting() {
	return worldIsExecuting;
    }
    
    public final ScrollableArea getMainScroller() {
	return ruleViewScroller;
    }
    
    void lockContentView() {
	ruleViewScroller.disableDrawing();
    }
    
    void unlockContentView() {
	ruleViewScroller.reenableDrawing();
    }
    
    void setAllowClose(boolean allowClose) {
	_allowClose = allowClose;
    }
    
    boolean hasStepRuleButton() {
	return stepRule;
    }
    
    void showStepRuleButton(Slot slot) {
	if (stepRuleButton != null) {
	    hideStepRuleButton();
	    steppingWindow = this;
	    stepRuleButton.setTarget(slot);
	    stepRuleButton
		.moveTo(breakpointButton.x() - stepRuleButton.width() - 5, 5);
	    stepRuleButton.setState(false);
	    this.addSubview(stepRuleButton);
	    stepRule = true;
	    stepRuleButton.setDirty(true);
	}
    }
    
    void changeStepButtonToPlay() {
	PlaywriteRoot.app().performCommandAndWait(this, "chgstptply", null);
    }
    
    void resetStepButtonAndHide() {
	PlaywriteRoot.app().performCommandAndWait(this, "resetstep", null);
    }
    
    static void hideStepRuleButton() {
	if (steppingWindow != null) {
	    steppingWindow._hideStepRuleButton();
	    steppingWindow = null;
	}
    }
    
    private void _hideStepRuleButton() {
	if (stepRuleButton != null) {
	    stepRuleButton.removeFromSuperview();
	    stepRuleButton.setState(false);
	    this.contentView().addDirtyRect(stepRuleButton.bounds());
	    stepRuleButton.setTarget(null);
	    stepRule = false;
	}
    }
    
    public void layoutParts() {
	super.layoutParts();
	if (ruleViewScroller != null) {
	    int ch = this.contentSize().height;
	    int cw = this.contentSize().width;
	    if (_variablesOpen)
		moveSeparatorTo(separatorButtonView.y(), false);
	    int bottom = separatorButtonView.bounds.maxY();
	    ruleViewScroller
		.sizeTo(cw, separatorButtonView.y() - ruleViewScroller.y());
	    newVariableButton.moveTo(newVariableButton.x(), bottom + 5);
	    bottom = newVariableButton.bounds.maxY();
	    int maxbottom = bottom + newVariableButton.height();
	    variablesViewScroller.setBounds(0, bottom, cw, ch - bottom);
	    this.contentView().addDirtyRect(null);
	}
    }
    
    public void subviewDidMove(View view) {
	if (view == separatorButtonView)
	    layoutParts();
    }
    
    public void destroyWindow() {
	Debug.print("debug.character.window", "Destroying ", this);
	if (steppingWindow == this) {
	    steppingWindow = null;
	    Application.application().performCommandLater(this.getWorld(),
							  World.CLEAR_DEBUG,
							  null, true);
	}
	PlaywriteView.garbageCount = 0;
	characterView.discard();
	Variable.systemVariable
	    (CocoaCharacter.SYS_NAME_VARIABLE_ID, character)
	    .removeValueWatcher(character, this);
	super.destroyWindow();
	character.setEditor(null);
	if (_lastCW_ == this)
	    _lastCW_ = null;
	newVariableButton = null;
	newSubButton = null;
	newPretestButton = null;
	breakpointButton = null;
	character = null;
	characterView = null;
	ruleView = null;
	ruleViewScroller = null;
	separatorButtonView = null;
	rootView = null;
	opener = null;
	variableListView = null;
	variablesViewScroller = null;
	stepRuleButton = null;
	if (disableViews != null)
	    disableViews.removeAllElements();
	disableViews = null;
    }
    
    public void update(Object target, Object value) {
	if (target instanceof Variable) {
	    Variable watchVar = (Variable) target;
	    if (watchVar.isSystemType(CocoaCharacter.SYS_NAME_VARIABLE_ID))
		this.setTitle(character.getName());
	    else
		throw new PlaywriteInternalError
			  ("Unexpected update in CharacterWindow on "
			   + watchVar);
	} else
	    throw new PlaywriteInternalError
		      ("Unexpected update in CharacterWindow on " + target);
    }
    
    public void worldStateChanged(Object target, Object oldState,
				  Object transition, Object newState) {
	if (target instanceof World) {
	    World world = (World) target;
	    worldIsExecuting
		= world.isSuspendedForDebug() || world.isRunning();
	    setEnabledOnButtons(newState == World.STOPPED);
	    boolean recordingOrEditing
		= newState == World.RECORDING || newState == World.EDITING;
	    if (!(getCharacter() instanceof CharacterPrototype))
		breakpointButton.setEnabled(recordingOrEditing ^ true);
	    if (recordingOrEditing)
		ruleViewScroller.disable();
	    else
		ruleViewScroller.enable();
	    if (newState == World.RUNNING && world.timeIsBackward()
		&& character instanceof CharacterInstance)
		resetDebuggingLights();
	}
	super.worldStateChanged(target, oldState, transition, newState);
    }
    
    void resetDebuggingLights() {
	if (ruleView != null)
	    ruleView.resetSubroutineLights();
    }
    
    public View sourceView(ToolSession session) {
	return this;
    }
    
    public void toolWasAccepted(ToolSession session) {
	if (session.isShiftKeyDown())
	    session.resetSession();
    }
    
    public void toolWasRejected(ToolSession session) {
	if (session.isShiftKeyDown())
	    session.resetSession();
    }
    
    public void sessionEnded(ToolSession session) {
	/* empty */
    }
    
    private int getSeparatorMaxY() {
	return this.contentSize().height - separatorButtonView.height();
    }
    
    private void moveSeparatorTo(int newY, boolean userOpening) {
	if (newY < minRuleViewBottom)
	    newY = minRuleViewBottom;
	if (newY != separatorButtonView.y()) {
	    int newVarAreaHeight = this.contentSize().height - newY;
	    if (newVarAreaHeight > minimumVariableAreaHeight || userOpening) {
		if (variableListView == null)
		    createVariableListView();
		opener.setToolTipText(Resource.getToolTip("ALT cwTogV"));
		opener.setState(true);
		_variablesOpen = true;
		separatorButtonView.moveTo(0, newY);
		if (userOpening)
		    character.getPrototype().setVariablesAreaHeight
			(this.contentSize().height - newY);
	    } else {
		this.disableDrawing();
		int sepYWhenAtBottom = getSeparatorMaxY();
		separatorButtonView.moveTo(0, sepYWhenAtBottom);
		variablesViewScroller.moveTo(0, this.contentSize().height);
		opener.setToolTipText(Resource.getToolTip("cwTogV"));
		opener.setState(false);
		this.reenableDrawing();
		if (variableListView != null) {
		    variablesViewScroller.getScrollView().setContentView(null);
		    variableListView.discard();
		    variableListView = null;
		}
		_variablesOpen = false;
	    }
	}
    }
    
    public void performCommand(String command, Object arg) {
	if (command == "cwShowV") {
	    createVariableListView();
	    int sepYWhenAtBottom = getSeparatorMaxY();
	    int minSepY = sepYWhenAtBottom / 3;
	    int maxSepY = (sepYWhenAtBottom - variableListView.height()
			   - ScrollableArea.SCROLL_ARROW_WIDTH * 2
			   - newVariableButton.height() - 5);
	    moveSeparatorTo(maxSepY > minSepY ? maxSepY : minSepY, false);
	} else if (command == "cwHideV")
	    moveSeparatorTo(this.contentSize().height, false);
	else if (command == "cwTogV") {
	    if (_variablesOpen) {
		performCommand("cwHideV", arg);
		character.getPrototype().setVariablesAreaHeight(0);
	    } else {
		performCommand("cwShowV", arg);
		if (_variablesOpen == false)
		    PlaywriteDialog.warning(Resource.getText("cwNoShoVars"),
					    true);
		else
		    character.getPrototype().setVariablesAreaHeight
			(this.contentSize().height - separatorButtonView.y());
	    }
	} else if (command == "sdb")
	    rootView = this.rootView();
	else if (command == "sde") {
	    rootView = null;
	    if (this.contentSize().height - separatorButtonView.y()
		< minimumVariableAreaHeight)
		moveSeparatorTo(this.contentSize().height, false);
	    if (_variablesOpen == false)
		character.getPrototype().setVariablesAreaHeight(0);
	} else if (command == "sd" && rootView != null) {
	    Point point = rootView.mousePoint();
	    point = rootView.convertPointToView(this.contentView(), point);
	    if (point.y < getSeparatorMaxY())
		moveSeparatorTo(point.y, true);
	} else if (command == "SHI")
	    character.halo();
	else if (command == "USHI")
	    character.unhalo();
	else if (command != "OUTLINE") {
	    if (command == "NEWSUB") {
		Subroutine newSub = Subroutine.createNormalSubroutine();
		newSub.setName(Resource.getTextAndFormat
			       ("cwNSN",
				new Object[] { new Integer(numNewSubs++) }));
		character.addAtFront(newSub);
		ruleViewScroller.scrollTo(0, 0);
	    } else if (command == "DISABLERULE")
		disableTool.newSession(this, 0, 0, "DISABLERULE");
	    else if (command == "NEWPRETEST")
		pretestTool.newSession(this, 0, 0, "NEWPRETEST");
	    else if (command == "NEWCOMMENT")
		commentTool.newSession(this, 0, 0, "NEWCOMMENT");
	    else if (command == "BREAKPOINT")
		breakpointTool.newSession(this, 0, 0, "BREAKPOINT");
	    else if (command == "show step button")
		showStepRuleButton((Slot) arg);
	    else if (command.equals(PlaywriteWindow.CLOSE) && !_allowClose) {
		this.hide();
		Application.application().performCommandLater(this,
							      (PlaywriteWindow
							       .CLOSE),
							      null, true);
	    } else if (command == "chgstptply") {
		if (stepRuleButton != null) {
		    COM.stagecast.ifc.netscape.application.Image image;
		    COM.stagecast.ifc.netscape.application.Image altImage;
		    String toolTip;
		    if (this.getWorld().isStepping()) {
			image = Resource.getButtonImage("CP step forward");
			altImage
			    = Resource.getAltButtonImage("CP step forward");
			toolTip = Resource.getToolTip("CP step forward");
		    } else {
			image = Resource.getButtonImage("CP Play");
			altImage = Resource.getAltButtonImage("CP Play");
			toolTip = Resource.getToolTip("CP Play");
		    }
		    this.contentView().addDirtyRect(stepRuleButton.bounds());
		    stepRuleButton.setImage(image);
		    stepRuleButton.setAltImage(altImage);
		    stepRuleButton.setToolTipText(toolTip);
		    stepRuleButton.sizeToMinSize();
		    stepRuleButton.draw();
		}
	    } else if (command == "resetstep") {
		if (stepRuleButton != null) {
		    hideStepRuleButton();
		    stepRuleButton
			.setImage(Resource.getButtonImage("cwStepBtn"));
		    stepRuleButton
			.setAltImage(Resource.getAltButtonImage("cwStepBtn"));
		    stepRuleButton
			.setToolTipText(Resource.getToolTip("cwStepBtn"));
		    stepRuleButton.sizeToMinSize();
		}
	    } else
		super.performCommand(command, arg);
	}
    }
    
    private void setEnabledOnButtons(boolean enabled) {
	int i = disableViews.size();
	while (i-- > 0) {
	    View view = (View) disableViews.elementAt(i);
	    if (view instanceof PlaywriteView) {
		if (enabled)
		    ((PlaywriteView) view).enable();
		else
		    ((PlaywriteView) view).disable();
	    } else if (view instanceof PlaywriteButton)
		((PlaywriteButton) view).setEnabled(enabled);
	}
    }
    
    void disable() {
	setEnabledOnButtons(false);
	ruleViewScroller.disable();
	if (isInSpotlight(character)) {
	    variablesViewScroller.enable();
	    newVariableButton.setEnabled(true);
	} else {
	    variablesViewScroller.disable();
	    newVariableButton.setEnabled(false);
	}
    }
    
    void enable() {
	setEnabledOnButtons(true);
	ruleViewScroller.enable();
	variablesViewScroller.enable();
	newVariableButton.setEnabled(true);
    }
    
    private boolean isInSpotlight(CocoaCharacter ch) {
	return RuleEditor.isInSpotlight(ch);
    }
    
    boolean boundify() {
	if (Tutorial.getTutorial() != null) {
	    Rect r = Tutorial.getTutorial().getWindowBounds("CharacterWindow");
	    if (r != null) {
		this.setBounds(r);
		return true;
	    }
	}
	boolean worked = super.boundify();
	if (worked) {
	    World world = this.getWorld();
	    PlaywriteWindow worldWindow = world.getWindow();
	    Rect rv = PlaywriteRoot.getMainRootViewBounds();
	    this.moveTo(worldWindow.bounds.maxX() + 2, worldWindow.bounds.y);
	    if (_lastCW_ != null && _lastCW_ != this) {
		int titleBarHeight
		    = _lastCW_.getTitleBar().getTitleLabel().bounds.maxY() + 5;
		int x = _lastCW_.bounds.x + titleBarHeight;
		int y = _lastCW_.bounds.y + titleBarHeight;
		int w = rv.maxX() - x;
		int h = rv.maxY() - y;
		if (w > 300)
		    w = 300;
		Size minSize = this.minSize();
		if (w > minSize.width && h > minSize.height
		    && x + w > _lastCW_.bounds.maxX())
		    this.setBounds(x, y, w, h);
	    }
	    super.boundify();
	    WorldView worldView = world.getWorldView();
	    if (worldView != null)
		this.resizeToAvoidView(worldView.getControlPanelScroller(),
				       10);
	    if (this.width() < 300)
		_lastCW_ = null;
	    else
		_lastCW_ = this;
	}
	return worked;
    }
    
    public void show() {
	super.show();
	if (RuleEditor.isRecordingOrEditing())
	    disable();
    }
}
