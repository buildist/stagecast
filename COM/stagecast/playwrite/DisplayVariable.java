/* DisplayVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class DisplayVariable
    implements Appearance.DisplayItem, ResourceIDs.VariableIDs, Externalizable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108704621874L;
    private Variable variable;
    private int centerX;
    private int baseY;
    private Font font;
    private Color color;
    private int justification = 0;
    private transient String sysvarID = null;
    private transient Boolean _isGlobal = null;
    
    public final Variable getVariable() {
	return variable;
    }
    
    final void setVariable(Variable v) {
	variable = v;
    }
    
    public final int getX() {
	return centerX;
    }
    
    final void setX(int i) {
	centerX = i;
    }
    
    public final int getBaseLineY() {
	return baseY;
    }
    
    final void setBaseLineY(int i) {
	baseY = i;
    }
    
    public final int getTopY() {
	return baseY - font.fontMetrics().ascent();
    }
    
    final void setTopY(int i) {
	baseY = i + font.fontMetrics().ascent();
    }
    
    public final int getJustification() {
	return justification;
    }
    
    final void setJustification(int j) {
	justification = j;
    }
    
    public final Color getColor() {
	return color;
    }
    
    final void setColor(Color c) {
	color = c;
    }
    
    public final Font getFont() {
	return font;
    }
    
    final void setFont(Font f) {
	font = f;
    }
    
    DisplayVariable(Variable v, int centerX, int baseLineY, int j, Font f,
		    Color c) {
	setVariable(v);
	setX(centerX);
	setFont(f);
	setColor(c);
	setJustification(j);
	setBaseLineY(baseLineY);
    }
    
    DisplayVariable(Variable v, int centerX, int topY, Font f, Color c) {
	setVariable(v);
	setX(centerX);
	setFont(f);
	setColor(c);
	setTopY(topY);
    }
    
    DisplayVariable(Variable v, DisplayVariable dv) {
	this(v, dv.getX(), dv.getBaseLineY(), dv.getJustification(),
	     dv.getFont(), dv.getColor());
    }
    
    public DisplayVariable() {
	/* empty */
    }
    
    public boolean isGlobal(CocoaCharacter ch) {
	if (_isGlobal == null)
	    _isGlobal
		= new Boolean(ch.getPrototype().contains(variable) ^ true);
	return _isGlobal.booleanValue();
    }
    
    public void draw(int squareSize, int targetSquareSize, int appearanceX,
		     int appearanceY, CocoaCharacter ch, Graphics g) {
	if (squareSize != 0) {
	    Object value
		= variable.getValue(isGlobal(ch)
				    ? (VariableOwner) ch.getWorld() : ch);
	    String s;
	    if (value == null)
		s = Resource.getText("NoValue ID");
	    else if (value instanceof Number)
		s = Resource.formatNumber((Number) value);
	    else
		s = value.toString();
	    int strX = appearanceX + centerX * targetSquareSize / squareSize;
	    int strY = appearanceY + baseY * targetSquareSize / squareSize;
	    Util.drawString(g, s, strX, strY, justification, font, color);
	}
    }
    
    void fixIfVersionOne(Appearance appearance) {
	if (variable == null)
	    variable
		= Variable.xlateV1Variable(sysvarID, appearance.getOwner());
    }
    
    public void wasAddedTo(Appearance appearance) {
	fixIfVersionOne(appearance);
	ASSERT.isTrue(variable.getListOwner() == appearance.getOwner()
		      || (variable.getListOwner()
			  == appearance.getOwner().getWorld()));
    }
    
    public void wasRemovedFrom(Appearance appearance) {
	/* empty */
    }
    
    public void displayedOn(CocoaCharacter cocoaCharacter) {
	if (cocoaCharacter instanceof CharacterInstance) {
	    VariableOwner target = (isGlobal(cocoaCharacter)
				    ? (VariableOwner) cocoaCharacter.getWorld()
				    : cocoaCharacter);
	    variable.addValueWatcher(target,
				     cocoaCharacter.getAppearanceUpdater());
	}
    }
    
    public void undisplayedOn(CocoaCharacter cocoaCharacter) {
	if (cocoaCharacter instanceof CharacterInstance) {
	    VariableOwner target = (isGlobal(cocoaCharacter)
				    ? (VariableOwner) cocoaCharacter.getWorld()
				    : cocoaCharacter);
	    variable.removeValueWatcher(target,
					cocoaCharacter.getAppearanceUpdater());
	}
    }
    
    public boolean usesVariable(Variable v) {
	return v == variable;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(variable);
	out.writeObject(variable);
	out.writeInt(centerX);
	out.writeInt(baseY);
	out.writeUTF(font.toString());
	out.writeInt(color.red());
	out.writeInt(color.green());
	out.writeInt(color.blue());
	out.writeInt(justification);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	Object v = in.readObject();
	if (v instanceof Variable)
	    variable = (Variable) v;
	else {
	    ASSERT.isClass(v, String.class);
	    variable = null;
	    sysvarID = (String) v;
	}
	centerX = in.readInt();
	baseY = in.readInt();
	font = Font.fontNamed(in.readUTF());
	int r = in.readInt();
	int g = in.readInt();
	int b = in.readInt();
	color = new Color(r, g, b);
	justification = in.readInt();
	setTopY(getTopY());
    }
}
