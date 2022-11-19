/* TextField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class TextField extends View implements ExtendedTarget, FormElement
{
    TextFieldOwner _owner;
    TextFilter _filter;
    Target _tabTarget;
    Target _backtabTarget;
    Target _contentsChangedTarget;
    Target _commitTarget;
    Vector _keyVector;
    Border border = BezelBorder.loweredBezel();
    Font _font;
    Color _textColor;
    Color _backgroundColor;
    Color _selectionColor;
    Color _caretColor;
    String _tabCommand;
    String _backtabCommand;
    String _contentsChangedCommand;
    String _commitCommand;
    FastStringBuffer _contents;
    FastStringBuffer _oldContents;
    Timer blinkTimer;
    char _drawableCharacter;
    int _selectionAnchorChar = -1;
    int _selectionEndChar = -1;
    int _justification;
    int _scrollOffset;
    int _fontHeight;
    int _initialAnchorChar = -1;
    int _clickCount;
    boolean _editing;
    boolean _caretShowing;
    boolean _canBlink;
    boolean _editable;
    boolean _selectable;
    boolean _mouseDragging;
    boolean _shadowed;
    boolean _textChanged = false;
    boolean _canWrap;
    boolean transparent = false;
    boolean wantsAutoscrollEvents = false;
    boolean isScrollable = true;
    boolean hasFocus = false;
    boolean _ignoreWillBecomeSelected;
    private int _dropShadowOffset = 2;
    public static char ANY_CHARACTER = '\uffff';
    public static final String SELECT_TEXT = "selectText";
    static final String BLINK_CARET = "blinkCaret";
    
    public TextField() {
	this(0, 0, 0, 0);
    }
    
    public TextField(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public TextField(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	_keyVector = new Vector();
	_contents = new FastStringBuffer();
	_drawableCharacter = ANY_CHARACTER;
	_textColor = Color.black;
	_backgroundColor = Color.white;
	_selectionColor = Color.lightGray;
	_caretColor = Color.black;
	setEditable(true);
	setFont(Font.defaultFont());
    }
    
    /**
     * @deprecated
     */
    public static TextField createLabel(String string, Font font) {
	FontMetrics fontmetrics = font.fontMetrics();
	int i = fontmetrics.stringWidth(string);
	int i_3_ = fontmetrics.stringHeight();
	TextField textfield = new TextField(0, 0, i, i_3_);
	textfield.setBorder(null);
	textfield.setStringValue(string);
	textfield.setFont(font);
	textfield.setTransparent(true);
	textfield.setEditable(false);
	textfield.setSelectable(false);
	return textfield;
    }
    
    public static TextField createLabel(String string) {
	return createLabel(string, Font.defaultFont());
    }
    
    private static int parseInt(String string) {
	try {
	    return Integer.parseInt(string);
	} catch (NumberFormatException numberformatexception) {
	    return 0;
	}
    }
    
    public int leftIndent() {
	int i = border.leftMargin();
	if (i > 2)
	    i = 2;
	return i;
    }
    
    public int rightIndent() {
	int i = border.rightMargin() + 1;
	if (i > 3)
	    i = 3;
	return i;
    }
    
    private int widthIndent() {
	return leftIndent() + rightIndent();
    }
    
    public Size minSize() {
	if (_minSize != null)
	    return new Size(_minSize);
	Size size = _font.fontMetrics().stringSize(drawableString());
	Rect rect = interiorRect();
	if (this.horizResizeInstruction() != 2 && _canWrap && !isEditable()
	    && size.width > rect.width) {
	    Vector vector = stringVectorForContents(rect.width);
	    size.sizeTo(rect.width, size.height * vector.count());
	}
	Rect.returnRect(rect);
	size.sizeBy(border.widthMargin() + widthIndent(),
		    border.heightMargin());
	return size;
    }
    
    public void setDrawableCharacter(char c) {
	_drawableCharacter = c;
	_scrollOffset = 0;
	_computeScrollOffset();
	this.setDirty(true);
    }
    
    public char drawableCharacter() {
	return _drawableCharacter;
    }
    
    public void setFont(Font font) {
	if (font == null)
	    _font = Font.defaultFont();
	else
	    _font = font;
	Size size = _font.fontMetrics().stringSize(null);
	_fontHeight = size.height;
	this.setDirty(true);
    }
    
    public Font font() {
	return _font;
    }
    
    public void setTextColor(Color color) {
	_textColor = color;
	if (_textColor == null)
	    _textColor = Color.black;
	this.setDirty(true);
    }
    
    public Color textColor() {
	return _textColor;
    }
    
    public void setBackgroundColor(Color color) {
	_backgroundColor = color;
	if (_backgroundColor == null)
	    _backgroundColor = Color.white;
	this.setDirty(true);
    }
    
    public Color backgroundColor() {
	return _backgroundColor;
    }
    
    public void setSelectionColor(Color color) {
	_selectionColor = color;
	if (_selectionColor == null)
	    _selectionColor = Color.lightGray;
	this.setDirty(true);
    }
    
    public Color selectionColor() {
	return _selectionColor;
    }
    
    public void setCaretColor(Color color) {
	_caretColor = color;
	if (_caretColor == null)
	    _caretColor = Color.black;
    }
    
    public Color caretColor() {
	return _caretColor;
    }
    
    public void setBorder(Border border) {
	if (border == null)
	    border = EmptyBorder.emptyBorder();
	this.border = border;
    }
    
    public Border border() {
	return border;
    }
    
    public void setDropShadowOffset(int i) {
	_dropShadowOffset = i;
	this.setDirty(true);
    }
    
    public void setDrawsDropShadow(boolean bool) {
	_shadowed = bool;
	this.setDirty(true);
    }
    
    public boolean drawsDropShadow() {
	return _shadowed;
    }
    
    public void setJustification(int i) {
	if (i >= 0 && i <= 2) {
	    if (i != _justification) {
		_justification = i;
		_scrollOffset = 0;
	    }
	}
    }
    
    public int justification() {
	return _justification;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
	if (transparent)
	    setBorder(null);
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public void setSelectable(boolean bool) {
	if (_selectable != bool) {
	    _selectable = bool;
	    wantsAutoscrollEvents = bool;
	    if (!_selectable && _scrollOffset != 0) {
		_scrollOffset = 0;
		drawInterior();
	    }
	    RootView rootview = this.rootView();
	    if (rootview != null)
		rootview.updateCursor();
	}
    }
    
    public boolean isSelectable() {
	return _selectable;
    }
    
    public boolean wantsAutoscrollEvents() {
	return wantsAutoscrollEvents;
    }
    
    public void setWrapsContents(boolean bool) {
	_canWrap = bool;
	if (bool && isEditable())
	    setEditable(false);
	drawInterior();
    }
    
    public boolean wrapsContents() {
	return _canWrap;
    }
    
    public void setEditable(boolean bool) {
	if (_editable != bool) {
	    _editable = bool;
	    setSelectable(bool);
	    if (bool && wrapsContents())
		setWrapsContents(false);
	}
    }
    
    public boolean isEditable() {
	return _editable;
    }
    
    public boolean isBeingEdited() {
	return _editing;
    }
    
    public int cursorForPoint(int i, int i_4_) {
	if (isEditable() || isSelectable())
	    return 2;
	return 0;
    }
    
    public void setOwner(TextFieldOwner textfieldowner) {
	_owner = textfieldowner;
    }
    
    public TextFieldOwner owner() {
	return _owner;
    }
    
    public void setFilter(TextFilter textfilter) {
	_filter = textfilter;
    }
    
    public TextFilter filter() {
	return _filter;
    }
    
    public void setContentsChangedCommandAndTarget(String string,
						   Target target) {
	_contentsChangedCommand = string;
	_contentsChangedTarget = target;
    }
    
    public Target contentsChangedTarget() {
	return _contentsChangedTarget;
    }
    
    public String contentsChangedCommand() {
	return _contentsChangedCommand;
    }
    
    public void setTabField(TextField textfield_5_) {
	if (textfield_5_ == null) {
	    _tabTarget = null;
	    _tabCommand = null;
	} else {
	    _tabTarget = textfield_5_;
	    _tabCommand = "selectText";
	}
	this.invalidateKeyboardSelectionOrder();
    }
    
    public TextField tabField() {
	if (_tabTarget instanceof TextField)
	    return (TextField) _tabTarget;
	return null;
    }
    
    public void setBacktabField(TextField textfield_6_) {
	if (textfield_6_ == null) {
	    _backtabTarget = null;
	    _backtabCommand = null;
	} else if (textfield_6_ != this) {
	    _backtabTarget = textfield_6_;
	    _backtabCommand = "selectText";
	}
	this.invalidateKeyboardSelectionOrder();
    }
    
    public TextField backtabField() {
	if (_backtabTarget instanceof TextField)
	    return (TextField) _backtabTarget;
	return null;
    }
    
    public void setTarget(Target target) {
	_commitTarget = target;
    }
    
    public void setCommand(String string) {
	_commitCommand = string;
    }
    
    public Target target() {
	return _commitTarget;
    }
    
    public String command() {
	return _commitCommand;
    }
    
    public void setStringValue(String string) {
	if (string != null && string.equals(stringValue())) {
	    if (isBeingEdited())
		cancelEditing();
	} else {
	    if (string == null)
		string = "";
	    replaceRangeWithString(new Range(0, charCount()), string);
	    _oldContents = null;
	    if (isBeingEdited())
		cancelEditing();
	    else
		this.setDirty(true);
	}
    }
    
    public String stringValue() {
	if (_contents == null)
	    return "";
	return _contents.toString();
    }
    
    public void replaceRangeWithString(Range range, String string) {
	String string_7_ = stringValue();
	Range range_8_ = new Range();
	range_8_.index = range.index;
	range_8_.length = range.length;
	range_8_.intersectWith(new Range(0, string_7_.length()));
	if (range_8_.isNullRange()) {
	    range_8_.index = string_7_.length();
	    range_8_.length = 0;
	}
	String string_9_ = string_7_.substring(0, range_8_.index);
	String string_10_
	    = string_7_.substring(range_8_.index + range_8_.length);
	if (string != null)
	    _contents = new FastStringBuffer(string_9_ + string + string_10_);
	else
	    _contents = new FastStringBuffer(string_9_ + string_10_);
	_textChanged = true;
	if (isBeingEdited()) {
	    range_8_.index = 0;
	    range_8_.length = _contents.length();
	    Range range_11_ = selectedRange();
	    range_11_.intersectWith(range_8_);
	    if (range_11_.isNullRange())
		selectRange(new Range(_contents.length, 0));
	    else
		selectRange(range_11_);
	} else
	    this.setDirty(true);
    }
    
    public String stringForRange(Range range) {
	String string = stringValue();
	Range range_12_ = new Range();
	range_12_.index = range.index;
	range_12_.length = range.length;
	range_12_.intersectWith(new Range(0, string.length()));
	if (range_12_.isNullRange())
	    return "";
	return string.substring(range_12_.index,
				range_12_.index + range_12_.length());
    }
    
    public String selectedStringValue() {
	if (hasInsertionPoint())
	    return "";
	int i = selectionStart();
	int i_13_ = selectionStop();
	if (i == -1 || i_13_ == -1)
	    return "";
	if (i_13_ == _contents.length())
	    return _contents.toString().substring(i);
	return _contents.toString().substring(i, i_13_);
    }
    
    public void setIntValue(int i) {
	setStringValue(Integer.toString(i));
    }
    
    public int intValue() {
	return parseInt(_contents.toString());
    }
    
    public boolean isEmpty() {
	return charCount() == 0;
    }
    
    public int charCount() {
	return _contents.length();
    }
    
    public int baseline() {
	String string = drawableString();
	Size size = _font.fontMetrics().stringSize(string);
	Rect rect = Rect.newRect();
	border.computeInteriorRect(0, 0, bounds.width, bounds.height, rect);
	int i = (rect.maxY() - (rect.height - size.height) / 2
		 - _font.fontMetrics().descent());
	Rect.returnRect(rect);
	return i;
    }
    
    public void selectRange(Range range) {
	if (range.length < 0 || range.index < 0)
	    throw new InconsistencyException("TextField - invalid range: "
					     + range);
	selectRange(range.index, range.index + range.length);
    }
    
    protected void selectRange(int i, int i_14_) {
	if (isSelectable()) {
	    if (i < 0)
		i = 0;
	    else if (i > _contents.length())
		i = _contents.length();
	    if (i_14_ < 0)
		i_14_ = 0;
	    else if (i_14_ > _contents.length())
		i_14_ = _contents.length();
	    _selectionAnchorChar = i;
	    _selectionEndChar = i_14_;
	    if (isEditable() && !isBeingEdited())
		_startEditing(true);
	    drawInterior();
	}
    }
    
    public void selectText() {
	selectRange(0, charCount());
    }
    
    public void setInsertionPoint(int i) {
	selectRange(i, i);
    }
    
    int selectionAnchorPoint() {
	return _selectionAnchorChar;
    }
    
    int selectionEndPoint() {
	return _selectionEndChar;
    }
    
    public Range selectedRange() {
	if (hasInsertionPoint())
	    return new Range(_selectionAnchorChar, 0);
	if (_selectionAnchorChar == -1 || _selectionEndChar == -1)
	    return new Range();
	return Range.rangeFromIndices(selectionStart(), selectionStop());
    }
    
    int selectionStart() {
	return (_selectionAnchorChar < _selectionEndChar ? _selectionAnchorChar
		: _selectionEndChar);
    }
    
    int selectionStop() {
	return (_selectionAnchorChar < _selectionEndChar ? _selectionEndChar
		: _selectionAnchorChar);
    }
    
    public boolean hasSelection() {
	return _selectionAnchorChar != _selectionEndChar;
    }
    
    public boolean hasInsertionPoint() {
	return (_selectionAnchorChar == _selectionEndChar
		&& _selectionAnchorChar != -1);
    }
    
    Rect caretRect() {
	FontMetrics fontmetrics = _font.fontMetrics();
	if (fontmetrics == null)
	    return null;
	Rect rect = interiorRect();
	int i = rect.maxY() - (rect.height - _fontHeight) / 2;
	int i_15_ = i - fontmetrics.charHeight();
	int i_16_ = border.topMargin();
	if (i_15_ < i_16_)
	    i_15_ = i_16_;
	Rect.returnRect(rect);
	return Rect.newRect(xPositionOfCharacter(_selectionEndChar), i_15_, 1,
			    i - i_15_);
    }
    
    Rect interiorRect() {
	Rect rect = Rect.newRect();
	border.computeInteriorRect(0, 0, this.width(), this.height(), rect);
	return rect;
    }
    
    Rect rectForRange(int i, int i_17_) {
	int i_18_ = xPositionOfCharacter(i);
	Rect rect = interiorRect();
	rect.setBounds(i_18_, rect.y, xPositionOfCharacter(i_17_) - i_18_ + 1,
		       rect.height);
	return rect;
    }
    
    public int xPositionOfCharacter(int i) {
	FontMetrics fontmetrics = _font.fontMetrics();
	String string = drawableString();
	int i_19_ = fontmetrics.stringWidth(string);
	int i_20_ = absoluteXOriginForStringWithWidth(i_19_);
	if (i <= 0)
	    return i_20_;
	return i_20_ + fontmetrics.stringWidth(string.substring(0, i));
    }
    
    public int charNumberForPoint(int i) {
	int i_21_ = _contents.length();
	if (i_21_ == 0)
	    return 0;
	FontMetrics fontmetrics = _font.fontMetrics();
	String string = drawableString();
	int i_22_ = fontmetrics.stringWidth(string);
	int i_23_ = absoluteXOriginForStringWithWidth(i_22_);
	if (i < i_23_)
	    return 0;
	if (i > i_23_ + i_22_)
	    return i_21_;
	int i_24_ = 0;
	for (int i_25_ = 1; i_25_ < i_21_; i_25_++) {
	    int i_26_ = fontmetrics.stringWidth(string.substring(0, i_25_));
	    int i_27_ = i_26_ - i_24_;
	    if (i <= i_23_ + i_26_) {
		if (i > i_23_ + i_24_ + i_27_ / 2)
		    return i_25_;
		return i_25_ - 1;
	    }
	    i_24_ = i_26_;
	}
	return i_21_;
    }
    
    void drawViewCaret(Graphics graphics) {
	if (_caretShowing && _selectionAnchorChar != -1
	    && _selectionAnchorChar == _selectionEndChar && hasFocus) {
	    graphics.setColor(_caretColor);
	    Rect rect = caretRect();
	    graphics.drawLine(rect.x, rect.y, rect.x, rect.maxY() - 1);
	    Rect.returnRect(rect);
	}
    }
    
    Vector stringVectorForContents(int i) {
	Vector vector = new Vector();
	String string = drawableString();
	char[] cs = new char[1];
	FontMetrics fontmetrics = font().fontMetrics();
	int[] is = fontmetrics.widthsArray();
	int i_28_ = 0;
	int i_29_ = string.length();
	int i_30_ = i_28_;
	int i_31_ = 0;
	int i_32_ = -1;
	while (i_28_ < i_29_) {
	    char c = string.charAt(i_28_);
	    if (c == ' ' || c == '\t')
		i_32_ = i_28_;
	    if (c == '\n') {
		vector.addElement(string.substring(i_30_, i_28_));
		i_30_ = ++i_28_;
		i_31_ = 0;
		i_32_ = -1;
	    } else {
		if (c < '\u0100')
		    i_31_ += is[c];
		else {
		    cs[0] = c;
		    i_31_ += fontmetrics.stringWidth(new String(cs));
		}
		if (i_31_ > i) {
		    if (i_28_ == i_30_) {
			vector.addElement(string.substring(i_30_, i_30_ + 1));
			i_30_ = ++i_28_;
		    }
		    if (i_32_ == -1) {
			vector.addElement(string.substring(i_30_, i_28_));
			i_30_ = i_28_;
		    } else {
			vector.addElement(string.substring(i_30_, i_32_));
			i_30_ = i_32_ + 1;
			i_28_ = i_30_;
		    }
		    i_31_ = 0;
		    i_32_ = -1;
		} else
		    i_28_++;
	    }
	}
	if (i_30_ < i_29_)
	    vector.addElement(string.substring(i_30_));
	return vector;
    }
    
    public void drawViewStringAt(Graphics graphics, String string, int i,
				 int i_33_) {
	if (_shadowed) {
	    graphics.setColor(Color.black);
	    graphics.drawString(string, i + _dropShadowOffset,
				i_33_ + _dropShadowOffset);
	}
	graphics.setColor(_textColor);
	graphics.drawString(string, i, i_33_);
    }
    
    int absoluteXOriginForStringWithWidth(int i) {
	int i_34_;
	if (_justification == 2)
	    i_34_ = (this.width() - border.rightMargin() - rightIndent() - i
		     - _scrollOffset);
	else if (_justification == 1)
	    i_34_ = (border.leftMargin() + leftIndent()
		     + (this.width() - (border.widthMargin() + widthIndent())
			- i) / 2
		     - _scrollOffset);
	else
	    i_34_ = border.leftMargin() + leftIndent() - _scrollOffset;
	return i_34_;
    }
    
    void drawViewLine(Graphics graphics, String string, Size size, int i) {
	if (size == null)
	    size = _font.fontMetrics().stringSize(string);
	int i_35_ = absoluteXOriginForStringWithWidth(size.width);
	drawViewStringAt(graphics, string, i_35_, i);
    }
    
    public void drawViewInterior(Graphics graphics, Rect rect) {
	int i = baseline();
	graphics.pushState();
	graphics.setClipRect(rect);
	if (!isTransparent()) {
	    graphics.setColor(_backgroundColor);
	    graphics.fillRect(rect);
	}
	if (_selectionAnchorChar != _selectionEndChar && hasFocus
	    && isSelectable()) {
	    Rect rect_36_ = caretRect();
	    int i_37_ = xPositionOfCharacter(selectionStart());
	    int i_38_;
	    if (selectionStop() == charCount())
		i_38_ = rect_36_.x + rect_36_.width;
	    else
		i_38_ = xPositionOfCharacter(selectionStop());
	    graphics.setColor(_selectionColor);
	    graphics.fillRect(i_37_, rect_36_.y, i_38_ - i_37_,
			      rect_36_.height);
	    Rect.returnRect(rect_36_);
	}
	graphics.setFont(_font);
	String string = drawableString();
	Size size = _font.fontMetrics().stringSize(string);
	if (!_canWrap || isEditable())
	    drawViewLine(graphics, string, size, i);
	else {
	    Vector vector = stringVectorForContents(rect.width);
	    int i_39_ = vector.count();
	    if (i_39_ > 1) {
		i += (rect.height - size.height) / 2;
		int i_40_ = (rect.height - size.height * i_39_) / 2;
		i -= i_40_ + (i_39_ - 1) * size.height;
	    }
	    for (int i_41_ = 0; i_41_ < i_39_; i_41_++) {
		drawViewLine(graphics, (String) vector.elementAt(i_41_), null,
			     i);
		i += size.height;
	    }
	}
	if (isBeingEdited() && _caretShowing)
	    drawViewCaret(graphics);
	graphics.popState();
    }
    
    public void drawViewBorder(Graphics graphics) {
	if (border != null)
	    border.drawInRect(graphics, 0, 0, this.width(), this.height());
	else if (!isTransparent() && _backgroundColor != null) {
	    graphics.setColor(_backgroundColor);
	    graphics.fillRect(0, 0, this.width(), this.height());
	}
    }
    
    public void drawView(Graphics graphics) {
	drawViewBorder(graphics);
	Rect rect = interiorRect();
	drawViewInterior(graphics, rect);
	Rect.returnRect(rect);
    }
    
    void drawCaret() {
	Rect rect = caretRect();
	this.addDirtyRect(rect);
	Rect.returnRect(rect);
    }
    
    void hideCaret() {
	_caretShowing = false;
	drawCaret();
    }
    
    void showCaret() {
	_caretShowing = true;
	drawCaret();
    }
    
    public void drawInterior() {
	Rect rect = interiorRect();
	this.addDirtyRect(rect);
	Rect.returnRect(rect);
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	boolean bool = true;
	Rect rect = null;
	_clickCount = mouseevent.clickCount();
	if (_clickCount > 3)
	    return true;
	if (!isSelectable())
	    return false;
	if (!isBeingEdited()) {
	    if (isEditable())
		_startEditing(true);
	    else if (isSelectable())
		_startEditing(false);
	}
	if (!this.rootView().mouseStillDown()) {
	    int i = charNumberForPoint(mouseevent.x);
	    _clickCount = 0;
	    selectRange(new Range(i, 0));
	    if (!hasSelection() && isEditable()) {
		_caretShowing = _canBlink = true;
		drawCaret();
		_startBlinkTimer();
	    }
	    return true;
	}
	boolean bool_42_ = _caretShowing;
	_canBlink = _caretShowing = false;
	_mouseDragging = true;
	if (hasSelection())
	    rect = rectForRange(selectionStart(), selectionStop());
	else if (bool_42_)
	    hideCaret();
	Range range = selectedRange();
	if (mouseevent.isShiftKeyDown() && _clickCount == 1) {
	    _selectionEndChar = charNumberForPoint(mouseevent.x);
	    if (rect != null)
		rect.unionWith(rectForRange(selectionStart(),
					    selectionStop()));
	    else
		rect = rectForRange(selectionStart(), selectionStop());
	} else
	    _selectionAnchorChar = _selectionEndChar = _initialAnchorChar
		= charNumberForPoint(mouseevent.x);
	switch (_clickCount) {
	case 2: {
	    Range range_43_ = groupForIndex(_selectionAnchorChar);
	    if (!range_43_.isNullRange()) {
		if (mouseevent.isShiftKeyDown()) {
		    Range range_44_ = new Range(range);
		    range_44_.unionWith(range_43_);
		    selectRange(range_44_);
		} else
		    selectRange(range_43_);
	    }
	    rect = null;
	    break;
	}
	case 3:
	    selectRange(new Range(0, charCount()));
	    rect = null;
	    break;
	}
	if (rect != null) {
	    this.addDirtyRect(rect);
	    Rect.returnRect(rect);
	}
	return true;
    }
    
    void _computeScrollOffset() {
	if (!isScrollable)
	    _scrollOffset = 0;
	else {
	    int i = border.leftMargin() + leftIndent();
	    int i_45_ = border.rightMargin() + rightIndent();
	    Rect rect = caretRect();
	    Rect rect_46_ = interiorRect();
	    String string = drawableString();
	    if (rect_46_.width - (leftIndent() + rightIndent())
		> _font.fontMetrics().stringWidth(string))
		_scrollOffset = 0;
	    else if (rect.x >= i && rect.x < bounds.width - i_45_)
		Rect.returnRect(rect);
	    else {
		if (rect.x < i)
		    _scrollOffset += rect.x - i;
		else
		    _scrollOffset += rect.x - (bounds.width - i_45_);
		Rect.returnRect(rect);
	    }
	}
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	boolean bool = false;
	boolean bool_47_ = true;
	if (isSelectable() && _clickCount <= 2) {
	    int i = _selectionEndChar;
	    _selectionEndChar = charNumberForPoint(mouseevent.x);
	    if (_clickCount == 2) {
		Range range = groupForIndex(_initialAnchorChar);
		Range range_48_ = groupForIndex(_selectionEndChar);
		Range range_49_ = Range.rangeFromUnion(range, range_48_);
		if (!range_49_.isNullRange()) {
		    if (range_48_.index > range.index) {
			_selectionAnchorChar = range.index;
			_selectionEndChar = range_48_.index + range_48_.length;
		    } else {
			_selectionAnchorChar = range.index + range.length;
			_selectionEndChar = range_48_.index;
		    }
		}
	    }
	    if (!this.containsPointInVisibleRect(mouseevent.x, 1)) {
		int i_50_ = _scrollOffset;
		_computeScrollOffset();
		if (_scrollOffset != i_50_) {
		    drawInterior();
		    bool_47_ = false;
		}
	    }
	    if (bool_47_ && _selectionEndChar != i) {
		int i_51_;
		int i_52_;
		if ((_selectionEndChar < _selectionAnchorChar
		     && i > _selectionAnchorChar)
		    || (_selectionEndChar > _selectionAnchorChar
			&& i < _selectionAnchorChar)) {
		    i_51_ = selectionStart();
		    i_52_ = selectionStop();
		    if (i < i_51_)
			i_51_ = i;
		    if (i > i_52_)
			i_52_ = i;
		} else if (_selectionEndChar > i) {
		    i_51_ = i;
		    i_52_ = _selectionEndChar;
		} else {
		    i_51_ = _selectionEndChar;
		    i_52_ = i;
		}
		Rect rect = rectForRange(i_51_, i_52_);
		this.addDirtyRect(rect);
		Rect.returnRect(rect);
	    }
	}
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	_mouseDragging = false;
	if (!hasSelection() && isEditable()) {
	    _caretShowing = _canBlink = true;
	    drawCaret();
	}
	_initialAnchorChar = -1;
	_clickCount = 0;
    }
    
    void _keyDown(KeyEvent keyevent) {
	if (keyevent.key != 1022) {
	    if (keyevent.type == -11
		&& (keyevent.isReturnKey() || keyevent.isTabKey()
		    || keyevent.isBackTabKey())) {
		int i;
		if (keyevent.isReturnKey())
		    i = 2;
		else if (keyevent.isTabKey())
		    i = 0;
		else
		    i = 1;
		if (_owner == null
		    || _owner.textEditingWillEnd(this, i, _textChanged)) {
		    boolean bool = _textChanged;
		    _completeEditing();
		    if (_owner != null)
			_owner.textEditingDidEnd(this, i, bool);
		    if (keyevent.isBackTabKey()) {
			sendBacktabCommand();
			if (bool)
			    sendCommitCommand(true);
		    } else if (keyevent.isTabKey()) {
			sendTabCommand();
			if (bool)
			    sendCommitCommand(true);
		    } else
			sendCommitCommand(true);
		}
	    } else if (keyevent.type == -11 && keyevent.isLeftArrowKey()) {
		if (keyevent.isShiftKeyDown()) {
		    int i = _scrollOffset;
		    selectRange(_selectionAnchorChar, _selectionEndChar - 1);
		    _computeScrollOffset();
		    if (_scrollOffset != i)
			drawInterior();
		} else {
		    int i = _selectionAnchorChar;
		    boolean bool = false;
		    if (_selectionAnchorChar != _selectionEndChar) {
			bool = true;
			_selectionAnchorChar = selectionStart();
			i = -1;
		    } else if (_selectionAnchorChar > 0) {
			hideCaret();
			_selectionAnchorChar--;
		    }
		    _selectionEndChar = _selectionAnchorChar;
		    if (i != _selectionAnchorChar) {
			int i_53_ = _scrollOffset;
			_computeScrollOffset();
			if (i_53_ != _scrollOffset || bool) {
			    _caretShowing = true;
			    drawInterior();
			} else
			    showCaret();
		    }
		}
	    } else if (keyevent.type == -11 && keyevent.isRightArrowKey()) {
		if (keyevent.isShiftKeyDown()) {
		    int i = _scrollOffset;
		    selectRange(_selectionAnchorChar, _selectionEndChar + 1);
		    _computeScrollOffset();
		    if (_scrollOffset != i)
			drawInterior();
		} else {
		    int i = _selectionAnchorChar;
		    boolean bool = false;
		    if (_selectionAnchorChar != _selectionEndChar) {
			bool = true;
			_selectionAnchorChar = selectionStop();
			i = -1;
		    } else if (_selectionAnchorChar < _contents.length()) {
			hideCaret();
			_selectionAnchorChar++;
			if (_selectionAnchorChar > _contents.length())
			    _selectionAnchorChar = _contents.length();
		    }
		    _selectionEndChar = _selectionAnchorChar;
		    if (i != _selectionAnchorChar) {
			int i_54_ = _scrollOffset;
			_computeScrollOffset();
			if (i_54_ != _scrollOffset || bool) {
			    _caretShowing = true;
			    drawInterior();
			} else
			    showCaret();
		    }
		}
	    } else if (keyevent.type == -11 && keyevent.isHomeKey()) {
		Range range = selectedRange();
		if (keyevent.isShiftKeyDown())
		    selectRange(_selectionAnchorChar, 0);
		else
		    selectRange(new Range(0, 0));
		int i = _scrollOffset;
		_computeScrollOffset();
		if (_scrollOffset != i)
		    drawInterior();
	    } else if (keyevent.type == -11 && keyevent.isEndKey()) {
		Range range = selectedRange();
		int i = _contents.length();
		if (keyevent.isShiftKeyDown())
		    selectRange(_selectionAnchorChar, i);
		else
		    selectRange(new Range(i, 0));
		int i_55_ = _scrollOffset;
		_computeScrollOffset();
		if (_scrollOffset != i_55_)
		    drawInterior();
	    } else if (keyevent.isBackspaceKey() || keyevent.isDeleteKey()
		       || keyevent.isPrintableKey()) {
		if (_oldContents == null)
		    _oldContents = new FastStringBuffer(_contents.toString());
		hideCaret();
		if (_selectionAnchorChar != _selectionEndChar) {
		    String string = _contents.toString();
		    int i = selectionStart();
		    _contents = new FastStringBuffer(string.substring(0, i));
		    _contents.append(string.substring(selectionStop()));
		    _selectionAnchorChar = _selectionEndChar = i;
		    if (keyevent.isBackspaceKey() || keyevent.isDeleteKey())
			keyevent = null;
		}
		if (keyevent != null) {
		    if (keyevent.isBackspaceKey()) {
			if (_contents.length() == 0
			    || _selectionAnchorChar == 0) {
			    showCaret();
			    return;
			}
			_contents.removeCharAt(_selectionAnchorChar - 1);
			_selectionAnchorChar--;
		    } else if (keyevent.isDeleteKey()) {
			if (_selectionAnchorChar < _contents.length())
			    _contents.removeCharAt(_selectionAnchorChar);
			else
			    showCaret();
		    } else if (keyevent.isExtendedKeyEvent())
			_contents.insert(keyevent.keyChar(),
					 _selectionAnchorChar++);
		    else
			_contents.insert((char) keyevent.key,
					 _selectionAnchorChar++);
		    _selectionEndChar = _selectionAnchorChar;
		}
		_computeScrollOffset();
		drawInterior();
		showCaret();
		if (_owner != null) {
		    _owner.textWasModified(this);
		    _textChanged = true;
		} else if (!_textChanged)
		    _textChanged = true;
	    }
	}
    }
    
    public void keyDown(KeyEvent keyevent) {
	if (isEditable()
	    && (!Application.application().handleExtendedKeyEvent()
		|| !JDK11AirLock.hasOneOneEvents()
		|| ((!keyevent.isPrintableKey() || keyevent.isDeleteKey())
		    && (keyevent.modifiers & 0x8) != 8
		    && !keyevent.isBackspaceKey()))) {
	    if (_filter != null) {
		if (_filter.acceptsEvent(this, keyevent, _keyVector))
		    _keyVector.addElement(keyevent);
	    } else
		_keyVector.addElement(keyevent);
	    while (!_keyVector.isEmpty()) {
		KeyEvent keyevent_56_
		    = (KeyEvent) _keyVector.removeFirstElement();
		_keyDown(keyevent_56_);
	    }
	}
    }
    
    public void keyTyped(KeyEvent keyevent) {
	if (isEditable()) {
	    if (_filter != null) {
		if (_filter.acceptsEvent(this, keyevent, _keyVector))
		    _keyVector.addElement(keyevent);
	    } else
		_keyVector.addElement(keyevent);
	    while (!_keyVector.isEmpty()) {
		KeyEvent keyevent_57_
		    = (KeyEvent) _keyVector.removeFirstElement();
		_keyDown(keyevent_57_);
	    }
	}
    }
    
    public void setFocusedView() {
	if (_editing == false && isEditable())
	    _startEditing(true);
	else
	    super.setFocusedView();
    }
    
    private void _startEditing(boolean bool) {
	if (_superview != null) {
	    if (isEditable())
		_canBlink = _caretShowing = hasInsertionPoint();
	    else
		_canBlink = _caretShowing = false;
	    _editing = true;
	    if (isSelectable())
		setFocusedView();
	    if (hasInsertionPoint() && isEditable())
		showCaret();
	    if (bool && _owner != null)
		_owner.textEditingDidBegin(this);
	}
    }
    
    private void _completeEditing() {
	_oldContents = null;
	boolean bool = _textChanged;
	_editing = false;
	if (_superview != null) {
	    _ignoreWillBecomeSelected = true;
	    _superview.setFocusedView(null);
	    _ignoreWillBecomeSelected = false;
	}
	if (bool)
	    sendContentsChangedCommand();
    }
    
    void _startBlinkTimer() {
	if (blinkTimer == null) {
	    blinkTimer = new Timer(this, "blinkCaret", 750);
	    blinkTimer.start();
	}
    }
    
    void _validateSelection() {
	String string = stringValue();
	if (_selectionAnchorChar == -1)
	    selectRange(new Range(0, 0));
	else {
	    if (_selectionAnchorChar < 0)
		_selectionAnchorChar = 0;
	    else if (_selectionAnchorChar > _contents.length())
		_selectionAnchorChar = _contents.length();
	    if (_selectionEndChar < 0)
		_selectionEndChar = 0;
	    else if (_selectionEndChar > _contents.length())
		_selectionEndChar = _contents.length();
	}
    }
    
    public void startFocus() {
	_validateSelection();
	hasFocus = true;
	if (isEditable())
	    _startBlinkTimer();
	this.setDirty(true);
    }
    
    public void stopFocus() {
	if (blinkTimer != null) {
	    blinkTimer.stop();
	    blinkTimer = null;
	}
	hasFocus = false;
	_scrollOffset = 0;
	if (_editing && _owner != null && isEditable())
	    _owner.textEditingWillEnd(this, 3, _textChanged);
	_caretShowing = _canBlink = false;
	if (_editing && isEditable()) {
	    if (_owner != null)
		_owner.textEditingDidEnd(this, 3, _textChanged);
	    if (_textChanged)
		sendCommitCommand(false);
	}
	_editing = _textChanged = false;
	drawInterior();
    }
    
    public void pauseFocus() {
	if (blinkTimer != null) {
	    blinkTimer.stop();
	    blinkTimer = null;
	    hideCaret();
	}
    }
    
    public void resumeFocus() {
	if (isEditable())
	    _startBlinkTimer();
    }
    
    public void cancelEditing() {
	if (isBeingEdited()) {
	    if (_oldContents != null) {
		_contents = _oldContents;
		_oldContents = null;
	    }
	    _editing = false;
	    if (_superview != null) {
		_ignoreWillBecomeSelected = true;
		_superview.setFocusedView(null);
		_ignoreWillBecomeSelected = false;
	    }
	}
    }
    
    public void completeEditing() {
	boolean bool = _textChanged;
	if (isBeingEdited()
	    && (_owner == null || _owner.textEditingWillEnd(this, 4, bool))) {
	    _completeEditing();
	    if (_owner != null)
		_owner.textEditingDidEnd(this, 4, bool);
	}
    }
    
    void sendCommand(String string, Target target) {
	if (target != null)
	    target.performCommand(string, this);
    }
    
    void sendTabCommand() {
	if (_tabCommand != null && _tabTarget != null)
	    sendCommand(_tabCommand, _tabTarget);
	else if (this.rootView() != null)
	    this.rootView().selectViewAfter(this);
    }
    
    void sendBacktabCommand() {
	if (_backtabCommand != null && _backtabCommand != null)
	    sendCommand(_backtabCommand, _backtabTarget);
	else if (this.rootView() != null)
	    this.rootView().selectViewBefore(this);
    }
    
    void sendContentsChangedCommand() {
	sendCommand(_contentsChangedCommand, _contentsChangedTarget);
    }
    
    void sendCommitCommand(boolean bool) {
	if (bool && _commitCommand == null && _commitTarget == null
	    && _tabCommand != null && _tabTarget != null)
	    sendTabCommand();
	sendCommand(_commitCommand, _commitTarget);
    }
    
    public boolean canPerformCommand(String string) {
	return ("blinkCaret".equals(string) || "selectText".equals(string)
		|| isEditable() && "cut".equals(string)
		|| "copy".equals(string)
		|| isEditable() && "paste".equals(string));
    }
    
    public void performCommand(String string, Object object) {
	if ("blinkCaret".equals(string))
	    blinkCaret();
	else if ("selectText".equals(string))
	    selectText();
	else if ("cut".equals(string))
	    cut();
	else if ("copy".equals(string))
	    copy();
	else if ("paste".equals(string))
	    paste();
	else
	    throw new NoSuchMethodError("unknown command: " + string);
    }
    
    private void blinkCaret() {
	if (_canBlink) {
	    _caretShowing = _caretShowing ^ true;
	    drawCaret();
	} else if (!_mouseDragging && hasInsertionPoint())
	    _canBlink = true;
    }
    
    boolean isWordCharacter(char c) {
	if (c >= '0' && c <= '9' || c >= 'A' && c <= 'Z'
	    || c >= 'a' && c <= 'z')
	    return true;
	return false;
    }
    
    Range groupForIndex(int i) {
	int i_58_ = charCount();
	if (i_58_ == 0)
	    return new Range();
	if (_drawableCharacter != ANY_CHARACTER)
	    return new Range(0, charCount());
	if (i < 0)
	    i = 0;
	else if (i >= i_58_)
	    i = i_58_ - 1;
	int i_59_ = i;
	char c = _contents.charAt(i_59_);
	if (c == '\n')
	    return new Range(i_59_, 1);
	if (c == ' ' || c == '\t') {
	    for (/**/; i_59_ > 0; i_59_--) {
		c = _contents.charAt(i_59_);
		if (c != ' ' && c != '\t')
		    break;
	    }
	    int i_60_ = i_59_ + 1;
	    for (i_59_ = i; i_59_ < i_58_; i_59_++) {
		c = _contents.charAt(i_59_);
		if (c != ' ' && c != '\t')
		    break;
	    }
	    int i_61_ = i_59_ - 1;
	    return new Range(i_60_, i_61_ - i_60_ + 1);
	}
	if (!isWordCharacter(c))
	    return new Range(i, 1);
	int i_62_;
	for (i_62_ = i_59_; i_62_ > 0; i_62_--) {
	    c = _contents.charAt(i_62_ - 1);
	    if (!isWordCharacter(c))
		break;
	}
	int i_63_;
	for (i_63_ = i_59_; i_63_ < i_58_ - 1; i_63_++) {
	    c = _contents.charAt(i_63_ + 1);
	    if (!isWordCharacter(c))
		break;
	}
	return new Range(i_62_, i_63_ - i_62_ + 1);
    }
    
    private String drawableString() {
	if (_drawableCharacter == ANY_CHARACTER) {
	    if (_contents != null)
		return _contents.toString();
	    return "";
	}
	if (_contents != null && _contents.length() > 0) {
	    char[] cs = new char[_contents.length()];
	    int i = 0;
	    for (int i_64_ = cs.length; i < i_64_; i++)
		cs[i] = _drawableCharacter;
	    return new String(cs);
	}
	return "";
    }
    
    public void willBecomeSelected() {
	if (!_ignoreWillBecomeSelected)
	    selectText();
    }
    
    public boolean canBecomeSelectedView() {
	if (isEditable())
	    return true;
	return false;
    }
    
    public View nextSelectableView() {
	if (_tabTarget != null && _tabTarget instanceof View)
	    return (View) _tabTarget;
	return null;
    }
    
    public View previousSelectableView() {
	if (_backtabTarget != null && _backtabTarget instanceof View)
	    return (View) _backtabTarget;
	return null;
    }
    
    public void copy() {
	Application.setClipboardText(stringForRange(selectedRange()));
    }
    
    public void cut() {
	if (isEditable()) {
	    Range range = selectedRange();
	    if (range != null && range.index >= 0) {
		Application.setClipboardText(stringForRange(range));
		replaceRangeWithString(range, "");
		selectRange(new Range(range.index(), 0));
	    }
	}
    }
    
    public void paste() {
	if (isEditable()) {
	    Range range = selectedRange();
	    String string = Application.clipboardText();
	    if (range != null && string != null) {
		replaceRangeWithString(range, string);
		selectRange(new Range(range.index() + string.length(), 0));
	    }
	}
    }
    
    public boolean isScrollable() {
	return isScrollable;
    }
    
    public void setScrollable(boolean bool) {
	isScrollable = bool;
    }
    
    public String formElementText() {
	return stringValue();
    }
}
