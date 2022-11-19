/* Label - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class Label extends View implements Target
{
    TextField label;
    Target target;
    String command;
    Rect underlineRect = null;
    int key;
    final int UNDERLINE_SIZE = 0;
    final String SEND_COMMAND = "sendCommand";
    
    public Label() {
	this("", null);
    }
    
    public Label(String string, Font font) {
	this.label = new TextField(0, 0, 0, 0);
	this.label.setBorder(null);
	this.label.setStringValue(string);
	this.label.setFont(font);
	this.label.setTransparent(true);
	this.label.setEditable(false);
	this.label.setSelectable(false);
	this.label.setJustification(2);
	this.addSubview(this.label);
	this.sizeToMinSize();
    }
    
    public void setJustification(int i) {
	this.label.setJustification(i);
    }
    
    public int justification() {
	return this.label.justification();
    }
    
    public void setTitle(String string) {
	this.label.setStringValue(string);
	invalidateUnderlineRect();
    }
    
    public String title() {
	return this.label.stringValue();
    }
    
    public void setFont(Font font) {
	this.label.setFont(font);
	invalidateUnderlineRect();
    }
    
    public Font font() {
	return this.label.font();
    }
    
    public Size minSize() {
	Font font = this.label.font();
	FontMetrics fontmetrics = font.fontMetrics();
	int i = fontmetrics.stringWidth(this.label.stringValue());
	int i_0_ = fontmetrics.stringHeight();
	return new Size(i, i_0_);
    }
    
    public void didSizeBy(int i, int i_1_) {
	super.didSizeBy(i, i_1_);
	invalidateUnderlineRect();
	this.label.setBounds(0, 0, this.width(), this.height());
    }
    
    public void setColor(Color color) {
	this.label.setTextColor(color);
    }
    
    public Color color() {
	return this.label.textColor();
    }
    
    public void setTarget(Target target) {
	this.target = target;
    }
    
    public Target target() {
	return target;
    }
    
    public void setCommand(String string) {
	command = string;
    }
    
    public String command() {
	return command;
    }
    
    public void setCommandKey(int i) {
	key = i;
	invalidateUnderlineRect();
	this.removeAllCommandsForKeys();
	this.setCommandForKey("sendCommand", null, i, 0, 1);
	this.setCommandForKey("sendCommand", null, i, 2, 1);
	this.setDirty(true);
    }
    
    public int commandKey() {
	return key;
    }
    
    public boolean isTransparent() {
	return true;
    }
    
    public void performCommand(String string, Object object) {
	if ("sendCommand".equals(string))
	    sendCommand();
    }
    
    void invalidateUnderlineRect() {
	underlineRect = null;
    }
    
    public Rect underlineRect() {
	if (underlineRect == null) {
	    if (key != 0) {
		String string = this.label.stringValue();
		int i = string.indexOf(key);
		if (i == -1) {
		    i = string.toUpperCase().indexOf(key);
		    if (i == -1)
			i = string.toLowerCase().indexOf(key);
		}
		if (i != -1) {
		    Rect rect = this.label.rectForRange(i, i + 1);
		    underlineRect = new Rect();
		    this.label.convertRectToView(this, rect, underlineRect);
		    underlineRect.y
			= underlineRect.y + underlineRect.height - 1;
		    underlineRect.height = 1;
		}
	    }
	    if (underlineRect == null)
		underlineRect = new Rect(0, 0, 0, 0);
	}
	return underlineRect;
    }
    
    public void drawView(Graphics graphics) {
	Rect rect = underlineRect();
	if (rect != null && rect.intersects(graphics.clipRect())) {
	    graphics.setColor(this.label.textColor());
	    graphics.fillRect(underlineRect());
	}
    }
    
    void sendCommand() {
	if (target != null && command != null)
	    target.performCommand(command, this);
    }
}
