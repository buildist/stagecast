/* WorldDrawingModule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Target;

public interface WorldDrawingModule
{
    public void screenRefresh();
    
    public void forceRepaint();
    
    public void requestDrawStages();
    
    public void disableStageDrawing();
    
    public void reenableStageDrawing();
    
    public void addSyncAction(Target target, String string, Object object);
    
    public boolean isInSyncPhase();
    
    public void close();
}
