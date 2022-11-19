/* UpdateManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Vector;

class UpdateManager implements Debug.Constants
{
    private Vector watchers = new Vector();
    
    int size() {
	return watchers.size();
    }
    
    void add(Watcher w) {
	watchers.addElement(w);
    }
    
    void remove(Watcher w) {
	boolean removed = watchers.removeElement(w);
	if (Debug.lookup("debug.consistency") && !removed) {
	    Debug.print(true, "Watcher ", w, " not found");
	    Debug.stackTrace();
	}
    }
    
    void removeAllWatchers() {
	watchers.removeAllElements();
    }
    
    void update(Object target, Object value) {
	Watcher[] watchArray = new Watcher[watchers.size()];
	watchers.copyInto(watchArray);
	for (int i = 0; i < watchArray.length; i++)
	    watchArray[i].update(target, value);
    }
}
