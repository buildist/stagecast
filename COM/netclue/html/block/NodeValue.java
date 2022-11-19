/* NodeValue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;

public class NodeValue
{
    int alloc;
    int minSize;
    int maxSize;
    int prefSize;
    int ratio;
    boolean fixLen;
    
    NodeValue() {
	alloc = minSize = maxSize = prefSize = ratio = 0;
	fixLen = false;
    }
    
    public void update(TableBlock.TableCell tablecell, int i) {
	int i_0_ = tablecell.getPreferredSize(i);
	minSize = Math.max(minSize, tablecell.getMinimumSize(i));
	maxSize = Math.max(maxSize, tablecell.getMaximumSize(i));
	if (i == 0)
	    ratio = Math.max(ratio, tablecell.getWidthRatio());
	int i_1_ = fixLen ? 2 : 0;
	i_1_ = i_1_ + (tablecell.isFixedCell(i) ? 1 : 0);
	switch (i_1_) {
	case 0:
	case 3:
	    prefSize = Math.max(prefSize, i_0_);
	    break;
	case 1:
	    prefSize = Math.max(i_0_, minSize);
	    fixLen = true;
	    break;
	case 2:
	    prefSize = Math.max(prefSize, minSize);
	    break;
	}
	alloc = fixLen ? prefSize : minSize;
    }
    
    public void update(int i, int i_2_, int i_3_, int i_4_) {
	minSize = Math.max(minSize, i);
	maxSize = Math.max(maxSize, i_2_);
	prefSize = Math.max(prefSize, i_3_);
	if (ratio > 0 && i_4_ > ratio)
	    ratio = i_4_;
	alloc = fixLen ? prefSize : minSize;
    }
    
    public void update(int i) {
	ratio = Math.max(ratio, i);
	fixLen = false;
	alloc = minSize;
    }
}
