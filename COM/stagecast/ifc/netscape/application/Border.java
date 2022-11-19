/* Border - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public abstract class Border
{
    public abstract int leftMargin();
    
    public abstract int rightMargin();
    
    public abstract int topMargin();
    
    public abstract int bottomMargin();
    
    public abstract void drawInRect(Graphics graphics, int i, int i_0_,
				    int i_1_, int i_2_);
    
    public void drawInRect(Graphics graphics, Rect rect) {
	drawInRect(graphics, rect.x, rect.y, rect.width, rect.height);
    }
    
    public int widthMargin() {
	return leftMargin() + rightMargin();
    }
    
    public int heightMargin() {
	return topMargin() + bottomMargin();
    }
    
    public void computeInteriorRect(int i, int i_3_, int i_4_, int i_5_,
				    Rect rect) {
	int i_6_ = leftMargin();
	int i_7_ = topMargin();
	rect.setBounds(i + i_6_, i_3_ + i_7_, i_4_ - i_6_ - rightMargin(),
		       i_5_ - i_7_ - bottomMargin());
    }
    
    public void computeInteriorRect(Rect rect, Rect rect_8_) {
	int i = leftMargin();
	int i_9_ = topMargin();
	rect_8_.setBounds(rect.x + i, rect.y + i_9_,
			  rect.width - i - rightMargin(),
			  rect.height - i_9_ - bottomMargin());
    }
    
    public Rect interiorRect(int i, int i_10_, int i_11_, int i_12_) {
	int i_13_ = leftMargin();
	int i_14_ = topMargin();
	return new Rect(i + i_13_, i_10_ + i_14_,
			i_11_ - i_13_ - rightMargin(),
			i_12_ - i_14_ - bottomMargin());
    }
    
    public Rect interiorRect(Rect rect) {
	int i = leftMargin();
	int i_15_ = topMargin();
	return new Rect(rect.x + i, rect.y + i_15_,
			rect.width - i - rightMargin(),
			rect.height - i_15_ - bottomMargin());
    }
}
