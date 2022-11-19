/* Pretest - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;

class Pretest extends Rule
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108756002098L;
    private static boolean testMode = false;
    
    public Pretest() {
	/* empty */
    }
    
    Pretest(CocoaCharacter self) {
	GeneralizedCharacter selfGC
	    = new GeneralizedCharacter(self.getPrototype());
	this.setSelfGC(selfGC);
	this.addTest(new BindTest(selfGC, false));
    }
    
    Pretest(CharacterInstance self, Subroutine subroutine) {
	super(self);
	this.setSubroutine(subroutine);
    }
    
    final void addAction(RuleAction newAction) {
	/* empty */
    }
    
    public PlaywriteView createView(int squareSize) {
	BeforeAfterView bav = (BeforeAfterView) super.createView(squareSize);
	bav.getArrowView().removeFromSuperview();
	bav.getBeforeBoardView().removeFromSuperview();
	BoardView abv = bav.getAfterBoardView();
	abv.setDrawDontCares(true);
	this.getAfterBoard().setShowDontCare(true);
	int edge = squareSize;
	bav.sizeTo(abv.width() + edge, abv.height() + edge);
	bav.setLayoutManager(null);
	Util.centerView(abv);
	return bav;
    }
    
    AfterBoardView createAfterBoardView() {
	return new PretestView(this);
    }
    
    PlaywriteView createMiniRuleView(Color backgroundColor) {
	return BeforeAfterView.createMiniBeforeBoard(this, backgroundColor);
    }
}
