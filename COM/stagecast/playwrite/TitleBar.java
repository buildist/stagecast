/* TitleBar - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class TitleBar extends PlaywriteView
    implements Debug.Constants, ResourceIDs.TitleBarIDs
{
    static final int TITLE_GRAY = 0;
    static final int TITLE_BROWN = 1;
    static final int TITLE_RUST = 2;
    static final int TITLE_PURPLE = 3;
    static final int TITLE_GREEN = 4;
    static final int TITLE_BLUE = 5;
    static final int TITLE_PERIWINKLE = 6;
    private static final ColorInfo[] COLOR_INFO_TABLE
	= { new ColorInfo(null, null, null),
	    new ColorInfo(null, null, new Color(102, 51, 0)),
	    new ColorInfo(null, null, new Color(170, 0, 0)),
	    new ColorInfo(null, null, new Color(102, 0, 153)),
	    new ColorInfo(null, null, new Color(0, 85, 0)),
	    new ColorInfo(null, null, new Color(0, 51, 161)),
	    new ColorInfo(null, null, new Color(51, 51, 102)) };
    static final PackConstraints LEFT_CONSTRAINTS
	= new PackConstraints(8, false, false, false, 0, 0, 1, 1, 2);
    static final PackConstraints RIGHT_CONSTRAINTS
	= new PackConstraints(8, false, false, false, 0, 0, 1, 1, 3);
    static final PackConstraints CENTER_CONSTRAINTS
	= new PackConstraints(8, true, false, false, 0, 0, 1, 1, 2);
    private PlaywriteWindow _owner;
    private PlaywriteLabel _titleLabel;
    private TitleBarRow _currentRow;
    private ColorInfo _currentColorInfo = COLOR_INFO_TABLE[0];
    private boolean _active = true;
    private Color _activeColor = Util.defaultDarkColor;
    private Color _inactiveColor = Util.defaultColor;
    
    static class ColorInfo
    {
	Bitmap _topBorder;
	Bitmap _bottomBorder;
	Color _color;
	
	ColorInfo(Bitmap topBorder, Bitmap bottomBorder, Color color) {
	    _topBorder = topBorder;
	    _bottomBorder = bottomBorder;
	    _color = color;
	}
    }
    
    private class TitleBarRow extends PlaywriteView
    {
	private PackLayout _packLayout;
	private Vector _itemsRight;
	private Hashtable _subviewSizeTable = new Hashtable();
	
	TitleBarRow() {
	    this(null);
	}
	
	TitleBarRow(String title) {
	    this.setHorizResizeInstruction(2);
	    this.setVertResizeInstruction(4);
	    _packLayout = new PackLayout();
	    _packLayout.setDefaultConstraints(TitleBar.RIGHT_CONSTRAINTS);
	    this.setLayoutManager(_packLayout);
	    if (title != null) {
		_titleLabel = new PlaywriteLabel(title, Util.titleFont,
						 Util.titleColor, true);
		_packLayout.setConstraints(_titleLabel,
					   TitleBar.CENTER_CONSTRAINTS);
		addSubview(_titleLabel);
	    }
	    this.setTransparent(true);
	    this.setMouseTransparency(true);
	    this.layoutView(0, 0);
	    this.setDirty(true);
	}
	
	public void addSubview(View subview) {
	    super.addSubview(subview);
	    _subviewSizeTable.put(subview,
				  new Size(subview.width(), subview.height()));
	    computeMinSize();
	    Size min = this.minSize();
	    if (min.height != this.height())
		this.sizeTo(this.width(), min.height);
	    this.layoutView(0, 0);
	    this.setDirty(true);
	}
	
	protected void removeSubview(View subview) {
	    if (_itemsRight != null)
		_itemsRight.removeElementIdentical(subview);
	    super.removeSubview(subview);
	    _subviewSizeTable.remove(subview);
	    computeMinSize();
	    Size min = this.minSize();
	    if (min.height != this.height())
		this.sizeTo(this.width(), min.height);
	    this.layoutView(0, 0);
	    this.setDirty(true);
	}
	
	public void subviewDidResize(View subview) {
	    super.subviewDidResize(subview);
	    computeMinSize();
	    Size min = this.minSize();
	    if (min.height != this.height())
		this.sizeTo(this.width(), min.height);
	    Size oldSize = (Size) _subviewSizeTable.get(subview);
	    Size newSize = new Size(subview.width(), subview.height());
	    if (oldSize != null && oldSize.equals(newSize) == false) {
		_subviewSizeTable.put(subview, newSize);
		this.layoutView(0, 0);
		this.setDirty(true);
	    }
	}
	
	void addSubviewLeft(View subview) {
	    Vector saveRight = _itemsRight;
	    _itemsRight = null;
	    boolean containsTitleLabel = _titleLabel.superview() == this;
	    if (saveRight != null) {
		for (int i = 0; i < saveRight.size(); i++)
		    ((View) saveRight.elementAt(i)).removeFromSuperview();
	    }
	    if (containsTitleLabel)
		_titleLabel.removeFromSuperview();
	    _packLayout.setConstraints(subview, TitleBar.LEFT_CONSTRAINTS);
	    addSubview(subview);
	    if (containsTitleLabel) {
		_packLayout.setConstraints(_titleLabel,
					   TitleBar.CENTER_CONSTRAINTS);
		addSubview(_titleLabel);
	    }
	    if (saveRight != null) {
		for (int i = 0; i < saveRight.size(); i++)
		    addSubview((View) saveRight.elementAt(i));
	    }
	    _itemsRight = saveRight;
	}
	
	void addSubviewRight(View subview) {
	    if (_itemsRight == null)
		_itemsRight = new Vector(2);
	    _itemsRight.addElement(subview);
	    addSubview(subview);
	}
	
	private void computeMinSize() {
	    Size result = new Size(this.border().widthMargin(),
				   this.border().heightMargin());
	    Enumeration subviews = this.subviews().elements();
	    while (subviews.hasMoreElements()) {
		View view = (View) subviews.nextElement();
		Size size = view.minSize();
		PackConstraints constraints = _packLayout.constraintsFor(view);
		result.width
		    = Math.max(result.width,
			       (this.border().widthMargin() + size.width
				+ constraints.padX() * 2));
		result.height = Math.max(result.height,
					 size.height + constraints.padY() * 2);
	    }
	    this.setMinSize(result.width, result.height);
	}
    }
    
    TitleBar(PlaywriteWindow window) {
	_owner = window;
	this.setTransparent(false);
	changeWindowColor(window.getWorld().getColor());
	setBorder(null);
	PlaywriteView ind = new PlaywriteView(0, 0, this.width(),
					      PlaywriteBorder.TOP_BORDER
						  .height() * 2) {
	    public void drawView(Graphics g) {
		if (_active) {
		    int currentX = 0;
		    int w = PlaywriteBorder.TOP_BORDER.width();
		    int h = PlaywriteBorder.TOP_BORDER.height();
		    for (currentX = 0; currentX < bounds.width;
			 currentX += w) {
			PlaywriteBorder.TOP_BORDER.drawAt(g, currentX, 0);
			PlaywriteBorder.TOP_BORDER.drawAt(g, currentX, h);
		    }
		}
	    }
	    
	    public View viewForMouse(int x, int y) {
		return null;
	    }
	};
	ind.setMinSize(0, ind.height());
	ind.setHorizResizeInstruction(2);
	ind.setVertResizeInstruction(4);
	addSubview(ind);
	_currentRow = new TitleBarRow("Playwrite");
	_currentRow.moveTo(0, this.border().topMargin() + ind.height());
	addSubview(_currentRow);
	this.sizeToMinSize();
	this.setHorizResizeInstruction(2);
	this.setVertResizeInstruction(4);
	this.setMouseTransparency(true);
	this.setBackgroundColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	this.setDirty(true);
    }
    
    World getWorld() {
	return _owner.getWorld();
    }
    
    public void setTitle(String title) {
	_titleLabel.setTitle(title);
    }
    
    public View getTitleLabel() {
	return _titleLabel;
    }
    
    void activate() {
	_active = true;
	this.setBackgroundColor(_activeColor);
	this.setDirty(true);
    }
    
    void deactivate() {
	_active = false;
	this.setBackgroundColor(_inactiveColor);
	this.setDirty(true);
    }
    
    void setColor(int colorCode) {
	/* empty */
    }
    
    void changeWindowColor(Color color) {
	_activeColor = color;
	_inactiveColor = color;
	if (_active)
	    activate();
	else
	    deactivate();
    }
    
    public void addSubview(View subview) {
	super.addSubview(subview);
	computeMinSize();
	Size min = this.minSize();
	if (min.height != this.height())
	    this.sizeTo(this.width(), min.height);
	this.layoutView(0, 0);
	this.setDirty(true);
    }
    
    protected void removeSubview(View subview) {
	super.removeSubview(subview);
	computeMinSize();
	Size min = this.minSize();
	if (min.height != this.height())
	    this.sizeTo(this.width(), min.height);
	this.layoutView(0, 0);
	this.setDirty(true);
    }
    
    public void subviewDidResize(View subview) {
	super.subviewDidResize(subview);
	computeMinSize();
	Size min = this.minSize();
	if (min.height != this.height())
	    this.sizeTo(this.width(), min.height);
    }
    
    public void addRow() {
	TitleBarRow oldRow = _currentRow;
	_currentRow = new TitleBarRow();
	addSubview(_currentRow);
	_currentRow.setBounds(0, oldRow.y() + oldRow.height(), oldRow.width(),
			      _currentRow.height());
    }
    
    public void addSubviewLeft(View subview) {
	_currentRow.addSubviewLeft(subview);
    }
    
    public void addSubviewRight(View subview) {
	_currentRow.addSubviewRight(subview);
    }
    
    private void computeMinSize() {
	Size result = new Size(this.border().widthMargin(),
			       this.border().heightMargin());
	Enumeration subviews = this.subviews().elements();
	while (subviews.hasMoreElements()) {
	    Size size = ((View) subviews.nextElement()).minSize();
	    result.width = Math.max(result.width,
				    this.border().widthMargin() + size.width);
	    result.height += size.height;
	}
	this.setMinSize(result.width, result.height);
    }
    
    void disable() {
	_currentRow.disable();
    }
    
    void enable() {
	_currentRow.enable();
    }
    
    public void setBorder(Border border) {
	super.setBorder(border);
    }
}
