/* PlaywriteButton - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Button;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;

public class PlaywriteButton extends Button
    implements Flashable, ToolTipable, ViewGlue
{
    protected static final Bitmap DISABLER_BITMAP
	= GrayLayer.grayTransparentBitmap;
    private boolean _tutorialEnabled = true;
    private boolean _worldEnabled = true;
    private boolean flashing = false;
    private boolean hilited = false;
    private String toolTipText = null;
    private boolean _wantsAutoScrollEvents = false;
    private MouseEvent _lastMouseDraggedEvent;
    private int _lastModifiers;
    private Color _overlayColor;
    
    public static PlaywriteButton createPWPushButton(int x, int y, int width,
						     int height) {
	PlaywriteButton pushButton = new PlaywriteButton(x, y, width, height);
	pushButton.setType(0);
	return pushButton;
    }
    
    public static PlaywriteButton createTextButton(String id, String command,
						   Target target) {
	PlaywriteButton button = createPWPushButton(0, 0, 14, 14);
	button.setTitle(Resource.getText(id));
	button.setCommand(command);
	button.setTarget(target);
	return button;
    }
    
    public static PlaywriteButton createButton(Image buttonImage,
					       String command, Target target) {
	PlaywriteButton button = createPWPushButton(0, 0, buttonImage.width(),
						    buttonImage.height());
	button.setImage(buttonImage);
	button.setCommand(command);
	button.setTarget(target);
	return button;
    }
    
    public static PlaywriteButton createButton(Image image, Image downImage,
					       String command, Target target) {
	PlaywriteButton button = new PlaywriteButton(image);
	if (downImage != null)
	    button.setAltImage(downImage);
	button.setCommand(command);
	button.setTarget(target);
	button.setFont(Util.microFont);
	button.setTitleColor(Util.microFontColor);
	button.setBordered(false);
	button.setTransparent(true);
	return button;
    }
    
    public static PlaywriteButton createFromResource(String resourceID,
						     boolean hasToolTip) {
	PlaywriteButton button
	    = new PlaywriteButton(Resource.getButtonImage(resourceID),
				  Resource.getAltButtonImage(resourceID));
	if (hasToolTip)
	    button.setToolTipText(Resource.getToolTip(resourceID));
	button.sizeToMinSize();
	button.setType(0);
	button.setBordered(false);
	button.setRaisedColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	button.setLoweredColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	return button;
    }
    
    public static PlaywriteButton createFromResource
	(String resourceID, String command, Target target) {
	return createFromResource(resourceID, command, target, true);
    }
    
    public static PlaywriteButton createFromResource
	(String resourceID, String command, Target target,
	 boolean hasToolTip) {
	PlaywriteButton result = createFromResource(resourceID, hasToolTip);
	result.setCommand(command);
	result.setTarget(target);
	return result;
    }
    
    public PlaywriteButton(int x, int y, int width, int height) {
	super(x, y, width, height);
	this.setFont(Util.buttonFont);
	this.setTitleColor(Util.buttonFontColor);
    }
    
    public PlaywriteButton(Image buttonImage) {
	this(0, 0, buttonImage.width(), buttonImage.height());
	this.setImage(buttonImage);
    }
    
    public PlaywriteButton(Image imageUp, Image imageDown) {
	this(imageUp);
	this.setAltImage(imageDown);
    }
    
    public void setEnabled(boolean enabled) {
	_worldEnabled = enabled;
	super.setEnabled(_worldEnabled && _tutorialEnabled);
    }
    
    public void setTutorialDisabled(boolean disabled) {
	_tutorialEnabled = disabled ^ true;
	super.setEnabled(_worldEnabled && _tutorialEnabled);
    }
    
    public void setWantsAutoscrollEvents(boolean wantsEm) {
	_wantsAutoScrollEvents = wantsEm;
    }
    
    public boolean wantsAutoscrollEvents() {
	return _wantsAutoScrollEvents;
    }
    
    public MouseEvent getLastMouseDraggedEvent() {
	return _lastMouseDraggedEvent;
    }
    
    public int getLastModifiers() {
	return _lastModifiers;
    }
    
    public final void willBecomeSelected() {
	/* empty */
    }
    
    public final void willBecomeUnselected() {
	/* empty */
    }
    
    public boolean mouseDown(MouseEvent event) {
	_lastModifiers = event.modifiers();
	if (isFlashing())
	    stopFlashing();
	ToolTips.notifyMouseDown();
	return super.mouseDown(event);
    }
    
    public void mouseDragged(MouseEvent mouseEvent) {
	super.mouseDragged(mouseEvent);
	_lastMouseDraggedEvent = null;
	_lastMouseDraggedEvent = (MouseEvent) mouseEvent.clone();
	if (_wantsAutoScrollEvents)
	    this.sendCommand();
    }
    
    public void mouseUp(MouseEvent mouseEvent) {
	_lastMouseDraggedEvent = null;
	super.mouseUp(mouseEvent);
	this.setState(this.state() ^ true);
	this.setState(this.state() ^ true);
    }
    
    public void drawView(Graphics g) {
	super.drawView(g);
	if (_overlayColor != null) {
	    g.setColor(_overlayColor);
	    g.fillRect(g.clipRect());
	} else {
	    if (!this.isEnabled()) {
		int h = DISABLER_BITMAP.height();
		int w = DISABLER_BITMAP.width();
		for (int x = 0; x < this.width(); x += w) {
		    for (int y = 0; y < this.height(); y += h)
			DISABLER_BITMAP.drawAt(g, x, y);
		}
	    }
	    if (isHilited())
		drawHilite(g);
	}
    }
    
    public void setColor(Color color) {
	this.setRaisedColor(color);
	this.setLoweredColor(color.darkerColor());
	this.setRaisedBorder(new BezelBorder(0, color));
	this.setLoweredBorder(new BezelBorder(1, color));
    }
    
    public void drawHilite(Graphics g) {
	g.setColor(Util.HIGHLIGHT_COLOR);
	g.drawRect(0, 0, this.width(), this.height());
	g.drawRect(1, 1, this.width() - 2, this.height() - 2);
    }
    
    public ToolDestination acceptsTool(ToolSession session, int x, int y) {
	return null;
    }
    
    public Object getModelObject() {
	return this;
    }
    
    public void discard() {
	this.setTarget(null);
    }
    
    public View view() {
	return this;
    }
    
    public void setOverlayColor(Color color) {
	_overlayColor = color;
    }
    
    public void setToolTipText(String s) {
	toolTipText = s;
    }
    
    public String getToolTipText() {
	return toolTipText;
    }
    
    public void mouseEntered(MouseEvent e) {
	ToolTips.notifyEntered(this);
	super.mouseEntered(e);
    }
    
    public void mouseExited(MouseEvent e) {
	ToolTips.notifyExited(this);
	super.mouseExited(e);
    }
    
    public void hilite() {
	hilited = true;
	this.setDirty(true);
    }
    
    public void unhilite() {
	hilited = false;
	this.setDirty(true);
    }
    
    public boolean isHilited() {
	return hilited;
    }
    
    public void startFlashing() {
	flashing = true;
    }
    
    public void stopFlashing() {
	flashing = false;
	if (isHilited())
	    unhilite();
    }
    
    public boolean isFlashing() {
	return flashing;
    }
}
