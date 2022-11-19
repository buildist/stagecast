/* TextParagraph - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class TextParagraph implements Cloneable
{
    TextView _owner;
    TextParagraphFormat _format;
    Vector _runVector;
    int _y;
    int _height;
    int[] _lineBreaks;
    int _breakCount;
    int[] _lineHeights;
    int _heightCount;
    int[] _baselines;
    int _baselineCount;
    int[] _lineRemainders;
    int _remainderCount;
    int _charCount;
    int _startChar;
    
    public TextParagraph() {
	/* empty */
    }
    
    TextParagraph(TextView textview) {
	this();
	init(textview);
    }
    
    TextParagraph(TextView textview, TextParagraphFormat textparagraphformat) {
	this();
	init(textview, textparagraphformat);
    }
    
    void init(TextView textview, TextParagraphFormat textparagraphformat) {
	_owner = textview;
	_runVector = new Vector();
	setFormat(textparagraphformat);
    }
    
    void init(TextView textview) {
	init(textview, null);
    }
    
    Object objectAt(Vector vector, int i) {
	return i < 0 || i >= vector.count() ? null : vector.elementAt(i);
    }
    
    public Object clone() {
	Object object = null;
	collectEmptyRuns();
	try {
	    object = super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new InconsistencyException(String.valueOf(this)
					     + ": clone() not supported :"
					     + clonenotsupportedexception);
	}
	if (object != null) {
	    TextParagraph textparagraph_0_ = (TextParagraph) object;
	    textparagraph_0_._owner = null;
	    if (_format != null)
		textparagraph_0_._format
		    = (TextParagraphFormat) _format.clone();
	    else
		textparagraph_0_._format = null;
	    textparagraph_0_._runVector = new Vector();
	    int i = _runVector.count();
	    for (int i_1_ = 0; i_1_ < i; i_1_++) {
		TextStyleRun textstylerun
		    = (TextStyleRun) _runVector.elementAt(i_1_);
		textparagraph_0_.addRun(textstylerun.createEmptyRun());
	    }
	    textparagraph_0_._lineBreaks = null;
	    textparagraph_0_._lineHeights = null;
	    textparagraph_0_._baselines = null;
	    textparagraph_0_._lineRemainders = null;
	}
	return object;
    }
    
    void setOwner(TextView textview) {
	_owner = textview;
    }
    
    TextView owner() {
	return _owner;
    }
    
    void setY(int i) {
	_y = i;
    }
    
    void setStartChar(int i) {
	_startChar = i;
    }
    
    void setFormat(TextParagraphFormat textparagraphformat) {
	if (textparagraphformat != null || _format != null) {
	    if (textparagraphformat != null)
		_format = (TextParagraphFormat) textparagraphformat.clone();
	    else
		_format = null;
	    if (_charCount > 0)
		computeLineBreaksAndHeights(_owner.bounds.width);
	}
    }
    
    TextParagraphFormat format() {
	return _format;
    }
    
    TextParagraphFormat currentParagraphFormat() {
	if (_format != null)
	    return _format;
	TextParagraphFormat textparagraphformat = null;
	if (_owner != null)
	    textparagraphformat
		= ((TextParagraphFormat)
		   _owner.defaultAttributes().get("ParagraphFormatKey"));
	if (textparagraphformat != null)
	    return textparagraphformat;
	return new TextParagraphFormat();
    }
    
    Vector runVector() {
	return _runVector;
    }
    
    TextStyleRun firstRun() {
	return (TextStyleRun) _runVector.firstElement();
    }
    
    TextStyleRun lastRun() {
	return (TextStyleRun) _runVector.lastElement();
    }
    
    void addRun(TextStyleRun textstylerun) {
	if (textstylerun != null) {
	    textstylerun.setParagraph(this);
	    _runVector.addElement(textstylerun);
	}
    }
    
    void collectEmptyRuns() {
	int i = 1;
	for (int i_2_ = _runVector.count(); i < i_2_; i++) {
	    TextStyleRun textstylerun = (TextStyleRun) _runVector.elementAt(i);
	    if (textstylerun.charCount() == 0
		&& (textstylerun._attributes == null
		    || (textstylerun._attributes.get("LinkDestinationKey")
			== null))) {
		_runVector.removeElementAt(i);
		i--;
		i_2_--;
	    }
	}
    }
    
    void addRuns(Vector vector) {
	if (vector != null) {
	    int i = vector.count();
	    for (int i_3_ = 0; i_3_ < i; i_3_++)
		addRun((TextStyleRun) vector.elementAt(i_3_));
	}
    }
    
    void insertRunAt(TextStyleRun textstylerun, int i) {
	if (textstylerun != null && i >= 0) {
	    textstylerun.setParagraph(this);
	    _runVector.insertElementAt(textstylerun, i);
	}
    }
    
    TextStyleRun runBefore(TextStyleRun textstylerun) {
	if (textstylerun == null)
	    return null;
	int i = _runVector.indexOfIdentical(textstylerun);
	if (i < 1)
	    return null;
	return (TextStyleRun) _runVector.elementAt(i - 1);
    }
    
    TextStyleRun runAfter(TextStyleRun textstylerun) {
	if (textstylerun == null)
	    return null;
	int i = _runVector.indexOfIdentical(textstylerun);
	if (i == _runVector.count() - 1)
	    return null;
	return (TextStyleRun) _runVector.elementAt(i + 1);
    }
    
    Vector runsBefore(TextStyleRun textstylerun) {
	Vector vector = TextView.newVector();
	if (textstylerun == null)
	    return vector;
	int i = _runVector.indexOfIdentical(textstylerun);
	if (i == -1)
	    return vector;
	for (int i_4_ = 0; i_4_ < i; i_4_++)
	    vector.addElement(_runVector.elementAt(i_4_));
	return vector;
    }
    
    Vector runsAfter(TextStyleRun textstylerun) {
	Vector vector = TextView.newVector();
	if (textstylerun == null)
	    return vector;
	int i = _runVector.indexOfIdentical(textstylerun);
	if (i == -1)
	    return vector;
	for (int i_5_ = _runVector.count(); i < i_5_; i++)
	    vector.addElement(_runVector.elementAt(i));
	return vector;
    }
    
    Vector runsFromTo(TextStyleRun textstylerun,
		      TextStyleRun textstylerun_6_) {
	Vector vector = TextView.newVector();
	if (textstylerun == textstylerun_6_ && textstylerun != null) {
	    vector.addElement(textstylerun);
	    return vector;
	}
	int i;
	if (textstylerun == null)
	    i = 0;
	else
	    i = _runVector.indexOfIdentical(textstylerun);
	int i_7_;
	if (textstylerun_6_ == null)
	    i_7_ = _runVector.count() - 1;
	else
	    i_7_ = _runVector.indexOfIdentical(textstylerun_6_);
	if (i < 0 || i_7_ < 0)
	    return vector;
	for (/**/; i <= i_7_; i++)
	    vector.addElement(_runVector.elementAt(i));
	return vector;
    }
    
    void removeRun(TextStyleRun textstylerun) {
	if (textstylerun != null)
	    _runVector.removeElement(textstylerun);
    }
    
    void removeRuns(Vector vector) {
	if (vector != null) {
	    int i = vector.count();
	    while (i-- > 0)
		_runVector.removeElement(vector.elementAt(i));
	}
    }
    
    void removeRunAt(int i) {
	_runVector.removeElementAt(i);
    }
    
    boolean isEmpty() {
	int i = 0;
	int i_8_ = _runVector.count();
	TextStyleRun textstylerun;
	for (/**/; i_8_-- > 0 && i == 0; i += textstylerun.charCount())
	    textstylerun = (TextStyleRun) _runVector.elementAt(i_8_);
	return i == 0;
    }
    
    int[] _growArrayTo(int[] is, int i) {
	if (i < 1)
	    return is;
	if (is != null && is.length >= i)
	    return is;
	int[] is_9_ = is;
	int i_10_;
	if (is != null) {
	    for (i_10_ = is.length; i_10_ < i; i_10_ *= 2) {
		/* empty */
	    }
	} else
	    i_10_ = 20;
	is = new int[i_10_];
	if (is_9_ == null)
	    return is;
	System.arraycopy(is_9_, 0, is, 0, is_9_.length);
	return is;
    }
    
    void _addLineBreak(int i) {
	if (i >= 0) {
	    _lineBreaks = _growArrayTo(_lineBreaks, _breakCount + 1);
	    _lineBreaks[_breakCount] = i;
	    _breakCount++;
	}
    }
    
    void _addLineHeightAndBaseline(int i, int i_11_) {
	if (i >= 0 && i_11_ >= 0) {
	    _lineHeights = _growArrayTo(_lineHeights, _heightCount + 1);
	    _lineHeights[_heightCount] = i;
	    _heightCount++;
	    _baselines = _growArrayTo(_baselines, _baselineCount + 1);
	    _baselines[_baselineCount] = i_11_;
	    _baselineCount++;
	}
    }
    
    void _addLineRemainder(int i) {
	if (i < 0)
	    i = 0;
	_lineRemainders = _growArrayTo(_lineRemainders, _remainderCount + 1);
	_lineRemainders[_remainderCount] = i;
	_remainderCount++;
    }
    
    int addWidthOfInitialTabs(int i) {
	int i_12_ = 0;
	int[] is = currentParagraphFormat().tabPositions();
	int i_13_ = 0;
	for (int i_14_ = _runVector.count(); i_13_ < i_14_; i_13_++) {
	    TextStyleRun textstylerun
		= (TextStyleRun) _runVector.elementAt(i_13_);
	    FastStringBuffer faststringbuffer = textstylerun._contents;
	    if (faststringbuffer == null || faststringbuffer.length() == 0)
		break;
	    int i_15_ = 0;
	    int i_16_;
	    for (i_16_ = faststringbuffer.length();
		 i_15_ < i_16_ && faststringbuffer.charAt(i_15_) == '\t';
		 i_15_++)
		i_12_++;
	    if (i_15_ < i_16_)
		break;
	}
	if (i_12_ == 0)
	    return i;
	i_13_ = 0;
	int i_17_;
	for (i_17_ = is.length; i_13_ < i_17_; i_13_++) {
	    if (is[i_13_] >= i)
		break;
	}
	if (i_13_ == i_17_)
	    return i;
	i_13_ += i_12_;
	if (i_13_ >= is.length)
	    return is[is.length - 1];
	return is[i_13_ - 1];
    }
    
    void computeLineBreaksAndHeights(int i) {
	computeLineBreaksAndHeights(i, 0);
    }
    
    void computeLineBreaksAndHeights(int i, int i_18_) {
	int i_19_ = i;
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	if (i_18_ > 0)
	    i_18_--;
	_breakCount = i_18_;
	_heightCount = i_18_;
	_baselineCount = i_18_;
	_remainderCount = i_18_;
	if (textparagraphformat._justification == 0 && i_18_ == 0)
	    _lineRemainders = null;
	i -= (textparagraphformat._leftMargin
	      + textparagraphformat._rightMargin);
	if (i < 1)
	    i = 1;
	int i_20_;
	int i_21_;
	if (i_18_ == 0) {
	    i_20_ = i - textparagraphformat._leftIndent;
	    if (i_20_ < 1)
		i_20_ = 1;
	    i_21_ = i_20_;
	    _height = 0;
	    _charCount = 0;
	} else {
	    i_20_ = i;
	    i_21_ = i;
	    _height = 0;
	    for (int i_22_ = 0; i_22_ < i_18_; i_22_++)
		_height += _lineHeights[i_22_];
	    _charCount = _lineBreaks[i_18_ - 1];
	}
	int i_23_ = _runVector.count();
	int i_24_ = _charCount;
	int i_25_ = 0;
	int i_26_ = 0;
	int i_28_;
	int i_27_ = i_28_ = 0;
	int i_29_ = textparagraphformat._leftMargin;
	int i_30_ = i_29_;
	if (i_18_ == 0)
	    i_29_ += textparagraphformat._leftIndent;
	if (textparagraphformat.wrapsUnderFirstCharacter()) {
	    i_30_ = addWidthOfInitialTabs(textparagraphformat._leftMargin
					  + textparagraphformat._leftIndent);
	    i -= i_30_ - i_29_;
	    i_20_ = i;
	}
	TextStyleRun textstylerun;
	int i_31_;
	int i_32_;
	int i_33_;
	if (i_18_ == 0) {
	    i_31_ = 0;
	    textstylerun = null;
	    i_32_ = 0;
	    i_33_ = 0;
	} else {
	    textstylerun = runForCharPosition(_startChar + _charCount);
	    i_31_ = _runVector.indexOfIdentical(textstylerun) + 1;
	    i_32_ = textstylerun.charCount();
	    i_33_ = _startChar + _charCount - textstylerun.rangeIndex();
	    _charCount += textstylerun.rangeIndex() + i_32_ - (_startChar
							       + _charCount);
	}
    while_1_:
	for (;;) {
	    do {
		if (textstylerun == null || i_33_ >= i_32_) {
		    if (i_31_ == _runVector.count())
			break while_1_;
		    textstylerun
			= (TextStyleRun) _runVector.elementAt(i_31_++);
		    if (textstylerun.charCount() == 0)
			break;
		    i_33_ = 0;
		    i_32_ = textstylerun.charCount();
		    _charCount += i_32_;
		}
		while (i_33_ < i_32_) {
		    int i_34_ = textstylerun.charsForWidth(i_33_, i_29_, i_20_,
							   i_21_,
							   (textparagraphformat
							    ._tabStops));
		    if (i_34_ > 0) {
			i_33_ += i_34_;
			i_24_ += i_34_;
			int i_35_ = textstylerun.baseline();
			int i_36_
			    = textstylerun.height() - textstylerun.baseline();
			if (i_27_ < i_35_)
			    i_27_ = i_35_;
			if (i_28_ < i_36_)
			    i_28_ = i_36_;
			if (i_27_ + i_28_ > i_25_)
			    i_25_ = i_27_ + i_28_;
			if (i_27_ > i_26_)
			    i_26_ = i_27_;
			i_29_ += i_20_ - textstylerun._remainder;
			i_20_ = textstylerun._remainder;
		    }
		    if (i_33_ < i_32_) {
			_addLineBreak(i_24_);
			_addLineHeightAndBaseline(i_25_ + (textparagraphformat
							   ._lineSpacing),
						  i_26_);
			_height += i_25_ + textparagraphformat._lineSpacing;
			_addLineRemainder(i_20_);
			i_20_ = i_21_ = i;
			i_25_ = i_26_ = 0;
			i_27_ = i_28_ = 0;
			i_29_ = i_30_;
		    }
		}
	    } while (false);
	}
	_addLineBreak(i_24_);
	if (i_25_ == 0) {
	    textstylerun = (TextStyleRun) _runVector.firstElement();
	    i_25_ = textstylerun.height() + textparagraphformat._lineSpacing;
	    i_26_ = textstylerun.baseline();
	} else
	    i_25_ += textparagraphformat._lineSpacing;
	_addLineHeightAndBaseline(i_25_, i_26_);
	_height += i_25_;
	_addLineRemainder(i_20_);
	_charCount++;
    }
    
    int characterStartingLine(int i) {
	if (i == 0)
	    return _startChar;
	if (i < _breakCount)
	    return _startChar + _lineBreaks[i - 1];
	return -1;
    }
    
    Rect rectForLine(int i) {
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	if (i >= _breakCount)
	    return null;
	int i_37_ = _y;
	int i_38_;
	for (i_38_ = 0; i_38_ < i; i_38_++)
	    i_37_ += _lineHeights[i_38_];
	return TextView.newRect(textparagraphformat._leftMargin, i_37_,
				(_owner.bounds.width
				 - textparagraphformat._rightMargin),
				_lineHeights[i_38_]);
    }
    
    Range rangeForLine(int i) {
	if (i >= _breakCount)
	    return new Range(_startChar + _charCount, 0);
	if (i == 0)
	    return new Range(_startChar, _lineBreaks[i]);
	return new Range(_startChar + _lineBreaks[i - 1],
			 _lineBreaks[i] - _lineBreaks[i - 1]);
    }
    
    int runIndexForCharPosition(int i) {
	int i_39_ = i - _startChar;
	int i_40_ = _runVector.count();
	for (int i_41_ = 0; i_41_ < i_40_; i_41_++) {
	    TextStyleRun textstylerun
		= (TextStyleRun) _runVector.elementAt(i_41_);
	    if (textstylerun.charCount() <= i_39_)
		i_39_ -= textstylerun.charCount();
	    else
		return i_41_;
	}
	return _runVector.count() - 1;
    }
    
    TextStyleRun runForCharPosition(int i) {
	int i_42_ = runIndexForCharPosition(i);
	if (i_42_ >= 0)
	    return (TextStyleRun) _runVector.elementAt(i_42_);
	return null;
    }
    
    char characterAt(int i) {
	if (_charCount < 2)
	    return '\n';
	int i_43_ = i - _startChar;
	int i_44_ = _runVector.count();
	for (int i_45_ = 0; i_45_ < i_44_; i_45_++) {
	    TextStyleRun textstylerun
		= (TextStyleRun) _runVector.elementAt(i_45_);
	    if (textstylerun.charCount() <= i_43_)
		i_43_ -= textstylerun.charCount();
	    else {
		char c = textstylerun.charAt(i_43_);
		return c;
	    }
	}
	if (i_43_ < 2)
	    return '\n';
	return '\0';
    }
    
    int lineForPosition(int i) {
	int i_46_ = i - _startChar;
	if (_breakCount > 0 && i_46_ == _lineBreaks[_breakCount - 1])
	    return _breakCount - 1;
	int i_47_;
	for (i_47_ = 0; i_47_ < _breakCount && i_46_ >= _lineBreaks[i_47_];
	     i_47_++) {
	    /* empty */
	}
	if (i_47_ >= _breakCount)
	    return -1;
	return i_47_;
    }
    
    TextPositionInfo positionForPoint(int i, int i_48_, boolean bool) {
	TextStyleRun textstylerun = null;
	Object object = null;
	int i_49_ = 0;
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	int i_50_ = _y;
	int i_51_;
	for (i_51_ = 0; i_51_ < _breakCount; i_51_++) {
	    if (i_48_ >= i_50_ && i_48_ <= i_50_ + _lineHeights[i_51_])
		break;
	    i_50_ += _lineHeights[i_51_];
	}
	int i_52_ = i_51_;
	int i_53_;
	if (i_52_ == 0)
	    i_53_ = _startChar;
	else
	    i_53_ = _startChar + _lineBreaks[i_52_ - 1];
	int i_54_ = _startChar + _lineBreaks[i_52_];
	int i_55_ = _runVector.count();
	int i_56_ = _startChar;
	for (i_51_ = 0; i_51_ < i_55_; i_51_++) {
	    textstylerun = (TextStyleRun) _runVector.elementAt(i_51_);
	    if (i_56_ + textstylerun.charCount() > i_53_) {
		i_49_ = i_53_ - i_56_;
		break;
	    }
	    i_56_ += textstylerun.charCount();
	}
	if (textstylerun == null)
	    return null;
	int i_57_ = i_51_;
	int i_58_;
	if (textparagraphformat._justification == 0)
	    i_58_ = textparagraphformat._leftMargin;
	else if (textparagraphformat._justification == 2)
	    i_58_ = textparagraphformat._leftMargin + _lineRemainders[i_52_];
	else
	    i_58_
		= textparagraphformat._leftMargin + _lineRemainders[i_52_] / 2;
	int i_59_ = (_owner.bounds.width - textparagraphformat._leftMargin
		     - textparagraphformat._rightMargin);
	if (i_52_ == 0) {
	    i_58_ += textparagraphformat._leftIndent;
	    i_59_ -= textparagraphformat._leftIndent;
	} else if (textparagraphformat.wrapsUnderFirstCharacter()) {
	    int i_60_ = i_58_;
	    i_58_ = addWidthOfInitialTabs(i_58_
					  + textparagraphformat._leftIndent);
	    i_59_ -= i_58_ - i_60_;
	}
	if (i_59_ < 1)
	    i_59_ = 1;
	if (i > i_58_ + i_59_ - _lineRemainders[i_52_]) {
	    TextPositionInfo textpositioninfo = infoForPosition(i_54_, -1);
	    textpositioninfo.setAtEndOfLine(true);
	    return textpositioninfo;
	}
	if (i <= i_58_)
	    return infoForPosition(i_53_, i_48_);
	i_57_++;
	while (textstylerun != null && textstylerun.charCount() == 0) {
	    textstylerun = runAfter(textstylerun);
	    i_57_++;
	}
	if (textstylerun == null)
	    return infoForPosition(i_53_ + i_56_, i_48_);
	while (i_53_ <= i_54_) {
	    int i_61_
		= textstylerun.widthOfContents(i_49_, 1, i_58_,
					       textparagraphformat._tabStops);
	    if (i >= i_58_ && i <= i_58_ + i_61_) {
		if (bool)
		    return infoForPosition(i_53_, i_48_);
		i_61_ /= 2;
		if (i >= i_58_ + i_61_)
		    return infoForPosition(i_53_ + 1, i_48_);
		return infoForPosition(i_53_, i_48_);
	    }
	    if (++i_49_ >= textstylerun.charCount()) {
		textstylerun = (TextStyleRun) objectAt(_runVector, i_57_++);
		while (textstylerun != null && textstylerun.charCount() == 0) {
		    textstylerun = runAfter(textstylerun);
		    i_57_++;
		}
		if (textstylerun == null)
		    return infoForPosition(i_53_ + i_56_, i_48_);
		i_49_ = 0;
	    }
	    i_53_++;
	    i_58_ += i_61_;
	}
	return null;
    }
    
    TextPositionInfo _infoForPosition(int i) {
	boolean bool = false;
	boolean bool_62_ = false;
	boolean bool_63_ = true;
	boolean bool_64_ = false;
	int i_65_ = 0;
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	int i_66_ = i - _startChar;
	int i_67_ = i_66_;
	if (i_66_ >= _charCount)
	    i_66_ = _charCount;
	int i_68_ = (_owner.bounds.width - textparagraphformat._leftMargin
		     - textparagraphformat._rightMargin);
	int i_69_ = _y;
	int i_70_;
	for (i_70_ = 0; i_70_ < _breakCount - 1; i_70_++) {
	    if (_lineBreaks[i_70_] >= i_66_)
		break;
	    i_69_ += _lineHeights[i_70_];
	}
	int i_71_;
	TextStyleRun textstylerun;
	int i_72_;
	if (i_70_ > 0) {
	    i_71_
		= runIndexForCharPosition(_startChar + _lineBreaks[i_70_ - 1]);
	    textstylerun = (TextStyleRun) _runVector.elementAt(i_71_++);
	    i_72_ = (_lineBreaks[i_70_ - 1] + _startChar
		     - textstylerun.rangeIndex());
	    i_66_ -= _lineBreaks[i_70_ - 1];
	} else {
	    i_71_ = 1;
	    textstylerun = (TextStyleRun) _runVector.firstElement();
	    i_72_ = 0;
	}
	for (/**/; i_70_ < _breakCount; i_70_++) {
	    int i_73_;
	    if (i_70_ == 0)
		i_73_ = _lineBreaks[i_70_];
	    else
		i_73_ = _lineBreaks[i_70_] - _lineBreaks[i_70_ - 1];
	    if (i_73_ > i_66_)
		i_73_ = i_66_;
	    int i_74_;
	    if (textparagraphformat._justification == 0)
		i_74_ = textparagraphformat._leftMargin;
	    else if (textparagraphformat._justification == 2)
		i_74_
		    = textparagraphformat._leftMargin + _lineRemainders[i_70_];
	    else
		i_74_ = (textparagraphformat._leftMargin
			 + _lineRemainders[i_70_] / 2);
	    int i_75_ = i_68_;
	    if (i_70_ == 0) {
		i_74_ += textparagraphformat._leftIndent;
		i_75_ -= textparagraphformat._leftIndent;
	    } else if (textparagraphformat.wrapsUnderFirstCharacter()) {
		int i_76_ = addWidthOfInitialTabs(i_74_ + (textparagraphformat
							   ._leftIndent));
		i_75_ -= i_76_ - i_74_;
		i_74_ = i_76_;
	    }
	    if (i_75_ < 1)
		i_75_ = 1;
	    if (i_73_ == 0)
		return new TextPositionInfo(textstylerun, i_74_, i_69_, i_70_,
					    _lineHeights[i_70_], i_72_, i);
	    while (i_73_ > 0) {
		int i_77_ = textstylerun.charCount() - i_72_;
		if (i_73_ >= i_77_) {
		    if (i_73_ <= i_66_)
			i_65_
			    = textstylerun.widthOfContents(i_72_, i_73_, i_74_,
							   (textparagraphformat
							    ._tabStops));
		    i_73_ -= i_77_;
		    i_66_ -= i_77_;
		    for (textstylerun
			     = (TextStyleRun) objectAt(_runVector, i_71_++);
			 textstylerun != null && textstylerun.charCount() == 0;
			 textstylerun
			     = (TextStyleRun) objectAt(_runVector, i_71_++)) {
			/* empty */
		    }
		    i_72_ = 0;
		} else {
		    if (i_73_ <= i_66_)
			i_65_
			    = textstylerun.widthOfContents(i_72_, i_73_, i_74_,
							   (textparagraphformat
							    ._tabStops));
		    i_72_ += i_73_;
		    i_66_ -= i_73_;
		    i_73_ = 0;
		}
		i_74_ += i_65_;
		i_75_ -= i_65_;
		if (i_66_ == 0 || textstylerun == null && i_66_ == 1) {
		    if (textstylerun == null) {
			textstylerun = (TextStyleRun) _runVector.lastElement();
			i_72_ = textstylerun.charCount();
		    }
		    TextPositionInfo textpositioninfo
			= new TextPositionInfo(textstylerun, i_74_, i_69_,
					       i_70_, _lineHeights[i_70_],
					       i_72_, i);
		    if (i_67_ == _lineBreaks[i_70_]) {
			textpositioninfo.setAtEndOfLine(true);
			if (i_70_ == _breakCount - 1)
			    textpositioninfo.setAtEndOfParagraph(true);
		    }
		    return textpositioninfo;
		}
	    }
	    i_69_ += _lineHeights[i_70_];
	}
	return null;
    }
    
    TextPositionInfo infoForPosition(int i, int i_78_) {
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	TextPositionInfo textpositioninfo = _infoForPosition(i);
	if (textpositioninfo == null) {
	    textpositioninfo = _infoForPosition(_startChar + _charCount);
	    return textpositioninfo;
	}
	if (i_78_ < textpositioninfo.maxY())
	    return textpositioninfo;
	TextPositionInfo textpositioninfo_79_ = _infoForPosition(i + 1);
	if (textpositioninfo_79_ == null || (textpositioninfo_79_._lineNumber
					     == textpositioninfo._lineNumber))
	    return textpositioninfo;
	int i_80_ = textpositioninfo_79_._lineNumber;
	int i_81_;
	if (textparagraphformat._justification == 0)
	    i_81_ = textparagraphformat._leftMargin;
	else if (textparagraphformat._justification == 2)
	    i_81_ = textparagraphformat._leftMargin + _lineRemainders[i_80_];
	else
	    i_81_
		= textparagraphformat._leftMargin + _lineRemainders[i_80_] / 2;
	textpositioninfo
	    = new TextPositionInfo(textpositioninfo._textRun, i_81_,
				   textpositioninfo_79_._y,
				   textpositioninfo_79_._lineNumber,
				   textpositioninfo_79_._lineHeight,
				   textpositioninfo._positionInRun,
				   textpositioninfo._absPosition);
	textpositioninfo.setNextLine(true);
	return textpositioninfo;
    }
    
    TextPositionInfo insertCharOrStringAt(char c, String string, int i) {
	TextStyleRun textstylerun = null;
	if (string == null && c == 0)
	    return null;
	int i_82_ = i - _startChar;
	int i_83_ = _runVector.count();
	int i_84_;
	for (i_84_ = 0; i_84_ < i_83_; i_84_++) {
	    textstylerun = (TextStyleRun) objectAt(_runVector, i_84_);
	    if (textstylerun == null)
		break;
	    if (textstylerun.charCount() < i_82_)
		i_82_ -= textstylerun.charCount();
	    else {
		if (textstylerun.containsATextAttachment()) {
		    if (i_82_ == 0) {
			TextStyleRun textstylerun_85_
			    = textstylerun.createEmptyRun();
			insertRunAt(textstylerun_85_,
				    _runVector.indexOfIdentical(textstylerun));
			textstylerun = textstylerun_85_;
		    } else {
			Object object = null;
			if (i_84_ > 0) {
			    TextStyleRun textstylerun_86_
				= ((TextStyleRun)
				   objectAt(_runVector, i_84_ - 1));
			    TextStyleRun textstylerun_87_
				= (textstylerun_86_.createEmptyRun
				   (TextView
					.attributesByRemovingStaticAttributes
				    (textstylerun_86_.attributes())));
			    insertRunAt(textstylerun_87_,
					(_runVector
					     .indexOfIdentical(textstylerun)
					 + 1));
			    textstylerun = textstylerun_87_;
			} else {
			    textstylerun
				= (TextStyleRun) objectAt(_runVector, 0);
			    TextStyleRun textstylerun_88_
				= (textstylerun.createEmptyRun
				   (TextView
					.attributesByRemovingStaticAttributes
				    (textstylerun.attributes())));
			    insertRunAt(textstylerun_88_, 1);
			    textstylerun = textstylerun_88_;
			}
			if (textstylerun == null) {
			    textstylerun = new TextStyleRun(this, "", null);
			    addRun(textstylerun);
			}
		    }
		    i_82_ = 0;
		}
		break;
	    }
	}
	if ((i_84_ >= i_83_ || textstylerun == null) && i_82_ == 1) {
	    textstylerun = (TextStyleRun) _runVector.lastElement();
	    i_82_ = textstylerun.charCount();
	} else if (textstylerun == null)
	    return null;
	int i_89_ = lineForPosition(i - 1);
	int i_90_ = _lineBreaks[i_89_] + _startChar;
	TextPositionInfo textpositioninfo = infoForPosition(i_90_, -1);
	int i_91_ = _height;
	int i_92_;
	if (string != null) {
	    textstylerun.insertStringAt(string, i_82_);
	    i_92_ = string.length();
	} else {
	    textstylerun.insertCharAt(c, i_82_);
	    i_92_ = 1;
	}
	computeLineBreaksAndHeights(_owner.bounds.width, i_89_);
	TextPositionInfo textpositioninfo_93_ = infoForPosition(i + i_92_, -1);
	if (i_91_ != _height) {
	    textpositioninfo_93_.setRedrawCurrentParagraphOnly(false);
	    textpositioninfo_93_.setRedrawCurrentLineOnly(false);
	    return textpositioninfo_93_;
	}
	textpositioninfo_93_.setRedrawCurrentParagraphOnly(true);
	if (textpositioninfo_93_._lineNumber != textpositioninfo._lineNumber) {
	    textpositioninfo_93_.setRedrawCurrentParagraphOnly(false);
	    if (textpositioninfo._lineNumber
		< textpositioninfo_93_._lineNumber)
		textpositioninfo_93_
		    .setUpdateLine(textpositioninfo._lineNumber);
	    else
		textpositioninfo_93_
		    .setUpdateLine(textpositioninfo_93_._lineNumber);
	    return textpositioninfo_93_;
	}
	textpositioninfo_93_.setRedrawCurrentParagraphOnly(true);
	TextPositionInfo textpositioninfo_94_ = infoForPosition(i_90_ + 1, -1);
	if (textpositioninfo_94_ != null && textpositioninfo != null
	    && (textpositioninfo_94_._lineNumber
		== textpositioninfo._lineNumber)) {
	    if (currentParagraphFormat().wrapsUnderFirstCharacter())
		textpositioninfo_93_.setRedrawCurrentLineOnly(false);
	    else
		textpositioninfo_93_.setRedrawCurrentLineOnly(true);
	} else
	    textpositioninfo_93_.setRedrawCurrentLineOnly(false);
	return textpositioninfo_93_;
    }
    
    TextPositionInfo removeCharAt(int i) {
	TextStyleRun textstylerun = null;
	if (i <= _startChar)
	    return null;
	i--;
	int i_95_ = i - _startChar;
	int i_96_ = _runVector.count();
	int i_97_;
	for (i_97_ = 0; i_97_ < i_96_; i_97_++) {
	    textstylerun = (TextStyleRun) _runVector.elementAt(i_97_);
	    if (textstylerun.charCount() > i_95_)
		break;
	    i_95_ -= textstylerun.charCount();
	}
	if ((i_97_ >= i_96_ || textstylerun == null) && i_95_ == 0) {
	    textstylerun = (TextStyleRun) _runVector.lastElement();
	    i_95_ = textstylerun.charCount() - 1;
	} else if (textstylerun == null)
	    return null;
	int i_98_ = lineForPosition(i - 1);
	int i_99_;
	if (i_98_ == 0)
	    i_99_ = _startChar + 1;
	else
	    i_99_ = _lineBreaks[i_98_ - 1] + _startChar + 1;
	int i_100_ = _lineBreaks[i_98_] + _startChar + 1;
	if (i_100_ > _startChar + _charCount)
	    i_100_ = _startChar + _charCount;
	TextPositionInfo textpositioninfo = infoForPosition(i_100_, -1);
	TextPositionInfo textpositioninfo_101_ = infoForPosition(i_99_, -1);
	int i_102_ = _height;
	textstylerun.removeCharAt(i_95_);
	if (textstylerun.charCount() == 0 && _runVector.count() > 1)
	    _runVector.removeElement(textstylerun);
	computeLineBreaksAndHeights(_owner.bounds.width);
	TextPositionInfo textpositioninfo_103_ = infoForPosition(i, -1);
	if (i_102_ == _height)
	    textpositioninfo_103_.setRedrawCurrentParagraphOnly(true);
	if (textpositioninfo_103_._lineNumber
	    != textpositioninfo_101_._lineNumber) {
	    if (textpositioninfo_101_._lineNumber
		< textpositioninfo_103_._lineNumber)
		textpositioninfo_103_
		    .setUpdateLine(textpositioninfo_101_._lineNumber);
	    else
		textpositioninfo_103_
		    .setUpdateLine(textpositioninfo_103_._lineNumber);
	    return textpositioninfo_103_;
	}
	TextPositionInfo textpositioninfo_104_
	    = infoForPosition(i_100_ - 1, -1);
	if (textpositioninfo._lineNumber
	    != textpositioninfo_104_._lineNumber) {
	    if (textpositioninfo._lineNumber
		< textpositioninfo_104_._lineNumber)
		textpositioninfo_103_
		    .setUpdateLine(textpositioninfo._lineNumber);
	    else
		textpositioninfo_103_
		    .setUpdateLine(textpositioninfo_104_._lineNumber);
	    return textpositioninfo_103_;
	}
	TextPositionInfo textpositioninfo_105_ = infoForPosition(i_99_, -1);
	if (i_102_ == _height && textpositioninfo_105_ != null
	    && textpositioninfo_101_ != null
	    && (textpositioninfo_105_._lineNumber
		== textpositioninfo_101_._lineNumber)) {
	    if (currentParagraphFormat().wrapsUnderFirstCharacter())
		textpositioninfo_103_.setRedrawCurrentLineOnly(false);
	    else
		textpositioninfo_103_.setRedrawCurrentLineOnly(true);
	}
	return textpositioninfo_103_;
    }
    
    TextStyleRun createNewRunAt(int i) {
	TextPositionInfo textpositioninfo = infoForPosition(i, -1);
	if (textpositioninfo == null)
	    return null;
	int i_106_ = _runVector.indexOfIdentical(textpositioninfo._textRun);
	TextStyleRun textstylerun
	    = textpositioninfo._textRun
		  .breakAt(textpositioninfo._positionInRun);
	insertRunAt(textstylerun, i_106_ + 1);
	return textstylerun;
    }
    
    TextParagraph createNewParagraphAt(int i) {
	Object object = null;
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	collectEmptyRuns();
	TextStyleRun textstylerun = runForCharPosition(i);
	int i_107_ = i - textstylerun.rangeIndex();
	TextParagraph textparagraph_108_
	    = new TextParagraph(_owner, textparagraphformat);
	int i_109_;
	if (i_107_ == 0) {
	    i_109_ = _runVector.indexOfIdentical(textstylerun);
	    Hashtable hashtable
		= (TextView.attributesByRemovingStaticAttributes
		   (textstylerun.attributes()));
	    if (i_109_ == 0) {
		TextStyleRun textstylerun_110_
		    = textstylerun.createEmptyRun(hashtable);
		_runVector.insertElementAt(textstylerun_110_, 0);
	    } else
		i_109_--;
	    textparagraph_108_.addRun(new TextStyleRun(this, "", hashtable));
	} else if (textstylerun.containsATextAttachment()) {
	    i_109_ = _runVector.indexOfIdentical(textstylerun);
	    int i_111_ = i_109_ - 1;
	    TextStyleRun textstylerun_112_ = null;
	    for (/**/; i_111_ > 0; i_111_--) {
		textstylerun_112_
		    = (TextStyleRun) objectAt(_runVector, i_111_);
		if (textstylerun_112_ == null
		    || !textstylerun_112_.containsATextAttachment())
		    break;
	    }
	    if (textstylerun_112_ == null) {
		TextStyleRun textstylerun_113_
		    = new TextStyleRun(this, "", null);
		textparagraph_108_.addRun(textstylerun_113_);
	    } else {
		TextStyleRun textstylerun_114_
		    = (new TextStyleRun
		       (this, "",
			(TextView.attributesByRemovingStaticAttributes
			 (textstylerun_112_.attributes()))));
		textparagraph_108_.addRun(textstylerun_114_);
	    }
	} else {
	    TextStyleRun textstylerun_116_;
	    TextStyleRun textstylerun_115_
		= textstylerun_116_ = textstylerun.breakAt(i_107_);
	    textparagraph_108_.addRun(textstylerun_116_);
	    i_109_ = _runVector.indexOfIdentical(textstylerun);
	}
	if (i_109_ < 0)
	    return textparagraph_108_;
	i_109_++;
	int i_117_ = _runVector.count();
	int i_118_ = 0;
	while (i_109_ < i_117_) {
	    textparagraph_108_
		.addRun((TextStyleRun) _runVector.elementAt(i_109_));
	    i_109_++;
	    i_118_++;
	}
	while (i_118_-- > 0)
	    _runVector.removeLastElement();
	return textparagraph_108_;
    }
    
    void drawBackgroundForLine(Graphics graphics, int i, int i_119_,
			       int i_120_) {
	Rect rect = null;
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	TextParagraphFormat textparagraphformat_121_
	    = ((TextParagraphFormat)
	       _owner.defaultAttributes().get("ParagraphFormatKey"));
	int i_122_ = (_owner.bounds.width - textparagraphformat._leftMargin
		      - textparagraphformat._rightMargin);
	int i_123_
	    = (_owner.bounds.width - textparagraphformat_121_._leftMargin
	       - textparagraphformat_121_._rightMargin);
	if (i == 0)
	    i_122_ -= textparagraphformat._leftIndent;
	if (i < 0 || i >= _breakCount
	    || !_owner.hasSelectionRange() && !_owner.insertionPointVisible) {
	    if (!_owner.isTransparent()) {
		graphics.setColor(_owner._backgroundColor);
		graphics.fillRect(textparagraphformat_121_._leftMargin, i_120_,
				  i_123_, _lineHeights[i]);
	    }
	} else {
	    int i_124_ = _owner.selectionStart();
	    int i_125_ = _owner.selectionEnd();
	    TextPositionInfo textpositioninfo = _owner.selectionStartInfo();
	    TextPositionInfo textpositioninfo_126_ = _owner.selectionEndInfo();
	    int i_127_;
	    if (i == 0)
		i_127_ = _startChar;
	    else
		i_127_ = _startChar + _lineBreaks[i - 1];
	    int i_128_ = _startChar + _lineBreaks[i];
	    if (i == _breakCount - 1)
		i_128_++;
	    if (i_124_ == i_125_ || i_124_ > i_128_ || i_125_ < i_127_) {
		if (!_owner.isTransparent()) {
		    graphics.setColor(_owner._backgroundColor);
		    graphics.fillRect(textparagraphformat_121_._leftMargin,
				      i_120_, i_123_, _lineHeights[i]);
		}
	    } else {
		Rect rect_129_;
		Rect rect_130_;
		if (i_127_ >= i_124_ && i_128_ <= i_125_) {
		    rect = TextView.newRect((textparagraphformat_121_
					     ._leftMargin),
					    i_120_, i_123_, _lineHeights[i]);
		    rect_129_ = rect_130_ = null;
		} else if (i_127_ >= i_124_ && i_128_ > i_125_
			   && i_127_ < i_125_) {
		    if (i_124_ == i_127_)
			rect = TextView.newRect(i_119_, i_120_,
						(textpositioninfo_126_._x
						 - i_119_),
						_lineHeights[i]);
		    else
			rect = TextView.newRect((textparagraphformat_121_
						 ._leftMargin),
						i_120_,
						(textpositioninfo_126_._x
						 - (textparagraphformat_121_
						    ._leftMargin)),
						_lineHeights[i]);
		    rect_129_ = TextView.newRect(rect.maxX(), i_120_,
						 i_123_ - rect.width,
						 _lineHeights[i]);
		    rect_130_ = null;
		} else if (i_127_ < i_124_ && i_124_ < i_128_
			   && i_128_ <= i_125_) {
		    if (textpositioninfo._textRun._paragraph != this
			|| textpositioninfo._lineNumber > i)
			rect_129_ = TextView.newRect((textparagraphformat_121_
						      ._leftMargin),
						     i_120_, i_123_,
						     _lineHeights[i]);
		    else {
			rect = TextView.newRect(textpositioninfo._x, i_120_,
						(_owner.bounds.width
						 - (textparagraphformat_121_
						    ._rightMargin)
						 - textpositioninfo._x),
						_lineHeights[i]);
			rect_129_
			    = (TextView.newRect
			       (textparagraphformat_121_._leftMargin, i_120_,
				rect.x - textparagraphformat_121_._leftMargin,
				_lineHeights[i]));
		    }
		    rect_130_ = null;
		} else if (i_127_ < i_124_ && i_128_ > i_125_) {
		    rect = TextView.newRect(textpositioninfo._x, i_120_,
					    (textpositioninfo_126_._x
					     - textpositioninfo._x),
					    _lineHeights[i]);
		    rect_129_ = TextView.newRect(rect.maxX(), i_120_,
						 i_123_ - rect.width,
						 _lineHeights[i]);
		    rect_130_
			= TextView.newRect((textparagraphformat_121_
					    ._leftMargin),
					   i_120_,
					   rect.x - (textparagraphformat_121_
						     ._leftMargin),
					   _lineHeights[i]);
		} else {
		    rect_129_
			= TextView.newRect((textparagraphformat_121_
					    ._leftMargin),
					   i_120_, i_123_, _lineHeights[i]);
		    rect_130_ = null;
		}
		if (!_owner.isTransparent()) {
		    graphics.setColor(_owner._backgroundColor);
		    if (rect_129_ != null) {
			graphics.fillRect(rect_129_);
			TextView.returnRect(rect_129_);
		    }
		    if (rect_130_ != null) {
			graphics.fillRect(rect_130_);
			TextView.returnRect(rect_130_);
		    }
		}
		if (_owner.hasSelectionRange() && rect != null
		    && _owner.isSelectable()) {
		    if (rect.width == 0 && rect.x == i_119_)
			rect.sizeTo(4, rect.height);
		    if (!_owner.isEditing()) {
			if (!_owner.isTransparent()) {
			    graphics.setColor(_owner._backgroundColor);
			    graphics.fillRect(rect);
			}
		    } else {
			graphics.setColor(_owner._selectionColor);
			graphics.fillRect(rect);
		    }
		    TextView.returnRect(rect);
		}
	    }
	}
    }
    
    void drawLine(Graphics graphics, int i) {
	boolean bool = false;
	int i_131_ = 1;
	int i_132_ = 0;
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	if (i < _breakCount) {
	    int i_133_ = (_owner.bounds.width - textparagraphformat._leftMargin
			  - textparagraphformat._rightMargin);
	    int i_134_ = _y;
	    if (!_owner.isTransparent()) {
		graphics.setColor(_owner._backgroundColor);
		graphics.fillRect(0, i_134_, textparagraphformat._leftMargin,
				  _height);
		graphics.fillRect((_owner.bounds.width
				   - textparagraphformat._rightMargin),
				  i_134_, textparagraphformat._rightMargin + 1,
				  _height);
	    }
	    TextStyleRun textstylerun
		= (TextStyleRun) _runVector.firstElement();
	    int i_135_ = 0;
	    for (int i_136_ = 0; i_136_ <= i; i_136_++) {
		int i_137_;
		if (textparagraphformat._justification == 0)
		    i_137_ = textparagraphformat._leftMargin;
		else if (textparagraphformat._justification == 2)
		    i_137_ = (textparagraphformat._leftMargin
			      + _lineRemainders[i_136_]);
		else
		    i_137_ = (textparagraphformat._leftMargin
			      + _lineRemainders[i_136_] / 2);
		int i_138_ = i_133_;
		if (i_136_ == 0) {
		    i_137_ += textparagraphformat._leftIndent;
		    i_138_ -= textparagraphformat._leftIndent;
		} else if (textparagraphformat.wrapsUnderFirstCharacter()) {
		    int i_139_
			= addWidthOfInitialTabs(i_137_ + (textparagraphformat
							  ._leftIndent));
		    i_138_ -= i_139_ - i_137_;
		    i_137_ = i_139_;
		}
		int i_140_;
		if (i_136_ == 0)
		    i_140_ = _lineBreaks[i_136_];
		else
		    i_140_ = _lineBreaks[i_136_] - _lineBreaks[i_136_ - 1];
		if (i_136_ == i)
		    drawBackgroundForLine(graphics, i_136_, i_137_, i_134_);
		while (i_140_ > 0) {
		    int i_141_ = textstylerun.charCount() - i_135_;
		    if (i_140_ >= i_141_) {
			if (i_136_ == i)
			    i_132_ = (textstylerun.drawCharacters
				      (graphics, i_135_, i_141_, i_137_,
				       i_134_ + _baselines[i_136_],
				       textparagraphformat._tabStops));
			i_140_ -= i_141_;
			for (textstylerun = (TextStyleRun) objectAt(_runVector,
								    i_131_++);
			     (textstylerun != null
			      && textstylerun.charCount() == 0);
			     textstylerun
				 = (TextStyleRun) objectAt(_runVector,
							   i_131_++)) {
			    /* empty */
			}
			i_135_ = 0;
		    } else {
			if (i_136_ == i)
			    i_132_ = (textstylerun.drawCharacters
				      (graphics, i_135_, i_140_, i_137_,
				       i_134_ + _baselines[i_136_],
				       textparagraphformat._tabStops));
			i_135_ += i_140_;
			i_140_ = 0;
		    }
		    i_137_ += i_132_;
		    i_138_ -= i_132_;
		}
		i_134_ += _lineHeights[i_136_];
	    }
	}
    }
    
    void drawView(Graphics graphics, Rect rect) {
	boolean bool = false;
	int i = 1;
	TextParagraphFormat textparagraphformat = currentParagraphFormat();
	TextParagraphFormat textparagraphformat_142_
	    = ((TextParagraphFormat)
	       _owner.defaultAttributes().get("ParagraphFormatKey"));
	int i_143_ = (_owner.bounds.width - textparagraphformat._leftMargin
		      - textparagraphformat._rightMargin);
	int i_144_ = _y;
	if (!_owner.isTransparent()) {
	    graphics.setColor(_owner._backgroundColor);
	    graphics.fillRect((rect.maxX()
			       - textparagraphformat_142_._rightMargin),
			      i_144_,
			      textparagraphformat_142_._rightMargin + 1,
			      _height);
	}
	TextStyleRun textstylerun = (TextStyleRun) _runVector.firstElement();
	int i_145_ = 0;
	Rect rect_146_ = TextView.newRect();
	Rect rect_147_ = graphics.clipRect();
	for (int i_148_ = 0; i_148_ < _breakCount; i_148_++) {
	    int i_149_;
	    if (textparagraphformat._justification == 0)
		i_149_ = rect.x + textparagraphformat._leftMargin;
	    else if (textparagraphformat._justification == 2)
		i_149_ = (rect.x + textparagraphformat._leftMargin
			  + _lineRemainders[i_148_]);
	    else
		i_149_ = (rect.x + textparagraphformat._leftMargin
			  + _lineRemainders[i_148_] / 2);
	    int i_150_ = i_143_;
	    if (i_148_ == 0) {
		i_149_ += textparagraphformat._leftIndent;
		i_150_ -= textparagraphformat._leftIndent;
	    } else if (textparagraphformat.wrapsUnderFirstCharacter()) {
		int i_151_
		    = addWidthOfInitialTabs(i_149_
					    + textparagraphformat._leftIndent);
		i_150_ -= i_151_ - i_149_;
		i_149_ = i_151_;
	    }
	    int i_152_;
	    if (i_148_ == 0)
		i_152_ = _lineBreaks[i_148_];
	    else
		i_152_ = _lineBreaks[i_148_] - _lineBreaks[i_148_ - 1];
	    rect_146_.setBounds(0, i_144_, _owner.bounds.width,
				_lineHeights[i_148_]);
	    boolean bool_153_ = rect_147_.intersects(rect_146_);
	    if (!_owner.isTransparent()) {
		graphics.setColor(_owner.backgroundColor());
		graphics.fillRect(rect.x, i_144_, i_149_ - rect.x,
				  _lineHeights[i_148_]);
	    }
	    if (bool_153_)
		drawBackgroundForLine(graphics, i_148_, i_149_, i_144_);
	    while (i_152_ > 0) {
		int i_154_ = textstylerun.charCount() - i_145_;
		int i_155_;
		if (i_152_ >= i_154_) {
		    if (bool_153_)
			i_155_
			    = textstylerun.drawCharacters(graphics, i_145_,
							  i_154_, i_149_,
							  i_144_ + (_baselines
								    [i_148_]),
							  (textparagraphformat
							   ._tabStops));
		    else
			i_155_ = 0;
		    i_152_ -= i_154_;
		    for (textstylerun
			     = (TextStyleRun) objectAt(_runVector, i++);
			 textstylerun != null && textstylerun.charCount() == 0;
			 textstylerun
			     = (TextStyleRun) objectAt(_runVector, i++)) {
			/* empty */
		    }
		    i_145_ = 0;
		} else {
		    if (bool_153_)
			i_155_
			    = textstylerun.drawCharacters(graphics, i_145_,
							  i_152_, i_149_,
							  i_144_ + (_baselines
								    [i_148_]),
							  (textparagraphformat
							   ._tabStops));
		    else
			i_155_ = 0;
		    i_145_ += i_152_;
		    i_152_ = 0;
		}
		i_149_ += i_155_;
		i_150_ -= i_155_;
	    }
	    i_144_ += _lineHeights[i_148_];
	}
	TextView.returnRect(rect_146_);
    }
    
    void draw() {
	if (_owner != null) {
	    Rect rect = TextView.newRect(0, _y, _owner.bounds.width, _height);
	    _owner.draw(rect);
	    TextView.returnRect(rect);
	}
    }
    
    void subsumeParagraph(TextParagraph textparagraph_156_) {
	if (textparagraph_156_ != null) {
	    int i = textparagraph_156_._runVector.count();
	    for (int i_157_ = 0; i_157_ < i; i_157_++) {
		TextStyleRun textstylerun
		    = ((TextStyleRun)
		       textparagraph_156_._runVector.elementAt(i_157_));
		if (textstylerun.charCount() > 0
		    || _runVector.isEmpty() && i_157_ + 1 == i)
		    addRun(textstylerun);
	    }
	}
    }
    
    public String toString() {
	FastStringBuffer faststringbuffer = new FastStringBuffer();
	faststringbuffer.append("[");
	int i = _runVector.count();
	for (int i_158_ = 0; i_158_ < i; i_158_++) {
	    TextStyleRun textstylerun
		= (TextStyleRun) _runVector.elementAt(i_158_);
	    faststringbuffer.append(textstylerun.toString());
	}
	faststringbuffer.append("]\n");
	return faststringbuffer.toString();
    }
    
    String stringForRange(Range range) {
	StringBuffer stringbuffer = new StringBuffer();
	int i = _startChar;
	Range range_159_ = TextView.allocateRange();
	int i_160_ = 0;
	for (int i_161_ = _runVector.count(); i_160_ < i_161_; i_160_++) {
	    TextStyleRun textstylerun
		= (TextStyleRun) _runVector.elementAt(i_160_);
	    range_159_.index = i;
	    range_159_.length = textstylerun.charCount();
	    range_159_.intersectWith(range);
	    if (range_159_.index != Range.nullRange().index) {
		String string
		    = textstylerun.text().substring(range_159_.index - i,
						    (range_159_.index - i
						     + range_159_.length));
		stringbuffer.append(string);
	    }
	    i += textstylerun.charCount();
	}
	if (range.index + range.length - 1 == _startChar + _charCount - 1)
	    stringbuffer.append("\n");
	TextView.recycleRange(range_159_);
	return stringbuffer.toString();
    }
    
    Range range() {
	return TextView.allocateRange(_startChar, _charCount);
    }
    
    int lineCount() {
	return _breakCount;
    }
}
