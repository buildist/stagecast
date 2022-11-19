/* FoundationFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;

public class FoundationFrame extends Frame
{
    ExternalWindow externalWindow;
    
    public boolean handleEvent(java.awt.Event event) {
	Application application = Application.application();
	if (event.id == 201) {
	    if (application != null
		&& application.eventLoop.shouldProcessSynchronously())
		externalWindow.hide();
	    else
		externalWindow.rootView().application()
		    .performCommandLater(externalWindow, "hide", null, false);
	    return true;
	}
	return super.handleEvent(event);
    }
    
    public ExternalWindow externalWindow() {
	return externalWindow;
    }
    
    void setExternalWindow(ExternalWindow externalwindow) {
	externalWindow = externalwindow;
    }
    
    public void layout() {
	Dimension dimension = this.size();
	Insets insets = this.insets();
	int i = insets.left;
	int i_0_ = insets.top;
	int i_1_ = dimension.width - (insets.left + insets.right);
	int i_2_ = dimension.height - (insets.top + insets.bottom);
	if (i_1_ > 0 && i_2_ > 0)
	    externalWindow.panel().reshape(i, i_0_, i_1_, i_2_);
    }
    
    public Dimension minimumSize() {
	if (externalWindow != null && externalWindow.minSize() != null) {
	    Size size = externalWindow.minSize();
	    return new Dimension(size.width, size.height);
	}
	return super.minimumSize();
    }
}
