/* BackgroundImage - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.FileChooser;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Op;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class BackgroundImage
    implements Debug.Constants, Externalizable, FirstClassValue, IconModel,
	       ModelViewInterface, Named, Proxy, ReferencedObject,
	       ResourceIDs.DialogIDs, ResourceIDs.InstanceNameIDs, Selectable,
	       StorageProxied
{
    static BackgroundImage noBackground;
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108752987442L;
    private String _name;
    private Bitmap _image;
    private Bitmap _icon;
    private XYContainer _container;
    private UniqueID _uniqueID;
    private UniqueID _uniqueParentID;
    private boolean _proxyFlag;
    private transient World _world;
    private transient ViewManager _viewManager;
    
    static void initStatics() {
	noBackground
	    = new BackgroundImage(null, Resource.getText("INST nobg ID"),
				  null);
	noBackground.setIconImage(Util.createFilledBitmap(16, 16,
							  Color.black));
	BuiltinProxyTable.helper.registerProxy("INST nobg ID", noBackground);
	Op.Equal.addOperation(BackgroundImage.class, Op.standardEqualsOp);
    }
    
    static String importAllBackgrounds(final World world) {
	FileChooser chooser
	    = new FileChooser(PlaywriteRoot.getMainRootView(),
			      Resource.getText("dialog cabf"), 0);
	chooser.showModally();
	String fname = chooser.file();
	if (fname == null)
	    return null;
	World.setBackgroundDirectory(chooser.directory());
	FileIO.FileIterator fileHandler = new FileIO.FileIterator() {
	    public void handleFile(String fileName) {
		BackgroundImage newBg = new BackgroundImage();
		Bitmap data
		    = ImageIO.importBitmapNamed(newBg, fileName, false);
		if (data != null) {
		    BackgroundImage itemToReplace
			= ((BackgroundImage)
			   Util.findEqualOrSameName(world.getBackgrounds()
							.getContents(),
						    newBg.getName()));
		    if (itemToReplace == null) {
			newBg.setImage(data);
			world.add(newBg);
		    } else
			itemToReplace.setImage(data);
		}
	    }
	};
	FileIO.iterateOverDirectory(chooser.directory(), fileHandler, false,
				    null);
	return fname;
    }
    
    BackgroundImage(XYContainer container, String name, Bitmap image) {
	_name = name;
	setImage(image);
	_uniqueID = new UniqueID();
	_uniqueParentID = null;
	_proxyFlag = false;
	if (container != null) {
	    container.add(this);
	    _world = container.getWorld();
	}
    }
    
    public BackgroundImage() {
	this(null, null, null);
    }
    
    public final String getName() {
	return _name;
    }
    
    public final void setName(String s) {
	_name = s;
    }
    
    public final Bitmap getImage() {
	return _image;
    }
    
    final void setImage(Bitmap image) {
	_image = image;
	if (_image != null)
	    _icon = BitmapManager.createScaledBitmapManager(_image, 16, 16);
    }
    
    public StorageProxyHelper getStorageProxyHelper() {
	return BuiltinProxyTable.helper;
    }
    
    public GenericContainer getContainer() {
	return _container;
    }
    
    public void setContainer(GenericContainer container) {
	_container = (XYContainer) container;
	if (_container != null)
	    _world = _container.getWorld();
    }
    
    public PlaywriteView createView() {
	return createIconView();
    }
    
    public PlaywriteView createIconView() {
	return new Icon(this);
    }
    
    public Image getIconImage() {
	return _icon;
    }
    
    public Rect getIconImageRect() {
	return new Rect(0, 0, _icon.width(), _icon.height());
    }
    
    public void setIconImage(Image image) {
	_icon = (Bitmap) image;
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
	if (getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	return copy(map, true);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	BackgroundImage newImage = (BackgroundImage) map.get(this);
	if (newImage != null) {
	    if (newImage.isProxy() && fullCopy)
		newImage.makeReal(this, map);
	    return newImage;
	}
	if (this == noBackground)
	    return this;
	World oldWorld = getWorld();
	World newWorld = (World) map.get(oldWorld);
	if (newWorld != null)
	    newImage = newWorld.findCopy(this);
	if (newImage == null) {
	    if (fullCopy) {
		String newName;
		if (newWorld == null)
		    newName = Util.makeCopyName(getName());
		else
		    newName = getName();
		if (newWorld == null)
		    newImage = new BackgroundImage(newWorld.getBackgrounds(),
						   newName, _image);
		else {
		    newImage = new BackgroundImage(newWorld.getBackgrounds(),
						   newName,
						   BitmapManager.copy(_image));
		    newImage.setParentID(getID());
		}
		map.put(this, newImage);
	    } else if (newWorld == null)
		newImage = this;
	    else
		newImage = (BackgroundImage) makeProxy(map);
	} else if (newImage.isProxy() && fullCopy)
	    newImage.makeReal(this, map);
	else
	    map.put(this, newImage);
	return newImage;
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
	BackgroundImage newImage = (BackgroundImage) copy(map, true);
	return newImage;
    }
    
    public void makeReal(Object source, Hashtable map) {
	BackgroundImage oldImage = (BackgroundImage) source;
	setProxy(false);
	map.put(oldImage, this);
	setImage(BitmapManager.copy(oldImage.getImage()));
    }
    
    public boolean isVisible() {
	return _proxyFlag ^ true;
    }
    
    public void setVisibility(boolean b) {
	if (b && _proxyFlag)
	    throw new PlaywriteInternalError
		      ("Proxy backgrounds cannot be visible: " + this);
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
    
    public World getWorld() {
	return _world;
    }
    
    public void remove() {
	_container.remove(this);
    }
    
    public String toString() {
	return "<Background '" + _name + "'>";
    }
    
    public boolean allowDelete() {
	return this != noBackground;
    }
    
    public void delete() {
	getWorld().referencedObjectWasDeleted();
	setName("<deleted>");
	_image = null;
    }
    
    public void undelete() {
	/* empty */
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_name);
	ASSERT.isNotNull(_image);
	if (_world.getFSVersion() < 2) {
	    BitmapManager original = (BitmapManager) _image;
	    int format = original.getSourceFormat();
	    if (format != 1) {
		try {
		    BitmapManager newImage
			= (BitmapManager) ImageIO.convertToJpeg(original, 70);
		    original.become(newImage);
		    setImage(original);
		} catch (IOException ioexception) {
		    Debug.print(true, "Background conversion to JPEG failed");
		}
	    }
	}
	out.writeUTF(_name);
	out.writeObject(_image);
	out.writeObject(_icon);
	out.writeObject(_uniqueID);
	out.writeObject(_uniqueParentID);
	_world.add(this);
    }
    
    public void readExternal(ObjectInput oi)
	throws IOException, ClassNotFoundException {
	WorldInStream in = (WorldInStream) oi;
	int version = in.loadVersion(BackgroundImage.class);
	_name = in.readUTF();
	switch (version) {
	case 1:
	    in.readObject();
	    setImage((Bitmap) in.readObject());
	    break;
	case 2:
	    _image = (Bitmap) in.readObject();
	    _icon = (Bitmap) in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 2);
	}
	_uniqueID = (UniqueID) in.readObject();
	_uniqueParentID = (UniqueID) in.readObject();
	in.getTargetWorld().add(this);
    }
}
