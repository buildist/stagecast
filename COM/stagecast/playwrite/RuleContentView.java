/* RuleContentView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.LineBorder;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.ScrollView;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.OperationManager;
import COM.stagecast.operators.Total;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class RuleContentView extends View
    implements Debug.Constants, DragDestination, ResourceIDs.RuleEditorIDs,
	       Target, Worldly
{
    private static final int SCROLL_AMOUNT = 50;
    private static final int BEFORE_AFTER_SQUARE_SIZE = 32;
    static final String SWITCHTO_TESTS = "switchToTests";
    static final String CLOSE_TESTS = "closeTests";
    static final String SWITCHTO_ACTIONS = "switchToActions";
    static final String CLOSE_ACTIONS = "closeActions";
    static final Color whiteColor = Color.white;
    static final Color blackColor = Util.defaultDarkColor;
    static final int margin = 0;
    private static final int borderMargin = 2;
    int dividerHeight = 20;
    boolean dividedWindow = false;
    boolean showActions = true;
    private PlaywriteButton showTestsButton = null;
    private PlaywriteButton showActionsButton = null;
    private RuleEditor ruleEditorWindow = null;
    private Rule rule = null;
    private BeforeAfterView ruleView = null;
    private ScrollableArea ruleScroller = null;
    private PlaywriteView _testView = null;
    private ScrollableArea testViewScroller = null;
    private PlaywriteView _actionView = null;
    private ScrollableArea actionViewScroller = null;
    private Rect ruleRect = null;
    private Rect testRect = null;
    private Object draggedObject = null;
    private PackLayout packLayout = new PackLayout();
    private PackConstraints pcForRuleScroller;
    private PackConstraints pcForTestLabel;
    private PackConstraints pcForActionLabel;
    private PackConstraints pcForLabelView;
    private PackConstraints pcForTestActionScroller;
    private PlaywriteLabel testLabel
	= new PlaywriteLabel(Resource.getText("RE ai"), Util.ruleFont,
			     Util.ruleColor);
    private PlaywriteLabel actionLabel
	= new PlaywriteLabel(Resource.getText("RE a"), Util.ruleFont,
			     Util.ruleColor);
    private PlaywriteView labelView = new PlaywriteView();
    private Color _baseColor = Util.defaultColor;
    private Color _lightColor = Util.defaultLightColor;
    private Color _darkColor = Util.defaultDarkColor;
    private Border _redBorder = new LineBorder(Color.red);
    private Border _greenBorder = new LineBorder(Color.green);
    
    RuleContentView(Rect contentBounds, PlaywriteButton testButton,
		    PlaywriteButton actionButton,
		    RuleEditor ruleEditorWindow) {
	super(growRect(contentBounds, 0, 0));
	this.setHorizResizeInstruction(2);
	this.setVertResizeInstruction(16);
	this.ruleEditorWindow = ruleEditorWindow;
	this.setLayoutManager(packLayout);
	testLabel.sizeTo(testLabel.width(), dividerHeight);
	actionLabel.sizeTo(actionLabel.width(), dividerHeight);
	PackLayout labelLayout = new PackLayout();
	labelView.setLayoutManager(labelLayout);
	labelView.setTransparent(true);
	pcForTestLabel
	    = new PackConstraints(8, true, false, false, 0, 0, 0, 0, 2);
	pcForActionLabel
	    = new PackConstraints(8, true, false, false, 0, 0, 0, 0, 3);
	labelView.addSubview(testLabel);
	labelLayout.setConstraints(testLabel, pcForTestLabel);
	labelView.addSubview(actionLabel);
	labelLayout.setConstraints(actionLabel, pcForActionLabel);
	pcForLabelView
	    = new PackConstraints(8, true, true, false, 0, 0, 0, 0, 0);
	pcForRuleScroller
	    = new PackConstraints(8, true, true, true, 0, 0, 2, 2, 0);
	pcForTestActionScroller
	    = new PackConstraints(4, true, true, true, 0, 0, 2, 2, 1);
	showTestsButton = testButton;
	showActionsButton = actionButton;
	showTestsButton.setCommand("RE st");
	showTestsButton.setTarget(this);
	showActionsButton.setCommand("RE sa");
	showActionsButton.setTarget(this);
	ruleRect = new Rect(contentBounds);
	ruleRect.moveTo(0, 0);
	ruleRect.growBy(-2, -2);
	testRect = new Rect(ruleRect);
	testRect.moveBy(0, testRect.height + dividerHeight + 6);
	_testView = null;
	testViewScroller = new ScrollableArea(testRect, null, true, true);
	testViewScroller.setBuffered(true);
	testViewScroller.setMinSize(testRect.width, testRect.height);
	testViewScroller.allowDragInto(AbstractVariableEditor.class, this);
	testViewScroller.allowDragInto(Total.class, this);
	testViewScroller.setHorizontalScrollAmount(50);
	testViewScroller.setVerticalScrollAmount(50);
	_actionView = null;
	actionViewScroller = new ScrollableArea(testRect, null, true, true);
	actionViewScroller.setBuffered(true);
	actionViewScroller.setMinSize(testRect.width, testRect.height);
	actionViewScroller.setHorizontalScrollAmount(50);
	actionViewScroller.setVerticalScrollAmount(50);
	actionViewScroller.allowDragInto(AbstractVariableEditor.class, this);
    }
    
    private static Rect growRect(Rect rect, int dx, int dy) {
	Rect r2 = new Rect(rect);
	r2.growBy(dx, dy);
	return r2;
    }
    
    public final World getWorld() {
	return ruleEditorWindow.getWorld();
    }
    
    final boolean isDivided() {
	return dividedWindow;
    }
    
    final boolean isShowingActions() {
	return showActions;
    }
    
    void changeWindowColor(Color color) {
	_baseColor = color;
	_lightColor = color;
	_darkColor = color.darkerColor();
	if (ruleScroller != null)
	    ruleScroller.changeWindowColor(color, color);
	testViewScroller.changeWindowColor(color, color);
	actionViewScroller.changeWindowColor(color, color);
	ruleScroller.changeWindowColor(color, color);
	ruleScroller.setBackgroundColor(color);
	ruleView.setBackgroundColor(color);
    }
    
    void setRule(Rule r, CocoaCharacter character) {
	if (rule == r && RuleEditor.isRecordingOrEditing()) {
	    if (!RuleEditor.isRuleEditing())
		throw new PlaywriteInternalError
			  ("rule editing state conflict...");
	} else {
	    if (ruleScroller != null && rule != null)
		dropRule();
	    PlaywriteView rv = ruleView;
	    int squareSize;
	    if (character.getContainer() instanceof Stage)
		squareSize = 32;
	    else
		squareSize = 32;
	    rule = r;
	    ruleView = (BeforeAfterView) rule.createView(squareSize);
	    if (ruleScroller != null)
		ruleScroller.getScrollView().setContentView(ruleView);
	    else {
		ruleScroller
		    = new ScrollableArea(ruleRect, ruleView, true, true);
		ruleScroller.setAllowSmallContentView(false);
		ruleScroller.setMinSize(ruleRect.width, ruleRect.height);
		this.addSubview(ruleScroller);
		packLayout.setConstraints(ruleScroller, pcForRuleScroller);
	    }
	    ruleScroller.maximizeContentView();
	    if (ruleView instanceof BeforeAfterView) {
		BeforeAfterView bav = ruleView;
		View centerView = bav.getArrowView();
		if (centerView.superview() == null)
		    centerView = bav.getAfterBoardView();
		int x = centerView.width() / 2;
		int y = centerView.height() / 2;
		Point newPoint = centerView.convertToView(bav, x, y);
		ScrollView sv = ruleScroller.getScrollView();
		newPoint.x = sv.width() / 2 - newPoint.x;
		newPoint.y = sv.height() / 2 - newPoint.y;
		ruleScroller.scrollBy(newPoint.x, newPoint.y);
	    }
	    boolean enableViews = RuleEditor.isRecordingOrEditing();
	    makeActions(enableViews);
	    makeTests(enableViews);
	    if (ruleEditorWindow.editingPretest() && isDivided())
		undivideWindow();
	}
    }
    
    void dropRule() {
	if (rule == null) {
	    Debug.print(true, "Error: redundant call to dropRule");
	    Debug.stackTrace();
	} else {
	    rule = null;
	    ruleScroller.getScrollView().setContentView(null);
	    ruleView.discard();
	    Util.detachSubviews(ruleView);
	    ruleView = null;
	    actionViewScroller.getScrollView().setContentView(null);
	    _actionView.discard();
	    Util.detachSubviews(_actionView);
	    _actionView = null;
	    testViewScroller.getScrollView().setContentView(null);
	    _testView.discard();
	    Util.detachSubviews(_testView);
	    _testView = null;
	}
    }
    
    private void makeActions(boolean enableViews) {
	if (_actionView != null)
	    Debug.print("debug.rule.editor", "actionview not null");
	_actionView = rule.createActionsView(enableViews);
	_actionView.allowDragInto(AbstractVariableEditor.class, this);
	actionViewScroller.getScrollView().setContentView(_actionView);
	actionViewScroller.scrollTo(0, 0);
    }
    
    private void makeTests(boolean enableViews) {
	if (_testView != null)
	    Debug.print("debug.rule.editor", "testView not null");
	Enumeration tests = rule.getTests();
	while (tests.hasMoreElements()) {
	    RuleTest test = (RuleTest) tests.nextElement();
	    if (test.isDisplayedInBeforeBoard()) {
		if (ruleEditorWindow.editingPretest())
		    test.setView(ruleEditorWindow.getAfterBoardView());
		else
		    test.setView(ruleEditorWindow.getBeforeBoardView());
	    } else if (test.getView() != null) {
		Debug.print("debug.rule.editor", "Test had a view already: ",
			    test);
		test.setView(null);
	    }
	}
	_testView = rule.createTestsView(enableViews);
	_testView.allowDragInto(AbstractVariableEditor.class, this);
	_testView.allowDragInto(Total.class, this);
	testViewScroller.getScrollView().setContentView(_testView);
	testViewScroller.scrollTo(0, 0);
    }
    
    void resetTestViews() {
	RuleTest test = null;
	PlaywriteView view = null;
	String resourceID = "HandleH";
	showTestsButton.setImage(Resource.getButtonImage(resourceID));
	showTestsButton.setAltImage(Resource.getAltButtonImage(resourceID));
	showTestsButton.setDirty(true);
	Enumeration testList = rule.getTests();
	while (testList.hasMoreElements()) {
	    test = (RuleTest) testList.nextElement();
	    test.resetView();
	}
    }
    
    final void setTestHandle(boolean red) {
	String resourceID = red ? "HandleH Red" : "HandleH Green";
	showTestsButton.setImage(Resource.getButtonImage(resourceID));
	showTestsButton.setAltImage(Resource.getAltButtonImage(resourceID));
	showTestsButton.setDirty(true);
    }
    
    public void drawView(Graphics g) {
	g.setColor(_lightColor);
	g.fillRect(0, 0, this.width(), this.height());
    }
    
    public void drawSubviews(Graphics g) {
	drawViewBorder(g);
	super.drawSubviews(g);
    }
    
    public void drawViewBorder(Graphics g) {
	BezelBorder.drawBezel(g, 0, 0, bounds.width, bounds.height, _baseColor,
			      RuleEditor.LIGHT_LINE_COLOR,
			      RuleEditor.DARK_LINE_COLOR,
			      RuleEditor.DARKER_LINE_COLOR, false);
	if (dividedWindow) {
	    int centron = bounds.width / 2;
	    int spacious = 30;
	    int x;
	    int width;
	    if (showActions) {
		x = -2;
		width = centron - spacious + 4;
	    } else {
		x = centron + spacious;
		width = bounds.width - x + 2;
	    }
	    int y = ruleScroller.bounds.maxY();
	    int height = dividerHeight;
	    BezelBorder.drawBezel(g, x, y, width, height, _baseColor,
				  RuleEditor.LIGHT_LINE_COLOR,
				  RuleEditor.DARK_LINE_COLOR,
				  RuleEditor.DARKER_LINE_COLOR, true);
	    g.setColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	    Rect r = new Rect(x, y, width, height);
	    r.growBy(0, -2);
	    r.moveBy(showActions ? -2 : 2, 0);
	    g.fillRect(r.x, r.y, r.width, r.height);
	}
    }
    
    public void layoutView(int dx, int dy) {
	int w = this.width();
	int h = this.height();
	w -= 4;
	h = (h - dividerHeight) / 2;
	if (ruleScroller != null) {
	    ruleScroller.setMinSize(w, h);
	    if (dividedWindow) {
		if (showActions)
		    actionViewScroller.setMinSize(w, h);
		else
		    testViewScroller.setMinSize(w, h);
	    }
	}
	super.layoutView(dx, dy);
	if (showTestsButton != null) {
	    int fourth = this.width() / 4;
	    Point tb = new Point(0, 0);
	    Point ab = new Point(0, 0);
	    tb.x = fourth - showTestsButton.width() / 2;
	    ab.x = tb.x + fourth * 2;
	    tb.y = ab.y = bounds.height;
	    if (dividedWindow) {
		if (showActions)
		    tb.y = ruleScroller.bounds.maxY() + 1;
		else
		    ab.y = ruleScroller.bounds.maxY() + 1;
	    }
	    tb = this.convertPointToView(showTestsButton.superview(), tb);
	    ab = this.convertPointToView(showActionsButton.superview(), ab);
	    showTestsButton.moveTo(tb.x, tb.y);
	    showActionsButton.moveTo(ab.x, ab.y);
	}
	if ((dx < 0 || dy < 0) && ruleScroller != null && ruleView != null) {
	    ScrollView scrollView = ruleScroller.getScrollView();
	    Size rvMinSize = ruleView.minSize();
	    if (scrollView.width() > rvMinSize.width
		|| scrollView.height() > rvMinSize.height) {
		if (rvMinSize.width < scrollView.width())
		    rvMinSize.width = scrollView.width();
		if (rvMinSize.height < scrollView.height())
		    rvMinSize.height = scrollView.height();
		ruleView.sizeTo(rvMinSize.width, rvMinSize.height);
	    }
	}
    }
    
    void switchToTestsOrActions() {
	this.disableDrawing();
	View remView = showActions ? testViewScroller : actionViewScroller;
	remView.removeFromSuperview();
	if (showActions) {
	    this.addSubview(actionViewScroller);
	    packLayout.setConstraints(actionViewScroller,
				      pcForTestActionScroller);
	    ruleEditorWindow.performCommand("switchToActions", this);
	} else {
	    this.addSubview(testViewScroller);
	    packLayout.setConstraints(testViewScroller,
				      pcForTestActionScroller);
	    ruleEditorWindow.performCommand("switchToTests", this);
	}
	updateTestActionButtons();
	this.reenableDrawing();
	layoutView(0, 0);
    }
    
    private void divideWindow() {
	ruleScroller.setAllowSmallContentView(true);
	dividedWindow = true;
	int yinc = ruleRect.height + dividerHeight + 2;
	this.sizeBy(0, yinc);
	this.setMinSize(bounds.width, bounds.height);
	this.disableDrawing();
	ruleScroller.setMinSize(testRect.width, testRect.height);
	PackConstraints pcForRule = packLayout.constraintsFor(ruleScroller);
	pcForRule.setFillY(false);
	this.addSubview(labelView);
	packLayout.setConstraints(labelView, pcForLabelView);
	if (showActions) {
	    actionViewScroller.setMinSize(testRect.width, testRect.height);
	    this.addSubview(actionViewScroller);
	    packLayout.setConstraints(actionViewScroller,
				      pcForTestActionScroller);
	    actionViewScroller.sizeTo(testRect.width, testRect.height);
	    ruleEditorWindow.performCommand("RE sa", this);
	    ruleEditorWindow.setMainScroller(actionViewScroller);
	} else {
	    testViewScroller.setMinSize(testRect.width, testRect.height);
	    this.addSubview(testViewScroller);
	    testViewScroller.sizeTo(testRect.width, testRect.height);
	    packLayout.setConstraints(testViewScroller,
				      pcForTestActionScroller);
	    ruleEditorWindow.performCommand("RE st", this);
	    ruleEditorWindow.setMainScroller(testViewScroller);
	}
	updateTestActionButtons();
	ruleScroller.setAllowSmallContentView(false);
	layoutView(0, 0);
	this.reenableDrawing();
	fixResizeHandles();
    }
    
    private void updateTestActionButtons() {
	if (dividedWindow) {
	    if (showActions) {
		showActionsButton
		    .setToolTipText(Resource.getToolTip("ALT RE sa"));
		showActionsButton.setState(false);
		showTestsButton.setToolTipText(Resource.getToolTip("RE st"));
		showTestsButton.setState(true);
		testLabel.setStringValue("");
		actionLabel.setStringValue(Resource.getText("RE a"));
	    } else {
		showActionsButton.setToolTipText(Resource.getToolTip("RE sa"));
		showActionsButton.setState(true);
		showTestsButton
		    .setToolTipText(Resource.getToolTip("ALT RE st"));
		showTestsButton.setState(false);
		testLabel.setStringValue(Resource.getText("RE ai"));
		actionLabel.setStringValue("");
	    }
	} else {
	    showActionsButton.setToolTipText(Resource.getToolTip("RE sa"));
	    showTestsButton.setToolTipText(Resource.getToolTip("RE st"));
	    showTestsButton.setState(true);
	    showTestsButton.setState(true);
	    testLabel.setStringValue("");
	    actionLabel.setStringValue("");
	}
    }
    
    void fixResizeHandles() {
	if (ruleView != null)
	    ruleView.updateSpotlightHandles();
    }
    
    private void undivideWindow() {
	ruleScroller.setAllowSmallContentView(true);
	if (dividedWindow) {
	    dividedWindow = false;
	    labelView.removeFromSuperview();
	    if (showActions)
		actionViewScroller.removeFromSuperview();
	    else
		testViewScroller.removeFromSuperview();
	    PackConstraints pcForRule
		= packLayout.constraintsFor(ruleScroller);
	    pcForRule.setFillY(true);
	    this.sizeTo(this.width(), ruleScroller.height());
	    this.superview().layoutView(0, 0);
	    this.disableDrawing();
	    if (showActions)
		ruleEditorWindow.performCommand("closeActions", this);
	    else
		ruleEditorWindow.performCommand("closeTests", this);
	    ruleScroller.setAllowSmallContentView(false);
	    ruleEditorWindow.setMainScroller(null);
	    updateTestActionButtons();
	    this.reenableDrawing();
	}
    }
    
    public final boolean isLocked() {
	return ruleEditorWindow.isLocked();
    }
    
    public DragDestination acceptsDrag(DragSession ds, int x, int y) {
	if (isLocked())
	    return null;
	DragDestination accepted = super.acceptsDrag(ds, x, y);
	if (accepted == null) {
	    if (ds.destinationView() instanceof PlaywriteView)
		((PlaywriteView) ds.destinationView()).unhilite();
	} else if (ds.destinationView() instanceof PlaywriteView)
	    ((PlaywriteView) ds.destinationView()).hilite();
	return accepted;
    }
    
    public boolean dragDropped(DragSession session) {
	View destinationView = session.destinationView();
	if (!(session.destinationView() instanceof PlaywriteView))
	    destinationView = session.destinationView().superview();
	if (destinationView instanceof PlaywriteView)
	    ((PlaywriteView) destinationView).unhilite();
	if (session.data() instanceof AbstractVariableEditor) {
	    AbstractVariableEditor variableEditor
		= (AbstractVariableEditor) session.data();
	    Variable variable = variableEditor.getVariable();
	    VariableOwner owner = variableEditor.getOwner();
	    VariableAlias varAlias = variable.createVariableAlias(owner);
	    Object value = variable.getValue(owner);
	    if (value instanceof CharacterInstance) {
		GeneralizedCharacter gc
		    = (rule.getBeforeBoard().getGeneralizedCharacterFor
		       ((CharacterInstance) value));
		if (gc == null) {
		    PlaywriteDialog.warning("RE ct warn", true);
		    value = null;
		} else
		    value = gc;
	    }
	    if (destinationView == _testView
		|| destinationView == testViewScroller) {
		testViewScroller.unhilite();
		COM.stagecast.operators.Expression newEqTest
		    = new OperationManager(varAlias, value, Op.Equal);
		rule.addTest(new BooleanTest(newEqTest));
		return true;
	    }
	    if (destinationView == _actionView
		|| destinationView == actionViewScroller) {
		actionViewScroller.unhilite();
		rule.addAction(new PutAction(varAlias, value));
		return true;
	    }
	} else {
	    Object model = ((ViewGlue) session.data()).getModelObject();
	    ASSERT.isTrue(destinationView == _testView
			  || destinationView == testViewScroller);
	    testViewScroller.unhilite();
	    if (model instanceof Total) {
		Object result = ((Total) model).eval();
		COM.stagecast.operators.Expression newEqTest
		    = new OperationManager(model, result, Op.Equal);
		rule.addTest(new BooleanTest(newEqTest));
		return true;
	    }
	}
	return false;
    }
    
    private void hiliteScrollerInDrag(View destinationView, boolean on) {
	if (destinationView instanceof ScrollView)
	    destinationView = destinationView.superview();
	if (destinationView == _testView
	    || destinationView == testViewScroller) {
	    if (on)
		testViewScroller.hilite();
	    else
		testViewScroller.unhilite();
	} else if (destinationView == _actionView
		   || destinationView == actionViewScroller) {
	    if (on)
		actionViewScroller.hilite();
	    else
		actionViewScroller.unhilite();
	}
    }
    
    public boolean dragEntered(DragSession session) {
	if (isLocked())
	    return false;
	hiliteScrollerInDrag(session.destinationView(), true);
	return true;
    }
    
    public void dragExited(DragSession session) {
	hiliteScrollerInDrag(session.destinationView(), false);
	if (session.destinationView() instanceof PlaywriteView)
	    ((PlaywriteView) session.destinationView()).unhilite();
    }
    
    public boolean dragMoved(DragSession session) {
	if (isLocked())
	    return false;
	hiliteScrollerInDrag(session.destinationView(), true);
	return true;
    }
    
    boolean showTest(RuleTest ruleTest) {
	if (rule != ruleTest.getRule())
	    return false;
	if (showActions || !dividedWindow)
	    performCommand("RE st", null);
	return showView(ruleTest);
    }
    
    boolean showAction(RuleAction ruleAction) {
	if (rule != ruleAction.getRule())
	    return false;
	if (!showActions || !dividedWindow)
	    performCommand("RE sa", null);
	return showView(ruleAction);
    }
    
    private boolean showView(IndexedObject thing) {
	View view = thing.getView();
	if (view == null)
	    return false;
	view.scrollRectToVisible(view.localBounds());
	return true;
    }
    
    void closeTestsAndActions() {
	if (dividedWindow)
	    undivideWindow();
    }
    
    public void showSpotlightHandles() {
	ruleView.showSpotlightHandles();
	if (_testView != null && _testView instanceof Enableable)
	    ((Enableable) _testView).setEnabled(true);
	if (_actionView != null && _actionView instanceof Enableable)
	    ((Enableable) _actionView).setEnabled(true);
    }
    
    public void hideSpotlightHandles() {
	ruleView.hideSpotlightHandles();
    }
    
    public void performCommand(String command, Object thing) {
	if (command.equals("RE st")) {
	    if (showActions || !dividedWindow) {
		showActions = false;
		this.addDirtyRect(null);
		if (dividedWindow)
		    switchToTestsOrActions();
		else
		    divideWindow();
	    } else if (dividedWindow)
		undivideWindow();
	} else if (command.equals("RE sa")) {
	    if (!showActions || !dividedWindow) {
		showActions = true;
		this.addDirtyRect(null);
		if (dividedWindow)
		    switchToTestsOrActions();
		else
		    divideWindow();
	    } else if (dividedWindow)
		undivideWindow();
	}
    }
}
