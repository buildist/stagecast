/* AnythingAppearance - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.util.Hashtable;

class AnythingAppearance extends Appearance implements Externalizable
{
    static final String NAME = "anything appearance";
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108756722994L;
    private Appearance baseAppearance;
    
    AnythingAppearance(Appearance app) {
	super("anything appearance", Resource.getImage("GraySplat"),
	      Resource.getImage("GraySplat").width(),
	      (Shape) app.getShape().clone());
	baseAppearance = app;
	this.setOwner(app.getOwner());
    }
    
    public AnythingAppearance() {
	/* empty */
    }
    
    boolean isLegalFor(CocoaCharacter ch) {
	return ch instanceof GeneralizedCharacter || ch instanceof GCAlias;
    }
    
    void draw(CharacterView characterView, Graphics g, int x, int y,
	      int targetSquareSize) {
	int maxX = this.getLogicalWidth();
	int maxY = this.getLogicalHeight();
	int width = baseAppearance.getLogicalWidth() * targetSquareSize;
	int height = baseAppearance.getLogicalHeight() * targetSquareSize;
	for (int ax = 0; ax < maxX; ax++) {
	    for (int ay = 0; ay < maxY; ay++) {
		if (this.getLocationHV(ax + 1, this.getLogicalHeight() - ay)) {
		    g.pushState();
		    g.setClipRect(new Rect(x + targetSquareSize * ax,
					   y + targetSquareSize * ay,
					   targetSquareSize, targetSquareSize),
				  true);
		    Resource.getImage("GraySplat").drawScaled(g, x, y, width,
							      height);
		    g.popState();
		}
	    }
	}
	if (characterView != null && characterView.isHilited())
	    Util.drawHilited(g, new Rect(x, y, width, height));
    }
    
    int getWidthAtSquareSize(int targetSquareSize) {
	return baseAppearance.getLogicalWidth() * targetSquareSize;
    }
    
    int getHeightAtSquareSize(int targetSquareSize) {
	return baseAppearance.getLogicalHeight() * targetSquareSize;
    }
    
    public Bitmap getBitmapAtSquareSize(int squareSize) {
	Size size = new Size(this.getSizeForSquareSize(squareSize));
	Bitmap bitmap = this.getCachedBitmap(size);
	if (bitmap == null) {
	    bitmap = Util.createBlankBitmap(size.width, size.height);
	    draw(null, bitmap.createGraphics(), 0, 0, squareSize);
	    bitmap = Util.createTransparentBitmap(bitmap);
	    this.cacheBitmap(size, bitmap);
	}
	return bitmap;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeObject(baseAppearance);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	baseAppearance = (Appearance) in.readObject();
	this.fillInObject("anything appearance",
			  Resource.getImage("GraySplat"),
			  (Shape) baseAppearance.getShape().clone());
	this.setOwner(baseAppearance.getOwner());
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	AnythingAppearance newAppearance = (AnythingAppearance) map.get(this);
	if (newAppearance != null) {
	    if (newAppearance.isProxy() && fullCopy)
		newAppearance.makeReal(this, map);
	    return newAppearance;
	}
	newAppearance
	    = new AnythingAppearance((Appearance)
				     baseAppearance.copy(map, fullCopy));
	map.put(this, newAppearance);
	return newAppearance;
    }
}
