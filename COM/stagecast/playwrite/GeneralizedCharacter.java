/* GeneralizedCharacter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Expression;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class GeneralizedCharacter extends CocoaCharacter
    implements Debug.Constants, Expression, Externalizable, ReferencedObject,
	       ResourceIDs.SummaryIDs, Summarizable, Verifiable, Target
{
    static final Bindable anyType = new BindsAnything();
    static final String HALO_COMMAND = "haloViews";
    static final String UNHALO_COMMAND = "unhaloViews";
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108756264242L;
    private Bindable _valueType = null;
    private Appearance _originalAppearance = null;
    private UniqueID _uniqueID;
    private UniqueID _uniqueParentID;
    private boolean _proxyFlag = false;
    private transient CharacterInstance _binding = null;
    private transient GCAlias _afterBoardCharacter = null;
    private transient boolean _haloed = false;
    
    public GeneralizedCharacter(CharacterPrototype prototype) {
	fillInObject(prototype);
    }
    
    GeneralizedCharacter(CharacterInstance ch) {
	this(ch.getPrototype());
	bind(ch);
	this.copyVariableValuesFrom(ch, false);
	this.setName(ch.getName());
	this.setContainer(null);
	Appearance app = ch.getCurrentAppearance();
	setOriginalAppearance(app);
	this.setCurrentAppearance(app);
    }
    
    public GeneralizedCharacter() {
	/* empty */
    }
    
    void fillInObject(CharacterPrototype prototype) {
	this.setPrototype(prototype);
	setOriginalAppearance(prototype.getCurrentAppearance());
	setValueType(prototype);
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
    
    public boolean isValid() {
	if (!super.isValid())
	    return false;
	if (_valueType == null)
	    return false;
	if (getOriginalAppearance() == null)
	    return false;
	return true;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	GeneralizedCharacter newGC = (GeneralizedCharacter) map.get(this);
	if (newGC != null)
	    return newGC;
	newGC = (GeneralizedCharacter) this.copy(map, fullCopy,
						 "generalized character");
	CharacterPrototype newPrototype = newGC.getPrototype();
	Appearance newOriginalAppearance
	    = (Appearance) map.get(_originalAppearance);
	if (newOriginalAppearance == null)
	    newOriginalAppearance
		= newPrototype.findSimilarAppearance(_originalAppearance);
	map.put(_originalAppearance, newOriginalAppearance);
	newGC.setOriginalAppearance(newOriginalAppearance);
	Bindable newValueType;
	if (_valueType == anyType)
	    newValueType = anyType;
	else if (_valueType == this.getPrototype())
	    newValueType = newPrototype;
	else
	    newValueType
		= (Bindable) ((ReferencedObject) _valueType).copy(map,
								  fullCopy);
	newGC.setValueType(newValueType);
	return newGC;
    }
    
    public final CharacterInstance getBinding() {
	return _binding;
    }
    
    final void setBinding(CharacterInstance ch) {
	World world = this.getWorld();
	if (_binding != null)
	    _binding._boundBy = null;
	_binding = ch;
	if (ch == null)
	    world.unmarkGeneralizedCharacter(this);
	else {
	    world.markGeneralizedCharacter(this);
	    _binding._boundBy = this;
	}
    }
    
    final Bindable getValueType() {
	return _valueType;
    }
    
    void setValueType(Bindable type) {
	if (_valueType != type) {
	    _valueType = type;
	    if (_valueType instanceof CharacterPrototype)
		this.setCurrentAppearance(_originalAppearance);
	    else
		this.setCurrentAppearance
		    (type.makeAppearance(_originalAppearance));
	    if (_afterBoardCharacter != null)
		_afterBoardCharacter.setValueType(type);
	    this.setName(_valueType.getName());
	}
    }
    
    final GCAlias getAfterBoardCharacter() {
	return _afterBoardCharacter;
    }
    
    final void setAfterBoardCharacter(GCAlias ch) {
	_afterBoardCharacter = ch;
    }
    
    final Appearance getOriginalAppearance() {
	if (_originalAppearance == null && this.getPrototype() != null)
	    _originalAppearance = this.getCurrentAppearance();
	return _originalAppearance;
    }
    
    final void setOriginalAppearance(Appearance a) {
	_originalAppearance = a;
    }
    
    public void edit() {
	if (!RuleEditor.isRecordingOrEditing())
	    PlaywriteDialog.warning("dialog ebo");
	else if (this.getPrototype().isProxy())
	    PlaywriteDialog.warning("dialog niw");
	else {
	    CocoaCharacter realCharacter = dereference();
	    if (realCharacter == null || realCharacter == this)
		_afterBoardCharacter.edit();
	    else
		realCharacter.edit();
	}
    }
    
    CocoaCharacter dereference() {
	return getBinding();
    }
    
    final boolean bind(CharacterInstance ch) {
	if (ch.isInvisible())
	    return false;
	if (_binding != null)
	    return _binding == ch;
	if (_valueType.binds(ch)) {
	    setBinding(ch);
	    return true;
	}
	return false;
    }
    
    boolean isBound() {
	return _binding != null;
    }
    
    final void unbind() {
	setBinding(null);
    }
    
    static void resetAll(GeneralizedCharacter[] boundGCs, int top) {
	int i = top;
	while (i-- > 0) {
	    if (boundGCs[i] != null) {
		boundGCs[i]._binding._boundBy = null;
		boundGCs[i]._binding = null;
		boundGCs[i] = null;
	    }
	}
    }
    
    public Object eval() {
	return _binding;
    }
    
    public void summarize(Summary s) {
	if (_valueType == anyType)
	    s.writeValue(Resource.getText("SUM gc a"));
	else if (_valueType instanceof Jar)
	    s.writeValue(Resource.getTextAndFormat("SUM gc j",
						   (new Object[]
						    { _valueType
							  .getName() })));
	else
	    s.writeValue(_valueType.getName());
    }
    
    public Object findReferenceTo(ReferencedObject obj) {
	if (refersTo(obj))
	    return this;
	return null;
    }
    
    public Expression evaluates(Expression foo) {
	return null;
    }
    
    public LineView makeView(LineView view) {
	PlaywriteView newView = createIcon();
	view.addSubview(newView);
	return view;
    }
    
    private void addHaloDelegate(PlaywriteView newView) {
	newView.setEventDelegate(-1, 0, 1, "haloViews", this);
	newView.setEventDelegate(-3, 0, 1, "unhaloViews", this);
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return (super.refersTo(obj) || getValueType() == obj
		|| getOriginalAppearance() == obj);
    }
    
    public void highlightForSelection() {
	/* empty */
    }
    
    public void unhighlightForSelection() {
	/* empty */
    }
    
    public void halo() {
	if (this.hasCharacterViews())
	    this.getCharacterViewManager().hilite();
	if (this.hasIconViews())
	    this.getIconViewManager().hilite();
	if (_binding != null)
	    _binding.halo();
	_haloed = true;
	if (_afterBoardCharacter != null)
	    _afterBoardCharacter.haloFromGC();
    }
    
    public void unhalo() {
	if (this.hasCharacterViews())
	    this.getCharacterViewManager().unhilite();
	if (this.hasIconViews())
	    this.getIconViewManager().unhilite();
	if (_binding != null)
	    _binding.unhalo();
	_haloed = false;
	if (_afterBoardCharacter != null)
	    _afterBoardCharacter.unhaloFromGC();
    }
    
    void haloFromGCAlias() {
	if (!_haloed)
	    halo();
    }
    
    void unhaloFromGCAlias() {
	if (_haloed)
	    unhalo();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_valueType);
	ASSERT.isNotNull(_originalAppearance);
	super.writeExternal(out);
	out.writeInt(this.getH());
	out.writeInt(this.getV());
	if (_valueType == anyType)
	    out.writeObject("ANY");
	else
	    out.writeObject(_valueType);
	out.writeObject(_originalAppearance);
	this.getVariableList().writeListValues(out, this);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	int h = in.readInt();
	int v = in.readInt();
	Object val = in.readObject();
	if (val instanceof String)
	    _valueType = anyType;
	else
	    _valueType = (Bindable) val;
	_originalAppearance = (Appearance) in.readObject();
	this.getVariableList().readListValues(in, this);
	this.setH(h);
	this.setV(v);
	if (_originalAppearance.getOwner() == null) {
	    Debug.print("debug.appearance", "setting null owner for '",
			_originalAppearance, "' in ", this, " to ",
			this.getPrototype());
	    _originalAppearance.setOwner(this.getPrototype());
	}
	if (this.getPrototype() instanceof SpecialPrototype
	    && (((WorldInStream) in).loadVersion(TextCharacterInstance.class)
		< 4)) {
	    Variable sWidth
		= Variable.systemVariable((SpecialPrototype
					   .SYS_SPECIAL_WIDTH_VARIABLE_ID),
					  this);
	    Variable sHeight
		= Variable.systemVariable((SpecialPrototype
					   .SYS_SPECIAL_HEIGHT_VARIABLE_ID),
					  this);
	    int logicalWidth = _originalAppearance.getLogicalWidth();
	    int logicalHeight = _originalAppearance.getLogicalHeight();
	    sWidth.setValue(this, new Double((double) logicalWidth));
	    sHeight.setValue(this, new Double((double) logicalHeight));
	    Debug.print(true,
			"Special char GC adjusted to size " + logicalWidth,
			"x" + logicalHeight);
	}
    }
    
    public PlaywriteView createView() {
	PlaywriteView GCView = this.getPrototype().createView();
	GCView.setModelObject(this);
	return GCView;
    }
    
    Icon createIcon() {
	Icon icon = super.createIcon();
	icon.setShowName(false);
	addHaloDelegate(icon);
	return icon;
    }
    
    public PlaywriteView createIconView() {
	PlaywriteView newView = super.createIconView();
	if (newView instanceof Icon)
	    ((Icon) newView).setShowName(false);
	addHaloDelegate(newView);
	return newView;
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("haloViews"))
	    halo();
	else if (command.equals("unhaloViews"))
	    unhalo();
    }
    
    public String toString() {
	String bindsTo = null;
	if (_valueType == anyType)
	    bindsTo = "<any object>";
	else if (_valueType == null) {
	    bindsTo = "NULL(";
	    if (this.getPrototype() == null)
		bindsTo += "no proto)";
	    else
		bindsTo += this.getPrototype().toString() + ")";
	} else
	    bindsTo = _valueType.toString();
	if (_binding != null)
	    bindsTo += " bound to " + _binding.toString();
	return "<GeneralizedCharacter of type " + bindsTo + ">";
    }
}
