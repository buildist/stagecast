/* HTMLParsingException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public class HTMLParsingException extends Exception
{
    int lineNumber = -1;
    
    private HTMLParsingException() {
	/* empty */
    }
    
    public HTMLParsingException(String string, int i) {
	super(string);
	lineNumber = i;
    }
    
    public int lineNumber() {
	return lineNumber;
    }
}
