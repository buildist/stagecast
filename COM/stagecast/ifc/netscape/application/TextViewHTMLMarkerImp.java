/* TextViewHTMLMarkerImp - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.net.URL;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;

public class TextViewHTMLMarkerImp extends TextViewHTMLMarker
{
    TextAttachment textAttachmentCache;
    
    public Hashtable attributesForMarker
	(Hashtable hashtable, Hashtable hashtable_0_, TextView textview) {
	if (marker.equals("HR") || marker.equals("IMG")) {
	    Hashtable hashtable_1_;
	    if (hashtable_0_ != null && hashtable_0_.count() > 0) {
		hashtable_1_
		    = (Hashtable) TextView.hashtablePool.allocateObject();
		Enumeration enumeration = hashtable_0_.keys();
		while (enumeration.hasMoreElements()) {
		    Object object = enumeration.nextElement();
		    hashtable_1_.put(object, hashtable_0_.get(object));
		}
	    } else
		hashtable_1_
		    = (Hashtable) TextView.hashtablePool.allocateObject();
	    TextAttachment textattachment
		= textAttachment(textview.baseURL(), textview);
	    hashtable_1_.put("TextAttachmentKey", textattachment);
	    if (marker.equals("IMG")) {
		Hashtable hashtable_2_
		    = this.hashtableForHTMLAttributes(attributes);
		String string;
		if (hashtable_2_ != null
		    && (string = (String) hashtable_2_.get("ALIGN")) != null) {
		    int i = 0;
		    if (string.equals("TOP")) {
			int i_3_ = 0;
			Font font = (Font) hashtable_1_.get("FontKey");
			if (font == null)
			    font = (Font) textview.defaultAttributes()
					      .get("FontKey");
			if (font != null) {
			    FontMetrics fontmetrics = new FontMetrics(font);
			    i_3_ = fontmetrics.ascent();
			}
			i = textattachment.height() - i_3_;
		    } else if (string.equals("MIDDLE"))
			i = textattachment.height() / 2;
		    hashtable_1_.put("TextAttachmentBaselineOffsetKey",
				     new Integer(i));
		}
	    }
	    return hashtable_1_;
	}
	return hashtable_0_;
    }
    
    public String prefix(Hashtable hashtable, char c) {
	if (marker.equals("HR"))
	    return "\n\n";
	return "";
    }
    
    public String string(Hashtable hashtable) {
	if (marker.equals("BR"))
	    return "\n";
	return "@";
    }
    
    private TextAttachment textAttachment(URL url, TextView textview) {
	if (textAttachmentCache == null) {
	    if (marker.equals("HR"))
		textAttachmentCache = new HRTextAttachment();
	    else if (marker.equals("IMG")) {
		int i = -1;
		int i_4_ = -1;
		Hashtable hashtable
		    = this.hashtableForHTMLAttributes(attributes);
		String string;
		if (hashtable != null
		    && (string = (String) hashtable.get("SRC")) != null) {
		    try {
			String string_5_ = (String) hashtable.get("WIDTH");
			if (string_5_ != null)
			    i = Integer.parseInt(string_5_);
			string_5_ = (String) hashtable.get("HEIGHT");
			if (string_5_ != null)
			    i_4_ = Integer.parseInt(string_5_);
		    } catch (NumberFormatException numberformatexception) {
			i = i_4_ = -1;
		    }
		    try {
			URL url_6_ = new URL(url, string);
			Bitmap bitmap;
			if ((bitmap = Bitmap.bitmapFromURL(url_6_)) != null)
			    bitmap.loadData();
			if (bitmap != null && bitmap.isValid())
			    textAttachmentCache = new ImageAttachment(bitmap);
		    } catch (java.net.MalformedURLException malformedurlexception) {
			System.err.println("Malformed URL " + string);
		    }
		}
		if (textAttachmentCache == null)
		    textAttachmentCache = new BrokenImageAttachment();
	    }
	}
	return textAttachmentCache;
    }
}
