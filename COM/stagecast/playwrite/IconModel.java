/* IconModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Rect;

public interface IconModel
{
    public Image getIconImage();
    
    public Rect getIconImageRect();
    
    public void setIconImage(Image image);
    
    public String getIconName();
    
    public void setIconName(String string);
    
    public boolean hasIconViews();
    
    public ViewManager getIconViewManager();
}
