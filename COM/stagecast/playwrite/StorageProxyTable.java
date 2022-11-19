/* StorageProxyTable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Vector;

public abstract class StorageProxyTable implements StorageProxyHelper
{
    private final Vector proxyTable = new Vector();
    
    private static class ProxyEntry
    {
	String _id;
	StorageProxied _value;
	
	ProxyEntry(String id, StorageProxied value) {
	    _id = id.intern();
	    _value = value;
	}
    }
    
    public final void registerProxy(String id, StorageProxied value) {
	ASSERT.isNull(resolveID(id));
	proxyTable.addElement(new ProxyEntry(id, value));
    }
    
    public final String getIDFor(StorageProxied obj) {
	for (int i = 0; i < proxyTable.size(); i++) {
	    ProxyEntry pe = (ProxyEntry) proxyTable.elementAt(i);
	    if (pe._value == obj)
		return pe._id;
	}
	return null;
    }
    
    public final Object resolveID(String id) {
	id = id.intern();
	for (int i = 0; i < proxyTable.size(); i++) {
	    ProxyEntry pe = (ProxyEntry) proxyTable.elementAt(i);
	    if (pe._id == id)
		return pe._value;
	}
	return null;
    }
    
    public StorageProxied[] getProxied() {
	StorageProxied[] sp = new StorageProxied[proxyTable.size()];
	for (int i = 0; i < proxyTable.size(); i++)
	    sp[i] = ((ProxyEntry) proxyTable.elementAt(i))._value;
	return sp;
    }
}
