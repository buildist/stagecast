/* CacheEnforcerComposite - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

public class CacheEnforcerComposite implements CacheEnforcer
{
    private Vector _enforcers = new Vector();
    
    public CacheEnforcerComposite(CacheEnforcer enforcer1,
				  CacheEnforcer enforcer2) {
	_enforcers.addElement(enforcer1);
	_enforcers.addElement(enforcer2);
    }
    
    public CacheEnforcerComposite addEnforcer(CacheEnforcer enforcer) {
	_enforcers.addElement(enforcer);
	return this;
    }
    
    public void shrinkCache() {
	Enumeration enforcers = _enforcers.elements();
	while (enforcers.hasMoreElements())
	    ((CacheEnforcer) enforcers.nextElement()).shrinkCache();
    }
    
    public void enforceMinLimit() {
	Enumeration enforcers = _enforcers.elements();
	while (enforcers.hasMoreElements())
	    ((CacheEnforcer) enforcers.nextElement()).enforceMinLimit();
    }
    
    public void enforceDefaultLimit() {
	Enumeration enforcers = _enforcers.elements();
	while (enforcers.hasMoreElements())
	    ((CacheEnforcer) enforcers.nextElement()).enforceDefaultLimit();
    }
    
    public void printStatistics() {
	Enumeration enforcers = _enforcers.elements();
	while (enforcers.hasMoreElements())
	    ((CacheEnforcer) enforcers.nextElement()).printStatistics();
    }
}
