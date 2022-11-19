/* PaintBucketTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PaintBucketTool extends AppearanceEditorTool
    implements ResourceIDs.PicturePainterIDs
{
    public PaintBucketTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Paint Bucket Tool",
	      "Picture Painter Paint Bucket Tool");
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	this.getAppearanceEditor().getPaintField()
	    .fillArea(x, y, this.getAppearanceEditor().getColor(), shiftKey);
    }
}
