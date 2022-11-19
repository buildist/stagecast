/* Door - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

interface Door extends Selectable, Contained
{
    public boolean isDestinationEnd();
    
    public Door getOtherEnd();
    
    public void setOtherEnd(Door door_0_);
}
