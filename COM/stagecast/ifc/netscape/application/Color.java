/* Color - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class Color
{
    java.awt.Color _color = java.awt.Color.white;
    public static final Color red = new Color(255, 0, 0);
    public static final Color green = new Color(0, 255, 0);
    public static final Color blue = new Color(0, 0, 255);
    public static final Color cyan = new Color(0, 255, 255);
    public static final Color magenta = new Color(255, 0, 255);
    public static final Color yellow = new Color(255, 255, 0);
    public static final Color orange = new Color(255, 200, 0);
    public static final Color pink = new Color(255, 175, 175);
    public static final Color white = new Color(255, 255, 255);
    public static final Color lightGray = new Color(192, 192, 192);
    public static final Color gray = new Color(128, 128, 128);
    public static final Color darkGray = new Color(64, 64, 64);
    public static final Color black = new Color(0, 0, 0);
    static final Color gray192 = lightGray;
    static final Color gray128 = gray;
    static final Color gray231 = new Color(231, 231, 231);
    static final Color gray204 = new Color(204, 204, 204);
    static final Color gray153 = new Color(153, 153, 153);
    static final Color gray102 = new Color(102, 102, 102);
    static final Color gray51 = new Color(51, 51, 51);
    static final Color gray160 = new Color(160, 160, 164);
    static final Color gray255 = new Color(255, 251, 240);
    static final String R_KEY = "r";
    static final String G_KEY = "g";
    static final String B_KEY = "b";
    private static Class colorClass;
    public static final String COLOR_TYPE
	= "COM.stagecast.ifc.netscape.application.Color";
    
    private static Class colorClass() {
	if (colorClass == null)
	    colorClass = black.getClass();
	return colorClass;
    }
    
    public static int rgbForHSB(float f, float f_0_, float f_1_) {
	return java.awt.Color.HSBtoRGB(f, f_0_, f_1_);
    }
    
    public static Color colorForHSB(float f, float f_2_, float f_3_) {
	return new Color(rgbForHSB(f, f_2_, f_3_));
    }
    
    public Color() {
	/* empty */
    }
    
    public Color(int i, int i_4_, int i_5_) {
	this();
	_color = new java.awt.Color(i, i_4_, i_5_);
    }
    
    public Color(int i) {
	this();
	_color = new java.awt.Color(i);
    }
    
    public Color(float f, float f_6_, float f_7_) {
	this();
	if ((double) f < 0.0)
	    f = 0.0F;
	if ((double) f > 1.0)
	    f = 1.0F;
	if ((double) f_6_ < 0.0)
	    f_6_ = 0.0F;
	if ((double) f_6_ > 1.0)
	    f_6_ = 1.0F;
	if ((double) f_7_ < 0.0)
	    f_7_ = 0.0F;
	if ((double) f_7_ > 1.0)
	    f_7_ = 1.0F;
	_color = new java.awt.Color(f, f_6_, f_7_);
    }
    
    Color(java.awt.Color color_8_) {
	this();
	_color = color_8_;
    }
    
    public int red() {
	return _color.getRed();
    }
    
    public int green() {
	return _color.getGreen();
    }
    
    public int blue() {
	return _color.getBlue();
    }
    
    public int rgb() {
	return _color.getRGB();
    }
    
    public int hashCode() {
	return _color.hashCode();
    }
    
    public boolean equals(Object object) {
	if (object instanceof Color) {
	    Color color_9_ = (Color) object;
	    return _color.equals(color_9_.awtColor());
	}
	if (object instanceof java.awt.Color)
	    return _color.equals((java.awt.Color) object);
	return false;
    }
    
    public String toString() {
	return (this.getClass().getName() + " (" + red() + ", " + green()
		+ ", " + blue() + ")");
    }
    
    java.awt.Color awtColor() {
	return _color;
    }
    
    public Color lighterColor() {
	if (this == lightGray)
	    return white;
	int i = 256 * red() / 192;
	if (i > 255)
	    i = 255;
	int i_10_ = 256 * green() / 192;
	if (i_10_ > 255)
	    i_10_ = 255;
	int i_11_ = 256 * blue() / 192;
	if (i_11_ > 255)
	    i_11_ = 255;
	return new Color(i, i_10_, i_11_);
    }
    
    public Color darkerColor() {
	if (this == lightGray)
	    return gray;
	int i = 128 * red() / 192;
	int i_12_ = 128 * green() / 192;
	int i_13_ = 128 * blue() / 192;
	return new Color(i, i_12_, i_13_);
    }
}
