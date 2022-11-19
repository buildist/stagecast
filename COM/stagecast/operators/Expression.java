/* Expression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.io.Externalizable;

import COM.stagecast.playwrite.Copyable;
import COM.stagecast.playwrite.FirstClassValue;
import COM.stagecast.playwrite.ReferencedObject;
import COM.stagecast.playwrite.Summarizable;
import COM.stagecast.playwrite.Verifiable;

public interface Expression
    extends Copyable, FirstClassValue, Summarizable, Verifiable, Externalizable
{
    public Object eval();
    
    public Object findReferenceTo(ReferencedObject referencedobject);
    
    public Expression evaluates(Expression expression_0_);
}
