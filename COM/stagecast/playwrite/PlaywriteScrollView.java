/* PlaywriteScrollView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.ContainerView;
import COM.stagecast.ifc.netscape.application.ScrollView;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;

class PlaywriteScrollView extends ScrollView
{
    private ScrollableArea _scrollableArea;
    private boolean _allowScrollToOnResize = false;
    private final boolean _redirectMouse;
    
    PlaywriteScrollView(int x, int y, int width, int height,
			ScrollableArea scrollableArea, boolean redirectMouse) {
	super(x, y, width, height);
	_scrollableArea = scrollableArea;
	_redirectMouse = redirectMouse;
    }
    
    void setAllowScrollToOnResize(boolean b) {
	_allowScrollToOnResize = b;
    }
    
    public void subviewDidResize(View subview) {
	if (subview != this.contentView()) {
	    if (this.superview() != null)
		this.superview().subviewDidResize(subview);
	} else {
	    if (_scrollableArea.getAllowSmallContentView() == false) {
		boolean resize = false;
		Size size = new Size(subview.width(), subview.height());
		if (size.width < this.width()) {
		    size.width = this.width();
		    resize = true;
		}
		if (size.height < this.height()) {
		    size.height = this.height();
		    resize = true;
		}
		if (resize)
		    subview.sizeTo(size.width, size.height);
	    }
	    if (_scrollableArea.checkArrows() || _allowScrollToOnResize)
		super.subviewDidResize(subview);
	    else
		this.setDirty(true);
	}
    }
    
    public final void willBecomeSelected() {
	/* empty */
    }
    
    public final void willBecomeUnselected() {
	/* empty */
    }
    
    public View viewForMouse(int x, int y) {
	View view = super.viewForMouse(x, y);
	if (view == this && _redirectMouse) {
	    View cView = this.contentView();
	    if (cView != null) {
		if (cView.width() < 2 || cView.height() < 2)
		    return view;
		x -= cView.bounds.x;
		y -= cView.bounds.y;
		if (x > cView.bounds.width)
		    x = cView.bounds.width - 1;
		if (y > cView.bounds.height)
		    y = cView.bounds.height - 1;
		if (x < 0)
		    x = 0;
		if (y < 0)
		    y = 0;
		return cView.viewForMouse(x, y);
	    }
	}
	return view;
    }
    
    public void setBackgroundColor(Color color) {
	super.setBackgroundColor(color);
	View contentView = this.contentView();
	if (contentView instanceof ContainerView)
	    ((ContainerView) contentView).setBackgroundColor(color);
	else if (contentView instanceof PlaywriteView)
	    ((PlaywriteView) contentView).setBackgroundColor(color);
    }
    
    public void setContentView(View aView) {
	if (aView instanceof ContainerView)
	    ((ContainerView) aView).setBackgroundColor(this.backgroundColor());
	else if (aView instanceof PlaywriteView)
	    ((PlaywriteView) aView).setBackgroundColor(this.backgroundColor());
	super.setContentView(aView);
    }
}
