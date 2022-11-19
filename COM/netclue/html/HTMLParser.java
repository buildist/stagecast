/* HTMLParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;

import com.netclue.html.util.HTMLUtilities;

public class HTMLParser
{
    static final int INIT_ST = 0;
    static final int TAG_ST = 1;
    static final int ANAME_ST = 2;
    static final int AEQUL_ST = 3;
    static final int AVALUE_ST = 4;
    static final int QUOTE_ST = 5;
    static final int TEXT_ST = 6;
    static final int COMMENT_ST = 7;
    static final int WAITBG_ST = 8;
    static final int SCRPT_ST = 10;
    static final int SCRPT1_ST = 11;
    static final int CQUOTE_ST = 12;
    static final int SPECCOM_ST = 14;
    static final int ENDSCOM_ST = 15;
    static final int SQUOTE_ST = 16;
    private static TagBag dftTagBag = new HTMLTagBag();
    Hashtable tbag = dftTagBag.getMapping();
    static int[] leafTagCode
	= { 4, 5, 8, 19, 50, 51, 120, 131, 133, 135, 137, 141, 150 };
    
    public GenericElement parse(CLHTMLDocument clhtmldocument, String string) {
	LinkList linklist = tokenize(clhtmldocument, string);
	return parseHTML(clhtmldocument, linklist);
    }
    
    protected LinkList tokenize(CLHTMLDocument clhtmldocument, String string) {
	LinkList linklist = new LinkList();
	boolean bool = true;
	boolean bool_0_ = false;
	boolean bool_1_ = false;
	char c = '\0';
	int i = 0;
	int i_2_ = 0;
	String string_3_ = null;
	Object object = null;
	StringBuffer stringbuffer_4_;
	StringBuffer stringbuffer_5_;
	StringBuffer stringbuffer = stringbuffer_4_ = stringbuffer_5_ = null;
	TagElement tagelement = null;
	StringTokenizer stringtokenizer
	    = new StringTokenizer(string, " \t\n\r<>'\"=", true);
	while (stringtokenizer.hasMoreTokens()) {
	    String string_6_ = stringtokenizer.nextToken();
	    int i_7_ = string_6_.length();
	    i_2_ += i_7_;
	    char c_8_;
	    if (i_7_ == 1) {
		c_8_ = string_6_.charAt(0);
		c_8_ = Character.isLetterOrDigit(c_8_) ? '\0' : c_8_;
		if (bool) {
		    if (c_8_ == '\t')
			c_8_ = ' ';
		    if (c_8_ == '\r')
			c_8_ = '\n';
		    if (c_8_ == ' ' || c_8_ == '\n') {
			if (bool_0_)
			    continue;
			bool_0_ = true;
		    } else
			bool_0_ = false;
		} else {
		    if (c_8_ == '\n' && c == '\r' || c_8_ == '\r' && c == '\n')
			continue;
		    c = c_8_;
		    if (c_8_ == '\r')
			c_8_ = '\n';
		}
	    } else {
		bool_0_ = false;
		c_8_ = '\0';
	    }
	    switch (i) {
	    case 0:
		if (c_8_ == '<')
		    i = 1;
		else if (c_8_ == '\n') {
		    if (bool) {
			stringbuffer = new StringBuffer();
			i = 6;
		    } else {
			tagelement = new TagElement(135, false);
			linklist.append(tagelement);
		    }
		} else {
		    stringbuffer = (c_8_ == ' ' ? new StringBuffer(" ")
				    : new StringBuffer(string_6_));
		    i = 6;
		}
		break;
	    case 1:
		if (c_8_ == ' ')
		    i = 0;
		else if (c_8_ == '<') {
		    stringbuffer = new StringBuffer("<<");
		    i = 6;
		} else if (string_6_.startsWith("!--")) {
		    if (string.indexOf("-->", i_2_) != -1)
			i = 15;
		    else
			i = 8;
		} else if (string_6_.charAt(0) == '!')
		    i = 7;
		else {
		    boolean bool_9_ = false;
		    string_6_ = string_6_.toLowerCase();
		    if (string_6_.charAt(0) == '/') {
			bool_9_ = true;
			string_6_ = string_6_.substring(1);
		    }
		    Integer integer = (Integer) tbag.get(string_6_);
		    if (integer != null) {
			tagelement
			    = new TagElement(integer.intValue(), bool_9_);
			linklist.append(tagelement);
			if (tagelement.type == HTMLTagBag.preID
			    || tagelement.type == 122)
			    bool = tagelement.isEnd;
		    }
		    i = 2;
		}
		break;
	    case 8:
		if (c_8_ == '>')
		    i = 0;
		break;
	    case 2:
		if (c_8_ == '>') {
		    if (tagelement != null
			&& (tagelement.type == 6 || tagelement.type == 7)
			&& !tagelement.isEnd) {
			linklist.removeLast();
			String string_10_
			    = (String) tagelement.getAttribute("src");
			i = 10;
		    } else
			i = 0;
		} else if (c_8_ == 0) {
		    string_3_ = string_6_;
		    i = 3;
		}
		break;
	    case 3:
		if (c_8_ == 0) {
		    if (tagelement != null)
			setAttributeValue(tagelement, string_3_, "");
		    string_3_ = string_6_;
		} else if (c_8_ == '>') {
		    if (tagelement != null) {
			setAttributeValue(tagelement, string_3_, "");
			if ((tagelement.type == 6 || tagelement.type == 7)
			    && !tagelement.isEnd) {
			    linklist.removeElement();
			    String string_11_
				= (String) tagelement.getAttribute("src");
			    i = 10;
			} else
			    i = 0;
		    } else
			i = 0;
		} else if (c_8_ == '=') {
		    stringbuffer_4_ = new StringBuffer();
		    i = 4;
		} else if (c_8_ != ' ' && c_8_ != '\n')
		    i = 2;
		break;
	    case 4: {
		boolean bool_12_ = c_8_ == ' ' || c_8_ == '\n';
		if (c_8_ == '\"' || c_8_ == '\'') {
		    if (stringbuffer_4_.length() > 0)
			i = 2;
		    else
			i = c_8_ == '\"' ? 5 : 16;
		} else if (!bool_12_ || stringbuffer_4_.length() != 0) {
		    if (bool_12_ || c_8_ == '>') {
			if (tagelement != null) {
			    setAttributeValue(tagelement, string_3_,
					      stringbuffer_4_.toString());
			    if (bool_12_)
				i = 2;
			    else if ((tagelement.type == 6
				      || tagelement.type == 7)
				     && !tagelement.isEnd) {
				linklist.removeElement();
				String string_13_
				    = (String) tagelement.getAttribute("src");
				i = 10;
			    } else
				i = 0;
			} else
			    i = bool_12_ ? 2 : 0;
		    } else
			stringbuffer_4_.append(string_6_);
		}
		break;
	    }
	    case 5:
		if (c_8_ == '\"') {
		    if (tagelement != null && stringbuffer_4_.length() > 0)
			setAttributeValue(tagelement, string_3_,
					  stringbuffer_4_.toString());
		    i = 2;
		} else
		    stringbuffer_4_.append(string_6_);
		break;
	    case 16:
		if (c_8_ == '\'') {
		    if (tagelement != null && stringbuffer_4_.length() > 0)
			setAttributeValue(tagelement, string_3_,
					  stringbuffer_4_.toString());
		    i = 2;
		} else
		    stringbuffer_4_.append(string_6_);
		break;
	    case 6:
		if (c_8_ == '<') {
		    if (stringbuffer.length() > 0)
			linklist
			    .append(new TagElement(stringbuffer.toString()));
		    i = 1;
		} else if (!bool && c_8_ == '\n') {
		    linklist.append(new TagElement(stringbuffer.toString()));
		    tagelement = new TagElement(135, false);
		    linklist.append(tagelement);
		    i = 0;
		} else if (c_8_ == ' ' || c_8_ == '\n')
		    stringbuffer.append(" ");
		else
		    stringbuffer.append(string_6_);
		break;
	    case 12:
		if (c_8_ == '\"')
		    i = 14;
		break;
	    case 14:
		if (string_6_.endsWith("--"))
		    i = 15;
		break;
	    case 15:
		if (c_8_ == '>')
		    i = 0;
		else
		    i = 14;
		break;
	    case 7:
		if (c_8_ == '>')
		    i = 0;
		break;
	    case 10:
		if (c_8_ == '<')
		    i = 11;
		break;
	    case 11:
		if (string_6_.equalsIgnoreCase("/script")) {
		    tagelement = new TagElement(6, true);
		    i = 2;
		} else if (string_6_.equalsIgnoreCase("/style")) {
		    tagelement = new TagElement(7, true);
		    linklist.append(tagelement);
		    i = 2;
		} else
		    i = 10;
		break;
	    }
	}
	if (i == 6 && stringbuffer.length() > 0)
	    linklist.append(new TagElement(stringbuffer.toString()));
	return linklist;
    }
    
    String loadExternalFile(URL url, String string) {
	String string_14_ = null;
	try {
	    URL url_15_ = new URL(url, string);
	    InputStreamReader inputstreamreader
		= new InputStreamReader(url_15_.openStream());
	    char[] cs = new char[4096];
	    boolean bool = false;
	    StringBuffer stringbuffer = new StringBuffer();
	    int i;
	    while ((i = inputstreamreader.read(cs, 0, 4096)) != -1)
		stringbuffer.append(cs, 0, i);
	    inputstreamreader.close();
	    string_14_ = stringbuffer.toString();
	} catch (Exception exception) {
	    /* empty */
	}
	return string_14_;
    }
    
    void setAttributeValue(TagElement tagelement, String string,
			   String string_16_) {
	String string_17_ = string.toLowerCase().intern();
	tagelement.setAttribute(string_17_, string_16_);
    }
    
    boolean isLeafTag(int i) {
	int i_18_ = 0;
	while (i > leafTagCode[i_18_++]) {
	    /* empty */
	}
	if (i != leafTagCode[i_18_ - 1])
	    return false;
	return true;
    }
    
    int structureWeight(int i) {
	if (i == HTMLTagBag.noframeID || i == HTMLTagBag.framesetID)
	    return 20;
	if (i == HTMLTagBag.trID || i == HTMLTagBag.captionID)
	    return 12;
	if (i == HTMLTagBag.cellID || i == HTMLTagBag.thID
	    || i >= HTMLTagBag.ulID && i <= HTMLTagBag.dlID)
	    return 10;
	if (i == HTMLTagBag.addrID || i == HTMLTagBag.nobrID
	    || i == HTMLTagBag.divID || i == HTMLTagBag.cenID
	    || i >= HTMLTagBag.liID && i <= HTMLTagBag.ddID)
	    return 8;
	if (i == HTMLTagBag.parID)
	    return 6;
	if (i > HTMLTagBag.bodyID && i < HTMLTagBag.addrID
	    || i == HTMLTagBag.bqID || i == HTMLTagBag.preID)
	    return 4;
	if (i == HTMLTagBag.selID)
	    return 2;
	if (i == HTMLTagBag.optID)
	    return 1;
	if (i == HTMLTagBag.headID)
	    return 16;
	if (i == HTMLTagBag.bodyID)
	    return 18;
	if (i < 2)
	    return 20;
	return 0;
    }
    
    GenericElement popIfExist(GenericElement genericelement, Stack stack,
			      int i, int i_19_) {
	int i_21_;
	int i_20_ = i_21_ = stack.size() - 1;
	int i_22_ = 0;
	for (/**/; i_20_ >= 0; i_20_--) {
	    i_22_ = ((AbstractElement) stack.elementAt(i_20_)).getTagCode();
	    if (i_22_ == i || structureWeight(i_22_) >= structureWeight(i))
		break;
	}
	if (i_20_ >= 0) {
	    if (i_22_ == i) {
		GenericElement genericelement_23_
		    = (GenericElement) stack.pop();
		genericelement_23_.setEndIndex(i_19_);
	    }
	    for (/**/; i_21_ > i_20_; i_21_--) {
		GenericElement genericelement_24_
		    = (GenericElement) stack.pop();
		genericelement_24_.setEndIndex(i_19_);
	    }
	    genericelement.setEndIndex(i_19_);
	    return (GenericElement) stack.pop();
	}
	return genericelement;
    }
    
    GenericElement popUntil(Stack stack, int i, int i_25_, int i_26_) {
	int i_27_;
	int i_28_;
	for (i_28_ = i_27_ = stack.size() - 1; i_28_ >= 0; i_28_--) {
	    AbstractElement abstractelement
		= (AbstractElement) stack.elementAt(i_28_);
	    int i_29_ = abstractelement.getTagCode();
	    if (i_29_ >= i && i_29_ <= i_25_)
		break;
	}
	if (i_28_ >= 0) {
	    for (/**/; i_27_ > i_28_; i_27_--) {
		GenericElement genericelement = (GenericElement) stack.pop();
		genericelement.setEndIndex(i_26_);
	    }
	    return (GenericElement) stack.pop();
	}
	return null;
    }
    
    GenericElement parseHTML(CLHTMLDocument clhtmldocument,
			     LinkList linklist) {
	Stack stack = new Stack();
	Stack stack_30_ = new Stack();
	int i_31_;
	int i_32_;
	int i_33_;
	int i_34_;
	int i_35_;
	int i = i_31_ = i_32_ = i_33_ = i_34_ = i_35_ = 0;
	boolean bool = false;
	boolean bool_36_ = true;
	boolean bool_38_;
	boolean bool_39_;
	boolean bool_37_ = bool_38_ = bool_39_ = false;
	HTMLTagAttributes htmltagattributes = new HTMLTagAttributes();
	StyleFactory stylefactory
	    = (clhtmldocument == null ? new StyleFactory()
	       : clhtmldocument.getStyleFactory());
	GenericElement genericelement = new GenericElement(1, null, null, 0);
	GenericElement genericelement_40_ = genericelement;
	GenericElement genericelement_41_ = null;
	linklist.setToBegin();
	while (linklist.hasMoreElements()) {
	    TagElement tagelement = (TagElement) linklist.nextElement();
	    i_33_ = i_34_;
	    i_34_ = tagelement.type;
	    if (bool_36_ && i_34_ == 150
		&& tagelement.getHTMLContentLength() == 0) {
		bool_39_
		    = i_33_ != 135 && genericelement.getElementCount() > 0;
		i_34_ = 135;
	    } else if (i_34_ == 1) {
		if (!tagelement.isEnd && !bool) {
		    genericelement = new GenericElement(1, null, null, 0);
		    genericelement_40_ = genericelement;
		    bool = true;
		    bool_37_ = false;
		}
	    } else if (i_34_ != 7) {
		if (i_34_ <= 64 || i_34_ == 135 || i_34_ == 136)
		    bool_39_ = false;
		i++;
		int i_42_ = genericelement.getTagCode();
		if (!tagelement.isEnd && i_34_ != HTMLTagBag.formID) {
		    switch (i_42_) {
		    case 3:
			if (i_34_ != 10) {
			    if (i_34_ == 150 && clhtmldocument != null)
				clhtmldocument.putProperty("title",
							   tagelement
							       .getContent());
			} else
			    break;
			continue;
		    case 60:
			if (i_34_ < HTMLTagBag.tableID
			    || i_34_ > HTMLTagBag.cellID) {
			    boolean bool_43_ = true;
			    if (i_34_ == 120) {
				String string
				    = (String) tagelement.getAttribute("type");
				if (string != null
				    && string.equalsIgnoreCase("hidden"))
				    bool_43_ = false;
			    }
			    if (genericelement.getElementCount() != 0
				|| !bool_43_)
				continue;
			    GenericElement genericelement_44_
				= new GenericElement(HTMLTagBag.trID,
						     genericelement, null, i);
			    genericelement.appendElement(genericelement_44_);
			    stack.push(genericelement);
			    genericelement = genericelement_44_;
			    genericelement_44_
				= new GenericElement(HTMLTagBag.cellID,
						     genericelement, null, i);
			    genericelement.appendElement(genericelement_44_);
			    stack.push(genericelement);
			    genericelement = genericelement_44_;
			    stack_30_.push(htmltagattributes);
			    stack_30_.push(Boolean.TRUE);
			    htmltagattributes = new HTMLTagAttributes();
			    i_42_ = HTMLTagBag.cellID;
			}
			break;
		    case 62:
			if (i_34_ < 62 || i_34_ > 64) {
			    boolean bool_45_ = true;
			    if (i_34_ == 120) {
				String string
				    = (String) tagelement.getAttribute("type");
				if (string != null
				    && string.equalsIgnoreCase("hidden"))
				    bool_45_ = false;
			    }
			    if (bool_45_) {
				GenericElement genericelement_46_
				    = new GenericElement(HTMLTagBag.cellID,
							 genericelement, null,
							 i);
				genericelement
				    .appendElement(genericelement_46_);
				stack.push(genericelement);
				genericelement = genericelement_46_;
				stack_30_.push(htmltagattributes);
				stack_30_.push(Boolean.TRUE);
				htmltagattributes = new HTMLTagAttributes();
				i_42_ = HTMLTagBag.cellID;
			    }
			}
			break;
		    case 140:
			if (i_34_ == 141)
			    break;
			continue;
		    }
		}
		if (i_34_ > 100 && i_34_ < 120) {
		    switch (i_34_) {
		    case 102:
		    case 110:
			if (tagelement.isEnd)
			    i_31_ &= ~0x1;
			else
			    i_31_ |= 0x1;
			break;
		    case 103:
		    case 111:
			if (tagelement.isEnd)
			    i_31_ &= ~0x2;
			else
			    i_31_ |= 0x2;
			break;
		    case 104:
			if (tagelement.isEnd)
			    i_31_ &= ~0x4;
			else
			    i_31_ |= 0x4;
			break;
		    case 105:
			if (tagelement.isEnd)
			    i_31_ &= ~0x8;
			else
			    i_31_ |= 0x8;
			break;
		    case 108:
		    case 109: {
			int i_47_ = i_34_ == 108 ? 32 : 16;
			if (tagelement.isEnd) {
			    i_31_ &= i_47_ ^ 0xffffffff;
			    htmltagattributes.removeAttribute("size");
			} else {
			    i_31_ |= i_47_;
			    htmltagattributes.addAttribute("size", "-1");
			}
			break;
		    }
		    case 106:
		    case 107:
			if (tagelement.isEnd)
			    htmltagattributes.removeAttribute("size");
			else {
			    String string = i_34_ == 106 ? "+2" : "-1";
			    htmltagattributes.addAttribute("size", string);
			}
			break;
		    default:
			break;
		    }
		} else if (isLeafTag(i_34_)) {
		    TermElement termelement;
		    switch (i_34_) {
		    case 120:
			if (!tagelement.isEnd) {
			    if (i_42_ == HTMLTagBag.selID)
				genericelement = (GenericElement) stack.pop();
			    termelement
				= new TermElement(i_34_, genericelement,
						  tagelement.attr, i);
			    if (clhtmldocument != null
				&& genericelement_41_ != null) {
				genericelement_41_.appendElement(termelement);
				clhtmldocument.associateFormCtrls
				    (genericelement_41_, termelement);
			    }
			    break;
			}
			continue;
		    case 150: {
			String string = tagelement.getContent();
			int i_48_ = string.length();
			boolean bool_49_
			    = (i_48_ > 1 ? string.charAt(i_48_ - 1) == ' '
			       : false);
			bool_39_ = bool_39_ | string.charAt(0) == ' ';
			if (bool_36_) {
			    string = string.trim();
			    if (bool_39_)
				string = " " + string;
			    bool_39_ = bool_49_;
			    if (string.length() == 0)
				continue;
			}
			string = HTMLUtilities.xlateSpecialChars(string);
			int i_50_ = i + string.length();
			if (i_31_ != 0)
			    htmltagattributes.addAttribute((StyleFactory
							    .FontProperty),
							   new Integer(i_31_));
			termelement = new TermElement(genericelement,
						      htmltagattributes,
						      stylefactory, i, string);
			htmltagattributes
			    .removeAttribute(StyleFactory.FontProperty);
			i = i_50_ - 1;
			break;
		    }
		    default:
			termelement = new TermElement(i_34_, genericelement,
						      tagelement.attr, i);
		    }
		    genericelement.appendElement(termelement);
		} else if (tagelement.isEnd) {
		    switch (i_34_) {
		    case 34:
			bool_36_ = true;
			break;
		    case 40:
			genericelement_41_ = null;
			continue;
		    case 60:
			if (i_42_ != HTMLTagBag.tableID) {
			    GenericElement genericelement_51_
				= popUntil(stack, HTMLTagBag.tableID,
					   HTMLTagBag.tableID, i);
			    if (genericelement_51_ != null) {
				genericelement.setEndIndex(i);
				genericelement = genericelement_51_;
				i_42_ = genericelement.getTagCode();
			    }
			}
			break;
		    case 62:
			if (i_42_ != HTMLTagBag.tableID
			    && (i_42_ != 62
				|| genericelement.getElementCount() != 0))
			    break;
			continue;
		    case 64:
			i_31_ &= ~0x2;
			/* fall through */
		    case 63:
			if (i_42_ != HTMLTagBag.tableID
			    && i_42_ != HTMLTagBag.trID) {
			    while (!stack_30_.empty()) {
				Object object = stack_30_.pop();
				if (object instanceof Boolean) {
				    htmltagattributes
					= (HTMLTagAttributes) stack_30_.pop();
				    break;
				}
			    }
			    break;
			}
			continue;
		    case 122:
			bool_36_ = true;
			break;
		    case 134:
			if (!stack_30_.empty()) {
			    Object object = stack_30_.pop();
			    if (object instanceof Boolean)
				stack_30_.push(object);
			    else
				htmltagattributes = (HTMLTagAttributes) object;
			}
			continue;
		    default:
			break;
		    case 1:
		    case 10:
			continue;
		    }
		    if (i_42_ == i_34_
			|| structureWeight(i_42_) < structureWeight(i_34_)) {
			if (i_42_ != i_34_)
			    genericelement
				= popIfExist(genericelement, stack, i_34_, i);
			else {
			    genericelement.setEndIndex(i);
			    genericelement = (GenericElement) stack.pop();
			}
		    }
		} else {
		    switch (i_34_) {
		    case 10:
			if (!bool_37_) {
			    if (i_42_ > 1 && i_42_ != 20) {
				GenericElement genericelement_52_
				    = popUntil(stack, 0, 1, i);
				genericelement.setEndIndex(i);
				genericelement = (genericelement_52_ == null
						  ? genericelement_40_
						  : genericelement_52_);
			    }
			    bool_37_ = true;
			    break;
			}
			continue;
		    case 18:
			if (i_42_ == HTMLTagBag.framesetID) {
			    GenericElement genericelement_53_
				= new GenericElement(i_34_, genericelement,
						     tagelement.attr, i);
			    genericelement.appendElement(genericelement_53_);
			    stack.push(genericelement);
			    genericelement.setEndIndex(i);
			    genericelement = genericelement_53_;
			} else
			    break;
			continue;
		    case 25:
			tagelement.setAttribute(StyleFactory.SpaceAbove,
						new Float(20.0F));
			break;
		    case 28:
			tagelement.setAttribute("bullet", new Integer(0));
			/* fall through */
		    case 27:
			if (i_42_ == HTMLTagBag.dtID
			    || i_42_ == HTMLTagBag.ddID) {
			    genericelement
				= popIfExist(genericelement, stack, i_34_, i);
			    i_42_ = genericelement.getTagCode();
			}
			break;
		    case 34:
			bool_36_ = false;
			break;
		    case 40:
			if (clhtmldocument != null) {
			    genericelement_41_
				= new GenericElement(40, null, tagelement.attr,
						     0);
			    clhtmldocument.addHTMLForm(genericelement_41_);
			}
			continue;
		    case 60:
			if (i_42_ == 60) {
			    stack.push(genericelement);
			    GenericElement genericelement_54_
				= new GenericElement(HTMLTagBag.trID,
						     genericelement, null, i);
			    genericelement.appendElement(genericelement_54_);
			    stack.push(genericelement = genericelement_54_);
			    genericelement_54_
				= new GenericElement(HTMLTagBag.cellID,
						     genericelement, null, i);
			    genericelement.appendElement(genericelement_54_);
			    genericelement = genericelement_54_;
			    stack_30_.push(htmltagattributes);
			    stack_30_.push(Boolean.TRUE);
			    htmltagattributes = new HTMLTagAttributes();
			}
			break;
		    case 62:
			if (i_42_ != HTMLTagBag.tableID) {
			    GenericElement genericelement_55_
				= popUntil(stack, HTMLTagBag.tableID,
					   HTMLTagBag.tableID, i);
			    if (genericelement_55_ == null)
				continue;
			    genericelement = genericelement_55_;
			}
			break;
		    case 64:
			i_31_ |= 0x2;
			/* fall through */
		    case 63: {
			GenericElement genericelement_56_;
			if (i_42_ == 63 || i_42_ == 64)
			    genericelement_56_ = (GenericElement) stack.pop();
			else if (i_42_ >= HTMLTagBag.tableID
				 && i_42_ <= HTMLTagBag.trID)
			    genericelement_56_ = genericelement;
			else
			    genericelement_56_
				= popUntil(stack, HTMLTagBag.tableID,
					   HTMLTagBag.trID, i);
			if (genericelement_56_ != null) {
			    genericelement.setEndIndex(i);
			    genericelement = genericelement_56_;
			    if (genericelement_56_.getTagCode()
				== HTMLTagBag.tableID) {
				genericelement_56_
				    = new GenericElement(HTMLTagBag.trID,
							 genericelement, null,
							 i);
				genericelement
				    .appendElement(genericelement_56_);
				stack.push(genericelement);
				genericelement = genericelement_56_;
			    }
			}
			String string
			    = ((String)
			       tagelement.getAttribute(HTMLConst.align));
			if (i_34_ == 64 && string == null)
			    string = "center";
			if (string == null)
			    string = ((String)
				      genericelement
					  .getLocalAttribute(HTMLConst.align));
			if (string != null)
			    tagelement.setAttribute(HTMLConst.align, string);
			if (tagelement.getAttribute(HTMLConst.valign)
			    == null) {
			    string
				= ((String)
				   genericelement
				       .getLocalAttribute(HTMLConst.valign));
			    if (string != null)
				tagelement.setAttribute(HTMLConst.valign,
							string);
			}
			stack_30_.push(htmltagattributes);
			stack_30_.push(Boolean.TRUE);
			htmltagattributes = new HTMLTagAttributes();
			break;
		    }
		    case 122:
			bool_36_ = false;
			/* fall through */
		    case 121:
			if (genericelement_41_ != null)
			    tagelement.setAttribute(HTMLConst.fmCtrl,
						    genericelement_41_);
			break;
		    case 134:
			if (tagelement.attr != null) {
			    stack_30_.push(htmltagattributes.clone());
			    htmltagattributes.addAttributes(tagelement.attr);
			}
			continue;
		    }
		    if ((i_34_ < HTMLTagBag.tableID
			 || i_34_ == HTMLTagBag.optID)
			&& (i_42_ == i_34_ || (structureWeight(i_42_)
					       < structureWeight(i_34_)))) {
			if (i_42_ != i_34_)
			    genericelement
				= popIfExist(genericelement, stack, i_34_, i);
			else {
			    genericelement.setEndIndex(i);
			    genericelement = (GenericElement) stack.pop();
			}
		    }
		    GenericElement genericelement_57_
			= new GenericElement(i_34_, genericelement,
					     tagelement.attr, i);
		    genericelement.appendElement(genericelement_57_);
		    stack.push(genericelement);
		    genericelement.setEndIndex(i);
		    genericelement = genericelement_57_;
		}
	    }
	}
	return genericelement_40_;
    }
}
