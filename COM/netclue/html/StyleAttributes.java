/* StyleAttributes - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;

public class StyleAttributes extends HTMLTagAttributes
{
    private StyleAttributes attContext;
    
    public StyleAttributes() {
	/* empty */
    }
    
    public StyleAttributes(TagAttributes tagattributes) {
	super(tagattributes);
    }
    
    public StyleAttributes getContext() {
	return attContext;
    }
    
    public void setContext(StyleAttributes styleattributes_0_) {
	HTMLTagAttributes.SharedInstance sharedinstance
	    = (HTMLTagAttributes.SharedInstance) intAttr;
	if (sharedinstance.getReferenceCount() > 1)
	    intAttr = sharedinstance.copyOnModify();
	attContext = styleattributes_0_;
    }
    
    public boolean equals(Object object) {
	if (object instanceof StyleAttributes && super.equals(object)) {
	    StyleAttributes styleattributes_1_ = (StyleAttributes) object;
	    StyleAttributes styleattributes_2_
		= styleattributes_1_.getContext();
	    if (attContext == null && styleattributes_2_ == null
		|| attContext.equals(styleattributes_2_))
		return true;
	}
	return false;
    }
    
    public Object getAttribute(Object object) {
	Object object_3_ = super.getAttribute(object);
	if (object_3_ == null && attContext != null)
	    object_3_ = attContext.getAttribute(object);
	return object_3_;
    }
    
    public int hashCode() {
	int i = super.hashCode();
	if (attContext != null)
	    i ^= attContext.hashCode();
	return i;
    }
}
