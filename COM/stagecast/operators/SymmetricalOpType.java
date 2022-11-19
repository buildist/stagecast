/* SymmetricalOpType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.ASSERT;
import COM.stagecast.playwrite.Debug;
import COM.stagecast.playwrite.PlaywriteInternalError;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.StorageProxyHelper;
import COM.stagecast.playwrite.Summary;

public class SymmetricalOpType implements OperationType, Debug.Constants
{
    private String _nameResourceID;
    private ResourceBundle _resourceBundle;
    private transient Hashtable _operations;
    private transient Vector _defaults;
    private transient Object _identityObject;
    private transient boolean _permits2Nulls = false;
    
    public SymmetricalOpType(String nameResourceID,
			     ResourceBundle resourceBundle,
			     boolean booleanOp) {
	ASSERT.isNotNull(nameResourceID);
	_nameResourceID = nameResourceID;
	_resourceBundle = resourceBundle;
	_operations = new Hashtable(10);
	_defaults = new Vector(2);
	if (booleanOp)
	    Op.registerBoolean(this);
	else
	    Op.registerNonBoolean(this);
    }
    
    public boolean isValid() {
	if (_operations == null)
	    return false;
	if (_nameResourceID == null)
	    return false;
	return true;
    }
    
    public String getLocalName() {
	return Resource.getText(_resourceBundle, _nameResourceID);
    }
    
    public final String getNameResourceID() {
	return _nameResourceID;
    }
    
    public StorageProxyHelper getStorageProxyHelper() {
	return Op.getStorageProxyHelper();
    }
    
    public void summarizeOp(Summary s, Object leftArg, Object rightArg) {
	Op.summarizeOp(s, this, leftArg, rightArg);
    }
    
    public Subtotal createSubtotal(OperationManager om) {
	return new NormalSubtotal(om);
    }
    
    public final void setIdentity(Object iObj) {
	if (_identityObject == null)
	    _identityObject = iObj;
	else
	    throw new PlaywriteInternalError("illegal identity reset");
    }
    
    public final void setIdentityX(Object iObj) {
	setIdentity(iObj);
	_permits2Nulls = true;
    }
    
    public final Object getIdentity() {
	return _identityObject;
    }
    
    public Operation addOperation(Class type, Operation o) {
	return (Operation) _operations.put(type, o);
    }
    
    public void addDefault(Class type, Operation o) {
	if (_defaults == null)
	    _defaults = new Vector(6);
	_defaults.addElement(type);
	_defaults.addElement(o);
    }
    
    public Object operate(Object leftValue, Object rightValue) {
	if (leftValue == Operation.ERROR || rightValue == Operation.ERROR)
	    return Operation.ERROR;
	if (!_permits2Nulls && leftValue == null && rightValue == null)
	    return Operation.ERROR;
	if (leftValue == null)
	    leftValue = _identityObject;
	if (rightValue == null)
	    rightValue = _identityObject;
	if (leftValue == null || rightValue == null)
	    return Operation.ERROR;
	Object commonType
	    = CoercionManager.coerce(leftValue, rightValue.getClass());
	if (commonType == null) {
	    commonType
		= CoercionManager.coerce(rightValue, leftValue.getClass());
	    if (commonType != null)
		rightValue = commonType;
	} else
	    leftValue = commonType;
	Operation o = null;
	if (commonType != null)
	    o = (Operation) _operations.get(commonType.getClass());
    while_2_:
	do {
	    if (o == null) {
		Debug.print("debug.operation.manager", "no operation ",
			    getLocalName(), " for ", leftValue.getClass());
		Debug.print("debug.operation.manager", " searching ",
			    getLocalName(), " for  a type that ",
			    leftValue.getClass(), " can be converted to");
		Object[] knownTypes = _operations.keysArray();
		Object newLeftValue = null;
		Object newRightValue = null;
		if (knownTypes != null) {
		    for (int i = 0; i < knownTypes.length; i++) {
			newLeftValue
			    = CoercionManager.coerce(leftValue,
						     (Class) knownTypes[i]);
			if (newLeftValue != null) {
			    newRightValue
				= CoercionManager.coerce(rightValue,
							 ((Class)
							  knownTypes[i]));
			    if (newRightValue != null) {
				leftValue = newLeftValue;
				rightValue = newRightValue;
				o = ((Operation)
				     _operations.get((Class) knownTypes[i]));
				Debug.print("debug.operation.manager",
					    getLocalName(),
					    " op found by type resolution");
				break while_2_;
			    }
			}
		    }
		}
		Debug.print("debug.operation.manager", "attempting default ",
			    getLocalName(), " for ", leftValue.getClass());
		int size = _defaults.size();
		Class defaultClass = null;
		for (int i = 0; i < size; i += 2) {
		    defaultClass = (Class) _defaults.elementAt(i);
		    if (defaultClass.isInstance(leftValue)
			&& (commonType != null
			    || defaultClass.isInstance(rightValue))) {
			o = (Operation) _defaults.elementAt(i + 1);
			Debug.print("debug.operation.manager", "default ",
				    getLocalName(), " op found");
			break while_2_;
		    }
		}
		if (o != null) {
		    Debug.print(true, "ERROR: Operation found but not used.");
		    Debug.stackTrace();
		}
		Debug.print("debug.operation.manager", "no ", getLocalName(),
			    " for ", leftValue.getClass(), " and ",
			    rightValue.getClass());
		return Operation.ERROR;
	    }
	} while (false);
	return o.operate(leftValue, rightValue);
    }
    
    public String toString() {
	return getLocalName();
    }
}
