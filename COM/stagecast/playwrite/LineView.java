/* LineView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.LineBorder;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;

public class LineView extends PlaywriteView
    implements Resource.FormatCallback, Enableable
{
    private int _hMargin;
    private int _vMargin;
    private Size _minSize = new Size(_hMargin, _vMargin);
    private boolean _updateMinSize = true;
    private boolean _enabled = true;
    private Border _saveBorder;
    
    public LineView() {
	this(8);
    }
    
    public LineView(int margin) {
	_hMargin = margin;
	_vMargin = margin;
	this.setBackgroundColor(Util.testColor);
	setBorder(ScrapBorder.getTestBorder());
	this.setTransparent(false);
	PackLayout layout = new PackLayout();
	PackConstraints constraints = new PackConstraints();
	constraints.setPadX(margin);
	constraints.setPadY(margin);
	constraints.setSide(2);
	layout.setDefaultConstraints(constraints);
	this.setLayoutManager(layout);
	this.sizeToMinSize();
    }
    
    LineView(IndexedObject model$, int margin$, String formatResourceID$,
	     Object[] args$, View[] viewArgs$) {
	this(margin$);
	addViews(formatResourceID$, args$, viewArgs$);
	connectAndFinish(model$);
    }
    
    public final void appendText(String string) {
	addLabel(string.trim());
    }
    
    public final void appendObject(Object object) {
	addSubview((View) object);
    }
    
    public final void embedText(String string) {
	appendText(string);
    }
    
    public final void embedObject(Object object) {
	appendObject(object);
    }
    
    public void addViews(String formatResourceID$, Object[] args$,
			 View[] viewArgs$) {
	Resource.format(this, formatResourceID$, args$, viewArgs$);
    }
    
    public void addViews(ResourceBundle resourceBundle$,
			 String formatResourceID$, Object[] args$,
			 View[] viewArgs$) {
	Resource.format(resourceBundle$, this, formatResourceID$, args$,
			viewArgs$);
    }
    
    public void connectAndFinish(IndexedObject model$) {
	this.setModelObject(model$);
	model$.setView(this);
	this.sizeToMinSize();
    }
    
    public static Label makeLabel(String text$) {
	Label label = new Label(text$.trim(), Util.ruleFont) {
	    public View viewForMouse(int x, int y) {
		return null;
	    }
	};
	label.setColor(Util.ruleColor);
	return label;
    }
    
    public void addLabel(String text$) {
	if (!"".equals(text$))
	    addSubview(makeLabel(text$));
    }
    
    public void setEnabled(boolean enabled) {
	this.addDirtyRect(this.bounds());
	_enabled = enabled;
	if (enabled) {
	    if (_saveBorder != null)
		setBorder(_saveBorder);
	} else {
	    if (this.border() != null)
		_saveBorder = this.border();
	    setBorder(LineBorder.blackLine());
	}
	this.setTransparent(enabled ^ true);
    }
    
    public boolean isEnabled() {
	return _enabled;
    }
    
    public void setBorder(Border border) {
	if (isEnabled() || border != _saveBorder)
	    super.setBorder(border);
    }
    
    final Point getDragPoint() {
	Point dragPoint = super.getDragPoint();
	dragPoint.x = 0;
	dragPoint.y = this.height() / 2;
	return dragPoint;
    }
    
    public void mouseUp(MouseEvent event) {
	if (isEnabled())
	    this.selectModel(event);
	super.mouseUp(event);
    }
    
    public Size minSize() {
	if (!_updateMinSize)
	    return new Size(_minSize);
	int size = this.subviews().size();
	int height = 0;
	int width = 0;
	for (int i = 0; i < size; i++) {
	    View view = (View) this.subviews().elementAt(i);
	    Size vms = view.minSize();
	    int newHeight = vms.height + _vMargin * 2;
	    if (newHeight > height)
		height = newHeight;
	    width = width + vms.width + _hMargin * 2;
	}
	_minSize.height = height;
	_minSize.width = width;
	_updateMinSize = false;
	this.setMinSize(width, height);
	return new Size(width, height);
    }
    
    public void addSubview(View subview) {
	super.addSubview(subview);
	_updateMinSize = true;
    }
    
    public void subviewDidResize(View subview) {
	_updateMinSize = true;
	this.sizeToMinSize();
	this.setDirty();
	super.subviewDidResize(subview);
    }
    
    public void ancestorWasAddedToViewHierarchy(View view) {
	this.setDirty(true);
	super.ancestorWasAddedToViewHierarchy(view);
    }
}
