/* CacheEnforcer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface CacheEnforcer
{
    public void shrinkCache();
    
    public void enforceMinLimit();
    
    public void enforceDefaultLimit();
    
    public void printStatistics();
}
