/* PaintFieldSelection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Vector;

public class PaintFieldSelection extends PaintFieldObject
{
    private TransparentBitmap _bitmap;
    private Rect _dirtyRect;
    private boolean _floating;
    private Vector _spans;
    
    PaintFieldSelection(PaintField p, Rect r, boolean alphaMask) {
	super(p, r);
	_dirtyRect = new Rect();
	_floating = false;
	_spans = null;
	_bitmap = new TransparentBitmap(r.width, r.height);
	Graphics g = _bitmap.createGraphics();
	COM.stagecast.ifc.netscape.application.Bitmap paintLayer
	    = p.getCurrentState().paintLayer;
	g.drawBitmapAt(paintLayer, -r.x, -r.y);
	g.dispose();
    }
    
    PaintFieldSelection(PaintField p, Rect r, Vector spans) {
	super(p, r);
	_dirtyRect = new Rect();
	_floating = false;
	_spans = null;
	_spans = spans;
	_bitmap = new TransparentBitmap(r.width, r.height);
	Graphics g = _bitmap.createGraphics();
	g.setColor(TransparentGraphics.T_COLOR);
	g.fillRect(0, 0, r.width, r.height);
	COM.stagecast.ifc.netscape.application.Bitmap paintLayer
	    = p.getCurrentState().paintLayer;
	g.drawBitmapAt(paintLayer, -r.x, -r.y);
	g.dispose();
	g = _bitmap.getTransparencyMap().createGraphics();
	g.setColor(TransparentGraphics.TRANSPARENT);
	g.translate(-r.x, -r.y);
	paintInverseSpans(g, r, spans);
	g.dispose();
    }
    
    PaintFieldSelection(PaintFieldSelection s, boolean copyImage) {
	super(s.paintField, new Rect(s.rect));
	_dirtyRect = new Rect();
	_floating = false;
	_spans = null;
	if (copyImage) {
	    int[] pixels = new int[s._bitmap.width() * s._bitmap.height()];
	    s._bitmap.grabPixels(pixels);
	    _bitmap = new TransparentBitmap(pixels, s._bitmap.width(),
					    s._bitmap.height());
	    _bitmap.initTransparencyMap(_bitmap);
	} else
	    _bitmap = s._bitmap;
	stretchMode = s.stretchMode;
	selected = s.selected;
	_floating = true;
    }
    
    public TransparentBitmap getBitmap() {
	return _bitmap;
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	_dirtyRect.setBounds(rect);
	paintField.recordStateForUndo();
	prepareToDrag();
	super.mouseDown(x, y, ctrlKey, shiftKey, altKey);
    }
    
    public void mouseDragged(int x, int y, boolean ctrlKey, boolean shiftKey,
			     boolean altKey) {
	super.mouseDragged(x, y, ctrlKey, shiftKey, altKey);
    }
    
    public void prepareToDrag() {
	if (!_floating) {
	    _bitmap = _bitmap.createTransparentBitmap();
	    COM.stagecast.ifc.netscape.application.Bitmap paintLayer
		= paintField.getCurrentState().paintLayer;
	    Graphics clearGraphics = paintLayer.createGraphics();
	    clearGraphics.setColor(TransparentGraphics.T_COLOR);
	    if (_spans == null)
		clearGraphics.fillRect(rect);
	    else
		paintSpans(clearGraphics, _spans);
	    clearGraphics.dispose();
	    _floating = true;
	}
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	super.mouseUp(x, y, ctrlKey, shiftKey, altKey);
	_dirtyRect.unionWith(rect);
	paintField.logicalDraw(_dirtyRect, 2);
    }
    
    public void keyDown(KeyEvent event) {
	switch (event.key) {
	case 1004:
	case 1005:
	case 1006:
	case 1007:
	    paintField.recordStateForUndo();
	    /* fall through */
	default:
	    super.keyDown(event);
	}
    }
    
    public void draw(PaintField p, Graphics g) {
	_bitmap.drawScaled(g, paintField.logicalToPhysical(new Rect(rect)));
	super.draw(p, g);
    }
    
    public void delete() {
	prepareToDrag();
	paintField.deleteCurrentSelection();
    }
    
    public void deselect() {
	paintField.deselect();
    }
    
    public void flipHorizontal() {
	prepareToDrag();
	paintField.recordStateForUndo();
	paintField.waitCursor(true);
	int width = _bitmap.width();
	int height = _bitmap.height();
	int[] pixels = new int[width * height];
	boolean success
	    = _bitmap.grabPixels(pixels, 0, 0, width, height, 0, width);
	ASSERT.isTrue(success, "grabPixels");
	for (int x = 0; x < width / 2; x++) {
	    for (int y = 0; y < height; y++) {
		int curHeight = y * width;
		int pixel = pixels[curHeight + x];
		pixels[curHeight + x] = pixels[curHeight + (width - x) - 1];
		pixels[curHeight + (width - x) - 1] = pixel;
	    }
	}
	_bitmap
	    = new TransparentBitmap(pixels, _bitmap.width(), _bitmap.height());
	paintField.logicalDraw(paintField.logicalVisibleRect(), 0);
	paintField.waitCursor(false);
    }
    
    public void flipVertical() {
	prepareToDrag();
	paintField.recordStateForUndo();
	paintField.waitCursor(true);
	int width = _bitmap.width();
	int height = _bitmap.height();
	int[] pixels = new int[width * height];
	boolean success
	    = _bitmap.grabPixels(pixels, 0, 0, width, height, 0, width);
	ASSERT.isTrue(success, "grabPixels");
	int count = 0;
	for (int y = 1; y <= height / 2; y++) {
	    int curHeight = (height - y) * width;
	    for (int x = 0; x < width; x++) {
		int pixel = pixels[curHeight + x];
		pixels[curHeight + x] = pixels[count];
		pixels[count] = pixel;
		count++;
	    }
	}
	_bitmap
	    = new TransparentBitmap(pixels, _bitmap.width(), _bitmap.height());
	paintField.logicalDraw(paintField.logicalVisibleRect(), 0);
	paintField.waitCursor(false);
    }
    
    public void rotate(boolean clockwise) {
	prepareToDrag();
	paintField.recordStateForUndo();
	paintField.waitCursor(true);
	int width = _bitmap.width();
	int height = _bitmap.height();
	_dirtyRect.setBounds(rect);
	int[] oldPixels = PaintField.getPixelArray(width * height);
	int[] pixels = new int[width * height];
	boolean success
	    = _bitmap.grabPixels(oldPixels, 0, 0, width, height, 0, width);
	ASSERT.isTrue(success, "grabPixels");
	int count = 0;
	if (clockwise) {
	    for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
		    pixels[height - y - 1 + x * height] = oldPixels[count];
		    count++;
		}
	    }
	} else {
	    for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
		    pixels[count] = oldPixels[height - y + 1 + x * height];
		    count++;
		}
	    }
	}
	int oldWidth = rect.width;
	rect.width = rect.height;
	rect.height = oldWidth;
	_bitmap
	    = new TransparentBitmap(pixels, _bitmap.height(), _bitmap.width());
	_dirtyRect.unionWith(rect);
	paintField.logicalDraw(_dirtyRect, 2);
	paintField.waitCursor(false);
    }
    
    public boolean isBlank() {
	prepareToDrag();
	boolean isBlank = true;
	if (_bitmap != null) {
	    int[] pixels = new int[_bitmap.width() * _bitmap.height()];
	    boolean success = _bitmap.grabPixels(pixels);
	    ASSERT.isTrue(success, "grabPixels");
	    for (int i = 0; i < pixels.length; i++) {
		if ((pixels[i] & ~0xffffff) != 0) {
		    isBlank = false;
		    break;
		}
	    }
	}
	return isBlank;
    }
    
    private void paintSpans(Graphics g, Vector spans) {
	for (int i = 0; i < spans.size(); i++) {
	    PolygonSpan s = (PolygonSpan) spans.elementAt(i);
	    g.fillRect(s.leftX, s.y, s.rightX - s.leftX + 1, 1);
	}
    }
    
    private void paintInverseSpans(Graphics g, Rect rect, Vector spans) {
	if (spans.size() > 0) {
	    PolygonSpan s = (PolygonSpan) spans.firstElement();
	    if (s != null && s.y > rect.y)
		g.fillRect(0, 0, rect.x + rect.width, s.y);
	    s = (PolygonSpan) spans.lastElement();
	    if (s != null && rect.y + rect.height - s.y > 0)
		g.fillRect(0, s.y + 1, rect.x + rect.width,
			   rect.y + rect.height - s.y);
	    for (int i = 0; i < spans.size(); i++) {
		s = (PolygonSpan) spans.elementAt(i);
		g.fillRect(0, s.y, s.leftX, 1);
		g.fillRect(s.rightX + 1, s.y, rect.x + rect.width - s.rightX,
			   1);
	    }
	} else
	    g.fillRect(0, 0, rect.x + rect.width, rect.y + rect.height);
    }
}
