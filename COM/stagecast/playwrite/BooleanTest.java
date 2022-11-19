/* BooleanTest - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Expression;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.Operation;
import COM.stagecast.operators.OperationManager;

class BooleanTest extends RuleTest implements Debug.Constants, Externalizable
{
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108755084594L;
    private Object _test;
    private boolean _evalTest;
    
    BooleanTest(Object theTest) {
	_test = theTest;
	_evalTest = theTest instanceof Expression;
    }
    
    public BooleanTest() {
	/* empty */
    }
    
    public static BooleanTest createForRuleEditor(World world) {
	return new BooleanTest(new OperationManager(null, null, Op.Equal));
    }
    
    final Object getTest() {
	return _test;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	BooleanTest newTest = (BooleanTest) map.get(this);
	if (newTest != null)
	    return newTest;
	if (_test instanceof Copyable)
	    newTest = new BooleanTest(((Copyable) _test).copy(map, fullCopy));
	else {
	    Debug.print("debug.rule.test", "creating duplicate reference to ",
			_test, " of ", this);
	    newTest = new BooleanTest(_test);
	}
	map.put(this, newTest);
	return newTest;
    }
    
    public boolean evaluate(CharacterInstance self) {
	Object testResult = _evalTest ? ((Expression) _test).eval() : _test;
	if (testResult == Operation.ERROR)
	    return false;
	boolean result;
	if (testResult instanceof Boolean)
	    result = ((Boolean) testResult).booleanValue();
	else if (testResult instanceof Number)
	    result = ((Number) testResult).doubleValue() != 0.0;
	else
	    result = testResult != null;
	return result;
    }
    
    public PlaywriteView createView() {
	PlaywriteView v = new BooleanTestView(this);
	this.setView(v);
	return v;
    }
    
    void showTestResult$(boolean success) {
	BooleanTestView btv = (BooleanTestView) this.getView();
	if (btv != null)
	    btv.updateValueViews();
	super.showTestResult$(success);
    }
    
    public boolean refersTo(ReferencedObject obj) {
	if (_evalTest)
	    return ((Expression) _test).findReferenceTo(obj) != null;
	if (_test == obj)
	    return true;
	return false;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_test);
	out.writeObject(_test);
	out.writeBoolean(_evalTest);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(BooleanTest.class);
	switch (version) {
	case 1:
	    in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 2);
	case 2:
	    /* empty */
	}
	_test = in.readObject();
	_evalTest = in.readBoolean();
    }
    
    public void summarize(Summary s) {
	if (_evalTest)
	    ((Expression) _test).summarize(s);
	else
	    s.writeValue(_test);
    }
    
    public String toString() {
	if (_test == null)
	    return "<BooleanTest test is null (always fails)>";
	return "<BooleanTest " + _test + ">";
    }
}
