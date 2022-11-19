/* KeyboardArrow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class KeyboardArrow extends InternalWindow
{
    Image image;
    View view;
    
    public KeyboardArrow() {
	super(0, 0, 0, 0, 0);
	this.setTransparent(true);
	this.setLayer(510);
	this.setCanBecomeMain(false);
    }
    
    public void setImage(Image image) {
	this.image = image;
	if (this.image != null)
	    this.sizeTo(this.image.width(), this.image.height());
	else
	    this.sizeTo(0, 0);
    }
    
    public void drawView(Graphics graphics) {
	if (image != null)
	    image.drawAt(graphics, 0, 0);
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	return false;
    }
    
    void setView(View view) {
	this.view = view;
    }
    
    View view() {
	return view;
    }
    
    public boolean canBecomeSelectedView() {
	return false;
    }
}
