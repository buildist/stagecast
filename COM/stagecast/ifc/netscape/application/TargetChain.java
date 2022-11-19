/* TargetChain - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

public class TargetChain implements ExtendedTarget
{
    private Vector preTargets = new Vector();
    private Vector postTargets = new Vector();
    private static TargetChain applicationChain;
    
    private TargetChain() {
	/* empty */
    }
    
    public static TargetChain applicationChain() {
	if (applicationChain == null)
	    applicationChain = new TargetChain();
	return applicationChain;
    }
    
    public synchronized void addTarget(ExtendedTarget extendedtarget,
				       boolean bool) {
	if (bool)
	    preTargets.insertElementAt(extendedtarget, 0);
	else
	    postTargets.addElement(extendedtarget);
    }
    
    public synchronized void removeTarget(ExtendedTarget extendedtarget) {
	preTargets.removeElement(extendedtarget);
	postTargets.removeElement(extendedtarget);
    }
    
    public synchronized Target targetForCommand(String string) {
	int i = preTargets.size();
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    ExtendedTarget extendedtarget
		= (ExtendedTarget) preTargets.elementAt(i_0_);
	    if (extendedtarget.canPerformCommand(string))
		return extendedtarget;
	}
	Application application = Application.application();
	View view = application.modalView();
	if (view == null) {
	    RootView rootview = application.firstRootView();
	    if (rootview != null) {
		View view_1_ = rootview.focusedView();
		if (objectCanPerformCommand(view_1_, string))
		    return (Target) view_1_;
	    }
	    Window window = application.currentDocumentWindow();
	    if (window != null) {
		View view_2_;
		if (window instanceof InternalWindow)
		    view_2_ = ((InternalWindow) window).focusedView();
		else
		    view_2_
			= ((ExternalWindow) window).rootView().focusedView();
		if (objectCanPerformCommand(view_2_, string))
		    return (Target) view_2_;
		WindowOwner windowowner = window.owner();
		if (objectCanPerformCommand(windowowner, string))
		    return (Target) windowowner;
		if (objectCanPerformCommand(window, string))
		    return window;
	    } else {
		RootView rootview_3_;
		if ((rootview_3_ = application.mainRootView()) != null) {
		    View view_4_;
		    if (rootview_3_.mainWindow() == null)
			view_4_ = rootview_3_.focusedView();
		    else
			view_4_ = rootview_3_.rootViewFocusedView();
		    if (objectCanPerformCommand(view_4_, string))
			return (Target) view_4_;
		}
	    }
	    if (rootview != null) {
		ExternalWindow externalwindow = rootview.externalWindow();
		if (externalwindow != null) {
		    WindowOwner windowowner = externalwindow.owner();
		    if (objectCanPerformCommand(windowowner, string))
			return (Target) windowowner;
		    if (objectCanPerformCommand(externalwindow, string))
			return externalwindow;
		}
		if (objectCanPerformCommand(rootview, string))
		    return rootview;
	    }
	    if (objectCanPerformCommand(application, string))
		return (Target) application;
	} else {
	    RootView rootview;
	    View view_5_;
	    Window window;
	    if (view instanceof RootView) {
		rootview = (RootView) view;
		view_5_ = rootview.focusedView();
		window = rootview.externalWindow();
	    } else if (view instanceof InternalWindow) {
		rootview = null;
		view_5_ = ((InternalWindow) view).focusedView();
		window = (Window) view;
	    } else {
		rootview = null;
		view_5_ = view;
		window = null;
	    }
	    if (objectCanPerformCommand(view_5_, string))
		return (Target) view_5_;
	    if (window != null) {
		WindowOwner windowowner = window.owner();
		if (objectCanPerformCommand(windowowner, string))
		    return (Target) windowowner;
		if (objectCanPerformCommand(window, string))
		    return window;
	    }
	    if (rootview != null && objectCanPerformCommand(rootview, string))
		return rootview;
	}
	i = postTargets.size();
	for (int i_6_ = 0; i_6_ < i; i_6_++) {
	    ExtendedTarget extendedtarget
		= (ExtendedTarget) postTargets.elementAt(i_6_);
	    if (extendedtarget.canPerformCommand(string))
		return extendedtarget;
	}
	return null;
    }
    
    public boolean canPerformCommand(String string) {
	return targetForCommand(string) != null;
    }
    
    public void performCommand(String string, Object object) {
	Target target = targetForCommand(string);
	if (target != null)
	    target.performCommand(string, object);
    }
    
    private boolean objectCanPerformCommand(Object object, String string) {
	if (object != null && object instanceof ExtendedTarget
	    && ((ExtendedTarget) object).canPerformCommand(string))
	    return true;
	return false;
    }
}
