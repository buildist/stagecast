/* XYContainer - Decompiled by JODE
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

public class XYContainer
    implements Cloneable, GenericContainer, Externalizable,
	       ResourceIDs.CommandIDs, Worldly
{
    static final int NOTIFY_ADD = 1;
    static final int NOTIFY_REMOVED = 2;
    static final int NOTIFY_UPDATE = 3;
    static final int NOTIFY_MOVED = 4;
    static final int EXACT = 1;
    static final int ASSIGNABLE = 2;
    static final int SUBCLASS = 3;
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108753904946L;
    private World _world;
    private Hashtable _contents;
    private Class _contentType;
    private int _strict;
    private boolean _primary;
    private transient Vector _viewList;
    private transient Hashtable _removedContents;
    
    XYContainer(World world, Class contentType) {
	this(world, contentType, 1, true);
    }
    
    XYContainer(World world, Class contentType, int strict, boolean primary) {
	_world = world;
	_contents = new Hashtable(20);
	_contentType = contentType;
	_strict = strict;
	_primary = primary;
	_viewList = null;
    }
    
    public XYContainer() {
	this(null, null, 1, true);
    }
    
    public World getWorld() {
	return _world;
    }
    
    public Class getContentType() {
	return _contentType;
    }
    
    public Enumeration getContents() {
	return _contents.keys();
    }
    
    public Object[] getContentArray() {
	return _contents.keysArray();
    }
    
    protected Hashtable getContentsTable() {
	return _contents;
    }
    
    boolean isPrimaryContainer() {
	return _primary;
    }
    
    public Object clone() {
	XYContainer theClone;
	try {
	    theClone = (XYContainer) super.clone();
	} catch (CloneNotSupportedException e) {
	    throw new PlaywriteInternalError(e.toString());
	}
	theClone._contents = (Hashtable) _contents.clone();
	return theClone;
    }
    
    boolean allowContentType(Class contentType) {
	switch (_strict) {
	case 1:
	    return contentType == _contentType;
	case 2:
	    return _contentType.isAssignableFrom(contentType);
	case 3:
	    return (contentType != _contentType
		    && _contentType.isAssignableFrom(contentType));
	default:
	    return false;
	}
    }
    
    boolean allowAdd(Contained obj) {
	if (contains(obj))
	    return false;
	if (isPrimaryContainer() && PlaywriteRoot.hasAuthoringLimits()
	    && getWorld().evalLimitForObjectReached(obj)) {
	    getWorld().evalLimitDialog(obj.getClass());
	    return false;
	}
	return allowContentType(obj.getClass());
    }
    
    public void add(Contained obj) {
	add(obj, -1, -1);
    }
    
    void add(Contained obj, int x, int y) {
	ASSERT.isNull(_removedContents);
	Object old = _contents.put(obj, new Point(x, y));
	if (_primary)
	    obj.setContainer(this);
	notifyViews(obj, old == null ? 1 : 4);
    }
    
    Contained copyForAdd(Contained obj) {
	return (Contained) ((Copyable) obj).copy(_world);
    }
    
    public boolean allowRemove(Contained obj) {
	if (PlaywriteRoot.isAuthoring() && RuleEditor.isRecordingOrEditing()
	    && obj instanceof ReferencedObject)
	    return false;
	if (_primary && obj instanceof Deletable)
	    return ((Deletable) obj).allowDelete();
	return true;
    }
    
    public void remove(Contained obj) {
	ASSERT.isNull(_removedContents);
	notifyViews(obj, 2);
	_contents.remove(obj);
	if (_primary) {
	    obj.setContainer(null);
	    if (obj instanceof Deletable)
		((Deletable) obj).delete();
	}
	getWorld().setModified(true);
    }
    
    public void update(Contained obj) {
	notifyViews(obj, 3);
    }
    
    boolean contains(Contained obj) {
	return _contents.get(obj) != null;
    }
    
    Object itemNamed(String name) {
	Enumeration items = getContents();
	while (items.hasMoreElements()) {
	    Object item = items.nextElement();
	    if (item instanceof Named
		&& name.equalsIgnoreCase(((Named) item).getName()))
		return item;
	}
	return null;
    }
    
    void removeAll() {
	Object[] items = _contents.keysArray();
	if (items != null) {
	    for (int i = 0; i < items.length; i++)
		remove((Contained) items[i]);
	}
    }
    
    void clearOut() {
	_contents.clear();
    }
    
    void temporaryRemove(Vector items) {
	ASSERT.isNull(_removedContents);
	_removedContents = new Hashtable();
	for (int i = 0; i < items.size(); i++) {
	    Object item = items.elementAt(i);
	    _removedContents.put(item, _contents.remove(item));
	}
    }
    
    void restoreTempRemoved() {
	Enumeration items = _removedContents.keys();
	while (items.hasMoreElements()) {
	    Object item = items.nextElement();
	    _contents.put(item, _removedContents.get(item));
	}
	_removedContents = null;
    }
    
    int size() {
	return _contents.size();
    }
    
    Point getLocation(Contained obj) {
	return (Point) _contents.get(obj);
    }
    
    void moveTo(Contained obj, int x, int y) {
	_contents.put(obj, new Point(x, y));
	notifyViews(obj, 4);
    }
    
    void addView(XYViewer view) {
	if (_viewList == null)
	    _viewList = new Vector(1);
	_viewList.addElement(view);
    }
    
    void removeView(XYViewer view) {
	_viewList.removeElement(view);
	if (_viewList.size() == 0)
	    _viewList = null;
    }
    
    void addViewFor(Contained item) {
	notifyViews(item, 1);
    }
    
    void removeViewFor(Contained item) {
	notifyViews(item, 2);
    }
    
    void notifyViews(Contained item, int howChanged) {
	if (_viewList != null) {
	    for (int i = 0; i < _viewList.size(); i++) {
		XYViewer view = (XYViewer) _viewList.elementAt(i);
		switch (howChanged) {
		case 1:
		    view.itemAdded(item, getLocation(item));
		    break;
		case 2:
		    view.itemRemoved(item);
		    break;
		case 3:
		    view.itemUpdated(item);
		    break;
		case 4:
		    view.itemMoved(item, getLocation(item));
		    break;
		}
	    }
	}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_world);
	ASSERT.isNotNull(_contentType);
	ASSERT.isNotNull(_contents);
	out.writeObject(_world);
	String contentClassName = _contentType.getName();
	if (_contentType == Bindable.class)
	    contentClassName = "COM.stagecast.playwrite.ch";
	out.writeUTF(contentClassName);
	out.writeInt(_strict);
	out.writeBoolean(_primary);
	((WorldOutStream) out).writeHashtable(_contents, true);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(XYContainer.class);
	_world = (World) in.readObject();
	String className = in.readUTF();
	try {
	    _contentType = Class.forName(className);
	} catch (ClassNotFoundException classnotfoundexception) {
	    if (className.equals("COM.stagecast.playwrite.ch"))
		_contentType = Bindable.class;
	    else if (className.equals("COM.stagecast.playwrite.dh"))
		_contentType = Bindable.class;
	    else if (className.equals("COM.stagecast.playwrite.Bindable"))
		_contentType = Bindable.class;
	    else
		throw new PlaywriteInternalError("XYContainer: unknown type:"
						 + className);
	}
	switch (version) {
	case 1:
	    if (in.readBoolean())
		_strict = 1;
	    else
		_strict = 2;
	    break;
	case 2:
	    _strict = in.readInt();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 2);
	}
	_primary = in.readBoolean();
	_contents = ((WorldInStream) in).readHashtable();
	if (_primary) {
	    Enumeration contents = _contents.keys();
	    while (contents.hasMoreElements()) {
		Contained obj = (Contained) contents.nextElement();
		obj.setContainer(this);
	    }
	}
    }
}
