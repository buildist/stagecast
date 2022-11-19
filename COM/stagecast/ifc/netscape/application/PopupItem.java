/* PopupItem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class PopupItem extends ListItem
{
    Popup popup;
    
    public void setPopup(Popup popup) {
	this.popup = popup;
    }
    
    public Popup popup() {
	return popup;
    }
    
    public void drawInRect(Graphics graphics, Rect rect) {
	super.drawInRect(graphics, rect);
	if (popup != null) {
	    PopupItem popupitem_0_ = (PopupItem) popup.selectedItem();
	    if (popupitem_0_.equals(this)) {
		Image image = popup.popupImage();
		int i;
		int i_1_;
		if (image != null) {
		    i = image.width();
		    i_1_ = image.height();
		} else
		    i = i_1_ = 0;
		if (selected)
		    graphics.setColor(selectedColor);
		else
		    graphics.setColor(listView.backgroundColor());
		if (!listView.isTransparent())
		    graphics.fillRect(rect.x + rect.width - i - 4, rect.y,
				      i + 4, rect.height);
		if (image != null)
		    image.drawAt(graphics, rect.x + rect.width - i - 2,
				 rect.y + (rect.height - i_1_) / 2);
	    }
	}
    }
}
