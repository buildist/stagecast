/* UpdateFilter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

class UpdateFilter implements EventFilter
{
    public Rect _rect;
    RootView rootView;
    
    UpdateFilter(Rect rect) {
	_rect = rect;
    }
    
    public Object filterEvents(Vector vector) {
	int i = vector.count();
	while (i-- > 0) {
	    Event event = (Event) vector.elementAt(i);
	    if (event instanceof ApplicationEvent && event.type == -23
		&& event.processor() == rootView) {
		ApplicationEvent applicationevent = (ApplicationEvent) event;
		Rect rect = applicationevent.rect();
		_rect.unionWith(rect);
		vector.removeElementAt(i);
	    }
	}
	return null;
    }
}
