/* UnaryOperation - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.unaryoperators;
import COM.stagecast.operators.Operation;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.playwrite.StorableToken;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public interface UnaryOperation extends StorableToken, ResourceIDs.UnaryOpIDs
{
    public static final String FUNCTION
	= ResourceIDs.UnaryOpIDs.SYS_UN_FUNC_XFMT_ID;
    public static final String PREFIX
	= ResourceIDs.UnaryOpIDs.SYS_UN_PREFIX_XFMT_ID;
    public static final String POSTFIX
	= ResourceIDs.UnaryOpIDs.SYS_UN_POSTFIX_XFMT_ID;
    public static final Object ERROR = Operation.ERROR;
    
    public Object uoperate(Object object);
    
    public String getDisplayType();
    
    public Class getArgumentClass();
    
    public String getProxyID();
    
    public Subtotal createSubtotal(UnaryExpression unaryexpression);
}
