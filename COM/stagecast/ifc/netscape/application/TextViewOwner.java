/* TextViewOwner - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface TextViewOwner
{
    public void textEditingDidBegin(TextView textview);
    
    public void textEditingDidEnd(TextView textview);
    
    public void textWillChange(TextView textview, Range range);
    
    public void textDidChange(TextView textview, Range range);
    
    public void attributesWillChange(TextView textview, Range range);
    
    public void attributesDidChange(TextView textview, Range range);
    
    public void selectionDidChange(TextView textview);
    
    public void linkWasSelected(TextView textview, Range range, String string);
}
