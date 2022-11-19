/* FVPutAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Hashtable;

final class FVPutAction extends RuleAction implements Debug.Constants
{
    private VariableOwner _variableOwner;
    private FingerVariable _fingerVariable;
    private Object _newValue;
    private Object _oldValue;
    
    FVPutAction(VariableOwner variableOwner, FingerVariable fingerVariable,
		Object newValue) {
	_fingerVariable = fingerVariable;
	_variableOwner = variableOwner;
	_newValue = newValue;
    }
    
    final void setTarget(GeneralizedCharacter gc) {
	/* empty */
    }
    
    final GeneralizedCharacter getTarget() {
	return null;
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	_oldValue = _fingerVariable.getValue(_variableOwner);
	_fingerVariable.setValue(_variableOwner, _newValue);
	return RuleAction.SUCCESS;
    }
    
    public void undo() {
	_fingerVariable.setValue(_variableOwner, _oldValue);
    }
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	/* empty */
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	return null;
    }
    
    public PlaywriteView createView() {
	PlaywriteView view = new LineView(8);
	COM.stagecast.ifc.netscape.application.Label label
	    = LineView.makeLabel(toString());
	view.addSubview(label);
	view.setModelObject(this);
	view.sizeToMinSize();
	return view;
    }
    
    public String toString() {
	return "<FVPutAction>";
    }
}
