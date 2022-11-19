/* Token - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Rect;

public abstract class Token implements Named, FirstClassValue, IconModel
{
    private String _name;
    private Image _iconImage;
    private Rect _iconImageRect;
    private ViewManager _iconViewManager;
    
    public Token(String name, Image iconImage) {
	ASSERT.isNotNull(name);
	ASSERT.isNotNull(iconImage);
	_name = name;
	_iconImage = iconImage;
	_iconImageRect
	    = new Rect(0, 0, _iconImage.width(), _iconImage.height());
    }
    
    public String getName() {
	return _name;
    }
    
    public void setName(String name) {
	ASSERT.isTrue(false);
    }
    
    public PlaywriteView createIconView() {
	PlaywriteView view = new Icon(_iconImage, _iconImageRect, getName());
	view.setModelObject(this);
	return view;
    }
    
    public PlaywriteView createView() {
	return createIconView();
    }
    
    public String toString() {
	return getName();
    }
    
    public Image getIconImage() {
	return _iconImage;
    }
    
    public Rect getIconImageRect() {
	return null;
    }
    
    public void setIconImage(Image image) {
	ASSERT.isTrue(false);
    }
    
    public String getIconName() {
	return getName();
    }
    
    public void setIconName(String newName) {
	ASSERT.isTrue(false);
    }
    
    public boolean hasIconViews() {
	return _iconViewManager != null && _iconViewManager.hasViews();
    }
    
    public ViewManager getIconViewManager() {
	if (_iconViewManager == null)
	    _iconViewManager = new ViewManager(this);
	return _iconViewManager;
    }
}
