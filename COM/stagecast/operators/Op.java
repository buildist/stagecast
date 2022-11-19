/* Op - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.ASSERT;
import COM.stagecast.playwrite.Condition;
import COM.stagecast.playwrite.PlaywriteInternalError;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.StorageProxy;
import COM.stagecast.playwrite.StorageProxyHelper;
import COM.stagecast.playwrite.StorageProxyTable;
import COM.stagecast.playwrite.Summary;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public final class Op extends StorageProxyTable
    implements ResourceIDs.OperationIDs
{
    private static final Op _proxyHelper = new Op();
    static Vector booleanOps = new Vector(10);
    static Vector nonBooleanOps = new Vector(10);
    static Vector allOps = new Vector(20);
    public static final SymmetricalOpType Add;
    public static final SymmetricalOpType Subtract;
    public static final SymmetricalOpType Multiply;
    public static final SymmetricalOpType Divide;
    public static final SymmetricalOpType Equal;
    public static final SymmetricalOpType Random;
    public static final SymmetricalOpType GreaterThan;
    public static final SymmetricalOpType GreaterThanEq;
    public static final SymmetricalOpType LessThan;
    public static final SymmetricalOpType LessThanEq;
    public static final SymmetricalOpType Contains;
    public static final SymmetricalOpType DoesNotContain;
    public static final SymmetricalOpType ContainedBy;
    public static final SymmetricalOpType StartsWith;
    public static final SymmetricalOpType EndsWith;
    public static final SymmetricalOpType Append;
    public static final SymmetricalOpType Remove;
    public static final SymmetricalOpType Mod;
    public static final SymmetricalOpType Power;
    public static final SymmetricalOpType Remainder;
    public static final SymmetricalOpType Round;
    public static final SymmetricalOpType AppendChar;
    public static final SymmetricalOpType AppendItem;
    public static final AsymmetricalOpType GetChar;
    public static final AsymmetricalOpType RemoveChar;
    public static final AsymmetricalOpType GetItem;
    public static final AsymmetricalOpType RemoveItem;
    public static final AsymmetricalOpType GetWord;
    public static final AsymmetricalOpType RemoveWord;
    private static final Object STRING_OP_ERROR = null;
    public static final NotOperationType NotEqual;
    public static final Operation standardEqualsOp = new Operation() {
	public Object operate(Object left, Object right) {
	    if (left == right)
		return Boolean.TRUE;
	    return left.equals(right) ? Boolean.TRUE : Boolean.FALSE;
	}
    };
    private static final Hashtable v1ObjectStoreSupport = new Hashtable(30);
    
    static {
	Object nullSubstitute
	    = new Condition("COM.stagecast.operators.Op.nullSubstitute");
	ResourceBundle resourceBundle = Resource.getTextResourceBundle();
	Add = new SymmetricalOpType("+op", resourceBundle, false);
	Subtract = new SymmetricalOpType("-op", resourceBundle, false);
	Multiply = new SymmetricalOpType("*op", resourceBundle, false);
	Divide = new SymmetricalOpType("/op", resourceBundle, false);
	Mod = new SymmetricalOpType("Mod Op", resourceBundle, false);
	Power = new SymmetricalOpType("Power Op", resourceBundle, false);
	Remainder
	    = new SymmetricalOpType("Remainder Op", resourceBundle, false);
	Round = new SymmetricalOpType("Round Op", resourceBundle, false);
	Random = new SymmetricalOpType("Random Number Op", resourceBundle,
				       false) {
	    public Subtotal createSubtotal(OperationManager om) {
		return new RandomSubtotal(om);
	    }
	};
	Add.setIdentity(new Integer(0));
	Subtract.setIdentity(new Integer(0));
	Multiply.setIdentity(new Integer(1));
	Divide.setIdentity(new Integer(1));
	Power.setIdentity(new Integer(1));
	Equal = new SymmetricalOpType("==op", resourceBundle, true);
	Equal.setIdentityX(nullSubstitute);
	NotEqual = new NotOperationType("!=op", resourceBundle, Equal);
	GreaterThan = new SymmetricalOpType(">op", resourceBundle, true);
	GreaterThanEq = new SymmetricalOpType(">=op", resourceBundle, true);
	LessThan = new SymmetricalOpType("<op", resourceBundle, true);
	LessThanEq = new SymmetricalOpType("<=op", resourceBundle, true);
	Contains = new SymmetricalOpType("Contains Op", resourceBundle, true);
	DoesNotContain = new SymmetricalOpType("does not Contain Op id",
					       resourceBundle, true);
	DoesNotContain.setIdentityX("");
	ContainedBy
	    = new SymmetricalOpType("Contained By Op", resourceBundle, true);
	StartsWith
	    = new SymmetricalOpType("Starts With Op", resourceBundle, true);
	EndsWith = new SymmetricalOpType("Ends With Op", resourceBundle, true);
	Append = new SymmetricalOpType("Append Op", resourceBundle, false);
	Append.setIdentity("");
	Remove = new SymmetricalOpType("Remove Op", resourceBundle, false);
	Remove.setIdentity("");
	AppendChar
	    = new SymmetricalOpType("Append Char Op", resourceBundle, false);
	AppendChar.setIdentity("");
	AppendItem
	    = new SymmetricalOpType("Append Item Op", resourceBundle, false);
	AppendItem.setIdentity("");
	GetChar = new AsymmetricalOpType("Get Char # Op", resourceBundle, String.class, Number.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String string = left.toString();
		Number number = (Number) right;
		int index = number.intValue();
		Object result = null;
		if (index > 0 && index <= string.length())
		    result
			= new String(new char[] { string.charAt(index - 1) });
		return result;
	    }
	});
	RemoveChar = new AsymmetricalOpType("Remove Char # Op", resourceBundle, String.class, Number.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String string = (String) left;
		Number number = (Number) right;
		int index = number.intValue();
		Object result = null;
		if (index > 0 && index <= string.length()) {
		    char[] chars = string.toCharArray();
		    StringBuffer resultBuf = new StringBuffer();
		    if (--index >= 0)
			resultBuf.append(chars, 0, index);
		    if (index < chars.length - 1) {
			int start = index + 1;
			int len = chars.length - start;
			resultBuf.append(chars, start, len);
		    }
		    result = resultBuf.toString();
		}
		return result;
	    }
	});
	GetItem = new AsymmetricalOpType("Get Item # Op", resourceBundle, String.class, Number.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String string = (String) left;
		Number number = (Number) right;
		int n = number.intValue();
		Object result = null;
		StringTokenizer tokenizer = new StringTokenizer(string, ",");
		if (n > 0 && n <= tokenizer.countTokens()) {
		    while (n-- > 0)
			result = tokenizer.nextToken();
		}
		return result;
	    }
	});
	RemoveItem = new AsymmetricalOpType("Remove Item # Op", resourceBundle, String.class, Number.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String string = (String) left;
		Number number = (Number) right;
		int n = number.intValue();
		StringBuffer stringBuffer = new StringBuffer();
		Object result = null;
		StringTokenizer tokenizer = new StringTokenizer(string, ",");
		int count = tokenizer.countTokens();
		if (n > 0 && n <= count) {
		    n--;
		    for (int i = 0; i < count; i++) {
			String token = tokenizer.nextToken();
			if (i != n) {
			    if (stringBuffer.length() > 0)
				stringBuffer.append(",");
			    stringBuffer.append(token);
			}
		    }
		    result = stringBuffer.toString();
		}
		return result;
	    }
	});
	GetWord = new AsymmetricalOpType("Get Word # Op", resourceBundle, String.class, Number.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String string = (String) left;
		Number number = (Number) right;
		int n = number.intValue();
		Object result = null;
		int[] range = getWordRange(string, n);
		if (range != null)
		    result = string.substring(range[0], range[1]).trim();
		return result;
	    }
	});
	RemoveWord = new AsymmetricalOpType("Remove Word # Op", resourceBundle, String.class, Number.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String string = (String) left;
		Number number = (Number) right;
		int n = number.intValue();
		Object result = string;
		int[] range = getWordRange(string, n);
		if (range != null)
		    result = (string.substring(0, range[0])
			      + string.substring(range[1]));
		return result;
	    }
	});
	Equal.addDefault(Object.class, standardEqualsOp);
	Operation intAdd = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		return new Long(a + b);
	    }
	};
	Add.addOperation(Short.class, intAdd);
	Add.addOperation(Long.class, intAdd);
	Add.addOperation(Integer.class, intAdd);
	Operation intSubtract = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		return new Long(a - b);
	    }
	};
	Subtract.addOperation(Short.class, intSubtract);
	Subtract.addOperation(Long.class, intSubtract);
	Subtract.addOperation(Integer.class, intSubtract);
	Operation intMultiply = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		return new Long(a * b);
	    }
	};
	Multiply.addOperation(Short.class, intMultiply);
	Multiply.addOperation(Long.class, intMultiply);
	Multiply.addOperation(Integer.class, intMultiply);
	Operation doubleDivide = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		double c = a / b;
		return new Double(c);
	    }
	};
	Divide.addDefault(Number.class, doubleDivide);
	Operation doubleMod = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		double c = a - b * Math.floor(a / b);
		return new Double(c);
	    }
	};
	Mod.addDefault(Number.class, doubleMod);
	Operation doubleRemainder = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		double c = a / b;
		boolean sign = c < 0.0;
		if (sign)
		    c = Math.abs(c);
		c = Math.floor(c);
		if (sign)
		    c *= -1.0;
		c = a - b * c;
		return new Double(c);
	    }
	};
	Remainder.addDefault(Number.class, doubleRemainder);
	Operation doublePower = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		double c;
		try {
		    c = Math.pow(a, b);
		} catch (ArithmeticException arithmeticexception) {
		    return Operation.ERROR;
		}
		return new Double(c);
	    }
	};
	Power.addDefault(Number.class, doublePower);
	Operation doubleRound = new Operation() {
	    public Object operate(Object left, Object right) {
		Number a = (Number) left;
		int b = ((Number) right).intValue();
		long aLong = 0L;
		if (b < 0)
		    return Operation.ERROR;
		double a1 = a.doubleValue();
		boolean sign = a1 < 0.0;
		if (sign)
		    a1 = Math.abs(a1);
		double f = Math.pow(10.0, (double) b);
		a1 *= f;
		aLong = Math.round(a1);
		Object result
		    = new Double((sign ? -1.0 : 1.0) * (double) aLong / f);
		return result;
	    }
	};
	Round.addDefault(Number.class, doubleRound);
	Operation intEqual = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		return a == b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	Equal.addOperation(Short.class, intEqual);
	Equal.addOperation(Integer.class, intEqual);
	Equal.addOperation(Long.class, intEqual);
	Operation intGT = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		return a > b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	GreaterThan.addOperation(Short.class, intGT);
	GreaterThan.addOperation(Integer.class, intGT);
	GreaterThan.addOperation(Long.class, intGT);
	Operation intGTEqual = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		return a >= b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	GreaterThanEq.addOperation(Short.class, intGTEqual);
	GreaterThanEq.addOperation(Integer.class, intGTEqual);
	GreaterThanEq.addOperation(Long.class, intGTEqual);
	Operation intLT = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		return a < b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	LessThan.addOperation(Short.class, intLT);
	LessThan.addOperation(Integer.class, intLT);
	LessThan.addOperation(Long.class, intLT);
	Operation intLTE = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		return a <= b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	LessThanEq.addOperation(Short.class, intLTE);
	LessThanEq.addOperation(Integer.class, intLTE);
	LessThanEq.addOperation(Long.class, intLTE);
	Operation doubleAdd = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		return new Double(a + b);
	    }
	};
	Add.addOperation(Float.class, doubleAdd);
	Add.addOperation(Double.class, doubleAdd);
	Operation doubleSubtract = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		return new Double(a - b);
	    }
	};
	Subtract.addOperation(Float.class, doubleSubtract);
	Subtract.addOperation(Double.class, doubleSubtract);
	Operation doubleMultiply = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		return new Double(a * b);
	    }
	};
	Multiply.addOperation(Float.class, doubleMultiply);
	Multiply.addOperation(Double.class, doubleMultiply);
	Divide.addOperation(Double.class, doubleDivide);
	Divide.addOperation(Float.class, doubleDivide);
	Operation doubleEqual = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		return a == b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	Equal.addOperation(Float.class, doubleEqual);
	Equal.addOperation(Double.class, doubleEqual);
	Operation doubleGT = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		return a > b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	GreaterThan.addOperation(Float.class, doubleGT);
	GreaterThan.addOperation(Double.class, doubleGT);
	Operation doubleGTE = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		return a >= b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	GreaterThanEq.addOperation(Float.class, doubleGTE);
	GreaterThanEq.addOperation(Double.class, doubleGTE);
	Operation doubleLT = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		return a < b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	LessThan.addOperation(Float.class, doubleLT);
	LessThan.addOperation(Double.class, doubleLT);
	Operation doubleLTE = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		return a <= b ? Boolean.TRUE : Boolean.FALSE;
	    }
	};
	LessThanEq.addOperation(Float.class, doubleLTE);
	LessThanEq.addOperation(Double.class, doubleLTE);
	Equal.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString();
		String b = right.toString();
		return a.equalsIgnoreCase(b) ? Boolean.TRUE : Boolean.FALSE;
	    }
	});
	Contains.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString().toLowerCase();
		String b = right.toString().toLowerCase();
		int result = a.indexOf(b);
		return result != -1 ? Boolean.TRUE : Boolean.FALSE;
	    }
	});
	DoesNotContain.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString().toLowerCase();
		String b = right.toString().toLowerCase();
		Boolean result;
		if (a.equals("") || b.equals(""))
		    result = Boolean.TRUE;
		else
		    result = a.indexOf(b) == -1 ? Boolean.TRUE : Boolean.FALSE;
		return result;
	    }
	});
	ContainedBy.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString().toLowerCase();
		String b = right.toString().toLowerCase();
		int result = b.indexOf(a);
		return result != -1 ? Boolean.TRUE : Boolean.FALSE;
	    }
	});
	StartsWith.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString();
		String b = right.toString();
		return (a.regionMatches(true, 0, b, 0, b.length())
			? Boolean.TRUE : Boolean.FALSE);
	    }
	});
	EndsWith.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString();
		String b = right.toString();
		return (a.regionMatches(true, a.length() - b.length(), b, 0,
					b.length())
			? Boolean.TRUE : Boolean.FALSE);
	    }
	});
	Append.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString();
		String b = right.toString();
		char[] bc = b.toCharArray();
		StringBuffer result = new StringBuffer();
		result.append(a);
		if (bc.length > 0) {
		    if (Character.isWhitespace(bc[0]))
			result.append(b);
		    else {
			if (result.length() > 0)
			    result.append(" ");
			result.append(b);
		    }
		}
		return result.toString();
	    }
	});
	Remove.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString();
		String a1 = a.toLowerCase();
		String b = right.toString();
		String b1 = b.toLowerCase();
		int bStart = a1.lastIndexOf(b1);
		if (bStart == -1)
		    return a;
		int aLength = a.length();
		int bLength = b.length();
		char[] source = a.toCharArray();
		StringBuffer result = new StringBuffer();
		int length = 0;
		int start = 0;
		int firstCharFollowingB = bStart + bLength;
		if (bStart == 0 && source.length == firstCharFollowingB)
		    return null;
		if (bStart == 0
		    && Character.isWhitespace(source[firstCharFollowingB])) {
		    start = firstCharFollowingB + 1;
		    result.append(source, start, aLength - start);
		} else if (firstCharFollowingB == aLength
			   && (bStart - 1 < 0 ? false
			       : Character.isWhitespace(source[bStart - 1])))
		    result.append(source, 0, bStart - 1);
		else {
		    if (bStart > 0 && firstCharFollowingB < aLength
			&& Character.isWhitespace(source[bStart - 1])
			&& Character.isWhitespace(source[firstCharFollowingB]))
			result.append(source, 0, bStart - 1);
		    else
			result.append(source, 0, bStart);
		    bStart += bLength;
		    result.append(source, bStart, aLength - bStart);
		}
		return result.toString();
	    }
	});
	AppendItem.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString();
		String b = right.toString();
		char[] bc = b.toCharArray();
		StringBuffer result = new StringBuffer();
		result.append(a);
		if (bc.length > 0) {
		    if (bc[bc.length - 1] == ',')
			result.append(b);
		    else {
			if (result.length() > 0)
			    result.append(",");
			result.append(b);
		    }
		}
		return result.toString();
	    }
	});
	AppendChar.addOperation(String.class, new Operation() {
	    public Object operate(Object left, Object right) {
		String a = left.toString();
		String b = right.toString();
		char[] bc = b.toCharArray();
		StringBuffer result = new StringBuffer();
		result.append(a);
		if (bc.length > 0)
		    result.append(b);
		return result.toString();
	    }
	});
	Operation intRandom = new Operation() {
	    public Object operate(Object left, Object right) {
		long a = ((Number) left).longValue();
		long b = ((Number) right).longValue();
		long min = Math.min(a, b);
		long max = Math.max(a, b);
		long range = max - min + 1L;
		double rand = Math.random();
		long result = min + (long) (rand * (double) range);
		if (result > max)
		    result = max;
		return new Long(result);
	    }
	};
	Random.addOperation(Short.class, intRandom);
	Random.addOperation(Integer.class, intRandom);
	Random.addOperation(Long.class, intRandom);
	Operation doubleRandom = new Operation() {
	    public Object operate(Object left, Object right) {
		double a = ((Number) left).doubleValue();
		double b = ((Number) right).doubleValue();
		double min = Math.min(a, b);
		double max = Math.max(a, b);
		double range = max - min;
		double rand = Math.random();
		double result = min + rand * range;
		return new Double(result);
	    }
	};
	Random.addDefault(Number.class, doubleRandom);
	addv1Mapping(Add, "+");
	addv1Mapping(Subtract, "-");
	addv1Mapping(Multiply, "*");
	addv1Mapping(Multiply, "\u2219");
	addv1Mapping(Divide, "\u00f7");
	addv1Mapping(Divide, "/");
	addv1Mapping(Equal, "is");
	addv1Mapping(NotEqual, "is not");
	addv1Mapping(GreaterThan, ">");
	addv1Mapping(GreaterThanEq, ">=");
	addv1Mapping(GreaterThanEq, "\u2265");
	addv1Mapping(LessThan, "<");
	addv1Mapping(LessThanEq, "<=");
	addv1Mapping(LessThanEq, "\u2264");
	addv1Mapping(Contains, "contains");
	addv1Mapping(ContainedBy, "is contained in  ");
	addv1Mapping(StartsWith, "starts with");
	addv1Mapping(EndsWith, "ends with");
	addv1Mapping(Append, "append");
	addv1Mapping(Remove, "remove");
    }
    
    private Op() {
	StorageProxy.registerHelper(this);
    }
    
    public static StorageProxyHelper getStorageProxyHelper() {
	return _proxyHelper;
    }
    
    public static void registerBoolean(OperationType operationType) {
	booleanOps.addElement(operationType);
	register(operationType);
    }
    
    public static void registerNonBoolean(OperationType operationType) {
	nonBooleanOps.addElement(operationType);
	register(operationType);
    }
    
    public static Enumeration getNonBooleanOps() {
	return nonBooleanOps.elements();
    }
    
    public static Enumeration getBooleanOps() {
	return booleanOps.elements();
    }
    
    private static void register(OperationType operationType) {
	StorageProxyHelper helper = operationType.getStorageProxyHelper();
	ASSERT.isNotNull(helper);
	helper.registerProxy(operationType.getNameResourceID(), operationType);
	allOps.addElement(operationType);
    }
    
    public static Enumeration getBooleanOperationTypes() {
	return booleanOps.elements();
    }
    
    public static boolean isBoolean(OperationType type) {
	return booleanOps.containsIdentical(type);
    }
    
    static OperationType getOperatorByID(String ID) {
	Enumeration list = allOps.elements();
	while (list.hasMoreElements()) {
	    OperationType om = (OperationType) list.nextElement();
	    if (om.getNameResourceID().equals(ID))
		return om;
	}
	return null;
    }
    
    public static void summarizeOp(Summary s, OperationType operationType,
				   Object leftArg, Object rightArg) {
	String printName = operationType.getLocalName();
	if (operationType == Divide)
	    printName = "/";
	s.writeFormat("operation summarizer format",
		      new Object[] { printName },
		      new Object[] { leftArg, rightArg });
    }
    
    public static Object checkResult(Object result) {
	if (result instanceof Double) {
	    Double d = (Double) result;
	    if (d.isNaN())
		return Operation.ERROR;
	    if (d.isInfinite())
		return Operation.ERROR;
	    long l = d.longValue();
	    if (d.equals(new Double((double) l)))
		return new Long(l);
	}
	return result;
    }
    
    private static int[] getWordRange(String string, int n) {
	int[] result = null;
	int startIndex = -1;
	int endIndex = -1;
	int word = 0;
	String whiteSpaceChars = " \n\r\t";
	boolean lookingForWSC = false;
	int i;
	for (i = 0; word != n && i < string.length(); i++) {
	    if (whiteSpaceChars.indexOf(string.charAt(i)) == -1) {
		if (!lookingForWSC) {
		    word++;
		    startIndex = i;
		    lookingForWSC = true;
		}
	    } else if (lookingForWSC)
		lookingForWSC = false;
	}
	if (word == n) {
	    for (/**/; i < string.length(); i++) {
		if (whiteSpaceChars.indexOf(string.charAt(i)) != -1)
		    break;
	    }
	    for (/**/; i < string.length() && string.charAt(i) == ' '; i++) {
		/* empty */
	    }
	    endIndex = i;
	    if (startIndex != -1 && endIndex != -1) {
		result = new int[2];
		result[0] = startIndex;
		result[1] = endIndex;
	    }
	}
	return result;
    }
    
    static OperationType getv1OperatorByName(String name) {
	return (OperationType) v1ObjectStoreSupport.get(name);
    }
    
    private static void addv1Mapping(OperationType operationType,
				     String name) {
	Object result = v1ObjectStoreSupport.put(name, operationType);
	if (result != null)
	    throw new PlaywriteInternalError("duplicate ids not permitted!");
    }
}
