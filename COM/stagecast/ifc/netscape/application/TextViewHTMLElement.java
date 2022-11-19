/* TextViewHTMLElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Hashtable;

public abstract class TextViewHTMLElement implements HTMLElement
{
    public Hashtable hashtableForHTMLAttributes(String string) {
	Hashtable hashtable = null;
	if (string != null) {
	    try {
		hashtable = HTMLParser.hashtableForAttributeString(string);
	    } catch (HTMLParsingException htmlparsingexception) {
		hashtable = null;
	    }
	}
	return hashtable;
    }
    
    public Font fontFromAttributes(Hashtable hashtable, TextView textview) {
	Font font = (Font) hashtable.get("FontKey");
	if (font == null) {
	    hashtable = textview.defaultAttributes();
	    font = (Font) hashtable.get("FontKey");
	    if (font == null)
		font = Font.defaultFont();
	}
	return font;
    }
    
    public abstract String string(Hashtable hashtable);
    
    abstract void appendString(Hashtable hashtable,
			       FastStringBuffer faststringbuffer);
    
    abstract void setAttributesStartingAt(int i, Hashtable hashtable,
					  TextView textview,
					  Hashtable hashtable_0_);
    
    public void setMarker(String string) {
	/* empty */
    }
    
    public void setAttributes(String string) {
	/* empty */
    }
    
    public void setString(String string) {
	/* empty */
    }
    
    public void setChildren(Object[] objects) {
	/* empty */
    }
}
