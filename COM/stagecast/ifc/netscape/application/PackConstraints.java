/* PackConstraints - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class PackConstraints implements Cloneable
{
    int anchor;
    boolean expand;
    boolean fillX;
    boolean fillY;
    int iPadX;
    int iPadY;
    int padX;
    int padY;
    int side;
    public static final int ANCHOR_NORTH = 0;
    public static final int ANCHOR_NORTHEAST = 1;
    public static final int ANCHOR_EAST = 2;
    public static final int ANCHOR_SOUTHEAST = 3;
    public static final int ANCHOR_SOUTH = 4;
    public static final int ANCHOR_SOUTHWEST = 5;
    public static final int ANCHOR_WEST = 6;
    public static final int ANCHOR_NORTHWEST = 7;
    public static final int ANCHOR_CENTER = 8;
    public static final int SIDE_TOP = 0;
    public static final int SIDE_BOTTOM = 1;
    public static final int SIDE_LEFT = 2;
    public static final int SIDE_RIGHT = 3;
    
    public PackConstraints() {
	anchor = 8;
	expand = false;
	fillX = false;
	fillY = false;
	iPadX = 0;
	iPadY = 0;
	padX = 0;
	padY = 0;
	side = 0;
    }
    
    public PackConstraints(int i, boolean bool, boolean bool_0_,
			   boolean bool_1_, int i_2_, int i_3_, int i_4_,
			   int i_5_, int i_6_) {
	setAnchor(i);
	setExpand(bool);
	setFillX(bool_0_);
	setFillY(bool_1_);
	setInternalPadX(i_2_);
	setInternalPadY(i_3_);
	setPadX(i_4_);
	setPadY(i_5_);
	setSide(i_6_);
    }
    
    public void setAnchor(int i) {
	if (i < 0 || i > 8)
	    throw new InconsistencyException(String.valueOf(this)
					     + "Invalid Anchor value: " + i);
	anchor = i;
    }
    
    public int anchor() {
	return anchor;
    }
    
    public void setExpand(boolean bool) {
	expand = bool;
    }
    
    public boolean expand() {
	return expand;
    }
    
    public void setFillX(boolean bool) {
	fillX = bool;
    }
    
    public boolean fillX() {
	return fillX;
    }
    
    public void setFillY(boolean bool) {
	fillY = bool;
    }
    
    public boolean fillY() {
	return fillY;
    }
    
    public void setInternalPadX(int i) {
	iPadX = i;
    }
    
    public int internalPadX() {
	return iPadX;
    }
    
    public void setInternalPadY(int i) {
	iPadY = i;
    }
    
    public int internalPadY() {
	return iPadY;
    }
    
    public void setPadX(int i) {
	padX = i;
    }
    
    public int padX() {
	return padX;
    }
    
    public void setPadY(int i) {
	padY = i;
    }
    
    public int padY() {
	return padY;
    }
    
    public void setSide(int i) {
	if (i < 0 || i > 3)
	    throw new InconsistencyException(String.valueOf(this)
					     + "Invalid Side value: " + i);
	side = i;
    }
    
    public int side() {
	return side;
    }
    
    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new InconsistencyException(String.valueOf(this)
					     + ": clone() not supported :"
					     + clonenotsupportedexception);
	}
    }
}
