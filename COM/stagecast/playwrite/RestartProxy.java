/* RestartProxy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Hashtable;

import COM.stagecast.ifc.netscape.util.Vector;

class RestartProxy implements Externalizable, Resolvable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108754822450L;
    private static final Hashtable registryTable = new Hashtable(3);
    private int _id;
    
    static int indexFor(World world, Object obj) {
	Vector registry = (Vector) registryTable.get(world);
	for (int i = 0; i < registry.size(); i++) {
	    if (obj == registry.elementAt(i))
		return i;
	}
	return -1;
    }
    
    static RestartProxy proxyFor(World world, Object obj) {
	if (obj == null)
	    return null;
	if (obj instanceof CharacterInstance)
	    return null;
	Vector registry = (Vector) registryTable.get(world);
	if (registry == null) {
	    registry = new Vector(100);
	    registryTable.put(world, registry);
	}
	int pos = registry.size();
	registry.addElement(obj);
	return new RestartProxy(pos);
    }
    
    static void emptyOut(World world) {
	Vector registry = (Vector) registryTable.remove(world);
	if (registry != null)
	    registry.removeAllElements();
    }
    
    private RestartProxy(int id) {
	_id = id;
    }
    
    public RestartProxy() {
	/* empty */
    }
    
    public Object resolve(WorldBuilder wb) {
	Vector registry = (Vector) registryTable.get(wb.getTargetWorld());
	return registry.elementAt(_id);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(_id);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_id = in.readInt();
    }
}
