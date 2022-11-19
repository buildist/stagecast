/* SidelineView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.Observable;
import java.util.Observer;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.FileChooser;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.application.Window;
import COM.stagecast.ifc.netscape.application.WindowOwner;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class SidelineView extends TallView
    implements Target, Observer, ToolSource, ToolDestination, WindowOwner,
	       Worldly, Debug.Constants, ResourceIDs.CommandIDs,
	       ResourceIDs.DrawerIDs
{
    static int DEFAULT_WIDTH = 95;
    static int DEFAULT_HEIGHT = 200;
    static String DRAWER_BUTTON = "Drawer Button";
    private static Bitmap splitOneIcon = null;
    private static Bitmap splitTwoIcon = null;
    private WorldView worldView;
    private StateWatcher worldWatcher;
    private Vector drawerInfos;
    private PlaywriteButton splitToggleButton;
    
    static interface WinWatcher
    {
	public void setWindow(PlaywriteWindow playwritewindow);
    }
    
    private class DrawerInfo implements WinWatcher
    {
	Button _button;
	Drawer _drawer;
	PlaywriteWindow _window;
	int _colorCode;
	
	DrawerInfo(Button button, Drawer drawer, int colorCode) {
	    _button = button;
	    _drawer = drawer;
	    _window = null;
	    _colorCode = colorCode;
	}
	
	public void setWindow(PlaywriteWindow window) {
	    Point p = new Point();
	    Rect maxRect
		= new Rect(0, 0, PlaywriteRoot.getRootWindowSize().width,
			   PlaywriteRoot.getRootWindowSize().height);
	    Rect winBounds = window.bounds();
	    SidelineView.this.convertPointToView(null,
						 new Point(_button.x(),
							   _button.y()),
						 p);
	    _window = window;
	    _window.setOwner(SidelineView.this);
	    _window.getTitleBar().setColor(_colorCode);
	    if (p.x - winBounds.width > maxRect.x
		|| p.x + _button.width() + winBounds.width > maxRect.maxX())
		winBounds.moveTo(p.x - winBounds.width, p.y);
	    else
		winBounds.moveTo(p.x + _button.width(), p.y);
	    _window.setBounds(winBounds);
	}
    }
    
    SidelineView(WorldView worldView) {
	super(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	if (splitOneIcon == null && PlaywriteRoot.isAuthoring()) {
	    splitOneIcon = Resource.getImage("drawer stages split1");
	    splitTwoIcon = Resource.getImage("drawer stages split2");
	}
	this.worldView = worldView;
	PackLayout lm = new PackLayout();
	PackConstraints defaultPC = new PackConstraints();
	defaultPC.setPadY(3);
	lm.setDefaultConstraints(defaultPC);
	this.setLayoutManager(lm);
	drawerInfos = new Vector(8);
	populate();
	worldWatcher = new StateWatcher() {
	    public void stateChanged(Object target, Object oldState,
				     Object transition, Object newState) {
		SidelineView.this.worldStateChanged(newState);
	    }
	};
	worldView.getWorld().addStateWatcher(worldWatcher);
	this.layoutView(0, 0);
    }
    
    WorldView getWorldView() {
	return worldView;
    }
    
    public World getWorld() {
	return worldView.getWorld();
    }
    
    public boolean isTransparent() {
	return false;
    }
    
    public void sizeToMinSize() {
	super.sizeToMinSize();
    }
    
    public void drawView(Graphics g) {
	g.setColor(getWorld().getColor());
	g.fillRect(this.localBounds());
    }
    
    public void registerDrawer(Button button, Drawer drawer, int colorCode) {
	button.setCommand(DRAWER_BUTTON);
	button.setTarget(this);
	this.addSubview(button);
	DrawerInfo info = new DrawerInfo(button, drawer, colorCode);
	drawerInfos.addElement(info);
    }
    
    public void discard() {
	super.discard();
	worldView.getWorld().removeStateWatcher(worldWatcher);
	worldView = null;
	drawerInfos = null;
	splitToggleButton = null;
    }
    
    private void populate() {
	World world = worldView.getWorld();
	Drawer d
	    = new Drawer(world, world.getPrototypes(), "drawer ct", null, null,
			 false, new Tool[] { Tool.newCharacterTool },
			 new Tool[] { Tool.editAppearanceTool }, this);
	Button b
	    = PlaywriteButton.createFromResource("drawer characters button",
						 false);
	b.setTransparent(true);
	registerDrawer(b, d, 2);
	d = new Drawer(world, world.getSpecialPrototypes(), "drawer spt", null,
		       null, false, null,
		       new Tool[] { Tool.editAppearanceTool }, this);
	b = PlaywriteButton.createFromResource("drawer specials button",
					       false);
	b.setTransparent(true);
	registerDrawer(b, d, 6);
	if (PlaywriteRoot.isAuthoring()) {
	    d = new Drawer(world, (world.getStages
				   ()), "drawer stt", "drawer new stage button", new Drawer.Specializer() {
		public PlaywriteButton createButton() {
		    splitToggleButton
			= (PlaywriteButton.createPWPushButton
			   (0, 0, SidelineView.splitOneIcon.width(),
			    SidelineView.splitOneIcon.height()));
		    splitToggleButton.setImage(SidelineView.splitOneIcon);
		    splitToggleButton.setCommand("SD Toggle Stage Split");
		    splitToggleButton.setTarget(SidelineView.this);
		    splitToggleButton.setToolTipText
			(Resource.getToolTip("SD Toggle Stage Split"));
		    return splitToggleButton;
		}
	    }, true, null, null, null);
	    d.setNewTarget("drawer new stage button", this);
	    b = PlaywriteButton.createFromResource("drawer stages button",
						   false);
	    b.setTransparent(true);
	    registerDrawer(b, d, 1);
	    d = new Drawer(world, (world.getSounds
				   ()), "drawer sot", null, new Drawer.Specializer() {
		public PlaywriteButton createButton() {
		    PlaywriteButton getSoundButton
			= PlaywriteButton.createTextButton("drawer gsl",
							   "drawer gsl",
							   SidelineView.this);
		    return getSoundButton;
		}
	    }, false, null, null, null);
	    b = PlaywriteButton.createFromResource("drawer sounds button",
						   false);
	    b.setTransparent(true);
	    registerDrawer(b, d, 4);
	    d = new Drawer(world, world.getJars(), "drawer jt",
			   "drawer new jar button", null, false, null, null,
			   null);
	    d.setNewTarget("drawer new jar button", this);
	    b = PlaywriteButton.createFromResource("drawer jars button",
						   false);
	    b.setTransparent(true);
	    registerDrawer(b, d, 3);
	    d = new GlobalsDrawer(world, (world.getVariableList
					  ()), "drawer gt", null, new Drawer.Specializer() {
		public PlaywriteButton createButton() {
		    PlaywriteButton newVarButton
			= VariableListView.newVariableTool.makeButton();
		    return newVarButton;
		}
	    }, false, null, null, null);
	    b = PlaywriteButton.createFromResource("drawer globals button",
						   false);
	    b.setTransparent(true);
	    registerDrawer(b, d, 5);
	}
    }
    
    private DrawerInfo drawerForButton(Button btn) {
	Enumeration e = drawerInfos.elements();
	while (e.hasMoreElements()) {
	    DrawerInfo di = (DrawerInfo) e.nextElement();
	    if (di._button == btn)
		return di;
	}
	return null;
    }
    
    private DrawerInfo drawerForWindow(Window win) {
	Enumeration e = drawerInfos.elements();
	while (e.hasMoreElements()) {
	    DrawerInfo di = (DrawerInfo) e.nextElement();
	    if (di._window == win)
		return di;
	}
	return null;
    }
    
    private void worldStateChanged(Object state) {
	Enumeration e = drawerInfos.elements();
	while (e.hasMoreElements()) {
	    DrawerInfo di = (DrawerInfo) e.nextElement();
	    if (di._window != null)
		di._drawer.worldStateChanged(state);
	}
	if (state != World.RUNNING && splitToggleButton != null)
	    splitToggleButton.setEnabled(true);
	e = drawerInfos.elements();
	if (state == World.RUNNING) {
	    while (e.hasMoreElements()) {
		DrawerInfo di = (DrawerInfo) e.nextElement();
		di._button.setEnabled(false);
	    }
	} else if (state == World.STOPPED || state == World.DEBUGGING) {
	    while (e.hasMoreElements()) {
		DrawerInfo di = (DrawerInfo) e.nextElement();
		di._button.setEnabled(true);
	    }
	}
    }
    
    private void getSound(final boolean importAll) {
	PlaywriteSound oldSound = null;
	boolean getNew = true;
	if (!importAll && Selection.selectionSize() == 1) {
	    Selectable item
		= (Selectable) Selection.getSelection().nextElement();
	    if (item instanceof PlaywriteSound
		&& item != PlaywriteSound.nullSound) {
		oldSound = (PlaywriteSound) item;
		PlaywriteDialog dlg
		    = new PlaywriteDialog(Resource.getText("dialog snor"),
					  "sound new", "sound repl",
					  "command c");
		String answer = dlg.getAnswer();
		if (answer == "command c")
		    return;
		if (answer == "sound repl")
		    getNew = false;
	    }
	}
	final PlaywriteSound finalOldSound = oldSound;
	final boolean finalGetNew = getNew;
	FileIO.FileIterator fileHandler = new FileIO.FileIterator() {
	    public void handleFile(String fileName) {
		PlaywriteSound newSound = new PlaywriteSound();
		SystemSound soundData
		    = SystemSound.importSoundFromFile(fileName, newSound,
						      importAll ^ true);
		boolean replace = finalGetNew ^ true;
		PlaywriteSound replaceSound = finalOldSound;
		if (soundData != null) {
		    if (importAll) {
			PlaywriteSound s = null;
			Enumeration e = getWorld().getSounds().getContents();
			while (e.hasMoreElements()) {
			    s = (PlaywriteSound) e.nextElement();
			    if (newSound.getName().equals(s.getName())) {
				replaceSound = s;
				replace = true;
				break;
			    }
			}
		    }
		    if (replace) {
			replaceSound.setSystemSound(soundData);
			Icon.updateIconImages(replaceSound);
		    } else
			newSound.fillInObject(getWorld(), newSound.getName(),
					      soundData);
		}
	    }
	};
	if (importAll)
	    SystemSound.importAllSounds(getWorld(), fileHandler);
	else {
	    FileChooser chooser = SystemSound.showImportSoundDialog(false);
	    if (chooser.file() != null)
		fileHandler.handleFile(chooser.directory() + chooser.file());
	}
    }
    
    void disable() {
	/* empty */
    }
    
    void enable() {
	/* empty */
    }
    
    public void performCommand(String command, Object data) {
	if (DRAWER_BUTTON.equals(command)) {
	    Button selected = (Button) data;
	    DrawerInfo info = drawerForButton(selected);
	    if (info._window != null) {
		info._window.moveToFront();
		if (PlaywriteRoot.getMainRootView().mainWindow()
		    != info._window)
		    info._window.setMainWindow();
	    } else {
		PlaywriteRoot.markBusy();
		info._drawer.performCommand("CR_WIN", info);
		Debug.print("debug.sidelines", "Creating drawer window");
		if (PlaywriteRoot.isAuthoring()
		    && (info._drawer.getContentModel()
			== getWorld().getStages()))
		    splitToggleButton.setImage(getWorld().getWorldView()
						   .numberOfStageViews() == 1
					       ? splitOneIcon : splitTwoIcon);
		PlaywriteRoot.clearBusy();
		info._window.show();
		if (splitToggleButton != null)
		    splitToggleButton.setEnabled(true);
	    }
	} else {
	    if ("drawer new stage button".equals(command)) {
		if (PlaywriteRoot.hasAuthoringLimits()
		    && getWorld().evalLimitForClassReached(Stage.class)) {
		    getWorld().evalLimitDialog(Stage.class);
		    return;
		}
		new Stage(getWorld());
	    } else if ("SD Toggle Stage Split".equals(command)) {
		int newCount;
		if (getWorld().getWorldView().numberOfStageViews() == 1) {
		    newCount = 2;
		    splitToggleButton.setImage(splitTwoIcon);
		    splitToggleButton.setToolTipText
			(Resource.getToolTip("ALT SD Toggle Stage Split"));
		} else {
		    newCount = 1;
		    splitToggleButton.setImage(splitOneIcon);
		    splitToggleButton.setToolTipText
			(Resource.getToolTip("SD Toggle Stage Split"));
		}
		RuleAction action = new SplitStageAction(newCount, getWorld());
		getWorld().doManualAction(action);
	    } else if ("drawer gsl".equals(command)) {
		boolean rightClick
		    = (((PlaywriteButton) data).getLastModifiers() & 0x4) != 0;
		getSound(rightClick && PlaywriteRoot.isProfessional());
	    } else if ("drawer new jar button".equals(command))
		Jar.createJarWithDefaultName(getWorld());
	    else
		Debug.print("debug.commands", "Unknown command: ", command);
	    getWorld().setModified(true);
	}
    }
    
    public void windowDidHide(Window window) {
	Debug.print("debug.sidelines", "Hiding drawer window");
	DrawerInfo info = drawerForWindow(window);
	info._drawer.performCommand("DS_WIN", info._window);
	info._window = null;
	if (getWorld().getState() != World.RUNNING)
	    info._button.setEnabled(true);
	info._button.setState(false);
	Debug.print("debug.sidelines", "Drawer button re-enabled");
    }
    
    public boolean windowWillHide(Window window) {
	return true;
    }
    
    public void windowDidBecomeMain(Window window) {
	/* empty */
    }
    
    public void windowDidResignMain(Window window) {
	/* empty */
    }
    
    public void windowDidShow(Window window) {
	/* empty */
    }
    
    public boolean windowWillShow(Window window) {
	return true;
    }
    
    public void windowWillSizeBy(Window window, Size size) {
	/* empty */
    }
    
    public void update(Observable sideline, Object character) {
	this.setDirty(true);
    }
    
    public View sourceView(ToolSession session) {
	return this;
    }
    
    public void toolWasAccepted(ToolSession session) {
	if (session.isShiftKeyDown())
	    session.resetSession();
    }
    
    public void toolWasRejected(ToolSession session) {
	if (session.isShiftKeyDown())
	    session.resetSession();
    }
    
    public void sessionEnded(ToolSession session) {
	/* empty */
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool toolType = session.toolType();
	Point dest = session.destinationMousePoint();
	PlaywriteView targetView = (PlaywriteView) session.destinationView();
	if (toolType == Tool.newCharacterTool) {
	    XYContainer chars = (XYContainer) targetView.getModelObject();
	    CharacterPrototype proto = getWorld().makeNewPrototype();
	    if (proto != null)
		chars.moveTo(proto, dest.x, dest.y);
	    else
		return false;
	} else if (toolType == Tool.editAppearanceTool) {
	    CharacterPrototype proto
		= (CharacterPrototype) targetView.getModelObject();
	    proto.editAppearance();
	} else
	    return false;
	getWorld().setModified(true);
	return true;
    }
}
