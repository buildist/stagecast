/* PlaywriteSound - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Op;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PlaywriteSound
    implements Debug.Constants, Editable, Externalizable, FirstClassValue,
	       IconModel, ModelViewInterface, Named, Proxy, ReferencedObject,
	       ResourceIDs.InstanceNameIDs, StorageProxied, Selectable, Worldly
{
    public static PlaywriteSound nullSound = null;
    static final SystemSound sysBeep = getSoundResource("boing.au");
    static final SystemSound sysSplat;
    static final int storeVersion = 3;
    static final long serialVersionUID = -3819410108756657458L;
    private World _world = null;
    private String _name = null;
    private XYContainer _container = null;
    private SystemSound _sound = null;
    private UniqueID _uniqueID;
    private UniqueID _uniqueParentID;
    private boolean _proxyFlag = false;
    private transient ViewManager _viewManager;
    
    static {
	sysBeep.preload();
	if (PlaywriteRoot.isAuthoring()) {
	    sysSplat = getSoundResource("splat.au");
	    sysSplat.preload();
	} else
	    sysSplat = null;
    }
    
    static void initStatics() {
	Op.Equal.addOperation(PlaywriteSound.class, Op.standardEqualsOp);
	nullSound
	    = new PlaywriteSound(null, Resource.getText("INST noso ID"), null);
	BuiltinProxyTable.helper.registerProxy("INST noso ID", nullSound);
	PlaywriteRoot.app();
	if (!PlaywriteRoot.isServer())
	    SystemSound.playSounds
		= PlaywriteSystem
		      .getApplicationPropertyAsBoolean("sounds_enabled", true);
    }
    
    static SystemSound getSoundResource(String resName) {
	return new SystemSound(new ResourceStreamProducer
			       (PlaywriteSound.class,
				"/COM/stagecast/creator/sounds/" + resName));
    }
    
    private PlaywriteSound(World world, String name, SystemSound sound) {
	this();
	fillInObject(world, name, sound);
    }
    
    public PlaywriteSound() {
	/* empty */
    }
    
    void fillInObject(World world, String name, SystemSound sound) {
	_world = world;
	setName(name);
	_sound = sound;
	_uniqueID = new UniqueID();
	_uniqueParentID = null;
	if (world != null) {
	    world.add(this);
	    Variable.updateSystemVariableWatchers((CocoaCharacter
						   .SYS_SOUND_VARIABLE_ID),
						  world);
	    ObjectSieve sieve = _world.getObjectSieve();
	    if (sieve != null && _sound != null && _sound.isPlayable())
		sieve.creation(_sound);
	} else
	    Debug.print("debug.sound", "Sound ", name,
			" called with world == null");
    }
    
    public final String getName() {
	return _name;
    }
    
    public final void setName(String s) {
	_name = s;
	Icon.updateIconNames(this);
    }
    
    public final World getWorld() {
	return _world;
    }
    
    public final SystemSound getSystemSound() {
	return _sound;
    }
    
    void setSystemSound(SystemSound s) {
	_sound = s;
    }
    
    public final String getMediaName() {
	return _sound == null ? "" : _sound.getMediaID();
    }
    
    public StorageProxyHelper getStorageProxyHelper() {
	return BuiltinProxyTable.helper;
    }
    
    public GenericContainer getContainer() {
	return _container;
    }
    
    public void setContainer(GenericContainer container) {
	_container = (XYContainer) container;
    }
    
    public PlaywriteView createView() {
	return createIconView();
    }
    
    public PlaywriteView createIconView() {
	return new Icon(this);
    }
    
    public Image getIconImage() {
	String imageName;
	if (_sound == null && !isProxy())
	    imageName = "NoSound";
	else if (_sound.isMacSound())
	    imageName = "UnSound";
	else
	    imageName = "Sound";
	return Resource.getImage(imageName);
    }
    
    public Rect getIconImageRect() {
	return null;
    }
    
    public void setIconImage(Image image) {
	/* empty */
    }
    
    public String getIconName() {
	return getName();
    }
    
    public void setIconName(String newName) {
	setName(newName);
    }
    
    public boolean hasIconViews() {
	return _viewManager != null && _viewManager.hasViews();
    }
    
    public ViewManager getIconViewManager() {
	if (_viewManager == null)
	    _viewManager = new ViewManager(this);
	return _viewManager;
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	Hashtable map = new Hashtable(50);
	if (getWorld() != null && getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	return copy(map, true);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	PlaywriteSound newSound = (PlaywriteSound) map.get(this);
	if (newSound != null) {
	    if (newSound.isProxy() && fullCopy)
		newSound.makeReal(this, map);
	    return newSound;
	}
	if (this == nullSound)
	    return this;
	World oldWorld = getWorld();
	World newWorld = (World) map.get(oldWorld);
	if (newWorld != null)
	    newSound = newWorld.findCopy(this);
	if (newSound == null) {
	    if (fullCopy) {
		String newName;
		if (newWorld == null)
		    newName = Util.makeCopyName(getName());
		else
		    newName = getName();
		if (newWorld == null)
		    newSound = new PlaywriteSound(oldWorld, newName, _sound);
		else {
		    newSound = new PlaywriteSound(newWorld, newName,
						  (SystemSound) _sound.copy());
		    map.put(this, newSound);
		    if (newWorld != null)
			newSound.setParentID(getID());
		}
	    } else if (newWorld == null)
		newSound = this;
	    else
		newSound = (PlaywriteSound) makeProxy(map);
	} else if (newSound.isProxy() && fullCopy)
	    newSound.makeReal(this, map);
	else
	    map.put(this, newSound);
	newSound.getWorld().setModified(true);
	return newSound;
    }
    
    public void edit() {
	play();
    }
    
    public void highlightForSelection() {
	_viewManager.hilite();
    }
    
    public void unhighlightForSelection() {
	_viewManager.unhilite();
    }
    
    public final boolean isProxy() {
	return _proxyFlag;
    }
    
    public final void setProxy(boolean b) {
	_proxyFlag = b;
    }
    
    public Object makeProxy(Hashtable map) {
	World newWorld = (World) map.get(getWorld());
	if (newWorld == null)
	    throw new PlaywriteInternalError
		      ("Can't make proxies in intraworld copies: " + this);
	PlaywriteSound newSound = (PlaywriteSound) copy(map, true);
	return newSound;
    }
    
    public void makeReal(Object source, Hashtable map) {
	PlaywriteSound oldSound = (PlaywriteSound) source;
	setProxy(false);
	map.put(oldSound, this);
	_sound = (SystemSound) oldSound._sound.copy();
    }
    
    public boolean isVisible() {
	return _proxyFlag ^ true;
    }
    
    public void setVisibility(boolean b) {
	if (b && _proxyFlag)
	    throw new PlaywriteInternalError("Proxy sounds cannot be visible: "
					     + this);
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
    
    public boolean isCopyOf(ReferencedObject sound) {
	return (_uniqueID.equals(sound.getParentID())
		|| sound.getID().equals(_uniqueParentID));
    }
    
    public void play(CocoaCharacter ch) {
	GenericContainer container = ch.getContainer();
	if (container != null && container instanceof Stage) {
	    Stage stage = (Stage) container;
	    if (stage.getWorld().isStageVisible(stage))
		play();
	    if (ch instanceof CharacterInstance)
		stage.addNoisy((CharacterInstance) ch);
	} else
	    play();
    }
    
    public void play() {
	if (_sound != null) {
	    Boolean playFlag
		= ((Boolean)
		   Variable.getSystemValue(World.SYS_ENABLE_SOUND_VARIABLE_ID,
					   getWorld()));
	    if (playFlag.booleanValue())
		_sound.play();
	}
    }
    
    public void remove() {
	_container.remove(this);
    }
    
    public String toString() {
	return _name == null ? "<unnamed>" : _name;
    }
    
    public boolean allowDelete() {
	if (this == nullSound)
	    return false;
	if (getWorld().ruleRefersTo(this, "REFOBJ sou ID"))
	    return false;
	return true;
    }
    
    public void delete() {
	if (this != nullSound) {
	    Variable.resetVariablesSetTo(this, getWorld());
	    Variable.updateSystemVariableWatchers((CocoaCharacter
						   .SYS_SOUND_VARIABLE_ID),
						  getWorld());
	    getWorld().referencedObjectWasDeleted();
	    if (hasIconViews()) {
		if (_viewManager.hasViews())
		    Debug.print("debug.sound",
				toString() + " being deleted still has views");
		_viewManager.delete();
	    }
	    _container = null;
	    _sound = null;
	    _viewManager = null;
	}
    }
    
    public void undelete() {
	throw new PlaywriteInternalError("Deleting sounds cannot be undone.");
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_name);
	ASSERT.isTrue(_container != null || _proxyFlag,
		      "Sound has no container");
	ASSERT.isNotNull(_uniqueID);
	out.writeUTF(_name);
	out.writeObject(_container);
	out.writeObject(_sound);
	out.writeObject(_uniqueID);
	out.writeObject(_uniqueParentID);
	out.writeBoolean(_proxyFlag);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(PlaywriteSound.class);
	_name = in.readUTF();
	_container = (XYContainer) in.readObject();
	_world = ((WorldInStream) in).getTargetWorld();
	switch (version) {
	case 1:
	    _sound = new SystemSound((byte[]) in.readObject());
	    in.readObject();
	    break;
	case 2:
	case 3:
	    _sound = (SystemSound) in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 3);
	}
	_uniqueID = (UniqueID) in.readObject();
	_uniqueParentID = (UniqueID) in.readObject();
	switch (version) {
	case 3:
	    _proxyFlag = in.readBoolean();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 3);
	case 1:
	case 2:
	    /* empty */
	}
	ObjectSieve sieve = _world.getObjectSieve();
	if (sieve != null && _sound != null && _sound.isPlayable())
	    sieve.creation(_sound);
    }
}
