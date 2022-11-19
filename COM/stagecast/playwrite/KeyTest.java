/* KeyTest - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class KeyTest extends RuleTest
    implements Externalizable, ResourceIDs.CommandIDs, ResourceIDs.DialogIDs,
	       ResourceIDs.KeyIDs
{
    private static final int ANY_KEY = -1;
    private static final Integer ZERO = new Integer(0);
    private static final Integer ONE = new Integer(1);
    static final int storeVersion = 3;
    static final long serialVersionUID = -3819410108755019058L;
    private int _key;
    private int _modifiers = 0;
    
    KeyTest(KeyEvent event) {
	this(event.key, event.modifiers);
    }
    
    KeyTest(int key, int modifiers) {
	init(key, modifiers);
    }
    
    KeyTest(int key) {
	if (key >= 65 && key <= 90)
	    init(key, 1);
	else
	    init(key, 0);
    }
    
    public KeyTest() {
	/* empty */
    }
    
    private void init(int key, int modifiers) {
	_key = key;
	_modifiers = modifiers;
    }
    
    public static KeyTest createForRuleEditor(World world) {
	Point loc = PlaywriteRoot.getMainRootView().mousePoint();
	loc.x -= 30;
	loc.y -= 20;
	return makeKeyTest(loc);
    }
    
    static KeyTest makeKeyTest(Point location) {
	KeyTest keyTest = null;
	PressKeyDialog dialog = new PressKeyDialog(location);
	String result = dialog.getAnswerModally();
	if (!"command c".equals(result)) {
	    if (result.equals("dialog pak any"))
		keyTest = new KeyTest(-1);
	    else
		keyTest = new KeyTest(dialog.getKeyEvent());
	}
	return keyTest;
    }
    
    public boolean evaluate(CharacterInstance self) {
	Vector events = self.getWorld().getActiveEvents();
	if (!((Stage) self.getContainer()).isViewed())
	    return false;
	synchronized (events) {
	    int n = events.size();
	    for (int i = 0; i < n; i++) {
		PlaywriteEvent event = (PlaywriteEvent) events.elementAt(i);
		if (event.isKeyEvent() && _key == -1)
		    return true;
		if (event.isKeyPress(_key, _modifiers))
		    return true;
	    }
	    return false;
	}
    }
    
    static String keyName(KeyEvent event) {
	return keyName(event.key, event.modifiers);
    }
    
    static String keyName(int key, int modifiers) {
	StringBuffer modBuffer = new StringBuffer(40);
	String name = keyName(key);
	if ((modifiers & 0x1) != 0 && name.length() > 0)
	    modBuffer.append(Resource.getText("shift key id"));
	if ((modifiers & 0x2) != 0)
	    modBuffer.append(Resource.getText("control key id"));
	if ((modifiers & 0x4) != 0) {
	    if (PlaywriteSystem.isMacintosh())
		modBuffer.append(Resource.getText("command key id"));
	    else
		modBuffer.append(Resource.getText("meta key id"));
	}
	if ((modifiers & 0x8) != 0) {
	    if (PlaywriteSystem.isMacintosh())
		modBuffer.append(Resource.getText("option key id"));
	    else
		modBuffer.append(Resource.getText("alt key id"));
	}
	return Resource.getTextAndFormat("key name format id",
					 new Object[] { modBuffer.toString(),
							name });
    }
    
    static String keyName(int key) {
	String resourceKey = null;
	switch (key) {
	case 10:
	    resourceKey = "return key id";
	    break;
	case 9:
	    resourceKey = "tab key id";
	    break;
	case 8:
	    resourceKey = "backspace key id";
	    break;
	case 127:
	    resourceKey = "delete key id";
	    break;
	case 27:
	    resourceKey = "escape key id";
	    break;
	case 1004:
	    resourceKey = "up-arrow key id";
	    break;
	case 1005:
	    resourceKey = "down-arrow key id";
	    break;
	case 1006:
	    resourceKey = "left-arrow key id";
	    break;
	case 1007:
	    resourceKey = "right-arrow key id";
	    break;
	case 1002:
	    resourceKey = "page-up key id";
	    break;
	case 1003:
	    resourceKey = "page-down key id";
	    break;
	case 1000:
	    resourceKey = "home key id";
	    break;
	case 1001:
	    resourceKey = "end key id";
	    break;
	case 1008:
	    resourceKey = "F1 key id";
	    break;
	case 1009:
	    resourceKey = "F2 key id";
	    break;
	case 1010:
	    resourceKey = "F3 key id";
	    break;
	case 1011:
	    resourceKey = "F4 key id";
	    break;
	case 1012:
	    resourceKey = "F5 key id";
	    break;
	case 1013:
	    resourceKey = "F6 key id";
	    break;
	case 1014:
	    resourceKey = "F7 key id";
	    break;
	case 1015:
	    resourceKey = "F8 key id";
	    break;
	case 1016:
	    resourceKey = "F9 key id";
	    break;
	case 1017:
	    resourceKey = "F10 key id";
	    break;
	case 1018:
	    resourceKey = "F11 key id";
	    break;
	case 1019:
	    resourceKey = "F12 key id";
	    break;
	default: {
	    String keyText = String.valueOf((char) key);
	    if (keyText.equals(" "))
		resourceKey = "space key id";
	    else
		return keyText;
	}
	}
	return Resource.getText(resourceKey);
    }
    
    public PlaywriteView createView() {
	String keyName = keyName(_key, _modifiers);
	PlaywriteView view
	    = new LineView(this, 8, "key test fmt",
			   new Object[] { _key == -1 ? ZERO : ONE, keyName },
			   null);
	return view;
    }
    
    public String toString() {
	String keyName = keyName(_key, _modifiers);
	return Resource.getTextAndFormat("key test fmt",
					 (new Object[]
					  { _key == -1 ? ZERO : ONE,
					    keyName }));
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	KeyTest newTest = (KeyTest) map.get(this);
	if (newTest != null)
	    return newTest;
	newTest = new KeyTest(_key, _modifiers);
	map.put(this, newTest);
	return newTest;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(_modifiers);
	out.writeInt(_key);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(KeyTest.class);
	switch (version) {
	case 1:
	    in.readObject();
	    break;
	case 3:
	    _modifiers = in.readInt();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 3);
	case 2:
	    /* empty */
	}
	_key = in.readInt();
    }
}
