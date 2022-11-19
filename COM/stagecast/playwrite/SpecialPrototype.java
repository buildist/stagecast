/* SpecialPrototype - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectOutput;

import COM.stagecast.playwrite.internationalization.ResourceIDs;

public abstract class SpecialPrototype extends CharacterPrototype
    implements ResourceIDs.SpecialCharIDs
{
    public static final String SYS_SPECIAL_WIDTH_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:width".intern();
    public static final String SYS_SPECIAL_HEIGHT_VARIABLE_ID
	= "Stagecast.TextCharacterPrototype:height".intern();
    static final double DEFAULT_WIDTH = 2.0;
    static final double DEFAULT_HEIGHT = 1.0;
    static final int storeVersion = 0;
    static final long serialVersionUID = -3819410108751480114L;
    
    static void staticInit() {
	new Variable(SYS_SPECIAL_WIDTH_VARIABLE_ID, "TXTwidthID",
		     new DimensionDirectAccessor(), true);
	new Variable(SYS_SPECIAL_HEIGHT_VARIABLE_ID, "TXTheightID",
		     new DimensionDirectAccessor(), true);
    }
    
    public void fillInObject(World world, String name, Appearance app) {
	super.fillInObject(world, name, app);
	if (this.getWorld() != null) {
	    Variable.setSystemValue(SYS_SPECIAL_WIDTH_VARIABLE_ID, this,
				    new Double(2.0));
	    Variable.setSystemValue(SYS_SPECIAL_HEIGHT_VARIABLE_ID, this,
				    new Double(1.0));
	    initVariables();
	}
    }
    
    public void fillInObject(World world, String name, Appearance appear,
			     boolean showWidthAndHeight) {
	fillInObject(world, name, appear);
	Variable.systemVariable(SYS_SPECIAL_WIDTH_VARIABLE_ID, this)
	    .setVisible(showWidthAndHeight);
	Variable.systemVariable(SYS_SPECIAL_HEIGHT_VARIABLE_ID, this)
	    .setVisible(showWidthAndHeight);
    }
    
    public abstract void initVariables();
    
    public abstract CharacterInstance makeInstance();
    
    public final void addSubclassSystemVariables() {
	Variable v = Variable.systemVariable((CocoaCharacter
					      .SYS_APPEARANCE_VARIABLE_ID),
					     this);
	v.setVisible(false);
	v = Variable.systemVariable((CocoaCharacter
				     .SYS_ROLLOVER_APPEARANCE_VARIABLE_ID),
				    this);
	v.setVisible(false);
	v = Variable.systemVariable((CocoaCharacter
				     .SYS_ROLLOVER_ENABLED_VARIABLE_ID),
				    this);
	v.setVisible(false);
	v = Variable.newSystemVariable(SYS_SPECIAL_HEIGHT_VARIABLE_ID, this);
	this.add(v);
	v = Variable.newSystemVariable(SYS_SPECIAL_WIDTH_VARIABLE_ID, this);
	this.add(v);
	addSpecialCharacterSystemVariables();
    }
    
    public void addSpecialCharacterSystemVariables() {
	/* empty */
    }
    
    public boolean editAppearance() {
	return false;
    }
    
    public abstract PlaywriteView createView();
    
    protected void sizeVariableFix() {
	Object width
	    = Variable.getSystemValue(SYS_SPECIAL_WIDTH_VARIABLE_ID, this);
	Object height
	    = Variable.getSystemValue(SYS_SPECIAL_HEIGHT_VARIABLE_ID, this);
	if (width instanceof Long || width instanceof Integer) {
	    double newWidth = ((Number) width).doubleValue() / 32.0;
	    Variable.setSystemValue(SYS_SPECIAL_WIDTH_VARIABLE_ID, this,
				    new Double(newWidth));
	}
	if (height instanceof Long || height instanceof Integer) {
	    double newHeight = ((Number) height).doubleValue() / 32.0;
	    Variable.setSystemValue(SYS_SPECIAL_HEIGHT_VARIABLE_ID, this,
				    new Double(newHeight));
	}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	super.writeExternal(out);
    }
}
