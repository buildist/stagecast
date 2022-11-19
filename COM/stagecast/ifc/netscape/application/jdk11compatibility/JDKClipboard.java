/* JDKClipboard - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application.jdk11compatibility;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import COM.stagecast.ifc.netscape.application.AWTCompatibility;
import COM.stagecast.ifc.netscape.application.TextView;

public class JDKClipboard
    implements COM.stagecast.ifc.netscape.application.Clipboard
{
    public JDKClipboard() throws InstantiationException {
	try {
	    AWTCompatibility.awtToolkit().getSystemClipboard();
	} catch (NoSuchMethodError nosuchmethoderror) {
	    throw new InstantiationException("Wrong AWT version");
	}
    }
    
    public synchronized void setText(String string) {
	Clipboard clipboard
	    = AWTCompatibility.awtToolkit().getSystemClipboard();
	StringSelection stringselection = new StringSelection(string);
	clipboard.setContents(stringselection, null);
    }
    
    public synchronized String text() {
	Clipboard clipboard
	    = AWTCompatibility.awtToolkit().getSystemClipboard();
	Transferable transferable = clipboard.getContents(null);
	String string = null;
	try {
	    string = ((String)
		      transferable.getTransferData(DataFlavor.stringFlavor));
	    string = TextView.stringWithoutCarriageReturns(string);
	} catch (Exception exception) {
	    /* empty */
	}
	return string;
    }
}
