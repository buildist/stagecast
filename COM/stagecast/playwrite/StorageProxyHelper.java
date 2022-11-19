/* StorageProxyHelper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface StorageProxyHelper
{
    public void registerProxy(String string, StorageProxied storageproxied);
    
    public String getIDFor(StorageProxied storageproxied);
    
    public Object resolveID(String string);
}
