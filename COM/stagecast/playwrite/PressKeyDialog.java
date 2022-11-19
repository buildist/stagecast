/* PressKeyDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class PressKeyDialog extends PlaywriteDialog implements ResourceIDs.DialogIDs
{
    private Point _location;
    private KeyEvent _keyEvent;
    
    PressKeyDialog(Point location) {
	super("dialog pak", "dialog pak any", true);
	_location = location;
	alignDialog();
	TextField textField = this.getTextField();
	textField.setJustification(1);
	textField.setEditable(false);
    }
    
    protected boolean handleKey(KeyEvent event) {
	return false;
    }
    
    public void setDefaultButton(int n) {
	/* empty */
    }
    
    public void keyDown(KeyEvent event) {
	if (!event.isControlKeyDown() && !event.isMetaKeyDown()) {
	    this.getTextField().setStringValue(KeyTest.keyName(event));
	    _keyEvent = null;
	    _keyEvent = (KeyEvent) event.clone();
	}
    }
    
    public void didBecomeMain() {
	this.setFocusedView();
    }
    
    public KeyEvent getKeyEvent() {
	KeyEvent temp = null;
	if (_keyEvent != null) {
	    temp = _keyEvent;
	    _keyEvent = null;
	}
	return temp;
    }
    
    public String getAnswerModally() {
	String result = super.getAnswerModally();
	if (_keyEvent == null && result != "dialog pak any")
	    return "command c";
	return result;
    }
    
    public void alignDialog() {
	if (_location != null)
	    this.moveTo(_location.x, _location.y);
	Size size = PlaywriteRoot.getRootWindowSize();
	int x = bounds.x;
	int y = bounds.y;
	if (bounds.maxX() > size.width)
	    x = size.width - bounds.width;
	if (bounds.maxY() > size.height)
	    y = size.height - bounds.height;
	if (x != bounds.x || y != bounds.y)
	    this.moveTo(x, y);
    }
}
