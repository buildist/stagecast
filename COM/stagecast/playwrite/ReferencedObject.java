/* ReferencedObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface ReferencedObject extends Copyable, Deletable, Worldly
{
    public UniqueID getID();
    
    public UniqueID getParentID();
    
    public void setParentID(UniqueID uniqueid);
    
    public boolean isCopyOf(ReferencedObject referencedobject_0_);
}
