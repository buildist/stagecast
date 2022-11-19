/* CommandFilter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

class CommandFilter implements EventFilter
{
    Target target;
    String command;
    Object object;
    
    CommandFilter(Target target, String string, Object object) {
	this.target = target;
	command = string;
	this.object = object;
    }
    
    static final boolean stringEquals(String string, String string_0_) {
	if (string != null)
	    return string.equals(string_0_);
	return string_0_ == null;
    }
    
    public Object filterEvents(Vector vector) {
	int i = vector.count();
	while (i-- > 0) {
	    Object object = vector.elementAt(i);
	    if (object instanceof CommandEvent) {
		CommandEvent commandevent = (CommandEvent) object;
		if (target == commandevent.target
		    && stringEquals(command, commandevent.command))
		    vector.removeElementAt(i);
	    }
	}
	return null;
    }
}
