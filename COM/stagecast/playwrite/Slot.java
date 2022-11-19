/* Slot - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Timer;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class Slot extends PlaywriteView
    implements DragDestination, ExtendedDragSource, Debug.Constants,
	       ResourceIDs.CharacterWindowIDs, ResourceIDs.RuleSlotIDs, Worldly
{
    private static final String TEST_RULE = "TESTRULE";
    private static final String ENABLE_OR_DISABLE = "DISENABLE";
    private static final String REALLY_TEST_RULE = "ReallyTestRule";
    private static final String REALLY_STEP_RULE = "ReallyStepRule";
    private static final int minHeight = 30;
    private static final int topMargin = 10;
    private static final int topMargin2 = 5;
    private static final int labelToLight = 5;
    private static Slot __hilitedSlot;
    private static Bitmap light;
    private static Bitmap greenLight;
    private static Bitmap redLight;
    private static Bitmap yellowLight;
    private static Bitmap blackLight;
    private static Bitmap disabledRule;
    private static Bitmap breakpointPic;
    private static Vector dragOrDropList = new Vector(10);
    private static String BLINK_COMMAND;
    private static Timer yellowBlinker;
    private RuleListItemView scrap;
    private Label indexLabel = Util.makeRuleIndexLabel("1");
    private PlaywriteView debugLight
	= new PlaywriteView(0, 0, light.width(), light.height());
    private boolean ruleDidFire = false;
    private Color _backgroundColor = PlaywriteWindow.DEFAULT_BACKGROUND_COLOR;
    private boolean nextHilite = false;
    private boolean breakpoint = false;
    private PlaywriteView bpView = null;
    
    static void initStatics() {
	light = Resource.getImage("RSL0L");
	greenLight = Resource.getImage("RSLGL");
	redLight = Resource.getImage("RSLRL");
	yellowLight = Resource.getImage("RSLBL");
	blackLight = Resource.getImage("RSLBLK");
	disabledRule = Resource.getImage("RSLDL");
	breakpointPic = Resource.getImage("cwBreak");
	BLINK_COMMAND = "blink";
	yellowBlinker = new Timer(null, BLINK_COMMAND, 300);
    }
    
    Slot(RuleListItemView theScrap) {
	init(theScrap);
    }
    
    private void init(RuleListItemView theScrap) {
	this.setBorder(null);
	this.allowTool(CharacterWindow.commentTool, this);
	this.allowTool(Tool.copyPlaceTool, this);
	this.allowTool(CharacterWindow.breakpointTool, this);
	debugLight.allowTool(CharacterWindow.disableTool, this);
	debugLight.allowTool(Tool.deleteTool, this);
	debugLight.setBorder(null);
	debugLight.setImageDisplayStyle(0);
	debugLight.setImage(light);
	debugLight.setHorizResizeInstruction(0);
	debugLight.setVertResizeInstruction(4);
	debugLight.setEventDelegate(-3, 0, 1, "TESTRULE", this);
	indexLabel.setJustification(1);
	indexLabel.setHorizResizeInstruction(0);
	indexLabel.setVertResizeInstruction(4);
	setScrap(theScrap);
	setBackgroundColor(getWorld().getColor());
    }
    
    public World getWorld() {
	return getRuleListItem().getWorld();
    }
    
    final boolean worldIsExecuting() {
	return ((CharacterWindow) this.window()).worldIsExecuting();
    }
    
    final boolean hasBreakpoint() {
	return breakpoint;
    }
    
    final void setBreakpoint(boolean breakpointNow) {
	breakpoint = breakpointNow;
    }
    
    boolean isLastSlot() {
	View superview = this.superview();
	if (superview != null && superview.subviews().lastElement() == this)
	    return true;
	return false;
    }
    
    static Vector getDragOrDropList() {
	return dragOrDropList;
    }
    
    public void setScrap(RuleListItemView theScrap) {
	if (breakpoint)
	    breakpointAddOrRemove();
	boolean scrapIsComment = theScrap instanceof CommentScrap;
	if (scrap != null && scrap.superview() == this)
	    scrap.removeFromSuperview();
	if (theScrap == null || scrapIsComment) {
	    indexLabel.removeFromSuperview();
	    debugLight.removeFromSuperview();
	}
	scrap = theScrap;
	if (scrap != null) {
	    if (scrap.isInViewHierarchy())
		scrap.removeFromSuperview();
	    Class dropClass = scrap.getModelObject().getClass();
	    allowDragInto(dropClass, this);
	    allowDragOutOf(dropClass, this);
	    this.disableDrawing();
	    if (scrapIsComment)
		scrap.moveTo(0, 10);
	    else {
		if (getRuleListItem().isEnabled())
		    debugLight.setImage(light);
		else
		    debugLight.setImage(disabledRule);
		debugLight.moveTo(0, 10);
		if (!debugLight.isInViewHierarchy())
		    this.addSubview(debugLight);
		RuleListItem theThing = getRuleListItem();
		indexLabel.setTitle("6");
		indexLabel.sizeTo(debugLight.width(), indexLabel.height());
		indexLabel.moveTo(debugLight.x(), debugLight.bounds.maxY());
		if (!indexLabel.isInViewHierarchy())
		    this.addSubview(indexLabel);
		int x
		    = (indexLabel.bounds.maxX() > debugLight.bounds.maxX()
		       ? indexLabel.bounds.maxX() : debugLight.bounds.maxX());
		scrap.moveTo(x + 1, 10);
		scrap.sizeToMinSize();
	    }
	    this.addSubview(scrap);
	    sizeToMinSize();
	    this.setDirty(true);
	    this.reenableDrawing();
	}
    }
    
    public RuleListItemView getScrap() {
	return scrap;
    }
    
    public Rect getDebugLightBounds() {
	return debugLight.bounds();
    }
    
    private RuleListItem getRuleListItem(RuleListItemView scrap) {
	return (RuleListItem) scrap.getModelObject();
    }
    
    RuleListItem getRuleListItem() {
	return (RuleListItem) scrap.getModelObject();
    }
    
    public void allowDragInto(Class dropModelClass, DragDestination dest) {
	if (dest != this)
	    throw new RuntimeException
		      ("attempt to set a non-this drag destination");
	if (!dragOrDropList.contains(dropModelClass))
	    dragOrDropList.addElement(dropModelClass);
    }
    
    public DragDestination allowsDragInto(Class dropModelClass) {
	if (dragOrDropList.contains(dropModelClass))
	    return this;
	return null;
    }
    
    public void allowDragOutOf(Class dragModelClass, ExtendedDragSource ds) {
	if (ds != this)
	    throw new RuntimeException
		      ("attempt to set a non-this drag destination");
	if (!dragOrDropList.contains(dragModelClass))
	    dragOrDropList.addElement(dragModelClass);
    }
    
    public ExtendedDragSource allowsDragOutOf(Class dragModelClass) {
	if (dragOrDropList.contains(dragModelClass))
	    return this;
	return null;
    }
    
    public void hilite() {
	if (__hilitedSlot != null)
	    __hilitedSlot.unhilite();
	__hilitedSlot = this;
	super.hilite();
    }
    
    public void unhilite() {
	if (__hilitedSlot == this)
	    __hilitedSlot = null;
	super.unhilite();
    }
    
    public void drawHilite(Graphics g) {
	g.setColor(Color.black);
	int position = 5;
	if (nextHilite)
	    position = this.height() - position;
	g.fillRect(15, position - 1, this.width(), 3);
	g.drawLine(0, position, 9, position);
	g.drawLine(5, position - 3, 8, position);
	g.drawLine(5, position + 3, 8, position);
    }
    
    void hiliteNextSlot() {
	Slot s = nextSlot();
	if (s == null) {
	    hilite();
	    nextHilite = true;
	} else {
	    s.hilite();
	    nextHilite = false;
	}
    }
    
    void unhiliteNextSlot() {
	Slot s = nextSlot();
	nextHilite = false;
	unhilite();
	if (s != null)
	    s.unhilite();
    }
    
    private boolean pointIsAboveThis(Point point) {
	return point.y < this.height() / 2;
    }
    
    final boolean hiliteForPoint(Point point) {
	if (pointIsAboveThis(point)) {
	    unhiliteNextSlot();
	    hilite();
	    return true;
	}
	unhilite();
	hiliteNextSlot();
	return false;
    }
    
    final boolean hiliteForPoint(DragSession dragSession) {
	return hiliteForPoint(dragSession.destinationMousePoint());
    }
    
    final boolean hiliteForPoint(ToolSession toolSession) {
	View destinationView = toolSession.destinationView();
	if (destinationView != null
	    && (destinationView == bpView || destinationView == debugLight))
	    return false;
	return hiliteForPoint(toolSession.destinationMousePoint());
    }
    
    public boolean dragDropped(DragSession session) {
	RuleListItem myRule = getRuleListItem();
	Subroutine mySub = myRule.getSubroutine();
	int myIndex = myRule.getIndex();
	CocoaCharacter myChar
	    = ((CharacterWindow) this.window()).getCharacter();
	RuleListItemView draggedScrap = (RuleListItemView) session.data();
	RuleListItem draggedRule = getRuleListItem(draggedScrap);
	if (!pointIsAboveThis(session.destinationMousePoint())) {
	    unhiliteNextSlot();
	    myIndex++;
	}
	unhilite();
	if (worldIsExecuting())
	    return false;
	boolean successful;
	if (session.isAltKeyDown())
	    successful = draggedRule.copyTo(mySub, myChar, myIndex);
	else
	    successful = draggedRule.moveTo(mySub, myChar, myIndex);
	if (successful)
	    getWorld().setModified(true);
	return successful;
    }
    
    public boolean dragEntered(DragSession dragSession) {
	hiliteForPoint(dragSession);
	return true;
    }
    
    public boolean dragMoved(DragSession dragSession) {
	hiliteForPoint(dragSession);
	return true;
    }
    
    public void dragExited(DragSession dragSession) {
	unhilite();
	unhiliteNextSlot();
    }
    
    Slot nextSlot() {
	Vector slotList = this.superview().subviews();
	int index = slotList.indexOf(this) + 1;
	if (slotList.size() == index)
	    return null;
	return (Slot) slotList.elementAt(index);
    }
    
    public boolean prepareToDrag(Object data) {
	if (getRuleListItem().dragOrToolPermitted())
	    return true;
	PlaywriteDialog.warning(Resource.getText("dialog noDragNP"));
	return false;
    }
    
    public void dragWasAccepted(DragSession session) {
	/* empty */
    }
    
    public boolean dragWasRejected(DragSession session) {
	return true;
    }
    
    public View sourceView(DragSession session) {
	return this;
    }
    
    public void setBackgroundColor(Color color) {
	_backgroundColor = color;
	if (debugLight != null)
	    debugLight.setBackgroundColor(color);
	super.setBackgroundColor(color);
    }
    
    private final void setDebugLight(Bitmap bitmap) {
	if (debugLight != null) {
	    debugLight.setImage(bitmap);
	    debugLight.setDirty(true);
	    if (yellowBlinker.target() != this)
		getWorld().screenRefresh();
	}
    }
    
    void setLight(boolean green) {
	stopIfFlashingYellowLight();
	setDebugLight(green ? greenLight : redLight);
    }
    
    void flashYellowLight() {
	setDebugLight(yellowLight);
	yellowBlinker.stop();
	yellowBlinker.setTarget(this);
	yellowBlinker.start();
    }
    
    void stopIfFlashingYellowLight() {
	if (yellowBlinker.target() == this) {
	    yellowBlinker.stop();
	    yellowBlinker.setTarget(null);
	}
    }
    
    void resetLight() {
	if (debugLight.image() != disabledRule)
	    debugLight.setImage(light);
	debugLight.addDirtyRect(null);
	scrap.resetSubroutineLights();
    }
    
    int getIndex() {
	return Integer.parseInt(indexLabel.title());
    }
    
    void setIndex(int i) {
	indexLabel.setTitle(Integer.toString(i));
    }
    
    private World testRule() {
	World world = null;
	CharacterWindow characterWindow = (CharacterWindow) this.window();
	CocoaCharacter character = characterWindow.getCharacter();
	if (character instanceof CharacterInstance
	    && getRuleListItem().isEnabled()) {
	    world = character.getWorld();
	    CharacterInstance characterInstance
		= (CharacterInstance) character;
	    if (world.getState() != World.DEBUGGING
		&& world.getState() != World.EDIT_DEBUGGING)
		characterInstance.getEditor().resetDebuggingLights();
	    RuleListItem rule = getRuleListItem();
	    world.tick();
	    setLight(rule.matches(characterInstance));
	}
	return world;
    }
    
    void enableOrDisable() {
	unhilite();
	RuleListItem rule = getRuleListItem();
	rule.setEnabled(rule.isEnabled() ^ true);
	final Bitmap image = rule.isEnabled() ? light : disabledRule;
	rule.getViewManager().updateViews(new ViewManager.ViewUpdater() {
	    public void updateView(Object view, Object value) {
		RuleListItemView scrap = (RuleListItemView) view;
		Slot slot = (Slot) scrap.superview();
		if (slot != null) {
		    slot.debugLight.setImage(image);
		    debugLight.addDirtyRect(null);
		}
	    }
	}, null);
    }
    
    private void breakpointAddOrRemove() {
	this.disableDrawing();
	unhilite();
	breakpoint = breakpoint ^ true;
	if (breakpoint) {
	    if (bpView == null) {
		bpView = new PlaywriteView(breakpointPic);
		bpView.setBackgroundColor(_backgroundColor);
		bpView.allowTool(Tool.deleteTool, this);
	    }
	    bpView.moveTo(this.border().leftMargin() + 3, scrap.y());
	    bpView.setHorizResizeInstruction(0);
	    bpView.setVertResizeInstruction(4);
	    int sizeby = bpView.height() + 10;
	    indexLabel.moveBy(0, sizeby);
	    debugLight.moveBy(0, sizeby);
	    scrap.moveBy(0, sizeby);
	    this.sizeBy(0, sizeby);
	    this.addSubview(bpView);
	} else {
	    int sizeby = bpView.height() + 10;
	    indexLabel.moveBy(0, -sizeby);
	    debugLight.moveBy(0, -sizeby);
	    scrap.moveBy(0, -sizeby);
	    bpView.removeFromSuperview();
	    this.sizeBy(0, -sizeby);
	}
	this.reenableDrawing();
    }
    
    private boolean toolOKHack(ToolSession session) {
	Tool toolType = session.toolType();
	if (worldIsExecuting() && session.destinationView() == this
	    && (toolType == Tool.deleteTool || toolType == Tool.copyPlaceTool
		|| toolType == CharacterWindow.commentTool))
	    return false;
	if (toolType == CharacterWindow.disableTool) {
	    if (scrap instanceof CommentScrap)
		return false;
	    return true;
	}
	if (toolType == CharacterWindow.commentTool)
	    return true;
	if (toolType == Tool.copyPlaceTool)
	    return session.data() instanceof RuleListItem;
	if (toolType == Tool.deleteTool
	    && session.destinationView() == debugLight
	    && getRuleListItem().isEnabled())
	    return false;
	return super.toolEntered(session);
    }
    
    public boolean toolEntered(ToolSession session) {
	return toolOKHack(session);
    }
    
    public boolean toolMoved(ToolSession session) {
	hiliteForPoint(session);
	return super.toolMoved(session);
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool toolType = session.toolType();
	RuleListItem myRule = getRuleListItem();
	Subroutine mySub = myRule.getSubroutine();
	int myIndex = myRule.getIndex();
	boolean successful = true;
	Slot nextSlot = nextSlot();
	unhilite();
	unhiliteNextSlot();
	if (__hilitedSlot != null)
	    __hilitedSlot.unhilite();
	if (!toolOKHack(session))
	    return false;
	if (toolType == CharacterWindow.disableTool)
	    enableOrDisable();
	else if (toolType == CharacterWindow.commentTool) {
	    if (!pointIsAboveThis(session.destinationMousePoint()))
		myIndex++;
	    mySub.getMainSubroutine().disableDrawingOnAllViews();
	    mySub.add(new Comment(), myIndex);
	    mySub.getMainSubroutine().reenableDrawingOnAllViews();
	} else if (toolType == Tool.copyPlaceTool) {
	    if (!pointIsAboveThis(session.destinationMousePoint()))
		myIndex++;
	    if (session.data() instanceof RuleListItem) {
		RuleListItem rli = (RuleListItem) session.data();
		CocoaCharacter myChar
		    = ((CharacterWindow) this.window()).getCharacter();
		successful = rli.copyTo(mySub, myChar, myIndex);
	    } else {
		Debug.print("debug.slot", "the class of the data is not RLI: ",
			    session.data().getClass());
		return false;
	    }
	} else {
	    if (toolType == CharacterWindow.breakpointTool) {
		if (!pointIsAboveThis(session.destinationMousePoint())) {
		    if (nextSlot != null)
			nextSlot.breakpointAddOrRemove();
		} else
		    breakpointAddOrRemove();
		return true;
	    }
	    if (toolType == Tool.deleteTool) {
		if (session.destinationView() == debugLight) {
		    if (getRuleListItem().isEnabled())
			return false;
		    enableOrDisable();
		} else if (session.destinationView() == bpView)
		    breakpointAddOrRemove();
	    }
	}
	if (successful)
	    getWorld().setModified(true);
	return successful;
    }
    
    public void toolDragged(ToolSession session) {
	Tool toolType = session.toolType();
	if (toolType == Tool.deleteTool) {
	    if (session.destinationView() == debugLight) {
		if (getRuleListItem().isEnabled())
		    return;
		enableOrDisable();
	    } else if (session.destinationView() == bpView) {
		breakpointAddOrRemove();
		return;
	    }
	} else
	    return;
	getWorld().setModified(true);
    }
    
    public void sizeToMinSize() {
	if (isLastSlot() && scrap != null)
	    this.sizeTo(scrap.bounds.maxX(), scrap.bounds.maxY() + 10);
	else
	    this.sizeTo(scrap.bounds.maxX(), scrap.bounds.maxY());
    }
    
    public Size minSize() {
	Size size = super.minSize();
	if (isLastSlot() && scrap != null)
	    size.height += 10;
	return size;
    }
    
    public void subviewDidResize(View subview) {
	sizeToMinSize();
    }
    
    public void performCommand(String command, Object arg) {
	if (command == "TESTRULE") {
	    if (yellowBlinker.target() == this)
		performCommand("STEPRULE", arg);
	    else
		Application.application()
		    .performCommandLater(this, "ReallyTestRule", arg, true);
	} else if (command == "STEPRULE")
	    Application.application()
		.performCommandLater(this, "ReallyStepRule", arg, true);
	else if (command == "ReallyTestRule") {
	    World w = testRule();
	    if (w != null)
		w.getWorldView().getControlPanelView().performCommand("smokin",
								      this);
	} else if (command == "ReallyStepRule") {
	    getWorld().clearDebug();
	    getWorld().getWorldView().getControlPanelView()
		.performCommand("smokin", this);
	} else if (command.equals("DISENABLE")) {
	    enableOrDisable();
	    getWorld().setModified(true);
	} else if (command == BLINK_COMMAND) {
	    if (debugLight.image() == yellowLight)
		setDebugLight(blackLight);
	    else
		setDebugLight(yellowLight);
	} else
	    super.performCommand(command, arg);
    }
    
    public void discard() {
	stopIfFlashingYellowLight();
	if (getScrap() != null && getScrap().superview() != this)
	    getScrap().discard();
	super.discard();
	scrap = null;
	indexLabel = null;
	debugLight = null;
	bpView = null;
    }
    
    public String toString() {
	String out = "Slot for " + indexLabel.title();
	Object modObject = this.getModelObject();
	if (modObject != null) {
	    if (modObject instanceof Named)
		out += ((Named) modObject).getName();
	} else
	    out += "?";
	return out;
    }
}
