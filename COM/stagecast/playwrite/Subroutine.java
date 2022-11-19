/* Subroutine - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class Subroutine extends RuleListItem
    implements Debug.Constants, Externalizable, ResourceIDs.SubroutineTypeIDs
{
    static final int storeVersion = 3;
    static final long serialVersionUID = -3819410108756067634L;
    private static Vector typeNames = new Vector(4);
    private static Vector typeClasses = new Vector(4);
    private String name = "";
    private String comment = null;
    private Vector rules;
    private Pretest pretest;
    private CharacterPrototype owner = null;
    private SubroutineType type = null;
    private transient Hashtable ownerTable = null;
    
    static {
	NormalSubType.initExtension();
	RandomSubType.initExtension();
	DoAllSubType.initExtension();
	SequenceSubType.initExtension();
    }
    
    public static Subroutine createNormalSubroutine() {
	Subroutine subroutine = new Subroutine();
	subroutine.setType(new NormalSubType());
	return subroutine;
    }
    
    public static Subroutine createRandomSubroutine() {
	Subroutine subroutine = new Subroutine();
	subroutine.setType(new RandomSubType());
	return subroutine;
    }
    
    public static Subroutine createDoAllSubroutine() {
	Subroutine subroutine = new Subroutine();
	subroutine.setType(new DoAllSubType());
	return subroutine;
    }
    
    public static Subroutine createSequenceSubroutine() {
	Subroutine subroutine = new Subroutine();
	subroutine.setType(new SequenceSubType());
	return subroutine;
    }
    
    public Subroutine() {
	rules = new Vector(1);
    }
    
    protected final boolean matchAndExecute(CharacterInstance self) {
	boolean success = false;
	if (!checkPretest(self))
	    return false;
	RuleListItem rule = null;
	int lastindex = -1;
	if (!type.prepareToExecute(self))
	    return false;
	CharacterWindow characterWindow = self.getEditor();
	if (characterWindow == null || !characterWindow.isVisible()) {
	    while ((rule = type.getNextRule(success, lastindex)) != null) {
		lastindex = rule.getIndex();
		success = rule.matches(self);
		if (success)
		    success = rule.continueExecution(self) ^ true;
	    }
	} else {
	    World world = self.getWorld();
	    if (isMainSubroutine())
		resetSubroutineLights(self);
	    while ((rule = type.getNextRule(success, lastindex)) != null) {
		lastindex = rule.getIndex();
		if (!rule.isEnabled())
		    success = false;
		else {
		    if (wantsDebug(self, rule, world)) {
			world.setForceUpdateAfterEveryChange(true);
			world.forceRepaint();
			world.suspendForDebug();
		    }
		    if (self.isInvisible())
			return false;
		    try {
			success = rule.matches(self);
		    } catch (Exception e) {
			e.printStackTrace();
		    } finally {
			world.setForceUpdateAfterEveryChange(false);
		    }
		    setLightOnRule(self, rule, success);
		    if (success)
			success = rule.continueExecution(self) ^ true;
		}
	    }
	}
	return type.subroutineMatched(self);
    }
    
    private void setLightOnRule(final CharacterInstance self,
				final RuleListItem rule,
				final boolean success) {
	Target t = new Target() {
	    public void performCommand(String s, Object data) {
		CharacterWindow cWin = self.getEditor();
		if (cWin != null && !cWin.hasClosed()) {
		    int index = rule.getIndex();
		    SubroutineScrap subroutineScrap = getViewFor(self);
		    Vector ruleViews = subroutineScrap.getRuleViews();
		    Slot ruleSlot = (Slot) ruleViews.elementAt(index);
		    ruleSlot.setLight(success);
		}
	    }
	};
	PlaywriteRoot.app().performCommandAndWait(t, null, null);
    }
    
    private void resetSubroutineLights(final CharacterInstance self) {
	Target t = new Target() {
	    public void performCommand(String command, Object data) {
		CharacterWindow cWin = self.getEditor();
		if (cWin != null && !cWin.hasClosed()) {
		    SubroutineScrap subroutineScrap = getViewFor(self);
		    Vector ruleViews = subroutineScrap.getRuleViews();
		    Slot ruleSlot = null;
		    subroutineScrap.resetSubroutineLights();
		}
	    }
	};
	PlaywriteRoot.app().performCommandAndWait(t, null, null);
    }
    
    private boolean wantsDebug(final CharacterInstance self,
			       final RuleListItem rule, final World world) {
	boolean[] breakpointFlag = new boolean[1];
	final RuleListItem currentRule = rule;
	Target t = new Target() {
	    public void performCommand(String command, Object data) {
		CharacterWindow cWin = self.getEditor();
		if (cWin != null && !cWin.hasClosed()) {
		    int index = rule.getIndex();
		    SubroutineScrap subroutineScrap = getViewFor(self);
		    Vector ruleViews = subroutineScrap.getRuleViews();
		    Slot ruleSlot = null;
		    if (ruleViews.size() > 0) {
			ruleSlot = (Slot) ruleViews.elementAt(index);
			if (!world.isSuspendedForDebug()
			    && (ruleSlot.hasBreakpoint()
				|| cWin.hasStepRuleButton())) {
			    Debug.print("debug.debugging", "Stop on ", self,
					"'s ", currentRule);
			    ((boolean[]) data)[0] = true;
			    Subroutine.this.debugItem(currentRule, self,
						      ruleSlot, world);
			    Debug.print("debug.debugging", "Resume on ", self,
					"'s ", currentRule);
			}
		    }
		}
	    }
	};
	PlaywriteRoot.app().performCommandAndWait(t, null, breakpointFlag);
	return breakpointFlag[0];
    }
    
    boolean continueExecution(CharacterInstance characterInstance) {
	return type.continueExecution(characterInstance);
    }
    
    Object findReferenceTo(ReferencedObject obj) {
	for (int i = 0; i < rules.size(); i++) {
	    RuleListItem item = (RuleListItem) rules.elementAt(i);
	    Object ref = item.findReferenceTo(obj);
	    if (ref != null)
		return ref;
	}
	return null;
    }
    
    Rule findRuleReferringTo(ReferencedObject obj) {
	for (int i = 0; i < rules.size(); i++) {
	    RuleListItem item = (RuleListItem) rules.elementAt(i);
	    if (item.refersTo(obj)) {
		if (item instanceof Rule)
		    return (Rule) item;
		return item.findRuleReferringTo(obj);
	    }
	}
	return null;
    }
    
    int countRulesReferringTo(ReferencedObject obj) {
	int count = 0;
	for (int i = 0; i < rules.size(); i++) {
	    RuleListItem item = (RuleListItem) rules.elementAt(i);
	    count += item.countRulesReferringTo(obj);
	}
	if (pretest != null)
	    count += pretest.countRulesReferringTo(obj);
	return count;
    }
    
    int getRuleCount() {
	int count = 0;
	for (int i = 0; i < rules.size(); i++) {
	    RuleListItem item = (RuleListItem) rules.elementAt(i);
	    count += item.getRuleCount();
	}
	return count;
    }
    
    int getItemCount() {
	int count = super.getItemCount();
	int i = rules.size();
	while (i-- > 0)
	    count += ((RuleListItem) rules.elementAt(i)).getItemCount();
	return count;
    }
    
    Object iterate(RuleListItem.IterationProcessor ruleUpdater,
		   Object lastValue) {
	Object temp = null;
	Pretest pretest = getPretest();
	if (pretest != null) {
	    lastValue = pretest.iterate(ruleUpdater, lastValue);
	    addPretest(pretest);
	    if (ruleUpdater.done(lastValue))
		return lastValue;
	}
	for (int i = 0; i < rules.size(); i++) {
	    RuleListItem item = (RuleListItem) rules.elementAt(i);
	    lastValue = item.iterate(ruleUpdater, lastValue);
	    if (ruleUpdater.done(lastValue))
		return lastValue;
	}
	return lastValue;
    }
    
    Subroutine findBadSubroutine(CharacterPrototype correctPrototype) {
	for (int i = 0; i < rules.size(); i++) {
	    Object item = rules.elementAt(i);
	    if (item instanceof Subroutine) {
		Subroutine containedSub = (Subroutine) item;
		if (containedSub.owner != null
		    && containedSub.owner != correctPrototype)
		    return containedSub;
		containedSub.setOwner(correctPrototype);
		containedSub.findBadSubroutine(correctPrototype);
	    }
	}
	return null;
    }
    
    private SubroutineScrap openAllEnclosingSubroutines
	(CocoaCharacter character) {
	SubroutineScrap subScrap = getViewFor(character);
	if (isMainSubroutine())
	    return subScrap;
	subScrap.showRules();
	this.getSubroutine().openAllEnclosingSubroutines(character);
	return subScrap;
    }
    
    CharacterWindow openWindowToItem(RuleListItem item,
				     CocoaCharacter character) {
	character.edit();
	CharacterWindow window = character.getEditor();
	window.moveToFront();
	SubroutineScrap subscrap = openAllEnclosingSubroutines(character);
	if (item instanceof Pretest)
	    subscrap.hilitePretestView();
	else
	    subscrap.showRule(item.getIndex());
	return window;
    }
    
    private void debugItem(final RuleListItem item,
			   final CharacterInstance self, final Slot itemSlot,
			   World world) {
	Target thunk = new Target() {
	    public void performCommand(String command, Object agr) {
		CharacterWindow characterWindow = openWindowToItem(item, self);
		itemSlot.flashYellowLight();
		characterWindow.showStepRuleButton(itemSlot);
	    }
	};
	Application.application().performCommandAndWait(thunk, null, null);
    }
    
    boolean moveTo(Subroutine newSubroutine, CocoaCharacter newSelf,
		   int index) {
	Subroutine testSubroutine = newSubroutine;
	do {
	    if (this == testSubroutine)
		return false;
	    testSubroutine = testSubroutine.getSubroutine();
	} while (testSubroutine != null);
	return super.moveTo(newSubroutine, newSelf, index);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Subroutine newSubroutine = (Subroutine) map.get(this);
	if (newSubroutine != null)
	    return newSubroutine;
	CharacterPrototype oldPrototype = getOwner();
	CharacterPrototype newPrototype
	    = (CharacterPrototype) map.get(oldPrototype);
	if (newPrototype == null)
	    newPrototype = oldPrototype;
	else if (newPrototype.isProxy() && fullCopy)
	    newPrototype.makeReal(oldPrototype, map);
	newSubroutine = new Subroutine();
	map.put(this, newSubroutine);
	newSubroutine.setName(name);
	newSubroutine.setComment(comment);
	newSubroutine.setOwner(newPrototype);
	try {
	    newSubroutine
		.setType((SubroutineType) type.getClass().newInstance());
	} catch (InstantiationException instantiationexception) {
	    return null;
	} catch (IllegalAccessException illegalaccessexception) {
	    return null;
	}
	if (pretest != null) {
	    pretest.setSubroutine(this);
	    newSubroutine.addPretest((Pretest) pretest.copy(map, fullCopy));
	}
	for (int i = 0; i < rules.size(); i++) {
	    RuleListItem newItem
		= ((RuleListItem)
		   ((RuleListItem) rules.elementAt(i)).copy(map, fullCopy));
	    newSubroutine.add(newItem);
	}
	return super.copy(map, fullCopy);
    }
    
    public void highlightForSelection() {
	if (isMainSubroutine())
	    Selection.unselect(this);
	else
	    super.highlightForSelection();
    }
    
    RuleListItemView createScrap(CocoaCharacter self) {
	SubroutineScrap view;
	if (this == self.getMainSubroutine())
	    view = new SubroutineScrap(self);
	else
	    view = type.createView(self);
	addView(self, view);
	return view;
    }
    
    final void addView(CocoaCharacter self, RuleListItemView view) {
	if (ownerTable == null)
	    ownerTable = new Hashtable(5);
	ownerTable.put(self, view);
	super.addView(self, view);
    }
    
    final void removeView(RuleListItemView view) {
	SubroutineScrap subScrap = (SubroutineScrap) view;
	CocoaCharacter owner = subScrap.getCharacter();
	ownerTable.remove(owner);
	super.removeView(view);
    }
    
    final void removeViewFor(CocoaCharacter self) {
	super.removeView((RuleListItemView) ownerTable.get(self));
	ownerTable.remove(self);
    }
    
    final SubroutineScrap getViewFor(CocoaCharacter self) {
	if (ownerTable == null)
	    return null;
	return (SubroutineScrap) ownerTable.get(self);
    }
    
    CharacterPrototype getOwner() {
	if (owner == null)
	    return super.getOwner();
	return owner;
    }
    
    final void setOwner(CharacterPrototype p) {
	if (owner != null && owner != p)
	    throw new BadBackpointerError(p, this);
	owner = p;
    }
    
    void setSubroutine(Subroutine sub) {
	if (sub != null) {
	    if (sub.owner == null)
		setOwner(sub.getOwner());
	    else if (owner == null)
		setOwner(sub.owner);
	    else if (owner != sub.getOwner())
		throw new BadBackpointerError(sub.getOwner(), this);
	}
	super.setSubroutine(sub);
    }
    
    final String getComment() {
	return comment;
    }
    
    final void setComment(String s) {
	comment = s;
    }
    
    final Vector getRules() {
	return rules;
    }
    
    protected final void setRules(Vector v) {
	rules = v;
    }
    
    final Pretest getPretest() {
	return pretest;
    }
    
    public boolean hasPretest() {
	return pretest != null;
    }
    
    final BeforeBoard getBeforeBoard() {
	return pretest.getBeforeBoard();
    }
    
    final void setBeforeBoard(BeforeBoard b) {
	pretest.setBeforeBoard(b);
    }
    
    static final Vector getTypeNames() {
	return typeNames;
    }
    
    static final Vector getTypeClasses() {
	return typeClasses;
    }
    
    final SubroutineType getType() {
	return type;
    }
    
    void setType(SubroutineType type) {
	if (this.type != null)
	    this.type.setSubroutine(null);
	this.type = type;
	type.setSubroutine(this);
    }
    
    final boolean isMainSubroutine() {
	return this.getSubroutine() == null;
    }
    
    public final String getName() {
	return name;
    }
    
    public final void setName(String s) {
	name = s;
    }
    
    public static void addSubroutineType(String name, Class classRef) {
	typeNames.addElement(name);
	typeClasses.addElement(classRef);
    }
    
    public boolean allowDelete() {
	return !isMainSubroutine() && super.allowDelete();
    }
    
    public void delete() {
	if (!isMainSubroutine())
	    super.delete();
    }
    
    public void add(RuleListItem item) {
	add(item, rules.size());
    }
    
    public void add(final RuleListItem item, int index) {
	if (index < 0 || index >= rules.size())
	    rules.addElement(item);
	else
	    rules.insertElementAt(item, index);
	item.setIndex(rules.size() - 1);
	item.setSubroutine(this);
	if (this.hasViews()) {
	    this.getMainSubroutine().disableDrawingOnAllViews();
	    this.getViewManager().updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    SubroutineScrap subScrap = (SubroutineScrap) view;
		    subScrap.addRule(item);
		    subScrap.reorderSlots(rules);
		    subScrap.layoutView(0, 0);
		}
	    }, null);
	    this.getMainSubroutine().reenableDrawingOnAllViews();
	}
	renumberItems(rules, 0);
    }
    
    public void remove(RuleListItem item) {
	remove(rules.indexOfIdentical(item));
    }
    
    public void remove(int index) {
	this.getMainSubroutine().disableDrawingOnAllViews();
	RuleListItem item = (RuleListItem) rules.elementAt(index);
	item.setSubroutine(null);
	rules.removeElementAt(index);
	if (item.hasViews()) {
	    ViewManager viewManager = item.getViewManager();
	    viewManager.updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    ((View) view).superview().removeFromSuperview();
		}
	    }, null);
	    viewManager.removeAllViews();
	}
	renumberItems(rules, index);
	if (this.hasViews())
	    reorderAllViewsSlots(rules);
	this.getMainSubroutine().reenableDrawingOnAllViews();
    }
    
    void reorderAllViewsSlots(final Vector rules) {
	this.getViewManager().updateViews(new ViewManager.ViewUpdater() {
	    public void updateView(Object view, Object value) {
		((SubroutineScrap) view).reorderSlots(rules);
	    }
	}, null);
    }
    
    void removeAllRules() {
	for (int i = rules.size() - 1; i >= 0; i--)
	    remove(i);
    }
    
    public void renumberItems(Vector items, int startIndex) {
	for (int index = startIndex; index < rules.size(); index++) {
	    RuleListItem item = (RuleListItem) rules.elementAt(index);
	    item.setIndex(index);
	}
    }
    
    void addPretest(RuleTest test) {
	if (getPretest() == null)
	    addPretest(new Pretest(getOwner()));
	getPretest().addTest(test);
    }
    
    void addPretest(Pretest pretest) {
	this.pretest = pretest;
	pretest.setSubroutine(this);
	pretest.drBuildBeforeBoard();
	if (this.hasViews()) {
	    final PlaywriteView ptView
		= pretest.createMiniRuleView(Util.ruleScrapColor);
	    this.getViewManager().updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    SubroutineScrap subroutineScrap = (SubroutineScrap) view;
		    subroutineScrap.addPretestView(ptView);
		    subroutineScrap.layoutView(0, 0);
		}
	    }, null);
	}
    }
    
    void removePretest() {
	pretest = null;
	if (this.hasViews())
	    this.getViewManager().updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    ((SubroutineScrap) view).removePretestView();
		}
	    }, null);
    }
    
    /**
     * @deprecated
     */
    PlaywriteView createPretestView() {
	return pretest.createView();
    }
    
    public RuleListItem getRule(int i) {
	return (RuleListItem) rules.elementAt(i);
    }
    
    final int numberOfRules() {
	return rules.size();
    }
    
    boolean convertToType(Class cl) {
	if (this.getWorld().isRunning())
	    return false;
	SubroutineType type;
	try {
	    type = (SubroutineType) cl.newInstance();
	} catch (InstantiationException instantiationexception) {
	    Debug.print("debug.subroutine",
			"Subroutine.convertTo: can't make an instance of ",
			cl);
	    return false;
	} catch (IllegalAccessException illegalaccessexception) {
	    Debug.print("debug.subroutine",
			"Subroutine.convertTo: can't make an instance of ",
			cl);
	    return false;
	}
	setType(type);
	return true;
    }
    
    public boolean checkPretest(CharacterInstance self) {
	if (pretest == null)
	    return true;
	return pretest.matchAndExecute(self);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(name);
	ASSERT.isNotNull(owner);
	ASSERT.isNotNull(type);
	ASSERT.isNotNull(rules);
	super.writeExternal(out);
	out.writeUTF(name);
	out.writeUTF(comment == null ? "" : comment);
	out.writeObject(owner);
	out.writeObject(type);
	out.writeObject(pretest);
	out.writeInt(rules.size());
	Enumeration ruleList = rules.elements();
	while (ruleList.hasMoreElements())
	    out.writeObject(ruleList.nextElement());
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int loadVersion = ((WorldInStream) in).loadVersion(Subroutine.class);
	Class typeClass = null;
	super.readExternal(in);
	name = in.readUTF();
	comment = in.readUTF();
	if (comment.equals(""))
	    comment = null;
	owner = (CharacterPrototype) in.readObject();
	switch (loadVersion) {
	case 1:
	case 2: {
	    String typeName = in.readUTF();
	    try {
		typeClass = Class.forName(typeName);
		setType((SubroutineType) typeClass.newInstance());
	    } catch (Exception e) {
		Debug.print("debug.subroutine", "Subroutine type ", typeClass,
			    " failed -> now NormalSubType");
		Debug.print("debug.subroutine", e);
		setType(new NormalSubType());
	    }
	    break;
	}
	case 3:
	    type = (SubroutineType) in.readObject();
	    break;
	}
	pretest = (Pretest) in.readObject();
	if (pretest != null)
	    pretest.setSubroutine(this);
	int listSize = in.readInt();
	rules = new Vector(listSize);
	while (listSize-- > 0)
	    add((RuleListItem) in.readObject());
    }
    
    public String toString() {
	String typeString;
	if (type == null)
	    typeString = "unspecified";
	else
	    typeString = type.toString();
	return "<" + typeString + " '" + name + "'>";
    }
}
