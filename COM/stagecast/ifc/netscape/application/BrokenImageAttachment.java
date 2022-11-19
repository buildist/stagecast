/* BrokenImageAttachment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class BrokenImageAttachment extends TextAttachment
{
    private static int WIDTH = 32;
    private static int HEIGHT = 32;
    
    public int width() {
	return WIDTH;
    }
    
    public int height() {
	return HEIGHT;
    }
    
    public void drawInRect(Graphics graphics, Rect rect) {
	Rect rect_0_ = new Rect();
	if (graphics != null && rect != null) {
	    graphics.setColor(Color.lightGray);
	    graphics.fillRect(rect);
	    graphics.setColor(Color.black);
	    graphics.fillRect(rect.x, rect.y, rect.width, 1);
	    graphics.fillRect(rect.x + rect.width - 1, rect.y, 1, rect.height);
	    graphics.fillRect(rect.x, rect.y, 1, rect.height);
	    graphics.fillRect(rect.x, rect.y + rect.height - 1, rect.width, 1);
	}
    }
}
