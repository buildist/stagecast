/* BRBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;

public class BRBlock extends Block
{
    int ySize;
    
    public BRBlock(AbstractElement abstractelement) {
	super(abstractelement);
    }
    
    public void setSize(int i, int i_0_) {
	ySize = i_0_;
    }
    
    public int getPreferredSize(int i) {
	if (i == 0)
	    return 0;
	return ySize;
    }
    
    public void paint(Graphics graphics, Shape shape) {
	/* empty */
    }
    
    public Rectangle findBounds(int i, Rectangle rectangle) {
	return null;
    }
    
    public int getDocIndex(int i, int i_1_, Shape shape) {
	return 0;
    }
}
