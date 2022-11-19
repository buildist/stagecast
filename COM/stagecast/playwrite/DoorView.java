/* DoorView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.DragSession;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MouseEvent;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class DoorView extends CharacterView
    implements Debug.Constants, ResourceIDs.DoorIDs
{
    static final String SHOW_OTHER_END = "show other end";
    private transient PlaywriteView _arrowView = null;
    
    DoorView(CocoaCharacter door, int scaleSize) {
	super(door, scaleSize);
	if (((Door) door).isDestinationEnd())
	    _arrowView
		= new PlaywriteView(Resource.getImage("door left arrow"));
	else {
	    _arrowView
		= new PlaywriteView(Resource.getImage("door right arrow"));
	    this.allowDragInto(CocoaCharacter.class, this);
	}
    }
    
    DoorView(CocoaCharacter door) {
	this(door, 0);
    }
    
    public boolean dragDropped(DragSession session) {
	View draggee = this.viewBeingDragged(session);
	CharacterView cView = null;
	Debug.print("debug.door", "dropping " + draggee);
	if (!(draggee instanceof CharacterView))
	    return super.dragDropped(session);
	cView = (CharacterView) draggee;
	CocoaCharacter ch = cView.getCharacter();
	Debug.print("debug.door", "dropped character = ", ch);
	Debug.print("debug.door", "destination = ", this);
	Debug.print("debug.door", "door = ", this.getCharacter());
	ch.setVisibility(true);
	GeneralizedCharacter doorGC;
	DoorInstance door;
	if (this.getCharacter() instanceof DoorInstance) {
	    door = (DoorInstance) this.getCharacter();
	    doorGC = new GeneralizedCharacter(door);
	} else if (this.getCharacter() instanceof GCAlias) {
	    doorGC = ((GCAlias) this.getCharacter()).findOriginal();
	    if (doorGC.dereference() instanceof DoorInstance)
		door = (DoorInstance) doorGC.dereference();
	    else if (RuleEditor.isRuleEditing())
		door = null;
	    else
		return false;
	} else
	    return false;
	World world;
	if (door == null)
	    world = ch.getWorld();
	else {
	    door.unhighlightForSelection();
	    world = door.getWorld();
	}
	GeneralizedCharacter gch;
	if (ch instanceof CharacterInstance) {
	    if (ch.getWorld() != world)
		ch = (CharacterInstance) ((CharacterInstance) ch).copy(world);
	    gch = new GeneralizedCharacter((CharacterInstance) ch);
	} else if (ch instanceof CharacterPrototype) {
	    gch = new GeneralizedCharacter((CharacterPrototype) ch);
	    Point pt = new Point(door.getH(), door.getV());
	    RuleEditor.makeRelativeToSelf(pt);
	    RuleAction createAction = new CreateAction(gch, pt.x, pt.y, -1);
	    world.doManualAction(createAction, door.getCharContainer());
	} else if (ch instanceof GCAlias) {
	    if (!RuleEditor.isRecordingOrEditing())
		return false;
	    GCAlias alias = (GCAlias) ch;
	    gch = alias.findOriginal();
	} else
	    return false;
	world.doManualAction(new TeleportAction(gch, doorGC));
	Selection.hideModalView();
	world.setModified(true);
	return true;
    }
    
    public boolean mouseDown(MouseEvent event) {
	if (this.getCharacter() instanceof Selectable
	    && Selection.isSelected((Selectable) this.getCharacter())
	    && _arrowView != null
	    && _arrowView.bounds.contains(event.x, event.y)) {
	    performCommand("show other end", null);
	    return false;
	}
	return super.mouseDown(event);
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("show other end")) {
	    CocoaCharacter character = this.getCharacter();
	    CharacterContainer container = character.getCharContainer();
	    if (character instanceof Door) {
		Door door = (Door) character;
		CocoaCharacter otherEnd = (CocoaCharacter) door.getOtherEnd();
		this.selectModel(null);
		container.makeVisible(otherEnd);
	    }
	} else
	    super.performCommand(command, data);
    }
    
    public void drawView(Graphics g) {
	super.drawView(g);
	drawArrow(this.getCharacter(), g, 0, 0);
    }
    
    public void ancestorWasAddedToViewHierarchy(View view) {
	super.ancestorWasAddedToViewHierarchy(view);
	if (!((CocoaCharacter) this.getModelObject()).isVisible()) {
	    this.saveSize();
	    this.sizeTo(0, 0);
	}
    }
    
    public void draw(CocoaCharacter ch, Graphics g, int x, int y,
		     int squareSize) {
	if (!ch.isInvisible()) {
	    super.draw(ch, g, x, y, squareSize);
	    drawArrow(ch, g, x, y);
	}
    }
    
    private void drawArrow(CocoaCharacter ch, Graphics g, int x, int y) {
	if (ch instanceof Selectable
	    && Selection.isSelected((Selectable) ch)) {
	    int w;
	    int h;
	    if (bounds.width < 32 || bounds.height < 32) {
		w = bounds.width * 43 / 100;
		h = bounds.height * 43 / 100;
	    } else {
		w = _arrowView.image().width();
		h = _arrowView.image().height();
	    }
	    _arrowView.sizeTo(w, h);
	    if (((Door) ch.getPrototype()).isDestinationEnd())
		_arrowView.moveTo(0,
				  (this.height() - _arrowView.height()) / 2);
	    else
		_arrowView.moveTo(this.width() - _arrowView.width(),
				  (this.height() - _arrowView.height()) / 2);
	    _arrowView.image().drawScaled(g, x + _arrowView.x(),
					  y + _arrowView.y(), w, h);
	}
    }
    
    public void hilite() {
	super.hilite();
    }
    
    public String toString() {
	String result = null;
	try {
	    result = "<DoorView of " + this.getCharacter() + ">";
	} catch (Exception exception) {
	    result = super.toString();
	}
	return result;
    }
}
