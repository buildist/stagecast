/* DragSource - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface DragSource
{
    public View sourceView(DragSession dragsession);
    
    public void dragWasAccepted(DragSession dragsession);
    
    public boolean dragWasRejected(DragSession dragsession);
}
