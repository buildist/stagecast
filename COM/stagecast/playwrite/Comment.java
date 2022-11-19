/* Comment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class Comment extends RuleListItem
    implements Externalizable, ResourceIDs.CharacterWindowIDs
{
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108753315122L;
    private String contents;
    private boolean isOpen = false;
    
    Comment(String string) {
	this.setEnabled(false);
	contents = string;
    }
    
    public Comment() {
	this.setEnabled(false);
	contents = Resource.getText("character window type a comment");
    }
    
    String getContents() {
	return contents;
    }
    
    void setContents(String comments) {
	contents = comments;
    }
    
    boolean getIsOpen() {
	return isOpen;
    }
    
    void setIsOpen(boolean open) {
	isOpen = open;
    }
    
    protected final boolean matchAndExecute(CharacterInstance ch) {
	return false;
    }
    
    RuleListItemView createScrap(CocoaCharacter self) {
	CommentScrap view = new CommentScrap(this);
	this.addView(self, view);
	view.setOpen(isOpen);
	return view;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Comment newComment = (Comment) map.get(this);
	if (newComment != null)
	    return newComment;
	newComment = new Comment(contents);
	map.put(this, newComment);
	return super.copy(map, fullCopy);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeUTF(contents);
	out.writeBoolean(isOpen);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	contents = in.readUTF();
	int loadVersion = ((WorldInStream) in).loadVersion(Comment.class);
	if (loadVersion == 2)
	    isOpen = in.readBoolean();
    }
    
    public String getName() {
	return contents;
    }
    
    public void setName(String name) {
	/* empty */
    }
    
    public String toString() {
	return "<Comment '" + contents + "'>";
    }
}
