/* PlaywriteTextField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.FontMetrics;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.LineBorder;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFieldOwner;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PlaywriteTextField extends TextField
    implements ResourceIDs.DialogIDs
{
    private boolean mouseTransparency = false;
    private boolean userEditable = true;
    private Color _backgroundColor = null;
    private Color _textColor = null;
    private boolean _isTransparent = false;
    private boolean _resetsColors = false;
    private boolean _hasFocus = false;
    private boolean _isDraggable = true;
    private boolean _maxSizeSet = false;
    private int _minWidth;
    private int _minHeight;
    private int _maxWidth;
    private int _maxHeight;
    private long _commandEventTimeStamp;
    
    private class TFBorder extends LineBorder
    {
	private boolean _drawing = false;
	
	public TFBorder() {
	    super(Color.gray);
	}
	
	public void drawInRect(Graphics g, int x, int y, int width,
			       int height) {
	    if (_drawing)
		super.drawInRect(g, x, y, width, height);
	}
	
	public void setDrawing(boolean drawing) {
	    _drawing = drawing;
	}
	
	public boolean getDrawing() {
	    return _drawing;
	}
	
	public int leftMargin() {
	    return 2;
	}
	
	public int rightMargin() {
	    return 2;
	}
	
	public int topMargin() {
	    return 2;
	}
	
	public int bottomMargin() {
	    return 2;
	}
    }
    
    public PlaywriteTextField() {
	init();
    }
    
    public PlaywriteTextField(Rect bounds) {
	super(bounds);
	init();
    }
    
    public PlaywriteTextField(int x, int y, int width, int height) {
	super(x, y, width, height);
	init();
    }
    
    private void init() {
	this.setBorder(new TFBorder());
	setEditable(false);
	this.setSelectionColor(Util.textSelectionColor);
    }
    
    private boolean getMouseTransparency() {
	return mouseTransparency && !this.isEditable();
    }
    
    private void setMouseTransparency(boolean flag) {
	mouseTransparency = flag;
    }
    
    Object getModelObject() {
	return this.stringValue();
    }
    
    public void setEditIndication(boolean resetEm) {
	_resetsColors = resetEm;
    }
    
    public void setDraggable(boolean b) {
	_isDraggable = b;
    }
    
    private boolean getDraggable() {
	return _isDraggable;
    }
    
    public void setUserEditable(boolean editable) {
	userEditable = editable;
    }
    
    public void setOurMinSize(int width, int height) {
	_minWidth = width;
	_minHeight = height;
    }
    
    public void setOurMaxSize(int width, int height) {
	_maxSizeSet = true;
	_maxWidth = width;
	_maxHeight = height;
    }
    
    public int getOurMinWidth() {
	return _minWidth;
    }
    
    public Size minSize() {
	Size size = super.minSize();
	size.width = Math.max(size.width, _minWidth);
	size.height = Math.max(size.height, _minHeight);
	if (_maxSizeSet && _maxWidth > 0 && size.width > _maxWidth) {
	    this.setMinSize(_maxWidth, size.height);
	    size = super.minSize();
	}
	if (_maxSizeSet && _maxHeight > 0 && size.height > _maxHeight)
	    size.height = _maxHeight;
	return size;
    }
    
    public int cursorForPoint(int x, int y) {
	if (getMouseTransparency() && getDraggable()) {
	    Point newPoint = this.convertToView(this.superview(), x, y);
	    return this.superview().cursorForPoint(newPoint.x, newPoint.y);
	}
	return super.cursorForPoint(x, y);
    }
    
    public void paste() {
	super.paste();
	TextFieldOwner owner = this.owner();
	if (owner != null)
	    owner.textWasModified(this);
    }
    
    public void cut() {
	super.cut();
	TextFieldOwner owner = this.owner();
	if (owner != null)
	    owner.textWasModified(this);
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
		cut();
		break;
	    default:
		break;
	    }
	} else
	    super.keyDown(keyEvent);
    }
    
    public void keyTyped(KeyEvent keyEvent) {
	if (keyEvent.timeStamp() != _commandEventTimeStamp)
	    super.keyTyped(keyEvent);
    }
    
    public void setEditable(boolean editable) {
	super.setEditable(editable);
	setMouseTransparency(editable ^ true);
    }
    
    public void startFocus() {
	Selection.resetGlobalState();
	if (_resetsColors && userEditable) {
	    _textColor = this.textColor();
	    _backgroundColor = this.backgroundColor();
	    _isTransparent = this.isTransparent();
	    if (this.border() instanceof TFBorder)
		((TFBorder) this.border()).setDrawing(true);
	    this.setBackgroundColor(Util.textBackgroundColor);
	    this.setTextColor(Util.textColor);
	    this.setTransparent(false);
	    _hasFocus = true;
	    this.sizeToMinSize();
	}
	super.startFocus();
    }
    
    public void pauseFocus() {
	stopFocus();
    }
    
    public void stopFocus() {
	if (_resetsColors && userEditable) {
	    this.setBackgroundColor(_backgroundColor);
	    this.setTextColor(_textColor);
	    this.setTransparent(_isTransparent);
	    if (this.border() instanceof TFBorder)
		((TFBorder) this.border()).setDrawing(false);
	    this.setDirty(true);
	    _hasFocus = false;
	}
	super.stopFocus();
	setEditable(false);
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (getMouseTransparency()) {
	    if (getDraggable())
		return (this.superview().mouseDown
			(this.convertEventToView(this.superview(), event)));
	    return true;
	}
	return super.mouseDown(event);
    }
    
    public void mouseUp(MouseEvent event) {
	if (getMouseTransparency()) {
	    if (getDraggable())
		this.superview()
		    .mouseUp(this.convertEventToView(this.superview(), event));
	    if (userEditable) {
		setEditable(true);
		this.selectText();
	    }
	} else
	    super.mouseUp(event);
    }
    
    public void mouseDragged(MouseEvent event) {
	if (getMouseTransparency() && getDraggable())
	    this.superview().mouseDragged
		(this.convertEventToView(this.superview(), event));
	else
	    super.mouseDragged(event);
    }
    
    public void mouseEntered(MouseEvent event) {
	if (getMouseTransparency() && getDraggable())
	    this.superview().mouseEntered
		(this.convertEventToView(this.superview(), event));
	else
	    super.mouseEntered(event);
    }
    
    public void mouseExited(MouseEvent event) {
	if (getMouseTransparency() && getDraggable())
	    this.superview()
		.mouseExited(this.convertEventToView(this.superview(), event));
	else
	    super.mouseExited(event);
    }
    
    public void setStringValue(String newValue) {
	if (!newValue.equals(this.stringValue()))
	    super.setStringValue(newValue);
    }
    
    public final void willBecomeSelected() {
	/* empty */
    }
    
    public final void willBecomeUnselected() {
	/* empty */
    }
    
    public void drawViewStringAt(Graphics g, String aString, int x, int y) {
	if (x < 0) {
	    FontMetrics fontMetrics = this.font().fontMetrics();
	    String contentString = this.stringValue();
	    int stringWidth = fontMetrics.stringWidth(contentString);
	    if (stringWidth < this.width() || !_maxSizeSet)
		x = 0;
	}
	super.drawViewStringAt(g, aString, x, y);
    }
    
    public int xPositionOfCharacter(int charNumber) {
	FontMetrics fontMetrics = this.font().fontMetrics();
	String contentString = this.stringValue();
	int stringWidth = fontMetrics.stringWidth(contentString);
	if (stringWidth > this.width() && _maxSizeSet)
	    return super.xPositionOfCharacter(charNumber);
	int startX = super.xPositionOfCharacter(-1);
	if (startX < 0)
	    startX = 0;
	if (charNumber <= 0)
	    return startX;
	return startX + fontMetrics.stringWidth(contentString
						    .substring(0, charNumber));
    }
    
    public int charNumberForPoint(int x) {
	int contentLength = this.stringValue().length();
	if (contentLength == 0)
	    return 0;
	FontMetrics fontMetrics = this.font().fontMetrics();
	String contentString = this.stringValue();
	int stringWidth = fontMetrics.stringWidth(contentString);
	if (_maxSizeSet && stringWidth > this.width())
	    return super.charNumberForPoint(x);
	int startX = super.xPositionOfCharacter(-1);
	if (startX < 0)
	    startX = 0;
	if (x < startX)
	    return 0;
	if (x > startX + stringWidth)
	    return contentLength;
	int oldWidth = 0;
	for (int i = 1; i < contentLength; i++) {
	    int width = fontMetrics.stringWidth(contentString.substring(0, i));
	    int delta = width - oldWidth;
	    if (x <= startX + width) {
		if (x > startX + oldWidth + delta / 2)
		    return i;
		return i - 1;
	    }
	    oldWidth = width;
	}
	return contentLength;
    }
}
