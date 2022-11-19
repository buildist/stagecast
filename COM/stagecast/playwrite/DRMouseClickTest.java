/* DRMouseClickTest - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class DRMouseClickTest extends RuleTest
    implements Externalizable, ResourceIDs.SummaryIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108752921906L;
    
    public DRMouseClickTest() {
	/* empty */
    }
    
    public boolean evaluate(CharacterInstance self) {
	if (self == null)
	    return false;
	Appearance appearance = self.getCurrentAppearance();
	if (appearance == null)
	    return false;
	int selfX = self.getH();
	int selfY = self.getV();
	int top = appearance.top(selfY);
	int bottom = appearance.bottom(selfY);
	int left = appearance.left(selfX);
	int right = appearance.right(selfX);
	Vector events = self.getWorld().getActiveEvents();
	synchronized (events) {
	    int n = events.size();
	    for (int i = 0; i < n; i++) {
		PlaywriteEvent event = (PlaywriteEvent) events.elementAt(i);
		if (event.isMouseClick()) {
		    int h = event.getH();
		    int v = event.getV();
		    if (bottom <= v && v <= top && left <= h && h <= right)
			return true;
		}
	    }
	    return false;
	}
    }
    
    public PlaywriteView createView() {
	COM.stagecast.ifc.netscape.application.Bitmap mouseImage
	    = Resource.getImage("mouse test image");
	View[] subviews = { new PlaywriteView(mouseImage),
			    this.getRule().getSelf().createIconView() };
	PlaywriteView view
	    = new LineView(this, 8, "mouse test xfmt", null, subviews);
	return view;
    }
    
    public void summarize(Summary s) {
	s.writeText(Resource.getText("SUM mct"));
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	return new DRMouseClickTest();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	/* empty */
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	/* empty */
    }
}
