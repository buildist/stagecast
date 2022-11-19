/* BindsAnything - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class BindsAnything implements Bindable, ResourceIDs.RuleEditorIDs
{
    public boolean binds(CharacterInstance ch) {
	return true;
    }
    
    public Appearance makeAppearance(Appearance appearance) {
	return new AnythingAppearance(appearance);
    }
    
    public Vector topLevelJars() {
	return null;
    }
    
    public void wasAddedToJar(Jar j) {
	/* empty */
    }
    
    public void wasRemovedFromJar(Jar j) {
	/* empty */
    }
    
    public String getName() {
	return Resource.getText("RE any");
    }
    
    public void setName(String name) {
	/* empty */
    }
}
