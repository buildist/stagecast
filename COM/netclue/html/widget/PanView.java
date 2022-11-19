/* PanView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.widget;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PanView extends Container
{
    private Image osBuffer;
    private Component scene;
    private Point lastScrlPos;
    boolean dontBuffer = false;
    boolean isBrandNew = true;
    
    public PanView() {
	this(null);
    }
    
    public PanView(Component component) {
	lastScrlPos = new Point(0, 0);
	this.setLayout(null);
	addScene(component);
	this.addComponentListener(new ComponentAdapter() {
	    public void componentResized(ComponentEvent componentevent) {
		Dimension dimension = PanView.this.getSize();
		updateOSBuffer(dimension);
	    }
	});
    }
    
    public void addScene(Component component) {
	if (scene != null)
	    this.remove(scene);
	scene = component;
	this.add(component);
    }
    
    public void setBounds(int i, int i_1_, int i_2_, int i_3_) {
	super.setBounds(i, i_1_, i_2_, i_3_);
    }
    
    public void setPanPosition(int i, int i_4_) {
	if (scene != null)
	    scene.setLocation(-i, -i_4_);
    }
    
    public void doLayout() {
	if (scene != null) {
	    Dimension dimension = this.getSize();
	    Dimension dimension_5_ = scene.getPreferredSize();
	    dimension.width = Math.max(dimension.width, dimension_5_.width);
	    dimension.height = Math.max(dimension.height, dimension_5_.height);
	    scene.setSize(dimension);
	    lastScrlPos.x = lastScrlPos.y = 0;
	}
    }
    
    public void addNotify() {
	super.addNotify();
	Container container = this;
	while ((container = container.getParent()) != null) {
	    if (container instanceof PanView) {
		dontBuffer = true;
		break;
	    }
	}
    }
    
    public void update(Graphics graphics) {
	/* empty */
    }
    
    public void paint(Graphics graphics) {
	Dimension dimension = this.getSize();
	if (dimension.width > 0 && dimension.height > 0
	    && (osBuffer != null || dontBuffer)) {
	    if (scene != null && scene.isVisible()) {
		Rectangle rectangle = graphics.getClipBounds();
		Rectangle rectangle_6_ = scene.getBounds();
		Point point = scene.getLocation();
		Rectangle rectangle_7_ = new Rectangle();
		Graphics graphics_8_
		    = dontBuffer ? graphics : osBuffer.getGraphics();
		try {
		    if (!dontBuffer && !lastScrlPos.equals(point)
			&& recycleLastPaint(graphics_8_, point, rectangle_7_))
			graphics_8_.clipRect(rectangle_7_.x, rectangle_7_.y,
					     rectangle_7_.width,
					     rectangle_7_.height);
		    else
			graphics_8_.clipRect(rectangle.x, rectangle.y,
					     rectangle.width,
					     rectangle.height);
		    graphics_8_.translate(rectangle_6_.x, rectangle_6_.y);
		    scene.paint(graphics_8_);
		} finally {
		    if (!dontBuffer) {
			graphics_8_.dispose();
			graphics.drawImage(osBuffer, 0, 0, this);
		    }
		}
		lastScrlPos = point;
	    }
	}
    }
    
    private boolean recycleLastPaint(Graphics graphics, Point point,
				     Rectangle rectangle) {
	int i = point.x - lastScrlPos.x;
	int i_9_ = point.y - lastScrlPos.y;
	int i_10_ = Math.abs(i);
	int i_11_ = Math.abs(i_9_);
	Dimension dimension = this.getSize();
	if (point.x == 0 && point.y == 0 || i_10_ >= dimension.width
	    || i_11_ >= dimension.height || isBrandNew) {
	    isBrandNew = false;
	    return false;
	}
	if (i == 0 && i_11_ > 0) {
	    int i_12_ = i_9_ < 0 ? i_11_ : 0;
	    graphics.copyArea(0, i_12_, dimension.width,
			      dimension.height - i_11_, 0, i_9_);
	    Rectangle rectangle_13_ = rectangle;
	    rectangle_13_.y
		= rectangle_13_.y + (i_9_ < 0 ? dimension.height + i_9_ : 0);
	    rectangle.width = dimension.width;
	    rectangle.height = i_11_;
	    return true;
	}
	if (i_9_ == 0 && i_10_ > 0) {
	    int i_14_ = i < 0 ? i_10_ : 0;
	    graphics.copyArea(i_14_, 0, dimension.width - i_10_,
			      dimension.height, i, 0);
	    Rectangle rectangle_15_ = rectangle;
	    rectangle_15_.x
		= rectangle_15_.x + (i < 0 ? dimension.width + i : 0);
	    rectangle.width = i_10_;
	    rectangle.height = dimension.height;
	    return true;
	}
	return false;
    }
    
    void updateOSBuffer(Dimension dimension) {
	if (dimension.width >= 0 && dimension.height >= 0 && !dontBuffer) {
	    if (osBuffer == null || dimension.width != osBuffer.getWidth(this)
		|| dimension.height != osBuffer.getHeight(this)) {
		osBuffer = this.createImage(dimension.width, dimension.height);
		MediaTracker mediatracker = new MediaTracker(this);
		mediatracker.addImage(osBuffer, 0);
		try {
		    mediatracker.waitForID(0);
		} catch (Exception exception) {
		    /* empty */
		}
		isBrandNew = true;
	    }
	}
    }
}
