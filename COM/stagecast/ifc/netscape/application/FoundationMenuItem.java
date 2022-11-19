/* FoundationMenuItem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class FoundationMenuItem extends java.awt.MenuItem
{
    MenuItem menuItem;
    
    public FoundationMenuItem(String string) {
	this(string, (MenuItem) null);
    }
    
    public FoundationMenuItem(String string, MenuItem menuitem) {
	super(string);
	setMenuItem(menuitem);
    }
    
    public boolean postEvent(java.awt.Event event) {
	MenuItem menuitem;
	Menu menu;
	for (menu = menuItem.supermenu(); !menu.isTopLevel();
	     menu = menuitem.supermenu())
	    menuitem = menu.superitem();
	Application application = menu.application();
	if (!application.isModalViewShowing()) {
	    Event event_0_ = new Event();
	    event_0_.setProcessor(menuItem);
	    application.eventLoop().addEvent(event_0_);
	}
	return true;
    }
    
    public void setMenuItem(MenuItem menuitem) {
	menuItem = menuitem;
    }
    
    public MenuItem menuItem() {
	return menuItem;
    }
}
