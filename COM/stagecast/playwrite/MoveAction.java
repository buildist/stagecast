/* MoveAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class MoveAction extends RuleAction
    implements Debug.Constants, Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108755739954L;
    private CharacterContainer _newContainer = null;
    private int _dx;
    private int _dy;
    private transient CharacterInstance _movedCharacter = null;
    private transient CharacterContainer _oldContainer = null;
    private transient int _oldX;
    private transient int _oldY;
    private transient int _oldZ;
    
    MoveAction(GeneralizedCharacter gch, CharacterContainer container, int dx,
	       int dy) {
	this.setTarget(gch);
	_newContainer = container;
	_dx = dx;
	_dy = dy;
    }
    
    MoveAction(GeneralizedCharacter gch, int dx, int dy) {
	this(gch, null, dx, dy);
    }
    
    public MoveAction() {
	/* empty */
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	_movedCharacter = this.getTarget().getBinding();
	Object result;
	if (_movedCharacter == null)
	    result = RuleAction.FAILURE;
	else {
	    _oldContainer = _movedCharacter.getCharContainer();
	    _oldX = _movedCharacter.getH();
	    _oldY = _movedCharacter.getV();
	    _oldZ = _oldContainer.getZ(_movedCharacter);
	    int newX = baseX + _dx;
	    int newY = baseY + _dy;
	    int newZ = -1;
	    if (_newContainer == null)
		_movedCharacter.moveTo(_oldContainer, newX, newY, newZ);
	    else
		_movedCharacter.moveTo(_newContainer, newX, newY, newZ);
	    if (_newContainer == _oldContainer && newX == _oldX
		&& newY == _oldY && _movedCharacter.getZ() == _oldZ)
		result = RuleAction.NOOP;
	    else
		result = RuleAction.SUCCESS;
	}
	return result;
    }
    
    public void undo() {
	if (_movedCharacter != null)
	    _movedCharacter.moveTo(_oldContainer, _oldX, _oldY, _oldZ);
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	GCAlias alias = this.getTarget().getAfterBoardCharacter();
	if (!afterBoard.isDeleted(alias)) {
	    GeneralizedCharacter selfGC
		= afterBoard.getBeforeBoard().getSelfGC();
	    alias.moveTo(afterBoard, selfGC.getH() + _dx, selfGC.getV() + _dy,
			 -1);
	}
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	MoveAction newAction = (MoveAction) map.get(this);
	if (newAction != null)
	    return newAction;
	GeneralizedCharacter newGC
	    = (GeneralizedCharacter) this.getTarget().copy(map, fullCopy);
	CharacterContainer newContainer;
	if (_newContainer instanceof Stage)
	    newContainer = (Stage) ((Stage) _newContainer).copy(map, fullCopy);
	else {
	    Debug.print("debug.copy", "Can't copy container ", _newContainer,
			" in ", this);
	    newContainer = _newContainer;
	}
	newAction = new MoveAction(newGC, newContainer, _dx, _dy);
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	return true;
    }
    
    public void summarize(Summary s) {
	GeneralizedCharacter self = this.getSelf();
	s.writeFormat("move action fmt", null,
		      new Object[] { this.getTarget(),
				     new Point(self.getH() + _dx,
					       self.getV() + _dy) });
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(this.getTarget());
	super.writeExternal(out);
	out.writeObject(_newContainer);
	out.writeInt(_dx);
	out.writeInt(_dy);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_newContainer = (CharacterContainer) in.readObject();
	_dx = in.readInt();
	_dy = in.readInt();
    }
    
    public PlaywriteView createView() {
	GeneralizedCharacter self = this.getSelf();
	GeneralizedCharacter target = this.getTarget();
	ASSERT.isNotNull(target);
	GCAlias gca = target.getAfterBoardCharacter();
	if (gca == null)
	    gca = new GCAlias(target);
	View grid = new LocationView(this.getRule().getBeforeBoard(), gca,
				     self.getH() + _dx, self.getV() + _dy);
	return new LineView(this, 8, "move action fmt", null,
			    new View[] { this.getTarget().createIcon(),
					 grid });
    }
    
    public String toString() {
	String result = null;
	try {
	    result = ("<Move " + this.getTarget() + " by (" + _dx + "," + _dy
		      + ")>");
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
