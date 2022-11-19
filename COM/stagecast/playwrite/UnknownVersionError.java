/* UnknownVersionError - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class UnknownVersionError extends RuntimeException
{
    public UnknownVersionError(String source, int encountered, int expected) {
	super("Unknown version for " + source + ", found version "
	      + encountered + ", expected " + expected);
    }
    
    public UnknownVersionError(Class cls, int encountered, int expected) {
	this(cls.getName(), encountered, expected);
    }
}
