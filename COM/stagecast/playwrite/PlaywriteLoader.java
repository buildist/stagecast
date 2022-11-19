/* PlaywriteLoader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

class PlaywriteLoader extends ClassLoader implements Debug.Constants
{
    private Hashtable _cache = new Hashtable();
    private PluginRegistry _plugins = new PluginRegistry();
    
    PluginRegistry getPlugins() {
	return _plugins;
    }
    
    void registerAndLoadPlugins() {
	_plugins.registerPlugins();
	_plugins.loadRegisteredExtensions(this);
    }
    
    protected synchronized Class loadClass(String name, boolean resolve)
	throws ClassNotFoundException {
	Class c = (Class) _cache.get(name);
	if (c == null) {
	    try {
		c = this.findSystemClass(name);
	    } catch (ClassNotFoundException classnotfoundexception) {
		c = null;
	    }
	}
	if (c == null)
	    c = findExtensionClass(name);
	if (c == null)
	    throw new ClassNotFoundException(name);
	if (resolve)
	    this.resolveClass(c);
	return c;
    }
    
    synchronized Class loadClassFromData(String name, byte[] data) {
	Class c = this.defineClass(name, data, 0, data.length);
	_cache.put(name, c);
	this.resolveClass(c);
	return c;
    }
    
    private void checkLegalClassName(String name) {
	int lastDot = name.lastIndexOf('.');
	if (lastDot >= 0) {
	    String packageName = name.substring(0, lastDot);
	    if (packageName.startsWith("java")
		|| packageName.equals("COM.stagecast.creator")
		|| packageName.equals("COM.stagecast.operators")
		|| packageName
		       .equals("COM.stagecast.playwrite.internationalization")
		|| packageName.equals("COM.stagecast.playwrite"))
		throw new PlaywriteInternalError("Illegal class name: "
						 + name);
	}
    }
    
    public InputStream getResourceAsStream(String name) {
	InputStream is = super.getResourceAsStream(name);
	if (is != null)
	    return is;
	return (_plugins == null ? null
		: _plugins.getPluginResourceAsStream(name));
    }
    
    private Class findExtensionClass(String name)
	throws ExtensionMissingException {
	Class cl = null;
	byte[] classData = null;
	if (_plugins == null)
	    return null;
	checkLegalClassName(name);
	String resource = name.replace('.', '/') + ".class";
	try {
	    InputStream is = _plugins.getPluginResourceAsStream(resource);
	    if (is == null)
		classData = null;
	    else
		classData = Util.streamToByteArray(is);
	} catch (IOException e) {
	    Debug.stackTrace(e);
	    classData = null;
	}
	if (classData != null) {
	    try {
		cl = loadClassFromData(name, classData);
	    } catch (NoClassDefFoundError noclassdeffounderror) {
		Debug.print(true, "Missing dependency for ", name);
	    }
	} else {
	    String extName = _plugins.extensionAbsentPluginName(resource);
	    if (extName != null) {
		Debug.print(true, "Missing plugin resource: ", resource);
		throw new ExtensionMissingException
			  (extName, _plugins.extensionAbsentURL(resource));
	    }
	}
	return cl;
    }
}
