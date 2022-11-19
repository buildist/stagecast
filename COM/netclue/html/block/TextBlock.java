/* TextBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.text.BreakIterator;

import com.netclue.html.AbstractElement;
import com.netclue.html.BaseDocument;
import com.netclue.html.DocumentTabs;
import com.netclue.html.HTMLTagBag;
import com.netclue.html.StyleFactory;
import com.netclue.html.TagAttributes;
import com.netclue.html.TermElement;

public class TextBlock extends Block
{
    int prefXSize;
    int prefYSize;
    Font font;
    FontMetrics metrics;
    Color fg;
    String text;
    boolean underline;
    boolean strikeThr;
    boolean isMarked;
    int fontLevel;
    DocumentTabs tabPos;
    static BreakIterator wordBreaker = BreakIterator.getWordInstance();
    int x;
    
    class LabelFragment extends Block
    {
	String text;
	short offset;
	short length;
	int x;
	int prefXSize = -1;
	
	public LabelFragment(AbstractElement abstractelement, int i,
			     int i_0_) {
	    super(abstractelement);
	    offset = (short) (i - abstractelement.getStartIndex());
	    length = (short) (i_0_ - i);
	    text = sliceText(i, i_0_);
	}
	
	public int getStartIndex() {
	    AbstractElement abstractelement = this.getElement();
	    return abstractelement.getStartIndex() + offset;
	}
	
	public int getEndIndex() {
	    AbstractElement abstractelement = this.getElement();
	    return abstractelement.getStartIndex() + offset + length;
	}
	
	public void setStatus(boolean bool) {
	    isMarked = bool;
	}
	
	public void paint(Graphics graphics, Shape shape) {
	    paintText(graphics, shape, text);
	}
	
	public int getPreferredSize(int i) {
	    if (i == 0) {
		if (prefXSize < 0)
		    prefXSize = TextBlock.this.getPreferredSize(i, x, text);
		return prefXSize;
	    }
	    return TextBlock.this.getPreferredSize(i);
	}
	
	public float getAlignment(int i) {
	    return TextBlock.this.getAlignment(i);
	}
	
	public Rectangle findBounds(int i, Rectangle rectangle) {
	    return TextBlock.this.findBounds(i, rectangle, getStartIndex(),
					     getEndIndex());
	}
	
	public int getDocIndex(int i, int i_1_, Shape shape) {
	    return TextBlock.this.getDocIndex(i, i_1_, shape, getStartIndex(),
					      getEndIndex());
	}
	
	public int getBreakWeight(int i, float f, float f_2_) {
	    return TextBlock.this.getBreakWeight(i, f, f_2_, getStartIndex(),
						 getEndIndex());
	}
	
	public Block divideBlock(int i, int i_3_, float f, float f_4_) {
	    return TextBlock.this.divideBlock(i, i_3_, f, f_4_);
	}
    }
    
    public TextBlock(AbstractElement abstractelement) {
	super(abstractelement);
	prefXSize = prefYSize = -1;
	text = ((TermElement) abstractelement).getContent();
    }
    
    String sliceText(int i, int i_5_) {
	TermElement termelement = (TermElement) this.getElement();
	String string = termelement.getContent();
	int i_6_ = Math.max(0, i - termelement.getStartIndex());
	int i_7_ = Math.min(i_5_ - i + i_6_, string.length());
	return string.substring(i_6_, i_7_);
    }
    
    void getDocumentTabs() {
	AbstractElement abstractelement = this.getElement();
	tabPos
	    = (DocumentTabs) abstractelement.getAttribute(StyleFactory.TabSet);
    }
    
    void drawText(Graphics graphics, String string, int i, int i_8_) {
	graphics.drawString(string, i, i_8_);
	if (underline || strikeThr) {
	    int i_9_ = string.length();
	    int i_11_;
	    int i_10_ = i_11_ = i;
	    int i_12_ = metrics.charWidth(' ');
	    int i_13_ = 0;
	    while (i_13_ < i_9_) {
		if (string.charAt(i_13_++) != ' ')
		    break;
		i_10_ += i_12_;
	    }
	    i_8_ = i_8_ + (strikeThr ? -metrics.getDescent() : 1);
	    i_11_ += metrics.stringWidth(string);
	    graphics.drawLine(i_10_, i_8_, i_11_, i_8_);
	}
    }
    
    void drawTabbedText(Graphics graphics, String string, int i, int i_14_) {
	int i_15_ = i;
	char[] cs = string.toCharArray();
	int i_16_ = 0;
	int i_17_ = 0;
	int i_18_ = cs.length;
	int i_19_ = i;
	boolean bool = true;
	for (int i_20_ = 0; i_20_ < i_18_; i_20_++) {
	    if (cs[i_20_] == '\t') {
		if (i_16_ > 0) {
		    graphics.drawChars(cs, i_17_, i_16_, i, i_14_);
		    i_16_ = 0;
		}
		i_17_ = i_20_ + 1;
		if (tabPos == null)
		    getDocumentTabs();
		i_15_ = (int) tabPos.nextTabPosition((float) i_15_);
		i = i_15_;
	    } else {
		i_16_++;
		int i_21_ = metrics.charWidth(cs[i_20_]);
		if (bool) {
		    if (cs[i_20_] == ' ')
			i_19_ += i_21_;
		    else
			bool = false;
		}
		i_15_ += i_21_;
	    }
	}
	if (i_16_ > 0)
	    graphics.drawChars(cs, i_17_, i_16_, i, i_14_);
	if (underline || strikeThr) {
	    i_14_ = i_14_ + (strikeThr ? -metrics.getDescent() : 1);
	    graphics.drawLine(i_19_, i_14_, i_15_, i_14_);
	}
    }
    
    int getTabbedWidth(String string, int i) {
	int i_22_ = i;
	char[] cs = string.toCharArray();
	int i_23_ = cs.length;
	for (int i_24_ = 0; i_24_ < i_23_; i_24_++) {
	    if (cs[i_24_] == '\t') {
		if (tabPos == null)
		    getDocumentTabs();
		i_22_ = (int) tabPos.nextTabPosition((float) i_22_);
	    } else
		i_22_ += metrics.charWidth(cs[i_24_]);
	}
	return i_22_ - i;
    }
    
    int getTabbedOffset(String string, int i, int i_25_) {
	int i_26_ = i;
	int i_27_ = i_26_;
	char[] cs = string.toCharArray();
	int i_28_ = cs.length;
	for (int i_29_ = 0; i_29_ < i_28_; i_29_++) {
	    if (cs[i_29_] == '\t') {
		if (tabPos == null)
		    getDocumentTabs();
		i_27_ = (int) tabPos.nextTabPosition((float) i_27_);
	    } else
		i_27_ += metrics.charWidth(cs[i_29_]);
	    if (i_25_ >= i_26_ && i_25_ < i_27_)
		return i_29_;
	    i_26_ = i_27_;
	}
	return -1;
    }
    
    void paintText(Graphics graphics, Shape shape, String string) {
	Rectangle rectangle = shape.getBounds();
	int i = rectangle.y + rectangle.height - metrics.getDescent();
	if (fontLevel != 0) {
	    int i_30_ = metrics.getAscent() >> 1;
	    i = i + (fontLevel > 0 ? i_30_ : -i_30_);
	}
	graphics.setFont(font);
	if (isMarked)
	    graphics.setColor(Color.red);
	else
	    graphics.setColor(fg);
	if (tabPos == null)
	    drawText(graphics, string, rectangle.x, i);
	else
	    drawTabbedText(graphics, string, rectangle.x, i);
    }
    
    public void refreshAttributes() {
	if (font == null) {
	    AbstractElement abstractelement = this.getElement();
	    BaseDocument basedocument = this.getDocument();
	    StyleFactory stylefactory = basedocument.getStyleFactory();
	    TagAttributes tagattributes = abstractelement.getAttributeNode();
	    int i = 0;
	    boolean bool = false;
	    if (tagattributes != null) {
		Integer integer
		    = ((Integer)
		       tagattributes.getAttribute(StyleFactory.FontProperty));
		if (integer != null) {
		    int i_31_ = integer.intValue();
		    underline = (i_31_ & 0x4) != 0;
		    strikeThr = (i_31_ & 0x8) != 0;
		    fontLevel = ((i_31_ & 0x20) != 0 ? 1 : (i_31_ & 0x10) != 0
				 ? -1 : 0);
		    if ((i_31_ & 0x2) != 0)
			i |= 0x1;
		    if ((i_31_ & 0x1) != 0)
			i |= 0x2;
		}
	    }
	    if (abstractelement.getParentElement().getTagCode()
		== HTMLTagBag.anchorID) {
		bool = true;
		underline = true;
	    }
	    String string
		= stylefactory.resolveAttribute(tagattributes, "face");
	    int i_32_ = stylefactory.getFontSize(tagattributes);
	    font = stylefactory.getFont(string, i, i_32_);
	    if (bool)
		fg = stylefactory.getLinkForeground(tagattributes);
	    else
		fg = stylefactory.getForeground(tagattributes);
	    metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
	    prefYSize = metrics.getHeight();
	}
    }
    
    public void setStatus(boolean bool) {
	isMarked = bool;
    }
    
    int getPreferredSize(int i, int i_33_, String string) {
	if (i == 0) {
	    refreshAttributes();
	    if (string.length() == 0)
		return 1;
	    return Math.max(1, getTabbedWidth(string, i_33_));
	}
	return prefYSize;
    }
    
    public int getMinimumSize(int i) {
	refreshAttributes();
	char[] cs = text.toCharArray();
	int i_34_ = cs.length;
	int i_36_;
	int i_37_;
	int i_35_ = i_36_ = i_37_ = 0;
	int i_38_ = metrics.charWidth(' ');
	for (/**/; i_35_ < i_34_ && Character.isWhitespace(cs[i_35_]); i_35_++)
	    i_36_ += i_38_;
	if (i_35_ < i_34_) {
	    wordBreaker.setText(text.substring(i_35_));
	    int i_39_;
	    while ((i_39_ = wordBreaker.next()) != -1) {
		i_37_ = Math.max(i_37_,
				 metrics.charsWidth(cs, i_35_, i_39_ - i_35_));
		i_35_ = i_39_ + 1;
	    }
	}
	return i_37_;
    }
    
    Rectangle findBounds(int i, Rectangle rectangle, int i_40_, int i_41_) {
	if (i >= i_40_ && i <= i_41_) {
	    String string = sliceText(i_40_, i);
	    int i_42_ = getTabbedWidth(string, rectangle.x);
	    return new Rectangle(rectangle.x + i_42_, rectangle.y, 0,
				 prefYSize);
	}
	return null;
    }
    
    int getDocIndex(int i, int i_43_, Shape shape, int i_44_, int i_45_) {
	Rectangle rectangle = shape.getBounds();
	String string = sliceText(i_44_, i_45_);
	int i_46_ = getTabbedOffset(string, rectangle.x, i);
	if (i_46_ == -1)
	    return -1;
	return i_44_ + i_46_;
    }
    
    int getBreakWeight(int i, float f, float f_47_, int i_48_, int i_49_) {
	if (i == 0) {
	    String string = sliceText(i_48_, i_49_);
	    int i_50_ = getTabbedOffset(string, (int) f, (int) (f + f_47_));
	    if (i_50_ == 0)
		return 0;
	    for (int i_51_ = Math.min(i_50_, string.length() - 1); i_51_ >= 0;
		 i_51_--) {
		char c = string.charAt(i_51_);
		if (Character.isWhitespace(c))
		    return 2000;
	    }
	    return 1000;
	}
	return super.getBreakWeight(i, f, f_47_);
    }
    
    public void paint(Graphics graphics, Shape shape) {
	TermElement termelement = (TermElement) this.getElement();
	String string = termelement.getContent();
	paintText(graphics, shape, string);
    }
    
    public int getPreferredSize(int i) {
	if (i == 0) {
	    if (prefXSize < 0)
		prefXSize = getPreferredSize(i, x, text);
	    return prefXSize;
	}
	if (prefYSize == -1)
	    refreshAttributes();
	return prefYSize;
    }
    
    public float getAlignment(int i) {
	if (i == 1) {
	    float f = (float) metrics.getHeight();
	    float f_52_ = (float) metrics.getDescent();
	    float f_53_ = (f - f_52_) / f;
	    return f_53_;
	}
	return super.getAlignment(i);
    }
    
    public Rectangle findBounds(int i, Rectangle rectangle) {
	return findBounds(i, rectangle, this.getStartIndex(),
			  this.getEndIndex());
    }
    
    public int getDocIndex(int i, int i_54_, Shape shape) {
	return getDocIndex(i, i_54_, shape, this.getStartIndex(),
			   this.getEndIndex());
    }
    
    public int getBreakWeight(int i, float f, float f_55_) {
	return getBreakWeight(i, f, f_55_, this.getStartIndex(),
			      this.getEndIndex());
    }
    
    public Block divideBlock(int i, int i_56_, float f, float f_57_) {
	if (i == 0) {
	    refreshAttributes();
	    String string = sliceText(i_56_, this.getEndIndex());
	    int i_58_ = getTabbedOffset(string, (int) f, (int) (f + f_57_));
	    if (i_58_ == -1)
		return null;
	    for (int i_59_ = Math.min(i_58_, string.length() - 1); i_59_ >= 0;
		 i_59_--) {
		char c = string.charAt(i_59_);
		if (Character.isWhitespace(c)) {
		    i_58_ = i_59_ + 1;
		    break;
		}
	    }
	    int i_60_ = i_56_ + i_58_;
	    return new LabelFragment(this.getElement(), i_56_, i_60_);
	}
	return this;
    }
    
    public Block createFragment(int i, int i_61_) {
	AbstractElement abstractelement = this.getElement();
	return new LabelFragment(abstractelement,
				 Math.max(abstractelement.getStartIndex(), i),
				 i_61_);
    }
}
