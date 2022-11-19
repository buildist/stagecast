/* IndexedObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Hashtable;

public abstract class IndexedObject
    implements Cloneable, Debug.Constants, Indexed, Selectable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108752201010L;
    private transient int _index = -1;
    private transient IndexedContainer _indexedContainer = null;
    private transient PlaywriteView _view = null;
    
    public Object clone() {
	IndexedObject newIndexed = null;
	try {
	    newIndexed = (IndexedObject) super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    Debug.print(true, "bad clone on RuleTest");
	    return null;
	} finally {
	    if (newIndexed == null)
		return null;
	}
	newIndexed._index = -1;
	newIndexed._indexedContainer = null;
	newIndexed._view = null;
	return newIndexed;
    }
    
    public final GenericContainer getContainer() {
	return _indexedContainer;
    }
    
    public final void setContainer(GenericContainer container) {
	_indexedContainer = (IndexedContainer) container;
    }
    
    public final IndexedContainer getIndexedContainer() {
	return _indexedContainer;
    }
    
    public final int getIndex() {
	return _index;
    }
    
    public final void setIndex(int index) {
	_index = index;
    }
    
    public final boolean removeFromContainer() {
	if (_view != null && (!(_view instanceof BoardView)
			      || !(this instanceof BindTest))) {
	    if (_view.superview() != null)
		_view.removeFromSuperview();
	    _view.discard();
	    _view = null;
	}
	if (_indexedContainer != null)
	    _indexedContainer.remove(this);
	return _indexedContainer == null;
    }
    
    public final PlaywriteView getView() {
	return _view;
    }
    
    public final void setView(PlaywriteView view) {
	if (_view != null && view != null && _view != view) {
	    Debug.print("debug.indexed.container", "IndexedObject", this,
			": current view =", _view);
	    Debug.print("debug.indexed.container", " new View:", view);
	}
	_view = view;
	if (_view != null && _view.getModelObject() != this
	    && !(_view instanceof BoardView)) {
	    _view.setModelObject(this);
	    Debug.stackTrace();
	}
    }
    
    public final void highlightForSelection() {
	if (_view != null)
	    _view.hilite();
    }
    
    public final void unhighlightForSelection() {
	if (_view != null)
	    _view.unhilite();
    }
    
    public final boolean allowDelete() {
	return getToolArbiter().wantsToolNow(Tool.deleteTool);
    }
    
    public void delete() {
	if (_indexedContainer != null && allowDelete()) {
	    IndexedContainer temp = _indexedContainer;
	    temp.remove(this);
	    temp.userModified(this);
	}
    }
    
    public final void undelete() {
	/* empty */
    }
    
    public abstract PlaywriteView createView();
    
    public abstract Object copy();
    
    abstract ToolHandler.ToolArbiter getToolArbiter();
    
    public abstract Object copy(Hashtable hashtable, boolean bool);
    
    public abstract Object copy(World world);
}
