/* Summary - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.ResourceBundle;

public interface Summary
{
    public void writeText(String string);
    
    public void writeValue(Object object);
    
    public String pushValue(Object object);
    
    public void writeFormat(String string, Object[] objects,
			    Object[] objects_0_);
    
    public void writeFormat(ResourceBundle resourcebundle, String string,
			    Object[] objects, Object[] objects_1_);
    
    public CharacterPrototype ruleTarget();
    
    public GeneralizedCharacter ruleSelf();
}
