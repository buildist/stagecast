/* ObjectSieve - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public class ObjectSieve extends AbstractSieve
{
    private static final String CREATE_TAG = "cr";
    private static final String DESTROY_TAG = "de";
    
    public static interface PresenterNotifier extends AbstractSieve.Notifier
    {
	public void create(Object object);
	
	public void destroy(Object object);
    }
    
    public ObjectSieve(AbstractSieve.Notifier n) {
	super(n);
    }
    
    public void creation(Object item) {
	this.getNotifier().addSieveDatum(new Object[] { this, "cr", item });
    }
    
    public void destruction(Object item) {
	this.getNotifier().addSieveDatum(new Object[] { this, "de", item });
    }
    
    public void processSieveDatum(Object[] datum) {
	if (datum[1] == "cr")
	    ((PresenterNotifier) this.getNotifier()).create(datum[2]);
	else
	    ((PresenterNotifier) this.getNotifier()).destroy(datum[2]);
    }
}
