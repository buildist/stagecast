/* RuleScrap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Range;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.TextFieldOwner;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.TextViewOwner;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class RuleScrap extends RuleListItemView
    implements Debug.Constants, ResourceIDs.RuleScrapIDs, TextFieldOwner,
	       TextViewOwner, ResourceIDs.CharacterWindowIDs
{
    static final String DISPLAY_COMMENT = "Display Comment";
    static final int MINIMUM_WIDTH = 160;
    protected boolean layoutEnabled = true;
    protected PlaywriteView visualRule = null;
    private PlaywriteTextField nameField = null;
    private PlaywriteTextView commentField = null;
    private Vector viewList = null;
    private Button displayCommentButton = null;
    private boolean displayComment = false;
    protected Point outerBound;
    
    public static class MiniRuleView extends PlaywriteView
    {
	public MiniRuleView(Image image) {
	    super(image);
	}
	
	public void discard() {
	    this.disableDrawing();
	    _setBitmap(null);
	    super.discard();
	}
	
	private final void _setBitmap(Bitmap bitmap) {
	    Bitmap oldBitmap = (Bitmap) this.image();
	    BitmapManager.checkInBitmap(oldBitmap);
	    BitmapManager.checkOutBitmap(bitmap);
	}
	
	public void setImage(Image image) {
	    _setBitmap((Bitmap) image);
	    super.setImage(image);
	}
	
	protected void finalize() throws Throwable {
	    _setBitmap(null);
	    super.finalize();
	}
    }
    
    RuleScrap(RuleListItem item) {
	super(item);
	outerBound = new Point(0, 0);
	ScrapBorder border = ScrapBorder.getRuleBorder();
	this.setBorder(border);
	this.setAutoResizeSubviews(false);
	this.setBackgroundColor(border.getBackgroundColor());
	this.setTransparent(false);
	visualRule = createVisualRule();
	visualRule.setCursor(12);
	nameField = new PlaywriteTextField(0, 0, 160, Util.valueFontHeight);
	nameField.setBackgroundColor(Util.valueBGColor);
	nameField.setTextColor(Util.valueColor);
	nameField.setFont(Util.valueFont);
	nameField.setBorder(null);
	nameField.setStringValue(getRuleName());
	nameField.setOurMinSize(25, nameField.height());
	nameField.sizeToMinSize();
	nameField.setOwner(this);
	Debug.print("debug.scrap", "NAME: ", getRuleName());
	this.addSubview(visualRule);
	this.addSubview(nameField);
	if (getComment() != null) {
	    addCommentButton();
	    addComment();
	}
	this.allowTool(CharacterWindow.commentTool, this);
	this.setCursor(12);
	layoutView(0, 0);
    }
    
    RuleScrap(RuleListItem item, boolean bogus) {
	super(item);
	outerBound = new Point(0, 0);
    }
    
    public void disableDrawing() {
	layoutEnabled = false;
	super.disableDrawing();
    }
    
    public void reenableDrawing() {
	layoutEnabled = true;
	super.reenableDrawing();
    }
    
    public void layoutView(int deltaWidth, int deltaHeight) {
	int borderWidth = this.border().leftMargin();
	int borderHeight = this.border().topMargin();
	outerBound.x = 160;
	outerBound.y = 0;
	visualRule.moveTo(borderWidth, borderHeight);
	maximize(outerBound, visualRule);
	nameField.moveTo(visualRule.x(), visualRule.bounds.maxY());
	maximize(outerBound, nameField);
	if (displayCommentButton != null) {
	    int xpos = outerBound.x - displayCommentButton.width();
	    int ypos = (nameField.y() + nameField.baseline()
			- displayCommentButton.height());
	    xpos = Math.max(xpos, nameField.bounds.maxX());
	    displayCommentButton.moveTo(xpos, ypos);
	    maximize(outerBound, displayCommentButton);
	    outerBound.y++;
	    if (displayComment == true) {
		commentField.moveTo(nameField.x(),
				    nameField.bounds.maxY() + 3);
		commentField.sizeTo((outerBound.x - commentField.x()
				     - this.border().rightMargin()),
				    commentField.height());
		maximize(outerBound, commentField);
	    }
	}
	if (this.superview() != null)
	    this.superview().addDirtyRect(this.bounds());
	this.sizeTo(outerBound.x, outerBound.y);
    }
    
    public Size minSize() {
	Size minSize = super.minSize();
	if (outerBound.x != 0 && outerBound.y != 0) {
	    minSize.width = outerBound.x;
	    minSize.height = outerBound.y;
	}
	minSize.width = Math.max(minSize.width, 160);
	return minSize;
    }
    
    public void sizeToMinSize() {
	layoutView(0, 0);
    }
    
    void maximize(Point point1, View view) {
	point1.x = Math.max(point1.x,
			    view.bounds.maxX() + this.border().rightMargin());
	point1.y = Math.max(point1.y,
			    view.bounds.maxY() + this.border().bottomMargin());
    }
    
    protected final int getRuleBottom() {
	if (displayComment)
	    return commentField.bounds.maxY();
	return nameField.bounds.maxY();
    }
    
    private void addCommentButton() {
	if (displayCommentButton == null) {
	    displayCommentButton
		= PlaywriteButton.createFromResource("RS comment",
						     "Display Comment", this,
						     false);
	    displayCommentButton.setRaisedColor(this.backgroundColor());
	    displayCommentButton.setLoweredColor(this.backgroundColor());
	    this.addSubview(displayCommentButton);
	    displayCommentButton.setDirty(true);
	}
    }
    
    private void addComment() {
	if (commentField == null) {
	    commentField = new PlaywriteTextView(this.border().leftMargin(),
						 nameField.bounds.maxY(),
						 nameField.width(),
						 Util.commentFontHeight * 2);
	    commentField.setBackgroundColor(Util.commentBackground);
	    commentField.setTextColor(Util.commentColor);
	    commentField.setFont(Util.commentFont);
	    commentField.setToolDelegate(this);
	    String comment = getComment();
	    if (comment == null) {
		comment = Resource.getText("character window type a comment");
		setComment(comment);
	    }
	    commentField.setString(comment);
	    commentField.setOwner(this);
	}
    }
    
    private void deleteComment() {
	setComment(null);
	RuleListItem RLI = (RuleListItem) this.getModelObject();
	RLI.getViewManager().updateViews(new ViewManager.ViewUpdater() {
	    public void updateView(Object view, Object value) {
		RuleScrap rs = (RuleScrap) view;
		rs.disableDrawing();
		rs.displayCommentButton.removeFromSuperview();
		if (rs.commentField != null
		    && rs.commentField.isInViewHierarchy())
		    rs.commentField.removeFromSuperview();
		rs.setDisplayComment(false);
		rs.setCommentField(null);
		rs.setDisplayCommentButton(null);
		rs.reenableDrawing();
		rs.setDirty(true);
		rs.layoutView(0, 0);
	    }
	}, null);
    }
    
    String getRuleName() {
	return getRule().getName();
    }
    
    void setRuleName(String name) {
	Rule rule = getRule();
	rule.setName(name);
	rule.getWorld().setModified(true);
    }
    
    void setRuleNameField(String newName) {
	nameField.setStringValue(newName);
	nameField.sizeToMinSize();
    }
    
    String getComment() {
	return getRule().getComment();
    }
    
    void setComment(String name) {
	Rule rule = getRule();
	rule.setComment(name);
	rule.getWorld().setModified(true);
    }
    
    void setDisplayComment(boolean foo) {
	displayComment = foo;
    }
    
    void setDisplayCommentButton(Button foo) {
	displayCommentButton = foo;
    }
    
    void setCommentField(PlaywriteTextView foo) {
	commentField = foo;
    }
    
    protected PlaywriteView createVisualRule() {
	PlaywriteView rulePic
	    = getRule().createMiniRuleView(this.backgroundColor());
	rulePic.setMouseTransparency(true);
	return rulePic;
    }
    
    void updateVisualRule() {
	Image bitmap = getRule().getMiniRuleImage(this.backgroundColor());
	visualRule.setImage(bitmap);
	visualRule.sizeTo(bitmap.width(), bitmap.height());
	if (PlaywriteRoot.app().inEventThread())
	    visualRule.draw();
    }
    
    protected Rule getRule() {
	return (Rule) this.getModelObject();
    }
    
    public void subviewDidResize(View subview) {
	if (subview == commentField || subview == visualRule
	    || subview == nameField)
	    layoutView(0, 0);
	else
	    super.subviewDidResize(subview);
    }
    
    void resetSubroutineLights() {
	/* empty */
    }
    
    void update() {
	/* empty */
    }
    
    public void textEditingDidBegin(TextField textField) {
	/* empty */
    }
    
    public void textWasModified(TextField textField) {
	if (textField == nameField) {
	    Rect oldBounds = this.bounds();
	    nameField.sizeToMinSize();
	    if (oldBounds.width > bounds.width) {
		oldBounds.width -= bounds.width;
		oldBounds.x = bounds.maxX();
		this.superview().addDirtyRect(oldBounds);
	    } else {
		oldBounds.width = bounds.width - oldBounds.width;
		oldBounds.x = bounds.maxX();
		this.superview().addDirtyRect(oldBounds);
	    }
	    if (RuleEditor.ruleBeingDefined() == this.getModelObject())
		RuleEditor.getRuleEditor()
		    .setRuleNameField(nameField.stringValue());
	}
    }
    
    public boolean textEditingWillEnd(TextField textField, int endCondition,
				      boolean contentsChanged) {
	return true;
    }
    
    public void textEditingDidEnd(TextField textField, int endCondition,
				  boolean contentsChanged) {
	if (textField == nameField) {
	    final String sv = textField.stringValue();
	    this.addDirtyRect(textField.bounds());
	    textField.sizeToMinSize();
	    this.getRLIViewManager()
		.updateViewsExcept(this, new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    ((RuleScrap) view).setRuleNameField(sv);
		}
	    }, null);
	    setRuleName(nameField.stringValue());
	}
    }
    
    public void attributesDidChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void attributesWillChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void linkWasSelected(TextView tv, Range r, String s) {
	/* empty */
    }
    
    public void selectionDidChange(TextView tv) {
	/* empty */
    }
    
    public void textDidChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void textEditingDidBegin(TextView tv) {
	/* empty */
    }
    
    public void textWillChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void textEditingDidEnd(TextView tv) {
	if (tv == commentField) {
	    final String comment = commentField.string();
	    setComment(comment);
	    this.getRLIViewManager()
		.updateViewsExcept(this, new ViewManager.ViewUpdater() {
		public void updateView(Object view, Object value) {
		    RuleScrap rs = (RuleScrap) view;
		    if (rs.commentField != null)
			rs.commentField.setString(comment);
		}
	    }, null);
	}
    }
    
    public boolean toolEntered(ToolSession session) {
	Tool toolType = session.toolType();
	if (toolType == CharacterWindow.commentTool && getComment() != null)
	    return false;
	return super.toolEntered(session);
    }
    
    public boolean toolClicked(ToolSession session) {
	Tool toolType = session.toolType();
	if (toolType == CharacterWindow.commentTool) {
	    if (displayCommentButton == null) {
		addCommentButton();
		addComment();
		performCommand("Display Comment", null);
		this.getRLIViewManager()
		    .updateViewsExcept(this, new ViewManager.ViewUpdater() {
		    public void updateView(Object view, Object value) {
			RuleScrap rs = (RuleScrap) view;
			if (rs.displayCommentButton == null) {
			    rs.addCommentButton();
			    rs.layoutView(0, 0);
			}
		    }
		}, null);
		this.getWorld().setModified(true);
		return true;
	    }
	} else if (toolType == Tool.deleteTool
		   && (session.destinationView() == commentField
		       || (displayCommentButton != null
			   && (displayCommentButton.bounds().contains
			       (session.destinationMousePoint()))))) {
	    this.getWorld().setModified(true);
	    deleteComment();
	    return true;
	}
	return super.toolClicked(session);
    }
    
    public void toolDragged(ToolSession session) {
	/* empty */
    }
    
    public void performCommand(String command, Object thing) {
	if (command.equals("Open Editor")) {
	    CocoaCharacter character = null;
	    if (this.window() instanceof CharacterWindow)
		character = ((CharacterWindow) this.window()).getCharacter();
	    RuleEditor.makeRuleEditor(character, getRule());
	    RuleEditor.showRuleEditor();
	} else if (command.equals("Display Comment")) {
	    displayComment = displayComment ^ true;
	    if (commentField == null)
		addComment();
	    if (displayComment)
		this.addSubview(commentField);
	    else
		commentField.removeFromSuperview();
	    layoutView(0, 0);
	} else
	    super.performCommand(command, thing);
    }
    
    public void discard() {
	Rule rule = getRule();
	if (rule != null)
	    getRule().removeView(this);
	if (viewList != null)
	    viewList.removeAllElements();
	if (commentField != null && !commentField.isInViewHierarchy())
	    commentField.discard();
	super.discard();
	visualRule = null;
	nameField = null;
	commentField = null;
	viewList = null;
	displayCommentButton = null;
    }
}
