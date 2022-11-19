/* FingerVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.playwrite.internationalization.ResourceIDs;

class FingerVariable extends Variable
    implements Viewable, Externalizable, ResourceIDs.VariableIDs
{
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108752463154L;
    static final int fingerBase = 0;
    static int fingerCount = 0;
    private transient Subroutine _subroutine;
    private transient boolean deleted = false;
    
    FingerVariable(Subroutine subroutine) {
	super(subroutine.getName() + "666" + fingerCount++,
	      subroutine.getOwner(), false);
	_subroutine = subroutine;
	this.setValue(subroutine.getOwner(), new Integer(0));
    }
    
    public FingerVariable() {
	/* empty */
    }
    
    public PlaywriteView createView() {
	PlaywriteView fingerView
	    = new PlaywriteView(Resource.getImage("finger image ID"));
	fingerView.setModelObject(this);
	return fingerView;
    }
    
    void modifyValue(VariableOwner owner, Object val) {
	if (!deleted) {
	    Debug.print(true, "error: modify called on FingerVariable");
	    Debug.stackTrace();
	    super.modifyValue(owner, val);
	}
    }
    
    public void delete() {
	deleted = true;
	super.delete();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
    }
    
    void setSubroutine(Subroutine subroutine) {
	_subroutine = subroutine;
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
    }
}
