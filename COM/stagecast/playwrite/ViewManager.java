/* ViewManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

public class ViewManager
{
    private Object model;
    private Vector viewList;
    private ViewAdder _viewAdder;
    private Owner _owner;
    private ViewRemover _viewRemover;
    
    public static interface ViewUpdater
    {
	public void updateView(Object object, Object object_0_);
    }
    
    public static interface ViewAdder
    {
	public void viewAdded(PlaywriteView playwriteview);
    }
    
    public static interface ViewRemover
    {
	public void viewRemoved(Object object, int i);
    }
    
    public static interface Owner
    {
	public void setViewManager(ViewManager viewmanager);
	
	public ViewManager getViewManager();
    }
    
    public ViewManager(Object model) {
	viewList = new Vector();
	_viewAdder = null;
	this.model = model;
    }
    
    public ViewManager(Owner owner) {
	viewList = new Vector();
	_viewAdder = null;
	_owner = owner;
	_owner.setViewManager(this);
    }
    
    public void delete() {
	model = null;
	viewList = null;
	_viewAdder = null;
    }
    
    public final Vector getViewList() {
	return viewList;
    }
    
    public boolean hasViews() {
	return viewList.size() > 0;
    }
    
    public void addView(Object newView) {
	if (newView instanceof PlaywriteView)
	    addView((PlaywriteView) newView);
	else
	    viewList.addElementIfAbsent(newView);
    }
    
    public void addView(PlaywriteView view) {
	if (!viewList.containsIdentical(view)) {
	    viewList.addElementIfAbsent(view);
	    if (_viewAdder != null)
		_viewAdder.viewAdded(view);
	}
    }
    
    public void removeView(Object newView) {
	if (viewList != null) {
	    viewList.removeElementIdentical(newView);
	    if (_viewRemover != null)
		_viewRemover.viewRemoved(newView, viewList.size());
	    _deleteIfOwned();
	}
    }
    
    private void _deleteIfOwned() {
	if (viewList.size() == 0 && _owner != null) {
	    _owner.setViewManager(null);
	    delete();
	}
    }
    
    void removeAllViews() {
	PlaywriteView view = null;
	int i = viewList.size();
	while (i-- > 0) {
	    view = (PlaywriteView) viewList.removeElementAt(i);
	    if (view.isInViewHierarchy())
		throw new PlaywriteInternalError
			  ("attempt to remove view in hierarchy");
	}
	_deleteIfOwned();
    }
    
    public void updateViews(ViewUpdater updater, Object value) {
	int i = viewList.size();
	while (i-- > 0)
	    updater.updateView(viewList.elementAt(i), value);
    }
    
    public void updateViewsExcept(Object exception, ViewUpdater updater,
				  Object value) {
	int i = viewList.size();
	while (i-- > 0) {
	    Object vue = viewList.elementAt(i);
	    if (vue != exception)
		updater.updateView(viewList.elementAt(i), value);
	}
    }
    
    public void setViewAdder(ViewAdder viewAdder) {
	_viewAdder = viewAdder;
    }
    
    public void setViewRemover(ViewRemover viewRemover) {
	_viewRemover = viewRemover;
    }
    
    public int getViewCount() {
	return viewList.size();
    }
    
    public void disableDrawing() {
	ViewUpdater disabler = new ViewUpdater() {
	    public void updateView(Object view, Object value) {
		((View) view).disableDrawing();
	    }
	};
	updateViews(disabler, null);
    }
    
    public void reenableDrawing() {
	ViewUpdater enabler = new ViewUpdater() {
	    public void updateView(Object view, Object value) {
		((View) view).reenableDrawing();
	    }
	};
	updateViews(enabler, null);
    }
    
    public void hilite() {
	ViewUpdater hiliter = new ViewUpdater() {
	    public void updateView(Object view, Object value) {
		((Hilitable) view).hilite();
	    }
	};
	updateViews(hiliter, null);
    }
    
    public void unhilite() {
	updateViews(new ViewUpdater() {
	    public void updateView(Object view, Object value) {
		((Hilitable) view).unhilite();
	    }
	}, null);
    }
    
    public void setDirty() {
	updateViews(new ViewUpdater() {
	    public void updateView(Object view, Object value) {
		((View) view).setDirty(true);
	    }
	}, null);
    }
}
