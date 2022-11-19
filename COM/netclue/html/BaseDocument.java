/* BaseDocument - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.util.Dictionary;
import java.util.Hashtable;

import com.netclue.html.event.EventListenerList;

public abstract class BaseDocument
{
    private Dictionary documentProperties;
    protected EventListenerList listenerList = new EventListenerList();
    public static final String BaseURL = "base";
    public static final String DocTitle = "title";
    public static final String domain = "domain";
    public static final String linkColor = "linkcolor";
    public static final String TextName = "content";
    public static final int NO_UPDATE = 0;
    public static final int PAINT = 1;
    public static final int LAYOUT = 2;
    public static final int STRUCTURE = 3;
    int updateHint;
    
    public Object getProperty(Object object) {
	return getDocumentProperties().get(object);
    }
    
    public void putProperty(Object object, Object object_0_) {
	if (object_0_ != null)
	    getDocumentProperties().put(object, object_0_);
    }
    
    public AbstractElement getElementAtIndex(AbstractElement abstractelement,
					     int i) {
	int i_1_;
	for (/**/; abstractelement != null && !abstractelement.isLeaf();
	     abstractelement = abstractelement.getElement(i_1_))
	    i_1_ = abstractelement.getElementIndex(i);
	return abstractelement;
    }
    
    public AbstractElement getDefaultRootElement() {
	return null;
    }
    
    public GenericElement getBodyElement() {
	return null;
    }
    
    GenericElement getLinkRoot() {
	return null;
    }
    
    public abstract StyleFactory getStyleFactory();
    
    public Dictionary getDocumentProperties() {
	if (documentProperties == null)
	    documentProperties = new Hashtable(8);
	return documentProperties;
    }
}
