/* Resource - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ImageResources;
import COM.stagecast.playwrite.internationalization.ResourceIDs;
import COM.stagecast.playwrite.internationalization.TextResources;
import COM.stagecast.playwrite.internationalization.ToolTipResources;

public class Resource
    implements Debug.Constants, ResourceIDs.CommandIDs, ResourceIDs.DialogIDs
{
    public static final String CREATOR_RESOURCE_PATH
	= "/COM/stagecast/creator/";
    private static final Locale _locale;
    private static final NumberFormat _numberFormat;
    private static final char _minusChar;
    private static final String _minusString;
    private static final char _decimalSeparator;
    private static ResourceBundle _textResources;
    private static ResourceBundle _toolTipResources;
    private static ResourceBundle _imageResources;
    private static final Hashtable _loadedImageResources = new Hashtable();
    private static boolean _noImagesRetrieved;
    
    public static interface FormatCallback
    {
	public void embedText(String string);
	
	public void appendText(String string);
	
	public void embedObject(Object object);
	
	public void appendObject(Object object);
    }
    
    static {
	Locale defaultLocale = Locale.getDefault();
	_locale
	    = (new Locale
	       (PlaywriteSystem.getApplicationProperty("locale_language",
						       defaultLocale
							   .getLanguage()),
		PlaywriteSystem.getApplicationProperty("locale_country",
						       defaultLocale
							   .getCountry())));
	_numberFormat = NumberFormat.getNumberInstance();
	_numberFormat.setMinimumIntegerDigits(1);
	_numberFormat.setMaximumIntegerDigits(15);
	_numberFormat.setMaximumFractionDigits(15);
	_numberFormat.setGroupingUsed(false);
	DecimalFormat df
	    = (_numberFormat instanceof DecimalFormat
	       ? (DecimalFormat) _numberFormat : new DecimalFormat());
	DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
	_minusChar = dfs.getMinusSign();
	_decimalSeparator = dfs.getDecimalSeparator();
	_minusString = new String(new char[] { _minusChar });
	try {
	    _textResources
		= (ResourceBundle.getBundle
		   ("COM.stagecast.playwrite.internationalization.TextResources",
		    _locale));
	} catch (Exception exception) {
	    _textResources = new TextResources();
	}
	try {
	    _imageResources
		= (ResourceBundle.getBundle
		   ("COM.stagecast.playwrite.internationalization.ImageResources",
		    _locale));
	} catch (OutOfMemoryError e) {
	    throw e;
	} catch (Throwable throwable) {
	    _imageResources = new ImageResources();
	}
	try {
	    _toolTipResources
		= (ResourceBundle.getBundle
		   ("COM.stagecast.playwrite.internationalization.ToolTipResources",
		    _locale));
	} catch (Exception exception) {
	    Debug.print("debug.internationalization",
			"Unable to locale the tool tips resource bundle.");
	    _toolTipResources = new ToolTipResources();
	}
	_noImagesRetrieved = true;
    }
    
    public static ResourceBundle getTextResourceBundle() {
	return _textResources;
    }
    
    public static char getDecimalSeparator() {
	return _decimalSeparator;
    }
    
    public static Locale getLocale() {
	return _locale;
    }
    
    public static String getText(String key) {
	return getText(_textResources, key);
    }
    
    public static String getText(ResourceBundle resourceBundle$, String key) {
	try {
	    return resourceBundle$.getString(key);
	} catch (MissingResourceException e) {
	    Debug.print("debug.internationalization", e, " ", key);
	    return key;
	}
    }
    
    public static String getTextAndFormat(String key, Object[] params) {
	return getTextAndFormat(_textResources, key, params);
    }
    
    public static String getTextAndFormat(ResourceBundle resourceBundle$,
					  String key, Object[] params) {
	try {
	    String text = getText(resourceBundle$, key);
	    return format(text, params);
	} catch (MissingResourceException e) {
	    Debug.print("debug.internationalization", e, " ", key);
	    return key;
	}
    }
    
    public static String format(String template, Object[] params) {
	MessageFormat formatter = new MessageFormat(template);
	formatter.setLocale(_locale);
	return formatter.format(params);
    }
    
    public static String getMinusString() {
	return _minusString;
    }
    
    static String getToolTip(String key) throws MissingResourceException {
	return _toolTipResources.getString(key);
    }
    
    public static Bitmap getImage(String key) {
	return getImage(key, null);
    }
    
    public static Bitmap getButtonImage(String key) {
	return getImage(key, new Object[] { new Integer(0) });
    }
    
    public static Bitmap getAltButtonImage(String key) {
	return getImage(key, new Object[] { new Integer(1) });
    }
    
    public static Bitmap getImage(String key, Object[] params) {
	Bitmap result = (Bitmap) _loadedImageResources.get(key + params);
	if (result == null) {
	    try {
		String relativePath
		    = _imageResources.getObject(key).toString();
		if (params != null) {
		    MessageFormat formatter = new MessageFormat(relativePath);
		    formatter.setLocale(_locale);
		    relativePath = formatter.format(params);
		}
		result = ImageResources.fetch(relativePath);
	    } catch (MissingResourceException missingresourceexception) {
		throw new PlaywriteInternalError("Resource not defined: "
						 + key);
	    }
	    if (result != null) {
		_noImagesRetrieved = false;
		_loadedImageResources.put(key, result);
	    } else {
		if (_noImagesRetrieved) {
		    PlaywriteDialog noImagesDlg
			= (new PlaywriteDialog
			   (getText("dialog no image access"), "command ok"));
		    noImagesDlg.getAnswer();
		}
		throw new PlaywriteInternalError("Image missing: " + key);
	    }
	}
	return result;
    }
    
    public static String formatNumber(Number n) {
	String s = _numberFormat.format(n);
	return s.length() < 16 ? s : s.substring(0, 16);
    }
    
    public static Object parseNumberString(String original) {
	String trimmedString = original.trim();
	Object newValue = trimmedString;
	if (trimmedString.length() == 0)
	    return null;
	if (trimmedString.charAt(0) != 'E') {
	    ParsePosition parsePosition = new ParsePosition(0);
	    try {
		newValue = _numberFormat.parse(trimmedString, parsePosition);
	    } catch (NumberFormatException numberformatexception) {
		if (trimmedString.charAt(0) == _minusChar)
		    return new Long(-9223372036854775808L);
		return new Long(9223372036854775807L);
	    }
	    if (parsePosition.getIndex() == trimmedString.length())
		return newValue;
	}
	return original;
    }
    
    public static void format(FormatCallback formatApplication$,
			      String formatResourceID$, Object[] args$,
			      Object[] viewArgs$) {
	format(_textResources, formatApplication$, formatResourceID$, args$,
	       viewArgs$);
    }
    
    public static void format
	(ResourceBundle resourceBundle$, FormatCallback formatApplication$,
	 String formatResourceID$, Object[] args$, Object[] viewArgs$) {
	String iText;
	if (args$ == null)
	    iText = getText(resourceBundle$, formatResourceID$);
	else
	    iText
		= getTextAndFormat(resourceBundle$, formatResourceID$, args$);
	if (viewArgs$ == null)
	    formatApplication$.appendText(iText);
	else {
	    int pos = 0;
	    int oldpos = 0;
	    int len = iText.length();
	    while (pos < len) {
		oldpos = pos;
		pos = iText.indexOf('<', pos);
		if (pos == -1) {
		    pos = len;
		    if (oldpos == len)
			continue;
		}
		if (pos > 0 && pos < len && iText.charAt(pos) == '<'
		    && iText.charAt(pos - 1) == '\\')
		    pos++;
		else {
		    if (pos > 0 && pos != oldpos) {
			String nextBit = iText.substring(oldpos, pos);
			formatApplication$.appendText(nextBit);
		    }
		    if (pos < len) {
			boolean embed = iText.charAt(pos + 1) == '!';
			if (embed)
			    pos++;
			oldpos = pos;
			pos = iText.indexOf('>', oldpos);
			if (pos == -1)
			    throw new PlaywriteInternalError
				      ("Unmatched '<' in resource:"
				       + formatResourceID$);
			String numberString = iText.substring(oldpos + 1, pos);
			Object number = parseNumberString(numberString);
			if (number instanceof Number && viewArgs$ != null) {
			    int n = ((Number) number).intValue();
			    if (n >= 0 && n < viewArgs$.length) {
				if (embed)
				    formatApplication$
					.embedObject(viewArgs$[n]);
				else
				    formatApplication$
					.appendObject(viewArgs$[n]);
			    }
			}
			pos++;
		    }
		}
	    }
	}
    }
}
