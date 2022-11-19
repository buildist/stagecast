/* TextBag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class TextBag implements Clipboard
{
    String text;
    
    public synchronized void setText(String string) {
	text = string;
    }
    
    public synchronized String text() {
	return text;
    }
}
