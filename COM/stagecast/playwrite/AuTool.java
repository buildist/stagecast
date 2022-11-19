/* AuTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class AuTool implements Debug.Constants
{
    private FileOutputStream unbuf;
    private BufferedOutputStream output;
    private int i;
    private long temp;
    private final String magic = ".snd";
    private final String writeErrMsg = "AuTool.read(): ";
    
    void error(String s) {
	Debug.print("debug.sound", s);
    }
    
    void setTitle(String s) {
	Debug.print("debug.sound", s);
    }
    
    public boolean write(AudioClass data, String filename) {
	try {
	    unbuf = new FileOutputStream(filename);
	} catch (java.io.FileNotFoundException filenotfoundexception) {
	    error("AuTool.read():  file " + filename + " not found");
	    return false;
	} catch (java.io.IOException ioexception) {
	    error("AuTool.read():  file " + filename + " can't open");
	    return false;
	}
	return write(data, unbuf);
    }
    
    public boolean write(AudioClass data, OutputStream unbuf) {
	data.SetMono();
	data.SetSampleRate(8012);
	data.SetEncoding("Mu-Law");
	Debug.print("debug.sound", "Writing au ");
	output = new BufferedOutputStream(unbuf, 100000);
	try {
	    for (i = 0; i < 4; i++)
		output.write(".snd".charAt(i));
	    for (i = 0; i < 3; i++)
		output.write(0);
	    output.write(28);
	    for (i = 1; i <= 4; i++) {
		temp = data.NumSamples >> 8 * (4 - i);
		temp &= 0xffL;
		output.write((int) temp);
	    }
	    for (i = 0; i < 3; i++)
		output.write(0);
	    output.write(1);
	    for (i = 0; i < 2; i++)
		output.write(0);
	    output.write(31);
	    output.write(76);
	    for (i = 0; i < 3; i++)
		output.write(0);
	    output.write(1);
	    for (i = 0; i < 4; i++)
		output.write("wvau".charAt(i));
	    Debug.print("debug.sound",
			"argh: data.numsamples " + data.NumSamples);
	    for (i = 0; (long) i < data.NumSamples; i++) {
		if (i % 1000 == 0)
		    Debug.print("debug.sound",
				(String.valueOf(i) + " of " + data.NumSamples
				 + " written"));
		output.write(data.SampleDataLeft[i]);
	    }
	    output.close();
	} catch (java.io.IOException ioexception) {
	    error("AuTool.read():  Can't write output file");
	    return false;
	}
	return true;
    }
}
