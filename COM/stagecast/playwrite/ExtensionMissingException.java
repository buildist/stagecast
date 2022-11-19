/* ExtensionMissingException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class ExtensionMissingException extends RecoverableException
    implements ResourceIDs.ExtensionIDs
{
    ExtensionMissingException() {
	super("EXT mea", new Object[0]);
    }
    
    ExtensionMissingException(String missing, String url) {
	super(url == null || "".equals(url) ? "EXT me" : "EXT meu",
	      new Object[] { missing, url });
    }
}
