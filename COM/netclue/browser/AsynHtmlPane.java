/* AsynHtmlPane - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.browser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.netclue.html.CLHtmlPane;
import com.netclue.html.event.HyperlinkEvent;
import com.netclue.html.event.HyperlinkListener;
import com.netclue.html.widget.PanView;

public class AsynHtmlPane extends Container implements AdjustmentListener
{
    static String mozillaVersion = "3.0 (compatible; Clue 2.5)";
    static String mozillaAgent = "Mozilla/" + mozillaVersion;
    char[] inbuf = new char[4096];
    int prvMethod;
    URL prvURL;
    String prvPostContent;
    CLHtmlPane htmlText;
    PanView viewPort;
    Scrollbar hbar;
    Scrollbar vbar;
    private Insets margin = new Insets(0, 0, 0, 0);
    static ProgressSign ps;
    static boolean isMSJVM;
    int ieBuggyY;
    WorkerThread loadThread;
    int policy = 0;
    int curPolicy = -1;
    HyperlinkListener hlinkCtrl;
    
    class ScrollLayout implements LayoutManager
    {
	int top;
	int bottom;
	int right;
	int left;
	
	public void addLayoutComponent(String string, Component component) {
	    /* empty */
	}
	
	public void removeLayoutComponent(Component component) {
	    /* empty */
	}
	
	public Dimension preferredLayoutSize(Container container) {
	    int i = 0;
	    int i_0_ = 0;
	    Insets insets = container.getInsets();
	    if (AsynHtmlPane.this.isVisible()) {
		Dimension dimension = htmlText.getPreferredSize();
		i = dimension.width;
		i_0_ = dimension.height;
	    }
	    if (vbar != null && vbar.isVisible()) {
		Dimension dimension = vbar.getSize();
		i += dimension.width;
		i_0_ = Math.max(i_0_, dimension.height);
	    }
	    if (hbar != null && hbar.isVisible()) {
		Dimension dimension = hbar.getSize();
		i = Math.max(i, dimension.width);
		i_0_ += dimension.height;
	    }
	    i += insets.left + insets.right;
	    i_0_ += insets.top + insets.bottom;
	    return new Dimension(i, i_0_);
	}
	
	public Dimension minimumLayoutSize(Container container) {
	    return preferredLayoutSize(container);
	}
	
	public void layoutContainer(Container container) {
	    Insets insets = container.getInsets();
	    Dimension dimension = container.getSize();
	    top = insets.top;
	    bottom = dimension.height - insets.bottom;
	    left = insets.left;
	    right = dimension.width - insets.right;
	    manageScrollbars();
	    if (vbar != null && vbar.isVisible()) {
		Dimension dimension_1_ = vbar.getPreferredSize();
		if (hbar != null && hbar.isVisible()) {
		    Dimension dimension_2_ = hbar.getPreferredSize();
		    hbar.setBounds(left,
				   bottom - dimension_2_.height - ieBuggyY,
				   right - left - dimension_1_.width,
				   dimension_2_.height);
		    bottom -= dimension_2_.height;
		}
		vbar.setBounds(right - dimension_1_.width, top - ieBuggyY,
			       dimension_1_.width, bottom - top);
		right -= dimension_1_.width;
	    } else if (hbar != null && hbar.isVisible()) {
		Dimension dimension_3_ = hbar.getPreferredSize();
		hbar.setBounds(left, bottom - dimension_3_.height - ieBuggyY,
			       right - left, dimension_3_.height);
		bottom -= dimension_3_.height;
	    }
	    if (htmlText.isVisible())
		viewPort.setBounds(left, top, right - left, bottom - top);
	    if (curPolicy != 2)
		setScrollbarValues(right - left, bottom - top);
	}
    }
    
    public AsynHtmlPane() {
	this(0, null);
    }
    
    public AsynHtmlPane(int i, ProgressSign progresssign) {
	htmlText = new CLHtmlPane();
	this.setLayout(new ScrollLayout());
	vbar = new Scrollbar(1);
	vbar.addAdjustmentListener(this);
	hbar = new Scrollbar(0);
	hbar.addAdjustmentListener(this);
	viewPort = new PanView(htmlText);
	this.add("east", vbar);
	this.add("south", hbar);
	this.add(viewPort);
	curPolicy = policy = i;
	if (progresssign != null)
	    ps = progresssign;
	this.setBackground(Color.white);
	installHyperlinkListener();
	this.addComponentListener(new ComponentAdapter() {
	    public void componentResized(ComponentEvent componentevent) {
		htmlText.updateBlockTree();
		if (AsynHtmlPane.isMSJVM && vbar.isVisible()) {
		    Point point = viewPort.getLocationOnScreen();
		    int i_4_ = point.y;
		    point = vbar.getLocationOnScreen();
		    i_4_ = point.y - i_4_;
		    if (ieBuggyY == 0 || i_4_ != 0)
			ieBuggyY = i_4_;
		}
		viewPort.setPanPosition(hbar.getValue(), vbar.getValue());
		AsynHtmlPane.this.validate();
	    }
	});
    }
    
    public void setProgressSign(ProgressSign progresssign) {
	ps = progresssign;
    }
    
    public URL getBaseURL() {
	return prvURL;
    }
    
    void setPolicy(int i) {
	policy = i;
    }
    
    public void reload() {
	if (prvURL != null)
	    updatePage(prvURL.toString(), null);
    }
    
    public void stopLoading() {
	if (loadThread != null) {
	    loadThread.interrupt();
	    loadThread = null;
	}
	if (ps != null)
	    ps.stop();
	updateStatus("Download interrupted!");
    }
    
    private void updatePage(String string, String string_6_) {
	try {
	    URL url = new URL(string);
	    if (string_6_ != null)
		loadPage(url, 0);
	    else
		loadPage(url, 0);
	} catch (Exception exception) {
	    /* empty */
	}
    }
    
    public void setPage(URL url) throws IOException {
	loadPage(url, HyperlinkEvent.GET_METHOD);
    }
    
    public void setPage(URL url, int i) throws IOException {
	loadPage(url, i);
    }
    
    public void setPageContent(String string) {
	htmlText.setPageContent(string);
	this.repaint();
    }
    
    public void setPageContent(String string, URL url, String string_7_) {
	htmlText.setPageContent(string, url, string_7_);
	this.repaint();
    }
    
    protected void loadPage(final URL url, final int method)
	throws IOException {
	if (url == null)
	    updateStatus("Invalid URL!!");
	else {
	    loadThread = new WorkerThread() {
		String type;
		String cookie;
		URL pageURL = url;
		
		public Object construct() {
		    type = null;
		    try {
			Object object = null;
			Object object_8_ = null;
			String string = pageURL.getHost();
			updateStatus("Contacting " + string);
			URLConnection urlconnection = pageURL.openConnection();
			urlconnection.setDoInput(true);
			if (method > 0) {
			    urlconnection.setDoOutput(true);
			    urlconnection.setRequestProperty
				("Content-Type",
				 "application/x-www-form-urlencoded");
			}
			urlconnection.setRequestProperty("User-Agent",
							 (AsynHtmlPane
							  .mozillaAgent));
			if (method > 0) {
			    DataOutputStream dataoutputstream
				= new DataOutputStream(urlconnection
							   .getOutputStream());
			    String string_9_ = htmlText.getPostContent();
			    if (string_9_ == null)
				string_9_ = prvPostContent;
			    else
				prvPostContent = string_9_;
			    dataoutputstream.writeBytes(string_9_);
			    dataoutputstream.flush();
			    dataoutputstream.close();
			} else
			    prvPostContent = null;
			InputStreamReader inputstreamreader
			    = new InputStreamReader(urlconnection
							.getInputStream());
			updateStatus("Connected to " + string);
			StringBuffer stringbuffer = new StringBuffer();
			int i = urlconnection.getContentLength();
			int i_10_;
			while ((i_10_ = inputstreamreader.read(inbuf)) != -1) {
			    stringbuffer.append(inbuf, 0, i_10_);
			    int i_11_ = stringbuffer.length();
			    String string_12_ = "read " + i_11_ + " bytes...";
			    if (i > 0) {
				if (i <= i_11_)
				    break;
				string_12_ += i_11_ * 100 / i + "%";
			    }
			    updateStatus(string_12_);
			}
			type = urlconnection.getHeaderField("Content-type");
			prvURL = pageURL;
			pageURL = urlconnection.getURL();
			prvMethod = method;
			return stringbuffer.toString();
		    } catch (Exception exception) {
			return null;
		    }
		}
		
		public void finished() {
		    String string = (String) value;
		    if (string != null) {
			htmlText.setPageContent(string, pageURL, type);
			if (policy != 2)
			    curPolicy = 0;
			if (viewPort != null) {
			    hbar.setVisible(false);
			    hbar.setValue(0);
			    vbar.setValue(0);
			    viewPort.setPanPosition(0, 0);
			}
			AsynHtmlPane.this.validate();
			AsynHtmlPane.this.repaint();
		    }
		    loadThread = null;
		}
	    };
	    loadThread.start();
	}
    }
    
    public void update(Graphics graphics) {
	/* empty */
    }
    
    public void setStatusLabel(Label label) {
	htmlText.setStatusLabel(label);
    }
    
    public Label getStatusLabel() {
	return htmlText.getStatusLabel();
    }
    
    public void updateStatus(String string) {
	htmlText.updateStatus(string);
    }
    
    public Insets getInsets() {
	return margin;
    }
    
    public Dimension getMinimumSize() {
	return new Dimension(250, 200);
    }
    
    public void setInsets(Insets insets) {
	margin = insets;
    }
    
    public void installHyperlinkListener() {
	if (hlinkCtrl == null)
	    hlinkCtrl = new HyperlinkListener() {
		public void hyperlinkTriggered(HyperlinkEvent hyperlinkevent) {
		    URL url = hyperlinkevent.getURL();
		    int i = hyperlinkevent.getMethod();
		    AsynHtmlPane asynhtmlpane_15_ = AsynHtmlPane.this;
		    do {
			try {
			    if (url.equals(asynhtmlpane_15_.getBaseURL()))
				break;
			    asynhtmlpane_15_.setPage(url, i);
			} catch (Exception exception) {
			    break;
			}
			break;
		    } while (false);
		}
	    };
	htmlText.addHyperlinkListener(hlinkCtrl);
    }
    
    public void uninstallHyperlinkListener() {
	htmlText.removeHyperlinkListener(hlinkCtrl);
    }
    
    public synchronized void addHyperlinkListener
	(HyperlinkListener hyperlinklistener) {
	htmlText.addHyperlinkListener(hyperlinklistener);
    }
    
    public synchronized void removeHyperlinkListener
	(HyperlinkListener hyperlinklistener) {
	htmlText.removeHyperlinkListener(hyperlinklistener);
    }
    
    public void adjustmentValueChanged(AdjustmentEvent adjustmentevent) {
	int i = hbar.getValue();
	int i_17_ = vbar.getValue();
	viewPort.setPanPosition(i, i_17_);
    }
    
    protected void manageScrollbars() {
	if (curPolicy == 2) {
	    hbar.setVisible(false);
	    vbar.setVisible(false);
	} else {
	    Dimension dimension = this.getSize();
	    Dimension dimension_18_ = htmlText.getPreferredSize();
	    manageVerticalScrollBar(dimension, dimension_18_);
	    manageHorizontalScrollBar(dimension, dimension_18_);
	}
    }
    
    protected void manageVerticalScrollBar(Dimension dimension,
					   Dimension dimension_19_) {
	if (hbar.isVisible())
	    dimension.height -= hbar.getSize().height;
	if (dimension_19_.height > dimension.height) {
	    if (!vbar.isVisible())
		vbar.setVisible(true);
	} else if (vbar.isVisible())
	    vbar.setVisible(false);
    }
    
    protected void manageHorizontalScrollBar(Dimension dimension,
					     Dimension dimension_20_) {
	if (vbar.isVisible())
	    dimension.width -= vbar.getSize().width;
	if (dimension_20_.width > dimension.width) {
	    if (!hbar.isVisible())
		hbar.setVisible(true);
	} else if (hbar.isVisible())
	    hbar.setVisible(false);
    }
    
    protected void resetScrollbarValues() {
	if (hbar != null)
	    hbar.setValue(0);
	if (vbar != null)
	    vbar.setValue(0);
    }
    
    protected void setScrollbarValues(int i, int i_21_) {
	Dimension dimension = htmlText.getPreferredSize();
	if (vbar.isVisible())
	    setVerticalScrollbarValue(i_21_, dimension.height);
	if (hbar.isVisible())
	    setHorizontalScrollbarValue(i, dimension.width);
    }
    
    protected void setVerticalScrollbarValue(int i, int i_22_) {
	int i_23_ = isMSJVM ? Math.max(i, i_22_) - i : i_22_;
	synchronized (vbar) {
	    vbar.setValues(vbar.getValue(), i, 0, i_23_);
	    vbar.setUnitIncrement(isMSJVM ? i : 20);
	    vbar.setBlockIncrement(i >> 1);
	}
    }
    
    protected void setHorizontalScrollbarValue(int i, int i_24_) {
	int i_25_ = isMSJVM ? Math.max(i, i_24_) - i : i_24_;
	synchronized (hbar) {
	    hbar.setValues(hbar.getValue(), i, 0, i_25_);
	    hbar.setUnitIncrement(isMSJVM ? i : 20);
	    hbar.setBlockIncrement(i >> 1);
	}
    }
    
    static {
	if (System.getProperty("java.vendor").startsWith("Micros"))
	    isMSJVM = true;
    }
}
