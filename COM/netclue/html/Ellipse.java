/* Ellipse - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.awt.Rectangle;
import java.awt.Shape;

public class Ellipse implements Shape
{
    private int cx;
    private int cy;
    private int cr;
    private int r2;
    private Rectangle bounds;
    
    public Ellipse(int i, int i_0_, int i_1_) {
	cx = i;
	cy = i_0_;
	cr = i_1_;
	r2 = i_1_ * i_1_;
	bounds = new Rectangle(i - i_1_, i_0_ - i_1_, i + i_1_, i_0_ + i_1_);
    }
    
    public Rectangle getBounds() {
	return bounds;
    }
    
    public boolean contains(int i, int i_2_) {
	int i_3_ = cx - i;
	int i_4_ = cy - i_2_;
	if (i_3_ * i_3_ + i_4_ * i_4_ <= r2)
	    return true;
	return false;
    }
}
