/* Copyable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Hashtable;

public interface Copyable extends Cloneable
{
    public Object copy();
    
    public Object copy(World world);
    
    public Object copy(Hashtable hashtable, boolean bool);
}
