/* NullAppearanceEditor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Window;

public class NullAppearanceEditor implements AppearanceEditorController
{
    public void displayEditorFor(CocoaCharacter character) {
	/* empty */
    }
    
    public void displayEditorFor(CocoaCharacter character, Appearance appear) {
	/* empty */
    }
    
    public void destroyEditorFor(CharacterPrototype proto) {
	/* empty */
    }
    
    public boolean isEditorView(Object viewLikeThing) {
	return false;
    }
    
    public boolean isEditorWindow(Window window) {
	return false;
    }
    
    public boolean prepareForClose() {
	return true;
    }
}
