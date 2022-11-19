/* Slider - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class Slider extends View implements Target, FormElement
{
    Target target;
    Border border = BezelBorder.loweredBezel();
    Image backgroundImage;
    Image knobImage;
    Color backgroundColor = Color.gray;
    String command;
    int value;
    int minValue;
    int maxValue;
    int sliderX;
    int knobHeight;
    int grooveHeight;
    int clickOffset;
    int imageDisplayStyle;
    boolean enabled = true;
    int incrementResolution;
    static Vector _fieldDescription;
    private static final int SLIDER_KNOB_WIDTH = 6;
    public static final String INCREASE_VALUE = "increaseValue";
    public static final String DECREASE_VALUE = "decreaseValue";
    
    public Slider() {
	this(0, 0, 0, 0);
    }
    
    public Slider(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public Slider(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	knobHeight = 13;
	grooveHeight = 8;
	minValue = 0;
	maxValue = 255;
	setValue(0);
	_setupKeyboard();
	incrementResolution = 20;
    }
    
    private static int parseInt(String string) {
	try {
	    return Integer.parseInt(string);
	} catch (NumberFormatException numberformatexception) {
	    return 0;
	}
    }
    
    public Size minSize() {
	if (_minSize != null)
	    return new Size(_minSize);
	return new Size(knobWidth() + 2,
			knobHeight > grooveHeight ? knobHeight : grooveHeight);
    }
    
    public void setLimits(int i, int i_3_) {
	if (i < i_3_) {
	    minValue = i;
	    maxValue = i_3_;
	    if (value < i)
		value = i;
	    else if (value > i_3_)
		value = i_3_;
	    int i_4_ = value;
	    if (value > 0)
		value--;
	    else
		value++;
	    setValue(i_4_);
	}
    }
    
    public int minValue() {
	return minValue;
    }
    
    public int maxValue() {
	return maxValue;
    }
    
    public void setKnobImage(Image image) {
	if (image != knobImage) {
	    knobImage = image;
	    if (image != null)
		knobHeight = image.height();
	    setValue(value);
	}
    }
    
    public Image knobImage() {
	return knobImage;
    }
    
    public void setBackgroundColor(Color color) {
	if (backgroundColor != null)
	    backgroundColor = color;
    }
    
    public Color backgroundColor() {
	return backgroundColor;
    }
    
    public void setImage(Image image) {
	backgroundImage = image;
    }
    
    public Image image() {
	return backgroundImage;
    }
    
    public void setImageDisplayStyle(int i) {
	if (i != 0 && i != 2 && i != 1)
	    throw new InconsistencyException("Unknown image display style: "
					     + i);
	imageDisplayStyle = i;
	this.draw();
    }
    
    public int imageDisplayStyle() {
	return imageDisplayStyle;
    }
    
    public void setTarget(Target target) {
	this.target = target;
    }
    
    public Target target() {
	return target;
    }
    
    public void setCommand(String string) {
	command = string;
    }
    
    public String command() {
	return command;
    }
    
    public void sendCommand() {
	if (target != null)
	    target.performCommand(command, this);
    }
    
    public void setEnabled(boolean bool) {
	if (bool != enabled) {
	    enabled = bool;
	    this.draw();
	}
    }
    
    public boolean isEnabled() {
	return enabled;
    }
    
    public int knobWidth() {
	if (knobImage != null)
	    return knobImage.width();
	return 6;
    }
    
    public void setKnobHeight(int i) {
	if (i > 0)
	    knobHeight = i;
    }
    
    public int knobHeight() {
	return knobHeight;
    }
    
    public void setGrooveHeight(int i) {
	if (i > 0)
	    grooveHeight = i;
    }
    
    public int grooveHeight() {
	return grooveHeight;
    }
    
    public void setBorder(Border border) {
	if (border == null)
	    border = EmptyBorder.emptyBorder();
	this.border = border;
	setValue(value);
    }
    
    public Border border() {
	return border;
    }
    
    void redrawView(int i) {
	if (sliderX != i) {
	    Rect rect;
	    if (sliderX < i)
		rect = Rect.newRect(sliderX, 0, i - sliderX + knobWidth(),
				    bounds.height);
	    else
		rect = Rect.newRect(i, 0, sliderX - i + knobWidth(),
				    bounds.height);
	    this.draw(rect);
	    Rect.returnRect(rect);
	}
    }
    
    void recomputeSliderPosition() {
	int i = value - minValue;
	float f = (float) (maxValue - minValue);
	sliderX = (int) ((float) i / f * (float) (bounds.width - knobWidth()));
    }
    
    public void setValue(int i) {
	if (i >= minValue && i <= maxValue) {
	    if (value != i) {
		value = i;
		int i_5_ = sliderX;
		recomputeSliderPosition();
		redrawView(i_5_);
	    }
	}
    }
    
    public int value() {
	return value;
    }
    
    public void drawViewGroove(Graphics graphics) {
	int i = (bounds.height - grooveHeight) / 2;
	Rect rect = Rect.newRect(0, i, bounds.width, grooveHeight);
	border.drawInRect(graphics, rect);
	border.computeInteriorRect(rect, rect);
	if (backgroundImage != null) {
	    graphics.pushState();
	    graphics.setClipRect(rect);
	    backgroundImage.drawWithStyle(graphics, rect, imageDisplayStyle);
	    graphics.popState();
	} else {
	    if (!enabled)
		graphics.setColor(Color.lightGray);
	    else
		graphics.setColor(backgroundColor);
	    graphics.fillRect(rect);
	}
	Rect.returnRect(rect);
    }
    
    public Rect knobRect() {
	int i;
	if (knobImage != null)
	    i = (bounds.height - knobImage.height()) / 2;
	else
	    i = ((bounds.height - grooveHeight) / 2
		 - (knobHeight - grooveHeight) / 2);
	return Rect.newRect(sliderX, i, knobWidth(), knobHeight);
    }
    
    public void drawViewKnob(Graphics graphics) {
	Rect rect = knobRect();
	if (knobImage != null)
	    knobImage.drawAt(graphics, rect.x, rect.y);
	else {
	    BezelBorder.raisedButtonBezel().drawInRect(graphics, rect);
	    graphics.setColor(Color.lightGray);
	    graphics.fillRect(rect.x + 2, rect.y + 2, rect.width - 4,
			      rect.height - 4);
	}
	Rect.returnRect(rect);
    }
    
    public void drawView(Graphics graphics) {
	drawViewGroove(graphics);
	drawViewKnob(graphics);
    }
    
    int _positionFromPoint(int i) {
	int i_6_ = knobWidth();
	i -= i_6_ / 2;
	if (i < 0)
	    i = 0;
	else if (i > bounds.width - i_6_)
	    i = bounds.width - i_6_;
	return i;
    }
    
    void _moveSliderTo(int i) {
	int i_7_ = sliderX;
	sliderX = _positionFromPoint(i);
	value = (int) ((double) (maxValue - minValue)
		       / (double) (float) (bounds.width - knobWidth())
		       * (double) sliderX) + minValue;
	redrawView(i_7_);
    }
    
    public void didSizeBy(int i, int i_8_) {
	recomputeSliderPosition();
	super.didSizeBy(i, i_8_);
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (!enabled)
	    return false;
	Rect rect = knobRect();
	if (rect.contains(mouseevent.x, mouseevent.y))
	    clickOffset = _positionFromPoint(mouseevent.x) - sliderX;
	else {
	    clickOffset = 0;
	    _moveSliderTo(mouseevent.x);
	}
	sendCommand();
	return true;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	_moveSliderTo(mouseevent.x - clickOffset);
	sendCommand();
    }
    
    void _setupKeyboard() {
	this.removeAllCommandsForKeys();
	this.setCommandForKey("increaseValue", 1007, 0);
	this.setCommandForKey("decreaseValue", 1006, 0);
	this.setCommandForKey("increaseValue", 43, 0);
	this.setCommandForKey("decreaseValue", 45, 0);
    }
    
    public boolean canBecomeSelectedView() {
	return true;
    }
    
    public void performCommand(String string, Object object) {
	if ("increaseValue".equals(string)) {
	    int i = value() + (int) Math.rint((double) (maxValue - minValue)
					      / (double) incrementResolution);
	    if (i > maxValue)
		i = maxValue;
	    setValue(i);
	    sendCommand();
	} else if ("decreaseValue".equals(string)) {
	    int i = value() - (int) Math.rint((double) (maxValue - minValue)
					      / (double) incrementResolution);
	    if (i < minValue)
		i = minValue;
	    setValue(i);
	    sendCommand();
	}
    }
    
    public int incrementResolution() {
	return incrementResolution;
    }
    
    public void setIncrementResolution(int i) {
	incrementResolution = i;
    }
    
    public String formElementText() {
	return Integer.toString(value());
    }
}
