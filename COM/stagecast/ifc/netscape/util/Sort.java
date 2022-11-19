/* Sort - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.util;

public class Sort
{
    private Sort() {
	throw new Error
		  ("All methods on Sort are static, do not call new Sort().");
    }
    
    private static Object[] upperCaseStrings(Object[] objects) {
	int i = objects.length;
	Object[] objects_0_ = new Object[i];
	for (int i_1_ = 0; i_1_ < i; i_1_++) {
	    String string = (String) objects[i_1_];
	    if (string != null)
		objects_0_[i_1_] = ((String) objects[i_1_]).toUpperCase();
	}
	return objects_0_;
    }
    
    public static void sort(Object[] objects, Object[] objects_2_, int i,
			    int i_3_, boolean bool) {
	if (i_3_ > 1) {
	    if (objects[0] instanceof String)
		quickSortStrings(objects, objects_2_, i, i + i_3_ - 1, bool);
	    else
		quickSort(objects, objects_2_, i, i + i_3_ - 1, bool);
	}
    }
    
    public static void sortStrings(Object[] objects, int i, int i_4_,
				   boolean bool, boolean bool_5_) {
	if (bool_5_)
	    sort(upperCaseStrings(objects), objects, i, i_4_, bool);
	else
	    sort(objects, null, i, i_4_, bool);
    }
    
    private static void quickSortStrings(Object[] objects, Object[] objects_6_,
					 int i, int i_7_, boolean bool) {
	if (objects.length > 1) {
	    int i_8_ = i;
	    int i_9_ = i_7_;
	    String string = (String) objects[(i + i_7_) / 2];
	    do {
		if (bool) {
		    for (/**/; i_8_ < i_7_; i_8_++) {
			if (string.compareTo((String) objects[i_8_]) <= 0)
			    break;
		    }
		    for (/**/; i_9_ > i; i_9_--) {
			if (string.compareTo((String) objects[i_9_]) >= 0)
			    break;
		    }
		} else {
		    for (/**/; i_8_ < i_7_; i_8_++) {
			if (string.compareTo((String) objects[i_8_]) >= 0)
			    break;
		    }
		    for (/**/;
			 (i_9_ > i
			  && string.compareTo((String) objects[i_9_]) > 0);
			 i_9_--) {
			/* empty */
		    }
		}
		if (i_8_ < i_9_) {
		    Object object = objects[i_8_];
		    objects[i_8_] = objects[i_9_];
		    objects[i_9_] = object;
		    if (objects_6_ != null) {
			object = objects_6_[i_8_];
			objects_6_[i_8_] = objects_6_[i_9_];
			objects_6_[i_9_] = object;
		    }
		}
		if (i_8_ <= i_9_) {
		    i_8_++;
		    i_9_--;
		}
	    } while (i_8_ <= i_9_);
	    if (i < i_9_)
		quickSortStrings(objects, objects_6_, i, i_9_, bool);
	    if (i_8_ < i_7_)
		quickSortStrings(objects, objects_6_, i_8_, i_7_, bool);
	}
    }
    
    private static void quickSort(Object[] objects, Object[] objects_10_,
				  int i, int i_11_, boolean bool) {
	if (objects.length > 1) {
	    int i_12_ = i;
	    int i_13_ = i_11_;
	    Comparable comparable = (Comparable) objects[(i + i_11_) / 2];
	    do {
		if (bool) {
		    for (/**/; i_12_ < i_11_; i_12_++) {
			if (comparable.compareTo((Comparable) objects[i_12_])
			    <= 0)
			    break;
		    }
		    for (/**/; i_13_ > i; i_13_--) {
			if (comparable.compareTo((Comparable) objects[i_13_])
			    >= 0)
			    break;
		    }
		} else {
		    for (/**/; i_12_ < i_11_; i_12_++) {
			if (comparable.compareTo((Comparable) objects[i_12_])
			    >= 0)
			    break;
		    }
		    for (/**/;
			 (i_13_ > i
			  && (comparable.compareTo((Comparable) objects[i_13_])
			      > 0));
			 i_13_--) {
			/* empty */
		    }
		}
		if (i_12_ < i_13_) {
		    Object object = objects[i_12_];
		    objects[i_12_] = objects[i_13_];
		    objects[i_13_] = object;
		    if (objects_10_ != null) {
			object = objects_10_[i_12_];
			objects_10_[i_12_] = objects_10_[i_13_];
			objects_10_[i_13_] = object;
		    }
		}
		if (i_12_ <= i_13_) {
		    i_12_++;
		    i_13_--;
		}
	    } while (i_12_ <= i_13_);
	    if (i < i_13_)
		quickSort(objects, objects_10_, i, i_13_, bool);
	    if (i_12_ < i_11_)
		quickSort(objects, objects_10_, i_12_, i_11_, bool);
	}
    }
}
