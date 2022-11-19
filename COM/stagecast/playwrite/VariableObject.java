/* VariableObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.FontMetrics;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;

public class VariableObject extends PaintFieldObject
{
    protected Font font;
    protected Color color;
    protected String name;
    protected int justification;
    protected Variable variable;
    protected Rect prevRect;
    
    public VariableObject(PaintField p, Variable realVar, Point loc) {
	super(p, new Rect());
	prevRect = new Rect();
	selected = false;
	stretchMode = false;
	font = p.getAppearanceEditor().getFont();
	color = p.getAppearanceEditor().getColor();
	name = realVar.getName();
	justification = 0;
	variable = realVar;
	Size size = calculateSizeForVariable();
	rect = new Rect(loc.x, loc.y, size.width, size.height);
    }
    
    public VariableObject(PaintField p, DisplayVariable realVar) {
	super(p, new Rect());
	prevRect = new Rect();
	selected = false;
	stretchMode = false;
	font = realVar.getFont();
	color = realVar.getColor();
	name = realVar.getVariable().getName();
	justification = realVar.getJustification();
	variable = realVar.getVariable();
	Size size = calculateSizeForVariable();
	int x = getXForAnchorX(realVar.getX(), size.width);
	rect = new Rect(x, realVar.getTopY(), size.width, size.height);
    }
    
    public VariableObject(VariableObject copy) {
	super(copy.paintField, new Rect(copy.rect));
	prevRect = new Rect();
	selected = false;
	stretchMode = false;
	font = copy.font;
	color = copy.color;
	name = copy.name;
	justification = copy.justification;
	variable = copy.variable;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String s) {
	name = s;
    }
    
    public Color getColor() {
	return color;
    }
    
    public void setColor(Color c) {
	color = c;
    }
    
    public Font getFont() {
	return font;
    }
    
    public void setFont(Font f) {
	font = f;
	Size size = calculateSizeForVariable();
	rect = new Rect(rect.x, rect.y, size.width, size.height);
    }
    
    public int getJustification() {
	return justification;
    }
    
    public void setJustification(int j) {
	justification = j;
    }
    
    public Variable getVariable() {
	return variable;
    }
    
    public void setVariable(Variable v) {
	variable = v;
    }
    
    public void mouseDown(int x, int y, boolean ctrlKey, boolean shiftKey,
			  boolean altKey) {
	prevRect.setBounds(rect);
	super.mouseDown(x, y, ctrlKey, shiftKey, altKey);
	paintField.deselect();
	paintField.setSelectedVariable(this);
    }
    
    public void mouseUp(int x, int y, boolean ctrlKey, boolean shiftKey,
			boolean altKey) {
	super.mouseUp(x, y, ctrlKey, shiftKey, altKey);
	prevRect.unionWith(rect);
	paintField.logicalDraw(prevRect, 2);
    }
    
    public int getAnchorX() {
	int anchorX = rect.x;
	if (justification == 1)
	    anchorX += rect.width / 2;
	else if (justification == 2)
	    anchorX += rect.width;
	return anchorX;
    }
    
    private int getXForAnchorX(int anchorX, int width) {
	int x = anchorX;
	if (justification == 1)
	    x -= width / 2;
	else if (justification == 2)
	    x -= width;
	return x;
    }
    
    public Size calculateSizeForVariable() {
	FontMetrics fm = font.fontMetrics();
	return new Size(fm.stringWidth(name) + 6, fm.stringHeight());
    }
    
    public void draw(PaintField p, Graphics g) {
	Rect originalClip = g.clipRect();
	int x = p.logicalToPhysicalX(rect.x);
	int y = p.logicalToPhysicalY(rect.y);
	int width = rect.width * p.getScale();
	int height = rect.height * p.getScale();
	g.setClipRect(new Rect(x, y, width, height));
	g.setColor(color);
	Font displayFont
	    = new Font(font.name(), font.style(), font.size() * p.getScale());
	FontMetrics metrics = displayFont.fontMetrics();
	g.setFont(displayFont);
	g.drawStringInRect(name, x, y, width, height, justification);
	super.draw(p, g);
	g.setClipRect(originalClip, false);
    }
    
    public void delete() {
	paintField.deleteVariable(this);
    }
    
    public void deselect() {
	paintField.setSelectedVariable(null);
    }
}
