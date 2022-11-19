/* Block - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;
import com.netclue.html.BaseDocument;

public abstract class Block
{
    public static final int BadBreakWeight = 0;
    public static final int GoodBreakWeight = 1000;
    public static final int ExcellentBreakWeight = 2000;
    public static final int ForcedBreakWeight = 3000;
    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;
    protected Block parent;
    private AbstractElement elem;
    
    public Block(AbstractElement abstractelement) {
	elem = abstractelement;
    }
    
    protected Block getParent() {
	return parent;
    }
    
    public void setParent(Block block_0_) {
	parent = block_0_;
    }
    
    public void setStatus(boolean bool) {
	/* empty */
    }
    
    public abstract int getPreferredSize(int i);
    
    public int getMinimumSize(int i) {
	return getPreferredSize(i);
    }
    
    public int getMaximumSize(int i) {
	return getPreferredSize(i);
    }
    
    public void sizeChanged(boolean bool, boolean bool_1_) {
	Block block_2_ = getParent();
	if (block_2_ != null)
	    block_2_.sizeChanged(bool, bool_1_);
    }
    
    public float getAlignment(int i) {
	return 0.5F;
    }
    
    public abstract void paint(Graphics graphics, Shape shape);
    
    public int getChildCount() {
	return 0;
    }
    
    public Block getChild(int i) {
	return null;
    }
    
    public abstract Rectangle findBounds(int i, Rectangle rectangle);
    
    public abstract int getDocIndex(int i, int i_3_, Shape shape);
    
    public BaseDocument getDocument() {
	return elem.getDocument();
    }
    
    public int getStartIndex() {
	return elem.getStartIndex();
    }
    
    public int getEndIndex() {
	return elem.getEndIndex();
    }
    
    public AbstractElement getElement() {
	return elem;
    }
    
    public Block divideBlock(int i, int i_4_, float f, float f_5_) {
	return this;
    }
    
    public Block createFragment(int i, int i_6_) {
	return this;
    }
    
    public int getBreakWeight(int i, float f, float f_7_) {
	return 0;
    }
    
    public boolean isResizable(int i) {
	return false;
    }
    
    public void setSize(int i, int i_8_) {
	/* empty */
    }
    
    public Container getContainer() {
	Block block_9_ = getParent();
	if (block_9_ != null)
	    return block_9_.getContainer();
	return null;
    }
    
    public BlockFactory getBlockFactory() {
	Block block_10_ = getParent();
	if (block_10_ != null)
	    return block_10_.getBlockFactory();
	return null;
    }
}
