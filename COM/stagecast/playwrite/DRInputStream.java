/* DRInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Point;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.util.Vector;

public class DRInputStream extends BufferedInputStream
    implements Debug.Constants
{
    private static final int FORMAT_NUMBER = 3;
    private static final int DEFAULT_BUFFER_SIZE = 16192;
    private static final int MARK_LIMIT = 16192;
    private static final int BYTE_BUFFER_SIZE = 4;
    private static final float MAX_RGB_MAC = 65535.0F;
    private boolean initialized = false;
    private DRTranslator translator = null;
    private int formatVersion = 3;
    private byte[] byteBuffer = null;
    private World curWorld;
    private int curPos;
    private int curMark;
    private InputStream inputStream;
    
    static boolean validateContent(InputStream in) {
	boolean valid = false;
	int length = 0;
	in.mark(8);
	try {
	    byte[] buf = new byte[4];
	    in.read(buf);
	    int count = in.read(buf);
	    if (count == 4) {
		int id = buf[0] << 24 | buf[1] << 16 | buf[2] << 8 | buf[3];
		valid = id == 1467116644;
	    }
	} catch (IOException ioexception) {
	    /* empty */
	} finally {
	    try {
		in.reset();
	    } catch (IOException ioexception) {
		/* empty */
	    }
	}
	return valid;
    }
    
    public DRInputStream(InputStream s, World world) {
	this(s, 16192, world);
    }
    
    public DRInputStream(InputStream s, int size, World world) {
	super(s, size);
	curWorld = world;
	curPos = 0;
	curMark = -1;
	inputStream = s;
    }
    
    public final boolean initializeStream() {
	translator = new DRTranslator(this, curWorld);
	if (translator != null) {
	    byteBuffer = new byte[4];
	    if (byteBuffer != null)
		initialized = true;
	}
	return initialized;
    }
    
    public synchronized int read() throws IOException {
	int byteRead = super.read();
	if (byteRead != -1)
	    curPos++;
	PlaywriteRoot.getProgressDialog().incrementTotalDone(1);
	return byteRead;
    }
    
    public final int read(byte[] b) throws IOException {
	return read(b, 0, b.length);
    }
    
    public synchronized int read(byte[] b, int off, int len)
	throws IOException {
	int bytesToRead = len;
	int totalBytesRead = 0;
	int bytesRead;
	for (/**/; bytesToRead > 0; bytesToRead -= bytesRead) {
	    bytesRead = super.read(b, off + totalBytesRead, bytesToRead);
	    if (bytesRead == -1) {
		totalBytesRead = -1;
		break;
	    }
	    PlaywriteRoot.getProgressDialog().incrementTotalDone(bytesRead);
	    totalBytesRead += bytesRead;
	}
	if (totalBytesRead != -1)
	    curPos = curPos + totalBytesRead;
	return totalBytesRead;
    }
    
    public synchronized long skip(long n) throws IOException {
	long bytesSkipped = super.skip(n);
	if (bytesSkipped > 0L)
	    curPos = curPos + (int) bytesSkipped;
	PlaywriteRoot.getProgressDialog()
	    .incrementTotalDone((int) bytesSkipped);
	return bytesSkipped;
    }
    
    public synchronized int available() throws IOException {
	return super.available();
    }
    
    public synchronized void mark(int readlimit) {
	super.mark(16192);
	curMark = curPos;
    }
    
    public synchronized void reset() throws IOException {
	super.reset();
	if (markpos != -1)
	    curPos = curMark;
    }
    
    public boolean markSupported() {
	return super.markSupported();
    }
    
    private final void setStreamPos(int newPos, boolean isAbsolute) {
	if (isAbsolute) {
	    long deltaPos = (long) (newPos - curPos);
	    if (deltaPos > 0L) {
		try {
		    skip(deltaPos);
		} catch (IOException ioexception) {
		    /* empty */
		}
	    } else if (deltaPos < 0L) {
		int resetPos = pos + (int) deltaPos;
		if (resetPos >= 0) {
		    pos = resetPos;
		    curPos = curPos + (int) deltaPos;
		} else
		    Debug.print
			("debug.dr",
			 "DRInputStream.setStreamPos: buffer underrun error");
	    }
	} else {
	    try {
		skip((long) newPos);
	    } catch (IOException ioexception) {
		/* empty */
	    }
	}
    }
    
    private final int getStreamPos() {
	return curPos;
    }
    
    public final boolean readBoolean() throws IOException {
	int ch = read();
	if (ch < 0)
	    throw new EOFException();
	PlaywriteRoot.getProgressDialog().incrementTotalDone(1);
	return ch != 0;
    }
    
    public final byte readByte() {
	int ch = 0;
	try {
	    ch = read();
	} catch (IOException ioexception) {
	    /* empty */
	}
	PlaywriteRoot.getProgressDialog().incrementTotalDone(1);
	return (byte) ch;
    }
    
    public final int readUnsignedByte() throws IOException {
	int ch = read();
	if (ch < 0)
	    throw new EOFException();
	PlaywriteRoot.getProgressDialog().incrementTotalDone(1);
	return ch;
    }
    
    public final short readShort() {
	int ch1 = 0;
	int ch2 = 0;
	try {
	    ch1 = read();
	    ch2 = read();
	} catch (IOException ioexception) {
	    /* empty */
	}
	PlaywriteRoot.getProgressDialog().incrementTotalDone(2);
	return (short) ((ch1 << 8) + ch2);
    }
    
    public final int readUnsignedShort() throws IOException {
	int ch1 = read();
	int ch2 = read();
	if ((ch1 | ch2) < 0)
	    throw new EOFException();
	PlaywriteRoot.getProgressDialog().incrementTotalDone(2);
	return (ch1 << 8) + ch2;
    }
    
    public final char readChar() throws IOException {
	int ch1 = read();
	int ch2 = read();
	if ((ch1 | ch2) < 0)
	    throw new EOFException();
	PlaywriteRoot.getProgressDialog().incrementTotalDone(2);
	return (char) ((ch1 << 8) + ch2);
    }
    
    public final int readInt() {
	int ch1 = 0;
	int ch2 = 0;
	int ch3 = 0;
	int ch4 = 0;
	int intReturn = -1;
	try {
	    ch1 = read();
	    ch2 = read();
	    ch3 = read();
	    ch4 = read();
	} catch (IOException ioexception) {
	    /* empty */
	}
	if ((ch1 | ch2 | ch3 | ch4) < 0)
	    Debug.print("debug.dr",
			"DRInputStream.readInt: EOFException error");
	else
	    intReturn = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
	PlaywriteRoot.getProgressDialog().incrementTotalDone(4);
	return intReturn;
    }
    
    public final long readLong() throws IOException {
	return (long) (readInt() << 32) + ((long) readInt() & 0xffffffffL);
    }
    
    public final float readFloat() throws IOException {
	return Float.intBitsToFloat(readInt());
    }
    
    public final double readDouble() throws IOException {
	return Double.longBitsToDouble(readLong());
    }
    
    public final void setFormatVersion(int inVersion) {
	formatVersion = inVersion;
    }
    
    public final int getFormatVersion() {
	return formatVersion;
    }
    
    public final int getFormatNumber() {
	return formatVersion << 8 >> 8;
    }
    
    public final Object readAll() {
	Object obj = null;
	setStreamPos(0, true);
	int fileLength = readInt();
	if (fileLength > 0) {
	    PlaywriteRoot.getProgressDialog().setTotalCount(fileLength);
	    setStreamPos(0, true);
	    obj = readObject();
	} else
	    Debug.print("debug.dr",
			"DRInputStream.readAll: file length invalid error");
	return obj;
    }
    
    public final Object readObject() {
	Object obj = null;
	if (initialized) {
	    int dataStart = getStreamPos();
	    int dataLength = readInt();
	    int classID = readInt();
	    if (classID != 1853189228)
		obj = translator.CreateObject(classID);
	    setStreamPos(dataStart + dataLength, true);
	}
	return obj;
    }
    
    public final Vector readList(Vector v) {
	int dataStart = getStreamPos();
	int dataLength = readInt();
	int classID = readInt();
	if (classID == 1818850164) {
	    while (getStreamPos() < dataStart + dataLength) {
		Object obj = readObject();
		if (obj != null)
		    v.addElement(obj);
	    }
	} else if (classID != 1853189228)
	    Debug.print("debug.dr",
			"CDRInputStream.readList: not a list, classID = ",
			Integer.toHexString(classID));
	setStreamPos(dataStart + dataLength, true);
	return v;
    }
    
    public final Vector readList() {
	return readList(new Vector(10));
    }
    
    public final boolean readDRBoolean() {
	if (readInt() == 0)
	    return false;
	return true;
    }
    
    public final int[] readInt32Array() {
	int currentInt = 0;
	int dataStart = getStreamPos();
	int dataLength = readInt();
	int classID = readInt();
	int[] values = new int[dataLength];
	int i = 0;
	if (classID == 1282303809) {
	    while (getStreamPos() < dataStart + dataLength) {
		values[i] = readInt();
		i++;
	    }
	} else
	    Debug.print
		("debug.dr",
		 "DRInputStream.readInt32Array: not an int_array error");
	setStreamPos(dataStart + dataLength, true);
	return values;
    }
    
    public final byte[] readBytes() {
	int dataLength = 0;
	int classID = 0;
	int handleLength = 0;
	byte[] byteData = null;
	int dataStart = getStreamPos();
	dataLength = readInt();
	classID = readInt();
	handleLength = dataLength - 8;
	if (classID == 1752065132) {
	    if (handleLength > 0) {
		byteData = new byte[handleLength];
		if (byteData != null) {
		    try {
			read(byteData, 0, handleLength);
		    } catch (IOException ioexception) {
			/* empty */
		    }
		}
	    } else
		Debug.print
		    ("debug.dr",
		     "DRInputStream.readBytes: handle length <= 0 error");
	} else
	    Debug.print("debug.dr",
			"DRInputStream.readBytes: not a handle error");
	setStreamPos(dataStart + dataLength, true);
	return byteData;
    }
    
    public final Color readRGB() {
	int iRed = 0;
	int iGreen = 0;
	int iBlue = 0;
	float fRed = 0.0F;
	float fGreen = 0.0F;
	float fBlue = 0.0F;
	Color returnColor = null;
	try {
	    iRed = readUnsignedShort();
	    iGreen = readUnsignedShort();
	    iBlue = readUnsignedShort();
	} catch (IOException ioexception) {
	    /* empty */
	}
	if ((iRed | iGreen | iBlue) >= 0) {
	    fRed = (float) iRed;
	    fGreen = (float) iGreen;
	    fBlue = (float) iBlue;
	    fRed /= 65535.0F;
	    fGreen /= 65535.0F;
	    fBlue /= 65535.0F;
	    returnColor = new Color(fRed, fGreen, fBlue);
	}
	setStreamPos(2, false);
	return returnColor;
    }
    
    public final String readPString() throws IOException {
	int strLength = 0;
	int bytesRead = 0;
	StringBuffer outString = new StringBuffer();
	strLength = readUnsignedByte();
	if (strLength > 0) {
	    byte[] pascalStr = new byte[strLength];
	    if (pascalStr != null) {
		bytesRead = read(pascalStr, 0, strLength);
		if (bytesRead > 0) {
		    try {
			for (int i = 0; i < bytesRead; i++)
			    outString.insert(i, (char) pascalStr[i]);
		    } catch (StringIndexOutOfBoundsException stringindexoutofboundsexception) {
			/* empty */
		    }
		    int paddingBytes = (bytesRead + 1) % 4;
		    if (paddingBytes != 0) {
			paddingBytes = 4 - paddingBytes;
			setStreamPos(paddingBytes, false);
			bytesRead += paddingBytes;
		    }
		}
	    }
	} else if (strLength == 0) {
	    strLength = 3;
	    byte[] pascalStr = new byte[strLength];
	    if (pascalStr != null) {
		read(pascalStr, 0, strLength);
		try {
		    outString.insert(0, "");
		} catch (StringIndexOutOfBoundsException stringindexoutofboundsexception) {
		    /* empty */
		}
	    }
	}
	return outString.toString();
    }
    
    public final Point readPoint() {
	int y = readShort();
	int x = readShort();
	Point pt = new Point(x, y);
	return pt;
    }
    
    public final Rect readRect() {
	int top = readShort();
	int left = readShort();
	int bottom = readShort();
	int right = readShort();
	int width = right - left;
	int height = bottom - top;
	Rect rect = new Rect(left, top, width, height);
	return rect;
    }
    
    public final void readWorld() {
	try {
	    if (initializeStream()) {
		Variable.setReadingCocoaWorld(true);
		readAll();
	    }
	} finally {
	    dropData();
	    Variable.setReadingCocoaWorld(false);
	}
    }
    
    public final void dropData() {
	if (translator != null)
	    translator.dropData();
	translator = null;
	byteBuffer = null;
	curWorld = null;
	inputStream = null;
    }
}
