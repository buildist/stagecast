/* PlaywriteDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PlaywriteDialog extends InternalWindow
    implements ResourceIDs.CommandIDs, Target
{
    private static final int defaultWidth = 300;
    private static final int defaultHeight = 200;
    private static final int borderInset = 15;
    private static final int buttonSeparation = 15;
    private static final String GET_ANSWER = "GetAnswer";
    private Button[] _buttons;
    private TextField textField;
    private TextField textField2;
    private String answer;
    private int bottom;
    private boolean _echoCharacters;
    private int _defaultButton;
    
    public static class DefaultButton extends Button
    {
	static final int LINE_WIDTH = 1;
	private Button _baseButton;
	
	public DefaultButton(Button baseButton) {
	    super(baseButton.bounds.x - 1, baseButton.bounds.y - 1,
		  baseButton.bounds.width + 2, baseButton.bounds.height + 2);
	    baseButton.moveTo(1, 1);
	    this.addSubview(baseButton);
	    _baseButton = baseButton;
	}
	
	public Button getBaseButton() {
	    return _baseButton;
	}
	
	public void drawView(Graphics g) {
	    g.translate(1, 1);
	    _baseButton.drawView(g);
	    g.translate(-1, -1);
	    Rect r = bounds;
	    g.setColor(Color.black);
	    g.drawRect(0, 0, r.width, r.height);
	}
	
	public void sendCommand() {
	    _baseButton.sendCommand();
	}
    }
    
    public static PlaywriteDialog createInteractiveDialog
	(ResourceBundle bundle, String resourceID) {
	PlaywriteDialog interactiveDialog
	    = new PlaywriteDialog(bundle, resourceID, null, null, true);
	interactiveDialog.textField.setEditable(true);
	return interactiveDialog;
    }
    
    PlaywriteDialog(String messageID, String button1ID, String button2ID,
		    String button3ID, String button4ID) {
	super(100, 100, 300, 200);
	_buttons = new Button[4];
	textField = null;
	textField2 = null;
	bottom = 0;
	_echoCharacters = true;
	_defaultButton = -1;
	this.setCloseable(false);
	this.setResizable(false);
	this.setTitle(null);
	this.setBorder(BezelBorder.groovedBezel());
	textField = makeAnswerTextField(15, 15, this.width() - 30, 0, true);
	textField.setWrapsContents(true);
	textField.setStringValue(Resource.getText(messageID));
	textField.sizeToMinSize();
	textField.setEditable(false);
	this.addSubview(textField);
	String[] inButtonIDs = { button1ID, button2ID, button3ID, button4ID };
	for (int i = 0; i < inButtonIDs.length; i++) {
	    if (inButtonIDs[i] != null)
		_buttons[i] = makeDialogButton(inButtonIDs[i], 0, 0);
	}
	layoutButtons();
	setDefaultButton(0);
	alignDialog();
    }
    
    public PlaywriteDialog(String messageID, String confirmPasswordMessageID,
			   String optionalButtonID, boolean echoCharacters) {
	this(Resource.getTextResourceBundle(), messageID,
	     confirmPasswordMessageID, optionalButtonID, echoCharacters);
    }
    
    public PlaywriteDialog(ResourceBundle bundle, String messageID,
			   String confirmPasswordMessageID,
			   String optionalButtonID, boolean echoCharacters) {
	super(100, 100, 300, 200);
	_buttons = new Button[4];
	textField = null;
	textField2 = null;
	bottom = 0;
	_echoCharacters = true;
	_defaultButton = -1;
	_echoCharacters = echoCharacters;
	this.setCloseable(false);
	this.setResizable(false);
	this.setTitle(null);
	this.setBorder(BezelBorder.groovedBezel());
	TextView messageView
	    = makeMessageView(15, 15, 270, 200, bundle, messageID);
	this.addSubview(messageView);
	textField
	    = makeAnswerTextField(15,
				  messageView.y() + messageView.height() + 10,
				  this.width() - 30, 12, echoCharacters);
	this.addSubview(textField);
	int y = textField.y() + textField.height() + 10;
	if (confirmPasswordMessageID != null) {
	    TextView messageView2
		= makeMessageView(textField.x(), y, textField.width(),
				  textField.height(), bundle,
				  confirmPasswordMessageID);
	    this.addSubview(messageView2);
	    y += messageView2.height() + 10;
	    textField2
		= makeAnswerTextField(textField.x(), y, textField.width(),
				      textField.height(), echoCharacters);
	    this.addSubview(textField2);
	    textField.setTabField(textField2);
	    textField2.setTabField(textField);
	}
	_buttons[0] = makeDialogButton("command ok", 0, y);
	if (optionalButtonID != null)
	    _buttons[1] = makeDialogButton(optionalButtonID, 0, y);
	_buttons[2] = makeDialogButton("command c", 0, y);
	setDefaultButton(0);
	layoutButtons();
	alignDialog();
    }
    
    PlaywriteDialog(String messageID, String button1ID) {
	this(messageID, button1ID, null, null, null);
    }
    
    public PlaywriteDialog(String messageID, String button1ID,
			   String button2ID) {
	this(messageID, button1ID, button2ID, null, null);
    }
    
    public PlaywriteDialog(String messageID, String button1ID,
			   String button2ID, String button3ID) {
	this(messageID, button1ID, button2ID, button3ID, null);
    }
    
    public PlaywriteDialog(String messageID, boolean echoCharacters) {
	this(messageID, null, null, echoCharacters);
    }
    
    public PlaywriteDialog(String messageID, String optionalButtonID,
			   boolean echoCharacters) {
	this(messageID, null, optionalButtonID, echoCharacters);
    }
    
    public void setDefaultButton(int n) {
	if (n < _buttons.length && _defaultButton != n) {
	    if (_defaultButton != -1) {
		_buttons[_defaultButton].removeFromSuperview();
		_buttons[_defaultButton]
		    = ((DefaultButton) _buttons[_defaultButton])
			  .getBaseButton();
		this.addSubview(_buttons[_defaultButton]);
	    }
	    _defaultButton = _buttons[n] == null ? -1 : n;
	    if (_defaultButton != -1) {
		_buttons[_defaultButton].removeFromSuperview();
		_buttons[_defaultButton]
		    = new DefaultButton(_buttons[_defaultButton]);
		this.addSubview(_buttons[_defaultButton]);
	    }
	    layoutButtons();
	    this.setDirty(true);
	}
    }
    
    public void layoutButtons() {
	TextField lowerTextField = textField2 != null ? textField2 : textField;
	int x = lowerTextField.bounds.maxX() + 15;
	int bottom = 0;
	int maxHeight = 0;
	for (int i = 0; i < _buttons.length; i++) {
	    if (_buttons[i] != null)
		maxHeight = Math.max(maxHeight, _buttons[i].height());
	}
	int y = lowerTextField.bounds.maxY() + 10 + maxHeight / 2;
	for (int i = _buttons.length - 1; i > -1; i--) {
	    if (_buttons[i] != null) {
		x = x - _buttons[i].width() - 15;
		_buttons[i].moveTo(x, y - _buttons[i].height() / 2);
		bottom = Math.max(bottom, _buttons[i].bounds.maxY());
	    }
	}
	if (x < 0) {
	    x = 15;
	    for (int i = 0; i < _buttons.length; i++) {
		if (_buttons[i] != null) {
		    _buttons[i].moveTo(x, _buttons[i].y());
		    x = x + _buttons[i].width() + 15;
		}
	    }
	}
	this.sizeTo(Math.max(x, textField.width() + 30), bottom + 15);
    }
    
    private TextView makeMessageView(int x, int y, int width, int height,
				     ResourceBundle bundle,
				     String resourceID) {
	TextView result = new TextView(x, y, width, height);
	result.setVertResizeInstruction(16);
	result.setString(Resource.getText(bundle, resourceID));
	result.sizeToMinSize();
	result.setEditable(false);
	result.setBackgroundColor(Color.lightGray);
	return result;
    }
    
    protected boolean handleKey(KeyEvent event) {
	if (event.key == 10
	    || event.isExtendedKeyEvent() && event.keyCode() == 10
	    || event.key == 27
	    || event.isExtendedKeyEvent() && event.keyCode() == 27
	    || event.key == 1006 || event.key == 1007) {
	    if (event.key == 1006 && _defaultButton != -1) {
		int n = _defaultButton - 1;
		if (n < 0)
		    n += _buttons.length;
		for (/**/; _buttons[n] == null;
		     n = (n - 1) % _buttons.length) {
		    /* empty */
		}
		setDefaultButton(n);
	    } else if (event.key == 1007 && _defaultButton != -1) {
		int n;
		for (n = (_defaultButton + 1) % _buttons.length;
		     _buttons[n] == null; n = (n + 1) % _buttons.length) {
		    /* empty */
		}
		setDefaultButton(n);
	    } else if (event.key == 10 || (event.isExtendedKeyEvent()
					   && event.keyCode() == 10)) {
		if (_defaultButton != -1)
		    _buttons[_defaultButton].sendCommand();
	    } else {
		for (int i = 0; i < _buttons.length; i++) {
		    if (_buttons[i] != null
			&& "command c".equals(_buttons[i].command())) {
			_buttons[i].sendCommand();
			break;
		    }
		}
	    }
	    return true;
	}
	return false;
    }
    
    private TextField makeAnswerTextField(int x, int y, int width, int height,
					  boolean echoCharacters) {
	TextField result = new TextField(x, y, width, height) {
	    public void keyDown(KeyEvent event) {
		if (!handleKey(event))
		    super.keyDown(event);
	    }
	};
	if (!echoCharacters)
	    result.setDrawableCharacter('*');
	result.setWrapsContents(false);
	result.sizeToMinSize();
	result.sizeTo(width, result.height());
	return result;
    }
    
    public String getTypedText() {
	return textField.stringValue();
    }
    
    public void didBecomeMain() {
	if (textField.isEditable())
	    this.setFocusedView(textField);
	else
	    this.setFocusedView(this);
	super.didBecomeMain();
    }
    
    public void keyDown(KeyEvent event) {
	if (!handleKey(event))
	    super.keyDown(event);
    }
    
    protected TextField getTextField() {
	return textField;
    }
    
    public void alignDialog() {
	this.center();
    }
    
    public String getAnswer() {
	PlaywriteRoot.app().performCommandAndWait(this, "GetAnswer", null);
	return answer;
    }
    
    public String getAnswerModally() {
	this.setFocusedView(this);
	showModally();
	return answer;
    }
    
    private Button makeDialogButton(String name, int x, int y) {
	Button button = Button.createPushButton(0, 0, 0, 0);
	button.setTitle(Resource.getText(name));
	button.sizeToMinSize();
	button.setTarget(this);
	button.setCommand(name);
	button.moveTo(x - button.width(), y);
	this.addSubview(button);
	return button;
    }
    
    public void performCommand(String command, Object data) {
	if (command.equalsIgnoreCase("GetAnswer"))
	    showModally();
	else if (command.equals("command ok") && textField2 != null
		 && textField != null
		 && !textField2.stringValue()
			 .equals(textField.stringValue())) {
	    textField.setInsertionPoint(0);
	    textField.setStringValue("");
	    textField2.setInsertionPoint(0);
	    textField2.setStringValue("");
	    PlaywriteSystem.beep();
	    textField.setFocusedView();
	} else {
	    answer = command;
	    this.hide();
	}
    }
    
    public void showModally() {
	ToolSession.cancelCurrentSession();
	super.showModally();
    }
    
    public static void warning(String message) {
	if (message.equals(PlaywriteRoot.getLastWarning()))
	    queueWarning(message);
	else {
	    PlaywriteSystem.beep();
	    PlaywriteRoot.setLastWarning(message);
	}
    }
    
    public static void warning(String message, boolean alwaysDisplayDialog) {
	if (alwaysDisplayDialog) {
	    PlaywriteSystem.beep();
	    queueWarning(message);
	    PlaywriteRoot.setLastWarning(message);
	} else
	    warning(message);
    }
    
    private static void queueWarning(String message) {
	PlaywriteRoot app = PlaywriteRoot.app();
	app.performCommandLater(app, "wng", message);
    }
}
