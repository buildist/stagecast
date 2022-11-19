/* BooleanVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.util.ResourceBundle;

public class BooleanVariable extends Variable implements Externalizable
{
    public static final VariableDirectAccessor STD_BOOLEAN_ACCESSOR
	= new BooleanDirectAccessor();
    private transient String _trueName;
    private transient String _falseName;
    
    public static class BooleanDirectAccessor
	extends Variable.StandardDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    if (value == Variable.UNBOUND)
		return value;
	    boolean bv;
	    if (value instanceof Boolean)
		bv = ((Boolean) value).booleanValue();
	    else if (value instanceof String)
		bv = "true".equalsIgnoreCase((String) value);
	    else
		bv = false;
	    return bv ? Boolean.TRUE : Boolean.FALSE;
	}
    }
    
    public BooleanVariable(String sysvarID, String trueNameID,
			   String falseNameID) {
	this(sysvarID, trueNameID, falseNameID, true);
    }
    
    public BooleanVariable(String sysvarID, String trueNameID,
			   String falseNameID,
			   VariableDirectAccessor accessor) {
	this(sysvarID, trueNameID, falseNameID, accessor, true);
    }
    
    protected BooleanVariable(String sysvarID, String trueNameID,
			      String falseNameID, boolean isVisible) {
	this(sysvarID, trueNameID, falseNameID, STD_BOOLEAN_ACCESSOR, true);
    }
    
    protected BooleanVariable
	(String sysvarID, String trueNameID, String falseNameID,
	 VariableDirectAccessor accessor, boolean isVisible) {
	super(sysvarID, trueNameID, accessor, isVisible);
	this.setDefaultValue(Boolean.FALSE);
	_trueName = Resource.getText(trueNameID);
	_falseName = Resource.getText(falseNameID);
    }
    
    public BooleanVariable(ResourceBundle bundle, String sysvarID,
			   String trueNameID, String falseNameID) {
	super(bundle, sysvarID, trueNameID, STD_BOOLEAN_ACCESSOR, true);
	this.setDefaultValue(Boolean.TRUE);
	_trueName = Resource.getText(bundle, trueNameID);
	_falseName = Resource.getText(bundle, falseNameID);
    }
    
    AbstractVariableEditor makeVariableEditor(VariableOwner owner,
					      ValueView.SetterGetter vsg) {
	return makeVariableEditor(owner, vsg, true);
    }
    
    AbstractVariableEditor makeVariableEditor(VariableOwner owner,
					      ValueView.SetterGetter vsg,
					      boolean displayContent) {
	AbstractVariableEditor editor
	    = new BooleanVariableEditor(owner, this, vsg, displayContent);
	if (displayContent)
	    editor.updateContentView();
	return editor;
    }
    
    String getTrueName() {
	return _trueName;
    }
    
    String getFalseName() {
	return _falseName;
    }
}
