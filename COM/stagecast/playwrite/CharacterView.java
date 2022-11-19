/* CharacterView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.ListItem;
import COM.stagecast.ifc.netscape.application.ListView;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.ScrollView;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.Timer;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class CharacterView extends PlaywriteView
    implements Debug.Constants, DragDestination, Flashable,
	       ResourceIDs.CommandIDs, ResourceIDs.DialogIDs,
	       ResourceIDs.RuleEditorIDs, Target, ToolDestination, Watcher,
	       Worldly
{
    static final int DELAY = 500;
    static String DISPLAY_POPUP = "display popup";
    static String GENERALIZE_CHARACTER = "GENERALIZE_CHARACTER";
    static String OPEN_CHARACTER_WINDOW = "OPEN_CHARACTER_WINDOW";
    static long lastMouseUpTime = 0L;
    static CocoaCharacter lastMouseUpCharacter = null;
    static boolean mouseIsDown = false;
    static MouseEvent mouseEvent = null;
    static Point mousePoint = null;
    static InternalWindow popupWindow = null;
    static ListView popupList = null;
    private Timer timer;
    private int oldWidth = -1;
    private int oldHeight = -1;
    private int fixedSize;
    private boolean _isFlashing = false;
    private Vector _disabledTools = null;
    private int _zValueCache = -1;
    private boolean _rolledOver = false;
    
    public CharacterView(CocoaCharacter ch, int fixedSize) {
	this.setBorder(null);
	this.setTransparent(true);
	this.fixedSize = fixedSize;
	setModelObject(ch);
	resize();
	if (!ch.isVisible()) {
	    saveSize();
	    this.sizeTo(0, 0);
	}
    }
    
    public CharacterView(CocoaCharacter ch) {
	this(ch, 0);
    }
    
    public final CocoaCharacter getCharacter() {
	return (CocoaCharacter) this.getModelObject();
    }
    
    final void setCharacter(CocoaCharacter ch) {
	setModelObject(ch);
    }
    
    final CharacterWindow getEditor() {
	return getCharacter().getEditor();
    }
    
    final void setEditor(CharacterWindow editor) {
	getCharacter().setEditor(editor);
    }
    
    final boolean isScaled() {
	return fixedSize > 0;
    }
    
    final int getScaleSize() {
	return fixedSize;
    }
    
    public World getWorld() {
	return getCharacter().getWorld();
    }
    
    public void setModelObject(Object newModel) {
	Object previousModel = this.getModelObject();
	super.setModelObject(newModel);
	if (previousModel != null && previousModel instanceof CocoaCharacter) {
	    CocoaCharacter previousCharacter = (CocoaCharacter) previousModel;
	    previousCharacter.removeView(this);
	    Variable.systemVariable
		(CocoaCharacter.SYS_ROLLOVER_ENABLED_VARIABLE_ID,
		 previousCharacter)
		.removeValueWatcher(previousCharacter, this);
	}
	if (newModel instanceof CocoaCharacter) {
	    CocoaCharacter newCharacter = (CocoaCharacter) newModel;
	    newCharacter.addView(this);
	    Variable.systemVariable
		(CocoaCharacter.SYS_ROLLOVER_ENABLED_VARIABLE_ID, newCharacter)
		.addValueWatcher(newCharacter, this);
	}
	if (newModel != null && PlaywriteRoot.isAuthoring()) {
	    if (newModel instanceof GeneralizedCharacter
		|| newModel instanceof GCAlias)
		allowTool(RuleEditor.variableWindowTool, this);
	    if (newModel instanceof CharacterPrototype
		&& PlaywriteRoot.isPlayer()) {
		disallowTool(Tool.copyLoadTool);
		disallowTool(Tool.deleteTool);
	    }
	    resize();
	}
    }
    
    final int getZValueCache() {
	return _zValueCache;
    }
    
    final void setZValueCache(int z) {
	_zValueCache = z;
    }
    
    public void discard() {
	CocoaCharacter myChar = getCharacter();
	if (myChar != null) {
	    myChar.removeView(this);
	    Variable.systemVariable
		(CocoaCharacter.SYS_ROLLOVER_ENABLED_VARIABLE_ID, myChar)
		.removeValueWatcher(myChar, this);
	}
	super.setModelObject(null);
	if (timer != null)
	    timer.stop();
	timer = null;
	lastMouseUpCharacter = null;
	mouseEvent = null;
	mousePoint = null;
	popupWindow = null;
	if (popupList != null)
	    popupList.removeAllItems();
	popupList = null;
	super.discard();
    }
    
    public void ancestorWasAddedToViewHierarchy(View view) {
	resize();
	if (view == this) {
	    View container = this.superview();
	    if (container != null)
		container.addDirtyRect(this.bounds());
	}
    }
    
    public void setDirty(boolean b) {
	if (b) {
	    View container = this.superview();
	    if (container != null)
		container.addDirtyRect(this.bounds());
	}
	super.setDirty(b);
    }
    
    public void setBounds(int x, int y, int width, int height) {
	View container = this.superview();
	if (container != null)
	    container.addDirtyRect(this.bounds());
	if (isScaled() && width != 0 && height != 0) {
	    width = fixedSize;
	    height = fixedSize;
	}
	super.setBounds(x, y, width, height);
	if (container != null)
	    container.addDirtyRect(this.bounds());
    }
    
    public Image getDragImage() {
	COM.stagecast.ifc.netscape.application.Bitmap result = null;
	if (getCharacter().getContainer() instanceof Board)
	    result = getCharacter().getCurrentAppearance()
			 .getBitmapAtSquareSize(getSquareSize());
	else if (isScaled())
	    result
		= getCharacter().getCurrentAppearance().getBitmap(fixedSize);
	return result;
    }
    
    public void draw(CocoaCharacter ch, Graphics g, int x, int y,
		     int squareSize) {
	if (ch.isVisible()) {
	    if (!_rolledOver)
		ch.getCurrentAppearance().draw(this, g, x, y, squareSize);
	    else {
		Appearance app
		    = ((Appearance)
		       Variable.systemVariable
			   (CocoaCharacter.SYS_ROLLOVER_APPEARANCE_VARIABLE_ID,
			    ch)
			   .getValue(ch));
		app.draw(this, g, x, y, squareSize);
	    }
	}
    }
    
    public void drawView(Graphics g) {
	if (getCharacter().isVisible()) {
	    CocoaCharacter character = getCharacter();
	    Appearance a = character.getCurrentAppearance();
	    if (isScaled())
		a.drawFixed(this, g, 0, 0, fixedSize);
	    else
		a.draw(this, g, 0, 0, a.getSquareSize());
	}
    }
    
    private GeneralizedCharacter getGCForGeneralization() {
	CocoaCharacter character = getCharacter();
	if (character instanceof GeneralizedCharacter)
	    return (GeneralizedCharacter) character;
	if (character instanceof GCAlias && RuleEditor.getRuleEditor() != null
	    && RuleEditor.getRuleEditor().editingPretest())
	    return ((GCAlias) character).findOriginal();
	return null;
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals(OPEN_CHARACTER_WINDOW)) {
	    if (PlaywriteRoot.isAuthoring()) {
		PlaywriteRoot.markBusy();
		Selection.hideModalView();
		((CocoaCharacter) data).edit();
		PlaywriteRoot.clearBusy();
		mouseEvent = null;
	    } else
		PlaywriteDialog
		    .warning("dialog no character window in player");
	} else if (command.equals(DISPLAY_POPUP)) {
	    CocoaCharacter character = getCharacter();
	    timer.stop();
	    timer = null;
	    if (this.convertRectToView(null, this.localBounds())
		    .contains(PlaywriteRoot.getMainRootView().mousePoint())) {
		GeneralizedCharacter gc = getGCForGeneralization();
		if (mouseIsDown && gc != null)
		    displayPopupList();
		else if (mouseEvent != null) {
		    if (this.superview() instanceof BoardView) {
			BoardView boardView = (BoardView) this.superview();
			Board board = boardView.getBoard();
			int h = boardView.squareH(mouseEvent.x);
			int v = boardView.squareV(mouseEvent.y);
			Vector square = board.getVisibleCharacters(h, v);
			square.addElementIfAbsent(getCharacter());
			if (square != null && square.size() > 1)
			    Selection.showModalView
				(new SquareView((BoardView) this.superview(),
						square, mousePoint));
		    }
		}
	    }
	} else if (command.equals(GENERALIZE_CHARACTER)) {
	    GeneralizedCharacter gch = getGCForGeneralization();
	    ListItem item = popupList.selectedItem();
	    popupWindow.hide();
	    popupWindow = null;
	    popupList = null;
	    Selection.resetGlobalState();
	    if (item != null) {
		Bindable type = (Bindable) item.data();
		Debug.print("debug.popup", "performCommand(", command, ", ",
			    type, ") on character = ", gch);
		if (type != null && type != gch.getValueType()) {
		    if (!PlaywriteRoot.isPlayer()
			&& !RuleEditor.isRecordingOrEditing())
			PlaywriteDialog.warning("dialog ebg");
		    else {
			gch.setValueType(type);
			Debug.print("debug.popup",
				    "performCommand set type of ", gch, " to ",
				    type);
		    }
		}
	    }
	} else
	    Debug.print("debug.commands", "Unknown command: ", command);
    }
    
    private void displayPopupList() {
	popupList = buildGeneralizationPopup();
	popupWindow = PlaywritePopup.displayPopupList(popupList);
	if (mouseEvent != null)
	    popupList.mouseDown(this.convertEventToView(popupList,
							mouseEvent));
    }
    
    private ListView buildGeneralizationPopup() {
	GeneralizedCharacter gch;
	if (getCharacter() instanceof GeneralizedCharacter)
	    gch = (GeneralizedCharacter) getCharacter();
	else if (getCharacter() instanceof GCAlias)
	    gch = ((GCAlias) getCharacter()).findOriginal();
	else
	    return null;
	CharacterPrototype prototype = gch.getPrototype();
	Bindable currentType = gch.getValueType();
	Appearance originalAppearance = gch.getOriginalAppearance();
	Appearance anyAppearance = new AnythingAppearance(originalAppearance);
	int miniSize = 32;
	ListView listView
	    = PlaywritePopup.makePopupList(this, GENERALIZE_CHARACTER);
	if (!PlaywriteRoot.isPlayer() && gch == RuleEditor.getSelfGC()) {
	    ListItem item
		= PlaywritePopup.makePopupItem(prototype.getName(),
					       originalAppearance
						   .getBitmap(miniSize),
					       prototype);
	    listView.addItem(item);
	    listView.selectItem(item);
	} else {
	    ListItem item
		= PlaywritePopup.makePopupItem(prototype.getName(),
					       originalAppearance
						   .getBitmap(miniSize),
					       prototype);
	    listView.addItem(item);
	    if (currentType == prototype || currentType == null)
		listView.selectItem(item);
	    Vector jars = Jar.allContainingJars(prototype);
	    if (jars != null) {
		for (int i = 0; i < jars.size(); i++) {
		    Jar jar = (Jar) jars.elementAt(i);
		    JarAppearance jarAppearance
			= new JarAppearance(originalAppearance);
		    String jarName
			= Resource.getTextAndFormat("RE any jar popup fmt",
						    (new Object[]
						     { jar.getName() }));
		    item = (PlaywritePopup.makePopupItem
			    (jarName, jarAppearance.getBitmap(miniSize), jar));
		    listView.addItem(item);
		    if (currentType == jar)
			listView.selectItem(item);
		}
	    }
	    item = PlaywritePopup.makePopupItem(Resource.getText("RE any"),
						anyAppearance
						    .getBitmap(miniSize),
						GeneralizedCharacter.anyType);
	    listView.addItem(item);
	    if (currentType == GeneralizedCharacter.anyType)
		listView.selectItem(item);
	}
	listView.sizeToMinSize();
	listView.sizeTo(listView.minItemWidth(), listView.height());
	return listView;
    }
    
    public DragDestination allowsDragInto(Class dropModelClass) {
	if (dropModelClass == Appearance.class)
	    return this;
	if (dropModelClass == PlaywriteSound.class)
	    return this;
	if (dropModelClass == RuleListItem.class)
	    return this;
	if (dropModelClass == Variable.class)
	    return this;
	return super.allowsDragInto(dropModelClass);
    }
    
    private ToolDestination _acceptsTool(ToolSession session, int x, int y) {
	if (this.isDisabled())
	    return null;
	if (this.window() != null
	    && this.window() instanceof PlaywriteWindow) {
	    PlaywriteWindow win = (PlaywriteWindow) this.window();
	    if (win.getWorld() != null
		&& win.getWorld().getState() == World.RUNNING)
		return null;
	}
	if (isLocallyHandledTool(session.toolType()))
	    return this;
	return super.acceptsTool(session, x, y);
    }
    
    public boolean supportsTool(Tool tool) {
	if (isLocallyHandledTool(tool))
	    return true;
	return super.supportsTool(tool);
    }
    
    private boolean isLocallyHandledTool(Tool tool) {
	if (_disabledTools != null && _disabledTools.containsIdentical(tool))
	    return false;
	if (tool == Tool.newCharacterTool)
	    return true;
	if (tool == Tool.editAppearanceTool)
	    return true;
	if (tool == Tool.newRuleTool)
	    return true;
	if (tool == Tool.copyLoadTool)
	    return true;
	if (tool == Tool.deleteTool)
	    return true;
	return false;
    }
    
    public void allowTool(Tool tool, ToolDestination dest) {
	if (_disabledTools != null)
	    _disabledTools.removeElementIdentical(tool);
	if (isLocallyHandledTool(tool)) {
	    if (dest != this)
		throw new PlaywriteInternalError
			  ("Attempting to reset Character view's handler for tool "
			   + tool);
	} else
	    super.allowTool(tool, dest);
    }
    
    public ToolDestination disallowTool(Tool tool) {
	if (isLocallyHandledTool(tool)) {
	    if (_disabledTools == null)
		_disabledTools = new Vector(2);
	    _disabledTools.addElementIfAbsent(tool);
	    return this;
	}
	return super.disallowTool(tool);
    }
    
    public boolean mouseDown(MouseEvent event) {
	CocoaCharacter character = getCharacter();
	World world = character.getWorld();
	mouseIsDown = true;
	this.setDragPoint(new Point(event.x, event.y));
	if (isFlashing())
	    stopFlashing();
	if (!world.isRunning() || world.isSuspendedForDebug()) {
	    if (event.isMetaKeyDown() && event.clickCount() == 1) {
		if (this.getModelObject() instanceof GeneralizedCharacter)
		    return false;
		if (!(this.superview() instanceof ModalView))
		    Selection.hideModalView();
		Selection.unselectAll();
		EnumeratedVariableEditor.displayPopupFor((character
							      .getPrototype
							  ().appearanceVar),
							 character, this,
							 new Point(event.x,
								   event.y));
	    } else {
		if (!(this.superview() instanceof ModalView))
		    Selection.hideModalView();
		if (getCharacter() instanceof Selectable
		    && (event.isShiftKeyDown()
			|| !Selection.isSelected((Selectable) getCharacter())))
		    selectModel(event);
		if (character instanceof GeneralizedCharacter
		    || getGCForGeneralization() != null)
		    startTimer(DISPLAY_POPUP, event);
	    }
	}
	return true;
    }
    
    void selectModel(MouseEvent event) {
	CocoaCharacter ch = getCharacter();
	if (!(ch instanceof GeneralizedCharacter) && !(ch instanceof GCAlias)
	    || PlaywriteRoot.isPlayer() || RuleEditor.isRecordingOrEditing())
	    super.selectModel(event);
    }
    
    public void mouseUp(MouseEvent event) {
	CocoaCharacter character = getCharacter();
	World world = character.getWorld();
	long time = event.timeStamp();
	mouseIsDown = false;
	if (this.localBounds().contains(event.x, event.y)) {
	    if (world.isRunning() && !world.isSuspendedForDebug()) {
		if (character.getCharContainer() instanceof Stage) {
		    BoardView boardView = (BoardView) this.superview();
		    event = this.convertEventToView(boardView, event);
		    world.queue(event, boardView);
		}
	    } else if (event.isShiftKeyDown() || event.isMetaKeyDown())
		resetDoubleClicking();
	    else if (time - lastMouseUpTime < 1000L
		     && lastMouseUpCharacter == character) {
		resetDoubleClicking();
		if (timer != null)
		    timer.stop();
		performCommand(OPEN_CHARACTER_WINDOW, character);
	    } else {
		if ((character instanceof GeneralizedCharacter
		     || getGCForGeneralization() != null)
		    && timer != null)
		    timer.stop();
		setDoubleClicking(time, character);
		if (this.superview() instanceof BoardView) {
		    mousePoint
			= this.convertPointToView(null,
						  new Point(event.x, event.y));
		    BoardView boardView = (BoardView) this.superview();
		    Board board = boardView.getBoard();
		    event = this.convertEventToView(boardView, event);
		    int h = boardView.squareH(event.x);
		    int v = boardView.squareV(event.y);
		    if (board.isOnBoard(h, v)) {
			Vector square = board.getVisibleCharacters(h, v);
			if (square != null) {
			    square.addElementIfAbsent(getCharacter());
			    if (square.size() > 1) {
				Selection.resetGlobalState();
				mouseEvent = event;
				startTimer(DISPLAY_POPUP, event);
			    }
			}
		    }
		}
	    }
	}
    }
    
    public void mouseEntered(MouseEvent event) {
	if (getWorld().isRunning()) {
	    CocoaCharacter ch = getCharacter();
	    if (((Boolean)
		 Variable.systemVariable
		     (CocoaCharacter.SYS_ROLLOVER_ENABLED_VARIABLE_ID, ch)
		     .getValue(ch))
		    .booleanValue()) {
		_rolledOver = true;
		setBoundsForAppearance
		    ((Appearance)
		     Variable.systemVariable
			 (CocoaCharacter.SYS_ROLLOVER_APPEARANCE_VARIABLE_ID,
			  ch)
			 .getValue(ch));
		this.setDirty();
	    }
	}
    }
    
    public void mouseExited(MouseEvent event) {
	if (getWorld().isRunning()) {
	    CocoaCharacter ch = getCharacter();
	    if (((Boolean)
		 Variable.systemVariable
		     (CocoaCharacter.SYS_ROLLOVER_ENABLED_VARIABLE_ID, ch)
		     .getValue(ch))
		    .booleanValue())
		disableRollover();
	}
    }
    
    private void disableRollover() {
	setBoundsForAppearance(getCharacter().getCurrentAppearance());
	_rolledOver = false;
	this.setDirty();
    }
    
    public void update(Object target, Object value) {
	if ((target
	     == Variable.systemVariable((CocoaCharacter
					 .SYS_ROLLOVER_ENABLED_VARIABLE_ID),
					getCharacter()))
	    && Boolean.FALSE.equals(value))
	    PlaywriteRoot.app().performCommandLater(new Target() {
		public void performCommand(String command, Object data) {
		    CharacterView.this.disableRollover();
		}
	    }, null, null);
    }
    
    public void setBoundsForAppearance(Appearance app) {
	if (this.superview() instanceof BoardView) {
	    BoardView bv = (BoardView) this.superview();
	    CocoaCharacter ch = getCharacter();
	    Point loc = bv.pixelOrigin(ch.getH(), ch.getV(), app);
	    this.moveTo(loc.x, loc.y);
	    resizeToAppearance(app);
	}
    }
    
    public void keyDown(KeyEvent event) {
	Debug.print("debug.view", "view ", this, " got key event: " + event);
	switch (event.key) {
	default:
	    super.keyDown(event);
	    /* fall through */
	case 9:
	    /* empty */
	}
    }
    
    public int cursorForPoint(int x, int y) {
	int cursor = super.cursorForPoint(x, y);
	World world = getCharacter().getWorld();
	if (cursor == 3)
	    return 3;
	if (world.isRunning() && !world.isSuspendedForDebug())
	    return 0;
	if (getCharacter().isVisible())
	    return 12;
	return 0;
    }
    
    public void didMoveBy(int x, int y) {
	super.didMoveBy(x, y);
	CocoaCharacter ch = (CocoaCharacter) this.getModelObject();
	if (ch.getWorld().getMainCharacter() == ch
	    && this.superview() instanceof BoardView
	    && ((BoardView) this.superview()).getBoard() instanceof Stage) {
	    if (ch.getWorld().getCenterFollowMe()) {
		View boardView = this.superview();
		if (boardView.superview() instanceof ScrollView) {
		    Rect r = boardView.superview().bounds();
		    r.moveTo(this.x() + this.width() / 2 - r.width / 2,
			     this.y() + this.height() / 2 - r.height / 2);
		    this.superview().scrollRectToVisible(r);
		}
	    } else
		scrollToVisible();
	}
    }
    
    public void scrollToVisible() {
	this.scrollRectToVisible(this.localBounds());
    }
    
    private void startTimer(String command, MouseEvent event) {
	if (timer != null) {
	    timer.stop();
	    timer.setCommand(command);
	    timer.setDelay(500);
	} else
	    timer = new Timer(this, command, 500);
	mouseEvent = event;
	timer.setRepeats(false);
	timer.start();
    }
    
    private void setDoubleClicking(long time, CocoaCharacter ch) {
	lastMouseUpTime = time;
	lastMouseUpCharacter = ch;
    }
    
    private void resetDoubleClicking() {
	setDoubleClicking(0L, null);
    }
    
    public boolean dragDropped(DragSession session) {
	CocoaCharacter character = getCharacter();
	View draggee = this.viewBeingDragged(session);
	character.unhighlightForSelection();
	if (draggee instanceof Icon
	    && ((Icon) draggee).getModelObject() instanceof PlaywriteSound) {
	    PlaywriteSound sound
		= (PlaywriteSound) this.modelObjectBeingDragged(session);
	    if (sound.getWorld() != character.getWorld())
		sound = (PlaywriteSound) sound.copy(character.getWorld());
	    Variable.systemVariable
		(CocoaCharacter.SYS_SOUND_VARIABLE_ID, character)
		.modifyValue(character, sound);
	} else if (!(draggee instanceof RuleListItemView))
	    return false;
	selectModel(null);
	getWorld().setModified(true);
	return true;
    }
    
    public boolean dragEntered(DragSession session) {
	getCharacter().highlightForSelection();
	return true;
    }
    
    public void dragExited(DragSession session) {
	getCharacter().unhighlightForSelection();
    }
    
    public boolean dragMoved(DragSession session) {
	return true;
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool toolType = session.toolType();
	CocoaCharacter character = getCharacter();
	boolean result = true;
	World myWorld = getWorld();
	if (getWorld().getState() == World.RUNNING)
	    return false;
	if (isFlashing())
	    stopFlashing();
	if ((character instanceof GeneralizedCharacter
	     || character instanceof GCAlias)
	    && !RuleEditor.isRecordingOrEditing())
	    return false;
	if (toolType == Tool.newCharacterTool) {
	    if (character instanceof GeneralizedCharacter)
		return false;
	    session.convertMousePoint(this, this.superview());
	    return ((PlaywriteView) this.superview()).toolClicked(session);
	}
	if (toolType == Tool.editAppearanceTool)
	    result = character.editAppearance();
	else if (toolType == Tool.newRuleTool) {
	    if (character instanceof CharacterInstance
		&& character.getContainer() instanceof Stage) {
		if (PlaywriteRoot.hasAuthoringLimits()
		    && getWorld().evalLimitForClassReached(Rule.class)) {
		    getWorld().evalLimitDialog(Rule.class);
		    return false;
		}
		PlaywriteRoot.markBusy();
		BoardView myBoardView;
		if (this.superview() instanceof BoardView)
		    myBoardView = (BoardView) this.superview();
		else if (this.superview() instanceof SquareView)
		    myBoardView
			= ((SquareView) this.superview()).getBoardView();
		else
		    throw new PlaywriteInternalError
			      ("Can't create a rule for this character. It is not on a board.");
		RuleEditor.startRecording((CharacterInstance) character);
		RuleEditor.getRuleEditor();
		RuleEditor.setSelfStageIndex
		    (myWorld.getWorldView().getStageViewIndex(myBoardView));
		Selection.hideModalView();
		PlaywriteRoot.getMainRootView().drawDirtyViews();
		PlaywriteRoot.clearBusy();
	    } else
		return false;
	} else {
	    if (toolType == Tool.copyLoadTool) {
		Debug.print("debug.tool", "Copy tool clicked on ", character);
		if (character instanceof GeneralizedCharacter)
		    return false;
		Point temp = session.destinationMousePoint();
		temp.moveBy(this.x(), this.y());
		if (PlaywriteRoot.isAuthoring()
		    && this.superview() instanceof AfterBoardView)
		    ((AfterBoardView) this.superview()).setStartPoint(temp);
		temp = null;
		Image charCursor = getDragImage();
		session.resetSession(charCursor, Tool.copyPlaceTool,
				     character);
		if (this.superview() instanceof ModalView) {
		    session.resetTargetView();
		    this.unhilite();
		}
		return true;
	    }
	    if (toolType == Tool.deleteTool) {
		if (this.superview() instanceof ModalView)
		    this.removeFromSuperview();
		return character.doDeleteAction();
	    }
	    if (toolType == RuleEditor.variableWindowTool)
		session.setData(this.getModelObject());
	    else
		return false;
	}
	myWorld.setModified(true);
	return result;
    }
    
    public void toolDragged(ToolSession session) {
	Tool toolType = session.toolType();
	CocoaCharacter character = getCharacter();
	if (toolType == Tool.deleteTool) {
	    if (character != null) {
		if (this.superview() instanceof ModalView)
		    this.removeFromSuperview();
		character.doDeleteAction();
	    }
	}
    }
    
    public ToolDestination acceptsTool(ToolSession session, int x, int y) {
	CocoaCharacter ch = getCharacter();
	if (!PlaywriteRoot.isPlayer() && RuleEditor.isRecordingOrEditing()) {
	    if (ch instanceof GeneralizedCharacter
		&& session.toolType() == RuleEditor.variableWindowTool)
		return _acceptsTool(session, x, y);
	    if (ch instanceof GCAlias)
		return _acceptsTool(session, x, y);
	    return null;
	}
	return _acceptsTool(session, x, y);
    }
    
    public void resize() {
	if (getCharacter() != null && !getCharacter().isDeleted())
	    resizeToAppearance(getCharacter().getCurrentAppearance());
    }
    
    private void resizeToAppearance(Appearance app) {
	int squareSize = getSquareSize();
	int width = app.getWidthAtSquareSize(squareSize);
	int height = app.getHeightAtSquareSize(squareSize);
	this.sizeTo(width, height);
    }
    
    public int getSquareSize() {
	if (this.superview() instanceof BoardView)
	    return ((BoardView) this.superview()).getSquareSize();
	if (getCharacter().getContainer() instanceof Board)
	    return ((Board) getCharacter().getContainer()).getSquareSize();
	return 32;
    }
    
    void saveSize() {
	oldWidth = this.width();
	oldHeight = this.height();
    }
    
    void restoreSize() {
	if (oldWidth >= 0 && oldHeight >= 0)
	    this.sizeTo(oldWidth, oldHeight);
    }
    
    public void startFlashing() {
	_isFlashing = true;
    }
    
    public void stopFlashing() {
	_isFlashing = false;
    }
    
    public boolean isFlashing() {
	return _isFlashing;
    }
    
    public String toString() {
	String result = null;
	try {
	    result = "<CharacterView of " + getCharacter() + ">";
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
    
    public void layoutView() {
	this.layoutView(0, 0);
    }
    
    public void setWidthAndHeightVariables(int maxX, int maxY) {
	/* empty */
    }
    
    public void adjustAppearanceShape() {
	/* empty */
    }
}
