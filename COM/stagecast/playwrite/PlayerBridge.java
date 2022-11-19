/* PlayerBridge - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.File;

public abstract class PlayerBridge extends PlaywriteRoot
{
    private static final boolean CUSTOMER_FLAG = true;
    private static final String RELEASE_TYPE = "f";
    private static final int CONFIG_FLAGS = 0;
    private static final boolean REQUIRE_SERIAL_NO = false;
    private static final String VERSION_MAJOR = "2";
    private static final String VERSION_MINOR = "0";
    private static final String VERSION_REVISION = "2";
    private static final String RELEASE_NUMBER = "1";
    private static final String CANDIDATE_NUMBER = "0";
    private static final String BUILD_STRING = "2007.07.18";
    
    public static final String getCompatibleVersion() {
	return "2.0";
    }
    
    static String getStaticVersionNumber() {
	String result = getCompatibleVersion() + "." + "2";
	if ("f" != "f")
	    result += "f1";
	return result;
    }
    
    protected String _getVersionString() {
	Object[] params = { PlaywriteRoot.getVersionNumber() };
	return Resource.getTextAndFormat("root vn", params);
    }
    
    protected String _getCompatibleVersionNumber() {
	return PlaywriteRoot.getCompatibleVersionNumber();
    }
    
    protected String _getVersionNumber() {
	return getStaticVersionNumber();
    }
    
    protected String _getProductName() {
	return Resource.getText("root ppn");
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
	return 0;
    }
    
    protected boolean _isSerialProtected() {
	return false;
    }
    
    protected final boolean getAuthoringFlag() {
	return false;
    }
    
    protected void applicationExec() {
	if (_initialWorld != null) {
	    this.performCommandLater(this, "OSW",
				     new File(_initialWorld
						  .getAbsolutePath()));
	    _initialWorld = null;
	} else
	    this.enableSplashScreen();
    }
    
    public String toString() {
	return _getVersionNumber();
    }
}
