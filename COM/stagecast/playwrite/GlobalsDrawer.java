/* GlobalsDrawer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Size;

class GlobalsDrawer extends Drawer
{
    GlobalsDrawer(World world, Object contentModel, String title,
		  String newCommandID, Drawer.Specializer special,
		  boolean allowSpecialWhileRecording, Tool[] drawerTools,
		  Tool[] contentTools, ToolDestination toolDest) {
	super(world, contentModel, title, newCommandID, special,
	      allowSpecialWhileRecording, drawerTools, contentTools, toolDest);
    }
    
    PlaywriteView createContentView(Size size) {
	return new VariableListView(((VariableList) this.getContentModel())
					.getOwner(),
				    size.width, size.height) {
	    public void dragWasAccepted(DragSession session) {
		super.dragWasAccepted(session);
		DrawerWindow win = (DrawerWindow) this.window();
		if (session.destination() != session.source()
		    && win.isSticking())
		    win.close();
	    }
	};
    }
    
    public void depopulateDrawer(PlaywriteWindow win) {
	((VariableListView) this.getContentView()).discard();
    }
}
