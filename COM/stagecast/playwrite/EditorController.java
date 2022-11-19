/* EditorController - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface EditorController
{
    public static interface Editor extends Worldly
    {
	public boolean isEditing(Object object);
	
	public void objectChanged(Object object);
    }
    
    public Class[] getEditedClasses();
    
    public boolean canEdit(Object object);
    
    public boolean displayEditorFor(Object object, ViewGlue viewglue);
    
    public Editor getEditorFor(Object object);
    
    public void destroyEditorFor(Object object);
}
