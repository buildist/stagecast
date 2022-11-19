/* VariableProxy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

class VariableProxy implements Externalizable, Resolvable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108754822450L;
    String _sysvarID;
    VariableOwner _listOwner;
    
    static VariableProxy proxyFor(Object obj) {
	if (!(obj instanceof Variable))
	    return null;
	Variable v = (Variable) obj;
	if (!v.isSystemVariable())
	    return null;
	return new VariableProxy(v.getSystemType(), v.getListOwner());
    }
    
    private VariableProxy(String sysvarID, VariableOwner listOwner) {
	ASSERT.isNotNull(sysvarID);
	ASSERT.isNotNull(listOwner);
	_sysvarID = sysvarID;
	_listOwner = listOwner;
    }
    
    public VariableProxy() {
	/* empty */
    }
    
    public Object resolve(WorldBuilder wb) {
	Object result
	    = _listOwner.getVariableList().findSystemVariable(_sysvarID);
	if (result == null)
	    result = Variable.newSystemVariable(_sysvarID, _listOwner);
	return result;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeUTF(_sysvarID);
	out.writeObject(_listOwner);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_sysvarID = in.readUTF();
	_listOwner = (VariableOwner) in.readObject();
    }
    
    public String toString() {
	return "VariableProxy" + _sysvarID + " of " + _listOwner;
    }
}
