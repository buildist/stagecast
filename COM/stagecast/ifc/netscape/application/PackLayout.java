/* PackLayout - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public class PackLayout implements LayoutManager
{
    Hashtable viewConstraints = new Hashtable();
    Vector viewVector = new Vector();
    PackConstraints defaultConstraints;
    
    public PackConstraints defaultConstraints() {
	if (defaultConstraints == null)
	    defaultConstraints = new PackConstraints();
	return defaultConstraints;
    }
    
    public void setDefaultConstraints(PackConstraints packconstraints) {
	if (!packconstraints.equals(defaultConstraints())) {
	    int i = viewVector.count();
	    for (int i_0_ = 0; i_0_ < i; i_0_++)
		constraintsFor((View) viewVector.elementAt(i_0_));
	}
	defaultConstraints = packconstraints;
    }
    
    public PackConstraints constraintsFor(View view) {
	if (viewConstraints.get(view) == null)
	    setConstraints(view, defaultConstraints());
	return (PackConstraints) viewConstraints.get(view);
    }
    
    public void addSubview(View view) {
	viewVector.addElementIfAbsent(view);
    }
    
    public void setConstraints(View view, PackConstraints packconstraints) {
	viewVector.addElementIfAbsent(view);
	viewConstraints.put(view, packconstraints.clone());
    }
    
    public void removeSubview(View view) {
	viewConstraints.remove(view);
	viewVector.removeElement(view);
    }
    
    public void layoutView(View view, int i, int i_1_) {
	int i_2_ = 0;
	int i_3_ = 0;
	int i_4_ = view.bounds.width;
	int i_5_ = view.bounds.height;
	Vector vector = view.subviews();
	int i_6_ = vector.count();
	for (int i_7_ = 0; i_7_ < i_6_; i_7_++) {
	    View view_8_ = (View) vector.elementAt(i_7_);
	    PackConstraints packconstraints
		= (PackConstraints) viewConstraints.get(view_8_);
	    if (packconstraints == null)
		packconstraints = defaultConstraints();
	    int i_9_ = packconstraints.side();
	    int i_10_ = packconstraints.padX() * 2;
	    int i_11_ = packconstraints.padY() * 2;
	    int i_12_ = packconstraints.internalPadX();
	    int i_13_ = packconstraints.internalPadY();
	    boolean bool = packconstraints.expand();
	    boolean bool_14_ = packconstraints.fillX();
	    boolean bool_15_ = packconstraints.fillY();
	    int i_16_ = packconstraints.anchor();
	    Size size = preferredLayoutSize(view_8_);
	    int i_17_;
	    int i_18_;
	    int i_19_;
	    int i_20_;
	    if (i_9_ == 0 || i_9_ == 1) {
		i_18_ = i_4_;
		i_17_ = size.height + i_11_ + i_13_;
		if (bool)
		    i_17_ += YExpansion(view_8_, i_5_);
		i_5_ -= i_17_;
		if (i_5_ < 0) {
		    i_17_ += i_5_;
		    i_5_ = 0;
		}
		i_20_ = i_2_;
		if (i_9_ == 0) {
		    i_19_ = i_3_;
		    i_3_ += i_17_;
		} else
		    i_19_ = i_3_ + i_5_;
	    } else {
		i_17_ = i_5_;
		i_18_ = size.width + i_10_ + i_12_;
		if (bool)
		    i_18_ += XExpansion(view_8_, i_4_);
		i_4_ -= i_18_;
		if (i_4_ < 0) {
		    i_18_ += i_4_;
		    i_4_ = 0;
		}
		i_19_ = i_3_;
		if (i_9_ == 2) {
		    i_20_ = i_2_;
		    i_2_ += i_18_;
		} else
		    i_20_ = i_2_ + i_4_;
	    }
	    int i_21_ = size.width + i_12_;
	    if (bool_14_ || i_21_ > i_18_ - i_10_)
		i_21_ = i_18_ - i_10_;
	    int i_22_ = size.height + i_13_;
	    if (bool_15_ || i_22_ > i_17_ - i_11_)
		i_22_ = i_17_ - i_11_;
	    i_10_ /= 2;
	    i_11_ /= 2;
	    int i_23_;
	    int i_24_;
	    switch (i_16_) {
	    case 0:
		i_23_ = i_20_ + (i_18_ - i_21_) / 2;
		i_24_ = i_19_ + i_11_;
		break;
	    case 1:
		i_23_ = i_20_ + i_18_ - i_21_ - i_10_;
		i_24_ = i_19_ + i_11_;
		break;
	    case 2:
		i_23_ = i_20_ + i_18_ - i_21_ - i_10_;
		i_24_ = i_19_ + (i_17_ - i_22_) / 2;
		break;
	    case 3:
		i_23_ = i_20_ + i_18_ - i_21_ - i_10_;
		i_24_ = i_19_ + i_17_ - i_22_ - i_11_;
		break;
	    case 4:
		i_23_ = i_20_ + (i_18_ - i_21_) / 2;
		i_24_ = i_19_ + i_17_ - i_22_ - i_11_;
		break;
	    case 5:
		i_23_ = i_20_ + i_10_;
		i_24_ = i_19_ + i_17_ - i_22_ - i_11_;
		break;
	    case 6:
		i_23_ = i_20_ + i_10_;
		i_24_ = i_19_ + (i_17_ - i_22_) / 2;
		break;
	    case 7:
		i_23_ = i_20_ + i_10_;
		i_24_ = i_19_ + i_11_;
		break;
	    default:
		i_23_ = i_20_ + (i_18_ - i_21_) / 2;
		i_24_ = i_19_ + (i_17_ - i_22_) / 2;
	    }
	    view_8_.setBounds(i_23_, i_24_, i_21_, i_22_);
	}
    }
    
    private int XExpansion(View view, int i) {
	int i_25_ = i;
	int i_26_ = 0;
	int i_27_ = viewVector.count();
	for (int i_28_ = viewVector.indexOf(view); i_28_ < i_27_; i_28_++) {
	    View view_29_ = (View) viewVector.elementAt(i_28_);
	    PackConstraints packconstraints
		= (PackConstraints) viewConstraints.get(view_29_);
	    if (packconstraints == null)
		packconstraints = defaultConstraints();
	    int i_30_ = packconstraints.padX() * 2;
	    int i_31_ = packconstraints.internalPadX();
	    boolean bool = packconstraints.expand();
	    int i_32_ = packconstraints.side();
	    int i_33_ = preferredLayoutSize(view_29_).width + i_30_ + i_31_;
	    if (i_32_ == 0 || i_32_ == 1) {
		int i_34_ = (i - i_33_) / i_26_;
		if (i_34_ < i_25_)
		    i_25_ = i_34_;
	    } else {
		i -= i_33_;
		if (bool)
		    i_26_++;
	    }
	}
	int i_35_ = i / i_26_;
	if (i_35_ < i_25_)
	    i_25_ = i_35_;
	if (i_25_ < 0)
	    return 0;
	return i_25_;
    }
    
    private int YExpansion(View view, int i) {
	int i_36_ = i;
	int i_37_ = 0;
	int i_38_ = viewVector.count();
	for (int i_39_ = viewVector.indexOf(view); i_39_ < i_38_; i_39_++) {
	    View view_40_ = (View) viewVector.elementAt(i_39_);
	    PackConstraints packconstraints
		= (PackConstraints) viewConstraints.get(view_40_);
	    if (packconstraints == null)
		packconstraints = defaultConstraints();
	    int i_41_ = packconstraints.padY() * 2;
	    int i_42_ = packconstraints.internalPadY();
	    boolean bool = packconstraints.expand();
	    int i_43_ = packconstraints.side();
	    int i_44_ = preferredLayoutSize(view_40_).height + i_41_ + i_42_;
	    if (i_43_ == 2 || i_43_ == 3) {
		int i_45_ = (i - i_44_) / i_37_;
		if (i_45_ < i_36_)
		    i_36_ = i_45_;
	    } else {
		i -= i_44_;
		if (bool)
		    i_37_++;
	    }
	}
	int i_46_ = i / i_37_;
	if (i_46_ < i_36_)
	    i_36_ = i_46_;
	if (i_36_ < 0)
	    return 0;
	return i_36_;
    }
    
    private Rect containedRect(View view) {
	Rect rect = new Rect(0, 0, 0, 0);
	Vector vector = view.subviews();
	if (vector == null || vector.count() < 1)
	    return new Rect(view.bounds.x, view.bounds.y, view.minSize().width,
			    view.minSize().height);
	view.layoutView(0, 0);
	int i = vector.count();
	for (int i_47_ = 0; i_47_ < i; i_47_++) {
	    if (i_47_ == 0)
		rect = containedRect((View) vector.elementAt(i_47_));
	    Rect rect_48_ = containedRect((View) vector.elementAt(i_47_));
	    rect.unionWith(rect_48_);
	}
	return rect;
    }
    
    public Size preferredLayoutSize(View view) {
	Size size = view.minSize();
	if (size.width != 0 || size.height != 0)
	    return size;
	Rect rect = containedRect(view);
	return new Size(rect.x + rect.width, rect.y + rect.height);
    }
}
