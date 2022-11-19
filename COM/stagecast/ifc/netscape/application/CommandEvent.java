/* CommandEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class CommandEvent extends Event implements EventProcessor
{
    Target target;
    String command;
    Object data;
    
    public CommandEvent() {
	this.setProcessor(this);
    }
    
    public CommandEvent(Target target, String string, Object object) {
	this();
	setTarget(target);
	setCommand(string);
	setData(object);
    }
    
    public void setTarget(Target target) {
	this.target = target;
    }
    
    public Target target() {
	return target;
    }
    
    public void setCommand(String string) {
	command = string;
    }
    
    public String command() {
	return command;
    }
    
    public void setData(Object object) {
	data = object;
    }
    
    public Object data() {
	return data;
    }
    
    public void processEvent(Event event) {
	if (target != null)
	    target.performCommand(command, data);
    }
}
