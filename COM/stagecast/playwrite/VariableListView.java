/* VariableListView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.ScrollView;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class VariableListView extends RubberBandView
    implements DragDestination, ExtendedDragSource, ResourceIDs.VariableIDs,
	       ResourceIDs.ToolIDs, Worldly
{
    static final int VARIABLE_SCROLL_AMOUNT = 25;
    static final Tool newVariableTool
	= Tool.createTool("new variable tool", "new variable tool button");
    private transient int vGap = 10;
    private transient int hGap = 10;
    private transient int currentX = 0;
    private transient int currentY = 0;
    private transient int tallest = 0;
    
    VariableListView(VariableOwner owner, int width, int height) {
	super(0, 0, width, height);
	this.setBorder(null);
	this.setMargins(2);
	this.setModelObject(owner);
	this.allowDragInto(AbstractVariableEditor.class, this);
	this.allowDragOutOf(AbstractVariableEditor.class, this);
	this.allowTool(Tool.copyPlaceTool, this);
	this.allowTool(newVariableTool, this);
	VariableList variableList = getVariableList();
	Vector leftOver = new Vector();
	Enumeration variables = variableList.elements();
	while (variables.hasMoreElements()) {
	    Variable variable = (Variable) variables.nextElement();
	    Point loc = variableList.variableLoc(variable);
	    if (variable.isVisible()) {
		if (loc.x == -1 && loc.y == -1)
		    leftOver.addElement(variable);
		else
		    addNewVariableEditor(variable);
	    }
	}
	variables = leftOver.elements();
	while (variables.hasMoreElements()) {
	    Variable variable = (Variable) variables.nextElement();
	    addNewVariableEditor(variable);
	}
	variableList.getViewManager().addView(this);
    }
    
    VariableOwner getOwner() {
	return (VariableOwner) this.getModelObject();
    }
    
    public World getWorld() {
	return getOwner().getWorld();
    }
    
    VariableList getVariableList() {
	return getOwner().getVariableList();
    }
    
    ViewManager getViewManager() {
	return getVariableList().getViewManager();
    }
    
    void addNewVariableEditor(Variable v) {
	VariableList variableList = getVariableList();
	VariableOwner owner = getOwner();
	AbstractVariableEditor varEditor = v.makeVariableEditor(owner, null);
	Point loc = getVariableList().variableLoc(v);
	if (loc.x == -1 && loc.y == -1) {
	    if (currentX != 0
		&& varEditor.width() + currentX > this.bounds().maxX()) {
		currentX = 0;
		currentY = tallest + vGap;
	    }
	    loc.x = currentX;
	    loc.y = currentY;
	}
	varEditor.moveTo(loc.x, loc.y);
	if (tallest < varEditor.bounds.maxY())
	    tallest = varEditor.bounds.maxY();
	currentX = varEditor.bounds.maxX() + hGap;
	this.addSubview(varEditor);
	this.addDirtyRect(varEditor.bounds);
    }
    
    public void setVariableEditorEnabled(Variable v, boolean enabled) {
	Vector subviews = this.subviews();
	for (int i = 0; i < subviews.size(); i++) {
	    if (subviews.elementAt(i) instanceof AbstractVariableEditor
		&& ((AbstractVariableEditor) subviews.elementAt(i))
		       .getVariable() == v) {
		if (enabled)
		    ((AbstractVariableEditor) subviews.elementAt(i)).enable();
		else
		    ((AbstractVariableEditor) subviews.elementAt(i)).disable();
	    }
	}
    }
    
    public void discard() {
	Vector editors = (Vector) this.subviews().clone();
	for (int j = 0; j < editors.size(); j++) {
	    AbstractVariableEditor varEd
		= (AbstractVariableEditor) editors.elementAt(j);
	    varEd.discard();
	    varEd.removeFromSuperview();
	}
	getViewManager().removeView(this);
	super.discard();
    }
    
    public boolean prepareToDrag(Object data) {
	return true;
    }
    
    public void dragWasAccepted(DragSession session) {
	/* empty */
    }
    
    public boolean dragWasRejected(DragSession session) {
	return true;
    }
    
    public View sourceView(DragSession session) {
	return this;
    }
    
    public boolean dragDropped(DragSession session) {
	Selection.unselectAll();
	unhilite();
	if (session.source() == this)
	    this.addSubview(this.viewBeingDragged(session));
	if (session.data() instanceof AbstractVariableEditor) {
	    AbstractVariableEditor view
		= (AbstractVariableEditor) session.data();
	    Point dropPoint = session.destinationMousePoint();
	    Variable droppedVar = view.getVariable();
	    VariableOwner oldOwner = view.getOwner();
	    Object oldValue = droppedVar.getActualValue(oldOwner);
	    VariableList variableList = getVariableList();
	    if (variableList.hasVariable(droppedVar)) {
		VariableListView sourceView
		    = (VariableListView) session.source();
		if (sourceView.getOwner() == variableList.getOwner()
		    && sourceView != this)
		    return false;
	    } else if (droppedVar.isSystemVariable())
		return false;
	    Variable newVariable;
	    if (variableList.hasVariable(droppedVar))
		newVariable = droppedVar;
	    else
		newVariable = copyVariableAndValue(droppedVar, oldValue);
	    if (view.getDragPoint() != null)
		dropPoint.moveBy(-view.getDragPoint().x,
				 -view.getDragPoint().y);
	    variableList.moveVariableTo(newVariable, dropPoint);
	    variableList.pushToTop(newVariable);
	    reposition(newVariable, dropPoint);
	    Selection.addToSelection(view, this);
	    getWorld().setModified(true);
	    return true;
	}
	return false;
    }
    
    public boolean dragEntered(DragSession ds) {
	if (ds.source() == this) {
	    PlaywriteView view = this.viewBeingDragged(ds);
	    view.removeFromSuperview();
	    this.addDirtyRect(view.bounds());
	}
	if (wantsObject(ds.data())) {
	    hilite();
	    return true;
	}
	unhilite();
	return false;
    }
    
    public void dragExited(DragSession ds) {
	if (ds.source() == this) {
	    PlaywriteView view = this.viewBeingDragged(ds);
	    this.addSubview(view);
	    this.addDirtyRect(view.bounds());
	}
	unhilite();
    }
    
    public boolean dragMoved(DragSession ds) {
	return wantsObject(ds.data());
    }
    
    boolean wantsObject(Object obj) {
	return obj instanceof AbstractVariableEditor;
    }
    
    public void hilite() {
	View superview = this.superview();
	if (superview instanceof ScrollView)
	    ((PlaywriteView) superview.superview()).hilite();
	else
	    super.hilite();
    }
    
    public void unhilite() {
	View superview = this.superview();
	if (superview instanceof ScrollView)
	    ((PlaywriteView) superview.superview()).unhilite();
	else
	    super.unhilite();
    }
    
    private boolean wantsTool(ToolSession session) {
	Tool toolType = session.toolType();
	return toolType == newVariableTool || (toolType == Tool.copyPlaceTool
					       && wantsObject(session.data()));
    }
    
    public boolean toolEntered(ToolSession session) {
	return wantsTool(session);
    }
    
    public boolean toolMoved(ToolSession session) {
	return wantsTool(session);
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool toolType = session.toolType();
	Point dropPoint = new Point(session.destinationMousePoint());
	if (toolType == Tool.copyPlaceTool) {
	    if (!(session.data() instanceof AbstractVariableEditor))
		return false;
	    AbstractVariableEditor view
		= (AbstractVariableEditor) session.data();
	    Variable toolVar = view.getVariable();
	    VariableOwner oldOwner = view.getOwner();
	    Object oldValue = toolVar.getActualValue(oldOwner);
	    VariableList variableList = getVariableList();
	    Variable newVariable = copyVariableAndValue(toolVar, oldValue);
	    dropPoint.moveBy(-view.width() / 2, -view.height() / 2);
	    variableList.moveVariableTo(newVariable, dropPoint);
	    reposition(newVariable, dropPoint);
	} else if (toolType == newVariableTool) {
	    dropPoint.moveBy(-26, -16);
	    String vname
		= (Resource.getTextAndFormat
		   ("NewVarNamID",
		    (new Object[]
		     { new Integer(getVariableList().getNewVarIndex()) })));
	    addVariable(new Variable(vname, getVariableList().getOwner()),
			null, dropPoint);
	}
	getWorld().setModified(true);
	return true;
    }
    
    private void addVariable(Variable newVar, Object initialValue,
			     Point location) {
	getVariableList().addVariable(newVar, initialValue, location);
    }
    
    private Variable copyVariableAndValue(Variable oldVariable,
					  Object oldValue) {
	World oldWorld = oldVariable.getWorld();
	World newWorld = getWorld();
	VariableOwner oldOwner = oldVariable.getListOwner();
	VariableOwner newOwner = getVariableList().getOwner();
	Hashtable map = new Hashtable(50);
	if (oldWorld != newWorld)
	    map.put(oldWorld, newWorld);
	if (oldOwner != newOwner)
	    map.put(oldOwner, newOwner);
	Variable newVariable = (Variable) oldVariable.copy(map, true);
	if (newOwner instanceof CharacterPrototype) {
	    newVariable.copyValue(oldVariable.getActualValue(oldOwner),
				  newOwner, map, false);
	    newVariable.notifyChanged(newOwner, new Object(),
				      newVariable.getValue(newOwner));
	}
	newVariable.copyValue(oldValue, getOwner(), map, false);
	newVariable.notifyChanged(getOwner(), new Object(),
				  newVariable.getValue(getOwner()));
	return newVariable;
    }
    
    private void reposition(final Variable variable, final Point newLoc) {
	getVariableList().getViewManager()
	    .updateViews(new ViewManager.ViewUpdater() {
	    public void updateView(Object view, Object whatever) {
		VariableListView variableListView = (VariableListView) view;
		variableListView._reposition(variable, newLoc);
	    }
	}, null);
    }
    
    private void _reposition(Variable variable, Point newLoc) {
	AbstractVariableEditor editor = getEditorFor(variable);
	this.addDirtyRect(editor.bounds);
	editor.removeFromSuperview();
	editor.moveTo(newLoc.x, newLoc.y);
	this.addSubview(editor);
	this.addDirtyRect(editor.bounds);
    }
    
    AbstractVariableEditor getEditorFor(Variable variable) {
	int nsubviews = this.subviews().size();
	for (int i = 0; i < nsubviews; i++) {
	    AbstractVariableEditor editor
		= (AbstractVariableEditor) this.subviews().elementAt(i);
	    if (editor.getVariable() == variable)
		return editor;
	}
	throw new PlaywriteInternalError("no editor for " + variable + " in "
					 + this);
    }
}
