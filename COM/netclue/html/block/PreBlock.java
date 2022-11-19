/* PreBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Vector;

import com.netclue.html.AbstractElement;
import com.netclue.html.DocumentTabs;
import com.netclue.html.HTMLTagBag;
import com.netclue.html.StyleFactory;
import com.netclue.html.TagAttributes;

public class PreBlock extends StackBlock implements DocumentTabs
{
    private int justification;
    private int rowJustification;
    private int allocWidth;
    private int minSpan = 1;
    private Vector childElements;
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
	
	public void replace(int i, int i_0_, Block[] blocks) {
	    for (int i_1_ = i; i_1_ < i + i_0_; i_1_++)
		children[i_1_].setParent(null);
	    int i_2_ = blocks.length - i_0_;
	    int i_3_ = i + i_0_;
	    int i_4_ = nchildren - i_3_;
	    int i_5_ = i_3_ + i_2_;
	    if (nchildren + i_2_ >= children.length) {
		int i_6_ = Math.max(2 * children.length, nchildren + i_2_);
		Block[] blocks_7_ = new Block[i_6_];
		System.arraycopy(children, 0, blocks_7_, 0, i);
		System.arraycopy(blocks, 0, blocks_7_, i, blocks.length);
		System.arraycopy(children, i_3_, blocks_7_, i_5_, i_4_);
		children = blocks_7_;
	    } else {
		System.arraycopy(children, i_3_, children, i_5_, i_4_);
		System.arraycopy(blocks, 0, children, i, blocks.length);
	    }
	    nchildren = nchildren + i_2_;
	    for (int i_8_ = 0; i_8_ < blocks.length; i_8_++) {
		Block block = blocks[i_8_];
		if (block.getParent() == null)
		    block.setParent(this);
		else
		    block.parent = this;
	    }
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
	    return super.getAlignment(i);
	}
	
	protected void setRowInsets(short i, short i_9_, short i_10_,
				    short i_11_) {
	    super.setInsets(i, i_9_, i_10_, i_11_);
	}
	
	public boolean isResizable(int i) {
	    if (i != 0)
		return false;
	    return true;
	}
	
	protected short getRowBottomInset() {
	    return super.getBottomInset();
	}
	
	public Rectangle findBounds(int i, Rectangle rectangle) {
	    int i_12_ = rectangle.height;
	    int i_13_ = rectangle.y;
	    Rectangle rectangle_14_ = super.findBounds(i, rectangle);
	    if (rectangle_14_ != null) {
		rectangle = rectangle_14_.getBounds();
		rectangle.height = i_12_;
		rectangle.y = i_13_;
	    }
	    return rectangle;
	}
	
	public int getStartIndex() {
	    int i = 2147483647;
	    int i_15_ = this.getChildCount();
	    for (int i_16_ = 0; i_16_ < i_15_; i_16_++) {
		Block block = this.getChild(i_16_);
		i = Math.min(i, block.getStartIndex());
	    }
	    return i;
	}
	
	public int getEndIndex() {
	    int i = 0;
	    int i_17_ = this.getChildCount();
	    for (int i_18_ = 0; i_18_ < i_17_; i_18_++) {
		Block block = this.getChild(i_18_);
		i = Math.max(i, block.getEndIndex());
	    }
	    return i;
	}
	
	protected Block getBlockByIdx(int i, Rectangle rectangle) {
	    int i_19_ = this.getChildCount();
	    for (int i_20_ = 0; i_20_ < i_19_; i_20_++) {
		Block block = this.getChild(i_20_);
		int i_21_ = block.getStartIndex();
		int i_22_ = block.getEndIndex();
		if (i >= i_21_ && i < i_22_) {
		    this.childAllocation(i_20_, rectangle);
		    return block;
		}
	    }
	    return null;
	}
	
	void doTiledLayout(int i, int i_23_) {
	    int i_24_ = i - preferredSpan[i_23_];
	    int i_25_ = resizeWeight[i_23_];
	    boolean bool = false;
	    int i_26_ = this.getChildCount();
	    int i_28_;
	    int i_27_ = i_28_ = 0;
	    for (/**/; i_28_ < i_26_; i_28_++) {
		Block block = this.getChild(i_28_);
		xOffsets[i_28_] = i_27_;
		int i_29_ = block.getPreferredSize(i_23_);
		int i_30_ = block.isResizable(i_23_) ? 1 : 0;
		if (i_30_ != 0 && i_25_ != 0) {
		    if (block instanceof TableBlock) {
			int i_31_ = ((TableBlock) block).getWidthRatio();
			if (i_31_ > 0) {
			    int i_32_ = i * i_31_ / 100;
			    if (i_32_ != i_29_) {
				block.sizeChanged(true, false);
				i_29_ = i_32_;
			    }
			}
		    } else {
			float f = (float) (i_30_ / i_25_);
			i_29_ += (float) i_24_ * f;
		    }
		}
		xSpans[i_28_] = i_29_;
		i_27_ += i_29_;
	    }
	    if (justification > 0 && i_27_ < i) {
		int i_33_ = (i - i_27_) * justification >> 1;
		i_28_ = 0;
		while (i_28_ < i_26_)
		    xOffsets[i_28_++] += i_33_;
	    }
	}
    }
    
    public PreBlock(AbstractElement abstractelement) {
	super(abstractelement, 1);
	abstractelement.setAttribute(StyleFactory.TabSet, this);
	TagAttributes tagattributes = abstractelement.getAttributeNode();
	top = (short) 20;
	justification = StyleFactory.getAlignment(tagattributes);
	allocWidth = 2147483647;
    }
    
    int countChildren(AbstractElement abstractelement) {
	int i_34_;
	int i = i_34_ = abstractelement.getElementCount();
	for (int i_35_ = 0; i_35_ < i_34_; i_35_++) {
	    AbstractElement abstractelement_36_
		= abstractelement.getElement(i_35_);
	    int i_37_ = abstractelement_36_.getTagCode();
	    if (i_37_ == HTMLTagBag.anchorID || i_37_ == HTMLTagBag.fontID)
		i += countChildren(abstractelement_36_) - 1;
	}
	return i;
    }
    
    int connectChildren(AbstractElement abstractelement,
			BlockFactory blockfactory) {
	int i = abstractelement.getElementCount();
	for (int i_38_ = 0; i_38_ < i; i_38_++) {
	    AbstractElement abstractelement_39_
		= abstractelement.getElement(i_38_);
	    abstractelement_39_.getTagCode();
	    Block block = blockfactory.create(abstractelement_39_);
	    if (block != null) {
		childElements.addElement(block);
		block.setParent(this);
	    } else
		connectChildren(abstractelement_39_, blockfactory);
	}
	return i;
    }
    
    protected void createChild(BlockFactory blockfactory) {
	AbstractElement abstractelement = this.getElement();
	int i = countChildren(abstractelement);
	childElements = new Vector(i, 16);
	connectChildren(abstractelement, blockfactory);
    }
    
    public int getMinimumSize(int i, int i_40_) {
	if (i == 0) {
	    Enumeration enumeration = childElements.elements();
	    int i_42_;
	    int i_43_;
	    int i_41_ = i_42_ = i_43_ = 0;
	    while (enumeration.hasMoreElements()) {
		Block block = (Block) enumeration.nextElement();
		if (block instanceof TableBlock)
		    i_41_ = Math.max(i_41_, block.getMinimumSize(i));
		else if (block instanceof TextBlock) {
		    int i_44_ = block.getMinimumSize(i);
		    if (i_44_ < 10 && i_40_ <= 0)
			i_42_ += i_44_;
		    else {
			if (i_44_ > i_41_ && i_40_ > 0)
			    i_44_ = Math.min(i_40_, i_44_);
			i_41_ = Math.max(i_41_, i_44_);
		    }
		} else {
		    i_41_ = Math.max(i_41_, block.getPreferredSize(i));
		    if (block instanceof BRBlock) {
			i_43_ = Math.max(i_43_, i_42_);
			i_42_ = 0;
		    }
		}
	    }
	    if (i_41_ != 0) {
		i_43_ = Math.max(i_43_, i_42_);
		minSpan = i_41_ + i_43_;
	    }
	    return minSpan;
	}
	return cheapYSize();
    }
    
    public int getMaximumSize(int i) {
	Enumeration enumeration = childElements.elements();
	int i_46_;
	int i_45_ = i_46_ = 0;
	while (enumeration.hasMoreElements()) {
	    Block block = (Block) enumeration.nextElement();
	    if (block instanceof BRBlock) {
		i_45_ = Math.max(i_45_, i_46_);
		i_46_ = 0;
	    } else
		i_46_ += block.getMaximumSize(i);
	}
	return Math.max(i_45_, i_46_);
    }
    
    protected Block getBlockByIdx(int i, Rectangle rectangle) {
	int i_47_ = this.getChildCount();
	for (int i_48_ = 0; i_48_ < i_47_; i_48_++) {
	    Block block = this.getChild(i_48_);
	    int i_49_ = block.getStartIndex();
	    int i_50_ = block.getEndIndex();
	    if (i >= i_49_ && i < i_50_) {
		this.childAllocation(i_48_, rectangle);
		return block;
	    }
	}
	return null;
    }
    
    protected void layout(int i, int i_51_) {
	if (this.getElement().getElementCount() > 0) {
	    if (allocWidth != i || !yAllocValid) {
		int i_52_ = getPreferredSize(1);
		createLines(i);
		int i_53_ = getPreferredSize(1);
		if (i_52_ != i_53_)
		    this.getParent().sizeChanged(false, true);
	    }
	    super.layout(i, i_51_);
	}
    }
    
    void createLines(int i) {
	allocWidth = i;
	rowJustification = justification;
	int i_54_ = this.getStartIndex();
	int i_55_ = this.getEndIndex();
	Object object = null;
	this.removeAll();
	while (i_54_ < i_55_) {
	    int i_56_ = i_54_;
	    Row row = new Row(this.getElement());
	    this.append(row);
	    layoutLines(row, i_54_);
	    if (row.getChildCount() == 0) {
		this.removeLast();
		object = null;
		break;
	    }
	    if (row.justification < 0)
		row.justification = rowJustification;
	    i_54_ = row.getEndIndex();
	    if (i_54_ <= i_56_)
		break;
	}
    }
    
    void layoutLines(Row row, int i) {
	int i_57_ = this.getEndIndex();
	Object object = null;
	Block block;
	for (/**/; i < i_57_; i = block.getEndIndex()) {
	    block = createBlock(i);
	    if (block instanceof StyleBlock || block instanceof HRuleBlock) {
		if (row.getChildCount() == 0)
		    row.append(block);
		break;
	    }
	    if (block instanceof BRBlock
		&& (row.getChildCount() != 0 || this.getChildCount() > 1)) {
		block.setSize(0, 12);
		row.append(block);
		break;
	    }
	    row.append(block);
	}
    }
    
    Block createBlock(int i) {
	Object object = null;
	int i_58_ = childElements.size();
	for (int i_59_ = 0; i_59_ < i_58_; i_59_++) {
	    Block block = (Block) childElements.elementAt(i_59_);
	    if (i < block.getEndIndex()) {
		if (i != block.getStartIndex())
		    block = block.createFragment(i, block.getEndIndex());
		return block;
	    }
	}
	return null;
    }
    
    public float nextTabPosition(float f) {
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
	int i_60_ = rectangle.x + this.getLeftInset();
	int i_61_ = rectangle.y + this.getTopInset();
	Rectangle rectangle_62_ = graphics.getClipBounds();
	for (int i_63_ = 0; i_63_ < i; i_63_++) {
	    rectangle.x = i_60_ + xOffsets[i_63_];
	    rectangle.y = i_61_ + yOffsets[i_63_];
	    rectangle.width = xSpans[i_63_];
	    rectangle.height = ySpans[i_63_];
	    if (rectangle.intersects(rectangle_62_)) {
		Block block = this.getChild(i_63_);
		block.paint(graphics, rectangle);
	    }
	}
    }
    
    public int getPreferredSize(int i) {
	switch (i) {
	case 0: {
	    int i_64_ = Math.min(allocWidth, 2147483647);
	    if (i_64_ == 2147483647 && this.getChildCount() == 0)
		i_64_ = (cheapXSize() + this.getLeftInset()
			 + this.getRightInset());
	    else
		i_64_ = super.getPreferredSize(i);
	    return i_64_;
	}
	case 1:
	    if (allocWidth == 2147483647 && this.getChildCount() == 0)
		return cheapYSize();
	    return super.getPreferredSize(i);
	default:
	    throw new IllegalArgumentException("Invalid axis: " + i);
	}
    }
    
    private int cheapXSize() {
	Enumeration enumeration = childElements.elements();
	int i_65_;
	int i_66_;
	int i = i_65_ = i_66_ = 0;
	while (enumeration.hasMoreElements()) {
	    Block block = (Block) enumeration.nextElement();
	    if (block instanceof TextBlock) {
		int i_67_ = block.getMinimumSize(0);
		if (i_67_ < 10)
		    i_65_ += i_67_;
		else
		    i = Math.max(i, i_67_);
	    } else if (block instanceof BRBlock) {
		i_66_ = Math.max(i_66_, i_65_);
		i_65_ = 0;
	    } else
		i = Math.max(i, block.getPreferredSize(0));
	}
	if (i != 0)
	    i += i_66_;
	return i;
    }
    
    private int cheapYSize() {
	Enumeration enumeration = childElements.elements();
	int i_68_;
	int i_69_;
	int i = i_68_ = i_69_ = 0;
	while (enumeration.hasMoreElements()) {
	    Block block = (Block) enumeration.nextElement();
	    if (block instanceof BRBlock) {
		TagAttributes tagattributes
		    = block.getElement().getAttributeNode();
		if (tagattributes != null) {
		    Integer integer
			= (Integer) tagattributes
					.getAttribute(StyleFactory.SpaceBelow);
		    if (integer != null)
			i_69_ = Math.max(i_69_, integer.intValue());
		}
		i += i_68_;
		i_68_ = 0;
	    } else {
		if (i_69_ > 0) {
		    i += i_69_;
		    i_69_ = 0;
		}
		i_68_ = Math.max(i_68_, block.getPreferredSize(1));
	    }
	}
	return i + i_68_;
    }
    
    public float getAlignment(int i) {
	return 0.0F;
    }
    
    public boolean isResizable(int i) {
	if (i == 0)
	    return false;
	return true;
    }
}
