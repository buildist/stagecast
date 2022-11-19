/* PaintField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.image.RGBImageFilter;
import java.util.Hashtable;
import java.util.Stack;

import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.DragDestination;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.KeyEvent;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class PaintField extends AppearanceEditorView
    implements AppearanceEventListener, DragDestination,
	       ResourceIDs.PicturePainterIDs, ResourceIDs.ToolIDs,
	       ToolDestination
{
    public static final Size GRID_SIZE = new Size(8, 8);
    public static final int NO_GRID_UPDATE = 0;
    public static final int RECTANGLE_GRID_UPDATE = 1;
    public static final int PIXEL_GRID_UPDATE = 2;
    public static final Tool HOME_SQUARE_TOOL
	= new Tool(null, Resource.getImage("home square tool button"));
    private static final Color GRID_LINE_COLOR = new Color(165, 165, 165);
    private static final Color UNUSED_GRID_COLOR = new Color(222, 222, 222);
    private static final Color HOME_SQUARE_COLOR = Color.red;
    private static Hashtable clipboardTable = new Hashtable(3);
    private static Hashtable universalPixelArrayTable = new Hashtable(3);
    private Bitmap _offscreenBuffer;
    private PaintState currentState;
    private PaintState lastState;
    private PaintFieldSelection currentSelection;
    private PaintFieldSelection lastSelection;
    private Vector displayVariables;
    private int scale = 8;
    private Point origin = new Point(0, 0);
    private boolean highlighted = false;
    private int squareSize = 32;
    private boolean[][] paintFieldSquares
	= new boolean[GRID_SIZE.height][GRID_SIZE.width];
    private Point homeSquare = new Point(0, 0);
    private Appearance appearance = null;
    private IconModel _appearanceDrawerIconModel = null;
    private PaintFieldObject currentObject = null;
    private boolean _isAppearanceDirty = false;
    
    private static interface ShapeDrawer
    {
	public void drawShape(Graphics graphics, int i, int i_0_, int i_1_,
			      int i_2_);
    }
    
    private static class DitherHackFilter extends RGBImageFilter
    {
	private int _drawColorRGB;
	private int _transparentColorRGB;
	
	DitherHackFilter(Color drawColor, Color transparentColor) {
	    _drawColorRGB = drawColor.rgb();
	    _transparentColorRGB = transparentColor.rgb();
	    canFilterIndexColorModel = true;
	}
	
	public int filterRGB(int x, int y, int rgb) {
	    return (rgb == _transparentColorRGB ? 0xffffff & rgb
		    : _drawColorRGB);
	}
    }
    
    private static PaintFieldObject getClipboard() {
	return ((PaintFieldObject)
		clipboardTable.get(Application.application()));
    }
    
    private static void setClipboard(PaintFieldObject pfo) {
	clipboardTable.put(Application.application(), pfo);
    }
    
    public static int[] getPixelArray(int size) {
	int[] universalPixelArray
	    = (int[]) universalPixelArrayTable.get(Application.application());
	if (universalPixelArray == null || universalPixelArray.length < size) {
	    universalPixelArray = new int[size];
	    universalPixelArrayTable.put(Application.application(),
					 universalPixelArray);
	}
	return universalPixelArray;
    }
    
    public PaintField(AppearanceEditor editor) {
	super(editor, new Rect(0, 0, 256, 256));
	displayVariables = new Vector();
	editor.addAppearanceEventListener(this);
    }
    
    private void resetShapeArray() {
	for (int y = 0; y < GRID_SIZE.height; y++) {
	    for (int x = 0; x < GRID_SIZE.width; x++)
		paintFieldSquares[y][x] = false;
	}
	if (homeSquare.x >= 0 && homeSquare.y >= 0)
	    paintFieldSquares[homeSquare.y][homeSquare.x] = true;
    }
    
    int getScale() {
	return scale;
    }
    
    public void setScale(int scale) {
	this.scale = scale;
	adjustOriginForHomeSquareAndRedraw();
    }
    
    Point getOrigin() {
	return new Point(origin);
    }
    
    void setOrigin(Point newOrigin) {
	origin = new Point(newOrigin);
	Size totalSize = getLogicalSize();
	Rect visibleSize = logicalVisibleRect();
	if (visibleSize.x + visibleSize.width > totalSize.width)
	    origin.x = totalSize.width - visibleSize.width;
	if (visibleSize.y + visibleSize.height > totalSize.height)
	    origin.y = totalSize.height - visibleSize.height;
	if (origin.x < 0)
	    origin.x = 0;
	if (origin.y < 0)
	    origin.y = 0;
	this.getAppearanceEditor().getPaintFieldScrollPane().checkArrows();
	this.draw();
    }
    
    public PaintState getCurrentState() {
	return currentState;
    }
    
    Appearance getAppearance() {
	return appearance;
    }
    
    void setAppearance(Appearance a, IconModel appearanceDrawerIconModel) {
	appearance = a;
	_appearanceDrawerIconModel = appearanceDrawerIconModel;
	squareSize = a.getSquareSize();
	if (lastState != null) {
	    lastState.flush();
	    lastState = null;
	}
	_isAppearanceDirty = false;
	if (currentState != null)
	    currentState.flush();
	currentState = new PaintState(getLogicalSize());
	currentSelection = null;
	Graphics g = currentState.createGraphics();
	g.setColor(TransparentGraphics.T_COLOR);
	g.fillRect(0, 0, currentState.width(), currentState.height());
	g.dispose();
	displayVariables = new Vector();
	Vector appearanceDisplayList = a.getDisplayList();
	if (appearanceDisplayList != null) {
	    for (int i = 0; i < appearanceDisplayList.size(); i++) {
		DisplayVariable realVar
		    = (DisplayVariable) appearanceDisplayList.elementAt(i);
		addVariable(new VariableObject(this, realVar), false, false);
	    }
	}
	Bitmap b = a.getBitmap();
	boolean success
	    = b.grabPixels(getPixelArray(getLogicalSize().width
					 * getLogicalSize().height));
	ASSERT.isTrue(success, "grabPixels");
	currentState.drawBitmap(b);
	homeSquare = a.getHomeSquare();
	Util.transformLL1ToUL0(homeSquare, a.getLogicalHeight());
	resetShapeArray();
	updateShapeArray(new Rect(0, 0, getLogicalSize().width,
				  getLogicalSize().height),
			 true);
	adjustOriginForHomeSquareAndRedraw();
    }
    
    public void setAppearanceBitmap(Bitmap bitmap) {
	recordStateForUndo();
	if (currentState != null)
	    currentState.flush();
	currentState = new PaintState(getLogicalSize());
	currentSelection = null;
	Graphics g = currentState.createGraphics();
	g.setColor(TransparentGraphics.T_COLOR);
	g.fillRect(0, 0, currentState.width(), currentState.height());
	g.dispose();
	boolean success
	    = bitmap.grabPixels(getPixelArray(getLogicalSize().width
					      * getLogicalSize().height));
	ASSERT.isTrue(success, "grabPixels");
	currentState.drawBitmap(bitmap);
	shapeArrayBounds();
	_isAppearanceDirty = true;
	adjustOriginForHomeSquareAndRedraw();
	_appearanceDrawerIconModel.setIconImage(bitmap);
    }
    
    private void adjustOriginForHomeSquareAndRedraw() {
	if (scale == 8)
	    setOrigin(new Point(homeSquare.x * squareSize,
				homeSquare.y * squareSize));
	else if (scale == 4)
	    setOrigin(new Point((homeSquare.x == 0 ? 0
				 : (homeSquare.x - 1) * squareSize),
				(homeSquare.y == 0 ? 0
				 : (homeSquare.y - 1) * squareSize)));
	else
	    setOrigin(new Point(0, 0));
    }
    
    private Rect shapeArrayBounds() {
	Point oldHomeSquare = new Point(homeSquare.x, homeSquare.y);
	homeSquare.x = homeSquare.y = -1;
	updateShapeArray(new Rect(0, 0, getLogicalSize().width,
				  getLogicalSize().height),
			 true);
	Rect result = shapeArrayBoundsFromSquares();
	boolean homeSquareEmpty
	    = paintFieldSquares[oldHomeSquare.y][oldHomeSquare.x] ^ true;
	homeSquare = oldHomeSquare;
	if (homeSquareEmpty) {
	    Point validNewHomeSquare = null;
	    for (int y = 0; y < GRID_SIZE.height; y++) {
		for (int x = 0; x < GRID_SIZE.width; x++) {
		    if (paintFieldSquares[y][x]) {
			validNewHomeSquare = new Point(x, y);
			this.setDirty(true);
			break;
		    }
		}
	    }
	    if (validNewHomeSquare != null)
		homeSquare = validNewHomeSquare;
	    else {
		paintFieldSquares[homeSquare.y][homeSquare.x] = true;
		result = new Rect(homeSquare.x, homeSquare.y, 1, 1);
	    }
	}
	return result;
    }
    
    private Rect shapeArrayBoundsFromSquares() {
	int minX = GRID_SIZE.width;
	int minY = GRID_SIZE.height;
	int maxX = -1;
	int maxY = -1;
	for (int y = 0; y < GRID_SIZE.height; y++) {
	    for (int x = 0; x < GRID_SIZE.width; x++) {
		if (paintFieldSquares[y][x]) {
		    if (x < minX)
			minX = x;
		    if (y < minY)
			minY = y;
		    if (x > maxX)
			maxX = x;
		    if (y > maxY)
			maxY = y;
		}
	    }
	}
	return new Rect(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
    
    void prepareForPossibleSave() {
	putSelectionBackInBitmap();
    }
    
    void saveChanges() {
	if (isAppearanceDirty() == true) {
	    this.getAppearanceEditor().getTool().prepareForPaintFieldChange();
	    synchAppearanceToEditor();
	    _isAppearanceDirty = false;
	}
    }
    
    private Bitmap createBitmapFromPaintField() {
	if (currentSelection != null)
	    deselect();
	Rect r = shapeArrayBounds();
	int xOffset = r.x * squareSize;
	int yOffset = r.y * squareSize;
	int newWidth = r.width * squareSize;
	int newHeight = r.height * squareSize;
	int[] pixels = new int[newWidth * newHeight];
	int[] transparencyPix = new int[newWidth * newHeight];
	boolean success
	    = currentState.grabPixels(pixels, xOffset, yOffset, newWidth,
				      newHeight, 0, newWidth);
	success = (currentState.paintLayer.getTransparencyMap().grabPixels
		   (transparencyPix, xOffset, yOffset, newWidth, newHeight, 0,
		    newWidth));
	ASSERT.isTrue(success, "grabPixels");
	int count = 0;
	for (int y = 0; y < newHeight; y++) {
	    for (int x = 0; x < newWidth; x++) {
		if (transparencyPix[count]
		    == TransparentGraphics.TRANSPARENT_INT)
		    pixels[count] = 0;
		count++;
	    }
	}
	return BitmapManager.createBitmapManager(pixels, newWidth, newHeight);
    }
    
    private void synchAppearanceToEditor() {
	Bitmap bitmap = createBitmapFromPaintField();
	Rect r = shapeArrayBounds();
	boolean[][] newShape = new boolean[r.height][r.width];
	for (int y = 0; y < r.height; y++) {
	    for (int x = 0; x < r.width; x++)
		newShape[y][x] = paintFieldSquares[y + r.y][x + r.x];
	}
	Point newHomeSquare
	    = new Point(homeSquare.x - r.x, homeSquare.y - r.y);
	Util.transformUL0ToLL1(newHomeSquare, r.height);
	int xOffset = r.x * squareSize;
	int yOffset = r.y * squareSize;
	int newHeight = r.height * squareSize;
	appearance.setBitmap(bitmap);
	appearance.changeSize(r.width, r.height);
	Point temp = new Point();
	for (int y = 0; y < r.height; y++) {
	    for (int x = 0; x < r.width; x++) {
		temp.moveTo(x, y);
		Util.transformUL0ToLL1(temp, r.height);
		appearance.setLocationHV(temp.x, temp.y,
					 paintFieldSquares[y + r.y][x + r.x]);
	    }
	}
	appearance.setHomeSquare(newHomeSquare);
	Vector appearanceDisplayList = appearance.getDisplayList();
	if (appearanceDisplayList != null) {
	    appearanceDisplayList = (Vector) appearanceDisplayList.clone();
	    int length = appearanceDisplayList.size();
	    for (int i = 0; i < length; i++) {
		DisplayVariable realVar
		    = (DisplayVariable) appearanceDisplayList.elementAt(i);
		appearance.unDisplay(realVar);
	    }
	}
	for (int i = 0; i < displayVariables.size(); i++) {
	    VariableObject realVar
		= (VariableObject) displayVariables.elementAt(i);
	    int locationX = realVar.getAnchorX() - xOffset;
	    int locationY = realVar.rect.y - yOffset;
	    appearance.display(realVar.getVariable(), locationX, locationY,
			       realVar.getJustification(), realVar.getFont(),
			       realVar.getColor());
	}
    }
    
    boolean isAppearanceDirty() {
	return _isAppearanceDirty;
    }
    
    boolean hasShapeChanged() {
	if (isAppearanceDirty() == false)
	    return false;
	Rect shapeBounds = shapeArrayBounds();
	if (shapeBounds.height != appearance.getLogicalHeight()
	    || shapeBounds.width != appearance.getLogicalWidth())
	    return true;
	Point temp = new Point();
	for (int y = 0; y < shapeBounds.height; y++) {
	    for (int x = 0; x < shapeBounds.width; x++) {
		temp.moveTo(x, y);
		Util.transformUL0ToLL1(temp, shapeBounds.height);
		if (paintFieldSquares[y][x]
		    != appearance.getLocationHV(temp.x, temp.y))
		    return true;
	    }
	}
	return false;
    }
    
    boolean hasHomeSquareChanged() {
	if (isAppearanceDirty() == false)
	    return false;
	Rect shapeBounds = shapeArrayBounds();
	Point newHomeSquare = new Point(homeSquare.x - shapeBounds.x,
					homeSquare.y - shapeBounds.y);
	Util.transformUL0ToLL1(newHomeSquare, shapeBounds.height);
	return newHomeSquare.equals(appearance.getHomeSquare()) ^ true;
    }
    
    private Rect updateShapeArray(Rect r, boolean pixelCheck) {
	Rect updateRect = new Rect(r);
	int height;
	int width = height = 0;
	int[] pixels = null;
	int startX = r.x / squareSize;
	int startY = r.y / squareSize;
	int endX = (int) Math.ceil((double) ((float) (r.x + r.width)
					     / (float) squareSize));
	int endY = (int) Math.ceil((double) ((float) (r.y + r.height)
					     / (float) squareSize));
	if (startX < 0)
	    startX = 0;
	if (startY < 0)
	    startY = 0;
	if (pixelCheck) {
	    boolean onlyHomeBeingUpdated
		= (startX == endX - 1 && startX == homeSquare.x
		   && startY == endY - 1 && startY == homeSquare.y);
	    if (!onlyHomeBeingUpdated) {
		width = currentState.width();
		height = currentState.height();
		pixels = getPixelArray(width * height);
		boolean success = currentState.paintLayer.getTransparencyMap
				      ().grabPixels(pixels);
		ASSERT.isTrue(success, "grabPixels");
	    }
	}
	for (int y = startY; y < endY && y < GRID_SIZE.height; y++) {
	    int yOffset = y * squareSize;
	    for (int x = startX; x < endX && x < GRID_SIZE.width; x++) {
		if (x != homeSquare.x || y != homeSquare.y) {
		    int xOffset = x * squareSize;
		    boolean containsPixels = false;
		    if (pixelCheck) {
		    while_5_:
			for (int pixelY = 0; pixelY < squareSize; pixelY++) {
			    int pixelYOffset = (yOffset + pixelY) * width;
			    for (int pixelX = 0; pixelX < squareSize;
				 pixelX++) {
				if (pixels[pixelX + xOffset + pixelYOffset]
				    != TransparentGraphics.TRANSPARENT_INT) {
				    int tp = pixelX + xOffset + pixelYOffset;
				    containsPixels = true;
				    break while_5_;
				}
			    }
			}
		    } else
			containsPixels = true;
		    if (containsPixels == false && currentSelection != null
			&& currentSelection.rect.intersects(xOffset, yOffset,
							    squareSize,
							    squareSize))
			containsPixels = true;
		    if (paintFieldSquares[y][x] != containsPixels) {
			paintFieldSquares[y][x] = containsPixels;
			updateRect.unionWith(new Rect(xOffset, yOffset,
						      squareSize, squareSize));
		    }
		}
	    }
	}
	return updateRect;
    }
    
    public synchronized void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	if (_offscreenBuffer == null || width != _offscreenBuffer.width()
	    || height != _offscreenBuffer.height()) {
	    if (_offscreenBuffer != null) {
		_offscreenBuffer.flush();
		_offscreenBuffer = null;
	    }
	    _offscreenBuffer
		= BitmapManager.createBitmapManager(width, height);
	}
    }
    
    public void discard() {
	super.discard();
	if (_offscreenBuffer != null)
	    _offscreenBuffer.flush();
	_offscreenBuffer = null;
	if (currentState != null)
	    currentState.flush();
	currentState = null;
	if (lastState != null)
	    lastState.flush();
	lastState = null;
	currentSelection = null;
	lastSelection = null;
	displayVariables = null;
	appearance = null;
	_appearanceDrawerIconModel = null;
	currentObject = null;
    }
    
    public void drawView(Graphics g) {
	Graphics g2 = _offscreenBuffer.createGraphics();
	g2.setClipRect(g.clipRect());
	drawGrid(g2);
	drawSelection(g2);
	drawVariables(g2);
	drawUnusedSpace(g2);
	g2.dispose();
	_offscreenBuffer.drawAt(g, 0, 0);
	if (highlighted) {
	    g.setColor(Util.HIGHLIGHT_COLOR);
	    g.drawRect(0, 0, this.width(), this.height());
	}
    }
    
    private void drawUnusedSpace(Graphics g) {
	int drawnWidth = squareSize * GRID_SIZE.width * scale;
	int drawnHeight = squareSize * GRID_SIZE.height * scale;
	if (drawnWidth < this.width() || drawnHeight < this.height()) {
	    g.setColor(this.getAppearanceEditor().getLightColor());
	    g.fillRect(drawnWidth, 0, this.width() - drawnWidth,
		       this.height());
	    g.fillRect(0, drawnHeight, this.width(),
		       this.height() - drawnHeight);
	}
    }
    
    private void drawVariables(Graphics g) {
	for (int i = 0; i < displayVariables.size(); i++) {
	    VariableObject var
		= (VariableObject) displayVariables.elementAt(i);
	    var.draw(this, g);
	}
    }
    
    private void drawSelection(Graphics g) {
	if (currentSelection != null)
	    currentSelection.draw(this, g);
    }
    
    private void drawGrid(Graphics g) {
	Rect source = physicalToLogical(new Rect(g.clipRect()));
	source.intersectWith(logicalVisibleRect());
	if (source.width != 0 && source.height != 0) {
	    BitmapManager.drawScaled(currentState.paintLayer, g, source,
				     logicalToPhysical(new Rect(source)));
	    int startX = source.x / squareSize;
	    int startY = source.y / squareSize;
	    int endX
		= (int) Math.ceil((double) ((float) (source.x + source.width)
					    / (float) squareSize));
	    int endY
		= (int) Math.ceil((double) ((float) (source.y + source.height)
					    / (float) squareSize));
	    if (startX < 0)
		startX = 0;
	    if (startY < 0)
		startY = 0;
	    int scaledSquare = squareSize * scale;
	    for (int y = startY; y < endY && y < GRID_SIZE.height; y++) {
		int physY = logicalToPhysicalY(y * squareSize);
		for (int x = startX; x < endX && x < GRID_SIZE.width; x++) {
		    int physX = logicalToPhysicalX(x * squareSize);
		    if (paintFieldSquares[y][x] == false) {
			g.setColor(UNUSED_GRID_COLOR);
			g.fillRect(physX, physY, scaledSquare, scaledSquare);
		    }
		}
	    }
	    g.setColor(GRID_LINE_COLOR);
	    for (int y = startY; y < endY && y < GRID_SIZE.height; y++) {
		int physY = (logicalToPhysicalY(y * squareSize)
			     + squareSize * scale - 1);
		if (physY > 0 && physY < this.height() - 1)
		    g.drawLine(0, physY, this.width(), physY);
	    }
	    for (int x = startX; x < endX && x < GRID_SIZE.width; x++) {
		int physX = (logicalToPhysicalX(x * squareSize)
			     + squareSize * scale - 1);
		if (physX > 0 && physX < this.width() - 1)
		    g.drawLine(physX, 0, physX, this.width());
	    }
	    g.setColor(HOME_SQUARE_COLOR);
	    g.drawRect(logicalToPhysicalX(homeSquare.x * squareSize),
		       logicalToPhysicalY(homeSquare.y * squareSize),
		       scaledSquare, scaledSquare);
	}
    }
    
    public boolean mouseDown(MouseEvent e) {
	this.setFocusedView();
	e.x = physicalToLogicalX(e.x);
	e.y = physicalToLogicalY(e.y);
	if (!isInLogicalRect(e.x, e.y))
	    return false;
	if (insideCurrentSelection(e.x, e.y))
	    currentObject = currentSelection;
	else
	    currentObject = insideDisplayVariable(e.x, e.y);
	if (currentObject != null)
	    currentObject.mouseDown(e.x, e.y, e.isControlKeyDown(),
				    e.isShiftKeyDown(), e.isAltKeyDown());
	else {
	    setSelectedVariable(null);
	    AppearanceEditorTool active = this.getAppearanceEditor().getTool();
	    if (active == null)
		return true;
	    active.mouseDown(e.x, e.y, e.isControlKeyDown(),
			     e.isShiftKeyDown(), e.isAltKeyDown());
	}
	return true;
    }
    
    public void mouseDragged(MouseEvent e) {
	if (currentObject == null) {
	    AppearanceEditorTool active = this.getAppearanceEditor().getTool();
	    if (active != null)
		active.mouseDragged(physicalToLogicalX(e.x),
				    physicalToLogicalY(e.y),
				    e.isControlKeyDown(), e.isShiftKeyDown(),
				    e.isAltKeyDown());
	} else
	    currentObject.mouseDragged(physicalToLogicalX(e.x),
				       physicalToLogicalY(e.y),
				       e.isControlKeyDown(),
				       e.isShiftKeyDown(), e.isAltKeyDown());
    }
    
    public void mouseUp(MouseEvent e) {
	if (currentObject == null) {
	    AppearanceEditorTool active = this.getAppearanceEditor().getTool();
	    if (active != null)
		active.mouseUp(physicalToLogicalX(e.x),
			       physicalToLogicalY(e.y), e.isControlKeyDown(),
			       e.isShiftKeyDown(), e.isAltKeyDown());
	} else {
	    currentObject.mouseUp(physicalToLogicalX(e.x),
				  physicalToLogicalY(e.y),
				  e.isControlKeyDown(), e.isShiftKeyDown(),
				  e.isAltKeyDown());
	    _isAppearanceDirty = true;
	}
    }
    
    public void keyDown(KeyEvent event) {
	AppearanceEditorTool currentTool
	    = this.getAppearanceEditor().getTool();
	KeyEvent cmdEvent = PlaywriteRoot.getLastCommandKeyEvent();
	boolean doneNothing = true;
	if (cmdEvent != null && !(currentTool instanceof TextTool)) {
	    switch (cmdEvent.key + 64) {
	    case 65:
	    case 97:
		doneNothing = false;
		selectAll(true);
		break;
	    case 67:
	    case 99:
		doneNothing = false;
		copy();
		break;
	    case 86:
	    case 118:
		doneNothing = false;
		paste();
		break;
	    case 88:
	    case 120:
		doneNothing = false;
		cut();
		break;
	    case 90:
	    case 122:
		doneNothing = false;
		undo();
		break;
	    }
	}
	if (doneNothing) {
	    if (currentSelection != null)
		currentSelection.keyDown(event);
	    else if (currentObject != null)
		currentObject.keyDown(event);
	    else if (currentTool != null)
		currentTool.keyDown(event);
	}
    }
    
    Rect minimalRect(int x1, int y1, int x2, int y2) {
	Rect r = new Rect();
	if (x1 < x2) {
	    r.x = x1;
	    r.width = x2 - x1;
	} else {
	    r.x = x2;
	    r.width = x1 - x2;
	}
	if (y1 < y2) {
	    r.y = y1;
	    r.height = y2 - y1;
	} else {
	    r.y = y2;
	    r.height = y1 - y2;
	}
	return r;
    }
    
    Color getValueAt(int x, int y) {
	int[] pixels = new int[1];
	boolean success = currentState.grabPixels(pixels, x, y, 1, 1, 0,
						  currentState.width());
	ASSERT.isTrue(success, "grabPixels");
	return new Color(pixels[0]);
    }
    
    Rect setPixel(int x, int y, int size, Color color) {
	Rect target = new Rect(x - size / 2, y - size / 2, size, size);
	target.intersectWith(logicalVisibleRect());
	drawRect(target.x, target.y, target.x + target.width,
		 target.y + target.height, 1, color, true);
	return target;
    }
    
    Rect drawLine(int x1, int y1, int x2, int y2, final int brushWidth,
		  final Color color) {
	ShapeDrawer drawer = new ShapeDrawer() {
	    public void drawShape(Graphics graphics, int x1_4_, int y1_5_,
				  int x2_6_, int y2_7_) {
		graphics.setColor(color);
		double mx;
		double my;
		if (x1_4_ - x2_6_ != 0) {
		    double slope
			= (double) ((y1_5_ - y2_7_) / (x1_4_ - x2_6_));
		    double k = Math.sqrt(1.0 + Math.pow(slope, 2.0));
		    mx = 1.0 / k;
		    my = slope / k;
		} else {
		    mx = 0.0;
		    my = 1.0;
		}
		for (int i = -brushWidth / 2; i < brushWidth / 2; i++) {
		    for (int j = -brushWidth / 2; j < brushWidth / 2; j++)
			graphics.drawLine((int) ((double) x1_4_
						 + my * (double) i),
					  (int) ((double) y1_5_
						 + mx * (double) j),
					  (int) ((double) x2_6_
						 + my * (double) i),
					  (int) ((double) y2_7_
						 + (double) j * mx));
		}
		graphics.drawLine(x1_4_, y1_5_, x2_6_, y2_7_);
	    }
	};
	Rect target = minimalRect(x1, y1, x2, y2);
	target.x = target.x - brushWidth;
	target.y = target.y - brushWidth;
	target.width = target.width + 2 * brushWidth;
	target.height = target.height + 2 * brushWidth;
	drawShape(color, x1, y1, x2, y2, new Size(target.width, target.height),
		  drawer);
	target.intersectWith(logicalVisibleRect());
	return target;
    }
    
    void eraseRect(Rect r) {
	if (lastState != null) {
	    Graphics g = currentState.createGraphics();
	    r.x--;
	    r.y--;
	    r.width += 2;
	    r.height += 2;
	    g.setClipRect(r);
	    g.drawBitmapAt(lastState.paintLayer, 0, 0);
	    g.dispose();
	    g = currentState.paintLayer.getTransparencyMap().createGraphics();
	    g.setClipRect(r);
	    g.drawBitmapAt(lastState.paintLayer.getTransparencyMap(), 0, 0);
	    g.dispose();
	}
    }
    
    void drawRect(int x1, int y1, int x2, int y2, final int brushWidth,
		  final Color color, final boolean filledIn) {
	ShapeDrawer drawer = new ShapeDrawer() {
	    public void drawShape(Graphics graphics, int x1_10_, int y1_11_,
				  int x2_12_, int y2_13_) {
		Rect bounds = minimalRect(x1_10_, y1_11_, x2_12_, y2_13_);
		graphics.setColor(color);
		if (filledIn)
		    graphics.fillRect(bounds);
		else if (brushWidth == 1)
		    graphics.drawRect(bounds);
		else {
		    int boundedBrushWidth = brushWidth;
		    int brushHeight = boundedBrushWidth;
		    if (brushHeight > bounds.height)
			brushHeight = bounds.height;
		    if (boundedBrushWidth > bounds.width)
			boundedBrushWidth = bounds.width;
		    if (bounds.height > 2 * boundedBrushWidth) {
			graphics.fillRect(bounds.x, bounds.y, bounds.width,
					  brushHeight);
			graphics.fillRect(bounds.x,
					  (bounds.y + bounds.height
					   - brushHeight),
					  bounds.width, brushHeight);
			graphics.fillRect(bounds.x,
					  bounds.y + boundedBrushWidth,
					  boundedBrushWidth,
					  (bounds.height
					   - 2 * boundedBrushWidth));
			graphics.fillRect((bounds.x + bounds.width
					   - boundedBrushWidth),
					  bounds.y + boundedBrushWidth,
					  boundedBrushWidth,
					  (bounds.height
					   - 2 * boundedBrushWidth));
		    } else
			graphics.fillRect(bounds.x, bounds.y, bounds.width,
					  bounds.height);
		}
	    }
	};
	Size size = new Size(Math.max(x1, x2) - Math.min(x1, x2),
			     Math.max(y1, y2) - Math.min(y1, y2));
	drawShape(color, x1, y1, x2, y2, size, drawer);
    }
    
    void drawOval(int x1, int y1, int x2, int y2, final int brushWidth,
		  final Color color, final boolean filledIn) {
	ShapeDrawer drawer = new ShapeDrawer() {
	    public void drawShape(Graphics graphics, int x1_16_, int y1_17_,
				  int x2_18_, int y2_19_) {
		Rect bounds = minimalRect(x1_16_, y1_17_, x2_18_, y2_19_);
		graphics.setColor(color);
		if (filledIn)
		    graphics.fillOval(bounds);
		else if (brushWidth == 1)
		    graphics.drawOval(bounds);
		else {
		    for (int i = 0; i < brushWidth; i++) {
			for (int j = 0; j < brushWidth; j++)
			    graphics.drawOval(bounds.x + i, bounds.y + j,
					      bounds.width - i * 2,
					      bounds.height - j * 2);
		    }
		}
	    }
	};
	Size size = new Size(Math.max(x1, x2) - Math.min(x1, x2),
			     Math.max(y1, y2) - Math.min(y1, y2));
	drawShape(color, x1, y1, x2, y2, size, drawer);
    }
    
    private void drawShape(Color color, int x1, int y1, int x2, int y2,
			   Size dimensions, ShapeDrawer drawer) {
	Graphics g = currentState.createGraphics();
	g.setClipRect(logicalVisibleRect());
	drawer.drawShape(g, x1, y1, x2, y2);
	g.dispose();
    }
    
    private void drawShapeWithoutDithering(Color color, int x1, int y1, int x2,
					   int y2, Size dimensions,
					   ShapeDrawer drawer) {
	if (dimensions.width != 0 && dimensions.height != 0) {
	    Color transparent = Color.white;
	    int rgb = color.rgb();
	    if ((rgb & 0xff0000) >>> 16 > 200 && (rgb & 0xff00) >>> 8 > 200
		&& (rgb & 0xff) > 200)
		transparent = Color.black;
	    Bitmap canvas
		= BitmapManager.createBitmapManager(dimensions.width,
						    dimensions.height);
	    Graphics g = canvas.createGraphics();
	    g.setColor(transparent);
	    g.fillRect(0, 0, dimensions.width, dimensions.height);
	    int xOffset = Math.min(x1, x2);
	    int yOffset = Math.min(y1, y2);
	    drawer.drawShape(g, x1 - xOffset, y1 - yOffset, x2 - xOffset,
			     y2 - yOffset);
	    g.dispose();
	    java.awt.image.ImageFilter filter
		= new DitherHackFilter(color, transparent);
	    canvas = BitmapManager.createFilteredBitmapManager(canvas, filter);
	    int[] pixels = new int[dimensions.width * dimensions.height];
	    boolean success = canvas.grabPixels(pixels);
	    ASSERT.isTrue(success, "grabPixels");
	    g = currentState.createGraphics();
	    g.setClipRect(logicalVisibleRect());
	    g.drawBitmapAt(canvas, xOffset, yOffset);
	    g.dispose();
	    canvas.flush();
	}
    }
    
    void drawChars(int x, int y, char[] chars, int offset, int length,
		   Color color, Font font) {
	Graphics g = currentState.createGraphics();
	g.setColor(color);
	g.setFont(font);
	g.setClipRect(logicalVisibleRect());
	g.drawChars(chars, offset, length, x, y);
	g.dispose();
    }
    
    void fillArea(int x, int y, Color color, boolean wasModifierKeyDown) {
	waitCursor(true);
	colorSeedFill(x, y, color, getValueAt(x, y), wasModifierKeyDown);
	waitCursor(false);
    }
    
    private void colorSeedFill(int startX, int startY, Color newColor,
			       Color oldColor, boolean wasModifierKeyDown) {
	Stack pointStack = new Stack();
	boolean isInsideShape = isInsideShape(startX, startY);
	Rect r = logicalVisibleRect();
	if (isInsideShape == false && wasModifierKeyDown == false)
	    r.intersectWith(logicalGridSquareRectFromPoint(startX, startY));
	int newColorInt = newColor.rgb();
	int oldColorInt = oldColor.rgb();
	int[] pixels = getPixelArray(r.width * r.height);
	int[] transparencyPixels = new int[r.width * r.height];
	boolean success = currentState.grabPixels(pixels, r.x, r.y, r.width,
						  r.height, 0, r.width);
	success
	    = (currentState.paintLayer.getTransparencyMap().grabPixels
	       (transparencyPixels, r.x, r.y, r.width, r.height, 0, r.width));
	ASSERT.isTrue(success, "grabPixels");
	startX -= r.x;
	startY -= r.y;
	int tmColorInt;
	if (newColor == TransparentGraphics.T_COLOR)
	    tmColorInt = TransparentGraphics.TRANSPARENT_INT;
	else
	    tmColorInt = TransparentGraphics.OPAQUE_INT;
	int oldtmColorInt = transparencyPixels[startX + r.width * startY];
	if ((newColor != TransparentGraphics.T_COLOR
	     || (transparencyPixels[startX + r.width * startY]
		 != TransparentGraphics.TRANSPARENT_INT))
	    && (newColor == TransparentGraphics.T_COLOR
		|| (pixels[startX + r.width * startY] == oldColorInt
		    && (pixels[startX + r.width * startY] != newColorInt
			|| (transparencyPixels[startX + r.width * startY]
			    != tmColorInt))))) {
	    recordStateForUndo();
	    pointStack.push(new Point(startX, startY));
	    while (!pointStack.empty()) {
		Point currentPoint = (Point) pointStack.pop();
		int left;
		int right = left = currentPoint.x;
		int currentYOffset = r.width * currentPoint.y;
	    while_6_:
		do {
		    for (;;) {
			int currentIndex = left + currentYOffset;
			if (pixels[currentIndex] != oldColorInt
			    || (transparencyPixels[currentIndex]
				!= oldtmColorInt)
			    || !isInsideShape(r, left, currentPoint.y))
			    break;
			pixels[currentIndex] = newColorInt;
			transparencyPixels[currentIndex] = tmColorInt;
			if (left <= 0)
			    break while_6_;
			left--;
		    }
		    left++;
		} while (false);
	    while_7_:
		do {
		    if (right < r.width - 1) {
			right++;
			for (;;) {
			    int currentIndex = right + currentYOffset;
			    if (pixels[currentIndex] != oldColorInt
				|| (transparencyPixels[currentIndex]
				    != oldtmColorInt)
				|| !isInsideShape(r, right, currentPoint.y))
				break;
			    pixels[currentIndex] = newColorInt;
			    transparencyPixels[currentIndex] = tmColorInt;
			    if (right >= r.width - 1)
				break while_7_;
			    right++;
			}
			right--;
		    }
		} while (false);
		int topright = right;
		boolean pushNext = true;
		if (currentPoint.y > 0) {
		    for (/**/; topright >= left; topright--) {
			int ix = topright + r.width * (currentPoint.y - 1);
			if (pixels[ix] == oldColorInt
			    && transparencyPixels[ix] == oldtmColorInt
			    && isInsideShape(r, topright,
					     currentPoint.y - 1)) {
			    if (pushNext) {
				pointStack.push(new Point(topright,
							  currentPoint.y - 1));
				pushNext = false;
			    }
			} else if (!pushNext)
			    pushNext = true;
		    }
		}
		int botright = right;
		pushNext = true;
		if (currentPoint.y < r.height - 1) {
		    for (/**/; botright >= left; botright--) {
			int ix = botright + r.width * (currentPoint.y + 1);
			if (pixels[ix] == oldColorInt
			    && transparencyPixels[ix] == oldtmColorInt
			    && isInsideShape(r, botright,
					     currentPoint.y + 1)) {
			    if (pushNext) {
				pointStack.push(new Point(botright,
							  currentPoint.y + 1));
				pushNext = false;
			    }
			} else if (!pushNext)
			    pushNext = true;
		    }
		}
	    }
	    Graphics g = currentState.createGraphics();
	    g.setClipRect(r);
	    Bitmap newbie
		= BitmapManager.createBitmapManager(pixels, r.width, r.height);
	    g.drawBitmapAt(newbie, r.x, r.y);
	    newbie.flush();
	    pixels = null;
	    Graphics g2 = currentState.paintLayer.getTransparencyMap()
			      .createGraphics();
	    g2.setClipRect(r);
	    newbie = BitmapManager.createBitmapManager(transparencyPixels,
						       r.width, r.height);
	    g2.drawBitmapAt(newbie, r.x, r.y);
	    newbie.flush();
	    g2.dispose();
	    g.dispose();
	    logicalDraw(r, 2);
	}
    }
    
    private final boolean isInsideShape(int x, int y) {
	return paintFieldSquares[y / squareSize][x / squareSize];
    }
    
    private final boolean isInsideShape(Rect offsetRect, int x, int y) {
	return isInsideShape(x + offsetRect.x, y + offsetRect.y);
    }
    
    void flipHorizontal() {
	if (currentSelection != null)
	    currentSelection.flipHorizontal();
	else {
	    this.disableDrawing();
	    selectAll(false);
	    if (currentSelection != null)
		currentSelection.flipHorizontal();
	    deselect();
	    this.reenableDrawing();
	    this.setDirty(true);
	}
    }
    
    void flipVertical() {
	if (currentSelection != null)
	    currentSelection.flipVertical();
	else {
	    this.disableDrawing();
	    selectAll(false);
	    if (currentSelection != null)
		currentSelection.flipVertical();
	    deselect();
	    this.reenableDrawing();
	    this.setDirty(true);
	}
    }
    
    void rotate(boolean clockwise) {
	if (currentSelection != null)
	    currentSelection.rotate(clockwise);
	else {
	    this.disableDrawing();
	    selectAll(false);
	    if (currentSelection != null)
		currentSelection.rotate(clockwise);
	    deselect();
	    this.reenableDrawing();
	    this.setDirty(true);
	}
    }
    
    Rect logicalGridSquareRectFromPoint(int x, int y) {
	return new Rect((int) (Math.floor((double) ((float) x
						    / (float) squareSize))
			       * (double) squareSize),
			(int) (Math.floor((double) ((float) y
						    / (float) squareSize))
			       * (double) squareSize),
			squareSize, squareSize);
    }
    
    Rect logicalVisibleRect() {
	int width
	    = Math.min(squareSize * GRID_SIZE.width, this.width() / scale);
	int height
	    = Math.min(squareSize * GRID_SIZE.height, this.height() / scale);
	return new Rect(origin.x, origin.y, width, height);
    }
    
    public boolean isInLogicalRect(int x, int y) {
	return (x < squareSize * GRID_SIZE.width
		&& y < squareSize * GRID_SIZE.height);
    }
    
    Size getLogicalSize() {
	return new Size(GRID_SIZE.width * squareSize,
			GRID_SIZE.height * squareSize);
    }
    
    public Point physicalToLogical(Point p) {
	Point newPoint = new Point();
	newPoint.x = physicalToLogicalX(p.x);
	newPoint.y = physicalToLogicalY(p.y);
	return newPoint;
    }
    
    public Rect physicalToLogical(Rect r) {
	int x = physicalToLogicalX(r.x);
	int y = physicalToLogicalY(r.y);
	int width = physicalToLogicalX(r.x + r.width - 1) - x + 1;
	int height = physicalToLogicalY(r.y + r.height - 1) - y + 1;
	r.setBounds(x, y, width, height);
	return r;
    }
    
    public int physicalToLogicalX(int x) {
	return x / scale + origin.x;
    }
    
    public int physicalToLogicalY(int y) {
	return y / scale + origin.y;
    }
    
    public Point logicalToPhysical(Point p) {
	Point newPoint = new Point();
	newPoint.x = logicalToPhysicalX(p.x);
	newPoint.y = logicalToPhysicalY(p.y);
	return newPoint;
    }
    
    public Rect logicalToPhysical(Rect r) {
	r.x = logicalToPhysicalX(r.x);
	r.y = logicalToPhysicalY(r.y);
	r.width *= scale;
	r.height *= scale;
	return r;
    }
    
    int logicalToPhysicalX(int x) {
	return (x - origin.x) * scale;
    }
    
    int logicalToPhysicalY(int y) {
	return (y - origin.y) * scale;
    }
    
    boolean isLeftEdgeVisible() {
	return origin.x == 0;
    }
    
    boolean isTopEdgeVisible() {
	return origin.y == 0;
    }
    
    boolean isRightEdgeVisible() {
	return (physicalToLogicalX(this.width() - 1)
		>= squareSize * GRID_SIZE.width - 1);
    }
    
    boolean isBottomEdgeVisible() {
	return (physicalToLogicalY(this.height() - 1)
		>= squareSize * GRID_SIZE.height - 1);
    }
    
    void logicalDraw(Rect r, int gridCheck) {
	if (gridCheck != 0)
	    r = updateShapeArray(r, gridCheck == 2);
	Rect newR = new Rect();
	Point topLeft = logicalToPhysical(new Point(r.x, r.y));
	newR.x = topLeft.x;
	newR.y = topLeft.y;
	Point bottomRight
	    = logicalToPhysical(new Point(r.x + r.width, r.y + r.height));
	newR.width = bottomRight.x - topLeft.x;
	newR.height = bottomRight.y - topLeft.y;
	this.draw(newR);
	if (currentSelection == null && gridCheck == 2)
	    _appearanceDrawerIconModel
		.setIconImage(createBitmapFromPaintField());
    }
    
    void logicalDraw(Rect r1, Rect r2, int gridCheck) {
	Rect newRect = new Rect();
	if (r1.x < r2.x)
	    newRect.x = r1.x;
	else
	    newRect.x = r2.x;
	if (r1.y < r2.y)
	    newRect.y = r1.y;
	else
	    newRect.y = r2.y;
	if (r1.x + r1.width > r2.x + r2.width)
	    newRect.width = r1.x + r1.width - newRect.x;
	else
	    newRect.width = r2.x + r2.width - newRect.x;
	if (r1.y + r1.height > r2.y + r2.height)
	    newRect.height = r1.y + r1.height - newRect.y;
	else
	    newRect.height = r2.y + r2.height - newRect.y;
	logicalDraw(newRect, gridCheck);
    }
    
    void cut() {
	if (currentSelection != null) {
	    setClipboard(new PaintFieldSelection(getCurrentSelection(), true));
	    deleteCurrentSelection();
	} else if (currentObject != null
		   && currentObject instanceof VariableObject) {
	    VariableObject var = (VariableObject) currentObject;
	    setClipboard(new VariableObject(var));
	    currentObject = null;
	    deleteVariable(var);
	}
    }
    
    void copy() {
	if (currentSelection != null)
	    setClipboard(new PaintFieldSelection(getCurrentSelection(), true));
	else if (currentObject != null
		 && currentObject instanceof VariableObject) {
	    VariableObject var = (VariableObject) currentObject;
	    setClipboard(new VariableObject(var));
	}
    }
    
    void paste() {
	Object clipboard = getClipboard();
	if (clipboard != null) {
	    putSelectionBackInBitmap();
	    if (clipboard instanceof PaintFieldSelection) {
		currentSelection
		    = new PaintFieldSelection((PaintFieldSelection) clipboard,
					      true);
		currentSelection.paintField = this;
		updateShapeArray(currentSelection.rect, false);
		this.draw();
		ensureSelectionToolSelected();
	    } else if (clipboard instanceof VariableObject) {
		VariableObject var = (VariableObject) clipboard;
		var.rect.x = var.rect.x + 2;
		var.rect.y = var.rect.y + 2;
		VariableObject newVar = new VariableObject(var);
		newVar.paintField = this;
		addVariable(newVar, true, true);
	    }
	    _isAppearanceDirty = true;
	}
    }
    
    void clear() {
	clear(true);
    }
    
    void clear(boolean recordIt) {
	if (getCurrentSelection() != null)
	    deleteCurrentSelection();
	else if (currentObject != null
		 && currentObject instanceof VariableObject) {
	    VariableObject var = (VariableObject) currentObject;
	    currentObject = null;
	    deleteVariable(var);
	} else {
	    this.getAppearanceEditor().getTool().prepareForPaintFieldChange();
	    if (recordIt)
		recordStateForUndo();
	    Graphics g = currentState.createGraphics();
	    g.setColor(TransparentGraphics.T_COLOR);
	    Rect clearRect = logicalVisibleRect();
	    g.fillRect(clearRect);
	    g.dispose();
	    updateShapeArray(clearRect, true);
	    logicalDraw(clearRect, 2);
	}
    }
    
    void undo() {
	if (lastState != null) {
	    this.getAppearanceEditor().getTool().prepareForPaintFieldChange();
	    PaintState current = currentState;
	    currentState = lastState;
	    lastState = current;
	    PaintFieldSelection current2 = currentSelection;
	    currentSelection = lastSelection;
	    lastSelection = current2;
	    if (currentSelection != null)
		ensureSelectionToolSelected();
	    Size s = getLogicalSize();
	    Rect r = new Rect(0, 0, s.width, s.height);
	    updateShapeArray(r, true);
	    logicalDraw(r, 2);
	    _isAppearanceDirty = true;
	}
    }
    
    void recordStateForUndo() {
	if (lastState == null)
	    lastState = new PaintState(currentState);
	else
	    lastState.copyFrom(currentState);
	if (currentSelection == null)
	    lastSelection = null;
	else
	    lastSelection = new PaintFieldSelection(currentSelection, false);
	_isAppearanceDirty = true;
    }
    
    void stretch() {
	if (currentSelection != null) {
	    currentSelection.stretchMode = currentSelection.stretchMode ^ true;
	    this.draw();
	}
    }
    
    void ensureSelectionToolSelected() {
	if (!(this.getAppearanceEditor().getTool()
	      instanceof RectangularSelectionTool)
	    && !(this.getAppearanceEditor().getTool() instanceof LassoTool))
	    this.getAppearanceEditor().selectSelectionTool();
    }
    
    PaintFieldSelection getCurrentSelection() {
	return currentSelection;
    }
    
    private void putSelectionBackInBitmap() {
	if (currentSelection != null) {
	    Graphics g = currentState.createGraphics();
	    Rect selRect = currentSelection.rect;
	    g.setClipRect(selRect);
	    currentSelection.getBitmap().drawScaled(g, currentSelection.rect);
	    currentState.paintLayer.addTransparencyFromBitmap(currentSelection
								  .getBitmap(),
							      (currentSelection
							       .rect));
	    currentSelection.getBitmap().flush();
	    currentSelection = null;
	    g.dispose();
	    logicalDraw(new Rect(0, 0, getLogicalSize().width,
				 getLogicalSize().height),
			2);
	}
    }
    
    void setCurrentSelection(Rect r, boolean alphaMask, Vector spans) {
	setCurrentSelection(r, alphaMask, spans, true);
    }
    
    void setCurrentSelection(Rect r, boolean alphaMask, Vector spans,
			     boolean cropToVisible) {
	if (currentSelection != null
	    || r != null && r.width > 0 && r.height > 0)
	    recordStateForUndo();
	putSelectionBackInBitmap();
	if (r != null) {
	    if (r.width > 0 && r.height > 0) {
		ensureSelectionToolSelected();
		setSelectedVariable(null);
		if (cropToVisible)
		    r.intersectWith(logicalVisibleRect());
		if (r.width > 0 && r.height > 0) {
		    if (spans == null)
			currentSelection
			    = new PaintFieldSelection(this, r, alphaMask);
		    else
			currentSelection
			    = new PaintFieldSelection(this, r, spans);
		    if (currentSelection.isBlank())
			currentSelection = null;
		}
	    }
	    logicalDraw(logicalVisibleRect(), 2);
	}
    }
    
    void deleteCurrentSelection() {
	if (currentSelection != null) {
	    recordStateForUndo();
	    currentSelection.getBitmap().flush();
	    currentSelection = null;
	    logicalDraw(new Rect(0, 0, getLogicalSize().width,
				 getLogicalSize().height),
			2);
	}
    }
    
    private boolean insideCurrentSelection(int x, int y) {
	return (currentSelection != null
		&& currentSelection.rect.contains(x, y));
    }
    
    void selectAll(boolean cropToVisible) {
	this.getAppearanceEditor().getTool().prepareForPaintFieldChange();
	Rect selectionRect
	    = new Rect(0, 0, currentState.width(), currentState.height());
	Rect shapeRect = shapeArrayBoundsFromSquares();
	int scaleFactor = squareSize;
	shapeRect.x *= scaleFactor;
	shapeRect.y *= scaleFactor;
	shapeRect.width *= scaleFactor;
	shapeRect.height *= scaleFactor;
	selectionRect.intersectWith(shapeRect);
	setCurrentSelection(selectionRect, true, null, cropToVisible);
	this.setFocusedView();
    }
    
    void deselect() {
	setCurrentSelection(null, false, null, true);
    }
    
    private VariableObject getSelectedVariable() {
	VariableObject returnVariable = null;
	for (int i = 0; i < displayVariables.size(); i++) {
	    VariableObject var
		= (VariableObject) displayVariables.elementAt(i);
	    if (var.selected)
		returnVariable = var;
	}
	return returnVariable;
    }
    
    void setSelectedVariable(VariableObject v) {
	VariableObject sel = getSelectedVariable();
	if (sel != null && sel != v) {
	    sel.stretchMode = false;
	    sel.selected = false;
	}
	if (v != null)
	    v.selected = true;
	if (sel != null || v != null)
	    this.draw();
    }
    
    void deleteVariable(VariableObject v) {
	displayVariables.removeElement(v);
	logicalDraw(v.rect, 2);
	_isAppearanceDirty = true;
    }
    
    private void addVariable(VariableObject v, boolean redraw,
			     boolean userAdded) {
	displayVariables.addElement(v);
	if (redraw == true)
	    logicalDraw(v.rect, 0);
	_isAppearanceDirty |= userAdded;
    }
    
    private VariableObject insideDisplayVariable(int x, int y) {
	VariableObject returnVariable = null;
	for (int i = 0; i < displayVariables.size(); i++) {
	    VariableObject var
		= (VariableObject) displayVariables.elementAt(i);
	    if (var.rect.contains(x, y))
		returnVariable = var;
	}
	return returnVariable;
    }
    
    public int cursorForPoint(int x, int y) {
	AppearanceEditorTool active = this.getAppearanceEditor().getTool();
	if (active == null)
	    return 0;
	Point logical = physicalToLogical(new Point(x, y));
	return active.cursorForPoint(logical);
    }
    
    public void setTool(AppearanceEditorTool tool) {
	/* empty */
    }
    
    public void setBrushWidth(int width) {
	/* empty */
    }
    
    public void setColor(Color color, boolean completed) {
	if (completed && currentObject != null) {
	    currentObject.setColor(color);
	    this.draw();
	    _isAppearanceDirty = true;
	}
    }
    
    public void setFont(Font font) {
	if (currentObject != null) {
	    Rect dirtyRect = new Rect(currentObject.rect);
	    currentObject.setFont(font);
	    dirtyRect.unionWith(currentObject.rect);
	    updateShapeArray(dirtyRect, true);
	    this.draw();
	    _isAppearanceDirty = true;
	}
    }
    
    public void setJustification(int justification) {
	if (currentObject != null) {
	    Rect dirtyRect = new Rect(currentObject.rect);
	    currentObject.setJustification(justification);
	    dirtyRect.unionWith(currentObject.rect);
	    updateShapeArray(dirtyRect, true);
	    this.draw();
	    _isAppearanceDirty = true;
	}
    }
    
    public DragDestination acceptsDrag(DragSession session, int x, int y) {
	if (session.data() instanceof VariableEditor)
	    return this;
	return null;
    }
    
    public boolean dragDropped(DragSession session) {
	boolean result = false;
	Object draggee = session.data();
	CharacterPrototype prototype
	    = this.getAppearanceEditor().getCharacter().getPrototype();
	if (draggee instanceof VariableEditor) {
	    VariableEditor ve = (VariableEditor) draggee;
	    if (ve.getOwner() instanceof World
		|| (ve.getOwner() instanceof CocoaCharacter
		    && (((CocoaCharacter) ve.getOwner()).getPrototype()
			== prototype))) {
		result = true;
		Rect destinationBounds = session.destinationBounds();
		addVariable((new VariableObject
			     (this, ve.getVariable(),
			      physicalToLogical(new Point(destinationBounds.x,
							  (destinationBounds
							   .y))))),
			    true, true);
	    }
	}
	this.unhilite();
	return result;
    }
    
    public boolean dragEntered(DragSession session) {
	this.hilite();
	return true;
    }
    
    public void dragExited(DragSession session) {
	this.unhilite();
	this.draw();
    }
    
    public boolean dragMoved(DragSession session) {
	return true;
    }
    
    public ToolDestination acceptsTool(ToolSession session, int x, int y) {
	if (session.toolType() == Tool.deleteTool)
	    return this;
	if (session.toolType() == HOME_SQUARE_TOOL)
	    return this;
	return null;
    }
    
    public boolean toolEntered(ToolSession session) {
	return true;
    }
    
    public boolean toolMoved(ToolSession session) {
	return true;
    }
    
    public void toolExited(ToolSession session) {
	/* empty */
    }
    
    public boolean toolClicked(ToolSession session) {
	boolean result = false;
	Tool toolType = session.toolType();
	if (toolType == Tool.deleteTool) {
	    Point loc = physicalToLogical(session.destinationMousePoint());
	    VariableObject v = insideDisplayVariable(loc.x, loc.y);
	    if (v != null) {
		deleteVariable(v);
		result = true;
	    }
	} else if (toolType == HOME_SQUARE_TOOL) {
	    Point newHomeSquare
		= physicalToLogical(session.destinationMousePoint());
	    newHomeSquare.moveTo(newHomeSquare.x / squareSize,
				 newHomeSquare.y / squareSize);
	    if (newHomeSquare.x < GRID_SIZE.width
		&& newHomeSquare.y < GRID_SIZE.height
		&& (paintFieldSquares[newHomeSquare.y][newHomeSquare.x]
		    == true)) {
		homeSquare = newHomeSquare;
		resetShapeArray();
		logicalDraw(new Rect(0, 0, getLogicalSize().width,
				     getLogicalSize().height),
			    2);
		_isAppearanceDirty = true;
		result = true;
	    } else
		PlaywriteDialog
		    .warning(Resource.getText("Picture Painter Alert 7"));
	}
	return result;
    }
    
    public void toolDragged(ToolSession session) {
	/* empty */
    }
    
    public void toolReleased(ToolSession session) {
	/* empty */
    }
    
    public void waitCursor(boolean on) {
	if (on)
	    Application.application().mainRootView().setOverrideCursor(3);
	else
	    Application.application().mainRootView().removeOverrideCursor();
    }
}
