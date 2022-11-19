/* ButtonLabel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.View;

public class ButtonLabel extends PlaywriteView
{
    PlaywriteButton _button;
    View _nameView;
    int _padding;
    
    ButtonLabel(PlaywriteButton button, View nameView, int padding) {
	_button = button;
	_nameView = nameView;
	_padding = padding;
	init();
    }
    
    public void setPadding(int padding) {
	_padding = padding;
    }
    
    private void init() {
	this.addSubview(_button);
	_button.moveTo(0, 0);
	this.addSubview(_nameView);
	this.setBackgroundColor(Color.black);
	layoutHorizontally();
    }
    
    public void layoutHorizontally() {
	_nameView.moveTo(_button.bounds.maxX() + _padding,
			 (_button.bounds.height / 2
			  - _nameView.bounds.height / 2));
	int off = _nameView.y();
	if (off < 0) {
	    _button.moveBy(0, -off);
	    _nameView.moveBy(0, -off);
	}
	this.sizeTo(_nameView.bounds.maxX(),
		    Math.max(_button.bounds.maxY(), _nameView.bounds.maxY()));
    }
    
    public void layoutVertically() {
	_nameView.moveTo(_button.bounds.width / 2 - _nameView.bounds.width / 2,
			 _button.bounds.maxY() + _padding);
	int off = _nameView.x();
	if (off < 0) {
	    _button.moveBy(-off, 0);
	    _nameView.moveBy(-off, 0);
	}
	this.sizeTo(Math.max(_button.bounds.maxX(), _nameView.bounds.maxX()),
		    _nameView.bounds.maxY());
    }
    
    public View viewForMouse(int x, int y) {
	if (this.localBounds().contains(x, y))
	    return this;
	return null;
    }
    
    public void mouseEntered(MouseEvent event) {
	_button.mouseEntered(event);
    }
    
    public void mouseExited(MouseEvent event) {
	_button.mouseExited(event);
    }
    
    public void mouseUp(MouseEvent event) {
	_button.sendCommand();
    }
    
    public void discard() {
	_button = null;
	_nameView = null;
	super.discard();
    }
}
