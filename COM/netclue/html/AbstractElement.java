/* AbstractElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;

public abstract class AbstractElement
{
    public AbstractElement parent;
    int tagCode;
    protected BaseDocument doc;
    TagAttributes attributes;
    
    public AbstractElement(AbstractElement abstractelement_0_,
			   TagAttributes tagattributes) {
	attributes = tagattributes;
	parent = abstractelement_0_;
    }
    
    public Object getLocalAttribute(Object object) {
	if (attributes == null)
	    return null;
	return attributes.getAttribute(object);
    }
    
    public Object getAttribute(Object object) {
	Object object_1_
	    = attributes == null ? null : attributes.getAttribute(object);
	if (object_1_ == null && parent != null)
	    object_1_ = parent.getAttribute(object);
	return object_1_;
    }
    
    public void setParent(AbstractElement abstractelement_2_) {
	if (abstractelement_2_ != parent)
	    parent = abstractelement_2_;
    }
    
    public void setAttribute(Object object, Object object_3_) {
	if (attributes == null)
	    attributes = new HTMLTagAttributes();
	attributes.addAttribute(object, object_3_);
    }
    
    public void setAttributeNode(TagAttributes tagattributes) {
	if (attributes == null)
	    attributes = tagattributes;
	else
	    attributes.addAttributes(tagattributes);
    }
    
    public void removeAttribute(Object object) {
	attributes.removeAttribute(object);
    }
    
    public void removeAttributeNode(TagAttributes tagattributes) {
	attributes.removeAttributes(tagattributes);
    }
    
    public void setDocument(BaseDocument basedocument) {
	doc = basedocument;
    }
    
    public BaseDocument getDocument() {
	if (doc != null)
	    return doc;
	return parent.getDocument();
    }
    
    public AbstractElement getParentElement() {
	return parent;
    }
    
    public TagAttributes getAttributeNode() {
	return attributes;
    }
    
    public void setTagCode(int i) {
	tagCode = i;
    }
    
    public int getTagCode() {
	return tagCode;
    }
    
    public int getElementIndex(int i) {
	return -1;
    }
    
    public AbstractElement getElement(int i) {
	return null;
    }
    
    public int getElementCount() {
	return 0;
    }
    
    protected abstract void reSequence(int i);
    
    protected abstract void setStartIndex(int i);
    
    protected abstract void setEndIndex(int i);
    
    public abstract int getStartIndex();
    
    public abstract int getEndIndex();
    
    public boolean isLeaf() {
	return true;
    }
    
    public void getElementsByTag(LinkList linklist, int i) {
	/* empty */
    }
    
    public void getElementsByName(LinkList linklist, String string) {
	if (attributes != null
	    && string.equals(attributes.getAttribute(HTMLConst.name)))
	    linklist.append(this);
    }
    
    public AbstractElement getElementById(String string) {
	if (attributes != null
	    && string.equals(attributes.getAttribute(HTMLConst.id)))
	    return this;
	return null;
    }
}
