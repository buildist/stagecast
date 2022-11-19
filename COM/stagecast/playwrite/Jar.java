/* Jar - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class Jar extends XYContainer
    implements Bindable, Debug.Constants, Externalizable, ModelViewInterface,
	       Named, ReferencedObject, ResourceIDs.InstanceNameIDs,
	       ResourceIDs.NameGeneratorIDs, Selectable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108756198706L;
    private String _name = "";
    private Vector _subjars;
    private UniqueID _uniqueID;
    private UniqueID _uniqueParentID;
    private transient Vector _containingJars;
    private transient GenericContainer _container;
    private transient boolean _selected;
    private transient ViewManager _viewManager;
    
    Jar(World world, String name) {
	super(world, Bindable.class, 2, false);
	_subjars = new Vector(1);
	_containingJars = null;
	_container = null;
	_selected = false;
	_viewManager = new ViewManager(this);
	_uniqueID = new UniqueID();
	_uniqueParentID = null;
	_name = name;
	world.add(this);
    }
    
    public Jar() {
	_subjars = new Vector(1);
	_containingJars = null;
	_container = null;
	_selected = false;
	_viewManager = new ViewManager(this);
    }
    
    public static Jar createJarWithDefaultName(World world) {
	Integer jarNumber = new Integer(world.getJars().size() + 1);
	String jarName = Resource.getTextAndFormat("Generator jar Name",
						   new Object[] { jarNumber });
	return new Jar(world, jarName);
    }
    
    public final String getName() {
	return _name;
    }
    
    public final void setName(String s) {
	_name = s;
    }
    
    public GenericContainer getContainer() {
	return _container;
    }
    
    public void setContainer(GenericContainer c) {
	_container = c;
    }
    
    public PlaywriteView createView() {
	JarView jarView = new JarView(this);
	_viewManager.addView(jarView);
	return jarView;
    }
    
    public boolean allowAdd(Contained obj) {
	if (!super.allowAdd(obj))
	    return false;
	if (obj instanceof Jar)
	    return ((Jar) obj).deepContains(this) ^ true;
	return true;
    }
    
    public void add(Contained obj) {
	add(obj, -1, -1);
    }
    
    public void add(Contained obj, int x, int y) {
	if (obj instanceof Jar) {
	    if (((Jar) obj).deepContains(this))
		throw new PlaywriteInternalError("adding " + obj + " to "
						 + this
						 + " would create a loop");
	    _subjars.addElement(obj);
	} else if (this.contains(obj))
	    return;
	super.add(obj, x, y);
	((Bindable) obj).wasAddedToJar(this);
    }
    
    public void remove(Contained obj) {
	if (obj instanceof Jar)
	    _subjars.removeElementIdentical(obj);
	super.remove(obj);
	((Bindable) obj).wasRemovedFromJar(this);
    }
    
    private boolean deepContains(Jar jar) {
	if (jar == this)
	    return true;
	for (int i = 0; i < _subjars.size(); i++) {
	    if (((Jar) _subjars.elementAt(i)).deepContains(jar))
		return true;
	}
	return false;
    }
    
    void removeView(JarView jv) {
	_viewManager.removeView(jv);
    }
    
    public boolean binds(CharacterInstance ch) {
	Enumeration e = this.getContents();
	while (e.hasMoreElements()) {
	    Bindable b = (Bindable) e.nextElement();
	    if (!(b instanceof Jar) && b.binds(ch))
		return true;
	}
	for (int i = 0; i < _subjars.size(); i++) {
	    if (((Jar) _subjars.elementAt(i)).binds(ch))
		return true;
	}
	return false;
    }
    
    public Appearance makeAppearance(Appearance appearance) {
	return new JarAppearance(appearance);
    }
    
    public Vector topLevelJars() {
	return _containingJars;
    }
    
    public void wasAddedToJar(Jar j) {
	if (_containingJars == null)
	    _containingJars = new Vector(1);
	_containingJars.addElementIfAbsent(j);
    }
    
    public void wasRemovedFromJar(Jar j) {
	_containingJars.removeElementIdentical(j);
	if (_containingJars.isEmpty())
	    _containingJars = null;
    }
    
    public void highlightForSelection() {
	_viewManager.hilite();
    }
    
    public void unhighlightForSelection() {
	_viewManager.unhilite();
    }
    
    public boolean allowDelete() {
	if (this.getWorld().ruleRefersTo(this, "REFOBJ jar ID"))
	    return false;
	return true;
    }
    
    public void delete() {
	if (this.getWorld().getState() != World.CLOSING)
	    Variable.resetVariablesSetTo(this, this.getWorld());
	this.removeAll();
	if (!_subjars.isEmpty())
	    throw new PlaywriteInternalError("Bad structure for " + this);
	while (_containingJars != null && !_containingJars.isEmpty())
	    ((Jar) _containingJars.lastElement()).remove(this);
	this.getWorld().referencedObjectWasDeleted();
	if (_viewManager.hasViews())
	    Debug.print("debug.jar",
			toString() + " being deleted still has views");
	_viewManager.delete();
	_container = null;
	_containingJars = null;
	_viewManager = null;
    }
    
    public void undelete() {
	throw new PlaywriteInternalError("Deleting jars cannot be undone.");
    }
    
    public final UniqueID getID() {
	return _uniqueID;
    }
    
    public final UniqueID getParentID() {
	return _uniqueParentID;
    }
    
    public final void setParentID(UniqueID id) {
	_uniqueParentID = id;
    }
    
    public boolean isCopyOf(ReferencedObject jar) {
	return (_uniqueID.equals(jar.getParentID())
		|| jar.getID().equals(_uniqueParentID)
		|| _name.equalsIgnoreCase(((Jar) jar).getName()));
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	Hashtable map = new Hashtable(50);
	if (this.getWorld() != newWorld)
	    map.put(this.getWorld(), newWorld);
	return copy(map, true);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Jar newJar = (Jar) map.get(this);
	if (newJar != null)
	    return newJar;
	World oldWorld = this.getWorld();
	World newWorld = (World) map.get(oldWorld);
	if (newWorld != null)
	    newJar = newWorld.findCopy(this);
	if (newJar == null) {
	    if (fullCopy) {
		if (newWorld == null) {
		    newJar = new Jar(oldWorld, Util.makeCopyName(_name));
		    map.put(this, newJar);
		    newJar._subjars = (Vector) _subjars.clone();
		    Enumeration e = this.getContents();
		    while (e.hasMoreElements()) {
			Contained obj = (Contained) e.nextElement();
			Point loc = this.getLocation(obj);
			newJar.add(obj, loc.x, loc.y);
		    }
		} else {
		    newJar = new Jar(newWorld, _name);
		    map.put(this, newJar);
		    newJar.setParentID(getID());
		    for (int i = 0; i < _subjars.size(); i++) {
			Jar subjar = (Jar) _subjars.elementAt(i);
			newJar._subjars.addElement(subjar.copy(map, fullCopy));
		    }
		    Enumeration e = this.getContents();
		    while (e.hasMoreElements()) {
			Contained obj = (Contained) e.nextElement();
			Point loc = this.getLocation(obj);
			if (obj instanceof Copyable) {
			    Contained newObj
				= ((Contained)
				   ((Copyable) obj).copy(map, false));
			    newJar.add(newObj, loc.x, loc.y);
			} else
			    throw new PlaywriteInternalError("Can't copy "
							     + obj + " in "
							     + this + " to "
							     + newWorld);
		    }
		}
	    } else if (newWorld == null)
		newJar = this;
	    else
		newJar = (Jar) copy(map, true);
	}
	newJar.getWorld().setModified(true);
	return newJar;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_name);
	ASSERT.isNotNull(_subjars);
	ASSERT.isNotNull(_uniqueID);
	super.writeExternal(out);
	out.writeUTF(_name);
	((WorldOutStream) out).writeVector(_subjars);
	out.writeObject(_uniqueID);
	out.writeObject(_uniqueParentID);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_name = in.readUTF();
	_subjars = ((WorldInStream) in).readVector();
	_uniqueID = (UniqueID) in.readObject();
	_uniqueParentID = (UniqueID) in.readObject();
	Enumeration e = this.getContents();
	while (e.hasMoreElements())
	    ((Bindable) e.nextElement()).wasAddedToJar(this);
    }
    
    public static Vector allContainingJars(Bindable thing) {
	Vector containingJars = thing.topLevelJars();
	if (containingJars == null)
	    return null;
	Vector allContainers = new Vector(1);
	iterateContainers(allContainers, containingJars);
	return allContainers;
    }
    
    private static void iterateContainers(Vector allContainers,
					  Vector newContainers) {
	for (int i = 0; i < newContainers.size(); i++) {
	    Jar nextJar = (Jar) newContainers.elementAt(i);
	    if (!allContainers.containsIdentical(nextJar)) {
		allContainers.addElement(nextJar);
		Vector nextJarsJars = nextJar.topLevelJars();
		if (nextJarsJars != null)
		    iterateContainers(allContainers, nextJarsJars);
	    }
	}
    }
    
    public String toString() {
	return _name == null ? "" : _name;
    }
}
