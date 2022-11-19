/* WindowOwner - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface WindowOwner
{
    public boolean windowWillShow(Window window);
    
    public void windowDidShow(Window window);
    
    public boolean windowWillHide(Window window);
    
    public void windowDidHide(Window window);
    
    public void windowDidBecomeMain(Window window);
    
    public void windowDidResignMain(Window window);
    
    public void windowWillSizeBy(Window window, Size size);
}
