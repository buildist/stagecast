/* Bindable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Vector;

public interface Bindable extends Named
{
    public boolean binds(CharacterInstance characterinstance);
    
    public Appearance makeAppearance(Appearance appearance);
    
    public Vector topLevelJars();
    
    public void wasAddedToJar(Jar jar);
    
    public void wasRemovedFromJar(Jar jar);
}
