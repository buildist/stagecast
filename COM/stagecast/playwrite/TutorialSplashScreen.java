/* TutorialSplashScreen - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.InputStream;
import java.net.URL;

import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.InternalWindow;
import COM.stagecast.ifc.netscape.application.Label;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class TutorialSplashScreen extends InternalWindow
    implements Debug.Constants, ResourceIDs.DialogIDs,
	       ResourceIDs.TutorialSplashScreenIDs, ResourceIDs.SplashScreenIDs
{
    public static final String TUTORIAL_INDEX_FILE_NAME = "tutorial.index";
    public static final String NEXT_SPLASH = "next splash";
    private String _indexFile = "tutorial.index";
    private String _nextFile;
    private Vector _history = new Vector(1);
    private PlaywriteButton _nextPageButton;
    private static int SCREEN_SIZE_MIN_FOR_HORIZONTAL_BUTTONS = 700;
    
    TutorialSplashScreen() {
	this.setType(0);
	COM.stagecast.ifc.netscape.application.RootView rootView
	    = PlaywriteRoot.getMainRootView();
	Size winSize = new Size(rootView.width(), rootView.height());
	this.setResizable(false);
	this.contentView().setBackgroundColor(Color.black);
	this.sizeTo(winSize.width, winSize.height);
	this.setHorizResizeInstruction(2);
	this.setVertResizeInstruction(16);
    }
    
    public void clear() {
	Vector subs = this.contentView().subviews();
	int size = subs.size();
	for (int i = size - 1; i >= 0; i--) {
	    View view = (View) subs.elementAt(i);
	    view.removeFromSuperview();
	    if (view instanceof ViewGlue)
		((ViewGlue) view).discard();
	}
    }
    
    private Vector readIndexFile(String fileName) {
	URL url = this.getClass().getResource("/tutorial/" + fileName);
	if (url == null) {
	    Debug.print("debug.tutorial",
			"index file not found: /tutorial/" + fileName);
	    PlaywriteDialog.warning("dialog cft", true);
	    return null;
	}
	try {
	    InputStream is = url.openStream();
	    if (is != null) {
		TutorialIndexFileParser parser = new TutorialIndexFileParser();
		Vector v = parser.parseInputStream(is);
		showScreen(v, parser);
		return v;
	    }
	} catch (java.io.IOException ioexception) {
	    PlaywriteDialog.warning("dialog cft", true);
	}
	return null;
    }
    
    private void showScreen(Vector labels, TutorialIndexFileParser parser) {
	int x = 0;
	int y = 0;
	PlaywriteView listView = new PlaywriteView(0, y, 100, 100);
	listView.setBackgroundColor(Color.black);
	listView.setHorizResizeInstruction(32);
	listView.setVertResizeInstruction(64);
	int maxWidth = 0;
	int height = 0;
	PlaywriteView header = parser.getHeader();
	if (header != null) {
	    listView.addSubview(header);
	    header.moveTo(x, y);
	    y = header.bounds.maxY() + 12;
	    x += 27;
	    height = y;
	    maxWidth = header.width();
	}
	int s = labels.size();
	int refX = -1;
	for (int i = 0; i < s; i++) {
	    TutorialLessonLabel label
		= (TutorialLessonLabel) labels.elementAt(i);
	    if (label.referenceX() > refX)
		refX = label.referenceX();
	}
	for (int i = 0; i < s; i++) {
	    TutorialLessonLabel label
		= (TutorialLessonLabel) labels.elementAt(i);
	    listView.addSubview(label);
	    label.moveByReferencePoint(refX + x, height);
	    maxWidth = Math.max(maxWidth, label.bounds.maxX());
	    height += label.bounds.height;
	}
	listView.setBounds((this.width() - maxWidth) / 2, 0, maxWidth, height);
	y = height;
	TutorialLessonLabel qsLabel = parser.getQuickStartLabel();
	if (qsLabel != null) {
	    PlaywriteView orView
		= new PlaywriteView(Resource.getImage("tutorial or img"));
	    qsLabel.moveTo(refX + x, height + 40);
	    listView.addSubview(qsLabel);
	    orView.moveTo((qsLabel.bounds.x
			   + (qsLabel.width() - orView.width()) / 2),
			  (height
			   + (qsLabel.y() - height - orView.height()) / 2));
	    listView.addSubview(orView);
	    listView.sizeTo(listView.width(), Math.max(listView.height(),
						       qsLabel.bounds.maxY()));
	}
	String nextPage = parser.getNextPage();
	if (nextPage != null) {
	    _nextFile = nextPage;
	    _nextPageButton
		= new RolloverButton(Resource.getImage("tutorial learn more"),
				     Resource
					 .getImage("tutorial learn more halo"),
				     "next splash", this);
	    int offsetForSmallScreenHack = 0;
	    if (PlaywriteRoot.getMainRootViewBounds().width <= 640)
		offsetForSmallScreenHack = 40;
	    _nextPageButton.moveTo((listView.width() - _nextPageButton.width()
				    - offsetForSmallScreenHack),
				   height);
	    listView.addSubview(_nextPageButton);
	    listView.sizeTo(listView.width(),
			    Math.max(listView.height(),
				     _nextPageButton.bounds.maxY()));
	}
	listView.moveTo((this.width() - listView.width()) / 2,
			(this.height() - listView.height()) / 2);
	this.addSubview(listView);
	this.setDirty(true);
    }
    
    public void openWorld(String fileName) {
	hide();
	String pathName = "";
	String worldName = fileName;
	int lastSlashIndex = fileName.lastIndexOf("/");
	if (lastSlashIndex != -1) {
	    pathName = fileName.substring(0, lastSlashIndex);
	    worldName
		= fileName.substring(lastSlashIndex + 1, fileName.length());
	}
	String fullPath = Tutorial.TUTORIAL_WORLD_DIRECTORY + pathName;
	World w = PlaywriteRoot.app().openWorld(null, fullPath, worldName);
	if (w != null) {
	    Tutorial tutorial = new Tutorial(w);
	}
    }
    
    boolean init() {
	return init(_indexFile);
    }
    
    boolean init(String indexFile) {
	clear();
	Font labelFont = new Font("Serif", 1, 24);
	Vector labels = readIndexFile(indexFile);
	if (labels == null)
	    return false;
	Label label = new Label(Resource.getText("splash q"), labelFont);
	label.setColor(Color.white);
	label.sizeToMinSize();
	ButtonLabel buttonLabel
	    = PlaywriteRoot.app().createQuitButtonLabel("tutorial splash quit",
							label);
	Rect screenBounds = PlaywriteRoot.getMainRootViewBounds();
	if (screenBounds.width < SCREEN_SIZE_MIN_FOR_HORIZONTAL_BUTTONS) {
	    buttonLabel.setPadding(0);
	    buttonLabel.layoutVertically();
	}
	buttonLabel.moveTo(bounds.width - buttonLabel.bounds.width - 10,
			   bounds.height - buttonLabel.bounds.height - 10);
	this.addSubview(buttonLabel);
	COM.stagecast.ifc.netscape.application.Bitmap upImage
	    = Resource.getButtonImage("tutorial splash back");
	COM.stagecast.ifc.netscape.application.Bitmap downImage
	    = Resource.getAltButtonImage("tutorial splash back");
	PlaywriteButton button
	    = new RolloverButton(upImage, downImage, "tutorial splash back",
				 this);
	label = new Label(Resource.getText("splash b"), labelFont);
	label.setColor(Color.white);
	label.sizeToMinSize();
	buttonLabel = new ButtonLabel(button, label, 5);
	if (screenBounds.width < SCREEN_SIZE_MIN_FOR_HORIZONTAL_BUTTONS) {
	    buttonLabel.setPadding(0);
	    buttonLabel.layoutVertically();
	}
	buttonLabel.moveTo(10, this.height() - buttonLabel.height() - 10);
	buttonLabel.setVertResizeInstruction(8);
	this.addSubview(buttonLabel);
	return true;
    }
    
    public void willMoveTo(Point p) {
	p.x = p.y = 0;
	super.willMoveTo(p);
    }
    
    public void hide() {
	super.hide();
	clear();
    }
    
    public void show() {
	Rect mainBounds = PlaywriteRoot.getMainRootView().bounds;
	if (!mainBounds.equals(bounds))
	    this.sizeTo(mainBounds.width, mainBounds.height);
	init(_indexFile);
	super.show();
    }
    
    public void performCommand(String command, Object data) {
	if (command == "tutorial splash back") {
	    PlaywriteRoot.markBusy();
	    if (_history.isEmpty()) {
		this.disableDrawing();
		hide();
		PlaywriteRoot.app().displayOrRemoveSplashScreen();
		this.reenableDrawing();
	    } else {
		_indexFile = (String) _history.removeLastElement();
		_nextFile = null;
		init(_indexFile);
	    }
	    PlaywriteRoot.clearBusy();
	} else if (command.equals("EX"))
	    PlaywriteRoot.app().quit();
	else if ("next splash".equals(command)) {
	    _history.addElement(_indexFile);
	    _indexFile = _nextFile;
	    _nextFile = null;
	    init(_indexFile);
	} else
	    super.performCommand(command, data);
    }
}
