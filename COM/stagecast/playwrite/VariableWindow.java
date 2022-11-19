/* VariableWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class VariableWindow extends PlaywriteWindow
    implements ResourceIDs.VariableIDs, ToolSource
{
    private VariableListView variableListView;
    private ScrollableArea variablesViewScroller;
    private PlaywriteButton newVariableButton;
    
    VariableWindow(VariableOwner owner, World world) {
	super(0, 0, 250, 150, world);
	this.setDisablable(true);
	this.setTitle(Resource.getTextAndFormat("VarWinTitleID",
						(new Object[]
						 { owner.getName() })));
	variableListView = new VariableListView(owner, 250, 150);
	Size contentSize = this.contentSize();
	Size vsize = new Size(variableListView.bounds.width,
			      variableListView.bounds.height);
	ScrollableArea.viewSizeForContentSize(vsize);
	variablesViewScroller
	    = new ScrollableArea(vsize.width, vsize.height, variableListView,
				 true, true);
	variablesViewScroller.setBuffered(true);
	variablesViewScroller.setHorizResizeInstruction(2);
	variablesViewScroller.setVertResizeInstruction(16);
	variablesViewScroller.setHorizontalScrollAmount(25);
	variablesViewScroller.setVerticalScrollAmount(25);
	newVariableButton = VariableListView.newVariableTool.makeButton();
	newVariableButton.setHorizResizeInstruction(1);
	newVariableButton.setVertResizeInstruction(64);
	this.getTitleBar().addSubviewRight(newVariableButton);
	Size size
	    = this.windowSizeForContentSize(variablesViewScroller.bounds.width,
					    (variablesViewScroller.bounds
					     .height));
	this.sizeTo(size.width, size.height);
	this.addSubview(variablesViewScroller);
    }
    
    void changeWindowColor(Color color) {
	Color lightColor = color.lighterColor();
	variablesViewScroller.changeWindowColor(color, lightColor);
	super.changeWindowColor(color, lightColor);
    }
    
    VariableOwner getOwner() {
	return variableListView.getOwner();
    }
    
    void disable() {
	newVariableButton.setEnabled(true);
	VariableOwner owner = getOwner();
	if (owner instanceof CocoaCharacter) {
	    CocoaCharacter ch = (CocoaCharacter) owner;
	    RuleEditor re = RuleEditor.getRuleEditor();
	    if (RuleEditor.isInSpotlight(ch)
		|| RuleEditor.wasDeletedFromSpotlight(ch))
		variablesViewScroller.enable();
	    else
		variablesViewScroller.disable();
	}
    }
    
    void enable() {
	newVariableButton.setEnabled(true);
    }
    
    public void destroyWindow() {
	variableListView.discard();
	super.destroyWindow();
    }
    
    public View sourceView(ToolSession session) {
	return this;
    }
    
    public void toolWasAccepted(ToolSession session) {
	if (session.isShiftKeyDown())
	    session.resetSession();
    }
    
    public void toolWasRejected(ToolSession session) {
	if (session.isShiftKeyDown())
	    session.resetSession();
    }
    
    public void sessionEnded(ToolSession session) {
	/* empty */
    }
}
