/* VariableSieve - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.simmer.ClientServerMessages;

public class VariableSieve extends AbstractSieve
    implements ClientServerMessages
{
    private static final int CLEANUP_INTERVAL = 100;
    private Hashtable _interestingVariables = new Hashtable(100);
    private Hashtable _interestingOwnerTypes = new Hashtable(10);
    private Hashtable _variableValueFilters = new Hashtable(10);
    
    public static interface VariableValueFilter
    {
	public Object filterVariableValue(VariableOwner variableowner,
					  Variable variable, Object object);
    }
    
    public static interface DataCollector extends AbstractSieve.Notifier
    {
	public void collect(VariableOwner variableowner, Variable variable,
			    Object object);
    }
    
    public VariableSieve(AbstractSieve.Notifier n) {
	super(n);
    }
    
    void ownerTypeIsInteresting(Class cl) {
	_interestingOwnerTypes.put(cl, this);
    }
    
    void ownerTypeIsNotInteresting(Class cl) {
	_interestingOwnerTypes.remove(cl);
    }
    
    boolean isOwnerInteresting(VariableOwner own) {
	return _interestingOwnerTypes.containsKey(own.getClass());
    }
    
    void variableIsInteresting(Variable var) {
	if (var.isUserVariable())
	    _interestingVariables.put(var, this);
    }
    
    void variableIsNotInteresting(Variable var) {
	_interestingVariables.remove(var);
    }
    
    boolean isVariableInteresting(Variable var) {
	return (var.isSystemVariable()
		|| _interestingVariables.containsKey(var));
    }
    
    public void strain(VariableOwner owner, Variable var, Object value) {
	if (isOwnerInteresting(owner) && isVariableInteresting(var))
	    this.getNotifier().addSieveDatum(new Object[] { this, owner, var,
							    value });
    }
    
    public void processSieveDatum(Object[] datum) {
	VariableOwner owner = (VariableOwner) datum[1];
	Variable var = (Variable) datum[2];
	Object value = datum[3];
	VariableValueFilter filter = getValueFilterFor(var);
	Object filteredValue = value;
	if (filter != null)
	    filteredValue = filter.filterVariableValue(owner, var, value);
	((DataCollector) this.getNotifier()).collect(owner, var,
						     filteredValue);
    }
    
    public void addVariableValueFilter(Variable v, VariableValueFilter f) {
	_variableValueFilters.put(v, f);
    }
    
    VariableValueFilter getValueFilterFor(Variable v) {
	return (VariableValueFilter) _variableValueFilters.get(v);
    }
}
