/* AppearanceDrawerView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.KeyEvent;

class AppearanceDrawerView extends XYContainerView
{
    AppearanceDrawerView(AppearanceDrawer drawer, int width, int height) {
	super((XYContainer) drawer, width, height);
    }
    
    private AppearanceDrawer getDrawer() {
	return (AppearanceDrawer) this.getModelObject();
    }
    
    public PlaywriteView makeModelView(Contained model) {
	Appearance appearance = (Appearance) model;
	AppearanceDrawerItemView view
	    = new AppearanceDrawerItemView(appearance, getDrawer());
	getDrawer().getAppearanceDrawerIconModel(appearance).getIconViewManager
	    ().addView(view);
	return view;
    }
    
    public void keyDown(KeyEvent event) {
	switch (event.key) {
	case 8:
	case 127:
	    if (getDrawer().allowRemove(getDrawer().getSelectedItem())
		&& getDrawer().getSelectedItem()
		       .isHighlightedForAppearanceDrawerSelection())
		getDrawer().remove(getDrawer().getSelectedItem());
	    break;
	default:
	    super.keyDown(event);
	}
    }
}
