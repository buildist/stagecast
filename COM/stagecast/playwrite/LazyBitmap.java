/* LazyBitmap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Image;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Rect;

public class LazyBitmap extends Bitmap
{
    private Bitmap _realBitmap;
    private BitmapMaker _bitmapMaker;
    private int _width;
    private int _height;
    
    public static interface BitmapMaker
    {
	public Bitmap createBitmap();
    }
    
    LazyBitmap(BitmapMaker bitmapMaker, int width, int height) {
	_bitmapMaker = bitmapMaker;
	_width = width;
	_height = height;
    }
    
    LazyBitmap(BitmapMaker bitmapMaker) {
	this(bitmapMaker, -1, -1);
    }
    
    BitmapMaker getBitmapMaker() {
	return _bitmapMaker;
    }
    
    private final boolean hasRealBitmap() {
	if (_realBitmap == null) {
	    _realBitmap = _bitmapMaker.createBitmap();
	    if (_realBitmap != null) {
		if (_width == -1)
		    _width = _realBitmap.width();
		if (_height == -1)
		    _height = _realBitmap.height();
	    }
	}
	return _realBitmap != null;
    }
    
    public void drawAt(Graphics g, int x, int y) {
	if (hasRealBitmap())
	    _realBitmap.drawAt(g, x, y);
    }
    
    public void drawCentered(Graphics g, int x, int y, int width, int height) {
	if (hasRealBitmap())
	    _realBitmap.drawCentered(g, x, y, width, height);
    }
    
    public void drawCentered(Graphics g, Rect rect) {
	if (hasRealBitmap())
	    _realBitmap.drawCentered(g, rect);
    }
    
    public void drawScaled(Graphics g, int x, int y, int width, int height) {
	if (hasRealBitmap())
	    _realBitmap.drawScaled(g, x, y, width, height);
    }
    
    public void drawTiled(Graphics g, int x, int y, int width, int height) {
	if (hasRealBitmap())
	    _realBitmap.drawTiled(g, x, y, width, height);
    }
    
    public void drawTiled(Graphics g, Rect rect) {
	if (hasRealBitmap())
	    _realBitmap.drawTiled(g, rect);
    }
    
    public void drawWithStyle(Graphics g, int x, int y, int width, int height,
			      int style) {
	if (hasRealBitmap())
	    _realBitmap.drawWithStyle(g, x, y, width, height, style);
    }
    
    public void drawWithStyle(Graphics g, Rect rect, int style) {
	if (hasRealBitmap())
	    _realBitmap.drawWithStyle(g, rect, style);
    }
    
    public int height() {
	if (_height == -1)
	    hasRealBitmap();
	if (_realBitmap != null)
	    return _realBitmap.height();
	return _height;
    }
    
    public int width() {
	if (_width == -1)
	    hasRealBitmap();
	if (_realBitmap != null)
	    return _realBitmap.width();
	return _width;
    }
    
    public boolean grabPixels(int[] pixels, int x, int y, int width,
			      int height, int offset, int scanSize) {
	if (!hasRealBitmap())
	    return false;
	return _realBitmap.grabPixels(pixels, x, y, width, height, offset,
				      scanSize);
    }
    
    public Image awtImage() {
	if (!hasRealBitmap())
	    return null;
	return _realBitmap.awtImage();
    }
    
    public void flush() {
	if (_realBitmap != null) {
	    _realBitmap.flush();
	    _realBitmap = null;
	}
    }
}
