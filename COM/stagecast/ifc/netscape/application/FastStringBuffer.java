/* FastStringBuffer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class FastStringBuffer
{
    String string;
    char[] buffer;
    int length;
    boolean doublesCapacity;
    
    public FastStringBuffer() {
	this("");
    }
    
    public FastStringBuffer(String string) {
	if (string == null || string.equals(""))
	    buffer = new char[8];
	else {
	    buffer = new char[string.length() + 1];
	    setStringValue(string);
	}
	doublesCapacity = false;
    }
    
    public FastStringBuffer(String string, int i, int i_0_) {
	buffer = new char[i_0_ - i];
	length = i_0_ - i;
	this.string = null;
	doublesCapacity = false;
	string.getChars(i, i_0_, buffer, 0);
    }
    
    public FastStringBuffer(char c) {
	buffer = new char[8];
	buffer[0] = c;
	length = 1;
	doublesCapacity = false;
    }
    
    void _increaseCapacityTo(int i) {
	if (buffer.length <= i) {
	    char[] cs = buffer;
	    if (doublesCapacity)
		buffer = new char[i * 2];
	    else
		buffer = new char[i + 20];
	    System.arraycopy(cs, 0, buffer, 0, cs.length);
	}
    }
    
    public void setDoublesCapacityWhenGrowing(boolean bool) {
	doublesCapacity = bool;
    }
    
    public boolean doublesCapacityWhenGrowing() {
	return doublesCapacity;
    }
    
    public void setStringValue(String string) {
	if (string == null || string.equals(""))
	    length = 0;
	else {
	    length = string.length();
	    _increaseCapacityTo(length);
	    string.getChars(0, length, buffer, 0);
	}
	this.string = string;
    }
    
    public String toString() {
	if (string == null)
	    string = new String(buffer, 0, length);
	return string;
    }
    
    public char charAt(int i) {
	if (i < 0 || i >= length)
	    throw new StringIndexOutOfBoundsException(i);
	return buffer[i];
    }
    
    public int indexOf(char c, int i) {
	if (i < 0 || i >= length)
	    throw new StringIndexOutOfBoundsException(i);
	for (int i_1_ = i; i_1_ < length; i_1_++) {
	    if (buffer[i_1_] == c)
		return i_1_;
	}
	return -1;
    }
    
    public int indexOf(char c) {
	return indexOf(c, 0);
    }
    
    public boolean tabOrSpaceAt(int i) {
	if (i < 0 || i >= length)
	    throw new StringIndexOutOfBoundsException(i);
	return buffer[i] == ' ' || buffer[i] == '\t';
    }
    
    public void append(char c) {
	_increaseCapacityTo(length + 1);
	buffer[length++] = c;
	string = null;
    }
    
    public void append(String string) {
	if (string != null && !string.equals("")) {
	    _increaseCapacityTo(length + string.length());
	    string.getChars(0, string.length(), buffer, length);
	    length += string.length();
	    this.string = null;
	}
    }
    
    public void insert(char c, int i) {
	if (i < 0)
	    throw new StringIndexOutOfBoundsException(i);
	if (i >= length)
	    append(c);
	else if (length < buffer.length) {
	    if (i != length)
		System.arraycopy(buffer, i, buffer, i + 1, length - i);
	    buffer[i] = c;
	    length++;
	    string = null;
	} else {
	    char[] cs = buffer;
	    buffer = new char[buffer.length + 20];
	    if (i > 0)
		System.arraycopy(cs, 0, buffer, 0, i);
	    if (i != length)
		System.arraycopy(cs, i, buffer, i + 1, length - i);
	    buffer[i] = c;
	    length++;
	    string = null;
	}
    }
    
    public void insert(String string, int i) {
	if (i < 0)
	    throw new StringIndexOutOfBoundsException(i);
	if (i > length)
	    append(string);
	else if (string != null && !string.equals("")) {
	    int i_2_ = string.length();
	    if (length + i_2_ < buffer.length) {
		System.arraycopy(buffer, i, buffer, i + i_2_, length - i);
		string.getChars(0, i_2_, buffer, i);
		length += i_2_;
		this.string = null;
	    } else {
		char[] cs = buffer;
		buffer = new char[length + i_2_ + 20];
		if (i > 0)
		    System.arraycopy(cs, 0, buffer, 0, i);
		System.arraycopy(cs, i, buffer, i + i_2_, length - i);
		string.getChars(0, i_2_, buffer, i);
		length += i_2_;
		this.string = null;
	    }
	}
    }
    
    public void removeCharAt(int i) {
	if (i < 0 || i >= length)
	    throw new StringIndexOutOfBoundsException(i);
	if (i + 1 == length) {
	    length--;
	    string = null;
	} else {
	    System.arraycopy(buffer, i + 1, buffer, i, length - (i + 1));
	    length--;
	    string = null;
	}
    }
    
    public void truncateToLength(int i) {
	if (i >= 0 && i <= length) {
	    length = i;
	    string = null;
	}
    }
    
    public int length() {
	return length;
    }
    
    public void moveChars(int i, int i_3_) {
	if (i > i_3_) {
	    if (i < 0 || i >= length)
		throw new StringIndexOutOfBoundsException(i);
	    if (i_3_ < 0 || i_3_ >= length)
		throw new StringIndexOutOfBoundsException(i_3_);
	    System.arraycopy(buffer, i, buffer, i_3_, length - i);
	    length -= i - i_3_;
	    string = null;
	}
    }
    
    public char[] charArray() {
	return buffer;
    }
}
