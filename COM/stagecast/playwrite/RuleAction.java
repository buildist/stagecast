/* RuleAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public abstract class RuleAction extends IndexedObject
    implements Copyable, Externalizable, Rule.Content,
	       ResourceIDs.RuleActionIDs, Summarizable, Target, Worldly
{
    public static final Condition SUCCESS
	= new Condition("COM.stagecast.playwrite.RuleAction.SUCCESS");
    public static final Condition FAILURE
	= new Condition("COM.stagecast.playwrite.RuleAction.FAILURE");
    public static final Condition NOOP
	= new Condition("COM.stagecast.playwrite.RuleAction.NO-OP");
    protected static final int spacing = 8;
    static final String EXECUTE = "execute";
    static final String UNDO = "undo";
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108753380658L;
    private GeneralizedCharacter _target;
    private Rule _rule = null;
    private transient int _clockTick;
    private transient int _actionNumber;
    
    public static class RuleExecutionArguments
    {
	public CharacterContainer container;
	public int baseX;
	public int baseY;
	
	RuleExecutionArguments(CharacterContainer container, int x, int y) {
	    this.container = container;
	    baseX = x;
	    baseY = y;
	}
    }
    
    GeneralizedCharacter getTarget() {
	return _target;
    }
    
    void setTarget(GeneralizedCharacter gch) {
	_target = gch;
    }
    
    final GeneralizedCharacter getSelf() {
	return _rule.getSelf();
    }
    
    public final Rule getRule() {
	return _rule;
    }
    
    public final void setRule(Rule r) {
	_rule = r;
    }
    
    final int getClockTick() {
	return _clockTick;
    }
    
    final void setClockTick(int tick) {
	_clockTick = tick;
    }
    
    final int getActionNumber() {
	return _actionNumber;
    }
    
    final void setActionNumber(int i) {
	_actionNumber = i;
    }
    
    public World getWorld() {
	if (_rule == null)
	    return null;
	return _rule.getWorld();
    }
    
    final ToolHandler.ToolArbiter getToolArbiter() {
	return getRule().getToolArbiter();
    }
    
    public abstract Object execute(CharacterContainer charactercontainer,
				   int i, int i_0_);
    
    public abstract void undo();
    
    public void updateAfterBoard(AfterBoard afterBoard) {
	/* empty */
    }
    
    public boolean wantsRepaint() {
	return false;
    }
    
    final Object findReferenceTo(ReferencedObject obj) {
	return refersTo(obj) ? this : null;
    }
    
    public boolean refersTo(ReferencedObject obj) {
	return _target != null && _target.refersTo(obj);
    }
    
    public boolean wantsToolNow(Tool toolType) {
	return RuleEditor.isRecordingOrEditing();
    }
    
    public abstract PlaywriteView createView();
    
    protected PlaywriteView createView(String theKind, GeneralizedCharacter gc,
				       String thePrep, int x, int y) {
	LineView view = new LineView(8);
	view.addSubview(LineView.makeLabel(theKind));
	gc.makeView(view);
	if (thePrep != null) {
	    view.addSubview(Util.makeLabel(thePrep));
	    view.addSubview(new LocationView(getRule().getBeforeBoard(), gc, x,
					     y));
	}
	view.setModelObject(this);
	view.sizeToMinSize();
	return view;
    }
    
    public Object copy() {
	return this.copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	Hashtable map = new Hashtable(50);
	if (getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	return this.copy(map, true);
    }
    
    public void performCommand(String command, Object data) {
	if (command == "execute") {
	    RuleExecutionArguments args = (RuleExecutionArguments) data;
	    execute(args.container, args.baseX, args.baseY);
	} else if (command == "undo")
	    undo();
	else
	    throw new PlaywriteInternalError("Bad command: " + command);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeObject(_target);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(RuleAction.class);
	_target = (GeneralizedCharacter) in.readObject();
	switch (version) {
	case 1:
	    in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 2);
	case 2:
	    /* empty */
	}
    }
    
    public void summarize(Summary s) {
	s.writeText(toString());
    }
    
    public String toString() {
	return "<" + this.getClass().getName() + ">";
    }
}
