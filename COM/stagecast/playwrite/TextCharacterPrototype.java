/* TextCharacterPrototype - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class TextCharacterPrototype extends SpecialPrototype
    implements ResourceIDs.ColorIDs, ResourceIDs.TextCharIDs
{
    public static final String SYS_TEXT_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:text".intern();
    public static final String SYS_TEXT_EDITABLE_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:editable".intern();
    public static final String SYS_TEXT_FONT_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:font".intern();
    public static final String SYS_TEXT_SIZE_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:size".intern();
    public static final String SYS_TEXT_ALIGNMENT_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:alignment".intern();
    public static final String SYS_TEXT_OFFSET_X_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:offset_x".intern();
    public static final String SYS_TEXT_OFFSET_Y_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:offset_y".intern();
    public static final String SYS_TEXT_COLOR_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:color".intern();
    public static final String SYS_TEXT_BGCOLOR_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:bgcolor".intern();
    public static final String SYS_TEXT_BORDER_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:border".intern();
    public static final String SYS_TEXT_SHRINKTOFIT_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:shrink_to_fit".intern();
    public static final String SYS_TEXT_WASCHANGED_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:was_changed".intern();
    public static final String SYS_TEXT_STYLE_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:style".intern();
    static final String DEFAULT_STRING = Resource.getText("TXT default text");
    static final int D_FONTSIZE = 10;
    static final String D_FONTNAME = Util.FONT_FAMILIES[0].getJavaName();
    static final Font D_FONT = Font.fontNamed(D_FONTNAME, 0, 10);
    static final int MIN_SIZE = 1;
    static final int DEFAULT_OFFSET_X = 5;
    static final int DEFAULT_OFFSET_Y = 5;
    static ColorValue D_COLOR;
    static ColorValue D_BGCOLOR;
    public static final String TRANSPARENT_COLOR = "transparent color";
    static final int storeVersion = 3;
    static final long serialVersionUID = -3819410108754625842L;
    static Appearance defaultTextAppear;
    
    static void staticInit() {
	defaultTextAppear
	    = new Appearance(Resource.getText("text character name id"),
			     Resource.getImage("TextProto"),
			     new Shape(1, 1, new Point(1, 1)));
	D_COLOR = new ColorValue(Color.black, "BlackID");
	D_BGCOLOR = new ColorValue(Color.white, "WhiteID");
	new Variable(SYS_TEXT_VARIABLE_ID, "TXTtxtID", true) {
	    public void notifyChanged(VariableOwner owner, Object oldVal,
				      Object val) {
		super.notifyChanged(owner, new Object(), val);
	    }
	};
	new BooleanVariable(SYS_TEXT_EDITABLE_VARIABLE_ID, "TXTroTID",
			    "TXTroFID");
	new BooleanVariable(SYS_TEXT_BORDER_VARIABLE_ID, "TXT border true ID",
			    "TXT border false ID");
	new BooleanVariable(SYS_TEXT_SHRINKTOFIT_VARIABLE_ID,
			    "TXT shrink_to_fit true ID",
			    "TXT shrink_to_fit false ID");
	new BooleanVariable(SYS_TEXT_WASCHANGED_VARIABLE_ID,
			    "TXT was_changed true ID",
			    "TXT was_changed false ID");
	new ColorVariable(SYS_TEXT_COLOR_VARIABLE_ID, "TXTcolorID");
	ColorVariable bgVarTemplate
	    = new ColorVariable(SYS_TEXT_BGCOLOR_VARIABLE_ID, "TXTbgcolorID");
	bgVarTemplate.setAllowTransparent();
	new PopupVariable(SYS_TEXT_FONT_VARIABLE_ID, "TXTfontID",
			  Util.getFontPopupList());
	PopupVariable styleVar
	    = Util.createFontStyleVariable(Resource.getTextResourceBundle(),
					   SYS_TEXT_STYLE_VARIABLE_ID,
					   "TXTstyleID");
	new Variable(SYS_TEXT_SIZE_VARIABLE_ID, "TXTsizeID",
		     Variable.STD_FONTSIZE_ACCESSOR, true);
	Vector v = new Vector(3);
	v.addElement(Resource.getText("TXTalignleftID"));
	v.addElement(Resource.getText("TXTalignrightID"));
	v.addElement(Resource.getText("TXTaligncenterID"));
	new PopupVariable(SYS_TEXT_ALIGNMENT_VARIABLE_ID, "TXTalignmentID", v);
	new Variable(SYS_TEXT_OFFSET_X_VARIABLE_ID, "TXT offset X ID",
		     new OffsetDirectAccessor(), true);
	new Variable(SYS_TEXT_OFFSET_Y_VARIABLE_ID, "TXT offset Y ID",
		     new OffsetDirectAccessor(), true);
    }
    
    public static void initExtension() {
	PlaywriteRoot.registerCharacterPrototype(TextCharacterPrototype.class);
    }
    
    public void init(World world) {
	this.fillInObject
	    (world, Resource.getText("text character name id"),
	     new Appearance(Resource.getText("text character name id"),
			    Resource.getImage("TextProto"),
			    new Shape(1, 1, new Point(1, 1))),
	     true);
    }
    
    public void initVariables() {
	if (this.getWorld() != null)
	    setDefaultVariableValues();
    }
    
    public CharacterInstance makeInstance() {
	return new TextCharacterInstance(this);
    }
    
    public void addSpecialCharacterSystemVariables() {
	Variable v
	    = ((ColorVariable)
	       Variable.newSystemVariable(SYS_TEXT_COLOR_VARIABLE_ID, this));
	this.add(v);
	v = Variable.newSystemVariable(SYS_TEXT_VARIABLE_ID, this);
	this.add(v);
	v = ((PopupVariable)
	     Variable.newSystemVariable(SYS_TEXT_FONT_VARIABLE_ID, this));
	this.add(v);
	VariableSieve sieve = this.getWorld().getVariableSieve();
	if (sieve != null)
	    sieve
		.addVariableValueFilter(v, new VariableSieve.VariableValueFilter() {
		public Object filterVariableValue
		    (VariableOwner owner, Variable v_1_, Object value) {
		    return Util.javaFontNameForUserName(value.toString());
		}
	    });
	v = ((PopupVariable)
	     Variable.newSystemVariable(SYS_TEXT_STYLE_VARIABLE_ID, this));
	this.add(v);
	if (sieve != null)
	    sieve
		.addVariableValueFilter(v, new VariableSieve.VariableValueFilter() {
		public Object filterVariableValue
		    (VariableOwner owner, Variable v_4_, Object value) {
		    return (new Integer
			    (Util.javaFontStyleForUserName(value.toString())));
		}
	    });
	v = Variable.newSystemVariable(SYS_TEXT_SIZE_VARIABLE_ID, this);
	this.add(v);
	v = ((PopupVariable)
	     Variable.newSystemVariable(SYS_TEXT_ALIGNMENT_VARIABLE_ID, this));
	this.add(v);
	if (sieve != null)
	    sieve
		.addVariableValueFilter(v, new VariableSieve.VariableValueFilter() {
		public Object filterVariableValue
		    (VariableOwner owner, Variable v_7_, Object value) {
		    int justification = 0;
		    if (value.equals(Resource.getText("TXTalignrightID")))
			justification = 1;
		    else if (value
				 .equals(Resource.getText("TXTaligncenterID")))
			justification = 2;
		    return new Integer(justification);
		}
	    });
	v = ((ColorVariable)
	     Variable.newSystemVariable(SYS_TEXT_BGCOLOR_VARIABLE_ID, this));
	this.add(v);
	if (sieve != null)
	    sieve
		.addVariableValueFilter(v, new VariableSieve.VariableValueFilter() {
		public Object filterVariableValue
		    (VariableOwner owner, Variable v_10_, Object value) {
		    if (value == ColorValue.transparentColor)
			return "transparent color";
		    return value;
		}
	    });
	v = Variable.newSystemVariable(SYS_TEXT_EDITABLE_VARIABLE_ID, this);
	this.add(v);
	v = Variable.newSystemVariable(SYS_TEXT_OFFSET_X_VARIABLE_ID, this);
	this.add(v);
	v = Variable.newSystemVariable(SYS_TEXT_OFFSET_Y_VARIABLE_ID, this);
	this.add(v);
	v = Variable.newSystemVariable(SYS_TEXT_BORDER_VARIABLE_ID, this);
	this.add(v);
	v = Variable.newSystemVariable(SYS_TEXT_SHRINKTOFIT_VARIABLE_ID, this);
	this.add(v);
	v = Variable.newSystemVariable(SYS_TEXT_WASCHANGED_VARIABLE_ID, this);
	this.add(v);
    }
    
    private void setDefaultVariableValues() {
	Variable.setSystemValue(SYS_TEXT_VARIABLE_ID, this, DEFAULT_STRING);
	Variable.setSystemValue(SYS_TEXT_FONT_VARIABLE_ID, this, D_FONTNAME);
	Variable.setSystemValue(SYS_TEXT_STYLE_VARIABLE_ID, this,
				Util.FONT_STYLES[0].getUserStyle());
	Variable.setSystemValue(SYS_TEXT_SIZE_VARIABLE_ID, this,
				new Integer(10));
	Variable.setSystemValue(SYS_TEXT_COLOR_VARIABLE_ID, this, D_COLOR);
	Variable.setSystemValue(SYS_TEXT_BGCOLOR_VARIABLE_ID, this, D_BGCOLOR);
	Variable.setSystemValue(SYS_TEXT_EDITABLE_VARIABLE_ID, this,
				Boolean.FALSE);
	Variable.setSystemValue(SYS_TEXT_BORDER_VARIABLE_ID, this,
				Boolean.TRUE);
	Variable.setSystemValue(SYS_TEXT_OFFSET_X_VARIABLE_ID, this,
				new Integer(5));
	Variable.setSystemValue(SYS_TEXT_OFFSET_Y_VARIABLE_ID, this,
				new Integer(5));
	Variable.setSystemValue(SYS_TEXT_ALIGNMENT_VARIABLE_ID, this,
				Resource.getText("TXTalignleftID"));
	Variable.setSystemValue(SYS_TEXT_SHRINKTOFIT_VARIABLE_ID, this,
				Boolean.FALSE);
	Variable.setSystemValue(SYS_TEXT_WASCHANGED_VARIABLE_ID, this,
				Boolean.FALSE);
    }
    
    public boolean editAppearance() {
	return false;
    }
    
    public PlaywriteView createView() {
	return new TextCharacterView(this);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version
	    = ((WorldInStream) in).loadVersion(TextCharacterPrototype.class);
	super.readExternal(in);
	switch (version) {
	case 1:
	    Variable.setSystemValue(SYS_TEXT_OFFSET_X_VARIABLE_ID, this,
				    new Integer(5));
	    Variable.setSystemValue(SYS_TEXT_OFFSET_Y_VARIABLE_ID, this,
				    new Integer(5));
	    this.sizeVariableFix();
	    break;
	case 2:
	    this.sizeVariableFix();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 3);
	case 3:
	    /* empty */
	}
	if (((WorldInStream) in).getFilesystemVersion() == 1) {
	    Variable.setSystemValue(SYS_TEXT_BORDER_VARIABLE_ID, this,
				    Boolean.TRUE);
	    Variable.setSystemValue(SYS_TEXT_ALIGNMENT_VARIABLE_ID, this,
				    Resource.getText("TXTalignleftID"));
	    Variable.setSystemValue(SYS_TEXT_SHRINKTOFIT_VARIABLE_ID, this,
				    Boolean.FALSE);
	    Variable.setSystemValue(SYS_TEXT_WASCHANGED_VARIABLE_ID, this,
				    Boolean.FALSE);
	}
    }
    
    public static void convertObsoleteVariableValue(VariableOwner owner,
						    Variable v) {
	if (v.isSystemType(SYS_TEXT_SIZE_VARIABLE_ID)) {
	    Object sizeVal = v.getValue(owner);
	    if (sizeVal instanceof String) {
		try {
		    sizeVal = new Integer(sizeVal.toString());
		} catch (NumberFormatException numberformatexception) {
		    sizeVal = null;
		}
		v.setActualValue(owner, sizeVal);
		Debug.print("debug.objectstore",
			    ("    convertObsoleteVariableValue TEXT_SIZE "
			     + sizeVal));
	    }
	} else if (v.isSystemType(SYS_TEXT_FONT_VARIABLE_ID)) {
	    String oldName = (String) v.getValue(owner);
	    for (int i = 0; i < Util.FONT_FAMILIES.length; i++) {
		if (Util.FONT_FAMILIES[i].getJavaName().equals(oldName)) {
		    v.setActualValue(owner,
				     Util.FONT_FAMILIES[i].getUserName());
		    Debug.print("debug.objectstore",
				("    convertObsoleteVariableValue TEXT_FONT "
				 + oldName + " to "
				 + Util.FONT_FAMILIES[i].getUserName()));
		    break;
		}
	    }
	} else if (v.isSystemType(SYS_TEXT_COLOR_VARIABLE_ID)
		   || v.isSystemType(SYS_TEXT_BGCOLOR_VARIABLE_ID)) {
	    Object oldValue = v.getValue(owner);
	    if (oldValue instanceof String) {
		ColorValue newValue = null;
		for (int i = 0; i < Util.chooseColorStrings.length; i++) {
		    if (Util.chooseColorStrings[i]
			    .equalsIgnoreCase((String) oldValue)) {
			newValue = new ColorValue(Util.chooseColors[i],
						  Util.chooseColorStrings[i]);
			break;
		    }
		}
		if (newValue == null)
		    newValue = (v.isSystemType(SYS_TEXT_COLOR_VARIABLE_ID)
				? D_COLOR : D_BGCOLOR);
		Debug.print("debug.objectstore",
			    ("    convertObsoleteVariableValue TEXT_COLOR "
			     + oldValue + " to " + newValue));
		v.setActualValue(owner, newValue);
	    }
	}
    }
}
