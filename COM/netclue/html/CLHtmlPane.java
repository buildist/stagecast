/* CLHtmlPane - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.netclue.html.block.Block;
import com.netclue.html.block.BlockFactory;
import com.netclue.html.block.RootBlock;
import com.netclue.html.event.EventListenerList;
import com.netclue.html.event.HyperlinkEvent;
import com.netclue.html.event.HyperlinkListener;

public class CLHtmlPane extends Container
{
    static final Insets ZeroInset = new Insets(0, 0, 0, 0);
    private Insets margin = ZeroInset;
    private Label statusLabel;
    private BlockFactory bkFactory;
    private String postContent;
    private HyperlinkListener hlinkCtrl;
    MouseListener linkHandler;
    protected EventListenerList listenerList = new EventListenerList();
    BaseDocument model;
    transient RootBlock rootBlk;
    
    public class LinkController extends MouseAdapter
	implements MouseMotionListener
    {
	public void mouseDragged(MouseEvent mouseevent) {
	    /* empty */
	}
	
	public void mouseExited(MouseEvent mouseevent) {
	    CLHtmlPane.this.setCursor(Cursor.getDefaultCursor());
	}
	
	public void mouseMoved(MouseEvent mouseevent) {
	    if (!isNew()) {
		String string = null;
		Point point = mouseevent.getPoint();
		int i = getDocIndex(point);
		CLHTMLDocument clhtmldocument = (CLHTMLDocument) getDocument();
		Object object = null;
		AbstractElement abstractelement
		    = clhtmldocument
			  .getElementAtIndex(clhtmldocument.getLinkRoot(), i);
		if (abstractelement != null) {
		    String string_0_
			= (String) abstractelement.getLocalAttribute("usemap");
		    if (string_0_ != null) {
			if (string_0_.charAt(0) == '#')
			    string_0_ = string_0_.substring(1);
			MapArea maparea = clhtmldocument.getMapArea(string_0_);
			if (maparea != null) {
			    Rectangle rectangle = getIndexBounds(i);
			    MapArea.AreaEntry areaentry
				= maparea.searchLink(point.x - rectangle.x,
						     point.y - rectangle.y);
			    if (areaentry != null)
				string = areaentry.getReference();
			}
		    } else {
			abstractelement = traceLinkAncestor(abstractelement);
			if (abstractelement != null)
			    string = ((String)
				      abstractelement
					  .getLocalAttribute(HTMLConst.href));
		    }
		}
		if (string != null) {
		    updateStatus(string);
		    CLHtmlPane.this.setCursor(Cursor.getPredefinedCursor(12));
		} else {
		    updateStatus("");
		    CLHtmlPane.this.setCursor(Cursor.getDefaultCursor());
		}
	    }
	}
	
	public void mouseClicked(MouseEvent mouseevent) {
	    int i = getDocIndex(mouseevent.getPoint());
	    if (i >= 0)
		activateLink(mouseevent.getX(), mouseevent.getY(), i);
	}
	
	protected void activateLink(int i, int i_1_, int i_2_) {
	    BaseDocument basedocument = getDocument();
	    if (basedocument instanceof CLHTMLDocument) {
		CLHTMLDocument clhtmldocument = (CLHTMLDocument) basedocument;
		AbstractElement abstractelement
		    = clhtmldocument.getElementAtIndex(clhtmldocument
							   .getLinkRoot(),
						       i_2_);
		if (abstractelement != null) {
		    boolean bool
			= abstractelement.getLocalAttribute("ismap") != null;
		    MapArea maparea = null;
		    String string
			= (String) abstractelement.getLocalAttribute("usemap");
		    if (string != null) {
			if (string.charAt(0) == '#')
			    string = string.substring(1);
			maparea = clhtmldocument.getMapArea(string);
		    }
		    String string_4_;
		    String string_3_ = string_4_ = null;
		    if (maparea != null) {
			Rectangle rectangle = getIndexBounds(i_2_);
			MapArea.AreaEntry areaentry
			    = maparea.searchLink(i - rectangle.x,
						 i_1_ - rectangle.y);
			if (areaentry != null) {
			    string_3_ = areaentry.getReference();
			    string_4_ = areaentry.getTarget();
			}
		    } else {
			abstractelement = traceLinkAncestor(abstractelement);
			if (abstractelement != null) {
			    string_3_
				= ((String)
				   abstractelement
				       .getLocalAttribute(HTMLConst.href));
			    string_4_
				= (String) abstractelement
					       .getLocalAttribute("target");
			}
		    }
		    if (string_3_ != null) {
			if (bool)
			    string_3_ += "?" + i + "," + i_1_;
			triggerHyperlinkEvent(string_3_, string_4_);
		    }
		}
	    }
	}
	
	AbstractElement traceLinkAncestor(AbstractElement abstractelement) {
	    Object object = null;
	    AbstractElement abstractelement_5_;
	    for (abstractelement_5_ = abstractelement;
		 abstractelement_5_ != null;
		 abstractelement_5_ = abstractelement_5_.getParentElement()) {
		if (abstractelement_5_.getTagCode() == 130)
		    return abstractelement_5_;
	    }
	    return abstractelement_5_;
	}
    }
    
    public CLHtmlPane() {
	linkHandler = new LinkController();
	this.addMouseListener(linkHandler);
	this.addMouseMotionListener((MouseMotionListener) linkHandler);
	rootBlk = new RootBlock(this);
	setPageContent("<body>&nbsp;</body>");
	this.addComponentListener(new ComponentAdapter() {
	    public void componentResized(ComponentEvent componentevent) {
		Dimension dimension = CLHtmlPane.this.getSize();
		rootBlk.setSize(dimension.width, dimension.height);
	    }
	});
    }
    
    public CLHtmlPane(URL url) throws IOException {
	this();
	setPage(url);
    }
    
    public CLHtmlPane(String string) throws IOException {
	this();
	setPage(string);
    }
    
    protected void setPostContent(String string) {
	postContent = string;
    }
    
    public String getPostContent() {
	return postContent;
    }
    
    public BaseDocument createDefaultDocument() {
	return new CLHTMLDocument();
    }
    
    public void setPageContent(String string) {
	try {
	    setPageContent(string, null, "text/html");
	} catch (Exception exception) {
	    /* empty */
	}
    }
    
    public void setPageContent(String string, URL url, String string_7_) {
	HTMLParser htmlparser = new HTMLParser();
	setPageContent(string, url, string_7_, htmlparser);
    }
    
    public void setPageContent(String string, URL url, String string_8_,
			       HTMLParser htmlparser) {
	BaseDocument basedocument = model;
	BaseDocument basedocument_9_ = createDefaultDocument();
	if (statusLabel != null)
	    basedocument_9_.putProperty("StatusLabel", statusLabel);
	basedocument_9_.putProperty("base", url);
	if (basedocument_9_ instanceof CLHTMLDocument) {
	    CLHTMLDocument clhtmldocument = (CLHTMLDocument) basedocument_9_;
	    try {
		model = basedocument_9_;
		GenericElement genericelement
		    = htmlparser.parse(clhtmldocument, string);
		clhtmldocument.setContent(genericelement);
		clhtmldocument.setTextPane(this);
		if (basedocument != null)
		    ((CLHTMLDocument) basedocument).clear();
		updateBlockTree();
	    } catch (Exception exception) {
		model = basedocument;
	    }
	}
    }
    
    public void setPage(String string) throws IOException {
	if (string == null)
	    throw new IOException("invalid url");
	URL url = new URL(string);
	setPage(url);
    }
    
    public void setPage(URL url) throws IOException {
	if (url == null)
	    throw new IOException("invalid url");
	String string = url.toString();
	string.lastIndexOf('.');
	InputStream inputstream = url.openStream();
	URLConnection urlconnection = url.openConnection();
	String string_10_ = urlconnection.getContentType();
	InputStreamReader inputstreamreader
	    = new InputStreamReader(inputstream);
	BufferedReader bufferedreader
	    = new BufferedReader(inputstreamreader, 4096);
	char[] cs = new char[4096];
	StringBuffer stringbuffer = new StringBuffer();
	int i;
	while ((i = bufferedreader.read(cs, 0, 4096)) != -1)
	    stringbuffer.append(cs, 0, i);
	setPageContent(stringbuffer.toString(), url, string_10_);
    }
    
    public URL getPage() {
	return (URL) model.getProperty("base");
    }
    
    public BaseDocument getDocument() {
	return model;
    }
    
    public void updateBlockTree() {
	if (rootBlk != null && model != null) {
	    this.removeAll();
	    GenericElement genericelement = model.getBodyElement();
	    if (bkFactory == null)
		bkFactory = new BlockFactory();
	    BlockFactory blockfactory = bkFactory;
	    setBodyBlock(blockfactory.create(genericelement));
	    System.gc();
	}
    }
    
    protected void setBodyBlock(Block block) {
	rootBlk.setBlock(block);
    }
    
    public Insets getInsets() {
	return margin;
    }
    
    public void setInsets(Insets insets) {
	margin = insets;
    }
    
    public Dimension getPreferredSize() {
	Dimension dimension = new Dimension(rootBlk.getPreferredSize(0),
					    rootBlk.getPreferredSize(1));
	return dimension;
    }
    
    public Rectangle getIndexBounds(int i) {
	Rectangle rectangle = this.getBounds();
	rectangle.x = rectangle.y = 0;
	return rootBlk.findBounds(i, rectangle);
    }
    
    public int getDocIndex(Point point) {
	Rectangle rectangle = this.getBounds();
	rectangle.x = rectangle.y = 0;
	return rootBlk.getDocIndex(point.x, point.y, rectangle);
    }
    
    public void processEvent(AWTEvent awtevent) {
	super.processEvent(awtevent);
    }
    
    public void installHyperlinkListener() {
	hlinkCtrl = new HyperlinkListener() {
	    public void hyperlinkTriggered(HyperlinkEvent hyperlinkevent) {
		URL url = hyperlinkevent.getURL();
		try {
		    setPage(url);
		} catch (Exception exception) {
		    /* empty */
		}
	    }
	};
	addHyperlinkListener(hlinkCtrl);
    }
    
    public void uninstallHyperlinkListener() {
	removeHyperlinkListener(hlinkCtrl);
    }
    
    public synchronized void addHyperlinkListener
	(HyperlinkListener hyperlinklistener) {
	listenerList.add(HyperlinkListener.class, hyperlinklistener);
    }
    
    public synchronized void removeHyperlinkListener
	(HyperlinkListener hyperlinklistener) {
	listenerList.remove(HyperlinkListener.class, hyperlinklistener);
    }
    
    public void fireHyperlinkEvent(HyperlinkEvent hyperlinkevent) {
	Object[] objects = listenerList.getListenerList();
	for (int i = objects.length - 2; i >= 0; i -= 2) {
	    if (objects[i] == HyperlinkListener.class)
		((HyperlinkListener) objects[i + 1])
		    .hyperlinkTriggered(hyperlinkevent);
	}
    }
    
    public BlockFactory getBlockFactory() {
	return bkFactory;
    }
    
    public void setStatusLabel(Label label) {
	statusLabel = label;
    }
    
    public Label getStatusLabel() {
	return statusLabel;
    }
    
    public void updateStatus(String string) {
	if (statusLabel != null)
	    statusLabel.setText(string);
    }
    
    public void update(Graphics graphics) {
	/* empty */
    }
    
    void triggerHyperlinkEvent(String string, String string_12_) {
	try {
	    String string_13_ = null;
	    if (string.lastIndexOf('#') != -1) {
		int i = string.lastIndexOf('#');
		string_13_ = string.substring(i + 1);
		string = string.substring(0, i);
	    }
	    URL url = new URL(getPage(), string);
	    HyperlinkEvent hyperlinkevent
		= new HyperlinkEvent(this, url, HyperlinkEvent.GET_METHOD);
	    if (string_12_ != null)
		hyperlinkevent.setTarget(string_12_);
	    if (string_13_ != null)
		hyperlinkevent.setAnchor(string_13_);
	    fireHyperlinkEvent(hyperlinkevent);
	} catch (java.net.MalformedURLException malformedurlexception) {
	    /* empty */
	}
    }
    
    public void paint(Graphics graphics) {
	Rectangle rectangle = this.getBounds();
	rectangle.x = rectangle.y = 0;
	rootBlk.paint(graphics, rectangle);
	super.paint(graphics);
    }
    
    boolean isNew() {
	return rootBlk.isNew();
    }
}
