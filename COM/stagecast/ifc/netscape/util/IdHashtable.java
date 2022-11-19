/* IdHashtable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.util;

public class IdHashtable
{
    static final int A = -1640531527;
    static final int NOT_FOUND = 0;
    int power;
    int count;
    int maxCount;
    int indexMask;
    Object[] keys;
    int[] values;
    boolean equals;
    
    IdHashtable(boolean bool) {
	equals = bool;
	power = 5;
	count = 0;
	indexMask = (1 << power) - 1;
	maxCount = 3 * (1 << power) / 4;
	keys = new Object[1 << power];
	values = new int[1 << power];
    }
    
    private boolean equalKeys(Object object, Object object_0_) {
	if (object == object_0_)
	    return true;
	if (equals)
	    return object.equals(object_0_);
	return false;
    }
    
    private void rehash() {
	int i = keys.length;
	Object[] objects = keys;
	int[] is = values;
	power++;
	count = 0;
	indexMask = (1 << power) - 1;
	maxCount = 3 * (1 << power) / 4;
	keys = new Object[1 << power];
	values = new int[1 << power];
	for (int i_1_ = 0; i_1_ < i; i_1_++) {
	    if (objects[i_1_] != null)
		putKnownAbsent(objects[i_1_], is[i_1_]);
	}
    }
    
    int get(Object object) {
	int i = object.hashCode() * -1640531527;
	int i_2_ = i >>> 32 - power;
	Object object_3_ = keys[i_2_];
	if (object_3_ == null)
	    return 0;
	if (equalKeys(object_3_, object))
	    return values[i_2_];
	int i_4_ = i >>> 32 - 2 * power & indexMask | 0x1;
	int i_5_ = 1;
	do {
	    i_5_++;
	    i_2_ = i_2_ + i_4_ & indexMask;
	    object_3_ = keys[i_2_];
	    if (object_3_ == null)
		return 0;
	    if (equalKeys(object_3_, object))
		return values[i_2_];
	} while (i_5_ <= count);
	throw new InconsistencyException("IdHashtable overflow");
    }
    
    void putKnownAbsent(Object object, int i) {
	if (count >= maxCount)
	    rehash();
	int i_6_ = object.hashCode() * -1640531527;
	int i_7_ = i_6_ >>> 32 - power;
	if (keys[i_7_] == null) {
	    keys[i_7_] = object;
	    values[i_7_] = i;
	    count++;
	} else {
	    int i_8_ = i_6_ >>> 32 - 2 * power & indexMask | 0x1;
	    int i_9_ = 1;
	    do {
		i_9_++;
		i_7_ = i_7_ + i_8_ & indexMask;
		if (keys[i_7_] == null) {
		    keys[i_7_] = object;
		    values[i_7_] = i;
		    count++;
		    return;
		}
	    } while (i_9_ <= count);
	    throw new InconsistencyException("IdHashtable overflow");
	}
    }
}
