/* RadioGroup - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.widget;

public class RadioGroup
{
    CCheckBox curBox;
    
    public synchronized void setSelectedCheckBox(CCheckBox ccheckbox) {
	if (curBox != null && curBox != ccheckbox)
	    curBox.updateStatus();
	curBox = ccheckbox;
    }
}
