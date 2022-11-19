/* PutCalcActionView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Popup;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.operators.Total;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class PutCalcActionView extends LineView
    implements Enableable, ResourceIDs.RuleActionIDs, Worldly
{
    private Popup _popup;
    private PutCalcAction _putCalcAction;
    private ValueView.SetterGetter _leftArgBoxVSG;
    private ValueView.SetterGetter _rightArgBoxVSG;
    private ValueView.SetterGetter _varAliasBoxVSG;
    private ValueView _leftArgValueView;
    private ValueView _rightArgValueView;
    private ValueView _varAliasValueView;
    
    PutCalcActionView(PutCalcAction action) {
	super(8);
	_putCalcAction = action;
	_leftArgBoxVSG = new ValueView.SetterGetter() {
	    ValueView _valueView = null;
	    
	    public World getWorld() {
		return PutCalcActionView.this.getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return PutCalcActionView.this.isEnabled();
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
		_putCalcAction.setLeftArgument(value);
		if (_putCalcAction.isValid())
		    RuleEditor.resetLater();
		return true;
	    }
	    
	    public Object getValue() {
		return _putCalcAction.getLeftArgument();
	    }
	    
	    public View createViewFor(Object value) {
		return null;
	    }
	};
	_rightArgBoxVSG = new ValueView.SetterGetter() {
	    ValueView _valueView = null;
	    
	    public World getWorld() {
		return PutCalcActionView.this.getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return PutCalcActionView.this.isEnabled();
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
		_putCalcAction.setRightArgument(value);
		if (_putCalcAction.isValid())
		    RuleEditor.resetLater();
		return true;
	    }
	    
	    public Object getValue() {
		return _putCalcAction.getRightArgument();
	    }
	    
	    public View createViewFor(Object value) {
		return null;
	    }
	};
	_varAliasBoxVSG = new ValueView.SetterGetter() {
	    ValueView _valueView = null;
	    
	    public World getWorld() {
		return PutCalcActionView.this.getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return PutCalcActionView.this.isEnabled();
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
		    _putCalcAction.setVariableAlias((VariableAlias) value);
		else if (value == null)
		    _putCalcAction.setVariableAlias(null);
		else
		    return false;
		if (_putCalcAction.isValid())
		    RuleEditor.resetLater();
		return true;
	    }
	    
	    public Object getValue() {
		return _putCalcAction.getVariableAlias();
	    }
	    
	    public View createViewFor(Object value) {
		return null;
	    }
	};
	_popup = new GreenPopup(true);
	int n = _putCalcAction.getNumberOfRegularTypes();
	for (int i = 0; i < n; i++) {
	    String name = _putCalcAction.getTypeName(i);
	    _popup.addItem(name + "  ", name);
	    _popup.itemAt(i).setFont(Util.ruleFont);
	}
	int nr = _putCalcAction.getNumberOfTypes();
	for (int i = n; i < nr; i++) {
	    String name = _putCalcAction.getTypeName(i);
	    _popup.addAltItem(name + "  ", name);
	    _popup.itemAt(i).setFont(Util.ruleFont);
	}
	int type = _putCalcAction.getType();
	_popup.selectItemAt(type);
	_popup.setTarget(this);
	_popup.sizeToMinSize();
	_leftArgValueView = new ValueView(_leftArgBoxVSG);
	_rightArgValueView = new ValueView(_rightArgBoxVSG);
	_varAliasValueView = new ValueView(_varAliasBoxVSG);
	this.addViews("put calc action fmt", null,
		      new View[] { _leftArgValueView, _popup,
				   _rightArgValueView, _varAliasValueView });
	this.connectAndFinish(_putCalcAction);
    }
    
    public final void setEnabled(boolean enabled) {
	this.disableDrawing();
	_popup.setEnabled(enabled);
	_leftArgValueView.setEnabled(enabled);
	_rightArgValueView.setEnabled(enabled);
	_varAliasValueView.setEnabled(enabled);
	super.setEnabled(enabled);
	this.reenableDrawing();
    }
    
    public final World getWorld() {
	return _putCalcAction.getWorld();
    }
    
    public void performCommand(String command, Object data) {
	if (data == _popup) {
	    _putCalcAction.changeToType(_popup.selectedIndex());
	    if (_putCalcAction.isValid())
		RuleEditor.getRuleEditor().resetRecording();
	    _popup.sizeToMinSize();
	}
    }
}
