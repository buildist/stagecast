/* ImageIO - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.FileChooser;
import COM.stagecast.jpeg.JpegEncoder;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class ImageIO implements Debug.Constants, ResourceIDs.DialogIDs
{
    private static interface ImageIODirectory
    {
	public String getDirectory();
	
	public void setDirectory(String string);
    }
    
    static Bitmap importPicture(World world, Named named) {
	ImageIODirectory directory = new ImageIODirectory() {
	    public String getDirectory() {
		return World.getPictureDirectory();
	    }
	    
	    public void setDirectory(String directory_0_) {
		World.setPictureDirectory(directory_0_);
	    }
	};
	return importBitmap(named, directory, Resource.getText("dialog cap"));
    }
    
    static Bitmap importBackground(World world, Named named) {
	ImageIODirectory directory = new ImageIODirectory() {
	    public String getDirectory() {
		return World.getBackgroundDirectory();
	    }
	    
	    public void setDirectory(String directory_1_) {
		World.setBackgroundDirectory(directory_1_);
	    }
	};
	return importBitmap(named, directory, Resource.getText("dialog cab"));
    }
    
    static boolean importAllImages(FileIO.FileIterator handler,
				   String chooserTitle) {
	FileChooser chooser = new FileChooser(PlaywriteRoot.getMainRootView(),
					      chooserTitle, 0);
	chooser.setDirectory(World.getPictureDirectory());
	chooser.showModally();
	String fname = chooser.file();
	if (fname == null)
	    return false;
	World.setPictureDirectory(chooser.directory());
	FileIO.iterateOverDirectory(chooser.directory(), handler, false, null);
	return true;
    }
    
    private static Bitmap importBitmap(Named named, ImageIODirectory directory,
				       String chooserTitle) {
	FileChooser chooser = new FileChooser(PlaywriteRoot.getMainRootView(),
					      chooserTitle, 0);
	chooser.setDirectory(directory.getDirectory());
	chooser.showModally();
	String fname = chooser.file();
	if (fname == null)
	    return null;
	directory.setDirectory(chooser.directory());
	fname = chooser.directory() + chooser.file();
	return importBitmapNamed(named, fname, true);
    }
    
    public static Bitmap importBitmapNamed(Named named, String fname,
					   boolean showWarning) {
	if (!isLegalBitmap(fname)) {
	    if (showWarning)
		PlaywriteDialog.warning("dialog bif", true);
	    return null;
	}
	Bitmap bitmap = null;
	try {
	    TempFileChunkManager mgr = PlaywriteRoot.getTempManager();
	    FileInputStream fis = new FileInputStream(fname);
	    String id = mgr.fillNewEntry(fis);
	    bitmap = (BitmapManager.createNativeBitmapManager
		      (new TempStreamProducer(null, id)));
	} catch (OutOfMemoryError e) {
	    throw e;
	} catch (Throwable throwable) {
	    Debug.print(true, "Error importing ", fname);
	}
	File file = new File(fname);
	if (named != null)
	    named.setName
		(Util.dePercentString(Util.getFilePart(file.getName())));
	return bitmap;
    }
    
    static Bitmap convertToJpeg(Bitmap original, int quality)
	throws IOException {
	Bitmap jpeg = null;
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	JpegEncoder je = new JpegEncoder(original.awtImage(), quality, baos);
	je.Compress();
	baos.close();
	TempFileChunkManager mgr = PlaywriteRoot.getTempManager();
	String id
	    = mgr.fillNewEntry(new ByteArrayInputStream(baos.toByteArray()));
	jpeg = (BitmapManager.createNativeBitmapManager
		(new TempStreamProducer(null, id), original.width(),
		 original.height()));
	original.flush();
	return jpeg;
    }
    
    static boolean isLegalBitmap(String f) {
	java.io.InputStream is = null;
	int imageCount = 0;
	byte[] buffer = new byte[256];
	byte[] gif_header = { 71, 73, 70 };
	byte[] jpeg_header = { -1, -40 };
	try {
	    is = new BufferedInputStream(new FileInputStream(f), 16384);
	    is.read(buffer, 0, 6);
	    if (buffer[0] != gif_header[0] || buffer[1] != gif_header[1]
		|| buffer[2] != gif_header[2]) {
		if (buffer[0] == jpeg_header[0] && buffer[1] == jpeg_header[1])
		    return true;
		return false;
	    }
	    is.read(buffer, 0, 7);
	    if ((buffer[4] & 0x80) != 0) {
		int gct_size = 1 << (buffer[4] & 0x7) + 1;
		is.read(buffer, 0, gct_size);
		is.read(buffer, 0, gct_size);
		is.read(buffer, 0, gct_size);
	    }
	    int separator;
	while_4_:
	    do {
		separator = is.read();
		switch (separator) {
		case 44: {
		    imageCount++;
		    is.read(buffer, 0, 10);
		    if ((buffer[8] & 0x80) != 0) {
			int lct_size = 1 << (buffer[8] & 0x7) + 1;
			is.read(buffer, 0, lct_size);
			is.read(buffer, 0, lct_size);
			is.read(buffer, 0, lct_size);
		    }
		    int count;
		    do {
			count = is.read();
			if (count > 0)
			    is.read(buffer, 0, count);
		    } while (count > 0);
		    break;
		}
		case 33: {
		    int type = is.read();
		    int size = is.read();
		    is.read(buffer, 0, size);
		    int count;
		    do {
			count = is.read();
			if (count > 0)
			    is.read(buffer, 0, count);
		    } while (count > 0);
		    break;
		}
		default:
		    Debug.print(true, "Separator = " + separator,
				" GIF validation out of sync");
		    break while_4_;
		case -1:
		case 59:
		    /* empty */
		}
		if (separator == 59)
		    break;
	    } while (separator != -1);
	} catch (OutOfMemoryError e) {
	    throw e;
	} catch (Throwable throwable) {
	    /* empty */
	} finally {
	    if (is != null) {
		try {
		    is.close();
		} catch (Throwable throwable) {
		    /* empty */
		}
	    }
	}
	return imageCount == 1;
    }
}
