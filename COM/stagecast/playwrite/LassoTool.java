/* LassoTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class LassoTool extends AppearanceEditorTool
    implements ResourceIDs.PicturePainterIDs
{
    protected Vector pointVector = null;
    protected boolean firstDrag = false;
    
    public LassoTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Lasso Tool",
	      "Picture Painter Lasso Tool");
    }
    
    public void onToolUnset() {
	super.onToolUnset();
	this.getAppearanceEditor().getPaintField().deselect();
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	if (p.getCurrentSelection() != null)
	    p.deselect();
	pointVector = new Vector();
	pointVector.addElement(new Point(x, y));
	firstDrag = true;
    }
    
    public void mouseDragged(int x, int y, boolean ctrlKey, boolean shiftKey,
			     boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	if (firstDrag == true) {
	    firstDrag = false;
	    p.recordStateForUndo();
	}
	Point lastPoint = (Point) pointVector.lastElement();
	pointVector.addElement(new Point(x, y));
	Rect r = p.drawLine(x, y, lastPoint.x, lastPoint.y, 1, Color.cyan);
	p.logicalDraw(r, 1);
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	Point previousPoint = (Point) pointVector.firstElement();
	int maxX;
	int minX = maxX = previousPoint.x;
	int maxY;
	int minY = maxY = previousPoint.y;
	for (int i = 1; i < pointVector.size(); i++) {
	    Point currentPoint = (Point) pointVector.elementAt(i);
	    if (currentPoint.x < minX)
		minX = currentPoint.x;
	    if (currentPoint.y < minY)
		minY = currentPoint.y;
	    if (currentPoint.x > maxX)
		maxX = currentPoint.x;
	    if (currentPoint.y > maxY)
		maxY = currentPoint.y;
	    previousPoint = currentPoint;
	}
	if (pointVector.size() > 1) {
	    pointVector.addElement(pointVector.firstElement());
	    int width = maxX - minX + 1;
	    int height = maxY - minY + 1;
	    p.eraseRect(new Rect(minX, minY, width, height));
	    Vector spans
		= new ScanConvertor().scanConvertConcavePolygon(pointVector);
	    p.setCurrentSelection(new Rect(minX, minY, width, height), true,
				  spans);
	}
    }
}
