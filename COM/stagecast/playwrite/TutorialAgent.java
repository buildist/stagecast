/* TutorialAgent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Image;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.Timer;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class TutorialAgent extends InternalWindow
    implements Debug.Constants, ResourceIDs.CommandIDs, Target
{
    private static final int FPS = 25;
    private static final int SPEED = 120;
    private static final String ANIMATE = "animate";
    public static final String HIDE = "hide";
    static final String CMD_ANIMATE = "cmd Animate";
    static final String CMD_CLICKON = "clickOn";
    static final String CMD_CLICKTOOL = "clickTool";
    static final String CMD_DRAGTOOLTO = "dragToolTo";
    static final String CMD_DRAGABCHAR = "dragABChar";
    static final String CMD_DRAGABHANDLE = "dragABHandle";
    static final String CMD_HIDE = "hide";
    static final String CMD_SHOW = "show";
    static final String CMD_UNSELECTALL = "unselectAll";
    static final String CMD_PUSHDONEINRULEMAKER = "pushDoneInRuleMaker";
    private int _totalSteps = 0;
    private int _step = 0;
    private double _deltax = 0.0;
    private double _deltay = 0.0;
    private boolean _wantReset = false;
    private int _currentImage = 0;
    private boolean _ready = false;
    private Vector _commandQueue = new Vector(10);
    private boolean _dragging = false;
    private ToolView _toolView;
    private Rect _oldRect;
    private ToolSession _session = null;
    private Point _currentPointer;
    private Point _agentPos;
    private Point _toolPos;
    private COM.stagecast.ifc.netscape.application.Image _toolImage = null;
    private AgentImage[] _agentImages = new AgentImage[4];
    private Point _target;
    private Timer _animTimer;
    private int _oldX;
    private int _oldY;
    private long _oldTime;
    
    static class AgentImage
    {
	COM.stagecast.ifc.netscape.application.Image image;
	Point pointer;
	
	AgentImage(COM.stagecast.ifc.netscape.application.Image i, Point p) {
	    image = i;
	    pointer = p;
	}
    }
    
    TutorialAgent() {
	super(0, 0, 32, 32);
	_agentPos = new Point(0, 0);
	_toolPos = new Point(0, 0);
	_agentImages[0]
	    = new AgentImage(Resource.getImage("Agent0"), new Point(1, 62));
	_agentImages[1]
	    = new AgentImage(Resource.getImage("Agent0"), new Point(1, 62));
	_agentImages[2]
	    = new AgentImage(Resource.getImage("Agent0"), new Point(1, 62));
	_agentImages[3]
	    = new AgentImage(Resource.getImage("Agent0"), new Point(1, 62));
	_currentImage = 0;
	_currentPointer = new Point(_agentImages[0].pointer);
	this.setType(0);
	this.setTransparent(true);
	this.contentView().setTransparent(true);
	this.setLayer(400);
	this.setRootView(PlaywriteRoot.getMainRootView());
	this.sizeTo(_agentImages[0].image.width(),
		    _agentImages[0].image.height());
	_oldRect = this.bounds();
	this.setBuffered(true);
    }
    
    public void show() {
	super.show();
	super.mouseDown(new MouseEvent(0L, -1, 0, 0, 0));
    }
    
    public void drawView(Graphics g) {
	if (_toolImage != null)
	    _toolImage.drawAt(g, _toolPos.x, _toolPos.y);
	_agentImages[_currentImage].image.drawAt(g, _agentPos.x, _agentPos.y);
    }
    
    public void destroyWindow() {
	_target = null;
	if (_animTimer != null)
	    _animTimer.stop();
	_animTimer = null;
	if (_commandQueue != null) {
	    synchronized (_commandQueue) {
		_commandQueue.removeAllElements();
		_commandQueue = null;
	    }
	}
	_toolView = null;
	_oldRect = null;
	_session = null;
	_currentPointer = null;
	_agentPos = null;
	_toolPos = null;
	_toolImage = null;
    }
    
    public void reset() {
	_wantReset = true;
	if (_animTimer != null) {
	    _animTimer.stop();
	    _animTimer = null;
	}
	if (_commandQueue != null) {
	    synchronized (_commandQueue) {
		_commandQueue.removeAllElements();
	    }
	}
	clearImage();
	_totalSteps = 0;
	_step = 0;
	_deltax = 0.0;
	_deltay = 0.0;
	_ready = false;
	_dragging = false;
	_toolView = null;
	_session = null;
	if (_currentPointer != null)
	    _currentPointer.moveTo(_agentImages[_currentImage].pointer.x,
				   _agentImages[_currentImage].pointer.y);
	_agentPos.moveTo(0, 0);
	_toolPos.moveTo(0, 0);
	_wantReset = false;
    }
    
    public void executeCommands() {
	_ready = true;
	nextCommand();
    }
    
    public void addCommand(Vector command) {
	_commandQueue.addElement(command);
	if (_ready)
	    nextCommand();
    }
    
    public void moveHotspotTo(int x, int y) {
	this.moveTo(x - _currentPointer.x, y - _currentPointer.y);
    }
    
    public void setHotSpot(int index, Point hotSpot) {
	if (index >= 0 && index < _agentImages.length) {
	    _agentImages[index].pointer = hotSpot;
	    if (index == _currentImage)
		_currentPointer.moveTo(hotSpot.x, hotSpot.y);
	} else
	    throw new RuntimeException
		      ("setHotSpot: index " + index
		       + "is invalid. index should be a number from 0 to "
		       + (_agentImages.length - 1));
    }
    
    public void setImage(int index, String imageName) {
	if (index >= 0 && index < _agentImages.length) {
	    try {
		Image image = null;
		COM.stagecast.ifc.netscape.application.Bitmap bitmap = null;
		StreamProducer sp = null;
		Class baseClass = null;
		baseClass = this.getClass();
		InputStream is = baseClass.getResourceAsStream(imageName);
		if (is != null)
		    is.close();
		else {
		    baseClass = PlaywriteRoot.app().applet().getClass();
		    is = baseClass.getResourceAsStream(imageName);
		    if (is != null)
			is.close();
		    else {
			if (imageName.startsWith("/"))
			    imageName = imageName.substring(1);
			baseClass = null;
			URL url = new URL(PlaywriteRoot.app().applet()
					      .getDocumentBase(),
					  imageName);
			image = PlaywriteSystem.getToolkit().getImage(url);
		    }
		}
		if (baseClass != null) {
		    sp = new ResourceStreamProducer(baseClass, imageName);
		    bitmap = BitmapManager.createNativeBitmapManager(sp);
		} else
		    bitmap = BitmapManager.createBitmapManager(image);
		_agentImages[index].image = bitmap;
	    } catch (OutOfMemoryError e) {
		throw e;
	    } catch (Throwable throwable) {
		throw new RuntimeException("setAgentImage: Image " + imageName
					   + " not found.");
	    }
	} else
	    throw new RuntimeException
		      ("setAgentImage: index " + index
		       + "is invalid. index should be a number from 0 to "
		       + (_agentImages.length - 1));
    }
    
    public void animateTo(Point p) {
	Point target = new Point(p);
	int opt = optimalAppearanceForPoint(target);
	if (opt != _currentImage)
	    changeImage(opt);
	target.moveBy(-_currentPointer.x, -_currentPointer.y);
	double totalDx = (double) (target.x - this.x());
	double totalDy = (double) (target.y - this.y());
	double distance
	    = (double) (int) Math.sqrt(totalDx * totalDx + totalDy * totalDy);
	_totalSteps = (int) (distance * 25.0 / 120.0);
	super.mouseDown(new MouseEvent(0L, -1, 0, 0, 0));
	if (_totalSteps == 0) {
	    smoothMoveTo(target.x, target.y);
	    commandDone();
	} else {
	    _target = target;
	    _deltax = totalDx / (double) _totalSteps;
	    _deltay = totalDy / (double) _totalSteps;
	    _step = 0;
	    _oldTime = System.currentTimeMillis();
	    _animTimer = new Timer(this, "animate", 40);
	    _animTimer.setInitialDelay(40);
	    _animTimer.setCoalesce(false);
	    _animTimer.start();
	}
    }
    
    public void animateTo2(Point p) {
	Point target = new Point(p);
	target.moveBy(-_currentPointer.x, -_currentPointer.y);
	int numberOfSteps = 10;
	int xnow = this.x();
	int ynow = this.y();
	int deltax = (target.x - this.x()) / numberOfSteps;
	int deltay = (target.y - this.y()) / numberOfSteps;
	for (int i = 0; i < numberOfSteps - 1; i++) {
	    xnow += deltax;
	    ynow += deltay;
	    this.moveTo(xnow, ynow);
	}
	this.moveTo(target.x, target.y);
    }
    
    public void animateTo3(Point p) {
	animateTo3(p, 10);
    }
    
    public void animateTo3(Point p, int sps) {
	Point target = new Point(p);
	target.moveBy(-_currentPointer.x, -_currentPointer.y);
	double xnow = (double) this.x();
	double ynow = (double) this.y();
	double totalDx = (double) (target.x - this.x());
	double totalDy = (double) (target.y - this.y());
	double distance = Math.sqrt(totalDx * totalDx + totalDy * totalDy);
	_totalSteps = (int) (distance * (double) sps / 120.0);
	int stepTime = 1000 / sps;
	try {
	    Thread.sleep((long) stepTime);
	} catch (InterruptedException interruptedexception) {
	    /* empty */
	}
	super.mouseDown(new MouseEvent(0L, -1, 0, 0, 0));
	if (_totalSteps > 1) {
	    _deltax = totalDx / (double) _totalSteps;
	    _deltay = totalDy / (double) _totalSteps;
	    for (int i = 0; i < _totalSteps - 1 && !_wantReset; i++) {
		long startTime = System.currentTimeMillis();
		xnow += _deltax;
		ynow += _deltay;
		smoothMoveTo((int) xnow, (int) ynow);
		drawDirtyViews();
		long sleepTime = ((long) stepTime
				  - (System.currentTimeMillis() - startTime));
		if (sleepTime > 10L) {
		    try {
			Thread.sleep(sleepTime);
		    } catch (InterruptedException interruptedexception) {
			/* empty */
		    }
		}
	    }
	}
	smoothMoveTo(target.x, target.y);
    }
    
    private void nextCommand() {
	Vector cmd = null;
	Date date = new Date();
	synchronized (_commandQueue) {
	    if (_commandQueue.size() > 0) {
		_ready = false;
		cmd = (Vector) _commandQueue.removeFirstElement();
		String cmdString = (String) cmd.firstElement();
		Debug.print("debug.tutorial.agent",
			    "executing command: " + cmd);
		if (cmdString.equals("cmd Animate")) {
		    animateTo3((Point) cmd.elementAt(1));
		    commandDone();
		} else if (cmdString.equals("clickOn")) {
		    View v = (View) cmd.elementAt(1);
		    if (v.rootView() != null) {
			Debug.print("debug.tutorial.agent",
				    "processing click on..." + v);
			v.mouseDown(new MouseEvent(date.getTime(), -1, 1, 1,
						   0));
			v.mouseUp(new MouseEvent(date.getTime(), -3, 1, 1, 0));
			ToolView tv = getToolView();
			if (tv != null) {
			    _toolView = tv;
			    tv.hide();
			}
			this.moveToFront();
		    } else
			_commandQueue.removeAllElements();
		    commandDone();
		} else if (cmdString.equals("clickTool")) {
		    Debug.print("debug.tutorial", "clicking with tool..");
		    if (_session != null) {
			CharacterView cv = (CharacterView) cmd.elementAt(1);
			if (cv.getCharacter() != null) {
			    Selection.select((Selectable) cv.getCharacter(),
					     cv.superview());
			    _session.mouseEntered
				(cv,
				 new MouseEvent(date.getTime(), -1, 1, 1, 0));
			    _session.mouseDown(new MouseEvent(date.getTime(),
							      -1, 1, 1, 0));
			    drawDirtyViews();
			} else
			    _commandQueue.removeAllElements();
		    }
		    _session = null;
		    _toolView = null;
		    clearImage();
		    commandDone();
		} else if (cmdString.equals("dragToolTo")) {
		    if (_toolView != null) {
			_session = _toolView.getToolSession();
			_toolView.hide();
			addImage(_toolView.getImage());
			animateTo3((Point) cmd.elementAt(1));
			commandDone();
		    } else {
			_commandQueue.removeAllElements();
			commandDone();
		    }
		} else if (cmdString.equals("dragABChar")) {
		    Point p = (Point) cmd.elementAt(2);
		    boolean succeed
			= moveABChar(Tutorial.getTutorial().getWorldView(),
				     (CharacterInstance) cmd.elementAt(1), p.x,
				     p.y);
		    if (!succeed)
			_commandQueue.removeAllElements();
		    commandDone();
		} else if (cmdString.equals("dragABHandle")) {
		    Point p = (Point) cmd.elementAt(1);
		    moveABHandle(Tutorial.getTutorial().getWorldView(), p.x,
				 p.y);
		    commandDone();
		} else if (cmdString.equals("hide")) {
		    this.hide();
		    commandDone();
		} else if (cmdString.equals("show")) {
		    show();
		    commandDone();
		} else if (cmdString.equals("unselectAll")) {
		    Selection.resetGlobalState();
		    commandDone();
		} else if (cmdString.equals("pushDoneInRuleMaker")) {
		    RuleEditor red = RuleEditor.getRuleEditor();
		    if (red != null) {
			COM.stagecast.ifc.netscape.application.Button b
			    = Tutorial.getTutorial()
				  .getButtonNamed("ruleMakerDone", red);
			if (b != null) {
			    String s = "cmd Animate";
			    Vector v = new Vector(2);
			    v.addElement(s);
			    Point p = b.convertToView(null, b.width() / 2,
						      b.height() / 2);
			    v.addElement(p);
			    _commandQueue.insertElementAt(v, 0);
			    v = new Vector(2);
			    s = "clickOn";
			    v.addElement(s);
			    v.addElement(b);
			    _commandQueue.insertElementAt(v, 1);
			    v = new Vector(1);
			    s = "unselectAll";
			    v.addElement(s);
			    _commandQueue.insertElementAt(v, 2);
			} else
			    Debug.print("debug.tutorial", "button null");
		    } else
			Debug.print("debug.tutorial", "red null" + red);
		    commandDone();
		}
		cmd.removeAllElements();
	    }
	}
    }
    
    private void commandDone() {
	_ready = true;
	if (_commandQueue.size() > 0)
	    nextCommand();
    }
    
    private void addImage(COM.stagecast.ifc.netscape.application.Image img) {
	_toolImage = img;
	_toolPos.x = _agentImages[_currentImage].pointer.x - img.width() / 2;
	_toolPos.y = _agentImages[_currentImage].pointer.y - img.height() / 2;
	Rect toolRect = new Rect(this.x() + _toolPos.x, this.y() + _toolPos.y,
				 img.width(), img.height());
	Rect agentRect = new Rect(this.bounds());
	agentRect.unionWith(toolRect);
	_currentPointer.moveTo(_agentImages[_currentImage].pointer.x,
			       _agentImages[_currentImage].pointer.y);
	if (_toolPos.y < 0) {
	    _currentPointer.y -= _toolPos.y;
	    _agentPos.y -= _toolPos.y;
	    _toolPos.y = 0;
	    agentRect.y -= _toolPos.y;
	}
	if (_toolPos.x < 0) {
	    _currentPointer.x -= _toolPos.x;
	    _agentPos.x -= _toolPos.x;
	    _toolPos.x = 0;
	    agentRect.x -= _toolPos.x;
	}
	this.setBounds(agentRect);
    }
    
    private void changeImage(int imgNumber) {
	_currentImage = imgNumber;
	COM.stagecast.ifc.netscape.application.Image temp = _toolImage;
	clearImage();
	if (temp != null)
	    addImage(temp);
    }
    
    private void clearImage() {
	_currentPointer.moveTo(_agentImages[_currentImage].pointer.x,
			       _agentImages[_currentImage].pointer.y);
	this.moveBy(_agentPos.x, _agentPos.y);
	_agentPos.x = 0;
	_agentPos.y = 0;
	this.sizeTo(_agentImages[_currentImage].image.width(),
		    _agentImages[_currentImage].image.height());
	_toolImage = null;
    }
    
    private int optimalAppearanceForPoint(Point destination) {
	Rect mainRect = PlaywriteRoot.getMainRootView().localBounds();
	Rect appearanceBounds = this.localBounds();
	int optimalApp = _currentImage;
	appearanceBounds.moveTo((destination.x
				 - _agentImages[_currentImage].pointer.x),
				(destination.y
				 - _agentImages[_currentImage].pointer.y));
	int minDifference = isInRect(mainRect, appearanceBounds);
	for (int i = 0; i < _agentImages.length; i++) {
	    appearanceBounds.setBounds(0, 0, _agentImages[i].image.width(),
				       _agentImages[i].image.height());
	    appearanceBounds.moveTo(destination.x - _agentImages[i].pointer.x,
				    destination.y - _agentImages[i].pointer.y);
	    int diff = isInRect(mainRect, appearanceBounds);
	    if (diff < minDifference) {
		optimalApp = i;
		minDifference = diff;
	    }
	}
	return optimalApp;
    }
    
    public void performCommand(String command, Object data) {
	if (command == "animate") {
	    Debug.print("debug.tutorial.agent", "animating. step: ", _step);
	    if (_wantReset) {
		Debug.print("debug.tutorial.agent",
			    "stopping animation want reset.");
		_animTimer.stop();
		_animTimer = null;
		synchronized (_commandQueue) {
		    _commandQueue.removeAllElements();
		}
		commandDone();
	    } else {
		int totalDx = _target.x - this.x();
		int totalDy = _target.y - this.y();
		int distance = totalDx * totalDx + totalDy * totalDy;
		long passed = System.currentTimeMillis() - _oldTime;
		float factor = (float) passed / 40.0F;
		int realDx = (int) (_deltax * (double) factor);
		int realDy = (int) (_deltay * (double) factor);
		_oldTime = System.currentTimeMillis();
		if (distance <= realDx * realDx + realDy * realDy) {
		    smoothMoveTo(_target.x, _target.y);
		    _animTimer.stop();
		    _animTimer = null;
		    commandDone();
		} else {
		    smoothMoveTo(bounds.x + realDx, bounds.y + realDy);
		    _step++;
		}
	    }
	} else if (command == "hide")
	    this.hide();
    }
    
    private void smoothMoveTo(int x, int y) {
	if (!this.isVisible())
	    show();
	super.mouseDragged(new MouseEvent(0L, -1, x - bounds.x, y - bounds.y,
					  0));
    }
    
    private void moveABHandle(View containingView, int x, int y) {
	BoardView abView = getAfterBoardView(containingView);
	AfterBoardHandle abHandle = getAfterBoardHandle(containingView);
	int sqSize = abView.getSquareSize();
	int steps = 10;
	int dx = sqSize * x;
	int dy = sqSize * y;
	int oldWidth = abHandle.width();
	if (x > 0 && dx < steps) {
	    steps = dx / 2;
	    if (steps == 0)
		steps = 1;
	}
	dx /= steps;
	Rect abHandleRect
	    = abHandle.convertRectToView(null, abHandle.localBounds());
	Point startPoint = new Point(abHandleRect.maxX() - 1,
				     abHandleRect.y + abHandleRect.height / 2);
	animateTo3(startPoint);
	Point destPoint = new Point(startPoint);
	destPoint.moveBy(sqSize * x - _currentPointer.x,
			 sqSize * y - _currentPointer.y);
	abHandle.mouseDown(new MouseEvent(new Date().getTime(), -1,
					  abHandle.width() - 1,
					  abHandle.height() / 2, 0));
	Point mousePoint
	    = this.convertToView(abView.superview(), abHandle.width(),
				 abHandle.height() / 2);
	super.mouseDown(new MouseEvent(0L, -1, 0, 0, 0));
	for (int i = 0; i < steps - 1; i++) {
	    smoothMoveTo(this.x() + dx, this.y());
	    abHandle.sizeTo(abHandle.width() + dx, abHandle.height());
	    abHandle.rearrangeHandles();
	    drawDirtyViews();
	}
	smoothMoveTo(destPoint.x, destPoint.y);
	abHandle.mouseUp(new MouseEvent(new Date().getTime(), -3,
					oldWidth + sqSize * x,
					abHandle.height() / 2, 0));
	abHandle.setDirty(true);
	abHandle.superview().addDirtyRect(abHandle.bounds);
	drawDirtyViews();
    }
    
    private boolean moveABChar(View containingView, CharacterInstance ci,
			       int destx, int desty) {
	BoardView abView = getAfterBoardView(containingView);
	CharacterView cv = getABCharView(abView, ci);
	GCAlias gca = (GCAlias) cv.getCharacter();
	World world = abView.getWorld();
	if (ci.getCurrentAppearance() == null)
	    return false;
	Point p = cv.convertToView(null, cv.bounds.width / 2,
				   cv.bounds.height / 2);
	animateTo3(p);
	addImage(ci.getCurrentAppearance().getBitmap());
	gca.setVisibility(false);
	abView.addDirtyChar(cv);
	p = new Point((this.x() + _currentPointer.x
		       + abView.getSquareSize() * destx),
		      (this.y() + _currentPointer.y
		       + abView.getSquareSize() * desty));
	animateTo3(p);
	if (!RuleEditor.isRecordingOrEditing())
	    return false;
	GeneralizedCharacter gch = gca.findOriginal();
	RuleAction action = new MoveAction(gch, destx, desty);
	world.doManualAction(action, gch.getBinding().getCharContainer());
	gca.setVisibility(true);
	clearImage();
	return true;
    }
    
    private void drawDirtyViews() {
	PlaywriteRoot.getMainRootView().drawDirtyViews();
    }
    
    private ToolView getToolView() {
	ToolView result = null;
	Enumeration e
	    = PlaywriteRoot.getMainRootView().internalWindows().elements();
	while (e.hasMoreElements()) {
	    InternalWindow iw = (InternalWindow) e.nextElement();
	    if (iw instanceof ToolView)
		result = (ToolView) iw;
	}
	return result;
    }
    
    private BoardView getAfterBoardView(View parentView) {
	Vector sub = parentView.subviews();
	BoardView result = null;
	Enumeration e = sub.elements();
	while (e.hasMoreElements()) {
	    View v = (View) e.nextElement();
	    if (v instanceof BoardView
		&& ((BoardView) v).getBoard() instanceof AfterBoard)
		return (BoardView) v;
	    if (v.subviews() != null) {
		result = getAfterBoardView(v);
		if (result != null)
		    return result;
	    }
	}
	return result;
    }
    
    private AfterBoardHandle getAfterBoardHandle(View parentView) {
	Vector sub = parentView.subviews();
	AfterBoardHandle result = null;
	Enumeration e = sub.elements();
	while (e.hasMoreElements()) {
	    View v = (View) e.nextElement();
	    if (v instanceof AfterBoardHandle)
		return (AfterBoardHandle) v;
	    if (v.subviews() != null) {
		result = getAfterBoardHandle(v);
		if (result != null)
		    return result;
	    }
	}
	return result;
    }
    
    private CharacterView getABCharView(BoardView abView,
					CharacterInstance ci) {
	Vector sub = abView.subviews();
	Enumeration e = sub.elements();
	while (e.hasMoreElements()) {
	    View v = (View) e.nextElement();
	    if (v instanceof CharacterView
		&& ((CharacterView) v).getCharacter() instanceof GCAlias) {
		CharacterView cv = (CharacterView) v;
		GCAlias gca = (GCAlias) cv.getCharacter();
		GeneralizedCharacter gc = gca.findOriginal();
		if (gc.getBinding() == ci)
		    return cv;
	    }
	}
	return null;
    }
    
    private int isInRect(Rect outerRect, Rect r) {
	if (outerRect.contains(r))
	    return 0;
	int dx = 0;
	int dy = 0;
	if (r.x < outerRect.x)
	    dx = outerRect.x - r.x;
	if (r.y < outerRect.y)
	    dy = outerRect.y - r.y;
	if (r.maxX() > outerRect.maxX())
	    dx = Math.max(dx, r.maxX() - outerRect.maxX());
	if (r.maxY() > outerRect.maxY())
	    dy = Math.max(dy, r.maxY() - outerRect.maxY());
	return dx * dx + dy * dy;
    }
}
