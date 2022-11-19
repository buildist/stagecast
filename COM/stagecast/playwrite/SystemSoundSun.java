/* SystemSoundSun - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;

import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class SystemSoundSun implements SystemSoundPlayer, Debug.Constants
{
    private StreamProducer _source;
    private AudioDataStream _ads;
    private AudioPlayer _player;
    
    public void init(StreamProducer sp) {
	_source = sp;
    }
    
    public boolean play() {
	boolean result = false;
	if (_ads != null && _player != null) {
	    try {
		_player.start(_ads);
		result = true;
	    } catch (OutOfMemoryError e) {
		throw e;
	    } catch (Throwable t) {
		Debug.stackTrace("debug.sound", t);
	    }
	}
	return result;
    }
    
    public void preload() {
	if (_ads == null) {
	    try {
		AudioStream as = new AudioStream(_source.makeInputStream());
		AudioData ad = as.getData();
		_ads = new AudioDataStream(ad);
		_player = AudioPlayer.player;
	    } catch (IOException e) {
		Debug.stackTrace("debug.sound", e);
	    }
	}
    }
    
    public void stop() {
	if (_ads != null)
	    _player.stop(_ads);
	_ads = null;
    }
}
