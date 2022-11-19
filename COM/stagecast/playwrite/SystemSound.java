/* SystemSound - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import COM.stagecast.ifc.netscape.application.FileChooser;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class SystemSound
    implements Debug.Constants, StreamedMediaItem, ResourceIDs.CommandIDs,
	       ResourceIDs.DialogIDs, ResourceIDs.DrawerIDs
{
    static final long SOUND_FILE_WARNING_LIMIT = 5242880L;
    static boolean playSounds = false;
    static final String playerClassFor1_2
	= "COM.stagecast.playwrite.SystemSoundSun";
    static final String playerClassFor1_3
	= "COM.stagecast.playwrite.SystemSoundJavax";
    static Class playerClass = null;
    private transient StreamProducer _source;
    private transient boolean _isMacSnd = false;
    private transient boolean _isValid = false;
    private transient String _mediaID;
    private transient SystemSoundPlayer _player = null;
    
    static FileChooser showImportSoundDialog(boolean importAll) {
	FileChooser chooser
	    = (new FileChooser
	       (PlaywriteRoot.getMainRootView(),
		Resource.getText(importAll ? "sound import all dialog title"
				 : "sound import dialog title"),
		0));
	chooser.setDirectory(World.getSoundDirectory());
	chooser.showModally();
	return chooser;
    }
    
    static boolean importAllSounds(World world, FileIO.FileIterator handler) {
	FileChooser chooser = showImportSoundDialog(true);
	String fname = chooser.file();
	if (fname == null)
	    return false;
	World.setSoundDirectory(chooser.directory());
	FileIO.iterateOverDirectory(chooser.directory(), handler, false, null);
	return true;
    }
    
    public static SystemSound importSoundFromFile(String fname, Named target,
						  boolean displayWarnings) {
	SystemSound newSound = null;
	Debug.print("debug.sound", "importing sound: ", fname);
	try {
	    File file = new File(fname);
	    if (file.length() > 5242880L) {
		PlaywriteDialog dlg
		    = new PlaywriteDialog("dialog ls", "command import",
					  "command c");
		dlg.setDefaultButton(1);
		String result = dlg.getAnswerModally();
		if (result == "command c")
		    return null;
	    }
	    newSound = new SystemSound(AbstractStreamProducer
					   .createStreamProducer(file));
	    if (!newSound.hasValidData()) {
		if (displayWarnings)
		    PlaywriteDialog.warning("dialog bsf", true);
		return null;
	    }
	    String sname = Util.getFilePart(file.getName());
	    if (target != null)
		target.setName(Util.dePercentString(sname));
	} catch (Exception e) {
	    Debug.print(true, e);
	    Debug.stackTrace(e);
	}
	return newSound;
    }
    
    SystemSound(StreamProducer sp, String name) {
	_source = sp;
	_mediaID = name;
	checkSoundData();
	if (playerClass == null) {
	    try {
		Class.forName("javax.sound.sampled.AudioSystem");
		this.getClass();
		playerClass
		    = Class
			  .forName("COM.stagecast.playwrite.SystemSoundJavax");
	    } catch (Exception exception) {
		try {
		    this.getClass();
		    playerClass = (Class.forName
				   ("COM.stagecast.playwrite.SystemSoundSun"));
		} catch (Exception e2) {
		    Debug.stackTrace(e2);
		}
	    }
	}
	try {
	    _player = (SystemSoundPlayer) playerClass.newInstance();
	} catch (Exception e) {
	    Debug.stackTrace(e);
	}
	_player.init(_source);
    }
    
    SystemSound(StreamProducer sp) {
	this(sp, "");
    }
    
    SystemSound(InputStream is) {
	this(AbstractStreamProducer.createStreamProducer(is, false));
    }
    
    SystemSound(byte[] data) {
	this(AbstractStreamProducer.createStreamProducer((data == null
							  ? new byte[] { -1 }
							  : data),
							 false));
	if (data == null)
	    Debug.print("debug.sound", "Sound created with no data!");
    }
    
    public SystemSound() {
	/* empty */
    }
    
    boolean isMacSound() {
	return _isMacSnd;
    }
    
    boolean isValue() {
	return _isValid;
    }
    
    public boolean isPlayable() {
	return _isValid && !_isMacSnd;
    }
    
    public StreamProducer getMediaSource() {
	return _source;
    }
    
    public String getMediaID() {
	return _mediaID;
    }
    
    private void checkSoundData() {
	InputStream is = null;
	_isValid = false;
	_isMacSnd = false;
	try {
	    byte[] buf = new byte[4];
	    is = _source.makeInputStream();
	    int count = is.read(buf);
	    if (count < 4)
		Debug.print("debug.sound", "sound size < 4");
	    else {
		ByteArrayOutputStream idMaker = new ByteArrayOutputStream(4);
		idMaker.write(buf, 0, 4);
		idMaker.close();
		String type = idMaker.toString();
		Debug.print("debug.sound", "sound id [", type, "]");
		if ("RIFF".equals(type)) {
		    StreamProducer convertedSource = convertWavToAu(_source);
		    if (convertedSource != _source) {
			_source = convertedSource;
			checkSoundData();
		    }
		} else if (".snd".equals(type))
		    _isValid = true;
		else if (count > 2 && buf[0] == 0 && buf[1] == 1) {
		    _isValid = true;
		    _isMacSnd = true;
		}
	    }
	} catch (IOException e) {
	    Debug.stackTrace(e);
	} finally {
	    if (is != null) {
		try {
		    is.close();
		} catch (Throwable throwable) {
		    /* empty */
		}
	    }
	    Debug.print("debug.sound", "Sound is valid: " + _isValid);
	}
    }
    
    private StreamProducer convertWavToAu(StreamProducer sp) {
	AudioClass waveSoundData = new AudioClass();
	WaveTool waveTool = new WaveTool();
	AuTool auTool = new AuTool();
	Debug.print("debug.sound", "translating .wav file");
	try {
	    if (waveTool.read(waveSoundData, sp.makeInputStream())) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		auTool.write(waveSoundData, baos);
		return AbstractStreamProducer
			   .createStreamProducer(baos.toByteArray(), false);
	    }
	} catch (IOException ioexception) {
	    Debug.print(true, "Can't convert WAV to AU");
	}
	return sp;
    }
    
    protected boolean hasValidData() {
	return isPlayable();
    }
    
    void writeData(OutputStream os) throws IOException {
	InputStream is = _source.makeInputStream();
	byte[] buffer = new byte[4096];
	int count = 0;
	while (count >= 0) {
	    count = is.read(buffer);
	    if (count > 0)
		os.write(buffer, 0, count);
	}
	is.close();
    }
    
    public Object copy() {
	try {
	    InputStream is = _source.makeInputStream();
	    StreamProducer sp
		= AbstractStreamProducer.createStreamProducer(is, false);
	    return new SystemSound(sp);
	} catch (IOException e) {
	    Debug.stackTrace(e);
	    return null;
	}
    }
    
    boolean play() {
	if (!playSounds)
	    return true;
	if (!hasValidData())
	    return true;
	stop();
	preload();
	return _player.play();
    }
    
    void preload() {
	if (hasValidData())
	    _player.preload();
    }
    
    void stop() {
	_player.stop();
    }
    
    protected void finalize() throws Throwable {
	stop();
	_player = null;
	super.finalize();
    }
}
