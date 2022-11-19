/* WidthButton - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class WidthButton extends AppearanceEditorTool
    implements ResourceIDs.OvalButtonIDs
{
    private static Bitmap RAISED_BUTTON_BACKGROUND;
    private static Bitmap LOWERED_BUTTON_BACKGROUND;
    private static Bitmap DISABLER;
    private int size = 0;
    
    static void initStatics() {
	RAISED_BUTTON_BACKGROUND = Resource.getImage("Oval Button sub");
	LOWERED_BUTTON_BACKGROUND = Resource.getImage("Oval Button sdb");
	DISABLER = GrayLayer.grayTransparentBitmap;
    }
    
    public WidthButton(AppearanceEditor editor, int size,
		       String toolTipResourceID) {
	super(editor, toolTipResourceID);
	this.size = size;
	this.sizeTo(RAISED_BUTTON_BACKGROUND.width(),
		    RAISED_BUTTON_BACKGROUND.height());
	this.setTransparent(true);
    }
    
    public void drawView(Graphics graphics) {
	boolean isPressed = this.state();
	(isPressed ? LOWERED_BUTTON_BACKGROUND : RAISED_BUTTON_BACKGROUND)
	    .drawAt(graphics, 0, 0);
	int offset = isPressed ? 1 : 0;
	graphics.setColor(Color.black);
	graphics.fillRect(this.width() / 2 - size / 2 + offset,
			  this.height() / 2 - size / 2, size, size);
	if (this.isEnabled() == false)
	    DISABLER.drawAt(graphics, 0, 0);
    }
    
    public boolean mouseDown(MouseEvent e) {
	if (this.isEnabled())
	    this.getAppearanceEditor().setBrushWidth(size);
	return true;
    }
    
    public void setTool(AppearanceEditorTool tool) {
	this.setEnabled(tool.allowsBrushWidth());
    }
    
    public void setBrushWidth(int width) {
	this.setState(width == size);
    }
    
    public void setScale(int scale) {
	/* empty */
    }
}
