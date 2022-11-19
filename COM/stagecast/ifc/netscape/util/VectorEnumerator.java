/* VectorEnumerator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.util;

class VectorEnumerator implements Enumeration
{
    Vector vector;
    int index;
    
    VectorEnumerator(Vector vector) {
	this.vector = vector;
	index = 0;
    }
    
    VectorEnumerator(Vector vector, int i) {
	this.vector = vector;
	index = i;
    }
    
    public boolean hasMoreElements() {
	return index < vector.count();
    }
    
    public Object nextElement() {
	return vector.elementAt(index++);
    }
}
