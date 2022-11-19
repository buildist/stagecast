/* SubroutineType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;

public interface SubroutineType extends Externalizable
{
    public String getTypeName();
    
    public void setSubroutine(Subroutine subroutine);
    
    public Subroutine getSubroutine();
    
    public boolean prepareToExecute(CharacterInstance characterinstance);
    
    public RuleListItem getNextRule(boolean bool, int i);
    
    public boolean subroutineMatched(CharacterInstance characterinstance);
    
    public boolean continueExecution(CharacterInstance characterinstance);
    
    public SubroutineScrap createView(CocoaCharacter cocoacharacter);
}
