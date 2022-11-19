/* SplitView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

class SplitView extends PlaywriteView
{
    static final int SPLIT_VERTICAL = 1;
    static final int SPLIT_HORIZONTAL = 2;
    static final int SPLITTER_DENSITY = 4;
    private Vector views;
    private Vector splitters;
    private int mode;
    private boolean startDrag;
    private Rect dragConstraints;
    private PlaywriteView target1;
    private PlaywriteView target2;
    private Rect targetSplit;
    private Color _splitterColor = Util.defaultDarkColor;
    
    SplitView(Rect bounds, int mode, PlaywriteView primary,
	      PlaywriteView secondary) {
	super(bounds);
	views = new Vector(2);
	splitters = new Vector(2);
	this.mode = mode;
	startDrag = false;
	dragConstraints = new Rect();
	if (primary != null)
	    setViewAt(0, primary);
	if (secondary != null)
	    setViewAt(1, secondary);
    }
    
    SplitView(Rect bounds, int mode) {
	this(bounds, mode, null, null);
    }
    
    SplitView(PlaywriteView primary, int mode) {
	this(primary.bounds(), mode, primary, null);
    }
    
    final int size() {
	return views.size();
    }
    
    final Enumeration elements() {
	return views.elements();
    }
    
    final Object elementAt(int ix) {
	return views.elementAt(ix);
    }
    
    final PlaywriteView viewAt(int ix) {
	return (PlaywriteView) views.elementAt(ix);
    }
    
    void changeWindowColor(Color color) {
	for (int i = 0; i < views.size(); i++) {
	    PlaywriteView view = (PlaywriteView) views.elementAt(i);
	    view.setBackgroundColor(color);
	}
	if (color.red() < 50 && color.green() < 50 && color.blue() < 50)
	    _splitterColor = Util.defaultLightColor;
	else
	    _splitterColor = color.darkerColor();
    }
    
    void setViewAt(int ix, PlaywriteView view) {
	if (ix == views.size())
	    addView(view);
	else if (view == null)
	    removeView(ix);
	else {
	    PlaywriteView oldView = (PlaywriteView) views.elementAt(ix);
	    view.setBounds(oldView.bounds());
	    oldView.removeFromSuperview();
	    oldView.discard();
	    views.setElementAt(view, ix);
	    this.addSubview(view);
	    this.addDirtyRect(view.bounds());
	}
    }
    
    double[] getSplitReal() {
	double[] v = new double[views.size()];
	for (int i = 0; i < v.length; i++) {
	    double percent;
	    if (mode == 1)
		percent = ((double) ((View) views.elementAt(i)).height()
			   / (double) this.height());
	    else
		percent = ((double) ((View) views.elementAt(i)).width()
			   / (double) this.width());
	    v[i] = percent;
	}
	return v;
    }
    
    void setSplitReal(double[] v) {
	int sum = 0;
	for (int i = 0; i < views.size() - 1; i++) {
	    PlaywriteView view = (PlaywriteView) views.elementAt(i);
	    Rect splitter = (Rect) splitters.elementAt(i);
	    double ratio = v[i];
	    if (mode == 1) {
		view.setBounds(0, sum, this.width(),
			       (int) Math.round(ratio
						* (double) this.height()));
		sum += view.height();
		splitter.y = sum;
	    } else {
		view.setBounds(sum, 0,
			       (int) Math.round(ratio * (double) this.width()),
			       this.height());
		sum += view.width();
		splitter.x = sum;
	    }
	    sum += 4;
	}
	if (mode == 1)
	    resizeVertical(this.height(), this.height(), this.width());
	else
	    resizeHorizontal(this.width(), this.width(), this.height());
    }
    
    public void setBounds(int x, int y, int width, int height) {
	if (mode == 1)
	    resizeVertical(this.height(), height, width);
	else
	    resizeHorizontal(this.width(), width, height);
	super.setBounds(x, y, width, height);
    }
    
    public void drawSubviews(Graphics g) {
	super.drawSubviews(g);
	g.setColor(_splitterColor);
	for (int i = 0; i < splitters.size(); i++)
	    g.fillRect((Rect) splitters.elementAt(i));
    }
    
    public int cursorForPoint(int x, int y) {
	int cursor = super.cursorForPoint(x, y);
	if (cursor == 3)
	    return cursor;
	for (int i = 0; i < splitters.size(); i++) {
	    if (((Rect) splitters.elementAt(i)).contains(x, y)) {
		if (PlaywriteSystem.isMRJ_2_0())
		    return 12;
		return 10;
	    }
	}
	return cursor;
    }
    
    public int splitterForPoint(int x, int y) {
	for (int i = 0; i < splitters.size(); i++) {
	    if (((Rect) splitters.elementAt(i)).contains(x, y))
		return i;
	}
	return -1;
    }
    
    public boolean mouseDown(MouseEvent event) {
	int ix = splitterForPoint(event.x, event.y);
	if (ix == -1)
	    return false;
	startDrag = true;
	target1 = (PlaywriteView) views.elementAt(ix);
	target2 = (PlaywriteView) views.elementAt(ix + 1);
	targetSplit = (Rect) splitters.elementAt(ix);
	dragConstraints.x = target1.x();
	dragConstraints.y = target1.y();
	if (mode == 1) {
	    dragConstraints.y += 4;
	    dragConstraints.width = target1.width();
	    dragConstraints.height = target1.height() + target2.height() - 4;
	} else {
	    dragConstraints.x += 4;
	    dragConstraints.width = target1.width() + target2.width() - 4;
	    dragConstraints.height = target1.height();
	}
	return true;
    }
    
    public void mouseDragged(MouseEvent event) {
	if (startDrag)
	    trackDrag(event.x, event.y, false);
	else
	    super.mouseDragged(event);
    }
    
    public void mouseUp(MouseEvent event) {
	if (startDrag) {
	    startDrag = false;
	    trackDrag(event.x, event.y, true);
	    target1 = null;
	    target2 = null;
	    targetSplit = null;
	    this.addDirtyRect(dragConstraints);
	} else
	    super.mouseDragged(event);
    }
    
    private void trackDrag(int x, int y, boolean resize) {
	if (mode == 1) {
	    if (y < dragConstraints.y)
		y = dragConstraints.y;
	    if (y > dragConstraints.y + dragConstraints.height)
		y = dragConstraints.y + dragConstraints.height;
	    int newHeight = y - target1.y() - 2;
	    this.addDirtyRect(targetSplit);
	    targetSplit.y = target1.y() + newHeight;
	    if (resize) {
		target1.sizeTo(target1.width(), newHeight);
		targetSplit.y = target1.bounds().maxY();
		target2.setBounds(target2.x(), targetSplit.y + 4,
				  target2.width(),
				  this.height() - target1.height() - 4);
	    }
	    this.addDirtyRect(targetSplit);
	} else {
	    if (x < dragConstraints.x)
		x = dragConstraints.x;
	    if (x > dragConstraints.x + dragConstraints.width)
		x = dragConstraints.x + dragConstraints.width;
	    int newWidth = x - target1.x() - 2;
	    this.addDirtyRect(targetSplit);
	    targetSplit.x = target1.x() + newWidth;
	    if (resize) {
		target1.sizeTo(newWidth, target1.height());
		targetSplit.x = target1.bounds().maxX();
		target2.setBounds(targetSplit.x + 4, target2.y(),
				  this.width() - target1.width() - 4,
				  target2.height());
	    }
	    this.addDirtyRect(targetSplit);
	}
    }
    
    private void resizeVertical(int oldHeight, int newHeight, int newWidth) {
	int ratio = oldHeight == 0 ? 100 : newHeight * 100 / oldHeight;
	int sumHeight = 0;
	for (int i = 0; i < views.size() - 1; i++) {
	    PlaywriteView view = (PlaywriteView) views.elementAt(i);
	    Rect splitter = (Rect) splitters.elementAt(i);
	    int newSize = view.height() * ratio / 100;
	    view.setBounds(view.x(), sumHeight, newWidth, newSize);
	    sumHeight += newSize;
	    splitter.y = sumHeight;
	    splitter.width = newWidth;
	    sumHeight += 4;
	}
	PlaywriteView view = (PlaywriteView) views.elementAt(views.size() - 1);
	view.setBounds(view.x(), sumHeight, newWidth, newHeight - sumHeight);
    }
    
    private void resizeHorizontal(int oldWidth, int newWidth, int newHeight) {
	int ratio = oldWidth == 0 ? 1000 : newWidth * 1000 / oldWidth;
	int sumWidth = 0;
	for (int i = 0; i < views.size() - 1; i++) {
	    PlaywriteView view = (PlaywriteView) views.elementAt(i);
	    Rect splitter = (Rect) splitters.elementAt(i);
	    int newSize = (view.width() * ratio + 500) / 1000;
	    view.setBounds(sumWidth, view.y(), newSize, newHeight);
	    sumWidth += newSize;
	    splitter.x = sumWidth;
	    splitter.height = newHeight;
	    sumWidth += 4;
	}
	if (views.size() > 0) {
	    PlaywriteView view
		= (PlaywriteView) views.elementAt(views.size() - 1);
	    view.setBounds(sumWidth, view.y(), newWidth - sumWidth, newHeight);
	}
    }
    
    private void addView(PlaywriteView newView) {
	Rect redrawBounds;
	if (views.size() == 0) {
	    redrawBounds = this.localBounds();
	    newView.setBounds(redrawBounds);
	} else {
	    PlaywriteView modifyView
		= (PlaywriteView) views.elementAt(views.size() - 1);
	    redrawBounds = modifyView.bounds();
	    Rect splitter;
	    if (mode == 1) {
		modifyView.sizeTo(redrawBounds.width,
				  (redrawBounds.height - 4) / 2);
		splitter = new Rect(modifyView.x(),
				    modifyView.y() + modifyView.height(),
				    modifyView.width(), 4);
		newView.setBounds(splitter.x, modifyView.y() + 4,
				  redrawBounds.width,
				  (redrawBounds.height - modifyView.height()
				   - 4));
	    } else {
		modifyView.sizeTo((redrawBounds.width - 4) / 2,
				  redrawBounds.height);
		splitter = new Rect(modifyView.x() + modifyView.width(),
				    modifyView.y(), 4, modifyView.height());
		newView.setBounds(splitter.x + 4, modifyView.y(),
				  redrawBounds.width - modifyView.width() - 4,
				  redrawBounds.height);
	    }
	    splitters.addElement(splitter);
	}
	views.addElement(newView);
	this.addSubview(newView);
	this.addDirtyRect(redrawBounds);
    }
    
    private void removeView(int ix) {
	PlaywriteView oldView = (PlaywriteView) views.elementAt(ix);
	oldView.removeFromSuperview();
	oldView.discard();
	PlaywriteView growView;
	if (ix == 0) {
	    if (views.size() == 1)
		growView = null;
	    else {
		growView = (PlaywriteView) views.elementAt(1);
		growView.moveTo(0, 0);
	    }
	} else
	    growView = (PlaywriteView) views.elementAt(ix - 1);
	if (growView != null) {
	    if (mode == 1)
		growView.sizeTo(oldView.width() + growView.width(),
				oldView.height() + 4);
	    else
		growView.sizeTo(oldView.width() + growView.width() + 4,
				oldView.height());
	}
	views.removeElementAt(ix);
	if (views.size() > 0) {
	    splitters.removeElementAt(ix == 0 ? 0 : ix - 1);
	    this.addDirtyRect(growView.bounds);
	}
    }
}
