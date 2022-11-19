/* PWFoundationPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.FoundationPanel;

public class PWFoundationPanel extends FoundationPanel
{
    public PWFoundationPanel() {
	this.setRootView(new PWRootView());
    }
    
    public PWFoundationPanel(int width, int height) {
	this.setRootView(new PWRootView(0, 0, width, height));
	this.resize(width, height);
    }
}
