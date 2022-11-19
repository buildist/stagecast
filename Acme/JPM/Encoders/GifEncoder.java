/* GifEncoder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package Acme.JPM.Encoders;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import Acme.IntHashtable;

public class GifEncoder extends ImageEncoder
{
    private boolean interlace = false;
    int width;
    int height;
    int[][] rgbPixels;
    IntHashtable colorHash;
    int Width;
    int Height;
    boolean Interlace;
    int curx;
    int cury;
    int CountDown;
    int Pass = 0;
    static final int EOF = -1;
    static final int BITS = 12;
    static final int HSIZE = 5003;
    int n_bits;
    int maxbits = 12;
    int maxcode;
    int maxmaxcode = 4096;
    int[] htab;
    int[] codetab;
    int hsize;
    int free_ent;
    boolean clear_flg;
    int g_init_bits;
    int ClearCode;
    int EOFCode;
    int cur_accum;
    int cur_bits;
    int[] masks;
    int a_count;
    byte[] accum;
    
    public GifEncoder(Image img, OutputStream out) throws IOException {
	super(img, out);
	htab = new int[5003];
	codetab = new int[5003];
	hsize = 5003;
	free_ent = 0;
	clear_flg = false;
	cur_accum = 0;
	cur_bits = 0;
	masks = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047,
			    4095, 8191, 16383, 32767, 65535 };
	accum = new byte[256];
    }
    
    public GifEncoder(Image img, OutputStream out, boolean interlace)
	throws IOException {
	super(img, out);
	htab = new int[5003];
	codetab = new int[5003];
	hsize = 5003;
	free_ent = 0;
	clear_flg = false;
	cur_accum = 0;
	cur_bits = 0;
	masks = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047,
			    4095, 8191, 16383, 32767, 65535 };
	accum = new byte[256];
	this.interlace = interlace;
    }
    
    public GifEncoder(ImageProducer prod, OutputStream out)
	throws IOException {
	super(prod, out);
	htab = new int[5003];
	codetab = new int[5003];
	hsize = 5003;
	free_ent = 0;
	clear_flg = false;
	cur_accum = 0;
	cur_bits = 0;
	masks = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047,
			    4095, 8191, 16383, 32767, 65535 };
	accum = new byte[256];
    }
    
    public GifEncoder
	(ImageProducer prod, OutputStream out, boolean interlace)
	throws IOException {
	super(prod, out);
	htab = new int[5003];
	codetab = new int[5003];
	hsize = 5003;
	free_ent = 0;
	clear_flg = false;
	cur_accum = 0;
	cur_bits = 0;
	masks = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047,
			    4095, 8191, 16383, 32767, 65535 };
	accum = new byte[256];
	this.interlace = interlace;
    }
    
    void encodeStart(int width, int height) throws IOException {
	this.width = width;
	this.height = height;
	rgbPixels = new int[height][width];
    }
    
    void encodePixels(int x, int y, int w, int h, int[] rgbPixels, int off,
		      int scansize) throws IOException {
	for (int row = 0; row < h; row++)
	    System.arraycopy(rgbPixels, row * scansize + off,
			     this.rgbPixels[y + row], x, w);
    }
    
    void encodeDone() throws IOException {
	int transparentIndex = -1;
	int transparentRgb = -1;
	colorHash = new IntHashtable();
	int index = 0;
	for (int row = 0; row < height; row++) {
	    int rowOffset = row * width;
	    for (int col = 0; col < width; col++) {
		int rgb = rgbPixels[row][col];
		boolean isTransparent = rgb >>> 24 < 128;
		if (isTransparent) {
		    if (transparentIndex < 0) {
			transparentIndex = index;
			transparentRgb = rgb;
		    } else if (rgb != transparentRgb)
			rgbPixels[row][col] = rgb = transparentRgb;
		}
		GifEncoderHashitem item
		    = (GifEncoderHashitem) colorHash.get(rgb);
		if (item == null) {
		    if (index >= 256)
			throw new IOException("too many colors for a GIF");
		    item
			= new GifEncoderHashitem(rgb, 1, index, isTransparent);
		    index++;
		    colorHash.put(rgb, item);
		} else
		    item.count++;
	    }
	}
	int logColors;
	if (index <= 2)
	    logColors = 1;
	else if (index <= 4)
	    logColors = 2;
	else if (index <= 16)
	    logColors = 4;
	else
	    logColors = 8;
	int mapSize = 1 << logColors;
	byte[] reds = new byte[mapSize];
	byte[] grns = new byte[mapSize];
	byte[] blus = new byte[mapSize];
	Enumeration e = colorHash.elements();
	while (e.hasMoreElements()) {
	    GifEncoderHashitem item = (GifEncoderHashitem) e.nextElement();
	    reds[item.index] = (byte) (item.rgb >> 16 & 0xff);
	    grns[item.index] = (byte) (item.rgb >> 8 & 0xff);
	    blus[item.index] = (byte) (item.rgb & 0xff);
	}
	GIFEncode(out, width, height, interlace, (byte) 0, transparentIndex,
		  logColors, reds, grns, blus);
    }
    
    byte GetPixel(int x, int y) throws IOException {
	GifEncoderHashitem item
	    = (GifEncoderHashitem) colorHash.get(rgbPixels[y][x]);
	if (item == null)
	    throw new IOException("color not found");
	return (byte) item.index;
    }
    
    static void writeString(OutputStream out, String str) throws IOException {
	byte[] buf = str.getBytes();
	out.write(buf);
    }
    
    void GIFEncode(OutputStream outs, int Width, int Height, boolean Interlace,
		   byte Background, int Transparent, int BitsPerPixel,
		   byte[] Red, byte[] Green, byte[] Blue) throws IOException {
	this.Width = Width;
	this.Height = Height;
	this.Interlace = Interlace;
	int ColorMapSize = 1 << BitsPerPixel;
	int TopOfs;
	int LeftOfs = TopOfs = 0;
	CountDown = Width * Height;
	Pass = 0;
	int InitCodeSize;
	if (BitsPerPixel <= 1)
	    InitCodeSize = 2;
	else
	    InitCodeSize = BitsPerPixel;
	curx = 0;
	cury = 0;
	writeString(outs, "GIF89a");
	Putword(Width, outs);
	Putword(Height, outs);
	byte B = -128;
	B |= 0x70;
	B |= (byte) (BitsPerPixel - 1);
	Putbyte(B, outs);
	Putbyte(Background, outs);
	Putbyte((byte) 0, outs);
	for (int i = 0; i < ColorMapSize; i++) {
	    Putbyte(Red[i], outs);
	    Putbyte(Green[i], outs);
	    Putbyte(Blue[i], outs);
	}
	if (Transparent != -1) {
	    Putbyte((byte) 33, outs);
	    Putbyte((byte) -7, outs);
	    Putbyte((byte) 4, outs);
	    Putbyte((byte) 1, outs);
	    Putbyte((byte) 0, outs);
	    Putbyte((byte) 0, outs);
	    Putbyte((byte) Transparent, outs);
	    Putbyte((byte) 0, outs);
	}
	Putbyte((byte) 44, outs);
	Putword(LeftOfs, outs);
	Putword(TopOfs, outs);
	Putword(Width, outs);
	Putword(Height, outs);
	if (Interlace)
	    Putbyte((byte) 64, outs);
	else
	    Putbyte((byte) 0, outs);
	Putbyte((byte) InitCodeSize, outs);
	compress(InitCodeSize + 1, outs);
	Putbyte((byte) 0, outs);
	Putbyte((byte) 59, outs);
    }
    
    void BumpPixel() {
	curx++;
	if (curx == Width) {
	    curx = 0;
	    if (!Interlace)
		cury++;
	    else {
		switch (Pass) {
		case 0:
		    cury += 8;
		    if (cury >= Height) {
			Pass++;
			cury = 4;
		    }
		    break;
		case 1:
		    cury += 8;
		    if (cury >= Height) {
			Pass++;
			cury = 2;
		    }
		    break;
		case 2:
		    cury += 4;
		    if (cury >= Height) {
			Pass++;
			cury = 1;
		    }
		    break;
		case 3:
		    cury += 2;
		    break;
		}
	    }
	}
    }
    
    int GIFNextPixel() throws IOException {
	if (CountDown == 0)
	    return -1;
	CountDown--;
	byte r = GetPixel(curx, cury);
	BumpPixel();
	return r & 0xff;
    }
    
    void Putword(int w, OutputStream outs) throws IOException {
	Putbyte((byte) (w & 0xff), outs);
	Putbyte((byte) (w >> 8 & 0xff), outs);
    }
    
    void Putbyte(byte b, OutputStream outs) throws IOException {
	outs.write(b);
    }
    
    final int MAXCODE(int n_bits) {
	return (1 << n_bits) - 1;
    }
    
    void compress(int init_bits, OutputStream outs) throws IOException {
	g_init_bits = init_bits;
	clear_flg = false;
	n_bits = g_init_bits;
	maxcode = MAXCODE(n_bits);
	ClearCode = 1 << init_bits - 1;
	EOFCode = ClearCode + 1;
	free_ent = ClearCode + 2;
	char_init();
	int ent = GIFNextPixel();
	int hshift = 0;
	for (int fcode = hsize; fcode < 65536; fcode *= 2)
	    hshift++;
	hshift = 8 - hshift;
	int hsize_reg = hsize;
	cl_hash(hsize_reg);
	output(ClearCode, outs);
	int c;
    while_0_:
	while ((c = GIFNextPixel()) != -1) {
	    int fcode = (c << maxbits) + ent;
	    int i = c << hshift ^ ent;
	    if (htab[i] == fcode)
		ent = codetab[i];
	    else {
		if (htab[i] >= 0) {
		    int disp = hsize_reg - i;
		    if (i == 0)
			disp = 1;
		    do {
			if ((i -= disp) < 0)
			    i += hsize_reg;
			if (htab[i] == fcode) {
			    ent = codetab[i];
			    continue while_0_;
			}
		    } while (htab[i] >= 0);
		}
		output(ent, outs);
		ent = c;
		if (free_ent < maxmaxcode) {
		    codetab[i] = free_ent++;
		    htab[i] = fcode;
		} else
		    cl_block(outs);
	    }
	}
	output(ent, outs);
	output(EOFCode, outs);
    }
    
    void output(int code, OutputStream outs) throws IOException {
	cur_accum &= masks[cur_bits];
	if (cur_bits > 0)
	    cur_accum |= code << cur_bits;
	else
	    cur_accum = code;
	for (cur_bits += n_bits; cur_bits >= 8; cur_bits -= 8) {
	    char_out((byte) (cur_accum & 0xff), outs);
	    cur_accum >>= 8;
	}
	if (free_ent > maxcode || clear_flg) {
	    if (clear_flg) {
		maxcode = MAXCODE(n_bits = g_init_bits);
		clear_flg = false;
	    } else {
		n_bits++;
		if (n_bits == maxbits)
		    maxcode = maxmaxcode;
		else
		    maxcode = MAXCODE(n_bits);
	    }
	}
	if (code == EOFCode) {
	    for (/**/; cur_bits > 0; cur_bits -= 8) {
		char_out((byte) (cur_accum & 0xff), outs);
		cur_accum >>= 8;
	    }
	    flush_char(outs);
	}
    }
    
    void cl_block(OutputStream outs) throws IOException {
	cl_hash(hsize);
	free_ent = ClearCode + 2;
	clear_flg = true;
	output(ClearCode, outs);
    }
    
    void cl_hash(int hsize) {
	for (int i = 0; i < hsize; i++)
	    htab[i] = -1;
    }
    
    void char_init() {
	a_count = 0;
    }
    
    void char_out(byte c, OutputStream outs) throws IOException {
	accum[a_count++] = c;
	if (a_count >= 254)
	    flush_char(outs);
    }
    
    void flush_char(OutputStream outs) throws IOException {
	if (a_count > 0) {
	    outs.write(a_count);
	    outs.write(accum, 0, a_count);
	    a_count = 0;
	}
    }
}
