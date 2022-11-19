/* DoorPrototype - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class DoorPrototype extends CharacterPrototype
    implements Door, Externalizable, ResourceIDs.DoorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108756460850L;
    static Appearance defaultDoorAppear;
    private DoorPrototype _otherEnd;
    private boolean _isDestination;
    
    static void staticInit() {
	defaultDoorAppear
	    = new Appearance(Resource.getText("door default appearance"),
			     Resource.getImage("door default appearance"),
			     new Shape(1, 1, new Point(1, 1)));
    }
    
    public static void initExtension() {
	PlaywriteRoot.registerCharacterPrototype(DoorPrototype.class);
    }
    
    public DoorPrototype() {
	/* empty */
    }
    
    public void init(World world) {
	String name = Resource.getText("door name");
	fillInObject(world, name,
		     new Appearance(Resource
					.getText("door default appearance"),
				    Resource
					.getImage("door default appearance"),
				    new Shape(1, 1, new Point(1, 1))),
		     false);
	DoorPrototype dest = new DoorPrototype();
	Appearance endAppearance
	    = new Appearance(Resource.getText("door end appearance"),
			     Resource.getImage("door end appearance"),
			     new Shape(1, 1, new Point(1, 1)));
	dest.fillInObject(world,
			  Resource.getTextAndFormat("door end name",
						    new Object[] { name }),
			  endAppearance, true);
	setOtherEnd(dest);
	dest.setOtherEnd(this);
    }
    
    private void fillInObject(World world, String name, Appearance appearance,
			      boolean isDest) {
	this.fillInObject(world, name, appearance);
	_isDestination = isDest;
	this.setVisibility(isDest ^ true);
    }
    
    public CharacterInstance makeInstance() {
	if (_isDestination)
	    return new DoorInstance(_otherEnd, false);
	return new DoorInstance(this, false);
    }
    
    final DoorPrototype getSourceEnd() {
	return _isDestination ? _otherEnd : this;
    }
    
    final DoorPrototype getDestinationEnd() {
	return _isDestination ? this : _otherEnd;
    }
    
    public final boolean isDestinationEnd() {
	return _isDestination;
    }
    
    public final Door getOtherEnd() {
	return _otherEnd;
    }
    
    public final void setOtherEnd(Door door) {
	_otherEnd = (DoorPrototype) door;
    }
    
    public void highlightForSelection() {
	if (!_isDestination)
	    super.highlightForSelection();
    }
    
    public void unhighlightForSelection() {
	if (!_isDestination)
	    super.unhighlightForSelection();
    }
    
    public void halo() {
	if (!_isDestination)
	    super.halo();
    }
    
    public void unhalo() {
	if (!_isDestination)
	    super.unhalo();
    }
    
    public void delete() {
	if (!_isDestination) {
	    deleteDoorPrototype();
	    _otherEnd.deleteDoorPrototype();
	}
    }
    
    private void deleteDoorPrototype() {
	if (this.getContainer() != null)
	    this.getContainer().remove(this);
	super.delete();
    }
    
    public void undelete() {
	/* empty */
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	DoorPrototype newDoor1 = copyDoor(map, fullCopy);
	DoorPrototype newDoor2 = _otherEnd.copyDoor(map, fullCopy);
	newDoor1.setOtherEnd(newDoor2);
	newDoor2.setOtherEnd(newDoor1);
	return newDoor1;
    }
    
    private DoorPrototype copyDoor(Hashtable map, boolean fullCopy) {
	DoorPrototype newDoor = (DoorPrototype) map.get(this);
	if (newDoor != null) {
	    if (newDoor.isProxy() && fullCopy)
		newDoor.makeReal(this, map);
	    return newDoor;
	}
	newDoor = (DoorPrototype) super.copy(map, fullCopy);
	if (_isDestination) {
	    newDoor._isDestination = true;
	    newDoor.setVisibility(false);
	} else
	    newDoor._isDestination = false;
	return newDoor;
    }
    
    public Object makeProxy(Hashtable map) {
	DoorPrototype newDoor = (DoorPrototype) super.makeProxy(map);
	newDoor._isDestination = _isDestination;
	if (!_isDestination && map.get(_otherEnd) == null) {
	    DoorPrototype newDest = (DoorPrototype) _otherEnd.makeProxy(map);
	    newDoor.setOtherEnd(newDest);
	    newDest.setOtherEnd(newDoor);
	}
	return newDoor;
    }
    
    public PlaywriteView createView() {
	return new DoorView(this);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_otherEnd);
	super.writeExternal(out);
	out.writeObject(_otherEnd);
	out.writeBoolean(_isDestination);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_otherEnd = (DoorPrototype) in.readObject();
	_isDestination = in.readBoolean();
	if (_isDestination)
	    this.setVisibility(false);
    }
}
