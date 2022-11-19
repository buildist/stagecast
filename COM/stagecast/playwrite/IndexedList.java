/* IndexedList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

class IndexedList implements IndexedContainer, Viewable, Debug.Constants
{
    private Vector _contents;
    private IndexedContainerView _view;
    private Class _contentClass;
    private ToolHandler.ToolAdder _toolAdder;
    private PermitDrag _permitDrag = null;
    private IndexedContainer.Notifier _notifier = null;
    private boolean _emptying = false;
    
    static interface PermitDrag
    {
	public boolean dragPermitted(Indexed indexed);
    }
    
    IndexedList(int size, Class contentType, ToolHandler.ToolAdder toolAdder,
		IndexedContainer.Notifier notifier) {
	_contents = new Vector(size);
	_contentClass = contentType;
	_toolAdder = toolAdder;
	_notifier = notifier;
    }
    
    IndexedList(Vector elements, Class contentType,
		ToolHandler.ToolAdder toolAdder) {
	this(elements.size(), contentType, toolAdder, null);
	add(elements);
    }
    
    void add(Vector elements) {
	for (int i = 0; i < elements.size(); i++)
	    add((Indexed) elements.elementAt(i));
    }
    
    public PlaywriteView createView() {
	return createView(false);
    }
    
    public PlaywriteView createView(boolean enableViews) {
	_view = new IndexedContainerView(this, _contentClass, _toolAdder,
					 enableViews);
	return _view;
    }
    
    public void add(Contained obj) {
	if (!(obj instanceof Indexed))
	    Debug.print("debug.indexed.container",
			"thou shalt not add non-indexed");
	else {
	    Indexed newElement = (Indexed) obj;
	    if (newElement.getIndexedContainer() != null)
		throw new BadBackpointerError(this, newElement);
	    newElement.setContainer(this);
	    newElement.setIndex(_contents.size());
	    _contents.addElement(newElement);
	    if (_notifier != null)
		_notifier.indexedAdded(newElement);
	    addViewFor(newElement);
	}
    }
    
    public boolean allowRemove(Contained obj) {
	if (obj instanceof Indexed)
	    return ((Indexed) obj).allowDelete();
	throw new PlaywriteInternalError("allowRemove called with non-indexed:"
					 + obj);
    }
    
    public boolean forceRemove(Indexed element) {
	int index = _contents.indexOfIdentical(element);
	if (index == -1)
	    return false;
	_contents.removeElementAt(index);
	element.setContainer(null);
	element.removeFromContainer();
	updateIndices(index);
	if (_notifier != null)
	    _notifier.indexedRemoved(element);
	return true;
    }
    
    public void moveContentsTo(IndexedList ic) {
	Vector contentsClone = (Vector) _contents.clone();
	empty();
	ic.add(contentsClone);
    }
    
    public void remove(Contained obj) {
	if (!(obj instanceof Indexed))
	    Debug.print("debug.indexed.container",
			"thou shalt not remove non-indexed");
	else {
	    Indexed element = (Indexed) obj;
	    if (_emptying || element.allowDelete()) {
		IndexedContainer container = element.getIndexedContainer();
		if (container != null && container != this) {
		    PlaywriteDialog.warning("Sorry, couldn't remove " + obj
					    + " from " + this);
		    Debug.print("debug.indexed.container",
				"attempt to remove ", element, " from ", this,
				" but backpointer is ",
				element.getContainer());
		    Debug.stackTrace();
		    container.forceRemove(element);
		}
		forceRemove(element);
	    }
	}
    }
    
    private void updateIndices(int start) {
	for (int i = start; i < _contents.size(); i++)
	    ((Indexed) _contents.elementAt(i)).setIndex(i);
    }
    
    private void addViewFor(Indexed newElement) {
	if (_view != null) {
	    PlaywriteView newView = newElement.getView();
	    if (newView == null || newView.isInViewHierarchy())
		newView = newElement.createView();
	    if (newView != null) {
		_view.addSubview(newView);
		newElement.setView(newView);
	    }
	}
    }
    
    public void update(Contained obj) {
	/* empty */
    }
    
    public void userModified(Indexed indexed) {
	if (_notifier != null)
	    _notifier.userModified(indexed);
    }
    
    Class getContentClass() {
	return _contentClass;
    }
    
    ToolHandler.ToolAdder getToolAdder() {
	return _toolAdder;
    }
    
    void setPermitDrag(PermitDrag permitDrag) {
	_permitDrag = permitDrag;
    }
    
    public Indexed getElementAt(int index) {
	Indexed item;
	try {
	    item = (Indexed) _contents.elementAt(index);
	} catch (IndexOutOfBoundsException indexoutofboundsexception) {
	    item = null;
	}
	return item;
    }
    
    public int insertElementAt(Indexed object, int index) {
	if (index >= _contents.size() || index < 0) {
	    add(object);
	    return object.getIndex();
	}
	if (object.getContainer() != null)
	    object.removeFromContainer();
	object.setContainer(this);
	_contents.insertElementAt(object, index);
	updateIndices(index);
	if (_notifier != null)
	    _notifier.indexedAdded(object);
	addViewFor(object);
	return object.getIndex();
    }
    
    public int getNumberOfElements() {
	return _contents.size();
    }
    
    public Enumeration getElements() {
	return _contents.elements();
    }
    
    public boolean permitDrag(Indexed member) {
	if (_permitDrag != null)
	    return _permitDrag.dragPermitted(member);
	return true;
    }
    
    public void viewDiscarded(View view) {
	if (_view == view) {
	    _view = null;
	    int i = _contents.size();
	    while (i-- > 0)
		((Indexed) _contents.elementAt(i)).setView(null);
	} else {
	    Debug.print("debug.indexed.container", "wrong view discarded");
	    Debug.stackTrace("debug.indexed.container");
	}
    }
    
    public void setNotifier(IndexedContainer.Notifier notifier) {
	_notifier = notifier;
    }
    
    void empty() {
	_emptying = true;
	Indexed element = null;
	int i = _contents.size();
	while (i-- > 0) {
	    element = (Indexed) _contents.elementAt(i);
	    remove(element);
	    if (element.getContainer() != null)
		throw new PlaywriteInternalError("unable to remove " + element
						 + " from " + this);
	}
	if (_contents.size() != 0)
	    throw new PlaywriteInternalError(String.valueOf(this)
					     + " couldn't empty");
	_emptying = false;
    }
    
    void discard() {
	if (_view != null && _view.superview() != null)
	    _view.removeFromSuperview();
	_view = null;
	empty();
    }
}
