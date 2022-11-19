/* DoAllSubType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class DoAllSubType extends NormalSubType
    implements ResourceIDs.SubroutineTypeIDs
{
    private static final String TYPE_NAME = "type da";
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108752659762L;
    
    static void initExtension() {
	Subroutine.addSubroutineType(Resource.getText("type da"),
				     DoAllSubType.class);
    }
    
    public String getTypeName() {
	return Resource.getText("type da");
    }
    
    public RuleListItem getNextRule(boolean previousSucceeded,
				    int previousIndex) {
	succeeded = succeeded | previousSucceeded;
	return super.getNextRule(false, previousIndex);
    }
    
    public boolean subroutineMatched(CharacterInstance self) {
	return succeeded;
    }
    
    public final boolean continueExecution
	(CharacterInstance characterInstance) {
	return true;
    }
    
    public String toString() {
	return "DoAllSubroutine";
    }
}
