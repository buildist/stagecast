/* Util - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.application.AWTCompatibility;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.ColorChooser;
import COM.stagecast.ifc.netscape.application.ContainerView;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.FontMetrics;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class Util implements Debug.Constants, ResourceIDs.ColorIDs,
			     ResourceIDs.CommandIDs, ResourceIDs.DialogIDs,
			     ResourceIDs.FontIDs, ResourceIDs.NameGeneratorIDs
{
    public static final int TRANSPARENT_COLOR_INT = 16777215;
    public static final MappedColor TRANSPARENT_STAND_IN_COLOR;
    static final Color HIGHLIGHT_COLOR;
    static final int MINI_CHARACTER_SIZE = 32;
    static final int DOUBLE_CLICK_DELAY = 1000;
    public static final String TEMP_FILE_PREFIX = "CREATOR.";
    public static final String TEMP_FILE_SUFFIX = ".tmp";
    public static final Color positionGridColor;
    public static final Color positionBackgroundColor;
    public static final Color positionColor;
    public static final Color selectionColor;
    public static final Color textSelectionColor;
    public static final Color textBackgroundColor;
    public static final Color textColor;
    public static final byte[] r8;
    public static final byte[] g8;
    public static final byte[] b8;
    static final Hashtable asciiTable;
    public static final Color defaultColor;
    public static final Color defaultLightColor;
    public static final Color defaultDarkColor;
    public static final Color innerBackgroundColor;
    public static final Color defaultBoardColor;
    public static final Color defaultGridColor;
    static final Color ruleScrapColor;
    public static final Font buttonFont;
    public static final Color buttonFontColor;
    public static final Font microFont;
    public static final Color microFontColor;
    public static final Font titleFont;
    public static final int titleFontHeight;
    public static final Color titleColor;
    public static final Color titleBarColor;
    public static final Color titleBarInactiveColor;
    public static final Font ruleFont;
    public static final int ruleFontHeight;
    public static final Color ruleColor;
    public static final Color valueBoxColor;
    public static final Color menuColor;
    public static final Color testColor;
    public static final Font valueFont;
    public static final int valueFontHeight;
    public static final Color valueColor;
    public static final Color valueBGColor;
    public static final Font nameFont;
    public static final int nameFontHeight;
    public static final Color nameColor;
    public static final Color nameBackground;
    public static final Font commentFont;
    public static final int commentFontHeight;
    public static final Color commentColor;
    public static final Color commentBackground;
    public static final Color commentBorder;
    public static final Font varTitleFont;
    public static final int varTitleFontHeight;
    public static final Color varTitleColor;
    public static final int LEFT_JUSTIFIED = 0;
    public static final int CENTERED = 1;
    public static final int RIGHT_JUSTIFIED = 2;
    public static final int[] FONT_SIZES;
    public static final FontFamily[] FONT_FAMILIES;
    public static final FontStyle[] FONT_STYLES;
    private static final InternalWindow _dummyWindow;
    public static final Color[] chooseColors;
    public static final String[] chooseColorStrings;
    static Bitmap lightSwitchOff;
    static Bitmap lightSwitchOn;
    private static Color selectedColor;
    private static int forceGCCount;
    private static long totalForceGCTime;
    private static int LOAD_IMAGE_FAILED;
    private static Vector _fonts;
    private static Vector _styles;
    
    private static class MappedColor
    {
	private Color _writeColor;
	private int _readRGB;
	
	MappedColor(Color writeColor, int readRGB) {
	    _writeColor = writeColor;
	    _readRGB = readRGB;
	}
	
	public Color getWriteColor() {
	    return _writeColor;
	}
	
	public int getReadRGB() {
	    return _readRGB;
	}
	
	public String toString() {
	    return ("Write color = " + toHexString(_writeColor.rgb())
		    + "; Read RGB = " + toHexString(_readRGB));
	}
    }
    
    public static class FontFamily
    {
	private String _userName;
	private String _javaName;
	
	protected FontFamily(String userName, String javaName) {
	    _userName = userName;
	    _javaName = javaName;
	}
	
	public String getUserName() {
	    return _userName;
	}
	
	public String getJavaName() {
	    return _javaName;
	}
    }
    
    public static class FontStyle
    {
	private String _userStyle;
	private int _javaStyle;
	
	protected FontStyle(String userStyle, int javaStyle) {
	    _userStyle = userStyle;
	    _javaStyle = javaStyle;
	}
	
	public String getUserStyle() {
	    return _userStyle;
	}
	
	public int getJavaStyle() {
	    return _javaStyle;
	}
    }
    
    static {
	Rect size = new Rect(0, 0, 4, 4);
	Bitmap buffer
	    = BitmapManager.createBitmapManager(size.width, size.height);
	Graphics graphics = buffer.createGraphics();
	int[] pixels = new int[size.width * size.height];
	Hashtable usedColorMap = new Hashtable();
	Color[][] colors = ColorGrid.getColors();
	for (int i = 0; i < colors.length; i++) {
	    for (int j = 0; j < colors[i].length; j++) {
		graphics.setColor(colors[i][j]);
		graphics.fillRect(size);
		boolean success = buffer.grabPixels(pixels);
		ASSERT.isTrue(success, "grabPixels");
		for (int z = 0; z < pixels.length; z++)
		    usedColorMap.put(new Integer(pixels[z]), colors[i][j]);
	    }
	}
	TRANSPARENT_STAND_IN_COLOR
	    = ((Toolkit.getDefaultToolkit().getColorModel()
		instanceof IndexColorModel)
	       ? getTransparentStandInColorForIndexColorModel(usedColorMap)
	       : getTransparentStandInColorForDirectColorModel(usedColorMap));
	Debug.print("debug.image", ("Transparent stand-in color: "
				    + TRANSPARENT_STAND_IN_COLOR));
	HIGHLIGHT_COLOR = Color.yellow;
	positionGridColor = Color.black;
	positionBackgroundColor = Color.white;
	positionColor = Color.red;
	selectionColor = Color.yellow;
	textSelectionColor = new Color(0.56F, 0.41F, 0.82F);
	textBackgroundColor = Color.white;
	textColor = Color.black;
	r8 = (new byte[]
	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -52, -52, -52, -52, -52, -52, -52, -52, -52,
		-52, -52, -52, -52, -52, -52, -52, -52, -52, -52, -52, -52,
		-52, -52, -52, -52, -52, -52, -52, -52, -52, -52, -52, -52,
		-52, -52, -52, -103, -103, -103, -103, -103, -103, -103, -103,
		-103, -103, -103, -103, -103, -103, -103, -103, -103, -103,
		-103, -103, -103, -103, -103, -103, -103, -103, -103, -103,
		-103, -103, -103, -103, -103, -103, -103, -103, 102, 102, 102,
		102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102,
		102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102,
		102, 102, 102, 102, 102, 102, 102, 102, 102, 51, 51, 51, 51,
		51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51,
		51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -18, -35, -69, -86,
		-120, 119, 85, 68, 34, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, -18, -35, -69, -86, -120, 119, 85, 68,
		34, 17, 0 });
	g8 = (new byte[]
	      { -1, -1, -1, -1, -1, -1, -52, -52, -52, -52, -52, -52, -103,
		-103, -103, -103, -103, -103, 102, 102, 102, 102, 102, 102, 51,
		51, 51, 51, 51, 51, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1,
		-52, -52, -52, -52, -52, -52, -103, -103, -103, -103, -103,
		-103, 102, 102, 102, 102, 102, 102, 51, 51, 51, 51, 51, 51, 0,
		0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -52, -52, -52, -52, -52,
		-52, -103, -103, -103, -103, -103, -103, 102, 102, 102, 102,
		102, 102, 51, 51, 51, 51, 51, 51, 0, 0, 0, 0, 0, 0, -1, -1, -1,
		-1, -1, -1, -52, -52, -52, -52, -52, -52, -103, -103, -103,
		-103, -103, -103, 102, 102, 102, 102, 102, 102, 51, 51, 51, 51,
		51, 51, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -52, -52,
		-52, -52, -52, -52, -103, -103, -103, -103, -103, -103, 102,
		102, 102, 102, 102, 102, 51, 51, 51, 51, 51, 51, 0, 0, 0, 0, 0,
		0, -1, -1, -1, -1, -1, -1, -52, -52, -52, -52, -52, -52, -103,
		-103, -103, -103, -103, -103, 102, 102, 102, 102, 102, 102, 51,
		51, 51, 51, 51, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, -18, -35, -69, -86, -120, 119, 85, 68, 34, 17, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, -18, -35, -69, -86, -120, 119, 85, 68, 34,
		17, 0 });
	b8 = (new byte[]
	      { -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52,
		-103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52, -103,
		102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51,
		0, -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1,
		-52, -103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52,
		-103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52, -103,
		102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51,
		0, -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1,
		-52, -103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52,
		-103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52, -103,
		102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51,
		0, -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1,
		-52, -103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52,
		-103, 102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52, -103,
		102, 51, 0, -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51,
		0, -1, -52, -103, 102, 51, 0, -1, -52, -103, 102, 51, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -18, -35,
		-69, -86, -120, 119, 85, 68, 34, 17, -18, -35, -69, -86, -120,
		119, 85, 68, 34, 17, 0 });
	asciiTable = new Hashtable();
	asciiTable.put("20", " ");
	asciiTable.put("28", "(");
	asciiTable.put("29", ")");
	asciiTable.put("2D", ",");
	asciiTable.put("2F", ".");
	asciiTable.put("3A", ":");
	asciiTable.put("3B", ";");
	asciiTable.put("3C", "<");
	asciiTable.put("3D", "=");
	asciiTable.put("3E", ">");
	asciiTable.put("3F", "?");
	asciiTable.put("40", "@");
	asciiTable.put("7B", "{");
	asciiTable.put("7C", "|");
	asciiTable.put("7D", "}");
	asciiTable.put("7E", "~");
	defaultColor = Color.gray;
	defaultLightColor = Color.lightGray;
	defaultDarkColor = Color.darkGray;
	innerBackgroundColor = Color.lightGray;
	defaultBoardColor = new Color(225, 225, 225);
	defaultGridColor = defaultLightColor;
	ruleScrapColor = Color.white;
	buttonFont = Font.fontNamed("Monaco", 0, 12);
	buttonFontColor = Color.black;
	microFont = Font.fontNamed("Monaco", 0, 6);
	microFontColor = Color.black;
	titleFont = Font.fontNamed("Chicago", 0, 12);
	titleFontHeight = fontPixelHeight(titleFont);
	titleColor = Color.white;
	titleBarColor = Color.gray;
	titleBarInactiveColor = Color.lightGray;
	ruleFont = titleFont;
	ruleFontHeight = titleFontHeight;
	ruleColor = Color.black;
	valueBoxColor = new Color(206, 206, 156);
	menuColor = new Color(192, 219, 192);
	testColor = new Color(255, 251, 224);
	valueFont = Font.fontNamed("Dialog", 0, 9);
	valueFontHeight = fontPixelHeight(valueFont);
	valueColor = Color.black;
	valueBGColor = Color.white;
	nameFont = Font.fontNamed("Dialog", 0, 9);
	nameFontHeight = fontPixelHeight(nameFont);
	nameColor = Color.black;
	nameBackground = Color.white;
	commentFont = Font.fontNamed("Dialog", 0, 9);
	commentFontHeight = fontPixelHeight(commentFont);
	commentColor = Color.black;
	commentBackground = new Color(255, 255, 153);
	commentBorder = Color.black;
	varTitleFont = valueFont;
	varTitleFontHeight = valueFontHeight;
	varTitleColor = Color.black;
	FONT_SIZES = new int[] { 8, 9, 10, 12, 14, 18, 24, 36 };
	FONT_FAMILIES
	    = (new FontFamily[]
	       { new FontFamily(Resource.getText("Font Family Helvetica"),
				"Helvetica"),
		 new FontFamily(Resource.getText("Font Family Times Roman"),
				"TimesRoman"),
		 new FontFamily(Resource.getText("Font Family Courier"),
				"Courier") });
	FONT_STYLES
	    = (new FontStyle[]
	       { new FontStyle(Resource.getText("Font Style Plain"), 0),
		 new FontStyle(Resource.getText("Font Style Bold"), 1),
		 new FontStyle(Resource.getText("Font Style Italic"), 2),
		 new FontStyle(Resource.getText("Font Style Bold Italic"),
			       3) });
	_dummyWindow = new InternalWindow() {
	    public void show() {
		/* empty */
	    }
	};
	chooseColors
	    = new Color[] { Color.black, Color.darkGray, Color.gray,
			    Color.lightGray, Color.magenta, Color.blue,
			    Color.cyan, Color.green, Color.yellow,
			    Color.orange, Color.red, Color.pink, Color.white };
	chooseColorStrings
	    = new String[] { "BlackID", "darkGray CID", "gray CID",
			     "lt Gray CID", "mgnta CID", "blu CID", "CY CID",
			     "GRN CID", "Yelo CID", "Orng CID", "Red CID",
			     "Pnk CID", "WhiteID" };
	selectedColor = null;
	forceGCCount = 0;
	totalForceGCTime = 0L;
	LOAD_IMAGE_FAILED = 3000;
	_fonts = new Vector(FONT_FAMILIES.length);
	for (int i = 0; i < FONT_FAMILIES.length; i++)
	    _fonts.addElement(FONT_FAMILIES[i].getUserName());
	_styles = new Vector(FONT_STYLES.length);
	for (int i = 0; i < FONT_STYLES.length; i++)
	    _styles.addElement(FONT_STYLES[i].getUserStyle());
    }
    
    static void initStatics() {
	if (PlaywriteRoot.isAuthoring()) {
	    lightSwitchOff = Resource.getImage("SwitchOff");
	    lightSwitchOn = Resource.getImage("SwitchOn");
	} else {
	    lightSwitchOff = null;
	    lightSwitchOn = null;
	}
    }
    
    /**
     * @deprecated
     */
    public static PlaywriteButton createBezelButton
	(COM.stagecast.ifc.netscape.application.Image image, String command,
	 Target target) {
	PlaywriteButton button
	    = PlaywriteButton.createPWPushButton(0, 0, 10, 10);
	button.setImage(image);
	button.sizeToMinSize();
	button.setCommand(command);
	button.setTarget(target);
	button.setFont(microFont);
	button.setTitleColor(microFontColor);
	return button;
    }
    
    public static PlaywriteButton createHorizHandle(String command,
						    Target target) {
	return createHVHandle(true, command, target);
    }
    
    public static PlaywriteButton createVertHandle(String command,
						   Target target) {
	return createHVHandle(false, command, target);
    }
    
    private static PlaywriteButton createHVHandle
	(boolean horizontal, String command, Target target) {
	String resourceID = horizontal ? "HandleH" : "HandleV";
	PlaywriteButton button
	    = new PlaywriteButton(Resource.getButtonImage(resourceID),
				  Resource.getAltButtonImage(resourceID));
	button.setType(1);
	button.setCommand(command);
	button.setTarget(target);
	button.setBordered(false);
	button.setTransparent(true);
	return button;
    }
    
    public static PlaywriteButton createLightSwitch(String command,
						    Target target) {
	PlaywriteButton button
	    = new PlaywriteButton(0, 0, lightSwitchOff.width(),
				  lightSwitchOff.height());
	button.setImage(lightSwitchOff);
	button.setAltImage(lightSwitchOn);
	button.setCommand(command);
	button.setTarget(target);
	button.setType(1);
	button.setFont(microFont);
	button.setTitleColor(microFontColor);
	button.setBordered(false);
	button.setTransparent(true);
	return button;
    }
    
    public static void drawHilited(Graphics g, Rect r) {
	g.setColor(HIGHLIGHT_COLOR);
	g.drawRect(r.x, r.y, r.width, r.height);
	g.drawRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
    }
    
    static Bitmap createFilledBitmap(int width, int height, Color color) {
	Bitmap bitmap = BitmapManager.createBitmapManager(width, height);
	Graphics g = bitmap.createGraphics();
	g.setColor(color);
	g.fillRect(0, 0, width, height);
	g.dispose();
	return bitmap;
    }
    
    static Bitmap createBlankBitmap(int width, int height) {
	return createFilledBitmap(width, height,
				  TRANSPARENT_STAND_IN_COLOR.getWriteColor());
    }
    
    static Bitmap createTransparentBitmap(Bitmap bitmap) {
	return createTransparentBitmap(bitmap, TRANSPARENT_STAND_IN_COLOR
						   .getReadRGB());
    }
    
    public static PlaywriteView makeViewPictureView(View view) {
	return (PlaywriteView) createImageFromView(view, true);
    }
    
    public static Bitmap makeBitmapFromView(View view) {
	return (Bitmap) createImageFromView(view, false);
    }
    
    private static Object createImageFromView(View view, boolean returnView) {
	ContainerView viewAsContainer = null;
	PlaywriteView viewAsPWView = null;
	Color viewBackgroundColor = null;
	Rect oldViewBounds = view.bounds();
	boolean viewWasAdded = false;
	PlaywriteView pictureView = new PlaywriteView(view.bounds);
	pictureView.setBorder(null);
	pictureView.setTransparent(false);
	pictureView
	    .setBackgroundColor(TRANSPARENT_STAND_IN_COLOR.getWriteColor());
	if (!view.isInViewHierarchy()) {
	    view.moveTo(0, 0);
	    viewWasAdded = true;
	    pictureView.addSubview(view);
	}
	if (view.isTransparent()) {
	    if (view instanceof ContainerView) {
		viewAsContainer = (ContainerView) view;
		viewBackgroundColor = viewAsContainer.backgroundColor();
		viewAsContainer.setTransparent(false);
		viewAsContainer.setBackgroundColor(TRANSPARENT_STAND_IN_COLOR
						       .getWriteColor());
	    } else if (view instanceof PlaywriteView) {
		viewAsPWView = (PlaywriteView) view;
		viewBackgroundColor = viewAsPWView.backgroundColor();
		viewAsPWView.setTransparent(false);
		viewAsPWView.setBackgroundColor(TRANSPARENT_STAND_IN_COLOR
						    .getWriteColor());
	    }
	}
	view.addDirtyRect(null);
	Bitmap bitmap = createBlankBitmap(view.width(), view.height());
	Graphics g = bitmap.createGraphics();
	view.draw(g, null);
	g.dispose();
	g = null;
	bitmap = createTransparentBitmap(bitmap);
	pictureView.setImage(bitmap);
	if (viewWasAdded) {
	    view.removeFromSuperview();
	    view.setBounds(oldViewBounds);
	}
	if (viewAsContainer != null) {
	    viewAsContainer.setTransparent(true);
	    viewAsContainer.setBackgroundColor(viewBackgroundColor);
	} else if (viewAsPWView != null) {
	    viewAsPWView.setTransparent(true);
	    viewAsPWView.setBackgroundColor(viewBackgroundColor);
	}
	if (returnView)
	    return pictureView;
	return bitmap;
    }
    
    static Bitmap createTransparentBitmap(Bitmap bitmap, int defaultColorInt) {
	int totalSize = bitmap.width() * bitmap.height();
	int[] pixels = new int[totalSize];
	boolean success = bitmap.grabPixels(pixels);
	ASSERT.isTrue(success, "grabPixels");
	for (int i = 0; i < totalSize; i++) {
	    if (pixels[i] == defaultColorInt)
		pixels[i] = 16777215;
	}
	return BitmapManager.createBitmapManager(pixels, bitmap.width(),
						 bitmap.height());
    }
    
    public static void scaleRectToImageProportion
	(Rect rect, COM.stagecast.ifc.netscape.application.Image image) {
	float imageProportion = (float) image.width() / (float) image.height();
	float rectProportion = (float) rect.width / (float) rect.height;
	if (imageProportion < rectProportion)
	    rect.width = image.width() * rect.height / image.height();
	else
	    rect.height = image.height() * rect.width / image.width();
    }
    
    public static Bitmap getTransparentBitmap(Bitmap b, int alpha) {
	int[] pixels = new int[b.width() * b.height()];
	boolean success = b.grabPixels(pixels);
	ASSERT.isTrue(success, "grabPixels");
	for (int i = 0; i < pixels.length; i++) {
	    int pix = pixels[i];
	    if ((pix & ~0xffffff) == -16777216)
		pixels[i] = alpha << 24 | pix & 0xffffff;
	}
	return BitmapManager.createBitmapManager(pixels, b.width(),
						 b.height());
    }
    
    public static void loadImageData(Bitmap bitmap) {
	if (!(bitmap instanceof BitmapManager)) {
	    if (bitmap.isValid()) {
		int endSignal = 176;
		Toolkit tk = PlaywriteSystem.getToolkit();
		Image img = AWTCompatibility.awtImageForBitmap(bitmap);
		int count = 0;
		boolean application = PlaywriteRoot.isApplication();
		if (!tk.prepareImage(img, -1, -1, null)) {
		    while ((tk.checkImage(img, -1, -1, null) & endSignal) == 0
			   && count < LOAD_IMAGE_FAILED) {
			try {
			    Thread.sleep(10L);
			} catch (InterruptedException interruptedexception) {
			    /* empty */
			}
			if (application)
			    count++;
		    }
		    if (count >= LOAD_IMAGE_FAILED)
			Debug.print
			    (true,
			     "failed to load image. it is safe to continue.");
		}
	    } else
		bitmap.loadData();
	}
    }
    
    public static String makeNumberedName(String baseName, int number) {
	Object[] params = { baseName, new Integer(number) };
	return Resource.getTextAndFormat("Generator nin", params);
    }
    
    public static String makeCopyName(String originalName) {
	Object[] params = { originalName };
	return Resource.getTextAndFormat("Generator cin", params);
    }
    
    public static String makeCopyName(String originalName, int number) {
	Object[] params = { originalName, new Integer(number) };
	return Resource.getTextAndFormat("Generator cin2", params);
    }
    
    public static Size stringSize(Font font, String string) {
	FontMetrics fm = font.fontMetrics();
	return fm.stringSize(string);
    }
    
    public static String substitute(String s1, String s2, String string) {
	StringBuffer buffer = new StringBuffer();
	int i = 0;
	int j;
	for (int n = string.length(); i < n; i = j + s2.length()) {
	    j = string.indexOf(s2, i);
	    if (j < 0) {
		buffer.append(string.substring(i));
		break;
	    }
	    buffer.append(string.substring(i, j));
	    buffer.append(s1);
	}
	return buffer.toString();
    }
    
    public static String dePercentString(String str) {
	StringBuffer out = new StringBuffer(str.length());
	char[] hexbuf = new char[2];
	int i = 0;
	while (i < str.length()) {
	    char ch = str.charAt(i++);
	    if (ch == '%') {
		if (i < str.length() && str.charAt(i) != '%') {
		    hexbuf[0] = str.charAt(i++);
		    hexbuf[1] = str.charAt(i++);
		    String mapped
			= (String) asciiTable.get(new String(hexbuf));
		    if (mapped == null)
			mapped = " ";
		    out.append(mapped);
		} else {
		    i++;
		    out.append(ch);
		}
	    } else
		out.append(ch);
	}
	return out.toString();
    }
    
    public static String safeURLStr(String str) {
	int len = str.length();
	StringBuffer out = new StringBuffer(len);
	for (int i = 0; i < len; i++) {
	    char ch = str.charAt(i);
	    if (ch < 33 || ch > 122) {
		out.append('%');
		out.append(hexChar((ch & 0xf0) >> 4));
		out.append(hexChar(ch & 0xf));
	    } else if (ch == 37)
		out.append("%%");
	    else
		out.append(ch);
	}
	return out.toString();
    }
    
    public static char hexChar(int i) {
	return i <= 9 ? (char) (48 + i) : (char) (65 + i - 10);
    }
    
    public static int fontPixelHeight(Font font) {
	return font.fontMetrics().height();
    }
    
    public static void drawString(Graphics g, String string, int x, int y,
				  int justification, Font font, Color color) {
	g.pushState();
	g.setColor(color);
	g.setFont(font);
	int width = font.fontMetrics().stringWidth(string);
	switch (justification) {
	case 2:
	    x -= width;
	    break;
	case 1:
	    x -= width / 2;
	    break;
	}
	g.drawString(string, x, y);
	g.popState();
    }
    
    public static Label makeLabel(String string) {
	Label label = new Label(string + " ", ruleFont) {
	    public View viewForMouse(int x, int y) {
		return null;
	    }
	};
	label.setColor(ruleColor);
	return label;
    }
    
    public static Label makeRuleIndexLabel(String string) {
	Label label = new Label(" " + string + " ", ruleFont);
	return label;
    }
    
    private static String getSafeName(Object value) {
	String valueName = null;
	if (value instanceof Named)
	    valueName = ((Named) value).getName();
	else if (value != null)
	    valueName = value.toString();
	else
	    valueName = "";
	return valueName;
    }
    
    static Object findEqualOrSameName(Enumeration list, Object value) {
	String valueName = getSafeName(value);
	Object namedMatch = null;
	while (list != null && list.hasMoreElements()) {
	    Object option = list.nextElement();
	    if (option == value)
		return value;
	    if (namedMatch == null) {
		String optionName = getSafeName(option);
		if (valueName.equalsIgnoreCase(optionName))
		    namedMatch = option;
	    }
	}
	return namedMatch;
    }
    
    public static void centerView(View view) {
	centerViewInRect(view, view.superview().bounds);
	view.setHorizResizeInstruction(32);
	view.setVertResizeInstruction(64);
    }
    
    public static void centerViewHorizontally(View view) {
	int w = view.superview().width();
	w -= view.width();
	view.moveTo(w / 2, view.bounds.y);
	view.setHorizResizeInstruction(32);
    }
    
    public static void centerViewVertically(View view) {
	int h = view.superview().height();
	h -= view.height();
	view.moveTo(view.bounds.x, h / 2);
	view.setVertResizeInstruction(64);
    }
    
    public static void centerViewInRect(View view, Rect rect) {
	int w = rect.width;
	int h = rect.height;
	w -= view.width();
	h -= view.height();
	view.moveTo(rect.x + w / 2, rect.y + h / 2);
    }
    
    public static void detachSubviews(View view) {
	if (view == null && Debug.lookup("debug.gc")) {
	    Debug.print("debug.gc",
			"WARNING: null passed to Util.detachSubviews");
	    Debug.stackTrace();
	} else {
	    if (view instanceof PlaywriteView)
		PlaywriteView.garbageCount++;
	    view.removeFromSuperview();
	    int nsubviews = view.subviews().size();
	    View[] subview = new View[nsubviews];
	    view.subviews().copyInto(subview);
	    for (int i = 0; i < nsubviews; i++)
		detachSubviews(subview[i]);
	}
    }
    
    public static Color getColorFromChooser(World world, Color initialColor,
					    int x, int y) {
	int BUTTON_HEIGHT = 20;
	ColorChooser colorChooser
	    = PlaywriteRoot.getMainRootView().colorChooser();
	colorChooser.setWindow(_dummyWindow);
	colorChooser.setColor(initialColor);
	final ContainerView colorChooserContent
	    = (ContainerView) colorChooser.contentView();
	final PlaywriteWindow colorWindow = new PlaywriteWindow(x, y, 100, 100,
								world) {
	    void changeWindowColor(Color color) {
		colorChooserContent.setBackgroundColor(color);
		super.changeWindowColor(color);
	    }
	};
	colorChooserContent.removeFromSuperview();
	colorChooserContent.setBackgroundColor(world.getColor());
	Size contentSize
	    = colorWindow.windowSizeForContentSize(colorChooserContent.width(),
						   colorChooserContent
						       .height());
	colorWindow.sizeTo(contentSize.width,
			   contentSize.height + BUTTON_HEIGHT);
	colorWindow.setResizable(false);
	colorWindow.setCloseable(false);
	colorWindow.setAllowDestroy(false);
	colorWindow.addSubview(colorChooserContent);
	colorChooserContent.moveTo(0, 0);
	Selection.resetGlobalState();
	Target target = new Target() {
	    public void performCommand(String command, Object data) {
		if (command.equals("command sc"))
		    Util.selectedColor
			= PlaywriteRoot.getMainRootView().colorChooser()
			      .color();
		else if (command.equals("command cc"))
		    Util.selectedColor = null;
		colorChooserContent.removeFromSuperview();
		colorWindow.close();
	    }
	};
	COM.stagecast.ifc.netscape.application.Button okButton
	    = new PlaywriteButton(0, colorChooserContent.height(),
				  contentSize.width / 2, BUTTON_HEIGHT);
	okButton.setTitle(Resource.getText("command ok"));
	okButton.setTarget(target);
	okButton.setCommand("command sc");
	colorWindow.addSubview(okButton);
	COM.stagecast.ifc.netscape.application.Button cancelButton
	    = new PlaywriteButton(okButton.bounds().maxX(),
				  colorChooserContent.height(),
				  contentSize.width / 2, BUTTON_HEIGHT);
	cancelButton.setTitle(Resource.getText("command c"));
	cancelButton.setTarget(target);
	cancelButton.setCommand("command cc");
	colorWindow.addSubview(cancelButton);
	colorWindow.setTitle(Resource.getText("dialog cct"));
	colorWindow.showModally();
	return selectedColor;
    }
    
    public static void initializeByteArray(byte[] array, byte value) {
	int length = array.length;
	if (length > 0) {
	    array[0] = value;
	    for (int i = 1; i < length; i += i)
		System.arraycopy(array, 0, array, i,
				 length - i < i ? length - i : i);
	}
    }
    
    public static void initializeIntArray(int[] array, int value) {
	int length = array.length;
	if (length > 0) {
	    array[0] = value;
	    for (int i = 1; i < length; i += i)
		System.arraycopy(array, 0, array, i,
				 length - i < i ? length - i : i);
	}
    }
    
    public static void initializeBooleanArray(boolean[] array, boolean value) {
	int length = array.length;
	if (length > 0) {
	    array[0] = value;
	    for (int i = 1; i < length; i += i)
		System.arraycopy(array, 0, array, i,
				 length - i < i ? length - i : i);
	}
    }
    
    public static void initializeCharArray(char[] array, char value) {
	int length = array.length;
	if (length > 0) {
	    array[0] = value;
	    for (int i = 1; i < length; i += i)
		System.arraycopy(array, 0, array, i,
				 length - i < i ? length - i : i);
	}
    }
    
    public static void initializeShortArray(short[] array, short value) {
	int length = array.length;
	if (length > 0) {
	    array[0] = value;
	    for (int i = 1; i < length; i += i)
		System.arraycopy(array, 0, array, i,
				 length - i < i ? length - i : i);
	}
    }
    
    public static void initializeLongArray(long[] array, long value) {
	int length = array.length;
	if (length > 0) {
	    array[0] = value;
	    for (int i = 1; i < length; i += i)
		System.arraycopy(array, 0, array, i,
				 length - i < i ? length - i : i);
	}
    }
    
    public static void initializeFloatArray(float[] array, float value) {
	int length = array.length;
	if (length > 0) {
	    array[0] = value;
	    for (int i = 1; i < length; i += i)
		System.arraycopy(array, 0, array, i,
				 length - i < i ? length - i : i);
	}
    }
    
    public static void initializeDoubleArray(double[] array, double value) {
	int length = array.length;
	if (length > 0) {
	    array[0] = value;
	    for (int i = 1; i < length; i += i)
		System.arraycopy(array, 0, array, i,
				 length - i < i ? length - i : i);
	}
    }
    
    public static String getFilePart(String fname) {
	int dotpos = fname.lastIndexOf(".");
	return dotpos < 0 ? fname : fname.substring(0, dotpos);
    }
    
    public static byte[] streamToByteArray(InputStream is, long streamSize)
	throws IOException {
	byte[] data = null;
	try {
	    if (streamSize == -1L) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		streamCopy(is, baos);
		is = null;
		data = baos.toByteArray();
	    } else {
		int max = (int) streamSize;
		int count = max;
		data = new byte[max];
		int chunk;
		for (/**/; count > 0; count -= chunk)
		    chunk = is.read(data, max - count, count);
	    }
	} finally {
	    try {
		if (is != null)
		    is.close();
	    } catch (IOException e) {
		Debug.stackTrace(e);
	    }
	}
	return data;
    }
    
    public static byte[] streamToByteArray(InputStream is) throws IOException {
	return streamToByteArray(is, -1L);
    }
    
    public static void streamCopy(InputStream is, OutputStream os)
	throws IOException {
	streamCopy(is, os, null, 0);
    }
    
    public static void streamCopy
	(InputStream is, OutputStream os, ProgressDialog dlg, int incr)
	throws IOException {
	int count = 0;
	is = new BufferedInputStream(is);
	os = new BufferedOutputStream(os);
	try {
	    int b;
	    do {
		b = is.read();
		if (b != -1)
		    os.write(b);
		count++;
		if (dlg != null && count % incr == 0)
		    dlg.incrementTotalDone(incr);
	    } while (b != -1);
	    os.flush();
	} finally {
	    try {
		os.close();
	    } catch (IOException e) {
		Debug.stackTrace(e);
	    }
	    try {
		is.close();
	    } catch (IOException e) {
		Debug.stackTrace(e);
	    }
	}
    }
    
    public static File createTempFile(String path) {
	File tempfile;
	do {
	    long rand = Math.round(Math.random() * 10000.0);
	    String name = "CREATOR." + rand + ".tmp";
	    tempfile = new File(path, name);
	} while (tempfile.exists());
	return tempfile;
    }
    
    public static boolean isPathWritable(String path) {
	File fpath = new File(path);
	if (!fpath.isDirectory())
	    return false;
	File temp = createTempFile(path);
	OutputStream out = null;
	try {
	    out = new FileOutputStream(temp);
	    out.write(1);
	    out.flush();
	} catch (IOException ioexception) {
	    return false;
	} finally {
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	    temp.delete();
	}
	return true;
    }
    
    public static boolean copyFile(File src, File dest) throws IOException {
	byte[] buffer = new byte[65536];
	int count = 0;
	try {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dest);
	    while (count >= 0) {
		count = in.read(buffer);
		if (count > 0)
		    out.write(buffer, 0, count);
	    }
	    out.flush();
	    out.close();
	    in.close();
	} finally {
	    buffer = null;
	}
	return src.length() == dest.length();
    }
    
    static final void transformLL1ToUL0(Point p, int height) {
	ASSERT.isTrue(p.x > 0);
	ASSERT.isTrue(p.y > 0);
	ASSERT.isTrue(p.y <= height);
	p.x--;
	p.y = height - p.y;
    }
    
    static final void transformUL0ToLL1(Point p, int height) {
	ASSERT.isTrue(p.x >= 0);
	ASSERT.isTrue(p.y >= 0);
	ASSERT.isTrue(p.y < height);
	p.x++;
	p.y = height - p.y;
    }
    
    public static String toHexString(int value) {
	StringBuffer buffer = new StringBuffer("0x");
	buffer.append(Integer.toHexString(value).toUpperCase());
	return buffer.toString();
    }
    
    public static void suggestGC() {
	Debug.print("debug.gc", "Force GC");
	long gcStart = System.currentTimeMillis();
	System.gc();
	System.gc();
	System.gc();
	System.runFinalization();
	forceGCCount++;
	totalForceGCTime += System.currentTimeMillis() - gcStart;
    }
    
    public static void printStatistics() {
	Debug.print("debug.statistics", "");
	Debug.print("debug.statistics",
		    "Total number of force (requested) GCs: ", forceGCCount);
	Debug.print("debug.statistics",
		    ("Total time in forced (requested) GCs: "
		     + totalForceGCTime),
		    "ms");
	forceGCCount = 0;
	totalForceGCTime = 0L;
    }
    
    private static MappedColor getTransparentStandInColorForIndexColorModel
	(Hashtable usedColorMap) {
	Rect size = new Rect(0, 0, 4, 4);
	Bitmap buffer
	    = BitmapManager.createBitmapManager(size.width, size.height);
	Graphics graphics = buffer.createGraphics();
	int[] pixels = new int[size.width * size.height];
	IndexColorModel model
	    = (IndexColorModel) Toolkit.getDefaultToolkit().getColorModel();
	for (int i = model.getMapSize() - 1; i >= 0; i--) {
	    int rgb = model.getRGB(i);
	    graphics.setColor(new Color(rgb));
	    graphics.fillRect(size);
	    boolean success = buffer.grabPixels(pixels);
	    ASSERT.isTrue(success, "grabPixels");
	    for (int z = 0; z < pixels.length; z++) {
		if (pixels[z] != pixels[0]
		    || usedColorMap.get(new Integer(pixels[z])) != null)
		    break;
		if (z + 1 == pixels.length)
		    return new MappedColor(new Color(rgb), pixels[z]);
	    }
	}
	return new MappedColor(Color.white, Color.white.rgb());
    }
    
    private static MappedColor getTransparentStandInColorForDirectColorModel
	(Hashtable usedColorMap) {
	Rect size = new Rect(0, 0, 4, 4);
	Bitmap buffer
	    = BitmapManager.createBitmapManager(size.width, size.height);
	Graphics graphics = buffer.createGraphics();
	int[] pixels = new int[size.width * size.height];
	for (int r = 0; r <= 255; r++) {
	    for (int g = 0; g <= 255; g++) {
		for (int b = 0; b <= 255; b++) {
		    int rgb = -16777216 + (r << 16) + (g << 8) + b;
		    graphics.setColor(new Color(rgb));
		    graphics.fillRect(size);
		    try {
			boolean success = buffer.grabPixels(pixels);
			ASSERT.isTrue(success, "grabPixels");
		    } catch (InternalError internalerror) {
			continue;
		    }
		    for (int z = 0; z < pixels.length; z++) {
			if (pixels[z] != pixels[0]
			    || (usedColorMap.get(new Integer(pixels[z]))
				!= null))
			    break;
			if (z + 1 == pixels.length)
			    return new MappedColor(new Color(rgb), pixels[z]);
		    }
		}
	    }
	}
	return new MappedColor(Color.white, Color.white.rgb());
    }
    
    static String intToIdentifier(int num) {
	byte[] buf = new byte[4];
	buf[0] = (byte) ((num & ~0xffffff) >> 24);
	buf[1] = (byte) ((num & 0xff0000) >> 16);
	buf[2] = (byte) ((num & 0xff00) >> 8);
	buf[3] = (byte) (num & 0xff);
	ByteArrayOutputStream idMaker = new ByteArrayOutputStream(4);
	idMaker.write(buf, 0, 4);
	String id = idMaker.toString();
	return id;
    }
    
    public static Object growArray(Object foo, int newSize) {
	Object newFoo = foo;
	int length = Array.getLength(foo);
	if (foo.getClass().isArray() && newSize > length) {
	    Class componentType = foo.getClass().getComponentType();
	    newFoo = Array.newInstance(componentType, newSize);
	    System.arraycopy(foo, 0, newFoo, 0, length);
	}
	return newFoo;
    }
    
    static final Vector getAllButtons(View parentView) {
	Vector sub = parentView.subviews();
	Vector result = new Vector(20);
	Enumeration e = sub.elements();
	while (e.hasMoreElements()) {
	    View v = (View) e.nextElement();
	    if (v instanceof PlaywriteButton)
		result.addElement(v);
	    if (v.subviews() != null)
		result.addElements(getAllButtons(v));
	}
	return result;
    }
    
    public static Subtotal copySubtotal(Subtotal subtotal) {
	Hashtable nilPotentMap = new Hashtable(40);
	buildTable(nilPotentMap, subtotal.getExpression());
	Subtotal subtotalCopy = (Subtotal) subtotal.copy(nilPotentMap, false);
	return subtotalCopy;
    }
    
    private static void buildTable(Hashtable map,
				   Subtotal.Creator expression) {
	if (expression != null) {
	    COM.stagecast.operators.Expression[] exp
		= expression.subexpressions();
	    if (exp != null) {
		int j = exp.length;
		while (j-- > 0) {
		    COM.stagecast.operators.Expression sub = exp[j];
		    if (sub instanceof VariableAlias)
			map.put(sub, sub);
		    else if (sub instanceof Subtotal.Creator)
			buildTable(map, (Subtotal.Creator) sub);
		    else if (sub instanceof Subtotal)
			buildTable(map, ((Subtotal) sub).getExpression());
		}
	    } else
		Debug.print(true, "expression ", expression,
			    " got no subexpressions!");
	}
    }
    
    public static Vector getFontPopupList() {
	return _fonts;
    }
    
    public static Vector getStylePopupList() {
	return _styles;
    }
    
    public static PopupVariable createFontVariable(ResourceBundle bundle,
						   String sysVarID) {
	return new PopupVariable(bundle, sysVarID,
				 EnumeratedVariable.ENUM_ACCESSOR, _fonts);
    }
    
    public static PopupVariable createFontStyleVariable
	(ResourceBundle bundle, String sysVarID, String nameID) {
	return new PopupVariable(bundle, sysVarID, nameID,
				 EnumeratedVariable.ENUM_ACCESSOR, _styles);
    }
    
    public static String javaFontNameForUserName(String userFontName) {
	for (int i = 0; i < FONT_FAMILIES.length; i++) {
	    if (FONT_FAMILIES[i].getUserName().equals(userFontName))
		return FONT_FAMILIES[i].getJavaName();
	}
	return null;
    }
    
    public static int javaFontStyleForUserName(String userFontName) {
	for (int i = 0; i < FONT_STYLES.length; i++) {
	    if (FONT_STYLES[i].getUserStyle().equals(userFontName))
		return FONT_STYLES[i].getJavaStyle();
	}
	return -1;
    }
}
