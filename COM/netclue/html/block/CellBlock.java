/* CellBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import com.netclue.html.AbstractElement;
import com.netclue.html.DocumentTabs;
import com.netclue.html.HTMLTagBag;
import com.netclue.html.StyleFactory;
import com.netclue.html.TagAttributes;

public class CellBlock extends StyleBlock implements DocumentTabs
{
    int justification;
    int gWidth;
    int gHeight;
    int curHeight;
    int rowCount;
    int rigidCount;
    int[] rowHeights;
    int[] preferredSize = new int[2];
    int[] maxSize = new int[2];
    int[] minSize = new int[2];
    Rectangle[] absPosition;
    Vector childElements;
    boolean xValid;
    boolean yValid;
    boolean isLayoutValid;
    String alignStr;
    Point lMgn;
    Point rMgn;
    Stack leftMargins;
    Stack rightMargins;
    Block[] rowBlocks;
    Block[] rigidBlocks;
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
	    block.parent = this;
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
	    if (i == 0)
		return true;
	    return super.isResizable(1);
	}
	
	protected void setLeftInset(short i) {
	    left = i;
	}
	
	protected void setRightInset(short i) {
	    right = i;
	}
	
	protected short getRowBottomInset() {
	    return super.getBottomInset();
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
    
    public CellBlock(AbstractElement abstractelement) {
	super(abstractelement);
	TagAttributes tagattributes = abstractelement.getAttributeNode();
	justification = 0;
	if (abstractelement.getTagCode() == HTMLTagBag.cenID) {
	    justification = 1;
	    alignStr = "center";
	} else if (tagattributes != null) {
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
	    int i_26_ = abstractelement_25_.getTagCode();
	    if (i_26_ == HTMLTagBag.parID && alignStr != null
		&& abstractelement_25_.getLocalAttribute("align") == null)
		abstractelement_25_.setAttribute("align", alignStr);
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
	rowHeights = new int[i];
	rowBlocks = new Block[i];
	childElements = new Vector(i, 16);
	connectChildren(abstractelement, blockfactory);
    }
    
    public int getChildCount() {
	return rowCount + rigidCount;
    }
    
    public Block getChild(int i) {
	if (i < 0 || i >= rowCount + rigidCount)
	    return null;
	if (i < rowCount)
	    return rowBlocks[i];
	return rigidBlocks[i - rowCount];
    }
    
    public void setSize(int i, int i_27_) {
	if (i != gWidth || i_27_ != gHeight) {
	    CellBlock cellblock_28_ = this;
	    cellblock_28_.xValid = cellblock_28_.xValid & i != gWidth;
	    isLayoutValid = false;
	}
	if (!isLayoutValid) {
	    gWidth = i;
	    gHeight = i_27_;
	    layout(gWidth - this.getLeftInset() - this.getRightInset(),
		   gHeight - this.getTopInset() - this.getBottomInset());
	    isLayoutValid = true;
	}
    }
    
    protected void layout(int i, int i_29_) {
	if (this.getElement().getElementCount() > 0) {
	    int i_30_ = getPreferredSize(1);
	    runFlows(i);
	    int i_31_ = getPreferredSize(1);
	    if (i_30_ != i_31_)
		this.getParent().sizeChanged(false, true);
	}
    }
    
    void runFlows(int i) {
	int i_32_ = this.getStartIndex();
	int i_33_ = this.getEndIndex();
	curHeight = 0;
	int i_34_ = rigidCount;
	Stack stack;
	if (leftMargins == null) {
	    leftMargins = new Stack();
	    stack = null;
	    lMgn = new Point(0, 2147483647);
	} else {
	    stack = deepCloneStack(leftMargins);
	    lMgn = (Point) leftMargins.pop();
	}
	Stack stack_35_;
	if (rightMargins == null) {
	    rightMargins = new Stack();
	    stack_35_ = null;
	    rMgn = new Point(0, 2147483647);
	} else {
	    stack_35_ = deepCloneStack(rightMargins);
	    rMgn = (Point) rightMargins.pop();
	}
	removeAll();
	while (i_32_ < i_33_) {
	    int i_36_ = i_32_;
	    Row row = new Row(this.getElement());
	    appendRow(row);
	    slice(row, i_32_, i, curHeight);
	    if (row.getChildCount() == 0) {
		removeLastRow();
		break;
	    }
	    row.justification = justification;
	    int i_37_ = row.getPreferredSize(1);
	    row.setSize(i, i_37_);
	    i_37_ = row.getPreferredSize(1);
	    rowHeights[rowCount - 1] = i_37_;
	    for (curHeight += i_37_; lMgn.y <= curHeight;
		 lMgn = (Point) leftMargins.pop()) {
		/* empty */
	    }
	    for (/**/; rMgn.y <= curHeight;
		 rMgn = (Point) rightMargins.pop()) {
		/* empty */
	    }
	    i_32_ = row.getEndIndex();
	    if (i_32_ <= i_36_)
		break;
	}
	rigidCount = Math.max(rigidCount, i_34_);
	yValid = false;
	leftMargins = stack;
	rightMargins = stack_35_;
	if (rowCount == 0)
	    this.setTopInset(0);
    }
    
    void layoutRigid(Block block, int i, int i_38_, int i_39_) {
	int i_40_ = ((Rigid) block).getWidthRatio();
	int i_41_;
	if (i_40_ > 0)
	    i_41_ = Math.max(i_38_ * i_40_ / 100, block.getMinimumSize(0));
	else
	    i_41_ = Math.min(i_38_, block.getMaximumSize(0));
	i_41_ = Math.min(i_41_, i_38_ - lMgn.x - rMgn.x);
	int i_42_ = block.getPreferredSize(1);
	block.setSize(i_41_, i_42_);
	i_42_ = block.getPreferredSize(1);
	int i_43_ = i_39_;
	i_39_ += i_42_;
	int i_44_;
	if (i == -1) {
	    i_44_ = lMgn.x;
	    leftMargins.push(new Point(lMgn));
	    lMgn.x += i_41_;
	    lMgn.y = i_39_;
	} else {
	    rightMargins.push(new Point(rMgn));
	    rMgn.x += i_41_;
	    rMgn.y = i_39_;
	    i_44_ = i_38_ - rMgn.x;
	}
	Rectangle rectangle = new Rectangle(i_44_, i_43_, i_41_, i_42_);
	CellBlock cellblock_45_ = getCellBlock();
	if (cellblock_45_ == null)
	    appendRigid(block, rectangle);
	else {
	    rectangle.y += this.getTopInset();
	    cellblock_45_.liftRigid(block, i, rectangle);
	}
    }
    
    protected void liftRigid(Block block, int i, Rectangle rectangle) {
	rectangle.y += curHeight;
	boolean bool = false;
	if (i == -1 && leftMargins != null) {
	    leftMargins.push(new Point(lMgn));
	    lMgn.x += rectangle.width;
	    lMgn.y = rectangle.y + rectangle.height;
	    bool = true;
	} else if (rightMargins != null) {
	    rightMargins.push(new Point(rMgn));
	    rMgn.x += rectangle.width;
	    rMgn.y = rectangle.y + rectangle.height;
	    bool = true;
	}
	if (bool)
	    updateRigid(block, rectangle);
    }
    
    void updateRigid(Block block, Rectangle rectangle) {
	if (rigidBlocks == null)
	    appendRigid(block, rectangle);
	else {
	    int i = rigidBlocks.length;
	    for (int i_46_ = 0; i_46_ < i && rigidBlocks[i_46_] != null;
		 i_46_++) {
		if (rigidBlocks[i_46_] == block) {
		    absPosition[i_46_] = rectangle;
		    return;
		}
	    }
	    appendRigid(block, rectangle);
	}
    }
    
    void slice(Row row, int i, int i_47_, int i_48_) {
	int i_50_;
	int i_49_ = i_50_ = i_47_ - rMgn.x - lMgn.x;
	row.setLeftInset((short) lMgn.x);
	row.setRightInset((short) rMgn.x);
	int i_51_ = this.getEndIndex();
	Object object = null;
	while (i < i_51_ && i_49_ > 0) {
	    Block block = pickBlock(i);
	    if (block == null)
		break;
	    if (block instanceof Rigid) {
		if (rigidCount > 0 && block == rigidBlocks[rigidCount - 1]) {
		    i = block.getEndIndex();
		    continue;
		}
		Rigid rigid = (Rigid) block;
		if (rigid.getFlushAlign() != 0) {
		    row.yValid = false;
		    int i_52_ = (row.getChildCount() == 0 ? 0
				 : row.getPreferredSize(1));
		    layoutRigid(block, rigid.getFlushAlign(), i_47_,
				i_48_ + i_52_);
		    if (row.getChildCount() == 0) {
			row.setLeftInset((short) lMgn.x);
			row.setRightInset((short) rMgn.x);
			i_49_ = i_50_ = i_47_ - rMgn.x - lMgn.x;
			if (i_49_ <= 10) {
			    BlankBlock blankblock
				= new BlankBlock(block.getElement());
			    int i_53_ = 0;
			    if (!leftMargins.empty())
				i_53_ = Math.max(lMgn.y, i_53_);
			    if (!rightMargins.empty())
				i_53_ = Math.max(rMgn.y, i_53_);
			    blankblock.setSize(0, i_53_ - i_48_);
			    row.append(blankblock);
			    break;
			}
		    }
		    i = block.getEndIndex();
		    continue;
		}
	    }
	    if ((block instanceof StyleBlock || block instanceof HRuleBlock)
		&& !(block instanceof NoBrBlock)) {
		if (row.getChildCount() == 0) {
		    row.append(block);
		    if (block instanceof CellBlock) {
			CellBlock cellblock_54_ = (CellBlock) block;
			short i_55_ = cellblock_54_.getTopInset();
			if (!leftMargins.empty()) {
			    Stack stack = createChildMargin(leftMargins, i_48_,
							    lMgn, i_55_);
			    cellblock_54_.isLayoutValid = false;
			    cellblock_54_.setLeftMargin(stack);
			}
			if (!rightMargins.empty()) {
			    Stack stack
				= createChildMargin(rightMargins, i_48_, rMgn,
						    i_55_);
			    cellblock_54_.isLayoutValid = false;
			    cellblock_54_.setRightMargin(stack);
			}
			row.setLeftInset((short) 0);
			row.setRightInset((short) 0);
		    }
		}
		break;
	    }
	    if (block instanceof BRBlock) {
		boolean bool = false;
		AbstractElement abstractelement = block.getElement();
		String string
		    = (String) abstractelement.getLocalAttribute("clear");
		if (string != null) {
		    string = string.toLowerCase().intern();
		    if ((string == "all" || string == "left")
			&& !leftMargins.empty()) {
			if (row.getChildCount() != 0)
			    break;
			int i_56_ = i_48_;
			Point point = lMgn;
			do {
			    i_56_ = Math.max(i_56_, point.y);
			    point = (Point) leftMargins.pop();
			} while (!leftMargins.empty());
			lMgn.x = point.x;
			lMgn.y = point.y;
			BlankBlock blankblock
			    = new BlankBlock(block.getElement());
			blankblock.setSize(0, i_56_ - i_48_);
			row.append(blankblock);
			bool = true;
		    }
		    if ((string == "all" || string == "right")
			&& !rightMargins.empty()) {
			if (row.getChildCount() != 0)
			    break;
			int i_57_ = i_48_;
			Point point = rMgn;
			do {
			    i_57_ = Math.max(i_57_, point.y);
			    point = (Point) rightMargins.pop();
			} while (!rightMargins.empty());
			rMgn.x = point.x;
			rMgn.y = point.y;
			BlankBlock blankblock
			    = new BlankBlock(block.getElement());
			blankblock.setSize(0, i_57_ - i_48_);
			row.append(blankblock);
			bool = true;
		    }
		}
		if (!bool) {
		    if (row.getChildCount() == 0)
			block.setSize(0, 10);
		    row.append(block);
		}
		break;
	    }
	    int i_58_ = block.getPreferredSize(0);
	    i_49_ -= i_58_;
	    if (i_49_ < 0) {
		if (row.getChildCount() == 0 || block instanceof TextBlock
		    || block instanceof TextBlock.LabelFragment)
		    row.append(block);
		else
		    i_49_ += i_58_;
		break;
	    }
	    row.append(block);
	    i = block.getEndIndex();
	}
	if (i_49_ < 0)
	    fitALine(row, i_50_, 0);
    }
    
    CellBlock getCellBlock() {
	for (Block block = this.getParent(); block != null;
	     block = block.getParent()) {
	    if (block instanceof TableBlock.TableCell)
		return null;
	    if (block instanceof CellBlock)
		return (CellBlock) block;
	}
	return null;
    }
    
    Stack deepCloneStack(Stack stack) {
	int i = stack.size();
	Stack stack_59_ = new Stack();
	for (int i_60_ = 0; i_60_ < i; i_60_++)
	    stack_59_.addElement(new Point((Point) stack.elementAt(i_60_)));
	return stack_59_;
    }
    
    Stack createChildMargin(Stack stack, int i, Point point, int i_61_) {
	int i_62_ = i + i_61_;
	int i_63_ = stack.size();
	Stack stack_64_ = new Stack();
	for (int i_65_ = 0; i_65_ < i_63_; i_65_++) {
	    Point point_66_ = (Point) stack.elementAt(i_65_);
	    if (point_66_.y > i_62_) {
		Point point_67_ = new Point(point_66_.x, point_66_.y - i_62_);
		stack_64_.addElement(point_67_);
	    }
	}
	if (point.y > i_62_) {
	    Point point_68_ = new Point(point.x, point.y - i_62_);
	    stack_64_.addElement(point_68_);
	}
	return stack_64_;
    }
    
    protected void setLeftMargin(Stack stack) {
	leftMargins = stack;
    }
    
    protected void setRightMargin(Stack stack) {
	rightMargins = stack;
    }
    
    void appendRigid(Block block, Rectangle rectangle) {
	if (rigidBlocks == null) {
	    rigidBlocks = new Block[2];
	    absPosition = new Rectangle[2];
	}
	if (rigidCount == rigidBlocks.length) {
	    Block[] blocks = new Block[rigidCount << 1];
	    System.arraycopy(rigidBlocks, 0, blocks, 0, rigidCount);
	    rigidBlocks = blocks;
	    Rectangle[] rectangles = new Rectangle[rigidCount << 1];
	    System.arraycopy(absPosition, 0, rectangles, 0, rigidCount);
	    absPosition = rectangles;
	}
	rigidBlocks[rigidCount] = block;
	absPosition[rigidCount++] = rectangle;
    }
    
    void appendRow(Block block) {
	if (rowCount == rowBlocks.length) {
	    Block[] blocks = new Block[rowCount + 8];
	    System.arraycopy(rowBlocks, 0, blocks, 0, rowCount);
	    rowBlocks = blocks;
	    int[] is = new int[rowCount + 8];
	    System.arraycopy(rowHeights, 0, is, 0, rowCount);
	    rowHeights = is;
	}
	rowBlocks[rowCount++] = block;
	block.parent = this;
    }
    
    void removeLastRow() {
	rowBlocks[--rowCount] = null;
    }
    
    public void removeAll() {
	for (int i = 0; i < rowCount; i++)
	    rowBlocks[i].setParent(null);
	rowCount = rigidCount = 0;
    }
    
    protected void fitALine(Row row, int i, int i_69_) {
	int i_70_ = row.getChildCount();
	int i_71_ = 0;
	int i_72_ = 1;
	int i_73_ = 0;
	int i_74_ = -1;
	for (int i_75_ = 0; i_75_ < i_70_; i_75_++) {
	    Block block = row.getChild(i_75_);
	    int i_76_ = i - i_71_;
	    int i_77_ = block.getBreakWeight(0, (float) (i_69_ + i_71_),
					     (float) i_76_);
	    if (i_77_ >= i_72_) {
		i_72_ = i_77_;
		i_74_ = i_75_;
		i_73_ = i_71_;
	    }
	    i_71_ += block.getPreferredSize(0);
	}
	if (i_74_ >= 0) {
	    int i_78_ = i - i_73_;
	    Block block = row.getChild(i_74_);
	    block = block.divideBlock(0, block.getStartIndex(),
				      (float) (i_69_ + i_73_), (float) i_78_);
	    if (block == null) {
		Block[] blocks = new Block[0];
		row.replace(++i_74_, i_70_ - i_74_, blocks);
	    } else {
		Block[] blocks = new Block[1];
		blocks[0] = block;
		row.replace(i_74_, i_70_ - i_74_, blocks);
	    }
	}
    }
    
    Block pickBlock(int i) {
	Object object = null;
	int i_79_ = childElements.size();
	for (int i_80_ = 0; i_80_ < i_79_; i_80_++) {
	    Block block = (Block) childElements.elementAt(i_80_);
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
	tabBase = rectangle.x;
	Rectangle rectangle_81_ = graphics.getClipBounds();
	paintLine(graphics, rectangle, rectangle_81_);
	paintAbs(graphics, rectangle, rectangle_81_);
    }
    
    void paintAbs(Graphics graphics, Rectangle rectangle,
		  Rectangle rectangle_82_) {
	Rectangle rectangle_83_ = new Rectangle();
	int i = rectangle.x + this.getLeftInset();
	int i_84_ = rectangle.y + this.getTopInset();
	for (int i_85_ = 0; i_85_ < rigidCount; i_85_++) {
	    Rectangle rectangle_86_ = absPosition[i_85_];
	    rectangle_83_.x = i + rectangle_86_.x;
	    rectangle_83_.y = i_84_ + rectangle_86_.y;
	    rectangle_83_.width = rectangle_86_.width;
	    rectangle_83_.height = rectangle_86_.height;
	    if (rectangle_83_.intersects(rectangle_82_))
		rigidBlocks[i_85_].paint(graphics, rectangle_83_);
	}
    }
    
    protected void paintLine(Graphics graphics, Rectangle rectangle,
			     Rectangle rectangle_87_) {
	Rectangle rectangle_88_ = new Rectangle(rectangle);
	rectangle_88_.x += this.getLeftInset();
	int i = rectangle_88_.y + this.getTopInset();
	rectangle_88_.width
	    = gWidth - this.getLeftInset() - this.getRightInset();
	for (int i_89_ = 0; i_89_ < rowCount; i_89_++) {
	    rectangle_88_.y = i;
	    rectangle_88_.height = rowHeights[i_89_];
	    i += rectangle_88_.height;
	    if (rectangle_88_.intersects(rectangle_87_)) {
		Block block = rowBlocks[i_89_];
		block.paint(graphics, rectangle_88_);
	    }
	}
    }
    
    public int getMaximumSize(int i) {
	refreshCache();
	return maxSize[i];
    }
    
    public int getPreferredSize(int i) {
	refreshCache();
	return preferredSize[i];
    }
    
    public void sizeChanged(boolean bool, boolean bool_90_) {
	if (bool)
	    xValid = isLayoutValid = false;
	if (bool_90_)
	    yValid = isLayoutValid = false;
	this.getParent().sizeChanged(bool, bool_90_);
    }
    
    void refreshCache() {
	if (!xValid) {
	    preferredSize[0] = minSize[0] = cheapXSize();
	    maxSize[0] = getXMaximumSize();
	}
	if (!yValid) {
	    if (rowCount == 0 && minSize[1] == 0)
		maxSize[1] = preferredSize[1] = minSize[1] = cheapYSize();
	    else {
		int i = this.getTopInset() + this.getBottomInset();
		for (int i_91_ = 0; i_91_ < rowCount; i_91_++)
		    i += rowHeights[i_91_];
		for (int i_92_ = 0; i_92_ < rigidCount; i_92_++) {
		    Rectangle rectangle = absPosition[i_92_];
		    i = Math.max(i, rectangle.y + rigidBlocks[i_92_]
						      .getPreferredSize(1));
		}
		maxSize[1] = preferredSize[1] = i;
		minSize[1] = cheapYSize();
	    }
	}
	xValid = yValid = true;
    }
    
    int getXMaximumSize() {
	Enumeration enumeration = childElements.elements();
	int i = 1;
	int i_94_;
	int i_95_;
	int i_93_ = i_94_ = i_95_ = 0;
	while (enumeration.hasMoreElements()) {
	    Block block = (Block) enumeration.nextElement();
	    int i_96_ = block.getMaximumSize(0);
	    int i_97_ = 0;
	    if (block instanceof Rigid)
		i_97_ = ((Rigid) block).getFlushAlign();
	    if (i_97_ != 0) {
		if (i_97_ == -1)
		    i_93_ += i_96_;
		else
		    i_94_ += i_96_;
	    } else if ((block instanceof StyleBlock
			|| block instanceof HRuleBlock)
		       && !(block instanceof NoBrBlock)) {
		i = Math.max(i, Math.max(i_96_, i_95_) + i_93_ + i_94_);
		i_95_ = 0;
	    } else if (block instanceof BRBlock) {
		i = Math.max(i, i_95_ + i_93_ + i_94_);
		i_95_ = 0;
		String string
		    = (String) block.getElement().getLocalAttribute("align");
		if (string != null) {
		    string = string.toLowerCase().intern();
		    if (string == "all" || string == "left")
			i_93_ = 0;
		    if (string == "all" || string == "right")
			i_94_ = 0;
		}
	    } else
		i_95_ += i_96_;
	}
	return Math.max(i, i_95_) + this.getLeftInset() + this.getRightInset();
    }
    
    int cheapXSize() {
	Enumeration enumeration = childElements.elements();
	int i = 1;
	while (enumeration.hasMoreElements()) {
	    Block block = (Block) enumeration.nextElement();
	    i = Math.max(i, block.getMinimumSize(0));
	}
	return i + this.getLeftInset() + this.getRightInset();
    }
    
    int cheapYSize() {
	Enumeration enumeration = childElements.elements();
	int i_98_;
	int i_99_;
	int i = i_98_ = i_99_ = 0;
	while (enumeration.hasMoreElements()) {
	    Block block = (Block) enumeration.nextElement();
	    if (block instanceof BRBlock) {
		if (i_99_++ > 0)
		    i_98_ += 10;
		i += i_98_;
		i_98_ = 0;
	    } else {
		if ((block instanceof StyleBlock
		     || block instanceof HRuleBlock)
		    && !(block instanceof NoBrBlock)) {
		    i += i_98_ + block.getPreferredSize(1);
		    i_98_ = 0;
		} else
		    i_98_ = Math.max(i_98_, block.getPreferredSize(1));
		i_99_ = 0;
	    }
	}
	return i + i_98_ + this.getLeftInset() + this.getRightInset();
    }
    
    public boolean isResizable(int i) {
	if (i != 0)
	    return false;
	return true;
    }
    
    protected boolean isOutOfBounds(int i, int i_100_, Rectangle rectangle) {
	if (i_100_ >= rectangle.y && i_100_ <= rectangle.height + rectangle.y)
	    return false;
	return true;
    }
    
    protected Block getBlockByIdx(int i, Rectangle rectangle) {
	Block block = null;
	if (rigidBlocks != null)
	    block = searchBlockByIdx(i, rigidBlocks, rigidCount, rectangle);
	if (block == null)
	    block = searchBlockByIdx(i, rowBlocks, rowCount, rectangle);
	return block;
    }
    
    Block searchBlockByIdx(int i, Block[] blocks, int i_101_,
			   Rectangle rectangle) {
	for (int i_102_ = 0; i_102_ < i_101_; i_102_++) {
	    Block block = blocks[i_102_];
	    int i_103_ = block.getStartIndex();
	    int i_104_ = block.getEndIndex();
	    if (i >= i_103_ && i < i_104_) {
		translateToChild(blocks == rigidBlocks, i_102_, rectangle);
		return block;
	    }
	}
	return null;
    }
    
    void translateToChild(boolean bool, int i, Rectangle rectangle) {
	if (bool) {
	    Rectangle rectangle_105_ = absPosition[i];
	    rectangle.x += rectangle_105_.x;
	    rectangle.y += rectangle_105_.y;
	    rectangle.width = rectangle_105_.width;
	    rectangle.height = rectangle_105_.height;
	} else {
	    int i_106_ = 0;
	    int i_107_ = 0;
	    while (i_107_ < i)
		i_106_ += rowHeights[i_107_++];
	    rectangle.y += i_106_;
	    rectangle.height = rowHeights[i];
	}
    }
    
    protected void childAllocation(int i, Rectangle rectangle) {
	/* empty */
    }
    
    protected Block getBlockByPos(int i, int i_108_, Rectangle rectangle) {
	if (getChildCount() == 0)
	    return null;
	for (int i_109_ = 0; i_109_ < rigidCount; i_109_++) {
	    Rectangle rectangle_110_ = absPosition[i_109_];
	    int i_111_ = rectangle.x + rectangle_110_.x;
	    int i_112_ = rectangle.y + rectangle_110_.y;
	    if (i_111_ <= i && i_111_ + rectangle_110_.width >= i
		&& i_112_ <= i_108_
		&& i_112_ + rectangle_110_.height >= i_108_) {
		rectangle.x = i_111_;
		rectangle.y = i_112_;
		rectangle.width = rectangle_110_.width;
		rectangle.height = rectangle_110_.height;
		return rigidBlocks[i_109_];
	    }
	}
	int i_113_ = rectangle.y;
	for (int i_114_ = 0; i_114_ < rowCount; i_114_++) {
	    i_113_ += rowHeights[i_114_];
	    if (i_113_ >= i_108_) {
		rectangle.y = i_113_ - rowHeights[i_114_];
		rectangle.height = rowHeights[i_114_];
		return rowBlocks[i_114_];
	    }
	}
	return null;
    }
}
