/* Hashtable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.util;

public class Hashtable implements Cloneable
{
    static final int A = -1640531527;
    static final int EMPTY = 0;
    static final int REMOVED = 1;
    static final int DEFAULT = 2;
    static final String keysField = "keys";
    static final String elementsField = "elements";
    int count;
    int totalCount;
    int shift = 30;
    int capacity;
    int indexMask;
    int[] hashCodes;
    Object[] keys;
    Object[] elements;
    
    public Hashtable() {
	/* empty */
    }
    
    public Hashtable(int i) {
	this();
	if (i < 0)
	    throw new IllegalArgumentException("initialCapacity must be > 0");
	grow(i);
    }
    
    public Object clone() {
	Hashtable hashtable_0_;
	try {
	    hashtable_0_ = (Hashtable) super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new InternalError
		      ("Error in clone(). This shouldn't happen.");
	}
	if (count == 0) {
	    hashtable_0_.shift = 30;
	    hashtable_0_.totalCount = 0;
	    hashtable_0_.capacity = 0;
	    hashtable_0_.indexMask = 0;
	    hashtable_0_.hashCodes = null;
	    hashtable_0_.keys = null;
	    hashtable_0_.elements = null;
	    return hashtable_0_;
	}
	int i = hashCodes.length;
	hashtable_0_.hashCodes = new int[i];
	hashtable_0_.keys = new Object[i];
	hashtable_0_.elements = new Object[i];
	System.arraycopy(hashCodes, 0, hashtable_0_.hashCodes, 0, i);
	System.arraycopy(keys, 0, hashtable_0_.keys, 0, i);
	System.arraycopy(elements, 0, hashtable_0_.elements, 0, i);
	return hashtable_0_;
    }
    
    public int count() {
	return count;
    }
    
    public int size() {
	return count;
    }
    
    public boolean isEmpty() {
	return count == 0;
    }
    
    public Enumeration keys() {
	return new HashtableEnumerator(this, true);
    }
    
    public Enumeration elements() {
	return new HashtableEnumerator(this, false);
    }
    
    public Vector keysVector() {
	if (count == 0)
	    return new Vector();
	Vector vector = new Vector(count);
	int i = 0;
	for (int i_1_ = 0; i_1_ < keys.length && i < count; i_1_++) {
	    Object object = keys[i_1_];
	    if (object != null) {
		vector.addElement(object);
		i++;
	    }
	}
	return vector;
    }
    
    public Vector elementsVector() {
	if (count == 0)
	    return new Vector();
	Vector vector = new Vector(count);
	int i = 0;
	for (int i_2_ = 0; i_2_ < elements.length && i < count; i_2_++) {
	    Object object = elements[i_2_];
	    if (object != null) {
		vector.addElement(object);
		i++;
	    }
	}
	return vector;
    }
    
    public Object[] keysArray() {
	if (count == 0)
	    return null;
	Object[] objects = new Object[count];
	int i = 0;
	for (int i_3_ = 0; i_3_ < keys.length && i < count; i_3_++) {
	    Object object = keys[i_3_];
	    if (object != null)
		objects[i++] = object;
	}
	return objects;
    }
    
    public Object[] elementsArray() {
	if (count == 0)
	    return null;
	Object[] objects = new Object[count];
	int i = 0;
	for (int i_4_ = 0; i_4_ < elements.length && i < count; i_4_++) {
	    Object object = elements[i_4_];
	    if (object != null)
		objects[i++] = object;
	}
	return objects;
    }
    
    public boolean contains(Object object) {
	if (count == 0)
	    return false;
	if (object == null)
	    throw new NullPointerException();
	if (elements == null)
	    return false;
	for (int i = 0; i < elements.length; i++) {
	    Object object_5_ = elements[i];
	    if (object_5_ != null && object.equals(object_5_))
		return true;
	}
	return false;
    }
    
    public boolean containsKey(Object object) {
	return get(object) != null;
    }
    
    public Object get(Object object) {
	if (count == 0)
	    return null;
	return elements[tableIndexFor(object, hash(object))];
    }
    
    public Object remove(Object object) {
	if (count == 0)
	    return null;
	int i = tableIndexFor(object, hash(object));
	Object object_6_ = elements[i];
	if (object_6_ == null)
	    return null;
	count--;
	hashCodes[i] = 1;
	keys[i] = null;
	elements[i] = null;
	return object_6_;
    }
    
    public Object put(Object object, Object object_7_) {
	if (object_7_ == null)
	    throw new NullPointerException();
	if (hashCodes == null)
	    grow();
	int i = hash(object);
	int i_8_ = tableIndexFor(object, i);
	Object object_9_ = elements[i_8_];
	if (object_9_ == null) {
	    if (hashCodes[i_8_] == 0) {
		if (totalCount >= capacity) {
		    grow();
		    return put(object, object_7_);
		}
		totalCount++;
	    }
	    count++;
	}
	hashCodes[i_8_] = i;
	keys[i_8_] = object;
	elements[i_8_] = object_7_;
	return object_9_;
    }
    
    private int hash(Object object) {
	int i = object.hashCode();
	if (i == 0 || i == 1)
	    i = 2;
	return i;
    }
    
    private int tableIndexFor(Object object, int i) {
	int i_10_ = i * -1640531527;
	int i_11_ = i_10_ >>> shift;
	int i_12_ = hashCodes[i_11_];
	int i_13_;
	if (i_12_ == i) {
	    if (object.equals(keys[i_11_]))
		return i_11_;
	    i_13_ = -1;
	} else {
	    if (i_12_ == 0)
		return i_11_;
	    if (i_12_ == 1)
		i_13_ = i_11_;
	    else
		i_13_ = -1;
	}
	int i_14_ = i_10_ >>> 2 * shift - 32 & indexMask | 0x1;
	int i_15_ = 1;
	do {
	    i_15_++;
	    i_11_ = i_11_ + i_14_ & indexMask;
	    i_12_ = hashCodes[i_11_];
	    if (i_12_ == i) {
		if (object.equals(keys[i_11_]))
		    return i_11_;
	    } else {
		if (i_12_ == 0) {
		    if (i_13_ < 0)
			return i_11_;
		    return i_13_;
		}
		if (i_12_ == 1 && i_13_ == -1)
		    i_13_ = i_11_;
	    }
	} while (i_15_ <= totalCount);
	throw new InconsistencyException("Hashtable overflow");
    }
    
    private void grow(int i) {
	int i_16_ = i * 4 / 3;
	int i_17_;
	for (i_17_ = 3; 1 << i_17_ < i_16_; i_17_++) {
	    /* empty */
	}
	shift = 32 - i_17_ + 1;
	grow();
    }
    
    private void grow() {
	shift--;
	int i = 32 - shift;
	indexMask = (1 << i) - 1;
	capacity = 3 * (1 << i) / 4;
	int[] is = hashCodes;
	Object[] objects = keys;
	Object[] objects_18_ = elements;
	hashCodes = new int[1 << i];
	keys = new Object[1 << i];
	elements = new Object[1 << i];
	totalCount = 0;
	if (count > 0) {
	    count = 0;
	    for (int i_19_ = 0; i_19_ < is.length; i_19_++) {
		Object object = objects[i_19_];
		if (object != null) {
		    int i_20_ = is[i_19_];
		    int i_21_ = tableIndexFor(object, i_20_);
		    hashCodes[i_21_] = i_20_;
		    keys[i_21_] = object;
		    elements[i_21_] = objects_18_[i_19_];
		    count++;
		    totalCount++;
		}
	    }
	}
    }
    
    public void clear() {
	if (hashCodes != null) {
	    for (int i = 0; i < hashCodes.length; i++) {
		hashCodes[i] = 0;
		keys[i] = null;
		elements[i] = null;
	    }
	    count = 0;
	    totalCount = 0;
	}
    }
    
    public String toString() {
	return "Hashtable(" + size() + ")";
    }
}
