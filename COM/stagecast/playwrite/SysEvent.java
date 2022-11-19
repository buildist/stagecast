/* SysEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Target;

public class SysEvent
{
    public static final String EVENT_OPEN_FILE = "SysEvent OPEN".intern();
    public static final String EVENT_PRINT_FILE = "SysEvent PRINT".intern();
    public static final String EVENT_ABOUT_APP = "SysEvent ABOUT".intern();
    public static final String EVENT_EXIT_APP = "SysEvent EXIT".intern();
    private static PlatformEventHandler platformHandler = null;
    
    public static interface PlatformEventHandler
    {
	public void setTargetFor(String string, Target target);
    }
    
    static void initialize() {
	String className
	    = (PlaywriteSystem.isMacintosh() || PlaywriteSystem.isMacOSX()
	       ? "COM.stagecast.creator.MacEvent" : "");
	if (className.length() > 0
	    && !className.toUpperCase().equals("NONE")) {
	    try {
		Class eventClass = Class.forName(className);
		platformHandler
		    = (PlatformEventHandler) eventClass.newInstance();
	    } catch (Throwable t) {
		Debug.stackTrace(t);
	    }
	}
    }
    
    public static void setTargetFor(String event, Target target) {
	if (platformHandler != null)
	    platformHandler.setTargetFor(event.intern(), target);
    }
}
