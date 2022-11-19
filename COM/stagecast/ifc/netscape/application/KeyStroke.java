/* KeyStroke - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class KeyStroke
{
    int key;
    int modifiers;
    
    public KeyStroke() {
	/* empty */
    }
    
    public KeyStroke(int i, int i_0_) {
	key = i;
	modifiers = i_0_;
    }
    
    public KeyStroke(KeyEvent keyevent) {
	this(keyevent.key, keyevent.modifiers);
    }
    
    public int key() {
	return key;
    }
    
    public int modifiers() {
	return modifiers;
    }
    
    public boolean equals(Object object) {
	if (!(object instanceof KeyStroke))
	    return false;
	if (key == ((KeyStroke) object).key
	    && modifiers == ((KeyStroke) object).modifiers)
	    return true;
	if ((modifiers & 0x2) > 0) {
	    int i = ((KeyStroke) object).key() + 64;
	    int i_1_ = key;
	    if (i >= 97 && i <= 122)
		i -= 32;
	    if (i_1_ >= 97 && i_1_ <= 122)
		i_1_ -= 32;
	    if (i == i_1_)
		return true;
	    return false;
	}
	return false;
    }
    
    public boolean matchesKeyEvent(KeyEvent keyevent) {
	if (keyevent == null)
	    return false;
	if (keyevent.type == -11 && keyevent.key == key
	    && keyevent.modifiers == modifiers)
	    return true;
	return false;
    }
    
    public int hashCode() {
	return String.valueOf(this).hashCode();
    }
    
    public String toString() {
	return "KeyStroke (" + key + "," + modifiers + ")";
    }
}
