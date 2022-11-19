/* BitmapCacheSizeEnforcer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class BitmapCacheSizeEnforcer
    implements CacheEnforcer, CacheStrategy.CacheListener, Debug.Constants
{
    private CacheStrategy _cache;
    private int _defaultLimit;
    private int _minLimit;
    private int _currentLimit;
    private int _currentCacheSize = 0;
    private int _enforcerRemovedCount = 0;
    
    public BitmapCacheSizeEnforcer(CacheStrategy cache, int defaultLimit,
				   int minLimit) {
	_cache = cache;
	_minLimit = minLimit > 0 ? minLimit : 0;
	_defaultLimit = defaultLimit > _minLimit ? defaultLimit : _minLimit;
	_currentLimit = _defaultLimit;
	_cache.addListener(this);
	Debug.print("debug.image", ("BitmapCacheSizeEnforcer: default limit = "
				    + _defaultLimit), "K");
	Debug.print("debug.image",
		    "BitmapCacheSizeEnforcer: minimum limit = " + _minLimit,
		    "K");
    }
    
    public void shrinkCache() {
	enforceLimit(_minLimit);
    }
    
    public void enforceMinLimit() {
	_currentLimit = _minLimit;
	enforceLimit();
    }
    
    public void enforceDefaultLimit() {
	_currentLimit = _defaultLimit;
	enforceLimit();
    }
    
    public void onCacheEntryAdded(MemoryConsumer data) {
	int itemSize = data.memoryImpact();
	_currentCacheSize += itemSize;
	enforceLimit();
    }
    
    public void onCacheEntryRemoved(MemoryConsumer data) {
	int itemSize = data.memoryImpact();
	_currentCacheSize -= itemSize;
    }
    
    private void enforceLimit() {
	enforceLimit(_currentLimit);
    }
    
    private void enforceLimit(int limit) {
	while (_currentCacheSize > limit) {
	    Object data = _cache.remove();
	    _enforcerRemovedCount++;
	    if (data == null)
		break;
	}
    }
    
    public void printStatistics() {
	Debug.print("debug.statistics",
		    "Current cache size: " + _currentCacheSize, "K");
	Debug.print("debug.statistics",
		    "Current cache limit: " + _currentLimit, "K");
	Debug.print("debug.statistics",
		    ("Items removed due to cache size overflow: "
		     + _enforcerRemovedCount));
	_enforcerRemovedCount = 0;
    }
}
