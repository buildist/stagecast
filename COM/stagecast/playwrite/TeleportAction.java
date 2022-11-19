/* TeleportAction - Decompiled by JODE
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

class TeleportAction extends RuleAction
    implements Debug.Constants, Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 3;
    static final long serialVersionUID = -3819410108755281202L;
    private GeneralizedCharacter _doorGC;
    private CharacterContainer _targetContainer;
    private transient CharacterInstance _teleportedCharacter = null;
    private transient CharacterContainer _oldContainer;
    private transient CharacterContainer _newContainer;
    private transient int _oldX;
    private transient int _oldY;
    private transient int _oldZ;
    private transient DoorInstance _destination;
    
    TeleportAction(GeneralizedCharacter gch, GeneralizedCharacter door) {
	this.setTarget(gch);
	_doorGC = door;
	_targetContainer = null;
    }
    
    /**
     * @deprecated
     */
    private TeleportAction(GeneralizedCharacter gch,
			   CharacterContainer container) {
	this.setTarget(gch);
	_doorGC = null;
	_targetContainer = container;
    }
    
    public TeleportAction() {
	/* empty */
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	World myWorld = this.getWorld();
	_teleportedCharacter = this.getTarget().getBinding();
	if (_teleportedCharacter == null)
	    return RuleAction.FAILURE;
	World world = _teleportedCharacter.getWorld();
	_oldContainer = _teleportedCharacter.getCharContainer();
	_oldX = _teleportedCharacter.getH();
	_oldY = _teleportedCharacter.getV();
	_oldZ = (_oldContainer == null ? -1
		 : _oldContainer.getZ(_teleportedCharacter));
	if (_doorGC == null) {
	    _destination = null;
	    _newContainer = _targetContainer;
	} else {
	    Object doorGCBinding = _doorGC.getBinding();
	    if (!(doorGCBinding instanceof Door) || doorGCBinding == null)
		return RuleAction.FAILURE;
	    Door door = (Door) _doorGC.getBinding();
	    _destination = (DoorInstance) door.getOtherEnd();
	    _newContainer = _destination.getCharContainer();
	}
	int newH;
	int newV;
	int newZ;
	if (_destination == null) {
	    newH = _oldX;
	    newV = _oldY;
	    newZ = -1;
	} else {
	    newH = _destination.getH();
	    newV = _destination.getV();
	    newZ = -1;
	}
	if (_newContainer == null)
	    return RuleAction.FAILURE;
	if (_oldContainer == _newContainer)
	    _oldContainer.relocate(_teleportedCharacter, newH, newV, newZ);
	else {
	    if (_oldContainer != null)
		_oldContainer.remove(_teleportedCharacter);
	    _newContainer.add(_teleportedCharacter, newH, newV, newZ);
	}
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	if (_teleportedCharacter != null && _newContainer != null) {
	    if (_oldContainer == _newContainer)
		_oldContainer.relocate(_teleportedCharacter, _oldX, _oldY,
				       _oldZ);
	    else {
		_newContainer.remove(_teleportedCharacter);
		if (_oldContainer != null)
		    _oldContainer.add(_teleportedCharacter, _oldX, _oldY,
				      _oldZ);
	    }
	}
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	if (_doorGC == null
	    || !afterBoard.isDeleted(_doorGC.getAfterBoardCharacter())) {
	    GCAlias alias = this.getTarget().getAfterBoardCharacter();
	    afterBoard.deleteCharacter(alias);
	}
    }
    
    public PlaywriteView createView() {
	View characterIcon = this.getTarget().createIcon();
	View[] viewArgs;
	String resourceID;
	if (_doorGC != null) {
	    viewArgs = new View[] { characterIcon, _doorGC.createIcon() };
	    resourceID = "teleport action 1 xfmt";
	} else {
	    viewArgs
		= new View[] { characterIcon,
			       ((Stage) _targetContainer).createIconView() };
	    resourceID = "teleport action 2 xfmt";
	}
	LineView view = new LineView(this, 8, resourceID, null, viewArgs);
	return view;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	TeleportAction newAction = (TeleportAction) map.get(this);
	if (newAction != null)
	    return newAction;
	GeneralizedCharacter newSource
	    = (GeneralizedCharacter) this.getTarget().copy(map, fullCopy);
	if (_doorGC == null) {
	    if (_targetContainer instanceof Stage)
		newAction
		    = new TeleportAction(newSource,
					 (Stage) ((Stage) _targetContainer)
						     .copy(map, fullCopy));
	    else {
		Debug.print("debug.rule.action", "Can't copy container ",
			    _targetContainer, " in ", this);
		newAction = new TeleportAction(newSource, _targetContainer);
	    }
	} else
	    newAction
		= new TeleportAction(newSource, ((GeneralizedCharacter)
						 _doorGC.copy(map, fullCopy)));
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	return true;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	if (_doorGC != null && _doorGC.refersTo(obj))
	    return true;
	if (_targetContainer == obj)
	    return true;
	return super.refersTo(obj);
    }
    
    public void summarize(Summary s) {
	Object[] args;
	String resourceID;
	if (_doorGC != null) {
	    args = new Object[] { this.getTarget(), _doorGC };
	    resourceID = "teleport action 1 xfmt";
	} else {
	    args = new Object[] { this.getTarget(), _targetContainer };
	    resourceID = "teleport action 2 xfmt";
	}
	s.writeFormat(resourceID, null, args);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(this.getTarget());
	ASSERT.isTrue(_doorGC == null ^ _targetContainer == null);
	super.writeExternal(out);
	out.writeObject(_doorGC);
	out.writeObject(_targetContainer);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(TeleportAction.class);
	super.readExternal(in);
	_doorGC = (GeneralizedCharacter) in.readObject();
	switch (version) {
	case 2: {
	    _targetContainer = (CharacterContainer) in.readObject();
	    int foo = in.readInt();
	    foo = in.readInt();
	    foo = in.readInt();
	    break;
	}
	case 3:
	    _targetContainer = (CharacterContainer) in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 3);
	case 1:
	    /* empty */
	}
    }
    
    public String toString() {
	String result = null;
	try {
	    result = ("<Teleport " + this.getTarget() + " through " + _doorGC
		      + ">");
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
