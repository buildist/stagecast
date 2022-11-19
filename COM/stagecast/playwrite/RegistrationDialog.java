/* RegistrationDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.ContainerView;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class RegistrationDialog extends InternalWindow
    implements Target, ResourceIDs.RegistrationIDs
{
    private static final int MAX_TRIES = 3;
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 200;
    private static final String REG_COMMAND = "REG_REG";
    private static final String CANCEL_COMMAND = "REG_CANCEL";
    private static final String PHASE1_COMMAND = "PH1";
    private static final String PHASE4_COMMAND = "PH4";
    private TextField _userName;
    private TextField _serialNumber;
    private Font _textFont;
    private Font _labelFont;
    private Font _userFont;
    private Font _buttonFont;
    private int _tries;
    
    RegistrationDialog() {
	super(1, 0, 0, 300, 200);
	this.setTransparent(false);
	this.setResizable(false);
	this.setCloseable(false);
	this.setCanBecomeMain(false);
	this.setLayer(100);
	this.setTitle(Resource.getText("reg ti"));
	_textFont = new Font("San Serif", 0, 10);
	_labelFont = new Font("Dialog", 1, 12);
	_userFont = new Font("Dialog", 0, 12);
	_buttonFont = new Font("Dialog", 0, 10);
	_tries = 0;
	performCommand("PH1", null);
    }
    
    private void emptyOut() {
	Object[] subviews = this.contentView().subviews().elementArray();
	for (int i = subviews.length - 1; i >= 0; i--)
	    ((View) subviews[i]).removeFromSuperview();
    }
    
    private void phase1() {
	emptyOut();
	TextView _message = new TextView(0, 0, 300, 200);
	_message.setFont(_textFont);
	_message.setString(Resource.getText("reg ms"));
	_message.setEditable(false);
	_message.sizeToMinSize();
	Label _name = new Label(Resource.getText("reg nl"), _labelFont);
	_name.sizeToMinSize();
	_userName = new TextField(0, 0, 20, 12);
	_userName.setFont(_userFont);
	Label _serial = new Label(Resource.getText("reg sl"), _labelFont);
	_serial.sizeToMinSize();
	_serialNumber = new TextField(0, 0, 20, 12);
	_serialNumber.setFont(_userFont);
	Button _okButton = Button.createPushButton(0, 0, 50, 16);
	_okButton.setFont(_buttonFont);
	_okButton.setTitle(Resource.getText("regb re"));
	_okButton.setTarget(this);
	_okButton.setCommand("REG_REG");
	_okButton.sizeToMinSize();
	Button _cancelButton = Button.createPushButton(0, 0, 50, 16);
	_cancelButton.setFont(_buttonFont);
	_cancelButton.setTitle(Resource.getText("regb can"));
	_cancelButton.setTarget(this);
	_cancelButton.setCommand("PH4");
	_cancelButton.sizeToMinSize();
	ContainerView msgView = new ContainerView(_message.bounds());
	msgView.setBorder(null);
	msgView.addSubview(_message);
	ContainerView nameView = new ContainerView(0, 0, 300, _name.height());
	nameView.setBorder(null);
	PackConstraints constraints = new PackConstraints();
	PackLayout layout = new PackLayout();
	nameView.setLayoutManager(layout);
	nameView.addSubview(_name);
	constraints.setSide(2);
	constraints.setExpand(false);
	layout.setConstraints(_name, constraints);
	nameView.addSubview(_userName);
	constraints.setSide(3);
	constraints.setExpand(true);
	constraints.setFillX(true);
	layout.setConstraints(_userName, constraints);
	nameView.layoutView(0, 0);
	ContainerView serialView
	    = new ContainerView(0, 0, 300, _serial.height());
	serialView.setBorder(null);
	constraints = new PackConstraints();
	layout = new PackLayout();
	serialView.setLayoutManager(layout);
	serialView.addSubview(_serial);
	constraints.setSide(2);
	constraints.setExpand(false);
	layout.setConstraints(_serial, constraints);
	serialView.addSubview(_serialNumber);
	constraints.setSide(3);
	constraints.setExpand(true);
	constraints.setFillX(true);
	layout.setConstraints(_serialNumber, constraints);
	serialView.layoutView(0, 0);
	ContainerView buttonView
	    = new ContainerView(0, 0, 300, _okButton.height());
	buttonView.setBorder(null);
	constraints = new PackConstraints();
	layout = new PackLayout();
	buttonView.setLayoutManager(layout);
	buttonView.addSubview(_okButton);
	constraints.setExpand(true);
	constraints.setSide(2);
	constraints.setInternalPadY(5);
	layout.setConstraints(_okButton, constraints);
	buttonView.addSubview(_cancelButton);
	constraints.setSide(3);
	layout.setConstraints(_cancelButton, constraints);
	buttonView.layoutView(0, 0);
	ContainerView contentView
	    = new ContainerView(0, 0, 300,
				(_message.height() + nameView.height()
				 + serialView.height() + buttonView.height()
				 + 20));
	constraints = new PackConstraints();
	layout = new PackLayout();
	contentView.setLayoutManager(layout);
	contentView.addSubview(msgView);
	constraints.setSide(0);
	constraints.setFillX(true);
	layout.setConstraints(msgView, constraints);
	contentView.addSubview(nameView);
	layout.setConstraints(nameView, constraints);
	contentView.addSubview(serialView);
	layout.setConstraints(serialView, constraints);
	contentView.addSubview(buttonView);
	constraints.setInternalPadY(5);
	layout.setConstraints(buttonView, constraints);
	contentView.layoutView(0, 0);
	contentView.sizeToMinSize();
	Size sz = this.windowSizeForContentSize(contentView.width(),
						contentView.height());
	this.sizeTo(sz.width, sz.height);
	this.addSubview(contentView);
	this.layoutParts();
	this.setDirty(true);
    }
    
    private void phase4() {
	emptyOut();
	TextView message = new TextView(0, 0, 300, 200);
	message.setFont(_textFont);
	message.setString(Resource.getText("reg fa"));
	message.setEditable(false);
	message.sizeToMinSize();
	Button okButton = Button.createPushButton(0, 0, 50, 16);
	okButton.setFont(_buttonFont);
	okButton.setTitle(Resource.getText("regb ok"));
	okButton.setTarget(this);
	okButton.setCommand("REG_CANCEL");
	okButton.sizeToMinSize();
	ContainerView msgView = new ContainerView(message.bounds());
	msgView.setBorder(null);
	msgView.addSubview(message);
	ContainerView contentView
	    = new ContainerView(0, 0, 300,
				message.height() + okButton.height() + 20);
	PackConstraints constraints = new PackConstraints();
	PackLayout layout = new PackLayout();
	contentView.setLayoutManager(layout);
	contentView.addSubview(msgView);
	constraints.setSide(0);
	constraints.setFillX(true);
	layout.setConstraints(msgView, constraints);
	contentView.addSubview(okButton);
	constraints.setInternalPadY(5);
	constraints.setFillX(false);
	layout.setConstraints(okButton, constraints);
	contentView.layoutView(0, 0);
	contentView.sizeToMinSize();
	Size sz = this.windowSizeForContentSize(contentView.width(),
						contentView.height());
	this.sizeTo(sz.width, sz.height);
	this.addSubview(contentView);
	this.layoutParts();
	this.setDirty(true);
    }
    
    public void show() {
	this.center();
	super.show();
    }
    
    public void hide() {
	super.hide();
    }
    
    public void performCommand(String command, Object data) {
	if ("PH1".equals(command))
	    phase1();
	else if ("PH4".equals(command))
	    phase4();
	else if ("REG_REG".equals(command)) {
	    _tries++;
	    if (PlaywriteRoot.app()
		    .serialNumberIsValid(_serialNumber.stringValue())) {
		PlaywriteRoot.app().setPreference("RegisteredUser",
						  _userName.stringValue());
		PlaywriteRoot.app().setPreference("SerialNumber",
						  _serialNumber.stringValue());
		hide();
	    } else {
		PlaywriteSystem.beep();
		if (_tries < 3)
		    _serialNumber.setStringValue("");
		else
		    phase4();
	    }
	} else if ("REG_CANCEL".equals(command))
	    hide();
	else
	    super.performCommand(command, data);
    }
}
