/* ListView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.Vector;

public class ListView extends View implements Target, FormElement
{
    ListItem protoItem;
    ListItem anchorItem;
    ListItem origSelectedItem;
    Vector items = new Vector();
    Vector selectedItems = new Vector();
    Vector dirtyItems = new Vector();
    String command;
    String doubleCommand;
    Target target;
    Color backgroundColor = Color.lightGray;
    int rowHeight = 17;
    boolean allowsMultipleSelection;
    boolean allowsEmptySelection;
    boolean tracksMouseOutsideBounds = true;
    boolean tracking;
    boolean enabled = true;
    boolean transparent = false;
    public static String SELECT_NEXT_ITEM = "selectNext";
    public static String SELECT_PREVIOUS_ITEM = "selectPrevious";
    
    public ListView() {
	this(0, 0, 0, 0);
    }
    
    public ListView(Rect rect) {
	this(rect.x, rect.y, rect.width, rect.height);
    }
    
    public ListView(int i, int i_0_, int i_1_, int i_2_) {
	super(i, i_0_, i_1_, i_2_);
	_setupKeyboard();
    }
    
    public void setPrototypeItem(ListItem listitem) {
	if (listitem == null)
	    protoItem = new ListItem();
	else
	    protoItem = listitem;
	if (protoItem.font() == null)
	    protoItem.setFont(Font.fontNamed("Default"));
	protoItem.setListView(this);
    }
    
    public ListItem prototypeItem() {
	if (protoItem == null)
	    setPrototypeItem(null);
	return protoItem;
    }
    
    public void setBackgroundColor(Color color) {
	backgroundColor = color;
    }
    
    public Color backgroundColor() {
	return backgroundColor;
    }
    
    public void setTransparent(boolean bool) {
	transparent = bool;
    }
    
    public boolean isTransparent() {
	return transparent;
    }
    
    public boolean wantsAutoscrollEvents() {
	return true;
    }
    
    public void setAllowsMultipleSelection(boolean bool) {
	allowsMultipleSelection = bool;
    }
    
    public boolean allowsMultipleSelection() {
	return allowsMultipleSelection;
    }
    
    public void setAllowsEmptySelection(boolean bool) {
	allowsEmptySelection = bool;
    }
    
    public boolean allowsEmptySelection() {
	return allowsEmptySelection;
    }
    
    public void setTracksMouseOutsideBounds(boolean bool) {
	tracksMouseOutsideBounds = bool;
    }
    
    public boolean tracksMouseOutsideBounds() {
	return tracksMouseOutsideBounds;
    }
    
    public void setEnabled(boolean bool) {
	if (enabled != bool) {
	    enabled = bool;
	    this.setDirty(true);
	}
    }
    
    public boolean isEnabled() {
	return enabled;
    }
    
    public void setRowHeight(int i) {
	if (i > 0)
	    rowHeight = i;
	else
	    rowHeight = minItemHeight();
    }
    
    public int rowHeight() {
	if (rowHeight > 0)
	    return rowHeight;
	setRowHeight(minItemHeight());
	return rowHeight;
    }
    
    public int minItemHeight() {
	int i = 0;
	int i_3_ = items.size();
	for (int i_4_ = 0; i_4_ < i_3_; i_4_++) {
	    ListItem listitem = (ListItem) items.elementAt(i_4_);
	    int i_5_ = listitem.minHeight();
	    if (i_5_ > i)
		i = i_5_;
	}
	return i;
    }
    
    public int minItemWidth() {
	int i = 0;
	int i_6_ = items.size();
	for (int i_7_ = 0; i_7_ < i_6_; i_7_++) {
	    ListItem listitem = (ListItem) items.elementAt(i_7_);
	    int i_8_ = listitem.minWidth();
	    if (i_8_ > i)
		i = i_8_;
	}
	return i;
    }
    
    public Size minSize() {
	return new Size(bounds.width, count() * rowHeight());
    }
    
    public ListItem itemForPoint(int i, int i_9_) {
	int i_10_ = items.size();
	if (rowHeight == 0)
	    return null;
	if (i_10_ == 0)
	    return null;
	if (!tracksMouseOutsideBounds
	    && !Rect.contains(0, 0, this.width(), this.height(), i, i_9_))
	    return null;
	int i_11_ = i_9_ / rowHeight;
	if (i_11_ < 0)
	    i_11_ = 0;
	else if (i_11_ >= i_10_)
	    i_11_ = i_10_ - 1;
	return itemAt(i_11_);
    }
    
    public ListItem itemAt(int i) {
	return (ListItem) items.elementAt(i);
    }
    
    public int indexOfItem(ListItem listitem) {
	return items.indexOf(listitem);
    }
    
    public Rect rectForItem(ListItem listitem) {
	if (listitem == null)
	    return null;
	return rectForItemAt(items.indexOf(listitem));
    }
    
    public Rect rectForItemAt(int i) {
	if (i < 0 || i >= items.size())
	    return null;
	return new Rect(0, rowHeight * i, bounds.width, rowHeight);
    }
    
    public boolean multipleItemsSelected() {
	return selectedItems.size() > 1;
    }
    
    public int selectedIndex() {
	ListItem listitem = selectedItem();
	if (listitem == null)
	    return -1;
	return items.indexOf(listitem);
    }
    
    public ListItem selectedItem() {
	if (selectedItems.size() > 0)
	    return (ListItem) selectedItems.elementAt(0);
	return null;
    }
    
    public Vector selectedItems() {
	return selectedItems;
    }
    
    public int count() {
	return items.size();
    }
    
    public ListItem addItem() {
	return insertItemAt(items.size());
    }
    
    public ListItem addItem(ListItem listitem) {
	if (listitem.font() == null)
	    listitem.setFont(Font.defaultFont());
	listitem.setListView(this);
	return insertItemAt(listitem, items.size());
    }
    
    public ListItem insertItemAt(ListItem listitem, int i) {
	if (listitem.font() == null)
	    listitem.setFont(Font.defaultFont());
	listitem.setListView(this);
	items.insertElementAt(listitem, i);
	if (!allowsEmptySelection && selectedItems.isEmpty())
	    selectItem(listitem);
	return listitem;
    }
    
    public ListItem insertItemAt(int i) {
	ListItem listitem = (ListItem) prototypeItem().clone();
	items.insertElementAt(listitem, i);
	if (!allowsEmptySelection && selectedItems.isEmpty())
	    selectItem(listitem);
	return listitem;
    }
    
    public void removeItemAt(int i) {
	ListItem listitem = (ListItem) items.elementAt(i);
	items.removeElementAt(i);
	selectedItems.removeElement(listitem);
	if (!allowsEmptySelection && selectedItems.size() == 0
	    && items.size() > 0) {
	    if (--i < 0)
		i = 0;
	    selectItem(itemAt(i));
	}
    }
    
    public void removeItem(ListItem listitem) {
	removeItemAt(items.indexOf(listitem));
    }
    
    public void removeAllItems() {
	items.removeAllElements();
	selectItem(null);
    }
    
    public void selectItem(ListItem listitem) {
	if (listitem != null && !listitem.isEnabled())
	    listitem = null;
	if (listitem == null) {
	    int i = selectedItems.size();
	    ListItem listitem_12_;
	    if (i > 0 && !allowsEmptySelection) {
		listitem_12_ = (ListItem) selectedItems.elementAt(i - 1);
		selectedItems.removeElementAt(i - 1);
		i--;
	    } else
		listitem_12_ = null;
	    for (int i_13_ = 0; i_13_ < i; i_13_++) {
		ListItem listitem_14_
		    = (ListItem) selectedItems.elementAt(i_13_);
		listitem_14_.setSelected(false);
		markDirty(listitem_14_);
	    }
	    selectedItems.removeAllElements();
	    if (listitem_12_ != null)
		selectedItems.addElement(listitem_12_);
	} else if (!selectedItems.contains(listitem)) {
	    if (!allowsMultipleSelection) {
		ListItem listitem_15_ = selectedItem();
		if (listitem_15_ != null) {
		    listitem_15_.setSelected(false);
		    selectedItems.removeElement(listitem_15_);
		    markDirty(listitem_15_);
		}
	    }
	    listitem.setSelected(true);
	    selectedItems.addElement(listitem);
	    markDirty(listitem);
	}
	drawDirtyItems();
    }
    
    public void selectItemAt(int i) {
	selectItem((ListItem) items.elementAt(i));
    }
    
    public void selectOnly(ListItem listitem) {
	boolean bool = false;
	if (listitem.isEnabled()) {
	    int i = selectedItems.size();
	    if (i != 1 || listitem != selectedItems.elementAt(0)) {
		for (int i_16_ = 0; i_16_ < i; i_16_++) {
		    ListItem listitem_17_
			= (ListItem) selectedItems.elementAt(i_16_);
		    if (listitem_17_ != listitem) {
			listitem_17_.setSelected(false);
			markDirty(listitem_17_);
		    } else
			bool = true;
		}
		selectedItems.removeAllElements();
		selectedItems.addElement(listitem);
		if (!bool) {
		    listitem.setSelected(true);
		    markDirty(listitem);
		}
		drawDirtyItems();
	    }
	}
    }
    
    public void deselectItem(ListItem listitem) {
	if (listitem != null
	    && (selectedItems.size() != 1 || allowsEmptySelection)
	    && (items.contains(listitem)
		&& selectedItems.contains(listitem))) {
	    selectedItems.removeElement(listitem);
	    listitem.setSelected(false);
	    markDirty(listitem);
	    drawDirtyItems();
	}
    }
    
    public void scrollItemAtToVisible(int i) {
	this.scrollRectToVisible(rectForItemAt(i));
    }
    
    public void scrollItemToVisible(ListItem listitem) {
	this.scrollRectToVisible(rectForItem(listitem));
    }
    
    public boolean mouseDown(MouseEvent mouseevent) {
	if (!enabled)
	    return false;
	tracking = true;
	origSelectedItem = selectedItem();
	ListItem listitem = itemForPoint(mouseevent.x, mouseevent.y);
	if (listitem == null)
	    return true;
	if (anchorItem != listitem && mouseevent.clickCount > 1)
	    mouseevent.setClickCount(1);
	selectOnly(listitem);
	anchorItem = listitem;
	if (mouseevent.clickCount == 2) {
	    sendDoubleCommand();
	    return false;
	}
	return true;
    }
    
    public void mouseDragged(MouseEvent mouseevent) {
	if (tracking) {
	    this.disableDrawing();
	    ListItem listitem = itemForPoint(mouseevent.x, mouseevent.y);
	    if (!tracksMouseOutsideBounds
		&& !Rect.contains(0, 0, this.width(), this.height(),
				  mouseevent.x, mouseevent.y))
		listitem = null;
	    if (!allowsMultipleSelection) {
		if (listitem != selectedItem()
		    && (allowsEmptySelection || listitem != null)) {
		    anchorItem = listitem;
		    selectItem(listitem);
		}
	    } else {
		int i = items.indexOf(anchorItem);
		int i_18_;
		if (listitem != null)
		    i_18_ = items.indexOf(listitem);
		else if (pointCompare(0, this.height(), mouseevent.y) < 0)
		    i_18_ = 0;
		else
		    i_18_ = items.size() - 1;
		int i_19_;
		int i_20_;
		if (i < i_18_) {
		    i_19_ = i;
		    i_20_ = i_18_;
		} else {
		    i_19_ = i_18_;
		    i_20_ = i;
		}
		int i_21_ = selectedItems.size();
		while (i_21_-- > 0) {
		    ListItem listitem_22_
			= (ListItem) selectedItems.elementAt(i_21_);
		    int i_23_ = items.indexOf(listitem_22_);
		    if (listitem_22_.isSelected()
			&& (i_23_ < i_19_ || i_23_ > i_20_))
			deselectItem(listitem_22_);
		    else if (!listitem_22_.isSelected() && i_23_ >= i_19_
			     && i_23_ <= i_20_)
			selectItem(listitem_22_);
		}
		if (i_19_ != -1 && i_20_ != -1) {
		    for (i_21_ = i_19_; i_21_ <= i_20_; i_21_++) {
			ListItem listitem_24_
			    = (ListItem) items.elementAt(i_21_);
			if (!listitem_24_.isSelected())
			    selectItem(listitem_24_);
		    }
		}
	    }
	    this.reenableDrawing();
	    autoscroll(mouseevent);
	}
    }
    
    public void mouseUp(MouseEvent mouseevent) {
	if (mouseevent.clickCount == 1)
	    sendCommand();
	tracking = false;
    }
    
    private int pointCompare(int i, int i_25_, int i_26_) {
	if (i_26_ < i)
	    return -1;
	if (i_26_ >= i + i_25_)
	    return 1;
	return 0;
    }
    
    private int rectCompare(Rect rect, Rect rect_27_) {
	if (rect_27_.maxY() <= rect.y)
	    return -1;
	if (rect_27_.y >= rect.maxY())
	    return 1;
	return 0;
    }
    
    private void autoscroll(MouseEvent mouseevent) {
	Rect rect = Rect.newRect();
	this.computeVisibleRect(rect);
	drawDirtyItems();
	if (!rect.contains(mouseevent.x, mouseevent.y)) {
	    if (mouseevent.y < rect.y) {
		Rect rect_28_ = Rect.newRect(rect.x, mouseevent.y, rect.width,
					     rowHeight);
		this.scrollRectToVisible(rect_28_);
		Rect.returnRect(rect_28_);
	    } else if (mouseevent.y > rect.maxY()) {
		Rect rect_29_ = Rect.newRect(rect.x, mouseevent.y - rowHeight,
					     rect.width, rowHeight);
		this.scrollRectToVisible(rect_29_);
		Rect.returnRect(rect_29_);
	    }
	}
	Rect.returnRect(rect);
    }
    
    private void markDirty(ListItem listitem) {
	if (!dirtyItems.contains(listitem))
	    dirtyItems.addElement(listitem);
    }
    
    private void drawDirtyItems() {
	if (this.canDraw()) {
	    int i = dirtyItems.size();
	    if (i != 0) {
		Vector vector = dirtyItems;
		dirtyItems = null;
		Rect rect = Rect.newRect(0, 0, bounds.width, rowHeight);
		int i_30_ = count();
		for (int i_31_ = 0; i_31_ < i; i_31_++) {
		    int i_32_ = items.indexOf(vector.elementAt(i_31_));
		    if (i_32_ >= 0 && i_32_ < i_30_) {
			rect.y = i_32_ * rowHeight;
			rect.height = rowHeight;
			if (i_31_ < i - 1) {
			    int i_33_
				= items.indexOf(vector.elementAt(i_31_ + 1));
			    if (i_33_ == i_32_ + 1) {
				rect.height += rowHeight;
				i_31_++;
			    } else if (i_33_ == i_32_ - 1) {
				rect.height += rowHeight;
				rect.y -= rowHeight;
				i_31_++;
			    }
			}
			this.draw(rect);
		    }
		}
		Rect.returnRect(rect);
		dirtyItems = vector;
		dirtyItems.removeAllElements();
	    }
	}
    }
    
    public void drawItemAt(int i) {
	Rect rect = rectForItemAt(i);
	this.draw(rect);
    }
    
    public void drawViewBackground(Graphics graphics, int i, int i_34_,
				   int i_35_, int i_36_) {
	if (!isTransparent() && backgroundColor != null) {
	    graphics.setColor(backgroundColor);
	    graphics.fillRect(i, i_34_, i_35_, i_36_);
	}
    }
    
    public void drawView(Graphics graphics) {
	if (dirtyItems != null)
	    dirtyItems.removeAllElements();
	if (rowHeight <= 0)
	    drawViewBackground(graphics, 0, 0, bounds.width, bounds.height);
	else {
	    Rect rect = Rect.newRect(graphics.clipRect());
	    int i = items.size();
	    int i_37_ = rect.y / rowHeight;
	    if (i_37_ < 0 || i_37_ >= i) {
		drawViewBackground(graphics, 0, 0, bounds.width,
				   bounds.height);
		Rect.returnRect(rect);
	    } else {
		Rect rect_38_ = rectForItemAt(i_37_);
		boolean bool;
		for (bool = rectCompare(rect, rect_38_) == 0;
		     i_37_ < i && bool;
		     bool = rectCompare(rect, rect_38_) == 0) {
		    graphics.pushState();
		    graphics.setClipRect(rect_38_);
		    ListItem listitem = (ListItem) items.elementAt(i_37_);
		    if (!isTransparent() && listitem.isTransparent())
			drawViewBackground(graphics, rect_38_.x, rect_38_.y,
					   bounds.width, rowHeight);
		    listitem.drawInRect(graphics, rect_38_);
		    graphics.popState();
		    i_37_++;
		    rect_38_.moveBy(0, rowHeight);
		}
		if (bool) {
		    int i_39_ = bounds.height - rect_38_.y;
		    if (i_39_ > 0 && !isTransparent())
			drawViewBackground(graphics, rect_38_.x, rect_38_.y,
					   bounds.width, i_39_);
		}
		Rect.returnRect(rect);
	    }
	}
    }
    
    public void setTarget(Target target) {
	this.target = target;
    }
    
    public Target target() {
	return target;
    }
    
    public void setCommand(String string) {
	command = string;
    }
    
    public String command() {
	return command;
    }
    
    public void setDoubleCommand(String string) {
	doubleCommand = string;
    }
    
    public String doubleCommand() {
	return doubleCommand;
    }
    
    public void sendCommand() {
	if (target != null) {
	    String string = null;
	    ListItem listitem = selectedItem();
	    if (listitem != null)
		string = listitem.command();
	    if (string == null)
		string = command;
	    target.performCommand(string, this);
	}
    }
    
    public void sendDoubleCommand() {
	if (target != null && doubleCommand != null)
	    target.performCommand(doubleCommand, this);
    }
    
    public void performCommand(String string, Object object) {
	if (SELECT_NEXT_ITEM.equals(string))
	    selectNextItem(true);
	else if (SELECT_PREVIOUS_ITEM.equals(string))
	    selectNextItem(false);
    }
    
    void selectNextItem(boolean bool) {
	int i = selectedIndex();
	int i_40_ = count();
	if (bool) {
	    if (i < i_40_ - 1) {
		selectItemAt(i + 1);
		sendCommand();
	    }
	} else if (i > 0) {
	    selectItemAt(i - 1);
	    sendCommand();
	}
    }
    
    void _setupKeyboard() {
	this.removeAllCommandsForKeys();
	this.setCommandForKey(SELECT_NEXT_ITEM, 1005, 0);
	this.setCommandForKey(SELECT_PREVIOUS_ITEM, 1004, 0);
    }
    
    public boolean canBecomeSelectedView() {
	if (isEnabled())
	    return true;
	return false;
    }
    
    public String formElementText() {
	if (selectedItem() != null)
	    return selectedItem().title();
	return "";
    }
}
