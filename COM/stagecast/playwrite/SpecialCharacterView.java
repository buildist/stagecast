/* SpecialCharacterView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.View;

public abstract class SpecialCharacterView extends CharacterView
{
    private boolean _initialized = false;
    private boolean _characterGeneralized = false;
    public int _buttonOffset = 5;
    private ResizeCornerView _resizeButton;
    private View _content = createContentView();
    private Watcher widthAndHeightWatcher;
    
    public SpecialCharacterView(CocoaCharacter ch, int scaleSize) {
	super(ch, scaleSize);
	this.addSubview(_content);
	_resizeButton
	    = new ResizeCornerView(this, this.right() - _buttonOffset,
				   this.bottom() - _buttonOffset,
				   _buttonOffset, _buttonOffset);
	this.addSubview(_resizeButton);
	_initialized = true;
    }
    
    public SpecialCharacterView(CocoaCharacter ch) {
	this(ch, 0);
    }
    
    protected abstract void addVariableWatchers(CocoaCharacter cocoacharacter);
    
    private void _addVariableWatchers(CocoaCharacter ch) {
	if (widthAndHeightWatcher == null)
	    widthAndHeightWatcher = new EventThreadWatcher(new Watcher() {
		public void update(Object target, Object value) {
		    SpecialCharacterView.this._layoutView(false);
		}
	    });
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID, ch.getPrototype())
	    .addValueWatcher(ch, widthAndHeightWatcher);
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID,
	     ch.getPrototype())
	    .addValueWatcher(ch, widthAndHeightWatcher);
	addVariableWatchers(ch);
    }
    
    protected abstract void removeVariableWatchers
	(CocoaCharacter cocoacharacter);
    
    private void _removeVariableWatchers(CocoaCharacter ch) {
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID, ch.getPrototype())
	    .removeValueWatcher(ch, widthAndHeightWatcher);
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID,
	     ch.getPrototype())
	    .removeValueWatcher(ch, widthAndHeightWatcher);
	removeVariableWatchers(ch);
    }
    
    public void setModelObject(Object obj) {
	Object oldCharacter = this.getModelObject();
	super.setModelObject(obj);
	if (_initialized)
	    _removeVariableWatchers((CocoaCharacter) oldCharacter);
	_addVariableWatchers((CocoaCharacter) obj);
    }
    
    public View createContentView() {
	PlaywriteView result = new PlaywriteView(0, 0, this.width(),
						 this.height()) {
	    public View viewForMouse(int x, int y) {
		View v = super.viewForMouse(x, y);
		if (v == this)
		    v = null;
		return v;
	    }
	};
	result.setHorizResizeInstruction(2);
	result.setVertResizeInstruction(16);
	return result;
    }
    
    public View getContentView() {
	return _content;
    }
    
    protected final boolean hasBeenGeneralized() {
	return _characterGeneralized;
    }
    
    protected boolean checkGeneralization() {
	_characterGeneralized
	    = (this.getCharacter().getCurrentAppearance().getClass()
	       != Appearance.class);
	return _characterGeneralized;
    }
    
    public void draw(CocoaCharacter ch, Graphics g, int x, int y,
		     int squareSize) {
	if (_characterGeneralized)
	    super.draw(ch, g, x, y, squareSize);
	else {
	    g.translate(x, y);
	    drawView(g);
	    this.drawSubviews(g);
	    g.translate(-x, -y);
	}
    }
    
    public void drawView(Graphics g) {
	if (_characterGeneralized)
	    super.drawView(g);
	else
	    drawSpecialView(g);
    }
    
    public abstract void drawSpecialView(Graphics graphics);
    
    public Image getDragImage() {
	if (hasBeenGeneralized())
	    return super.getDragImage();
	return Util.makeBitmapFromView(this);
    }
    
    private void _layoutView(boolean forceLayout) {
	CocoaCharacter ch = this.getCharacter();
	CharacterPrototype proto = ch.getPrototype();
	int squareSize = getContainerSquareSize();
	double rawW
	    = ((Number)
	       Variable.systemVariable
		   (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID, proto)
		   .getValue(ch))
		  .doubleValue();
	double rawH
	    = ((Number)
	       Variable.systemVariable
		   (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID, proto)
		   .getValue(ch))
		  .doubleValue();
	int width = Math.max((int) (rawW * (double) squareSize), 1);
	int height = Math.max((int) (rawH * (double) squareSize), 1);
	if (forceLayout || this.width() != width || this.height() != height)
	    this.sizeTo(width, height);
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (event.isMetaKeyDown())
	    return false;
	super.mouseDown(event);
	return true;
    }
    
    public void ancestorWasAddedToViewHierarchy(View addedView) {
	if (addedView == this.superview() && addedView instanceof BoardView)
	    _layoutView(true);
	super.ancestorWasAddedToViewHierarchy(addedView);
    }
    
    public void discard() {
	_removeVariableWatchers(this.getCharacter());
	super.discard();
    }
    
    public void resize() {
	if (checkGeneralization())
	    super.resize();
	else if (_initialized)
	    _layoutView(true);
    }
    
    public void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	if (_initialized)
	    _resizeButton.moveTo(width - _resizeButton.width(),
				 height - _resizeButton.height());
    }
    
    public void setBoundsForAppearance(Appearance app) {
	/* empty */
    }
    
    public boolean toolClicked(ToolSession session) {
	if (session.toolType() == Tool.editAppearanceTool)
	    return false;
	return super.toolClicked(session);
    }
    
    public ToolDestination acceptsTool(ToolSession session, int x, int y) {
	if (session.toolType() == Tool.editAppearanceTool)
	    return null;
	return super.acceptsTool(session, x, y);
    }
    
    public void setResizeButtonEnabled(boolean b) {
	_resizeButton.setEnabled(b);
    }
    
    public PlaywriteView getResizeButton() {
	return _resizeButton;
    }
    
    public void setWidthAndHeightVariables(int w, int h) {
	CocoaCharacter ch = this.getCharacter();
	CharacterPrototype proto = ch.getPrototype();
	double squareSize = (double) getContainerSquareSize();
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID, proto)
	    .setValue(ch, new Double((double) w / squareSize));
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID, proto)
	    .setValue(ch, new Double((double) h / squareSize));
    }
    
    public void commitWidthAndHeightVariables(int oldWidth, int oldHeight,
					      int newWidth, int newHeight) {
	CocoaCharacter ch = this.getCharacter();
	CharacterPrototype proto = ch.getPrototype();
	int squareSize = getContainerSquareSize();
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID, proto)
	    .setValue(ch, new Double((double) oldWidth / (double) squareSize));
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID, proto).setValue
	    (ch, new Double((double) oldHeight / (double) squareSize));
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID, proto).modifyValue
	    (ch, new Double((double) newWidth / (double) squareSize));
	this.getWorld().suspendClockTicks(true);
	Variable.systemVariable
	    (SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID, proto)
	    .modifyValue
	    (ch, new Double((double) newHeight / (double) squareSize));
	this.getWorld().suspendClockTicks(false);
    }
    
    public int getContainerSquareSize() {
	if (this.superview() instanceof BoardView)
	    return ((BoardView) this.superview()).getSquareSize();
	return 32;
    }
}
