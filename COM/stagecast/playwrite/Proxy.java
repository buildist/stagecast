/* Proxy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Hashtable;

interface Proxy extends Visible
{
    public boolean isProxy();
    
    public void setProxy(boolean bool);
    
    public Object makeProxy(Hashtable hashtable);
    
    public void makeReal(Object object, Hashtable hashtable);
}
