/* SwitchStageAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Expression;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class SwitchStageAction extends DeferredAction
    implements Debug.Constants, Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 4;
    static final long serialVersionUID = -3819410108755346738L;
    private Stage _newStage;
    private Object _newStageIndirectThing;
    private int _index;
    private boolean _relative;
    private boolean _myStage;
    private transient World _world;
    private transient Stage _oldStage = null;
    private transient int[] _indices = null;
    
    SwitchStageAction(Stage newStage, int index) {
	this(newStage, null, index, false, false);
	Debug.print("debug.switch.stage", "creating switch stage action: ",
		    newStage, " index= " + _index);
    }
    
    SwitchStageAction(Stage newStage, boolean myStage) {
	this(newStage, null, -1, true, myStage);
	Debug.print("debug.switch.stage", "creating switch stage action: ",
		    newStage, " selfStage= " + myStage);
    }
    
    private SwitchStageAction(Stage newStage, Object indirectStage, int index,
			      boolean relative, boolean myStage) {
	_newStage = newStage;
	_newStageIndirectThing = indirectStage;
	_index = index;
	_relative = relative;
	_myStage = myStage;
    }
    
    public SwitchStageAction() {
	/* empty */
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	if (_newStageIndirectThing != null)
	    _newStage = getIndirectStage();
	return super.execute(container, baseX, baseY);
    }
    
    Stage getStageFrom(Object object) {
	Stage stage = null;
	if (object instanceof Stage)
	    return (Stage) object;
	if (object != null) {
	    if (object instanceof Expression)
		object = ((Expression) object).eval();
	    object = Util.findEqualOrSameName(getWorld().getStages()
						  .getContents(),
					      object);
	    if (object instanceof Stage)
		stage = (Stage) object;
	}
	return stage;
    }
    
    private Stage getIndirectStage() {
	if (_newStageIndirectThing != null)
	    return getStageFrom(_newStageIndirectThing);
	return null;
    }
    
    public void performCommand(String command, Object data) {
	if (command == "execute") {
	    RuleAction.RuleExecutionArguments args
		= (RuleAction.RuleExecutionArguments) data;
	    deferredExecute(args.container, args.baseX, args.baseY);
	} else
	    super.performCommand(command, data);
    }
    
    public void deferredExecute(CharacterContainer container, int baseX,
				int baseY) {
	if (_newStage != null && !_newStage.isProxy()) {
	    if (!getWorld().isInSyncPhase())
		getWorld().addSyncAction
		    (this, "execute",
		     new RuleAction.RuleExecutionArguments(container, baseX,
							   baseY));
	    else {
		Debug.print("debug.switch.stage", "switching from ", container,
			    " to ", _newStage,
			    (", index = " + _index + "relative = " + _relative
			     + " self stage = " + _myStage));
		long stats = System.currentTimeMillis();
		_world = _newStage.getWorld();
		if (_relative) {
		    if (_world.getStageViewIndex((Stage) container) != -1
			|| !_myStage) {
			_indices = new int[_world.getNumberOfVisibleStages()];
			if (_myStage) {
			    for (int i = 0;
				 i < _world.getNumberOfVisibleStages(); i++) {
				if (_world.getStageAtIndex(i) == container)
				    _indices[i] = i;
				else
				    _indices[i] = -1;
			    }
			} else {
			    boolean matchNext = false;
			    CharacterContainer replaceThis = null;
			    for (int i = 0;
				 i < _world.getNumberOfVisibleStages(); i++) {
				if (replaceThis == _world.getStageAtIndex(i)
				    || (replaceThis == null
					&& (_world.getStageAtIndex(i)
					    != container))) {
				    _indices[i] = i;
				    if (replaceThis == null)
					replaceThis
					    = _world.getStageAtIndex(i);
				} else
				    _indices[i] = -1;
			    }
			}
		    } else
			return;
		}
		if (_indices == null) {
		    _indices = new int[1];
		    _indices[0] = _index;
		}
		for (int i = 0; i < _indices.length; i++) {
		    if (_indices[i] != -1) {
			_oldStage = _world.getStageAtIndex(_indices[i]);
			if (_oldStage != _newStage)
			    _world.switchStage(_newStage, _indices[i]);
			else
			    _indices[i] = -1;
		    }
		}
		stats = System.currentTimeMillis() - stats;
		Debug.print("debug.statistics", "Stage switch time " + stats,
			    "ms");
	    }
	}
    }
    
    public void undo() {
	if (_world != null) {
	    if (!PlaywriteRoot.app().inEventThread())
		_world.addSyncAction(this, "undo", null);
	    else {
		Debug.print("debug.switch.stage",
			    "undoing stage switch action, going back to ",
			    _oldStage, ", index = " + _index);
		if (_indices != null) {
		    if (_oldStage != _newStage) {
			for (int i = 0; i < _indices.length; i++) {
			    if (_oldStage != _newStage && _indices[i] != -1)
				_world.switchStage(_oldStage, _indices[i]);
			}
		    }
		}
	    }
	}
    }
    
    void setEditStage(Object val) {
	if (val instanceof Stage) {
	    _newStage = (Stage) val;
	    _newStageIndirectThing = null;
	} else {
	    _newStage = null;
	    _newStageIndirectThing = val;
	}
    }
    
    Object getEditStage() {
	return (_newStageIndirectThing == null ? (Object) _newStage
		: _newStageIndirectThing);
    }
    
    boolean isEditValid() {
	if (_newStageIndirectThing == null)
	    return _newStage != null;
	return getIndirectStage() != null;
    }
    
    public PlaywriteView createView() {
	return new SwitchStageActionView(this);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	SwitchStageAction newAction = (SwitchStageAction) map.get(this);
	if (newAction != null)
	    return newAction;
	Stage newStage = null;
	Object newStageIndirectThing = null;
	if (_newStageIndirectThing == null) {
	    if (_newStage != null)
		newStage = (Stage) _newStage.copy(map, fullCopy);
	} else {
	    newStage = null;
	    if (_newStageIndirectThing instanceof Copyable)
		newStageIndirectThing
		    = ((Copyable) _newStageIndirectThing).copy(map, fullCopy);
	    else
		newStageIndirectThing = _newStageIndirectThing;
	}
	newAction = new SwitchStageAction(newStage, newStageIndirectThing,
					  _index, _relative, _myStage);
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	if (super.refersTo(obj))
	    return true;
	if (_newStageIndirectThing == null)
	    return _newStage == obj;
	if (_newStageIndirectThing instanceof Expression)
	    return (((Expression) _newStageIndirectThing).findReferenceTo(obj)
		    != null);
	return _newStageIndirectThing == obj;
    }
    
    public World getWorld() {
	World world = super.getWorld();
	if (world == null)
	    return _newStage.getWorld();
	return world;
    }
    
    public void summarize(Summary s) {
	s.writeFormat("switch stage action fmt", null,
		      new Object[] { _newStageIndirectThing == null
				     ? (Object) _newStage
				     : _newStageIndirectThing });
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeObject(_newStage);
	out.writeObject(_newStageIndirectThing);
	out.writeBoolean(_myStage);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version
	    = ((WorldInStream) in).loadVersion(SwitchStageAction.class);
	super.readExternal(in);
	_newStage = (Stage) in.readObject();
	switch (version) {
	case 1:
	    _index = in.readInt();
	    _relative = true;
	    _myStage = true;
	    break;
	case 2:
	    _index = -1;
	    _relative = true;
	    _myStage = in.readBoolean();
	    break;
	case 3:
	case 4:
	    _newStageIndirectThing = in.readObject();
	    _index = -1;
	    _relative = true;
	    _myStage = in.readBoolean();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 4);
	}
    }
    
    public String toString() {
	return "<Switch stage to " + _newStage + ">";
    }
}
