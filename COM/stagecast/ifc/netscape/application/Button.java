/* Button - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class Button extends View implements Target, FormElement
{
    Font _titleFont;
    Color _titleColor;
    Color _disabledTitleColor;
    Color _raisedColor;
    Color _loweredColor;
    Image _image;
    Image _altImage;
    Border _raisedBorder;
    Border _loweredBorder;
    Timer _actionTimer;
    Target _target;
    String _title = "";
    String _command;
    String _altTitle = "";
    int _type;
    int _imagePosition;
    int _repeatDelay = 75;
    boolean _state;
    boolean _enabled = true;
    boolean _bordered = true;
    boolean _highlighted;
    boolean _oldState;
    boolean transparent = false;
    private int _clickCount;
    private boolean _performingAction;
    static Vector _fieldDescription;
    public static final int PUSH_TYPE = 0;
    public static final int TOGGLE_TYPE = 1;
    public static final int RADIO_TYPE = 2;
    public static final int CONTINUOUS_TYPE = 3;
    public static final int IMAGE_ON_LEFT = 0;
    public static final int IMAGE_ON_RIGHT = 1;
    public static final int IMAGE_ABOVE = 2;
    public static final int IMAGE_BELOW = 3;
    public static final int IMAGE_BENEATH = 4;
    public static final String SEND_COMMAND = "sendCommand";
    public static final String CLICK = "click";
    public static final String SELECT_NEXT_RADIO_BUTTON
	= "selectNextRadioButton";
    public static final String SELECT_PREVIOUS_RADIO_BUTTON
	= "selectPreviousRadioButton";
    
    public static Button createPushButton(int i, int i_0_, int i_1_,
					  int i_2_) {
	Button button = new Button(i, i_0_, i_1_, i_2_);
	button.setType(0);
	return button;
    }
    
    public static Button createCheckButton(int i, int i_3_, int i_4_,
					   int i_5_) {
	Button button = new Button(i, i_3_, i_4_, i_5_);
	button.setType(1);
	button.setTransparent(true);
	button.setImage(new CheckButtonImage(false));
	button.setAltImage(new CheckButtonImage(true));
	button.setImagePosition(0);
	return button;
    }
    
    public static Button createRadioButton(int i, int i_6_, int i_7_,
					   int i_8_) {
	Button button = new Button(i, i_6_, i_7_, i_8_);
	button.setType(2);
	button.setImage
	    (Bitmap.bitmapNamed("netscape/application/RadioButtonOff.gif"));
	button.setAltImage
	    (Bitmap.bitmapNamed("netscape/application/RadioButtonOn.gif"));
	button.setImagePosition(0);
	button.setTransparent(true);
	return button;
    }
    
    public Button() {
	this(0, 0, 0, 0);
    }
    
    public Button(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public Button(int i, int i_9_, int i_10_, int i_11_) {
	super(i, i_9_, i_10_, i_11_);
	_titleColor = Color.black;
	_disabledTitleColor = Color.gray;
	_titleFont = Font.defaultFont();
	_raisedBorder = BezelBorder.raisedButtonBezel();
	_loweredBorder = BezelBorder.loweredButtonBezel();
	_raisedColor = Color.lightGray;
	_loweredColor = Color.lightGray;
	_setupKeyboard();
    }
    
    public void setTitle(String string) {
	if (string == null)
	    _title = "";
	else
	    _title = string;
	this.draw();
    }
    
    public String title() {
	return _title;
    }
    
    public void setAltTitle(String string) {
	if (string == null)
	    _altTitle = "";
	else
	    _altTitle = string;
	this.setDirty(true);
    }
    
    public String altTitle() {
	return _altTitle;
    }
    
    public void setEnabled(boolean bool) {
	if (bool != _enabled) {
	    _enabled = bool;
	    this.setDirty(true);
	}
    }
    
    public boolean isEnabled() {
	return _enabled;
    }
    
    void _setState(boolean bool) {
	if (bool != _state) {
	    _state = bool;
	    this.draw();
	}
    }
    
    void selectNextRadioButton(boolean bool) {
	View view = this.superview();
	if (view != null) {
	    Vector vector = view.subviews();
	    int i = vector.indexOfIdentical(this);
	    int i_12_ = vector.count();
	    View view_13_;
	    do {
		if (bool) {
		    if (++i == i_12_)
			i = 0;
		} else if (--i < 0)
		    i = i_12_ - 1;
		view_13_ = (View) vector.elementAt(i);
		if (view_13_ instanceof Button
		    && ((Button) view_13_).type() == 2) {
		    ((Button) view_13_).setState(true);
		    this.rootView().selectView(view_13_, true);
		    break;
		}
	    } while (view_13_ != this);
	}
    }
    
    Button _otherActive() {
	if (this.superview() == null)
	    return null;
	Vector vector = this.superview().peersForSubview(this);
	int i = vector.count();
	while (i-- > 0) {
	    View view = (View) vector.elementAt(i);
	    if (view instanceof Button && view != this) {
		Button button_14_ = (Button) view;
		if (button_14_.type() == 2 && button_14_.isEnabled()
		    && button_14_.state())
		    return button_14_;
	    }
	}
	return null;
    }
    
    public void setState(boolean bool) {
	RootView rootview = this.rootView();
	if (_type == 2) {
	    if (bool == state())
		return;
	    if (bool) {
		Button button_15_ = _otherActive();
		if (button_15_ != null)
		    button_15_._setState(false);
	    }
	}
	_setState(bool);
    }
    
    public boolean state() {
	return _state;
    }
    
    public void setTarget(Target target) {
	_target = target;
    }
    
    public Target target() {
	return _target;
    }
    
    public void setCommand(String string) {
	_command = string;
    }
    
    public String command() {
	return _command;
    }
    
    public Size imageAreaSize() {
	Size size;
	if (_image != null)
	    size = new Size(_image.width(), _image.height());
	else
	    size = new Size();
	if (_altImage != null) {
	    if (_altImage.width() > size.width)
		size.width = _altImage.width();
	    if (_altImage.height() > size.height)
		size.height = _altImage.height();
	}
	return size;
    }
    
    Size minStringSize(String string) {
	int i = string.indexOf('\n');
	if (i == -1)
	    return _titleFont.fontMetrics().stringSize(string);
	Size size = new Size(0, _titleFont.fontMetrics().stringHeight());
	int i_16_ = 1;
	String string_17_ = string;
	for (/**/; i != -1; i = string_17_.indexOf('\n')) {
	    int i_18_ = _titleFont.fontMetrics()
			    .stringWidth(string_17_.substring(0, i));
	    if (i_18_ > size.width)
		size.width = i_18_;
	    i_16_++;
	    string_17_ = string_17_.substring(i + 1);
	}
	int i_19_ = _titleFont.fontMetrics().stringWidth(string_17_);
	if (i_19_ > size.width)
	    size.width = i_19_;
	size.height *= i_16_;
	return size;
    }
    
    public Size minSize() {
	Size size = null;
	Size size_20_ = null;
	boolean bool = false;
	if (_minSize != null)
	    return new Size(_minSize);
	if (_title != null && _title.length() > 0) {
	    size = minStringSize(_title);
	    bool = true;
	}
	if (_altTitle != null && _altTitle.length() > 0) {
	    size_20_ = minStringSize(_altTitle);
	    bool = true;
	}
	if (size == null)
	    size = size_20_;
	else if (size_20_ != null) {
	    if (size.width < size_20_.width)
		size.width = size_20_.width;
	    if (size.height < size_20_.height)
		size.height = size_20_.height;
	}
	if (size != null) {
	    if (size.width > 0)
		size.sizeBy(3, 0);
	} else
	    size = new Size();
	Size size_21_ = imageAreaSize();
	boolean bool_22_ = size_21_.width > 0 || size_21_.height > 0;
	if (bool_22_) {
	    if (_imagePosition == 2 || _imagePosition == 3) {
		if (size.width < size_21_.width)
		    size.width = size_21_.width;
		size.height += size_21_.height + 2;
	    } else if (bool && _imagePosition != 4) {
		size.sizeBy(size_21_.width + 2, 0);
		if (size_21_.height > size.height)
		    size.height = size_21_.height;
	    } else {
		if (size_21_.width > size.width)
		    size.width = size_21_.width;
		if (size_21_.height > size.height)
		    size.height = size_21_.height;
	    }
	}
	if (_bordered)
	    size.sizeBy(3, 3);
	return size;
    }
    
    public void setFont(Font font) {
	if (font == null)
	    _titleFont = Font.defaultFont();
	else
	    _titleFont = font;
    }
    
    public Font font() {
	return _titleFont;
    }
    
    public void setTitleColor(Color color) {
	if (color == null)
	    _titleColor = Color.black;
	else
	    _titleColor = color;
    }
    
    public Color titleColor() {
	return _titleColor;
    }
    
    public void setDisabledTitleColor(Color color) {
	if (color == null)
	    _disabledTitleColor = Color.gray;
	else
	    _disabledTitleColor = color;
    }
    
    public Color disabledTitleColor() {
	return _disabledTitleColor;
    }
    
    public void setRaisedColor(Color color) {
	if (color == null)
	    _raisedColor = Color.lightGray;
	else
	    _raisedColor = color;
    }
    
    public Color raisedColor() {
	return _raisedColor;
    }
    
    public void setLoweredColor(Color color) {
	if (color == null)
	    _loweredColor = Color.lightGray;
	else
	    _loweredColor = color;
    }
    
    public Color loweredColor() {
	return _loweredColor;
    }
    
    public void setImage(Image image) {
	_image = image;
    }
    
    public Image image() {
	return _image;
    }
    
    public void setAltImage(Image image) {
	_altImage = image;
    }
    
    public Image altImage() {
	return _altImage;
    }
    
    public void setRaisedBorder(Border border) {
	_raisedBorder = border;
    }
    
    public Border raisedBorder() {
	return _raisedBorder;
    }
    
    public void setLoweredBorder(Border border) {
	_loweredBorder = border;
    }
    
    public Border loweredBorder() {
	return _loweredBorder;
    }
    
    public void setBordered(boolean bool) {
	_bordered = bool;
	if (_bordered)
	    setTransparent(false);
    }
    
    public boolean isBordered() {
	return _bordered;
    }
    
    public void setType(int i) {
	if (i < 0 || i > 3)
	    throw new InconsistencyException("Invalid Button type: " + i);
	_type = i;
	setState(false);
	_setupKeyboard();
    }
    
    public int type() {
	return _type;
    }
    
    public void setRepeatDelay(int i) {
	if (i > 0) {
	    _repeatDelay = i;
	    if (_actionTimer != null)
		_actionTimer.setDelay(_repeatDelay);
	}
    }
    
    public int repeatDelay() {
	return _repeatDelay;
    }
    
    public void setImagePosition(int i) {
	if (i >= 0 && i <= 4)
	    _imagePosition = i;
    }
    
    public int imagePosition() {
	return _imagePosition;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
	if (transparent)
	    _bordered = false;
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    protected void ancestorWasAddedToViewHierarchy(View view) {
	super.ancestorWasAddedToViewHierarchy(view);
	if (_type == 2 && _state) {
	    _state = false;
	    setState(true);
	}
    }
    
    public void drawViewTitleInRect(Graphics graphics, String string,
				    Font font, Rect rect, int i) {
	if (string != null && string.length() != 0) {
	    if (_enabled)
		graphics.setColor(_titleColor);
	    else
		graphics.setColor(_disabledTitleColor);
	    graphics.setFont(font);
	    int i_23_ = string.indexOf('\n');
	    if (i_23_ == -1)
		graphics.drawStringInRect(string, rect, i);
	    else {
		Rect rect_24_ = new Rect(rect);
		rect_24_.height = _titleFont.fontMetrics().stringHeight();
		String string_25_ = string;
		for (/**/; i_23_ != -1; i_23_ = string_25_.indexOf('\n')) {
		    graphics.drawStringInRect(string_25_.substring(0, i_23_),
					      rect_24_, i);
		    rect_24_.y += rect_24_.height;
		    string_25_ = string_25_.substring(i_23_ + 1);
		}
		graphics.drawStringInRect(string_25_, rect_24_, i);
	    }
	}
    }
    
    public void drawViewInterior(Graphics graphics, String string, Image image,
				 Rect rect) {
	Size size = imageAreaSize();
	if (_imagePosition == 0) {
	    int i;
	    if (string == null || string.length() == 0)
		i = rect.x + 1 + (rect.width - size.width - 2) / 2;
	    else
		i = rect.x + 1;
	    if (image != null)
		image.drawAt(graphics, i,
			     rect.y + (rect.height - size.height) / 2);
	    int i_26_;
	    if (size.width > 0) {
		rect.moveBy(size.width + 3, 0);
		rect.sizeBy(-(size.width + 4), 0);
		i_26_ = 0;
	    } else {
		rect.moveBy(1, 0);
		rect.sizeBy(-2, 0);
		i_26_ = 1;
	    }
	    drawViewTitleInRect(graphics, string, _titleFont, rect, i_26_);
	} else if (_imagePosition == 2) {
	    if (image != null)
		image.drawAt(graphics, rect.x + (rect.width - size.width) / 2,
			     rect.y + 2);
	    int i = _titleFont.fontMetrics().charHeight();
	    rect.setBounds(rect.x + 1, rect.maxY() - i - 1, rect.width - 2, i);
	    drawViewTitleInRect(graphics, string, _titleFont, rect, 1);
	} else if (_imagePosition == 3) {
	    if (image != null)
		image.drawAt(graphics, rect.x + (rect.width - size.width) / 2,
			     rect.maxY() - size.height - 2);
	    rect.setBounds(rect.x + 1, rect.y + 1, rect.width - 2,
			   _titleFont.fontMetrics().charHeight());
	    drawViewTitleInRect(graphics, string, _titleFont, rect, 1);
	} else {
	    int i;
	    if (image != null && _imagePosition == 4) {
		image.drawAt(graphics, rect.x + (rect.width - size.width) / 2,
			     rect.y + (rect.height - size.height) / 2);
		i = 1;
	    } else if (size.width == 0) {
		rect.moveBy(2, 0);
		i = 1;
	    } else
		i = 0;
	    int i_27_;
	    if (string == null || string.length() == 0)
		i_27_ = rect.x + 1 + (rect.width - size.width - 2) / 2;
	    else
		i_27_ = rect.maxX() - size.width - 1;
	    if (image != null && _imagePosition == 1)
		image.drawAt(graphics, i_27_,
			     rect.y + (rect.height - size.height) / 2);
	    drawViewTitleInRect(graphics, string, _titleFont, rect, i);
	}
    }
    
    public void drawViewBackground(Graphics graphics, Rect rect,
				   boolean bool) {
	if (_bordered) {
	    rect.sizeBy(-3, -3);
	    if (bool) {
		_loweredBorder.drawInRect(graphics, 0, 0, bounds.width,
					  bounds.height);
		graphics.setColor(_loweredColor);
		graphics.fillRect(_loweredBorder.leftMargin(),
				  _loweredBorder.topMargin(),
				  bounds.width - _loweredBorder.widthMargin(),
				  (bounds.height
				   - _loweredBorder.heightMargin()));
		rect.moveBy(2, 2);
	    } else {
		_raisedBorder.drawInRect(graphics, 0, 0, bounds.width,
					 bounds.height);
		graphics.setColor(_raisedColor);
		graphics.fillRect(_raisedBorder.leftMargin(),
				  _raisedBorder.topMargin(),
				  bounds.width - _raisedBorder.widthMargin(),
				  (bounds.height
				   - _raisedBorder.heightMargin()));
		rect.moveBy(1, 1);
	    }
	} else if (!isTransparent()) {
	    if (bool)
		graphics.setColor(_loweredColor);
	    else
		graphics.setColor(_raisedColor);
	    graphics.fillRect(0, 0, bounds.width, bounds.height);
	}
    }
    
    public void drawView(Graphics graphics) {
	Image image = null;
	boolean bool = _highlighted ? _state ^ true : _state;
	Rect rect = Rect.newRect(0, 0, bounds.width, bounds.height);
	drawViewBackground(graphics, rect, bool);
	if (image == null) {
	    image = _image;
	    if (bool && _altImage != null)
		image = _altImage;
	}
	String string;
	if (bool && _altTitle != null && _altTitle.length() != 0)
	    string = _altTitle;
	else
	    string = _title;
	if (image == null && (string == null || string.length() == 0))
	    Rect.returnRect(rect);
	else {
	    drawViewInterior(graphics, string, image, rect);
	    Rect.returnRect(rect);
	}
    }
    
    Button _activeForPoint(int i, int i_28_) {
	if (this.superview() == null)
	    return null;
	Vector vector = this.superview().peersForSubview(this);
	Point point = Point.newPoint();
	int i_29_ = vector.count();
	while (i_29_-- > 0) {
	    View view = (View) vector.elementAt(i_29_);
	    if (view instanceof Button && view != this) {
		Button button_30_ = (Button) view;
		if (button_30_.type() == 2) {
		    this.convertToView(view, i, i_28_, point);
		    if (button_30_.containsPoint(point.x, point.y)) {
			Point.returnPoint(point);
			return button_30_;
		    }
		}
	    }
	}
	Point.returnPoint(point);
	return null;
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (!_enabled)
	    return false;
	if (!this.containsPoint(mouseevent.x, mouseevent.y))
	    return false;
	if (_type == 2) {
	    Button button_31_ = _otherActive();
	    if (button_31_ != null)
		button_31_._setState(false);
	    _oldState = _state;
	    _state = false;
	}
	_clickCount = mouseevent.clickCount;
	if (_type == 1 || _type == 2)
	    setHighlighted(true);
	else
	    setState(true);
	if (_type == 3 && _actionTimer == null) {
	    sendCommand();
	    _actionTimer = new Timer(this, "sendCommand", _repeatDelay);
	    _actionTimer.setInitialDelay(300);
	    _actionTimer.start();
	}
	return true;
    }
    
    void _buttonDown() {
	/* empty */
    }
    
    void _buttonUp() {
	/* empty */
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	if (_enabled) {
	    if (_type == 2
		&& !this.containsPoint(mouseevent.x, mouseevent.y)) {
		Button button_32_
		    = _activeForPoint(mouseevent.x, mouseevent.y);
		if (button_32_ != null) {
		    setHighlighted(false);
		    button_32_.setHighlighted(true);
		    this.rootView().setMouseView(button_32_);
		}
	    } else if (this.containsPoint(mouseevent.x, mouseevent.y)) {
		if (!_state && !_highlighted) {
		    _buttonDown();
		    if (_type == 1 || _type == 2)
			setHighlighted(true);
		    else
			setState(true);
		    if (_type == 3) {
			sendCommand();
			_actionTimer = new Timer(this, "sendCommand", 100);
			_actionTimer.start();
		    }
		}
	    } else if (_state || _highlighted) {
		_buttonUp();
		if (_type == 3 && _actionTimer != null) {
		    _actionTimer.stop();
		    _actionTimer = null;
		}
		if (_type == 1 || _type == 2)
		    setHighlighted(false);
		else
		    setState(false);
	    }
	}
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	if (_enabled) {
	    if (_type == 2) {
		if (_highlighted) {
		    _highlighted = false;
		    setState(true);
		}
		if (_state != _oldState)
		    sendCommand();
		_oldState = false;
		if (canBecomeSelectedView() && this.rootView() != null)
		    this.rootView().selectView(this, true);
	    } else {
		if (_actionTimer != null) {
		    _actionTimer.stop();
		    _actionTimer = null;
		}
		boolean bool = this.containsPoint(mouseevent.x, mouseevent.y);
		if (bool)
		    _buttonUp();
		if (_type == 3) {
		    _state = false;
		    if (bool)
			this.setDirty(true);
		    if (canBecomeSelectedView() && this.rootView() != null)
			this.rootView().selectView(this, true);
		} else {
		    if (_type == 1) {
			if (bool) {
			    _highlighted = false;
			    _state = _state ^ true;
			} else
			    _highlighted = false;
			if (bool)
			    this.setDirty(true);
			if (bool)
			    sendCommand();
		    } else {
			if (_type != 3 && bool)
			    sendCommand();
			_state = false;
			if (bool)
			    this.setDirty(true);
		    }
		    if (canBecomeSelectedView() && this.rootView() != null)
			this.rootView().selectView(this, true);
		}
	    }
	}
    }
    
    public int clickCount() {
	if (_performingAction)
	    return _clickCount;
	return 0;
    }
    
    public void click() {
	if (_enabled && (_type != 2 || !_state)) {
	    if (_type == 1 || _type == 2) {
		setState(_state ^ true);
		this.application().syncGraphics();
		sendCommand();
	    } else {
		setState(true);
		this.application().syncGraphics();
		try {
		    Thread.sleep(200L);
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
		_clickCount = 1;
		sendCommand();
		setState(false);
	    }
	}
    }
    
    public void performCommand(String string, Object object) {
	int i = type();
	if ("sendCommand".equals(string))
	    sendCommand();
	else if ("click".equals(string))
	    click();
	else if (i == 2 && "selectPreviousRadioButton".equals(string))
	    selectNextRadioButton(false);
	else if (i == 2 && "selectNextRadioButton".equals(string))
	    selectNextRadioButton(true);
	else
	    throw new NoSuchMethodError("unknown command: " + string);
    }
    
    public void sendCommand() {
	_performingAction = true;
	if (_target != null)
	    _target.performCommand(_command, this);
	_performingAction = false;
    }
    
    protected void setHighlighted(boolean bool) {
	if (_highlighted != bool) {
	    _highlighted = bool;
	    this.setDirty(true);
	}
    }
    
    protected boolean isHighlighted() {
	return _highlighted;
    }
    
    public boolean canBecomeSelectedView() {
	if (isEnabled() && this.hasKeyboardBindings()) {
	    if (type() == 2) {
		if (state() == true)
		    return true;
		return false;
	    }
	    return true;
	}
	return false;
    }
    
    void _setupKeyboard() {
	this.removeAllCommandsForKeys();
	if (type() == 2) {
	    this.setCommandForKey("selectNextRadioButton", 1007, 0);
	    this.setCommandForKey("selectNextRadioButton", 1005, 0);
	    this.setCommandForKey("selectPreviousRadioButton", 1006, 0);
	    this.setCommandForKey("selectPreviousRadioButton", 1004, 0);
	} else
	    this.setCommandForKey("click", 10, 0);
    }
    
    public String formElementText() {
	if (_type == 1) {
	    if (_state)
		return "true";
	    return "false";
	}
	return title();
    }
}
