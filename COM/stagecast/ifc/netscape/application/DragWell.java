/* DragWell - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class DragWell extends View implements DragSource
{
    Image image;
    String dataType;
    Object data;
    Border border = BezelBorder.loweredBezel();
    boolean enabled = true;
    
    public DragWell() {
	this(0, 0, 0, 0);
    }
    
    public DragWell(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public DragWell(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
    }
    
    public boolean isTransparent() {
	return false;
    }
    
    public void setImage(Image image) {
	if (this.image != image) {
	    this.image = image;
	    this.draw();
	}
    }
    
    public Image image() {
	return image;
    }
    
    public void setDataType(String string) {
	dataType = string;
    }
    
    public String dataType() {
	return dataType;
    }
    
    public void setData(Object object) {
	data = object;
    }
    
    public Object data() {
	return data;
    }
    
    public void setEnabled(boolean bool) {
	if (enabled != bool) {
	    enabled = bool;
	    this.draw();
	}
    }
    
    public boolean isEnabled() {
	return enabled;
    }
    
    public void setBorder(Border border) {
	if (border == null)
	    border = EmptyBorder.emptyBorder();
	this.border = border;
    }
    
    public Border border() {
	return border;
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (!enabled)
	    return false;
	Image image = image();
	if (image == null)
	    return false;
	Rect rect = new Rect((this.width() - image.width()) / 2,
			     (this.height() - image.height()) / 2,
			     image.width(), image.height());
	if (!rect.contains(mouseevent.x, mouseevent.y))
	    return false;
	new DragSession(this, image, rect.x, rect.y, mouseevent.x,
			mouseevent.y, dataType(), data);
	return true;
    }
    
    public void drawView(Graphics graphics) {
	graphics.setColor(Color.lightGray);
	graphics.fillRect(0, 0, this.width(), this.height());
	Image image = image();
	if (image != null)
	    image.drawCentered(graphics, 0, 0, this.width(), this.height());
	border.drawInRect(graphics, 0, 0, this.width(), this.height());
    }
    
    public View sourceView(DragSession dragsession) {
	return this;
    }
    
    public void dragWasAccepted(DragSession dragsession) {
	/* empty */
    }
    
    public boolean dragWasRejected(DragSession dragsession) {
	return true;
    }
}
