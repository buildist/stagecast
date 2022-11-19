/* CellNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;

public class CellNode
{
    int axis;
    int sIdx;
    int eIdx;
    int slots;
    NodeValue nValue;
    CellNode parent;
    CellNode child;
    CellNode sibling;
    boolean notVisited;
    boolean isRatioed;
    
    public CellNode(int i, NodeValue nodevalue, int i_0_, int i_1_) {
	axis = i;
	eIdx = i_1_;
	sIdx = i_0_;
	nValue = nodevalue;
    }
    
    protected void finalize() {
	parent = child = sibling = null;
	nValue = null;
    }
    
    public boolean updateChild(NodeValue nodevalue, int i, int i_2_) {
	for (CellNode cellnode_3_ = child; cellnode_3_ != null;
	     cellnode_3_ = cellnode_3_.sibling) {
	    int i_4_ = cellnode_3_.eIdx;
	    if (i_2_ <= cellnode_3_.sIdx)
		break;
	    if (i < i_4_) {
		if (i == cellnode_3_.sIdx && i_2_ == i_4_)
		    return true;
		if (i <= cellnode_3_.sIdx && i_2_ >= i_4_)
		    return growChild(cellnode_3_, nodevalue, i, i_2_);
		if (i >= cellnode_3_.sIdx && i_2_ <= i_4_)
		    return cellnode_3_.updateChild(nodevalue, i, i_2_);
		return false;
	    }
	}
	CellNode cellnode_5_ = new CellNode(axis, nodevalue, i, i_2_);
	cellnode_5_.parent = this;
	if (child == null)
	    child = cellnode_5_;
	else if (i_2_ <= child.sIdx) {
	    cellnode_5_.sibling = child;
	    child = cellnode_5_;
	} else {
	    CellNode cellnode_6_;
	    for (cellnode_6_ = child; cellnode_6_.sibling != null;
		 cellnode_6_ = cellnode_6_.sibling) {
		if (cellnode_6_.sibling.sIdx >= i_2_)
		    break;
	    }
	    cellnode_5_.sibling = cellnode_6_.sibling;
	    cellnode_6_.sibling = cellnode_5_;
	}
	return true;
    }
    
    boolean growChild(CellNode cellnode_7_, NodeValue nodevalue, int i,
		      int i_8_) {
	CellNode cellnode_9_ = null;
	for (CellNode cellnode_10_ = cellnode_7_; cellnode_10_.sibling != null;
	     cellnode_10_ = cellnode_10_.sibling) {
	    if (cellnode_10_.sibling.sIdx >= i_8_) {
		if (cellnode_10_.eIdx > i_8_)
		    return false;
		cellnode_9_ = cellnode_10_.sibling;
		cellnode_10_.sibling = null;
		break;
	    }
	    if (cellnode_10_.sibling.eIdx > i_8_)
		return false;
	}
	CellNode cellnode_11_ = new CellNode(axis, nodevalue, i, i_8_);
	cellnode_11_.parent = this;
	cellnode_11_.sibling = cellnode_9_;
	cellnode_11_.child = cellnode_7_;
	for (CellNode cellnode_12_ = cellnode_7_; cellnode_12_ != null;
	     cellnode_12_ = cellnode_12_.sibling)
	    cellnode_12_.parent = cellnode_11_;
	if (child == cellnode_7_)
	    child = cellnode_11_;
	else {
	    CellNode cellnode_13_;
	    for (cellnode_13_ = child; cellnode_13_.sibling != cellnode_7_;
		 cellnode_13_ = cellnode_13_.sibling) {
		/* empty */
	    }
	    cellnode_13_.sibling = cellnode_11_;
	}
	return true;
    }
    
    protected void relaxation() {
	if (child != null) {
	    boolean bool = true;
	    int i_14_;
	    int i_15_;
	    int i_16_;
	    int i = i_14_ = i_15_ = i_16_ = slots = 0;
	    for (CellNode cellnode_17_ = child; cellnode_17_ != null;
		 cellnode_17_ = cellnode_17_.sibling) {
		cellnode_17_.relaxation();
		NodeValue nodevalue = cellnode_17_.nValue;
		i_16_ += nodevalue.ratio;
		i += nodevalue.minSize;
		i_14_ += nodevalue.maxSize;
		i_15_ += nodevalue.prefSize;
		slots += cellnode_17_.eIdx - cellnode_17_.sIdx;
		bool &= nodevalue.fixLen;
	    }
	    if (i_16_ > 0)
		isRatioed = true;
	    if (slots < eIdx - sIdx) {
		int i_18_ = eIdx - sIdx - slots << 1;
		i += i_18_;
		i_14_ += i_18_;
		i_15_ += i_18_;
	    }
	    nValue.update(i, i_14_, i_15_, i_16_);
	    if (!nValue.fixLen && bool) {
		nValue.fixLen = true;
		nValue.alloc = i_15_;
	    }
	}
    }
    
    protected void propogation() {
	if (child != null && slots >= eIdx - sIdx) {
	    int i = nValue.alloc;
	    int i_19_ = 0;
	    int i_20_ = getRootAllocation();
	    for (CellNode cellnode_21_ = child; cellnode_21_ != null;
		 cellnode_21_ = cellnode_21_.sibling) {
		NodeValue nodevalue = cellnode_21_.nValue;
		if (nodevalue.ratio > 0)
		    nodevalue.alloc = Math.max(nodevalue.minSize,
					       i_20_ * nodevalue.ratio / 100);
		i_19_ += nodevalue.alloc;
	    }
	    if (i_19_ > i) {
		i_19_ = 0;
		for (CellNode cellnode_22_ = child; cellnode_22_ != null;
		     cellnode_22_ = cellnode_22_.sibling) {
		    NodeValue nodevalue = cellnode_22_.nValue;
		    if (nodevalue.ratio == 0 && !nodevalue.fixLen) {
			nodevalue.alloc = nodevalue.minSize;
			nodevalue.prefSize = nodevalue.minSize;
		    }
		    i_19_ += nodevalue.alloc;
		}
	    }
	    int i_24_;
	    int i_25_;
	    int i_23_ = i_24_ = i_25_ = i_19_ = 0;
	    for (CellNode cellnode_26_ = child; cellnode_26_ != null;
		 cellnode_26_ = cellnode_26_.sibling) {
		NodeValue nodevalue = cellnode_26_.nValue;
		if (nodevalue.ratio == 0) {
		    if (nodevalue.fixLen)
			i_24_ += nodevalue.alloc;
		    else if (nodevalue.maxSize > nodevalue.alloc)
			i_23_ += nodevalue.maxSize;
		    else if (!nodevalue.fixLen)
			i_25_ += nodevalue.alloc;
		    cellnode_26_.notVisited = true;
		} else
		    i_24_ += nodevalue.alloc;
		i_19_ += nodevalue.alloc;
	    }
	    if (i_19_ < i) {
		int i_27_ = i - i_19_;
		i_19_ = 0;
		if (i_23_ == i) {
		    for (CellNode cellnode_28_ = child; cellnode_28_ != null;
			 cellnode_28_ = cellnode_28_.sibling) {
			NodeValue nodevalue = cellnode_28_.nValue;
			nodevalue.alloc = nodevalue.maxSize;
			cellnode_28_.notVisited = false;
			i_19_ += nodevalue.alloc;
		    }
		} else {
		    for (CellNode cellnode_29_ = child; cellnode_29_ != null;
			 cellnode_29_ = cellnode_29_.sibling) {
			NodeValue nodevalue = cellnode_29_.nValue;
			if (nodevalue.ratio == 0 && !nodevalue.fixLen
			    && nodevalue.maxSize > nodevalue.alloc) {
			    nodevalue.alloc
				+= i_27_ * nodevalue.maxSize / i_23_;
			    cellnode_29_.notVisited = false;
			}
			i_19_ += nodevalue.alloc;
		    }
		}
	    }
	    int i_30_ = i - i_19_;
	    if (i_30_ > 2) {
		i_19_ = 0;
		for (CellNode cellnode_31_ = child; cellnode_31_ != null;
		     cellnode_31_ = cellnode_31_.sibling) {
		    NodeValue nodevalue = cellnode_31_.nValue;
		    if (cellnode_31_.notVisited && !nodevalue.fixLen) {
			nodevalue.alloc += i_30_ * nodevalue.alloc / i_25_;
			cellnode_31_.notVisited = false;
		    }
		    i_19_ += nodevalue.alloc;
		}
	    }
	    i_30_ = i - i_19_;
	    if (i_30_ > 2) {
		i_19_ = 0;
		for (CellNode cellnode_32_ = child; cellnode_32_ != null;
		     cellnode_32_ = cellnode_32_.sibling) {
		    NodeValue nodevalue = cellnode_32_.nValue;
		    if (nodevalue.fixLen || nodevalue.ratio > 0)
			nodevalue.alloc += i_30_ * nodevalue.alloc / i_24_;
		    i_19_ += nodevalue.alloc;
		}
	    }
	    if (i_30_ < 0) {
		i_30_ = i;
		i_19_ = i_23_ = 0;
		for (CellNode cellnode_33_ = child; cellnode_33_ != null;
		     cellnode_33_ = cellnode_33_.sibling) {
		    NodeValue nodevalue = cellnode_33_.nValue;
		    if (nodevalue.alloc > nodevalue.minSize)
			i_23_ += nodevalue.alloc;
		    i_30_ -= nodevalue.minSize;
		}
		for (CellNode cellnode_34_ = child; cellnode_34_ != null;
		     cellnode_34_ = cellnode_34_.sibling) {
		    NodeValue nodevalue = cellnode_34_.nValue;
		    if (nodevalue.alloc > nodevalue.minSize)
			nodevalue.alloc = (nodevalue.minSize
					   + i_30_ * nodevalue.alloc / i_23_);
		    i_19_ += nodevalue.alloc;
		}
	    }
	    if (i_19_ < i) {
		int i_35_ = i - i_19_;
		for (CellNode cellnode_36_ = child;
		     cellnode_36_ != null && i_35_ > 0; i_35_--) {
		    cellnode_36_.nValue.alloc++;
		    cellnode_36_ = cellnode_36_.sibling;
		}
	    }
	    for (CellNode cellnode_37_ = child; cellnode_37_ != null;
		 cellnode_37_ = cellnode_37_.sibling)
		cellnode_37_.propogation();
	}
    }
    
    public void getLength(int[] is) {
	if (child == null) {
	    int i = nValue.alloc / (eIdx - sIdx);
	    for (int i_38_ = sIdx; i_38_ < eIdx; i_38_++) {
		if (is[i_38_] == 0)
		    is[i_38_] = i;
	    }
	} else {
	    int i = eIdx - sIdx - slots;
	    if (i == 0) {
		for (CellNode cellnode_39_ = child; cellnode_39_ != null;
		     cellnode_39_ = cellnode_39_.sibling)
		    cellnode_39_.getLength(is);
	    } else {
		int i_40_ = 0;
		for (CellNode cellnode_41_ = child; cellnode_41_ != null;
		     cellnode_41_ = cellnode_41_.sibling)
		    i_40_ += cellnode_41_.nValue.alloc;
		i_40_ = (nValue.alloc - i_40_) / i;
		CellNode cellnode_42_ = child;
		int i_43_ = sIdx;
		while (i_43_ < eIdx) {
		    if (cellnode_42_ != null && cellnode_42_.sIdx == i_43_) {
			i_43_ = cellnode_42_.eIdx;
			cellnode_42_ = cellnode_42_.sibling;
		    } else
			is[i_43_++] = i_40_;
		}
	    }
	}
    }
    
    public int getMaximumSize() {
	return nValue.maxSize;
    }
    
    public int getMinimumSize() {
	return nValue.minSize;
    }
    
    public int getPreferredSize() {
	return nValue.prefSize;
    }
    
    int getRootAllocation() {
	if (parent == null)
	    return nValue.alloc;
	return parent.getRootAllocation();
    }
}
