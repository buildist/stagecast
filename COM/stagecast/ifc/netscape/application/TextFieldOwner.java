/* TextFieldOwner - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface TextFieldOwner
{
    public static final int TAB_KEY = 0;
    public static final int BACKTAB_KEY = 1;
    public static final int RETURN_KEY = 2;
    public static final int LOST_FOCUS = 3;
    public static final int RESIGNED_FOCUS = 4;
    
    public void textEditingDidBegin(TextField textfield);
    
    public void textWasModified(TextField textfield);
    
    public boolean textEditingWillEnd(TextField textfield, int i,
				      boolean bool);
    
    public void textEditingDidEnd(TextField textfield, int i, boolean bool);
}
