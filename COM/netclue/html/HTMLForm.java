/* HTMLForm - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html;
import java.awt.Button;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.netclue.html.event.HyperlinkEvent;
import com.netclue.html.widget.CButton;
import com.netclue.html.widget.CCheckBox;
import com.netclue.html.widget.CSelect;
import com.netclue.html.widget.CTextField;
import com.netclue.html.widget.RadioGroup;
import com.netclue.html.widget.Widget;

public class HTMLForm
{
    private String action;
    private String target;
    private int method;
    private int txCount;
    private ActionController actCtrl;
    private Hashtable compHash = new Hashtable(16);
    private Hashtable dftNmVle = new Hashtable(16);
    private Hashtable curNmVle = new Hashtable(16);
    private Hashtable radioHash = new Hashtable(16);
    private Hashtable valueHash = new Hashtable(16);
    private Vector submitStr = new Vector(4);
    private Vector orderedName = new Vector();
    Component resetComp;
    CLHtmlPane htmlText;
    
    class ActionController implements ActionListener
    {
	private HTMLForm htmlForm;
	
	ActionController(HTMLForm htmlform_0_) {
	    htmlForm = htmlform_0_;
	}
	
	public void actionPerformed(ActionEvent actionevent) {
	    String string = actionevent.getActionCommand();
	    Component component = (Component) actionevent.getSource();
	    if (component == resetComp)
		reset();
	    else {
		String string_1_ = htmlForm.getName(component);
		if (component instanceof CCheckBox) {
		    string = htmlForm.getValue(component);
		    CCheckBox ccheckbox = (CCheckBox) component;
		    if (ccheckbox.getRadioGroup() == null) {
			if (ccheckbox.getState())
			    htmlForm.updateAttribute(string_1_, string, false);
			else
			    htmlForm.removeAttribute(string_1_);
		    } else
			htmlForm.updateAttribute(string_1_, string, true);
		} else if (!(component instanceof Button)
			   && !(component instanceof CButton)
			   && string_1_ != null && string_1_ != null)
		    htmlForm.updateAttribute(string_1_, string, true);
		do {
		    if (!htmlForm.isSubmit(string)) {
			HTMLForm htmlform = HTMLForm.this;
			if ((htmlform.txCount != 1
			     || !(component instanceof CTextField))
			    && (string_1_ != null
				|| !(component instanceof CButton)))
			    break;
		    }
		    htmlForm.submit();
		} while (false);
	    }
	}
    }
    
    public HTMLForm(String string, int i) {
	action = string;
	method = i;
	actCtrl = new ActionController(this);
    }
    
    public void setTextPane(CLHtmlPane clhtmlpane) {
	htmlText = clhtmlpane;
    }
    
    public void addControl(Component component, String string,
			   String string_2_, String string_3_) {
	if (component == null) {
	    if (string != null)
		addAttribute(string, string_2_);
	} else {
	    if (component instanceof Widget)
		((Widget) component).addActionListener(actCtrl);
	    if (string_2_ == null)
		string_2_ = "";
	    if (string != null) {
		addAttribute(string, string_2_);
		compHash.put(component, string);
	    }
	    if (component instanceof CCheckBox) {
		valueHash.put(component, string_2_);
		if (string_3_.equals("radio")) {
		    RadioGroup radiogroup = (RadioGroup) radioHash.get(string);
		    if (radiogroup == null) {
			radiogroup = new RadioGroup();
			radioHash.put(string, radiogroup);
		    }
		    ((CCheckBox) component).setRadioGroup(radiogroup);
		}
	    } else if (component instanceof CTextField)
		txCount++;
	    else if (string_3_.equals("submit") || string_3_.equals("image"))
		submitStr.addElement(string);
	    else if (string_3_.equals("reset"))
		resetComp = component;
	}
    }
    
    public void setAction(String string) {
	if (string != null)
	    action = string;
    }
    
    public String getAction() {
	return action;
    }
    
    public void setMethod(int i) {
	method = i;
    }
    
    public int getMethod() {
	return method;
    }
    
    public void setTarget(String string) {
	if (string != null)
	    target = string;
    }
    
    public String getTarget() {
	return target;
    }
    
    String getRadioValue(Component component) {
	return (String) valueHash.get(component);
    }
    
    public String getValue(Component component) {
	String string = getName(component);
	if (string != null)
	    return (String) curNmVle.get(string);
	return null;
    }
    
    public void setValue(Component component, String string) {
	if (string != null) {
	    String string_4_ = getName(component);
	    if (string_4_ != null) {
		string = string.trim();
		curNmVle.put(string_4_, string);
	    }
	}
    }
    
    public String getName(Component component) {
	return (String) compHash.get(component);
    }
    
    protected void addAttribute(String string, String string_5_) {
	orderedName.addElement(string);
	if (string_5_ != null) {
	    String string_6_ = string_5_.trim();
	    dftNmVle.put(string, string_6_);
	    curNmVle.put(string, string_6_);
	} else {
	    dftNmVle.put(string, "");
	    curNmVle.put(string, "");
	}
    }
    
    protected void removeAttribute(String string) {
	curNmVle.remove(string);
    }
    
    protected void updateAttribute(String string, String string_7_,
				   boolean bool) {
	if (bool && curNmVle.containsKey(string))
	    curNmVle.remove(string);
	if (string_7_ != null)
	    curNmVle.put(string, string_7_.trim());
	else
	    curNmVle.put(string, "");
    }
    
    public boolean isSubmit(String string) {
	return submitStr.contains(string);
    }
    
    protected void finalize() {
	actCtrl = null;
	compHash = dftNmVle = curNmVle = radioHash = valueHash = null;
	submitStr = null;
	htmlText = null;
    }
    
    public String toString() {
	Enumeration enumeration = compHash.keys();
	while (enumeration.hasMoreElements()) {
	    Component component = (Component) enumeration.nextElement();
	    if (component instanceof CTextField) {
		String string = ((CTextField) component).getText();
		updateAttribute(getName(component), string, true);
	    }
	}
	String string = null;
	int i = orderedName.size();
	boolean bool = true;
	for (int i_8_ = 0; i_8_ < i; i_8_++) {
	    String string_9_ = (String) orderedName.elementAt(i_8_);
	    String string_10_
		= URLEncoder.encode((String) curNmVle.get(string_9_));
	    if (bool) {
		string = string_9_ + "=" + string_10_;
		bool = false;
	    } else
		string += "&" + (String) string_9_ + "=" + (String) string_10_;
	}
	if (method == HyperlinkEvent.GET_METHOD)
	    string = action + '?' + string;
	else {
	    htmlText.setPostContent(string);
	    string = action;
	}
	return string;
    }
    
    public void submit() {
	if (action != null) {
	    try {
		URL url = new URL(htmlText.getPage(), toString());
		HyperlinkEvent hyperlinkevent
		    = new HyperlinkEvent(htmlText, url, method);
		if (target != null)
		    hyperlinkevent.setTarget(target);
		htmlText.fireHyperlinkEvent(hyperlinkevent);
	    } catch (java.net.MalformedURLException malformedurlexception) {
		/* empty */
	    }
	}
    }
    
    public void reset() {
	Enumeration enumeration = compHash.keys();
	while (enumeration.hasMoreElements()) {
	    Component component = (Component) enumeration.nextElement();
	    String string = getName(component);
	    String string_11_ = (String) dftNmVle.get(string);
	    if (component instanceof CTextField)
		((CTextField) component).setText(string_11_);
	    else if (component instanceof CCheckBox) {
		boolean bool = string_11_ != null;
		CCheckBox ccheckbox = (CCheckBox) component;
		if (ccheckbox.getRadioGroup() == null)
		    ccheckbox.setState(bool);
		else {
		    String string_12_ = getRadioValue(component);
		    if (string_12_.equals(string_11_) && !ccheckbox.getState())
			ccheckbox.updateStatus();
		}
	    } else if (component instanceof CSelect)
		((CSelect) component).setSelectedItem(string_11_);
	}
	curNmVle = (Hashtable) dftNmVle.clone();
    }
}
