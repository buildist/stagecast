/* AppearanceEditorController - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Window;

public interface AppearanceEditorController
{
    public void displayEditorFor(CocoaCharacter cocoacharacter);
    
    public void displayEditorFor(CocoaCharacter cocoacharacter,
				 Appearance appearance);
    
    public void destroyEditorFor(CharacterPrototype characterprototype);
    
    public boolean isEditorView(Object object);
    
    public boolean isEditorWindow(Window window);
    
    public boolean prepareForClose();
}
