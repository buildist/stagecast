/* TimerQueue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

class TimerQueue implements Runnable, ApplicationObserver
{
    Timer firstTimer;
    boolean running;
    
    public TimerQueue() {
	Application application = Application.application();
	application.addObserver(this);
	if (application.isRunning())
	    start();
    }
    
    synchronized void start() {
	if (running)
	    throw new InconsistencyException
		      ("Can't start a TimerQueue that is already running");
	Thread thread = new Thread(this, "TimerQueue");
	try {
	    if (thread.getPriority() > 1)
		thread.setPriority(thread.getPriority() - 1);
	    thread.setDaemon(true);
	} catch (SecurityException securityexception) {
	    /* empty */
	}
	thread.start();
	running = true;
    }
    
    synchronized void stop() {
	running = false;
	this.notify();
    }
    
    synchronized void addTimer(Timer timer, long l) {
	if (!timer.running) {
	    Timer timer_0_ = null;
	    Timer timer_1_;
	    for (timer_1_ = firstTimer; timer_1_ != null;
		 timer_1_ = timer_1_.nextTimer) {
		if (timer_1_.expirationTime > l)
		    break;
		timer_0_ = timer_1_;
	    }
	    if (timer_0_ == null)
		firstTimer = timer;
	    else
		timer_0_.nextTimer = timer;
	    timer.expirationTime = l;
	    timer.nextTimer = timer_1_;
	    timer.running = true;
	    this.notify();
	}
    }
    
    synchronized void removeTimer(Timer timer) {
	if (timer.running) {
	    Timer timer_2_ = null;
	    Timer timer_3_ = firstTimer;
	    boolean bool = false;
	    for (/**/; timer_3_ != null; timer_3_ = timer_3_.nextTimer) {
		if (timer_3_ == timer) {
		    bool = true;
		    break;
		}
		timer_2_ = timer_3_;
	    }
	    if (bool) {
		if (timer_2_ == null)
		    firstTimer = timer.nextTimer;
		else
		    timer_2_.nextTimer = timer.nextTimer;
		timer.expirationTime = 0L;
		timer.nextTimer = null;
		timer.running = false;
	    }
	}
    }
    
    synchronized boolean containsTimer(Timer timer) {
	return timer.running;
    }
    
    synchronized long postExpiredTimers() {
	long l;
	do {
	    Timer timer = firstTimer;
	    if (timer == null)
		return 0L;
	    long l_4_ = System.currentTimeMillis();
	    l = timer.expirationTime - l_4_;
	    if (l <= 0L) {
		timer.post(l_4_);
		removeTimer(timer);
		if (timer.repeats())
		    addTimer(timer, l_4_ + (long) timer.delay());
		try {
		    this.wait(1L);
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    }
	} while (l <= 0L);
	return l;
    }
    
    public synchronized void run() {
	while (running) {
	    long l = postExpiredTimers();
	    try {
		this.wait(l);
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	}
    }
    
    public synchronized String toString() {
	StringBuffer stringbuffer = new StringBuffer();
	stringbuffer.append("TimerQueue (");
	Timer timer = firstTimer;
	while (timer != null) {
	    stringbuffer.append(timer.toString());
	    timer = timer.nextTimer;
	    if (timer != null)
		stringbuffer.append(", ");
	}
	stringbuffer.append(")");
	return stringbuffer.toString();
    }
    
    public void focusDidChange(Application application, View view) {
	/* empty */
    }
    
    public void currentDocumentDidChange(Application application,
					 Window window) {
	/* empty */
    }
    
    public void applicationDidPause(Application application) {
	/* empty */
    }
    
    public void applicationDidResume(Application application) {
	/* empty */
    }
    
    public synchronized void applicationDidStart(Application application) {
	start();
    }
    
    public synchronized void applicationDidStop(Application application) {
	stop();
    }
}
