/* Menu - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.MenuBar;

import COM.stagecast.ifc.netscape.util.Vector;

public class Menu
{
    java.awt.Menu awtMenu;
    MenuBar awtMenuBar;
    Application application;
    Vector items;
    MenuItem superitem;
    MenuItem prototypeItem;
    Border border;
    Color backgroundColor;
    boolean transparent = false;
    MenuView menuView = null;
    
    public Menu() {
	this(true);
    }
    
    public Menu(boolean bool) {
	items = new Vector();
	backgroundColor = Color.lightGray;
	setBorder(new MenuBorder(this));
	if (bool)
	    awtMenuBar = new MenuBar();
	else
	    awtMenu = new java.awt.Menu("");
	MenuItem menuitem = new MenuItem();
	setPrototypeItem(menuitem);
    }
    
    boolean isTopLevel() {
	if (superitem != null && superitem.supermenu() != null)
	    return false;
	return true;
    }
    
    void setSuperitem(MenuItem menuitem) {
	superitem = menuitem;
    }
    
    MenuItem superitem() {
	return superitem;
    }
    
    public void setPrototypeItem(MenuItem menuitem) {
	prototypeItem = menuitem;
    }
    
    public MenuItem prototypeItem() {
	return prototypeItem;
    }
    
    public void setBackgroundColor(Color color) {
	backgroundColor = color;
    }
    
    public Color backgroundColor() {
	return backgroundColor;
    }
    
    public void setBorder(Border border) {
	if (border == null)
	    this.border = EmptyBorder.emptyBorder();
	else
	    this.border = border;
    }
    
    public Border border() {
	return border;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    MenuItem createItem(boolean bool) {
	MenuItem menuitem = (MenuItem) prototypeItem().clone();
	if (!bool)
	    menuitem.foundationMenuItem = new FoundationMenuItem("", menuitem);
	else {
	    menuitem.foundationMenuItem
		= new FoundationCheckMenuItem("", menuitem);
	    menuitem.setCheckedImage
		(Bitmap.bitmapNamed("netscape/application/RadioButtonOn.gif"));
	    menuitem.setUncheckedImage
		(Bitmap
		     .bitmapNamed("netscape/application/RadioButtonOff.gif"));
	    menuitem.setImage(menuitem.uncheckedImage());
	    menuitem.setSelectedImage(menuitem.uncheckedImage());
	}
	menuitem.setFont(prototypeItem().font());
	return menuitem;
    }
    
    protected Menu createMenuAsSubmenu() {
	Menu menu_0_ = new Menu(false);
	menu_0_.setPrototypeItem(createItem(false));
	menu_0_.setBackgroundColor(backgroundColor());
	return menu_0_;
    }
    
    public MenuItem addItemWithSubmenu(String string) {
	MenuItem menuitem = createItem(false);
	menuitem.setTitle(string);
	Menu menu_1_ = createMenuAsSubmenu();
	menuitem.setSubmenu(menu_1_);
	addItemAt(menuitem, itemCount());
	return menuitem;
    }
    
    public MenuItem addItem(String string, String string_2_, Target target) {
	return addItem(string, '\0', string_2_, target);
    }
    
    public MenuItem addItem(String string, String string_3_, Target target,
			    boolean bool) {
	return addItem(string, '\0', string_3_, target, bool);
    }
    
    public MenuItem addItem(String string, char c, String string_4_,
			    Target target) {
	return addItem(string, c, string_4_, target, false);
    }
    
    public MenuItem addItem(String string, char c, String string_5_,
			    Target target, boolean bool) {
	return addItemAt(string, c, string_5_, target, bool, itemCount());
    }
    
    public MenuItem addItemAt(String string, char c, String string_6_,
			      Target target, boolean bool, int i) {
	MenuItem menuitem = createItem(bool);
	menuitem.setSubmenu(null);
	menuitem.setSupermenu(this);
	menuitem.setCommandKey(c);
	menuitem.setTitle(string);
	menuitem.setTarget(target);
	menuitem.setCommand(string_6_);
	addItemAt(menuitem, i);
	return menuitem;
    }
    
    public MenuItem addSeparator() {
	Object object = null;
	MenuItem menuitem = createItem(false);
	menuitem.setSeparator(true);
	addItemAt(menuitem, itemCount());
	return menuitem;
    }
    
    public int indexOfItem(MenuItem menuitem) {
	return items.indexOf(menuitem);
    }
    
    public int itemCount() {
	return items.count();
    }
    
    public MenuItem itemAt(int i) {
	return (MenuItem) items.elementAt(i);
    }
    
    public void addItemAt(MenuItem menuitem, int i) {
	menuitem.setSupermenu(this);
	if (menuitem.hasSubmenu()) {
	    java.awt.Menu menu_7_ = menuitem.submenu().awtMenu();
	    menu_7_.setLabel(menuitem.title());
	    menu_7_.setFont(AWTCompatibility.awtFontForFont(menuitem.font()));
	    if (isTopLevel()) {
		if (awtMenuBar != null)
		    awtMenuBar.add(menu_7_);
	    } else if (awtMenu != null)
		awtMenu.add(menu_7_);
	} else if (!isTopLevel() && awtMenu != null) {
	    if (menuitem.isSeparator())
		awtMenu.addSeparator();
	    else
		awtMenu.add(menuitem.foundationMenuItem());
	}
	items.insertElementAt(menuitem, i);
	for (int i_8_ = 0; i_8_ < itemCount(); i_8_++)
	    itemAt(i_8_).setTitle(itemAt(i_8_).title());
    }
    
    public void removeItem(MenuItem menuitem) {
	items.removeElement(menuitem);
	if (isTopLevel())
	    awtMenuBar.remove(menuitem.foundationMenuItem());
	else
	    awtMenu.remove(menuitem.foundationMenuItem());
    }
    
    public void removeItemAt(int i) {
	items.removeElementAt(i);
	if (isTopLevel())
	    awtMenuBar.remove(i);
	else
	    awtMenu.remove(i);
    }
    
    public void replaceItemAt(int i, MenuItem menuitem) {
	items.replaceElementAt(i, menuitem);
    }
    
    public void replaceItem(MenuItem menuitem, MenuItem menuitem_9_) {
	int i = indexOfItem(menuitem);
	if (i != -1)
	    replaceItemAt(i, menuitem_9_);
    }
    
    public void performCommand(String string, Object object) {
	if (object != null) {
	    MenuItem menuitem = (MenuItem) object;
	    menuitem.setState(menuitem.state() ^ true);
	    menuitem.sendCommand();
	}
    }
    
    int minItemWidth() {
	int i = 0;
	for (int i_10_ = 0; i_10_ < itemCount(); i_10_++) {
	    int i_11_ = itemAt(i_10_).minWidth();
	    if (i_11_ > i)
		i = i_11_;
	}
	return i;
    }
    
    public boolean handleCommandKeyEvent(KeyEvent keyevent) {
	boolean bool = false;
	if (!keyevent.isControlKeyDown())
	    return false;
	MenuItem menuitem = itemForKeyEvent(keyevent);
	if (menuitem != null) {
	    menuitem.sendCommand();
	    bool = true;
	}
	return bool;
    }
    
    MenuItem itemForKeyEvent(KeyEvent keyevent) {
	MenuItem menuitem = null;
	if (!keyevent.isControlKeyDown())
	    return null;
	for (int i = 0; i < itemCount() && menuitem == null; i++) {
	    MenuItem menuitem_12_ = itemAt(i);
	    if (menuitem_12_.isEnabled()) {
		if (menuitem_12_.hasSubmenu())
		    menuitem
			= menuitem_12_.submenu().itemForKeyEvent(keyevent);
		else if (menuitem_12_.commandKey() == keyevent.key + 64)
		    menuitem = menuitem_12_;
	    }
	}
	return menuitem;
    }
    
    java.awt.Menu awtMenu() {
	return awtMenu;
    }
    
    MenuBar awtMenuBar() {
	return awtMenuBar;
    }
    
    void setApplication(Application application) {
	this.application = application;
    }
    
    Application application() {
	return application;
    }
}
