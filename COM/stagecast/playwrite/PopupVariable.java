/* PopupVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

public class PopupVariable extends EnumeratedVariable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108754560306L;
    Vector _items;
    
    static interface PopupVariableOwner extends VariableOwner
    {
	public Enumeration getLegalValues(PopupVariable popupvariable);
	
	public Object legalValueForValue(PopupVariable popupvariable,
					 Object object);
    }
    
    public PopupVariable(String sysvarID, String name, Vector items) {
	this(sysvarID, name, EnumeratedVariable.ENUM_ACCESSOR, items);
    }
    
    public PopupVariable(String sysvarID, String name,
			 VariableDirectAccessor accessor, Vector items) {
	super(sysvarID, name, accessor, true);
	_items = items;
	if (_items != null && _items.size() > 0)
	    this.setDefaultValue(_items.elementAt(0));
    }
    
    public PopupVariable(ResourceBundle bundle, String sysVarID,
			 VariableDirectAccessor accessor, Vector items) {
	this(bundle, sysVarID, sysVarID, accessor, items);
    }
    
    public PopupVariable(ResourceBundle bundle, String sysVarID, String nameID,
			 VariableDirectAccessor accessor, Vector items) {
	super(bundle, sysVarID, nameID, accessor, true);
	_items = items;
	if (_items != null && _items.size() > 0)
	    this.setDefaultValue(_items.elementAt(0));
    }
    
    public PopupVariable() {
	/* empty */
    }
    
    void updateVariableWatchers() {
	Enumeration e = legalValues(this.getListOwner());
	if (e.hasMoreElements())
	    this.setDefaultValue(e.nextElement());
	super.updateVariableWatchers();
    }
    
    String nameOf(Object value) {
	return (value instanceof Named ? ((Named) value).getName()
		: value.toString());
    }
    
    public Enumeration legalValues(VariableOwner owner) {
	if (_items == null)
	    return ((PopupVariableOwner) owner).getLegalValues(this);
	return _items.elements();
    }
    
    public Object getLegalValue(VariableOwner owner, Object value) {
	Object legalValue = null;
	if (value == Variable.UNBOUND)
	    return Variable.UNBOUND;
	if (value == null || value == Variable.ILLEGAL_VALUE)
	    return Variable.ILLEGAL_VALUE;
	if (_items == null) {
	    if (owner instanceof PopupVariableOwner)
		legalValue
		    = ((PopupVariableOwner) owner).legalValueForValue(this,
								      value);
	    if (legalValue == null)
		legalValue = super.getLegalValue(owner, value);
	    return legalValue;
	}
	if (_items != null && _items.contains(value))
	    return value;
	Object valueByName = super.getLegalValue(owner, value);
	if (valueByName == null)
	    return this.getDefaultValue(owner);
	return valueByName;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	WorldOutStream outstream = (WorldOutStream) out;
	outstream.writeVector(_items);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	WorldInStream instream = (WorldInStream) in;
	_items = instream.readVector();
    }
}
