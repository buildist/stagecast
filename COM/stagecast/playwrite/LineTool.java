/* LineTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class LineTool extends XORDrawingTool
    implements ResourceIDs.PicturePainterIDs
{
    public LineTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Line Tool",
	      "Picture Painter Line Tool");
    }
    
    protected Point shiftKeyConstrainPoint(int x, int y) {
	int width = Math.abs(x - startX);
	int height = Math.abs(y - startY);
	if (width < height) {
	    float ratio = (float) width / (float) height;
	    if ((double) ratio < 0.25) {
		if (y < startY)
		    x = startX;
		else
		    x = startX;
	    } else if (y < startY)
		y = startY - width;
	    else
		y = startY + width;
	} else {
	    float ratio = (float) height / (float) width;
	    if ((double) ratio < 0.25) {
		if (x < startX)
		    y = startY;
		else
		    y = startY;
	    } else if (x < startX)
		x = startX - height;
	    else
		x = startX + height;
	}
	return new Point(x, y);
    }
    
    public Rect drawIt(int x, int y, int x2, int y2) {
	return (this.getAppearanceEditor().getPaintField().drawLine
		(x, y, x2, y2, this.getAppearanceEditor().getBrushWidth(),
		 this.getAppearanceEditor().getColor()));
    }
    
    public boolean allowsBrushWidth() {
	return true;
    }
}
