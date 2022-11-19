/* FoundationAppletStub - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

class FoundationAppletStub implements AppletStub, AppletContext
{
    URL documentBase;
    URL codeBase;
    
    public boolean isActive() {
	return true;
    }
    
    public URL getDocumentBase() {
	if (documentBase == null) {
	    try {
		documentBase = new URL("file:"
				       + System.getProperty("user.dir")
					     .replace(File.separatorChar, '/')
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
		codeBase = new URL("file:"
				   + System.getProperty("user.dir")
					 .replace(File.separatorChar, '/')
				   + "/");
	    } catch (Exception exception) {
		/* empty */
	    }
	}
	return codeBase;
    }
    
    public String getParameter(String string) {
	return null;
    }
    
    public AppletContext getAppletContext() {
	return this;
    }
    
    public void appletResize(int i, int i_0_) {
	/* empty */
    }
    
    public AudioClip getAudioClip(URL url) {
	return null;
    }
    
    public java.awt.Image getImage(URL url) {
	return AWTCompatibility.awtToolkit().getImage(url);
    }
    
    public Applet getApplet(String string) {
	return null;
    }
    
    public Enumeration getApplets() {
	Vector vector = new Vector();
	vector.addElement(AWTCompatibility.awtApplet());
	return vector.elements();
    }
    
    public void showDocument(URL url) {
	/* empty */
    }
    
    public void showDocument(URL url, String string) {
	/* empty */
    }
    
    public void showStatus(String string) {
	/* empty */
    }

    public void setStream(String key, InputStream stream) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public InputStream getStream(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Iterator getStreamKeys() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
