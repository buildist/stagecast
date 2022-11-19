/* AppearanceEditor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.File;
import java.util.Date;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Menu;
import COM.stagecast.ifc.netscape.application.MenuItem;
import COM.stagecast.ifc.netscape.application.MenuView;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class AppearanceEditor extends PlaywriteWindow
    implements Debug.Constants, ResourceIDs.CommandIDs,
	       ResourceIDs.PicturePainterIDs, Target
{
    private static DefaultAEController controller;
    private static final String SHOW_HIDE_EDITOR = "show or hide editor";
    private static final int DRAWER_AREA_X_OFFSET = 74;
    private static final int DRAWER_AREA_Y_OFFSET = 300;
    private PaintFieldScrollPane paintFieldScrollPane;
    private PaintField paintField;
    private ToolPalette paintingTools;
    private View _placeHolder;
    private PlaywriteButton okButton;
    private PlaywriteButton getButton;
    private PlaywriteButton newButton;
    private PlaywriteButton revertButton;
    private PlaywriteButton undoButton;
    private PlaywriteButton clearButton;
    private PlaywriteButton _showEditorButton;
    private PlaywriteView _editorPane;
    private PlaywriteView _drawerPane;
    private AppearanceDrawer _drawer;
    private ScrollableArea _drawerScroller;
    private AppearanceDrawerView _drawerView;
    private CocoaCharacter _character;
    private PlaywriteButton _editMenuButton;
    private PlaywriteButton _fontsMenuButton;
    private MenuView _editMenuView;
    private MenuView _fontsMenuView;
    private MenuItemGroup _fontSizeGroup = new MenuItemGroup();
    private MenuItemGroup _fontNameGroup = new MenuItemGroup();
    private MenuItemGroup _fontStyleGroup = new MenuItemGroup();
    private MenuItemGroup _fontJustificationGroup = new MenuItemGroup();
    private boolean _enabled = true;
    private Color _baseColor = Util.defaultColor;
    private Color _lightColor = Util.defaultLightColor;
    private Color _darkColor = Util.defaultDarkColor;
    private String _originalName;
    private Vector appearanceEventListeners = new Vector();
    private AppearanceEditorTool activeTool;
    private int brushWidth;
    private Color color = Color.black;
    private int scale = 8;
    private Font font = Font.defaultFont();
    
    private class OurMenuView extends MenuView
    {
	PlaywriteButton _button;
	
	public OurMenuView(Menu menu, PlaywriteButton button) {
	    super(menu);
	    _button = button;
	    this.setType(1);
	    this.sizeToMinSize();
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
	    MenuItem clickedItem = null;
	    clickedItem = this.itemForPoint(event.x, event.y);
	    if (this.owner() != null || clickedItem != null || child != null)
		super.mouseUp(event);
	}
    }
    
    private class MenuItemGroup
    {
	private Vector _group = new Vector();
	
	public void addItem(MenuItem item) {
	    _group.addElement(item);
	}
	
	public void selectItem(MenuItem item) {
	    Enumeration elements = _group.elements();
	    while (elements.hasMoreElements()) {
		MenuItem temp = (MenuItem) elements.nextElement();
		if (temp == item)
		    temp.setState(true);
		else
		    temp.setState(false);
	    }
	}
    }
    
    private static class Direction
    {
	public static final Direction UNKNOWN = new Direction();
	public static final Direction EAST = new Direction();
	public static final Direction SOUTH = new Direction();
	
	private Direction() {
	    /* empty */
	}
    }
    
    static void setController(DefaultAEController aec) {
	ASSERT.isNull(controller);
	controller = aec;
    }
    
    public static AppearanceEditor displayAppearanceEditor
	(CocoaCharacter character) {
	return displayAppearanceEditor(character,
				       character.getCurrentAppearance());
    }
    
    public static AppearanceEditor displayAppearanceEditor
	(CocoaCharacter character, Appearance appearance$) {
	CharacterPrototype prototype = character.getPrototype();
	ASSERT.isNotNull(prototype);
	ASSERT.isNotNull(appearance$);
	ASSERT.isTrue(appearance$.getOwner() == prototype);
	AppearanceEditor editor = controller.getEditorFor(prototype);
	if (editor != null) {
	    editor.moveToFront();
	    editor._drawer.setSelectedItem(appearance$);
	    editor.setModelObject(character);
	} else {
	    Enumeration appearances = prototype.getAppearances();
	    while (appearances.hasMoreElements()) {
		Appearance appearance = (Appearance) appearances.nextElement();
		int squareSize = appearance.getSquareSize();
		Bitmap bitmap = appearance.getBitmap();
		if (bitmap.width() > PaintField.GRID_SIZE.width * squareSize
		    || (bitmap.height()
			> PaintField.GRID_SIZE.height * squareSize)) {
		    PlaywriteDialog.warning("Picture Painter Alert 6", true);
		    return null;
		}
	    }
	    editor = new AppearanceEditor(character);
	    editor._drawer.setSelectedItem(appearance$);
	    editor.show();
	}
	return editor;
    }
    
    private AppearanceEditor(CocoaCharacter character) {
	super(5, 5, 100, 370, character.getWorld());
	this.setDisablable(true);
	this.setModelObject(character);
	this.setTitle(Resource.getText("Picture Painter Title"));
	initializeBounds(this.windowSizeForContentSize(411, 110));
	Rect contentBounds = this.contentView().bounds();
	_drawerPane = new PlaywriteView(0, 0, contentBounds.width,
					contentBounds.height);
	_drawerPane.setHorizResizeInstruction(2);
	_drawerPane.setVertResizeInstruction(16);
	_drawerPane.setBackgroundColor(character.getWorld().getColor());
	populateDrawerPane(character);
	this.addSubview(_drawerPane);
	_editorPane
	    = new PlaywriteView(0, contentBounds.height / 2,
				contentBounds.width, contentBounds.height / 2);
	_editorPane.setHorizResizeInstruction(2);
	_editorPane.setVertResizeInstruction(8);
	_editorPane.setBackgroundColor(character.getWorld().getColor());
	if (!PlaywriteRoot.isProfessional())
	    showEditorUI();
	changeWindowColor(character.getWorld().getColor());
	setColor(Color.black, true);
	setFont(Font.defaultFont());
	_showEditorButton.setState(isShowingEditorUI());
    }
    
    private void populateDrawerPane(CocoaCharacter character) {
	_drawer = new AppearanceDrawer(character, this, character.getWorld());
	String resourceID1 = "HandleV";
	String resourceID2 = "HandleH";
	_showEditorButton
	    = PlaywriteButton.createButton(Resource
					       .getAltButtonImage(resourceID1),
					   Resource
					       .getAltButtonImage(resourceID2),
					   "show or hide editor", this);
	Bitmap img1 = Resource.getAltButtonImage(resourceID1);
	Bitmap img2 = Resource.getAltButtonImage(resourceID2);
	_showEditorButton.sizeTo(Math.max(img1.width(), img2.width()),
				 Math.max(img1.width(), img2.width()));
	_showEditorButton.setType(1);
	_showEditorButton
	    .setToolTipText("open editor <- make this a resource");
	_drawerPane.addSubview(_showEditorButton);
	newButton
	    = PlaywriteButton.createTextButton("Picture Painter new button",
					       "command n", this);
	_drawerPane.addSubview(newButton);
	getButton
	    = PlaywriteButton.createTextButton("Picture Painter get button",
					       "command g", this);
	_drawerPane.addSubview(getButton);
	Size newButtonSize = newButton.minSize();
	Size getButtonSize = getButton.minSize();
	Size preferredSize
	    = new Size(Math.max(newButtonSize.width, getButtonSize.width),
		       Math.max(newButtonSize.height, getButtonSize.height));
	newButton.setBounds(10, 10, preferredSize.width, preferredSize.height);
	getButton.setBounds(10, newButton.bounds.maxY() + 5,
			    preferredSize.width, preferredSize.height);
	_drawerView = new AppearanceDrawerView(_drawer, 100, 100);
	_drawerScroller
	    = new ScrollableArea(new Rect(newButton.bounds.maxX() + 5, 10,
					  (_drawerPane.width()
					   - (newButton.bounds.maxX() + 5)
					   - 10),
					  _drawerPane.height() - 20),
				 _drawerView, true, true);
	_drawerScroller.setAllowSmallContentView(false);
	_drawerScroller.maximizeContentView();
	_drawerView.init();
	_drawerScroller.changeWindowColor(RuleEditor.INNER_WINDOW_COLOR,
					  RuleEditor.INNER_WINDOW_COLOR);
	_drawerScroller.setBackgroundColor(RuleEditor.INNER_WINDOW_COLOR);
	_drawerScroller.setBorder(new BezelBorder(1,
						  (RuleEditor
						   .INNER_WINDOW_COLOR),
						  RuleEditor.LIGHT_LINE_COLOR,
						  RuleEditor.DARK_LINE_COLOR) {
	    public void drawInRect(Graphics g, int x, int y, int width,
				   int height) {
		BezelBorder.drawBezel(g, x, y, width, height,
				      RuleEditor.INNER_WINDOW_COLOR,
				      RuleEditor.LIGHT_LINE_COLOR,
				      RuleEditor.DARK_LINE_COLOR,
				      RuleEditor.DARKER_LINE_COLOR, false);
	    }
	});
	_drawerScroller.setAllowSmallContentView(false);
	_drawerScroller.setHorizResizeInstruction(2);
	_drawerScroller.setVertResizeInstruction(16);
	_drawerPane.addSubview(_drawerScroller);
	_showEditorButton
	    .moveTo(10, _drawerPane.height() - _showEditorButton.height() - 5);
	_showEditorButton.setVertResizeInstruction(8);
    }
    
    private void populateEditorPane() {
	paintingTools = new ToolPalette(this);
	_editorPane.addSubview(paintingTools);
	paintField = new PaintField(this);
	paintField.setFocusedView();
	paintFieldScrollPane = new PaintFieldScrollPane(this, paintField);
	paintField.setAppearance
	    ((Appearance) _drawer.getSelectedItem().copy(),
	     _drawer.getAppearanceDrawerIconModel(_drawer.getSelectedItem()));
	_editorPane.addSubview(paintFieldScrollPane);
	paintingTools.setBounds(10, 10, 83, 250);
	paintFieldScrollPane.moveTo(paintingTools.bounds.maxX() + 5, 8);
	int minHeight = Math.max(paintingTools.bounds.maxY(),
				 paintFieldScrollPane.bounds.maxY());
	minHeight += 10;
	_editorPane.setMinSize(paintFieldScrollPane.bounds.maxX() + 20,
			       minHeight);
	_editorPane.sizeToMinSize();
    }
    
    public void showEditorUI() {
	this.disableDrawing();
	populateEditorPane();
	this.addSubview(_editorPane);
	Rect contentBounds = this.contentView().bounds();
	this.sizeBy(0, _editorPane.height());
	_editorPane.moveTo(0, contentBounds.height);
	_drawerPane.setBounds(0, 0, _drawerPane.width(), _editorPane.y());
	_editMenuButton = createMenuButton("Picture Painter Edit Menu");
	_editMenuView = createEditMenuView();
	this.getTitleBar().addSubviewLeft(_editMenuButton);
	_fontsMenuButton = createMenuButton("Picture Painter Fonts Menu");
	_fontsMenuView = createFontsMenuView();
	this.getTitleBar().addSubviewLeft(_fontsMenuButton);
	revertButton = (PlaywriteButton.createTextButton
			("Picture Painter Command Revert",
			 "Picture Painter Command Revert", this));
	this.getTitleBar().addSubviewRight(revertButton);
	undoButton
	    = PlaywriteButton.createTextButton("Picture Painter Command Undo",
					       "Picture Painter Command Undo",
					       this);
	this.getTitleBar().addSubviewRight(undoButton);
	_placeHolder = new View(0, 0, 5, 10);
	_placeHolder.setMinSize(5, 10);
	this.getTitleBar().addSubviewRight(_placeHolder);
	clearButton
	    = PlaywriteButton.createTextButton("Picture Painter Command Clear",
					       "Picture Painter Command Clear",
					       this);
	this.getTitleBar().addSubviewRight(clearButton);
	boundify();
	Bitmap b = _drawer.getSelectedItem().getBitmap();
	int squareSize = _drawer.getSelectedItem().getSquareSize();
	int newScale = 1;
	for (int i = 2; i <= 8; i *= 2) {
	    if (i * squareSize <= this.width()
		&& i * squareSize <= this.height()
		&& i * b.width() <= this.width()
		&& i * b.height() <= this.height())
		newScale = i;
	}
	setScale(newScale);
	this.reenableDrawing();
    }
    
    public void hideEditorUI() {
	this.disableDrawing();
	_drawerPane.setVertResizeInstruction(4);
	_editorPane.removeFromSuperview();
	this.sizeBy(0, -_editorPane.height());
	_drawerPane.setVertResizeInstruction(16);
	paintingTools.removeFromSuperview();
	paintField.removeFromSuperview();
	paintFieldScrollPane.removeFromSuperview();
	appearanceEventListeners.removeAllElements();
	_editMenuButton.removeFromSuperview();
	_editMenuView.removeFromSuperview();
	_fontsMenuButton.removeFromSuperview();
	_fontsMenuView.removeFromSuperview();
	revertButton.removeFromSuperview();
	undoButton.removeFromSuperview();
	_placeHolder.removeFromSuperview();
	clearButton.removeFromSuperview();
	paintingTools.discard();
	paintField.discard();
	paintFieldScrollPane.discard();
	_editMenuButton.discard();
	_fontsMenuButton.discard();
	revertButton.discard();
	undoButton.discard();
	clearButton.discard();
	paintingTools = null;
	paintField = null;
	paintFieldScrollPane = null;
	_editMenuButton = null;
	_editMenuView = null;
	_fontsMenuButton = null;
	_fontsMenuView = null;
	revertButton = null;
	undoButton = null;
	_placeHolder = null;
	clearButton = null;
	activeTool = null;
	this.reenableDrawing();
    }
    
    public boolean isShowingEditorUI() {
	return paintingTools != null && paintingTools.superview() != null;
    }
    
    public boolean isEnabled() {
	return _enabled;
    }
    
    void disable() {
	if (isShowingEditorUI()) {
	    _editMenuButton.setEnabled(false);
	    _fontsMenuButton.setEnabled(false);
	    revertButton.setEnabled(false);
	    undoButton.setEnabled(false);
	    clearButton.setEnabled(false);
	    paintingTools.disable();
	    paintField.disable();
	    paintFieldScrollPane.getHomeSquareToolButton().setEnabled(false);
	}
	newButton.setEnabled(false);
	getButton.setEnabled(false);
	_showEditorButton.setEnabled(false);
	_enabled = false;
    }
    
    void enable() {
	if (isShowingEditorUI()) {
	    _editMenuButton.setEnabled(true);
	    _fontsMenuButton.setEnabled(true);
	    revertButton.setEnabled(true);
	    undoButton.setEnabled(true);
	    clearButton.setEnabled(true);
	    paintingTools.enable();
	    paintField.enable();
	    paintFieldScrollPane.getHomeSquareToolButton().setEnabled(true);
	}
	newButton.setEnabled(true);
	getButton.setEnabled(true);
	_showEditorButton.setEnabled(true);
	_enabled = true;
    }
    
    public void worldStateChanged(Object who, Object oldState,
				  Object transition, Object newState) {
	if (transition == World.STOP)
	    enable();
	else if (transition == World.RUN)
	    disable();
	super.worldStateChanged(who, oldState, transition, newState);
    }
    
    protected Button createCloseButton() {
	if (okButton == null) {
	    this.getTitleBar().addRow();
	    okButton = PlaywriteButton.createFromResource("Win close", true);
	    okButton.setImage(Resource.getButtonImage("command done w. text"));
	    okButton.setAltImage
		(Resource.getAltButtonImage("command done w. text"));
	    okButton.setCommand("Win close");
	    okButton.setTarget(this);
	}
	return okButton;
    }
    
    private PlaywriteButton createMenuButton(String resourceID) {
	PlaywriteButton button = new PlaywriteButton(0, 0, 0, 0) {
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
	};
	Bitmap image = Resource.getButtonImage(resourceID);
	button.setImage(image);
	button.setAltImage(Resource.getAltButtonImage(resourceID));
	button.sizeTo(image.width(), image.height());
	button.setCommand(resourceID);
	button.setTarget(this);
	button.setToolTipText(Resource.getToolTip(resourceID));
	button.setBordered(false);
	return button;
    }
    
    private MenuView createEditMenuView() {
	Menu menu = new Menu(false);
	menu.addItem(Resource.getText("command cut"), "command cut", this);
	menu.addItem(Resource.getText("command copy"), "command copy", this);
	menu.addItem(Resource.getText("command p"), "command p", this);
	menu.addSeparator();
	menu.addItem(Resource.getText("command sa"), "command sa", this);
	menu.addSeparator();
	menu.addItem(Resource.getText("Picture Painter Command Rotate"),
		     "Picture Painter Command Rotate", this);
	menu.addItem(Resource
			 .getText("Picture Painter Command Flip Horizontal"),
		     "Picture Painter Command Flip Horizontal", this);
	menu.addItem(Resource.getText("Picture Painter Command Flip Vertical"),
		     "Picture Painter Command Flip Vertical", this);
	menu.addItem(Resource.getText("Picture Painter Command Stretch"),
		     "Picture Painter Command Stretch", this);
	return new OurMenuView(menu, _editMenuButton);
    }
    
    private MenuView createFontsMenuView() {
	Menu menu = new Menu(false);
	COM.stagecast.ifc.netscape.application.Image check
	    = Resource.getImage("Picture Painter Menu Item Check");
	COM.stagecast.ifc.netscape.application.Image uncheck
	    = Resource.getImage("Picture Painter Menu Item Uncheck");
	for (int i = 0; i < Util.FONT_SIZES.length; i++) {
	    final int size = Util.FONT_SIZES[i];
	    Target target = new Target() {
		public void performCommand(String command, Object data) {
		    setFontToSize(size);
		    _fontSizeGroup.selectItem((MenuItem) data);
		}
	    };
	    MenuItem item
		= menu.addItem(Integer.toString(size), null, target, true);
	    item.setCheckedImage(check);
	    item.setUncheckedImage(uncheck);
	    _fontSizeGroup.addItem(item);
	    if (i == 0 || size == 12) {
		setFontToSize(size);
		_fontSizeGroup.selectItem(item);
	    }
	}
	menu.addSeparator();
	for (int i = 0; i < Util.FONT_FAMILIES.length; i++) {
	    final String javaName = Util.FONT_FAMILIES[i].getJavaName();
	    Target target = new Target() {
		public void performCommand(String command, Object data) {
		    setFontToName(javaName);
		    _fontNameGroup.selectItem((MenuItem) data);
		}
	    };
	    MenuItem item = menu.addItem(Util.FONT_FAMILIES[i].getUserName(),
					 null, target, true);
	    item.setCheckedImage(check);
	    item.setUncheckedImage(uncheck);
	    _fontNameGroup.addItem(item);
	    if (i == 0) {
		setFontToName(javaName);
		_fontNameGroup.selectItem(item);
	    }
	}
	menu.addSeparator();
	for (int i = 0; i < Util.FONT_STYLES.length; i++) {
	    final int style = Util.FONT_STYLES[i].getJavaStyle();
	    Target target = new Target() {
		public void performCommand(String command, Object data) {
		    setFontToStyle(style);
		    _fontStyleGroup.selectItem((MenuItem) data);
		}
	    };
	    MenuItem item = addMenuItem(Util.FONT_STYLES[i].getUserStyle(),
					target, menu, _fontStyleGroup);
	    if (i == 0) {
		setFontToStyle(style);
		_fontStyleGroup.selectItem(item);
	    }
	}
	menu.addSeparator();
	Target target = new Target() {
	    public void performCommand(String command, Object data) {
		setJustification(0);
		_fontJustificationGroup.selectItem((MenuItem) data);
	    }
	};
	MenuItem item = addMenuItem("Picture Painter Command Left Justify",
				    target, menu, _fontJustificationGroup);
	setFontToStyle(0);
	_fontJustificationGroup.selectItem(item);
	target = new Target() {
	    public void performCommand(String command, Object data) {
		setJustification(1);
		_fontJustificationGroup.selectItem((MenuItem) data);
	    }
	};
	addMenuItem("Picture Painter Command Center Justify", target, menu,
		    _fontJustificationGroup);
	target = new Target() {
	    public void performCommand(String command, Object data) {
		setJustification(2);
		_fontJustificationGroup.selectItem((MenuItem) data);
	    }
	};
	addMenuItem("Picture Painter Command Right Justify", target, menu,
		    _fontJustificationGroup);
	return new OurMenuView(menu, _fontsMenuButton);
    }
    
    private MenuItem addMenuItem(String resourceID, Target handler, Menu menu,
				 MenuItemGroup menuGroup) {
	MenuItem item
	    = menu.addItem(Resource.getText(resourceID), null, handler, true);
	item.setCheckedImage(Resource
				 .getImage("Picture Painter Menu Item Check"));
	item.setUncheckedImage
	    (Resource.getImage("Picture Painter Menu Item Uncheck"));
	menuGroup.addItem(item);
	return item;
    }
    
    private void initializeBounds(Size ourSize) {
	COM.stagecast.ifc.netscape.application.RootView rootView
	    = PlaywriteRoot.getMainRootView();
	Point defaultPosition
	    = new Point((rootView.width() - ourSize.width) / 2,
			(rootView.height() - ourSize.height) / 3);
	defaultPosition.x = defaultPosition.x >= 0 ? defaultPosition.x : 0;
	defaultPosition.y = defaultPosition.y >= 0 ? defaultPosition.y : 0;
	Point basePosition = new Point(defaultPosition);
	Point ourPosition = new Point(defaultPosition);
	int offset = this.getTitleBar().height() / 2;
	Direction direction = Direction.UNKNOWN;
	while (controller.isPositionNearActiveEditor(ourPosition) == true) {
	    ourPosition.x += offset;
	    ourPosition.y += offset;
	    if (ourPosition.y + ourSize.height > rootView.height()) {
		if (basePosition.y != 0)
		    basePosition.moveTo(0, 0);
		else if (direction == Direction.UNKNOWN)
		    direction = Direction.EAST;
		if (direction == Direction.EAST)
		    basePosition.x += offset;
		else if (direction == Direction.SOUTH)
		    basePosition.y += offset;
		ourPosition.moveTo(basePosition.x, basePosition.y);
		if (ourPosition.x + ourSize.width > rootView.width()
		    || ourPosition.y + ourSize.height > rootView.height()) {
		    ourPosition = defaultPosition;
		    break;
		}
	    }
	    if (ourPosition.x + ourSize.width > rootView.width()) {
		if (basePosition.x != 0)
		    basePosition.moveTo(0, 0);
		else if (direction == Direction.UNKNOWN)
		    direction = Direction.SOUTH;
		if (direction == Direction.EAST)
		    basePosition.x += offset;
		else if (direction == Direction.SOUTH)
		    basePosition.y += offset;
		ourPosition.moveTo(basePosition.x, basePosition.y);
		if (ourPosition.x + ourSize.width > rootView.width()
		    || ourPosition.y + ourSize.height > rootView.height()) {
		    ourPosition = defaultPosition;
		    break;
		}
	    }
	}
	this.setBounds(ourPosition.x, ourPosition.y, ourSize.width,
		       ourSize.height);
    }
    
    public void destroyWindow() {
	super.destroyWindow();
	_drawer.destroy();
	_drawer = null;
	this.setModelObject(null);
	paintFieldScrollPane = null;
	paintField = null;
	paintingTools = null;
	okButton = null;
	getButton = null;
	newButton = null;
	revertButton = null;
	undoButton = null;
	clearButton = null;
	_drawerScroller = null;
	_drawerView = null;
	_editMenuButton = null;
	_fontsMenuButton = null;
	_editMenuView = null;
	_fontsMenuView = null;
	_fontSizeGroup = null;
	_fontNameGroup = null;
	_fontStyleGroup = null;
	_fontJustificationGroup = null;
    }
    
    public int getTransparentPixelColor() {
	return 16777215;
    }
    
    final CocoaCharacter getCharacter() {
	return (CocoaCharacter) this.getModelObject();
    }
    
    final String getCharacterName() {
	return getCharacter().getName();
    }
    
    public Color getBaseColor() {
	return _baseColor;
    }
    
    public Color getLightColor() {
	return _lightColor;
    }
    
    public Color getDarkColor() {
	return _darkColor;
    }
    
    void changeWindowColor(Color color) {
	super.changeWindowColor(color);
	_baseColor = color;
	_lightColor = color;
	_darkColor = color;
	this.getTitleBar().changeWindowColor(_baseColor);
	if (isShowingEditorUI())
	    paintFieldScrollPane.changeWindowColor(_baseColor, _baseColor);
    }
    
    public void close() {
	CharacterPrototype proto = getCharacter().getPrototype();
	super.close();
	controller.destroyEditorFor(proto);
    }
    
    public void hide() {
	if (isShowingEditorUI()) {
	    if (_editMenuView.isVisible())
		_editMenuView.hide();
	    if (_fontsMenuView.isVisible())
		_fontsMenuView.hide();
	}
	super.hide();
    }
    
    public boolean boundify() {
	if (Tutorial.getTutorial() != null) {
	    Rect r = Tutorial.getTutorial().getWindowBounds("PicturePainter");
	    if (r != null) {
		this.setBounds(r);
		return true;
	    }
	}
	return super.boundify();
    }
    
    public boolean prepareToClose() {
	return _drawer.prepareForClose();
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("Win close")) {
	    if (prepareToClose() == true) {
		getCharacter().getPrototype().setModified(true);
		getCharacter().getWorld().setModified(true);
		close();
	    }
	} else if (command.equals("command n"))
	    performNewCommand();
	else if (command.equals("command g")) {
	    boolean rightClick
		= (((PlaywriteButton) data).getLastModifiers() & 0x4) != 0;
	    performGetCommand(rightClick && PlaywriteRoot.isProfessional());
	} else if (command.equals("show or hide editor")) {
	    if (isShowingEditorUI())
		hideEditorUI();
	    else
		showEditorUI();
	    this.setDirty(true);
	} else if (command.equals("Picture Painter Command Clear"))
	    paintField.clear();
	else if (command.equals("Picture Painter Command Undo"))
	    paintField.undo();
	else if (command.equals("Picture Painter Command Revert")) {
	    AppearanceEditorTool tool = getTool();
	    if (tool != null)
		tool.prepareForPaintFieldChange();
	    if (paintField.isAppearanceDirty() == true)
		paintField.setAppearanceBitmap(_drawer.getSelectedItem()
						   .getBitmap());
	    _drawer.getSelectedItem().setName(_originalName);
	} else if (command.equals("Picture Painter Edit Menu")) {
	    int dy = (_editMenuButton.superview().y() + _editMenuButton.y()
		      + _editMenuButton.height());
	    int dx = _editMenuButton.x() + this.getTitleBar().x();
	    _editMenuView.show(this.rootView(),
			       new MouseEvent(new Date().getTime(), -1,
					      this.x() + dx, this.y() + dy,
					      0));
	    this.rootView().setMouseView(_editMenuView);
	} else if (command.equals("Picture Painter Fonts Menu")) {
	    int dy = (_fontsMenuButton.superview().y() + _fontsMenuButton.y()
		      + _fontsMenuButton.height());
	    int dx = _fontsMenuButton.x() + this.getTitleBar().x();
	    _fontsMenuView.show(this.rootView(),
				new MouseEvent(new Date().getTime(), -1,
					       this.x() + dx, this.y() + dy,
					       0));
	    this.rootView().setMouseView(_fontsMenuView);
	} else if (command.equals("command sa"))
	    paintField.selectAll(true);
	else if (command.equals("Picture Painter Command Rotate"))
	    paintField.rotate(true);
	else if (command.equals("Picture Painter Command Flip Horizontal"))
	    paintField.flipHorizontal();
	else if (command.equals("Picture Painter Command Flip Vertical"))
	    paintField.flipVertical();
	else if (command.equals("Picture Painter Command Stretch"))
	    paintField.stretch();
	else if (command.equals("command cut"))
	    paintField.cut();
	else if (command.equals("command copy"))
	    paintField.copy();
	else if (command.equals("command p"))
	    paintField.paste();
	else
	    super.performCommand(command, data);
    }
    
    public void appearanceWasSelected(Appearance a) {
	_originalName = a.getName();
    }
    
    private void performNewCommand() {
	boolean saved = _drawer.saveChangesToSelectedItemUnlessAborted();
	if (saved) {
	    Appearance item = (Appearance) _drawer.getSelectedItem().copy();
	    item.setGeneratedName(getCharacter().getPrototype()
				      .makeUniqueApppearanceName());
	    int squareSize = getCharacterSquareSize();
	    if (squareSize != item.getSquareSize()) {
		Bitmap bitmap = item.getBitmap();
		float scaleFactor
		    = (float) squareSize / (float) item.getSquareSize();
		Rect preferredSize
		    = new Rect(0, 0,
			       (int) ((float) bitmap.width() * scaleFactor),
			       (int) ((float) bitmap.height() * scaleFactor));
		Util.scaleRectToImageProportion(preferredSize, bitmap);
		bitmap = BitmapManager.createScaledBitmapManager(bitmap,
								 (preferredSize
								  .width),
								 (preferredSize
								  .height));
		int maxSize = squareSize * PaintField.GRID_SIZE.width;
		if (bitmap.width() > maxSize || bitmap.height() > maxSize) {
		    Bitmap original = bitmap;
		    PlaywriteDialog.warning("Picture Painter Alert 5", true);
		    int width = Math.min(original.width(), maxSize);
		    int height = Math.min(original.height(), maxSize);
		    int[] pixels = new int[width * height];
		    boolean success = original.grabPixels(pixels, 0, 0, width,
							  height, 0, width);
		    ASSERT.isTrue(success, "grabPixels");
		    bitmap = BitmapManager.createBitmapManager(pixels, width,
							       height);
		    original.flush();
		}
		item.setBitmap(bitmap);
		item.setSquareSize(squareSize);
	    }
	    if (_drawer.allowAdd(item)) {
		_drawer.add(item);
		if (isShowingEditorUI())
		    paintField.selectAll(false);
	    }
	}
    }
    
    private void importBitmap(Bitmap bitmap, boolean batch,
			      String appearanceName) {
	int squareSize = getCharacterSquareSize();
	bitmap = cropBitmapToMaxSize(bitmap,
				     squareSize * PaintField.GRID_SIZE.width,
				     batch ^ true);
	Shape shape = Appearance.makeShape(bitmap, getCharacterSquareSize());
	Rect minRect = shape.getMinimalBoundingRect();
	Point bitmapOrigin
	    = new Point(minRect.x, minRect.y + minRect.height - 1);
	Util.transformLL1ToUL0(bitmapOrigin, shape.getHeight());
	Rect bitmapRect
	    = new Rect(bitmapOrigin.x * squareSize,
		       bitmapOrigin.y * squareSize, minRect.width * squareSize,
		       minRect.height * squareSize);
	bitmap = cropBitmapToRect(bitmap, bitmapRect, false);
	shape = shape.getMinimalShape();
	if (shape != null) {
	    setHomeSquareToOccupiedSquare(shape);
	    Appearance newAppearance
		= new Appearance(appearanceName, bitmap,
				 getCharacterSquareSize(), shape);
	    newAppearance.setOwner(getCharacter().getPrototype());
	    Appearance oldAppearance = null;
	    if (batch)
		oldAppearance
		    = getCharacter().getAppearanceNamed(appearanceName);
	    else
		oldAppearance = _drawer.getSelectedItem();
	    if (oldAppearance != null) {
		boolean updateRules = (oldAppearance.getShape()
					   .equals(newAppearance.getShape())
				       ^ true);
		_drawer.updateAppearance(oldAppearance, newAppearance,
					 updateRules);
		if (_drawer.getSelectedItem() == oldAppearance
		    && getPaintField() != null)
		    getPaintField().setAppearance
			((Appearance) newAppearance.copy(),
			 (_drawer.getAppearanceDrawerIconModel
			  (_drawer.getSelectedItem())));
	    } else
		_drawer.add(newAppearance, false);
	}
    }
    
    private void performGetCommand(boolean getAll) {
	if (getAll) {
	    FileIO.FileIterator iterator = new FileIO.FileIterator() {
		public void handleFile(String fname) {
		    Bitmap bitmap
			= ImageIO.importBitmapNamed(null, fname, false);
		    if (bitmap != null) {
			String name
			    = (Util.dePercentString
			       (Util.getFilePart(new File(fname).getName())));
			AppearanceEditor.this.importBitmap(bitmap, true, name);
		    }
		}
	    };
	    ImageIO.importAllImages
		(iterator, Resource.getText("dialog choose all pictures"));
	} else {
	    final Appearance selected = _drawer.getSelectedItem();
	    Named named = new Named() {
		public String getName() {
		    return selected.getName();
		}
		
		public void setName(String name) {
		    if (selected.wasNameGenerated())
			selected.setGeneratedName(name);
		}
	    };
	    Bitmap bitmap
		= ImageIO.importPicture(getCharacter().getWorld(), named);
	    if (bitmap != null)
		importBitmap(bitmap, false, selected.getName());
	}
    }
    
    private void setHomeSquareToOccupiedSquare(Shape shape) {
	if (!shape.getLocationHV(shape.getOriginX(), shape.getOriginY())) {
	    for (int v = shape.getHeight(); v > 0; v--) {
		for (int h = 1; h <= shape.getWidth(); h++) {
		    if (shape.getLocationHV(h, v)) {
			shape.setOrigin(h, v);
			return;
		    }
		}
	    }
	}
    }
    
    private Bitmap cropBitmapToMaxSize(Bitmap bitmap, int maxSize,
				       boolean showWarning) {
	return cropBitmapToRect(bitmap, new Rect(0, 0, maxSize, maxSize),
				showWarning);
    }
    
    private Bitmap cropBitmapToRect(Bitmap bitmap, Rect cropRect,
				    boolean showWarning) {
	Rect rect = new Rect(cropRect);
	rect.intersectWith(0, 0, bitmap.width(), bitmap.height());
	if (rect.x != 0 || rect.y != 0 || rect.width != bitmap.width()
	    || rect.height != bitmap.height()) {
	    Bitmap original = bitmap;
	    if (showWarning)
		PlaywriteDialog.warning("Picture Painter Alert 3", true);
	    int[] pixels = new int[rect.width * rect.height];
	    boolean success
		= original.grabPixels(pixels, rect.x, rect.y, rect.width,
				      rect.height, 0, rect.width);
	    ASSERT.isTrue(success, "grabPixels");
	    bitmap = BitmapManager.createBitmapManager(pixels, rect.width,
						       rect.height);
	    original.flush();
	}
	return bitmap;
    }
    
    public void setFontToName(String name) {
	Font newFont = new Font(name, font.style(), font.size());
	setFont(newFont);
    }
    
    public void setFontToStyle(int style) {
	Font newFont = new Font(font.name(), style, font.size());
	setFont(newFont);
    }
    
    public void setFontToSize(int size) {
	Font newFont = new Font(font.name(), font.style(), size);
	setFont(newFont);
    }
    
    public void selectSelectionTool() {
	paintingTools.selectSelectionTool();
    }
    
    public void didBecomeMain() {
	super.didBecomeMain();
	if (this.isVisible())
	    _drawer.onPainterBecameMainWindow();
    }
    
    public void didResignMain() {
	super.didResignMain();
	if (this.isVisible())
	    _drawer.onPainterResignedMainWindow();
    }
    
    public synchronized void addAppearanceEventListener
	(AppearanceEventListener l) {
	if (!appearanceEventListeners.contains(l))
	    appearanceEventListeners.addElement(l);
    }
    
    public synchronized void removeAppearanceEventListener
	(AppearanceEventListener l) {
	if (appearanceEventListeners.contains(l))
	    appearanceEventListeners.removeElement(l);
    }
    
    public PaintField getPaintField() {
	return paintField;
    }
    
    PaintFieldScrollPane getPaintFieldScrollPane() {
	return paintFieldScrollPane;
    }
    
    public AppearanceEditorTool getTool() {
	return activeTool;
    }
    
    public synchronized void setTool(AppearanceEditorTool tool) {
	if (activeTool != null)
	    activeTool.onToolUnset();
	activeTool = tool;
	Vector v = (Vector) appearanceEventListeners.clone();
	for (int i = 0; i < v.size(); i++) {
	    AppearanceEventListener l
		= (AppearanceEventListener) v.elementAt(i);
	    l.setTool(tool);
	}
    }
    
    public int getBrushWidth() {
	return brushWidth;
    }
    
    public synchronized void setBrushWidth(int w) {
	brushWidth = w;
	Vector v = (Vector) appearanceEventListeners.clone();
	for (int i = 0; i < v.size(); i++) {
	    AppearanceEventListener l
		= (AppearanceEventListener) v.elementAt(i);
	    l.setBrushWidth(w);
	}
    }
    
    public Color getColor() {
	return color;
    }
    
    public synchronized void setColor(Color c, boolean completed) {
	color = c;
	Vector v = (Vector) appearanceEventListeners.clone();
	for (int i = 0; i < v.size(); i++) {
	    AppearanceEventListener l
		= (AppearanceEventListener) v.elementAt(i);
	    l.setColor(c, completed);
	}
    }
    
    public int getScale() {
	return scale;
    }
    
    public synchronized void setScale(int s) {
	scale = s;
	Vector v = (Vector) appearanceEventListeners.clone();
	for (int i = 0; i < v.size(); i++) {
	    AppearanceEventListener l
		= (AppearanceEventListener) v.elementAt(i);
	    l.setScale(s);
	}
	paintFieldScrollPane.checkArrows();
    }
    
    public Font getFont() {
	return font;
    }
    
    public synchronized void setFont(Font f) {
	font = f;
	Vector v = (Vector) appearanceEventListeners.clone();
	for (int i = 0; i < v.size(); i++) {
	    AppearanceEventListener l
		= (AppearanceEventListener) v.elementAt(i);
	    l.setFont(f);
	}
    }
    
    public synchronized void setJustification(int justification) {
	Vector v = (Vector) appearanceEventListeners.clone();
	for (int i = 0; i < v.size(); i++) {
	    AppearanceEventListener l
		= (AppearanceEventListener) v.elementAt(i);
	    l.setJustification(justification);
	}
    }
    
    private int getCharacterSquareSize() {
	return (getCharacter().getContainer() instanceof Board
		? ((Board) getCharacter().getContainer()).getSquareSize()
		: getCharacter().getWorld().getFirstVisibleStage()
		      .getSquareSize());
    }
}
