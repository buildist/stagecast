/* UnaryExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.unaryoperators;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.CoercionManager;
import COM.stagecast.operators.Expression;
import COM.stagecast.operators.Operation;
import COM.stagecast.operators.Subtotal;
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

public final class UnaryExpression
    implements Subtotal.Creator, Externalizable, ResourceIDs.RuleEditorIDs,
	       ResourceIDs.UnaryOpIDs, Debug.Constants
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108751742258L;
    private Object _argument;
    private transient boolean _evalArgument;
    private UnaryOperation _unaryOperation;
    private transient Subtotal _subtotal;
    
    public static final int getStoreVersion() {
	return 1;
    }
    
    public UnaryExpression(UnaryOperation unaryOperation, Object argument) {
	setArgument(argument);
	setUnaryOperation(unaryOperation);
	if (unaryOperation == null)
	    System.out.println("didn't get an argument for unaryoperation");
    }
    
    public UnaryExpression() {
	/* empty */
    }
    
    public boolean isValid() {
	if (_argument == null)
	    return false;
	if (_evalArgument && ((Expression) _argument).isValid() == false)
	    return false;
	return true;
    }
    
    public final Object getArgument() {
	return _argument;
    }
    
    public final void setArgument(Object foo) {
	_argument = foo;
	_evalArgument = _argument instanceof Expression;
	if (_subtotal != null)
	    _subtotal.expressionChanged();
    }
    
    public void setUnaryOperation(UnaryOperation opType) {
	_unaryOperation = opType;
    }
    
    public UnaryOperation getUnaryOperation() {
	return _unaryOperation;
    }
    
    public final String getName() {
	return _unaryOperation.getLocalName();
    }
    
    public final boolean isSameOpAs(UnaryExpression op2) {
	return op2._unaryOperation == _unaryOperation;
    }
    
    public final boolean isSameOpAs(UnaryOperation type) {
	return type == _unaryOperation;
    }
    
    public final void changeToOperation(UnaryOperation newOperation) {
	_unaryOperation = newOperation;
    }
    
    public Object eval() {
	UnaryOperation uop = getUnaryOperation();
	Object argValue
	    = _evalArgument ? ((Expression) _argument).eval() : _argument;
	if (argValue == Operation.ERROR || uop == null)
	    return Operation.ERROR;
	if (argValue == null)
	    return Operation.ERROR;
	Class argumentClass = uop.getArgumentClass();
	if (argumentClass != null) {
	    if (!argumentClass.isAssignableFrom(argValue.getClass()))
		argValue = CoercionManager.coerce(argValue, argumentClass);
	    if (argValue == null)
		return Operation.ERROR;
	}
	Object result = _unaryOperation.uoperate(argValue);
	if (result instanceof Double) {
	    Double d = (Double) result;
	    long l = d.longValue();
	    if (d.equals(new Double((double) l)))
		return new Long(l);
	}
	return result;
    }
    
    public Object findReferenceTo(ReferencedObject obj) {
	if (_evalArgument) {
	    Object ref = ((Expression) _argument).findReferenceTo(obj);
	    if (ref != null)
		return ref;
	} else if (_argument == obj)
	    return _argument;
	return null;
    }
    
    public Expression evaluates(Expression obj) {
	Expression ref = null;
	if (_argument == obj)
	    ref = this;
	else if (_evalArgument)
	    ref = ((Expression) _argument).evaluates(obj);
	return ref;
    }
    
    public Expression[] subexpressions() {
	if (_evalArgument)
	    return new Expression[] { (Expression) _argument };
	return null;
    }
    
    public Subtotal getSubtotal() {
	if (_subtotal == null)
	    _subtotal = createSubtotal();
	return _subtotal;
    }
    
    public Subtotal createSubtotal() {
	return _unaryOperation.createSubtotal(this);
    }
    
    public void setSubtotal(Subtotal subtotal) {
	_subtotal = subtotal;
    }
    
    public void clearCaches() {
	if (_evalArgument && _argument instanceof Subtotal)
	    ((Subtotal) _argument).clearCache();
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	throw new PlaywriteInternalError("This method should not be used.");
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	UnaryExpression newOp = (UnaryExpression) map.get(this);
	if (newOp != null)
	    return newOp;
	Object newLeft = copyTerm(_argument, map, fullCopy);
	newOp = new UnaryExpression(_unaryOperation, newLeft);
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
	out.writeObject(_unaryOperation);
	out.writeObject(_argument);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(UnaryExpression.class);
	_unaryOperation = (UnaryOperation) in.readObject();
	if (_unaryOperation == null)
	    throw new PlaywriteInternalError("No operator");
	setArgument(in.readObject());
    }
    
    public void summarize(Summary s) {
	String format = _unaryOperation.getDisplayType();
	String printName = _unaryOperation.getLocalName();
	int needsParens = _argument instanceof Expression ? 1 : 2;
	s.writeFormat(format,
		      new Object[] { new Integer(needsParens), printName },
		      new Object[] { _argument });
    }
    
    public String toString() {
	String argString;
	if (_argument != null)
	    argString = _argument.toString();
	else
	    argString = "";
	Object[] params = { getName(), argString };
	return Resource.getTextAndFormat(_unaryOperation.getDisplayType(),
					 params);
    }
}
