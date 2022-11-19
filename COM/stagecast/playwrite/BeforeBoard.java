/* BeforeBoard - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.OperationManager;

class BeforeBoard extends Board implements Debug.Constants, Externalizable
{
    public static final Object DOWN = new Object();
    public static final Object UP = new Object();
    public static final Object LEFT = new Object();
    public static final Object RIGHT = new Object();
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108753970482L;
    private Shape _dontCare = null;
    private GeneralizedCharacter _selfGC;
    private transient AfterBoard _afterBoard = null;
    private transient Rect _stageSquares = null;
    private transient UpdateManager _growthWatcher;
    
    BeforeBoard(int width, int height, GeneralizedCharacter self, int sqSize) {
	super(width, height, self.getWorld(), sqSize);
	_growthWatcher = new UpdateManager();
	_selfGC = self;
	this.showGrid();
    }
    
    public BeforeBoard() {
	_growthWatcher = new UpdateManager();
    }
    
    static BeforeBoard createFromRule(Rule rule, int squareSize) {
	GeneralizedCharacter gc = null;
	int height = 0;
	int width = 0;
	int bottom = 0;
	int left = 0;
	int right = 0;
	int top = 0;
	World world = null;
	Enumeration tests = rule.getTests();
	while (tests.hasMoreElements()) {
	    RuleTest test = (RuleTest) tests.nextElement();
	    if (test instanceof BindTest) {
		BindTest bindTest = (BindTest) test;
		int h = bindTest.getDx();
		int v = bindTest.getDy();
		if (bindTest.isSelf()) {
		    gc = bindTest.getGC(0);
		    world = gc.getWorld();
		    Debug.print("debug.rule.translator", "got WORLD ");
		}
		if (h < left)
		    left = h;
		if (h > right)
		    right = h;
		if (v < bottom)
		    bottom = v;
		if (v > top)
		    top = v;
	    }
	}
	width = right - left + 1;
	height = top - bottom + 1;
	int selfX = width - right;
	int selfY = height - top;
	if (gc == null)
	    Debug.print
		("debug.rule.translator",
		 "Rule does not define a bindtest for the self character!");
	BeforeBoard bb = new BeforeBoard(width, height, gc, squareSize);
	Debug.print("debug.rule.translator", "width: ", width);
	Debug.print("debug.rule.translator", "height: ", height);
	Debug.print("debug.rule.translator", "selfX: ", selfX);
	Debug.print("debug.rule.translator", "selfY: ", selfY);
	Enumeration tests_0_ = rule.getTests();
	while (tests_0_.hasMoreElements()) {
	    RuleTest test = (RuleTest) tests_0_.nextElement();
	    if (test instanceof BindTest) {
		BindTest bindTest = (BindTest) test;
		Debug.print("debug.rule.translator",
			    ("test " + bindTest.getIndex()
			     + " is a BindTest; number of gc's = "
			     + bindTest.getGcSize()));
		int h = bindTest.getDx() + selfX;
		int v = bindTest.getDy() + selfY;
		if (bindTest.getDontCare())
		    bb.setDontCare(h, v, true);
		if (((BindTest) test).getGcSize() > 0) {
		    int gcSize = bindTest.getGcSize();
		    boolean isSelfTest = bindTest.isSelf();
		    if (!isSelfTest && gcSize > 0) {
			gc = bindTest.getGC(0);
			if (!bb.getCharacters().containsIdentical(gc)) {
			    Debug.print("debug.rule.translator", "adding GC ",
					gc);
			    bb.add(gc, h, v, -1);
			}
		    }
		    for (int k = 1; k < gcSize; k++) {
			gc = bindTest.getGC(k);
			if (!bb.getCharacters().containsIdentical(gc)) {
			    Debug.print("debug.rule.translator", "adding GC ",
					gc);
			    bb.add(gc, h, v, -1);
			}
		    }
		    if (isSelfTest && gcSize > 0) {
			gc = bindTest.getGC(0);
			if (!bb.getCharacters().containsIdentical(gc)) {
			    Debug.print("debug.rule.translator", "adding GC ",
					gc);
			    bb.add(gc, h, v, -1);
			}
		    }
		}
	    } else if (test instanceof BooleanTest) {
		Object booleanTest = ((BooleanTest) test).getTest();
		Debug.print("debug.rule.translator",
			    "test " + test.getIndex() + " is a BooleanTest");
		Debug.print("debug.rule.translator", "test = ", booleanTest);
		if (booleanTest instanceof OperationManager) {
		    OperationManager opManager
			= (OperationManager) booleanTest;
		    if (opManager.isSameOpAs(Op.Equal)) {
			Object rightSide = opManager.getRightSide();
			if (rightSide instanceof Appearance) {
			    Debug.print("debug.rule.translator",
					"it's an appearance test");
			    Object leftSide = opManager.getLeftSide();
			    if (opManager.getNot()) {
				if (leftSide instanceof VariableAlias) {
				    gc = ((GeneralizedCharacter)
					  ((VariableAlias) leftSide)
					      .getOwner());
				    Enumeration allAppearances
					= gc.getPrototype().getAppearances();
				    Appearance app;
				    for (app = ((Appearance)
						allAppearances.nextElement());
					 (allAppearances.hasMoreElements()
					  && app == (Appearance) rightSide);
					 app = ((Appearance)
						allAppearances
						    .nextElement())) {
					/* empty */
				    }
				    gc.setCurrentAppearance(app);
				    Debug.print("debug.rule.translator",
						"setting ", gc,
						"'s appearance to ",
						(Appearance) rightSide);
				}
			    } else if (leftSide instanceof VariableAlias) {
				gc = ((GeneralizedCharacter)
				      ((VariableAlias) leftSide).getOwner());
				gc.setCurrentAppearance((Appearance)
							rightSide);
				Debug.print("debug.rule.translator",
					    "setting ", gc,
					    "'s appearance to ",
					    (Appearance) rightSide);
			    }
			}
		    }
		}
	    }
	}
	if (rule.getBeforeBoard() == null)
	    rule.setBeforeBoard(bb);
	return bb;
    }
    
    final AfterBoard getAfterBoard() {
	return _afterBoard;
    }
    
    final void setAfterBoard(AfterBoard board) {
	_afterBoard = board;
    }
    
    final GeneralizedCharacter getSelfGC() {
	return _selfGC;
    }
    
    final Rect getStageSquares() {
	return _stageSquares;
    }
    
    final void setStageSquares(Rect rect) {
	_stageSquares = rect;
    }
    
    boolean isDontCareVisible() {
	return true;
    }
    
    final boolean hasDontCare() {
	return _dontCare != null;
    }
    
    final boolean getDontCare(int h, int v) {
	return _dontCare == null ? false : _dontCare.getLocationHV(h, v);
    }
    
    final void copyDontCare(BeforeBoard oldBoard) {
	if (oldBoard.hasDontCare())
	    _dontCare = (Shape) oldBoard._dontCare.clone();
    }
    
    final void setDontCare(int h, int v, boolean value) {
	if (_dontCare == null)
	    _dontCare = new Shape(this.numberOfColumns(), this.numberOfRows(),
				  null, false);
	_dontCare.setLocationHV(h, v, value);
    }
    
    final void translateDxDyToBoard(Point deltas) {
	deltas.x = _selfGC.getH() + deltas.x;
	deltas.y = _selfGC.getV() + deltas.y;
    }
    
    final boolean isMouseClickTestSquare(int h, int v) {
	MouseClickTest mt = getMouseTest(false);
	return mt.getShape().getLocationHV(h, v);
    }
    
    final MouseClickTest getMouseTest(boolean createNew) {
	Rule r = RuleEditor.ruleBeingDefined();
	if (r == null)
	    return null;
	MouseClickTest mtest = null;
	Enumeration tests = r.getTests();
	while (tests.hasMoreElements()) {
	    Object aTest = tests.nextElement();
	    if (aTest instanceof MouseClickTest) {
		mtest = (MouseClickTest) aTest;
		break;
	    }
	}
	if (createNew && mtest == null) {
	    mtest = new MouseClickTest(this);
	    r.addTest(mtest);
	}
	return mtest;
    }
    
    public void add(CocoaCharacter ch, int h, int v, int z) {
	this.add(ch, h, v, z, false);
    }
    
    public void addGrowthWatcher(Watcher watcher) {
	_growthWatcher.add(watcher);
    }
    
    public void removeGrowthWatcher(Watcher watcher) {
	_growthWatcher.remove(watcher);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Vector characters = this.getCharacters();
	BeforeBoard newBeforeBoard = (BeforeBoard) map.get(this);
	if (newBeforeBoard != null)
	    return newBeforeBoard;
	newBeforeBoard
	    = new BeforeBoard(this.numberOfColumns(), this.numberOfRows(),
			      ((GeneralizedCharacter)
			       getSelfGC().copy(map, fullCopy)),
			      this.getSquareSize());
	map.put(this, newBeforeBoard);
	for (int i = 0; i < characters.size(); i++) {
	    GeneralizedCharacter oldGC
		= (GeneralizedCharacter) characters.elementAt(i);
	    GeneralizedCharacter newGC
		= (GeneralizedCharacter) oldGC.copy(map, fullCopy);
	    newBeforeBoard.add(newGC, oldGC.getH(), oldGC.getV(), -1);
	}
	newBeforeBoard.copyDontCare(this);
	return newBeforeBoard;
    }
    
    void populate(Stage stage, Rect boundingRect) {
	int firstH = boundingRect.x;
	int firstV = boundingRect.y;
	Vector allChars = new Vector(5);
	_stageSquares = boundingRect;
	for (int h = 1; h <= boundingRect.width; h++) {
	    for (int v = 1; v <= boundingRect.height; v++) {
		Vector stageSquare
		    = stage.getSquare(firstH + h - 1, firstV + v - 1);
		if (stageSquare != null) {
		    for (int i = 0; i < stageSquare.size(); i++) {
			CharacterInstance ch
			    = (CharacterInstance) stageSquare.elementAt(i);
			if (ch.isVisible() && !isNewlyCreated(ch))
			    allChars.addElementIfAbsent(ch);
		    }
		}
	    }
	}
	allChars = stage.sortZ(allChars);
	for (int i = 0; i < allChars.size(); i++) {
	    CharacterInstance ch = (CharacterInstance) allChars.elementAt(i);
	    GeneralizedCharacter gch;
	    if (ch == _selfGC.getBinding())
		gch = _selfGC;
	    else
		gch = getGeneralizedCharacterFor(ch);
	    if (gch == null)
		gch = new GeneralizedCharacter(ch);
	    if (this.getCharacters().containsIdentical(gch)) {
		this.putInSquare(gch, gch.getH(), gch.getV(), false);
		this.setZ(gch, i);
	    } else
		add(gch, ch.getH() - firstH + 1, ch.getV() - firstV + 1, -1);
	}
    }
    
    Vector compile(GeneralizedCharacter self) {
	int selfH = self.getH();
	int selfV = self.getV();
	Vector bindTests = new Vector(10);
	if (_dontCare != null && _dontCare.isEmpty())
	    _dontCare = null;
	Vector square = (Vector) this.getSquare(selfH, selfV).clone();
	square.removeElement(self);
	boolean matchExactly = getDontCare(selfH, selfV) ^ true;
	bindTests.addElement(new BindTest(self, square, matchExactly));
	for (int v = 1; v <= this.numberOfRows(); v++) {
	    for (int h = 1; h <= this.numberOfColumns(); h++) {
		square = this.getSquare(h, v);
		matchExactly = getDontCare(h, v) ^ true;
		if (h != selfH || v != selfV) {
		    if (square.isEmpty())
			bindTests.addElement(new BindTest((Vector) null,
							  matchExactly,
							  h - selfH,
							  v - selfV));
		    else
			bindTests.addElement(new BindTest(square, matchExactly,
							  h - selfH,
							  v - selfV));
		}
	    }
	}
	return bindTests;
    }
    
    void reset() {
	_stageSquares = null;
	this.getWorld().resetGeneralizedCharacters();
    }
    
    void growLeftBy(int nSquares) {
	if (_dontCare != null)
	    _dontCare.growLeftBy(nSquares);
	super.growLeftBy(nSquares);
	_growthWatcher.update(this, LEFT);
    }
    
    void growDownBy(int nSquares) {
	if (_dontCare != null)
	    _dontCare.growDownBy(nSquares);
	super.growDownBy(nSquares);
	_growthWatcher.update(this, DOWN);
    }
    
    void growUpBy(int nSquares) {
	super.growUpBy(nSquares);
	_growthWatcher.update(this, UP);
    }
    
    void growRightBy(int nSquares) {
	super.growRightBy(nSquares);
	_growthWatcher.update(this, RIGHT);
    }
    
    public void changeSize(int newColumns, int newRows) {
	if (_dontCare != null)
	    _dontCare.changeSize(newColumns, newRows);
	super.changeSize(newColumns, newRows);
    }
    
    GeneralizedCharacter getGeneralizedCharacterFor(CharacterInstance ch) {
	Vector characters = this.getCharacters();
	for (int i = 0; i < characters.size(); i++) {
	    GeneralizedCharacter gch
		= (GeneralizedCharacter) characters.elementAt(i);
	    if (gch.getBinding() == ch)
		return gch;
	}
	if (_afterBoard == null)
	    return null;
	return _afterBoard.getGeneralizedCharacterFor(ch);
    }
    
    private boolean isNewlyCreated(CharacterInstance ch) {
	return _afterBoard != null && _afterBoard.isNewlyCreated(ch);
    }
    
    GeneralizedCharacter findReferenceTo(ReferencedObject obj) {
	Vector characters = this.getCharacters();
	int n = characters.size();
	for (int i = 0; i < n; i++) {
	    GeneralizedCharacter gch
		= (GeneralizedCharacter) characters.elementAt(i);
	    if (gch.refersTo(obj))
		return gch;
	}
	return null;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_selfGC);
	super.writeExternal(out);
	out.writeObject(_dontCare);
	out.writeObject(_selfGC);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	WorldInStream wis = (WorldInStream) in;
	int version = wis.loadVersion(BeforeBoard.class);
	super.readExternal(wis);
	this.fillInObject(this.numberOfColumns(), this.numberOfRows(),
			  this.getWorld(), this.getSquareSize());
	this.showGrid();
	_dontCare = (Shape) wis.readObject();
	_selfGC = (GeneralizedCharacter) wis.readObject();
	switch (version) {
	case 1: {
	    Vector chars = wis.readVector();
	    for (int i = 0; i < chars.size(); i++) {
		CocoaCharacter ch = (CocoaCharacter) chars.elementAt(i);
		if (!this.getCharacters().containsIdentical(ch))
		    add(ch, ch.getH(), ch.getV(), -1);
	    }
	    break;
	}
	case 2:
	    this.rebuildCharacterList();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 2);
	}
	CocoaCharacter
	    .notifyReadCompleted(wis, this.getCharacters().elementArray());
    }
    
    public String toString() {
	return "<BeforeBoard>";
    }
}
