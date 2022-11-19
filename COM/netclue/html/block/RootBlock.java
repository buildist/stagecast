/* RootBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;
import com.netclue.html.BaseDocument;
import com.netclue.html.CLHtmlPane;
import com.netclue.html.widget.PanView;

public class RootBlock extends Block
{
    CLHtmlPane editor;
    private Block blk;
    
    public RootBlock(CLHtmlPane clhtmlpane) {
	super((AbstractElement) null);
	editor = clhtmlpane;
    }
    
    public void setBlock(Block block) {
	blk = block;
	if (blk != null)
	    blk.setParent(this);
    }
    
    public boolean isNew() {
	if (blk == null || ((CellBlock) blk).rowCount == 0)
	    return true;
	return false;
    }
    
    public int getPreferredSize(int i) {
	if (blk != null)
	    return blk.getPreferredSize(i);
	return 10;
    }
    
    public void sizeChanged(boolean bool, boolean bool_0_) {
	editor.invalidate();
	Container container = editor.getParent();
	if (container != null && container instanceof PanView) {
	    container.getParent().validate();
	    container.getParent().repaint();
	}
    }
    
    public float getAlignment(int i) {
	if (blk != null)
	    return blk.getAlignment(i);
	return 0.0F;
    }
    
    public void paint(Graphics graphics, Shape shape) {
	if (blk != null) {
	    Rectangle rectangle = shape.getBounds();
	    blk.setSize(rectangle.width, rectangle.height);
	    blk.paint(graphics, shape);
	}
    }
    
    public void setParent(Block block) {
	/* empty */
    }
    
    public int getChildCount() {
	return 1;
    }
    
    public Block getChild(int i) {
	return blk;
    }
    
    public Rectangle findBounds(int i, Rectangle rectangle) {
	if (blk != null)
	    return blk.findBounds(i, rectangle);
	return null;
    }
    
    public int getDocIndex(int i, int i_1_, Shape shape) {
	if (blk != null)
	    return blk.getDocIndex(i, i_1_, shape);
	return -1;
    }
    
    public Block getBlockByIndex(int i, Rectangle rectangle) {
	if (blk != null) {
	    Block block = blk;
	    while (block instanceof StyleBlock) {
		StyleBlock styleblock = (StyleBlock) block;
		if ((block = styleblock.getBlockByIdx(i, rectangle)) == null)
		    return null;
	    }
	    return block;
	}
	return null;
    }
    
    public BaseDocument getDocument() {
	return editor.getDocument();
    }
    
    public int getStartIndex() {
	if (blk != null)
	    return blk.getStartIndex();
	return getElement().getStartIndex();
    }
    
    public int getEndIndex() {
	if (blk != null)
	    return blk.getEndIndex();
	return getElement().getEndIndex();
    }
    
    public AbstractElement getElement() {
	if (blk != null)
	    return blk.getElement();
	return editor.getDocument().getDefaultRootElement();
    }
    
    public boolean isResizable(int i) {
	if (blk != null)
	    return blk.isResizable(i);
	return false;
    }
    
    public void setSize(int i, int i_2_) {
	if (blk != null)
	    blk.setSize(i, i_2_);
    }
    
    public Container getContainer() {
	return editor;
    }
    
    public BlockFactory getBlockFactory() {
	return editor.getBlockFactory();
    }
}
