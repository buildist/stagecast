/* SubtotalObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.ASSERT;
import COM.stagecast.playwrite.Debug;
import COM.stagecast.playwrite.Named;
import COM.stagecast.playwrite.PlaywriteInternalError;
import COM.stagecast.playwrite.PlaywriteView;
import COM.stagecast.playwrite.ReferencedObject;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.Summary;
import COM.stagecast.playwrite.World;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public abstract class SubtotalObject implements Subtotal, Named
{
    public static final int storeVersion = 0;
    public static final long serialVersionUID = -3819410108751414578L;
    protected Subtotal.Creator _expression;
    protected Object _result;
    protected String _resultString;
    private String _name;
    private boolean _needsEval = true;
    
    public SubtotalObject(Subtotal.Creator expression) {
	ASSERT.isNotNull(expression);
	_expression = expression;
    }
    
    public SubtotalObject() {
	/* empty */
    }
    
    public String getResultAsString() {
	return _resultString;
    }
    
    public Object getResult() {
	return _result;
    }
    
    public Subtotal.Creator getExpression() {
	return _expression;
    }
    
    public void reevaluate() {
	_result = getExpression().eval();
	_result = Op.checkResult(_result);
	if (_result != null) {
	    if (_result instanceof Number)
		_resultString = Resource.formatNumber((Number) _result);
	    else if (_result instanceof String)
		_resultString = (String) _result;
	    else if (_result == Operation.ERROR)
		_resultString = Resource.getText(ResourceIDs.OperationIDs
						 .SYS_CALC_ERROR_ID);
	    else
		_resultString = _result.toString();
	} else
	    _resultString = "?";
	_needsEval = false;
    }
    
    public void clearCache() {
	expressionChanged();
	Subtotal.Creator e = getExpression();
	if (e != null)
	    e.clearCaches();
    }
    
    public void expressionChanged() {
	_needsEval = true;
	_resultString = null;
	_result = null;
    }
    
    public String getName() {
	return _name;
    }
    
    public void setName(String name) {
	_name = name;
    }
    
    public Object eval() {
	if (_needsEval)
	    reevaluate();
	return _result;
    }
    
    public Object findReferenceTo(ReferencedObject obj) {
	return _expression.findReferenceTo(obj);
    }
    
    public Expression evaluates(Expression object) {
	if (object == getExpression())
	    return this;
	return getExpression().evaluates(object);
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	throw new PlaywriteInternalError("This method should not be used.");
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	SubtotalObject newSubtotalObject = (SubtotalObject) map.get(this);
	if (newSubtotalObject == null) {
	    newSubtotalObject = (SubtotalObject) clone();
	    newSubtotalObject._expression
		= (Subtotal.Creator) _expression.copy(map, fullCopy);
	    newSubtotalObject._expression.setSubtotal(newSubtotalObject);
	    map.put(this, newSubtotalObject);
	}
	return newSubtotalObject;
    }
    
    public Object clone() {
	SubtotalObject newSubtotalObject = null;
	try {
	    newSubtotalObject = (SubtotalObject) super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    Debug.print(true, "bad clone on SubtotalObject");
	    return null;
	} finally {
	    if (newSubtotalObject == null)
		return null;
	}
	return newSubtotalObject;
    }
    
    public PlaywriteView createIconView() {
	return createView();
    }
    
    public abstract PlaywriteView createView();
    
    public void summarize(Summary s) {
	s.writeFormat("subtotal xfmt", new Object[] { getName() },
		      new Object[] { getExpression() });
    }
    
    public boolean isValid() {
	return _expression != null && _expression.isValid();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeObject(_expression);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_expression = (Subtotal.Creator) in.readObject();
	_expression.setSubtotal(this);
    }
}
