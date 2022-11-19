/* ContainerView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class ContainerView extends View implements FormElement
{
    private String title = "";
    private Font titleFont;
    private Image image;
    private TextField titleField;
    private Color backgroundColor = Color.lightGray;
    private Color titleColor = Color.black;
    private Border border = BezelBorder.groovedBezel();
    private int imageDisplayStyle = 0;
    private boolean transparent = false;
    static Vector _fieldDescription = null;
    
    public ContainerView() {
	this(0, 0, 0, 0);
    }
    
    public ContainerView(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public ContainerView(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	titleField = new TextField(0, 0, 10, 18);
	titleField.setBorder(null);
	titleField.setTransparent(true);
	titleField.setEditable(false);
	titleField.setVertResizeInstruction(4);
	titleField.setHorizResizeInstruction(2);
	titleField.setJustification(1);
	titleFont = Font.fontNamed("Helvetica", 1, 12);
	layoutParts();
    }
    
    void layoutParts() {
	titleField.removeFromSuperview();
	titleField.setStringValue(title);
	titleField.setFont(titleFont);
	titleField.sizeToMinSize();
	int i;
	int i_3_;
	if (border != null) {
	    i = border.leftMargin();
	    i_3_ = border.rightMargin();
	} else {
	    i = 0;
	    i_3_ = 0;
	}
	if (!titleField.isEmpty()) {
	    titleField.moveTo(i, 0);
	    titleField.sizeTo(this.width() - i - i_3_, titleField.height());
	    this.addSubview(titleField);
	}
    }
    
    public Size minSize() {
	Size size = super.minSize();
	Vector vector = this.subviews();
	int i = 0;
	int i_4_ = 0;
	if (size.width != 0 || size.height != 0)
	    return size;
	if (title != null && !"".equals(title)) {
	    titleField.setStringValue(title);
	    titleField.setFont(titleFont);
	    i = titleField.minSize().width + 6;
	    i_4_ = titleField.minSize().height + 2;
	}
	layoutView(0, 0);
	int i_5_ = vector.count();
	for (int i_6_ = 0; i_6_ < i_5_; i_6_++) {
	    View view = (View) vector.elementAt(i_6_);
	    if (view.bounds().maxX() > i)
		i = view.bounds().maxX();
	    if (view.bounds().maxY() > i_4_)
		i_4_ = view.bounds().maxY();
	}
	size.width = i;
	size.height = i_4_;
	if (border != null) {
	    size.width += border.rightMargin();
	    size.height += border.bottomMargin();
	}
	return size;
    }
    
    public Rect interiorRect() {
	Rect rect;
	if (border != null)
	    rect = border.interiorRect(0, 0, this.width(), this.height());
	else
	    rect = new Rect(0, 0, this.width(), this.height());
	if (titleField._superview != null) {
	    rect.y += titleField.bounds.height;
	    rect.height -= titleField.bounds.height;
	}
	return rect;
    }
    
    public void setTitle(String string) {
	if (string != null)
	    title = string;
	else
	    title = "";
	layoutParts();
	this.setDirty(true);
    }
    
    public String title() {
	return title;
    }
    
    public void setTitleColor(Color color) {
	if (color != null && !color.equals(titleColor)) {
	    titleColor = color;
	    titleField.setTextColor(color);
	    this.setDirty(true);
	}
    }
    
    public Color titleColor() {
	return titleColor;
    }
    
    public void setTitleFont(Font font) {
	if (font == null)
	    titleFont = Font.fontNamed("Helvetica", 1, 12);
	else
	    titleFont = font;
	layoutParts();
	this.setDirty(true);
    }
    
    public Font titleFont() {
	return titleFont;
    }
    
    public void setBackgroundColor(Color color) {
	backgroundColor = color;
	this.setDirty(true);
    }
    
    public Color backgroundColor() {
	return backgroundColor;
    }
    
    public void setBorder(Border border) {
	if (border == null)
	    border = EmptyBorder.emptyBorder();
	this.border = border;
	layoutParts();
	this.setDirty(true);
    }
    
    public Border border() {
	return border;
    }
    
    public void setImage(Image image) {
	this.image = image;
	this.setDirty(true);
    }
    
    public Image image() {
	return image;
    }
    
    public void setImageDisplayStyle(int i) {
	if (i != 0 && i != 2 && i != 1)
	    throw new InconsistencyException("Unknown image display style: "
					     + i);
	imageDisplayStyle = i;
	this.setDirty(true);
    }
    
    public int imageDisplayStyle() {
	return imageDisplayStyle;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
    }
    
    public boolean isTransparent() {
	return transparent || !titleField.isEmpty();
    }
    
    public void drawViewBackground(Graphics graphics) {
	if (!transparent
	    && (image == null
		|| imageDisplayStyle == 0 && backgroundColor != null)) {
	    Rect rect;
	    if (image == null)
		rect = Rect.newRect();
	    else
		rect = Rect.newRect(0, 0, image.width(), image.height());
	    if (!rect.contains(bounds)) {
		rect.setBounds(0, 0, this.width(), this.height());
		if (titleField.isInViewHierarchy()) {
		    rect.moveBy(0, titleField.bounds.height / 2);
		    rect.sizeBy(0, -titleField.bounds.height / 2);
		}
		graphics.setColor(backgroundColor);
		graphics.fillRect(rect);
	    }
	    Rect.returnRect(rect);
	}
	if (image != null)
	    image.drawWithStyle(graphics, 0, 0, this.width(), this.height(),
				imageDisplayStyle);
    }
    
    public void drawViewBorder(Graphics graphics) {
	if (border != null) {
	    Rect rect = Rect.newRect(0, 0, this.width(), this.height());
	    if (titleField.isInViewHierarchy()) {
		rect.moveBy(0, titleField.bounds.height / 2);
		rect.sizeBy(0, -titleField.bounds.height / 2);
	    }
	    if (!titleField.isInViewHierarchy()) {
		border.drawInRect(graphics, rect);
		Rect.returnRect(rect);
	    } else {
		Size size = titleField.minSize();
		Rect rect_7_ = Rect.newRect(titleField.bounds);
		rect_7_.x = rect_7_.midX() - size.width / 2 - 4;
		rect_7_.width = size.width + 8;
		Rect rect_8_ = Rect.newRect(rect);
		rect_8_.width = rect_7_.x - rect.x;
		graphics.pushState();
		graphics.setClipRect(rect_8_);
		border.drawInRect(graphics, rect);
		graphics.popState();
		rect_8_.x = rect_7_.maxX();
		rect_8_.width = rect.maxX() - rect_7_.maxX();
		graphics.pushState();
		graphics.setClipRect(rect_8_);
		border.drawInRect(graphics, rect);
		graphics.popState();
		rect_8_.x = rect_7_.x;
		rect_8_.y = rect_7_.maxY();
		rect_8_.width = rect_7_.width;
		rect_8_.height = rect.maxY() - rect_7_.maxY();
		graphics.pushState();
		graphics.setClipRect(rect_8_);
		border.drawInRect(graphics, rect);
		graphics.popState();
		Rect.returnRect(rect_8_);
		Rect.returnRect(rect_7_);
		Rect.returnRect(rect);
	    }
	}
    }
    
    public void drawView(Graphics graphics) {
	drawViewBackground(graphics);
    }
    
    public void drawSubviews(Graphics graphics) {
	super.drawSubviews(graphics);
	drawViewBorder(graphics);
    }
    
    public void layoutView(int i, int i_9_) {
	if (titleField.isInViewHierarchy())
	    titleField.removeFromSuperview();
	super.layoutView(i, i_9_);
	layoutParts();
    }
    
    public String formElementText() {
	if (titleField != null)
	    return titleField.stringValue();
	return "";
    }
}
