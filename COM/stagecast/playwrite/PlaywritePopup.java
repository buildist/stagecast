/* PlaywritePopup - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.ListItem;
import COM.stagecast.ifc.netscape.application.ListView;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

class PlaywritePopup extends PlaywriteView
{
    private ListView _popupList = null;
    private InternalWindow _popupWindow = null;
    private Vector _altItems = new Vector(1);
    private boolean _altItemsAdded = false;
    
    class PopupIcon extends ListItem implements Iconish, Debug.Constants
    {
	Rect imageRect = null;
	Rect drawRect = new Rect();
	
	PopupIcon(IconModel model) {
	    this.setTitle(model.getIconName());
	    this.setImage(model.getIconImage());
	    this.setSelectedImage(this.image());
	    this.setSelectedColor(Util.selectionColor);
	    this.setData(model);
	    imageRect = model.getIconImageRect();
	    if (imageRect == null)
		imageRect = new Rect(0, 0, 32, 32);
	    else
		imageRect = new Rect(imageRect);
	    Util.scaleRectToImageProportion(imageRect, this.image());
	    model.getIconViewManager().addView(this);
	}
	
	int imageWidth() {
	    return this.image() == null ? 0 : imageRect.width;
	}
	
	int imageHeight() {
	    return this.image() == null ? 0 : imageRect.height;
	}
	
	public void hilite() {
	    /* empty */
	}
	
	public void unhilite() {
	    /* empty */
	}
	
	public boolean isHilited() {
	    return this.isSelected();
	}
	
	public void setIconImage(Image image) {
	    this.setImage(image);
	    this.listView().setRowHeight(0);
	}
	
	public void setIconName(String newName) {
	    this.setTitle(newName);
	}
	
	public IconModel getIconModel() {
	    return (IconModel) this.data();
	}
	
	public void discardIcon() {
	    Debug.print("debug.gc", "discarding item named ", this.title());
	    getIconModel().getIconViewManager().removeView(this);
	}
	
	public int minWidth() {
	    return Math.max(imageWidth(), Util.stringSize(Util.nameFont,
							  this.title()).width);
	}
	
	public int minHeight() {
	    return imageHeight() + Util.nameFontHeight + 3;
	}
	
	public void drawInRect(Graphics g, Rect rectIn) {
	    drawRect.setBounds(rectIn);
	    int temp = drawRect.y;
	    this.drawBackground(g, drawRect);
	    drawRect.y = drawRect.y + drawRect.height - Util.nameFontHeight;
	    drawRect.height = Util.nameFontHeight;
	    g.setColor(Util.nameColor);
	    this.drawStringInRect(g, this.title(), Util.nameFont, drawRect, 1);
	    if (this.image() != null) {
		drawRect.height = drawRect.y - temp;
		drawRect.y = temp;
		temp = drawRect.width - imageWidth();
		if (temp > 0)
		    temp /= 2;
		else
		    temp = 0;
		drawRect.x += temp;
		temp = drawRect.height - imageHeight();
		if (temp > 0)
		    temp /= 2;
		else
		    temp = 0;
		drawRect.y += temp;
		drawRect.width = imageWidth();
		drawRect.height = imageHeight();
		this.image().drawScaled(g, drawRect);
	    }
	}
    }
    
    PlaywritePopup(Image image) {
	super(image);
    }
    
    PlaywritePopup(Image image, ListView popupList) {
	this(image);
	_popupList = popupList;
    }
    
    final ListView getPopupList() {
	return _popupList;
    }
    
    final void setPopupList(ListView list) {
	discardListItems();
	_popupList = list;
    }
    
    final ListItem selectedItem() {
	return _popupList.selectedItem();
    }
    
    final void addAltItem(ListItem item) {
	_altItems.addElement(item);
    }
    
    final void clearAltItems() {
	_altItems = new Vector(1);
    }
    
    static InternalWindow displayPopupList(ListView popupList) {
	Border border = BezelBorder.groovedBezel();
	Point pt = PlaywriteRoot.getMainRootView().mousePoint();
	Rect newBounds
	    = new Rect(pt.x,
		       (pt.y
			- popupList.selectedIndex() * popupList.rowHeight()
			- popupList.rowHeight() / 2),
		       popupList.width() + border.widthMargin(),
		       popupList.height() + border.heightMargin());
	Size screenSize = PlaywriteRoot.getRootWindowSize();
	if (newBounds.maxX() > screenSize.width)
	    newBounds.x -= newBounds.maxX() - screenSize.width;
	if (newBounds.maxY() > screenSize.height)
	    newBounds.y -= newBounds.maxY() - screenSize.height;
	if (newBounds.x < 0)
	    newBounds.x = 0;
	if (newBounds.y < 0)
	    newBounds.y = 0;
	InternalWindow window
	    = new InternalWindow(0, newBounds.x, newBounds.y, newBounds.width,
				 newBounds.height);
	window.setBorder(border);
	window.setLayer(300);
	if (popupList.bounds.height
	    > PlaywriteRoot.getMainRootView().height()) {
	    ScrollableArea scroller
		= new ScrollableArea(popupList.bounds, popupList, false, true);
	    scroller.sizeTo(scroller.width(),
			    PlaywriteRoot.getMainRootView().height());
	    scroller.setVerticalScrollAmount(popupList.minItemHeight());
	    window.addSubview(scroller);
	} else
	    window.addSubview(popupList);
	window.show();
	PlaywriteRoot.getMainRootView().setMouseView(popupList);
	return window;
    }
    
    static ListView makePopupList(Target target, String command) {
	ListView listView = new ListView() {
	    public void mouseUp(MouseEvent e) {
		this.sendCommand();
	    }
	};
	listView.setTarget(target);
	listView.setCommand(command);
	listView.setBackgroundColor(Color.white);
	listView.setTransparent(false);
	listView.setRowHeight(34);
	listView.setTracksMouseOutsideBounds(false);
	listView.setAllowsEmptySelection(true);
	listView.setAllowsMultipleSelection(false);
	return listView;
    }
    
    static ListItem makePopupItem(String title, Image image, Object data) {
	ListItem item = new ListItem();
	item.setTitle(title);
	item.setImage(image);
	item.setSelectedImage(image);
	item.setSelectedColor(Util.selectionColor);
	item.setData(data);
	return item;
    }
    
    public boolean mouseDown(MouseEvent event) {
	Debug.print("debug.gc", "mouseDown called on ", this);
	insertAltItems(event);
	_popupWindow = displayPopupList(_popupList);
	_popupList.mouseDown(this.convertEventToView(_popupList, event));
	return false;
    }
    
    public boolean mouseDown(MouseEvent event, View view) {
	Debug.print("debug.gc", "mouseDown called on ", this);
	insertAltItems(event);
	_popupWindow = displayPopupList(_popupList);
	_popupList.mouseDown(view.convertEventToView(_popupList, event));
	return false;
    }
    
    private void insertAltItems(MouseEvent event) {
	if (event.isMetaKeyDown() && event.clickCount() == 1) {
	    _altItemsAdded = true;
	    for (int i = 0; i < _altItems.size(); i++)
		_popupList.addItem((ListItem) _altItems.elementAt(i));
	    _popupList.sizeToMinSize();
	    _popupList.sizeTo(_popupList.minItemWidth(), _popupList.height());
	}
    }
    
    void hide() {
	if (_altItemsAdded) {
	    _altItemsAdded = false;
	    for (int i = 0; i < _altItems.size(); i++)
		_popupList.removeItem((ListItem) _altItems.elementAt(i));
	    _popupList.sizeToMinSize();
	    _popupList.sizeTo(_popupList.minItemWidth(), _popupList.height());
	}
	_popupWindow.hide();
	_popupWindow = null;
    }
    
    private void discardListItems() {
	if (_popupList != null) {
	    int count = _popupList.count();
	    for (int i = count - 1; i > -1; i--) {
		ListItem item = _popupList.itemAt(i);
		_popupList.removeItemAt(i);
		if (item instanceof Iconish)
		    ((Iconish) item).discardIcon();
	    }
	}
    }
    
    public void discard() {
	discardListItems();
	super.discard();
	_popupList = null;
	_popupWindow = null;
    }
}
