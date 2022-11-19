/* LightTextField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;

public class LightTextField extends PlaywriteTextField
{
    private static final Color textColor = Color.white;
    private static final Color editableTextColor = Color.black;
    private static final Font italicFont
	= Font.fontNamed(Util.valueFont.name(), 2, Util.valueFont.size());
    
    public LightTextField(int x, int y, int width, int height) {
	super(x, y, width, height);
	this.setEditIndication(true);
	this.setTransparent(true);
	this.setFont(italicFont);
	this.setJustification(1);
	this.setUserEditable(false);
	this.setDraggable(false);
	this.setDrawsDropShadow(true);
	this.setDropShadowOffset(1);
	this.setTextColor(textColor);
    }
    
    public void startFocus() {
	this.setDrawsDropShadow(false);
	this.setTextColor(editableTextColor);
	super.startFocus();
    }
    
    public void stopFocus() {
	super.stopFocus();
	this.setDrawsDropShadow(true);
	this.setTextColor(textColor);
    }
}
