/* StorageProxy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Vector;

public class StorageProxy
    implements Externalizable, Resolvable, Debug.Constants
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108751611186L;
    private static Vector helperList = new Vector(4);
    private String _helperClassName;
    private String _id;
    
    public static void registerHelper(StorageProxyHelper helper) {
	ASSERT.isTrue(helperList.contains(helper) ^ true);
	helperList.addElement(helper);
    }
    
    static StorageProxy proxyFor(Object obj) {
	if (obj instanceof StorageProxied) {
	    StorageProxied sobj = (StorageProxied) obj;
	    StorageProxyHelper helper = sobj.getStorageProxyHelper();
	    String id = helper.getIDFor(sobj);
	    ASSERT.isTrue(helperList.contains(helper));
	    return id == null ? null : new StorageProxy(helper.getClass(), id);
	}
	return null;
    }
    
    public static Object resolvePetitPalaisProxy(Class helper, String id,
						 WorldBuilder wb) {
	return new StorageProxy(helper, id).resolve(wb);
    }
    
    private StorageProxy(Class helperClass, String id) {
	_helperClassName = helperClass.getName();
	_id = id;
    }
    
    public StorageProxy() {
	/* empty */
    }
    
    public Object resolve(WorldBuilder wb) {
	StorageProxyHelper helper = null;
	for (int i = 0; i < helperList.size(); i++) {
	    helper = (StorageProxyHelper) helperList.elementAt(i);
	    if (helper.getClass().getName().equals(_helperClassName))
		break;
	    helper = null;
	}
	ASSERT.isNotNull(helper);
	return helper.resolveID(_id);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeUTF(_helperClassName);
	out.writeUTF(_id);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_helperClassName = in.readUTF();
	_id = in.readUTF();
	if (_helperClassName.startsWith("com.stagecast.")) {
	    _helperClassName = "COM" + _helperClassName.substring(3);
	    Debug.print("debug.objectstore.detail",
			"replacing helper classname with ", _helperClassName);
	}
    }
}
