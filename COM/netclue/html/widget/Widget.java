/* Widget - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.widget;
import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Widget extends Component
{
    private String actCommand;
    private boolean hasBorder = true;
    private boolean isOpaque = false;
    transient ActionListener actionListener;
    
    public Widget() {
	actCommand = null;
    }
    
    public void setBorder(boolean bool) {
	hasBorder = bool;
    }
    
    public boolean hasBorder() {
	return hasBorder;
    }
    
    public void setOpaque(boolean bool) {
	isOpaque = bool;
    }
    
    public boolean isOpaque() {
	return isOpaque;
    }
    
    public void setActionCommand(String string) {
	actCommand = string;
    }
    
    public String getActionCommand() {
	return actCommand;
    }
    
    public synchronized void addActionListener(ActionListener actionlistener) {
	actionListener
	    = AWTEventMulticaster.add(actionListener, actionlistener);
    }
    
    public synchronized void removeActionListener
	(ActionListener actionlistener) {
	actionListener
	    = AWTEventMulticaster.remove(actionListener, actionlistener);
    }
    
    protected void processActionEvent(ActionEvent actionevent) {
	if (actionListener != null)
	    actionListener.actionPerformed(actionevent);
    }
    
    protected void processEvent(AWTEvent awtevent) {
	if (awtevent instanceof ActionEvent)
	    processActionEvent((ActionEvent) awtevent);
	else
	    super.processEvent(awtevent);
    }
}
