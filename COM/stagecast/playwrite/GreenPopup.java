/* GreenPopup - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.ListItem;
import COM.stagecast.ifc.netscape.application.Popup;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class GreenPopup extends Popup
    implements Enableable, ResourceIDs.ScrapBorderIDs
{
    private static Image _menuImage = null;
    private static ScrapBorder _menuBorder = null;
    private Font _font;
    
    public GreenPopup(boolean enabled) {
	init();
	this.setEnabled(enabled);
    }
    
    public GreenPopup(int x, int y, int width, int height, boolean enabled) {
	super(x, y, width, height);
	init();
	this.setEnabled(enabled);
    }
    
    private void init() {
	this.setBorder(getMenuBorder());
	this.popupList().setBackgroundColor(Util.menuColor);
	this.setPopupImage(getMenuImage());
    }
    
    public void setFont(Font font) {
	_font = font;
    }
    
    public ListItem addItem(String title, String command) {
	ListItem listItem = super.addItem(title, command);
	if (_font != null)
	    listItem.setFont(_font);
	return listItem;
    }
    
    private ScrapBorder getMenuBorder() {
	if (_menuBorder == null)
	    _menuBorder = new ScrapBorder(Resource.getImage("MenuLeft"),
					  Resource.getImage("MenuTop"),
					  Resource.getImage("MenuRight"),
					  Resource.getImage("MenuBottom"));
	return _menuBorder;
    }
    
    private Image getMenuImage() {
	if (_menuImage == null)
	    _menuImage = Resource.getImage("MenuArrow");
	return _menuImage;
    }
}
