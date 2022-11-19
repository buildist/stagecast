/* ProgressDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;

class ProgressDialog extends InternalWindow
{
    Color _bgColor;
    Color _fillColor;
    int _percentDone;
    int _inset;
    Bitmap _overlay;
    PercentView _view;
    float _total = 0.0F;
    float _done = 0.0F;
    
    private class PercentView extends View
    {
	Color _bgColor;
	Color _fillColor;
	float _percentDone;
	Image _overlay;
	View _redraw;
	Bitmap _imageCache;
	
	PercentView(int width, int height, Color bg, Color fill, Image overlay,
		    View redraw) {
	    super(0, 0, width, height);
	    _imageCache = new Bitmap(width, height);
	    _bgColor = bg;
	    _fillColor = fill;
	    _overlay = overlay;
	    _redraw = redraw;
	}
	
	public int getPercentDone() {
	    return (int) (_percentDone * 100.0F);
	}
	
	public void setPercentDone(float percent) {
	    if ((double) percent < 0.0)
		percent = 0.0F;
	    if ((double) percent > 1.0)
		percent = 1.0F;
	    int current = getPercentDone();
	    _percentDone = percent;
	    updateImageCache();
	    if (current != getPercentDone()) {
		if (_redraw == null)
		    this.draw();
		else
		    _redraw.draw();
	    }
	}
	
	public void drawView(Graphics g) {
	    _imageCache.drawAt(g, 0, 0);
	}
	
	private void updateImageCache() {
	    Graphics g = _imageCache.createGraphics();
	    Rect r = new Rect(0, 0, this.width(), this.height());
	    g.setColor(_bgColor);
	    g.fillRect(r);
	    g.setColor(Color.black);
	    g.drawRect(r);
	    r.setBounds(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
	    r.width = (int) ((float) r.width * _percentDone);
	    g.setColor(_fillColor);
	    g.fillRect(r);
	    if (_overlay != null)
		_overlay.drawAt(g, 0, 0);
	    g.dispose();
	}
    }
    
    ProgressDialog(int width, int height, String title) {
	super(1, 0, 0, width, height);
	this.setTransparent(false);
	this.setResizable(false);
	this.setCloseable(false);
	this.setCanBecomeMain(false);
	this.setLayer(100);
	this.setTitle(title);
	_overlay = null;
	setBgColor(Color.white);
	setFillColor(Color.magenta);
	setInset(4);
    }
    
    ProgressDialog(Bitmap overlay, String title) {
	this(300, 100, title);
	setInset(0);
	_overlay = overlay;
	Size sz
	    = this.windowSizeForContentSize(overlay.width(), overlay.height());
	this.sizeTo(sz.width, sz.height);
    }
    
    public void setBgColor(Color c) {
	_bgColor = c;
    }
    
    public Color getBgColor() {
	return _bgColor;
    }
    
    public void setFillColor(Color c) {
	_fillColor = c;
    }
    
    public Color getFIllColor() {
	return _fillColor;
    }
    
    public void setInset(int i) {
	_inset = i;
    }
    
    public int getInset() {
	return _inset;
    }
    
    public int getPercentDone() {
	return _view.getPercentDone();
    }
    
    public void setPercentDone(int percent) {
	_view.setPercentDone((float) percent / 100.0F);
    }
    
    public void setTotalCount(int count) {
	_total = (float) count;
    }
    
    public int getTotalCount() {
	return (int) _total;
    }
    
    public void setTotalDone(int done) {
	_done = (float) done;
	setPercentDone();
    }
    
    public void incrementTotalDone(int done) {
	_done += (float) done;
	setPercentDone();
    }
    
    public void setPercentDone() {
	if (_total != 0.0F)
	    _view.setPercentDone(_done / _total);
    }
    
    public void show() {
	Size sz = this.contentSize();
	_view = new PercentView(sz.width - 2 * _inset, sz.height - 2 * _inset,
				_bgColor, _fillColor, _overlay, this);
	_view.moveTo(_inset, _inset);
	this.addSubview(_view);
	this.center();
	super.show();
    }
    
    public void hide() {
	_view.removeFromSuperview();
	_view = null;
	super.hide();
    }
}
