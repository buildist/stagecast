/* NormalSubtotal - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.ASSERT;
import COM.stagecast.playwrite.PlaywriteView;
import COM.stagecast.playwrite.TitledObjectView;
import COM.stagecast.playwrite.ValueView;
import COM.stagecast.playwrite.ViewManager;

public class NormalSubtotal extends SubtotalObject
    implements ViewManager.ViewUpdater, ViewManager.Owner,
	       ViewManager.ViewRemover
{
    public static final int storeVersion = 0;
    public static final long serialVersionUID = -3819410108751283506L;
    static final Object UPDATE_NAME = "update name";
    static final Object UPDATE_CONTENT = "update content";
    static final Object UPDATE_COLORS = "update colors";
    private static int BASE = 256;
    private static Color BASE_COLOR = Color.gray;
    private static int COLOR_INC = 32;
    private static int COLOR_COUNT = 0;
    private static Vector COLOR_CACHE = new Vector();
    private transient ViewManager _viewManager;
    private transient ValueView.SetterGetter _vsg;
    private Color _color;
    
    private static Color computeNextColor() {
	if (COLOR_CACHE.size() > 0)
	    return (Color) COLOR_CACHE.removeFirstElement();
	int add = (COLOR_COUNT / 6 + 1) * COLOR_INC;
	int type = COLOR_COUNT % 6 + 1;
	int bon = type & 0x1;
	int gon = (type & 0x2) >> 1;
	int ron = (type & 0x4) >> 2;
	int b = bon * (BASE - add * bon);
	int g = gon * (BASE - add * gon);
	int r = ron * (BASE - add * ron);
	if (r == 0)
	    r = add;
	if (g == 0)
	    g = add;
	if (b == 0)
	    b = add;
	COLOR_COUNT++;
	return new Color(r, g, b);
    }
    
    public NormalSubtotal(Subtotal.Creator expression) {
	super(expression);
	String nameString = "";
	setName(nameString);
	reevaluate();
    }
    
    public NormalSubtotal() {
	/* empty */
    }
    
    public Object clone() {
	NormalSubtotal newSubtotalObject = null;
	newSubtotalObject = (NormalSubtotal) super.clone();
	newSubtotalObject._vsg = null;
	newSubtotalObject._viewManager = null;
	return newSubtotalObject;
    }
    
    public PlaywriteView createView() {
	if (_viewManager == null) {
	    _viewManager = new ViewManager(this);
	    _viewManager.setViewRemover(this);
	}
	if (_vsg == null)
	    _vsg = new ValueView.DisplayOnlySetterGetter() {
		public Object getValue() {
		    return NormalSubtotal.this.getResultAsString();
		}
	    };
	TitledObjectView view
	    = new TitledObjectView(this, _color == null ? BASE_COLOR : _color);
	view.initializeContentView(_vsg);
	view.setModelObject(this);
	view.sizeToMinSize();
	view.setViewManager(_viewManager);
	if (_viewManager.getViewCount() == 2) {
	    if (_color == null)
		_color = computeNextColor();
	    _viewManager.updateViews(this, "update colors");
	}
	return view;
    }
    
    public void reevaluate() {
	super.reevaluate();
	updateAllContentViews();
    }
    
    public void setName(String name) {
	super.setName(name);
	updateAllNameViews();
    }
    
    public void setViewManager(ViewManager viewManager) {
	ASSERT.isTrue(_viewManager == null || viewManager == null);
	_viewManager = viewManager;
    }
    
    public ViewManager getViewManager() {
	return _viewManager;
    }
    
    public void viewRemoved(Object view, int size) {
	if (size == 1) {
	    COLOR_CACHE.addElement(_color);
	    _color = BASE_COLOR;
	    _viewManager.updateViews(this, "update colors");
	    _color = null;
	}
    }
    
    public void updateView(Object view, Object value) {
	TitledObjectView tov = (TitledObjectView) view;
	if (value == "update name")
	    tov.updateNameView();
	else if (value == "update content")
	    tov.updateContentView();
	else if (value == "update colors") {
	    tov.setTitleColor(_color);
	    tov.setDirty(true);
	    tov.setDragImageDirty();
	}
    }
    
    public void updateAllNameViews() {
	if (_viewManager != null)
	    _viewManager.updateViews(this, "update name");
    }
    
    public void updateAllContentViews() {
	if (_viewManager != null)
	    _viewManager.updateViews(this, "update content");
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
	if (this.getName() == null)
	    out.writeUTF("");
	else
	    out.writeUTF(this.getName());
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	super.readExternal(in);
	String nameString = in.readUTF();
	setName(nameString);
    }
}
