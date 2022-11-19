/* FontChooser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class FontChooser implements Target
{
    ListView _nameList;
    Popup _sizePopup;
    Popup _stylePopup;
    TextField _sizeTextField;
    TextField _messageTextField;
    Button _setButton;
    Font _currentFont;
    private ContainerView contentView;
    private Window window;
    
    public FontChooser() {
	String[] strings = new String[4];
	int[] is = new int[4];
	int[] is_0_ = new int[6];
	contentView = new ContainerView(0, 0, 178, 120);
	contentView.setBackgroundColor(Color.lightGray);
	contentView.setBorder(null);
	contentView.setHorizResizeInstruction(2);
	contentView.setVertResizeInstruction(16);
	ScrollGroup scrollgroup = new ScrollGroup(4, 19, 90, 61);
	scrollgroup.setHasVertScrollBar(true);
	scrollgroup.setBorder(BezelBorder.loweredBezel());
	scrollgroup.setHorizResizeInstruction(2);
	scrollgroup.setVertResizeInstruction(16);
	Rect rect = scrollgroup.scrollView().bounds;
	_nameList = new ListView(0, 0, rect.width, rect.width);
	_nameList.setHorizResizeInstruction(2);
	_nameList.setPrototypeItem(new FontItem());
	_nameList.prototypeItem().setFont(Font.fontNamed("Default"));
	_loadNameList();
	scrollgroup.setContentView(_nameList);
	contentView.addSubview(scrollgroup);
	TextField textfield = new TextField(28, 1, 70, 18);
	textfield.setEditable(false);
	textfield.setTextColor(Color.black);
	textfield.setFont(Font.fontNamed("Helvetica", 1, 12));
	textfield.setBackgroundColor(Color.lightGray);
	textfield.setStringValue("Name");
	textfield.setJustification(0);
	textfield.setBorder(null);
	textfield.setHorizResizeInstruction(0);
	textfield.setVertResizeInstruction(4);
	contentView.addSubview(textfield);
	_sizePopup = new Popup(99, 19, 45, 20);
	FontItem fontitem = new FontItem();
	fontitem.setPopup(_sizePopup);
	fontitem.setFont(Font.fontNamed("Default"));
	_sizePopup.setPrototypeItem(fontitem);
	is_0_[0] = 8;
	is_0_[1] = 10;
	is_0_[2] = 12;
	is_0_[3] = 14;
	is_0_[4] = 24;
	is_0_[5] = 36;
	int i = is_0_.length;
	ListView listview = _sizePopup.popupList();
	for (int i_1_ = 0; i_1_ < i; i_1_++) {
	    FontItem fontitem_2_ = (FontItem) listview.addItem();
	    fontitem_2_.setTitle(Integer.toString(is_0_[i_1_]));
	    fontitem_2_.setTag(is_0_[i_1_]);
	}
	FontItem fontitem_3_ = (FontItem) listview.addItem();
	fontitem_3_.setTitle("Other");
	fontitem_3_.setTag(-1);
	_sizePopup.setTarget(this);
	_sizePopup.setHorizResizeInstruction(1);
	_sizePopup.setVertResizeInstruction(4);
	contentView.addSubview(_sizePopup);
	_sizeTextField = new TextField(146, 19, 25, 20);
	_sizeTextField.setEditable(true);
	_sizeTextField.setContentsChangedCommandAndTarget("", this);
	_sizeTextField.setHorizResizeInstruction(1);
	_sizeTextField.setVertResizeInstruction(4);
	contentView.addSubview(_sizeTextField);
	textfield = new TextField(100, 1, 30, 18);
	textfield.setEditable(false);
	textfield.setTextColor(Color.black);
	textfield.setBackgroundColor(Color.lightGray);
	textfield.setFont(Font.fontNamed("Helvetica", 1, 12));
	textfield.setStringValue("Size");
	textfield.setJustification(0);
	textfield.setBorder(null);
	textfield.setHorizResizeInstruction(1);
	textfield.setVertResizeInstruction(4);
	contentView.addSubview(textfield);
	_stylePopup = new Popup(99, 61, 75, 21);
	fontitem = new FontItem();
	fontitem.setPopup(_stylePopup);
	fontitem.setFont(Font.fontNamed("Default"));
	_stylePopup.setPrototypeItem(fontitem);
	strings[0] = "Plain";
	strings[1] = "Bold";
	strings[2] = "Italic";
	strings[3] = "Bold Italic";
	is[0] = 0;
	is[1] = 1;
	is[2] = 2;
	is[3] = 3;
	i = strings.length;
	listview = _stylePopup.popupList();
	for (int i_4_ = 0; i_4_ < i; i_4_++) {
	    fontitem_3_ = (FontItem) listview.addItem();
	    fontitem_3_.setTitle(strings[i_4_]);
	    fontitem_3_.setTag(is[i_4_]);
	}
	_stylePopup.setHorizResizeInstruction(1);
	_stylePopup.setVertResizeInstruction(4);
	contentView.addSubview(_stylePopup);
	textfield = new TextField(100, 43, 30, 18);
	textfield.setEditable(false);
	textfield.setTextColor(Color.black);
	textfield.setBackgroundColor(Color.lightGray);
	textfield.setFont(Font.fontNamed("Helvetica", 1, 12));
	textfield.setStringValue("Style");
	textfield.setJustification(0);
	textfield.setBorder(null);
	textfield.setHorizResizeInstruction(1);
	textfield.setVertResizeInstruction(4);
	contentView.addSubview(textfield);
	ContainerView containerview = new ContainerView(-2, 87, 184, 2);
	containerview.setHorizResizeInstruction(2);
	containerview.setVertResizeInstruction(8);
	contentView.addSubview(containerview);
	_setButton = new Button(124, 95, 50, 21);
	_setButton.setTitle("Set");
	_setButton.setHorizResizeInstruction(1);
	_setButton.setVertResizeInstruction(8);
	_setButton.setCommand("setFont");
	_setButton.setTarget(this);
	contentView.addSubview(_setButton);
	_messageTextField = new TextField(4, 95, 100, 21);
	_messageTextField.setEditable(false);
	_messageTextField.setBorder(null);
	_messageTextField.setTextColor(Color.gray);
	_messageTextField.setBackgroundColor(Color.lightGray);
	_messageTextField.setFont(Font.fontNamed("Helvetica", 0, 10));
	_messageTextField.setHorizResizeInstruction(0);
	_messageTextField.setVertResizeInstruction(8);
	contentView.addSubview(_messageTextField);
	setFont(Font.defaultFont());
    }
    
    private void _loadNameList() {
	String[] strings = new String[6];
	String[] strings_5_ = new String[6];
	strings[0] = "Courier";
	strings[1] = "Dialog";
	strings[2] = "Dialog Input";
	strings[3] = "Helvetica";
	strings[4] = "Times Roman";
	strings[5] = "Zapf Dingbats";
	strings_5_[0] = "Courier";
	strings_5_[1] = "Dialog";
	strings_5_[2] = "DialogInput";
	strings_5_[3] = "Helvetica";
	strings_5_[4] = "TimesRoman";
	strings_5_[5] = "ZapfDingbats";
	int i = strings.length;
	for (int i_6_ = 0; i_6_ < i; i_6_++) {
	    FontItem fontitem = (FontItem) _nameList.addItem();
	    fontitem.setTitle(strings[i_6_]);
	    fontitem.setFontName(strings_5_[i_6_]);
	}
	_nameList.setRowHeight(_nameList.minItemHeight());
	_nameList.sizeToMinSize();
    }
    
    public void show() {
	if (window != null)
	    window.show();
    }
    
    public void hide() {
	if (window != null)
	    window.hide();
    }
    
    private void _setSizePopupToSize(int i) {
	int i_7_ = _sizePopup.count();
	while (i_7_-- > 0) {
	    FontItem fontitem = (FontItem) _sizePopup.popupList().itemAt(i_7_);
	    if (fontitem.tag() == i) {
		_sizePopup.selectItemAt(i_7_);
		return;
	    }
	}
	if (i_7_ == -1)
	    _sizePopup.selectItemAt(_sizePopup.count() - 1);
    }
    
    public void setFont(Font font) {
	if (font != null) {
	    _currentFont = font;
	    int i = _nameList.count();
	    while (i-- > 0) {
		FontItem fontitem = (FontItem) _nameList.itemAt(i);
		if (fontitem.hasFontName(font.family())) {
		    _nameList.selectItemAt(i);
		    _nameList.scrollItemAtToVisible(i);
		    break;
		}
	    }
	    if (i == -1)
		_nameList.selectItemAt(0);
	    if (font.isBold()) {
		if (font.isItalic())
		    _stylePopup.selectItemAt(3);
		else
		    _stylePopup.selectItemAt(1);
	    } else if (font.isItalic())
		_stylePopup.selectItemAt(2);
	    else
		_stylePopup.selectItemAt(0);
	    _setSizePopupToSize(font.size());
	    _sizeTextField.setIntValue(font.size());
	}
    }
    
    public Font font() {
	String string;
	if (_nameList.selectedItem() == null)
	    string = "";
	else {
	    FontItem fontitem = (FontItem) _nameList.selectedItem();
	    string = fontitem.fontName();
	}
	Font font
	    = Font.fontNamed(string,
			     ((FontItem) _stylePopup.selectedItem()).tag(),
			     _sizeTextField.intValue());
	return font;
    }
    
    public void performCommand(String string, Object object) {
	if (object == _sizeTextField) {
	    int i = _sizeTextField.intValue();
	    if (i > 0)
		_setSizePopupToSize(i);
	    else {
		_setSizePopupToSize(8);
		_sizeTextField.setIntValue(8);
	    }
	} else if (object == _sizePopup) {
	    int i = ((FontItem) _sizePopup.selectedItem()).tag();
	    if (i > 0)
		_sizeTextField.setIntValue(i);
	} else if (object == _setButton) {
	    TargetChain targetchain = TargetChain.applicationChain();
	    targetchain.performCommand(string, font());
	}
    }
    
    public void setWindow(Window window) {
	this.window = window;
	Size size = this.window.windowSizeForContentSize(contentView.width(),
							 contentView.height());
	this.window.sizeTo(size.width, size.height);
	this.window.addSubview(contentView);
	this.window.setTitle("Font Chooser");
	Rect rect = this.window.bounds();
	this.window.setMinSize(rect.width, rect.width);
	if (this.window instanceof InternalWindow) {
	    InternalWindow internalwindow = (InternalWindow) this.window;
	    internalwindow.setCloseable(true);
	    internalwindow.setBuffered(true);
	}
	this.window.setContainsDocument(false);
    }
    
    public Window window() {
	return window;
    }
    
    public View contentView() {
	return contentView;
    }
}
