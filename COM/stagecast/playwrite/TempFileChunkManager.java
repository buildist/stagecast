/* TempFileChunkManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

import COM.stagecast.ifc.netscape.util.Hashtable;

public class TempFileChunkManager implements Debug.Constants
{
    private DecimalFormat idFormat = new DecimalFormat("TMP0000000");
    private File _source = null;
    private int _nextid;
    private volatile boolean _busy;
    private Hashtable _directory;
    private long _nextAvail;
    private String _current;
    
    private class ChunkInfo
    {
	long _start;
	long _length;
	
	ChunkInfo(long start) {
	    _start = start;
	}
    }
    
    private class EmbeddedOutStream extends OutputStream
    {
	RandomAccessFile _file;
	long _written;
	
	EmbeddedOutStream(RandomAccessFile file) {
	    _file = file;
	    _written = 0L;
	}
	
	public void close() throws IOException {
	    try {
		_file.close();
	    } finally {
		TempFileChunkManager.this.endOutStream(_written);
	    }
	}
	
	public void flush() {
	    /* empty */
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
	    _file.write(b, off, len);
	    _written += (long) len;
	}
	
	public void write(int b) throws IOException {
	    _file.write(b);
	    _written++;
	}
    }
    
    private class EmbeddedInStream extends InputStream
    {
	private RandomAccessFile _source;
	private long _length;
	private long _bytesAvail;
	
	EmbeddedInStream(RandomAccessFile source, long length) {
	    _source = source;
	    _length = length;
	    _bytesAvail = _length;
	}
	
	public int available() {
	    return (int) _bytesAvail;
	}
	
	public void close() throws IOException {
	    try {
		_source.close();
	    } finally {
		_bytesAvail = 0L;
		TempFileChunkManager.this.endInStream();
	    }
	}
	
	public void mark(int limit) {
	    /* empty */
	}
	
	public boolean markSupported() {
	    return false;
	}
	
	public int read() throws IOException {
	    if (_bytesAvail > 0L) {
		_bytesAvail--;
		return _source.read();
	    }
	    return -1;
	}
	
	public int read(byte[] buffer, int offset, int length)
	    throws IOException {
	    if (_bytesAvail <= 0L)
		return -1;
	    if ((long) length > _bytesAvail)
		length = (int) _bytesAvail;
	    int count = _source.read(buffer, offset, length);
	    _bytesAvail -= (long) count;
	    return count;
	}
	
	public long skip(long n) throws IOException {
	    if (n > _bytesAvail)
		n = _bytesAvail;
	    long result = (long) _source.skipBytes((int) n);
	    _bytesAvail -= n;
	    return result;
	}
    }
    
    private class TempInStream extends ByteArrayInputStream
    {
	public TempInStream(byte[] chunk) {
	    super(chunk);
	}
	
	public void close() throws IOException {
	    try {
		super.close();
	    } finally {
		TempFileChunkManager.this.endInStream();
	    }
	}
    }
    
    private class TempOutStream extends ByteArrayOutputStream
    {
	public void close() throws IOException {
	    try {
		super.close();
	    } finally {
		TempFileChunkManager.this.endOutStream(0L);
	    }
	}
    }
    
    TempFileChunkManager() {
	if (PlaywriteRoot.isApplication()) {
	    try {
		File tempFile = Util.createTempFile(FileIO.getTempDir());
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.close();
		_source = tempFile;
		Debug.print("debug.temp", "Using temp file ", tempFile);
	    } catch (Exception e) {
		Debug.print("debug.temp", "Unable to use temp file: ", e);
	    }
	}
	if (_source == null)
	    Debug.print("debug.temp", "Using RAM for temp space");
	_nextid = 0;
	_busy = false;
	_directory = new Hashtable();
	_nextAvail = 0L;
    }
    
    void done() {
	if (_source != null)
	    _source.delete();
	_source = null;
	_directory = null;
    }
    
    synchronized String uniqueEntryName() {
	return idFormat.format((long) ++_nextid);
    }
    
    OutputStream createNewEntry(String name) throws IOException {
	Debug.print("debug.temp", "Creating temp entry: ", name);
	acquire(name);
	OutputStream os;
	if (_source == null) {
	    os = new TempOutStream();
	    _directory.put(_current, os);
	} else {
	    _directory.put(_current, new ChunkInfo(_nextAvail));
	    RandomAccessFile outFile = new RandomAccessFile(_source, "rw");
	    outFile.seek(_nextAvail);
	    os = new EmbeddedOutStream(outFile);
	}
	return os;
    }
    
    void fillEntry(InputStream is, OutputStream os) {
	try {
	    byte[] buffer = new byte[4096];
	    int count = 0;
	    while (count >= 0) {
		count = is.read(buffer);
		if (count > 0)
		    os.write(buffer, 0, count);
	    }
	} catch (IOException e) {
	    Debug.stackTrace(e);
	} finally {
	    if (os != null) {
		try {
		    os.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	}
    }
    
    String fillNewEntry(InputStream is) {
	String entry = uniqueEntryName();
	OutputStream os = null;
	try {
	    os = createNewEntry(entry);
	    fillEntry(is, os);
	} catch (IOException e) {
	    Debug.stackTrace(e);
	} finally {
	    if (is != null) {
		try {
		    is.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	}
	return entry;
    }
    
    private void endOutStream(long length) {
	if (_source == null) {
	    ByteArrayOutputStream baos
		= (ByteArrayOutputStream) _directory.get(_current);
	    _directory.put(_current, baos.toByteArray());
	} else {
	    ChunkInfo info = (ChunkInfo) _directory.get(_current);
	    info._length = length;
	    _nextAvail += length;
	}
	release();
    }
    
    InputStream getEntry(String name) throws IOException {
	acquire(name);
	InputStream is;
	if (_source == null) {
	    byte[] chunk = (byte[]) _directory.get(_current);
	    is = new TempInStream(chunk);
	} else {
	    ChunkInfo info = (ChunkInfo) _directory.get(_current);
	    RandomAccessFile inFile = new RandomAccessFile(_source, "r");
	    inFile.seek(info._start);
	    is = new EmbeddedInStream(inFile, info._length);
	}
	release();
	return is;
    }
    
    private void endInStream() {
	/* empty */
    }
    
    private synchronized void acquire(String item) {
	Debug.print("debug.temp", "Aquiring temp lock: ", item);
	while (_busy) {
	    try {
		this.wait();
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	}
	_busy = true;
	_current = item;
    }
    
    private synchronized void release() {
	Debug.print("debug.temp", "Releasing temp lock");
	_busy = false;
	_current = null;
	this.notifyAll();
    }
}
