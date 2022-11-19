/* QuitAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public final class QuitAction extends RuleAction
    implements Debug.Constants, Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108751676722L;
    public static final String QUIT_ACTION = "quit action";
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	World world = container.getWorld();
	if (world.getState() == World.RUNNING) {
	    PlaywriteRoot.app();
	    if (PlaywriteRoot.isApplication())
		PlaywriteRoot.app().performCommandLater(PlaywriteRoot.app(),
							"quit action", world);
	}
	return RuleAction.SUCCESS;
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
	return new QuitAction();
    }
    
    public Object copy(World newWorld) {
	return new QuitAction();
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	return new QuitAction();
    }
    
    public String toString() {
	return Resource.getText("quit action");
    }
}
