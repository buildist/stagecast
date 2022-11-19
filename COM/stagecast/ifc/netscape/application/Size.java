/* Size - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

public class Size
{
    public int width;
    public int height;
    private static Vector _sizeCache = new Vector();
    private static boolean _cacheSizes = true;
    
    public Size() {
	/* empty */
    }
    
    public Size(int i, int i_0_) {
	width = i;
	height = i_0_;
    }
    
    public Size(Size size_1_) {
	width = size_1_.width;
	height = size_1_.height;
    }
    
    public boolean isEmpty() {
	return width == 0 || height == 0;
    }
    
    public String toString() {
	return "(" + width + ", " + height + ")";
    }
    
    public void sizeTo(int i, int i_2_) {
	width = i;
	height = i_2_;
    }
    
    public void sizeBy(int i, int i_3_) {
	sizeTo(width + i, height + i_3_);
    }
    
    public void union(Size size_4_) {
	if (width < size_4_.width)
	    width = size_4_.width;
	if (height < size_4_.height)
	    height = size_4_.height;
    }
    
    public boolean equals(Object object) {
	if (!(object instanceof Size))
	    return false;
	Size size_5_ = (Size) object;
	return size_5_.width == width && size_5_.height == height;
    }
    
    public int hashCode() {
	return width ^ height;
    }
    
    static Size newSize(int i, int i_6_) {
	Size size;
	synchronized (_sizeCache) {
	    if (!_cacheSizes || _sizeCache.isEmpty())
		return new Size(i, i_6_);
	    size = (Size) _sizeCache.removeLastElement();
	}
	size.sizeTo(i, i_6_);
	return size;
    }
    
    static Size newSize(Size size) {
	if (size == null)
	    return newSize(0, 0);
	return newSize(size.width, size.height);
    }
    
    static Size newSize() {
	return newSize(0, 0);
    }
    
    static void returnSize(Size size) {
	if (_cacheSizes) {
	    synchronized (_sizeCache) {
		if (_sizeCache.count() < 30)
		    _sizeCache.addElement(size);
	    }
	}
    }
    
    static void returnSizes(Vector vector) {
	if (vector != null && _cacheSizes) {
	    int i = vector.count();
	    while (i-- > 0)
		returnSize((Size) vector.elementAt(i));
	    vector.removeAllElements();
	}
    }
    
    static void setShouldCacheSizes(boolean bool) {
	synchronized (_sizeCache) {
	    _cacheSizes = bool;
	    if (!_cacheSizes)
		_sizeCache.removeAllElements();
	}
    }
}
