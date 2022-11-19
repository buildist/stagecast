/* Debug - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.PrintWriter;
import java.util.Date;

public class Debug
{
    private static final boolean MASTER_DEBUG_SWITCH = true;
    private static boolean debugAll
	= PlaywriteSystem.getApplicationPropertyAsBoolean("debug_all", false);
    private static PrintWriter logStream = null;
    
    public static interface Constants
    {
	public static final String DEBUG_AFTER_BOARD = "debug.afterboard";
	public static final String DEBUG_APPEARANCE = "debug.appearance";
	public static final String DEBUG_AUTO_SCROLL = "debug.autoscroll";
	public static final String DEBUG_BUTTON = "debug.button";
	public static final String DEBUG_BOARDVIEW = "debug.boardview";
	public static final String DEBUG_CHARACTER = "debug.character";
	public static final String DEBUG_CHAR_WINDOW
	    = "debug.character.window";
	public static final String DEBUG_COMMANDS = "debug.commands";
	public static final String DEBUG_CONSISTENCY = "debug.consistency";
	public static final String DEBUG_CONTROL_PANEL = "debug.control.panel";
	public static final String DEBUG_COPY = "debug.copy";
	public static final String DEBUG_DEBUGGING = "debug.debugging";
	public static final String DEBUG_DOOR = "debug.door";
	public static final String DEBUG_DR = "debug.dr";
	public static final String DEBUG_EXAMINE = "debug.examine";
	public static final String DEBUG_FINAL = "debug.final";
	public static final String DEBUG_FTP = "debug.ftp";
	public static final String DEBUG_GC = "debug.gc";
	public static final String DEBUG_GERMAN_VERSION = "debug.german";
	public static final String DEBUG_HELP = "debug.help";
	public static final String DEBUG_IMAGE = "debug.image";
	public static final String DEBUG_IMAGE_CREATION
	    = "debug.image.creation";
	public static final String DEBUG_IMAGE_PRODUCER
	    = "debug.image.producer";
	public static final String DEBUG_INDEXEDCONTAINER
	    = "debug.indexed.container";
	public static final String DEBUG_I18N = "debug.internationalization";
	public static final String DEBUG_JAR = "debug.jar";
	public static final String DEBUG_LEGO = "debug.lego";
	public static final String DEBUG_LOADER = "debug.loader";
	public static final String DEBUG_MEMORY = "debug.memory";
	public static final String DEBUG_FATAL_ERROR_ON_RUN
	    = "debug.fatalerroronrun";
	public static final String DEBUG_OBJ_STORE = "debug.objectstore";
	public static final String DEBUG_OBJ_STORE_DETAIL
	    = "debug.objectstore.detail";
	public static final String DEBUG_OBJ_STORE_MEDIA
	    = "debug.objectstore.media";
	public static final String DEBUG_OPERATIONS = "debug.operations";
	public static final String DEBUG_OP_MGR = "debug.operation.manager";
	public static final String DEBUG_PLUGINS = "debug.plugins";
	public static final String DEBUG_POPUP = "debug.popup";
	public static final String DEBUG_PWWINDOW = "debug.pwwindow";
	public static final String DEBUG_QUICKEXIT = "debug.quickexit";
	public static final String DEBUG_RHINO = "debug.rhino";
	public static final String DEBUG_RULE = "debug.rule";
	public static final String DEBUG_RULE_ACTION = "debug.rule.action";
	public static final String DEBUG_RULE_EDIT = "debug.rule.editor";
	public static final String DEBUG_RULE_TEST = "debug.rule.test";
	public static final String DEBUG_RULE_XLATE = "debug.rule.translator";
	public static final String DEBUG_SCRAP = "debug.scrap";
	public static final String DEBUG_SERVER = "debug.server";
	public static final String DEBUG_SHAPE = "debug.shape";
	public static final String DEBUG_SIDELINES = "debug.sidelines";
	public static final String DEBUG_SLOT = "debug.slot";
	public static final String DEBUG_SOUND = "debug.sound";
	public static final String DEBUG_STATISTICS = "debug.statistics";
	public static final String DEBUG_SUBROUTINE = "debug.subroutine";
	public static final String DEBUG_SUB_SCRAP = "debug.subroutine.scrap";
	public static final String DEBUG_SWITCH_STAGE = "debug.switch.stage";
	public static final String DEBUG_TEMP = "debug.temp";
	public static final String DEBUG_TEXT_CHAR = "debug.text.character";
	public static final String DEBUG_TOOL = "debug.tool";
	public static final String DEBUG_TRACKPOINT = "debug.trackpoint";
	public static final String DEBUG_TUTORIAL = "debug.tutorial";
	public static final String DEBUG_TUTORIAL_AGENT
	    = "debug.tutorial.agent";
	public static final String DEBUG_VARIABLE = "debug.variable";
	public static final String DEBUG_VIEW = "debug.view";
	public static final String DEBUG_WORLD = "debug.world";
    }
    
    public static final void print(boolean flag, Object o1, Object o2,
				   Object o3, Object o4, Object o5,
				   Object o6) {
	if (flag) {
	    System.out.print(o1);
	    System.out.print(o2);
	    System.out.print(o3);
	    System.out.print(o4);
	    System.out.print(o5);
	    System.out.print(o6);
	    System.out.println();
	    System.out.flush();
	    if (logStream != null) {
		logStream.print(o1);
		logStream.print(o2);
		logStream.print(o3);
		logStream.print(o4);
		logStream.print(o5);
		logStream.print(o6);
		logStream.println();
		logStream.flush();
	    }
	}
    }
    
    public static final void print(String flag, char output) {
	if (lookup(flag))
	    System.out.print(output);
    }
    
    public static final void stackTrace(String flag, Throwable t) {
	if (lookup(flag))
	    stackTrace(t);
    }
    
    public static final void stackTrace(Throwable t) {
	t.printStackTrace(System.out);
	print(true, new Date(), ": ", PlaywriteRoot.getProductName(), " ",
	      PlaywriteRoot.getVersionString());
	System.out.flush();
	if (logStream != null) {
	    t.printStackTrace(logStream);
	    logStream.flush();
	}
    }
    
    public static final void stackTrace() {
	Thread.dumpStack();
	print(true, PlaywriteRoot.getProductName(), " ",
	      PlaywriteRoot.getVersionString());
	System.out.flush();
    }
    
    public static final void stackTrace(String flag) {
	if (lookup(flag))
	    stackTrace();
    }
    
    public static final boolean lookup(String flag) {
	String result = PlaywriteSystem.getSystemProperty(flag);
	if (result == null)
	    return debugAll;
	return result.toLowerCase().equals("true");
    }
    
    private static final String kb(long bytes) {
	return (int) (bytes / 1024L) + "K";
    }
    
    public static final String mem() {
	long fm = Runtime.getRuntime().freeMemory();
	long tm = Runtime.getRuntime().totalMemory();
	long used = tm - fm;
	return "Memory: " + kb(used) + "/" + kb(tm);
    }
    
    public static final String mem(boolean flag) {
	if (flag)
	    return mem();
	return "";
    }
    
    public static final String mem(String flag) {
	if (lookup(flag))
	    return mem();
	return "";
    }
    
    public static final void print(String flag, Object o1) {
	print(lookup(flag), o1, "", "", "", "", "");
    }
    
    public static final void print(String flag, Object o1, Object o2) {
	print(lookup(flag), o1, o2, "", "", "", "");
    }
    
    public static final void print(String flag, Object o1, Object o2,
				   Object o3) {
	print(lookup(flag), o1, o2, o3, "", "", "");
    }
    
    public static final void print(String flag, Object o1, Object o2,
				   Object o3, Object o4) {
	print(lookup(flag), o1, o2, o3, o4, "", "");
    }
    
    public static final void print(String flag, Object o1, Object o2,
				   Object o3, Object o4, Object o5) {
	print(lookup(flag), o1, o2, o3, o4, o5, "");
    }
    
    public static final void print(String flag, Object o1, Object o2,
				   Object o3, Object o4, Object o5,
				   Object o6) {
	print(lookup(flag), o1, o2, o3, o4, o5, o6);
    }
    
    public static final void print(Debuggable d, Object o1) {
	print(d.isDebuggingOn(), o1, "", "", "", "", "");
    }
    
    public static final void print(Debuggable d, Object o1, Object o2) {
	print(d.isDebuggingOn(), o1, o2, "", "", "", "");
    }
    
    public static final void print(Debuggable d, Object o1, Object o2,
				   Object o3) {
	print(d.isDebuggingOn(), o1, o2, o3, "", "", "");
    }
    
    public static final void print(Debuggable d, Object o1, Object o2,
				   Object o3, Object o4) {
	print(d.isDebuggingOn(), o1, o2, o3, o4, "", "");
    }
    
    public static final void print(Debuggable d, Object o1, Object o2,
				   Object o3, Object o4, Object o5) {
	print(d.isDebuggingOn(), o1, o2, o3, o4, o5, "");
    }
    
    public static final void print(Debuggable d, Object o1, Object o2,
				   Object o3, Object o4, Object o5,
				   Object o6) {
	print(d.isDebuggingOn(), o1, o2, o3, o4, o5, o6);
    }
    
    public static final void print(boolean flag, Object o1) {
	print(flag, o1, "", "", "", "", "");
    }
    
    public static final void print(boolean flag, Object o1, Object o2) {
	print(flag, o1, o2, "", "", "", "");
    }
    
    public static final void print(boolean flag, Object o1, Object o2,
				   Object o3) {
	print(flag, o1, o2, o3, "", "", "");
    }
    
    public static final void print(boolean flag, Object o1, Object o2,
				   Object o3, Object o4) {
	print(flag, o1, o2, o3, o4, "", "");
    }
    
    public static final void print(boolean flag, Object o1, Object o2,
				   Object o3, Object o4, Object o5) {
	print(flag, o1, o2, o3, o4, o5, "");
    }
    
    public static final void print(String flag, Object o1, int i) {
	print(flag, o1, new Integer(i), "", "", "", "");
    }
    
    public static final void print(String flag, Object o1, boolean b) {
	print(flag, o1, new Boolean(b), "", "", "", "");
    }
}
