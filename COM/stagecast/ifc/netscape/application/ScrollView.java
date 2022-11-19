/* ScrollView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

public class ScrollView extends View implements Scrollable
{
    View contentView;
    Color backgroundColor;
    Rect clipRect;
    Vector scrollBars;
    boolean transparent = false;
    private boolean scrollBarUpdatesEnabled = true;
    
    public ScrollView() {
	this(0, 0, 0, 0);
    }
    
    public ScrollView(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public ScrollView(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	scrollBars = new Vector();
	backgroundColor = Color.lightGray;
	this.setHorizResizeInstruction(2);
	this.setVertResizeInstruction(16);
    }
    
    public void addSubview(View view) {
	this.subviews().removeAllElements();
	super.addSubview(view);
    }
    
    public void setContentView(View view) {
	if (contentView != null)
	    contentView.removeFromSuperview();
	contentView = view;
	if (contentView != null) {
	    contentView.moveTo(0, 0);
	    addSubview(contentView);
	}
	updateScrollBars();
    }
    
    public View contentView() {
	return contentView;
    }
    
    public int cursorForPoint(int i, int i_3_) {
	if (contentView == null)
	    return 0;
	Point point = Point.newPoint(i, i_3_);
	this.convertPointToView(contentView, point, point);
	int i_4_ = contentView.cursorForPoint(point.x, point.y);
	Point.returnPoint(point);
	return i_4_;
    }
    
    public void setBackgroundColor(Color color) {
	if (color != null)
	    backgroundColor = color;
    }
    
    public Color backgroundColor() {
	return backgroundColor;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public void addScrollBar(Target target) {
	scrollBars.addElementIfAbsent(target);
    }
    
    public void removeScrollBar(Target target) {
	scrollBars.removeElement(target);
    }
    
    public void setScrollBarUpdatesEnabled(boolean bool) {
	scrollBarUpdatesEnabled = bool;
    }
    
    public boolean scrollBarUpdatesEnabled() {
	return scrollBarUpdatesEnabled;
    }
    
    public void updateScrollBars() {
	if (scrollBarUpdatesEnabled) {
	    int i = scrollBars.count();
	    for (int i_5_ = 0; i_5_ < i; i_5_++) {
		Target target = (Target) scrollBars.elementAt(i_5_);
		target.performCommand("updateScrollValue", this);
	    }
	}
    }
    
    View scrollingView() {
	return this;
    }
    
    public void scrollRectToVisible(Rect rect) {
	boolean bool = false;
	boolean bool_6_ = false;
	if (rect != null && contentView != null) {
	    int i = positionAdjustment(bounds.width, rect.width, rect.x);
	    int i_7_ = positionAdjustment(bounds.height, rect.height, rect.y);
	    if (i != 0 || i_7_ != 0)
		scrollBy(i, i_7_);
	}
    }
    
    private int positionAdjustment(int i, int i_8_, int i_9_) {
	if (i_9_ >= 0 && i_8_ + i_9_ <= i)
	    return 0;
	if (i_9_ <= 0 && i_8_ + i_9_ >= i)
	    return 0;
	if (i_9_ > 0 && i_8_ <= i)
	    return -i_9_ + i - i_8_;
	if (i_9_ >= 0 && i_8_ >= i)
	    return -i_9_;
	if (i_9_ <= 0 && i_8_ <= i)
	    return -i_9_;
	if (i_9_ < 0 && i_8_ >= i)
	    return -i_9_ + i - i_8_;
	return 0;
    }
    
    public void scrollTo(int i, int i_10_) {
	Rect rect = null;
	if (contentView != null) {
	    setClipRect(null);
	    if (i > 0 || bounds.width >= contentView.bounds.width)
		i = 0;
	    else if (i < bounds.width - contentView.bounds.width)
		i = bounds.width - contentView.bounds.width;
	    if (i_10_ > 0 || bounds.height >= contentView.bounds.height)
		i_10_ = 0;
	    else if (i_10_ < bounds.height - contentView.bounds.height)
		i_10_ = bounds.height - contentView.bounds.height;
	    int i_11_ = i - contentView.bounds.x;
	    int i_12_ = i_10_ - contentView.bounds.y;
	    if (i_11_ == 0 && i_12_ == 0)
		updateScrollBars();
	    else {
		boolean bool = this.isBuffered() && drawingBufferValid;
		if (bool) {
		    if (i_11_ != 0 && i_12_ == 0
			&& Math.abs(i_11_) < bounds.width) {
			if (i_11_ < 0)
			    rect = Rect.newRect(bounds.width + i_11_, 0,
						-i_11_, bounds.height);
			else
			    rect = Rect.newRect(0, 0, i_11_, bounds.height);
		    } else if (i_11_ == 0 && i_12_ != 0
			       && Math.abs(i_12_) < bounds.height) {
			if (i_12_ < 0)
			    rect = Rect.newRect(0, bounds.height + i_12_,
						bounds.width, -i_12_);
			else
			    rect = Rect.newRect(0, 0, bounds.width, i_12_);
		    }
		    if (rect != null) {
			contentView.moveTo(i, i_10_);
			setClipRect(rect);
			Rect.returnRect(rect);
		    } else
			contentView.moveTo(i, i_10_);
		} else
		    contentView.moveTo(i, i_10_);
		if (scrollBarUpdatesEnabled)
		    updateScrollBars();
		this.setDirty(true);
	    }
	}
    }
    
    public void scrollBy(int i, int i_13_) {
	if (contentView != null)
	    scrollTo(contentView.bounds.x + i, contentView.bounds.y + i_13_);
    }
    
    public void subviewDidResize(View view) {
	if (view == contentView) {
	    scrollBy(0, 0);
	    drawBackground();
	}
    }
    
    public void didSizeBy(int i, int i_14_) {
	super.didSizeBy(i, i_14_);
	scrollBy(0, 0);
    }
    
    void setClipRect(Rect rect) {
	if (clipRect != null)
	    Rect.returnRect(clipRect);
	if (rect != null)
	    clipRect = Rect.newRect(rect);
	else
	    clipRect = null;
    }
    
    void updateDrawingBuffer(Rect rect) {
	if (clipRect != null && this.isBuffered() && !isTransparent()) {
	    Graphics graphics = drawingBuffer.createGraphics();
	    graphics.setDebugOptions(this.shouldDebugGraphics());
	    if (clipRect.height != bounds.height) {
		if (clipRect.y == 0)
		    graphics.copyArea(clipRect.x, clipRect.y, clipRect.width,
				      bounds.height - clipRect.height,
				      clipRect.x, clipRect.maxY());
		else
		    graphics.copyArea(0, clipRect.height, clipRect.width,
				      bounds.height - clipRect.height, 0, 0);
	    } else if (clipRect.x == 0)
		graphics.copyArea(clipRect.x, clipRect.y,
				  bounds.width - clipRect.width,
				  clipRect.height, clipRect.maxX(),
				  clipRect.y);
	    else
		graphics.copyArea(clipRect.width, 0,
				  bounds.width - clipRect.width,
				  clipRect.height, 0, 0);
	    graphics.dispose();
	    Object object = null;
	    rect = new Rect(rect);
	    Rect rect_15_ = new Rect(0, 0, 0, 0);
	    contentView.getDirtyRect(rect_15_);
	    if (rect_15_.isEmpty())
		rect.intersectWith(clipRect);
	    else {
		rect_15_.unionWith(clipRect);
		rect.intersectWith(rect_15_);
	    }
	    setClipRect(null);
	}
	super.updateDrawingBuffer(rect);
    }
    
    public void computeVisibleRect(Rect rect) {
	super.computeVisibleRect(rect);
	if (clipRect != null)
	    rect.intersectWith(clipRect);
    }
    
    public void drawView(Graphics graphics) {
	if (!isTransparent()) {
	    if (contentView != null && contentView.isTransparent()) {
		graphics.setColor(backgroundColor);
		Rect rect = graphics.clipRect();
		graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
	    } else {
		int i;
		int i_16_;
		if (contentView != null) {
		    i = bounds.width - contentView.bounds.width;
		    i_16_ = bounds.height - contentView.bounds.height;
		} else {
		    i = bounds.width;
		    i_16_ = bounds.height;
		}
		if (i > 0) {
		    graphics.setColor(backgroundColor);
		    graphics.fillRect(bounds.width - i, 0, i, bounds.height);
		}
		if (i_16_ > 0) {
		    graphics.setColor(backgroundColor);
		    graphics.fillRect(0, bounds.height - i_16_, bounds.width,
				      i_16_);
		}
		if (clipRect != null && this.isBuffered()) {
		    if (clipRect.y == bounds.y)
			drawingBuffer.drawAt(graphics, clipRect.x,
					     clipRect.maxY());
		    else
			drawingBuffer.drawAt(graphics, clipRect.x,
					     -clipRect.height);
		}
	    }
	}
    }
    
    public void drawSubviews(Graphics graphics) {
	super.drawSubviews(graphics);
	setClipRect(null);
    }
    
    void drawBackground() {
	int i;
	int i_17_;
	if (contentView != null) {
	    i = bounds.width - contentView.bounds.width;
	    i_17_ = bounds.height - contentView.bounds.height;
	} else {
	    i = bounds.width;
	    i_17_ = bounds.height;
	}
	Rect rect = Rect.newRect();
	if (i > 0) {
	    rect.setBounds(bounds.width - i, 0, i, bounds.height);
	    this.addDirtyRect(rect);
	}
	if (i_17_ > 0) {
	    rect.setBounds(0, bounds.height - i_17_, bounds.width, i_17_);
	    this.addDirtyRect(rect);
	}
	Rect.returnRect(rect);
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (contentView != null) {
	    boolean bool
		= contentView.mouseDown(this.convertEventToView(contentView,
								mouseevent));
	    if (bool) {
		this.rootView().setMouseView(contentView);
		return true;
	    }
	    return false;
	}
	return false;
    }
    
    public DragDestination acceptsDrag(DragSession dragsession, int i,
				       int i_18_) {
	if (contentView != null)
	    return contentView.acceptsDrag(dragsession,
					   i - contentView.bounds.x,
					   i_18_ - contentView.bounds.y);
	return null;
    }
    
    public int lengthOfScrollViewForAxis(int i) {
	if (i == 0)
	    return bounds.width;
	return bounds.height;
    }
    
    public int lengthOfContentViewForAxis(int i) {
	if (contentView == null)
	    return 0;
	if (i == 0)
	    return contentView.bounds.width;
	return contentView.bounds.height;
    }
    
    public int positionOfContentViewForAxis(int i) {
	if (contentView == null)
	    return 0;
	if (i == 0)
	    return contentView.bounds.x;
	return contentView.bounds.y;
    }
}
