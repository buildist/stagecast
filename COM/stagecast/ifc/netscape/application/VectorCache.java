/* VectorCache - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

public class VectorCache
{
    private static Vector _vectorCache = new Vector();
    private static boolean _shouldCache = true;
    
    public static Vector newVector() {
	Vector vector;
	synchronized (_vectorCache) {
	    if (!_shouldCache || _vectorCache.isEmpty())
		return new Vector();
	    vector = (Vector) _vectorCache.removeLastElement();
	}
	return vector;
    }
    
    public static void returnVector(Vector vector) {
	if (_shouldCache) {
	    synchronized (_vectorCache) {
		if (vector != null && _vectorCache.count() < 15) {
		    vector.removeAllElements();
		    _vectorCache.addElement(vector);
		}
	    }
	}
    }
    
    static void setShouldCacheVectors(boolean bool) {
	synchronized (_vectorCache) {
	    _shouldCache = bool;
	    if (!_shouldCache)
		_vectorCache.removeAllElements();
	}
    }
}
