/* RuleEditor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.ContainerView;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFieldOwner;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.application.Window;
import COM.stagecast.ifc.netscape.application.WindowOwner;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class RuleEditor extends PlaywriteWindow
    implements Debug.Constants, ResourceIDs.BeforeBoardIDs,
	       ResourceIDs.CommandIDs, ResourceIDs.DialogIDs,
	       ResourceIDs.RuleEditorIDs, ToolSource, TextFieldOwner,
	       WindowOwner, RuleEditor.State, StateWatcher
{
    static final int margin = 10;
    static final int twoXmargin = 20;
    static final int topMargin = 40;
    static final Size ruleViewSize = new Size(375, 144);
    static final Size mainViewSize
	= new Size(ruleViewSize.width, ruleViewSize.height + 40 + 10);
    static final String RESET_RECORDING = "RESET_RECORDING";
    static final int INNER_WINDOW_BRIGHTNESS = 112;
    static final int LIGHT_LINE_BRIGHTNESS = 179;
    static final int DARKER_LINE_BRIGHTNESS = 64;
    static final int DARK_LINE_BRIGHTNESS = 30;
    static final Color INNER_WINDOW_COLOR = new Color(112, 112, 112);
    static final Color LIGHT_LINE_COLOR = new Color(179, 179, 179);
    static final Color DARKER_LINE_COLOR = new Color(64, 64, 64);
    static final Color DARK_LINE_COLOR = new Color(30, 30, 30);
    static Tool examineTool;
    static Tool variableWindowTool;
    static Tool dontCareTool;
    static Tool mouseClickTool;
    private static boolean recordMode = false;
    private static boolean ruleEditingMode = false;
    private static int recordTime = 0;
    private static RuleEditor ruleEditor = null;
    private static CharacterInstance selfCharacter = null;
    private static Stage originalSelfContainer = null;
    private static int originalSelfStageIndex = -1;
    private static Point originalSelfLocation = null;
    private static Vector testPalette = new Vector(5);
    private static Vector actionPalette = new Vector(5);
    private static Vector controlViewPalette = new Vector(5);
    private static Subroutine subroutine = null;
    private Point tempPoint = new Point();
    private CocoaCharacter character;
    private Rule rule = null;
    private Rule originalCopy = null;
    private PlaywriteButton doneButton;
    private PlaywriteButton cancelButton;
    private Icon characterView;
    private PlaywriteView testToolsView;
    private PlaywriteView actionToolsView;
    private PlaywriteView currentPalette;
    private ScrollableArea toolPaletteScroller;
    private ContainerView mainView = null;
    private PlaywriteView controlView;
    private static final int SPACE_BETWEEN_CONTROLS = 5;
    private PlaywriteButton editButton;
    private PlaywriteButton testButton;
    private PlaywriteButton examineButton;
    private PlaywriteButton showVariablesButton;
    private PlaywriteButton calculatorButton;
    private PlaywriteButton showActionsButton;
    private PlaywriteTextField nameField = null;
    private PlaywriteLabel nameLabel = null;
    private RuleContentView ruleContentView = null;
    private BoardView beforeBoardView = null;
    private BoardView afterBoardView = null;
    private PlaywriteWindow dialog;
    private ExamineWindow examineWindow;
    private Vector variableWindowList = new Vector(10);
    private Vector dependantWindowList = new Vector(10);
    private int handleHeight;
    private Size toolSize = new Size(50, 80);
    private boolean _legalClose = false;
    private boolean _resetRecording = false;
    private Hashtable _extensions = new Hashtable(10);
    private static Vector _exHackQueue = new Vector(10);
    private StateMachine _state;
    
    public static interface State
    {
	public static final Object CLOSED_STATE = "closed";
	public static final Object VIEWING_STATE = "viewing";
	public static final Object RECORDING_STATE = "recording";
	public static final Object EDITING_STATE = "editing";
	public static final Object SET_RULE = "set rule";
	public static final Object TEST_RULE = "test rule";
	public static final Object EDIT_RULE = "edit rule";
	public static final Object NEW_RULE = "new rule";
	public static final Object CLOSE_EDITOR = "close editor";
    }
    
    private class ExamineWindow extends PlaywriteWindow
	implements ResourceIDs.ExamineWindowIDs
    {
	private Bitmap xBitmap;
	private Bitmap checkBitmap;
	private Bitmap ruleBitmap;
	private Label dontCare;
	private Label offStage;
	private PlaywriteButton closeButton;
	private BindTest bindTest;
	private Stage stage;
	private Size xCheckSize;
	private PackConstraints itemConstraints;
	private PackConstraints windowConstraints;
	private PlaywriteView legendView;
	private final Size bindSize = new Size(64, 50);
	
	ExamineWindow(World world) {
	    super(50, 50, 300, 100, world);
	    xBitmap = Resource.getImage("EWi x");
	    checkBitmap = Resource.getImage("EWi chk");
	    xCheckSize = new Size();
	    xCheckSize.width = (xBitmap.width() > checkBitmap.width()
				? xBitmap.width() : checkBitmap.width());
	    xCheckSize.height = (xBitmap.height() > checkBitmap.height()
				 ? xBitmap.height() : checkBitmap.height());
	    dontCare = Util.makeLabel(Resource.getText("EW dc"));
	    offStage = Util.makeLabel(Resource.getText("EW off"));
	    this.setTitle(Resource.getText("EW title"));
	    this.contentView()
		.setBackgroundColor(this.getWorld().getLightColor());
	    itemConstraints = new PackConstraints();
	    itemConstraints.setAnchor(6);
	    itemConstraints.setSide(2);
	    itemConstraints.setFillY(true);
	    windowConstraints = new PackConstraints();
	    windowConstraints.setAnchor(6);
	    windowConstraints.setSide(1);
	    legendView = createLegendView();
	}
	
	void changeWindowColor(Color color) {
	    super.changeWindowColor(color, color.lighterColor());
	}
	
	void removeSubviews() {
	    Vector viewList = this.contentView().subviews();
	    int nsubviews = viewList.size();
	    View[] subview = new View[nsubviews];
	    viewList.copyInto(subview);
	    for (int i = 0; i < nsubviews; i++)
		subview[i].removeFromSuperview();
	}
	
	PlaywriteView createBindingView(View bindingIcon, View xOrCheck,
					View GCicon) {
	    Size GCiconSize = bindSize;
	    if (bindingIcon instanceof Icon)
		((Icon) bindingIcon).setComputeMinSize(false);
	    if (xOrCheck instanceof Icon)
		((Icon) xOrCheck).setComputeMinSize(false);
	    if (GCicon instanceof Icon)
		((Icon) GCicon).setComputeMinSize(false);
	    bindingIcon.setMinSize(bindSize.width, bindSize.height);
	    GCicon.setMinSize(GCiconSize.width, GCiconSize.height);
	    xOrCheck.setMinSize(xCheckSize.width, xCheckSize.height);
	    bindingIcon.sizeToMinSize();
	    GCicon.sizeToMinSize();
	    xOrCheck.sizeToMinSize();
	    PlaywriteView bindingView = new WideView();
	    bindingView.setTransparent(true);
	    PackLayout packLayout = new PackLayout();
	    packLayout.setDefaultConstraints(itemConstraints);
	    bindingView.setLayoutManager(packLayout);
	    bindingView.addSubview(bindingIcon);
	    bindingView.layoutView(0, 0);
	    bindingView.addSubview(xOrCheck);
	    bindingView.layoutView(0, 0);
	    bindingView.addSubview(GCicon);
	    bindingView.layoutView(0, 0);
	    return bindingView;
	}
	
	private boolean bind(GeneralizedCharacter gc, Vector square) {
	    for (int i = 0; i < square.size(); i++) {
		CharacterInstance character
		    = (CharacterInstance) square.elementAt(i);
		if (gc.bind(character)) {
		    square.removeElementAt(i);
		    return true;
		}
	    }
	    return false;
	}
	
	private PlaywriteView createDontCareIcon() {
	    return new Icon(Resource.getImage("dcsTool"), null,
			    Resource.getText("EW dc"));
	}
	
	private PlaywriteView createBlankView() {
	    PlaywriteView view = new PlaywriteView();
	    view.setTransparent(true);
	    return view;
	}
	
	private PlaywriteView createLegendView() {
	    Font font = Font.fontNamed("serif", 1, 12);
	    PlaywriteLabel stageLabel
		= new PlaywriteLabel(Resource.getText("EW stage"), font,
				     Util.ruleColor);
	    PlaywriteLabel ruleLabel
		= new PlaywriteLabel(Resource.getText("EW rule"), font,
				     Util.ruleColor);
	    stageLabel.setUnderlined(true);
	    ruleLabel.setUnderlined(true);
	    int widest = Math.max(stageLabel.width(), ruleLabel.width());
	    widest = Math.max(widest, bindSize.width);
	    bindSize.width = widest;
	    return createBindingView(stageLabel, createBlankView(), ruleLabel);
	}
	
	void setBindTest(BindTest bindTest) {
	    int margin = 10;
	    int y = 0;
	    if (this.bindTest != null)
		removeSubviews();
	    this.bindTest = bindTest;
	    CharacterInstance self = (CharacterInstance) character;
	    Vector square
		= self.adjacentSquare(bindTest.getDx(), bindTest.getDy());
	    if (square == null) {
		this.addSubview(offStage);
		this.contentView().sizeTo(offStage.width() + 2 * margin,
					  offStage.height() + 2 * margin);
		Util.centerView(offStage);
		Size windowSize
		    = this.windowSizeForContentSize(this.contentView().width(),
						    this.contentView()
							.height());
		this.sizeTo(windowSize.width, windowSize.height);
		this.contentView().layoutView(0, 0);
	    } else {
		square = (Vector) square.clone();
		PlaywriteView mainView = new TallView();
		PackLayout packLayout = new PackLayout();
		mainView.setTransparent(true);
		packLayout.setDefaultConstraints(windowConstraints);
		mainView.setLayoutManager(packLayout);
		int i = square.size();
		while (i-- > 0) {
		    CharacterInstance instance
			= (CharacterInstance) square.elementAt(i);
		    if (instance.isInvisible())
			square.removeElementAt(i);
		}
		int gcSize = bindTest.getGcSize();
		Vector unboundGCs = new Vector(5);
		for (int i_0_ = 0; i_0_ < bindTest.getGcSize(); i_0_++) {
		    GeneralizedCharacter gc = bindTest.getGC(i_0_);
		    CharacterInstance instance = gc.getBinding();
		    if (instance == null) {
			if (gc == bindTest.getSelfifSelf()) {
			    if (!bind(gc, square) || gc.getBinding() != self)
				throw new PlaywriteInternalError
					  ("RuleEditor:Examine: couldn't bind self");
			} else
			    unboundGCs.addElement(gc);
		    } else if (square.containsIdentical(instance))
			square.removeElementIdentical(instance);
		    else
			gc.unbind();
		}
		boolean success = true;
		for (int i_1_ = 0; i_1_ < unboundGCs.size(); i_1_++) {
		    GeneralizedCharacter gc
			= (GeneralizedCharacter) unboundGCs.elementAt(i_1_);
		    if (gc.getBinding() == null && !bind(gc, square))
			success = false;
		}
		if (bindTest.getMatchExactly()
		    && (unboundGCs.size() != 0 || square.size() > 0))
		    success = false;
		for (int i_2_ = 0; i_2_ < gcSize; i_2_++) {
		    GeneralizedCharacter gc = bindTest.getGC(i_2_);
		    PlaywriteView gcIcon = gc.createIcon();
		    CharacterInstance instance = gc.getBinding();
		    PlaywriteView didMatchView;
		    PlaywriteView bindingIcon;
		    if (instance == null) {
			didMatchView = new PlaywriteView(xBitmap);
			bindingIcon = createBlankView();
		    } else {
			didMatchView = new PlaywriteView(checkBitmap);
			bindingIcon = instance.createIcon();
		    }
		    mainView.addSubview(createBindingView(bindingIcon,
							  didMatchView,
							  gcIcon));
		    this.layoutView(0, 0);
		}
		if (square.size() == 0 && bindTest.getDontCare()) {
		    PlaywriteView gcIcon = createDontCareIcon();
		    PlaywriteView unboundCharIcon = createBlankView();
		    PlaywriteView didMatchView = createBlankView();
		    mainView.addSubview(createBindingView(unboundCharIcon,
							  didMatchView,
							  gcIcon));
		    mainView.layoutView(0, 0);
		} else {
		    for (int i_3_ = 0; i_3_ < square.size(); i_3_++) {
			CharacterInstance instance
			    = (CharacterInstance) square.elementAt(i_3_);
			PlaywriteView unboundCharIcon = instance.createIcon();
			PlaywriteView gcIcon;
			PlaywriteView didMatchView;
			if (bindTest.getDontCare()) {
			    gcIcon = (i_3_ == 0 ? createDontCareIcon()
				      : createBlankView());
			    didMatchView = new PlaywriteView(checkBitmap);
			} else {
			    gcIcon = createBlankView();
			    didMatchView = new PlaywriteView(xBitmap);
			}
			mainView.addSubview(createBindingView(unboundCharIcon,
							      didMatchView,
							      gcIcon));
			mainView.layoutView(0, 0);
		    }
		}
		mainView.addSubview(legendView);
		mainView.layoutView(0, 0);
		this.contentView().sizeTo(mainView.width(), mainView.height());
		this.contentView().addSubview(mainView);
		Size windowSize
		    = this.windowSizeForContentSize(this.contentView().width(),
						    this.contentView()
							.height());
		this.sizeTo(windowSize.width, windowSize.height);
	    }
	}
	
	public void destroyWindow() {
	    removeSubviews();
	    bindTest = null;
	}
    }
    
    static void initStatics() {
	dontCareTool = Tool.createTool("RE dc", "RE dcb");
	dontCareTool.setDragEnabled(true);
	dontCareTool.setWarningResource("RE dc warn");
	mouseClickTool = Tool.createTool("RE mt", "RE mtb");
	mouseClickTool.setDragEnabled(true);
	mouseClickTool.setWarningResource("RE mt warn");
	examineTool = Tool.createTool("RE e", "RE eb");
	examineTool.setWarningResource("RE e warn");
	variableWindowTool = Tool.createTool("RE sv", "RE svb");
	variableWindowTool.setWarningResource("PE sv warn");
    }
    
    static void initExtensions() {
	if (dontCareTool == null)
	    initStatics();
	addToTestPalette(dontCareTool);
	addToTestPalette(mouseClickTool);
	addToActionPalette(Tool.copyLoadTool);
	addToActionPalette(Tool.deleteTool);
    }
    
    RuleEditor(CharacterInstance character) {
	this(new Rule(character), character);
    }
    
    RuleEditor(Rule rule, CocoaCharacter character) {
	super(PlaywriteRoot.getMainRootView().width() - (ruleViewSize.width
							 + 10),
	      0, ruleViewSize.width, ruleViewSize.height,
	      character.getWorld());
	this.setOwner(null);
	setWorld(rule, character);
	setRuleEditor(this);
	_state = new StateMachine("closed", this);
	_state.addTransitions("closed",
			      new Object[] { "set rule", "new rule" },
			      new Object[] { "viewing", "recording" });
	_state.addTransitions("viewing",
			      new Object[] { "set rule", "test rule",
					     "edit rule", "close editor",
					     "new rule" },
			      new Object[] { "viewing", "viewing", "editing",
					     "closed", "recording" });
	_state.addTransitions("recording", new Object[] { "close editor" },
			      new Object[] { "closed" });
	_state.addTransitions("editing", new Object[] { "close editor" },
			      new Object[] { "closed" });
	this.setTitle(Resource.getText("RE title"));
	cancelButton
	    = new PlaywriteButton(Resource.getButtonImage("command c w. text"),
				  Resource
				      .getAltButtonImage("command c w. text"));
	cancelButton.setCommand("command c");
	cancelButton.setTarget(this);
	cancelButton.setRaisedColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	cancelButton.setLoweredColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	cancelButton.setImagePosition(0);
	cancelButton.setBordered(false);
	cancelButton.setToolTipText(Resource.getText("TT cancel"));
	this.getTitleBar().addSubviewLeft(cancelButton);
	mainView = new ContainerView(10, 0, mainViewSize.width,
				     mainViewSize.height);
	mainView.setBorder(null);
	Size newSize = this.windowSizeForContentSize(mainViewSize.width + 20,
						     mainViewSize.height);
	this.sizeTo(newSize.width, newSize.height);
	this.addSubview(mainView);
	mainView.setHorizResizeInstruction(2);
	mainView.setVertResizeInstruction(16);
	showActionsButton = Util.createHorizHandle(null, null);
	showActionsButton.setToolTipText(Resource.getToolTip("RE sa"));
	showActionsButton.setState(true);
	handleHeight = showActionsButton.height();
	PlaywriteButton showTestsButton = Util.createHorizHandle(null, null);
	showTestsButton.setToolTipText(Resource.getToolTip("RE st"));
	showTestsButton.setState(true);
	showTestsButton.moveTo(mainView.width() / 6, mainView.height() - 10);
	showActionsButton.moveTo((mainView.width() - showTestsButton.bounds.x
				  - showActionsButton.width()),
				 showTestsButton.bounds.y);
	testToolsView
	    = new TallView(10, 10, toolSize.width + 2, toolSize.height);
	testToolsView.setLayoutManager(new PackLayout());
	((PackLayout) testToolsView.layoutManager()).setDefaultConstraints
	    (new PackConstraints(8, false, false, false, 0, 0, 0, 2, 0));
	populatePalette(testPalette, testToolsView);
	toolPaletteScroller
	    = new ScrollableArea(10, 10, toolSize.width, toolSize.height,
				 testToolsView, false, true, false);
	toolPaletteScroller.setVertResizeInstruction(16);
	addPaletteButton(KeyTest.class, "RE kt");
	addPaletteButton(BooleanTest.class, "RE nt");
	actionToolsView
	    = new TallView(10, 10, toolSize.width + 2, toolSize.height);
	actionToolsView.setLayoutManager(new PackLayout());
	((PackLayout) actionToolsView.layoutManager()).setDefaultConstraints
	    (new PackConstraints(8, false, false, false, 0, 0, 0, 2, 0));
	populatePalette(actionPalette, actionToolsView);
	addPaletteButton(PutAction.class, "RE pa");
	addPaletteButton(PutCalcAction.class, "RE pca");
	PlaywriteButton urlbutton
	    = PlaywriteButton.createFromResource("RE open url", true);
	addTestOrActionButton(RuleAction.class, OpenURLAction.class,
			      urlbutton);
	Enumeration extensions = _exHackQueue.elements();
	while (extensions.hasMoreElements()) {
	    Class superclass = (Class) extensions.nextElement();
	    Class extensionClass = (Class) extensions.nextElement();
	    Button exButton = (Button) extensions.nextElement();
	    addTestOrActionButton(superclass, extensionClass, exButton);
	}
	Rect ruleRect
	    = new Rect(0, 40, ruleViewSize.width, ruleViewSize.height);
	ruleContentView = new RuleContentView(ruleRect, showTestsButton,
					      showActionsButton, this);
	ruleContentView.setHorizResizeInstruction(2);
	ruleContentView.setVertResizeInstruction(16);
	mainView.addSubview(ruleContentView);
	mainView.addSubview(showActionsButton);
	mainView.addSubview(showTestsButton);
	controlView = new PlaywriteView(0, 0, mainView.width(), 40);
	controlView.setHorizResizeInstruction(2);
	PackLayout cvLayout = new PackLayout();
	controlView.setLayoutManager(cvLayout);
	PackConstraints evenConstraints
	    = new PackConstraints(6, false, false, false, 0, 0, 0, 0, 2);
	PackConstraints oddConstraints
	    = (PackConstraints) evenConstraints.clone();
	oddConstraints.setPadX(5);
	editButton = PlaywriteButton.createTextButton("RE er", "RE er", this);
	controlView.addSubview(editButton);
	cvLayout.setConstraints(editButton, evenConstraints);
	testButton = PlaywriteButton.createTextButton("RE tr", "RE tr", this);
	controlView.addSubview(testButton);
	cvLayout.setConstraints(testButton, oddConstraints);
	examineButton = new ToolButton(examineTool, "RE e", this);
	examineButton.setToolTipText(Resource.getToolTip("RE e"));
	controlView.addSubview(examineButton);
	cvLayout.setConstraints(examineButton, evenConstraints);
	examineButton.setEnabled(false);
	showVariablesButton
	    = new ToolButton(variableWindowTool, "RE sv", this);
	showVariablesButton.setToolTipText(Resource.getToolTip("RE sv"));
	controlView.addSubview(showVariablesButton);
	cvLayout.setConstraints(showVariablesButton, oddConstraints);
	for (int i = 0; i < controlViewPalette.size(); i++) {
	    Button button = (Button) controlViewPalette.elementAt(i);
	    controlView.addSubview(button);
	    cvLayout.setConstraints(button, (i % 2 == 0 ? evenConstraints
					     : oddConstraints));
	}
	nameLabel = new PlaywriteLabel(Resource.getText("RE name"),
				       Util.ruleFont, Util.ruleColor);
	controlView.addSubview(nameLabel);
	cvLayout.setConstraints(nameLabel, oddConstraints);
	nameField = new PlaywriteTextField();
	nameField.setBorder(BezelBorder.loweredBezel());
	nameField.setFont(Util.ruleFont);
	nameField.setTextColor(Util.ruleColor);
	nameField.setBackgroundColor(Util.ruleScrapColor);
	nameField.sizeTo(3, Util.ruleFontHeight + 5);
	nameField.setOwner(this);
	controlView.addSubview(nameField);
	evenConstraints.setFillX(true);
	evenConstraints.setExpand(true);
	cvLayout.setConstraints(nameField, evenConstraints);
	evenConstraints.setFillX(false);
	evenConstraints.setExpand(false);
	controlView.layoutView(0, 0);
	mainView.addSubview(controlView);
	setRule(rule, character);
	changeWindowColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	this.contentView().layoutView(0, 0);
	sizeToContents();
    }
    
    void changeState(Object transition) {
	ASSERT.isInEventThread();
	_state.changeState(transition);
    }
    
    public void stateChanged(Object target, Object oldState, Object transition,
			     Object newState) {
	Debug.print(true, " transition: ", oldState, "to", newState, "by",
		    transition);
	Debug.stackTrace();
    }
    
    public void addStateWatcher(StateWatcher w) {
	_state.addWatcher(w);
    }
    
    public void removeStateWatcher(StateWatcher w) {
	_state.removeWatcher(w);
    }
    
    void changeWindowColor(final Color color) {
	BezelBorder border = new BezelBorder(1, color, LIGHT_LINE_COLOR,
					     DARK_LINE_COLOR) {
	    public void drawInRect(Graphics g, int x, int y, int width,
				   int height) {
		BezelBorder.drawBezel(g, x, y, width, height, color,
				      RuleEditor.LIGHT_LINE_COLOR,
				      RuleEditor.DARK_LINE_COLOR,
				      RuleEditor.DARKER_LINE_COLOR, false);
	    }
	};
	mainView.setBackgroundColor(color);
	ruleContentView.changeWindowColor(INNER_WINDOW_COLOR);
	testToolsView.setBackgroundColor(color);
	actionToolsView.setBackgroundColor(color);
	toolPaletteScroller.setBackgroundColor(color);
	toolPaletteScroller.setBorder(border);
	this.contentView().setColor(color);
	controlView.setBackgroundColor(color);
	super.changeWindowColor(color);
    }
    
    private void populatePalette(Vector toolSource, View palette) {
	for (int i = 0; i < toolSource.size(); i++) {
	    Tool tool = (Tool) toolSource.elementAt(i);
	    PlaywriteButton toolButton = tool.makeButton();
	    palette.addSubview(toolButton);
	}
    }
    
    final boolean isLocked() {
	return isRecordingOrEditing() ^ true;
    }
    
    final CocoaCharacter getCharacter() {
	return character;
    }
    
    final Rule getRule() {
	return rule;
    }
    
    final BoardView getBeforeBoardView() {
	return beforeBoardView;
    }
    
    final void setBeforeBoardView(BoardView bbv) {
	beforeBoardView = bbv;
    }
    
    final BoardView getAfterBoardView() {
	return afterBoardView;
    }
    
    final void setAfterBoardView(BoardView abv) {
	afterBoardView = abv;
    }
    
    final void resetTestDisplay() {
	ruleContentView.resetTestViews();
	closeDependantWindows();
    }
    
    final void setTestHandle(boolean red) {
	ruleContentView.setTestHandle(red);
    }
    
    final void closeDependantWindows() {
	if (examineWindow != null) {
	    examineWindow.close();
	    examineWindow = null;
	}
	int i = variableWindowList.size();
	while (i-- > 0) {
	    PlaywriteWindow window
		= (PlaywriteWindow) variableWindowList.removeElementAt(i);
	    window.close();
	}
	int i_6_ = dependantWindowList.size();
	while (i_6_-- > 0) {
	    PlaywriteWindow window
		= (PlaywriteWindow) dependantWindowList.removeElementAt(i_6_);
	    window.close();
	}
    }
    
    public final void addDependantWindow(PlaywriteWindow window) {
	dependantWindowList.addElementIfAbsent(window);
    }
    
    private final void setExamineEnabled(boolean examine) {
	examineButton.setEnabled(examine);
	if (!examine)
	    resetTestDisplay();
    }
    
    static final RuleEditor getRuleEditor() {
	return ruleEditor;
    }
    
    static final void setRuleEditor(RuleEditor ed) {
	ruleEditor = ed;
    }
    
    static Rule ruleBeingDefined() {
	if (ruleEditor == null)
	    return null;
	return ruleEditor.getRule();
    }
    
    static final CharacterInstance getSelf() {
	return selfCharacter;
    }
    
    static GeneralizedCharacter getSelfGC() {
	return ruleBeingDefined().getBeforeBoard().getSelfGC();
    }
    
    static final Stage getSelfContainer() {
	return originalSelfContainer;
    }
    
    static final Point getSelfLocation() {
	return originalSelfLocation;
    }
    
    static AfterBoard getAfterBoard() {
	return ruleBeingDefined().getAfterBoard();
    }
    
    static int getSelfStageIndex() {
	return originalSelfStageIndex;
    }
    
    static void setSelfStageIndex(int idx) {
	originalSelfStageIndex = idx;
    }
    
    World setWorld(Rule rule, CocoaCharacter self) {
	World world;
	if (self != null)
	    world = self.getWorld();
	else if (rule != null)
	    world = rule.getWorld();
	else {
	    if (ruleEditor.getWorld() == null)
		throw new PlaywriteInternalError
			  ("Rule editor can't get World");
	    world = ruleEditor.getWorld();
	}
	this.setWorld(world);
	return world;
    }
    
    static void startRecording(CharacterInstance ch) {
	startRecording(ch, null);
    }
    
    static void startRecording(CharacterInstance self, Rule rule) {
	boolean recordNewRule = rule == null;
	recordMode = true;
	ruleEditingMode = recordNewRule ^ true;
	if (self == null) {
	    GeneralizedCharacter selfGC = getSelfGC();
	    originalSelfContainer = null;
	    originalSelfLocation = new Point(selfGC.getH(), selfGC.getV());
	    originalSelfStageIndex = -1;
	} else {
	    selfCharacter = self;
	    originalSelfContainer = (Stage) self.getCharContainer();
	    originalSelfLocation = new Point(self.getH(), self.getV());
	}
	makeRuleEditor(self, rule);
	World world = ruleEditor.getWorld();
	world.tick();
	world.suspendClockTicks(true);
	recordTime = world.getTime();
	PlaywriteRoot.disableAllWindows(world);
	world.changeState(recordNewRule ? World.RECORD_RULE : World.EDIT_RULE);
	getRuleEditor().changeState(recordNewRule ? "new rule" : "edit rule");
	world.setModified(true);
	if (ruleEditingMode && self != null
	    && rule.getSelf().bind(self) == false)
	    throw new PlaywriteInternalError("couldn't bind self... ");
	showRuleEditor();
    }
    
    static void resetLater() {
	PlaywriteRoot.app().performCommandLater(getRuleEditor(),
						"RESET_RECORDING", null);
    }
    
    void resetRecording() {
	if (_resetRecording)
	    throw new PlaywriteInternalError
		      ("redundant all to resetRecording");
	_resetRecording = true;
	this.disableDrawing();
	this.getWorld().undoActions(recordTime);
	RuleAction[] actions = new RuleAction[rule.getNumberOfActions()];
	int i = 0;
	Enumeration ruleActions = rule.getActions();
	while (ruleActions.hasMoreElements())
	    actions[i++] = (RuleAction) ruleActions.nextElement();
	while (i-- > 0)
	    rule.removeAction(actions[i]);
	AfterBoard afterboard = rule.getAfterBoard();
	afterboard.depopulate();
	afterboard.populate();
	World world = this.getWorld();
	for (i = 0; i < actions.length; i++)
	    world.doManualAction(actions[i]);
	this.reenableDrawing();
	this.draw();
	_resetRecording = false;
    }
    
    static void stopRecording(boolean cancel) {
	World world = ruleEditor.getWorld();
	recordMode = false;
	world.suspendClockTicks(false);
	if (cancel) {
	    world.undoActions(recordTime);
	    world.changeState(World.CANCEL);
	} else
	    world.changeState(World.DONE);
	PlaywriteRoot.enableAllWindows(world);
	Selection.resetGlobalState();
	if (selfCharacter != null)
	    Selection.select(selfCharacter, null);
	selfCharacter = null;
	originalSelfContainer = null;
	originalSelfLocation = null;
	originalSelfStageIndex = -1;
    }
    
    public static final boolean isRecordingOrEditing() {
	return recordMode;
    }
    
    public static final boolean isRuleEditing() {
	return recordMode && ruleEditingMode;
    }
    
    public static final boolean isRuleRecording() {
	return recordMode && !ruleEditingMode;
    }
    
    static boolean isInSpotlight(CocoaCharacter ch) {
	if (ch == selfCharacter)
	    return true;
	Rule rule = ruleBeingDefined();
	AfterBoard afterBoard = rule == null ? null : rule.getAfterBoard();
	if (afterBoard == null)
	    return false;
	return afterBoard.isInSpotlight(ch);
    }
    
    static boolean wasDeletedFromSpotlight(CocoaCharacter ch) {
	Rule rule = ruleBeingDefined();
	AfterBoard afterBoard = rule == null ? null : rule.getAfterBoard();
	return (afterBoard == null ? false
		: afterBoard.wasDeletedFromSpotlight(ch));
    }
    
    static void makeRuleEditor(CocoaCharacter ch, Rule rule) {
	Rule oldRule = null;
	if (ruleEditor == null)
	    ruleEditor = new RuleEditor(rule, ch);
	else {
	    oldRule = ruleEditor.getRule();
	    if (oldRule != rule || rule == null)
		ruleEditor.setRule(rule, ch);
	    else
		ruleEditor.character = ch;
	}
    }
    
    static void showRuleEditor() {
	ruleEditor.show();
    }
    
    static void showAt(Rule rule, ReferencedObject obj) {
	makeRuleEditor(rule.getOwner(), rule);
	Object part = rule.findReferenceTo(obj);
	ruleEditor.showTestOrAction(part);
	ruleEditor.show();
    }
    
    private void showTestOrAction(Object part) {
	if (part instanceof RuleTest && !(part instanceof BindTest))
	    ruleEditor.showTest((RuleTest) part);
	else if (part instanceof RuleAction)
	    ruleEditor.showAction((RuleAction) part);
	else
	    ruleEditor.closeTestsAndActions();
    }
    
    static void recordAction(RuleAction action) {
	if (isRecordingOrEditing())
	    ruleBeingDefined().addAction((RuleAction) action.clone());
    }
    
    static GeneralizedCharacter getGeneralizedCharacterFor
	(CharacterInstance ch) {
	if (isRecordingOrEditing())
	    return ruleBeingDefined().getBeforeBoard()
		       .getGeneralizedCharacterFor(ch);
	GeneralizedCharacter gch = new GeneralizedCharacter(ch);
	new GCAlias(gch);
	return gch;
    }
    
    static void makeRelativeToSelf(Point pt) {
	if (isRecordingOrEditing()) {
	    GeneralizedCharacter selfGC = getSelfGC();
	    pt.x = pt.x - selfGC.getH();
	    pt.y = pt.y - selfGC.getV();
	}
    }
    
    static Point alwaysMakeRelativeToSelf(Point pt) {
	GeneralizedCharacter selfGC = getSelfGC();
	return new Point(pt.x - selfGC.getH(), pt.y - selfGC.getV());
    }
    
    public static void setExtensionElementsActive(boolean active) {
	Enumeration extensions = _exHackQueue.elements();
	while (extensions.hasMoreElements()) {
	    extensions.nextElement();
	    extensions.nextElement();
	    ((Button) extensions.nextElement()).setEnabled(active);
	}
	for (int i = 0; i < controlViewPalette.size(); i++)
	    ((Button) controlViewPalette.elementAt(i)).setEnabled(active);
    }
    
    static void beginPretestMode(Subroutine sub) {
	subroutine = sub;
    }
    
    boolean editingPretest() {
	return subroutine != null;
    }
    
    static void endPretestMode() {
	subroutine = null;
    }
    
    void setRuleNameField(String newName) {
	nameField.setStringValue(newName);
    }
    
    void setRule(Rule r, CocoaCharacter character) {
	World world = setWorld(r, character);
	if (world != null)
	    world.setRuleEditor(this);
	contentChangePreamble();
	if (rule == r)
	    Debug.print("debug.rule.editor", "The rule is the same");
	if (r == null) {
	    if (subroutine != null)
		rule = new Pretest((CharacterInstance) character, subroutine);
	    else
		rule = new Rule((CharacterInstance) character);
	    originalCopy = null;
	} else {
	    rule = r;
	    originalCopy = (Rule) rule.copy();
	    if (!(rule instanceof Pretest))
		endPretestMode();
	}
	setCharacter(character);
	ruleContentView.setRule(rule, character);
	nameField.setStringValue(rule.getName());
	if (editingPretest()) {
	    this.setTitle(Resource.getText("PE title"));
	    showActionsButton.setEnabled(false);
	    if (showActionsButton.isDrawingEnabled())
		showActionsButton.disableDrawing();
	    if (nameLabel.isDrawingEnabled())
		nameLabel.disableDrawing();
	    if (nameField.isDrawingEnabled())
		nameField.disableDrawing();
	} else {
	    this.setTitle(Resource.getText("RE title"));
	    showActionsButton.reenableDrawing();
	    showActionsButton.setEnabled(true);
	    nameField.reenableDrawing();
	    nameLabel.reenableDrawing();
	}
	if (examineButton != null)
	    setExamineEnabled(false);
	boolean isRecording = isRecordingOrEditing();
	editButton.setEnabled(isRecording ^ true);
	testButton.setEnabled(isRecording ^ true);
	setPaletteButtonsEnabled(isRecording);
	showPalette();
	contentChangePostamble();
	changeWindowColor(this.getWorld().getColor());
	changeState("set rule");
    }
    
    public void worldStateChanged(Object who, Object oldState,
				  Object transition, Object newState) {
	setEnabledForWorldState(newState);
	super.worldStateChanged(who, oldState, transition, newState);
    }
    
    private void setEnabledForWorldState(Object newState) {
	if (newState != World.CLOSING)
	    setExamineEnabled(false);
	showVariablesButton.setEnabled(newState == World.RECORDING
				       || newState == World.EDITING);
	boolean stopped = newState == World.STOPPED;
	editButton.setEnabled(stopped);
	testButton.setEnabled(stopped || newState == World.DEBUGGING);
    }
    
    private void setCharacter(CocoaCharacter ch) {
	character = ch;
	if (characterView != null) {
	    characterView.removeFromSuperview();
	    characterView.discard();
	}
	if (character != null) {
	    characterView = new Icon(rule.getSelf());
	    characterView.setShowName(false);
	    this.getTitleBar().addSubviewRight(characterView);
	    characterView.setEventDelegate(-1, 0, 1, "SHI", this);
	    characterView.setEventDelegate(-3, 0, 1, "USHI", this);
	}
    }
    
    private void sizeToContents() {
	Size newSize
	    = this.windowSizeForContentSize(this.contentView().width(),
					    this.contentView().height());
	this.sizeTo(newSize.width, newSize.height);
    }
    
    public View sourceView(ToolSession session) {
	return this;
    }
    
    private void showToolWindow(ToolSession session, InternalWindow window) {
	if (session.destinationView() != null && !window.isVisible()) {
	    Point pt = (session.destinationView().convertPointToView
			(null, session.destinationMousePoint()));
	    window.moveTo(pt.x + 10, pt.y - window.height() / 2);
	}
	window.show();
    }
    
    public void toolWasAccepted(ToolSession session) {
	Tool toolType = session.toolType();
	if (toolType == examineTool) {
	    if (session.data() instanceof BindTest) {
		if (examineWindow == null)
		    examineWindow
			= new ExamineWindow(this.character.getWorld());
		examineWindow.setBindTest((BindTest) session.data());
		showToolWindow(session, examineWindow);
	    }
	} else if (toolType == variableWindowTool) {
	    if (session.data() instanceof CocoaCharacter) {
		CocoaCharacter character = (CocoaCharacter) session.data();
		CocoaCharacter realCharacter = character.dereference();
		if (realCharacter == null) {
		    if (character instanceof GeneralizedCharacter)
			realCharacter = ((GeneralizedCharacter) character)
					    .getAfterBoardCharacter();
		    else
			realCharacter = character;
		}
		if (variableWindowList.size() != 0) {
		    for (int i = 0; i < variableWindowList.size(); i++) {
			VariableWindow vw
			    = (VariableWindow) variableWindowList.elementAt(i);
			if (vw.getOwner() == realCharacter) {
			    vw.moveToFront();
			    return;
			}
		    }
		}
		VariableWindow vw
		    = new VariableWindow(realCharacter,
					 realCharacter.getWorld());
		vw.setOwner(this);
		showToolWindow(session, vw);
	    } else
		throw new PlaywriteInternalError
			  ("Variable Window tool got an object of class "
			   + session.data().getClass());
	}
    }
    
    public void toolWasRejected(ToolSession session) {
	/* empty */
    }
    
    public void sessionEnded(ToolSession session) {
	/* empty */
    }
    
    public void windowDidBecomeMain(Window win) {
	/* empty */
    }
    
    public void windowDidShow(Window win) {
	if (win instanceof VariableWindow)
	    variableWindowList.addElementIfAbsent(win);
	else if (win != this)
	    dependantWindowList.addElementIfAbsent(win);
    }
    
    public void windowDidHide(Window win) {
	if (win instanceof VariableWindow)
	    variableWindowList.removeElementIdentical(win);
	else if (win != this)
	    dependantWindowList.removeElementIdentical(win);
    }
    
    public void windowDidResignMain(Window win) {
	/* empty */
    }
    
    public boolean windowWillHide(Window win) {
	return true;
    }
    
    public boolean windowWillShow(Window win) {
	return true;
    }
    
    public void windowWillSizeBy(Window win, Size size) {
	/* empty */
    }
    
    private void showPalette() {
	if (isRecordingOrEditing() && ruleContentView.isDivided()) {
	    PlaywriteView newToolView = (ruleContentView.isShowingActions()
					 ? actionToolsView : testToolsView);
	    toolPaletteScroller.getScrollView().setContentView(newToolView);
	    this.addSubview(toolPaletteScroller);
	    mainView.moveBy(toolPaletteScroller.width() + 10, 0);
	    this.moveBy(-(toolPaletteScroller.width() + 10), 0);
	    currentPalette = newToolView;
	}
    }
    
    private void switchPalettes() {
	if (isRecordingOrEditing() && ruleContentView.isDivided()) {
	    PlaywriteView newToolView = (ruleContentView.isShowingActions()
					 ? actionToolsView : testToolsView);
	    toolPaletteScroller.getScrollView().setContentView(newToolView);
	    currentPalette = newToolView;
	}
    }
    
    private void removePalette() {
	if (currentPalette != null) {
	    toolPaletteScroller.removeFromSuperview();
	    mainView.moveBy(-(toolPaletteScroller.width() + 10), 0);
	    this.moveBy(toolPaletteScroller.width() + 10, 0);
	    currentPalette = null;
	}
    }
    
    private void finishRecording() {
	if (isRecordingOrEditing()) {
	    if (character == null)
		Debug.print("debug.rule.editor", "character is null");
	    rule.setName(nameField.stringValue());
	    rule.finishRecording();
	    if (!isRuleEditing() && !editingPretest())
		character.addAtFront(rule);
	    else if (editingPretest())
		subroutine.addPretest((Pretest) rule);
	    stopRecording(false);
	    if (currentPalette != null) {
		contentChangePreamble();
		removePalette();
		contentChangePostamble();
	    }
	}
    }
    
    private void contentChangePreamble() {
	this.contentView().disableDrawing();
    }
    
    private void contentChangePostamble() {
	this.contentView().setAutoResizeSubviews(false);
	mainView.setAutoResizeSubviews(false);
	int mainViewHorizRSI = mainView.horizResizeInstruction();
	int mainViewVertRSI = mainView.vertResizeInstruction();
	mainView.setHorizResizeInstruction(0);
	mainView.setVertResizeInstruction(4);
	int wid = mainView.bounds.width;
	int hit = mainView.bounds.height;
	Size mvs = mainView.minSize();
	mainView.sizeTo(mvs.width, mvs.height);
	int newContentHeight = mainView.bounds.maxY() + (10 - handleHeight);
	this.contentView().sizeTo(mainView.bounds.maxX() + 10,
				  newContentHeight);
	sizeToContents();
	if (currentPalette != null) {
	    toolPaletteScroller.moveTo(10, 10);
	    toolPaletteScroller.sizeTo(toolSize.width, newContentHeight - 20);
	}
	this.addDirtyRect(null);
	this.contentView().reenableDrawing();
	this.contentView().setAutoResizeSubviews(true);
	mainView.setAutoResizeSubviews(true);
	mainView.setHorizResizeInstruction(mainViewHorizRSI);
	mainView.setVertResizeInstruction(mainViewVertRSI);
	boundify();
    }
    
    private final boolean getLegalClose() {
	return _legalClose;
    }
    
    private final void setLegalClose(boolean okToClose) {
	_legalClose = okToClose;
    }
    
    public void show() {
	setLegalClose(false);
	cancelButton.setEnabled(true);
	doneButton.setEnabled(true);
	setExtensionElementsActive(Tutorial.isTutorialRunning() ^ true);
	if (rule != null && rule.getRuleEditorObject() != null) {
	    showTestOrAction(rule.getRuleEditorObject());
	    rule.setRuleEditorObject(null);
	}
	super.show();
	if (this.getWorld() != null)
	    setEnabledForWorldState(this.getWorld().getState());
	ASSERT.isTrue(_state.getState() != "closed");
    }
    
    public void hide() {
	if (getLegalClose()) {
	    if (_state.getState() != "closed")
		changeState("close editor");
	    super.hide();
	    if (rule == null) {
		Debug.print(true, "Error: redundant Rule Editor close");
		Debug.stackTrace();
	    } else {
		ruleContentView.dropRule();
		beforeBoardView = null;
		afterBoardView = null;
		closeDependantWindows();
		characterView.removeFromSuperview();
		characterView.discard();
		characterView = null;
		character = null;
		rule = null;
		originalCopy = null;
		this.setWorld(null);
	    }
	} else
	    performCommand("command c", null);
    }
    
    public void close() {
	if (getLegalClose())
	    hide();
	else
	    performCommand("command done", null);
    }
    
    public boolean boundify() {
	if (Tutorial.getTutorial() != null) {
	    Rect r = Tutorial.getTutorial().getWindowBounds("RuleMaker");
	    if (r != null) {
		this.setBounds(r);
		return true;
	    }
	}
	return super.boundify();
    }
    
    boolean showTest(RuleTest test) {
	return ruleContentView.showTest(test);
    }
    
    boolean showAction(RuleAction action) {
	return ruleContentView.showAction(action);
    }
    
    void closeTestsAndActions() {
	ruleContentView.closeTestsAndActions();
    }
    
    public void layoutParts() {
	this.disableDrawing();
	super.layoutParts();
	if (ruleContentView != null)
	    ruleContentView.fixResizeHandles();
	this.reenableDrawing();
    }
    
    private boolean hasLostFocus(String command, Object arg) {
	if (arg != this) {
	    PlaywriteRoot.getMainRootView().setFocusedView(null);
	    Application.application().performCommandLater(this, command, this);
	    return false;
	}
	return true;
    }
    
    public void setPaletteButtonsEnabled(boolean enabled) {
	for (int i = 0; i < controlViewPalette.size(); i++) {
	    Button button = (Button) controlViewPalette.elementAt(i);
	    button.setEnabled(enabled);
	}
	if (enabled)
	    setExtensionElementsActive(Tutorial.isTutorialRunning() ^ true);
    }
    
    public void performCommand(String command, Object arg) {
	if (command.equals("command c")) {
	    cancelButton.setEnabled(false);
	    doneButton.setEnabled(false);
	    if (this.getWorld().isClosing() || hasLostFocus(command, arg)) {
		setLegalClose(true);
		Selection.resetGlobalState();
		if (isRecordingOrEditing()) {
		    rule.cancelRecording(originalCopy);
		    stopRecording(true);
		    if (currentPalette != null) {
			contentChangePreamble();
			removePalette();
			contentChangePostamble();
		    }
		}
		endPretestMode();
		hide();
	    }
	} else if (command.equals("command done")) {
	    boolean dontClose = false;
	    cancelButton.setEnabled(false);
	    doneButton.setEnabled(false);
	    if (this.getWorld().isClosing() || hasLostFocus(command, arg)) {
		Tutorial tutorial = Tutorial.getTutorial();
		if (Tutorial.getTutorial() != null
		    && tutorial.wantToTestRule()) {
		    if (!tutorial.testRule(getRule()))
			dontClose = true;
		} else if (getRule().getNumberOfActions() < 1
			   && !editingPretest()) {
		    PlaywriteDialog dialog
			= new PlaywriteDialog("dialog rhna", "command done",
					      "command c");
		    String answer = dialog.getAnswer();
		    if (answer.equals("command c"))
			dontClose = true;
		}
		if (dontClose) {
		    cancelButton.setEnabled(true);
		    doneButton.setEnabled(true);
		} else {
		    setLegalClose(true);
		    Selection.resetGlobalState();
		    finishRecording();
		    String newName = nameField.stringValue();
		    rule.setName(newName);
		    rule.updateViews();
		    this.getWorld().setModified(true);
		    rule.getSelf().getPrototype().setModified(true);
		    endPretestMode();
		    hide();
		}
	    }
	} else if (command.equals("RESET_RECORDING"))
	    resetRecording();
	else if (command.equals("RE er")) {
	    if (isRuleEditing())
		throw new PlaywriteInternalError
			  ("attempt to leave Edit mode without hitting done");
	    setExamineEnabled(false);
	    this.getWorld().resetGeneralizedCharacters();
	    startRecording(null, rule);
	    ruleContentView.showSpotlightHandles();
	    if (ruleContentView.isDivided()) {
		contentChangePreamble();
		showPalette();
		contentChangePostamble();
	    }
	    setPaletteButtonsEnabled(true);
	} else if (command.equals("RE tr")) {
	    if (character instanceof CharacterInstance) {
		rule.setTestMode(true);
		rule.matches((CharacterInstance) character);
		changeState("test rule");
		rule.setTestMode(false);
		setExamineEnabled(true);
	    } else
		PlaywriteDialog.warning("dialog tbdw");
	} else if (command.equals("RE e"))
	    examineTool.newSession(this, 0, 0, command);
	else if (command.equals("RE sv"))
	    variableWindowTool.newSession(this, 0, 0, command);
	else if (command.equals("SHI"))
	    getRule().getSelf().halo();
	else if (command.equals("USHI"))
	    getRule().getSelf().unhalo();
	else if (arg == ruleContentView) {
	    contentChangePreamble();
	    if (command.equals("RE st") || command.equals("RE sa"))
		showPalette();
	    else if (command.equals("switchToTests")
		     || command.equals("switchToActions"))
		switchPalettes();
	    else if (command.equals("closeTests")
		     || command.equals("closeActions"))
		removePalette();
	    contentChangePostamble();
	} else if (_extensions.get(command) != null)
	    addNewTestOrAction(command);
	else
	    super.performCommand(command, arg);
    }
    
    protected Button createCloseButton() {
	if (doneButton == null) {
	    doneButton = (PlaywriteButton) super.createCloseButton();
	    doneButton
		.setImage(Resource.getButtonImage("command done w. text"));
	    doneButton.setAltImage
		(Resource.getAltButtonImage("command done w. text"));
	}
	return doneButton;
    }
    
    protected void finalize() throws Throwable {
	Debug.print("debug.gc", "Reclaiming RuleEditor ", this.title());
	super.finalize();
    }
    
    public void textEditingDidBegin(TextField textField) {
	/* empty */
    }
    
    public void textWasModified(TextField textField) {
	this.getWorld().setModified(true);
    }
    
    public boolean textEditingWillEnd(TextField textField, int endCondition,
				      boolean contentsChanged) {
	return true;
    }
    
    public void textEditingDidEnd(TextField textField, int endCondition,
				  boolean contentsChanged) {
	/* empty */
    }
    
    static void addToTestPalette(Tool t) {
	testPalette.addElement(t);
    }
    
    static void addToActionPalette(Tool t) {
	actionPalette.addElement(t);
    }
    
    public static boolean addRuleActionButton(Class ruleActionClass,
					      Button button) {
	return addTestOrActionButton(RuleAction.class, ruleActionClass,
				     button);
    }
    
    public static boolean addRuleTestButton(Class ruleTestClass,
					    Button button) {
	return addTestOrActionButton(RuleTest.class, ruleTestClass, button);
    }
    
    public static void addControlViewButton(Button button) {
	ASSERT.isTrue(getRuleEditor() == null);
	controlViewPalette.addElementIfAbsent(button);
    }
    
    private static boolean addPaletteButton(Class testActionClass,
					    String resourceID) {
	PlaywriteButton button
	    = PlaywriteButton.createFromResource(resourceID, true);
	if (RuleAction.class.isAssignableFrom(testActionClass))
	    return addTestOrActionButton(RuleAction.class, testActionClass,
					 button);
	return addTestOrActionButton(RuleTest.class, testActionClass, button);
    }
    
    private static boolean addTestOrActionButton
	(Class superclass, Class testOrActionClass, Button button) {
	String extensionName = testOrActionClass.getName();
	if (!superclass.isAssignableFrom(testOrActionClass)) {
	    String message
		= Resource.getTextAndFormat("RE not Ruleaction class fmt",
					    (new Object[]
					     { extensionName,
					       superclass.getName() }));
	    return false;
	}
	Method consMethod;
	try {
	    consMethod
		= testOrActionClass.getDeclaredMethod("createForRuleEditor",
						      (new Class[]
						       { World.class }));
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	    String message
		= Resource.getTextAndFormat("RE no method fmt",
					    new Object[] { extensionName });
	    return false;
	}
	if (!Modifier.isStatic(consMethod.getModifiers())
	    || !Modifier.isPublic(consMethod.getModifiers())
	    || (!RuleAction.class.isAssignableFrom(consMethod.getReturnType())
		&& !RuleTest.class
			.isAssignableFrom(consMethod.getReturnType()))) {
	    String message
		= Resource.getTextAndFormat("RE bad method fmt",
					    new Object[] { extensionName });
	    PlaywriteDialog.warning(message, true);
	    return false;
	}
	if (getRuleEditor() == null) {
	    _exHackQueue.addElement(superclass);
	    _exHackQueue.addElement(testOrActionClass);
	    _exHackQueue.addElement(button);
	    return true;
	}
	button.setTarget(getRuleEditor());
	button.setCommand(extensionName);
	if (getRuleEditor()._extensions.get(extensionName) == null) {
	    if (superclass != RuleAction.class && superclass != RuleTest.class)
		throw new RecoverableException
			  ("Attempt to add non-test or action", false);
	    RuleEditor ruleEditor = getRuleEditor();
	    View paletteView
		= (superclass == RuleAction.class ? ruleEditor.actionToolsView
		   : ruleEditor.testToolsView);
	    paletteView.addSubview(button);
	} else
	    Debug.print(true, "remaperating: ", extensionName);
	getRuleEditor()._extensions.put(extensionName, consMethod);
	return true;
    }
    
    private final void addNewTestOrAction(String actionClassName) {
	ASSERT.isTrue(isRecordingOrEditing());
	Method oneWorldMethod = (Method) _extensions.get(actionClassName);
	World world = this.getWorld();
	ASSERT.isNotNull(world);
	ASSERT.isNotNull(oneWorldMethod);
	Object testOrAction = null;
	try {
	    testOrAction = oneWorldMethod.invoke(null, new Object[] { world });
	} catch (java.lang.reflect.InvocationTargetException invocationtargetexception) {
	    /* empty */
	} catch (IllegalAccessException illegalaccessexception) {
	    /* empty */
	}
	if (testOrAction != null) {
	    if (testOrAction instanceof RuleTest)
		rule.addTest((RuleTest) testOrAction);
	    else if (testOrAction instanceof RuleAction)
		rule.addAction((RuleAction) testOrAction);
	    else
		throw new RecoverableException
			  ("unknown object created of type " + actionClassName,
			   false);
	}
    }
}
