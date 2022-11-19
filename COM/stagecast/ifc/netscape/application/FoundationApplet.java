/* FoundationApplet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Insets;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class FoundationApplet extends Applet implements Runnable
{
    static final String defaultApplicationClass
	= "com.stagecast.creator.PlayerApplet";
    static Hashtable groupToApplet = new Hashtable(1);
    Application application;
    FoundationPanel panel;
    boolean startedRun;
    boolean appletStarted;
    private static Vector applicationStack;
    
    static void setAppletForGroup(FoundationApplet foundationapplet) {
	groupToApplet.put(Thread.currentThread().getThreadGroup(),
			  foundationapplet);
    }
    
    static FoundationApplet applet() {
	ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();
	return (FoundationApplet) groupToApplet.get(threadgroup);
    }
    
    public void setApplication(Application application) {
	this.application = application;
    }
    
    public Application application() {
	return application;
    }
    
    public void init() {
	Thread thread = new Thread(Thread.currentThread().getThreadGroup(),
				   this, "Main Application Thread");
	super.init();
	setAppletForGroup(this);
	thread.start();
	synchronized (this) {
	    while (!startedRun) {
		try {
		    this.wait();
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    }
	}
    }
    
    public void run() {
	String string = this.getParameter("ApplicationClass");
	if (string == null || string.equals(""))
	    string = "com.stagecast.creator.PlayerApplet";
	if (string != null && !string.equals("")) {
	    Object object = instantiateObjectOfClass(string);
	    if (object instanceof Application)
		application = (Application) object;
	    else
		throw new InconsistencyException
			  ("ApplicationClass " + string
			   + " must be a subclass of COM.stagecast.ifc.netscape.application.Application");
	} else
	    throw new InconsistencyException
		      ("An ApplicationClass parameter must be specified in the <applet> tag.  For example:\n<applet code=\"netscape.application.FoundationApplet\" width=320 height=200>\n    <param name=\"ApplicationClass\" value=\"MyApplication\">\n</applet>\n");
	synchronized (this) {
	    startedRun = true;
	    this.notifyAll();
	    while (!appletStarted) {
		try {
		    this.wait();
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    }
	}
	application.run();
    }
    
    public void start() {
	synchronized (this) {
	    ApplicationEvent applicationevent = new ApplicationEvent();
	    if (!appletStarted) {
		appletStarted = true;
		this.notifyAll();
	    }
	    applicationevent.type = -27;
	    applicationevent.processor = application;
	    application.eventLoop.addEvent(applicationevent);
	}
    }
    
    public void stop() {
	ApplicationEvent applicationevent = new ApplicationEvent();
	applicationevent.type = -26;
	applicationevent.processor = application;
	application.eventLoop.addEvent(applicationevent);
    }
    
    public void destroy() {
	application.stopRunningForAWT();
	super.destroy();
    }
    
    void destroyFromIFC() {
	super.destroy();
    }
    
    void cleanup() {
	Enumeration enumeration = groupToApplet.keys();
	removeApplication(application);
	while (enumeration.hasMoreElements()) {
	    ThreadGroup threadgroup = (ThreadGroup) enumeration.nextElement();
	    FoundationApplet foundationapplet_0_
		= (FoundationApplet) groupToApplet.get(threadgroup);
	    if (foundationapplet_0_ == this) {
		groupToApplet.remove(threadgroup);
		break;
	    }
	}
	application = null;
    }
    
    void setupCanvas(Application application) {
	this.application = application;
	int i = this.size().width;
	int i_1_ = this.size().height;
	panel = createPanel();
	this.application.setMainRootView(panel.rootView());
	panel.reshape(0, 0, i, i_1_);
	this.add(panel);
    }
    
    public void layout() {
	if (panel != null) {
	    Dimension dimension = this.size();
	    Insets insets = this.insets();
	    int i = insets.left;
	    int i_2_ = insets.top;
	    int i_3_ = dimension.width - (insets.left + insets.right);
	    int i_4_ = dimension.height - (insets.top + insets.bottom);
	    if (i_3_ > 0 && i_4_ > 0)
		panel.reshape(i, i_2_, i_3_, i_4_);
	}
    }
    
    Object instantiateObjectOfClass(String string) {
	Object object;
	try {
	    Class var_class = classForName(string);
	    object = var_class.newInstance();
	} catch (ClassNotFoundException classnotfoundexception) {
	    throw new InconsistencyException("Unable to find class \"" + string
					     + "\"");
	} catch (InstantiationException instantiationexception) {
	    throw new InconsistencyException("Unable to instantiate class \""
					     + string + "\" -- "
					     + instantiationexception
						   .getMessage());
	} catch (IllegalAccessException illegalaccessexception) {
	    throw new InconsistencyException("Unable to instantiate class \""
					     + string + "\" -- "
					     + illegalaccessexception
						   .getMessage());
	}
	return object;
    }
    
    public Class classForName(String string) throws ClassNotFoundException {
	return Class.forName(string);
    }
    
    public void paint(java.awt.Graphics graphics) {
	super.paint(graphics);
    }
    
    protected FoundationPanel createPanel() {
	return new FoundationPanel();
    }
    
    public FoundationPanel panel() {
	return panel;
    }
    
    static boolean isMozillaThread(Thread thread) {
	return true;
    }
    
    public Application pushIFCContext() {
	if (application == null)
	    return null;
	if (!isMozillaThread(Thread.currentThread()))
	    throw new InconsistencyException
		      ("pushIFCContext() must be called from JavaScript.");
	if (applicationStack == null)
	    applicationStack = new Vector();
	applicationStack.addElement(application);
	return application;
    }
    
    public void popIFCContext() {
	if (!isMozillaThread(Thread.currentThread()))
	    throw new InconsistencyException
		      ("popIFCContext() must be called from JavaScript.");
	if (applicationStack == null)
	    throw new InconsistencyException
		      ("popIFCContext() called without ever calling pushIFCContext()");
	if (applicationStack.lastElement() != application)
	    throw new InconsistencyException
		      ("popIFCContext() attempted to pop "
		       + applicationStack.lastElement()
		       + " which was not itself:" + application);
	if (applicationStack.removeLastElement() == null)
	    throw new InconsistencyException
		      ("extraneous popIFCContext() called without corresponding pushIFCContext()");
    }
    
    static Application currentApplication() {
	if (applicationStack == null)
	    return null;
	return (Application) applicationStack.lastElement();
    }
    
    static void removeApplication(Application application) {
	if (applicationStack != null && application != null)
	    applicationStack.removeAll(application);
    }
}
