/* DoorInstance - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;

public class DoorInstance extends CharacterInstance
    implements Door, Externalizable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108756329778L;
    private DoorInstance _otherEnd;
    private boolean _isDestination;
    private transient CharacterContainer _oldContainer;
    private transient int _oldX;
    private transient int _oldY;
    private transient int _oldZ;
    private transient boolean _oldVisibility;
    private transient boolean _mustAddOtherDoor = false;
    
    DoorInstance(DoorPrototype prototype, boolean isDest) {
	super(prototype);
	_isDestination = isDest;
	if (_isDestination)
	    this.setVisibility(false);
	else {
	    DoorInstance dest
		= new DoorInstance((DoorPrototype) prototype.getOtherEnd(),
				   true);
	    setOtherEnd(dest);
	    dest.setOtherEnd(this);
	}
	_mustAddOtherDoor = true;
    }
    
    public DoorInstance() {
	/* empty */
    }
    
    final DoorInstance getSourceEnd() {
	return _isDestination ? _otherEnd : this;
    }
    
    final DoorInstance getDestinationEnd() {
	return _isDestination ? this : _otherEnd;
    }
    
    public final boolean isDestinationEnd() {
	return _isDestination;
    }
    
    public final Door getOtherEnd() {
	return _otherEnd;
    }
    
    public final void setOtherEnd(Door door) {
	_otherEnd = (DoorInstance) door;
    }
    
    public void highlightForSelection() {
	hiliteDoor();
	_otherEnd.hiliteDoor();
    }
    
    public void unhighlightForSelection() {
	unhiliteDoor();
	_otherEnd.unhiliteDoor();
    }
    
    private void hiliteDoor() {
	if (_isDestination)
	    this.setVisibility(true);
	super.highlightForSelection();
    }
    
    private void unhiliteDoor() {
	super.unhighlightForSelection();
	if (_isDestination)
	    this.setVisibility(false);
    }
    
    public void halo() {
	haloDoor();
	_otherEnd.haloDoor();
    }
    
    private void haloDoor() {
	if (_isDestination)
	    this.setVisibility(true);
	super.halo();
    }
    
    public void unhalo() {
	unhaloDoor();
	_otherEnd.unhaloDoor();
    }
    
    private void unhaloDoor() {
	super.unhalo();
	if (_isDestination)
	    this.setVisibility(false);
    }
    
    void setLocation(int h, int v) {
	super.setLocation(h, v);
	if (_mustAddOtherDoor && _otherEnd != null) {
	    _mustAddOtherDoor = false;
	    _otherEnd._mustAddOtherDoor = false;
	    CharacterContainer container = this.getCharContainer();
	    int dh;
	    if (container instanceof Board)
		dh = 2;
	    else
		dh = this.getCurrentAppearance().getPhysicalWidth() + 10;
	    if (_isDestination)
		container.add(_otherEnd, h - dh, v, -1);
	    else
		container.add(_otherEnd, h + dh, v, -1);
	}
    }
    
    public void delete() {
	deleteDoorInstance();
	_otherEnd.deleteDoorInstance();
    }
    
    private void deleteDoorInstance() {
	_oldContainer = this.getCharContainer();
	_oldVisibility = this.isVisible();
	if (_oldContainer != null) {
	    _oldX = this.getH();
	    _oldY = this.getV();
	    _oldZ = this.getZ();
	    _oldContainer.remove(this);
	}
	super.delete();
    }
    
    public void undelete() {
	undeleteDoorInstance();
	_otherEnd.undeleteDoorInstance();
    }
    
    private void undeleteDoorInstance() {
	super.undelete();
	if (isDestinationEnd())
	    this.setVisibility(false);
	if (this.getContainer() == null && _oldContainer != null)
	    _oldContainer.add(this, _oldX, _oldY, _oldZ);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	DoorInstance newDoor1 = copyDoor(map, fullCopy);
	DoorInstance newDoor2 = _otherEnd.copyDoor(map, fullCopy);
	newDoor1.setOtherEnd(newDoor2);
	newDoor2.setOtherEnd(newDoor1);
	return newDoor1;
    }
    
    private DoorInstance copyDoor(Hashtable map, boolean fullCopy) {
	DoorInstance newDoor = null;
	if (map != null)
	    newDoor = (DoorInstance) map.get(this);
	if (newDoor != null)
	    return newDoor;
	newDoor = (DoorInstance) this.copy(map, fullCopy, "door character");
	if (_isDestination) {
	    newDoor._isDestination = true;
	    newDoor.setVisibility(false);
	} else
	    newDoor._isDestination = false;
	newDoor._mustAddOtherDoor = true;
	return newDoor;
    }
    
    public PlaywriteView createView() {
	return new DoorView(this);
    }
    
    public PlaywriteView createView(int fixedSize) {
	return new DoorView(this, fixedSize);
    }
    
    public void edit() {
	if (!isDestinationEnd())
	    super.edit();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_otherEnd);
	ASSERT.isTrue(_isDestination != _otherEnd._isDestination);
	super.writeExternal(out);
	out.writeObject(_otherEnd);
	out.writeBoolean(_isDestination);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_otherEnd = (DoorInstance) in.readObject();
	_isDestination = in.readBoolean();
	if (_isDestination)
	    this.setVisibility(false);
    }
}
