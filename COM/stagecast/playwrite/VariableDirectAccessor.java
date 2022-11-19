/* VariableDirectAccessor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface VariableDirectAccessor
{
    public void setDirectValue(Variable variable, VariableOwner variableowner,
			       Object object);
    
    public Object getDirectValue(Variable variable,
				 VariableOwner variableowner);
    
    public Object mapUnboundDirect(Variable variable,
				   VariableOwner variableowner);
    
    public Object constrainDirectValue
	(Variable variable, VariableOwner variableowner, Object object);
}
