/* PaintFieldScrollPane - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PaintFieldScrollPane extends ScrollableArea
    implements AppearanceEventListener, ResourceIDs.ToolIDs
{
    private static final int SCROLL_AMOUNT = 10;
    private PaintField _paintField;
    private ScaleBar _scaleBar;
    private PlaywriteButton _homeSquareToolButton;
    
    public PaintFieldScrollPane(AppearanceEditor editor,
				PaintField paintField) {
	super(0, 0, paintField, true, true);
	_paintField = paintField;
	_scaleBar = new ScaleBar(editor);
	this.addSubview(_scaleBar);
	_homeSquareToolButton = PaintField.HOME_SQUARE_TOOL.makeButton();
	_homeSquareToolButton
	    .setToolTipText(Resource.getToolTip("home square tool button"));
	this.addSubview(_homeSquareToolButton);
	int maxWidgetHeight
	    = Math.max(bottomArrow.height(), _scaleBar.height());
	maxWidgetHeight
	    = Math.max(maxWidgetHeight, _homeSquareToolButton.height() + 2);
	this.sizeTo((_paintField.width() + leftArrow.width()
		     + rightArrow.width() + 2),
		    (_paintField.height() + topArrow.height() + maxWidgetHeight
		     + 2));
	this.setInset(true);
	editor.addAppearanceEventListener(this);
    }
    
    public PlaywriteButton getHomeSquareToolButton() {
	return _homeSquareToolButton;
    }
    
    public void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	layoutComponents();
    }
    
    private void layoutComponents() {
	int maxWidgetHeight
	    = Math.max(bottomArrow.height(), _scaleBar.height());
	maxWidgetHeight
	    = Math.max(maxWidgetHeight, _homeSquareToolButton.height() + 2);
	this.getScrollView().sizeTo(_paintField.width(), _paintField.height());
	_scaleBar.moveTo(leftArrow.width(), this.height() - maxWidgetHeight);
	_homeSquareToolButton.moveTo((this.width() - rightArrow.width()
				      - _homeSquareToolButton.width()),
				     this.height() - maxWidgetHeight + 2);
	bottomArrow.moveTo(bottomArrow.x(), this.height() - maxWidgetHeight);
	int scrollViewCenter
	    = this.getScrollView().x() + this.getScrollView().height() / 2;
	leftArrow.moveTo(leftArrow.x(),
			 scrollViewCenter - leftArrow.height() / 2);
	rightArrow.moveTo(rightArrow.x(),
			  scrollViewCenter - rightArrow.height() / 2);
    }
    
    int getContentViewHeight() {
	return _paintField.getLogicalSize().height * _paintField.getScale();
    }
    
    protected boolean checkArrows() {
	this.setEnabled(leftArrow, _paintField.isLeftEdgeVisible() == false);
	this.setEnabled(topArrow, _paintField.isTopEdgeVisible() == false);
	this.setEnabled(rightArrow, _paintField.isRightEdgeVisible() == false);
	this.setEnabled(bottomArrow,
			_paintField.isBottomEdgeVisible() == false);
	return true;
    }
    
    public void scrollBy(int deltaX, int deltaY) {
	deltaX /= _paintField.getScale();
	deltaY /= _paintField.getScale();
	Point p = _paintField.getOrigin();
	p.x = p.x - deltaX;
	p.y = p.y - deltaY;
	_paintField.setOrigin(p);
	checkArrows();
    }
    
    public void discard() {
	super.discard();
	_paintField = null;
	_scaleBar = null;
	_homeSquareToolButton = null;
    }
    
    public void setTool(AppearanceEditorTool tool) {
	/* empty */
    }
    
    public void setBrushWidth(int width) {
	/* empty */
    }
    
    public void setColor(Color color, boolean completed) {
	/* empty */
    }
    
    public void setScale(int scale) {
	this.setHorizontalScrollAmount(10 * _paintField.getScale());
	this.setVerticalScrollAmount(10 * _paintField.getScale());
    }
    
    public void setFont(Font font) {
	/* empty */
    }
    
    public void setJustification(int justification) {
	/* empty */
    }
}
