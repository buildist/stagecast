/* WaveTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class WaveTool implements Debug.Constants
{
    private StringBuffer chunktype;
    private FileInputStream unbuf;
    private InputStream input;
    private int i;
    private int j;
    private int k;
    private int first;
    private int second;
    private int[] tempsample;
    private boolean gotFmtChunk;
    private long FileSize;
    private long temp;
    private long BytesRead;
    private long ChunkLength;
    private long DataRead;
    private byte[] inputBuffer;
    private byte tempByte;
    private short magnitude;
    private final String readErrMsg = "WaveTool.read(): ";
    
    public WaveTool() {
	chunktype = new StringBuffer("zzzz");
    }
    
    void error(String message) {
	Debug.print(true, message);
    }
    
    private void setTitle(String message) {
	Debug.print("debug.sound", message);
    }
    
    public boolean read(AudioClass data, String filename) {
	setTitle("Reading " + filename);
	try {
	    unbuf = new FileInputStream(filename);
	} catch (java.io.FileNotFoundException filenotfoundexception) {
	    error("WaveTool.read():  file " + filename + " not found");
	    return false;
	} catch (java.io.IOException ioexception) {
	    error("WaveTool.read():  file " + filename + " can't open");
	    return false;
	}
	return read(data, new BufferedInputStream(unbuf, 100000));
    }
    
    public boolean read(AudioClass data, byte[] soundData) {
	return read(data, new ByteArrayInputStream(soundData));
    }
    
    public boolean read(AudioClass data, InputStream is) {
	boolean gotData = false;
	input = new BufferedInputStream(is);
	BytesRead = 0L;
	readChunkID();
	if (!"RIFF".equals(chunktype.toString())) {
	    error("WaveTool.read():  not a RIFF file");
	    return false;
	}
	FileSize = readIntelDWord();
	readChunkID();
	if (!"WAVE".equals(chunktype.toString())) {
	    error("WaveTool.read():  not a WAVE file");
	    return false;
	}
	BytesRead = 0L;
	gotFmtChunk = false;
	long mark = 0L;
	while (BytesRead < FileSize) {
	    readChunkID();
	    ChunkLength = readIntelDWord();
	    mark = BytesRead;
	    if ("fmt ".equals(chunktype.toString())) {
		if (readIntelWord() == 1)
		    data.Encoding = "PCM";
		else {
		    error
			("WaveTool.read(): Unidentified encoding in .WAV file");
		    return false;
		}
		data.Channels = readIntelWord();
		if (data.Channels > 2) {
		    error
			("WaveTool.read():  This file has " + data.Channels
			 + "channels. Only 1 and 2 channel files are implemented");
		    return false;
		}
		data.SampleRate = readIntelDWord();
		readIntelDWord();
		readIntelWord();
		data.BitsPerSample = readIntelWord();
		gotFmtChunk = true;
	    } else if ("data".equals(chunktype.toString())) {
		if (!gotFmtChunk) {
		    error("WaveTool.read(): Data Chunk before Format Chunk");
		    return false;
		}
		DataRead = 0L;
		data.NumSamples
		    = ChunkLength / (long) (data.Channels
					    * (data.BitsPerSample / 8));
		data.SampleDataLeft = new int[(int) data.NumSamples];
		if (data.Channels == 2)
		    data.SampleDataRight = new int[(int) data.NumSamples];
		this.i = j = 0;
		tempsample = new int[2];
		while (DataRead < data.NumSamples) {
		    try {
			if (data.BitsPerSample == 8) {
			    for (k = 0; k < data.Channels; k++) {
				tempByte = (byte) input.read();
				if (tempByte < 0)
				    tempsample[k] = 256 + tempByte;
				else
				    tempsample[k] = tempByte;
				tempsample[k] <<= 8;
				tempsample[k] -= 32768;
			    }
			    data.SampleDataLeft[j] = tempsample[0];
			    if (data.Channels == 2)
				data.SampleDataRight[j] = tempsample[1];
			    j++;
			    DataRead++;
			} else if (data.BitsPerSample == 16) {
			    for (k = 0; k < data.Channels; k++) {
				first = input.read();
				second = input.read();
				second <<= 8;
				second += first;
				if ((second & 0x8000) == 32768) {
				    magnitude = (short) second;
				    second = magnitude ^ 0xffffffff;
				    second++;
				    second = -second;
				}
				tempsample[k] = second;
			    }
			    data.SampleDataLeft[j] = tempsample[0];
			    if (data.Channels == 2)
				data.SampleDataRight[j] = tempsample[1];
			    j++;
			    DataRead++;
			}
		    } catch (java.io.IOException ioexception) {
			error("WaveTool.read(): error reading data.");
			return false;
		    }
		}
		gotData = true;
		break;
	    }
	    long unreadChunkLength = ChunkLength - (BytesRead - mark);
	    if (unreadChunkLength % 2L != 0L)
		unreadChunkLength++;
	    if (unreadChunkLength != 0L) {
		unreadChunkLength /= 2L;
		for (long i = 0L; i < unreadChunkLength; i++)
		    readIntelWord();
	    }
	}
	try {
	    input.close();
	} catch (java.io.IOException ioexception) {
	    error("WaveTool.read():  can't close file!");
	}
	return gotData;
    }
    
    private boolean readChunkID() {
	for (i = 0; i < 4; i++) {
	    try {
		chunktype.setCharAt(i, (char) input.read());
	    } catch (java.io.IOException ioexception) {
		error("WaveTool.read():  read error in file ");
		return false;
	    }
	}
	String ct = chunktype.toString();
	Debug.print("debug.sound", "Chunk type = " + ct);
	BytesRead += 4L;
	return true;
    }
    
    private long readIntelDWord() {
	temp = 0L;
	for (i = 0; i < 4; i++) {
	    try {
		temp += (long) ((char) input.read() << i * 8);
	    } catch (java.io.IOException ioexception) {
		error("WaveTool.read():  read error in file ");
		return 0L;
	    }
	}
	BytesRead += 4L;
	return temp;
    }
    
    private int readIntelWord() {
	temp = 0L;
	for (i = 0; i < 2; i++) {
	    try {
		temp += (long) ((char) input.read() << i * 8);
	    } catch (java.io.IOException ioexception) {
		error("WaveTool.read():  read error in file ");
		return 0;
	    }
	}
	BytesRead += 2L;
	return (int) temp;
    }
    
    int readSample(AudioClass data) {
	try {
	    if (data.BitsPerSample == 8) {
		int first = input.read();
		first <<= 8;
		return first - 32768;
	    }
	    if (data.BitsPerSample == 16) {
		int first = input.read();
		int second = input.read();
		second <<= 8;
		second += first;
		if ((second & 0x8000) == 32768) {
		    short magnitude = (short) second;
		    second = magnitude ^ 0xffffffff;
		    second = -++second;
		}
		return second;
	    }
	    error("WaveTool.read(): Can't handle file with " + data.Channels
		  + "channels and" + data.BitsPerSample + "bits per sample");
	    DataRead = ChunkLength;
	    return 0;
	} catch (java.io.IOException ioexception) {
	    error("WaveTool.read(): Error reading file.");
	    DataRead = ChunkLength;
	    return 0;
	}
    }
}
