/* PlaywriteEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Event;
import COM.stagecast.ifc.netscape.application.KeyEvent;

public class PlaywriteEvent
{
    public static final int MOUSE_EVENT = 1;
    public static final int KEY_EVENT = 2;
    private Stage stage;
    private int clockTick;
    private int eventType;
    private Event ifcEvent;
    private int squareH = 0;
    private int squareV = 0;
    
    PlaywriteEvent(Event event, int type, int tick) {
	this(event, null, -1, -1, type, tick);
    }
    
    PlaywriteEvent(Event event, Stage stage, int h, int v, int type,
		   int tick) {
	ifcEvent = event;
	eventType = type;
	this.stage = stage;
	clockTick = tick;
	squareH = h;
	squareV = v;
    }
    
    final boolean isMouseEvent() {
	return eventType == 1;
    }
    
    final boolean isKeyEvent() {
	return eventType == 2;
    }
    
    Event getEvent() {
	return ifcEvent;
    }
    
    final Stage getStage() {
	return stage;
    }
    
    final int getClockTick() {
	return clockTick;
    }
    
    final int getH() {
	return squareH;
    }
    
    final int getV() {
	return squareV;
    }
    
    final int getKey() {
	return ((KeyEvent) ifcEvent).key;
    }
    
    final int getModifiers() {
	return ((KeyEvent) ifcEvent).modifiers;
    }
    
    final boolean isMouseClick() {
	return isMouseEvent();
    }
    
    final boolean isMouseClick(int h, int v) {
	return isMouseEvent() && squareH == h && squareV == v;
    }
    
    final boolean isKeyPress(int key, int modifiers) {
	return isKeyEvent() && getKey() == key && getModifiers() == modifiers;
    }
    
    public String toString() {
	if (isMouseEvent())
	    return ("<mouse click at (" + squareH + "," + squareV + "), tick: "
		    + clockTick + ">");
	return "<key press of " + getKey() + ", tick: " + clockTick + ">";
    }
}
