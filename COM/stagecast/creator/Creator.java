/* Creator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.creator;
import java.io.File;

import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.playwrite.PlayerBridge;
import COM.stagecast.playwrite.PlaywriteRoot;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.Tutorial;
import COM.stagecast.playwrite.World;
import COM.stagecast.playwrite.WorldWindow;

public class Creator extends PlaywriteRoot
{
    private static final boolean CUSTOMER_FLAG = true;
    private static final String RELEASE_TYPE = "f";
    private static final int CONFIG_FLAGS = 5;
    private static final boolean REQUIRE_SERIAL_NO = false;
    static final String VERSION_MAJOR = "2";
    static final String VERSION_MINOR = "0";
    static final String VERSION_REVISION = "2";
    static final String RELEASE_NUMBER = "1";
    static final String CANDIDATE_NUMBER = "0";
    static final String BUILD_STRING = "2007.07.18";
    
    protected String _getVersionString() {
	Object[] params = { PlaywriteRoot.getVersionNumber() };
	return Resource.getTextAndFormat("root vn", params);
    }
    
    protected String _getCompatibleVersionNumber() {
	return PlayerBridge.getCompatibleVersion();
    }
    
    protected String _getVersionNumber() {
	String result = "2.0.2";
	if ("f" != "f")
	    result += "f1";
	return result;
    }
    
    protected String _getProductName() {
	return Resource.getText("root cpn");
    }
    
    protected String _getProductType() {
	String token;
	if (PlaywriteRoot.isFinalBuild()) {
	    if (PlaywriteRoot.hasDemoRestrictions())
		token = "root ptd";
	    else
		token = "root pts";
	} else
	    token = "root ptp";
	return Resource.getText(token);
    }
    
    protected String _getReleaseType() {
	return "f";
    }
    
    protected String _getBuildString() {
	return Resource.getTextAndFormat("root bn",
					 new Object[] { "2007.07.18" });
    }
    
    protected boolean _isCustomerBuild() {
	return true;
    }
    
    protected int _getEvalFlags() {
	return 5;
    }
    
    protected boolean _isSerialProtected() {
	return false;
    }
    
    protected boolean getAuthoringFlag() {
	return true;
    }
    
    protected int getMaxOpenWorlds() {
	return PlaywriteRoot.isProfessional() ? 42 : 2;
    }
    
    protected String preferredAppearanceEditor() {
	return "COM.stagecast.playwrite.DefaultAEController";
    }
    
    protected void layoutWorlds() {
	if (this.numberOfWorlds() < 2)
	    super.layoutWorlds();
	else if (PlaywriteRoot.isProfessional()) {
	    Size screenSize = PlaywriteRoot.getRootWindowSize();
	    World world = (World) _worlds.elementAt(_worlds.size() - 2);
	    WorldWindow worldWindow = world.getWindow();
	    Rect prevBounds = worldWindow.bounds();
	    world = (World) _worlds.elementAt(_worlds.size() - 1);
	    worldWindow = world.getWindow();
	    Rect newBounds = worldWindow.getUserBounds();
	    newBounds.x = prevBounds.x + 20;
	    newBounds.y = prevBounds.y + 20;
	    if (newBounds.x + 100 > screenSize.height
		|| newBounds.y + 100 > screenSize.width) {
		newBounds.x = 0;
		newBounds.y = 0;
	    }
	    newBounds.height
		= Math.min(newBounds.height, screenSize.height - newBounds.y);
	    newBounds.width
		= Math.min(newBounds.width, screenSize.width - newBounds.x);
	    worldWindow.setSystemBounds(newBounds.x, newBounds.y,
					newBounds.width, newBounds.height);
	} else {
	    int buffer = 10;
	    Size screenSize = PlaywriteRoot.getRootWindowSize();
	    int n = _worlds.size();
	    int availableWidth;
	    if (PlaywriteRoot.isPlayer() || Tutorial.getTutorial() != null
		|| n > 1)
		availableWidth = screenSize.width - (n - 1) * buffer;
	    else
		availableWidth = (screenSize.width - (n - 1) * buffer
				  - screenSize.width / 5);
	    if (n == 1
		&& (((World) _worlds.elementAt(0)).getWindow().bounds.width
		    > availableWidth))
		availableWidth = screenSize.width;
	    int windowX = 0;
	    for (int i = 0; i < n; i++) {
		World world = (World) _worlds.elementAt(i);
		WorldWindow worldWindow = world.getWindow();
		boolean changed = false;
		Rect newBounds = worldWindow.bounds();
		if (newBounds.x != windowX) {
		    newBounds.x = windowX;
		    changed = true;
		}
		if (newBounds.y != 0) {
		    newBounds.y = 0;
		    changed = true;
		}
		int windowWidth;
		if (worldWindow.bounds.width < availableWidth / (n - i))
		    windowWidth = worldWindow.bounds.width;
		else if (world.desiredWidth() < availableWidth / (n - i))
		    windowWidth = world.desiredWidth();
		else
		    windowWidth = availableWidth / (n - i);
		if (newBounds.width != windowWidth) {
		    newBounds.width = windowWidth;
		    changed = true;
		}
		int windowHeight;
		if (worldWindow.bounds.height < screenSize.height)
		    windowHeight = worldWindow.bounds.height;
		else if (world.desiredHeight() < screenSize.height)
		    windowHeight = world.desiredHeight();
		else
		    windowHeight = screenSize.height;
		if (newBounds.height != windowHeight) {
		    newBounds.height = windowHeight;
		    changed = true;
		}
		availableWidth -= newBounds.width;
		if (changed)
		    worldWindow.setSystemBounds(newBounds.x, newBounds.y,
						newBounds.width,
						newBounds.height);
		windowX = worldWindow.bounds.maxX() + buffer;
	    }
	}
    }
    
    public static void main(String[] args) {
	new Creator().main(args, 41497);
    }
    
    protected void applicationExec() {
	if (_initialWorld != null) {
	    this.performCommandLater(this, "OSW",
				     new File(_initialWorld
						  .getAbsolutePath()));
	    _initialWorld = null;
	} else if (PlaywriteRoot.isProfessional())
	    this.performCommand("NW", null);
	else
	    this.enableSplashScreen();
    }
}
