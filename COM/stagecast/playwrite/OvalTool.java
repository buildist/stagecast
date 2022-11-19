/* OvalTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class OvalTool extends DualFunctionTool
    implements ResourceIDs.PicturePainterIDs
{
    public OvalTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Oval Tool");
    }
    
    public Rect drawIt(int x, int y, int x2, int y2) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	p.drawOval(x, y, x2, y2, this.getAppearanceEditor().getBrushWidth(),
		   this.getAppearanceEditor().getColor(), lineDraw ^ true);
	return p.minimalRect(x, y, x2, y2);
    }
    
    public void drawViewInterior(Graphics graphics, Rect interiorRect) {
	super.drawViewInterior(graphics, interiorRect);
	graphics.setColor(Color.white);
	graphics.fillOval(interiorRect.x + 2, interiorRect.y + 2,
			  interiorRect.width - 4, interiorRect.height - 4);
	graphics.setColor(Color.black);
	graphics.drawOval(interiorRect.x + 2, interiorRect.y + 2,
			  interiorRect.width - 4, interiorRect.height - 4);
	graphics.setClipRect(new Rect(interiorRect.x + interiorRect.width / 2,
				      interiorRect.y, interiorRect.width / 2,
				      interiorRect.height));
	graphics.fillOval(interiorRect.x + 2, interiorRect.y + 2,
			  interiorRect.width - 4, interiorRect.height - 4);
    }
}
