/* Point - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

public class Point
{
    public int x;
    public int y;
    private static Vector _pointCache = new Vector();
    private static boolean _cachePoints = true;
    
    public Point() {
	/* empty */
    }
    
    public Point(int i, int i_0_) {
	x = i;
	y = i_0_;
    }
    
    public Point(Point point_1_) {
	x = point_1_.x;
	y = point_1_.y;
    }
    
    public String toString() {
	return "(" + x + ", " + y + ")";
    }
    
    public void moveTo(int i, int i_2_) {
	x = i;
	y = i_2_;
    }
    
    public void moveBy(int i, int i_3_) {
	x += i;
	y += i_3_;
    }
    
    public boolean equals(Object object) {
	if (!(object instanceof Point))
	    return false;
	Point point_4_ = (Point) object;
	return point_4_.x == x && point_4_.y == y;
    }
    
    public int hashCode() {
	return x ^ y;
    }
    
    static Point newPoint(int i, int i_5_) {
	Point point;
	synchronized (_pointCache) {
	    if (!_cachePoints || _pointCache.isEmpty())
		return new Point(i, i_5_);
	    point = (Point) _pointCache.removeLastElement();
	}
	point.moveTo(i, i_5_);
	return point;
    }
    
    static Point newPoint(Point point) {
	if (point == null)
	    return newPoint(0, 0);
	return newPoint(point.x, point.y);
    }
    
    static Point newPoint() {
	return newPoint(0, 0);
    }
    
    static void returnPoint(Point point) {
	if (_cachePoints) {
	    synchronized (_pointCache) {
		if (_pointCache.count() < 30)
		    _pointCache.addElement(point);
	    }
	}
    }
    
    static void returnPoints(Vector vector) {
	if (vector != null && _cachePoints) {
	    int i = vector.count();
	    while (i-- > 0)
		returnPoint((Point) vector.elementAt(i));
	    vector.removeAllElements();
	}
    }
    
    static void setShouldCachePoints(boolean bool) {
	synchronized (_pointCache) {
	    _cachePoints = bool;
	    if (!_cachePoints)
		_pointCache.removeAllElements();
	}
    }
}
