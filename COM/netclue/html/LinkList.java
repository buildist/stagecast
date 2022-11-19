/* LinkList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.util.Enumeration;
import java.util.Vector;

public class LinkList
{
    ListElement head;
    ListElement cur;
    ListElement tail;
    int size;
    
    class ListElement
    {
	ListElement next;
	ListElement prev;
	Object value;
	
	public ListElement(Object object) {
	    value = object;
	}
    }
    
    public void append(Object object) {
	ListElement listelement = new ListElement(object);
	if (head == null)
	    head = cur = tail = listelement;
	else {
	    tail.next = listelement;
	    listelement.prev = tail;
	    tail = listelement;
	}
	size++;
    }
    
    public void appendList(LinkList linklist_0_) {
	tail.next = linklist_0_.head;
	linklist_0_.head.prev = tail;
	tail = linklist_0_.tail;
    }
    
    public int size() {
	return size;
    }
    
    public Enumeration getElements() {
	Vector vector = new Vector(size);
	for (ListElement listelement = head; listelement != null;
	     listelement = listelement.next)
	    vector.addElement(listelement.value);
	return vector.elements();
    }
    
    public void insertBeforeCurrentPosition(Object object) {
	ListElement listelement = new ListElement(object);
	cur = cur == null ? tail : cur;
	ListElement listelement_1_ = cur.prev;
	cur.prev = listelement;
	listelement.next = cur;
	listelement.prev = listelement_1_;
	if (listelement_1_ != null)
	    listelement_1_.next = listelement;
	else
	    head = listelement;
    }
    
    public void replace(Object object, Object object_2_) {
	for (ListElement listelement = head; listelement != null;
	     listelement = listelement.next) {
	    if (object_2_ == listelement.value) {
		listelement.value = object;
		cur = listelement;
		break;
	    }
	}
    }
    
    public Object elementAt(int i) {
	int i_3_ = 0;
	ListElement listelement;
	for (listelement = head; i_3_ < i && listelement != null; i_3_++)
	    listelement = listelement.next;
	if (i_3_ == i)
	    return listelement.value;
	return null;
    }
    
    public int elementPosition(Object object) {
	int i = 0;
	ListElement listelement = head;
	while (listelement != null) {
	    if (object == listelement.value) {
		cur = listelement;
		return i;
	    }
	    listelement = listelement.next;
	    i++;
	}
	return -1;
    }
    
    public boolean containElement(Object object) {
	for (ListElement listelement = head; listelement != null;
	     listelement = listelement.next) {
	    if (object == listelement.value) {
		cur = listelement;
		return true;
	    }
	}
	return false;
    }
    
    public boolean removeElement(Object object) {
	for (ListElement listelement = head; listelement != null;
	     listelement = listelement.next) {
	    if (object == listelement.value) {
		cur = listelement;
		removeElement();
		return true;
	    }
	}
	return false;
    }
    
    public void removeAll() {
	cur = head;
	while (cur != null) {
	    cur.prev = null;
	    cur.value = null;
	    ListElement listelement = cur;
	    cur = cur.next;
	    listelement = listelement.next = null;
	}
	head = tail = cur = null;
	size = 0;
    }
    
    public void removePrevElement() {
	if (cur != null && cur.prev != null) {
	    cur = cur.prev;
	    removeElement();
	}
    }
    
    public void removeLast() {
	ListElement listelement = tail.prev;
	if (listelement != null) {
	    listelement.next = null;
	    if (cur == tail)
		cur = listelement;
	    tail = listelement;
	} else
	    head = cur = tail = null;
    }
    
    public void removeElement() {
	if (cur != null) {
	    ListElement listelement = cur;
	    ListElement listelement_4_ = cur.next;
	    cur = listelement_4_;
	    if (listelement == head) {
		if (listelement_4_ != null) {
		    listelement_4_.prev = null;
		    head = listelement_4_;
		} else
		    head = tail = null;
	    } else if (listelement == tail) {
		listelement.prev.next = null;
		tail = listelement.prev;
	    } else {
		listelement.prev.next = listelement_4_;
		listelement_4_.prev = listelement.prev;
	    }
	    listelement.value = null;
	    size--;
	}
    }
    
    public void setToBegin() {
	cur = head;
    }
    
    public void setToEnd() {
	cur = tail;
    }
    
    public boolean isBegin() {
	if (cur != head)
	    return false;
	return true;
    }
    
    public boolean isEnd() {
	if (cur != tail)
	    return false;
	return true;
    }
    
    public boolean hasMoreElements() {
	if (cur == null)
	    return false;
	return true;
    }
    
    public Object nextElement() {
	Object object = cur.value;
	cur = cur.next;
	return object;
    }
    
    public void moveBack() {
	if (cur != head)
	    cur = cur.prev;
    }
    
    public void moveForward() {
	if (cur != tail)
	    cur = cur.next;
    }
    
    public Object getCurrentValue() {
	if (cur == null)
	    return null;
	return cur.value;
    }
    
    public String toString() {
	StringBuffer stringbuffer = new StringBuffer();
	ListElement listelement = cur;
	setToBegin();
	while (hasMoreElements())
	    stringbuffer.append(nextElement().toString() + " ");
	cur = listelement;
	return stringbuffer.toString();
    }
}
