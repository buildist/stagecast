/* BrushSizeBar - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class BrushSizeBar extends PlaywriteView
    implements ResourceIDs.PicturePainterIDs
{
    public BrushSizeBar(AppearanceEditor editor) {
	String[] toolTipKeys
	    = { "Picture Painter Brush Size 1", "Picture Painter Brush Size 3",
		"Picture Painter Brush Size 5", "Picture Painter Brush Size 7",
		"Picture Painter Brush Size 9" };
	for (int y = 0; y < 5; y++) {
	    WidthButton t = new WidthButton(editor, y * 2 + 1, toolTipKeys[y]);
	    t.moveTo(0, y * 25);
	    this.addSubview(t);
	}
	this.setTransparent(true);
    }
}
