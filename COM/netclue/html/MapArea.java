/* MapArea - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Vector;

public class MapArea
{
    Vector areaEntry;
    
    public class AreaEntry
    {
	String href;
	String target;
	Shape area;
	
	public AreaEntry(String string, String string_0_, Shape shape) {
	    href = string;
	    target = string_0_;
	    area = shape;
	}
	
	public String getReference() {
	    return href;
	}
	
	public String getTarget() {
	    return target;
	}
	
	protected Point getCenter() {
	    Rectangle rectangle = area.getBounds();
	    return new Point(rectangle.width >> 1, rectangle.height >> 1);
	}
	
	boolean contains(int i, int i_1_) {
	    if (area instanceof Rectangle && ((Rectangle) area).contains(i,
									 i_1_)
		|| area instanceof Ellipse && ((Ellipse) area).contains(i,
									i_1_)
		|| area instanceof Polygon && ((Polygon) area).contains(i,
									i_1_))
		return true;
	    return false;
	}
    }
    
    public MapArea() {
	this(10);
    }
    
    public MapArea(int i) {
	areaEntry = new Vector(i);
    }
    
    public void addEntry(String string, String string_2_, Shape shape) {
	areaEntry.addElement(new AreaEntry(string, string_2_, shape));
    }
    
    public Enumeration getEntries() {
	return areaEntry.elements();
    }
    
    public AreaEntry searchLink(int i, int i_3_) {
	Enumeration enumeration = areaEntry.elements();
	while (enumeration.hasMoreElements()) {
	    AreaEntry areaentry = (AreaEntry) enumeration.nextElement();
	    if (areaentry.contains(i, i_3_))
		return areaentry;
	}
	return null;
    }
}
