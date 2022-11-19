/* Icon - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFieldOwner;
import COM.stagecast.ifc.netscape.application.View;

class Icon extends PlaywriteView
    implements TextFieldOwner, Iconish, Debug.Constants
{
    private static final ViewManager.ViewUpdater updateIconNames = new ViewManager.ViewUpdater() {
	public void updateView(Object view, Object value) {
	    Iconish iconView = (Iconish) view;
	    iconView.setIconName((String) value);
	}
    };
    private PlaywriteView _imageView;
    private Rect _imageViewRect;
    private PlaywriteTextField _nameField;
    private boolean _showName = true;
    private boolean _computeMinSize = true;
    private boolean _selectsModel = false;
    
    private class IconBorder extends Border
    {
	public IconBorder() {
	    /* empty */
	}
	
	public int topMargin() {
	    return 2;
	}
	
	public int bottomMargin() {
	    return 2;
	}
	
	public int leftMargin() {
	    return 2;
	}
	
	public int rightMargin() {
	    return 2;
	}
	
	public void drawInRect(Graphics graphics, int x, int y, int width,
			       int height) {
	    /* empty */
	}
    }
    
    Icon(IconModel model) {
	this(model.getIconImage(), model.getIconImageRect(),
	     model.getIconName());
	_nameField.setUserEditable(false);
	this.setModelObject(model);
	model.getIconViewManager().addView(this);
    }
    
    Icon(Image image, Rect imageViewRect, String title) {
	this.setBorder(new IconBorder());
	this.setTransparent(true);
	_imageView = new PlaywriteView(image) {
	    public void drawViewBackground(Graphics graphics) {
		Bitmap bitmap = (Bitmap) this.image();
		if (bitmap.width() == this.width()
		    && bitmap.height() == this.height())
		    bitmap.drawAt(graphics, 0, 0);
		else {
		    Rect scaledRect
			= new Rect(0, 0, this.width(), this.height());
		    Util.scaleRectToImageProportion(scaledRect, this.image());
		    if (bitmap.width() == scaledRect.width
			&& bitmap.height() == scaledRect.height)
			bitmap.drawAt(graphics, 0, 0);
		    else
			this.image().drawScaled(graphics, scaledRect);
		}
	    }
	};
	_imageView.setMouseTransparency(true);
	if (imageViewRect == null) {
	    _imageViewRect = new Rect(0, this.border().topMargin(),
				      _imageView.width(), _imageView.height());
	    _imageView.setBounds(_imageViewRect);
	} else {
	    _imageViewRect
		= new Rect(0, this.border().topMargin(), imageViewRect.width,
			   imageViewRect.height);
	    _imageView.setBounds(_imageViewRect);
	}
	_imageView.setHorizResizeInstruction(32);
	_imageView.setVertResizeInstruction(4);
	this.addSubview(_imageView);
	_nameField = createNameTextField(0, _imageView.bounds.maxY(), 10,
					 Util.valueFontHeight);
	_nameField.setOwner(this);
	if (title != null)
	    _nameField.setStringValue(title);
	_nameField.sizeToMinSize();
	_nameField.setHorizResizeInstruction(32);
	_nameField.setVertResizeInstruction(8);
	_nameField.setUserEditable(false);
	this.addSubview(_nameField);
	sizeToMinSize();
	layoutView(0, 0);
    }
    
    final IconModel getIconModel() {
	return (IconModel) this.getModelObject();
    }
    
    protected PlaywriteTextField getNameField() {
	return _nameField;
    }
    
    protected PlaywriteTextField createNameTextField(int x, int y, int w,
						     int h) {
	PlaywriteTextField result = new PlaywriteTextField(x, y, w, h);
	result.setEditIndication(true);
	result.setTransparent(true);
	result.setFont(Util.valueFont);
	result.setJustification(1);
	result.setUserEditable(false);
	result.setDraggable(false);
	return result;
    }
    
    final void setEditable(boolean editable) {
	_nameField.setUserEditable(PlaywriteRoot.isAuthoring() && editable);
    }
    
    public void setSelectsModel(boolean selectsModel) {
	if (PlaywriteRoot.isAuthoring())
	    _selectsModel = selectsModel;
    }
    
    void setShowName(boolean showName) {
	_showName = showName;
	if (_showName) {
	    if (_nameField.superview() == null)
		this.addSubview(_nameField);
	} else if (_nameField.superview() != null)
	    _nameField.removeFromSuperview();
	layoutView(0, 0);
	resetMinSize();
	sizeToMinSize();
    }
    
    protected boolean isLegalIconName(String name) {
	return true;
    }
    
    public Rect getImageRect() {
	return _imageView.bounds();
    }
    
    public void setIconImage(Image image) {
	_imageView.setImage(image);
	_imageView.setImageDisplayStyle(0);
	if (this.getModelObject() instanceof IconModel) {
	    _imageViewRect = getIconModel().getIconImageRect();
	    if (_imageViewRect == null)
		_imageViewRect
		    = new Rect(0, this.border().topMargin(),
			       _imageView.width(), _imageView.height());
	}
	_imageView.sizeTo(_imageViewRect.width, _imageViewRect.height);
	sizeToMinSize();
	this.setDirty();
	this.setDragImageDirty();
    }
    
    public void setIconName(String newName) {
	_nameField.setStringValue(newName);
	_nameField.sizeToMinSize();
	sizeToMinSize();
	this.setDragImageDirty();
	layoutView(0, 0);
    }
    
    public void discardIcon() {
	if (this.getModelObject() != null) {
	    if (this.getModelObject() instanceof IconModel) {
		IconModel iconModel = getIconModel();
		if (iconModel != null && iconModel.hasIconViews())
		    getIconModel().getIconViewManager().removeView(this);
	    }
	    this.setModelObject(null);
	    if (_nameField != null)
		_nameField.setOwner(null);
	    if (_imageView != null)
		_imageView.discard();
	    _imageView = null;
	    _imageViewRect = null;
	    _nameField = null;
	}
    }
    
    public void discard() {
	discardIcon();
    }
    
    public void setCursor(int cursor) {
	super.setCursor(cursor);
	_imageView.setCursor(cursor);
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (super.mouseDown(event)) {
	    if (_selectsModel && this.getModelObject() instanceof Selectable)
		this.selectModel(event);
	    return true;
	}
	return false;
    }
    
    public void keyDown(KeyEvent keyEvent) {
	if (!_selectsModel && (keyEvent.key == 127 || keyEvent.key == 8)) {
	    View superview = this.superview();
	    if (superview instanceof ValueView) {
		ValueView vv = (ValueView) superview;
		vv.deleteTool();
	    }
	} else
	    super.keyDown(keyEvent);
    }
    
    public void startFocus() {
	super.startFocus();
	if (!_selectsModel) {
	    this.hilite();
	    this.draw();
	}
    }
    
    public void resumeFocus() {
	super.resumeFocus();
	if (!_selectsModel) {
	    this.hilite();
	    this.draw();
	}
    }
    
    public void pauseFocus() {
	super.pauseFocus();
	if (!_selectsModel) {
	    this.unhilite();
	    this.draw();
	}
    }
    
    public void stopFocus() {
	super.stopFocus();
	if (!_selectsModel) {
	    this.unhilite();
	    this.draw();
	}
    }
    
    public View viewForMouse(int x, int y) {
	View view = super.viewForMouse(x, y);
	if (view == this)
	    return null;
	return view;
    }
    
    static void updateIcons(IconModel iconModel) {
	if (iconModel.hasIconViews()) {
	    ViewManager viewManager = iconModel.getIconViewManager();
	    if (viewManager.hasViews()) {
		final Image newImage = iconModel.getIconImage();
		final String newName = iconModel.getIconName();
		viewManager.updateViews(new ViewManager.ViewUpdater() {
		    public void updateView(Object view, Object nullParameter) {
			((Iconish) view).setIconImage(newImage);
			((Iconish) view).setIconName(newName);
		    }
		}, null);
	    }
	}
    }
    
    static void updateIconNames(IconModel iconModel) {
	if (iconModel.hasIconViews()) {
	    ViewManager viewManager = iconModel.getIconViewManager();
	    if (viewManager.hasViews()) {
		final String newName = iconModel.getIconName();
		viewManager.updateViews(new ViewManager.ViewUpdater() {
		    public void updateView(Object view, Object nullParameter) {
			((Iconish) view).setIconName(newName);
		    }
		}, null);
	    }
	}
    }
    
    static void updateIconImages(IconModel iconModel) {
	if (iconModel.hasIconViews()) {
	    ViewManager viewManager = iconModel.getIconViewManager();
	    if (viewManager.hasViews()) {
		final Image newImage = iconModel.getIconImage();
		viewManager.updateViews(new ViewManager.ViewUpdater() {
		    public void updateView(Object view, Object nullParameter) {
			((Iconish) view).setIconImage(newImage);
		    }
		}, null);
	    }
	}
    }
    
    public void layoutView(int dx, int dy) {
	_nameField.moveTo(0, _imageView.bounds.maxY());
	super.layoutView(dx, dy);
	this.setDirty();
    }
    
    void resetMinSize() {
	if (_showName)
	    this.setMinSize((Math.max(_imageView.width(), _nameField.width())
			     + this.border().widthMargin()),
			    (_imageView.height() + _nameField.height()
			     + this.border().heightMargin()));
	else
	    this.setMinSize(_imageView.width() + this.border().widthMargin(),
			    (_imageView.height()
			     + this.border().heightMargin()));
    }
    
    public void setComputeMinSize(boolean compute) {
	_computeMinSize = compute;
    }
    
    public void sizeToMinSize() {
	if (_computeMinSize)
	    resetMinSize();
	super.sizeToMinSize();
    }
    
    public void subviewDidResize(View subview) {
	sizeToMinSize();
	layoutView(0, 0);
    }
    
    public void setBounds(int x, int y, int w, int h) {
	if (this.superview() != null)
	    this.superview().addDirtyRect(bounds);
	super.setBounds(x, y, w, h);
	if (this.superview() != null)
	    this.superview().addDirtyRect(bounds);
    }
    
    public void textEditingDidBegin(TextField tf) {
	/* empty */
    }
    
    public void textWasModified(TextField tf) {
	tf.sizeToMinSize();
    }
    
    public boolean textEditingWillEnd(TextField tf, int endCondition,
				      boolean changed) {
	return true;
    }
    
    public void textEditingDidEnd(TextField tf, int endCondition,
				  boolean changed) {
	if (tf == _nameField) {
	    String newName = tf.stringValue();
	    IconModel model = getIconModel();
	    if (isLegalIconName(newName)
		&& model != PlaywriteSound.nullSound) {
		model.setIconName(newName);
		model.getIconViewManager().updateViews(updateIconNames,
						       newName);
		if (model instanceof Worldly) {
		    World world = ((Worldly) model).getWorld();
		    if (world != null)
			world.setModified(true);
		}
	    } else
		setIconName(model.getIconName());
	}
    }
}
