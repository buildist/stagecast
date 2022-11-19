/* AfterBoard - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public class AfterBoard extends Board implements Debug.Constants
{
    private BeforeBoard _beforeBoard;
    private Vector _createdCharacters = null;
    private Vector _deletedCharacters = null;
    private Rect _minRect;
    private boolean _showDontCare;
    
    AfterBoard(BeforeBoard bb, int minSquareH, int minSquareV) {
	super(bb.numberOfColumns(), bb.numberOfRows(), bb.getWorld(),
	      bb.getSquareSize());
	setBeforeBoard(bb);
	bb.setAfterBoard(this);
	this.showGrid();
	this.setBackgroundColor(bb.getBackgroundColor());
	setShowDontCare(false);
	_minRect = new Rect(minSquareH, minSquareV, 1, 1);
    }
    
    AfterBoard(BeforeBoard bb) {
	this(bb, bb.getSelfGC().getH(), bb.getSelfGC().getV());
	if (bb.getSelfGC().getH() == 0 || bb.getSelfGC().getV() == 0)
	    throw new PlaywriteInternalError
		      ("AfterBoard: BeforeBoard's self character has invalid h or v (==0).");
    }
    
    static AfterBoard createFromRule(Rule rule, BeforeBoard bb) {
	Variable.setDeferredUpdates(true);
	AfterBoard afterBoard;
	try {
	    afterBoard = new AfterBoard(bb, rule.getSelf().getH(),
					rule.getSelf().getV());
	    afterBoard.populate();
	    for (int i = 0; i < rule.getNumberOfActions(); i++) {
		RuleAction action = rule.getRuleAction(i);
		action.updateAfterBoard(afterBoard);
	    }
	    if (rule.getAfterBoard() == null)
		rule.setAfterBoard(afterBoard);
	} finally {
	    Variable.setDeferredUpdates(false);
	}
	return afterBoard;
    }
    
    final BeforeBoard getBeforeBoard() {
	return _beforeBoard;
    }
    
    final void setBeforeBoard(BeforeBoard board) {
	_beforeBoard = board;
    }
    
    protected void setShowDontCare(boolean showEm) {
	_showDontCare = showEm;
    }
    
    final boolean isDontCareVisible() {
	return _showDontCare;
    }
    
    final Stage getStage() {
	if (RuleEditor.isRecordingOrEditing() && !RuleEditor.isRuleEditing())
	    return RuleEditor.getSelfContainer();
	return null;
    }
    
    public void add(GCAlias alias, int h, int v, int z) {
	this.add(alias, h, v, z, false);
    }
    
    public void putInSquare(CocoaCharacter ch, int h, int v,
			    boolean validate) {
	super.putInSquare(ch, h, v, false);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Vector characters = this.getCharacters();
	GCAlias oldAlias = null;
	GCAlias newAlias = null;
	AfterBoard newAfterBoard = (AfterBoard) map.get(this);
	if (newAfterBoard != null)
	    return newAfterBoard;
	BeforeBoard newBeforeBoard = (BeforeBoard) map.get(_beforeBoard);
	if (newBeforeBoard == null)
	    throw new PlaywriteInternalError
		      ("After Board being copied before its Before Board");
	newAfterBoard = new AfterBoard(newBeforeBoard, 0, 0);
	map.put(this, newAfterBoard);
	for (int i = 0; i < characters.size(); i++) {
	    oldAlias = (GCAlias) characters.elementAt(i);
	    newAlias = (GCAlias) oldAlias.copy(map, fullCopy);
	    newAfterBoard.add(newAlias, oldAlias.getH(), oldAlias.getV(), -1);
	}
	if (_createdCharacters != null) {
	    newAfterBoard._createdCharacters
		= new Vector(_createdCharacters.size());
	    for (int i = 0; i < _createdCharacters.size(); i++) {
		oldAlias = (GCAlias) _createdCharacters.elementAt(i);
		newAlias = (GCAlias) oldAlias.copy(map, fullCopy);
		newAfterBoard._createdCharacters.addElement(newAlias);
	    }
	}
	if (_deletedCharacters != null) {
	    newAfterBoard._deletedCharacters
		= new Vector(_deletedCharacters.size());
	    for (int i = 0; i < _deletedCharacters.size(); i++) {
		oldAlias = (GCAlias) _deletedCharacters.elementAt(i);
		newAlias = (GCAlias) oldAlias.copy(map, fullCopy);
		newAlias.setH(oldAlias.getH());
		newAlias.setV(oldAlias.getV());
		newAfterBoard._deletedCharacters.addElement(newAlias);
	    }
	}
	newAfterBoard.setMinRect(new Rect(_minRect));
	return newAfterBoard;
    }
    
    void expand(int direction, int count) {
	if (count != 0) {
	    Stage stage = getStage();
	    Rect stageSquares = getBeforeBoard().getStageSquares();
	    switch (direction) {
	    case 1:
		Debug.print("debug.afterboard", "growing left by ", count);
		if (stageSquares != null) {
		    stageSquares.x = stageSquares.x - count;
		    if (stageSquares.x < 1) {
			int n = 1 - stageSquares.x;
			count -= n;
			stageSquares.x = 1;
		    }
		    stageSquares.width = stageSquares.width + count;
		}
		_beforeBoard.growLeftBy(count);
		_minRect.x += count;
		this.growLeftBy(count);
		break;
	    case 2:
		Debug.print("debug.afterboard", "growing right by ", count);
		if (stageSquares != null) {
		    stageSquares.width = stageSquares.width + count;
		    if (stage != null
			&& (stageSquares.x + stageSquares.width - 1
			    > stage.numberOfColumns())) {
			int n = (stageSquares.x + stageSquares.width - 1
				 - stage.numberOfColumns());
			count -= n;
			stageSquares.width = stageSquares.width - n;
		    }
		}
		_beforeBoard.growRightBy(count);
		this.growRightBy(count);
		break;
	    case 3:
		Debug.print("debug.afterboard", "growing up by ", count);
		if (stageSquares != null) {
		    stageSquares.height = stageSquares.height + count;
		    if (stage != null
			&& (stageSquares.y + stageSquares.height - 1
			    > stage.numberOfRows())) {
			int n = (stageSquares.y + stageSquares.height - 1
				 - stage.numberOfRows());
			count -= n;
			stageSquares.height = stageSquares.height - n;
		    }
		}
		_beforeBoard.growUpBy(count);
		this.growUpBy(count);
		break;
	    case 4:
		Debug.print("debug.afterboard", "growing down by ", count);
		if (stageSquares != null) {
		    stageSquares.y = stageSquares.y - count;
		    if (stageSquares.y < 1) {
			int n = 1 - stageSquares.y;
			count -= n;
			stageSquares.y = 1;
		    }
		    stageSquares.height = stageSquares.height + count;
		}
		_beforeBoard.growDownBy(count);
		_minRect.y += count;
		this.growDownBy(count);
		break;
	    }
	    if (stageSquares != null) {
		_beforeBoard.populate(RuleEditor.getSelfContainer(),
				      stageSquares);
		populate();
	    }
	    PlaywriteRoot.disableAllWindows(this.getWorld());
	}
    }
    
    public void constrainRectToStage(Rect newBounds, Stage stage) {
	/* empty */
    }
    
    void populate() {
	Vector characters = _beforeBoard.getCharacters();
	for (int i = 0; i < characters.size(); i++) {
	    GeneralizedCharacter gch
		= (GeneralizedCharacter) characters.elementAt(i);
	    GCAlias alias = gch.getAfterBoardCharacter();
	    if (alias == null)
		alias = new GCAlias(gch);
	    if (this.getCharacters().containsIdentical(alias))
		putInSquare(alias, alias.getH(), alias.getV(), false);
	    else if (_deletedCharacters == null
		     || !_deletedCharacters.containsIdentical(alias))
		add(alias, gch.getH(), gch.getV(), i);
	}
    }
    
    void depopulate() {
	Vector characters = (Vector) this.getCharacters().clone();
	int i = characters.size();
	while (i-- > 0)
	    super.deleteCharacter((GCAlias) characters.elementAt(i));
	_createdCharacters = null;
	_deletedCharacters = null;
    }
    
    public void deleteCharactersOutOfBounds() {
	Rect logicalBounds = _beforeBoard.getStageSquares();
	Stage stage = getStage();
	Vector outOfBoundsCharacters = new Vector(1);
	if (stage != null) {
	    for (int h = logicalBounds.x; h < logicalBounds.maxX(); h++) {
		for (int v = logicalBounds.y; v < logicalBounds.maxY(); v++) {
		    if (h > stage.getNumberOfColumns()
			|| v > stage.getNumberOfRows()) {
			int localH = h - logicalBounds.x + 1;
			int localV = v - logicalBounds.y + 1;
			Vector square = this.getSquare(localH, localV);
			outOfBoundsCharacters.addElementsIfAbsent(square);
		    }
		}
	    }
	    for (int i = 0; i < outOfBoundsCharacters.size(); i++)
		deleteCharacter((GCAlias) outOfBoundsCharacters.elementAt(i));
	    outOfBoundsCharacters.removeAllElements();
	}
    }
    
    void createCharacter(GCAlias alias, int x, int y) {
	add(alias, x, y, -1);
	if (_createdCharacters == null)
	    _createdCharacters = new Vector(1);
	_createdCharacters.addElement(alias);
    }
    
    void deleteCharacter(GCAlias alias) {
	if (!isDeleted(alias)) {
	    Appearance app = alias.getCurrentAppearance();
	    addAppearanceRectToMinRect(new Rect(app.left(alias.getH()),
						app.bottom(alias.getV()),
						app.getLogicalWidth(),
						app.getLogicalHeight()));
	    this.remove(alias);
	    if (_deletedCharacters == null)
		_deletedCharacters = new Vector(1);
	    _deletedCharacters.addElement(alias);
	}
    }
    
    final boolean isDeleted(GCAlias alias) {
	return alias == null ? true : alias.getContainer() == null;
    }
    
    private void addAppearanceRectToMinRect(Rect appRect) {
	addAppearanceRectToRect(_minRect, appRect);
    }
    
    private void addAppearanceRectToRect(Rect original, Rect appRect) {
	if (!appRect.intersects(original)) {
	    if (original.x >= appRect.maxX()) {
		int dx = original.x - appRect.maxX() + 1;
		original.x -= dx;
		original.width += dx;
	    } else if (original.maxX() <= appRect.x)
		original.width += appRect.x - original.maxX() + 1;
	    if (original.y >= appRect.y + appRect.height) {
		int dy = original.y - appRect.maxY() + 1;
		original.y -= dy;
		original.height += dy;
	    } else if (original.maxY() <= appRect.y)
		original.height += appRect.y - original.maxY() + 1;
	}
    }
    
    boolean isNewlyCreated(CharacterInstance ch) {
	return getGeneralizedCharacterFor(ch) != null;
    }
    
    GeneralizedCharacter getGeneralizedCharacterFor(CharacterInstance ch) {
	if (_createdCharacters == null)
	    return null;
	for (int i = 0; i < _createdCharacters.size(); i++) {
	    GCAlias alias = (GCAlias) _createdCharacters.elementAt(i);
	    GeneralizedCharacter gch = alias.findOriginal();
	    if (gch.getBinding() == ch)
		return gch;
	}
	return null;
    }
    
    boolean isInSpotlight(CocoaCharacter ch) {
	World world = this.getWorld();
	if (RuleEditor.isRuleEditing()) {
	    Vector charactersInSpotlight = this.getCharacters();
	    return charactersInSpotlight.containsIdentical(ch);
	}
	if (RuleEditor.isRecordingOrEditing()) {
	    Vector characters = this.getCharacters();
	    for (int i = 0; i < characters.size(); i++) {
		GCAlias alias = (GCAlias) characters.elementAt(i);
		if (alias.dereference() == ch)
		    return true;
	    }
	}
	return false;
    }
    
    boolean wasDeletedFromSpotlight(CocoaCharacter ch) {
	if (RuleEditor.isRecordingOrEditing() && _deletedCharacters != null)
	    return _deletedCharacters.containsIdentical(ch);
	return false;
    }
    
    void reset() {
	/* empty */
    }
    
    void addSquareToMinRect(int h, int v) {
	_minRect.unionWith(new Rect(h, v, 1, 1));
    }
    
    Rect getMinRectForView(int squareSize) {
	Rect result = new Rect(_minRect);
	if (RuleEditor.isRecordingOrEditing()) {
	    Vector characters = this.getCharacters();
	    Rect appRect = new Rect();
	    Rule rule = RuleEditor.getRuleEditor().getRule();
	    boolean addIt = false;
	    for (int i = 0; i < characters.size(); i++) {
		GCAlias ch = (GCAlias) characters.elementAt(i);
		GeneralizedCharacter gch = ch.findOriginal();
		Appearance app = ch.getCurrentAppearance();
		appRect.setBounds(app.left(ch.getH()), app.bottom(ch.getV()),
				  app.getLogicalWidth(),
				  app.getLogicalHeight());
		if (!appRect.intersects(result)) {
		    Enumeration tests = rule.getTests();
		    while (tests.hasMoreElements()) {
			RuleTest test = (RuleTest) tests.nextElement();
			if (!(test instanceof BindTest)
			    && test.refersTo(gch)) {
			    addIt = true;
			    break;
			}
		    }
		    if (!addIt) {
			Enumeration actions = rule.getActions();
			while (actions.hasMoreElements()) {
			    RuleAction action
				= (RuleAction) actions.nextElement();
			    if (action.refersTo(gch)) {
				addIt = true;
				break;
			    }
			}
		    }
		    if (addIt) {
			addAppearanceRectToRect(result, appRect);
			addIt = false;
		    }
		}
	    }
	}
	int x = (result.x - 1) * squareSize;
	int y = ((this.numberOfRows() - (result.y + result.height - 1))
		 * squareSize);
	result.setBounds(x, y, result.width * squareSize,
			 result.height * squareSize);
	return result;
    }
    
    private void setMinRect(Rect r) {
	_minRect = r;
    }
    
    GCAlias findReferenceTo(ReferencedObject obj) {
	if (_createdCharacters == null)
	    return null;
	for (int i = 0; i < _createdCharacters.size(); i++) {
	    GCAlias alias = (GCAlias) _createdCharacters.elementAt(i);
	    if (alias.refersTo(obj))
		return alias;
	}
	return null;
    }
    
    public PlaywriteView createView() {
	return new AfterBoardView(this);
    }
    
    public String toString() {
	return "<AfterBoard>";
    }
}
