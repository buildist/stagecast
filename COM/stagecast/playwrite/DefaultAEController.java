/* DefaultAEController - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Window;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;

public class DefaultAEController implements AppearanceEditorController
{
    private static boolean inited = false;
    private Hashtable activeEditors;
    
    public DefaultAEController() {
	ASSERT.isTrue(inited ^ true);
	WidthButton.initStatics();
	activeEditors = new Hashtable();
	AppearanceEditor.setController(this);
	inited = true;
    }
    
    public void displayEditorFor(CocoaCharacter character) {
	CharacterPrototype proto = character.getPrototype();
	AppearanceEditor editor
	    = AppearanceEditor.displayAppearanceEditor(character);
	if (editor != null)
	    activeEditors.put(proto, editor);
    }
    
    public void displayEditorFor(CocoaCharacter character, Appearance appear) {
	CharacterPrototype proto = character.getPrototype();
	AppearanceEditor editor
	    = AppearanceEditor.displayAppearanceEditor(character, appear);
	if (editor != null)
	    activeEditors.put(proto, editor);
    }
    
    public void destroyEditorFor(CharacterPrototype proto) {
	AppearanceEditor editor = (AppearanceEditor) activeEditors.get(proto);
	if (editor != null) {
	    if (editor.isVisible())
		editor.close();
	    activeEditors.remove(proto);
	}
    }
    
    public boolean prepareForClose() {
	Enumeration e = activeEditors.elements();
	while (e.hasMoreElements()) {
	    AppearanceEditor ae = (AppearanceEditor) e.nextElement();
	    if (!ae.prepareToClose())
		return false;
	}
	return true;
    }
    
    public boolean isEditorView(Object viewLikeThing) {
	return (viewLikeThing instanceof AppearanceDrawerView
		|| viewLikeThing instanceof AppearanceDrawerItemView
		|| viewLikeThing instanceof AppearanceEditorView);
    }
    
    public boolean isEditorWindow(Window window) {
	return window instanceof AppearanceEditor;
    }
    
    AppearanceEditor getEditorFor(CharacterPrototype proto) {
	return (AppearanceEditor) activeEditors.get(proto);
    }
    
    boolean isPositionNearActiveEditor(Point position) {
	Enumeration editors = activeEditors.elements();
	while (editors.hasMoreElements() == true) {
	    AppearanceEditor editor = (AppearanceEditor) editors.nextElement();
	    if (Math.abs(editor.x() - position.x) < 5
		&& Math.abs(editor.y() - position.y) < 5)
		return true;
	}
	return false;
    }
}
