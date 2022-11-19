/* VariableEditor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;

class VariableEditor extends AbstractVariableEditor
    implements ValueView.SetterGetter
{
    static final String POPUP_VALUES = "PV";
    static final String POPUP_SELECT = "PS";
    static final Color TITLE_COLOR_SYSTEM = new Color(178, 178, 178);
    static final Color TITLE_COLOR_USER = new Color(241, 246, 100);
    static final Tool[] CONTENT_TOOLS = { Tool.copyLoadTool, Tool.deleteTool };
    
    VariableEditor(VariableOwner owner, Variable variable,
		   ValueView.SetterGetter vsg) {
	super(owner, variable, vsg);
    }
    
    Tool[] getContentTools() {
	return CONTENT_TOOLS;
    }
    
    void createContentView(ValueView.SetterGetter vsg) {
	Rect interior = this.interiorRect();
	if (this.getDisplayContent()) {
	    Tool[] legalTools;
	    if (this.isEnabled())
		legalTools = getContentTools();
	    else
		legalTools = null;
	    ValueView valueView = new ValueView(vsg, true, false, legalTools) {
		public void didSizeBy(int dx, int dy) {
		    if (this.superview() != null)
			this.moveTo(((this.superview().width() - this.width())
				     / 2),
				    this.y());
		}
	    };
	    this.setValueView(valueView);
	    valueView.setTextFieldMinSize(40, Util.valueFontHeight);
	    this.setContentView(valueView);
	    valueView.setBorder(null);
	    valueView.setEnabledBorder(null);
	} else {
	    PlaywriteView blank
		= new PlaywriteView(0, 0, 10, Util.valueFontHeight);
	    blank.setBorder(null);
	    blank.setBackgroundColor(Color.white);
	    blank.setMinSize(10, Util.valueFontHeight);
	    this.setContentView(blank);
	    this.setMouseTransparency(true);
	}
	View content = this.getContentView();
	content.moveTo(interior.x, getNameViewBottom());
	content.setHorizResizeInstruction(32);
	content.setVertResizeInstruction(4);
	this.addSubview(content);
	this.sizeToMinSize();
	this.setDirty(true);
    }
    
    final void updateContentView() {
	if (!this.getDisplayContent())
	    Debug.print(true, "VariableEditor ", this,
			" shouldn't have got an update event");
	else
	    this.getValueView().updateView();
    }
    
    public int getNameViewBottom() {
	return this.getNameView().height() + this.border().topMargin();
    }
    
    public void drawViewBackground(Graphics g) {
	super.drawViewBackground(g);
	g.setColor(this.getVariable().isSystemVariable() ? TITLE_COLOR_SYSTEM
		   : TITLE_COLOR_USER);
	g.fillRect(0, 0, this.width(), getNameViewBottom());
    }
    
    public Size minSize() {
	Size nameSize = this.getNameView().minSize();
	Size contentSize = this.getContentView().minSize();
	int width = (Math.max(nameSize.width, contentSize.width)
		     + this.border().widthMargin());
	int height = (getNameViewBottom() + contentSize.height
		      + this.border().bottomMargin());
	return new Size(width, height);
    }
    
    public void setEnabled(boolean enabled) {
	super.setEnabled(enabled);
	this.getValueView().setEnabled(enabled);
    }
}
