/* ResetAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Hashtable;

public class ResetAction extends RuleAction
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108751742258L;
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	World world = this.getWorld();
	if (world != null && world.getState() == World.RUNNING
	    && world.hasSavedState()) {
	    world.stopAndReset();
	    return RuleAction.SUCCESS;
	}
	return RuleAction.FAILURE;
    }
    
    public void undo() {
	/* empty */
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	/* empty */
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return false;
    }
    
    public PlaywriteView createView() {
	PlaywriteView view = new LineView(8);
	COM.stagecast.ifc.netscape.application.Label label
	    = Util.makeLabel(toString());
	view.addSubview(label);
	view.setModelObject(this);
	view.sizeToMinSize();
	return view;
    }
    
    public Object copy() {
	return new ResetAction();
    }
    
    public Object copy(World newWorld) {
	return new ResetAction();
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	return new ResetAction();
    }
    
    public String toString() {
	return "Reset World";
    }
}
