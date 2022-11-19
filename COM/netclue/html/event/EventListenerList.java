/* EventListenerList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.event;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;

public class EventListenerList implements Serializable
{
    private static final Object[] NULL_ARRAY = new Object[0];
    protected transient Object[] listenerList = NULL_ARRAY;
    
    public Object[] getListenerList() {
	return listenerList;
    }
    
    public int getListenerCount() {
	return listenerList.length / 2;
    }
    
    public int getListenerCount(Class var_class) {
	int i = 0;
	Object[] objects = listenerList;
	for (int i_0_ = 0; i_0_ < objects.length; i_0_ += 2) {
	    if (var_class == (Class) objects[i_0_])
		i++;
	}
	return i;
    }
    
    public synchronized void add(Class var_class,
				 EventListener eventlistener) {
	if (!var_class.isInstance(eventlistener))
	    throw new IllegalArgumentException("Listener " + eventlistener
					       + " is not of type "
					       + var_class);
	if (eventlistener == null)
	    throw new IllegalArgumentException("Listener " + eventlistener
					       + " is null");
	if (listenerList == NULL_ARRAY)
	    listenerList = new Object[] { var_class, eventlistener };
	else {
	    int i = listenerList.length;
	    Object[] objects = new Object[i + 2];
	    System.arraycopy(listenerList, 0, objects, 0, i);
	    objects[i] = var_class;
	    objects[i + 1] = eventlistener;
	    listenerList = objects;
	}
    }
    
    public synchronized void remove(Class var_class,
				    EventListener eventlistener) {
	if (!var_class.isInstance(eventlistener))
	    throw new IllegalArgumentException("Listener " + eventlistener
					       + " is not of type "
					       + var_class);
	if (eventlistener == null)
	    throw new IllegalArgumentException("Listener " + eventlistener
					       + " is null");
	int i = -1;
	for (int i_1_ = listenerList.length - 2; i_1_ >= 0; i_1_ -= 2) {
	    if (listenerList[i_1_] == var_class
		&& listenerList[i_1_ + 1] == eventlistener) {
		i = i_1_;
		break;
	    }
	}
	if (i != -1) {
	    Object[] objects = new Object[listenerList.length - 2];
	    System.arraycopy(listenerList, 0, objects, 0, i);
	    if (i < objects.length)
		System.arraycopy(listenerList, i + 2, objects, i,
				 objects.length - i);
	    listenerList = objects.length == 0 ? NULL_ARRAY : objects;
	}
    }
    
    private void writeObject(ObjectOutputStream objectoutputstream)
	throws IOException {
	Object[] objects = listenerList;
	objectoutputstream.defaultWriteObject();
	for (int i = 0; i < objects.length; i += 2) {
	    Class var_class = (Class) objects[i];
	    EventListener eventlistener = (EventListener) objects[i + 1];
	    if (eventlistener != null
		&& eventlistener instanceof Serializable) {
		objectoutputstream.writeObject(var_class.getName());
		objectoutputstream.writeObject(eventlistener);
	    }
	}
	objectoutputstream.writeObject(null);
    }
    
    private void readObject(ObjectInputStream objectinputstream)
	throws IOException, ClassNotFoundException {
	listenerList = NULL_ARRAY;
	objectinputstream.defaultReadObject();
	Object object;
	while ((object = objectinputstream.readObject()) != null) {
	    EventListener eventlistener
		= (EventListener) objectinputstream.readObject();
	    add(Class.forName((String) object), eventlistener);
	}
    }
    
    public String toString() {
	Object[] objects = listenerList;
	String string = "EventListenerList: ";
	string += objects.length / 2 + " listeners: ";
	for (int i = 0; i <= objects.length - 2; i += 2) {
	    string += " type " + ((Class) objects[i]).getName();
	    string += " listener " + (Object) objects[i + 1];
	}
	return string;
    }
}
