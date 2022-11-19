/* Font - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.net.URL;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public class Font
{
    FontMetrics _metrics;
    String _name;
    URL _url;
    Bitmap _glyphsImage;
    Vector _glyphVector;
    Vector _widthsVector;
    Hashtable _description;
    java.awt.Font _awtFont;
    int _type = 0;
    int _widthsArrayBase;
    int[] _widthsArray;
    static final int INVALID = 0;
    static final int AWT = 1;
    static final int DOWNLOADED = 2;
    public static final int PLAIN = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    static final String FAMILY = "Family";
    static final String STYLE = "Style";
    static final String SIZE = "Size";
    static final String WIDTHS = "Widths";
    static final String DESCRIPTION = "Description";
    static final String GLYPHS = "glyphs.gif";
    private static Class fontClass;
    
    private static Class fontClass() {
	if (fontClass == null)
	    fontClass = new Font().getClass();
	return fontClass;
    }
    
    public Font() {
	/* empty */
    }
    
    public Font(String string, int i, int i_0_) {
	this();
	_awtFont = new java.awt.Font(string, i, i_0_);
	_name = string;
	if (_awtFont != null)
	    _type = 1;
    }
    
    public static Font defaultFont() {
	return fontNamed("Helvetica", 0, 12);
    }
    
    public static synchronized Font fontNamed(String string, int i, int i_1_) {
	if (string == null || i_1_ == 0)
	    return null;
	String string_2_ = string + "." + i + "." + i_1_;
	Application application = Application.application();
	Font font = (Font) application.fontByName.get(string_2_);
	if (font != null)
	    return font;
	font = new Font(string, i, i_1_);
	if (!font.isValid())
	    return null;
	application.fontByName.put(string_2_, font);
	return font;
    }
    
    public static Font fontNamed(String string) {
	String[] strings = stringsForString(string);
	if (strings == null || strings.length == 0)
	    return null;
	String string_3_ = strings[0];
	if (string_3_.equals("Default"))
	    return defaultFont();
	if (string_3_.length() == 0)
	    return null;
	if (strings.length == 1) {
	    Application application = Application.application();
	    URL url = application._appResources.urlForFontNamed(string_3_);
	    return null;
	}
	String string_4_ = strings[1];
	char c = string_4_.charAt(0);
	int i;
	if (c == 'P' || c == 'p')
	    i = 0;
	else if (c == 'B' || c == 'b')
	    i = 1;
	else if (c == 'I' || c == 'i')
	    i = 2;
	else {
	    try {
		i = Integer.parseInt(string_4_);
	    } catch (NumberFormatException numberformatexception) {
		i = 0;
	    }
	}
	int i_5_ = parseInt(strings[2]);
	if (i_5_ < 0)
	    i_5_ = 0;
	return fontNamed(string_3_, i, i_5_);
    }
    
    private static int parseInt(String string) {
	try {
	    return Integer.parseInt(string);
	} catch (NumberFormatException numberformatexception) {
	    return 0;
	}
    }
    
    private static String[] stringsForString(String string) {
	if (string == null)
	    return null;
	if (string.indexOf(':') == -1) {
	    String[] strings = new String[1];
	    strings[0] = string;
	    return strings;
	}
	int i = string.length();
	StringBuffer stringbuffer = new StringBuffer();
	Vector vector = new Vector();
	for (int i_6_ = 0; i_6_ < i; i_6_++) {
	    if (string.charAt(i_6_) == ':') {
		vector.addElement(stringbuffer.toString());
		stringbuffer = new StringBuffer();
	    } else
		stringbuffer.append(string.charAt(i_6_));
	}
	vector.addElement(stringbuffer.toString());
	i = vector.count();
	String[] strings = new String[i];
	for (int i_7_ = 0; i_7_ < i; i_7_++)
	    strings[i_7_] = (String) vector.elementAt(i_7_);
	return strings;
    }
    
    synchronized void nameFont(String string, Font font_8_) {
	Application.application().fontByName.put(string, font_8_);
    }
    
    void _loadWidths() {
	if (_description != null) {
	    Object[] objects = (Object[]) _description.get("Widths");
	    if (objects == null)
		System.err.println
		    ("Font._loadWidths() - No widths information for "
		     + _name);
	    else {
		_widthsArrayBase = parseInt((String) objects[0]);
		int i = objects.length - 1;
		_widthsArray = new int[_widthsArrayBase + i];
		for (int i_9_ = 0; i_9_ < _widthsArrayBase; i_9_++)
		    _widthsArray[i_9_] = 0;
		int i_10_ = 1;
		int i_11_ = _widthsArrayBase;
		while (i_10_ < i) {
		    _widthsArray[i_11_] = parseInt((String) objects[i_10_]);
		    i_10_++;
		    i_11_++;
		}
		if (_widthsArray[32] == 0)
		    _widthsArray[32] = 5;
	    }
	}
    }
    
    java.awt.Image croppedImage(int i, int i_12_, int i_13_, int i_14_) {
	java.awt.Image image
	    = (AWTCompatibility.awtApplet().createImage
	       (new FilteredImageSource(_glyphsImage.awtImage().getSource(),
					new CropImageFilter(i, i_12_, i_13_,
							    i_14_))));
	return image;
    }
    
    void _loadGlyphs(URL url) {
	URL url_15_;
	try {
	    url_15_ = new URL(url, _name + ".font/" + "glyphs.gif");
	} catch (Exception exception) {
	    System.err.println("Font.init() - Trouble creating font glyph URL "
			       + url + _name + ".font/" + "glyphs.gif" + " : "
			       + exception);
	    _type = 0;
	    return;
	}
	_glyphsImage = Bitmap.bitmapFromURL(url_15_);
	_glyphsImage.loadData();
	if (_glyphsImage == null || !_glyphsImage.isValid())
	    System.err.println
		("Font._loadGlyphs() - Trouble loading glyphs for " + _name);
	else {
	    _glyphsImage.loadData();
	    int i = _widthsArrayBase;
	    int i_16_ = 0;
	    for (/**/; i < _widthsArray.length; i++) {
		java.awt.Image image = croppedImage(i_16_, 0, _widthsArray[i],
						    _glyphsImage.height());
		_glyphVector
		    .addElement(AWTCompatibility.bitmapForAWTImage(image));
		Image image_17_ = (Image) _glyphVector.lastElement();
		i_16_ += _widthsArray[i];
	    }
	}
    }
    
    boolean isValid() {
	return _type != 0;
    }
    
    boolean wasDownloaded() {
	return _type == 2;
    }
    
    public FontMetrics fontMetrics() {
	if (_metrics == null)
	    _metrics = new FontMetrics(this);
	return _metrics;
    }
    
    public String family() {
	if (_type == 0)
	    return "";
	if (_awtFont != null)
	    return _awtFont.getFamily();
	return (String) _description.get("Family");
    }
    
    public String name() {
	if (_type == 0)
	    return "";
	return _name;
    }
    
    public int style() {
	if (_type == 0)
	    return -1;
	if (_awtFont != null)
	    return _awtFont.getStyle();
	return parseInt((String) _description.get("Style"));
    }
    
    public int size() {
	if (_type == 0)
	    return -1;
	if (_awtFont != null)
	    return _awtFont.getSize();
	return parseInt((String) _description.get("Size"));
    }
    
    public boolean isPlain() {
	return style() == 0;
    }
    
    public boolean isBold() {
	return (style() & 0x1) > 0;
    }
    
    public boolean isItalic() {
	return (style() & 0x2) > 0;
    }
    
    Vector glyphVector() {
	return _glyphVector;
    }
    
    String _stringValueFromDescription(String string) {
	if (string == null || _description == null)
	    return "";
	return (String) _description.get(string);
    }
    
    int _intValueFromDescription(String string) {
	if (string == null || _description == null)
	    return 0;
	return parseInt((String) _description.get(string));
    }
    
    public String toString() {
	if (_type == 0 || wasDownloaded())
	    return _name;
	String string;
	if (isBold()) {
	    if (isItalic())
		string = "BoldItalic";
	    else
		string = "Bold";
	} else if (isItalic())
	    string = "Italic";
	else
	    string = "Plain";
	return family() + ":" + string + ":" + size();
    }
}
