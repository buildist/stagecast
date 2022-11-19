/* FoundationPanelListenerImp - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application.jdk11compatibility;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import COM.stagecast.ifc.netscape.application.ApplicationEvent;
import COM.stagecast.ifc.netscape.application.FoundationPanel;
import COM.stagecast.ifc.netscape.application.FoundationPanelListener;

public class FoundationPanelListenerImp
    implements FoundationPanelListener, KeyListener, FocusListener,
	       MouseListener, MouseMotionListener
{
    FoundationPanel panel;
    
    public void setFoundationPanel(FoundationPanel foundationpanel) {
	panel = foundationpanel;
	foundationpanel.addKeyListener(this);
	foundationpanel.addFocusListener(this);
	foundationpanel.addMouseListener(this);
	foundationpanel.addMouseMotionListener(this);
    }
    
    public void keyTyped(KeyEvent keyevent) {
	ExtendedKeyEvent extendedkeyevent
	    = new ExtendedKeyEvent(keyevent.getWhen(), keyevent.getKeyCode(),
				   keyevent.getKeyChar(),
				   keyevent.getModifiers(), -13);
	panel.addEvent(extendedkeyevent);
    }
    
    public void keyPressed(KeyEvent keyevent) {
	panel.addEvent(new ExtendedKeyEvent(keyevent.getWhen(),
					    keyevent.getKeyCode(),
					    keyevent.getKeyChar(),
					    keyevent.getModifiers(), -11));
    }
    
    public void keyReleased(KeyEvent keyevent) {
	panel.addEvent(new ExtendedKeyEvent(keyevent.getWhen(),
					    keyevent.getKeyCode(),
					    keyevent.getKeyChar(),
					    keyevent.getModifiers(), -12));
    }
    
    public void focusGained(FocusEvent focusevent) {
	panel.addEvent(ApplicationEvent.newFocusEvent(true));
    }
    
    public void focusLost(FocusEvent focusevent) {
	panel.addEvent(ApplicationEvent.newFocusEvent(false));
    }
    
    public void mouseClicked(MouseEvent mouseevent) {
	/* empty */
    }
    
    public void mousePressed(MouseEvent mouseevent) {
	panel.requestFocus();
	panel.addEvent(new COM.stagecast.ifc.netscape.application.MouseEvent
		       (mouseevent.getWhen(), -1, mouseevent.getX(),
			mouseevent.getY(), mouseevent.getModifiers()));
	mouseevent.consume();
    }
    
    public void mouseReleased(MouseEvent mouseevent) {
	panel.addEvent(new COM.stagecast.ifc.netscape.application.MouseEvent
		       (mouseevent.getWhen(), -3, mouseevent.getX(),
			mouseevent.getY(), mouseevent.getModifiers()));
	mouseevent.consume();
    }
    
    public void mouseEntered(MouseEvent mouseevent) {
	panel.addEvent(new COM.stagecast.ifc.netscape.application.MouseEvent
		       (mouseevent.getWhen(), -4, mouseevent.getX(),
			mouseevent.getY(), mouseevent.getModifiers()));
	mouseevent.consume();
    }
    
    public void mouseExited(MouseEvent mouseevent) {
	panel.addEvent(new COM.stagecast.ifc.netscape.application.MouseEvent
		       (mouseevent.getWhen(), -6, mouseevent.getX(),
			mouseevent.getY(), mouseevent.getModifiers()));
	mouseevent.consume();
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	panel.addEvent(new COM.stagecast.ifc.netscape.application.MouseEvent
		       (mouseevent.getWhen(), -2, mouseevent.getX(),
			mouseevent.getY(), mouseevent.getModifiers()));
	mouseevent.consume();
    }
    
    public void mouseMoved(MouseEvent mouseevent) {
	panel.addEvent(new COM.stagecast.ifc.netscape.application.MouseEvent
		       (mouseevent.getWhen(), -5, mouseevent.getX(),
			mouseevent.getY(), mouseevent.getModifiers()));
	mouseevent.consume();
    }
}
