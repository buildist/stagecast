/* ValueMessageMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.ListResourceBundle;

import COM.stagecast.playwrite.internationalization.ResourceIDs;
import COM.stagecast.simmer.ClientServerMessages;

public class ValueMessageMap extends ListResourceBundle
    implements ClientServerMessages, VariableSieve.VariableValueFilter,
	       ResourceIDs.StageIDs
{
    private static final Object[][] contents
	= { { "STG ABC ID", "center" }, { "STG ABT ID", "tiled" },
	    { "STG ABS ID", "scaled" } };
    private static ValueMessageMap _this;
    
    static ValueMessageMap getResourceBundle() {
	if (_this == null) {
	    Debug.print(true, "using default us locale");
	    _this = new ValueMessageMap();
	    if (_this == null)
		throw new RuntimeException("can't initialized reader bundle");
	}
	return _this;
    }
    
    static VariableSieve.VariableValueFilter getVariableValueFilter() {
	return getResourceBundle();
    }
    
    public Object[][] getContents() {
	return contents;
    }
    
    public static String getText(String resourceID$) {
	return Resource.getText(getResourceBundle(), resourceID$);
    }
    
    public static String getTextAndFormat(String key, Object[] params) {
	return Resource.getTextAndFormat(getResourceBundle(), key, params);
    }
    
    public Object filterVariableValue(VariableOwner own, Variable var,
				      Object val) {
	Object newVal = val;
	if (val instanceof StorageProxied) {
	    StorageProxied storageProxied = (StorageProxied) val;
	    StorageProxyHelper storageProxyHelper
		= storageProxied.getStorageProxyHelper();
	    newVal = getText(storageProxyHelper.getIDFor(storageProxied));
	}
	if (newVal == val)
	    Debug.print(true,
			"ValueMessageMap:unmapped variable value: " + val);
	return newVal;
    }
}
