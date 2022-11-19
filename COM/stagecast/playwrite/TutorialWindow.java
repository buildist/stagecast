/* TutorialWindow - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;

import COM.stagecast.ifc.netscape.application.AWTCompatibility;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Event;
import COM.stagecast.ifc.netscape.application.EventFilter;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Range;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.TextView;
import COM.stagecast.ifc.netscape.application.TextViewOwner;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

import com.netclue.browser.AsynHtmlPane;
import com.netclue.browser.ProgressSign;
import com.netclue.html.event.HyperlinkEvent;
import com.netclue.html.event.HyperlinkListener;

class TutorialWindow extends PlaywriteWindow
    implements Debug.Constants, EventFilter, PlaywriteSystem.Properties,
	       ResourceIDs.TutorialWindowIDs, TextViewOwner, Target
{
    private static final int DEFAULT_HEIGHT
	= PlaywriteRoot.getMainRootView().height() / 2;
    private static final int CONTENT_HEIGHT = 168;
    private static final int CONTENT_WIDTH = 584;
    private static final String TUTORIAL_REFRESH = "refresh";
    private static final String RETRY_PAGE = "retry";
    private static final String PAGE_ERROR = "page error";
    private static final String BLANK_PAGE
	= "<html><body>&nbsp;</body></html>";
    public static final String TUTORIAL_COMMAND_FILE_NAME = "tutorial.cmd";
    private static final MessageFormat HTML_PAGE_NAME
	= new MessageFormat("{0}.html");
    public static final int UNDER_WORLD = 0;
    public static final int COVER_WORLD = 1;
    public static final int AUTO = 2;
    private Frame _awtFrame;
    private AsynHtmlPane _html;
    private Label _status;
    private PlaywriteView _pageView;
    private Bitmap _pageImage;
    private COM.stagecast.ifc.netscape.application.Graphics _pageGraphics;
    private Graphics _pageAWTGraphics;
    private PlaywriteTextField _debugInfoLabel = null;
    private PlaywriteButton _nextButton;
    private PlaywriteButton _prevButton;
    private Thread _refreshThread;
    private URL _lastURL;
    private volatile boolean _newImageData = false;
    private volatile int _pageFailed = 0;
    private volatile boolean _modifying = false;
    private int _pageNumber;
    private String _dirName;
    private Tutorial _tutorial;
    private boolean _ignoreMouseEvents = false;
    private boolean _nextEnabled = true;
    private boolean _prevEnabled = true;
    private Vector _commandSet = null;
    
    class ExtendedHtmlPane extends AsynHtmlPane
    {
	public ExtendedHtmlPane(int flags, ProgressSign progress) {
	    super(flags, progress);
	}
	
	public void paint(Graphics g) {
	    if (!_modifying) {
		superPaint(g);
		Toolkit.getDefaultToolkit().sync();
		if (_pageFailed == 0)
		    superPaint(TutorialWindow.this.getPageAWTGraphics());
		_newImageData = true;
	    }
	}
	
	void superPaint(Graphics g) {
	    try {
		super.paint(g);
		_pageFailed = 0;
	    } catch (Exception e) {
		_pageFailed++;
		e.printStackTrace();
	    }
	}
    }
    
    TutorialWindow(Tutorial t, World world, String baseDir) {
	super(0, PlaywriteWindow.getRootView().height() - DEFAULT_HEIGHT,
	      PlaywriteWindow.getRootView().width(), DEFAULT_HEIGHT, world);
	Size s = this.windowSizeForContentSize(584, 168);
	this.sizeTo(s.width, s.height);
	setNewBaseName(baseDir);
	_tutorial = t;
	init();
    }
    
    void init() {
	this.setCanBecomeMain(false);
	this.setResizable(false);
	setPageImage(new Bitmap(584, 168));
	_pageView = new PlaywriteView(getPageImage());
	this.addSubview(_pageView);
	_html = new ExtendedHtmlPane(2, null);
	_html.setSize(584, 168);
	_html.addHyperlinkListener(new HyperlinkListener() {
	    public void hyperlinkTriggered(HyperlinkEvent e) {
		Debug.print(true, "-- link clicked: ", e);
	    }
	});
	_status = new Label();
	_html.setStatusLabel(_status);
	PlaywriteRoot.app();
	Panel panel = PlaywriteRoot.getMainRootView().panel();
	Panel nativeComponent = new Panel();
	nativeComponent.add(_html);
	nativeComponent.setBounds(4000, 4000, 1, 1);
	panel.add(nativeComponent);
	_html.setPageContent("<html><body>&nbsp;</body></html>");
	_refreshThread = new Thread(new Runnable() {
	    public void run() {
		while (_tutorial != null) {
		    try {
			Thread.sleep(500L);
		    } catch (InterruptedException interruptedexception) {
			/* empty */
		    }
		    if (_newImageData) {
			_newImageData = false;
			String cmd = "refresh";
			if (_pageFailed > 0) {
			    if (_pageFailed < 5)
				cmd = "retry";
			    else
				cmd = "page error";
			}
			PlaywriteRoot.app().performCommandAndWait
			    (TutorialWindow.this, cmd, null);
		    }
		}
	    }
	});
	_refreshThread.setDaemon(true);
	_refreshThread.start();
	_nextButton
	    = PlaywriteButton.createButton(Resource.getImage("tutorial n"),
					   null, "tutorial n", this);
	_nextButton.setToolTipText(Resource.getToolTip("tutorial n"));
	_nextButton.moveTo((this.width() - _nextButton.width()
			    - this.border().rightMargin()),
			   (this.height() - _nextButton.height()
			    - this.border().bottomMargin()));
	_nextButton.setHorizResizeInstruction(1);
	_nextButton.setVertResizeInstruction(8);
	_prevButton
	    = PlaywriteButton.createButton(Resource.getImage("tutorial p"),
					   null, "tutorial p", this);
	_prevButton.setToolTipText(Resource.getToolTip("tutorial p"));
	_prevButton.moveTo(this.border().leftMargin(),
			   (this.height() - _prevButton.height()
			    - this.border().bottomMargin()));
	_prevButton.setHorizResizeInstruction(0);
	_prevButton.setVertResizeInstruction(8);
	_nextButton.moveTo((this.width() - _nextButton.width()
			    - this.border().rightMargin()
			    - this.border().leftMargin()),
			   _pageView.height() - _nextButton.height());
	_prevButton.moveTo(0, _pageView.height() - _prevButton.height());
	_pageView.addSubview(_nextButton);
	_pageView.addSubview(_prevButton);
	_prevButton.setEnabled(false);
	if (Debug.lookup("debug.tutorial")) {
	    PlaywriteButton nx = new PlaywriteButton(_nextButton.image()) {
		public void drawView
		    (COM.stagecast.ifc.netscape.application.Graphics g) {
		    g.setColor(Color.black);
		    g.drawRect(0, 0, bounds.width, bounds.height);
		    g.drawRect(1, 1, bounds.width - 1, bounds.height - 1);
		    this.image().drawScaled(g, 2, 2, bounds.width - 4,
					    bounds.height - 4);
		}
	    };
	    nx.setCommand(_nextButton.command());
	    nx.setTarget(_nextButton.target());
	    nx.sizeTo(16, 16);
	    nx.moveTo(_nextButton.bounds.maxX() - nx.width() - 1,
		      _nextButton.bounds.maxY() - nx.height() - 1);
	    nx.setHorizResizeInstruction(1);
	    nx.setVertResizeInstruction(8);
	    _pageView.addSubview(nx);
	    PlaywriteButton pr = new PlaywriteButton(_prevButton.image()) {
		public void drawView
		    (COM.stagecast.ifc.netscape.application.Graphics g) {
		    g.setColor(Color.black);
		    g.drawRect(0, 0, bounds.width, bounds.height);
		    g.drawRect(1, 1, bounds.width - 1, bounds.height - 1);
		    this.image().drawScaled(g, 2, 2, bounds.width - 4,
					    bounds.height - 4);
		}
	    };
	    pr.setCommand(_prevButton.command());
	    pr.setTarget(_prevButton.target());
	    pr.sizeTo(16, 16);
	    pr.moveTo(0, _prevButton.bounds.maxY() - pr.height() - 1);
	    pr.setHorizResizeInstruction(0);
	    pr.setVertResizeInstruction(8);
	    _pageView.addSubview(pr);
	}
	this.setTitle(null);
	this.setCloseable(false);
	TitleBar tb = this.getTitleBar();
	tb.setMinSize(tb.width(), PlaywriteBorder.TOP_BORDER.height());
	tb.sizeTo(tb.width(), PlaywriteBorder.TOP_BORDER.height());
    }
    
    public PlaywriteButton getNextButton() {
	return _nextButton;
    }
    
    public PlaywriteButton getPrevButton() {
	return _prevButton;
    }
    
    public void setNewBaseName(String baseDir) {
	_dirName = baseDir;
	_pageNumber = 1;
	_commandSet = null;
    }
    
    private synchronized void setPageImage(Bitmap image) {
	if (_pageImage != null) {
	    _pageGraphics.dispose();
	    _pageGraphics = null;
	    _pageAWTGraphics = null;
	    _pageImage.flush();
	    _pageImage = null;
	}
	_pageImage = image;
	if (_pageImage != null) {
	    _pageGraphics = _pageImage.createGraphics();
	    _pageAWTGraphics
		= AWTCompatibility.awtGraphicsForGraphics(_pageGraphics);
	}
    }
    
    private synchronized Bitmap getPageImage() {
	return _pageImage;
    }
    
    private synchronized Graphics getPageAWTGraphics() {
	return _pageAWTGraphics;
    }
    
    public void layoutParts() {
	if (_prevButton != null) {
	    _pageView.sizeToMinSize();
	    _nextButton.moveTo((this.width() - _nextButton.width()
				- this.border().rightMargin()
				- this.border().leftMargin()),
			       _pageView.height() - _nextButton.height());
	    _prevButton.moveTo(0, _pageView.height() - _prevButton.height());
	    TitleBar tb = this.getTitleBar();
	    tb.setMinSize(tb.width(), PlaywriteBorder.TOP_BORDER.height());
	    tb.sizeTo(tb.width(), PlaywriteBorder.TOP_BORDER.height());
	}
	super.layoutParts();
    }
    
    public void destroyWindow() {
	_tutorial = null;
	super.destroyWindow();
	setPageImage(null);
	if (_awtFrame != null) {
	    _awtFrame.setVisible(false);
	    _awtFrame.dispose();
	}
	_awtFrame = null;
    }
    
    public boolean playSound(String soundName) {
	try {
	    URL soundUrl = resourceToURL(nameToResource(soundName));
	    SystemSound sound
		= new SystemSound(new URLStreamProducer(soundUrl));
	    if (sound.isPlayable())
		return sound.play();
	} catch (Exception e) {
	    Debug.print("debug.tutorial", e);
	}
	return false;
    }
    
    public void enableNextButton(boolean b) {
	int nextPageNumber = _tutorial.getNextPageNumber();
	if (nextPageNumber == -1)
	    nextPageNumber = _pageNumber + 1;
	if (!b || pageExists(nextPageNumber))
	    _nextButton.setEnabled(b);
	_nextEnabled = b;
    }
    
    public void enablePrevButton(boolean b) {
	int prevPageNumber = _tutorial.getPrevPageNumber();
	if (prevPageNumber == -1)
	    prevPageNumber = _pageNumber - 1;
	if (!b || pageExists(prevPageNumber))
	    _prevButton.setEnabled(b);
	_prevEnabled = b;
    }
    
    public void tutorialResize(int width, int height, Rect worldWindowRect) {
	Size s = this.windowSizeForContentSize(width, height);
	if (s.width != this.width() || s.height != this.height()) {
	    this.hide();
	    this.sizeTo(s.width, s.height);
	    ASSERT.isTrue(PlaywriteRoot.app().inEventThread());
	    try {
		_modifying = true;
		_html.stopLoading();
		_html.setPageContent("<html><body>&nbsp;</body></html>");
		_html.setSize(width, height);
		setPageImage(new Bitmap(width, height));
		_pageView.setImage(getPageImage());
		_pageView.setMinSize(width, height);
		_pageView.sizeToMinSize();
	    } finally {
		_modifying = false;
	    }
	}
    }
    
    public void tutorialMove(int location, Rect worldWindowRect) {
	Size screenSize = PlaywriteRoot.getRootWindowSize();
	int x = worldWindowRect.x + (worldWindowRect.width - this.width()) / 2;
	Point p = new Point(this.x(), this.y());
	int underDisplacement = 0;
	int coverDisplacement = 0;
	if (screenSize.height <= 480) {
	    underDisplacement
		= PlaywriteSystem
		      .getApplicationPropertyAsInt("under_displacement", 10);
	    coverDisplacement
		= PlaywriteSystem
		      .getApplicationPropertyAsInt("cover_displacement", 16);
	}
	if (location == 0)
	    p.moveTo(x, worldWindowRect.maxY() - underDisplacement);
	else if (location == 1)
	    p.moveTo(x, worldWindowRect.y - coverDisplacement);
	else if (location == 2) {
	    if (this.height() < screenSize.height - worldWindowRect.maxY())
		p.moveTo(x, worldWindowRect.maxY());
	    else if (this.height() < screenSize.height - worldWindowRect.y)
		p.moveTo(x, worldWindowRect.y);
	    else
		p.moveTo(0, 0);
	}
	if (p.x != this.x() || p.y != this.y()) {
	    this.hide();
	    this.moveTo(p.x, p.y);
	}
    }
    
    public void goToPage(int p) {
	_pageNumber = p;
	loadTutorialPage();
    }
    
    boolean boundify() {
	return true;
    }
    
    public void loadTutorialPage() {
	loadTutorialPage(_pageNumber);
    }
    
    private void loadTutorialPage(int page) {
	ASSERT.isTrue(PlaywriteRoot.app().inEventThread());
	Tutorial.getTutorial().reset();
	String commands = null;
	String name = pageToResource(page);
	URL url = resourceToURL(name);
	if (url == null) {
	    tutorialError("sorry! file " + name + " not found");
	    Debug.print("debug.tutorial", "tutorial file not found: ", name);
	} else {
	    try {
		Debug.print("debug.tutorial", url.toString());
		commands = getCommands();
		if (commands != null)
		    _tutorial.executeCommands(commands);
		setPage(url);
	    } catch (Exception e) {
		tutorialError("sorry, an error occurred while processing "
			      + url.toString());
		Debug.stackTrace(e);
	    }
	}
	if (_ignoreMouseEvents)
	    Application.application().eventLoop().filterEvents(this);
	this.setDirty(true);
	if (Debug.lookup("debug.tutorial")) {
	    if (_debugInfoLabel == null) {
		_debugInfoLabel = new PlaywriteTextField(0, 0, 40, 40);
		_pageView.addSubview(_debugInfoLabel);
	    }
	    _debugInfoLabel
		.setStringValue(new Integer(_pageNumber).toString());
	    _debugInfoLabel.sizeToMinSize();
	}
	this.show();
	this.moveToFront();
	_tutorial.didLoadPage();
    }
    
    private String getCommands() throws IOException {
	if (_commandSet == null)
	    parseCommandSet();
	if (_pageNumber < 0 || _pageNumber >= _commandSet.size())
	    return null;
	return (String) _commandSet.elementAt(_pageNumber);
    }
    
    private void parseCommandSet() throws IOException {
	StringBuffer nextCommand = new StringBuffer(128);
	_commandSet = new Vector(50);
	URL url = resourceToURL(nameToResource("tutorial.cmd"));
	if (url != null) {
	    InputStream is = url.openStream();
	    BufferedReader r = new BufferedReader(new InputStreamReader(is));
	    boolean hasPage = false;
	    String line = null;
	    while ((line = r.readLine()) != null) {
		line = line.trim();
		if (hasPage) {
		    if (line.length() < 1) {
			_commandSet.addElement(nextCommand.toString());
			hasPage = false;
			nextCommand.setLength(0);
		    } else {
			nextCommand.append(line);
			nextCommand.append("\n");
		    }
		} else if (line.length() >= 1) {
		    int page = Integer.parseInt(line);
		    if (page < _commandSet.size()) {
			Debug.print(true, "Page " + page, " out of order in ",
				    url.toString());
			return;
		    }
		    while (_commandSet.size() < page)
			_commandSet.addElement("");
		    hasPage = true;
		}
	    }
	    if (hasPage)
		_commandSet.addElement(nextCommand.toString());
	}
    }
    
    private void setPage(URL url) {
	_status.setText("");
	try {
	    _modifying = true;
	    _lastURL = url;
	    _html.stopLoading();
	    _status.setText("");
	    _html.setPage(url);
	    _pageFailed = 0;
	} catch (IOException e) {
	    setPage(e.toString());
	    Debug.stackTrace(e);
	} finally {
	    _modifying = false;
	}
    }
    
    private void setPage(String s) {
	try {
	    _modifying = true;
	    _html.stopLoading();
	    _status.setText("");
	    _html.setPageContent(s);
	    _pageFailed = 0;
	} finally {
	    _modifying = false;
	}
    }
    
    void tutorialError(String s) {
	setPage(s);
    }
    
    private int nextPageNumber() {
	int n = Tutorial.getTutorial().getNextPageNumber();
	if (n == -1)
	    n = _pageNumber + 1;
	return n;
    }
    
    private int prevPageNumber() {
	int n = Tutorial.getTutorial().getPrevPageNumber();
	if (n == -1)
	    n = _pageNumber - 1;
	return n;
    }
    
    private boolean pageExists(int pageNumber) {
	return pageToURL(pageNumber) != null;
    }
    
    private URL pageToURL(int pageNumber) {
	return resourceToURL(pageToResource(pageNumber));
    }
    
    private URL resourceToURL(String resource) {
	return PlaywriteRoot.app().getClass().getResource(resource);
    }
    
    private String pageToResource(int pageNumber) {
	return nameToResource(HTML_PAGE_NAME.format
			      (new Object[] { new Integer(pageNumber) }));
    }
    
    private String nameToResource(String name) {
	StringBuffer result = new StringBuffer(80);
	result.append("/");
	result.append("tutorial");
	result.append("/");
	result.append(_dirName);
	result.append("/");
	result.append(name);
	return result.toString();
    }
    
    public void attributesDidChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void attributesWillChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void linkWasSelected(TextView tv, Range r, String url) {
	/* empty */
    }
    
    public void selectionDidChange(TextView tv) {
	/* empty */
    }
    
    public void textDidChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void textEditingDidBegin(TextView tv) {
	/* empty */
    }
    
    public void textEditingDidEnd(TextView tv) {
	/* empty */
    }
    
    public void textWillChange(TextView tv, Range r) {
	/* empty */
    }
    
    public void performCommand(String command, Object arg) {
	if (command == "tutorial n" || command == "tutorial p") {
	    if (command == "tutorial n") {
		_pageNumber = nextPageNumber();
		_ignoreMouseEvents = true;
		loadTutorialPage();
		_ignoreMouseEvents = false;
	    } else if (command == "tutorial p") {
		_pageNumber = prevPageNumber();
		_ignoreMouseEvents = true;
		loadTutorialPage();
		_ignoreMouseEvents = false;
	    }
	    boolean enableButton
		= pageExists(nextPageNumber()) && _nextEnabled;
	    _nextButton.setEnabled(enableButton);
	    enableButton = pageExists(prevPageNumber()) && _prevEnabled;
	    _prevButton.setEnabled(enableButton);
	} else if (command == "refresh")
	    _pageView.setDirty(true);
	else if (command == "retry") {
	    Debug.print(true, "Retrying page");
	    _html.reload();
	} else if (command == "page error")
	    setPage("page error");
	else
	    super.performCommand(command, arg);
    }
    
    public Object filterEvents(Vector events) {
	for (int i = events.size() - 1; i >= 0; i--) {
	    Event event = (Event) events.elementAt(i);
	    if (event instanceof MouseEvent) {
		MouseEvent mouseEvent = (MouseEvent) event;
		if (mouseEvent.type() == -1)
		    events.removeElementAt(i);
	    }
	}
	return null;
    }
}
