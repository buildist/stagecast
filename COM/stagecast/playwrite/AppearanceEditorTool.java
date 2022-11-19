/* AppearanceEditorTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Target;

public abstract class AppearanceEditorTool extends PlaywriteButton
    implements Target, AppearanceEventListener
{
    public final Color SELECTED_COLOR = Color.yellow;
    private AppearanceEditor _editor;
    
    public AppearanceEditorTool(AppearanceEditor editor,
				String imageResourceID,
				String toolTipResourceID) {
	super(Resource.getImage(imageResourceID));
	init(editor, toolTipResourceID);
    }
    
    public AppearanceEditorTool(AppearanceEditor editor,
				String toolTipResourceID) {
	super(0, 0, 0, 0);
	init(editor, toolTipResourceID);
    }
    
    private final void init(AppearanceEditor editor,
			    String toolTipResourceID) {
	_editor = editor;
	this.setToolTipText(Resource.getToolTip(toolTipResourceID));
	editor.addAppearanceEventListener(this);
	this.setLoweredColor(SELECTED_COLOR);
    }
    
    public boolean mouseDown(MouseEvent event) {
	_editor.setTool(this);
	return true;
    }
    
    public void mouseUp(MouseEvent event) {
	/* empty */
    }
    
    public void mouseDragged(MouseEvent event) {
	/* empty */
    }
    
    protected AppearanceEditor getAppearanceEditor() {
	return _editor;
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	/* empty */
    }
    
    public void mouseDragged(int x, int y, boolean ctrlKey, boolean shiftKey,
			     boolean altKey) {
	/* empty */
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	/* empty */
    }
    
    public void keyDown(KeyEvent event) {
	/* empty */
    }
    
    public void prepareForPaintFieldChange() {
	/* empty */
    }
    
    public int cursorForPoint(Point p) {
	return 0;
    }
    
    public boolean allowsBrushWidth() {
	return false;
    }
    
    public void onToolUnset() {
	/* empty */
    }
    
    public void setTool(AppearanceEditorTool tool) {
	this.setState(tool == this);
    }
    
    public void setBrushWidth(int width) {
	/* empty */
    }
    
    public void setColor(Color color, boolean completed) {
	/* empty */
    }
    
    public void setScale(int scale) {
	/* empty */
    }
    
    public void setFont(Font font) {
	/* empty */
    }
    
    public void setJustification(int justification) {
	/* empty */
    }
}
