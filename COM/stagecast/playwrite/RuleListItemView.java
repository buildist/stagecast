/* RuleListItemView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

abstract class RuleListItemView extends PlaywriteView
    implements ResourceIDs.CommandIDs, ResourceIDs.DialogIDs, Worldly
{
    static final String OPEN_EDITOR = "Open Editor";
    static final String VERIFY_DELETE = "verify_delete";
    private static boolean openOnMouseUp = false;
    
    RuleListItemView(RuleListItem item) {
	this.setModelObject(item);
	this.allowTool(Tool.deleteTool, this);
	this.allowTool(Tool.copyLoadTool, this);
	this.allowTool(CharacterWindow.disableTool, this);
	this.setBackgroundColor(getWorld().getColor());
    }
    
    final RuleListItem getItem() {
	return (RuleListItem) this.getModelObject();
    }
    
    final ViewManager getRLIViewManager() {
	return getItem().getViewManager();
    }
    
    public final CharacterPrototype getOwner() {
	return getItem().getOwner();
    }
    
    public World getWorld() {
	return getOwner().getWorld();
    }
    
    void resetSubroutineLights() {
	/* empty */
    }
    
    abstract void update();
    
    public boolean mouseDown(MouseEvent event) {
	RuleListItem item = getItem();
	World world = getWorld();
	long time = event.timeStamp();
	this.setDragPoint(new Point(event.x, event.y));
	if (world.isRunning() && !world.isSuspendedForDebug())
	    return false;
	if (event.isShiftKeyDown()) {
	    this.selectModel(event);
	    PlaywriteRoot.resetDoubleClicking();
	} else if (PlaywriteRoot.isDoubleClick(time, item)) {
	    openOnMouseUp = true;
	    PlaywriteRoot.resetDoubleClicking();
	} else {
	    Selection.resetGlobalState(event);
	    this.selectModel(event);
	    PlaywriteRoot.setDoubleClicking(time, item);
	}
	return true;
    }
    
    public void mouseUp(MouseEvent event) {
	if (openOnMouseUp) {
	    openOnMouseUp = false;
	    Selection.resetGlobalState(event);
	    PlaywriteRoot.markBusy();
	    performCommand("Open Editor", getItem());
	    PlaywriteRoot.clearBusy();
	} else
	    super.mouseUp(event);
    }
    
    final Point getDragPoint() {
	Point dragPoint = super.getDragPoint();
	dragPoint.x = 0;
	dragPoint.y = this.height() / 2;
	return dragPoint;
    }
    
    public boolean toolEntered(ToolSession session) {
	return getItem().dragOrToolPermitted() && super.toolEntered(session);
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool toolType = session.toolType();
	if (!getItem().dragOrToolPermitted()) {
	    if (toolType == Tool.deleteTool)
		PlaywriteDialog.warning(Resource.getText("dialog dltNP"));
	    else if (toolType == Tool.copyLoadTool)
		PlaywriteDialog.warning(Resource.getText("dialog cpyNP"));
	    else
		PlaywriteDialog.warning(Resource.getText("dialog toolNP"));
	    return false;
	}
	if (toolType == Tool.deleteTool) {
	    if (this.getModelObject() instanceof Deletable) {
		Deletable modelObject = (Deletable) this.getModelObject();
		if (modelObject.allowDelete()) {
		    if (modelObject instanceof Subroutine
			&& ((Subroutine) modelObject).getRuleCount() > 0) {
			session.cancelSession(false);
			PlaywriteRoot.app().performCommandLater
			    (this, "verify_delete", modelObject);
			return true;
		    }
		    getWorld().setModified(true);
		    ((Deletable) this.getModelObject()).delete();
		    if (!session.isAltKeyDown())
			session.cancelSession(false);
		    return true;
		}
		return false;
	    }
	} else {
	    if (toolType == Tool.copyLoadTool) {
		session.resetSession(this, Tool.copyPlaceTool);
		return true;
	    }
	    if (toolType == CharacterWindow.disableTool) {
		RuleListItem item = getItem();
		if (!(item instanceof Comment)) {
		    if (this.superview() instanceof Slot)
			((Slot) this.superview()).enableOrDisable();
		    return true;
		}
		return false;
	    }
	}
	return super.toolClicked(session);
    }
    
    public void toolDragged(ToolSession session) {
	/* empty */
    }
    
    public String toString() {
	String out = String.valueOf(this.getClass()) + " for ";
	Object modObject = this.getModelObject();
	if (modObject != null) {
	    if (modObject instanceof Named)
		out += ((Named) modObject).getName();
	} else
	    out += "?";
	return out;
    }
    
    public void performCommand(String command, Object data) {
	if ("verify_delete".equals(command)) {
	    PlaywriteDialog dlg
		= new PlaywriteDialog("dialog ver del sub", "command d",
				      "command c");
	    String result = dlg.getAnswer();
	    if (result.equals("command d")) {
		getWorld().setModified(true);
		((Deletable) data).delete();
	    }
	} else
	    super.performCommand(command, data);
    }
}
