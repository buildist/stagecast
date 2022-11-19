/* WorldView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.Date;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.Menu;
import COM.stagecast.ifc.netscape.application.MenuView;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Range;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.TextViewOwner;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.application.Window;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class WorldView extends PlaywriteView
    implements ResourceIDs.AboutWindowIDs, ResourceIDs.CommandIDs,
	       ResourceIDs.WorldViewIDs, TextViewOwner, Worldly
{
    private static final int MENUBUTTON_X = 30;
    private World _world;
    private SplitView _multiStageView;
    private SidelineView _sidelineView;
    private ScrollableArea _sidelineScroller;
    private ControlPanelView _controlPanelView;
    private ScrollableArea _controlScroller;
    private WorldWindow _worldWindow;
    private PlaywriteWindow _aboutWindow = null;
    private PlaywriteView _sidelineButtonContainer;
    private BoardView[] _boardViews = new BoardView[2];
    private PlaywriteButton systemMenuButton;
    private PlaywriteButton showSidelineButton;
    private PlaywriteButton showControlPanelButton;
    private MenuView systemMenuView;
    private Label authorLabel;
    private Label commentLabel;
    private Label creditLabel;
    private PlaywriteTextView authorField;
    private PlaywriteTextView commentField;
    private static int ID = 1;
    
    private class TextFieldContainer extends PlaywriteView
    {
	private TextView _trackView;
	private Label _label;
	private boolean _sizing = false;
	private int id = WorldView.ID;
	
	TextFieldContainer() {
	    WorldView.ID++;
	}
	
	public void setTrackView(TextView trackView) {
	    _trackView = trackView;
	}
	
	public void setLabel(Label label) {
	    _label = label;
	}
	
	private int tallestSubview() {
	    int h = 0;
	    int t = 0;
	    int i = this.subviews().size();
	    while (i-- > 0) {
		t = ((View) this.subviews().elementAt(i)).bounds.maxY();
		if (t > h)
		    h = t;
	    }
	    return h;
	}
	
	public Size minSize() {
	    if (this.superview() != null) {
		int width = this.window().width() - 10;
		if (_trackView != null) {
		    width -= _trackView.x();
		    _trackView.sizeTo(width, _trackView.height());
		    _trackView.sizeToMinSize();
		}
		_label.sizeToMinSize();
		int x = width - _label.width();
		if (x != 0)
		    x /= 2;
		_label.moveTo(x, _label.y());
	    }
	    Size size = super.minSize();
	    return size;
	}
	
	public void subviewDidResize(View view) {
	    if (view == _trackView) {
		this.sizeTo(this.width(), tallestSubview());
		if (this.superview() != null)
		    this.superview().sizeToMinSize();
	    }
	    super.subviewDidResize(view);
	}
    }
    
    private class SystemMenuView extends MenuView implements StateWatcher
    {
	PlaywriteButton _button;
	
	public SystemMenuView(Menu menu, PlaywriteButton button) {
	    super(menu);
	    _button = button;
	    this.setType(1);
	    this.sizeToMinSize();
	    _world.addStateWatcher(this);
	}
	
	public void stateChanged(Object target, Object oldState,
				 Object transition, Object newState) {
	    if (transition == World.CLOSE_WORLD && this.isVisible())
		hide();
	}
	
	public void hide() {
	    super.hide();
	    Target target = new Target() {
		public void performCommand(String command, Object data) {
		    _button.setState(false);
		}
	    };
	    Application.application().performCommandLater(target, null, null);
	}
	
	public void mouseUp(MouseEvent event) {
	    COM.stagecast.ifc.netscape.application.MenuItem clickedItem = null;
	    clickedItem = this.itemForPoint(event.x, event.y);
	    if (this.owner() != null || clickedItem != null || child != null)
		super.mouseUp(event);
	}
    }
    
    private class StageSpace extends PlaywriteView implements DragDestination
    {
	World startWorld;
	int stageIndex;
	
	StageSpace(World w, int ix) {
	    super(0, 0, 40, 40);
	    startWorld = w;
	    stageIndex = ix;
	}
	
	public void drawSubviews(Graphics g) {
	    super.drawSubviews(g);
	    g.setFont(Util.buttonFont);
	    g.setColor(Color.black);
	    g.drawString(Resource.getText("WW dm"), 5, 20);
	}
	
	public boolean dragEntered(DragSession session) {
	    return true;
	}
	
	public void dragExited(DragSession session) {
	    /* empty */
	}
	
	public boolean dragMoved(DragSession session) {
	    return true;
	}
	
	public boolean dragDropped(DragSession session) {
	    Object dragItem = this.modelObjectBeingDragged(session);
	    Selection.unselectAll();
	    if (dragItem instanceof Stage) {
		if (!getWorld().isOkToDrop(dragItem))
		    return false;
		if (getWorld().hasPreviousCopy((Stage) dragItem))
		    return false;
		Stage newStage = (Stage) dragItem;
		if (newStage.getWorld() != startWorld)
		    newStage = (Stage) newStage.copy(startWorld);
		RuleAction action;
		if (PlaywriteRoot.isAuthoring()
		    && RuleEditor.isRecordingOrEditing())
		    action = new SwitchStageAction(newStage, false);
		else
		    action = new SwitchStageAction(newStage, stageIndex);
		_world.doManualAction(action, null);
		return true;
	    }
	    return false;
	}
    }
    
    WorldView(WorldWindow worldWindow, World world) {
	super(0, 0, 0, 0);
	_world = world;
	_worldWindow = worldWindow;
	this.setBackgroundColor(_world.getColor());
	_worldWindow.getTitleBar()
	    .addSubviewRight(new PlaywriteView(Resource.getImage("WW icon")));
	systemMenuButton = initializeSystemMenuView();
	_worldWindow.getTitleBar().addSubviewLeft(systemMenuButton);
	this.sizeTo(_worldWindow.contentView().width(),
		    _worldWindow.contentView().height());
	_controlPanelView = new ControlPanelView(_world);
	_controlPanelView.sizeToMinSize();
	_controlScroller = new ScrollableArea(this.width(),
					      _controlPanelView.height(),
					      _controlPanelView, true, false) {
	    public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		this.getContentView().sizeToMinSize();
	    }
	};
	_controlScroller.setBackgroundColor(_world.getColor());
	_controlScroller.setAllowSmallContentView(false);
	_controlScroller.setBounds(0,
				   this.height() - _controlPanelView.height(),
				   (this.width()
				    - PlaywriteBorder.RIGHT_BORDER.width()),
				   _controlPanelView.height());
	_controlScroller.setVertResizeInstruction(8);
	_controlScroller.setHorizResizeInstruction(2);
	this.addSubview(_controlScroller);
	_sidelineView = new SidelineView(this);
	_sidelineView.sizeToMinSize();
	_sidelineScroller = new ScrollableArea(SidelineView.DEFAULT_WIDTH,
					       this.height(), _sidelineView,
					       false, true) {
	    public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		this.getContentView().sizeToMinSize();
	    }
	};
	_sidelineScroller.setBackgroundColor(_world.getColor());
	_sidelineScroller.setAllowSmallContentView(false);
	_sidelineScroller.setVertResizeInstruction(16);
	_sidelineScroller.setHorizResizeInstruction(1);
	addDrawerHandlesToWindow(_worldWindow);
	Rect displayRect = new Rect(this.bounds());
	displayRect.height = displayRect.height - _controlScroller.height();
	displayRect.width -= showSidelineButton.width();
	_multiStageView = new SplitView(displayRect, 2);
	_multiStageView.setBackgroundColor(_world.getColor());
	_multiStageView.setVertResizeInstruction(16);
	_multiStageView.setHorizResizeInstruction(2);
	this.addSubview(_multiStageView);
	_worldWindow.addSubview(this);
	_worldWindow.setLayer(-1);
	changeWindowColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
    }
    
    void changeWindowColor(Color color) {
	_sidelineButtonContainer.setBackgroundColor(color);
	_controlScroller.changeWindowColor(color, color);
	_sidelineScroller.changeWindowColor(color, color);
	_multiStageView.changeWindowColor(color);
    }
    
    public final World getWorld() {
	return _world;
    }
    
    final ControlPanelView getControlPanelView() {
	return _controlPanelView;
    }
    
    final ScrollableArea getControlPanelScroller() {
	return _controlScroller;
    }
    
    public final SplitView getMultiStageView() {
	return _multiStageView;
    }
    
    public final double[] getSplitReal() {
	return _multiStageView.getSplitReal();
    }
    
    public final void setSplitReal(double[] splits) {
	_multiStageView.setSplitReal(splits);
    }
    
    public Size getWindowSizeForStageSize(int width, int height) {
	int leftMargin = _worldWindow.border().leftMargin();
	int topMargin = _worldWindow.border().topMargin();
	int rightMargin = (_worldWindow.border().rightMargin()
			   + _sidelineButtonContainer.width());
	if (sidelineIsVisible())
	    rightMargin += _sidelineScroller.width();
	int bottomMargin = _worldWindow.border().bottomMargin();
	if (controlPanelIsVisible())
	    bottomMargin += _controlScroller.height();
	topMargin += ScrollableArea.SCROLL_ARROW_WIDTH * 2;
	leftMargin += ScrollableArea.SCROLL_ARROW_WIDTH * 2;
	return new Size(width + leftMargin + rightMargin,
			height + topMargin + bottomMargin);
    }
    
    public Size getStageSize() {
	return new Size((_multiStageView.width()
			 - ScrollableArea.SCROLL_ARROW_WIDTH * 2),
			(_multiStageView.height()
			 - ScrollableArea.SCROLL_ARROW_WIDTH * 2));
    }
    
    public final void hideSystemMenu() {
	if (systemMenuButton.superview() != null)
	    systemMenuButton.removeFromSuperview();
	Menu menu = _world.getSystemMenu();
	for (int i = 0; i < menu.itemCount(); i++)
	    menu.itemAt(i).setEnabled(false);
    }
    
    public boolean isTransparent() {
	return false;
    }
    
    public void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	if (_controlPanelView != null)
	    _controlPanelView.sizeToMinSize();
	if (_sidelineView != null)
	    _sidelineView.sizeToMinSize();
	this.setDirty(true);
    }
    
    public void discard() {
	super.discard();
	if (_sidelineScroller.superview() == null)
	    _sidelineScroller.discard();
	if (_controlScroller.superview() == null)
	    _controlScroller.discard();
	_world = null;
	_multiStageView = null;
	_sidelineView = null;
	_sidelineScroller = null;
	_controlPanelView = null;
	_controlScroller = null;
	_worldWindow = null;
    }
    
    void addVisibleStageView(Stage stage) {
	addVisibleStageView(stage, _multiStageView.size());
    }
    
    private void addVisibleStageView(Stage stage, int index) {
	if (stage == null) {
	    StageSpace bogusStage = new StageSpace(getWorld(), index);
	    bogusStage.setBackgroundColor(_world.getLightColor());
	    bogusStage.allowDragInto(Stage.class, bogusStage);
	    _multiStageView.setViewAt(index, bogusStage);
	    _boardViews[index] = null;
	} else {
	    ScrollableArea scroller
		= createScrollerForStage(stage, new Rect());
	    _multiStageView.setViewAt(index, scroller);
	    _boardViews[index] = getBVfromScroller(scroller);
	    if (!PlaywriteRoot.isPlayer() && RuleEditor.isRecordingOrEditing())
		_boardViews[index].disable();
	    if (index == 0)
		((PlaywriteWindow) this.window()).setMainScroller(scroller);
	    _boardViews[index].updateDrawingBuffer();
	}
    }
    
    void setVisibleStageView(Stage stage, int index) {
	PlaywriteRoot.markBusy();
	addVisibleStageView(stage, index);
	PlaywriteRoot.clearBusy();
    }
    
    public void removeVisibleStageView(int index) {
	BoardView view = _boardViews[index];
	Stage stage = view == null ? null : (Stage) view.getBoard();
	_multiStageView.setViewAt(index, null);
	_boardViews[index] = null;
	if (stage != null)
	    stage.disconnectView(view);
    }
    
    final int numberOfStageViews() {
	return getWorld().getNumberOfVisibleStages();
    }
    
    BoardView getStageView(Stage stage) {
	for (int i = numberOfStageViews() - 1; i >= 0; i--) {
	    if (_boardViews[i] != null
		&& stage == (Stage) _boardViews[i].getBoard())
		return _boardViews[i];
	}
	return null;
    }
    
    BoardView getStageView(int i) {
	if (i < 0 || i >= numberOfStageViews())
	    return null;
	return _boardViews[i];
    }
    
    int getStageViewIndex(BoardView stageView) {
	for (int i = 0; i < numberOfStageViews(); i++) {
	    if (stageView == _boardViews[i])
		return i;
	}
	return -1;
    }
    
    private PlaywriteButton initializeSystemMenuView() {
	PlaywriteButton menuSymbol = new PlaywriteButton((Resource
							      .getButtonImage
							  ("WW Show Menu")),
							 (Resource
							      .getAltButtonImage
							  ("WW Show Menu"))) {
	    public boolean mouseDown(MouseEvent event) {
		if (this.isFlashing())
		    this.stopFlashing();
		ToolTips.notifyMouseDown();
		if (this.isEnabled() && this.state() == false) {
		    this.setState(true);
		    this.sendCommand();
		}
		return false;
	    }
	    
	    public void drawViewBackground(Graphics g, Rect interiorRect,
					   boolean drawDownState) {
		/* empty */
	    }
	};
	menuSymbol.setBordered(false);
	menuSymbol
	    .setRaisedColor(_worldWindow.getTitleBar().backgroundColor());
	menuSymbol
	    .setLoweredColor(_worldWindow.getTitleBar().backgroundColor());
	menuSymbol.setCommand("WW Show Menu");
	menuSymbol.setTarget(this);
	menuSymbol.moveTo(30, 0);
	if (PlaywriteRoot.isAuthoring())
	    menuSymbol.setToolTipText(Resource.getToolTip("WW Show Menu"));
	else
	    menuSymbol
		.setToolTipText(Resource.getToolTip("WW Show Menu Player"));
	systemMenuView
	    = new SystemMenuView(_world.getSystemMenu(), menuSymbol);
	return menuSymbol;
    }
    
    private BoardView getBVfromScroller(View scroller) {
	return (BoardView) (scroller == null ? (View) null
			    : ((ScrollableArea) scroller).getContentView());
    }
    
    private ScrollableArea createScrollerForStage(Stage stage, Rect rect) {
	BoardView stageView = (BoardView) stage.createView();
	ScrollableArea scroller
	    = new ScrollableArea(0, 0, rect.width, rect.height, stageView,
				 true, true, false);
	scroller.setBuffered(true);
	scroller.setBackgroundColor(_world.getColor());
	scroller.setHorizontalScrollAmount(stageView.getSquareSize());
	scroller.setVerticalScrollAmount(stageView.getSquareSize());
	scroller.getScrollView().setAllowScrollToOnResize(true);
	stageView.setScrollTarget(scroller);
	return scroller;
    }
    
    private void addDrawerHandlesToWindow(PlaywriteWindow win) {
	showSidelineButton = Util.createVertHandle("WW Show Sidelines", this);
	showSidelineButton.moveTo(0, 0);
	showSidelineButton
	    .setToolTipText(Resource.getToolTip("WW Show Sidelines"));
	showControlPanelButton
	    = Util.createHorizHandle("WW Show Control Panel", this);
	showControlPanelButton.moveTo(0,
				      (win.bounds.maxY()
				       - showControlPanelButton.height()
				       - PlaywriteBorder.BOTTOM_BORDER.height()
				       - 1));
	showControlPanelButton
	    .setToolTipText(Resource.getToolTip("ALT WW Show Control Panel"));
	showControlPanelButton.setState(true);
	_sidelineButtonContainer
	    = new PlaywriteView(this.width() - showSidelineButton.width(), 0,
				showSidelineButton.width(), this.height());
	_sidelineButtonContainer
	    .setBackgroundColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	_sidelineButtonContainer.setVertResizeInstruction(16);
	_sidelineButtonContainer.setHorizResizeInstruction(1);
	_sidelineButtonContainer.addSubview(showSidelineButton);
	this.addSubview(_sidelineButtonContainer);
	win.addSubviewToWindow(showControlPanelButton);
	Util.centerViewVertically(showSidelineButton);
	Util.centerViewHorizontally(showControlPanelButton);
	showControlPanelButton.setHorizResizeInstruction(32);
	showControlPanelButton.setVertResizeInstruction(8);
	showSidelineButton.setVertResizeInstruction(64);
	showSidelineButton.setHorizResizeInstruction(1);
    }
    
    public int getSidelineWidth() {
	return showSidelineButton.width();
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("WW Show Menu")) {
	    _world.setMenuCommandEnabled("command lc",
					 (_world.getState() == World.STOPPED
					  && PlaywriteRoot.app()
						 .numberOfWorlds() == 1));
	    int dy = (systemMenuButton.y() + systemMenuButton.height()
		      + systemMenuButton.superview().y());
	    int dx = systemMenuButton.x() + _worldWindow.getTitleBar().x();
	    systemMenuView.show(this.rootView(),
				new MouseEvent(new Date().getTime(), -1,
					       _worldWindow.x() + dx,
					       _worldWindow.y() + dy, 0));
	    this.rootView().setMouseView(systemMenuView);
	} else if (command == "WW Show Sidelines") {
	    if (sidelineIsVisible())
		hideSidelines();
	    else
		showSidelines();
	} else if (command == "WW Show Control Panel") {
	    if (controlPanelIsVisible())
		hideControlPanel();
	    else
		showControlPanel();
	} else
	    throw new PlaywriteInternalError("Unknown command: " + command);
    }
    
    public void keyDown(KeyEvent event) {
	if (_world.isRunning())
	    _world.queue(event);
	else
	    super.keyDown(event);
    }
    
    public void keyUp(KeyEvent event) {
	if (event.isFunctionKey() == 7 && !_world.isRunning())
	    toggleBoardViewSquareSize();
	else
	    super.keyUp(event);
    }
    
    public boolean handleCommandKeyEvent(KeyEvent event) {
	return _world.getSystemMenu().handleCommandKeyEvent(event);
    }
    
    private void toggleBoardViewSquareSize() {
	BoardView bv = getStageView(0);
	Board board = bv.getBoard();
	PlaywriteWindow win = (PlaywriteWindow) this.window();
	int sqSize = board.getSquareSize();
	if (bv.getSquareSize() == board.getSquareSize()) {
	    Rect screen = this.rootView().bounds();
	    Border border = this.border();
	    int totalRows = 0;
	    int totalColumns = 0;
	    int maxWidth = (screen.width - border.widthMargin()
			    - _sidelineScroller.width());
	    int maxHeight
		= (screen.height - border.heightMargin()
		   - win.getTitleBar().height() - _controlScroller.height());
	    for (int i = 0; i < numberOfStageViews(); i++) {
		board = getStageView(i).getBoard();
		totalRows = Math.max(totalRows, board.numberOfRows());
		totalColumns += board.numberOfColumns();
	    }
	    sqSize = Math.min(maxWidth / totalColumns, maxHeight / totalRows);
	}
	Debug.print(true, "Setting view square size to " + sqSize);
	for (int i = 0; i < numberOfStageViews(); i++)
	    getStageView(i).setSquareSize(sqSize);
	Size pref = getPreferredSize();
	pref = win.windowSizeForContentSize(pref.width, pref.height);
	win.sizeTo(pref.width, pref.height);
    }
    
    void about() {
	Font userFont = Font.fontNamed("Monospaced", 0, 12);
	Font labelFont = Font.fontNamed("Monospaced", 3, 12);
	Font labelFont2 = Font.fontNamed("Monospaced", 3, 10);
	Color backgroundColor = Color.white;
	if (_aboutWindow != null && _aboutWindow.isVisible())
	    _aboutWindow.moveToFront();
	else {
	    Size screenSize = PlaywriteRoot.getRootWindowSize();
	    int width = Math.min(350, screenSize.width);
	    int height = screenSize.height - 20;
	    int x = Math.min(100, (screenSize.width - width) / 2);
	    _aboutWindow = new PlaywriteWindow(x, 10, width, height, _world);
	    _aboutWindow.setTitle
		(Resource.getTextAndFormat("about wint",
					   new Object[] { _world.getName() }));
	    width = _aboutWindow.contentSize().width;
	    height = _aboutWindow.contentSize().height;
	    PlaywriteView scrolledView = new TallView(0, 0, width,
						      height - 20) {
		public void setBounds(int x_10_, int y, int width_11_,
				      int height_12_) {
		    super.setBounds(x_10_, y, width_11_, height_12_);
		    this.layoutView(0, 0);
		}
	    };
	    scrolledView.setHorizResizeInstruction(2);
	    scrolledView.setVertResizeInstruction(16);
	    scrolledView.setBackgroundColor(backgroundColor);
	    _aboutWindow.setOwner(new PlaywriteWindow.DefaultOwner() {
		public void windowDidHide(Window win) {
		    _aboutWindow = null;
		    authorLabel = null;
		    commentLabel = null;
		    authorField = null;
		    commentField = null;
		}
	    });
	    int yGap = 3;
	    PackLayout packLayout = new PackLayout();
	    PackConstraints pc = new PackConstraints();
	    pc.setSide(0);
	    pc.setFillX(true);
	    pc.setPadY(3);
	    packLayout.setDefaultConstraints(pc);
	    scrolledView.setLayoutManager(packLayout);
	    ScrollableArea scroller
		= new ScrollableArea(width, height, scrolledView, false, true);
	    scroller.setBuffered(true);
	    scroller.setVerticalScrollAmount(20);
	    _aboutWindow.addSubview(scroller);
	    scroller.setHorizResizeInstruction(2);
	    scroller.setVertResizeInstruction(16);
	    scroller.setBackgroundColor(backgroundColor);
	    TextFieldContainer authorContainer = new TextFieldContainer();
	    authorContainer.setBackgroundColor(backgroundColor);
	    authorLabel = Util.makeLabel(Resource.getText("about a"));
	    authorLabel.setColor(Color.blue);
	    authorLabel.setFont(labelFont);
	    authorContainer.addSubview(authorLabel);
	    authorField
		= new PlaywriteTextView(0, authorLabel.bounds.maxY() + 3 + 10,
					width, 20);
	    authorField.setString(_world.getAuthor());
	    authorField.setOwner(this);
	    authorField.setFont(userFont);
	    authorContainer.addSubview(authorField);
	    authorContainer.setTrackView(authorField);
	    authorContainer.setLabel(authorLabel);
	    TextFieldContainer commentContainer = new TextFieldContainer();
	    commentContainer.setBackgroundColor(backgroundColor);
	    commentLabel = Util.makeLabel(Resource.getText("about c"));
	    commentLabel.setColor(Color.blue);
	    commentLabel.setFont(labelFont);
	    commentContainer.addSubview(commentLabel);
	    commentField
		= new PlaywriteTextView(0, commentLabel.bounds.maxY() + 3,
					width, 20);
	    commentField.setString(_world.getComment());
	    commentField.setOwner(this);
	    commentField.setFont(userFont);
	    commentContainer.addSubview(commentField);
	    commentContainer.setTrackView(commentField);
	    commentContainer.setLabel(commentLabel);
	    String creditLine
		= Resource.getTextAndFormat("about cred",
					    (new Object[]
					     { _world.getCreatorVersion() }));
	    creditLabel = Util.makeLabel(creditLine);
	    creditLabel.setColor(Color.blue);
	    creditLabel.setFont(labelFont2);
	    TextFieldContainer creditContainer = new TextFieldContainer();
	    creditContainer.setBackgroundColor(backgroundColor);
	    creditContainer.setTrackView(null);
	    creditContainer.addSubview(creditLabel);
	    creditContainer.setLabel(creditLabel);
	    PlaywriteView dummy = new PlaywriteView(0, 0, 1, 1);
	    dummy.setMinSize(1, 1);
	    scrolledView.addSubview(authorContainer);
	    scrolledView.addSubview(commentContainer);
	    scrolledView.addSubview(creditContainer);
	    scrolledView.addSubview(dummy);
	    scrolledView.layoutView(0, 0);
	    if (!PlaywriteRoot.isAuthoring()) {
		authorField.setUserEditable(false);
		commentField.setUserEditable(false);
	    }
	    Size ws
		= _aboutWindow.windowSizeForContentSize(scrolledView.width(),
							scrolledView.height());
	    _aboutWindow.setMinSize(300, 200);
	    ws.height += 25;
	    if (ws.height < _aboutWindow.height()) {
		_aboutWindow.sizeTo(_aboutWindow.width() + 1, ws.height);
		scrolledView.layoutView(0, 0);
	    }
	    _aboutWindow.boundify();
	    Size contentSize = _aboutWindow.contentSize();
	    scroller.sizeTo(contentSize.width, contentSize.height);
	    _aboutWindow.show();
	}
    }
    
    public void attributesDidChange(TextView textView, Range r) {
	/* empty */
    }
    
    public void attributesWillChange(TextView textView, Range r) {
	/* empty */
    }
    
    public void linkWasSelected(TextView textView, Range r, String s) {
	/* empty */
    }
    
    public void selectionDidChange(TextView textView) {
	/* empty */
    }
    
    public void textDidChange(TextView textView, Range r) {
	textView.string().trim();
	textView.sizeToMinSize();
    }
    
    public void textEditingDidBegin(TextView textView) {
	/* empty */
    }
    
    public void textEditingDidEnd(TextView textView) {
	if (textView == authorField)
	    _world.setAuthor(authorField.string());
	else if (textView == commentField)
	    _world.setComment(commentField.string());
	else
	    return;
	_world.setModified(true);
    }
    
    public void textWillChange(TextView textView, Range r) {
	/* empty */
    }
    
    void disable() {
	for (int i = 0; i < _boardViews.length; i++) {
	    if (_boardViews[i] != null)
		_boardViews[i].disable();
	}
	_controlPanelView.disable();
    }
    
    void enable() {
	for (int i = 0; i < _boardViews.length; i++) {
	    if (_boardViews[i] != null)
		_boardViews[i].enable();
	}
	_sidelineView.enable();
	_controlPanelView.enable();
    }
    
    Size getPreferredSize() {
	Size sz = new Size(0, 0);
	for (int i = 0; i < _boardViews.length; i++) {
	    if (_boardViews[i] != null) {
		sz.height = Math.max(sz.height, _boardViews[i].height());
		sz.width += _boardViews[i].width();
	    }
	}
	ScrollableArea.viewSizeForContentSize(sz);
	if (sidelineIsVisible())
	    sz.width += _sidelineScroller.width();
	if (controlPanelIsVisible())
	    sz.height += _controlScroller.height();
	return sz;
    }
    
    boolean sidelineIsVisible() {
	return _sidelineScroller.superview() != null;
    }
    
    boolean controlPanelIsVisible() {
	return _controlScroller.superview() != null;
    }
    
    void showSidelines() {
	if (!sidelineIsVisible()) {
	    _sidelineView.sizeToMinSize();
	    _controlPanelView.sizeToMinSize();
	    this.disableDrawing();
	    int sideX = (this.width() - _sidelineScroller.width()
			 - showSidelineButton.width());
	    _multiStageView.sizeTo(sideX, _multiStageView.height());
	    _sidelineScroller.setBounds(sideX, 0, _sidelineScroller.width(),
					_multiStageView.height());
	    this.addSubview(_sidelineScroller);
	    this.addDirtyRect(_sidelineScroller.bounds());
	    this.reenableDrawing();
	    showSidelineButton
		.setToolTipText(Resource.getToolTip("ALT WW Show Sidelines"));
	}
    }
    
    void hideSidelines() {
	if (sidelineIsVisible()) {
	    this.disableDrawing();
	    _sidelineScroller.removeFromSuperview();
	    _multiStageView.sizeTo(this.width() - showSidelineButton.width(),
				   _multiStageView.height());
	    this.addDirtyRect(_sidelineScroller.bounds());
	    this.reenableDrawing();
	    showSidelineButton
		.setToolTipText(Resource.getToolTip("WW Show Sidelines"));
	}
    }
    
    void showControlPanel() {
	if (!controlPanelIsVisible()) {
	    _sidelineView.sizeToMinSize();
	    _controlPanelView.sizeToMinSize();
	    this.disableDrawing();
	    int newHeight = this.height() - _controlScroller.height();
	    _multiStageView.sizeTo(_multiStageView.width(), newHeight);
	    _sidelineScroller.sizeTo(_sidelineScroller.width(), newHeight);
	    _controlScroller.setBounds(0, newHeight, this.width(),
				       _controlScroller.height());
	    this.addSubview(_controlScroller);
	    this.addDirtyRect(_controlScroller.bounds());
	    this.reenableDrawing();
	    showControlPanelButton.setToolTipText
		(Resource.getToolTip("ALT WW Show Control Panel"));
	}
    }
    
    void hideControlPanel() {
	if (controlPanelIsVisible()) {
	    this.disableDrawing();
	    _controlScroller.removeFromSuperview();
	    _multiStageView.sizeTo(_multiStageView.width(), this.height());
	    _sidelineScroller.sizeTo(_sidelineScroller.width(), this.height());
	    this.addDirtyRect(_controlPanelView.bounds());
	    this.reenableDrawing();
	    showControlPanelButton
		.setToolTipText(Resource.getToolTip("WW Show Control Panel"));
	}
    }
}
