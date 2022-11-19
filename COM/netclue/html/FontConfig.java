/* FontConfig - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;

public class FontConfig
{
    public static final int F_ITALIC = 1;
    public static final int F_BOLD = 2;
    public static final int F_UNDLN = 4;
    public static final int F_STKTH = 8;
    public static final int F_SUPER = 16;
    public static final int F_SUB = 32;
    static int maxFont = 8;
    static int[] sizeMap = { 8, 10, 12, 14, 16, 18, 22, 28, 36 };
    static int curBase;
    static int curIdx = 3;
    static String face = "Serif";
    
    public static void setFace(String string) {
	face = string;
    }
    
    public static String getFace() {
	return face;
    }
    
    public static int getBaseSize() {
	return sizeMap[curIdx + curBase];
    }
    
    public static void setBase(int i) {
	curBase = i;
    }
    
    public static void setBaseIndex(int i) {
	curIdx = i;
    }
    
    public static int getBaseIndex() {
	return curIdx + curBase;
    }
    
    public static int getRelSize(int i) {
	int i_0_ = curIdx + i + curBase;
	return getAbsSize(i_0_);
    }
    
    public static int getAbsSize(int i) {
	int i_1_ = i + curBase;
	i_1_ = i_1_ < 0 ? 0 : i_1_ > maxFont ? maxFont : i_1_;
	return sizeMap[i_1_];
    }
}
