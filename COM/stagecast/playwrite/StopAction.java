/* StopAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;

import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public final class StopAction extends RuleAction
    implements Debug.Constants, Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108751807794L;
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	final World world = container.getWorld();
	if (world.getState() == World.RUNNING) {
	    PlaywriteRoot.app().performCommandLater(new Target() {
		public void performCommand(String cmd, Object target) {
		    world.stopWorld();
		}
	    }, null, this);
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
	return new StopAction();
    }
    
    public Object copy(World newWorld) {
	return new StopAction();
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	return new StopAction();
    }
    
    public String toString() {
	return Resource.getText("stop action");
    }
}
