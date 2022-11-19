/* TableBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;
import com.netclue.html.HTMLConst;
import com.netclue.html.HTMLTagBag;
import com.netclue.html.StyleFactory;
import com.netclue.html.TagAttributes;
import com.netclue.html.util.HTMLUtilities;

public class TableBlock extends StyleBlock implements Rigid
{
    boolean capIsTop = true;
    Block capBlk;
    int startRow;
    int[] colWidths;
    int[] rowHeight;
    int colSize;
    int vspace;
    int hspace;
    int tableWidth = -1;
    int tableHeight = -1;
    int lastGivenHeight;
    int maxTabWidth;
    int minWidth;
    int minHeight;
    private int widthRatio;
    boolean fixedTableWidth = false;
    boolean fixedTableHeight = false;
    protected int cellBorder;
    protected int cellSpacing = 2;
    protected int cellPadding = 1;
    protected int cellStuffing;
    private int flushAligned;
    private float alignment;
    Color bgcolor;
    Color borderColor;
    private CellTree colTree;
    private CellTree rowTree;
    private boolean xValid;
    private boolean yValid;
    private boolean xAllocValid;
    private boolean yAllocValid;
    
    public class TableRow extends StackBlock
    {
	protected Color bgcolor;
	
	public TableRow(AbstractElement abstractelement) {
	    super(abstractelement, 0);
	    this.alignment[0] = 0.0F;
	    this.alignment[1] = 0.0F;
	    TagAttributes tagattributes = abstractelement.getAttributeNode();
	    if (tagattributes != null)
		extractTagAttributes(tagattributes);
	}
	
	private void extractTagAttributes(TagAttributes tagattributes) {
	    String string = (String) tagattributes.getAttribute("bgcolor");
	    if (string != null)
		bgcolor = HTMLUtilities.stringToColor(string);
	}
	
	public void paint(Graphics graphics, Shape shape) {
	    Rectangle rectangle = shape.getBounds();
	    rectangle.x += cellSpacing;
	    rectangle.y += cellSpacing;
	    int i = this.getChildCount();
	    for (int i_0_ = 0; i_0_ < i; i_0_++) {
		Block block = this.getChild(i_0_);
		block.paint(graphics, rectangle);
		rectangle.x += colWidths[i_0_];
	    }
	}
	
	protected void childAllocation(int i, Rectangle rectangle) {
	    TableCell tablecell = (TableCell) this.getChild(i);
	    int i_1_ = tablecell.row;
	    if (tablecell instanceof ShadowCell)
		tablecell = ((ShadowCell) tablecell).getHostView();
	    int i_2_ = tablecell.col;
	    int i_3_ = 0;
	    while (i_3_ < i_2_)
		rectangle.x += colWidths[i_3_++];
	    for (i_3_ = i_1_ - tablecell.row; i_3_ > 0; i_3_--)
		rectangle.y -= rowHeight[i_1_ - i_3_];
	    rectangle.width = rectangle.height = -cellStuffing;
	    for (i_3_ = 0; i_3_ < tablecell.colSpan; i_3_++)
		rectangle.width += colWidths[tablecell.col + i_3_];
	    for (i_3_ = 0; i_3_ < tablecell.rowSpan; i_3_++)
		rectangle.height += rowHeight[tablecell.row + i_3_];
	}
	
	protected Block getBlockByPos(int i, int i_4_, Rectangle rectangle) {
	    if (i < rectangle.x)
		return null;
	    int i_5_ = this.getChildCount();
	    int i_6_ = cellPadding + cellBorder;
	    int i_7_ = rectangle.x;
	    int i_8_;
	    for (i_8_ = 0; i_8_ < i_5_; i_8_++) {
		i_7_ += colWidths[i_8_];
		if (i <= i_7_)
		    break;
	    }
	    if (i_8_ == i_5_)
		return null;
	    rectangle.x += i_6_;
	    rectangle.y += i_6_;
	    childAllocation(i_8_, rectangle);
	    Block block = this.getChild(i_8_);
	    if (block instanceof ShadowCell)
		block = ((ShadowCell) block).host;
	    return block;
	}
	
	protected void createChild(BlockFactory blockfactory) {
	    AbstractElement abstractelement = this.getElement();
	    int i = abstractelement.getElementCount();
	    if (i > 0) {
		Block[] blocks = new Block[i];
		int i_9_ = 0;
		for (int i_10_ = 0; i_10_ < i; i_10_++) {
		    AbstractElement abstractelement_11_
			= abstractelement.getElement(i_10_);
		    int i_12_ = abstractelement_11_.getTagCode();
		    if (i_12_ == 63 || i_12_ == 64) {
			blocks[i_9_] = new TableCell(abstractelement_11_);
			blocks[i_9_++].setParent(this);
		    }
		}
		children = blocks;
		nchildren = i_9_;
	    }
	}
	
	public int getPreferredSize(int i) {
	    if (i == 0)
		return tableWidth;
	    return super.getPreferredSize(i);
	}
    }
    
    public class TableCell extends StyleBlock
    {
	public int row;
	public int col;
	int cellWidth = -1;
	int widthRatio;
	int cellHeight = -1;
	int colSpan;
	int rowSpan;
	int yOffset;
	float[] alignment = new float[2];
	boolean fixWidth = false;
	boolean fixHeight = false;
	Color bgcolor;
	Block child;
	
	public TableCell(AbstractElement abstractelement) {
	    this(abstractelement, false);
	}
	
	public TableCell(AbstractElement abstractelement, boolean bool) {
	    super(abstractelement);
	    colSpan = rowSpan = 1;
	    if (!bool) {
		alignment[1] = 0.5F;
		TagAttributes tagattributes
		    = abstractelement.getAttributeNode();
		if (tagattributes != null)
		    extractTagAttributes(tagattributes);
	    }
	}
	
	private void extractTagAttributes(TagAttributes tagattributes) {
	    String string = (String) tagattributes.getAttribute("valign");
	    if (string != null) {
		if (string.equalsIgnoreCase("top"))
		    alignment[1] = 0.0F;
		else if (string.equalsIgnoreCase("bottom"))
		    alignment[1] = 1.0F;
	    }
	    string = (String) tagattributes.getAttribute("colspan");
	    if (string != null)
		colSpan = Math.max(1, HTMLUtilities.stringToInt(string));
	    string = (String) tagattributes.getAttribute("rowspan");
	    if (string != null)
		rowSpan = Math.max(1, HTMLUtilities.stringToInt(string));
	    string = (String) tagattributes.getAttribute("width");
	    if (string != null) {
		int i = HTMLUtilities.stringToRatioInt(string);
		if (i < 1000) {
		    cellWidth = i;
		    if (cellWidth > 0) {
			fixWidth = true;
			if (fixedTableWidth && tableWidth < cellWidth) {
			    fixWidth = false;
			    cellWidth = 0;
			}
		    } else
			widthRatio = -cellWidth;
		}
	    }
	    string = (String) tagattributes.getAttribute("height");
	    if (string != null) {
		int i = HTMLUtilities.stringToRatioInt(string);
		if (i > 0) {
		    cellHeight = i;
		    fixHeight = true;
		}
	    }
	    string = (String) tagattributes.getAttribute("bgcolor");
	    if (string != null)
		bgcolor = HTMLUtilities.stringToColor(string);
	}
	
	public boolean isFixedCell(int i) {
	    if (i == 0)
		return fixWidth;
	    return fixHeight;
	}
	
	public int getWidthRatio() {
	    return widthRatio / colSpan;
	}
	
	public int getColspan() {
	    return colSpan;
	}
	
	protected void setColumnCount(int i) {
	    colSpan = i;
	}
	
	public int getRowCount() {
	    return rowSpan;
	}
	
	protected void setRowCount(int i) {
	    rowSpan = i;
	}
	
	public void setGridLocation(int i, int i_13_) {
	    row = i;
	    col = i_13_;
	}
	
	protected void createChild(BlockFactory blockfactory) {
	    AbstractElement abstractelement = this.getElement();
	    child = new CellBlock(abstractelement);
	    child.setParent(this);
	}
	
	public int getChildCount() {
	    return child.getChildCount();
	}
	
	public Block getChild(int i) {
	    return child.getChild(i);
	}
	
	public int getPreferredSize(int i) {
	    if (i == 0) {
		if (fixWidth)
		    return cellWidth;
		return child.getPreferredSize(i) + cellStuffing;
	    }
	    int i_14_ = child.getPreferredSize(1) + cellStuffing;
	    if (fixHeight)
		return Math.max(i_14_, cellHeight);
	    return i_14_;
	}
	
	public int getMinimumSize(int i) {
	    int i_15_ = Math.max(1, child.getMinimumSize(i)) + cellStuffing;
	    if (i == 0) {
		if (fixWidth)
		    cellWidth = Math.max(cellWidth, i_15_);
	    } else if (fixHeight)
		i_15_ = cellHeight = Math.max(cellHeight, i_15_);
	    return i_15_;
	}
	
	public int getMaximumSize(int i) {
	    if (i == 0) {
		if (fixWidth)
		    return cellWidth;
		return Math.max(1, child.getMaximumSize(0)) + cellStuffing;
	    }
	    return child.getMaximumSize(1) + cellStuffing;
	}
	
	public void sizeChanged(boolean bool, boolean bool_16_) {
	    TagAttributes tagattributes = this.getElement().getAttributeNode();
	    if (bool && fixWidth && tagattributes != null) {
		String string = (String) tagattributes.getAttribute("width");
		int i = HTMLUtilities.stringToInt(string);
		if (i < 1000)
		    cellWidth = i;
	    }
	    if (bool_16_ && fixHeight && tagattributes != null) {
		String string = (String) tagattributes.getAttribute("height");
		cellHeight = HTMLUtilities.stringToInt(string);
	    }
	    cellChanged(this, bool, bool_16_, col, col + colSpan, row,
			row + rowSpan);
	    super.sizeChanged(bool, bool_16_);
	}
	
	public void setSize(int i, int i_17_) {
	    int i_18_ = i_17_ - cellStuffing;
	    child.setSize(i - cellStuffing, i_18_);
	    int i_19_ = child.getPreferredSize(1);
	    yOffset = (int) ((float) (i_18_ - i_19_) * alignment[1]);
	}
	
	public void paint(Graphics graphics, Shape shape) {
	    Rectangle rectangle = shape.getBounds();
	    int i = getRowCount();
	    int i_20_ = getColspan();
	    int i_21_ = cellPadding + cellBorder;
	    int i_23_;
	    int i_22_ = i_23_ = 0;
	    for (int i_24_ = 0; i_24_ < i_20_; i_24_++)
		i_23_ += getColumnWidth(col + i_24_);
	    for (int i_25_ = 0; i_25_ < i; i_25_++)
		i_22_ += getRowSpan(row + i_25_);
	    setSize(i_23_, i_22_);
	    i_23_ -= cellSpacing;
	    i_22_ -= cellSpacing;
	    if (bgcolor == null) {
		TableRow tablerow = (TableRow) this.getParent();
		bgcolor = tablerow.bgcolor;
	    }
	    if (bgcolor != null) {
		graphics.setColor(bgcolor);
		graphics.fillRect(rectangle.x, rectangle.y, i_23_, i_22_);
	    }
	    int i_26_ = rectangle.x;
	    int i_27_ = rectangle.y;
	    rectangle.x += i_21_;
	    rectangle.y += i_21_ + yOffset;
	    rectangle.width = i_23_ - (i_21_ << 1);
	    rectangle.height = i_22_ - (i_21_ << 1);
	    child.paint(graphics, rectangle);
	    if (cellBorder > 0) {
		rectangle.x = i_26_;
		rectangle.y = i_27_;
		rectangle.width = i_23_;
		rectangle.height = i_22_;
		paintBorder(graphics, rectangle, cellBorder, borderColor,
			    false);
	    }
	}
	
	public Rectangle findBounds(int i, Rectangle rectangle) {
	    rectangle.y += yOffset;
	    return child.findBounds(i, rectangle);
	}
	
	public int getDocIndex(int i, int i_28_, Shape shape) {
	    Rectangle rectangle = shape.getBounds();
	    rectangle.y += yOffset;
	    return child.getDocIndex(i, i_28_, rectangle);
	}
	
	protected boolean isOutOfBounds(int i, int i_29_,
					Rectangle rectangle) {
	    return false;
	}
	
	protected Block getBlockByPos(int i, int i_30_, Rectangle rectangle) {
	    return null;
	}
	
	protected void childAllocation(int i, Rectangle rectangle) {
	    /* empty */
	}
    }
    
    class ShadowCell extends TableCell
    {
	TableCell host;
	
	ShadowCell(TableCell tablecell) {
	    super(tablecell.getElement(), true);
	    host = tablecell;
	}
	
	protected void createChild(BlockFactory blockfactory) {
	    /* empty */
	}
	
	public int getColspan() {
	    return 1;
	}
	
	public AbstractElement getElement() {
	    return null;
	}
	
	public int getRowCount() {
	    return 1;
	}
	
	public boolean isResizable(int i) {
	    return host.isResizable(i);
	}
	
	public void paint(Graphics graphics, Shape shape) {
	    /* empty */
	}
	
	public void setSize(int i, int i_31_) {
	    /* empty */
	}
	
	public void setParent(Block block) {
	    /* empty */
	}
	
	public int getChildCount() {
	    return 0;
	}
	
	public TableCell getHostView() {
	    return host;
	}
	
	public Rectangle findBounds(int i, Rectangle rectangle) {
	    return host.findBounds(i, rectangle);
	}
	
	public boolean isFixedCell(int i) {
	    return host.isFixedCell(i);
	}
	
	public int getWidthRatio() {
	    return host.getWidthRatio();
	}
    }
    
    public TableBlock(AbstractElement abstractelement) {
	super(abstractelement);
	retrieveAttributes(abstractelement);
	cellStuffing = (cellPadding + cellBorder << 1) + cellSpacing;
    }
    
    private void retrieveAttributes(AbstractElement abstractelement) {
	TagAttributes tagattributes = abstractelement.getAttributeNode();
	if (tagattributes != null) {
	    String string
		= (String) tagattributes.getAttribute(HTMLConst.width);
	    if (string != null) {
		int i = HTMLUtilities.stringToRatioInt(string);
		if (i > 0) {
		    tableWidth = i;
		    fixedTableWidth = true;
		} else
		    widthRatio = -i;
	    }
	    string = (String) tagattributes.getAttribute(HTMLConst.height);
	    if (string != null) {
		tableHeight = HTMLUtilities.stringToInt(string);
		fixedTableHeight = true;
	    }
	    string = (String) tagattributes.getAttribute("cellspacing");
	    if (string != null)
		cellSpacing
		    = Math.max(0, HTMLUtilities.stringToRatioInt(string));
	    string = (String) tagattributes.getAttribute("cellpadding");
	    if (string != null)
		cellPadding
		    = Math.max(0, HTMLUtilities.stringToRatioInt(string));
	    string = (String) tagattributes.getAttribute("border");
	    if (string != null) {
		if (string.length() == 0)
		    cellBorder = 1;
		else
		    cellBorder
			= Math.max(0, HTMLUtilities.stringToRatioInt(string));
	    }
	    string = (String) tagattributes.getAttribute("vspace");
	    vspace = string == null ? 0 : HTMLUtilities.stringToInt(string);
	    string = (String) tagattributes.getAttribute("hspace");
	    hspace = string == null ? 0 : HTMLUtilities.stringToInt(string);
	    String string_32_
		= (String) tagattributes.getAttribute(HTMLConst.align);
	    if (string_32_ != null) {
		String string_33_ = string_32_.toLowerCase();
		if (string_33_.equals("center"))
		    alignment = 0.5F;
		else if (string_33_.equals("right")) {
		    flushAligned = 1;
		    alignment = 1.0F;
		} else if (string_33_.equals("left"))
		    flushAligned = -1;
	    }
	    string = (String) tagattributes.getAttribute("bgcolor");
	    if (string != null)
		bgcolor = HTMLUtilities.stringToColor(string);
	    string = (String) tagattributes.getAttribute("bordercolor");
	    if (string != null)
		borderColor = HTMLUtilities.stringToColor(string);
	}
    }
    
    public boolean isFixedWidth() {
	return fixedTableWidth;
    }
    
    public int getFlushAlign() {
	return flushAligned;
    }
    
    protected boolean isOutOfBounds(int i, int i_34_, Rectangle rectangle) {
	if (i >= rectangle.x && i <= rectangle.width + rectangle.x
	    && i_34_ >= rectangle.y && i_34_ <= rectangle.height + rectangle.y)
	    return false;
	return true;
    }
    
    public int getWidthRatio() {
	return widthRatio;
    }
    
    public boolean isResizable(int i) {
	if (i == 0) {
	    if (fixedTableWidth)
		return false;
	    return true;
	}
	return false;
    }
    
    public float getAlignment(int i) {
	if (i == 0)
	    return alignment;
	return 0.0F;
    }
    
    int getColumnWidth(int i) {
	return colWidths[i];
    }
    
    int getRowSpan(int i) {
	return rowHeight[i];
    }
    
    public synchronized void refreshCache() {
	int i = (cellBorder << 1) + cellSpacing;
	if (!xValid) {
	    if (colTree == null)
		buildColTree();
	    colTree.updateValues();
	    minWidth = colTree.getMinimumSize() + i;
	    if (fixedTableWidth) {
		TagAttributes tagattributes
		    = this.getElement().getAttributeNode();
		String string
		    = (String) tagattributes.getAttribute(HTMLConst.width);
		tableWidth
		    = Math.max(minWidth, HTMLUtilities.stringToInt(string));
	    } else
		tableWidth
		    = Math.max(tableWidth, colTree.getPreferredSize() + i);
	    if (!fixedTableWidth)
		maxTabWidth
		    = Math.max(tableWidth, colTree.getMaximumSize() + i);
	    else
		maxTabWidth = tableWidth;
	    xValid = true;
	}
	if (!yValid) {
	    buildRowTree();
	    rowTree.updateValues();
	    minHeight = rowTree.getMinimumSize() + i;
	    if (fixedTableHeight) {
		TagAttributes tagattributes
		    = this.getElement().getAttributeNode();
		String string
		    = (String) tagattributes.getAttribute(HTMLConst.height);
		tableHeight
		    = Math.max(minHeight, HTMLUtilities.stringToInt(string));
	    } else
		tableHeight = rowTree.getPreferredSize() + i;
	    yValid = true;
	}
    }
    
    public int getPreferredSize(int i) {
	refreshCache();
	if (i == 0)
	    return tableWidth + (hspace << 1);
	int i_35_ = tableHeight + (vspace << 1);
	if (capBlk != null)
	    i_35_ += capBlk.getPreferredSize(1);
	return i_35_;
    }
    
    int getTRowCount() {
	int i = this.getChildCount();
	if (capBlk != null)
	    i--;
	return i;
    }
    
    protected void createChild(BlockFactory blockfactory) {
	if (this.getChildCount() <= 0) {
	    AbstractElement abstractelement = this.getElement();
	    int i = abstractelement.getElementCount();
	    if (i > 0) {
		Block[] blocks = new Block[i];
		int i_36_ = 0;
		int i_37_ = 0;
		while (i_37_ < i) {
		    AbstractElement abstractelement_38_
			= abstractelement.getElement(i_37_++);
		    if (abstractelement_38_.getTagCode()
			== HTMLTagBag.captionID) {
			abstractelement_38_.setAttribute((StyleFactory
							  .Alignment),
							 new Integer(1));
			String string
			    = ((String)
			       abstractelement_38_.getLocalAttribute("align"));
			if (string != null
			    && string.equalsIgnoreCase("bottom"))
			    capIsTop = false;
			else
			    startRow = 1;
			capBlk = blockfactory.create(abstractelement_38_);
			capBlk.setParent(this);
		    } else {
			blocks[i_36_] = new TableRow(abstractelement_38_);
			blocks[i_36_++].setParent(this);
		    }
		}
		if (capBlk == null || !capIsTop) {
		    if (!capIsTop)
			blocks[i - 1] = capBlk;
		    children = blocks;
		} else {
		    children = new Block[i];
		    children[0] = capBlk;
		    System.arraycopy(blocks, 0, children, 1, i - 1);
		}
		nchildren = i;
	    }
	    int i_39_ = 0;
	    if (capBlk != null) {
		i--;
		if (capIsTop)
		    i_39_ = 1;
	    }
	    rowHeight = new int[i];
	    colSize = findTotalCols();
	    TableCell[][] tablecells = new TableCell[i][colSize];
	    for (int i_40_ = 0; i_40_ < i; i_40_++) {
		TableRow tablerow = (TableRow) this.getChild(i_40_ + i_39_);
		int i_41_ = 0;
		int i_42_ = 0;
		for (/**/; i_41_ < tablerow.getChildCount(); i_41_++) {
		    Block block = tablerow.getChild(i_41_);
		    if (block instanceof TableCell) {
			TableCell tablecell = (TableCell) block;
			for (/**/; tablecells[i_40_][i_42_] != null; i_42_++) {
			    /* empty */
			}
			tablecells[i_40_][i_42_] = tablecell;
			if (tablecell.getColspan() > 1
			    || tablecell.getRowCount() > 1) {
			    int i_43_
				= Math.min(i, i_40_ + tablecell.getRowCount());
			    int i_44_
				= Math.min(colSize,
					   i_42_ + tablecell.getColspan());
			    for (int i_45_ = i_40_; i_45_ < i_43_; i_45_++) {
				for (int i_46_ = i_42_; i_46_ < i_44_;
				     i_46_++) {
				    if (i_45_ != i_40_ || i_46_ != i_42_)
					tablecells[i_45_][i_46_]
					    = new ShadowCell(tablecell);
				}
			    }
			    i_42_ = i_44_;
			} else
			    i_42_++;
		    }
		}
	    }
	    addShadows(tablecells);
	    colWidths = new int[colSize];
	}
    }
    
    private int findTotalCols() {
	int i = getTRowCount();
	boolean bool = true;
	int i_48_;
	int i_47_ = i_48_ = 0;
	int[] is = new int[i];
	TableCell[] tablecells = new TableCell[i];
	for (int i_49_ = 0; i_49_ < i; i_49_++) {
	    TableRow tablerow = (TableRow) this.getChild(i_49_ + startRow);
	    int i_50_ = tablerow.getChildCount() - 1;
	    int i_51_ = 0;
	    for (int i_52_ = 0; i_52_ <= i_50_; i_52_++) {
		TableCell tablecell = (TableCell) tablerow.getChild(i_52_);
		i_51_ += tablecell.getColspan();
	    }
	    if (i_49_ != 0)
		bool = bool & i_47_ == i_51_;
	    else
		bool = i_51_ > 1;
	    i_47_ = Math.max(i_47_, i_51_);
	}
	int[] is_53_ = new int[i_47_ + 4];
	for (int i_54_ = 0; i_54_ < i; i_54_++) {
	    TableRow tablerow = (TableRow) this.getChild(i_54_ + startRow);
	    int i_55_ = tablerow.getChildCount() - 1;
	    int i_56_ = 0;
	    for (int i_57_ = 0; i_57_ <= i_55_; i_57_++) {
		TableCell tablecell = (TableCell) tablerow.getChild(i_57_);
		for (/**/; i_54_ < is_53_[i_56_]; i_56_++) {
		    /* empty */
		}
		int i_58_ = i_54_ + tablecell.getRowCount();
		if (i_58_ > i) {
		    tablecell.setRowCount(i - i_54_);
		    i_58_ = i;
		}
		int i_59_ = tablecell.getColspan();
		for (int i_60_ = 0; i_60_ < i_59_; i_60_++)
		    is_53_[i_56_ + i_60_] = i_58_;
		if (i_57_ == i_55_) {
		    tablecells[i_54_] = tablecell;
		    is[i_54_] = i_56_;
		    i_48_ = Math.max(i_48_, i_56_);
		    i_47_ = Math.max(i_47_, i_56_ + i_59_);
		}
	    }
	    boolean bool_61_ = bool && is_53_[0] > i_54_ + 1;
	    for (int i_62_ = 1; bool_61_ && i_62_ < i_47_; i_62_++)
		bool_61_ = bool_61_ & is_53_[i_62_] == is_53_[i_62_ - 1];
	    if (bool_61_) {
		int i_63_ = is_53_[0] - i_54_ - 1;
		for (int i_64_ = 0; i_64_ <= i_55_; i_64_++) {
		    TableCell tablecell = (TableCell) tablerow.getChild(i_64_);
		    tablecell.setRowCount(tablecell.getRowCount() - i_63_);
		}
		for (int i_65_ = 0; i_65_ < i_47_; i_65_++)
		    is_53_[i_65_] -= i_63_;
	    }
	}
	i_48_++;
	if (i_47_ > i_48_) {
	    for (int i_66_ = 0; i_66_ < i; i_66_++) {
		TableCell tablecell = tablecells[i_66_];
		if (tablecell.getColspan() + is[i_66_] > i_48_)
		    tablecell.setColumnCount(i_48_ - is[i_66_]);
	    }
	    i_47_ = i_48_;
	}
	return i_47_;
    }
    
    void layoutRows(int i) {
	int i_67_ = (cellBorder << 1) + cellSpacing;
	int i_68_ = i - i_67_;
	int i_69_ = getTRowCount();
	int[] is = new int[i_69_];
	rowTree.layoutNodes(i_68_);
	rowTree.getLength(is);
	rowHeight = is;
    }
    
    void layoutColumns(int i) {
	int i_70_ = (cellBorder << 1) + cellSpacing;
	int i_71_ = tableWidth - i_70_;
	int i_72_ = (fixedTableWidth ? tableWidth : i) - i_70_;
	int[] is = new int[colSize];
	colTree.layoutNodes(i_72_);
	colTree.getLength(is);
	colWidths = is;
	int i_73_;
	tableWidth = i_73_ = 0;
	while (i_73_ < colSize)
	    tableWidth += colWidths[i_73_++];
	if (i_71_ != tableWidth)
	    this.getParent().sizeChanged(true, false);
	tableWidth += i_70_;
    }
    
    private void addShadows(TableCell[][] tablecells) {
	int i = getTRowCount();
	for (int i_74_ = 0; i_74_ < i; i_74_++) {
	    TableRow tablerow = (TableRow) this.getChild(i_74_ + startRow);
	    TableCell tablecell = null;
	    for (int i_75_ = 0; i_75_ < colSize; i_75_++) {
		if (tablecells[i_74_][i_75_] == null) {
		    tablecells[i_74_][i_75_] = new ShadowCell(tablecell);
		    if (tablecell.getRowCount() == 1)
			tablecell.setColumnCount(tablecell.getColspan() + 1);
		}
		if (tablecells[i_74_][i_75_] instanceof ShadowCell)
		    tablerow.insert(i_75_, tablecells[i_74_][i_75_]);
		else
		    tablecell = tablecells[i_74_][i_75_];
		tablecells[i_74_][i_75_].setGridLocation(i_74_, i_75_);
	    }
	}
    }
    
    private void buildColTree() {
	int i = getTRowCount();
	colTree = new CellTree(0, colSize);
	try {
	    for (int i_76_ = 0; i_76_ < i; i_76_++) {
		Block block = this.getChild(i_76_ + startRow);
		TableCell tablecell;
		for (int i_77_ = 0; i_77_ < colSize;
		     i_77_ += tablecell.getColspan()) {
		    tablecell = (TableCell) block.getChild(i_77_);
		    if (!(tablecell instanceof ShadowCell))
			colTree.updateNode(tablecell, tablecell.col,
					   (tablecell.col
					    + tablecell.getColspan()));
		}
	    }
	} catch (Exception exception) {
	    /* empty */
	}
    }
    
    private void buildRowTree() {
	int i = getTRowCount();
	rowTree = new CellTree(1, i);
	try {
	    for (int i_78_ = 0; i_78_ < i; i_78_++) {
		Block block = this.getChild(i_78_ + startRow);
		TableCell tablecell;
		for (int i_79_ = 0; i_79_ < colSize;
		     i_79_ += tablecell.getColspan()) {
		    tablecell = (TableCell) block.getChild(i_79_);
		    if (!(tablecell instanceof ShadowCell))
			rowTree.updateNode(tablecell, tablecell.row,
					   (tablecell.row
					    + tablecell.getRowCount()));
		}
	    }
	} catch (Exception exception) {
	    /* empty */
	}
    }
    
    public void setSize(int i, int i_80_) {
	TableBlock tableblock_81_ = this;
	tableblock_81_.xAllocValid
	    = tableblock_81_.xAllocValid & i == tableWidth;
	TableBlock tableblock_82_ = this;
	tableblock_82_.yAllocValid
	    = tableblock_82_.yAllocValid & i_80_ == lastGivenHeight;
	if ((!xAllocValid || !yAllocValid) && colSize > 0) {
	    layout(i - (hspace << 1), i_80_ - (vspace << 1));
	    lastGivenHeight = i_80_;
	}
    }
    
    void layoutCells() {
	int i = getTRowCount();
	for (int i_83_ = 0; i_83_ < i; i_83_++) {
	    Block block = this.getChild(i_83_ + startRow);
	    int i_84_ = block.getChildCount();
	    for (int i_85_ = 0; i_85_ < i_84_; i_85_++) {
		TableCell tablecell = (TableCell) block.getChild(i_85_);
		int i_86_ = tablecell.getColspan();
		int i_88_;
		int i_87_ = i_88_ = 0;
		for (/**/; i_88_ < i_86_; i_88_++)
		    i_87_ += colWidths[i_85_ + i_88_];
		i_86_ = tablecell.getRowCount();
		int i_89_ = i_88_ = 0;
		for (/**/; i_88_ < i_86_; i_88_++)
		    i_89_ += rowHeight[i_83_ + i_88_];
		tablecell.setSize(i_87_, i_89_);
	    }
	}
    }
    
    protected void layout(int i, int i_90_) {
	int i_91_ = getPreferredSize(1);
	if (!xAllocValid) {
	    layoutColumns(i);
	    layoutCells();
	}
	if (!xAllocValid || !yAllocValid) {
	    int i_92_ = 0;
	    if (capBlk != null) {
		capBlk.setSize(i, i_90_);
		i_92_ = capBlk.getPreferredSize(1);
	    }
	    yValid &= xAllocValid;
	    int i_93_ = getPreferredSize(1);
	    layoutRows(i_93_ - i_92_);
	    if (i_93_ != i_91_)
		this.getParent().sizeChanged(false, true);
	}
	xAllocValid = yAllocValid = true;
    }
    
    public int getMinimumSize(int i) {
	refreshCache();
	if (i == 0) {
	    if (fixedTableWidth)
		return tableWidth;
	    return minWidth + (hspace << 1);
	}
	int i_94_ = minHeight + (vspace << 1);
	if (capBlk != null)
	    i_94_ += capBlk.getMinimumSize(1);
	return i_94_;
    }
    
    public int getMaximumSize(int i) {
	if (i == 0) {
	    refreshCache();
	    return maxTabWidth + (hspace << 1);
	}
	return getPreferredSize(1);
    }
    
    protected void trimSpace(Rectangle rectangle) {
	rectangle.x += hspace;
	rectangle.y += vspace;
	rectangle.width -= hspace << 1;
	rectangle.height -= vspace << 1;
    }
    
    void paintBorder(Graphics graphics, Rectangle rectangle, int i,
		     Color color, boolean bool) {
	rectangle.width--;
	rectangle.height--;
	if (color == null) {
	    int i_95_ = rectangle.x;
	    int i_96_ = rectangle.y;
	    int i_97_ = i_95_ + rectangle.width;
	    int i_98_ = i_96_ + rectangle.height;
	    Color color_99_ = bool ? Color.lightGray : Color.gray;
	    Color color_100_ = bool ? Color.gray : Color.lightGray;
	    for (int i_101_ = 0; i_101_ < i; i_101_++) {
		graphics.setColor(color_99_);
		graphics.drawLine(i_95_, i_96_, i_95_, i_98_);
		graphics.drawLine(i_95_, i_96_, i_97_, i_96_);
		graphics.setColor(color_100_);
		graphics.drawLine(i_95_, i_98_, i_97_, i_98_);
		graphics.drawLine(i_97_, i_98_, i_97_, i_96_);
		i_95_++;
		i_96_++;
		i_97_--;
		i_98_--;
	    }
	    rectangle.x += i;
	    rectangle.y += i;
	    rectangle.width -= i << 1;
	    rectangle.height -= i << 1;
	} else {
	    graphics.setColor(color);
	    for (int i_102_ = 0; i_102_ < i; i_102_++) {
		graphics.drawRect(rectangle.x, rectangle.y, rectangle.width,
				  rectangle.height);
		rectangle.x++;
		rectangle.y++;
		rectangle.width -= 2;
		rectangle.height -= 2;
	    }
	}
    }
    
    Rectangle adjustCaptionSpace(Rectangle rectangle) {
	Rectangle rectangle_103_ = new Rectangle(rectangle);
	rectangle_103_.height -= tableHeight;
	rectangle.height = tableHeight;
	if (capIsTop)
	    rectangle.y += rectangle_103_.height;
	else
	    rectangle_103_.y += tableHeight;
	return rectangle_103_;
    }
    
    public void paint(Graphics graphics, Shape shape) {
	Rectangle rectangle = shape.getBounds();
	trimSpace(rectangle);
	if (capBlk != null) {
	    Rectangle rectangle_104_ = adjustCaptionSpace(rectangle);
	    capBlk.paint(graphics, rectangle_104_);
	}
	if (bgcolor != null) {
	    graphics.setColor(bgcolor);
	    graphics.fillRect(rectangle.x, rectangle.y, rectangle.width,
			      rectangle.height);
	}
	if (cellBorder > 0)
	    paintBorder(graphics, rectangle, cellBorder, borderColor, true);
	int i = getTRowCount();
	int i_105_ = rectangle.y;
	for (int i_106_ = 0; i_106_ < i; i_106_++) {
	    rectangle.y = i_105_;
	    this.getChild(i_106_ + startRow).paint(graphics, rectangle);
	    i_105_ += rowHeight[i_106_];
	}
    }
    
    protected void cellChanged(TableCell tablecell, boolean bool,
			       boolean bool_107_, int i, int i_108_,
			       int i_109_, int i_110_) {
	if (bool)
	    colTree.updateCellValue(tablecell, i, i_108_);
    }
    
    public void sizeChanged(boolean bool, boolean bool_111_) {
	if (bool)
	    xValid = xAllocValid = false;
	if (bool_111_)
	    yValid = yAllocValid = false;
	super.sizeChanged(bool, bool_111_);
    }
    
    protected void childAllocation(int i, Rectangle rectangle) {
	Block block = this.getChild(i);
	if (block == capBlk) {
	    Rectangle rectangle_112_ = adjustCaptionSpace(rectangle);
	    rectangle.x = rectangle_112_.x;
	    rectangle.y = rectangle_112_.y;
	    rectangle.width = rectangle_112_.width;
	    rectangle.height = rectangle_112_.height;
	} else {
	    int i_113_ = cellBorder + cellSpacing;
	    int i_114_ = i_113_;
	    i -= startRow;
	    int i_115_ = 0;
	    while (i_115_ < i)
		i_114_ += rowHeight[i_115_++];
	    rectangle.x += i_113_;
	    rectangle.y += i_114_;
	    rectangle.width = tableWidth - (i_113_ << 1);
	    rectangle.height = rowHeight[i];
	}
    }
    
    public int getDocIndex(int i, int i_116_, Shape shape) {
	Rectangle rectangle = shape.getBounds();
	trimSpace(rectangle);
	if (capBlk != null) {
	    Rectangle rectangle_117_ = adjustCaptionSpace(rectangle);
	    if (!((CellBlock) capBlk).isOutOfBounds(i, i_116_, rectangle_117_))
		return capBlk.getDocIndex(i, i_116_, rectangle_117_);
	}
	int i_118_ = cellBorder + cellSpacing;
	if (i > rectangle.x + i_118_ && i < rectangle.x + tableWidth - i_118_
	    && i_116_ > rectangle.y + i_118_
	    && i_116_ < rectangle.y + rectangle.height - i_118_) {
	    Block block = getBlockByPos(i, i_116_, rectangle);
	    if (block != null)
		return block.getDocIndex(i, i_116_, rectangle);
	}
	return -1;
    }
    
    protected Block getBlockByPos(int i, int i_119_, Rectangle rectangle) {
	int i_120_ = getTRowCount();
	int i_121_ = rectangle.y + cellBorder;
	for (int i_122_ = 0; i_122_ < i_120_; i_122_++) {
	    i_121_ += rowHeight[i_122_];
	    if (i_119_ <= i_121_) {
		rectangle.y = i_121_ - rowHeight[i_122_];
		rectangle.height = rowHeight[i_122_];
		return this.getChild(i_122_ + startRow);
	    }
	}
	return null;
    }
}
