/* RectangleTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class RectangleTool extends DualFunctionTool
    implements ResourceIDs.PicturePainterIDs
{
    public RectangleTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Rectangle Tool");
    }
    
    public Rect drawIt(int x, int y, int x2, int y2) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	p.drawRect(x, y, x2, y2, this.getAppearanceEditor().getBrushWidth(),
		   this.getAppearanceEditor().getColor(), lineDraw ^ true);
	return p.minimalRect(x, y, x2, y2);
    }
    
    public void drawViewInterior(Graphics graphics, Rect interiorRect) {
	super.drawViewInterior(graphics, interiorRect);
	graphics.setColor(Color.white);
	graphics.fillRect(interiorRect.x + 2, interiorRect.y + 4,
			  interiorRect.width - 4, interiorRect.height - 8);
	graphics.setColor(Color.black);
	graphics.drawRect(interiorRect.x + 2, interiorRect.y + 4,
			  interiorRect.width - 4, interiorRect.height - 8);
	graphics.fillRect(interiorRect.x + interiorRect.width / 2,
			  interiorRect.y + 4, interiorRect.width / 2 - 2,
			  interiorRect.height - 8);
    }
}
