/* NotOperationType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.util.ResourceBundle;

import COM.stagecast.playwrite.ASSERT;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.StorageProxyHelper;
import COM.stagecast.playwrite.Summary;

class NotOperationType implements OperationType
{
    private OperationType _parentType;
    private String _nameResourceID;
    private ResourceBundle _resourceBundle;
    
    public NotOperationType(String nameResourceID,
			    ResourceBundle resourceBundle,
			    OperationType parentType) {
	ASSERT.isNotNull(parentType);
	ASSERT.isTrue(Op.isBoolean(parentType));
	ASSERT.isNotNull(nameResourceID);
	_nameResourceID = nameResourceID;
	_resourceBundle = resourceBundle;
	_parentType = parentType;
	Op.registerBoolean(this);
    }
    
    public Object operate(Object leftValue, Object rightValue) {
	Object result = _parentType.operate(leftValue, rightValue);
	if (result instanceof Boolean)
	    return new Boolean(((Boolean) result).booleanValue() ^ true);
	return Operation.ERROR;
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
}
