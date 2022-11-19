/* Password - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Hashtable;

final class Password implements Externalizable
{
    static final String ENCRYPTED_PASSWORD = new String();
    static final String ATTRIBUTE_CUSTOM_ENCODE = new String();
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108751873330L;
    Hashtable _data;
    
    Password(String pass, Object[] attributes) {
	_data = new Hashtable(5);
	if (attributes != null) {
	    for (int i = 0; i < attributes.length; i++)
		_data.put(attributes[i], attributes[i]);
	}
	_data.put(ENCRYPTED_PASSWORD, encrypt(pass));
    }
    
    public Password() {
	_data = new Hashtable(5);
    }
    
    private String encrypt(String s) {
	char[] charArray = s.toCharArray();
	for (int i = 0; i < charArray.length; i++)
	    charArray[i] = (char) (charArray[i] ^ '\uffff');
	return new String(charArray);
    }
    
    final boolean check(String s) {
	return encrypt(s).equals((String) _data.get(ENCRYPTED_PASSWORD));
    }
    
    final void set(String s) {
	_data.put(ENCRYPTED_PASSWORD, encrypt(s));
    }
    
    final void clear() {
	_data.remove(ENCRYPTED_PASSWORD);
    }
    
    final boolean isEmpty() {
	return _data.get(ENCRYPTED_PASSWORD) == null;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	((WorldOutStream) out).writeHashtable(_data, false);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_data = ((WorldInStream) in).readHashtable();
    }
    
    void writePassword(WorldOutStream out) throws IOException {
	out.writeHashtable(_data, false);
    }
    
    void readPassword(WorldInStream in)
	throws IOException, ClassNotFoundException {
	_data = in.readHashtable();
    }
}
