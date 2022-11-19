/* Tool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class Tool
    implements ResourceIDs.ToolIDs, Target, ToolSource, Debug.Constants
{
    static final String TOOL_CMND = "Start Tool";
    public static Tool newCharacterTool;
    public static Tool editAppearanceTool;
    public static Tool newRuleTool;
    public static Tool copyLoadTool;
    public static Tool copyPlaceTool;
    public static Tool deleteTool;
    static Bitmap toolSeparator;
    private ToolSource source;
    private View sourceView;
    private Bitmap cursorImage;
    private Bitmap buttonImage;
    private Bitmap altButtonImage;
    private Point _hotSpot;
    private boolean _optionClickEnabled;
    private boolean _dragEnabled;
    private String _warningMsg;
    private String toolTip;
    private boolean _scrollerMapping = false;
    
    public static class NewCharacterButton extends ToolButton
    {
	int image_index = 0;
	
	public NewCharacterButton(Tool tool) {
	    super(tool);
	}
	
	public void sendCommand() {
	    this.getTool().setCursorBitmap
		(Resource.getImage("CP new character tool cursor",
				   new Object[] { new Integer(image_index) }));
	    super.sendCommand();
	}
	
	public void setImageIndex(int index) {
	    image_index = index;
	    this.setTBBitmaps
		(Resource.getImage("CP new character button",
				   new Object[] { new Integer(0),
						  new Integer(image_index) }),
		 Resource.getImage("CP new character button",
				   new Object[] { new Integer(1),
						  new Integer(image_index) }));
	}
    }
    
    static void initStatics() {
	newCharacterTool = new Tool((Resource.getImage
				     ("CP new character tool cursor",
				      new Object[] { new Integer(0) })),
				    (Resource.getImage
				     ("CP new character button",
				      new Object[] { new Integer(0),
						     new Integer(0) })),
				    (Resource.getImage
				     ("CP new character button",
				      new Object[] { new Integer(1),
						     new Integer(0) })),
				    null, false) {
	    PlaywriteButton makeButton() {
		Tool.NewCharacterButton toolButton
		    = new Tool.NewCharacterButton(this);
		if (((Tool) this).toolTip != null)
		    toolButton.setToolTipText(((Tool) this).toolTip);
		return toolButton;
	    }
	};
	newCharacterTool.toolTip
	    = Resource.getToolTip("CP new character tool cursor");
	newCharacterTool.setWarningResource("CP nc warn");
	editAppearanceTool
	    = createTool("CP edit appearance", "CP edit appearance btn");
	editAppearanceTool.setWarningResource("CP ea warn");
	newRuleTool = createTool("CP new rule", "CP new rule button");
	newRuleTool.setWarningResource("CP nr warn");
	copyLoadTool = createTool("CP copy tool", "CP copy tool button");
	copyLoadTool.setHotSpot(new Point(10, 17));
	copyLoadTool.setWarningResource("CP ct warn");
	copyPlaceTool = new Tool(Resource.getImage("CP copy tool"), null);
	copyPlaceTool.setHotSpot(new Point(10, 17));
	copyPlaceTool.setOptionClickEnabled(true);
	copyPlaceTool.setDragEnabled(true);
	copyPlaceTool.setScrollerMappingEnabled(true);
	copyPlaceTool.setWarningResource("CP ctp warn");
	deleteTool = createTool("CP delete tool", "CP delete tool button");
	deleteTool.setOptionClickEnabled(true);
	deleteTool.setDragEnabled(true);
	deleteTool.setWarningResource("CP d warn");
	toolSeparator = Resource.getImage("CP tool separator");
    }
    
    Tool(Bitmap cursorImage, Bitmap buttonImage) {
	this(cursorImage, buttonImage, null);
	source = this;
    }
    
    Tool(Bitmap cursorImage, Bitmap buttonImage, ToolSource source) {
	this(cursorImage, buttonImage, source, false);
    }
    
    Tool(Bitmap cursorImage, Bitmap buttonImage, ToolSource source,
	 boolean enableOptionClicking) {
	this(cursorImage == null ? buttonImage : cursorImage,
	     buttonImage == null ? cursorImage : buttonImage, null, source,
	     enableOptionClicking);
    }
    
    Tool(Bitmap cursorImage, Bitmap buttonImage, Bitmap altButtonImage,
	 ToolSource source, boolean enableOptionClicking) {
	this.cursorImage = cursorImage;
	this.buttonImage = buttonImage;
	this.altButtonImage = altButtonImage;
	this.source = source == null ? (ToolSource) this : source;
	_hotSpot = null;
	_optionClickEnabled = enableOptionClicking;
	_dragEnabled = false;
    }
    
    static Tool createTool(String toolResourceID, String buttonResourceID) {
	Tool newTool = new Tool(Resource.getImage(toolResourceID),
				Resource.getButtonImage(buttonResourceID),
				Resource.getAltButtonImage(buttonResourceID),
				null, false);
	newTool.toolTip = Resource.getToolTip(toolResourceID);
	return newTool;
    }
    
    Bitmap getCursorBitmap() {
	return cursorImage;
    }
    
    void setCursorBitmap(Bitmap b) {
	cursorImage = b;
    }
    
    Bitmap getButtonBitmap() {
	return buttonImage;
    }
    
    Bitmap getAltButtonBitmap() {
	return altButtonImage;
    }
    
    void setOptionClickEnabled(boolean b) {
	_optionClickEnabled = b;
    }
    
    boolean isOptionClickEnabled() {
	return _optionClickEnabled;
    }
    
    void setDragEnabled(boolean b) {
	_dragEnabled = b;
    }
    
    boolean isDragEnabled() {
	return _dragEnabled;
    }
    
    void setToolTipResource(String id) {
	toolTip = Resource.getToolTip(id);
    }
    
    void setWarningResource(String id) {
	_warningMsg = Resource.getText(id);
    }
    
    String getWarningMsg() {
	return _warningMsg;
    }
    
    void setHotSpot(Point hot) {
	_hotSpot = hot;
    }
    
    void setScrollerMappingEnabled(boolean flag) {
	_scrollerMapping = flag;
    }
    
    boolean getScrollerMapping() {
	return _scrollerMapping;
    }
    
    static PlaywriteView makeSeparator() {
	return new PlaywriteView(toolSeparator);
    }
    
    PlaywriteButton makeButton() {
	ToolButton toolButton = new ToolButton(this);
	if (toolTip != null)
	    toolButton.setToolTipText(toolTip);
	return toolButton;
    }
    
    protected ToolSession createToolSession(ToolSource source, Image image,
					    int x, int y, Object data) {
	ToolSession s = new ToolSession(source, image, x, y, this, data);
	if (_hotSpot != null)
	    s.setHotSpot(_hotSpot.x, _hotSpot.y);
	return s;
    }
    
    ToolSession newSession(ToolSource source, int x, int y, Object data) {
	return createToolSession(source, getCursorBitmap(), x, y, data);
    }
    
    ToolSession newSession(int x, int y, Object data) {
	return newSession(source, x, y, data);
    }
    
    public View sourceView(ToolSession session) {
	return sourceView;
    }
    
    public void toolWasAccepted(ToolSession session) {
	/* empty */
    }
    
    public void toolWasRejected(ToolSession session) {
	/* empty */
    }
    
    public void sessionEnded(ToolSession session) {
	sourceView = null;
    }
    
    public void performCommand(String command, Object data) {
	if ("Start Tool".equals(command)) {
	    Debug.print("debug.tool", "Tool.performCommand called with data: ",
			data);
	    sourceView = ((PlaywriteButton) data).superview();
	    newSession(source, 0, 0, null);
	} else
	    Debug.print("debug.commands", "Unknown command in tool: ",
			command);
    }
}
