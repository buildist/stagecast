/* DRStringValue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

class DRStringValue
{
    private int specifier;
    private Object value;
    
    DRStringValue(int i, String s) {
	setSpecifier(i);
	setValue(s);
    }
    
    final int getSpecifier() {
	return specifier;
    }
    
    final void setSpecifier(int i) {
	specifier = i;
    }
    
    final Object getValue() {
	return value;
    }
    
    final void setValue(Object o) {
	value = o;
    }
    
    final void setValue(String s) {
	Object theValue = s;
	try {
	    try {
		Double dnum = new Double(s);
		theValue = dnum;
		try {
		    Long lnum = new Long(s);
		    if (dnum.doubleValue() == lnum.doubleValue())
			theValue = lnum;
		} catch (NumberFormatException numberformatexception) {
		    /* empty */
		}
	    } catch (NumberFormatException numberformatexception) {
		/* empty */
	    }
	} catch (ClassCastException classcastexception) {
	    /* empty */
	} finally {
	    String check = theValue.toString();
	    if (check.equals(s))
		setValue(theValue);
	    else
		setValue((Object) s);
	}
    }
    
    public String toString() {
	if (value instanceof String)
	    return "<StringValue '" + value + "'>";
	return "<StringValue " + value + ">";
    }
}
