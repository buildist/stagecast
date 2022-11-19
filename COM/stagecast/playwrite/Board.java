/* Board - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;

public abstract class Board
    implements CharacterContainer, Copyable, Debug.Constants, Target, Worldly
{
    static final int MIN_SQUARE_SIZE = 4;
    static final int MAX_SQUARE_SIZE = 256;
    public static final int defaultSquareSize = 32;
    static final Color defaultColor = Util.defaultBoardColor;
    static final int storeVersion = 6;
    static final long serialVersionUID = -3819410108753511730L;
    public static final String WORLD_EVENT_REMOVE_CHARACTER
	= "world event remove character";
    public static final String WORLD_EVENT_RELOCATE_CHARACTER
	= "world event relocate character";
    public static final String WORLD_EVENT_ADD_CHARACTER
	= "world event add character";
    public static final String WORLD_EVENT_CHANGE_APPEARANCE
	= "world event change appearance";
    public static final String STAGE_SET_Z_ACTION_ID
	= "Stagecast.Stage:action.set_z";
    public static final String WORLD_EVENT_CHANGE_SQUARE_SIZE
	= "world event change square size";
    private World world = null;
    private int nRows;
    private int nColumns;
    private int squareSize;
    private Color backgroundColor = defaultColor;
    private Bitmap backgroundImage = null;
    private int _backgroundAlignment = 1;
    private Vector characters;
    private transient Vector[][] contents;
    private transient Vector views;
    private transient boolean showGrid;
    private transient Color gridColor;
    private transient Vector _loadChars;
    
    Board(int width, int height, World world, int sqSize) {
	characters = new Vector(1);
	views = new Vector(1);
	showGrid = false;
	gridColor = Util.defaultGridColor;
	_loadChars = null;
	fillInObject(width, height, world, sqSize);
    }
    
    Board() {
	characters = new Vector(1);
	views = new Vector(1);
	showGrid = false;
	gridColor = Util.defaultGridColor;
	_loadChars = null;
    }
    
    void fillInObject(int width, int height, World w, int sqSize) {
	privateFillInObject(width, height, w, sqSize);
    }
    
    private void privateFillInObject(int width, int height, World w,
				     int sqSize) {
	if (world == null) {
	    world = w;
	    nRows = height;
	    nColumns = width;
	    squareSize = sqSize;
	    contents = new Vector[nColumns][nRows];
	}
    }
    
    final int numberOfRows() {
	return nRows;
    }
    
    final int getNumberOfRows() {
	return nRows;
    }
    
    final int getSquareHeight() {
	return nRows;
    }
    
    final int numberOfColumns() {
	return nColumns;
    }
    
    final int getNumberOfColumns() {
	return nColumns;
    }
    
    final int getSquareWidth() {
	return nColumns;
    }
    
    public final int getSquareSize() {
	return squareSize;
    }
    
    void setSquareSize(int i) {
	changeSquareSize(i);
    }
    
    public final Vector getCharacters() {
	return characters;
    }
    
    public final Vector getViews() {
	return views;
    }
    
    public final Bitmap getBackgroundImage() {
	return backgroundImage;
    }
    
    void setBackgroundImage(Bitmap bg) {
	synchronized (views) {
	    for (int i = 0; i < views.size(); i++) {
		BoardView boardView = (BoardView) views.elementAt(i);
		boardView.unlockBackground();
	    }
	    backgroundImage = bg;
	    for (int i = 0; i < views.size(); i++) {
		BoardView boardView = (BoardView) views.elementAt(i);
		boardView.lockBackground();
	    }
	}
    }
    
    final int getBackgroundAlignment() {
	return _backgroundAlignment;
    }
    
    void setBackgroundAlignment(int align) {
	_backgroundAlignment = align;
    }
    
    final Color getBackgroundColor() {
	return backgroundColor;
    }
    
    void setBackgroundColor(Color color) {
	backgroundColor = color;
    }
    
    final Color getGridColor() {
	return gridColor;
    }
    
    final void setGridColor(Color color) {
	gridColor = color;
    }
    
    void showGrid() {
	showGrid = true;
	invalidateScreen(true);
    }
    
    void hideGrid() {
	showGrid = false;
	invalidateScreen(true);
    }
    
    boolean wantsGrid() {
	if (showGrid)
	    return true;
	if (((Boolean)
	     Variable.getSystemValue(World.SYS_ENABLE_GRID_VARIABLE_ID,
				     getWorld()))
		.booleanValue())
	    return true;
	if (PlaywriteRoot.isAuthoring() && RuleEditor.isRecordingOrEditing()
	    && !RuleEditor.isRuleEditing()
	    && RuleEditor.getSelfContainer() == this)
	    return true;
	return false;
    }
    
    boolean isDontCareVisible() {
	return false;
    }
    
    void setCharsToLoad(Vector chars) {
	ASSERT.isNull(_loadChars);
	_loadChars = chars;
    }
    
    int desiredWidth() {
	return nColumns * squareSize;
    }
    
    int desiredHeight() {
	return nRows * squareSize;
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
    
    public void delete() {
	ASSERT.isInEventThread();
	deleteAllCharacters();
	for (int i = 0; i < views.size(); i++) {
	    BoardView view = (BoardView) views.elementAt(i);
	    view.discard();
	}
	if (backgroundImage != null)
	    backgroundImage.flush();
	world = null;
	backgroundImage = null;
    }
    
    void deleteAllCharacters() {
	if (getWorld().getState() == World.CLOSING)
	    characters.removeAllElements();
	else {
	    while (!characters.isEmpty())
		deleteCharacter((CharacterInstance) characters.lastElement());
	}
    }
    
    public void add(Contained obj) {
	throw new PlaywriteInternalError
		  ("use CharacterContainer add, not GenericContainer");
    }
    
    public boolean allowRemove(Contained obj) {
	if (obj instanceof Deletable)
	    return ((Deletable) obj).allowDelete();
	return false;
    }
    
    public void remove(Contained obj) {
	remove((CocoaCharacter) obj);
    }
    
    public void update(Contained obj) {
	throw new PlaywriteInternalError
		  ("Use CharacterContainer update, not GenericContainer");
    }
    
    public World getWorld() {
	return world;
    }
    
    final void addAgain(CocoaCharacter ch, boolean validate) {
	add(ch, ch.getH(), ch.getV(), -1, validate);
    }
    
    public void add(CocoaCharacter ch, int h, int v, int z) {
	add(ch, h, v, z, true);
    }
    
    void add(CocoaCharacter ch, int h, int v, int z, boolean validate) {
	ch.setContainer(this);
	characters.addElement(ch);
	setZ(ch, z);
	putInSquare(ch, h, v, validate);
	getWorld().addSyncAction(this, "world event add character", ch);
    }
    
    public void remove(CocoaCharacter ch) {
	synchronized (this) {
	    characters.removeElementIdentical(ch);
	}
	removeFromSquare(ch);
	getWorld().addSyncAction(this, "world event remove character", ch);
	ch.setContainer(null);
    }
    
    public void deleteCharacter(CocoaCharacter ch) {
	ch.setOldX(ch.getH());
	ch.setOldY(ch.getV());
	ch.setOldZ(ch.getZ());
	remove(ch);
	ch.delete();
    }
    
    public void undeleteCharacter(CocoaCharacter ch) {
	ch.undelete();
	add(ch, ch.getOldX(), ch.getOldY(), ch.getOldZ());
    }
    
    public void relocate(CocoaCharacter ch, int h, int v, int z) {
	removeFromSquare(ch);
	putInSquare(ch, h, v, true);
	setZ(ch, z);
	getWorld().addSyncAction(this, "world event relocate character", ch);
    }
    
    protected void putAppearanceOnBoard(CocoaCharacter ch, Appearance app) {
	int left = app.left(ch.getH());
	int bottom = app.bottom(ch.getV());
	for (int dh = 0; dh < app.getLogicalWidth(); dh++) {
	    for (int dv = 0; dv < app.getLogicalHeight(); dv++) {
		if (app.getLocationHV(dh + 1, dv + 1)) {
		    Vector sq = getSquare(left + dh, bottom + dv);
		    if (sq != null)
			sq.addElementIfAbsent(ch);
		}
	    }
	}
    }
    
    protected void removeAppearanceFromBoard(CocoaCharacter ch,
					     Appearance app) {
	int left = app.left(ch.getH());
	int bottom = app.bottom(ch.getV());
	for (int dh = 0; dh < app.getLogicalWidth(); dh++) {
	    for (int dv = 0; dv < app.getLogicalHeight(); dv++) {
		if (app.getLocationHV(dh + 1, dv + 1)) {
		    Vector sq = getSquare(left + dh, bottom + dv);
		    if (sq != null)
			sq.removeElementIdentical(ch);
		}
	    }
	}
    }
    
    public void changeAppearance(CocoaCharacter ch, Appearance oldAppearance,
				 Appearance newAppearance) {
	if (newAppearance != null) {
	    if (oldAppearance == null
		|| !oldAppearance.isSimilarTo(newAppearance)) {
		if (oldAppearance != null)
		    removeAppearanceFromBoard(ch, oldAppearance);
		putAppearanceOnBoard(ch, newAppearance);
	    }
	    if (oldAppearance != null)
		getWorld().addSyncAction(this, "world event change appearance",
					 ch);
	}
    }
    
    public void update(CocoaCharacter ch, Variable v) {
	/* empty */
    }
    
    public int getZ(CocoaCharacter ch) {
	return characters.indexOfIdentical(ch);
    }
    
    public int setZ(CocoaCharacter ch, int z) {
	int result = z;
	if (ch.getContainer() != this)
	    throw new BadBackpointerError(this, ch);
	characters.removeElementIdentical(ch);
	if (z < 0 || z >= characters.size()) {
	    characters.addElement(ch);
	    result = characters.size() - 1;
	} else
	    characters.insertElementAt(ch, z);
	World world = getWorld();
	world.addSyncAction(this, "Stagecast.Stage:action.set_z", ch);
	return result;
    }
    
    public void makeVisible(CocoaCharacter ch) {
	CharacterContainer container = ch.getCharContainer();
	if (container instanceof Board)
	    ((Board) container).makeVisible(ch, this);
	else
	    container.makeVisible(ch);
    }
    
    void makeVisible(CocoaCharacter ch, CharacterContainer oldContainer) {
	ASSERT.isTrue(false);
    }
    
    Vector sortZ(Vector v) {
	Vector newV = new Vector(v.size());
	for (int i = 0; i < characters.size(); i++) {
	    Object ch = characters.elementAt(i);
	    if (v.containsIdentical(ch))
		newV.addElement(ch);
	}
	return newV;
    }
    
    CocoaCharacter getCharacterAtZ(int z) {
	if (z < 0 || z >= characters.size())
	    return null;
	return (CocoaCharacter) characters.elementAt(z);
    }
    
    CocoaCharacter topVisibleCharacter(int h, int v) {
	Vector square = getVisibleCharacters(h, v);
	if (square == null || square.isEmpty())
	    return null;
	return (CocoaCharacter) sortZ(square).lastElement();
    }
    
    Vector getVisibleCharacters(int h, int v) {
	Vector visibleChars = null;
	Vector square = getSquareData(h, v);
	if (square != null) {
	    visibleChars = new Vector(square.size());
	    for (int i = 0; i < square.size(); i++) {
		if (((CocoaCharacter) square.elementAt(i)).isVisible())
		    visibleChars.addElement(square.elementAt(i));
	    }
	}
	return visibleChars;
    }
    
    final boolean isOnBoard(int h, int v) {
	return validateH(h) != 0 && validateV(v) != 0;
    }
    
    int validateH(int h) {
	if (h < 1 || h > nColumns)
	    return 0;
	return h;
    }
    
    int validateV(int v) {
	if (v < 1 || v > nRows)
	    return 0;
	return v;
    }
    
    int validH(int h) {
	if (h < 1)
	    return 1;
	if (h > nColumns)
	    return nColumns;
	return h;
    }
    
    int validV(int v) {
	if (v < 1)
	    return 1;
	if (v > nRows)
	    return nRows;
	return v;
    }
    
    Rect squaresOccupied(CocoaCharacter ch) {
	return ch.getCurrentAppearance().squaresOccupied(ch, this);
    }
    
    protected Vector getSquareData(int h, int v) {
	return contents[h - 1][nRows - v];
    }
    
    protected void setSquareData(int h, int v, Vector sq) {
	contents[h - 1][nRows - v] = sq;
    }
    
    final void rebuild() {
	Vector oldCharList = (Vector) getCharacters().clone();
	for (int i = 0; i < oldCharList.size(); i++)
	    remove((CocoaCharacter) oldCharList.elementAt(i));
	int hmax = numberOfColumns();
	int vmax = numberOfRows();
	for (int h = 1; h <= hmax; h++) {
	    for (int v = 1; v <= vmax; v++) {
		Vector chars = getSquareData(h, v);
		if (chars != null)
		    chars.removeAllElements();
	    }
	}
	for (int i = 0; i < oldCharList.size(); i++) {
	    CocoaCharacter cocoaCharacter
		= (CocoaCharacter) oldCharList.elementAt(i);
	    addAgain(cocoaCharacter, false);
	}
    }
    
    Vector getSquare(int h, int v) {
	h = validateH(h);
	if (h == 0)
	    return null;
	v = validateV(v);
	if (v == 0)
	    return null;
	Vector sq = getSquareData(h, v);
	if (sq == null) {
	    sq = new Vector(1);
	    setSquareData(h, v, sq);
	}
	return sq;
    }
    
    public void putInSquare(CocoaCharacter ch, int h, int v,
			    boolean validate) {
	if (validate) {
	    h = validH(h);
	    v = validV(v);
	}
	ch.setLocation(h, v);
	putAppearanceOnBoard(ch, ch.getCurrentAppearance());
    }
    
    public void removeFromSquare(CocoaCharacter ch) {
	removeAppearanceFromBoard(ch, ch.getCurrentAppearance());
    }
    
    void resetContentsArray() {
	for (int h = 0; h < nColumns; h++) {
	    for (int v = 0; v < nRows; v++)
		contents[h][v] = null;
	}
    }
    
    void growLeftBy(int nSquares) {
	ASSERT.isInEventThread();
	ASSERT.isTrue(getWorld().isInSyncPhase());
	Vector deletedChars = new Vector(2);
	if (nSquares != 0) {
	    nColumns = nColumns + nSquares;
	    contents = new Vector[nColumns][nRows];
	    for (int i = 0; i < characters.size(); i++) {
		CocoaCharacter ch = (CocoaCharacter) characters.elementAt(i);
		int h = ch.getH() + nSquares;
		int v = ch.getV();
		if (onBoard(ch, h, v))
		    putInSquare(ch, h, v, false);
		else
		    deletedChars.addElement(ch);
	    }
	    deleteCharacters(deletedChars);
	    repositionCharacterViews();
	    resizeViews();
	    moveSpotlights(-nSquares, 0);
	}
    }
    
    void growRightBy(int nSquares) {
	if (nSquares != 0) {
	    changeSize(nColumns + nSquares, nRows);
	    moveSpotlights(0, 0);
	}
    }
    
    void growUpBy(int nSquares) {
	if (nSquares != 0) {
	    changeSize(nColumns, nRows + nSquares);
	    repositionCharacterViews();
	    moveSpotlights(0, -nSquares);
	}
    }
    
    void growDownBy(int nSquares) {
	ASSERT.isInEventThread();
	ASSERT.isTrue(getWorld().isInSyncPhase());
	Vector deletedChars = new Vector(2);
	if (nSquares != 0) {
	    nRows = nRows + nSquares;
	    contents = new Vector[nColumns][nRows];
	    for (int i = 0; i < characters.size(); i++) {
		CocoaCharacter ch = (CocoaCharacter) characters.elementAt(i);
		int h = ch.getH();
		int v = ch.getV() + nSquares;
		if (onBoard(ch, h, v))
		    putInSquare(ch, h, v, false);
		else
		    deletedChars.addElement(ch);
	    }
	    deleteCharacters(deletedChars);
	    resizeViews();
	    moveSpotlights(0, 0);
	}
    }
    
    public void changeSize(int newWidth, int newHeight) {
	ASSERT.isInEventThread();
	ASSERT.isTrue(getWorld().isInSyncPhase());
	Vector deletedChars = new Vector(2);
	if (newWidth != nColumns || newHeight != nRows) {
	    nColumns = newWidth;
	    nRows = newHeight;
	    contents = new Vector[nColumns][nRows];
	    for (int i = 0; i < characters.size(); i++) {
		CocoaCharacter ch = (CocoaCharacter) characters.elementAt(i);
		int h = ch.getH();
		int v = ch.getV();
		if (onBoard(ch, h, v))
		    putInSquare(ch, h, v, false);
		else
		    deletedChars.addElement(ch);
	    }
	    deleteCharacters(deletedChars);
	    resizeViews();
	}
    }
    
    boolean onBoard(CocoaCharacter ch, int h, int v) {
	return (ch.getTop(v) >= 1 && ch.getBottom(v) <= nRows
		&& ch.getRight(h) >= 1 && ch.getLeft(h) <= nColumns);
    }
    
    void deleteCharacters(Vector dChars) {
	for (int i = 0; i < dChars.size(); i++) {
	    CocoaCharacter ch = (CocoaCharacter) dChars.elementAt(i);
	    deleteCharacter(ch);
	}
    }
    
    private void repositionCharacterViews() {
	ASSERT.isInEventThread();
	for (int i = 0; i < views.size(); i++) {
	    BoardView boardView = (BoardView) views.elementAt(i);
	    boardView.repositionCharacterViews();
	}
    }
    
    private void resizeViews() {
	ASSERT.isInEventThread();
	for (int i = 0; i < views.size(); i++) {
	    BoardView boardView = (BoardView) views.elementAt(i);
	    boardView.resize();
	}
    }
    
    private void moveSpotlights(int dx, int dy) {
	if (!PlaywriteRoot.isPlayer() && RuleEditor.isRecordingOrEditing()
	    && !RuleEditor.isRuleEditing() && this instanceof AfterBoard) {
	    ASSERT.isInEventThread();
	    ASSERT.isTrue(getWorld().isRunning() ^ true);
	    Stage stage = RuleEditor.getSelfContainer();
	    Vector stageViews = stage.getViews();
	    for (int i = 0; i < stageViews.size(); i++) {
		BoardView stageView = (BoardView) stageViews.elementAt(i);
		BoardView spotlight = stageView.getSpotlight();
		int pixelDx = stageView.getSquareSize() * dx;
		int pixelDy = stageView.getSquareSize() * dy;
		if (views.containsIdentical(spotlight))
		    stageView.moveSpotlight(pixelDx, pixelDy);
	    }
	}
    }
    
    private void changeSquareSize(int newSize) {
	if (newSize < 4)
	    squareSize = 4;
	else if (newSize > 256)
	    squareSize = 256;
	else
	    squareSize = newSize;
	getWorld().addSyncAction(this, "world event change square size", null);
    }
    
    public PlaywriteView createView() {
	return new BoardView(this);
    }
    
    void connectView(View view) {
	ASSERT.isInEventThread();
	views.addElement(view);
    }
    
    void disconnectView(View view) {
	ASSERT.isInEventThread();
	views.removeElementIdentical(view);
    }
    
    void invalidateScreen(boolean b) {
	ASSERT.isInEventThread();
	for (int i = 0; i < views.size(); i++)
	    ((View) views.elementAt(i)).setDirty(b);
    }
    
    void flushImages() {
	if (backgroundImage != null)
	    backgroundImage.flush();
    }
    
    void rebuildCharacterList() {
	if (characters != null && !characters.isEmpty())
	    throw new PlaywriteInternalError
		      ("rebuildCharacterList called when Board has characters!");
	if (_loadChars != null) {
	    characters = new Vector(_loadChars.size());
	    for (int i = 0; i < _loadChars.size(); i++) {
		CocoaCharacter ch = (CocoaCharacter) _loadChars.elementAt(i);
		if (!characters.containsIdentical(ch))
		    addAgain(ch, false);
	    }
	    _loadChars = null;
	}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isNotNull(world);
	out.writeObject(world);
	out.writeInt(nRows);
	out.writeInt(nColumns);
	out.writeInt(squareSize);
	((WorldOutStream) out).writeVector(characters);
    }
    
    public void performCommand(String command, Object data) {
	ASSERT.isInEventThread();
	if (command == "world event remove character") {
	    CocoaCharacter ch = (CocoaCharacter) data;
	    for (int i = 0; i < views.size(); i++) {
		BoardView view = (BoardView) views.elementAt(i);
		view.remove(ch);
	    }
	} else if (command == "world event relocate character") {
	    CocoaCharacter ch = (CocoaCharacter) data;
	    for (int i = 0; i < views.size(); i++) {
		BoardView view = (BoardView) views.elementAt(i);
		view.relocate(ch);
	    }
	} else if (command == "world event add character") {
	    CocoaCharacter ch = (CocoaCharacter) data;
	    if (!ch.isDeleted()) {
		for (int i = 0; i < views.size(); i++) {
		    BoardView view = (BoardView) views.elementAt(i);
		    view.add(ch);
		}
	    }
	} else if (command == "world event change appearance") {
	    CocoaCharacter ch = (CocoaCharacter) data;
	    for (int i = 0; i < views.size(); i++) {
		BoardView view = (BoardView) views.elementAt(i);
		view.changeAppearance(ch);
	    }
	} else if (command == "Stagecast.Stage:action.set_z") {
	    CocoaCharacter ch = (CocoaCharacter) data;
	    for (int i = 0; i < views.size(); i++) {
		BoardView view = (BoardView) views.elementAt(i);
		view.markDirty(ch);
	    }
	} else if (command == "world event change square size") {
	    for (int i = 0; i < views.size(); i++) {
		BoardView view = (BoardView) views.elementAt(i);
		view.setSquareSize(getSquareSize());
	    }
	}
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	int version = ((WorldInStream) in).loadVersion(Board.class);
	World world = (World) in.readObject();
	int nRows = in.readInt();
	int nColumns = in.readInt();
	int squareSize = -1;
	switch (version) {
	case 2:
	    squareSize = 32;
	    backgroundColor = defaultColor;
	    backgroundImage = (Bitmap) in.readObject();
	    break;
	case 3:
	    squareSize = in.readInt();
	    backgroundColor = defaultColor;
	    backgroundImage = (Bitmap) in.readObject();
	    break;
	case 4:
	    squareSize = in.readInt();
	    backgroundColor = new Color(in.readInt());
	    backgroundImage = (Bitmap) in.readObject();
	    break;
	case 5:
	case 6:
	    squareSize = in.readInt();
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 6);
	}
	privateFillInObject(nColumns, nRows, world, squareSize);
	switch (version) {
	case 6:
	    setCharsToLoad(((WorldInStream) in).readVector());
	    break;
	default:
	    throw new UnknownVersionError(this.getClass(), version, 6);
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	    /* empty */
	}
    }
    
    public abstract Object copy(Hashtable hashtable, boolean bool);
}
