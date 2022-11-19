/* ToolSource - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.View;

public interface ToolSource
{
    public View sourceView(ToolSession toolsession);
    
    public void toolWasAccepted(ToolSession toolsession);
    
    public void toolWasRejected(ToolSession toolsession);
    
    public void sessionEnded(ToolSession toolsession);
}
