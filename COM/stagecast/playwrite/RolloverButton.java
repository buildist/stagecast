/* RolloverButton - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Target;

public class RolloverButton extends PlaywriteButton
{
    private boolean _active = false;
    
    public RolloverButton(Image normalImage, Image activeImage, String command,
			  Target target) {
	super(0, 0, normalImage.width(), normalImage.height());
	this.setType(0);
	this.setImage(normalImage);
	this.setAltImage(activeImage);
	this.setTransparent(true);
	setActive(false);
	this.setCommand(command);
	this.setTarget(target);
    }
    
    public boolean isActive() {
	return _active;
    }
    
    public void setActive(boolean b) {
	_active = b;
    }
    
    public void mouseEntered(MouseEvent event) {
	if (!isActive()) {
	    setActive(true);
	    Image temp = this.image();
	    this.setImage(this.altImage());
	    this.setAltImage(temp);
	    this.setDirty(true);
	}
	super.mouseEntered(event);
    }
    
    public void mouseExited(MouseEvent event) {
	if (isActive()) {
	    setActive(false);
	    Image temp = this.image();
	    this.setImage(this.altImage());
	    this.setAltImage(temp);
	    this.setDirty(true);
	}
	super.mouseExited(event);
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (isActive()) {
	    setActive(false);
	    Image temp = this.image();
	    this.setImage(this.altImage());
	    this.setAltImage(temp);
	    this.setDirty(true);
	}
	return super.mouseDown(event);
    }
}
