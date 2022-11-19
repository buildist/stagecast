/* PencilTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PencilTool extends PixelLevelTool
    implements ResourceIDs.PicturePainterIDs
{
    boolean erasing = false;
    
    public PencilTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Pencil Tool",
	      "Picture Painter Pencil Tool");
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	Color initialValue
	    = this.getAppearanceEditor().getPaintField().getValueAt(x, y);
	erasing = initialValue.equals(this.getAppearanceEditor().getColor());
	super.mouseDown(x, y, ctrlKey, shiftKey, altKey);
    }
    
    public Color colorToUse() {
	if (erasing)
	    return TransparentGraphics.T_COLOR;
	return this.getAppearanceEditor().getColor();
    }
    
    public int widthToUse() {
	return 1;
    }
}
