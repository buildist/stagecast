/* TimeoutView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class TimeoutView extends XYContainerView
    implements Debug.Constants, ResourceIDs.ToolIDs, ResourceIDs.WorldViewIDs
{
    private static final String TIMEOUT_BOX_NAME
	= Resource.getText("WW Timeout");
    private static final int MIN_HEIGHT = 96;
    
    TimeoutView(XYCharContainer timeout, int x, int y, int width, int height) {
	super(timeout, width, height);
	this.moveTo(x, y);
	this.setBorder(null);
	this.setBackgroundColor(this.getWorld().getColor());
	this.setMinSize(SidelineView.DEFAULT_WIDTH, 96);
	this.setName("timeout");
    }
    
    public void setProperties() {
	super.setProperties();
	this.allowDragInto(CharacterPrototype.class, this);
	this.allowTool(Tool.newCharacterTool, this);
    }
    
    protected PlaywriteView makeModelView(Contained model) {
	return ((CocoaCharacter) model).createView(32);
    }
    
    void setViewProperties(PlaywriteView view) {
	/* empty */
    }
    
    public void setBounds(int x, int y, int width, int height) {
	Vector contents = this.subviews();
	for (int i = 0; i < contents.size(); i++) {
	    View v = (View) contents.elementAt(i);
	    if (v.y() + v.height() > height)
		v.moveTo(v.x(), height - v.height() - 5);
	}
	super.setBounds(x, y, width, height);
    }
    
    public void drawView(Graphics g) {
	super.drawView(g);
	Rect box = this.localBounds();
	box.growBy(-5, -5);
	g.setColor(Color.black);
	g.drawRect(box);
	box.growBy(-1, -1);
	g.setColor(Color.white);
	g.drawRect(box);
	g.setFont(Util.valueFont);
	g.setColor(Color.black);
	g.drawString(TIMEOUT_BOX_NAME, box.x + 5, box.y + 13);
    }
    
    public boolean dragDropped(DragSession session) {
	PlaywriteView cView = this.viewBeingDragged(session);
	CharacterContainer timeout
	    = (CharacterContainer) this.getModelObject();
	World world = this.getWorld();
	CocoaCharacter ch = (CocoaCharacter) cView.getModelObject();
	CharacterInstance draggedChar = null;
	Point dest = session.destinationMousePoint();
	this.unhilite();
	if (!PlaywriteRoot.isPlayer()) {
	    RuleEditor ruleEditor = world.getRuleEditor();
	    if (ruleEditor != null && RuleEditor.isRecordingOrEditing())
		return false;
	}
	if (session.source() == this)
	    this.addSubview(this.viewBeingDragged(session));
	dest.x -= cView.width() / 2;
	dest.y -= cView.height() / 2;
	if (dest.x < 6)
	    dest.x = 6;
	else if (dest.x + cView.width() + 6 > this.width())
	    dest.x = this.width() - cView.width() - 6;
	if (dest.y < 13 + Util.valueFontHeight)
	    dest.y = 13 + Util.valueFontHeight;
	else if (dest.y + cView.height() + 6 > this.height())
	    dest.y = this.height() - cView.height() - 6;
	ch.setVisibility(true);
	if (ch instanceof CharacterPrototype) {
	    GeneralizedCharacter gch
		= world.doCreateAction((CharacterPrototype) ch, timeout,
				       dest.x, dest.y);
	    draggedChar = gch.getBinding();
	} else if (ch instanceof CharacterInstance)
	    draggedChar = world.doMoveAction((CharacterInstance) ch, timeout,
					     dest.x, dest.y);
	else
	    return super.dragDropped(session);
	Selection.hideModalView();
	if (draggedChar != null)
	    cView = getViewFor(draggedChar);
	if (cView != null)
	    cView.selectModel(null);
	world.setModified(true);
	return true;
    }
    
    private PlaywriteView getViewFor(CharacterInstance ch) {
	Vector views = this.subviews();
	for (int i = 0; i < views.size(); i++) {
	    PlaywriteView view = (PlaywriteView) views.elementAt(i);
	    if (view.getModelObject() == ch)
		return view;
	}
	return null;
    }
    
    public boolean toolClicked(ToolSession session) {
	Point dest = session.destinationMousePoint();
	Tool toolType = session.toolType();
	CharacterContainer timeout
	    = (CharacterContainer) this.getModelObject();
	World world = this.getWorld();
	Debug.print("debug.tool", "tool clicked at ", dest);
	if (toolType == Tool.newCharacterTool) {
	    CharacterPrototype prototype = world.makeNewPrototype();
	    if (prototype == null)
		return false;
	    GeneralizedCharacter newCharacter
		= new GeneralizedCharacter(prototype);
	    RuleAction action
		= new CreateAction(newCharacter, dest.x, dest.y, -1);
	    world.doManualAction(action, timeout);
	    PlaywriteSound.sysSplat.play();
	    PlaywriteView cView = getViewFor(newCharacter.getBinding());
	    if (cView != null)
		cView.selectModel(null);
	    this.getWorld().getWorldView().getControlPanelView()
		.getNewCharButton
		().setImageIndex(this.getWorld().getSplatImageCount());
	} else if (toolType == Tool.copyPlaceTool) {
	    if (!(session.data() instanceof CocoaCharacter))
		return false;
	    CocoaCharacter stampChar = (CocoaCharacter) session.data();
	    if (stampChar == null)
		throw new PlaywriteInternalError("Can't copy null");
	    if (stampChar.getWorld() != this.getWorld())
		stampChar = (CocoaCharacter) stampChar.copy(this.getWorld());
	    if (stampChar instanceof CharacterInstance) {
		GeneralizedCharacter sourceCharacter
		    = new GeneralizedCharacter((CharacterInstance) stampChar);
		GeneralizedCharacter newCharacter
		    = new GeneralizedCharacter(stampChar.getPrototype());
		RuleAction action
		    = new CopyAction(sourceCharacter, newCharacter, dest.x,
				     dest.y, -1);
		world.doManualAction(action, timeout);
	    } else if (stampChar instanceof CharacterPrototype) {
		GeneralizedCharacter newCharacter
		    = new GeneralizedCharacter((CharacterPrototype) stampChar);
		RuleAction action
		    = new CreateAction(newCharacter, dest.x, dest.y, -1);
		world.doManualAction(action, timeout);
	    } else
		return false;
	} else
	    return super.toolClicked(session);
	world.setModified(true);
	return true;
    }
}
