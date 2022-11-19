/* TextTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.FontMetrics;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class TextTool extends AppearanceEditorTool
    implements ResourceIDs.PicturePainterIDs
{
    private boolean inEditingMode = false;
    private Point _referencePoint = new Point();
    private char[] chars = new char[256];
    private int length = 0;
    private Font font;
    private Rect _eraseCharsRect = null;
    
    public TextTool(AppearanceEditor editor) {
	super(editor, "Picture Painter Text Tool",
	      "Picture Painter Text Tool");
	font = editor.getFont();
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	if (inEditingMode)
	    stopEditing();
	reset();
	_referencePoint.x = x;
	_referencePoint.y
	    = y + (int) ((double) font.fontMetrics().height() / 2.5);
	PaintField p = this.getAppearanceEditor().getPaintField();
	p.setFocusedView();
	p.recordStateForUndo();
	inEditingMode = true;
	drawChars(true, true);
    }
    
    public void keyDown(KeyEvent event) {
	if (inEditingMode) {
	    if (event.key == 10)
		event.key = 10;
	    switch (event.key) {
	    case 8:
	    case 127:
		if (length > 0) {
		    eraseChars();
		    length--;
		    drawChars(true, true);
		}
		break;
	    case 10:
	    case 27:
		stopEditing();
		break;
	    default:
		if (isPrintableKey(event) == true) {
		    eraseChars();
		    chars[length++] = (event.isExtendedKeyEvent()
				       ? event.keyChar() : (char) event.key);
		    drawChars(true, true);
		}
	    }
	}
    }
    
    private static boolean isPrintableKey(KeyEvent event) {
	return (event.isPrintableKey() && event.key != 1002
		&& event.key != 1003 && event.key != 1020 && event.key != 1021
		&& event.key != 1022 && event.key != 1023 && event.key != 1024
		&& event.key != 1025);
    }
    
    public void prepareForPaintFieldChange() {
	stopEditing();
    }
    
    public int cursorForPoint(Point p) {
	return 2;
    }
    
    public void onToolUnset() {
	super.onToolUnset();
	stopEditing();
    }
    
    public void setFont(Font f) {
	eraseChars();
	font = f;
	drawChars(true, true);
	super.setFont(f);
    }
    
    public void setColor(Color c, boolean completed) {
	super.setColor(c, completed);
	if (completed && inEditingMode)
	    drawChars(true, true);
    }
    
    public void setScale(int scale) {
	if (this.state() == true)
	    stopEditing();
    }
    
    private void reset() {
	length = 0;
	inEditingMode = false;
    }
    
    private void stopEditing() {
	if (inEditingMode) {
	    eraseChars();
	    drawChars(false, false);
	    PaintField p = this.getAppearanceEditor().getPaintField();
	    p.logicalDraw(new Rect(0, 0, p.getLogicalSize().width,
				   p.getLogicalSize().height),
			  2);
	    inEditingMode = false;
	}
    }
    
    private void eraseChars() {
	if (inEditingMode) {
	    FontMetrics metrics = font.fontMetrics();
	    int width = metrics.charsWidth(chars, 0, length);
	    int height = metrics.height();
	    int descent = metrics.descent();
	    int INSERTION_BAR_FUDGE = 4;
	    Rect r = new Rect(_referencePoint.x - 1,
			      (_referencePoint.y - height + descent
			       - INSERTION_BAR_FUDGE),
			      width + 7, height + INSERTION_BAR_FUDGE);
	    PaintField p = this.getAppearanceEditor().getPaintField();
	    p.eraseRect(r);
	    _eraseCharsRect = r;
	}
    }
    
    private void drawChars(boolean drawPoint, boolean redraw) {
	if (inEditingMode) {
	    AppearanceEditor ed = this.getAppearanceEditor();
	    PaintField p = ed.getPaintField();
	    p.drawChars(_referencePoint.x, _referencePoint.y, chars, 0, length,
			ed.getColor(), font);
	    int width = font.fontMetrics().charsWidth(chars, 0, length);
	    if (drawPoint)
		drawInsertionPoint(_referencePoint.x + width,
				   _referencePoint.y);
	    if (redraw)
		redraw(0);
	}
    }
    
    private void redraw(int update) {
	if (inEditingMode) {
	    PaintField p = this.getAppearanceEditor().getPaintField();
	    FontMetrics metrics = font.fontMetrics();
	    int height = metrics.height();
	    int width = metrics.charsWidth(chars, 0, length + 1);
	    int descent = metrics.descent();
	    int topY = _referencePoint.y - height + descent;
	    Rect r = new Rect(_referencePoint.x - 1, topY, width + 7, height);
	    if (_eraseCharsRect != null) {
		r = Rect.rectFromUnion(r, _eraseCharsRect);
		_eraseCharsRect = null;
	    }
	    r.intersectWith(p.logicalVisibleRect());
	    p.logicalDraw(r, update);
	}
    }
    
    private void drawInsertionPoint(int x, int y) {
	AppearanceEditor ed = this.getAppearanceEditor();
	PaintField p = ed.getPaintField();
	FontMetrics metrics = font.fontMetrics();
	int topY = y - metrics.maxAscent();
	p.drawRect(x, y, x + 1, topY, 1, ed.getColor(), true);
	p.drawRect(x - 1, topY, x + 2, topY + 1, 1, ed.getColor(), true);
	p.drawRect(x - 1, y, x + 2, y + 1, 1, ed.getColor(), true);
	Rect r = new Rect(x - 1, topY, x + 2, y);
	r.intersectWith(p.logicalVisibleRect());
	if (!r.isEmpty())
	    p.logicalDraw(r, 1);
    }
}
