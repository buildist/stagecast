/* RandomSubType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class RandomSubType extends NormalSubType
    implements ResourceIDs.SubroutineTypeIDs
{
    private static final String TYPE_NAME = "type dr";
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108752594226L;
    
    static void initExtension() {
	Subroutine.addSubroutineType(Resource.getText("type dr"),
				     RandomSubType.class);
    }
    
    public String getTypeName() {
	return Resource.getText("type dr");
    }
    
    public void setSubroutine(Subroutine subroutine) {
	if (subroutine == null && sub.hasViews()
	    && this.getSubroutine() != null)
	    this.getSubroutine()
		.reorderAllViewsSlots(this.getSubroutine().getRules());
	super.setSubroutine(subroutine);
    }
    
    public boolean prepareToExecute(CharacterInstance self) {
	scrambleRules(self);
	return super.prepareToExecute(self);
    }
    
    private Vector scrambleRules(CharacterInstance self) {
	Vector rules = sub.getRules();
	int n = rules.size();
	for (int i = 0; i < n; i++) {
	    int swap = (int) Math.round((double) (rules.size() - 1)
					* Math.random());
	    Object temp = rules.elementAt(i);
	    rules.setElementAt(rules.elementAt(swap), i);
	    rules.setElementAt(temp, swap);
	}
	if (self.isBeingEdited()) {
	    SubroutineScrap subScrap = sub.getViewFor(self);
	    subScrap.reorderSlots(rules);
	}
	sub.renumberItems(rules, 0);
	return rules;
    }
    
    public String toString() {
	return "RandomSubroutine";
    }
}
