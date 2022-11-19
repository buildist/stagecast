/* StackBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;

public class StackBlock extends StyleBlock
{
    int axis;
    int width;
    int height;
    boolean xValid;
    boolean yValid;
    int[] preferredSpan = new int[2];
    int[] resizeWeight = new int[2];
    float[] alignment = new float[2];
    boolean xAllocValid;
    int[] xOffsets;
    int[] xSpans;
    boolean yAllocValid;
    int[] yOffsets;
    int[] ySpans;
    static Block[] ZERO = new Block[0];
    
    public StackBlock(AbstractElement abstractelement, int i) {
	super(abstractelement);
	axis = i;
	alignment[0] = alignment[1] = 0.5F;
    }
    
    protected void paintChild(Graphics graphics, Rectangle rectangle, int i) {
	Block block = this.getChild(i);
	block.paint(graphics, rectangle);
    }
    
    public void removeLast() {
	replace(this.getChildCount() - 1, 1, ZERO);
    }
    
    public void replace(int i, int i_0_, Block[] blocks) {
	super.replace(i, i_0_, blocks);
	xOffsets = null;
	xSpans = null;
	xValid = false;
	xAllocValid = false;
	yOffsets = null;
	ySpans = null;
	yValid = false;
	yAllocValid = false;
    }
    
    public void sizeChanged(boolean bool, boolean bool_1_) {
	if (bool)
	    xValid = xAllocValid = false;
	if (bool_1_)
	    yValid = yAllocValid = false;
	super.sizeChanged(bool, bool_1_);
    }
    
    public void setSize(int i, int i_2_) {
	if (i != width)
	    xAllocValid = false;
	if (i_2_ != height)
	    yAllocValid = false;
	if (!xAllocValid || !yAllocValid) {
	    width = i;
	    height = i_2_;
	    layout(i - this.getLeftInset() - this.getRightInset(),
		   i_2_ - this.getTopInset() - this.getBottomInset());
	}
    }
    
    public void paint(Graphics graphics, Shape shape) {
	Rectangle rectangle = shape.getBounds();
	setSize(rectangle.width, rectangle.height);
	int i = this.getChildCount();
	int i_3_ = rectangle.x + this.getLeftInset();
	int i_4_ = rectangle.y + this.getTopInset();
	Rectangle rectangle_5_ = graphics.getClipBounds();
	for (int i_6_ = 0; i_6_ < i; i_6_++) {
	    rectangle.x = i_3_ + xOffsets[i_6_];
	    rectangle.y = i_4_ + yOffsets[i_6_];
	    rectangle.width = xSpans[i_6_];
	    rectangle.height = ySpans[i_6_];
	    if (rectangle.intersects(rectangle_5_))
		paintChild(graphics, rectangle, i_6_);
	}
    }
    
    public float getAlignment(int i) {
	return alignment[i];
    }
    
    public boolean isResizable(int i) {
	refreshCache();
	if (resizeWeight[i] <= 0)
	    return false;
	return true;
    }
    
    public int getPreferredSize(int i) {
	refreshCache();
	if (i == 0)
	    return (preferredSpan[i] + this.getLeftInset()
		    + this.getRightInset());
	return preferredSpan[i] + this.getTopInset() + this.getBottomInset();
    }
    
    protected boolean isOutOfBounds(int i, int i_7_, Rectangle rectangle) {
	if (axis == 0) {
	    if (i >= rectangle.x && i <= rectangle.width + rectangle.x)
		return false;
	    return true;
	}
	if (i_7_ >= rectangle.y && i_7_ <= rectangle.height + rectangle.y)
	    return false;
	return true;
    }
    
    protected Block getBlockByPos(int i, int i_8_, Rectangle rectangle) {
	int i_9_ = this.getChildCount();
	if (i_9_ == 0)
	    return null;
	Object object = null;
	if (axis == 0) {
	    int i_10_;
	    for (i_10_ = 1; i_10_ < i_9_; i_10_++) {
		if (i < rectangle.x + xOffsets[i_10_])
		    break;
	    }
	    i_10_--;
	    if (i_8_ < rectangle.y + yOffsets[i_10_]
		|| i_8_ > rectangle.y + yOffsets[i_10_] + ySpans[i_10_])
		return null;
	    Block block = this.getChild(i_10_);
	    childAllocation(i_10_, rectangle);
	    return block;
	}
	int i_11_;
	for (i_11_ = 1; i_11_ < i_9_; i_11_++) {
	    if (i_8_ < rectangle.y + yOffsets[i_11_])
		break;
	}
	Block block = this.getChild(i_11_ - 1);
	childAllocation(i_11_ - 1, rectangle);
	return block;
    }
    
    protected void childAllocation(int i, Rectangle rectangle) {
	rectangle.x += xOffsets[i];
	rectangle.y += yOffsets[i];
	rectangle.width = xSpans[i];
	rectangle.height = ySpans[i];
    }
    
    protected void layout(int i, int i_12_) {
	refreshCache();
	if (xSpans == null) {
	    int i_13_ = this.getChildCount();
	    xSpans = new int[i_13_];
	    ySpans = new int[i_13_];
	    xOffsets = new int[i_13_];
	    yOffsets = new int[i_13_];
	}
	if (axis == 0) {
	    if (!xAllocValid)
		doTiledLayout(i, 0);
	    if (!yAllocValid)
		doAlignedLayout(i_12_, 1);
	} else {
	    if (!xAllocValid)
		doAlignedLayout(i, 0);
	    if (!yAllocValid)
		doTiledLayout(i_12_, 1);
	}
	xAllocValid = true;
	yAllocValid = true;
	int i_14_ = this.getChildCount();
	for (int i_15_ = 0; i_15_ < i_14_; i_15_++) {
	    Block block = this.getChild(i_15_);
	    block.setSize(xSpans[i_15_], ySpans[i_15_]);
	}
    }
    
    public int getWidth() {
	return width;
    }
    
    public int getHeight() {
	return height;
    }
    
    void refreshCache() {
	if (axis == 0) {
	    if (!xValid)
		checkOrthogonal(0);
	    if (!yValid)
		checkParallel(1);
	} else {
	    if (!xValid)
		checkParallel(0);
	    if (!yValid)
		checkOrthogonal(1);
	}
	yValid = true;
	xValid = true;
    }
    
    void checkOrthogonal(int i) {
	preferredSpan[i] = 0;
	resizeWeight[i] = 0;
	int i_16_ = this.getChildCount();
	int i_17_ = 0;
	while (i_17_ < i_16_) {
	    Block block = this.getChild(i_17_++);
	    preferredSpan[i] += block.getPreferredSize(i);
	    int[] is = resizeWeight;
	    int i_18_ = i;
	    is[i_18_] = is[i_18_] + (block.isResizable(i) ? 1 : 0);
	}
    }
    
    void checkParallel(int i) {
	int i_19_ = 0;
	int i_20_ = this.getChildCount();
	for (int i_21_ = 0; i_21_ < i_20_; i_21_++) {
	    Block block = this.getChild(i_21_);
	    int i_22_ = block.getPreferredSize(i);
	    i_19_ = Math.max(i_19_, i_22_);
	    int[] is = resizeWeight;
	    int i_23_ = i;
	    is[i_23_] = is[i_23_] + (block.isResizable(i) ? 1 : 0);
	}
	preferredSpan[i] = i_19_;
    }
    
    void doAlignedLayout(int i, int i_24_) {
	int[] is = i_24_ == 0 ? xOffsets : yOffsets;
	int[] is_25_ = i_24_ == 0 ? xSpans : ySpans;
	int i_26_ = this.getChildCount();
	for (int i_27_ = 0; i_27_ < i_26_; i_27_++) {
	    Block block = this.getChild(i_27_);
	    float f = block.getAlignment(i_24_);
	    int i_28_ = block.getPreferredSize(i_24_);
	    is[i_27_] = 0;
	    if (block.isResizable(i_24_))
		i_28_ = i;
	    else
		is[i_27_] = (int) ((float) (i - i_28_) * f);
	    is_25_[i_27_] = i_28_;
	}
    }
    
    void doTiledLayout(int i, int i_29_) {
	int[] is = i_29_ == 0 ? xOffsets : yOffsets;
	int[] is_30_ = i_29_ == 0 ? xSpans : ySpans;
	int i_31_ = i - preferredSpan[i_29_];
	int i_32_ = resizeWeight[i_29_];
	int i_33_ = 0;
	int i_34_ = this.getChildCount();
	for (int i_35_ = 0; i_35_ < i_34_; i_35_++) {
	    Block block = this.getChild(i_35_);
	    is[i_35_] = i_33_;
	    int i_36_ = block.getPreferredSize(i_29_);
	    int i_37_ = block.isResizable(i_29_) ? 1 : 0;
	    if (i_37_ != 0 && i_32_ != 0) {
		float f = (float) i_37_ / (float) i_32_;
		i_36_ += (float) i_31_ * f;
	    }
	    is_30_[i_35_] = i_36_;
	    i_33_ += i_36_;
	}
    }
}
