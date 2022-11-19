/* TextPositionInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class TextPositionInfo
{
    public TextStyleRun _textRun;
    public int _x;
    public int _y;
    public int _lineNumber;
    public int _lineHeight;
    public int _absPosition;
    public int _positionInRun;
    public int _updateLine;
    public boolean _redrawCurrentLineOnly;
    public boolean _redrawCurrentParagraphOnly;
    public boolean _nextLine;
    public boolean _endOfLine;
    public boolean _endOfParagraph;
    
    TextPositionInfo() {
	/* empty */
    }
    
    TextPositionInfo(TextStyleRun textstylerun, int i, int i_0_, int i_1_,
		     int i_2_, int i_3_, int i_4_) {
	this();
	init(textstylerun, i, i_0_, i_1_, i_2_, i_3_, i_4_);
    }
    
    TextPositionInfo(TextPositionInfo textpositioninfo_5_) {
	this();
	init(textpositioninfo_5_._textRun, textpositioninfo_5_._x,
	     textpositioninfo_5_._y, textpositioninfo_5_._lineNumber,
	     textpositioninfo_5_._lineHeight,
	     textpositioninfo_5_._positionInRun,
	     textpositioninfo_5_._absPosition);
    }
    
    public String toString() {
	return ("run is " + _textRun + " x is " + _x + " y is " + _y
		+ " lineNumber is " + _lineNumber + "line height is: "
		+ _lineHeight + "positionInRun is " + _positionInRun
		+ "position is:" + _absPosition + "endOfLine is " + _endOfLine
		+ "_endOfParagraph is " + _endOfParagraph);
    }
    
    void init(TextStyleRun textstylerun, int i, int i_6_, int i_7_, int i_8_,
	      int i_9_, int i_10_) {
	_textRun = textstylerun;
	_x = i;
	_y = i_6_;
	_lineNumber = i_7_;
	_lineHeight = i_8_;
	_positionInRun = i_9_;
	_absPosition = i_10_;
	_updateLine = _lineNumber;
    }
    
    void init(TextPositionInfo textpositioninfo_11_) {
	init(textpositioninfo_11_._textRun, textpositioninfo_11_._x,
	     textpositioninfo_11_._y, textpositioninfo_11_._lineNumber,
	     textpositioninfo_11_._lineHeight,
	     textpositioninfo_11_._positionInRun,
	     textpositioninfo_11_._absPosition);
    }
    
    void representCharacterAfterEndOfLine() {
	if (_endOfLine) {
	    TextParagraphFormat textparagraphformat
		= _textRun.paragraph().currentParagraphFormat();
	    _x = (textparagraphformat._leftMargin
		  + textparagraphformat._leftIndent);
	    if (textparagraphformat.wrapsUnderFirstCharacter())
		_x = (textparagraphformat._leftMargin
		      + ((_textRun.paragraph().addWidthOfInitialTabs
			  (textparagraphformat._leftMargin
			   + textparagraphformat._leftIndent))
			 - textparagraphformat._leftIndent));
	    _y += _lineHeight;
	    _lineNumber++;
	    _lineHeight = _textRun.paragraph()._lineHeights[_lineNumber];
	    _endOfLine = false;
	}
    }
    
    void representCharacterBeforeEndOfLine() {
	TextPositionInfo textpositioninfo_12_
	    = _textRun._paragraph._owner.positionInfoForIndex(_absPosition);
	if (textpositioninfo_12_._endOfLine) {
	    _textRun = textpositioninfo_12_._textRun;
	    _x = textpositioninfo_12_._x;
	    _y = textpositioninfo_12_._y;
	    _absPosition = textpositioninfo_12_._absPosition;
	    _lineNumber = textpositioninfo_12_._lineNumber;
	    _lineHeight = textpositioninfo_12_._lineHeight;
	    _positionInRun = textpositioninfo_12_._positionInRun;
	    _updateLine = textpositioninfo_12_._updateLine;
	    _redrawCurrentLineOnly
		= textpositioninfo_12_._redrawCurrentLineOnly;
	    _redrawCurrentParagraphOnly
		= textpositioninfo_12_._redrawCurrentParagraphOnly;
	    _nextLine = textpositioninfo_12_._nextLine;
	    _endOfLine = textpositioninfo_12_._endOfLine;
	    _endOfParagraph = textpositioninfo_12_._endOfParagraph;
	}
    }
    
    void setUpdateLine(int i) {
	_updateLine = i;
    }
    
    void setRedrawCurrentLineOnly(boolean bool) {
	_redrawCurrentLineOnly = bool;
    }
    
    void setRedrawCurrentParagraphOnly(boolean bool) {
	_redrawCurrentParagraphOnly = bool;
    }
    
    void setX(int i) {
	_x = i;
    }
    
    void setAbsPosition(int i) {
	_absPosition = i;
    }
    
    void setPositionInRun(int i) {
	_positionInRun = i;
    }
    
    void moveBy(int i, int i_13_) {
	_x += i;
	_y += i_13_;
    }
    
    int maxY() {
	return _y + _lineHeight;
    }
    
    Rect lineBounds() {
	return _textRun._paragraph.rectForLine(_lineNumber);
    }
    
    Range lineRange() {
	return _textRun._paragraph.rangeForLine(_lineNumber);
    }
    
    void setNextLine(boolean bool) {
	_nextLine = bool;
    }
    
    void setAtEndOfLine(boolean bool) {
	_endOfLine = bool;
    }
    
    void setAtEndOfParagraph(boolean bool) {
	_endOfParagraph = bool;
    }
}
