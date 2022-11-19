/* Selection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.Hashtable;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

final class Selection implements ResourceIDs.DialogIDs
{
    private static final Hashtable selectTable = new Hashtable(3);
    private Vector _selectedObjects = new Vector(10);
    private ModalView _modalView = null;
    private View _containingView = null;
    private boolean _isDeleting = false;
    
    static void createSelectionObject() {
	selectTable.put(Application.application(), new Selection());
    }
    
    static void destroySelectionObject() {
	selectTable.remove(Application.application());
    }
    
    static void select(Selectable obj, View view) {
	((Selection) selectTable.get(Application.application()))._select(obj,
									 view);
    }
    
    static void addToSelection(Selectable obj, View view) {
	((Selection) selectTable.get(Application.application()))
	    ._addToSelection(obj, view);
    }
    
    static void unselect(Selectable obj) {
	((Selection) selectTable.get(Application.application()))
	    ._unselect(obj);
    }
    
    static void unselectAll() {
	((Selection) selectTable.get(Application.application()))
	    ._unselectAll();
    }
    
    static boolean isSelected(Object obj) {
	return ((Selection) selectTable.get(Application.application()))
		   ._isSelected(obj);
    }
    
    static int selectionSize() {
	return ((Selection) selectTable.get(Application.application()))
		   ._selectionSize();
    }
    
    static Enumeration getSelection() {
	return ((Selection) selectTable.get(Application.application()))
		   ._getSelection();
    }
    
    static void deleteSelection() {
	((Selection) selectTable.get(Application.application()))
	    ._deleteSelection();
    }
    
    static void delete(Selectable s) {
	((Selection) selectTable.get(Application.application()))._delete(s);
    }
    
    static void showModalView(ModalView view) {
	((Selection) selectTable.get(Application.application()))
	    ._showModalView(view);
    }
    
    static void hideModalView() {
	((Selection) selectTable.get(Application.application()))
	    ._hideModalView();
    }
    
    static void resetGlobalState(MouseEvent event) {
	((Selection) selectTable.get(Application.application()))
	    ._resetGlobalState(event);
    }
    
    static void resetGlobalState() {
	resetGlobalState(null);
    }
    
    private Selection() {
	/* empty */
    }
    
    private void _select(Selectable obj, View view) {
	unselectAll();
	addToSelection(obj, view);
	_containingView = view;
    }
    
    private void _addToSelection(Selectable obj, View view) {
	if (_containingView == null)
	    _containingView = view;
	else if (_containingView != view) {
	    PlaywriteDialog.warning(PlaywriteSystem.isMRJ_2_1_x()
				    ? "dialog us-mac" : "dialog us");
	    return;
	}
	if (_selectedObjects.containsIdentical(obj))
	    unselect(obj);
	else {
	    obj.highlightForSelection();
	    _selectedObjects.addElement(obj);
	}
    }
    
    private void _unselect(Selectable obj) {
	obj.unhighlightForSelection();
	_selectedObjects.removeElementIdentical(obj);
	if (_selectedObjects.isEmpty())
	    _containingView = null;
    }
    
    private void _unselectAll() {
	for (int i = 0; i < _selectedObjects.size(); i++) {
	    Selectable obj = (Selectable) _selectedObjects.elementAt(i);
	    obj.unhighlightForSelection();
	}
	_selectedObjects.removeAllElements();
	_containingView = null;
    }
    
    private boolean _isSelected(Object obj) {
	return _selectedObjects.containsIdentical(obj);
    }
    
    private int _selectionSize() {
	return _selectedObjects.size();
    }
    
    private Enumeration _getSelection() {
	return _selectedObjects.elements();
    }
    
    private void _delete(Selectable s) {
	_select(s, null);
	_deleteSelection();
    }
    
    private void _deleteSelection() {
	if (_selectedObjects.size() != 0 && !_isDeleting) {
	    if (_selectedObjects.firstElement() instanceof Worldly) {
		World world
		    = ((Worldly) _selectedObjects.firstElement()).getWorld();
		if (world != null) {
		    if (world.getState() == World.RUNNING)
			return;
		    world.setModified(true);
		}
	    }
	    _isDeleting = true;
	    hideModalView();
	    GenericContainer container = null;
	    if (_selectedObjects.firstElement() instanceof Contained) {
		if (_containingView instanceof PlaywriteView
		    && (((PlaywriteView) _containingView).getModelObject()
			instanceof GenericContainer))
		    container
			= ((GenericContainer)
			   ((PlaywriteView) _containingView).getModelObject());
		else
		    container = ((Contained) _selectedObjects.firstElement())
				    .getContainer();
	    }
	    for (int i = 0; i < _selectedObjects.size(); i++) {
		Selectable obj = (Selectable) _selectedObjects.elementAt(i);
		obj.unhighlightForSelection();
		if (container != null
		    && container.allowRemove((Contained) obj)) {
		    if (container instanceof CharacterContainer
			&& obj instanceof CocoaCharacter)
			((CocoaCharacter) obj).doDeleteAction();
		    else if (obj instanceof Indexed)
			((Indexed) obj).delete();
		    else
			container.remove((Contained) obj);
		} else if (container == null && obj.allowDelete())
		    obj.delete();
	    }
	    _selectedObjects.removeAllElements();
	    _isDeleting = false;
	}
    }
    
    private void _showModalView(ModalView view) {
	hideModalView();
	view.show();
	_modalView = view;
    }
    
    private void _hideModalView() {
	if (_modalViewExists()) {
	    _modalView.hide();
	    _modalView = null;
	}
    }
    
    private boolean _modalViewExists() {
	return _modalView != null;
    }
    
    private void _resetGlobalState(MouseEvent event) {
	if (event == null || !event.isShiftKeyDown())
	    unselectAll();
	hideModalView();
    }
    
    private void _resetGlobalState() {
	resetGlobalState(null);
    }
}
