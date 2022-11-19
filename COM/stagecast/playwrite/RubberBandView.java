/* RubberBandView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

public class RubberBandView extends PlaywriteView
{
    int margins = 0;
    boolean sizeToMin = false;
    
    public RubberBandView(int x, int y, int width, int height) {
	super(x, y, width, height);
	this.setAutoResizeSubviews(false);
	setMargins(margins);
    }
    
    public RubberBandView(Rect bounds) {
	this(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    public RubberBandView() {
	this(0, 0, 0, 0);
    }
    
    void setMargins(int m) {
	margins = m;
	recomputeSize();
    }
    
    int getMargins() {
	return margins;
    }
    
    void setSizeToMin(boolean sz) {
	if (sz != sizeToMin) {
	    sizeToMin = sz;
	    if (sizeToMin)
		recomputeSize();
	}
    }
    
    boolean getSizeToMin() {
	return sizeToMin;
    }
    
    public void setBounds(int x, int y, int width, int height) {
	Size min = this.minSize();
	if (min.width > width)
	    width = min.width;
	if (min.height > height)
	    height = min.height;
	super.setBounds(x, y, width, height);
    }
    
    public void addSubview(View subview) {
	if (checkExpand(subview))
	    this.sizeToMinSize();
	super.addSubview(subview);
    }
    
    protected void removeSubview(View subview) {
	if (sizeToMin)
	    recomputeSize();
	super.removeSubview(subview);
    }
    
    public void subviewDidResize(View subview) {
	if (this.subviews().containsIdentical(subview) && checkExpand(subview))
	    this.sizeToMinSize();
	super.subviewDidResize(subview);
    }
    
    public void subviewDidMove(View subview) {
	if (this.subviews().containsIdentical(subview) && checkExpand(subview))
	    this.sizeToMinSize();
	super.subviewDidMove(subview);
    }
    
    void recomputeSize() {
	Vector subviewList = this.subviews();
	boolean changed = false;
	this.setMinSize(2 * margins, 2 * margins);
	for (int i = 0; i < subviewList.size(); i++)
	    changed |= checkExpand((View) subviewList.elementAt(i));
	if (changed)
	    this.sizeToMinSize();
    }
    
    boolean checkExpand(View subview) {
	boolean changed = false;
	Size min = this.minSize();
	if (subview.x() < margins)
	    subview.moveTo(margins, subview.y());
	if (subview.y() < margins)
	    subview.moveTo(subview.x(), margins);
	int pos = subview.x() + subview.width() + margins;
	if (pos > min.width)
	    min.width = pos;
	changed = changed || pos > this.width();
	pos = subview.y() + subview.height() + margins;
	if (pos > min.height)
	    min.height = pos;
	changed = changed || pos > this.height();
	this.setMinSize(min.width, min.height);
	this.addDirtyRect(this.bounds());
	return changed;
    }
}
