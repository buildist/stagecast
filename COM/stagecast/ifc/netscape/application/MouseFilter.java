/* MouseFilter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

class MouseFilter implements EventFilter
{
    public Object filterEvents(Vector vector) {
	int i = vector.count();
	MouseEvent mouseevent = null;
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    Event event = (Event) vector.elementAt(i_0_);
	    if (event instanceof MouseEvent) {
		int i_1_ = event.type();
		if (i_1_ == -2 || i_1_ == -5) {
		    mouseevent = (MouseEvent) event;
		    vector.removeElementAt(i_0_);
		    i--;
		    i_0_--;
		}
	    } else if (!(event.processor() instanceof Timer))
		break;
	}
	return mouseevent;
    }
}
