/* ToolTips - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.Hashtable;

import COM.stagecast.ifc.netscape.application.Application;

class ToolTips
{
    private static int DEFAULT_START_DELAY = 2000;
    static final int STOP_DELAY = 200;
    static final String ON = "ON";
    static final String OFF = "OFF";
    private static final Hashtable toolTipTable = new Hashtable(3);
    private boolean _on;
    private ToolTipable _currentView;
    private ToolTipWindow _tipWin;
    private int _startDelay = DEFAULT_START_DELAY;
    
    private ToolTips() {
	_on = false;
	_currentView = null;
	_tipWin = new ToolTipWindow();
    }
    
    static void createToolTips() {
	toolTipTable.put(Application.application(), new ToolTips());
    }
    
    static void destroyToolTips() {
	toolTipTable.remove(Application.application());
    }
    
    static void setOn(boolean b) {
	((ToolTips) toolTipTable.get(Application.application()))._setOn(b);
    }
    
    static ToolTipable getCurrentView() {
	return ((ToolTips) toolTipTable.get(Application.application()))
		   ._getCurrentView();
    }
    
    static void notifyEntered(ToolTipable v) {
	((ToolTips) toolTipTable.get(Application.application()))
	    ._notifyEntered(v);
    }
    
    static void notifyExited(ToolTipable v) {
	((ToolTips) toolTipTable.get(Application.application()))
	    ._notifyExited(v);
    }
    
    static void notifyMouseDown() {
	((ToolTips) toolTipTable.get(Application.application()))
	    ._notifyMouseDown();
    }
    
    public static void setToolTipDelay(int delay) {
	((ToolTips) toolTipTable.get(Application.application()))
	    ._setToolTipDelay(delay);
    }
    
    public static void resetTootTips() {
	((ToolTips) toolTipTable.get(Application.application()))
	    ._resetToolTips();
    }
    
    void _setOn(boolean b) {
	_on = b;
    }
    
    ToolTipable _getCurrentView() {
	return _currentView;
    }
    
    void _notifyEntered(ToolTipable v) {
	if (v.getToolTipText() != null) {
	    _currentView = v;
	    if (!_on)
		_tipWin.startTimer("ON", _startDelay);
	    else {
		_tipWin.showToolTips(v);
		_tipWin.stopTimer();
	    }
	}
    }
    
    void _notifyExited(ToolTipable v) {
	if (v.getToolTipText() != null) {
	    _currentView = null;
	    _tipWin.hideToolTips();
	    if (_on)
		_tipWin.startTimer("OFF", 200);
	    else
		_tipWin.stopTimer();
	}
    }
    
    void _notifyMouseDown() {
	_tipWin.hideToolTips();
	_tipWin.stopTimer();
	_on = false;
    }
    
    void _resetToolTips() {
	_notifyMouseDown();
	_setToolTipDelay(DEFAULT_START_DELAY);
    }
    
    void _setToolTipDelay(int delay) {
	_startDelay = delay;
	_tipWin.setToolTipDelay(delay);
    }
}
