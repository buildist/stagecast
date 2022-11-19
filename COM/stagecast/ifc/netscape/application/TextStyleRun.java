/* TextStyleRun - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public class TextStyleRun
{
    TextParagraph _paragraph;
    FastStringBuffer _contents;
    Hashtable _attributes;
    FontMetrics _fontMetricsCache;
    int _remainder;
    
    public TextStyleRun() {
	/* empty */
    }
    
    TextStyleRun(TextParagraph textparagraph) {
	this();
	init(textparagraph);
    }
    
    TextStyleRun(TextParagraph textparagraph, String string,
		 Hashtable hashtable) {
	this();
	init(textparagraph, string, hashtable);
    }
    
    TextStyleRun(TextParagraph textparagraph, String string, int i, int i_0_,
		 Hashtable hashtable) {
	this();
	init(textparagraph, string, i, i_0_, hashtable);
    }
    
    void init(TextParagraph textparagraph) {
	_paragraph = textparagraph;
    }
    
    void init(TextParagraph textparagraph, String string,
	      Hashtable hashtable) {
	init(textparagraph);
	setText(string);
	setAttributes(hashtable);
    }
    
    void init(TextParagraph textparagraph, String string, int i, int i_1_,
	      Hashtable hashtable) {
	init(textparagraph);
	setText(string, i, i_1_);
	setAttributes(hashtable);
    }
    
    TextStyleRun createEmptyRun() {
	return (new TextStyleRun
		(_paragraph, "",
		 TextView.attributesByRemovingStaticAttributes(_attributes)));
    }
    
    TextStyleRun createEmptyRun(Hashtable hashtable) {
	return new TextStyleRun(_paragraph, "", hashtable);
    }
    
    void setParagraph(TextParagraph textparagraph) {
	_paragraph = textparagraph;
    }
    
    TextParagraph paragraph() {
	return _paragraph;
    }
    
    void setText(String string) {
	_contents = new FastStringBuffer(string);
    }
    
    void setText(String string, int i, int i_2_) {
	_contents = new FastStringBuffer(string, i, i_2_);
    }
    
    void setText(StringBuffer stringbuffer) {
	setText(stringbuffer.toString());
    }
    
    int rangeIndex() {
	int i = _paragraph._startChar;
	Vector vector = _paragraph.runsBefore(this);
	int i_3_ = 0;
	for (int i_4_ = vector.count(); i_3_ < i_4_; i_3_++)
	    i += ((TextStyleRun) vector.elementAt(i_3_)).charCount();
	return i;
    }
    
    Range range() {
	return TextView.allocateRange(rangeIndex(), charCount());
    }
    
    private Font getFont() {
	Font font = null;
	if (_paragraph.owner().usesSingleFont()) {
	    font
		= (Font) _paragraph.owner().defaultAttributes().get("FontKey");
	    return font;
	}
	if (_attributes != null)
	    font = (Font) _attributes.get("FontKey");
	if (font == null)
	    font
		= (Font) _paragraph.owner().defaultAttributes().get("FontKey");
	return font;
    }
    
    private Color getColor() {
	Object object = null;
	Color color;
	if (_attributes != null) {
	    if (_attributes.get("LinkKey") != null) {
		if (_attributes.get("_IFCLinkPressedKey") != null)
		    color = (Color) _attributes.get("PressedLinkColorKey");
		else
		    color = (Color) _attributes.get("LinkColorKey");
	    } else
		color = (Color) _attributes.get("TextColorKey");
	} else
	    color = (Color) _paragraph.owner().defaultAttributes()
				.get("TextColorKey");
	return color;
    }
    
    private void validateFontMetricsCache() {
	if (_paragraph.owner().usesSingleFont() || _attributes == null
	    || _attributes.get("FontKey") == null)
	    _fontMetricsCache = _paragraph.owner().defaultFontMetrics();
	else if (_fontMetricsCache == null)
	    _fontMetricsCache = getFont().fontMetrics();
    }
    
    private void invalidateFontMetricsCache() {
	_fontMetricsCache = null;
    }
    
    boolean containsATextAttachment() {
	if (_attributes != null
	    && _attributes.get("TextAttachmentKey") != null)
	    return true;
	return false;
    }
    
    Rect textAttachmentBoundsForOrigin(int i, int i_5_, int i_6_) {
	Rect rect = new Rect();
	TextAttachment textattachment;
	if (_attributes != null
	    && ((textattachment
		 = (TextAttachment) _attributes.get("TextAttachmentKey"))
		!= null)) {
	    rect.x = i;
	    rect.y = (i_5_ + i_6_ - textattachment.height()
		      + attachmentBaselineOffset());
	    rect.width = textattachment.width();
	    rect.height = textattachment.height();
	    return rect;
	}
	return null;
    }
    
    char charAt(int i) {
	FastStringBuffer faststringbuffer = _contents;
	if (faststringbuffer.length() == 0 || i >= faststringbuffer.length())
	    return '\0';
	return faststringbuffer.charAt(i);
    }
    
    void insertCharAt(char c, int i) {
	if (_contents == null) {
	    FastStringBuffer faststringbuffer = new FastStringBuffer(c);
	    _contents = faststringbuffer;
	} else {
	    FastStringBuffer faststringbuffer = _contents;
	    faststringbuffer.insert(c, i);
	}
    }
    
    void insertStringAt(String string, int i) {
	if (i >= 0 && string != null) {
	    if (_contents == null) {
		FastStringBuffer faststringbuffer
		    = new FastStringBuffer(string);
		_contents = faststringbuffer;
	    } else {
		FastStringBuffer faststringbuffer = _contents;
		faststringbuffer.insert(string, i);
	    }
	}
    }
    
    void removeCharAt(int i) {
	FastStringBuffer faststringbuffer = _contents;
	if (faststringbuffer.length() != 0 && i < faststringbuffer.length())
	    faststringbuffer.removeCharAt(i);
    }
    
    TextStyleRun breakAt(int i) {
	FastStringBuffer faststringbuffer = _contents;
	if (faststringbuffer.length() == 0 || i >= faststringbuffer.length())
	    return createEmptyRun(TextView.attributesByRemovingStaticAttributes
				  (_attributes));
	String string = faststringbuffer.toString();
	TextStyleRun textstylerun_7_
	    = (new TextStyleRun
	       (_paragraph, string.substring(i, faststringbuffer.length()),
		TextView.attributesByRemovingStaticAttributes(_attributes)));
	faststringbuffer.truncateToLength(i);
	return textstylerun_7_;
    }
    
    void cutBefore(int i) {
	FastStringBuffer faststringbuffer = _contents;
	if (faststringbuffer.length() != 0 && i < faststringbuffer.length())
	    faststringbuffer.moveChars(i, 0);
    }
    
    void cutAfter(int i) {
	FastStringBuffer faststringbuffer = _contents;
	if (faststringbuffer.length() != 0 && i < faststringbuffer.length())
	    faststringbuffer.truncateToLength(i);
    }
    
    String text() {
	return _contents.toString();
    }
    
    public String toString() {
	String string = "";
	if (_attributes != null)
	    string += _attributes.toString();
	else
	    string += "{DefAttr}";
	string += "**(";
	string += _contents.toString();
	string += ")**";
	return string;
    }
    
    int charCount() {
	return _contents.length();
    }
    
    int attachmentBaselineOffset() {
	Integer integer;
	if (_attributes != null
	    && ((integer
		 = ((Integer)
		    _attributes.get("TextAttachmentBaselineOffsetKey")))
		!= null))
	    return integer.intValue();
	return 0;
    }
    
    int height() {
	TextAttachment textattachment;
	if (_attributes != null
	    && ((textattachment
		 = (TextAttachment) _attributes.get("TextAttachmentKey"))
		!= null)) {
	    int i = attachmentBaselineOffset();
	    if (i > 0)
		return Math.max(textattachment.height(), i);
	    return textattachment.height() + Math.abs(i);
	}
	validateFontMetricsCache();
	return _fontMetricsCache.ascent() + _fontMetricsCache.descent();
    }
    
    int baseline() {
	TextAttachment textattachment;
	if (_attributes != null
	    && ((textattachment
		 = (TextAttachment) _attributes.get("TextAttachmentKey"))
		!= null)) {
	    int i = attachmentBaselineOffset();
	    return Math.max(textattachment.height() - i, 0);
	}
	validateFontMetricsCache();
	return _fontMetricsCache.ascent();
    }
    
    int _widthForTab(int i, int[] is) {
	if (is == null)
	    return 0;
	for (int i_8_ = 0; i_8_ < is.length; i_8_++) {
	    if (i < is[i_8_])
		return is[i_8_] - i;
	}
	return 0;
    }
    
    int _breakForSubstring(int i, int i_9_, int i_10_) {
	for (int i_11_ = _widthOfSubstring(i, i_9_, 0, null);
	     i_11_ > i_10_ && i_9_ > 0;
	     i_11_ = _widthOfSubstring(i, i_9_, 0, null))
	    i_9_--;
	return i_9_;
    }
    
    int charsForWidth(int i, int i_12_, int i_13_, int i_14_, int[] is) {
	int i_15_ = -1;
	int i_16_ = i_13_;
	char[] cs = new char[1];
	if (_contents == null) {
	    _remainder = i_13_;
	    return 0;
	}
	TextAttachment textattachment;
	if (_attributes != null
	    && ((textattachment
		 = (TextAttachment) _attributes.get("TextAttachmentKey"))
		!= null)) {
	    if (textattachment.width() > i_14_) {
		if (i_16_ == i_14_) {
		    _remainder = 0;
		    return 1;
		}
		return 0;
	    }
	    if (textattachment.width() <= i_16_) {
		_remainder = i_16_ - textattachment.width();
		return 1;
	    }
	    _remainder = i_16_;
	    return 0;
	}
	validateFontMetricsCache();
	int[] is_17_ = _fontMetricsCache.widthsArray();
	int i_18_ = _contents.length();
	int i_19_ = i;
	while (i_19_ < i_18_ && i_16_ > 0) {
	    int i_21_;
	    int i_20_ = i_21_ = 0;
	    int i_22_ = i_19_;
	    int i_23_ = -1;
	    while (i_19_ < i_18_ && _contents.buffer[i_19_] != ' '
		   && _contents.buffer[i_19_] != '\t') {
		if (_contents.buffer[i_19_] < '\u0100')
		    i_20_ += is_17_[_contents.buffer[i_19_]];
		else {
		    cs[0] = _contents.buffer[i_19_];
		    i_20_ += _fontMetricsCache.stringWidth(new String(cs));
		}
		i_19_++;
		if (i_20_ > i_16_ && i_23_ == -1) {
		    i_23_ = i_19_;
		    break;
		}
	    }
	    if (i_19_ < i_18_ && (_contents.buffer[i_19_] == ' '
				  || _contents.buffer[i_19_] == '\t')) {
		for (/**/;
		     i_19_ < i_18_ && (_contents.buffer[i_19_] == ' '
				       || _contents.buffer[i_19_] == '\t');
		     i_19_++) {
		    if (_contents.buffer[i_19_] == ' ')
			i_21_ += is_17_[32];
		    else {
			i_21_ += _widthForTab(i_12_ + i_20_ + i_21_, is);
			if (i_15_ == -1)
			    i_15_ = i_19_;
		    }
		}
	    }
	    if (i_20_ + i_21_ <= i_16_)
		i_16_ -= i_20_ + i_21_;
	    else {
		if (i_16_ <= i_14_ && i_20_ <= i_16_)
		    i_16_ -= i_20_;
		else if (i_20_ > i_16_ && i_16_ >= i_14_) {
		    if (i_23_ != -1)
			i_18_
			    = _breakForSubstring(i_22_, i_23_ - i_22_, i_16_);
		    else
			i_18_
			    = _breakForSubstring(i_22_, i_19_ - i_22_, i_16_);
		    if (i_18_ > 0) {
			i_19_ = i + i_18_;
			i_16_ -= _widthOfSubstring(i, i_18_, 0, null);
		    } else
			i_19_ = i_22_;
		} else
		    i_19_ = i_22_;
		break;
	    }
	}
	if (i_16_ > 0)
	    _remainder = i_16_;
	else
	    _remainder = 0;
	if (i_19_ == i && i_16_ == i_14_) {
	    _remainder = 0;
	    return 1;
	}
	return i_19_ - i;
    }
    
    int _widthOfSubstring(int i, int i_24_, int i_25_, int[] is) {
	int i_26_ = 0;
	char[] cs = new char[1];
	validateFontMetricsCache();
	int[] is_27_ = _fontMetricsCache.widthsArray();
	int i_28_ = i + i_24_;
	for (int i_29_ = i; i_29_ < i_28_; i_29_++) {
	    if (_contents.buffer[i_29_] == '\t' && is != null)
		i_26_ += _widthForTab(i_25_ + i_26_, is);
	    else if (_contents.buffer[i_29_] < '\u0100')
		i_26_ += is_27_[_contents.buffer[i_29_]];
	    else {
		cs[0] = _contents.buffer[i_29_];
		i_26_ += _fontMetricsCache.stringWidth(new String(cs));
	    }
	}
	return i_26_;
    }
    
    int widthOfContents(int i, int i_30_, int i_31_, int[] is) {
	TextAttachment textattachment;
	if (_attributes != null
	    && ((textattachment
		 = (TextAttachment) _attributes.get("TextAttachmentKey"))
		!= null))
	    return textattachment.width();
	validateFontMetricsCache();
	if (i_30_ == 0)
	    return 0;
	if (i < 0)
	    i = 0;
	if (i + i_30_ > _contents.length())
	    i_30_ = _contents.length() - i;
	return _widthOfSubstring(i, i_30_, i_31_, is);
    }
    
    int drawCharacters(Graphics graphics, int i, int i_32_, int i_33_,
		       int i_34_, int[] is) {
	if (graphics == null)
	    return 0;
	TextAttachment textattachment;
	if (_attributes != null
	    && ((textattachment
		 = (TextAttachment) _attributes.get("TextAttachmentKey"))
		!= null)) {
	    int i_35_ = attachmentBaselineOffset();
	    Rect rect
		= Rect.newRect(i_33_, i_34_ - textattachment.height() + i_35_,
			       0, 0);
	    rect.width = textattachment.width();
	    rect.height = textattachment.height();
	    textattachment.drawInRect(graphics, rect);
	    Rect.returnRect(rect);
	    return textattachment.width();
	}
	validateFontMetricsCache();
	if (_fontMetricsCache == null || i_32_ <= 0)
	    return 0;
	graphics.setFont(getFont());
	graphics.setColor(getColor());
	if (i < 0)
	    i = 0;
	if (i + i_32_ > _contents.length())
	    i_32_ = _contents.length() - i;
	char[] cs = _contents.charArray();
	int[] is_36_ = _fontMetricsCache.widthsArray();
	int i_37_ = i + i_32_;
	int i_38_ = 0;
	int i_39_;
	for (/**/; i < i_37_; i += i_39_) {
	    int i_40_ = _contents.indexOf('\t', i);
	    if (i_40_ == -1)
		i_39_ = i_37_ - i;
	    else
		i_39_ = i_40_ - i;
	    if (i_39_ > 0)
		graphics.drawChars(cs, i, i_39_, i_33_, i_34_);
	    if (i_40_ != -1)
		i_39_++;
	    int i_41_ = _widthOfSubstring(i, i_39_, i_33_, is);
	    i_33_ += i_41_;
	    i_38_ += i_41_;
	}
	return i_38_;
    }
    
    void setAttributes(Hashtable hashtable) {
	if (hashtable != null) {
	    invalidateFontMetricsCache();
	    _attributes = (Hashtable) hashtable.clone();
	} else
	    _attributes = null;
	if (_attributes != null
	    && _attributes.get("ParagraphFormatKey") != null)
	    _attributes.remove("ParagraphFormatKey");
    }
    
    void appendAttributes(Hashtable hashtable) {
	if (hashtable != null) {
	    if (_attributes == null)
		_attributes = ((Hashtable)
			       _paragraph.owner().defaultAttributes().clone());
	    Enumeration enumeration = hashtable.keys();
	    while (enumeration.hasMoreElements()) {
		String string = (String) enumeration.nextElement();
		if (string.equals("FontKey"))
		    invalidateFontMetricsCache();
		_attributes.put(string, hashtable.get(string));
	    }
	}
    }
    
    Hashtable attributes() {
	if (_attributes != null) {
	    _attributes.put("ParagraphFormatKey",
			    _paragraph.currentParagraphFormat());
	    return _attributes;
	}
	return null;
    }
}
