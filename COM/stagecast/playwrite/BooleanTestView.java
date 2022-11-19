/* BooleanTestView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Popup;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.operators.Expression;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.OperationManager;
import COM.stagecast.operators.OperationType;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.operators.Total;

class BooleanTestView extends LineView implements Worldly, Enableable
{
    private Popup popup;
    private BooleanTest booleanTest;
    private OperationManager op = null;
    private Object test = null;
    private Vector types = null;
    private ValueView.SetterGetter leftBoxVSG;
    private ValueView.SetterGetter rightBoxVSG;
    private ValueView leftView;
    private ValueView rightView;
    
    BooleanTestView(BooleanTest bt) {
	super(8);
	booleanTest = bt;
	test = booleanTest.getTest();
	if (test == null)
	    throw new PlaywriteInternalError("no test in booleanTest!");
	if (test instanceof OperationManager
	    && ((OperationManager) test).isBooleanOp()) {
	    op = (OperationManager) test;
	    createVSGs();
	    types = new Vector(10);
	    Enumeration e = Op.getBooleanOperationTypes();
	    while (e.hasMoreElements())
		types.addElement(e.nextElement());
	    popup = new GreenPopup(true);
	    int n = types.size();
	    int ourType = -1;
	    for (int i = 0; i < n; i++) {
		OperationType operationType
		    = (OperationType) types.elementAt(i);
		String name = operationType.getLocalName();
		popup.addItem(name + " ", name);
		if (op.isSameOpAs(operationType))
		    ourType = i;
		popup.itemAt(i).setFont(Util.ruleFont);
	    }
	    if (ourType == -1)
		throw new PlaywriteInternalError("couldn't find type");
	    popup.selectItemAt(ourType);
	    popup.setTarget(this);
	    popup.sizeToMinSize();
	    leftView = new ValueView(leftBoxVSG);
	    this.addSubview(leftView);
	    this.addSubview(popup);
	    rightView = new ValueView(rightBoxVSG);
	    this.addSubview(rightView);
	} else if (test instanceof Expression)
	    this.addSubview(((Expression) test).createIconView());
	else
	    this.addSubview(LineView.makeLabel(test.toString()));
	this.setModelObject(booleanTest);
	this.sizeToMinSize();
    }
    
    private void createVSGs() {
	leftBoxVSG = new ValueView.SetterGetter() {
	    private ValueView _valueView = null;
	    
	    public World getWorld() {
		return BooleanTestView.this.getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return BooleanTestView.this.isEnabled();
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
		op.setLeftSide(value);
		leftView.updateView();
		if (op.getRightSide() == null
		    && value instanceof VariableAlias) {
		    op.setRightSide(((VariableAlias) value).eval());
		    rightView.updateView();
		}
		BooleanTestView.this.layoutView(0, 0);
		return true;
	    }
	    
	    public Object getValue() {
		return op.getLeftSide();
	    }
	    
	    public View createViewFor(Object value) {
		if (value instanceof VariableAlias)
		    return ((VariableAlias) value).createBoundView();
		return null;
	    }
	};
	rightBoxVSG = new ValueView.SetterGetter() {
	    private ValueView _valueView = null;
	    
	    public World getWorld() {
		return BooleanTestView.this.getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return BooleanTestView.this.isEnabled();
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
		op.setRightSide(value);
		rightView.updateView();
		if (op.getLeftSide() == null
		    && value instanceof VariableAlias) {
		    op.setLeftSide(((VariableAlias) value).eval());
		    leftView.updateView();
		}
		BooleanTestView.this.layoutView(0, 0);
		return true;
	    }
	    
	    public Object getValue() {
		return op.getRightSide();
	    }
	    
	    public View createViewFor(Object value) {
		if (value instanceof VariableAlias)
		    return ((VariableAlias) value).createBoundView();
		return null;
	    }
	};
    }
    
    public final void setEnabled(boolean enabled) {
	this.disableDrawing();
	popup.setEnabled(enabled);
	leftView.setEnabled(enabled);
	rightView.setEnabled(enabled);
	super.setEnabled(enabled);
	this.reenableDrawing();
    }
    
    public final World getWorld() {
	return booleanTest.getWorld();
    }
    
    public final void updateValueViews() {
	leftView.updateView();
	rightView.updateView();
    }
    
    public void performCommand(String command, Object data) {
	if (data == popup)
	    op.changeToOperation((OperationType)
				 types.elementAt(popup.selectedIndex()));
    }
}
