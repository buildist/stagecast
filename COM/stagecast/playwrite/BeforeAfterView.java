/* BeforeAfterView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.ContainerView;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;

class BeforeAfterView extends PlaywriteView
{
    private static Bitmap _littleArrow = null;
    private BeforeBoard beforeBoard;
    private AfterBoard afterBoard;
    private BoardView beforeBoardView;
    private AfterBoardView afterBoardView;
    private ContainerView arrowView;
    private AfterBoardHandle spotlight = null;
    private int edge;
    
    BeforeAfterView(Rule r, int squareSize, boolean inRuleEditor) {
	Bitmap ruleArrow
	    = Resource.getImage(squareSize < 32 ? "LittleArrow" : "BigArrow");
	beforeBoard = r.getBeforeBoard();
	if (beforeBoard == null) {
	    beforeBoard = BeforeBoard.createFromRule(r, squareSize);
	    r.setBeforeBoard(beforeBoard);
	}
	afterBoard = r.getAfterBoard();
	if (afterBoard == null) {
	    afterBoard = AfterBoard.createFromRule(r, beforeBoard);
	    r.setAfterBoard(afterBoard);
	}
	beforeBoardView = (BoardView) beforeBoard.createView();
	afterBoardView = r.createAfterBoardView();
	if (beforeBoard.getSquareSize() != squareSize)
	    beforeBoard.setSquareSize(squareSize);
	if (afterBoard.getSquareSize() != squareSize)
	    afterBoard.setSquareSize(squareSize);
	this.setBorder(null);
	setBackgroundColor(getWorld().getLightColor());
	edge = squareSize / 2;
	RuleEditor editor = RuleEditor.getRuleEditor();
	if (inRuleEditor && editor != null) {
	    editor.setBeforeBoardView(beforeBoardView);
	    editor.setAfterBoardView(afterBoardView);
	    edge = squareSize;
	}
	PackLayout layout = new PackLayout();
	PackConstraints pc
	    = new PackConstraints(8, false, false, false, 0, 0, 1, 1, 2);
	this.setLayoutManager(layout);
	this.addSubview(beforeBoardView);
	pc.setAnchor(2);
	pc.setExpand(true);
	layout.setConstraints(beforeBoardView, pc);
	arrowView = new ContainerView();
	arrowView.setImage(ruleArrow);
	arrowView.setBorder(null);
	arrowView.setMinSize(ruleArrow.width(), ruleArrow.height());
	arrowView.sizeToMinSize();
	this.addSubview(arrowView);
	pc.setExpand(false);
	pc.setAnchor(8);
	pc.setInternalPadX(20);
	layout.setConstraints(arrowView, pc);
	this.addSubview(afterBoardView);
	if (inRuleEditor && editor != null
	    && RuleEditor.isRecordingOrEditing())
	    showSpotlightHandles();
	pc.setAnchor(6);
	pc.setInternalPadX(0);
	pc.setExpand(true);
	layout.setConstraints(afterBoardView, pc);
	this.sizeToMinSize();
	this.layoutView(0, 0);
	if (spotlight != null)
	    spotlight.resize();
	this.setDirty(true);
    }
    
    public void showSpotlightHandles() {
	spotlight = new AfterBoardHandle(afterBoardView);
	this.addSubview(spotlight);
	if (this.layoutManager() != null)
	    this.layoutManager().removeSubview(spotlight);
	this.addDirtyRect(spotlight.bounds);
    }
    
    public void hideSpotlightHandles() {
	if (spotlight != null) {
	    spotlight.removeFromSuperview();
	    spotlight.discard();
	}
    }
    
    static PlaywriteView createMiniView(Rule r, Color backgroundColor) {
	BeforeAfterView bav = new BeforeAfterView(r, 16, false);
	bav.beforeBoardView.setDrawDontCares(false);
	bav.setBackgroundColor(backgroundColor);
	PlaywriteView result = Util.makeViewPictureView(bav);
	bav.discard();
	return result;
    }
    
    static Bitmap createMiniRuleImage(final Rule rule,
				      final Color backgroundColor) {
	final int miniSquareSize = 16;
	LazyBitmap.BitmapMaker bitmapMaker = new LazyBitmap.BitmapMaker() {
	    public Bitmap createBitmap() {
		BeforeAfterView bav
		    = new BeforeAfterView(rule, miniSquareSize, false);
		bav.getBeforeBoardView().setDrawDontCares(false);
		bav.setBackgroundColor(backgroundColor);
		Bitmap miniImage = Util.makeBitmapFromView(bav);
		bav.discard();
		return miniImage;
	    }
	};
	BeforeBoard beforeBoard = rule.getBeforeBoard();
	if (beforeBoard == null) {
	    beforeBoard = BeforeBoard.createFromRule(rule, 16);
	    rule.setBeforeBoard(beforeBoard);
	}
	int boardWidth = beforeBoard.numberOfColumns() * 16 + 16;
	int boardHeight = beforeBoard.numberOfRows() * 16;
	if (_littleArrow == null)
	    _littleArrow = Resource.getImage("LittleArrow");
	int width = boardWidth * 2 + 16 + _littleArrow.width();
	int height = boardHeight + 16;
	return BitmapManager.createLazyBitmapManager(bitmapMaker, width,
						     height);
    }
    
    static PlaywriteView createMiniBeforeBoard(Rule r, Color backgroundColor) {
	BeforeAfterView bav = new BeforeAfterView(r, 16, false);
	bav.beforeBoardView.setDrawDontCares(false);
	bav.setBackgroundColor(backgroundColor);
	PlaywriteView miniView = Util.makeViewPictureView(bav.beforeBoardView);
	bav.discard();
	return miniView;
    }
    
    public void setBackgroundColor(Color color) {
	if (arrowView != null)
	    arrowView.setBackgroundColor(color);
	super.setBackgroundColor(color);
    }
    
    public Size minSize() {
	return new Size((edge * 2 + 2 * beforeBoardView.width()
			 + arrowView.width()),
			edge * 2 + beforeBoardView.height());
    }
    
    public void subviewDidResize(View subview) {
	super.subviewDidResize(subview);
	if (subview == afterBoardView) {
	    this.sizeToMinSize();
	    this.layoutView(0, 0);
	    this.scrollRectToVisible(this.localBounds());
	}
	this.setDirty(true);
    }
    
    public void discard() {
	beforeBoard = null;
	afterBoard = null;
	beforeBoardView = null;
	afterBoardView = null;
	arrowView = null;
	spotlight = null;
	afterBoardView = null;
	super.discard();
    }
    
    final World getWorld() {
	return beforeBoard.getWorld();
    }
    
    final BoardView getBeforeBoardView() {
	return beforeBoardView;
    }
    
    final AfterBoardView getAfterBoardView() {
	return afterBoardView;
    }
    
    final View getArrowView() {
	return arrowView;
    }
    
    final void updateSpotlightHandles() {
	if (spotlight != null) {
	    spotlight.resize();
	    this.addDirtyRect(spotlight.bounds);
	}
    }
}
