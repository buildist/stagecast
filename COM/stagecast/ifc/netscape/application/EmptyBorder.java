/* EmptyBorder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class EmptyBorder extends Border
{
    private static Border emptyBorder;
    private static Class emptyBorderClass;
    
    public static Border emptyBorder() {
	if (emptyBorder == null)
	    emptyBorder = new EmptyBorder();
	return emptyBorder;
    }
    
    public int leftMargin() {
	return 0;
    }
    
    public int rightMargin() {
	return 0;
    }
    
    public int topMargin() {
	return 0;
    }
    
    public int bottomMargin() {
	return 0;
    }
    
    public void drawInRect(Graphics graphics, int i, int i_0_, int i_1_,
			   int i_2_) {
	/* empty */
    }
    
    private static Class emptyBorderClass() {
	if (emptyBorderClass == null)
	    emptyBorderClass = emptyBorder().getClass();
	return emptyBorderClass;
    }
}
