/* Range - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class Range
{
    private static Range nullRange;
    public int index;
    public int length;
    
    public static Range nullRange() {
	if (nullRange == null)
	    nullRange = new Range(-1, 0);
	return nullRange;
    }
    
    public static Range rangeFromIntersection(Range range, Range range_0_) {
	Range range_1_ = new Range(range);
	range_1_.intersectWith(range_0_);
	return range_1_;
    }
    
    public static Range rangeFromUnion(Range range, Range range_2_) {
	Range range_3_ = new Range(range);
	range_3_.unionWith(range_2_);
	return range_3_;
    }
    
    public static Range rangeFromIndices(int i, int i_4_) {
	if (i < i_4_)
	    return new Range(i, i_4_ - i);
	return new Range(i_4_, i - i_4_);
    }
    
    public Range() {
	index = nullRange().index;
	length = nullRange().length;
    }
    
    public Range(int i, int i_5_) {
	index = i;
	length = i_5_;
    }
    
    public Range(Range range_6_) {
	index = range_6_.index;
	length = range_6_.length;
    }
    
    public int index() {
	return index;
    }
    
    public int length() {
	return length;
    }
    
    public int lastIndex() {
	return index + length - 1;
    }
    
    public boolean equals(Object object) {
	if (!(object instanceof Range))
	    return false;
	Range range_7_ = (Range) object;
	if (range_7_.index == index && range_7_.length == length)
	    return true;
	return false;
    }
    
    public void unionWith(Range range_8_) {
	unionWith(range_8_.index, range_8_.length);
    }
    
    public void unionWith(int i, int i_9_) {
	if (index == nullRange().index) {
	    index = i;
	    length = i_9_;
	} else if (i != nullRange().index) {
	    int i_10_;
	    if (index < i)
		i_10_ = index;
	    else
		i_10_ = i;
	    int i_11_;
	    if (index + length > i + i_9_)
		i_11_ = index + length;
	    else
		i_11_ = i + i_9_;
	    index = i_10_;
	    length = i_11_ - i_10_;
	}
    }
    
    public void intersectWith(Range range_12_) {
	intersectWith(range_12_.index, range_12_.length);
    }
    
    public void intersectWith(int i, int i_13_) {
	int i_14_;
	int i_15_;
	int i_16_;
	int i_17_;
	if (index < i) {
	    i_14_ = index;
	    i_15_ = length;
	    i_16_ = i;
	    i_17_ = i_13_;
	} else {
	    i_14_ = i;
	    i_15_ = i_13_;
	    i_16_ = index;
	    i_17_ = length;
	}
	if (i_14_ + i_15_ <= i_16_) {
	    index = nullRange().index;
	    length = nullRange().length;
	} else {
	    index = i_16_;
	    if (i_16_ + i_17_ > i_14_ + i_15_)
		length = i_14_ + i_15_ - i_16_;
	    else
		length = i_17_;
	}
    }
    
    public String toString() {
	if (isNullRange())
	    return "Null range";
	return "(" + index + ", " + length + ")";
    }
    
    public boolean intersects(Range range_18_) {
	int i = index;
	int i_19_ = length;
	intersectWith(range_18_);
	boolean bool;
	if (index == nullRange().index)
	    bool = false;
	else
	    bool = true;
	index = i;
	length = i_19_;
	return bool;
    }
    
    public boolean intersects(int i, int i_20_) {
	int i_21_ = index;
	int i_22_ = length;
	intersectWith(i, i_20_);
	boolean bool;
	if (index == nullRange().index)
	    bool = false;
	else
	    bool = true;
	index = i_21_;
	length = i_22_;
	return bool;
    }
    
    public boolean isNullRange() {
	if (index == nullRange().index)
	    return true;
	return false;
    }
    
    public boolean isEmpty() {
	if (length == 0)
	    return true;
	return false;
    }
    
    public boolean contains(int i) {
	if (i >= index && i < index + length)
	    return true;
	return false;
    }
}
