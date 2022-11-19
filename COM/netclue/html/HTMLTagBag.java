/* HTMLTagBag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.util.Hashtable;

public class HTMLTagBag implements TagBag
{
    static String[] tName
	= { "html", "head", "title", "meta", "link", "script", "style", "base",
	    "body", "h1", "h2", "h3", "h4", "h5", "h6", "frameset", "frame",
	    "noframes", "ul", "ol", "dir", "menu", "dl", "li", "dt", "dd", "p",
	    "div", "center", "blockquote", "pre", "address", "form", "hr",
	    "spacer", "table", "caption", "tr", "td", "th", "tt", "i", "b",
	    "u", "strike", "big", "small", "sub", "sup", "em", "strong", "dfn",
	    "code", "samp", "kbd", "var", "cite", "input", "select",
	    "textarea", "isindex", "option", "a", "img", "applet", "param",
	    "font", "br", "nobr", "basefont", "map", "area", "text" };
    static int[] tCode
	= { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21,
	    22, 23, 24, 25, 26, 27, 28, 30, 31, 32, 33, 34, 35, 40, 50, 51, 60,
	    61, 62, 63, 64, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110,
	    111, 112, 113, 114, 115, 116, 117, 120, 121, 122, 123, 124, 130,
	    131, 132, 133, 134, 135, 136, 137, 140, 141, 150 };
    Hashtable bag = new Hashtable();
    public static int headID;
    public static int bodyID;
    public static int anchorID;
    public static int fontID;
    public static int parID;
    public static int cenID;
    public static int divID;
    public static int preID;
    public static int bqID;
    public static int addrID;
    public static int noframeID;
    public static int frameID;
    public static int framesetID;
    public static int tableID;
    public static int captionID;
    public static int trID;
    public static int cellID;
    public static int thID;
    public static int imgID;
    public static int mapID;
    public static int areaID;
    public static int appID;
    public static int paramID;
    public static int formID;
    public static int selID;
    public static int optID;
    public static int inputID;
    public static int nobrID;
    public static int brID;
    public static int menuID;
    public static int ulID;
    public static int olID;
    public static int dlID;
    public static int liID;
    public static int dtID;
    public static int ddID;
    public static int spcID;
    public static int hrID;
    public static int textID;
    public static int blankID;
    
    public HTMLTagBag() {
	for (int i = 0; i < tCode.length; i++)
	    bag.put(tName[i], new Integer(tCode[i]));
	headID = ((Integer) bag.get("head")).intValue();
	bodyID = ((Integer) bag.get("body")).intValue();
	anchorID = ((Integer) bag.get("a")).intValue();
	fontID = ((Integer) bag.get("font")).intValue();
	parID = ((Integer) bag.get("p")).intValue();
	cenID = ((Integer) bag.get("center")).intValue();
	divID = ((Integer) bag.get("div")).intValue();
	preID = ((Integer) bag.get("pre")).intValue();
	bqID = ((Integer) bag.get("blockquote")).intValue();
	addrID = ((Integer) bag.get("address")).intValue();
	framesetID = ((Integer) bag.get("frameset")).intValue();
	frameID = ((Integer) bag.get("frame")).intValue();
	noframeID = ((Integer) bag.get("noframes")).intValue();
	tableID = ((Integer) bag.get("table")).intValue();
	captionID = ((Integer) bag.get("caption")).intValue();
	trID = ((Integer) bag.get("tr")).intValue();
	cellID = ((Integer) bag.get("td")).intValue();
	thID = ((Integer) bag.get("th")).intValue();
	imgID = ((Integer) bag.get("img")).intValue();
	mapID = ((Integer) bag.get("map")).intValue();
	areaID = ((Integer) bag.get("area")).intValue();
	appID = ((Integer) bag.get("applet")).intValue();
	paramID = ((Integer) bag.get("param")).intValue();
	formID = ((Integer) bag.get("form")).intValue();
	selID = ((Integer) bag.get("select")).intValue();
	optID = ((Integer) bag.get("option")).intValue();
	inputID = ((Integer) bag.get("input")).intValue();
	nobrID = ((Integer) bag.get("nobr")).intValue();
	brID = ((Integer) bag.get("br")).intValue();
	menuID = ((Integer) bag.get("menu")).intValue();
	ulID = ((Integer) bag.get("ul")).intValue();
	olID = ((Integer) bag.get("ol")).intValue();
	dlID = ((Integer) bag.get("dl")).intValue();
	liID = ((Integer) bag.get("li")).intValue();
	dtID = ((Integer) bag.get("dt")).intValue();
	ddID = ((Integer) bag.get("dd")).intValue();
	hrID = ((Integer) bag.get("hr")).intValue();
	spcID = ((Integer) bag.get("spacer")).intValue();
	textID = ((Integer) bag.get("text")).intValue();
	blankID = ((Integer) bag.get("spacer")).intValue();
    }
    
    public Hashtable getMapping() {
	return bag;
    }
    
    public static String getTagName(int i) {
	int i_0_ = tCode.length;
	for (int i_1_ = 0; i_1_ < i_0_; i_1_++) {
	    if (i == tCode[i_1_])
		return tName[i_1_];
	}
	return null;
    }
}
