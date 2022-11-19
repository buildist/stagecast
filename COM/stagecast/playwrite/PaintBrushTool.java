/* PaintBrushTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PaintBrushTool extends PixelLevelTool
    implements ResourceIDs.PicturePainterIDs
{
    public PaintBrushTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Paint Brush Tool",
	      "Picture Painter Paint Brush Tool");
    }
    
    public Color colorToUse() {
	return this.getAppearanceEditor().getColor();
    }
    
    public int widthToUse() {
	return this.getAppearanceEditor().getBrushWidth();
    }
    
    public boolean allowsBrushWidth() {
	return true;
    }
}
