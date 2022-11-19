/* ULBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import com.netclue.html.AbstractElement;
import com.netclue.html.HTMLTagBag;
import com.netclue.html.util.HTMLUtilities;

public class ULBlock extends StackBlock
{
    boolean isOrdered = false;
    int startSeq = 1;
    char bulletType = '1';
    
    public ULBlock(AbstractElement abstractelement) {
	super(abstractelement, 1);
	if (abstractelement.getTagCode() == HTMLTagBag.olID) {
	    isOrdered = true;
	    String string
		= (String) abstractelement.getLocalAttribute("start");
	    if (string != null)
		startSeq = HTMLUtilities.stringToInt(string);
	    string = (String) abstractelement.getLocalAttribute("type");
	    if (string != null)
		bulletType = string.trim().charAt(0);
	}
	this.setInsets((short) 20, (short) 20, (short) 0, (short) 0);
    }
    
    public int getMaximumSize(int i) {
	int i_0_ = this.getChildCount();
	int i_1_ = 0;
	for (int i_2_ = 0; i_2_ < i_0_; i_2_++)
	    i_1_ = Math.max(i_1_, this.getChild(i_2_).getMaximumSize(i));
	return i_1_;
    }
    
    int connectChildren(AbstractElement abstractelement, Block[] blocks, int i,
			BlockFactory blockfactory) {
	int i_3_ = abstractelement.getElementCount();
	int i_4_ = 0;
	int i_5_ = i_3_;
	int i_6_ = startSeq;
	for (int i_7_ = 0; i_7_ < i_3_; i_7_++) {
	    AbstractElement abstractelement_8_
		= abstractelement.getElement(i_7_);
	    if (isOrdered
		&& abstractelement_8_.getTagCode() == HTMLTagBag.liID) {
		String string = makeSequence(i_6_++);
		abstractelement_8_.setAttribute("BSequence", string);
	    }
	    Block block = blockfactory.create(abstractelement_8_);
	    if (block != null) {
		blocks[i + i_4_++] = block;
		block.setParent(this);
	    } else {
		int i_9_ = connectChildren(abstractelement_8_, blocks,
					   i + i_4_, blockfactory);
		i_4_ += i_9_;
		i_5_ += i_9_ - 1;
	    }
	}
	return i_5_;
    }
    
    String makeSequence(int i) {
	String string = null;
	switch (bulletType) {
	case '1':
	    string = String.valueOf(i);
	    break;
	case 'a':
	    if (i > 26)
		string = (String.valueOf((char) (97 + i / 26 - 1))
			  + String.valueOf((char) (97 + i % 26 - 1)));
	    else
		string = String.valueOf((char) (97 + i - 1));
	    break;
	case 'A':
	    if (i > 26)
		string = (String.valueOf((char) (65 + i / 26 - 1))
			  + String.valueOf((char) (65 + i % 26 - 1)));
	    else
		string = String.valueOf((char) (65 + i - 1));
	    break;
	case 'i':
	    string = HTMLUtilities.getRomeSequence(i);
	    break;
	case 'I':
	    string = HTMLUtilities.getRomeSequence(i).toUpperCase();
	    break;
	}
	return string;
    }
}
