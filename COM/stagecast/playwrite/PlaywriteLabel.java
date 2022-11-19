/* PlaywriteLabel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.TextField;

class PlaywriteLabel extends TextField implements Debug.Constants
{
    private static final boolean mouseDefault = true;
    private boolean mouseTransparency = true;
    private boolean _underlined = false;
    
    PlaywriteLabel(String string, Font font, Color color) {
	this(string, font, color, false);
	this.setJustification(1);
    }
    
    PlaywriteLabel(String string, Font font, Color color, boolean dropShadow) {
	this.setTransparent(true);
	this.setFont(font);
	this.setTextColor(color);
	setTitle(string);
	this.setEditable(false);
	this.setDrawsDropShadow(dropShadow);
	this.setJustification(1);
    }
    
    public String getName() {
	return title();
    }
    
    public void setName(String n) {
	setTitle(n);
    }
    
    public boolean getMouseTransparency() {
	return mouseTransparency;
    }
    
    public void setMouseTransparency(boolean sendToAncestralContainer) {
	mouseTransparency = sendToAncestralContainer;
    }
    
    public void setTitle(String string) {
	super.setStringValue(string);
	Size size = Util.stringSize(this.font(), string);
	this.sizeTo(size.width, size.height);
    }
    
    public String title() {
	return this.stringValue();
    }
    
    public void setUnderlined(boolean underlined) {
	_underlined = underlined;
	this.setDirty(true);
    }
    
    public void drawViewStringAt(Graphics g, String aString, int x, int y) {
	super.drawViewStringAt(g, aString, x, y);
	if (_underlined) {
	    int baseline = this.baseline() + 2;
	    g.drawLine(0, baseline, this.width(), baseline);
	}
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (getMouseTransparency())
	    return (this.superview().mouseDown
		    (this.convertEventToView(this.superview(), event)));
	super.mouseDown(event);
	return true;
    }
    
    public void mouseUp(MouseEvent event) {
	if (getMouseTransparency())
	    this.superview().mouseUp(this.convertEventToView(this.superview(),
							     event));
	else
	    super.mouseUp(event);
    }
    
    public void mouseDragged(MouseEvent event) {
	if (getMouseTransparency())
	    this.superview().mouseDragged
		(this.convertEventToView(this.superview(), event));
	else
	    super.mouseDragged(event);
    }
    
    public void mouseEntered(MouseEvent event) {
	if (getMouseTransparency())
	    this.superview().mouseEntered
		(this.convertEventToView(this.superview(), event));
	else
	    super.mouseEntered(event);
    }
    
    public void mouseExited(MouseEvent event) {
	if (getMouseTransparency())
	    this.superview()
		.mouseExited(this.convertEventToView(this.superview(), event));
	else
	    super.mouseExited(event);
    }
    
    public String toString() {
	String title = title();
	return (title == null ? "Un-named label"
		: "<PWLabel containing: " + title() + ">");
    }
    
    protected void finalize() throws Throwable {
	Debug.print("debug.gc", "Reclaiming PlaywriteLabel ", this);
	super.finalize();
    }
    
    String getInfo() {
	return "Label '" + title() + "'";
    }
}
