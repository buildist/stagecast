/* BitmapObserver - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.image.ImageObserver;

class BitmapObserver implements ImageObserver
{
    Application application;
    Bitmap bitmap;
    int lastInfo;
    
    BitmapObserver(Application application, Bitmap bitmap) {
	this.application = application;
	this.bitmap = bitmap;
    }
    
    public synchronized boolean imageUpdate
	(java.awt.Image image, int i, int i_0_, int i_1_, int i_2_, int i_3_) {
	lastInfo = i;
	if (image == null)
	    return true;
	if ((i & 0x1) != 0 || (i & 0x2) != 0 || (i & 0x4) != 0
	    || (i & 0x20) != 0 || (i & 0x40) != 0 || (i & 0x80) != 0) {
	    this.notifyAll();
	    return true;
	}
	if (!bitmap.loadsIncrementally())
	    return true;
	if ((i & 0x8) != 0) {
	    bitmap.unionWithUpdateRect(i_0_, i_1_, i_2_, i_3_);
	    Target target = bitmap.updateTarget();
	    if (target != null && application != null)
		application.performCommandLater(target, bitmap.updateCommand(),
						bitmap, true);
	} else if ((i & 0x10) != 0) {
	    Target target = bitmap.updateTarget();
	    if (target != null && application != null)
		application.performCommandLater(target, bitmap.updateCommand(),
						bitmap, true);
	}
	return true;
    }
    
    synchronized boolean allBitsPresent() {
	return (lastInfo & 0x20) != 0;
    }
    
    synchronized boolean imageHasProblem() {
	return (lastInfo & 0x40) != 0 || (lastInfo & 0x80) != 0;
    }
}
