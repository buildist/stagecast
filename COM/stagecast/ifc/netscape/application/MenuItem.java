/* MenuItem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class MenuItem implements Cloneable, EventProcessor
{
    Menu submenu;
    Menu supermenu;
    java.awt.MenuItem foundationMenuItem;
    String command;
    String title;
    Target target;
    char commandKey;
    Font font;
    Image checkedImage;
    Image uncheckedImage;
    Image image;
    Image selectedImage;
    Color selectedColor;
    Color selectedTextColor;
    Color textColor;
    Color disabledColor;
    boolean selected;
    boolean separator;
    boolean state;
    boolean enabled;
    Object data;
    
    public MenuItem() {
	this("", '\0', null, null, false);
    }
    
    public MenuItem(String string, String string_0_, Target target) {
	this(string, '\0', string_0_, target, false);
    }
    
    public MenuItem(String string, char c, String string_1_, Target target) {
	this(string, c, string_1_, target, false);
    }
    
    public MenuItem(String string, String string_2_, Target target,
		    boolean bool) {
	this(string, '\0', string_2_, target, bool);
    }
    
    public MenuItem(String string, char c, String string_3_, Target target,
		    boolean bool) {
	commandKey = Character.toUpperCase(c);
	if (!bool)
	    foundationMenuItem = new FoundationMenuItem(string, this);
	else {
	    foundationMenuItem = new FoundationCheckMenuItem(string, this);
	    setUncheckedImage(Bitmap.bitmapNamed
			      ("netscape/application/RadioButtonOff.gif"));
	    setCheckedImage
		(Bitmap.bitmapNamed("netscape/application/RadioButtonOn.gif"));
	    setImage(uncheckedImage);
	    setSelectedImage(uncheckedImage);
	}
	setEnabled(true);
	setFont(new Font("Helvetica", 0, 12));
	setTitle(string);
	setTarget(target);
	setCommand(string_3_);
	selectedColor = Color.white;
	textColor = Color.black;
	selectedTextColor = Color.black;
	disabledColor = Color.gray;
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
    
    java.awt.MenuItem foundationMenuItem() {
	return foundationMenuItem;
    }
    
    public void setSeparator(boolean bool) {
	separator = bool;
	if (bool)
	    setEnabled(true);
    }
    
    public boolean isSeparator() {
	return separator;
    }
    
    public void setData(Object object) {
	data = object;
    }
    
    public Object data() {
	return data;
    }
    
    public void setSubmenu(Menu menu) {
	submenu = menu;
	if (submenu != null)
	    submenu.setSuperitem(this);
    }
    
    public Menu submenu() {
	return submenu;
    }
    
    public boolean hasSubmenu() {
	if (submenu != null)
	    return true;
	return false;
    }
    
    public void setSupermenu(Menu menu) {
	supermenu = menu;
    }
    
    public Menu supermenu() {
	return supermenu;
    }
    
    public void setCommandKey(char c) {
	commandKey = Character.toUpperCase(c);
	setTitle(title);
    }
    
    public char commandKey() {
	return commandKey;
    }
    
    public void setState(boolean bool) {
	state = bool;
	if (foundationMenuItem instanceof FoundationCheckMenuItem) {
	    ((FoundationCheckMenuItem) foundationMenuItem).setState(state);
	    if (state) {
		setImage(checkedImage());
		setSelectedImage(checkedImage());
	    } else {
		setImage(uncheckedImage());
		setSelectedImage(uncheckedImage());
	    }
	}
    }
    
    public boolean state() {
	if (foundationMenuItem instanceof FoundationCheckMenuItem)
	    return state;
	return false;
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
    
    public void setCheckedImage(Image image) {
	checkedImage = image;
	if (foundationMenuItem instanceof FoundationCheckMenuItem
	    && state() == true) {
	    setImage(checkedImage);
	    setSelectedImage(checkedImage);
	}
    }
    
    public Image checkedImage() {
	return checkedImage;
    }
    
    public void setUncheckedImage(Image image) {
	uncheckedImage = image;
	if (foundationMenuItem instanceof FoundationCheckMenuItem
	    && state() == false) {
	    setImage(uncheckedImage);
	    setSelectedImage(uncheckedImage);
	}
    }
    
    public Image uncheckedImage() {
	return uncheckedImage;
    }
    
    public void setSelectedColor(Color color) {
	selectedColor = color;
    }
    
    public Color selectedColor() {
	return selectedColor;
    }
    
    public void setSelectedTextColor(Color color) {
	selectedTextColor = color;
    }
    
    public Color selectedTextColor() {
	return selectedTextColor;
    }
    
    public void setTextColor(Color color) {
	textColor = color;
    }
    
    public Color textColor() {
	return textColor;
    }
    
    public void setDisabledColor(Color color) {
	disabledColor = color;
    }
    
    public Color disabledColor() {
	return disabledColor;
    }
    
    public void setSelected(boolean bool) {
	if (!isEnabled())
	    bool = false;
	selected = bool;
    }
    
    public boolean isSelected() {
	return selected;
    }
    
    public void setCommand(String string) {
	command = string;
    }
    
    public String command() {
	return command;
    }
    
    public void setTarget(Target target) {
	this.target = target;
    }
    
    public Target target() {
	return target;
    }
    
    public void processEvent(Event event) {
	sendCommand();
    }
    
    public void sendCommand() {
	if (target != null)
	    Application.application().performCommandLater(target, command,
							  this);
    }
    
    private boolean canUseTabFormatter() {
	return false;
    }
    
    public void setTitle(String string) {
	String string_4_ = string;
	title = string;
	if (commandKey() != 0) {
	    if (JDK11AirLock.setMenuShortcut(this, commandKey()))
		string_4_ = string;
	    else if (canUseTabFormatter())
		string_4_ = string + "\tCtrl+" + commandKey();
	    else {
		StringBuffer stringbuffer = new StringBuffer();
		FontMetrics fontmetrics = font().fontMetrics();
		int i;
		if (supermenu() != null)
		    i = supermenu().minItemWidth();
		else
		    i = minWidth();
		int i_5_ = fontmetrics.stringWidth(string);
		stringbuffer.append(string);
		int i_6_ = (i - i_5_) / fontmetrics.stringWidth(" ");
		for (int i_7_ = 0; i_7_ < i_6_; i_7_++)
		    stringbuffer.append(' ');
		string_4_ = stringbuffer.toString() + "  Ctrl+" + commandKey();
	    }
	}
	foundationMenuItem.setLabel(string_4_);
    }
    
    public String title() {
	return title;
    }
    
    public int minHeight() {
	int i = 0;
	if (font() != null)
	    i = font().fontMetrics().stringHeight();
	return i;
    }
    
    public int minWidth() {
	int i = 0;
	if (image != null)
	    i = image.width();
	if (selectedImage != null && selectedImage.width() > i)
	    i = selectedImage.width();
	if (font() != null)
	    i += font().fontMetrics().stringWidth(title);
	if (i > 0)
	    i += 11;
	i += commandKeyWidth();
	return i;
    }
    
    int commandKeyWidth() {
	int i = 0;
	if (font() != null && commandKey() != 0) {
	    i = font().fontMetrics().stringWidth("Ctrl+W");
	    i += 10;
	}
	return i;
    }
    
    public void setEnabled(boolean bool) {
	enabled = bool;
	if (hasSubmenu())
	    submenu.awtMenu().enable(bool);
	else
	    foundationMenuItem.enable(bool);
	requestDraw();
    }
    
    public boolean isEnabled() {
	return enabled;
    }
    
    public void setFont(Font font) {
	this.font = font;
	java.awt.Font font_8_ = AWTCompatibility.awtFontForFont(this.font);
	foundationMenuItem.setFont(font_8_);
    }
    
    public Font font() {
	return font;
    }
    
    public void requestDraw() {
	if (supermenu() != null && supermenu().menuView != null)
	    supermenu().menuView.drawItemAt(supermenu().indexOfItem(this));
    }
    
    protected void drawSeparator(Graphics graphics, Rect rect) {
	int i = rect.y + rect.height / 2;
	int i_9_ = rect.x + rect.width;
	graphics.setColor(Color.gray153);
	graphics.drawLine(rect.x, i - 1, i_9_, i - 1);
	graphics.setColor(Color.gray231);
	graphics.drawLine(rect.x, i, i_9_, i);
    }
    
    protected void drawBackground(Graphics graphics, Rect rect) {
	if (isSelected()) {
	    graphics.setColor(selectedColor);
	    graphics.fillRect(rect);
	}
    }
    
    protected void drawStringInRect(Graphics graphics, String string,
				    Font font, Rect rect, int i) {
	int i_10_ = 0;
	if (isEnabled() && !isSelected())
	    graphics.setColor(textColor);
	else if (isEnabled() && isSelected())
	    graphics.setColor(selectedTextColor);
	else
	    graphics.setColor(disabledColor);
	graphics.setFont(font);
	graphics.drawStringInRect(string, rect, i);
	if (commandKey() != 0) {
	    Font font_11_ = font();
	    if (font_11_ != null) {
		i_10_ = font_11_.fontMetrics().stringWidth("Ctrl+W");
		i_10_ += 10;
	    }
	    Rect rect_12_ = new Rect(rect.x + rect.width - i_10_, rect.y,
				     i_10_, rect.height);
	    String string_13_ = "Ctrl+" + commandKey();
	    graphics.drawStringInRect(string_13_, rect_12_, 0);
	}
    }
    
    public void drawInRect(Graphics graphics, Rect rect, boolean bool) {
	if (isSeparator())
	    drawSeparator(graphics, rect);
	else {
	    drawBackground(graphics, rect);
	    Image image;
	    if (isSelected())
		image = selectedImage;
	    else
		image = this.image;
	    int i_14_;
	    int i = i_14_ = 0;
	    if (this.image != null) {
		i = this.image.width();
		i_14_ = this.image.height();
	    }
	    if (selectedImage != null) {
		if (selectedImage.width() > i)
		    i = selectedImage.width();
		if (selectedImage.height() > i_14_)
		    i_14_ = selectedImage.height();
	    }
	    if (image != null)
		image.drawAt(graphics, rect.x,
			     rect.y + (rect.height - i_14_) / 2);
	    if (title != null && title.length() > 0) {
		Rect rect_15_ = Rect.newRect(rect.x + 2 + i, rect.y,
					     rect.width - 2 - i, rect.height);
		drawStringInRect(graphics, title, font(), rect_15_, 0);
		Rect.returnRect(rect_15_);
	    }
	    if (bool && hasSubmenu()) {
		Bitmap bitmap
		    = (Bitmap.bitmapNamed
		       ("netscape/application/ScrollRightArrow.gif"));
		i = bitmap.width();
		i_14_ = bitmap.height();
		bitmap.drawAt(graphics, rect.x + rect.width - i,
			      rect.y + (rect.height - i_14_) / 2);
	    }
	}
    }
}
