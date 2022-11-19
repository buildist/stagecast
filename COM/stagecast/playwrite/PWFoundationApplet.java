/* PWFoundationApplet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.FoundationApplet;
import COM.stagecast.ifc.netscape.application.FoundationPanel;

public class PWFoundationApplet extends FoundationApplet
{
    public void run() {
	try {
	    super.run();
	} catch (Throwable t) {
	    if (t instanceof ThreadDeath)
		throw (ThreadDeath) t;
	    PlaywriteRoot.fatalError(t);
	}
    }
    
    protected FoundationPanel createPanel() {
	return new PWFoundationPanel();
    }
}
