/* AfterBoardHandle - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.Vector;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.View;

class AfterBoardHandle extends PlaywriteView implements Flashable
{
    static final int LEFT = 1;
    static final int RIGHT = 2;
    static final int TOP = 3;
    static final int BOTTOM = 4;
    private static Bitmap LEFT_IMAGE;
    private static Bitmap RIGHT_IMAGE;
    private static Bitmap TOP_IMAGE;
    private static Bitmap BOTTOM_IMAGE;
    static Vector spotlights = new Vector(2);
    private AfterBoardView afterBoardView;
    private int edge = LEFT_IMAGE.width();
    private Rect minRect = new Rect();
    private Rect hTop;
    private Rect hBottom;
    private Rect hLeft;
    private Rect hRight;
    private Rect dirtyRedraw = new Rect();
    private int dragDirection = 0;
    private Point lastPoint = new Point(0, 0);
    private int startWidth;
    private int startHeight;
    private boolean flashing = false;
    private boolean hilited = false;
    private boolean[] flashMask;
    
    static void initStatics() {
	LEFT_IMAGE = Resource.getImage("SpotlightLeft");
	RIGHT_IMAGE = Resource.getImage("SpotlightRight");
	TOP_IMAGE = Resource.getImage("SpotlightTop");
	BOTTOM_IMAGE = Resource.getImage("SpotlightBottom");
    }
    
    AfterBoardHandle(AfterBoardView afterBoardView) {
	this.afterBoardView = afterBoardView;
	this.setBorder(null);
	this.setTransparent(true);
	spotlights.addElement(this);
	sizeToAfterBoard();
    }
    
    static Vector getSpotlights() {
	return spotlights;
    }
    
    int getSquareSize() {
	return afterBoardView.getSquareSize();
    }
    
    public void discard() {
	spotlights.removeElement(this);
	afterBoardView = null;
	super.discard();
    }
    
    public View viewForMouse(int x, int y) {
	if (hLeft.contains(x, y) || hRight.contains(x, y)
	    || hTop.contains(x, y) || hBottom.contains(x, y))
	    return this;
	return null;
    }
    
    void resize() {
	synchronized (spotlights) {
	    for (int i = 0; i < spotlights.size(); i++)
		((AfterBoardHandle) spotlights.elementAt(i))
		    .sizeToAfterBoard();
	}
    }
    
    void sizeToAfterBoard() {
	Rect myBounds = afterBoardView.bounds();
	startWidth = myBounds.width;
	startHeight = myBounds.height;
	myBounds.growBy(edge + 1, edge + 1);
	Rect oldBounds = this.bounds();
	this.setBounds(myBounds);
	hTop = new Rect(myBounds.width / 2 - TOP_IMAGE.width() / 2, 0,
			TOP_IMAGE.width(), TOP_IMAGE.height());
	hBottom = new Rect(myBounds.width / 2 - BOTTOM_IMAGE.width() / 2,
			   myBounds.height - BOTTOM_IMAGE.height(),
			   BOTTOM_IMAGE.width(), BOTTOM_IMAGE.height());
	hLeft = new Rect(0, myBounds.height / 2 - LEFT_IMAGE.height() / 2,
			 LEFT_IMAGE.width(), LEFT_IMAGE.height());
	hRight = new Rect(myBounds.width - RIGHT_IMAGE.width(),
			  myBounds.height / 2 - LEFT_IMAGE.height() / 2,
			  RIGHT_IMAGE.width(), RIGHT_IMAGE.height());
	this.setDirty(true);
    }
    
    void rearrangeHandles() {
	Rect myBounds = this.bounds();
	hTop.setBounds(myBounds.width / 2 - TOP_IMAGE.width() / 2, 0,
		       TOP_IMAGE.width(), TOP_IMAGE.height());
	hBottom.setBounds(myBounds.width / 2 - BOTTOM_IMAGE.width() / 2,
			  myBounds.height - BOTTOM_IMAGE.height(),
			  BOTTOM_IMAGE.width(), BOTTOM_IMAGE.height());
	hLeft.setBounds(0, myBounds.height / 2 - LEFT_IMAGE.height() / 2,
			LEFT_IMAGE.width(), LEFT_IMAGE.height());
	hRight.setBounds(myBounds.width - RIGHT_IMAGE.width(),
			 myBounds.height / 2 - LEFT_IMAGE.height() / 2,
			 RIGHT_IMAGE.width(), RIGHT_IMAGE.height());
    }
    
    public int cursorForPoint(int x, int y) {
	int defaultCursor = super.cursorForPoint(x, y);
	int result = defaultCursor;
	if (defaultCursor != 3) {
	    if (hLeft.contains(x, y))
		result = 10;
	    else if (hRight.contains(x, y))
		result = 11;
	    else if (hTop.contains(x, y))
		result = 8;
	    else if (hBottom.contains(x, y))
		result = 9;
	    if (result != defaultCursor
		&& afterBoardView.pointIsOffStage(x - edge, y - edge))
		result = defaultCursor;
	}
	return result;
    }
    
    public boolean mouseDown(MouseEvent event) {
	super.mouseDown(event);
	this.convertToView(this.superview(), event.x, event.y, lastPoint);
	afterBoardView.convertRectToView
	    (this.superview(),
	     ((AfterBoard) afterBoardView.getBoard())
		 .getMinRectForView(afterBoardView.getSquareSize()),
	     minRect);
	switch (cursorForPoint(event.x, event.y)) {
	case 10:
	    dragDirection = 1;
	    if (isFlashing() && flashMask[0])
		stopFlashing();
	    return true;
	case 11:
	    dragDirection = 2;
	    if (isFlashing() && flashMask[1])
		stopFlashing();
	    return true;
	case 8:
	    dragDirection = 3;
	    if (isFlashing() && flashMask[2])
		stopFlashing();
	    return true;
	case 9:
	    dragDirection = 4;
	    if (isFlashing() && flashMask[3])
		stopFlashing();
	    return true;
	default:
	    return false;
	}
    }
    
    public void mouseDragged(MouseEvent event) {
	trackMouseDragged(event);
    }
    
    public void mouseUp(MouseEvent event) {
	if (dragDirection != 0) {
	    trackMouseDragged(event);
	    AfterBoard afterBoard = (AfterBoard) afterBoardView.getBoard();
	    int sqSize = afterBoardView.getSquareSize();
	    Rect myBounds = this.bounds();
	    myBounds.growBy(-edge, -edge);
	    switch (dragDirection) {
	    case 1:
	    case 2:
		if (myBounds.width < startWidth)
		    afterBoard.expand(dragDirection,
				      (myBounds.width - startWidth
				       - sqSize / 2) / sqSize);
		else
		    afterBoard.expand(dragDirection,
				      (myBounds.width - startWidth
				       + sqSize / 2) / sqSize);
		break;
	    case 3:
	    case 4:
		if (myBounds.height < startHeight)
		    afterBoard.expand(dragDirection,
				      (myBounds.height - startHeight
				       - sqSize / 2) / sqSize);
		else
		    afterBoard.expand(dragDirection,
				      (myBounds.height - startHeight
				       + sqSize / 2) / sqSize);
		break;
	    }
	    dragDirection = 0;
	    resize();
	}
    }
    
    void trackMouseDragged(MouseEvent event) {
	Point mousePoint
	    = this.convertToView(this.superview(), event.x, event.y);
	Rect myBounds = this.bounds();
	switch (dragDirection) {
	case 1: {
	    if (mousePoint.x > minRect.x)
		mousePoint.x = minRect.x;
	    int delta = myBounds.x - mousePoint.x + edge;
	    setBounds(mousePoint.x - edge, myBounds.y, myBounds.width + delta,
		      myBounds.height);
	    hRight.x += delta;
	    hTop.x += delta;
	    hBottom.x += delta;
	    if (this.x() < myBounds.x)
		dirtyRedraw.setBounds(this.x(), myBounds.y,
				      this.width() - myBounds.width + edge + 1,
				      myBounds.height);
	    else
		dirtyRedraw.setBounds(myBounds.x, myBounds.y,
				      myBounds.width - this.width() + edge + 1,
				      myBounds.height);
	    break;
	}
	case 2:
	    if (mousePoint.x < minRect.maxX())
		mousePoint.x = minRect.maxX();
	    setBounds(myBounds.x, myBounds.y, mousePoint.x - myBounds.x + edge,
		      myBounds.height);
	    hRight.x = this.width() - edge;
	    if (this.width() > myBounds.width)
		dirtyRedraw.setBounds(myBounds.maxX() - edge - 1, myBounds.y,
				      this.width() - myBounds.width + edge + 1,
				      myBounds.height);
	    else
		dirtyRedraw.setBounds(this.bounds().maxX() - edge - 1,
				      myBounds.y,
				      myBounds.width - this.width() + edge + 1,
				      myBounds.height);
	    break;
	case 3: {
	    if (mousePoint.y > minRect.y)
		mousePoint.y = minRect.y;
	    int delta = myBounds.y - mousePoint.y + edge;
	    setBounds(myBounds.x, mousePoint.y - edge, myBounds.width,
		      myBounds.height + delta);
	    hRight.y += delta;
	    hLeft.y += delta;
	    hBottom.y += delta;
	    if (this.y() < myBounds.y)
		dirtyRedraw.setBounds(myBounds.x, this.y(), myBounds.width,
				      (this.height() - myBounds.height + edge
				       + 1));
	    else
		dirtyRedraw.setBounds(myBounds.x, myBounds.y, myBounds.width,
				      (myBounds.height - this.height() + edge
				       + 1));
	    break;
	}
	case 4:
	    if (mousePoint.y < minRect.maxY())
		mousePoint.y = minRect.maxY();
	    setBounds(myBounds.x, myBounds.y, myBounds.width,
		      mousePoint.y - myBounds.y + edge);
	    hBottom.y = this.height() - edge;
	    if (this.height() > myBounds.height)
		dirtyRedraw.setBounds(myBounds.x, myBounds.maxY() - edge - 1,
				      myBounds.width,
				      (this.height() - myBounds.height + edge
				       + 1));
	    else
		dirtyRedraw.setBounds(myBounds.x,
				      this.bounds().maxY() - edge - 1,
				      myBounds.width,
				      (myBounds.height - this.height() + edge
				       + 1));
	    break;
	}
	afterBoardView.checkDragScroll(dragDirection, mousePoint,
				       this.superview());
	this.superview().addDirtyRect(dirtyRedraw);
	lastPoint.x = mousePoint.x;
	lastPoint.y = mousePoint.y;
    }
    
    public void removeFromSuperview() {
	spotlights.removeElement(this);
	super.removeFromSuperview();
    }
    
    public void drawView(Graphics g) {
	super.drawView(g);
	LEFT_IMAGE.drawAt(g, hLeft.x, hLeft.y);
	RIGHT_IMAGE.drawAt(g, hRight.x, hRight.y);
	TOP_IMAGE.drawAt(g, hTop.x, hTop.y);
	BOTTOM_IMAGE.drawAt(g, hBottom.x, hBottom.y);
	if (isHilited()) {
	    if (flashMask == null || flashMask[0])
		Util.drawHilited(g,
				 new Rect(hLeft.x, hLeft.y, LEFT_IMAGE.width(),
					  LEFT_IMAGE.height()));
	    if (flashMask == null || flashMask[1])
		Util.drawHilited(g, new Rect(hRight.x, hRight.y,
					     RIGHT_IMAGE.width(),
					     RIGHT_IMAGE.height()));
	    if (flashMask == null || flashMask[2])
		Util.drawHilited(g, new Rect(hTop.x, hTop.y, TOP_IMAGE.width(),
					     TOP_IMAGE.height()));
	    if (flashMask == null || flashMask[3])
		Util.drawHilited(g, new Rect(hBottom.x, hBottom.y,
					     BOTTOM_IMAGE.width(),
					     BOTTOM_IMAGE.height()));
	}
	Rect r = this.localBounds();
	r.growBy(-edge, -edge);
	g.setColor(Color.orange);
	g.drawRect(r);
    }
    
    public String toString() {
	return "<AfterBoardHandles[" + this.bounds() + "]";
    }
    
    public void hilite() {
	hilited = true;
	this.setDirty(true);
	if (this.superview() instanceof BoardView)
	    this.superview().addDirtyRect(this.bounds());
    }
    
    public void unhilite() {
	hilited = false;
	this.setDirty(true);
	if (this.superview() instanceof BoardView)
	    this.superview().addDirtyRect(this.bounds());
    }
    
    public boolean isHilited() {
	return hilited;
    }
    
    public void setBounds(int x, int y, int w, int h) {
	if (this.superview() instanceof BoardView)
	    this.superview().addDirtyRect(this.bounds());
	super.setBounds(x, y, w, h);
    }
    
    public void startFlashing(boolean[] mask) {
	if (mask.length >= 4) {
	    flashMask = mask;
	    flashing = true;
	}
    }
    
    public void startFlashing() {
	startFlashing(new boolean[] { true, true, true, true });
    }
    
    public void stopFlashing() {
	unhilite();
	flashMask = null;
	flashing = false;
    }
    
    public boolean isFlashing() {
	return flashing;
    }
}
