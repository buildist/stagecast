/* SystemSoundPlayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

public interface SystemSoundPlayer
{
    public void init(StreamProducer streamproducer);
    
    public boolean play();
    
    public void preload();
    
    public void stop();
}
