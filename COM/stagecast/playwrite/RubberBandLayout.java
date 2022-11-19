/* RubberBandLayout - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.LayoutManager;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

class RubberBandLayout implements LayoutManager
{
    private View clientView;
    public int topMargin = 5;
    public int leftMargin = 5;
    public int bottomMargin = 5;
    public int rightMargin = 5;
    
    RubberBandLayout(View clientView) {
	this.clientView = clientView;
    }
    
    RubberBandLayout(View clientView, int rightMargin, int bottomMargin) {
	this.clientView = clientView;
	this.rightMargin = rightMargin;
	this.bottomMargin = bottomMargin;
    }
    
    RubberBandLayout(View clientView, int margin) {
	this.clientView = clientView;
	setMargins(margin);
    }
    
    public void setMargins(int margin) {
	topMargin = margin;
	leftMargin = margin;
	bottomMargin = margin;
	rightMargin = margin;
    }
    
    public void addSubview(View subview) {
	if (subview.x() < leftMargin)
	    subview.moveBy(leftMargin, 0);
	if (subview.y() < topMargin)
	    subview.moveBy(0, topMargin);
	clientView.layoutView(0, 0);
    }
    
    public void removeSubview(View subview) {
	clientView.layoutView(0, 0);
    }
    
    public void layoutView(View layoutView, int deltaWidth, int deltaHeight) {
	Vector subviews = layoutView.subviews();
	if (subviews != null) {
	    Enumeration viewList = subviews.elements();
	    Rect newBounds = new Rect();
	    while (viewList.hasMoreElements()) {
		View view = (View) viewList.nextElement();
		newBounds.unionWith(view.bounds());
	    }
	    layoutView.sizeTo(newBounds.width + rightMargin,
			      newBounds.height + bottomMargin);
	    layoutView.addDirtyRect(null);
	}
    }
}
