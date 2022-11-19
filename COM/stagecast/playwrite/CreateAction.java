/* CreateAction - Decompiled by JODE
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

class CreateAction extends RuleAction
    implements Debug.Constants, Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108755871026L;
    private int _dx;
    private int _dy;
    private int _z;
    private transient CharacterInstance _newCharacter = null;
    
    CreateAction(GeneralizedCharacter gch, int dx, int dy, int z) {
	this.setTarget(gch);
	_dx = dx;
	_dy = dy;
	_z = z;
    }
    
    CreateAction(GeneralizedCharacter gch, int deltaX, int deltaY) {
	this(gch, deltaX, deltaY, -1);
    }
    
    public CreateAction() {
	/* empty */
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	GeneralizedCharacter newCharacterGC = this.getTarget();
	CharacterPrototype prototype = newCharacterGC.getPrototype();
	if (prototype.isProxy())
	    return RuleAction.FAILURE;
	_newCharacter = prototype.makeInstance();
	_newCharacter
	    .setCurrentAppearance(newCharacterGC.getCurrentAppearance());
	container.add(_newCharacter, baseX + _dx, baseY + _dy, _z);
	newCharacterGC.bind(_newCharacter);
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	if (_newCharacter != null) {
	    _newCharacter.getCharContainer().deleteCharacter(_newCharacter);
	    this.getTarget().unbind();
	}
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	GCAlias alias = this.getTarget().getAfterBoardCharacter();
	if (alias == null || afterBoard.isDeleted(alias))
	    alias = new GCAlias(this.getTarget());
	GeneralizedCharacter selfGC = afterBoard.getBeforeBoard().getSelfGC();
	afterBoard.createCharacter(alias, selfGC.getH() + _dx,
				   selfGC.getV() + _dy);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	CreateAction newAction = (CreateAction) map.get(this);
	if (newAction != null)
	    return newAction;
	GeneralizedCharacter newCharGC
	    = (GeneralizedCharacter) this.getTarget().copy(map, true);
	newAction = new CreateAction(newCharGC, _dx, _dy, _z);
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	return true;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(this.getTarget());
	super.writeExternal(out);
	out.writeInt(_dx);
	out.writeInt(_dy);
	out.writeInt(_z);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_dx = in.readInt();
	_dy = in.readInt();
	_z = in.readInt();
    }
    
    public void summarize(Summary s) {
	GeneralizedCharacter self = this.getSelf();
	String location
	    = new Point(self.getH() + _dx, self.getV() + _dy).toString();
	s.writeFormat("create action fmt", null,
		      new Object[] { this.getTarget(), location });
    }
    
    public PlaywriteView createView() {
	GeneralizedCharacter self = this.getSelf();
	View grid = new LocationView(this.getRule().getBeforeBoard(),
				     this.getTarget(), self.getH() + _dx,
				     self.getV() + _dy);
	return new LineView(this, 8, "create action fmt", null,
			    new View[] { this.getTarget().createIcon(),
					 grid });
    }
    
    public String toString() {
	String result = null;
	try {
	    result = ("<Create " + this.getTarget() + " at (" + _dx + "," + _dy
		      + ")>");
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
