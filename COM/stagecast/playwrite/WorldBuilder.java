/* WorldBuilder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.util.zip.ZipFile;

public interface WorldBuilder
{
    public World getTargetWorld();
    
    public ZipFile getMediaContainer();
    
    public PlaywriteLoader getLoader();
    
    public String mapName(String string);
}
