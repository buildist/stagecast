/* VariableAlias - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Expression;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class VariableAlias
    implements Debug.Constants, Expression, Externalizable,
	       ResourceIDs.SummaryIDs, Verifiable, Worldly
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108754887986L;
    private VariableOwner _owner;
    private Variable _variable;
    private transient String _systemVariableID;
    private transient ValueView.SetterGetter setterGetter;
    private transient ValueView.SetterGetter aliasSetterGetter;
    private transient ValueView.SetterGetter blankSetterGetter;
    
    VariableAlias(VariableOwner owner, Variable v) {
	_owner = owner;
	_variable = v;
    }
    
    VariableAlias(VariableOwner owner, String systemVariableID) {
	ASSERT.isNotNull(systemVariableID);
	_owner = owner;
	_systemVariableID = systemVariableID;
	if (owner != null && owner.getVariableListOwner() != null)
	    drFixUpSystemVariable();
    }
    
    public VariableAlias() {
	/* empty */
    }
    
    boolean drFixUpSystemVariable() {
	if (_variable == null && _systemVariableID != null && _owner != null) {
	    CharacterPrototype proto
		= ((GeneralizedCharacter) _owner).getPrototype();
	    if (proto == null)
		Debug.print("debug.dr", "bad variable alias: null proto, var ",
			    _systemVariableID);
	    else {
		_variable = proto.findSystemVariable(_systemVariableID);
		_systemVariableID = null;
	    }
	}
	return _variable != null;
    }
    
    boolean drIsSystemVar(String id) {
	boolean isTheSame = false;
	if (_variable == null) {
	    if (_systemVariableID == null)
		Debug.print
		    ("debug.dr",
		     "VariableAlias: unable to determine if my variable is type ",
		     id);
	    else if (_systemVariableID.equals(id))
		isTheSame = true;
	} else
	    isTheSame = _variable.isSystemType(id);
	return isTheSame;
    }
    
    public boolean isValid() {
	if (_owner == null || _variable == null)
	    return false;
	if (!_owner.isValid())
	    return false;
	if (!_variable.isValid())
	    return false;
	if (_owner.getVariableList().hasVariable(_variable) == false)
	    return false;
	return true;
    }
    
    /**
     * @deprecated
     */
    final VariableOwner getOwner() {
	return _owner;
    }
    
    final boolean setOwner(VariableOwner newOwner) {
	if (_owner == null) {
	    _owner = newOwner;
	    return true;
	}
	return false;
    }
    
    public final World getWorld() {
	return _owner.getWorld();
    }
    
    final Variable getVariable(VariableOwner own) {
	return (_variable.existsFor(own) ? _variable
		: own.getVariableList().findEquivalentVariable(_variable));
    }
    
    private Variable getVariable() {
	VariableOwner own = getActualOwner();
	if (own == null)
	    return _variable;
	return getVariable(own);
    }
    
    Object getActualValue() {
	VariableOwner owner = getActualOwner();
	Variable v = getVariable();
	if (owner == null || v == null)
	    return Variable.ILLEGAL_VALUE;
	return v.getActualValue(owner);
    }
    
    public Object eval() {
	VariableOwner owner = getActualOwner();
	Variable v = getVariable();
	if (owner == null || v == null)
	    return Variable.ILLEGAL_VALUE;
	return v.getValue(owner);
    }
    
    public void summarize(Summary s) {
	String name = _variable.getName();
	Object[] params = { name };
	if (s.ruleSelf() == _owner)
	    s.writeValue(Resource.getTextAndFormat("SUM mva", params));
	else if (getWorld() == _owner)
	    s.writeValue(Resource.getTextAndFormat("SUM twv", params));
	else {
	    s.writeValue(_owner);
	    s.writeValue(Resource.getTextAndFormat("SUM pva", params));
	}
    }
    
    public Object findReferenceTo(ReferencedObject obj) {
	VariableOwner actualOwner = _owner;
	if (actualOwner == obj || actualOwner.refersTo(obj))
	    return actualOwner;
	if (_variable == obj)
	    return _variable;
	return null;
    }
    
    public Expression evaluates(Expression foo) {
	if (_owner == foo)
	    return this;
	return null;
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	Hashtable map = new Hashtable(50);
	if (getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	return copy(map, true);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	VariableAlias newAlias = (VariableAlias) map.get(this);
	if (newAlias != null)
	    return newAlias;
	World oldWorld = getWorld();
	World newWorld = (World) map.get(oldWorld);
	VariableOwner newOwner;
	if (_owner instanceof Copyable)
	    newOwner = (VariableOwner) ((Copyable) _owner).copy(map, fullCopy);
	else if (_owner instanceof World)
	    newOwner = newWorld == null ? oldWorld : newWorld;
	else
	    throw new PlaywriteInternalError("Can't copy " + this);
	Variable newVariable = (Variable) _variable.copy(map, fullCopy);
	newVariable.copyValue(_variable.getActualValue(_owner), newOwner, map,
			      false);
	newAlias = new VariableAlias(newOwner, newVariable);
	map.put(this, newAlias);
	return newAlias;
    }
    
    void setValue(Object value) {
	setValue(getActualOwner(), value);
    }
    
    void setValue(VariableOwner own, Object value) {
	if (own != null) {
	    Variable v = getVariable(own);
	    if (v != null)
		v.setValue(own, value);
	}
    }
    
    void setGCAliasValue(Object value) {
	if (_owner instanceof GeneralizedCharacter) {
	    GCAlias gcAlias
		= ((GeneralizedCharacter) _owner).getAfterBoardCharacter();
	    if (gcAlias != null)
		_variable.setValue(gcAlias, value);
	} else if (_owner instanceof World
		   && RuleEditor.isRecordingOrEditing())
	    _variable.setValue(_owner, value);
    }
    
    final VariableOwner getActualOwner() {
	VariableOwner actualOwner = _owner;
	if (_owner instanceof GeneralizedCharacter) {
	    GeneralizedCharacter owner = (GeneralizedCharacter) _owner;
	    actualOwner = owner.getBinding();
	    if (actualOwner == null)
		actualOwner = owner.getAfterBoardCharacter();
	}
	return actualOwner;
    }
    
    private ValueView.SetterGetter getBestCaseVSG() {
	if (setterGetter == null)
	    setterGetter = new ValueView.DisplayOnlySetterGetter() {
		public World getWorld() {
		    return VariableAlias.this.getWorld();
		}
		
		public void setValueView(ValueView valueView) {
		    /* empty */
		}
		
		public Object getValue() {
		    Object value = eval();
		    if (value == Variable.ILLEGAL_VALUE)
			return null;
		    return value;
		}
	    };
	return setterGetter;
    }
    
    private ValueView.SetterGetter getBoundVSG() {
	if (setterGetter == null)
	    setterGetter = new ValueView.DisplayOnlySetterGetter() {
		public World getWorld() {
		    return VariableAlias.this.getWorld();
		}
		
		public void setValueView(ValueView valueView) {
		    /* empty */
		}
		
		public Object getValue() {
		    Object value = null;
		    if (!(_owner instanceof GeneralizedCharacter)
			|| ((GeneralizedCharacter) _owner).isBound())
			value = eval();
		    if (value == Variable.ILLEGAL_VALUE)
			return null;
		    return value;
		}
	    };
	return setterGetter;
    }
    
    private ValueView.SetterGetter getBlankVSG() {
	if (blankSetterGetter == null)
	    blankSetterGetter = new ValueView.DisplayOnlySetterGetter() {
		public World getWorld() {
		    return VariableAlias.this.getWorld();
		}
		
		public void setValueView(ValueView valueView) {
		    /* empty */
		}
		
		public Object getValue() {
		    return null;
		}
	    };
	return blankSetterGetter;
    }
    
    public PlaywriteView createView() {
	return createView(getBlankVSG());
    }
    
    public PlaywriteView createBoundView() {
	return createView(getBoundVSG());
    }
    
    public PlaywriteView createBestCaseView() {
	return createView(getBestCaseVSG());
    }
    
    public PlaywriteView createView(ValueView.SetterGetter vsg) {
	PlaywriteView ownerView = getOwner().createIconView();
	AbstractVariableEditor variableView;
	if (_variable instanceof BooleanVariable)
	    variableView
		= ((BooleanVariable) _variable).makeVariableEditor(_owner, vsg,
								   false);
	else
	    variableView = _variable.makeVariableEditor(_owner, vsg);
	variableView.setEnabled(false);
	PlaywriteView aliasView = new PlaywriteView() {
	    Point getDragPoint() {
		Point dragPoint = super.getDragPoint();
		dragPoint.x = 0;
		dragPoint.y = 10;
		return dragPoint;
	    }
	};
	aliasView.setTransparent(true);
	aliasView.addSubview(ownerView);
	ownerView.setMouseTransparency(true);
	variableView.moveTo(ownerView.bounds.maxX(), 0);
	aliasView.addSubview(variableView);
	aliasView.sizeToMinSize();
	aliasView.setModelObject(this);
	return aliasView;
    }
    
    public PlaywriteView createIconView() {
	return createView();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_owner);
	ASSERT.isNotNull(_variable);
	out.writeObject(_owner);
	out.writeObject(_variable);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_owner = (VariableOwner) in.readObject();
	_variable = Variable.xlateV1Variable(in.readObject(),
					     _owner.getVariableListOwner());
	fixDuplicateVariable();
    }
    
    void fixDuplicateVariable() {
	VariableOwner owner = getOwner().getVariableListOwner();
	if (owner instanceof CocoaCharacter) {
	    Variable variable = getVariable();
	    Variable replacement
		= variable.getFixedCocoaVariable((CocoaCharacter) owner);
	    if (replacement != variable) {
		Debug.print("debug.dr", "replacing ", variable, " with ",
			    replacement, " va: ", owner);
		_variable = variable;
	    }
	}
    }
    
    public boolean equals(Object foo) {
	if (foo == this)
	    return true;
	if (foo instanceof VariableAlias) {
	    VariableAlias v2 = (VariableAlias) foo;
	    return v2._owner == _owner && v2._variable == _variable;
	}
	return false;
    }
    
    public String toString() {
	String result = null;
	String name
	    = _variable == null ? _systemVariableID : _variable.getName();
	try {
	    result = "<VariableAlias for '" + name + "' in " + _owner + ">";
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
