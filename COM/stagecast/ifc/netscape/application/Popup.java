/* Popup - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class Popup extends View implements Target, FormElement
{
    ListView popupList;
    Window popupWindow;
    ContainerView container;
    ListItem selectedItem;
    ListItem wasSelectedItem;
    Target target;
    Image image;
    boolean _showingPopupForKeyboard;
    boolean enabled = true;
    private int altItemStart = -1;
    private boolean showAltItems;
    private Border savedBorder;
    private Image savedImage;
    private Color savedColor;
    public static final String SELECT_NEXT_ITEM = ListView.SELECT_NEXT_ITEM;
    public static final String SELECT_PREVIOUS_ITEM
	= ListView.SELECT_PREVIOUS_ITEM;
    public static final String POPUP = "popup";
    static final String CLOSE_POPUP_AND_CANCEL = "cancel";
    static final String CLOSE_POPUP_AND_COMMIT = "commit";
    
    public Popup() {
	this(0, 0, 0, 0);
    }
    
    public Popup(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public Popup(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	InternalWindow internalwindow
	    = new InternalWindow(i, i_0_, i_1_, i_2_);
	internalwindow.setType(0);
	internalwindow.setLayer(300);
	internalwindow._contentView.setTransparent(true);
	internalwindow.setScrollsToVisible(true);
	InternalWindow internalwindow_3_ = internalwindow;
	container = new ContainerView(0, 0, i_1_, i_2_);
	container.setTransparent(true);
	container.setBorder(BezelBorder.raisedBezel());
	container.setVertResizeInstruction(16);
	container.setHorizResizeInstruction(2);
	ListView listview = new ListView(0, 0, i_1_, i_2_);
	PopupItem popupitem = new PopupItem();
	popupitem.setPopup(this);
	setPopupList(listview);
	setPrototypeItem(popupitem);
	setPopupWindow(internalwindow_3_);
	setPopupImage(Bitmap
			  .bitmapNamed("netscape/application/PopupKnobH.gif"));
	_setupKeyboard();
    }
    
    public void setPrototypeItem(ListItem listitem) {
	popupList.setPrototypeItem(listitem);
	if (listitem instanceof PopupItem)
	    ((PopupItem) listitem).setPopup(this);
    }
    
    public ListItem prototypeItem() {
	return popupList.prototypeItem();
    }
    
    public void removeAllItems() {
	popupList.removeAllItems();
    }
    
    public ListItem addItem(String string, String string_4_) {
	hidePopupIfNeeded();
	ListItem listitem = popupList.addItem();
	listitem.setTitle(string);
	listitem.setCommand(string_4_);
	return listitem;
    }
    
    public void removeItem(String string) {
	if (string != null) {
	    int i = popupList.count();
	    while (i-- > 0) {
		ListItem listitem = itemAt(i);
		if (string.equals(listitem.title())) {
		    hidePopupIfNeeded();
		    popupList.removeItemAt(i);
		    break;
		}
	    }
	}
    }
    
    public void removeItemAt(int i) {
	if (popupList.count() > i) {
	    hidePopupIfNeeded();
	    popupList.removeItemAt(i);
	}
    }
    
    public int selectedIndex() {
	int i = popupList.indexOfItem(selectedItem);
	if (i < 0 && popupList.count() > 0) {
	    i = 0;
	    selectItemAt(0);
	}
	return i;
    }
    
    public ListItem selectedItem() {
	int i = selectedIndex();
	if (i < 0)
	    return null;
	return popupList.itemAt(i);
    }
    
    public void selectItem(ListItem listitem) {
	selectedItem = listitem;
	this.sizeToMinSize();
	this.draw();
    }
    
    public void selectItemAt(int i) {
	selectItem(popupList.itemAt(i));
    }
    
    public int count() {
	return popupList.count();
    }
    
    public ListItem itemAt(int i) {
	return popupList.itemAt(i);
    }
    
    public void setBorder(Border border) {
	container.setBorder(border);
    }
    
    public Border border() {
	return container.border();
    }
    
    public void setPopupList(ListView listview) {
	popupList = listview;
	popupList.setTarget(this);
	popupList.setAllowsMultipleSelection(false);
	popupList.setAllowsEmptySelection(true);
	popupList.setTracksMouseOutsideBounds(false);
	container.addSubview(popupList);
    }
    
    public ListView popupList() {
	return popupList;
    }
    
    public void setPopupWindow(Window window) {
	popupWindow = window;
	if (window instanceof InternalWindow) {
	    InternalWindow internalwindow = (InternalWindow) window;
	    internalwindow.setScrollsToVisible(true);
	    window.addSubview(container);
	}
    }
    
    public Window popupWindow() {
	return popupWindow;
    }
    
    public void setPopupImage(Image image) {
	this.image = image;
    }
    
    public Image popupImage() {
	return image;
    }
    
    public ListItem addAltItem(String string, String string_5_) {
	ListItem listitem = addItem(string, string_5_);
	if (altItemStart == -1)
	    altItemStart = popupList().indexOfItem(listitem);
	return listitem;
    }
    
    protected void layoutPopupWindow() {
	Border border = container.border();
	int i = selectedIndex();
	popupList.setRowHeight(bounds.height - border.heightMargin());
	int i_6_;
	int i_7_;
	if (altItemStart == -1 || showAltItems || i >= altItemStart) {
	    i_6_ = popupList.count() * popupList.rowHeight();
	    i_7_ = popupList.minItemWidth();
	} else {
	    i_6_ = altItemStart * popupList.rowHeight();
	    i_7_ = 0;
	    for (int i_8_ = 0; i_8_ < altItemStart; i_8_++) {
		int i_9_ = popupList.itemAt(i_8_).minWidth();
		if (i_9_ > i_7_)
		    i_7_ = i_9_;
	    }
	}
	popupList.setBounds(border.leftMargin(), border.topMargin(),
			    i_7_ + (image == null ? 0 : image.width()), i_6_);
	Rect rect = Rect.newRect(0, 0, this.width(), this.height());
	this.convertRectToView(null, rect, rect);
	if (this.rootView().windowClipView() != null)
	    this.rootView().convertRectToView(this.rootView().windowClipView(),
					      rect, rect);
	popupWindow.setBounds(rect.x, rect.y - i * popupList.rowHeight(),
			      popupList.width() + border.widthMargin(),
			      popupList.height() + border.heightMargin());
	Rect.returnRect(rect);
    }
    
    protected void showPopupWindow(MouseEvent mouseevent) {
	if (popupWindow instanceof InternalWindow) {
	    InternalWindow internalwindow = (InternalWindow) popupWindow;
	    internalwindow.setRootView(this.rootView());
	    Application.application().beginModalSessionForView(internalwindow);
	} else {
	    ExternalWindow externalwindow = (ExternalWindow) popupWindow;
	    Application.application()
		.beginModalSessionForView(externalwindow.rootView());
	}
	popupWindow.show();
	this.rootView().setMouseView(popupList);
	if (mouseevent != null)
	    popupList.mouseDown(this.convertEventToView(popupList,
							mouseevent));
	else {
	    popupList.selectItem(selectedItem());
	    this.rootView().makeSelectedView(popupList);
	}
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (!isEnabled())
	    return false;
	showAltItems
	    = mouseevent.isMetaKeyDown() && mouseevent.clickCount() == 1;
	layoutPopupWindow();
	showPopupWindow(mouseevent);
	return true;
    }
    
    public void setEnabled(boolean bool) {
	boolean bool_10_ = bool != enabled;
	enabled = bool;
	if (bool_10_) {
	    if (enabled) {
		if (savedBorder != null)
		    setBorder(savedBorder);
		if (savedImage != null)
		    setPopupImage(savedImage);
		if (savedColor != null)
		    popupList.setBackgroundColor(savedColor);
		popupList.setTransparent(false);
		container.setTransparent(false);
	    } else {
		savedBorder = border();
		savedImage = popupImage();
		savedColor = popupList.backgroundColor();
		setBorder(null);
		setPopupImage(null);
		popupList.setBackgroundColor(Color.blue);
		popupList.setTransparent(true);
		container.setTransparent(true);
	    }
	    this.sizeToMinSize();
	    this.setDirty(true);
	}
    }
    
    public boolean isEnabled() {
	return enabled;
    }
    
    public boolean isTransparent() {
	return popupList.isTransparent();
    }
    
    public void drawView(Graphics graphics) {
	Object object = null;
	Border border = container.border();
	if (selectedItem == null && popupList.selectedItem() == null)
	    selectItem(popupList.itemAt(0));
	if (!popupList.isTransparent() && selectedItem != null
	    && selectedItem.isTransparent()) {
	    graphics.setColor(popupList.backgroundColor());
	    graphics.fillRect(0, 0, this.width(), this.height());
	}
	if (selectedItem != null) {
	    Rect rect = Rect.newRect(border.leftMargin(), border.topMargin(),
				     bounds.width - border.widthMargin(),
				     bounds.height - border.heightMargin());
	    graphics.pushState();
	    graphics.setClipRect(rect);
	    selectedItem.drawInRect(graphics, rect);
	    graphics.popState();
	    Rect.returnRect(rect);
	}
	border.drawInRect(graphics, 0, 0, this.width(), this.height());
    }
    
    public void setTarget(Target target) {
	this.target = target;
    }
    
    public Target target() {
	return target;
    }
    
    public void setCommand(String string) {
	popupList.setCommand(string);
    }
    
    public String command() {
	return popupList.command();
    }
    
    public void sendCommand() {
	if (target != null) {
	    String string = null;
	    if (selectedItem != null)
		string = selectedItem.command();
	    if (string == null)
		string = command();
	    target.performCommand(string, this);
	}
    }
    
    public void performCommand(String string, Object object) {
	if (SELECT_NEXT_ITEM.equals(string))
	    selectNextItem(true);
	else if (SELECT_PREVIOUS_ITEM.equals(string))
	    selectNextItem(false);
	else if ("popup".equals(string)) {
	    layoutPopupWindow();
	    wasSelectedItem = selectedItem();
	    _showingPopupForKeyboard = true;
	    showPopupWindow(null);
	    _setupKeyboardToClosePopup(true);
	} else if (showingPopupForKeyboard()) {
	    boolean bool = false;
	    if ("cancel".equals(string)) {
		selectItem(wasSelectedItem);
		bool = true;
	    } else if ("commit".equals(string)) {
		selectItem(popupList.selectedItem());
		if (popupList.selectedItem() != null)
		    sendCommand();
		bool = true;
	    }
	    if (bool) {
		popupList.disableDrawing();
		popupList.deselectItem(popupList.selectedItem());
		popupList.reenableDrawing();
		popupWindow.hide();
		if (popupWindow instanceof InternalWindow) {
		    InternalWindow internalwindow
			= (InternalWindow) popupWindow;
		    Application.application()
			.endModalSessionForView(internalwindow);
		} else {
		    ExternalWindow externalwindow
			= (ExternalWindow) popupWindow;
		    Application.application()
			.endModalSessionForView(externalwindow.rootView());
		}
		_setupKeyboardToClosePopup(false);
		_showingPopupForKeyboard = false;
	    }
	} else {
	    if (popupList.selectedItem() != null)
		selectedItem = popupList.selectedItem();
	    if (selectedItem != null) {
		sendCommand();
		this.sizeToMinSize();
	    }
	    popupList.disableDrawing();
	    popupList.deselectItem(selectedItem);
	    popupList.reenableDrawing();
	    popupWindow.hide();
	    if (popupWindow instanceof InternalWindow) {
		InternalWindow internalwindow = (InternalWindow) popupWindow;
		Application.application()
		    .endModalSessionForView(internalwindow);
	    } else {
		ExternalWindow externalwindow = (ExternalWindow) popupWindow;
		Application.application()
		    .endModalSessionForView(externalwindow.rootView());
	    }
	    _setupKeyboardToClosePopup(false);
	}
	this.draw();
    }
    
    public Size minSize() {
	int i = 0;
	int i_11_ = 0;
	if (container.border() != null) {
	    i = container.border().widthMargin();
	    i_11_ = container.border().heightMargin();
	}
	int i_12_ = 0;
	int i_13_ = 0;
	if (popupList != null) {
	    if (selectedItem == null) {
		i_12_ = popupList.minItemWidth();
		i_13_ = popupList.minItemHeight();
	    } else {
		i_12_ = selectedItem.minWidth();
		i_13_ = selectedItem.minHeight();
	    }
	}
	int i_14_ = 0;
	int i_15_ = 0;
	if (image != null && enabled) {
	    i_14_ = image.width();
	    i_15_ = image.height();
	}
	if (i_15_ < i_13_)
	    i_15_ = 0;
	else
	    i_15_ = i_13_ - i_15_;
	return new Size(i + i_12_ + i_14_, i_11_ + i_13_ + i_15_);
    }
    
    void _setupKeyboard() {
	this.removeAllCommandsForKeys();
	this.setCommandForKey(SELECT_NEXT_ITEM, 1005, 0);
	this.setCommandForKey(SELECT_PREVIOUS_ITEM, 1004, 0);
	this.setCommandForKey("popup", 10, 0);
	this.setCommandForKey("popup", 32, 0);
    }
    
    void selectNextItem(boolean bool) {
	int i = selectedIndex();
	int i_16_ = count();
	if (bool && i < i_16_ - 1)
	    selectItemAt(i + 1);
	else if (!bool && i > 0)
	    selectItemAt(i - 1);
	if (selectedItem() != null)
	    sendCommand();
    }
    
    public boolean canBecomeSelectedView() {
	return true;
    }
    
    void _setupKeyboardToClosePopup(boolean bool) {
	if (bool) {
	    this.setCommandForKey("cancel", 27, 2);
	    this.setCommandForKey("commit", 10, 2);
	} else {
	    this.removeCommandForKey(27);
	    this.setCommandForKey("popup", 10, 0);
	}
    }
    
    protected void ancestorWillRemoveFromViewHierarchy(View view) {
	super.ancestorWillRemoveFromViewHierarchy(view);
	hidePopupIfNeeded();
    }
    
    public void hidePopupIfNeeded() {
	if (showingPopupForKeyboard())
	    performCommand("cancel", this);
    }
    
    public boolean showingPopupForKeyboard() {
	return _showingPopupForKeyboard;
    }
    
    public String formElementText() {
	if (selectedItem() != null)
	    return selectedItem().title();
	return "";
    }
}
