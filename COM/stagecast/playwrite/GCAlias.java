/* GCAlias - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;

public class GCAlias extends CocoaCharacter implements Movable, Selectable
{
    private GeneralizedCharacter _referent;
    private Appearance _baseAppearance;
    private boolean _haloed = false;
    
    GCAlias(GeneralizedCharacter gch) {
	this.setPrototype(gch.getPrototype());
	_referent = gch;
	gch.setAfterBoardCharacter(this);
	this.copyVariableValuesFrom(_referent, false);
	_baseAppearance = gch.getOriginalAppearance();
	this.setCurrentAppearance(gch.getCurrentAppearance());
	this.getPrototype().appearanceVar.addValueWatcher(this, new Watcher() {
	    public void update(Object variable, Object val) {
		if (val instanceof Appearance)
		    _baseAppearance = (Appearance) val;
	    }
	});
    }
    
    final GeneralizedCharacter findOriginal() {
	return _referent;
    }
    
    Object getValueType() {
	return _referent.getValueType();
    }
    
    void setValueType(Bindable type) {
	Appearance savedAppearance = _baseAppearance;
	if (type instanceof CharacterPrototype)
	    this.setCurrentAppearance(_baseAppearance);
	else
	    this.setCurrentAppearance(type.makeAppearance(_baseAppearance));
	_baseAppearance = savedAppearance;
    }
    
    public void delete() {
	this.delete(false);
	if (_referent != null)
	    _referent.setAfterBoardCharacter(null);
	_baseAppearance = null;
    }
    
    public void edit() {
	if (!RuleEditor.isRecordingOrEditing())
	    PlaywriteDialog.warning("dialog ebo");
	else {
	    CocoaCharacter realCharacter = dereference();
	    if (realCharacter == null || realCharacter == this) {
		super.edit();
		RuleEditor ruleEditor = RuleEditor.getRuleEditor();
		ruleEditor.addDependantWindow(this.getEditor());
		this.getEditor().setOwner(ruleEditor);
	    } else
		realCharacter.edit();
	}
    }
    
    CocoaCharacter dereference() {
	return findOriginal().dereference();
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return (_referent.refersTo(obj) || _baseAppearance == obj
		|| this.getCurrentAppearance() == obj);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	GCAlias newAlias = (GCAlias) map.get(this);
	if (newAlias != null)
	    return newAlias;
	GeneralizedCharacter newGC = (GeneralizedCharacter) map.get(_referent);
	if (newGC == null)
	    newGC = (GeneralizedCharacter) _referent.copy(map, fullCopy);
	newAlias = new GCAlias(newGC);
	map.put(this, newAlias);
	newAlias.copyDataFrom(this, map, fullCopy);
	World newWorld = (World) map.get(this.getWorld());
	if (newWorld == null) {
	    newAlias._baseAppearance
		= getSimilarAppearance(_baseAppearance, newAlias, map);
	    newAlias.setCurrentAppearance
		(getSimilarAppearance(this.getCurrentAppearance(), newAlias,
				      map));
	} else {
	    newAlias._baseAppearance
		= (Appearance) _baseAppearance.copy(map, false);
	    newAlias.setCurrentAppearance((Appearance)
					  this.getCurrentAppearance()
					      .copy(map, false));
	}
	return newAlias;
    }
    
    private Appearance getSimilarAppearance
	(Appearance oldAppearance, CocoaCharacter newOwner, Hashtable map) {
	Appearance newAppearance = (Appearance) map.get(oldAppearance);
	if (newAppearance == null) {
	    newAppearance = newOwner.findSimilarAppearance(oldAppearance);
	    map.put(oldAppearance, newAppearance);
	}
	return newAppearance;
    }
    
    public PlaywriteView createView() {
	GeneralizedCharacter gch = findOriginal();
	PlaywriteView aliasView = gch.createView();
	aliasView.setModelObject(this);
	return aliasView;
    }
    
    public void halo() {
	if (this.hasCharacterViews())
	    this.getCharacterViewManager().hilite();
	if (this.hasIconViews())
	    this.getIconViewManager().hilite();
	_haloed = true;
	if (_referent != null)
	    _referent.haloFromGCAlias();
    }
    
    public void unhalo() {
	if (this.hasCharacterViews())
	    this.getCharacterViewManager().unhilite();
	if (this.hasIconViews())
	    this.getIconViewManager().unhilite();
	_haloed = false;
	if (_referent != null)
	    _referent.unhaloFromGCAlias();
    }
    
    void haloFromGC() {
	if (!_haloed)
	    halo();
    }
    
    void unhaloFromGC() {
	if (_haloed)
	    unhalo();
    }
    
    public Enumeration getLegalValues(PopupVariable popupVariable) {
	CharacterPrototype proto = this.getPrototype();
	if (proto != null)
	    return proto.getLegalValues(popupVariable);
	return super.getLegalValues(popupVariable);
    }
    
    public Object legalValueForValue(PopupVariable popupVariable,
				     Object value) {
	CharacterPrototype proto = this.getPrototype();
	if (proto != null)
	    return proto.legalValueForValue(popupVariable, value);
	return super.legalValueForValue(popupVariable, value);
    }
    
    public String toString() {
	return "<GCAlias for " + _referent + ">";
    }
}
