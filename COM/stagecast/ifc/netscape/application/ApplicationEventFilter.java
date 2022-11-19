/* ApplicationEventFilter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

class ApplicationEventFilter implements EventFilter
{
    RootView rootView;
    
    ApplicationEventFilter(RootView rootview) {
	rootView = rootview;
    }
    
    public Object filterEvents(Vector vector) {
	int i = vector.count();
	while (i-- > 0) {
	    Event event = (Event) vector.elementAt(i);
	    if (event instanceof ApplicationEvent) {
		ApplicationEvent applicationevent = (ApplicationEvent) event;
		if (rootView == applicationevent.processor)
		    vector.removeElementAt(i);
	    }
	}
	return null;
    }
}
