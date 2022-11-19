/* CharacterInstance - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class CharacterInstance extends CocoaCharacter
    implements Externalizable, Movable, ResourceIDs.NameGeneratorIDs,
	       Selectable, Target
{
    static final int storeVersion = 3;
    static final long serialVersionUID = -3819410108756395314L;
    static final String SYNC_ACTION_HIGHLIGHT = "SAHL";
    static final String SYNC_ACTION_UNHIGHLIGHT = "SAUHL";
    private long _instanceCount = 0L;
    transient GeneralizedCharacter _boundBy;
    
    public CharacterInstance(CharacterPrototype prototype) {
	fillInObject(prototype);
    }
    
    public CharacterInstance() {
	/* empty */
    }
    
    void fillInObject(CharacterPrototype prototype) {
	this.setPrototype(prototype);
	_instanceCount = prototype.getNextInstanceCount();
	ObjectSieve sieve = prototype.getWorld().getObjectSieve();
	if (sieve != null)
	    sieve.creation(this);
	VariableSieve vs = prototype.getWorld().getVariableSieve();
	if (vs != null) {
	    Enumeration vars = this.getVariables();
	    while (vars.hasMoreElements()) {
		Variable v = (Variable) vars.nextElement();
		if (v.isUserVariable()
		    && v.getActualValue(this) == Variable.UNBOUND)
		    vs.strain(this, v, v.getValue(this));
	    }
	}
	this.setCurrentAppearance(prototype.getCurrentAppearance());
    }
    
    public void halo() {
	if (this.hasCharacterViews())
	    this.getCharacterViewManager().hilite();
	if (this.hasIconViews())
	    this.getIconViewManager().hilite();
    }
    
    public void unhalo() {
	if (this.hasCharacterViews())
	    this.getCharacterViewManager().unhilite();
	if (this.hasIconViews())
	    this.getIconViewManager().unhilite();
    }
    
    void setH(int i) {
	super.setH(i);
	boolean timeoutBox
	    = this.getContainer() == this.getWorld().getTimeout();
	this.getPrototype().horizVar
	    .setValue(this, new Long((long) (timeoutBox ? 0 : i)));
    }
    
    void setV(int i) {
	super.setV(i);
	boolean timeoutBox
	    = this.getContainer() == this.getWorld().getTimeout();
	this.getPrototype().vertVar.setValue(this, new Long((long) (timeoutBox
								    ? 0 : i)));
    }
    
    public void setContainer(GenericContainer c) {
	GenericContainer oldContainer = this.getContainer();
	super.setContainer(c);
	if (c instanceof Stage && oldContainer != c)
	    this.getPrototype().stageVar.setValue(this, c);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	CharacterInstance newCharacter = null;
	if (map != null)
	    newCharacter = (CharacterInstance) map.get(this);
	if (newCharacter != null)
	    return newCharacter;
	World oldWorld = this.getWorld();
	World newWorld = map == null ? null : (World) map.get(oldWorld);
	CharacterPrototype oldPrototype = this.getPrototype();
	CharacterPrototype newPrototype
	    = map == null ? null : (CharacterPrototype) map.get(oldPrototype);
	if (!fullCopy && newWorld == null)
	    return this;
	return this.copy(map, fullCopy, "character instance");
    }
    
    public boolean allowDelete() {
	return true;
    }
    
    public void delete() {
	ObjectSieve sieve = this.getPrototype().getWorld().getObjectSieve();
	if (sieve != null)
	    sieve.destruction(this);
	_boundBy = null;
	super.delete();
    }
    
    public void undelete() {
	super.undelete();
	this.setVisibility(true);
    }
    
    void deferredDelete() {
	this.setVisibility(false);
	((Stage) this.getContainer()).deferredDelete(this);
    }
    
    final void execute() {
	this.getPrototype().getMainSubroutine().matches(this);
    }
    
    Vector adjacentSquare(int deltaH, int deltaV) {
	return ((Board) this.getContainer()).getSquare(this.getH() + deltaH,
						       this.getV() + deltaV);
    }
    
    public Rect getRuleDefineRect() {
	if (this.getContainer() instanceof Stage)
	    return ((Stage) this.getContainer()).squaresOccupied(this);
	return null;
    }
    
    String getUnboundName() {
	CharacterPrototype prototype = this.getPrototype();
	Object[] params = { prototype.getName(), new Long(_instanceCount) };
	return Resource.getTextAndFormat("Generator cin3", params);
    }
    
    protected Object getDirectH() {
	if (this.getContainer() instanceof Stage)
	    return super.getDirectH();
	return new Integer(0);
    }
    
    protected void setDirectH(Number h) {
	if (this.getWorld().getTimeout() != this.getContainer()
	    && !this.isDeleted()) {
	    int x = h.intValue();
	    if (x != this.getH()) {
		CharacterContainer cc = this.getCharContainer();
		if (cc instanceof Stage)
		    cc.relocate(this, x, this.getV(), -1);
		else
		    setH(this.getH());
	    }
	}
    }
    
    protected Object getDirectV() {
	if (this.getContainer() instanceof Stage)
	    return super.getDirectV();
	return new Integer(0);
    }
    
    protected void setDirectV(Number v) {
	if (this.getWorld().getTimeout() != this.getContainer()
	    && !this.isDeleted()) {
	    int y = v.intValue();
	    if (y != this.getV()) {
		CharacterContainer cc = this.getCharContainer();
		if (cc instanceof Stage)
		    cc.relocate(this, this.getH(), y, -1);
		else
		    setV(this.getV());
	    }
	}
    }
    
    protected void setDirectStage(Object stage) {
	CharacterContainer oldCC = this.getCharContainer();
	CharacterContainer newCC = (CharacterContainer) stage;
	if (oldCC != newCC && !this.isDeleted()) {
	    if (oldCC != null)
		oldCC.remove(this);
	    if (newCC != null)
		newCC.add(this, this.getH(), this.getV(), -1);
	}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeInt(this.getH());
	out.writeInt(this.getV());
	out.writeInt(this.getZ());
	out.writeLong(_instanceCount);
	this.getVariableList().writeListValues(out, this);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	readCharacterInstance(in);
    }
    
    protected final void readCharacterInstance(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version
	    = ((WorldInStream) in).loadVersion(CharacterInstance.class);
	super.readExternal(in);
	int h = in.readInt();
	int v = in.readInt();
	int z = in.readInt();
	ObjectSieve sieve = this.getPrototype().getWorld().getObjectSieve();
	if (sieve != null)
	    sieve.creation(this);
	_instanceCount = 42L;
	CharacterContainer cc = null;
	switch (version) {
	case 1:
	    cc = (CharacterContainer) in.readObject();
	    break;
	case 2:
	    super.setH(h);
	    super.setV(v);
	    break;
	case 3:
	    super.setH(h);
	    super.setV(v);
	    _instanceCount = in.readLong();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 3);
	}
	this.getVariableList().readListValues(in, this);
	this.setCurrentAppearance(this.getCurrentAppearance());
	switch (version) {
	case 1:
	    cc.add(this, h, v, z);
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 3);
	case 2:
	case 3:
	    /* empty */
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
    
    public void performCommand(String command, Object object) {
	if (command.equals("SAHL"))
	    halo();
	else if (command.equals("SAUHL"))
	    unhalo();
    }
}
