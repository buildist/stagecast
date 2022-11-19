/* EyeDropper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class EyeDropper extends AppearanceEditorTool
    implements ResourceIDs.PicturePainterIDs
{
    public EyeDropper(AppearanceEditor editor) {
	super(editor, "Picture Painter Eye Dropper Tool",
	      "Picture Painter Eye Dropper Tool");
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	Color colorUnderMouse
	    = this.getAppearanceEditor().getPaintField().getValueAt(x, y);
	if (!colorUnderMouse.equals(this.getAppearanceEditor().getColor()))
	    this.getAppearanceEditor().setColor(colorUnderMouse, false);
    }
    
    public void mouseDragged(int x, int y, boolean ctrlKey, boolean shiftKey,
			     boolean altKey) {
	mouseDown(x, y, ctrlKey, shiftKey, altKey);
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	Color colorUnderMouse
	    = this.getAppearanceEditor().getPaintField().getValueAt(x, y);
	this.getAppearanceEditor().setColor(colorUnderMouse, true);
    }
}
