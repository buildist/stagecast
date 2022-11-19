/* SquareView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Vector;

class SquareView extends ModalView
{
    private BoardView _boardView;
    
    SquareView(BoardView boardView, Vector sq, Point pt) {
	super(boardView.getBoard().sortZ(sq), pt, false);
	_boardView = boardView;
	this.allowDragInto(CharacterInstance.class, this);
	this.allowDragOutOf(CharacterInstance.class, this);
	this.allowDragInto(GCAlias.class, this);
	this.allowDragOutOf(GCAlias.class, this);
	this.layoutContents();
    }
    
    public BoardView getBoardView() {
	return _boardView;
    }
    
    public void discard() {
	_boardView = null;
	super.discard();
    }
    
    int targetSlot(CharacterView draggee, Point dest) {
	Vector subviews = this.subviews();
	Rect targetRect = new Rect();
	int y = dest.y;
	int size = subviews.size();
	for (int i = 0; i < size; i++) {
	    CharacterView view = (CharacterView) subviews.elementAt(i);
	    if (y > view.bounds.y + view.bounds.height / 2)
		return i;
	}
	return size;
    }
    
    public void dragWasAccepted(DragSession session) {
	_boardView.dragWasAccepted(session);
    }
    
    public boolean dragDropped(DragSession session) {
	COM.stagecast.ifc.netscape.application.View draggee
	    = this.viewBeingDragged(session);
	Vector subviews = this.subviews();
	if (draggee instanceof CharacterView) {
	    CharacterView view
		= (CharacterView) this.viewBeingDragged(session);
	    CocoaCharacter ch = view.getCharacter();
	    Board board = (Board) ch.getCharContainer();
	    World world = ch.getWorld();
	    Point dest = session.destinationMousePoint();
	    ch.setVisibility(true);
	    draggee.setDirty(true);
	    if (ch instanceof CharacterInstance) {
		int slot = targetSlot(view, dest);
		int mySlot = subviews.indexOfIdentical(draggee);
		if (slot == mySlot || mySlot + 1 == slot)
		    return false;
		CharacterInstance target;
		if (slot < subviews.size()) {
		    view = (CharacterView) subviews.elementAt(slot);
		    target = (CharacterInstance) view.getCharacter();
		} else {
		    view = (CharacterView) subviews.lastElement();
		    target = (CharacterInstance) view.getCharacter();
		    int z = target.getZ();
		    target = (CharacterInstance) board.getCharacterAtZ(z + 1);
		}
		GeneralizedCharacter gc1
		    = new GeneralizedCharacter((CharacterInstance) ch);
		GeneralizedCharacter gc2;
		if (target == null)
		    gc2 = null;
		else
		    gc2 = new GeneralizedCharacter(target);
		world.doManualAction(new PutUnderAction(gc1, gc2));
	    } else if (ch instanceof GCAlias) {
		if (!RuleEditor.isRecordingOrEditing())
		    return false;
		AfterBoard afterBoard = (AfterBoard) ch.getContainer();
		int slot = targetSlot(view, dest);
		int mySlot = subviews.indexOfIdentical(draggee);
		if (slot == mySlot || slot - 1 == mySlot || slot < 0)
		    return false;
		GCAlias alias;
		if (slot < subviews.size()) {
		    view = (CharacterView) subviews.elementAt(slot);
		    alias = (GCAlias) view.getCharacter();
		} else {
		    view = (CharacterView) subviews.lastElement();
		    alias = (GCAlias) view.getCharacter();
		    int z = alias.getZ();
		    alias = (GCAlias) afterBoard.getCharacterAtZ(z + 1);
		}
		GeneralizedCharacter gc1 = ((GCAlias) ch).findOriginal();
		GeneralizedCharacter gc2;
		if (alias == null)
		    gc2 = null;
		else
		    gc2 = alias.findOriginal();
		world.doManualAction(new PutUnderAction(gc1, gc2));
	    } else
		return false;
	    this.setList(board.sortZ(this.getList()));
	    this.layoutContents();
	    world.setModified(true);
	    return true;
	}
	return false;
    }
}
