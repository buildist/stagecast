/* Event - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class Event implements Cloneable
{
    EventProcessor processor;
    Object synchronousLock;
    long timeStamp;
    int type;
    
    public Event() {
	this(System.currentTimeMillis());
    }
    
    public Event(long l) {
	timeStamp = l;
    }
    
    public void setType(int i) {
	type = i;
    }
    
    public int type() {
	return type;
    }
    
    public void setTimeStamp(long l) {
	timeStamp = l;
    }
    
    public long timeStamp() {
	return timeStamp;
    }
    
    public void setProcessor(EventProcessor eventprocessor) {
	processor = eventprocessor;
    }
    
    public EventProcessor processor() {
	return processor;
    }
    
    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new InconsistencyException(String.valueOf(this)
					     + ": clone() not supported :"
					     + clonenotsupportedexception);
	}
    }
    
    synchronized Object synchronousLock() {
	return synchronousLock;
    }
    
    synchronized Object createSynchronousLock() {
	if (synchronousLock != null)
	    throw new InconsistencyException
		      ("Can't create synchronous lock if one is already set");
	synchronousLock = new Object();
	return synchronousLock;
    }
    
    synchronized void clearSynchronousLock() {
	if (synchronousLock == null)
	    throw new InconsistencyException
		      ("Can't clear synchronous lock if one isn't set");
	synchronousLock = null;
    }
}
