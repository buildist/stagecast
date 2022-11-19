/* AsymmetricalOpType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.util.ResourceBundle;

import COM.stagecast.playwrite.ASSERT;
import COM.stagecast.playwrite.Debug;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.StorageProxyHelper;
import COM.stagecast.playwrite.Summary;

public class AsymmetricalOpType implements OperationType, Debug.Constants
{
    private String _nameResourceID;
    private ResourceBundle _resourceBundle;
    private Class _leftArgumentClass;
    private Class _rightArgumentClass;
    private Operation _operation;
    
    public AsymmetricalOpType(String nameResourceID,
			      ResourceBundle resourceBundle,
			      Class leftArgumentClass,
			      Class rightArgumentClass, Operation operation) {
	ASSERT.isNotNull(nameResourceID);
	ASSERT.isNotNull(leftArgumentClass);
	ASSERT.isNotNull(operation);
	ASSERT.isNotNull(rightArgumentClass);
	_nameResourceID = nameResourceID;
	_resourceBundle = resourceBundle;
	_leftArgumentClass = leftArgumentClass;
	_rightArgumentClass = rightArgumentClass;
	_operation = operation;
	Op.registerNonBoolean(this);
    }
    
    public String getNameResourceID() {
	return _nameResourceID;
    }
    
    public String getLocalName() {
	return Resource.getText(_resourceBundle, _nameResourceID);
    }
    
    public void summarizeOp(Summary s, Object leftArg, Object rightArg) {
	Op.summarizeOp(s, this, leftArg, rightArg);
    }
    
    public Subtotal createSubtotal(OperationManager om) {
	return new NormalSubtotal(om);
    }
    
    public StorageProxyHelper getStorageProxyHelper() {
	return Op.getStorageProxyHelper();
    }
    
    public boolean isValid() {
	if (_nameResourceID == null)
	    return false;
	return true;
    }
    
    public Object operate(Object leftValue, Object rightValue) {
	if (leftValue == Operation.ERROR || rightValue == Operation.ERROR)
	    return Operation.ERROR;
	if (leftValue == null || rightValue == null)
	    return Operation.ERROR;
	if (!_leftArgumentClass.isAssignableFrom(leftValue.getClass()))
	    leftValue = CoercionManager.coerce(leftValue, _leftArgumentClass);
	if (leftValue == null)
	    return Operation.ERROR;
	if (!_rightArgumentClass.isAssignableFrom(rightValue.getClass()))
	    rightValue
		= CoercionManager.coerce(rightValue, _rightArgumentClass);
	if (rightValue == null)
	    return Operation.ERROR;
	return _operation.operate(leftValue, rightValue);
    }
    
    public String toString() {
	return getLocalName();
    }
}
