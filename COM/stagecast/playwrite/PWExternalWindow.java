/* PWExternalWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.ExternalWindow;
import COM.stagecast.ifc.netscape.application.FoundationPanel;

public class PWExternalWindow extends ExternalWindow
{
    public PWExternalWindow() {
	/* empty */
    }
    
    public PWExternalWindow(int windowType) {
	super(windowType);
    }
    
    protected FoundationPanel createPanel() {
	return new PWFoundationPanel();
    }
}
