/* GenericElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.util.Enumeration;

public class GenericElement extends AbstractElement
{
    AbstractElement[] children;
    int nchildren;
    int lastIndex;
    int givenStartIdx;
    int givenEndIdx;
    
    public GenericElement(int i, AbstractElement abstractelement,
			  TagAttributes tagattributes, int i_0_) {
	super(abstractelement, tagattributes);
	this.setTagCode(i);
	children = new AbstractElement[1];
	nchildren = 0;
	lastIndex = 0;
	givenStartIdx = i_0_;
	givenEndIdx = 1;
    }
    
    public AbstractElement[] getAllElements() {
	return children;
    }
    
    public void removeAll() {
	int i = 0;
	while (i < nchildren)
	    children[i++] = null;
	nchildren = 0;
    }
    
    public AbstractElement positionToElement(int i) {
	int i_1_ = getElementIndex(i);
	AbstractElement abstractelement = children[i_1_];
	int i_2_ = abstractelement.getStartIndex();
	int i_3_ = abstractelement.getEndIndex();
	if (i >= i_2_ && i < i_3_)
	    return abstractelement;
	return null;
    }
    
    public void insertDecedent(GenericElement genericelement_4_) {
	AbstractElement[] abstractelements = new AbstractElement[nchildren];
	System.arraycopy(children, 0, abstractelements, 0, nchildren);
	genericelement_4_.nchildren = nchildren;
	genericelement_4_.children = abstractelements;
	abstractelements = new AbstractElement[1];
	abstractelements[0] = genericelement_4_;
	children = abstractelements;
	nchildren = 1;
    }
    
    public void swapElementAt(AbstractElement abstractelement, int i) {
	children[i] = abstractelement;
    }
    
    public void removeElementAt(int i) {
	nchildren--;
	if (children.length > i + 1)
	    System.arraycopy(children, i + 1, children, i, nchildren - i);
	children[nchildren] = null;
    }
    
    public void replace(int i, int i_5_, AbstractElement[] abstractelements) {
	int i_6_ = abstractelements.length;
	int i_7_ = i_6_ - i_5_;
	int i_8_ = i + i_5_;
	int i_9_ = nchildren - i_8_;
	int i_10_ = i_8_ + i_7_;
	if (nchildren + i_7_ >= children.length) {
	    int i_11_ = Math.max(2 * children.length, nchildren + i_7_);
	    AbstractElement[] abstractelements_12_
		= new AbstractElement[i_11_];
	    System.arraycopy(children, 0, abstractelements_12_, 0, i);
	    System.arraycopy(abstractelements, 0, abstractelements_12_, i,
			     abstractelements.length);
	    System.arraycopy(children, i_8_, abstractelements_12_, i_10_,
			     i_9_);
	    children = abstractelements_12_;
	} else {
	    System.arraycopy(children, i_8_, children, i_10_, i_9_);
	    System.arraycopy(abstractelements, 0, children, i,
			     abstractelements.length);
	}
	for (int i_13_ = 0; i_13_ < i_6_; i_13_++)
	    abstractelements[i_13_].parent = this;
	nchildren = nchildren + i_7_;
    }
    
    public void removeLast() {
	children[--nchildren] = null;
    }
    
    public int getStartIndex() {
	if (nchildren == 0)
	    return givenStartIdx;
	return children[0].getStartIndex();
    }
    
    public int getEndIndex() {
	if (nchildren == 0)
	    return givenEndIdx;
	AbstractElement abstractelement = children[nchildren - 1];
	return abstractelement.getEndIndex();
    }
    
    public void reSequence(int i) {
	int i_14_ = getElementCount();
	givenStartIdx = i;
	for (int i_15_ = 0; i_15_ < i_14_; i_15_++) {
	    AbstractElement abstractelement = getElement(i_15_);
	    abstractelement.reSequence(i);
	    i = abstractelement.getEndIndex();
	}
	givenEndIdx = i == givenStartIdx ? i + 1 : i;
    }
    
    protected void setStartIndex(int i) {
	givenStartIdx = i;
    }
    
    protected void setEndIndex(int i) {
	givenEndIdx = i;
    }
    
    public AbstractElement getElement(int i) {
	if (i < nchildren)
	    return children[i];
	return null;
    }
    
    public int getElementCount() {
	return nchildren;
    }
    
    public int getElementIndex(int i) {
	int i_16_ = 0;
	int i_17_ = nchildren - 1;
	boolean bool = false;
	int i_18_ = getStartIndex();
	if (nchildren == 0)
	    return 0;
	if (i >= getEndIndex())
	    return nchildren;
	if (lastIndex >= i_16_ && lastIndex <= i_17_) {
	    AbstractElement abstractelement = children[lastIndex];
	    i_18_ = abstractelement.getStartIndex();
	    int i_19_ = abstractelement.getEndIndex();
	    if (i >= i_18_ && i < i_19_)
		return lastIndex;
	    if (i < i_18_)
		i_17_ = lastIndex;
	    else
		i_16_ = lastIndex;
	}
	while (i_16_ <= i_17_) {
	    int i_20_ = i_16_ + (i_17_ - i_16_ >> 1);
	    AbstractElement abstractelement = children[i_20_];
	    i_18_ = abstractelement.getStartIndex();
	    int i_21_ = abstractelement.getEndIndex();
	    if (i >= i_18_ && i < i_21_) {
		lastIndex = i_20_;
		return lastIndex;
	    }
	    if (i < i_18_)
		i_17_ = i_20_ - 1;
	    else
		i_16_ = i_20_ + 1;
	}
	return nchildren;
    }
    
    public boolean isLeaf() {
	return false;
    }
    
    public String toString() {
	return ("GenericElement(" + this.getTagCode() + ") " + getStartIndex()
		+ "," + getEndIndex());
    }
    
    void printIdent(int i) {
	for (int i_22_ = 0; i_22_ < i; i_22_++)
	    System.out.print(" ");
    }
    
    public void dump(int i) {
	printIdent(i);
	System.out.println(HTMLTagBag.getTagName(this.getTagCode()));
	i += 2;
	for (int i_23_ = 0; i_23_ < nchildren; i_23_++) {
	    AbstractElement abstractelement = children[i_23_];
	    if (abstractelement instanceof GenericElement)
		((GenericElement) abstractelement).dump(i);
	    else {
		printIdent(i);
		System.out.println(abstractelement);
	    }
	}
    }
    
    public void appendElement(AbstractElement abstractelement) {
	int i = children.length;
	if (i <= nchildren) {
	    int i_24_ = i << 1;
	    AbstractElement[] abstractelements = new AbstractElement[i_24_];
	    System.arraycopy(children, 0, abstractelements, 0, nchildren);
	    children = abstractelements;
	}
	children[nchildren++] = abstractelement;
    }
    
    public Enumeration children() {
	return null;
    }
    
    public void getElementsByTag(LinkList linklist, int i) {
	int i_25_ = getElementCount();
	for (int i_26_ = 0; i_26_ < i_25_; i_26_++) {
	    AbstractElement abstractelement = getElement(i_26_);
	    if (abstractelement instanceof GenericElement)
		abstractelement.getElementsByTag(linklist, i);
	    if (abstractelement.getTagCode() == i)
		linklist.append(abstractelement);
	}
    }
    
    public void getElementsByName(LinkList linklist, String string) {
	super.getElementsByName(linklist, string);
	int i = getElementCount();
	for (int i_27_ = 0; i_27_ < i; i_27_++) {
	    AbstractElement abstractelement = getElement(i_27_);
	    abstractelement.getElementsByName(linklist, string);
	}
    }
    
    public AbstractElement getElementById(String string) {
	if (string.equals(attributes.getAttribute(HTMLConst.id)))
	    return this;
	Object object = null;
	int i = getElementCount();
	for (int i_28_ = 0; i_28_ < i; i_28_++) {
	    AbstractElement abstractelement = getElement(i_28_);
	    AbstractElement abstractelement_29_
		= abstractelement.getElementById(string);
	    if (abstractelement_29_ != null)
		return abstractelement_29_;
	}
	return null;
    }
}
