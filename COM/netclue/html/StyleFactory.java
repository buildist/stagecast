/* StyleFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.netclue.html.util.HTMLUtilities;

public class StyleFactory
{
    public static final Object FontFamily = new NameIdentifier("f_family");
    public static final Object FontSize = new NameIdentifier("f_size");
    public static final Object SupScript = new NameIdentifier("Superscript");
    public static final Object SubScript = new NameIdentifier("Subscript");
    public static final Object Bold = new NameIdentifier("bold");
    public static final Object Italic = new NameIdentifier("italic");
    public static final Object Underline = new NameIdentifier("underline");
    public static final Object StrikeThrough = new NameIdentifier("strike");
    public static final Object Foreground = new NameIdentifier("foreground");
    public static final Object Background = new NameIdentifier("background");
    public static final Object ComponentAttribute
	= new NameIdentifier("component");
    public static final Object IconAttribute = new NameIdentifier("icon");
    public static final Object FirstLineIndent
	= new NameIdentifier("FirstLineIndent");
    public static final Object LeftIndent = new NameIdentifier("LeftIndent");
    public static final Object RightIndent = new NameIdentifier("RightIndent");
    public static final Object LineSpacing = new NameIdentifier("LineSpacing");
    public static final Object SpaceAbove = new NameIdentifier("SpaceAbove");
    public static final Object SpaceBelow = new NameIdentifier("SpaceBelow");
    public static final Object Alignment = new NameIdentifier("Alignment");
    public static final Object VAlignment = new NameIdentifier("VAlignment");
    public static final Object TabSet = new NameIdentifier("TabSet");
    public static final Object FontProperty = new NameIdentifier("ftprop");
    public static final Object StyleName = new NameIdentifier("styleName");
    public static final Object hasLink = new NameIdentifier("hasLink");
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int ALIGN_JUSTIFIED = 3;
    public static final String baseStyleName = "_clBase";
    int nameCounter;
    Color baseColor;
    Hashtable styleToName = new Hashtable(64);
    Hashtable nameToStyle = new Hashtable(64);
    Hashtable tagAttPool = new Hashtable();
    private transient FontKey fKey = new FontKey(null, 0, 0);
    private transient Hashtable fontTable = new Hashtable();
    
    static class FontKey
    {
	private String family;
	private int style;
	private int size;
	
	public FontKey(String string, int i, int i_0_) {
	    setValue(string, i, i_0_);
	}
	
	public void setValue(String string, int i, int i_1_) {
	    family = string != null ? string.intern() : null;
	    style = i;
	    size = i_1_;
	}
	
	public int hashCode() {
	    return family.hashCode() ^ style ^ size;
	}
	
	public boolean equals(Object object) {
	    if (object instanceof FontKey) {
		FontKey fontkey_2_ = (FontKey) object;
		if (size != fontkey_2_.size || style != fontkey_2_.style
		    || family != fontkey_2_.family)
		    return false;
		return true;
	    }
	    return false;
	}
    }
    
    public StyleFactory() {
	StyleAttributes styleattributes = new StyleAttributes();
	String string = FontConfig.face;
	int i = FontConfig.curIdx + FontConfig.curBase;
	styleattributes.addAttribute("face", string);
	styleattributes.addAttribute("size", String.valueOf(i));
	styleattributes.addAttribute("color", "#000000");
	styleattributes.addAttribute("link", "blue");
	setNamedStyle("_clBase", styleattributes);
	baseColor = Color.black;
	Font font = new Font(string, 0, i);
	fKey.setValue(string, 0, i);
	fontTable.put(fKey, font);
    }
    
    public HTMLTagAttributes getSharedAttributes
	(HTMLTagAttributes htmltagattributes) {
	HTMLTagAttributes htmltagattributes_3_
	    = (HTMLTagAttributes) tagAttPool.get(htmltagattributes);
	if (htmltagattributes_3_ != null)
	    htmltagattributes_3_.updateReferenceCount(1);
	if (htmltagattributes_3_ == null)
	    return new HTMLTagAttributes(htmltagattributes);
	return htmltagattributes_3_;
    }
    
    public static boolean isUnderline(TagAttributes tagattributes) {
	Boolean var_boolean = (Boolean) tagattributes.getAttribute(Underline);
	if (var_boolean != null)
	    return var_boolean.booleanValue();
	return false;
    }
    
    public static boolean isStrikeThrough(TagAttributes tagattributes) {
	Boolean var_boolean
	    = (Boolean) tagattributes.getAttribute(StrikeThrough);
	if (var_boolean != null)
	    return var_boolean.booleanValue();
	return false;
    }
    
    public static int getFontLevel(TagAttributes tagattributes) {
	Boolean var_boolean = (Boolean) tagattributes.getAttribute(SupScript);
	if (var_boolean != null) {
	    if (var_boolean.booleanValue())
		return -1;
	    return 0;
	}
	var_boolean = (Boolean) tagattributes.getAttribute(SubScript);
	if (var_boolean != null) {
	    if (var_boolean.booleanValue())
		return 1;
	    return 0;
	}
	return 0;
    }
    
    public static boolean isBold(TagAttributes tagattributes) {
	Boolean var_boolean = (Boolean) tagattributes.getAttribute(Bold);
	if (var_boolean != null)
	    return var_boolean.booleanValue();
	return false;
    }
    
    public static boolean isItalic(TagAttributes tagattributes) {
	Boolean var_boolean = (Boolean) tagattributes.getAttribute(Italic);
	if (var_boolean != null)
	    return var_boolean.booleanValue();
	return false;
    }
    
    public static int getAlignment(TagAttributes tagattributes, int i) {
	Integer integer = (Integer) tagattributes.getAttribute(Alignment);
	if (integer == null)
	    return i;
	return integer.intValue();
    }
    
    public static int getAlignment(TagAttributes tagattributes) {
	Integer integer = (Integer) tagattributes.getAttribute(Alignment);
	if (integer == null)
	    return 0;
	return integer.intValue();
    }
    
    public static int getVerticalAlignment(TagAttributes tagattributes) {
	Integer integer = (Integer) tagattributes.getAttribute(VAlignment);
	if (integer == null)
	    return 1;
	return integer.intValue();
    }
    
    public void setNamedStyle(String string, StyleAttributes styleattributes) {
	styleToName.put(styleattributes, string);
	nameToStyle.put(string, styleattributes);
    }
    
    public String getStyleName(StyleAttributes styleattributes) {
	String string = null;
	if (styleattributes != null
	    && styleattributes.getAttributeCount() != 0) {
	    string = (String) styleToName.get(styleattributes);
	    if (string == null) {
		string = "unamed" + nameCounter++;
		styleToName.put(styleattributes, string);
		nameToStyle.put(string, styleattributes);
	    }
	}
	return string;
    }
    
    public StyleAttributes getStyleByName(String string) {
	return (StyleAttributes) nameToStyle.get(string);
    }
    
    public Color getLinkForeground(TagAttributes tagattributes) {
	String string = null;
	if (tagattributes != null)
	    string = (String) tagattributes.getAttribute("color");
	if (string == null) {
	    StyleAttributes styleattributes = getStyleByName("_clBase");
	    string = (String) styleattributes.getAttribute("link");
	}
	return HTMLUtilities.stringToColor(string);
    }
    
    public String resolveAttribute(TagAttributes tagattributes,
				   String string) {
	String string_4_ = null;
	if (tagattributes != null) {
	    String string_5_ = (String) tagattributes.getAttribute(StyleName);
	    if (string_5_ != null)
		tagattributes = getStyleByName(string_5_);
	    string_4_ = (String) tagattributes.getAttribute(string);
	}
	if (string_4_ == null) {
	    StyleAttributes styleattributes = getStyleByName("_clBase");
	    string_4_ = (String) styleattributes.getAttribute(string);
	}
	return string_4_;
    }
    
    public Color getForeground(TagAttributes tagattributes) {
	String string = resolveAttribute(tagattributes, "color");
	return HTMLUtilities.stringToColor(string);
    }
    
    public int getFontSize(TagAttributes tagattributes) {
	String string = resolveAttribute(tagattributes, "size");
	int i = HTMLUtilities.stringToFontSize(string);
	if (i < 900)
	    i = FontConfig.getAbsSize(i);
	else
	    i = FontConfig.getRelSize(i - 1000);
	return i;
    }
    
    public Font getFont(String string, int i, int i_6_) {
	StringTokenizer stringtokenizer = new StringTokenizer(string, ",");
	Font font = null;
	while (stringtokenizer.hasMoreTokens()) {
	    String string_7_ = stringtokenizer.nextToken().trim();
	    fKey.setValue(string_7_, i, i_6_);
	    font = (Font) fontTable.get(fKey);
	    if (font == null)
		font = new Font(string_7_, i, i_6_);
	    if (font != null) {
		fKey.setValue(string_7_, i, i_6_);
		fontTable.put(fKey, font);
		break;
	    }
	}
	if (font == null) {
	    fKey.setValue(FontConfig.face, 0,
			  FontConfig.curIdx + FontConfig.curBase);
	    font = (Font) fontTable.get(fKey);
	}
	return font;
    }
    
    public static FontMetrics getFontMetrics(Font font) {
	return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }
    
    public void clearFonts() {
	fontTable.clear();
    }
}
