/* BindTest - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

class BindTest extends RuleTest implements Externalizable
{
    static final boolean matchExactlyFlag = true;
    static final boolean dontCareFlag = false;
    static final Vector emptySquare = null;
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108755150130L;
    private int gcSize;
    private GeneralizedCharacter[] generalizedCharacters;
    private boolean matchExactly;
    private int dx;
    private int dy;
    private boolean isSelfSquare = false;
    
    BindTest(GeneralizedCharacter self, Vector others, boolean matchExact) {
	isSelfSquare = true;
	if (others == null)
	    others = makeUnitSquare(self);
	else if (others.isEmpty())
	    others.addElement(self);
	else
	    others.insertElementAt(self, 0);
	initialize(others, matchExact, 0, 0);
    }
    
    BindTest(GeneralizedCharacter self, boolean matchExact) {
	this(self, null, matchExact);
    }
    
    public BindTest() {
	/* empty */
    }
    
    BindTest(Vector square, boolean matchExact, int deltaX, int deltaY) {
	initialize(square, matchExact, deltaX, deltaY);
    }
    
    BindTest(GeneralizedCharacter genChar, boolean matchExact, int deltaX,
	     int deltaY) {
	initialize(makeUnitSquare(genChar), matchExact, deltaX, deltaY);
    }
    
    private void initialize(Vector square, boolean matchExact, int deltaX,
			    int deltaY) {
	if (square == null)
	    gcSize = 0;
	else
	    gcSize = square.size();
	if (gcSize == 0)
	    generalizedCharacters = null;
	else {
	    generalizedCharacters = new GeneralizedCharacter[gcSize];
	    square.copyInto(generalizedCharacters);
	}
	matchExactly = matchExact;
	dx = deltaX;
	dy = deltaY;
	if (dx == 0 && dy == 0 && !isSelfSquare)
	    throw new PlaywriteInternalError
		      ("A self-binding square MUST use the constructor without coordinates!");
    }
    
    public PlaywriteView createView() {
	return null;
    }
    
    void showTestResult$(boolean success) {
	if (this.getView() != null)
	    ((BoardView) this.getView()).addTestedSquare(dx, dy, success);
    }
    
    void resetView$() {
	if (this.getView() != null)
	    ((BoardView) this.getView()).resetTestedSquares();
    }
    
    final int getGcSize() {
	return gcSize;
    }
    
    final GeneralizedCharacter getGC(int i) {
	return generalizedCharacters[i];
    }
    
    final boolean isSelf() {
	return isSelfSquare;
    }
    
    final int getDx() {
	return dx;
    }
    
    final int getDy() {
	return dy;
    }
    
    final boolean isForSquare(int testdx, int testdy) {
	return testdx == dx && testdy == dy;
    }
    
    final boolean getMatchExactly() {
	return matchExactly;
    }
    
    final boolean getDontCare() {
	return matchExactly ^ true;
    }
    
    public boolean evaluate(CharacterInstance self) {
	Vector square;
	if (isSelfSquare)
	    square = ((Stage) self.getContainer()).getSquareData(self.getH(),
								 self.getV());
	else {
	    square = self.adjacentSquare(dx, dy);
	    if (square == null)
		return false;
	}
	int nVisible = numberOfVisibleCharacters(square);
	if (gcSize == 0)
	    return nVisible == 0 || matchExactly == false;
	if (nVisible == 0)
	    return false;
	if (matchExactly == true && nVisible != gcSize)
	    return false;
	World world = self.getWorld();
	int squareSize = square.size();
	boolean[] hasBeenBound = world.resetValues(square);
	int lastGC = 0;
	if (isSelfSquare) {
	    if (self.isInvisible())
		return false;
	    if (generalizedCharacters[0].bind(self))
		hasBeenBound[square.indexOfIdentical(self)] = true;
	    else
		throw new PlaywriteInternalError
			  ("BindTest.evaluate on " + self.getName()
			   + " couldn't bind SELF! rule = " + this.getRule()
			   + " on " + this.getRule().getOwner());
	    lastGC = 1;
	}
	int i = gcSize;
    while_3_:
	while (i-- > lastGC) {
	    GeneralizedCharacter gch = generalizedCharacters[i];
	    CharacterInstance binding = gch.getBinding();
	    if (binding == null) {
		int j = squareSize;
		while (j-- > 0) {
		    if (!hasBeenBound[j]
			&& gch.bind((CharacterInstance) square.elementAt(j))) {
			hasBeenBound[j] = true;
			continue while_3_;
		    }
		}
		return false;
	    }
	    if (!square.containsIdentical(binding))
		return false;
	}
	return true;
    }
    
    private int _indexOfIdentical(CharacterInstance[] squares,
				  CharacterInstance ch) {
	for (int i = 0; i < squares.length; i++) {
	    if (squares[i] == ch)
		return i;
	}
	return -1;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	BindTest newTest = (BindTest) map.get(this);
	if (newTest != null)
	    return newTest;
	GeneralizedCharacter newSelf
	    = (GeneralizedCharacter) map.get(this.getSelf());
	if (newSelf == null)
	    throw new PlaywriteInternalError
		      ("Trying to copy a bind test before the self character has been copied");
	if (isSelfSquare)
	    newTest = new BindTest(newSelf, copyGCs(map, fullCopy, 1),
				   matchExactly);
	else
	    newTest = new BindTest(copyGCs(map, fullCopy, 0), matchExactly, dx,
				   dy);
	map.put(this, newTest);
	return newTest;
    }
    
    private Vector copyGCs(Hashtable map, boolean fullCopy, int startIndex) {
	if (gcSize == startIndex)
	    return null;
	Vector newGCs = new Vector(gcSize);
	for (int i = startIndex; i < gcSize; i++)
	    newGCs.addElement(generalizedCharacters[i].copy(map, fullCopy));
	return newGCs;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	for (int i = 0; i < gcSize; i++) {
	    if (generalizedCharacters[i].refersTo(obj))
		return true;
	}
	return false;
    }
    
    private int numberOfVisibleCharacters(Vector sq) {
	int n = sq.size();
	int count = n;
	while (--n >= 0) {
	    if (((CocoaCharacter) sq.elementAt(n)).isInvisible())
		count--;
	}
	return count;
    }
    
    private Vector makeUnitSquare(Object obj) {
	Vector v = new Vector(1);
	v.addElement(obj);
	return v;
    }
    
    public boolean isDisplayedInBeforeBoard() {
	return true;
    }
    
    Point getCoordinate() {
	return new Point(dx, dy);
    }
    
    GeneralizedCharacter getSelfifSelf() {
	if (isSelfSquare)
	    return generalizedCharacters[0];
	return null;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(gcSize);
	for (int i = 0; i < gcSize; i++) {
	    GeneralizedCharacter gc = generalizedCharacters[i];
	    ASSERT.isNotNull(gc);
	    out.writeObject(gc);
	}
	out.writeBoolean(matchExactly);
	out.writeInt(dx);
	out.writeInt(dy);
	out.writeBoolean(isSelfSquare);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	gcSize = in.readInt();
	generalizedCharacters = new GeneralizedCharacter[gcSize];
	for (int i = 0; i < gcSize; i++)
	    generalizedCharacters[i] = (GeneralizedCharacter) in.readObject();
	matchExactly = in.readBoolean();
	dx = in.readInt();
	dy = in.readInt();
	isSelfSquare = in.readBoolean();
    }
    
    public String toString() {
	String gcs = null;
	String result = null;
	try {
	    if (gcSize > 0) {
		gcs = "[" + generalizedCharacters[0];
		for (int i = 1; i < gcSize; i++)
		    gcs += ", " + generalizedCharacters[i];
		gcs += "]";
	    }
	    result = ("<BindTest (" + dx + "," + dy + ")"
		      + (matchExactly ? " exactly " : " don't-care ")
		      + (generalizedCharacters == null ? "empty" : gcs) + ">");
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
