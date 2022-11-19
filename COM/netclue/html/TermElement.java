/* TermElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;

public class TermElement extends AbstractElement
{
    private int p0;
    private int p1;
    String content;
    
    public TermElement(AbstractElement abstractelement,
		       TagAttributes tagattributes, int i, String string) {
	this(HTMLTagBag.textID, abstractelement, tagattributes, i);
	content = string;
	p1 = i + string.length();
    }
    
    public TermElement(AbstractElement abstractelement,
		       HTMLTagAttributes htmltagattributes,
		       StyleFactory stylefactory, int i, String string) {
	this(HTMLTagBag.textID, abstractelement, htmltagattributes, i);
	content = string;
	p1 = i + string.length();
	if (htmltagattributes != null)
	    attributes = stylefactory.getSharedAttributes(htmltagattributes);
    }
    
    public TermElement(int i, AbstractElement abstractelement,
		       TagAttributes tagattributes, int i_0_) {
	super(abstractelement, tagattributes);
	this.setTagCode(i);
	p0 = i_0_;
	p1 = i_0_ + 1;
    }
    
    public String toString() {
	return (HTMLTagBag.getTagName(this.getTagCode()) + "[" + p0 + ", " + p1
		+ "]");
    }
    
    protected void reSequence(int i) {
	setStartIndex(i);
    }
    
    protected void setStartIndex(int i) {
	p0 = i;
	p1 = content == null ? p0 + 1 : p0 + content.length();
    }
    
    protected void setEndIndex(int i) {
	/* empty */
    }
    
    public int getStartIndex() {
	return p0;
    }
    
    public int getEndIndex() {
	return p1;
    }
    
    public String getContent() {
	return content;
    }
    
    public void setContent(String string) {
	content = string;
	int i = p0 + string.length();
	p1 = i;
    }
}
