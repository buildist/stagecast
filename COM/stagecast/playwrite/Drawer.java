/* Drawer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class Drawer implements ResourceIDs.CommandIDs, Target, Worldly
{
    static final String CREATE_WINDOW = "CR_WIN";
    static final String DESTROY_WINDOW = "DS_WIN";
    private World world;
    private Object contentModel;
    private PlaywriteView contentView;
    private ScrollableArea scroller;
    private String title;
    private String newCommand;
    private Target newTarget;
    private PlaywriteButton newButton;
    private Specializer special;
    private boolean allowSpecialWhileRecording;
    private PlaywriteButton specialButton;
    private Tool[] drawerTools;
    private Tool[] contentTools;
    private ToolDestination toolDest;
    private String _id;
    
    static interface Specializer
    {
	public PlaywriteButton createButton();
    }
    
    Drawer(World world, Object contentModel, String titleResourceID,
	   String newCommandID, Specializer special,
	   boolean allowSpecialWhileRecording, Tool[] drawerTools,
	   Tool[] contentTools, ToolDestination toolDest) {
	this.world = world;
	this.contentModel = contentModel;
	contentView = null;
	_id = titleResourceID;
	title = Resource.getText(titleResourceID);
	newCommand = newCommandID;
	newTarget = newCommandID != null ? this : null;
	newButton = null;
	this.special = special;
	specialButton = null;
	this.allowSpecialWhileRecording = allowSpecialWhileRecording;
	this.drawerTools = drawerTools;
	this.contentTools = contentTools;
	this.toolDest = toolDest;
    }
    
    public World getWorld() {
	return world;
    }
    
    Object getContentModel() {
	return contentModel;
    }
    
    PlaywriteView getContentView() {
	return contentView;
    }
    
    PlaywriteButton getNewButton() {
	return newButton;
    }
    
    void setNewTarget(String command, Target target) {
	newCommand = command;
	newTarget = target;
    }
    
    public Rect getWindowBounds() {
	return getWorld().getViewData().getRect(_id);
    }
    
    public void storeWindowBounds(Rect r) {
	getWorld().getViewData().putRect(_id, r);
    }
    
    void presentDrawer(SidelineView.WinWatcher watcher) {
	boolean openedForTheFirstTime = false;
	if (getWindowBounds() == null) {
	    openedForTheFirstTime = true;
	    storeWindowBounds(new Rect(0, 0, 200, 200));
	}
	PlaywriteWindow win = new DrawerWindow(this, getWindowBounds());
	if (newTarget != null) {
	    newButton
		= PlaywriteButton.createTextButton(newCommand, newCommand,
						   newTarget);
	    newButton.setCommand(newCommand);
	    win.getTitleBar().addSubviewRight(newButton);
	}
	if (special != null) {
	    specialButton = special.createButton();
	    win.getTitleBar().addSubviewRight(specialButton);
	}
	setWindowProperties(win);
	Size size = win.contentSize();
	size.height -= 2 * ScrollableArea.SCROLL_ARROW_WIDTH;
	size.width -= 2 * ScrollableArea.SCROLL_ARROW_WIDTH;
	contentView = createContentView(size);
	contentView.setBorder(null);
	contentView.setBackgroundColor(getWorld().getLightColor());
	size.height += 2 * ScrollableArea.SCROLL_ARROW_WIDTH;
	size.width += 2 * ScrollableArea.SCROLL_ARROW_WIDTH;
	if (openedForTheFirstTime) {
	    size.sizeTo((contentView.bounds.width
			 + 2 * ScrollableArea.SCROLL_ARROW_WIDTH),
			(contentView.bounds.height
			 + 2 * ScrollableArea.SCROLL_ARROW_WIDTH));
	    Size winSize
		= win.windowSizeForContentSize(size.width, size.height);
	    Rect newBounds = new Rect(win.bounds.x, win.bounds.y,
				      winSize.width, winSize.height);
	    win.setBounds(newBounds);
	}
	watcher.setWindow(win);
	setContentViewProperties(contentView);
	worldStateChanged(world.getState());
	scroller = new ScrollableArea(size.width, size.height, contentView,
				      true, true);
	scroller.setBuffered(true);
	scroller.setHorizontalScrollAmount(10);
	scroller.setVerticalScrollAmount(10);
	scroller.setBackgroundColor(getWorld().getColor());
	scroller.getScrollView().setTransparent(true);
	scroller.setHorizResizeInstruction(2);
	scroller.setVertResizeInstruction(16);
	scroller.setAllowSmallContentView(false);
	win.addSubview(scroller);
    }
    
    void changeWindowColor(Color color) {
	scroller.changeWindowColor(color, color);
    }
    
    public void removeDrawer(PlaywriteWindow win) {
	contentView = null;
	scroller = null;
	specialButton = null;
    }
    
    void setWindowProperties(PlaywriteWindow window) {
	window.setTitle(title);
    }
    
    PlaywriteView createContentView(Size size) {
	XYContainerView view = new XYContainerView((XYContainer) contentModel,
						   size.width, size.height) {
	    public void setProperties() {
		super.setProperties();
		this.setToolsAreDraggable(false);
		if (contentModel == world.getPrototypes()
		    || contentModel == world.getSpecialPrototypes())
		    this.allowDragInto(CharacterInstance.class, this);
	    }
	    
	    public void setViewProperties(PlaywriteView view_2_) {
		super.setViewProperties(view_2_);
		if (contentTools != null) {
		    for (int i = 0; i < contentTools.length; i++)
			view_2_.allowTool(contentTools[i], toolDest);
		}
	    }
	    
	    public void dragWasAccepted(DragSession session) {
		super.dragWasAccepted(session);
		DrawerWindow win = (DrawerWindow) contentView.window();
		if (session.destination() != session.source()
		    && win.isSticking())
		    win.close();
	    }
	    
	    protected PlaywriteView makeModelView(Contained model) {
		PlaywriteView result = null;
		if (model instanceof Visible && !((Visible) model).isVisible())
		    return null;
		if (model instanceof IconModel) {
		    result = new Icon((IconModel) model) {
			protected PlaywriteTextField createNameTextField
			    (int x, int y, int w, int h) {
			    return new LightTextField(x, y, w, h);
			}
		    };
		    ((Icon) result).setEditable(true);
		    ((Icon) result).setSelectsModel(true);
		} else
		    result = super.makeModelView(model);
		return result;
	    }
	    
	    public boolean dragDropped(DragSession session) {
		if (contentModel == world.getPrototypes()
		    || contentModel == world.getSpecialPrototypes()) {
		    Object obj = this.modelObjectBeingDragged(session);
		    if (obj instanceof CharacterInstance) {
			CharacterPrototype prototype
			    = ((CharacterInstance) obj).getPrototype();
			if (((XYContainer) contentModel).contains(prototype))
			    return false;
			if (world != prototype.getWorld()
			    && world.findCopy(prototype) != null
			    && !world.findCopy(prototype).isProxy())
			    return false;
			((CharacterInstance) obj).setVisibility(true);
			session.setData(prototype.createView());
		    }
		}
		return super.dragDropped(session);
	    }
	    
	    boolean copyToContainer(Object obj, int x, int y) {
		if (contentModel == world.getPrototypes()
		    || contentModel == world.getSpecialPrototypes()) {
		    if (obj instanceof CharacterInstance)
			return copyToContainer(((CharacterInstance) obj)
						   .getPrototype(),
					       x, y);
		    if (obj instanceof CharacterPrototype) {
			CharacterPrototype prototype
			    = (CharacterPrototype) obj;
			if (prototype.getWorld() == world)
			    return super.copyToContainer(prototype, x, y);
			if (world.findCopy(prototype) != null
			    && !world.findCopy(prototype).isProxy())
			    return false;
			prototype = (CharacterPrototype) prototype.copy(world);
			((XYContainer) prototype.getContainer()).add(prototype,
								     x, y);
			return true;
		    }
		    return false;
		}
		return super.copyToContainer(obj, x, y);
	    }
	};
	view.init();
	return view;
    }
    
    void setContentViewProperties(PlaywriteView contentView) {
	if (drawerTools != null) {
	    for (int i = 0; i < drawerTools.length; i++)
		contentView.allowTool(drawerTools[i], toolDest);
	}
    }
    
    boolean addNewModelObject(Object contentModel) {
	XYContainer cm = (XYContainer) contentModel;
	try {
	    Contained newObj = (Contained) cm.getContentType().newInstance();
	    cm.add(newObj);
	    return true;
	} catch (Exception e) {
	    Debug.print(true, "Can't instantiate model object ", cm);
	    Debug.stackTrace(e);
	    return false;
	}
    }
    
    void worldStateChanged(Object state) {
	boolean enableFlag
	    = state == World.STOPPED || state == World.DEBUGGING;
	if (newButton != null)
	    newButton.setEnabled(enableFlag);
	if (specialButton != null)
	    specialButton.setEnabled(enableFlag);
    }
    
    void disable() {
	if (newButton != null)
	    newButton.setEnabled(false);
	if (specialButton != null && !allowSpecialWhileRecording)
	    specialButton.setEnabled(false);
    }
    
    void enable() {
	if (newButton != null)
	    newButton.setEnabled(true);
	if (specialButton != null)
	    specialButton.setEnabled(true);
    }
    
    public void performCommand(String command, Object data) {
	if ("CR_WIN".equals(command))
	    presentDrawer((SidelineView.WinWatcher) data);
	else if ("DS_WIN".equals(command))
	    removeDrawer((PlaywriteWindow) data);
	else if (newCommand.equals(command)) {
	    if (addNewModelObject(getContentModel()))
		getWorld().setModified(true);
	} else
	    throw new PlaywriteInternalError("Illegal command in Drawer: "
					     + command);
    }
}
