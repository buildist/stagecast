/* ViewGlue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.View;

public interface ViewGlue extends Hilitable
{
    public ToolDestination acceptsTool(ToolSession toolsession, int i,
				       int i_0_);
    
    public Object getModelObject();
    
    public void discard();
    
    public View view();
    
    public void willBecomeSelected();
    
    public void willBecomeUnselected();
}
