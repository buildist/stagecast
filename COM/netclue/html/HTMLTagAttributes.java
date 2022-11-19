/* HTMLTagAttributes - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.util.Enumeration;
import java.util.Hashtable;

public class HTMLTagAttributes implements TagAttributes
{
    static final int ARRAY_ATTR = 0;
    static final int HASH_ATTR = 1;
    static final int SIZE_THRESHOLD = 4;
    TagAttributes intAttr;
    
    interface SharedInstance
    {
	public int getReferenceCount();
	
	public void updateReferenceCount(int i);
	
	public TagAttributes copyOnModify();
    }
    
    class ArrayAttributes implements TagAttributes, SharedInstance
    {
	Object[] attArray;
	int attCount;
	int refCount = 1;
	
	ArrayAttributes() {
	    attArray = new Object[8];
	}
	
	ArrayAttributes(TagAttributes tagattributes) {
	    if (tagattributes instanceof HTMLTagAttributes
		&& ((HTMLTagAttributes) tagattributes)
		       .isArrayImplementation()) {
		HTMLTagAttributes htmltagattributes_0_
		    = (HTMLTagAttributes) tagattributes;
		ArrayAttributes arrayattributes_1_
		    = (ArrayAttributes) htmltagattributes_0_.intern();
		attArray = (Object[]) arrayattributes_1_.getArray().clone();
		attCount = tagattributes.getAttributeCount();
	    } else if (tagattributes instanceof ArrayAttributes) {
		ArrayAttributes arrayattributes_2_
		    = (ArrayAttributes) tagattributes;
		attArray = (Object[]) arrayattributes_2_.getArray().clone();
		attCount = arrayattributes_2_.getAttributeCount();
	    } else {
		attArray = new Object[8];
		addAttributes(tagattributes);
	    }
	}
	
	Object[] getArray() {
	    return attArray;
	}
	
	public Object getAttribute(Object object) {
	    Object[] objects = attArray;
	    int i = attCount << 1;
	    for (int i_3_ = 0; i_3_ < i; i_3_ += 2) {
		if (object.equals(objects[i_3_]))
		    return objects[i_3_ + 1];
	    }
	    return null;
	}
	
	public String toString() {
	    String string = "{";
	    Object[] objects = attArray;
	    int i = attCount << 1;
	    for (int i_4_ = 0; i_4_ < i; i_4_ += 2) {
		if (objects[i_4_ + 1] instanceof TagAttributes)
		    string += ((Object) objects[i_4_] + "=" + "TagAttributes"
			       + ",");
		else
		    string += ((Object) objects[i_4_] + "="
			       + (Object) objects[i_4_ + 1] + ",");
	    }
	    string += "}";
	    return string;
	}
	
	public int hashCode() {
	    int i = 0;
	    Object[] objects = attArray;
	    int i_5_ = attCount << 1;
	    for (int i_6_ = 1; i_6_ < i_5_; i_6_ += 2)
		i ^= objects[i_6_].hashCode();
	    return i;
	}
	
	public boolean equals(Object object) {
	    if (object instanceof TagAttributes) {
		TagAttributes tagattributes = (TagAttributes) object;
		return isEqual(tagattributes);
	    }
	    return false;
	}
	
	public int getAttributeCount() {
	    return attCount;
	}
	
	public boolean isDefined(Object object) {
	    Object[] objects = attArray;
	    int i = attCount << 1;
	    for (int i_7_ = 0; i_7_ < i; i_7_ += 2) {
		if (object.equals(objects[i_7_]))
		    return true;
	    }
	    return false;
	}
	
	public boolean isEqual(TagAttributes tagattributes) {
	    if (getAttributeCount() != tagattributes.getAttributeCount()
		|| !containsAttributes(tagattributes))
		return false;
	    return true;
	}
	
	public TagAttributes copyAttributes() {
	    return new ArrayAttributes(this);
	}
	
	public Enumeration getAttributeNames() {
	    return new NameEnumerator(attArray, attCount);
	}
	
	public boolean containsAttribute(Object object, Object object_8_) {
	    return object_8_.equals(getAttribute(object));
	}
	
	public boolean containsAttributes(TagAttributes tagattributes) {
	    boolean bool = true;
	    Object object;
	    for (Enumeration enumeration = tagattributes.getAttributeNames();
		 bool && enumeration.hasMoreElements();
		 bool = tagattributes.getAttribute(object)
			    .equals(getAttribute(object)))
		object = enumeration.nextElement();
	    return bool;
	}
	
	public Object clone() {
	    return new ArrayAttributes(this);
	}
	
	public void addAttribute(Object object, Object object_9_) {
	    Object[] objects = attArray;
	    int i = attCount << 1;
	    for (int i_10_ = 0; i_10_ < i; i_10_ += 2) {
		if (objects[i_10_].equals(object)) {
		    objects[i_10_ + 1] = object_9_;
		    return;
		}
	    }
	    objects[i++] = object;
	    objects[i] = object_9_;
	    attCount++;
	}
	
	public void addAttributes(TagAttributes tagattributes) {
	    tagattributes.getAttributeCount();
	    Enumeration enumeration = tagattributes.getAttributeNames();
	    while (enumeration.hasMoreElements()) {
		Object object = enumeration.nextElement();
		addAttribute(object, tagattributes.getAttribute(object));
	    }
	}
	
	public void removeAttribute(Object object) {
	    if (attCount != 0) {
		Object[] objects = attArray;
		for (int i = 0; i < attCount; i++) {
		    if (object.equals(objects[i << 1])) {
			int i_11_ = attCount - 1 << 1;
			int i_12_ = i << 1;
			objects[i_12_] = objects[i_11_];
			objects[i_12_ + 1] = objects[i_11_ + 1];
			objects[i_11_] = objects[i_11_ + 1] = null;
			attCount--;
			break;
		    }
		}
	    }
	}
	
	public void removeAttributes(Enumeration enumeration) {
	    while (enumeration.hasMoreElements())
		removeAttribute(enumeration.nextElement());
	}
	
	public void removeAttributes(TagAttributes tagattributes) {
	    Enumeration enumeration = tagattributes.getAttributeNames();
	    removeAttributes(enumeration);
	}
	
	public int getReferenceCount() {
	    return refCount;
	}
	
	public void updateReferenceCount(int i) {
	    refCount += i;
	}
	
	public TagAttributes copyOnModify() {
	    refCount--;
	    return new ArrayAttributes(this);
	}
    }
    
    class HTableAttributes implements TagAttributes, SharedInstance
    {
	int refCount = 1;
	private Hashtable table;
	
	public HTableAttributes() {
	    table = new Hashtable(8);
	}
	
	public HTableAttributes(TagAttributes tagattributes) {
	    table = new Hashtable(8);
	    addAttributes(tagattributes);
	}
	
	private HTableAttributes(Hashtable hashtable) {
	    table = new Hashtable(8);
	    table = hashtable;
	}
	
	public boolean isEmpty() {
	    return table.isEmpty();
	}
	
	public int getAttributeCount() {
	    return table.size();
	}
	
	public boolean isDefined(Object object) {
	    return table.containsKey(object);
	}
	
	public boolean isEqual(TagAttributes tagattributes) {
	    if (getAttributeCount() != tagattributes.getAttributeCount()
		|| !containsAttributes(tagattributes))
		return false;
	    return true;
	}
	
	public TagAttributes copyAttributes() {
	    return (TagAttributes) clone();
	}
	
	public Enumeration getAttributeNames() {
	    return table.keys();
	}
	
	public Object getAttribute(Object object) {
	    return table.get(object);
	}
	
	public boolean containsAttribute(Object object, Object object_13_) {
	    return object_13_.equals(getAttribute(object));
	}
	
	public boolean containsAttributes(TagAttributes tagattributes) {
	    boolean bool = true;
	    Object object;
	    for (Enumeration enumeration = tagattributes.getAttributeNames();
		 bool && enumeration.hasMoreElements();
		 bool = tagattributes.getAttribute(object)
			    .equals(getAttribute(object)))
		object = enumeration.nextElement();
	    return bool;
	}
	
	public void addAttribute(Object object, Object object_14_) {
	    table.put(object, object_14_);
	}
	
	public void addAttributes(TagAttributes tagattributes) {
	    Enumeration enumeration = tagattributes.getAttributeNames();
	    while (enumeration.hasMoreElements()) {
		Object object = enumeration.nextElement();
		addAttribute(object, tagattributes.getAttribute(object));
	    }
	}
	
	public void removeAttribute(Object object) {
	    table.remove(object);
	}
	
	public void removeAttributes(Enumeration enumeration) {
	    while (enumeration.hasMoreElements())
		removeAttribute(enumeration.nextElement());
	}
	
	public void removeAttributes(TagAttributes tagattributes) {
	    Enumeration enumeration = tagattributes.getAttributeNames();
	    while (enumeration.hasMoreElements()) {
		Object object = enumeration.nextElement();
		Object object_15_ = tagattributes.getAttribute(object);
		if (object_15_.equals(getAttribute(object)))
		    removeAttribute(object);
	    }
	}
	
	public Object clone() {
	    return new HTableAttributes((Hashtable) table.clone());
	}
	
	public String toString() {
	    String string = "";
	    Enumeration enumeration = getAttributeNames();
	    while (enumeration.hasMoreElements()) {
		String string_16_ = enumeration.nextElement().toString();
		Object object = getAttribute(string_16_);
		if (object instanceof TagAttributes)
		    string += (String) string_16_ + "=**TagAttributes** ";
		else
		    string
			+= (String) string_16_ + "=" + (Object) object + " ";
	    }
	    return string;
	}
	
	public int getReferenceCount() {
	    return refCount;
	}
	
	public void updateReferenceCount(int i) {
	    refCount += i;
	}
	
	public TagAttributes copyOnModify() {
	    refCount--;
	    return new HTableAttributes(this);
	}
    }
    
    class NameEnumerator implements Enumeration
    {
	Object[] attr;
	int i;
	int len;
	
	NameEnumerator(Object[] objects, int i) {
	    attr = objects;
	    this.i = 0;
	    len = i << 1;
	}
	
	public boolean hasMoreElements() {
	    if (i >= len)
		return false;
	    return true;
	}
	
	public Object nextElement() {
	    if (i < len) {
		Object object = attr[i];
		i += 2;
		return object;
	    }
	    return null;
	}
    }
    
    public HTMLTagAttributes() {
	this(0);
    }
    
    public HTMLTagAttributes(int i) {
	if (i == 0)
	    intAttr = new ArrayAttributes();
	else
	    intAttr = new HTableAttributes();
    }
    
    public HTMLTagAttributes(TagAttributes tagattributes) {
	if (tagattributes instanceof HTMLTagAttributes) {
	    HTMLTagAttributes htmltagattributes_17_
		= (HTMLTagAttributes) tagattributes;
	    intAttr = htmltagattributes_17_.intern();
	    SharedInstance sharedinstance = (SharedInstance) intAttr;
	    sharedinstance.updateReferenceCount(1);
	} else
	    createIntern(tagattributes);
    }
    
    public Object clone() {
	return new HTMLTagAttributes(this);
    }
    
    public boolean equals(Object object) {
	if (object instanceof HTMLTagAttributes) {
	    HTMLTagAttributes htmltagattributes_18_
		= (HTMLTagAttributes) object;
	    if (intAttr.equals(htmltagattributes_18_.intern()))
		return true;
	}
	return false;
    }
    
    protected void updateReferenceCount(int i) {
	SharedInstance sharedinstance = (SharedInstance) intAttr;
	sharedinstance.updateReferenceCount(i);
    }
    
    protected void finalize() {
	SharedInstance sharedinstance = (SharedInstance) intAttr;
	sharedinstance.updateReferenceCount(1);
	intAttr = null;
    }
    
    void createIntern(TagAttributes tagattributes) {
	if (tagattributes.getAttributeCount() > 4)
	    intAttr = new HTableAttributes(tagattributes);
	else
	    intAttr = new ArrayAttributes(tagattributes);
    }
    
    public static TagAttributes getEmptySet() {
	return new HTMLTagAttributes();
    }
    
    public int getAttributeCount() {
	return intAttr.getAttributeCount();
    }
    
    public boolean isDefined(Object object) {
	return intAttr.isDefined(object);
    }
    
    public boolean isEqual(TagAttributes tagattributes) {
	return intAttr.isEqual(tagattributes);
    }
    
    public TagAttributes copyAttributes() {
	return new HTMLTagAttributes(this);
    }
    
    public Object getAttribute(Object object) {
	return intAttr.getAttribute(object);
    }
    
    public Enumeration getAttributeNames() {
	return intAttr.getAttributeNames();
    }
    
    public boolean containsAttribute(Object object, Object object_19_) {
	return intAttr.containsAttribute(object, object_19_);
    }
    
    public boolean containsAttributes(TagAttributes tagattributes) {
	return intAttr.containsAttributes(tagattributes);
    }
    
    public void addAttribute(Object object, Object object_20_) {
	SharedInstance sharedinstance = (SharedInstance) intAttr;
	if (sharedinstance.getReferenceCount() > 1)
	    intAttr = sharedinstance.copyOnModify();
	if (intAttr instanceof ArrayAttributes
	    && intAttr.getAttributeCount() == 4)
	    intAttr = new HTableAttributes(intAttr);
	intAttr.addAttribute(object, object_20_);
    }
    
    public void addAttributes(TagAttributes tagattributes) {
	SharedInstance sharedinstance = (SharedInstance) intAttr;
	if (sharedinstance.getReferenceCount() > 1)
	    intAttr = sharedinstance.copyOnModify();
	int i = intAttr.getAttributeCount();
	if (intAttr instanceof ArrayAttributes
	    && i + tagattributes.getAttributeCount() > 4)
	    intAttr = new HTableAttributes(intAttr);
	intAttr.addAttributes(tagattributes);
    }
    
    public void removeAttribute(Object object) {
	SharedInstance sharedinstance = (SharedInstance) intAttr;
	if (sharedinstance.getReferenceCount() > 1)
	    intAttr = sharedinstance.copyOnModify();
	intAttr.removeAttribute(object);
    }
    
    public void removeAttributes(Enumeration enumeration) {
	SharedInstance sharedinstance = (SharedInstance) intAttr;
	if (sharedinstance.getReferenceCount() > 1)
	    intAttr = sharedinstance.copyOnModify();
	intAttr.removeAttributes(enumeration);
    }
    
    public void removeAttributes(TagAttributes tagattributes) {
	intAttr.removeAttributes(tagattributes);
    }
    
    public int hashCode() {
	return intAttr.hashCode();
    }
    
    boolean isArrayImplementation() {
	return intAttr instanceof ArrayAttributes;
    }
    
    TagAttributes intern() {
	return intAttr;
    }
}
