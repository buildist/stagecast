/* OperationType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import COM.stagecast.playwrite.StorableToken;
import COM.stagecast.playwrite.Summary;

public interface OperationType extends Operation, StorableToken
{
    public String getNameResourceID();
    
    public void summarizeOp(Summary summary, Object object, Object object_0_);
    
    public Subtotal createSubtotal(OperationManager operationmanager);
}
