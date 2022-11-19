/* HTMLCharTable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.util.Hashtable;

public class HTMLCharTable
{
    Hashtable HTMLCharTable = new Hashtable(64);
    
    public HTMLCharTable() {
	HTMLCharTable.put("quot", new Character('\"'));
	HTMLCharTable.put("amp", new Character('&'));
	HTMLCharTable.put("lt", new Character('<'));
	HTMLCharTable.put("gt", new Character('>'));
	HTMLCharTable.put("nbsp", new Character('\u00a0'));
	HTMLCharTable.put("iexcl", new Character('\u00a1'));
	HTMLCharTable.put("cent", new Character('\u00a2'));
	HTMLCharTable.put("pound", new Character('\u00a3'));
	HTMLCharTable.put("curren", new Character('\u00a4'));
	HTMLCharTable.put("yen", new Character('\u00a5'));
	HTMLCharTable.put("brvbar", new Character('\u00a6'));
	HTMLCharTable.put("sect", new Character('\u00a7'));
	HTMLCharTable.put("uml", new Character('\u00a8'));
	HTMLCharTable.put("copy", new Character('\u00a9'));
	HTMLCharTable.put("ordf", new Character('\u00aa'));
	HTMLCharTable.put("laquo", new Character('\u00ab'));
	HTMLCharTable.put("not", new Character('\u00ac'));
	HTMLCharTable.put("shy", new Character('\u00ad'));
	HTMLCharTable.put("reg", new Character('\u00ae'));
	HTMLCharTable.put("macr", new Character('\u00af'));
	HTMLCharTable.put("deg", new Character('\u00b0'));
	HTMLCharTable.put("plusmn", new Character('\u00b1'));
	HTMLCharTable.put("sup2", new Character('\u00b2'));
	HTMLCharTable.put("sup3", new Character('\u00b3'));
	HTMLCharTable.put("acute", new Character('\u00b4'));
	HTMLCharTable.put("micro", new Character('\u00b5'));
	HTMLCharTable.put("para", new Character('\u00b6'));
	HTMLCharTable.put("middot", new Character('\u00b7'));
	HTMLCharTable.put("cedil", new Character('\u00b8'));
	HTMLCharTable.put("sup1", new Character('\u00b9'));
	HTMLCharTable.put("ordm", new Character('\u00ba'));
	HTMLCharTable.put("raquo", new Character('\u00bb'));
	HTMLCharTable.put("frac14", new Character('\u00bc'));
	HTMLCharTable.put("frac12", new Character('\u00bd'));
	HTMLCharTable.put("frac34", new Character('\u00be'));
	HTMLCharTable.put("iquest", new Character('\u00bf'));
	HTMLCharTable.put("Agrave", new Character('\u00c0'));
	HTMLCharTable.put("Aacute", new Character('\u00c1'));
	HTMLCharTable.put("Acirc", new Character('\u00c2'));
	HTMLCharTable.put("Atilde", new Character('\u00c3'));
	HTMLCharTable.put("Auml", new Character('\u00c4'));
	HTMLCharTable.put("Aring", new Character('\u00c5'));
	HTMLCharTable.put("Aelig", new Character('\u00c6'));
	HTMLCharTable.put("Ccedil", new Character('\u00c7'));
	HTMLCharTable.put("Egrave", new Character('\u00c8'));
	HTMLCharTable.put("Eacute", new Character('\u00c9'));
	HTMLCharTable.put("Ecirc", new Character('\u00ca'));
	HTMLCharTable.put("Euml", new Character('\u00cb'));
	HTMLCharTable.put("Igrave", new Character('\u00cc'));
	HTMLCharTable.put("Iacute", new Character('\u00cd'));
	HTMLCharTable.put("Icirc", new Character('\u00ce'));
	HTMLCharTable.put("Iuml", new Character('\u00cf'));
	HTMLCharTable.put("ETH", new Character('\u00d0'));
	HTMLCharTable.put("Ntilde", new Character('\u00d1'));
	HTMLCharTable.put("Ograve", new Character('\u00d2'));
	HTMLCharTable.put("Oacute", new Character('\u00d3'));
	HTMLCharTable.put("Ocirc", new Character('\u00d4'));
	HTMLCharTable.put("Otilde", new Character('\u00d5'));
	HTMLCharTable.put("Ouml", new Character('\u00d6'));
	HTMLCharTable.put("times", new Character('\u00d7'));
	HTMLCharTable.put("Oslash", new Character('\u00d8'));
	HTMLCharTable.put("Ugrave", new Character('\u00d9'));
	HTMLCharTable.put("Uacute", new Character('\u00da'));
	HTMLCharTable.put("Ucirc", new Character('\u00db'));
	HTMLCharTable.put("Uuml", new Character('\u00dc'));
	HTMLCharTable.put("Yacute", new Character('\u00dd'));
	HTMLCharTable.put("THORN", new Character('\u00de'));
	HTMLCharTable.put("szlig", new Character('\u00df'));
	HTMLCharTable.put("agrave", new Character('\u00e0'));
	HTMLCharTable.put("aacute", new Character('\u00e1'));
	HTMLCharTable.put("acirc", new Character('\u00e2'));
	HTMLCharTable.put("atilde", new Character('\u00e3'));
	HTMLCharTable.put("auml", new Character('\u00e4'));
	HTMLCharTable.put("aring", new Character('\u00e5'));
	HTMLCharTable.put("aelig", new Character('\u00e6'));
	HTMLCharTable.put("ccedil", new Character('\u00e7'));
	HTMLCharTable.put("egrave", new Character('\u00e8'));
	HTMLCharTable.put("eacute", new Character('\u00e9'));
	HTMLCharTable.put("ecirc", new Character('\u00ea'));
	HTMLCharTable.put("euml", new Character('\u00eb'));
	HTMLCharTable.put("igrave", new Character('\u00ec'));
	HTMLCharTable.put("iacute", new Character('\u00ed'));
	HTMLCharTable.put("iicrc", new Character('\u00ee'));
	HTMLCharTable.put("iuml", new Character('\u00ef'));
	HTMLCharTable.put("eth", new Character('\u00f0'));
	HTMLCharTable.put("ntilde", new Character('\u00f1'));
	HTMLCharTable.put("ograve", new Character('\u00f2'));
	HTMLCharTable.put("oacute", new Character('\u00f3'));
	HTMLCharTable.put("ocirc", new Character('\u00f4'));
	HTMLCharTable.put("otilde", new Character('\u00f5'));
	HTMLCharTable.put("ouml", new Character('\u00f6'));
	HTMLCharTable.put("divide", new Character('\u00f7'));
	HTMLCharTable.put("oslash", new Character('\u00f8'));
	HTMLCharTable.put("ugrave", new Character('\u00f9'));
	HTMLCharTable.put("uacute", new Character('\u00fa'));
	HTMLCharTable.put("ucirc", new Character('\u00fb'));
	HTMLCharTable.put("uuml", new Character('\u00fc'));
	HTMLCharTable.put("yacute", new Character('\u00fd'));
	HTMLCharTable.put("thorn", new Character('\u00fe'));
	HTMLCharTable.put("yuml", new Character('\u00ff'));
    }
    
    public char getSymbol(String string) {
	Character character = (Character) HTMLCharTable.get(string);
	if (character == null)
	    return '\0';
	return character.charValue();
    }
}
