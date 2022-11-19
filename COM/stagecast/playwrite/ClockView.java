/* ClockView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class ClockView extends PlaywriteView implements ResourceIDs.ControlPanelIDs
{
    static final int NUMBEROFHOURS = 9;
    static Bitmap[] timeImages = null;
    private World world;
    private boolean isDisabled = false;
    
    ClockView(int x, int y, World w) {
	super(x, y, 32, 32);
	world = w;
	checkImageCache();
	this.setImage(timeImages[0]);
	this.setMinSize(this.image().width(), this.image().height());
	this.sizeToMinSize();
	this.setBorder(null);
	this.setTransparent(true);
	w.setClockView(this);
	this.setBuffered(true);
    }
    
    private static synchronized void checkImageCache() {
	if (timeImages == null) {
	    timeImages = new Bitmap[9];
	    for (int i = 0; i < timeImages.length; i++)
		timeImages[i]
		    = Resource.getImage("CP clock img",
					new Object[] { new Integer(i) });
	}
    }
    
    void drawTick() {
	this.setDirty(true);
	if (this.superview() != null)
	    this.superview().addDirtyRect(this.bounds());
    }
    
    public void disable() {
	isDisabled = true;
	this.setDirty(true);
    }
    
    public void enable() {
	isDisabled = false;
	this.setDirty(true);
    }
    
    public void drawView(Graphics g) {
	timeImages[world.getTime() % 9].drawAt(g, 0, 0);
	if (isDisabled) {
	    g.setClipRect(new Rect(0, 0, this.width(), this.height()));
	    GrayLayer.grayTransparentBitmap.drawAt(g, 0, 0);
	}
    }
    
    public void discard() {
	super.discard();
	world = null;
    }
}
