/* SplitStageAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class SplitStageAction extends RuleAction
    implements Externalizable, ResourceIDs.RuleEditorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108755412274L;
    private int _newNumber;
    private transient World _actualWorld = null;
    private transient int _oldNumber;
    private transient Stage[] _oldStages = null;
    
    SplitStageAction(int n, World world) {
	_actualWorld = world;
	_newNumber = n;
    }
    
    public SplitStageAction() {
	/* empty */
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	if (!getWorld().isInSyncPhase())
	    getWorld().addSyncAction
		(this, "execute",
		 new RuleAction.RuleExecutionArguments(container, baseX,
						       baseY));
	else {
	    World world = getWorld();
	    _actualWorld = world;
	    _oldNumber = world.getNumberOfVisibleStages();
	    if (_oldNumber > _newNumber) {
		_oldStages = new Stage[_oldNumber - _newNumber];
		int oldIndex = _oldNumber - 1;
		for (int i = _oldStages.length - 1; i >= 0; i--) {
		    _oldStages[i] = world.getStageAtIndex(oldIndex);
		    oldIndex--;
		}
	    }
	    world.splitStage(_newNumber);
	}
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	if (!PlaywriteRoot.app().inEventThread())
	    getWorld().addSyncAction(this, "undo", null);
	else if (_actualWorld != null) {
	    if (_oldNumber > _newNumber)
		_actualWorld.addVisibleStages(_oldStages);
	    else
		_actualWorld.splitStage(_oldNumber);
	}
    }
    
    public PlaywriteView createView() {
	LineView view
	    = new LineView(this, 8, "split stage action fmt",
			   new Object[] { new Integer(_newNumber) }, null);
	return view;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	SplitStageAction newAction = (SplitStageAction) map.get(this);
	if (newAction != null)
	    return newAction;
	newAction = new SplitStageAction(_newNumber, null);
	map.put(this, newAction);
	return newAction;
    }
    
    public World getWorld() {
	World world = super.getWorld();
	if (world == null)
	    return _actualWorld;
	return world;
    }
    
    public void summarize(Summary s) {
	if (_newNumber == 1)
	    s.writeText(Resource.getText("RE unsplit the stage"));
	else
	    s.writeText(Resource.getText("RE split the stage"));
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeInt(_newNumber);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_newNumber = in.readInt();
    }
    
    public String toString() {
	return ("<Show " + _newNumber
		+ (_newNumber == 1 ? " stage>" : " stages>"));
    }
}
