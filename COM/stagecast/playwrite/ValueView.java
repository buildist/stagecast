/* ValueView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.LineBorder;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFieldOwner;
import COM.stagecast.ifc.netscape.application.TextFilter;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class ValueView extends PlaywriteView
    implements DragDestination, ExtendedDragSource, ResourceIDs.DialogIDs,
	       TextFieldOwner, ToolDestination, Enableable
{
    private static final Border _defaultBorder
	= new LineBorder(Color.darkGray);
    private static final int CONTENT_BORDER = 2;
    private static final int CONTENT_BORDER_WIDTH = 4;
    private SetterGetter _setterGetter;
    private View _contentView;
    private PlaywriteTextField _textField;
    private boolean _fixedBounds;
    private Border _enabledBorder;
    private boolean _borderedMode = true;
    
    public static interface SetterGetter extends Worldly
    {
	public void setValueView(ValueView valueview);
	
	public boolean isInteractive();
	
	public boolean acceptsView(ViewGlue viewglue);
	
	public boolean viewDropped(ViewGlue viewglue);
	
	public boolean setValue(Object object);
	
	public Object getValue();
	
	public View createViewFor(Object object);
    }
    
    public abstract static class WorldSetterGetter implements SetterGetter
    {
	private ValueView _myValueView;
	
	public World getWorld() {
	    return ((Worldly) _myValueView.window()).getWorld();
	}
	
	public void setValueView(ValueView valueView) {
	    _myValueView = valueView;
	}
	
	public abstract boolean viewDropped(ViewGlue viewglue);
	
	public abstract boolean isInteractive();
	
	public abstract boolean acceptsView(ViewGlue viewglue);
	
	public abstract boolean setValue(Object object);
	
	public abstract Object getValue();
	
	public abstract View createViewFor(Object object);
    }
    
    public abstract static class DisplayOnlySetterGetter
	extends WorldSetterGetter
    {
	public boolean isInteractive() {
	    return false;
	}
	
	public final boolean acceptsView(ViewGlue draggedView) {
	    return false;
	}
	
	public final boolean viewDropped(ViewGlue droppedView) {
	    return false;
	}
	
	public final boolean setValue(Object value) {
	    return false;
	}
	
	public View createViewFor(Object value) {
	    return null;
	}
    }
    
    public ValueView(SetterGetter valueSetterGetter) {
	this(valueSetterGetter, true, true,
	     new Tool[] { Tool.deleteTool, Tool.copyLoadTool,
			  Tool.copyPlaceTool });
    }
    
    public ValueView(SetterGetter valueSetterGetter, boolean dragIn,
		     boolean dragOut, Tool[] toolList) {
	_setterGetter = valueSetterGetter;
	_setterGetter.setValueView(this);
	_enabledBorder = _defaultBorder;
	this.setBorder(_enabledBorder);
	setBackgroundColor(Util.testColor);
	this.setTransparent(true);
	if (dragIn)
	    this.allowDragInto(Object.class, this);
	if (dragOut)
	    this.allowDragOutOf(Object.class, this);
	if (toolList != null) {
	    for (int i = 0; i < toolList.length; i++)
		this.allowTool(toolList[i], this);
	}
	_textField = new PlaywriteTextField(0, 0, 10, Util.valueFontHeight);
	_textField.setOurMinSize(Util.valueFontHeight, Util.valueFontHeight);
	_textField.setOurMaxSize(120, 0);
	_textField.setStringValue("ERROR!");
	_textField.setBorder(null);
	_textField.setFont(Util.valueFont);
	_textField.setTextColor(Util.valueColor);
	_textField.setJustification(1);
	_textField.setUserEditable(isEnabled());
	_textField.setOwner(this);
	_textField.setHorizResizeInstruction(32);
	_textField.setTransparent(true);
	_contentView = _textField;
	this.addSubview(_contentView);
	updateView();
    }
    
    public final void setBackgroundColor(Color color) {
	super.setBackgroundColor(color);
	this.setTransparent(false);
    }
    
    public final void setTextColor(Color color) {
	_textField.setTextColor(color);
    }
    
    public final void setTextFont(Font font) {
	_textField.setFont(font);
    }
    
    public final void setTextJustification(int justification) {
	ASSERT.isTrue(justification == 1 || justification == 0
		      || justification == 2);
	_textField.setJustification(justification);
    }
    
    public final void setTextFilter(TextFilter filter) {
	_textField.setFilter(filter);
    }
    
    public final void setTextFieldMinSize(int width, int height) {
	_textField.setOurMinSize(width, height);
    }
    
    public final boolean isEnabled() {
	return _setterGetter.isInteractive();
    }
    
    public void setEnabled(boolean enabled) {
	if (enabled)
	    this.setBorder(_enabledBorder);
	else
	    this.setBorder(null);
	_textField.setUserEditable(enabled);
	if (_contentView != null && _contentView instanceof Enableable)
	    ((Enableable) _contentView).setEnabled(enabled);
    }
    
    public void setEnabledBorder(Border border) {
	_enabledBorder = border;
	if (isEnabled())
	    this.setBorder(_enabledBorder);
    }
    
    public void discard() {
	super.discard();
	_setterGetter.setValueView(null);
	_textField = null;
	_contentView = null;
    }
    
    public void setBorderedMode(boolean flag) {
	_borderedMode = flag;
    }
    
    public void layoutView(int dx, int dy) {
	if (_textField != null)
	    _textField.sizeToMinSize();
	if (_borderedMode) {
	    if (_contentView != null)
		_contentView.moveTo(this.border().leftMargin() + 2,
				    this.border().topMargin() + 2);
	} else
	    _contentView.moveTo(0, 0);
	super.layoutView(dx, dy);
	this.setDirty();
    }
    
    public void sizeToMinSize() {
	if (!_fixedBounds) {
	    int cw = _contentView.width();
	    int ch = _contentView.height();
	    if (_borderedMode) {
		int width = cw + this.border().widthMargin() + 4;
		int height = ch + this.border().heightMargin() + 4;
		this.setMinSize(width, height);
		this.sizeTo(width, height);
	    } else {
		this.setMinSize(cw, ch);
		this.sizeTo(cw, ch);
	    }
	}
    }
    
    public void setFixedBounds(int x, int y, int width, int height) {
	_fixedBounds = true;
	super.setBounds(x, y, width, height);
	this.setMinSize(width, height);
	int w = width;
	int h = height;
	if (_borderedMode) {
	    w -= this.border().widthMargin();
	    h -= this.border().heightMargin();
	    _textField.moveTo(this.border().leftMargin(),
			      this.border().topMargin());
	}
	_textField.setMinSize(w, h);
    }
    
    public void subviewDidResize(View subview) {
	sizeToMinSize();
	layoutView(0, 0);
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (isEnabled() && _contentView == _textField) {
	    _textField.setUserEditable(true);
	    _textField.startFocus();
	}
	return super.mouseDown(event);
    }
    
    public void drawHilite(Graphics g) {
	g.setColor(Color.black);
	g.drawRect(0, 0, this.width(), this.height());
	g.drawRect(1, 1, this.width() - 2, this.height() - 2);
    }
    
    public final void updateView() {
	this.setDirty(true);
	Object newValue = _setterGetter.getValue();
	View newView = _setterGetter.createViewFor(newValue);
	if (newView == null) {
	    if (newValue instanceof FirstClassValue) {
		newView = ((FirstClassValue) newValue).createIconView();
		if (newView instanceof Icon)
		    ((Icon) newView).setSelectsModel(false);
	    }
	    if (newValue instanceof StorableToken) {
		_textField.setUserEditable(false);
		_textField
		    .setStringValue(((StoredToken) newValue).getLocalName());
		_textField.sizeToMinSize();
		newView = _textField;
	    }
	    if (newView == null) {
		String newString;
		if (newValue instanceof Number)
		    newString = Resource.formatNumber((Number) newValue);
		else if (newValue == null)
		    newString = "";
		else
		    newString = newValue.toString();
		_textField.setUserEditable(isEnabled());
		_textField.setStringValue(newString);
		_textField.sizeToMinSize();
		newView = _textField;
	    }
	}
	if (_contentView != newView) {
	    _contentView.removeFromSuperview();
	    if (_contentView instanceof ViewGlue && _contentView != _textField)
		((ViewGlue) _contentView).discard();
	    newView.setHorizResizeInstruction(32);
	    this.addSubview(newView);
	    _contentView = newView;
	}
	sizeToMinSize();
	layoutView(0, 0);
    }
    
    public Object getViewsValue(ViewGlue view) {
	VariableAlias variableAlias = getViewsVariableAlias(view);
	if (variableAlias != null)
	    return variableAlias;
	Object o = view.getModelObject();
	if (o == null) {
	    if (view instanceof TextField)
		throw new PlaywriteInternalError("this code is not useless");
	    return null;
	}
	if (o instanceof GCAlias)
	    o = ((GCAlias) o).findOriginal();
	else if (o instanceof Subtotal)
	    o = Util.copySubtotal((Subtotal) o);
	return o;
    }
    
    public VariableAlias getViewsVariableAlias(ViewGlue view) {
	if (view instanceof AbstractVariableEditor) {
	    if (RuleEditor.isRecordingOrEditing()) {
		AbstractVariableEditor variableEditor
		    = (AbstractVariableEditor) view;
		Variable variable = variableEditor.getVariable();
		return variable.createVariableAlias(variableEditor.getOwner());
	    }
	    throw new PlaywriteInternalError
		      ("illegal drop of AbstractVariableEditor");
	}
	if (view.getModelObject() instanceof VariableAlias)
	    return (VariableAlias) view.getModelObject();
	return null;
    }
    
    public void textEditingDidBegin(TextField tf) {
	if (!_fixedBounds)
	    tf.setMinSize(-1, -1);
    }
    
    public void textWasModified(TextField tf) {
	tf.setDirty(true);
	tf.sizeToMinSize();
    }
    
    public boolean textEditingWillEnd(TextField tf, int endCondition,
				      boolean changed) {
	return true;
    }
    
    public void textEditingDidEnd(TextField tf, int endCondition,
				  boolean changed) {
	if (tf == _textField) {
	    if (changed) {
		if (!isEnabled())
		    updateView();
		else {
		    String valueString = _textField.stringValue();
		    boolean accepted = false;
		    Object newValue = Resource.parseNumberString(valueString);
		    accepted = _setterGetter.setValue(newValue);
		    if (!accepted)
			updateView();
		    else {
			sizeToMinSize();
			layoutView(0, 0);
		    }
		}
	    }
	}
    }
    
    public boolean prepareToDrag(Object data) {
	if (!isEnabled())
	    return false;
	return true;
    }
    
    public void dragWasAccepted(DragSession session) {
	/* empty */
    }
    
    public boolean dragWasRejected(DragSession session) {
	return true;
    }
    
    public View sourceView(DragSession session) {
	return _contentView;
    }
    
    public DragDestination acceptsDrag(DragSession ds, int x, int y) {
	if (!isEnabled())
	    return null;
	return super.acceptsDrag(ds, x, y);
    }
    
    public boolean dragDropped(DragSession session) {
	Object draggee = session.data();
	boolean acceptable = false;
	this.unhilite();
	if (draggee instanceof ViewGlue) {
	    ViewGlue droppedView = (ViewGlue) draggee;
	    if (droppedView instanceof MultiDragView)
		return false;
	    acceptable = _setterGetter.acceptsView(droppedView);
	    if (acceptable)
		acceptable = _setterGetter.viewDropped(droppedView);
	}
	if (!acceptable)
	    return false;
	updateView();
	return true;
    }
    
    public boolean dragEntered(DragSession session) {
	if (!isEnabled())
	    return false;
	return acceptsObject(session);
    }
    
    public void dragExited(DragSession session) {
	this.unhilite();
    }
    
    public boolean dragMoved(DragSession session) {
	if (!isEnabled())
	    return false;
	return acceptsObject(session);
    }
    
    final boolean acceptsObject(DragSession session) {
	if (!isEnabled())
	    return false;
	if (session.data() instanceof ViewGlue) {
	    ViewGlue data = (ViewGlue) session.data();
	    Object mo = data.getModelObject();
	    boolean accepts = true;
	    if (mo instanceof Worldly)
		accepts
		    = (((Worldly) mo).getWorld() == _setterGetter.getWorld()
		       || ((Worldly) mo).getWorld() == null);
	    else if (mo instanceof RuleTest || mo instanceof RuleAction
		     || !_setterGetter.acceptsView(data))
		accepts = false;
	    if (accepts)
		this.hilite();
	    else
		this.unhilite();
	    return accepts;
	}
	return false;
    }
    
    public ToolDestination acceptsTool(ToolSession session, int x, int y) {
	ToolDestination toolDestination = super.acceptsTool(session, x, y);
	if (toolDestination != null) {
	    Tool tool = session.toolType();
	    if ((tool == Tool.copyLoadTool || tool == Tool.deleteTool)
		&& _setterGetter.getValue() == null)
		return null;
	    if (tool == Tool.copyPlaceTool && session.data() == null)
		return null;
	}
	return toolDestination;
    }
    
    public boolean toolEntered(ToolSession session) {
	return isEnabled();
    }
    
    public boolean toolMoved(ToolSession session) {
	return isEnabled();
    }
    
    boolean deleteTool() {
	if (this.supportsTool(Tool.deleteTool)) {
	    boolean accepted = _setterGetter.setValue(null);
	    if (accepted) {
		updateView();
		return true;
	    }
	    return false;
	}
	return false;
    }
    
    public boolean toolClicked(ToolSession session) {
	boolean accepted = false;
	if (!isEnabled())
	    return false;
	Tool toolType = session.toolType();
	if (toolType == Tool.deleteTool)
	    return deleteTool();
	if (toolType == Tool.copyLoadTool) {
	    if (_setterGetter.getValue() == null)
		return false;
	    COM.stagecast.ifc.netscape.application.Image dragImage
		= Util.makeBitmapFromView(_contentView);
	    session.resetSession(dragImage, Tool.copyPlaceTool,
				 _setterGetter.getValue());
	    return true;
	}
	if (toolType == Tool.copyPlaceTool) {
	    if (session.data() instanceof ViewGlue) {
		Object mo = ((ViewGlue) session.data()).getModelObject();
		if (mo instanceof Worldly
		    && ((Worldly) mo).getWorld() != _setterGetter.getWorld())
		    return false;
	    }
	    accepted = _setterGetter.setValue(session.data());
	    if (accepted) {
		updateView();
		return true;
	    }
	    return false;
	}
	return false;
    }
}
