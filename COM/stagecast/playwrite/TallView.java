/* TallView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

public class TallView extends PlaywriteView
{
    public TallView() {
	/* empty */
    }
    
    public TallView(int x, int y, int w, int h) {
	super(x, y, w, h);
    }
    
    public TallView(Rect bounds) {
	super(bounds);
    }
    
    public void addSubview(View subview) {
	super.addSubview(subview);
	computeMinSize();
	growIfNecessary();
    }
    
    protected void removeSubview(View view) {
	super.removeSubview(view);
	computeMinSize();
    }
    
    public void subviewDidResize(View subview) {
	computeMinSize();
	growIfNecessary();
	this.layoutView(0, 0);
    }
    
    private void computeMinSize() {
	int maxW = this.border().widthMargin();
	int sigmaH = this.border().heightMargin();
	int xgap = 0;
	int ygap = 0;
	boolean isPacked = this.layoutManager() instanceof PackLayout;
	PackLayout packLayout = null;
	PackConstraints packConstraints = null;
	if (isPacked)
	    packLayout = (PackLayout) this.layoutManager();
	Vector subviews = this.subviews();
	int n = subviews.size();
	for (int i = 0; i < n; i++) {
	    View view = (View) subviews.elementAt(i);
	    if (!(view instanceof GrayLayer)) {
		if (isPacked) {
		    packConstraints = packLayout.constraintsFor(view);
		    xgap = packConstraints.padX() * 2;
		    ygap = packConstraints.padY() * 2;
		}
		maxW = Math.max(maxW, (view.width() + xgap
				       + this.border().widthMargin()));
		sigmaH += view.height() + ygap;
	    }
	}
	this.setMinSize(maxW, sigmaH);
    }
    
    private void growIfNecessary() {
	Size min = this.minSize();
	int deltaW = min.width - this.width();
	int deltaH = min.height - this.height();
	if (deltaW > 0 || deltaH > 0)
	    this.sizeBy(deltaW < 0 ? 0 : deltaW, deltaH < 0 ? 0 : deltaH);
    }
}
