/* PretestView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

class PretestView extends AfterBoardView
{
    PretestView(Pretest pretest) {
	this(pretest.getAfterBoard());
    }
    
    PretestView(AfterBoard board) {
	super(board);
    }
    
    protected void setupDragAndDrop() {
	/* empty */
    }
    
    protected void setupTools() {
	this.setupBeforeBoardTools();
    }
    
    protected void setupCharacterViewTools(PlaywriteView characterView) {
	characterView.disallowTool(Tool.deleteTool);
	characterView.disallowTool(Tool.copyLoadTool);
	characterView.disallowTool(Tool.newRuleTool);
	characterView.disallowTool(Tool.editAppearanceTool);
	characterView.disallowTool(Tool.newCharacterTool);
    }
}
