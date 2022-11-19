/* ScrollBar - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class ScrollBar extends View implements Target
{
    Scrollable scrollableView;
    Button increaseButton;
    Button decreaseButton;
    Image knobImage;
    Image trayTopImage;
    Image trayBottomImage;
    Image trayLeftImage;
    Image trayRightImage;
    Timer timer;
    int scrollValue;
    int origScrollValue;
    int knobLength;
    int lastMouseValue;
    int lastAltMouseValue;
    int lineIncrement;
    int axis;
    boolean active;
    boolean enabled;
    boolean shouldRedraw;
    float pageSizeAsPercent;
    int pixelScrollValue;
    ScrollBarOwner scrollBarOwner;
    public static final int DEFAULT_LINE_INCREMENT = 12;
    public static final float DEFAULT_PAGE_SIZE = 1.0F;
    public static final int DEFAULT_WIDTH = 0;
    public static final int DEFAULT_HEIGHT = 0;
    public static final String UPDATE = "updateScrollValue";
    public static final String SCROLL_PAGE_BACKWARD = "scrollPageBackward";
    public static final String SCROLL_PAGE_FORWARD = "scrollPageForward";
    public static final String SCROLL_LINE_BACKWARD = "scrollLineBackward";
    public static final String SCROLL_LINE_FORWARD = "scrollLineForward";
    private static final String TIMER_SCROLL_PAGE = "timerScroll";
    
    public ScrollBar() {
	this(0, 0, 0, 0);
    }
    
    public ScrollBar(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public ScrollBar(int i, int i_0_, int i_1_, int i_2_) {
	this(i, i_0_, i_1_, i_2_, 1);
    }
    
    public ScrollBar(int i, int i_3_, int i_4_, int i_5_, int i_6_) {
	super(i, i_3_, i_4_, i_5_);
	axis = i_6_;
	lineIncrement = 12;
	pageSizeAsPercent = 1.0F;
	setEnabled(true);
	Button button = new Button(0, 0, 16, 16);
	button.setType(3);
	button.setBordered(true);
	button.setRaisedBorder(BezelBorder.raisedScrollButtonBezel());
	button.setLoweredBorder(BezelBorder.loweredScrollButtonBezel());
	if (axis == 0) {
	    button.setImage(Bitmap.bitmapNamed
			    ("netscape/application/ScrollRightArrow.gif"));
	    button.setAltImage
		(Bitmap.bitmapNamed
		 ("netscape/application/ScrollRightArrowActive.gif"));
	} else {
	    button.setImage(Bitmap.bitmapNamed
			    ("netscape/application/ScrollDownArrow.gif"));
	    button.setAltImage
		(Bitmap.bitmapNamed
		 ("netscape/application/ScrollDownArrowActive.gif"));
	}
	setIncreaseButton(button);
	button = new Button(0, 0, 16, 16);
	button.setType(3);
	button.setBordered(true);
	button.setRaisedBorder(BezelBorder.raisedScrollButtonBezel());
	button.setLoweredBorder(BezelBorder.loweredScrollButtonBezel());
	if (axis == 0) {
	    button.setImage(Bitmap.bitmapNamed
			    ("netscape/application/ScrollLeftArrow.gif"));
	    button.setAltImage
		(Bitmap.bitmapNamed
		 ("netscape/application/ScrollLeftArrowActive.gif"));
	} else {
	    button.setImage
		(Bitmap.bitmapNamed("netscape/application/ScrollUpArrow.gif"));
	    button.setAltImage
		(Bitmap.bitmapNamed
		 ("netscape/application/ScrollUpArrowActive.gif"));
	}
	setDecreaseButton(button);
	if (axis == 0) {
	    this.setHorizResizeInstruction(2);
	    this.setVertResizeInstruction(8);
	} else {
	    this.setHorizResizeInstruction(1);
	    this.setVertResizeInstruction(16);
	}
	if (axis == 0)
	    setKnobImage
		(Bitmap.bitmapNamed("netscape/application/ScrollKnobH.gif"));
	else
	    setKnobImage
		(Bitmap.bitmapNamed("netscape/application/ScrollKnobV.gif"));
	trayTopImage
	    = Bitmap.bitmapNamed("netscape/application/ScrollTrayTop.gif");
	trayBottomImage
	    = Bitmap.bitmapNamed("netscape/application/ScrollTrayBottom.gif");
	trayLeftImage
	    = Bitmap.bitmapNamed("netscape/application/ScrollTrayLeft.gif");
	trayRightImage
	    = Bitmap.bitmapNamed("netscape/application/ScrollTrayRight.gif");
	if (axis == 0 && bounds.height == 0 || axis != 0 && bounds.width == 0)
	    _adjustToFit();
	_computeScrollValue();
	_setupKeyboard();
    }
    
    public boolean isTransparent() {
	return false;
    }
    
    public Rect interiorRect() {
	return Rect.newRect(1, 1, this.width() - 2, this.height() - 2);
    }
    
    public Size minSize() {
	int i;
	int i_7_;
	if (increaseButton != null) {
	    i = increaseButton.bounds.width;
	    i_7_ = increaseButton.bounds.height;
	} else
	    i = i_7_ = 0;
	if (decreaseButton != null) {
	    if (axis == 0) {
		i += decreaseButton.bounds.width;
		if (i_7_ < decreaseButton.bounds.height)
		    i_7_ = decreaseButton.bounds.height;
	    } else {
		if (i < decreaseButton.bounds.width)
		    i = decreaseButton.bounds.width;
		i_7_ += decreaseButton.bounds.height;
	    }
	}
	return new Size(i + 2, i_7_ + 2);
    }
    
    void _adjustToFit() {
	Size size = minSize();
	if (axis == 0)
	    this.sizeTo(bounds.width, size.height);
	else
	    this.sizeTo(size.width, bounds.height);
    }
    
    public void didSizeBy(int i, int i_8_) {
	super.didSizeBy(i, i_8_);
	_computeScrollValue();
    }
    
    public void setIncreaseButton(Button button) {
	if (increaseButton != null)
	    increaseButton.removeFromSuperview();
	increaseButton = button;
	if (increaseButton != null) {
	    increaseButton.setTarget(this);
	    increaseButton.setCommand("scrollLineForward");
	    if (axis == 0) {
		increaseButton.setHorizResizeInstruction(1);
		increaseButton.setVertResizeInstruction(16);
	    } else {
		increaseButton.setHorizResizeInstruction(2);
		increaseButton.setVertResizeInstruction(8);
	    }
	    _adjustToFit();
	}
	if (enabled && active)
	    addParts();
    }
    
    public Button increaseButton() {
	return increaseButton;
    }
    
    public void setDecreaseButton(Button button) {
	if (decreaseButton != null)
	    decreaseButton.removeFromSuperview();
	decreaseButton = button;
	if (decreaseButton != null) {
	    decreaseButton.setTarget(this);
	    decreaseButton.setCommand("scrollLineBackward");
	    if (axis == 0) {
		decreaseButton.setHorizResizeInstruction(0);
		decreaseButton.setVertResizeInstruction(16);
	    } else {
		decreaseButton.setHorizResizeInstruction(2);
		decreaseButton.setVertResizeInstruction(4);
	    }
	    _adjustToFit();
	}
	if (enabled && active)
	    addParts();
    }
    
    public Button decreaseButton() {
	return decreaseButton;
    }
    
    public void addParts() {
	int i = 1;
	int i_9_ = 1;
	if (decreaseButton != null)
	    decreaseButton.moveTo(i, i_9_);
	if (axis == 0) {
	    if (increaseButton != null)
		increaseButton
		    .moveTo(this.width() - 1 - increaseButton.width(), i_9_);
	} else if (increaseButton != null)
	    increaseButton.moveTo(i,
				  this.height() - 1 - increaseButton.height());
	this.addSubview(increaseButton);
	this.addSubview(decreaseButton);
    }
    
    public void removeParts() {
	if (increaseButton != null)
	    increaseButton.removeFromSuperview();
	if (decreaseButton != null)
	    decreaseButton.removeFromSuperview();
    }
    
    public void setScrollableObject(Scrollable scrollable) {
	if (scrollableView != scrollable) {
	    scrollableView = scrollable;
	    _computeScrollValue();
	}
    }
    
    public Scrollable scrollableObject() {
	return scrollableView;
    }
    
    public void setScrollBarOwner(ScrollBarOwner scrollbarowner) {
	scrollBarOwner = scrollbarowner;
    }
    
    public ScrollBarOwner scrollBarOwner() {
	return scrollBarOwner;
    }
    
    public Rect scrollTrayRect() {
	if (axis == 0) {
	    int i = 1;
	    if (decreaseButton != null)
		i += decreaseButton.bounds.width;
	    int i_10_ = this.width() - 1;
	    if (increaseButton != null)
		i_10_ -= increaseButton.width();
	    return Rect.newRect(i, 1, i_10_ - i, this.height() - 2);
	}
	int i = 1;
	if (decreaseButton != null)
	    i += decreaseButton.height();
	int i_11_ = this.height() - 1;
	if (increaseButton != null)
	    i_11_ -= increaseButton.height();
	return Rect.newRect(1, i, this.width() - 2, i_11_ - i);
    }
    
    public int scrollTrayLength() {
	Rect rect = scrollTrayRect();
	int i = axis == 0 ? rect.width : rect.height;
	Rect.returnRect(rect);
	return i;
    }
    
    public void setKnobImage(Image image) {
	knobImage = image;
	_adjustToFit();
    }
    
    public Image knobImage() {
	return knobImage;
    }
    
    public Rect knobRect() {
	Rect rect = scrollTrayRect();
	int i;
	int i_12_;
	int i_13_;
	int i_14_;
	if (axis == 0) {
	    i = rect.x + scrollValue;
	    i_12_ = rect.y;
	    i_13_ = knobLength;
	    i_14_ = rect.height;
	} else {
	    i = rect.x;
	    i_12_ = rect.y + scrollValue;
	    i_13_ = rect.width;
	    i_14_ = knobLength;
	}
	rect.setBounds(i, i_12_, i_13_, i_14_);
	return rect;
    }
    
    public void setKnobLength(int i) {
	int i_15_ = scrollTrayLength();
	if (i < minKnobLength())
	    i = minKnobLength();
	else if (i > i_15_)
	    i = i_15_;
	knobLength = i;
    }
    
    public int knobLength() {
	return knobLength;
    }
    
    public int minKnobLength() {
	if (axis == 0)
	    return this.height() - 2;
	return this.width() - 2;
    }
    
    public void setEnabled(boolean bool) {
	if (bool != enabled) {
	    enabled = bool;
	    if (active && enabled)
		addParts();
	    else if (!enabled)
		removeParts();
	    else
		return;
	    this.setDirty(true);
	    if (scrollBarOwner != null) {
		if (enabled)
		    scrollBarOwner.scrollBarWasEnabled(this);
		else
		    scrollBarOwner.scrollBarWasDisabled(this);
	    }
	}
    }
    
    public boolean isEnabled() {
	return enabled;
    }
    
    public void setActive(boolean bool) {
	if (bool != active) {
	    active = bool;
	    if (active && enabled)
		addParts();
	    else if (!active)
		removeParts();
	    this.setDirty(true);
	    if (scrollBarOwner != null) {
		if (active)
		    scrollBarOwner.scrollBarDidBecomeActive(this);
		else
		    scrollBarOwner.scrollBarDidBecomeInactive(this);
	    }
	}
    }
    
    public boolean isActive() {
	return active;
    }
    
    public void drawViewKnobInRect(Graphics graphics, Rect rect) {
	BezelBorder.raisedScrollButtonBezel().drawInRect(graphics, rect);
	graphics.setColor(Color.lightGray);
	graphics.fillRect(rect.x + 1, rect.y + 1, rect.width - 2,
			  rect.height - 2);
	if (knobImage != null)
	    knobImage.drawCentered(graphics, rect);
    }
    
    public void drawView(Graphics graphics) {
	graphics.setColor(Color.gray153);
	graphics.drawLine(0, 0, 0, this.height() - 1);
	graphics.drawLine(1, 0, this.width() - 1, 0);
	graphics.setColor(Color.gray231);
	graphics.drawLine(this.width() - 1, 0, this.width() - 1,
			  this.height());
	graphics.drawLine(0, this.height() - 1, this.width() - 1,
			  this.height() - 1);
	Rect rect = scrollTrayRect();
	int i = interiorRect().x;
	int i_16_ = interiorRect().y;
	int i_17_ = interiorRect().maxX();
	int i_18_ = interiorRect().maxY();
	int i_19_ = rect.x;
	int i_20_ = rect.y;
	int i_21_ = rect.x + rect.width;
	int i_22_ = rect.y + rect.height;
	if (!isEnabled() || !isActive()) {
	    if (axis == 0) {
		graphics.setColor(Color.lightGray);
		graphics.fillRect(i + 1, i_16_ + 1, i_19_ - 3, i_18_ - 3);
		graphics.fillRect(i_21_ + 1, i_16_ + 1, i_17_ - i_21_ - 2,
				  i_18_ - 3);
		graphics.fillRect(rect.x + 1, rect.y + 1, rect.width - 2,
				  rect.height - 2);
		graphics.setColor(Color.gray153);
		graphics.drawLine(i, i_18_ - 1, i_17_ - 1, i_18_ - 1);
		graphics.drawLine(i_17_ - 1, i_16_, i_17_ - 1, i_18_ - 2);
		graphics.drawLine(i_19_ - 1, i_16_, i_19_ - 1, i_18_ - 2);
		graphics.drawLine(i_21_ - 1, i_16_, i_21_ - 1, i_18_ - 2);
		graphics.drawLine(i + 5, i_16_ + 7, i + 5, i_16_ + 8);
		graphics.drawLine(i + 6, i_16_ + 6, i + 6, i_16_ + 9);
		graphics.drawLine(i + 7, i_16_ + 5, i + 7, i_16_ + 10);
		graphics.drawLine(i + 8, i_16_ + 4, i + 8, i_16_ + 11);
		graphics.drawLine(i + 9, i_16_ + 3, i + 9, i_16_ + 12);
		graphics.drawLine(i_21_ + 6, i_16_ + 3, i_21_ + 6, i_16_ + 12);
		graphics.drawLine(i_21_ + 7, i_16_ + 4, i_21_ + 7, i_16_ + 11);
		graphics.drawLine(i_21_ + 8, i_16_ + 5, i_21_ + 8, i_16_ + 10);
		graphics.drawLine(i_21_ + 9, i_16_ + 6, i_21_ + 9, i_16_ + 9);
		graphics.drawLine(i_21_ + 10, i_16_ + 7, i_21_ + 10,
				  i_16_ + 8);
		graphics.setColor(Color.gray231);
		graphics.drawLine(i, i_16_, i, i_18_ - 2);
		graphics.drawLine(i + 1, i_16_, i_19_ - 2, i_16_);
		graphics.drawLine(i_19_, i_16_, i_19_, i_18_ - 2);
		graphics.drawLine(i_19_ + 1, i_16_, i_21_ - 2, i_16_);
		graphics.drawLine(i_21_, i_16_, i_21_, i_18_ - 2);
		graphics.drawLine(i_21_ + 1, i_16_, i_17_ - 2, i_16_);
	    } else {
		graphics.setColor(Color.lightGray);
		graphics.fillRect(i + 1, i_16_ + 1, i_17_ - 3, i_20_ - 3);
		graphics.fillRect(i + 1, i_22_ + 1, i_17_ - 3,
				  i_18_ - i_22_ - 2);
		graphics.fillRect(rect.x + 1, rect.y + 1, rect.width - 2,
				  rect.height - 2);
		graphics.setColor(Color.gray153);
		graphics.drawLine(i_17_ - 1, i_16_, i_17_ - 1, i_18_ - 1);
		graphics.drawLine(i, i_18_ - 1, i_17_ - 2, i_18_ - 1);
		graphics.drawLine(i, i_20_ - 1, i_17_ - 2, i_20_ - 1);
		graphics.drawLine(i, i_22_ - 1, i_17_ - 2, i_22_ - 1);
		graphics.drawLine(i + 7, i_16_ + 5, i + 8, i_16_ + 5);
		graphics.drawLine(i + 6, i_16_ + 6, i + 9, i_16_ + 6);
		graphics.drawLine(i + 5, i_16_ + 7, i + 10, i_16_ + 7);
		graphics.drawLine(i + 4, i_16_ + 8, i + 11, i_16_ + 8);
		graphics.drawLine(i + 3, i_16_ + 9, i + 12, i_16_ + 9);
		graphics.drawLine(i + 3, i_22_ + 6, i + 12, i_22_ + 6);
		graphics.drawLine(i + 4, i_22_ + 7, i + 11, i_22_ + 7);
		graphics.drawLine(i + 5, i_22_ + 8, i + 10, i_22_ + 8);
		graphics.drawLine(i + 6, i_22_ + 9, i + 9, i_22_ + 9);
		graphics.drawLine(i + 7, i_22_ + 10, i + 8, i_22_ + 10);
		graphics.setColor(Color.gray231);
		graphics.drawLine(i, i_16_, i_17_ - 2, i_16_);
		graphics.drawLine(i, i_16_ + 1, i, i_20_ - 2);
		graphics.drawLine(i, i_20_, i_17_ - 2, i_20_);
		graphics.drawLine(i, i_20_ + 1, i, i_22_ - 2);
		graphics.drawLine(i, i_22_, i_17_ - 2, i_22_);
		graphics.drawLine(i, i_22_ + 1, i, i_18_ - 2);
	    }
	    Rect.returnRect(rect);
	} else if (knobLength <= scrollTrayLength()) {
	    i = rect.x;
	    i_17_ = rect.maxX();
	    i_16_ = rect.y;
	    i_18_ = rect.maxY();
	    if (axis == 0) {
		i_19_ = i + scrollValue - 1;
		i_21_ = i + scrollValue + knobLength;
		if (scrollValue > 0) {
		    graphics.setColor(Color.gray153);
		    graphics.drawLine(i, i_16_, i_19_, i_16_);
		    graphics.drawLine(i, i_16_ + 1, i, i_18_ - 2);
		    trayTopImage.drawTiled(graphics, i + 1, i_16_ + 1,
					   i_19_ - i, 1);
		    trayBottomImage.drawTiled(graphics, i + 1, i_18_ - 1,
					      i_19_ - i, 1);
		    trayLeftImage.drawTiled(graphics, i + 1, i_16_ + 1, 1,
					    i_18_ - 3);
		    graphics.setColor(Color.lightGray);
		    graphics.fillRect(i + 2, i_16_ + 2, i_19_ - i - 1,
				      i_18_ - i_16_ - 3);
		}
		if (i_21_ < i_17_) {
		    graphics.setColor(Color.gray153);
		    graphics.drawLine(i_21_, i_16_, i_17_ - 2, i_16_);
		    graphics.drawLine(i_17_ - 1, i_16_, i_17_ - 1, i_18_ - 1);
		    trayTopImage.drawTiled(graphics, i_21_, i_16_ + 1,
					   i_17_ - i_21_ - 1, 1);
		    trayBottomImage.drawTiled(graphics, i_21_, i_18_ - 1,
					      i_17_ - i_21_ - 1, 1);
		    trayLeftImage.drawTiled(graphics, i_21_, i_16_ + 1, 1,
					    i_18_ - 3);
		    trayRightImage.drawTiled(graphics, i_17_ - 2, i_16_ + 1, 1,
					     i_18_ - 3);
		    graphics.setColor(Color.lightGray);
		    graphics.fillRect(i_21_ + 1, i_16_ + 2, i_17_ - i_21_ - 3,
				      i_18_ - i_16_ - 3);
		}
	    } else {
		i_20_ = i_16_ + scrollValue - 1;
		i_22_ = i_16_ + scrollValue + knobLength;
		if (scrollValue > 0) {
		    graphics.setColor(Color.gray153);
		    graphics.drawLine(i, i_16_ + 1, i, i_20_);
		    graphics.drawLine(i, i_16_, i_17_ - 1, i_16_);
		    trayTopImage.drawTiled(graphics, i + 1, i_16_ + 1,
					   i_17_ - 2, 1);
		    trayLeftImage.drawTiled(graphics, i + 1, i_16_ + 1, 1,
					    i_20_ - i_16_);
		    trayRightImage.drawTiled(graphics, i_17_ - 1, i_16_ + 1, 1,
					     i_20_ - i_16_);
		    graphics.setColor(Color.lightGray);
		    graphics.fillRect(i + 2, i_16_ + 2, i_17_ - i - 3,
				      i_20_ - i_16_ - 1);
		}
		if (i_22_ < i_18_) {
		    graphics.setColor(Color.gray153);
		    graphics.drawLine(i, i_22_, i, i_18_ - 2);
		    graphics.drawLine(i, i_18_ - 1, i_17_ - 1, i_18_ - 1);
		    trayTopImage.drawTiled(graphics, i + 1, i_22_,
					   i_17_ - i - 2, 1);
		    trayBottomImage.drawTiled(graphics, i + 1, i_18_ - 2,
					      i_17_ - i - 1, 1);
		    trayLeftImage.drawTiled(graphics, i + 1, i_22_, 1,
					    i_18_ - i_22_ - 2);
		    trayRightImage.drawTiled(graphics, i_17_ - 1, i_22_, 1,
					     i_18_ - i_22_ - 2);
		    graphics.setColor(Color.lightGray);
		    graphics.fillRect(i + 2, i_22_ + 1, i_17_ - i - 3,
				      i_18_ - i_22_ - 3);
		}
	    }
	    Rect.returnRect(rect);
	    Rect rect_23_ = knobRect();
	    drawViewKnobInRect(graphics, rect_23_);
	    Rect.returnRect(rect_23_);
	} else if (axis == 0) {
	    graphics.setColor(Color.lightGray);
	    graphics.fillRect(rect.x + 1, rect.y + 1, rect.width - 2,
			      rect.height - 2);
	    graphics.setColor(Color.gray102);
	    graphics.drawLine(i, i_18_ - 1, i_17_ - 1, i_18_ - 1);
	    graphics.drawLine(i_21_ - 1, i_16_, i_21_ - 1, i_18_ - 2);
	    graphics.setColor(Color.gray231);
	    graphics.drawLine(i_19_, i_16_, i_19_, i_18_ - 2);
	    graphics.drawLine(i_19_ + 1, i_16_, i_21_ - 2, i_16_);
	} else {
	    graphics.setColor(Color.lightGray);
	    graphics.fillRect(rect.x + 1, rect.y + 1, rect.width - 2,
			      rect.height - 2);
	    graphics.setColor(Color.gray102);
	    graphics.drawLine(i_17_ - 1, i_16_, i_17_ - 1, i_18_ - 1);
	    graphics.drawLine(i, i_22_ - 1, i_17_ - 2, i_22_ - 1);
	    graphics.setColor(Color.gray231);
	    graphics.drawLine(i, i_20_, i_17_ - 2, i_20_);
	    graphics.drawLine(i, i_20_ + 1, i, i_22_ - 2);
	}
    }
    
    public void drawScrollTray() {
	Rect rect = scrollTrayRect();
	this.addDirtyRect(rect);
	Rect.returnRect(rect);
    }
    
    void _setScrollValue(int i) {
	if (i < 0)
	    scrollValue = 0;
	else if (i > _maxScrollValue())
	    scrollValue = _maxScrollValue();
	else
	    scrollValue = i;
    }
    
    void _setScrollPercent(float f) {
	if (!(f < 0.0F) && !(f > 1.0F)) {
	    int i = (int) (f * (float) _maxScrollValue());
	    if (i != 0 || f == 0.0F)
		_setScrollValue(i);
	    else
		_setScrollValue
		    ((int) Math.ceil((double) (f
					       * (float) _maxScrollValue())));
	}
    }
    
    void _setPercentVisible(float f) {
	int i = scrollTrayLength();
	int i_24_ = (int) (f * (float) i);
	setKnobLength(i_24_);
	if (i < 1 || i <= i_24_)
	    setActive(false);
	else
	    setActive(true);
    }
    
    void _computeScrollValue() {
	int i = scrollValue;
	int i_25_ = knobLength;
	boolean bool = active;
	int i_26_ = 0;
	int i_27_;
	int i_28_;
	int i_29_;
	if (scrollableView != null) {
	    int i_30_ = axis();
	    i_27_ = scrollableView.lengthOfScrollViewForAxis(i_30_);
	    i_28_ = scrollableView.lengthOfContentViewForAxis(i_30_);
	    i_29_ = scrollableView.positionOfContentViewForAxis(i_30_);
	} else {
	    i_27_ = 0;
	    i_28_ = 0;
	    i_29_ = 0;
	}
	if (i_28_ == 0)
	    _setPercentVisible(1.0F);
	else
	    _setPercentVisible((float) i_27_ / (float) i_28_);
	int i_31_ = i_28_ - i_27_;
	if (i_31_ <= 0)
	    _setScrollPercent(0.0F);
	else
	    _setScrollPercent((float) (i_26_ - i_29_) / (float) i_31_);
	if ((scrollValue != i || knobLength != i_25_ || active != bool)
	    && isActive() && isEnabled())
	    drawScrollTray();
    }
    
    public int scrollValue() {
	return scrollValue;
    }
    
    int pixelScrollValue() {
	return pixelScrollValue;
    }
    
    int _maxScrollValue() {
	return scrollTrayLength() - knobLength;
    }
    
    public float scrollPercent() {
	int i = _maxScrollValue();
	if (i == 0)
	    return 0.0F;
	return (float) scrollValue() / (float) i;
    }
    
    int _mouseValue(MouseEvent mouseevent) {
	Rect rect = scrollTrayRect();
	int i = axis == 0 ? mouseevent.x - rect.x : mouseevent.y - rect.y;
	Rect.returnRect(rect);
	return i;
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (!isEnabled() || !isActive())
	    return false;
	lastMouseValue = lastAltMouseValue = _mouseValue(mouseevent);
	if (mouseevent.isMetaKeyDown()
	    && (lastMouseValue >= scrollValue + knobLength
		|| lastMouseValue < scrollValue)) {
	    int i = scrollValue;
	    _setScrollValue(lastMouseValue - knobLength / 2);
	    int i_32_ = scrollValue - i;
	    if (i_32_ != 0)
		scrollToCurrentPosition();
	} else if (lastMouseValue < scrollValue) {
	    scrollPageBackward();
	    timer = new Timer(this, "timerScroll", 75);
	    timer.setInitialDelay(300);
	    if (lastMouseValue < scrollValue)
		timer.start();
	    else if (lastMouseValue < scrollValue + knobLength)
		timer = null;
	} else if (lastMouseValue >= scrollValue + knobLength) {
	    scrollPageForward();
	    timer = new Timer(this, "timerScroll", 75);
	    timer.setInitialDelay(300);
	    if (lastMouseValue >= scrollValue - knobLength)
		timer.start();
	    else if (lastMouseValue >= scrollValue)
		timer = null;
	}
	origScrollValue = scrollValue;
	return true;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	int i = _mouseValue(mouseevent);
	if (!isEnabled() || !isActive() || timer != null) {
	    lastMouseValue = i;
	    origScrollValue = scrollValue;
	} else if (mouseevent.isControlKeyDown()) {
	    pixelScrollValue = lastAltMouseValue - i;
	    scrollByPixel(pixelScrollValue);
	    lastAltMouseValue = i;
	    origScrollValue = scrollValue;
	} else {
	    lastAltMouseValue = i;
	    int i_33_ = scrollValue;
	    _setScrollValue(origScrollValue + (i - lastMouseValue));
	    int i_34_ = scrollValue - i_33_;
	    if (i_34_ != 0)
		scrollToCurrentPosition();
	}
    }
    
    private void timerScroll() {
	if (lastMouseValue < scrollValue) {
	    scrollPageBackward();
	    if (lastMouseValue >= scrollValue) {
		timer.stop();
		if (lastMouseValue < scrollValue + knobLength)
		    timer = null;
	    }
	} else if (lastMouseValue >= scrollValue + knobLength) {
	    scrollPageForward();
	    if (lastMouseValue < scrollValue - knobLength) {
		timer.stop();
		if (lastMouseValue >= scrollValue)
		    timer = null;
	    }
	}
	origScrollValue = scrollValue;
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	if (timer != null) {
	    timer.stop();
	    timer = null;
	}
    }
    
    public void performCommand(String string, Object object) {
	if ("timerScroll".equals(string))
	    timerScroll();
	else if ("updateScrollValue".equals(string))
	    update();
	else if ("scrollLineForward".equals(string))
	    scrollLineForward();
	else if ("scrollLineBackward".equals(string))
	    scrollLineBackward();
	else if ("scrollPageForward".equals(string))
	    scrollPageForward();
	else if ("scrollPageBackward".equals(string))
	    scrollPageBackward();
	else
	    throw new NoSuchMethodError("unknown command: " + string);
    }
    
    private void update() {
	int i = scrollValue;
	int i_35_ = knobLength;
	boolean bool = active;
	bool = active;
	_computeScrollValue();
	if (bool != active)
	    this.setDirty(true);
	else if (shouldRedraw
		 || (scrollValue != i || knobLength != i_35_
		     || active != bool) && isActive() && isEnabled())
	    drawScrollTray();
    }
    
    public int axis() {
	return axis;
    }
    
    public float pageSizeAsPercent() {
	return pageSizeAsPercent;
    }
    
    public void setPageSizeAsPercent(float f) {
	if (!(f <= 0.0F) && !(f > 1.0F))
	    pageSizeAsPercent = f;
    }
    
    public void setLineIncrement(int i) {
	if (i > 0)
	    lineIncrement = i;
    }
    
    public int lineIncrement() {
	return lineIncrement;
    }
    
    private void scrollTo(int i, int i_36_) {
	shouldRedraw = true;
	if (scrollableView != null)
	    scrollableView.scrollTo(i, i_36_);
	shouldRedraw = false;
    }
    
    private void scrollBy(int i, int i_37_) {
	shouldRedraw = true;
	if (scrollableView != null)
	    scrollableView.scrollBy(i, i_37_);
	shouldRedraw = false;
    }
    
    private void scrollByPixel(int i) {
	if (scrollableView != null) {
	    if (axis == 0)
		scrollBy(i, 0);
	    else
		scrollBy(0, i);
	}
    }
    
    private void scrollByLine(int i) {
	if (scrollableView != null) {
	    if (axis == 0)
		scrollBy(i * lineIncrement, 0);
	    else
		scrollBy(0, i * lineIncrement);
	}
    }
    
    private void scrollByPage(float f) {
	if (scrollableView != null) {
	    if (axis == 0)
		scrollBy
		    ((int) (f * (float) scrollableView
					    .lengthOfScrollViewForAxis(axis)),
		     0);
	    else
		scrollBy(0, (int) (f * (float) (scrollableView
						    .lengthOfScrollViewForAxis
						(axis))));
	}
    }
    
    private void scrollToPercent(float f) {
	if (scrollableView != null) {
	    int i = scrollableView.lengthOfContentViewForAxis(axis);
	    int i_38_ = scrollableView.lengthOfScrollViewForAxis(axis);
	    int i_39_ = -(int) (f * (float) (i - i_38_));
	    if (axis == 0)
		scrollTo(i_39_,
			 scrollableView.positionOfContentViewForAxis(1));
	    else
		scrollTo(scrollableView.positionOfContentViewForAxis(0),
			 i_39_);
	}
    }
    
    public void scrollLineForward() {
	scrollByLine(-1);
    }
    
    public void scrollLineBackward() {
	scrollByLine(1);
    }
    
    public void scrollPageForward() {
	scrollByPage(-pageSizeAsPercent);
    }
    
    public void scrollPageBackward() {
	scrollByPage(pageSizeAsPercent);
    }
    
    public void scrollToCurrentPosition() {
	scrollToPercent(scrollPercent());
    }
    
    public void setScrollPercent(float f) {
	_setScrollPercent(f);
	scrollToCurrentPosition();
    }
    
    void _setupKeyboard() {
	this.removeAllCommandsForKeys();
	if (axis() == 0) {
	    this.setCommandForKey("scrollLineForward", 1007, 1);
	    this.setCommandForKey("scrollLineBackward", 1006, 1);
	} else {
	    this.setCommandForKey("scrollLineForward", 1005, 1);
	    this.setCommandForKey("scrollLineBackward", 1004, 1);
	}
    }
    
    public boolean hidesSubviewsFromKeyboard() {
	return true;
    }
}
