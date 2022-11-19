/* TextViewHTMLContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public abstract class TextViewHTMLContainer extends TextViewHTMLElement
{
    String marker;
    String attributes;
    Object[] children = new Object[0];
    String prefix = null;
    String suffix = null;
    int[] lengths;
    
    public String prefix(Hashtable hashtable, char c) {
	return "";
    }
    
    public String suffix(Hashtable hashtable, char c) {
	return "";
    }
    
    public void setupContext(Hashtable hashtable) {
	/* empty */
    }
    
    public void cleanupContext(Hashtable hashtable) {
	/* empty */
    }
    
    public Hashtable attributesForPrefix
	(Hashtable hashtable, Hashtable hashtable_0_, TextView textview) {
	return hashtable_0_;
    }
    
    public Hashtable attributesForContents
	(Hashtable hashtable, Hashtable hashtable_1_, TextView textview) {
	return hashtable_1_;
    }
    
    public Hashtable attributesForSuffix
	(Hashtable hashtable, Hashtable hashtable_2_, TextView textview) {
	return hashtable_2_;
    }
    
    public String string(Hashtable hashtable) {
	FastStringBuffer faststringbuffer = new FastStringBuffer();
	int i = 0;
	if (children.length > 0)
	    lengths = new int[children.length];
	int i_3_ = 0;
	for (int i_4_ = children.length; i_3_ < i_4_; i_3_++) {
	    ((TextViewHTMLElement) children[i_3_])
		.appendString(hashtable, faststringbuffer);
	    if (i_3_ == 0) {
		lengths[i_3_] = faststringbuffer.length();
		i = lengths[i_3_];
	    } else {
		lengths[i_3_] = faststringbuffer.length() - i;
		i += lengths[i_3_];
	    }
	}
	return faststringbuffer.toString();
    }
    
    public Object[] children() {
	return children;
    }
    
    public Vector childrenVector() {
	Vector vector = new Vector();
	for (int i = 0; i < children.length; i++)
	    vector.addElement(children[i]);
	return vector;
    }
    
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
    
    public void setChildren(Object[] objects) {
	if (objects == null)
	    children = new Object[0];
	else
	    children = objects;
    }
    
    void appendString(Hashtable hashtable, FastStringBuffer faststringbuffer) {
	char c = '\0';
	if (faststringbuffer.length() > 0)
	    c = faststringbuffer.charAt(faststringbuffer.length() - 1);
	prefix = prefix(hashtable, c);
	if (prefix != null && prefix.length() > 0)
	    faststringbuffer.append(prefix);
	if (children != null) {
	    setupContext(hashtable);
	    faststringbuffer.append(string(hashtable));
	    cleanupContext(hashtable);
	}
	if (faststringbuffer.length() > 0)
	    c = faststringbuffer.charAt(faststringbuffer.length() - 1);
	suffix = suffix(hashtable, c);
	if (suffix != null && suffix.length() > 0)
	    faststringbuffer.append(suffix);
    }
    
    void setAttributesStartingAt(int i, Hashtable hashtable, TextView textview,
				 Hashtable hashtable_5_) {
	int i_6_ = 0;
	Object object = null;
	if (prefix != null && prefix.length() > 0) {
	    Hashtable hashtable_7_
		= attributesForPrefix(hashtable_5_, hashtable, textview);
	    if (hashtable_7_ != hashtable)
		textview.addAttributesForRange(hashtable_7_,
					       new Range(i, prefix.length()));
	    i_6_ += prefix.length();
	}
	if (appliesAttributesToChildren()) {
	    if (children != null && children.length > 0 && lengths != null) {
		Hashtable hashtable_8_
		    = attributesForContents(hashtable_5_, hashtable, textview);
		setupContext(hashtable_5_);
		int i_9_ = 0;
		for (int i_10_ = children.length; i_9_ < i_10_; i_9_++) {
		    ((TextViewHTMLElement) children[i_9_])
			.setAttributesStartingAt
			(i + i_6_, hashtable_8_, textview, hashtable_5_);
		    if (lengths != null)
			i_6_ += lengths[i_9_];
		    else
			i_6_ += ((TextViewHTMLElement) children[i_9_]).string
				    (hashtable_5_).length();
		}
		cleanupContext(hashtable_5_);
	    } else {
		Range range = TextView.allocateRange(i + i_6_, 0);
		Hashtable hashtable_11_
		    = attributesForContents(hashtable_5_, hashtable, textview);
		textview.addAttributesForRange(hashtable_11_, range);
		TextView.recycleRange(range);
	    }
	} else {
	    Hashtable hashtable_12_
		= attributesForContents(hashtable_5_, hashtable, textview);
	    if (hashtable_12_ != hashtable)
		textview.addAttributesForRange(hashtable_12_,
					       new Range(i + i_6_,
							 string
							     (hashtable_5_)
							     .length()));
	}
	if (suffix != null && suffix.length() > 0) {
	    Hashtable hashtable_13_
		= attributesForSuffix(hashtable_5_, hashtable, textview);
	    if (hashtable_13_ != hashtable)
		textview.addAttributesForRange(hashtable_13_,
					       new Range(i + i_6_,
							 suffix.length()));
	    i_6_ += suffix.length();
	}
    }
    
    public boolean appliesAttributesToChildren() {
	return true;
    }
    
    public void setString(String string) {
	/* empty */
    }
    
    public String toString() {
	StringBuffer stringbuffer = new StringBuffer();
	stringbuffer.append(marker + attributes);
	for (int i = 0; i < children.length; i++)
	    stringbuffer.append(children[i].toString());
	return stringbuffer.toString();
    }
}
