/* PlaywriteTextView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.ImageAttachment;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Range;
import COM.stagecast.ifc.netscape.application.TextAttachment;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PlaywriteTextView extends TextView
    implements ResourceIDs.DialogIDs, ViewGlue
{
    private Object model;
    private ViewGlue toolDelegate;
    private boolean hilited = false;
    private boolean selectAll = false;
    private boolean editing = false;
    private View _nextView;
    private View _previousView;
    private boolean isUserEditable = true;
    private boolean _isDraggable = true;
    private boolean _wantsAutoScrollEvents;
    private long _commandEventTimeStamp;
    
    public PlaywriteTextView(int x, int y, int w, int h) {
	super(x, y, w, h);
	this.setSelectionColor(Util.textSelectionColor);
	this.setEditable(false);
    }
    
    void setModelObject(Object thing) {
	model = thing;
    }
    
    void setToolDelegate(ViewGlue view) {
	toolDelegate = view;
    }
    
    public View nextSelectableView() {
	return _nextView;
    }
    
    public void setNextSelectableView(View view) {
	_nextView = view;
    }
    
    public View previousSelectableView() {
	View previousView = _previousView;
	if (previousView == null)
	    previousView = findPreviousView(this);
	return previousView;
    }
    
    public void setPreviousSelectableView(View view) {
	_previousView = view;
    }
    
    public void setUserEditable(boolean editable) {
	isUserEditable = editable;
	this.setTransparent(isUserEditable ^ true);
    }
    
    public void setDraggable(boolean b) {
	_isDraggable = b;
    }
    
    private boolean getDraggable() {
	return _isDraggable;
    }
    
    public ToolDestination acceptsTool(ToolSession session, int x, int y) {
	if (toolDelegate != null)
	    return toolDelegate.acceptsTool(session, x, y);
	return null;
    }
    
    public void hilite() {
	hilited = true;
	this.setDirty(true);
    }
    
    public void unhilite() {
	hilited = false;
	this.setDirty(true);
    }
    
    public boolean isHilited() {
	return hilited;
    }
    
    public Object getModelObject() {
	return model;
    }
    
    public void discard() {
	model = null;
	toolDelegate = null;
	for (int i = 0; i < this.subviews().size(); i++) {
	    View view = (View) this.subviews().elementAt(i);
	    if (view instanceof ViewGlue)
		((ViewGlue) view).discard();
	}
	flushAllBitmaps();
    }
    
    public View view() {
	return this;
    }
    
    public final void willBecomeSelected() {
	/* empty */
    }
    
    public final void willBecomeUnselected() {
	/* empty */
    }
    
    public void drawView(Graphics g) {
	ensureImagesAreLoaded();
	super.drawView(g);
	if (hilited) {
	    g.setColor(Util.HIGHLIGHT_COLOR);
	    g.drawRect(0, 0, this.width(), this.height());
	    g.drawRect(1, 1, this.width() - 2, this.height() - 2);
	}
    }
    
    public void selectText() {
	this.selectRange(new Range(0, this.length()));
    }
    
    public void setWantsAutoscrollEvents(boolean b) {
	_wantsAutoScrollEvents = b;
    }
    
    public boolean wantsAutoscrollEvents() {
	return _wantsAutoScrollEvents;
    }
    
    public int cursorForPoint(int x, int y) {
	if (editing || !getDraggable())
	    return super.cursorForPoint(x, y);
	Point newPoint = this.convertToView(this.superview(), x, y);
	return this.superview().cursorForPoint(newPoint.x, newPoint.y);
    }
    
    public void keyDown(KeyEvent keyEvent) {
	KeyEvent cmdEvent = PlaywriteRoot.getLastCommandKeyEvent();
	if (cmdEvent != null) {
	    _commandEventTimeStamp = cmdEvent.timeStamp();
	    switch (cmdEvent.key + 64) {
	    case 67:
	    case 99:
		this.copy();
		break;
	    case 86:
	    case 118:
		paste();
		break;
	    case 88:
	    case 120:
		this.cut();
		break;
	    default:
		break;
	    }
	} else if (keyEvent.key != 9)
	    super.keyDown(keyEvent);
    }
    
    public void keyUp(KeyEvent keyEvent) {
	if (keyEvent.key == 9) {
	    MouseEvent mouseUp
		= new MouseEvent(keyEvent.timeStamp(), -3, 1, 1, 0);
	    if (keyEvent.isShiftKeyDown()) {
		View previousView = previousSelectableView();
		if (previousView != null)
		    previousView.mouseUp(mouseUp);
	    } else if (nextSelectableView() != null)
		nextSelectableView().mouseUp(mouseUp);
	} else
	    super.keyUp(keyEvent);
    }
    
    public void keyTyped(KeyEvent keyEvent) {
	if (keyEvent.timeStamp() != _commandEventTimeStamp)
	    super.keyTyped(keyEvent);
    }
    
    private View findPreviousView(View view) {
	View nextView = view.nextSelectableView();
	if (nextView == this)
	    return view;
	if (nextView == null)
	    return null;
	return findPreviousView(nextView);
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (editing || this.isEditable() || !getDraggable())
	    return super.mouseDown(event);
	return (this.superview().mouseDown
		(this.convertEventToView(this.superview(), event)));
    }
    
    public void mouseDragged(MouseEvent event) {
	if (!this.isEditable() && getDraggable())
	    this.superview().mouseDragged
		(this.convertEventToView(this.superview(), event));
	else if (editing || !getDraggable())
	    super.mouseDragged(event);
	else
	    this.superview().mouseDragged
		(this.convertEventToView(this.superview(), event));
    }
    
    public void mouseUp(MouseEvent event) {
	if (!this.isEditable() && isUserEditable && getDraggable()) {
	    this.setEditable(true);
	    selectText();
	    selectAll = false;
	    editing = true;
	    this.setFocusedView();
	    PlaywriteRoot.getMainRootView().updateCursor();
	} else
	    super.mouseUp(event);
    }
    
    public void startFocus() {
	Selection.resetGlobalState();
	super.startFocus();
	selectAll = true;
    }
    
    public void pauseFocus() {
	stopFocus();
    }
    
    public void stopFocus() {
	super.stopFocus();
	selectAll = false;
	editing = false;
	this.setEditable(false);
	if (this.isInViewHierarchy())
	    PlaywriteRoot.getMainRootView().updateCursor();
    }
    
    public void paste() {
	if (PlaywriteSystem.isMacintosh()) {
	    if (this.isEditable()) {
		Range range = this.selectedRange();
		String text = Application.clipboardText();
		if (range != null && range.index != -1 && text != null) {
		    text = text.replace('\r', '\n');
		    this.replaceRangeWithString(range, text);
		    range = new Range(range.index() + text.length(), 0);
		    this.selectRange(range);
		    this.scrollRangeToVisible(range);
		}
	    }
	} else
	    super.paste();
    }
    
    public void ensureImagesAreLoaded() {
	Vector bitmaps = getAllBitmaps();
	for (int i = 0; i < bitmaps.size(); i++) {
	    Bitmap b = (Bitmap) bitmaps.elementAt(i);
	    Util.loadImageData(b);
	}
    }
    
    public void flushAllBitmaps() {
	Vector bitmaps = getAllBitmaps();
	for (int i = 0; i < bitmaps.size(); i++) {
	    Bitmap b = (Bitmap) bitmaps.elementAt(i);
	    b.flush();
	}
    }
    
    private Vector getAllBitmaps() {
	Vector result = new Vector(1);
	Vector paragraphs
	    = this.paragraphsForRange(new Range(0, this.length()));
	for (int i = 0; i < paragraphs.size(); i++) {
	    Range range = (Range) paragraphs.elementAt(i);
	    Hashtable attributes = this.attributesAtIndex(range.index);
	    TextAttachment textAttachment
		= (TextAttachment) attributes.get("TextAttachmentKey");
	    if (textAttachment instanceof ImageAttachment) {
		COM.stagecast.ifc.netscape.application.Image image
		    = ((ImageAttachment) textAttachment).image();
		if (image instanceof Bitmap)
		    result.addElement(image);
	    }
	}
	return result;
    }
}
