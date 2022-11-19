/* AbstractVariableEditor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFieldOwner;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Expression;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.operators.Total;

abstract class AbstractVariableEditor extends PlaywriteView
    implements Enableable, Selectable, TextFieldOwner, ToolDestination,
	       ValueView.SetterGetter, Worldly
{
    static final int MIN_SIZE = 40;
    private VariableOwner owner;
    private Variable variable;
    private boolean _displayContent = true;
    private boolean _enabled = true;
    private PlaywriteTextField nameView;
    private View contentView;
    private Watcher varWatcher = null;
    private Watcher valueWatcher = null;
    private ValueView.SetterGetter _vsg;
    private ValueView _valueView;
    
    class ValueUpdater implements Watcher
    {
	public void update(Object variable, Object value) {
	    valueHasChanged();
	    updateContentView();
	    AbstractVariableEditor.this.setDragImageDirty();
	}
    }
    
    AbstractVariableEditor(VariableOwner owner, Variable variable,
			   ValueView.SetterGetter vsg) {
	this(owner, variable, vsg, true);
    }
    
    AbstractVariableEditor(VariableOwner owner, Variable variable,
			   ValueView.SetterGetter vsg,
			   boolean displayContent) {
	super(0, 0, 40, 40);
	boolean enabled = true;
	_vsg = vsg == null ? (ValueView.SetterGetter) this : vsg;
	this.owner = owner;
	this.variable = variable;
	_enabled = enabled;
	this.setBackgroundColor(Util.valueBGColor);
	this.setTransparent(false);
	_displayContent = displayContent;
	if (displayContent) {
	    this.setBorder(ScrapBorder.getRuleBorder());
	    this.allowTool(Tool.deleteTool, this);
	    this.allowTool(Tool.copyLoadTool, this);
	    valueWatcher = new ValueUpdater();
	    getVariable().addValueWatcher(getOwner(), valueWatcher);
	} else {
	    this.setMouseTransparency(true);
	    this.setBorder(ScrapBorder.getVariableAliasBorder());
	}
	varWatcher = new Watcher() {
	    public void update(Object variable_1_, Object info) {
		updateVariableView();
		AbstractVariableEditor.this.setDragImageDirty();
	    }
	};
	getVariable().addVariableWatcher(varWatcher);
	createNameView();
	createContentView(_vsg);
	this.layoutView(0, 0);
    }
    
    final Variable getVariable() {
	return variable;
    }
    
    final VariableOwner getOwner() {
	return owner;
    }
    
    public final World getWorld() {
	return owner.getWorld();
    }
    
    final PlaywriteTextField getNameView() {
	return nameView;
    }
    
    final void setNameView(PlaywriteTextField view) {
	nameView = view;
    }
    
    final View getContentView() {
	return contentView;
    }
    
    final void setContentView(View view) {
	contentView = view;
    }
    
    final boolean getDisplayContent() {
	return _displayContent;
    }
    
    public final Object getModelObject() {
	return this;
    }
    
    public final void setModelObject(Object obj) {
	throw new PlaywriteInternalError("must be itself!");
    }
    
    public boolean isActive() {
	return getOwner().getWorld().getState() != World.RUNNING;
    }
    
    final Point getDragPoint() {
	Point dragPoint = super.getDragPoint();
	dragPoint.x = 0;
	dragPoint.y = getNameView().height() / 2;
	return dragPoint;
    }
    
    public void setEnabled(boolean enabled) {
	_enabled = enabled;
	nameView
	    .setUserEditable(_enabled && !getVariable().isSystemVariable());
    }
    
    public boolean isEnabled() {
	return _enabled;
    }
    
    void createNameView() {
	PlaywriteTextField titleView
	    = new PlaywriteTextField(0, 0, this.width(), 20);
	String title = getVariable().getName();
	Size titleSize = Util.stringSize(Util.varTitleFont, title);
	if (nameView != null)
	    nameView.removeFromSuperview();
	setNameView(titleView);
	nameView.setOwner(this);
	nameView.setBorder(null);
	nameView.setTransparent(true);
	nameView.setFont(Util.varTitleFont);
	nameView.setJustification(1);
	nameView.setWrapsContents(false);
	nameView.setUserEditable(!getVariable().isSystemVariable()
				 && getDisplayContent() && isEnabled());
	nameView.setStringValue(title);
	nameView.setBackgroundColor(Util.valueBGColor);
	nameView.sizeToMinSize();
	Rect interior = this.interiorRect();
	nameView.setBounds(interior.x, interior.y, interior.width,
			   nameView.height());
	nameView.setVertResizeInstruction(4);
	nameView.setHorizResizeInstruction(2);
	this.addSubview(nameView);
    }
    
    abstract Tool[] getContentTools();
    
    abstract void createContentView(ValueView.SetterGetter settergetter);
    
    void updateNameView() {
	this.setDirty(true);
	nameView.setStringValue(getVariable().getName());
	this.sizeToMinSize();
	this.setDirty(true);
    }
    
    void valueHasChanged() {
	/* empty */
    }
    
    abstract void updateContentView();
    
    void updateVariableView() {
	updateNameView();
    }
    
    public void discard() {
	if (variable == null) {
	    if (Debug.lookup("debug.variable"))
		throw new PlaywriteInternalError("discard called twice");
	} else {
	    if (varWatcher != null) {
		getVariable().removeVariableWatcher(varWatcher);
		varWatcher = null;
	    }
	    if (valueWatcher != null) {
		getVariable().removeValueWatcher(owner, valueWatcher);
		valueWatcher = null;
	    }
	    super.discard();
	    owner = null;
	    nameView = null;
	    contentView = null;
	    variable = null;
	}
    }
    
    public Size minSize() {
	Size nameSize = nameView.minSize();
	Size contentSize = contentView.minSize();
	int width = (Math.max(nameSize.width, contentSize.width)
		     + this.border().widthMargin());
	int height = (nameSize.height + contentSize.height
		      + this.border().heightMargin());
	return new Size(width, height);
    }
    
    public void subviewDidResize(View subview) {
	if (this.subviews().containsIdentical(subview))
	    this.sizeToMinSize();
	super.subviewDidResize(subview);
    }
    
    public Object getValue() {
	return getVariable().getValue(getOwner());
    }
    
    public final boolean setValue(Object value) {
	Variable vari = getVariable();
	VariableOwner owner = getOwner();
	if (vari == null)
	    return false;
	Object constrainedValue;
	Object testValue;
	if (value instanceof Expression) {
	    if (value instanceof VariableAlias)
		constrainedValue = value;
	    else if (value instanceof Total) {
		ASSERT.isTrue(PlaywriteRoot.app().getEditorFor(value) == null);
		constrainedValue = value;
	    } else if (value instanceof GeneralizedCharacter)
		constrainedValue = value;
	    else {
		Debug.print(true, "unknown Expression type: ",
			    value.getClass().getName());
		return false;
	    }
	    testValue
		= vari.constrainedValue(owner, ((Expression) value).eval());
	} else if (value instanceof GCAlias) {
	    constrainedValue = ((GCAlias) value).findOriginal();
	    testValue = vari.constrainedValue(owner, ((GeneralizedCharacter)
						      constrainedValue)
							 .eval());
	} else {
	    constrainedValue = vari.constrainedValue(owner, value);
	    testValue = null;
	}
	if (testValue == Variable.ILLEGAL_VALUE
	    || constrainedValue == Variable.ILLEGAL_VALUE)
	    return false;
	vari.modifyValue(owner, constrainedValue);
	owner.getWorld().setModified(true);
	if (owner instanceof ModificationAware)
	    ((ModificationAware) owner).setModified(true);
	return true;
    }
    
    public View createViewFor(Object value) {
	return null;
    }
    
    public boolean viewDropped(ViewGlue droppedView) {
	Object value = null;
	if (droppedView instanceof AbstractVariableEditor) {
	    if (droppedView == this)
		return false;
	    Object state = getWorld().getState();
	    if (state == World.RECORDING || state == World.EDITING)
		value = _valueView.getViewsValue(droppedView);
	    else
		value = ((AbstractVariableEditor) droppedView).getValue();
	} else if (droppedView instanceof ViewGlue)
	    value = _valueView.getViewsValue(droppedView);
	else
	    return false;
	return setValue(value);
    }
    
    public final boolean acceptsView(ViewGlue draggedView) {
	Object model = draggedView.getModelObject();
	if (model instanceof Subtotal)
	    return model instanceof Total;
	return true;
    }
    
    public boolean isInteractive() {
	return isActive();
    }
    
    public final void setValueView(ValueView valueView) {
	_valueView = valueView;
    }
    
    public final ValueView getValueView() {
	return _valueView;
    }
    
    public void textEditingDidBegin(TextField tf) {
	this.unhilite();
	this.setDirty(true);
    }
    
    public void textWasModified(TextField tf) {
	this.setDirty(true);
	this.sizeToMinSize();
    }
    
    public boolean textEditingWillEnd(TextField tf, int endCondition,
				      boolean changed) {
	return true;
    }
    
    public void textEditingDidEnd(TextField tf, int endCondition,
				  boolean changed) {
	if (tf == nameView) {
	    if (isActive()) {
		getVariable().setName(tf.stringValue());
		owner.getWorld().setModified(true);
	    } else
		tf.setStringValue(getVariable().getName());
	} else
	    Debug.print(true, "Notified about textEdit for ", tf);
    }
    
    public boolean toolEntered(ToolSession session) {
	return isActive();
    }
    
    public boolean toolMoved(ToolSession session) {
	return true;
    }
    
    public void toolExited(ToolSession session) {
	/* empty */
    }
    
    public final boolean toolClicked(ToolSession session) {
	boolean result = true;
	Tool toolType = session.toolType();
	if (toolType == Tool.deleteTool) {
	    getWorld().setModified(true);
	    result = toolDelete(session);
	    return result;
	}
	if (toolType == Tool.copyLoadTool) {
	    if (getVariable().isSystemVariable())
		return false;
	    session.resetSession(this, Tool.copyPlaceTool);
	    return true;
	}
	return false;
    }
    
    public final void toolDragged(ToolSession session) {
	Tool toolType = session.toolType();
	if (toolType == Tool.deleteTool) {
	    getWorld().setModified(true);
	    toolDelete(session);
	}
    }
    
    public View viewForMouse(int x, int y) {
	View returnedView = null;
	if (isEnabled())
	    returnedView = super.viewForMouse(x, y);
	return returnedView;
    }
    
    public boolean mouseDown(MouseEvent event) {
	this.setDragPoint(new Point(event.x, event.y));
	if (super.mouseDown(event)) {
	    if (getDisplayContent() && getModelObject() instanceof Selectable)
		this.selectModel(event);
	    return true;
	}
	return false;
    }
    
    public void setBounds(int x, int y, int w, int h) {
	if (this.superview() != null)
	    this.superview().addDirtyRect(bounds);
	if (w < 40)
	    w = 40;
	super.setBounds(x, y, w, h);
	if (this.superview() != null)
	    this.superview().addDirtyRect(bounds);
    }
    
    public int cursorForPoint(int x, int y) {
	int cursor = super.cursorForPoint(x, y);
	if (cursor == 3)
	    return 3;
	return 12;
    }
    
    public Object copy() {
	Debug.print(true, this,
		    ": copy is not yet implemented for variable editors");
	return this;
    }
    
    public Object copy(World newWorld) {
	Debug.print(true, this,
		    ": copy is not yet implemented for variable editors");
	return this;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Debug.print(true, this,
		    ": copy is not yet implemented for variable editors");
	return this;
    }
    
    public boolean allowDelete() {
	Object state = getWorld().getState();
	if (state == World.EDITING || state == World.RECORDING)
	    return false;
	return variable.allowDelete();
    }
    
    public void delete() {
	getVariable().deleteUserVariable(getOwner());
    }
    
    public void undelete() {
	/* empty */
    }
    
    public void highlightForSelection() {
	this.hilite();
    }
    
    public void unhighlightForSelection() {
	this.unhilite();
	this.setDirty(true);
    }
    
    final boolean toolDelete(ToolSession session) {
	Selection.addToSelection(this, this.superview());
	Selection.deleteSelection();
	return true;
    }
}
