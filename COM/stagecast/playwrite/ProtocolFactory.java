/* ProtocolFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Hashtable;

public class ProtocolFactory
{
    private Hashtable _managers = new Hashtable(10);
    
    ProtocolFactory() {
	/* empty */
    }
    
    public void registerProtocolManager(Class cls, ProtocolMgr mgr) {
	_managers.put(cls, mgr);
    }
    
    public ProtocolMgr getProtocolMgr(Object target) {
	return (ProtocolMgr) _managers.get(target.getClass());
    }
}
