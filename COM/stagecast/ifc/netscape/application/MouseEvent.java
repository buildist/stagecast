/* MouseEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class MouseEvent extends Event
{
    public int x;
    public int y;
    int clickCount;
    int modifiers;
    public static final int MOUSE_DOWN = -1;
    public static final int MOUSE_DRAGGED = -2;
    public static final int MOUSE_UP = -3;
    public static final int MOUSE_ENTERED = -4;
    public static final int MOUSE_MOVED = -5;
    public static final int MOUSE_EXITED = -6;
    
    public MouseEvent() {
	/* empty */
    }
    
    public MouseEvent(long l, int i, int i_0_, int i_1_, int i_2_) {
	this();
	timeStamp = l;
	if (i < -6 || i > -1)
	    throw new IllegalArgumentException("Invalid MouseEvent type: "
					       + i);
	type = i;
	x = i_0_;
	y = i_1_;
	modifiers = i_2_;
    }
    
    public void setClickCount(int i) {
	clickCount = i;
    }
    
    public int clickCount() {
	return clickCount;
    }
    
    public void setModifiers(int i) {
	modifiers = i;
    }
    
    public int modifiers() {
	return modifiers;
    }
    
    public boolean isShiftKeyDown() {
	return (modifiers & 0x1) != 0;
    }
    
    public boolean isControlKeyDown() {
	return (modifiers & 0x2) != 0;
    }
    
    public boolean isMetaKeyDown() {
	return (modifiers & 0x4) != 0;
    }
    
    public boolean isAltKeyDown() {
	return (modifiers & 0x8) != 0;
    }
    
    public void setRootView(RootView rootview) {
	processor = rootview;
    }
    
    public RootView rootView() {
	return (RootView) processor;
    }
    
    public String toString() {
	String string;
	switch (type) {
	case -1:
	    string = "Down";
	    break;
	case -2:
	    string = "Dragged";
	    break;
	case -3:
	    string = "Up";
	    break;
	case -4:
	    string = "Entered";
	    break;
	case -5:
	    string = "Moved";
	    break;
	case -6:
	    string = "Exited";
	    break;
	default:
	    string = "Unknown Type";
	}
	return ("MouseEvent: " + string + " at: (" + x + "," + y + ")"
		+ " modifiers: " + modifiers + " clicks: " + clickCount);
    }
}
