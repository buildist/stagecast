/* TransparentBitmap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.image.RGBImageFilter;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Rect;

public class TransparentBitmap extends BitmapManager
{
    private Bitmap _transparencyMap = null;
    
    public static class OpaqueFilter extends RGBImageFilter
    {
	public OpaqueFilter() {
	    canFilterIndexColorModel = true;
	}
	
	public int filterRGB(int x, int y, int rgb) {
	    int a = rgb >> 24 & 0xff;
	    if (a == 0)
		return a;
	    return TransparentGraphics.OPAQUE_INT;
	}
    }
    
    public TransparentBitmap(int width, int height) {
	super(new Bitmap(width, height));
	_transparencyMap = new Bitmap(width, height);
    }
    
    private TransparentBitmap(TransparentBitmap template) {
	super(new Bitmap(template.width(), template.height()));
    }
    
    public TransparentBitmap(int[] pixels, int width, int height) {
	super(new Bitmap(pixels, width, height));
    }
    
    public Bitmap getTransparencyMap() {
	if (_transparencyMap == null)
	    _transparencyMap = new Bitmap(this.width(), this.height());
	return _transparencyMap;
    }
    
    public Graphics createGraphics() {
	return new TransparentGraphics(this);
    }
    
    public void initTransparencyMap(Bitmap b) {
	int[] pixels = new int[b.width() * b.height()];
	b.grabPixels(pixels);
	for (int i = 0; i < pixels.length; i++) {
	    if ((pixels[i] & ~0xffffff) == 0)
		pixels[i] = TransparentGraphics.TRANSPARENT_INT;
	    else
		pixels[i] = TransparentGraphics.OPAQUE_INT;
	}
	Bitmap bn
	    = BitmapManager.createBitmapManager(pixels, b.width(), b.height());
	Graphics g = getTransparencyMap().createGraphics();
	g.drawBitmapAt(bn, 0, 0);
    }
    
    public void addTransparencyFromBitmap(Bitmap b, Rect r) {
	java.awt.image.ImageFilter f = new OpaqueFilter();
	Bitmap filtered = BitmapManager.createFilteredBitmapManager(b, f);
	Graphics g = getTransparencyMap().createGraphics();
	if (r == null)
	    filtered.drawAt(g, 0, 0);
	else
	    filtered.drawScaled(g, r);
	g.dispose();
    }
    
    public TransparentBitmap createTransparentBitmap() {
	int[] tmPix = new int[this.width() * this.height()];
	int[] resultPix = new int[this.width() * this.height()];
	getTransparencyMap().grabPixels(tmPix);
	this.grabPixels(resultPix);
	for (int i = 0; i < tmPix.length; i++) {
	    if (tmPix[i] == -16777216)
		resultPix[i] = 0;
	}
	TransparentBitmap result
	    = new TransparentBitmap(resultPix, this.width(), this.height());
	result._transparencyMap = _transparencyMap;
	_transparencyMap = null;
	return result;
    }
}
