/* WorldWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class WorldWindow extends PlaywriteWindow
    implements ResourceIDs.WorldViewIDs
{
    Rect _userBounds;
    private ResizeWindowOutline _resizeWindowOutline;
    private boolean _wasBuffered = false;
    
    WorldWindow(int x, int y, int width, int height, World world) {
	super(x, y, width, height, world);
	_userBounds = new Rect(bounds);
	this.getTitleBar().changeWindowColor(world.getColor());
    }
    
    void changeWindowColor(Color color) {
	this.getWorld().getWorldView().changeWindowColor(color);
	super.changeWindowColor(color);
    }
    
    boolean boundify() {
	return true;
    }
    
    public void setBounds(int x, int y, int width, int height) {
	privateSetBounds(x, y, width, height);
	_userBounds = this.bounds();
    }
    
    private void privateSetBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
    }
    
    public void setTitle(String s) {
	super.setTitle(Resource.getTextAndFormat("WW world name fmt",
						 new Object[] { s }));
    }
    
    public void setSystemBounds(int x, int y, int width, int height) {
	privateSetBounds(x, y, width, height);
    }
    
    public Rect getUserBounds() {
	return _userBounds;
    }
    
    public void sizeToUserBounds() {
	if (!bounds.equals(_userBounds))
	    this.setBounds(_userBounds);
    }
    
    public Size minSize() {
	int width = SidelineView.DEFAULT_WIDTH;
	int height = 44;
	width = width + (this.border() == null ? 0
			 : this.border().widthMargin());
	height = height + (this.border() == null ? 0
			   : this.border().heightMargin());
	return new Size(width, height);
    }
    
    public void show() {
	int x = bounds.x;
	int y = bounds.y;
	if (PlaywriteRoot.app().numberOfWorlds() < 2) {
	    this.moveTo(-bounds.width, -bounds.height);
	    this.addDirtyRect(null);
	}
	super.show();
	if (PlaywriteRoot.app().numberOfWorlds() < 2)
	    this.moveTo(x, y);
    }
    
    public boolean mouseDown(MouseEvent event) {
	boolean result = super.mouseDown(event);
	if (this.isResizing()) {
	    World world = this.getWorld();
	    Rect stageAreaBounds
		= (world.getWorldView().getMultiStageView().convertRectToView
		   (null,
		    world.getWorldView().getMultiStageView().localBounds()));
	    stageAreaBounds.moveBy(ScrollableArea.SCROLL_ARROW_WIDTH,
				   ScrollableArea.SCROLL_ARROW_WIDTH);
	    stageAreaBounds.sizeBy(-ScrollableArea.SCROLL_ARROW_WIDTH * 2 + 1,
				   -ScrollableArea.SCROLL_ARROW_WIDTH * 2 + 1);
	    _resizeWindowOutline
		= new ResizeWindowOutline(stageAreaBounds, this.resizePart(),
					  PlaywriteRoot.getMainRootView());
	    _resizeWindowOutline.mouseDown(event);
	    _wasBuffered = this.isBuffered();
	    this.setBuffered(true);
	}
	return result;
    }
    
    public void mouseDragged(MouseEvent event) {
	int eventX = event.x;
	int eventY = event.y;
	super.mouseDragged(event);
	if (this.isResizing()) {
	    event.x = eventX;
	    event.y = eventY;
	    _resizeWindowOutline.mouseDragged(event);
	}
    }
    
    public void mouseUp(MouseEvent event) {
	if (this.isResizing()) {
	    _resizeWindowOutline.mouseUp(event);
	    _resizeWindowOutline = null;
	    this.setBuffered(_wasBuffered);
	}
	super.mouseUp(event);
    }
}
