/* DragDestination - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface DragDestination
{
    public boolean dragEntered(DragSession dragsession);
    
    public boolean dragMoved(DragSession dragsession);
    
    public void dragExited(DragSession dragsession);
    
    public boolean dragDropped(DragSession dragsession);
}
