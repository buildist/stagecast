/* BezelBorder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class BezelBorder extends Border
{
    public static final int RAISED = 0;
    public static final int LOWERED = 1;
    public static final int GROOVED = 2;
    public static final int RAISED_BUTTON = 3;
    public static final int LOWERED_BUTTON = 4;
    static final int RAISED_SCROLL_BUTTON = 5;
    static final int LOWERED_SCROLL_BUTTON = 6;
    private static Border raisedBezel;
    private static Border loweredBezel;
    private static Border groovedBezel;
    private static Border raisedButtonBezel;
    private static Border loweredButtonBezel;
    private static Border raisedScrollButtonBezel;
    private static Border loweredScrollButtonBezel;
    private int bezelType;
    private Color baseColor;
    private Color lighterColor;
    private Color darkerColor;
    static final Color gray218 = new Color(218, 218, 218);
    static final Color gray165 = new Color(165, 165, 165);
    static final Color gray143 = new Color(143, 143, 143);
    
    public static Border raisedBezel() {
	if (raisedBezel == null)
	    raisedBezel
		= new BezelBorder(0, Color.lightGray, Color.white, Color.gray);
	return raisedBezel;
    }
    
    public static Border loweredBezel() {
	if (loweredBezel == null)
	    loweredBezel
		= new BezelBorder(1, Color.lightGray, Color.white, Color.gray);
	return loweredBezel;
    }
    
    public static Border groovedBezel() {
	if (groovedBezel == null)
	    groovedBezel
		= new BezelBorder(2, Color.lightGray, Color.white, Color.gray);
	return groovedBezel;
    }
    
    public static Border raisedButtonBezel() {
	if (raisedButtonBezel == null)
	    raisedButtonBezel
		= new BezelBorder(3, Color.lightGray, Color.white, Color.gray);
	return raisedButtonBezel;
    }
    
    public static Border loweredButtonBezel() {
	if (loweredButtonBezel == null)
	    loweredButtonBezel
		= new BezelBorder(4, Color.lightGray, Color.white, Color.gray);
	return loweredButtonBezel;
    }
    
    static Border raisedScrollButtonBezel() {
	if (raisedScrollButtonBezel == null)
	    raisedScrollButtonBezel = new BezelBorder(5);
	return raisedScrollButtonBezel;
    }
    
    static Border loweredScrollButtonBezel() {
	if (loweredScrollButtonBezel == null)
	    loweredScrollButtonBezel = new BezelBorder(6);
	return loweredScrollButtonBezel;
    }
    
    public BezelBorder() {
	/* empty */
    }
    
    public BezelBorder(int i) {
	this(i, Color.lightGray, Color.white, Color.gray);
    }
    
    public BezelBorder(int i, Color color) {
	this();
	bezelType = i;
	baseColor = color;
	lighterColor = color.lighterColor();
	darkerColor = color.darkerColor();
    }
    
    public BezelBorder(int i, Color color, Color color_0_, Color color_1_) {
	this();
	bezelType = i;
	baseColor = color;
	lighterColor = color_0_;
	darkerColor = color_1_;
    }
    
    public int leftMargin() {
	if (bezelType == 4)
	    return 3;
	if (bezelType == 5 || bezelType == 6)
	    return 1;
	return 2;
    }
    
    public int rightMargin() {
	if (bezelType == 3)
	    return 3;
	if (bezelType == 5 || bezelType == 6)
	    return 1;
	return 2;
    }
    
    public int topMargin() {
	if (bezelType == 4)
	    return 3;
	if (bezelType == 5 || bezelType == 6)
	    return 1;
	return 2;
    }
    
    public int bottomMargin() {
	if (bezelType == 3)
	    return 3;
	if (bezelType == 5 || bezelType == 6)
	    return 1;
	return 2;
    }
    
    public int type() {
	return bezelType;
    }
    
    public void drawInRect(Graphics graphics, int i, int i_2_, int i_3_,
			   int i_4_) {
	switch (bezelType) {
	case 0:
	    drawBezel(graphics, i, i_2_, i_3_, i_4_, baseColor, lighterColor,
		      darkerColor, Color.darkGray, true);
	    break;
	case 1:
	    drawBezel(graphics, i, i_2_, i_3_, i_4_, baseColor, lighterColor,
		      darkerColor, Color.darkGray, false);
	    break;
	case 2:
	    drawGroovedBezel(graphics, i, i_2_, i_3_, i_4_, lighterColor,
			     darkerColor);
	    break;
	case 3:
	    drawRaisedButtonBezel(graphics, i, i_2_, i_3_, i_4_);
	    break;
	case 4:
	    drawLoweredButtonBezel(graphics, i, i_2_, i_3_, i_4_);
	    break;
	case 5:
	    drawRaisedScrollButtonBezel(graphics, i, i_2_, i_3_, i_4_);
	    break;
	case 6:
	    drawLoweredScrollButtonBezel(graphics, i, i_2_, i_3_, i_4_);
	    break;
	default:
	    throw new InconsistencyException("Invalid bezelType: "
					     + bezelType);
	}
    }
    
    public static void drawBezel
	(Graphics graphics, int i, int i_5_, int i_6_, int i_7_, Color color,
	 Color color_8_, Color color_9_, Color color_10_, boolean bool) {
	if (i_6_ != 0 && i_7_ != 0) {
	    Color color_11_;
	    Color color_12_;
	    Color color_13_;
	    Color color_14_;
	    if (bool) {
		color_11_ = color;
		color_12_ = color_10_;
		color_13_ = color_8_;
		color_14_ = color_9_;
	    } else {
		color_11_ = color_9_;
		color_12_ = color_8_;
		color_13_ = color_10_;
		color_14_ = color;
	    }
	    graphics.setColor(color_11_);
	    graphics.fillRect(i, i_5_, i_6_ - 1, 1);
	    graphics.fillRect(i, i_5_ + 1, 1, i_7_ - 1);
	    graphics.setColor(color_12_);
	    graphics.fillRect(i, i_5_ + i_7_ - 1, i_6_, 1);
	    graphics.fillRect(i + i_6_ - 1, i_5_, 1, i_7_);
	    i++;
	    i_5_++;
	    i_6_ -= 2;
	    i_7_ -= 2;
	    graphics.setColor(color_13_);
	    graphics.fillRect(i, i_5_, i_6_ - 1, 1);
	    graphics.fillRect(i, i_5_ + 1, 1, i_7_ - 1);
	    graphics.setColor(color_14_);
	    graphics.fillRect(i, i_5_ + i_7_ - 1, i_6_, 1);
	    graphics.fillRect(i + i_6_ - 1, i_5_, 1, i_7_);
	}
    }
    
    public static void drawGroovedBezel(Graphics graphics, int i, int i_15_,
					int i_16_, int i_17_, Color color,
					Color color_18_) {
	if (i_16_ != 0 && i_17_ != 0) {
	    graphics.setColor(color);
	    graphics.drawRect(i + 1, i_15_ + 1, i_16_ - 1, i_17_ - 1);
	    graphics.drawPoint(i + i_16_ - 1, i_15_);
	    graphics.drawPoint(i, i_15_ + i_17_ - 1);
	    graphics.setColor(color_18_);
	    graphics.drawRect(i, i_15_, i_16_ - 1, i_17_ - 1);
	}
    }
    
    public static void drawRaisedButtonBezel(Graphics graphics, int i,
					     int i_19_, int i_20_, int i_21_) {
	int i_22_ = i + i_20_;
	int i_23_ = i_19_ + i_21_;
	graphics.setColor(Color.white);
	graphics.drawPoint(i + 1, i_19_ + 1);
	graphics.setColor(Color.gray231);
	graphics.drawLine(i, i_19_, i, i_23_ - 4);
	graphics.drawLine(i + 1, i_19_, i_22_ - 3, i_19_);
	graphics.setColor(Color.lightGray);
	graphics.drawLine(i, i_23_ - 3, i, i_23_ - 2);
	graphics.drawLine(i + 1, i_19_ + 2, i + 1, i_23_ - 3);
	graphics.drawLine(i + 2, i_19_ + 1, i_22_ - 3, i_19_ + 1);
	graphics.drawLine(i + 2, i_23_ - 3, i_22_ - 4, i_23_ - 3);
	graphics.drawLine(i_22_ - 3, i_19_ + 2, i_22_ - 3, i_23_ - 4);
	graphics.drawPoint(i_22_ - 2, i_19_);
	graphics.setColor(Color.gray153);
	graphics.drawLine(i + 1, i_23_ - 2, i_22_ - 3, i_23_ - 2);
	graphics.drawLine(i_22_ - 2, i_19_ + 1, i_22_ - 2, i_23_ - 3);
	graphics.drawPoint(i_22_ - 3, i_23_ - 3);
	graphics.drawPoint(i_22_ - 1, i_19_);
	graphics.drawPoint(i, i_23_ - 1);
	graphics.setColor(Color.gray102);
	graphics.drawLine(i + 1, i_23_ - 1, i_22_ - 1, i_23_ - 1);
	graphics.drawLine(i_22_ - 1, i_19_ + 1, i_22_ - 1, i_23_ - 2);
	graphics.drawPoint(i_22_ - 2, i_23_ - 2);
    }
    
    public static void drawLoweredButtonBezel
	(Graphics graphics, int i, int i_24_, int i_25_, int i_26_) {
	int i_27_ = i + i_25_;
	int i_28_ = i_24_ + i_26_;
	graphics.setColor(Color.white);
	graphics.drawPoint(i_27_ - 2, i_28_ - 2);
	graphics.setColor(Color.gray231);
	graphics.drawLine(i + 2, i_28_ - 1, i_27_ - 1, i_28_ - 1);
	graphics.drawLine(i_27_ - 1, i_24_ + 3, i_27_ - 1, i_28_ - 2);
	graphics.setColor(Color.lightGray);
	graphics.drawLine(i + 2, i_24_ + 3, i + 2, i_28_ - 3);
	graphics.drawLine(i + 3, i_24_ + 2, i_27_ - 2, i_24_ + 2);
	graphics.drawLine(i_27_ - 2, i_24_ + 3, i_27_ - 2, i_28_ - 3);
	graphics.drawLine(i + 2, i_28_ - 2, i_27_ - 3, i_28_ - 2);
	graphics.drawLine(i_27_ - 1, i_24_ + 1, i_27_ - 1, i_24_ + 2);
	graphics.drawPoint(i + 1, i_28_ - 1);
	graphics.setColor(Color.gray153);
	graphics.drawLine(i + 1, i_24_ + 2, i + 1, i_28_ - 2);
	graphics.drawLine(i + 2, i_24_ + 1, i_27_ - 2, i_24_ + 1);
	graphics.drawPoint(i, i_28_ - 1);
	graphics.drawPoint(i_27_ - 1, i_24_);
	graphics.drawPoint(i + 2, i_24_ + 2);
	graphics.setColor(Color.gray102);
	graphics.drawLine(i, i_24_, i, i_28_ - 2);
	graphics.drawLine(i + 1, i_24_, i_27_ - 2, i_24_);
	graphics.drawPoint(i + 1, i_24_ + 1);
    }
    
    static void drawRaisedScrollButtonBezel(Graphics graphics, int i,
					    int i_29_, int i_30_, int i_31_) {
	int i_32_ = i + i_30_;
	int i_33_ = i_29_ + i_31_;
	graphics.setColor(Color.white);
	graphics.drawLine(i, i_29_ + 1, i, i_33_ - 3);
	graphics.drawLine(i + 1, i_29_, i_32_ - 3, i_29_);
	graphics.setColor(Color.gray231);
	graphics.drawPoint(i, i_29_);
	graphics.drawPoint(i, i_33_ - 2);
	graphics.drawPoint(i_32_ - 2, i_29_);
	graphics.setColor(Color.gray153);
	graphics.drawPoint(i_32_ - 1, i_29_);
	graphics.drawPoint(i_32_ - 1, i_33_ - 1);
	graphics.drawPoint(i, i_33_ - 1);
	graphics.setColor(Color.gray102);
	graphics.drawLine(i_32_ - 1, i_29_ + 1, i_32_ - 1, i_33_ - 2);
	graphics.drawLine(i + 1, i_33_ - 1, i_32_ - 2, i_33_ - 1);
    }
    
    static void drawLoweredScrollButtonBezel(Graphics graphics, int i,
					     int i_34_, int i_35_, int i_36_) {
	int i_37_ = i + i_35_;
	int i_38_ = i_34_ + i_36_;
	graphics.setColor(Color.gray153);
	graphics.drawLine(i, i_38_ - 1, i_37_ - 1, i_38_ - 1);
	graphics.drawLine(i_37_ - 1, i_34_, i_37_ - 1, i_38_ - 2);
	graphics.setColor(Color.lightGray);
	graphics.drawLine(i, i_34_, i_37_ - 2, i_34_);
	graphics.drawLine(i, i_34_ + 1, i, i_38_ - 2);
    }
}
