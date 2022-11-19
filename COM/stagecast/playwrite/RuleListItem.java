/* RuleListItem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public abstract class RuleListItem
    implements Debug.Constants, Named, ResourceIDs.CommandIDs,
	       ResourceIDs.DialogIDs, ResourceIDs.NameGeneratorIDs, Selectable,
	       Viewable, Worldly
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108753315122L;
    private boolean enabled = true;
    private boolean editable = true;
    private int index = 0;
    private Subroutine subroutine = null;
    private boolean locked = false;
    private String password = null;
    private transient ViewManager viewManager = null;
    private transient boolean selected = false;
    
    static interface IterationProcessor
    {
	public Object processItem(RuleListItem rulelistitem, Object object);
	
	public boolean done(Object object);
    }
    
    final boolean isEnabled() {
	return enabled;
    }
    
    final void setEnabled(boolean b) {
	enabled = b;
    }
    
    final boolean isEditable() {
	return editable;
    }
    
    final void setEditable(boolean b) {
	editable = b;
    }
    
    final boolean hasViews() {
	if (viewManager == null)
	    return false;
	return viewManager.hasViews();
    }
    
    final ViewManager getViewManager() {
	if (viewManager == null)
	    viewManager = new ViewManager(this);
	return viewManager;
    }
    
    int getRuleCount() {
	return 0;
    }
    
    int getItemCount() {
	return 1;
    }
    
    final boolean matches(CharacterInstance characterInstance) {
	if (enabled)
	    return matchAndExecute(characterInstance);
	return false;
    }
    
    protected abstract boolean matchAndExecute
	(CharacterInstance characterinstance);
    
    boolean continueExecution(CharacterInstance characterInstance) {
	return false;
    }
    
    boolean refersTo(ReferencedObject obj) {
	return findReferenceTo(obj) != null;
    }
    
    Object findReferenceTo(ReferencedObject obj) {
	return null;
    }
    
    Rule findRuleReferringTo(ReferencedObject obj) {
	return null;
    }
    
    int countRulesReferringTo(ReferencedObject obj) {
	return 0;
    }
    
    public final World getWorld() {
	return getOwner().getWorld();
    }
    
    CharacterPrototype getOwner() {
	if (subroutine != null)
	    return subroutine.getOwner();
	return null;
    }
    
    Subroutine getSubroutine() {
	return subroutine;
    }
    
    void setSubroutine(Subroutine sub) {
	subroutine = sub;
    }
    
    final Subroutine getMainSubroutine() {
	return getOwner().getMainSubroutine();
    }
    
    final int getIndex() {
	return index;
    }
    
    final void setIndex(int i) {
	index = i;
    }
    
    final void showInCharacterWindow(CocoaCharacter character) {
	getSubroutine().openWindowToItem(this, character);
    }
    
    final PlaywriteView getView(CocoaCharacter character) {
	SubroutineScrap subroutineScrap
	    = getSubroutine().getViewFor(character);
	if (subroutineScrap != null) {
	    Slot slot
		= (Slot) subroutineScrap.getRuleViews().elementAt(getIndex());
	    if (slot != null)
		return slot.getScrap();
	}
	return null;
    }
    
    boolean moveTo(Subroutine newSubroutine, CocoaCharacter newSelf,
		   int index) {
	if (subroutine == null) {
	    Debug.print(false, "Can't move RLI w/null subroutine");
	    return true;
	}
	if (subroutine == newSubroutine) {
	    if (index == this.index || index == this.index + 1)
		return false;
	    if (index > this.index)
		index--;
	}
	if (newSubroutine.getOwner() == getOwner()) {
	    getMainSubroutine().disableDrawingOnAllViews();
	    subroutine.remove(this);
	    newSubroutine.add(this, index);
	    getMainSubroutine().reenableDrawingOnAllViews();
	    return true;
	}
	return copyTo(newSubroutine, newSelf, index);
    }
    
    boolean copyTo(Subroutine newSubroutine, CocoaCharacter self, int index) {
	World oldWorld = getWorld();
	World newWorld = newSubroutine.getWorld();
	CharacterPrototype oldPrototype = getOwner();
	CharacterPrototype newPrototype = newSubroutine.getOwner();
	if (!newWorld.isOkToCopyWithDialog(this))
	    return false;
	Hashtable map = new Hashtable(50);
	if (oldWorld != newWorld)
	    map.put(oldWorld, newWorld);
	if (oldPrototype != newPrototype)
	    map.put(oldPrototype, newPrototype);
	RuleListItem newRLI = (RuleListItem) copy(map, true);
	if (newSubroutine == getSubroutine()) {
	    Object[] params = { newRLI.getName() };
	    newRLI.setName(Resource.getTextAndFormat("Generator cin", params));
	}
	if (newSubroutine.isMainSubroutine())
	    newPrototype.add(newRLI, index);
	else
	    newSubroutine.add(newRLI, index);
	return true;
    }
    
    Object iterate(IterationProcessor iterationProcessor, Object lastValue) {
	return lastValue;
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
    
    public Object copy(Hashtable map, boolean fullCopy) {
	RuleListItem newItem = (RuleListItem) map.get(this);
	if (newItem == null)
	    throw new PlaywriteInternalError("Illegal copy of " + this);
	newItem.enabled = enabled;
	newItem.editable = editable;
	newItem.locked = locked;
	newItem.password = password;
	return newItem;
    }
    
    final boolean worldIsStopped() {
	if (getWorld() != null)
	    return getWorld().getState() == World.STOPPED;
	return true;
    }
    
    final boolean dragOrToolPermitted() {
	return worldIsStopped();
    }
    
    public boolean allowDelete() {
	if (dragOrToolPermitted())
	    return true;
	PlaywriteDialog.warning(Resource.getText("dialog dltNP"));
	return false;
    }
    
    public void delete() {
	if (RuleEditor.ruleBeingDefined() == this)
	    RuleEditor.getRuleEditor().performCommand("command c", this);
	getSubroutine().remove(this);
    }
    
    public void undelete() {
	/* empty */
    }
    
    public PlaywriteView createView() {
	return null;
    }
    
    abstract RuleListItemView createScrap(CocoaCharacter cocoacharacter);
    
    void addView(CocoaCharacter self, RuleListItemView view) {
	getViewManager().addView(view);
    }
    
    void removeView(RuleListItemView scrap) {
	getViewManager().removeView(scrap);
    }
    
    public void highlightForSelection() {
	selected = true;
	getViewManager().hilite();
    }
    
    public void unhighlightForSelection() {
	selected = false;
	getViewManager().unhilite();
    }
    
    final void disableDrawingOnAllViews() {
	getViewManager().disableDrawing();
    }
    
    final void reenableDrawingOnAllViews() {
	getViewManager().reenableDrawing();
    }
    
    protected void finalize() throws Throwable {
	Debug.print("debug.gc", " Reclaiming RuleListItem ", this);
	super.finalize();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeBoolean(enabled);
	out.writeBoolean(editable);
	out.writeInt(index);
	out.writeObject(subroutine);
	out.writeBoolean(locked);
	out.writeObject(password);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	enabled = in.readBoolean();
	editable = in.readBoolean();
	index = in.readInt();
	subroutine = (Subroutine) in.readObject();
	locked = in.readBoolean();
	password = (String) in.readObject();
    }
    
    public abstract String getName();
    
    public abstract void setName(String string);
}
