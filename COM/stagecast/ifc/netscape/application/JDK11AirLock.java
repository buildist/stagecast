/* JDK11AirLock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class JDK11AirLock
{
    static boolean lookedForPrintClass = false;
    static Class printClass = null;
    static boolean lookedForMenuShortcut = false;
    static boolean menuShortcutExists = false;
    static boolean lookedForOneOneEvent = false;
    static boolean hasOneOneEvents = false;
    
    static boolean hasOneOneEvents() {
	if (lookedForOneOneEvent)
	    return hasOneOneEvents;
	Class var_class = null;
	try {
	    var_class = Class.forName("java.awt.event.KeyEvent");
	} catch (ClassNotFoundException classnotfoundexception) {
	    /* empty */
	}
	if (var_class != null)
	    hasOneOneEvents = true;
	else
	    hasOneOneEvents = false;
	lookedForOneOneEvent = true;
	return hasOneOneEvents;
    }
    
    static void createFoundationPanelListener
	(FoundationPanel foundationpanel) {
	Object object = null;
	try {
	    Class var_class
		= (Class.forName
		   ("COM.stagecast.ifc.netscape.application.jdk11compatibility.FoundationPanelListenerImp"));
	    FoundationPanelListener foundationpanellistener
		= (FoundationPanelListener) var_class.newInstance();
	    foundationpanellistener.setFoundationPanel(foundationpanel);
	} catch (ClassNotFoundException classnotfoundexception) {
	    /* empty */
	} catch (InstantiationException instantiationexception) {
	    /* empty */
	} catch (IllegalAccessException illegalaccessexception) {
	    /* empty */
	}
    }
    
    static Clipboard clipboard() {
	try {
	    Class var_class = Class.forName("java.awt.datatransfer.Clipboard");
	    Class var_class_0_
		= (Class.forName
		   ("COM.stagecast.ifc.netscape.application.jdk11compatibility.JDKClipboard"));
	    return (Clipboard) var_class_0_.newInstance();
	} catch (ClassNotFoundException classnotfoundexception) {
	    /* empty */
	} catch (InstantiationException instantiationexception) {
	    /* empty */
	} catch (IllegalAccessException illegalaccessexception) {
	    /* empty */
	}
	return null;
    }
    
    static boolean setMenuShortcut(MenuItem menuitem, char c) {
	try {
	    Class var_class = Class.forName("java.awt.MenuShortcut");
	    Class var_class_1_
		= (Class.forName
		   ("COM.stagecast.ifc.netscape.application.jdk11compatibility.JDKMenuShortcut"));
	    MenuShortcut menushortcut
		= (MenuShortcut) var_class_1_.newInstance();
	    if (menushortcut != null) {
		menushortcut.setMenuShortcut(menuitem.foundationMenuItem, c);
		return true;
	    }
	} catch (ClassNotFoundException classnotfoundexception) {
	    /* empty */
	} catch (InstantiationException instantiationexception) {
	    /* empty */
	} catch (IllegalAccessException illegalaccessexception) {
	    /* empty */
	}
	return false;
    }
    
    static boolean menuShortcutExists() {
	if (!lookedForMenuShortcut) {
	    lookedForMenuShortcut = true;
	    try {
		Class var_class = Class.forName("java.awt.MenuShortcut");
		Class var_class_2_
		    = (Class.forName
		       ("COM.stagecast.ifc.netscape.application.jdk11compatibility.JDKMenuShortcut"));
		menuShortcutExists = true;
	    } catch (ClassNotFoundException classnotfoundexception) {
		/* empty */
	    }
	}
	return menuShortcutExists;
    }
    
    static boolean isPrintGraphics(java.awt.Graphics graphics) {
	if (!lookedForPrintClass) {
	    lookedForPrintClass = true;
	    try {
		printClass = Class.forName("java.awt.PrintGraphics");
	    } catch (ClassNotFoundException classnotfoundexception) {
		/* empty */
	    }
	}
	if (printClass != null) {
	    for (Class var_class = graphics.getClass(); var_class != null;
		 var_class = var_class.getSuperclass()) {
		Class[] var_classes = var_class.getInterfaces();
		int i = var_classes.length;
		while (i-- > 0) {
		    if (var_classes[i].equals(printClass))
			return true;
		}
	    }
	}
	return false;
    }
}
