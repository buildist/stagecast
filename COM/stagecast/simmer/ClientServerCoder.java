/* ClientServerCoder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.simmer;

public class ClientServerCoder implements ClientServerMessages
{
    public static final char TOKEN_DELIMITER = '#';
    public static final char ESCAPE_CHARACTER = '%';
    public static final char ESCAPED_DELIM = '1';
    public static final char ESCAPED_NEWLINE = '2';
    public static final int MAX_TOKENS = 15;
    StringBuffer _buffer;
    
    public ClientServerCoder(String msgType) {
	_buffer = new StringBuffer(msgType);
    }
    
    public ClientServerCoder(String msgType, String s) {
	this(msgType);
	append(s);
    }
    
    public ClientServerCoder(String msgType, int itoken) {
	this(msgType);
	append(itoken);
    }
    
    public ClientServerCoder append(String s) {
	_buffer.append('#');
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    if (c == '#') {
		_buffer.append('%');
		_buffer.append('1');
	    } else if (c == '\r' || c == '\n') {
		_buffer.append('%');
		_buffer.append('2');
		if (c == '\r' && i + 1 < s.length() && s.charAt(i + 1) == '\n')
		    i++;
	    } else {
		_buffer.append(c);
		if (c == '%')
		    _buffer.append(c);
	    }
	}
	return this;
    }
    
    public ClientServerCoder append(int itoken) {
	_buffer.append('#');
	_buffer.append(itoken);
	return this;
    }
    
    public ClientServerCoder replace(int itoken) {
	int pos = _buffer.toString().indexOf('#');
	if (pos >= 0)
	    _buffer.setLength(pos);
	append(itoken);
	return this;
    }
    
    public String toString() {
	return _buffer.toString();
    }
    
    public static String getMsgType(String msg) {
	int pos = msg.indexOf('#');
	if (pos < 0)
	    pos = msg.length();
	return msg.substring(0, pos);
    }
    
    public static String[] decode(String msg) {
	boolean escapeMode = false;
	int out = 0;
	int tokenCount = 0;
	int[] tokenLength = new int[15];
	int[] tokenStart = new int[15];
	char[] chars = msg.toCharArray();
	int in = 0;
	while (in < chars.length) {
	    char c = chars[in++];
	    if (escapeMode) {
		if (c == '%')
		    chars[out++] = c;
		else if (c == '1')
		    chars[out++] = '#';
		else if (c == '2')
		    chars[out++] = '\n';
		escapeMode = false;
	    } else if (c == '#') {
		tokenLength[tokenCount] = out - tokenStart[tokenCount];
		tokenCount++;
		tokenStart[tokenCount] = in;
		out = in;
	    } else if (c == '%' && in < chars.length)
		escapeMode = true;
	    else
		chars[out++] = c;
	    if (in == chars.length) {
		tokenLength[tokenCount] = out - tokenStart[tokenCount];
		tokenCount++;
	    }
	}
	String[] tokens = new String[tokenCount];
	for (int i = 0; i < tokenCount; i++)
	    tokens[i] = new String(chars, tokenStart[i], tokenLength[i]);
	return tokens;
    }
}
