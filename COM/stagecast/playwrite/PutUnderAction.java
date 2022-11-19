/* PutUnderAction - Decompiled by JODE
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

class PutUnderAction extends RuleAction
    implements Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108755477810L;
    private GeneralizedCharacter _onTopGC;
    private transient CharacterInstance _changedCharacter = null;
    private transient CharacterContainer _container = null;
    private transient int _oldZ;
    
    PutUnderAction(GeneralizedCharacter ch1, GeneralizedCharacter ch2) {
	this.setTarget(ch1);
	_onTopGC = ch2;
    }
    
    public PutUnderAction() {
	/* empty */
    }
    
    final GeneralizedCharacter getNewOnTop() {
	return _onTopGC;
    }
    
    public Object execute(CharacterContainer unusedContainer, int unusedX,
			  int unusedY) {
	_changedCharacter = this.getTarget().getBinding();
	if (_changedCharacter == null)
	    return RuleAction.FAILURE;
	_container = _changedCharacter.getCharContainer();
	if (_container == null)
	    return RuleAction.FAILURE;
	_oldZ = _container.getZ(_changedCharacter);
	int newZ = -1;
	if (_onTopGC != null) {
	    int changedZ = _changedCharacter.getZ();
	    int targetZ = _container.getZ(_onTopGC.getBinding());
	    if (changedZ < targetZ)
		newZ = targetZ - 1;
	    else
		newZ = targetZ;
	}
	_container.setZ(_changedCharacter, newZ);
	return (_oldZ == _changedCharacter.getZ() ? RuleAction.NOOP
		: RuleAction.SUCCESS);
    }
    
    public void undo() {
	if (_changedCharacter != null && _container != null)
	    _container.setZ(_changedCharacter, _oldZ);
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	GCAlias alias = this.getTarget().getAfterBoardCharacter();
	if (!afterBoard.isDeleted(alias)) {
	    if (_onTopGC == null)
		afterBoard.setZ(alias, -1);
	    else {
		GCAlias onTopGCAlias = _onTopGC.getAfterBoardCharacter();
		if (!afterBoard.isDeleted(onTopGCAlias))
		    afterBoard.setZ(alias, afterBoard.getZ(onTopGCAlias));
	    }
	}
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	PutUnderAction newAction = (PutUnderAction) map.get(this);
	if (newAction != null)
	    return newAction;
	GeneralizedCharacter newBottom
	    = (GeneralizedCharacter) this.getTarget().copy(map, fullCopy);
	GeneralizedCharacter newTop
	    = (_onTopGC == null ? null
	       : (GeneralizedCharacter) _onTopGC.copy(map, fullCopy));
	newAction = new PutUnderAction(newBottom, newTop);
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	return true;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	if (_onTopGC != null && _onTopGC.refersTo(obj))
	    return true;
	return super.refersTo(obj);
    }
    
    public void summarize(Summary s) {
	Object[] args;
	String resourceID;
	if (_onTopGC == null) {
	    args = new Object[] { this.getTarget() };
	    resourceID = "put on top action fmt";
	} else {
	    args = new Object[] { this.getTarget(), _onTopGC };
	    resourceID = "put under action fmt";
	}
	s.writeFormat(resourceID, null, args);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(this.getTarget());
	super.writeExternal(out);
	out.writeObject(_onTopGC);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_onTopGC = (GeneralizedCharacter) in.readObject();
    }
    
    public PlaywriteView createView() {
	View targetGCView = this.getTarget().createIcon();
	LineView view;
	if (_onTopGC == null)
	    view = new LineView(this, 8, "put on top action fmt", null,
				new View[] { targetGCView });
	else
	    view = new LineView(this, 8, "put under action fmt", null,
				new View[] { targetGCView,
					     _onTopGC.createIcon() });
	return view;
    }
    
    public String toString() {
	String result = null;
	try {
	    result = "<Put " + this.getTarget() + " under " + _onTopGC + ">";
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
