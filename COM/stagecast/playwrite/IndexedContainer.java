/* IndexedContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;

interface IndexedContainer extends GenericContainer
{
    public static interface Notifier
    {
	public void indexedAdded(Indexed indexed);
	
	public void indexedRemoved(Indexed indexed);
	
	public void userModified(Indexed indexed);
    }
    
    public Indexed getElementAt(int i);
    
    public int insertElementAt(Indexed indexed, int i);
    
    public int getNumberOfElements();
    
    public Enumeration getElements();
    
    public boolean permitDrag(Indexed indexed);
    
    public void viewDiscarded(View view);
    
    public void setNotifier(Notifier notifier);
    
    public boolean forceRemove(Indexed indexed);
    
    public void userModified(Indexed indexed);
}
