/* TextView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class TextView extends View
    implements ExtendedTarget, EventFilter, DragDestination, FormElement
{
    public static final String TEXT_ATTACHMENT_STRING = "@";
    public static final String PARAGRAPH_FORMAT_KEY = "ParagraphFormatKey";
    public static final String FONT_KEY = "FontKey";
    public static final String TEXT_COLOR_KEY = "TextColorKey";
    public static final String TEXT_ATTACHMENT_KEY = "TextAttachmentKey";
    public static final String TEXT_ATTACHMENT_BASELINE_OFFSET_KEY
	= "TextAttachmentBaselineOffsetKey";
    public static final String CARET_COLOR_KEY = "CaretColorKey";
    public static final String LINK_KEY = "LinkKey";
    public static final String LINK_DESTINATION_KEY = "LinkDestinationKey";
    public static final String LINK_COLOR_KEY = "LinkColorKey";
    public static final String PRESSED_LINK_COLOR_KEY = "PressedLinkColorKey";
    static final String LINK_IS_PRESSED_KEY = "_IFCLinkPressedKey";
    static Vector attributesChangingFormatting = new Vector();
    Vector _paragraphVector;
    Color _backgroundColor;
    Color _selectionColor;
    TextParagraph _updateParagraph;
    TextPositionInfo _anchorInfo;
    TextPositionInfo _upInfo;
    TextSelection _selection;
    TextFilter _filter;
    TextViewOwner _owner;
    Hashtable _defaultAttributes;
    Hashtable _typingAttributes;
    Timer _updateTimer;
    Vector _eventVector;
    int _charCount;
    int _paragraphSpacing;
    int _updateLine;
    int _downY;
    int _clickCount;
    int _resizeDisabled;
    int _formattingDisabled;
    boolean _drawText = true;
    boolean _editing;
    boolean _useSingleFont = false;
    boolean _editable = true;
    boolean _selectable = true;
    boolean _drawNextParagraph;
    boolean _resizing = false;
    boolean insertionPointVisible = false;
    boolean transparent = false;
    boolean selectLineBreak;
    private Range _selectedRange;
    private Range _wasSelectedRange;
    private TextAttachment _mouseDownTextAttachment;
    private Point _mouseDownTextAttachmentOrigin;
    private FontMetrics _defaultFontMetricsCache;
    private URL _baseURL;
    private Range _clickedRange;
    private Range _firstRange;
    private HTMLParsingRules _htmlParsingRules;
    private int notifyAttachmentDisabled = 0;
    private Range invalidAttachmentRange = null;
    private static Vector _rectCache;
    private static Vector _vectorCache;
    private static boolean _shouldCache;
    private static boolean _cacheVectors;
    static ObjectPool hashtablePool;
    static ObjectPool rangePool;
    
    static {
	attributesChangingFormatting.addElement("TextAttachmentKey");
	attributesChangingFormatting
	    .addElement("TextAttachmentBaselineOffsetKey");
	attributesChangingFormatting.addElement("FontKey");
	attributesChangingFormatting.addElement("ParagraphFormatKey");
	_rectCache = new Vector();
	_vectorCache = new Vector();
	_shouldCache = false;
	_cacheVectors = false;
	hashtablePool
	    = new ObjectPool("COM.stagecast.ifc.netscape.util.Hashtable", 32);
	rangePool
	    = new ObjectPool("COM.stagecast.ifc.netscape.application.Range",
			     32);
    }
    
    public TextView() {
	this(0, 0, 0, 0);
    }
    
    public TextView(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public TextView(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	_eventVector = new Vector();
	_selection = new TextSelection(this);
	_paragraphVector = new Vector();
	_paragraphSpacing = 0;
	_backgroundColor = Color.white;
	_selectionColor = Color.lightGray;
	_defaultAttributes = new Hashtable();
	_defaultAttributes.put("FontKey", Font.defaultFont());
	_defaultAttributes.put("TextColorKey", Color.black);
	_defaultAttributes.put("LinkColorKey", Color.blue);
	_defaultAttributes.put("CaretColorKey", Color.black);
	_defaultAttributes.put("PressedLinkColorKey", Color.red);
	TextParagraphFormat textparagraphformat = new TextParagraphFormat();
	textparagraphformat.setLeftMargin(3);
	textparagraphformat.setRightMargin(3);
	textparagraphformat.setJustification(0);
	int i_3_ = 30;
	for (int i_4_ = 0; i_4_ < 20; i_4_++) {
	    textparagraphformat.addTabPosition(i_3_);
	    i_3_ += 30;
	}
	_defaultAttributes.put("ParagraphFormatKey", textparagraphformat);
	_wasSelectedRange = new Range(selectedRange());
	TextParagraph textparagraph = new TextParagraph(this);
	textparagraph.addRun(new TextStyleRun(textparagraph, "", null));
	addParagraph(textparagraph);
	reformatAll();
	_typingAttributes = new Hashtable();
    }
    
    public void didMoveBy(int i, int i_5_) {
	if (i == 0 && i_5_ == 0 && _updateTimer != null) {
	    _updateTimer.stop();
	    _updateTimer = null;
	    _updateParagraph = null;
	}
	super.didMoveBy(i, i_5_);
    }
    
    public void sizeBy(int i, int i_6_) {
	if (isResizingEnabled()) {
	    int i_7_ = bounds.width;
	    _resizing = true;
	    super.sizeBy(i, i_6_);
	    _resizing = false;
	    if (bounds.width != i_7_ + i) {
		disableResizing();
		reformatAll();
		enableResizing();
		this.setDirty(true);
	    } else if (i != 0 || i_6_ != 0)
		this.setDirty(true);
	}
    }
    
    public void didSizeBy(int i, int i_8_) {
	if (!_resizing)
	    reformatAll();
	super.didSizeBy(i, i_8_);
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public boolean wantsAutoscrollEvents() {
	return true;
    }
    
    public void drawView(Graphics graphics) {
	if (!_drawText) {
	    if (_updateParagraph != null)
		_updateParagraph.drawLine(graphics, _updateLine);
	} else {
	    int i = _paragraphVector.count();
	    int i_9_ = graphics.clipRect().y;
	    int i_10_ = graphics.clipRect().maxY();
	    Rect rect = Rect.newRect(0, 0, this.width(), this.height());
	    for (int i_11_ = 0; i_11_ < i; i_11_++) {
		TextParagraph textparagraph
		    = (TextParagraph) _paragraphVector.elementAt(i_11_);
		if (textparagraph._y <= i_10_
		    && textparagraph._y + textparagraph._height >= i_9_)
		    textparagraph.drawView(graphics, rect);
	    }
	    Rect.returnRect(rect);
	}
	if (_selection._insertionPointShowing) {
	    Rect rect = _selection.insertionPointRect();
	    Color color = null;
	    if (graphics.clipRect().intersects(rect)) {
		TextPositionInfo textpositioninfo
		    = _selection.insertionPointInfo();
		TextStyleRun textstylerun
		    = _runForIndex(textpositioninfo._absPosition);
		Hashtable hashtable;
		if ((hashtable = textstylerun.attributes()) != null)
		    color = (Color) hashtable.get("CaretColorKey");
		if (color == null)
		    color = (Color) _defaultAttributes.get("CaretColorKey");
		if (color == null)
		    color = Color.black;
		graphics.setColor(color);
		graphics.fillRect(rect);
	    }
	}
    }
    
    public Object filterEvents(Vector vector) {
	int i;
	if (Application.application().handleExtendedKeyEvent()
	    && JDK11AirLock.hasOneOneEvents())
	    i = -13;
	else
	    i = -11;
	for (int i_12_ = 0; i_12_ < vector.count(); i_12_++) {
	    Event event = (Event) vector.elementAt(i_12_);
	    if (event instanceof KeyEvent && event.type() == i) {
		if (_filter != null) {
		    if (_filter.acceptsEvent(this, (KeyEvent) event,
					     _eventVector))
			_eventVector.addElement(event);
		} else
		    _eventVector.addElement(event);
		vector.removeElementAt(i_12_);
		i_12_--;
	    }
	}
	return null;
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	_mouseDownTextAttachment = null;
	_clickedRange = null;
	if (isEditable() || isSelectable())
	    this.setFocusedView();
	if (!this.rootView().mouseStillDown()) {
	    this.rootView().adjustForExpectedMouseDownCount();
	    if (!this.rootView().mouseStillDown())
		return true;
	}
	_selection.hideInsertionPoint();
	_clickCount = mouseevent.clickCount();
	_anchorInfo = positionForPoint(mouseevent.x, mouseevent.y, false);
	TextPositionInfo textpositioninfo
	    = positionForPoint(mouseevent.x, mouseevent.y, true);
	if (_anchorInfo != null && _anchorInfo._endOfLine) {
	    Rect rect = new Rect(_anchorInfo._x, _anchorInfo._y, bounds.width,
				 _anchorInfo._lineHeight);
	    if (!rect.contains(mouseevent.x, mouseevent.y))
		selectLineBreak = false;
	    else
		selectLineBreak = true;
	} else
	    selectLineBreak = false;
	if (textpositioninfo != null) {
	    TextStyleRun textstylerun
		= _runForIndex(textpositioninfo._absPosition);
	    if (textstylerun != null) {
		Hashtable hashtable = textstylerun.attributes();
		if (hashtable != null) {
		    TextAttachment textattachment;
		    if ((textattachment
			 = ((TextAttachment)
			    hashtable.get("TextAttachmentKey"))) != null
			&& (textstylerun.rangeIndex()
			    == textpositioninfo._absPosition)) {
			Rect rect = (textstylerun.textAttachmentBoundsForOrigin
				     (textpositioninfo._x, textpositioninfo._y,
				      (textstylerun._paragraph._baselines
				       [textpositioninfo._lineNumber])));
			if (rect != null
			    && rect.contains(mouseevent.x, mouseevent.y)) {
			    boolean bool
				= (textattachment.mouseDown
				   (new MouseEvent(mouseevent.timeStamp,
						   mouseevent.type,
						   mouseevent.x - rect.x,
						   mouseevent.y - rect.y,
						   mouseevent.modifiers)));
			    if (bool) {
				_mouseDownTextAttachment = textattachment;
				_mouseDownTextAttachmentOrigin
				    = new Point(rect.x, rect.y);
				return true;
			    }
			}
		    }
		    if (!isEditable() && hashtable.get("LinkKey") != null
			&& runUnderMouse(textstylerun, mouseevent.x,
					 mouseevent.y)
			&& _clickCount == 1) {
			_clickedRange = linkRangeForPosition(textpositioninfo
							     ._absPosition);
			highlightLinkWithRange(_clickedRange, true);
		    }
		}
	    }
	}
	if (!isSelectable() && _clickedRange == null)
	    return false;
	_firstRange = null;
	if (_clickCount > 1) {
	    if (!selectLineBreak) {
		switch (_clickCount) {
		case 2:
		    _firstRange = groupForIndex(_anchorInfo._absPosition);
		    break;
		default:
		    _firstRange = paragraphForIndex(_anchorInfo._absPosition);
		}
		if (_firstRange != null && !_firstRange.isNullRange()) {
		    if (mouseevent.isShiftKeyDown()) {
			Range range = new Range(selectedRange());
			range.unionWith(_firstRange.index, _firstRange.length);
			_selection.setRange(range.index,
					    range.index + range.length, null,
					    false);
		    } else
			_selection.setRange(_firstRange.index,
					    _firstRange.lastIndex() + 1, null,
					    false);
		    _selectionChanged();
		}
		return true;
	    }
	    _firstRange = new Range(_anchorInfo._absPosition, 0);
	}
	if (mouseevent.isShiftKeyDown())
	    _selection.setRange(_selection.orderedSelectionStart(),
				_anchorInfo._absPosition, null, false);
	else
	    _selection.setInsertionPoint(_anchorInfo);
	_selectionChanged();
	_upInfo = null;
	_downY = _anchorInfo._y + _anchorInfo._lineHeight;
	return true;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	if (_mouseDownTextAttachment != null)
	    _mouseDownTextAttachment.mouseDragged
		(new MouseEvent(mouseevent.timeStamp, mouseevent.type,
				(mouseevent.x
				 - _mouseDownTextAttachmentOrigin.x),
				(mouseevent.y
				 - _mouseDownTextAttachmentOrigin.y),
				mouseevent.modifiers));
	else {
	    Point point = new Point(mouseevent.x, mouseevent.y);
	    if (point.x >= bounds.width)
		point.x = bounds.width - 1;
	    else if (point.x < 0)
		point.x = 0;
	    if (point.y >= bounds.height)
		point.y = bounds.height - 1;
	    else if (point.y < 0)
		point.y = 0;
	    TextPositionInfo textpositioninfo
		= positionForPoint(point.x, point.y, false);
	    if (_clickedRange != null) {
		TextStyleRun textstylerun
		    = _runForIndex(textpositioninfo._absPosition);
		Hashtable hashtable;
		if ((hashtable = textstylerun.attributes()) != null
		    && hashtable.get("LinkKey") != null
		    && runUnderMouse(textstylerun, mouseevent.x,
				     mouseevent.y)) {
		    Range range
			= linkRangeForPosition(textpositioninfo._absPosition);
		    if (!range.equals(_clickedRange)) {
			highlightLinkWithRange(_clickedRange, false);
			_clickedRange = range;
			highlightLinkWithRange(_clickedRange, true);
		    }
		    return;
		}
		highlightLinkWithRange(_clickedRange, false);
		_clickedRange = null;
	    }
	    if (isSelectable() && textpositioninfo != null) {
		if (!this.containsPointInVisibleRect(mouseevent.x,
						     mouseevent.y)) {
		    Rect rect
			= newRect(textpositioninfo._x, textpositioninfo._y, 1,
				  textpositioninfo._lineHeight);
		    this.scrollRectToVisible(rect);
		    returnRect(rect);
		}
		int i = _anchorInfo._absPosition;
		boolean bool;
		if (_upInfo != null
		    && textpositioninfo._absPosition != _upInfo._absPosition)
		    bool = true;
		else
		    bool = false;
		_upInfo = textpositioninfo;
		if (bool) {
		    Range range;
		    switch (_clickCount) {
		    case 0:
		    case 1:
			_selection.setRange(i, _upInfo._absPosition, _upInfo,
					    selectLineBreak);
			_selectionChanged();
			return;
		    case 2:
			range = groupForIndex(_upInfo._absPosition);
			break;
		    default:
			range = paragraphForIndex(_upInfo._absPosition);
		    }
		    if (_firstRange != null && !_firstRange.isNullRange()
			&& !range.isNullRange()) {
			range.unionWith(_firstRange);
			if (!range.equals(selectedRange())) {
			    _selection.setRange(range.index,
						range.lastIndex() + 1, null,
						selectLineBreak);
			    _selectionChanged();
			}
		    }
		}
	    }
	}
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	if (_mouseDownTextAttachment != null) {
	    _mouseDownTextAttachment.mouseUp
		(new MouseEvent(mouseevent.timeStamp, mouseevent.type,
				(mouseevent.x
				 - _mouseDownTextAttachmentOrigin.x),
				(mouseevent.y
				 - _mouseDownTextAttachmentOrigin.y),
				mouseevent.modifiers));
	    _mouseDownTextAttachment = null;
	    _mouseDownTextAttachmentOrigin = null;
	} else {
	    if (_clickedRange != null) {
		TextPositionInfo textpositioninfo
		    = positionForPoint(mouseevent.x, mouseevent.y, true);
		Range range
		    = linkRangeForPosition(textpositioninfo._absPosition);
		highlightLinkWithRange(_clickedRange, false);
		if (range != null && range.equals(_clickedRange)
		    && _owner != null) {
		    TextStyleRun textstylerun
			= _runForIndex(_clickedRange.index);
		    Hashtable hashtable = textstylerun.attributes();
		    Object object = null;
		    String string;
		    if (hashtable != null
			&& (string = (String) hashtable.get("LinkKey")) != null
			&& runsUnderMouse(runsForRange(_clickedRange),
					  mouseevent.x, mouseevent.y))
			_owner.linkWasSelected(this, _clickedRange, string);
		}
		_clickedRange = null;
	    }
	    if (isSelectable()) {
		if (_upInfo == null
		    || _upInfo._absPosition == _anchorInfo._absPosition)
		    _selection.showInsertionPoint();
		_firstRange = null;
	    }
	}
    }
    
    public int cursorForPoint(int i, int i_13_) {
	if (isEditable())
	    return 2;
	TextPositionInfo textpositioninfo = positionForPoint(i, i_13_, true);
	if (textpositioninfo != null) {
	    TextStyleRun textstylerun
		= _runForIndex(textpositioninfo._absPosition);
	    if (textstylerun != null) {
		Hashtable hashtable = textstylerun.attributes();
		if (hashtable != null && hashtable.get("LinkKey") != null
		    && runUnderMouse(textstylerun, i, i_13_))
		    return 12;
	    }
	}
	if (isSelectable())
	    return 2;
	return 0;
    }
    
    public void performCommand(String string, Object object) {
	if (string.equals("refreshBitmap"))
	    refreshBitmap(object);
	else {
	    if (string != null && string.equals("setFont")) {
		processSetFont((Font) object);
		return;
	    }
	    if (string.equals("cut"))
		cut();
	    else if (string.equals("copy"))
		copy();
	    else if (string.equals("paste"))
		paste();
	    else if (!(object instanceof Timer))
		return;
	}
	if (_updateParagraph == null) {
	    if (_updateTimer != null) {
		_updateTimer.stop();
		_updateTimer = null;
	    }
	} else {
	    _drawText = false;
	    Rect rect = _updateParagraph.rectForLine(_updateLine);
	    this.draw(rect);
	    returnRect(rect);
	    _drawText = true;
	    _updateLine++;
	    if (_updateLine >= _updateParagraph._breakCount) {
		if (!_drawNextParagraph)
		    _updateParagraph = null;
		else {
		    int i
			= (_paragraphVector.indexOfIdentical(_updateParagraph)
			   + 1);
		    if (i == 0 || i >= _paragraphVector.count())
			_updateParagraph = null;
		    else {
			_updateParagraph
			    = (TextParagraph) _paragraphVector.elementAt(i);
			_updateLine = 0;
		    }
		}
	    }
	    if (_updateParagraph == null && _updateTimer != null) {
		_updateTimer.stop();
		_updateTimer = null;
	    }
	}
    }
    
    public boolean canPerformCommand(String string) {
	if (string.equals("setFont")) {
	    if (usesSingleFont() || !isEditable())
		return false;
	    return true;
	}
	if (string.equals("refreshBitmap") || string.equals("copy")
	    || isEditable() && string.equals("cut")
	    || isEditable() && string.equals("paste"))
	    return true;
	return false;
    }
    
    public void keyDown(KeyEvent keyevent) {
	if (!Application.application().handleExtendedKeyEvent()
	    || !JDK11AirLock.hasOneOneEvents()
	    || ((!keyevent.isPrintableKey() || keyevent.isDeleteKey())
		&& (keyevent.modifiers & 0x8) != 8
		&& !keyevent.isBackspaceKey() && !keyevent.isReturnKey()))
	    processKey(keyevent);
    }
    
    public void keyTyped(KeyEvent keyevent) {
	processKey(keyevent);
    }
    
    void processKey(KeyEvent keyevent) {
	if (keyevent.isPageUpKey()) {
	    Rect rect = new Rect();
	    this.computeVisibleRect(rect);
	    rect.y -= rect.height - 1;
	    if (rect.y < 0)
		rect.y = 0;
	    TextPositionInfo textpositioninfo
		= positionForPoint(rect.x, rect.y, true);
	    if (textpositioninfo != null)
		rect.y = textpositioninfo._y;
	    this.scrollRectToVisible(rect);
	} else if (keyevent.isPageDownKey()) {
	    Rect rect = new Rect();
	    this.computeVisibleRect(rect);
	    TextPositionInfo textpositioninfo
		= positionForPoint(rect.x, rect.y, true);
	    rect.y += rect.height - 1;
	    if (rect.y > bounds.height - rect.height)
		rect.y = bounds.height - rect.height;
	    TextPositionInfo textpositioninfo_14_
		= positionForPoint(rect.x, rect.y, true);
	    if (textpositioninfo_14_ != null) {
		rect.y = textpositioninfo_14_._y;
		if (textpositioninfo != null
		    && (textpositioninfo._absPosition
			== textpositioninfo_14_._absPosition))
		    rect.y += textpositioninfo_14_._lineHeight;
	    }
	    this.scrollRectToVisible(rect);
	} else if (isEditable() && hasSelection()) {
	    if (_filter != null) {
		if (_filter.acceptsEvent(this, keyevent, _eventVector))
		    _eventVector.addElement(keyevent);
	    } else
		_eventVector.addElement(keyevent);
	    this.application().eventLoop().filterEvents(this);
	    while (!_eventVector.isEmpty())
		_keyDown();
	}
    }
    
    public DragDestination acceptsDrag(DragSession dragsession, int i,
				       int i_15_) {
	String string = dragsession.dataType();
	if (isEditable() && hasSelection()
	    && ("COM.stagecast.ifc.netscape.application.Color".equals(string)
		|| "COM.stagecast.ifc.netscape.application.Image"
		       .equals(string)))
	    return this;
	return null;
    }
    
    public boolean dragEntered(DragSession dragsession) {
	return true;
    }
    
    public boolean dragMoved(DragSession dragsession) {
	return true;
    }
    
    public void dragExited(DragSession dragsession) {
	/* empty */
    }
    
    public boolean dragDropped(DragSession dragsession) {
	if (!isEditable() || !hasSelection())
	    return false;
	Object object = dragsession.data();
	if (object == null)
	    return false;
	if (object instanceof Color) {
	    Range range = selectedRange();
	    if (range.length > 0)
		addAttributeForRange("TextColorKey", object, range);
	    else
		addTypingAttribute("TextColorKey", object);
	    return true;
	}
	if (object instanceof Image) {
	    replaceRangeWithTextAttachment(selectedRange(),
					   new ImageAttachment((Image)
							       object));
	    return true;
	}
	return false;
    }
    
    public void startFocus() {
	_setEditing(true);
	showInsertionPoint();
	_selection._startFlashing();
	if (isEditable() && _owner != null)
	    _owner.textEditingDidBegin(this);
	if (hasSelection()) {
	    Range range = selectedRange();
	    if (range.length > 0)
		dirtyRange(range);
	} else
	    selectRange(new Range(0, 0));
    }
    
    public void stopFocus() {
	_selection._stopFlashing();
	hideInsertionPoint();
	_setEditing(false);
	if (isEditable() && _owner != null)
	    _owner.textEditingDidEnd(this);
	if (hasSelection()) {
	    Range range = selectedRange();
	    if (range.length > 0)
		dirtyRange(range);
	}
    }
    
    public void pauseFocus() {
	_selection._stopFlashing();
	hideInsertionPoint();
    }
    
    public void resumeFocus() {
	showInsertionPoint();
	_selection._startFlashing();
    }
    
    public String toString() {
	StringBuffer stringbuffer = new StringBuffer();
	int i = 0;
	for (int i_16_ = _paragraphVector.count(); i < i_16_; i++)
	    stringbuffer.append(_paragraphVector.elementAt(i).toString());
	return stringbuffer.toString();
    }
    
    public void setFilter(TextFilter textfilter) {
	_filter = textfilter;
    }
    
    public TextFilter filter() {
	return _filter;
    }
    
    public void setOwner(TextViewOwner textviewowner) {
	_owner = textviewowner;
    }
    
    public TextViewOwner owner() {
	return _owner;
    }
    
    public void disableResizing() {
	_resizeDisabled++;
    }
    
    public void enableResizing() {
	_resizeDisabled--;
	if (_resizeDisabled < 0)
	    _resizeDisabled = 0;
    }
    
    public boolean isResizingEnabled() {
	return _resizeDisabled == 0;
    }
    
    public void sizeToMinSize() {
	sizeBy(0, adjustCharCountsAndSpacing() - bounds.height);
    }
    
    public void scrollRangeToVisible(Range range) {
	if (range.index == 0 && range.length == 0)
	    this.scrollRectToVisible(new Rect(0, 0, 1, 1));
	else if (range.index >= length())
	    this.scrollRectToVisible(new Rect(this.width() - 1,
					      this.height() - 1, 1, 1));
	else if (range.length == 0) {
	    TextPositionInfo textpositioninfo
		= positionInfoForIndex(range.index);
	    this.scrollRectToVisible(new Rect(textpositioninfo._x,
					      (textpositioninfo._y
					       + textpositioninfo._lineHeight),
					      1,
					      textpositioninfo._lineHeight));
	} else {
	    TextPositionInfo textpositioninfo
		= positionInfoForIndex(range.index);
	    TextPositionInfo textpositioninfo_17_
		= positionInfoForIndex(range.index + range.length);
	    int i = textpositioninfo._y;
	    int i_18_
		= textpositioninfo_17_._y + textpositioninfo_17_._lineHeight;
	    if (textpositioninfo._y == textpositioninfo_17_._y)
		this.scrollRectToVisible(new Rect(textpositioninfo._x, i,
						  (textpositioninfo_17_._x
						   - textpositioninfo._x),
						  i_18_ - i));
	    else
		this.scrollRectToVisible(new Rect(0, i, this.width(),
						  i_18_ - i));
	}
    }
    
    public void setUseSingleFont(boolean bool) {
	_useSingleFont = bool;
    }
    
    public boolean usesSingleFont() {
	return _useSingleFont;
    }
    
    public void setDefaultAttributes(Hashtable hashtable) {
	_defaultAttributes = hashtable;
	if (hashtable.get("FontKey") != null)
	    _defaultFontMetricsCache = null;
	if (hashtable.get("ParagraphFormatKey") == null) {
	    TextParagraphFormat textparagraphformat
		= new TextParagraphFormat();
	    textparagraphformat.setLeftMargin(3);
	    textparagraphformat.setRightMargin(3);
	    textparagraphformat.setJustification(0);
	    _defaultAttributes.put("ParagraphFormatKey", textparagraphformat);
	}
	reformatAll();
	Range range = allocateRange(0, length());
	dirtyRange(range);
	recycleRange(range);
    }
    
    public Hashtable defaultAttributes() {
	return _defaultAttributes;
    }
    
    public void addTypingAttribute(String string, Object object) {
	Hashtable hashtable = new Hashtable();
	hashtable.put(string, object);
	hashtable = attributesByRemovingStaticAttributes(hashtable);
	Enumeration enumeration = hashtable.keys();
	while (enumeration.hasMoreElements()) {
	    String string_19_ = (String) enumeration.nextElement();
	    _typingAttributes.put(string_19_, hashtable.get(string_19_));
	}
    }
    
    public Hashtable typingAttributes() {
	return _typingAttributes;
    }
    
    public void setTypingAttributes(Hashtable hashtable) {
	if (hashtable == null)
	    _typingAttributes = new Hashtable();
	else
	    _typingAttributes
		= attributesByRemovingStaticAttributes(hashtable);
    }
    
    public void setBackgroundColor(Color color) {
	if (color != null)
	    _backgroundColor = color;
    }
    
    public Color backgroundColor() {
	return _backgroundColor;
    }
    
    public void setSelectionColor(Color color) {
	if (color != null)
	    _selectionColor = color;
    }
    
    public Color selectionColor() {
	return _selectionColor;
    }
    
    public void setEditable(boolean bool) {
	if (_editable != bool) {
	    _editable = bool;
	    setSelectable(true);
	}
    }
    
    public boolean isEditable() {
	return _editable;
    }
    
    public void setSelectable(boolean bool) {
	if (_selectable != bool) {
	    _selectable = bool;
	    RootView rootview = this.rootView();
	    if (rootview != null)
		rootview.updateCursor();
	}
    }
    
    public boolean isSelectable() {
	return _selectable;
    }
    
    public void setFont(Font font) {
	if (font != null)
	    addDefaultAttribute("FontKey", font);
    }
    
    public Font font() {
	return (Font) _defaultAttributes.get("FontKey");
    }
    
    public void setTextColor(Color color) {
	if (color != null)
	    addDefaultAttribute("TextColorKey", color);
    }
    
    public Color textColor() {
	Color color = (Color) _defaultAttributes.get("TextColorKey");
	if (color == null)
	    return Color.black;
	return color;
    }
    
    public void setCaretColor(Color color) {
	if (color != null)
	    addDefaultAttribute("CaretColorKey", color);
    }
    
    public Color caretColor() {
	Color color = (Color) _defaultAttributes.get("CaretColorKey");
	if (color == null)
	    return Color.black;
	return color;
    }
    
    public void replaceRangeWithString(Range range, String string) {
	boolean bool = true;
	if (_owner != null)
	    _owner.textWillChange(this, range);
	if (range.equals(new Range(0, length()))) {
	    replaceContentWithString(string);
	    if (_owner != null)
		_owner.textDidChange(this, new Range(0, length()));
	} else {
	    disableResizing();
	    int i = _paragraphIndexForIndex(range.index);
	    deleteRange(range, null);
	    if (string == null || string.equals("")) {
		enableResizing();
		if (i > 0)
		    sizeBy(0,
			   adjustCharCountsAndSpacing(i - 1) - bounds.height);
		else
		    sizeToMinSize();
		if (_owner != null)
		    _owner.textDidChange(this, new Range(range.index, 0));
	    } else {
		disableAttachmentNotification();
		int i_20_ = range.index;
		int i_21_ = 0;
		int i_22_ = string.indexOf('\n');
		if (i_22_ == -1) {
		    insertString(string, i_20_);
		    enableResizing();
		    if (i > 0)
			sizeBy(0, (adjustCharCountsAndSpacing(i - 1)
				   - bounds.height));
		    else
			sizeToMinSize();
		    enableAttachmentNotification();
		    if (_owner != null)
			_owner.textDidChange(this, new Range(range.index,
							     string.length()));
		} else {
		    insertString(string.substring(i_21_, i_22_), i_20_, true);
		    insertReturn(i_20_ + (i_22_ - i_21_));
		    i_20_ += i_22_ - i_21_ + 1;
		    int i_23_ = string.length() - 1;
		    while (i_22_ < i_23_) {
			i_21_ = i_22_ + 1;
			i_22_ = string.indexOf('\n', i_21_);
			if (i_22_ == -1) {
			    i_22_ = i_23_ + 1;
			    bool = false;
			}
			if (i_22_ > i_21_) {
			    String string_24_ = string.substring(i_21_, i_22_);
			    insertString(string_24_, i_20_, true);
			    if (bool)
				insertReturn(i_20_ + (i_22_ - i_21_));
			    i_20_ += i_22_ - i_21_ + 1;
			} else {
			    insertReturn(i_20_);
			    i_20_++;
			}
		    }
		    enableResizing();
		    if (i > 0)
			sizeBy(0, (adjustCharCountsAndSpacing(i - 1)
				   - bounds.height));
		    else
			sizeToMinSize();
		    enableAttachmentNotification();
		    if (_owner != null)
			_owner.textDidChange(this, new Range(range.index,
							     string.length()));
		}
	    }
	}
    }
    
    public String stringForRange(Range range) {
	TextParagraph textparagraph = _paragraphForIndex(range.index);
	TextParagraph textparagraph_25_
	    = _paragraphForIndex(range.index + range.length);
	if (textparagraph == null)
	    return null;
	if (textparagraph_25_ == null)
	    textparagraph_25_ = lastParagraph();
	if (textparagraph == textparagraph_25_)
	    return textparagraph.stringForRange(range);
	StringBuffer stringbuffer = new StringBuffer();
	Range range_26_ = allocateRange();
	int i = _paragraphVector.indexOfIdentical(textparagraph);
	for (int i_27_ = _paragraphVector.indexOfIdentical(textparagraph_25_);
	     i <= i_27_; i++) {
	    range_26_.index = range.index;
	    range_26_.length = range.length;
	    TextParagraph textparagraph_28_
		= (TextParagraph) _paragraphVector.elementAt(i);
	    range_26_.intersectWith(textparagraph_28_.range());
	    stringbuffer.append(textparagraph_28_.stringForRange(range_26_));
	}
	recycleRange(range_26_);
	return stringbuffer.toString();
    }
    
    public void setAttributesForRange(Hashtable hashtable, Range range) {
	Range range_29_ = paragraphsRangeForRange(range);
	int i = range_29_.index;
	for (int i_30_ = range_29_.index + range_29_.length; i < i_30_; i++)
	    ((TextParagraph) _paragraphVector.elementAt(i)).setFormat(null);
	Vector vector = createAndReturnRunsForRange(range);
	i = 0;
	for (int i_31_ = vector.count(); i < i_31_; i++) {
	    TextStyleRun textstylerun = (TextStyleRun) vector.elementAt(i);
	    textstylerun.setAttributes(null);
	}
	addAttributesForRange(hashtable, range);
    }
    
    public Hashtable attributesAtIndex(int i) {
	TextStyleRun textstylerun = _runForIndex(i);
	TextParagraph textparagraph = _paragraphForIndex(i);
	if (textstylerun != null && textparagraph != null) {
	    Hashtable hashtable = textstylerun.attributes();
	    if (hashtable == null) {
		TextParagraphFormat textparagraphformat
		    = textparagraph.format();
		if (textparagraphformat == null)
		    return _defaultAttributes;
		Hashtable hashtable_32_
		    = (Hashtable) _defaultAttributes.clone();
		hashtable_32_.put("ParagraphFormatKey", textparagraphformat);
		return hashtable_32_;
	    }
	    TextParagraphFormat textparagraphformat = textparagraph.format();
	    Hashtable hashtable_33_ = (Hashtable) hashtable.clone();
	    if (textparagraphformat != null)
		hashtable_33_.put("ParagraphFormatKey", textparagraphformat);
	    else
		hashtable_33_.put("ParagraphFormatKey",
				  _defaultAttributes
				      .get("ParagraphFormatKey"));
	    return hashtable_33_;
	}
	return _defaultAttributes;
    }
    
    public int length() {
	return _charCount - 1;
    }
    
    public String string() {
	Range range = allocateRange(0, length());
	String string = stringForRange(range);
	recycleRange(range);
	return string;
    }
    
    public void setString(String string) {
	Range range = allocateRange(0, length());
	replaceRangeWithString(range, string);
	recycleRange(range);
    }
    
    public Range appendString(String string) {
	Range range = allocateRange(length(), 0);
	replaceRangeWithString(range, string);
	range.length = string.length();
	return range;
    }
    
    public void replaceRangeWithTextAttachment(Range range,
					       TextAttachment textattachment) {
	replaceRangeWithString(range, "@");
	Hashtable hashtable = new Hashtable();
	hashtable.put("TextAttachmentKey", textattachment);
	Range range_34_ = allocateRange(range.index, 1);
	addAttributesForRange(hashtable, range_34_);
	recycleRange(range_34_);
    }
    
    public void addAttributesForRange(Hashtable hashtable, Range range) {
	if (_owner != null)
	    _owner.attributesWillChange(this, range);
	addAttributesForRangeWithoutNotification(hashtable, range);
	if (_owner != null)
	    _owner.attributesDidChange(this, range);
    }
    
    public void addAttributeForRange(String string, Object object,
				     Range range) {
	Hashtable hashtable = new Hashtable();
	hashtable.put(string, object);
	addAttributesForRange(hashtable, range);
    }
    
    public void removeAttributeForRange(String string, Range range) {
	Vector vector = runsForRange(range);
	Range range_35_ = allocateRange();
	int i = 0;
	for (int i_36_ = vector.count(); i < i_36_; i++) {
	    Range range_37_ = (Range) vector.elementAt(i);
	    TextStyleRun textstylerun = _runForIndex(range_37_.index);
	    Hashtable hashtable = textstylerun.attributes();
	    if (hashtable != null && hashtable.get(string) != null) {
		Hashtable hashtable_38_ = (Hashtable) hashtable.clone();
		hashtable_38_.remove(string);
		range_35_.index = range.index;
		range_35_.length = range.length;
		range_35_.intersectWith(range_37_);
		setAttributesForRange(hashtable_38_, range_35_);
	    }
	}
    }
    
    public void addDefaultAttribute(String string, Object object) {
	Hashtable hashtable = defaultAttributes();
	hashtable.put(string, object);
	setDefaultAttributes(hashtable);
    }
    
    public Vector runsForRange(Range range) {
	Vector vector = new Vector();
	Object object = null;
	boolean bool = false;
	if (range.length == 0) {
	    Range range_39_ = runForIndex(range.index);
	    if (!range_39_.isNullRange())
		vector.addElement(range_39_);
	    return vector;
	}
	TextStyleRun textstylerun = _runForIndex(range.index);
	if (textstylerun == null)
	    textstylerun = _runForIndex(0);
	TextStyleRun textstylerun_40_
	    = _runForIndex(range.index + range.length() - 1);
	if (textstylerun_40_ == null)
	    textstylerun_40_ = _runForIndex(length() - 1);
	Vector vector_41_ = textstylerun.paragraph().runVector();
	int i;
	if (textstylerun.paragraph() == textstylerun_40_.paragraph()) {
	    i = vector_41_.indexOfIdentical(textstylerun_40_) + 1;
	    bool = true;
	} else {
	    i = vector_41_.count();
	    bool = false;
	}
	for (int i_42_ = vector_41_.indexOfIdentical(textstylerun); i_42_ < i;
	     i_42_++)
	    vector.addElement(((TextStyleRun) vector_41_.elementAt(i_42_))
				  .range());
	if (!bool) {
	    int i_43_
		= (_paragraphVector.indexOfIdentical(textstylerun.paragraph())
		   + 1);
	    for (i = _paragraphVector
			 .indexOfIdentical(textstylerun_40_.paragraph());
		 i_43_ < i; i_43_++) {
		vector_41_
		    = ((TextParagraph) _paragraphVector.elementAt(i_43_))
			  .runVector();
		int i_44_ = 0;
		for (int i_45_ = vector_41_.count(); i_44_ < i_45_; i_44_++)
		    vector.addElement(((TextStyleRun)
				       vector_41_.elementAt(i_44_))
					  .range());
	    }
	    vector_41_ = textstylerun_40_.paragraph().runVector();
	    i_43_ = 0;
	    for (i = vector_41_.indexOfIdentical(textstylerun_40_); i_43_ <= i;
		 i_43_++)
		vector.addElement(((TextStyleRun) vector_41_.elementAt(i_43_))
				      .range());
	}
	return vector;
    }
    
    public Vector paragraphsForRange(Range range) {
	Range range_46_ = paragraphsRangeForRange(range);
	Vector vector = new Vector();
	int i = range_46_.index;
	for (int i_47_ = range_46_.index + range_46_.length; i < i_47_; i++) {
	    Range range_48_
		= ((TextParagraph) _paragraphVector.elementAt(i)).range();
	    if (i == i_47_ - 1)
		range_48_.length--;
	    vector.addElement(range_48_);
	}
	return vector;
    }
    
    public Range runForIndex(int i) {
	TextStyleRun textstylerun = _runForIndex(i);
	if (textstylerun == null)
	    return allocateRange();
	return textstylerun.range();
    }
    
    public Range paragraphForIndex(int i) {
	Range range = allocateRange(i, 0);
	Vector vector = paragraphsForRange(range);
	recycleRange(range);
	if (vector.count() > 0)
	    return (Range) vector.elementAt(0);
	return allocateRange();
    }
    
    public Range paragraphForPoint(int i, int i_49_) {
	TextParagraph textparagraph = _paragraphForPoint(i, i_49_);
	if (textparagraph != null)
	    return textparagraph.range();
	return allocateRange();
    }
    
    public Range runForPoint(int i, int i_50_) {
	TextPositionInfo textpositioninfo = positionForPoint(i, i_50_, true);
	if (textpositioninfo != null) {
	    TextStyleRun textstylerun
		= _runForIndex(textpositioninfo._absPosition);
	    if (textstylerun != null)
		return textstylerun.range();
	}
	return allocateRange();
    }
    
    public int indexForPoint(int i, int i_51_) {
	TextPositionInfo textpositioninfo = positionForPoint(i, i_51_, true);
	if (textpositioninfo != null)
	    return textpositioninfo._absPosition;
	return -1;
    }
    
    public Vector rectsForRange(Range range) {
	return rectsForRange(range, null);
    }
    
    public Range selectedRange() {
	if (_selectedRange == null)
	    _selectedRange = allocateRange();
	_selectedRange.index = _selection.selectionStart();
	if (_selectedRange.index < 0)
	    return allocateRange();
	_selectedRange.length
	    = _selection.selectionEnd() - _selection.selectionStart();
	return _selectedRange;
    }
    
    public void selectRange(Range range) {
	if (range.isNullRange())
	    _selection.clearRange();
	else
	    _selection.setRange(range.index(), range.lastIndex() + 1);
	_selectionChanged();
    }
    
    public boolean hasSelection() {
	Range range = selectedRange();
	if (range.isNullRange())
	    return false;
	return true;
    }
    
    public void insertHTMLElementsInRange(Vector vector, Range range,
					  Hashtable hashtable) {
	int[] is = new int[vector.count()];
	int i = 0;
	int i_52_ = 0;
	Hashtable hashtable_53_ = new Hashtable();
	FastStringBuffer faststringbuffer = new FastStringBuffer();
	if (hashtable == null)
	    hashtable = defaultAttributes();
	Hashtable hashtable_54_
	    = attributesByRemovingStaticAttributes(hashtable);
	this.setDirty(true);
	disableResizing();
	faststringbuffer.setDoublesCapacityWhenGrowing(true);
	int i_55_ = 0;
	for (int i_56_ = vector.count(); i_55_ < i_56_; i_55_++) {
	    ((TextViewHTMLElement) vector.elementAt(i_55_))
		.appendString(hashtable_53_, faststringbuffer);
	    if (i_55_ == 0)
		i_52_ = is[0] = faststringbuffer.length();
	    else {
		is[i_55_] = faststringbuffer.length() - i_52_;
		i_52_ += is[i_55_];
	    }
	}
	replaceRangeWithString(range, faststringbuffer.toString());
	disableFormatting();
	i_55_ = 0;
	for (int i_57_ = vector.count(); i_55_ < i_57_; i_55_++) {
	    ((TextViewHTMLElement) vector.elementAt(i_55_))
		.setAttributesStartingAt
		(range.index + i, hashtable_54_, this, hashtable_53_);
	    i += is[i_55_];
	}
	enableFormatting();
	reformatAll();
	enableResizing();
	sizeToMinSize();
    }
    
    public void importHTMLInRange
	(InputStream inputstream, Range range, URL url)
	throws IOException, HTMLParsingException {
	importHTMLInRange(inputstream, range, url, defaultAttributes());
    }
    
    public void importHTMLInRange
	(InputStream inputstream, Range range, URL url, Hashtable hashtable)
	throws IOException, HTMLParsingException {
	Vector vector = new Vector();
	validateHTMLParsingRules();
	HTMLParser htmlparser = new HTMLParser(inputstream, _htmlParsingRules);
	try {
	    HTMLElement htmlelement;
	    while ((htmlelement = htmlparser.nextHTMLElement()) != null)
		vector.addElement(htmlelement);
	} catch (InstantiationException instantiationexception) {
	    throw new InconsistencyException("Cannot intantiate HTML storage");
	} catch (IllegalAccessException illegalaccessexception) {
	    throw new InconsistencyException
		      ("Cannot access HTML storage classes");
	}
	setBaseURL(url);
	insertHTMLElementsInRange(vector, range, hashtable);
    }
    
    public void importHTMLFromURLString(String string) {
	Range range = allocateRange(0, length());
	try {
	    URL url = new URL(string);
	    InputStream inputstream = url.openStream();
	    importHTMLInRange(inputstream, range, url, defaultAttributes());
	    range.index = 0;
	    range.length = 0;
	    selectRange(range);
	    scrollRangeToVisible(range);
	} catch (java.net.MalformedURLException malformedurlexception) {
	    System.err.println("Bad URL " + string);
	} catch (IOException ioexception) {
	    System.err.println("IOException while reading " + string);
	} catch (HTMLParsingException htmlparsingexception) {
	    System.err.println("At line " + htmlparsingexception.lineNumber()
			       + ":" + htmlparsingexception);
	}
	recycleRange(range);
    }
    
    public void setHTMLParsingRules(HTMLParsingRules htmlparsingrules) {
	_htmlParsingRules = htmlparsingrules;
    }
    
    public HTMLParsingRules htmlParsingRules() {
	validateHTMLParsingRules();
	return _htmlParsingRules;
    }
    
    public Range runWithLinkDestinationNamed(String string) {
	TextStyleRun textstylerun = null;
	Range range = null;
	int i = 0;
	for (int i_58_ = _paragraphVector.count(); i < i_58_; i++) {
	    TextParagraph textparagraph
		= (TextParagraph) _paragraphVector.elementAt(i);
	    int i_59_ = 0;
	    for (int i_60_ = textparagraph._runVector.count(); i_59_ < i_60_;
		 i_59_++) {
		textstylerun
		    = (TextStyleRun) textparagraph._runVector.elementAt(i_59_);
		String string_61_;
		if (textstylerun._attributes != null
		    && ((string_61_ = (String) textstylerun._attributes
						   .get("LinkDestinationKey"))
			!= null)
		    && string_61_.equals(string)) {
		    range = textstylerun.range();
		    break;
		}
	    }
	    if (range != null)
		break;
	}
	if (range == null)
	    return new Range();
	if (textstylerun != null) {
	    Range range_62_ = range;
	    while (range.length == 0) {
		textstylerun = runAfter(textstylerun);
		if (textstylerun != null)
		    range = textstylerun.range();
		else {
		    range = range_62_;
		    break;
		}
	    }
	}
	return range;
    }
    
    public int lineCount() {
	int i = 0;
	int i_63_ = 0;
	for (int i_64_ = _paragraphVector.count(); i_63_ < i_64_; i_63_++) {
	    TextParagraph textparagraph
		= (TextParagraph) _paragraphVector.elementAt(i_63_);
	    i += textparagraph.lineCount();
	}
	return i;
    }
    
    public void setBaseURL(URL url) {
	_baseURL = url;
    }
    
    public URL baseURL() {
	return _baseURL;
    }
    
    public boolean canBecomeSelectedView() {
	if (isEditable() || isSelectable())
	    return true;
	return false;
    }
    
    public void willBecomeSelected() {
	this.setFocusedView();
    }
    
    public static String stringWithoutCarriageReturns(String string) {
	FastStringBuffer faststringbuffer = new FastStringBuffer();
	int i = 0;
	for (int i_65_ = string.length(); i < i_65_; i++) {
	    char c = string.charAt(i);
	    if (c != '\r' || i + 1 >= i_65_ || string.charAt(i + 1) != '\n')
		faststringbuffer.append(c);
	}
	return faststringbuffer.toString();
    }
    
    FontMetrics defaultFontMetrics() {
	if (_defaultFontMetricsCache == null)
	    _defaultFontMetricsCache = font().fontMetrics();
	return _defaultFontMetricsCache;
    }
    
    static Hashtable attributesByRemovingStaticAttributes
	(Hashtable hashtable) {
	if (hashtable == null)
	    return null;
	Hashtable hashtable_66_ = (Hashtable) hashtable.clone();
	hashtable_66_.remove("TextAttachmentKey");
	hashtable_66_.remove("TextAttachmentBaselineOffsetKey");
	return hashtable_66_;
    }
    
    private void addParagraph(TextParagraph textparagraph) {
	if (textparagraph != null) {
	    textparagraph.setOwner(this);
	    _paragraphVector.addElement(textparagraph);
	}
    }
    
    synchronized void _setEditing(boolean bool) {
	_editing = bool;
    }
    
    synchronized boolean isEditing() {
	return _editing;
    }
    
    private void reformatAll() {
	Range range = selectedRange();
	int i = 0;
	_charCount = 0;
	int i_67_ = _paragraphVector.count();
	if (formattingEnabled()) {
	    for (int i_68_ = 0; i_68_ < i_67_; i_68_++) {
		TextParagraph textparagraph
		    = (TextParagraph) _paragraphVector.elementAt(i_68_);
		textparagraph._y = i;
		textparagraph._startChar = _charCount;
		textparagraph.computeLineBreaksAndHeights(bounds.width);
		i += textparagraph._height + _paragraphSpacing;
		_charCount += textparagraph._charCount;
	    }
	    sizeBy(0, i - bounds.height);
	    notifyAttachmentsForRange(new Range(0, length()), true);
	    selectRange(range);
	}
    }
    
    void disableFormatting() {
	_formattingDisabled++;
    }
    
    void enableFormatting() {
	_formattingDisabled--;
	if (_formattingDisabled < 0)
	    _formattingDisabled = 0;
    }
    
    boolean formattingEnabled() {
	return _formattingDisabled == 0;
    }
    
    private void formatParagraphAtIndex(int i) {
	Range range = new Range();
	if (formattingEnabled() && i != -1) {
	    TextParagraph textparagraph
		= (TextParagraph) _paragraphVector.elementAt(i);
	    int i_69_ = i - 1;
	    if (i_69_ >= -1) {
		int i_70_;
		if (i_69_ == -1) {
		    _charCount = 0;
		    i_70_ = 0;
		} else {
		    TextParagraph textparagraph_71_
			= (TextParagraph) _paragraphVector.elementAt(i_69_);
		    _charCount = (textparagraph_71_._startChar
				  + textparagraph_71_._charCount);
		    i_70_ = (textparagraph_71_._y + textparagraph_71_._height
			     + _paragraphSpacing);
		}
		textparagraph.setY(i_70_);
		textparagraph.setStartChar(_charCount);
		textparagraph.computeLineBreaksAndHeights(bounds.width);
		_charCount += textparagraph._charCount;
		i_70_ += textparagraph._height + _paragraphSpacing;
		int i_72_ = _paragraphVector.count();
		for (int i_73_ = i + 1; i_73_ < i_72_; i_73_++) {
		    TextParagraph textparagraph_74_
			= (TextParagraph) _paragraphVector.elementAt(i_73_);
		    textparagraph_74_.setY(i_70_);
		    textparagraph_74_.setStartChar(_charCount);
		    i_70_ += textparagraph_74_._height + _paragraphSpacing;
		    _charCount += textparagraph_74_._charCount;
		}
		range.index = textparagraph._startChar;
		range.unionWith(lastParagraph().range());
		sizeBy(0, i_70_ - bounds.height);
		notifyAttachmentsForRange(range, true);
	    }
	}
    }
    
    private void formatParagraph(TextParagraph textparagraph) {
	formatParagraphAtIndex(_paragraphVector
				   .indexOfIdentical(textparagraph));
    }
    
    private int adjustCharCountsAndSpacing(int i) {
	int i_75_ = _charCount = 0;
	if (i > 0) {
	    TextParagraph textparagraph
		= (TextParagraph) _paragraphVector.elementAt(i - 1);
	    i_75_
		= textparagraph._y + textparagraph._height + _paragraphSpacing;
	    _charCount = textparagraph._startChar + textparagraph._charCount;
	}
	int i_76_ = _paragraphVector.count();
	for (int i_77_ = i; i_77_ < i_76_; i_77_++) {
	    TextParagraph textparagraph
		= (TextParagraph) _paragraphVector.elementAt(i_77_);
	    textparagraph._y = i_75_;
	    textparagraph._startChar = _charCount;
	    i_75_ += textparagraph._height + _paragraphSpacing;
	    _charCount += textparagraph._charCount;
	}
	return i_75_;
    }
    
    private int adjustCharCountsAndSpacing() {
	return adjustCharCountsAndSpacing(0);
    }
    
    int _paragraphIndexForIndex(int i) {
	int i_78_ = _paragraphVector.count();
	if (i > length() / 2) {
	    for (int i_79_ = i_78_ - 1; i_79_ >= 0; i_79_--) {
		TextParagraph textparagraph
		    = (TextParagraph) _paragraphVector.elementAt(i_79_);
		if (i >= textparagraph._startChar
		    && i < textparagraph._startChar + textparagraph._charCount)
		    return i_79_;
	    }
	} else {
	    for (int i_80_ = 0; i_80_ < i_78_; i_80_++) {
		TextParagraph textparagraph
		    = (TextParagraph) _paragraphVector.elementAt(i_80_);
		if (i >= textparagraph._startChar
		    && i < textparagraph._startChar + textparagraph._charCount)
		    return i_80_;
	    }
	}
	return -1;
    }
    
    TextParagraph _paragraphForIndex(int i) {
	int i_81_ = _paragraphIndexForIndex(i);
	if (i_81_ != -1)
	    return (TextParagraph) _paragraphVector.elementAt(i_81_);
	return null;
    }
    
    private TextParagraph _paragraphForPoint(int i, int i_82_) {
	if (i_82_ < 0) {
	    i_82_ = 0;
	    boolean bool = false;
	}
	int i_83_ = _paragraphVector.count();
	int i_84_ = 0;
	for (int i_85_ = 0; i_85_ < i_83_; i_85_++) {
	    TextParagraph textparagraph
		= (TextParagraph) _paragraphVector.elementAt(i_85_);
	    int i_86_ = i_84_ + textparagraph._height;
	    if (i_82_ >= i_84_ && i_82_ < i_86_)
		return textparagraph;
	    i_84_ = i_86_;
	}
	return null;
    }
    
    TextPositionInfo positionInfoForIndex(int i) {
	TextParagraph textparagraph = _paragraphForIndex(i);
	if (textparagraph == null)
	    textparagraph = lastParagraph();
	return textparagraph._infoForPosition(i);
    }
    
    private TextPositionInfo positionForPoint(int i, int i_87_, boolean bool) {
	if (i_87_ < 0) {
	    i_87_ = 0;
	    i = 0;
	}
	TextParagraph textparagraph = _paragraphForPoint(i, i_87_);
	if (textparagraph == null) {
	    if (_charCount == 0)
		return lastParagraph().infoForPosition(_charCount, i_87_);
	    return lastParagraph().infoForPosition(_charCount - 1, i_87_);
	}
	return textparagraph.positionForPoint(i, i_87_, bool);
    }
    
    void drawInsertionPoint() {
	Rect rect = _selection.insertionPointRect();
	this.addDirtyRect(rect);
	returnRect(rect);
    }
    
    private void insertString(String string, int i) {
	insertString(string, i, false);
    }
    
    private void insertString(String string, int i, boolean bool) {
	boolean bool_88_ = false;
	if (string != null) {
	    int i_89_ = _paragraphIndexForIndex(i);
	    if (i_89_ != -1) {
		TextParagraph textparagraph
		    = (TextParagraph) _paragraphVector.elementAt(i_89_);
		if (textparagraph != null) {
		    TextParagraph textparagraph_90_ = lastParagraph();
		    if (textparagraph_90_ != null) {
			int i_91_
			    = textparagraph_90_._y + textparagraph_90_._height;
		    } else {
			boolean bool_92_ = false;
		    }
		    TextPositionInfo textpositioninfo
			= positionInfoForIndex(i);
		    if (textpositioninfo == null)
			textpositioninfo
			    = textparagraph.infoForPosition(i, -1);
		    TextPositionInfo textpositioninfo_93_
			= textparagraph.insertCharOrStringAt('\0', string, i);
		    int i_94_ = string.length();
		    _charCount += i_94_;
		    if (textpositioninfo_93_ == null)
			_selection.showInsertionPoint();
		    else if (this.isDirty() && dirtyRect == null) {
			Range range
			    = new Range(textparagraph._startChar,
					length() - textparagraph._startChar);
			notifyAttachmentsForRange(range, true);
		    } else {
			if (bool)
			    formatParagraphAtIndex(i_89_);
			textpositioninfo_93_.setAbsPosition(i + i_94_);
			TextParagraphFormat textparagraphformat
			    = textparagraph.currentParagraphFormat();
			if (textpositioninfo_93_._redrawCurrentLineOnly
			    && textparagraphformat._justification == 0) {
			    TextParagraph textparagraph_95_ = _updateParagraph;
			    int i_96_ = _updateLine;
			    _updateParagraph = textparagraph;
			    _updateLine = textpositioninfo_93_._updateLine;
			    _drawText = false;
			    Rect rect = (textparagraph.rectForLine
					 (textpositioninfo_93_._lineNumber));
			    rect.setBounds(textpositioninfo._x, rect.y,
					   rect.width - (textpositioninfo._x
							 - rect.x),
					   rect.height);
			    this.addDirtyRect(rect);
			    returnRect(rect);
			    _drawText = true;
			    _updateParagraph = textparagraph_95_;
			    _updateLine = i_96_;
			    notifyAttachmentsForRange(textpositioninfo_93_
							  .lineRange(),
						      true);
			} else if ((textpositioninfo_93_
				    ._redrawCurrentParagraphOnly)
				   || (textpositioninfo_93_
				       ._redrawCurrentLineOnly)) {
			    Range range = textparagraph.range();
			    notifyAttachmentsForRange(range, true);
			    dirtyRange(range);
			} else {
			    Range range = new Range(textparagraph._startChar,
						    length() - (textparagraph
								._startChar));
			    notifyAttachmentsForRange(range, true);
			    dirtyRange(range);
			}
		    }
		}
	    }
	}
    }
    
    private void fastDeleteChar(boolean bool) {
	boolean bool_97_ = false;
	if (isEditing()) {
	    int i = _selection.insertionPoint();
	    if ((bool || i != 0) && (!bool || i != length())) {
		if (bool)
		    i++;
		TextParagraph textparagraph = _paragraphForIndex(i);
		if (textparagraph != null) {
		    notifyAttachmentsForRange(new Range(i, 1), false);
		    TextParagraph textparagraph_98_ = lastParagraph();
		    if (textparagraph_98_ != null) {
			int i_99_
			    = textparagraph_98_._y + textparagraph_98_._height;
		    } else {
			boolean bool_100_ = false;
		    }
		    TextPositionInfo textpositioninfo;
		    if (textparagraph._startChar == i
			&& !isOnlyParagraph(textparagraph)) {
			int i_101_
			    = _paragraphVector.indexOfIdentical(textparagraph);
			TextParagraph textparagraph_102_
			    = ((TextParagraph)
			       _paragraphVector.elementAt(i_101_ - 1));
			if (textparagraph_102_ != null) {
			    textparagraph_102_.subsumeParagraph(textparagraph);
			    _paragraphVector.removeElement(textparagraph);
			    textparagraph = textparagraph_102_;
			    formatParagraph(textparagraph);
			}
			if (i == 0)
			    textpositioninfo
				= textparagraph.infoForPosition(0, -1);
			else
			    textpositioninfo
				= textparagraph.infoForPosition(i - 1, -1);
		    } else {
			textpositioninfo = textparagraph.removeCharAt(i);
			_charCount--;
		    }
		    TextPositionInfo textpositioninfo_103_ = textpositioninfo;
		    int i_104_ = adjustCharCountsAndSpacing();
		    sizeBy(0, i_104_ - bounds.height);
		    if (textpositioninfo == null)
			_selection.showInsertionPoint();
		    else {
			notifyAttachmentsForRange(textpositioninfo._textRun
						      ._paragraph.range(),
						  true);
			if (!bool) {
			    if (i == 0)
				textpositioninfo.setAbsPosition(0);
			    else
				textpositioninfo.setAbsPosition(i - 1);
			    _selection.setInsertionPoint(textpositioninfo);
			    _selectionChanged();
			} else
			    _selection.setInsertionPoint(textpositioninfo);
			TextParagraphFormat textparagraphformat
			    = textparagraph.currentParagraphFormat();
			if (textpositioninfo._redrawCurrentLineOnly
			    && textparagraphformat._justification == 0) {
			    TextParagraph textparagraph_105_
				= _updateParagraph;
			    int i_106_ = _updateLine;
			    _updateParagraph = textparagraph;
			    _updateLine = textpositioninfo._updateLine;
			    _drawText = false;
			    Rect rect
				= textparagraph.rectForLine(textpositioninfo
							    ._lineNumber);
			    rect.setBounds(textpositioninfo_103_._x, rect.y,
					   (rect.width
					    - (textpositioninfo_103_._x
					       - rect.x)),
					   rect.height);
			    this.addDirtyRect(rect);
			    returnRect(rect);
			    _drawText = true;
			    _updateParagraph = textparagraph_105_;
			    _updateLine = i_106_;
			} else if (textpositioninfo._redrawCurrentParagraphOnly
				   || textpositioninfo._redrawCurrentLineOnly)
			    dirtyRange(textparagraph.range());
			else {
			    Range range
				= allocateRange(textparagraph._startChar,
						(length()
						 - textparagraph._startChar));
			    dirtyRange(range);
			    recycleRange(range);
			}
		    }
		}
	    }
	}
    }
    
    private Rect insertReturn() {
	if (_selection.isARange())
	    deleteSelection();
	int i = _selection.insertionPoint();
	Rect rect = insertReturn(i);
	_selection.setRange(i + 1, i + 1);
	_selectionChanged();
	return rect;
    }
    
    private Rect insertReturn(int i) {
	int i_107_ = _paragraphIndexForIndex(i);
	if (i_107_ == -1)
	    i_107_ = _paragraphVector.count() - 1;
	TextParagraph textparagraph
	    = (TextParagraph) _paragraphVector.elementAt(i_107_);
	TextParagraph textparagraph_108_ = textparagraph;
	textparagraph = textparagraph.createNewParagraphAt(i);
	formatParagraphAtIndex(i_107_);
	_charCount++;
	_paragraphVector.insertElementAt(textparagraph, i_107_ + 1);
	formatParagraphAtIndex(i_107_ + 1);
	return newRect(0, textparagraph_108_._y, bounds.width,
		       bounds.height - textparagraph_108_._y);
    }
    
    private TextStyleRun _runForIndex(int i) {
	int i_109_ = length();
	if (i_109_ > 0 && i >= i_109_)
	    return _runForIndex(i_109_ - 1);
	if (i >= 0) {
	    TextParagraph textparagraph = _paragraphForIndex(i);
	    if (textparagraph != null)
		return textparagraph.runForCharPosition(i);
	    return null;
	}
	return null;
    }
    
    private boolean equalsAttributesHint(Hashtable hashtable,
					 Hashtable hashtable_110_) {
	if (hashtable == hashtable_110_)
	    return true;
	if (hashtable == null || hashtable_110_ == null)
	    return false;
	return false;
    }
    
    private void _keyDown() {
	Event event = (Event) _eventVector.removeFirstElement();
	if (event instanceof KeyEvent) {
	    KeyEvent keyevent = (KeyEvent) event;
	    if (keyevent.key != 1022) {
		_selection.disableInsertionPoint();
		int i = _selection.insertionPoint();
		TextParagraph textparagraph = _paragraphForIndex(i);
		boolean bool = true;
		if (keyevent.isReturnKey()) {
		    Range range = new Range(selectedRange());
		    if (_owner != null)
			_owner.textWillChange(this, range);
		    Rect rect = insertReturn();
		    if (_owner != null) {
			range.length = 1;
			_owner.textDidChange(this, range);
		    }
		    this.draw(rect);
		    returnRect(rect);
		} else if (keyevent.isLeftArrowKey()) {
		    if (keyevent.isShiftKeyDown()) {
			int i_111_ = _selection.orderedSelectionEnd() - 1;
			if (i_111_ < 0)
			    i_111_ = 0;
			_selection.setRange(_selection.orderedSelectionStart(),
					    i_111_, true);
		    } else if (i == -1)
			_selection.setRange(_selection.selectionStart(),
					    _selection.selectionStart(), true);
		    else {
			int i_112_ = i - 1;
			if (i_112_ < 0)
			    i_112_ = 0;
			_selection.setRange(i_112_, i_112_, true);
		    }
		    _selectionChanged();
		} else if (keyevent.isRightArrowKey()) {
		    if (keyevent.isShiftKeyDown())
			_selection.setRange(_selection.orderedSelectionStart(),
					    (_selection.orderedSelectionEnd()
					     + 1),
					    false);
		    else if (i == -1)
			_selection.setRange(_selection.selectionEnd(),
					    _selection.selectionEnd());
		    else
			_selection.setRange(i + 1, i + 1, false);
		    _selectionChanged();
		} else if (keyevent.isUpArrowKey()) {
		    TextPositionInfo textpositioninfo
			= _selection.orderedSelectionEndInfo();
		    TextPositionInfo textpositioninfo_113_
			= positionForPoint(textpositioninfo._x,
					   textpositioninfo._y - 1, false);
		    if (textpositioninfo_113_ != null) {
			textpositioninfo_113_
			    .representCharacterBeforeEndOfLine();
			if (keyevent.isShiftKeyDown()) {
			    if ((textpositioninfo_113_._absPosition
				 == textpositioninfo._absPosition)
				&& textpositioninfo_113_._absPosition > 0)
				_selection.setRange
				    (_selection.orderedSelectionStart(),
				     textpositioninfo_113_._absPosition - 1,
				     null, false, true);
			    else
				_selection.setRange
				    (_selection.orderedSelectionStart(),
				     textpositioninfo_113_._absPosition,
				     textpositioninfo_113_, false, true);
			} else if (textpositioninfo._lineNumber != 0
				   || (textpositioninfo._textRun._paragraph
				       != _paragraphVector.elementAt(0)))
			    _selection
				.setInsertionPoint(textpositioninfo_113_);
			_selectionChanged();
		    }
		} else if (keyevent.isDownArrowKey()) {
		    TextPositionInfo textpositioninfo
			= _selection.orderedSelectionEndInfo();
		    textpositioninfo.representCharacterBeforeEndOfLine();
		    TextPositionInfo textpositioninfo_114_
			= positionForPoint(textpositioninfo._x,
					   (textpositioninfo._y
					    + textpositioninfo._lineHeight
					    + 1),
					   false);
		    if (textpositioninfo_114_ != null) {
			if (keyevent.isShiftKeyDown())
			    _selection.setRange(_selection
						    .orderedSelectionStart(),
						(textpositioninfo_114_
						 ._absPosition),
						textpositioninfo_114_, false,
						false);
			else if ((textpositioninfo_114_._textRun._paragraph
				  != textpositioninfo._textRun._paragraph)
				 || (textpositioninfo_114_._y
				     != textpositioninfo._y))
			    _selection
				.setInsertionPoint(textpositioninfo_114_);
			_selectionChanged();
		    }
		} else if (keyevent.isHomeKey()) {
		    Range range = selectedRange();
		    TextPositionInfo textpositioninfo
			= _selection.orderedSelectionEndInfo();
		    Range range_115_ = lineForPosition(textpositioninfo);
		    TextPositionInfo textpositioninfo_116_
			= positionInfoForIndex(range_115_.index);
		    if (textpositioninfo_116_ != null) {
			if (textpositioninfo_116_._y != textpositioninfo._y)
			    textpositioninfo_116_
				.representCharacterAfterEndOfLine();
			if (keyevent.isShiftKeyDown()) {
			    if (textpositioninfo_116_._absPosition
				!= textpositioninfo._absPosition)
				_selection.setRange
				    (_selection.orderedSelectionStart(),
				     textpositioninfo_116_._absPosition,
				     textpositioninfo_116_, false, false);
			} else
			    _selection
				.setInsertionPoint(textpositioninfo_116_);
		    }
		    _selectionChanged();
		} else if (keyevent.isEndKey()) {
		    Range range = selectedRange();
		    TextPositionInfo textpositioninfo
			= _selection.orderedSelectionEndInfo();
		    Range range_117_ = lineForPosition(textpositioninfo);
		    TextPositionInfo textpositioninfo_118_
			= positionInfoForIndex(range_117_.index
					       + range_117_.length);
		    if (textpositioninfo_118_ != null) {
			if (textpositioninfo_118_._y != textpositioninfo._y)
			    textpositioninfo_118_
				.representCharacterAfterEndOfLine();
			if (keyevent.isShiftKeyDown()) {
			    if (textpositioninfo_118_._absPosition
				!= textpositioninfo._absPosition)
				_selection.setRange
				    (_selection.orderedSelectionStart(),
				     textpositioninfo_118_._absPosition,
				     textpositioninfo_118_, false, false);
			} else
			    _selection
				.setInsertionPoint(textpositioninfo_118_);
		    }
		    _selectionChanged();
		} else if (keyevent.isBackspaceKey()) {
		    Range range = new Range(selectedRange());
		    if (_selection.isARange()) {
			if (_owner != null)
			    _owner.textWillChange(this, range);
			deleteSelection();
			if (_owner != null) {
			    range.length = 0;
			    _owner.textDidChange(this, range);
			}
		    } else {
			if (_owner != null) {
			    range.index--;
			    range.length = 1;
			    _owner.textWillChange(this, range);
			}
			fastDeleteChar(false);
			if (_owner != null) {
			    range.length = 0;
			    _owner.textDidChange(this, range);
			}
		    }
		} else if (keyevent.isDeleteKey()) {
		    Range range = selectedRange();
		    if (_selection.isARange()) {
			if (_owner != null)
			    _owner.textWillChange(this, range);
			deleteSelection();
			if (_owner != null) {
			    range.length = 0;
			    _owner.textDidChange(this, range);
			}
		    } else if (range.index < length()) {
			range.length = 1;
			if (_owner != null)
			    _owner.textWillChange(this, range);
			fastDeleteChar(true);
			range.length = 0;
			if (_owner != null)
			    _owner.textDidChange(this, range);
		    }
		} else if (keyevent.isPrintableKey()) {
		    Range range = selectedRange();
		    Range range_119_ = allocateRange();
		    if (keyevent.isExtendedKeyEvent())
			replaceRangeWithString(range,
					       String.valueOf(keyevent
								  .keyChar()));
		    else
			replaceRangeWithString(range,
					       String.valueOf((char) keyevent
								     .key));
		    if (_typingAttributes.count() > 0) {
			range_119_.index = range.index;
			range_119_.length = 1;
			addAttributesForRangeWithoutNotification
			    (_typingAttributes, range_119_);
			clearTypingAttributes();
		    }
		    range_119_.index = range.index + 1;
		    range_119_.length = 0;
		    selectRange(range_119_);
		    recycleRange(range_119_);
		} else if (keyevent.isTabKey()) {
		    Range range = selectedRange();
		    Range range_120_ = allocateRange();
		    replaceRangeWithString(range, "\t");
		    range_120_.index = range.index + 1;
		    range_120_.length = 0;
		    selectRange(range_120_);
		    recycleRange(range_120_);
		} else
		    bool = false;
		if (bool) {
		    Range range
			= new Range(_selection.orderedSelectionEnd(), 0);
		    if (range.index > 0) {
			range.index--;
			range.length++;
		    }
		    scrollRangeToVisible(range);
		}
		_selection.enableInsertionPoint();
	    }
	}
    }
    
    private Range paragraphsRangeForRange(Range range) {
	Range range_121_ = allocateRange();
	boolean bool = false;
	int i = _paragraphVector.count();
	int i_122_ = range.index + range.length;
	for (int i_123_ = 0; i_123_ < i; i_123_++) {
	    TextParagraph textparagraph
		= (TextParagraph) _paragraphVector.elementAt(i_123_);
	    if (!bool && range.index >= textparagraph._startChar
		&& (range.index
		    < textparagraph._startChar + textparagraph._charCount)) {
		range_121_.index = i_123_;
		bool = true;
	    }
	    if (bool && i_122_ >= textparagraph._startChar
		&& (i_122_
		    < textparagraph._startChar + textparagraph._charCount)) {
		range_121_.length = i_123_ - range_121_.index + 1;
		break;
	    }
	}
	return range_121_;
    }
    
    private Vector createAndReturnRunsForRange(Range range) {
	int i = range.index;
	int i_124_ = range.index + range.length;
	TextParagraph textparagraph = _paragraphForIndex(i);
	TextParagraph textparagraph_125_ = _paragraphForIndex(i_124_);
	boolean bool = textparagraph == textparagraph_125_;
	if (range.length == 0) {
	    TextStyleRun textstylerun = textparagraph.createNewRunAt(i);
	    if (textstylerun.charCount() > 0)
		textstylerun = textparagraph.createNewRunAt(i);
	    Vector vector = newVector();
	    vector.addElement(textstylerun);
	    return vector;
	}
	TextStyleRun textstylerun = textparagraph.runForCharPosition(i);
	if (textstylerun.rangeIndex() != i)
	    textstylerun = textparagraph.createNewRunAt(i);
	TextStyleRun textstylerun_126_
	    = textparagraph_125_.runForCharPosition(i_124_);
	if (i_124_ <= (textstylerun_126_.rangeIndex()
		       + textstylerun_126_.charCount() - 1)) {
	    textstylerun_126_ = textparagraph_125_.createNewRunAt(i_124_);
	    textstylerun_126_
		= textparagraph_125_.runBefore(textstylerun_126_);
	}
	if (bool) {
	    if (textstylerun == textstylerun_126_) {
		Vector vector = new Vector();
		vector.addElement(textstylerun);
		return vector;
	    }
	    Vector vector
		= textparagraph.runsFromTo(textstylerun, textstylerun_126_);
	    return vector;
	}
	Vector vector = newVector();
	vector.addElement(textstylerun);
	Vector vector_127_ = textparagraph.runsAfter(textstylerun);
	vector.addElementsIfAbsent(vector_127_);
	returnVector(vector_127_);
	int i_129_;
	int i_128_
	    = i_129_ = _paragraphVector.indexOfIdentical(textparagraph) + 1;
	for (int i_130_
		 = _paragraphVector.indexOfIdentical(textparagraph_125_);
	     i_129_ < i_130_; i_129_++) {
	    TextParagraph textparagraph_131_
		= (TextParagraph) _paragraphVector.elementAt(i_129_);
	    vector.addElementsIfAbsent(textparagraph_131_.runVector());
	}
	vector_127_ = textparagraph_125_.runsBefore(textstylerun_126_);
	vector_127_.addElement(textstylerun_126_);
	vector.addElementsIfAbsent(vector_127_);
	returnVector(vector_127_);
	return vector;
    }
    
    private void processSetFont(Font font) {
	Range range = selectedRange();
	if (range.length > 0) {
	    Hashtable hashtable = new Hashtable();
	    hashtable.put("FontKey", font);
	    addAttributesForRange(hashtable, range);
	} else
	    addTypingAttribute("FontKey", font);
    }
    
    private void deleteSelection(Vector vector) {
	Range range = selectedRange();
	deleteRange(range, vector);
	_selection.setRange(range.index, range.index);
	_selectionChanged();
    }
    
    private void deleteRange(Range range, Vector vector) {
	Object object = null;
	if (range.length != 0) {
	    Range range_132_
		= allocateRange(range.index, length() - range.index);
	    dirtyRange(range_132_);
	    recycleRange(range_132_);
	    notifyAttachmentsForRange(range, false);
	    TextPositionInfo textpositioninfo
		= positionInfoForIndex(range.index);
	    TextPositionInfo textpositioninfo_133_
		= positionInfoForIndex(range.index + range.length);
	    TextParagraph textparagraph = textpositioninfo._textRun._paragraph;
	    TextParagraph textparagraph_134_
		= textpositioninfo_133_._textRun._paragraph;
	    boolean bool = textparagraph == textparagraph_134_;
	    TextStyleRun textstylerun = textpositioninfo._textRun;
	    if (textstylerun.rangeIndex() != textpositioninfo._absPosition)
		textstylerun = textparagraph.createNewRunAt(textpositioninfo
							    ._absPosition);
	    if (!bool) {
		Vector vector_135_ = textparagraph.runsAfter(textstylerun);
		textparagraph.removeRun(textstylerun);
		textparagraph.removeRuns(vector_135_);
		if (vector != null) {
		    TextParagraph textparagraph_136_ = new TextParagraph(this);
		    textparagraph_136_.setFormat(textparagraph._format);
		    textparagraph_136_.addRun(textstylerun);
		    textparagraph_136_.addRuns(vector_135_);
		    vector.addElement(textparagraph_136_);
		}
		vector_135_.removeAllElements();
		returnVector(vector_135_);
	    }
	    TextStyleRun textstylerun_137_ = textpositioninfo_133_._textRun;
	    if (textstylerun_137_.rangeIndex()
		!= textpositioninfo_133_._absPosition)
		textstylerun_137_
		    = textparagraph_134_
			  .createNewRunAt(textpositioninfo_133_._absPosition);
	    if (bool) {
		Vector vector_138_
		    = textparagraph.runsFromTo(textstylerun,
					       textstylerun_137_);
		vector_138_.removeElement(textstylerun_137_);
		textparagraph.removeRuns(vector_138_);
		if (vector != null) {
		    TextParagraph textparagraph_139_ = new TextParagraph(this);
		    textparagraph_139_.setFormat(textparagraph._format);
		    textparagraph_139_.addRuns(vector_138_);
		    vector.addElement(textparagraph_139_);
		}
		vector_138_.removeAllElements();
		returnVector(vector_138_);
	    } else {
		int i_140_;
		int i
		    = (i_140_
		       = _paragraphVector.indexOfIdentical(textparagraph) + 1);
		for (int i_141_ = _paragraphVector
				      .indexOfIdentical(textparagraph_134_);
		     i_140_ < i_141_; i_140_++) {
		    TextParagraph textparagraph_142_
			= (TextParagraph) _paragraphVector.removeElementAt(i);
		    if (vector != null)
			vector.addElement(textparagraph_142_);
		}
		Vector vector_143_
		    = textparagraph_134_.runsBefore(textstylerun_137_);
		textparagraph_134_.removeRuns(vector_143_);
		if (vector != null) {
		    TextParagraph textparagraph_144_ = new TextParagraph(this);
		    textparagraph_144_.setFormat(textparagraph._format);
		    textparagraph_144_.addRuns(vector_143_);
		    vector.addElement(textparagraph_144_);
		}
		vector_143_.removeAllElements();
		returnVector(vector_143_);
		textparagraph.subsumeParagraph(textparagraph_134_);
		_paragraphVector.removeElement(textparagraph_134_);
	    }
	    formatParagraph(textparagraph);
	}
    }
    
    private void deleteSelection() {
	deleteSelection(null);
    }
    
    boolean isOnlyParagraph(TextParagraph textparagraph) {
	if (_paragraphVector.count() == 1
	    && _paragraphVector.contains(textparagraph))
	    return true;
	return false;
    }
    
    int selectionStart() {
	return _selection.selectionStart();
    }
    
    TextPositionInfo selectionStartInfo() {
	return _selection.selectionStartInfo();
    }
    
    int selectionEnd() {
	return _selection.selectionEnd();
    }
    
    TextPositionInfo selectionEndInfo() {
	return _selection.selectionEndInfo();
    }
    
    boolean hasSelectionRange() {
	return _selection.isARange();
    }
    
    TextParagraph lastParagraph() {
	return (TextParagraph) _paragraphVector.lastElement();
    }
    
    char characterAt(int i) {
	if (i < 0 || i > _charCount)
	    return '\0';
	TextParagraph textparagraph = _paragraphForIndex(i);
	if (textparagraph == null)
	    textparagraph = lastParagraph();
	return textparagraph.characterAt(i);
    }
    
    int _positionOfPreviousWord(int i) {
	boolean bool = false;
	if (i == 0)
	    return 0;
	char c = characterAt(i--);
	if (c == '\n')
	    return i + 1;
	if (c == ' ' || c == '\t') {
	    do {
		c = characterAt(i--);
		bool = c != ' ' && c != '\t' || c == '\n';
		if (i <= -1)
		    break;
	    } while (!bool);
	} else {
	    do {
		c = characterAt(i--);
		bool = (c == ' ' || c == '\t' || c >= '!' && c <= '/'
			|| c >= ':' && c <= '@' || c >= '[' && c <= '\''
			|| c >= '{' && c <= '~' || c == '\n');
	    } while (i > -1 && !bool);
	}
	if (bool)
	    return i + 2;
	return 0;
    }
    
    int _positionOfNextWord(int i) {
	boolean bool = false;
	if (i >= _charCount)
	    return _charCount;
	if (i > 0) {
	    char c = characterAt(i - 1);
	    if (c == '\n')
		return i - 1;
	}
	char c = characterAt(i++);
	if (c == ' ' || c == '\t') {
	    do {
		char c_145_ = c;
		c = characterAt(i++);
		bool = c != ' ' && c != '\t' || c == '\n';
		if (i >= _charCount)
		    break;
	    } while (!bool);
	} else {
	    do {
		char c_146_ = c;
		c = characterAt(i++);
		bool = (c == ' ' || c == '\t' || c >= '!' && c <= '/'
			|| c >= ':' && c <= '@' || c >= '[' && c <= '\''
			|| c >= '{' && c <= '~' || c == '\n');
	    } while (i < _charCount && !bool);
	}
	if (bool)
	    return i - 1;
	return _charCount;
    }
    
    private void hideInsertionPoint() {
	if (insertionPointVisible)
	    insertionPointVisible = false;
    }
    
    private void showInsertionPoint() {
	if (!insertionPointVisible)
	    insertionPointVisible = true;
    }
    
    static Rect newRect(int i, int i_147_, int i_148_, int i_149_) {
	Rect rect;
	synchronized (_rectCache) {
	    if (!_shouldCache || _rectCache.isEmpty())
		return new Rect(i, i_147_, i_148_, i_149_);
	    rect = (Rect) _rectCache.removeLastElement();
	}
	rect.setBounds(i, i_147_, i_148_, i_149_);
	return rect;
    }
    
    static Rect newRect(Rect rect) {
	Rect rect_150_;
	synchronized (_rectCache) {
	    if (!_shouldCache || _rectCache.isEmpty())
		return new Rect(rect);
	    rect_150_ = (Rect) _rectCache.removeLastElement();
	}
	rect_150_.setBounds(rect);
	return rect_150_;
    }
    
    static Rect newRect() {
	return newRect(0, 0, 0, 0);
    }
    
    static void returnRect(Rect rect) {
	if (rect != null && _shouldCache) {
	    synchronized (_rectCache) {
		if (_rectCache.count() < 50)
		    _rectCache.addElement(rect);
	    }
	}
    }
    
    static void returnRects(Vector vector) {
	if (vector != null && _shouldCache) {
	    int i = vector.count();
	    while (i-- > 0)
		returnRect((Rect) vector.elementAt(i));
	    vector.removeAllElements();
	}
    }
    
    static void setShouldCacheRects(boolean bool) {
	synchronized (_rectCache) {
	    _shouldCache = bool;
	    if (!_shouldCache)
		_rectCache.removeAllElements();
	}
    }
    
    static Vector newVector() {
	Vector vector;
	synchronized (_vectorCache) {
	    if (!_shouldCache || _vectorCache.isEmpty())
		return new Vector();
	    vector = (Vector) _vectorCache.removeLastElement();
	}
	return vector;
    }
    
    static void returnVector(Vector vector) {
	if (_shouldCache) {
	    synchronized (_vectorCache) {
		if (vector != null && _vectorCache.count() < 15) {
		    vector.removeAllElements();
		    _vectorCache.addElement(vector);
		}
	    }
	}
    }
    
    static void setShouldCacheVectors(boolean bool) {
	synchronized (_vectorCache) {
	    _shouldCache = bool;
	    if (!_cacheVectors)
		_vectorCache.removeAllElements();
	}
    }
    
    private void _selectionChanged() {
	int i = _selection.selectionStart();
	int i_151_ = _selection.selectionEnd();
	if (i != _wasSelectedRange.index
	    || i_151_ - i != _wasSelectedRange.length) {
	    _wasSelectedRange.index = i;
	    _wasSelectedRange.length = i_151_ - i;
	    clearTypingAttributes();
	    if (_owner != null)
		_owner.selectionDidChange(this);
	}
    }
    
    void dirtyRange(Range range) {
	if (!this.isDirty() || dirtyRect != null) {
	    Range range_152_ = allocateRange(range);
	    Rect rect = new Rect();
	    this.computeVisibleRect(rect);
	    range_152_.intersectWith(0, length());
	    if (_superview != null && range_152_ != null
		&& !range_152_.isNullRange() && range_152_.length > 0) {
		Vector vector = rectsForRange(range_152_, rect);
		int i = 0;
		for (int i_153_ = vector.count(); i < i_153_; i++) {
		    Rect rect_154_ = (Rect) vector.elementAt(i);
		    rect_154_.x = 0;
		    rect_154_.width = bounds.width;
		    if (rect_154_.width > 0 && rect_154_.height > 0)
			this.addDirtyRect(rect_154_);
		}
	    }
	    recycleRange(range_152_);
	}
    }
    
    private TextParagraphFormat _formatForTextPositionInfo
	(TextPositionInfo textpositioninfo) {
	TextParagraphFormat textparagraphformat
	    = textpositioninfo._textRun.paragraph().format();
	if (textparagraphformat == null)
	    textparagraphformat
		= ((TextParagraphFormat)
		   _defaultAttributes.get("ParagraphFormatKey"));
	return textparagraphformat;
    }
    
    private TextPositionInfo positionInfoForNextLine
	(TextPositionInfo textpositioninfo) {
	int i = textpositioninfo._textRun.paragraph()
		    .characterStartingLine(textpositioninfo._lineNumber + 1);
	if (i == -1) {
	    int i_155_
		= (_paragraphVector
		       .indexOfIdentical(textpositioninfo._textRun.paragraph())
		   + 1);
	    if (i_155_ < _paragraphVector.count()) {
		TextParagraph textparagraph
		    = (TextParagraph) _paragraphVector.elementAt(i_155_);
		return positionInfoForIndex(textparagraph._startChar);
	    }
	    return null;
	}
	TextPositionInfo textpositioninfo_156_ = positionInfoForIndex(i);
	return textpositioninfo_156_;
    }
    
    private TextStyleRun runBefore(TextStyleRun textstylerun) {
	TextStyleRun textstylerun_157_
	    = textstylerun.paragraph().runBefore(textstylerun);
	if (textstylerun_157_ == null) {
	    int i
		= _paragraphVector.indexOfIdentical(textstylerun.paragraph());
	    if (i > 0)
		return ((TextParagraph) _paragraphVector.elementAt(i - 1))
			   .lastRun();
	}
	return textstylerun_157_;
    }
    
    private TextStyleRun runAfter(TextStyleRun textstylerun) {
	TextStyleRun textstylerun_158_
	    = textstylerun.paragraph().runAfter(textstylerun);
	if (textstylerun_158_ == null) {
	    int i
		= _paragraphVector.indexOfIdentical(textstylerun.paragraph());
	    if (i < _paragraphVector.count() - 1)
		return ((TextParagraph) _paragraphVector.elementAt(i + 1))
			   .firstRun();
	}
	return textstylerun_158_;
    }
    
    private Range linkRangeForPosition(int i) {
	TextStyleRun textstylerun = _runForIndex(i);
	Hashtable hashtable;
	String string;
	if ((hashtable = textstylerun.attributes()) != null
	    && (string = (String) hashtable.get("LinkKey")) != null) {
	    TextStyleRun textstylerun_160_;
	    TextStyleRun textstylerun_159_ = textstylerun_160_ = textstylerun;
	    for (;;) {
		textstylerun_159_ = runBefore(textstylerun_159_);
		if (textstylerun_159_ == null
		    || (hashtable = textstylerun_159_.attributes()) == null
		    || !string.equals((String) hashtable.get("LinkKey")))
		    break;
		textstylerun_160_ = textstylerun_159_;
	    }
	    TextStyleRun textstylerun_161_;
	    textstylerun_159_ = textstylerun_161_ = textstylerun;
	    for (;;) {
		textstylerun_159_ = runAfter(textstylerun_159_);
		if (textstylerun_159_ == null
		    || (hashtable = textstylerun_159_.attributes()) == null
		    || !string.equals((String) hashtable.get("LinkKey")))
		    break;
		textstylerun_161_ = textstylerun_159_;
	    }
	    Range range = textstylerun_160_.range();
	    range.unionWith(textstylerun_161_.range());
	    return range;
	}
	return null;
    }
    
    private void highlightLinkWithRange(Range range, boolean bool) {
	if (bool)
	    addAttributeForRange("_IFCLinkPressedKey", "", range);
	else
	    removeAttributeForRange("_IFCLinkPressedKey", range);
	Range range_162_ = paragraphsRangeForRange(range);
	int i = range_162_.index;
	for (int i_163_ = range_162_.index + range_162_.length; i < i_163_;
	     i++)
	    ((TextParagraph) _paragraphVector.elementAt(i)).collectEmptyRuns();
    }
    
    private boolean runUnderMouse(TextStyleRun textstylerun, int i,
				  int i_164_) {
	Vector vector = rectsForRange(textstylerun.range());
	int i_165_ = 0;
	for (int i_166_ = vector.count(); i_165_ < i_166_; i_165_++) {
	    if (((Rect) vector.elementAt(i_165_)).contains(i, i_164_))
		return true;
	}
	return false;
    }
    
    private boolean runsUnderMouse(Vector vector, int i, int i_167_) {
	int i_168_ = 0;
	for (int i_169_ = vector.count(); i_168_ < i_169_; i_168_++) {
	    Range range = (Range) vector.elementAt(i_168_);
	    TextStyleRun textstylerun = _runForIndex(range.index);
	    if (runUnderMouse(textstylerun, i, i_167_))
		return true;
	}
	return false;
    }
    
    boolean lastParagraphIsEmpty() {
	return lastParagraph()._charCount == 0;
    }
    
    char charAt(int i) {
	String string = stringForRange(new Range(i, 1));
	if (string != null && string.length() > 0)
	    return string.charAt(0);
	return '\0';
    }
    
    boolean isWordCharacter(char c) {
	if (c >= '0' && c <= '9' || c >= 'A' && c <= 'Z'
	    || c >= 'a' && c <= 'z')
	    return true;
	return false;
    }
    
    Range groupForIndex(int i) {
	int i_170_ = length();
	int i_171_ = i;
	char c = charAt(i_171_);
	if (c == '\n')
	    return new Range(i_171_, 1);
	if (c == ' ' || c == '\t') {
	    for (/**/; i_171_ > 0; i_171_--) {
		c = charAt(i_171_);
		if (c != ' ' && c != '\t')
		    break;
	    }
	    int i_172_ = i_171_ + 1;
	    for (i_171_ = i; i_171_ < length(); i_171_++) {
		c = charAt(i_171_);
		if (c != ' ' && c != '\t')
		    break;
	    }
	    int i_173_ = i_171_ - 1;
	    return new Range(i_172_, i_173_ - i_172_ + 1);
	}
	if (!isWordCharacter(c))
	    return new Range(i, 1);
	int i_174_;
	for (i_174_ = i_171_; i_174_ > 0; i_174_--) {
	    c = charAt(i_174_ - 1);
	    if (!isWordCharacter(c))
		break;
	}
	int i_175_;
	for (i_175_ = i_171_; i_175_ < i_170_ - 1; i_175_++) {
	    c = charAt(i_175_ + 1);
	    if (!isWordCharacter(c))
		break;
	}
	return new Range(i_174_, i_175_ - i_174_ + 1);
    }
    
    Range lineForPosition(TextPositionInfo textpositioninfo) {
	TextParagraph textparagraph = textpositioninfo._textRun._paragraph;
	if (textparagraph != null) {
	    int i;
	    if (textpositioninfo._lineNumber == 0)
		i = textparagraph._startChar;
	    else
		i = (textparagraph._startChar
		     + (textparagraph._lineBreaks
			[textpositioninfo._lineNumber - 1]));
	    int i_176_
		= (textparagraph._startChar
		   + textparagraph._lineBreaks[textpositioninfo._lineNumber]
		   - 1);
	    return new Range(i, i_176_ - i + 1);
	}
	return new Range();
    }
    
    void replaceContentWithString(String string) {
	notifyAttachmentsForRange(new Range(0, length()), false);
	_paragraphVector.removeAllElements();
	int i = 0;
	int i_177_ = string.length();
	while (i < i_177_) {
	    int i_178_ = string.indexOf('\n', i);
	    int i_179_;
	    int i_180_;
	    if (i_178_ == -1) {
		i_179_ = i;
		i_180_ = i_177_;
		i = i_177_;
	    } else {
		i_179_ = i;
		i_180_ = i_178_;
		i = i_178_ + 1;
	    }
	    TextParagraph textparagraph = new TextParagraph(this);
	    TextStyleRun textstylerun = new TextStyleRun(textparagraph, string,
							 i_179_, i_180_, null);
	    textparagraph.addRun(textstylerun);
	    _paragraphVector.addElement(textparagraph);
	}
	if (_paragraphVector.count() == 0
	    || string.charAt(i_177_ - 1) == '\n') {
	    TextParagraph textparagraph = new TextParagraph(this);
	    TextStyleRun textstylerun
		= new TextStyleRun(textparagraph, "", null);
	    textparagraph.addRun(textstylerun);
	    _paragraphVector.addElement(textparagraph);
	}
	this.setDirty(true);
	reformatAll();
    }
    
    Vector rectsForRange(Range range, Rect rect) {
	Range range_181_ = allocateRange(range.index, range.length);
	Vector vector = new Vector();
	range_181_.intersectWith(0, length());
	if (range_181_.length == 0 || range_181_.isNullRange()) {
	    recycleRange(range_181_);
	    return vector;
	}
	TextPositionInfo textpositioninfo
	    = positionInfoForIndex(range_181_.index);
	if (textpositioninfo._endOfLine && !textpositioninfo._endOfParagraph)
	    textpositioninfo.representCharacterAfterEndOfLine();
	TextPositionInfo textpositioninfo_182_
	    = positionInfoForIndex(range_181_.index + range_181_.length());
	if (textpositioninfo == null || textpositioninfo_182_ == null) {
	    recycleRange(range_181_);
	    return vector;
	}
	if ((textpositioninfo._textRun.paragraph()
	     == textpositioninfo_182_._textRun.paragraph())
	    && (textpositioninfo._lineNumber
		== textpositioninfo_182_._lineNumber)) {
	    TextParagraphFormat textparagraphformat
		= _formatForTextPositionInfo(textpositioninfo);
	    if (textpositioninfo_182_._endOfLine) {
		TextParagraph textparagraph
		    = textpositioninfo._textRun.paragraph();
		int i = (textparagraph._lineRemainders
			 [textpositioninfo._lineNumber]);
		switch (textparagraphformat._justification) {
		case 1:
		    vector.addElement(new Rect(textpositioninfo._x,
					       textpositioninfo._y,
					       (bounds.width
						- (textparagraphformat
						   ._rightMargin)
						- textpositioninfo._x - i / 2),
					       textpositioninfo._lineHeight));
		    break;
		case 2:
		    vector.addElement(new Rect(textpositioninfo._x,
					       textpositioninfo._y,
					       (bounds.width
						- (textparagraphformat
						   ._rightMargin)
						- textpositioninfo._x),
					       textpositioninfo._lineHeight));
		    break;
		default:
		    vector.addElement(new Rect(textpositioninfo._x,
					       textpositioninfo._y,
					       (bounds.width
						- (textparagraphformat
						   ._rightMargin)
						- textpositioninfo._x - i),
					       textpositioninfo._lineHeight));
		}
	    } else
		vector.addElement(new Rect(textpositioninfo._x,
					   textpositioninfo._y,
					   (textpositioninfo_182_._x
					    - textpositioninfo._x),
					   textpositioninfo._lineHeight));
	    recycleRange(range_181_);
	    return vector;
	}
	TextParagraphFormat textparagraphformat
	    = _formatForTextPositionInfo(textpositioninfo);
	TextParagraph textparagraph = textpositioninfo._textRun.paragraph();
	int i = textparagraph._lineRemainders[textpositioninfo._lineNumber];
	Rect rect_183_;
	switch (textparagraphformat._justification) {
	case 1:
	    rect_183_
		= new Rect(textpositioninfo._x, textpositioninfo._y,
			   (bounds.width - textparagraphformat._rightMargin
			    - textpositioninfo._x - i / 2),
			   textpositioninfo._lineHeight);
	    break;
	case 2:
	    rect_183_
		= new Rect(textpositioninfo._x, textpositioninfo._y,
			   (bounds.width - textparagraphformat._rightMargin
			    - textpositioninfo._x),
			   textpositioninfo._lineHeight);
	    break;
	default:
	    rect_183_
		= new Rect(textpositioninfo._x, textpositioninfo._y,
			   (bounds.width - textparagraphformat._rightMargin
			    - textpositioninfo._x - i),
			   textpositioninfo._lineHeight);
	}
	if (rect_183_.height > 0)
	    vector.addElement(rect_183_);
	rect_183_ = new Rect(0, 0, 0, 0);
	TextPositionInfo textpositioninfo_184_ = textpositioninfo;
	boolean bool = false;
	int i_185_ = -1;
	while (!bool) {
	    textpositioninfo_184_
		= positionInfoForNextLine(textpositioninfo_184_);
	    if (textpositioninfo_184_ == null)
		break;
	    if (textpositioninfo_184_._endOfLine
		&& !textpositioninfo_184_._endOfParagraph)
		textpositioninfo_184_.representCharacterAfterEndOfLine();
	    if (textpositioninfo_184_._y <= i_185_)
		break;
	    i_185_ = textpositioninfo_184_._y;
	    if (rect != null) {
		if (textpositioninfo_184_._y < rect.y)
		    continue;
		if (textpositioninfo_184_._y > rect.y + rect.height) {
		    bool = true;
		    break;
		}
	    }
	    textparagraphformat
		= _formatForTextPositionInfo(textpositioninfo_184_);
	    if ((textpositioninfo_184_._textRun.paragraph()
		 != textpositioninfo_182_._textRun.paragraph())
		|| (textpositioninfo_184_._lineNumber
		    < textpositioninfo_182_._lineNumber)) {
		rect_183_.x = textpositioninfo_184_._x;
		rect_183_.y = textpositioninfo_184_._y;
		textparagraph = textpositioninfo_184_._textRun.paragraph();
		i = (textparagraph._lineRemainders
		     [textpositioninfo_184_._lineNumber]);
		switch (textparagraphformat._justification) {
		case 1:
		    rect_183_.width
			= (bounds.width - textparagraphformat._rightMargin
			   - rect_183_.x - i / 2);
		    break;
		case 2:
		    rect_183_.width
			= (bounds.width - textparagraphformat._rightMargin
			   - rect_183_.x);
		    break;
		default:
		    rect_183_.width
			= (bounds.width - textparagraphformat._rightMargin
			   - rect_183_.x - i);
		}
		rect_183_.height = textpositioninfo_184_._lineHeight;
	    } else {
		rect_183_.x = textpositioninfo_184_._x;
		rect_183_.y = textpositioninfo_184_._y;
		rect_183_.width = textpositioninfo_182_._x - rect_183_.x;
		rect_183_.height = textpositioninfo_184_._lineHeight;
		bool = true;
	    }
	    if (rect_183_.height > 0) {
		Rect rect_186_ = (Rect) vector.lastElement();
		if (rect_186_ != null && rect_186_.x == rect_183_.x
		    && rect_186_.width == rect_183_.width)
		    rect_186_.height
			= rect_183_.y + rect_183_.height - rect_186_.y;
		else
		    vector.addElement(new Rect(rect_183_));
	    }
	}
	recycleRange(range_181_);
	return vector;
    }
    
    Vector rangesOfVisibleAttachmentsWithBitmap(Bitmap bitmap) {
	Vector vector = new Vector();
	Rect rect = new Rect();
	this.computeVisibleRect(rect);
	TextPositionInfo textpositioninfo
	    = positionForPoint(rect.x, rect.y, true);
	int i;
	if (textpositioninfo == null)
	    i = 0;
	else if (textpositioninfo._absPosition > 0)
	    i = textpositioninfo._absPosition - 1;
	else
	    i = 0;
	textpositioninfo = positionForPoint(rect.x + rect.width,
					    rect.y + rect.height, true);
	int i_187_;
	if (textpositioninfo == null)
	    i_187_ = length() - 1;
	else if (textpositioninfo._absPosition < length() - 1)
	    i_187_ = textpositioninfo._absPosition + 1;
	else
	    i_187_ = length() - 1;
	TextStyleRun textstylerun = _runForIndex(i);
	while (textstylerun != null) {
	    Hashtable hashtable = textstylerun.attributes();
	    TextAttachment textattachment;
	    if (hashtable != null
		&& ((textattachment
		     = (TextAttachment) hashtable.get("TextAttachmentKey"))
		    != null)
		&& textattachment instanceof ImageAttachment
		&& ((ImageAttachment) textattachment).image() == bitmap)
		vector.addElement(textstylerun.range());
	    textstylerun = runAfter(textstylerun);
	    if (textstylerun.rangeIndex() > i_187_)
		break;
	}
	return vector;
    }
    
    void refreshBitmap(Object object) {
	Bitmap bitmap = (Bitmap) object;
	Vector vector = rangesOfVisibleAttachmentsWithBitmap(bitmap);
	int i = 0;
	for (int i_188_ = vector.count(); i < i_188_; i++) {
	    Range range = (Range) vector.elementAt(i);
	    if (!range.isNullRange()) {
		Vector vector_189_ = rectsForRange(range);
		Rect rect = bitmap.updateRect();
		if (vector_189_.count() > 0) {
		    Rect rect_190_ = (Rect) vector_189_.elementAt(0);
		    int i_191_ = 1;
		    for (int i_192_ = vector_189_.count(); i_191_ < i_192_;
			 i_191_++)
			rect_190_
			    .unionWith((Rect) vector_189_.elementAt(i_191_));
		    rect_190_.x = 0;
		    rect_190_.width = bounds.width;
		    rect_190_.y += rect.y;
		    rect_190_.height = rect.height;
		    this.addDirtyRect(rect_190_);
		}
	    }
	}
    }
    
    boolean attributesChangingFormatting(Hashtable hashtable) {
	if (hashtable != null) {
	    Vector vector = hashtable.keysVector();
	    int i = 0;
	    for (int i_193_ = vector.count(); i < i_193_; i++) {
		if (attributesChangingFormatting.indexOf(vector.elementAt(i))
		    != -1)
		    return true;
	    }
	}
	return false;
    }
    
    void clearTypingAttributes() {
	if (_typingAttributes != null)
	    _typingAttributes.clear();
    }
    
    void addAttributesForRangeWithoutNotification(Hashtable hashtable,
						  Range range) {
	Range range_194_ = selectedRange();
	Range range_195_ = allocateRange();
	Vector vector = new Vector();
	if (hashtable == null)
	    recycleRange(range_195_);
	else {
	    TextAttachment textattachment;
	    if ((textattachment
		 = (TextAttachment) hashtable.get("TextAttachmentKey"))
		!= null)
		textattachment.setOwner(this);
	    TextParagraphFormat textparagraphformat;
	    if ((textparagraphformat
		 = (TextParagraphFormat) hashtable.get("ParagraphFormatKey"))
		!= null) {
		Range range_196_ = paragraphsRangeForRange(range);
		int i = range_196_.index;
		for (int i_197_ = range_196_.index + range_196_.length;
		     i < i_197_; i++) {
		    TextParagraph textparagraph
			= (TextParagraph) _paragraphVector.elementAt(i);
		    textparagraph.setFormat(textparagraphformat);
		    vector.addElementIfAbsent(textparagraph);
		    range_195_.unionWith(textparagraph.range());
		}
		if (hashtable.count() == 1) {
		    i = 0;
		    for (int i_198_ = vector.count(); i < i_198_; i++)
			formatParagraph((TextParagraph) vector.elementAt(i));
		    dirtyRange(range_195_);
		    if (formattingEnabled())
			_selection.setRange(range_194_.index,
					    (range_194_.index
					     + range_194_.length));
		    recycleRange(range_195_);
		    recycleRange(range_196_);
		    return;
		}
		recycleRange(range_196_);
	    }
	    TextStyleRun textstylerun = _runForIndex(range.index);
	    if (textstylerun != null) {
		Range range_199_ = textstylerun.range();
		if (range.equals(range_199_)) {
		    textstylerun.appendAttributes(hashtable);
		    range_195_.unionWith(textstylerun.range());
		    if (attributesChangingFormatting(hashtable))
			vector.addElementIfAbsent(_paragraphForIndex(range
								     .index));
		    int i = 0;
		    for (int i_200_ = vector.count(); i < i_200_; i++) {
			TextParagraph textparagraph
			    = (TextParagraph) vector.elementAt(i);
			formatParagraph(textparagraph);
			range_195_.unionWith(textparagraph.range());
		    }
		    dirtyRange(range_195_);
		    if (formattingEnabled())
			_selection.setRange(range_194_.index,
					    (range_194_.index
					     + range_194_.length));
		    recycleRange(range_195_);
		    recycleRange(range_199_);
		    return;
		}
		if (range.index >= range_199_.index
		    && (range.index + range.length
			<= range_199_.index + range_199_.length)
		    && equalsAttributesHint(hashtable,
					    textstylerun.attributes())) {
		    recycleRange(range_195_);
		    recycleRange(range_199_);
		    return;
		}
	    }
	    Vector vector_201_ = createAndReturnRunsForRange(range);
	    int i = 0;
	    for (int i_202_ = vector_201_.count(); i < i_202_; i++) {
		textstylerun = (TextStyleRun) vector_201_.elementAt(i);
		textstylerun.appendAttributes(hashtable);
		Range range_203_ = textstylerun.range();
		range_195_.unionWith(range_203_);
		recycleRange(range_203_);
	    }
	    if (attributesChangingFormatting(hashtable)) {
		Range range_204_ = paragraphsRangeForRange(range);
		i = range_204_.index;
		for (int i_205_ = range_204_.index + range_204_.length;
		     i < i_205_; i++)
		    vector.addElementIfAbsent(_paragraphVector.elementAt(i));
		recycleRange(range_204_);
	    }
	    i = 0;
	    for (int i_206_ = vector.count(); i < i_206_; i++) {
		TextParagraph textparagraph
		    = (TextParagraph) vector.elementAt(i);
		formatParagraph(textparagraph);
		Range range_207_ = textparagraph.range();
		range_195_.unionWith(range_207_);
		recycleRange(range_207_);
	    }
	    dirtyRange(range_195_);
	    if (formattingEnabled())
		_selection.setRange(range_194_.index,
				    range_194_.index + range_194_.length);
	    recycleRange(range_195_);
	}
    }
    
    void validateHTMLParsingRules() {
	if (_htmlParsingRules == null) {
	    String[] strings
		= { "BODY", "H1", "H2", "H3", "H4", "H5", "H6", "B", "STRONG",
		    "CENTER", "EM", "I", "PRE", "A", "OL", "UL", "LI",
		    "ADDRESS", "BLOCKQUOTE", "DIR", "MENU", "TT", "SAMP",
		    "CODE", "KBD", "VAR", "CITE", "DL", "DT", "DD", "TITLE",
		    "P" };
	    String[] strings_208_ = { "BR", "HR", "IMG" };
	    _htmlParsingRules = new HTMLParsingRules();
	    int i = 0;
	    for (int i_209_ = strings.length; i < i_209_; i++)
		_htmlParsingRules.setClassNameForMarker
		    ("COM.stagecast.ifc.netscape.application.TextViewHTMLContainerImp",
		     strings[i]);
	    i = 0;
	    for (int i_210_ = strings_208_.length; i < i_210_; i++)
		_htmlParsingRules.setClassNameForMarker
		    ("COM.stagecast.ifc.netscape.application.TextViewHTMLMarkerImp",
		     strings_208_[i]);
	    _htmlParsingRules.setStringClassName
		("COM.stagecast.ifc.netscape.application.TextViewHTMLString");
	}
    }
    
    void disableAttachmentNotification() {
	notifyAttachmentDisabled++;
    }
    
    void enableAttachmentNotification() {
	notifyAttachmentDisabled--;
	if (notifyAttachmentDisabled < 0)
	    notifyAttachmentDisabled = 0;
	if (notifyAttachmentDisabled == 0 && invalidAttachmentRange != null) {
	    notifyAttachmentsForRange(invalidAttachmentRange, true);
	    invalidAttachmentRange = null;
	}
    }
    
    void _notifyAttachmentsForRange(Range range, boolean bool) {
	int i = _paragraphIndexForIndex(range.index);
	int i_211_ = range.index;
	int i_212_ = range.index + range.length;
	if (i != -1) {
	    TextParagraph textparagraph
		= (TextParagraph) _paragraphVector.elementAt(i);
	    int i_213_ = textparagraph.runIndexForCharPosition(i_211_);
	    if (i_213_ != -1) {
		TextStyleRun textstylerun
		    = ((TextStyleRun)
		       textparagraph._runVector.elementAt(i_213_));
		i_211_ = textstylerun.rangeIndex();
		while (i_211_ < i_212_) {
		    Hashtable hashtable = textstylerun.attributes();
		    TextAttachment textattachment;
		    if (hashtable != null
			&& (textattachment
			    = ((TextAttachment)
			       hashtable.get("TextAttachmentKey"))) != null) {
			if (bool) {
			    TextPositionInfo textpositioninfo
				= (textstylerun._paragraph._infoForPosition
				   (textstylerun.rangeIndex()));
			    if (textpositioninfo != null) {
				textpositioninfo
				    .representCharacterAfterEndOfLine();
				Rect rect
				    = (textstylerun
					   .textAttachmentBoundsForOrigin
				       (textpositioninfo._x,
					textpositioninfo._y,
					(textstylerun._paragraph._baselines
					 [textpositioninfo._lineNumber])));
				textattachment._willShowWithBounds(rect);
			    }
			} else
			    textattachment._willHide();
		    }
		    i_211_ += textstylerun.charCount();
		    if (++i_213_ < textparagraph._runVector.count())
			textstylerun = (TextStyleRun) textparagraph
							  ._runVector
							  .elementAt(i_213_);
		    else {
			i++;
			i_211_++;
			if (i >= _paragraphVector.count())
			    break;
			textparagraph
			    = (TextParagraph) _paragraphVector.elementAt(i);
			i_213_ = 0;
			if (textparagraph._runVector.count() <= 0)
			    break;
			textstylerun = (TextStyleRun) textparagraph
							  ._runVector
							  .elementAt(i_213_);
		    }
		}
	    }
	}
    }
    
    void notifyAttachmentsForRange(Range range, boolean bool) {
	if (bool == false)
	    _notifyAttachmentsForRange(range, false);
	else if (notifyAttachmentDisabled > 0) {
	    if (invalidAttachmentRange != null)
		invalidAttachmentRange.unionWith(range);
	    else
		invalidAttachmentRange = new Range(range);
	} else
	    _notifyAttachmentsForRange(range, true);
    }
    
    boolean isLeftHalfOfCharacter(int i, int i_214_) {
	TextPositionInfo textpositioninfo = positionForPoint(i, i_214_, false);
	TextPositionInfo textpositioninfo_215_
	    = positionForPoint(i, i_214_, true);
	if (textpositioninfo == null || textpositioninfo_215_ == null)
	    return true;
	if (textpositioninfo._absPosition
	    == textpositioninfo_215_._absPosition)
	    return true;
	return false;
    }
    
    static Range allocateRange() {
	return allocateRange(Range.nullRange().index,
			     Range.nullRange().length);
    }
    
    static Range allocateRange(Range range) {
	return allocateRange(range.index, range.length);
    }
    
    static Range allocateRange(int i, int i_216_) {
	Range range = (Range) rangePool.allocateObject();
	range.index = i;
	range.length = i_216_;
	return range;
    }
    
    static void recycleRange(Range range) {
	rangePool.recycleObject(range);
    }
    
    public void copy() {
	Application.setClipboardText(stringForRange(selectedRange()));
    }
    
    public void cut() {
	if (isEditable()) {
	    Range range = selectedRange();
	    Application.setClipboardText(stringForRange(range));
	    replaceRangeWithString(range, "");
	    selectRange(new Range(range.index(), 0));
	}
    }
    
    public void paste() {
	if (isEditable()) {
	    Range range = selectedRange();
	    String string = Application.clipboardText();
	    if (range != null && range.index != -1 && string != null) {
		_selection.disableInsertionPoint();
		replaceRangeWithString(range, string);
		range = new Range(range.index() + string.length(), 0);
		selectRange(range);
		scrollRangeToVisible(range);
		_selection.enableInsertionPoint();
	    }
	}
    }
    
    public String formElementText() {
	return string();
    }
}
