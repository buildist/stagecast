/* HTMLTokenGenerator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class HTMLTokenGenerator extends FilterInputStream
{
    public static final byte NULL_TOKEN = 0;
    public static final byte STRING_TOKEN = 1;
    public static final byte MARKER_BEGIN_TOKEN = 2;
    public static final byte MARKER_END_TOKEN = 3;
    public static final byte COMMENT_TOKEN = 4;
    static final byte LAST_TOKEN_TYPE = 4;
    static final int CHARACTER_COUNT_PER_ARRAY = 128;
    static final int CCPA_BIT_COUNT = 7;
    static final int CCPA_MASK = 127;
    static final int PARSING_NONE_STATE = 0;
    static final int PARSING_STRING_STATE = 1;
    static final int PARSING_MARKER_STATE = 2;
    static final int PARSING_COMMENT_STATE = 3;
    static final int PARSING_MARKER_OR_COMMENT_STATE = 4;
    static final int PARSING_END_COMMENT_ONE_STATE = 5;
    static final int PARSING_END_COMMENT_TWO_STATE = 6;
    private byte[][] input = new byte[1][];
    private int nextAvailableByteIndex;
    private int markedByteIndex;
    private int nextFreeByteSlotIndex;
    private int currentLineNumber;
    private int parserState;
    private int currentToken;
    private String currentTokenString;
    private String currentTokenAttributes;
    
    public HTMLTokenGenerator(InputStream inputstream) {
	super(inputstream);
	input[0] = new byte[128];
	nextAvailableByteIndex = 0;
	nextFreeByteSlotIndex = 0;
	currentLineNumber = 0;
	parserState = 0;
    }
    
    private final void markCurrentCharacter() {
	markedByteIndex = nextAvailableByteIndex;
    }
    
    private final void markPreviousCharacter() {
	markedByteIndex = nextAvailableByteIndex - 1;
    }
    
    private final void growInputBuffer() {
	byte[][] is = new byte[input.length + 1][];
	System.arraycopy(input, 0, is, 0, input.length);
	is[input.length] = new byte[128];
	input = is;
    }
    
    private final void readMoreCharacters() throws IOException {
	int i = nextFreeByteSlotIndex >> 7;
	if (i >= input.length)
	    growInputBuffer();
	int i_0_ = this.read(input[i], nextFreeByteSlotIndex & 0x7f,
			     128 - (nextFreeByteSlotIndex & 0x7f));
	if (i_0_ != -1)
	    nextFreeByteSlotIndex += i_0_;
	else
	    return;
	if (i_0_ < 128) {
	    i = nextFreeByteSlotIndex >> 7;
	    if (i >= input.length)
		growInputBuffer();
	    i_0_ = this.read(input[i], nextFreeByteSlotIndex & 0x7f,
			     128 - (nextFreeByteSlotIndex & 0x7f));
	    if (i_0_ != -1)
		nextFreeByteSlotIndex += i_0_;
	}
    }
    
    private final boolean hasMoreCharacters() throws IOException {
	if (nextAvailableByteIndex < nextFreeByteSlotIndex)
	    return true;
	readMoreCharacters();
	if (nextAvailableByteIndex < nextFreeByteSlotIndex)
	    return true;
	return false;
    }
    
    private final byte peekNextCharacter() throws IOException {
	byte i = 0;
	if (nextAvailableByteIndex >= nextFreeByteSlotIndex)
	    readMoreCharacters();
	if (nextAvailableByteIndex < nextFreeByteSlotIndex) {
	    i = (input[nextAvailableByteIndex >> 7]
		 [nextAvailableByteIndex & 0x7f]);
	    nextAvailableByteIndex++;
	}
	return i;
    }
    
    private final void rewindToMarkedCharacter() {
	nextAvailableByteIndex = markedByteIndex;
    }
    
    private final void deletePeekedCharacters() {
	markedByteIndex = -1;
	while (nextAvailableByteIndex >> 7 > 0) {
	    byte[] is = input[0];
	    int i = 0;
	    for (int i_1_ = input.length - 1; i < i_1_; i++)
		input[i] = input[i + 1];
	    input[input.length - 1] = is;
	    nextAvailableByteIndex -= 128;
	    nextFreeByteSlotIndex -= 128;
	}
    }
    
    private final void deletePeekedCharactersMinusOne() {
	markedByteIndex = -1;
	while (nextAvailableByteIndex - 1 >> 7 > 0) {
	    byte[] is = input[0];
	    int i = 0;
	    for (int i_2_ = input.length - 1; i < i_2_; i++)
		input[i] = input[i + 1];
	    input[input.length - 1] = is;
	    nextAvailableByteIndex -= 128;
	    nextFreeByteSlotIndex -= 128;
	}
    }
    
    private final byte[] getAndDeletePeekedCharacters() {
	int i = nextAvailableByteIndex - markedByteIndex;
	byte[] is = new byte[i];
	int i_3_ = markedByteIndex;
	for (int i_4_ = markedByteIndex + i; i_3_ < i_4_; i_3_++)
	    is[i_3_ - markedByteIndex] = input[i_3_ >> 7][i_3_ & 0x7f];
	deletePeekedCharacters();
	markedByteIndex = -1;
	return is;
    }
    
    private final byte[] getAndDeletePeekedCharactersMinusOne() {
	int i = nextAvailableByteIndex - markedByteIndex - 1;
	byte[] is = new byte[i];
	int i_5_ = markedByteIndex;
	for (int i_6_ = markedByteIndex + i; i_5_ < i_6_; i_5_++)
	    is[i_5_ - markedByteIndex] = input[i_5_ >> 7][i_5_ & 0x7f];
	deletePeekedCharactersMinusOne();
	markedByteIndex = -1;
	return is;
    }
    
    private final boolean isSpaceOrCR(byte i) {
	if (i == 32 || i == 9 || i == 10 || i == 13)
	    return true;
	return false;
    }
    
    private String attributes(byte[] is) throws HTMLParsingException {
	if (is.length == 0 || is[0] != 60 || is[is.length - 1] != 62)
	    syntaxError("Malformed marker");
	int i = 1;
	int i_7_;
	for (i_7_ = is.length; i < i_7_; i++) {
	    if (!isSpaceOrCR(is[i]))
		break;
	}
	for (/**/; i < i_7_; i++) {
	    if (isSpaceOrCR(is[i]))
		break;
	}
	for (/**/; i < i_7_ && isSpaceOrCR(is[i]); i++) {
	    /* empty */
	}
	if (i_7_ - 1 - i > 0)
	    return new String(is, 0, i, i_7_ - 1 - i);
	return "";
    }
    
    private String marker(byte[] is) throws HTMLParsingException {
	if (is.length == 0 || is[0] != 60 || is[is.length - 1] != 62)
	    syntaxError("Malformed marker");
	int i_8_;
	int i = i_8_ = 1;
	int i_9_ = is.length;
	while (i_8_ < i_9_ && isSpaceOrCR(is[i_8_])) {
	    i_8_++;
	    i++;
	}
	if (is[i_8_] == 47) {
	    i_8_++;
	    i++;
	}
	for (/**/; i_8_ < i_9_ - 1 && !isSpaceOrCR(is[i_8_]); i_8_++) {
	    /* empty */
	}
	return new String(is, 0, i, i_8_ - i).toUpperCase();
    }
    
    private boolean isMarkerBegin(byte[] is) throws HTMLParsingException {
	if (is.length == 0 || is[0] != 60 || is[is.length - 1] != 62)
	    syntaxError("Malformed marker");
	int i = 1;
	for (int i_10_ = is.length; i < i_10_ && isSpaceOrCR(is[i]); i++) {
	    /* empty */
	}
	if (is[i] == 47)
	    return false;
	return true;
    }
    
    private final void parseOneToken()
	throws HTMLParsingException, IOException {
	while (currentToken == 0) {
	    byte i;
	    if (hasMoreCharacters())
		i = peekNextCharacter();
	    else {
		if (parserState != 1 && markedByteIndex != -1)
		    rewindToMarkedCharacter();
		break;
	    }
	    if (i == 10)
		currentLineNumber++;
	    switch (parserState) {
	    case 0:
		if (i == 60)
		    parserState = 4;
		else
		    parserState = 1;
		markPreviousCharacter();
		break;
	    case 1:
		if (i == 60) {
		    currentToken = 1;
		    currentTokenAttributes = null;
		    currentTokenString
			= new String(getAndDeletePeekedCharactersMinusOne(),
				     0);
		    markPreviousCharacter();
		    parserState = 4;
		}
		break;
	    case 2:
		if (i == 62) {
		    byte[] is = getAndDeletePeekedCharacters();
		    if (isMarkerBegin(is))
			currentToken = 2;
		    else
			currentToken = 3;
		    currentTokenAttributes = attributes(is);
		    currentTokenString = marker(is);
		    parserState = 0;
		}
		break;
	    case 4:
		if (i == 33)
		    parserState = 3;
		else
		    parserState = 2;
		break;
	    case 3:
		if (i == 45)
		    parserState = 5;
		else if (i == 62) {
		    currentToken = 4;
		    currentTokenString
			= new String(getAndDeletePeekedCharacters(), 0);
		    currentTokenAttributes = null;
		    parserState = 0;
		}
		break;
	    case 5:
		if (i == 45)
		    parserState = 6;
		else if (i == 62) {
		    currentToken = 4;
		    currentTokenString
			= new String(getAndDeletePeekedCharacters(), 0);
		    currentTokenAttributes = null;
		    parserState = 0;
		} else
		    parserState = 3;
		break;
	    case 6:
		if (i != 10 && i != 13) {
		    if (i == 62) {
			currentToken = 4;
			currentTokenString
			    = new String(getAndDeletePeekedCharacters(), 0);
			currentTokenAttributes = null;
			parserState = 0;
		    } else
			parserState = 3;
		}
		break;
	    }
	}
	if (currentToken == 0 && !hasMoreCharacters()) {
	    switch (parserState) {
	    case 1:
		currentToken = 1;
		currentTokenString
		    = new String(getAndDeletePeekedCharacters(), 0);
		currentTokenAttributes = null;
		parserState = 0;
		break;
	    case 2:
	    case 4:
		parserState = 0;
		syntaxError("Unterminated marker");
		break;
	    default:
		parserState = 0;
		syntaxError
		    ("Unterminated comment. Comment should end with -->");
		break;
	    case 0:
		/* empty */
	    }
	}
    }
    
    public final boolean hasMoreTokens()
	throws HTMLParsingException, IOException {
	if (currentToken != 0)
	    return true;
	parseOneToken();
	if (currentToken != 0)
	    return true;
	return false;
    }
    
    public final int nextToken() throws HTMLParsingException, IOException {
	int i = 0;
	if (currentToken == 0)
	    parseOneToken();
	if (currentToken != 0) {
	    i = currentToken;
	    currentToken = 0;
	}
	return i;
    }
    
    final int peekNextToken() throws HTMLParsingException, IOException {
	if (currentToken == 0)
	    parseOneToken();
	return currentToken;
    }
    
    public final String stringForLastToken() {
	return currentTokenString;
    }
    
    public final String attributesForLastToken() {
	return currentTokenAttributes;
    }
    
    final int lineForLastToken() {
	return currentLineNumber + 1;
    }
    
    final void syntaxError(String string) throws HTMLParsingException {
	throw new HTMLParsingException(string, lineForLastToken());
    }
}
