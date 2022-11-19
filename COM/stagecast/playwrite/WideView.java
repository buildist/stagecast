/* WideView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

class WideView extends PlaywriteView
{
    WideView() {
	/* empty */
    }
    
    WideView(int x, int y, int w, int h) {
	super(x, y, w, h);
    }
    
    WideView(Rect bounds) {
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
	int sigmaW = this.border().widthMargin();
	int maxH = this.border().heightMargin();
	int xgap = 0;
	int ygap = 0;
	boolean isPacked = this.layoutManager() instanceof PackLayout;
	PackLayout packLayout
	    = (PackLayout) (isPacked ? this.layoutManager() : null);
	Vector subviews = this.subviews();
	int n = subviews.size();
	for (int i = 0; i < n; i++) {
	    View v = (View) subviews.elementAt(i);
	    if (!(v instanceof GrayLayer)) {
		if (isPacked) {
		    PackConstraints contstraints
			= packLayout.constraintsFor(v);
		    xgap = contstraints.padX() * 2;
		    ygap = contstraints.padY() * 2;
		}
		sigmaW += v.width() + xgap;
		maxH = Math.max(maxH, (v.height() + ygap
				       + this.border().heightMargin()));
	    }
	}
	this.setMinSize(sigmaW, maxH);
    }
    
    private void growIfNecessary() {
	Size min = this.minSize();
	int deltaW = min.width - this.width();
	int deltaH = min.height - this.height();
	if (deltaW > 0 || deltaH > 0)
	    this.sizeBy(deltaW < 0 ? 0 : deltaW, deltaH < 0 ? 0 : deltaH);
    }
}
