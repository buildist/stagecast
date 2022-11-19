/* Operation - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import COM.stagecast.playwrite.Condition;

public interface Operation
{
    public static final Object ERROR
	= new Condition("COM.stagecast.operators.Operation.ERROR");
    
    public Object operate(Object object, Object object_0_);
}
