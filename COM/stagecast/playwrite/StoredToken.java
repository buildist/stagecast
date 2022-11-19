/* StoredToken - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class StoredToken implements StorableToken
{
    private String _localizedName;
    private String _resID;
    private StorageProxyHelper _helper;
    
    StoredToken(String _resID) {
	this(Resource.getText(_resID), _resID, BuiltinProxyTable.helper);
    }
    
    public StoredToken(String localizedName, String resID,
		       StorageProxyHelper helper) {
	_localizedName = localizedName;
	_resID = resID;
	_helper = helper;
	_helper.registerProxy(_resID, this);
    }
    
    public String getLocalName() {
	return _localizedName;
    }
    
    public StorageProxyHelper getStorageProxyHelper() {
	return _helper;
    }
}
