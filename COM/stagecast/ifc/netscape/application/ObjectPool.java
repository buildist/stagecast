/* ObjectPool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class ObjectPool
{
    Object[] freePool = new Object[1];
    int freePoolMaxLength = 1;
    int freePoolNextSlot = 0;
    Class objectClass;
    int allocSaved;
    int allocDone;
    int maxCapacity;
    
    public ObjectPool(String string) {
	this(string, 32);
    }
    
    public ObjectPool(String string, int i) {
	try {
	    objectClass = Class.forName(string);
	} catch (ClassNotFoundException classnotfoundexception) {
	    System.out.println("ObjectPool cannot find class " + string);
	}
	allocSaved = 0;
	allocDone = 0;
	maxCapacity = i;
    }
    
    public Object allocateObject() {
	Object object = null;
	synchronized (this) {
	    if (freePoolNextSlot > 0) {
		freePoolNextSlot--;
		object = freePool[freePoolNextSlot];
	    }
	}
	if (object == null) {
	    allocDone++;
	    try {
		object = objectClass.newInstance();
	    } catch (InstantiationException instantiationexception) {
		System.out.println("Cannot instantiate instance of class "
				   + objectClass);
	    } catch (IllegalAccessException illegalaccessexception) {
		System.out.println
		    ("Cannot instantiate instance of class. Illegal."
		     + objectClass);
	    }
	} else
	    allocSaved++;
	return object;
    }
    
    public void recycleObject(Object object) {
	synchronized (this) {
	    if (freePoolMaxLength < maxCapacity) {
		if (freePoolNextSlot == freePoolMaxLength) {
		    Object[] objects = new Object[freePoolMaxLength * 2];
		    System.arraycopy(freePool, 0, objects, 0,
				     freePoolMaxLength);
		    freePool = objects;
		    freePoolMaxLength *= 2;
		}
		freePool[freePoolNextSlot++] = object;
	    }
	}
    }
    
    protected void finalize() {
	for (int i = 0; i < freePoolNextSlot; i++)
	    freePool[i] = null;
	freePool = null;
    }
    
    public String toString() {
	return ("Object pool for class " + objectClass + " has "
		+ freePoolNextSlot + " instances." + " " + allocSaved
		+ " allocations avoided allocation performed:" + allocDone);
    }
}
