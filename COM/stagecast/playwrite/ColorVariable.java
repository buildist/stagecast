/* ColorVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class ColorVariable extends PopupVariable
    implements ResourceIDs.ColorIDs
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108753184050L;
    private static ColorValue colorPicker
	= new ColorValue(Color.black, "NewColorID");
    private static Vector _standardColors;
    private boolean _allowTransparent;
    
    static {
	if (_standardColors == null) {
	    _standardColors = new Vector(Util.chooseColors.length + 1);
	    for (int i = 0; i < Util.chooseColors.length; i++)
		_standardColors.addElement(new ColorValue(Util.chooseColors[i],
							  (Util
							   .chooseColorStrings
							   [i])));
	}
    }
    
    public static ColorValue getColorValue(Color color) {
	ColorValue testColorValue = null;
	for (int i = 0; i < _standardColors.size(); i++) {
	    testColorValue = (ColorValue) _standardColors.elementAt(i);
	    if (testColorValue.getColor().equals(color))
		return testColorValue;
	}
	return null;
    }
    
    public ColorVariable(ResourceBundle bundle, String sysvarID,
			 VariableDirectAccessor accessor, boolean isVisible,
			 Object defaultValue) {
	super(bundle, sysvarID, accessor, (Vector) null);
	if (defaultValue != null)
	    setDefaultValue(defaultValue);
    }
    
    /**
     * @deprecated
     */
    protected ColorVariable(String sysvarID, String name, Vector colors) {
	super(sysvarID, name, (Vector) null);
	setDefaultValue(new ColorValue(Color.white, "WhiteID"));
    }
    
    protected ColorVariable(String sysvarID, String name) {
	this(sysvarID, name, (Vector) null);
    }
    
    public ColorVariable() {
	/* empty */
    }
    
    public void setDefaultValue(Object value) {
	if (value instanceof Color) {
	    Color color = (Color) value;
	    ColorValue colorValue = getColorValue(color);
	    if (colorValue == null) {
		value = new ColorValue(color, "?");
		_standardColors.addElement(value);
	    }
	}
	super.setDefaultValue(value);
    }
    
    public void setAllowTransparent() {
	_allowTransparent = true;
    }
    
    public Enumeration legalValues(VariableOwner owner) {
	Vector values = new Vector(_standardColors.size() + 1);
	ColorValue currentColor = (ColorValue) this.getValue(owner);
	values.addElements(_standardColors);
	if (currentColor != null && !_standardColors.contains(currentColor))
	    _standardColors.addElement(currentColor);
	if (_allowTransparent)
	    values.addElement(ColorValue.transparentColor);
	values.addElement(colorPicker);
	return values.elements();
    }
    
    public Object getLegalValue(VariableOwner owner, Object value) {
	if (value == Variable.UNBOUND)
	    return value;
	if (value == colorPicker) {
	    Color prevColor = ((ColorValue) this.getValue(owner)).getColor();
	    Point pt = PlaywriteRoot.getMainRootView().mousePoint();
	    Color color = Util.getColorFromChooser(owner.getWorld(), prevColor,
						   pt.x + 20, pt.y - 20);
	    if (color == null)
		color = prevColor;
	    return new ColorValue(color, "ThisColorID");
	}
	return (value instanceof ColorValue ? value
		: super.getLegalValue(owner, value));
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	throw new PlaywriteInternalError
		  ("Color lists shouldn't be written out");
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	Vector colors = ((WorldInStream) in).readVector();
    }
}
