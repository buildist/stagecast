/* WindowInvalidationAgent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class WindowInvalidationAgent implements Target
{
    ExternalWindow window;
    
    WindowInvalidationAgent(ExternalWindow externalwindow) {
	window = externalwindow;
    }
    
    void run() {
	Application application = Application.application();
	if (application != null)
	    application.performCommandLater(this, "invalidate", null, false);
    }
    
    public void performCommand(String string, Object object) {
	window.invalidateAWTWindow();
    }
}
