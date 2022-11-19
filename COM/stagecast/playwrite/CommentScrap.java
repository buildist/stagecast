/* CommentScrap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Range;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.TextViewOwner;
import COM.stagecast.ifc.netscape.application.View;

class CommentScrap extends RuleListItemView implements TextViewOwner
{
    private Button opener;
    private boolean open = false;
    private PlaywriteTextView commentField = null;
    private PlaywriteView commentHolder = null;
    
    CommentScrap(Comment r) {
	super((RuleListItem) r);
	this.setBorder(null);
	this.setCursor(12);
	this.setAutoResizeSubviews(false);
	opener = Util.createHorizHandle("FOO", this);
	this.addSubview(opener);
	commentHolder = new PlaywriteView();
	commentHolder.setBorder(ScrapBorder.getCommentBorder());
	commentHolder.setBackgroundColor(Util.commentBackground);
	commentHolder.setMouseTransparency(true);
	commentHolder.setCursor(12);
	int minWidth = 200;
	commentField
	    = new PlaywriteTextView(commentHolder.border().leftMargin(),
				    commentHolder.border().topMargin(),
				    minWidth, Util.commentFontHeight);
	commentField.setBackgroundColor(Util.commentBackground);
	commentField.setTextColor(Util.commentColor);
	commentField.setFont(Util.commentFont);
	commentField.setToolDelegate(this);
	String comment = getComment().getContents();
	commentField.setString(comment);
	commentField.setOwner(this);
	commentHolder.addSubview(commentField);
	this.addSubview(commentHolder);
	commentHolder.sizeToMinSize();
	this.setDirty();
    }
    
    private Comment getComment() {
	return (Comment) this.getModelObject();
    }
    
    private void lockWindow(boolean lock) {
	CharacterWindow cw = (CharacterWindow) this.window();
	if (cw != null) {
	    if (lock)
		cw.lockContentView();
	    else
		cw.unlockContentView();
	}
    }
    
    private void openComment() {
	open = true;
	getComment().setIsOpen(open);
	lockWindow(true);
	commentHolder.sizeTo(commentHolder.width(),
			     (commentField.bounds.maxY()
			      + commentHolder.border().bottomMargin() + 1));
	layoutView(0, 0);
	lockWindow(false);
    }
    
    private void closeComment() {
	open = false;
	getComment().setIsOpen(open);
	lockWindow(true);
	commentHolder.sizeTo(commentHolder.width(),
			     (commentHolder.border().topMargin()
			      + commentHolder.border().bottomMargin()
			      + Util.commentFontHeight));
	layoutView(0, 0);
	lockWindow(false);
    }
    
    void setOpen(boolean opened) {
	if (opened)
	    openComment();
	else
	    closeComment();
    }
    
    private void setComment(String s) {
	getComment().setContents(s);
    }
    
    public void layoutView(int dx, int dy) {
	opener.moveTo((commentHolder.width() - opener.width()) / 2,
		      commentHolder.bounds.maxY());
	this.sizeTo(commentHolder.width(),
		    opener.bounds.maxY() + this.border().bottomMargin());
    }
    
    public void subviewDidResize(View subview) {
	if (subview == commentField)
	    openComment();
	else
	    super.subviewDidResize(subview);
    }
    
    public void discard() {
	getComment().removeView(this);
	super.discard();
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
	if (!open)
	    openComment();
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
		    ((CommentScrap) view).commentField.setString(comment);
		}
	    }, null);
	}
    }
    
    void update() {
	/* empty */
    }
    
    public void performCommand(String command, Object arg) {
	if (arg == opener || command.equals("Open Editor")) {
	    if (open)
		closeComment();
	    else
		openComment();
	} else
	    super.performCommand(command, arg);
    }
}
