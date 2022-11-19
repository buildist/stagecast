/* ImageAttachment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class ImageAttachment extends TextAttachment
{
    private Image image;
    boolean incrementalBitmap;
    int width;
    int height;
    
    public ImageAttachment() {
	image = null;
	incrementalBitmap = false;
    }
    
    public ImageAttachment(Image image) {
	this.image = image;
	incrementalBitmap = false;
    }
    
    ImageAttachment(Bitmap bitmap, int i, int i_0_) {
	image = bitmap;
	width = i;
	height = i_0_;
	incrementalBitmap = true;
    }
    
    public void setImage(Image image) {
	this.image = image;
    }
    
    public Image image() {
	return image;
    }
    
    public int width() {
	if (incrementalBitmap)
	    return width;
	if (image != null)
	    return image.width();
	return 0;
    }
    
    public int height() {
	if (incrementalBitmap)
	    return height;
	if (image != null)
	    return image.height();
	return 0;
    }
    
    public void drawInRect(Graphics graphics, Rect rect) {
	if (graphics != null && rect != null) {
	    Rect rect_1_ = graphics.clipRect();
	    if (image != null)
		image.drawAt(graphics, rect.x, rect.y);
	}
    }
}
