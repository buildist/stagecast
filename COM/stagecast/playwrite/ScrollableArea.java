/* ScrollableArea - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.image.ColorModel;
import java.awt.image.RGBImageFilter;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.Timer;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public class ScrollableArea extends PlaywriteView
    implements Target, DragDestination, ToolDestination
{
    public static int SCROLL_ARROW_WIDTH;
    private static int minWH;
    private static Hashtable scrollImageCache = new Hashtable();
    private static final int AUTOSCROLL_FREQUENCY = 100;
    private static final int AUTOSCROLL_DELAY = 500;
    private static final int AUTOSCROLL_INSET = 50;
    private static final int AUTOSCROLL_INSETx2 = 100;
    private static final int SCROLL_ARROW_REPEAT_DELAY = 10;
    static final String SCROLL_LEFT = "Left";
    static final String SCROLL_RIGHT = "Right";
    static final String SCROLL_UP = "Up";
    static final String SCROLL_DOWN = "Down";
    static final String SCROLL_UP_LEFT = "UpL";
    static final String SCROLL_UP_RIGHT = "UpR";
    static final String SCROLL_DOWN_LEFT = "DownL";
    static final String SCROLL_DOWN_RIGHT = "DownR";
    static final String PAGE_DOWN = "PgDown";
    static final String PAGE_UP = "PgUp";
    static final String PAGE_RIGHT = "PgRt";
    static final String PAGE_LEFT = "PgLft";
    static final String HOME = "Home";
    static final String END = "End";
    private static final Timer _autoScrollTimer = new Timer(null, null, 100);
    private static final Vector _autoScrollReasons = new Vector(2);
    private PlaywriteScrollView _scrollView;
    private boolean scrollableHorizontally;
    private boolean scrollableVertically;
    private int horizontalScrollAmount = 10;
    private int verticalScrollAmount = 10;
    private Vector dropTypes = new Vector(1);
    private int horizControlMargin = 0;
    private int vertControlMargin = 0;
    private boolean allowSmallContentView = true;
    private boolean inset = false;
    protected PlaywriteButton leftArrow = null;
    protected PlaywriteButton rightArrow = null;
    protected PlaywriteButton topArrow = null;
    protected PlaywriteButton bottomArrow = null;
    private Color baseColor = PlaywriteWindow.DEFAULT_BACKGROUND_COLOR;
    private Color lightColor = PlaywriteWindow.DEFAULT_BACKGROUND_COLOR;
    private Color darkColor = PlaywriteWindow.DEFAULT_BACKGROUND_COLOR;
    private Point _tempPoint = new Point();
    
    static interface AutoScrollReason
    {
    }
    
    private class ScrollArrowFilter extends RGBImageFilter
    {
	ScrollArrowFilter() {
	    canFilterIndexColorModel = true;
	}
	
	public int filterRGB(int x, int y, int rgb) {
	    ColorModel model = ColorModel.getRGBdefault();
	    int alpha = model.getAlpha(rgb);
	    int red = model.getRed(darkColor.rgb());
	    int green = model.getGreen(darkColor.rgb());
	    int blue = model.getBlue(darkColor.rgb());
	    return alpha << 24 | red << 16 | green << 8 | blue;
	}
    }
    
    static {
	_autoScrollTimer.setInitialDelay(500);
    }
    
    private static Bitmap getImage(String name) {
	Bitmap image = (Bitmap) scrollImageCache.get(name);
	if (image == null) {
	    image = Resource.getImage(name);
	    scrollImageCache.put(name, image);
	}
	return image;
    }
    
    public ScrollableArea(Rect bounds, View contentView, boolean hor,
			  boolean ver) {
	this(bounds.x, bounds.y, bounds.width, bounds.height, contentView, hor,
	     ver, true);
    }
    
    public ScrollableArea(int width, int height, View contentView, boolean hor,
			  boolean ver) {
	this(0, 0, width, height, contentView, hor, ver, true);
    }
    
    public ScrollableArea(int x, int y, int width, int height,
			  View contentView, boolean hor, boolean ver,
			  boolean redirectMouse) {
	super(x, y, width, height);
	init(contentView, hor, ver, redirectMouse);
    }
    
    private void init(View contentView, boolean hor, boolean ver,
		      boolean redirectMouse) {
	int width = this.width();
	int height = this.height();
	_scrollView
	    = new PlaywriteScrollView(hor ? SCROLL_ARROW_WIDTH : 0,
				      ver ? SCROLL_ARROW_WIDTH : 0,
				      (hor ? width - 2 * SCROLL_ARROW_WIDTH
				       : width),
				      (ver ? height - 2 * SCROLL_ARROW_WIDTH
				       : height),
				      this, redirectMouse);
	_scrollView.setContentView(contentView);
	_scrollView.setTransparent(false);
	setBackgroundColor(baseColor);
	_scrollView.addScrollBar(this);
	this.addSubview(_scrollView);
	if (hor) {
	    scrollableHorizontally = true;
	    leftArrow = makeScrollArrow("Left", "Right");
	    leftArrow.moveTo(0, 0);
	    Util.centerViewVertically(leftArrow);
	    leftArrow.setHorizResizeInstruction(0);
	    horizControlMargin += leftArrow.width();
	    rightArrow = makeScrollArrow("Right", "Left");
	    rightArrow.moveTo(width - rightArrow.width(), 0);
	    Util.centerViewVertically(rightArrow);
	    rightArrow.setHorizResizeInstruction(1);
	    horizControlMargin = horizControlMargin + rightArrow.width();
	}
	if (ver) {
	    scrollableVertically = ver;
	    topArrow = makeScrollArrow("Top", "Down");
	    topArrow.moveTo(0, 0);
	    Util.centerViewHorizontally(topArrow);
	    topArrow.setVertResizeInstruction(4);
	    vertControlMargin += topArrow.height();
	    bottomArrow = makeScrollArrow("Bottom", "Up");
	    bottomArrow.moveTo(0, height - bottomArrow.height());
	    Util.centerViewHorizontally(bottomArrow);
	    bottomArrow.setVertResizeInstruction(8);
	    vertControlMargin = vertControlMargin + bottomArrow.height();
	}
    }
    
    public final PlaywriteScrollView getScrollView() {
	return _scrollView;
    }
    
    public final View getContentView() {
	return _scrollView.contentView();
    }
    
    public final int getHorizontalScrollAmount() {
	return horizontalScrollAmount;
    }
    
    public final void setHorizontalScrollAmount(int i) {
	horizontalScrollAmount = i;
    }
    
    public final int getVerticalScrollAmount() {
	return verticalScrollAmount;
    }
    
    public final void setVerticalScrollAmount(int i) {
	verticalScrollAmount = i;
    }
    
    public final boolean getAllowSmallContentView() {
	return allowSmallContentView;
    }
    
    public final void setAllowSmallContentView(boolean b) {
	allowSmallContentView = b;
    }
    
    public final boolean getInset() {
	return inset;
    }
    
    public final void setInset(boolean b) {
	inset = b;
    }
    
    static void initStatics() {
	Bitmap rArrow = getImage("RightArrow");
	SCROLL_ARROW_WIDTH = rArrow.width() + 1;
	minWH = SCROLL_ARROW_WIDTH * 2;
    }
    
    static void addAutoScrollReason(AutoScrollReason reason) {
	_autoScrollReasons.addElementIfAbsent(reason);
    }
    
    static void removeAutoScrollReason(AutoScrollReason reason) {
	_autoScrollReasons.removeElementIdentical(reason);
    }
    
    static boolean hasAutoScrollReason() {
	return _autoScrollReasons.size() != 0;
    }
    
    private void setArrowHilite(String command, boolean on) {
	PlaywriteButton horizArrow = null;
	PlaywriteButton vertArrow = null;
	if (command == null) {
	    if (!on) {
		if (scrollableHorizontally) {
		    leftArrow.unhilite();
		    rightArrow.unhilite();
		}
		if (scrollableVertically) {
		    topArrow.unhilite();
		    bottomArrow.unhilite();
		}
	    }
	} else {
	    if (command == "Left" || command == "UpL" || command == "DownL")
		horizArrow = rightArrow;
	    else if (command == "Right" || command == "UpR"
		     || command == "DownR")
		horizArrow = leftArrow;
	    if (command == "Down" || command == "DownR" || command == "DownL")
		vertArrow = topArrow;
	    else if (command == "Up" || command == "UpL" || command == "UpR")
		vertArrow = bottomArrow;
	    if (horizArrow != null) {
		if (on)
		    horizArrow.hilite();
		else
		    horizArrow.unhilite();
	    }
	    if (vertArrow != null) {
		if (on)
		    vertArrow.hilite();
		else
		    vertArrow.unhilite();
	    }
	}
    }
    
    static void beginAutoScroll(ScrollableArea newArea, String command) {
	Target oldArea = _autoScrollTimer.target();
	if (oldArea == null || oldArea != newArea
	    || _autoScrollTimer.command() != command) {
	    cancelAutoScrolling();
	    if (command != null) {
		_autoScrollTimer.setData(_autoScrollTimer);
		_autoScrollTimer.setCommand(command);
		_autoScrollTimer.setTarget(newArea);
		_autoScrollTimer.start();
		newArea.setArrowHilite(command, true);
	    }
	}
    }
    
    static void cancelAutoScrolling() {
	ScrollableArea sa = (ScrollableArea) _autoScrollTimer.target();
	if (sa != null)
	    sa.setArrowHilite(_autoScrollTimer.command(), false);
	_autoScrollTimer.stop();
	_autoScrollTimer.setCommand(null);
	_autoScrollTimer.setTarget(null);
    }
    
    void scrollTo(int x, int y) {
	_scrollView.scrollTo(x, y);
	if (PlaywriteRoot.app().inEventThread())
	    _scrollView.draw();
    }
    
    int getContentViewHeight() {
	return getContentView().height();
    }
    
    boolean canScrollBy(int deltaX, int deltaY) {
	if (scrollableHorizontally) {
	    if (deltaX > 0) {
		if (!leftArrow.isDrawingEnabled())
		    deltaX = 0;
	    } else if (!rightArrow.isDrawingEnabled())
		deltaX = 0;
	}
	if (scrollableVertically) {
	    if (deltaY > 0) {
		if (!topArrow.isDrawingEnabled())
		    deltaY = 0;
	    } else if (!bottomArrow.isDrawingEnabled())
		deltaY = 0;
	}
	return deltaX != 0 || deltaY != 0;
    }
    
    void scrollBy(int deltaX, int deltaY) {
	_scrollView.scrollBy(deltaX, deltaY);
	if (PlaywriteRoot.app().inEventThread())
	    _scrollView.draw();
    }
    
    boolean checkArrows() {
	if (_scrollView.contentView() == null)
	    return false;
	int hpos = _scrollView.positionOfContentViewForAxis(0);
	int vpos = _scrollView.positionOfContentViewForAxis(1);
	int contentRight = _scrollView.lengthOfScrollViewForAxis(0) - hpos;
	int contentBottom = _scrollView.lengthOfScrollViewForAxis(1) - vpos;
	int contentWidth = _scrollView.lengthOfContentViewForAxis(0);
	int contentHeight = _scrollView.lengthOfContentViewForAxis(1);
	if (leftArrow != null)
	    setEnabled(leftArrow, hpos != 0);
	if (topArrow != null)
	    setEnabled(topArrow, vpos != 0);
	if (rightArrow != null)
	    setEnabled(rightArrow, contentRight < contentWidth);
	if (bottomArrow != null)
	    setEnabled(bottomArrow, contentBottom < contentHeight);
	return false;
    }
    
    protected void setEnabled(Button arrow, boolean enable) {
	if (arrow.isDrawingEnabled() != enable) {
	    if (enable)
		arrow.reenableDrawing();
	    else if (arrow.isDrawingEnabled())
		arrow.disableDrawing();
	    this.addDirtyRect(arrow.bounds());
	}
    }
    
    protected void ancestorWasAddedToViewHierarchy(View view) {
	checkArrows();
	super.ancestorWasAddedToViewHierarchy(view);
    }
    
    public void keyDown(KeyEvent keyEvent) {
	String command = null;
	switch (keyEvent.key) {
	case 1003:
	    command = "PgDown";
	    break;
	case 1002:
	    command = "PgUp";
	    break;
	case 1000:
	    command = "Home";
	    break;
	case 1001:
	    command = "End";
	    break;
	}
	if (command == null && keyEvent.isControlKeyDown()
	    && keyEvent.isArrowKey()) {
	    switch (keyEvent.key) {
	    case 1005:
		command = "PgDown";
		break;
	    case 1004:
		command = "PgUp";
		break;
	    case 1007:
		command = "PgLft";
		break;
	    case 1006:
		command = "PgRt";
		break;
	    }
	}
	if (command != null) {
	    performCommand(command, keyEvent);
	    keyEvent.key = 0;
	} else
	    super.keyDown(keyEvent);
    }
    
    public static void viewSizeForContentSize(Size size) {
	size.width += 2 * SCROLL_ARROW_WIDTH;
	size.height += 2 * SCROLL_ARROW_WIDTH;
    }
    
    public void sizeToContents() {
	View contentView = getContentView();
	if (contentView != null)
	    this.sizeTo(contentView.bounds.width + (scrollableHorizontally
						    ? 2 * SCROLL_ARROW_WIDTH
						    : 0),
			contentView.bounds.height + (scrollableVertically
						     ? 2 * SCROLL_ARROW_WIDTH
						     : 0));
    }
    
    final boolean processScrollCommand(String command, Object sender) {
	double r = 1.0;
	if (sender instanceof PlaywriteButton) {
	    PlaywriteButton sb = (PlaywriteButton) sender;
	    MouseEvent mouseEvent = sb.getLastMouseDraggedEvent();
	    if (mouseEvent != null) {
		r = Math.sqrt((double) (mouseEvent.x * mouseEvent.x
					+ mouseEvent.y * mouseEvent.y));
		r /= 30.0;
		if (r < 1.0)
		    r = 1.0;
	    }
	} else if (sender == _autoScrollTimer)
	    r *= 2.0;
	int hsa = (int) ((double) horizontalScrollAmount * r);
	int vsa = (int) ((double) verticalScrollAmount * r);
	int svh = _scrollView.height();
	int svw = _scrollView.width();
	int dx;
	int dy;
	if (command.equals("Left")) {
	    dx = -hsa;
	    dy = 0;
	} else if (command.equals("Right")) {
	    dx = hsa;
	    dy = 0;
	} else if (command.equals("Up")) {
	    dx = 0;
	    dy = -vsa;
	} else if (command.equals("Down")) {
	    dx = 0;
	    dy = vsa;
	} else if (command.equals("UpL")) {
	    dx = -hsa;
	    dy = -vsa;
	} else if (command.equals("UpR")) {
	    dx = hsa;
	    dy = -vsa;
	} else if (command.equals("DownL")) {
	    dx = -hsa;
	    dy = vsa;
	} else if (command.equals("DownR")) {
	    dx = hsa;
	    dy = vsa;
	} else if (command.equals("PgDown")) {
	    dx = 0;
	    dy = -svh;
	} else if (command.equals("PgUp")) {
	    dx = 0;
	    dy = svh;
	} else if (command.equals("PgRt")) {
	    dx = svw;
	    dy = 0;
	} else if (command.equals("PgLft")) {
	    dx = -svw;
	    dy = 0;
	} else if (command.equals("Home")) {
	    dx = 0;
	    dy = getContentViewHeight();
	} else if (command.equals("End")) {
	    dx = 0;
	    dy = -getContentViewHeight();
	} else
	    return false;
	if (canScrollBy(dx, dy))
	    scrollBy(dx, dy);
	else
	    cancelAutoScrolling();
	return true;
    }
    
    public void performCommand(String command, Object sender) {
	if (!processScrollCommand(command, sender)) {
	    if (command.equals("updateScrollValue")) {
		checkArrows();
		this.setDirty(true);
	    }
	}
    }
    
    public void setBackgroundColor(Color color) {
	super.setBackgroundColor(color);
	if (_scrollView != null)
	    _scrollView.setBackgroundColor(color);
    }
    
    void changeWindowColor(Color color, Color contentsColor) {
	baseColor = color;
	lightColor = color;
	darkColor = color;
	setBackgroundColor(contentsColor);
    }
    
    public void setBounds(int x, int y, int width, int height) {
	if (width < minWH)
	    width = minWH;
	if (height < minWH)
	    height = minWH;
	super.setBounds(x, y, width, height);
	_scrollView.setBounds(scrollableHorizontally ? SCROLL_ARROW_WIDTH : 0,
			      scrollableVertically ? SCROLL_ARROW_WIDTH : 0,
			      (scrollableHorizontally
			       ? width - 2 * SCROLL_ARROW_WIDTH : width),
			      (scrollableVertically
			       ? height - 2 * SCROLL_ARROW_WIDTH : height));
	if (!allowSmallContentView)
	    maximizeContentView();
	if (_scrollView.contentView() instanceof BoardView)
	    _scrollView.contentView().setDirty(true);
	checkArrows();
	this.setDirty(true);
    }
    
    void maximizeContentView() {
	if (!allowSmallContentView) {
	    View cv = _scrollView.contentView();
	    if (cv != null) {
		int deltaWidth = _scrollView.width() - cv.width();
		int deltaHeight = _scrollView.height() - cv.height();
		if (deltaWidth > 0 || deltaHeight > 0) {
		    cv.sizeBy(deltaWidth > 0 ? deltaWidth : 0,
			      deltaHeight > 0 ? deltaHeight : 0);
		    cv.setDirty(true);
		}
	    }
	}
    }
    
    public void setBoundsForContentBounds(int x, int y, int width,
					  int height) {
	setBounds(x, y, width + 2 * SCROLL_ARROW_WIDTH,
		  height + 2 * SCROLL_ARROW_WIDTH);
    }
    
    public boolean isTransparent() {
	return false;
    }
    
    public void drawView(Graphics g) {
	super.drawView(g);
	if (inset) {
	    Rect rect = _scrollView.bounds();
	    int x1 = rect.x - 1;
	    int x2 = rect.x + rect.width;
	    int y1 = rect.y - 1;
	    int y2 = rect.y + rect.height;
	    g.setColor(darkColor);
	    g.drawLine(x1, y1, x2, y1);
	    g.drawLine(x1, y1, x1, y2);
	    g.setColor(lightColor);
	    g.drawLine(x1, y2, x2, y2);
	    g.drawLine(x2, y1, x2, y2);
	}
    }
    
    public void discard() {
	View contentView = _scrollView.contentView();
	if (contentView instanceof ViewGlue)
	    ((ViewGlue) contentView).discard();
	super.discard();
    }
    
    private PlaywriteButton makeScrollArrow(String name, String command) {
	Bitmap buttonUp = getImage(name + "Arrow");
	Bitmap buttonDown = getImage(name + "ArrowDown");
	PlaywriteButton button
	    = PlaywriteButton.createButton(buttonUp, buttonDown, command,
					   this);
	button.setType(3);
	button.setRepeatDelay(10);
	button.setWantsAutoscrollEvents(true);
	button.setTransparent(true);
	this.addSubview(button);
	return button;
    }
    
    public DragDestination acceptsDrag(DragSession session, int x, int y) {
	return this;
    }
    
    private String getScrollCommand(Point pt) {
	if (pt == null)
	    return null;
	if (pt.x < 0 || pt.y < 0)
	    return null;
	Rect autoScrollBounds = _scrollView.bounds;
	String command = null;
	if (autoScrollBounds.width > 0 || autoScrollBounds.height > 0) {
	    if (scrollableVertically) {
		if (pt.y < autoScrollBounds.y)
		    command = "Down";
		else if (pt.y > autoScrollBounds.maxY())
		    command = "Up";
	    }
	    if (scrollableHorizontally) {
		if (pt.x < autoScrollBounds.x) {
		    command = "Right";
		    if (scrollableVertically) {
			if (pt.y < autoScrollBounds.y)
			    command = "DownR";
			else if (pt.y > autoScrollBounds.maxY())
			    command = "UpR";
		    }
		} else if (pt.x > autoScrollBounds.maxX()) {
		    command = "Left";
		    if (scrollableVertically) {
			if (pt.y < autoScrollBounds.y)
			    command = "DownL";
			else if (pt.y > autoScrollBounds.maxY())
			    command = "UpL";
		    }
		}
	    }
	}
	return command;
    }
    
    public boolean dragDropped(DragSession session) {
	cancelAutoScrolling();
	return false;
    }
    
    public boolean dragEntered(DragSession session) {
	return scrollForPoint(session);
    }
    
    public void dragExited(DragSession session) {
	cancelAutoScrolling();
    }
    
    public boolean dragMoved(DragSession session) {
	return scrollForPoint(session);
    }
    
    private boolean scrollForPoint(DragSession session) {
	return scrollForPoint(session.destinationMousePoint());
    }
    
    private boolean scrollForPoint(ToolSession session) {
	return scrollForPoint(session.destinationMousePoint());
    }
    
    private boolean scrollForPoint(Point pt) {
	String scrollCommand = getScrollCommand(pt);
	beginAutoScroll(this, scrollCommand);
	return scrollCommand != null;
    }
    
    public ToolDestination acceptsTool(ToolSession session, int x, int y) {
	return this;
    }
    
    public boolean toolEntered(ToolSession session) {
	scrollForPoint(session);
	return false;
    }
    
    public boolean toolMoved(ToolSession session) {
	scrollForPoint(session);
	return false;
    }
    
    public void toolExited(ToolSession session) {
	cancelAutoScrolling();
    }
    
    public boolean toolClicked(ToolSession session) {
	return false;
    }
    
    public void toolDragged(ToolSession session) {
	scrollForPoint(session);
    }
    
    public void toolReleased(ToolSession session) {
	cancelAutoScrolling();
    }
}
