/* BitmapManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Locale;

import COM.stagecast.ifc.netscape.application.AWTCompatibility;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;

public class BitmapManager extends Bitmap
    implements Debug.Constants, PlaywriteSystem.Properties, MemoryConsumer,
	       StreamedMediaItem
{
    private static final String INITIAL_DEFAULT_CACHE_SIZE = "3m";
    private static final String INITIAL_DEFAULT_MIN_CACHE_SIZE = "1m";
    private static final boolean HAS_GDI_MEMORY_LIMITATIONS
	= PlaywriteSystem.isWindows() && PlaywriteSystem.isJava_1_1_x();
    private static final boolean HAS_MAC_MEMORY_LIMITATION
	= MemoryCleanupGnome.GC_MEMORY_THRESHOLD > 0;
    private static final boolean IS_BUG_1889_SUSCEPTIBLE
	= ((PlaywriteSystem.isWindows() && PlaywriteSystem.isJava_1_2_x()
	    || PlaywriteSystem.isMRJ_2_1_x())
	   && (Toolkit.getDefaultToolkit().getColorModel().getPixelSize() == 16
	       || (Toolkit.getDefaultToolkit().getColorModel().getPixelSize()
		   == 15)));
    private static double _totalCheckoutCount = 0.0;
    private static int _checkoutMissCount = 0;
    private static double _averageCheckOutTime = 0.0;
    private static long _worstCaseCheckOutTime = 0L;
    private static CacheStrategy _bitmapCache = new LRUCacheStrategy();
    private static CacheEnforcer _enforcer;
    private static ScaledDrawingStrategy _scaledDrawingStrategy;
    private static G2Drawer g2Drawer;
    public static final int SRC_UNKNOWN = 0;
    public static final int SRC_NATIVE = 1;
    public static final int SRC_PIXMAP = 2;
    private StreamProducer _mediaSource;
    private int _mediaSourceFormat;
    private Bitmap _delegate;
    private Size _size;
    private int _checkoutCount = 0;
    private boolean _loadImageData = true;
    
    private static class NativeMaker implements LazyBitmap.BitmapMaker
    {
	StreamProducer _sp;
	
	NativeMaker(StreamProducer sp) {
	    _sp = sp;
	}
	
	public Bitmap createBitmap() {
	    try {
		byte[] buffer = Util.streamToByteArray(_sp.makeInputStream());
		Image image = Toolkit.getDefaultToolkit().createImage(buffer);
		Bitmap bmp = AWTCompatibility.bitmapForAWTImage(image);
		Util.loadImageData(bmp);
		return bmp;
	    } catch (IOException e) {
		Debug.stackTrace(e);
		return null;
	    }
	}
    }
    
    public static class G2Drawer
    {
	Method method_getScaleInstance;
	Method method_drawImageMethod;
	
	public G2Drawer() {
	    try {
		Class class_affineTransform
		    = Class.forName("java.awt.geom.AffineTransform");
		method_getScaleInstance
		    = class_affineTransform.getMethod("getScaleInstance",
						      (new Class[]
						       { Double.TYPE,
							 Double.TYPE }));
		method_drawImageMethod
		    = (Class.forName("java.awt.Graphics2D").getMethod
		       ("drawImage",
			new Class[] { Image.class, class_affineTransform,
				      java.awt.image.ImageObserver.class }));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
	public void drawG2(Graphics g2, Image image, int x, int y,
			   Double scaleX, Double scaleY) {
	    try {
		g2.translate(x, y);
		Object transform
		    = method_getScaleInstance
			  .invoke(null, new Object[] { scaleX, scaleY });
		method_drawImageMethod
		    .invoke(g2, new Object[] { image, transform, null });
		g2.translate(-x, -y);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
    
    private static interface ScaledDrawingStrategy
    {
	public void drawScaled
	    (Bitmap bitmap,
	     COM.stagecast.ifc.netscape.application.Graphics graphics,
	     Rect rect, Rect rect_0_);
    }
    
    private static class DefaultScaledDrawingStrategy
	implements ScaledDrawingStrategy
    {
	public DefaultScaledDrawingStrategy() {
	    Debug.print("debug.image",
			"DefaultScaledDrawingStrategy: installed.");
	}
	
	public void drawScaled
	    (Bitmap bitmap, COM.stagecast.ifc.netscape.application.Graphics g,
	     Rect source, Rect destination) {
	    if (!PlaywriteSystem.isMacintosh()
		&& isBug1889SusceptibleBitmap(bitmap) == true) {
		BitmapManager temp
		    = createBug1889SafeBitmap(bitmap, source.x, source.y,
					      source.width, source.height);
		temp.drawScaled(g, new Rect(0, 0, source.width, source.height),
				destination);
		temp.flush();
	    } else {
		Image internalImage
		    = AWTCompatibility.awtImageForBitmap(bitmap);
		Graphics internalGraphics
		    = AWTCompatibility.awtGraphicsForGraphics(g);
		internalGraphics.translate(g.xTranslation(), g.yTranslation());
		if (BitmapManager.g2Drawer != null) {
		    java.awt.Shape shape = internalGraphics.getClip();
		    Double scaleX
			= new Double(((double) destination.width
				      / (double) source.width) + 0.001);
		    Double scaleY
			= new Double(((double) destination.height
				      / (double) source.height) + 0.001);
		    internalGraphics.clipRect(destination.x, destination.y,
					      destination.width,
					      destination.height);
		    BitmapManager.g2Drawer.drawG2
			(internalGraphics, internalImage,
			 destination.x - source.x * scaleX.intValue(),
			 destination.y - source.y * scaleY.intValue(), scaleX,
			 scaleY);
		    internalGraphics.setClip(shape);
		} else
		    internalGraphics.drawImage
			(internalImage, destination.x, destination.y,
			 destination.x + destination.width,
			 destination.y + destination.height, source.x,
			 source.y, source.x + source.width,
			 source.y + source.height, null);
		internalGraphics.translate(-g.xTranslation(),
					   -g.yTranslation());
	    }
	}
    }
    
    private static class ReplicateScaledDrawingStrategy
	implements ScaledDrawingStrategy
    {
	public ReplicateScaledDrawingStrategy() {
	    Debug.print("debug.image",
			"ReplicateScaledDrawingStrategy: installed.");
	}
	
	public void drawScaled
	    (Bitmap bitmap, COM.stagecast.ifc.netscape.application.Graphics g,
	     Rect source, Rect destination) {
	    if (!PlaywriteSystem.isMacintosh()
		&& isBug1889SusceptibleBitmap(bitmap) == true) {
		BitmapManager temp
		    = createBug1889SafeBitmap(bitmap, source.x, source.y,
					      source.width, source.height);
		temp.drawScaled(g, new Rect(0, 0, source.width, source.height),
				destination);
		temp.flush();
	    } else {
		Bitmap cropped = null;
		Bitmap scaled = null;
		if (source.x != 0 || source.y != 0
		    || source.width != bitmap.width()
		    || source.height != bitmap.height()) {
		    ImageFilter filter
			= new CropImageFilter(source.x, source.y, source.width,
					      source.height);
		    bitmap = cropped
			= createFilteredBitmapManager(bitmap, filter);
		}
		if (source.width != destination.width
		    || source.height != destination.height)
		    bitmap = scaled
			= createScaledBitmapManager(bitmap, destination.width,
						    destination.height);
		bitmap.drawAt(g, destination.x, destination.y);
		if (cropped != null)
		    cropped.flush();
		if (scaled != null)
		    scaled.flush();
	    }
	}
    }
    
    public static class MemoryCleanupGnome
    {
	public static final int GC_MEMORY_THRESHOLD
	    = (PlaywriteSystem.getApplicationPropertyAsInt
	       ("bitmap_memory_threshold",
		PlaywriteSystem.isMacintosh() ? 512 : 0));
	private static final Size ZERO_SIZE = new Size(0, 0);
	private static boolean isActive = true;
	private static int _flushedImageMem = 0;
	
	public static void imageFlushed(BitmapManager image) {
	    _flushedImageMem = _flushedImageMem + (image == null ? 0
						   : image.memoryImpact());
	    if (isActive && _flushedImageMem > GC_MEMORY_THRESHOLD) {
		Debug.print
		    ("debug.memory",
		     ("BitmapManager.MemoryCleanupGnome: manual gc because "
		      + _flushedImageMem),
		     " > " + GC_MEMORY_THRESHOLD);
		Util.suggestGC();
		_flushedImageMem = 0;
	    }
	}
	
	public static boolean setActive(boolean active) {
	    boolean wasActive = isActive;
	    isActive = active;
	    if (isActive)
		imageFlushed(null);
	    return wasActive;
	}
    }
    
    static {
	_bitmapCache = new ThreadSafeCacheStrategy(_bitmapCache);
	_bitmapCache.addListener(new CacheStrategy.CacheListener() {
	    public void onCacheEntryAdded(MemoryConsumer data) {
		Debug.print("debug.image", '+');
	    }
	    
	    public void onCacheEntryRemoved(MemoryConsumer data) {
		Debug.print("debug.image", '-');
		data.minimizeMemoryUse();
	    }
	});
	int maxCache = getPropertyMemorySetting("cache_size", "3m");
	Size screenSize = PlaywriteSystem.getScreenSize();
	maxCache += screenSize.width * screenSize.height * 4 / 1024;
	_enforcer = (new BitmapCacheSizeEnforcer
		     (_bitmapCache, maxCache,
		      getPropertyMemorySetting("min_cache_size", "1m")));
	if (HAS_GDI_MEMORY_LIMITATIONS
	    || PlaywriteSystem.isApplicationPropertyDefined("cache_count"))
	    _enforcer
		= (new CacheEnforcerComposite
		   (_enforcer,
		    (new CacheCountEnforcer
		     (_bitmapCache,
		      PlaywriteSystem
			  .getApplicationPropertyAsInt("cache_count", 200),
		      (PlaywriteSystem.getApplicationPropertyAsInt
		       ("min_cache_count", 100))))));
	g2Drawer = null;
    }
    
    static void initStatics() {
	if (PlaywriteSystem.isWindows()) {
	    try {
		Class.forName("java.awt.Graphics2D");
		g2Drawer = new G2Drawer();
	    } catch (ClassNotFoundException classnotfoundexception) {
		g2Drawer = null;
	    }
	    boolean matching = true;
	    if (g2Drawer == null) {
		Bitmap unscaled = new Bitmap(10, 10);
		Bitmap scaled1 = new Bitmap(20, 20);
		Bitmap scaled2 = new Bitmap(20, 20);
		COM.stagecast.ifc.netscape.application.Graphics g
		    = unscaled.createGraphics();
		g.setColor(Color.blue);
		g.fillRect(0, 0, unscaled.width(), unscaled.height());
		g.setColor(Color.yellow);
		g.drawOval(3, 3, unscaled.width() / 2, unscaled.height() / 2);
		g.dispose();
		int[] unscaledPixels
		    = new int[unscaled.width() * unscaled.height()];
		boolean success = unscaled.grabPixels(unscaledPixels);
		ASSERT.isTrue(success, "grabPixels");
		Bitmap temp = unscaled;
		unscaled = new Bitmap(unscaledPixels, unscaled.width(),
				      unscaled.height());
		temp.flush();
		g = scaled1.createGraphics();
		unscaled.drawScaled(g, 0, 0, scaled1.width(),
				    scaled1.height());
		g.dispose();
		int[] defaultPixels
		    = new int[scaled1.width() * scaled1.height()];
		success = scaled1.grabPixels(defaultPixels);
		ASSERT.isTrue(success, "grabPixels");
		ImageFilter filter
		    = new ReplicateScaleFilter(scaled2.width(),
					       scaled2.height());
		ImageProducer producer
		    = AWTCompatibility.awtImageProducerForBitmap(unscaled);
		FilteredImageSource source
		    = new FilteredImageSource(producer, filter);
		scaled2 = AWTCompatibility.bitmapForAWTImageProducer(source);
		int[] replicatePixels
		    = new int[scaled2.width() * scaled2.height()];
		success = scaled2.grabPixels(replicatePixels);
		ASSERT.isTrue(success, "grabPixels");
		unscaled.flush();
		scaled1.flush();
		scaled2.flush();
		for (int i = 0; i < defaultPixels.length; i++) {
		    if (defaultPixels[i] != replicatePixels[i]) {
			matching = false;
			break;
		    }
		}
	    }
	    String strategy = (PlaywriteSystem.getApplicationProperty
			       ("scaled_drawing_strategy",
				matching ? "default" : "replicate"));
	    if (strategy.toLowerCase().equals("replicate"))
		_scaledDrawingStrategy = new ReplicateScaledDrawingStrategy();
	    else
		_scaledDrawingStrategy = new DefaultScaledDrawingStrategy();
	} else
	    _scaledDrawingStrategy = new DefaultScaledDrawingStrategy();
    }
    
    public static BitmapManager createNativeBitmapManager(StreamProducer sp) {
	return createNativeBitmapManager(sp, -1, -1);
    }
    
    public static BitmapManager createNativeBitmapManager
	(StreamProducer sp, int width, int height) {
	BitmapManager bm = createLazyBitmapManager(new NativeMaker(sp));
	bm._mediaSource = sp;
	bm._mediaSourceFormat = 1;
	if (width > 0 && height > 0)
	    bm._size = new Size(width, height);
	return bm;
    }
    
    public static BitmapManager createBitmapManager(StreamProducer sp,
						    int width, int height) {
	BitmapManager bm
	    = createBitmapManager(new StreamedImageProducer(sp, width,
							    height));
	bm._mediaSource = sp;
	bm._mediaSourceFormat = 2;
	bm._size = new Size(width, height);
	return bm;
    }
    
    public static BitmapManager createBitmapManager(int width, int height) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	return ((HAS_GDI_MEMORY_LIMITATIONS
		 || (PlaywriteSystem.isApplicationPropertyDefined
		     ("offscreen_bitmap_threshold")))
		? new BitmapManager(new OffscreenBitmapHack(width, height))
		: new BitmapManager(new Bitmap(width, height)));
    }
    
    public static BitmapManager createBitmapManager(int[] pixels, int width,
						    int height) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	return new BitmapManager(new Bitmap(pixels, width, height));
    }
    
    public static BitmapManager createBitmapManager
	(int[] pixels, int width, int height, int offset, int scanSize) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	Bitmap delegate = new Bitmap(pixels, width, height, offset, scanSize);
	return new BitmapManager(delegate);
    }
    
    public static BitmapManager createBitmapManager(Image image) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	Bitmap delegate = AWTCompatibility.bitmapForAWTImage(image);
	return new BitmapManager(delegate);
    }
    
    public static BitmapManager createBitmapManager(ImageProducer producer) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	Bitmap delegate = AWTCompatibility.bitmapForAWTImageProducer(producer);
	return new BitmapManager(delegate);
    }
    
    public static BitmapManager createBitmapManagerFromBlank
	(int[] pixels, int width, int height) {
	if (!PlaywriteSystem.isWindows()
	    || Toolkit.getDefaultToolkit().getColorModel().getPixelSize() > 8)
	    return createBitmapManager(pixels, width, height);
	BitmapManager bitmap = createBitmapManager(width, height);
	COM.stagecast.ifc.netscape.application.Graphics g
	    = bitmap.createGraphics();
	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++) {
		g.setColor(new Color(pixels[x + y * width]));
		g.fillRect(x, y, 1, 1);
	    }
	}
	g.dispose();
	return bitmap;
    }
    
    public static BitmapManager createFilteredBitmapManager
	(Bitmap bitmap, ImageFilter filter) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	BitmapManager result = null;
	if (isBug1889SusceptibleBitmap(bitmap) == true) {
	    BitmapManager temp
		= createBug1889SafeBitmap(bitmap, 0, 0, bitmap.width(),
					  bitmap.height());
	    result = createFilteredBitmapManager(temp, filter);
	    temp.flush();
	} else {
	    if (bitmap instanceof BitmapManager)
		bitmap = ((BitmapManager) bitmap)._delegate;
	    ImageProducer producer
		= AWTCompatibility.awtImageProducerForBitmap(bitmap);
	    FilteredImageSource source
		= new FilteredImageSource(producer, filter);
	    result = createBitmapManager(source);
	}
	return result;
    }
    
    public static BitmapManager createScaledBitmapManager
	(Bitmap bitmap, int width, int height) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	BitmapManager result = null;
	if (isBug1889SusceptibleBitmap(bitmap) == true) {
	    BitmapManager temp
		= createBug1889SafeBitmap(bitmap, 0, 0, bitmap.width(),
					  bitmap.height());
	    result = createScaledBitmapManager(temp, width, height);
	    temp.flush();
	} else {
	    if (bitmap instanceof BitmapManager)
		bitmap = ((BitmapManager) bitmap)._delegate;
	    Image image = AWTCompatibility.awtImageForBitmap(bitmap);
	    image = image.getScaledInstance(width, height, 8);
	    result = createBitmapManager(image);
	}
	return result;
    }
    
    public static BitmapManager createLazyBitmapManager
	(LazyBitmap.BitmapMaker bitmapMaker, int width, int height) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	BitmapManager bm
	    = new BitmapManager(new LazyBitmap(bitmapMaker, width, height));
	bm._loadImageData = false;
	return bm;
    }
    
    public static BitmapManager createLazyBitmapManager
	(LazyBitmap.BitmapMaker bitmapMaker) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	BitmapManager bm = new BitmapManager(new LazyBitmap(bitmapMaker));
	bm._loadImageData = false;
	return bm;
    }
    
    static BitmapManager copy(Bitmap source) {
	BitmapManager original = (BitmapManager) source;
	if (original._mediaSource == null)
	    return original;
	Debug.print(true, "Copying bitmap via producer");
	try {
	    StreamProducer sp = original._mediaSource.copy();
	    if (original._mediaSourceFormat == 1)
		return createNativeBitmapManager(sp, original.width(),
						 original.height());
	    return createBitmapManager(sp, original.width(),
				       original.height());
	} catch (IOException e) {
	    Debug.stackTrace(e);
	    return null;
	}
    }
    
    public static void shrinkCache() {
	Debug.print("debug.image", "shrinking cache");
	_enforcer.shrinkCache();
    }
    
    public static void enforceMinCacheLimits() {
	Debug.print("debug.image", "enforce min cache");
	_enforcer.enforceMinLimit();
    }
    
    public static void enforceDefaultCacheLimits() {
	Debug.print("debug.image", "enforce default cache");
	_enforcer.enforceDefaultLimit();
    }
    
    public static void checkOutBitmap(Bitmap bitmap) {
	if (bitmap instanceof BitmapManager)
	    ((BitmapManager) bitmap).checkOut();
    }
    
    public static void checkInBitmap(Bitmap bitmap) {
	if (bitmap instanceof BitmapManager)
	    ((BitmapManager) bitmap).checkIn();
    }
    
    private static int getPropertyMemorySetting(String propertyIx,
						String defaultValue) {
	int result = 0;
	String value = PlaywriteSystem.getApplicationProperty
			   (propertyIx, defaultValue).toLowerCase();
	int multiplier = value.endsWith("m") ? 1024 : 1;
	try {
	    NumberFormat format
		= NumberFormat.getNumberInstance(Locale.ENGLISH);
	    format.setParseIntegerOnly(true);
	    result = format.parse(value).intValue() * multiplier;
	} catch (java.text.ParseException parseexception) {
	    Debug.print(true, ("BitmapManager.getPropertyMemorySetting(): "
			       + value + " is not a valid memory setting."));
	}
	return result;
    }
    
    public static void printStatistics() {
	Debug.print("debug.statistics", "");
	Debug.print("debug.statistics",
		    "Average checkout time = " + _averageCheckOutTime + "ms");
	Debug.print("debug.statistics", ("Worst case checkout time = "
					 + _worstCaseCheckOutTime + "ms"));
	Debug.print("debug.statistics", ("Total number of checkouts = "
					 + (int) _totalCheckoutCount));
	Debug.print("debug.statistics",
		    "Checkout miss count = " + _checkoutMissCount);
	Debug.print("debug.statistics",
		    ("Checkout hit/miss ratio = "
		     + (float) (100.0 - ((double) _checkoutMissCount
					 / _totalCheckoutCount * 100.0))
		     + "%"));
	_enforcer.printStatistics();
	_bitmapCache.printStatistics();
	_totalCheckoutCount = 0.0;
	_checkoutMissCount = 0;
	_averageCheckOutTime = 0.0;
	_worstCaseCheckOutTime = 0L;
    }
    
    private static boolean isBug1889SusceptibleBitmap(Bitmap bitmap) {
	boolean result = false;
	if (bitmap instanceof BitmapManager)
	    bitmap = ((BitmapManager) bitmap)._delegate;
	ImageProducer producer
	    = AWTCompatibility.awtImageForBitmap(bitmap).getSource();
	if (producer.getClass().getName()
		.equals("sun.awt.image.OffScreenImageSource"))
	    result = (PlaywriteSystem.getApplicationPropertyAsBoolean
		      ("copy_16bit_selection", IS_BUG_1889_SUSCEPTIBLE));
	return result;
    }
    
    private static BitmapManager createBug1889SafeBitmap
	(Bitmap bitmap, int x, int y, int width, int height) {
	if (PlaywriteSystem.isMacintosh())
	    PlaywriteSystem.checkForLowMemory();
	Debug.print("debug.image", '^');
	int[] pixels = new int[width * height];
	boolean success
	    = bitmap.grabPixels(pixels, x, y, width, height, 0, width);
	ASSERT.isTrue(success, "grabPixels");
	return createBitmapManager(pixels, width, height);
    }
    
    public void drawAt(COM.stagecast.ifc.netscape.application.Graphics g,
		       int x, int y) {
	if (!PlaywriteRoot.isServer()) {
	    checkOut();
	    _delegate.drawAt(g, x, y);
	    checkIn();
	}
    }
    
    public static void drawScaled
	(Bitmap b, COM.stagecast.ifc.netscape.application.Graphics g,
	 Rect source, Rect destination) {
	((BitmapManager) b).drawScaled(g, source, destination);
    }
    
    public void drawScaled(COM.stagecast.ifc.netscape.application.Graphics g,
			   int x, int y, int width, int height) {
	drawScaled(g, new Rect(x, y, width, height));
    }
    
    public void drawScaled(COM.stagecast.ifc.netscape.application.Graphics g,
			   Rect destination) {
	drawScaled(g, new Rect(0, 0, width(), height()), destination);
    }
    
    public void drawScaled(COM.stagecast.ifc.netscape.application.Graphics g,
			   Rect source, Rect destination) {
	if (!PlaywriteRoot.isServer()) {
	    checkOut();
	    _scaledDrawingStrategy.drawScaled(_delegate, g, source,
					      destination);
	    checkIn();
	}
    }
    
    public void drawTiled(COM.stagecast.ifc.netscape.application.Graphics g,
			  int x, int y, int width, int height) {
	if (!PlaywriteRoot.isServer()) {
	    checkOut();
	    _delegate.drawTiled(g, x, y, width, height);
	    checkIn();
	}
    }
    
    public StreamProducer getMediaSource() {
	return _mediaSource;
    }
    
    public String getMediaID(StreamProducer sp) {
	if (sp instanceof MediaStreamProducer)
	    return ((MediaStreamProducer) sp).getMediaID();
	if (sp instanceof TempStreamProducer)
	    return ((TempStreamProducer) sp).getID();
	return null;
    }
    
    public String getMediaID() {
	return getMediaID(_mediaSource);
    }
    
    public int getSourceFormat() {
	return _mediaSourceFormat;
    }
    
    boolean isLazy() {
	return _delegate instanceof LazyBitmap;
    }
    
    public Image awtImage() {
	return _delegate.awtImage();
    }
    
    public int width() {
	if (_size == null) {
	    checkOut();
	    _size = new Size(_delegate.width(), _delegate.height());
	    checkIn();
	}
	return _size.width;
    }
    
    public int height() {
	if (_size == null) {
	    checkOut();
	    _size = new Size(_delegate.width(), _delegate.height());
	    checkIn();
	}
	return _size.height;
    }
    
    public boolean grabPixels(int[] pixels) {
	return grabPixels(pixels, 0, 0, width(), height(), 0, width());
    }
    
    public boolean grabPixels(int[] pixels, int x, int y, int width,
			      int height, int offset, int scanSize) {
	boolean result = false;
	checkOut();
	try {
	    result = _delegate.grabPixels(pixels, x, y, width, height, offset,
					  scanSize);
	    ASSERT.isTrue(result, "grabPixels");
	} catch (InternalError internalerror) {
	    Debug.print
		(true,
		 "BitmapManager.grabPixels(): Failure.  Attempting to reclaim GDI memory...");
	    _enforcer.shrinkCache();
	    Util.suggestGC();
	    result = _delegate.grabPixels(pixels, x, y, width, height, offset,
					  scanSize);
	    ASSERT.isTrue(result, "grabPixels");
	    Debug.print
		(true,
		 "BitmapManager.grabPixels(): Succeeded in grabbing pixels!!!!!!!!!!!!!!!!!");
	} finally {
	    checkIn();
	}
	return result;
    }
    
    public boolean hasLoadedData() {
	return true;
    }
    
    public void setLoadsIncrementally(boolean flag) {
	/* empty */
    }
    
    public void loadData() {
	/* empty */
    }
    
    public COM.stagecast.ifc.netscape.application.Graphics createGraphics() {
	return _delegate.createGraphics();
    }
    
    public synchronized void flush() {
	if (_checkoutCount == 0 && _delegate != null) {
	    if (_bitmapCache.contains(this))
		_bitmapCache.remove(this);
	    _delegate.flush();
	    if (HAS_MAC_MEMORY_LIMITATION)
		MemoryCleanupGnome.imageFlushed(this);
	}
    }
    
    void flushDelegate() {
	if (_delegate != null)
	    _delegate.flush();
    }
    
    public int memoryImpact() {
	int chunk = _size == null ? 0 : _size.width * _size.height / 256;
	return chunk < 1 ? 1 : chunk;
    }
    
    public void minimizeMemoryUse() {
	flushDelegate();
    }
    
    protected BitmapManager(Bitmap delegate) {
	if (Debug.lookup("debug.image.creation"))
	    createdFromStaticInit();
	_mediaSource = null;
	_mediaSourceFormat = 0;
	_delegate = delegate;
	_size = null;
    }
    
    private void debugInfo(String msg) {
	Debug.print(true, msg);
	Debug.print(true, "  Media source: " + getMediaSource());
	Debug.print(true, "  Media ID: " + getMediaID());
	Debug.print(true, "  Delegate: " + _delegate);
	if (_delegate instanceof LazyBitmap) {
	    LazyBitmap.BitmapMaker maker
		= ((LazyBitmap) _delegate).getBitmapMaker();
	    if (maker instanceof NativeMaker) {
		StreamProducer sp = ((NativeMaker) maker)._sp;
		Debug.print(true, ("  Lazy stream producer: " + getMediaID(sp)
				   + " " + sp));
	    }
	}
    }
    
    void become(BitmapManager other) {
	_delegate.flush();
	_mediaSource = other._mediaSource;
	_mediaSourceFormat = other._mediaSourceFormat;
	_delegate = other._delegate;
	_size = other._size;
	_loadImageData = other._loadImageData;
    }
    
    void resetDelegate(StreamProducer sp, boolean isNative) {
	if (isNative != (_mediaSourceFormat == 1)) {
	    _delegate.flush();
	    _delegate = null;
	}
	if (isNative) {
	    _delegate = new LazyBitmap(new NativeMaker(sp));
	    _mediaSource = sp;
	    _mediaSourceFormat = 1;
	    _loadImageData = false;
	} else {
	    _delegate
		= (AWTCompatibility.bitmapForAWTImageProducer
		   (new StreamedImageProducer(sp, _size.width, _size.height)));
	    _mediaSource = sp;
	    _mediaSourceFormat = 2;
	}
    }
    
    private synchronized void checkOut() {
	long entryTime = System.currentTimeMillis();
	_checkoutCount++;
	Object entry = null;
	if (_checkoutCount == 1) {
	    if (_size == null)
		_size = new Size(_delegate.width(), _delegate.height());
	    if (_bitmapCache.contains(this))
		_bitmapCache.checkOut(this);
	    else
		_bitmapCache.addToCache(this, this);
	    if (_loadImageData && _delegate != null)
		Util.loadImageData(_delegate);
	}
	if (_delegate instanceof OffscreenBitmapHack)
	    ((OffscreenBitmapHack) _delegate).onCheckOut();
	if (_size == null)
	    _size = new Size(_delegate.width(), _delegate.height());
	long exitTime = System.currentTimeMillis();
	_totalCheckoutCount++;
	if (entry == null)
	    _checkoutMissCount++;
	long elapsedTime = exitTime - entryTime;
	_averageCheckOutTime -= ((_averageCheckOutTime - (double) elapsedTime)
				 / _totalCheckoutCount);
	_worstCaseCheckOutTime = (_worstCaseCheckOutTime < elapsedTime
				  ? elapsedTime : _worstCaseCheckOutTime);
    }
    
    private synchronized void checkIn() {
	if (_checkoutCount != 0) {
	    _checkoutCount--;
	    if (_checkoutCount == 0)
		_bitmapCache.checkIn(this);
	}
    }
    
    private void createdFromStaticInit() {
	PrintStream err = System.err;
	PrintStream out = System.out;
	ByteArrayOutputStream bas = new ByteArrayOutputStream();
	System.setErr(new PrintStream(bas));
	System.setOut(new PrintStream(bas));
	String errString = bas.toString();
	System.setErr(err);
	System.setOut(out);
	int clinitIndex = errString.lastIndexOf("<clinit>");
	if (clinitIndex != -1) {
	    String specific
		= errString.substring(errString.lastIndexOf("\n", clinitIndex),
				      errString.indexOf("\n", clinitIndex));
	    Debug.print("debug.image.creation",
			"image was created from a static initializer: ",
			specific, "\n", errString);
	}
    }
}
