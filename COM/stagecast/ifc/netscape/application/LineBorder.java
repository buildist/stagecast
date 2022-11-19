/* LineBorder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class LineBorder extends Border
{
    private static Border blackLine;
    private static Border grayLine;
    private Color color;
    
    public static Border blackLine() {
	if (blackLine == null)
	    blackLine = new LineBorder(Color.black);
	return blackLine;
    }
    
    public static Border grayLine() {
	if (grayLine == null)
	    grayLine = new LineBorder(Color.gray);
	return grayLine;
    }
    
    public LineBorder() {
	/* empty */
    }
    
    public LineBorder(Color color) {
	this();
	this.color = color;
    }
    
    public void setColor(Color color) {
	this.color = color;
    }
    
    public Color color() {
	return color;
    }
    
    public int leftMargin() {
	return 1;
    }
    
    public int rightMargin() {
	return 1;
    }
    
    public int topMargin() {
	return 1;
    }
    
    public int bottomMargin() {
	return 1;
    }
    
    public void drawInRect(Graphics graphics, int i, int i_0_, int i_1_,
			   int i_2_) {
	graphics.setColor(color);
	graphics.drawRect(i, i_0_, i_1_, i_2_);
    }
}
