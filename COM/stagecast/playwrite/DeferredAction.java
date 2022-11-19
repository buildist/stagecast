/* DeferredAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public abstract class DeferredAction extends RuleAction
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108752332082L;
    private CharacterContainer _container;
    private int _baseX;
    private int _baseY;
    
    public CharacterContainer getCharacterContainer() {
	return _container;
    }
    
    public int getBaseX() {
	return _baseX;
    }
    
    public int getBaseY() {
	return _baseY;
    }
    
    public Object execute(CharacterContainer container, int baseX, int baseY) {
	Stage s;
	if (container instanceof Stage
	    && (s = (Stage) container).isExecuting()) {
	    _container = container;
	    _baseX = baseX;
	    _baseY = baseY;
	    s.addDeferredAction(this);
	} else
	    deferredExecute(container, baseX, baseY);
	return RuleAction.SUCCESS;
    }
    
    public abstract void deferredExecute(CharacterContainer charactercontainer,
					 int i, int i_0_);
    
    public void deferredExecute() {
	deferredExecute(_container, _baseX, _baseY);
    }
    
    public void performCommand(String command, Object data) {
	ASSERT.isTrue(command != "execute");
	super.performCommand(command, data);
    }
}
