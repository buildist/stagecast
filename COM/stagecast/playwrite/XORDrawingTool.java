/* XORDrawingTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;

public abstract class XORDrawingTool extends AppearanceEditorTool
{
    protected int startX;
    protected int startY;
    protected int lastX;
    protected int lastY;
    protected Rect lastRect;
    protected boolean firstDrag = false;
    
    public XORDrawingTool(AppearanceEditor editor, String imageResourceID,
			  String toolTipResourceID) {
	super(editor, imageResourceID, toolTipResourceID);
    }
    
    public XORDrawingTool(AppearanceEditor editor, String toolTipResourceID) {
	super(editor, toolTipResourceID);
    }
    
    public abstract Rect drawIt(int i, int i_0_, int i_1_, int i_2_);
    
    public void commit(int x, int y, int x2, int y2) {
	/* empty */
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	startX = lastX = x;
	startY = lastY = y;
	lastRect = new Rect(x, y, 0, 0);
	firstDrag = true;
    }
    
    protected Point shiftKeyConstrainPoint(int x, int y) {
	int width = Math.abs(x - startX);
	int height = Math.abs(y - startY);
	if (width < height) {
	    if (y < startY)
		y = startY - width;
	    else
		y = startY + width;
	} else if (x < startX)
	    x = startX - height;
	else
	    x = startX + height;
	return new Point(x, y);
    }
    
    public void mouseDragged(int x, int y, boolean ctrlKey, boolean shiftKey,
			     boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	if (firstDrag == true) {
	    firstDrag = false;
	    p.recordStateForUndo();
	}
	p.eraseRect(lastRect);
	if (shiftKey) {
	    Point pt = shiftKeyConstrainPoint(x, y);
	    x = pt.x;
	    y = pt.y;
	}
	Rect newRect = drawIt(startX, startY, x, y);
	p.logicalDraw(lastRect.intersectionRect(p.logicalVisibleRect()),
		      newRect.intersectionRect(p.logicalVisibleRect()), 1);
	lastRect = newRect;
	lastX = x;
	lastY = y;
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	commit(startX, startY, lastX, lastY);
	p.logicalDraw(p.logicalVisibleRect(), 2);
    }
}
