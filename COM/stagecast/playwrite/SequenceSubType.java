/* SequenceSubType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class SequenceSubType
    implements Debug.Constants, ResourceIDs.SubroutineTypeIDs, SubroutineType
{
    private static final String TYPE_NAME = "type dit";
    private static final int fingerBase = 0;
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108752528690L;
    private FingerVariable fingerVariable = null;
    private Subroutine sub = null;
    private transient boolean succeeded;
    private transient int finger;
    private transient CharacterInstance self;
    
    static void initExtension() {
	Subroutine.addSubroutineType(Resource.getText("type dit"),
				     SequenceSubType.class);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(sub);
	ASSERT.isNotNull(fingerVariable);
	out.writeObject(sub);
	out.writeObject(fingerVariable);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	sub = (Subroutine) in.readObject();
	fingerVariable = (FingerVariable) in.readObject();
	fingerVariable.setSubroutine(sub);
    }
    
    public String getTypeName() {
	return Resource.getText("type dit");
    }
    
    public void setSubroutine(Subroutine subroutine) {
	if (subroutine == null && sub != null && sub.hasViews()) {
	    final FingerVariable fv = fingerVariable;
	    sub.getViewManager().updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    ((SubroutineScrap) view).removeFingerView(fv);
		}
	    }, null);
	    sub = null;
	    fv.deleteUserVariable(fv.getListOwner());
	    fingerVariable = null;
	} else {
	    sub = subroutine;
	    CocoaCharacter owner = subroutine.getOwner();
	    if (owner != null) {
		addFingerVariable(owner);
		final FingerVariable fv = fingerVariable;
		sub.getViewManager()
		    .updateViews(new ViewManager.ViewUpdater() {
		    public void updateView(Object view, Object value) {
			((SubroutineScrap) view).addFingerView(fv.createView(),
							       fv);
		    }
		}, null);
	    } else
		Debug.print
		    ("debug.subroutine", "Subroutine ", sub,
		     " doesn't have an owner but has a SequenceSubroutine");
	}
    }
    
    public Subroutine getSubroutine() {
	return sub;
    }
    
    private void addFingerVariable(CocoaCharacter self) {
	if (fingerVariable == null)
	    fingerVariable = new FingerVariable(getSubroutine());
	self.getPrototype().add(fingerVariable);
    }
    
    void setFingerVariable(CocoaCharacter self, int value) {
	World world = self.getWorld();
	if (self instanceof CharacterInstance) {
	    RuleAction putAction
		= new FVPutAction(self, fingerVariable, new Integer(value));
	    if (world.isRunning())
		world.executeAction(putAction, null, 0, 0);
	    else
		world.doManualAction(putAction);
	} else
	    fingerVariable.setValue(self, new Integer(value));
    }
    
    public SubroutineScrap createView(CocoaCharacter self) {
	addFingerVariable(self);
	SubroutineScrap view = new SubroutineScrap(getSubroutine(), self);
	view.addFingerView(fingerVariable.createView(), fingerVariable);
	sub.addView(self, view);
	return view;
    }
    
    public boolean prepareToExecute(CharacterInstance self) {
	addFingerVariable(self);
	this.self = self;
	return true;
    }
    
    public RuleListItem getNextRule(boolean previousSucceeded,
				    int previousIndex) {
	if (previousIndex < 0) {
	    Object val = fingerVariable.getValue(self);
	    if (val instanceof Integer)
		finger = ((Integer) val).intValue();
	    else {
		setFingerVariable(self, 0);
		finger = 0;
		Debug.print("debug.subroutine", "WARNING: SequenceSubroutine ",
			    sub.getName(), " had finger set to a non-number!");
	    }
	    RuleListItem thing = null;
	    RuleListItem nonComment = null;
	    for (int i = finger; i < sub.numberOfRules(); i++) {
		thing = sub.getRule(i);
		if (thing.isEnabled()) {
		    nonComment = thing;
		    finger = i;
		    break;
		}
	    }
	    if (nonComment == null) {
		for (int i = 0; i < finger; i++) {
		    thing = sub.getRule(i);
		    if (!(thing instanceof Comment)) {
			nonComment = thing;
			finger = i;
			break;
		    }
		}
	    }
	    if (nonComment != null) {
		setFingerVariable(self, finger);
		return nonComment;
	    }
	    return null;
	}
	succeeded = previousSucceeded;
	if (finger < sub.numberOfRules()) {
	    RuleListItem r = sub.getRule(finger);
	    if (r instanceof Subroutine
		&& ((Subroutine) r).getType() instanceof DoAllSubType)
		succeeded = true;
	}
	return null;
    }
    
    public boolean subroutineMatched(CharacterInstance self) {
	if (succeeded) {
	    finger = finger + 1;
	    if (finger == sub.numberOfRules())
		finger = 0;
	    setFingerVariable(self, finger);
	}
	return succeeded;
    }
    
    public boolean continueExecution(CharacterInstance self) {
	return false;
    }
    
    public String toString() {
	return "SequenceSubroutine";
    }
}
