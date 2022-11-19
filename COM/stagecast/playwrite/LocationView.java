/* LocationView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;

class LocationView extends PlaywriteView implements Watcher
{
    static final int squareSize = 8;
    private Shape _shape = null;
    private BeforeBoard _beforeBoard = null;
    private Size _boardSize = null;
    private Point _hilite = null;
    private Appearance _appearance = null;
    
    LocationView(BeforeBoard beforeBoard, CocoaCharacter ch, int x, int y) {
	super(0, 0, beforeBoard.numberOfColumns() * 8 + 1,
	      beforeBoard.numberOfRows() * 8 + 1);
	this.setMinSize(this.width(), this.height());
	sizeToBeforeBoard(beforeBoard);
	this.setBorder(null);
	_appearance = ch.getCurrentAppearance();
	this.setBackgroundColor(Util.positionBackgroundColor);
	_hilite = new Point();
	int height = beforeBoard.numberOfRows();
	_boardSize = new Size(beforeBoard.numberOfColumns(), height);
	_hilite.x = _appearance.left(x) - 1;
	_hilite.y = height - _appearance.top(y);
	_beforeBoard = beforeBoard;
	beforeBoard.addGrowthWatcher(this);
    }
    
    LocationView(BeforeBoard beforeBoard, Shape shape) {
	super(0, 0, beforeBoard.numberOfColumns() * 8 + 1,
	      beforeBoard.numberOfRows() * 8 + 1);
	this.setMinSize(this.width(), this.height());
	sizeToBeforeBoard(beforeBoard);
	this.setBackgroundColor(Util.positionBackgroundColor);
	_beforeBoard = beforeBoard;
	_shape = shape;
	beforeBoard.addGrowthWatcher(this);
    }
    
    public void sizeToBeforeBoard(BeforeBoard b) {
	int newWidth = b.numberOfColumns() * 8 + 1;
	int newHeight = b.numberOfRows() * 8 + 1;
	this.setMinSize(newWidth, newHeight);
	this.sizeTo(newWidth, newHeight);
    }
    
    public View viewForMouse(int x, int y) {
	return null;
    }
    
    public void discard() {
	if (_beforeBoard != null)
	    _beforeBoard.removeGrowthWatcher(this);
	_beforeBoard = null;
	_shape = null;
	this.setModelObject(null);
	super.discard();
    }
    
    public void drawView(Graphics g) {
	if (_shape == null)
	    drawHilitesForAppearance(g);
	else
	    drawHilitesForShape(g);
    }
    
    private void drawHilitesForAppearance(Graphics g) {
	super.drawView(g);
	int width = _appearance.getLogicalWidth();
	int height = _appearance.getLogicalHeight();
	g.setColor(Util.positionColor);
	Point loc = new Point();
	for (int dh = 1; dh <= width; dh++) {
	    for (int dv = 1; dv <= height; dv++) {
		if (_appearance.getLocationHV(dh, dv)) {
		    loc.moveTo(dh, dv);
		    Util.transformLL1ToUL0(loc, height);
		    g.fillRect((loc.x + _hilite.x) * 8,
			       (loc.y + _hilite.y) * 8, 8, 8);
		}
	    }
	}
	g.setColor(Util.positionGridColor);
	int w = this.width() - 1;
	int h = this.height() - 1;
	g.drawLine(w, 0, w, h);
	g.drawLine(0, 0, 0, h);
	g.drawLine(0, h, w, h);
	g.drawLine(0, 0, w, 0);
	for (int i = 8; i < this.width(); i += 8)
	    g.drawLine(i, 0, i, h);
	for (int i = 8; i < this.height(); i += 8)
	    g.drawLine(0, i, w, i);
    }
    
    private void drawHilitesForShape(Graphics g) {
	super.drawView(g);
	Size size = new Size(_shape.getWidth() + 1, _shape.getHeight() + 1);
	g.setColor(Util.positionColor);
	Point selfLocation = new Point(_beforeBoard.getSelfGC().getH(),
				       _beforeBoard.getSelfGC().getV());
	for (int h = 1; h < size.width; h++) {
	    for (int v = 1; v < size.height; v++) {
		if (_shape.getLocationHV(h, v)) {
		    Point square = _shape.boardLocation(selfLocation, h, v);
		    translateToXY(square);
		    g.fillRect(square.x * 8, square.y * 8, 8, 8);
		}
	    }
	}
	g.setColor(Util.positionGridColor);
	int w = this.width() - 1;
	int h = this.height() - 1;
	g.drawLine(w, 0, w, h);
	g.drawLine(0, 0, 0, h);
	g.drawLine(0, h, w, h);
	g.drawLine(0, 0, w, 0);
	for (int i = 8; i < this.width(); i += 8)
	    g.drawLine(i, 0, i, h);
	for (int i = 8; i < this.height(); i += 8)
	    g.drawLine(0, i, w, i);
    }
    
    private void translateToXY(Point p) {
	p.x = p.x - 1;
	p.y = _beforeBoard.numberOfRows() - p.y;
    }
    
    public void update(Object target, Object value) {
	if (_hilite != null) {
	    int dx
		= ((BeforeBoard) target).numberOfColumns() - _boardSize.width;
	    int dy = ((BeforeBoard) target).numberOfRows() - _boardSize.height;
	    if (value == BeforeBoard.UP)
		_hilite.y += dy;
	    else if (value == BeforeBoard.LEFT)
		_hilite.x += dx;
	    _boardSize.sizeBy(dx, dy);
	}
	sizeToBeforeBoard((BeforeBoard) target);
	this.superview().layoutView(0, 0);
	this.superview().setDirty(true);
    }
    
    String getInfo() {
	return ("LocationView has size " + this.width() / 8 + ","
		+ this.height() / 8 + " with coord " + _hilite);
    }
    
    public String toString() {
	String s = "LocationView " + super.toString();
	return s;
    }
}
