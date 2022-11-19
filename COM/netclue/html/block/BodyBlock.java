/* BodyBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.URL;

import com.netclue.html.AbstractElement;
import com.netclue.html.TagAttributes;
import com.netclue.html.util.HTMLUtilities;

public class BodyBlock extends CellBlock implements ImageObserver
{
    Image bgimage;
    Color bgcolor = Color.white;
    String imageStr;
    int imgWidth = -1;
    int imgHeight = -1;
    
    public BodyBlock(AbstractElement abstractelement) {
	super(abstractelement);
	this.setInsets((short) 2, (short) 5, (short) 2, (short) 5);
	TagAttributes tagattributes = abstractelement.getAttributeNode();
	if (tagattributes != null)
	    initBackground(tagattributes);
    }
    
    void initBackground(TagAttributes tagattributes) {
	bgimage = null;
	String string = (String) tagattributes.getAttribute("bgcolor");
	if (string == null)
	    bgcolor = Color.white;
	else
	    bgcolor = HTMLUtilities.stringToColor(string);
	imageStr = (String) tagattributes.getAttribute("background");
	if (imageStr != null) {
	    bgimage = loadImage(imageStr);
	    if (bgimage != null) {
		imgHeight = bgimage.getHeight(this);
		imgWidth = bgimage.getWidth(this);
	    }
	}
    }
    
    private Image loadImage(String string) {
	Object object = null;
	URL url = null;
	URL url_0_ = (URL) this.getElement().getDocument().getProperty("base");
	if (url_0_ != null) {
	    try {
		url = new URL(url_0_, string);
	    } catch (java.net.MalformedURLException malformedurlexception) {
		/* empty */
	    }
	}
	Image image;
	if (url != null)
	    image = Toolkit.getDefaultToolkit().getImage(url);
	else
	    image = Toolkit.getDefaultToolkit().getImage(string);
	return image;
    }
    
    public void paint(Graphics graphics, Shape shape) {
	Rectangle rectangle = shape.getBounds();
	if (bgimage != null && imgWidth > 0 && imgHeight > 0) {
	    for (int i = 0; i < rectangle.width; i += imgWidth) {
		for (int i_1_ = 0; i_1_ < rectangle.height; i_1_ += imgHeight)
		    graphics.drawImage(bgimage, i, i_1_, null);
	    }
	} else {
	    graphics.setColor(bgcolor);
	    graphics.fillRect(0, 0, rectangle.width, rectangle.height);
	}
	super.paint(graphics, rectangle);
    }
    
    public boolean imageUpdate(Image image, int i, int i_2_, int i_3_,
			       int i_4_, int i_5_) {
	if (bgimage == null)
	    return false;
	if ((i & 0xc0) != 0) {
	    bgimage = null;
	    this.sizeChanged(true, true);
	    return false;
	}
	if ((i & 0x2) != 0 && imgHeight < 0)
	    imgHeight = i_5_;
	if ((i & 0x1) != 0 && imgWidth < 0)
	    imgWidth = i_4_;
	if ((i & 0x30) != 0)
	    this.sizeChanged(true, false);
	return true;
    }
}
