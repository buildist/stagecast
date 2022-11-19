/* ToolTipResources - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite.internationalization;
import java.util.Hashtable;
import java.util.ListResourceBundle;

import COM.stagecast.playwrite.PlaywriteRoot;

public class ToolTipResources extends ListResourceBundle
    implements ResourceIDs.ControlPanelIDs, ResourceIDs.HelpWindowIDs,
	       ResourceIDs.PicturePainterIDs, ResourceIDs.RuleEditorIDs,
	       ResourceIDs.WorldViewIDs, ResourceIDs.DrawerIDs,
	       ResourceIDs.CharacterWindowIDs, ResourceIDs.ToolIDs,
	       ResourceIDs.LookAndFeelIDs, ResourceIDs.TutorialWindowIDs
{
    public static final String ALT = "ALT ";
    private static final Object[][] contents
	= { { "Win close", "close this window" },
	    { "WW Show Menu", "Creator menu" },
	    { "WW Show Menu Player", "Player menu" },
	    { "WW Show Sidelines", "show the sidelines" },
	    { "ALT WW Show Sidelines", "hide the sidelines" },
	    { "WW Show Control Panel", "show the control panel" },
	    { "ALT WW Show Control Panel", "hide the control panel" },
	    { "CP new character tool cursor", "create a character" },
	    { "CP edit appearance", "draw an appearance" },
	    { "CP new rule", "make a rule" }, { "CP copy tool", "copy" },
	    { "CP delete tool", "delete" },
	    { "home square tool button", "set anchor square" },
	    { "new variable tool", "create a variable" },
	    { "CP reset", "reset" }, { "CP rewind", "rewind" },
	    { "CP step back", "one step backward" }, { "CP stop", "stop" },
	    { "CP step forward", "one step forward" }, { "CP Play", "play" },
	    { "CP SLOW", "slow" }, { "CP MEDIUM", "medium" },
	    { "CP FAST", "fast" }, { "CP MAX", "as fast as possible" },
	    { "help window back button", "go back one page" },
	    { "cwShowV", "show the variables" },
	    { "cwOpenSub", "open rule box" },
	    { "cwCloseSub", "close rule box" },
	    { "cwNCmt", "create new comment" },
	    { "cwBreak", "set a breakpoint" },
	    { "cwStepBtn", "try the next rule" },
	    { "cwDis", "disable a rule" },
	    { "cwNewSubBtn", "create a rule box" },
	    { "cwPre", "add a pretest" },
	    { "ALT cwTogV", "hide the variables" },
	    { "cwTogV", "show the variables" },
	    { "Picture Painter Edit Menu", "edit menu" },
	    { "Picture Painter Fonts Menu", "fonts menu" },
	    { "Picture Painter Command Clear", "clear picture" },
	    { "Picture Painter Command Revert", "revert picture" },
	    { "Picture Painter Command Undo", "undo/redo edit" },
	    { "Picture Painter Eraser Tool", "eraser tool" },
	    { "Picture Painter Eye Dropper Tool", "color selection tool" },
	    { "Picture Painter Lasso Tool", "free-form selection tool" },
	    { "Picture Painter Line Tool", "line tool" },
	    { "Picture Painter Oval Tool", "oval tool" },
	    { "Picture Painter Paint Brush Tool", "paint brush tool" },
	    { "Picture Painter Paint Bucket Tool", "paint bucket tool" },
	    { "Picture Painter Pencil Tool", "pencil tool" },
	    { "Picture Painter Rectangular Selection Tool", "selection tool" },
	    { "Picture Painter Rectangle Tool", "rectangle tool" },
	    { "Picture Painter Text Tool", "text tool" },
	    { "Picture Painter Brush Size 1", "1 pixel brush" },
	    { "Picture Painter Brush Size 3", "3 pixel brush" },
	    { "Picture Painter Brush Size 5", "5 pixel brush" },
	    { "Picture Painter Brush Size 7", "7 pixel brush" },
	    { "Picture Painter Brush Size 9", "9 pixel brush" },
	    { "Picture Painter Scale Bar", "magnify" },
	    { "Picture Painter get button", "import gif or jpeg" },
	    { "Picture Painter new button", "create picture" },
	    { "RE e", "examine a square" }, { "RE er", "edit this rule" },
	    { "RE tr", "test this rule" }, { "RE sv", "show variables" },
	    { "RE c", "show the calculator" }, { "RE st", "show the tests" },
	    { "ALT RE st", "hide the tests" },
	    { "RE nt", "create a variable test" },
	    { "RE kt", "create a key test" },
	    { "RE mt", "create a mouse test" },
	    { "RE dc", "don't care what else" },
	    { "RE sa", "show the actions" },
	    { "ALT RE sa", "hide the actions" },
	    { "RE pa", "create a put action" },
	    { "RE pca", "create a put calculation action" },
	    { "RE open url", "create an open URL action" },
	    { "drawer gsl", "import a sound" },
	    { "SD Toggle Stage Split", "split stage" },
	    { "ALT SD Toggle Stage Split", "unsplit stage" },
	    { "tutorial n", "next page" }, { "tutorial p", "previous page" } };
    
    static {
	if (PlaywriteRoot.isCustomerBuild() == false) {
	    boolean throwException = false;
	    Hashtable ourResourcesTable = new Hashtable(contents.length);
	    for (int i = 0; i < contents.length; i++) {
		if (ourResourcesTable.contains(contents[i][0]) == true) {
		    String message
			= ("Duplicate resource found in "
			   + ToolTipResources.class + ": " + contents[i][0]);
		    System.err.println(message);
		    throwException = true;
		}
		ourResourcesTable.put(contents[i][0], contents[i][0]);
	    }
	    if (throwException == true)
		throw new RuntimeException
			  ("Invalid resources found in "
			   + ToolTipResources.class
			   + ".  See System.err output for details.");
	}
    }
    
    public Object[][] getContents() {
	return contents;
    }
}
