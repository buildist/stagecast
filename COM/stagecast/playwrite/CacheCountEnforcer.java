/* CacheCountEnforcer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class CacheCountEnforcer
    implements CacheEnforcer, CacheStrategy.CacheListener, Debug.Constants
{
    private CacheStrategy _cache;
    private int _defaultLimit;
    private int _minLimit;
    private int _currentLimit;
    private int _currentCacheCount = 0;
    private int _enforcerRemovedCount = 0;
    
    public CacheCountEnforcer(CacheStrategy cache, int defaultLimit,
			      int minLimit) {
	_cache = cache;
	_minLimit = minLimit > 0 ? minLimit : 0;
	_defaultLimit = defaultLimit > _minLimit ? defaultLimit : _minLimit;
	_currentLimit = _defaultLimit;
	_cache.addListener(this);
	Debug.print("debug.image", ("CacheCountEnforcer: default limit = "
				    + _defaultLimit + "."));
	Debug.print("debug.image",
		    "CacheCountEnforcer: minimum limit = " + _minLimit + ".");
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
	_currentCacheCount++;
	enforceLimit();
    }
    
    public void onCacheEntryRemoved(MemoryConsumer data) {
	_currentCacheCount--;
    }
    
    private void enforceLimit() {
	enforceLimit(_currentLimit);
    }
    
    private void enforceLimit(int limit) {
	while (_currentCacheCount > limit) {
	    Object data = _cache.remove();
	    _enforcerRemovedCount++;
	    if (data == null)
		break;
	}
    }
    
    public void printStatistics() {
	Debug.print("debug.statistics", "Current cache item count: ",
		    _currentCacheCount);
	Debug.print("debug.statistics",
		    ("Items removed due to cache count overflow: "
		     + _enforcerRemovedCount));
	_enforcerRemovedCount = 0;
    }
}
