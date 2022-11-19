/* HRuleBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;
import com.netclue.html.HTMLConst;
import com.netclue.html.TagAttributes;
import com.netclue.html.util.HTMLUtilities;

public class HRuleBlock extends Block
{
    private float alignment = -1.0F;
    private boolean noshade = false;
    private int size = 2;
    private int hrwidth;
    private int hratio = 100;
    private int ymargin = 5;
    
    public HRuleBlock(AbstractElement abstractelement) {
	super(abstractelement);
	TagAttributes tagattributes = abstractelement.getAttributeNode();
	if (tagattributes != null) {
	    String string
		= (String) tagattributes.getAttribute(HTMLConst.align);
	    if (string != null) {
		alignment = 0.0F;
		if (string.equalsIgnoreCase("center"))
		    alignment = 0.5F;
		else if (string.equalsIgnoreCase("right"))
		    alignment = 1.0F;
	    }
	    if (tagattributes.getAttribute("noshade") != null)
		noshade = true;
	    String string_0_
		= (String) tagattributes.getAttribute(HTMLConst.size);
	    if (string_0_ != null)
		size = HTMLUtilities.stringToInt(string_0_);
	    string_0_ = (String) tagattributes.getAttribute(HTMLConst.width);
	    if (string_0_ != null) {
		int i = HTMLUtilities.stringToRatioInt(string_0_);
		if (i < 0)
		    hratio = -i;
		else {
		    hratio = 0;
		    hrwidth = i;
		}
	    }
	}
    }
    
    public void setSize(int i, int i_1_) {
	if (hratio > 0)
	    hrwidth = i * hratio / 100;
    }
    
    public void paint(Graphics graphics, Shape shape) {
	Rectangle rectangle = shape.getBounds();
	setSize(rectangle.width, rectangle.height);
	int i = rectangle.x;
	int i_2_ = rectangle.y + ymargin;
	int i_3_ = hrwidth;
	int i_4_ = size;
	if (alignment < 0.0F)
	    alignment = this.getParent().getAlignment(0);
	i += (int) ((float) (rectangle.width - i_3_) * alignment);
	if (noshade) {
	    graphics.setColor(Color.gray);
	    graphics.fillRect(i, i_2_, i_3_, i_4_);
	} else {
	    i_3_--;
	    i_4_--;
	    graphics.setColor(Color.gray);
	    graphics.drawRect(i, i_2_, i_3_, i_4_);
	    graphics.setColor(Color.lightGray);
	    int i_5_ = i + i_3_;
	    int i_6_ = i_2_ + i_4_;
	    graphics.drawLine(i, i_6_, i_5_, i_6_);
	    graphics.drawLine(i_5_, i_2_, i_5_, i_6_);
	}
    }
    
    public int getPreferredSize(int i) {
	if (i == 0)
	    return hrwidth;
	return size + (ymargin << 1);
    }
    
    public boolean isResizable(int i) {
	if (i != 0)
	    return false;
	return true;
    }
    
    public Rectangle findBounds(int i, Rectangle rectangle) {
	int i_7_ = this.getStartIndex();
	int i_8_ = this.getEndIndex();
	if (i >= i_7_ && i < i_8_)
	    return rectangle;
	return null;
    }
    
    public int getDocIndex(int i, int i_9_, Shape shape) {
	return this.getStartIndex();
    }
}
