/* NormalSubType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class NormalSubType
    implements ResourceIDs.SubroutineTypeIDs, SubroutineType
{
    private static final String TYPE_NAME = "type df";
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108752725298L;
    protected transient int lastRule = 0;
    protected transient boolean succeeded = false;
    protected Subroutine sub = null;
    
    static void initExtension() {
	Subroutine.addSubroutineType(Resource.getText("type df"),
				     NormalSubType.class);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(sub);
	out.writeObject(sub);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	sub = (Subroutine) in.readObject();
    }
    
    public void setSubroutine(Subroutine subroutine) {
	sub = subroutine;
    }
    
    public Subroutine getSubroutine() {
	return sub;
    }
    
    public String getTypeName() {
	return Resource.getText("type df");
    }
    
    public boolean prepareToExecute(CharacterInstance self) {
	succeeded = false;
	lastRule = sub.getRules().size();
	return true;
    }
    
    public RuleListItem getNextRule(boolean previousSucceeded,
				    int previousIndex) {
	if (previousSucceeded) {
	    succeeded = true;
	    return null;
	}
	int nextIndex = previousIndex + 1;
	if (nextIndex < lastRule)
	    return sub.getRule(nextIndex);
	return null;
    }
    
    public boolean subroutineMatched(CharacterInstance self) {
	return succeeded;
    }
    
    public boolean continueExecution(CharacterInstance self) {
	return false;
    }
    
    public SubroutineScrap createView(CocoaCharacter self) {
	return new SubroutineScrap(sub, self);
    }
    
    public String toString() {
	return "NormalSubroutine";
    }
}
