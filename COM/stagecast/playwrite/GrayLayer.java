/* GrayLayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;

class GrayLayer extends PlaywriteView
{
    public static final Bitmap grayTransparentBitmap;
    public static final int DISABLE_ALPHA = 140;
    public static final int DISABLE_GRAY = 76;
    
    static {
	int[] pixels = new int[4096];
	for (int i = 0; i < pixels.length; i++)
	    pixels[i] = -1941156788;
	grayTransparentBitmap
	    = BitmapManager.createBitmapManager(pixels, 64, 64, 0, 64);
    }
    
    GrayLayer(int width, int height) {
	super(0, 0, width, height);
	this.setTransparent(true);
	this.setBorder(null);
	this.setImageDisplayStyle(2);
	this.setImage(grayTransparentBitmap);
    }
}
