/* SelectView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

class SelectView extends View
{
    private final Color SELECT_COLOR = Util.defaultDarkColor;
    private int startx;
    private int starty;
    private Vector[] quadrantViews = new Vector[4];
    private int oldQuadrant = 0;
    private PlaywriteSelectView creator;
    private Rect maxRect = new Rect();
    
    SelectView(PlaywriteSelectView creator, int x, int y) {
	super(x, y, 0, 0);
	startx = x;
	starty = y;
	this.creator = creator;
	creator.addSubview(this);
	creator.computeVisibleRect(maxRect);
	Selection.resetGlobalState();
	PlaywriteRoot.getMainRootView().setMouseView(this);
	initQuadrants();
    }
    
    public void drawView(Graphics g) {
	g.setColor(SELECT_COLOR);
	g.drawRect(0, 0, this.width(), this.height());
    }
    
    public void mouseDragged(MouseEvent event) {
	Point mouseloc
	    = this.convertToView(this.superview(), event.x, event.y);
	int quadrant = 3;
	int x = startx;
	int y = starty;
	int width = mouseloc.x - startx;
	int height = mouseloc.y - starty;
	if (width < 0) {
	    x = mouseloc.x;
	    width = startx - mouseloc.x;
	    quadrant = 2;
	}
	if (height < 0) {
	    y = mouseloc.y;
	    height = starty - mouseloc.y;
	    if (quadrant == 3)
		quadrant = 1;
	    else
		quadrant = 0;
	}
	if (oldQuadrant != quadrant) {
	    Selection.unselectAll();
	    oldQuadrant = quadrant;
	}
	Rect newBounds = new Rect(x, y, width, height);
	newBounds.intersectWith(maxRect);
	this.setBounds(newBounds);
	this.superview().addDirtyRect(bounds);
	Vector views = quadrantViews[quadrant];
	for (int i = 0; i < views.size(); i++) {
	    if (views.elementAt(i) instanceof PlaywriteView) {
		Object model
		    = ((PlaywriteView) views.elementAt(i)).getModelObject();
		if (model instanceof Selectable) {
		    if (((View) views.elementAt(i)).bounds
			    .intersects(bounds)) {
			if (!Selection.isSelected((Selectable) model)) {
			    Selection.addToSelection((Selectable) model,
						     creator);
			    ((View) views.elementAt(i)).setFocusedView();
			}
		    } else if (Selection.isSelected((Selectable) model))
			Selection.unselect((Selectable) model);
		}
	    }
	}
	this.superview().draw();
    }
    
    public void mouseUp(MouseEvent event) {
	creator.setSelecting(false);
	creator.setSelectView(null);
	this.superview().addDirtyRect(bounds);
	this.removeFromSuperview();
	for (int k = 0; k < 4; k++)
	    quadrantViews[k].removeAllElements();
    }
    
    private void initQuadrants() {
	Vector views = this.superview().subviews();
	Rect[] q = new Rect[4];
	q[0] = new Rect(0, 0, startx, starty);
	q[1] = new Rect(startx, 0, this.superview().width() - startx, starty);
	q[2] = new Rect(0, starty, startx, this.superview().height() - starty);
	q[3] = new Rect(startx, starty, this.superview().width() - startx,
			this.superview().height() - starty);
	for (int k = 0; k < 4; k++)
	    quadrantViews[k] = new Vector(5);
	for (int i = 0; i < views.size() - 1; i++) {
	    View v = (View) views.elementAt(i);
	    for (int k = 0; k < 4; k++) {
		if (q[k].intersects(v.bounds))
		    quadrantViews[k].addElement(v);
	    }
	}
    }
}
