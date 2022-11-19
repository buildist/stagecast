/* GridLayout - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class GridLayout implements LayoutManager
{
    int rowCount;
    int columnCount;
    int horizGap;
    int vertGap;
    int flowDirection;
    public static final int FLOW_ACROSS = 0;
    public static final int FLOW_DOWN = 1;
    
    public GridLayout() {
	this(0, 0, 0, 0, 0);
    }
    
    public GridLayout(int i, int i_0_) {
	this(i, i_0_, 0, 0, 0);
    }
    
    public GridLayout(int i, int i_1_, int i_2_, int i_3_, int i_4_) {
	setRowCount(i);
	setColumnCount(i_1_);
	horizGap = i_2_;
	vertGap = i_3_;
	setFlowDirection(i_4_);
    }
    
    public void setRowCount(int i) {
	rowCount = i >= 0 ? i : 0;
    }
    
    public int rowCount() {
	return rowCount;
    }
    
    public void setColumnCount(int i) {
	columnCount = i >= 0 ? i : 0;
    }
    
    public int columnCount() {
	return columnCount;
    }
    
    public void setHorizGap(int i) {
	horizGap = i;
    }
    
    public int horizGap() {
	return horizGap;
    }
    
    public void setVertGap(int i) {
	vertGap = i;
    }
    
    public int vertGap() {
	return vertGap;
    }
    
    public void setFlowDirection(int i) {
	if (i == 0 || i == 1)
	    flowDirection = i;
	else
	    throw new InconsistencyException
		      (String.valueOf(this)
		       + "Invalid Flow direction specified: " + i);
    }
    
    public int flowDirection() {
	return flowDirection;
    }
    
    public void addSubview(View view) {
	/* empty */
    }
    
    public void removeSubview(View view) {
	/* empty */
    }
    
    public void layoutView(View view, int i, int i_5_) {
	Vector vector = view.subviews();
	int i_6_ = vector.count();
	if (i_6_ >= 1) {
	    Size size = gridSize(view);
	    int i_7_ = size.width;
	    int i_8_ = size.height;
	    int i_9_ = (view.bounds.width - horizGap * i_7_ - horizGap) / i_7_;
	    int i_10_ = (view.bounds.height - vertGap * i_8_ - vertGap) / i_8_;
	    int i_11_ = 0;
	    int i_12_ = 0;
	    if (flowDirection == 1) {
		for (int i_13_ = 0; i_13_ < i_6_; i_13_++) {
		    int i_14_ = i_9_ * i_12_ + horizGap * (i_12_ + 1);
		    int i_15_ = i_10_ * i_11_ + vertGap * (i_11_ + 1);
		    View view_16_ = (View) vector.elementAt(i_13_);
		    view_16_.setBounds(i_14_, i_15_, i_9_, i_10_);
		    i_11_++;
		    if (i_8_ > 0 && i_11_ >= i_8_) {
			i_12_++;
			i_11_ = 0;
		    }
		}
	    } else {
		for (int i_17_ = 0; i_17_ < i_6_; i_17_++) {
		    int i_18_ = i_9_ * i_12_ + horizGap * (i_12_ + 1);
		    int i_19_ = i_10_ * i_11_ + vertGap * (i_11_ + 1);
		    View view_20_ = (View) vector.elementAt(i_17_);
		    view_20_.setBounds(i_18_, i_19_, i_9_, i_10_);
		    i_12_++;
		    if (i_7_ > 0 && i_12_ >= i_7_) {
			i_11_++;
			i_12_ = 0;
		    }
		}
	    }
	}
    }
    
    public Size gridSize(View view) {
	int i = view.subviews().count();
	if (i < 1)
	    return new Size();
	int i_21_;
	int i_22_;
	if (rowCount == 0) {
	    if (columnCount == 0) {
		i_21_ = (int) Math.ceil(Math.sqrt((double) (float) i));
		i_22_ = i_21_;
	    } else {
		i_21_ = columnCount;
		i_22_ = (int) Math.ceil((double) ((float) i / (float) i_21_));
	    }
	} else if (columnCount == 0) {
	    if (rowCount == 0) {
		i_21_ = (int) Math.ceil(Math.sqrt((double) (float) i));
		i_22_ = i_21_;
	    } else {
		i_22_ = rowCount;
		i_21_ = (int) Math.ceil((double) ((float) i / (float) i_22_));
	    }
	} else {
	    i_22_ = rowCount;
	    i_21_ = columnCount;
	}
	return new Size(i_21_, i_22_);
    }
}
