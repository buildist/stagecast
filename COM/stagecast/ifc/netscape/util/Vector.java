/* Vector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.util;

public class Vector implements Cloneable
{
    Object[] array;
    int count;
    
    public Vector() {
	count = 0;
    }
    
    public Vector(Object[] objects) {
	array = (Object[]) objects.clone();
	count = objects.length;
    }
    
    public Vector(int i) {
	array = new Object[i];
	count = 0;
    }
    
    public Object clone() {
	Vector vector_0_;
	try {
	    vector_0_ = (Vector) super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new InternalError
		      ("Error in clone(). This shouldn't happen.");
	}
	if (count == 0) {
	    vector_0_.array = null;
	    return vector_0_;
	}
	vector_0_.array = new Object[count];
	System.arraycopy(array, 0, vector_0_.array, 0, count);
	return vector_0_;
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
    
    public void addElementIfAbsent(Object object) {
	if (object == null)
	    throw new NullPointerException
		      ("It is illegal to store nulls in Vectors.");
	if (object != null && !contains(object))
	    addElement(object);
    }
    
    public boolean insertElementBefore(Object object, Object object_1_) {
	if (object == null)
	    throw new NullPointerException
		      ("It is illegal to store nulls in Vectors.");
	if (object_1_ == null)
	    return false;
	int i = indexOf(object_1_);
	if (i == -1)
	    return false;
	insertElementAt(object, i);
	return true;
    }
    
    public boolean insertElementAfter(Object object, Object object_2_) {
	if (object == null)
	    throw new NullPointerException
		      ("It is illegal to store nulls in Vectors.");
	if (object_2_ == null)
	    return false;
	int i = indexOf(object_2_);
	if (i == -1)
	    return false;
	if (i >= count - 1)
	    addElement(object);
	else
	    insertElementAt(object, i + 1);
	return true;
    }
    
    public void addElementsIfAbsent(Vector vector_3_) {
	if (vector_3_ != null) {
	    int i = vector_3_.count();
	    for (int i_4_ = 0; i_4_ < i; i_4_++) {
		Object object = vector_3_.elementAt(i_4_);
		if (!contains(object))
		    addElement(object);
	    }
	}
    }
    
    public void addElements(Vector vector_5_) {
	if (vector_5_ != null) {
	    int i = vector_5_.count();
	    if (array == null || count + i >= array.length)
		ensureCapacity(count + i);
	    for (int i_6_ = 0; i_6_ < i; i_6_++)
		addElement(vector_5_.elementAt(i_6_));
	}
    }
    
    public void removeAll(Object object) {
	int i = count();
	while (i-- > 0) {
	    if (elementAt(i).equals(object))
		removeElementAt(i);
	}
    }
    
    public Object removeFirstElement() {
	if (count == 0)
	    return null;
	return removeElementAt(0);
    }
    
    public Object removeLastElement() {
	if (count == 0)
	    return null;
	return removeElementAt(count - 1);
    }
    
    public Object replaceElementAt(int i, Object object) {
	if (object == null)
	    throw new NullPointerException
		      ("It is illegal to store nulls in Vectors.");
	if (i >= count)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(i) + " >= "
						     + count);
	if (i < 0)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(i)
						     + " < 0");
	Object object_7_ = elementAt(i);
	array[i] = object;
	return object_7_;
    }
    
    public Object[] elementArray() {
	Object[] objects = new Object[count];
	if (count > 0)
	    System.arraycopy(array, 0, objects, 0, count);
	return objects;
    }
    
    public void copyInto(Object[] objects) {
	if (count > 0)
	    System.arraycopy(array, 0, objects, 0, count);
    }
    
    public void trimToSize() {
	if (count == 0)
	    array = null;
	else if (count != array.length)
	    array = elementArray();
    }
    
    public void ensureCapacity(int i) {
	if (array == null)
	    array = new Object[8];
	if (i >= array.length) {
	    int i_8_;
	    if (array.length < 8)
		i_8_ = 8;
	    else
		i_8_ = array.length;
	    for (/**/; i_8_ < i; i_8_ = 2 * i_8_) {
		/* empty */
	    }
	    Object[] objects = new Object[i_8_];
	    System.arraycopy(array, 0, objects, 0, count);
	    array = objects;
	}
    }
    
    public int capacity() {
	if (array == null)
	    return 0;
	return array.length;
    }
    
    public Enumeration elements() {
	return new VectorEnumerator(this);
    }
    
    public Enumeration elements(int i) {
	return new VectorEnumerator(this, i);
    }
    
    public boolean contains(Object object) {
	if (indexOf(object, 0) != -1)
	    return true;
	return false;
    }
    
    public boolean containsIdentical(Object object) {
	if (indexOfIdentical(object, 0) != -1)
	    return true;
	return false;
    }
    
    public int indexOf(Object object) {
	return indexOf(object, 0);
    }
    
    public int indexOf(Object object, int i) {
	for (int i_9_ = i; i_9_ < count; i_9_++) {
	    if (array[i_9_].equals(object))
		return i_9_;
	}
	return -1;
    }
    
    public int indexOfIdentical(Object object, int i) {
	for (int i_10_ = i; i_10_ < count; i_10_++) {
	    if (array[i_10_] == object)
		return i_10_;
	}
	return -1;
    }
    
    public int indexOfIdentical(Object object) {
	return indexOfIdentical(object, 0);
    }
    
    public int lastIndexOf(Object object) {
	return lastIndexOf(object, count);
    }
    
    public int lastIndexOf(Object object, int i) {
	if (i > count)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(i) + " > "
						     + count);
	for (int i_11_ = i - 1; i_11_ >= 0; i_11_--) {
	    if (array[i_11_].equals(object))
		return i_11_;
	}
	return -1;
    }
    
    public Object elementAt(int i) {
	if (i >= count)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(i) + " >= "
						     + count);
	return array[i];
    }
    
    public Object firstElement() {
	if (count == 0)
	    return null;
	return array[0];
    }
    
    public Object lastElement() {
	if (count == 0)
	    return null;
	return array[count - 1];
    }
    
    public void setElementAt(Object object, int i) {
	if (i >= count)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(i) + " >= "
						     + count);
	if (object == null)
	    throw new NullPointerException
		      ("It is illegal to store nulls in Vectors.");
	array[i] = object;
    }
    
    public Object removeElementAt(int i) {
	if (i >= count)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(i) + " >= "
						     + count);
	Object object = array[i];
	int i_12_ = count - i - 1;
	if (i_12_ > 0)
	    System.arraycopy(array, i + 1, array, i, i_12_);
	count--;
	array[count] = null;
	return object;
    }
    
    public void insertElementAt(Object object, int i) {
	if (i >= count + 1)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(i) + " >= "
						     + count);
	if (object == null)
	    throw new NullPointerException
		      ("It is illegal to store nulls in Vectors.");
	if (array == null || count >= array.length)
	    ensureCapacity(count + 1);
	System.arraycopy(array, i, array, i + 1, count - i);
	array[i] = object;
	count++;
    }
    
    public void addElement(Object object) {
	if (object == null)
	    throw new NullPointerException
		      ("It is illegal to store nulls in Vectors.");
	if (array == null || count >= array.length)
	    ensureCapacity(count + 1);
	array[count] = object;
	count++;
    }
    
    public boolean removeElement(Object object) {
	int i = indexOf(object);
	if (i < 0)
	    return false;
	removeElementAt(i);
	return true;
    }
    
    public boolean removeElementIdentical(Object object) {
	int i = indexOfIdentical(object, 0);
	if (i < 0)
	    return false;
	removeElementAt(i);
	return true;
    }
    
    public void removeAllElements() {
	for (int i = 0; i < count; i++)
	    array[i] = null;
	count = 0;
    }
    
    public void sort(boolean bool) {
	Sort.sort(array, null, 0, count, bool);
    }
    
    public void sortStrings(boolean bool, boolean bool_13_) {
	Sort.sortStrings(array, 0, count, bool, bool_13_);
    }
    
    public String toString() {
	return "Vector(" + size() + ")";
    }
}
