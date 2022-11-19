/* ScreenBufferManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Size;

class ScreenBufferManager
{
    private Bitmap screenBuffer = BitmapManager.createBitmapManager(1, 1);
    private Size size = new Size(0, 0);
    
    public void sizeTo(int w, int h) {
	if (w < 1)
	    w = 1;
	if (h < 1)
	    h = 1;
	size.sizeTo(w, h);
    }
    
    public Graphics createGraphics() {
	if (size.width != screenBuffer.width()
	    || size.height != screenBuffer.height())
	    adjustDrawingBuffer();
	return screenBuffer.createGraphics();
    }
    
    public final boolean adjustDrawingBuffer() {
	if (size.width > screenBuffer.width()
	    || size.height > screenBuffer.height()) {
	    screenBuffer.flush();
	    screenBuffer = null;
	    screenBuffer
		= BitmapManager.createBitmapManager(size.width, size.height);
	    return true;
	}
	return false;
    }
    
    public void drawAt(Graphics g, int x, int y) {
	screenBuffer.drawAt(g, x, y);
    }
    
    public void flush() {
	screenBuffer.flush();
    }
}
