/* ColorValue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class ColorValue
    implements Named, IconModel, FirstClassValue, Externalizable,
	       ResourceIDs.ColorIDs, StorageProxied
{
    public static ColorValue transparentColor;
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108753118514L;
    static int ICON_WIDTH = 16;
    static int ICON_HEIGHT = 16;
    private Color _color;
    private String _name;
    private transient Image _image;
    private transient ViewManager _vmgr;
    
    static void initStatics() {
	transparentColor
	    = new ColorValue(new Color(16777215), "TransparentCID");
	BuiltinProxyTable.helper.registerProxy("TransparentCID",
					       transparentColor);
    }
    
    public ColorValue(Color color, String nameID) {
	this();
	_color = color;
	_name = Resource.getText(nameID);
	setIconImage(Util.createFilledBitmap(ICON_WIDTH, ICON_HEIGHT, _color));
    }
    
    public ColorValue() {
	/* empty */
    }
    
    public Color getColor() {
	return _color;
    }
    
    public String getName() {
	return _name;
    }
    
    public void setName(String name) {
	_name = name;
    }
    
    public StorageProxyHelper getStorageProxyHelper() {
	return BuiltinProxyTable.helper;
    }
    
    public boolean equals(Object other) {
	return (other instanceof ColorValue
		&& getColor().equals(((ColorValue) other).getColor()));
    }
    
    public Image getIconImage() {
	return _image;
    }
    
    public Rect getIconImageRect() {
	return new Rect(0, 0, _image.width(), _image.height());
    }
    
    public void setIconImage(Image image) {
	_image = image;
	Icon.updateIconImages(this);
    }
    
    public String getIconName() {
	return _name;
    }
    
    public void setIconName(String newName) {
	_name = newName;
    }
    
    public boolean hasIconViews() {
	return _vmgr != null;
    }
    
    public ViewManager getIconViewManager() {
	if (_vmgr == null)
	    _vmgr = new ViewManager(this);
	return _vmgr;
    }
    
    public PlaywriteView createIconView() {
	return new Icon(this);
    }
    
    public PlaywriteView createView() {
	return createIconView();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(_color.rgb());
	out.writeObject(_name);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_color = new Color(in.readInt());
	_name = (String) in.readObject();
	setIconImage(Util.createFilledBitmap(ICON_WIDTH, ICON_HEIGHT, _color));
    }
    
    public String toString() {
	String result = null;
	try {
	    result = ("<ColorValue R:" + _color.red() + " G:" + _color.green()
		      + " B:" + _color.blue() + ">");
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
