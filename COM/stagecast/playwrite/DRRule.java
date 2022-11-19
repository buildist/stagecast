/* DRRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Vector;

class DRRule extends Rule implements Debug.Constants
{
    private Point beforeBoardDimensions;
    private Vector boardContents;
    private GeneralizedCharacter self;
    private Vector ruleList;
    private Vector tests;
    private Vector actions;
    private DRTranslator xlate;
    
    DRRule(DRTranslator trans, Point mDimensions, Vector mContents,
	   GeneralizedCharacter myself) {
	xlate = trans;
	beforeBoardDimensions = mDimensions;
	boardContents = mContents;
	self = myself;
	xlate.addDRRule(this);
    }
    
    final void setTests(Vector andIfTests) {
	tests = andIfTests;
    }
    
    final void setActions(Vector actionses) {
	actions = actionses;
    }
    
    final void setRuleList(Vector rl) {
	ruleList = rl;
    }
    
    final Vector getRuleList() {
	return ruleList;
    }
    
    final GeneralizedCharacter getTheSelf() {
	return self;
    }
    
    protected boolean matchAndExecute(CharacterInstance self) {
	throw new PlaywriteInternalError("thou shalt not execute DRRules");
    }
    
    public boolean drNeedsCompile() {
	return (boardContents != null || beforeBoardDimensions != null
		|| self != null || tests != null || actions != null);
    }
    
    public Rule drCompile() {
	if (drNeedsCompile()) {
	    if (beforeBoardDimensions == null)
		Debug.print("debug.dr", "beforeBoardDimensions is null");
	    if (boardContents == null)
		Debug.print("debug.dr", "boardContents is null");
	    if (self == null)
		Debug.print("debug.dr", "self is null");
	    Vector bindTests = null;
	    bindTests = xlate.buildBindTests(beforeBoardDimensions,
					     boardContents, self);
	    if (bindTests == null)
		Debug.print("debug.dr", "bindTests is null");
	    if (tests == null)
		Debug.print("debug.dr", "tests is null");
	    if (actions == null)
		Debug.print("debug.dr", " is null");
	    Rule rule = new Rule();
	    rule.setName(this.getName());
	    xlate.buildRule(rule, bindTests, tests, actions);
	    return rule;
	}
	return null;
    }
}
