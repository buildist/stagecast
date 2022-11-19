/* ScanConvertor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Vector;

public class ScanConvertor
{
    private PolygonPoint[] currentPoints;
    
    class Edge implements COM.stagecast.ifc.netscape.util.Comparable
    {
	public double x;
	public double dx;
	public int i;
	
	public int compareTo(Object other) {
	    Edge otherEdge = (Edge) other;
	    if (x < otherEdge.x)
		return -1;
	    if (x == otherEdge.x)
		return 0;
	    return 1;
	}
    }
    
    class PolygonPoint
    {
	public double x;
	public double y;
    }
    
    class SortableInteger implements COM.stagecast.ifc.netscape.util.Comparable
    {
	public int integer;
	
	public SortableInteger(int i) {
	    integer = i;
	}
	
	public int compareTo(Object other) {
	    SortableInteger otherInt = (SortableInteger) other;
	    if (currentPoints[integer].y < currentPoints[otherInt.integer].y)
		return -1;
	    if (currentPoints[integer].y == currentPoints[otherInt.integer].y)
		return 0;
	    return 1;
	}
    }
    
    private PolygonPoint[] convertPoints(Vector points) {
	PolygonPoint[] convertedVector = new PolygonPoint[points.size()];
	for (int i = 0; i < points.size(); i++) {
	    PolygonPoint p = new PolygonPoint();
	    Point currentPoint = (Point) points.elementAt(i);
	    p.x = (double) (float) currentPoint.x;
	    p.y = (double) (float) currentPoint.y;
	    convertedVector[i] = p;
	}
	return convertedVector;
    }
    
    private void deleteIndexedEdge(Vector edges, int edgeIndex) {
	boolean done = false;
	for (int i = 0; i < edges.size() && !done; i++) {
	    Edge currentEdge = (Edge) edges.elementAt(i);
	    if (currentEdge.i == edgeIndex) {
		edges.removeElement(currentEdge);
		done = true;
	    }
	}
    }
    
    private void addActiveEdge(Vector edges, PolygonPoint[] points, int i,
			       int y) {
	Edge newEdge = new Edge();
	int j = i < points.length - 1 ? i + 1 : 0;
	PolygonPoint p;
	PolygonPoint q;
	if (points[i].y < points[j].y) {
	    p = points[i];
	    q = points[j];
	} else {
	    p = points[j];
	    q = points[i];
	}
	newEdge.dx = (q.x - p.x) / (q.y - p.y);
	newEdge.x = newEdge.dx * ((double) y + 0.5 - p.y) + p.x;
	newEdge.i = i;
	edges.addElement(newEdge);
    }
    
    public Vector scanConvertConcavePolygon(Vector rawPoints) {
	Vector spanVector = new Vector();
	int n = rawPoints.size();
	if (n <= 0)
	    return spanVector;
	PolygonPoint[] points = convertPoints(rawPoints);
	currentPoints = points;
	Vector indices = new Vector();
	for (int k = 0; k < n; k++)
	    indices.addElement(new SortableInteger(k));
	indices.sort(true);
	int[] ind = new int[n];
	for (int k = 0; k < n; k++) {
	    SortableInteger item = (SortableInteger) indices.elementAt(k);
	    ind[k] = item.integer;
	}
	Vector active = new Vector();
	int k = 0;
	int y0 = (int) Math.ceil(points[ind[0]].y - 0.5);
	int y1 = (int) Math.floor(points[ind[n - 1]].y - 0.5);
	for (int y = y0; y <= y1; y++) {
	    for (/**/; k < n && points[ind[k]].y <= (double) y + 0.5; k++) {
		int i = ind[k];
		int j = i > 0 ? i - 1 : n - 1;
		if (points[j].y <= (double) y - 0.5)
		    deleteIndexedEdge(active, j);
		else if (points[j].y > (double) y + 0.5)
		    addActiveEdge(active, points, j, y);
		j = i < n - 1 ? i + 1 : 0;
		if (points[j].y <= (double) y - 0.5)
		    deleteIndexedEdge(active, i);
		else if (points[j].y > (double) y + 0.5)
		    addActiveEdge(active, points, i, y);
	    }
	    active.sort(true);
	    for (int j = 0; j < active.size() - 1; j += 2) {
		Edge currentEdge = (Edge) active.elementAt(j);
		Edge nextEdge = (Edge) active.elementAt(j + 1);
		int leftX = (int) Math.ceil(currentEdge.x - 0.5);
		int rightX = (int) Math.floor(nextEdge.x - 0.5);
		if (leftX <= rightX)
		    spanVector.addElement(new PolygonSpan(y, leftX, rightX));
		currentEdge.x += currentEdge.dx;
		nextEdge.x += nextEdge.dx;
	    }
	}
	return spanVector;
    }
}
