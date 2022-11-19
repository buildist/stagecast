/* MultiDragView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Vector;

public class MultiDragView extends PlaywriteView implements Visible
{
    private Vector _pwViews;
    
    private class MultiDragBitmap extends Bitmap
    {
	Image[] _images;
	int[] _locX;
	int[] _locY;
	int _width;
	int _height;
	
	public MultiDragBitmap(Image[] images, int[] locX, int[] locY,
			       int width, int height) {
	    _images = images;
	    _locX = locX;
	    _locY = locY;
	    _width = width;
	    _height = height;
	}
	
	public void drawAt(Graphics g, int x, int y) {
	    for (int i = 0; i < _images.length; i++)
		_images[i].drawAt(g, x + _locX[i], y + _locY[i]);
	}
	
	public int height() {
	    return _height;
	}
	
	public int width() {
	    return _width;
	}
    }
    
    MultiDragView(Vector playwriteViews, PlaywriteView selectedView) {
	_pwViews = playwriteViews;
	init(selectedView);
    }
    
    private void init(PlaywriteView selectedView) {
	Rect viewRect = new Rect(selectedView.bounds);
	for (int i = 0; i < _pwViews.size(); i++) {
	    PlaywriteView v = (PlaywriteView) _pwViews.elementAt(i);
	    viewRect.unionWith(v.bounds);
	}
	this.setBounds(viewRect);
	Point dp = selectedView.getDragPoint();
	this.setDragPoint(dp.x + selectedView.bounds.x - this.x(),
			  dp.y + selectedView.bounds.y - this.y());
	this.setModelObject(this);
    }
    
    public Vector getPWViews() {
	return _pwViews;
    }
    
    public void drawView(Graphics g) {
	for (int i = 0; i < _pwViews.size(); i++) {
	    PlaywriteView pwv = (PlaywriteView) _pwViews.elementAt(i);
	    pwv.getDragImage().drawAt(g, pwv.bounds.x - bounds.x,
				      pwv.bounds.y - bounds.y);
	}
    }
    
    public Class getItemModelClass() {
	PlaywriteView pwv = (PlaywriteView) _pwViews.firstElement();
	return pwv.getModelObject().getClass();
    }
    
    public Image createDragImage() {
	int size = _pwViews.size();
	Image[] images = new Image[size];
	int[] locX = new int[size];
	int[] locY = new int[size];
	for (int i = 0; i < size; i++) {
	    PlaywriteView v = (PlaywriteView) _pwViews.elementAt(i);
	    images[i] = v.getDragImage();
	    locX[i] = v.bounds.x - bounds.x;
	    locY[i] = v.bounds.y - bounds.y;
	}
	return new MultiDragBitmap(images, locX, locY, bounds.width,
				   bounds.height);
    }
    
    public void setVisibility(boolean visible) {
	if (((PlaywriteView) _pwViews.firstElement()).getModelObject()
	    instanceof Visible) {
	    for (int i = 0; i < _pwViews.size(); i++) {
		CharacterView cv = (CharacterView) _pwViews.elementAt(i);
		cv.getCharacter().setVisibility(visible);
	    }
	    this.superview();
	}
    }
    
    public boolean isVisible() {
	if (((PlaywriteView) _pwViews.firstElement()).getModelObject()
	    instanceof Visible)
	    return ((Visible)
		    ((PlaywriteView) _pwViews.firstElement()).getModelObject())
		       .isVisible();
	return true;
    }
}
