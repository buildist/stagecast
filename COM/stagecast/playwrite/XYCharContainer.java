/* XYCharContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;

import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Target;

class XYCharContainer extends XYContainer
    implements CharacterContainer, Externalizable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108753839410L;
    
    XYCharContainer(World world, Class contentType) {
	super(world, contentType);
    }
    
    XYCharContainer(World world, Class contentType, int strict,
		    boolean primary) {
	super(world, contentType, strict, primary);
    }
    
    public XYCharContainer() {
	/* empty */
    }
    
    public void add(CocoaCharacter ch) {
	ch.setContainer(this);
	super.add(ch);
    }
    
    public void add(CocoaCharacter ch, int x, int y, int z) {
	ch.setContainer(this);
	super.add(ch, x, y);
	ch.setLocation(x, y);
    }
    
    public void remove(CocoaCharacter ch) {
	notifyViews(ch, 2);
	this.getContentsTable().remove(ch);
	ch.setContainer(null);
    }
    
    public void deleteCharacter(CocoaCharacter ch) {
	Point pt = this.getLocation(ch);
	ch.setOldX(pt.x);
	ch.setOldY(pt.y);
	ch.setOldZ(ch.getZ());
	remove(ch);
	ch.delete();
    }
    
    public void undeleteCharacter(CocoaCharacter ch) {
	ch.undelete();
	add(ch, ch.getOldX(), ch.getOldY(), ch.getOldZ());
    }
    
    public void update(CocoaCharacter ch, Variable v) {
	this.update(ch);
    }
    
    public void relocate(CocoaCharacter ch, int x, int y, int z) {
	this.moveTo(ch, x, y);
	ch.setLocation(x, y);
    }
    
    public void changeAppearance(CocoaCharacter ch, Appearance oldAppearance,
				 Appearance newAppearance) {
	this.update(ch);
    }
    
    public Point pixelOrigin(CocoaCharacter ch) {
	return this.getLocation(ch);
    }
    
    public int getZ(CocoaCharacter ch) {
	return 0;
    }
    
    public int setZ(CocoaCharacter ch, int z) {
	return 0;
    }
    
    public void makeVisible(CocoaCharacter ch) {
	/* empty */
    }
    
    void removeAll() {
	Object[] items = this.getContentsTable().keysArray();
	if (items != null) {
	    for (int i = 0; i < items.length; i++) {
		if (!(items[i] instanceof DoorPrototype)
		    || !((DoorPrototype) items[i]).isDestinationEnd())
		    deleteCharacter((CocoaCharacter) items[i]);
	    }
	}
    }
    
    void notifyViews(final Contained item, final int howChanged) {
	if (this.getWorld().inWorldThread())
	    this.getWorld().addSyncAction(new Target() {
		public void performCommand(String command, Object data) {
		    notifyViews(item, howChanged);
		}
	    }, null, null);
	else
	    super.notifyViews(item, howChanged);
    }
}
