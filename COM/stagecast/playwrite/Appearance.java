/* Appearance - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Dimension;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.operators.Op;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class Appearance
    implements Contained, Debug.Constants, Externalizable, FirstClassValue,
	       IconModel, Named, PlaywriteSystem.Properties, Proxy,
	       ReferencedObject, ResourceIDs.NameGeneratorIDs,
	       ResourceIDs.InstanceNameIDs, Selectable, Worldly
{
    static final int storeVersion = 5;
    static final long serialVersionUID = -3819410108756788530L;
    public static Point INVALID_POSITION;
    public static Rect ICON_IMAGE_RECT;
    private static Size defaultSize;
    private static boolean draw_scaled;
    private static Vector MALFORMED_APPEARANCES;
    private static final ViewManager.ViewUpdater appearanceDrawerHighlighter = new ViewManager.ViewUpdater() {
	public void updateView(Object v, Object highlightBoolean) {
	    AppearanceEditorController aec
		= PlaywriteRoot.getAppearanceEditorController();
	    if (aec != null && aec.isEditorView(v) && v instanceof Hilitable) {
		Hilitable hilitable = (Hilitable) v;
		if (((Boolean) highlightBoolean).booleanValue())
		    hilitable.hilite();
		else
		    hilitable.unhilite();
	    }
	}
    };
    private CharacterPrototype _owner = null;
    private String _name = null;
    private Bitmap _bitmap;
    private Shape _shape;
    private int _initialSquareSize;
    private Vector _displayList = null;
    private UniqueID _uniqueID;
    private UniqueID _uniqueParentID;
    private boolean _proxyFlag = false;
    private Rect _clipRect;
    private Point _drawerPosition;
    private boolean _wasNameGenerated;
    private transient ViewManager _iconViewManager;
    private transient boolean _isHighlightedForAppearanceDrawerSelection;
    private transient GenericContainer _container;
    private transient Hashtable _bitmapCache;
    
    public static interface DisplayItem
    {
	public void draw(int i, int i_0_, int i_1_, int i_2_,
			 CocoaCharacter cocoacharacter, Graphics graphics);
	
	public void wasAddedTo(Appearance appearance);
	
	public void wasRemovedFrom(Appearance appearance);
	
	public void displayedOn(CocoaCharacter cocoacharacter);
	
	public void undisplayedOn(CocoaCharacter cocoacharacter);
	
	public boolean usesVariable(Variable variable);
    }
    
    static void initStatics() {
	INVALID_POSITION = new Point(-1, -1);
	ICON_IMAGE_RECT = new Rect(0, 0, 32, 32);
	defaultSize = new Size(1, 1);
	draw_scaled
	    = PlaywriteSystem.getApplicationPropertyAsBoolean("draw_scaled",
							      true);
	Debug.print("debug.appearance", "DRAW_SCALED = ",
		    draw_scaled ? Boolean.TRUE : Boolean.FALSE);
	MALFORMED_APPEARANCES = new Vector();
	Op.Equal.addOperation(Appearance.class, Op.standardEqualsOp);
    }
    
    public Appearance(String name, Bitmap image, int squareSize, Shape shape) {
	_clipRect = new Rect();
	_drawerPosition = INVALID_POSITION;
	_wasNameGenerated = false;
	_isHighlightedForAppearanceDrawerSelection = false;
	_bitmapCache = new Hashtable();
	_initialSquareSize = squareSize;
	fillInObject(name, image, shape);
    }
    
    public Appearance(String name, Bitmap image, Shape shape) {
	this(name, image, 32, shape);
    }
    
    public Appearance() {
	_clipRect = new Rect();
	_drawerPosition = INVALID_POSITION;
	_wasNameGenerated = false;
	_isHighlightedForAppearanceDrawerSelection = false;
	_bitmapCache = new Hashtable();
	_initialSquareSize = 32;
    }
    
    void fillInObject(String name, Bitmap image, Shape shape) {
	ASSERT.isNotNull(shape);
	_uniqueID = new UniqueID();
	_uniqueParentID = null;
	setName(name);
	setBitmap(image);
	_shape = shape;
    }
    
    public final World getWorld() {
	return _owner.getWorld();
    }
    
    final CharacterPrototype getOwner() {
	return _owner;
    }
    
    final void setOwner(CharacterPrototype cp) {
	if (_owner != null && _owner != cp) {
	    Debug.print("debug.dr", "SHARED APPEARANCE!");
	    throw new BadBackpointerError(cp, this);
	}
	_owner = cp;
    }
    
    public final String getName() {
	return _name == null ? "" : _name;
    }
    
    public final void setName(String newName) {
	setName(newName, false);
    }
    
    public final void setGeneratedName(String newName) {
	setName(newName, true);
    }
    
    private final void setName(String newName, boolean wasGenerated) {
	if (_name == null || _name.equals(newName) == false) {
	    _name = newName;
	    _wasNameGenerated = wasGenerated;
	    Icon.updateIconNames(this);
	}
    }
    
    public final boolean wasNameGenerated() {
	return _wasNameGenerated;
    }
    
    protected final Shape getShape() {
	if (_shape == null && Variable.readingCocoaWorld)
	    _shape = new Shape(1, 1, new Point(1, 1), true);
	return _shape;
    }
    
    void setLocationHV(int h, int v, boolean b) {
	_shape.setLocationHV(h, v, b);
    }
    
    public boolean getLocationHV(int h, int v) {
	return _shape.getLocationHV(h, v);
    }
    
    void changeSize(int newWidth, int newHeight) {
	_shape.changeSize(newWidth, newHeight);
    }
    
    public final int getHomeSquareX() {
	return _shape.getOriginX();
    }
    
    public final int getHomeSquareY() {
	return _shape.getOriginY();
    }
    
    public final Point getHomeSquare() {
	return new Point(getHomeSquareX(), getHomeSquareY());
    }
    
    public final void setHomeSquare(Point pt) {
	_shape.setOrigin(pt.x, pt.y);
    }
    
    public final int getSquareSize() {
	return _initialSquareSize;
    }
    
    protected final void setSquareSize(int sq) {
	_initialSquareSize = sq;
    }
    
    final Vector getDisplayList() {
	return _displayList;
    }
    
    final void setDisplayList(Vector v) {
	_displayList = v;
    }
    
    final Point getDrawerPosition() {
	return _drawerPosition;
    }
    
    final void setDrawerPosition(Point p) {
	_drawerPosition = p;
	if (_drawerPosition.x < 0 || _drawerPosition.y < 0)
	    _drawerPosition = INVALID_POSITION;
    }
    
    public final int getPhysicalWidth() {
	return _bitmap.width();
    }
    
    public final int getPhysicalHeight() {
	return _bitmap.height();
    }
    
    public final int getLogicalWidth() {
	return _shape.getWidth();
    }
    
    public final int getLogicalHeight() {
	return _shape.getHeight();
    }
    
    int getWidthAtSquareSize(int squareSize) {
	return _bitmap.width() * squareSize / _initialSquareSize;
    }
    
    int getHeightAtSquareSize(int squareSize) {
	return _bitmap.height() * squareSize / _initialSquareSize;
    }
    
    static final Vector getMalformedAppearances() {
	return MALFORMED_APPEARANCES;
    }
    
    static String makeDefaultName(int number) {
	Object[] params = { new Integer(number) };
	return Resource.getTextAndFormat("Generator an", params);
    }
    
    public Bitmap getBitmap() {
	return _bitmap;
    }
    
    public Bitmap getBitmap(int fixedSize) {
	return getBitmap(new Size(fixedSize, fixedSize));
    }
    
    protected Bitmap getCachedBitmap(Size size) {
	return (Bitmap) _bitmapCache.get(size);
    }
    
    protected void cacheBitmap(Size size, Bitmap bitmap) {
	_bitmapCache.put(size, bitmap);
    }
    
    protected void clearBitmapCache() {
	_bitmapCache = null;
    }
    
    private Bitmap getBitmap(Size size) {
	Bitmap bitmap = getCachedBitmap(size);
	if (bitmap == null) {
	    Rect scaleRect = new Rect(0, 0, size.width, size.height);
	    Util.scaleRectToImageProportion(scaleRect, _bitmap);
	    Size scaledSize = new Size(scaleRect.width, scaleRect.height);
	    bitmap = getCachedBitmap(scaledSize);
	    if (bitmap == null) {
		bitmap
		    = BitmapManager.createScaledBitmapManager(_bitmap,
							      scaledSize.width,
							      (scaleRect
							       .height));
		cacheBitmap(size, bitmap);
		if (scaledSize.equals(size) == false)
		    cacheBitmap(scaledSize, bitmap);
	    }
	}
	return bitmap;
    }
    
    public Bitmap getBitmapAtSquareSize(int squareSize) {
	if (squareSize == _initialSquareSize)
	    return _bitmap;
	return getBitmap(getSizeForSquareSize(squareSize));
    }
    
    public Size getSizeForSquareSize(int squareSize) {
	return new Size(getWidthAtSquareSize(squareSize),
			getHeightAtSquareSize(squareSize));
    }
    
    public final void setBitmap(Bitmap image) {
	_bitmap = image;
	_bitmapCache = new Hashtable();
	if (image != null)
	    cacheBitmap(new Size(_bitmap.width(), _bitmap.height()), _bitmap);
	Icon.updateIconImages(this);
    }
    
    boolean isLegalFor(CocoaCharacter ch) {
	return ch.hasAppearance(this);
    }
    
    boolean isInLocation(int dx, int dy) {
	return _shape.getSafeLocationDeltaHV(dx, dy);
    }
    
    public boolean isBlank() {
	boolean result = true;
	Dimension size = new Dimension(_bitmap.width(), _bitmap.height());
	int[] pixels = new int[size.width * size.height];
	boolean success = _bitmap.grabPixels(pixels);
	ASSERT.isTrue(success, "grabPixels");
	for (int i = 0; i < pixels.length; i++) {
	    if ((pixels[i] & 0x11000000) != 0) {
		result = false;
		break;
	    }
	}
	return result;
    }
    
    private boolean[][] makeSolidShape(int width, int height) {
	boolean[][] newShape = new boolean[height][width];
	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++)
		newShape[y][x] = true;
	}
	return newShape;
    }
    
    private boolean[][] copyShape(boolean[][] oldShape) {
	int width = oldShape[0].length;
	int height = oldShape.length;
	boolean[][] newShape = new boolean[height][width];
	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++)
		newShape[y][x] = oldShape[y][x];
	}
	return newShape;
    }
    
    /**
     * @deprecated
     */
    public void adjustShape(BoardView boardView, int pixWidth, int pixHeight) {
	int squareSize = boardView.getSquareSize();
	int width = pixWidth / squareSize;
	if (pixWidth % squareSize > 0)
	    width++;
	int height = pixHeight / squareSize;
	if (pixHeight % squareSize > 0)
	    height++;
	_shape = new Shape(width, height, new Point(1, height));
    }
    
    public void adjustShape(int logicalWidth, int logicalHeight) {
	_shape = new Shape(logicalWidth, logicalHeight,
			   new Point(1, logicalHeight));
    }
    
    public final boolean isProxy() {
	return _proxyFlag;
    }
    
    public final void setProxy(boolean b) {
	_proxyFlag = b;
    }
    
    public Object makeProxy(Hashtable map) {
	CharacterPrototype newPrototype = findNewOwner(map);
	Appearance similarAppearance
	    = newPrototype.findSimilarAppearance(this);
	Shape oldShape = getShape();
	Shape newShape = similarAppearance.getShape();
	if (!oldShape.equals(newShape))
	    Debug.print("debug.copy", ("The shape for " + this
				       + " is not the same as the shape for "
				       + similarAppearance));
	Appearance newAppearance
	    = new Appearance(getName(), similarAppearance.getBitmap(),
			     getSquareSize(), (Shape) getShape().clone());
	map.put(this, newAppearance);
	newAppearance.setProxy(true);
	newAppearance.setParentID(getID());
	newPrototype.add(newAppearance);
	return newAppearance;
    }
    
    public void makeReal(Object source, Hashtable map) {
	Appearance oldAppearance = (Appearance) source;
	setProxy(false);
	map.put(oldAppearance, this);
	copyDataFrom(oldAppearance, map);
    }
    
    private boolean shapesEqual(boolean[][] shape1, boolean[][] shape2) {
	if (shape1 == shape2)
	    return true;
	if (shape1.length != shape2.length
	    || shape1[0].length != shape2[0].length)
	    return false;
	for (int i = 0; i < shape1.length; i++) {
	    for (int j = 0; j < shape1[0].length; j++) {
		if (shape1[i][j] != shape2[i][j])
		    return false;
	    }
	}
	return true;
    }
    
    public boolean isVisible() {
	return _proxyFlag ^ true;
    }
    
    public void setVisibility(boolean b) {
	if (b && _proxyFlag)
	    throw new PlaywriteInternalError
		      ("Proxy appearances cannot be visible: " + this);
    }
    
    public final UniqueID getID() {
	return _uniqueID;
    }
    
    public final UniqueID getParentID() {
	return _uniqueParentID;
    }
    
    public final void setParentID(UniqueID id) {
	_uniqueParentID = id;
    }
    
    public boolean isCopyOf(ReferencedObject appearance) {
	return (_uniqueID.equals(appearance.getParentID())
		|| appearance.getID().equals(_uniqueParentID));
    }
    
    int left(int h) {
	return h - getHomeSquareX() + 1;
    }
    
    int right(int h) {
	return h - getHomeSquareX() + getLogicalWidth();
    }
    
    int top(int v) {
	return v - getHomeSquareY() + getLogicalHeight();
    }
    
    int bottom(int v) {
	return v - getHomeSquareY() + 1;
    }
    
    Point topLeftToHome(int h, int v) {
	return new Point(getHomeSquareX() + h - 1,
			 getHomeSquareY() + v - getLogicalHeight());
    }
    
    Rect squaresOccupied(CocoaCharacter ch, Board board) {
	int left = left(ch.getH());
	left = Math.max(left, 1);
	int right = right(ch.getH());
	right = Math.min(right, board.numberOfColumns());
	int top = top(ch.getV());
	top = Math.min(top, board.numberOfRows());
	int bottom = bottom(ch.getV());
	bottom = Math.max(bottom, 1);
	return new Rect(left, bottom, right - left + 1, top - bottom + 1);
    }
    
    Rect pixelBounds(CocoaCharacter ch, BoardView boardView) {
	int squareSize = boardView.getSquareSize();
	Rect rect = new Rect();
	rect.x = boardView.pixelX(left(ch.getH()));
	rect.y = boardView.pixelY(top(ch.getV()));
	rect.width = getLogicalWidth() * squareSize;
	rect.height = getLogicalHeight() * squareSize;
	return rect;
    }
    
    Point pixelHome(BoardView boardView) {
	int squareSize = boardView.getSquareSize();
	Point sq = getHomeSquare();
	sq.x = (sq.x - 1) * squareSize + squareSize / 2;
	sq.y = (getLogicalHeight() - sq.y) * squareSize + squareSize / 2;
	return sq;
    }
    
    void draw(CharacterView characterView, Graphics g, int x, int y,
	      int targetSquareSize) {
	CocoaCharacter ch = (CocoaCharacter) characterView.getModelObject();
	int width;
	int height;
	if (targetSquareSize == _initialSquareSize) {
	    width = getPhysicalWidth();
	    height = getPhysicalHeight();
	    _bitmap.drawAt(g, x, y);
	} else {
	    width = getWidthAtSquareSize(targetSquareSize);
	    height = getHeightAtSquareSize(targetSquareSize);
	    if (draw_scaled)
		_bitmap.drawScaled(g, new Rect(x, y, width, height));
	    else
		getBitmap(new Size(width, height)).drawAt(g, x, y);
	}
	if (characterView.isHilited())
	    Util.drawHilited(g, new Rect(x, y, width, height));
	if (_displayList != null && !_displayList.isEmpty()) {
	    int n = _displayList.size();
	    g.pushState();
	    _clipRect.setBounds(x, y, width, height);
	    g.setClipRect(_clipRect);
	    for (int i = 0; i < n; i++) {
		DisplayItem displayItem
		    = (DisplayItem) _displayList.elementAt(i);
		displayItem.draw(_initialSquareSize, targetSquareSize, x, y,
				 ch, g);
	    }
	    g.popState();
	}
    }
    
    void draw(CharacterView characterView, Graphics g, int x, int y) {
	draw(characterView, g, x, y, _initialSquareSize);
    }
    
    void drawFixed(CharacterView characterView, Graphics g, int x, int y,
		   int fixedSize) {
	Rect rect = new Rect(0, 0, fixedSize, fixedSize);
	Util.scaleRectToImageProportion(rect, _bitmap);
	rect.moveTo(x, y);
	if (draw_scaled)
	    _bitmap.drawScaled(g, rect);
	else
	    getBitmap(new Size(rect.width, rect.height)).drawAt(g, x, y);
	if (characterView != null && characterView.isHilited())
	    Util.drawHilited(g, rect);
    }
    
    void display(Variable v, int x, int y, Font f, Color c) {
	DisplayVariable dv = new DisplayVariable(v, x, y, f, c);
	display(dv);
    }
    
    void display(Variable v, int x, int y, int justification, Font f,
		 Color c) {
	DisplayVariable dv = new DisplayVariable(v, x, y, justification, f, c);
	dv.setTopY(y);
	display(dv);
    }
    
    void display(DisplayItem displayItem) {
	if (_displayList == null)
	    _displayList = new Vector(2);
	if (displayItem instanceof DisplayVariable) {
	    DisplayVariable dv = (DisplayVariable) displayItem;
	    dv.fixIfVersionOne(this);
	    Variable variable = ((DisplayVariable) displayItem).getVariable();
	    if (variable.getListOwner() == null
		|| variable.getListOwner().getVariableList() == null
		|| variable.getListOwner().getVariableList()
		       .hasVariable(variable) == false) {
		Debug.print(true,
			    ("variable " + variable
			     + " wasn't valid and was removed from appearance "
			     + this));
		return;
	    }
	}
	if (!_displayList.containsIdentical(displayItem)) {
	    _displayList.addElement(displayItem);
	    displayItem.wasAddedTo(this);
	}
    }
    
    void unDisplay(DisplayItem displayItem) {
	if (_displayList != null) {
	    _displayList.removeElementIdentical(displayItem);
	    displayItem.wasRemovedFrom(this);
	}
    }
    
    void displayItemsOn(CocoaCharacter character) {
	if (_displayList != null) {
	    int i = _displayList.size();
	    while (i-- > 0)
		((DisplayItem) _displayList.elementAt(i))
		    .displayedOn(character);
	}
    }
    
    void undisplayItemsOn(CocoaCharacter character) {
	if (_displayList != null) {
	    int i = _displayList.size();
	    while (i-- > 0)
		((DisplayItem) _displayList.elementAt(i))
		    .undisplayedOn(character);
	}
    }
    
    boolean isDisplaying(Variable variable) {
	if (_displayList != null) {
	    int i = _displayList.size();
	    while (i-- > 0) {
		if (((DisplayItem) _displayList.elementAt(i))
			.usesVariable(variable))
		    return true;
	    }
	}
	return false;
    }
    
    void appearanceChanged() {
	if (_owner != null)
	    _owner.appearanceChanged(this);
    }
    
    public PlaywriteView makeMiniView() {
	int miniSize = 32;
	Rect rect = new Rect(0, 0, miniSize, miniSize);
	Util.scaleRectToImageProportion(rect, _bitmap);
	return makeViewSized(rect.width, rect.height);
    }
    
    public PlaywriteView makeViewSized(int width, int height) {
	PlaywriteView newView = makeView();
	newView.sizeTo(width, height);
	return newView;
    }
    
    public PlaywriteView makeView() {
	PlaywriteView view = new PlaywriteView(_bitmap);
	view.setImageDisplayStyle(1);
	return view;
    }
    
    public static Shape makeShape(Bitmap image, int targetSquareSize) {
	boolean[][] shapeArray = makeShapeArray(image, targetSquareSize);
	int width = shapeArray[0].length;
	int height = shapeArray.length;
	Shape shape = new Shape(width, height, new Point(1, height), false);
	Point p = new Point();
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		p.moveTo(j, i);
		Util.transformUL0ToLL1(p, height);
		shape.setLocationHV(p.x, p.y, shapeArray[i][j]);
	    }
	}
	return shape;
    }
    
    public static boolean[][] makeShapeArray(Bitmap image,
					     int targetSquareSize) {
	Point shapeIndex = new Point();
	Point squareIndex = new Point();
	Point imageIndex = new Point();
	Dimension imageSize = new Dimension(image.width(), image.height());
	int[] pixels = new int[imageSize.width * imageSize.height];
	boolean success = image.grabPixels(pixels);
	ASSERT.isTrue(success, "grabPixels");
	Dimension shapeSize
	    = (new Dimension
	       ((int) Math.ceil((double) ((float) imageSize.width
					  / (float) targetSquareSize)),
		(int) Math.ceil((double) ((float) imageSize.height
					  / (float) targetSquareSize))));
	boolean[][] shape = new boolean[shapeSize.height][shapeSize.width];
	for (shapeIndex.y = 0; shapeIndex.y < shapeSize.height;
	     shapeIndex.y++) {
	    for (shapeIndex.x = 0; shapeIndex.x < shapeSize.width;
		 shapeIndex.x++) {
		for (squareIndex.y = 0; squareIndex.y < targetSquareSize;
		     squareIndex.y++) {
		    imageIndex.y
			= shapeIndex.y * targetSquareSize + squareIndex.y;
		    if (imageIndex.y >= imageSize.height
			|| shape[shapeIndex.y][shapeIndex.x] == true)
			break;
		    for (squareIndex.x = 0; squareIndex.x < targetSquareSize;
			 squareIndex.x++) {
			imageIndex.x
			    = shapeIndex.x * targetSquareSize + squareIndex.x;
			if (imageIndex.x >= imageSize.width)
			    break;
			int pixelIndex
			    = imageSize.width * imageIndex.y + imageIndex.x;
			if ((pixels[pixelIndex] & 0x11000000) != 0) {
			    shape[shapeIndex.y][shapeIndex.x] = true;
			    break;
			}
		    }
		}
	    }
	}
	return shape;
    }
    
    public static Shape createShapeFromMask(boolean[][] mask,
					    Point oneBasedOrigin$) {
	int width = mask.length;
	int height = mask[0].length;
	Shape result = new Shape(width, height, oneBasedOrigin$, false);
	Point temp = new Point();
	for (int i = 0; i < width; i++) {
	    for (int j = 0; j < height; j++) {
		temp.moveTo(i, j);
		Util.transformUL0ToLL1(temp, height);
		result.setLocationHV(temp.x, temp.y, mask[i][j]);
	    }
	}
	return result;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(getName());
	ASSERT.isNotNull(_bitmap);
	ASSERT.isNotNull(_shape);
	ASSERT.isNotNull(_owner);
	ASSERT.isNotNull(_uniqueID);
	out.writeUTF(getName());
	out.writeObject(_bitmap);
	out.writeInt(_drawerPosition.x);
	out.writeInt(_drawerPosition.y);
	out.writeObject(_shape);
	out.writeInt(_initialSquareSize);
	out.writeObject(_owner);
	out.writeObject(_uniqueID);
	out.writeObject(_uniqueParentID);
	((WorldOutStream) out).writeVector(_displayList);
	out.writeBoolean(_proxyFlag);
	out.writeBoolean(_wasNameGenerated);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(Appearance.class);
	String name = in.readUTF();
	Bitmap image;
	switch (version) {
	case 2:
	case 3:
	case 4:
	case 5:
	    image = (Bitmap) in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 5);
	}
	_drawerPosition = new Point();
	_drawerPosition.x = in.readInt();
	_drawerPosition.y = in.readInt();
	if (_drawerPosition.equals(INVALID_POSITION))
	    _drawerPosition = INVALID_POSITION;
	Shape shape;
	switch (version) {
	case 1:
	case 2:
	case 3:
	case 4: {
	    in.readInt();
	    in.readInt();
	    boolean[][] shapeArray = (boolean[][]) in.readObject();
	    boolean[][] widthHeightShapeArray
		= new boolean[shapeArray[0].length][shapeArray.length];
	    for (int i = 0; i < shapeArray.length; i++) {
		for (int j = 0; j < shapeArray[0].length; j++)
		    widthHeightShapeArray[j][i] = shapeArray[i][j];
	    }
	    Point pt = new Point();
	    pt.x = in.readInt();
	    pt.y = in.readInt();
	    shape = createShapeFromMask(widthHeightShapeArray, pt);
	    break;
	}
	case 5:
	    shape = (Shape) in.readObject();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 5);
	}
	fillInObject(name, image, shape);
	_initialSquareSize = in.readInt();
	setOwner((CharacterPrototype) in.readObject());
	_uniqueID = (UniqueID) in.readObject();
	_uniqueParentID = (UniqueID) in.readObject();
	if (_owner == null) {
	    Debug.print("debug.appearance", "owner is null for '", this,
			"', version = ", new Integer(version), ", ID = ",
			_uniqueID);
	    MALFORMED_APPEARANCES.addElement(this);
	}
	Vector dv = ((WorldInStream) in).readVector();
	if (dv != null) {
	    for (int i = 0; i < dv.size(); i++)
		display((DisplayItem) dv.elementAt(i));
	}
	switch (version) {
	case 3:
	case 4:
	case 5:
	    _proxyFlag = in.readBoolean();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 5);
	case 1:
	case 2:
	    /* empty */
	}
	switch (version) {
	case 1:
	case 2:
	case 3:
	    _wasNameGenerated = false;
	    break;
	case 4:
	case 5:
	    _wasNameGenerated = in.readBoolean();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 5);
	}
	ObjectSieve sieve = _owner.getWorld().getObjectSieve();
	if (sieve != null) {
	    sieve.creation(this);
	    createServerDisplayVars();
	}
    }
    
    public void createServerDisplayVars() {
	if (_displayList != null) {
	    ObjectSieve sieve = _owner.getWorld().getObjectSieve();
	    VariableSieve vs = _owner.getWorld().getVariableSieve();
	    for (int i = 0; i < _displayList.size(); i++) {
		DisplayVariable dvar
		    = (DisplayVariable) _displayList.elementAt(i);
		sieve.creation(dvar);
		vs.variableIsInteresting(dvar.getVariable());
	    }
	}
    }
    
    public boolean equals(Object thing) {
	if (thing instanceof Appearance)
	    return (this == thing
		    || getName()
			   .equalsIgnoreCase(((Appearance) thing).getName()));
	if (thing instanceof String)
	    return getName().equalsIgnoreCase((String) thing);
	return false;
    }
    
    public final String toString() {
	String result = null;
	try {
	    result = getName();
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
    
    public boolean isSimilarTo(Appearance app) {
	if (this == app)
	    return true;
	if (_shape.equals(app.getShape()))
	    return true;
	return false;
    }
    
    public PlaywriteView createView() {
	return createIconView();
    }
    
    public PlaywriteView createIconView() {
	return new Icon(this);
    }
    
    public Image getIconImage() {
	return draw_scaled ? getBitmap() : getBitmap(32);
    }
    
    public Rect getIconImageRect() {
	return ICON_IMAGE_RECT;
    }
    
    public void setIconImage(Image image) {
	/* empty */
    }
    
    public String getIconName() {
	return getName();
    }
    
    public void setIconName(String newName) {
	setName(newName);
    }
    
    public boolean hasIconViews() {
	return _iconViewManager != null && _iconViewManager.hasViews();
    }
    
    public ViewManager getIconViewManager() {
	if (_iconViewManager == null)
	    _iconViewManager = new ViewManager(this);
	return _iconViewManager;
    }
    
    public GenericContainer getContainer() {
	return _container;
    }
    
    public void setContainer(GenericContainer container) {
	_container = container;
    }
    
    public void highlightForSelection() {
	getIconViewManager().updateViews(appearanceDrawerHighlighter,
					 Boolean.TRUE);
    }
    
    public void unhighlightForSelection() {
	getIconViewManager().updateViews(appearanceDrawerHighlighter,
					 Boolean.FALSE);
    }
    
    void highlightForAppearanceDrawerSelection() {
	_isHighlightedForAppearanceDrawerSelection = true;
	highlightForSelection();
    }
    
    void unhighlightForAppearanceDrawerSelection() {
	_isHighlightedForAppearanceDrawerSelection = false;
	unhighlightForSelection();
    }
    
    boolean isHighlightedForAppearanceDrawerSelection() {
	return _isHighlightedForAppearanceDrawerSelection;
    }
    
    public Object copy() {
	return copy(new Hashtable(50), true);
    }
    
    public Object copy(World newWorld) {
	Hashtable map = new Hashtable(50);
	if (getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	return copy(map, true);
    }
    
    Object copy(World newWorld, CharacterPrototype newPrototype) {
	Hashtable map = new Hashtable(50);
	if (getWorld() != newWorld)
	    map.put(getWorld(), newWorld);
	if (_owner != newPrototype)
	    map.put(_owner, newPrototype);
	return copy(map, true);
    }
    
    public Object copy(Hashtable map, boolean fullCopy) {
	Appearance newAppearance = (Appearance) map.get(this);
	if (newAppearance != null) {
	    if (newAppearance.isProxy() && fullCopy)
		newAppearance.makeReal(this, map);
	    return newAppearance;
	}
	World oldWorld = getWorld();
	World newWorld = (World) map.get(oldWorld);
	CharacterPrototype oldOwner = getOwner();
	CharacterPrototype newOwner = findNewOwner(map);
	if (oldOwner != newOwner)
	    newAppearance = newOwner.findCopy(this);
	if (newAppearance == null) {
	    if (fullCopy) {
		String newName;
		if (oldOwner == newOwner)
		    newName
			= newOwner.makeUniqueCopiedApppearanceName(getName());
		else
		    newName = newOwner.makeUniqueApppearanceName(getName());
		newAppearance = new Appearance(newName, null, getSquareSize(),
					       (Shape) getShape().clone());
		map.put(this, newAppearance);
		newAppearance._wasNameGenerated = true;
		newAppearance.copyDataFrom(this, map);
		if (oldOwner != newOwner)
		    newAppearance.setParentID(getID());
	    } else if (oldOwner == newOwner)
		newAppearance = this;
	    else if ((newAppearance = newOwner.getAppearanceNamed(getName()))
		     != null)
		map.put(this, newAppearance);
	    else
		newAppearance = (Appearance) makeProxy(map);
	} else if (newAppearance.isProxy() && fullCopy)
	    newAppearance.makeReal(this, map);
	else
	    map.put(this, newAppearance);
	newAppearance.setOwner(newOwner);
	return newAppearance;
    }
    
    void copyDataFrom(Appearance oldAppearance, Hashtable map) {
	Vector newVars = null;
	World oldWorld = oldAppearance.getWorld();
	World newWorld = (World) map.get(oldWorld);
	CharacterPrototype oldOwner = oldAppearance.getOwner();
	CharacterPrototype newOwner = oldAppearance.findNewOwner(map);
	if (newWorld == null)
	    setBitmap(oldAppearance.getBitmap());
	else
	    setBitmap(BitmapManager.copy(oldAppearance.getBitmap()));
	setSquareSize(oldAppearance.getSquareSize());
	_shape = (Shape) oldAppearance.getShape().clone();
	Vector oldVars = oldAppearance.getDisplayList();
	if (oldVars == null)
	    newVars = null;
	else if (oldOwner == newOwner)
	    newVars = (Vector) oldVars.clone();
	else
	    newVars = copyDisplayedVariables(oldAppearance, newOwner, map);
	setDisplayList(newVars);
	if (oldOwner != newOwner)
	    setDrawerPosition(oldAppearance.getDrawerPosition());
    }
    
    private Vector copyDisplayedVariables(Appearance oldAppearance,
					  CharacterPrototype newOwner,
					  Hashtable map) {
	Vector oldVars = oldAppearance.getDisplayList();
	if (oldVars == null)
	    return null;
	Vector newVars = new Vector(oldVars.size());
	for (int i = 0; i < oldVars.size(); i++) {
	    DisplayVariable oldDV = (DisplayVariable) oldVars.elementAt(i);
	    Variable newV = (Variable) oldDV.getVariable().copy(map, true);
	    newVars.addElement(new DisplayVariable(newV, oldDV));
	}
	return newVars;
    }
    
    protected CharacterPrototype findNewOwner(Hashtable map) {
	CharacterPrototype oldPrototype = _owner;
	CharacterPrototype newPrototype
	    = (CharacterPrototype) map.get(oldPrototype);
	if (newPrototype != null)
	    return newPrototype;
	World oldWorld = oldPrototype.getWorld();
	World newWorld = (World) map.get(oldWorld);
	if (newPrototype == null && newWorld != null)
	    newPrototype = newWorld.findCopy(oldPrototype);
	if (newPrototype == null) {
	    if (newWorld == null)
		newPrototype = oldPrototype;
	    else
		newPrototype
		    = (CharacterPrototype) oldPrototype.copy(map, false);
	}
	return newPrototype;
    }
    
    public boolean allowDelete() {
	if (this instanceof AnythingAppearance
	    || this instanceof JarAppearance)
	    return false;
	if (_owner.numberOfAppearances() < 2)
	    return false;
	if (getWorld().ruleRefersTo(this, "REFOBJ app ID"))
	    return false;
	return true;
    }
    
    public void delete() {
	internalDelete(true);
    }
    
    private void internalDelete(boolean isReferenced) {
	if (!(this instanceof AnythingAppearance)
	    && !(this instanceof JarAppearance)) {
	    if (_owner != null) {
		_owner.appearanceIsDeleted(this);
		Variable.resetVariablesSetTo(this, getWorld());
		while (_displayList != null && !_displayList.isEmpty())
		    unDisplay((DisplayItem) _displayList.lastElement());
		if (isReferenced)
		    getWorld().referencedObjectWasDeleted();
		ObjectSieve sieve = getWorld().getObjectSieve();
		if (sieve != null)
		    sieve.destruction(this);
	    }
	    if (_iconViewManager != null) {
		_iconViewManager.updateViews(new ViewManager.ViewUpdater() {
		    public void updateView(Object view, Object value) {
			((Iconish) view).discardIcon();
		    }
		}, null);
		_iconViewManager.delete();
	    }
	    _owner = null;
	    _container = null;
	    _shape = null;
	    if (_bitmap != null)
		_bitmap.flush();
	    _bitmap = null;
	    _bitmapCache = null;
	    _iconViewManager = null;
	    _displayList = null;
	}
    }
    
    public void deleteSpecial() {
	internalDelete(false);
    }
    
    public void undelete() {
	throw new PlaywriteInternalError
		  ("Deleting appearances cannot be undone.");
    }
}
