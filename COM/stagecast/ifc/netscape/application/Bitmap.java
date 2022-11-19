/* Bitmap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.MediaTracker;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.net.URL;

public class Bitmap extends Image
{
    private java.awt.Image awtImage;
    private BitmapObserver bitmapObserver;
    private Target updateTarget;
    private Rect updateRect;
    private String name;
    private String updateCommand;
    private int imageNumber;
    private boolean loaded = false;
    private boolean valid = true;
    private boolean transparent = true;
    private boolean loadIncrementally;
    private boolean added;
    private boolean useStaticTracker = false;
    private MediaTracker mediaTracker;
    static final int WIDTH = 0;
    static final int HEIGHT = 1;
    
    public static synchronized Bitmap bitmapNamed(String string, boolean bool,
						  boolean bool_0_) {
	if (string == null || string.equals(""))
	    return null;
	Application application = application();
	Bitmap bitmap = (Bitmap) application.bitmapByName.get(string);
	if (bitmap != null)
	    return bitmap;
	bitmap = systemBitmapNamed(string);
	if (bitmap != null) {
	    if (bool_0_) {
		application.bitmapByName.put(string, bitmap);
		bitmap.useStaticTracker = true;
	    }
	    bitmap.name = string;
	    return bitmap;
	}
	URL url = application._appResources.urlForBitmapNamed(string);
	bitmap = bitmapFromURL(url);
	if (bitmap == null)
	    return null;
	if (bool_0_) {
	    application.bitmapByName.put(string, bitmap);
	    bitmap.useStaticTracker = true;
	}
	bitmap.name = string;
	if (bool)
	    bitmap.startLoadingData();
	return bitmap;
    }
    
    public static Bitmap bitmapNamed(String string, boolean bool) {
	return bitmapNamed(string, bool, true);
    }
    
    public static Bitmap bitmapNamed(String string) {
	return bitmapNamed(string, true, true);
    }
    
    public static Bitmap bitmapFromURL(URL url) {
	java.awt.Image image = AWTCompatibility.awtApplet().getImage(url);
	if (image == null)
	    return null;
	Bitmap bitmap = new Bitmap(image);
	return bitmap;
    }
    
    public Graphics createGraphics() {
	return Graphics.newGraphics(this);
    }
    
    public Bitmap() {
	imageNumber = application().nextBitmapNumber();
    }
    
    public Bitmap(int i, int i_1_) {
	this();
	if (i <= 0 || i_1_ <= 0)
	    throw new IllegalArgumentException("Invalid bitmap size: " + i
					       + "x" + i_1_);
	awtImage = AWTCompatibility.awtApplet().createImage(i, i_1_);
	setLoaded(true);
    }
    
    Bitmap(java.awt.Image image) {
	this();
	awtImage = image;
    }
    
    public Bitmap(int[] is, int i, int i_2_) {
	this(is, i, i_2_, 0, i);
    }
    
    public Bitmap(int[] is, int i, int i_3_, int i_4_, int i_5_) {
	this();
	MemoryImageSource memoryimagesource
	    = new MemoryImageSource(i, i_3_, is, i_4_, i_5_);
	awtImage = AWTCompatibility.awtApplet().createImage(memoryimagesource);
	setLoaded(true);
    }
    
    public boolean grabPixels(int[] is) {
	return grabPixels(is, 0, 0, width(), height(), 0, width());
    }
    
    public boolean grabPixels(int[] is, int i, int i_6_, int i_7_, int i_8_,
			      int i_9_, int i_10_) {
	java.awt.Image image = AWTCompatibility.awtImageForBitmap(this);
	PixelGrabber pixelgrabber
	    = new PixelGrabber(image, i, i_6_, i_7_, i_8_, is, i_9_, i_10_);
	boolean bool;
	try {
	    bool = pixelgrabber.grabPixels();
	} catch (InterruptedException interruptedexception) {
	    bool = false;
	}
	return bool;
    }
    
    public String name() {
	return name;
    }
    
    BitmapObserver bitmapObserver() {
	if (bitmapObserver == null) {
	    if (updateTarget != null)
		bitmapObserver = new BitmapObserver(application(), this);
	    else
		bitmapObserver = new BitmapObserver(null, this);
	}
	return bitmapObserver;
    }
    
    int getWidthOrHeight(int i) {
	int i_11_ = -1;
	boolean bool = false;
	if (i != 0 && i != 1)
	    throw new IllegalArgumentException("Invalid dimension request: "
					       + i);
	if (hasLoadedData()) {
	    if (i == 0)
		return awtImage.getWidth(null);
	    return awtImage.getHeight(null);
	}
	BitmapObserver bitmapobserver = bitmapObserver();
	synchronized (bitmapobserver) {
	    while (!bool) {
		if (i == 0) {
		    i_11_ = awtImage.getWidth(bitmapobserver);
		    i_11_ = awtImage.getWidth(bitmapobserver);
		} else {
		    i_11_ = awtImage.getHeight(bitmapobserver);
		    i_11_ = awtImage.getHeight(bitmapobserver);
		}
		if (i_11_ != -1 || !isValid())
		    break;
		if ((bitmapobserver.lastInfo & 0x40) != 0
		    || (bitmapobserver.lastInfo & 0x80) != 0) {
		    valid = false;
		    reportWhyInvalid();
		    setLoaded(true);
		} else {
		    try {
			bitmapobserver.wait();
		    } catch (InterruptedException interruptedexception) {
			/* empty */
		    }
		    if ((bitmapobserver.lastInfo & 0x40) != 0
			|| (bitmapobserver.lastInfo & 0x80) != 0) {
			valid = false;
			reportWhyInvalid();
			setLoaded(true);
		    }
		}
	    }
	}
	return i_11_;
    }
    
    public int width() {
	return getWidthOrHeight(0);
    }
    
    public int height() {
	return getWidthOrHeight(1);
    }
    
    public java.awt.Image awtImage() {
	return awtImage;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public void drawAt(Graphics graphics, int i, int i_12_) {
	graphics.drawBitmapAt(this, i, i_12_);
    }
    
    public void drawScaled(Graphics graphics, int i, int i_13_, int i_14_,
			   int i_15_) {
	graphics.drawBitmapScaled(this, i, i_13_, i_14_, i_15_);
    }
    
    public void drawTiled(Graphics graphics, int i, int i_16_, int i_17_,
			  int i_18_) {
	if (!isValid())
	    System.err.println("Graphics.drawBitmapTiled() - Invalid bitmap: "
			       + name());
	else
	    super.drawTiled(graphics, i, i_16_, i_17_, i_18_);
    }
    
    boolean createScaledVersion(int i, int i_19_) {
	if (!isValid())
	    return false;
	Application application = application();
	BitmapObserver bitmapobserver = bitmapObserver();
	while (!application.applet.prepareImage(awtImage, i, i_19_,
						bitmapobserver)) {
	    if (loadsIncrementally())
		return true;
	    try {
		Thread.sleep(40L);
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	}
	return true;
    }
    
    boolean createScaledVersion(float f, float f_20_) {
	return createScaledVersion((int) (f * (float) width()),
				   (int) (f_20_ * (float) height()));
    }
    
    void startLoadingData() {
	MediaTracker mediatracker = tracker();
	if (!added) {
	    mediatracker.addImage(awtImage, imageNumber);
	    added = true;
	}
	mediatracker.checkID(imageNumber, true);
    }
    
    public void loadData() {
	if (!loaded) {
	    MediaTracker mediatracker = tracker();
	    while (!loaded) {
		try {
		    startLoadingData();
		    if (loadIncrementally)
			break;
		    mediatracker.waitForID(imageNumber);
		    setLoaded(true);
		} catch (InterruptedException interruptedexception) {
		    System.err.println("Bitmap.loadData() - "
				       + interruptedexception);
		}
	    }
	    if (valid)
		valid = mediatracker.isErrorID(imageNumber) ^ true;
	    if (!valid) {
		reportWhyInvalid();
		setLoaded(true);
	    }
	}
    }
    
    void setLoaded(boolean bool) {
	loaded = bool;
	mediaTracker = null;
	bitmapObserver = null;
    }
    
    public boolean hasLoadedData() {
	return loaded;
    }
    
    public void setLoadsIncrementally(boolean bool) {
	loadIncrementally = bool;
    }
    
    public boolean loadsIncrementally() {
	return loadIncrementally;
    }
    
    public synchronized Rect updateRect() {
	Rect rect;
	if (updateRect == null)
	    rect = new Rect();
	else {
	    rect = updateRect;
	    updateRect = null;
	}
	return rect;
    }
    
    public synchronized void setUpdateTarget(Target target) {
	updateTarget = target;
    }
    
    public synchronized Target updateTarget() {
	return updateTarget;
    }
    
    public synchronized void setUpdateCommand(String string) {
	updateCommand = string;
    }
    
    public synchronized String updateCommand() {
	return updateCommand;
    }
    
    void reportWhyInvalid() {
	String string = "";
	int i = tracker().statusID(imageNumber, false);
	if ((i & 0x2) != 0)
	    string += " ABORTED";
	else if ((i & 0x8) != 0)
	    string += " COMPLETE";
	else if ((i & 0x4) != 0)
	    string += " ERRORED";
	else if ((i & 0x1) != 0)
	    string += " LOADING";
	System.err.println("Invalid bitmap: " + name() + string);
    }
    
    public boolean isValid() {
	return valid;
    }
    
    public void flush() {
	awtImage.flush();
    }
    
    public String toString() {
	if (name != null)
	    return "Bitmap(" + name + ")";
	return super.toString();
    }
    
    private static Application application() {
	return Application.application();
    }
    
    private static Bitmap systemBitmapNamed(String string) {
	Bitmap bitmap = null;
	if (string == null)
	    return null;
	if (!string.startsWith("netscape/application/"))
	    return null;
	String string_21_ = string.substring("netscape/application/".length());
	if (string_21_.equals("RedGrad.gif"))
	    bitmap = SystemImages.redGrad();
	else if (string_21_.equals("GreenGrad.gif"))
	    bitmap = SystemImages.greenGrad();
	else if (string_21_.equals("BlueGrad.gif"))
	    bitmap = SystemImages.blueGrad();
	else if (string_21_.equals("CheckMark.gif"))
	    bitmap = SystemImages.checkMark();
	else if (string_21_.equals("CloseButton.gif"))
	    bitmap = SystemImages.closeButton();
	else if (string_21_.equals("CloseButtonActive.gif"))
	    bitmap = SystemImages.closeButtonActive();
	else if (string_21_.equals("ColorScrollKnob.gif"))
	    bitmap = SystemImages.colorScrollKnob();
	else if (string_21_.equals("PopupKnob.gif"))
	    bitmap = SystemImages.popupKnob();
	else if (string_21_.equals("PopupKnobH.gif"))
	    bitmap = SystemImages.popupKnobH();
	else if (string_21_.equals("RadioButtonOff.gif"))
	    bitmap = SystemImages.radioButtonOff();
	else if (string_21_.equals("RadioButtonOn.gif"))
	    bitmap = SystemImages.radioButtonOn();
	else if (string_21_.equals("ResizeLeft.gif"))
	    bitmap = SystemImages.resizeLeft();
	else if (string_21_.equals("ResizeRight.gif"))
	    bitmap = SystemImages.resizeRight();
	else if (string_21_.equals("ScrollDownArrow.gif"))
	    bitmap = SystemImages.scrollDownArrow();
	else if (string_21_.equals("ScrollDownArrowActive.gif"))
	    bitmap = SystemImages.scrollDownArrowActive();
	else if (string_21_.equals("ScrollKnobH.gif"))
	    bitmap = SystemImages.scrollKnobH();
	else if (string_21_.equals("ScrollKnobV.gif"))
	    bitmap = SystemImages.scrollKnobV();
	else if (string_21_.equals("ScrollLeftArrow.gif"))
	    bitmap = SystemImages.scrollLeftArrow();
	else if (string_21_.equals("ScrollLeftArrowActive.gif"))
	    bitmap = SystemImages.scrollLeftArrowActive();
	else if (string_21_.equals("ScrollRightArrow.gif"))
	    bitmap = SystemImages.scrollRightArrow();
	else if (string_21_.equals("ScrollRightArrowActive.gif"))
	    bitmap = SystemImages.scrollRightArrowActive();
	else if (string_21_.equals("ScrollTrayBottom.gif"))
	    bitmap = SystemImages.scrollTrayBottom();
	else if (string_21_.equals("ScrollTrayLeft.gif"))
	    bitmap = SystemImages.scrollTrayLeft();
	else if (string_21_.equals("ScrollTrayRight.gif"))
	    bitmap = SystemImages.scrollTrayRight();
	else if (string_21_.equals("ScrollTrayTop.gif"))
	    bitmap = SystemImages.scrollTrayTop();
	else if (string_21_.equals("ScrollUpArrow.gif"))
	    bitmap = SystemImages.scrollUpArrow();
	else if (string_21_.equals("ScrollUpArrowActive.gif"))
	    bitmap = SystemImages.scrollUpArrowActive();
	else if (string_21_.equals("TitleBarLeft.gif"))
	    bitmap = SystemImages.titleBarLeft();
	else if (string_21_.equals("TitleBarRight.gif"))
	    bitmap = SystemImages.titleBarRight();
	else if (string_21_.equals("alertNotification.gif"))
	    bitmap = SystemImages.alertNotification();
	else if (string_21_.equals("alertQuestion.gif"))
	    bitmap = SystemImages.alertQuestion();
	else if (string_21_.equals("alertWarning.gif"))
	    bitmap = SystemImages.alertWarning();
	else if (string_21_.equals("topLeftArrow.gif"))
	    bitmap = SystemImages.topLeftArrow();
	else if (string_21_.equals("topRightArrow.gif"))
	    bitmap = SystemImages.topRightArrow();
	else if (string_21_.equals("bottomRightArrow.gif"))
	    bitmap = SystemImages.bottomRightArrow();
	else if (string_21_.equals("bottomLeftArrow.gif"))
	    bitmap = SystemImages.bottomLeftArrow();
	return bitmap;
    }
    
    public Image imageWithName(String string) {
	return bitmapNamed(string);
    }
    
    synchronized void unionWithUpdateRect(int i, int i_22_, int i_23_,
					  int i_24_) {
	if (updateRect == null)
	    updateRect = new Rect(i, i_22_, i_23_, i_24_);
	else
	    updateRect.unionWith(i, i_22_, i_23_, i_24_);
    }
    
    private MediaTracker tracker() {
	if (mediaTracker == null) {
	    if (useStaticTracker)
		mediaTracker = Application.application().mediaTracker();
	    else
		mediaTracker = new MediaTracker(AWTCompatibility.awtApplet());
	}
	return mediaTracker;
    }
}
