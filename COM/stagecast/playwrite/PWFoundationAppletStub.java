/* PWFoundationAppletStub - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import COM.stagecast.ifc.netscape.application.AWTCompatibility;

public class PWFoundationAppletStub implements AppletStub, AppletContext
{
    URL documentBase;
    URL codeBase;
    
    public boolean isActive() {
	return true;
    }
    
    public URL getDocumentBase() {
	if (documentBase == null) {
	    try {
		documentBase
		    = new URL("file:"
			      + FileIO.getAppDir().replace(File.separatorChar,
							   '/')
			      + "/");
	    } catch (Exception exception) {
		/* empty */
	    }
	}
	return documentBase;
    }
    
    public URL getCodeBase() {
	if (codeBase == null) {
	    try {
		codeBase
		    = new URL("file:"
			      + FileIO.getAppDir().replace(File.separatorChar,
							   '/')
			      + "/");
	    } catch (Exception exception) {
		/* empty */
	    }
	}
	return codeBase;
    }
    
    public String getParameter(String name) {
	return null;
    }
    
    public AppletContext getAppletContext() {
	return this;
    }
    
    public void appletResize(int width, int height) {
	/* empty */
    }
    
    public AudioClip getAudioClip(URL url) {
	return null;
    }
    
    public Image getImage(URL url) {
	return AWTCompatibility.awtToolkit().getImage(url);
    }
    
    public Applet getApplet(String name) {
	return null;
    }
    
    public Enumeration getApplets() {
	Vector applets = new Vector();
	applets.addElement(AWTCompatibility.awtApplet());
	return applets.elements();
    }
    
    public void showDocument(URL url) {
	/* empty */
    }
    
    public void showDocument(URL url, String target) {
	/* empty */
    }
    
    public void showStatus(String status) {
	/* empty */
    }
}
