/* CopyAction - Decompiled by JODE
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

class CopyAction extends RuleAction
    implements Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108755936562L;
    private CharacterInstance _newCharacter = null;
    private int _dx;
    private int _dy;
    private int _z;
    private GeneralizedCharacter _newCharacterGC = null;
    
    CopyAction(GeneralizedCharacter source, GeneralizedCharacter dest, int dx,
	       int dy, int z) {
	this.setTarget(source);
	_newCharacterGC = dest;
	_dx = dx;
	_dy = dy;
	_z = z;
	synchronizeCopyGC();
    }
    
    public CopyAction() {
	/* empty */
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	CharacterInstance source = this.getTarget().getBinding();
	if (source == null)
	    return RuleAction.FAILURE;
	if (source.getPrototype().isProxy())
	    return RuleAction.FAILURE;
	_newCharacter = (CharacterInstance) source.copy(null, true);
	container.add(_newCharacter, baseX + _dx, baseY + _dy, _z);
	_newCharacterGC.bind(_newCharacter);
	if (!_newCharacterGC.isBound()) {
	    synchronizeCopyGC();
	    _newCharacterGC.bind(_newCharacter);
	    if (!_newCharacterGC.isBound())
		throw new PlaywriteInternalError
			  ("CopyAction couldn't bind the new character");
	}
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	if (_newCharacter != null) {
	    _newCharacter.getCharContainer().deleteCharacter(_newCharacter);
	    _newCharacterGC.unbind();
	}
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	GCAlias copyAlias = _newCharacterGC.getAfterBoardCharacter();
	GCAlias originalAlias = this.getTarget().getAfterBoardCharacter();
	if (!afterBoard.isDeleted(originalAlias)) {
	    if (copyAlias == null || afterBoard.isDeleted(copyAlias)) {
		copyAlias = new GCAlias(_newCharacterGC);
		if (originalAlias != null)
		    copyAlias.copyVariableValuesFrom(originalAlias, false);
		copyAlias.setName(Util.makeCopyName(copyAlias.getName()));
	    }
	    GeneralizedCharacter selfGC
		= afterBoard.getBeforeBoard().getSelfGC();
	    afterBoard.createCharacter(copyAlias, selfGC.getH() + _dx,
				       selfGC.getV() + _dy);
	}
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	CopyAction newAction = (CopyAction) map.get(this);
	if (newAction != null)
	    return newAction;
	GeneralizedCharacter newSource
	    = (GeneralizedCharacter) this.getTarget().copy(map, fullCopy);
	GeneralizedCharacter newDestination
	    = (GeneralizedCharacter) _newCharacterGC.copy(map, fullCopy);
	newAction = new CopyAction(newSource, newDestination, _dx, _dy, _z);
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	return true;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return _newCharacterGC.refersTo(obj) || super.refersTo(obj);
    }
    
    private final void synchronizeCopyGC() {
	_newCharacterGC.setValueType(this.getTarget().getValueType());
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_newCharacterGC);
	super.writeExternal(out);
	out.writeObject(_newCharacterGC);
	out.writeInt(_dx);
	out.writeInt(_dy);
	out.writeInt(_z);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_newCharacterGC = (GeneralizedCharacter) in.readObject();
	_dx = in.readInt();
	_dy = in.readInt();
	_z = in.readInt();
    }
    
    public void summarize(Summary s) {
	GeneralizedCharacter self = this.getSelf();
	s.writeFormat("copy action fmt", null,
		      new Object[] { this.getTarget(),
				     new Point
					 (self.getH() + _dx, self.getV() + _dy)
					 .toString() });
    }
    
    public final PlaywriteView createView() {
	GeneralizedCharacter self = this.getSelf();
	View grid = new LocationView(this.getRule().getBeforeBoard(),
				     this.getTarget().getAfterBoardCharacter(),
				     self.getH() + _dx, self.getV() + _dy);
	return new LineView(this, 8, "copy action fmt", null,
			    new View[] { this.getTarget().createIcon(),
					 grid });
    }
    
    public String toString() {
	String result = null;
	try {
	    result = ("<Copy " + this.getTarget() + " to (" + _dx + "," + _dy
		      + ")>");
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
