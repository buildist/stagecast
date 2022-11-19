/* ScrollViewLineBorder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class ScrollViewLineBorder extends Border
{
    public int leftMargin() {
	return 2;
    }
    
    public int rightMargin() {
	return 2;
    }
    
    public int topMargin() {
	return 2;
    }
    
    public int bottomMargin() {
	return 2;
    }
    
    public void drawInRect(Graphics graphics, int i, int i_0_, int i_1_,
			   int i_2_) {
	graphics.setColor(Color.gray153);
	graphics.drawLine(i, i_0_, i, i_2_ - 2);
	graphics.drawLine(i + 1, i_0_, i + 1, i_2_ - 2);
	graphics.drawLine(i, i_0_, i_1_ - 3, i_0_);
	graphics.drawLine(i, i_0_ + 1, i_1_ - 3, i_0_ + 1);
	graphics.setColor(Color.lightGray);
	graphics.drawLine(i, i_2_ - 1, i_1_ - 1, i_2_ - 1);
	graphics.drawLine(i_1_ - 1, i_0_, i_1_ - 1, i_2_ - 1);
	graphics.setColor(Color.gray231);
	graphics.drawLine(i + 2, i_2_ - 2, i_1_ - 2, i_2_ - 2);
	graphics.drawLine(i_1_ - 2, i_0_, i_1_ - 2, i_2_ - 2);
    }
}
