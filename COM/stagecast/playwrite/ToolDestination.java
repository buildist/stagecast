/* ToolDestination - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface ToolDestination
{
    public boolean toolEntered(ToolSession toolsession);
    
    public boolean toolMoved(ToolSession toolsession);
    
    public void toolExited(ToolSession toolsession);
    
    public boolean toolClicked(ToolSession toolsession);
    
    public void toolDragged(ToolSession toolsession);
    
    public void toolReleased(ToolSession toolsession);
}
