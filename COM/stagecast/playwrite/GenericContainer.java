/* GenericContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface GenericContainer
{
    public void add(Contained contained);
    
    public void remove(Contained contained);
    
    public boolean allowRemove(Contained contained);
    
    public void update(Contained contained);
}
