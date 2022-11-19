/* WidgetBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;
import com.netclue.html.StyleFactory;
import com.netclue.html.TagAttributes;

public class WidgetBlock extends Block
{
    protected Component c;
    Dimension prefDim;
    
    public WidgetBlock(AbstractElement abstractelement) {
	super(abstractelement);
	TagAttributes tagattributes = abstractelement.getAttributeNode();
	if (tagattributes != null) {
	    c = ((Component)
		 tagattributes.getAttribute(StyleFactory.ComponentAttribute));
	    if (c != null)
		c.setVisible(false);
	}
    }
    
    public void paint(Graphics graphics, Shape shape) {
	if (c != null) {
	    c.setBounds(shape.getBounds());
	    if (!c.isVisible())
		c.setVisible(true);
	}
    }
    
    public int getPreferredSize(int i) {
	if (c == null)
	    return 10;
	if (prefDim == null)
	    prefDim = c.getPreferredSize();
	if (i == 0)
	    return prefDim.width;
	return prefDim.height;
    }
    
    public float getAlignment(int i) {
	if (c == null)
	    return 0.0F;
	if (i == 0)
	    return c.getAlignmentX();
	return 0.75F;
    }
    
    public int getBreakWeight(int i, float f, float f_0_) {
	if (f_0_ >= (float) getPreferredSize(i))
	    return 2000;
	return 0;
    }
    
    public void setSize(int i, int i_1_) {
	if (c != null) {
	    if (prefDim == null)
		prefDim = c.getPreferredSize();
	    prefDim.width = i;
	    prefDim.height = i_1_;
	    c.setSize(i, i_1_);
	}
    }
    
    public void setParent(Block block) {
	super.setParent(block);
	if (block == null) {
	    if (c != null) {
		Container container = c.getParent();
		if (container != null)
		    container.remove(c);
	    }
	} else if (c != null) {
	    Container container = this.getContainer();
	    container.add(c);
	}
    }
    
    protected Component getComponent() {
	return c;
    }
    
    public Rectangle findBounds(int i, Rectangle rectangle) {
	int i_2_ = this.getStartIndex();
	int i_3_ = this.getEndIndex();
	if (i >= i_2_ && i < i_3_)
	    return rectangle;
	return null;
    }
    
    public int getDocIndex(int i, int i_4_, Shape shape) {
	return this.getStartIndex();
    }
}
