/* BooleanVariableEditor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.FontMetrics;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;

class BooleanVariableEditor extends AbstractVariableEditor implements Target
{
    static final String TOGGLE_VALUE = "TV";
    static final int PADDING = 2;
    private transient String _trueText;
    private transient String _falseText;
    private transient PlaywriteView _toggle;
    
    BooleanVariableEditor(VariableOwner owner, BooleanVariable variable,
			  ValueView.SetterGetter vsg) {
	this(owner, variable, vsg, true);
    }
    
    BooleanVariableEditor(VariableOwner owner, BooleanVariable variable,
			  ValueView.SetterGetter vsg, boolean displayContent) {
	super(owner, (Variable) variable, vsg, displayContent);
    }
    
    private final BooleanVariable getBooleanVariable() {
	return (BooleanVariable) this.getVariable();
    }
    
    void createNameView() {
	_trueText = getBooleanVariable().getTrueName();
	_falseText = getBooleanVariable().getFalseName();
	super.createNameView();
	PlaywriteTextField name = this.getNameView();
	name.setJustification(0);
	name.setVertResizeInstruction(64);
	name.setHorizResizeInstruction(0);
    }
    
    Tool[] getContentTools() {
	return new Tool[0];
    }
    
    void createContentView(ValueView.SetterGetter vsg) {
	_toggle = new PlaywriteView(0, 0, Util.lightSwitchOn.width(),
				    Util.lightSwitchOff.height());
	_toggle.setMinSize(Util.lightSwitchOn.width(),
			   Util.lightSwitchOff.height());
	_toggle.setBackgroundColor(Util.valueBGColor);
	_toggle.setImageDisplayStyle(0);
	Rect interior = this.interiorRect();
	PlaywriteTextField name = this.getNameView();
	_toggle.moveTo(interior.x, interior.y);
	name.moveTo(_toggle.x() + _toggle.width() + 2, interior.y);
	_toggle.setVertResizeInstruction(64);
	_toggle.setHorizResizeInstruction(0);
	_toggle.setEventDelegate(-3, 0, 1, "TV", this);
	_toggle.setMouseTransparency(!this.getDisplayContent()
				     || !this.isEnabled());
	this.addSubview(_toggle);
	this.setContentView(_toggle);
	updateNameForValue();
    }
    
    public void setEnabled(boolean enabled) {
	super.setEnabled(enabled);
	_toggle.setMouseTransparency(!this.getDisplayContent() || !enabled);
    }
    
    void updateNameForValue() {
	PlaywriteTextField nameView = this.getNameView();
	boolean val = Boolean.TRUE.equals(this.getValue());
	nameView.setStringValue(val || !this.getDisplayContent() ? _trueText
				: _falseText);
	nameView.sizeToMinSize();
    }
    
    final void updateContentView() {
	if (this.getDisplayContent()) {
	    boolean val = Boolean.TRUE.equals(this.getValue());
	    this.setDirty(true);
	    updateNameForValue();
	    ((PlaywriteView) this.getContentView())
		.setImage(val ? Util.lightSwitchOn : Util.lightSwitchOff);
	    sizeToMinSize();
	    this.setDirty(true);
	}
    }
    
    public void sizeToMinSize() {
	FontMetrics fm = this.getNameView().font().fontMetrics();
	int trueWidth = fm.stringWidth(_trueText);
	int falseWidth = fm.stringWidth(_falseText);
	int width
	    = (Math.max(trueWidth, falseWidth) + this.getContentView().width()
	       + this.border().widthMargin() + 2);
	int height = (Math.max(this.getNameView().height(),
			       this.getContentView().height())
		      + this.border().heightMargin());
	this.sizeTo(width, height);
    }
    
    public void performCommand(String command, Object data) {
	if ("TV".equals(command)) {
	    if (this.isActive()) {
		boolean b = ((Boolean) this.getValue()).booleanValue();
		this.setValue(b ? Boolean.FALSE : Boolean.TRUE);
	    }
	} else
	    throw new PlaywriteInternalError("Illegal commmand " + command
					     + " in " + this.getClass());
    }
}
