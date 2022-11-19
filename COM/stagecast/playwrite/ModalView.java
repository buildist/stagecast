/* ModalView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;

abstract class ModalView extends PlaywriteView
    implements ExtendedDragSource, DragDestination, Worldly
{
    private static int border = 2;
    private Vector list;
    private boolean horizontal;
    private InternalWindow window;
    
    ModalView(Vector list, Point pt, boolean horizontal) {
	this.setBorder(BezelBorder.groovedBezel());
	this.list = list;
	this.horizontal = horizontal;
	computeBounds();
	Rect windowRect
	    = new Rect(pt.x - this.width() / 2, pt.y - this.height() / 2,
		       this.width(), this.height());
	PlaywriteRoot.app();
	Rect maxBounds = PlaywriteRoot.getMainRootViewBounds();
	windowRect.intersectWith(maxBounds);
	window = new InternalWindow(0, windowRect.x, windowRect.y,
				    windowRect.width, windowRect.height);
	window.setLayer(300);
	if (bounds.height > maxBounds.height)
	    window.addSubview(new ScrollableArea(0, 0, windowRect.width,
						 windowRect.height, this,
						 false, true, true));
	else
	    window.addSubview(this);
    }
    
    final Vector getList() {
	return list;
    }
    
    final void setList(Vector v) {
	list = v;
    }
    
    public World getWorld() {
	return ((CocoaCharacter) list.firstElement()).getWorld();
    }
    
    private void computeBounds() {
	int miniSize = 32;
	if (horizontal)
	    this.sizeTo(list.size() * 32 + 2 * border, 32 + 2 * border);
	else
	    this.sizeTo(32 + 2 * border, list.size() * 32 + 2 * border);
    }
    
    void layoutContents() {
	int miniSize = 32;
	while (!this.subviews().isEmpty()) {
	    CharacterView cView
		= (CharacterView) this.subviews().firstElement();
	    cView.removeFromSuperview();
	    cView.discard();
	}
	for (int i = 0; i < list.size(); i++) {
	    CocoaCharacter ch = (CocoaCharacter) list.elementAt(i);
	    CharacterView view = new CharacterView(ch, 32);
	    Rect rect = view.bounds();
	    Util.scaleRectToImageProportion(rect, ch.getCurrentAppearance()
						      .getBitmap());
	    if (horizontal)
		view.moveTo(border + i * 32 + (32 - rect.width) / 2, border);
	    else
		view.moveTo(border, (border + (list.size() - 1 - i) * 32
				     + (32 - rect.height) / 2));
	    this.addSubview(view);
	    if (Selection.isSelected(ch))
		view.hilite();
	}
	this.setDirty(true);
    }
    
    void show() {
	window.show();
    }
    
    public void hide() {
	window.hide();
	this.removeFromSuperview();
	window = null;
	while (!this.subviews().isEmpty()) {
	    CharacterView view
		= (CharacterView) this.subviews().firstElement();
	    view.removeFromSuperview();
	    view.setModelObject(null);
	}
    }
    
    public boolean prepareToDrag(Object data) {
	CharacterView draggee = (CharacterView) data;
	draggee.getCharacter().setVisibility(false);
	draggee.setDirty(true);
	return true;
    }
    
    public void dragWasAccepted(DragSession session) {
	/* empty */
    }
    
    public boolean dragWasRejected(DragSession session) {
	CharacterView draggee = (CharacterView) this.viewBeingDragged(session);
	draggee.getCharacter().setVisibility(true);
	draggee.setDirty(true);
	return true;
    }
    
    public View sourceView(DragSession session) {
	return this;
    }
    
    public boolean dragEntered(DragSession session) {
	if (wantsDraggedObject(session))
	    return true;
	return false;
    }
    
    public void dragExited(DragSession session) {
	wantsDraggedObject(session);
    }
    
    public boolean dragMoved(DragSession session) {
	return wantsDraggedObject(session);
    }
    
    boolean wantsDraggedObject(DragSession session) {
	View draggee = this.viewBeingDragged(session);
	CocoaCharacter ch = ((CharacterView) draggee).getCharacter();
	return ch instanceof CharacterInstance || ch instanceof GCAlias;
    }
    
    public void drawView(Graphics g) {
	super.drawView(g);
	Vector contents = this.subviews();
	for (int i = 0; i < contents.size(); i++) {
	    CharacterView view = (CharacterView) contents.elementAt(i);
	    CocoaCharacter ch = view.getCharacter();
	    if (ch.isVisible())
		ch.getCurrentAppearance().drawFixed(view, g, view.x(),
						    view.y(), 32);
	}
    }
    
    public String toString() {
	return "<ModalView of " + list + ">";
    }
    
    public abstract boolean dragDropped(DragSession dragsession);
}
