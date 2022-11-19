/* RecoverableException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class RecoverableException extends RuntimeException
{
    private boolean _messageIsResourceID;
    private Object[] _messageArgs;
    
    public RecoverableException(String message, boolean isResourceID) {
	super(message);
	_messageIsResourceID = isResourceID;
	_messageArgs = null;
    }
    
    public RecoverableException(String message, Object[] args) {
	super(message);
	_messageIsResourceID = true;
	_messageArgs = args;
    }
    
    public String getLocalizedMessage() {
	if (_messageIsResourceID) {
	    if (_messageArgs == null)
		return Resource.getText(this.getMessage());
	    return Resource.getTextAndFormat(this.getMessage(), _messageArgs);
	}
	return super.getLocalizedMessage();
    }
    
    public void showDialog() {
	PlaywriteDialog.warning(getLocalizedMessage(), true);
    }
}
