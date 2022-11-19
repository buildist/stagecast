/* TextSelection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

class TextSelection implements Target
{
    TextView _owner;
    private TextParagraph _editParagraph;
    TextPositionInfo _insertionPointInfo;
    TextPositionInfo _anchorPositionInfo;
    TextPositionInfo _endPositionInfo;
    Timer _blinkTimer;
    long _lastFlashTime;
    int _anchorPosition;
    int _endPosition;
    boolean _flashInsertionPoint;
    boolean _insertionPointShowing;
    boolean _ignoreNextFlash;
    boolean _selectionDefined = false;
    int _insertionPointDisabled;
    
    TextSelection() {
	/* empty */
    }
    
    TextSelection(TextView textview) {
	this();
	init(textview);
	_selectionDefined = false;
    }
    
    void init(TextView textview) {
	_owner = textview;
    }
    
    void _startFlashing() {
	if (_insertionPointDisabled == 0 && _owner.isEditable()
	    && _selectionDefined) {
	    if (_blinkTimer == null)
		_blinkTimer = new Timer(this, "blinkCaret", 700);
	    if (_owner.isEditing())
		_blinkTimer.start();
	    else if (System.currentTimeMillis() - _lastFlashTime > 350L)
		_ignoreNextFlash = true;
	    showInsertionPoint();
	}
    }
    
    void _stopFlashing() {
	hideInsertionPoint();
	if (_blinkTimer != null) {
	    _blinkTimer.stop();
	    _ignoreNextFlash = false;
	}
    }
    
    public void performCommand(String string, Object object) {
	if (_owner.isEditing() && _selectionDefined) {
	    if (_ignoreNextFlash || !_flashInsertionPoint || isARange())
		_ignoreNextFlash = false;
	    else {
		_lastFlashTime = _blinkTimer.timeStamp();
		_insertionPointShowing = _insertionPointShowing ^ true;
		_owner.drawInsertionPoint();
	    }
	} else if (_blinkTimer != null)
	    _blinkTimer.stop();
    }
    
    boolean isARange() {
	return _insertionPointInfo == null && _anchorPositionInfo != null;
    }
    
    void disableInsertionPoint() {
	if (_insertionPointDisabled == 0)
	    hideInsertionPoint();
	_insertionPointDisabled++;
    }
    
    void enableInsertionPoint() {
	_insertionPointDisabled--;
	if (_insertionPointDisabled == 0)
	    showInsertionPoint();
    }
    
    void showInsertionPoint() {
	if (_insertionPointDisabled == 0 && _owner.isEditable()
	    && _owner.isEditing()) {
	    _flashInsertionPoint = true;
	    if (!isARange() && !_insertionPointShowing) {
		_insertionPointShowing = true;
		_owner.drawInsertionPoint();
		_startFlashing();
	    }
	}
    }
    
    void hideInsertionPoint() {
	_flashInsertionPoint = false;
	if (!isARange() && _insertionPointShowing) {
	    _insertionPointShowing = false;
	    _owner.drawInsertionPoint();
	}
    }
    
    Rect insertionPointRect() {
	if (_insertionPointInfo == null)
	    return TextView.newRect();
	return TextView.newRect((_insertionPointInfo._x > 0
				 ? _insertionPointInfo._x - 1
				 : _insertionPointInfo._x),
				_insertionPointInfo._y, 1,
				_insertionPointInfo._lineHeight);
    }
    
    Rect _updateRectForInfo(int i, int i_0_, TextPositionInfo textpositioninfo,
			    TextPositionInfo textpositioninfo_1_) {
	if (i == i_0_)
	    return null;
	Rect rect;
	if (textpositioninfo._y == textpositioninfo_1_._y) {
	    if (textpositioninfo_1_._x < textpositioninfo._x)
		rect = TextView.newRect(textpositioninfo_1_._x,
					textpositioninfo_1_._y,
					(textpositioninfo._x
					 - textpositioninfo_1_._x),
					textpositioninfo_1_._lineHeight);
	    else
		rect = TextView.newRect(textpositioninfo._x,
					textpositioninfo_1_._y,
					(textpositioninfo_1_._x
					 - textpositioninfo._x),
					textpositioninfo_1_._lineHeight);
	} else {
	    rect = textpositioninfo.lineBounds();
	    Rect rect_2_ = textpositioninfo_1_.lineBounds();
	    rect.unionWith(rect_2_);
	    TextView.returnRect(rect_2_);
	}
	return rect;
    }
    
    void setRange(int i, int i_3_, TextPositionInfo textpositioninfo,
		  boolean bool, boolean bool_4_) {
	if (i == -1 || i_3_ == -1)
	    _selectionDefined = false;
	else
	    _selectionDefined = true;
	int i_5_;
	TextPositionInfo textpositioninfo_6_;
	TextPositionInfo textpositioninfo_7_;
	if (_anchorPosition != _endPosition) {
	    i_5_ = (_anchorPosition < _endPosition ? _anchorPosition
		    : _endPosition);
	    textpositioninfo_6_ = (_anchorPosition < _endPosition
				   ? _anchorPositionInfo : _endPositionInfo);
	    int i_8_ = (_anchorPosition > _endPosition ? _anchorPosition
			: _endPosition);
	    textpositioninfo_7_ = (_anchorPosition > _endPosition
				   ? _anchorPositionInfo : _endPositionInfo);
	} else {
	    int i_9_;
	    i_5_ = i_9_ = -1;
	    textpositioninfo_6_ = textpositioninfo_7_ = null;
	}
	if (i < 0)
	    i = 0;
	else if (i >= _owner._charCount)
	    i = _owner._charCount - 1;
	if (i_3_ < 0)
	    i_3_ = 0;
	else if (i_3_ >= _owner._charCount)
	    i_3_ = _owner._charCount - 1;
	_anchorPosition = i;
	_endPosition = i_3_;
	if (_anchorPosition == _endPosition) {
	    _editParagraph = _owner._paragraphForIndex(_anchorPosition);
	    _insertionPointInfo
		= _editParagraph.infoForPosition(_anchorPosition, -1);
	    if (bool_4_ && _insertionPointInfo._endOfLine
		&& _anchorPosition < _owner.length() - 1) {
		TextParagraph textparagraph
		    = _owner._paragraphForIndex(_anchorPosition + 1);
		if (textparagraph == _editParagraph) {
		    TextPositionInfo textpositioninfo_10_
			= _editParagraph.infoForPosition(_anchorPosition + 1,
							 -1);
		    if (textpositioninfo_10_._y > _insertionPointInfo._y)
			_insertionPointInfo
			    = (_editParagraph.infoForPosition
			       (_anchorPosition, textpositioninfo_10_._y));
		}
	    }
	    _anchorPositionInfo = _endPositionInfo = null;
	} else {
	    TextParagraph textparagraph
		= _owner._paragraphForIndex(_anchorPosition);
	    _anchorPositionInfo
		= textparagraph.infoForPosition(_anchorPosition, -1);
	    if (_anchorPositionInfo._endOfLine && !bool)
		_anchorPositionInfo
		    = textparagraph.infoForPosition(_anchorPosition,
						    _anchorPositionInfo
							.maxY());
	    if (textpositioninfo == null)
		_endPositionInfo = _owner._paragraphForIndex(_endPosition)
				       .infoForPosition(_endPosition, -1);
	    else
		_endPositionInfo = textpositioninfo;
	    _insertionPointInfo = null;
	    _editParagraph = null;
	}
	int i_11_;
	TextPositionInfo textpositioninfo_12_;
	TextPositionInfo textpositioninfo_13_;
	if (_anchorPosition != _endPosition) {
	    i_11_ = (_anchorPosition < _endPosition ? _anchorPosition
		     : _endPosition);
	    textpositioninfo_12_ = (_anchorPosition < _endPosition
				    ? _anchorPositionInfo : _endPositionInfo);
	    int i_14_ = (_anchorPosition > _endPosition ? _anchorPosition
			 : _endPosition);
	    textpositioninfo_13_ = (_anchorPosition > _endPosition
				    ? _anchorPositionInfo : _endPositionInfo);
	} else {
	    int i_15_;
	    i_11_ = i_15_ = -1;
	    textpositioninfo_12_ = textpositioninfo_13_ = null;
	}
	if (i_5_ == -1) {
	    if (i_11_ == -1) {
		_startFlashing();
		_updateCurrentFont();
	    } else {
		dirtyRangeForSelection(_anchorPositionInfo, _endPositionInfo,
				       null, null);
		_updateCurrentFont();
	    }
	} else if (i_11_ == -1) {
	    dirtyRangeForSelection(textpositioninfo_6_, textpositioninfo_7_,
				   null, null);
	    _startFlashing();
	    _updateCurrentFont();
	} else {
	    dirtyRangeForSelection(textpositioninfo_12_, textpositioninfo_13_,
				   textpositioninfo_6_, textpositioninfo_7_);
	    _updateCurrentFont();
	}
    }
    
    void dirtyRangeForSelection(TextPositionInfo textpositioninfo,
				TextPositionInfo textpositioninfo_16_,
				TextPositionInfo textpositioninfo_17_,
				TextPositionInfo textpositioninfo_18_) {
	Rect rect = null;
	Rect rect_19_ = _owner.bounds();
	Range range
	    = Range.rangeFromIndices(textpositioninfo._absPosition,
				     textpositioninfo_16_._absPosition);
	Range range_20_;
	if (textpositioninfo_17_ == null || textpositioninfo_18_ == null)
	    range_20_ = range;
	else {
	    Range range_21_
		= Range.rangeFromIndices(textpositioninfo_17_._absPosition,
					 textpositioninfo_18_._absPosition);
	    if (range_21_.equals(range))
		return;
	    if (range.index == range_21_.index) {
		if (range.length > range_21_.length)
		    range_20_ = new Range(range.index + range_21_.length,
					  range.length - range_21_.length);
		else
		    range_20_ = new Range(range.index + range.length,
					  range_21_.length - range.length);
	    } else if (range.index + range.length
		       == range_21_.index + range_21_.length) {
		if (range.length > range_21_.length)
		    range_20_ = new Range(range.index,
					  range.length - range_21_.length);
		else
		    range_20_ = new Range(range_21_.index,
					  range_21_.length - range.length);
	    } else {
		dirtyRangeForSelection(textpositioninfo, textpositioninfo_16_,
				       null, null);
		dirtyRangeForSelection(textpositioninfo_17_,
				       textpositioninfo_18_, null, null);
		return;
	    }
	}
	Vector vector = _owner.rectsForRange(range_20_);
	int i = 0;
	for (int i_22_ = vector.count(); i < i_22_; i++) {
	    Rect rect_23_ = (Rect) vector.elementAt(i);
	    rect_23_.x = 0;
	    rect_23_.width = rect_19_.width;
	    if (rect_23_.height > 0) {
		if (rect == null)
		    rect = new Rect(rect_23_);
		else
		    rect.unionWith(rect_23_);
	    }
	}
	if (rect != null) {
	    if (range_20_.contains(textpositioninfo._absPosition)
		&& !rect.contains(textpositioninfo._x, textpositioninfo._y)
		&& textpositioninfo._absPosition > 0) {
		TextPositionInfo textpositioninfo_24_
		    = _owner.positionInfoForIndex(textpositioninfo._absPosition
						  - 1);
		if (textpositioninfo_24_ != null) {
		    rect.y -= textpositioninfo_24_._lineHeight;
		    rect.height += textpositioninfo_24_._lineHeight;
		}
	    }
	    if (range_20_.contains(textpositioninfo_16_._absPosition)
		&& !rect.contains(textpositioninfo_16_._x,
				  textpositioninfo_16_._y)
		&& textpositioninfo_16_._absPosition > 0) {
		TextPositionInfo textpositioninfo_25_
		    = _owner.positionInfoForIndex((textpositioninfo_16_
						   ._absPosition) - 1);
		if (textpositioninfo_25_ != null) {
		    rect.y -= textpositioninfo_25_._lineHeight;
		    rect.height += textpositioninfo_25_._lineHeight;
		}
	    }
	    if (textpositioninfo_17_ != null
		&& range_20_.contains(textpositioninfo_17_._absPosition)
		&& !rect.contains(textpositioninfo_17_._x,
				  textpositioninfo_17_._y)
		&& textpositioninfo_17_._absPosition > 0) {
		TextPositionInfo textpositioninfo_26_
		    = _owner.positionInfoForIndex((textpositioninfo_17_
						   ._absPosition) - 1);
		if (textpositioninfo_26_ != null) {
		    rect.y -= textpositioninfo_26_._lineHeight;
		    rect.height += textpositioninfo_26_._lineHeight;
		}
	    }
	    if (textpositioninfo_18_ != null
		&& range_20_.contains(textpositioninfo_18_._absPosition)
		&& !rect.contains(textpositioninfo_18_._x,
				  textpositioninfo_18_._y)
		&& textpositioninfo_18_._absPosition > 0) {
		TextPositionInfo textpositioninfo_27_
		    = _owner.positionInfoForIndex((textpositioninfo_18_
						   ._absPosition) - 1);
		if (textpositioninfo_27_ != null) {
		    rect.y -= textpositioninfo_27_._lineHeight;
		    rect.height += textpositioninfo_27_._lineHeight;
		}
	    }
	    _owner.addDirtyRect(rect);
	}
    }
    
    void setRange(int i, int i_28_, TextPositionInfo textpositioninfo,
		  boolean bool) {
	setRange(i, i_28_, textpositioninfo, bool, false);
    }
    
    void setRange(int i, int i_29_) {
	setRange(i, i_29_, null, false, false);
    }
    
    void setRange(int i, int i_30_, boolean bool) {
	setRange(i, i_30_, null, false, bool);
    }
    
    void clearRange() {
	hideInsertionPoint();
	setRange(-1, -1);
	_stopFlashing();
    }
    
    void setInsertionPoint(TextPositionInfo textpositioninfo) {
	Rect rect = null;
	if (textpositioninfo != null) {
	    _selectionDefined = true;
	    if (_anchorPosition != _endPosition) {
		rect = _anchorPositionInfo.lineBounds();
		Rect rect_31_ = _endPositionInfo.lineBounds();
		rect.unionWith(rect_31_);
		rect.x = 0;
		rect.width = _owner.bounds.width;
		TextView.returnRect(rect_31_);
	    }
	    _anchorPosition = _endPosition = textpositioninfo._absPosition;
	    _insertionPointInfo = textpositioninfo;
	    _editParagraph = _insertionPointInfo._textRun._paragraph;
	    _anchorPositionInfo = _endPositionInfo = null;
	    if (rect != null) {
		_owner.draw(rect);
		TextView.returnRect(rect);
	    }
	    if (_insertionPointShowing)
		_startFlashing();
	}
    }
    
    int insertionPoint() {
	if (isARange())
	    return -1;
	return _anchorPosition;
    }
    
    TextPositionInfo insertionPointInfo() {
	if (isARange())
	    return null;
	return _insertionPointInfo;
    }
    
    int selectionStart() {
	if (!_selectionDefined)
	    return -1;
	if (_anchorPosition <= _endPosition)
	    return _anchorPosition;
	return _endPosition;
    }
    
    TextPositionInfo selectionStartInfo() {
	if (!isARange())
	    return _insertionPointInfo;
	if (_anchorPosition <= _endPosition)
	    return _anchorPositionInfo;
	return _endPositionInfo;
    }
    
    int selectionEnd() {
	if (!_selectionDefined)
	    return -1;
	if (_endPosition > _anchorPosition)
	    return _endPosition;
	return _anchorPosition;
    }
    
    TextPositionInfo selectionEndInfo() {
	if (!isARange())
	    return _insertionPointInfo;
	if (_endPosition > _anchorPosition)
	    return _endPositionInfo;
	return _anchorPositionInfo;
    }
    
    void _updateCurrentFont() {
	/* empty */
    }
    
    int orderedSelectionStart() {
	if (!_selectionDefined)
	    return -1;
	return _anchorPosition;
    }
    
    TextPositionInfo orderedSelectionStartInfo() {
	if (!isARange())
	    return _insertionPointInfo;
	return _anchorPositionInfo;
    }
    
    int orderedSelectionEnd() {
	if (!_selectionDefined)
	    return -1;
	return _endPosition;
    }
    
    TextPositionInfo orderedSelectionEndInfo() {
	if (!isARange())
	    return _insertionPointInfo;
	return _endPositionInfo;
    }
}
