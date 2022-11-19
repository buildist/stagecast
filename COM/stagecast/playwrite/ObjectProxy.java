/* ObjectProxy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ObjectProxy implements Externalizable, Resolvable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108754822450L;
    static Object[] registry;
    int id = 0;
    
    static void initStatics() {
	registry
	    = (new Object[]
	       { null, CocoaCharacter.SYS_NAME_VARIABLE_ID,
		 CocoaCharacter.SYS_APPEARANCE_VARIABLE_ID,
		 CocoaCharacter.SYS_SOUND_VARIABLE_ID,
		 CocoaCharacter.SYS_HORIZ_VARIABLE_ID,
		 CocoaCharacter.SYS_VERT_VARIABLE_ID,
		 CocoaCharacter.SYS_STAGE_VARIABLE_ID,
		 TextCharacterPrototype.SYS_TEXT_VARIABLE_ID,
		 SpecialPrototype.SYS_SPECIAL_WIDTH_VARIABLE_ID,
		 SpecialPrototype.SYS_SPECIAL_HEIGHT_VARIABLE_ID,
		 TextCharacterPrototype.SYS_TEXT_FONT_VARIABLE_ID,
		 TextCharacterPrototype.SYS_TEXT_SIZE_VARIABLE_ID,
		 TextCharacterPrototype.SYS_TEXT_COLOR_VARIABLE_ID,
		 TextCharacterPrototype.SYS_TEXT_BGCOLOR_VARIABLE_ID,
		 TextCharacterPrototype.SYS_TEXT_EDITABLE_VARIABLE_ID,
		 new boolean[][] { { true } },
		 new boolean[][] { { true }, { true } },
		 new boolean[][] { { true, true } },
		 new boolean[][] { { true, true }, { true, true } },
		 new boolean[][] { { true, true, true } },
		 new boolean[][] { { true, true, true },
				   { true, true, true } },
		 new boolean[][] { { true, true, true }, { true, true, true },
				   { true, true, true } },
		 new boolean[][] { { true }, { true }, { true } },
		 new boolean[][] { { true, true }, { true, true },
				   { true, true } },
		 PlaywriteSound.nullSound, World.SYS_WORLD_NAME_VARIABLE_ID,
		 World.SYS_ENABLE_SOUND_VARIABLE_ID,
		 World.SYS_SHOW_ALL_ACTIONS_VARIABLE_ID,
		 World.SYS_RUN_ALL_STAGES_VARIABLE_ID,
		 World.SYS_ENABLE_GRID_VARIABLE_ID,
		 World.SYS_FOLLOW_ME_VARIABLE_ID,
		 DoorPrototype.defaultDoorAppear,
		 TextCharacterPrototype.defaultTextAppear,
		 Stage.SYS_BACKGROUND_VARIABLE_ID,
		 Stage.SYS_BG_COLOR_VARIABLE_ID, Stage.SYS_WRAP_H_VARIABLE_ID,
		 Stage.SYS_WRAP_V_VARIABLE_ID, Stage.SYS_WIDTH_VARIABLE_ID,
		 Stage.SYS_HEIGHT_VARIABLE_ID,
		 Stage.SYS_SQUARE_SIZE_VARIABLE_ID,
		 BackgroundImage.noBackground, BackgroundImage.noBackground,
		 BackgroundImage.noBackground,
		 World.SYS_FRAME_RATE_VARIABLE_ID, World.SYS_SPEED_VARIABLE_ID,
		 World.SYS_HISTORY_SIZE_VARIABLE_ID,
		 World.SYS_WINDOW_COLOR_VARIABLE_ID,
		 ColorValue.transparentColor,
		 Tutorial.SYS_TUTORIAL_FILE_VARIABLE_ID,
		 Variable.deletedVariable, Variable._SYSTEM,
		 Variable.UNBOUND });
    }
    
    static int indexFor(Object obj) {
	for (int i = 0; i < registry.length; i++) {
	    if (obj == registry[i])
		return i;
	}
	return -1;
    }
    
    public Object resolve(WorldBuilder wb) {
	Object resolution;
	try {
	    resolution = registry[id];
	} catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
	    throw new UnknownVersionError("ObjectProxy", id, registry.length);
	}
	if (resolution == DoorPrototype.defaultDoorAppear
	    || resolution == TextCharacterPrototype.defaultTextAppear) {
	    Appearance old = (Appearance) resolution;
	    resolution = new Appearance(old.getName(), old.getBitmap(),
					old.getSquareSize(),
					(Shape) old.getShape().clone());
	}
	return resolution;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	ASSERT.isTrue(false);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	id = in.readInt();
    }
}
