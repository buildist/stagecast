/* TagAttributes - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.util.Enumeration;

public interface TagAttributes
{
    public int getAttributeCount();
    
    public boolean isDefined(Object object);
    
    public boolean isEqual(TagAttributes tagattributes_0_);
    
    public TagAttributes copyAttributes();
    
    public Object getAttribute(Object object);
    
    public Enumeration getAttributeNames();
    
    public boolean containsAttribute(Object object, Object object_1_);
    
    public boolean containsAttributes(TagAttributes tagattributes_2_);
    
    public void addAttribute(Object object, Object object_3_);
    
    public void addAttributes(TagAttributes tagattributes_4_);
    
    public void removeAttribute(Object object);
    
    public void removeAttributes(Enumeration enumeration);
    
    public void removeAttributes(TagAttributes tagattributes_5_);
}
