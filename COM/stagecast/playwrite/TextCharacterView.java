/* TextCharacterView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.FontMetrics;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Range;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextFilter;
import COM.stagecast.ifc.netscape.application.TextParagraphFormat;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.TextViewOwner;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class TextCharacterView extends SpecialCharacterView
    implements Debug.Constants, ResourceIDs.TextCharIDs, TextViewOwner, Watcher
{
    public static final String UPDATE_SETTINGS = "update settings";
    private PlaywriteTextView textView;
    private PlaywriteView content;
    private boolean _showAsInputBox = false;
    private int _offsetX = 0;
    private int _offsetY = 0;
    private int _offsetBottom = 2;
    private int _offsetRight = 2;
    private boolean _initialized = false;
    private boolean _drawBorder;
    private boolean _characterGeneralized = false;
    private boolean _autoSize = false;
    boolean _boardWantsDraw = false;
    
    public TextCharacterView(CocoaCharacter ch, int scaleSize) {
	super(ch, scaleSize);
	init();
	_initialized = true;
    }
    
    public TextCharacterView(CocoaCharacter ch) {
	this(ch, 0);
    }
    
    protected void init() {
	CocoaCharacter tc = this.getCharacter();
	TextCharacterPrototype proto
	    = (TextCharacterPrototype) tc.getPrototype();
	textView = createTextView();
	initTextView();
	content = new PlaywriteView(0, 0, this.width(), this.height()) {
	    public boolean isTransparent() {
		return super.isTransparent() && !_boardWantsDraw;
	    }
	};
	content.setMouseTransparency(true);
	content.setBackgroundColor(textView.backgroundColor());
	content.setBorder(null);
	content.addSubview(textView);
	this.addSubview(content);
	this.setResizeButtonEnabled(true);
	dirtyAllVariables();
	this.checkGeneralization();
    }
    
    protected PlaywriteTextView createTextView() {
	return new PlaywriteTextView(0, 0, this.width(), this.height()) {
	    public void keyDown(KeyEvent event) {
		if (TextCharacterView.this.getWorld().isRunning()
		    && (event.key == 10 || (event.isExtendedKeyEvent()
					    && event.keyCode() == 10))) {
		    PlaywriteRoot.app();
		    PlaywriteRoot.getMainRootView()
			.setFocusedView(this.superview());
		} else
		    super.keyDown(event);
	    }
	    
	    public void addDirtyRect(Rect r) {
		super.addDirtyRect(r);
		int dx = TextCharacterView.this.x() + this.x();
		int dy = TextCharacterView.this.y() + this.y();
		r.moveBy(dx, dy);
		if (TextCharacterView.this.superview() != null)
		    TextCharacterView.this.superview().addDirtyRect(r);
		r.moveBy(-dx, -dy);
	    }
	};
    }
    
    protected void initTextView() {
	textView.setHorizResizeInstruction(2);
	textView.setVertResizeInstruction(16);
	textView.enableResizing();
	textView.setWantsAutoscrollEvents(false);
	textView.setOwner(this);
	TextParagraphFormat format = new TextParagraphFormat();
	format.setLeftMargin(0);
	format.setRightMargin(0);
	format.setLeftIndent(0);
	textView.addDefaultAttribute("ParagraphFormatKey", format);
	textView.setFilter(new TextFilter() {
	    public boolean acceptsEvent(Object textObject, KeyEvent event,
					Vector events) {
		for (int i = 0; i < events.size(); i++) {
		    if (event instanceof KeyEvent && event.type() == -11)
			return false;
		}
		return true;
	    }
	});
    }
    
    public PlaywriteTextView textView() {
	return textView;
    }
    
    public void dirtyAllVariables() {
	TextCharacterPrototype proto
	    = (TextCharacterPrototype) this.getCharacter().getPrototype();
	updateSettings(Variable.systemVariable((TextCharacterPrototype
						.SYS_TEXT_VARIABLE_ID),
					       proto));
	updateSettings(Variable.systemVariable((TextCharacterPrototype
						.SYS_TEXT_FONT_VARIABLE_ID),
					       proto));
	updateSettings(Variable.systemVariable((TextCharacterPrototype
						.SYS_TEXT_STYLE_VARIABLE_ID),
					       proto));
	updateSettings(Variable.systemVariable((TextCharacterPrototype
						.SYS_TEXT_SIZE_VARIABLE_ID),
					       proto));
	updateSettings(Variable.systemVariable
		       (TextCharacterPrototype.SYS_TEXT_ALIGNMENT_VARIABLE_ID,
			proto));
	updateSettings(Variable.systemVariable
		       (TextCharacterPrototype.SYS_TEXT_OFFSET_X_VARIABLE_ID,
			proto));
	updateSettings(Variable.systemVariable
		       (TextCharacterPrototype.SYS_TEXT_OFFSET_Y_VARIABLE_ID,
			proto));
	updateSettings(Variable.systemVariable((TextCharacterPrototype
						.SYS_TEXT_COLOR_VARIABLE_ID),
					       proto));
	updateSettings(Variable.systemVariable((TextCharacterPrototype
						.SYS_TEXT_BGCOLOR_VARIABLE_ID),
					       proto));
	updateSettings(Variable.systemVariable((TextCharacterPrototype
						.SYS_TEXT_BORDER_VARIABLE_ID),
					       proto));
	updateSettings
	    (Variable.systemVariable((TextCharacterPrototype
				      .SYS_TEXT_SHRINKTOFIT_VARIABLE_ID),
				     proto));
	updateSettings(Variable.systemVariable
		       (TextCharacterPrototype.SYS_TEXT_EDITABLE_VARIABLE_ID,
			proto));
    }
    
    public void didSizeBy(int dx, int dy) {
	super.didSizeBy(dx, dy);
	if (content != null) {
	    content.sizeTo(this.width() - _offsetX - _offsetRight,
			   this.height() - _offsetY - _offsetBottom);
	    content.moveTo(_offsetX, _offsetY);
	    checkAutoSize();
	}
    }
    
    public void drawSpecialView(Graphics g) {
	_boardWantsDraw = true;
	CocoaCharacter character = this.getCharacter();
	if (character.isVisible()) {
	    if (content.backgroundColor() != null) {
		g.setColor(content.backgroundColor());
		g.fillRect(0, 0, this.width(), this.height());
	    }
	    int tx = content.x();
	    int ty = content.y();
	    g.translate(tx, ty);
	    content.draw(g, null);
	    g.translate(-tx, -ty);
	    if (_showAsInputBox) {
		g.setColor(Color.black);
		g.drawRect(0, 0, this.width(), this.height());
	    } else if (_drawBorder) {
		g.setColor(Color.lightGray);
		g.drawRect(0, 0, this.width(), this.height());
	    }
	    if (this.isHilited())
		Util.drawHilited(g,
				 new Rect(0, 0, this.width(), this.height()));
	}
	_boardWantsDraw = false;
    }
    
    public void drawSubviews(Graphics g) {
	/* empty */
    }
    
    public void setOffset(int dx, int dy) {
	GenericContainer c;
	if ((c = this.getCharacter().getContainer()) instanceof Board) {
	    int squareSizeMinusOne = ((Board) c).getSquareSize() - 1;
	    if (dx >= 0)
		_offsetX = Math.min(dx, squareSizeMinusOne);
	    if (dy >= 0)
		_offsetY = Math.min(dy, squareSizeMinusOne);
	    didSizeBy(0, 0);
	}
    }
    
    public int cursorForPoint(int x, int y) {
	int cursor = super.cursorForPoint(x, y);
	if (cursor == 3
	    || this.getCharacter().getWorld().getState() == World.RUNNING)
	    return cursor;
	if (textView.bounds.contains(x, y) && !content.getMouseTransparency())
	    return textView.cursorForPoint(x, y);
	return 12;
    }
    
    public View viewForMouse(int x, int y) {
	View result = null;
	if (this.containsPoint(x, y)) {
	    CocoaCharacter tc = this.getCharacter();
	    TextCharacterPrototype proto
		= (TextCharacterPrototype) tc.getPrototype();
	    boolean readOnly = false;
	    if (this.getWorld().isRunning())
		readOnly = ((Boolean) Variable.systemVariable
					  ((TextCharacterPrototype
					    .SYS_TEXT_EDITABLE_VARIABLE_ID),
					   proto)
					  .getValue(tc))
			       .booleanValue() ^ true;
	    if (readOnly || tc instanceof GeneralizedCharacter
		|| locationIsInInvisibleBorder(x, y))
		result = this;
	    else
		result = super.viewForMouse(x, y);
	}
	return result;
    }
    
    public boolean locationIsInInvisibleBorder(int x, int y) {
	if (this.containsPoint(x, y))
	    return (((x > this.width() - _buttonOffset
		      || y > this.height() - _buttonOffset)
		     && !this.getResizeButton().bounds.contains(x, y))
		    || x < _buttonOffset || y < _buttonOffset);
	return false;
    }
    
    public void addVariableWatchers(CocoaCharacter ch) {
	TextCharacterPrototype proto
	    = (TextCharacterPrototype) ch.getPrototype();
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_FONT_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_STYLE_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_SIZE_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_ALIGNMENT_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_OFFSET_X_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_OFFSET_Y_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_COLOR_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_BGCOLOR_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_EDITABLE_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_BORDER_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_SHRINKTOFIT_VARIABLE_ID, ch)
	    .addValueWatcher(ch, this);
	if (_initialized)
	    dirtyAllVariables();
    }
    
    public void removeVariableWatchers(CocoaCharacter ch) {
	TextCharacterPrototype proto
	    = (TextCharacterPrototype) ch.getPrototype();
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_FONT_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_STYLE_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_SIZE_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_ALIGNMENT_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_OFFSET_X_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_OFFSET_Y_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_COLOR_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_BGCOLOR_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_EDITABLE_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_BORDER_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
	Variable.systemVariable
	    (TextCharacterPrototype.SYS_TEXT_SHRINKTOFIT_VARIABLE_ID, ch)
	    .removeValueWatcher(ch, this);
    }
    
    public void resize() {
	if (_initialized) {
	    update(Variable.systemVariable((TextCharacterPrototype
					    .SYS_TEXT_OFFSET_X_VARIABLE_ID),
					   this.getCharacter().getPrototype()),
		   null);
	    update(Variable.systemVariable((TextCharacterPrototype
					    .SYS_TEXT_OFFSET_Y_VARIABLE_ID),
					   this.getCharacter().getPrototype()),
		   null);
	}
	super.resize();
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
    
    public void update(Object target, Object value) {
	if (PlaywriteRoot.app().inEventThread())
	    performCommand("update settings", target);
	else
	    PlaywriteRoot.app().performCommandLater(this, "update settings",
						    target);
    }
    
    public boolean isVariableEditorEnabled(Variable v) {
	boolean enable
	    = Variable.getSystemValue
		  (TextCharacterPrototype.SYS_TEXT_EDITABLE_VARIABLE_ID,
		   this.getCharacter())
		  .equals(Boolean.FALSE);
	if (!enable) {
	    for (int i = 0;
		 i < TextCharacterInstance.EDITABLE_DISABLE_LIST.length; i++) {
		if (v.getSystemType()
		    == TextCharacterInstance.EDITABLE_DISABLE_LIST[i])
		    return false;
	    }
	}
	return true;
    }
    
    private void updateSettings(Object target) {
	CocoaCharacter tc = this.getCharacter();
	TextCharacterPrototype proto
	    = (TextCharacterPrototype) tc.getPrototype();
	Object val = Variable.systemVariable
			 (TextCharacterPrototype.SYS_TEXT_VARIABLE_ID, tc)
			 .getValue(tc);
	String s = val == null ? "" : val.toString();
	if (target instanceof Variable
	    && isVariableEditorEnabled((Variable) target)) {
	    if (target == Variable.systemVariable((TextCharacterPrototype
						   .SYS_TEXT_VARIABLE_ID),
						  proto)) {
		if (!textView.string().equals(s)) {
		    textView.setString(s);
		    checkAutoSize();
		}
	    } else if ((target
			== (Variable.systemVariable
			    (TextCharacterPrototype.SYS_TEXT_FONT_VARIABLE_ID,
			     proto)))
		       || target == (Variable.systemVariable
				     ((TextCharacterPrototype
				       .SYS_TEXT_SIZE_VARIABLE_ID),
				      proto))
		       || target == (Variable.systemVariable
				     ((TextCharacterPrototype
				       .SYS_TEXT_STYLE_VARIABLE_ID),
				      proto))) {
		Object sizeValue
		    = Variable.systemVariable
			  (TextCharacterPrototype.SYS_TEXT_SIZE_VARIABLE_ID,
			   proto)
			  .getValue(tc);
		Object fontNameValue
		    = Variable.systemVariable
			  (TextCharacterPrototype.SYS_TEXT_FONT_VARIABLE_ID,
			   proto)
			  .getValue(tc);
		Object fontStyleValue
		    = Variable.systemVariable
			  (TextCharacterPrototype.SYS_TEXT_STYLE_VARIABLE_ID,
			   proto)
			  .getValue(tc);
		if (usefulValue(sizeValue) && usefulValue(fontNameValue)
		    && usefulValue(fontStyleValue)) {
		    int fontSize = ((Number) sizeValue).intValue();
		    String fontName
			= Util.javaFontNameForUserName((String) fontNameValue);
		    int fontStyle
			= Util.javaFontStyleForUserName((String)
							fontStyleValue);
		    textView.setFont(Font.fontNamed(fontName, fontStyle,
						    fontSize));
		    checkAutoSize();
		}
	    } else if (target
		       == (Variable.systemVariable
			   (TextCharacterPrototype.SYS_TEXT_COLOR_VARIABLE_ID,
			    proto))) {
		Object o
		    = Variable.systemVariable
			  (TextCharacterPrototype.SYS_TEXT_COLOR_VARIABLE_ID,
			   proto)
			  .getValue(tc);
		if (usefulValue(o)) {
		    ColorValue color = (ColorValue) o;
		    textView.setTextColor(color.getColor());
		}
	    } else if (target == (Variable.systemVariable
				  ((TextCharacterPrototype
				    .SYS_TEXT_BGCOLOR_VARIABLE_ID),
				   proto))) {
		Object o
		    = Variable.systemVariable
			  (TextCharacterPrototype.SYS_TEXT_BGCOLOR_VARIABLE_ID,
			   proto)
			  .getValue(tc);
		if (usefulValue(o)) {
		    ColorValue color = (ColorValue) o;
		    if (color == ColorValue.transparentColor) {
			textView.setTransparent(true);
			content.setBackgroundColor(null);
			content.setTransparent(true);
		    } else {
			textView.setBackgroundColor(color.getColor());
			content.setBackgroundColor(textView.backgroundColor());
			textView.setTransparent(false);
			content.setTransparent(false);
		    }
		}
	    } else if (target
		       == (Variable.systemVariable
			   (TextCharacterPrototype.SYS_TEXT_BORDER_VARIABLE_ID,
			    proto))) {
		Object o
		    = Variable.systemVariable
			  (TextCharacterPrototype.SYS_TEXT_BORDER_VARIABLE_ID,
			   proto)
			  .getValue(tc);
		if (usefulValue(o))
		    _drawBorder = ((Boolean) o).booleanValue();
	    } else if (target == (Variable.systemVariable
				  ((TextCharacterPrototype
				    .SYS_TEXT_ALIGNMENT_VARIABLE_ID),
				   proto))) {
		Object o = Variable.systemVariable
			       ((TextCharacterPrototype
				 .SYS_TEXT_ALIGNMENT_VARIABLE_ID),
				proto)
			       .getValue(tc);
		if (usefulValue(o)) {
		    TextParagraphFormat format = new TextParagraphFormat();
		    format.setLeftMargin(0);
		    format.setRightMargin(0);
		    format.setLeftIndent(0);
		    int justification = -1;
		    if (o.equals(Resource.getText("TXTalignleftID")))
			justification = 0;
		    else if (o.equals(Resource.getText("TXTalignrightID")))
			justification = 2;
		    else if (o.equals(Resource.getText("TXTaligncenterID")))
			justification = 1;
		    if (justification != -1) {
			format.setJustification(justification);
			textView.addDefaultAttribute("ParagraphFormatKey",
						     format);
		    }
		}
	    } else if (target == (Variable.systemVariable
				  ((TextCharacterPrototype
				    .SYS_TEXT_OFFSET_X_VARIABLE_ID),
				   proto))
		       || target == (Variable.systemVariable
				     ((TextCharacterPrototype
				       .SYS_TEXT_OFFSET_Y_VARIABLE_ID),
				      proto))) {
		Object ox
		    = Variable.systemVariable((TextCharacterPrototype
					       .SYS_TEXT_OFFSET_X_VARIABLE_ID),
					      proto).getValue(tc);
		Object oy
		    = Variable.systemVariable((TextCharacterPrototype
					       .SYS_TEXT_OFFSET_Y_VARIABLE_ID),
					      proto).getValue(tc);
		if (usefulValue(ox) || usefulValue(oy)) {
		    int offx = 0;
		    int offy = 0;
		    if (ox instanceof Number)
			offx = ((Number) ox).intValue();
		    if (oy instanceof Number)
			offy = ((Number) oy).intValue();
		    setOffset(offx, offy);
		}
	    } else if (target == (Variable.systemVariable
				  ((TextCharacterPrototype
				    .SYS_TEXT_SHRINKTOFIT_VARIABLE_ID),
				   proto))) {
		boolean autoSizeFont
		    = ((Boolean) Variable.systemVariable
				     ((TextCharacterPrototype
				       .SYS_TEXT_SHRINKTOFIT_VARIABLE_ID),
				      proto)
				     .getValue(tc))
			  .booleanValue();
		setAutoSize(autoSizeFont);
	    } else
		Variable.systemVariable((TextCharacterPrototype
					 .SYS_TEXT_EDITABLE_VARIABLE_ID),
					proto);
	}
    }
    
    private void checkAutoSize() {
	if (_autoSize)
	    autoSizeFont();
    }
    
    public void setAutoSize(boolean autoSizeFont) {
	_autoSize = autoSizeFont;
	if (_autoSize)
	    autoSizeFont();
	else
	    performCommand("update settings",
			   (Variable.systemVariable
			    (TextCharacterPrototype.SYS_TEXT_SIZE_VARIABLE_ID,
			     this.getCharacter())));
    }
    
    private void autoSizeFont() {
	int optimalSize
	    = getAutoSizeFontSize(textView.string(), content.bounds(),
				  textView.font());
	Font font = textView.font();
	if (optimalSize != font.size())
	    textView.setFont(Font.fontNamed(font.name(), font.style(),
					    optimalSize));
    }
    
    private int getAutoSizeFontSize(String text, Rect textBounds,
				    Font baseFont) {
	int startFontSize = baseFont.size();
	int result = startFontSize;
	if (text != null && text.length() > 0) {
	    if (textBounds.width > 0 && textBounds.height > 0) {
		Font font = baseFont;
		FontMetrics fm = font.fontMetrics();
		int initialStringHeight
		    = getStringHeight(text, textBounds.width, fm);
		int increment = 1;
		if (initialStringHeight == -1
		    || initialStringHeight > textBounds.height)
		    increment = -1;
		boolean tooLarge;
		for (boolean end = false; !end && result > 1;
		     end = increment == 1 ? tooLarge : tooLarge ^ true) {
		    result += increment;
		    font = Font.fontNamed(baseFont.name(), baseFont.style(),
					  result);
		    fm = font.fontMetrics();
		    int newHeight
			= getStringHeight(text, textBounds.width, fm);
		    tooLarge
			= newHeight == -1 || newHeight > textBounds.height;
		}
		if (increment == 1)
		    result--;
	    } else
		result = 10;
	}
	return result;
    }
    
    private int getStringHeight(String text, int width, FontMetrics fm) {
	Vector strings = new Vector(2);
	boolean b = breakTextForWidth(text, strings, fm, width);
	if (b)
	    return -1;
	return fm.ascent() + (strings.size() - 1) * fm.height() + fm.descent();
    }
    
    private boolean breakTextForWidth(String string, Vector v, FontMetrics fm,
				      int width) {
	boolean wordWasBroken = false;
	String substr = string;
	int endPos = substr.length();
	int crPos = substr.indexOf('\r');
	int lfPos = substr.indexOf('\n');
	if (crPos >= 0 || lfPos >= 0) {
	    int breakPos = crPos == -1 ? lfPos : crPos;
	    int incr = crPos == -1 || lfPos == -1 ? 1 : 2;
	    substr = substr.substring(0, breakPos);
	    if (fm.stringWidth(substr) <= width) {
		v.addElement(substr);
		wordWasBroken
		    = breakTextForWidth(string.substring(breakPos + incr), v,
					fm, width);
		return wordWasBroken;
	    }
	    endPos = breakPos;
	}
	if (fm.stringWidth(substr) <= width || substr.length() == 1)
	    v.addElement(substr);
	else {
	    int pos = substr.lastIndexOf(' ', endPos);
	    boolean wordMode = pos > -1;
	    if (!wordMode) {
		pos = substr.length() - 1;
		wordWasBroken = true;
	    }
	    while (pos > 0) {
		substr = substr.substring(0, pos);
		if (substr.length() == 1 || fm.stringWidth(substr) <= width)
		    break;
		if (wordMode) {
		    pos = substr.lastIndexOf(' ');
		    if (pos == -1) {
			pos = substr.length() - 1;
			wordMode = false;
			wordWasBroken = true;
		    }
		} else
		    pos--;
	    }
	    v.addElement(substr);
	    if (wordMode)
		pos++;
	    boolean b = breakTextForWidth(string.substring(pos), v, fm, width);
	    wordWasBroken = b || wordWasBroken;
	}
	return wordWasBroken;
    }
    
    public boolean usefulValue(Object value) {
	return value != null;
    }
    
    public void attributesDidChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void attributesWillChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void linkWasSelected(TextView tv, Range r, String url) {
	/* empty */
    }
    
    public void selectionDidChange(TextView tv) {
	if (this.superview() != null)
	    this.superview().addDirtyRect(this.bounds());
	if (textView.isTransparent())
	    this.setDirty(true);
    }
    
    public void textWillChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void textDidChange(TextView tv, Range r) {
	if (this.superview() != null)
	    this.superview().addDirtyRect(this.bounds());
	checkAutoSize();
	if (textView.isTransparent())
	    this.setDirty(true);
    }
    
    public void textEditingDidBegin(TextView tv) {
	if (this.superview() != null)
	    this.superview().addDirtyRect(this.bounds());
    }
    
    public void textEditingDidEnd(TextView tv) {
	final CocoaCharacter tc = this.getCharacter();
	final TextCharacterPrototype proto
	    = (TextCharacterPrototype) tc.getPrototype();
	String t = textView.string();
	this.getWorld().performInWorldThread(new Target() {
	    public void performCommand(String command, Object data) {
		if (!tc.isDeleted()) {
		    Variable.systemVariable
			(TextCharacterPrototype.SYS_TEXT_VARIABLE_ID, proto)
			.modifyValue
			(tc, Resource.parseNumberString((String) data));
		    int i = tc.getWorld().getTime();
		}
	    }
	}, null, t, false);
    }
    
    public void performCommand(String command, Object data) {
	if (command == "update settings") {
	    updateSettings(data);
	    this.setDirty(true);
	} else
	    super.performCommand(command, data);
    }
}
