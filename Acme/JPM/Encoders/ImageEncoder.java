/* ImageEncoder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package Acme.JPM.Encoders;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

public abstract class ImageEncoder implements ImageConsumer
{
    protected OutputStream out;
    private ImageProducer producer;
    private int width = -1;
    private int height = -1;
    private int hintflags = 0;
    private boolean started = false;
    private volatile boolean encoding;
    private IOException iox;
    private static final ColorModel rgbModel = ColorModel.getRGBdefault();
    private Hashtable props = null;
    private boolean accumulate = false;
    private int[] accumulator;
    
    public ImageEncoder(Image img, OutputStream out) throws IOException {
	this(img.getSource(), out);
    }
    
    public ImageEncoder(ImageProducer producer, OutputStream out)
	throws IOException {
	this.producer = producer;
	this.out = out;
    }
    
    abstract void encodeStart(int i, int i_0_) throws IOException;
    
    abstract void encodePixels(int i, int i_1_, int i_2_, int i_3_, int[] is,
			       int i_4_, int i_5_) throws IOException;
    
    abstract void encodeDone() throws IOException;
    
    public synchronized void encode() throws IOException {
	encoding = true;
	iox = null;
	producer.startProduction(this);
	while (encoding) {
	    try {
		this.wait();
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	}
	if (iox != null)
	    throw iox;
    }
    
    private void encodePixelsWrapper
	(int x, int y, int w, int h, int[] rgbPixels, int off, int scansize)
	throws IOException {
	if (!started) {
	    started = true;
	    encodeStart(width, height);
	    if ((hintflags & 0x2) == 0) {
		accumulate = true;
		accumulator = new int[width * height];
	    }
	}
	if (accumulate) {
	    for (int row = 0; row < h; row++)
		System.arraycopy(rgbPixels, row * scansize + off, accumulator,
				 (y + row) * width + x, w);
	} else
	    encodePixels(x, y, w, h, rgbPixels, off, scansize);
    }
    
    private void encodeFinish() throws IOException {
	if (accumulate) {
	    encodePixels(0, 0, width, height, accumulator, 0, width);
	    accumulator = null;
	    accumulate = false;
	}
    }
    
    private synchronized void stop() {
	encoding = false;
	this.notifyAll();
    }
    
    public void setDimensions(int width, int height) {
	this.width = width;
	this.height = height;
    }
    
    public void setProperties(Hashtable props) {
	this.props = props;
    }
    
    public void setColorModel(ColorModel model) {
	/* empty */
    }
    
    public void setHints(int hintflags) {
	this.hintflags = hintflags;
    }
    
    public void setPixels(int x, int y, int w, int h, ColorModel model,
			  byte[] pixels, int off, int scansize) {
	int[] rgbPixels = new int[w];
	for (int row = 0; row < h; row++) {
	    int rowOff = off + row * scansize;
	    for (int col = 0; col < w; col++)
		rgbPixels[col] = model.getRGB(pixels[rowOff + col] & 0xff);
	    try {
		encodePixelsWrapper(x, y + row, w, 1, rgbPixels, 0, w);
	    } catch (IOException e) {
		iox = e;
		stop();
		break;
	    }
	}
    }
    
    public void setPixels(int x, int y, int w, int h, ColorModel model,
			  int[] pixels, int off, int scansize) {
	if (model == rgbModel) {
	    try {
		encodePixelsWrapper(x, y, w, h, pixels, off, scansize);
	    } catch (IOException e) {
		iox = e;
		stop();
	    }
	} else {
	    int[] rgbPixels = new int[w];
	    for (int row = 0; row < h; row++) {
		int rowOff = off + row * scansize;
		for (int col = 0; col < w; col++)
		    rgbPixels[col] = model.getRGB(pixels[rowOff + col]);
		try {
		    encodePixelsWrapper(x, y + row, w, 1, rgbPixels, 0, w);
		} catch (IOException e) {
		    iox = e;
		    stop();
		    break;
		}
	    }
	}
    }
    
    public void imageComplete(int status) {
	producer.removeConsumer(this);
	if (status == 4)
	    iox = new IOException("image aborted");
	else {
	    try {
		encodeFinish();
		encodeDone();
	    } catch (IOException e) {
		iox = e;
	    }
	}
	stop();
    }
}
