/* Image - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

public abstract class Image
{
    public static final int CENTERED = 0;
    public static final int SCALED = 1;
    public static final int TILED = 2;
    public static final String IMAGE_TYPE
	= "COM.stagecast.ifc.netscape.application.Image";
    
    public static Image imageNamed(String string) {
	String string_0_ = "";
	int i = string.indexOf('/');
	if (i == -1)
	    i = string.indexOf('\\');
	if (i == -1)
	    return null;
	String string_1_ = string.substring(0, i);
	Class var_class;
	try {
	    string_0_ = "COM.stagecast.ifc.netscape.application." + string_1_;
	    var_class = Class.forName(string_0_);
	} catch (ClassNotFoundException classnotfoundexception) {
	    var_class = null;
	}
	if (var_class == null) {
	    try {
		string_0_ = string_1_;
		var_class = Class.forName(string_1_);
	    } catch (ClassNotFoundException classnotfoundexception) {
		var_class = null;
	    }
	}
	if (var_class == null)
	    return null;
	Image image;
	try {
	    image = (Image) var_class.newInstance();
	} catch (InstantiationException instantiationexception) {
	    throw new InconsistencyException("Unable to instantiate class \""
					     + string_0_ + "\" -- "
					     + instantiationexception
						   .getMessage());
	} catch (IllegalAccessException illegalaccessexception) {
	    throw new InconsistencyException("Illegal access to class \""
					     + string_0_ + "\" -- "
					     + illegalaccessexception
						   .getMessage());
	}
	return image.imageWithName(string.substring(i + 1));
    }
    
    public abstract int width();
    
    public abstract int height();
    
    public abstract void drawAt(Graphics graphics, int i, int i_2_);
    
    public void drawScaled(Graphics graphics, int i, int i_3_, int i_4_,
			   int i_5_) {
	drawCentered(graphics, i, i_3_, i_4_, i_5_);
    }
    
    public String name() {
	return null;
    }
    
    public void drawCentered(Graphics graphics, int i, int i_6_, int i_7_,
			     int i_8_) {
	drawAt(graphics, i + (i_7_ - width()) / 2,
	       i_6_ + (i_8_ - height()) / 2);
    }
    
    public void drawCentered(Graphics graphics, Rect rect) {
	if (rect != null)
	    drawCentered(graphics, rect.x, rect.y, rect.width, rect.height);
    }
    
    public void drawScaled(Graphics graphics, Rect rect) {
	if (rect != null)
	    drawScaled(graphics, rect.x, rect.y, rect.width, rect.height);
    }
    
    public void drawTiled(Graphics graphics, int i, int i_9_, int i_10_,
			  int i_11_) {
	Rect rect = graphics.clipRect();
	int i_12_ = width();
	int i_13_ = height();
	if (i_12_ > 0 && i_13_ > 0) {
	    graphics.pushState();
	    graphics.setClipRect(new Rect(i, i_9_, i_10_, i_11_));
	    int i_14_;
	    if (i > rect.x)
		i_14_ = i;
	    else
		i_14_ = i + i_12_ * ((rect.x - i) / i_12_);
	    int i_15_;
	    if (i + i_10_ < rect.maxX())
		i_15_ = i + i_10_;
	    else
		i_15_ = rect.maxX();
	    int i_16_;
	    if (i_9_ > rect.y)
		i_16_ = i_9_;
	    else
		i_16_ = i_9_ + i_13_ * ((rect.y - i_9_) / i_13_);
	    int i_17_;
	    if (i_9_ + i_11_ < rect.maxY())
		i_17_ = i_9_ + i_11_;
	    else
		i_17_ = rect.maxY();
	    for (i = i_14_; i < i_15_; i += i_12_) {
		for (i_9_ = i_16_; i_9_ < i_17_; i_9_ += i_13_)
		    drawAt(graphics, i, i_9_);
	    }
	    graphics.popState();
	}
    }
    
    public void drawTiled(Graphics graphics, Rect rect) {
	if (rect != null)
	    drawTiled(graphics, rect.x, rect.y, rect.width, rect.height);
    }
    
    public void drawWithStyle(Graphics graphics, int i, int i_18_, int i_19_,
			      int i_20_, int i_21_) {
	switch (i_21_) {
	case 0:
	    drawCentered(graphics, i, i_18_, i_19_, i_20_);
	    break;
	case 1:
	    drawScaled(graphics, i, i_18_, i_19_, i_20_);
	    break;
	case 2:
	    drawTiled(graphics, i, i_18_, i_19_, i_20_);
	    break;
	default:
	    throw new InconsistencyException("Unknown style: " + i_21_);
	}
    }
    
    public void drawWithStyle(Graphics graphics, Rect rect, int i) {
	drawWithStyle(graphics, rect.x, rect.y, rect.width, rect.height, i);
    }
    
    public Image imageWithName(String string) {
	return null;
    }
    
    public boolean isTransparent() {
	return true;
    }
}
