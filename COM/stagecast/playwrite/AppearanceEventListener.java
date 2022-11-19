/* AppearanceEventListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;

public interface AppearanceEventListener
{
    public void setTool(AppearanceEditorTool appearanceeditortool);
    
    public void setBrushWidth(int i);
    
    public void setColor(Color color, boolean bool);
    
    public void setScale(int i);
    
    public void setFont(Font font);
    
    public void setJustification(int i);
}
