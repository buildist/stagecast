/* AfterBoardView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class AfterBoardView extends BoardView implements ResourceIDs.DialogIDs
{
    private Point _startPoint = null;
    
    AfterBoardView(AfterBoard board) {
	super((Board) board);
    }
    
    protected void setupDragAndDrop() {
	this.allowDragInto(GCAlias.class, this);
	this.allowDragOutOf(GCAlias.class, this);
	this.allowDragInto(CharacterPrototype.class, this);
    }
    
    void setStartPoint(Point p) {
	_startPoint = p;
    }
    
    CharacterView getViewFor(CocoaCharacter ch) {
	if (ch instanceof GCAlias)
	    return super.getViewFor(ch);
	if (ch instanceof GeneralizedCharacter) {
	    Vector v = this.getBoard().getCharacters();
	    for (int i = 0; i < v.size(); i++) {
		GCAlias gca = (GCAlias) v.elementAt(i);
		if (gca.findOriginal() == ch)
		    return super.getViewFor(gca);
	    }
	} else if (ch instanceof CharacterInstance) {
	    Vector v = this.getBoard().getCharacters();
	    for (int i = 0; i < v.size(); i++) {
		GCAlias gca = (GCAlias) v.elementAt(i);
		if (gca.findOriginal().getBinding() == ch)
		    return super.getViewFor(gca);
	    }
	}
	return null;
    }
    
    public DragDestination acceptsDrag(DragSession ds, int x, int y) {
	if (PlaywriteRoot.isPlayer() || !RuleEditor.isRecordingOrEditing())
	    return null;
	return super.acceptsDrag(ds, x, y);
    }
    
    public boolean pointIsOffStage(int x, int y) {
	Stage stage = ((AfterBoard) this.getBoard()).getStage();
	if (stage != null) {
	    Rect logicalBounds = ((AfterBoard) this.getBoard()).getBeforeBoard
				     ().getStageSquares();
	    int correspondingX = this.squareH(x) + logicalBounds.x - 1;
	    int correspondingY = this.squareV(y) + logicalBounds.y - 1;
	    return (correspondingX > stage.getNumberOfColumns()
		    || correspondingX < 1
		    || correspondingY > stage.getNumberOfRows()
		    || correspondingY < 1);
	}
	return false;
    }
    
    public boolean characterDropped(CocoaCharacter ch, PlaywriteView view,
				    Point dest, Point dragPt) {
	Stage stage = ((AfterBoard) this.getBoard()).getStage();
	if (pointIsOffStage(dest.x, dest.y)) {
	    PlaywriteDialog.warning
		(Resource.getText("dialog part of spotlight not on stage"));
	    return false;
	}
	Appearance current = ch.getCurrentAppearance();
	Point homePt = current.pixelHome(this);
	Point pt = new Point(dest.x + homePt.x - dragPt.x,
			     dest.y + homePt.y - dragPt.y);
	pt.moveTo(this.squareH(pt.x), this.squareV(pt.y));
	Point hvHome = current.getHomeSquare();
	Rect newRect
	    = new Rect(pt.x - (hvHome.x - 1), pt.y - (hvHome.y - 1),
		       current.getLogicalWidth(), current.getLogicalHeight());
	Rect boardRect = new Rect(1, 1, this.getBoard().numberOfColumns(),
				  this.getBoard().numberOfRows());
	if (!boardRect.intersects(newRect))
	    return false;
	if (ch instanceof GCAlias) {
	    ch.setVisibility(true);
	    GCAlias alias = (GCAlias) ch;
	    if (alias.getH() == pt.x && alias.getV() == pt.y)
		return false;
	    if (!dest.equals(_startPoint)) {
		if (_startPoint != null) {
		    addPointToMinRect(_startPoint);
		    _startPoint = null;
		}
		addPointToMinRect(dest);
	    }
	    RuleEditor.makeRelativeToSelf(pt);
	    GeneralizedCharacter gch = alias.findOriginal();
	    this.getWorld().doManualAction(new MoveAction(gch, pt.x, pt.y));
	    return true;
	}
	return super.characterDropped(ch, view, dest, dragPt);
    }
    
    public boolean toolClicked(ToolSession session) {
	Point dest = session.destinationMousePoint();
	if (session.toolType != RuleEditor.examineTool
	    && (PlaywriteRoot.isPlayer()
		|| !RuleEditor.isRecordingOrEditing()))
	    return false;
	if (pointIsOffStage(dest.x, dest.y)) {
	    PlaywriteDialog.warning
		(Resource.getText("dialog part of spotlight not on stage"));
	    return false;
	}
	if (super.toolClicked(session)) {
	    if (session.toolType() == Tool.copyPlaceTool
		&& !RuleEditor.isRecordingOrEditing())
		return false;
	    if (_startPoint != null) {
		addPointToMinRect(_startPoint);
		_startPoint = null;
	    }
	    addPointToMinRect(dest);
	    return true;
	}
	return false;
    }
    
    public void toolDragged(ToolSession session) {
	if (!PlaywriteRoot.isPlayer() && RuleEditor.isRecordingOrEditing()) {
	    if (session.toolType() == Tool.copyPlaceTool) {
		Point dest = session.destinationMousePoint();
		addPointToMinRect(dest);
	    }
	    super.toolDragged(session);
	}
    }
    
    public void setBounds(int x, int y, int w, int h) {
	if (this.superview() instanceof BoardView)
	    this.superview().addDirtyRect(this.bounds());
	super.setBounds(x, y, w, h);
    }
    
    private int onBoard(int coordinate, int maxBoardCoordinate) {
	if (coordinate < 1)
	    return 1;
	if (coordinate > maxBoardCoordinate)
	    return maxBoardCoordinate;
	return coordinate;
    }
    
    Point positionSpotlight(CharacterInstance ch, BoardView view) {
	Appearance appear = ch.getCurrentAppearance();
	Point pt = new Point(appear.left(ch.getH()), appear.top(ch.getV()));
	Rect rect = appear.pixelBounds(ch, view);
	this.moveTo(rect.x, rect.y);
	return pt;
    }
    
    public void addDirtyRect(Rect rect) {
	super.addDirtyRect(rect);
	if (this.superview() instanceof BoardView)
	    ((BoardView) this.superview()).addDirtyRect(this.bounds());
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("DRAG")) {
	    PlaywriteView view = (PlaywriteView) data;
	    Point dp = view.getDragPoint();
	    if (dp != null)
		_startPoint = view.convertPointToView(this, dp);
	}
	super.performCommand(command, data);
    }
    
    private void addPointToMinRect(Point p) {
	((AfterBoard) this.getBoard()).addSquareToMinRect(this.squareH(p.x),
							  this.squareV(p.y));
    }
}
