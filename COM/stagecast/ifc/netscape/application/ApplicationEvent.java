/* ApplicationEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.Rectangle;

public class ApplicationEvent extends Event
{
    static final int GOT_FOCUS = -21;
    static final int LOST_FOCUS = -22;
    static final int UPDATE = -23;
    static final int RESIZE = -24;
    static final int STOP = -25;
    static final int APPLET_STOPPED = -26;
    static final int APPLET_STARTED = -27;
    static final int PRINT = -28;
    Object data;
    
    static ApplicationEvent newResizeEvent(int i, int i_0_) {
	ApplicationEvent applicationevent = new ApplicationEvent();
	applicationevent.type = -24;
	applicationevent.data = new Rect(0, 0, i, i_0_);
	return applicationevent;
    }
    
    static ApplicationEvent newUpdateEvent(java.awt.Graphics graphics) {
	ApplicationEvent applicationevent = new ApplicationEvent();
	Rectangle rectangle = graphics.getClipRect();
	applicationevent.type = -23;
	if (rectangle == null)
	    applicationevent.data = new Rect(0, 0, 2147483647, 2147483647);
	else
	    applicationevent.data
		= new Rect(rectangle.x, rectangle.y, rectangle.width,
			   rectangle.height);
	return applicationevent;
    }
    
    public static ApplicationEvent newFocusEvent(boolean bool) {
	ApplicationEvent applicationevent = new ApplicationEvent();
	applicationevent.type = bool ? -21 : -22;
	return applicationevent;
    }
    
    static ApplicationEvent newPrintEvent(java.awt.Graphics graphics) {
	ApplicationEvent applicationevent = new ApplicationEvent();
	applicationevent.type = -28;
	applicationevent.data = graphics;
	return applicationevent;
    }
    
    public String toString() {
	String string;
	switch (type) {
	case -21:
	    string = "GotFocus";
	    break;
	case -22:
	    string = "LostFocus";
	    break;
	case -23:
	    string = "Update";
	    break;
	case -24:
	    string = "Resize";
	    break;
	case -25:
	    string = "Stop";
	    break;
	case -26:
	    string = "AppletStopped";
	    break;
	case -27:
	    string = "AppletStarted";
	    break;
	case -28:
	    string = "Print";
	    break;
	default:
	    string = "Unknown Type";
	}
	return "ApplicationEvent: " + string;
    }
    
    Rect rect() {
	return (Rect) data;
    }
    
    java.awt.Graphics graphics() {
	return (java.awt.Graphics) data;
    }
}
