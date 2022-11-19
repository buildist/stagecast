/* MenuBorder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class MenuBorder extends Border
{
    Menu menu;
    
    public MenuBorder() {
	this(null);
    }
    
    public MenuBorder(Menu menu) {
	this.menu = menu;
    }
    
    public void setMenu(Menu menu) {
	this.menu = menu;
    }
    
    public int leftMargin() {
	if (menu.isTopLevel() && menu.menuView.type() != 1)
	    return 0;
	return 1;
    }
    
    public int rightMargin() {
	if (menu.isTopLevel() && menu.menuView.type() != 1)
	    return 0;
	return 4;
    }
    
    public int topMargin() {
	if (menu.isTopLevel() && menu.menuView.type() != 1)
	    return 0;
	return 1;
    }
    
    public int bottomMargin() {
	if (menu.isTopLevel() && menu.menuView.type() != 1)
	    return 1;
	return 4;
    }
    
    public void drawInRect(Graphics graphics, int i, int i_0_, int i_1_,
			   int i_2_) {
	if (menu.isTopLevel() && menu.menuView.type() != 1) {
	    graphics.setColor(Color.gray102);
	    graphics.drawLine(i, i_0_ + i_2_ - 1, i + i_1_ - 1,
			      i_0_ + i_2_ - 1);
	} else {
	    graphics.setColor(Color.gray231);
	    graphics.drawLine(i, i_0_, i, i_0_ + i_2_ - 4);
	    graphics.drawLine(i + 1, i_0_, i + i_1_ - 4, i_0_);
	    graphics.setColor(Color.gray153);
	    graphics.drawLine(i + 1, i_0_ + i_2_ - 4, i + i_1_ - 4,
			      i_0_ + i_2_ - 4);
	    graphics.drawLine(i + i_1_ - 4, i_0_ + 1, i + i_1_ - 4,
			      i_0_ + i_2_ - 5);
	    graphics.setColor(Color.darkGray);
	    graphics.drawLine(i + 4, i_0_ + i_2_ - 3, i + i_1_ - 3,
			      i_0_ + i_2_ - 3);
	    graphics.drawLine(i + i_1_ - 3, i_0_ + 4, i + i_1_ - 3,
			      i_0_ + i_2_ - 4);
	    graphics.setColor(Color.gray102);
	    graphics.drawLine(i + 5, i_0_ + i_2_ - 2, i + i_1_ - 2,
			      i_0_ + i_2_ - 2);
	    graphics.drawLine(i + i_1_ - 2, i_0_ + 5, i + i_1_ - 2,
			      i_0_ + i_2_ - 3);
	    graphics.setColor(Color.gray153);
	    graphics.drawLine(i + 6, i_0_ + i_2_ - 1, i + i_1_ - 1,
			      i_0_ + i_2_ - 1);
	    graphics.drawLine(i + i_1_ - 1, i_0_ + 6, i + i_1_ - 1,
			      i_0_ + i_2_ - 2);
	}
    }
}
