/* WorkerThread - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.browser;

public abstract class WorkerThread
{
    Object value;
    Thread thread;
    
    public WorkerThread() {
	new Runnable() {
	    public void run() {
		finished();
	    }
	};
	Runnable runnable = new Runnable() {
	    public void run() {
		synchronized (WorkerThread.this) {
		    value = construct();
		    finished();
		    thread = null;
		}
	    }
	};
	thread = new Thread(runnable);
    }
    
    public void start() {
	thread.start();
    }
    
    public abstract Object construct();
    
    public void finished() {
	/* empty */
    }
    
    public void interrupt() {
	Thread thread = this.thread;
	if (thread != null)
	    thread.stop();
	this.thread = null;
    }
    
    public Object get() {
	return value;
    }
}
