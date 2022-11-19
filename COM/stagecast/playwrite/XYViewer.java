/* XYViewer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Point;

interface XYViewer
{
    public void itemAdded(Contained contained, Point point);
    
    public void itemRemoved(Contained contained);
    
    public void itemUpdated(Contained contained);
    
    public void itemMoved(Contained contained, Point point);
}
