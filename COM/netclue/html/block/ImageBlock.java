/* ImageBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;

public class ImageBlock extends Block implements Rigid, Runnable
{
    private ImageComponent ic;
    boolean isMarked;
    
    public ImageBlock(AbstractElement abstractelement) {
	super(abstractelement);
	ic = new ImageComponent(abstractelement, this);
	ic.setVisible(false);
    }
    
    protected void finalize() {
	if (ic != null) {
	    ic.getParent().remove(ic);
	    ic = null;
	}
    }
    
    public void setSize(int i, int i_0_) {
	/* empty */
    }
    
    public void setParent(Block block) {
	super.setParent(block);
	if (block == null) {
	    if (ic.getParent() != null)
		ic.getParent().remove(ic);
	} else {
	    Container container = this.getContainer();
	    container.add(ic);
	}
    }
    
    public void setStatus(boolean bool) {
	isMarked = bool;
    }
    
    public void paint(Graphics graphics, Shape shape) {
	Rectangle rectangle = shape.getBounds();
	ic.setBounds(rectangle);
	if (!ic.isVisible())
	    ic.setVisible(true);
	if (isMarked) {
	    graphics.setColor(Color.red);
	    graphics.drawRect(rectangle.x - 1, rectangle.y - 1,
			      rectangle.width + 1, rectangle.height + 1);
	}
    }
    
    public int getPreferredSize(int i) {
	return ic.getPreferredSize(i);
    }
    
    public float getAlignment(int i) {
	switch (i) {
	case 1:
	    return ic.getVerticalAlignment();
	default:
	    return super.getAlignment(i);
	}
    }
    
    public int getBreakWeight(int i, float f, float f_1_) {
	if (f_1_ >= (float) getPreferredSize(i))
	    return 2000;
	return 0;
    }
    
    public int getFlushAlign() {
	return ic.isFlushAlign();
    }
    
    public int getWidthRatio() {
	return 0;
    }
    
    public Rectangle findBounds(int i, Rectangle rectangle) {
	int i_2_ = this.getStartIndex();
	int i_3_ = this.getEndIndex();
	if (i >= i_2_ && i < i_3_) {
	    int i_4_ = ic.getSpace(0);
	    int i_5_ = ic.getSpace(1);
	    rectangle.x += i_4_;
	    rectangle.y += i_5_;
	    rectangle.width -= i_4_ << 1;
	    rectangle.height -= i_5_ << 1;
	    return rectangle;
	}
	return null;
    }
    
    public int getDocIndex(int i, int i_6_, Shape shape) {
	return this.getStartIndex();
    }
    
    public void run() {
	this.sizeChanged(true, true);
    }
}
