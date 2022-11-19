/* EnumeratedVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

public abstract class EnumeratedVariable extends Variable
{
    public static final VariableDirectAccessor ENUM_ACCESSOR
	= new EnumeratedAccessor();
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108752266546L;
    
    public static class EnumeratedAccessor
	extends Variable.StandardDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    return ((EnumeratedVariable) variable).getLegalValue(owner, value);
	}
    }
    
    protected EnumeratedVariable(String sysvarID, String nameToken,
				 boolean isVisible) {
	this(sysvarID, nameToken, ENUM_ACCESSOR, isVisible);
    }
    
    protected EnumeratedVariable(String sysvarID, String nameToken,
				 VariableDirectAccessor accessor,
				 boolean isVisible) {
	super(sysvarID, nameToken, accessor, isVisible);
    }
    
    public EnumeratedVariable(ResourceBundle bundle, String sysvarID,
			      VariableDirectAccessor accessor,
			      boolean isVisible) {
	this(bundle, sysvarID, sysvarID, accessor, isVisible);
    }
    
    public EnumeratedVariable(ResourceBundle bundle, String sysvarID,
			      String nameID, VariableDirectAccessor accessor,
			      boolean isVisible) {
	super(bundle, sysvarID, nameID, accessor, isVisible);
    }
    
    EnumeratedVariable() {
	/* empty */
    }
    
    AbstractVariableEditor makeVariableEditor(VariableOwner owner,
					      ValueView.SetterGetter vsg) {
	AbstractVariableEditor editor
	    = new EnumeratedVariableEditor(owner, this, vsg);
	editor.updateContentView();
	return editor;
    }
    
    abstract String nameOf(Object object);
    
    abstract Enumeration legalValues(VariableOwner variableowner);
    
    public Enumeration alternateValues(VariableOwner owner) {
	return new Vector(1).elements();
    }
    
    public Object getLegalValue(VariableOwner owner, Object value) {
	if (value == Variable.UNBOUND || value == Variable.ILLEGAL_VALUE)
	    return value;
	value = Util.findEqualOrSameName(legalValues(owner), value);
	return value == null ? Variable.ILLEGAL_VALUE : value;
    }
}
