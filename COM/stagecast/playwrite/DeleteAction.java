/* DeleteAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class DeleteAction extends RuleAction
    implements Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108755805490L;
    private transient CharacterInstance _deletedCharacter = null;
    private transient CharacterContainer _oldContainer = null;
    private transient boolean _mainCharacterDeleted = false;
    private transient World _world;
    
    DeleteAction(GeneralizedCharacter gch) {
	this.setTarget(gch);
    }
    
    public DeleteAction() {
	/* empty */
    }
    
    public Object execute(CharacterContainer unusedContainer, int unusedBaseX,
			  int unusedBaseY) {
	_deletedCharacter = this.getTarget().getBinding();
	if (_deletedCharacter == null)
	    return RuleAction.FAILURE;
	_world = _deletedCharacter.getWorld();
	_oldContainer = _deletedCharacter.getCharContainer();
	if (_world.getMainCharacter() == _deletedCharacter)
	    _mainCharacterDeleted = true;
	if (_world.isRunning())
	    _deletedCharacter.deferredDelete();
	else if (_oldContainer != null)
	    _oldContainer.deleteCharacter(_deletedCharacter);
	this.getTarget().unbind();
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	if (_deletedCharacter != null && _oldContainer != null) {
	    _oldContainer.undeleteCharacter(_deletedCharacter);
	    if (_mainCharacterDeleted)
		_world.setMainCharacter(_deletedCharacter);
	    if (this.getTarget().getWorld().getState() == World.RECORDING)
		this.getTarget().bind(_deletedCharacter);
	}
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	GCAlias alias = this.getTarget().getAfterBoardCharacter();
	afterBoard.deleteCharacter(alias);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	DeleteAction newAction = (DeleteAction) map.get(this);
	if (newAction != null)
	    return newAction;
	GeneralizedCharacter deletedGC
	    = (GeneralizedCharacter) this.getTarget().copy(map, fullCopy);
	newAction = new DeleteAction(deletedGC);
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	return true;
    }
    
    public void summarize(Summary s) {
	s.writeFormat("delete action fmt", null,
		      new Object[] { this.getTarget() });
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(this.getTarget());
	super.writeExternal(out);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
    }
    
    public PlaywriteView createView() {
	return new LineView(this, 8, "delete action fmt", null,
			    new View[] { this.getTarget().createIcon() });
    }
    
    public String toString() {
	String result = null;
	try {
	    result = "<Delete " + this.getTarget() + ">";
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
