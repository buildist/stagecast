/* VariableList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;

public class VariableList
    implements Cloneable, GenericContainer, Debug.Constants
{
    private VariableOwner owner;
    private Vector variables;
    private Vector positions;
    private transient ViewManager viewManager = null;
    private transient int newVarIndex = 1;
    
    VariableList(VariableOwner owner) {
	this.owner = owner;
	variables = new Vector(10);
	positions = new Vector(10);
    }
    
    VariableOwner getOwner() {
	return owner;
    }
    
    boolean hasViews() {
	return viewManager != null && viewManager.hasViews();
    }
    
    ViewManager getViewManager() {
	if (viewManager == null)
	    viewManager = new ViewManager(this);
	return viewManager;
    }
    
    void destroyViewsOf(final VariableOwner owner) {
	if (viewManager != null && viewManager.hasViews())
	    viewManager.updateViews(new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object whatever) {
		    VariableListView variableListView
			= (VariableListView) view;
		    if (variableListView.getModelObject() == owner) {
			COM.stagecast.ifc.netscape.application.Window window
			    = variableListView.window();
			if (window != null
			    && window instanceof PlaywriteWindow)
			    Application.application().performCommandAndWait
				(window, PlaywriteWindow.CLOSE, null);
		    }
		}
	    }, null);
    }
    
    public void add(Contained obj) {
	add((Variable) obj);
    }
    
    public void remove(Contained obj) {
	remove((Variable) obj);
    }
    
    public boolean allowRemove(Contained obj) {
	return true;
    }
    
    public void update(Contained obj) {
	/* empty */
    }
    
    int getNewVarIndex() {
	return newVarIndex++;
    }
    
    public Enumeration elements() {
	return variables.elements();
    }
    
    private final Vector getVariables() {
	return variables;
    }
    
    void add(Variable v) {
	addAt(v, new Point(-1, -1));
    }
    
    void addAt(Variable v, Point loc) {
	if (loc == null) {
	    Debug.print("debug.variable", "Location is null for ", v);
	    if (variableLoc(v) == null)
		loc = new Point(-1, -1);
	    else
		loc = variableLoc(v);
	}
	if (variableLoc(v) == null)
	    positions.addElement(loc);
	else
	    moveVariableTo(v, loc);
	if (!variables.containsIdentical(v)) {
	    variables.addElement(v);
	    v.setContainer(this);
	}
	if (variables.size() != positions.size())
	    throw new PlaywriteInternalError
		      ("positions and variables out of sync");
    }
    
    void addVariable(final Variable newVar, Object initialValue,
		     Point location) {
	VariableList variableList = this;
	VariableOwner owner = getOwner();
	addAt(newVar, location);
	if (initialValue != null)
	    newVar.setValue(owner, initialValue);
	getViewManager().updateViews(new ViewManager.ViewUpdater() {
	    public void updateView(Object view, Object varToEdit) {
		VariableListView variableListView = (VariableListView) view;
		variableListView.addNewVariableEditor(newVar);
	    }
	}, null);
    }
    
    void remove(Variable v) {
	int ix = variables.indexOfIdentical(v);
	if (ix >= 0) {
	    deleteAllEditorsFor(v);
	    variables.removeElementAt(ix);
	    positions.removeElementAt(ix);
	    v.delete();
	}
    }
    
    boolean hasVariable(Variable v) {
	return variables.containsIdentical(v);
    }
    
    Variable findVariableNamed(String variableName, boolean caseSensitive) {
	if (variableName == null)
	    throw new PlaywriteInternalError("null value passed in for name");
	int size = variables.size();
	for (int i = 0; i < size; i++) {
	    Variable v = (Variable) variables.elementAt(i);
	    if (caseSensitive) {
		if (v.getName().equals(variableName))
		    return v;
	    } else if (v.getName().equalsIgnoreCase(variableName))
		return v;
	}
	return null;
    }
    
    public Variable findSystemVariable(String sysvarID) {
	for (int i = 0; i < variables.size(); i++) {
	    Variable v = (Variable) variables.elementAt(i);
	    if (v.isSystemType(sysvarID))
		return v;
	}
	return null;
    }
    
    Variable findEquivalentVariable(Variable targetVar) {
	if (targetVar.isSystemVariable())
	    return findSystemVariable(targetVar.getSystemType());
	for (int i = 0; i < variables.size(); i++) {
	    Variable v = (Variable) variables.elementAt(i);
	    if (targetVar.getName().equalsIgnoreCase(v.getName())
		&& targetVar.isUserVariable())
		return v;
	}
	return null;
    }
    
    Point variableLoc(Variable v) {
	int ix = variables.indexOfIdentical(v);
	if (ix < 0)
	    return null;
	return (Point) positions.elementAt(ix);
    }
    
    void moveVariableTo(Variable v, Point loc) {
	int ix = variables.indexOfIdentical(v);
	if (ix >= 0)
	    positions.setElementAt(loc, ix);
    }
    
    void pushToTop(Variable v) {
	int ix = variables.indexOfIdentical(v);
	if (ix >= 0) {
	    Point loc = (Point) positions.elementAt(ix);
	    variables.removeElementAt(ix);
	    positions.removeElementAt(ix);
	    variables.addElement(v);
	    positions.addElement(loc);
	}
    }
    
    void deleteAllEditorsFor(final Variable variable) {
	if (variable.isVisible()) {
	    VariableOwner owner = getOwner();
	    if (hasViews())
		getViewManager().updateViews(new ViewManager.ViewUpdater() {
		    public void updateView(Object view, Object whatever) {
			VariableListView variableListView
			    = (VariableListView) view;
			AbstractVariableEditor editor
			    = variableListView.getEditorFor(variable);
			variableListView.addDirtyRect(editor.bounds);
			editor.removeFromSuperview();
			editor.discard();
		    }
		}, null);
	}
    }
    
    void drVerifyReplacements() {
	CocoaCharacter variableOwner = (CocoaCharacter) getOwner();
	int i = variables.size();
	while (i-- > 0) {
	    Variable variable = (Variable) variables.elementAt(i);
	    Variable replacement
		= variable.getFixedCocoaVariable(variableOwner);
	    ASSERT.isTrue(variable == replacement);
	}
    }
    
    void printList(VariableOwner owner) {
	for (int i = 0; i < variables.size(); i++) {
	    Variable v = (Variable) variables.elementAt(i);
	    Object val = v.getActualValue(owner);
	    System.out.println("  Variable " + v.getName()
			       + " has actual value " + val);
	}
    }
    
    void writeContents(ObjectOutput oo) throws IOException {
	WorldOutStream out = (WorldOutStream) oo;
	out.writeInt(-100);
	int count = variables.size();
	out.writeInt(count);
	for (int i = 0; i < count; i++) {
	    Variable v = (Variable) variables.elementAt(i);
	    Point pt = variableLoc(v);
	    out.writeObject(v);
	    out.writeInt(pt == null ? -1 : pt.x);
	    out.writeInt(pt == null ? -1 : pt.y);
	}
    }
    
    void readContents(ObjectInput oi)
	throws IOException, ClassNotFoundException {
	WorldInStream in = (WorldInStream) oi;
	int count = in.readInt();
	if (count == -100) {
	    count = in.readInt();
	    for (int i = 0; i < count; i++) {
		Variable v = (Variable) in.readObject();
		int x = in.readInt();
		int y = in.readInt();
		Point pt = new Point(x, y);
		if (v != null) {
		    if (v.existsFor(owner))
			moveVariableTo(v, pt);
		    else
			addAt(v, pt);
		}
	    }
	} else {
	    Vector vars = new Vector(count);
	    for (int i = 0; i < count; i++)
		vars.addElement(in.readObject());
	    count = in.readInt();
	    Vector locations = new Vector(count);
	    for (int i = 0; i < count; i++) {
		int x = in.readInt();
		int y = in.readInt();
		locations.addElement(new Point(x, y));
	    }
	    for (int i = 0; i < vars.size(); i++) {
		Variable v
		    = Variable.xlateV1Variable(vars.elementAt(i), owner);
		Point point
		    = i < count ? (Point) locations.elementAt(i) : null;
		if (v.existsFor(owner)) {
		    if (point != null)
			moveVariableTo(v, point);
		} else {
		    v = v.getFixedCocoaVariable(getOwner());
		    if (point != null)
			addAt(v, point);
		    else
			add(v);
		}
	    }
	}
    }
    
    void writeListValues(ObjectOutput oo, VariableOwner owner)
	throws IOException {
	WorldOutStream out = (WorldOutStream) oo;
	out.writeInt(variables.size() * 2);
	for (int i = 0; i < variables.size(); i++) {
	    Variable v = (Variable) variables.elementAt(i);
	    out.writeObject(v);
	    out.writeObject(v.isTransient() ? null : v.getActualValue(owner));
	}
    }
    
    void readListValues(ObjectInput oi, VariableOwner owner)
	throws IOException, ClassNotFoundException {
	_readListValues(oi, owner, false);
    }
    
    void readListValuesAndNotify(ObjectInput oi, VariableOwner owner)
	throws IOException, ClassNotFoundException {
	_readListValues(oi, owner, true);
    }
    
    private void _readListValues
	(ObjectInput oi, VariableOwner owner, boolean notify)
	throws IOException, ClassNotFoundException {
	ASSERT.isNotNull(owner);
	WorldInStream in = (WorldInStream) oi;
	Debug.print("debug.objectstore", "Reading values for ",
		    String.valueOf(owner) + ": " + owner.getClass());
	int count = in.readInt();
	while (count > 0) {
	    Object vari = in.readObject();
	    Object val = in.readObject();
	    boolean unbound = val == vari;
	    count -= 2;
	    Variable v;
	    if (vari instanceof String) {
		v = findSystemVariable((String) vari);
		if (v == null)
		    Debug.print(true, "Can't find system variable: ", vari,
				" for ", owner);
	    } else
		v = (Variable) vari;
	    if (v != null) {
		if (val instanceof CocoaCharacter
		    && !v.isSystemType(World.SYS_FOLLOW_ME_VARIABLE_ID))
		    val = v.getDefaultValue(owner);
		if (v.isTransient())
		    Debug.print("debug.objectstore", "  Transient variable ",
				v.getName(), " ignored");
		else {
		    v.setActualValue(owner,
				     (unbound ? (Object) Variable.UNBOUND
				      : val));
		    Debug.print("debug.objectstore", "  Variable ", v, " -> ",
				v.getActualValue(owner));
		    TextCharacterPrototype.convertObsoleteVariableValue(owner,
									v);
		    if (notify)
			v.notifyChanged(owner, Variable.UNBOUND,
					v.getValue(owner));
		}
	    }
	}
    }
}
