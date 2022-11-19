/* Timer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class Timer implements EventProcessor, EventFilter
{
    EventLoop eventLoop;
    Target target;
    String command;
    Object data;
    long timeStamp;
    int initialDelay;
    int delay;
    boolean repeats = true;
    boolean coalesce = true;
    boolean removeEvents;
    long expirationTime;
    Timer nextTimer;
    boolean running;
    
    public Timer(EventLoop eventloop, Target target, String string, int i) {
	if (eventloop == null)
	    throw new InconsistencyException("eventLoop parameter is null");
	eventLoop = eventloop;
	this.target = target;
	command = string;
	setDelay(i);
	setInitialDelay(i);
    }
    
    public Timer(Target target, String string, int i) {
	this(Application.application().eventLoop(), target, string, i);
    }
    
    TimerQueue timerQueue() {
	if (eventLoop != null && eventLoop.application != null)
	    return eventLoop.application.timerQueue();
	return Application.application().timerQueue();
    }
    
    public EventLoop eventLoop() {
	return eventLoop;
    }
    
    public void setTarget(Target target) {
	this.target = target;
    }
    
    public Target target() {
	return target;
    }
    
    public void setCommand(String string) {
	command = string;
    }
    
    public String command() {
	return command;
    }
    
    public void setData(Object object) {
	data = object;
    }
    
    public Object data() {
	return data;
    }
    
    public void setDelay(int i) {
	if (i < 0)
	    throw new InconsistencyException("Invalid initial delay: " + i);
	delay = i;
	if (isRunning()) {
	    TimerQueue timerqueue = timerQueue();
	    timerqueue.removeTimer(this);
	    removeEvents();
	    timerqueue.addTimer(this, System.currentTimeMillis() + (long) i);
	}
    }
    
    public int delay() {
	return delay;
    }
    
    public void setInitialDelay(int i) {
	if (i < 0)
	    throw new InconsistencyException("Invalid initial delay: " + i);
	initialDelay = i;
    }
    
    public int initialDelay() {
	return initialDelay;
    }
    
    public void setRepeats(boolean bool) {
	repeats = bool;
    }
    
    public boolean repeats() {
	return repeats;
    }
    
    public long timeStamp() {
	return timeStamp;
    }
    
    public void setCoalesce(boolean bool) {
	coalesce = bool;
    }
    
    public boolean doesCoalesce() {
	return coalesce;
    }
    
    public void start() {
	timerQueue().addTimer(this, (System.currentTimeMillis()
				     + (long) initialDelay()));
    }
    
    public boolean isRunning() {
	return timerQueue().containsTimer(this);
    }
    
    public void stop() {
	timerQueue().removeTimer(this);
	removeEvents();
    }
    
    synchronized void removeEvents() {
	removeEvents = true;
	eventLoop.filterEvents(this);
    }
    
    synchronized boolean peekEvent() {
	removeEvents = false;
	return eventLoop.filterEvents(this) != null;
    }
    
    public Object filterEvents(Vector vector) {
	int i = vector.count();
	while (i-- > 0) {
	    Event event = (Event) vector.elementAt(i);
	    if (event.processor() == this) {
		if (removeEvents)
		    vector.removeElementAt(i);
		else
		    return event;
	    }
	}
	return null;
    }
    
    public void processEvent(Event event) {
	timeStamp = event.timeStamp;
	if (target != null)
	    target.performCommand(command, data);
    }
    
    public String toString() {
	return ("Timer {target = " + target + "; command = " + command
		+ "; delay = " + delay + "; initialDelay = " + initialDelay
		+ "; repeats = " + repeats + "}");
    }
    
    void post(long l) {
	if (!coalesce || !peekEvent()) {
	    Event event = new Event(l);
	    event.setProcessor(this);
	    eventLoop.addEvent(event);
	}
    }
}
