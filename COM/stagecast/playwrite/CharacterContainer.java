/* CharacterContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface CharacterContainer extends GenericContainer, Worldly
{
    public void add(CocoaCharacter cocoacharacter, int i, int i_0_, int i_1_);
    
    public void remove(CocoaCharacter cocoacharacter);
    
    public void deleteCharacter(CocoaCharacter cocoacharacter);
    
    public void undeleteCharacter(CocoaCharacter cocoacharacter);
    
    public void relocate(CocoaCharacter cocoacharacter, int i, int i_2_,
			 int i_3_);
    
    public void changeAppearance(CocoaCharacter cocoacharacter,
				 Appearance appearance,
				 Appearance appearance_4_);
    
    public void update(CocoaCharacter cocoacharacter, Variable variable);
    
    public int getZ(CocoaCharacter cocoacharacter);
    
    public int setZ(CocoaCharacter cocoacharacter, int i);
    
    public void makeVisible(CocoaCharacter cocoacharacter);
}
