/* AutoDragSession - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.DragSource;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;

public class AutoDragSession extends DragSession
    implements ScrollableArea.AutoScrollReason
{
    public AutoDragSession(DragSource source, Image image, int initialX,
			   int initialY, int mouseDownX, int mouseDownY,
			   String dataType, Object data) {
	super(source, image, initialX, initialY, mouseDownX, mouseDownY,
	      dataType, data);
	ScrollableArea.addAutoScrollReason(this);
    }
    
    public void mouseUp(MouseEvent event) {
	ScrollableArea.cancelAutoScrolling();
	ScrollableArea.removeAutoScrollReason(this);
	if (this.destination() == null || !this.destinationIsAccepting()) {
	    MouseEvent pseudoDragEvent
		= new MouseEvent(event.timeStamp(), -2, event.x, event.y,
				 event.modifiers());
	    this.mouseDragged(pseudoDragEvent);
	}
	super.mouseUp(event);
    }
}
