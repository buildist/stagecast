/* InternalWindowBorder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class InternalWindowBorder extends Border
{
    Image indentLeftImage;
    Image indentRightImage;
    Image leftResizeImage;
    Image rightResizeImage;
    InternalWindow window;
    static final int TITLE_BAR_INDENT_OFFSET = 1;
    
    public InternalWindowBorder() {
	this(null);
    }
    
    public InternalWindowBorder(InternalWindow internalwindow) {
	window = internalwindow;
	indentLeftImage
	    = Bitmap.bitmapNamed("netscape/application/TitleBarLeft.gif");
	indentRightImage
	    = Bitmap.bitmapNamed("netscape/application/TitleBarRight.gif");
	leftResizeImage
	    = Bitmap.bitmapNamed("netscape/application/ResizeLeft.gif");
	rightResizeImage
	    = Bitmap.bitmapNamed("netscape/application/ResizeRight.gif");
    }
    
    public void setWindow(InternalWindow internalwindow) {
	window = internalwindow;
    }
    
    public InternalWindow window() {
	return window;
    }
    
    public int leftMargin() {
	return 3;
    }
    
    public int rightMargin() {
	return 2;
    }
    
    public int topMargin() {
	return 22;
    }
    
    public int bottomMargin() {
	if (window.isResizable())
	    return 11;
	return 2;
    }
    
    public int resizePartWidth() {
	if (!window.isResizable())
	    return 0;
	return leftResizeImage.width();
    }
    
    public void drawTitleBar(Graphics graphics, int i, int i_0_, int i_1_,
			     int i_2_) {
	int i_3_ = topMargin();
	Rect rect = Rect.newRect(i, i_0_, i_1_, i_3_);
	if (!graphics.clipRect().intersects(rect))
	    Rect.returnRect(rect);
	else {
	    Rect.returnRect(rect);
	    int i_4_ = i_1_ - 1;
	    int i_5_ = i_3_ - 1;
	    graphics.setColor(Color.lightGray);
	    graphics.fillRect(i + 1, i_0_ + 1, i_4_ - 1, i_5_ - 1);
	    graphics.setColor(Color.gray153);
	    graphics.drawPoint(i, i_0_);
	    graphics.drawLine(i + 1, i_0_, i_4_, i_0_);
	    graphics.drawPoint(i + 1, i_5_);
	    graphics.drawLine(i, i_0_ + 1, i, i_5_);
	    graphics.setColor(Color.white);
	    graphics.drawLine(i + 2, i_0_ + 1, i_4_ - 2, i_0_ + 1);
	    graphics.drawPoint(i + 2, i_0_ + 2);
	    graphics.drawLine(i + 1, i_0_ + 2, i + 1, i_5_ - 2);
	    graphics.setColor(Color.gray153);
	    graphics.drawLine(i + 2, i_5_ - 1, i_4_ - 2, i_5_ - 1);
	    graphics.drawPoint(i_4_ - 2, i_5_ - 2);
	    graphics.drawLine(i_4_ - 1, i_0_ + 2, i_4_ - 1, i_5_ - 2);
	    graphics.setColor(Color.gray102);
	    graphics.drawLine(i + 2, i_5_, i_4_, i_5_);
	    graphics.drawLine(i_4_, i_0_ + 1, i_4_, i_5_);
	    graphics.drawPoint(i_4_ - 1, i_5_ - 1);
	    int i_6_ = 25 + indentLeftImage.width();
	    int i_7_ = i_1_ - 25 - indentRightImage.width() - i_6_;
	    boolean bool = window.isMain();
	    if (bool) {
		indentLeftImage.drawAt(graphics, 25, 1);
		indentRightImage
		    .drawAt(graphics, i_1_ - indentRightImage.width() - 24, 1);
		Rect rect_8_ = Rect.newRect(i_6_, 3, i_7_,
					    indentLeftImage.height() - 4);
		graphics.setColor(Color.gray153);
		graphics.drawLine(rect_8_.x, rect_8_.y + 1, rect_8_.maxX(),
				  rect_8_.y + 1);
		graphics.setColor(Color.white);
		graphics.drawLine(rect_8_.x + 1, rect_8_.maxY() - 2,
				  rect_8_.maxX(), rect_8_.maxY() - 2);
		Rect.returnRect(rect_8_);
	    }
	    Rect rect_9_
		= Rect.newRect(i_6_, 0, i_7_, indentLeftImage.height() - 2);
	    graphics.pushState();
	    graphics.setClipRect(rect_9_);
	    graphics.setColor(Color.darkGray);
	    graphics.setFont(window.font());
	    graphics.drawStringInRect(window.title(), rect_9_, 1);
	    graphics.popState();
	    Rect.returnRect(rect_9_);
	}
    }
    
    public void drawLeftBorder(Graphics graphics, int i, int i_10_, int i_11_,
			       int i_12_) {
	Rect rect = Rect.newRect(0, 0, leftMargin(), i_12_);
	if (!graphics.clipRect().intersects(rect))
	    Rect.returnRect(rect);
	else {
	    Rect.returnRect(rect);
	    int i_13_ = topMargin();
	    graphics.setColor(Color.gray153);
	    graphics.drawLine(0, i_13_, 0, i_12_ - 1);
	    graphics.setColor(Color.white);
	    graphics.drawLine(1, i_13_, 1, i_12_ - 2);
	    graphics.setColor(Color.lightGray);
	    graphics.drawLine(2, i_13_, 2, i_12_ - 3);
	    MenuView menuview = window.menuView();
	    if (menuview != null) {
		graphics.setColor(Color.gray102);
		graphics.drawLine(1, i_13_ + menuview.height() - 1, 2,
				  i_13_ + menuview.height() - 1);
	    }
	}
    }
    
    public void drawRightBorder(Graphics graphics, int i, int i_14_, int i_15_,
				int i_16_) {
	Rect rect
	    = Rect.newRect(i_15_ - rightMargin(), 0, rightMargin(), i_16_);
	if (!graphics.clipRect().intersects(rect))
	    Rect.returnRect(rect);
	else {
	    Rect.returnRect(rect);
	    int i_17_ = topMargin();
	    int i_18_ = i_16_ - bottomMargin();
	    graphics.setColor(Color.gray102);
	    graphics.drawLine(i_15_ - 1, i_17_, i_15_ - 1, i_18_);
	    graphics.setColor(Color.gray153);
	    graphics.drawLine(i_15_ - 2, i_17_, i_15_ - 2, i_18_ - 1);
	    MenuView menuview = window.menuView();
	    if (menuview != null) {
		graphics.setColor(Color.gray102);
		graphics.drawPoint(i + i_15_ - 2,
				   i_17_ + menuview.height() - 1);
	    }
	}
    }
    
    public void drawBottomBorder(Graphics graphics, int i, int i_19_,
				 int i_20_, int i_21_) {
	Rect rect
	    = Rect.newRect(0, i_21_ - bottomMargin(), i_20_, bottomMargin());
	if (!graphics.clipRect().intersects(rect))
	    Rect.returnRect(rect);
	else {
	    Rect.returnRect(rect);
	    int i_22_ = bottomMargin();
	    int i_23_ = i_21_ - i_22_;
	    graphics.setColor(Color.gray102);
	    graphics.drawLine(1, i_21_ - 1, i_20_ - 1, i_21_ - 1);
	    graphics.setColor(Color.gray153);
	    graphics.drawLine(2, i_21_ - 2, i_20_ - 2, i_21_ - 2);
	    graphics.setColor(Color.lightGray);
	    graphics.fillRect(2, i_23_, i_20_ - 3, i_22_ - 2);
	    graphics.setColor(Color.gray102);
	    graphics.drawLine(i_20_ - 1, i_23_, i_20_ - 1, i_21_ - 1);
	    graphics.setColor(Color.gray153);
	    graphics.drawLine(i_20_ - 2, i_23_, i_20_ - 2, i_21_ - 2);
	    if (window.isResizable() && leftResizeImage != null
		&& rightResizeImage != null) {
		leftResizeImage.drawAt(graphics, 0,
				       i_21_ - leftResizeImage.height());
		rightResizeImage.drawAt(graphics,
					i_20_ - rightResizeImage.width(),
					i_21_ - rightResizeImage.height());
	    }
	}
    }
    
    public void drawInRect(Graphics graphics, int i, int i_24_, int i_25_,
			   int i_26_) {
	drawTitleBar(graphics, i, i_24_, i_25_, i_26_);
	drawLeftBorder(graphics, i, i_24_, i_25_, i_26_);
	drawRightBorder(graphics, i, i_24_, i_25_, i_26_);
	drawBottomBorder(graphics, i, i_24_, i_25_, i_26_);
    }
}
