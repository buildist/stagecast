/* DelegatedInstanceAccessor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class DelegatedInstanceAccessor implements VariableDirectAccessor
{
    private VariableDirectAccessor _delegate;
    private VariableDirectAccessor _normal = Variable.STD_ACCESSOR;
    
    public DelegatedInstanceAccessor(VariableDirectAccessor delegate) {
	ASSERT.isNotNull(delegate);
	_delegate = delegate;
    }
    
    public void setNonInstanceAccessor(VariableDirectAccessor normal) {
	ASSERT.isNotNull(normal);
	_normal = normal;
    }
    
    public void setDirectValue(Variable variable, VariableOwner owner,
			       Object value) {
	if (owner instanceof CharacterInstance && value != Variable.UNBOUND)
	    _delegate.setDirectValue(variable, owner, value);
	else
	    _normal.setDirectValue(variable, owner, value);
    }
    
    public Object getDirectValue(Variable variable, VariableOwner owner) {
	if (owner instanceof CharacterInstance)
	    return _delegate.getDirectValue(variable, owner);
	return _normal.getDirectValue(variable, owner);
    }
    
    public Object mapUnboundDirect(Variable variable, VariableOwner owner) {
	return _delegate.mapUnboundDirect(variable, owner);
    }
    
    public Object constrainDirectValue(Variable variable, VariableOwner owner,
				       Object value) {
	return _delegate.constrainDirectValue(variable, owner, value);
    }
}
