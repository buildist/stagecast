/* PlaywriteSystem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;

import COM.stagecast.ifc.netscape.application.AWTCompatibility;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PlaywriteSystem
    implements Debug.Constants, PlaywriteSystem.Properties,
	       ResourceIDs.CommandIDs, ResourceIDs.DialogIDs
{
    private static Hashtable appProperties = new Hashtable();
    private static int _lowMemDialogLimit;
    private static boolean _lowMem;
    private static PlaywriteDialog _lowMemDialog;
    
    public static interface Properties
    {
	public static final String DRAW_SCALED = "draw_scaled";
	public static final String CACHE_SIZE = "cache_size";
	public static final String MIN_CACHE_SIZE = "min_cache_size";
	public static final String CACHE_COUNT = "cache_count";
	public static final String MIN_CACHE_COUNT = "min_cache_count";
	public static final String SCALED_DRAWING_STRATEGY
	    = "scaled_drawing_strategy";
	public static final String OFFSCREEN_BITMAP_THRESHOLD
	    = "offscreen_bitmap_threshold";
	public static final String BITMAP_MEMORY_THRESHOLD
	    = "bitmap_memory_threshold";
	public static final String WINDOW_SIZE_OVERRIDE
	    = "window_size_override";
	public static final String UNDER_DISPLACEMENT = "under_displacement";
	public static final String COVER_DISPLACEMENT = "cover_displacement";
	public static final String COPY_16BIT_SELECTION
	    = "copy_16bit_selection";
	public static final String GC_FOR_BITMAPS = "gc_for_bitmaps";
	public static final String NATIVE_STARTUP_CLASS = "native_startup";
	public static final String WORLD_DIRECTORY = "world_directory";
	public static final String MIN_APP_RAM = "min_app_ram";
	public static final String MIN_SYS_RAM = "min_sys_ram";
	public static final String LOW_RAM_THRESHOLD = "low_ram";
	public static final String SOUNDS_ENABLED = "sounds_enabled";
	public static final String APPEARANCE_EDITOR = "appearance_editor";
	public static final String STARTUP_WORLD = "startup_world";
	public static final String STARTUP_TUTORIAL = "startup_tutorial";
	public static final String MAX_WINDOWS = "max_windows";
	public static final String TEST_MAX_CYCLE = "max_cyles";
	public static final String TEST_SOUNDS_IN_RAM = "sounds_in_ram";
	public static final String DEBUG_ALL = "debug_all";
	public static final String PORT_NUMBER = "port_number";
	public static final String BATCH_COMPRESS = "batch_compress";
	public static final String DEADMAN_TIMEOUT = "deadman";
	public static final String DRAWING_ENABLED = "drawing_enabled";
	public static final String WORLD_CACHE = "drawing_enabled";
	public static final String WORLD_CANCEL_PASSWORD = "cancel_password";
	public static final String SERVER_RESTART_PASSWORD
	    = "restart_password";
	public static final String FRAME_RATE_OVERRIDE = "frame_rate_override";
	public static final String HTTP_ROOT_DIRECTORY = "http_root_directory";
	public static final String HTTP_DEFAULT_FILENAME
	    = "http_default_filename";
	public static final String PRO_VERSION = "pro_versioon";
	public static final String MULTI_USER_FILES = "multi_user";
	public static final String APPLICATION_DIR = "app_dir";
	public static final String USER_DIR = "user_dir";
	public static final String LOCALE_LANGUAGE = "locale_language";
	public static final String LOCALE_COUNTRY = "locale_country";
    }
    
    static {
	appProperties.put("draw_scaled", "DrawScaled");
	appProperties.put("cache_size", "CacheSize");
	appProperties.put("min_cache_size", "MinCacheSize");
	appProperties.put("cache_count", "CacheCount");
	appProperties.put("min_cache_count", "MinCacheCount");
	appProperties.put("scaled_drawing_strategy", "ScaledDrawingStrategy");
	appProperties.put("offscreen_bitmap_threshold",
			  "OffscreenBitmapThreshold");
	appProperties.put("bitmap_memory_threshold", "BitmapMemoryThreshold");
	appProperties.put("window_size_override", "WindowSize");
	appProperties.put("under_displacement", "UnderDisplacement");
	appProperties.put("cover_displacement", "CoverDisplacement");
	appProperties.put("copy_16bit_selection", "Copy16bitSelection");
	appProperties.put("gc_for_bitmaps", "GCNonheapBitmaps");
	appProperties.put("native_startup", "NativeStartupClass");
	appProperties.put("world_directory", "WorldDirectory");
	appProperties.put("min_app_ram", "MinAppRAM");
	appProperties.put("min_sys_ram", "MinSysRAM");
	appProperties.put("low_ram", "LowRAMThreshold");
	appProperties.put("locale_language", "LocaleLanguage");
	appProperties.put("locale_country", "LocaleCountry");
	appProperties.put("sounds_enabled", "Sounds");
	appProperties.put("appearance_editor", "AEController");
	appProperties.put("startup_world", "World");
	appProperties.put("startup_tutorial", "Tutorial");
	appProperties.put("max_windows", "MaxWindows");
	appProperties.put("frame_rate_override", "FrameRateOverride");
	appProperties.put("max_cyles", "test.maxcycle");
	appProperties.put("sounds_in_ram", "test.ramsound");
	appProperties.put("debug_all", "debug.all");
	appProperties.put("drawing_enabled", "DrawingEnabled");
	appProperties.put("port_number", "PortNumber");
	appProperties.put("batch_compress", "BatchCompress");
	appProperties.put("deadman", "Deadman");
	appProperties.put("drawing_enabled", "WorldCacheSize");
	appProperties.put("cancel_password", "WorldCancelPassword");
	appProperties.put("restart_password", "ServerRestartPassword");
	appProperties.put("http_root_directory", "HttpRootDirectory");
	appProperties.put("http_default_filename", "HttpDefaultFilename");
	appProperties.put("pro_versioon", "Pro");
	appProperties.put("multi_user", "MultiUser");
	appProperties.put("app_dir", "ApplicationDirectory");
	appProperties.put("user_dir", "UserDirectory");
	_lowMemDialogLimit = 3;
	_lowMem = false;
	_lowMemDialog = null;
    }
    
    public static final Applet getApplet() {
	return AWTCompatibility.awtApplet();
    }
    
    public static final Toolkit getToolkit() {
	return Toolkit.getDefaultToolkit();
    }
    
    public static final boolean isMacintosh() {
	return System.getProperty("os.name").equalsIgnoreCase("Mac OS");
    }
    
    public static final boolean isMacOSX() {
	return System.getProperty("os.name").equalsIgnoreCase("Mac OS X");
    }
    
    public static final boolean isMRJ_2_0() {
	return (isMacintosh()
		&& System.getProperty("java.vendor")
		       .equalsIgnoreCase("Apple Computer, Inc.")
		&& System.getProperty("java.version").equals("1.1.3"));
    }
    
    public static final boolean isMRJ_2_1_x() {
	return (isMacintosh()
		&& System.getProperty("java.vendor")
		       .equalsIgnoreCase("Apple Computer, Inc.")
		&& System.getProperty("mrj.version").startsWith("2.1"));
    }
    
    public static final boolean isJava_1_1_x() {
	return System.getProperty("java.version").startsWith("1.1");
    }
    
    public static final boolean isJava_1_2_x() {
	return System.getProperty("java.version").startsWith("1.2");
    }
    
    public static final boolean isWindows() {
	return System.getProperty("os.name").toLowerCase()
		   .startsWith("windows");
    }
    
    public static final FontMetrics getFontMetrics(Font font) {
	return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }
    
    public static final int getScreenResolution() {
	return Toolkit.getDefaultToolkit().getScreenResolution();
    }
    
    public static Size getScreenSize() {
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	if (isMRJ_2_0()) {
	    d.width -= 20;
	    d.height -= 60;
	} else if (isMacOSX())
	    d.height -= 20;
	return new Size(d.width, d.height);
    }
    
    public static final URL getCodeBase() {
	return Application.application().codeBase();
    }
    
    public static final void beep() {
	PlaywriteSound.sysBeep.play();
    }
    
    public static final void errorBeep() {
	beep();
	beep();
    }
    
    public static void checkForLowMemory() {
	do {
	    if (!_lowMem && isMacintosh() && PlaywriteRoot.isApplication()) {
		PlaywriteRoot.app();
		if (!PlaywriteRoot.isServer())
		    break;
	    }
	    return;
	} while (false);
	try {
	    if (_lowMemDialog == null) {
		_lowMemDialog
		    = new PlaywriteDialog((PlaywriteRoot.isAuthoring()
					   ? "dialog mlm" : "dialog mlmp"),
					  "command ok");
		_lowMemDialogLimit
		    = getApplicationPropertyAsInt("low_ram",
						  _lowMemDialogLimit);
	    }
	    Class memoryFunctions
		= (Class.forName
		   ("COM.stagecast.creator.MacStartup$MemoryFunctions"));
	    Method applicationMaxRAMMethod
		= memoryFunctions.getMethod("applicationMaxRAM", new Class[0]);
	    Integer freeRAM
		= ((Integer)
		   applicationMaxRAMMethod.invoke(null, new Object[0]));
	    if (freeRAM.intValue() < _lowMemDialogLimit) {
		Debug.print("debug.memory",
			    "low memory situation detected at freeRAM=",
			    freeRAM, ", invoking gc");
		BitmapManager.shrinkCache();
		Util.suggestGC();
		freeRAM
		    = (Integer) applicationMaxRAMMethod.invoke(null,
							       new Object[0]);
		if (freeRAM.intValue() < _lowMemDialogLimit && !_lowMem) {
		    Debug.print
			("debug.memory",
			 "low memory situation not recoverable. showing dialog.");
		    _lowMem = true;
		    _lowMemDialog.setLayer(200);
		    PlaywriteRoot.app().performCommandLater
			(PlaywriteRoot.app(), "show modal dialog",
			 _lowMemDialog);
		}
	    }
	} catch (Throwable t) {
	    Debug.stackTrace(t);
	}
    }
    
    public static void loadProperties() {
	if (PlaywriteRoot.isApplication()) {
	    java.io.InputStream is = null;
	    try {
		String fileName
		    = PlaywriteRoot.getShortProductName() + ".properties";
		File props
		    = new File(System.getProperty("user.home"), fileName);
		if (!props.exists()) {
		    props = new File(System.getProperty("user.dir"), fileName);
		    if (!props.exists())
			props = null;
		}
		if (props != null) {
		    is = new FileInputStream(props);
		    if (is != null)
			System.getProperties().load(is);
		}
	    } catch (SecurityException securityexception) {
		Debug.print(true, "Can't append to system properties");
	    } catch (java.io.IOException ioexception) {
		Debug.print(true, "I/O error loading properties");
	    } finally {
		if (is != null) {
		    try {
			is.close();
		    } catch (Exception exception) {
			/* empty */
		    }
		}
	    }
	}
    }
    
    public static String getSystemProperty(String key) {
	String result = null;
	Application app = Application.application();
	if (app != null && app.isApplet()) {
	    result = app.parameterNamed(key);
	    if (result != null)
		return result;
	}
	try {
	    result = System.getProperty(key);
	} catch (SecurityException securityexception) {
	    /* empty */
	}
	return result;
    }
    
    public static int getSystemPropertyAsInt(String key, int defaultValue) {
	int result = defaultValue;
	try {
	    result = Integer.getInteger(key, defaultValue).intValue();
	} catch (SecurityException securityexception) {
	    /* empty */
	}
	return result;
    }
    
    public static String getApplicationProperty(String keyIndex,
						String defaultValue) {
	String key = (String) appProperties.get(keyIndex);
	String value = getSystemProperty(key);
	if (value == null)
	    value = defaultValue;
	Debug.print("debug.world", "Application property ", key, " is ",
		    value);
	return value;
    }
    
    public static int getApplicationPropertyAsInt(String keyIndex,
						  int defaultValue) {
	String key = (String) appProperties.get(keyIndex);
	int value = getSystemPropertyAsInt(key, defaultValue);
	Debug.print("debug.world", "Application property ", key,
		    " is " + value);
	return value;
    }
    
    public static boolean getApplicationPropertyAsBoolean
	(String keyIndex, boolean defaultValue) {
	return getApplicationProperty
		   (keyIndex, defaultValue ? "true" : "false").toLowerCase
		   ().equals("true");
    }
    
    public static boolean isApplicationPropertyDefined(String keyIndex) {
	String key = (String) appProperties.get(keyIndex);
	return getSystemProperty(key) != null;
    }
}
