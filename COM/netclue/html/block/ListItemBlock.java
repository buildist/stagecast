/* ListItemBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;
import com.netclue.html.StyleFactory;

public class ListItemBlock extends StyleBlock
{
    public static final int BULLET_NONE = 0;
    public static final int BULLET_DISK = 1;
    public static final int BULLET_SQRE = 2;
    public static final int BULLET_CIRL = 3;
    private int lOffset = 10;
    private int bulletType = 1;
    private int bulletSize;
    private int margin;
    private CellBlock child;
    String bulletLabel;
    
    public ListItemBlock(AbstractElement abstractelement) {
	super(abstractelement);
	init(abstractelement);
	child = new CellBlock(abstractelement);
    }
    
    void init(AbstractElement abstractelement) {
	Integer integer
	    = (Integer) abstractelement.getLocalAttribute("bullet");
	if (integer != null)
	    bulletType = integer.intValue();
	bulletLabel = (String) abstractelement.getLocalAttribute("BSequence");
	if (bulletLabel != null)
	    bulletSize = 10;
	else
	    bulletSize = bulletType > 0 ? 6 : 0;
	margin = lOffset - 5;
	lOffset += bulletSize;
	if (abstractelement.getElementCount() > 5)
	    abstractelement.setAttribute(StyleFactory.SpaceAbove,
					 new Float(20.0F));
    }
    
    protected void createChild(BlockFactory blockfactory) {
	child.setParent(this);
    }
    
    public int getPreferredSize(int i) {
	if (child == null)
	    return lOffset;
	if (i == 0)
	    return lOffset + child.getPreferredSize(0);
	return child.getPreferredSize(1);
    }
    
    public int getMaximumSize(int i) {
	if (child == null)
	    return lOffset;
	if (i == 0)
	    return child.getMaximumSize(0) + lOffset;
	return child.getMaximumSize(1);
    }
    
    public int getMinimumSize(int i) {
	if (child == null)
	    return lOffset;
	if (i == 0)
	    return lOffset + child.getMinimumSize(0);
	return child.getMinimumSize(1);
    }
    
    public void paint(Graphics graphics, Shape shape) {
	Rectangle rectangle = shape.getBounds();
	if (bulletLabel != null) {
	    FontMetrics fontmetrics = graphics.getFontMetrics();
	    int i = lOffset - fontmetrics.stringWidth(bulletLabel) - 5;
	    graphics.setColor(Color.black);
	    graphics.drawString(bulletLabel, rectangle.x + i,
				(rectangle.y + fontmetrics.getAscent()
				 + child.getTopInset()));
	} else if (bulletType > 0) {
	    graphics.setColor(Color.darkGray);
	    graphics.fillOval(rectangle.x + margin,
			      rectangle.y + 6 + child.getTopInset(),
			      bulletSize, bulletSize);
	}
	rectangle.x += lOffset;
	rectangle.width -= lOffset;
	child.paint(graphics, rectangle);
    }
    
    public boolean isResizable(int i) {
	return true;
    }
    
    public int getChildCount() {
	return 1;
    }
    
    public Block getChild(int i) {
	if (i == 0)
	    return child;
	return null;
    }
    
    public void setSize(int i, int i_0_) {
	child.setSize(i - lOffset, i_0_);
    }
    
    protected Rectangle getInnerBounds(Shape shape) {
	if (shape != null) {
	    Rectangle rectangle = shape.getBounds();
	    rectangle.x += lOffset;
	    rectangle.width -= lOffset;
	    return rectangle;
	}
	return null;
    }
    
    protected void childAllocation(int i, Rectangle rectangle) {
	rectangle.x += lOffset;
	rectangle.width -= lOffset;
    }
    
    protected Block getBlockByIdx(int i, Rectangle rectangle) {
	if (child != null)
	    childAllocation(0, rectangle);
	return child;
    }
    
    protected Block getBlockByPos(int i, int i_1_, Rectangle rectangle) {
	if (child != null)
	    childAllocation(0, rectangle);
	return child;
    }
    
    protected boolean isOutOfBounds(int i, int i_2_, Rectangle rectangle) {
	if (i >= rectangle.x && i <= rectangle.width + rectangle.x)
	    return false;
	return true;
    }
}
