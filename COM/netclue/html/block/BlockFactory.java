/* BlockFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import com.netclue.html.AbstractElement;
import com.netclue.html.HTMLTagBag;
import com.netclue.html.StyleFactory;

public class BlockFactory
{
    public Block create(AbstractElement abstractelement) {
	int i = abstractelement.getTagCode();
	if (i == HTMLTagBag.textID)
	    return new TextBlock(abstractelement);
	if (i >= HTMLTagBag.parID && i < HTMLTagBag.preID)
	    return new CellBlock(abstractelement);
	if (i == HTMLTagBag.brID)
	    return new BRBlock(abstractelement);
	if (i == HTMLTagBag.imgID)
	    return new ImageBlock(abstractelement);
	if (i == HTMLTagBag.tableID)
	    return new TableBlock(abstractelement);
	if (i == HTMLTagBag.hrID)
	    return new HRuleBlock(abstractelement);
	if (i == HTMLTagBag.inputID || i == 121 || i == 122) {
	    Object object
		= abstractelement
		      .getLocalAttribute(StyleFactory.ComponentAttribute);
	    if (object == null)
		return null;
	    return new WidgetBlock(abstractelement);
	}
	if (i == HTMLTagBag.nobrID)
	    return new NoBrBlock(abstractelement);
	if (i == HTMLTagBag.liID || i == HTMLTagBag.ddID)
	    return new ListItemBlock(abstractelement);
	if (i >= 11 && i <= 16 || i == HTMLTagBag.addrID)
	    return new CellBlock(abstractelement);
	if (i == HTMLTagBag.dlID || i == HTMLTagBag.dtID)
	    return new CellBlock(abstractelement);
	if (i == HTMLTagBag.blankID)
	    return new BlankBlock(abstractelement);
	if (i >= HTMLTagBag.ulID && i <= HTMLTagBag.menuID)
	    return new ULBlock(abstractelement);
	if (i == HTMLTagBag.bodyID)
	    return new BodyBlock(abstractelement);
	if (i == HTMLTagBag.captionID)
	    return new CellBlock(abstractelement);
	if (i == HTMLTagBag.preID)
	    return new PreBlock(abstractelement);
	return null;
    }
}
