/* ClassRegistry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.operators.OperationManager;
import COM.stagecast.unaryoperators.UnaryExpression;

public class ClassRegistry implements Debug.Constants
{
    private Hashtable _table = new Hashtable(50);
    private Hashtable _overflow = new Hashtable(10);
    
    private class RegInfo
    {
	int _version;
	Object _extensionID;
	
	RegInfo(int version, Object extensionID) {
	    _version = version;
	    _extensionID = extensionID;
	}
    }
    
    static void dumpVersionRegistry(ClassRegistry reg, ObjectOutput out)
	throws IOException {
	out.writeInt(reg._table.size());
	Enumeration items = reg._table.keys();
	while (items.hasMoreElements()) {
	    String className = (String) items.nextElement();
	    RegInfo info = (RegInfo) reg._table.get(className);
	    out.writeUTF(className);
	    out.writeInt(info._version);
	}
    }
    
    static ClassRegistry loadVersionRegistry(ObjectInput in, int fsVersion)
	throws IOException, ClassNotFoundException {
	ClassRegistry sysreg = PlaywriteRoot.getClassRegistry();
	ClassRegistry reg = new ClassRegistry();
	int size = in.readInt();
	while (size-- > 0) {
	    String className = in.readUTF();
	    int version = in.readInt();
	    if (fsVersion == 1)
		in.readBoolean();
	    reg.registerClass(className, version);
	}
	return reg;
    }
    
    public void registerClass(String className, int version,
			      Object extensionID) {
	RegInfo newinfo = new RegInfo(version, extensionID);
	RegInfo previous = (RegInfo) _table.get(className);
	if (previous == null) {
	    Debug.print("debug.loader", "Registering class ", className,
			": version " + version,
			" in extension: " + extensionID);
	    _table.put(className, newinfo);
	} else {
	    if (previous._version != newinfo._version)
		throw new PlaywriteInternalError
			  ("Duplicate extensions of different versions");
	    previous = (RegInfo) _overflow.get(className);
	    ASSERT.isNull(previous);
	    Debug.print("debug.loader", "Registering duplicate class ",
			className, ": version " + version,
			" in extension: " + extensionID);
	    _overflow.put(className, newinfo);
	}
    }
    
    public void registerClass(Class cls, int version, Object extensionID) {
	registerClass(cls.getName(), version, extensionID);
    }
    
    void registerClass(Class cls, int version) {
	registerClass(cls.getName(), version, PluginRegistry.BUILT_IN);
    }
    
    void registerClass(String className, int version) {
	registerClass(className, version, PluginRegistry.BUILT_IN);
    }
    
    public void deregister(String className, Object extensionID) {
	Object removed = _table.remove(className);
	if (removed == null)
	    removed = _overflow.remove(className);
	ASSERT.isNotNull(removed);
    }
    
    public void deregister(Class cls, Object extensionID) {
	deregister(cls.getName(), extensionID);
    }
    
    RegInfo infoForClass(String className) {
	RegInfo info = (RegInfo) _table.get(className);
	return info == null ? (RegInfo) _overflow.get(className) : info;
    }
    
    boolean isRegistered(String className) {
	return infoForClass(className) != null;
    }
    
    boolean isRegistered(Class cls) {
	return isRegistered(cls.getName());
    }
    
    int version(String className) {
	RegInfo info = infoForClass(className);
	return info == null ? 1 : info._version;
    }
    
    int version(Class cls) {
	return version(cls.getName());
    }
    
    void init() {
	registerClass(World.class, 12);
	registerClass(Stage.class, 6);
	registerClass(Appearance.class, 5);
	registerClass(AnythingAppearance.class, 1);
	registerClass(JarAppearance.class, 1);
	registerClass(PlaywriteSound.class, 3);
	registerClass(CharacterPrototype.class, 2);
	registerClass(DoorPrototype.class, 1);
	registerClass(TextCharacterPrototype.class, 3);
	registerClass(CharacterInstance.class, 3);
	registerClass(DoorInstance.class, 1);
	registerClass(TextCharacterInstance.class, 4);
	registerClass(GeneralizedCharacter.class, 1);
	registerClass(Jar.class, 1);
	registerClass(XYContainer.class, 2);
	registerClass(XYCharContainer.class, 1);
	registerClass(Variable.class, 4);
	registerClass(BooleanVariable.class, 4);
	registerClass(PopupVariable.class, 1);
	registerClass(Subroutine.class, 3);
	registerClass(Pretest.class, 1);
	registerClass(Rule.class, 1);
	registerClass(BeforeBoard.class, 2);
	registerClass(CopyAction.class, 1);
	registerClass(CreateAction.class, 1);
	registerClass(DeleteAction.class, 1);
	registerClass(MoveAction.class, 1);
	registerClass(OpenURLAction.class, 2);
	registerClass(PutAction.class, 2);
	registerClass(PutUnderAction.class, 1);
	registerClass(SplitStageAction.class, 1);
	registerClass(SwitchStageAction.class, 4);
	registerClass(TeleportAction.class, 3);
	registerClass(OperationManager.class,
		      OperationManager.getStoreVersion());
	registerClass(BindTest.class, 1);
	registerClass(BooleanTest.class, 2);
	registerClass(KeyTest.class, 3);
	registerClass(MouseClickTest.class, 1);
	registerClass(VariableAlias.class, 1);
	registerClass(ObjectProxy.class, 1);
	registerClass(MediaProxy.class, 1);
	registerClass(VariableProxy.class, 1);
	registerClass(Shape.class, 1);
	registerClass(UniqueID.class, 1);
	registerClass(RestartProxy.class, 1);
	registerClass(DisplayVariable.class, 1);
	registerClass(Board.class, 6);
	registerClass(CocoaCharacter.class, 1);
	registerClass(RuleAction.class, 2);
	registerClass(RuleListItem.class, 1);
	registerClass(Comment.class, 2);
	registerClass(RuleTest.class, 1);
	registerClass(ColorVariable.class, 1);
	registerClass(ColorValue.class, 1);
	registerClass(BackgroundVariable.class, 1);
	registerClass(BackgroundImage.class, 2);
	registerClass(DRMouseClickTest.class, 1);
	registerClass(DRTeleportAction.class, 1);
	registerClass(ViewData.class, 2);
	registerClass(NormalSubType.class, 2);
	registerClass(DoAllSubType.class, 2);
	registerClass(RandomSubType.class, 2);
	registerClass(SequenceSubType.class, 2);
	registerClass(FingerVariable.class, 2);
	registerClass(PutCalcAction.class, 1);
	registerClass(DeferredAction.class, 1);
	registerClass(EnumeratedVariable.class, 1);
	registerClass(IndexedObject.class, 1);
	registerClass(Password.class, 1);
	registerClass(StopAction.class, 1);
	registerClass(ResetAction.class, 1);
	registerClass(QuitAction.class, 1);
	registerClass(StorageProxy.class, 1);
	registerClass(SpecialInstance.class, 0);
	registerClass(SpecialPrototype.class, 0);
	registerClass(COM.stagecast.operators.SubtotalObject.class, 0);
	registerClass(COM.stagecast.operators.NormalSubtotal.class, 0);
	registerClass(COM.stagecast.operators.RandomSubtotal.class, 0);
	registerClass(COM.stagecast.operators.Total.class, 0);
	registerClass(UnaryExpression.class,
		      UnaryExpression.getStoreVersion());
    }
}
