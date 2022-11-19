/* CheckButtonImage - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class CheckButtonImage extends Image
{
    boolean drawsCheckMark;
    private static Bitmap checkBitmap;
    
    private Bitmap checkBitmap() {
	if (checkBitmap == null)
	    checkBitmap
		= Bitmap.bitmapNamed("netscape/application/CheckMark.gif");
	return checkBitmap;
    }
    
    public CheckButtonImage() {
	/* empty */
    }
    
    public CheckButtonImage(boolean bool) {
	this();
	drawsCheckMark = bool;
    }
    
    public void setDrawsCheckMark(boolean bool) {
	drawsCheckMark = bool;
    }
    
    public boolean drawsCheckMark() {
	return drawsCheckMark;
    }
    
    public int width() {
	return 16;
    }
    
    public int height() {
	return 16;
    }
    
    public void drawAt(Graphics graphics, int i, int i_0_) {
	Rect rect = Rect.newRect(i, i_0_, width(), height());
	BezelBorder.raisedButtonBezel().drawInRect(graphics, rect);
	graphics.setColor(Color.lightGray);
	graphics.fillRect(rect.x + 2, rect.y + 2, rect.width - 4,
			  rect.height - 4);
	if (drawsCheckMark)
	    checkBitmap().drawCentered(graphics, rect);
	Rect.returnRect(rect);
    }
    
    public void drawScaled(Graphics graphics, int i, int i_1_, int i_2_,
			   int i_3_) {
	Rect rect = Rect.newRect(i, i_1_, i_2_, i_3_);
	BezelBorder.raisedButtonBezel().drawInRect(graphics, rect);
	graphics.setColor(Color.lightGray);
	graphics.fillRect(rect.x + 2, rect.y + 2, rect.width - 4,
			  rect.height - 4);
	if (drawsCheckMark)
	    checkBitmap().drawCentered(graphics, i, i_1_, i_2_, i_3_);
	Rect.returnRect(rect);
    }
}
