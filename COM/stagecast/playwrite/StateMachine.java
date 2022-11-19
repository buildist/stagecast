/* StateMachine - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Vector;

public class StateMachine
{
    static final Object ILLEGAL_STATE = new String("*Illegal state*");
    private Object _state;
    private Vector _transitions;
    private Transition _cache;
    private Object _watchTarget;
    private Vector _watchers;
    
    private class Transition
    {
	Object _initialState;
	Object[] _legalTransitions;
	Object[] _newStateForTransition;
	
	Transition(Object initialState, Object[] legalTransitions,
		   Object[] newStates) {
	    _initialState = initialState;
	    _legalTransitions = legalTransitions;
	    _newStateForTransition = newStates;
	}
	
	Object newState(Object transition) {
	    for (int i = 0; i < _legalTransitions.length; i++) {
		if (_legalTransitions[i] == transition)
		    return _newStateForTransition[i];
	    }
	    return StateMachine.ILLEGAL_STATE;
	}
    }
    
    public StateMachine(Object initialState, Object watchTarget) {
	_state = initialState;
	_transitions = new Vector(4);
	_cache = null;
	_watchTarget = watchTarget;
	_watchers = new Vector(4);
    }
    
    public void addTransitions(Object state, Object[] transitionList,
			       Object[] stateList) {
	Transition t = new Transition(state, transitionList, stateList);
	_transitions.addElement(t);
	if (_state == state)
	    _cache = t;
    }
    
    public final void addWatcher(StateWatcher w) {
	_watchers.addElement(w);
    }
    
    public final void removeWatcher(StateWatcher w) {
	_watchers.removeElementIdentical(w);
    }
    
    private final void updateWatchers(Object oldState, Object trans,
				      Object newState) {
	int size = _watchers.size();
	for (int i = size - 1; i >= 0; i--) {
	    StateWatcher w = (StateWatcher) _watchers.elementAt(i);
	    w.stateChanged(_watchTarget, oldState, trans, newState);
	}
    }
    
    public Object getState() {
	return _state;
    }
    
    boolean isLegal(Object transition) {
	return _cache.newState(transition) != ILLEGAL_STATE;
    }
    
    public Object changeState(Object transition) {
	Object newState = _cache.newState(transition);
	Object oldState = _state;
	if (newState != ILLEGAL_STATE) {
	    for (int i = 0; i < _transitions.size(); i++) {
		_cache = (Transition) _transitions.elementAt(i);
		if (_cache._initialState == newState) {
		    _state = newState;
		    updateWatchers(oldState, transition, newState);
		    return newState;
		}
	    }
	    throw new PlaywriteInternalError("No transition supplied for "
					     + transition + " in state "
					     + _state);
	}
	throw new PlaywriteInternalError("Illegal state change from " + _state
					 + " by " + transition);
    }
}
