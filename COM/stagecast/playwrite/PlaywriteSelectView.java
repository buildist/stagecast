/* PlaywriteSelectView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

class PlaywriteSelectView extends PlaywriteView
{
    private boolean selecting = false;
    private SelectView selectView = null;
    
    PlaywriteSelectView(int x, int y, int width, int height) {
	super(x, y, width, height);
    }
    
    PlaywriteSelectView() {
	/* empty */
    }
    
    final boolean getSelecting() {
	return selecting;
    }
    
    final void setSelecting(boolean b) {
	selecting = b;
    }
    
    final SelectView getSelectView() {
	return selectView;
    }
    
    final void setSelectView(SelectView v) {
	selectView = v;
    }
    
    public boolean mouseDown(MouseEvent event) {
	boolean b = super.mouseDown(event);
	if (!this.isDisabled()) {
	    SelectView v = new SelectView(this, event.x, event.y);
	    setSelectView(v);
	    setSelecting(true);
	    PlaywriteRoot.getMainRootView().setMouseView(v);
	    return false;
	}
	return b;
    }
    
    public DragDestination acceptsDrag(DragSession ds, int x, int y) {
	if (this.isDisabled())
	    return null;
	Object model = this.modelObjectBeingDragged(ds);
	Class test;
	if (model instanceof MultiDragView)
	    test = ((MultiDragView) model).getItemModelClass();
	else
	    test = model.getClass();
	return this.allowsDragInto(test);
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("DRAG")
	    && Selection.isSelected(((PlaywriteView) data).getModelObject())) {
	    Vector views = this.subviews();
	    Vector selectedViews = new Vector(1);
	    for (int i = 0; i < views.size(); i++) {
		View view = (View) views.elementAt(i);
		if (view instanceof PlaywriteView) {
		    PlaywriteView v = (PlaywriteView) view;
		    if (Selection.isSelected(v.getModelObject()))
			selectedViews.addElementIfAbsent(v);
		}
	    }
	    selectedViews.addElementIfAbsent(data);
	    if (selectedViews.size() == 1)
		super.performCommand(command, data);
	    else {
		MultiDragView multiView
		    = new MultiDragView(selectedViews, (PlaywriteView) data);
		this.addSubview(multiView);
		ExtendedDragSource ds
		    = this.allowsDragOutOf(multiView.getItemModelClass());
		Point tempPoint = new Point(0, 0);
		multiView.convertPointToView(this, multiView.getDragPoint(),
					     tempPoint);
		Image dragImage = multiView.getDragImage();
		if (ds.prepareToDrag(multiView)) {
		    multiView.removeFromSuperview();
		    new AutoDragSession(ds, dragImage, multiView.x(),
					multiView.y(), tempPoint.x,
					tempPoint.y, null, multiView);
		}
	    }
	} else
	    super.performCommand(command, data);
    }
}
