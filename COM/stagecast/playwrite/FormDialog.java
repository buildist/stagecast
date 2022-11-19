/* FormDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.ContainerView;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFilter;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class FormDialog extends InternalWindow
    implements ResourceIDs.CommandIDs, Target
{
    private static final int LABEL_WIDTH = 50;
    private static final int EDGE_SPACE = 4;
    private static final int ROW_SPACE = 4;
    private int _width;
    private int _labelWidth = 0;
    private int _rowHeight;
    private Hashtable _results;
    private boolean _accepted;
    private Font _labelFont;
    private Font _textFont;
    private TextField _firstText;
    private TextField _lastText;
    private TextFilter _filter;
    private int _nextRow;
    
    class ReturnEnterFilter implements TextFilter
    {
	public boolean acceptsEvent(Object textObject, KeyEvent event,
				    Vector events) {
	    if (event.key == 10
		|| event.isExtendedKeyEvent() && event.keyCode() == 10)
		PlaywriteRoot.app().performCommandLater(FormDialog.this,
							"command ok", null);
	    return true;
	}
    }
    
    FormDialog(String title, Font labelFont, Font textFont, int width) {
	super(1, 100, 100, width, 20);
	this.setTitle(Resource.getText(title));
	this.setCloseable(false);
	this.setResizable(false);
	this.setLayer(100);
	_labelFont = labelFont;
	_textFont = textFont;
	_filter = new ReturnEnterFilter();
	_width = width;
	_rowHeight = Math.max(_labelFont.size(), _textFont.size());
	_nextRow = 4;
	_results = new Hashtable();
    }
    
    void addField(String label) {
	ContainerView rowView
	    = new ContainerView(0, _nextRow, _width, _rowHeight);
	_nextRow += _rowHeight + 4;
	rowView.setBorder(null);
	Label rowLabel = new Label(" " + Resource.getText(label), _labelFont);
	rowLabel.sizeToMinSize();
	if (rowLabel.width() > _labelWidth)
	    _labelWidth = rowLabel.width();
	rowLabel.moveTo(4, 0);
	rowLabel.setVertResizeInstruction(64);
	rowView.addSubview(rowLabel);
	TextField rowText = new TextField(50, 0, _width - 50, _rowHeight);
	rowText.setHorizResizeInstruction(2);
	rowText.setVertResizeInstruction(64);
	rowText.setEditable(true);
	rowText.setBackgroundColor(Color.white);
	rowText.setBorder(null);
	rowText.setFilter(_filter);
	rowView.addSubview(rowText);
	if (_firstText == null)
	    _firstText = rowText;
	if (_lastText != null)
	    _lastText.setTabField(rowText);
	_lastText = rowText;
	rowText.setTabField(_firstText);
	rowView.layoutView(0, 0);
	this.addSubview(rowView);
	_results.put(label, rowText);
    }
    
    void addPasswordField(String label) {
	addField(label);
	_lastText.setDrawableCharacter('*');
    }
    
    public boolean display() {
	Button ok = Button.createPushButton(_width - 50, 0, 50, _rowHeight);
	ok.setTitle(Resource.getText("command ok"));
	ok.setCommand("command ok");
	ok.setTarget(this);
	ok.sizeToMinSize();
	ok = new PlaywriteDialog.DefaultButton(ok);
	ok.setVertResizeInstruction(64);
	ok.setHorizResizeInstruction(0);
	Button cancel = Button.createPushButton(25, 0, 50, _rowHeight);
	cancel.setTitle(Resource.getText("command c"));
	cancel.setCommand("command c");
	cancel.setTarget(this);
	cancel.sizeToMinSize();
	cancel.setVertResizeInstruction(64);
	cancel.setHorizResizeInstruction(1);
	Enumeration e = _results.elements();
	while (e.hasMoreElements()) {
	    TextField tf = (TextField) e.nextElement();
	    tf.setBounds(_labelWidth + 8, 0, _width - _labelWidth - 12,
			 _rowHeight);
	}
	ContainerView rowView
	    = new ContainerView(0, _nextRow, _width, ok.height());
	_nextRow += ok.height() + 4;
	rowView.setBorder(null);
	rowView.addSubview(ok);
	rowView.addSubview(cancel);
	rowView.layoutView(0, 0);
	this.addSubview(rowView);
	this.contentView().sizeTo(_width, _nextRow);
	Size size = this.windowSizeForContentSize(this.contentView().width(),
						  this.contentView().height());
	this.sizeTo(size.width, size.height);
	this.layoutParts();
	this.center();
	this.setFocusedView(_firstText);
	this.showModally();
	return _accepted;
    }
    
    String getResult(String label) {
	String result = ((TextField) _results.get(label)).stringValue();
	return result == null ? "" : result;
    }
    
    public void performCommand(String command, Object data) {
	if ("command ok".equals(command))
	    _accepted = true;
	else if ("command c".equals(command))
	    _accepted = false;
	this.hide();
    }
}
