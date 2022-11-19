/* Stage - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.Window;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.Operation;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class Stage extends Board
    implements Editable, Externalizable, IconModel, ModelViewInterface, Named,
	       Proxy, ReferencedObject, ResourceIDs.ColorIDs,
	       ResourceIDs.CommandIDs, ResourceIDs.DialogIDs,
	       ResourceIDs.InstanceNameIDs, ResourceIDs.StageIDs, Selectable,
	       Target, VariableOwner
{
    static final int MIN_WIDTH = 1;
    static final int MAX_WIDTH = 500;
    static final int MIN_HEIGHT = 1;
    static final int MAX_HEIGHT = 500;
    static final int DEFAULT_WIDTH = 20;
    static final int DEFAULT_HEIGHT = 14;
    static final float LONG_RATIO = 0.75F;
    static final Rect LONG_RECT = new Rect(0, 0, 24, 40);
    static final float TALL_RATIO = 1.25F;
    static final Rect TALL_RECT = new Rect(0, 0, 40, 24);
    static final Rect STD_RECT = new Rect(0, 0, 32, 32);
    public static final String SYS_BACKGROUND_VARIABLE_ID
	= "Stagecast.Stage:background_image".intern();
    public static final String SYS_BACKGROUND_ALIGNMENT_VARIABLE_ID
	= "Stagecast.Stage:background_align".intern();
    public static final String SYS_BG_COLOR_VARIABLE_ID
	= "Stagecast.Stage:background_color".intern();
    public static final String SYS_WRAP_H_VARIABLE_ID
	= "Stagecast.Stage:wrap_horiz".intern();
    public static final String SYS_WRAP_V_VARIABLE_ID
	= "Stagecast.Stage:wrap_vert".intern();
    public static final String SYS_WIDTH_VARIABLE_ID
	= "Stagecast.Stage:width".intern();
    public static final String SYS_HEIGHT_VARIABLE_ID
	= "Stagecast.Stage:height".intern();
    public static final String SYS_SQUARE_SIZE_VARIABLE_ID
	= "Stagecast.Stage:square_size".intern();
    static final StoredToken ALIGN_CENTERED = new StoredToken("STG ABC ID");
    static final StoredToken ALIGN_TILED = new StoredToken("STG ABT ID");
    static final StoredToken ALIGN_SCALED = new StoredToken("STG ABS ID");
    static final String CHANGE_WIDTH = "change width";
    static final String CHANGE_HEIGHT = "change height";
    static final String CHANGE_WRAPPING = "change wrapping";
    static final String CHARS_WERE_DELETED = "chars deleted";
    static final int storeVersion = 6;
    static final long serialVersionUID = -3819410108756854066L;
    private String _name = "";
    private boolean _proxyFlag = false;
    private UniqueID _uniqueID = null;
    private UniqueID _uniqueParentID;
    private Bitmap _iconImage = null;
    private VariableList _stageVariables;
    private Vector _activeCharacters;
    private transient Vector _newCharacters;
    private transient Vector _deletedCharacters;
    private transient Vector _inactiveList;
    private transient Vector _noisyCharacters;
    private transient GenericContainer _container;
    private transient boolean _selected;
    private transient boolean _inExecLoop;
    private transient ViewManager _iconViewManager;
    private transient boolean _iconVisible;
    private transient Vector _deferredActions;
    private transient Window _editWindow;
    private transient Watcher _colorWatcher;
    private transient Watcher _backgroundWatcher;
    private transient Watcher _wrappingWatcher;
    private transient Watcher _bgAlignWatcher;
    private transient Variable _widthVariable;
    private transient Variable _heightVariable;
    private transient Variable _wrapHVariable;
    private transient Variable _wrapVVariable;
    private Point _entrance;
    
    private static class DirectWidth implements VariableDirectAccessor
    {
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    ((Stage) owner).setDirectWidth((Number) value);
	}
	
	public Object getDirectValue(Variable variable, VariableOwner owner) {
	    return ((Stage) owner).getDirectWidth();
	}
	
	public Object mapUnboundDirect(Variable variable,
				       VariableOwner owner) {
	    throw new PlaywriteInternalError("Unbound stage width variable");
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    return ((Stage) owner).constrainedWidth(value);
	}
    }
    
    private static class DirectHeight implements VariableDirectAccessor
    {
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    ((Stage) owner).setDirectHeight((Number) value);
	}
	
	public Object getDirectValue(Variable variable, VariableOwner owner) {
	    return ((Stage) owner).getDirectHeight();
	}
	
	public Object mapUnboundDirect(Variable variable,
				       VariableOwner owner) {
	    throw new PlaywriteInternalError("Unbound stage width variable");
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    return ((Stage) owner).constrainedHeight(value);
	}
    }
    
    private static class DirectSquareSize implements VariableDirectAccessor
    {
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    ((Stage) owner).setDirectSquareSize((Number) value);
	}
	
	public Object getDirectValue(Variable variable, VariableOwner owner) {
	    return ((Stage) owner).getDirectSquareSize();
	}
	
	public Object mapUnboundDirect(Variable variable,
				       VariableOwner owner) {
	    throw new PlaywriteInternalError("Unbound stage width variable");
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    return ((Stage) owner).constrainedSquareSize(value);
	}
    }
    
    static void initStatics() {
	Op.Equal.addOperation(Stage.class, new Operation() {
	    public Object operate(Object left, Object right) {
		Stage a = (Stage) left;
		Stage b = (Stage) right;
		boolean result = a.getName().equals(b.getName());
		return result ? Boolean.TRUE : Boolean.FALSE;
	    }
	});
	Vector bgColors = new Vector(1);
	bgColors.addElement(new ColorValue(Util.defaultBoardColor, "Def CID"));
	new BackgroundVariable(SYS_BACKGROUND_VARIABLE_ID, "SBGVarID");
	new ColorVariable(SYS_BG_COLOR_VARIABLE_ID, "BGColorVarID", bgColors);
	new BooleanVariable(SYS_WRAP_H_VARIABLE_ID, "SWLRTVarID",
			    "SWLRFVarID");
	new BooleanVariable(SYS_WRAP_V_VARIABLE_ID, "SWUDTVarID",
			    "SWUDFVarID");
	Variable v = new Variable(SYS_WIDTH_VARIABLE_ID, "SWVarID",
				  new DirectWidth(), true);
	v.setDefaultValue(new Integer(20));
	new Variable(SYS_HEIGHT_VARIABLE_ID, "SHVarID", new DirectHeight(),
		     true);
	v.setDefaultValue(new Integer(14));
	new Variable(SYS_SQUARE_SIZE_VARIABLE_ID, "SSqSzVarID",
		     new DirectSquareSize(), true);
	v.setDefaultValue(new Integer(32));
	Vector modes = new Vector(3);
	modes.addElement(ALIGN_CENTERED);
	modes.addElement(ALIGN_TILED);
	modes.addElement(ALIGN_SCALED);
	new PopupVariable(SYS_BACKGROUND_ALIGNMENT_VARIABLE_ID, "BGAlignVarID",
			  modes);
    }
    
    public boolean isExecuting() {
	return _inExecLoop;
    }
    
    Stage(int width, int height, World world, int sqSize, boolean isProxy) {
	_activeCharacters = new Vector(10);
	_newCharacters = new Vector(10);
	_deletedCharacters = new Vector(10);
	_inactiveList = new Vector(2);
	_noisyCharacters = new Vector(2);
	_selected = false;
	_inExecLoop = false;
	_iconVisible = true;
	_deferredActions = new Vector(4);
	_editWindow = null;
	_colorWatcher = null;
	_backgroundWatcher = null;
	_wrappingWatcher = null;
	_bgAlignWatcher = null;
	setProxy(isProxy);
	fillInObject(width, height, world, sqSize);
    }
    
    Stage(int width, int height, World world, int sqSize) {
	this(width, height, world, sqSize, false);
    }
    
    Stage(int width, int height, World world) {
	this(width, height, world, 32);
    }
    
    Stage(World world) {
	this(20, 14, world);
    }
    
    public Stage() {
	_activeCharacters = new Vector(10);
	_newCharacters = new Vector(10);
	_deletedCharacters = new Vector(10);
	_inactiveList = new Vector(2);
	_noisyCharacters = new Vector(2);
	_selected = false;
	_inExecLoop = false;
	_iconVisible = true;
	_deferredActions = new Vector(4);
	_editWindow = null;
	_colorWatcher = null;
	_backgroundWatcher = null;
	_wrappingWatcher = null;
	_bgAlignWatcher = null;
    }
    
    void fillInObject(int width, int height, World world, int sqSize) {
	super.fillInObject(width, height, world, sqSize);
	ObjectSieve sieve = world.getObjectSieve();
	if (sieve != null)
	    sieve.creation(this);
	if (_name.equals(""))
	    setName(Resource.getTextAndFormat
		    ("stage name gererator",
		     (new Object[]
		      { new Integer(world.getStageNameCounter()) })));
	_uniqueID = new UniqueID();
	_uniqueParentID = null;
	_stageVariables = new VariableList(this);
	Variable v;
	add(v = Variable.newSystemVariable(SYS_BACKGROUND_VARIABLE_ID, this));
	v.setValue(this, BackgroundImage.noBackground);
	_backgroundWatcher = new EventThreadWatcher(new Watcher() {
	    public void update(Object var, Object data) {
		if (data instanceof BackgroundImage) {
		    Bitmap newImage = ((BackgroundImage) data).getImage();
		    if (newImage != Stage.this.getBackgroundImage()) {
			Stage.this.superSetBackgroundImage(newImage);
			_iconImage = null;
			if (newImage != null && hasIconViews())
			    getIconImage();
			Stage.this.invalidateScreen(true);
			Icon.updateIconImages(Stage.this);
		    }
		}
	    }
	});
	v.addValueWatcher(this, _backgroundWatcher);
	v = Variable.newSystemVariable(SYS_BACKGROUND_ALIGNMENT_VARIABLE_ID,
				       this);
	add(v);
	_bgAlignWatcher = new EventThreadWatcher(new Watcher() {
	    public void update(Object var, Object data) {
		if (data == Stage.ALIGN_CENTERED)
		    Stage.this.superSetBackgroundAlignment(0);
		else if (data == Stage.ALIGN_SCALED)
		    Stage.this.superSetBackgroundAlignment(1);
		else
		    Stage.this.superSetBackgroundAlignment(2);
		Stage.this.invalidateScreen(true);
	    }
	});
	v.addValueWatcher(this, _bgAlignWatcher);
	v.setValue(this, ALIGN_TILED);
	VariableSieve variableSieve = this.getWorld().getVariableSieve();
	if (variableSieve != null)
	    variableSieve.addVariableValueFilter
		(v, ValueMessageMap.getVariableValueFilter());
	add(v = Variable.newSystemVariable(SYS_BG_COLOR_VARIABLE_ID, this));
	v.setValue(this, new ColorValue(Board.defaultColor, "ThisColorID"));
	_colorWatcher = new EventThreadWatcher(new Watcher() {
	    public void update(Object var, Object data) {
		if (data instanceof ColorValue) {
		    Stage.this.superSetBackgroundColor(((ColorValue) data)
							   .getColor());
		    Stage.this.invalidateScreen(true);
		    Icon.updateIconImages(Stage.this);
		}
	    }
	});
	v.addValueWatcher(this, _colorWatcher);
	add(_wrapHVariable = Variable.newSystemVariable(SYS_WRAP_H_VARIABLE_ID,
							this));
	_wrapHVariable.setValue(this, Boolean.TRUE);
	_wrappingWatcher = new Watcher() {
	    public void update(Object var, Object data) {
		Stage.this.getWorld().addSyncAction(Stage.this,
						    "change wrapping", data);
	    }
	};
	_wrapHVariable.addValueWatcher(this, _wrappingWatcher);
	add(_wrapVVariable = Variable.newSystemVariable(SYS_WRAP_V_VARIABLE_ID,
							this));
	_wrapVVariable.setValue(this, Boolean.TRUE);
	_wrapVVariable.addValueWatcher(this, _wrappingWatcher);
	add(_widthVariable = Variable.newSystemVariable(SYS_WIDTH_VARIABLE_ID,
							this));
	add(_heightVariable
	    = Variable.newSystemVariable(SYS_HEIGHT_VARIABLE_ID, this));
	add(v = Variable.newSystemVariable(SYS_SQUARE_SIZE_VARIABLE_ID, this));
	this.getCharacters().ensureCapacity(10);
	this.setGridColor(Color.white);
	world.addStage(this);
	Variable.updateSystemVariableWatchers((CocoaCharacter
					       .SYS_STAGE_VARIABLE_ID),
					      world);
    }
    
    public boolean isValid() {
	if (this.getWorld() == null)
	    return false;
	if (_uniqueID == null)
	    return false;
	return true;
    }
    
    public final String getName() {
	return _name;
    }
    
    public final void setName(String s) {
	_name = s;
	Icon.updateIconNames(this);
    }
    
    final Vector getActiveCharacters() {
	return _activeCharacters;
    }
    
    final void setActiveCharacters(Vector v) {
	_activeCharacters = v;
    }
    
    public final GenericContainer getContainer() {
	return _container;
    }
    
    public final void setContainer(GenericContainer c) {
	_container = c;
    }
    
    final boolean getWrapHorizontal() {
	return ((Boolean) _wrapHVariable.getValue(this)).booleanValue();
    }
    
    final void setWrapHorizontal(boolean b) {
	_wrapHVariable.setValue(this, b ? Boolean.TRUE : Boolean.FALSE);
    }
    
    final boolean getWrapVertical() {
	return ((Boolean) _wrapVVariable.getValue(this)).booleanValue();
    }
    
    final void setWrapVertical(boolean b) {
	_wrapVVariable.setValue(this, b ? Boolean.TRUE : Boolean.FALSE);
    }
    
    final void setSquareSize(int i) {
	Variable.setSystemValue(SYS_SQUARE_SIZE_VARIABLE_ID, this,
				new Long((long) i));
    }
    
    final void setBackgroundImage(Bitmap image) {
	ASSERT.isTrue(false);
    }
    
    private void superSetBackgroundImage(Bitmap image) {
	super.setBackgroundImage(image);
    }
    
    final void setBackgroundColor(Color color) {
	Variable.setSystemValue(SYS_BG_COLOR_VARIABLE_ID, this,
				new ColorValue(color, "ThisColorID"));
    }
    
    private void superSetBackgroundColor(Color color) {
	super.setBackgroundColor(color);
    }
    
    final void setBackgroundAlignment(int align) {
	ASSERT.isTrue(false);
    }
    
    private void superSetBackgroundAlignment(int align) {
	super.setBackgroundAlignment(align);
    }
    
    final void setDirectWidth(Number width) {
	int curWidth = this.numberOfColumns();
	int newWidth = width.intValue();
	int delta = newWidth - curWidth;
	if (delta != 0) {
	    this.getWorld().addSyncAction(this, "change width",
					  new Integer(delta));
	    this.getWorld().screenRefresh();
	}
    }
    
    final Object getDirectWidth() {
	return new Integer(this.numberOfColumns());
    }
    
    final Object constrainedWidth(Object value) {
	if (value instanceof Number) {
	    int val = ((Number) value).intValue();
	    if (val < 1) {
		val = 1;
		warnOfConstraint("dialog stage min width", 1);
	    } else if (val > 500) {
		val = 500;
		warnOfConstraint("dialog stage max width", 500);
	    }
	    return new Integer(val);
	}
	return Variable.ILLEGAL_VALUE;
    }
    
    final void setDirectHeight(Number height) {
	int curHeight = this.numberOfRows();
	int newHeight = height.intValue();
	int delta = newHeight - curHeight;
	if (delta != 0) {
	    this.getWorld().addSyncAction(this, "change height",
					  new Integer(delta));
	    this.getWorld().screenRefresh();
	}
    }
    
    final Object getDirectHeight() {
	return new Integer(this.numberOfRows());
    }
    
    final Object constrainedHeight(Object value) {
	if (value instanceof Number) {
	    int val = ((Number) value).intValue();
	    if (val < 1) {
		val = 1;
		warnOfConstraint("dialog stage min height", 1);
	    } else if (val > 500) {
		val = 500;
		warnOfConstraint("dialog stage max height", 500);
	    }
	    return new Integer(val);
	}
	return Variable.ILLEGAL_VALUE;
    }
    
    final void setDirectSquareSize(Number size) {
	int oldSize = this.getSquareSize();
	int newSize = size.intValue();
	if (oldSize != newSize)
	    super.setSquareSize(newSize);
    }
    
    final Object getDirectSquareSize() {
	return new Integer(this.getSquareSize());
    }
    
    final Object constrainedSquareSize(Object value) {
	if (value instanceof Number) {
	    int val = ((Number) value).intValue();
	    if (val < 4) {
		val = 4;
		warnOfConstraint("dialog stage min sqare sz", 4);
	    } else if (val > 256) {
		val = 256;
		warnOfConstraint("dialog stage max sqare sz", 256);
	    }
	    return new Integer(val);
	}
	return Variable.ILLEGAL_VALUE;
    }
    
    final Point getEntrance() {
	return _entrance;
    }
    
    final void setEntrance(Point p) {
	_entrance = p;
    }
    
    public void highlightForSelection() {
	_selected = true;
	_iconViewManager.hilite();
    }
    
    public void unhighlightForSelection() {
	_selected = false;
	_iconViewManager.unhilite();
    }
    
    public void edit() {
	if (_editWindow != null)
	    _editWindow.moveToFront();
	else if (_editWindow == null && !PlaywriteRoot.isPlayer()) {
	    _editWindow = new VariableWindow(this, this.getWorld());
	    _editWindow.setOwner(new PlaywriteWindow.DefaultOwner() {
		public void windowDidHide(Window w) {
		    _editWindow = null;
		}
	    });
	    Point pt = PlaywriteRoot.getMainRootView().mousePoint();
	    _editWindow.moveTo(pt.x, pt.y);
	    _editWindow.show();
	}
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	Hashtable map = new Hashtable(50);
	if (this.getWorld() != newWorld)
	    map.put(this.getWorld(), newWorld);
	return copy(map, true);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Stage newStage = (Stage) map.get(this);
	if (newStage != null) {
	    if (newStage.isProxy() && fullCopy)
		newStage.makeReal(this, map);
	    return newStage;
	}
	World oldWorld = this.getWorld();
	World newWorld = (World) map.get(oldWorld);
	if (newWorld != null)
	    newStage = newWorld.findCopy(this);
	if (newStage == null) {
	    if (fullCopy) {
		World world = newWorld == null ? oldWorld : newWorld;
		newStage
		    = new Stage(this.numberOfColumns(), this.numberOfRows(),
				world, this.getSquareSize());
		map.put(this, newStage);
		newStage.copyDataFrom(this, map);
		String newName;
		if (newWorld == null)
		    newName = Util.makeCopyName(getName());
		else
		    newName = getName();
		newStage.setName(newName);
		if (newWorld != null)
		    newStage.setParentID(getID());
	    } else if (newWorld == null)
		newStage = this;
	    else
		newStage = (Stage) makeProxy(map);
	} else if (newStage.isProxy() && fullCopy)
	    newStage.makeReal(this, map);
	else
	    map.put(this, newStage);
	newStage.getWorld().setModified(true);
	return newStage;
    }
    
    private void copyDataFrom(Stage oldStage, Hashtable map) {
	World oldWorld = oldStage.getWorld();
	World newWorld = (World) map.get(oldWorld);
	Enumeration variables = oldStage.getVariables();
	while (variables.hasMoreElements())
	    ((Variable) variables.nextElement()).copy(map, true);
	Enumeration variables_10_ = getVariables();
	while (variables_10_.hasMoreElements()) {
	    Variable newVariable = (Variable) variables_10_.nextElement();
	    Variable oldVariable = oldStage.findSimilarVariable(newVariable);
	    if (oldVariable != null)
		newVariable.copyValue(oldVariable.getActualValue(oldStage),
				      this, map, false);
	}
	Variable v = Variable.systemVariable(SYS_BG_COLOR_VARIABLE_ID, this);
	_colorWatcher.update(v, v.getValue(this));
	v = Variable.systemVariable(SYS_BACKGROUND_VARIABLE_ID, this);
	_backgroundWatcher.update(v, v.getValue(this));
	v = Variable.systemVariable(SYS_BACKGROUND_ALIGNMENT_VARIABLE_ID,
				    this);
	_bgAlignWatcher.update(v, v.getValue(this));
	Vector characters = oldStage.getCharacters();
	for (int i = 0; i < characters.size(); i++) {
	    CharacterInstance ch = (CharacterInstance) characters.elementAt(i);
	    CharacterInstance newCh = (CharacterInstance) ch.copy(map, true);
	    newCh.setName(ch.getName());
	    add(newCh, ch.getH(), ch.getV(), -1);
	    if (newCh.hasRules()
		&& !_activeCharacters.containsIdentical(newCh))
		_activeCharacters.addElement(newCh);
	}
    }
    
    Variable findSimilarVariable(Variable oldVariable) {
	if (_stageVariables.hasVariable(oldVariable))
	    return oldVariable;
	Variable newVariable = findCopy(oldVariable);
	if (newVariable != null)
	    return newVariable;
	newVariable = _stageVariables.findEquivalentVariable(oldVariable);
	if (newVariable != null)
	    return newVariable;
	return null;
    }
    
    Variable findCopy(Variable variable) {
	Enumeration variables = _stageVariables.elements();
	while (variables.hasMoreElements()) {
	    Variable v = (Variable) variables.nextElement();
	    if (v.isCopyOf(variable))
		return v;
	}
	return null;
    }
    
    public boolean allowDelete() {
	if (this.getWorld().getNumberOfStagesInWorld() < 2)
	    return false;
	if (this.getCharacters().size() > 0) {
	    PlaywriteDialog dialog
		= new PlaywriteDialog("dialog rds", "command d", "command c");
	    String answer = dialog.getAnswer();
	    if (answer.equals("command c"))
		return false;
	}
	if (this.getWorld().ruleRefersTo(this, "REFOBJ stg ID"))
	    return false;
	return true;
    }
    
    public void delete() {
	World world = this.getWorld();
	Vector characters = this.getCharacters();
	world.removeFromVisibleStages(this);
	if (world.getCurrentStageDRHack() == this)
	    world.setCurrentStageDRHack(null);
	ObjectSieve sieve = world.getObjectSieve();
	if (sieve != null)
	    sieve.destruction(this);
	getVariableList().destroyViewsOf(this);
	if (_wrappingWatcher != null) {
	    Variable.systemVariable(SYS_WRAP_H_VARIABLE_ID, this)
		.removeValueWatcher(this, _wrappingWatcher);
	    Variable.systemVariable(SYS_WRAP_V_VARIABLE_ID, this)
		.removeValueWatcher(this, _wrappingWatcher);
	}
	if (_colorWatcher != null)
	    Variable.systemVariable(SYS_BG_COLOR_VARIABLE_ID, this)
		.removeValueWatcher(this, _colorWatcher);
	if (_backgroundWatcher != null)
	    Variable.systemVariable(SYS_BACKGROUND_VARIABLE_ID, this)
		.removeValueWatcher(this, _backgroundWatcher);
	if (_bgAlignWatcher != null)
	    Variable.systemVariable
		(SYS_BACKGROUND_ALIGNMENT_VARIABLE_ID, this)
		.removeValueWatcher(this, _bgAlignWatcher);
	this.deleteAllCharacters();
	if (world.getState() != World.CLOSING)
	    Variable.deleteOwner(this);
	super.delete();
	world.referencedObjectWasDeleted();
	if (_iconViewManager != null) {
	    _iconViewManager.delete();
	    _iconViewManager = null;
	}
	_container = null;
	_activeCharacters = null;
	_stageVariables = null;
	_newCharacters = null;
	_deletedCharacters = null;
	_noisyCharacters = null;
	_inactiveList = null;
	_colorWatcher = null;
	_backgroundWatcher = null;
	_wrappingWatcher = null;
	_iconViewManager = null;
    }
    
    public void undelete() {
	throw new PlaywriteInternalError("Deleting stages cannot be undone.");
    }
    
    void deleteCharacters(Vector dChars) {
	World world = this.getWorld();
	boolean clockState = world.areClockTicksSuspended();
	world.suspendClockTicks(true);
	boolean recordingState = world.isRecordingSuspended();
	world.suspendRecording(true);
	try {
	    for (int i = 0; i < dChars.size(); i++) {
		CharacterInstance ch = (CharacterInstance) dChars.elementAt(i);
		ch.doDeleteAction();
	    }
	} finally {
	    world.suspendClockTicks(clockState);
	    world.suspendRecording(recordingState);
	}
    }
    
    public void add(CocoaCharacter ch, int h, int v, int z) {
	if (ch instanceof CharacterInstance) {
	    if (this.getCharacters().containsIdentical(ch))
		this.relocate(ch, h, v, z);
	    else {
		super.add(ch, h, v, z);
		if (ch.hasRules()) {
		    if (_inExecLoop)
			_newCharacters.addElement(ch);
		    else
			_activeCharacters.addElement(ch);
		}
	    }
	}
    }
    
    public void remove(CocoaCharacter ch) {
	super.remove(ch);
	_newCharacters.removeElementIdentical(ch);
	if (_inExecLoop)
	    _inactiveList.addElement(ch);
	else
	    _activeCharacters.removeElementIdentical(ch);
    }
    
    void makeVisible(CocoaCharacter ch, CharacterContainer oldContainer) {
	World world = ch.getWorld();
	ASSERT.isInEventThread();
	if (!isViewed()) {
	    if (!(oldContainer instanceof Stage))
		return;
	    Stage oldStage = (Stage) oldContainer;
	    int index = world.getStageViewIndex(oldStage);
	    index++;
	    if (world.getNumberOfVisibleStages() > 1) {
		if (index != world.getNumberOfVisibleStages())
		    index = world.getNumberOfVisibleStages();
		else
		    index = world.getNumberOfVisibleStages() - 1;
	    }
	    index--;
	    world.doManualAction(new SwitchStageAction(this, index), oldStage);
	}
	BoardView stageView = (BoardView) this.getViews().firstElement();
	if (stageView != null) {
	    CharacterView cView = stageView.getViewFor(ch);
	    cView.scrollRectToVisible(cView.localBounds());
	}
    }
    
    public Image getIconImage() {
	Bitmap thumb;
	if (_iconImage == null) {
	    Rect bounds = getIconImageRect();
	    thumb = BitmapManager.createBitmapManager(bounds.width,
						      bounds.height);
	    Bitmap bgImage = this.getBackgroundImage();
	    Graphics gr = thumb.createGraphics();
	    gr.setColor(this.getBackgroundColor());
	    gr.fillRect(bounds);
	    if (bgImage != null)
		bgImage.drawScaled(gr, bounds);
	    else {
		gr.setColor(Color.white);
		gr.drawRect(bounds);
	    }
	    gr.dispose();
	    if (bgImage != null)
		_iconImage = thumb;
	} else
	    thumb = _iconImage;
	return thumb;
    }
    
    public Rect getIconImageRect() {
	float h = (float) this.numberOfRows();
	float v = (float) this.numberOfColumns();
	float ratio = h / v;
	Rect bounds;
	if (ratio < 0.75F)
	    bounds = TALL_RECT;
	else if (ratio > 1.25F)
	    bounds = LONG_RECT;
	else
	    bounds = STD_RECT;
	return bounds;
    }
    
    public void setIconImage(Image image) {
	/* empty */
    }
    
    public String getIconName() {
	return getName();
    }
    
    public void setIconName(String newName) {
	setName(newName);
    }
    
    public boolean hasIconViews() {
	return _iconViewManager != null && _iconViewManager.hasViews();
    }
    
    public ViewManager getIconViewManager() {
	if (_iconViewManager == null)
	    _iconViewManager = new ViewManager(this);
	return _iconViewManager;
    }
    
    public final boolean isProxy() {
	return _proxyFlag;
    }
    
    public final void setProxy(boolean b) {
	_proxyFlag = b;
	if (getContainer() != null)
	    getContainer().update(this);
    }
    
    public Object makeProxy(Hashtable map) {
	World newWorld = (World) map.get(this.getWorld());
	if (newWorld == null)
	    throw new PlaywriteInternalError
		      ("Can't make proxies in intraworld copies: " + this);
	Stage newStage = new Stage(this.numberOfColumns(), this.numberOfRows(),
				   newWorld, this.getSquareSize(), true);
	map.put(this, newStage);
	newStage.setProxy(true);
	newStage.setParentID(getID());
	Enumeration variables = getVariables();
	while (variables.hasMoreElements())
	    ((Variable) variables.nextElement()).copy(map, true);
	newStage.setName(getName());
	Variable.setSystemValue
	    (SYS_BG_COLOR_VARIABLE_ID, newStage,
	     Variable.getSystemValue(SYS_BG_COLOR_VARIABLE_ID, this));
	return newStage;
    }
    
    public void makeReal(Object source, Hashtable map) {
	Stage oldStage = (Stage) source;
	setProxy(false);
	map.put(oldStage, this);
	copyDataFrom(oldStage, map);
    }
    
    public boolean isVisible() {
	return _proxyFlag ^ true;
    }
    
    public void setVisibility(boolean b) {
	if (b && _proxyFlag)
	    throw new PlaywriteInternalError("Proxy stages cannot be visible: "
					     + this);
    }
    
    public final UniqueID getID() {
	return _uniqueID;
    }
    
    public final UniqueID getParentID() {
	return _uniqueParentID;
    }
    
    public final void setParentID(UniqueID id) {
	_uniqueParentID = id;
    }
    
    public boolean isCopyOf(ReferencedObject stage) {
	return (_uniqueID.equals(stage.getParentID())
		|| stage.getID().equals(_uniqueParentID));
    }
    
    void add(Variable variable) {
	_stageVariables.add(variable);
    }
    
    public Enumeration getVariables() {
	return _stageVariables.elements();
    }
    
    public VariableList getVariableList() {
	return _stageVariables;
    }
    
    public VariableOwner getVariableListOwner() {
	return this;
    }
    
    private void warnOfConstraint(String resourceID, int constraint) {
	if (!this.getWorld().isRunning())
	    PlaywriteDialog.warning
		(Resource.getTextAndFormat(resourceID,
					   (new Object[]
					    { new Integer(constraint) })));
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return this == obj;
    }
    
    public boolean affectsDisplay(Variable variable) {
	if (variable.isSystemType(SYS_BACKGROUND_VARIABLE_ID)
	    || variable.isSystemType(SYS_BG_COLOR_VARIABLE_ID)
	    || variable.isSystemType(SYS_SQUARE_SIZE_VARIABLE_ID))
	    return this.getWorld().isStageVisible(this);
	return false;
    }
    
    public PlaywriteView createIconView() {
	Icon icon = new Icon(this);
	_iconViewManager.addView(icon);
	return icon;
    }
    
    void addNoisy(CharacterInstance ch) {
	_noisyCharacters.addElement(ch);
    }
    
    private void clearNoisy(CharacterInstance ch) {
	if (_noisyCharacters.removeElementIdentical(ch))
	    this.getWorld().executeAction
		(new PutAction(new VariableAlias(ch,
						 (Variable.systemVariable
						  ((CocoaCharacter
						    .SYS_SOUND_VARIABLE_ID),
						   ch))),
			       PlaywriteSound.nullSound),
		 this, 0, 0);
    }
    
    boolean noiseOccurred(PlaywriteSound snd) {
	for (int i = 0; i < _noisyCharacters.size(); i++) {
	    CocoaCharacter chNoisy
		= (CocoaCharacter) _noisyCharacters.elementAt(i);
	    if (Variable.getSystemValue(CocoaCharacter.SYS_SOUND_VARIABLE_ID,
					chNoisy)
		== snd)
		return true;
	}
	return false;
    }
    
    void execute(boolean highlightChars) {
	CharacterInstance och = null;
	World world = this.getWorld();
	if (Debug.lookup("debug.consistency")) {
	    boolean error = false;
	    int n = _activeCharacters.size();
	    for (int i = n - 1; i >= 0; i--) {
		CharacterInstance ch
		    = (CharacterInstance) _activeCharacters.elementAt(i);
		if (ch.getContainer() != this) {
		    Debug.print
			("debug.consistency",
			 "Character in stages active list not on stage");
		    try {
			Debug.print("debug.consistency", "Stage: ", getName(),
				    " Character: ", ch.getName(),
				    " Container: ", ch.getContainer());
		    } catch (Throwable throwable) {
			/* empty */
		    }
		    _activeCharacters.removeElementAt(i);
		}
	    }
	    if (error)
		PlaywriteDialog.warning("Consistency error", true);
	}
	_inExecLoop = true;
	int n = _activeCharacters.size();
	for (int i = 0; i < n; i++) {
	    CharacterInstance ch
		= (CharacterInstance) _activeCharacters.elementAt(i);
	    clearNoisy(ch);
	    if (ch.isVisible()) {
		if (highlightChars) {
		    world.addSyncAction(ch, "SAHL", null);
		    world.forceRepaint();
		}
		ch.execute();
		if (ch.getEditor() != null
		    && ch.getEditor().hasStepRuleButton()) {
		    CharacterWindow characterWindow = ch.getEditor();
		    characterWindow.changeStepButtonToPlay();
		    world.suspendForDebug();
		    characterWindow.resetStepButtonAndHide();
		}
		if (highlightChars) {
		    world.addSyncAction(ch, "SAUHL", null);
		    world.forceRepaint();
		}
	    }
	}
	_inExecLoop = false;
	for (int i = 0; i < _deletedCharacters.size(); i++) {
	    CharacterInstance ch
		= (CharacterInstance) _deletedCharacters.elementAt(i);
	    if (ch.getContainer() != null)
		this.deleteCharacter(ch);
	}
	_deletedCharacters.removeAllElements();
	for (int i = 0; i < _inactiveList.size(); i++) {
	    CharacterInstance ch
		= (CharacterInstance) _inactiveList.elementAt(i);
	    _activeCharacters.removeElementIdentical(ch);
	}
	_inactiveList.removeAllElements();
	n = _newCharacters.size();
	if (n > 0) {
	    for (int i = 0; i < n; i++)
		_activeCharacters.addElement(_newCharacters.elementAt(i));
	    _newCharacters.removeAllElements();
	}
	n = _deferredActions.size();
	for (int i = 0; i < n; i++)
	    ((DeferredAction) _deferredActions.elementAt(i)).deferredExecute();
	_deferredActions.removeAllElements();
    }
    
    int validateH(int h) {
	int nColumns = this.numberOfColumns();
	boolean wrap = getWrapHorizontal();
	if (h < 1) {
	    if (wrap)
		h = h % nColumns + nColumns;
	    else
		h = 0;
	} else if (h > nColumns) {
	    if (wrap)
		h %= nColumns;
	    else
		h = 0;
	}
	return h;
    }
    
    int validateV(int v) {
	int nRows = this.numberOfRows();
	boolean wrap = getWrapVertical();
	if (v < 1) {
	    if (wrap)
		v = v % nRows + nRows;
	    else
		v = 0;
	} else if (v > nRows) {
	    if (wrap)
		v %= nRows;
	    else
		v = 0;
	}
	return v;
    }
    
    int validH(int h) {
	int nColumns = this.numberOfColumns();
	boolean wrap = getWrapHorizontal();
	if (h < 1) {
	    if (wrap)
		h = nColumns + h % nColumns;
	    else
		h = 1;
	} else if (h > nColumns) {
	    if (wrap)
		h = (h - 1) % nColumns + 1;
	    else
		h = nColumns;
	}
	return h;
    }
    
    int validV(int v) {
	int nRows = this.numberOfRows();
	boolean wrap = getWrapVertical();
	if (v < 1) {
	    if (wrap)
		v = nRows + v % nRows;
	    else
		v = 1;
	} else if (v > nRows) {
	    if (wrap)
		v = (v - 1) % nRows + 1;
	    else
		v = nRows;
	}
	return v;
    }
    
    boolean onBoard(CocoaCharacter ch, int h, int v) {
	return (h >= 1 && h <= this.numberOfColumns() && v >= 1
		&& v <= this.numberOfRows());
    }
    
    public void addDeferredAction(DeferredAction action) {
	_deferredActions.addElement(action);
    }
    
    void deferredDelete(CharacterInstance ch) {
	_deletedCharacters.addElementIfAbsent(ch);
	_newCharacters.removeElementIdentical(ch);
    }
    
    final boolean isViewed() {
	return this.getWorld().isStageVisible(this);
    }
    
    public int setZ(CocoaCharacter ch, int z) {
	ASSERT.isTrue(ch instanceof CharacterInstance);
	z = super.setZ(ch, z);
	Object[] data = { ch, new Integer(z) };
	if (this.getWorld().getActionSieve() != null
	    && ch instanceof CharacterInstance)
	    this.getWorld().getActionSieve()
		.action(this, "Stagecast.Stage:action.set_z", data);
	return z;
    }
    
    public void performCommand(String command, Object data) {
	int initialCharCount = this.getCharacters().size();
	if ("change width".equals(command)) {
	    Integer oldval = new Integer(this.numberOfColumns());
	    this.growRightBy(((Integer) data).intValue());
	    Integer newval = new Integer(this.numberOfColumns());
	    Icon.updateIconImages(this);
	    if (this.getCharacters().size() < initialCharCount)
		PlaywriteRoot.app().performCommandLater(this, "chars deleted",
							null);
	    _widthVariable.notifyChanged(this, oldval, newval);
	} else if ("change height".equals(command)) {
	    Integer oldval = new Integer(this.numberOfRows());
	    this.growUpBy(((Integer) data).intValue());
	    Integer newval = new Integer(this.numberOfRows());
	    Icon.updateIconImages(this);
	    if (this.getCharacters().size() < initialCharCount)
		PlaywriteRoot.app().performCommandLater(this, "chars deleted",
							null);
	    _heightVariable.notifyChanged(this, oldval, newval);
	} else if ("change wrapping".equals(command)) {
	    Vector characters = this.getCharacters();
	    this.resetContentsArray();
	    for (int i = 0; i < characters.size(); i++) {
		CharacterInstance ch
		    = (CharacterInstance) characters.elementAt(i);
		this.putAppearanceOnBoard(ch, ch.getCurrentAppearance());
	    }
	} else if ("chars deleted".equals(command)) {
	    if (this.getWorld().getState() != World.RUNNING) {
		PlaywriteDialog dialog
		    = new PlaywriteDialog("dialog rdc", "command ok");
		dialog.getAnswer();
	    }
	} else
	    super.performCommand(command, data);
    }
    
    /**
     * @deprecated
     */
    void determineActiveCharacters() {
	Vector chList = this.getCharacters();
	for (int i = 0; i < chList.size(); i++) {
	    CharacterInstance ch = (CharacterInstance) chList.elementAt(i);
	    if (ch.hasRules())
		_activeCharacters.addElement(ch);
	}
    }
    
    void rebuildActiveList(Vector active, boolean checkConsistency) {
	boolean error = false;
	_activeCharacters = new Vector(active.size());
	for (int i = 0; i < active.size(); i++) {
	    CharacterInstance ch = (CharacterInstance) active.elementAt(i);
	    if (checkConsistency && ch.getContainer() != this) {
		Debug.print("debug.consistency",
			    "Character in stages active list not on stage");
		try {
		    Debug.print("debug.consistency", "Stage: ", getName(),
				" Character: ", ch.getName(), " Container: ",
				ch.getContainer());
		} catch (Throwable throwable) {
		    /* empty */
		}
		ch = null;
	    }
	    if (ch != null)
		_activeCharacters.addElement(ch);
	}
	if (error)
	    PlaywriteDialog.warning("Consistency error", true);
    }
    
    void updateActiveCharacterList() {
	Vector chList = this.getCharacters();
	for (int i = 0; i < chList.size(); i++) {
	    CharacterInstance ch = (CharacterInstance) chList.elementAt(i);
	    boolean isInList = _activeCharacters.containsIdentical(ch);
	    if (ch.hasRules() && !isInList)
		_activeCharacters.addElement(ch);
	    else if (!ch.hasRules() && isInList)
		_activeCharacters.removeElementIdentical(ch);
	}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_name);
	ASSERT.isNotNull(_uniqueID);
	ASSERT.isNotNull(_stageVariables);
	super.writeExternal(out);
	out.writeUTF(_name);
	if (_entrance == null) {
	    out.writeInt(-1);
	    out.writeInt(-10);
	} else {
	    out.writeInt(_entrance.x);
	    out.writeInt(_entrance.y);
	}
	out.writeObject(_uniqueID);
	out.writeObject(_uniqueParentID);
	out.writeBoolean(_proxyFlag);
	out.writeObject(_iconImage);
	_stageVariables.writeContents(out);
	getVariableList().writeListValues(out, this);
	((WorldOutStream) out).writeVector(_activeCharacters);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	WorldInStream wis = (WorldInStream) in;
	int version = wis.loadVersion(Stage.class);
	super.readExternal(in);
	fillInObject(this.numberOfColumns(), this.numberOfRows(),
		     this.getWorld(), this.getSquareSize());
	_name = in.readUTF();
	switch (version) {
	case 5:
	case 6:
	    _entrance = new Point();
	    _entrance.x = in.readInt();
	    _entrance.y = in.readInt();
	    if (_entrance.x == -1 || _entrance.y == -1)
		_entrance = null;
	    /* fall through */
	case 3:
	case 4:
	    _uniqueID = (UniqueID) in.readObject();
	    _uniqueParentID = (UniqueID) in.readObject();
	    _proxyFlag = in.readBoolean();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 6);
	case 1:
	case 2:
	    /* empty */
	}
	switch (version) {
	case 6:
	    _iconImage = (Bitmap) in.readObject();
	    /* fall through */
	case 2:
	case 3:
	case 4:
	case 5:
	    _stageVariables.readContents(in);
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 6);
	case 1:
	    /* empty */
	}
	getVariableList().readListValues(in, this);
	if (!(Variable.getSystemValue(SYS_BG_COLOR_VARIABLE_ID, this)
	      instanceof ColorValue))
	    Variable.setSystemValue(SYS_BG_COLOR_VARIABLE_ID, this,
				    new ColorValue(this.getBackgroundColor(),
						   "ThisColorID"));
	Object gotBG
	    = Variable.getSystemValue(SYS_BACKGROUND_VARIABLE_ID, this);
	if (!(gotBG instanceof BackgroundImage)
	    || (gotBG == BackgroundImage.noBackground
		&& this.getBackgroundImage() != null)) {
	    gotBG = (this.getBackgroundImage() == null
		     ? BackgroundImage.noBackground
		     : new BackgroundImage(this.getWorld().getBackgrounds(),
					   "picture",
					   this.getBackgroundImage()));
	    Variable.setSystemValue(SYS_BACKGROUND_VARIABLE_ID, this, gotBG);
	}
	switch (version) {
	case 1:
	case 2:
	case 3:
	    determineActiveCharacters();
	    break;
	case 4:
	case 5:
	case 6: {
	    this.rebuildCharacterList();
	    Vector active = wis.readVector();
	    rebuildActiveList(active,
			      wis.loadVersion(CharacterInstance.class) > 1);
	    break;
	}
	default:
	    throw new UnknownVersionError(this.getClass(), version, 6);
	}
	superSetBackgroundColor
	    (((ColorValue)
	      Variable.getSystemValue(SYS_BG_COLOR_VARIABLE_ID, this))
		 .getColor());
	superSetBackgroundImage
	    (((BackgroundImage)
	      Variable.getSystemValue(SYS_BACKGROUND_VARIABLE_ID, this))
		 .getImage());
	_bgAlignWatcher.update
	    (null,
	     Variable.getSystemValue(SYS_BACKGROUND_ALIGNMENT_VARIABLE_ID,
				     this));
	boolean wrap = getWrapHorizontal();
	setWrapHorizontal(wrap ^ true);
	setWrapHorizontal(wrap);
	wrap = getWrapVertical();
	setWrapVertical(wrap ^ true);
	setWrapVertical(wrap);
	CocoaCharacter
	    .notifyReadCompleted(wis, this.getCharacters().elementArray());
    }
    
    public String toString() {
	return _name;
    }
}
