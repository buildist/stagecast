/* TagElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.util.Enumeration;

public class TagElement
{
    int type;
    boolean isEnd;
    String content;
    HTMLTagAttributes attr;
    
    public TagElement(int i, boolean bool) {
	type = i;
	isEnd = bool;
    }
    
    public TagElement(String string) {
	type = 150;
	content = string;
    }
    
    public TagElement duplicate(boolean bool) {
	TagElement tagelement_0_ = new TagElement(type, isEnd);
	tagelement_0_.attr
	    = bool || attr == null ? null : (HTMLTagAttributes) attr.clone();
	return tagelement_0_;
    }
    
    public String getContent() {
	return content;
    }
    
    public int getContentLength() {
	if (content == null)
	    return 0;
	return content.length();
    }
    
    public int getHTMLContentLength() {
	return content.trim().length();
    }
    
    public int getType() {
	return type;
    }
    
    public void setType(int i) {
	type = i;
    }
    
    public void setEnded(boolean bool) {
	isEnd = bool;
    }
    
    public boolean isEnd() {
	return isEnd;
    }
    
    public void setAttribute(Object object, Object object_1_) {
	if (attr == null)
	    attr = new HTMLTagAttributes();
	attr.addAttribute(object, object_1_);
    }
    
    public Object getAttribute(String string) {
	if (attr == null)
	    return null;
	return attr.getAttribute(string);
    }
    
    public Enumeration getAttributeNames() {
	if (attr == null)
	    return null;
	return attr.getAttributeNames();
    }
    
    public void removeAttribute(String string) {
	attr.removeAttribute(string);
    }
}
