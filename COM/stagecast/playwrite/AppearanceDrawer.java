/* AppearanceDrawer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class AppearanceDrawer extends XYContainer
    implements ResourceIDs.CommandIDs, ResourceIDs.PicturePainterIDs
{
    private CocoaCharacter _character;
    private Appearance _selectedItem;
    private Hashtable _currentAppearanceMap = new Hashtable(19);
    private Hashtable _iconModelTable = new Hashtable(19);
    private PlaywriteDialog _dialog = null;
    private AppearanceEditor _editor;
    
    private static class AppearanceDrawerIconModel implements IconModel
    {
	private Appearance _appearance;
	private ViewManager _viewManager;
	private Image _iconImage = null;
	
	public AppearanceDrawerIconModel(Appearance appearance) {
	    _appearance = appearance;
	    _viewManager = new ViewManager(this);
	}
	
	public Image getIconImage() {
	    return (_iconImage != null ? _iconImage
		    : _appearance.getIconImage());
	}
	
	public Rect getIconImageRect() {
	    return Appearance.ICON_IMAGE_RECT;
	}
	
	public void setIconImage(Image iconImage) {
	    if (_iconImage != null && _iconImage instanceof Bitmap)
		((Bitmap) _iconImage).flush();
	    _iconImage = iconImage;
	    Icon.updateIconImages(this);
	}
	
	public String getIconName() {
	    return _appearance.getIconName();
	}
	
	public void setIconName(String newName) {
	    throw new PlaywriteInternalError
		      ("Icon names must be updated directly with the appearance.");
	}
	
	public boolean hasIconViews() {
	    return true;
	}
	
	public ViewManager getIconViewManager() {
	    return _viewManager;
	}
    }
    
    AppearanceDrawer(CocoaCharacter character,
		     AppearanceEditor appearanceEditor, World world) {
	super(world, Appearance.class, 1, true);
	_editor = appearanceEditor;
	_character = character;
	Enumeration appearances = character.getAppearances();
	while (appearances.hasMoreElements()) {
	    Appearance appearance = (Appearance) appearances.nextElement();
	    if (appearance.isHighlightedForAppearanceDrawerSelection())
		appearance.unhighlightForAppearanceDrawerSelection();
	    Point position = appearance.getDrawerPosition();
	    if (position.equals(Appearance.INVALID_POSITION) == false)
		add(appearance, position.x, position.y, false);
	    else
		add(appearance, false);
	}
	initializeCurrentAppearanceMap();
	setSelectedItem(character.getCurrentAppearance());
    }
    
    private void initializeCurrentAppearanceMap() {
	_currentAppearanceMap = new Hashtable();
	Enumeration elements = this.getContents();
	while (elements.hasMoreElements())
	    _currentAppearanceMap.put(elements.nextElement(), new Vector());
	elements = _character.getPrototype().getInstances();
	while (elements.hasMoreElements()) {
	    CocoaCharacter tempCharacter
		= (CocoaCharacter) elements.nextElement();
	    Vector characterList
		= (Vector) _currentAppearanceMap
			       .get(tempCharacter.getCurrentAppearance());
	    if (characterList != null)
		characterList.addElement(tempCharacter);
	}
    }
    
    IconModel getAppearanceDrawerIconModel(Appearance appearance) {
	IconModel model = (IconModel) _iconModelTable.get(appearance);
	if (model == null) {
	    model = new AppearanceDrawerIconModel(appearance);
	    _iconModelTable.put(appearance, model);
	}
	return model;
    }
    
    CocoaCharacter getCharacter() {
	return _character;
    }
    
    void destroy() {
	Enumeration e = this.getContents();
	while (e.hasMoreElements()) {
	    Contained c = (Contained) e.nextElement();
	    this.notifyViews(c, 2);
	    c.setContainer(null);
	}
	this.clearOut();
	_character = null;
	_editor = null;
	_selectedItem = null;
	_currentAppearanceMap.clear();
	_iconModelTable.clear();
	_dialog = null;
    }
    
    boolean allowAdd(Contained item) {
	return (item instanceof Appearance
		&& (_character.getPrototype()
			.getAppearanceNamed(((Appearance) item).getName())
		    == null));
    }
    
    void add(Contained item, boolean select) {
	add(item, -1, -1, select);
    }
    
    public void add(Contained item, int x, int y) {
	add(item, x, y, true);
    }
    
    private void add(Contained item, int x, int y, boolean select) {
	super.add(item, x, y);
	_character.add((Appearance) item);
	if (select == true)
	    setSelectedItem((Appearance) item, true);
    }
    
    public void moveTo(Contained item, int x, int y) {
	((Appearance) item).setDrawerPosition(new Point(x, y));
	super.moveTo(item, x, y);
    }
    
    Contained copyForAdd(Contained obj) {
	if (obj instanceof Appearance)
	    return ((Contained)
		    ((Appearance) obj).copy(this.getWorld(),
					    _character.getPrototype()));
	throw new PlaywriteInternalError("Can't copy " + obj + " to " + this);
    }
    
    public boolean allowRemove(Contained item) {
	return super.allowRemove(item) && this.size() > 1;
    }
    
    public void remove(Contained item) {
	super.remove(item);
	if (_selectedItem == item) {
	    Appearance selection
		= (Appearance) this.getContents().nextElement();
	    setSelectedItem(selection, false);
	}
    }
    
    Appearance getSelectedItem() {
	return _selectedItem;
    }
    
    boolean setSelectedItem(Appearance item) {
	return setSelectedItem(item, true);
    }
    
    private boolean setSelectedItem(Appearance item,
				    boolean saveChangesToPrevSelection) {
	boolean result = true;
	if (item == _selectedItem)
	    return result;
	if (this.contains(item) == false)
	    throw new IllegalArgumentException
		      ("Only appearances currently in our drawer can be selected");
	if (saveChangesToPrevSelection && _editor.isShowingEditorUI()
	    && _editor.getPaintField().isAppearanceDirty())
	    result = saveChangesToSelectedItemUnlessAborted();
	if (result == true) {
	    if (_selectedItem != null) {
		if (_selectedItem.isHighlightedForAppearanceDrawerSelection()
		    == true)
		    item.highlightForAppearanceDrawerSelection();
		_selectedItem.unhighlightForAppearanceDrawerSelection();
	    }
	    _selectedItem = item;
	    _editor.appearanceWasSelected(item);
	    if (_editor.isShowingEditorUI())
		_editor.getPaintField().setAppearance
		    ((Appearance) _selectedItem.copy(),
		     getAppearanceDrawerIconModel(_selectedItem));
	}
	return result;
    }
    
    boolean prepareForClose() {
	if (saveChangesToSelectedItemUnlessAborted() == false)
	    return false;
	Enumeration elements = this.getContents();
	while (elements.hasMoreElements()) {
	    Appearance ourItem = (Appearance) elements.nextElement();
	    if (ourItem.isBlank() == true) {
		_editor.moveToFront();
		setSelectedItem(ourItem);
		PlaywriteSystem.beep();
		_dialog = new PlaywriteDialog("Picture Painter Alert 1",
					      "command ok");
		_dialog.getAnswer();
		_dialog = null;
		return false;
	    }
	}
	if (_editor.isEnabled())
	    _character.setCurrentAppearance(_selectedItem);
	return true;
    }
    
    boolean saveChangesToSelectedItemUnlessAborted() {
	boolean result = true;
	if (_editor.isShowingEditorUI()) {
	    _editor.getPaintField().prepareForPossibleSave();
	    if (_editor.getPaintField().isAppearanceDirty() == false)
		return true;
	    boolean recompileRules
		= (_editor.getPaintField().hasShapeChanged()
		   || _editor.getPaintField().hasHomeSquareChanged());
	    int ruleCount
		= _character.getWorld().countRulesReferringTo(_selectedItem);
	    if (recompileRules && ruleCount > 0) {
		_dialog = new PlaywriteDialog("Picture Painter Alert 2",
					      "command u", "command c");
		String answer = _dialog.getAnswer();
		_dialog = null;
		if (answer.equals("command u"))
		    saveChangesToSelectedItem(true);
		else
		    result = false;
	    } else
		saveChangesToSelectedItem(false);
	} else
	    result = true;
	return result;
    }
    
    private void saveChangesToSelectedItem(boolean recompileRules) {
	if (_editor.isShowingEditorUI()
	    && _editor.getPaintField().isAppearanceDirty()) {
	    _editor.getPaintField().saveChanges();
	    Appearance newAppearance = _editor.getPaintField().getAppearance();
	    newAppearance.setName(_selectedItem.getName());
	    updateAppearance(_selectedItem, newAppearance, recompileRules);
	}
    }
    
    void updateAppearance(final Appearance target, Appearance source,
			  final boolean recompileRules) {
	if (_currentAppearanceMap.get(target) != null) {
	    Enumeration characters
		= ((Vector) _currentAppearanceMap.get(target)).elements();
	    while (characters.hasMoreElements()) {
		CocoaCharacter tempCharacter
		    = (CocoaCharacter) characters.nextElement();
		if (tempCharacter.getContainer() instanceof Board) {
		    Board board = (Board) tempCharacter.getContainer();
		    board.changeAppearance(tempCharacter, target, source);
		    target.undisplayItemsOn(tempCharacter);
		    source.displayItemsOn(tempCharacter);
		}
	    }
	}
	target.copyDataFrom(source, new Hashtable(50));
	target.appearanceChanged();
	World world = target.getWorld();
	int count = world.countRulesReferringTo(target);
	if (count > 0) {
	    final ProgressDialog progressDialog
		= new ProgressDialog(250, 50, Resource.getText("PP update"));
	    progressDialog.setTotalCount(count);
	    progressDialog.show();
	    RuleListItem.IterationProcessor ruleUpdater = new RuleListItem.IterationProcessor() {
		public Object processItem(RuleListItem item,
					  Object lastValue) {
		    if (item.refersTo(target) && item instanceof Rule) {
			Rule rule = (Rule) item;
			rule.updateViews();
			if (recompileRules)
			    rule.recompile();
			progressDialog.incrementTotalDone(1);
		    }
		    return null;
		}
		
		public boolean done(Object lastValue) {
		    return false;
		}
	    };
	    world.iterateOverRules(ruleUpdater, null);
	    progressDialog.hide();
	}
    }
    
    public void onPainterBecameMainWindow() {
	Selection.unselectAll();
	_selectedItem.highlightForAppearanceDrawerSelection();
	initializeCurrentAppearanceMap();
    }
    
    public void onPainterResignedMainWindow() {
	Target target = new Target() {
	    public void performCommand(String command, Object data) {
		if (_dialog == null)
		    saveChangesToSelectedItemUnlessAborted();
	    }
	};
	Application.application().performCommandLater(target, null, null);
	_selectedItem.unhighlightForAppearanceDrawerSelection();
    }
}
