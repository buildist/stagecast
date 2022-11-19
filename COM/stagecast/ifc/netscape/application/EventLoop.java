/* EventLoop - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class EventLoop implements Runnable
{
    Vector events = new Vector();
    Thread mainThread;
    private boolean shouldRun = false;
    static boolean letAwtThreadRun = true;
    Application application;
    
    public void addEvent(Event event) {
	synchronized (events) {
	    events.addElement(event);
	    events.notify();
	}
    }
    
    final void letAWTThreadRun() {
	if (letAwtThreadRun) {
	    if (shouldProcessSynchronously()) {
		Thread thread = Thread.currentThread();
		int i = thread.getPriority();
		int i_0_ = i - 1;
		if (i_0_ < 1)
		    i_0_ = 1;
		try {
		    thread.setPriority(i_0_);
		    Thread.yield();
		    thread.setPriority(i);
		} catch (SecurityException securityexception) {
		    letAwtThreadRun = false;
		}
	    }
	}
    }
    
    public void removeEvent(Event event) {
	synchronized (events) {
	    events.removeElement(event);
	}
    }
    
    public Object filterEvents(EventFilter eventfilter) {
	letAWTThreadRun();
	Object object;
	synchronized (events) {
	    object = eventfilter.filterEvents(events);
	    events.notify();
	}
	return object;
    }
    
    public Event getNextEvent() {
	Object object = null;
	Event event;
	synchronized (events) {
	    while (events.count() == 0) {
		try {
		    events.wait();
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    }
	    event = (Event) events.removeFirstElement();
	}
	return event;
    }
    
    public Event peekNextEvent() {
	letAWTThreadRun();
	Event event;
	synchronized (events) {
	    event = (Event) events.firstElement();
	}
	return event;
    }
    
    public void processEvent(Event event) {
	Object object = event.synchronousLock();
	Application application = Application.application();
	application.willProcessInternalEvent(event);
	application.willProcessEvent(event);
	event.processor().processEvent(event);
	if (object != null) {
	    synchronized (object) {
		event.clearSynchronousLock();
		object.notify();
	    }
	}
	application.didProcessEvent(event);
	application.didProcessInternalEvent(event);
    }
    
    public void run() {
	synchronized (this) {
	    if (mainThread != null)
		throw new InconsistencyException
			  ("Only one thread may run an EventLoop");
	    mainThread = Thread.currentThread();
	    shouldRun = true;
	}
	while (shouldRun) {
	    Event event = getNextEvent();
	    if (shouldRun) {
		try {
		    processEvent(event);
		} catch (Exception exception) {
		    System.err
			.println(Application.application().exceptionHeader());
		    exception.printStackTrace(System.err);
		    System.err.println("Restarting EventLoop.");
		    Application.application()
			.handleEventLoopException(exception);
		}
	    }
	}
	synchronized (this) {
	    mainThread = null;
	}
    }
    
    public synchronized void stopRunning() {
	ApplicationEvent applicationevent = new ApplicationEvent();
	shouldRun = false;
	applicationevent.type = -25;
	addEvent(applicationevent);
    }
    
    public synchronized boolean isRunning() {
	return shouldRun;
    }
    
    synchronized boolean shouldProcessSynchronously() {
	return mainThread == null || Thread.currentThread() == mainThread;
    }
    
    public void addEventAndWait(Event event) {
	if (Thread.currentThread() == mainThread)
	    throw new InconsistencyException
		      ("Can't call addEventAndWait from within the EventLoop's main thread");
	Object object = event.createSynchronousLock();
	synchronized (object) {
	    addEvent(event);
	    while (event.synchronousLock() != null) {
		try {
		    object.wait();
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    }
	}
    }
    
    public synchronized String toString() {
	return events.toString();
    }
}
