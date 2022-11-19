/* StreamedImageProducer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.util.Vector;

public class StreamedImageProducer implements Debug.Constants, ImageProducer
{
    private StreamProducer _source;
    private ColorModel _colorModel;
    private int _width;
    private int _height;
    private Vector _consumers;
    
    public StreamedImageProducer(StreamProducer source, int width,
				 int height) {
	_source = source;
	_colorModel = ColorModel.getRGBdefault();
	_width = width;
	_height = height;
	_consumers = new Vector(2);
    }
    
    StreamProducer getSource() {
	return _source;
    }
    
    ColorModel getColorModel() {
	return _colorModel;
    }
    
    int getWidth() {
	return _width;
    }
    
    int getHeight() {
	return _height;
    }
    
    public StreamProducer getMediaSource() {
	return _source;
    }
    
    public void setMediaSource(StreamProducer source) {
	_source = source;
    }
    
    public synchronized void addConsumer(ImageConsumer ic) {
	if (!_consumers.contains(ic)) {
	    _consumers.addElement(ic);
	    try {
		int[] pixels = new int[_width * _height];
		readPixelData(pixels);
		ic.setDimensions(_width, _height);
		ic.setColorModel(ColorModel.getRGBdefault());
		ic.setHints(30);
		ic.setPixels(0, 0, _width, _height, ColorModel.getRGBdefault(),
			     pixels, 0, _width);
		ic.imageComplete(3);
	    } catch (Exception e) {
		ic.imageComplete(1);
		Debug.stackTrace(e);
	    } finally {
		removeConsumer(ic);
	    }
	}
    }
    
    public synchronized boolean isConsumer(ImageConsumer ic) {
	return _consumers.contains(ic);
    }
    
    public synchronized void removeConsumer(ImageConsumer ic) {
	_consumers.removeElement(ic);
    }
    
    public void startProduction(ImageConsumer ic) {
	addConsumer(ic);
    }
    
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
	/* empty */
    }
    
    private void readPixelData(int[] pixmap) throws IOException {
	ASSERT.isNotNull(_source);
	int pixel = 0;
	byte[] rawbytes = _source.getDataChunk();
	for (int i = 4; i <= rawbytes.length; i += 4)
	    pixmap[pixel++]
		= ((rawbytes[i - 4] & 0xff) << 24
		   | (rawbytes[i - 3] & 0xff) << 16
		   | (rawbytes[i - 2] & 0xff) << 8 | rawbytes[i - 1] & 0xff);
    }
}
