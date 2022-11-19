/* AppearanceVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Enumeration;

public class AppearanceVariable extends EnumeratedVariable
{
    private static final VariableDirectAccessor CURRENT_APPEARANCE_ACCESSOR
	= new CurrentAppearanceAccessor();
    private static final VariableDirectAccessor APPEARANCE_ACCESSOR
	= new AppearanceAccessor();
    
    public static class CurrentAppearanceAccessor extends AppearanceAccessor
    {
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    Object actualOldValue = variable.getActualValue(owner);
	    Appearance oldAppearance = (Appearance) variable.getValue(owner);
	    super.setDirectValue(variable, owner, value);
	    Appearance newAppearance = (Appearance) variable.getValue(owner);
	    CocoaCharacter ch = (CocoaCharacter) owner;
	    if (oldAppearance != newAppearance
		|| actualOldValue == Variable.UNBOUND) {
		if (oldAppearance != null)
		    oldAppearance.undisplayItemsOn(ch);
		ch.appearanceChanged(oldAppearance, newAppearance);
		if (newAppearance != null)
		    newAppearance.displayItemsOn(ch);
	    }
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    value = super.constrainDirectValue(variable, owner, value);
	    if (value == null)
		return Variable.ILLEGAL_VALUE;
	    return value;
	}
    }
    
    public static class AppearanceAccessor
	extends Variable.StandardDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    CocoaCharacter ch = (CocoaCharacter) owner;
	    if (value == null)
		return null;
	    if (value instanceof Number)
		value = value.toString();
	    if (value instanceof String)
		value = ch.getAppearanceNamed((String) value);
	    if (value == null || !(value instanceof Appearance))
		return Variable.ILLEGAL_VALUE;
	    Appearance newAppearance = (Appearance) value;
	    if (!newAppearance.isLegalFor(ch)) {
		newAppearance = ch.getAppearanceNamed(newAppearance.getName());
		if (newAppearance == null)
		    return Variable.ILLEGAL_VALUE;
	    }
	    return newAppearance;
	}
    }
    
    protected AppearanceVariable(String sysvarID, String nameToken) {
	super(sysvarID, nameToken,
	      (sysvarID
		   .equals(CocoaCharacter.SYS_ROLLOVER_APPEARANCE_VARIABLE_ID)
	       ? APPEARANCE_ACCESSOR : CURRENT_APPEARANCE_ACCESSOR),
	      true);
    }
    
    String nameOf(Object value) {
	return ((Appearance) value).getName();
    }
    
    public Enumeration legalValues(VariableOwner owner) {
	return ((CocoaCharacter) owner).getAppearances();
    }
    
    public Object getDefaultValue(VariableOwner owner) {
	Enumeration appearances = ((CocoaCharacter) owner).getAppearances();
	if (appearances.hasMoreElements())
	    return appearances.nextElement();
	return null;
    }
    
    public Object getLegalValue(VariableOwner owner, Object value) {
	if (value instanceof Appearance
	    && ((Appearance) value).getOwner() == owner.getVariableListOwner())
	    return value;
	return super.getLegalValue(owner, value);
    }
    
    private Appearance getAppearanceNamed(VariableOwner owner, String name) {
	if (owner != null && name != null && owner instanceof CocoaCharacter)
	    return ((CocoaCharacter) owner).getAppearanceNamed(name);
	return null;
    }
}
