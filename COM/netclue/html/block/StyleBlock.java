/* StyleBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;
import com.netclue.html.HTMLTagBag;
import com.netclue.html.StyleFactory;
import com.netclue.html.TagAttributes;

public abstract class StyleBlock extends Block
{
    private static Block[] ONE = new Block[1];
    private static Block[] ZERO = new Block[0];
    Block[] children = new Block[1];
    int nchildren = 0;
    short left;
    short right;
    short top;
    short bottom;
    
    public StyleBlock(AbstractElement abstractelement) {
	super(abstractelement);
    }
    
    int countChildren(AbstractElement abstractelement) {
	int i_0_;
	int i = i_0_ = abstractelement.getElementCount();
	for (int i_1_ = 0; i_1_ < i_0_; i_1_++) {
	    AbstractElement abstractelement_2_
		= abstractelement.getElement(i_1_);
	    int i_3_ = abstractelement_2_.getTagCode();
	    if (i_3_ == HTMLTagBag.anchorID || i_3_ == HTMLTagBag.fontID)
		i += countChildren(abstractelement_2_) - 1;
	}
	return i;
    }
    
    int connectChildren(AbstractElement abstractelement, Block[] blocks, int i,
			BlockFactory blockfactory) {
	int i_4_ = abstractelement.getElementCount();
	int i_5_ = i_4_;
	int i_6_ = 0;
	for (int i_7_ = 0; i_7_ < i_4_; i_7_++) {
	    AbstractElement abstractelement_8_
		= abstractelement.getElement(i_7_);
	    Block block = blockfactory.create(abstractelement_8_);
	    if (block != null) {
		blocks[i + i_6_++] = block;
		block.setParent(this);
	    } else {
		int i_9_ = connectChildren(abstractelement_8_, blocks,
					   i + i_6_, blockfactory);
		i_6_ += i_9_;
		i_5_ += i_9_ - 1;
	    }
	}
	return i_5_;
    }
    
    protected void createChild(BlockFactory blockfactory) {
	AbstractElement abstractelement = this.getElement();
	int i = countChildren(abstractelement);
	if (i > 0) {
	    Block[] blocks = new Block[i];
	    nchildren
		= connectChildren(abstractelement, blocks, 0, blockfactory);
	    children = blocks;
	}
    }
    
    public void removeAll() {
	replace(0, nchildren, ZERO);
    }
    
    public void insert(int i, Block block) {
	ONE[0] = block;
	replace(i, 0, ONE);
    }
    
    public void append(Block block) {
	ONE[0] = block;
	replace(nchildren, 0, ONE);
    }
    
    public void replace(int i, int i_10_, Block[] blocks) {
	for (int i_11_ = i; i_11_ < i + i_10_; i_11_++)
	    children[i_11_].setParent(null);
	int i_12_ = blocks.length - i_10_;
	int i_13_ = i + i_10_;
	int i_14_ = nchildren - i_13_;
	int i_15_ = i_13_ + i_12_;
	if (nchildren + i_12_ >= children.length) {
	    int i_16_ = Math.max(2 * children.length, nchildren + i_12_);
	    Block[] blocks_17_ = new Block[i_16_];
	    System.arraycopy(children, 0, blocks_17_, 0, i);
	    System.arraycopy(blocks, 0, blocks_17_, i, blocks.length);
	    System.arraycopy(children, i_13_, blocks_17_, i_15_, i_14_);
	    children = blocks_17_;
	} else {
	    System.arraycopy(children, i_13_, children, i_15_, i_14_);
	    System.arraycopy(blocks, 0, children, i, blocks.length);
	}
	nchildren = nchildren + i_12_;
	for (int i_18_ = 0; i_18_ < blocks.length; i_18_++)
	    blocks[i_18_].setParent(this);
    }
    
    public void setParent(Block block) {
	super.setParent(block);
	if (block != null) {
	    BlockFactory blockfactory = this.getBlockFactory();
	    createChild(blockfactory);
	}
    }
    
    public int getChildCount() {
	return nchildren;
    }
    
    public Block getChild(int i) {
	return children[i];
    }
    
    public Rectangle findBounds(int i, Rectangle rectangle) {
	if (rectangle == null)
	    return null;
	trimSpace(rectangle);
	Block block = getBlockByIdx(i, rectangle);
	if (block != null)
	    return block.findBounds(i, rectangle);
	return null;
    }
    
    public int getDocIndex(int i, int i_19_, Shape shape) {
	if (shape == null)
	    return -1;
	Rectangle rectangle = shape.getBounds();
	trimSpace(rectangle);
	if (isOutOfBounds(i, i_19_, rectangle))
	    return -1;
	Block block = getBlockByPos(i, i_19_, rectangle);
	if (block != null)
	    return block.getDocIndex(i, i_19_, rectangle);
	return -1;
    }
    
    protected abstract boolean isOutOfBounds(int i, int i_20_,
					     Rectangle rectangle);
    
    protected abstract Block getBlockByPos(int i, int i_21_,
					   Rectangle rectangle);
    
    protected abstract void childAllocation(int i, Rectangle rectangle);
    
    protected Block getBlockByIdx(int i, Rectangle rectangle) {
	int i_22_ = getChildCount();
	for (int i_23_ = 0; i_23_ < i_22_; i_23_++) {
	    Block block = getChild(i_23_);
	    int i_24_ = block.getStartIndex();
	    int i_25_ = block.getEndIndex();
	    if (i >= i_24_ && i < i_25_) {
		childAllocation(i_23_, rectangle);
		return block;
	    }
	}
	return null;
    }
    
    protected void trimSpace(Rectangle rectangle) {
	rectangle.x += left;
	rectangle.y += top;
	rectangle.width -= left + right;
	rectangle.height -= top + bottom;
    }
    
    protected void setInsets(TagAttributes tagattributes) {
	Float var_float
	    = (Float) tagattributes.getAttribute(StyleFactory.SpaceAbove);
	top = var_float == null ? (short) 0 : (short) var_float.intValue();
	var_float
	    = (Float) tagattributes.getAttribute(StyleFactory.LeftIndent);
	left = var_float == null ? (short) 0 : (short) var_float.intValue();
	var_float
	    = (Float) tagattributes.getAttribute(StyleFactory.SpaceBelow);
	bottom = var_float == null ? (short) 0 : (short) var_float.intValue();
	var_float
	    = (Float) tagattributes.getAttribute(StyleFactory.RightIndent);
	right = var_float == null ? (short) 0 : (short) var_float.intValue();
    }
    
    protected void setInsets(short i, short i_26_, short i_27_, short i_28_) {
	top = i;
	left = i_26_;
	right = i_28_;
	bottom = i_27_;
    }
    
    protected void setTopInset(int i) {
	top = (short) i;
    }
    
    protected short getLeftInset() {
	return left;
    }
    
    protected short getRightInset() {
	return right;
    }
    
    protected short getTopInset() {
	return top;
    }
    
    protected short getBottomInset() {
	return bottom;
    }
}
