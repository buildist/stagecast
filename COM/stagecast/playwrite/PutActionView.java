/* PutActionView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.Popup;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.operators.Total;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class PutActionView extends LineView
    implements Enableable, Worldly, ResourceIDs.RuleActionIDs
{
    private Label prepositionLabel;
    private Popup popup;
    private PutAction putAction;
    private ValueView.SetterGetter leftBoxVSG;
    private ValueView.SetterGetter rightBoxVSG;
    private ValueView leftValueView;
    private ValueView rightValueView;
    
    PutActionView(PutAction action) {
	super(8);
	putAction = action;
	leftBoxVSG = new ValueView.SetterGetter() {
	    ValueView _valueView = null;
	    
	    public World getWorld() {
		return PutActionView.this.getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return PutActionView.this.isEnabled();
	    }
	    
	    public boolean acceptsView(ViewGlue draggedView) {
		Object object = draggedView.getModelObject();
		if (object instanceof Subtotal)
		    return object instanceof Total;
		return true;
	    }
	    
	    public boolean viewDropped(ViewGlue droppedView) {
		return setValue(_valueView.getViewsValue(droppedView));
	    }
	    
	    public boolean setValue(Object value) {
		putAction.setLeftSide(value);
		if (putAction.isValid())
		    RuleEditor.resetLater();
		return true;
	    }
	    
	    public Object getValue() {
		return putAction.getLeftSide();
	    }
	    
	    public View createViewFor(Object value) {
		return null;
	    }
	};
	rightBoxVSG = new ValueView.SetterGetter() {
	    ValueView _valueView = null;
	    
	    public World getWorld() {
		return PutActionView.this.getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return PutActionView.this.isEnabled();
	    }
	    
	    public boolean acceptsView(ViewGlue draggedView) {
		return _valueView.getViewsVariableAlias(draggedView) != null;
	    }
	    
	    public boolean viewDropped(ViewGlue droppedView) {
		VariableAlias variableAlias
		    = _valueView.getViewsVariableAlias(droppedView);
		if (variableAlias != null)
		    return setValue(variableAlias);
		return false;
	    }
	    
	    public boolean setValue(Object value) {
		if (value instanceof VariableAlias)
		    putAction.setVariableAlias((VariableAlias) value);
		else if (value == null)
		    putAction.setVariableAlias(null);
		else
		    return false;
		if (putAction.isValid())
		    RuleEditor.resetLater();
		return true;
	    }
	    
	    public Object getValue() {
		return putAction.getVariableAlias();
	    }
	    
	    public View createViewFor(Object value) {
		return null;
	    }
	};
	popup = new GreenPopup(true);
	int n = putAction.getNumberOfTypes();
	for (int i = 0; i < n; i++) {
	    String name = putAction.getTypeName(i);
	    popup.addItem(name + "  ", name);
	    popup.itemAt(i).setFont(Util.ruleFont);
	}
	int type = putAction.getType();
	popup.selectItemAt(type);
	prepositionLabel = LineView.makeLabel(putAction.getTypePrep(type));
	popup.setTarget(this);
	leftValueView = new ValueView(leftBoxVSG);
	rightValueView = new ValueView(rightBoxVSG);
	View[] viewArgs
	    = { popup, leftValueView, prepositionLabel, rightValueView };
	this.addViews("put action fmt", null, viewArgs);
	this.connectAndFinish(putAction);
    }
    
    public final void setEnabled(boolean enabled) {
	this.disableDrawing();
	popup.setEnabled(enabled);
	leftValueView.setEnabled(enabled);
	rightValueView.setEnabled(enabled);
	super.setEnabled(enabled);
	this.reenableDrawing();
    }
    
    public final World getWorld() {
	return putAction.getWorld();
    }
    
    public void performCommand(String command, Object data) {
	if (data == popup) {
	    putAction.changeToType(popup.selectedIndex());
	    prepositionLabel
		.setTitle(putAction.getTypePrep(putAction.getType()));
	    prepositionLabel.sizeToMinSize();
	    if (putAction.isValid())
		RuleEditor.getRuleEditor().resetRecording();
	}
    }
}
