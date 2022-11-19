/* CharacterPrototype - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class CharacterPrototype extends CocoaCharacter
    implements Bindable, Copyable, Debug.Constants, Deletable, Externalizable,
	       ModificationAware, Movable, Proxy, ReferencedObject,
	       ResourceIDs.CommandIDs, ResourceIDs.DialogIDs,
	       ResourceIDs.CharacterVariableIDs, ResourceIDs.NameGeneratorIDs,
	       ResourceIDs.InstanceNameIDs, ResourceIDs.ToolIDs, Selectable
{
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108756526386L;
    private World _world = null;
    private VariableList _variableList = null;
    private Vector _appearances = null;
    private long _instanceCounter = 0L;
    private int _appearanceCounter = 0;
    private boolean _locked = false;
    private String _password = null;
    private UniqueID _uniqueID;
    private UniqueID _uniqueParentID;
    private boolean _proxyFlag = false;
    private transient Vector _instances;
    private transient Vector _pseudoInstances;
    private transient Vector _jars = null;
    private transient boolean _inited = false;
    private transient boolean _modified = false;
    private transient Subroutine _mainSubroutine;
    private transient int _variableAreaHeight = 0;
    transient AppearanceVariable appearanceVar;
    transient Variable horizVar;
    transient Variable vertVar;
    transient PopupVariable stageVar;
    
    private static interface CharacterUpdater
    {
	public void updateCharacter(CocoaCharacter cocoacharacter);
    }
    
    public CharacterPrototype(World world, String name, Appearance appearance,
			      boolean isVisible) {
	this();
	fillInObject(world, name, appearance);
	setVisibility(isVisible);
    }
    
    public CharacterPrototype(World world, String name,
			      Appearance appearance) {
	this(world, name, appearance, true);
    }
    
    public CharacterPrototype() {
	_uniqueID = new UniqueID();
	_uniqueParentID = null;
	_instances = new Vector(1);
	_pseudoInstances = new Vector(1);
	_variableList = new VariableList(this);
	_appearances = new Vector(1);
	this.setPrototype(this);
	_mainSubroutine = Subroutine.createNormalSubroutine();
	_mainSubroutine.setOwner(this);
    }
    
    public void init(World w) {
	/* empty */
    }
    
    public void fillInObject(World world, String name, Appearance appearance) {
	fillInObject(world, name, appearance, true);
    }
    
    private void fillInObject(World world, String name, Appearance appearance,
			      boolean addToWorld) {
	if (world != null) {
	    if (_inited && !Variable.readingCocoaWorld)
		throw new PlaywriteInternalError
			  ("prototype being initialized twice: " + this
			   + " new name = " + name);
	    _world = world;
	    addDefaultSystemVariables();
	    if (appearance != null)
		add(appearance);
	    if (addToWorld)
		_world.add(this);
	    Variable.setSystemValue(CocoaCharacter.SYS_SOUND_VARIABLE_ID, this,
				    PlaywriteSound.nullSound);
	    Variable.setSystemValue(CocoaCharacter.SYS_STAGE_VARIABLE_ID, this,
				    null);
	    Variable.setSystemValue(CocoaCharacter.SYS_HORIZ_VARIABLE_ID, this,
				    new Integer(0));
	    Variable.setSystemValue(CocoaCharacter.SYS_VERT_VARIABLE_ID, this,
				    new Integer(0));
	    Variable.setSystemValue((CocoaCharacter
				     .SYS_ROLLOVER_ENABLED_VARIABLE_ID),
				    this, Boolean.FALSE);
	    Variable.setSystemValue((CocoaCharacter
				     .SYS_ROLLOVER_APPEARANCE_VARIABLE_ID),
				    this, this.getCurrentAppearance());
	    if (name != null)
		this.setName(name);
	    else
		this.setName(makeDefaultName());
	    _inited = true;
	    _modified = false;
	}
    }
    
    boolean initialized() {
	return _inited;
    }
    
    private final void addDefaultSystemVariables() {
	if (findSystemVariable(CocoaCharacter.SYS_APPEARANCE_VARIABLE_ID)
	    == null) {
	    appearanceVar
		= ((AppearanceVariable)
		   Variable.newSystemVariable((CocoaCharacter
					       .SYS_APPEARANCE_VARIABLE_ID),
					      this));
	    horizVar = Variable.newSystemVariable((CocoaCharacter
						   .SYS_HORIZ_VARIABLE_ID),
						  this);
	    vertVar = Variable.newSystemVariable((CocoaCharacter
						  .SYS_VERT_VARIABLE_ID),
						 this);
	    stageVar = ((PopupVariable)
			Variable.newSystemVariable((CocoaCharacter
						    .SYS_STAGE_VARIABLE_ID),
						   this));
	    add(Variable.newSystemVariable(CocoaCharacter.SYS_NAME_VARIABLE_ID,
					   this));
	    add(horizVar);
	    add(vertVar);
	    add(stageVar);
	    add(Variable.newSystemVariable((CocoaCharacter
					    .SYS_SOUND_VARIABLE_ID),
					   this));
	    add(appearanceVar);
	    add(Variable.newSystemVariable((CocoaCharacter
					    .SYS_VISIBLE_VARIABLE_ID),
					   this));
	    add(Variable.newSystemVariable
		(CocoaCharacter.SYS_ROLLOVER_APPEARANCE_VARIABLE_ID, this));
	    add(Variable.newSystemVariable((CocoaCharacter
					    .SYS_ROLLOVER_ENABLED_VARIABLE_ID),
					   this));
	    addSubclassSystemVariables();
	}
    }
    
    public void addSubclassSystemVariables() {
	/* empty */
    }
    
    public boolean isValid() {
	if (_world == null)
	    return false;
	if (_variableList == null)
	    return false;
	if (_appearances == null || _appearances.size() == 0)
	    return false;
	if (_inited == false)
	    return false;
	if (_mainSubroutine == null)
	    return false;
	if (this.getCurrentAppearance() == null)
	    return false;
	return true;
    }
    
    public final World getWorld() {
	return _world;
    }
    
    final Subroutine getMainSubroutine() {
	return _mainSubroutine;
    }
    
    final void setMainSubroutine(Subroutine s) {
	_mainSubroutine = s;
	Subroutine baddy = _mainSubroutine.findBadSubroutine(this);
	if (baddy != null)
	    throw new BadBackpointerError(this, baddy);
    }
    
    public VariableList getVariableList() {
	return _variableList;
    }
    
    private final void setVariableList(VariableList vl) {
	_variableList = vl;
    }
    
    final Enumeration getAppearances() {
	return _appearances.elements();
    }
    
    final boolean hasAppearance(Appearance a) {
	return _appearances.containsIdentical(a);
    }
    
    final Vector getJars() {
	return _jars;
    }
    
    boolean isSpecial() {
	return this.getClass() != CharacterPrototype.class;
    }
    
    final long getNextInstanceCount() {
	return ++_instanceCounter;
    }
    
    final int getVariablesAreaHeight() {
	return _variableAreaHeight;
    }
    
    final void setVariablesAreaHeight(int foo) {
	_variableAreaHeight = foo;
    }
    
    Appearance findAppearanceDisplaying(Variable v) {
	int i = _appearances.size();
	while (i-- > 0) {
	    Appearance foo = (Appearance) _appearances.elementAt(i);
	    if (foo.isDisplaying(v))
		return foo;
	}
	return null;
    }
    
    int countAppearancesDisplaying(Variable v) {
	int count = 0;
	int i = _appearances.size();
	while (i-- > 0) {
	    Appearance foo = (Appearance) _appearances.elementAt(i);
	    if (foo.isDisplaying(v))
		count++;
	}
	return count;
    }
    
    Object getDirectH() {
	return new Integer(0);
    }
    
    Object getDirectV() {
	return new Integer(0);
    }
    
    Object constrainedStage(Object stage) {
	return null;
    }
    
    public boolean isModified() {
	return _modified;
    }
    
    public void setModified(boolean flag) {
	_modified = flag;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	CharacterPrototype newPrototype = null;
	newPrototype = (CharacterPrototype) map.get(this);
	if (newPrototype != null) {
	    if (newPrototype.isProxy() && fullCopy)
		newPrototype.makeReal(this, map);
	    return newPrototype;
	}
	World oldWorld = getWorld();
	World newWorld = (World) map.get(oldWorld);
	if (newWorld != null)
	    newPrototype = newWorld.findCopy(this);
	if (newPrototype == null) {
	    if (fullCopy) {
		try {
		    newPrototype
			= (CharacterPrototype) this.getClass().newInstance();
		    map.put(this, newPrototype);
		} catch (Exception exception) {
		    throw new PlaywriteInternalError
			      ("Couldn't make an instance of "
			       + this.getClass());
		}
		newPrototype._world = newWorld;
		Appearance firstAppearance
		    = new Appearance("bogus",
				     Resource.getImage("CP new character",
						       (new Object[]
							{ new Integer(0) })),
				     new Shape(1, 1, new Point(1, 1)));
		newPrototype.fillInObject((newWorld == null ? oldWorld
					   : newWorld),
					  "", firstAppearance);
		newPrototype.copyDataFrom(this, map);
		firstAppearance.deleteSpecial();
		newPrototype.setName(newWorld == null
				     ? Util.makeCopyName(this.getName())
				     : this.getName());
		if (newWorld != null)
		    newPrototype.setParentID(getID());
		newPrototype.setModified(true);
		newPrototype.updateViewsForAppearanceChange
		    (newPrototype.getCurrentAppearance());
		if (!newPrototype.isVisible())
		    ((XYContainer) newPrototype.getCharContainer())
			.removeViewFor(newPrototype);
	    } else if (newWorld == null)
		newPrototype = this;
	    else
		newPrototype = (CharacterPrototype) makeProxy(map);
	} else if (newPrototype.isProxy() && fullCopy)
	    newPrototype.makeReal(this, map);
	else
	    map.put(this, newPrototype);
	newPrototype.getWorld().setModified(true);
	return newPrototype;
    }
    
    private void copyDataFrom(CharacterPrototype oldPrototype, Hashtable map) {
	copyAppearancesFrom(oldPrototype, map, true);
	copyVariablesFrom(oldPrototype, map, true);
	this.copyVariableValuesFrom(oldPrototype, map, true);
	copyRulesFrom(oldPrototype, map, true);
	copyJarsFrom(oldPrototype, map, false);
    }
    
    private void copyAppearancesFrom(CharacterPrototype oldPrototype,
				     Hashtable map, boolean fullCopy) {
	Vector appearances = oldPrototype._appearances;
	Appearance newCurrentAppearance = null;
	for (int i = 0; i < appearances.size(); i++) {
	    Appearance oldAppearance = (Appearance) appearances.elementAt(i);
	    Appearance newAppearance
		= (Appearance) oldAppearance.copy(map, fullCopy);
	    add(newAppearance);
	    if (oldAppearance == oldPrototype.getCurrentAppearance())
		this.setCurrentAppearance(newAppearance);
	}
    }
    
    private void copyVariablesFrom(CharacterPrototype oldPrototype,
				   Hashtable map, boolean fullCopy) {
	Enumeration variables = oldPrototype.getVariables();
	while (variables.hasMoreElements())
	    ((Variable) variables.nextElement()).copy(map, fullCopy);
    }
    
    private void copyRulesFrom(CharacterPrototype oldPrototype, Hashtable map,
			       boolean fullCopy) {
	_mainSubroutine
	    = (Subroutine) oldPrototype._mainSubroutine.copy(map, fullCopy);
    }
    
    private void copyJarsFrom(CharacterPrototype oldPrototype, Hashtable map,
			      boolean fullCopy) {
	Vector jars = oldPrototype._jars;
	if (map.get(oldPrototype.getWorld()) != null && jars != null) {
	    for (int i = 0; i < jars.size(); i++) {
		Jar oldJar = (Jar) jars.elementAt(i);
		Jar newJar = (Jar) oldJar.copy(map, fullCopy);
		Point loc = oldJar.getLocation(oldPrototype);
		if (newJar.contains(this))
		    newJar.addViewFor(this);
		else
		    newJar.add(this, loc.x, loc.y);
	    }
	}
    }
    
    public Enumeration getLegalValues(PopupVariable v) {
	if (v.isSystemType(CocoaCharacter.SYS_STAGE_VARIABLE_ID)) {
	    Enumeration contents = getWorld().getStages().getContents();
	    Vector vector = new Vector(5);
	    while (contents.hasMoreElements()) {
		Stage stage = (Stage) contents.nextElement();
		if (!stage.isProxy())
		    vector.addElement(stage);
	    }
	    return vector.elements();
	}
	throw new PlaywriteInternalError("Unknown variable: " + v);
    }
    
    public boolean allowDelete() {
	if (PlaywriteRoot.isPlayer())
	    return false;
	PlaywriteDialog dialog
	    = new PlaywriteDialog("dialog rdp", "command d", "command c");
	String answer = dialog.getAnswer();
	if (answer.equals("command c"))
	    return false;
	if (_world.getState() != World.CLOSING
	    && getWorld().ruleRefersTo(this, "REFOBJ pro ID"))
	    return false;
	return true;
    }
    
    public void delete() {
	if (getWorld().getState() == World.CLOSING) {
	    _instances.removeAllElements();
	    _pseudoInstances.removeAllElements();
	} else {
	    deleteAllInstances();
	    deleteAllPseudoInstances();
	}
	_mainSubroutine.removeAllRules();
	while (_jars != null) {
	    if (_jars.isEmpty())
		break;
	    ((Jar) _jars.lastElement()).remove(this);
	}
	while (_appearances != null && !_appearances.isEmpty())
	    ((Appearance) _appearances.lastElement()).delete();
	super.delete(false);
	Variable v;
	while ((v = findUserVariable()) != null)
	    remove(v);
	if (PlaywriteRoot.isAuthoring())
	    PlaywriteRoot.getAppearanceEditorController()
		.destroyEditorFor(this);
	getWorld().referencedObjectWasDeleted();
	_mainSubroutine = null;
	_appearances = null;
	_variableList = null;
	_instances = null;
	_pseudoInstances = null;
	_jars = null;
    }
    
    public void undelete() {
	/* empty */
    }
    
    void deleteAllInstances() {
	while (!_instances.isEmpty()) {
	    CharacterInstance ch
		= (CharacterInstance) _instances.lastElement();
	    if (ch.getCharContainer() == null)
		ch.delete();
	    else
		ch.getCharContainer().deleteCharacter(ch);
	}
    }
    
    void deleteAllPseudoInstances() {
	while (!_pseudoInstances.isEmpty()) {
	    CocoaCharacter ch
		= (CocoaCharacter) _pseudoInstances.lastElement();
	    if (ch.getCharContainer() == null)
		ch.delete();
	    else
		ch.getCharContainer().deleteCharacter(ch);
	}
    }
    
    public final void add(RuleListItem r) {
	addRule(r, false);
    }
    
    public final void add(RuleListItem item, int index) {
	if (_mainSubroutine.numberOfRules() == 0)
	    add(item);
	else
	    _mainSubroutine.add(item, index);
    }
    
    void addAtFront(RuleListItem r) {
	addRule(r, true);
    }
    
    private void addRule(RuleListItem r, boolean atFront) {
	if (_mainSubroutine.numberOfRules() == 0)
	    activateAllInstances();
	if (atFront)
	    _mainSubroutine.add(r, 0);
	else
	    _mainSubroutine.add(r);
    }
    
    public void remove(RuleListItem r) {
	_mainSubroutine.remove(r);
	if (_mainSubroutine.numberOfRules() == 0)
	    deactivateAllInstances();
    }
    
    public final void remove(int index) {
	_mainSubroutine.remove(index);
    }
    
    final Vector getRules() {
	return _mainSubroutine.getRules();
    }
    
    final void setRules(Vector rules) {
	_mainSubroutine.removeAllRules();
	int n = rules.size();
	for (int i = 0; i < n; i++)
	    _mainSubroutine.add((RuleListItem) rules.elementAt(i));
    }
    
    boolean hasRules() {
	return _mainSubroutine.numberOfRules() > 0;
    }
    
    Rule findRuleReferringTo(ReferencedObject obj) {
	return _mainSubroutine.findRuleReferringTo(obj);
    }
    
    int countRulesReferringTo(ReferencedObject obj) {
	return _mainSubroutine.countRulesReferringTo(obj);
    }
    
    Variable addNewVariable(String name) {
	Variable v = new Variable(name, this);
	add(v);
	return v;
    }
    
    public void add(Variable v) {
	_variableList.add(v);
    }
    
    public void add(Variable v, Point pt) {
	_variableList.addAt(v, pt);
    }
    
    public void remove(Variable v) {
	_variableList.remove(v);
    }
    
    public boolean contains(Variable v) {
	return _variableList.hasVariable(v);
    }
    
    public Point getLocation(Variable v) {
	return _variableList.variableLoc(v);
    }
    
    public Variable findUserVariable() {
	Enumeration e = _variableList.elements();
	while (e.hasMoreElements()) {
	    Variable v = (Variable) e.nextElement();
	    if (v.isUserVariable())
		return v;
	}
	return null;
    }
    
    public Variable findSystemVariable(String sysvarID) {
	return _variableList.findSystemVariable(sysvarID);
    }
    
    public Variable findEquivalentVariable(Variable v) {
	return _variableList.findEquivalentVariable(v);
    }
    
    public Variable findCopy(Variable variable) {
	Enumeration variables = _variableList.elements();
	while (variables.hasMoreElements()) {
	    Variable v = (Variable) variables.nextElement();
	    if (v.isCopyOf(variable))
		return v;
	}
	return null;
    }
    
    public boolean isProxy() {
	return _proxyFlag;
    }
    
    public void setProxy(boolean b) {
	_proxyFlag = b;
	if (b)
	    setVisibility(false);
    }
    
    public Object makeProxy(Hashtable map) {
	World newWorld = (World) map.get(getWorld());
	if (newWorld == null)
	    throw new PlaywriteInternalError
		      ("Can't make proxies in intraworld copies: " + this);
	CharacterPrototype newPrototype;
	try {
	    newPrototype = (CharacterPrototype) this.getClass().newInstance();
	    map.put(this, newPrototype);
	} catch (Exception exception) {
	    throw new PlaywriteInternalError("Couldn't make an instance of "
					     + this.getClass());
	}
	newPrototype._world = newWorld;
	Appearance newCurrentAppearance
	    = (Appearance) this.getCurrentAppearance().copy(map, true);
	newPrototype.fillInObject(newWorld, this.getName(),
				  newCurrentAppearance);
	newPrototype.setProxy(true);
	newPrototype.setParentID(getID());
	newPrototype.copyVariablesFrom(this, map, true);
	newPrototype.setVisibility(false);
	newPrototype.copyJarsFrom(this, map, false);
	newPrototype.setModified(true);
	return newPrototype;
    }
    
    public void makeReal(Object source, Hashtable map) {
	CharacterPrototype oldPrototype = (CharacterPrototype) source;
	setProxy(false);
	map.put(oldPrototype, this);
	setVisibility(oldPrototype.isVisible());
	copyDataFrom(oldPrototype, map);
	if (hasRules())
	    activateAllInstances();
	setModified(true);
    }
    
    public void setVisibility(boolean b) {
	if (this.isVisible() != b) {
	    super.setVisibility(b);
	    if (this.getContainer() != null) {
		if (b)
		    ((XYContainer) this.getContainer()).addViewFor(this);
		else
		    ((XYContainer) this.getContainer()).removeViewFor(this);
	    }
	}
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
    
    public boolean isCopyOf(ReferencedObject obj) {
	return (_uniqueID.equals(obj.getParentID())
		|| obj.getID().equals(_uniqueParentID));
    }
    
    private String makeDefaultName() {
	if (isSpecial()) {
	    Object[] params
		= { new Integer(getWorld().getSpecialPrototypes().size()
				+ 1) };
	    return Resource.getTextAndFormat("Generator scpn", params);
	}
	Object[] params = { new Integer(_world.getPrototypeCounter()) };
	return Resource.getTextAndFormat("Generator cpn", params);
    }
    
    public void highlightForSelection() {
	if (this.hasIconViews())
	    this.getIconViewManager().hilite();
    }
    
    public void unhighlightForSelection() {
	if (this.hasIconViews())
	    this.getIconViewManager().unhilite();
    }
    
    public void halo() {
	if (this.hasIconViews())
	    this.getIconViewManager().hilite();
	updateAllInstances(new CharacterUpdater() {
	    public void updateCharacter(CocoaCharacter character) {
		character.halo();
	    }
	});
    }
    
    public void unhalo() {
	if (this.hasIconViews())
	    this.getIconViewManager().unhilite();
	updateAllInstances(new CharacterUpdater() {
	    public void updateCharacter(CocoaCharacter character) {
		character.unhalo();
	    }
	});
    }
    
    public CharacterInstance makeInstance() {
	return new CharacterInstance(this);
    }
    
    void addCharacter(CocoaCharacter ch) {
	addOrRemoveCharacter(ch, true);
    }
    
    protected void remove(CocoaCharacter ch) {
	addOrRemoveCharacter(ch, false);
    }
    
    private void addOrRemoveCharacter(CocoaCharacter ch, boolean add) {
	if (!(ch instanceof CharacterPrototype)) {
	    Vector list;
	    if (ch instanceof CharacterInstance)
		list = _instances;
	    else
		list = _pseudoInstances;
	    if (add)
		list.addElementIfAbsent(ch);
	    else
		list.removeElementIdentical(ch);
	}
    }
    
    public int numberOfInstances() {
	return _instances.size();
    }
    
    public Enumeration getInstances() {
	return _instances.elements();
    }
    
    void removeAllInstances() {
	_instances = new Vector(1);
	_pseudoInstances = new Vector(1);
	_instanceCounter = 0L;
    }
    
    private void activateAllInstances() {
	for (int i = 0; i < _instances.size(); i++) {
	    CharacterInstance instance
		= (CharacterInstance) _instances.elementAt(i);
	    CharacterContainer container = instance.getCharContainer();
	    if (container instanceof Stage) {
		Vector activeChars = ((Stage) container).getActiveCharacters();
		activeChars.addElementIfAbsent(instance);
	    }
	}
    }
    
    private void deactivateAllInstances() {
	for (int i = 0; i < _instances.size(); i++) {
	    CharacterInstance instance
		= (CharacterInstance) _instances.elementAt(i);
	    CharacterContainer container = instance.getCharContainer();
	    if (container instanceof Stage) {
		Vector activeChars = ((Stage) container).getActiveCharacters();
		activeChars.removeElementIdentical(instance);
	    }
	}
    }
    
    private void updateAllInstances(CharacterUpdater updater) {
	Vector characterList = _instances;
	int i = characterList.size();
	while (i-- > 0)
	    updater
		.updateCharacter((CocoaCharacter) characterList.elementAt(i));
	characterList = _pseudoInstances;
	int i_4_ = characterList.size();
	while (i_4_-- > 0)
	    updater.updateCharacter((CocoaCharacter)
				    characterList.elementAt(i_4_));
    }
    
    public void add(Appearance a) {
	if (!_appearances.containsIdentical(a)) {
	    _appearanceCounter = _appearanceCounter + 1;
	    if (a.getName().equals(""))
		a.setGeneratedName(makeUniqueApppearanceName());
	    a.setOwner(this);
	    _appearances.addElement(a);
	    if (this.getCurrentAppearance() == null)
		this.setCurrentAppearance(a);
	    appearanceVar.updateVariableWatchers();
	}
    }
    
    void appearanceIsDeleted(final Appearance a) {
	_appearances.removeElementIdentical(a);
	if (getWorld().getState() != World.CLOSING) {
	    Variable v
		= (Variable.systemVariable
		   (CocoaCharacter.SYS_ROLLOVER_APPEARANCE_VARIABLE_ID, this));
	    if ((Appearance) v.getValue(this) == a)
		v.setValue(this, v.getDefaultValue(this));
	    if (!_appearances.isEmpty()) {
		if (this.getCurrentAppearance() == a)
		    this.setCurrentAppearance((Appearance)
					      _appearances.firstElement());
		appearanceVar.updateVariableWatchers();
		updateAllInstances(new CharacterUpdater() {
		    public void updateCharacter(CocoaCharacter character) {
			if (character.getCurrentAppearance() == a)
			    character.setCurrentAppearance
				(character.getPrototype()
				     .getCurrentAppearance());
		    }
		});
	    }
	}
    }
    
    int numberOfAppearances() {
	return _appearances.size();
    }
    
    Appearance getAppearanceNamed(String s) {
	int aSize = _appearances.size();
	for (int i = 0; i < aSize; i++) {
	    Appearance a = (Appearance) _appearances.elementAt(i);
	    if (a.getName().equalsIgnoreCase(s))
		return a;
	}
	return null;
    }
    
    Appearance findCopy(Appearance appearance) {
	for (int i = 0; i < _appearances.size(); i++) {
	    Appearance a = (Appearance) _appearances.elementAt(i);
	    if (a.isCopyOf(appearance))
		return a;
	}
	return null;
    }
    
    void appearanceChanged(final Appearance appearance) {
	super.appearanceChanged(appearance);
	updateAllInstances(new CharacterUpdater() {
	    public void updateCharacter(CocoaCharacter character) {
		character.appearanceChanged(appearance);
	    }
	});
	setModified(true);
    }
    
    String makeUniqueApppearanceName() {
	String name = null;
	int number = 1;
	do
	    name = Appearance.makeDefaultName(number++);
	while (getAppearanceNamed(name) != null);
	return name;
    }
    
    String makeUniqueApppearanceName(String baseName) {
	String name = baseName;
	int number = 2;
	for (/**/; getAppearanceNamed(name) != null;
	     name = Util.makeNumberedName(baseName, number++)) {
	    /* empty */
	}
	return name;
    }
    
    String makeUniqueCopiedApppearanceName(String baseName) {
	String name = null;
	int number = 2;
	for (name = Util.makeCopyName(baseName);
	     getAppearanceNamed(name) != null;
	     name = Util.makeCopyName(baseName, number++)) {
	    /* empty */
	}
	return name;
    }
    
    public boolean binds(CharacterInstance ch) {
	return this == ch.getPrototype() && !_proxyFlag;
    }
    
    public Appearance makeAppearance(Appearance appearance) {
	return appearance;
    }
    
    public Vector topLevelJars() {
	return _jars;
    }
    
    public void wasAddedToJar(Jar j) {
	if (_jars == null)
	    _jars = new Vector(1);
	_jars.addElementIfAbsent(j);
    }
    
    public void wasRemovedFromJar(Jar j) {
	_jars.removeElementIdentical(j);
	if (_jars.isEmpty())
	    _jars = null;
    }
    
    public void writeExternal(ObjectOutput oo) throws IOException {
	WorldOutStream out = (WorldOutStream) oo;
	ASSERT.isNotNull(_world);
	ASSERT.isNotNull(_variableList);
	ASSERT.isNotNull(_appearances);
	ASSERT.isNotNull(_uniqueID);
	super.writeExternal(out);
	out.writeObject(_world);
	_variableList.writeContents(out);
	out.writeVector(_appearances);
	out.writeLong(_instanceCounter);
	out.writeInt(_appearanceCounter);
	out.writeObject(_uniqueID);
	out.writeObject(_uniqueParentID);
	out.writeBoolean(_proxyFlag);
	out.writeBoolean(_locked);
	out.writeObject(_password);
	getVariableList().writeListValues(out, this);
    }
    
    public void readExternal(ObjectInput oi)
	throws IOException, ClassNotFoundException {
	WorldInStream in = (WorldInStream) oi;
	int version = in.loadVersion(CharacterPrototype.class);
	super.readExternal(in);
	_world = (World) in.readObject();
	addDefaultSystemVariables();
	_variableList.readContents(in);
	_appearances = in.readVector();
	try {
	    fixAppearances();
	} catch (RuntimeException runtimeexception) {
	    throw new RecoverableException("dialog badW", true);
	}
	_instanceCounter = in.readLong();
	_appearanceCounter = in.readInt();
	_uniqueID = (UniqueID) in.readObject();
	_uniqueParentID = (UniqueID) in.readObject();
	switch (version) {
	case 2:
	    setProxy(in.readBoolean());
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 2);
	case 1:
	    /* empty */
	}
	_locked = in.readBoolean();
	_password = (String) in.readObject();
	getVariableList().readListValues(in, this);
	stageVar.setValue(this, null);
	_inited = true;
    }
    
    private void fixAppearances() {
	for (int i = 0; i < _appearances.size(); i++) {
	    Appearance appearance = (Appearance) _appearances.elementAt(i);
	    if (appearance.getOwner() != this) {
		Debug.print("debug.appearance",
			    "setting owner of " + appearance + " to " + this);
		appearance.setOwner(this);
	    }
	}
    }
    
    public String toString() {
	String result = null;
	try {
	    result = this.getName();
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
