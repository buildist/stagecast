/* Rect - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

public class Rect
{
    public int x;
    public int y;
    public int width;
    public int height;
    private static Vector _rectCache = new Vector();
    private static boolean _cacheRects = true;
    
    public static boolean contains(int i, int i_0_, int i_1_, int i_2_,
				   int i_3_, int i_4_) {
	return (i_3_ >= i && i_3_ < i + i_1_ && i_4_ >= i_0_
		&& i_4_ < i_0_ + i_2_);
    }
    
    public static Rect rectFromIntersection(Rect rect, Rect rect_5_) {
	Rect rect_6_ = new Rect(rect);
	rect_6_.intersectWith(rect_5_);
	return rect_6_;
    }
    
    public static Rect rectFromUnion(Rect rect, Rect rect_7_) {
	Rect rect_8_ = new Rect(rect);
	rect_8_.unionWith(rect_7_);
	return rect_8_;
    }
    
    public Rect() {
	/* empty */
    }
    
    public Rect(int i, int i_9_, int i_10_, int i_11_) {
	x = i;
	y = i_9_;
	width = i_10_;
	height = i_11_;
    }
    
    public Rect(Rect rect_12_) {
	x = rect_12_.x;
	y = rect_12_.y;
	width = rect_12_.width;
	height = rect_12_.height;
    }
    
    public String toString() {
	return "(" + x + ", " + y + ", " + width + ", " + height + ")";
    }
    
    public void setBounds(int i, int i_13_, int i_14_, int i_15_) {
	x = i;
	y = i_13_;
	width = i_14_ < 0 ? 0 : i_14_;
	height = i_15_ < 0 ? 0 : i_15_;
    }
    
    public void setBounds(Rect rect_16_) {
	if (rect_16_ == null)
	    setBounds(0, 0, 0, 0);
	else
	    setBounds(rect_16_.x, rect_16_.y, rect_16_.width, rect_16_.height);
    }
    
    public void setCoordinates(int i, int i_17_, int i_18_, int i_19_) {
	setBounds(i, i_17_, i_18_ - i, i_19_ - i_17_);
    }
    
    public void moveTo(int i, int i_20_) {
	x = i;
	y = i_20_;
    }
    
    public void moveBy(int i, int i_21_) {
	x += i;
	y += i_21_;
    }
    
    public void sizeTo(int i, int i_22_) {
	width = i < 0 ? 0 : i;
	height = i_22_ < 0 ? 0 : i_22_;
    }
    
    public void sizeBy(int i, int i_23_) {
	width += i;
	if (width < 0)
	    width = 0;
	height += i_23_;
	if (height < 0)
	    height = 0;
    }
    
    public void growBy(int i, int i_24_) {
	x -= i;
	y -= i_24_;
	width += 2 * i;
	height += 2 * i_24_;
	if (width < 0)
	    width = 0;
	if (height < 0)
	    height = 0;
    }
    
    public int maxX() {
	return x + width;
    }
    
    public int maxY() {
	return y + height;
    }
    
    public int midX() {
	return x + width / 2;
    }
    
    public int midY() {
	return y + height / 2;
    }
    
    public boolean equals(Object object) {
	if (!(object instanceof Rect))
	    return false;
	Rect rect_25_ = (Rect) object;
	return (rect_25_.x == x && rect_25_.y == y && rect_25_.width == width
		&& rect_25_.height == height);
    }
    
    public int hashCode() {
	return x ^ y ^ width ^ height;
    }
    
    public boolean isEmpty() {
	return width == 0 || height == 0;
    }
    
    public boolean contains(int i, int i_26_) {
	return i >= x && i < x + width && i_26_ >= y && i_26_ < y + height;
    }
    
    public boolean contains(Point point) {
	return contains(point.x, point.y);
    }
    
    public boolean contains(Rect rect_27_) {
	if (rect_27_ == null)
	    return false;
	if (rect_27_.x >= x && rect_27_.x + rect_27_.width <= x + width
	    && rect_27_.y >= y && rect_27_.y + rect_27_.height <= y + height)
	    return true;
	return false;
    }
    
    public boolean intersects(int i, int i_28_, int i_29_, int i_30_) {
	if (x >= i + i_29_ || x + width <= i || y >= i_28_ + i_30_
	    || y + height <= i_28_)
	    return false;
	return (width == 0 || height == 0 || i_29_ == 0 || i_30_ == 0) ^ true;
    }
    
    public boolean intersects(Rect rect_31_) {
	if (rect_31_ == null)
	    return false;
	return intersects(rect_31_.x, rect_31_.y, rect_31_.width,
			  rect_31_.height);
    }
    
    public void intersectWith(int i, int i_32_, int i_33_, int i_34_) {
	int i_35_ = i;
	int i_36_ = i_32_;
	int i_37_ = i_35_ + i_33_;
	int i_38_ = i_36_ + i_34_;
	int i_39_ = x + width;
	int i_40_ = y + height;
	if (x >= i_37_ || i_39_ <= i_35_)
	    i_35_ = i_37_ = 0;
	else if (x > i_35_ && x < i_37_) {
	    i_35_ = x;
	    if (i_39_ < i_37_)
		i_37_ = i_39_;
	} else if (i_39_ > i_35_ && i_39_ < i_37_)
	    i_37_ = i_39_;
	if (y >= i_38_ || i_40_ <= i_36_)
	    i_36_ = i_38_ = 0;
	else if (y > i_36_ && y < i_38_) {
	    i_36_ = y;
	    if (i_40_ < i_38_)
		i_38_ = i_40_;
	} else if (i_40_ > i_36_ && i_40_ < i_38_)
	    i_38_ = i_40_;
	setCoordinates(i_35_, i_36_, i_37_, i_38_);
    }
    
    public void intersectWith(Rect rect_41_) {
	intersectWith(rect_41_.x, rect_41_.y, rect_41_.width, rect_41_.height);
    }
    
    public Rect intersectionRect(Rect rect_42_) {
	int i = rect_42_.x;
	int i_43_ = rect_42_.y;
	int i_44_ = rect_42_.x + rect_42_.width;
	int i_45_ = rect_42_.y + rect_42_.height;
	int i_46_ = x + width;
	int i_47_ = y + height;
	if (x >= i_44_ || i_46_ <= i || y >= i_45_ || i_47_ <= i_43_)
	    return new Rect();
	if (x > i && x < i_44_) {
	    i = x;
	    if (i_46_ < i_44_)
		i_44_ = i_46_;
	} else if (i_46_ > i && i_46_ < i_44_)
	    i_44_ = i_46_;
	if (y > i_43_ && y < i_45_) {
	    i_43_ = y;
	    if (i_47_ < i_45_)
		i_45_ = i_47_;
	} else if (i_47_ > i_43_ && i_47_ < i_45_)
	    i_45_ = i_47_;
	return new Rect(i, i_43_, i_44_ - i, i_45_ - i_43_);
    }
    
    public void unionWith(int i, int i_48_, int i_49_, int i_50_) {
	int i_51_ = x < i ? x : i;
	int i_52_ = i + i_49_;
	if (x + width > i_52_)
	    i_52_ = x + width;
	int i_53_ = y < i_48_ ? y : i_48_;
	int i_54_ = i_48_ + i_50_;
	if (y + height > i_54_)
	    i_54_ = y + height;
	setCoordinates(i_51_, i_53_, i_52_, i_54_);
    }
    
    public void unionWith(Rect rect_55_) {
	if (rect_55_ != null)
	    unionWith(rect_55_.x, rect_55_.y, rect_55_.width, rect_55_.height);
    }
    
    public Rect unionRect(Rect rect_56_) {
	if (rect_56_ == null)
	    return new Rect(this);
	int i = x < rect_56_.x ? x : rect_56_.x;
	int i_57_ = rect_56_.x + rect_56_.width;
	int i_58_ = x + width;
	if (i_58_ > i_57_)
	    i_57_ = i_58_;
	int i_59_ = y < rect_56_.y ? y : rect_56_.y;
	int i_60_ = rect_56_.y + rect_56_.height;
	int i_61_ = y + height;
	if (i_61_ > i_60_)
	    i_60_ = i_61_;
	return new Rect(i, i_59_, i_57_ - i, i_60_ - i_59_);
    }
    
    void filterEmptyRects(Vector vector) {
	int i = vector.count();
	while (i-- > 0) {
	    Rect rect_62_ = (Rect) vector.elementAt(i);
	    if (rect_62_.width == 0 || rect_62_.height == 0)
		vector.removeElementAt(i);
	}
    }
    
    public void computeDisunionRects(Rect rect_63_, Vector vector) {
	if (rect_63_ != null && intersects(rect_63_) && vector != null
	    && !rect_63_.contains(this)) {
	    if (contains(rect_63_)) {
		vector.addElement(newRect(x, y, rect_63_.x - x, height));
		vector.addElement(newRect(rect_63_.x, y, rect_63_.width,
					  rect_63_.y - y));
		vector.addElement(newRect(rect_63_.x, rect_63_.maxY(),
					  rect_63_.width,
					  maxY() - rect_63_.maxY()));
		vector.addElement(newRect(rect_63_.maxX(), y,
					  maxX() - rect_63_.maxX(), height));
		filterEmptyRects(vector);
	    } else if (rect_63_.x <= x && rect_63_.y <= y) {
		if (rect_63_.maxX() > maxX())
		    vector.addElement(newRect(x, rect_63_.maxY(), width,
					      maxY() - rect_63_.maxY()));
		else if (rect_63_.maxY() > maxY())
		    vector.addElement(newRect(rect_63_.maxX(), y,
					      maxX() - rect_63_.maxX(),
					      height));
		else {
		    vector.addElement(newRect(rect_63_.maxX(), y,
					      maxX() - rect_63_.maxX(),
					      rect_63_.maxY() - y));
		    vector.addElement(newRect(x, rect_63_.maxY(), width,
					      maxY() - rect_63_.maxY()));
		}
		filterEmptyRects(vector);
	    } else if (rect_63_.x <= x && rect_63_.maxY() >= maxY()) {
		if (rect_63_.maxX() > maxX())
		    vector.addElement(newRect(x, y, width, rect_63_.y - y));
		else {
		    vector.addElement(newRect(x, y, width, rect_63_.y - y));
		    vector.addElement(newRect(rect_63_.maxX(), rect_63_.y,
					      maxX() - rect_63_.maxX(),
					      maxY() - rect_63_.y));
		}
		filterEmptyRects(vector);
	    } else if (rect_63_.x <= x) {
		if (rect_63_.maxX() >= maxX()) {
		    vector.addElement(newRect(x, y, width, rect_63_.y - y));
		    vector.addElement(newRect(x, rect_63_.maxY(), width,
					      maxY() - rect_63_.maxY()));
		} else {
		    vector.addElement(newRect(x, y, width, rect_63_.y - y));
		    vector.addElement(newRect(rect_63_.maxX(), rect_63_.y,
					      maxX() - rect_63_.maxX(),
					      rect_63_.height));
		    vector.addElement(newRect(x, rect_63_.maxY(), width,
					      maxY() - rect_63_.maxY()));
		}
		filterEmptyRects(vector);
	    } else if (rect_63_.x <= maxX() && rect_63_.maxX() > maxX()) {
		if (rect_63_.y <= y && rect_63_.maxY() > maxY())
		    vector.addElement(newRect(x, y, rect_63_.x - x, height));
		else if (rect_63_.y <= y) {
		    vector.addElement(newRect(x, y, rect_63_.x - x,
					      rect_63_.maxY() - y));
		    vector.addElement(newRect(x, rect_63_.maxY(), width,
					      maxY() - rect_63_.maxY()));
		} else if (rect_63_.maxY() > maxY()) {
		    vector.addElement(newRect(x, y, width, rect_63_.y - y));
		    vector.addElement(newRect(x, rect_63_.y, rect_63_.x - x,
					      maxY() - rect_63_.y));
		} else {
		    vector.addElement(newRect(x, y, width, rect_63_.y - y));
		    vector.addElement(newRect(x, rect_63_.y, rect_63_.x - x,
					      rect_63_.height));
		    vector.addElement(newRect(x, rect_63_.maxY(), width,
					      maxY() - rect_63_.maxY()));
		}
		filterEmptyRects(vector);
	    } else if (rect_63_.x >= x && rect_63_.maxX() <= maxX()) {
		if (rect_63_.y <= y && rect_63_.maxY() > maxY()) {
		    vector.addElement(newRect(x, y, rect_63_.x - x, height));
		    vector.addElement(newRect(rect_63_.maxX(), y,
					      maxX() - rect_63_.maxX(),
					      height));
		} else if (rect_63_.y <= y) {
		    vector.addElement(newRect(x, y, rect_63_.x - x, height));
		    vector.addElement(newRect(rect_63_.x, rect_63_.maxY(),
					      rect_63_.width,
					      maxY() - rect_63_.maxY()));
		    vector.addElement(newRect(rect_63_.maxX(), y,
					      maxX() - rect_63_.maxX(),
					      height));
		} else {
		    vector.addElement(newRect(x, y, rect_63_.x - x, height));
		    vector.addElement(newRect(rect_63_.x, y, rect_63_.width,
					      rect_63_.y - y));
		    vector.addElement(newRect(rect_63_.maxX(), y,
					      maxX() - rect_63_.maxX(),
					      height));
		}
		filterEmptyRects(vector);
	    }
	}
    }
    
    static Rect newRect(int i, int i_64_, int i_65_, int i_66_) {
	Rect rect;
	synchronized (_rectCache) {
	    if (!_cacheRects || _rectCache.isEmpty())
		return new Rect(i, i_64_, i_65_, i_66_);
	    rect = (Rect) _rectCache.removeLastElement();
	}
	rect.setBounds(i, i_64_, i_65_, i_66_);
	return rect;
    }
    
    static Rect newRect(Rect rect) {
	if (rect == null)
	    return newRect(0, 0, 0, 0);
	return newRect(rect.x, rect.y, rect.width, rect.height);
    }
    
    static Rect newRect() {
	return newRect(0, 0, 0, 0);
    }
    
    static void returnRect(Rect rect) {
	if (_cacheRects) {
	    synchronized (_rectCache) {
		if (_rectCache.count() < 50)
		    _rectCache.addElement(rect);
	    }
	}
    }
    
    static void returnRects(Vector vector) {
	if (vector != null && _cacheRects) {
	    int i = vector.count();
	    while (i-- > 0)
		returnRect((Rect) vector.elementAt(i));
	    vector.removeAllElements();
	}
    }
    
    static void setShouldCacheRects(boolean bool) {
	synchronized (_rectCache) {
	    _cacheRects = bool;
	    if (!_cacheRects)
		_rectCache.removeAllElements();
	}
    }
}
