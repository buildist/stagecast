/* TitledObjectView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFieldOwner;
import COM.stagecast.ifc.netscape.application.View;

public class TitledObjectView extends PlaywriteView
    implements Enableable, TextFieldOwner
{
    static final int MIN_SIZE = 40;
    static final int VALUE_TEXT_MIN_WIDTH = 25;
    private boolean _enabled = true;
    private PlaywriteTextField _nameView;
    private View _contentView;
    private ValueView _valueView;
    private Named _namedObject;
    private String _fixedName;
    private Color _titleColor;
    private Color _insetBackgroundColor;
    private ViewManager _viewManager;
    
    public TitledObjectView(Named namedObject, Color titleColor) {
	super(0, 0, 40, 40);
	ASSERT.isNotNull(namedObject);
	_namedObject = namedObject;
	_titleColor = titleColor;
	this.setBorder(ScrapBorder.getRuleBorder());
	this.setBackgroundColor(Util.valueBGColor);
	this.setTransparent(false);
    }
    
    public TitledObjectView(String name, Color titleColor) {
	super(0, 0, 40, 40);
	ASSERT.isNotNull(name);
	_fixedName = name;
	_titleColor = titleColor;
	this.setBorder(ScrapBorder.getRuleBorder());
	this.setBackgroundColor(Util.valueBGColor);
	this.setTransparent(false);
    }
    
    public void initializeContentView(ValueView.SetterGetter vsg) {
	ASSERT.isNotNull(vsg);
	ValueView valueView = new ValueView(vsg, true, false, null);
	valueView.setTextFieldMinSize(25, Util.valueFontHeight);
	valueView.setBorder(null);
	valueView.setEnabledBorder(null);
	initializeContentView(valueView);
    }
    
    public void initializeContentView(ValueView valueView) {
	ASSERT.isNotNull(valueView);
	createNameView();
	addContentView(valueView);
	this.layoutView(0, 0);
    }
    
    public final void setViewManager(ViewManager vm) {
	ASSERT.isNotNull(vm);
	ASSERT.isTrue(_viewManager == null);
	_viewManager = vm;
	_viewManager.addView(this);
    }
    
    final PlaywriteTextField getNameView() {
	return _nameView;
    }
    
    final void setNameView(PlaywriteTextField view) {
	_nameView = view;
    }
    
    final View getContentView() {
	return _contentView;
    }
    
    final void setContentView(View view) {
	_contentView = view;
    }
    
    final Point getDragPoint() {
	Point dragPoint = super.getDragPoint();
	dragPoint.x = 0;
	dragPoint.y = getNameView().height() / 2;
	return dragPoint;
    }
    
    public void resetNamedObject(Named named) {
	_namedObject = named;
	updateNameView();
    }
    
    public void setEnabled(boolean enabled) {
	_enabled = enabled;
	if (_namedObject != null)
	    _nameView.setUserEditable(_enabled);
    }
    
    public boolean isEnabled() {
	return _enabled;
    }
    
    void createNameView() {
	PlaywriteTextField titleView
	    = new PlaywriteTextField(0, 0, this.width(), 20);
	if (_nameView != null)
	    _nameView.removeFromSuperview();
	setNameView(titleView);
	String title;
	if (_namedObject == null) {
	    title = _fixedName;
	    _nameView.setUserEditable(false);
	} else {
	    title = _namedObject.getName();
	    _nameView.setUserEditable(isEnabled());
	}
	Size titleSize = Util.stringSize(Util.varTitleFont, title);
	_nameView.setOwner(this);
	_nameView.setBorder(null);
	_nameView.setTransparent(true);
	_nameView.setFont(Util.varTitleFont);
	_nameView.setJustification(1);
	_nameView.setWrapsContents(false);
	_nameView.setStringValue(title);
	_nameView.setBackgroundColor(Util.valueBGColor);
	_nameView.sizeToMinSize();
	Rect interior = this.interiorRect();
	_nameView.setBounds(interior.x, interior.y, interior.width,
			    _nameView.height());
	_nameView.setVertResizeInstruction(4);
	_nameView.setHorizResizeInstruction(2);
	this.addSubview(_nameView);
    }
    
    void addContentView(ValueView valueView) {
	Rect interior = this.interiorRect();
	setValueView(valueView);
	valueView.setTextFieldMinSize(25, Util.valueFontHeight);
	setContentView(valueView);
	View content = getContentView();
	content.moveTo(interior.x, getNameViewBottom());
	content.setHorizResizeInstruction(32);
	content.setVertResizeInstruction(4);
	this.addSubview(content);
	this.sizeToMinSize();
	this.setDirty(true);
    }
    
    void createContentView(ValueView.SetterGetter vsg) {
	Rect interior = this.interiorRect();
	ValueView valueView = new ValueView(vsg, true, false, null);
	setValueView(valueView);
	valueView.setTextFieldMinSize(25, Util.valueFontHeight);
	setContentView(valueView);
	valueView.setBorder(null);
	valueView.setEnabledBorder(null);
	View content = getContentView();
	content.moveTo(interior.x, getNameViewBottom());
	content.setHorizResizeInstruction(32);
	content.setVertResizeInstruction(4);
	this.addSubview(content);
	this.sizeToMinSize();
	this.setDirty(true);
    }
    
    public void setValueView(ValueView valueView) {
	_valueView = valueView;
    }
    
    public ValueView getValueView() {
	return _valueView;
    }
    
    public int getNameViewBottom() {
	return getNameView().height() + this.border().topMargin();
    }
    
    public void updateNameView() {
	ASSERT.isNotNull(_namedObject);
	this.setDirty(true);
	_nameView.setStringValue(_namedObject.getName());
	this.sizeToMinSize();
	this.setDirty(true);
    }
    
    public void updateContentView() {
	getValueView().updateView();
    }
    
    public void setTitleColor(Color color) {
	_titleColor = color;
    }
    
    public Color getTitleColor() {
	return _titleColor;
    }
    
    public void setInsetBackgroundColor(Color color) {
	_insetBackgroundColor = color;
    }
    
    public void drawViewBackground(Graphics g) {
	super.drawViewBackground(g);
	if (_insetBackgroundColor != null) {
	    g.setColor(_insetBackgroundColor);
	    int x = this.border().leftMargin() + 1;
	    int y = getNameViewBottom() + 2;
	    int w = this.width() - x - this.border().rightMargin() - 1;
	    int h = this.height() - y - this.border().bottomMargin();
	    g.fillRect(x, y, w, h);
	}
	g.setColor(getTitleColor());
	g.fillRect(0, 0, this.width(), getNameViewBottom());
    }
    
    public Size minSize() {
	Size nameSize = getNameView().minSize();
	Size contentSize = getContentView().minSize();
	int width = (Math.max(nameSize.width, contentSize.width)
		     + this.border().widthMargin());
	int height = (getNameViewBottom() + contentSize.height
		      + this.border().bottomMargin());
	return new Size(width, height);
    }
    
    public void subviewDidResize(View subview) {
	if (this.subviews().containsIdentical(subview)) {
	    this.sizeToMinSize();
	    this.layoutView(0, 0);
	}
	super.subviewDidResize(subview);
    }
    
    public void textEditingDidBegin(TextField tf) {
	this.unhilite();
	this.setDirty(true);
    }
    
    public void textWasModified(TextField tf) {
	this.setDirty(true);
	this.sizeToMinSize();
	this.setDragImageDirty();
    }
    
    public boolean textEditingWillEnd(TextField tf, int endCondition,
				      boolean changed) {
	return true;
    }
    
    public void textEditingDidEnd(TextField tf, int endCondition,
				  boolean changed) {
	if (tf == _nameView) {
	    if (_enabled)
		_namedObject.setName(tf.stringValue());
	    else
		throw new RuntimeException
			  ("text editing shouldn't happen at all when disabled");
	} else
	    Debug.print(true, "Notified about textEdit for ", tf);
    }
    
    public View viewForMouse(int x, int y) {
	View returnedView = super.viewForMouse(x, y);
	if (returnedView != _nameView && !isEnabled())
	    returnedView = null;
	return returnedView;
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
    
    public void discard() {
	if (_viewManager != null)
	    _viewManager.removeView(this);
	super.discard();
    }
}
