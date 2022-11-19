/* IntHashtableEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package Acme;

class IntHashtableEntry
{
    int hash;
    int key;
    Object value;
    IntHashtableEntry next;
    
    protected Object clone() {
	IntHashtableEntry entry = new IntHashtableEntry();
	entry.hash = hash;
	entry.key = key;
	entry.value = value;
	entry.next = next != null ? (IntHashtableEntry) next.clone() : null;
	return entry;
    }
}
