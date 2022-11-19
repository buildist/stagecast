/* AudioClass - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.TextField;

public class AudioClass implements Debug.Constants
{
    static final boolean ZEROTRAP = true;
    static final int BIAS = 132;
    static final int CLIP = 32635;
    static final int[] exp_lut
	= { 0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4,
	    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
	    5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6,
	    6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	    6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	    6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7,
	    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
	    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 };
    public int Channels;
    public long SampleRate;
    public long NumSamples;
    public int BitsPerSample;
    public String Encoding;
    public int[][] SampleData;
    public int[] SampleDataLeft;
    public int[] SampleDataRight;
    public int[] newleft;
    public int[] newright;
    private int i;
    private int j;
    private int[] sample;
    private TextField status;
    private double temp;
    private double time_per_sample;
    private double newtime;
    private double delta_t;
    private int newNumSamples;
    private int newOffset;
    int exponent;
    int mantissa;
    int ulawbyte;
    int sign;
    
    public void AudioError(String msg) {
	Debug.print("debug.sound", msg);
    }
    
    public boolean SetEncoding(String NewEncoding) {
	if (NewEncoding.equals(Encoding))
	    return true;
	if (!NewEncoding.equals("Mu-Law") || !Encoding.equals("PCM")) {
	    AudioError("Unimplemented Encoding change.");
	    return false;
	}
	sample = new int[2];
	Debug.print("debug.sound", "Converting to Mu-Law");
	for (i = 0; (long) i < NumSamples; i++) {
	    sample[0] = SampleDataLeft[i];
	    if (Channels == 2)
		sample[1] = SampleDataRight[i];
	    for (j = 0; j < Channels; j++) {
		if (sample[j] < 0) {
		    sample[j] = -sample[j];
		    sign = 128;
		} else
		    sign = 0;
		if (sample[j] > 32635)
		    sample[j] = 32635;
		sample[j] = sample[j] + 132;
		exponent = exp_lut[sample[j] >> 7 & 0xff];
		mantissa = (byte) (sample[j] >> exponent + 3 & 0xf);
		ulawbyte = sign | exponent << 4 | mantissa;
		ulawbyte = ulawbyte ^ 0xffffffff;
		ulawbyte &= 0xff;
		if (ulawbyte == 0)
		    ulawbyte = 2;
		sample[j] = ulawbyte;
	    }
	    SampleDataLeft[i] = sample[0];
	    if (Channels == 2)
		SampleDataRight[i] = sample[1];
	}
	Encoding = "Mu-Law";
	return true;
    }
    
    boolean SetMono() {
	if (Channels == 1)
	    return true;
	Debug.print("debug.sound", "Converting to mono...");
	for (i = 0; (long) i < NumSamples; i++) {
	    SampleDataLeft[i] += SampleDataRight[i];
	    SampleDataLeft[i] /= Channels;
	}
	Channels = 1;
	return true;
    }
    
    boolean SetSampleRate(int newrate) {
	if ((long) newrate == SampleRate)
	    return true;
	Debug.print("debug.sound", "Resampling at " + newrate + " Hertz");
	temp = (double) newrate / (double) SampleRate;
	time_per_sample = 1.0 / (double) SampleRate;
	newNumSamples = (int) ((double) NumSamples * temp) + 1;
	newleft = new int[newNumSamples];
	if (Channels == 2)
	    newright = new int[newNumSamples];
	delta_t = 0.0;
	newtime = 1.0 / (double) newrate;
	newOffset = 0;
	for (i = 0; (long) i < NumSamples; i++) {
	    delta_t += time_per_sample;
	    if (delta_t > newtime) {
		newleft[newOffset] = SampleDataLeft[i];
		if (Channels == 2)
		    newright[newOffset] = SampleDataRight[i];
		delta_t -= newtime;
		newOffset++;
	    }
	}
	NumSamples = (long) newOffset;
	SampleDataLeft = newleft;
	if (Channels == 2)
	    SampleDataRight = newright;
	return true;
    }
}
