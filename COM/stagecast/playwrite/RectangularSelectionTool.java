/* RectangularSelectionTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class RectangularSelectionTool extends XORDrawingTool
    implements ResourceIDs.PicturePainterIDs
{
    public RectangularSelectionTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Rectangular Selection Tool",
	      "Picture Painter Rectangular Selection Tool");
	this.setState(true);
    }
    
    public void onToolUnset() {
	super.onToolUnset();
	this.getAppearanceEditor().getPaintField().deselect();
    }
    
    public Rect drawIt(int x, int y, int x2, int y2) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	p.drawRect(x, y, x2, y2, 1, Color.cyan, false);
	return p.minimalRect(x, y, x2, y2);
    }
    
    public void commit(int x, int y, int x2, int y2) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	p.eraseRect(p.minimalRect(x, y, x2, y2));
	p.setCurrentSelection(p.minimalRect(x, y, x2, y2), false, null);
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	if (p.getCurrentSelection() != null)
	    p.deselect();
	super.mouseDown(x, y, ctrlKey, shiftKey, altKey);
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	PaintField p = this.getAppearanceEditor().getPaintField();
	commit(startX, startY, lastX, lastY);
    }
}
