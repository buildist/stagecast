/* VariableOwner - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.util.Enumeration;

public interface VariableOwner
    extends FirstClassValue, Named, Worldly, Verifiable
{
    public Enumeration getVariables();
    
    public VariableList getVariableList();
    
    public VariableOwner getVariableListOwner();
    
    public boolean refersTo(ReferencedObject referencedobject);
    
    public boolean affectsDisplay(Variable variable);
}
