/* AppearanceEditorView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Rect;

public class AppearanceEditorView extends PlaywriteView
{
    protected AppearanceEditor editor;
    
    public AppearanceEditorView(AppearanceEditor editor) {
	this.editor = editor;
    }
    
    public AppearanceEditorView(AppearanceEditor editor, Rect r) {
	super(r);
	this.editor = editor;
    }
    
    final World getWorld() {
	return editor.getWorld();
    }
    
    final AppearanceEditor getAppearanceEditor() {
	return editor;
    }
    
    public void discard() {
	super.discard();
	editor = null;
    }
}
