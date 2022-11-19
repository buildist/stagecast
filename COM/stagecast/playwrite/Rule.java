/* Rule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.OperationManager;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class Rule extends RuleListItem
    implements Debug.Constants, Externalizable, ResourceIDs.CharacterWindowIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108756002098L;
    private static ToolHandler.ToolArbiter defaultToolArbiter = new ToolHandler.ToolArbiter() {
	public boolean wantsToolNow(Tool toolType) {
	    return RuleEditor.isRecordingOrEditing();
	}
    };
    private static ToolHandler defaultToolHandler
	= new ToolHandler(defaultToolArbiter);
    private static ToolHandler.ToolAdder defaultTools = new ToolHandler.ToolAdder() {
	public void addTools(PlaywriteView testOrActionView) {
	    testOrActionView.allowTool(Tool.deleteTool,
				       Rule.defaultToolHandler);
	}
    };
    private static IndexedList.PermitDrag defaultPermitDrag = new IndexedList.PermitDrag() {
	public boolean dragPermitted(Indexed member) {
	    return RuleEditor.isRecordingOrEditing();
	}
    };
    private String _name = Resource.getText("character window new rule");
    private String _comment;
    private GeneralizedCharacter _selfGC;
    private IndexedList _ruleTests;
    private IndexedList _ruleActions;
    private BeforeBoard _beforeBoard;
    private boolean testMode = false;
    private transient AfterBoard _afterBoard;
    private transient Bitmap _miniRuleImage;
    private transient IndexedContainer.Notifier _notifier = new IndexedContainer.Notifier() {
	public void indexedAdded(Indexed indexed) {
	    ((Content) indexed).setRule(Rule.this);
	}
	
	public void indexedRemoved(Indexed indexed) {
	    /* empty */
	}
	
	public void userModified(Indexed indexed) {
	    if (indexed.getContainer() != _ruleTests) {
		RuleEditor ruleEditor = RuleEditor.getRuleEditor();
		if (ruleEditor != null && ruleEditor.getRule() == Rule.this)
		    ruleEditor.resetRecording();
	    }
	}
    };
    private transient Object _ruleEditorObject;
    
    static interface Content
    {
	public void setRule(Rule rule);
	
	public Rule getRule();
    }
    
    public Rule() {
	_ruleTests
	    = new IndexedList(1, RuleTest.class, defaultTools, _notifier);
	_ruleActions
	    = new IndexedList(1, RuleAction.class, defaultTools, _notifier);
	_ruleTests.setPermitDrag(defaultPermitDrag);
	_ruleActions.setPermitDrag(defaultPermitDrag);
    }
    
    Rule(CharacterInstance self) {
	this();
	if (!(self.getContainer() instanceof Stage))
	    throw new PlaywriteInternalError
		      ("rules may only be defined on stages");
	Appearance appearance = self.getCurrentAppearance();
	Stage stage = (Stage) self.getContainer();
	_selfGC = new GeneralizedCharacter(self);
	Point homeSquare = self.getCurrentAppearance().getHomeSquare();
	_selfGC.setH(homeSquare.x);
	_selfGC.setV(homeSquare.y);
	Rect rect = self.getRuleDefineRect();
	_beforeBoard = new BeforeBoard(rect.width, rect.height, _selfGC, 32);
	_beforeBoard.populate(stage, rect);
	_afterBoard = new AfterBoard(_beforeBoard);
	_afterBoard.populate();
	if (Variable.systemVariable
		(CocoaCharacter.SYS_APPEARANCE_VARIABLE_ID, self)
		.isVisible()) {
	    VariableAlias selfAppearance
		= new VariableAlias(_selfGC,
				    self.getPrototype().appearanceVar);
	    addTest(new BooleanTest(new OperationManager(selfAppearance,
							 appearance,
							 Op.Equal)));
	}
	this.setSubroutine(self.getMainSubroutine());
    }
    
    final GeneralizedCharacter getSelf() {
	return _selfGC;
    }
    
    final AfterBoard getAfterBoard() {
	return _afterBoard;
    }
    
    final void setAfterBoard(AfterBoard b) {
	ASSERT.isTrue(_afterBoard == null || _afterBoard == b);
	_afterBoard = b;
    }
    
    final BeforeBoard getBeforeBoard() {
	return _beforeBoard;
    }
    
    final void setBeforeBoard(BeforeBoard b) {
	ASSERT.isTrue(_beforeBoard == null || _beforeBoard == b);
	_beforeBoard = b;
    }
    
    final void drBuildBeforeBoard() {
	if (getBeforeBoard() == null)
	    BeforeBoard.createFromRule(this, 32);
	else
	    Debug.print("debug.dr", "no beforeBoard needed");
    }
    
    final String getComment() {
	return _comment;
    }
    
    final void setComment(String s) {
	_comment = s;
    }
    
    public final String getName() {
	return _name;
    }
    
    public final void setName(String s) {
	_name = s;
    }
    
    int getNumberOfTests() {
	return _ruleTests.getNumberOfElements();
    }
    
    int getNumberOfActions() {
	return _ruleActions.getNumberOfElements();
    }
    
    RuleTest getRuleTest(int i) {
	return (RuleTest) _ruleTests.getElementAt(i);
    }
    
    RuleAction getRuleAction(int i) {
	return (RuleAction) _ruleActions.getElementAt(i);
    }
    
    Enumeration getTests() {
	return _ruleTests.getElements();
    }
    
    Enumeration getActions() {
	return _ruleActions.getElements();
    }
    
    final void setTestMode(boolean tm) {
	testMode = tm;
    }
    
    protected void setSelfGC(GeneralizedCharacter gc) {
	_selfGC = gc;
    }
    
    ToolHandler.ToolArbiter getToolArbiter() {
	return defaultToolArbiter;
    }
    
    final void setRuleEditorObject(Object foo) {
	_ruleEditorObject = foo;
    }
    
    final Object getRuleEditorObject() {
	return _ruleEditorObject;
    }
    
    void addTest(RuleTest test) {
	if (test != null) {
	    if (test instanceof BindTest) {
		Point newBinding = ((BindTest) test).getCoordinate();
		int rtSize = _ruleTests.getNumberOfElements();
		for (int i = 0; i < rtSize; i++) {
		    RuleTest previousTest
			= (RuleTest) _ruleTests.getElementAt(i);
		    if (previousTest instanceof BindTest
			&& ((BindTest) previousTest).getCoordinate()
			       .equals(newBinding))
			throw new PlaywriteInternalError
				  ("Attempting to add a new BindTest at the same location as the previous test");
		}
		if (_selfGC == null)
		    _selfGC = ((BindTest) test).getSelfifSelf();
	    }
	    _ruleTests.add(test);
	}
    }
    
    void removeTest(RuleTest test) {
	_ruleTests.remove(test);
    }
    
    BindTest getBindTestFor(int dx, int dy) {
	int rtSize = _ruleTests.getNumberOfElements();
	for (int i = 0; i < rtSize; i++) {
	    RuleTest ruleTest = (RuleTest) _ruleTests.getElementAt(i);
	    if (ruleTest instanceof BindTest) {
		BindTest bindTest = (BindTest) ruleTest;
		if (bindTest.isForSquare(dx, dy))
		    return bindTest;
	    }
	}
	return null;
    }
    
    void addAction(RuleAction newAction) {
	if (newAction != null)
	    _ruleActions.add(newAction);
    }
    
    void removeAction(RuleAction action) {
	_ruleActions.remove(action);
    }
    
    void setBounds(int xmina, int ymina, int xmaxa, int ymaxa) {
	Debug.print(true, "invocation of Rule.setBounds: please remove");
	Debug.stackTrace();
    }
    
    protected boolean matchAndExecute(CharacterInstance self) {
	World world = self.getWorld();
	RuleEditor ruleEditor = world.getRuleEditor();
	if (self.getContainer() == null)
	    return false;
	world.resetGeneralizedCharacters();
	if (ruleEditor != null && RuleEditor.ruleBeingDefined() == this
	    && ruleEditor.getCharacter() == self) {
	    boolean bindFailed = false;
	    boolean testFailed = false;
	    ruleEditor.resetTestDisplay();
	    int rtSize = _ruleTests.getNumberOfElements();
	    int i;
	    for (i = 0; i < rtSize; i++) {
		RuleTest test = (RuleTest) _ruleTests.getElementAt(i);
		if (!(test instanceof BindTest))
		    break;
		boolean result = test.evaluate(self);
		test.showTestResult(result);
		if (!result)
		    bindFailed = testFailed = true;
	    }
	    if (!bindFailed) {
		for (int j = i; j < rtSize; j++) {
		    RuleTest test = (RuleTest) _ruleTests.getElementAt(j);
		    boolean result = test.evaluate(self);
		    test.showTestResult(result);
		    if (!result)
			testFailed = true;
		}
		ruleEditor.setTestHandle(testFailed);
	    }
	    if (testMode)
		return false;
	    if (testFailed)
		return false;
	} else {
	    int rtSize = _ruleTests.getNumberOfElements();
	    for (int i = 0; i < rtSize; i++) {
		RuleTest test = (RuleTest) _ruleTests.getElementAt(i);
		if (!test.evaluate(self))
		    return false;
	    }
	}
	world.setRuleMatched();
	CharacterContainer container = self.getCharContainer();
	int baseX = self.getH();
	int baseY = self.getV();
	int raSize = _ruleActions.getNumberOfElements();
	for (int i = 0; i < raSize; i++) {
	    RuleAction action = (RuleAction) _ruleActions.getElementAt(i);
	    action = (RuleAction) action.clone();
	    world.executeAction(action, container, baseX, baseY);
	}
	return true;
    }
    
    Object findReferenceTo(ReferencedObject obj) {
	int size = _ruleActions.getNumberOfElements();
	for (int i = 0; i < size; i++) {
	    RuleAction action = (RuleAction) _ruleActions.getElementAt(i);
	    if (action.findReferenceTo(obj) != null)
		return action;
	}
	size = _ruleTests.getNumberOfElements();
	for (int i = 0; i < size; i++) {
	    RuleTest test = (RuleTest) _ruleTests.getElementAt(i);
	    if (test.findReferenceTo(obj) != null)
		return test;
	}
	if (_afterBoard != null) {
	    GCAlias alias = _afterBoard.findReferenceTo(obj);
	    if (alias != null)
		return alias;
	}
	return null;
    }
    
    int countRulesReferringTo(ReferencedObject obj) {
	if (this.refersTo(obj))
	    return 1;
	return 0;
    }
    
    int getRuleCount() {
	return 1;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Rule newRule = (Rule) map.get(this);
	if (newRule != null)
	    return newRule;
	try {
	    newRule = (Rule) this.getClass().newInstance();
	    map.put(this, newRule);
	} catch (Exception e) {
	    Debug.print(true, "Can't instantiate Rule type ", this.getClass());
	    Debug.stackTrace(e);
	    return null;
	}
	newRule.setName(_name);
	newRule.setComment(_comment);
	CharacterPrototype oldPrototype = this.getOwner();
	CharacterPrototype newPrototype
	    = (CharacterPrototype) map.get(oldPrototype);
	if (newPrototype == null)
	    newPrototype = oldPrototype;
	else if (newPrototype.isProxy() && fullCopy)
	    newPrototype.makeReal(oldPrototype, map);
	GeneralizedCharacter newSelfGC
	    = (GeneralizedCharacter) _selfGC.copy(map, false);
	newSelfGC.setH(_selfGC.getH());
	newSelfGC.setV(_selfGC.getV());
	newRule._selfGC = newSelfGC;
	int rtSize = _ruleTests.getNumberOfElements();
	for (int i = 0; i < rtSize; i++) {
	    RuleTest oldTest = (RuleTest) _ruleTests.getElementAt(i);
	    RuleTest newTest = (RuleTest) oldTest.copy(map, false);
	    newRule.addTest(newTest);
	}
	int raSize = _ruleActions.getNumberOfElements();
	for (int i = 0; i < raSize; i++) {
	    RuleAction oldAction = (RuleAction) _ruleActions.getElementAt(i);
	    RuleAction newAction = (RuleAction) oldAction.copy(map, false);
	    newRule.addAction(newAction);
	}
	if (_beforeBoard != null) {
	    BeforeBoard newBeforeBoard
		= (BeforeBoard) _beforeBoard.copy(map, false);
	    newRule.setBeforeBoard(newBeforeBoard);
	    if (_afterBoard != null) {
		AfterBoard newAfterBoard
		    = (AfterBoard) _afterBoard.copy(map, false);
		newRule.setAfterBoard(newAfterBoard);
	    }
	}
	return super.copy(map, fullCopy);
    }
    
    RuleListItemView createScrap(CocoaCharacter self) {
	RuleScrap scrap = new RuleScrap(this);
	this.addView(self, scrap);
	return scrap;
    }
    
    PlaywriteView createTestsView(boolean enableViews) {
	return _ruleTests.createView(enableViews);
    }
    
    PlaywriteView createActionsView(boolean enableViews) {
	return _ruleActions.createView(enableViews);
    }
    
    public PlaywriteView createView(int squareSize) {
	return new BeforeAfterView(this, squareSize, true);
    }
    
    PlaywriteView createMiniRuleView(Color backgroundColor) {
	if (_miniRuleImage == null)
	    _miniRuleImage
		= BeforeAfterView.createMiniRuleImage(this, backgroundColor);
	PlaywriteView miniView = new RuleScrap.MiniRuleView(_miniRuleImage);
	return miniView;
    }
    
    Image getMiniRuleImage(Color bgColor) {
	if (_miniRuleImage == null)
	    createMiniRuleView(bgColor);
	return _miniRuleImage;
    }
    
    void setMiniRuleDirty() {
	if (_miniRuleImage != null) {
	    _miniRuleImage.flush();
	    _miniRuleImage = null;
	}
    }
    
    AfterBoardView createAfterBoardView() {
	return (AfterBoardView) _afterBoard.createView();
    }
    
    void updateViews() {
	setMiniRuleDirty();
	if (this.hasViews()) {
	    final String name = getName();
	    this.getViewManager().updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    RuleScrap scrap = (RuleScrap) view;
		    scrap.setRuleNameField(name);
		    scrap.updateVisualRule();
		}
	    }, null);
	}
    }
    
    final void recompile() {
	if (_beforeBoard == null)
	    _beforeBoard = BeforeBoard.createFromRule(this, 16);
	else
	    _beforeBoard.rebuild();
	finishRecording();
    }
    
    Object iterate(RuleListItem.IterationProcessor ruleUpdater,
		   Object lastValue) {
	lastValue = ruleUpdater.processItem(this, lastValue);
	return lastValue;
    }
    
    public void add(Contained obj) {
	if (obj instanceof RuleAction)
	    addAction((RuleAction) obj);
	else if (obj instanceof RuleTest)
	    addTest((RuleTest) obj);
	else
	    throw new PlaywriteInternalError(this.getClass().toString()
					     + ": add of " + obj.getClass()
					     + " to Rule not supported");
    }
    
    public void remove(Contained obj) {
	if (obj instanceof RuleAction)
	    removeAction((RuleAction) obj);
	else if (obj instanceof RuleTest)
	    removeTest((RuleTest) obj);
	else
	    throw new PlaywriteInternalError(this.getClass().toString()
					     + ": remove of " + obj.getClass()
					     + " from Rule not supported");
    }
    
    public void update(Contained obj) {
	/* empty */
    }
    
    void finishRecording() {
	Vector newRuleTests = _beforeBoard.compile(_selfGC);
	int rtSize = _ruleTests.getNumberOfElements();
	for (int i = 0; i < rtSize; i++) {
	    Object test = (RuleTest) _ruleTests.getElementAt(i);
	    if (!(test instanceof BindTest))
		newRuleTests.addElement(test);
	}
	_ruleTests.empty();
	_ruleTests.add(newRuleTests);
	_beforeBoard.reset();
	if (_afterBoard != null)
	    _afterBoard.reset();
    }
    
    void cancelRecording(Rule original) {
	if (original != null) {
	    _name = original._name;
	    _selfGC = original._selfGC;
	    _ruleTests.empty();
	    original._ruleTests.moveContentsTo(_ruleTests);
	    _ruleActions.empty();
	    original._ruleActions.moveContentsTo(_ruleActions);
	    _beforeBoard = original._beforeBoard;
	    _afterBoard = original._afterBoard;
	    _beforeBoard.reset();
	    if (_afterBoard != null)
		_afterBoard.reset();
	}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_name);
	ASSERT.isNotNull(_selfGC);
	ASSERT.isNotNull(_beforeBoard);
	ASSERT.isNotNull(_ruleTests);
	ASSERT.isNotNull(_ruleActions);
	super.writeExternal(out);
	out.writeUTF(_name);
	out.writeUTF(_comment == null ? "" : _comment);
	out.writeObject(_selfGC);
	out.writeObject(_beforeBoard);
	int rtSize = _ruleTests.getNumberOfElements();
	out.writeInt(rtSize);
	for (int i = 0; i < rtSize; i++) {
	    RuleTest test = (RuleTest) _ruleTests.getElementAt(i);
	    if (test.getRule() != this)
		throw new BadBackpointerError(this, test);
	    out.writeObject(test);
	}
	int raSize = _ruleActions.getNumberOfElements();
	out.writeInt(raSize);
	for (int i = 0; i < raSize; i++) {
	    RuleAction action = (RuleAction) _ruleActions.getElementAt(i);
	    if (action.getRule() != this)
		throw new BadBackpointerError(this, action);
	    out.writeObject(action);
	}
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_name = in.readUTF();
	_comment = in.readUTF();
	if (_comment.equals(""))
	    _comment = null;
	_selfGC = (GeneralizedCharacter) in.readObject();
	_beforeBoard = (BeforeBoard) in.readObject();
	int size = in.readInt();
	Vector tests = new Vector(size);
	while (size-- > 0) {
	    Object test = in.readObject();
	    tests.addElement(test);
	}
	size = in.readInt();
	Vector actions = new Vector(size);
	while (size-- > 0) {
	    Object action = in.readObject();
	    actions.addElement(action);
	}
	size = tests.size();
	while (size-- > 0) {
	    RuleTest test = (RuleTest) tests.removeElementAt(0);
	    IndexedContainer indexedContainer = test.getIndexedContainer();
	    if (indexedContainer != null && indexedContainer != this) {
		Debug.print("debug.objectstore",
			    "warning: bad test; removing ", test, " from ",
			    indexedContainer, " to ", this);
		PlaywriteRoot.setBadWorldly(this);
		indexedContainer.forceRemove(test);
	    }
	    addTest(test);
	}
	size = actions.size();
	while (size-- > 0) {
	    RuleAction action = (RuleAction) actions.removeElementAt(0);
	    IndexedContainer indexedContainer = action.getIndexedContainer();
	    if (indexedContainer != null && indexedContainer != this) {
		Debug.print("debug.objectstore",
			    "warning: bad action; removing ", action, " from ",
			    indexedContainer, " to ", this);
		PlaywriteRoot.setBadWorldly(this);
		indexedContainer.forceRemove(action);
	    }
	    addAction(action);
	}
	if (_beforeBoard == null) {
	    _beforeBoard = BeforeBoard.createFromRule(this, 16);
	    Debug.print(true, "Rule ", _name,
			" has no before board.  Please save in this version");
	}
	ASSERT.isNotNull(_selfGC);
	ASSERT.isNotNull(_beforeBoard);
    }
    
    public String toString() {
	return "<Rule '" + _name + "'>";
    }
}
