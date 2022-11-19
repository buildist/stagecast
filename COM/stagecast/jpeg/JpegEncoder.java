/* JpegEncoder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.jpeg;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JpegEncoder extends Frame
{
    Thread runner;
    BufferedOutputStream outStream;
    Image image;
    JpegInfo JpegObj;
    Huffman Huf;
    DCT dct;
    int imageHeight;
    int imageWidth;
    int Quality;
    int code;
    public static int[] jpegNaturalOrder
	= { 0, 1, 8, 16, 9, 2, 3, 10, 17, 24, 32, 25, 18, 11, 4, 5, 12, 19, 26,
	    33, 40, 48, 41, 34, 27, 20, 13, 6, 7, 14, 21, 28, 35, 42, 49, 56,
	    57, 50, 43, 36, 29, 22, 15, 23, 30, 37, 44, 51, 58, 59, 52, 45, 38,
	    31, 39, 46, 53, 60, 61, 54, 47, 55, 62, 63 };
    
    public JpegEncoder(Image image, int quality, OutputStream out) {
	MediaTracker tracker = new MediaTracker(this);
	tracker.addImage(image, 0);
	try {
	    tracker.waitForID(0);
	} catch (InterruptedException interruptedexception) {
	    /* empty */
	}
	Quality = quality;
	JpegObj = new JpegInfo(image);
	imageHeight = JpegObj.imageHeight;
	imageWidth = JpegObj.imageWidth;
	outStream = new BufferedOutputStream(out);
	dct = new DCT(Quality);
	Huf = new Huffman(imageWidth, imageHeight);
    }
    
    public void setQuality(int quality) {
	dct = new DCT(quality);
    }
    
    public int getQuality() {
	return Quality;
    }
    
    public void Compress() {
	WriteHeaders(outStream);
	WriteCompressedData(outStream);
	WriteEOI(outStream);
	try {
	    outStream.flush();
	} catch (IOException e) {
	    System.out.println("IO Error: " + e.getMessage());
	}
    }
    
    public void WriteCompressedData(BufferedOutputStream outStream) {
	int temp = 0;
	float[][] dctArray1 = new float[8][8];
	double[][] dctArray2 = new double[8][8];
	int[] dctArray3 = new int[64];
	int[] lastDCvalue = new int[JpegObj.NumberOfComponents];
	int[] zeroArray = new int[64];
	int Width = 0;
	int Height = 0;
	int nothing = 0;
	int MinBlockWidth
	    = (imageWidth % 8 != 0
	       ? (int) (Math.floor((double) imageWidth / 8.0) + 1.0) * 8
	       : imageWidth);
	int MinBlockHeight
	    = (imageHeight % 8 != 0
	       ? (int) (Math.floor((double) imageHeight / 8.0) + 1.0) * 8
	       : imageHeight);
	for (int comp = 0; comp < JpegObj.NumberOfComponents; comp++) {
	    MinBlockWidth = Math.min(MinBlockWidth, JpegObj.BlockWidth[comp]);
	    MinBlockHeight
		= Math.min(MinBlockHeight, JpegObj.BlockHeight[comp]);
	}
	int xpos = 0;
	for (int r = 0; r < MinBlockHeight; r++) {
	    for (int c = 0; c < MinBlockWidth; c++) {
		xpos = c * 8;
		int ypos = r * 8;
		for (int comp = 0; comp < JpegObj.NumberOfComponents; comp++) {
		    Width = JpegObj.BlockWidth[comp];
		    Height = JpegObj.BlockHeight[comp];
		    float[][] inputArray
			= (float[][]) JpegObj.Components[comp];
		    for (int i = 0; i < JpegObj.VsampFactor[comp]; i++) {
			for (int j = 0; j < JpegObj.HsampFactor[comp]; j++) {
			    int xblockoffset = j * 8;
			    int yblockoffset = i * 8;
			    for (int a = 0; a < 8; a++) {
				for (int b = 0; b < 8; b++)
				    dctArray1[a][b]
					= (inputArray[ypos + yblockoffset + a]
					   [xpos + xblockoffset + b]);
			    }
			    dctArray2 = dct.forwardDCT(dctArray1);
			    dctArray3 = dct.quantizeBlock(dctArray2,
							  (JpegObj.QtableNumber
							   [comp]));
			    Huf.HuffmanBlockEncoder(outStream, dctArray3,
						    lastDCvalue[comp],
						    (JpegObj.DCtableNumber
						     [comp]),
						    (JpegObj.ACtableNumber
						     [comp]));
			    lastDCvalue[comp] = dctArray3[0];
			}
		    }
		}
	    }
	}
	Huf.flushBuffer(outStream);
    }
    
    public void WriteEOI(BufferedOutputStream out) {
	byte[] EOI = { -1, -39 };
	WriteMarker(EOI, out);
    }
    
    public void WriteHeaders(BufferedOutputStream out) {
	byte[] SOI = { -1, -40 };
	WriteMarker(SOI, out);
	byte[] JFIF = new byte[18];
	JFIF[0] = (byte) -1;
	JFIF[1] = (byte) -32;
	JFIF[2] = (byte) 0;
	JFIF[3] = (byte) 16;
	JFIF[4] = (byte) 74;
	JFIF[5] = (byte) 70;
	JFIF[6] = (byte) 73;
	JFIF[7] = (byte) 70;
	JFIF[8] = (byte) 0;
	JFIF[9] = (byte) 1;
	JFIF[10] = (byte) 0;
	JFIF[11] = (byte) 0;
	JFIF[12] = (byte) 0;
	JFIF[13] = (byte) 1;
	JFIF[14] = (byte) 0;
	JFIF[15] = (byte) 1;
	JFIF[16] = (byte) 0;
	JFIF[17] = (byte) 0;
	WriteArray(JFIF, out);
	String comment = new String();
	comment = JpegObj.getComment();
	int length = comment.length();
	byte[] COM = new byte[length + 4];
	COM[0] = (byte) -1;
	COM[1] = (byte) -2;
	COM[2] = (byte) (length >> 8 & 0xff);
	COM[3] = (byte) (length & 0xff);
	System.arraycopy(JpegObj.Comment.getBytes(), 0, COM, 4,
			 JpegObj.Comment.length());
	WriteArray(COM, out);
	byte[] DQT = new byte[134];
	DQT[0] = (byte) -1;
	DQT[1] = (byte) -37;
	DQT[2] = (byte) 0;
	DQT[3] = (byte) -124;
	int offset = 4;
	for (int i = 0; i < 2; i++) {
	    DQT[offset++] = (byte) i;
	    int[] tempArray = (int[]) dct.quantum[i];
	    for (int j = 0; j < 64; j++)
		DQT[offset++] = (byte) tempArray[jpegNaturalOrder[j]];
	}
	WriteArray(DQT, out);
	byte[] SOF = new byte[19];
	SOF[0] = (byte) -1;
	SOF[1] = (byte) -64;
	SOF[2] = (byte) 0;
	SOF[3] = (byte) 17;
	SOF[4] = (byte) JpegObj.Precision;
	SOF[5] = (byte) (JpegObj.imageHeight >> 8 & 0xff);
	SOF[6] = (byte) (JpegObj.imageHeight & 0xff);
	SOF[7] = (byte) (JpegObj.imageWidth >> 8 & 0xff);
	SOF[8] = (byte) (JpegObj.imageWidth & 0xff);
	SOF[9] = (byte) JpegObj.NumberOfComponents;
	int index = 10;
	for (int i = 0; i < SOF[9]; i++) {
	    SOF[index++] = (byte) JpegObj.CompID[i];
	    SOF[index++] = (byte) ((JpegObj.HsampFactor[i] << 4)
				   + JpegObj.VsampFactor[i]);
	    SOF[index++] = (byte) JpegObj.QtableNumber[i];
	}
	WriteArray(SOF, out);
	length = 2;
	index = 4;
	int oldindex = 4;
	byte[] DHT1 = new byte[17];
	byte[] DHT4 = new byte[4];
	DHT4[0] = (byte) -1;
	DHT4[1] = (byte) -60;
	for (int i = 0; i < 4; i++) {
	    int bytes = 0;
	    DHT1[index++ - oldindex]
		= (byte) ((int[]) Huf.bits.elementAt(i))[0];
	    for (int j = 1; j < 17; j++) {
		int temp = ((int[]) Huf.bits.elementAt(i))[j];
		DHT1[index++ - oldindex] = (byte) temp;
		bytes += temp;
	    }
	    int intermediateindex = index;
	    byte[] DHT2 = new byte[bytes];
	    for (int j = 0; j < bytes; j++)
		DHT2[index++ - intermediateindex]
		    = (byte) ((int[]) Huf.val.elementAt(i))[j];
	    byte[] DHT3 = new byte[index];
	    System.arraycopy(DHT4, 0, DHT3, 0, oldindex);
	    System.arraycopy(DHT1, 0, DHT3, oldindex, 17);
	    System.arraycopy(DHT2, 0, DHT3, oldindex + 17, bytes);
	    DHT4 = DHT3;
	    oldindex = index;
	}
	DHT4[2] = (byte) (index - 2 >> 8 & 0xff);
	DHT4[3] = (byte) (index - 2 & 0xff);
	WriteArray(DHT4, out);
	byte[] SOS = new byte[14];
	SOS[0] = (byte) -1;
	SOS[1] = (byte) -38;
	SOS[2] = (byte) 0;
	SOS[3] = (byte) 12;
	SOS[4] = (byte) JpegObj.NumberOfComponents;
	index = 5;
	for (int i = 0; i < SOS[4]; i++) {
	    SOS[index++] = (byte) JpegObj.CompID[i];
	    SOS[index++] = (byte) ((JpegObj.DCtableNumber[i] << 4)
				   + JpegObj.ACtableNumber[i]);
	}
	SOS[index++] = (byte) JpegObj.Ss;
	SOS[index++] = (byte) JpegObj.Se;
	SOS[index++] = (byte) ((JpegObj.Ah << 4) + JpegObj.Al);
	WriteArray(SOS, out);
    }
    
    void WriteMarker(byte[] data, BufferedOutputStream out) {
	try {
	    out.write(data, 0, 2);
	} catch (IOException e) {
	    System.out.println("IO Error: " + e.getMessage());
	}
    }
    
    void WriteArray(byte[] data, BufferedOutputStream out) {
	try {
	    int length = ((data[2] & 0xff) << 8) + (data[3] & 0xff) + 2;
	    out.write(data, 0, length);
	} catch (IOException e) {
	    System.out.println("IO Error: " + e.getMessage());
	}
    }
}
