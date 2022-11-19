/* CocoaCharacter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public abstract class CocoaCharacter
    implements Debug.Constants, Editable, IconModel, ModelViewInterface, Named,
	       ResourceIDs.CharacterVariableIDs, ResourceIDs.CommandIDs,
	       ResourceIDs.DialogIDs, VariableOwner,
	       PopupVariable.PopupVariableOwner, Verifiable, Visible, Worldly
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108753446194L;
    public static final String SYS_NAME_VARIABLE_ID
	= "Stagecast.CharacterPrototype:name".intern();
    public static final String SYS_APPEARANCE_VARIABLE_ID
	= "Stagecast.CharacterPrototype:appearance".intern();
    public static final String SYS_SOUND_VARIABLE_ID
	= "Stagecast.CharacterPrototype:sound".intern();
    public static final String SYS_HORIZ_VARIABLE_ID
	= "Stagecast.CharacterPrototype:horiz".intern();
    public static final String SYS_VERT_VARIABLE_ID
	= "Stagecast.CharacterPrototype:vert".intern();
    public static final String SYS_STAGE_VARIABLE_ID
	= "Stagecast.CharacterPrototype:stage".intern();
    public static final String SYS_VISIBLE_VARIABLE_ID
	= "Stagecast.CharacterPrototype:visible".intern();
    public static final String SYS_ROLLOVER_APPEARANCE_VARIABLE_ID
	= "Stagecast.CharacterPrototype:rolloverAppearance".intern();
    public static final String SYS_ROLLOVER_ENABLED_VARIABLE_ID
	= "Stagecast.CharacterPrototype:rolloverEnabled".intern();
    public static final String SYS_DRAGGABLE_VARIABLE_ID
	= "Stagecast.CharacterPrototype:draggable".intern();
    public static final String SYS_DRAGPARAMS_VARIABLE_ID
	= "Stagecast.CharacterPrototype:dragParams".intern();
    public static int maxWindows
	= PlaywriteSystem.getApplicationPropertyAsInt("max_windows", 15);
    transient String _debugName;
    private CharacterPrototype _prototype = null;
    private transient CharacterContainer _container = null;
    private transient int _h = 0;
    private transient int _v = 0;
    private transient boolean _visible = true;
    private transient boolean _deletable = true;
    private transient Vector _oldValues = null;
    private transient int _oldX;
    private transient int _oldY;
    private transient int _oldZ;
    private transient CharacterWindow _editor = null;
    private transient Watcher _updateViews = null;
    private transient ViewManager _characterViewManager = null;
    private transient ViewManager _iconViewManager = null;
    private transient boolean _isDeleted = false;
    
    public static class DirectName extends Variable.StringDirectAccessor
    {
	DirectName() {
	    /* empty */
	}
	
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    super.setDirectValue(variable, owner, value);
	    Icon.updateIconNames((CocoaCharacter) owner);
	    if (!PlaywriteRoot.isCustomerBuild())
		((CocoaCharacter) owner)._debugName
		    = ((CocoaCharacter) owner).getName();
	}
	
	public Object mapUnboundDirect(Variable variable,
				       VariableOwner owner) {
	    if (owner instanceof CharacterPrototype)
		return null;
	    return ((CocoaCharacter) owner).getUnboundName();
	}
    }
    
    public static class DirectHoriz extends Variable.NumberDirectAccessor
    {
	DirectHoriz() {
	    /* empty */
	}
	
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    if (value != Variable.UNBOUND)
		((CocoaCharacter) owner).setDirectH((Number) value);
	}
	
	public Object getDirectValue(Variable variable, VariableOwner owner) {
	    return ((CocoaCharacter) owner).getDirectH();
	}
	
	public Object mapUnboundDirect(Variable variable,
				       VariableOwner owner) {
	    throw new PlaywriteInternalError("Unbound H variable");
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    value = super.constrainDirectValue(variable, owner, value);
	    return ((CocoaCharacter) owner).constrainedH(value);
	}
    }
    
    public static class DirectVert extends Variable.NumberDirectAccessor
    {
	DirectVert() {
	    /* empty */
	}
	
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    if (value != Variable.UNBOUND)
		((CocoaCharacter) owner).setDirectV((Number) value);
	}
	
	public Object getDirectValue(Variable variable, VariableOwner owner) {
	    return ((CocoaCharacter) owner).getDirectV();
	}
	
	public Object mapUnboundDirect(Variable variable,
				       VariableOwner owner) {
	    throw new PlaywriteInternalError("Unbound V variable");
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    value = super.constrainDirectValue(variable, owner, value);
	    return ((CocoaCharacter) owner).constrainedV(value);
	}
    }
    
    public static class DirectStage extends Variable.StandardDirectAccessor
    {
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    super.setDirectValue(variable, owner, value);
	    ((CocoaCharacter) owner).setDirectStage(value);
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    if (variable instanceof EnumeratedVariable)
		value = ((EnumeratedVariable) variable).getLegalValue(owner,
								      value);
	    return ((CocoaCharacter) owner).constrainedStage(value);
	}
    }
    
    public static class DirectVisible
	extends BooleanVariable.BooleanDirectAccessor
    {
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    ((CocoaCharacter) owner)
		._setVisibility(((Boolean) value).booleanValue());
	}
	
	public Object getDirectValue(Variable variable, VariableOwner owner) {
	    return (((CocoaCharacter) owner)._visible ? Boolean.TRUE
		    : Boolean.FALSE);
	}
    }
    
    static final void initStatics() {
	boolean proVisible = PlaywriteRoot.isProfessional();
	Variable v = new Variable(SYS_NAME_VARIABLE_ID, "NamVarID",
				  new DirectName(), true);
	v.setDefaultValue("-unnamed-");
	new AppearanceVariable(SYS_APPEARANCE_VARIABLE_ID, "AppVarID");
	new SoundVariable(SYS_SOUND_VARIABLE_ID, "SndVarID");
	v = new Variable(SYS_HORIZ_VARIABLE_ID, "HvarID", new DirectHoriz(),
			 true);
	v.setDefaultValue(new Integer(0));
	v = new Variable(SYS_VERT_VARIABLE_ID, "VvarID", new DirectVert(),
			 true);
	v.setDefaultValue(new Integer(0));
	v = new PopupVariable(SYS_STAGE_VARIABLE_ID, "StgVarID",
			      new DirectStage(), (Vector) null);
	v.setTransient(true);
	v = new BooleanVariable(SYS_VISIBLE_VARIABLE_ID, "visible",
				"not visible", new DirectVisible(), false);
	v.setDefaultValue(Boolean.TRUE);
	v = new AppearanceVariable(SYS_ROLLOVER_APPEARANCE_VARIABLE_ID,
				   "ROAppVarID");
	v.setVisible(proVisible);
	v = new BooleanVariable(SYS_ROLLOVER_ENABLED_VARIABLE_ID, "ROEnVarID",
				"RODiVarID");
	v.setVisible(proVisible);
	v.setDefaultValue(Boolean.FALSE);
	v = new BooleanVariable(SYS_DRAGGABLE_VARIABLE_ID, "draggable",
				"not draggable");
	v.setDefaultValue(Boolean.FALSE);
	v = new Variable(SYS_DRAGPARAMS_VARIABLE_ID, "drag params", true);
    }
    
    CocoaCharacter() {
	/* empty */
    }
    
    void fillInObject(CharacterPrototype prototype) {
	throw new PlaywriteInternalError("This method should not be used.");
    }
    
    public World getWorld() {
	return _prototype.getWorld();
    }
    
    public final CharacterPrototype getPrototype() {
	return _prototype;
    }
    
    void setPrototype(CharacterPrototype p) {
	if (p == null)
	    throw new PlaywriteInternalError("Setting prototype to null");
	if (_prototype != null)
	    Debug.print("debug.character", "Overwriting a non-null prototype");
	_prototype = p;
	_prototype.addCharacter(this);
	if (!PlaywriteRoot.isCustomerBuild())
	    _debugName = getUnboundName();
    }
    
    final CharacterContainer getCharContainer() {
	return _container;
    }
    
    final int getZ() {
	return _container.getZ(this);
    }
    
    final int getOldX() {
	return _oldX;
    }
    
    final void setOldX(int x) {
	_oldX = x;
    }
    
    final int getOldY() {
	return _oldY;
    }
    
    final void setOldY(int y) {
	_oldY = y;
    }
    
    final int getOldZ() {
	return _oldZ;
    }
    
    final void setOldZ(int z) {
	_oldZ = z;
    }
    
    public CharacterWindow getEditor() {
	return _editor;
    }
    
    public void setEditor(CharacterWindow ed) {
	if (_editor != null && ed != null)
	    throw new PlaywriteInternalError(toString()
					     + " already has an editor");
	_editor = ed;
    }
    
    final boolean isBeingEdited() {
	return _editor != null && _editor.isVisible();
    }
    
    public final int getH() {
	return _h;
    }
    
    public final int getV() {
	return _v;
    }
    
    public final GenericContainer getContainer() {
	return _container;
    }
    
    public void setContainer(GenericContainer c) {
	_container = (CharacterContainer) c;
    }
    
    void setH(int i) {
	_h = i;
    }
    
    void setV(int i) {
	_v = i;
    }
    
    void setLocation(int h, int v) {
	setH(h);
	setV(v);
    }
    
    CocoaCharacter dereference() {
	return this;
    }
    
    public final boolean isDeletable() {
	return _deletable;
    }
    
    public final void setDeletable(boolean b) {
	_deletable = b;
    }
    
    int getLeft(int h) {
	return getCurrentAppearance().left(h);
    }
    
    int getRight(int h) {
	return getCurrentAppearance().right(h);
    }
    
    int getTop(int v) {
	return getCurrentAppearance().top(v);
    }
    
    int getBottom(int v) {
	return getCurrentAppearance().bottom(v);
    }
    
    boolean hasRules() {
	return _prototype.hasRules();
    }
    
    Subroutine getMainSubroutine() {
	return _prototype.getMainSubroutine();
    }
    
    String getUnboundName() {
	return this.getClass().toString() + " of " + getPrototype().toString();
    }
    
    void setDirectH(Number h) {
	setH(h.intValue());
    }
    
    Object getDirectH() {
	return new Integer(getH());
    }
    
    protected Object constrainedH(Object h) {
	CharacterContainer cc = getCharContainer();
	if (!(h instanceof Number))
	    return Variable.ILLEGAL_VALUE;
	if (cc instanceof Board)
	    return new Long(Math.round(((Number) h).doubleValue()));
	return h;
    }
    
    void setDirectV(Number h) {
	setV(h.intValue());
    }
    
    Object getDirectV() {
	return new Integer(getV());
    }
    
    protected Object constrainedV(Object v) {
	CharacterContainer cc = getCharContainer();
	if (!(v instanceof Number))
	    return Variable.ILLEGAL_VALUE;
	if (cc instanceof Board)
	    return new Long(Math.round(((Number) v).doubleValue()));
	return v;
    }
    
    void setDirectStage(Object stage) {
	/* empty */
    }
    
    Object constrainedStage(Object stage) {
	return stage instanceof Stage ? stage : Variable.ILLEGAL_VALUE;
    }
    
    public final boolean isVisible() {
	return _visible;
    }
    
    final boolean isInvisible() {
	return _visible ^ true;
    }
    
    public void setVisibility(boolean b) {
	Variable.setSystemValue(SYS_VISIBLE_VARIABLE_ID, this,
				b ? Boolean.TRUE : Boolean.FALSE);
    }
    
    private void _setVisibility(final boolean b) {
	if (_visible != b) {
	    _visible = b;
	    Target target = new Target() {
		public void performCommand(String command, Object data) {
		    if (b)
			restoreViewSizes();
		    else
			zeroAllViews();
		}
	    };
	    if (getWorld() != null)
		getWorld().addSyncAction(target, "zero views", null);
	}
    }
    
    public boolean isValid() {
	if (_prototype == null)
	    return false;
	if (!(this instanceof CharacterPrototype) && !_prototype.isValid())
	    return false;
	if (getCurrentAppearance() == null)
	    return false;
	return true;
    }
    
    public void edit() {
	if (!PlaywriteRoot.isPlayer()
	    && (Tutorial.getTutorial() == null
		|| !Tutorial.getTutorial().doubleClickDisabled())) {
	    if (getWorld().isLocked())
		PlaywriteDialog.warning("dialog cil");
	    else if (_editor == null || !_editor.isVisible()) {
		if (_editor != null && _editor.isVisible() == false) {
		    Debug.print(true,
				"Invisible Character Window for " + this);
		    Debug.stackTrace();
		    _editor.destroyWindow();
		}
		if (PlaywriteRoot.getMainRootView().internalWindows().size()
		    > maxWindows)
		    PlaywriteDialog.warning(Resource.getTextAndFormat
					    ("dialog max windows",
					     (new Object[]
					      { new Integer(maxWindows) })));
		else
		    setEditor(new CharacterWindow(this));
	    } else
		_editor.moveToFront();
	}
    }
    
    public boolean editAppearance() {
	if (this instanceof GeneralizedCharacter)
	    return false;
	if (getWorld().isLocked()) {
	    PlaywriteDialog.warning("dialog cil");
	    return false;
	}
	ASSERT.isTrue(PlaywriteRoot.isAuthoring());
	if (PlaywriteRoot.getMainRootView().internalWindows().size()
	    > maxWindows) {
	    PlaywriteDialog.warning
		(Resource.getTextAndFormat("dialog max windows",
					   (new Object[]
					    { new Integer(maxWindows) })));
	    return false;
	}
	PlaywriteRoot.getAppearanceEditorController().displayEditorFor(this);
	return true;
    }
    
    public Watcher getAppearanceUpdater() {
	if (_updateViews == null) {
	    final Target target = new Target() {
		public void performCommand(String command, Object data) {
		    if (!isDeleted() && hasCharacterViews())
			getCharacterViewManager().setDirty();
		}
	    };
	    _updateViews = new Watcher() {
		public void update(Object variable, Object value) {
		    getWorld().addSyncAction(target, null, this);
		}
	    };
	}
	return _updateViews;
    }
    
    public Image getIconImage() {
	return getCurrentAppearance().getIconImage();
    }
    
    public final Rect getIconImageRect() {
	return getCurrentAppearance().getIconImageRect();
    }
    
    public void setIconImage(Image image) {
	throw new PlaywriteInternalError("Can't set an icon image");
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
	String haloCommand = "halo";
	String unhaloCommand = "unhalo";
	if (_iconViewManager == null)
	    _iconViewManager = new ViewManager(this);
	return _iconViewManager;
    }
    
    public PlaywriteView createView() {
	return new CharacterView(this);
    }
    
    public PlaywriteView createView(int fixedSize) {
	return new CharacterView(this, fixedSize);
    }
    
    Icon createIcon() {
	return new Icon(this);
    }
    
    public PlaywriteView createIconView() {
	return createIcon();
    }
    
    protected boolean hasCharacterViews() {
	return _characterViewManager != null;
    }
    
    public ViewManager getCharacterViewManager() {
	if (_characterViewManager == null)
	    _characterViewManager = new ViewManager(this);
	return _characterViewManager;
    }
    
    void addView(CharacterView view) {
	getCharacterViewManager().addView(view);
    }
    
    void removeView(CharacterView view) {
	getCharacterViewManager().removeView(view);
    }
    
    void restoreViewSizes() {
	if (hasCharacterViews())
	    getCharacterViewManager()
		.updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    CharacterView characterView = (CharacterView) view;
		    characterView.setDirty(true);
		    characterView.restoreSize();
		    characterView.setDirty(true);
		}
	    }, null);
    }
    
    void updateViewsForAppearanceChange(final Appearance newAppearance) {
	Target target = new Target() {
	    public void performCommand(String s, Object o) {
		updateViewsForAppearanceChange$(newAppearance);
	    }
	};
	getWorld().addSyncAction(target, null, null);
    }
    
    void updateViewsForAppearanceChange$(final Appearance newAppearance) {
	if (hasCharacterViews())
	    getCharacterViewManager()
		.updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    CharacterView characterView = (CharacterView) view;
		    characterView.setBoundsForAppearance(newAppearance);
		    characterView.setDirty(true);
		}
	    }, null);
	Icon.updateIconImages(this);
    }
    
    void zeroAllViews() {
	if (hasCharacterViews())
	    getCharacterViewManager()
		.updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    CharacterView characterView = (CharacterView) view;
		    characterView.setDirty(true);
		    characterView.saveSize();
		    characterView.sizeTo(0, 0);
		}
	    }, null);
    }
    
    void appearanceChanged(Appearance appearance) {
	if (getPrototype().appearanceVar.getValue(this) == appearance)
	    updateViewsForAppearanceChange(appearance);
    }
    
    public Appearance getCurrentAppearance() {
	return (Appearance) getPrototype().appearanceVar.getValue(this);
    }
    
    public void setCurrentAppearance(Appearance appearance) {
	getPrototype().appearanceVar.setValue(this, appearance);
    }
    
    void appearanceChanged(Appearance oldAppearance,
			   Appearance newAppearance) {
	updateViewsForAppearanceChange(newAppearance);
	if (_container != null)
	    _container.changeAppearance(this, oldAppearance, newAppearance);
    }
    
    public void add(Appearance a) {
	_prototype.add(a);
    }
    
    Appearance getAppearanceNamed(String s) {
	return _prototype.getAppearanceNamed(s);
    }
    
    Appearance findSimilarAppearance(Appearance oldAppearance) {
	if (_prototype == oldAppearance.getOwner())
	    return oldAppearance;
	Appearance newAppearance = _prototype.findCopy(oldAppearance);
	if (newAppearance != null)
	    return newAppearance;
	newAppearance = getAppearanceNamed(oldAppearance.getName());
	if (newAppearance != null)
	    return newAppearance;
	return getCurrentAppearance();
    }
    
    boolean hasAppearance(Appearance appearance) {
	return _prototype.hasAppearance(appearance);
    }
    
    Enumeration getAppearances() {
	return _prototype.getAppearances();
    }
    
    void add(RuleListItem r) {
	_prototype.add(r);
    }
    
    void addAtFront(RuleListItem r) {
	_prototype.addAtFront(r);
    }
    
    void remove(RuleListItem r) {
	_prototype.remove(r);
    }
    
    public String getName() {
	Variable nameVar = Variable.systemVariable(SYS_NAME_VARIABLE_ID, this);
	Object value = null;
	if (nameVar != null)
	    value = nameVar.getValue(this);
	if (value == null)
	    return "";
	return value.toString();
    }
    
    public void setName(String name) {
	Variable.systemVariable(SYS_NAME_VARIABLE_ID, this).setValue(this,
								     name);
    }
    
    void variableMoveTo(Variable v, Point loc) {
	_prototype.variableMoveTo(v, loc);
    }
    
    Point variableLoc(Variable v) {
	return _prototype.variableLoc(v);
    }
    
    Variable findSimilarVariable(Variable oldVariable) {
	if (_prototype.contains(oldVariable))
	    return oldVariable;
	Variable newVariable = _prototype.findCopy(oldVariable);
	if (newVariable != null)
	    return newVariable;
	newVariable = _prototype.findEquivalentVariable(oldVariable);
	if (newVariable != null)
	    return newVariable;
	return null;
    }
    
    public void highlightForSelection() {
	if (hasCharacterViews())
	    getCharacterViewManager().hilite();
    }
    
    public void unhighlightForSelection() {
	if (hasCharacterViews())
	    getCharacterViewManager().unhilite();
    }
    
    public abstract void halo();
    
    public abstract void unhalo();
    
    public void moveTo(Object newContainer, int x, int y, int z) {
	CharacterContainer oldContainer = getCharContainer();
	if (oldContainer == newContainer)
	    oldContainer.relocate(this, x, y, z);
	else if (oldContainer == null || isInvisible())
	    Debug.print("debug.character",
			"CocoaCharacter.moveTo: oldContainer==null");
	else {
	    oldContainer.remove(this);
	    ((CharacterContainer) newContainer).add(this, x, y, z);
	}
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	Hashtable map = new Hashtable(50);
	if (getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	return copy(map, true);
    }
    
    public abstract Object copy(Hashtable hashtable, boolean bool);
    
    public Object copy(Hashtable map, boolean fullCopy, String type) {
	Appearance newCurrentAppearance = null;
	CocoaCharacter newCharacter = null;
	if (map != null)
	    newCharacter = (CocoaCharacter) map.get(this);
	if (newCharacter != null)
	    return newCharacter;
	World oldWorld = getWorld();
	World newWorld = map == null ? null : (World) map.get(oldWorld);
	CharacterPrototype oldPrototype = getPrototype();
	CharacterPrototype newPrototype
	    = map == null ? null : (CharacterPrototype) map.get(oldPrototype);
	if (newPrototype == null) {
	    if (newWorld == null || map == null)
		newPrototype = oldPrototype;
	    else
		newPrototype
		    = (CharacterPrototype) oldPrototype.copy(map, fullCopy);
	} else if (newPrototype.isProxy() && fullCopy)
	    newPrototype.makeReal(oldPrototype, map);
	try {
	    newCharacter = (CocoaCharacter) this.getClass().newInstance();
	    if (map != null)
		map.put(this, newCharacter);
	} catch (Exception exception) {
	    throw new PlaywriteInternalError("Couldn't make an instance of "
					     + this.getClass());
	}
	newCharacter.fillInObject(newPrototype);
	String newName = newCharacter.getName();
	newCharacter.copyDataFrom(this, map, fullCopy);
	newCharacter.setName(newName);
	if (map != null)
	    newCurrentAppearance
		= (Appearance) map.get(getCurrentAppearance());
	if (newCurrentAppearance == null)
	    newCurrentAppearance
		= newPrototype.findSimilarAppearance(getCurrentAppearance());
	if (map != null)
	    map.put(getCurrentAppearance(), newCurrentAppearance);
	newCharacter.setCurrentAppearance(newCurrentAppearance);
	return newCharacter;
    }
    
    void copyDataFrom(CocoaCharacter oldCh, Hashtable map, boolean fullCopy) {
	copyVariableValuesFrom(oldCh, map, fullCopy);
    }
    
    public void copyVariableValuesFrom(CocoaCharacter oldCh,
				       boolean fullCopy) {
	if (getWorld() == oldCh.getWorld())
	    copyVariableValuesFrom(oldCh, null, fullCopy);
	else {
	    Hashtable map = new Hashtable(50);
	    map.put(oldCh.getWorld(), getWorld());
	    copyVariableValuesFrom(oldCh, map, fullCopy);
	}
    }
    
    public void copyVariableValuesFrom(CocoaCharacter oldCh, Hashtable map,
				       boolean fullCopy) {
	World newWorld
	    = map == null ? null : (World) map.get(oldCh.getWorld());
	Enumeration variables = getVariables();
	while (variables.hasMoreElements()) {
	    Variable newVariable = (Variable) variables.nextElement();
	    Variable oldVariable = oldCh.findSimilarVariable(newVariable);
	    if (oldVariable != null
		&& !oldVariable.isSystemType(SYS_STAGE_VARIABLE_ID))
		newVariable.copyValue(oldVariable.getActualValue(oldCh), this,
				      map, false);
	}
    }
    
    public boolean allowDelete() {
	return this instanceof Deletable;
    }
    
    public void delete() {
	delete(getWorld().getState() != World.CLOSING);
    }
    
    protected void delete(boolean undoable) {
	if (_container != null)
	    throw new PlaywriteInternalError
		      (toString() + " being deleted still has a container");
	if (undoable) {
	    _oldValues = new Vector(10);
	    Enumeration vars = getVariables();
	    while (vars.hasMoreElements()) {
		Variable v = (Variable) vars.nextElement();
		Object val = v.getActualValue(this);
		if (val != null) {
		    _oldValues.addElement(v);
		    _oldValues.addElement(val);
		}
	    }
	}
	if (getEditor() != null) {
	    PlaywriteRoot.app().performCommandAndWait(getEditor(),
						      PlaywriteWindow.CLOSE,
						      this);
	    setEditor(null);
	}
	Target target = new Target() {
	    public void performCommand(String c, Object o) {
		CocoaCharacter.this.discardViews();
	    }
	};
	getWorld().addSyncAction(target, null, null);
	if (getWorld().getState() != World.CLOSING)
	    Variable.deleteOwner(this);
	_prototype.remove(this);
	_isDeleted = true;
    }
    
    private void discardViews() {
	getVariableList().destroyViewsOf(this);
	if (getCurrentAppearance() != null)
	    getCurrentAppearance().undisplayItemsOn(this);
	if (_characterViewManager != null) {
	    _characterViewManager.updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    ((PlaywriteView) view).discard();
		}
	    }, null);
	    _characterViewManager.delete();
	}
	if (_iconViewManager != null) {
	    _iconViewManager.updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    ((Icon) view).discard();
		}
	    }, null);
	    _iconViewManager.delete();
	}
	_updateViews = null;
	_characterViewManager = null;
	_iconViewManager = null;
	_editor = null;
    }
    
    public boolean isDeleted() {
	return _isDeleted;
    }
    
    public void undelete() {
	if (_oldValues != null) {
	    for (int i = 0; i < _oldValues.size(); i += 2) {
		Variable v = (Variable) _oldValues.elementAt(i);
		Object val = _oldValues.elementAt(i + 1);
		v.setValue(this, val);
	    }
	    _oldValues = null;
	}
	_prototype.addCharacter(this);
	_isDeleted = false;
    }
    
    /**
     * @deprecated
     */
    boolean doDeleteAction() {
	World world = getWorld();
	GeneralizedCharacter deletedCharacterGC = null;
	if (!(this instanceof Deletable))
	    return false;
	if (!isDeletable())
	    return false;
	world.setModified(true);
	if (this instanceof CharacterPrototype) {
	    getCharContainer().deleteCharacter(this);
	    return true;
	}
	if (this instanceof CharacterInstance) {
	    boolean oldVisibility = isVisible();
	    setVisibility(true);
	    deletedCharacterGC
		= new GeneralizedCharacter((CharacterInstance) this);
	    setVisibility(oldVisibility);
	} else if (this instanceof GCAlias)
	    deletedCharacterGC = ((GCAlias) this).findOriginal();
	else
	    throw new PlaywriteInternalError
		      ("Unknown type of character being deleted: " + this);
	world.doManualAction(new DeleteAction(deletedCharacterGC));
	return true;
    }
    
    public Enumeration getVariables() {
	return getVariableList().elements();
    }
    
    public VariableList getVariableList() {
	return getPrototype().getVariableList();
    }
    
    public VariableOwner getVariableListOwner() {
	return getPrototype();
    }
    
    public Enumeration getLegalValues(PopupVariable v) {
	return getPrototype().getLegalValues(v);
    }
    
    public Object legalValueForValue(PopupVariable v, Object value) {
	if (v.isSystemType(SYS_STAGE_VARIABLE_ID)) {
	    if (value instanceof Stage
		&& ((Stage) value).getWorld() == getWorld()
		&& !((Stage) value).isProxy())
		return value;
	    return null;
	}
	return null;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return (this == obj || getPrototype() == obj
		|| obj instanceof Appearance && getCurrentAppearance() == obj);
    }
    
    public boolean affectsDisplay(Variable variable) {
	if (this instanceof CharacterInstance) {
	    if (variable.isSystemType(SYS_APPEARANCE_VARIABLE_ID)
		|| variable.isSystemType(SYS_STAGE_VARIABLE_ID))
		return true;
	    return getCurrentAppearance().isDisplaying(variable);
	}
	return false;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_prototype);
	out.writeObject(_prototype);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_prototype = (CharacterPrototype) in.readObject();
	if (_prototype != this)
	    _prototype.addCharacter(this);
    }
    
    public void readCompleted(WorldInStream wis) {
	/* empty */
    }
    
    static void notifyReadCompleted(WorldInStream wis, Object[] chars) {
	if (chars != null) {
	    for (int i = 0; i < chars.length; i++)
		((CocoaCharacter) chars[i]).readCompleted(wis);
	}
    }
    
    public String toString() {
	String result = null;
	try {
	    result = getName();
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
