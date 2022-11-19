/* PlaywriteInternalError - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class PlaywriteInternalError extends RuntimeException
{
    public PlaywriteInternalError(String s) {
	super(s);
	PlaywriteSystem.errorBeep();
    }
}
