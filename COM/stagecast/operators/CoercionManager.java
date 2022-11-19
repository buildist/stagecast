/* CoercionManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.playwrite.Debug;
import COM.stagecast.playwrite.Named;
import COM.stagecast.playwrite.Resource;

public final class CoercionManager implements Debug.Constants
{
    private static Hashtable sourceTypeTable;
    private static int numCoercions;
    private static Double E = new Double(2.71828182845904);
    private static Double PI = new Double(3.14159265358979);
    
    static {
	sourceTypeTable = new Hashtable(10);
	numCoercions = 10;
	Coercion numToLong = new Coercion() {
	    public Object coerce(Object value) {
		return new Long(((Number) value).longValue());
	    }
	};
	addCoercion(Short.class, Long.class, numToLong);
	addCoercion(Integer.class, Long.class, numToLong);
	Coercion numToDouble = new Coercion() {
	    public Object coerce(Object value) {
		return new Double(((Number) value).doubleValue());
	    }
	};
	addCoercion(Short.class, Double.class, numToDouble);
	addCoercion(Integer.class, Double.class, numToDouble);
	addCoercion(Long.class, Double.class, numToDouble);
	Coercion booleanToString = new Coercion() {
	    public Object coerce(Object value) {
		return value.toString();
	    }
	};
	addCoercion(Boolean.class, String.class, booleanToString);
    }
    
    public static Object addCoercion(Class sourceType, Class destinationType,
				     Coercion coercion) {
	Hashtable coercionTable = (Hashtable) sourceTypeTable.get(sourceType);
	if (coercionTable == null) {
	    coercionTable = new Hashtable(numCoercions);
	    sourceTypeTable.put(sourceType, coercionTable);
	}
	Object oldValue = coercionTable.put(destinationType, coercion);
	return oldValue;
    }
    
    private static Object registeredCoercion(Object thing, Class destType) {
	Class sourceType = thing.getClass();
	Object result = null;
	if (destType.isAssignableFrom(sourceType))
	    result = thing;
	else {
	    Hashtable coercionTable
		= (Hashtable) sourceTypeTable.get(sourceType);
	    if (coercionTable != null) {
		Coercion coercion = (Coercion) coercionTable.get(destType);
		if (coercion != null)
		    result = coercion.coerce(thing);
	    }
	}
	return result;
    }
    
    public static Object coerce(Object thing, Class destType) {
	Object result = registeredCoercion(thing, destType);
	if (result == null) {
	    boolean wantNumber = Number.class.isAssignableFrom(destType);
	    boolean wantString = destType == String.class;
	    String thingString = null;
	    if (wantString || wantNumber) {
		if (thing instanceof String)
		    thingString = (String) thing;
		else if (thing instanceof Named)
		    thingString = ((Named) thing).getName();
		else if (wantString && thing instanceof Number)
		    thingString = Resource.formatNumber((Number) thing);
		if (wantString)
		    result = thingString;
		else if (thingString != null) {
		    if ("pi".equalsIgnoreCase(thingString))
			thing = PI;
		    else if ("e".equalsIgnoreCase(thingString))
			thing = E;
		    else
			thing = Resource.parseNumberString(thingString);
		    if (thing == null)
			return null;
		    result = registeredCoercion(thing, destType);
		}
	    }
	}
	return result;
    }
}
