/* WindowContentView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class WindowContentView extends View
{
    Color _color;
    boolean transparent = false;
    
    public WindowContentView() {
	this(0, 0, 0, 0);
    }
    
    public WindowContentView(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public WindowContentView(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	_color = Color.lightGray;
    }
    
    public void setColor(Color color) {
	setBackgroundColor(color);
    }
    
    public void setBackgroundColor(Color color) {
	_color = color;
    }
    
    public Color backgroundColor() {
	return _color;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public void drawView(Graphics graphics) {
	InternalWindow internalwindow = this.window();
	if (_color != null && !isTransparent()
	    && !internalwindow.isTransparent()) {
	    graphics.setColor(_color);
	    int i = this.subviewCount();
	    graphics.fillRect(0, 0, this.width(), this.height());
	}
    }
}
