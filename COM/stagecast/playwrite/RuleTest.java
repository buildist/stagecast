/* RuleTest - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.LineBorder;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public abstract class RuleTest extends IndexedObject
    implements Copyable, ResourceIDs.RuleTestIDs, Rule.Content, Summarizable,
	       Target, Worldly
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108752135474L;
    static final int spacing = 8;
    private static final String SHOW_TEST_RESULT = "str";
    private static final String RESET_VIEW = "rv";
    private Rule _rule = null;
    
    public final Rule getRule() {
	return _rule;
    }
    
    public final void setRule(Rule rule) {
	_rule = rule;
    }
    
    final GeneralizedCharacter getSelf() {
	return _rule.getSelf();
    }
    
    public final World getWorld() {
	return _rule.getWorld();
    }
    
    final ToolHandler.ToolArbiter getToolArbiter() {
	return getRule().getToolArbiter();
    }
    
    public abstract boolean evaluate(CharacterInstance characterinstance);
    
    public boolean isDisplayedInBeforeBoard() {
	return false;
    }
    
    final void showTestResult(boolean success) {
	getWorld().addSyncAction(this, "str",
				 success ? Boolean.TRUE : Boolean.FALSE);
    }
    
    final void resetView() {
	getWorld().addSyncAction(this, "rv", null);
    }
    
    void showTestResult$(boolean success) {
	if (this.getView() != null) {
	    if (success)
		this.getView().setBorder(ScrapBorder.getGreenTestBorder());
	    else
		this.getView().setBorder(ScrapBorder.getRedTestBorder());
	}
    }
    
    void resetView$() {
	if (this.getView() != null)
	    this.getView().setBorder(LineBorder.blackLine());
    }
    
    final Object findReferenceTo(ReferencedObject obj) {
	return refersTo(obj) ? this : null;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return false;
    }
    
    public abstract PlaywriteView createView();
    
    public Object copy() {
	return this.copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	Hashtable map = new Hashtable(50);
	if (getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	return this.copy(map, true);
    }
    
    public void performCommand(String command, Object data) {
	if (command == "str")
	    showTestResult$(((Boolean) data).booleanValue());
	else if (command == "rv")
	    resetView$();
    }
    
    public void summarize(Summary s) {
	s.writeText(toString());
    }
    
    public String toString() {
	return "<" + this.getClass().getName() + ">";
    }
}
