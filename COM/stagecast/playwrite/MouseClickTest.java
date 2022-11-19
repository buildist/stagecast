/* MouseClickTest - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class MouseClickTest extends RuleTest
    implements Externalizable, ResourceIDs.RuleEditorIDs,
	       ResourceIDs.SummaryIDs, Watcher
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108754953522L;
    private static Bitmap mouseImage = null;
    private Shape _shape = null;
    
    final Shape getShape() {
	return _shape;
    }
    
    MouseClickTest(BeforeBoard bboard) {
	GeneralizedCharacter self = bboard.getSelfGC();
	_shape = new Shape(bboard.numberOfColumns(), bboard.numberOfRows(),
			   new Point(self.getH(), self.getV()), false);
    }
    
    /**
     * @deprecated
     */
    MouseClickTest(int nCols, int nRows, int dH, int dV) {
	_shape = new Shape(nCols, nRows, null, false);
	setLocation(dH, dV, true);
    }
    
    public MouseClickTest() {
	/* empty */
    }
    
    void setLocation(int dH, int dV, boolean flag) {
	_shape.setLocationDeltaHV(dH, dV, flag);
	if (this.getView() != null)
	    this.getView().setDirty();
    }
    
    boolean getLocation(int dH, int dV) {
	return _shape.getLocationDeltaHV(dH, dV);
    }
    
    public boolean evaluate(CharacterInstance self) {
	Vector events = self.getWorld().getActiveEvents();
	int myH = self.getH();
	int myV = self.getV();
	synchronized (events) {
	    int n = events.size();
	    for (int i = 0; i < n; i++) {
		PlaywriteEvent event = (PlaywriteEvent) events.elementAt(i);
		if (event.isMouseEvent()
		    && self.getContainer() == event.getStage()) {
		    int dH = event.getH() - myH;
		    int dV = event.getV() - myV;
		    if (_shape.getSafeLocationDeltaHV(dH, dV))
			return true;
		}
	    }
	    return false;
	}
    }
    
    public PlaywriteView createView() {
	this.getRule().getBeforeBoard().addGrowthWatcher(this);
	if (mouseImage == null)
	    mouseImage = Resource.getImage("mouse test image");
	View[] subviews
	    = { new PlaywriteView(mouseImage),
		new LocationView(this.getRule().getBeforeBoard(), _shape) };
	PlaywriteView view = new LineView(this, 8, "mouse test xfmt", null,
					  subviews) {
	    public void discard() {
		super.discard();
		BeforeBoard beforeBoard
		    = MouseClickTest.this.getRule().getBeforeBoard();
		if (beforeBoard != null)
		    beforeBoard.removeGrowthWatcher(MouseClickTest.this);
	    }
	};
	return view;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	MouseClickTest newTest = (MouseClickTest) map.get(this);
	if (newTest != null)
	    return newTest;
	newTest = new MouseClickTest();
	map.put(this, newTest);
	newTest._shape = (Shape) _shape.clone();
	return newTest;
    }
    
    public void update(Object target, Object value) {
	int dx = ((BeforeBoard) target).numberOfColumns() - _shape.getWidth();
	int dy = ((BeforeBoard) target).numberOfRows() - _shape.getHeight();
	if (value == BeforeBoard.DOWN)
	    _shape.growDownBy(dy);
	else if (value == BeforeBoard.LEFT)
	    _shape.growLeftBy(dx);
	else
	    _shape.changeSize(_shape.getWidth() + dx, _shape.getHeight() + dy);
    }
    
    public void summarize(Summary s) {
	s.writeText(Resource.getText("SUM mct"));
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(_shape);
	out.writeObject(_shape);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_shape = (Shape) in.readObject();
    }
}
