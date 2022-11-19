/* TextViewHTMLMarker - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Hashtable;

public abstract class TextViewHTMLMarker extends TextViewHTMLElement
{
    String marker;
    String attributes;
    String prefix;
    String string;
    String suffix;
    
    public String prefix(Hashtable hashtable, char c) {
	return "";
    }
    
    public String suffix(Hashtable hashtable, char c) {
	return "";
    }
    
    public Hashtable attributesForPrefix
	(Hashtable hashtable, Hashtable hashtable_0_, TextView textview) {
	return hashtable_0_;
    }
    
    public Hashtable attributesForMarker
	(Hashtable hashtable, Hashtable hashtable_1_, TextView textview) {
	return hashtable_1_;
    }
    
    public Hashtable attributesForSuffix
	(Hashtable hashtable, Hashtable hashtable_2_, TextView textview) {
	return hashtable_2_;
    }
    
    public abstract String string(Hashtable hashtable);
    
    public String marker() {
	return marker;
    }
    
    public Hashtable attributes() {
	return this.hashtableForHTMLAttributes(attributes);
    }
    
    public void setMarker(String string) {
	marker = string;
    }
    
    public void setAttributes(String string) {
	attributes = string;
    }
    
    void appendString(Hashtable hashtable, FastStringBuffer faststringbuffer) {
	char c = '\0';
	if (faststringbuffer.length() > 0)
	    c = faststringbuffer.charAt(faststringbuffer.length() - 1);
	prefix = prefix(hashtable, c);
	if (prefix != null && prefix.length() > 0)
	    faststringbuffer.append(prefix);
	string = string(hashtable);
	if (string != null && string.length() > 0)
	    faststringbuffer.append(string);
	else
	    string = "";
	if (faststringbuffer.length() > 0)
	    c = faststringbuffer.charAt(faststringbuffer.length() - 1);
	suffix = suffix(hashtable, c);
	if (suffix != null && suffix.length() > 0)
	    faststringbuffer.append(suffix);
    }
    
    void setAttributesStartingAt(int i, Hashtable hashtable, TextView textview,
				 Hashtable hashtable_3_) {
	int i_4_ = 0;
	if (prefix != null && prefix.length() > 0) {
	    Hashtable hashtable_5_
		= attributesForPrefix(hashtable_3_, hashtable, textview);
	    if (hashtable_5_ != hashtable)
		textview.addAttributesForRange(hashtable_5_,
					       new Range(i, prefix.length()));
	    i_4_ += prefix.length();
	}
	if (string == null)
	    string = string(hashtable_3_);
	if (string != null && string.length() > 0) {
	    Hashtable hashtable_6_
		= attributesForMarker(hashtable_3_, hashtable, textview);
	    textview.addAttributesForRange(hashtable_6_,
					   new Range(i + i_4_,
						     string.length()));
	    i_4_ += string.length();
	}
	if (suffix != null && suffix.length() > 0) {
	    Hashtable hashtable_7_
		= attributesForSuffix(hashtable_3_, hashtable, textview);
	    if (hashtable_7_ != hashtable)
		textview.addAttributesForRange(hashtable_7_,
					       new Range(i + i_4_,
							 suffix.length()));
	    i_4_ += suffix.length();
	}
    }
    
    public void setChildren(Object[] objects) {
	/* empty */
    }
    
    public void setString(String string) {
	/* empty */
    }
    
    public String toString() {
	return marker + attributes;
    }
}
