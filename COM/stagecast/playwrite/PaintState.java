/* PaintState - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Size;

public class PaintState
{
    protected TransparentBitmap paintLayer;
    
    public PaintState(Size s) {
	paintLayer = new TransparentBitmap(s.width, s.height);
    }
    
    public PaintState(PaintState copy) {
	copyFrom(copy);
    }
    
    public Graphics createGraphics() {
	return paintLayer.createGraphics();
    }
    
    public int width() {
	return paintLayer.width();
    }
    
    public int height() {
	return paintLayer.height();
    }
    
    public boolean grabPixels(int[] pixels, int x, int y, int width,
			      int height, int offset, int scanSize) {
	return paintLayer.grabPixels(pixels, x, y, width, height, offset,
				     scanSize);
    }
    
    public boolean grabPixels(int[] pixels) {
	return paintLayer.grabPixels(pixels);
    }
    
    public void drawBitmap(Bitmap b) {
	Graphics g = paintLayer.createGraphics();
	b.drawAt(g, 0, 0);
	paintLayer.initTransparencyMap(b);
	g.dispose();
    }
    
    public void copyFrom(PaintState copy) {
	if (paintLayer == null || copy.paintLayer.width() != paintLayer.width()
	    || copy.paintLayer.height() != paintLayer.height()) {
	    if (paintLayer != null)
		paintLayer.flush();
	    paintLayer = new TransparentBitmap(copy.paintLayer.width(),
					       copy.paintLayer.height());
	}
	Graphics g = paintLayer.createGraphics();
	copy.paintLayer.drawAt(g, 0, 0);
	g.dispose();
	g = paintLayer.getTransparencyMap().createGraphics();
	copy.paintLayer.getTransparencyMap().drawAt(g, 0, 0);
	g.dispose();
    }
    
    public void flush() {
	if (paintLayer != null)
	    paintLayer.flush();
    }
}
