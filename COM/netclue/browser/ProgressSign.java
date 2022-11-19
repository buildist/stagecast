/* ProgressSign - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.browser;
import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProgressSign extends Component implements Runnable
{
    private Thread thread;
    private int sleepAmount = 250;
    private int rad;
    private int numFrames;
    private Dimension panelDim;
    private boolean growBig = true;
    private Image[] signImg = new Image[4];
    transient ActionListener actionListener;
    
    public ProgressSign() {
	panelDim = new Dimension(28, 28);
	this.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent mouseevent) {
		ActionEvent actionevent
		    = new ActionEvent(ProgressSign.this, 1001, "stop");
		processActionEvent(actionevent);
		stop();
	    }
	});
    }
    
    public void setImages(Image[] images) {
	signImg = images;
	numFrames = signImg.length - 1;
	panelDim.width = images[0].getWidth(this);
	panelDim.height = images[0].getHeight(this);
    }
    
    public void setPace(int i) {
	sleepAmount = i;
    }
    
    public void start() {
	if (thread == null) {
	    thread = new Thread(this);
	    thread.start();
	}
    }
    
    public Dimension getPreferredSize() {
	return panelDim;
    }
    
    public Dimension getMinimumSize() {
	return panelDim;
    }
    
    public synchronized void stop() {
	if (thread != null)
	    thread.stop();
	thread = null;
	rad = 0;
	growBig = true;
	this.notifyAll();
	this.repaint();
    }
    
    public void run() {
	Thread thread = Thread.currentThread();
	while (this.getSize().width == 0) {
	    try {
		Thread.sleep((long) sleepAmount);
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	}
	while (this.thread == thread) {
	    this.repaint(0L);
	    try {
		Thread.sleep((long) sleepAmount);
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	}
	this.thread = null;
    }
    
    public void paint(Graphics graphics) {
	graphics.drawImage(signImg[rad], 0, 0, this);
	if (thread != null) {
	    if (growBig) {
		if (++rad > numFrames) {
		    rad = numFrames - 1;
		    growBig = false;
		}
	    } else if (--rad < 0) {
		rad = 1;
		growBig = true;
	    }
	}
    }
    
    public void update(Graphics graphics) {
	/* empty */
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
