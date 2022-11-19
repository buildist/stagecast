/* ExtendedKeyEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application.jdk11compatibility;
import COM.stagecast.ifc.netscape.application.KeyEvent;

public class ExtendedKeyEvent extends KeyEvent
{
    public static final int VK_ENTER = 10;
    public static final int VK_BACK_SPACE = 8;
    public static final int VK_TAB = 9;
    public static final int VK_CANCEL = 3;
    public static final int VK_CLEAR = 12;
    public static final int VK_SHIFT = 16;
    public static final int VK_CONTROL = 17;
    public static final int VK_ALT = 18;
    public static final int VK_PAUSE = 19;
    public static final int VK_CAPS_LOCK = 20;
    public static final int VK_ESCAPE = 27;
    public static final int VK_SPACE = 32;
    public static final int VK_PAGE_UP = 33;
    public static final int VK_PAGE_DOWN = 34;
    public static final int VK_END = 35;
    public static final int VK_HOME = 36;
    public static final int VK_LEFT = 37;
    public static final int VK_UP = 38;
    public static final int VK_RIGHT = 39;
    public static final int VK_DOWN = 40;
    public static final int VK_COMMA = 44;
    public static final int VK_PERIOD = 46;
    public static final int VK_SLASH = 47;
    public static final int VK_0 = 48;
    public static final int VK_1 = 49;
    public static final int VK_2 = 50;
    public static final int VK_3 = 51;
    public static final int VK_4 = 52;
    public static final int VK_5 = 53;
    public static final int VK_6 = 54;
    public static final int VK_7 = 55;
    public static final int VK_8 = 56;
    public static final int VK_9 = 57;
    public static final int VK_SEMICOLON = 59;
    public static final int VK_EQUALS = 61;
    public static final int VK_A = 65;
    public static final int VK_B = 66;
    public static final int VK_C = 67;
    public static final int VK_D = 68;
    public static final int VK_E = 69;
    public static final int VK_F = 70;
    public static final int VK_G = 71;
    public static final int VK_H = 72;
    public static final int VK_I = 73;
    public static final int VK_J = 74;
    public static final int VK_K = 75;
    public static final int VK_L = 76;
    public static final int VK_M = 77;
    public static final int VK_N = 78;
    public static final int VK_O = 79;
    public static final int VK_P = 80;
    public static final int VK_Q = 81;
    public static final int VK_R = 82;
    public static final int VK_S = 83;
    public static final int VK_T = 84;
    public static final int VK_U = 85;
    public static final int VK_V = 86;
    public static final int VK_W = 87;
    public static final int VK_X = 88;
    public static final int VK_Y = 89;
    public static final int VK_Z = 90;
    public static final int VK_OPEN_BRACKET = 91;
    public static final int VK_BACK_SLASH = 92;
    public static final int VK_CLOSE_BRACKET = 93;
    public static final int VK_NUMPAD0 = 96;
    public static final int VK_NUMPAD1 = 97;
    public static final int VK_NUMPAD2 = 98;
    public static final int VK_NUMPAD3 = 99;
    public static final int VK_NUMPAD4 = 100;
    public static final int VK_NUMPAD5 = 101;
    public static final int VK_NUMPAD6 = 102;
    public static final int VK_NUMPAD7 = 103;
    public static final int VK_NUMPAD8 = 104;
    public static final int VK_NUMPAD9 = 105;
    public static final int VK_MULTIPLY = 106;
    public static final int VK_ADD = 107;
    public static final int VK_SEPARATER = 108;
    public static final int VK_SUBTRACT = 109;
    public static final int VK_DECIMAL = 110;
    public static final int VK_DIVIDE = 111;
    public static final int VK_F1 = 112;
    public static final int VK_F2 = 113;
    public static final int VK_F3 = 114;
    public static final int VK_F4 = 115;
    public static final int VK_F5 = 116;
    public static final int VK_F6 = 117;
    public static final int VK_F7 = 118;
    public static final int VK_F8 = 119;
    public static final int VK_F9 = 120;
    public static final int VK_F10 = 121;
    public static final int VK_F11 = 122;
    public static final int VK_F12 = 123;
    public static final int VK_DELETE = 127;
    public static final int VK_NUM_LOCK = 144;
    public static final int VK_SCROLL_LOCK = 145;
    public static final int VK_PRINTSCREEN = 154;
    public static final int VK_INSERT = 155;
    public static final int VK_HELP = 156;
    public static final int VK_META = 157;
    public static final int VK_BACK_QUOTE = 192;
    public static final int VK_QUOTE = 222;
    public static final int VK_FINAL = 24;
    public static final int VK_CONVERT = 28;
    public static final int VK_NONCONVERT = 29;
    public static final int VK_ACCEPT = 30;
    public static final int VK_MODECHANGE = 31;
    public static final int VK_KANA = 21;
    public static final int VK_KANJI = 25;
    public static final int VK_UNDEFINED = 0;
    public static final char CHAR_UNDEFINED = '\0';
    public int keyCode;
    public char keyChar;
    
    public static int keyFromKeyCodeAndKeyChar(int i, char c, int i_0_) {
	switch (i) {
	case 10:
	    return 10;
	case 8:
	    return 8;
	case 9:
	    return 9;
	case 19:
	    return 1024;
	case 20:
	    return 1022;
	case 27:
	    return 27;
	case 32:
	    return 32;
	case 33:
	    return 1002;
	case 34:
	    return 1003;
	case 36:
	    return 1000;
	case 35:
	    return 1001;
	case 38:
	    return 1004;
	case 40:
	    return 1005;
	case 37:
	    return 1006;
	case 39:
	    return 1007;
	case 44:
	    return 44;
	case 46:
	    return 46;
	case 47:
	    return 47;
	case 3:
	case 12:
	case 16:
	case 17:
	case 18:
	    return 0;
	default:
	    if (i >= 112 && i <= 123)
		return 1008 + (i - 112);
	    return c;
	}
    }
    
    public ExtendedKeyEvent(long l, int i, char c, int i_1_, int i_2_) {
	super(l, keyFromKeyCodeAndKeyChar(i, c, i_1_), i_1_, i_2_ == -11);
	keyChar = c;
	keyCode = i;
	this.setType(i_2_);
    }
    
    public boolean isExtendedKeyEvent() {
	return true;
    }
    
    public String toString() {
	String string;
	switch (this.type()) {
	case -11:
	    string = "KeyDown";
	    break;
	case -12:
	    string = "KeyUp";
	    break;
	default:
	    string = "KeyTyped";
	}
	return ("ExtendedKeyEvent " + string + " ch: " + new Character(keyChar)
		+ ": keyChar=(0x" + Integer.toString(keyChar, 16)
		+ ") keyCode=(0x" + Integer.toString(keyCode, 16) + ") key=(0x"
		+ Integer.toString(key, 16) + ") mod=" + modifiers);
    }
    
    public int keyCode() {
	return keyCode;
    }
    
    public char keyChar() {
	return keyChar;
    }
}
