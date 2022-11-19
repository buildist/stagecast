/* SwitchStageActionView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class SwitchStageActionView extends LineView
    implements Enableable, ResourceIDs.RuleActionIDs
{
    private ValueView _stageValueView;
    
    SwitchStageActionView(final SwitchStageAction action) {
	super(8);
	ValueView.SetterGetter stageVSG = new ValueView.SetterGetter() {
	    ValueView valueView = null;
	    
	    public World getWorld() {
		return action.getWorld();
	    }
	    
	    public void setValueView(ValueView view) {
		valueView = view;
	    }
	    
	    public boolean isInteractive() {
		return SwitchStageActionView.this.isEnabled();
	    }
	    
	    public boolean acceptsView(ViewGlue draggedView) {
		if (draggedView instanceof VariableEditor) {
		    Object object = valueView.getViewsValue(draggedView);
		    return true;
		}
		Object object = draggedView.getModelObject();
		return action.getStageFrom(object) != null;
	    }
	    
	    public boolean viewDropped(ViewGlue droppedView) {
		return setValue(valueView.getViewsValue(droppedView));
	    }
	    
	    public boolean setValue(Object value) {
		action.setEditStage(value);
		if (action.isEditValid())
		    RuleEditor.resetLater();
		return true;
	    }
	    
	    public Object getValue() {
		return action.getEditStage();
	    }
	    
	    public View createViewFor(Object value) {
		return null;
	    }
	};
	_stageValueView = new ValueView(stageVSG);
	this.addViews("switch stage action fmt", null,
		      new View[] { _stageValueView });
	this.connectAndFinish(action);
    }
    
    public final void setEnabled(boolean enabled) {
	this.disableDrawing();
	_stageValueView.setEnabled(enabled);
	super.setEnabled(enabled);
	this.reenableDrawing();
    }
}
