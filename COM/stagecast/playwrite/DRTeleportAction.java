/* DRTeleportAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Expression;

class DRTeleportAction extends RuleAction implements Debug.Constants
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108752856370L;
    private transient CharacterInstance _teleportedCharacter;
    private transient Stage _oldStage = null;
    private transient Stage _newStage = null;
    private transient int _fromX;
    private transient int _fromY;
    private Expression _stageExpression = null;
    private String _newStageName = "";
    
    DRTeleportAction(String stageName) {
	_newStageName = stageName;
    }
    
    DRTeleportAction(VariableAlias expression) {
	_stageExpression = expression;
    }
    
    public DRTeleportAction() {
	/* empty */
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	World world = this.getWorld();
	Debug.print("debug.dr", "executing DRteleportAction");
	if (_stageExpression != null) {
	    Object val = _stageExpression.eval();
	    if (val instanceof String)
		_newStageName = (String) val;
	    else {
		Debug.print("debug.dr",
			    "DRTeleportAction: couldn't get Stage named: ",
			    val);
		_newStageName = "";
	    }
	}
	_newStage = world.findStageNamedDR(_newStageName);
	if (_newStage == null)
	    return RuleAction.FAILURE;
	_teleportedCharacter = world.getMainCharacter();
	if (_teleportedCharacter == null) {
	    Debug.print("debug.dr", "main character is null");
	    _oldStage = world.getCurrentStageDRHack();
	} else {
	    CharacterContainer oldContainer
		= (CharacterContainer) _teleportedCharacter.getContainer();
	    _oldStage = (Stage) oldContainer;
	    if (_oldStage == null) {
		Debug.print("debug.dr", "DRTeleport: old stage = null");
		return RuleAction.FAILURE;
	    }
	    Point newEntrancePoint = _newStage.getEntrance();
	    _fromX = _teleportedCharacter.getH();
	    _fromY = _teleportedCharacter.getV();
	    int toX = newEntrancePoint.x;
	    int toY = newEntrancePoint.y;
	    _oldStage.remove(_teleportedCharacter);
	    _newStage.add(_teleportedCharacter, toX, toY, -1);
	}
	RuleAction action = new SwitchStageAction(_newStage, true);
	world.executeAction(action, container, 0, 0);
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	if (_teleportedCharacter != null) {
	    _newStage.remove(_teleportedCharacter);
	    _oldStage.add(_teleportedCharacter, _fromX, _fromY, -1);
	}
    }
    
    public PlaywriteView createView() {
	View stageNameView;
	if (_stageExpression == null)
	    stageNameView = LineView.makeLabel(_newStageName);
	else if (_stageExpression instanceof VariableAlias)
	    stageNameView = ((VariableAlias) _stageExpression).createView();
	else
	    stageNameView = LineView.makeLabel(_stageExpression.toString());
	LineView view = new LineView(this, 8, "drteleport action fmt", null,
				     new View[] { stageNameView });
	return view;
    }
    
    public void summarize(Summary s) {
	Object stageName;
	if (_stageExpression == null)
	    stageName = _newStageName;
	else
	    stageName = _stageExpression;
	s.writeFormat("drteleport action fmt", null,
		      new Object[] { stageName });
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	DRTeleportAction newAction = (DRTeleportAction) map.get(this);
	if (newAction != null)
	    return newAction;
	if (_stageExpression == null)
	    newAction = new DRTeleportAction(_newStageName);
	else
	    newAction
		= new DRTeleportAction((VariableAlias)
				       _stageExpression.copy(map, fullCopy));
	map.put(this, newAction);
	return newAction;
    }
    
    public boolean wantsRepaint() {
	return true;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	if (_stageExpression != null
	    && _stageExpression.findReferenceTo(obj) != null)
	    return true;
	return false;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeObject(_stageExpression);
	out.writeObject(_newStageName);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	_stageExpression = (Expression) in.readObject();
	_newStageName = (String) in.readObject();
    }
    
    public String toString() {
	String result = null;
	try {
	    result = "<DRTeleport to " + (_stageExpression == null
					  ? "'" + _newStageName + "'"
					  : _stageExpression.toString()) + ">";
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
