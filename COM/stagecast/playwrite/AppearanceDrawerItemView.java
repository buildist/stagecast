/* AppearanceDrawerItemView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class AppearanceDrawerItemView extends Icon
    implements ResourceIDs.CommandIDs, ResourceIDs.PicturePainterIDs
{
    private static final String MOUSE_DOWN = "Mouse Down";
    private AppearanceDrawer _drawer;
    
    AppearanceDrawerItemView(Appearance appearance, AppearanceDrawer drawer) {
	super(appearance);
	_drawer = drawer;
	this.setEditable(true);
	this.setSelectsModel(true);
	appearance.getIconViewManager().addView(this);
	if (appearance.isHighlightedForAppearanceDrawerSelection())
	    this.hilite();
    }
    
    public void discardIcon() {
	super.discardIcon();
	_drawer = null;
    }
    
    protected boolean isLegalIconName(String name) {
	Appearance appearance
	    = _drawer.getCharacter().getPrototype().getAppearanceNamed(name);
	boolean isLegal
	    = appearance == null || appearance == this.getModelObject();
	if (isLegal == false) {
	    Target target = new Target() {
		public void performCommand(String command, Object data) {
		    PlaywriteSystem.beep();
		}
	    };
	    Application.application().performCommandLater(target, "", null);
	}
	return isLegal;
    }
    
    public Image createDragImage() {
	boolean wasHilited = this.isHilited();
	if (this.isHilited())
	    this.unhilite();
	Image returnImage = Util.makeBitmapFromView(this);
	if (wasHilited)
	    this.hilite();
	return returnImage;
    }
    
    public boolean mouseDown(MouseEvent event) {
	boolean allowDrag = true;
	this.setDragPoint(event.x, event.y);
	this.setFocusedView();
	if (_drawer.getSelectedItem() == this.getModelObject())
	    allowDrag = _drawer.saveChangesToSelectedItemUnlessAborted();
	else
	    allowDrag
		= _drawer.setSelectedItem((Appearance) this.getModelObject());
	return allowDrag;
    }
    
    public void keyDown(KeyEvent event) {
	switch (event.key) {
	case 8:
	case 127:
	    if (_drawer.allowRemove(_drawer.getSelectedItem())
		&& _drawer.getSelectedItem()
		       .isHighlightedForAppearanceDrawerSelection())
		_drawer.remove(_drawer.getSelectedItem());
	    break;
	default:
	    super.keyDown(event);
	}
    }
    
    public View viewForMouse(int x, int y) {
	View result = super.viewForMouse(x, y);
	if (_drawer.getSelectedItem() != this.getModelObject()
	    && result == this.getNameField())
	    result = this;
	return result;
    }
}
