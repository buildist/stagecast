/* KeyEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class KeyEvent extends Event
{
    public int key;
    public int modifiers;
    public static final int KEY_DOWN = -11;
    public static final int KEY_UP = -12;
    public static final int KEY_TYPED = -13;
    public static final int NO_MODIFIERS_MASK = 0;
    public static final int ALT_MASK = 8;
    public static final int CONTROL_MASK = 2;
    public static final int SHIFT_MASK = 1;
    public static final int META_MASK = 4;
    public static final int RETURN_KEY = 10;
    public static final int BACKSPACE_KEY = 8;
    public static final int DELETE_KEY = 127;
    public static final int ESCAPE_KEY = 27;
    public static final int TAB_KEY = 9;
    public static final int UP_ARROW_KEY = 1004;
    public static final int DOWN_ARROW_KEY = 1005;
    public static final int LEFT_ARROW_KEY = 1006;
    public static final int RIGHT_ARROW_KEY = 1007;
    public static final int HOME_KEY = 1000;
    public static final int END_KEY = 1001;
    public static final int PAGE_UP_KEY = 1002;
    public static final int PAGE_DOWN_KEY = 1003;
    public static final int F1_KEY = 1008;
    public static final int F2_KEY = 1009;
    public static final int F3_KEY = 1010;
    public static final int F4_KEY = 1011;
    public static final int F5_KEY = 1012;
    public static final int F6_KEY = 1013;
    public static final int F7_KEY = 1014;
    public static final int F8_KEY = 1015;
    public static final int F9_KEY = 1016;
    public static final int F10_KEY = 1017;
    public static final int F11_KEY = 1018;
    public static final int F12_KEY = 1019;
    
    public KeyEvent() {
	/* empty */
    }
    
    public KeyEvent(long l, int i, int i_0_, boolean bool) {
	this();
	timeStamp = l;
	if (bool)
	    type = -11;
	else
	    type = -12;
	key = i;
	modifiers = i_0_;
    }
    
    public boolean isExtendedKeyEvent() {
	return false;
    }
    
    public int keyCode() {
	return 0;
    }
    
    public char keyChar() {
	return '\0';
    }
    
    public boolean isShiftKeyDown() {
	return (modifiers & 0x1) != 0;
    }
    
    public boolean isControlKeyDown() {
	return (modifiers & 0x2) != 0;
    }
    
    public boolean isMetaKeyDown() {
	return (modifiers & 0x4) != 0;
    }
    
    public boolean isAltKeyDown() {
	return (modifiers & 0x8) != 0;
    }
    
    public boolean isReturnKey() {
	return key == 10;
    }
    
    public boolean isBackspaceKey() {
	return key == 8;
    }
    
    public boolean isDeleteKey() {
	return key == 127;
    }
    
    public boolean isEscapeKey() {
	return key == 27;
    }
    
    public boolean isTabKey() {
	return key == 9 && !isShiftKeyDown();
    }
    
    public boolean isBackTabKey() {
	return key == 9 && isShiftKeyDown();
    }
    
    public boolean isUpArrowKey() {
	return key == 1004;
    }
    
    public boolean isDownArrowKey() {
	return key == 1005;
    }
    
    public boolean isLeftArrowKey() {
	return key == 1006;
    }
    
    public boolean isRightArrowKey() {
	return key == 1007;
    }
    
    public boolean isArrowKey() {
	return key == 1004 || key == 1005 || key == 1006 || key == 1007;
    }
    
    public boolean isHomeKey() {
	return key == 1000;
    }
    
    public boolean isEndKey() {
	return key == 1001;
    }
    
    public boolean isPageUpKey() {
	return key == 1002;
    }
    
    public boolean isPageDownKey() {
	return key == 1003;
    }
    
    public int isFunctionKey() {
	if (key == 1008)
	    return 1;
	if (key == 1009)
	    return 2;
	if (key == 1010)
	    return 3;
	if (key == 1011)
	    return 4;
	if (key == 1012)
	    return 5;
	if (key == 1013)
	    return 6;
	if (key == 1014)
	    return 7;
	if (key == 1015)
	    return 8;
	if (key == 1016)
	    return 9;
	if (key == 1017)
	    return 10;
	if (key == 1018)
	    return 11;
	if (key == 1019)
	    return 12;
	return 0;
    }
    
    public boolean isPrintableKey() {
	return ((key < 32 || isArrowKey() || isHomeKey() || isEndKey()
		 || isFunctionKey() != 0 || isPageUpKey() || isPageDownKey())
		^ true);
    }
    
    public void setRootView(RootView rootview) {
	processor = rootview;
    }
    
    public RootView rootView() {
	return (RootView) processor;
    }
    
    public String toString() {
	String string;
	if (type == -11)
	    string = "KeyDown";
	else
	    string = "KeyUp";
	if (key < 32)
	    return (string + ":'' (0x" + Integer.toString(key, 16) + ")':"
		    + modifiers);
	return (string + ":'" + (char) key + "' (0x"
		+ Integer.toString(key, 16) + ")':" + modifiers);
    }
}
