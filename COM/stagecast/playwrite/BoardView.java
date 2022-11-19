/* BoardView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.CommandEvent;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.EventFilter;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.ScrollView;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class BoardView extends PlaywriteSelectView
    implements ExtendedDragSource, DragDestination, ToolDestination,
	       Debug.Constants, ResourceIDs.BeforeBoardIDs,
	       ResourceIDs.ToolIDs, Target, Worldly
{
    private static TestResult hilitedTestSquare = null;
    private static Rect SPECIAL_ZERO_RECT = new Rect(-1, -1, 0, 0);
    private static final String REFRESH_REGION = "refresh region command";
    private static final String REFRESH_CHARACTERVIEW
	= "refresh character command";
    private static Image dontCareN;
    private static Image dontCareS;
    private static Image dontCareE;
    private static Image dontCareW;
    private static ScreenBufferManager availableBuffer = null;
    private Board _board;
    private Hashtable charMap;
    private AfterBoardView spotlight = null;
    private AfterBoardHandle spotHandle = null;
    private Point spotHome = null;
    private Target scrollTarget;
    private Rect privateDirty;
    ScreenBufferManager screenBufferManager;
    private Vector testedSquares;
    private boolean dontCareState;
    private boolean mouseTestState;
    private int _squareSize;
    private boolean _drawDontCares = true;
    private CocoaCharacter _copyCharacter = null;
    private Image _copyDragImage = null;
    private Vector _copyDragXYLocations = null;
    private Vector _copyDragHVLocations = null;
    private int _currentDragCopyIndex = 0;
    private Rect _copyDragRect = null;
    private CharacterView _viewOfTheFirstCopy = null;
    private Point _lastDropPoint = null;
    private EventFilter _refreshScreenCombiner = new EventFilter() {
	public Object filterEvents(Vector v) {
	    int size = v.size();
	    Rect result = null;
	    for (int i = size - 1; i >= 0; i--) {
		if (v.elementAt(i) instanceof CommandEvent) {
		    CommandEvent commandEvent = (CommandEvent) v.elementAt(i);
		    if (commandEvent.command() == "refresh region command"
			&& commandEvent.target() == BoardView.this) {
			if (result == null)
			    result = new Rect((Rect) commandEvent.data());
			else
			    result.unionWith((Rect) commandEvent.data());
			v.removeElementAt(i);
		    }
		}
	    }
	    return result;
	}
    };
    
    private class TestResult
    {
	Point location;
	boolean success;
	
	TestResult(int dxSelf, int dySelf, boolean result) {
	    BeforeBoard beforeBoard = BoardView.this.getBeforeBoard();
	    location = new Point(dxSelf, dySelf);
	    beforeBoard.translateDxDyToBoard(location);
	    setDirty();
	    success = result;
	}
	
	void setDirty() {
	    addDirtyRect(new Rect(pixelX(location.x), pixelY(location.y),
				  getSquareSize(), getSquareSize()));
	}
	
	boolean isAt(int x, int y) {
	    return x == location.x && y == location.y;
	}
	
	void draw(Graphics g) {
	    if (success)
		g.setColor(Color.green);
	    else
		g.setColor(Color.red);
	    int x = pixelX(location.x);
	    int y = pixelY(location.y);
	    g.drawRect(x, y, getSquareSize(), getSquareSize());
	    if (this == BoardView.hilitedTestSquare)
		g.setColor(Util.HIGHLIGHT_COLOR);
	    g.drawRect(x + 1, y + 1, getSquareSize() - 2, getSquareSize() - 2);
	}
    }
    
    private synchronized ScreenBufferManager getAvailableBuffer() {
	ScreenBufferManager buf = availableBuffer;
	availableBuffer = null;
	if (buf == null)
	    buf = new ScreenBufferManager();
	return buf;
    }
    
    private synchronized void setAvailableBuffer(ScreenBufferManager buf) {
	if (availableBuffer != null)
	    availableBuffer.flush();
	availableBuffer = buf;
    }
    
    BoardView(Board board) {
	if (dontCareN == null) {
	    dontCareN = Resource.getImage("dcsN");
	    dontCareS = Resource.getImage("dcsS");
	    dontCareE = Resource.getImage("dcsE");
	    dontCareW = Resource.getImage("dcsW");
	}
	privateDirty = new Rect(0, 0, 0, 0);
	this.setModelObject(board);
	_board = board;
	_squareSize = _board.getSquareSize();
	board.connectView(this);
	charMap = new Hashtable(40);
	this.setBorder(null);
	this.setName("Board");
	setupDragAndDrop();
	setupTools();
	Vector characters = board.getCharacters();
	for (int i = 0; i < characters.size(); i++) {
	    CocoaCharacter ch = (CocoaCharacter) characters.elementAt(i);
	    add(ch);
	}
	lockBackground();
	screenBufferManager = getAvailableBuffer();
	resize();
    }
    
    protected void setupDragAndDrop() {
	this.allowDragInto(CharacterInstance.class, this);
	this.allowDragOutOf(CharacterInstance.class, this);
	this.allowDragInto(CharacterPrototype.class, this);
	this.allowDragInto(Stage.class, this);
    }
    
    protected void setupTools() {
	if (getBoard() instanceof BeforeBoard)
	    setupBeforeBoardTools();
	else {
	    this.allowTool(Tool.newCharacterTool, this);
	    this.allowTool(Tool.copyPlaceTool, this);
	}
    }
    
    protected void setupBeforeBoardTools() {
	if (!PlaywriteRoot.isPlayer()) {
	    this.allowTool(Tool.deleteTool, this);
	    this.allowTool(RuleEditor.dontCareTool, this);
	    this.allowTool(RuleEditor.mouseClickTool, this);
	    this.allowTool(RuleEditor.examineTool, this);
	}
    }
    
    public final Board getBoard() {
	return _board;
    }
    
    private final BeforeBoard getBeforeBoard() {
	if (getBoard() instanceof BeforeBoard)
	    return (BeforeBoard) getBoard();
	if (getBoard() instanceof AfterBoard)
	    return ((AfterBoard) getBoard()).getBeforeBoard();
	return null;
    }
    
    final BoardView getSpotlight() {
	return spotlight;
    }
    
    final Target getScrollTarget() {
	return scrollTarget;
    }
    
    final void setScrollTarget(Target t) {
	scrollTarget = t;
    }
    
    final void setSquareSize(int sz) {
	int old = _squareSize;
	_squareSize = sz;
	squareSizeChanged(old);
    }
    
    final int getSquareSize() {
	return _squareSize;
    }
    
    public final World getWorld() {
	return getBoard().getWorld();
    }
    
    CharacterView getViewFor(CocoaCharacter ch) {
	return (CharacterView) charMap.get(ch);
    }
    
    protected void setupCharacterViewTools(PlaywriteView cv) {
	/* empty */
    }
    
    void add(CocoaCharacter ch) {
	PlaywriteView cView = (PlaywriteView) charMap.get(ch);
	Point origin = pixelOrigin(ch);
	if (cView == null) {
	    cView = ch.createView();
	    setupCharacterViewTools(cView);
	    charMap.put(ch, cView);
	    this.addSubview(cView);
	}
	cView.moveTo(origin.x, origin.y);
	addDirtyChar((CharacterView) cView);
    }
    
    void remove(CocoaCharacter ch) {
	CharacterView cView = (CharacterView) charMap.remove(ch);
	if (cView != null) {
	    cView.removeFromSuperview();
	    cView.discard();
	    addDirtyRect(cView.bounds);
	}
    }
    
    void relocate(CocoaCharacter ch) {
	CharacterView cView = (CharacterView) charMap.get(ch);
	if (cView != null) {
	    Point loc = pixelOrigin(ch);
	    cView.moveTo(loc.x, loc.y);
	}
    }
    
    void changeAppearance(CocoaCharacter ch) {
	CharacterView cView = (CharacterView) charMap.get(ch);
	if (cView != null) {
	    Appearance newAppear = ch.getCurrentAppearance();
	    Point newPosition = XYForHV(newAppear, ch.getH(), ch.getV());
	    int newWidth = newAppear.getWidthAtSquareSize(getSquareSize());
	    int newHeight = newAppear.getHeightAtSquareSize(getSquareSize());
	    cView.setBounds(newPosition.x, newPosition.y, newWidth, newHeight);
	}
    }
    
    void markDirty(CocoaCharacter ch) {
	PlaywriteView characterView = (PlaywriteView) charMap.get(ch);
	if (characterView != null)
	    addDirtyChar(characterView);
    }
    
    void lockBackground() {
	Bitmap image = getBoard().getBackgroundImage();
	if (image != null)
	    BitmapManager.checkOutBitmap(image);
    }
    
    void unlockBackground() {
	Bitmap image = getBoard().getBackgroundImage();
	if (image != null)
	    BitmapManager.checkInBitmap(image);
    }
    
    public View viewForMouse(int x, int y) {
	if (!this.containsPoint(x, y))
	    return null;
	if (getWorld().isLocked())
	    return null;
	View result = null;
	if (this.isDisabled()) {
	    if (spotlight != null)
		result = spotlight.viewForMouse(x - spotlight.bounds.x,
						y - spotlight.bounds.y);
	    if (result != null)
		return result;
	    if (spotHandle != null)
		result = spotHandle.viewForMouse(x - spotHandle.bounds.x,
						 y - spotHandle.bounds.y);
	} else {
	    Vector subviews = this.subviews();
	    int size = subviews.size();
	    int highestZ = -2;
	    for (int i = 0; i < size; i++) {
		if (subviews.elementAt(i) instanceof CharacterView) {
		    CharacterView cView
			= (CharacterView) subviews.elementAt(i);
		    int currentZ = cView.getZValueCache();
		    if (currentZ > highestZ) {
			View test = cView.viewForMouse(x - cView.bounds.x,
						       y - cView.bounds.y);
			if (test != null) {
			    result = test;
			    highestZ = currentZ;
			}
		    }
		}
	    }
	}
	if (result == null)
	    result = this;
	return result;
    }
    
    public boolean mouseDown(MouseEvent event) {
	World world = getWorld();
	if (world.isRunning() && !world.isSuspendedForDebug())
	    return true;
	if (world.isLocked())
	    return false;
	return super.mouseDown(event);
    }
    
    public void mouseUp(MouseEvent event) {
	World world = getWorld();
	if (world.isRunning() && !world.isSuspendedForDebug()
	    && getBoard() instanceof Stage)
	    world.queue(event, this);
    }
    
    public void didMoveBy(int x, int y) {
	setDirty(true);
    }
    
    public void discard() {
	getBoard().disconnectView(this);
	unlockBackground();
	this.setModelObject(null);
	_board = null;
	if (charMap != null)
	    charMap.clear();
	charMap = null;
	spotlight = null;
	spotHandle = null;
	scrollTarget = null;
	setAvailableBuffer(screenBufferManager);
	screenBufferManager = null;
	privateDirty = null;
	if (testedSquares != null)
	    testedSquares.removeAllElements();
	testedSquares = null;
	PlaywriteRoot.app().eventLoop().filterEvents(new EventFilter() {
	    public Object filterEvents(Vector events) {
		int i = events.size();
		while (--i >= 0) {
		    Object event = events.elementAt(i);
		    if (event instanceof CommandEvent) {
			CommandEvent commandEvent = (CommandEvent) event;
			if (commandEvent.target() == BoardView.this
			    && ((commandEvent.command()
				 == "refresh character command")
				|| (commandEvent.command()
				    == "refresh region command")))
			    events.removeElementAt(i);
		    }
		}
		return null;
	    }
	});
	super.discard();
    }
    
    public boolean prepareToDrag(Object data) {
	World world = getBoard().getWorld();
	if (world.isRunning() && !world.isSuspendedForDebug())
	    return false;
	View draggee = (View) data;
	addDirtyRect(draggee.bounds);
	((Visible) ((PlaywriteView) data).getModelObject())
	    .setVisibility(false);
	return true;
    }
    
    public void dragWasAccepted(DragSession session) {
	if (session.destinationView() instanceof PlaywriteView) {
	    PlaywriteView destView = (PlaywriteView) session.destinationView();
	    if (!(destView.getModelObject() instanceof CharacterContainer)) {
		Visible visible
		    = (Visible) this.modelObjectBeingDragged(session);
		if (visible != null) {
		    visible.setVisibility(true);
		    addDirtyRect(this.viewBeingDragged(session).bounds);
		}
	    }
	}
    }
    
    public boolean dragWasRejected(DragSession session) {
	Visible obj = (Visible) this.modelObjectBeingDragged(session);
	if (obj != null)
	    obj.setVisibility(true);
	addDirtyRect(this.viewBeingDragged(session).bounds);
	return true;
    }
    
    public View sourceView(DragSession session) {
	return this;
    }
    
    public DragDestination acceptsDrag(DragSession ds, int x, int y) {
	if (getBoard().getWorld().getState() == World.RUNNING
	    || getBoard().getWorld().isLocked())
	    return null;
	Object model = this.modelObjectBeingDragged(ds);
	if (model == null)
	    return null;
	if (this.isDisabled() && model instanceof Stage
	    && getBoard() instanceof Stage)
	    return this.allowsDragInto(model.getClass());
	return super.acceptsDrag(ds, x, y);
    }
    
    public boolean dragDropped(DragSession session) {
	World world = getWorld();
	Object dragItem = this.modelObjectBeingDragged(session);
	Selection.unselectAll();
	if (dragItem instanceof CocoaCharacter) {
	    PlaywriteView cView = this.viewBeingDragged(session);
	    Point dragPoint = new Point(cView.getDragPoint());
	    if (cView instanceof Icon) {
		Rect r = ((Icon) cView).getImageRect();
		dragPoint.moveBy(-r.x, -r.y);
	    }
	    if (!characterDropped((CocoaCharacter) dragItem, cView,
				  session.destinationMousePoint(), dragPoint))
		return false;
	} else if (dragItem instanceof MultiDragView) {
	    MultiDragView multiView = (MultiDragView) dragItem;
	    Vector droppedViews = multiView.getPWViews();
	    if (!(droppedViews.firstElement() instanceof CharacterView))
		return false;
	    CharacterView cView = (CharacterView) droppedViews.firstElement();
	    int oldSquareSize
		= ((Board) cView.getCharacter().getCharContainer())
		      .getSquareSize();
	    int newSquareSize = getBoard().getSquareSize();
	    int dragX
		= oldSquareSize * Math.round((float) multiView.getDragPoint().x
					     / (float) oldSquareSize);
	    int dragY
		= oldSquareSize * Math.round((float) multiView.getDragPoint().y
					     / (float) oldSquareSize);
	    Point dest = session.destinationMousePoint();
	    Point itemDragPt = new Point();
	    boolean succeed = true;
	    for (int i = 0; i < droppedViews.size(); i++) {
		if (i == 1)
		    getWorld().suspendClockTicks(true);
		cView = (CharacterView) droppedViews.elementAt(i);
		int newX = ((dragX - cView.bounds.x + multiView.bounds.x)
			    / oldSquareSize * newSquareSize);
		int newY = ((dragY - cView.bounds.y + multiView.bounds.y)
			    / oldSquareSize * newSquareSize);
		itemDragPt.moveTo(newX, newY);
		succeed
		    = characterDropped(cView.getCharacter(), cView,
				       new Point(dest), itemDragPt) && succeed;
	    }
	    getWorld().suspendClockTicks(false);
	    if (!succeed)
		return false;
	} else if (dragItem instanceof Stage) {
	    if (!getWorld().isOkToDrop(dragItem))
		return false;
	    if (getWorld().hasPreviousCopy((Stage) dragItem))
		return false;
	    if (getBoard() instanceof Stage) {
		Stage newStage = (Stage) dragItem;
		if (newStage.getWorld() != getWorld())
		    newStage = (Stage) newStage.copy(getWorld());
		RuleAction action;
		if (PlaywriteRoot.isAuthoring()
		    && RuleEditor.isRecordingOrEditing()) {
		    int selfIndex = RuleEditor.getSelfStageIndex();
		    boolean relative;
		    if (selfIndex == -1)
			relative = true;
		    else
			relative
			    = (world.getWorldView().getStageViewIndex(this)
			       == RuleEditor.getSelfStageIndex());
		    action = new SwitchStageAction(newStage, relative);
		} else
		    action
			= new SwitchStageAction(newStage,
						world.getWorldView()
						    .getStageViewIndex(this));
		world.doManualAction(action, getBoard());
	    } else
		return false;
	} else
	    return false;
	Selection.hideModalView();
	world.setModified(true);
	return true;
    }
    
    public boolean characterDropped(CocoaCharacter ch, PlaywriteView view,
				    Point dest, Point dragPt) {
	CharacterInstance draggedChar = null;
	if (getBoard() instanceof BeforeBoard)
	    return false;
	if (!getWorld().isOkToDrop(ch))
	    return false;
	ch.setVisibility(true);
	addDirtyRect(view.bounds);
	Point homePt = ch.getCurrentAppearance().pixelHome(this);
	Point pt = new Point(dest.x + homePt.x - dragPt.x,
			     dest.y + homePt.y - dragPt.y);
	pt.moveTo(squareH(pt.x), squareV(pt.y));
	if (PlaywriteRoot.isAuthoring())
	    RuleEditor.makeRelativeToSelf(pt);
	if (ch instanceof CharacterPrototype) {
	    GeneralizedCharacter gch
		= getWorld().doCreateAction((CharacterPrototype) ch,
					    getBoard(), pt.x, pt.y);
	    draggedChar = gch.getBinding();
	} else if (ch instanceof CharacterInstance) {
	    if (PlaywriteRoot.isAuthoring()
		&& RuleEditor.isRecordingOrEditing())
		return false;
	    draggedChar = getWorld().doMoveAction((CharacterInstance) ch,
						  getBoard(), pt.x, pt.y);
	} else
	    return false;
	if (getWorld().getState() == World.STOPPED && draggedChar != null)
	    Selection.addToSelection(draggedChar, this);
	return true;
    }
    
    public boolean dragEntered(DragSession session) {
	if (wantsDraggedObject(session))
	    return true;
	return false;
    }
    
    public void dragExited(DragSession session) {
	wantsDraggedObject(session);
    }
    
    public boolean dragMoved(DragSession session) {
	return true;
    }
    
    boolean wantsDraggedObject(DragSession session) {
	return acceptsDrag(session, 0, 0) != null;
    }
    
    void checkDragScroll(int direction, Point mouseDrag, View viewContext) {
	Target scroller = scrollTarget;
	Point parentPoint;
	Rect parentBounds;
	if (scrollTarget != null) {
	    parentPoint = viewContext.convertToView(this.superview(),
						    mouseDrag.x, mouseDrag.y);
	    parentBounds = this.superview().bounds();
	} else if (this.superview() instanceof BoardView) {
	    BoardView stageview = (BoardView) this.superview();
	    scroller = stageview.getScrollTarget();
	    parentPoint = viewContext.convertToView(stageview.superview(),
						    mouseDrag.x, mouseDrag.y);
	    parentBounds = stageview.superview().bounds();
	} else
	    return;
	if (scroller != null) {
	    switch (direction) {
	    case 1:
		if (parentPoint.x < parentBounds.x)
		    scroller.performCommand("scroll right", null);
		break;
	    case 2:
		if (parentPoint.x > parentBounds.maxX())
		    scroller.performCommand("scroll left", null);
		break;
	    case 3:
		if (parentPoint.y < parentBounds.y)
		    scroller.performCommand("scroll down", null);
		break;
	    case 4:
		if (parentPoint.y > parentBounds.maxY())
		    scroller.performCommand("scroll up", null);
		break;
	    }
	}
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool toolType = session.toolType();
	Point dest = session.destinationMousePoint();
	World world = getWorld();
	CharacterInstance newInstance = null;
	GeneralizedCharacter gch = null;
	CharacterView cView = null;
	int h = squareH(dest.x);
	int v = squareV(dest.y);
	Point pt = new Point(h, v);
	if (PlaywriteRoot.isAuthoring())
	    RuleEditor.makeRelativeToSelf(pt);
	if (toolType == Tool.newCharacterTool) {
	    Debug.print("debug.tool", "New Character tool clicked on ", this,
			" at ", dest);
	    if (getBoard() instanceof AfterBoard
		&& !RuleEditor.isRecordingOrEditing())
		return false;
	    CharacterPrototype prototype
		= world.makeNewPrototype(getSquareSize());
	    if (prototype == null)
		return true;
	    gch = new GeneralizedCharacter(prototype);
	    world.doManualAction(new CreateAction(gch, pt.x, pt.y, -1),
				 getBoard());
	    PlaywriteSound.sysSplat.play();
	    cView = getViewFor(gch.getBinding());
	    if (cView != null)
		cView.selectModel(null);
	    getWorld().getWorldView().getControlPanelView().getNewCharButton
		().setImageIndex(getWorld().getSplatImageCount());
	} else if (toolType == Tool.copyPlaceTool) {
	    if (!getWorld().isOkToCopyWithDialog(session.data()))
		return false;
	    if (!(session.data() instanceof CocoaCharacter))
		return false;
	    _copyCharacter = (CocoaCharacter) session.data();
	    if (world != _copyCharacter.getWorld()
		&& _copyCharacter instanceof GCAlias) {
		_copyCharacter = null;
		return false;
	    }
	    Debug.print("debug.tool", "Copy tool clicked on ", this, " at ",
			dest);
	    Debug.print("debug.tool", "character being copied = ",
			_copyCharacter);
	    _copyDragXYLocations = new Vector(4);
	    _copyDragHVLocations = new Vector(4);
	    dest = session.destinationMousePoint();
	    Image dragImage = session.image();
	    Point dragPoint
		= new Point(dragImage.width() / 2, dragImage.height() / 2);
	    Point dropPoint
		= new Point(dest.x - dragPoint.x, dest.y - dragPoint.y);
	    pt = XYForDropPoint(dropPoint);
	    _copyDragXYLocations.addElement(pt);
	    pt = HVForXY(_copyCharacter.getCurrentAppearance(), pt.x, pt.y);
	    _copyDragHVLocations.addElement(pt);
	    _currentDragCopyIndex = 1;
	    if (PlaywriteRoot.isAuthoring())
		RuleEditor.makeRelativeToSelf(pt);
	    CocoaCharacter ch
		= addCopy(_copyCharacter,
			  _copyCharacter.getWorld() == getWorld(), pt);
	    CharacterView cv = getViewFor(ch);
	    _viewOfTheFirstCopy = cv;
	    _copyDragRect = new Rect(cv.bounds);
	    _lastDropPoint = new Point(_copyDragRect.x, _copyDragRect.y);
	    _copyDragImage = cv.getDragImage();
	} else {
	    BeforeBoard beforeBoard = getBeforeBoard();
	    if (beforeBoard == null)
		return false;
	    Point toolLocSelfRel
		= RuleEditor.alwaysMakeRelativeToSelf(new Point(h, v));
	    if (toolType == RuleEditor.dontCareTool) {
		dontCareState = beforeBoard.getDontCare(h, v) ^ true;
		beforeBoard.setDontCare(h, v, dontCareState);
		setDirty(true);
	    } else if (toolType == Tool.deleteTool) {
		dontCareState = beforeBoard.getDontCare(h, v);
		if (dontCareState) {
		    beforeBoard.setDontCare(h, v, false);
		    setDirty(true);
		} else
		    return false;
	    } else if (toolType == RuleEditor.mouseClickTool) {
		MouseClickTest mouseTest = beforeBoard.getMouseTest(true);
		mouseTestState = mouseTest.getLocation(pt.x, pt.y) ^ true;
		mouseTest.setLocation(pt.x, pt.y, mouseTestState);
	    } else {
		if (toolType == RuleEditor.examineTool) {
		    if (hilitedTestSquare != null) {
			hilitedTestSquare.setDirty();
			hilitedTestSquare = null;
		    }
		    BindTest bindTest
			= (RuleEditor.ruleBeingDefined().getBindTestFor
			   (toolLocSelfRel.x, toolLocSelfRel.y));
		    if (bindTest == null) {
			Debug.print("debug.examine",
				    "couldn't get a bindTest for ", pt);
			return false;
		    }
		    session.setData(bindTest);
		    return true;
		}
		return false;
	    }
	}
	world.setModified(true);
	return true;
    }
    
    public boolean toolMoved(ToolSession session) {
	Tool toolType = session.toolType();
	if (!PlaywriteRoot.isPlayer() && toolType == RuleEditor.examineTool) {
	    BeforeBoard beforeBoard = getBeforeBoard();
	    if (beforeBoard == null)
		return false;
	    Point dest = session.destinationMousePoint();
	    int h = squareH(dest.x);
	    int v = squareV(dest.y);
	    if (hilitedTestSquare != null) {
		TestResult temp = hilitedTestSquare;
		hilitedTestSquare = null;
		temp.setDirty();
	    }
	    hilitedTestSquare = getTestedSquare(h, v);
	    if (hilitedTestSquare != null)
		hilitedTestSquare.setDirty();
	    return hilitedTestSquare != null;
	}
	return super.toolMoved(session);
    }
    
    public void toolDragged(ToolSession session) {
	Tool toolType = session.toolType();
	Point dest = session.destinationMousePoint();
	int h = squareH(dest.x);
	int v = squareV(dest.y);
	if (PlaywriteRoot.isAuthoring()
	    && (toolType == RuleEditor.dontCareTool
		|| toolType == RuleEditor.mouseClickTool
		|| toolType == Tool.deleteTool)) {
	    BeforeBoard beforeBoard = getBeforeBoard();
	    if (beforeBoard == null)
		return;
	    if (toolType == RuleEditor.dontCareTool) {
		beforeBoard.setDontCare(h, v, dontCareState);
		setDirty(true);
	    } else if (toolType == Tool.deleteTool) {
		beforeBoard.setDontCare(h, v, false);
		setDirty(true);
	    } else if (toolType == RuleEditor.mouseClickTool) {
		Point pt = new Point(h, v);
		RuleEditor.makeRelativeToSelf(pt);
		MouseClickTest mouseTest = beforeBoard.getMouseTest(true);
		mouseTest.setLocation(pt.x, pt.y, mouseTestState);
	    }
	} else if (toolType == Tool.copyPlaceTool) {
	    if (_copyCharacter != null) {
		Point destPoint = session.destinationMousePoint();
		Point dragPoint = new Point(_copyDragRect.width / 2,
					    _copyDragRect.height / 2);
		Point dropPoint = new Point(destPoint.x - dragPoint.x,
					    destPoint.y - dragPoint.y);
		dropPoint = XYForDropPoint(dropPoint);
		int squareSize = getSquareSize();
		int dx = dropPoint.x - _lastDropPoint.x;
		int dy = dropPoint.y - _lastDropPoint.y;
		int sqSzSquared = squareSize * squareSize;
		Vector linePoints;
		if (dx * dx <= sqSzSquared && dy * dy <= sqSzSquared) {
		    linePoints = new Vector(2);
		    linePoints.addElement(_lastDropPoint);
		} else
		    linePoints
			= bresenhamLine(_lastDropPoint.x, _lastDropPoint.y,
					dropPoint.x, dropPoint.y, squareSize);
		if (!((Point) linePoints.lastElement()).equals(dropPoint))
		    linePoints.addElement(dropPoint);
		_lastDropPoint.moveTo(dropPoint.x, dropPoint.y);
		for (int i = 1; i < linePoints.size(); i++) {
		    dropPoint = (Point) linePoints.elementAt(i);
		    if (!isOccupiedByCopy(dropPoint)) {
			_copyDragXYLocations.addElement(dropPoint);
			_copyDragRect.moveTo(dropPoint.x, dropPoint.y);
			addDirtyRect(_copyDragRect);
			dropPoint
			    = HVForXY(_copyCharacter.getCurrentAppearance(),
				      dropPoint.x, dropPoint.y);
			if (PlaywriteRoot.isAuthoring())
			    RuleEditor.makeRelativeToSelf(dropPoint);
			_copyDragHVLocations.addElement(dropPoint);
		    }
		}
	    }
	} else
	    return;
	getWorld().setModified(true);
    }
    
    private final boolean isOccupiedByCopy(Point pt) {
	int oldx = _copyDragRect.x;
	int oldy = _copyDragRect.y;
	Rect newRect
	    = new Rect(pt.x, pt.y, _copyDragRect.width, _copyDragRect.height);
	for (int i = 0; i < _copyDragXYLocations.size(); i++) {
	    Point point = (Point) _copyDragXYLocations.elementAt(i);
	    _copyDragRect.moveTo(point.x, point.y);
	    if (_copyDragRect.intersects(newRect)) {
		_copyDragRect.moveTo(oldx, oldy);
		return true;
	    }
	}
	_copyDragRect.moveTo(oldx, oldy);
	return false;
    }
    
    public void toolReleased(ToolSession session) {
	Tool toolType = session.toolType();
	if (toolType == Tool.copyPlaceTool && _copyCharacter != null) {
	    doAllCopies(true);
	    _copyCharacter = null;
	    _copyDragImage = null;
	    _copyDragXYLocations = null;
	    _copyDragHVLocations = null;
	    _copyDragRect = null;
	    _viewOfTheFirstCopy = null;
	}
    }
    
    public void toolExited(ToolSession session) {
	Tool toolType = session.toolType();
	if (toolType == Tool.copyPlaceTool && _copyDragXYLocations != null
	    && !_copyDragXYLocations.isEmpty())
	    doAllCopies(false);
    }
    
    public void doAllCopies(boolean endOfToolSession) {
	Point pt = null;
	World world = getWorld();
	CocoaCharacter ch = null;
	PlaywriteRoot.markBusy();
	for (int i = _currentDragCopyIndex; i < _copyDragHVLocations.size();
	     i++) {
	    pt = (Point) _copyDragHVLocations.elementAt(i);
	    ch = addCopy(_copyCharacter, world == _copyCharacter.getWorld(),
			 pt);
	}
	if (endOfToolSession) {
	    if (world.getState() == World.STOPPED
		&& _copyDragHVLocations.size() == 1
		&& _viewOfTheFirstCopy != null)
		_viewOfTheFirstCopy.selectModel(null);
	    _copyDragXYLocations.removeAllElements();
	    _copyDragHVLocations.removeAllElements();
	    _currentDragCopyIndex = 0;
	} else
	    _currentDragCopyIndex = _copyDragXYLocations.size();
	PlaywriteRoot.clearBusy();
    }
    
    private CocoaCharacter addCopy(CocoaCharacter original, boolean sameWorld,
				   Point HVLocation) {
	CocoaCharacter result = null;
	GeneralizedCharacter gch = null;
	if (original instanceof CharacterPrototype) {
	    gch = getWorld().doCreateAction(((CharacterPrototype)
					     _copyCharacter),
					    getBoard(), HVLocation.x,
					    HVLocation.y);
	    if (RuleEditor.isRuleEditing())
		result = gch.getAfterBoardCharacter();
	    else
		result = gch.getBinding();
	} else if (sameWorld) {
	    GeneralizedCharacter sourceGC = null;
	    if (original instanceof GCAlias)
		sourceGC = ((GCAlias) original).findOriginal();
	    else if (original instanceof CharacterInstance)
		sourceGC
		    = new GeneralizedCharacter((CharacterInstance) original);
	    else
		throw new PlaywriteInternalError
			  ("Illegal character being copied: " + original);
	    gch = new GeneralizedCharacter(sourceGC.getPrototype());
	    getWorld().doManualAction(new CopyAction(sourceGC, gch,
						     HVLocation.x,
						     HVLocation.y, -1),
				      getBoard());
	    if (original instanceof GCAlias)
		result = gch.getAfterBoardCharacter();
	    else
		result = gch.getBinding();
	} else if (original instanceof CharacterInstance) {
	    result = ((CocoaCharacter)
		      ((CharacterInstance) original).copy(getWorld()));
	    getBoard().add(result, HVLocation.x, HVLocation.y, -1);
	} else
	    throw new PlaywriteInternalError("Illegal character being copied: "
					     + original);
	return result;
    }
    
    MouseClickTest getMouseTest() {
	Rule r = RuleEditor.ruleBeingDefined();
	MouseClickTest mtest = null;
	Enumeration tests = r.getTests();
	while (tests.hasMoreElements()) {
	    Object test = tests.nextElement();
	    if (test instanceof MouseClickTest) {
		mtest = (MouseClickTest) test;
		break;
	    }
	}
	if (mtest == null) {
	    mtest = new MouseClickTest(getBeforeBoard());
	    r.addTest(mtest);
	}
	return mtest;
    }
    
    int squareH(int x) {
	int result = x / getSquareSize();
	if (x >= 0)
	    result++;
	return result;
    }
    
    int squareV(int y) {
	int result = getBoard().numberOfRows() - y / getSquareSize();
	if (y < 0)
	    result++;
	return result;
    }
    
    int roundedSquareH(int x) {
	return (x + (getSquareSize() >> 1)) / getSquareSize() + 1;
    }
    
    int roundedSquareV(int y) {
	return (getBoard().numberOfRows()
		- (y + (getSquareSize() >> 1)) / getSquareSize());
    }
    
    int pixelX(int h) {
	return getSquareSize() * (h - 1);
    }
    
    int pixelY(int v) {
	return getSquareSize() * (getBoard().numberOfRows() - v);
    }
    
    public Point pixelOrigin(CocoaCharacter ch) {
	return pixelOrigin(ch.getH(), ch.getV(), ch.getCurrentAppearance());
    }
    
    public Point pixelOrigin(int h, int v, Appearance app) {
	return new Point(pixelX(app.left(h)), pixelY(app.top(v)));
    }
    
    public Point pixelCenter(CocoaCharacter ch) {
	Appearance app = ch.getCurrentAppearance();
	return new Point((pixelX(app.left(ch.getH()))
			  + app.getPhysicalWidth() / 2),
			 (pixelY(app.top(ch.getV()))
			  + app.getPhysicalHeight() / 2));
    }
    
    public Point XYForDropPoint(Point dropPoint) {
	double doubleLeft = (double) dropPoint.x;
	double doubleTop = (double) dropPoint.y;
	Point p = new Point();
	int sqSz = getSquareSize();
	p.x = (int) Math.round(doubleLeft / (double) sqSz) * sqSz;
	p.y = (int) Math.round(doubleTop / (double) sqSz) * sqSz;
	return p;
    }
    
    public Point HVForXY(Appearance appearance, int x, int y) {
	Point home = appearance.getHomeSquare();
	int h = x / getSquareSize() + home.x;
	int v = (getBoard().numberOfRows() - y / getSquareSize()
		 - appearance.getLogicalHeight() + home.y);
	return new Point(h, v);
    }
    
    public Point XYForHV(Appearance appearance, int h, int v) {
	Point home = appearance.getHomeSquare();
	int x = (h - home.x) * getSquareSize();
	int y = ((getBoard().numberOfRows()
		  - (v + (appearance.getLogicalHeight() - home.y)))
		 * getSquareSize());
	return new Point(x, y);
    }
    
    void resize() {
	Rect oldBounds = this.bounds();
	int newWidth = getBoard().numberOfColumns() * getSquareSize();
	int newHeight = getBoard().numberOfRows() * getSquareSize();
	this.setMinSize(newWidth, newHeight);
	this.sizeToMinSize();
	if (spotlight != null) {
	    int dy = oldBounds.height - newHeight;
	    moveSpotlight(0, -dy);
	    ((AfterBoard) spotlight.getBoard()).deleteCharactersOutOfBounds();
	}
	setDirty(true);
    }
    
    public synchronized void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	int minWidth;
	int minHeight;
	if (this.superview() != null) {
	    minWidth = Math.min(this.width(), this.superview().width());
	    minHeight = Math.min(this.height(), this.superview().height());
	} else {
	    minWidth = this.width();
	    minHeight = this.height();
	}
	screenBufferManager.sizeTo(minWidth, minHeight);
    }
    
    void squareSizeChanged(int old) {
	for (int i = 0; i < this.subviews().size(); i++) {
	    Object view = this.subviews().elementAt(i);
	    if (view instanceof CharacterView) {
		CharacterView cView = (CharacterView) view;
		if (cView.getCharacter() != null)
		    cView.resize();
	    }
	}
	repositionCharacterViews();
	resize();
	if (spotlight != null) {
	    spotlight.setSquareSize(getSquareSize());
	    spotlight.moveTo(pixelX(spotHome.x), pixelY(spotHome.y));
	    spotHandle.resize();
	}
    }
    
    void repositionCharacterViews() {
	ASSERT.isInEventThread();
	ASSERT.isTrue(getWorld().isInSyncPhase());
	Vector characters = getBoard().getCharacters();
	for (int i = 0; i < characters.size(); i++) {
	    CocoaCharacter ch = (CocoaCharacter) characters.elementAt(i);
	    if (charMap.get(ch) != null) {
		remove(ch);
		add(ch);
	    }
	}
    }
    
    void addDirtyChar(PlaywriteView cView) {
	addDirtyRect(cView.bounds);
    }
    
    void disable() {
	if (!this.isDisabled()) {
	    if (RuleEditor.isRuleEditing())
		super.disable();
	    else {
		CharacterInstance ch = RuleEditor.getSelf();
		if (RuleEditor.getSelfContainer() == getBoard()) {
		    getBoard().invalidateScreen(true);
		    showSpotlight(ch);
		}
		super.disable();
		addDirtyRect(null);
	    }
	}
    }
    
    void enable() {
	if (this.isDisabled()) {
	    getBoard().invalidateScreen(true);
	    super.enable();
	    hideSpotlight();
	    addDirtyRect(null);
	}
    }
    
    void showSpotlight(CharacterInstance ch) {
	if (spotlight == null && ch.getContainer() == getBoard()) {
	    spotlight = RuleEditor.ruleBeingDefined().createAfterBoardView();
	    spotlight.setSquareSize(getSquareSize());
	    spotHome = spotlight.positionSpotlight(ch, this);
	    spotHandle = new AfterBoardHandle(spotlight);
	    this.addSubview(spotHandle);
	    this.addSubview(spotlight);
	    spotHandle.resize();
	    addDirtyRect(spotHandle.bounds);
	}
    }
    
    void hideSpotlight() {
	if (spotlight != null) {
	    spotlight.removeFromSuperview();
	    spotHandle.removeFromSuperview();
	    setDirty(true);
	    spotlight.discard();
	    spotHandle.discard();
	    spotlight = null;
	    spotHandle = null;
	}
    }
    
    void moveSpotlight(int dx, int dy) {
	if (spotlight != null) {
	    addDirtyRect(spotHandle.bounds);
	    spotlight.moveBy(dx, dy);
	    spotHandle.resize();
	    addDirtyRect(spotHandle.bounds);
	}
    }
    
    public void subviewDidResize(View whichOne) {
	if (whichOne == spotlight)
	    spotHandle.resize();
    }
    
    protected void ancestorWasAddedToViewHierarchy(View view) {
	if (view instanceof PWRootView)
	    ((PWRootView) this.rootView()).addTarget(this);
	super.ancestorWasAddedToViewHierarchy(view);
    }
    
    protected void ancestorWillRemoveFromViewHierarchy(View view) {
	if (view instanceof PWRootView)
	    ((PWRootView) this.rootView()).removeTarget(this);
	super.ancestorWillRemoveFromViewHierarchy(view);
    }
    
    public void addDirtyRect(Rect rect) {
	ASSERT.isInEventThread();
	if (rect == null)
	    privateDirty.setBounds(0, 0, this.width(), this.height());
	else if (privateDirty.width == 0)
	    privateDirty.setBounds(rect);
	else
	    privateDirty.unionWith(rect);
	super.addDirtyRect(rect);
    }
    
    public void setDirty(boolean dirty) {
	ASSERT.isInEventThread();
	if (dirty && privateDirty != null)
	    privateDirty.setBounds(0, 0, this.width(), this.height());
	super.setDirty(dirty);
    }
    
    public boolean isTransparent() {
	return false;
    }
    
    void drawToBuffer(boolean nested) {
	drawToBuffer(privateDirty, nested, null);
	privateDirty.setBounds(null);
    }
    
    private void drawToBuffer(Rect clipRect, boolean nested,
			      CharacterView thisViewOnly) {
	ASSERT.isInEventThread();
	Graphics g = null;
	if (clipRect.width != 0) {
	    g = screenBufferManager.createGraphics();
	    if (this.superview() != null
		&& this.superview() instanceof ScrollView)
		g.translate(this.bounds().x, this.bounds().y);
	    clipRect.intersectWith(0, 0, this.width(), this.height());
	    g.setClipRect(clipRect);
	    if (thisViewOnly == null)
		drawViewBackground(g);
	    if (thisViewOnly == null)
		threadSafeDrawCharacters(g);
	    else
		thisViewOnly.draw(thisViewOnly.getCharacter(), g,
				  thisViewOnly.bounds.x, thisViewOnly.bounds.y,
				  getSquareSize());
	    if (_copyDragImage != null)
		drawCopyDragImages(g);
	    if (getBoard().isDontCareVisible())
		drawDontCare(g);
	    if (this.isDisabled())
		this.getGrayLayer().drawView(g);
	    if (spotlight != null)
		drawSpotlight(g);
	    if (testedSquares != null)
		drawTestedSquares(g);
	    g.dispose();
	    if (!nested)
		addDirtyRect(clipRect);
	}
    }
    
    public void convertRectToView(View otherView, Rect sourceRect,
				  Rect destRect) {
	if (getWorld().getState() == World.RUNNING
	    && otherView instanceof CharacterView)
	    destRect = SPECIAL_ZERO_RECT;
	else
	    super.convertRectToView(otherView, sourceRect, destRect);
    }
    
    public synchronized void updateDrawingBuffer() {
	if (screenBufferManager.adjustDrawingBuffer()) {
	    privateDirty.setBounds(0, 0, this.width(), this.height());
	    drawToBuffer(true);
	}
    }
    
    public synchronized void drawView(Graphics gr) {
	if (screenBufferManager.adjustDrawingBuffer()) {
	    privateDirty.setBounds(0, 0, this.width(), this.height());
	    drawToBuffer(true);
	} else if (getWorld().getState() != World.RUNNING
		   || !(getBoard() instanceof Stage))
	    drawToBuffer(true);
	if (this.superview() != null && this.superview() instanceof ScrollView)
	    screenBufferManager.drawAt(gr, Math.abs(bounds.x),
				       Math.abs(bounds.y));
	else
	    screenBufferManager.drawAt(gr, 0, 0);
	if (this.getSelecting()) {
	    gr.translate(this.getSelectView().bounds.x,
			 this.getSelectView().bounds.y);
	    this.getSelectView().drawView(gr);
	    gr.translate(-this.getSelectView().bounds.x,
			 -this.getSelectView().bounds.y);
	}
    }
    
    public void refreshRegion(Rect region) {
	PlaywriteRoot.app().performCommandLater(this, "refresh region command",
						new Rect(region));
    }
    
    public void refreshCharacterView(CharacterView view) {
	PlaywriteRoot.app()
	    .performCommandLater(this, "refresh character command", view);
    }
    
    public void drawSubviews(Graphics g) {
	/* empty */
    }
    
    public void drawViewBackground(Graphics g) {
	Bitmap image = getBoard().getBackgroundImage();
	Rect clipRect = g.clipRect();
	g.setColor(getBoard().getBackgroundColor());
	g.fillRect(0, 0, this.width(), this.height());
	if (image != null) {
	    int viewHeight = this.height();
	    int viewWidth = this.width();
	    int imgWidth = image.width();
	    int imgHeight = image.height();
	    int align = getBoard().getBackgroundAlignment();
	    int imgX = 0;
	    int imgY = 0;
	    if (align == 1)
		image.drawScaled(g, 0, 0, viewWidth, viewHeight);
	    else if (align == 0)
		image.drawAt(g, (viewWidth - imgWidth) / 2,
			     (viewHeight - imgHeight) / 2);
	    else {
		for (/**/; imgY < viewHeight; imgY += imgHeight) {
		    for (/**/; imgX < viewWidth; imgX += imgWidth) {
			if (clipRect.intersects(imgX, imgY, imgWidth,
						imgHeight))
			    image.drawAt(g, imgX, imgY);
		    }
		    imgX = 0;
		}
	    }
	}
	if (getBoard().wantsGrid())
	    drawGrid(g);
    }
    
    private void drawGrid(Graphics g) {
	int nRows = getBoard().numberOfRows();
	int nColumns = getBoard().numberOfColumns();
	int width = this.width();
	int height = this.height();
	g.setColor(getBoard().getGridColor());
	for (int h = 1; h < nColumns; h++)
	    g.drawLine(h * getSquareSize() - 1, 0, h * getSquareSize() - 1,
		       height);
	for (int v = 1; v < nRows; v++)
	    g.drawLine(0, v * getSquareSize() - 1, width,
		       v * getSquareSize() - 1);
    }
    
    void setDrawDontCares(boolean drawEm) {
	_drawDontCares = drawEm;
    }
    
    private void drawDontCare(Graphics g) {
	BeforeBoard beforeBoard = getBeforeBoard();
	if (beforeBoard != null && beforeBoard.hasDontCare()
	    && _drawDontCares) {
	    int squareSizeInset = getSquareSize() - 2;
	    int width = beforeBoard.numberOfColumns();
	    int height = beforeBoard.numberOfRows();
	    for (int h = 1; h <= width; h++) {
		for (int v = 1; v <= height; v++) {
		    if (beforeBoard.getDontCare(h, v)) {
			if (v == height
			    || beforeBoard.getDontCare(h, v + 1) == false)
			    dontCareN.drawAt(g, pixelX(h), pixelY(v));
			if (v == 1
			    || beforeBoard.getDontCare(h, v - 1) == false)
			    dontCareS.drawAt(g, pixelX(h), pixelY(v));
			if (h == width
			    || beforeBoard.getDontCare(h + 1, v) == false)
			    dontCareE.drawAt(g, pixelX(h), pixelY(v));
			if (h == 1
			    || beforeBoard.getDontCare(h - 1, v) == false)
			    dontCareW.drawAt(g, pixelX(h), pixelY(v));
		    }
		}
	    }
	}
    }
    
    private void drawMouseTests(Graphics g) {
	BeforeBoard theBoard = (BeforeBoard) getBoard();
	MouseClickTest mtest = theBoard.getMouseTest(false);
	if (mtest != null) {
	    for (int h = 1; h <= mtest.getShape().getWidth(); h++) {
		for (int v = 1; v <= mtest.getShape().getHeight(); v++) {
		    if (theBoard.isMouseClickTestSquare(h, v))
			Resource.getImage("MouseClickIndi").drawAt
			    (g, (h - 1) * getSquareSize(),
			     (theBoard.numberOfRows() - v) * getSquareSize());
		}
	    }
	}
    }
    
    private final void threadSafeDrawCharacters(Graphics g) {
	if (getWorld().isInSyncPhase())
	    drawCharacters(g);
	else {
	    synchronized (getBoard()) {
		drawCharacters(g);
	    }
	}
    }
    
    private void drawCharacters(Graphics g) {
	int squareSize = getSquareSize();
	Vector characters = getBoard().getCharacters();
	Rect clipRect = g.clipRect();
	int size = characters.size();
	for (int i = 0; i < size; i++) {
	    CocoaCharacter ch = (CocoaCharacter) characters.elementAt(i);
	    CharacterView chView = (CharacterView) charMap.get(ch);
	    if (chView != null) {
		chView.setZValueCache(i);
		Rect rect = chView.bounds;
		if (clipRect.intersects(rect.x, rect.y, rect.width,
					rect.height))
		    chView.draw(ch, g, rect.x, rect.y, squareSize);
	    }
	}
    }
    
    private void drawSpotlight(Graphics g) {
	g.pushState();
	g.setClipRect(spotlight.bounds);
	g.translate(spotlight.x(), spotlight.y());
	spotlight.drawView(g);
	g.popState();
	g.pushState();
	g.setClipRect(spotHandle.bounds);
	g.translate(spotHandle.x(), spotHandle.y());
	spotHandle.drawView(g);
	g.popState();
    }
    
    private void drawCopyDragImages(Graphics g) {
	for (int i = _currentDragCopyIndex; i < _copyDragXYLocations.size();
	     i++) {
	    Point p = (Point) _copyDragXYLocations.elementAt(i);
	    _copyDragImage.drawAt(g, p.x, p.y);
	}
    }
    
    private Vector bresenhamLine(int x1, int y1, int x2, int y2,
				 int gridSize) {
	Vector result = new Vector(4);
	int x = x1;
	int y = y1;
	int deltaX = x2 - x1;
	int deltaY = y2 - y1;
	int xChange;
	if (deltaX < 0) {
	    xChange = -gridSize;
	    deltaX = -deltaX;
	} else
	    xChange = gridSize;
	int yChange;
	if (deltaY < 0) {
	    yChange = -gridSize;
	    deltaY = -deltaY;
	} else
	    yChange = gridSize;
	int error = 0;
	if (deltaX < deltaY) {
	    for (int i = 0; i < deltaY + gridSize; i += gridSize) {
		result.addElement(new Point(x, y));
		y += yChange;
		error += deltaX;
		if (error > deltaY) {
		    x += xChange;
		    error -= deltaY;
		}
	    }
	} else {
	    for (int i = 0; i < deltaX + gridSize; i += gridSize) {
		result.addElement(new Point(x, y));
		x += xChange;
		error += deltaY;
		if (error > deltaX) {
		    y += yChange;
		    error -= deltaX;
		}
	    }
	}
	return result;
    }
    
    void addTestedSquare(int dxSelf, int dySelf, boolean result) {
	if (testedSquares == null)
	    testedSquares = new Vector(5);
	testedSquares.addElement(new TestResult(dxSelf, dySelf, result));
    }
    
    void resetTestedSquares() {
	hilitedTestSquare = null;
	if (testedSquares != null && testedSquares.size() != 0) {
	    Enumeration testSquares = testedSquares.elements();
	    while (testSquares.hasMoreElements()) {
		TestResult result = (TestResult) testSquares.nextElement();
		result.setDirty();
	    }
	    testedSquares.removeAllElements();
	}
    }
    
    void drawTestedSquares(Graphics g) {
	int size = testedSquares.size();
	for (int i = 0; i < size; i++)
	    ((TestResult) testedSquares.elementAt(i)).draw(g);
    }
    
    TestResult getTestedSquare(int x, int y) {
	if (testedSquares == null)
	    return null;
	int size = testedSquares.size();
	for (int i = 0; i < size; i++) {
	    TestResult testResult = (TestResult) testedSquares.elementAt(i);
	    if (testResult.isAt(x, y))
		return testResult;
	}
	return null;
    }
    
    public void performCommand(String command, Object data) {
	if (command == "refresh region command") {
	    ASSERT.isInEventThread();
	    Rect r = (Rect) data;
	    r.unionWith((Rect) PlaywriteRoot.app().eventLoop()
				   .filterEvents(_refreshScreenCombiner));
	    drawToBuffer(r, false, null);
	} else if (command == "refresh character command") {
	    ASSERT.isInEventThread();
	    drawToBuffer(((CharacterView) data).bounds(), false,
			 (CharacterView) data);
	} else if (command == PWRootView.COMMAND_DIRTYVIEW) {
	    if (!(data instanceof PlaywriteView)) {
		View view = (View) data;
		Rect rect = view.dirtyRect();
		for (/**/; view != null; view = view.superview()) {
		    if (view == this) {
			view.addDirtyRect(rect);
			break;
		    }
		    rect.x += view.bounds.x;
		    rect.y += view.bounds.y;
		}
	    }
	} else if (command != PWRootView.COMMAND_CLEANVIEW)
	    super.performCommand(command, data);
    }
}
