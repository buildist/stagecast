/* PixelLevelTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;

public abstract class PixelLevelTool extends AppearanceEditorTool
{
    private int startX;
    private int startY;
    private boolean shiftMode;
    private boolean horizontal;
    private boolean needToCalculateDirection;
    Point lastPoint;
    
    public PixelLevelTool(AppearanceEditor editor, String imageResourceID,
			  String toolTipResourceID) {
	super(editor, imageResourceID, toolTipResourceID);
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	p.recordStateForUndo();
	Rect r = p.setPixel(x, y, widthToUse(), colorToUse());
	p.logicalDraw(r, 1);
	startX = x;
	startY = y;
	lastPoint = new Point(x, y);
	shiftMode = shiftKey;
	needToCalculateDirection = shiftMode;
    }
    
    public void mouseDragged(int x, int y, boolean ctrlKey, boolean shiftKey,
			     boolean altKey) {
	if (needToCalculateDirection) {
	    needToCalculateDirection = false;
	    horizontal = Math.abs(x - startX) > Math.abs(y - startY);
	}
	if (shiftMode) {
	    if (horizontal)
		y = startY;
	    else
		x = startX;
	}
	int width = widthToUse();
	PaintField p = this.getAppearanceEditor().getPaintField();
	Rect r = p.setPixel(x, y, width, colorToUse());
	if (width > 1)
	    width += 2;
	Rect r2
	    = p.drawLine(x, y, lastPoint.x, lastPoint.y, width, colorToUse());
	p.logicalDraw(r, r2, 1);
	lastPoint = new Point(x, y);
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	p.logicalDraw(p.logicalVisibleRect(), 2);
    }
    
    public abstract Color colorToUse();
    
    public abstract int widthToUse();
}
