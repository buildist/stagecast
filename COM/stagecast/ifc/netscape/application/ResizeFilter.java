/* ResizeFilter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

class ResizeFilter implements EventFilter
{
    public ApplicationEvent lastEvent;
    
    public Object filterEvents(Vector vector) {
	int i = vector.count();
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    Event event = (Event) vector.elementAt(i_0_);
	    if (event instanceof ApplicationEvent && event.type == -24
		&& event.processor == lastEvent.processor) {
		lastEvent = (ApplicationEvent) event;
		vector.removeElementAt(i_0_);
		i_0_--;
		i--;
	    }
	}
	return null;
    }
}
