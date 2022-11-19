/* TextParagraphFormat - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class TextParagraphFormat implements Cloneable
{
    int _leftIndent;
    int _leftMargin;
    int _rightMargin;
    int _lineSpacing;
    int[] _tabStops;
    int _justification;
    boolean _wrapsUnderFirstCharacter;
    
    public Object clone() {
	try {
	    Object object = super.clone();
	    TextParagraphFormat textparagraphformat_0_
		= (TextParagraphFormat) object;
	    if (_tabStops != null) {
		textparagraphformat_0_.clearAllTabPositions();
		textparagraphformat_0_.setTabPositions(_tabStops);
	    }
	    return object;
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new InconsistencyException(String.valueOf(this)
					     + ": clone() not supported :"
					     + clonenotsupportedexception);
	}
    }
    
    public void setLeftMargin(int i) {
	_leftMargin = i;
    }
    
    public int leftMargin() {
	return _leftMargin;
    }
    
    public void setLeftIndent(int i) {
	_leftIndent = i;
    }
    
    public int leftIndent() {
	return _leftIndent;
    }
    
    public void setRightMargin(int i) {
	_rightMargin = i;
    }
    
    public int rightMargin() {
	return _rightMargin;
    }
    
    public void setLineSpacing(int i) {
	_lineSpacing = i;
    }
    
    public int lineSpacing() {
	return _lineSpacing;
    }
    
    public void setJustification(int i) {
	if (_justification >= 0 && _justification <= 3)
	    _justification = i;
    }
    
    public int justification() {
	return _justification;
    }
    
    public void setWrapsUnderFirstCharacter(boolean bool) {
	_wrapsUnderFirstCharacter = bool;
    }
    
    public boolean wrapsUnderFirstCharacter() {
	if (_justification == 0)
	    return _wrapsUnderFirstCharacter;
	return false;
    }
    
    public void clearAllTabPositions() {
	_tabStops = null;
    }
    
    public void addTabPosition(int i) {
	if (i >= 0) {
	    if (_tabStops == null) {
		_growTabArrayTo(20);
		_tabStops[0] = i;
	    } else {
		int i_1_ = _tabStops.length;
		if (_tabStops[i_1_ - 1] != -1) {
		    _growTabArrayTo(_tabStops.length + 10);
		    i_1_ = _tabStops.length;
		}
		for (int i_2_ = 0; i_2_ < i_1_; i_2_++) {
		    if (_tabStops[i_2_] > i)
			break;
		    if (_tabStops[i_2_] == -1) {
			_tabStops[i_2_] = i;
			break;
		    }
		}
	    }
	}
    }
    
    public void setTabPositions(int[] is) {
	if (is != null) {
	    clearAllTabPositions();
	    int i = is.length;
	    for (int i_3_ = 0; i_3_ < i; i_3_++)
		addTabPosition(is[i_3_]);
	}
    }
    
    public int[] tabPositions() {
	int i = 0;
	int i_4_ = 0;
	for (int i_5_ = _tabStops.length; i_4_ < i_5_; i_4_++) {
	    if (_tabStops[i_4_] == -1)
		break;
	    i++;
	}
	int[] is = new int[i];
	System.arraycopy(_tabStops, 0, is, 0, i);
	return is;
    }
    
    public int positionForTab(int i) {
	if (_tabStops == null || i < 0 || i >= _tabStops.length)
	    return -1;
	return _tabStops[i];
    }
    
    private void _growTabArrayTo(int i) {
	if (i >= 1 && (_tabStops == null || _tabStops.length < i)) {
	    int[] is = _tabStops;
	    int i_6_ = i + 5;
	    _tabStops = new int[i_6_];
	    while (i_6_-- > 0)
		_tabStops[i_6_] = -1;
	    if (is != null) {
		int i_7_ = is.length;
		for (i_6_ = 0; i_6_ < i_7_; i_6_++)
		    _tabStops[i_6_] = is[i_6_];
	    }
	}
    }
    
    public String toString() {
	StringBuffer stringbuffer = new StringBuffer();
	stringbuffer.append("leftIndent = " + _leftIndent + " "
			    + "leftMargin = " + _leftMargin + " "
			    + "rightMargin = " + _rightMargin + " "
			    + "lineSpacing = " + _lineSpacing + " "
			    + "justification = " + _justification);
	return stringbuffer.toString();
    }
}
