/* AbstractSieve - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.Hashtable;

public abstract class AbstractSieve
{
    private static Hashtable notifierStash = new Hashtable(10);
    private Notifier _notifier;
    
    public static interface Notifier
    {
	public void addSieveDatum(Object[] objects);
    }
    
    public static void stashNotifier(Notifier n) {
	notifierStash.put(Thread.currentThread(), n);
    }
    
    public static Notifier getStashedNotifier() {
	return (Notifier) notifierStash.remove(Thread.currentThread());
    }
    
    public AbstractSieve(Notifier n) {
	_notifier = n;
    }
    
    public Notifier getNotifier() {
	return _notifier;
    }
    
    public abstract void processSieveDatum(Object[] objects);
}
