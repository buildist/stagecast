/* CellTree - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.block;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CellTree
{
    int axis;
    CellNode root;
    Vector roots;
    Hashtable cellPool;
    
    public CellTree(int i, int i_0_) {
	NodeValue nodevalue = new NodeValue();
	root = new CellNode(i, nodevalue, 0, i_0_);
	axis = i;
	roots = new Vector(4);
	roots.addElement(root);
	cellPool = new Hashtable();
    }
    
    public void updateNode(TableBlock.TableCell tablecell, int i, int i_1_) {
	NodeValue nodevalue = getNodeValue(tablecell, i, i_1_);
	boolean bool = false;
	Enumeration enumeration = roots.elements();
	while (enumeration.hasMoreElements()) {
	    CellNode cellnode = (CellNode) enumeration.nextElement();
	    bool |= cellnode.updateChild(nodevalue, i, i_1_);
	}
	if (!bool) {
	    NodeValue nodevalue_2_ = new NodeValue();
	    CellNode cellnode = new CellNode(axis, nodevalue_2_, 0, root.eIdx);
	    cellnode.updateChild(nodevalue, i, i_1_);
	    recallNodes(cellnode);
	    roots.addElement(cellnode);
	}
    }
    
    void recallNodes(CellNode cellnode) {
	Enumeration enumeration = cellPool.keys();
	while (enumeration.hasMoreElements()) {
	    Integer integer = (Integer) enumeration.nextElement();
	    int i = integer.intValue();
	    int i_3_ = i >> 10;
	    int i_4_ = i - (i_3_ << 10);
	    NodeValue nodevalue = (NodeValue) cellPool.get(integer);
	    cellnode.updateChild(nodevalue, i_3_, i_4_);
	}
    }
    
    public void updateCellValue(TableBlock.TableCell tablecell, int i,
				int i_5_) {
	Integer integer = new Integer((i << 10) + i_5_);
	NodeValue nodevalue = (NodeValue) cellPool.get(integer);
	if (nodevalue != null)
	    nodevalue.update(tablecell, axis);
	else
	    System.out.println("serious bug!!");
    }
    
    public void updateValues() {
	Enumeration enumeration = roots.elements();
	while (enumeration.hasMoreElements()) {
	    CellNode cellnode = (CellNode) enumeration.nextElement();
	    cellnode.relaxation();
	}
    }
    
    public void layoutNodes(int i) {
	Enumeration enumeration = roots.elements();
	while (enumeration.hasMoreElements()) {
	    CellNode cellnode = (CellNode) enumeration.nextElement();
	    cellnode.nValue.alloc = i;
	    cellnode.propogation();
	}
    }
    
    public int getMaximumSize() {
	int i = 0;
	Enumeration enumeration = roots.elements();
	while (enumeration.hasMoreElements()) {
	    CellNode cellnode = (CellNode) enumeration.nextElement();
	    i = Math.max(i, cellnode.getMaximumSize());
	}
	return i;
    }
    
    public int getMinimumSize() {
	int i = 0;
	Enumeration enumeration = roots.elements();
	while (enumeration.hasMoreElements()) {
	    CellNode cellnode = (CellNode) enumeration.nextElement();
	    i = Math.max(i, cellnode.getMinimumSize());
	}
	return i;
    }
    
    public int getPreferredSize() {
	int i = 0;
	Enumeration enumeration = roots.elements();
	while (enumeration.hasMoreElements()) {
	    CellNode cellnode = (CellNode) enumeration.nextElement();
	    i = Math.max(i, cellnode.getPreferredSize());
	}
	return i;
    }
    
    public void getLength(int[] is) {
	Enumeration enumeration = roots.elements();
	while (enumeration.hasMoreElements()) {
	    CellNode cellnode = (CellNode) enumeration.nextElement();
	    cellnode.getLength(is);
	}
    }
    
    NodeValue getNodeValue(TableBlock.TableCell tablecell, int i, int i_6_) {
	Integer integer = new Integer((i << 10) + i_6_);
	NodeValue nodevalue = (NodeValue) cellPool.get(integer);
	if (nodevalue == null) {
	    nodevalue = new NodeValue();
	    cellPool.put(integer, nodevalue);
	}
	nodevalue.update(tablecell, axis);
	return nodevalue;
    }
}
