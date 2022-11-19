/* TextViewHTMLContainerImp - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public class TextViewHTMLContainerImp extends TextViewHTMLContainer
{
    static String[] markersStartingWithDoubleCarriageReturn
	= { "H1", "H2", "H3", "H4", "H5", "H6", "BLOCKQUOTE", "DL" };
    static String[] markersEndingWithDoubleCarriageReturn
	= { "H1", "H2", "H3", "H4", "H5", "H6", "BLOCKQUOTE", "DL" };
    static String[] markersStartingWithCarriageReturn
	= { "CENTER", "PRE", "OL", "UL", "MENU", "DIR", "ADDRESS", "P" };
    static String[] markersEndingWithCarriageReturn
	= { "CENTER", "PRE", "ADDRESS", "LI", "P" };
    static String[] markersStartingWithCarriageReturnOptionaly
	= { "DT", "DD" };
    static final String LIST_CONTEXT = "listctxt";
    
    private String currentListMarker(Hashtable hashtable) {
	Vector vector = (Vector) hashtable.get("listctxt");
	if (vector == null)
	    return null;
	Vector vector_0_ = (Vector) vector.lastElement();
	if (vector_0_ == null || vector_0_.count() != 2)
	    return null;
	return (String) vector_0_.elementAt(0);
    }
    
    private int levelOfCurrentListMarker(Hashtable hashtable) {
	Vector vector = (Vector) hashtable.get("listctxt");
	if (vector == null)
	    return 1;
	return vector.count();
    }
    
    private void addListInContext(String string, Hashtable hashtable) {
	Vector vector = (Vector) hashtable.get("listctxt");
	Vector vector_1_ = new Vector();
	vector_1_.addElement(string);
	vector_1_.addElement("0");
	if (vector != null)
	    vector.addElement(vector_1_);
	else {
	    vector = new Vector();
	    vector.addElement(vector_1_);
	    hashtable.put("listctxt", vector);
	}
    }
    
    private void removeLastListFromContext(Hashtable hashtable) {
	Vector vector = (Vector) hashtable.get("listctxt");
	Vector vector_2_;
	if (vector != null
	    && (vector_2_ = (Vector) vector.lastElement()) != null)
	    vector.removeLastElement();
    }
    
    private void bumpNumberOfListItemProcessed(Hashtable hashtable) {
	Vector vector = (Vector) hashtable.get("listctxt");
	Vector vector_3_;
	if (vector != null
	    && (vector_3_ = (Vector) vector.lastElement()) != null) {
	    String string = (String) vector_3_.elementAt(1);
	    string
		= String.valueOf(Integer.parseInt((String)
						  vector_3_.elementAt(1)) + 1);
	    vector_3_.removeLastElement();
	    vector_3_.addElement(string);
	}
    }
    
    private int numberOfListItemProcessed(Hashtable hashtable) {
	Vector vector = (Vector) hashtable.get("listctxt");
	Vector vector_4_;
	if (vector != null
	    && (vector_4_ = (Vector) vector.lastElement()) != null)
	    return Integer.parseInt((String) vector_4_.elementAt(1));
	return 0;
    }
    
    public String prefix(Hashtable hashtable, char c) {
	if (marker.equals("LI")) {
	    String string = currentListMarker(hashtable);
	    if (string == null)
		return null;
	    FastStringBuffer faststringbuffer = new FastStringBuffer();
	    int i = levelOfCurrentListMarker(hashtable);
	    for (int i_5_ = 0; i_5_ < i; i_5_++)
		faststringbuffer.append("\t");
	    if (string.equals("OL"))
		return (faststringbuffer.toString()
			+ (numberOfListItemProcessed(hashtable) + 1) + ". ");
	    return faststringbuffer.toString() + "\u00b7 ";
	}
	int i = 0;
	for (int i_6_ = markersStartingWithDoubleCarriageReturn.length;
	     i < i_6_; i++) {
	    if (markersStartingWithDoubleCarriageReturn[i].equals(marker)) {
		if (c != '\n')
		    return "\n\n";
		return "\n";
	    }
	}
	i = 0;
	for (int i_7_ = markersStartingWithCarriageReturn.length; i < i_7_;
	     i++) {
	    if (markersStartingWithCarriageReturn[i].equals(marker))
		return "\n";
	}
	if (c != '\n') {
	    i = 0;
	    for (int i_8_ = markersStartingWithCarriageReturnOptionaly.length;
		 i < i_8_; i++) {
		if (markersStartingWithCarriageReturnOptionaly[i]
			.equals(marker))
		    return "\n";
	    }
	}
	return "";
    }
    
    public String suffix(Hashtable hashtable, char c) {
	int i = 0;
	for (int i_9_ = markersEndingWithDoubleCarriageReturn.length; i < i_9_;
	     i++) {
	    if (markersEndingWithDoubleCarriageReturn[i].equals(marker)) {
		if (c != '\n')
		    return "\n\n";
		return "\n";
	    }
	}
	i = 0;
	for (int i_10_ = markersEndingWithCarriageReturn.length; i < i_10_;
	     i++) {
	    if (markersEndingWithCarriageReturn[i].equals(marker))
		return "\n";
	}
	return "";
    }
    
    public void setupContext(Hashtable hashtable) {
	if (marker.equals("OL") || marker.equals("UL") || marker.equals("DIR")
	    || marker.equals("MENU"))
	    addListInContext(marker, hashtable);
	else if (marker.equals("LI") && currentListMarker(hashtable) != null)
	    bumpNumberOfListItemProcessed(hashtable);
    }
    
    public void cleanupContext(Hashtable hashtable) {
	if (marker.equals("OL") || marker.equals("UL") || marker.equals("DIR")
	    || marker.equals("MENU"))
	    removeLastListFromContext(hashtable);
    }
    
    public String string(Hashtable hashtable) {
	if (marker.equals("TITLE"))
	    return "";
	return super.string(hashtable);
    }
    
    public Hashtable attributesForContents
	(Hashtable hashtable, Hashtable hashtable_11_, TextView textview) {
	Font font = Font.defaultFont();
	Hashtable hashtable_12_;
	if (hashtable_11_ != null && hashtable_11_.count() > 0) {
	    hashtable_12_
		= (Hashtable) TextView.hashtablePool.allocateObject();
	    Enumeration enumeration = hashtable_11_.keys();
	    while (enumeration.hasMoreElements()) {
		Object object = enumeration.nextElement();
		hashtable_12_.put(object, hashtable_11_.get(object));
	    }
	} else
	    hashtable_12_
		= (Hashtable) TextView.hashtablePool.allocateObject();
	if (marker.equals("H1"))
	    hashtable_12_.put("FontKey", Font.fontNamed(font.name(), 1, 24));
	else if (marker.equals("H2"))
	    hashtable_12_.put("FontKey", Font.fontNamed(font.name(), 1, 18));
	else if (marker.equals("H3"))
	    hashtable_12_.put("FontKey", Font.fontNamed(font.name(), 1, 16));
	else if (marker.equals("H4"))
	    hashtable_12_.put("FontKey", Font.fontNamed(font.name(), 1, 12));
	else if (marker.equals("H5"))
	    hashtable_12_.put("FontKey", Font.fontNamed(font.name(), 1, 10));
	else if (marker.equals("H6"))
	    hashtable_12_.put("FontKey", Font.fontNamed(font.name(), 1, 8));
	else if (marker.equals("B") || marker.equals("STRONG")) {
	    Font font_13_ = (Font) hashtable_12_.get("FontKey");
	    if (font_13_ != null) {
		if (!font_13_.isBold())
		    hashtable_12_.put("FontKey",
				      Font.fontNamed(font_13_.name(),
						     font_13_.style() | 0x1,
						     font_13_.size()));
	    } else
		hashtable_12_.put("FontKey",
				  Font.fontNamed(font.name(), 1, font.size()));
	} else if (marker.equals("CENTER")) {
	    TextParagraphFormat textparagraphformat
		= ((TextParagraphFormat)
		   hashtable_12_.get("ParagraphFormatKey"));
	    TextParagraphFormat textparagraphformat_14_;
	    if (textparagraphformat != null)
		textparagraphformat_14_
		    = (TextParagraphFormat) textparagraphformat.clone();
	    else
		textparagraphformat_14_
		    = ((TextParagraphFormat)
		       ((TextParagraphFormat)
			hashtable_11_.get("ParagraphFormatKey"))
			   .clone());
	    textparagraphformat_14_.setJustification(1);
	    hashtable_12_.put("ParagraphFormatKey", textparagraphformat_14_);
	} else if (marker.equals("BLOCKQUOTE") || marker.equals("DD")) {
	    TextParagraphFormat textparagraphformat
		= ((TextParagraphFormat)
		   hashtable_12_.get("ParagraphFormatKey"));
	    TextParagraphFormat textparagraphformat_15_;
	    if (textparagraphformat != null)
		textparagraphformat_15_
		    = (TextParagraphFormat) textparagraphformat.clone();
	    else
		textparagraphformat_15_
		    = ((TextParagraphFormat)
		       ((TextParagraphFormat)
			hashtable_11_.get("ParagraphFormatKey"))
			   .clone());
	    textparagraphformat_15_.setLeftMargin(50);
	    hashtable_12_.put("ParagraphFormatKey", textparagraphformat_15_);
	} else if (marker.equals("EM") || marker.equals("I")
		   || marker.equals("ADDRESS") || marker.equals("VAR")
		   || marker.equals("CITE")) {
	    Font font_16_ = (Font) hashtable_12_.get("FontKey");
	    if (font_16_ != null) {
		if (!font_16_.isItalic())
		    hashtable_12_.put("FontKey",
				      Font.fontNamed(font_16_.name(),
						     font_16_.style() | 0x2,
						     font_16_.size()));
	    } else
		hashtable_12_.put("FontKey",
				  Font.fontNamed(font.name(), 2, font.size()));
	} else if (marker.equals("PRE")) {
	    Font font_17_ = (Font) hashtable_12_.get("FontKey");
	    TextParagraphFormat textparagraphformat
		= ((TextParagraphFormat)
		   ((TextParagraphFormat)
		    hashtable_11_.get("ParagraphFormatKey"))
		       .clone());
	    if (font_17_ == null)
		font_17_ = Font.defaultFont();
	    hashtable_12_.put("FontKey",
			      Font.fontNamed("Courier", font_17_.style(),
					     font_17_.size()));
	    hashtable_12_.put("ParagraphFormatKey", textparagraphformat);
	} else if (marker.equals("TT") || marker.equals("CODE")
		   || marker.equals("SAMP") || marker.equals("KBD")) {
	    Font font_18_ = (Font) hashtable_12_.get("FontKey");
	    if (font_18_ == null)
		font_18_ = Font.defaultFont();
	    hashtable_12_.put("FontKey",
			      Font.fontNamed("Courier", font_18_.style(),
					     font_18_.size()));
	} else if (marker.equals("A")) {
	    Hashtable hashtable_19_
		= this.hashtableForHTMLAttributes(attributes);
	    if (hashtable_19_ != null) {
		String string;
		if ((string = (String) hashtable_19_.get("HREF")) != null)
		    hashtable_12_.put("LinkKey", string);
		String string_20_;
		if ((string_20_ = (String) hashtable_19_.get("NAME")) != null)
		    hashtable_12_.put("LinkDestinationKey", string_20_);
	    }
	} else if (marker.equals("LI")) {
	    TextParagraphFormat textparagraphformat
		= ((TextParagraphFormat)
		   hashtable_12_.get("ParagraphFormatKey"));
	    if (textparagraphformat == null)
		textparagraphformat
		    = ((TextParagraphFormat)
		       ((TextParagraphFormat)
			hashtable_11_.get("ParagraphFormatKey"))
			   .clone());
	    textparagraphformat.setWrapsUnderFirstCharacter(true);
	    hashtable_12_.put("ParagraphFormatKey", textparagraphformat);
	} else if (marker.equals("P")) {
	    Hashtable hashtable_21_
		= this.hashtableForHTMLAttributes(attributes);
	    String string;
	    if ((string = (String) hashtable_21_.get("ALIGN")) != null) {
		TextParagraphFormat textparagraphformat
		    = ((TextParagraphFormat)
		       hashtable_11_.get("ParagraphFormatKey"));
		textparagraphformat
		    = (TextParagraphFormat) textparagraphformat.clone();
		string = string.toUpperCase();
		if (string.equals("LEFT"))
		    textparagraphformat.setJustification(0);
		else if (string.equals("CENTER"))
		    textparagraphformat.setJustification(1);
		else if (string.equals("RIGHT"))
		    textparagraphformat.setJustification(2);
		hashtable_12_.put("ParagraphFormatKey", textparagraphformat);
	    }
	}
	return hashtable_12_;
    }
}
