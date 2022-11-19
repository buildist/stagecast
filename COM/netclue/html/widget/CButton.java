/* CButton - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.widget;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class CButton extends Widget
{
    private String label;
    private Image img;
    private boolean isButtonPressed = false;
    
    public CButton(String string) {
	label = string;
	this.setOpaque(true);
	installMouseListener();
    }
    
    public CButton(URL url) {
	try {
	    img = Toolkit.getDefaultToolkit().getImage(url);
	    MediaTracker mediatracker = new MediaTracker(this);
	    mediatracker.addImage(img, 0);
	    mediatracker.waitForID(0);
	    installMouseListener();
	} catch (Exception exception) {
	    /* empty */
	}
    }
    
    public String getActionCommand() {
	String string = super.getActionCommand();
	if (string == null)
	    return label;
	return string;
    }
    
    public Dimension getPreferredSize() {
	int i = this.hasBorder() ? 4 : 0;
	Dimension dimension;
	if (img != null)
	    dimension = new Dimension(img.getWidth(this) + i,
				      img.getHeight(this) + i);
	else if (label != null) {
	    FontMetrics fontmetrics = this.getFontMetrics(this.getFont());
	    dimension = new Dimension(fontmetrics.stringWidth(label) + 8 + i,
				      fontmetrics.getHeight() + 4 + i);
	} else
	    dimension = new Dimension(10, 10);
	return dimension;
    }
    
    private void drawLabel(Graphics graphics, Dimension dimension) {
	graphics.setFont(this.getFont());
	FontMetrics fontmetrics = graphics.getFontMetrics();
	int i = fontmetrics.stringWidth(label);
	int i_0_ = dimension.width - i >> 1;
	int i_1_ = ((dimension.height - fontmetrics.getHeight() >> 1)
		    + fontmetrics.getAscent());
	graphics.drawString(label, i_0_, i_1_);
    }
    
    public void paint(Graphics graphics) {
	Color color = this.getForeground();
	Color color_2_ = this.getBackground();
	Dimension dimension = this.getSize();
	if (this.isOpaque()) {
	    graphics.setColor(color_2_);
	    graphics.fillRect(0, 0, dimension.width, dimension.height);
	}
	if (img != null) {
	    if (this.hasBorder())
		graphics.drawImage(img, 1, 1, dimension.width - 4,
				   dimension.height - 4, this);
	    else
		graphics.drawImage(img, 0, 0, dimension.width,
				   dimension.height, this);
	} else if (label != null) {
	    graphics.setColor(color);
	    drawLabel(graphics, dimension);
	}
	if (this.hasBorder())
	    drawBorder(graphics, color_2_);
    }
    
    private void drawBorder(Graphics graphics, Color color) {
	Color color_3_
	    = isButtonPressed ? color.darker() : color.brighter().brighter();
	Color color_4_
	    = isButtonPressed ? color.brighter().brighter() : color.darker();
	Dimension dimension = this.getSize();
	graphics.setColor(color_3_);
	for (int i = 0; i < 1; i++)
	    graphics.drawRect(i, i, dimension.width - i - i,
			      dimension.height - i - i);
	graphics.setColor(color_4_.darker());
	graphics.drawLine(0, dimension.height - 1, dimension.width - 1,
			  dimension.height - 1);
	graphics.drawLine(dimension.width - 1, 0, dimension.width - 1,
			  dimension.height - 1);
	graphics.setColor(color_4_);
	graphics.drawLine(1, dimension.height - 2, dimension.width - 2,
			  dimension.height - 2);
	graphics.drawLine(dimension.width - 2, 1, dimension.width - 2,
			  dimension.height - 2);
    }
    
    private void installMouseListener() {
	this.addMouseListener(new MouseAdapter() {
	    public void mousePressed(MouseEvent mouseevent) {
		if (CButton.this.isEnabled()) {
		    CButton cbutton_5_ = CButton.this;
		    cbutton_5_.isButtonPressed = true;
		    CButton.this.repaint(0L);
		}
	    }
	    
	    public void mouseReleased(MouseEvent mouseevent) {
		if (CButton.this.isEnabled()) {
		    CButton cbutton_6_ = CButton.this;
		    cbutton_6_.isButtonPressed = false;
		    CButton.this.repaint(0L);
		    ActionEvent actionevent
			= new ActionEvent(CButton.this, 1001,
					  getActionCommand());
		    CButton.this.processActionEvent(actionevent);
		}
	    }
	});
    }
}
