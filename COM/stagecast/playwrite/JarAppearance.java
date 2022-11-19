/* JarAppearance - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.util.Hashtable;

class JarAppearance extends Appearance implements Externalizable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108756657458L;
    static final Color CAP_COLOR = Color.orange;
    static final Color JAR_COLOR = Color.white;
    static final Color OUTLINE_COLOR = Color.black;
    
    private JarAppearance(Appearance app, CharacterPrototype theOwner) {
	super(app.getName(), createJarBitmap(app), app.getSquareSize(),
	      (Shape) app.getShape().clone());
	this.setOwner(theOwner);
    }
    
    JarAppearance(Appearance app) {
	this(app, app.getOwner());
    }
    
    public JarAppearance() {
	/* empty */
    }
    
    private static Bitmap createJarBitmap(Appearance appearance) {
	Bitmap originalBitmap = appearance.getBitmap();
	int w = appearance.getLogicalWidth() * appearance.getSquareSize();
	int h = appearance.getLogicalHeight() * appearance.getSquareSize();
	Bitmap jarBitmap = Util.createBlankBitmap(w, h);
	Graphics g = jarBitmap.createGraphics();
	originalBitmap.drawAt(g, 0, 0);
	int size = 4;
	for (int i = 0; i < size; i++) {
	    if (i == size - 1 || i == 0)
		g.setColor(Color.black);
	    else
		g.setColor(JAR_COLOR);
	    g.drawRoundedRect(i, i, w - 2 * i, h - 2 * i, 10, 10);
	}
	g.setColor(CAP_COLOR);
	g.fillRect(0, 0, w, h / 7);
	g.setColor(OUTLINE_COLOR);
	g.drawRect(0, 0, w, h / 7);
	g.dispose();
	return Util.createTransparentBitmap(jarBitmap);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	JarAppearance newAppearance = (JarAppearance) map.get(this);
	if (newAppearance != null) {
	    if (newAppearance.isProxy() && fullCopy)
		newAppearance.makeReal(this, map);
	    return newAppearance;
	}
	newAppearance = new JarAppearance(this, this.findNewOwner(map));
	map.put(this, newAppearance);
	return newAppearance;
    }
    
    int getWidthAtSquareSize(int targetSquareSize) {
	return this.getLogicalWidth() * targetSquareSize;
    }
    
    int getHeightAtSquareSize(int targetSquareSize) {
	return this.getLogicalHeight() * targetSquareSize;
    }
    
    boolean isLegalFor(CocoaCharacter ch) {
	return ch instanceof GeneralizedCharacter || ch instanceof GCAlias;
    }
}
