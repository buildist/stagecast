/* HTMLParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public class HTMLParser extends FilterInputStream
{
    private static final String[] specialChars
	= { "lt", "<", "gt", ">", "amp", "&", "quot", "\"", "nbsp", "\u00a0",
	    "iexcl", "\u00a1", "cent", "\u00a2", "pound", "\u00a3", "curren",
	    "\u00a4", "yen", "\u00a5", "brvbar", "\u00a6", "sect", "\u00a7",
	    "uml", "\u00a8", "copy", "\u00a9", "ordf", "\u00aa", "laquo",
	    "\u00ab", "not", "\u00ac", "shy", "\u00ad", "reg", "\u00ae",
	    "macr", "\u00af", "deg", "\u00b0", "plusmn", "\u00b1", "sup2",
	    "\u00b2", "sup3", "\u00b3", "acute", "\u00b4", "micro", "\u00b5",
	    "para", "\u00b6", "middot", "\u00b7", "cedil", "\u00b8", "sup1",
	    "\u00b9", "ordm", "\u00ba", "raquo", "\u00bb", "frac14", "\u00bc",
	    "frac12", "\u00bd", "frac34", "\u00be", "iquest", "\u00bf",
	    "Agrave", "\u00c0", "Aacute", "\u00c1", "Acirc", "\u00c2",
	    "Atilde", "\u00c3", "Auml", "\u00c4", "Aring", "\u00c5", "AElig",
	    "\u00c6", "Ccedil", "\u00c7", "Egrave", "\u00c8", "Eacute",
	    "\u00c9", "Ecirc", "\u00ca", "Euml", "\u00cb", "Igrave", "\u00cc",
	    "Iacute", "\u00cd", "Icirc", "\u00ce", "Iuml", "\u00cf", "ETH",
	    "\u00d0", "Ntilde", "\u00d1", "Ograve", "\u00d2", "Oacute",
	    "\u00d3", "Ocirc", "\u00d4", "Otilde", "\u00d5", "Ouml", "\u00d6",
	    "times", "\u00d7", "Oslash", "\u00d8", "Ugrave", "\u00d9",
	    "Uacute", "\u00da", "Ucirc", "\u00db", "Uuml", "\u00dc", "Yacute",
	    "\u00dd", "THORN", "\u00de", "szlig", "\u00df", "agrave", "\u00e0",
	    "aacute", "\u00e1", "acirc", "\u00e2", "atilde", "\u00e3", "auml",
	    "\u00e4", "aring", "\u00e5", "aelig", "\u00e6", "ccedil", "\u00e7",
	    "egrave", "\u00e8", "eacute", "\u00e9", "ecirc", "\u00ea", "euml",
	    "\u00eb", "igrave", "\u00ec", "iacute", "\u00ed", "icirc",
	    "\u00ee", "iuml", "\u00ef", "eth", "\u00f0", "ntilde", "\u00f1",
	    "ograve", "\u00f2", "oacute", "\u00f3", "ocirc", "\u00f4",
	    "otilde", "\u00f5", "ouml", "\u00f6", "divide", "\u00f7", "oslash",
	    "\u00f8", "ugrave", "\u00f9", "uacute", "\u00fa", "ucirc",
	    "\u00fb", "uuml", "\u00fc", "yacute", "\u00fd", "thorn", "\u00fe",
	    "yuml", "\u00ff", "ensp", " ", "emsp", " ", "endash", "-",
	    "emdash", "-" };
    private HTMLTokenGenerator tokenGenerator;
    private HTMLParsingRules rules;
    private Class defaultContainerClass = null;
    private Class defaultMarkerClass = null;
    private boolean throwsException = false;
    private FoundationApplet applet;
    
    public HTMLParser(InputStream inputstream) {
	this(inputstream, new HTMLParsingRules());
    }
    
    public HTMLParser(InputStream inputstream,
		      HTMLParsingRules htmlparsingrules) {
	super(inputstream);
	rules = htmlparsingrules;
	tokenGenerator = new HTMLTokenGenerator(inputstream);
    }
    
    public void setThrowsExceptionOnHTMLError(boolean bool) {
	throwsException = bool;
    }
    
    public boolean throwsExceptionOnHTMLError() {
	return throwsException;
    }
    
    public HTMLElement nextHTMLElement()
	throws IOException, HTMLParsingException, InstantiationException,
	       IllegalAccessException {
	while (tokenGenerator.hasMoreTokens()) {
	    HTMLElement htmlelement = parseNextHTMLElement(true, true, null);
	    if (htmlelement != null)
		return htmlelement;
	}
	return null;
    }
    
    public static Hashtable hashtableForAttributeString(String string)
	throws HTMLParsingException {
	Hashtable hashtable = new Hashtable();
	FastStringBuffer faststringbuffer = new FastStringBuffer();
	if (string == null)
	    return hashtable;
	int i = string.length();
	int i_0_ = 0;
	while (i_0_ < i) {
	    for (/**/; i_0_ < i && isSpace(string.charAt(i_0_)); i_0_++) {
		/* empty */
	    }
	    if (i_0_ == i)
		break;
	    faststringbuffer.truncateToLength(0);
	    int i_1_ = parseKeyOrValue(string, i_0_, faststringbuffer);
	    if (i_1_ == 0)
		throw new HTMLParsingException
			  ("Error while parsing attributes " + string, 0);
	    String string_2_ = filterKeyOrValue(faststringbuffer);
	    string_2_ = string_2_.toUpperCase();
	    i_0_ += i_1_;
	    if (!string_2_.equals("")) {
		for (/**/; i_0_ < i && isSpace(string.charAt(i_0_)); i_0_++) {
		    /* empty */
		}
		if (i_0_ < i && string.charAt(i_0_) == '=') {
		    i_0_++;
		    faststringbuffer.truncateToLength(0);
		    i_1_ = parseKeyOrValue(string, i_0_, faststringbuffer);
		    String string_3_ = filterKeyOrValue(faststringbuffer);
		    i_0_ += i_1_;
		    hashtable.put(string_2_, string_3_);
		} else
		    hashtable.put(string_2_, "");
	    }
	}
	return hashtable;
    }
    
    public void reportSyntaxError(String string) throws HTMLParsingException {
	if (throwsException)
	    throw new HTMLParsingException(string,
					   tokenGenerator.lineForLastToken());
    }
    
    public void setClassForMarker(Class var_class, String string) {
	rules.setClassNameForMarker(var_class.getName(), string);
    }
    
    private final char unicodeCharForBytes(String string) {
	String string_4_ = string;
	if (string_4_.length() > 0 && string_4_.charAt(0) == '#')
	    return (char) Integer.parseInt(string_4_.substring(1,
							       string_4_
								   .length()));
	int i = 0;
	for (int i_5_ = specialChars.length; i < i_5_; i += 2) {
	    if (specialChars[i].equals(string_4_))
		return specialChars[i + 1].charAt(0);
	}
	return '\0';
    }
    
    private final int convertSpecialCharacter
	(String string, int i, FastStringBuffer faststringbuffer) {
	int i_6_ = string.length();
	if (i + 1 < i_6_) {
	    int i_7_ = i + 1;
	    int i_8_ = i_7_;
	    char c = string.charAt(i_8_);
	    while (i_8_ < i_6_ && c != ';' && c != ' ' && c != '\n'
		   && c != '\t') {
		if (++i_8_ < i_6_)
		    c = string.charAt(i_8_);
		else
		    c = '\0';
	    }
	    if (i_8_ > i_7_) {
		String string_9_
		    = string.substring(i_7_, i_7_ + (i_8_ - i_7_));
		char c_10_ = unicodeCharForBytes(string_9_);
		if (c_10_ != 0 && c_10_ != '\010')
		    faststringbuffer.append(c_10_);
		if (i_8_ < i_6_ && string.charAt(i_8_) == ';')
		    return string_9_.length() + 2;
		return string_9_.length() + 1;
	    }
	}
	return 0;
    }
    
    private final String filterHTMLString(String string, boolean bool,
					  boolean bool_11_) {
	FastStringBuffer faststringbuffer = new FastStringBuffer();
	boolean bool_12_ = false;
	boolean bool_13_ = false;
	int i = 0;
	for (int i_14_ = string.length(); i < i_14_; i++) {
	    char c = string.charAt(i);
	    if (bool && (c == ' ' || c == '\t' || c == '\n')) {
		if ((bool_13_
		     || ((!bool_11_ || c != '\t' && c != '\n')
			 && (bool_11_ || c != '\t' && c != '\n' && c != ' ')))
		    && !bool_12_) {
		    bool_12_ = true;
		    faststringbuffer.append(' ');
		}
	    } else if (c == '&') {
		int i_15_
		    = convertSpecialCharacter(string, i, faststringbuffer);
		if (i_15_ > 0)
		    i += i_15_ - 1;
		bool_12_ = false;
		bool_13_ = true;
	    } else if (c == '\n' || c == '\t' || c >= ' ' && c <= '~') {
		bool_12_ = false;
		bool_13_ = true;
		faststringbuffer.append(c);
	    }
	}
	if (faststringbuffer.length() > 0)
	    return faststringbuffer.toString();
	return null;
    }
    
    private Class classForMarker(String string) {
	String string_16_ = rules.classNameForMarker(string);
	if (string_16_ != null) {
	    Class var_class;
	    try {
		Application application = Application.application();
		if (application != null)
		    var_class = application.classForName(string_16_);
		else
		    var_class = Class.forName(string_16_);
	    } catch (ClassNotFoundException classnotfoundexception) {
		System.err.println(String.valueOf(classnotfoundexception));
		var_class = null;
	    }
	    return var_class;
	}
	return null;
    }
    
    private final HTMLElement parseNextHTMLElement
	(boolean bool, boolean bool_17_, String string)
	throws IOException, HTMLParsingException, InstantiationException,
	       IllegalAccessException {
	Object object = null;
	int i = tokenGenerator.nextToken();
	switch (i) {
	case 1: {
	    Class var_class;
	    if ((var_class = classForMarker("IFCSTRING")) != null) {
		String string_18_ = tokenGenerator.stringForLastToken();
		string_18_ = filterHTMLString(string_18_, bool, bool_17_);
		if (string_18_ != null) {
		    HTMLElement htmlelement
			= (HTMLElement) var_class.newInstance();
		    htmlelement.setMarker("IFCSTRING");
		    htmlelement.setString(string_18_);
		    return htmlelement;
		}
	    }
	    break;
	}
	case 2: {
	    String string_19_ = tokenGenerator.stringForLastToken();
	    Hashtable hashtable = rules.rulesForMarker(string_19_);
	    Class var_class;
	    if ((var_class = classForMarker(string_19_)) != null) {
		if (rules.isContainer(hashtable)) {
		    Vector vector = null;
		    Vector vector_20_ = null;
		    boolean bool_21_ = false;
		    boolean bool_22_ = false;
		    HTMLElement htmlelement
			= (HTMLElement) var_class.newInstance();
		    htmlelement.setMarker(string_19_);
		    htmlelement.setAttributes(tokenGenerator
						  .attributesForLastToken());
		    Object[] objects = new Object[2];
		    int i_23_ = 0;
		    if (hashtable != null) {
			vector = (Vector) hashtable.get("BeginTermination");
			vector_20_ = (Vector) hashtable.get("EndTermination");
		    }
		    while (tokenGenerator.hasMoreTokens()) {
			i = tokenGenerator.peekNextToken();
			if (i == 3) {
			    String string_24_
				= tokenGenerator.stringForLastToken();
			    if (string_19_.equals(string_24_)) {
				tokenGenerator.nextToken();
				bool_21_ = true;
				break;
			    }
			    if (vector_20_ != null
				&& vector_20_.indexOf(string_24_) != -1) {
				bool_21_ = true;
				break;
			    }
			    if (classForMarker(string_24_) != null) {
				reportSyntaxError
				    ("Unexcpected closing " + string_24_
				     + " while parsing contents for "
				     + string_19_);
				bool_21_ = true;
				break;
			    }
			} else if (i == 2 && vector != null
				   && ((vector.indexOf
					(tokenGenerator.stringForLastToken()))
				       != -1)) {
			    bool_21_ = true;
			    break;
			}
			HTMLElement htmlelement_25_;
			if (rules.shouldFilterStringsForChildren(hashtable)
			    && bool == true)
			    htmlelement_25_
				= parseNextHTMLElement(true, bool_22_,
						       string_19_);
			else
			    htmlelement_25_
				= parseNextHTMLElement(false, bool_22_,
						       string_19_);
			bool_22_ = true;
			if (htmlelement_25_ == null) {
			    if (tokenGenerator.hasMoreTokens() == false) {
				reportSyntaxError("Unterminated marker "
						  + string_19_);
				break;
			    }
			} else {
			    objects[i_23_++] = htmlelement_25_;
			    if (i_23_ == objects.length) {
				Object[] objects_26_
				    = new Object[objects.length * 2];
				System.arraycopy(objects, 0, objects_26_, 0,
						 i_23_);
				objects = objects_26_;
			    }
			}
		    }
		    if (i_23_ > 0) {
			Object[] objects_27_ = new Object[i_23_];
			System.arraycopy(objects, 0, objects_27_, 0, i_23_);
			htmlelement.setChildren(objects_27_);
		    } else
			htmlelement.setChildren(null);
		    if (!bool_21_)
			reportSyntaxError("No end found for marker "
					  + string_19_);
		    return htmlelement;
		}
		HTMLElement htmlelement
		    = (HTMLElement) var_class.newInstance();
		htmlelement.setMarker(string_19_);
		htmlelement
		    .setAttributes(tokenGenerator.attributesForLastToken());
		return htmlelement;
	    }
	    break;
	}
	case 4: {
	    Class var_class;
	    if ((var_class = classForMarker("IFCCOMMENT")) != null) {
		String string_28_ = tokenGenerator.stringForLastToken();
		HTMLElement htmlelement
		    = (HTMLElement) var_class.newInstance();
		htmlelement.setMarker("IFCCOMMENT");
		htmlelement.setString(string_28_);
		return htmlelement;
	    }
	    break;
	}
	case 3: {
	    String string_29_ = tokenGenerator.stringForLastToken();
	    Class var_class = classForMarker(string_29_);
	    if (var_class != null
		&& !rules.shouldIgnoreEnd(rules.rulesForMarker(string_29_)))
		reportSyntaxError("Unexpected closing " + string_29_
				  + " while parsing contents for marker "
				  + string);
	    break;
	}
	default:
	    reportSyntaxError("Unexpected statement");
	}
	return null;
    }
    
    private static boolean isSpace(char c) {
	if (c == ' ' || c == '\t' || c == '\n')
	    return true;
	return false;
    }
    
    private static int parseKeyOrValue(String string, int i,
				       FastStringBuffer faststringbuffer) {
	int i_30_ = i;
	int i_31_ = string.length();
	char c = '\0';
	for (/**/; i_30_ < i_31_ && isSpace(string.charAt(i_30_)); i_30_++) {
	    /* empty */
	}
	if (i_30_ == i_31_)
	    return 0;
	int i_32_ = i_30_;
	if (string.charAt(i_32_) == '\'' || string.charAt(i_32_) == '\"')
	    c = string.charAt(i_32_);
	do
	    faststringbuffer.append(string.charAt(i_32_));
	while (++i_32_ < i_31_ && ((c == 0 && !isSpace(string.charAt(i_32_))
				    && string.charAt(i_32_) != '=')
				   || c != 0 && string.charAt(i_32_) != c));
	if (i_32_ < i_31_ && string.charAt(i_32_) == c) {
	    faststringbuffer.append(string.charAt(i_32_));
	    i_32_++;
	}
	return i_32_ - i_30_;
    }
    
    private static String filterKeyOrValue(FastStringBuffer faststringbuffer) {
	int i = faststringbuffer.length();
	if (i == 0)
	    return "";
	if (faststringbuffer.charAt(0) == '\''
	    || faststringbuffer.charAt(0) == '\"') {
	    if (i <= 2)
		return "";
	    return faststringbuffer.toString().substring(1, i - 1);
	}
	return faststringbuffer.toString();
    }
}
