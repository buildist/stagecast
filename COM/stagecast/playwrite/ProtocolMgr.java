/* ProtocolMgr - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.simmer.ClientServerCoder;

public interface ProtocolMgr
{
    public static interface Mapper
    {
	public Integer idForObject(Object object);
	
	public Integer addToMap(Object object);
	
	public void removeFromMap(Object object);
	
	public void addMediaObject(StreamedMediaItem streamedmediaitem);
    }
    
    public ClientServerCoder buildCreateMessage(Object object, Integer integer,
						Mapper mapper);
    
    public ClientServerCoder buildDestroyMessage(Integer integer);
    
    public ClientServerCoder buildSetVariableMessage
	(Integer integer, Integer integer_0_, Object object, Mapper mapper);
    
    public ClientServerCoder buildActionMessage(Integer integer, String string,
						Object object, Mapper mapper);
}
