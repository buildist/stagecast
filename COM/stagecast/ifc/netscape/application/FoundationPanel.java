/* FoundationPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.Frame;
import java.awt.Panel;

public class FoundationPanel extends Panel
{
    RootView rootView;
    
    public FoundationPanel() {
	if (Application.application().handleExtendedKeyEvent()
	    && JDK11AirLock.hasOneOneEvents())
	    JDK11AirLock.createFoundationPanelListener(this);
	setRootView(new RootView());
    }
    
    public FoundationPanel(int i, int i_0_) {
	setRootView(new RootView(0, 0, i, i_0_));
	resize(i, i_0_);
    }
    
    public RootView rootView() {
	return rootView;
    }
    
    public void setRootView(RootView rootview) {
	Application application = Application.application();
	if (rootView != null)
	    application.removeRootView(rootView);
	rootView = rootview;
	rootview.setPanel(this);
	rootview.setVisible(application.isPaused ^ true);
	application.addRootView(rootview);
    }
    
    public void resize(int i, int i_1_) {
	Application application = Application.application();
	super.resize(i, i_1_);
	if (application != null
	    && application.eventLoop.shouldProcessSynchronously())
	    rootView.processEvent(ApplicationEvent.newResizeEvent(i, i_1_));
	else
	    addEvent(ApplicationEvent.newResizeEvent(i, i_1_));
    }
    
    public void reshape(int i, int i_2_, int i_3_, int i_4_) {
	Application application = Application.application();
	super.reshape(i, i_2_, i_3_, i_4_);
	if (application != null
	    && application.eventLoop.shouldProcessSynchronously())
	    rootView.processEvent(ApplicationEvent.newResizeEvent(i_3_, i_4_));
	else
	    addEvent(ApplicationEvent.newResizeEvent(i_3_, i_4_));
    }
    
    public void update(java.awt.Graphics graphics) {
	paint(graphics);
    }
    
    public void paint(java.awt.Graphics graphics) {
	if (JDK11AirLock.isPrintGraphics(graphics))
	    paintNow(graphics);
	else {
	    addEvent(ApplicationEvent.newUpdateEvent(graphics));
	    super.paint(graphics);
	}
    }
    
    public boolean mouseDown(java.awt.Event event, int i, int i_5_) {
	this.requestFocus();
	addEvent(new MouseEvent(event.when, -1, i, i_5_, event.modifiers));
	return true;
    }
    
    public boolean mouseDrag(java.awt.Event event, int i, int i_6_) {
	addEvent(new MouseEvent(event.when, -2, i, i_6_, event.modifiers));
	return true;
    }
    
    public boolean mouseUp(java.awt.Event event, int i, int i_7_) {
	addEvent(new MouseEvent(event.when, -3, i, i_7_, event.modifiers));
	return true;
    }
    
    public boolean mouseEnter(java.awt.Event event, int i, int i_8_) {
	addEvent(new MouseEvent(event.when, -4, i, i_8_, event.modifiers));
	return true;
    }
    
    public boolean mouseMove(java.awt.Event event, int i, int i_9_) {
	addEvent(new MouseEvent(event.when, -5, i, i_9_, event.modifiers));
	return true;
    }
    
    public boolean mouseExit(java.awt.Event event, int i, int i_10_) {
	addEvent(new MouseEvent(event.when, -6, i, i_10_, event.modifiers));
	return true;
    }
    
    public boolean keyDown(java.awt.Event event, int i) {
	if (event.target == this) {
	    addEvent(new KeyEvent(event.when, i, event.modifiers, true));
	    return true;
	}
	return super.keyDown(event, i);
    }
    
    public boolean keyUp(java.awt.Event event, int i) {
	if (event.target == this) {
	    addEvent(new KeyEvent(event.when, i, event.modifiers, false));
	    return true;
	}
	return super.keyUp(event, i);
    }
    
    public synchronized boolean lostFocus(java.awt.Event event,
					  Object object) {
	addEvent(ApplicationEvent.newFocusEvent(false));
	return true;
    }
    
    public boolean gotFocus(java.awt.Event event, Object object) {
	addEvent(ApplicationEvent.newFocusEvent(true));
	return true;
    }
    
    public synchronized void addEvent(Event event) {
	if (event.processor() == null)
	    event.setProcessor(rootView);
	if (rootView != null && rootView.application() != null)
	    rootView.application().eventLoop().addEvent(event);
    }
    
    Frame frame() {
	java.awt.Container container;
	for (container = this.getParent();
	     container != null && !(container instanceof Frame);
	     container = container.getParent()) {
	    /* empty */
	}
	if (container != null)
	    return (Frame) container;
	return null;
    }
    
    public void setCursor(int i) {
	Frame frame = frame();
	if (frame != null)
	    frame.setCursor(i);
    }
    
    public void layout() {
	/* empty */
    }
    
    public void paintNow(java.awt.Graphics graphics) {
	Application application = Application.application();
	if (application == null && rootView != null)
	    application = rootView.application();
	if (application != null) {
	    if (application.eventLoop.shouldProcessSynchronously()) {
		Rect rect
		    = new Rect(0, 0, rootView.width(), rootView.height());
		Graphics graphics_11_ = new Graphics(rect, graphics);
		rootView.redraw(graphics_11_, rect);
		rootView.redrawTransparentWindows(graphics_11_, rect, null);
	    } else {
		ApplicationEvent applicationevent
		    = ApplicationEvent.newPrintEvent(graphics);
		applicationevent.processor = rootView;
		application.eventLoop().addEventAndWait(applicationevent);
	    }
	} else
	    System.err.println("Can't print with no application");
    }
    
    public void printAll(java.awt.Graphics graphics) {
	paintNow(graphics);
    }
}
