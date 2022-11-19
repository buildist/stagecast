/* ColorWell - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class ColorWell extends DragWell implements DragDestination, Target
{
    Target target;
    String command;
    int origX;
    int origY;
    public static final String SHOW_COLOR_CHOOSER = "showColorChooser";
    
    public ColorWell() {
	this(0, 0, 0, 0);
    }
    
    public ColorWell(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public ColorWell(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	setColor(Color.blue);
	_setupKeyboard();
    }
    
    public void setColor(Color color) {
	if (color != null) {
	    setData(color);
	    this.draw();
	}
    }
    
    public Color color() {
	return (Color) this.data();
    }
    
    public void setData(Object object) {
	if (!(object instanceof Color))
	    throw new InconsistencyException
		      ("ColorWells can only contain colors");
	super.setData(object);
    }
    
    public Image image() {
	Bitmap bitmap = new Bitmap(12, 12);
	Graphics graphics = new Graphics(bitmap);
	graphics.setColor(Color.black);
	graphics.drawRect(0, 0, 12, 12);
	graphics.setColor((Color) data);
	graphics.fillRect(1, 1, 10, 10);
	graphics.dispose();
	bitmap.setTransparent(false);
	return bitmap;
    }
    
    public void setImage(Image image) {
	throw new InconsistencyException("Can't set image on ColorWell");
    }
    
    public void setDataType(String string) {
	throw new InconsistencyException("Can't set data type on ColorWell");
    }
    
    public String dataType() {
	return "COM.stagecast.ifc.netscape.application.Color";
    }
    
    public void setTarget(Target target) {
	this.target = target;
    }
    
    public Target target() {
	return target;
    }
    
    public void setCommand(String string) {
	command = string;
    }
    
    public String command() {
	return command;
    }
    
    public void drawView(Graphics graphics) {
	graphics.setColor(color());
	graphics.fillRect(border.leftMargin(), border.topMargin(),
			  bounds.width - border.widthMargin(),
			  bounds.height - border.heightMargin());
	border.drawInRect(graphics, 0, 0, bounds.width, bounds.height);
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (!this.isEnabled())
	    return false;
	origX = mouseevent.x;
	origY = mouseevent.y;
	return true;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	if (Math.abs(mouseevent.x - origX) > 3
	    || Math.abs(mouseevent.y - origY) > 3)
	    new DragSession(this, image(), mouseevent.x - 6, mouseevent.y - 6,
			    mouseevent.x, mouseevent.y, dataType(), data);
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	ColorChooser colorchooser = this.rootView().colorChooser();
	colorchooser.setColor(color());
	this.rootView().showColorChooser();
    }
    
    public DragDestination acceptsDrag(DragSession dragsession, int i,
				       int i_3_) {
	if ("COM.stagecast.ifc.netscape.application.Color"
		.equals(dragsession.dataType()))
	    return this;
	return null;
    }
    
    public boolean dragEntered(DragSession dragsession) {
	return true;
    }
    
    public boolean dragMoved(DragSession dragsession) {
	return true;
    }
    
    public void dragExited(DragSession dragsession) {
	/* empty */
    }
    
    public boolean dragDropped(DragSession dragsession) {
	if (!this.isEnabled() || dragsession.source() == this)
	    return false;
	Object object = dragsession.data();
	if (object == null || !(object instanceof Color))
	    return false;
	setColor((Color) object);
	sendCommand();
	return true;
    }
    
    public void sendCommand() {
	if (target != null)
	    target.performCommand(command, this);
    }
    
    public void performCommand(String string, Object object) {
	if ("showColorChooser".equals(string) && this.rootView() != null)
	    this.rootView().showColorChooser();
    }
    
    void _setupKeyboard() {
	this.removeAllCommandsForKeys();
	this.setCommandForKey("showColorChooser", 10, 0);
    }
    
    public boolean canBecomeSelectedView() {
	return true;
    }
}
