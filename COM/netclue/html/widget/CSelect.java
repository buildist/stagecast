/* CSelect - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.widget;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class CSelect extends Widget
{
    private int index;
    private String maxItem;
    private PopupMenu pMenu;
    private Vector itemList = new Vector(16, 32);
    private Vector labelList = new Vector(16, 32);
    
    public CSelect() {
	index = 0;
	maxItem = "";
	pMenu = new PopupMenu();
	pMenu.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent actionevent) {
		CSelect cselect_0_ = CSelect.this;
		int i = cselect_0_.labelList
			    .indexOf(actionevent.getActionCommand());
		if (i >= 0) {
		    CSelect cselect_1_ = CSelect.this;
		    cselect_1_.index = i;
		    ActionEvent actionevent_2_
			= new ActionEvent(CSelect.this, 1001,
					  actionevent.getActionCommand());
		    CSelect.this.processActionEvent(actionevent_2_);
		    CSelect.this.repaint();
		}
	    }
	});
	this.add(pMenu);
	installMouseListener();
    }
    
    public void addItem(String string) {
	if (string.length() > maxItem.length())
	    maxItem = string;
	itemList.addElement(string);
	labelList.addElement(string);
	pMenu.add(string);
    }
    
    public void addItem(String string, String string_4_) {
	if (string_4_.length() > maxItem.length())
	    maxItem = string_4_;
	itemList.addElement(string);
	labelList.addElement(string_4_);
	pMenu.add(string_4_);
    }
    
    public void removeItemAt(int i) {
	itemList.removeElementAt(i);
	labelList.removeElementAt(i);
	pMenu.remove(i);
    }
    
    public int getLength() {
	return itemList.size();
    }
    
    public void select(int i) {
	index = i;
    }
    
    public int getSelectedIndex() {
	return index;
    }
    
    public String getSelectedItem() {
	if (index >= itemList.size())
	    return "";
	return (String) itemList.elementAt(index);
    }
    
    public void setSelectedItem(String string) {
	int i = itemList.indexOf(string);
	if (i != -1) {
	    index = i;
	    this.repaint();
	}
    }
    
    public String getActionCommand() {
	if (itemList.size() == 0)
	    return "";
	return (String) itemList.elementAt(index);
    }
    
    public Dimension getPreferredSize() {
	Dimension dimension = new Dimension(20, 15);
	FontMetrics fontmetrics = this.getFontMetrics(this.getFont());
	dimension.height = fontmetrics.getHeight() + 4 + 4;
	dimension.width
	    = fontmetrics.stringWidth(maxItem) + 8 + 8 + 4 + (dimension.height
							      - 4);
	return dimension;
    }
    
    private void drawLabel(Graphics graphics, Dimension dimension) {
	graphics.setFont(this.getFont());
	FontMetrics fontmetrics = graphics.getFontMetrics();
	int i = ((dimension.height - fontmetrics.getHeight() >> 1)
		 + fontmetrics.getAscent());
	if (index < labelList.size())
	    graphics.drawString((String) labelList.elementAt(index), 6, i);
    }
    
    private void drawIndication(Graphics graphics, Dimension dimension) {
	int i = dimension.height - 4;
	int i_5_ = dimension.width - i - 2;
	graphics.setColor(Color.gray);
	graphics.fill3DRect(i_5_, 2, i, i, true);
	graphics.setColor(Color.black);
	i = (i & 0x1) == 1 ? i : i + 1;
	int[] is = { i_5_ + 4, i_5_ + i / 2 + 1, i_5_ + i - 4 };
	int i_6_ = i / 3;
	int[] is_7_ = { 2 + i_6_, 2 + (i_6_ << 1), 2 + i_6_ };
	graphics.fillPolygon(is, is_7_, 3);
    }
    
    public void paint(Graphics graphics) {
	Color color = this.getForeground();
	Dimension dimension = this.getSize();
	graphics.setColor(this.getBackground());
	graphics.fillRect(0, 0, dimension.width, dimension.height);
	graphics.setColor(color);
	if (itemList.size() > 0)
	    drawLabel(graphics, dimension);
	drawIndication(graphics, dimension);
	drawBorder(graphics, dimension);
    }
    
    private void drawBorder(Graphics graphics, Dimension dimension) {
	graphics.setColor(Color.gray);
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
    
    void showSelection() {
	FontMetrics fontmetrics = this.getFontMetrics(this.getFont());
	pMenu.show(this, 0, fontmetrics.getHeight() + 8);
    }
    
    private void installMouseListener() {
	this.addMouseListener(new MouseAdapter() {
	    public void mousePressed(MouseEvent mouseevent) {
		if (CSelect.this.isEnabled())
		    showSelection();
	    }
	});
    }
}
