/* BackgroundVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class BackgroundVariable extends PopupVariable
    implements ResourceIDs.VariableIDs
{
    private BackgroundImage loadBackground
	= new BackgroundImage(null, Resource.getText("GetBackgID"),
			      Util.createFilledBitmap(16, 16, Color.white));
    private BackgroundImage loadAllBackgrounds
	= new BackgroundImage(null, Resource.getText("GetAllBgsID"),
			      Util.createFilledBitmap(16, 16, Color.cyan));
    
    protected BackgroundVariable(String sysvarID, String name) {
	super(sysvarID, name, (Vector) null);
	this.setDefaultValue(BackgroundImage.noBackground);
    }
    
    public Enumeration legalValues(VariableOwner owner) {
	Vector values = new Vector(10);
	Enumeration e = owner.getWorld().getBackgrounds().getContents();
	while (e.hasMoreElements())
	    values.addElement(e.nextElement());
	values.addElement(BackgroundImage.noBackground);
	values.addElement(loadBackground);
	return values.elements();
    }
    
    public Enumeration alternateValues(VariableOwner owner) {
	Vector values = new Vector(1);
	if (PlaywriteRoot.isProfessional())
	    values.addElement(loadAllBackgrounds);
	return values.elements();
    }
    
    public Object getLegalValue(VariableOwner owner, Object value) {
	if (value == Variable.UNBOUND)
	    return value;
	if (!(value instanceof BackgroundImage))
	    return super.getLegalValue(owner, value);
	if (value == loadBackground) {
	    BackgroundImage bg
		= new BackgroundImage(owner.getWorld().getBackgrounds(), "",
				      null);
	    Bitmap bmp = ImageIO.importBackground(owner.getWorld(), bg);
	    bg.setImage(bmp);
	    if (bmp == null) {
		value = null;
		owner.getWorld().getBackgrounds().remove(bg);
	    } else
		value = bg;
	} else if (value == loadAllBackgrounds) {
	    String picked
		= BackgroundImage.importAllBackgrounds(owner.getWorld());
	    XYContainer allBackgrounds = owner.getWorld().getBackgrounds();
	    if (picked == null || allBackgrounds.size() == 0)
		value = null;
	    else {
		value = allBackgrounds.itemNamed(Util.getFilePart(picked));
		if (value == null)
		    value = allBackgrounds.getContents().nextElement();
	    }
	}
	return value;
    }
}
