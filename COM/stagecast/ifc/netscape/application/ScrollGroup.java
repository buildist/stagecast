/* ScrollGroup - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

public class ScrollGroup extends View implements ScrollBarOwner
{
    ScrollView scrollView;
    ScrollBar vertScrollBar;
    ScrollBar horizScrollBar;
    Border border = EmptyBorder.emptyBorder();
    Border interiorBorder = new ScrollViewLineBorder();
    Color cornerColor;
    int horizScrollDisplay;
    int vertScrollDisplay;
    private boolean ignoreScrollBars;
    private static final boolean debugScrollers = false;
    public static final int NEVER_DISPLAY = 0;
    public static final int ALWAYS_DISPLAY = 1;
    public static final int AS_NEEDED_DISPLAY = 2;
    
    public ScrollGroup() {
	this(0, 0, 0, 0);
    }
    
    public ScrollGroup(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public ScrollGroup(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	scrollView = createScrollView();
	this.addSubview(scrollView);
	horizScrollBar = createScrollBar(true);
	horizScrollBar.setScrollableObject(scrollView);
	horizScrollBar.setScrollBarOwner(this);
	scrollView.addScrollBar(horizScrollBar);
	this.addSubview(horizScrollBar);
	vertScrollBar = createScrollBar(false);
	vertScrollBar.setScrollableObject(scrollView);
	vertScrollBar.setScrollBarOwner(this);
	scrollView.addScrollBar(vertScrollBar);
	this.addSubview(vertScrollBar);
	ignoreScrollBars = false;
	setCornerColor(Color.lightGray);
	layoutView(0, 0);
    }
    
    public Size minSize() {
	boolean bool = false;
	boolean bool_3_ = false;
	Size size = super.minSize();
	if (size != null && (size.width != 0 || size.height != 0))
	    return size;
	if (vertScrollDisplay == 0 && horizScrollDisplay == 0)
	    return new Size(border.widthMargin(), border.heightMargin());
	if (vertScrollDisplay != 0 && horizScrollDisplay != 0)
	    return new Size((border.widthMargin()
			     + horizScrollBar().minSize().width
			     + vertScrollBar().minSize().width),
			    (border.heightMargin()
			     + horizScrollBar().minSize().height
			     + vertScrollBar().minSize().height));
	if (horizScrollDisplay == 1 || horizScrollDisplay == 2)
	    return new Size((border.widthMargin()
			     + horizScrollBar().minSize().width),
			    (border.heightMargin()
			     + horizScrollBar().minSize().height * 2));
	if (vertScrollDisplay == 1 || vertScrollDisplay == 2)
	    return new Size((border.widthMargin()
			     + vertScrollBar().minSize().width * 2),
			    (border.heightMargin()
			     + vertScrollBar().minSize().height));
	return super.minSize();
    }
    
    public boolean isTransparent() {
	Vector vector = this.subviews();
	int i = vector.count();
	for (int i_4_ = 0; i_4_ < i; i_4_++) {
	    if (((View) this.subviews().elementAt(i_4_)).isTransparent())
		return true;
	}
	return false;
    }
    
    public void setBorder(Border border) {
	if (border == null)
	    border = EmptyBorder.emptyBorder();
	interiorBorder = border;
	layoutView(0, 0);
    }
    
    public Border border() {
	return interiorBorder;
    }
    
    protected ScrollView createScrollView() {
	return new ScrollView(0, 0, bounds.width, bounds.height);
    }
    
    public ScrollView scrollView() {
	return scrollView;
    }
    
    protected ScrollBar createScrollBar(boolean bool) {
	ScrollBar scrollbar;
	if (bool)
	    scrollbar = new ScrollBar(0, 0, bounds.width, 1, 0);
	else
	    scrollbar = new ScrollBar(0, 0, 1, bounds.height, 1);
	return scrollbar;
    }
    
    public void setHasVertScrollBar(boolean bool) {
	if (bool)
	    setVertScrollBarDisplay(1);
	else
	    setVertScrollBarDisplay(0);
    }
    
    public boolean hasVertScrollBar() {
	return vertScrollDisplay == 1;
    }
    
    public void setVertScrollBarDisplay(int i) {
	vertScrollDisplay = i;
	if (vertScrollBar == null) {
	    vertScrollBar = createScrollBar(false);
	    vertScrollBar.setScrollableObject(scrollView);
	    vertScrollBar.setScrollBarOwner(this);
	    scrollView.addScrollBar(vertScrollBar);
	    this.addSubview(vertScrollBar);
	}
	layoutView(0, 0);
    }
    
    public int vertScrollBarDisplay() {
	return vertScrollDisplay;
    }
    
    public ScrollBar vertScrollBar() {
	return vertScrollBar;
    }
    
    public void setHasHorizScrollBar(boolean bool) {
	if (bool)
	    setHorizScrollBarDisplay(1);
	else
	    setHorizScrollBarDisplay(0);
    }
    
    public boolean hasHorizScrollBar() {
	return horizScrollDisplay == 1;
    }
    
    public void setHorizScrollBarDisplay(int i) {
	horizScrollDisplay = i;
	if (horizScrollBar == null) {
	    horizScrollBar = createScrollBar(true);
	    horizScrollBar.setScrollableObject(scrollView);
	    horizScrollBar.setScrollBarOwner(this);
	    scrollView.addScrollBar(horizScrollBar);
	    this.addSubview(horizScrollBar);
	}
	layoutView(0, 0);
    }
    
    public int horizScrollBarDisplay() {
	return horizScrollDisplay;
    }
    
    public ScrollBar horizScrollBar() {
	return horizScrollBar;
    }
    
    private void putParts() {
	horizScrollBar.removeFromSuperview();
	vertScrollBar.removeFromSuperview();
	if (scrollView != null) {
	    Rect rect = new Rect(0, 0, 0, 0);
	    if (horizScrollBar != null
		&& horizScrollBar.scrollableObject() != null)
		rect.width = horizScrollBar.scrollableObject()
				 .lengthOfContentViewForAxis(0);
	    else if (scrollView.contentView != null)
		rect.width = scrollView.contentView.bounds.width;
	    if (vertScrollBar != null
		&& vertScrollBar.scrollableObject() != null)
		rect.height = vertScrollBar.scrollableObject()
				  .lengthOfContentViewForAxis(1);
	    else if (scrollView.contentView != null)
		rect.height = scrollView.contentView.bounds.height;
	    int i = border.leftMargin() + interiorBorder.leftMargin();
	    int i_5_ = border.topMargin() + interiorBorder.topMargin();
	    int i_6_ = (bounds.width - border.widthMargin()
			- interiorBorder.widthMargin());
	    int i_7_ = (bounds.height - border.heightMargin()
			- interiorBorder.heightMargin());
	    ignoreScrollBars = true;
	    scrollView.moveTo(i, i_5_);
	    scrollView.sizeTo(i_6_, i_7_);
	    vertScrollBar.moveTo(i + i_6_ - vertScrollBar.bounds.width, i_5_);
	    vertScrollBar.sizeTo(vertScrollBar.bounds.width, i_7_);
	    horizScrollBar.moveTo(i,
				  i_5_ + i_7_ - horizScrollBar.bounds.height);
	    horizScrollBar.sizeTo(i_6_, horizScrollBar.bounds.height);
	    ignoreScrollBars = false;
	    if (vertScrollDisplay == 1 && horizScrollDisplay == 1
		|| (vertScrollDisplay == 2 && horizScrollDisplay == 2
		    && scrollView.bounds.width < rect.width
		    && scrollView.bounds.height < rect.height))
		setBothScrollersOn();
	    else if (vertScrollDisplay == 1
		     || (vertScrollDisplay == 2
			 && scrollView.bounds.height < rect.height)) {
		if (horizScrollDisplay == 1
		    || (horizScrollDisplay == 2
			&& (scrollView.bounds.width - rect.width
			    < vertScrollBar.bounds.width)))
		    setBothScrollersOn();
		else {
		    horizScrollBar.removeFromSuperview();
		    ignoreScrollBars = true;
		    scrollView.moveTo(i, i_5_);
		    scrollView.sizeTo(i_6_ - vertScrollBar.bounds.width, i_7_);
		    vertScrollBar.moveTo((bounds.width - border.rightMargin()
					  - vertScrollBar.bounds.width),
					 border.topMargin());
		    vertScrollBar.sizeTo(vertScrollBar.bounds.width,
					 (bounds.height
					  - border.heightMargin()));
		    this.addSubview(vertScrollBar);
		    ignoreScrollBars = false;
		}
	    } else if (vertScrollDisplay == 2 && horizScrollDisplay == 2
		       && scrollView.bounds.height == rect.height) {
		ignoreScrollBars = true;
		vertScrollBar.removeFromSuperview();
		horizScrollBar.removeFromSuperview();
		scrollView.moveTo(i, i_5_);
		scrollView.sizeTo(i_6_, i_7_);
		ignoreScrollBars = false;
	    } else if (horizScrollDisplay == 1
		       || (horizScrollDisplay == 2
			   && scrollView.bounds.width < rect.width)) {
		if (vertScrollDisplay == 1
		    || (vertScrollDisplay == 2
			&& (scrollView.bounds.height - rect.height
			    < horizScrollBar.bounds.height)))
		    setBothScrollersOn();
		else {
		    vertScrollBar.removeFromSuperview();
		    ignoreScrollBars = true;
		    scrollView.moveTo(i, i_5_);
		    scrollView.sizeTo(i_6_,
				      i_7_ - horizScrollBar.bounds.height);
		    horizScrollBar.moveTo(border.leftMargin(),
					  (bounds.height
					   - border.bottomMargin()
					   - horizScrollBar.bounds.height));
		    horizScrollBar.sizeTo(bounds.width - border.widthMargin(),
					  horizScrollBar.bounds.height);
		    this.addSubview(horizScrollBar);
		    ignoreScrollBars = false;
		}
	    } else if (horizScrollDisplay == 2 && vertScrollDisplay == 2
		       && scrollView.bounds.width == rect.width) {
		ignoreScrollBars = true;
		horizScrollBar.removeFromSuperview();
		vertScrollBar.removeFromSuperview();
		scrollView.moveTo(i, i_5_);
		scrollView.sizeTo(i_6_, i_7_);
		ignoreScrollBars = false;
	    }
	}
    }
    
    void setBothScrollersOn() {
	int i = border.leftMargin() + interiorBorder.leftMargin();
	int i_8_ = border.topMargin() + interiorBorder.topMargin();
	int i_9_ = (bounds.width - border.widthMargin()
		    - interiorBorder.widthMargin());
	int i_10_ = (bounds.height - border.heightMargin()
		     - interiorBorder.heightMargin());
	ignoreScrollBars = true;
	scrollView.moveTo(i, i_8_);
	scrollView.sizeTo(i_9_ - vertScrollBar.bounds.width,
			  i_10_ - horizScrollBar.bounds.height);
	horizScrollBar.moveTo(border.leftMargin(),
			      (bounds.height - border.bottomMargin()
			       - horizScrollBar.bounds.height));
	horizScrollBar.sizeTo((bounds.width - border.widthMargin()
			       - vertScrollBar.bounds.width + 2),
			      horizScrollBar.bounds.height);
	this.addSubview(horizScrollBar);
	vertScrollBar.moveTo((bounds.width - border.rightMargin()
			      - vertScrollBar.bounds.width),
			     border.topMargin());
	vertScrollBar.sizeTo(vertScrollBar.bounds.width,
			     (bounds.height - border.heightMargin()
			      - horizScrollBar.bounds.height + 2));
	this.addSubview(vertScrollBar);
	ignoreScrollBars = false;
    }
    
    public void drawView(Graphics graphics) {
	border.drawInRect(graphics, 0, 0, bounds.width, bounds.height);
	interiorBorder.drawInRect(graphics, border.leftMargin(),
				  border.topMargin(),
				  (scrollView.bounds.width
				   + interiorBorder.widthMargin()),
				  (scrollView.bounds.height
				   + interiorBorder.heightMargin()));
	if (cornerColor != null && horizScrollBarIsVisible()
	    && vertScrollBarIsVisible()) {
	    graphics.setColor(cornerColor);
	    graphics.fillRect(horizScrollBar.bounds.maxX(),
			      vertScrollBar.bounds.maxY(),
			      vertScrollBar.bounds.width - 2,
			      horizScrollBar.bounds.height - 2);
	}
    }
    
    public void drawContents() {
	scrollView.setDirty(true);
    }
    
    public void drawSubviews(Graphics graphics) {
	super.drawSubviews(graphics);
	drawView(graphics);
    }
    
    public void setContentView(View view) {
	scrollView.setContentView(view);
	layoutView(0, 0);
    }
    
    public View contentView() {
	return scrollView.contentView();
    }
    
    public void setBackgroundColor(Color color) {
	scrollView.setBackgroundColor(color);
    }
    
    public void setCornerColor(Color color) {
	cornerColor = color;
    }
    
    public void scrollBarDidBecomeActive(ScrollBar scrollbar) {
	if (!ignoreScrollBars
	    && (scrollbar != horizScrollBar || horizScrollDisplay != 0)
	    && (scrollbar != vertScrollBar || vertScrollDisplay != 0))
	    layoutView(0, 0);
    }
    
    public void scrollBarDidBecomeInactive(ScrollBar scrollbar) {
	if (!ignoreScrollBars
	    && (scrollbar != horizScrollBar || horizScrollDisplay != 0)
	    && (scrollbar != vertScrollBar || vertScrollDisplay != 0))
	    layoutView(0, 0);
    }
    
    public void scrollBarWasEnabled(ScrollBar scrollbar) {
	if (!ignoreScrollBars
	    && (scrollbar != horizScrollBar || horizScrollDisplay != 0)
	    && (scrollbar != vertScrollBar || vertScrollDisplay != 0))
	    layoutView(0, 0);
    }
    
    public void scrollBarWasDisabled(ScrollBar scrollbar) {
	if (!ignoreScrollBars
	    && (scrollbar != horizScrollBar || horizScrollDisplay != 0)
	    && (scrollbar != vertScrollBar || vertScrollDisplay != 0))
	    layoutView(0, 0);
    }
    
    public boolean vertScrollBarIsVisible() {
	return vertScrollBar.isInViewHierarchy();
    }
    
    public boolean horizScrollBarIsVisible() {
	return horizScrollBar.isInViewHierarchy();
    }
    
    public void layoutView(int i, int i_11_) {
	putParts();
	ignoreScrollBars = false;
	this.setDirty(true);
    }
}
