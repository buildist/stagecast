/* ImageResources - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite.internationalization;
import java.util.Hashtable;
import java.util.ListResourceBundle;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.playwrite.BitmapManager;
import COM.stagecast.playwrite.PlaywriteRoot;
import COM.stagecast.playwrite.ResourceStreamProducer;

public class ImageResources extends ListResourceBundle
    implements ResourceIDs.BeforeBoardIDs, ResourceIDs.CharacterWindowIDs,
	       ResourceIDs.CommandIDs, ResourceIDs.ControlPanelIDs,
	       ResourceIDs.DoorIDs, ResourceIDs.DrawerIDs,
	       ResourceIDs.ExamineWindowIDs, ResourceIDs.LookAndFeelIDs,
	       ResourceIDs.OvalButtonIDs, ResourceIDs.PicturePainterIDs,
	       ResourceIDs.RuleEditorIDs, ResourceIDs.RuleScrapIDs,
	       ResourceIDs.RuleSlotIDs, ResourceIDs.RuleTestIDs,
	       ResourceIDs.ScrapBorderIDs, ResourceIDs.SplashScreenIDs,
	       ResourceIDs.TitleBarIDs, ResourceIDs.ToolIDs,
	       ResourceIDs.TutorialSplashScreenIDs,
	       ResourceIDs.TutorialWindowIDs, ResourceIDs.VariableIDs,
	       ResourceIDs.WorldViewIDs
{
    static Object[][] contents
	= { { "GraySplat", "appearances/character gray.gif" },
	    { "TextProto", "appearances/text character.gif" },
	    { "CP clock img", "backgrounds/clock/{0, number, integer}.gif" },
	    { "CP frame", "backgrounds/speed control.gif" },
	    { "CP reset", "buttons/reset button{0, choice, 0#|1# down}.gif" },
	    { "CP rewind", "buttons/rewind{0, choice, 0#|1# down}.gif" },
	    { "CP step back",
	      "buttons/step backward{0, choice, 0#|1# down}.gif" },
	    { "CP stop", "buttons/stop{0, choice, 0#|1# down}.gif" },
	    { "CP step forward",
	      "buttons/step forward{0, choice, 0#|1# down}.gif" },
	    { "CP Play", "buttons/play{0, choice, 0#|1# down}.gif" },
	    { "CP speedup", "buttons/speed.gif" },
	    { "CP speeddown", "buttons/speed down.gif" },
	    { "door left arrow", "appearances/door arrow left.gif" },
	    { "door right arrow", "appearances/door arrow right.gif" },
	    { "door default appearance", "appearances/door.gif" },
	    { "door end appearance", "appearances/door end.gif" },
	    { "splash quit",
	      "buttons/splash/quit{0, choice, 0#|1# down}.jpg" },
	    { "splash new", "buttons/splash/new{0, choice, 0#|1# down}.jpg" },
	    { "splash open",
	      "buttons/splash/open{0, choice, 0#|1# down}.jpg" },
	    { "splash learn",
	      "buttons/splash/learn{0, choice, 0#|1# down}.jpg" },
	    { "logo unfilled", "backgrounds/logo-noblack-alpha.gif" },
	    { "tutorial splash back",
	      "buttons/back button{0, choice, 0#|1# down}.jpg" },
	    { "tutorial splash quit",
	      "buttons/tutorial quit{0, choice, 0#|1# down}.jpg" },
	    { "tutorial learn more", "interface/tutorial_learn_more.gif" },
	    { "tutorial learn more halo",
	      "interface/tutorial_learn_more_halo.gif" },
	    { "tutorial or img", "interface/tutorial_or.gif" },
	    { "Win close", "buttons/check button{0, choice, 0#|1# down}.gif" },
	    { "view top", "backgrounds/window border top.gif" },
	    { "view bot", "backgrounds/window border bottom.gif" },
	    { "view lft", "backgrounds/window border left.gif" },
	    { "view rt", "backgrounds/window border right.gif" },
	    { "view ll corner", "backgrounds/resize window left.gif" },
	    { "view lr corner", "backgrounds/resize window right.gif" },
	    { "view tl corner", "backgrounds/window corner left.gif" },
	    { "view tr corner", "backgrounds/window corner right.gif" },
	    { "Oval Button sub", "buttons/small tool.gif" },
	    { "Oval Button sdb", "buttons/small tool down.gif" },
	    { "CP tool separator", "backgrounds/control panel separator.gif" },
	    { "CP edit appearance", "cursors/edit appearance cursor.gif" },
	    { "CP edit appearance btn",
	      "buttons/edit appearance{0, choice, 0#|1# down}.gif" },
	    { "CP new rule", "cursors/new rule cursor.gif" },
	    { "CP new rule button",
	      "buttons/new rule{0, choice, 0#|1# down}.gif" },
	    { "CP copy tool", "cursors/copy cursor.gif" },
	    { "CP copy tool button",
	      "buttons/copy{0, choice, 0#|1# down}.gif" },
	    { "CP delete tool", "cursors/delete cursor.gif" },
	    { "CP delete tool button",
	      "buttons/delete{0, choice, 0#|1# down}.gif" },
	    { "home square tool button", "buttons/home-square-tool.gif" },
	    { "CP new character",
	      "appearances/character{0, number, integer}.gif" },
	    { "CP new character button",
	      "buttons/new character/new character{1, number, integer}{0, choice, 0#|1# down}.gif" },
	    { "CP new character tool cursor",
	      "cursors/new character/character{0, number, integer} cursor.gif" },
	    { "new variable tool", "cursors/new variable cursor.gif" },
	    { "new variable tool button",
	      "buttons/new variable{0, choice, 0#|1# down}.gif" },
	    { "NoSound", "buttons/no sound icon.gif" },
	    { "Sound", "buttons/sound icon.gif" },
	    { "UnSound", "buttons/unplayable sound.gif" },
	    { "command c",
	      "buttons/cancel button{0, choice, 0#|1# down}.gif" },
	    { "command done",
	      "buttons/check button{0, choice, 0#|1# down}.gif" },
	    { "command c w. text",
	      "buttons/cancel button text{0, choice, 0#|1# down}.gif" },
	    { "command done w. text",
	      "buttons/check button text{0, choice, 0#|1# down}.gif" },
	    { "LeftArrow", "buttons/left arrow.gif" },
	    { "LeftArrowDown", "buttons/left arrow down.gif" },
	    { "RightArrow", "buttons/right arrow.gif" },
	    { "RightArrowDown", "buttons/right arrow down.gif" },
	    { "TopArrow", "buttons/top arrow.gif" },
	    { "TopArrowDown", "buttons/top arrow down.gif" },
	    { "BottomArrow", "buttons/bottom arrow.gif" },
	    { "BottomArrowDown", "buttons/bottom arrow down.gif" },
	    { "TB tgray", "backgrounds/title bar gray top.gif" },
	    { "TB bgray", "backgrounds/title bar gray bottom.gif" },
	    { "TB tbrown", "backgrounds/title bar brown top.gif" },
	    { "TB bbrown", "backgrounds/title bar brown bottom.gif" },
	    { "TB trust", "backgrounds/title bar rust top.gif" },
	    { "TB brust", "backgrounds/title bar rust bottom.gif" },
	    { "TB tpurple", "backgrounds/title bar purple top.gif" },
	    { "TB bpurple", "backgrounds/title bar purple bottom.gif" },
	    { "TB tgreen", "backgrounds/title bar green top.gif" },
	    { "TB bgreen", "backgrounds/title bar green bottom.gif" },
	    { "TB tblue", "backgrounds/title bar blue top.gif" },
	    { "TB bblue", "backgrounds/title bar blue bottom.gif" },
	    { "TB tperiwinkle", "backgrounds/title bar periwinkle top.gif" },
	    { "TB bperiwinkle",
	      "backgrounds/title bar periwinkle bottom.gif" },
	    { "HandleH", "buttons/handleP bottom{0, choice, 0#|1# open}.gif" },
	    { "HandleV", "buttons/handleP right{0, choice, 0#|1# open}.gif" },
	    { "HandleH Red",
	      "buttons/handleR bottom{0, choice, 0#|1# open}.gif" },
	    { "HandleH Green",
	      "buttons/handleG bottom{0, choice, 0#|1# open}.gif" },
	    { "WW Show Menu",
	      "buttons/menu symbol{0, choice, 0#|1# down}.gif" },
	    { "splash screen player image",
	      "backgrounds/splash screen player.jpg" },
	    { "splash screen tile", "backgrounds/splash tile.jpg" },
	    { "splash screen authoring image",
	      "backgrounds/splash screen.jpg" },
	    { "splash screen pro image", "backgrounds/splash screen pro.jpg" },
	    { "SpotlightLeft", "backgrounds/spotlight handle left.gif" },
	    { "SpotlightRight", "backgrounds/spotlight handle right.gif" },
	    { "SpotlightTop", "backgrounds/spotlight handle top.gif" },
	    { "SpotlightBottom", "backgrounds/spotlight handle bottom.gif" },
	    { "BigArrow", "backgrounds/big arrow.gif" },
	    { "LittleArrow", "backgrounds/little arrow.gif" },
	    { "MouseClickIndi", "interface/mouse click indicator.gif" },
	    { "dcsTool", "backgrounds/dont care square.gif" },
	    { "dcsN", "backgrounds/dontcareN.gif" },
	    { "dcsS", "backgrounds/dontcareS.gif" },
	    { "dcsE", "backgrounds/dontcareE.gif" },
	    { "dcsW", "backgrounds/dontcareW.gif" },
	    { "cwDis", "cursors/disable rule cursor.gif" },
	    { "cwDisBtn", "buttons/disable rule{0, choice, 0#|1# down}.gif" },
	    { "cwNCmt", "cursors/new comment cursor.gif" },
	    { "cwNCmtBtn",
	      "buttons/comment button{0, choice, 0#|1# down}.gif" },
	    { "cwPre", "cursors/new pretest cursor.gif" },
	    { "cwPreBtn", "buttons/pretest{0, choice, 0#|1# down}.gif" },
	    { "cwBreak", "cursors/breakpoint cursor.gif" },
	    { "cwBreakBtn", "buttons/breakpoint{0, choice, 0#|1# down}.gif" },
	    { "cwNewSubBtn",
	      "buttons/new subroutine button{0, choice, 0#|1# down}.gif" },
	    { "cwStepBtn",
	      "buttons/step rule button{0, choice, 0#|1# down}.gif" },
	    { "cwSeparator", "interface/horiz separator.gif" },
	    { "finger image ID", "interface/sequence finger.gif" },
	    { "picture painter menu triangle", "interface/menu triangle.gif" },
	    { "Picture Painter Menu Item Check", "interface/menu check.gif" },
	    { "Picture Painter Menu Item Uncheck",
	      "interface/menu uncheck.gif" },
	    { "Picture Painter Command Clear",
	      "buttons/clear button{0, choice, 0#|1# down}.gif" },
	    { "Picture Painter Edit Menu",
	      "buttons/edit menu button{0, choice, 0#|1# down}.gif" },
	    { "Picture Painter Fonts Menu",
	      "buttons/font menu button{0, choice, 0#|1# down}.gif" },
	    { "Picture Painter get button",
	      "buttons/get button{0, choice, 0#|1# down}.gif" },
	    { "Picture Painter new button",
	      "buttons/new button{0, choice, 0#|1# down}.gif" },
	    { "Picture Painter Command Revert",
	      "buttons/revert button{0, choice, 0#|1# down}.gif" },
	    { "Picture Painter Command Undo",
	      "buttons/undo button{0, choice, 0#|1# down}.gif" },
	    { "Picture Painter Scale Bar", "tools/scalebar.gif" },
	    { "Picture Painter Scale Bar Hilite", "tools/sbar_lit.gif" },
	    { "Picture Painter Eraser Tool", "tools/eraser.gif" },
	    { "Picture Painter Eye Dropper Tool", "tools/dropper.gif" },
	    { "Picture Painter Lasso Tool", "tools/lasso.gif" },
	    { "Picture Painter Line Tool", "tools/line.gif" },
	    { "Picture Painter Paint Brush Tool", "tools/brush.gif" },
	    { "Picture Painter Paint Bucket Tool", "tools/bucket.gif" },
	    { "Picture Painter Pencil Tool", "tools/pencil.gif" },
	    { "Picture Painter Rectangular Selection Tool",
	      "tools/select.gif" },
	    { "Picture Painter Text Tool", "tools/text.gif" },
	    { "RE dc", "cursors/dont care cursor.gif" },
	    { "RE dcb", "buttons/dont care{0, choice, 0#|1# down}.gif" },
	    { "RE mt", "cursors/mouse test cursor.gif" },
	    { "RE mtb",
	      "buttons/mouse test button{0, choice, 0#|1# down}.gif" },
	    { "RE e", "cursors/examine cursor.gif" },
	    { "RE eb", "buttons/examine{0, choice, 0#|1# down}.gif" },
	    { "RE sv", "cursors/variable window cursor.gif" },
	    { "RE svb", "buttons/variable window{0, choice, 0#|1# down}.gif" },
	    { "EWi x", "interface/x.gif" },
	    { "EWi chk", "interface/check.gif" },
	    { "RE kt", "buttons/key test{0, choice, 0#|1# down}.gif" },
	    { "RE nt", "buttons/boolean test{0, choice, 0#|1# down}.gif" },
	    { "RE pa", "buttons/put action{0, choice, 0#|1# down}.gif" },
	    { "RE pca",
	      "buttons/put calculation action{0, choice, 0#|1# down}.gif" },
	    { "RE open url", "buttons/url{0, choice, 0#|1# down}.gif" },
	    { "RE c", "buttons/calculator{0, choice, 0#|1# down}.gif" },
	    { "RE co", "interface/calculation.gif" },
	    { "RE dicey", "buttons/dice.gif" },
	    { "RE er", "buttons/edit button{0, choice, 0#|1# down}.gif" },
	    { "RE tr", "buttons/test button{0, choice, 0#|1# down}.gif" },
	    { "drawer gsl", "buttons/get button{0, choice, 0#|1# down}.gif" },
	    { "drawer new stage button",
	      "buttons/new button{0, choice, 0#|1# down}.gif" },
	    { "drawer new jar button",
	      "buttons/new button{0, choice, 0#|1# down}.gif" },
	    { "RS comment",
	      "buttons/comment button small{0, choice, 0#|1# down}.gif" },
	    { "TestLeft", "interface/testborderleft.gif" },
	    { "TestTop", "interface/testbordertop.gif" },
	    { "TestRight", "interface/testborderright.gif" },
	    { "TestBottom", "interface/testborderbottom.gif" },
	    { "TESTGLeft", "interface/testborderleftG.gif" },
	    { "TESTGTop", "interface/testbordertopG.gif" },
	    { "TESTGRight", "interface/testborderrightG.gif" },
	    { "TESTGBottom", "interface/testborderbottomG.gif" },
	    { "TESTRLeft", "interface/testborderleftR.gif" },
	    { "TESTRTop", "interface/testbordertopR.gif" },
	    { "TESTRRight", "interface/testborderrightR.gif" },
	    { "TESTRBottom", "interface/testborderbottomR.gif" },
	    { "AliasLeft", "interface/aliasborderleft.gif" },
	    { "AliasTop", "interface/aliasbordertop.gif" },
	    { "AliasRight", "interface/aliasborderright.gif" },
	    { "AliasBottom", "interface/aliasborderbottom.gif" },
	    { "MenuLeft", "interface/menuborderleft.gif" },
	    { "MenuTop", "interface/menubordertop.gif" },
	    { "MenuRight", "interface/menuborderright.gif" },
	    { "MenuBottom", "interface/menuborderbottom.gif" },
	    { "MenuArrow", "interface/menuarrow.gif" },
	    { "drawer stages split1", "buttons/stage split 1.gif" },
	    { "drawer stages split2", "buttons/stage split 2.gif" },
	    { "drawer stages button",
	      "buttons/stage drawer{0, choice, 0#|1# down}.gif" },
	    { "drawer sounds button",
	      "buttons/sounds drawer{0, choice, 0#|1# down}.gif" },
	    { "drawer jars button",
	      "buttons/jar drawer{0, choice, 0#|1# down}.gif" },
	    { "drawer globals button",
	      "buttons/globals drawer{0, choice, 0#|1# down}.gif" },
	    { "drawer characters button",
	      "buttons/character drawer{0, choice, 0#|1# down}.gif" },
	    { "drawer specials button",
	      "buttons/special drawer{0, choice, 0#|1# down}.gif" },
	    { "RSL0L", "interface/rule no light.gif" },
	    { "RSLGL", "interface/rule green light.gif" },
	    { "RSLRL", "interface/rule red light.gif" },
	    { "RSLBL", "interface/rule yellow light.gif" },
	    { "RSLBLK", "interface/rule black light.gif" },
	    { "RSLDL", "interface/disabled rule.gif" },
	    { "mouse test image", "cursors/mouse test cursor.gif" },
	    { "Agent0", "backgrounds/tutorial character 0.gif" },
	    { "Agent1", "backgrounds/tutorial character 0.gif" },
	    { "Agent2", "backgrounds/tutorial character 0.gif" },
	    { "Agent3", "backgrounds/tutorial character 0.gif" },
	    { "tutorial n", "buttons/tutorial_next.gif" },
	    { "tutorial p", "buttons/tutorial_previous.gif" },
	    { "SwitchOff", "buttons/light switch off.gif" },
	    { "SwitchOn", "buttons/light switch on.gif" },
	    { "VariablePopup", "buttons/variable popup.gif" },
	    { "WorldIcon", "buttons/world.gif" },
	    { "WW icon", "backgrounds/world title icon.gif" } };
    private static Bitmap fakeServerBitmap;
    
    static {
	if (PlaywriteRoot.isCustomerBuild() == false) {
	    boolean throwException = false;
	    Hashtable ourResourcesTable = new Hashtable(contents.length);
	    for (int i = 0; i < contents.length; i++) {
		if (ourResourcesTable.contains(contents[i][0]) == true) {
		    String message
			= ("Duplicate resource found in "
			   + ImageResources.class + ": " + contents[i][0]);
		    System.err.println(message);
		    throwException = true;
		}
		ourResourcesTable.put(contents[i][0], contents[i][0]);
	    }
	    if (throwException == true)
		throw new RuntimeException
			  ("Invalid resources found in " + ImageResources.class
			   + ".  See System.err output for details.");
	}
	fakeServerBitmap = null;
    }
    
    public static Bitmap fetch(String name) {
	String resName = "/COM/stagecast/creator/images/" + name;
	return fetch(ImageResources.class, resName);
    }
    
    public static Bitmap fetch(Class classObject, String resName) {
	PlaywriteRoot.app();
	if (PlaywriteRoot.isServer()) {
	    if (fakeServerBitmap == null)
		fakeServerBitmap = BitmapManager.createBitmapManager(1, 1);
	    return fakeServerBitmap;
	}
	ResourceStreamProducer sp
	    = new ResourceStreamProducer(classObject, resName);
	return BitmapManager.createNativeBitmapManager(sp);
    }
    
    public Object[][] getContents() {
	return contents;
    }
}