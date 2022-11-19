/* Polygon - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.Rectangle;

public class Polygon
{
    public int numPoints;
    public int[] xPoints;
    public int[] yPoints;
    java.awt.Polygon awtPolygon;
    
    public Polygon() {
	awtPolygon = new java.awt.Polygon();
	update();
    }
    
    public Polygon(int[] is, int[] is_0_, int i) {
	awtPolygon = new java.awt.Polygon(is, is_0_, i);
	update();
    }
    
    public void addPoint(int i, int i_1_) {
	awtPolygon.addPoint(i, i_1_);
	update();
    }
    
    public Rect boundingRect() {
	Rectangle rectangle = awtPolygon.getBoundingBox();
	Rect rect = new Rect(rectangle.x, rectangle.y, rectangle.width,
			     rectangle.height);
	update();
	return rect;
    }
    
    public boolean containsPoint(int i, int i_2_) {
	boolean bool = awtPolygon.inside(i, i_2_);
	update();
	return bool;
    }
    
    public boolean containsPoint(Point point) {
	return containsPoint(point.x, point.y);
    }
    
    public void moveBy(int i, int i_3_) {
	int i_4_ = awtPolygon.npoints;
	while (i_4_-- > 0) {
	    awtPolygon.xpoints[i_4_] += i;
	    awtPolygon.ypoints[i_4_] += i_3_;
	}
    }
    
    private void update() {
	numPoints = awtPolygon.npoints;
	xPoints = awtPolygon.xpoints;
	yPoints = awtPolygon.ypoints;
    }
}
