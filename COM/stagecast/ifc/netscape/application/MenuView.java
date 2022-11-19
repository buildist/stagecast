/* MenuView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class MenuView extends View
{
    Menu menu;
    MenuItem selectedItem;
    public MenuView owner;
    public MenuView child;
    InternalWindow menuWindow;
    int type;
    int itemHeight = 17;
    boolean transparent = false;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    public MenuView() {
	this(0, 0, 0, 0, null, null);
    }
    
    public MenuView(Menu menu) {
	this(0, 0, 0, 0, menu, null);
    }
    
    public MenuView(int i, int i_0_, int i_1_, int i_2_) {
	this(i, i_0_, i_1_, i_2_, null, null);
    }
    
    public MenuView(int i, int i_3_, int i_4_, int i_5_, Menu menu) {
	this(i, i_3_, i_4_, i_5_, menu, null);
    }
    
    public MenuView(int i, int i_6_, int i_7_, int i_8_, Menu menu,
		    MenuView menuview_9_) {
	super(i, i_6_, i_7_, i_8_);
	if (menu != null)
	    this.menu = menu;
	else
	    this.menu = new Menu(true);
	owner = menuview_9_;
	menuWindow = createMenuWindow();
	menuWindow.addSubview(this);
	if (this.menu.isTopLevel()) {
	    type = 0;
	    this.setHorizResizeInstruction(2);
	} else
	    type = 1;
	this.menu.menuView = this;
    }
    
    protected InternalWindow createMenuWindow() {
	InternalWindow internalwindow = new InternalWindow(0, 0, 0, 0);
	internalwindow.setType(0);
	internalwindow.setLayer(511);
	internalwindow.setCanBecomeMain(false);
	internalwindow._contentView.setTransparent(true);
	internalwindow.setScrollsToVisible(true);
	return internalwindow;
    }
    
    public void setType(int i) {
	type = i;
    }
    
    public int type() {
	return type;
    }
    
    public void setMenu(Menu menu) {
	this.menu = menu;
    }
    
    public Menu menu() {
	return menu;
    }
    
    public void setOwner(MenuView menuview_10_) {
	owner = menuview_10_;
    }
    
    public MenuView owner() {
	return owner;
    }
    
    public Color backgroundColor() {
	return menu.backgroundColor();
    }
    
    public Border border() {
	return menu.border();
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
	menuWindow.setTransparent(bool);
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public void setItemHeight(int i) {
	if (i > 0)
	    itemHeight = i;
	else
	    itemHeight = minItemHeight();
    }
    
    public int itemHeight() {
	if (itemHeight > 0)
	    return itemHeight;
	setItemHeight(minItemHeight());
	return itemHeight;
    }
    
    public int minItemHeight() {
	int i = 0;
	int i_11_ = menu.itemCount();
	for (int i_12_ = 0; i_12_ < i_11_; i_12_++) {
	    int i_13_ = menu.itemAt(i_12_).minHeight();
	    if (i_13_ > i)
		i = i_13_;
	}
	return i;
    }
    
    public int minItemWidth() {
	int i = 0;
	for (int i_14_ = 0; i_14_ < menu.itemCount(); i_14_++) {
	    int i_15_ = menu.itemAt(i_14_).minWidth();
	    if (i_15_ > i)
		i = i_15_;
	}
	return i;
    }
    
    public Size minSize() {
	int i = menu.itemCount();
	int i_16_;
	int i_17_;
	if (type == 0) {
	    i_16_ = 0;
	    for (int i_18_ = 0; i_18_ < i; i_18_++) {
		MenuItem menuitem = menu.itemAt(i_18_);
		i_16_ += menuitem.minWidth();
	    }
	    i_17_ = itemHeight();
	} else {
	    i_16_ = minItemWidth();
	    i_17_ = i * itemHeight();
	}
	i_16_ += border().widthMargin();
	i_17_ += border().heightMargin();
	return new Size(i_16_, i_17_);
    }
    
    public MenuItem itemForPoint(int i, int i_19_) {
	int i_20_ = -1;
	for (int i_21_ = 0; i_21_ < menu.itemCount(); i_21_++) {
	    Rect rect = rectForItemAt(i_21_);
	    if (rect.contains(i, i_19_)) {
		i_20_ = i_21_;
		break;
	    }
	}
	MenuItem menuitem;
	if (i_20_ >= 0)
	    menuitem = menu.itemAt(i_20_);
	else
	    menuitem = null;
	return menuitem;
    }
    
    public int selectedIndex() {
	MenuItem menuitem = selectedItem();
	if (menuitem == null)
	    return -1;
	return menu.indexOfItem(menuitem);
    }
    
    public MenuItem selectedItem() {
	return selectedItem;
    }
    
    public void selectItem(MenuItem menuitem) {
	if (menuitem.isEnabled()) {
	    if (selectedItem != menuitem) {
		if (selectedItem != null) {
		    selectedItem.setSelected(false);
		    this.addDirtyRect
			(rectForItemAt(menu.indexOfItem(selectedItem)));
		}
		menuitem.setSelected(true);
		selectedItem = menuitem;
		this.addDirtyRect(rectForItemAt(menu.indexOfItem(menuitem)));
	    }
	}
    }
    
    public void deselectItem() {
	if (selectedItem != null) {
	    selectedItem.setSelected(false);
	    this.addDirtyRect(rectForItemAt(menu.indexOfItem(selectedItem)));
	    selectedItem = null;
	}
    }
    
    public Rect rectForItemAt(int i) {
	if (i < 0 || i >= menu.itemCount())
	    return null;
	int i_22_ = 0;
	int i_23_;
	int i_24_;
	if (type == 0) {
	    i_23_ = 0;
	    for (int i_25_ = 0; i_25_ < i; i_25_++) {
		MenuItem menuitem = menu.itemAt(i_25_);
		i_22_ += menuitem.minWidth();
	    }
	    i_24_ = menu.itemAt(i).minWidth();
	} else {
	    i_23_ = itemHeight() * i;
	    i_24_ = bounds.width - border().widthMargin();
	}
	i_22_ += border().leftMargin();
	i_23_ += border().topMargin();
	Rect rect = Rect.newRect(i_22_, i_23_, i_24_, itemHeight());
	return rect;
    }
    
    public Rect interiorRect() {
	Rect rect = new Rect(border().leftMargin(), border().topMargin(),
			     bounds.width - border().widthMargin(),
			     bounds.height - border().heightMargin());
	return rect;
    }
    
    public void drawItemAt(int i) {
	Rect rect = rectForItemAt(i);
	this.draw(rect);
    }
    
    protected MenuView createMenuView(Menu menu) {
	return new MenuView(0, 0, 0, 0, menu, this);
    }
    
    MenuView mainOwner() {
	MenuView menuview_26_;
	for (menuview_26_ = owner; menuview_26_.owner() != null;
	     menuview_26_ = menuview_26_.owner()) {
	    /* empty */
	}
	return menuview_26_;
    }
    
    boolean performCommandForKeyStroke(KeyStroke keystroke, int i) {
	if (Application.application().activeMenuViews.count() > 0)
	    return false;
	if (this.window() != null
	    && !this.descendsFrom(this.rootView()._mainWindow))
	    return false;
	if (i == 2 || i == 1) {
	    KeyEvent keyevent
		= new KeyEvent(0L, keystroke.key, keystroke.modifiers, true);
	    return menu.handleCommandKeyEvent(keyevent);
	}
	return false;
    }
    
    void mouseWillDown(MouseEvent mouseevent) {
	RootView rootview = Application.application().firstRootView();
	if (rootview != this.rootView())
	    hideAll();
	else {
	    MouseEvent mouseevent_27_
		= this.rootView().convertEventToView(this, mouseevent);
	    if (!this.containsPoint(mouseevent_27_.x, mouseevent_27_.y)) {
		if (selectedItem() != null) {
		    for (MenuView menuview_28_ = child; menuview_28_ != null;
			 menuview_28_ = menuview_28_.child) {
			Rect rect = new Rect(menuview_28_.bounds);
			menuview_28_.superview().convertRectToView(null, rect,
								   rect);
			if (Rect.contains(rect.x, rect.y, rect.width,
					  rect.height, mouseevent.x,
					  mouseevent.y))
			    return;
		    }
		}
		hideAll();
	    }
	}
    }
    
    void hideAll() {
	Application.application().removeActiveMenuView(this);
	if (child != null)
	    child.hide();
	if (isVisible())
	    hide();
	deselectItem();
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (owner != null)
	    return true;
	MenuItem menuitem = itemForPoint(mouseevent.x, mouseevent.y);
	if (menuitem == null || !menuitem.isEnabled()) {
	    if (child != null)
		child.hide();
	    deselectItem();
	    return true;
	}
	if (selectedItem != null && menuitem == selectedItem) {
	    if (isVisible())
		return true;
	    Application.application().removeActiveMenuView(this);
	    if (child != null) {
		child.hide();
		child = null;
	    }
	    deselectItem();
	    return true;
	}
	selectItem(menuitem);
	if (menuitem.isEnabled() && menuitem.hasSubmenu()) {
	    MenuView menuview_29_ = createMenuView(menuitem.submenu());
	    menuview_29_.show(this.rootView(), mouseevent);
	    child = menuview_29_;
	}
	return true;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	MenuItem menuitem = itemForPoint(mouseevent.x, mouseevent.y);
	if (!Rect.contains(0, 0, this.width(), this.height(), mouseevent.x,
			   mouseevent.y)) {
	    menuitem = null;
	    if (owner != null && child == null
		|| owner == null && child == null && isVisible())
		deselectItem();
	}
	if (menuitem == null || menuitem.isEnabled()) {
	    for (MenuView menuview_30_ = child;
		 menuview_30_ != null && menuview_30_.isVisible();
		 menuview_30_ = menuview_30_.child) {
		Rect rect = new Rect(menuview_30_.bounds);
		MouseEvent mouseevent_31_
		    = this.convertEventToView(menuview_30_, mouseevent);
		if (Rect.contains(rect.x, rect.y, rect.width, rect.height,
				  mouseevent_31_.x, mouseevent_31_.y)) {
		    menuitem = menuview_30_.itemForPoint(mouseevent_31_.x,
							 mouseevent_31_.y);
		    if (menuitem != null) {
			menuview_30_.mouseDragged(mouseevent_31_);
			menuview_30_.autoscroll(mouseevent_31_);
			return;
		    }
		} else if (menuview_30_.child == null) {
		    if (menuview_30_.selectedItem() == null)
			menuview_30_.autoscroll(mouseevent_31_);
		    menuview_30_.deselectItem();
		}
	    }
	    if (menuitem != null && menuitem != selectedItem()) {
		if (selectedItem != null && selectedItem.hasSubmenu()
		    && child != null && child.isVisible()) {
		    child.hide();
		    child = null;
		}
		selectItem(menuitem);
		if (menuitem.hasSubmenu()) {
		    MenuView menuview_32_ = createMenuView(menuitem.submenu());
		    menuview_32_.show(this.rootView(), mouseevent);
		    child = menuview_32_;
		}
	    }
	}
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	Object object = null;
	MenuItem menuitem = itemForPoint(mouseevent.x, mouseevent.y);
	if (owner == null && selectedItem == menuitem) {
	    if (isVisible() && selectedItem != null) {
		Application.application().removeActiveMenuView(this);
		hide();
		menu.performCommand("", menuitem);
	    } else
		return;
	}
	if (owner == null && menuitem == null) {
	    MenuView menuview_33_;
	    for (menuview_33_ = child; menuview_33_.child != null;
		 menuview_33_ = menuview_33_.child) {
		/* empty */
	    }
	    menuview_33_.mouseUp(this.convertEventToView(menuview_33_,
							 mouseevent));
	}
	if (owner != null && selectedItem != null) {
	    menuitem = selectedItem;
	    Application.application().removeActiveMenuView(mainOwner());
	    hide();
	    MenuView menuview_34_;
	    for (menuview_34_ = owner; menuview_34_.owner() != null;
		 menuview_34_ = menuview_34_.owner())
		menuview_34_.hide();
	    menuview_34_.hide();
	    menu.performCommand("", menuitem);
	}
    }
    
    public void mouseEntered(MouseEvent mouseevent) {
	if (owner == null && selectedItem != null || isVisible())
	    mouseDragged(mouseevent);
    }
    
    public void mouseMoved(MouseEvent mouseevent) {
	if (owner == null && selectedItem != null || isVisible())
	    mouseDragged(mouseevent);
    }
    
    public void mouseExited(MouseEvent mouseevent) {
	if (owner == null && selectedItem != null || isVisible())
	    mouseDragged(mouseevent);
    }
    
    public void drawView(Graphics graphics) {
	border().drawInRect(graphics, 0, 0, this.width(), this.height());
	int i = menu.itemCount();
	int i_35_ = 0;
	for (int i_36_ = 0; i_36_ < i; i_36_++) {
	    MenuItem menuitem = menu.itemAt(i_36_);
	    Rect rect = rectForItemAt(i_36_);
	    i_35_ += rect.width;
	    if (graphics.clipRect().intersects(rect)) {
		graphics.pushState();
		graphics.setClipRect(rect);
		if (!isTransparent()) {
		    graphics.setColor(backgroundColor());
		    graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
		boolean bool;
		if (type == 0)
		    bool = false;
		else
		    bool = true;
		menuitem.drawInRect(graphics, rect, bool);
		graphics.popState();
		Rect.returnRect(rect);
	    }
	}
	if (i_35_ < interiorRect().width && type == 0 && !isTransparent()) {
	    graphics.setColor(backgroundColor());
	    graphics.fillRect(interiorRect().x + i_35_, interiorRect().y,
			      interiorRect().width - i_35_,
			      interiorRect().height);
	}
    }
    
    public void show(RootView rootview, MouseEvent mouseevent) {
	if (this.menu.itemCount() != 0) {
	    int i = minItemWidth() + border().widthMargin() + 20;
	    int i_37_ = (itemHeight() * this.menu.itemCount()
			 + border().heightMargin());
	    this.sizeTo(i, i_37_);
	    Menu menu;
	    if (this.menu.superitem() != null)
		menu = this.menu.superitem().supermenu();
	    else
		menu = this.menu;
	    MenuView menuview_38_;
	    if (owner != null)
		menuview_38_ = owner;
	    else
		menuview_38_ = this;
	    Rect rect = Rect.newRect(0, 0, this.width(), this.height());
	    menuview_38_.convertRectToView(null, rect, rect);
	    if (rootview.windowClipView() != null && menuWindow.layer() != 511)
		rootview.convertRectToView(rootview.windowClipView(), rect,
					   rect);
	    Rect rect_39_
		= menuview_38_.rectForItemAt(menuview_38_.selectedIndex());
	    if (rect_39_ == null)
		rect_39_ = new Rect(0, 0, 0, 0);
	    int i_40_;
	    int i_41_;
	    if (menu.isTopLevel() && menuview_38_.type == 0) {
		i_40_
		    = rect.x + rect_39_.x - menuview_38_.border().leftMargin();
		i_41_ = rect.y + menuview_38_.height();
	    } else if (this.menu.isTopLevel() && type == 1) {
		i_40_ = mouseevent.x;
		i_41_ = mouseevent.y;
	    } else {
		i_40_ = (rect.x + rect_39_.width
			 + menuview_38_.border().widthMargin() - 3);
		i_41_ = (rect.y + rect_39_.maxY() - rect_39_.height
			 - menuview_38_.border().topMargin());
	    }
	    int i_42_ = this.width();
	    menuWindow.setBounds(i_40_, i_41_, this.width(), this.height());
	    this.sizeTo(i_42_, this.height());
	    Rect.returnRect(rect);
	    Rect.returnRect(rect_39_);
	    if (owner != null)
		menuview_38_ = mainOwner();
	    else
		menuview_38_ = this;
	    Application.application().addActiveMenuView(menuview_38_);
	    menuWindow.setRootView(rootview);
	    menuWindow.show();
	}
    }
    
    public void hide() {
	if (menuWindow.isVisible())
	    menuWindow.hide();
	if (selectedItem != null && selectedItem.hasSubmenu()) {
	    child.hide();
	    child = null;
	}
	deselectItem();
    }
    
    public boolean isVisible() {
	return menuWindow.isVisible();
    }
    
    public boolean wantsAutoscrollEvents() {
	return true;
    }
    
    void autoscroll(MouseEvent mouseevent) {
	Rect rect = Rect.newRect();
	this.computeVisibleRect(rect);
	if (!rect.contains(mouseevent.x, mouseevent.y)) {
	    if (mouseevent.y < rect.y) {
		Rect rect_43_ = Rect.newRect(rect.x, mouseevent.y, rect.width,
					     itemHeight());
		this.scrollRectToVisible(rect_43_);
		Rect.returnRect(rect_43_);
	    } else if (mouseevent.y > rect.maxY()) {
		Rect rect_44_
		    = Rect.newRect(rect.x, mouseevent.y - itemHeight(),
				   rect.width, itemHeight());
		this.scrollRectToVisible(rect_44_);
		Rect.returnRect(rect_44_);
	    }
	}
	Rect.returnRect(rect);
    }
}
