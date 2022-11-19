/* FontMetrics - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class FontMetrics
{
    Font _font;
    java.awt.FontMetrics _awtMetrics;
    static final String LEADING = "Leading";
    static final String ASCENT = "Ascent";
    static final String DESCENT = "Descent";
    static final String TOTAL_HEIGHT = "Total Height";
    static final String MAX_ASCENT = "Maximum Ascent";
    static final String MAX_DESCENT = "Maximum Descent";
    static final String MAX_ADVANCE = "Maximum Advance";
    
    public FontMetrics() {
	/* empty */
    }
    
    public FontMetrics(Font font) {
	this();
	_font = font;
	if (!_font.wasDownloaded())
	    _awtMetrics
		= AWTCompatibility.awtToolkit().getFontMetrics(_font._awtFont);
    }
    
    FontMetrics(java.awt.FontMetrics fontmetrics_0_) {
	this();
	_font = AWTCompatibility.fontForAWTFont(fontmetrics_0_.getFont());
	_awtMetrics = fontmetrics_0_;
    }
    
    public Font font() {
	return _font;
    }
    
    public int leading() {
	if (_awtMetrics != null)
	    return _awtMetrics.getLeading();
	return _font._intValueFromDescription("Leading");
    }
    
    public int ascent() {
	if (_awtMetrics != null)
	    return _awtMetrics.getAscent();
	return _font._intValueFromDescription("Ascent");
    }
    
    public int descent() {
	if (_awtMetrics != null)
	    return _awtMetrics.getDescent();
	return _font._intValueFromDescription("Descent");
    }
    
    public int height() {
	if (_awtMetrics != null)
	    return _awtMetrics.getHeight();
	return _font._intValueFromDescription("Total Height");
    }
    
    public int charHeight() {
	if (_awtMetrics != null)
	    return _awtMetrics.getAscent() + _awtMetrics.getDescent();
	return (_font._intValueFromDescription("Ascent")
		+ _font._intValueFromDescription("Descent"));
    }
    
    public int maxAscent() {
	if (_awtMetrics != null)
	    return _awtMetrics.getMaxAscent();
	return _font._intValueFromDescription("Maximum Ascent");
    }
    
    public int maxDescent() {
	if (_awtMetrics != null)
	    return _awtMetrics.getMaxDecent();
	return _font._intValueFromDescription("Maximum Descent");
    }
    
    public int maxAdvance() {
	if (_awtMetrics != null)
	    return _awtMetrics.getMaxAdvance();
	return _font._intValueFromDescription("Maximum Advance");
    }
    
    public int charWidth(int i) {
	if (_awtMetrics != null)
	    return _awtMetrics.charWidth(i);
	return 0;
    }
    
    public int charWidth(char c) {
	if (_awtMetrics != null)
	    return _awtMetrics.charWidth(c);
	return 0;
    }
    
    public int stringWidth(String string) {
	if (string == null)
	    return 0;
	if (_awtMetrics != null)
	    return _awtMetrics.stringWidth(string);
	int i = 0;
	for (int i_1_ = 0; i_1_ < string.length(); i_1_++) {
	    char c = string.charAt(i_1_);
	    if (c >= 0 && c < _font._widthsArray.length)
		i += _font._widthsArray[c];
	}
	return i;
    }
    
    public int stringHeight() {
	return ascent() + descent();
    }
    
    public Size stringSize(String string) {
	return new Size(stringWidth(string), stringHeight());
    }
    
    public int charsWidth(char[] cs, int i, int i_2_) {
	if (_awtMetrics != null)
	    return _awtMetrics.charsWidth(cs, i, i_2_);
	return 0;
    }
    
    public int bytesWidth(byte[] is, int i, int i_3_) {
	if (_awtMetrics != null)
	    return _awtMetrics.bytesWidth(is, i, i_3_);
	return 0;
    }
    
    public int[] widthsArray() {
	if (_awtMetrics != null)
	    return _awtMetrics.getWidths();
	return _font._widthsArray;
    }
    
    public int widthsArrayBase() {
	return _font._widthsArrayBase;
    }
    
    public String toString() {
	if (_awtMetrics != null)
	    return _awtMetrics.toString();
	return "";
    }
}
