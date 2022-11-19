/* ActionSieve - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.simmer.ClientServerMessages;

public class ActionSieve extends AbstractSieve implements ClientServerMessages
{
    private Hashtable _actionMap = new Hashtable(10);
    
    public static interface ActionNotifier extends AbstractSieve.Notifier
    {
	public void doAction(Object object, String string, Object object_0_);
    }
    
    public ActionSieve(AbstractSieve.Notifier n) {
	super(n);
	registerAction("Stagecast.Stage:action.set_z", "SZ");
	registerAction("Stagecast.World:action.num_visi_regions", "VR");
	registerAction("Stagecast.Stage:action.set_visi_stage", "SV");
	registerAction("Stagecast.Stage:action.reset", "RE");
	registerAction("VOODOO", "OU");
    }
    
    public final void registerAction(String localID,
				     String clientServerMessagesID) {
	Object previousValue = _actionMap.put(localID, clientServerMessagesID);
	ASSERT.isTrue(previousValue == null);
    }
    
    public void action(Object target, String id, Object data) {
	this.getNotifier().addSieveDatum(new Object[]
					 { this, target,
					   (String) _actionMap.get(id),
					   data });
    }
    
    public void processSieveDatum(Object[] datum) {
	((ActionNotifier) this.getNotifier())
	    .doAction(datum[1], (String) datum[2], datum[3]);
    }
}
