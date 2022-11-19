/* AWTCompatibility - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.applet.Applet;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;

public class AWTCompatibility
{
    private AWTCompatibility() {
	/* empty */
    }
    
    public static Bitmap bitmapForAWTImage(java.awt.Image image) {
	return new Bitmap(image);
    }
    
    public static Bitmap bitmapForAWTImageProducer
	(ImageProducer imageproducer) {
	return new Bitmap(Application.application().applet
			      .createImage(imageproducer));
    }
    
    public static java.awt.Image awtImageForBitmap(Bitmap bitmap) {
	return bitmap.awtImage();
    }
    
    public static ImageProducer awtImageProducerForBitmap(Bitmap bitmap) {
	return bitmap.awtImage().getSource();
    }
    
    public static Font fontForAWTFont(java.awt.Font font) {
	Font font_0_ = new Font();
	font_0_._awtFont = font;
	font_0_._name = font.getName();
	font_0_._type = 1;
	return font_0_;
    }
    
    public static java.awt.Font awtFontForFont(Font font) {
	return font._awtFont;
    }
    
    public static Color colorForAWTColor(java.awt.Color color) {
	return new Color(color);
    }
    
    public static java.awt.Color awtColorForColor(Color color) {
	return color._color;
    }
    
    public static FontMetrics fontMetricsForAWTFontMetrics
	(java.awt.FontMetrics fontmetrics) {
	return new FontMetrics(fontmetrics);
    }
    
    public static java.awt.FontMetrics awtFontMetricsForFontMetrics
	(FontMetrics fontmetrics) {
	return fontmetrics._awtMetrics;
    }
    
    public static MenuBar awtMenuBarForMenu(Menu menu) {
	if (menu.isTopLevel())
	    return menu.awtMenuBar();
	return null;
    }
    
    public static java.awt.Menu awtMenuForMenu(Menu menu) {
	if (!menu.isTopLevel())
	    return menu.awtMenu();
	return null;
    }
    
    public static java.awt.MenuItem awtMenuItemForMenuItem(MenuItem menuitem) {
	return menuitem.foundationMenuItem();
    }
    
    public static Graphics graphicsForAWTGraphics(java.awt.Graphics graphics) {
	Rectangle rectangle = graphics.getClipRect();
	Rect rect;
	if (rectangle != null)
	    rect = new Rect(rectangle.x, rectangle.y, rectangle.width,
			    rectangle.height);
	else
	    rect = new Rect(0, 0, 2147483647, 2147483647);
	return new Graphics(rect, graphics.create());
    }
    
    public static java.awt.Graphics awtGraphicsForGraphics(Graphics graphics) {
	return graphics.awtGraphics();
    }
    
    public static Panel awtPanelForRootView(RootView rootview) {
	return rootview.panel();
    }
    
    public static java.awt.Window awtWindowForExternalWindow
	(ExternalWindow externalwindow) {
	return externalwindow.awtWindow;
    }
    
    public static Applet awtApplet() {
	Application application = Application.application();
	if (application == null)
	    return null;
	return application.applet;
    }
    
    public static FileDialog awtFileDialogForFileChooser
	(FileChooser filechooser) {
	return filechooser.awtDialog;
    }
    
    public static Toolkit awtToolkit() {
	return Toolkit.getDefaultToolkit();
    }
    
    public static Frame awtFrameForRootView(RootView rootview) {
	return rootview.panel().frame();
    }
}
