/* ApplicationObserver - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface ApplicationObserver
{
    public void applicationDidStart(Application application);
    
    public void applicationDidStop(Application application);
    
    public void focusDidChange(Application application, View view);
    
    public void currentDocumentDidChange(Application application,
					 Window window);
    
    public void applicationDidPause(Application application);
    
    public void applicationDidResume(Application application);
}
