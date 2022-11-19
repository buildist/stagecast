/* HTMLParsingRules - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public class HTMLParsingRules
{
    private Hashtable rules = null;
    private String defaultContainerClassName;
    private String defaultMarkerClassName;
    private static final String HTMLDefaultRules
	= "{  LI = { BeginTermination = ( LI ); EndTermination = (OL,UL,DIR,MENU); };     IMG= { IsContainer=false; };                                       DD = { BeginTermination = (DT,DD); EndTermination = (DL,A); };       DT = { BeginTermination = (DT,DD); EndTermination = (DL,A); };        P = { IsContainer=true; BeginTermination=(P,OL,UL,DIR,MENU,PRE,H1,H2,H3,H4,H5,H6);  };   BR = { IsContainer=false; };                                       HR = { IsContainer=false; };                                       PRE= { ShouldRetainFormatting=true; };                                  A= { IsContainer=true; BeginTermination = (A); };              }";
    public static final String STRING_MARKER_KEY = "IFCSTRING";
    public static final String COMMENT_MARKER_KEY = "IFCCOMMENT";
    public static final String REPRESENTATION_KEY = "Representation";
    public static final String BEGIN_TERMINATION_MARKERS_KEY
	= "BeginTermination";
    public static final String END_TERMINATION_MARKERS_KEY = "EndTermination";
    public static final String IS_CONTAINER_KEY = "IsContainer";
    public static final String SHOULD_RETAIN_FORMATTING_KEY
	= "ShouldRetainFormatting";
    public static final String SHOULD_IGNORE_END_KEY = "ShouldIgnoreEnd";
    
    public HTMLParsingRules() {
	rules = new Hashtable();
	Vector vector = new Vector(new Object[] { "LI" });
	setRuleForMarker("BeginTermination", vector, "LI");
	vector = new Vector(new Object[] { "OL", "UL", "DIR", "MENU" });
	setRuleForMarker("EndTermination", vector, "LI");
	setRuleForMarker("IsContainer", "false", "IMG");
	vector = new Vector(new Object[] { "DT", "DD" });
	setRuleForMarker("BeginTermination", vector, "DD");
	vector = new Vector(new Object[] { "DL", "A" });
	setRuleForMarker("EndTermination", vector, "DD");
	vector = new Vector(new Object[] { "DT", "DD" });
	setRuleForMarker("BeginTermination", vector, "DT");
	vector = new Vector(new Object[] { "DL", "A" });
	setRuleForMarker("EndTermination", vector, "DT");
	setRuleForMarker("IsContainer", "true", "P");
	vector
	    = new Vector(new Object[] { "P", "OL", "UL", "DIR", "MENU", "PRE",
					"H1", "H2", "H3", "H4", "H5", "H6" });
	setRuleForMarker("BeginTermination", vector, "P");
	setRuleForMarker("IsContainer", "false", "BR");
	setRuleForMarker("IsContainer", "false", "HR");
	setRuleForMarker("ShouldRetainFormatting", "true", "PRE");
	setRuleForMarker("IsContainer", "true", "A");
	vector = new Vector(new Object[] { "A" });
	setRuleForMarker("BeginTermination", vector, "A");
    }
    
    public void setRulesForMarker(Hashtable hashtable, String string) {
	rules.put(string, hashtable);
    }
    
    public Hashtable rulesForMarker(String string) {
	return (Hashtable) rules.get(string);
    }
    
    public void setRuleForMarker(String string, Object object,
				 String string_0_) {
	Hashtable hashtable = rulesForMarker(string_0_);
	if (hashtable == null)
	    hashtable = new Hashtable();
	hashtable.put(string, object);
	setRulesForMarker(hashtable, string_0_);
    }
    
    public void setClassNameForMarker(String string, String string_1_) {
	Hashtable hashtable = rulesForMarker(string_1_);
	if (hashtable == null)
	    hashtable = new Hashtable();
	hashtable.put("Representation", string);
	setRulesForMarker(hashtable, string_1_);
    }
    
    public String classNameForMarker(String string) {
	Hashtable hashtable = rulesForMarker(string);
	if (hashtable != null) {
	    String string_2_ = (String) hashtable.get("Representation");
	    if (string_2_ == null) {
		if (isContainer(hashtable))
		    string_2_ = defaultContainerClassName;
		else
		    string_2_ = defaultMarkerClassName;
	    }
	    return string_2_;
	}
	Hashtable hashtable_3_ = rulesForMarker(string);
	boolean bool;
	if (hashtable_3_ != null)
	    bool = isContainer(hashtable_3_);
	else
	    bool = true;
	if (bool && defaultContainerClassName != null)
	    return defaultContainerClassName;
	if (!bool && defaultMarkerClassName != null)
	    return defaultMarkerClassName;
	return null;
    }
    
    public void setDefaultContainerClassName(String string) {
	defaultContainerClassName = string;
    }
    
    public String defaultContainerClassName() {
	return defaultContainerClassName;
    }
    
    public void setDefaultMarkerClassName(String string) {
	defaultMarkerClassName = string;
    }
    
    public String defaultMarkerClassName() {
	return defaultMarkerClassName;
    }
    
    public void setStringClassName(String string) {
	setClassNameForMarker(string, "IFCSTRING");
    }
    
    public String classNameForString() {
	return classNameForMarker("IFCSTRING");
    }
    
    public void setClassNameForComment(String string) {
	setClassNameForMarker(string, "IFCCOMMENT");
    }
    
    public String classNameForComment() {
	return classNameForMarker("IFCCOMMENT");
    }
    
    boolean shouldIgnoreEnd(Hashtable hashtable) {
	if (hashtable == null)
	    return false;
	if (hashtable.get("ShouldIgnoreEnd") != null
	    && ((String) hashtable.get("ShouldIgnoreEnd")).toUpperCase()
		   .equals("TRUE"))
	    return true;
	return false;
    }
    
    boolean isContainer(Hashtable hashtable) {
	if (hashtable == null)
	    return true;
	if (hashtable.get("IsContainer") != null
	    && ((String) hashtable.get("IsContainer")).toUpperCase()
		   .equals("FALSE"))
	    return false;
	return true;
    }
    
    boolean shouldFilterStringsForChildren(Hashtable hashtable) {
	if (hashtable == null)
	    return true;
	if (hashtable.get("ShouldRetainFormatting") != null
	    && ((String) hashtable.get("ShouldRetainFormatting")).toUpperCase
		   ().equals("TRUE"))
	    return false;
	return true;
    }
}
