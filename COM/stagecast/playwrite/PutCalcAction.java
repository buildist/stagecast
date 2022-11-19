/* PutCalcAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.OperationManager;
import COM.stagecast.operators.OperationType;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

final class PutCalcAction extends RuleAction
    implements Debug.Constants, Externalizable, ResourceIDs.RuleEditorIDs,
	       Verifiable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108752397618L;
    static final OperationType[] types
	= { Op.Add, Op.Subtract, Op.Divide, Op.Multiply, Op.Mod, Op.Power,
	    Op.Remainder, Op.Round, Op.Append, Op.GetWord, Op.Remove,
	    Op.RemoveWord, Op.AppendChar, Op.GetChar, Op.RemoveChar,
	    Op.AppendItem, Op.GetItem, Op.RemoveItem };
    static final int _regularTypes = 4;
    private VariableAlias variableAlias;
    private OperationManager calculation;
    private transient VariableOwner modifiedItem;
    private transient Object oldValue;
    private transient Object newValue;
    private transient boolean validated = false;
    private transient boolean valid = false;
    
    PutCalcAction(VariableAlias va, OperationManager calculation) {
	setVariableAlias(va);
	setCalculation(calculation);
    }
    
    PutCalcAction(GeneralizedCharacter gch, Variable v,
		  OperationManager calculation) {
	this(new VariableAlias(gch, v), calculation);
    }
    
    public static PutCalcAction createForRuleEditor(World world) {
	return new PutCalcAction(null,
				 new OperationManager(null, null, types[0]));
    }
    
    public PutCalcAction() {
	/* empty */
    }
    
    public final boolean isValid() {
	if (validated)
	    return valid;
	validated = true;
	if (variableAlias == null || !variableAlias.isValid())
	    return valid = false;
	if (calculation == null || !calculation.isValid())
	    return valid = false;
	return valid = true;
    }
    
    void invalidate() {
	validated = false;
    }
    
    final GeneralizedCharacter getTarget() {
	throw new PlaywriteInternalError("PutCalcAction do not have target");
    }
    
    final void setTarget(GeneralizedCharacter gc) {
	throw new PlaywriteInternalError("PutCalcAction do not have target");
    }
    
    final VariableAlias getVariableAlias() {
	return variableAlias;
    }
    
    final void setVariableAlias(VariableAlias va) {
	variableAlias = va;
	invalidate();
    }
    
    final void setCalculation(OperationManager calc) {
	calculation = calc;
	invalidate();
    }
    
    final Object getLeftArgument() {
	return calculation.getLeftSide();
    }
    
    final void setLeftArgument(Object object) {
	calculation.setLeftSide(object);
	invalidate();
    }
    
    final Object getRightArgument() {
	return calculation.getRightSide();
    }
    
    final void setRightArgument(Object object) {
	calculation.setRightSide(object);
	invalidate();
    }
    
    final int getNumberOfTypes() {
	return types.length;
    }
    
    final int getNumberOfRegularTypes() {
	return 4;
    }
    
    final OperationType getTypeOp(int i) {
	return types[i];
    }
    
    final String getTypeName(int i) {
	return types[i].getLocalName();
    }
    
    final int getType() {
	for (int i = 1; i < types.length; i++) {
	    if (calculation.isSameOpAs(getTypeOp(i)))
		return i;
	}
	return 0;
    }
    
    final void changeToType(int t) {
	calculation.changeToOperation(getTypeOp(t));
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	if (!isValid())
	    return RuleAction.FAILURE;
	modifiedItem = variableAlias.getActualOwner();
	if (modifiedItem == null)
	    return RuleAction.FAILURE;
	oldValue = variableAlias.getActualValue();
	newValue = calculation.eval();
	if (newValue == Variable.ILLEGAL_VALUE)
	    return RuleAction.FAILURE;
	newValue = Op.checkResult(newValue);
	variableAlias.setValue(newValue);
	Object testValue = variableAlias.getActualValue();
	if (oldValue == testValue
	    || (testValue instanceof Number || testValue instanceof Boolean
		|| testValue instanceof String) && testValue.equals(oldValue))
	    return RuleAction.NOOP;
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	if (isValid())
	    variableAlias.setValue(modifiedItem, oldValue);
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	if (isValid() && newValue != Variable.ILLEGAL_VALUE) {
	    if (newValue != null)
		variableAlias.setGCAliasValue(newValue);
	    else
		variableAlias.setGCAliasValue(calculation.eval());
	}
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	PutCalcAction newAction = (PutCalcAction) map.get(this);
	if (newAction != null)
	    return newAction;
	VariableAlias newAlias;
	if (variableAlias == null)
	    newAlias = null;
	else
	    newAlias = (VariableAlias) variableAlias.copy(map, fullCopy);
	OperationManager newCalculation;
	if (calculation == null)
	    newCalculation = null;
	else
	    newCalculation
		= (OperationManager) calculation.copy(map, fullCopy);
	newAction = new PutCalcAction(newAlias, newCalculation);
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	if (isValid())
	    return modifiedItem.affectsDisplay(variableAlias
						   .getVariable(modifiedItem));
	return false;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	Object ref = calculation.findReferenceTo(obj);
	if (ref == null && variableAlias != null)
	    ref = variableAlias.findReferenceTo(obj);
	return ref != null;
    }
    
    public void summarize(Summary s) {
	String expr = s.pushValue(calculation);
	Object[] params = { Resource.getText("RE put"), expr,
			    Resource.getText("RE into"), variableAlias };
	s.writeFormat("put action fmt", null, params);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeObject(variableAlias);
	out.writeObject(calculation);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	setVariableAlias((VariableAlias) in.readObject());
	setCalculation((OperationManager) in.readObject());
    }
    
    public PlaywriteView createView() {
	PlaywriteView v = new PutCalcActionView(this);
	return v;
    }
    
    public String toString() {
	String result = null;
	try {
	    result
		= "<PutCalc " + calculation + " into " + variableAlias + ">";
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
