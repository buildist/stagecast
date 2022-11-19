/* ResizeWindowOutline - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.RootView;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.util.Vector;

public class ResizeWindowOutline
{
    Rect _bounds;
    int _resizePart;
    RootView _rootView;
    Vector _resizeWindowVector;
    int _lastX;
    int _lastY;
    Rect _newBounds = new Rect(0, 0, 0, 0);
    Size _tempSize = new Size(0, 0);
    
    public ResizeWindowOutline(Rect bounds, int resizePart,
			       RootView rootView) {
	_bounds = bounds;
	_resizePart = resizePart;
	_rootView = rootView;
	_resizeWindowVector = new Vector(4);
	_newBounds = new Rect(_bounds);
	_tempSize = new Size(_bounds.width, _bounds.height);
    }
    
    public Rect getBounds() {
	return _newBounds;
    }
    
    public boolean mouseDown(MouseEvent event) {
	_lastX = event.x + _bounds.x;
	_lastY = event.y + _bounds.y;
	makeBorderWindow(_bounds.x, _bounds.y, 1, _bounds.height, 16);
	makeBorderWindow(_bounds.maxX(), _bounds.y, 1, _bounds.height, 16);
	makeBorderWindow(_bounds.x, _bounds.y, _bounds.width, 1, 2);
	makeBorderWindow(_bounds.x, _bounds.maxY(), _bounds.width, 1, 2);
	return true;
    }
    
    private InternalWindow makeBorderWindow
	(int x, int y, int width, int height, int resizeInstruction) {
	InternalWindow window = new InternalWindow(x, y, width, height);
	window.setType(0);
	window.contentView().setTransparent(false);
	window.contentView().setBackgroundColor(Color.darkGray);
	window.setLayer(400);
	if (resizeInstruction == 2)
	    window.setHorizResizeInstruction(2);
	else
	    window.setVertResizeInstruction(16);
	window.setRootView(_rootView);
	window.show();
	_resizeWindowVector.addElement(window);
	return window;
    }
    
    public void mouseDragged(MouseEvent event) {
	mouseResizeDrag(event);
    }
    
    void mouseResizeDrag(MouseEvent event) {
	int deltaX = 0;
	int deltaY = 0;
	event.x += _bounds.x;
	event.y += _bounds.y;
	if (_resizePart == 2)
	    event.x = _lastX;
	_newBounds.setBounds(_bounds);
	if (_resizePart == 1) {
	    _newBounds.moveBy(event.x - _lastX, 0);
	    _newBounds.sizeBy(_lastX - event.x, event.y - _lastY);
	} else
	    _newBounds.sizeBy(event.x - _lastX, event.y - _lastY);
	_tempSize.sizeTo(_newBounds.width - _bounds.width,
			 _newBounds.height - _bounds.height);
	if (_resizePart == 1) {
	    if (_newBounds.x > _bounds.x + _bounds.width)
		_newBounds.moveBy(_bounds.x - _newBounds.x - _tempSize.width,
				  0);
	    else
		_newBounds.moveBy((_newBounds.width - _bounds.width
				   - _tempSize.width),
				  0);
	}
	_newBounds.sizeBy(_tempSize.width - (_newBounds.width - _bounds.width),
			  _tempSize.height - (_newBounds.height
					      - _bounds.height));
	InternalWindow window
	    = (InternalWindow) _resizeWindowVector.elementAt(0);
	deltaY = _newBounds.height - window.bounds.height;
	if (_resizePart == 1) {
	    if (deltaY < 0) {
		window = (InternalWindow) _resizeWindowVector.elementAt(1);
		window.sizeTo(1, _newBounds.height);
		window = (InternalWindow) _resizeWindowVector.elementAt(2);
		window.setBounds(_newBounds.x + 1, _newBounds.y,
				 _newBounds.width - 2, 1);
		window = (InternalWindow) _resizeWindowVector.elementAt(3);
		window.setBounds(_newBounds.x + 1, _newBounds.maxY() - 1,
				 _newBounds.width - 2, 1);
		window = (InternalWindow) _resizeWindowVector.elementAt(0);
		window.setBounds(_newBounds.x, _newBounds.y, 1,
				 _newBounds.height);
	    } else {
		window = (InternalWindow) _resizeWindowVector.elementAt(0);
		window.setBounds(_newBounds.x, _newBounds.y, 1,
				 _newBounds.height);
		window = (InternalWindow) _resizeWindowVector.elementAt(3);
		window.setBounds(_newBounds.x + 1, _newBounds.maxY() - 1,
				 _newBounds.width - 2, 1);
		window = (InternalWindow) _resizeWindowVector.elementAt(2);
		window.setBounds(_newBounds.x + 1, _newBounds.y,
				 _newBounds.width - 2, 1);
		window = (InternalWindow) _resizeWindowVector.elementAt(1);
		window.sizeTo(1, _newBounds.height);
	    }
	} else if (_resizePart == 2) {
	    if (deltaY < 0) {
		window = (InternalWindow) _resizeWindowVector.elementAt(1);
		window.sizeTo(1, _newBounds.height);
		window = (InternalWindow) _resizeWindowVector.elementAt(0);
		window.sizeTo(1, _newBounds.height);
		window = (InternalWindow) _resizeWindowVector.elementAt(3);
		window.setBounds(_newBounds.x + 1, _newBounds.maxY() - 1,
				 window.bounds.width, window.bounds.height);
	    } else {
		window = (InternalWindow) _resizeWindowVector.elementAt(3);
		window.setBounds(_newBounds.x + 1, _newBounds.maxY() - 1,
				 window.bounds.width, window.bounds.height);
		window = (InternalWindow) _resizeWindowVector.elementAt(1);
		window.sizeTo(1, _newBounds.height);
		window = (InternalWindow) _resizeWindowVector.elementAt(0);
		window.sizeTo(1, _newBounds.height);
	    }
	} else if (deltaY < 0) {
	    window = (InternalWindow) _resizeWindowVector.elementAt(0);
	    window.sizeTo(1, _newBounds.height);
	    window = (InternalWindow) _resizeWindowVector.elementAt(2);
	    window.sizeTo(_newBounds.width - 2, 1);
	    window = (InternalWindow) _resizeWindowVector.elementAt(3);
	    window.setBounds(_newBounds.x + 1, _newBounds.maxY() - 1,
			     _newBounds.width - 2, 1);
	    window = (InternalWindow) _resizeWindowVector.elementAt(1);
	    window.setBounds(_newBounds.maxX() - 1, _newBounds.y, 1,
			     _newBounds.height);
	} else {
	    window = (InternalWindow) _resizeWindowVector.elementAt(1);
	    window.setBounds(_newBounds.maxX() - 1, _newBounds.y, 1,
			     _newBounds.height);
	    window = (InternalWindow) _resizeWindowVector.elementAt(3);
	    window.setBounds(_newBounds.x + 1, _newBounds.maxY() - 1,
			     _newBounds.width - 2, 1);
	    window = (InternalWindow) _resizeWindowVector.elementAt(2);
	    window.sizeTo(_newBounds.width - 2, 1);
	    window = (InternalWindow) _resizeWindowVector.elementAt(0);
	    window.sizeTo(1, _newBounds.height);
	}
    }
    
    public void mouseUp(MouseEvent event) {
	if (_resizePart != 0) {
	    int i = _resizeWindowVector.count();
	    while (i-- > 0)
		((InternalWindow) _resizeWindowVector.elementAt(i)).hide();
	    _resizeWindowVector.removeAllElements();
	    _resizePart = 0;
	}
    }
}
