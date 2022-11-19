/* TextViewHTMLString - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Hashtable;

public class TextViewHTMLString extends TextViewHTMLElement
{
    String string;
    
    public String string(Hashtable hashtable) {
	return string;
    }
    
    public String string() {
	return string;
    }
    
    void appendString(Hashtable hashtable, FastStringBuffer faststringbuffer) {
	faststringbuffer.append(string(hashtable));
    }
    
    void setAttributesStartingAt(int i, Hashtable hashtable, TextView textview,
				 Hashtable hashtable_0_) {
	String string = string(hashtable_0_);
	int i_1_ = string.length();
	if (hashtable != null && i_1_ > 0) {
	    Range range = TextView.allocateRange(i, i_1_);
	    textview.addAttributesForRange(hashtable, range);
	    TextView.recycleRange(range);
	}
    }
    
    public void setString(String string) {
	this.string = string;
    }
    
    public void setMarker(String string) {
	/* empty */
    }
    
    public void setAttributes(String string) {
	/* empty */
    }
    
    public void setChildren(Object[] objects) {
	/* empty */
    }
    
    public String toString() {
	return string;
    }
}
