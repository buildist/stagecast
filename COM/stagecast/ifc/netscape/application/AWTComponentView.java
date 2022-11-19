/* AWTComponentView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.Component;

public class AWTComponentView extends View
{
    Component component;
    RootView rootView;
    
    public AWTComponentView() {
	this(0, 0, 0, 0);
    }
    
    public AWTComponentView(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public AWTComponentView(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
    }
    
    void setComponentBounds() {
	Rect rect = _superview.convertRectToView(null, bounds);
	component.reshape(rect.x, rect.y, rect.width, rect.height);
    }
    
    void addComponent() {
	if (rootView != null)
	    rootView.addComponentView(this);
    }
    
    void removeComponent() {
	if (component != null && rootView != null)
	    rootView.removeComponentView(this);
    }
    
    public void setAWTComponent(Component component) {
	removeComponent();
	this.component = component;
	addComponent();
    }
    
    public Component awtComponent() {
	return component;
    }
    
    protected void ancestorWillRemoveFromViewHierarchy(View view) {
	removeComponent();
	rootView = null;
    }
    
    protected void ancestorWasAddedToViewHierarchy(View view) {
	rootView = this.rootView();
	addComponent();
    }
}
