/* ScaleBar - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class ScaleBar extends AppearanceEditorView
    implements ResourceIDs.PicturePainterIDs, AppearanceEventListener
{
    protected Bitmap litBitmap;
    protected Bitmap backgroundBitmap;
    protected int scaleIndex = 3;
    protected int offsetToFirstButton = 11;
    protected int yOffset = 0;
    protected int buttonSizePlusOffsetBetweenButtons = 12;
    
    public ScaleBar(AppearanceEditor editor) {
	super(editor);
	backgroundBitmap = Resource.getImage("Picture Painter Scale Bar");
	litBitmap = Resource.getImage("Picture Painter Scale Bar Hilite");
	this.sizeTo(backgroundBitmap.width(), backgroundBitmap.height());
	editor.addAppearanceEventListener(this);
	this.setToolTipText(Resource.getToolTip("Picture Painter Scale Bar"));
	this.setTransparent(true);
    }
    
    public void drawView(Graphics g) {
	backgroundBitmap.drawAt(g, 0, 0);
	litBitmap.drawAt(g, (scaleIndex * buttonSizePlusOffsetBetweenButtons
			     + offsetToFirstButton), yOffset);
	if (this.getGrayLayer() != null)
	    this.getGrayLayer().drawView(g);
    }
    
    public boolean mouseDown(MouseEvent e) {
	return this.isDisabled() ^ true;
    }
    
    public void mouseUp(MouseEvent e) {
	int newScale = this.getAppearanceEditor().getScale();
	if (e.x < offsetToFirstButton) {
	    newScale /= 2;
	    if (newScale < 1)
		newScale = 1;
	} else if (e.x
		   < offsetToFirstButton + buttonSizePlusOffsetBetweenButtons)
	    newScale = 1;
	else if (e.x < (offsetToFirstButton
			+ buttonSizePlusOffsetBetweenButtons * 2))
	    newScale = 2;
	else if (e.x < (offsetToFirstButton
			+ buttonSizePlusOffsetBetweenButtons * 3))
	    newScale = 4;
	else if (e.x < (offsetToFirstButton
			+ buttonSizePlusOffsetBetweenButtons * 4))
	    newScale = 8;
	else {
	    newScale *= 2;
	    if (newScale > 8)
		newScale = 8;
	}
	if (newScale != this.getAppearanceEditor().getScale())
	    this.getAppearanceEditor().setScale(newScale);
    }
    
    public void discard() {
	super.discard();
	litBitmap.flush();
	litBitmap = null;
	backgroundBitmap.flush();
	backgroundBitmap = null;
    }
    
    public void setTool(AppearanceEditorTool tool) {
	/* empty */
    }
    
    public void setBrushWidth(int width) {
	/* empty */
    }
    
    public void setColor(Color color, boolean completed) {
	/* empty */
    }
    
    public void setScale(int s) {
	switch (s) {
	case 1:
	    scaleIndex = 0;
	    break;
	case 2:
	    scaleIndex = 1;
	    break;
	case 4:
	    scaleIndex = 2;
	    break;
	case 8:
	    scaleIndex = 3;
	    break;
	}
	this.draw();
    }
    
    public void setFont(Font font) {
	/* empty */
    }
    
    public void setJustification(int justification) {
	/* empty */
    }
}
