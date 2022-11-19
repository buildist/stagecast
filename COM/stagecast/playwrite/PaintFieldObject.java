/* PaintFieldObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Rect;

public class PaintFieldObject
{
    protected final int DRAG_MODE = 1;
    protected final int RESIZE_TOP_LEFT = 2;
    protected final int RESIZE_TOP_RIGHT = 3;
    protected final int RESIZE_BOTTOM_LEFT = 4;
    protected final int RESIZE_BOTTOM_RIGHT = 5;
    public PaintField paintField = null;
    public Rect rect;
    public int grabberSize = 9;
    public boolean stretchMode = false;
    protected int mouseDownMode;
    public boolean selected = true;
    protected Color selectedBorderColor = Color.cyan;
    protected Color deselectedBorderColor = Color.black;
    int lastX;
    int lastY;
    
    public PaintFieldObject(PaintField p, Rect r) {
	paintField = p;
	rect = r;
    }
    
    public void draw(PaintField p, Graphics g) {
	Rect originalClip = g.clipRect();
	int x = p.logicalToPhysicalX(rect.x);
	int y = p.logicalToPhysicalY(rect.y);
	int width = rect.width * p.getScale();
	int height = rect.height * p.getScale();
	if (selected)
	    g.setColor(selectedBorderColor);
	else
	    g.setColor(deselectedBorderColor);
	g.drawRect(x, y, width, height);
	if (stretchMode) {
	    g.setClipRect(new Rect(x, y, width, height), true);
	    int size = grabberSize;
	    g.setColor(Color.black);
	    g.fillRect(x, y, size, size);
	    g.fillRect(x + width - size, y, size, size);
	    g.fillRect(x, y + height - size, size, size);
	    g.fillRect(x + width - size, y + height - size, size, size);
	    g.setColor(Color.gray);
	    g.drawRect(x, y, size, size);
	    g.drawRect(x + width - size, y, size, size);
	    g.drawRect(x, y + height - size, size, size);
	    g.drawRect(x + width - size, y + height - size, size, size);
	}
	g.setClipRect(originalClip, false);
    }
    
    private void moveBy(int deltaX, int deltaY) {
	Rect oldRect = new Rect(rect);
	rect.x = rect.x + deltaX;
	rect.y = rect.y + deltaY;
	paintField.logicalDraw(rect, oldRect, 0);
    }
    
    private void resizeTo(int newX, int newY, int newWidth, int newHeight) {
	Rect oldRect = new Rect(rect);
	rect.x = newX;
	rect.y = newY;
	if (newWidth < 1)
	    newWidth = 1;
	rect.width = newWidth;
	if (newHeight < 1)
	    newHeight = 1;
	rect.height = newHeight;
	paintField.logicalDraw(rect, oldRect, 0);
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	lastX = x;
	lastY = y;
	mouseDownMode = 1;
	if (stretchMode) {
	    int size = grabberSize / paintField.getScale();
	    if (size < 1)
		size = 1;
	    int rightX = rect.x + rect.width - size;
	    int bottomY = rect.y + rect.height - size;
	    if (Rect.contains(rect.x, rect.y, size, size, x, y))
		mouseDownMode = 2;
	    else if (Rect.contains(rightX, rect.y, size, size, x, y))
		mouseDownMode = 3;
	    else if (Rect.contains(rect.x, bottomY, size, size, x, y))
		mouseDownMode = 4;
	    else if (Rect.contains(rightX, bottomY, size, size, x, y))
		mouseDownMode = 5;
	}
    }
    
    public void mouseDragged(int x, int y, boolean ctrlKey, boolean shiftKey,
			     boolean altKey) {
	int deltaX = x - lastX;
	int deltaY = y - lastY;
	if (mouseDownMode == 1)
	    moveBy(deltaX, deltaY);
	else {
	    switch (mouseDownMode) {
	    case 2: {
		int newX = Math.min(x, rect.x + rect.width - 1);
		int newY = Math.min(y, rect.y + rect.height - 1);
		resizeTo(newX, newY, rect.x + rect.width - newX,
			 rect.y + rect.height - newY);
		break;
	    }
	    case 3: {
		int newY = Math.min(y, rect.y + rect.height - 1);
		resizeTo(rect.x, newY, Math.max(x - rect.x, 1),
			 rect.y + rect.height - newY);
		break;
	    }
	    case 4: {
		int newX = Math.min(x, rect.x + rect.width - 1);
		resizeTo(newX, rect.y, rect.x + rect.width - newX,
			 Math.max(y - rect.y, 1));
		break;
	    }
	    case 5:
		resizeTo(rect.x, rect.y, Math.max(x - rect.x, 1),
			 Math.max(y - rect.y, 1));
		break;
	    }
	}
	lastX = x;
	lastY = y;
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	/* empty */
    }
    
    public void delete() {
	/* empty */
    }
    
    public void deselect() {
	/* empty */
    }
    
    public void keyDown(KeyEvent event) {
	int delta = 1;
	if (event.isShiftKeyDown())
	    delta = 5;
	if (event.isControlKeyDown())
	    delta *= 10;
	switch (event.key) {
	case 8:
	case 127:
	    delete();
	    break;
	case 27:
	    deselect();
	    break;
	case 1006:
	    moveBy(-delta, 0);
	    break;
	case 1007:
	    moveBy(delta, 0);
	    break;
	case 1004:
	    moveBy(0, -delta);
	    break;
	case 1005:
	    moveBy(0, delta);
	    break;
	}
    }
    
    public void setColor(Color c) {
	/* empty */
    }
    
    public void setFont(Font f) {
	/* empty */
    }
    
    public void setJustification(int justification) {
	/* empty */
    }
}
