/* ImageComponent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.URL;

import com.netclue.html.AbstractElement;
import com.netclue.html.BaseDocument;
import com.netclue.html.CLHtmlPane;
import com.netclue.html.HTMLConst;
import com.netclue.html.TagAttributes;
import com.netclue.html.util.HTMLUtilities;

public class ImageComponent extends Container implements ImageObserver
{
    static final int DFT_WIDTH = 12;
    static final int DFT_HEIGHT = 12;
    private Image hostImg;
    private int iHeight;
    private int iWidth;
    private int flushAlign;
    private int vSpace;
    private int hSpace;
    private float vAlignment;
    private AbstractElement iElem;
    private TagAttributes iAttr;
    private ImageBlock imgHost;
    
    public ImageComponent(AbstractElement abstractelement,
			  ImageBlock imageblock) {
	initialize(abstractelement);
	imgHost = imageblock;
    }
    
    private void initialize(AbstractElement abstractelement) {
	iElem = abstractelement;
	iAttr = abstractelement.getAttributeNode();
	URL url = resolveSourceURL();
	if (url != null)
	    hostImg = Toolkit.getDefaultToolkit().getImage(url);
	vAlignment = 0.5F;
	flushAlign = 0;
	String string = (String) iAttr.getAttribute(HTMLConst.align);
	if (string != null) {
	    string = string.toLowerCase();
	    if (string.equals("left"))
		flushAlign = -1;
	    else if (string.equals("right"))
		flushAlign = 1;
	    else if (string.equals("top") || string.equals("texttop")
		     || string.equals("left") || string.equals("right"))
		vAlignment = 0.0F;
	    else if (string.equals("bottom"))
		vAlignment = 1.0F;
	}
	String string_0_ = (String) iAttr.getAttribute(HTMLConst.hspace);
	hSpace = (string_0_ == null ? flushAlign != 0 ? 2 : 0
		  : HTMLUtilities.stringToInt(string_0_));
	vSpace = getIntValue(HTMLConst.vspace, 0);
	iHeight = getIntValue(HTMLConst.height, -1);
	boolean bool = iHeight > 0;
	if (!bool && hostImg != null)
	    iHeight = hostImg.getHeight(this);
	iWidth = getIntValue(HTMLConst.width, -1);
	boolean bool_1_ = iWidth > 0;
	if (!bool_1_ && hostImg != null)
	    iWidth = hostImg.getWidth(this);
	if (iWidth < 0)
	    iWidth = 12;
	if (iHeight < 0)
	    iHeight = 12;
    }
    
    int getSpace(int i) {
	if (i == 0)
	    return hSpace;
	return vSpace;
    }
    
    float getVerticalAlignment() {
	return vAlignment;
    }
    
    public int isFlushAlign() {
	return flushAlign;
    }
    
    private boolean hasPixels(ImageObserver imageobserver) {
	if (hostImg == null || hostImg.getHeight(imageobserver) <= 0
	    || hostImg.getWidth(imageobserver) <= 0)
	    return false;
	return true;
    }
    
    URL resolveSourceURL() {
	String string = null;
	if (iAttr != null)
	    string = (String) iAttr.getAttribute(HTMLConst.src);
	if (string == null)
	    return null;
	BaseDocument basedocument = iElem.getDocument();
	URL url = (URL) basedocument.getProperty("base");
	try {
	    URL url_2_ = new URL(url, string);
	    return url_2_;
	} catch (java.net.MalformedURLException malformedurlexception) {
	    return null;
	}
    }
    
    int getIntValue(String string, int i) {
	String string_3_ = (String) iAttr.getAttribute(string);
	if (string_3_ == null)
	    return i;
	return HTMLUtilities.stringToInt(string_3_);
    }
    
    public void update(Graphics graphics) {
	/* empty */
    }
    
    public void paint(Graphics graphics) {
	this.getBounds();
	int i = getSpace(0);
	int i_4_ = getSpace(1);
	int i_5_ = iWidth;
	int i_6_ = iHeight;
	if (hostImg == null) {
	    if (i_6_ > 10 && i_5_ > 10) {
		graphics.setColor(Color.gray);
		graphics.drawRect(i, i_4_, i_5_ - 1, i_6_ - 1);
	    }
	} else {
	    ImageComponent imagecomponent_7_ = this;
	    if (hostImg == null || hostImg.getHeight(imagecomponent_7_) <= 0
		|| hostImg.getWidth(imagecomponent_7_) <= 0 && !false) {
		if (i_6_ > 10 && i_5_ > 10) {
		    graphics.setColor(Color.lightGray);
		    graphics.drawRect(i, i_4_, i_5_ - 1, i_6_ - 1);
		}
	    } else {
		Rectangle rectangle = graphics.getClipBounds();
		Rectangle rectangle_8_ = new Rectangle(i, i_4_, i_5_, i_6_);
		if (rectangle_8_.intersects(rectangle))
		    graphics.drawImage(hostImg, i, i_4_, i_5_, i_6_, this);
	    }
	}
    }
    
    public boolean imageUpdate(Image image, int i, int i_9_, int i_10_,
			       int i_11_, int i_12_) {
	if (hostImg == null)
	    return false;
	if ((i & 0xc0) != 0) {
	    hostImg = null;
	    this.repaint();
	    return false;
	}
	boolean bool = false;
	if ((i & 0x2) != 0 && iAttr.getAttribute(HTMLConst.height) == null) {
	    iHeight = i_12_;
	    bool = true;
	}
	if ((i & 0x1) != 0 && iAttr.getAttribute(HTMLConst.width) == null) {
	    iWidth = i_11_;
	    bool = true;
	}
	if (bool) {
	    HTMLUtilities.scheduleIt(imgHost);
	    return true;
	}
	if ((i & 0x30) != 0)
	    this.repaint(0L);
	else if ((i & 0x8) != 0)
	    this.repaint(200L);
	return true;
    }
    
    public int getPreferredSize(int i) {
	int i_13_ = getSpace(i) << 1;
	if (i == 0)
	    return iWidth + i_13_;
	return iHeight + i_13_;
    }
    
    CLHtmlPane getHtmlPane() {
	for (Container container = this.getParent(); container != null;
	     container = container.getParent()) {
	    if (container instanceof CLHtmlPane)
		return (CLHtmlPane) container;
	}
	return null;
    }
    
    protected void finalize() {
	hostImg = null;
	iElem = null;
	iAttr = null;
    }
}
