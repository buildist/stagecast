/* DRTranslator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Date;
import java.util.Stack;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.operators.Op;
import COM.stagecast.operators.OperationManager;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class DRTranslator implements Debug.Constants, ResourceIDs.DialogIDs,
				     ResourceIDs.DRTranslatorIDs
{
    static final int LIST_TAG = 1818850164;
    static final int HANDLE_TAG = 1752065132;
    static final int INT_ARRAY_TAG = 1282303809;
    static final int NULL_TAG = 1853189228;
    static final int WRLH_TAG = 1467116616;
    static final int WINF_TAG = 1464430182;
    static final int TYPE_TAG = 1417244773;
    static final int PIEC_TAG = 1349084515;
    static final int WRLD_TAG = 1467116644;
    static final int BORD_TAG = 1114600036;
    static final int PCVR_TAG = 1348687474;
    static final int BTMP_TAG = 1114926448;
    static final int SOND_TAG = 1399811684;
    static final int SNDS_TAG = 1399743603;
    static final int TEXT_TAG = 1415936116;
    static final int STVL_TAG = 1400133228;
    static final int POCK_TAG = 1349477227;
    static final int TYPS_TAG = 1417244787;
    static final int BRDS_TAG = 1114793075;
    static final int APRS_TAG = 1097888371;
    static final int APPR_TAG = 1097887858;
    static final int RULE_TAG = 1383427173;
    static final int PROC_TAG = 1349676899;
    static final int RESP_TAG = 1382380400;
    static final int RSPS_TAG = 1383297139;
    static final int CHKS_TAG = 1130916723;
    static final int CHCK_TAG = 1130914667;
    static final int VARI_TAG = 1449226857;
    static final int MGRD_TAG = 1296527972;
    static final int MVAC_TAG = 1299595619;
    static final int CRAC_TAG = 1131561315;
    static final int DLAC_TAG = 1147945315;
    static final int CHAC_TAG = 1130905955;
    static final int DONT_TAG = 1148153460;
    static final int cocoaMatchSpecifier_AnyObject = 0;
    static final int cocoaMatchSpecifier_DontCare = -1;
    static final int cocoaMatchSpecifier_Type = -2;
    static final int cocoaWrapMode_None = 0;
    static final int cocoaWrapMode_Horizontal = 1;
    static final int cocoaWrapMode_Vertical = 2;
    static final int cocoaWrapMode_Both = 3;
    static final int cocoaProcedureMode_DoFirstMatch = 2;
    static final int cocoaProcedureMode_DoAllAndContinue = 4;
    static final int cocoaProcedureMode_ShuffleAndDoFirst = 3;
    static final int cocoaTrigger_WhenIdle = 0;
    static final int cocoaTrigger_WhenCreated = -1;
    static final int cocoaTrigger_WhenDestroyed = -2;
    static final int cocoaTrigger_WhenClicked = -3;
    static final int cocoaValueType_Board = -8;
    static final int cocoaValueType_Type = -7;
    static final int cocoaValueType_Color = -6;
    static final int cocoaValueType_Appearance = -5;
    static final int cocoaValueType_Sound = -4;
    static final int cocoaValueType_Enumeration = -3;
    static final int cocoaValueType_Collection = -2;
    static final int cocoaValueType_Reference = -1;
    static final int cocoaValueType_Null = 0;
    static final int cocoaValueType_Boolean = 1;
    static final int cocoaValueType_String = 2;
    static final int cocoaValueType_Integer = 3;
    static final int cocoaValueType_Calculation = 4;
    static final int cocoaValueType_Real = 5;
    static final int cocoaSpecifier_Name = -1;
    static final int cocoaSpecifier_Type = -2;
    static final int cocoaSpecifier_Sound = -90;
    static final int cocoaSpecifier_Appearance = -5;
    static final int cocoaSpecifier_Background = -6;
    static final int cocoaSpecifier_Height = -7;
    static final int cocoaSpecifier_Width = -8;
    static final int cocoaSpecifier_Row = -11;
    static final int cocoaSpecifier_Column = -12;
    static final int cocoaSpecifier_Types = -70;
    static final int cocoaSpecifier_Boards = -80;
    static final int cocoaSpecifier_Appearances = -5;
    static final int cocoaSpecifier_Sounds = -90;
    static final int cocoaSpecifier_World = 1196913260;
    static final int cocoaComparator_Equal = 0;
    static final int cocoaComparator_NotEqual = 1;
    static final int cocoaComparator_GreaterThan = 2;
    static final int cocoaComparator_LessThan = 3;
    static final int cocoaComparator_GreaterThanOrEqual = 4;
    static final int cocoaComparator_LessThanOrEqual = 5;
    static final int cocoaComparator_Contains = 6;
    static final int cocoaComparator_IsContainedIn = 7;
    static final int cocoaComparator_StartsWith = 8;
    static final int cocoaComparator_EndsWith = 9;
    static final int cocoaContext_CurrentBoard = -100;
    static final int cocoaContext_Vacuum = -101;
    static final int cocoaContext_Calculator = -102;
    static final int cocoaContext_World = -103;
    static final int cocoaContext_Types = -104;
    static final int cocoaContext_Jar = -105;
    static final int cocoaChangeMode_Subtract = -1;
    static final int cocoaChangeMode_Replace = 0;
    static final int cocoaChangeMode_Add = 1;
    static final boolean DEBUGINT = true;
    static final boolean DEBUGBOOLEAN = true;
    static final boolean DEBUGPOINT = true;
    static final boolean DEBUGLIST = true;
    static final boolean DEBUGSTRING = true;
    static final boolean DEBUGRECT = true;
    static final boolean DEBUGCOLOR = true;
    private static boolean hasDuplicateVariables = false;
    boolean DEBUG = true;
    private World curWorld;
    private DRInputStream curStream;
    private Stack prototypeStack = new Stack();
    private ReferenceTable _refTable = new ReferenceTable();
    private ColorModel colorModel;
    private Vector _stageVariableAliases = null;
    private Vector _overwrittenCharacters = new Vector(10);
    private Vector _overwrittenObjects = new Vector(10);
    private Vector _badSounds = new Vector(10);
    private int nestLevel = 0;
    private int ruleNumber = 0;
    private int actionNumber = 0;
    private int variableNumber = 0;
    private Vector putActions;
    private Vector drRules;
    private Vector _protoList = new Vector(20);
    private Vector _variableAliases = new Vector(80);
    private Vector _gcList = new Vector(100);
    private StringBuffer _log = new StringBuffer(300);
    private boolean _mustDisplayLogWindow = false;
    
    class ReferenceTable
    {
	private Hashtable _allClassesTables = new Hashtable();
	
	class Reference
	{
	    private boolean _initialized = false;
	    private Object _object;
	    private Object _key;
	    
	    Reference(Class cl, Object key) {
		try {
		    _object = cl.newInstance();
		} catch (Exception exception) {
		    throw new PlaywriteInternalError
			      ("DRTranslator.getOrCreate: couldn't make an instance of "
			       + this.getClass());
		}
		ReferenceTable.this.addReference(cl, key, this);
		_key = key;
	    }
	    
	    boolean hasBeenInitialized() {
		return _initialized;
	    }
	    
	    Object getObject() {
		return _object;
	    }
	    
	    Object getKey() {
		return _key;
	    }
	    
	    void setInitialized() {
		if (_initialized)
		    throw new PlaywriteInternalError("object " + _object
						     + " initialized twice");
		_initialized = true;
	    }
	}
	
	private Hashtable getClassTable(Class cl) {
	    Hashtable classTable = (Hashtable) _allClassesTables.get(cl);
	    if (classTable == null) {
		classTable = new Hashtable();
		_allClassesTables.put(cl, classTable);
	    }
	    return classTable;
	}
	
	private void addReference(Class cl, Object key, Reference reference) {
	    Object oldObject = getClassTable(cl).put(key, reference);
	    if (oldObject != null)
		throw new RecoverableException("dialog badCW", true);
	}
	
	private Reference fetchReference(Class cl, Object key) {
	    if (key instanceof Integer && ((Integer) key).intValue() == 0)
		Debug.print("debug.dr", "creating object with ref 0:", cl);
	    Reference reference = (Reference) getClassTable(cl).get(key);
	    if (reference == null)
		reference = new Reference(cl, key);
	    return reference;
	}
	
	Object overwriteInitializedXName(int id, String objectName) {
	    Class cl = Stage.class;
	    Hashtable classTable = getClassTable(cl);
	    Integer objectID = new Integer(id);
	    Reference idReference = (Reference) classTable.get(objectID);
	    Reference nameReference = (Reference) classTable.get(objectName);
	    Reference reference = null;
	    if (idReference != null) {
		if (idReference.hasBeenInitialized()) {
		    reference = getReplacementReference(cl, idReference);
		    if (nameReference == null
			|| nameReference.hasBeenInitialized())
			getClassTable(cl).put(objectName, reference);
		} else
		    throw new PlaywriteInternalError
			      ("abuse of overwriteXName");
	    } else if (nameReference == null
		       || nameReference.hasBeenInitialized()) {
		reference = fetchReference(cl, objectID);
		getClassTable(cl).put(objectName, reference);
	    } else {
		reference = nameReference;
		getClassTable(cl).put(objectID, reference);
	    }
	    reference.setInitialized();
	    return reference.getObject();
	}
	
	Object fetch(Class cl, int id) {
	    return fetch(cl, new Integer(id));
	}
	
	Object fetch(Class cl, Object id) {
	    Reference reference = fetchReference(cl, id);
	    return reference.getObject();
	}
	
	Object fetchUninitialized(Class cl, int id) {
	    return fetchUninitialized(cl, new Integer(id));
	}
	
	Object fetchUninitialized(Class cl, Object id) {
	    Reference reference = fetchReference(cl, id);
	    reference.setInitialized();
	    return reference.getObject();
	}
	
	Object fetchNoCreate(Class cl, int ref) {
	    Reference reference
		= (Reference) getClassTable(cl).get(new Integer(ref));
	    return reference == null ? null : reference.getObject();
	}
	
	Object fetchNoCreate(Class cl, String ref) {
	    Reference reference = (Reference) getClassTable(cl).get(ref);
	    return reference == null ? null : reference.getObject();
	}
	
	Object overwriteInitialized(Class cl, int id) {
	    Integer key = new Integer(id);
	    return overwriteInitialized(cl, key);
	}
	
	Object overwriteInitialized(Class cl, Object key) {
	    Reference reference = fetchReference(cl, key);
	    if (reference.hasBeenInitialized())
		reference = getReplacementReference(cl, reference);
	    reference.setInitialized();
	    return reference.getObject();
	}
	
	Object overwrite(Class cl, int id) {
	    Integer key = new Integer(id);
	    Reference reference = fetchReference(cl, key);
	    if (reference.hasBeenInitialized())
		reference = getReplacementReference(cl, reference);
	    return reference.getObject();
	}
	
	void clear() {
	    Enumeration e = _allClassesTables.keys();
	    while (e.hasMoreElements())
		dropReferences((Class) e.nextElement());
	    _allClassesTables.clear();
	    _allClassesTables = null;
	}
	
	boolean dropReferences(Class cl) {
	    Hashtable classTable = (Hashtable) _allClassesTables.remove(cl);
	    if (classTable != null) {
		Debug.print("debug.dr", "dropping references to ", cl);
		Enumeration refList = classTable.elements();
		while (refList.hasMoreElements())
		    dropReference(cl, (Reference) refList.nextElement());
		classTable.clear();
	    }
	    return classTable != null;
	}
	
	private Reference getReplacementReference(Class cl,
						  Reference reference) {
	    Object key = reference.getKey();
	    dropReference(cl, reference);
	    Reference newReference = fetchReference(cl, key);
	    if (cl == CharacterInstance.class)
		_overwrittenCharacters.addElementIfAbsent(key);
	    else {
		_overwrittenObjects.addElementIfAbsent(reference.getObject());
		_overwrittenObjects
		    .addElementIfAbsent(newReference.getObject());
	    }
	    return newReference;
	}
	
	private void dropReference(Class cl, Reference reference) {
	    Object object = reference.getObject();
	    if (!reference.hasBeenInitialized()
		|| (object instanceof Verifiable
		    && ((Verifiable) object).isValid() == false)) {
		if (cl == GeneralizedCharacter.class)
		    _gcList.addElement(object);
		else if (cl == PlaywriteSound.class)
		    _badSounds.addElement(object);
		else if (cl == CharacterPrototype.class)
		    _protoList.addElementIfAbsent(object);
		else if (cl != Variable.class
			 || ((Variable) object).isValid() == false)
		    throw new RecoverableException("dialog badCW", true);
	    } else
		getClassTable(cl).remove(reference.getKey());
	}
    }
    
    static void setHasDuplicateVariables(boolean b) {
	hasDuplicateVariables = b;
    }
    
    public DRTranslator(DRInputStream inStream, World world) {
	putActions = new Vector(50);
	drRules = new Vector(10);
	curStream = inStream;
	curWorld = world;
	curWorld.setOriginalCreatorVersion("Cocoa DR3");
	int trans = 0;
	colorModel
	    = new IndexColorModel(8, 256, Util.r8, Util.g8, Util.b8, trans);
	DEBUG = Debug.lookup("debug.dr");
    }
    
    public final Object CreateObject(int classID) {
	Object obj = null;
	if (DEBUG)
	    nestLevel = nestLevel + 1;
	switch (classID) {
	case 1467116616:
	    if (DEBUG)
		printIndented(nestLevel, "CreateWorldHeader");
	    obj = CreateWorldHeaderStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateWorldHeader returns "
					  + safeToString(obj)));
	    break;
	case 1464430182:
	    if (DEBUG)
		printIndented(nestLevel, "CreateWorldInfo");
	    obj = CreateWorldInfoStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateWorldInfo returns " + safeToString(obj));
	    break;
	case 1417244773:
	    if (DEBUG)
		printIndented(nestLevel, "CreatePieceType");
	    obj = CreatePieceTypeStream();
	    if (obj != null && obj instanceof CharacterPrototype)
		_protoList.addElement(obj);
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreatePieceType returns " + safeToString(obj));
	    break;
	case 1349084515:
	    if (DEBUG)
		printIndented(nestLevel, "CreatePiece");
	    obj = CreatePieceStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreatePiece returns " + safeToString(obj));
	    break;
	case 1467116644:
	    if (DEBUG)
		printIndented(nestLevel, "CreateWorld");
	    obj = CreateWorldStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateWorld returns " + safeToString(obj));
	    break;
	case 1114600036:
	    if (DEBUG)
		printIndented(nestLevel, "CreateMainBoard");
	    obj = CreateMainBoardStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateMainBoard returns " + safeToString(obj));
	    break;
	case 1348687474:
	    if (DEBUG)
		printIndented(nestLevel, "CreateGeneralizedPiece");
	    obj = CreateGeneralizedPieceStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateGeneralizedPiece returns "
					  + safeToString(obj)));
	    break;
	case 1114926448:
	    if (DEBUG)
		printIndented(nestLevel, "CreateBitmapData");
	    obj = CreateBitmapDataStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateBitmapData returns " + safeToString(obj));
	    break;
	case 1399811684:
	    if (DEBUG)
		printIndented(nestLevel, "CreateSound");
	    obj = CreateSoundStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateSound returns " + safeToString(obj));
	    break;
	case 1399743603:
	    if (DEBUG)
		printIndented(nestLevel, "CreateSounds");
	    obj = CreateSoundsStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateSounds returns " + safeToString(obj));
	    break;
	case 1400133228:
	case 1415936116:
	    if (DEBUG)
		printIndented(nestLevel, "CreateStringValue");
	    obj = CreateStringValueStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateStringValue returns "
					  + safeToString(obj)));
	    break;
	case 1349477227:
	    if (DEBUG)
		printIndented(nestLevel, "CreateCollectionValue");
	    obj = CreateCollectionValueStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateCollectionValue returns "
					  + safeToString(obj)));
	    break;
	case 1417244787:
	    if (DEBUG)
		printIndented(nestLevel, "CreateTypes");
	    obj = CreateTypesStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateTypes returns " + safeToString(obj));
	    break;
	case 1114793075:
	    if (DEBUG)
		printIndented(nestLevel, "CreateBoards");
	    obj = CreateBoardsStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateBoards returns " + safeToString(obj));
	    break;
	case 1097888371:
	    if (DEBUG)
		printIndented(nestLevel, "CreateAppearances");
	    obj = CreateAppearancesStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateAppearances returns "
					  + safeToString(obj)));
	    break;
	case 1097887858:
	    if (DEBUG)
		printIndented(nestLevel, "CreateAppearance");
	    obj = CreateAppearanceStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateAppearance returns " + safeToString(obj));
	    break;
	case 1383427173:
	    if (DEBUG)
		printIndented(nestLevel, "CreateRule");
	    obj = CreateRuleStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateRule returns " + safeToString(obj));
	    break;
	case 1349676899:
	    if (DEBUG)
		printIndented(nestLevel, "CreateProcedure");
	    obj = CreateProcedureStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateProcedure returns " + safeToString(obj));
	    break;
	case 1382380400:
	    if (DEBUG)
		printIndented(nestLevel, "CreateResponse");
	    obj = CreateResponseStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateResponse returns " + safeToString(obj));
	    break;
	case 1383297139:
	    if (DEBUG)
		printIndented(nestLevel, "CreateResponses");
	    obj = CreateResponsesStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateResponses returns " + safeToString(obj));
	    break;
	case 1130916723:
	    if (DEBUG)
		printIndented(nestLevel, "CreateChecklist");
	    obj = CreateChecklistStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateChecklist returns " + safeToString(obj));
	    break;
	case 1130914667:
	    if (DEBUG)
		printIndented(nestLevel, "CreateCheck");
	    obj = CreateCheckStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateCheck returns " + safeToString(obj));
	    break;
	case 1449226857:
	    if (DEBUG)
		printIndented(nestLevel, "CreateVariable");
	    obj = CreateVariableStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateVariable returns "
					  + (obj instanceof Appearance
					     ? "<Appearance '" + obj + "'>"
					     : safeToString(obj))));
	    break;
	case 1296527972:
	    if (DEBUG)
		printIndented(nestLevel, "CreateMatchGrid");
	    obj = CreateMatchGridStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateMatchGrid returns " + safeToString(obj));
	    break;
	case 1299595619:
	    if (DEBUG)
		printIndented(nestLevel, "CreateMoveAction");
	    obj = CreateMoveActionStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      "CreateMoveAction returns " + safeToString(obj));
	    break;
	case 1131561315:
	    if (DEBUG)
		printIndented(nestLevel, "CreateCreateAction");
	    obj = CreateCreateActionStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateCreateAction returns "
					  + safeToString(obj)));
	    break;
	case 1147945315:
	    if (DEBUG)
		printIndented(nestLevel, "CreateDeleteAction");
	    obj = CreateDeleteActionStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateDeleteAction returns "
					  + safeToString(obj)));
	    break;
	case 1130905955:
	    if (DEBUG)
		printIndented(nestLevel, "CreateChangeAction");
	    obj = CreateChangeActionStream();
	    if (DEBUG)
		printIndented(nestLevel,
			      ("CreateChangeAction returns action "
			       + ((RuleAction) obj).getActionNumber() + " "
			       + safeToString(obj)));
	    break;
	case 1148153460:
	    if (DEBUG)
		printIndented(nestLevel, "CreateDontCareSquare");
	    obj = CreateDontCareSquareStream();
	    if (DEBUG)
		printIndented(nestLevel, ("CreateDontCareSquare returns "
					  + safeToString(obj)));
	    break;
	default:
	    throw new RecoverableException("dialog !cco", true);
	}
	nestLevel = nestLevel - 1;
	return obj;
    }
    
    public final Object CreateAppearanceStream() {
	int mObjectID = readInt("mObjectID");
	Point mLocation = readPoint("mLocation");
	String tmpString = readString();
	Appearance app
	    = ((Appearance)
	       _refTable.overwriteInitialized(Appearance.class, mObjectID));
	Vector mContents = readList("mContents");
	if (!mContents.isEmpty()) {
	    if (mContents.size() == 1) {
		Bitmap bitmap = (Bitmap) mContents.firstElement();
		int width = bitmap.width() / 32;
		if (bitmap.width() % 32 > 0)
		    width++;
		int height = bitmap.height() / 32;
		if (bitmap.height() % 32 > 0)
		    height++;
		app.fillInObject(tmpString, bitmap,
				 new Shape(width, height,
					   new Point(1, height)));
	    } else
		drWarning
		    ("DRTranslator.CreateAppearanceStream: wrong number of images for "
		     + app + ": " + mContents.size());
	}
	return app;
    }
    
    public final Object CreateAppearancesStream() {
	Vector mContents = readList("mContents");
	int mSpecifier = readInt("mSpecifier");
	return mContents;
    }
    
    public final Object CreateBitmapDataStream() {
	int mHeight = readInt("mHeight");
	int mWidth = readInt("mWidth");
	int mRowBytes = readInt("mRowBytes");
	int codecVersion = readInt("codecVersion");
	byte[] pict = curStream.readBytes();
	java.awt.Image image = null;
	byte[] pixData = new byte[mHeight * mWidth];
	int err = loadPixData(pict, pixData, mHeight, mWidth, mRowBytes,
			      codecVersion);
	Bitmap bitmap;
	if (err == 0) {
	    java.awt.image.ImageProducer source
		= new MemoryImageSource(mWidth, mHeight, colorModel, pixData,
					0, mWidth);
	    bitmap = BitmapManager.createBitmapManager(source);
	} else
	    throw new PlaywriteInternalError("couldn't load bitmap");
	return bitmap;
    }
    
    public final Object CreateBoardsStream() {
	Vector mContents = readList("mContents");
	int mSpecifier = readInt("mSpecifier");
	return mContents;
    }
    
    public final Object CreateChangeActionStream() {
	RuleAction action = null;
	boolean isAppearanceChange = false;
	VariableAlias mData = (VariableAlias) curStream.readObject();
	_variableAliases.addElement(mData);
	String varName = null;
	if (mData != null)
	    isAppearanceChange
		= mData.drIsSystemVar(CocoaCharacter
				      .SYS_APPEARANCE_VARIABLE_ID);
	Object mNewValue = curStream.readObject();
	if (DEBUG)
	    indentAndPrint("mNewValue = " + safeToString(mNewValue));
	if (isAppearanceChange || mNewValue instanceof Appearance)
	    Debug.print("debug.dr", "A NEW KIND OF APPEARANCE CHANGE!");
	isAppearanceChange
	    = isAppearanceChange || mNewValue instanceof Appearance;
	Object theNumber = mNewValue;
	int mMode = readInt("mMode");
	switch (mMode) {
	case 0:
	    if (isAppearanceChange)
		action = new PutAction(mData, mNewValue);
	    else if (mNewValue instanceof Stage)
		action = new DRTeleportAction(((Stage) mNewValue).getName());
	    else if (mData.drIsSystemVar(CocoaCharacter
					 .SYS_STAGE_VARIABLE_ID)) {
		if (mNewValue instanceof String)
		    action = new DRTeleportAction((String) mNewValue);
		else if (mNewValue instanceof VariableAlias) {
		    _variableAliases.addElement(mNewValue);
		    action = new DRTeleportAction((VariableAlias) mNewValue);
		} else {
		    drWarning
			("DRTranslator: CreateChangeActionStream: teleporting to unknown board "
			 + mNewValue);
		    action = new DRTeleportAction("");
		}
	    } else
		action = new PutAction(mData, mNewValue);
	    break;
	case 1: {
	    OperationManager addExp
		= new OperationManager(mData, theNumber, Op.Add);
	    action = new PutAction(mData, addExp);
	    break;
	}
	case -1: {
	    OperationManager subExp
		= new OperationManager(mData, theNumber, Op.Subtract);
	    action = new PutAction(mData, subExp);
	    break;
	}
	}
	action.setActionNumber(++actionNumber);
	if (action instanceof PutAction)
	    putActions.addElement(action);
	return action;
    }
    
    public final Object CreateCheckStream() {
	int mValueType = readInt("mValueType");
	Object mLeftOperand = curStream.readObject();
	int mComparator = readInt("mComparator");
	Object mRightOperand = curStream.readObject();
	boolean mIsEnabled = readBoolean("mIsEnabled");
	if (!mIsEnabled) {
	    if (DEBUG)
		drWarning("ignoring disabled check");
	    return null;
	}
	if (mLeftOperand instanceof String
	    && mRightOperand instanceof String) {
	    if (((String) mLeftOperand).equals("")
		&& ((String) mRightOperand).equals("")) {
		Debug.print("debug.dr", "HEY! EMPTY CHECK!");
		return null;
	    }
	    Debug.print("debug.dr", "two strings but not empty: 1 = ",
			mLeftOperand, ", 2 = ", mRightOperand);
	}
	Object test;
	switch (mComparator) {
	case 0:
	    test = new OperationManager(mLeftOperand, mRightOperand, Op.Equal);
	    break;
	case 1:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.NotEqual);
	    break;
	case 2:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.GreaterThan);
	    break;
	case 3:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.LessThan);
	    break;
	case 4:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.GreaterThanEq);
	    break;
	case 5:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.LessThanEq);
	    break;
	case 6:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.Contains);
	    break;
	case 7:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.ContainedBy);
	    break;
	case 8:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.StartsWith);
	    break;
	case 9:
	    test = new OperationManager(mLeftOperand, mRightOperand,
					Op.EndsWith);
	    break;
	default:
	    drWarning
		("DRTranslator.CreateCheckStream: unknown boolean operator type: "
		 + mComparator);
	    test = new OperationManager(mLeftOperand, mRightOperand, Op.Equal);
	}
	return new BooleanTest(test);
    }
    
    public final Object CreateChecklistStream() {
	Vector mContents = readList("mContents");
	boolean mIsEnabled = readBoolean("mIsEnabled");
	if (!mIsEnabled) {
	    if (DEBUG)
		drWarning
		    ("DRTranslator.CreateChecklistStream: disabled 'and if' test list");
	    mContents.removeAllElements();
	}
	return mContents;
    }
    
    public final Object CreateCreateActionStream() {
	int mData = readInt("mData(GeneralizedCharID)");
	GeneralizedCharacter charToCreateRef
	    = ((GeneralizedCharacter)
	       _refTable.fetch(GeneralizedCharacter.class, mData));
	Point mNewLocation = readPoint("mLocation");
	int mContext = readInt("mContext");
	CreateAction action = new CreateAction(charToCreateRef, mNewLocation.x,
					       -mNewLocation.y);
	return action;
    }
    
    public final Object CreateCollectionValueStream() {
	Vector mContents = readList("mContents");
	int mSpecifier = readInt("mSpecifier");
	return mContents;
    }
    
    public final Object CreateDeleteActionStream() {
	GeneralizedCharacter mData
	    = ((GeneralizedCharacter)
	       _refTable.fetch(GeneralizedCharacter.class, readInt("")));
	Point mNewLocation = readPoint("mNewLocation");
	int mContext = readInt("mContext");
	DeleteAction action = new DeleteAction(mData);
	return action;
    }
    
    public final Object CreateDontCareSquareStream() {
	int mObjectID = readInt("mObjectID");
	Point mLocation = readPoint("mLocation");
	GeneralizedCharacter gc = new DRDontCare();
	gc.setH(mLocation.x);
	gc.setV(-mLocation.y);
	readString();
	readList("mValues");
	readInt("mPieceType");
	readInt("mAppearance");
	readInt("mMatchSpecifier");
	return gc;
    }
    
    public final Object CreateGeneralizedPieceStream() {
	int mObjectID = readInt("mObjectID");
	Point mLocation = readPoint("mLocation");
	String tmpString = readString();
	Vector mValues = readList("mValues");
	int mPieceType = readInt("mPieceType");
	int mAppearance = readInt("mAppearance");
	Appearance appearance = null;
	if (mAppearance == 0) {
	    Debug.print("debug.dr", "zero appearance ID");
	    CharacterPrototype cp
		= ((CharacterPrototype)
		   _refTable.fetchNoCreate(CharacterPrototype.class,
					   mPieceType));
	    if (cp != null && cp.getWorld() != null)
		appearance = cp.getCurrentAppearance();
	} else
	    appearance
		= (Appearance) _refTable.fetch(Appearance.class, mAppearance);
	int mMatchSpecifier = readInt("mMatchSpecifier");
	GeneralizedCharacter gc
	    = ((GeneralizedCharacter)
	       _refTable.overwriteInitialized(GeneralizedCharacter.class,
					      mObjectID));
	gc.setH(mLocation.x);
	gc.setV(-mLocation.y);
	switch (mMatchSpecifier) {
	case -1:
	    Debug.print("debug.dr", " pieceType for Don't Care is:",
			mPieceType);
	    break;
	case -2:
	case 0: {
	    CharacterPrototype prototype
		= ((CharacterPrototype)
		   _refTable.fetchNoCreate(CharacterPrototype.class,
					   mPieceType));
	    if (prototype == null) {
		prototype
		    = ((CharacterPrototype)
		       _refTable.fetch(CharacterPrototype.class, mPieceType));
		prototype.fillInObject(curWorld, null, appearance);
	    }
	    gc.fillInObject(prototype);
	    prototype.findSystemVariable
		(CocoaCharacter.SYS_APPEARANCE_VARIABLE_ID)
		.setActualValue(gc, appearance);
	    gc.setOriginalAppearance(appearance);
	    if (mMatchSpecifier == 0)
		gc.setValueType(GeneralizedCharacter.anyType);
	    break;
	}
	default:
	    Debug.print("debug.dr", "Unknown MatchSpecifier: ",
			mMatchSpecifier);
	}
	return gc;
    }
    
    public final Object CreateMainBoardStream() {
	int mObjectID = readInt("");
	Point mLocation = readPoint("mLocation");
	String tmpString = readString();
	Stage stage = (Stage) _refTable.overwriteInitializedXName(mObjectID,
								  tmpString);
	stage.setName(tmpString);
	Vector mContents = readList("mContents");
	Point mDimensions = readPoint("mDimensions");
	Point mCellSize = readPoint("mCellSize");
	int mWrapMode = readInt("mWrapMode");
	if (mCellSize.x != mCellSize.y) {
	    drWarning
		("DRTranslator.CreateMainBoardStream: only square grids are supported");
	    mCellSize.y = mCellSize.x;
	}
	stage.fillInObject(mDimensions.x, mDimensions.y, curWorld,
			   mCellSize.x);
	switch (mWrapMode) {
	case 0:
	    stage.setWrapHorizontal(false);
	    stage.setWrapVertical(false);
	    break;
	case 1:
	    stage.setWrapHorizontal(true);
	    stage.setWrapVertical(false);
	    break;
	case 2:
	    stage.setWrapHorizontal(false);
	    stage.setWrapVertical(true);
	    break;
	case 3:
	    stage.setWrapHorizontal(true);
	    stage.setWrapVertical(true);
	    break;
	default:
	    drWarning("DRTranslator.CreateMainBoardStream: unknown wrap mode: "
		      + mWrapMode);
	    stage.setWrapHorizontal(true);
	    stage.setWrapVertical(true);
	}
	Color mBackColor = readColor("mBackColor");
	stage.setBackgroundColor(mBackColor);
	Point mEntrance = readPoint("mEntrance");
	mEntrance.x = mEntrance.x + 1;
	mEntrance.y = stage.numberOfRows() - mEntrance.y;
	stage.setEntrance(mEntrance);
	int[] mActivePieces = curStream.readInt32Array();
	for (int i = 0; i < mContents.size(); i++) {
	    CharacterInstance ch = (CharacterInstance) mContents.elementAt(i);
	    int h = ch.getH() + 1;
	    int v = stage.numberOfRows() - ch.getV();
	    stage.add(ch, h, v, -1);
	}
	return stage;
    }
    
    public final Object CreateMatchGridStream() {
	readInt("mObjectID");
	readPoint("mLocation");
	readString();
	Vector mContents = readList("mContents");
	Point mDimensions = readPoint("mDimensions");
	readPoint("mCellSize");
	readInt("mWrapMode");
	int mSelf = readInt("self GC");
	Point mOrigin = readPoint("mOrigin");
	readPoint("mDataOrigin");
	readRect("mMinimumRect");
	boolean mIsEnabled = readBoolean("mIsEnabled");
	if (!mIsEnabled)
	    drWarning
		("DRTranslator.CreateMatchGridStream: disabled patterns are not supported");
	readList("mPendingPieces");
	GeneralizedCharacter self
	    = ((GeneralizedCharacter)
	       _refTable.fetchNoCreate(GeneralizedCharacter.class, mSelf));
	if (self == null)
	    throw new PlaywriteInternalError
		      ("DRTranslator.CreateMatchGridStream: self doesn't exist");
	self.setH(1 - mOrigin.x);
	self.setV(mDimensions.y + mOrigin.y);
	Point selfLoc = new Point(self.getH(), self.getV());
	if (DEBUG)
	    indentAndPrint("self's " + mSelf + " location = "
			   + pointToString(selfLoc));
	boolean appearancesRead = true;
	for (int i = 0; i < mContents.size(); i++) {
	    GeneralizedCharacter gc
		= (GeneralizedCharacter) mContents.elementAt(i);
	    Point gcLoc = new Point(gc.getH(), gc.getV());
	    if (gc != self) {
		gcLoc.x = selfLoc.x + gcLoc.x;
		gcLoc.y = selfLoc.y + gcLoc.y;
		gc.setH(gcLoc.x);
		gc.setV(gcLoc.y);
	    }
	    if (!(gc instanceof DRDontCare)) {
		if (gc.getCurrentAppearance() == null)
		    appearancesRead = false;
		else if (gc.getCurrentAppearance().getBitmap() == null)
		    appearancesRead = false;
	    }
	    if (DEBUG)
		indentAndPrint(String.valueOf(gc) + " location = "
			       + pointToString(gcLoc));
	}
	if (appearancesRead)
	    return buildBindTests(mDimensions, mContents, self);
	return new DRRule(this, mDimensions, mContents, self);
    }
    
    Vector buildBindTests(Point mDimensions, Vector mContents,
			  GeneralizedCharacter self) {
	Point gcLoc = new Point(0, 0);
	Point selfLoc = new Point(self.getH(), self.getV());
	BeforeBoard beforeBoard
	    = new BeforeBoard(mDimensions.x, mDimensions.y, self, 32);
	for (int i = 0; i < mContents.size(); i++) {
	    GeneralizedCharacter gc
		= (GeneralizedCharacter) mContents.elementAt(i);
	    gcLoc = new Point(gc.getH(), gc.getV());
	    if (gc instanceof DRDontCare) {
		if (beforeBoard.isOnBoard(gcLoc.x, gcLoc.y))
		    beforeBoard.setDontCare(gcLoc.x, gcLoc.y, true);
		else
		    Debug.print("debug.dr", "bad dontcare coordinate: ", gcLoc,
				beforeBoard);
	    } else if (gc.getCurrentAppearance() == null)
		Debug.print("debug.dr", "gc ", gc, "has no appearance...");
	    else
		beforeBoard.add(gc, gcLoc.x, gcLoc.y, -1);
	}
	Vector bindTests = beforeBoard.compile(self);
	Point minxy = new Point(1 - gcLoc.x, 1 - gcLoc.y);
	Point maxxy
	    = new Point(mDimensions.x - gcLoc.x, mDimensions.y - gcLoc.y);
	bindTests.addElement(minxy);
	bindTests.addElement(maxxy);
	return bindTests;
    }
    
    void buildRule(Rule rule, Vector bindTests, Vector andIfTests,
		   Vector actions) {
	BindTest b = null;
	Point maxxy = (Point) bindTests.lastElement();
	bindTests.removeElementAt(bindTests.size() - 1);
	Point minxy = (Point) bindTests.lastElement();
	bindTests.removeElementAt(bindTests.size() - 1);
	for (int i = 0; i < bindTests.size(); i++) {
	    b = (BindTest) bindTests.elementAt(i);
	    rule.addTest(b);
	}
	for (int i = 0; i < andIfTests.size(); i++)
	    rule.addTest((RuleTest) andIfTests.elementAt(i));
	RuleAction ruleAction = null;
	for (int i = 0; i < actions.size(); i++) {
	    ruleAction = (RuleAction) actions.elementAt(i);
	    rule.addAction(ruleAction);
	}
	rule.drBuildBeforeBoard();
    }
    
    public final Object CreateMoveActionStream() {
	GeneralizedCharacter gc
	    = ((GeneralizedCharacter)
	       _refTable.fetch(GeneralizedCharacter.class, readInt("GC id")));
	Point mNewLocation = readPoint("mNewLocation");
	int mContext = readInt("mContext");
	RuleAction action;
	switch (mContext) {
	case -100:
	    action = new MoveAction(gc, mNewLocation.x, -mNewLocation.y);
	    break;
	case -101:
	    action = new DeleteAction(gc);
	    break;
	default:
	    drWarning
		("DRTranslator.CreateMoveActionStream: unknown type of action: "
		 + mContext);
	    action = new MoveAction(gc, mNewLocation.x, -mNewLocation.y);
	}
	return action;
    }
    
    public final Object CreatePieceTypeStream() {
	int mObjectID = readInt("mObjectID");
	Point mLocation = readPoint("mLocation");
	CharacterPrototype prototype
	    = ((CharacterPrototype)
	       _refTable.overwriteInitialized(CharacterPrototype.class,
					      mObjectID));
	prototypeStack.push(prototype);
	if (prototypeStack.size() > 1)
	    Debug.print("debug.dr", "nested prototypes!");
	String tmpString = readString();
	Vector mValues = readList("mValues");
	int mPieceType = readInt("mPieceType");
	int appID = readInt("appearance id");
	Appearance currentApp
	    = (Appearance) _refTable.overwrite(Appearance.class, appID);
	prototype.fillInObject(curWorld, tmpString, currentApp);
	unpackPrototypeVariables(prototype, mValues);
	int mModel = readInt("mModel");
	int mSerialization = readInt("mSerialization");
	Vector mAppearances = (Vector) curStream.readObject();
	if (mAppearances != null) {
	    for (int i = 0; i < mAppearances.size(); i++) {
		try {
		    prototype.add((Appearance) mAppearances.elementAt(i));
		} catch (BadBackpointerError badbackpointererror) {
		    throw new RecoverableException("dialog badCW", true);
		}
	    }
	    if (mAppearances.contains(currentApp))
		prototype.setCurrentAppearance(currentApp);
	    else {
		Debug.print("debug.dr", ("wacko appearance id " + appID
					 + " not in this proto id " + mObjectID
					 + " named " + tmpString));
		prototype.setCurrentAppearance((Appearance)
					       mAppearances.firstElement());
	    }
	}
	Vector mSounds = (Vector) curStream.readObject();
	Vector mResponses = (Vector) curStream.readObject();
	try {
	    prototype.setRules(mResponses);
	} catch (BadBackpointerError badbackpointererror) {
	    throw new RecoverableException("dialog badCW", true);
	}
	if (prototypeStack.pop() != prototype)
	    throw new PlaywriteInternalError("interleaved prototypes!!!");
	if (prototype.getMainSubroutine().findBadSubroutine(prototype) != null)
	    throw new RecoverableException("dialog badCW", true);
	return prototype;
    }
    
    public final Object CreatePieceStream() {
	int mObjectID = readInt("mObjectID");
	Point mLocation = readPoint("mLocation");
	String tmpString = readString();
	Vector mValues = readList("mValues");
	int mPieceType = readInt("mPieceType");
	int mAppearance = readInt("mAppearance");
	CharacterPrototype prototype
	    = ((CharacterPrototype)
	       _refTable.fetchNoCreate(CharacterPrototype.class, mPieceType));
	if (prototype == null)
	    throw new PlaywriteInternalError
		      ("DRTranslator.CreatePieceStream: null prototype");
	CharacterInstance character
	    = ((CharacterInstance)
	       _refTable.overwriteInitialized(CharacterInstance.class,
					      mObjectID));
	character.fillInObject(prototype);
	Appearance currentApp
	    = (Appearance) _refTable.fetch(Appearance.class, mAppearance);
	if (currentApp == null)
	    throw new PlaywriteInternalError
		      ("DRTranslator.CreatePieceStream: current appearance does not exist for "
		       + character);
	unpackVariables(character, mValues);
	prototype.findSystemVariable(CocoaCharacter.SYS_NAME_VARIABLE_ID)
	    .setActualValue(character, tmpString);
	prototype.findSystemVariable
	    (CocoaCharacter.SYS_APPEARANCE_VARIABLE_ID)
	    .setActualValue(character, currentApp);
	character.setH(mLocation.x);
	character.setV(mLocation.y);
	return character;
    }
    
    public final Object CreateProcedureStream() {
	Subroutine sub = null;
	int mObjectID = readInt("");
	Point mLocation = readPoint("mLocation");
	String tmpString = readString();
	Vector mContents = readList("mContents");
	boolean mIsEnabled = readBoolean("mIsEnabled");
	BeforeBoard mMatch = (BeforeBoard) curStream.readObject();
	Vector mChecklist = (Vector) curStream.readObject();
	int mMode = readInt("");
	sub = (Subroutine) _refTable.overwriteInitialized(Subroutine.class,
							  mObjectID);
	switch (mMode) {
	case 4:
	    sub.setType(new DoAllSubType());
	    break;
	case 3:
	    sub.setType(new RandomSubType());
	    break;
	default:
	    if (mMode != 2)
		drWarning
		    ("DRTranslator.CreateProcedureStream: unknown subroutine type: "
		     + mMode);
	    sub.setType(new NormalSubType());
	}
	sub.setName(tmpString);
	sub.setEnabled(mIsEnabled);
	int n = mContents.size();
	for (int i = 0; i < n; i++)
	    sub.add((RuleListItem) mContents.elementAt(i));
	if (mContents != null) {
	    for (int i = 0; i < mContents.size(); i++) {
		Object foo = mContents.elementAt(i);
		if (foo instanceof DRRule)
		    ((DRRule) foo).setRuleList(mContents);
	    }
	}
	return sub;
    }
    
    public final Object CreateResponseStream() {
	Subroutine sub = (Subroutine) CreateProcedureStream();
	int mTrigger = readInt("mTrigger");
	if (mTrigger == 0)
	    sub.setName("When I'm Waiting");
	else if (mTrigger == -3) {
	    if (sub.getPretest() == null)
		sub.addPretest(new Pretest((CharacterPrototype)
					   prototypeStack.peek()));
	    sub.addPretest(new DRMouseClickTest());
	    sub.setName("When I'm Clicked");
	} else if (mTrigger > 0) {
	    switch (mTrigger) {
	    case 28:
		mTrigger = 1006;
		break;
	    case 29:
		mTrigger = 1007;
		break;
	    case 30:
		mTrigger = 1004;
		break;
	    case 31:
		mTrigger = 1005;
		break;
	    }
	    if (sub.getPretest() == null)
		sub.addPretest(new Pretest((CharacterPrototype)
					   prototypeStack.peek()));
	    RuleTest keyTest = new KeyTest(mTrigger);
	    sub.addPretest(keyTest);
	    sub.setName("When " + keyTest.toString());
	} else
	    throw new PlaywriteInternalError
		      ("DRTranslator.CreateResponseStream: unknown trigger "
		       + mTrigger);
	return sub;
    }
    
    public final Object CreateResponsesStream() {
	Object deleted = new Object();
	Vector mContents = readList("mContents");
	_refTable.dropReferences(Subroutine.class);
	Vector finalList = new Vector(5);
	for (int i = 0; i < mContents.size(); i++) {
	    Subroutine sub = (Subroutine) mContents.elementAt(i);
	    if (sub.getPretest() != null) {
		finalList.addElement(sub);
		mContents.setElementAt(deleted, i);
	    } else if (sub.getRules().isEmpty() && sub.getName().equals(""))
		mContents.setElementAt(deleted, i);
	}
	for (int i = 0; i < mContents.size(); i++) {
	    if (mContents.elementAt(i) != deleted)
		finalList.addElement(mContents.elementAt(i));
	}
	return finalList;
    }
    
    void addDRRule(DRRule drRule) {
	drRules.addElement(drRule);
    }
    
    public final Object CreateRuleStream() {
	int mObjectID = readInt("");
	Point mLocation = readPoint("mLocation");
	GeneralizedCharacter self = null;
	if (_stageVariableAliases != null)
	    throw new PlaywriteInternalError("interleaved rule madness!");
	_stageVariableAliases = new Vector(10);
	String name = readString();
	Vector mContents = readList("mContents");
	boolean mIsEnabled = readBoolean("mIsEnabled");
	Object beforeBoard = curStream.readObject();
	Vector andIfTests = (Vector) curStream.readObject();
	Rule rule;
	if (beforeBoard instanceof Vector) {
	    Vector bindTests = (Vector) beforeBoard;
	    rule = new Rule();
	    buildRule(rule, bindTests, andIfTests, mContents);
	    self = rule.getSelf();
	} else if (beforeBoard instanceof DRRule) {
	    DRRule drRule = (DRRule) beforeBoard;
	    rule = drRule;
	    if (andIfTests == null)
		Debug.print("debug.dr", "And If tests are null");
	    if (mContents == null)
		Debug.print("debug.dr", "mContents is null");
	    drRule.setTests(andIfTests);
	    drRule.setActions(mContents);
	    Debug.print("debug.dr", "RuleStream returns a drRule!");
	    self = drRule.getTheSelf();
	} else {
	    drWarning("CREATING EMPTY RULE");
	    rule = new Rule();
	    Debug.print("debug.dr", "returning normal MT rule");
	}
	if (name == null || name.equals(""))
	    rule.setName(Resource.getText("drRM4") + " " + ++ruleNumber);
	else
	    rule.setName(name);
	if (!mIsEnabled)
	    Debug.print("debug.dr", "rule ", rule, " DISABLED");
	rule.setEnabled(mIsEnabled);
	if (self != null) {
	    int i = _stageVariableAliases.size();
	    while (i-- > 0) {
		VariableAlias alias
		    = (VariableAlias) _stageVariableAliases.removeElementAt(i);
		if (!alias.setOwner(self))
		    throw new PlaywriteInternalError
			      ("couldn't set VariableAlias for stage's owner...");
	    }
	} else if (_stageVariableAliases.size() != 0)
	    throw new PlaywriteInternalError
		      ("unable to resolve references to stage variable");
	_refTable.dropReferences(GeneralizedCharacter.class);
	_refTable.dropReferences(DRDontCare.class);
	_stageVariableAliases = null;
	return rule;
    }
    
    public final Object CreateSoundStream() {
	readInt("mObjectID");
	Point mLocation = readPoint("mLocation");
	String tmpString = readString();
	PlaywriteSound sound
	    = ((PlaywriteSound)
	       _refTable.overwriteInitialized(PlaywriteSound.class,
					      tmpString));
	int codecVersion = readInt("codecVersion");
	byte[] sndBytes = curStream.readBytes();
	SystemSound theSound = new SystemSound(sndBytes);
	sound.fillInObject(curWorld, tmpString, theSound);
	return sound;
    }
    
    public final Object CreateSoundsStream() {
	Vector mContents = readList("mContents");
	int mSpecifier = readInt("mSpecifier");
	return mContents;
    }
    
    public final Object CreateStringValueStream() {
	int mSpecifier = readInt("mSpecifier");
	String tmpString = readString();
	return new DRStringValue(mSpecifier, tmpString);
    }
    
    public final Object CreateTypesStream() {
	Vector mContents = readList("mContents");
	int mSpecifier = readInt("mSpecifier");
	return mContents;
    }
    
    public final Object CreateVariableStream() {
	Object operand = null;
	int mData = -1;
	int mValueType = readInt("mValueType");
	int mSpecifier = readInt("mSpecifier");
	if (mValueType == -1)
	    mData = readInt("mData");
	else
	    operand = curStream.readObject();
	switch (mValueType) {
	case 2:
	case 3:
	case 5: {
	    DRStringValue stringValue = (DRStringValue) operand;
	    operand = stringValue.getValue();
	    break;
	}
	case -5: {
	    DRStringValue stringValue = (DRStringValue) operand;
	    String name = stringValue.getValue().toString();
	    int specifier = stringValue.getSpecifier();
	    operand = _refTable.fetchNoCreate(Appearance.class, specifier);
	    if (operand != null) {
		if (!name.equalsIgnoreCase(((Appearance) operand).getName()))
		    operand = name;
	    } else if ((operand
			= _refTable.fetchNoCreate(CharacterPrototype.class,
						  specifier))
		       != null) {
		if (DEBUG)
		    drWarning("APPEARANCE REFERENCED VIA PROTOTYPE SPECIFIER: "
			      + specifier);
		CharacterPrototype prototype
		    = ((CharacterPrototype)
		       _refTable.fetch(CharacterPrototype.class, specifier));
		operand = prototype.getAppearanceNamed(name);
		if (operand == null)
		    operand = name;
	    } else
		operand = name;
	    break;
	}
	case -4: {
	    DRStringValue stringValue = (DRStringValue) operand;
	    String name = stringValue.getValue().toString();
	    int specifier = stringValue.getSpecifier();
	    operand = _refTable.fetchNoCreate(PlaywriteSound.class, name);
	    if (operand == null) {
		operand = _refTable.fetch(PlaywriteSound.class, name);
		((PlaywriteSound) operand).fillInObject(curWorld, name, null);
	    }
	    break;
	}
	case -1: {
	    CharacterPrototype proto = null;
	    VariableOwner owner;
	    if (mData == 1196913260)
		owner = curWorld;
	    else {
		GeneralizedCharacter gc
		    = ((GeneralizedCharacter)
		       _refTable.fetch(GeneralizedCharacter.class, mData));
		owner = gc;
	    }
	    switch (mSpecifier) {
	    case -1:
		operand
		    = new VariableAlias(owner,
					CocoaCharacter.SYS_NAME_VARIABLE_ID);
		break;
	    case -5:
		operand
		    = new VariableAlias(owner, (CocoaCharacter
						.SYS_APPEARANCE_VARIABLE_ID));
		break;
	    case -90:
		operand
		    = new VariableAlias(owner,
					CocoaCharacter.SYS_SOUND_VARIABLE_ID);
		break;
	    case -80:
		operand
		    = new VariableAlias(null,
					CocoaCharacter.SYS_STAGE_VARIABLE_ID);
		_stageVariableAliases.addElement(operand);
		break;
	    case -6:
		Debug.print
		    ("debug.dr",
		     "DRTranslator.CreateVariableStream: type specifier 'background' is not implemented");
		break;
	    case -7:
		Debug.print
		    ("debug.dr",
		     "DRTranslator.CreateVariableStream: type specifier 'height' is not implemented");
		break;
	    case -8:
		Debug.print
		    ("debug.dr",
		     "DRTranslator.CreateVariableStream: type specifier 'width' is not implemented");
		break;
	    case -11:
		Debug.print
		    ("debug.dr",
		     "DRTranslator.CreateVariableStream: type specifier 'row' is not implemented");
		break;
	    case -12:
		Debug.print
		    ("debug.dr",
		     "DRTranslator.CreateVariableStream: type specifier 'column' is not implemented");
		break;
	    case -70:
		Debug.print
		    ("debug.dr",
		     "DRTranslator.CreateVariableStream: type specifier 'types' is not implemented");
		break;
	    default: {
		Variable theVar
		    = (Variable) _refTable.fetch(Variable.class, mSpecifier);
		String varName = theVar.getName();
		if (varName == null)
		    varName = newVariableName(mSpecifier);
		ASSERT.isTrue(owner instanceof GeneralizedCharacter
			      || !(owner instanceof CocoaCharacter));
		theVar.drFillInObject(varName,
				      (owner instanceof CocoaCharacter
				       ? owner.getVariableListOwner() : owner),
				      null, Variable.STD_ACCESSOR, true);
		if (mData == 1196913260) {
		    owner = curWorld;
		    ASSERT.isTrue(curWorld.getVariableList()
				      .hasVariable(theVar));
		}
		operand = new VariableAlias(owner, theVar);
		if (owner != curWorld)
		    _variableAliases.addElement(operand);
		break;
	    }
	    case -2:
		break;
	    }
	    break;
	}
	case -8: {
	    DRStringValue stringValue = (DRStringValue) operand;
	    String stageName = stringValue.getValue().toString();
	    Stage stage = (Stage) _refTable.fetch(Stage.class, stageName);
	    stage.setName(stageName);
	    operand = stage;
	    break;
	}
	default:
	    drWarning("DRTranslator.CreateVariableStream: value type "
		      + mValueType + " is not implemented");
	}
	if (operand instanceof VariableAlias
	    && _variableAliases.indexOfIdentical(operand) == -1)
	    _variableAliases.addElement(operand);
	return operand;
    }
    
    public final Object CreateWorldStream() {
	if (curStream.getFormatVersion() >> 31 != 0)
	    return null;
	if (curStream.getFormatNumber() != 3)
	    return null;
	curStream.readObject();
	int mObjectID = readInt("");
	Point mLocation = readPoint("mLocation");
	String tmpString = readString();
	String titleString
	    = Resource.getTextAndFormat("drRM8",
					(new Object[]
					 { tmpString,
					   PlaywriteRoot.getProductName(),
					   PlaywriteRoot.getVersionString(),
					   PlaywriteRoot.getBuildString(),
					   new Date() }));
	logCR(false, titleString);
	logCR(false, Resource.getText("drRM0"));
	curWorld.setName(tmpString);
	Vector mValues = readList("mValues");
	int i = 0;
	while (i < mValues.size()) {
	    DRStringValue value = (DRStringValue) mValues.elementAt(i);
	    switch (value.getSpecifier()) {
	    default: {
		Variable theVar
		    = ((Variable)
		       _refTable.overwriteInitialized(Variable.class,
						      value.getSpecifier()));
		theVar.drFillInObject(("Global:"
				       + newVariableName(value
							     .getSpecifier())),
				      curWorld, null, Variable.STD_ACCESSOR,
				      true);
		curWorld.add(theVar);
		theVar.setValue(curWorld, value.getValue());
	    }
		/* fall through */
	    case -8:
	    case -7:
		i++;
	    }
	}
	Vector mTypes = (Vector) curStream.readObject();
	int i_0_ = _variableAliases.size();
	while (i_0_-- > 0) {
	    VariableAlias variableAlias
		= (VariableAlias) _variableAliases.elementAt(i_0_);
	    if (!variableAlias.drFixUpSystemVariable())
		Debug.print("debug.dr", "Unable to resolve: ", variableAlias);
	}
	int i_1_ = drRules.size();
	while (i_1_-- > 0) {
	    DRRule drRule = (DRRule) drRules.removeElementAt(i_1_);
	    Rule newRule = drRule.drCompile();
	    if (newRule == null) {
		Debug.print("debug.dr", "unable to compile rule ", drRule,
			    " deleting");
		drRule.delete();
	    } else {
		int ruleIndex = drRule.getIndex();
		Subroutine subroutine = drRule.getSubroutine();
		drRule.delete();
		subroutine.add(newRule, ruleIndex);
	    }
	}
	int i_2_ = putActions.size();
	while (i_2_-- > 0) {
	    PutAction putAction = (PutAction) putActions.removeElementAt(i_2_);
	    if (!putAction.isValid()) {
		IndexedContainer indexedContainer
		    = putAction.getIndexedContainer();
		Debug.print("debug.dr", "deleting malformed putaction ",
			    putAction, " from ", putAction.getRule());
		if (indexedContainer != null)
		    indexedContainer.forceRemove(putAction);
		else
		    Debug.print("debug.dr", "container is null!");
	    }
	}
	Vector mBoards = (Vector) curStream.readObject();
	int mCurrentBoard = readInt("mCurrentBoard");
	Stage stage
	    = (Stage) _refTable.fetchNoCreate(Stage.class, mCurrentBoard);
	if (stage == null)
	    throw new PlaywriteInternalError
		      ("DRTranslator.CreateWorldStream: couldn't find current stage");
	curWorld.setCurrentStageDRHack(stage);
	int mAvatar = readInt("mAvatar");
	if (mAvatar != 0) {
	    Integer mAvatarInteger = new Integer(mAvatar);
	    if (_overwrittenCharacters.contains(mAvatarInteger))
		logCR(true, Resource.getText("drRM12"));
	    else {
		CharacterInstance avatar
		    = ((CharacterInstance)
		       _refTable.fetchNoCreate(CharacterInstance.class,
					       mAvatar));
		if (avatar == null)
		    logCR(true, Resource.getText("drRM9"));
		else
		    curWorld.setMainCharacter(avatar);
	    }
	}
	_refTable.clear();
	int i_3_ = _gcList.size();
	while (i_3_-- > 0) {
	    GeneralizedCharacter gc
		= (GeneralizedCharacter) _gcList.elementAt(i_3_);
	    if (gc.getPrototype() == null) {
		Rule rule;
		while ((rule = curWorld.findRuleReferringTo(gc)) != null) {
		    rule.delete();
		    logDeletedRule(rule);
		}
	    }
	}
	Enumeration protos = curWorld.getPrototypes().getContents();
	while (protos.hasMoreElements()) {
	    CharacterPrototype prototype
		= (CharacterPrototype) protos.nextElement();
	    if (!prototype.isValid()) {
		logDeletedObject("drRM5", prototype);
		int count = curWorld.countRulesReferringTo(prototype);
		if (count > 0) {
		    logCR(true, (Resource.getTextAndFormat
				 ("drRM1",
				  new Object[] { new Integer(count),
						 getObjectName("drRM5",
							       prototype) })));
		    for (int i_4_ = 1; i_4_ <= count; i_4_++) {
			Rule rule = curWorld.findRuleReferringTo(prototype);
			log(true, String.valueOf(i_4_) + ". ");
			logDeletedRule(rule);
			rule.delete();
		    }
		}
		curWorld.remove(prototype);
		_protoList.removeElementIdentical(prototype);
	    }
	}
	if (_badSounds.size() != 0) {
	    for (int j = 0; j < _badSounds.size(); j++) {
		PlaywriteSound sound
		    = (PlaywriteSound) _badSounds.elementAt(j);
		logDeletedObject("drRM3", sound);
		int count = curWorld.countRulesReferringTo(sound);
		if (count > 0) {
		    logCR(true,
			  (Resource.getTextAndFormat
			   ("drRM1",
			    new Object[] { new Integer(count),
					   getObjectName("drRM3", sound) })));
		    for (int i_5_ = 1; i_5_ <= count; i_5_++) {
			Rule rule = curWorld.findRuleReferringTo(sound);
			log(true, String.valueOf(i_5_) + ". ");
			logDeletedRule(rule);
			rule.delete();
		    }
		}
		curWorld.remove(sound);
	    }
	}
	if (hasDuplicateVariables)
	    fixDuplicateVariables();
	if (_mustDisplayLogWindow) {
	    if (PlaywriteRoot.isAuthoring())
		displayLogWindow();
	    else
		PlaywriteDialog.warning(Resource.getText("drRM6"));
	}
	return curWorld;
    }
    
    private void fixDuplicateVariables() {
	int size = _protoList.size();
	for (int i = 0; i < size; i++) {
	    CharacterPrototype proto
		= (CharacterPrototype) _protoList.elementAt(i);
	    proto.getVariableList().drVerifyReplacements();
	}
	int i = _variableAliases.size();
	while (i-- > 0) {
	    VariableAlias variableAlias
		= (VariableAlias) _variableAliases.elementAt(i);
	    variableAlias.fixDuplicateVariable();
	}
	Variable.discardCocoaFixTable();
    }
    
    public final Object CreateWorldHeaderStream() {
	int mFormatVersion = readInt("mFormatVersion");
	curStream.setFormatVersion(mFormatVersion);
	int mPlaySpeed = readInt("mPlaySpeed");
	curWorld.setVersionNumber(mFormatVersion);
	return null;
    }
    
    public final Object CreateWorldInfoStream() {
	return null;
    }
    
    private int loadPixData(byte[] pict, byte[] pixData, int mHeight,
			    int mWidth, int mRowBytes, int codecVersion) {
	int err = 0;
	switch (codecVersion) {
	case 0:
	    err = loadUncompressedPixData(pict, pixData, mHeight, mWidth,
					  mRowBytes, codecVersion);
	    break;
	case 1:
	    err = loadPackedPixData(pict, pixData, mHeight, mWidth, mRowBytes,
				    codecVersion);
	    break;
	default:
	    err = -1;
	}
	return err;
    }
    
    private int loadPackedPixData(byte[] pict, byte[] pixData, int mHeight,
				  int mWidth, int mRowBytes,
				  int codecVersion) {
	int n = 0;
	int kTotal = 0;
	int k = 0;
	int m = 0;
	int dataSize = mHeight * mWidth;
	while (n < pict.length - 1 && m < dataSize) {
	    byte c = pict[n++];
	    if ((c & 0x80) != 0) {
		k = ((c ^ 0xffffffff) & 0xff) + 2;
		byte index = pict[n++];
		kTotal += k;
		for (int j = 0; j < k; j++) {
		    if (m >= dataSize)
			break;
		    pixData[m++] = index;
		}
	    } else {
		k = (c & 0xff) + 1;
		for (int j = 0; j < k && m < dataSize && n < pict.length;
		     j++) {
		    byte index = pict[n++];
		    pixData[m++] = index;
		}
		kTotal += k;
	    }
	}
	return 0;
    }
    
    private int loadUncompressedPixData(byte[] pict, byte[] pixData,
					int mHeight, int mWidth, int mRowBytes,
					int codecVersion) {
	return -1;
    }
    
    private final int byteValue(byte b) {
	if (b < 0)
	    return b + 256;
	return b;
    }
    
    public final void dropData() {
	putActions.removeAllElements();
	drRules.removeAllElements();
	curStream = null;
	curWorld = null;
	_refTable = null;
	colorModel = null;
    }
    
    private int readInt(String name) {
	int value = curStream.readInt();
	if (DEBUG && !name.equals(""))
	    indentAndPrint("int " + name + " = " + value);
	return value;
    }
    
    private boolean readBoolean(String name) {
	boolean value = curStream.readDRBoolean();
	if (DEBUG && !name.equals(""))
	    indentAndPrint("boolean " + name + " = " + (value ? "true"
							: "false"));
	return value;
    }
    
    private Point readPoint(String name) {
	Point value = curStream.readPoint();
	if (DEBUG && !name.equals(""))
	    indentAndPrint("point " + name + " = " + pointToString(value));
	return value;
    }
    
    private Vector readList(String name) {
	if (DEBUG && !name.equals(""))
	    indentAndPrint("list " + name);
	nestLevel = nestLevel + 1;
	Vector value = curStream.readList();
	nestLevel = nestLevel - 1;
	if (DEBUG && !name.equals(""))
	    indentAndPrint("list " + name + " = " + safeToString(value));
	return value;
    }
    
    private String readString() {
	String value;
	try {
	    value = curStream.readPString();
	} catch (java.io.IOException ioexception) {
	    Debug.print("debug.dr", "end of file encountered in String");
	    value = "";
	}
	if (DEBUG)
	    indentAndPrint("string = '" + value + "'");
	return value;
    }
    
    private Rect readRect(String name) {
	Point leftTop = curStream.readPoint();
	Point rightBottom = curStream.readPoint();
	Rect value = new Rect(leftTop.x, leftTop.y, rightBottom.x - leftTop.x,
			      rightBottom.y - leftTop.y);
	if (DEBUG && !name.equals(""))
	    indentAndPrint("rectangle " + name + " = " + safeToString(value));
	return value;
    }
    
    private Color readColor(String name) {
	Color value = curStream.readRGB();
	if (DEBUG && !name.equals(""))
	    indentAndPrint("color " + name + " = " + safeToString(value));
	return value;
    }
    
    private void unpackPrototypeVariables(CharacterPrototype ch,
					  Vector varValues) {
	if (!varValues.isEmpty()) {
	    Debug.print("debug.dr", "setting up a prototype's variables");
	    for (int i = 0; i < varValues.size(); i++) {
		DRStringValue sv = (DRStringValue) varValues.elementAt(i);
		Variable theVar
		    = (Variable) _refTable.fetchNoCreate(Variable.class,
							 sv.getSpecifier());
		if (theVar == null || !theVar.existsFor(ch)) {
		    theVar = (Variable) _refTable.fetch(Variable.class,
							sv.getSpecifier());
		    if (theVar.getListOwner() != null)
			theVar = theVar.getFixedCocoaVariable(ch);
		    String varName = theVar.getName();
		    if (varName == null)
			varName = newVariableName(sv.getSpecifier());
		    theVar.drFillInObject(varName, ch, null,
					  Variable.STD_ACCESSOR, true);
		    ch.add(theVar);
		}
		if (sv.getValue() == null)
		    Debug.print("debug.dr", "Variable id " + sv.getSpecifier(),
				" is now null");
		else
		    Debug.print("debug.dr",
				"setting var id " + sv.getSpecifier(), " to ",
				sv.getValue());
		theVar.setValue(ch, sv.getValue());
	    }
	}
    }
    
    private void unpackVariables(CharacterInstance ch, Vector varValues) {
	CharacterPrototype prototype = ch.getPrototype();
	if (!varValues.isEmpty()) {
	    Debug.print("debug.dr", "setting up an instance's variables");
	    for (int i = 0; i < varValues.size(); i++) {
		DRStringValue sv = (DRStringValue) varValues.elementAt(i);
		Variable theVar
		    = (Variable) _refTable.fetchNoCreate(Variable.class,
							 sv.getSpecifier());
		if (!theVar.existsFor(ch))
		    theVar = theVar.getFixedCocoaVariable(ch);
		if (sv.getValue() == null)
		    Debug.print("debug.dr", "Variable id " + sv.getSpecifier(),
				" is now null");
		else
		    Debug.print("debug.dr",
				"setting var id " + sv.getSpecifier(),
				" to " + sv.getValue());
		if (theVar == null)
		    Debug.print("debug.dr", "null variable in list for ", ch);
		else
		    theVar.setActualValue(ch, sv.getValue());
	    }
	}
    }
    
    private final void indentAndPrint(String msg) {
	nestLevel = nestLevel + 1;
	printIndented(nestLevel, msg);
	nestLevel = nestLevel - 1;
    }
    
    private final void printIndented(int level, String msg) {
	for (int i = 1; i < level; i++)
	    System.out.print("|  ");
	System.out.println(msg);
    }
    
    private String safeToString(Object obj) {
	String s;
	if (obj == null)
	    s = "null";
	else {
	    try {
		s = obj.toString();
	    } catch (Exception exception) {
		s = "unprintable";
	    }
	}
	return s;
    }
    
    private String pointToString(Point p) {
	return "(" + p.x + "," + p.y + ")";
    }
    
    private void drWarning(String msg) {
	String warning = "WARNING: ";
	System.out.println(warning + msg);
    }
    
    private void log(boolean mustDisplay, String message) {
	_mustDisplayLogWindow |= mustDisplay;
	_log.append(message);
    }
    
    private void logCR(boolean mustDisplay, String message) {
	_mustDisplayLogWindow |= mustDisplay;
	_log.append(message);
	_log.append('\n');
    }
    
    private String getObjectName(String typeNameID, Named object) {
	return Resource.getTextAndFormat("drRM7",
					 (new Object[]
					  { Resource.getText(typeNameID),
					    object.getName() }));
    }
    
    private void logDeletedObject(String typeNameID, Named object) {
	_mustDisplayLogWindow = true;
	String objectName = getObjectName(typeNameID, object);
	String logEntry
	    = Resource.getTextAndFormat("drRM2", new Object[] { objectName });
	logCR(true, logEntry);
    }
    
    private void logDeletedRule(Rule rule) {
	_mustDisplayLogWindow = true;
	CharacterPrototype proto = rule.getOwner();
	String protoName;
	if (proto != null)
	    protoName = getObjectName("drRM5", proto);
	else
	    protoName = "?";
	String ruleName = getObjectName("drRM4", rule);
	String logEntry
	    = Resource.getTextAndFormat("drRM10",
					new Object[] { ruleName, protoName });
	logCR(true, logEntry);
    }
    
    private void displayLogWindow() {
	Font userFont = Font.fontNamed("Monospaced", 0, 12);
	Color backgroundColor = Color.white;
	Size screenSize = PlaywriteRoot.getRootWindowSize();
	int width = Math.min(425, screenSize.width);
	int height = screenSize.height - 20;
	int x = Math.min(100, (screenSize.width - width) / 2);
	PlaywriteWindow logWindow
	    = new PlaywriteWindow(x, 10, width, height, curWorld);
	logWindow.setTitle("Translation Report");
	width = logWindow.contentSize().width;
	height = logWindow.contentSize().height;
	PlaywriteView contentView = new TallView(0, 0, width, height - 20) {
	    public void setBounds(int x_7_, int y, int width_8_,
				  int height_9_) {
		super.setBounds(x_7_, y, width_8_, height_9_);
		this.layoutView(0, 0);
	    }
	};
	contentView.setHorizResizeInstruction(2);
	contentView.setVertResizeInstruction(16);
	contentView.setBackgroundColor(backgroundColor);
	int yGap = 3;
	PackLayout packLayout = new PackLayout();
	PackConstraints pc = new PackConstraints();
	pc.setSide(0);
	pc.setFillX(true);
	pc.setPadY(3);
	packLayout.setDefaultConstraints(pc);
	contentView.setLayoutManager(packLayout);
	ScrollableArea scroller
	    = new ScrollableArea(width, height, contentView, false, true);
	scroller.setVerticalScrollAmount(20);
	scroller.setHorizResizeInstruction(2);
	scroller.setVertResizeInstruction(16);
	logWindow.addSubview(scroller);
	scroller.setBackgroundColor(backgroundColor);
	PlaywriteTextView logField = new PlaywriteTextView(0, 0, width, 20);
	logField.setString(_log.toString());
	logField.setFont(userFont);
	logField.setTextColor(Color.blue);
	contentView.addSubview(logField);
	contentView.layoutView(0, 0);
	Size ws = logWindow.windowSizeForContentSize(contentView.width(),
						     contentView.height());
	logWindow.setMinSize(300, 200);
	ws.height += 25;
	if (ws.height < logWindow.height()) {
	    logWindow.sizeTo(logWindow.width() + 1, ws.height);
	    contentView.layoutView(0, 0);
	}
	logWindow.show();
    }
    
    private String newVariableName(int id) {
	return "variable " + variableNumber++;
    }
}
