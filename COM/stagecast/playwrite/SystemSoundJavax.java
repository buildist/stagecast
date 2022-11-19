/* SystemSoundJavax - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class SystemSoundJavax
    implements Debug.Constants, SystemSoundPlayer, Runnable
{
    private static final int BUFFER_SIZE = 8192;
    private static int count = 1;
    private StreamProducer _source;
    private SourceDataLine _line;
    private AudioInputStream _audioInputStream;
    private AudioFormat _format;
    private Thread _thread;
    private volatile boolean _break;
    
    public void init(StreamProducer sp) {
	_source = sp;
    }
    
    public boolean play() {
	if (_line == null)
	    return false;
	_break = false;
	_thread = new Thread(this);
	_thread.setName("Play sound " + count++);
	_thread.start();
	return true;
    }
    
    public void run() {
	if (_format == null || _line == null)
	    Debug.print(true, "Invalid sound");
	else {
	    int frameSizeInBytes = _format.getFrameSize();
	    int bufferLengthInFrames = _line.getBufferSize() / 8;
	    int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
	    byte[] data = new byte[bufferLengthInBytes];
	    int numBytesRead = 0;
	    int numBytesRemaining = 0;
	    Debug.print("debug.sound", "Playback thread is ", _thread);
	    Debug.print("debug.sound", "Frame size = " + frameSizeInBytes,
			" bytes");
	    Debug.print("debug.sound",
			"Line Buffer length = " + bufferLengthInFrames,
			" frames");
	    Debug.print("debug.sound", "Buffer size = " + bufferLengthInBytes,
			" bytes");
	    _line.start();
	    try {
	    while_9_:
		for (;;) {
		    numBytesRead = _audioInputStream.read(data);
		    if (numBytesRead < 0)
			break;
		    numBytesRemaining = numBytesRead;
		    while (numBytesRemaining > 0) {
			numBytesRemaining
			    -= _line.write(data, 0, numBytesRemaining);
			if (_break)
			    break while_9_;
		    }
		}
	    } catch (Exception e) {
		Debug.stackTrace(e);
	    }
	    try {
		for (boolean done = false; !done;
		     done = _break || !_line.isActive())
		    Thread.sleep(50L);
	    } catch (InterruptedException e) {
		Debug.stackTrace(e);
	    }
	    if (!_break)
		_line.drain();
	    _line.stop();
	    _line.flush();
	    _line.close();
	    _line = null;
	    _audioInputStream = null;
	    _format = null;
	    _thread = null;
	}
    }
    
    public void preload() {
	if (_line == null) {
	    try {
		java.io.InputStream is
		    = new BufferedInputStream(_source.makeInputStream());
		_audioInputStream = AudioSystem.getAudioInputStream(is);
		_format = _audioInputStream.getFormat();
		if (_format.getEncoding() == AudioFormat.Encoding.ULAW
		    || _format.getEncoding() == AudioFormat.Encoding.ALAW) {
		    AudioFormat newFormat
			= new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
					  _format.getSampleRate(),
					  _format.getSampleSizeInBits() * 2,
					  _format.getChannels(),
					  _format.getFrameSize() * 2,
					  _format.getFrameRate(), true);
		    _audioInputStream
			= AudioSystem.getAudioInputStream(newFormat,
							  _audioInputStream);
		    _format = newFormat;
		}
		DataLine.Info info
		    = new DataLine.Info(SourceDataLine.class, _format);
		_line = (SourceDataLine) AudioSystem.getLine(info);
		_line.open(_format, 8192);
	    } catch (Exception e) {
		Debug.stackTrace(e);
		Debug.print(true, "Audio format: " + _format);
		Debug.print(true, "Data line: " + _line);
		_line = null;
	    }
	}
    }
    
    public void stop() {
	if (_line != null) {
	    if (_thread == null)
		_line = null;
	    else {
		_break = true;
		Debug.print("debug.sound", "From ", Thread.currentThread(),
			    " sound thread interrupted: ", _thread);
		try {
		    _thread.join(5000L);
		} catch (InterruptedException interruptedexception) {
		    /* empty */
		}
	    }
	}
    }
}
