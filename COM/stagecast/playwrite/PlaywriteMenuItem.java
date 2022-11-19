/* PlaywriteMenuItem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MenuItem;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PlaywriteMenuItem extends MenuItem implements ResourceIDs.MenuIDs
{
    private World _world;
    private StateWatcher _stateWatcher;
    
    public PlaywriteMenuItem(String title, String command, Target target) {
	super(title, command, target);
	if (target instanceof World)
	    attachToWorld((World) target);
    }
    
    public PlaywriteMenuItem(String title, char key, String command,
			     Target target) {
	super(title, key, command, target);
	if (target instanceof World)
	    attachToWorld((World) target);
    }
    
    public final World getWorld() {
	return _world;
    }
    
    public final StateWatcher getStateWatcher() {
	return _stateWatcher;
    }
    
    public final void setStateWatcher(StateWatcher watcher) {
	if (_stateWatcher != null)
	    _world.removeStateWatcher(_stateWatcher);
	_stateWatcher = watcher;
	if (_stateWatcher != null)
	    _world.addStateWatcher(_stateWatcher);
    }
    
    public final void attachToWorld(World world) {
	_world = world;
	setDefaultStateWatcher();
    }
    
    protected void setDefaultStateWatcher() {
	setStateWatcher(new StateWatcher() {
	    public void stateChanged(Object target, Object oldState,
				     Object transition, Object newState) {
		if (oldState == World.STOPPED
		    && (newState == World.RECORDING
			|| newState == World.EDITING
			|| newState == World.RUNNING))
		    setEnabled(false);
		if (newState == World.STOPPED
		    && (oldState == World.RECORDING
			|| oldState == World.EDITING
			|| oldState == World.RUNNING
			|| oldState == World.OPENING))
		    setEnabled(true);
	    }
	});
    }
    
    public Object clone() {
	PlaywriteMenuItem newItem = (PlaywriteMenuItem) super.clone();
	newItem._world = null;
	newItem._stateWatcher = null;
	if (newItem.target() == this)
	    newItem.setTarget((Target) newItem);
	Debug.print(true, "-- cloning ", this, " returning ", newItem);
	return newItem;
    }
    
    public void sendCommand() {
	if (this.target() instanceof World)
	    super.sendCommand();
	else {
	    try {
		Debug.print(true, "-- invoking menu command on ", this);
		super.sendCommand();
	    } catch (RecoverableException e) {
		e.showDialog();
	    } catch (Throwable t) {
		Debug.stackTrace(t);
	    }
	}
    }
    
    public void setEnabled(boolean b) {
	if (!Tutorial.isTutorialRunning()
	    || !(b & (Tutorial.allowMenuCommandEnable(this.command()) ^ true)))
	    super.setEnabled(b);
    }
    
    protected void drawStringInRect(Graphics g, String title, Font titleFont,
				    Rect textBounds, int justification) {
	String acceleratorKey
	    = (PlaywriteSystem.isMacintosh()
	       ? Resource.getText("menu mac accelerator key")
	       : Resource.getText("menu win accelerator key"));
	int width = 0;
	if (this.isEnabled() && !this.isSelected())
	    g.setColor(this.textColor());
	else if (this.isEnabled() && this.isSelected())
	    g.setColor(this.selectedTextColor());
	else
	    g.setColor(this.disabledColor());
	g.setFont(titleFont);
	g.drawStringInRect(title, textBounds, justification);
	if (this.commandKey() != 0) {
	    Font font = this.font();
	    if (font != null) {
		width = font.fontMetrics().stringWidth(acceleratorKey + "W");
		width += 10;
	    }
	    Rect textRect = new Rect(textBounds.x + textBounds.width - width,
				     textBounds.y, width, textBounds.height);
	    String string = acceleratorKey + this.commandKey();
	    g.drawStringInRect(string, textRect, 0);
	}
    }
}
