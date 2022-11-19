/* CurrentColorIndicator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class CurrentColorIndicator extends AppearanceEditorView
    implements AppearanceEventListener, ResourceIDs.PicturePainterIDs, Target
{
    public Color currentColor = Color.black;
    
    public CurrentColorIndicator(AppearanceEditor editor) {
	super(editor);
	this.getAppearanceEditor().addAppearanceEventListener(this);
    }
    
    public void drawView(Graphics g) {
	g.setColor(this.getAppearanceEditor().getDarkColor());
	g.drawRect(0, 0, this.width(), 1);
	g.drawRect(0, 0, 1, this.height());
	g.setColor(this.getAppearanceEditor().getLightColor());
	g.drawRect(this.width() - 1, 1, 1, this.height() - 1);
	g.drawRect(1, this.height() - 1, this.width() - 1, 1);
	g.setColor(currentColor);
	g.fillRect(1, 1, this.width() - 2, this.height() - 2);
    }
    
    public boolean mouseDown(MouseEvent e) {
	if (e.clickCount() == 1) {
	    AppearanceEditor a = this.getAppearanceEditor();
	    Point pt = this.convertToView(null, e.x, e.y);
	    Color color
		= Util.getColorFromChooser(this.getWorld(), currentColor,
					   pt.x + 20, pt.y - 20);
	    if (color != null)
		a.setColor(color, true);
	    return true;
	}
	return super.mouseDown(e);
    }
    
    public void setTool(AppearanceEditorTool tool) {
	/* empty */
    }
    
    public void setBrushWidth(int width) {
	/* empty */
    }
    
    public void setColor(Color color, boolean completed) {
	currentColor = color;
	this.draw();
    }
    
    public void setScale(int scale) {
	/* empty */
    }
    
    public void setFont(Font font) {
	/* empty */
    }
    
    public void setJustification(int justification) {
	/* empty */
    }
}
