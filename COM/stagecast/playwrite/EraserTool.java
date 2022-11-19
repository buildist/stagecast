/* EraserTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class EraserTool extends PixelLevelTool
    implements ResourceIDs.PicturePainterIDs
{
    public EraserTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Eraser Tool",
	      "Picture Painter Eraser Tool");
    }
    
    public Color colorToUse() {
	return TransparentGraphics.T_COLOR;
    }
    
    public int widthToUse() {
	return this.getAppearanceEditor().getBrushWidth();
    }
    
    public boolean allowsBrushWidth() {
	return true;
    }
    
    public int cursorForPoint(Point p) {
	return 1;
    }
}
