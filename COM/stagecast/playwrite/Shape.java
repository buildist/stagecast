/* Shape - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;

public class Shape implements Cloneable, Externalizable, Debug.Constants
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108754691378L;
    private boolean[][] _shapeArray;
    private Point _oneBasedOrigin;
    
    public Shape(int width, int height, Point oneBasedOrigin$, boolean solid) {
	_oneBasedOrigin = new Point(-1, -1);
	_shapeArray = new boolean[width][height];
	if (getWidth() != width)
	    Debug.print("debug.shape",
			" the width " + getWidth() + " != " + width);
	if (getHeight() != height)
	    Debug.print("debug.shape",
			" the height " + getHeight() + " != " + height);
	for (int h = 0; h < width; h++) {
	    for (int v = 0; v < height; v++)
		_shapeArray[h][v] = solid;
	}
	if (oneBasedOrigin$ == null)
	    setOrigin(1, 1);
	else
	    setOrigin(oneBasedOrigin$.x, oneBasedOrigin$.y);
    }
    
    public Shape(int width, int height, Point origin$) {
	this(width, height, origin$, true);
    }
    
    public Shape() {
	_oneBasedOrigin = new Point(-1, -1);
    }
    
    final int getOriginX() {
	return _oneBasedOrigin.x;
    }
    
    final int getOriginY() {
	return _oneBasedOrigin.y;
    }
    
    final void setOriginX(int x) {
	ASSERT.isTrue(x > 0);
	ASSERT.isTrue(x <= getWidth());
	_oneBasedOrigin.x = x;
    }
    
    final void setOriginY(int y) {
	ASSERT.isTrue(y > 0);
	ASSERT.isTrue(y <= getHeight());
	_oneBasedOrigin.y = y;
    }
    
    final void setOrigin(int x, int y) {
	setOriginX(x);
	setOriginY(y);
    }
    
    final int getWidth() {
	return _shapeArray.length;
    }
    
    final int getHeight() {
	return _shapeArray[0].length;
    }
    
    public Object clone() {
	Shape clone;
	try {
	    clone = (Shape) super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    clone = null;
	}
	clone._shapeArray
	    = new boolean[_shapeArray.length][_shapeArray[0].length];
	for (int i = 0; i < _shapeArray.length; i++)
	    System.arraycopy(_shapeArray[i], 0, clone._shapeArray[i], 0,
			     _shapeArray[i].length);
	int length = _shapeArray.length;
	clone._oneBasedOrigin = new Point();
	clone.setOrigin(getOriginX(), getOriginY());
	return clone;
    }
    
    public Shape getMinimalShape() {
	Shape result = new Shape(1, 1, new Point(1, 1));
	Rect minRect = getMinimalBoundingRect();
	if (minRect == null)
	    return null;
	result.changeSize(minRect.width, minRect.height);
	int x = getOriginX();
	int y = getOriginY();
	x -= minRect.x - 1;
	y -= minRect.y - 1;
	if (x < 1 || x > minRect.width)
	    x = 1;
	if (y < 1 || y > minRect.height)
	    y = minRect.height;
	result.setOrigin(x, y);
	for (int dx = 0; dx < minRect.width; dx++) {
	    for (int dy = 0; dy < minRect.height; dy++)
		result.setLocationHV(dx + 1, dy + 1,
				     getLocationHV(minRect.x + dx,
						   minRect.y + dy));
	}
	return result;
    }
    
    public Rect getMinimalBoundingRect() {
	Rect result = null;
	for (int h = 1; h <= getWidth(); h++) {
	    for (int v = 1; v <= getHeight(); v++) {
		if (getLocationHV(h, v)) {
		    if (result == null)
			result = new Rect(h, v, 1, 1);
		    else
			result.unionWith(h, v, 1, 1);
		}
	    }
	}
	return result;
    }
    
    void setLocationHV(int h, int v, boolean flag) {
	_shapeArray[h - 1][v - 1] = flag;
    }
    
    boolean getLocationHV(int h, int v) {
	return _shapeArray[h - 1][v - 1];
    }
    
    boolean getLocationDeltaHV(int dH, int dV) {
	return _shapeArray[getOriginX() + dH - 1][getOriginY() + dV - 1];
    }
    
    void setLocationDeltaHV(int dH, int dV, boolean flag) {
	_shapeArray[getOriginX() + dH - 1][getOriginY() + dV - 1] = flag;
    }
    
    boolean getSafeLocationDeltaHV(int dH, int dV) {
	dH = dH + getOriginX() - 1;
	dV = dV + getOriginY() - 1;
	if (dH < 0 || dV < 0 || dH >= getWidth() || dV >= getHeight())
	    return false;
	return _shapeArray[dH][dV];
    }
    
    Point boardLocation(Point originHV, int h, int v) {
	return new Point(originHV.x - getOriginX() + h,
			 originHV.y - getOriginY() + v);
    }
    
    public boolean equals(Object o) {
	if (o instanceof Shape) {
	    Shape shape = (Shape) o;
	    if (shape.getOriginX() == getOriginX()
		&& shape.getOriginY() == getOriginY()
		&& shape.getWidth() == getWidth()
		&& shape.getHeight() == getHeight()) {
		for (int i = 0; i < _shapeArray.length; i++) {
		    for (int j = 0; j < _shapeArray[0].length; j++) {
			if (_shapeArray[i][j] != shape._shapeArray[i][j])
			    return false;
		    }
		}
		return true;
	    }
	}
	return false;
    }
    
    boolean isEmpty() {
	boolean sum = false;
	for (int i = 0; i < getWidth(); i++) {
	    for (int j = 0; j < getHeight(); j++) {
		if (_shapeArray[i][j])
		    return false;
	    }
	}
	return true;
    }
    
    public void changeSize(int newH, int newV) {
	int copyH = Math.min(getWidth(), newH);
	int copyV = Math.min(getHeight(), newV);
	boolean[][] newShape = new boolean[newH][newV];
	for (int h = 0; h < copyH; h++) {
	    for (int v = 0; v < copyV; v++)
		newShape[h][v] = _shapeArray[h][v];
	    for (int v = copyV; v < newV; v++)
		newShape[h][v] = false;
	}
	for (int h = copyH; h < newH; h++) {
	    for (int v = 0; v < newV; v++)
		newShape[h][v] = false;
	}
	_shapeArray = newShape;
    }
    
    void growLeftBy(int deltaWidth) {
	int newWidth = getWidth() + deltaWidth;
	ASSERT.isTrue(newWidth > 0);
	boolean[][] newShape = new boolean[newWidth][getHeight()];
	for (int v = 0; v < getHeight(); v++) {
	    for (int h = 0; h < deltaWidth; h++)
		newShape[h][v] = false;
	    for (int h = deltaWidth < 0 ? 0 : deltaWidth; h < newWidth; h++)
		newShape[h][v] = _shapeArray[h - deltaWidth][v];
	}
	_shapeArray = newShape;
	int newOriginX = getOriginX() + deltaWidth;
	newOriginX = Math.max(1, newOriginX);
	newOriginX = Math.min(newWidth, newOriginX);
	setOriginX(newOriginX);
    }
    
    void growDownBy(int deltaHeight) {
	int newHeight = getHeight() + deltaHeight;
	ASSERT.isTrue(newHeight > 0);
	boolean[][] newShape
	    = new boolean[getWidth()][getHeight() + deltaHeight];
	for (int h = 0; h < getWidth(); h++) {
	    for (int v = 0; v < deltaHeight; v++)
		newShape[h][v] = false;
	    for (int v = deltaHeight < 0 ? 0 : deltaHeight; v < newHeight; v++)
		newShape[h][v] = _shapeArray[h][v - deltaHeight];
	}
	_shapeArray = newShape;
	int newOriginY = getOriginY() + deltaHeight;
	newOriginY = Math.max(1, newOriginY);
	newOriginY = Math.min(newHeight, newOriginY);
	setOriginY(newOriginY);
    }
    
    void printShape() {
	for (int v = getHeight() - 1; v >= 0; v--) {
	    for (int h = 0; h < getWidth(); h++)
		System.out.print(_shapeArray[h][v] ? "X" : "O");
	    System.out.println("");
	}
	System.out.println("");
    }
    
    public String toString() {
	return ("Shape " + getWidth() + ", " + getHeight() + " - origin = "
		+ _oneBasedOrigin);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_shapeArray);
	setOriginX(getOriginX());
	setOriginY(getOriginY());
	out.writeObject(_shapeArray);
	out.writeInt(getOriginX());
	out.writeInt(getOriginY());
	out.writeBoolean(true);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_shapeArray = (boolean[][]) in.readObject();
	int x = in.readInt();
	int y = in.readInt();
	if (x < 1 || x > getWidth()) {
	    Debug.print(true, ("decrufting shape origin x! should be 1>=" + x
			       + ">=" + getWidth()));
	    Debug.stackTrace();
	    x = 1;
	}
	if (y < 1 || y > getHeight()) {
	    Debug.print(true, ("decrufting shape origin y! should be 1>=" + y
			       + ">=" + getHeight()));
	    Debug.stackTrace();
	    y = 1;
	}
	setOrigin(x, y);
	in.readBoolean();
    }
}
