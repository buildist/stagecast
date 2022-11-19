/* DrawerWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Hashtable;

class DrawerWindow extends PlaywriteWindow
{
    private static Hashtable _stickingWindows = null;
    private Drawer _drawer;
    private String _windowTitle;
    
    DrawerWindow(Drawer drawer, Rect bounds) {
	super(bounds, drawer.getWorld());
	_drawer = drawer;
	this.setDisablable(true);
	if (_stickingWindows == null)
	    _stickingWindows = new Hashtable(3);
	init();
    }
    
    private void init() {
	if (_stickingWindows.get(this.getWorld()) != null)
	    ((DrawerWindow) _stickingWindows.remove(this.getWorld())).close();
	_stickingWindows.put(this.getWorld(), this);
	super.setTitle(null);
    }
    
    public boolean isSticking() {
	return this == _stickingWindows.get(this.getWorld());
    }
    
    public void mouseDragged(MouseEvent event) {
	if (isSticking())
	    _stickingWindows.remove(this.getWorld());
	super.mouseDragged(event);
    }
    
    public void setTitle(String title) {
	super.setTitle(title);
    }
    
    void changeWindowColor(Color color) {
	_drawer.changeWindowColor(color);
	super.changeWindowColor(color);
    }
    
    void disable() {
	_drawer.disable();
    }
    
    void enable() {
	_drawer.enable();
    }
    
    public void destroyWindow() {
	if (isSticking())
	    _stickingWindows.remove(this.getWorld());
	_drawer = null;
	super.destroyWindow();
    }
    
    public void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	_drawer.storeWindowBounds(this.bounds());
    }
}
