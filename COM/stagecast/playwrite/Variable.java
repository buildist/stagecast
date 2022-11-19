/* Variable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.operators.Operation;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class Variable
    implements Cloneable, Contained, Debug.Constants, Externalizable, Named,
	       ReferencedObject, ResourceIDs.CommandIDs, ResourceIDs.DialogIDs,
	       ResourceIDs.InstanceNameIDs, Selectable
{
    static final Object _SYSTEM = new Object();
    public static final boolean VISIBLE = true;
    public static final boolean INVISIBLE = false;
    public static final VariableDirectAccessor STD_ACCESSOR
	= new StandardDirectAccessor();
    public static final VariableDirectAccessor STD_NUMBER_ACCESSOR
	= new NumberDirectAccessor();
    public static final VariableDirectAccessor STD_STRING_ACCESSOR
	= new StringDirectAccessor();
    public static final VariableDirectAccessor STD_FONTSIZE_ACCESSOR
	= new FontSizeDirectAccessor();
    private static final Object LOCAL_NULL = new Object() {
	public String toString() {
	    return "Variable.LOCAL_NULL";
	}
    };
    public static final UnboundValueClass UNBOUND = new UnboundValueClass();
    static final Variable deletedVariable;
    public static final Object ILLEGAL_VALUE;
    static final int storeVersion = 4;
    static final long serialVersionUID = -3819410108756133170L;
    private static Hashtable variablesInWorld;
    private static Hashtable systemVariableRegistry;
    static int deferNotify;
    private static Hashtable cocoaFixTable;
    static boolean readingCocoaWorld;
    private String _name;
    private VariableOwner _listOwner;
    private boolean _isVisible;
    private UniqueID _uniqueID;
    private UniqueID _uniqueParentID;
    private transient String _systemID;
    private transient VariableDirectAccessor _accessor;
    private transient Object _defaultValue;
    private transient boolean _isTransient;
    private transient GenericContainer _container;
    private transient Hashtable _values;
    private transient Hashtable _valueWatchers;
    private transient UpdateManager _watchers;
    
    private static class UnboundValueClass
	implements FirstClassValue, StorageProxied
    {
	public PlaywriteView createView() {
	    return null;
	}
	
	public PlaywriteView createIconView() {
	    return null;
	}
	
	public StorageProxyHelper getStorageProxyHelper() {
	    return BuiltinProxyTable.helper;
	}
	
	public String toString() {
	    return "Variable.UNBOUND";
	}
    }
    
    public static class StandardDirectAccessor
	implements VariableDirectAccessor
    {
	public void setDirectValue(Variable variable, VariableOwner owner,
				   Object value) {
	    if (value != Variable.ILLEGAL_VALUE) {
		if (variable._values == null)
		    variable._values = new Hashtable(53, 0.4F);
		if (value == null)
		    value = Variable.LOCAL_NULL;
		if (value == Variable.UNBOUND)
		    variable._values.remove(owner);
		else
		    variable._values.put(owner, value);
	    }
	}
	
	public Object getDirectValue(Variable variable, VariableOwner owner) {
	    if (variable._values == null)
		return Variable.UNBOUND;
	    Object val = variable._values.get(owner);
	    if (val == null)
		val = Variable.UNBOUND;
	    else if (val == Variable.LOCAL_NULL)
		val = null;
	    return val;
	}
	
	public Object mapUnboundDirect(Variable variable,
				       VariableOwner owner) {
	    Object val;
	    if (owner == variable._listOwner)
		val = variable.getDefaultValue(owner);
	    else {
		val = variable.getActualValue(variable._listOwner);
		if (val == Variable.UNBOUND)
		    val = variable.getDefaultValue(owner);
	    }
	    return val;
	}
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    return value;
	}
    }
    
    public static class NumberDirectAccessor extends StandardDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    if (value == Variable.UNBOUND)
		return value;
	    if (value instanceof Number)
		return value;
	    String valueString = null;
	    if (value instanceof String)
		valueString = (String) value;
	    else if (value != null)
		valueString = value.toString();
	    if (valueString != null) {
		Object attempt = Resource.parseNumberString(valueString);
		if (attempt instanceof Number)
		    return attempt;
	    }
	    return Variable.ILLEGAL_VALUE;
	}
    }
    
    public static class StringDirectAccessor extends StandardDirectAccessor
    {
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    if (value == null)
		return value;
	    if (value == Variable.UNBOUND)
		return value;
	    if (value instanceof String)
		return value;
	    if (value instanceof Number)
		return Resource.formatNumber((Number) value);
	    if (value instanceof Named)
		return ((Named) value).getName();
	    return Variable.ILLEGAL_VALUE;
	}
    }
    
    public static class FontSizeDirectAccessor extends NumberDirectAccessor
    {
	public static final int MAX_TEXT_SIZE_ON_WINDOWS = 70;
	
	public Object constrainDirectValue(Variable variable,
					   VariableOwner owner, Object value) {
	    Object result = super.constrainDirectValue(variable, owner, value);
	    if (result != Variable.ILLEGAL_VALUE
		&& result != Variable.UNBOUND) {
		int size = ((Number) result).intValue();
		if (size < 1 || size > 70)
		    result = Variable.ILLEGAL_VALUE;
	    }
	    return result;
	}
    }
    
    static {
	BuiltinProxyTable.helper.registerProxy(UNBOUND.toString(), UNBOUND);
	deletedVariable = new Variable() {
	    public String toString() {
		return "*deleted variable*";
	    }
	};
	ILLEGAL_VALUE = Operation.ERROR;
	variablesInWorld = new Hashtable(7);
	systemVariableRegistry = new Hashtable(50);
	deferNotify = 0;
    }
    
    public static Variable newSystemVariable(String sysvarID,
					     VariableOwner listOwner) {
	Variable newVariable = null;
	Debug.print("debug.variable", "New system variable: ", sysvarID,
		    " for ", listOwner);
	sysvarID = sysvarID.intern();
	Variable masterVariable
	    = (Variable) systemVariableRegistry.get(sysvarID);
	if (masterVariable != null) {
	    try {
		newVariable = (Variable) masterVariable.clone();
	    } catch (CloneNotSupportedException e) {
		Debug.print(true, e);
	    }
	}
	if (newVariable == null) {
	    Debug.print(true,
			"Attempt to create unregistered system variable: ",
			sysvarID);
	    newVariable = new Variable(sysvarID, listOwner);
	}
	newVariable.setListOwner(listOwner);
	newVariable._watchers = new UpdateManager();
	return newVariable;
    }
    
    public Variable(String sysvarID, String nameToken,
		    VariableDirectAccessor accessor, boolean visible) {
	fillInObject(Resource.getText(nameToken), null, sysvarID, accessor,
		     visible);
    }
    
    public Variable(ResourceBundle bundle, String sysvarID,
		    VariableDirectAccessor accessor, boolean visible) {
	this(bundle, sysvarID, sysvarID, accessor, visible);
    }
    
    public Variable(ResourceBundle bundle, String sysvarID, String nameID,
		    VariableDirectAccessor accessor, boolean visible) {
	fillInObject(Resource.getText(bundle, nameID), null, sysvarID,
		     accessor, visible);
    }
    
    protected Variable(String sysvarID, String nameToken, boolean visible) {
	this(sysvarID, nameToken, STD_ACCESSOR, visible);
    }
    
    public Variable(String name, VariableOwner listOwner, boolean visible) {
	fillInObject(name, listOwner, null, STD_ACCESSOR, visible);
    }
    
    public Variable(String name, VariableOwner listOwner) {
	this(name, listOwner, true);
    }
    
    public Variable() {
	/* empty */
    }
    
    void fillInObject(String name, VariableOwner listOwner, String systemID,
		      VariableDirectAccessor accessor, boolean visible) {
	if (systemID == null) {
	    ASSERT.isNotNull(listOwner, "listOwner");
	    if (listOwner instanceof CocoaCharacter)
		ASSERT.isIdentical(((CocoaCharacter) listOwner).getPrototype(),
				   listOwner, "prototype()", "listOwner");
	} else
	    ASSERT.isNull(listOwner, "listOwner");
	ASSERT.isNotNull(accessor, "accessor");
	drFillInObject(name, listOwner, systemID, accessor, visible);
    }
    
    void drFillInObject(String name, VariableOwner listOwner, String systemID,
			VariableDirectAccessor accessor, boolean visible) {
	_name = name;
	setListOwner(listOwner);
	_isVisible = visible;
	_systemID = systemID == null ? null : systemID.intern();
	_accessor = accessor;
	_uniqueID = new UniqueID();
	_uniqueParentID = null;
	if (isSystemVariable()) {
	    _watchers = null;
	    if (systemVariableRegistry.get(_systemID) != null)
		Debug.print(true,
			    "Duplicate registration of system variable: ",
			    systemID);
	    systemVariableRegistry.put(_systemID, this);
	} else
	    _watchers = new UpdateManager();
    }
    
    protected Object clone() throws CloneNotSupportedException {
	Variable newVar = (Variable) super.clone();
	newVar._listOwner = null;
	ASSERT.isNotNull(_systemID, "_systemID");
	ASSERT.isNull(_values, "_values");
	newVar._container = null;
	newVar._values = null;
	newVar._valueWatchers = null;
	newVar._watchers = null;
	return newVar;
    }
    
    public boolean isValid() {
	if (_listOwner == null || _name == null || _uniqueID == null)
	    return false;
	return true;
    }
    
    public final String getName() {
	return _name;
    }
    
    public void setName(String s) {
	_name = s;
	updateVariableWatchers();
    }
    
    public final boolean isUserVariable() {
	return _systemID == null;
    }
    
    public final boolean isSystemVariable() {
	return _systemID != null;
    }
    
    public final boolean isSystemType(String id) {
	return _systemID == id.intern();
    }
    
    public final String getSystemType() {
	return _systemID;
    }
    
    public final boolean isVisible() {
	return _isVisible;
    }
    
    public final void setVisible(boolean b) {
	_isVisible = b;
	if (_listOwner != null)
	    updateVariableWatchers();
    }
    
    public final boolean isTransient() {
	return _isTransient;
    }
    
    public final void setTransient(boolean flag) {
	_isTransient = flag;
    }
    
    public World getWorld() {
	return _listOwner.getWorld();
    }
    
    public Object getDefaultValue(VariableOwner owner) {
	return _defaultValue;
    }
    
    public void setDefaultValue(Object value) {
	_defaultValue = value;
    }
    
    VariableOwner getListOwner() {
	return _listOwner;
    }
    
    void setListOwner(VariableOwner owner) {
	if (_listOwner == null && owner != null) {
	    _listOwner = owner;
	    addNewVariable(this);
	} else if (_listOwner != owner) {
	    if (readingCocoaWorld)
		DRTranslator.setHasDuplicateVariables(true);
	    else
		throw new PlaywriteInternalError
			  ("Setting list owner twice for " + this + ", was "
			   + _listOwner + " wants to be " + owner);
	}
    }
    
    public GenericContainer getContainer() {
	return _container;
    }
    
    public void setContainer(GenericContainer c) {
	_container = c;
    }
    
    static void setReadingCocoaWorld(boolean b) {
	readingCocoaWorld = b;
    }
    
    Variable getFixedCocoaVariable(VariableOwner owner) {
	if (isSystemVariable())
	    return this;
	ASSERT.isTrue(owner.getVariableListOwner() != null);
	owner = owner.getVariableListOwner();
	if (getListOwner() != null
	    && getListOwner() != owner.getVariableListOwner()) {
	    if (cocoaFixTable == null)
		cocoaFixTable = new Hashtable(10);
	    Hashtable myVarMap = (Hashtable) cocoaFixTable.get(this);
	    if (myVarMap == null) {
		myVarMap = new Hashtable(10);
		cocoaFixTable.put(this, myVarMap);
	    }
	    Variable newVarForOwner = (Variable) myVarMap.get(owner);
	    if (newVarForOwner == null) {
		newVarForOwner = (Variable) copy(owner);
		if (newVarForOwner == this)
		    throw new PlaywriteInternalError
			      ("asked for a copy of variable, got the same one back!");
		myVarMap.put(owner, newVarForOwner);
	    }
	    return newVarForOwner;
	}
	return this;
    }
    
    static void discardCocoaFixTable() {
	cocoaFixTable = null;
    }
    
    public void setDirectAccessor(VariableDirectAccessor accessor) {
	_accessor = accessor;
    }
    
    public VariableDirectAccessor getDirectAccessor() {
	return _accessor;
    }
    
    public static final void deregister(String sysvarID) {
	Object removed = systemVariableRegistry.remove(sysvarID);
	ASSERT.isNotNull(removed);
    }
    
    public static final Variable systemVariable(String sysvarID,
						VariableOwner owner) {
	return owner.getVariableList().findSystemVariable(sysvarID);
    }
    
    public static final Object setSystemValue
	(String sysvarID, VariableOwner owner, Object value) {
	return systemVariable(sysvarID, owner).setValue(owner, value);
    }
    
    public static final Object getSystemValue(String sysvarID,
					      VariableOwner owner) {
	return systemVariable(sysvarID, owner).getValue(owner);
    }
    
    static final void updateSystemVariableWatchers(String sysvarID,
						   World world) {
	Vector variableList = (Vector) variablesInWorld.get(world);
	if (variableList != null) {
	    for (int i = 0; i < variableList.size(); i++) {
		Variable v = (Variable) variableList.elementAt(i);
		if (v.isSystemType(sysvarID))
		    v.updateVariableWatchers();
	    }
	}
    }
    
    private static void addNewVariable(Variable v) {
	ASSERT.isNotNull(v._listOwner);
	Vector variableList = (Vector) variablesInWorld.get(v.getWorld());
	if (variableList == null) {
	    variableList = new Vector(100);
	    variablesInWorld.put(v.getWorld(), variableList);
	}
	if (!variableList.contains(v)) {
	    variableList.addElement(v);
	    Debug.print("debug.variable", "Added to variablesInWorld: ", v);
	}
    }
    
    private static void removeVariable(Variable v) {
	Vector variableList = (Vector) variablesInWorld.get(v.getWorld());
	variableList.removeElement(v);
	Debug.print("debug.variable", "Removing variable: ", v);
    }
    
    static void deleteOwner(VariableOwner owner) {
	Vector variableList = (Vector) variablesInWorld.get(owner.getWorld());
	if (variableList != null) {
	    for (int i = 0; i < variableList.size(); i++) {
		Variable v = (Variable) variableList.elementAt(i);
		if (v._values != null) {
		    Object o = v._values.remove(owner);
		    if (o != null)
			Debug.print("debug.variable", "Removing owner ", owner,
				    " on variable ", v);
		}
		if (v._valueWatchers != null)
		    v._valueWatchers.remove(owner);
	    }
	    resetVariablesSetTo(owner, owner.getWorld());
	    if (owner instanceof World)
		variablesInWorld.remove(owner);
	}
    }
    
    static void resetVariablesSetTo(Object obj, World world) {
	if (world.getState() != World.CLOSING) {
	    Vector variableList = (Vector) variablesInWorld.get(world);
	    if (variableList != null) {
		for (int i = 0; i < variableList.size(); i++) {
		    Variable variable = (Variable) variableList.elementAt(i);
		    if (variable._values != null) {
			Enumeration owners = variable._values.keys();
			while (owners.hasMoreElements()) {
			    VariableOwner owner
				= (VariableOwner) owners.nextElement();
			    Object val = variable.getActualValue(owner);
			    if (val == obj
				|| (obj instanceof CharacterPrototype
				    && val instanceof CharacterInstance
				    && ((CharacterInstance) val)
					   .getPrototype() == obj))
				variable.setValue(owner,
						  variable
						      .getDefaultValue(owner));
			}
		    }
		}
	    }
	}
    }
    
    static Variable xlateV1Variable(Object vari, VariableOwner owner) {
	Variable v;
	if (vari instanceof Variable) {
	    v = (Variable) vari;
	    if (v._listOwner == null)
		v.setListOwner(owner);
	} else {
	    ASSERT.isClass(vari, String.class);
	    v = owner.getVariableList().findSystemVariable((String) vari);
	    if (v == null) {
		Debug.print(true, "Adding system variable from object store");
		v = newSystemVariable((String) vari, owner);
	    }
	}
	return v;
    }
    
    public static void dumpVariableTable(World world) {
	Vector variableList = (Vector) variablesInWorld.get(world);
	if (variableList != null) {
	    for (int i = 0; i < variableList.size(); i++) {
		Variable variable = (Variable) variableList.elementAt(i);
		if (variable._values != null) {
		    Enumeration owners = variable._values.keys();
		    while (owners.hasMoreElements()) {
			VariableOwner owner
			    = (VariableOwner) owners.nextElement();
			Object val = variable.getActualValue(owner);
			try {
			    Debug.print(true, variable, "[", owner, "] -> ",
					val);
			} catch (Exception exception) {
			    /* empty */
			}
		    }
		}
	    }
	}
    }
    
    static void dumpVariablesFor(VariableOwner own) {
	Debug.print(true, "  Owner -> ", own);
	COM.stagecast.ifc.netscape.util.Enumeration e
	    = own.getVariableList().elements();
	while (e.hasMoreElements()) {
	    Variable v = (Variable) e.nextElement();
	    Object val = v.getActualValue(own);
	    Debug.print(true, "[", v, "] -> " + val);
	}
    }
    
    boolean existsFor(VariableOwner owner) {
	return owner.getVariableList().hasVariable(this);
    }
    
    public final Object getActualValue(VariableOwner owner) {
	return _accessor.getDirectValue(this, owner);
    }
    
    final Object mapUnboundValue(VariableOwner owner) {
	return _accessor.mapUnboundDirect(this, owner);
    }
    
    public final Object getValue(VariableOwner owner) {
	ASSERT.isNotNull(owner, "owner");
	ASSERT.isNotNull(_listOwner, "_listOwner");
	if (owner instanceof CocoaCharacter)
	    ASSERT.isIdentical(((CocoaCharacter) owner).getPrototype(),
			       _listOwner, "prototype()", " _listOwner");
	else
	    ASSERT.isIdentical(owner, _listOwner, "owner", "_listOwner");
	Object val = getActualValue(owner);
	if (val == UNBOUND)
	    val = mapUnboundValue(owner);
	return val;
    }
    
    public final void setActualValue(VariableOwner owner, Object val) {
	_accessor.setDirectValue(this, owner, val);
	VariableSieve sieve = owner.getWorld().getVariableSieve();
	if (sieve != null) {
	    if (val == UNBOUND)
		val = getValue(owner);
	    sieve.strain(owner, this, val);
	}
    }
    
    public final Object setValue(VariableOwner owner, Object val) {
	Object oldVal = getValue(owner);
	ASSERT.isNotNull(owner, "owner");
	ASSERT.isNotNull(_listOwner, "_listOwner");
	if (owner instanceof CocoaCharacter)
	    ASSERT.isIdentical(((CocoaCharacter) owner).getPrototype(),
			       _listOwner, "prototype()", "_listOwner");
	else
	    ASSERT.isIdentical(owner, _listOwner, "owner", "_listOwner");
	Debug.print("debug.variable", "Set ", owner, "'s ", getName(), " to ",
		    val);
	val = constrainedValue(owner, val);
	if (val == ILLEGAL_VALUE)
	    return val;
	setActualValue(owner, val);
	if (val == UNBOUND)
	    val = getValue(owner);
	notifyChanged(owner, oldVal, val);
	if (owner != _listOwner && getActualValue(_listOwner) == UNBOUND)
	    setValue(_listOwner, val);
	return val;
    }
    
    public static void setDeferredUpdates(boolean defer) {
	if (defer)
	    deferNotify++;
	else if (--deferNotify < 0)
	    deferNotify = 0;
    }
    
    public void notifyChanged(final VariableOwner owner, final Object oldVal,
			      final Object val) {
	if (_valueWatchers != null) {
	    if (deferNotify > 0) {
		final Variable v = this;
		PlaywriteRoot.app().performCommandLater(new Target() {
		    public void performCommand(String command, Object data) {
			v.notifyChanged(owner, oldVal, val);
		    }
		}, "go", null);
	    } else {
		UpdateManager list = (UpdateManager) _valueWatchers.get(owner);
		if (list != null) {
		    Debug.print("debug.variable",
				"Notifying value watchers on ", owner);
		    list.update(this, val);
		}
		if (_listOwner == owner
		    && _listOwner instanceof CharacterPrototype) {
		    Enumeration allOwners = _valueWatchers.keys();
		    while (allOwners.hasMoreElements()) {
			VariableOwner wOwner
			    = (VariableOwner) allOwners.nextElement();
			if (getActualValue(wOwner) == UNBOUND) {
			    list = (UpdateManager) _valueWatchers.get(wOwner);
			    if (list != null) {
				Debug.print("debug.variable",
					    "Notifying value watchers on ",
					    wOwner);
				list.update(this, val);
			    }
			}
		    }
		}
	    }
	}
    }
    
    final Object constrainedValue(VariableOwner owner, Object value) {
	if (value instanceof Variable || value instanceof VariableAlias)
	    throw new PlaywriteInternalError
		      ("embedding of variables not permitted");
	if (value instanceof ReferencedObject
	    && !(value instanceof FirstClassValue))
	    return ILLEGAL_VALUE;
	if (value instanceof CocoaCharacter
	    && !isSystemType(World.SYS_FOLLOW_ME_VARIABLE_ID))
	    return ILLEGAL_VALUE;
	if (value == ILLEGAL_VALUE)
	    return value;
	return _accessor.constrainDirectValue(this, owner, value);
    }
    
    void addVariableWatcher(Watcher w) {
	_watchers.add(w);
    }
    
    void removeVariableWatcher(Watcher w) {
	_watchers.remove(w);
    }
    
    void updateVariableWatchers() {
	Debug.print("debug.variable", "Updating variable watchers on ", this);
	_watchers.update(this, null);
    }
    
    public void addValueWatcher(VariableOwner owner, Watcher w) {
	if (_valueWatchers == null)
	    _valueWatchers = new Hashtable(10);
	UpdateManager list = (UpdateManager) _valueWatchers.get(owner);
	if (list == null) {
	    list = new UpdateManager();
	    _valueWatchers.put(owner, list);
	}
	list.add(w);
    }
    
    public void removeValueWatcher(VariableOwner owner, Watcher w) {
	if (_valueWatchers != null) {
	    UpdateManager list = (UpdateManager) _valueWatchers.get(owner);
	    if (list != null)
		list.remove(w);
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
    
    public boolean isCopyOf(ReferencedObject v) {
	return (_uniqueID.equals(v.getParentID())
		|| v.getID().equals(_uniqueParentID));
    }
    
    public void highlightForSelection() {
	/* empty */
    }
    
    public void unhighlightForSelection() {
	/* empty */
    }
    
    public Object copy() {
	return copy(new COM.stagecast.ifc.netscape.util.Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	COM.stagecast.ifc.netscape.util.Hashtable map
	    = new COM.stagecast.ifc.netscape.util.Hashtable(50);
	if (getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	return copy(map, true);
    }
    
    public Object copy(VariableOwner newOwner) {
	COM.stagecast.ifc.netscape.util.Hashtable map
	    = new COM.stagecast.ifc.netscape.util.Hashtable(50);
	if (_listOwner != newOwner)
	    map.put(_listOwner, newOwner);
	return copy(map, true);
    }
    
    public Object copy(COM.stagecast.ifc.netscape.util.Hashtable map,
		       boolean fullCopy) {
	VariableOwner newOwner = null;
	Variable newVariable = (Variable) map.get(this);
	if (newVariable != null)
	    return newVariable;
	World oldWorld = getWorld();
	Object mappedObject = map.get(oldWorld);
	World newWorld;
	if (mappedObject == null)
	    newWorld = null;
	else if (mappedObject instanceof World)
	    newWorld = (World) mappedObject;
	else if (mappedObject instanceof VariableOwner) {
	    newOwner = (VariableOwner) mappedObject;
	    if (oldWorld == newOwner.getWorld())
		newWorld = null;
	    else
		newWorld = newOwner.getWorld();
	} else
	    throw new PlaywriteInternalError("Illegal mapping: " + oldWorld
					     + " -> " + mappedObject);
	VariableOwner oldOwner = _listOwner;
	if (newOwner == null)
	    newOwner = (VariableOwner) map.get(oldOwner);
	if (newOwner == null)
	    newOwner = oldOwner;
	else {
	    newVariable
		= newOwner.getVariableList().findEquivalentVariable(this);
	    if (newVariable != null && newVariable.getListOwner() != newOwner)
		newVariable = null;
	}
	if (newVariable == null) {
	    if (fullCopy) {
		String newName;
		if (oldOwner == newOwner)
		    newName = Util.makeCopyName(getName());
		else
		    newName = getName();
		if (isSystemVariable())
		    newVariable = newSystemVariable(_systemID, newOwner);
		else
		    newVariable = new Variable(newName, newOwner, _isVisible);
		map.put(this, newVariable);
		newOwner.getVariableList().addVariable(newVariable, null,
						       null);
		if (newWorld != null)
		    newVariable.setParentID(getID());
	    } else if (newWorld == null) {
		if (oldOwner == newOwner)
		    newVariable = this;
		else {
		    newVariable = newOwner.getVariableList()
				      .findEquivalentVariable(this);
		    if (newVariable == null) {
			newVariable = (Variable) copy(map, true);
			newVariable.setValue(newOwner, getValue(oldOwner));
		    }
		}
	    } else
		newVariable = (Variable) copy(map, true);
	}
	return newVariable;
    }
    
    void copyValue(Object oldValue, VariableOwner newOwner,
		   COM.stagecast.ifc.netscape.util.Hashtable map,
		   boolean fullCopy) {
	Debug.print("debug.variable", "Copying value [", oldValue, "] of ",
		    getName(), " to ", newOwner);
	if (oldValue instanceof Copyable && map != null) {
	    Object newVal = ((Copyable) oldValue).copy(map, fullCopy);
	    setActualValue(newOwner, newVal);
	    Debug.print("debug.variable", "  copied value transformed to ",
			newVal);
	} else
	    setActualValue(newOwner, oldValue);
    }
    
    public boolean allowDelete() {
	ASSERT.isTrue(PlaywriteRoot.isAuthoring());
	if (isSystemVariable())
	    return false;
	if (getWorld().ruleRefersTo(this, "REFOBJ var ID"))
	    return false;
	if (getListOwner() instanceof CharacterPrototype) {
	    CharacterPrototype proto = (CharacterPrototype) getListOwner();
	    int count = proto.countAppearancesDisplaying(this);
	    if (count > 0) {
		Object[] params = { new Integer(count) };
		PlaywriteDialog dialog
		    = new PlaywriteDialog((Resource.getTextAndFormat
					   ("dialog appearance ref variable",
					    params)),
					  "command show appearance",
					  "command c");
		if (dialog.getAnswer().equals("command show appearance")) {
		    PlaywriteRoot.markBusy();
		    Appearance appearance
			= proto.findAppearanceDisplaying(this);
		    PlaywriteRoot.getAppearanceEditorController()
			.displayEditorFor(proto, appearance);
		    PlaywriteRoot.clearBusy();
		}
		return false;
	    }
	}
	return true;
    }
    
    public void delete() {
	ASSERT.isNotNull(_listOwner, "_listOwner in delete");
	Debug.print("debug.variable", "Deleting variable ", this);
	removeVariable(this);
	if (_valueWatchers != null)
	    _valueWatchers.clear();
	if (_watchers != null)
	    _watchers.removeAllWatchers();
	_watchers = null;
	if (_values != null)
	    _values.clear();
	_values = null;
	_container = null;
	_accessor = null;
	_listOwner = null;
	_name = null;
    }
    
    public void deleteUserVariable(VariableOwner owner) {
	ASSERT.isTrue(isUserVariable());
	owner.getVariableList().remove(this);
	ASSERT.isNull(_listOwner);
	ASSERT.isNull(_accessor);
    }
    
    public void undelete() {
	throw new PlaywriteInternalError
		  ("Deleting variables cannot be undone.");
    }
    
    AbstractVariableEditor makeVariableEditor(VariableOwner owner,
					      ValueView.SetterGetter vsg) {
	AbstractVariableEditor editor = new VariableEditor(owner, this, vsg);
	editor.updateContentView();
	return editor;
    }
    
    VariableAlias createVariableAlias(VariableOwner owner) {
	VariableAlias va = null;
	if (owner instanceof CharacterInstance) {
	    GeneralizedCharacter gch
		= RuleEditor
		      .getGeneralizedCharacterFor((CharacterInstance) owner);
	    va = new VariableAlias(gch, this);
	} else if (owner instanceof GCAlias) {
	    if (RuleEditor.isRecordingOrEditing()) {
		GeneralizedCharacter gch = ((GCAlias) owner).findOriginal();
		va = new VariableAlias(gch, this);
	    } else
		return null;
	} else {
	    if (owner instanceof CharacterPrototype)
		return null;
	    va = new VariableAlias(owner, this);
	}
	return va;
    }
    
    public static final boolean areEqual(Object a, Object b) {
	if (a == b
	    || (a instanceof Number && b instanceof Number
		&& ((Number) a).doubleValue() == ((Number) b).doubleValue())
	    || a instanceof String && b instanceof String && a.equals(b))
	    return true;
	return false;
    }
    
    void modifyValue(VariableOwner owner, Object val) {
	if (owner instanceof GeneralizedCharacter)
	    PlaywriteDialog.warning("dialog ccgc");
	else if (owner instanceof CharacterPrototype)
	    setValue(owner, val);
	else if (owner instanceof GCAlias
		 && !RuleEditor.isRecordingOrEditing())
	    PlaywriteDialog.warning("dialog ebc");
	else {
	    Object oldValue = getValue(owner);
	    if (areEqual(val, oldValue)
		&& !isSystemType(CocoaCharacter.SYS_SOUND_VARIABLE_ID))
		setValue(owner, val);
	    else {
		VariableAlias va = createVariableAlias(owner);
		RuleAction putAction = new PutAction(va, val);
		owner.getWorld().doManualAction(putAction);
	    }
	}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isTrue(isSystemVariable() ^ true,
		      "Attempt to write system variable");
	ASSERT.isNotNull(_name);
	ASSERT.isNotNull(_listOwner);
	ASSERT.isNotNull(_uniqueID);
	ASSERT.isTrue(_isTransient ^ true);
	out.writeUTF(_name);
	out.writeBoolean(_isVisible);
	out.writeObject(_listOwner);
	out.writeObject(_uniqueID);
	out.writeObject(_uniqueParentID);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(Variable.class);
	_name = in.readUTF();
	_accessor = STD_ACCESSOR;
	_listOwner = null;
	_container = null;
	_defaultValue = null;
	_values = null;
	_valueWatchers = null;
	_watchers = new UpdateManager();
	switch (version) {
	case 1:
	case 2:
	    in.readBoolean();
	    break;
	case 3:
	    in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 4);
	case 4:
	    /* empty */
	}
	_isVisible = in.readBoolean();
	switch (version) {
	case 2:
	case 3:
	    in.readBoolean();
	    break;
	case 4:
	    setListOwner((VariableOwner) in.readObject());
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 4);
	case 1:
	    /* empty */
	}
	_uniqueID = (UniqueID) in.readObject();
	_uniqueParentID = (UniqueID) in.readObject();
    }
    
    public String toString() {
	return ("<" + (isSystemVariable() ? "System " : "") + "Variable '"
		+ _name + "'>");
    }
}
