/* ListItem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class ListItem implements Cloneable
{
    ListView listView;
    String command;
    String title;
    Font font;
    Color selectedColor;
    Color textColor;
    Image image;
    Image selectedImage;
    boolean selected;
    boolean enabled = true;
    Object data;
    
    public ListItem() {
	selectedColor = Color.white;
	textColor = Color.black;
    }
    
    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new InconsistencyException(String.valueOf(this)
					     + ": clone() not supported :"
					     + clonenotsupportedexception);
	}
    }
    
    void setListView(ListView listview) {
	listView = listview;
    }
    
    public ListView listView() {
	return listView;
    }
    
    public boolean isTransparent() {
	return selected ^ true;
    }
    
    public void setCommand(String string) {
	command = string;
    }
    
    public String command() {
	return command;
    }
    
    public void setTitle(String string) {
	title = string;
    }
    
    public String title() {
	return title;
    }
    
    public void setSelected(boolean bool) {
	if (!enabled)
	    bool = false;
	selected = bool;
    }
    
    public boolean isSelected() {
	return selected;
    }
    
    public void setEnabled(boolean bool) {
	enabled = bool;
	if (!enabled)
	    selected = false;
    }
    
    public boolean isEnabled() {
	return enabled;
    }
    
    public void setImage(Image image) {
	this.image = image;
    }
    
    public Image image() {
	return image;
    }
    
    public void setSelectedImage(Image image) {
	selectedImage = image;
    }
    
    public Image selectedImage() {
	return selectedImage;
    }
    
    public void setFont(Font font) {
	this.font = font;
    }
    
    public Font font() {
	if (font == null)
	    font = Font.defaultFont();
	return font;
    }
    
    public void setSelectedColor(Color color) {
	selectedColor = color;
    }
    
    public Color selectedColor() {
	return selectedColor;
    }
    
    public void setTextColor(Color color) {
	textColor = color;
    }
    
    public Color textColor() {
	return textColor;
    }
    
    public void setData(Object object) {
	data = object;
    }
    
    public Object data() {
	return data;
    }
    
    public int minWidth() {
	int i = 0;
	if (image != null)
	    i = image.width();
	if (selectedImage != null && selectedImage.width() > i)
	    i = selectedImage.width();
	Font font = font();
	if (font != null)
	    i += font.fontMetrics().stringWidth(title);
	if (i > 0)
	    i += 3;
	return i;
    }
    
    public int minHeight() {
	int i = 0;
	int i_0_ = 0;
	if (image != null)
	    i = image.height();
	if (selectedImage != null && selectedImage.height() > i)
	    i = selectedImage.height();
	Font font = font();
	if (font != null)
	    i_0_ = font.fontMetrics().stringHeight();
	if (i_0_ > i)
	    i = i_0_;
	return i;
    }
    
    protected void drawBackground(Graphics graphics, Rect rect) {
	if (selected) {
	    graphics.setColor(selectedColor);
	    graphics.fillRect(rect);
	}
    }
    
    protected void drawStringInRect(Graphics graphics, String string,
				    Font font, Rect rect, int i) {
	if (listView().isEnabled() && enabled)
	    graphics.setColor(textColor);
	else
	    graphics.setColor(Color.gray);
	graphics.setFont(font);
	graphics.drawStringInRect(string, rect, i);
    }
    
    public void drawInRect(Graphics graphics, Rect rect) {
	drawBackground(graphics, rect);
	Image image;
	if (selected)
	    image = selectedImage;
	else
	    image = this.image;
	int i_1_;
	int i = i_1_ = 0;
	if (this.image != null) {
	    i = this.image.width();
	    i_1_ = this.image.height();
	}
	if (selectedImage != null) {
	    if (selectedImage.width() > i)
		i = selectedImage.width();
	    if (selectedImage.height() > i_1_)
		i_1_ = selectedImage.height();
	}
	if (image != null)
	    image.drawAt(graphics, rect.x, rect.y + (rect.height - i_1_) / 2);
	if (title != null && title.length() > 0) {
	    Rect rect_2_ = Rect.newRect(rect.x + 2 + i, rect.y,
					rect.width - 2 - i, rect.height);
	    drawStringInRect(graphics, title, font(), rect_2_, 0);
	    Rect.returnRect(rect_2_);
	}
    }
}
