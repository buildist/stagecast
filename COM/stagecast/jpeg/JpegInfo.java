/* JpegInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.jpeg;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.image.PixelGrabber;

class JpegInfo
{
    String Comment;
    public Image imageobj;
    public int imageHeight;
    public int imageWidth;
    public int[] BlockWidth;
    public int[] BlockHeight;
    public int Precision = 8;
    public int NumberOfComponents = 3;
    public Object[] Components;
    public int[] CompID = { 1, 2, 3 };
    public int[] HsampFactor = { 1, 1, 1 };
    public int[] VsampFactor = { 1, 1, 1 };
    public int[] QtableNumber = { 0, 1, 1 };
    public int[] DCtableNumber = { 0, 1, 1 };
    public int[] ACtableNumber = { 0, 1, 1 };
    public boolean[] lastColumnIsDummy = new boolean[3];
    public boolean[] lastRowIsDummy = new boolean[3];
    public int Ss = 0;
    public int Se = 63;
    public int Ah = 0;
    public int Al = 0;
    public int[] compWidth;
    public int[] compHeight;
    public int MaxHsampFactor;
    public int MaxVsampFactor;
    
    public JpegInfo(Image image) {
	Components = new Object[NumberOfComponents];
	compWidth = new int[NumberOfComponents];
	compHeight = new int[NumberOfComponents];
	BlockWidth = new int[NumberOfComponents];
	BlockHeight = new int[NumberOfComponents];
	imageobj = image;
	imageWidth = image.getWidth(null);
	imageHeight = image.getHeight(null);
	Comment
	    = "JPEG Encoder Copyright 1998, James R. Weeks and BioElectroMech.  ";
	getYCCArray();
    }
    
    public void setComment(String comment) {
	Comment.concat(comment);
    }
    
    public String getComment() {
	return Comment;
    }
    
    private void getYCCArray() {
	int[] values = new int[imageWidth * imageHeight];
	PixelGrabber grabber
	    = new PixelGrabber(imageobj.getSource(), 0, 0, imageWidth,
			       imageHeight, values, 0, imageWidth);
	MaxHsampFactor = 1;
	MaxVsampFactor = 1;
	for (int y = 0; y < NumberOfComponents; y++) {
	    MaxHsampFactor = Math.max(MaxHsampFactor, HsampFactor[y]);
	    MaxVsampFactor = Math.max(MaxVsampFactor, VsampFactor[y]);
	}
	for (int y = 0; y < NumberOfComponents; y++) {
	    compWidth[y] = (imageWidth % 8 != 0
			    ? (int) Math.ceil((double) imageWidth / 8.0) * 8
			    : imageWidth) / MaxHsampFactor * HsampFactor[y];
	    if (compWidth[y] != imageWidth / MaxHsampFactor * HsampFactor[y])
		lastColumnIsDummy[y] = true;
	    BlockWidth[y] = (int) Math.ceil((double) compWidth[y] / 8.0);
	    compHeight[y] = (imageHeight % 8 != 0
			     ? (int) Math.ceil((double) imageHeight / 8.0) * 8
			     : imageHeight) / MaxVsampFactor * VsampFactor[y];
	    if (compHeight[y] != imageHeight / MaxVsampFactor * VsampFactor[y])
		lastRowIsDummy[y] = true;
	    BlockHeight[y] = (int) Math.ceil((double) compHeight[y] / 8.0);
	}
	try {
	    if (grabber.grabPixels() != true) {
		try {
		    throw new AWTException("Grabber returned false: "
					   + grabber.status());
		} catch (Exception exception) {
		    /* empty */
		}
	    }
	} catch (InterruptedException interruptedexception) {
	    /* empty */
	}
	float[][] Y = new float[compHeight[0]][compWidth[0]];
	float[][] Cr1 = new float[compHeight[0]][compWidth[0]];
	float[][] Cb1 = new float[compHeight[0]][compWidth[0]];
	float[][] Cb2 = new float[compHeight[1]][compWidth[1]];
	float[][] Cr2 = new float[compHeight[2]][compWidth[2]];
	int index = 0;
	for (int y = 0; y < imageHeight; y++) {
	    for (int x = 0; x < imageWidth; x++) {
		int r = values[index] >> 16 & 0xff;
		int g = values[index] >> 8 & 0xff;
		int b = values[index] & 0xff;
		Y[y][x] = (float) (0.299 * (double) (float) r
				   + 0.587 * (double) (float) g
				   + 0.114 * (double) (float) b);
		Cb1[y][x] = 128.0F + (float) (-0.16874 * (double) (float) r
					      - 0.33126 * (double) (float) g
					      + 0.5 * (double) (float) b);
		Cr1[y][x] = 128.0F + (float) (0.5 * (double) (float) r
					      - 0.41869 * (double) (float) g
					      - 0.08131 * (double) (float) b);
		index++;
	    }
	}
	Components[0] = Y;
	Components[1] = Cb1;
	Components[2] = Cr1;
    }
    
    float[][] DownSample(float[][] C, int comp) {
	int inrow = 0;
	int incol = 0;
	float[][] output = new float[compHeight[comp]][compWidth[comp]];
	for (int outrow = 0; outrow < compHeight[comp]; outrow++) {
	    int bias = 1;
	    for (int outcol = 0; outcol < compWidth[comp]; outcol++) {
		output[outrow][outcol]
		    = (C[inrow][incol++] + C[inrow++][incol--]
		       + C[inrow][incol++] + C[inrow--][incol++]
		       + (float) bias) / 4.0F;
		bias ^= 0x3;
	    }
	    inrow += 2;
	    incol = 0;
	}
	return output;
    }
}
