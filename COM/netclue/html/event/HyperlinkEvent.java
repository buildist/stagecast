/* HyperlinkEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.event;
import java.net.URL;
import java.util.EventObject;

public class HyperlinkEvent extends EventObject
{
    public static int GET_METHOD;
    public static int POST_METHOD = 1;
    private int method;
    private String targetFrame;
    private String anchor;
    private boolean consumed = false;
    private URL u;
    
    public HyperlinkEvent(Object object, URL url) {
	this(object, url, GET_METHOD);
    }
    
    public HyperlinkEvent(Object object, URL url, int i) {
	super(object);
	u = url;
	method = i;
    }
    
    public void consume() {
	consumed = true;
    }
    
    public boolean isConsumed() {
	return consumed;
    }
    
    public void setTarget(String string) {
	targetFrame = string;
    }
    
    public String getTarget() {
	return targetFrame;
    }
    
    public void setAnchor(String string) {
	anchor = string;
    }
    
    public String getAnchor() {
	return anchor;
    }
    
    public int getMethod() {
	return method;
    }
    
    public URL getURL() {
	return u;
    }
}
