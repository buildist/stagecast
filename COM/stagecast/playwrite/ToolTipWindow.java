/* ToolTipWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.LineBorder;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.Timer;

class ToolTipWindow extends InternalWindow implements Target
{
    public static Font toolTipFont = Font.fontNamed("Dialog", 0, 10);
    private TextField textField;
    private Timer timer;
    
    ToolTipWindow() {
	this.setCanBecomeMain(false);
	this.setTransparent(false);
	this.setBorder(new LineBorder(Util.defaultColor));
	this.setLayer(400);
	this.contentView().setBackgroundColor(Color.white);
	textField = TextField.createLabel("what is this?");
	textField.setTransparent(false);
	textField.setBackgroundColor(Color.white);
	textField.setBorder(null);
	textField.setJustification(1);
	textField.setFont(toolTipFont);
	this.addSubview(textField);
	textField.moveTo(2, 0);
	timer = new Timer(this, "", 1000);
	timer.setInitialDelay(0);
	timer.setRepeats(false);
    }
    
    void showToolTips(ToolTipable currentView) {
	RootView root = PlaywriteRoot.getMainRootView();
	Point mouseLoc = root.mousePoint();
	if (root.viewForMouse(mouseLoc.x, mouseLoc.y) != currentView)
	    ToolTips.setOn(false);
	else {
	    String tip = currentView.getToolTipText();
	    Rect newBounds = new Rect();
	    if (tip != null) {
		textField.setStringValue(currentView.getToolTipText());
		textField.sizeToMinSize();
		newBounds.sizeTo(textField.width() + 6,
				 textField.height() + 4);
		newBounds.moveTo(mouseLoc.x - textField.width() / 2,
				 mouseLoc.y + 16);
		Rect screen = PlaywriteRoot.getMainRootView().bounds;
		COM.stagecast.ifc.netscape.application.Size screenSize
		    = PlaywriteRoot.getRootWindowSize();
		if (!screen.contains(newBounds))
		    newBounds.moveTo(mouseLoc.x - textField.width() / 2,
				     mouseLoc.y - textField.height() - 16);
		if (!screen.contains(newBounds))
		    newBounds.moveTo(mouseLoc.x - textField.width() - 16,
				     mouseLoc.y - textField.height() / 2);
		if (!screen.contains(newBounds))
		    newBounds.moveTo(mouseLoc.x + 16,
				     mouseLoc.y - textField.height() / 2);
		this.setBounds(newBounds);
		this.show();
	    }
	}
    }
    
    void hideToolTips() {
	this.hide();
    }
    
    void startTimer(String command, int delay) {
	timer.setCommand(command);
	timer.setInitialDelay(delay);
	timer.start();
    }
    
    void stopTimer() {
	timer.stop();
    }
    
    void setToolTipDelay(int delay) {
	if (timer != null)
	    timer.setDelay(delay);
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("ON")) {
	    ToolTips.setOn(true);
	    showToolTips(ToolTips.getCurrentView());
	}
	if (command.equals("OFF")) {
	    ToolTips.setOn(false);
	    hideToolTips();
	}
    }
}
