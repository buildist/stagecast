/* PlaywriteBorder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.InternalWindowBorder;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class PlaywriteBorder extends InternalWindowBorder
    implements ResourceIDs.LookAndFeelIDs
{
    static final int RESIZE_AREA = 16;
    static Bitmap TOP_BORDER;
    static Bitmap BOTTOM_BORDER;
    static Bitmap LEFT_BORDER;
    static Bitmap RIGHT_BORDER;
    static Bitmap LOWER_LEFT_CORNER;
    static Bitmap LOWER_RIGHT_CORNER;
    static Bitmap TOP_LEFT_CORNER;
    static Bitmap TOP_RIGHT_CORNER;
    private int controlMargin = 0;
    private Color textColor = Color.black;
    private Color titleColor = null;
    private Color borderColor = null;
    private Font titleFont = null;
    private PlaywriteView titleBar = null;
    private Rect box = new Rect(0, 0, 0, 0);
    private Color resizeColor = Util.defaultDarkColor;
    
    static void initStatics() {
	TOP_BORDER = Resource.getImage("view top");
	BOTTOM_BORDER = Resource.getImage("view bot");
	LEFT_BORDER = Resource.getImage("view lft");
	RIGHT_BORDER = Resource.getImage("view rt");
	LOWER_LEFT_CORNER = Resource.getImage("view ll corner");
	LOWER_RIGHT_CORNER = Resource.getImage("view lr corner");
	TOP_LEFT_CORNER = Resource.getImage("view tl corner");
	TOP_RIGHT_CORNER = Resource.getImage("view tr corner");
    }
    
    PlaywriteBorder(PlaywriteWindow window) {
	super((COM.stagecast.ifc.netscape.application.InternalWindow) window);
	setResizeColor(window.getWorld().getDarkColor());
    }
    
    PlaywriteView getTitleBar() {
	return ((PlaywriteWindow) this.window()).getTitleBar();
    }
    
    public int bottomMargin() {
	return LOWER_RIGHT_CORNER.height();
    }
    
    public int leftMargin() {
	return LEFT_BORDER.width();
    }
    
    public int rightMargin() {
	return RIGHT_BORDER.width();
    }
    
    public int topMargin() {
	return getTitleBar().height() + TOP_BORDER.height();
    }
    
    public int resizePartWidth() {
	if (this.window().isResizable())
	    return LOWER_RIGHT_CORNER.width();
	return 0;
    }
    
    public void drawInRect(Graphics g, int x, int y, int width, int height) {
	super.drawInRect(g, x, y, width, height);
	drawCorners(g, x, y, width, height);
    }
    
    public void drawTitleBar(Graphics g, int x, int y, int width, int height) {
	Rect clipRect = g.clipRect();
	if (clipRect.intersects(x, y, width, height)) {
	    int ourHeight = TOP_BORDER.height();
	    int ourWidth = TOP_BORDER.width();
	    for (x = x; x < width; x += ourWidth) {
		if (clipRect.intersects(x, y, ourWidth, ourHeight))
		    TOP_BORDER.drawAt(g, x, y);
	    }
	}
    }
    
    public void drawBottomBorder(Graphics g, int x, int y, int width,
				 int height) {
	Rect clipRect = g.clipRect();
	int ourX = 0;
	int ourY = height - bottomMargin();
	int ourWidth = width;
	int ourHeight = bottomMargin();
	if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight)) {
	    g.setColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	    g.fillRect(ourX, ourY, ourWidth, ourHeight);
	    ourY = height - BOTTOM_BORDER.height();
	    ourWidth = BOTTOM_BORDER.width();
	    for (ourX = 0; ourX < width; ourX += ourWidth) {
		if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight))
		    BOTTOM_BORDER.drawAt(g, ourX, ourY);
	    }
	}
    }
    
    public void drawLeftBorder(Graphics g, int x, int y, int width,
			       int height) {
	Rect clipRect = g.clipRect();
	int ourX = x;
	int ourY = y + TOP_BORDER.height();
	int ourWidth = leftMargin();
	int ourHeight = height;
	if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight)) {
	    g.setColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	    g.fillRect(ourX, ourY, ourWidth, ourHeight);
	    ourHeight = LEFT_BORDER.height();
	    for (ourY = ourY; ourY < height; ourY += ourHeight) {
		if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight))
		    LEFT_BORDER.drawAt(g, ourX, ourY);
	    }
	}
    }
    
    public void drawRightBorder(Graphics g, int x, int y, int width,
				int height) {
	Rect clipRect = g.clipRect();
	int ourX = width - rightMargin();
	int ourY = 0;
	ourX = x + width - rightMargin();
	ourY = y + TOP_BORDER.height();
	int ourWidth = rightMargin();
	int ourHeight = height;
	if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight)) {
	    g.setColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	    g.fillRect(ourX, ourY, ourWidth, ourHeight);
	    ourHeight = RIGHT_BORDER.height();
	    for (ourY = ourY; ourY < height; ourY += ourHeight) {
		if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight))
		    RIGHT_BORDER.drawAt(g, ourX, ourY);
	    }
	}
    }
    
    void drawResizeRegion(Graphics g, int x, int y, int width, int height) {
	if (this.window().isResizable()) {
	    box.setBounds(0, height - 16, rightMargin(), 16);
	    g.setColor(resizeColor);
	    g.fillRect(box);
	    box.setBounds(0, height - bottomMargin(), 16, bottomMargin());
	    g.fillRect(box);
	    box.setBounds(width - leftMargin(), height - 16, leftMargin(), 16);
	    g.fillRect(box);
	    box.setBounds(width - 16, height - bottomMargin(), 16,
			  bottomMargin());
	    g.fillRect(box);
	}
    }
    
    void drawCorners(Graphics g, int x, int y, int width, int height) {
	if (this.window().isResizable()) {
	    LOWER_LEFT_CORNER.drawAt(g, 0,
				     height - LOWER_LEFT_CORNER.height());
	    LOWER_RIGHT_CORNER.drawAt(g, width - LOWER_RIGHT_CORNER.width(),
				      height - LOWER_RIGHT_CORNER.height());
	    TOP_LEFT_CORNER.drawAt(g, 0, 0);
	    TOP_RIGHT_CORNER.drawAt(g, width - TOP_RIGHT_CORNER.width(), 0);
	}
    }
    
    void setResizeColor(Color color) {
	resizeColor = color;
    }
}
