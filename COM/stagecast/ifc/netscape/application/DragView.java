/* DragView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class DragView extends InternalWindow
{
    int animationCount;
    int animationDeltaX;
    int animationDeltaY;
    boolean _animatingBack;
    boolean _wasAccepted;
    Timer timer;
    DragSession session;
    
    public DragView() {
	/* empty */
    }
    
    public DragView(DragSession dragsession) {
	this.setBounds(dragsession.initialX, dragsession.initialY,
		       dragsession.image.width(), dragsession.image.height());
	session = dragsession;
	_lastX = dragsession.mouseDownX;
	_lastY = dragsession.mouseDownY;
	this.setType(0);
	this.setTransparent(dragsession.image.isTransparent());
	this.contentView().setTransparent(true);
	this.setLayer(400);
	this.setRootView(dragsession.rootView);
	this.show();
	this.rootView().setMouseView(this);
	this.draw();
    }
    
    public void performCommand(String string, Object object) {
	if ("animateRejectedDrag".equals(string))
	    animateRejectedDrag();
	else
	    super.performCommand(string, object);
    }
    
    private void animateRejectedDrag() {
	if (animationCount <= 0) {
	    timer.stop();
	    timer = null;
	    _animatingBack = false;
	    animationCount = 0;
	    animationDeltaX = 0;
	    animationDeltaY = 0;
	    stopDragging();
	} else {
	    animationCount--;
	    int i = _lastX - (animationDeltaX + bounds.x);
	    int i_0_ = _lastY - (animationDeltaY + bounds.y);
	    MouseEvent mouseevent = new MouseEvent(0L, -2, i, i_0_, 0);
	    mouseDragged(mouseevent);
	}
    }
    
    void startAnimatingRejectedDrag() {
	_animatingBack = true;
	if (_lastX - session.mouseDownX != 0
	    && _lastY - session.mouseDownY != 0) {
	    float f = (float) (_lastX - session.mouseDownX);
	    float f_1_ = (float) (_lastY - session.mouseDownY);
	    int i;
	    int i_2_;
	    if (f > f_1_) {
		i = 1 + (int) (Math.abs(f_1_) / 5.0F);
		i_2_ = (int) (Math.abs(f_1_) / (float) i);
		f /= Math.abs(f_1_);
		if (f_1_ < 0.0F)
		    f_1_ = -1.0F;
		else
		    f_1_ = 1.0F;
	    } else if (f_1_ > f) {
		i = 1 + (int) (Math.abs(f) / 5.0F);
		i_2_ = (int) (Math.abs(f) / (float) i);
		f_1_ /= Math.abs(f);
		if (f < 0.0F)
		    f = -1.0F;
		else
		    f = 1.0F;
	    } else {
		i_2_ = 0;
		i = 0;
	    }
	    if (i_2_ > 0) {
		animationCount = i_2_;
		animationDeltaX = (int) ((float) i * f);
		animationDeltaY = (int) ((float) i * f_1_);
		timer = new Timer(this, "animateRejectedDrag", 25);
		timer.start();
	    } else {
		_animatingBack = false;
		stopDragging();
	    }
	} else {
	    _animatingBack = false;
	    stopDragging();
	}
    }
    
    public boolean canBecomeMain() {
	return false;
    }
    
    public boolean canBecomeDocument() {
	return false;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	super.mouseDragged(mouseevent);
	if (!_animatingBack)
	    session.mouseDragged(mouseevent);
    }
    
    void stopDragging() {
	if (this.isVisible()) {
	    this.hide();
	    this.setBuffered(false);
	    rootView.redraw(bounds);
	}
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	super.mouseUp(mouseevent);
	session.mouseUp(mouseevent);
    }
    
    public void drawView(Graphics graphics) {
	session.image.drawAt(graphics, 0, 0);
    }
    
    public View viewForMouse(int i, int i_3_) {
	return null;
    }
}
