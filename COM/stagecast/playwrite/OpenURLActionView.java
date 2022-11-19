/* OpenURLActionView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.operators.Total;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class OpenURLActionView extends LineView
    implements Enableable, Worldly, ResourceIDs.RuleActionIDs
{
    private static String[] modes = { "_self", "_parent", "_top", "_blank" };
    private GreenPopup modePopup;
    private OpenURLAction openURLAction;
    private ValueView.SetterGetter urlBoxVSG;
    private ValueView urlValueView;
    
    OpenURLActionView(OpenURLAction action) {
	super(8);
	openURLAction = action;
	urlBoxVSG = new ValueView.WorldSetterGetter() {
	    ValueView _valueView = null;
	    
	    public World getWorld() {
		return ((Worldly) _valueView.window()).getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return OpenURLActionView.this.isEnabled();
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
		openURLAction.setURLObject(value);
		return true;
	    }
	    
	    public Object getValue() {
		return openURLAction.getURLObject();
	    }
	    
	    public View createViewFor(Object value) {
		return null;
	    }
	};
	modePopup = new GreenPopup(true);
	modePopup.setFont(Util.ruleFont);
	int n = modes.length;
	for (int i = 0; i < n; i++) {
	    String id = modes[i];
	    String name = Resource.getText(id);
	    modePopup.addItem(name + "  ", id);
	}
	String currentModeID = openURLAction.getMode();
	modePopup.selectItemAt(translateMode(currentModeID));
	modePopup.setTarget(this);
	urlValueView = new ValueView(urlBoxVSG);
	View[] viewArgs = { urlValueView, modePopup };
	this.addViews("open url action fmt", null, viewArgs);
	this.connectAndFinish(openURLAction);
    }
    
    private int translateMode(String modeIDString) {
	for (int i = 0; i < modes.length; i++) {
	    if (modeIDString == modes[i])
		return i;
	}
	Debug.print(true, "unknown openURLMode:", modeIDString);
	ASSERT.isTrue(false);
	return -1;
    }
    
    public final void setEnabled(boolean enabled) {
	this.disableDrawing();
	modePopup.setEnabled(enabled);
	urlValueView.setEnabled(enabled);
	super.setEnabled(enabled);
	this.reenableDrawing();
    }
    
    public final World getWorld() {
	return openURLAction.getWorld();
    }
    
    public void performCommand(String command, Object data) {
	if (data == modePopup)
	    openURLAction.setMode(command);
	else
	    super.performCommand(command, data);
    }
}
