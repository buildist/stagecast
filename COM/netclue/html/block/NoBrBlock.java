/* NoBrBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Vector;

import com.netclue.html.AbstractElement;
import com.netclue.html.HTMLTagBag;
import com.netclue.html.StyleFactory;
import com.netclue.html.TagAttributes;

public class NoBrBlock extends StackBlock
{
    int justification;
    Vector childElements;
    String alignStr;
    private int tabBase;
    
    class Row extends StackBlock
    {
	int justification = -1;
	
	Row(AbstractElement abstractelement) {
	    super(abstractelement, 0);
	}
	
	protected void createChild(BlockFactory blockfactory) {
	    /* empty */
	}
	
	public void append(Block block) {
	    if (nchildren == children.length) {
		Block[] blocks = new Block[nchildren << 2];
		System.arraycopy(children, 0, blocks, 0, nchildren);
		children = blocks;
	    }
	    children[nchildren++] = block;
	    if (block instanceof StyleBlock)
		block.parent = this;
	    else
		block.setParent(this);
	}
	
	public float getAlignment(int i) {
	    if (i == 0) {
		switch (justification) {
		case 0:
		    return 0.0F;
		case 2:
		    return 1.0F;
		case 1:
		case 3:
		    return 0.5F;
		}
	    }
	    return 0.0F;
	}
	
	public boolean isResizable(int i) {
	    if (i != 0)
		return false;
	    return true;
	}
	
	protected void setLeftInset(short i) {
	    left = i;
	}
	
	protected void setRightInset(short i) {
	    right = i;
	}
	
	public int getStartIndex() {
	    int i = 2147483647;
	    int i_0_ = this.getChildCount();
	    for (int i_1_ = 0; i_1_ < i_0_; i_1_++) {
		Block block = this.getChild(i_1_);
		i = Math.min(i, block.getStartIndex());
	    }
	    return i;
	}
	
	public int getEndIndex() {
	    int i = 0;
	    int i_2_ = this.getChildCount();
	    for (int i_3_ = 0; i_3_ < i_2_; i_3_++) {
		Block block = this.getChild(i_3_);
		i = Math.max(i, block.getEndIndex());
	    }
	    return i;
	}
	
	protected Block getBlockByIdx(int i, Rectangle rectangle) {
	    int i_4_ = this.getChildCount();
	    for (int i_5_ = 0; i_5_ < i_4_; i_5_++) {
		Block block = this.getChild(i_5_);
		int i_6_ = block.getStartIndex();
		int i_7_ = block.getEndIndex();
		if (i >= i_6_ && i < i_7_) {
		    this.childAllocation(i_5_, rectangle);
		    return block;
		}
	    }
	    return null;
	}
	
	void doTiledLayout(int i, int i_8_) {
	    int i_9_ = i - preferredSpan[i_8_];
	    int i_10_ = resizeWeight[i_8_];
	    boolean bool = false;
	    int i_11_ = this.getChildCount();
	    int i_13_;
	    int i_12_ = i_13_ = 0;
	    for (/**/; i_13_ < i_11_; i_13_++) {
		Block block = this.getChild(i_13_);
		xOffsets[i_13_] = i_12_;
		int i_14_ = block.getPreferredSize(i_8_);
		int i_15_ = block.isResizable(i_8_) ? 1 : 0;
		if (i_15_ != 0 && i_10_ != 0) {
		    if (block instanceof TableBlock) {
			int i_16_ = ((TableBlock) block).getWidthRatio();
			if (i_16_ > 0) {
			    int i_17_ = i * i_16_ / 100;
			    i_17_ = Math.max(block.getMinimumSize(0), i_17_);
			    if (i_17_ != i_14_) {
				block.sizeChanged(true, false);
				i_14_ = i_17_;
			    }
			} else {
			    int i_18_ = block.getMaximumSize(0);
			    i_14_ = Math.min(i_18_, i);
			}
		    } else
			i_14_ += i_9_ * i_15_ / i_10_;
		}
		xSpans[i_13_] = i_14_;
		i_12_ += i_14_;
	    }
	    if (justification > 0 && i_12_ < i) {
		int i_19_ = (i - i_12_) * justification >> 1;
		i_13_ = 0;
		while (i_13_ < i_11_)
		    xOffsets[i_13_++] += i_19_;
	    }
	}
    }
    
    public NoBrBlock(AbstractElement abstractelement) {
	super(abstractelement, 1);
	TagAttributes tagattributes = abstractelement.getAttributeNode();
	justification = 0;
	if (tagattributes != null) {
	    this.setInsets(tagattributes);
	    alignStr = (String) tagattributes.getAttribute("align");
	    if (alignStr != null) {
		alignStr = alignStr.toLowerCase();
		if (alignStr.equals("center") || alignStr.equals("middle"))
		    justification = 1;
		else if (alignStr.equals("right"))
		    justification = 2;
		else if (alignStr.equals("left"))
		    justification = 0;
		else
		    justification = StyleFactory.getAlignment(tagattributes);
	    }
	}
    }
    
    int countChildren(AbstractElement abstractelement) {
	int i_20_;
	int i = i_20_ = abstractelement.getElementCount();
	for (int i_21_ = 0; i_21_ < i_20_; i_21_++) {
	    AbstractElement abstractelement_22_
		= abstractelement.getElement(i_21_);
	    int i_23_ = abstractelement_22_.getTagCode();
	    if (i_23_ == HTMLTagBag.anchorID || i_23_ == HTMLTagBag.fontID)
		i += countChildren(abstractelement_22_) - 1;
	}
	return i;
    }
    
    int connectChildren(AbstractElement abstractelement,
			BlockFactory blockfactory) {
	int i = abstractelement.getElementCount();
	for (int i_24_ = 0; i_24_ < i; i_24_++) {
	    AbstractElement abstractelement_25_
		= abstractelement.getElement(i_24_);
	    Block block = blockfactory.create(abstractelement_25_);
	    if (block != null) {
		childElements.addElement(block);
		block.setParent(this);
	    } else
		connectChildren(abstractelement_25_, blockfactory);
	}
	return i;
    }
    
    protected void createChild(BlockFactory blockfactory) {
	AbstractElement abstractelement = this.getElement();
	int i = countChildren(abstractelement);
	childElements = new Vector(i, 16);
	connectChildren(abstractelement, blockfactory);
    }
    
    protected void layout(int i, int i_26_) {
	if (this.getElement().getElementCount() > 0) {
	    int i_27_ = this.getPreferredSize(1);
	    runFlows(i);
	    int i_28_ = this.getPreferredSize(1);
	    if (i_27_ != i_28_)
		this.getParent().sizeChanged(false, true);
	    super.layout(i, i_26_);
	}
    }
    
    void runFlows(int i) {
	int i_29_ = this.getStartIndex();
	int i_30_ = this.getEndIndex();
	int i_31_ = 0;
	while (i_29_ < i_30_) {
	    int i_32_ = i_29_;
	    Row row;
	    if (i_31_ < nchildren) {
		row = (Row) children[i_31_];
		row.removeAll();
	    } else {
		row = new Row(this.getElement());
		this.append(row);
	    }
	    slice(row, i_29_);
	    if (row.getChildCount() == 0) {
		this.removeLast();
		break;
	    }
	    row.justification = justification;
	    i_29_ = row.getEndIndex();
	    if (i_29_ <= i_32_)
		break;
	    i_31_++;
	}
    }
    
    void slice(Row row, int i) {
	int i_33_ = this.getEndIndex();
	Object object = null;
	Block block;
	for (/**/; i < i_33_; i = block.getEndIndex()) {
	    block = pickBlock(i);
	    if (block == null)
		break;
	    if (block instanceof StyleBlock || block instanceof HRuleBlock) {
		if (row.getChildCount() == 0)
		    row.append(block);
		break;
	    }
	    row.append(block);
	    if (block instanceof BRBlock)
		break;
	}
    }
    
    Block pickBlock(int i) {
	Object object = null;
	int i_34_ = childElements.size();
	for (int i_35_ = 0; i_35_ < i_34_; i_35_++) {
	    Block block = (Block) childElements.elementAt(i_35_);
	    if (i < block.getEndIndex()) {
		if (i != block.getStartIndex())
		    block = block.createFragment(i, block.getEndIndex());
		return block;
	    }
	}
	return null;
    }
    
    public float nextTabPosition(float f, int i) {
	if (justification != 0)
	    return f + 10.0F;
	f -= (float) tabBase;
	return (float) (tabBase + ((int) f / 72 + 1) * 72);
    }
    
    public void paint(Graphics graphics, Shape shape) {
	Rectangle rectangle = shape.getBounds();
	this.setSize(rectangle.width, rectangle.height);
	tabBase = rectangle.x;
	paintLine(graphics, rectangle);
    }
    
    protected void paintLine(Graphics graphics, Rectangle rectangle) {
	int i = this.getChildCount();
	int i_36_ = rectangle.x + this.getLeftInset();
	int i_37_ = rectangle.y + this.getTopInset();
	Rectangle rectangle_38_ = graphics.getClipBounds();
	for (int i_39_ = 0; i_39_ < i; i_39_++) {
	    rectangle.x = i_36_ + xOffsets[i_39_];
	    rectangle.y = i_37_ + yOffsets[i_39_];
	    rectangle.width = xSpans[i_39_];
	    rectangle.height = ySpans[i_39_];
	    if (rectangle.intersects(rectangle_38_))
		this.getChild(i_39_).paint(graphics, rectangle);
	}
    }
    
    void refreshCache() {
	if (this.getChildCount() == 0) {
	    if (!xValid)
		preferredSpan[0] = cheapXSize();
	    if (!yValid)
		preferredSpan[1] = cheapYSize();
	} else
	    super.refreshCache();
	xValid = yValid = true;
    }
    
    int cheapXSize() {
	Enumeration enumeration = childElements.elements();
	int i = 0;
	int i_40_ = 1;
	while (enumeration.hasMoreElements()) {
	    Block block = (Block) enumeration.nextElement();
	    if (block instanceof BRBlock) {
		i_40_ = Math.max(i_40_, i);
		i = 0;
	    }
	    i += block.getPreferredSize(0);
	}
	return Math.max(i_40_, i) + this.getLeftInset() + this.getRightInset();
    }
    
    int cheapYSize() {
	Enumeration enumeration = childElements.elements();
	int i_41_;
	int i_42_;
	int i = i_41_ = i_42_ = 0;
	while (enumeration.hasMoreElements()) {
	    Block block = (Block) enumeration.nextElement();
	    if (block instanceof BRBlock) {
		if (i_42_++ > 0)
		    i_41_ += 10;
		i += i_41_;
		i_41_ = 0;
	    } else {
		i_41_ = Math.max(i_41_, block.getPreferredSize(1));
		i_42_ = 0;
	    }
	}
	return i + i_41_ + this.getLeftInset() + this.getRightInset();
    }
    
    public boolean isResizable(int i) {
	return false;
    }
}
