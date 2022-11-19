/* BadBackpointerError - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

class BadBackpointerError extends RuntimeException
{
    BadBackpointerError(Object parent, Object child) {
	super("Object " + child + " does not point back to parent " + parent);
    }
}
