/* HashtableEnumerator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.util;

class HashtableEnumerator implements Enumeration
{
    boolean keyEnum;
    int index;
    int returnedCount;
    Hashtable table;
    
    HashtableEnumerator(Hashtable hashtable, boolean bool) {
	table = hashtable;
	keyEnum = bool;
	returnedCount = 0;
	if (hashtable.keys != null)
	    index = hashtable.keys.length;
	else
	    index = 0;
    }
    
    public boolean hasMoreElements() {
	if (returnedCount < table.count)
	    return true;
	return false;
    }
    
    public Object nextElement() {
	for (index--; index >= 0 && table.elements[index] == null; index--) {
	    /* empty */
	}
	if (index < 0 || returnedCount >= table.count)
	    throw new NoSuchElementException();
	returnedCount++;
	if (keyEnum)
	    return table.keys[index];
	return table.elements[index];
    }
}
