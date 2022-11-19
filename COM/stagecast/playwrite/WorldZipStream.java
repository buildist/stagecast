/* WorldZipStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import COM.stagecast.ifc.netscape.util.Hashtable;

class WorldZipStream extends ZipInputStream implements Debug.Constants
{
    private boolean _processingMedia = false;
    private Hashtable _mediaNameMap;
    private byte[] _worldBuf;
    private boolean _inflateWorld;
    
    WorldZipStream(InputStream is, Hashtable map) throws IOException {
	super(is);
	_mediaNameMap = map;
    }
    
    World readWorld() throws IOException {
	World world = null;
	ByteArrayInputStream bais = new ByteArrayInputStream(_worldBuf);
	world = processWorld(bais, _inflateWorld);
	_worldBuf = null;
	return world;
    }
    
    private World processWorld(InputStream worldStream, boolean inflate)
	throws IOException {
	if (inflate)
	    worldStream = new InflaterInputStream(worldStream);
	World world
	    = new WorldInStream(worldStream, _mediaNameMap).readWorld();
	return world;
    }
    
    void readMedia() throws IOException {
	_processingMedia = true;
	try {
	    ZipEntry ze;
	    while ((ze = this.getNextEntry()) != null) {
		String mediaName = ze.getName();
		if ("iworld".equals(mediaName) || "world".equals(mediaName))
		    captureWorld(mediaName);
		else
		    processMedia(mediaName);
		this.closeEntry();
		PlaywriteRoot.incrementProgress(1, 50);
	    }
	} finally {
	    _processingMedia = false;
	}
    }
    
    private void processMedia(String oldName) throws IOException {
	TempFileChunkManager mgr = PlaywriteRoot.getTempManager();
	String newName = mgr.fillNewEntry(this);
	_mediaNameMap.put(oldName, newName);
    }
    
    private void captureWorld(String entryName) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Util.streamCopy(this, baos);
	_worldBuf = baos.toByteArray();
	_inflateWorld = entryName.startsWith("i");
    }
    
    public void close() throws IOException {
	if (!_processingMedia)
	    super.close();
    }
}
