/* CLHTMLDocument - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import com.netclue.html.event.HyperlinkEvent;
import com.netclue.html.util.HTMLUtilities;
import com.netclue.html.widget.CButton;
import com.netclue.html.widget.CCheckBox;
import com.netclue.html.widget.CSelect;
import com.netclue.html.widget.CTextField;
import com.netclue.html.widget.Widget;

public class CLHTMLDocument extends BaseDocument
{
    StyleFactory styles = new StyleFactory();
    Vector formList;
    Hashtable fCtrlMap = new Hashtable(8);
    Hashtable imMapHash = new Hashtable(8);
    private GenericElement rootElement;
    private GenericElement headElem;
    private GenericElement bodyElem;
    private GenericElement linkRoot;
    CLHtmlPane htmlPane;
    
    public CLHTMLDocument() {
	StyleAttributes styleattributes = new StyleAttributes();
	styleattributes.addAttribute(StyleFactory.Foreground, Color.blue);
	styles.setNamedStyle("linked", styleattributes);
    }
    
    protected void clear() {
	formList = null;
	fCtrlMap.clear();
    }
    
    public StyleFactory getStyleFactory() {
	return styles;
    }
    
    public void setTitle(String string) {
	this.putProperty("title", string);
    }
    
    public String getTitle() {
	return (String) this.getProperty("title");
    }
    
    public AbstractElement getDefaultRootElement() {
	return rootElement;
    }
    
    public GenericElement getBodyElement() {
	return bodyElem;
    }
    
    public GenericElement getLinkRoot() {
	return linkRoot;
    }
    
    public void setContent(GenericElement genericelement) {
	headElem = bodyElem = null;
	reOrgDocTree(genericelement);
	if (headElem != null)
	    processHeader();
	processBody();
    }
    
    void reOrgDocTree(GenericElement genericelement) {
	rootElement = new GenericElement(1, null, null, 0);
	rootElement.setDocument(this);
	int i = genericelement.getElementCount();
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    AbstractElement abstractelement = genericelement.getElement(i_0_);
	    int i_1_ = abstractelement.getTagCode();
	    if (i_1_ == HTMLTagBag.headID) {
		if (headElem == null) {
		    headElem = (GenericElement) abstractelement;
		    headElem.setParent(rootElement);
		    rootElement.appendElement(headElem);
		}
	    } else if (i_1_ == HTMLTagBag.bodyID) {
		if (bodyElem == null) {
		    bodyElem = (GenericElement) abstractelement;
		    bodyElem.setParent(rootElement);
		    rootElement.appendElement(bodyElem);
		} else {
		    bodyElem
			.setAttributeNode(abstractelement.getAttributeNode());
		    AbstractElement[] abstractelements
			= (new AbstractElement
			   [abstractelement.getElementCount()]);
		    System.arraycopy((((GenericElement) abstractelement)
				      .children),
				     0, abstractelements, 0,
				     abstractelements.length);
		    bodyElem.replace(bodyElem.getElementCount(), 0,
				     abstractelements);
		    abstractelement.setParent(null);
		}
	    } else if (i_1_ < HTMLTagBag.bodyID) {
		if (headElem == null) {
		    headElem
			= new GenericElement(HTMLTagBag.headID, rootElement,
					     null,
					     abstractelement.getStartIndex());
		    rootElement.appendElement(headElem);
		}
		abstractelement.setParent(headElem);
		headElem.appendElement(abstractelement);
	    } else {
		if (bodyElem == null) {
		    bodyElem
			= new GenericElement(HTMLTagBag.bodyID, rootElement,
					     null,
					     abstractelement.getStartIndex());
		    rootElement.appendElement(bodyElem);
		}
		abstractelement.setParent(bodyElem);
		bodyElem.appendElement(abstractelement);
	    }
	}
	if (bodyElem == null) {
	    bodyElem
		= new GenericElement(HTMLTagBag.bodyID, rootElement, null, 0);
	    rootElement.appendElement(bodyElem);
	}
    }
    
    void processHeader() {
	int i = headElem.getElementCount();
	for (int i_2_ = 0; i_2_ < i; i_2_++) {
	    AbstractElement abstractelement = headElem.getElement(i_2_);
	    int i_3_ = abstractelement.getTagCode();
	    if (i_3_ == 8) {
		String string
		    = (String) abstractelement.getLocalAttribute("href");
		if (string != null) {
		    try {
			this.putProperty("base", new URL(string));
		    } catch (Exception exception) {
			/* empty */
		    }
		}
	    }
	}
    }
    
    void processBody() {
	StyleAttributes styleattributes = styles.getStyleByName("_clBase");
	String string = (String) bodyElem.getLocalAttribute("link");
	if (string != null)
	    styleattributes.addAttribute("link", string);
	string = (String) bodyElem.getLocalAttribute("text");
	if (string != null)
	    styleattributes.addAttribute("color", string);
	int i = 0;
	String string_5_;
	String string_4_ = string_5_ = null;
	linkRoot = new GenericElement(0, null, null, 0);
	Stack stack = new Stack();
	stack.push(bodyElem);
	while (!stack.empty()) {
	    AbstractElement abstractelement = (AbstractElement) stack.pop();
	    int i_6_ = abstractelement.getTagCode();
	    switch (i_6_) {
	    case 11:
	    case 12:
	    case 13:
	    case 14:
	    case 15:
	    case 16:
		i = 2;
		string_5_ = String.valueOf(18 - i_6_);
		abstractelement.setAttribute(StyleFactory.SpaceAbove,
					     new Float(20.0F));
		abstractelement.setAttribute(StyleFactory.SpaceBelow,
					     new Float(20.0F));
		break;
	    case 33:
		abstractelement.setAttribute(StyleFactory.SpaceAbove,
					     new Float(20.0F));
		abstractelement.setAttribute(StyleFactory.LeftIndent,
					     new Float(20.0F));
		abstractelement.setAttribute(StyleFactory.RightIndent,
					     new Float(20.0F));
		break;
	    case 34:
		string_4_ = "courier";
		break;
	    case 120:
		createFormInput(abstractelement);
		continue;
	    case 121:
	    case 122: {
		GenericElement genericelement
		    = ((GenericElement)
		       abstractelement.getLocalAttribute(HTMLConst.fmCtrl));
		if (genericelement != null) {
		    genericelement.appendElement(abstractelement);
		    associateFormCtrls(genericelement, abstractelement);
		}
		if (i_6_ == 121)
		    createSelect(abstractelement);
		else
		    createTextArea(abstractelement);
		continue;
	    }
	    case 130:
		if (abstractelement.getLocalAttribute(HTMLConst.href) != null)
		    linkRoot.appendElement(abstractelement);
		break;
	    case 131:
		if (abstractelement.getParentElement().getTagCode() != 130) {
		    String string_7_
			= (String) abstractelement.getLocalAttribute("usemap");
		    if (string_7_ != null)
			createProxyLink(string_7_, abstractelement);
		}
		continue;
	    case 140:
		createMap((GenericElement) abstractelement);
		continue;
	    }
	    int i_8_ = abstractelement.getElementCount() - 1;
	    for (int i_9_ = i_8_; i_9_ >= 0; i_9_--) {
		AbstractElement abstractelement_10_
		    = abstractelement.getElement(i_9_);
		i_6_ = abstractelement_10_.getTagCode();
		if ((i_6_ == HTMLTagBag.trID || i_6_ == HTMLTagBag.tableID
		     || i_6_ >= HTMLTagBag.parID && i_6_ <= HTMLTagBag.cenID)
		    && abstractelement_10_.getElementCount() == 0)
		    ((GenericElement) abstractelement).removeElementAt(i_9_);
		else {
		    if (i_6_ == HTMLTagBag.textID) {
			if (i != 0) {
			    Integer integer
				= ((Integer)
				   (abstractelement_10_.getLocalAttribute
				    (StyleFactory.FontProperty)));
			    int i_11_
				= integer == null ? i : integer.intValue() | i;
			    abstractelement_10_.setAttribute
				(StyleFactory.FontProperty,
				 new Integer(i_11_));
			}
			if (string_5_ != null
			    && (abstractelement_10_.getLocalAttribute("size")
				== null))
			    abstractelement_10_.setAttribute("size",
							     string_5_);
			if (string_4_ != null
			    && (abstractelement_10_.getLocalAttribute("face")
				== null))
			    abstractelement_10_.setAttribute("face",
							     string_4_);
		    } else if (i_9_ > 0 && i_6_ >= HTMLTagBag.parID
			       && i_6_ <= HTMLTagBag.addrID)
			abstractelement_10_.setAttribute((StyleFactory
							  .SpaceAbove),
							 new Float(20.0F));
		    stack.push(abstractelement_10_);
		}
	    }
	    i = 0;
	    string_5_ = string_4_ = null;
	}
    }
    
    void createProxyLink(String string, AbstractElement abstractelement) {
	int i = abstractelement.getStartIndex();
	GenericElement genericelement
	    = new GenericElement(130, linkRoot, null, i);
	genericelement.setAttribute("usemap", string);
	genericelement.appendElement(abstractelement);
	linkRoot.appendElement(genericelement);
    }
    
    void createTextArea(AbstractElement abstractelement) {
	int i = abstractelement.getElementCount();
	StringBuffer stringbuffer = new StringBuffer();
	for (int i_12_ = 0; i_12_ < i; i_12_++) {
	    AbstractElement abstractelement_13_
		= abstractelement.getElement(i_12_);
	    int i_14_ = abstractelement_13_.getTagCode();
	    if (i_14_ == HTMLTagBag.textID)
		stringbuffer
		    .append(((TermElement) abstractelement_13_).getContent());
	    else if (i_12_ != 0 && i_14_ == HTMLTagBag.brID)
		stringbuffer.append("<br>");
	}
	String string = stringbuffer.toString();
	int i_15_ = 2;
	int i_16_ = 20;
	try {
	    String string_17_
		= (String) abstractelement.getLocalAttribute("rows");
	    i_15_ = Integer.parseInt(string_17_);
	    string_17_ = (String) abstractelement.getLocalAttribute("cols");
	    i_16_ = Integer.parseInt(string_17_);
	} catch (Exception exception) {
	    /* empty */
	}
	TextArea textarea = new TextArea(string, i_15_, i_16_, 1);
	abstractelement.setAttribute(StyleFactory.ComponentAttribute,
				     textarea);
    }
    
    void createSelect(AbstractElement abstractelement) {
	CSelect cselect = new CSelect();
	int i = abstractelement.getElementCount();
	int i_18_ = 0;
	int i_19_ = 0;
	for (int i_20_ = 0; i_20_ < i; i_20_++) {
	    AbstractElement abstractelement_21_
		= abstractelement.getElement(i_20_);
	    if (abstractelement_21_ instanceof GenericElement) {
		GenericElement genericelement
		    = (GenericElement) abstractelement_21_;
		int i_22_ = genericelement.getElementCount();
		StringBuffer stringbuffer = new StringBuffer();
		for (int i_23_ = 0; i_23_ < i_22_; i_23_++) {
		    AbstractElement abstractelement_24_
			= genericelement.getElement(i_23_);
		    if (abstractelement_24_.getTagCode() == 150)
			stringbuffer.append(((TermElement) abstractelement_24_)
						.getContent());
		}
		cselect.addItem(((String)
				 genericelement.getLocalAttribute("value")),
				stringbuffer.toString());
		if (genericelement.getLocalAttribute("selected") != null)
		    i_19_ = i_18_;
		i_18_++;
	    }
	}
	cselect.select(i_19_);
	abstractelement.setAttribute(StyleFactory.ComponentAttribute, cselect);
	HTMLForm htmlform = lookupForm(abstractelement);
	if (htmlform != null) {
	    String string
		= (String) abstractelement.getLocalAttribute(HTMLConst.name);
	    String string_25_ = cselect.getSelectedItem();
	    htmlform.addControl(cselect, string, string_25_, "select");
	}
    }
    
    void createFormInput(AbstractElement abstractelement) {
	String string
	    = (String) abstractelement.getLocalAttribute(HTMLConst.type);
	if (string == null)
	    string = "text";
	else
	    string = string.toLowerCase().intern();
	Widget widget = null;
	String string_26_
	    = (String) abstractelement.getLocalAttribute(HTMLConst.name);
	String string_27_
	    = (String) abstractelement.getLocalAttribute(HTMLConst.value);
	if (string == "submit" || string == "button" || string == "reset") {
	    if (string_27_ == null)
		string_27_ = string == "reset" ? "Reset" : "Submit";
	    widget = new CButton(string_27_);
	    widget.setBackground(Color.gray);
	} else if (string.equals("text") || string.equals("password")) {
	    int i = 20;
	    String string_28_
		= (String) abstractelement.getLocalAttribute(HTMLConst.size);
	    if (string_28_ != null)
		i = HTMLUtilities.stringToInt(string_28_);
	    widget = new CTextField(string_27_, i);
	} else if (string.equals("image")) {
	    String string_29_
		= (String) abstractelement.getLocalAttribute(HTMLConst.src);
	    URL url = (URL) this.getProperty("base");
	    try {
		URL url_30_ = new URL(url, string_29_);
		widget = new CButton(url_30_);
		widget.setActionCommand(string_26_);
		widget.setBorder(false);
	    } catch (Exception exception) {
		widget = null;
	    }
	} else if (string.equals("checkbox")) {
	    boolean bool
		= abstractelement.getLocalAttribute("checked") != null;
	    widget = new CCheckBox(null, bool);
	} else if (string.equals("radio")) {
	    boolean bool
		= abstractelement.getLocalAttribute("checked") != null;
	    widget = new CCheckBox(null, bool);
	}
	if (widget != null)
	    abstractelement.setAttribute(StyleFactory.ComponentAttribute,
					 widget);
	HTMLForm htmlform = lookupForm(abstractelement);
	if (htmlform != null)
	    htmlform.addControl(widget, string_26_, string_27_, string);
    }
    
    public HTMLForm lookupForm(AbstractElement abstractelement) {
	HTMLForm htmlform = null;
	GenericElement genericelement
	    = (GenericElement) fCtrlMap.get(abstractelement);
	if (genericelement != null)
	    htmlform = ((HTMLForm)
			genericelement.getLocalAttribute(HTMLConst.fmCtrl));
	return htmlform;
    }
    
    public GenericElement lookupFormComponent
	(AbstractElement abstractelement) {
	return (GenericElement) fCtrlMap.get(abstractelement);
    }
    
    void createMap(GenericElement genericelement) {
	String string
	    = (String) genericelement.getLocalAttribute(HTMLConst.name);
	if (string != null) {
	    int i = genericelement.getElementCount();
	    MapArea maparea = new MapArea(i);
	    for (int i_31_ = 0; i_31_ < i; i_31_++) {
		AbstractElement abstractelement
		    = genericelement.getElement(i_31_);
		if (abstractelement.getTagCode() == 141) {
		    String string_32_
			= (String) abstractelement.getLocalAttribute("coords");
		    String string_33_
			= ((String)
			   abstractelement.getLocalAttribute(HTMLConst.href));
		    String string_34_
			= (String) abstractelement.getLocalAttribute("target");
		    String string_35_
			= (String) abstractelement.getLocalAttribute("shape");
		    xlateAreaCoord(maparea, string_33_, string_34_, string_32_,
				   string_35_);
		}
	    }
	    imMapHash.put(string, maparea);
	}
    }
    
    boolean xlateAreaCoord(MapArea maparea, String string, String string_36_,
			   String string_37_, String string_38_) {
	boolean bool = false;
	if (string_37_ != null) {
	    StringTokenizer stringtokenizer
		= new StringTokenizer(string_37_, ", ");
	    int i = stringtokenizer.countTokens();
	    int[] is = new int[i];
	    int i_39_ = 0;
	    while (stringtokenizer.hasMoreTokens()) {
		String string_40_ = stringtokenizer.nextToken();
		try {
		    is[i_39_] = Integer.parseInt(string_40_);
		    i_39_++;
		} catch (NumberFormatException numberformatexception) {
		    /* empty */
		}
	    }
	    i = i_39_;
	    string_38_
		= string_38_ == null ? "rect" : string_38_.toLowerCase();
	    Shape shape = null;
	    if (string_38_.equalsIgnoreCase("rect"))
		shape = new Rectangle(is[0], is[1], is[2] - is[0],
				      is[3] - is[1]);
	    else if (string_38_.equalsIgnoreCase("circle"))
		shape = new Ellipse(is[0], is[1], is[2]);
	    else if (string_38_.equalsIgnoreCase("poly")) {
		Polygon polygon = new Polygon();
		i = (i & 0x1) != 0 ? i - 1 : i;
		i_39_ = 0;
		while (i_39_ < i)
		    polygon.addPoint(is[i_39_++], is[i_39_++]);
		shape = polygon;
	    }
	    if (shape != null) {
		maparea.addEntry(string, string_36_, shape);
		bool = true;
	    }
	}
	return bool;
    }
    
    HTMLForm processForm(GenericElement genericelement) {
	HTMLForm htmlform = null;
	String string = (String) genericelement.getLocalAttribute("action");
	String string_41_
	    = (String) genericelement.getLocalAttribute("method");
	int i = HyperlinkEvent.GET_METHOD;
	if (string_41_ != null) {
	    string_41_ = string_41_.toLowerCase();
	    if (string_41_.startsWith("post"))
		i = HyperlinkEvent.POST_METHOD;
	}
	URL url = (URL) this.getProperty("base");
	try {
	    URL url_42_ = new URL(url, string);
	    htmlform = new HTMLForm(url_42_.toString(), i);
	} catch (Exception exception) {
	    /* empty */
	}
	return htmlform;
    }
    
    protected void addHTMLForm(GenericElement genericelement) {
	if (formList == null)
	    formList = new Vector(4);
	formList.addElement(genericelement);
	HTMLForm htmlform = processForm(genericelement);
	if (htmlform != null)
	    genericelement.setAttribute(HTMLConst.fmCtrl, htmlform);
    }
    
    protected void associateFormCtrls(GenericElement genericelement,
				      AbstractElement abstractelement) {
	fCtrlMap.put(abstractelement, genericelement);
    }
    
    public MapArea getMapArea(String string) {
	return (MapArea) imMapHash.get(string);
    }
    
    public void setTextPane(CLHtmlPane clhtmlpane) {
	htmlPane = clhtmlpane;
	htmlPane.processEvent(new ActionEvent(this, 1001, "done"));
	if (formList != null) {
	    Enumeration enumeration = formList.elements();
	    while (enumeration.hasMoreElements()) {
		GenericElement genericelement
		    = (GenericElement) enumeration.nextElement();
		HTMLForm htmlform
		    = ((HTMLForm)
		       genericelement.getLocalAttribute(HTMLConst.fmCtrl));
		if (htmlform != null)
		    htmlform.setTextPane(clhtmlpane);
	    }
	}
    }
    
    public void updateStatus(String string) {
	if (htmlPane != null)
	    htmlPane.updateStatus(string);
    }
    
    public CLHtmlPane getTextPane() {
	return htmlPane;
    }
}
