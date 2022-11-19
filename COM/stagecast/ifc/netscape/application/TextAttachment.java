/* TextAttachment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public abstract class TextAttachment
{
    private TextView _owner;
    private int _width;
    private int _height;
    private boolean _visible = false;
    
    public TextAttachment() {
	_owner = null;
	_width = _height = 0;
    }
    
    public void setOwner(TextView textview) {
	_owner = textview;
    }
    
    public TextView owner() {
	return _owner;
    }
    
    public void setWidth(int i) {
	_width = i;
    }
    
    public int width() {
	return _width;
    }
    
    public void setHeight(int i) {
	_height = i;
    }
    
    public int height() {
	return _height;
    }
    
    public void drawInRect(Graphics graphics, Rect rect) {
	/* empty */
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	return false;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	/* empty */
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	/* empty */
    }
    
    public void willBecomeVisibleWithBounds(Rect rect) {
	/* empty */
    }
    
    public void boundsDidChange(Rect rect) {
	/* empty */
    }
    
    public void willBecomeInvisible() {
	/* empty */
    }
    
    void _willShowWithBounds(Rect rect) {
	if (_visible)
	    boundsDidChange(rect);
	else {
	    willBecomeVisibleWithBounds(rect);
	    _visible = true;
	}
    }
    
    void _willHide() {
	willBecomeInvisible();
	_visible = false;
    }
}
