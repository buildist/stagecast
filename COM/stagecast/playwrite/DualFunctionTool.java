/* DualFunctionTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Rect;

public abstract class DualFunctionTool extends XORDrawingTool
{
    public boolean lineDraw = false;
    
    public DualFunctionTool(AppearanceEditor editor,
			    String toolTipResourceID) {
	super(editor, toolTipResourceID);
    }
    
    public boolean mouseDown(MouseEvent e) {
	lineDraw = e.x <= this.width() / 2;
	super.mouseDown(e);
	this.draw();
	return true;
    }
    
    public boolean allowsBrushWidth() {
	return lineDraw;
    }
    
    public void drawView(Graphics graphics) {
	Border border = (this.state() == true ? this.loweredBorder()
			 : this.raisedBorder());
	border.drawInRect(graphics, 0, 0, this.width(), this.height());
	Rect interiorRect = new Rect();
	interiorRect.x = border.leftMargin();
	interiorRect.y = border.topMargin();
	interiorRect.width = this.width() - border.widthMargin();
	interiorRect.height = this.height() - border.heightMargin();
	drawViewInterior(graphics, interiorRect);
    }
    
    public void drawViewInterior(Graphics graphics, Rect interiorRect) {
	if (this.state() == true) {
	    graphics
		.setColor(lineDraw ? this.loweredColor() : this.raisedColor());
	    graphics.fillRect(interiorRect.x, interiorRect.y,
			      interiorRect.width / 2, interiorRect.height);
	    graphics
		.setColor(lineDraw ? this.raisedColor() : this.loweredColor());
	    graphics.fillRect(interiorRect.x + interiorRect.width / 2,
			      interiorRect.y, interiorRect.width / 2,
			      interiorRect.height);
	} else {
	    graphics.setColor(this.raisedColor());
	    graphics.fillRect(interiorRect.x, interiorRect.y,
			      interiorRect.width, interiorRect.height);
	}
    }
    
    public final void drawViewInterior(Graphics graphics, String title,
				       Image image, Rect interiorRect) {
	/* empty */
    }
    
    public final void drawViewBackground(Graphics graphics, Rect interiorRect,
					 boolean drawDownState) {
	/* empty */
    }
}
