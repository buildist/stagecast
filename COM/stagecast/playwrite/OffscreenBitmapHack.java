/* OffscreenBitmapHack - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;

public class OffscreenBitmapHack extends Bitmap
    implements Debug.Constants, PlaywriteSystem.Properties
{
    private static final int GC_THRESHOLD_COUNT
	= PlaywriteSystem
	      .getApplicationPropertyAsInt("offscreen_bitmap_threshold", 50);
    private static int _flushedInstanceCount;
    private boolean _isFlushed = false;
    
    static {
	Debug.print("debug.image",
		    ("OffscreenBitmapHack: gc threshold count = "
		     + GC_THRESHOLD_COUNT + "."));
	_flushedInstanceCount = 0;
    }
    
    public OffscreenBitmapHack(int width, int height) {
	super(width, height);
    }
    
    protected void finalize() throws Throwable {
	super.finalize();
	if (_isFlushed == true) {
	    _flushedInstanceCount--;
	    assert(_flushedInstanceCount >= 0);
	}
    }
    
    public synchronized void onCheckOut() {
	if (_isFlushed == true) {
	    _isFlushed = false;
	    _flushedInstanceCount--;
	    assert(_flushedInstanceCount >= 0);
	}
    }
    
    public synchronized void flush() {
	super.flush();
	if (_isFlushed == false) {
	    _isFlushed = true;
	    _flushedInstanceCount++;
	    if (_flushedInstanceCount > GC_THRESHOLD_COUNT) {
		Util.suggestGC();
		Debug.print("debug.image", '#');
	    }
	}
    }
    
    private void assert(boolean condition) {
	if (condition == false)
	    new Throwable("******** ASSERT FAILIURE *********")
		.printStackTrace();
    }
}
