/* Scrollable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface Scrollable
{
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    public int lengthOfScrollViewForAxis(int i);
    
    public int lengthOfContentViewForAxis(int i);
    
    public int positionOfContentViewForAxis(int i);
    
    public void scrollTo(int i, int i_0_);
    
    public void scrollBy(int i, int i_1_);
}
