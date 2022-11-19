/* BlankBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import com.netclue.html.AbstractElement;
import com.netclue.html.HTMLConst;
import com.netclue.html.TagAttributes;
import com.netclue.html.util.HTMLUtilities;

public class BlankBlock extends Block
{
    private int vWidth = 1;
    private int vHeight = 1;
    private boolean fixedWidth = false;
    private boolean fixedHeight = false;
    
    public BlankBlock(AbstractElement abstractelement) {
	super(abstractelement);
	TagAttributes tagattributes = abstractelement.getAttributeNode();
	if (tagattributes != null) {
	    String string = (String) tagattributes.getAttribute("type");
	    if (string == null)
		string = "block";
	    else
		string = string.trim().toLowerCase().intern();
	    if (string == "vertical") {
		string = (String) tagattributes.getAttribute("size");
		if (string != null)
		    vHeight = HTMLUtilities.stringToInt(string);
	    } else if (string == "horizontal") {
		string = (String) tagattributes.getAttribute("size");
		if (string != null)
		    vWidth = HTMLUtilities.stringToInt(string);
	    } else {
		string = (String) tagattributes.getAttribute(HTMLConst.width);
		if (string != null)
		    vWidth = HTMLUtilities.stringToInt(string);
		string = (String) tagattributes.getAttribute(HTMLConst.height);
		if (string != null)
		    vHeight = HTMLUtilities.stringToInt(string);
	    }
	}
    }
    
    public int getPreferredSize(int i) {
	if (i == 0)
	    return vWidth;
	return vHeight;
    }
    
    public void paint(Graphics graphics, Shape shape) {
	/* empty */
    }
    
    public Rectangle findBounds(int i, Rectangle rectangle) {
	return rectangle;
    }
    
    public int getDocIndex(int i, int i_0_, Shape shape) {
	return -1;
    }
    
    public void setSize(int i, int i_1_) {
	if (!fixedWidth)
	    vWidth = i;
	if (!fixedHeight)
	    vHeight = i_1_;
    }
    
    public boolean isResizable(int i) {
	if (i == 0) {
	    if (fixedWidth)
		return false;
	    return true;
	}
	if (fixedHeight)
	    return false;
	return true;
    }
}
