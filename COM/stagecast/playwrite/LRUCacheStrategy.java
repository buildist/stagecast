/* LRUCacheStrategy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;

public class LRUCacheStrategy implements CacheStrategy, Debug.Constants
{
    private final LRUnode _head = new LRUnode("<head>", null);
    private final LRUnode _tail = new LRUnode("<tail>", null);
    private CacheStrategy.CacheListenerManager _listenerManager
	= new CacheStrategy.CacheListenerManager();
    private Hashtable _cacheMap = new Hashtable();
    private long _checkoutCount = 0L;
    private double _averageCount = 0.0;
    
    private static class LRUnode
    {
	public LRUnode _prev;
	public LRUnode _next;
	public Object _key;
	public MemoryConsumer _data;
	
	public LRUnode(LRUnode prev, LRUnode next, Object key,
		       MemoryConsumer data) {
	    _prev = prev;
	    _next = next;
	    _key = key;
	    _data = data;
	}
	
	public LRUnode(Object key, MemoryConsumer data) {
	    this(null, null, key, data);
	}
	
	public final boolean isCheckedOut() {
	    return _prev == null && _next == null;
	}
	
	public final void checkout() {
	    _prev = null;
	    _next = null;
	}
    }
    
    public LRUCacheStrategy() {
	_head._next = _tail;
	_tail._prev = _head;
    }
    
    protected void finalize() throws Throwable {
	super.finalize();
	Debug.print("debug.image", ("Average number of items checked in = "
				    + (int) _averageCount));
    }
    
    public void printLRUList(String text) {
	LRUnode node = _head;
	int s = 0;
	if (text != null)
	    System.out.println("---------------- " + text);
	while (node != _tail) {
	    System.out.println(String.valueOf(s) + " node: " + node.hashCode()
			       + " " + node._key + " @ " + node._prev
			       + "<-- n -->" + node._next + " @");
	    node = node._next;
	    s++;
	}
    }
    
    public boolean contains(Object key) {
	return _cacheMap.containsKey(key);
    }
    
    public void addToCache(Object key, MemoryConsumer data) {
	if (_cacheMap.contains(key))
	    throw new CacheStrategy.IllegalCheckOutStateException
		      ("Attempted to add item to cache that is already in the cache");
	_cacheMap.put(key, new LRUnode(key, data));
	_listenerManager.onCacheEntryAdded(data);
    }
    
    public MemoryConsumer checkOut(Object key) {
	LRUnode item = (LRUnode) _cacheMap.get(key);
	if (item == null)
	    throw new CacheStrategy.IllegalCheckOutStateException
		      ("Attempted to check-out data that is not in the cache");
	if (item.isCheckedOut())
	    throw new CacheStrategy.IllegalCheckOutStateException
		      ("Attempted to multiply check-out data");
	item._next._prev = item._prev;
	item._prev._next = item._next;
	item.checkout();
	_checkoutCount++;
	_averageCount -= ((_averageCount - (double) _cacheMap.size())
			  / (double) _checkoutCount);
	return item._data;
    }
    
    public void checkIn(Object key)
	throws CacheStrategy.IllegalCheckOutStateException {
	LRUnode item = (LRUnode) _cacheMap.get(key);
	if (item == null)
	    throw new CacheStrategy.IllegalCheckOutStateException
		      ("Attempted to check-in data that is not in the cache");
	if (!item.isCheckedOut())
	    throw new CacheStrategy.IllegalCheckOutStateException
		      ("Attempted to check-in data that is in the cache but is not checked-out");
	item._prev = _tail._prev;
	item._next = _tail;
	_tail._prev._next = item;
	_tail._prev = item;
    }
    
    public MemoryConsumer remove() {
	LRUnode item = _head._next;
	if (item == _tail)
	    return null;
	return remove(item._key);
    }
    
    public MemoryConsumer remove(Object key) {
	LRUnode item = (LRUnode) _cacheMap.remove(key);
	if (item == null)
	    throw new CacheStrategy.IllegalCheckOutStateException
		      ("Attempted to remove item that is not in the cache");
	if (item.isCheckedOut())
	    throw new CacheStrategy.IllegalCheckOutStateException
		      ("Attempted to remove data that is currently checked out");
	item._prev._next = item._next;
	item._next._prev = item._prev;
	_listenerManager.onCacheEntryRemoved(item._data);
	return item._data;
    }
    
    public void printStatistics() {
	int[] cutoffs = { 4, 64, 2147483647 };
	int[] bins = new int[cutoffs.length];
	int[] totals = new int[cutoffs.length];
	int checkInCount = 0;
	int checkOutCount = 0;
	Debug.print("debug.statistics", "Cache contains " + _cacheMap.size(),
		    " items");
	Enumeration items = _cacheMap.elements();
	while (items.hasMoreElements()) {
	    LRUnode item = (LRUnode) items.nextElement();
	    if (item.isCheckedOut())
		checkOutCount++;
	    else
		checkInCount++;
	    int sizeInK = item._data.memoryImpact();
	    for (int bin = 0; bin < bins.length; bin++) {
		if (sizeInK < cutoffs[bin]) {
		    bins[bin]++;
		    totals[bin] += sizeInK;
		    break;
		}
	    }
	}
	Debug.print("debug.statistics", "  " + checkInCount,
		    " items currently checked in");
	Debug.print("debug.statistics", "  " + checkOutCount,
		    " items currently checked out");
	int binTotal = 0;
	for (int bin = 0; bin < bins.length; bin++) {
	    binTotal += totals[bin];
	    Debug.print("debug.statistics", "  " + bins[bin],
			" items < " + cutoffs[bin],
			"K (" + totals[bin] + "K)");
	}
	Debug.print("debug.statistics", "  " + binTotal, "K total in cache");
    }
    
    public void addListener(CacheStrategy.CacheListener listener) {
	_listenerManager.addListener(listener);
    }
}
