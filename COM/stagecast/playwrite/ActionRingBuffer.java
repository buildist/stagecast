/* ActionRingBuffer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

class ActionRingBuffer
{
    private RuleAction[] buffer;
    private int beginning;
    private int end;
    private int ignoreClock;
    
    ActionRingBuffer(int size) {
	buffer = new RuleAction[size];
	beginning = -1;
	end = -1;
	ignoreClock = -1;
    }
    
    void add(RuleAction action) {
	if (ignoreClock > -1) {
	    if (action.getClockTick() == ignoreClock)
		return;
	    ignoreClock = -1;
	}
	end = end + 1;
	if (end >= buffer.length)
	    end = 0;
	RuleAction previous = buffer[end];
	buffer[end] = action;
	if (beginning == -1)
	    beginning = 0;
	else if (end == beginning) {
	    if (previous == null) {
		beginning = beginning + 1;
		if (beginning >= buffer.length)
		    beginning = 0;
	    } else {
		int prevTick = previous.getClockTick();
		if (prevTick == action.getClockTick()) {
		    reset();
		    ignoreClock = prevTick;
		} else {
		    do {
			beginning = beginning + 1;
			if (beginning >= buffer.length)
			    beginning = 0;
			action = buffer[beginning];
			if (action.getClockTick() == prevTick)
			    buffer[beginning] = null;
		    } while (action.getClockTick() == prevTick);
		}
	    }
	}
    }
    
    RuleAction removeLast() {
	ignoreClock = -1;
	if (isEmpty())
	    return null;
	RuleAction action = buffer[end];
	if (end == beginning) {
	    beginning = -1;
	    end = -1;
	} else {
	    end = end - 1;
	    if (end < 0)
		end = buffer.length - 1;
	}
	return action;
    }
    
    int lastClockTick() {
	if (isEmpty())
	    return -1;
	return buffer[end].getClockTick();
    }
    
    void reset() {
	beginning = -1;
	end = -1;
	ignoreClock = -1;
	for (int i = 0; i < buffer.length; i++)
	    buffer[i] = null;
    }
    
    final boolean isEmpty() {
	return end == -1;
    }
}
