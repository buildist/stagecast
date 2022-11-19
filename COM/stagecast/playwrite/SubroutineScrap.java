/* SubroutineScrap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.ListItem;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Popup;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.ScrollView;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class SubroutineScrap extends RuleScrap
    implements DragDestination, Debug.Constants,
	       ResourceIDs.CharacterWindowIDs, ResourceIDs.DialogIDs
{
    private static final String FINGER_DRAG = "FINGER_DRAG";
    private static final String CHANGE_TYPE = "CHANGE_TYPE";
    private static final String EDIT_PRETEST = "EDIT_PRETEST";
    private static final String REORDER_SLOTS = "REORDER_SLOTS";
    private static final int INDENT = 20;
    private static ProgressDialog _progressDialog = null;
    private Popup typePopup;
    private RuleListView ruleView;
    private PlaywriteView ruleArrowView;
    private PlaywriteView pretestView;
    private TextView ruleName;
    private PlaywriteButton opener;
    private boolean open = false;
    private CocoaCharacter self;
    private boolean reorderingSlots = false;
    private final boolean isMain;
    private PlaywriteView fingerView;
    private int fingerValue = -1;
    private boolean fingerDragging = false;
    private FingerVariable _fingerVariable;
    private Watcher _fingerWatcher;
    private boolean discarded = false;
    
    SubroutineScrap(Subroutine sub, CocoaCharacter self) {
	super(sub);
	isMain = false;
	this.self = self;
	opener = Util.createHorizHandle("FOO", this);
	opener.setToolTipText(Resource.getToolTip("cwOpenSub"));
	this.addBorderControl(opener);
	Util.centerViewHorizontally(opener);
	ruleView = new RuleListView();
	ruleView.setBackgroundColor(this.getWorld().getLightColor());
	createContentsView();
	this.allowTool(Tool.copyPlaceTool, this);
	layoutView(0, 0);
    }
    
    SubroutineScrap(CocoaCharacter self) {
	super(self.getMainSubroutine(), false);
	Debug.print("debug.subroutine.scrap",
		    "generating main subroutine for ", self);
	isMain = true;
	this.self = self;
	ruleView = new RuleListView();
	this.addSubview(ruleView);
	this.setBorder(null);
	this.sizeToMinSize();
	Debug.print("debug.subroutine.scrap", "finished main subroutine for ",
		    self);
	this.disallowTool(Tool.deleteTool);
	this.disallowTool(Tool.copyLoadTool);
	this.disallowTool(CharacterWindow.disableTool);
	this.allowTool(Tool.copyPlaceTool, this);
	this.allowTool(CharacterWindow.commentTool, this);
    }
    
    protected PlaywriteView createVisualRule() {
	visualRule = new PlaywriteView();
	visualRule.setBackgroundColor(Util.ruleScrapColor);
	visualRule.setMouseTransparency(true);
	Subroutine sub = getSubroutine();
	Vector possibleTypes = Subroutine.getTypeNames();
	Vector possibleClasses = Subroutine.getTypeClasses();
	typePopup = new GreenPopup(this.border().leftMargin(),
				   this.border().topMargin(), 100, 16, true) {
	    public boolean mouseDown(MouseEvent event) {
		if (!SubroutineScrap.this.isEditable())
		    return false;
		return super.mouseDown(event);
	    }
	};
	typePopup.setTarget(this);
	for (int i = 0; i < possibleTypes.size(); i++) {
	    String typeString = (String) possibleTypes.elementAt(i);
	    Class typeClass = (Class) possibleClasses.elementAt(i);
	    ListItem item
		= typePopup.addItem(typeString + "  ", "CHANGE_TYPE");
	    item.setData(typeClass);
	    if (typeClass == sub.getType().getClass())
		typePopup.selectItemAt(i);
	}
	typePopup.sizeToMinSize();
	visualRule.addSubview(typePopup);
	this.allowTool(CharacterWindow.pretestTool, this);
	if (sub.getPretest() != null)
	    addPretestView(null);
	return visualRule;
    }
    
    public void mouseDragged(MouseEvent event) {
	if (fingerDragging) {
	    this.addDirtyRect(fingerView.bounds());
	    if (event.y >= ruleView.y()
		&& event.y <= ruleView.bounds.maxY() - 20) {
		fingerView.moveTo(fingerView.x(), event.y);
		this.addDirtyRect(fingerView.bounds());
	    }
	} else
	    super.mouseDragged(event);
    }
    
    public void drawViewBackground(Graphics g) {
	if (opener != null) {
	    g.setColor(this.backgroundColor());
	    g.fillRect(0, 0, bounds.width, bounds.height - opener.height());
	} else
	    super.drawViewBackground(g);
    }
    
    public void drawViewBorder(Graphics g) {
	if (opener != null) {
	    this.border().drawInRect(g, 0, 0, this.width(),
				     this.height() - opener.height());
	    if (this.isHilited())
		Util.drawHilited(g, new Rect(0, 0, this.width(),
					     this.height() - opener.height()));
	} else
	    super.drawViewBorder(g);
    }
    
    public void mouseUp(MouseEvent event) {
	if (fingerDragging) {
	    fingerDragging = false;
	    Vector RVs = getRuleViews();
	    for (int i = 0; i < RVs.size(); i++) {
		Slot slot = (Slot) RVs.elementAt(i);
		Rect slotBounds
		    = ruleView.convertRectToView(this, slot.bounds);
		if (event.y < slotBounds.maxY()) {
		    SequenceSubType sub
			= (SequenceSubType) getSubroutine().getType();
		    sub.setFingerVariable(self,
					  slot.getRuleListItem().getIndex());
		    return;
		}
	    }
	}
	super.mouseUp(event);
    }
    
    void layoutPretest() {
	if (pretestView != null) {
	    int x = 0;
	    int y = 0;
	    pretestView.moveTo(x, y);
	    x = pretestView.bounds.maxX() + 10;
	    y = pretestView.bounds.maxY() - ruleArrowView.height();
	    if (y <= 0)
		y = 0;
	    else
		y /= 2;
	    ruleArrowView.moveTo(x, y);
	    x = x + ruleArrowView.width() + 10;
	    y = (ruleArrowView.y()
		 + (ruleArrowView.height() - typePopup.height()) / 2);
	    typePopup.moveTo(x, y);
	}
	visualRule.sizeToMinSize();
    }
    
    public void layoutView(int dx, int dy) {
	if (isMain)
	    this.sizeTo(ruleView.width(), ruleView.height());
	else {
	    layoutPretest();
	    outerBound.x = 0;
	    outerBound.y = 0;
	    super.layoutView(dx, dy);
	    outerBound.y = this.getRuleBottom() + this.border().bottomMargin();
	    if (open && ruleView.subviews().size() > 0) {
		ruleView.moveTo(this.border().leftMargin() + 20, outerBound.y);
		this.maximize(outerBound, ruleView);
		if (fingerView != null)
		    syncFingerLocation(fingerValue);
	    }
	    if (opener != null) {
		opener.moveTo((outerBound.x - opener.width()) / 2,
			      outerBound.y);
		outerBound.y
		    += Math.max(opener.height(), this.border().bottomMargin());
	    }
	    this.sizeTo(outerBound.x, outerBound.y);
	}
    }
    
    public void subviewDidResize(View subview) {
	if (!reorderingSlots) {
	    if (subview == ruleView) {
		if (getSubroutine().getItemCount() == 0)
		    hideOpener();
		else
		    showOpener();
		layoutView(0, 0);
	    } else
		super.subviewDidResize(subview);
	}
    }
    
    public void addRule(RuleListItem r) {
	ruleView.addRuleListItemView(r.createScrap(self));
	showOpener();
    }
    
    final Vector getRuleViews() {
	return ruleView.subviews();
    }
    
    final void showRule(int index) {
	Slot slot = (Slot) ruleView.subviews().elementAt(index);
	ruleView.scrollRectToVisible(slot.bounds);
    }
    
    protected Rule getRule() {
	return getSubroutine().getPretest();
    }
    
    final Subroutine getSubroutine() {
	return (Subroutine) this.getModelObject();
    }
    
    final CocoaCharacter getCharacter() {
	return self;
    }
    
    String getRuleName() {
	return getSubroutine().getName();
    }
    
    void setRuleName(String name) {
	Subroutine subroutine = getSubroutine();
	subroutine.setName(name);
	subroutine.getWorld().setModified(true);
    }
    
    String getComment() {
	return getSubroutine().getComment();
    }
    
    void setComment(String name) {
	Subroutine subroutine = getSubroutine();
	subroutine.setComment(name);
	subroutine.getWorld().setModified(true);
    }
    
    CharacterInstance getCharacterInstance() {
	CocoaCharacter test = getCharacter();
	if (test instanceof CharacterInstance)
	    return (CharacterInstance) test;
	return null;
    }
    
    private void showOpener() {
	if (opener != null) {
	    if (opener.superview() == null)
		this.addBorderControl(opener);
	}
    }
    
    private void hideOpener() {
	if (opener != null)
	    opener.removeFromSuperview();
    }
    
    void syncFingerLocation(int i) {
	fingerValue = i;
	if (open) {
	    if (fingerView == null)
		Debug.print("debug.subroutine", "null fingerView");
	    else {
		if (!fingerView.isInViewHierarchy())
		    this.addSubview(fingerView);
		int lastSlot = getRuleViews().size();
		if (lastSlot != 0) {
		    if (fingerValue >= lastSlot && lastSlot != 0)
			_fingerVariable.setValue(self,
						 new Integer(lastSlot - 1));
		    else {
			Slot slot
			    = (Slot) getRuleViews().elementAt(fingerValue);
			Rect debugRect = slot.getDebugLightBounds();
			debugRect.x += slot.bounds.x + ruleView.bounds.x;
			debugRect.y += slot.bounds.y + ruleView.bounds.y;
			this.addDirtyRect(fingerView.bounds());
			fingerView.moveTo(debugRect.x - fingerView.width() - 2,
					  debugRect.midY());
			this.addDirtyRect(fingerView.bounds());
		    }
		}
	    }
	}
    }
    
    void resetSubroutineLights() {
	Vector slots = getRuleViews();
	Slot slot = null;
	for (int i = 0; i < slots.size(); i++) {
	    slot = (Slot) slots.elementAt(i);
	    slot.resetLight();
	}
    }
    
    void reorderSlots(Vector reorderedRuleList) {
	if (getRuleViews() != null) {
	    World world = this.getWorld();
	    if (world.inWorldThread())
		Application.application().performCommandAndWait
		    (this, "REORDER_SLOTS", reorderedRuleList);
	    else
		_reorderSlots(reorderedRuleList);
	}
    }
    
    private void _reorderSlots(Vector reorderedRuleList) {
	if (!discarded) {
	    Slot slot = null;
	    reorderingSlots = true;
	    int numberOfRules = getRuleViews().size();
	    if (reorderedRuleList.size() != numberOfRules) {
		Thread.dumpStack();
		throw new RuntimeException
			  ("ruleList and viewList out of sync");
	    }
	    if (reorderedRuleList.size() == 0) {
		hideOpener();
		hideRules();
	    }
	    Slot[] slots = new Slot[getRuleViews().size()];
	    getRuleViews().copyInto(slots);
	    ruleView.disableDrawing();
	    for (int i = 0; i < numberOfRules; i++) {
		slot = slots[i];
		slot.removeFromSuperview();
		RuleListItem rule = slot.getRuleListItem();
		rule.setIndex(i);
	    }
	    int slotNumber = 1;
	    for (int i = 0; i < numberOfRules; i++) {
		RuleListItem rule
		    = (RuleListItem) reorderedRuleList.elementAt(i);
		slot = slots[rule.getIndex()];
		if (!(slot.getScrap() instanceof CommentScrap))
		    slot.setIndex(slotNumber++);
		rule.setIndex(i);
		ruleView.addSlot(slot);
	    }
	    ruleView.setDirty(true);
	    if (slot != null)
		slot.sizeToMinSize();
	    ruleView.reenableDrawing();
	    reorderingSlots = false;
	    layoutView(0, 0);
	}
    }
    
    void setPopupToCurrentType() {
	Subroutine sub = getSubroutine();
	getSubroutine();
	Vector possibleClasses = Subroutine.getTypeClasses();
	Enumeration classes = possibleClasses.elements();
	Class subType = null;
	while (classes.hasMoreElements()) {
	    subType = (Class) classes.nextElement();
	    if (sub.getType().getClass() == subType) {
		typePopup
		    .selectItemAt(possibleClasses.indexOfIdentical(subType));
		break;
	    }
	}
    }
    
    void updatePopups(final int newSelection) {
	getSubroutine().getViewManager()
	    .updateViews(new ViewManager.ViewUpdater() {
	    public void updateView(Object view, Object value) {
		((SubroutineScrap) view).typePopup.selectItemAt(newSelection);
	    }
	}, null);
    }
    
    void addFingerView(PlaywriteView view, FingerVariable fingerVariable) {
	if (fingerView != null)
	    throw new RuntimeException("fingerview already exists!");
	fingerView = view;
	fingerView.setCursor(12);
	fingerView.setMouseTransparency(true);
	fingerView.setEventDelegate(-1, 0, 1, "FINGER_DRAG", this);
	_fingerVariable = fingerVariable;
	_fingerWatcher = new Watcher() {
	    public void update(Object target, Object value) {
		syncFingerLocation(((Integer) value).intValue());
	    }
	};
	fingerVariable.addValueWatcher(self, _fingerWatcher);
	syncFingerLocation(((Integer) fingerVariable.getValue(self))
			       .intValue());
    }
    
    void removeFingerView(FingerVariable fv) {
	if (fingerView.isInViewHierarchy())
	    this.addDirtyRect(fingerView.bounds());
	_fingerVariable.removeValueWatcher(self, _fingerWatcher);
	fingerView.removeFromSuperview();
	fingerView = null;
	if (_fingerVariable != fv)
	    Debug.print(true, "removing fingerView: known var is ",
			_fingerVariable, " but was passed ", fv);
	_fingerVariable = null;
	_fingerWatcher = null;
	fingerValue = -1;
    }
    
    private boolean isEditable() {
	return this.getWorld().getState() == World.STOPPED;
    }
    
    public DragDestination allowsDragInto(Class dropModelClass) {
	if (Slot.getDragOrDropList().contains(dropModelClass))
	    return this;
	return null;
    }
    
    public boolean dragDropped(DragSession session) {
	Subroutine mySub = getSubroutine();
	CocoaCharacter myChar
	    = ((CharacterWindow) this.window()).getCharacter();
	RuleListItemView draggedScrap = (RuleListItemView) session.data();
	RuleListItem draggedItem = draggedScrap.getItem();
	unhilite();
	if (!isEditable())
	    return false;
	boolean successful;
	if (draggedItem == mySub)
	    successful = false;
	else if (session.isAltKeyDown())
	    successful = draggedItem.copyTo(mySub, myChar, 0);
	else
	    successful = draggedItem.moveTo(mySub, myChar, 0);
	if (successful)
	    this.getWorld().setModified(true);
	return successful;
    }
    
    public boolean dragEntered(DragSession dragSession) {
	if (isEditable()) {
	    hilite();
	    return true;
	}
	return false;
    }
    
    public boolean dragMoved(DragSession dragSession) {
	if (isEditable()) {
	    hilite();
	    return true;
	}
	return false;
    }
    
    public void dragExited(DragSession dragSession) {
	unhilite();
    }
    
    public void hilite() {
	View superview = this.superview();
	if (superview instanceof ScrollView)
	    ((PlaywriteView) superview.superview()).hilite();
	else
	    super.hilite();
    }
    
    public void unhilite() {
	View superview = this.superview();
	if (superview instanceof ScrollView)
	    ((PlaywriteView) superview.superview()).unhilite();
	else
	    super.unhilite();
    }
    
    public boolean toolEntered(ToolSession session) {
	if (this.getItem().dragOrToolPermitted()
	    && session.toolType() == Tool.copyPlaceTool)
	    return session.data() instanceof RuleListItem;
	return super.toolEntered(session);
    }
    
    public boolean toolMoved(ToolSession session) {
	if (this.getItem().dragOrToolPermitted()
	    && session.toolType() == Tool.copyPlaceTool)
	    return session.data() instanceof RuleListItem;
	return super.toolMoved(session);
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool toolType = session.toolType();
	if (!this.getItem().dragOrToolPermitted()) {
	    if (toolType == Tool.deleteTool)
		PlaywriteDialog.warning(Resource.getText("dialog dltNP"));
	    else if (toolType == Tool.copyLoadTool)
		PlaywriteDialog.warning(Resource.getText("dialog cpyNP"));
	    else
		PlaywriteDialog.warning(Resource.getText("dialog toolNP"));
	    return false;
	}
	if (toolType == CharacterWindow.commentTool
	    && getSubroutine().isMainSubroutine()) {
	    unhilite();
	    Subroutine mySub = getSubroutine();
	    mySub.disableDrawingOnAllViews();
	    mySub.add(new Comment());
	    mySub.reenableDrawingOnAllViews();
	    return true;
	}
	if (toolType == CharacterWindow.pretestTool) {
	    CharacterInstance myCharacter = getCharacterInstance();
	    if (myCharacter == null)
		return false;
	    RuleEditor.beginPretestMode(getSubroutine());
	    RuleEditor.startRecording(myCharacter,
				      getSubroutine().getPretest());
	    return true;
	}
	if (toolType == Tool.deleteTool
	    && session.destinationView() == pretestView) {
	    if (pretestView != null) {
		getSubroutine().removePretest();
		return true;
	    }
	    return false;
	}
	if (toolType == Tool.copyPlaceTool) {
	    unhilite();
	    if (session.data() instanceof RuleListItem) {
		RuleListItem copyItem = (RuleListItem) session.data();
		Subroutine mySub = getSubroutine();
		CocoaCharacter myChar
		    = ((CharacterWindow) this.window()).getCharacter();
		boolean successful = copyItem.copyTo(mySub, myChar, 0);
		if (successful)
		    this.getWorld().setModified(true);
		return successful;
	    }
	    return false;
	}
	return super.toolClicked(session);
    }
    
    void showRules() {
	if (!open) {
	    if (!PlaywriteRoot.app().inEventThread())
		Debug.stackTrace("warning: draw called from non-Event thread");
	    open = true;
	    layoutView(0, 0);
	    getSubroutine().getMainSubroutine().getViewFor(getCharacter())
		.draw();
	    this.addSubview(ruleView);
	    ruleView.draw();
	    opener.setToolTipText(Resource.getToolTip("cwCloseSub"));
	}
    }
    
    void hideRules() {
	if (open) {
	    Debug.print("debug.subroutine.scrap", "CLOSE");
	    ruleView.removeFromSuperview();
	    open = false;
	    layoutView(0, 0);
	    opener.setToolTipText(Resource.getToolTip("cwOpenSub"));
	    this.superview().scrollRectToVisible(this.bounds());
	}
    }
    
    public void performCommand(String command, Object arg) {
	if (arg == opener || command.equals("Open Editor")) {
	    if (!isMain) {
		if (getSubroutine().numberOfRules() == 0)
		    hideRules();
		else if (open)
		    hideRules();
		else
		    showRules();
	    }
	} else if (command.equals("FINGER_DRAG"))
	    fingerDragging = true;
	else if (command.equals("CHANGE_TYPE")) {
	    if (getSubroutine()
		    .convertToType((Class) typePopup.selectedItem().data())) {
		updatePopups(typePopup.selectedIndex());
		this.getWorld().setModified(true);
	    } else
		setPopupToCurrentType();
	    visualRule.sizeToMinSize();
	    layoutView(0, 0);
	} else if (command.equals("EDIT_PRETEST")) {
	    RuleEditor.beginPretestMode(getSubroutine());
	    super.performCommand("Open Editor", this);
	} else if (command == "REORDER_SLOTS")
	    _reorderSlots((Vector) arg);
	else
	    super.performCommand(command, arg);
    }
    
    void addPretestView(PlaywriteView view) {
	boolean newPretest = pretestView == null;
	if (view == null)
	    pretestView = getSubroutine().getPretest()
			      .createMiniRuleView(Util.ruleScrapColor);
	else if (newPretest)
	    pretestView = new PlaywriteView(view.image());
	else {
	    Image image = view.image();
	    pretestView.setImage(image);
	    pretestView.setMinSize(image.width(), image.height());
	    pretestView.sizeToMinSize();
	    pretestView.setDirty(true);
	}
	if (newPretest) {
	    visualRule.addSubview(pretestView);
	    ruleArrowView
		= new PlaywriteView(Resource.getImage("LittleArrow"));
	    ruleArrowView.setMouseTransparency(true);
	    visualRule.addSubview(ruleArrowView);
	    pretestView.setEventDelegate(-3, 0, 2, "EDIT_PRETEST", this);
	    pretestView.setBackgroundColor(Color.white);
	    pretestView.allowTool(Tool.deleteTool, this);
	}
	pretestView.setMouseTransparency(true);
	this.setDragImageDirty();
    }
    
    void removePretestView() {
	if (pretestView != null) {
	    pretestView.removeFromSuperview();
	    pretestView = null;
	    ruleArrowView.removeFromSuperview();
	    if (typePopup != null)
		typePopup.moveTo(this.border().leftMargin(),
				 this.border().topMargin());
	    layoutView(0, 0);
	    this.allowTool(CharacterWindow.pretestTool, this);
	}
    }
    
    void hilitePretestView() {
	if (pretestView != null)
	    pretestView.hilite();
    }
    
    void unhilitePretestView() {
	if (pretestView != null)
	    pretestView.unhilite();
    }
    
    RuleListView createContentsView() {
	Subroutine subroutine = getSubroutine();
	boolean isMain = subroutine.isMainSubroutine();
	RuleListView view = ruleView;
	Vector rules = subroutine.getRules();
	Debug.print("debug.subroutine.scrap", "generating ruleviews");
	if (_progressDialog == null && getSubroutine().isMainSubroutine()) {
	    int count = getSubroutine().getItemCount();
	    if (count > 0) {
		_progressDialog
		    = new ProgressDialog(250, 50,
					 Resource.getText("dialog ocw"));
		_progressDialog.setTotalCount(count);
		_progressDialog.show();
	    }
	}
	int numRules = rules.size();
	if (numRules == 0)
	    hideOpener();
	for (int i = 0; i < rules.size(); i++) {
	    RuleListItem rli = (RuleListItem) rules.elementAt(i);
	    Debug.print("debug.subroutine.scrap", " rv for :", rli);
	    RuleListItemView sop = rli.createScrap(self);
	    Debug.print("debug.subroutine.scrap", "  finished rv ", rli);
	    if (sop != null) {
		Slot newSlot = view.addRuleListItemView(sop);
		if (isMain)
		    newSlot.draw();
		if (_progressDialog != null)
		    _progressDialog.incrementTotalDone(1);
	    }
	}
	View lastSlot = (View) view.subviews().lastElement();
	if (lastSlot != null)
	    lastSlot.sizeToMinSize();
	if (_progressDialog != null && getSubroutine().isMainSubroutine()) {
	    _progressDialog.hide();
	    _progressDialog = null;
	}
	return view;
    }
    
    public void setBackgroundColor(Color color) {
	if (ruleView != null) {
	    ruleView.setBackgroundColor(color);
	    Vector slots = getRuleViews();
	    for (int i = 0; i < slots.size(); i++) {
		Slot slot = (Slot) slots.elementAt(i);
		slot.setBackgroundColor(color);
	    }
	}
	super.setBackgroundColor(color);
    }
    
    public void discard() {
	discarded = true;
	if (fingerView != null)
	    removeFingerView(_fingerVariable);
	super.discard();
	if (ruleView != null && !ruleView.isInViewHierarchy()) {
	    Vector ruleList = getRuleViews();
	    for (int i = 0; i < ruleList.size(); i++)
		((Slot) ruleList.elementAt(i)).discard();
	}
	getSubroutine().removeView(this);
    }
    
    public String toString() {
	String foo = super.toString();
	if (getSubroutine() != null)
	    foo += " " + getSubroutine().getName();
	if (self != null)
	    foo += self.toString();
	return foo;
    }
}
