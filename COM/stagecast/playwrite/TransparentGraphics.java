/* TransparentGraphics - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Polygon;
import COM.stagecast.ifc.netscape.application.Rect;

public class TransparentGraphics extends Graphics
{
    public static final Color T_COLOR = new Color(255, 255, 255);
    public static Color TRANSPARENT = Color.black;
    public static Color OPAQUE = Color.white;
    public static int TRANSPARENT_INT = -16777216;
    public static int OPAQUE_INT = -1;
    private Bitmap _transparencyCache;
    private Graphics _tg;
    
    public TransparentGraphics(TransparentBitmap bitmap) {
	super((Bitmap) bitmap);
	_transparencyCache = bitmap.getTransparencyMap();
    }
    
    private Graphics getTransparentGraphics() {
	if (_tg == null)
	    _tg = _transparencyCache.createGraphics();
	return _tg;
    }
    
    public void clearClipRect() {
	super.clearClipRect();
	getTransparentGraphics().clearClipRect();
    }
    
    public void dispose() {
	super.dispose();
	if (_tg != null)
	    _tg.dispose();
    }
    
    public void drawArc(Rect r, int x, int y) {
	super.drawArc(r, x, y);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawArc(r, x, y);
    }
    
    public void drawArc(int x, int y, int w, int h, int x1, int y1) {
	super.drawArc(x, y, w, h, x1, y1);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawArc(x, y, w, h, x1, y1);
    }
    
    public void drawBitmapAt(Bitmap b, int x, int y) {
	super.drawBitmapAt(b, x, y);
	if (b instanceof TransparentBitmap) {
	    Graphics g = getTransparentGraphics();
	    g.drawBitmapAt(((TransparentBitmap) b).getTransparencyMap(), x, y);
	}
    }
    
    public void drawBitmapScaled(Bitmap b, int x, int y, int w, int h) {
	super.drawBitmapScaled(b, x, y, w, h);
	if (b instanceof TransparentBitmap) {
	    Graphics g = getTransparentGraphics();
	    g.drawBitmapScaled(((TransparentBitmap) b).getTransparencyMap(), x,
			       y, w, h);
	}
    }
    
    public void drawBytes(byte[] bytes, int x, int y, int w, int h) {
	super.drawBytes(bytes, x, y, w, h);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawBytes(bytes, x, y, w, h);
    }
    
    public void drawChars(char[] chars, int x, int y, int w, int h) {
	super.drawChars(chars, x, y, w, h);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawChars(chars, x, y, w, h);
    }
    
    public void drawLine(int x1, int y1, int x2, int y2) {
	super.drawLine(x1, y1, x2, y2);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawLine(x1, y1, x2, y2);
    }
    
    public void drawOval(int x, int y, int w, int h) {
	super.drawOval(x, y, w, h);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawOval(x, y, w, h);
    }
    
    public void drawPoint(int x, int y) {
	super.drawPoint(x, y);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawPoint(x, y);
    }
    
    public void drawPolygon(int[] xs, int[] ys, int n) {
	super.drawPolygon(xs, ys, n);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawPolygon(xs, ys, n);
    }
    
    public void drawPolygon(Polygon poly) {
	super.drawPolygon(poly);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawPolygon(poly);
    }
    
    public void drawRect(int x, int y, int w, int h) {
	super.drawRect(x, y, w, h);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawRect(x, y, w, h);
    }
    
    public void drawRoundedRect(int x, int y, int width, int height,
				int arcWidth, int arcHeight) {
	super.drawRoundedRect(x, y, width, height, arcWidth, arcHeight);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawRoundedRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    public void drawString(String s, int x, int y) {
	super.drawString(s, x, y);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawString(s, x, y);
    }
    
    public void drawStringInRect(String s, int x, int y, int w, int h,
				 int justification) {
	super.drawStringInRect(s, x, y, w, h, justification);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.drawStringInRect(s, x, y, w, h, justification);
    }
    
    public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
	super.fillArc(x, y, width, height, startAngle, arcAngle);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.fillArc(x, y, width, height, startAngle, arcAngle);
    }
    
    public void fillOval(int x, int y, int w, int h) {
	super.fillOval(x, y, w, h);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.fillOval(x, y, w, h);
    }
    
    public void fillPolygon(int[] xs, int[] ys, int n) {
	super.fillPolygon(xs, ys, n);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.fillPolygon(xs, ys, n);
    }
    
    public void fillRect(int x, int y, int w, int h) {
	super.fillRect(x, y, w, h);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.fillRect(x, y, w, h);
    }
    
    public void fillRoundedRect(int x, int y, int width, int height,
				int arcWidth, int arcHeight) {
	super.fillRoundedRect(x, y, width, height, arcWidth, arcHeight);
	Graphics g = getTransparentGraphics();
	g.setColor(this.color() == T_COLOR ? TRANSPARENT : OPAQUE);
	g.fillRoundedRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    public void popState() {
	super.popState();
	Graphics g = getTransparentGraphics();
	g.popState();
    }
    
    public void pushState() {
	super.pushState();
	Graphics g = getTransparentGraphics();
	g.pushState();
    }
    
    public void setClipRect(Rect r, boolean b) {
	super.setClipRect(r, b);
	Graphics g = getTransparentGraphics();
	g.setClipRect(r, b);
    }
    
    public void setClipRect(Rect r) {
	super.setClipRect(r);
	Graphics g = getTransparentGraphics();
	g.setClipRect(r);
    }
    
    public void setFont(Font f) {
	super.setFont(f);
	Graphics g = getTransparentGraphics();
	g.setFont(f);
    }
    
    public void sync() {
	super.sync();
	Graphics g = getTransparentGraphics();
	g.sync();
    }
    
    public String toString() {
	return ("TransparentGraphics: " + this + " transparency map: "
		+ getTransparentGraphics());
    }
    
    public void translate(int x, int y) {
	super.translate(x, y);
	Graphics g = getTransparentGraphics();
	g.translate(x, y);
    }
}
