/* JDKMenuShortcut - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application.jdk11compatibility;
import java.awt.MenuItem;
import java.awt.MenuShortcut;

public class JDKMenuShortcut
    implements COM.stagecast.ifc.netscape.application.MenuShortcut
{
    public void setMenuShortcut(MenuItem menuitem, char c) {
	MenuShortcut menushortcut = new MenuShortcut(c);
	menuitem.setShortcut(menushortcut);
    }
}
