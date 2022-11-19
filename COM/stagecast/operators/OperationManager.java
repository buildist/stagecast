/* OperationManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.CharacterInstance;
import COM.stagecast.playwrite.Copyable;
import COM.stagecast.playwrite.Debug;
import COM.stagecast.playwrite.PlaywriteInternalError;
import COM.stagecast.playwrite.PlaywriteView;
import COM.stagecast.playwrite.ReferencedObject;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.Summary;
import COM.stagecast.playwrite.World;
import COM.stagecast.playwrite.WorldInStream;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public final class OperationManager
    implements Subtotal.Creator, Externalizable, ResourceIDs.OperationIDs,
	       ResourceIDs.RuleEditorIDs, Debug.Constants
{
    static final int storeVersion = 3;
    static final long serialVersionUID = -3819410108755215666L;
    private Object _leftSide;
    private Object _rightSide;
    private boolean _evalLeft;
    private boolean _evalRight;
    private OperationType _operationType;
    private transient Subtotal _subtotal;
    
    public static final int getStoreVersion() {
	return 3;
    }
    
    public OperationManager(Object left, Object right, OperationType opType) {
	setLeftSide(left);
	setRightSide(right);
	setOperationType(opType);
    }
    
    public OperationManager() {
	/* empty */
    }
    
    public boolean isValid() {
	if (_operationType == null)
	    return false;
	if (_evalLeft && ((Expression) _leftSide).isValid() == false)
	    return false;
	if (_evalRight && ((Expression) _rightSide).isValid() == false)
	    return false;
	return true;
    }
    
    public final Object getLeftSide() {
	return _leftSide;
    }
    
    public final void setLeftSide(Object foo) {
	_leftSide = foo;
	_evalLeft = _leftSide instanceof Expression;
	notifySubtotal();
    }
    
    private void notifySubtotal() {
	if (_subtotal != null)
	    _subtotal.expressionChanged();
    }
    
    public final Object getRightSide() {
	return _rightSide;
    }
    
    public final void setRightSide(Object foo) {
	_rightSide = foo;
	_evalRight = _rightSide instanceof Expression;
	notifySubtotal();
    }
    
    public final void setOperationType(OperationType opType) {
	_operationType = opType;
    }
    
    public final OperationType getOperationType() {
	return _operationType;
    }
    
    public final String getName() {
	return _operationType.getLocalName();
    }
    
    public final boolean isBooleanOp() {
	return Op.isBoolean(_operationType);
    }
    
    public final boolean getNot() {
	return NotOperationType.class
		   .isAssignableFrom(_operationType.getClass());
    }
    
    public final boolean isSameOpAs(OperationManager op2) {
	return op2._operationType == _operationType;
    }
    
    public final boolean isSameOpAs(OperationType type) {
	return type == _operationType;
    }
    
    public final void changeToOperation(OperationType newOperation) {
	_operationType = newOperation;
    }
    
    public Object eval() {
	Object leftValue
	    = _evalLeft ? ((Expression) _leftSide).eval() : _leftSide;
	Object rightValue
	    = _evalRight ? ((Expression) _rightSide).eval() : _rightSide;
	Object result = _operationType.operate(leftValue, rightValue);
	return result;
    }
    
    public Object findReferenceTo(ReferencedObject obj) {
	if (_evalLeft) {
	    Object ref = ((Expression) _leftSide).findReferenceTo(obj);
	    if (ref != null)
		return ref;
	} else if (_leftSide == obj)
	    return _leftSide;
	if (_evalRight) {
	    Object ref = ((Expression) _rightSide).findReferenceTo(obj);
	    if (ref != null)
		return ref;
	} else if (_rightSide == obj)
	    return _rightSide;
	return null;
    }
    
    public Expression evaluates(Expression obj) {
	Expression ref = null;
	if (_leftSide == obj || _rightSide == obj)
	    ref = this;
	else if (_evalLeft)
	    ref = ((Expression) _leftSide).evaluates(obj);
	if (ref == null && _evalRight)
	    ref = ((Expression) _rightSide).evaluates(obj);
	return ref;
    }
    
    public Expression[] subexpressions() {
	Expression[] subs = null;
	if (_evalLeft && _evalRight)
	    subs = new Expression[] { (Expression) _leftSide,
				      (Expression) _rightSide };
	else if (_evalLeft)
	    subs = new Expression[] { (Expression) _leftSide };
	else if (_evalRight)
	    subs = new Expression[] { (Expression) _rightSide };
	return subs;
    }
    
    public Subtotal getSubtotal() {
	if (_subtotal == null)
	    _subtotal = createSubtotal();
	return _subtotal;
    }
    
    public Subtotal createSubtotal() {
	return _operationType.createSubtotal(this);
    }
    
    public void setSubtotal(Subtotal subtotal) {
	if (_subtotal != null && _subtotal != subtotal) {
	    Debug.stackTrace();
	    Debug.print(true,
			("WARNING: duplicate subtotal generated for " + this
			 + " old=" + _subtotal + " new=" + subtotal));
	}
	_subtotal = subtotal;
    }
    
    public void clearCaches() {
	if (_evalLeft && _leftSide instanceof Subtotal)
	    ((Subtotal) _leftSide).clearCache();
	if (_evalRight && _rightSide instanceof Subtotal)
	    ((Subtotal) _rightSide).clearCache();
    }
    
    public void summarize(Summary s) {
	_operationType.summarizeOp(s, getLeftSide(), getRightSide());
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	throw new PlaywriteInternalError("This method should not be used.");
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	OperationManager newOp = (OperationManager) map.get(this);
	if (newOp != null)
	    return newOp;
	Object newLeft = copyTerm(_leftSide, map, fullCopy);
	Object newRight = copyTerm(_rightSide, map, fullCopy);
	newOp = new OperationManager(newLeft, newRight, _operationType);
	map.put(this, newOp);
	return newOp;
    }
    
    private Object copyTerm(Object term, Hashtable map, boolean fullCopy) {
	if (term == null)
	    return null;
	if (term instanceof Copyable)
	    return ((Copyable) term).copy(map, fullCopy);
	Debug.print("debug.copy", "Can't copy ", term, " in ", this);
	return term;
    }
    
    public PlaywriteView createView() {
	return null;
    }
    
    public PlaywriteView createIconView() {
	PlaywriteView calcView
	    = new PlaywriteView(Resource.getButtonImage("RE c"));
	calcView.setModelObject(this);
	calcView.setAutoEditable(true);
	return calcView;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeObject(_operationType);
	out.writeObject(_leftSide);
	out.writeObject(_rightSide);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	WorldInStream wis = (WorldInStream) in;
	int version = wis.loadVersion(OperationManager.class);
	switch (version) {
	case 1: {
	    String typeName = in.readUTF();
	    _operationType = Op.getv1OperatorByName(typeName);
	    if (_operationType == null)
		throw new PlaywriteInternalError("No operator named "
						 + typeName);
	    break;
	}
	case 2: {
	    String typeResourceID = in.readUTF();
	    _operationType = Op.getOperatorByID(typeResourceID);
	    if (_operationType == null)
		throw new PlaywriteInternalError("No operator with id "
						 + typeResourceID);
	    break;
	}
	case 3:
	    _operationType = (OperationType) in.readObject();
	    break;
	default:
	    throw new PlaywriteInternalError("unknown version");
	}
	if (_operationType == null)
	    throw new PlaywriteInternalError("No operator");
	setLeftSide(validateSide(wis));
	setRightSide(validateSide(wis));
	switch (version) {
	case 1:
	    in.readBoolean();
	}
    }
    
    private Object validateSide(WorldInStream in)
	throws IOException, ClassNotFoundException {
	Object side = in.readObject();
	if (side instanceof CharacterInstance) {
	    Debug.print(true, "Illegal character in operation");
	    in.getTargetWorld().incrementBadCharacterCount();
	    return null;
	}
	return side;
    }
    
    public String toString() {
	String left;
	if (_leftSide != null)
	    left = _leftSide.toString();
	else
	    left = "";
	String right;
	if (_rightSide != null)
	    right = _rightSide.toString();
	else
	    right = "";
	return left + " " + getName() + " " + right;
    }
}
