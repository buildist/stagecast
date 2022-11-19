/* EnumeratedVariableEditor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.ListItem;
import COM.stagecast.ifc.netscape.application.ListView;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;

class EnumeratedVariableEditor extends VariableEditor
    implements Debug.Constants
{
    static String SETVALUE = "SET_VALUE";
    private static final Tool[] ENUM_CONTENT_TOOLS = { Tool.copyLoadTool };
    private PlaywritePopup _popupMenu;
    private boolean _initializing = true;
    private boolean _isDynamic;
    private Rect _nameViewBounds;
    
    static void displayPopupFor(EnumeratedVariable variable,
				VariableOwner owner, PlaywriteView view,
				Point pt) {
	if (!(view.superview() instanceof ModalView))
	    Selection.hideModalView();
	Selection.unselectAll();
	EnumeratedVariableEditor dynamicPopup
	    = ((EnumeratedVariableEditor)
	       variable.makeVariableEditor(owner, null));
	dynamicPopup.makeDynamic();
	ListView popupList = dynamicPopup.getPopupMenu().getPopupList();
	final Target dynamicTarget = popupList.target();
	popupList.setTarget(new Target() {
	    public void performCommand(String command, Object data) {
		dynamicTarget.performCommand(command, data);
	    }
	});
	dynamicPopup.getPopupMenu()
	    .mouseDown(new MouseEvent(0L, -1, pt.x, pt.y, 0), view);
    }
    
    EnumeratedVariableEditor(VariableOwner owner, EnumeratedVariable variable,
			     ValueView.SetterGetter vsg) {
	super(owner, (Variable) variable, vsg);
	setEnabled(this.isEnabled());
	_initializing = false;
    }
    
    EnumeratedVariable getEnumeratedVariable() {
	return (EnumeratedVariable) this.getVariable();
    }
    
    final PlaywritePopup getPopupMenu() {
	return _popupMenu;
    }
    
    private final void makeDynamic() {
	_isDynamic = true;
    }
    
    Tool[] getContentTools() {
	return ENUM_CONTENT_TOOLS;
    }
    
    public void setEnabled(boolean enabled) {
	super.setEnabled(enabled);
	if (enabled) {
	    if (_popupMenu == null) {
		PlaywriteTextField nameView = this.getNameView();
		_popupMenu = new PlaywritePopup(Resource.getImage
						("VariablePopup")) {
		    public boolean mouseDown(MouseEvent event) {
			if (EnumeratedVariableEditor.this.isActive())
			    return super.mouseDown(event);
			return false;
		    }
		};
		_popupMenu.setPopupList(buildPopupList(_popupMenu));
		this.sizeToMinSize();
		Rect innerBounds = this.interiorRect();
		_nameViewBounds = nameView.bounds();
		nameView.setBounds(nameView.x(), nameView.y(),
				   innerBounds.width - _popupMenu.width(),
				   nameView.height());
		_popupMenu.moveTo(nameView.x() + nameView.width(),
				  innerBounds.y);
		_popupMenu.setVertResizeInstruction(4);
		_popupMenu.setHorizResizeInstruction(1);
		this.addSubview(_popupMenu);
	    }
	} else if (_popupMenu != null) {
	    ASSERT.isNotNull(_nameViewBounds);
	    _popupMenu.removeFromSuperview();
	    _popupMenu = null;
	    this.getNameView().setBounds(_nameViewBounds);
	    this.sizeToMinSize();
	}
    }
    
    public Size minSize() {
	Size basicSize = super.minSize();
	View nameView = this.getNameView();
	Size nameSize = nameView.minSize();
	if (_popupMenu != null)
	    basicSize.width = Math.max((nameSize.width + _popupMenu.width()
					+ this.border().widthMargin()),
				       basicSize.width);
	return basicSize;
    }
    
    public void subviewDidResize(View subview) {
	if (!_initializing)
	    super.subviewDidResize(subview);
    }
    
    private ListView buildPopupList(PlaywritePopup popupMenu) {
	ListView listView = PlaywritePopup.makePopupList(this, "PS");
	Enumeration values
	    = getEnumeratedVariable().legalValues(this.getOwner());
	Object currentValue = this.getValue();
	while (values.hasMoreElements()) {
	    Object value = values.nextElement();
	    ListItem item = itemForValue(popupMenu, value);
	    listView.addItem(item);
	    listView.setRowHeight(0);
	    if (value == currentValue)
		listView.selectItem(item);
	}
	values = getEnumeratedVariable().alternateValues(this.getOwner());
	if (values.hasMoreElements()) {
	    popupMenu.clearAltItems();
	    while (values.hasMoreElements()) {
		Object value = values.nextElement();
		popupMenu.addAltItem(itemForValue(popupMenu, value));
	    }
	}
	listView.sizeToMinSize();
	listView.sizeTo(listView.minItemWidth(), listView.height());
	return listView;
    }
    
    private ListItem itemForValue(PlaywritePopup popupMenu, Object value) {
	ListItem item = null;
	if (value instanceof IconModel)
	    item = popupMenu.new PopupIcon((IconModel) value);
	else if (value instanceof StorableToken)
	    item = PlaywritePopup.makePopupItem(((StoredToken) value)
						    .getLocalName(),
						null, value);
	else {
	    item = PlaywritePopup.makePopupItem(getEnumeratedVariable()
						    .nameOf(value),
						null, value);
	    if (!(value instanceof String))
		Debug.print
		    (true,
		     "popup item should implement IconModel or StoredToken for ",
		     value.getClass());
	}
	return item;
    }
    
    void updateVariableView() {
	super.updateVariableView();
	if (_popupMenu != null)
	    _popupMenu.setPopupList(buildPopupList(_popupMenu));
    }
    
    void valueHasChanged() {
	super.valueHasChanged();
	if (_popupMenu != null)
	    _popupMenu.setPopupList(buildPopupList(_popupMenu));
    }
    
    public void discard() {
	super.discard();
	if (_popupMenu != null)
	    _popupMenu.discard();
	_popupMenu = null;
    }
    
    public void performCommand(String command, Object data) {
	if ("PS".equals(command)) {
	    Debug.print("debug.popup", "performCommand(", command, ", ",
			String.valueOf(data) + ")");
	    ListItem item = _popupMenu.selectedItem();
	    _popupMenu.hide();
	    Application.application().performCommandLater(this, SETVALUE,
							  item);
	} else if (SETVALUE.equals(command)) {
	    Debug.print("debug.popup", "setting variable ", this.getVariable(),
			" to ", data, " in ", this.getOwner());
	    if (data != null)
		this.setValue(((ListItem) data).data());
	    if (_isDynamic)
		discard();
	} else
	    throw new PlaywriteInternalError
		      ("Illegal command in VariableEditor " + command);
    }
    
    public boolean isInteractive() {
	return this.isActive() && !(this.getValue() instanceof String);
    }
}
