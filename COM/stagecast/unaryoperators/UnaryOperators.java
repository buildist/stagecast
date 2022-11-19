/* UnaryOperators - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.unaryoperators;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.operators.NormalSubtotal;
import COM.stagecast.operators.Subtotal;
import COM.stagecast.playwrite.ASSERT;
import COM.stagecast.playwrite.Resource;
import COM.stagecast.playwrite.StorageProxy;
import COM.stagecast.playwrite.StorageProxyHelper;
import COM.stagecast.playwrite.StorageProxyTable;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public final class UnaryOperators extends StorageProxyTable
    implements ResourceIDs.UnaryOpIDs
{
    private static UnaryOperators _helper;
    public static UnaryOperation SIN;
    public static UnaryOperation COS;
    public static UnaryOperation TAN;
    public static UnaryOperation ASIN;
    public static UnaryOperation ACOS;
    public static UnaryOperation ATAN;
    public static UnaryOperation DEG;
    public static UnaryOperation RAD;
    public static UnaryOperation SQRT;
    public static UnaryOperation NEG;
    public static UnaryOperation NUM_CHARS;
    public static UnaryOperation NUM_WORDS;
    public static UnaryOperation NUM_ITEMS;
    private static Vector unarys = new Vector(30);
    
    private abstract class UnaryTemplate implements UnaryOperation
    {
	private String _resourceID;
	
	UnaryTemplate(String resourceID) {
	    _resourceID = resourceID;
	    register(this);
	}
	
	public String getProxyID() {
	    return _resourceID;
	}
	
	public String getLocalName() {
	    return Resource.getText(_resourceID);
	}
	
	public String getDisplayType() {
	    return UnaryOperation.FUNCTION;
	}
	
	public Class getArgumentClass() {
	    return Number.class;
	}
	
	public StorageProxyHelper getStorageProxyHelper() {
	    return UnaryOperators._helper;
	}
	
	public Subtotal createSubtotal(UnaryExpression expression) {
	    return new NormalSubtotal(expression);
	}
	
	public abstract Object uoperate(Object object);
    }
    
    public static void initStatics() {
	ASSERT.isTrue(_helper == null);
	_helper = new UnaryOperators();
	SIN = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_SIN_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = Math.sin(n);
		return new Double(n);
	    }
	};
	COS = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_COS_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = Math.cos(n);
		return new Double(n);
	    }
	};
	TAN = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_TAN_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = Math.tan(n);
		return new Double(n);
	    }
	};
	ASIN = _helper
		   .new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_ASIN_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = Math.asin(n);
		return new Double(n);
	    }
	};
	ACOS = _helper
		   .new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_ACOS_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = Math.acos(n);
		return new Double(n);
	    }
	};
	ATAN = _helper
		   .new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_ATAN_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = Math.atan(n);
		return new Double(n);
	    }
	};
	DEG = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_DEG_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = n * 3.14159265358979 / 180.0;
		return new Double(n);
	    }
	};
	RAD = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_RAD_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = n * 180.0 / 3.14159265358979;
		return new Double(n);
	    }
	};
	SQRT = _helper
		   .new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_SQRT_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = Math.sqrt(n);
		return new Double(n);
	    }
	};
	NEG = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs.SYS_OP_NEG_ID) {
	    public Object uoperate(Object argument) {
		double n = ((Number) argument).doubleValue();
		n = -n;
		if (n == -0.0)
		    n = 0.0;
		return new Double(n);
	    }
	};
	NUM_CHARS = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs
					      .SYS_OP_NUM_CHARS_ID) {
	    public Object uoperate(Object argument) {
		String s = argument.toString();
		return new Integer(s.length());
	    }
	    
	    public Class getArgumentClass() {
		return String.class;
	    }
	};
	NUM_WORDS = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs
					      .SYS_OP_NUM_WORDS_ID) {
	    public Object uoperate(Object argument) {
		String s = argument.toString();
		int length = s.length();
		if (length == 0)
		    return new Integer(0);
		String whiteSpaceChars = " \n\r\t";
		int count = 0;
		boolean lastCharIsWhiteSpace = true;
		for (int i = 0; i < length; i++) {
		    if (whiteSpaceChars.indexOf(s.charAt(i)) >= 0) {
			if (!lastCharIsWhiteSpace)
			    count++;
			lastCharIsWhiteSpace = true;
		    } else
			lastCharIsWhiteSpace = false;
		}
		if (!lastCharIsWhiteSpace)
		    count++;
		return new Integer(count);
	    }
	    
	    public Class getArgumentClass() {
		return String.class;
	    }
	};
	NUM_ITEMS = _helper.new UnaryTemplate(ResourceIDs.UnaryOpIDs
					      .SYS_OP_NUM_ITEMS_ID) {
	    public Object uoperate(Object argument) {
		String s = argument.toString();
		int length = s.length();
		if (length == 0)
		    return new Integer(0);
		int count = 1;
		length--;
		for (int i = 1; i < length; i++) {
		    if (s.charAt(i) == ',')
			count++;
		}
		return new Integer(count);
	    }
	    
	    public Class getArgumentClass() {
		return String.class;
	    }
	};
    }
    
    public static void register(UnaryOperation unaryOperation) {
	StorageProxyHelper helper = unaryOperation.getStorageProxyHelper();
	ASSERT.isNotNull(helper);
	helper.registerProxy(unaryOperation.getProxyID(), unaryOperation);
	unarys.addElementIfAbsent(unaryOperation);
    }
    
    public static Enumeration getOps() {
	return unarys.elements();
    }
    
    private UnaryOperators() {
	StorageProxy.registerHelper(this);
    }
}
