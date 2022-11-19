/* SoundVariable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Enumeration;

class SoundVariable extends EnumeratedVariable
{
    protected SoundVariable(String sysvarID, String name) {
	super(sysvarID, name, true);
	this.setDefaultValue(PlaywriteSound.nullSound);
    }
    
    public Object getLegalValue(VariableOwner owner, Object value) {
	if (value == null)
	    return PlaywriteSound.nullSound;
	return super.getLegalValue(owner, value);
    }
    
    public void notifyChanged(VariableOwner owner, Object oldVal,
			      Object value) {
	if (owner instanceof CharacterInstance
	    || owner instanceof GCAlias && RuleEditor.isRuleEditing())
	    ((PlaywriteSound) value).play((CocoaCharacter) owner);
	super.notifyChanged(owner, oldVal, value);
    }
    
    String nameOf(Object value) {
	return ((PlaywriteSound) value).getName();
    }
    
    public Enumeration legalValues(VariableOwner owner) {
	return owner.getWorld().getSounds().getContents();
    }
}
