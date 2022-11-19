/* ColorChooser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class ColorChooser implements Target, TextFieldOwner
{
    ColorWell _colorWell;
    Slider _redSlider;
    Slider _greenSlider;
    Slider _blueSlider;
    TextField _redTextField;
    TextField _greenTextField;
    TextField _blueTextField;
    private ContainerView contentView = new ContainerView(0, 0, 157, 113);
    private Window window;
    private static final String COLOR_CHANGED = "colorChanged";
    
    public ColorChooser() {
	contentView.setBackgroundColor(Color.lightGray);
	contentView.setBorder(null);
	contentView.setHorizResizeInstruction(2);
	contentView.setVertResizeInstruction(16);
	_colorWell = new ColorWell(3, 3, 152, 40);
	_colorWell.setHorizResizeInstruction(2);
	_colorWell.setVertResizeInstruction(16);
	_colorWell.setTarget(this);
	contentView.addSubview(_colorWell);
	_redSlider = new Slider(3, 50, 120, 13);
	_redSlider.setBuffered(true);
	_redSlider.setImageDisplayStyle(1);
	_redSlider
	    .setImage(Bitmap.bitmapNamed("netscape/application/RedGrad.gif"));
	_redSlider.setTarget(this);
	_redSlider.setCommand("colorChanged");
	_redSlider.setValue(255);
	_redSlider.setHorizResizeInstruction(2);
	_redSlider.setVertResizeInstruction(8);
	contentView.addSubview(_redSlider);
	_greenSlider = new Slider(3, 68, 120, 13);
	_greenSlider.setBuffered(true);
	_greenSlider.setImageDisplayStyle(1);
	_greenSlider.setImage
	    (Bitmap.bitmapNamed("netscape/application/GreenGrad.gif"));
	_greenSlider.setTarget(this);
	_greenSlider.setCommand("colorChanged");
	_greenSlider.setValue(255);
	_greenSlider.setHorizResizeInstruction(2);
	_greenSlider.setVertResizeInstruction(8);
	contentView.addSubview(_greenSlider);
	_blueSlider = new Slider(3, 86, 120, 13);
	_blueSlider.setBuffered(true);
	_blueSlider.setImageDisplayStyle(1);
	_blueSlider
	    .setImage(Bitmap.bitmapNamed("netscape/application/BlueGrad.gif"));
	_blueSlider.setTarget(this);
	_blueSlider.setCommand("colorChanged");
	_blueSlider.setValue(255);
	_blueSlider.setHorizResizeInstruction(2);
	_blueSlider.setVertResizeInstruction(8);
	contentView.addSubview(_blueSlider);
	Font font = Font.fontNamed("Helvetica", 0, 10);
	_redTextField = new TextField(125, 49, 30, 16);
	_redTextField.setContentsChangedCommandAndTarget("", this);
	_redTextField.setFont(font);
	_redTextField.setTextColor(Color.darkGray);
	_redTextField.setBackgroundColor(Color.white);
	_redTextField.setIntValue(_redSlider.value());
	_redTextField.setHorizResizeInstruction(1);
	_redTextField.setVertResizeInstruction(8);
	contentView.addSubview(_redTextField);
	_greenTextField = new TextField(125, 67, 30, 16);
	_greenTextField.setContentsChangedCommandAndTarget("", this);
	_greenTextField.setFont(font);
	_greenTextField.setTextColor(Color.darkGray);
	_greenTextField.setBackgroundColor(Color.white);
	_greenTextField.setIntValue(_greenSlider.value());
	_greenTextField.setHorizResizeInstruction(1);
	_greenTextField.setVertResizeInstruction(8);
	contentView.addSubview(_greenTextField);
	_blueTextField = new TextField(125, 85, 30, 16);
	_blueTextField.setIntValue(_blueSlider.value());
	_blueTextField.setTextColor(Color.darkGray);
	_blueTextField.setBackgroundColor(Color.white);
	_blueTextField.setContentsChangedCommandAndTarget("", this);
	_blueTextField.setFont(font);
	_blueTextField.setHorizResizeInstruction(1);
	_blueTextField.setVertResizeInstruction(8);
	contentView.addSubview(_blueTextField);
	_redTextField.setOwner(this);
	_blueTextField.setOwner(this);
	_updateColorWell();
    }
    
    public void show() {
	if (window != null)
	    window.show();
    }
    
    public void hide() {
	if (window != null)
	    window.hide();
    }
    
    private void _updateColorWell() {
	_colorWell.setColor(new Color(_redSlider.value(), _greenSlider.value(),
				      _blueSlider.value()));
    }
    
    public void setColor(Color color) {
	if (color != null) {
	    _redSlider.setValue(color.red());
	    _redTextField.setIntValue(color.red());
	    _greenSlider.setValue(color.green());
	    _greenTextField.setIntValue(color.green());
	    _blueSlider.setValue(color.blue());
	    _blueTextField.setIntValue(color.blue());
	    _updateColorWell();
	}
    }
    
    public Color color() {
	return _colorWell.color();
    }
    
    public void performCommand(String string, Object object) {
	if (object == _redTextField) {
	    _redSlider.setValue(_redTextField.intValue());
	    _redTextField.setIntValue(_redSlider.value());
	} else if (object == _greenTextField) {
	    _greenSlider.setValue(_greenTextField.intValue());
	    _greenTextField.setIntValue(_greenSlider.value());
	} else if (object == _blueTextField) {
	    _blueSlider.setValue(_blueTextField.intValue());
	    _blueTextField.setIntValue(_blueSlider.value());
	} else if (object == _redSlider)
	    _redTextField.setIntValue(_redSlider.value());
	else if (object == _greenSlider)
	    _greenTextField.setIntValue(_greenSlider.value());
	else if (object == _blueSlider)
	    _blueTextField.setIntValue(_blueSlider.value());
	else if (object == _colorWell) {
	    setColor(_colorWell.color());
	    return;
	}
	_updateColorWell();
    }
    
    public void textEditingDidBegin(TextField textfield) {
	/* empty */
    }
    
    public void textWasModified(TextField textfield) {
	/* empty */
    }
    
    public boolean textEditingWillEnd(TextField textfield, int i,
				      boolean bool) {
	return true;
    }
    
    public void textEditingDidEnd(TextField textfield, int i, boolean bool) {
	/* empty */
    }
    
    public void setWindow(Window window) {
	this.window = window;
	Size size = this.window.windowSizeForContentSize(contentView.width(),
							 contentView.height());
	this.window.sizeTo(size.width, size.height);
	this.window.setTitle("Color Chooser");
	if (this.window instanceof InternalWindow) {
	    InternalWindow internalwindow = (InternalWindow) this.window;
	    internalwindow.setCloseable(true);
	}
	this.window.setContainsDocument(false);
	this.window.addSubview(contentView);
    }
    
    public Window window() {
	return window;
    }
    
    public View contentView() {
	return contentView;
    }
}
