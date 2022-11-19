/* RuleListView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.View;

class RuleListView extends TallView
{
    private int hiliteBarY = 0;
    
    RuleListView() {
	super(0, 0, 4, 4);
	init(0);
    }
    
    RuleListView(int x, int y, int w, int h) {
	super(x, y, w, h);
	init(0);
    }
    
    RuleListView(Rect bounds) {
	super(bounds);
	init(0);
    }
    
    RuleListView(Rect bounds, int Ygap) {
	super(bounds);
	init(Ygap);
    }
    
    private void init(int Ygap) {
	this.setBorder(null);
	PackLayout lm = new PackLayout();
	PackConstraints pc = new PackConstraints();
	pc.setAnchor(6);
	pc.setSide(0);
	pc.setFillX(true);
	pc.setPadY(Ygap);
	pc.setPadX(2);
	lm.setDefaultConstraints(pc);
	this.setLayoutManager(lm);
	if (Debug.lookup("debug.character.window"))
	    this.setBackgroundColor(Color.blue);
	else
	    this.setBackgroundColor(Util.defaultLightColor);
	this.setMinSize(4, 4);
    }
    
    public void setBounds(int x, int y, int width, int height) {
	if (width < 4)
	    width = 4;
	if (height < 4)
	    height = 4;
	super.setBounds(x, y, width, height);
    }
    
    public void drawHilite(Graphics g) {
	super.drawHilite(g);
	if (hiliteBarY != 0) {
	    g.setColor(Color.black);
	    g.fillRect(15, hiliteBarY - 1, this.width(), 3);
	    g.drawLine(0, hiliteBarY, 9, hiliteBarY);
	    g.drawLine(5, hiliteBarY - 3, 8, hiliteBarY);
	    g.drawLine(5, hiliteBarY + 3, 8, hiliteBarY);
	}
    }
    
    public void addSubview(View subview) {
	super.addSubview(subview);
	this.setDirty(true);
    }
    
    protected void removeSubview(View subview) {
	super.removeSubview(subview);
	this.sizeToMinSize();
	this.setDirty(true);
    }
    
    public void subviewDidResize(View subview) {
	super.subviewDidResize(subview);
	this.sizeToMinSize();
    }
    
    public Slot addRuleListItemView(RuleListItemView ruleScrap) {
	Slot slot = new Slot(ruleScrap);
	addSlot(slot);
	return slot;
    }
    
    public void addSlot(Slot slot) {
	Slot lastOne = (Slot) this.subviews().lastElement();
	int idx = 1;
	if (lastOne != null) {
	    idx = lastOne.getIndex();
	    if (!(lastOne.getScrap() instanceof CommentScrap))
		idx++;
	}
	slot.setIndex(idx);
	addSubview(slot);
	this.layoutView(0, 0);
    }
}
