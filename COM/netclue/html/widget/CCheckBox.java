/* CCheckBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.widget;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CCheckBox extends Widget
{
    private boolean isChecked;
    private String label;
    private RadioGroup radioGroup;
    
    public CCheckBox() {
	this(null, false);
    }
    
    public CCheckBox(boolean bool) {
	this(null, bool);
    }
    
    public CCheckBox(String string, boolean bool) {
	label = string;
	isChecked = bool;
	installMouseListener();
    }
    
    public void setRadioGroup(RadioGroup radiogroup) {
	radioGroup = radiogroup;
	if (isChecked)
	    radiogroup.setSelectedCheckBox(this);
    }
    
    public RadioGroup getRadioGroup() {
	return radioGroup;
    }
    
    public boolean getState() {
	return isChecked;
    }
    
    public void setState(boolean bool) {
	if (bool != isChecked) {
	    isChecked = bool;
	    this.repaint();
	}
    }
    
    public String getActionCommand() {
	if (null == null)
	    return label;
	return null;
    }
    
    public Dimension getPreferredSize() {
	FontMetrics fontmetrics = this.getFontMetrics(this.getFont());
	int i = fontmetrics.getHeight() + 4;
	Dimension dimension = new Dimension(i + 2, i);
	if (label != null)
	    dimension.width += fontmetrics.stringWidth(label);
	return dimension;
    }
    
    private void drawLabel(Graphics graphics, Dimension dimension) {
	graphics.setFont(this.getFont());
	FontMetrics fontmetrics = graphics.getFontMetrics();
	int i = ((dimension.height - fontmetrics.getHeight() >> 1)
		 + fontmetrics.getAscent());
	graphics.drawString(label, fontmetrics.getHeight() + 6, i);
    }
    
    private void drawIndication(Graphics graphics, Dimension dimension) {
	int i = dimension.height - 7;
	int i_0_ = 3;
	graphics.setColor(Color.gray);
	if (radioGroup == null)
	    graphics.draw3DRect(i_0_, i_0_, i, i, false);
	else
	    graphics.drawOval(i_0_, i_0_, i, i);
	if (isChecked) {
	    graphics.setColor(Color.black);
	    i -= 5;
	    i_0_ += 3;
	    if (radioGroup == null)
		graphics.fillRect(i_0_, i_0_, i, i);
	    else
		graphics.fillOval(i_0_, i_0_, i, i);
	}
    }
    
    public void paint(Graphics graphics) {
	Color color = this.getForeground();
	Dimension dimension = this.getSize();
	graphics.setColor(color);
	if (label != null)
	    drawLabel(graphics, dimension);
	drawIndication(graphics, dimension);
    }
    
    public void updateStatus() {
	isChecked = !isChecked;
	if (radioGroup != null)
	    radioGroup.setSelectedCheckBox(this);
	ActionEvent actionevent
	    = new ActionEvent(this, 1001, getActionCommand());
	this.processActionEvent(actionevent);
	this.repaint();
    }
    
    private void installMouseListener() {
	this.addMouseListener(new MouseAdapter() {
	    public void mousePressed(MouseEvent mouseevent) {
		if (getRadioGroup() == null || !getState())
		    updateStatus();
	    }
	});
    }
}
