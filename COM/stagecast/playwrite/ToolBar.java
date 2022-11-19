/* ToolBar - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Rect;

public class ToolBar extends PlaywriteView
{
    public ToolBar(AppearanceEditor editor) {
	AppearanceEditorTool[][] tools
	    = { { new RectangularSelectionTool(editor),
		  new LassoTool(editor) },
		{ new PencilTool(editor), new EraserTool(editor) },
		{ new PaintBucketTool(editor), new PaintBrushTool(editor) },
		{ new LineTool(editor), new TextTool(editor) },
		{ new RectangleTool(editor), new OvalTool(editor) } };
	for (int y = 0; y < 5; y++) {
	    for (int x = 0; x < 2; x++) {
		AppearanceEditorTool t = tools[y][x];
		Rect r = new Rect(x * 25, y * 25, 25, 25);
		t.setBounds(r);
		this.addSubview(t);
	    }
	}
	this.setTransparent(true);
    }
}
