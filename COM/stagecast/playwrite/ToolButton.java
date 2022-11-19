/* ToolButton - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class ToolButton extends PlaywriteButton
    implements ResourceIDs.OvalButtonIDs
{
    private Tool tool;
    
    static void initStatics() {
	/* empty */
    }
    
    private ToolButton(Tool tool, Bitmap buttonBitmap) {
	this(tool, buttonBitmap, buttonBitmap);
    }
    
    private ToolButton(Tool tool, Bitmap upBitmap, Bitmap downBitmap) {
	super(upBitmap, downBitmap);
	this.setTransparent(true);
	this.tool = tool;
	this.setType(0);
    }
    
    ToolButton(Tool tool, String command, Target target) {
	this(tool, tool.getButtonBitmap(), tool.getAltButtonBitmap());
	this.setCommand(command);
	this.setTarget(target);
    }
    
    ToolButton(Tool tool) {
	this(tool, "Start Tool", tool);
    }
    
    Tool getTool() {
	return tool;
    }
    
    void setTBBitmaps(Bitmap up, Bitmap down) {
	this.setImage(up);
	this.setAltImage(down);
	this.setDirty(true);
    }
}
