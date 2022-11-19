/* ToolPalette - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class ToolPalette extends AppearanceEditorView
{
    private ToolBar toolbar;
    private ColorGridView colorGridView;
    private BrushSizeBar brushSizeBar;
    private EyeDropper eyeDropper;
    private CurrentColorIndicator currentColorIndicator;
    
    public ToolPalette(AppearanceEditor editor) {
	super(editor);
	toolbar = new ToolBar(editor);
	this.addSubview(toolbar);
	brushSizeBar = new BrushSizeBar(editor);
	this.addSubview(brushSizeBar);
	colorGridView = new ColorGridView(editor);
	this.addSubview(colorGridView);
	eyeDropper = new EyeDropper(editor);
	this.addSubview(eyeDropper);
	currentColorIndicator = new CurrentColorIndicator(editor);
	this.addSubview(currentColorIndicator);
	this.getAppearanceEditor().setBrushWidth(1);
	selectSelectionTool();
	this.setTransparent(true);
	layoutComponents();
    }
    
    public void selectSelectionTool() {
	this.getAppearanceEditor()
	    .setTool((AppearanceEditorTool) toolbar.subviews().elementAt(0));
    }
    
    public void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	layoutComponents();
    }
    
    private void layoutComponents() {
	int width = this.width();
	int y1 = 0;
	toolbar.setBounds(0, y1, 50, 125);
	brushSizeBar.setBounds(width - 25, y1, 25, 125);
	int y3 = y1 + 125 - 1 + 9;
	colorGridView.setBounds(0, y3, width, width);
	int y4 = y3 + width - 1 + 6;
	eyeDropper.setBounds(width / 2 - 3 - 25, y4, 25, 25);
	currentColorIndicator.setBounds(width / 2 + 3, y4, 25, 25);
    }
    
    public void discard() {
	super.discard();
	toolbar = null;
	colorGridView = null;
	brushSizeBar = null;
	eyeDropper = null;
	currentColorIndicator = null;
    }
}
