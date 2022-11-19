/* ResizeCornerView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;

public class ResizeCornerView extends PlaywriteView
{
    private int mx;
    private int my;
    CharacterView sv;
    private boolean _enabled = true;
    private int _oldRight;
    private int _oldBottom;
    
    public ResizeCornerView(CharacterView sview, int x, int y, int width,
			    int height) {
	super(x, y, width, height);
	sv = sview;
	this.setBackgroundColor(Color.black);
    }
    
    public final void setEnabled(boolean b) {
	_enabled = b;
    }
    
    public final boolean isEnabled() {
	return _enabled && !(sv.getCharacter() instanceof GCAlias);
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (isEnabled()
	    && sv.getCharacter().getWorld().getState() != World.RUNNING) {
	    _oldRight = this.right();
	    _oldBottom = this.bottom();
	    mx = event.x;
	    my = event.y;
	    mouseDragged(event);
	    sv.mouseDown(this.convertEventToView(sv, event));
	    return true;
	}
	return false;
    }
    
    public void mouseDragged(MouseEvent event) {
	Point mouseloc
	    = this.convertToView(this.superview(), event.x, event.y);
	int nx = mouseloc.x - mx;
	int ny = mouseloc.y - my;
	World world = sv.getWorld();
	if (nx + sv.bounds.x > sv.superview().width() - this.width())
	    nx = sv.superview().width() - this.width() - sv.bounds.x;
	if (ny + sv.bounds.y > sv.superview().height() - this.height())
	    ny = sv.superview().height() - this.height() - sv.bounds.y;
	if (nx < 5)
	    nx = 5;
	if (ny < 5)
	    ny = 5;
	if (bounds.x != nx || bounds.y != ny) {
	    this.moveTo(nx, ny);
	    sv.setWidthAndHeightVariables(this.right(), this.bottom());
	    if (world != null)
		world.setModified(true);
	}
    }
    
    public void mouseUp(MouseEvent event) {
	this.moveTo(sv.width() - this.width(), sv.height() - this.height());
	if (sv instanceof SpecialCharacterView)
	    ((SpecialCharacterView) sv).commitWidthAndHeightVariables
		(_oldRight, _oldBottom, this.right(), this.bottom());
	sv.adjustAppearanceShape();
    }
    
    public int cursorForPoint(int x, int y) {
	int cursor = super.cursorForPoint(x, y);
	if (cursor == 3 || !isEnabled()
	    || sv.getCharacter().getWorld().getState() == World.RUNNING)
	    return cursor;
	return 5;
    }
}
