/* ColorGridView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;

public class ColorGridView extends AppearanceEditorView
    implements AppearanceEventListener
{
    private static final int CELL_SPACING = 1;
    private static final int INSET = 1;
    private static final int TABLE_SIZE = 10;
    private static final Color[][] COLORS = ColorGrid.getColors();
    private static final Color CROSS_COLOR = Color.black;
    private int selectedX = -1;
    private int selectedY = -1;
    
    public ColorGridView(AppearanceEditor editor) {
	super(editor);
	this.getAppearanceEditor().addAppearanceEventListener(this);
	this.setTransparent(true);
	COLORS[0][0] = TransparentGraphics.T_COLOR;
    }
    
    public void drawView(Graphics g) {
	int size = (this.width() - 2 - 11) / 10;
	for (int y = 0; y < 10; y++) {
	    for (int x = 0; x < 10; x++) {
		g.setColor(COLORS[x][y]);
		g.fillRect(x * size + (x + 1) + 1, y * size + (y + 1) + 1,
			   size, size);
	    }
	}
	if (selectedX != -1) {
	    g.setColor(Color.cyan);
	    g.drawRect(selectedX * size + (selectedX + 1) + 1 - 1,
		       selectedY * size + (selectedY + 1) + 1 - 1, size + 2,
		       size + 2);
	}
	g.setColor(CROSS_COLOR);
	g.drawLine(2, 2, size + 1, size + 1);
	g.drawLine(2, size + 1, size + 1, 2);
	g.setColor(this.getAppearanceEditor().getDarkColor());
	g.drawRect(0, 0, this.width(), 1);
	g.drawRect(0, 0, 1, this.height());
	g.setColor(this.getAppearanceEditor().getLightColor());
	g.drawRect(this.width() - 1, 1, 1, this.height() - 1);
	g.drawRect(1, this.height() - 1, this.width() - 1, 1);
    }
    
    public boolean mouseDown(MouseEvent e) {
	selectedX
	    = (int) Math.floor((double) ((float) e.x
					 / ((float) this.width() / 10.0F)));
	selectedY
	    = (int) Math.floor((double) ((float) e.y
					 / ((float) this.width() / 10.0F)));
	if (selectedX < 0)
	    selectedX = 0;
	if (selectedX >= 10)
	    selectedX = 9;
	if (selectedY < 0)
	    selectedY = 0;
	if (selectedY >= 10)
	    selectedY = 9;
	this.getAppearanceEditor().setColor(COLORS[selectedX][selectedY],
					    true);
	return true;
    }
    
    private void findSelectionForColor(Color color) {
	selectedX = -1;
	selectedY = -1;
	for (int y = 0; y < 10; y++) {
	    for (int x = 0; x < 10; x++) {
		if (color.equals(COLORS[x][y])) {
		    selectedX = x;
		    selectedY = y;
		}
	    }
	}
	if (selectedX == 0 && selectedY == 0) {
	    selectedX = -1;
	    selectedY = -1;
	}
    }
    
    public void setTool(AppearanceEditorTool tool) {
	/* empty */
    }
    
    public void setBrushWidth(int width) {
	/* empty */
    }
    
    public void setColor(Color color, boolean completed) {
	if (completed) {
	    if (selectedX == -1 || !color.equals(COLORS[selectedX][selectedY]))
		findSelectionForColor(color);
	    this.draw();
	}
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
