/* WorldInStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.OptionalDataException;
import java.util.zip.ZipFile;

import COM.stagecast.ifc.netscape.application.FoundationApplet;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class WorldInStream extends ObjectInputStream
    implements Debug.Constants, ResourceIDs.DialogIDs, WorldBuilder
{
    ClassRegistry _streamRegistry;
    ZipFile _zipContainer;
    PlaywriteLoader _loader;
    String _creatorVersion;
    int _filesystemVersion;
    World _targetWorld;
    Hashtable _mediaNameMap;
    UpdateManager _worldFinishedReadingUpdater;
    
    WorldInStream(InputStream in, ZipFile container) throws IOException {
	super(in);
	this.enableResolveObject(true);
	_zipContainer = container;
	_loader = null;
    }
    
    WorldInStream(InputStream in, Hashtable mediaNameMap) throws IOException {
	super(in);
	this.enableResolveObject(true);
	_zipContainer = null;
	_loader = null;
	_mediaNameMap = mediaNameMap;
    }
    
    WorldInStream(World w) throws IOException {
	this(new ByteArrayInputStream(w.getSnapshotBuffer()),
	     w.getMediaSource());
	_loader = w.getLoader();
    }
    
    void setTargetWorld(World world) {
	_targetWorld = world;
	if (world.getMediaSource() == null)
	    world.setMediaSource(_zipContainer);
    }
    
    public World getTargetWorld() {
	return _targetWorld;
    }
    
    public ZipFile getMediaContainer() {
	return _zipContainer;
    }
    
    public int getFilesystemVersion() {
	return _filesystemVersion;
    }
    
    public PlaywriteLoader getLoader() {
	return _loader;
    }
    
    public String mapName(String old) {
	return (String) (_mediaNameMap == null ? (Object) old
			 : _mediaNameMap.get(old));
    }
    
    public int loadVersion(Class cls) {
	return _streamRegistry.version(cls);
    }
    
    World readWorld() {
	return readWorld(true);
    }
    
    void overrideWorld() throws IOException, OptionalDataException {
	readWorld(false);
    }
    
    private World readWorld(boolean assignRules) {
	World world = null;
	if (PlaywriteRoot.isApplication()) {
	    ASSERT.isNull(_loader);
	    _loader = PlaywriteRoot.app()._loader;
	    if (_loader != null)
		_loader.getPlugins().addExtensions(_zipContainer, false);
	}
	Debug.print("debug.loader", "Creating class loader ", _loader);
	BitmapManager.enforceMinCacheLimits();
	try {
	    Debug.print("debug.objectstore", "Reading prologue");
	    readPrologue();
	    Debug.print("debug.objectstore", "Reading world");
	    world = (World) this.readObject();
	    world.setCreatorVersion(_creatorVersion);
	    world.setFSVersion(_filesystemVersion);
	    Object next = this.readObject();
	    if (loadVersion(World.class) < 7) {
		Debug.print("debug.objectstore", "Reading instances");
		for (/**/; next instanceof CharacterInstance;
		     next = this.readObject()) {
		    /* empty */
		}
	    }
	    PlaywriteRoot.setProgress(80);
	    Debug.print("debug.objectstore", "Reading rules - ",
			Debug.mem("debug.objectstore"));
	    while (next instanceof Subroutine) {
		if (assignRules) {
		    Subroutine s = (Subroutine) next;
		    CharacterPrototype cp = s.getOwner();
		    try {
			cp.setMainSubroutine(s);
		    } catch (BadBackpointerError badbackpointererror) {
			throw new RecoverableException("dialog badW", true);
		    }
		}
		try {
		    next = this.readObject();
		} catch (BadBackpointerError badbackpointererror) {
		    throw new RecoverableException("dialog badW", true);
		}
		PlaywriteRoot.incrementProgress(1, 100);
	    }
	    if (!"*End of world*".equals(next))
		System.out.println("End marker missing, totally lost");
	    if (loadVersion(Stage.class) < 4) {
		Enumeration stages = world.getStages().getContents();
		while (stages.hasMoreElements())
		    ((Stage) stages.nextElement()).determineActiveCharacters();
	    }
	    Debug.print("debug.objectstore", "Read world complete - ",
			Debug.mem("debug.objectstore"));
	} catch (ClassNotFoundException e) {
	    System.out
		.println("Attempt to read object of unknown class: " + e);
	    Debug.stackTrace(e);
	} catch (IOException e) {
	    System.out.println("IO Error: " + e);
	    Debug.stackTrace(e);
	} finally {
	    BitmapManager.enforceDefaultCacheLimits();
	}
	if (_worldFinishedReadingUpdater != null) {
	    _worldFinishedReadingUpdater.update(this, null);
	    _worldFinishedReadingUpdater.removeAllWatchers();
	}
	return world;
    }
    
    protected Class resolveClass(ObjectStreamClass v)
	throws IOException, ClassNotFoundException {
	String className = v.getName();
	if (className.startsWith("com.stagecast.")) {
	    className = "COM" + className.substring(3);
	    Debug.print("debug.objectstore.detail",
			"replacing classname with ", className);
	}
	int version = _streamRegistry.version(className);
	byte[] bytecodes = null;
	Debug.print("debug.objectstore.detail", "Resolving class ", className,
		    " version " + version);
	if (v.forClass() != null)
	    return v.forClass();
	Class cl = null;
	if (_loader == null) {
	    try {
		cl = ((FoundationApplet) PlaywriteRoot.app().applet())
			 .classForName(className);
	    } catch (ClassNotFoundException classnotfoundexception) {
		throw new ExtensionMissingException();
	    }
	} else {
	    try {
		cl = _loader.loadClass(className);
	    } catch (ClassNotFoundException classnotfoundexception) {
		cl = super.resolveClass(v);
	    }
	}
	return cl;
    }
    
    protected Object resolveObject(Object obj) throws IOException {
	if (obj instanceof Resolvable) {
	    Debug.print("debug.objectstore.detail", "Resolving proxy ", obj);
	    obj = ((Resolvable) obj).resolve(this);
	    if (obj == World.SYS_WORLD_NAME_VARIABLE_ID)
		obj = new Variable("<world name>", getTargetWorld());
	    return obj;
	}
	if (obj instanceof Variable) {
	    Variable v = (Variable) obj;
	    if (v.isSystemType(Tutorial.SYS_TUTORIAL_FILE_VARIABLE_ID)) {
		Debug.print("debug.objectstore.detail",
			    "Resolving tutorial variable ", obj);
		return (Variable.systemVariable
			(Tutorial.SYS_TUTORIAL_FILE_VARIABLE_ID,
			 _targetWorld));
	    }
	    if (v.getName().equals("<deleted>")) {
		Debug.print("debug.objectstore.detail",
			    "Resolving deleted variable ", obj);
		return Variable.deletedVariable;
	    }
	    if (v.getClass() != Variable.class
		&& v.getClass() != FingerVariable.class) {
		Debug.print("debug.objectstore.detail",
			    "Mapping old system variable to user variable");
		return new Variable("V1-" + v.getName(), _targetWorld, true);
	    }
	}
	Debug.print("debug.objectstore.detail", "Deserializing ", obj);
	return obj;
    }
    
    void readPrologue() throws IOException, ClassNotFoundException {
	_creatorVersion = this.readUTF();
	_filesystemVersion = this.readInt();
	int supportedVersion = PlaywriteRoot.getObjectStoreVersion();
	if (_filesystemVersion > supportedVersion)
	    throw new UnknownVersionError("Object store", _filesystemVersion,
					  supportedVersion);
	if (PlaywriteRoot.isServer() && _filesystemVersion < 2)
	    throw new UnknownVersionError("Server", _filesystemVersion, 2);
	_streamRegistry
	    = ClassRegistry.loadVersionRegistry(this, _filesystemVersion);
    }
    
    Vector readVector() throws IOException, ClassNotFoundException {
	int size = this.readInt();
	if (size < 0)
	    return null;
	Vector v = new Vector(size);
	for (int i = 0; i < size; i++)
	    v.addElement(this.readObject());
	return v;
    }
    
    Hashtable readHashtable() throws IOException, ClassNotFoundException {
	int size = this.readInt();
	boolean hasPoints = this.readBoolean();
	Hashtable h = new Hashtable(size);
	for (int i = 0; i < size; i++) {
	    Object key = this.readObject();
	    if (hasPoints) {
		int x = this.readInt();
		int y = this.readInt();
		h.put(key, new Point(x, y));
	    } else
		h.put(key, this.readObject());
	}
	return h;
    }
    
    public void addFinishedReadingWatcher(Watcher w) {
	if (_worldFinishedReadingUpdater == null)
	    _worldFinishedReadingUpdater = new UpdateManager();
	_worldFinishedReadingUpdater.add(w);
    }
}
