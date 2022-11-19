/* TextCharacterInstance - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;

public class TextCharacterInstance extends SpecialInstance
{
    static final int CHANGED_FLAG_EXPIRE_TIME = 1;
    public static final String[] EDITABLE_DISABLE_LIST = new String[0];
    static final int storeVersion = 4;
    static final long serialVersionUID = -3819410108754756914L;
    private TextClockWatcher _clockWatcher;
    private TextVariableChangedWatcher _changedWatcher;
    private Watcher _editableWatcher;
    private int _timeLastChanged = -1;
    
    public class TextClockWatcher implements Watcher
    {
	TextCharacterInstance _ti;
	
	public TextClockWatcher(TextCharacterInstance instance) {
	    _ti = instance;
	}
	
	public void update(Object target, Object value) {
	    _ti.newTick();
	}
    }
    
    public class TextVariableChangedWatcher implements Watcher
    {
	TextCharacterInstance _ti;
	
	public TextVariableChangedWatcher(TextCharacterInstance instance) {
	    _ti = instance;
	}
	
	public void update(Object target, Object value) {
	    _ti.textChanged();
	}
    }
    
    public TextCharacterInstance(TextCharacterPrototype prototype) {
	super((SpecialPrototype) prototype);
    }
    
    public TextCharacterInstance() {
	/* empty */
    }
    
    public void setTimeLastChanged(int time) {
	_timeLastChanged = time;
    }
    
    public int getTimeLastChanged() {
	return _timeLastChanged;
    }
    
    void fillInObject(CharacterPrototype prototype) {
	super.fillInObject(prototype);
	addTCVariableWatchers();
    }
    
    private void addTCVariableWatchers() {
	_changedWatcher = new TextVariableChangedWatcher(this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_VARIABLE_ID, this)
	    .addValueWatcher(this, _changedWatcher);
	_editableWatcher = new EventThreadWatcher(new Watcher() {
	    public void update(Object target, Object value) {
		TextCharacterInstance.this.updateVariableEditorState();
	    }
	});
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_EDITABLE_VARIABLE_ID, this)
	    .addValueWatcher(this, _editableWatcher);
    }
    
    private void removeTCVariableWatchers() {
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_VARIABLE_ID, this)
	    .removeValueWatcher(this, _changedWatcher);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_EDITABLE_VARIABLE_ID, this)
	    .removeValueWatcher(this, _editableWatcher);
	if (_clockWatcher != null)
	    this.getWorld().removeClockWatcher(_clockWatcher);
    }
    
    public void newTick() {
	World world = this.getWorld();
	int difference = world.getTime() - getTimeLastChanged();
	if (difference < -1 || difference > 1) {
	    world.removeClockWatcher(_clockWatcher);
	    Variable.systemVariable
		(TextCharacterPrototype.SYS_TEXT_WASCHANGED_VARIABLE_ID, this)
		.setValue(this, Boolean.FALSE);
	    _clockWatcher = null;
	}
    }
    
    public void textChanged() {
	setTimeLastChanged(this.getWorld().getTime());
	if (_clockWatcher == null) {
	    Variable.systemVariable
		(TextCharacterPrototype.SYS_TEXT_WASCHANGED_VARIABLE_ID, this)
		.setValue(this, Boolean.TRUE);
	    _clockWatcher = new TextClockWatcher(this);
	    this.getWorld().addClockWatcher(_clockWatcher);
	}
    }
    
    public PlaywriteView createView() {
	return new TextCharacterView(this);
    }
    
    public void setEditor(CharacterWindow ed) {
	super.setEditor(ed);
	updateVariableEditorState();
    }
    
    private void updateVariableEditorState() {
	CharacterWindow window = this.getEditor();
	if (window != null) {
	    boolean enable
		= Variable.getSystemValue
		      (TextCharacterPrototype.SYS_TEXT_EDITABLE_VARIABLE_ID,
		       this)
		      .equals(Boolean.FALSE);
	    for (int i = 0; i < EDITABLE_DISABLE_LIST.length; i++)
		window.setVariableEditorEnabled
		    (Variable.systemVariable(EDITABLE_DISABLE_LIST[i], this),
		     enable);
	}
    }
    
    public void delete() {
	removeTCVariableWatchers();
	_clockWatcher = null;
	_changedWatcher = null;
	_editableWatcher = null;
	super.delete();
    }
    
    public boolean affectsDisplay(Variable variable) {
	if (variable.isSystemType(TextCharacterPrototype.SYS_TEXT_VARIABLE_ID)
	    || variable.isSystemType(SpecialPrototype
				     .SYS_SPECIAL_WIDTH_VARIABLE_ID)
	    || variable.isSystemType(SpecialPrototype
				     .SYS_SPECIAL_HEIGHT_VARIABLE_ID)
	    || variable.isSystemType(TextCharacterPrototype
				     .SYS_TEXT_FONT_VARIABLE_ID)
	    || variable.isSystemType(TextCharacterPrototype
				     .SYS_TEXT_STYLE_VARIABLE_ID)
	    || variable.isSystemType(TextCharacterPrototype
				     .SYS_TEXT_SIZE_VARIABLE_ID)
	    || variable.isSystemType(TextCharacterPrototype
				     .SYS_TEXT_ALIGNMENT_VARIABLE_ID)
	    || variable.isSystemType(TextCharacterPrototype
				     .SYS_TEXT_OFFSET_X_VARIABLE_ID)
	    || variable.isSystemType(TextCharacterPrototype
				     .SYS_TEXT_OFFSET_Y_VARIABLE_ID)
	    || variable.isSystemType(TextCharacterPrototype
				     .SYS_TEXT_COLOR_VARIABLE_ID)
	    || variable.isSystemType(TextCharacterPrototype
				     .SYS_TEXT_BGCOLOR_VARIABLE_ID))
	    return true;
	return super.affectsDisplay(variable);
    }
    
    private void preVersion3FontFix() {
	GenericContainer c = this.getContainer();
	if (c instanceof Board) {
	    Board board = (Board) c;
	    TextCharacterPrototype proto
		= (TextCharacterPrototype) this.getPrototype();
	    Object sizeObj
		= Variable.getSystemValue((TextCharacterPrototype
					   .SYS_TEXT_SIZE_VARIABLE_ID),
					  this);
	    int oldSize = Integer.parseInt(sizeObj.toString());
	    int scaledFontSize = oldSize * board.getSquareSize() / 32;
	    Variable.setSystemValue((TextCharacterPrototype
				     .SYS_TEXT_SIZE_VARIABLE_ID),
				    this, new Integer(scaledFontSize));
	}
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version
	    = ((WorldInStream) in).loadVersion(TextCharacterInstance.class);
	switch (version) {
	case 1:
	case 2:
	case 3:
	    this.readCharacterInstance(in);
	    break;
	case 4:
	    super.readExternal(in);
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 4);
	}
	switch (version) {
	case 1:
	    this.setNewAppearance();
	    this.sizeInPixelsToSizeInSquaresFix();
	    this.addWidthAndHeightWatchers();
	    break;
	case 2:
	case 3:
	    in.readObject();
	    this.setNewAppearance();
	    this.sizeInPixelsToSizeInSquaresFix();
	    this.addWidthAndHeightWatchers();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 4);
	case 4:
	    /* empty */
	}
	if (((WorldInStream) in).getFilesystemVersion() == 1)
	    Variable.setSystemValue((TextCharacterPrototype
				     .SYS_TEXT_EDITABLE_VARIABLE_ID),
				    this, Boolean.FALSE);
	addTCVariableWatchers();
	int logicalWidth = (int) Math.ceil(this.getLogicalWidth());
	int logicalHeight = (int) Math.ceil(this.getLogicalHeight());
	Appearance app = this.getCurrentAppearance();
	if (app.getLogicalWidth() != logicalWidth
	    || app.getLogicalHeight() != logicalHeight) {
	    Debug.print(true, ("fixing appearance size for " + this
			       + " should be " + logicalWidth + ", "
			       + logicalHeight + " is " + app.getLogicalWidth()
			       + ", " + app.getLogicalHeight()));
	    app.adjustShape(logicalWidth, logicalHeight);
	}
    }
    
    public void readCompleted(WorldInStream wis) {
	super.readCompleted(wis);
	int version = wis.loadVersion(TextCharacterInstance.class);
	if (version < 3)
	    preVersion3FontFix();
    }
}
