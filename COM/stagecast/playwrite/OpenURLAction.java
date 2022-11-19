/* OpenURLAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.applet.AppletContext;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.Expression;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class OpenURLAction extends RuleAction
    implements Externalizable, ResourceIDs.RuleEditorIDs,
	       ResourceIDs.RuleActionIDs
{
    static final int storeVersion = 2;
    static final long serialVersionUID = -3819410108755674418L;
    public static final String ID = "VOODOO";
    private static String[] modeIDs = { "_self", "_parent", "_top", "_blank" };
    private Object _urlObject;
    private String _mode = "_self";
    
    OpenURLAction(Object urlObject) {
	_urlObject = urlObject;
    }
    
    public OpenURLAction() {
	/* empty */
    }
    
    public static RuleAction createForRuleEditor(World world) {
	return new OpenURLAction("http://www.stagecast.com/");
    }
    
    public void setURLObject(Object urlObject) {
	_urlObject = urlObject;
    }
    
    public Object getURLObject() {
	return _urlObject;
    }
    
    public String getMode() {
	return _mode;
    }
    
    public void setMode(String mode) {
	ASSERT.isTrue(_mode == "_self" || _mode == "_parent" || _mode == "_top"
		      || _mode == "_blank");
	_mode = mode;
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	String urlString = null;
	Object result = RuleAction.FAILURE;
	if (_urlObject != null) {
	    Object url = _urlObject;
	    if (_urlObject instanceof Expression)
		url = ((Expression) _urlObject).eval();
	    if (url instanceof String)
		urlString = (String) url;
	    else if (url != null)
		urlString = url.toString();
	}
	if (urlString == null)
	    Debug.print(true, "null URL passed to OpenURLAction");
	else if (PlaywriteRoot.app().isApplet()) {
	    AppletContext appletContext
		= PlaywriteSystem.getApplet().getAppletContext();
	    if (appletContext == null)
		Debug.print(true, "OpenURLAction:no applet context");
	    else {
		URL realURL = null;
		try {
		    realURL = new URL(urlString);
		    appletContext.showDocument(realURL, _mode);
		    result = RuleAction.SUCCESS;
		} catch (java.net.MalformedURLException malformedurlexception) {
		    result = RuleAction.FAILURE;
		}
	    }
	} else if (this.getWorld().getActionSieve() != null)
	    this.getWorld().getActionSieve().action(this.getWorld(), "VOODOO",
						    new Object[] { urlString,
								   _mode });
	if (result == RuleAction.SUCCESS)
	    Debug.print(true, "opening URL:", urlString);
	else if (urlString != null)
	    Debug.print(true, "browser would open URL:", urlString);
	else
	    Debug.print(true, "no URL specified to OpenURLAction");
	return result;
    }
    
    public void undo() {
	/* empty */
    }
    
    public boolean refersTo(ReferencedObject obj) {
	Object ref = null;
	if (_urlObject instanceof Expression)
	    ref = ((Expression) _urlObject).findReferenceTo(obj);
	else if (_urlObject == obj)
	    ref = _urlObject;
	return ref != null;
    }
    
    public PlaywriteView createView() {
	return new OpenURLActionView(this);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	OpenURLAction newAction = (OpenURLAction) map.get(this);
	if (newAction != null)
	    return newAction;
	Object newUrlObject = _urlObject;
	if (_urlObject instanceof Copyable)
	    newUrlObject = ((Copyable) _urlObject).copy(map, fullCopy);
	newAction = new OpenURLAction(newUrlObject);
	newAction._mode = _mode;
	map.put(this, newAction);
	return newAction;
    }
    
    public void summarize(Summary s) {
	Object[] params = { _urlObject, Resource.getText(_mode) };
	s.writeFormat("open url action fmt", null, params);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	out.writeObject(_urlObject);
	out.writeUTF(_mode);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	int version = ((WorldInStream) in).loadVersion(OpenURLAction.class);
	switch (version) {
	case 1:
	    _urlObject = in.readUTF();
	    break;
	case 2: {
	    _urlObject = in.readObject();
	    String temp = in.readUTF();
	    int i = 0;
	    for (i = 0; i < modeIDs.length; i++) {
		if (modeIDs[i].equals(temp)) {
		    setMode(modeIDs[i]);
		    break;
		}
	    }
	    if (i == modeIDs.length)
		Debug.print(true, "error reading OpenURLAction mode =", temp);
	    break;
	}
	default:
	    throw new UnknownVersionError(this.getClass().getName(), version,
					  2);
	}
    }
    
    public String toString() {
	if (_urlObject != null)
	    return "<Go to url '" + _urlObject + "'>";
	return "<Go to url '?'>";
    }
}
