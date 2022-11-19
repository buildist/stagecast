/* CacheStrategy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

public interface CacheStrategy
{
    public static interface CacheListener
    {
	public void onCacheEntryAdded(MemoryConsumer memoryconsumer);
	
	public void onCacheEntryRemoved(MemoryConsumer memoryconsumer);
    }
    
    public static class CacheListenerManager implements CacheListener
    {
	private Vector _listeners;
	
	public void addListener(CacheListener listener) {
	    if (_listeners == null)
		_listeners = new Vector();
	    _listeners.addElement(listener);
	}
	
	public void onCacheEntryAdded(MemoryConsumer data) {
	    Enumeration listeners = _listeners.elements();
	    while (listeners.hasMoreElements())
		((CacheListener) listeners.nextElement())
		    .onCacheEntryAdded(data);
	}
	
	public void onCacheEntryRemoved(MemoryConsumer data) {
	    Enumeration listeners = _listeners.elements();
	    while (listeners.hasMoreElements())
		((CacheListener) listeners.nextElement())
		    .onCacheEntryRemoved(data);
	}
    }
    
    public static class IllegalCheckOutStateException extends RuntimeException
    {
	public IllegalCheckOutStateException(String description) {
	    super(description);
	}
    }
    
    public boolean contains(Object object);
    
    public void addToCache(Object object, MemoryConsumer memoryconsumer);
    
    public MemoryConsumer checkOut(Object object);
    
    public void checkIn(Object object) throws IllegalCheckOutStateException;
    
    public MemoryConsumer remove();
    
    public MemoryConsumer remove(Object object)
	throws IllegalCheckOutStateException;
    
    public void printStatistics();
    
    public void addListener(CacheListener cachelistener);
}
