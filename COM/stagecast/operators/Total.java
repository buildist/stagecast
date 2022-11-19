/* Total - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.Named;
import COM.stagecast.playwrite.PlaywriteView;
import COM.stagecast.playwrite.ReferencedObject;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.RubberBandView;
import COM.stagecast.playwrite.Summary;
import COM.stagecast.playwrite.TitledObjectView;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class Total extends NormalSubtotal implements ResourceIDs.RuleEditorIDs
{
    public static final int storeVersion = 0;
    public static final long serialVersionUID = -3819410108751217970L;
    public static final Color TOTAL_BACKGROUND_COLOR
	= new Color(102, 153, 153);
    private transient Named _auxNamed;
    private Subtotal _subtotal;
    
    public Total(Subtotal subtotal) {
	super(subtotal.getExpression());
	_subtotal = subtotal;
    }
    
    public Total() {
	/* empty */
    }
    
    public void reevaluate() {
	if (_subtotal == null) {
	    _result = null;
	    _resultString = null;
	} else {
	    _subtotal.clearCache();
	    _subtotal.reevaluate();
	    _result = _subtotal.getResult();
	    _resultString = _subtotal.getResultAsString();
	}
	this.updateAllContentViews();
    }
    
    public void clearCache() {
	this.expressionChanged();
	if (_subtotal != null)
	    _subtotal.clearCache();
    }
    
    public Object eval() {
	if (_subtotal == null)
	    return Operation.ERROR;
	reevaluate();
	return _result;
    }
    
    public Object findReferenceTo(ReferencedObject obj) {
	if (_subtotal != null)
	    return _subtotal.findReferenceTo(obj);
	return null;
    }
    
    public Expression evaluates(Expression object) {
	if (_subtotal != null) {
	    if (object == this.getExpression())
		return this;
	    return this.getExpression().evaluates(object);
	}
	return null;
    }
    
    public Object clone() {
	Total newTotal = null;
	newTotal = (Total) super.clone();
	newTotal._auxNamed = null;
	newTotal._subtotal = null;
	return newTotal;
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Total newTotal = (Total) map.get(this);
	if (newTotal == null) {
	    newTotal = (Total) clone();
	    if (_subtotal != null)
		newTotal._subtotal = (Subtotal) _subtotal.copy(map, fullCopy);
	    map.put(this, newTotal);
	}
	return newTotal;
    }
    
    public void setName(String name) {
	super.setName(name);
	if (_auxNamed != null)
	    _auxNamed.setName(name);
    }
    
    public void setAuxNamed(Named thing) {
	_auxNamed = thing;
    }
    
    public Subtotal getSubtotal() {
	return _subtotal;
    }
    
    public void resetSubtotal(Subtotal subtotal) {
	_subtotal = subtotal;
	if (subtotal == null)
	    _expression = null;
	else
	    _expression = subtotal.getExpression();
	reevaluate();
    }
    
    public PlaywriteView createView() {
	if (this.getResultAsString() == null)
	    reevaluate();
	TitledObjectView subtotalView = (TitledObjectView) super.createView();
	subtotalView.setInsetBackgroundColor(TOTAL_BACKGROUND_COLOR);
	subtotalView.setEnabled(false);
	RubberBandView view = new RubberBandView();
	view.setTransparent(true);
	view.setCursor(12);
	PlaywriteView calcIcon = new PlaywriteView(Resource.getImage("RE co"));
	view.addSubview(calcIcon);
	calcIcon.setMouseTransparency(true);
	view.setModelObject(this);
	view.setAutoEditable(true);
	subtotalView.moveTo(calcIcon.bounds.maxX(), calcIcon.y());
	view.addSubview(subtotalView);
	view.sizeToMinSize();
	subtotalView.setMouseTransparency(true);
	return view;
    }
    
    public void summarize(Summary s) {
	s.writeFormat("total xfmt", new Object[] { this.getName() },
		      new Object[] { _subtotal });
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeObject(_subtotal);
	out.writeUTF(this.getName());
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_subtotal = (Subtotal) in.readObject();
	String name = in.readUTF();
	setName(name);
	_expression = _subtotal.getExpression();
    }
}
