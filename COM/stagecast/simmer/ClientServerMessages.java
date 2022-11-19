/* ClientServerMessages - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.simmer;

public interface ClientServerMessages
{
    public static final int CLIENT_PROTOCOL_VERSION = 2;
    public static final int MESSAGE_PORT = 8189;
    public static final String CLIENT_ALIVE = "CA";
    public static final String ACK_TICK = "AK";
    public static final String WORLD_REQUEST = "LA";
    public static final String MOUSE_EVENT = "MO";
    public static final String KEY_EVENT = "KE";
    public static final String SIM_AHEAD = "SA";
    public static final String SET_VAR = "SV";
    public static final String MEDIA_REQUEST = "MR";
    public static final String CLIENT_EXIT = "CE";
    public static final String CREATE_OBJ = "CR";
    public static final String DESTROY_OBJ = "DE";
    public static final String SEND_MSG = "MS";
    public static final String DO_ACTION = "DO";
    public static final String PROGRESS = "PR";
    public static final String BATCH = "BC";
    public static final String BEGIN_WORLD = "BW";
    public static final String ERROR = "ER";
    public static final String MEDIA_ITEM = "MI";
    public static final String QUIT_WORLD = "QW";
    public static final String DRAW = "DW";
    public static final String SET_VAR_NUMBER = "SN";
    public static final String SET_VAR_STRING = "SS";
    public static final String SET_VAR_TRUE = "ST";
    public static final String SET_VAR_FALSE = "SF";
    public static final String SET_VAR_COLOR = "SC";
    public static final String SET_VAR_NULL = "S0";
    public static final String SET_VAR_OBJECT = "SO";
    public static final String MOUSE_UP = "UP";
    public static final String MOUSE_DOWN = "DN";
    public static final String MOUSE_ENTER = "EN";
    public static final String MOUSE_LEAVE = "LV";
    public static final String MOUSE_DRAG = "DR";
    public static final String KEY_UP = "UP";
    public static final String KEY_DOWN = "DN";
    public static final String CONTROL = "C";
    public static final String ALT = "A";
    public static final String SHIFT = "S";
    public static final String WORLD = "WO";
    public static final String STAGE = "ST";
    public static final String SOUND = "SO";
    public static final String STD_CHAR = "CH";
    public static final String TEXT_CHAR = "TX";
    public static final String DOOR_CHAR = "DR";
    public static final String APPEARANCE = "AP";
    public static final String DISPLAY_VARIABLE = "DV";
    public static final String PIXMAP_BITMAP = "BP";
    public static final String NATIVE_BITMAP = "BN";
    public static final String NUMBER_VALUE = "NO";
    public static final String STRING_VALUE = "ST";
    public static final String BOOLEAN_VALUE = "BO";
    public static final String COLOR_VALUE = "CL";
    public static final String NULL_VALUE = "NL";
    public static final String OBJECT_VALUE = "OB";
    public static final Integer CLIENT_NULL_SOUND_ID = new Integer(0);
    public static final Integer CLIENT_FOLLOW_ME_VARIABLE_ID = new Integer(1);
    public static final Integer CLIENT_CENTER_FOLLOW_ME_VARIABLE_ID
	= new Integer(2);
    public static final Integer CLIENT_SIM_AHEAD_VARIABLE_ID = new Integer(3);
    public static final Integer CLIENT_FRAME_RATE_VARIABLE_ID = new Integer(4);
    public static final Integer CLIENT_SPEED_VARIABLE_ID = new Integer(5);
    public static final Integer CLIENT_NAME_VARIABLE_ID = new Integer(6);
    public static final Integer CLIENT_APPEARANCE_VARIABLE_ID = new Integer(7);
    public static final Integer CLIENT_SOUND_VARIABLE_ID = new Integer(8);
    public static final Integer CLIENT_HORIZ_VARIABLE_ID = new Integer(9);
    public static final Integer CLIENT_VERT_VARIABLE_ID = new Integer(10);
    public static final Integer CLIENT_STAGE_VARIABLE_ID = new Integer(11);
    public static final Integer CLIENT_VISIBLE_VARIABLE_ID = new Integer(12);
    public static final Integer CLIENT_BACKGROUND_VARIABLE_ID
	= new Integer(13);
    public static final Integer CLIENT_BACKGROUND_ALIGNMENT_VARIABLE_ID
	= new Integer(14);
    public static final Integer CLIENT_BG_COLOR_VARIABLE_ID = new Integer(15);
    public static final Integer CLIENT_WIDTH_VARIABLE_ID = new Integer(16);
    public static final Integer CLIENT_HEIGHT_VARIABLE_ID = new Integer(17);
    public static final Integer CLIENT_SQUARE_SIZE_VARIABLE_ID
	= new Integer(18);
    public static final Integer CLIENT_SPECIAL_WIDTH_VARIABLE_ID
	= new Integer(19);
    public static final Integer CLIENT_SPECIAL_HEIGHT_VARIABLE_ID
	= new Integer(20);
    public static final Integer CLIENT_TEXT_VARIABLE_ID = new Integer(21);
    public static final Integer CLIENT_TEXT_EDITABLE_VARIABLE_ID
	= new Integer(22);
    public static final Integer CLIENT_TEXT_FONT_VARIABLE_ID = new Integer(23);
    public static final Integer CLIENT_TEXT_SIZE_VARIABLE_ID = new Integer(24);
    public static final Integer CLIENT_TEXT_ALIGNMENT_VARIABLE_ID
	= new Integer(25);
    public static final Integer CLIENT_TEXT_OFFSET_X_VARIABLE_ID
	= new Integer(26);
    public static final Integer CLIENT_TEXT_OFFSET_Y_VARIABLE_ID
	= new Integer(27);
    public static final Integer CLIENT_TEXT_COLOR_VARIABLE_ID
	= new Integer(28);
    public static final Integer CLIENT_TEXT_BGCOLOR_VARIABLE_ID
	= new Integer(29);
    public static final Integer CLIENT_TEXT_BORDER_VARIABLE_ID
	= new Integer(30);
    public static final Integer CLIENT_TEXT_SHRINKTOFIT_VARIABLE_ID
	= new Integer(31);
    public static final Integer CLIENT_ROLLOVER_APPEARANCE_VARIABLE_ID
	= new Integer(32);
    public static final Integer CLIENT_ROLLOVER_ENABLED_VARIABLE_ID
	= new Integer(33);
    public static final Integer CLIENT_PLAY_SOUNDS_VARIABLE_ID
	= new Integer(34);
    public static final Integer CLIENT_TEXT_STYLE_VARIABLE_ID
	= new Integer(35);
    public static final int FIRST_MAP_ID = 36;
    public static final String CLIENT_SET_Z_ACTION_ID = "SZ";
    public static final String CLIENT_NUMBER_OF_VISIBLE_REGIONS_ACTION_ID
	= "VR";
    public static final String CLIENT_SET_VISIBLE_STAGE_ACTION_ID = "SV";
    public static final String CLIENT_RESET_ACTION_ID = "RE";
    public static final String CLIENT_OPEN_URL_ACTION_ID = "OU";
    public static final String CLIENT_BG_CENTER_VAL = "center";
    public static final String CLIENT_BG_TILED_VAL = "tiled";
    public static final String CLIENT_BG_SCALED_VAL = "scaled";
    public static final int CLIENT_SLOW_SPEED = 0;
    public static final int CLIENT_MEDIUM_SPEED = 1;
    public static final int CLIENT_FAST_SPEED = 2;
    public static final int CLIENT_FULL_SPEED = 3;
    public static final long CLIENT_SLOW_DIVISOR = 3L;
    public static final long CLIENT_FAST_MULTIPLIER = 3L;
}
