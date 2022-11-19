/* PutAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Expression;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.OperationManager;
import COM.stagecast.operators.OperationType;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public final class PutAction extends RuleAction
    implements Debug.Constants, Externalizable, ResourceIDs.RuleEditorIDs,
	       Verifiable
{
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108755608882L;
    static final Object[][] types
	= { { null, Resource.getText("RE put"), Resource.getText("RE into") },
	    { Op.Add, Resource.getText("RE add"), Resource.getText("RE to") },
	    { Op.Subtract, Resource.getText("RE subtract"),
	      Resource.getText("RE from") } };
    public static int DEFAULT_TYPE = 0;
    private VariableAlias variableAlias;
    private Object simpleValue;
    private Expression expression;
    private transient boolean evalValue;
    private transient VariableOwner modifiedItem;
    private transient Object oldValue;
    private transient Object newValue;
    private transient int type = -1;
    private transient boolean blank = true;
    
    PutAction(VariableAlias va, Object value) {
	variableAlias = va;
	setValue(value);
    }
    
    PutAction(GeneralizedCharacter gch, Variable v, Object value) {
	this(new VariableAlias(gch, v), value);
    }
    
    protected PutAction(Object value) {
	this(null, null, value);
    }
    
    public PutAction() {
	/* empty */
    }
    
    public boolean isValid() {
	if (blank)
	    return false;
	if (!variableAlias.isValid())
	    return false;
	if (evalValue && !expression.isValid())
	    return false;
	return true;
    }
    
    public static RuleAction createForRuleEditor(World world) {
	return new PutAction();
    }
    
    final GeneralizedCharacter getTarget() {
	throw new PlaywriteInternalError("Put actions do not have targets");
    }
    
    final void setTarget(GeneralizedCharacter gc) {
	throw new PlaywriteInternalError("Put actions do not have targets");
    }
    
    final Object getValue() {
	return evalValue ? (Object) expression : simpleValue;
    }
    
    public final void setValue(Object v) {
	if (v instanceof Expression) {
	    evalValue = true;
	    simpleValue = null;
	    expression = (Expression) v;
	} else {
	    evalValue = false;
	    simpleValue = v;
	    expression = null;
	}
	blank = variableAlias == null;
    }
    
    public final VariableAlias getVariableAlias() {
	return variableAlias;
    }
    
    public final void setVariableAlias(VariableAlias va) {
	variableAlias = va;
	if (type > 0)
	    ((OperationManager) expression).setLeftSide(va);
	blank = variableAlias == null;
    }
    
    final Object getLeftSide() {
	if (type == 0)
	    return getValue();
	return ((OperationManager) expression).getRightSide();
    }
    
    final void setLeftSide(Object object) {
	if (type == 0)
	    setValue(object);
	else
	    ((OperationManager) expression).setRightSide(object);
    }
    
    final int getNumberOfTypes() {
	return types.length;
    }
    
    final OperationType getTypeOp(int i) {
	return (OperationType) types[i][0];
    }
    
    final String getTypeName(int i) {
	return (String) types[i][1];
    }
    
    final String getTypePrep(int i) {
	return (String) types[i][2];
    }
    
    final int getType() {
	if (type != -1)
	    return type;
	if (evalValue && expression instanceof OperationManager) {
	    OperationManager om = (OperationManager) expression;
	    if (variableAlias == null
		|| variableAlias.equals(om.getLeftSide())) {
		for (int i = 1; i < types.length; i++) {
		    if (om.isSameOpAs(getTypeOp(i)))
			return type = i;
		}
	    }
	}
	return type = 0;
    }
    
    public final void changeToType(int t) {
	if (type == -1)
	    getType();
	if (t >= types.length)
	    throw new PlaywriteInternalError("bad index for type change!");
	if (type != t) {
	    if (type == 0) {
		Object rightSide = getValue();
		OperationType operationType = getTypeOp(t);
		setValue(new OperationManager(variableAlias, rightSide,
					      operationType));
	    } else if (t > 0) {
		OperationManager om = (OperationManager) expression;
		om.changeToOperation(getTypeOp(t));
	    } else {
		if (t != 0 || type < 1)
		    throw new PlaywriteInternalError("bad change");
		OperationManager om = (OperationManager) expression;
		setValue(om.getRightSide());
	    }
	    type = -1;
	    if (t != getType())
		throw new PlaywriteInternalError("type indices don't match!");
	}
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	if (blank)
	    return RuleAction.FAILURE;
	modifiedItem = variableAlias.getActualOwner();
	if (modifiedItem == null)
	    return RuleAction.FAILURE;
	oldValue = variableAlias.getActualValue();
	newValue = getNewValue();
	if (newValue == Variable.ILLEGAL_VALUE)
	    return RuleAction.FAILURE;
	variableAlias.setValue(newValue);
	Object testValue = variableAlias.getActualValue();
	if (Variable.areEqual(oldValue, testValue))
	    return RuleAction.NOOP;
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	if (!blank)
	    variableAlias.setValue(modifiedItem, oldValue);
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	if (!blank && newValue != Variable.ILLEGAL_VALUE) {
	    if (newValue != null)
		variableAlias.setGCAliasValue(newValue);
	    else
		variableAlias.setGCAliasValue(getNewValue());
	}
    }
    
    private final Object getNewValue() {
	if (evalValue) {
	    Object result = expression.eval();
	    return Op.checkResult(result);
	}
	return simpleValue;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	PutAction newAction = (PutAction) map.get(this);
	if (newAction != null)
	    return newAction;
	VariableAlias newAlias;
	if (variableAlias == null)
	    newAlias = null;
	else
	    newAlias = (VariableAlias) variableAlias.copy(map, fullCopy);
	Object newValue;
	if (evalValue) {
	    if (expression == null)
		newValue = null;
	    else
		newValue = expression.copy(map, fullCopy);
	} else if (simpleValue instanceof Copyable)
	    newValue = ((Copyable) simpleValue).copy(map, fullCopy);
	else {
	    Debug.print("debug.copy", "Can't copy ", simpleValue, " in ",
			this);
	    newValue = simpleValue;
	}
	newAction = new PutAction(newAlias, newValue);
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	if (blank)
	    return false;
	return modifiedItem
		   .affectsDisplay(variableAlias.getVariable(modifiedItem));
    }
    
    public boolean refersTo(ReferencedObject obj) {
	Object ref = null;
	if (evalValue)
	    ref = expression.findReferenceTo(obj);
	else if (simpleValue == obj)
	    ref = simpleValue;
	if (ref == null && !blank)
	    ref = variableAlias.findReferenceTo(obj);
	return ref != null;
    }
    
    public void summarize(Summary s) {
	int exprType = getType();
	Object[] params = { getTypeName(exprType), getLeftSide(),
			    getTypePrep(exprType), variableAlias };
	s.writeFormat("put action fmt", null, params);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeObject(variableAlias);
	if (evalValue)
	    out.writeObject(expression);
	else
	    out.writeObject(simpleValue);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(PutAction.class);
	super.readExternal(in);
	setVariableAlias((VariableAlias) in.readObject());
	switch (version) {
	case 1:
	    in.readBoolean();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 2);
	case 2:
	    /* empty */
	}
	Object val = in.readObject();
	if (val instanceof CharacterInstance) {
	    Debug.print(true,
			"Illegal character instance encountered in PutAction");
	    ((WorldInStream) in).getTargetWorld().incrementBadCharacterCount();
	    val = null;
	}
	setValue(val);
    }
    
    public PlaywriteView createView() {
	PlaywriteView v = new PutActionView(this);
	return v;
    }
    
    public String toString() {
	String result = null;
	try {
	    result = ("<Put " + (evalValue ? (Object) expression : simpleValue)
		      + " into " + variableAlias + ">");
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
