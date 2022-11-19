/* ToolTipable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.MouseEvent;

interface ToolTipable
{
    public void setToolTipText(String string);
    
    public String getToolTipText();
    
    public void mouseEntered(MouseEvent mouseevent);
    
    public void mouseExited(MouseEvent mouseevent);
    
    public boolean mouseDown(MouseEvent mouseevent);
}
