/* FontItem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class FontItem extends PopupItem
{
    String fontName;
    int tag;
    
    public void setFontName(String string) {
	fontName = string;
    }
    
    public String fontName() {
	return fontName;
    }
    
    public boolean hasFontName(String string) {
	if (string == null)
	    return false;
	return string.equals(fontName);
    }
    
    public void setTag(int i) {
	tag = i;
    }
    
    public int tag() {
	return tag;
    }
}
