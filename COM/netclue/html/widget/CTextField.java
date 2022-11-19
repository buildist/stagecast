/* CTextField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.widget;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CTextField extends Widget implements Runnable
{
    private int cols;
    private String data;
    private boolean hasFocus = false;
    private boolean isCaretVisible = false;
    private Thread caretThread;
    
    public CTextField() {
	this(null, -1);
    }
    
    public CTextField(String string, int i) {
	data = string;
	cols = i;
	this.setOpaque(true);
	this.setCursor(Cursor.getPredefinedCursor(2));
	installKeyListener();
	installMouseListener();
	installFocusListener();
    }
    
    public void selectAll() {
	/* empty */
    }
    
    public void setEnabled(boolean bool) {
	super.setEnabled(bool);
	if (bool) {
	    caretThread = new Thread(this);
	    caretThread.start();
	} else if (caretThread != null) {
	    caretThread.stop();
	    caretThread = null;
	}
    }
    
    public void setFocus(boolean bool) {
	hasFocus = bool;
	if (bool) {
	    caretThread = new Thread(this);
	    caretThread.start();
	} else if (caretThread != null) {
	    caretThread.stop();
	    caretThread = null;
	    isCaretVisible = false;
	    this.repaint();
	}
    }
    
    public boolean isFocusTraversable() {
	return true;
    }
    
    public String getActionCommand() {
	String string = super.getActionCommand();
	if (string == null)
	    return data;
	return string;
    }
    
    public String getText() {
	return data;
    }
    
    public void setText(String string) {
	data = string;
	this.repaint(0L);
    }
    
    public Dimension getPreferredSize() {
	Dimension dimension = new Dimension(10, 10);
	FontMetrics fontmetrics = this.getFontMetrics(this.getFont());
	if (cols > 0)
	    dimension.width = fontmetrics.charWidth('O') * cols + 8 + 2;
	else if (data != null)
	    dimension.width = fontmetrics.stringWidth(data) + 8 + 2;
	dimension.height = fontmetrics.getHeight() + 4 + 2 + 2;
	return dimension;
    }
    
    public void run() {
	for (;;) {
	    if (Thread.currentThread() == caretThread) {
		Graphics graphics = this.getGraphics();
		if (graphics != null) {
		    isCaretVisible = !isCaretVisible;
		    FontMetrics fontmetrics = graphics.getFontMetrics();
		    int i
			= data == null ? 5 : fontmetrics.stringWidth(data) + 5;
		    this.repaint(i, 2, 5, fontmetrics.getHeight() + 4);
		    graphics.dispose();
		}
		try {
		    Thread.sleep(600L);
		} catch (Exception exception) {
		    /* empty */
		}
	    }
	}
    }
    
    private void drawLabel(Graphics graphics, Dimension dimension) {
	graphics.setFont(this.getFont());
	FontMetrics fontmetrics = graphics.getFontMetrics();
	int i = ((dimension.height - fontmetrics.getHeight() >> 1)
		 + fontmetrics.getAscent());
	graphics.drawString(data, 5, i);
    }
    
    private void drawCaret(Graphics graphics) {
	FontMetrics fontmetrics = graphics.getFontMetrics();
	int i = 5;
	if (data != null)
	    i += fontmetrics.stringWidth(data);
	graphics.drawRect(i, 4, 1, fontmetrics.getHeight());
    }
    
    public void paint(Graphics graphics) {
	Color color = this.getForeground();
	Dimension dimension = this.getSize();
	if (this.isOpaque()) {
	    graphics.setColor(Color.white);
	    graphics.fillRect(0, 0, dimension.width, dimension.height);
	}
	graphics.setColor(color);
	if (data != null)
	    drawLabel(graphics, dimension);
	if (isCaretVisible)
	    drawCaret(graphics);
	drawBorder(graphics, dimension);
    }
    
    private void drawBorder(Graphics graphics, Dimension dimension) {
	graphics.setColor(Color.gray);
	dimension.width--;
	dimension.height--;
	for (int i = 0; i < 2; i++)
	    graphics.drawRect(i, i, dimension.width - i - i - 1,
			      dimension.height - i - i - 1);
	graphics.setColor(Color.darkGray);
	graphics.drawLine(1, 1, dimension.width - 2, 1);
	graphics.drawLine(1, 1, 1, dimension.height - 2);
	graphics.setColor(Color.lightGray);
	graphics.drawLine(0, dimension.height - 1, dimension.width - 1,
			  dimension.height - 1);
	graphics.drawLine(dimension.width - 1, 0, dimension.width - 1,
			  dimension.height - 1);
    }
    
    private void installFocusListener() {
	this.addFocusListener(new FocusListener() {
	    public void focusGained(FocusEvent focusevent) {
		setFocus(true);
	    }
	    
	    public void focusLost(FocusEvent focusevent) {
		setFocus(false);
	    }
	});
    }
    
    private void installMouseListener() {
	this.addMouseListener(new MouseAdapter() {
	    public void mousePressed(MouseEvent mouseevent) {
		if (CTextField.this.isEnabled())
		    CTextField.this.requestFocus();
	    }
	});
    }
    
    private void installKeyListener() {
	this.addKeyListener(new KeyListener() {
	    public void keyReleased(KeyEvent keyevent) {
		if (CTextField.this.isEnabled()) {
		    int i = keyevent.getKeyCode();
		    if (i == 10) {
			ActionEvent actionevent
			    = new ActionEvent(CTextField.this, 1001,
					      getActionCommand());
			CTextField.this.processActionEvent(actionevent);
			CTextField.this.transferFocus();
		    } else if (i == 8) {
			CTextField ctextfield_2_ = CTextField.this;
			if (ctextfield_2_.data != null) {
			    ctextfield_2_ = CTextField.this;
			    int i_3_ = ctextfield_2_.data.length() - 1;
			    if (i_3_ >= 0) {
				CTextField ctextfield_4_ = CTextField.this;
				CTextField ctextfield_5_ = CTextField.this;
				String string
				    = ctextfield_5_.data.substring(0, i_3_);
				ctextfield_4_.data = string;
				CTextField.this.repaint();
			    }
			}
		    }
		}
	    }
	    
	    public void keyTyped(KeyEvent keyevent) {
		if (CTextField.this.isEnabled() && !keyevent.isActionKey()) {
		    char c = keyevent.getKeyChar();
		    if (!Character.isISOControl(c)) {
			CTextField ctextfield_6_ = CTextField.this;
			if (ctextfield_6_.data == null) {
			    ctextfield_6_ = CTextField.this;
			    String string = String.valueOf(c);
			    ctextfield_6_.data = string;
			} else
			    data += c;
			CTextField.this.repaint();
		    }
		}
	    }
	    
	    public void keyPressed(KeyEvent keyevent) {
		/* empty */
	    }
	});
    }
}
