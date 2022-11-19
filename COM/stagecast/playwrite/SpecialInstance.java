/* SpecialInstance - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Hashtable;

public abstract class SpecialInstance extends CharacterInstance
{
    static final int storeVersion = 0;
    static final long serialVersionUID = -3819410108751545650L;
    protected Appearance _currentAppearance;
    private Watcher _widthAndHeightWatcher;
    
    public SpecialInstance(SpecialPrototype prototype) {
	super((CharacterPrototype) prototype);
	setNewAppearance();
    }
    
    public SpecialInstance() {
	/* empty */
    }
    
    public void setLogicalWidth(double width) {
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID,
	     this.getPrototype())
	    .setValue(this, new Double(width));
    }
    
    public void setLogicalHeight(double height) {
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID,
	     this.getPrototype())
	    .setValue(this, new Double(height));
    }
    
    public double getLogicalWidth() {
	return ((Number)
		Variable.getSystemValue((SpecialPrototype
					 .SYS_SPECIAL_WIDTH_VARIABLE_ID),
					this))
		   .doubleValue();
    }
    
    public double getLogicalHeight() {
	return ((Number)
		Variable.getSystemValue((SpecialPrototype
					 .SYS_SPECIAL_HEIGHT_VARIABLE_ID),
					this))
		   .doubleValue();
    }
    
    void fillInObject(CharacterPrototype prototype) {
	super.fillInObject(prototype);
	addWidthAndHeightWatchers();
    }
    
    public boolean editAppearance() {
	return false;
    }
    
    public Appearance getCurrentAppearance() {
	if (_currentAppearance != null)
	    return _currentAppearance;
	return super.getCurrentAppearance();
    }
    
    public void setCurrentAppearance(Appearance appearance) {
	/* empty */
    }
    
    public void delete() {
	removeWidthAndHeightWatchers();
	super.delete();
    }
    
    public Rect getRuleDefineRect() {
	if (this.getContainer() instanceof Stage)
	    return new Rect(this.getH(), this.getV(), 1, 1);
	return null;
    }
    
    public abstract PlaywriteView createView();
    
    public Object copy(Hashtable map, boolean fullCopy) {
	SpecialInstance newInstance
	    = (SpecialInstance) this.copy(map, fullCopy, "");
	newInstance.setNewAppearance();
	return newInstance;
    }
    
    protected void setNewAppearance() {
	Appearance protoApp = this.getPrototype().getCurrentAppearance();
	int appHeight = (int) Math.ceil(getLogicalHeight());
	Appearance newApp
	    = new Appearance("<none>", protoApp.getBitmap(),
			     new Shape((int) Math.ceil(getLogicalWidth()),
				       appHeight, new Point(1, appHeight)));
	_currentAppearance = newApp;
	this.add(newApp);
	super.setCurrentAppearance(newApp);
    }
    
    public void adjustAppearanceShape() {
	Appearance app = getCurrentAppearance();
	double width = getLogicalWidth();
	double height = getLogicalHeight();
	int logicalWidth = (int) Math.ceil(width);
	int logicalHeight = (int) Math.ceil(height);
	if (logicalWidth != app.getLogicalWidth()
	    || (logicalHeight != app.getLogicalHeight()
		&& this.getCharContainer() instanceof Board)) {
	    Board board = (Board) this.getContainer();
	    if (board != null)
		board.removeFromSquare(this);
	    app.adjustShape(logicalWidth, logicalHeight);
	    app.setHomeSquare(new Point(1, app.getLogicalHeight()));
	    if (board != null)
		board.putInSquare(this, this.getH(), this.getV(), false);
	}
    }
    
    protected void addWidthAndHeightWatchers() {
	_widthAndHeightWatcher = new EventThreadWatcher(new Watcher() {
	    public void update(Object target, Object value) {
		adjustAppearanceShape();
	    }
	});
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID,
	     this.getPrototype())
	    .addValueWatcher(this, _widthAndHeightWatcher);
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID,
	     this.getPrototype())
	    .addValueWatcher(this, _widthAndHeightWatcher);
    }
    
    protected void removeWidthAndHeightWatchers() {
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID,
	     this.getPrototype())
	    .removeValueWatcher(this, _widthAndHeightWatcher);
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID,
	     this.getPrototype())
	    .removeValueWatcher(this, _widthAndHeightWatcher);
    }
    
    protected void sizeInPixelsToSizeInSquaresFix() {
	Object width
	    = Variable.getSystemValue((SpecialPrototype
				       .SYS_SPECIAL_WIDTH_VARIABLE_ID),
				      this);
	Object height
	    = Variable.getSystemValue((SpecialPrototype
				       .SYS_SPECIAL_HEIGHT_VARIABLE_ID),
				      this);
	if (width instanceof Number) {
	    Variable v
		= Variable.systemVariable((SpecialPrototype
					   .SYS_SPECIAL_WIDTH_VARIABLE_ID),
					  this);
	    if (v.getActualValue(this) != Variable.UNBOUND) {
		double newWidth = ((Number) width).doubleValue() / 32.0;
		Variable.setSystemValue((SpecialPrototype
					 .SYS_SPECIAL_WIDTH_VARIABLE_ID),
					this, new Double(newWidth));
	    }
	}
	if (height instanceof Number) {
	    Variable v
		= Variable.systemVariable((SpecialPrototype
					   .SYS_SPECIAL_HEIGHT_VARIABLE_ID),
					  this);
	    if (v.getActualValue(this) != Variable.UNBOUND) {
		double newHeight = ((Number) height).doubleValue() / 32.0;
		Variable.setSystemValue((SpecialPrototype
					 .SYS_SPECIAL_HEIGHT_VARIABLE_ID),
					this, new Double(newHeight));
	    }
	}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.setCurrentAppearance(_currentAppearance);
	super.writeExternal(out);
	out.writeObject(_currentAppearance);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(SpecialInstance.class);
	super.readExternal(in);
	_currentAppearance = (Appearance) in.readObject();
	if (_currentAppearance.getOwner() != this.getPrototype()) {
	    Debug.print(true, "Fixing shared appearance between ", this,
			" and ", _currentAppearance.getOwner());
	    setNewAppearance();
	}
	addWidthAndHeightWatchers();
    }
}
