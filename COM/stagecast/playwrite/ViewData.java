/* ViewData - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Vector;

class ViewData implements Externalizable
{
    public static final String DRAWER_CHARACTERS_ID = "A";
    public static final String DRAWER_SPECIAL_ID = "B";
    public static final String DRAWER_STAGES_ID = "C";
    public static final String DRAWER_GLOBALS_ID = "D";
    public static final String DRAWER_SOUNDS_ID = "E";
    public static final String DRAWER_JARS_ID = "F";
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108752790834L;
    private Vector _data = new Vector(10);
    private Vector _keys = new Vector(10);
    
    public ViewData() {
	/* empty */
    }
    
    public void putData(String key, Vector data) {
	int index = _keys.indexOf(key);
	if (index == -1) {
	    _keys.addElement(key);
	    _data.addElement(data);
	} else
	    _data.replaceElementAt(index, data);
    }
    
    public Vector getData(String key) {
	int index = _keys.indexOf(key);
	if (index == -1)
	    return null;
	return (Vector) _data.elementAt(index);
    }
    
    public void putRect(String key, Rect r) {
	Vector rectVect = new Vector(4);
	rectVect.addElement(new Integer(r.x));
	rectVect.addElement(new Integer(r.y));
	rectVect.addElement(new Integer(r.width));
	rectVect.addElement(new Integer(r.height));
	putData(key, rectVect);
    }
    
    public Rect getRect(String key) {
	Vector rectVect = getData(key);
	if (rectVect == null)
	    return null;
	Rect r = new Rect(((Integer) rectVect.elementAt(0)).intValue(),
			  ((Integer) rectVect.elementAt(1)).intValue(),
			  ((Integer) rectVect.elementAt(2)).intValue(),
			  ((Integer) rectVect.elementAt(3)).intValue());
	return r;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(_data.size());
	for (int i = 0; i < _data.size(); i++) {
	    ((WorldOutStream) out).writeVector((Vector) _data.elementAt(i));
	    out.writeUTF((String) _keys.elementAt(i));
	}
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(ViewData.class);
	int size = in.readInt();
	for (int i = 0; i < size; i++) {
	    _data.addElement(((WorldInStream) in).readVector());
	    _keys.addElement(in.readUTF());
	}
    }
}
