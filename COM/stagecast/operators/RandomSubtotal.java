/* RandomSubtotal - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.FontMetrics;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.ASSERT;
import COM.stagecast.playwrite.EditorController;
import COM.stagecast.playwrite.Enableable;
import COM.stagecast.playwrite.PlaywriteView;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.ScrapBorder;
import COM.stagecast.playwrite.Summary;
import COM.stagecast.playwrite.Util;
import COM.stagecast.playwrite.ValueView;
import COM.stagecast.playwrite.ViewGlue;
import COM.stagecast.playwrite.World;
import COM.stagecast.playwrite.Worldly;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class RandomSubtotal extends SubtotalObject
    implements Cloneable, ResourceIDs.RuleEditorIDs
{
    public static final int storeVersion = 0;
    public static final long serialVersionUID = -3819410108751349042L;
    private static Bitmap diceBitmap;
    private String _toString;
    private SubtotalView _view;
    private int _toStringWidth;
    private int _subtotalWidth;
    
    class SubtotalView extends PlaywriteView implements Enableable
    {
	private ValueView.SetterGetter _minVSG;
	private ValueView.SetterGetter _maxVSG;
	private ValueView _minArgValueView;
	private ValueView _maxArgValueView;
	private boolean _enabled = true;
	
	private abstract class VSG extends ValueView.WorldSetterGetter
	{
	    private ValueView _valueView = null;
	    
	    public World getWorld() {
		return ((Worldly) _valueView.window()).getWorld();
	    }
	    
	    public void setValueView(ValueView valueView) {
		_valueView = valueView;
	    }
	    
	    public boolean isInteractive() {
		return _enabled;
	    }
	    
	    public boolean acceptsView(ViewGlue draggedView) {
		Object dropModel = _valueView.getViewsValue(draggedView);
		OperationManager randExpression = getRandOpMgr();
		boolean accept = true;
		if (randExpression == dropModel)
		    accept = false;
		else if (dropModel instanceof Expression) {
		    Expression expression = (Expression) dropModel;
		    Object foo = expression.evaluates(randExpression);
		    accept = foo == null;
		}
		return accept;
	    }
	    
	    public boolean viewDropped(ViewGlue droppedView) {
		return setValue(_valueView.getViewsValue(droppedView));
	    }
	    
	    public boolean setValue(Object value) {
		COM.stagecast.ifc.netscape.application.Window window
		    = SubtotalView.this.window();
		if (window instanceof EditorController.Editor)
		    ((EditorController.Editor) window)
			.objectChanged(getRandOpMgr());
		return true;
	    }
	    
	    public abstract Object getValue();
	    
	    public View createViewFor(Object value) {
		return null;
	    }
	}
	
	SubtotalView() {
	    this.setModelObject(RandomSubtotal.this);
	    this.setBorder(ScrapBorder.getRuleBorder());
	    this.setBackgroundColor(Subtotal.LIGHTER_BACKGROUND_COLOR);
	    this.sizeToMinSize();
	    _minVSG = new VSG() {
		public boolean setValue(Object value) {
		    getRandOpMgr().setLeftSide(value);
		    return super.setValue(value);
		}
		
		public Object getValue() {
		    return getRandOpMgr().getLeftSide();
		}
	    };
	    _maxVSG = new VSG() {
		public boolean setValue(Object value) {
		    getRandOpMgr().setRightSide(value);
		    return super.setValue(value);
		}
		
		public Object getValue() {
		    return getRandOpMgr().getRightSide();
		}
	    };
	    _minArgValueView = new CalculatorValueView(_minVSG);
	    _maxArgValueView = new CalculatorValueView(_maxVSG);
	    _minArgValueView.setTextFont(Subtotal.FONT);
	    _maxArgValueView.setTextFont(Subtotal.FONT);
	    _minArgValueView.setBackgroundColor(Color.white);
	    _maxArgValueView.setBackgroundColor(Color.white);
	    this.addSubview(_minArgValueView);
	    this.addSubview(_maxArgValueView);
	    _minArgValueView.updateView();
	    _maxArgValueView.updateView();
	    this.sizeToMinSize();
	    layoutView(0, 0);
	    if (_view == null)
		_view = this;
	    else
		throw new RuntimeException
			  ("only one view per RandomSubtotal.  period");
	}
	
	private int getTopHalfHeight() {
	    int max = (Math.max(_minArgValueView.height(),
				_maxArgValueView.height())
		       - 1);
	    max = Math.max(max, getDiceBitmap().height());
	    max += this.border().topMargin();
	    return max;
	}
	
	public void layoutView(int dx, int dy) {
	    if (_minArgValueView != null && _maxArgValueView != null) {
		int minX
		    = this.border().leftMargin() + getDiceBitmap().width() + 2;
		int maxX = (this.width() - this.border().rightMargin()
			    - _maxArgValueView.width());
		int minY;
		int maxY;
		if (_minArgValueView.height() > _maxArgValueView.height()) {
		    minY = this.border().topMargin();
		    maxY = (this.border().topMargin()
			    + (_minArgValueView.height()
			       - _maxArgValueView.height()) / 2);
		} else {
		    minY = (this.border().topMargin()
			    + (_maxArgValueView.height()
			       - _minArgValueView.height()) / 2);
		    maxY = this.border().topMargin();
		}
		_minArgValueView.moveTo(minX, minY);
		_maxArgValueView.moveTo(maxX, maxY);
	    }
	    super.layoutView(dx, dy);
	}
	
	public Size minSize() {
	    Size minSize = null;
	    if (_minArgValueView == null)
		minSize = super.minSize();
	    else {
		Size leftSize = _minArgValueView.minSize();
		Size rightSize = _maxArgValueView.minSize();
		int topWidth
		    = (leftSize.width + rightSize.width + _toStringWidth
		       + getDiceBitmap().width() + 6);
		int width = Math.max(topWidth, _subtotalWidth);
		int height = getTopHalfHeight() + Subtotal.SEPARATOR_Y;
		minSize = new Size(width + this.border().widthMargin(),
				   height + this.border().heightMargin());
	    }
	    return minSize;
	}
	
	public void drawViewBackground(Graphics g) {
	    super.drawViewBackground(g);
	    g.setColor(Color.gray);
	    g.fillRect(0, 0, this.width(), getTopHalfHeight());
	}
	
	public void drawView(Graphics g) {
	    int middleX = this.width() / 2;
	    int middleY = getTopHalfHeight();
	    super.drawView(g);
	    int imgY = (middleY - getDiceBitmap().height()) / 2;
	    getDiceBitmap().drawAt(g, this.border().leftMargin(), imgY);
	    g.setColor(Subtotal.BORDER_COLOR);
	    g.drawLine(0, middleY, this.width(), middleY);
	    String result = RandomSubtotal.this.getResultAsString();
	    if (result != null)
		Util.drawString(g, result, middleX, middleY + Subtotal.ASCENT,
				1, Subtotal.FONT, Subtotal.BORDER_COLOR);
	    int x = _minArgValueView.right();
	    Util.drawString(g, _toString, x + 2,
			    (middleY + Subtotal.ASCENT) / 2, 0, Subtotal.FONT,
			    Subtotal.BORDER_COLOR);
	}
	
	public void subviewDidResize(View subview) {
	    if (subview == _minArgValueView || subview == _maxArgValueView)
		this.sizeToMinSize();
	    super.subviewDidResize(subview);
	}
	
	public void discard() {
	    _view = null;
	    super.discard();
	}
	
	public boolean isEnabled() {
	    return _enabled;
	}
	
	public void setEnabled(boolean enabled) {
	    if (_enabled != enabled) {
		_enabled = enabled;
		_minArgValueView.setEnabled(_enabled);
		_maxArgValueView.setEnabled(_enabled);
	    }
	}
    }
    
    private class CalculatorValueView extends ValueView
    {
	CalculatorValueView(ValueView.SetterGetter vsg) {
	    super(vsg);
	}
	
	public Object getViewsValue(ViewGlue view) {
	    Object droppedValue = view.getModelObject();
	    if (droppedValue instanceof Subtotal
		&& !(droppedValue instanceof RandomSubtotal))
		return droppedValue;
	    return super.getViewsValue(view);
	}
    }
    
    public static Bitmap getDiceBitmap() {
	if (diceBitmap == null)
	    diceBitmap = Resource.getImage("RE dicey");
	return diceBitmap;
    }
    
    public RandomSubtotal(OperationManager randExp) {
	super((Subtotal.Creator) randExp);
	ASSERT.isNotNull(randExp);
	ASSERT.isTrue(randExp.getOperationType() == Op.Random);
	_toString = Resource.getText(ResourceIDs.OperationIDs.SYS_CALC_TO_ID);
	_toStringWidth = Subtotal.FONT.fontMetrics().stringWidth(_toString);
	reevaluate();
    }
    
    public RandomSubtotal() {
	_toString = Resource.getText(ResourceIDs.OperationIDs.SYS_CALC_TO_ID);
	_toStringWidth = Subtotal.FONT.fontMetrics().stringWidth(_toString);
    }
    
    OperationManager getRandOpMgr() {
	return (OperationManager) this.getExpression();
    }
    
    public PlaywriteView createView() {
	if (_view == null)
	    return new SubtotalView();
	return _view;
    }
    
    public void reevaluate() {
	super.reevaluate();
	FontMetrics fm = Subtotal.FONT.fontMetrics();
	_subtotalWidth = fm.stringWidth(this.getResultAsString());
	if (_view != null) {
	    _view._minArgValueView.updateView();
	    _view._maxArgValueView.updateView();
	    _view.sizeToMinSize();
	    _view.setDirty(true);
	    _view.setDragImageDirty();
	}
    }
    
    public Object clone() {
	RandomSubtotal newSubtotalObject = null;
	newSubtotalObject = (RandomSubtotal) super.clone();
	newSubtotalObject._view = null;
	return newSubtotalObject;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	RandomSubtotal newSubtotal
	    = (RandomSubtotal) super.copy(map, fullCopy);
	return newSubtotal;
    }
    
    public String getName() {
	return Resource.getText("Random Number Op");
    }
    
    public void summarize(Summary s) {
	Object lower = getRandOpMgr().getLeftSide();
	Object upper = getRandOpMgr().getRightSide();
	s.writeFormat("random xfmt", null, new Object[] { lower, upper });
    }
}
