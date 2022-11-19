/* ASSERT - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Vector;

public class ASSERT
{
    private static final boolean MASTER_ASSERTION_SWITCH = true;
    
    static class AssertionException extends RuntimeException
    {
	AssertionException(String msg) {
	    super(msg);
	}
    }
    
    public static final void assert(boolean condition, Object o1, Object o2,
				    Object o3, Object o4, Object o5,
				    Object o6) {
	if (!condition) {
	    StringBuffer errorMsg = new StringBuffer(80);
	    errorMsg.append(o1);
	    errorMsg.append(o2);
	    errorMsg.append(o3);
	    errorMsg.append(o4);
	    errorMsg.append(o5);
	    errorMsg.append(o6);
	    throw new AssertionException(errorMsg.toString());
	}
    }
    
    public static final void stackTrace() {
	Thread.dumpStack();
    }
    
    public static final void isTrue(boolean condition) {
	assert(condition, "", "", "", "", "", "");
    }
    
    public static final void isTrue(boolean condition, Object o1) {
	assert(condition, o1, "", "", "", "", "");
    }
    
    public static final void isTrue(boolean condition, Object o1, Object o2) {
	assert(condition, o1, o2, "", "", "", "");
    }
    
    public static final void isTrue(boolean condition, Object o1, Object o2,
				    Object o3) {
	assert(condition, o1, o2, o3, "", "", "");
    }
    
    public static final void isTrue(boolean condition, Object o1, Object o2,
				    Object o3, Object o4) {
	assert(condition, o1, o2, o3, o4, "", "");
    }
    
    public static final void isTrue(boolean condition, Object o1, Object o2,
				    Object o3, Object o4, Object o5) {
	assert(condition, o1, o2, o3, o4, o5, "");
    }
    
    public static final void isNull(Object item) {
	isTrue(item == null, "Item should be null: ", "", "", "");
    }
    
    public static final void isNull(Object item, Object o1) {
	isTrue(item == null, "Item should be null: ", o1, "", "");
    }
    
    public static final void isNull(Object item, Object o1, Object o2) {
	isTrue(item == null, "Item should be null: ", o1, o2, "");
    }
    
    public static final void isNotNull(Object item) {
	isTrue(item != null, "Item should not be null", "", "", "", "");
    }
    
    public static final void isNotNull(Object item, Object o1) {
	isTrue(item != null, "Item should not be null: ", o1, "", "", "");
    }
    
    public static final void isNotNull(Object item, Object o1, Object o2) {
	isTrue(item != null, "Item should not be null: ", o1, o2, "", "");
    }
    
    public static final void isIdentical(Object item1, Object item2) {
	isTrue(item1 == item2, "Items should be EQ", "", "", "", "");
    }
    
    public static final void isIdentical(Object item1, Object item2,
					 Object o1) {
	isTrue(item1 == item2, "Item should be EQ: ", o1, "", "", "");
    }
    
    public static final void isIdentical(Object item1, Object item2, Object o1,
					 Object o2) {
	isTrue(item1 == item2, "Item should be EQ: ", o1, o2, "", "");
    }
    
    public static final void isInVector(Object item, Vector vector) {
	isTrue(vector.contains(item), "Item should be in vector", "", "", "",
	       "");
    }
    
    public static final void isInVector(Object item, Vector vector,
					Object o1) {
	isTrue(vector.contains(item), "Item should be in vector: ", o1, "", "",
	       "");
    }
    
    public static final void isInVector(Object item, Vector vector, Object o1,
					Object o2) {
	isTrue(vector.contains(item), "Item should be in vector: ", o1, o2, "",
	       "");
    }
    
    public static final void isNotInVector(Object item, Vector vector) {
	isTrue(vector.contains(item) ^ true, "Item should not be in vector",
	       "", "", "", "");
    }
    
    public static final void isNotInVector(Object item, Vector vector,
					   Object o1) {
	isTrue(vector.contains(item) ^ true, "Item should not be in vector: ",
	       o1, "", "", "");
    }
    
    public static final void isNotInVector(Object item, Vector vector,
					   Object o1, Object o2) {
	isTrue(vector.contains(item) ^ true, "Item should not be in vector: ",
	       o1, o2, "", "");
    }
    
    public static final void isClass(Object item, Class cl) {
	isTrue(item.getClass() == cl, "Class ", cl, " was expected, got ",
	       item.getClass(), "");
    }
    
    public static final void isClass(Object item, Class cl, Object o1) {
	isTrue(item.getClass() == cl, "Class ", cl, " was expected, got ",
	       item.getClass(), o1);
    }
    
    public static final void isInstanceOf(Object item, Class cl) {
	isTrue(cl.isInstance(item), "Item is not an instance of class ", cl,
	       "", "", "");
    }
    
    public static final void isInstanceOf(Object item, Class cl, Object o1) {
	isTrue(cl.isInstance(item), "Item is not an instance of class ", cl,
	       o1, "", "");
    }
    
    public static final void isInEventThread() {
	PlaywriteRoot.app();
	isTrue(PlaywriteRoot.isServer()
	       || PlaywriteRoot.app().inEventThread());
    }
}
