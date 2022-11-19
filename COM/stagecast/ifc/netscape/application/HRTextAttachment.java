/* HRTextAttachment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class HRTextAttachment extends TextAttachment
{
    private static int WIDTH_OFFSET = 10;
    private static int HEIGHT = 12;
    
    public int width() {
	return this.owner().width();
    }
    
    public int height() {
	return HEIGHT;
    }
    
    public void drawInRect(Graphics graphics, Rect rect) {
	Rect rect_0_ = new Rect();
	if (graphics != null && rect != null) {
	    rect_0_.x = rect.x + WIDTH_OFFSET;
	    rect_0_.width = rect.width - 2 * WIDTH_OFFSET;
	    rect_0_.height = 2;
	    rect_0_.y = rect.y + (HEIGHT - 2) / 2;
	    graphics.setColor(Color.darkGray);
	    graphics.fillRect(rect_0_);
	    rect_0_.y++;
	    rect_0_.height = 1;
	    graphics.setColor(Color.lightGray);
	    graphics.fillRect(rect_0_);
	}
    }
}
