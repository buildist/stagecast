/* ThreadSafeCacheStrategy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class ThreadSafeCacheStrategy implements CacheStrategy
{
    private CacheStrategy _delegate;
    
    public ThreadSafeCacheStrategy(CacheStrategy cache) {
	_delegate = cache;
    }
    
    public synchronized boolean contains(Object key) {
	return _delegate.contains(key);
    }
    
    public synchronized void addToCache(Object key, MemoryConsumer data) {
	_delegate.addToCache(key, data);
    }
    
    public synchronized MemoryConsumer checkOut(Object key) {
	return _delegate.checkOut(key);
    }
    
    public synchronized void checkIn(Object key)
	throws CacheStrategy.IllegalCheckOutStateException {
	_delegate.checkIn(key);
    }
    
    public synchronized MemoryConsumer remove() {
	return _delegate.remove();
    }
    
    public synchronized MemoryConsumer remove(Object key)
	throws CacheStrategy.IllegalCheckOutStateException {
	return _delegate.remove(key);
    }
    
    public void printStatistics() {
	_delegate.printStatistics();
    }
    
    public void addListener(CacheStrategy.CacheListener listener) {
	_delegate.addListener(listener);
    }
}
