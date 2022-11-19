/* ToolHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Image;

class ToolHandler implements ToolDestination
{
    private ToolArbiter _toolArbiter;
    
    static interface ToolArbiter
    {
	public boolean wantsToolNow(Tool tool);
    }
    
    static interface ToolAdder
    {
	public void addTools(PlaywriteView playwriteview);
    }
    
    ToolHandler(ToolArbiter toolArbiter) {
	_toolArbiter = toolArbiter;
    }
    
    public boolean toolEntered(ToolSession session) {
	return _toolArbiter.wantsToolNow(session.toolType());
    }
    
    public boolean toolMoved(ToolSession session) {
	return _toolArbiter.wantsToolNow(session.toolType());
    }
    
    public void toolExited(ToolSession session) {
	/* empty */
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool tool = session.toolType();
	ViewGlue viewClicked = (ViewGlue) session.destinationView();
	if (_toolArbiter.wantsToolNow(tool)) {
	    if (tool == Tool.deleteTool) {
		Object model = viewClicked.getModelObject();
		if (model instanceof Deletable)
		    ((Deletable) model).delete();
		return true;
	    }
	    if (tool == Tool.copyLoadTool) {
		Image dragImage;
		if (viewClicked instanceof PlaywriteView)
		    dragImage = ((PlaywriteView) viewClicked).getDragImage();
		else
		    dragImage = Util.makeBitmapFromView(viewClicked.view());
		session.resetSession(dragImage, Tool.copyPlaceTool,
				     viewClicked.getModelObject());
		return true;
	    }
	    return false;
	}
	return false;
    }
    
    public void toolDragged(ToolSession session) {
	/* empty */
    }
    
    public void toolReleased(ToolSession session) {
	/* empty */
    }
}
