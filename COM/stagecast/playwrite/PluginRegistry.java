/* PluginRegistry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import COM.stagecast.ifc.netscape.util.Hashtable;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class PluginRegistry implements Debug.Constants, ResourceIDs.ExtensionIDs
{
    static final File PLUGIN_DIR
	= (PlaywriteRoot.isApplication()
	   ? new File(FileIO.getAppDir() + File.separator + "Plugins") : null);
    static final Object BUILT_IN = new ExtensionID("built-in");
    private static final String MANIFEST_FOLDER = "CREATOR-INF";
    private static final String PLUGIN_MANIFEST = "EXTEND.INF";
    private static final String PLUGIN_MANIFEST_PATH
	= "CREATOR-INF/EXTEND.INF";
    private static final String EXTENSION_TOK = "EXTENSION";
    private static final String VERSION_TOK = "VERSION";
    private static final String BASECLASS_TOK = "BASE_CLASS";
    private static final String REQUIRES_TOK = "REQUIRES";
    private static final String ABOUT_TOK = "ABOUT";
    private static final String IFABSENT_TOK = "IF_ABSENT";
    private static final String AUTHORING_TOK = "AUTHORING_ONLY";
    private static final String EMBED_TOK = "EMBED";
    private static final int STRING_DELIMITER = 34;
    private Hashtable _extensionsTable;
    private Hashtable _invalidTable;
    private Vector _pluginFiles;
    private Extension _first = null;
    private Extension _last = null;
    
    static class ExtensionID
    {
	String _name;
	
	ExtensionID(String name) {
	    ASSERT.isNotNull(name);
	    _name = name;
	}
	
	public String toString() {
	    return _name;
	}
    }
    
    private class Extension
    {
	String _name;
	Object _id;
	String _baseClassName;
	Class _baseClass;
	ZipFile _source;
	int _version = -1;
	Vector _contents = null;
	Vector _authoringOnly = new Vector(1);
	String _about = "";
	String _absentURL = "";
	boolean _embed = false;
	boolean _installed = false;
	Extension _next = null;
	
	Extension(String name) {
	    _name = name;
	    _id = new ExtensionID(name);
	}
	
	private void buildContentsList() {
	    _contents = new Vector(5);
	    Enumeration entries = _source.entries();
	    Debug.print("debug.plugins", "Plugin ", _source.getName(),
			" includes:");
	    while (entries.hasMoreElements()) {
		ZipEntry entry = (ZipEntry) entries.nextElement();
		String entryName = entry.getName();
		if (!entryName.startsWith("CREATOR-INF")
		    && !entryName.endsWith("/")) {
		    _contents.addElementIfAbsent(entryName);
		    Debug.print("debug.plugins", "    ", entryName);
		}
	    }
	}
	
	private boolean validateContentsList() {
	    boolean result = true;
	    COM.stagecast.ifc.netscape.util.Enumeration entries
		= _contents.elements();
	    while (entries.hasMoreElements()) {
		String name = (String) entries.nextElement();
		ZipEntry entry = _source.getEntry(name);
		if (entry == null) {
		    Debug.print("debug.plugins", "Expected plugin element ",
				name, " missing");
		    result = false;
		}
	    }
	    return result;
	}
	
	private boolean isValid() {
	    if (_name == null || _source == null || _baseClassName == null)
		return false;
	    if (_version < 1)
		return false;
	    if (_contents == null)
		buildContentsList();
	    else if (!validateContentsList())
		return false;
	    if (!_contents
		     .contains(_baseClassName.replace('.', '/') + ".class"))
		return false;
	    return true;
	}
	
	void init() {
	    try {
		invokeExtensionInit(_baseClass, _id);
	    } catch (Exception e) {
		Debug.print(true, "Failed to initialize ", _baseClass);
		Debug.stackTrace(e);
	    }
	}
	
	void drop() {
	    try {
		Method method
		    = _baseClass.getMethod("dropExtension",
					   new Class[] { Object.class });
		Debug.print("debug.plugins", "    dropping ", _baseClass);
		method.invoke(null, new Object[] { _id });
	    } catch (NoSuchMethodException nosuchmethodexception) {
		Debug.print("debug.plugins", "No drop method found on ",
			    _baseClass);
	    } catch (Throwable t) {
		Debug.print(true, "Failed to drop ", _baseClass);
		Debug.stackTrace(t);
	    }
	}
    }
    
    private class ParseException extends Exception
    {
	ParseException(String msg, StreamTokenizer st) {
	    super(msg + " near " + st.toString());
	}
    }
    
    static boolean invokeExtensionInit(Class cl, Object id) {
	boolean success = false;
	try {
	    Method method
		= cl.getMethod("initExtension", new Class[] { Object.class });
	    Debug.print("debug.plugins", "    initializing ", cl);
	    method.invoke(null, new Object[] { id });
	    success = true;
	} catch (NoSuchMethodException nosuchmethodexception) {
	    Debug.print(true, "No init method found on ", cl);
	} catch (InvocationTargetException e) {
	    Debug.print(true, "Extension failed to initialize: ", cl);
	    Debug.stackTrace(e.getTargetException());
	} catch (IllegalAccessException e) {
	    Debug.print(true, "Can't access init method: ", cl);
	    Debug.stackTrace(e);
	}
	return success;
    }
    
    PluginRegistry() {
	_extensionsTable = new Hashtable();
	_invalidTable = new Hashtable();
	_pluginFiles = new Vector();
	if (PLUGIN_DIR == null || !PLUGIN_DIR.exists())
	    Debug.print("debug.plugins", "No plugin folder: ", PLUGIN_DIR);
	else
	    populateFilesList(_pluginFiles, PLUGIN_DIR);
    }
    
    private void populateFilesList(Vector list, File dir) {
	String[] files = dir.list();
	for (int i = 0; i < files.length; i++) {
	    String file = files[i].toLowerCase();
	    if (file.endsWith(".zip") || file.endsWith(".jar")) {
		Debug.print("debug.plugins", "Found plugin file ", file);
		list.addElement(new File(dir, files[i]));
	    }
	}
	for (int i = 0; i < files.length; i++) {
	    File subdir = new File(dir, files[i]);
	    if (subdir.isDirectory())
		populateFilesList(list, subdir);
	}
    }
    
    void close() {
	Vector zips = new Vector();
	COM.stagecast.ifc.netscape.util.Enumeration extensions
	    = _extensionsTable.elements();
	while (extensions.hasMoreElements()) {
	    Extension ext = (Extension) extensions.nextElement();
	    ext.drop();
	    zips.addElementIfAbsent(ext._source);
	}
	for (int i = 0; i < zips.size(); i++) {
	    ZipFile zip = (ZipFile) zips.elementAt(i);
	    try {
		zip.close();
	    } catch (IOException ioexception) {
		/* empty */
	    }
	}
	_extensionsTable.clear();
	_pluginFiles.removeAllElements();
    }
    
    boolean isEmpty() {
	return _extensionsTable.size() < 1;
    }
    
    COM.stagecast.ifc.netscape.util.Enumeration currentPlugins() {
	return _pluginFiles.elements();
    }
    
    File findPluginFile(String name) {
	COM.stagecast.ifc.netscape.util.Enumeration files = currentPlugins();
	while (files.hasMoreElements()) {
	    File f = (File) files.nextElement();
	    if (f.getName().equals(name))
		return f;
	}
	return null;
    }
    
    boolean hasEmbedded() {
	COM.stagecast.ifc.netscape.util.Enumeration extensions
	    = _extensionsTable.elements();
	while (extensions.hasMoreElements()) {
	    Extension ext = (Extension) extensions.nextElement();
	    if (ext._embed)
		return true;
	}
	return false;
    }
    
    String aboutText() {
	StringBuffer result = new StringBuffer(512);
	String newline = "\n";
	COM.stagecast.ifc.netscape.util.Enumeration extensions
	    = _extensionsTable.elements();
	while (extensions.hasMoreElements()) {
	    Extension ext = (Extension) extensions.nextElement();
	    result.append(Resource.getTextAndFormat
			  ("EXT pv",
			   new Object[] { ext._name,
					  new Integer(ext._version) }));
	    result.append(newline);
	    result.append(ext._about == null ? "" : ext._about);
	    result.append(newline);
	    result.append(newline);
	}
	if (result.length() == 0)
	    result.append(Resource.getText("EXT pnf"));
	return result.toString();
    }
    
    void registerPlugins() {
	COM.stagecast.ifc.netscape.util.Enumeration plugins = currentPlugins();
	while (plugins.hasMoreElements()) {
	    File plugin = (File) plugins.nextElement();
	    try {
		Debug.print("debug.plugins", "Checking ", plugin);
		ZipFile zf = new ZipFile(plugin);
		if (!addExtensions(zf, true))
		    zf.close();
	    } catch (ZipException zipexception) {
		Debug.print(true, plugin, " is not in ZIP format");
	    } catch (IOException ioexception) {
		Debug.print(true, "Failed to read ", plugin);
	    }
	}
    }
    
    boolean addExtensions(ZipFile source, boolean installed) {
	if (source == null)
	    return false;
	Extension e = null;
	ZipEntry entry = source.getEntry("CREATOR-INF/EXTEND.INF");
	if (entry == null)
	    return false;
	InputStream is = null;
	try {
	    is = source.getInputStream(entry);
	    java.io.Reader r = new BufferedReader(new InputStreamReader(is));
	    StreamTokenizer st = new StreamTokenizer(r);
	    st.eolIsSignificant(true);
	    st.quoteChar(34);
	    st.wordChars(47, 47);
	    st.wordChars(36, 36);
	    st.wordChars(95, 95);
	    st.slashStarComments(true);
	    st.slashSlashComments(true);
	    e = parseExtension(st);
	    do {
		e._source = source;
		e._installed = installed;
		if (e.isValid()) {
		    Extension old
			= extensionForResource(e._baseClassName + ".class",
					       true);
		    if (old != null && old._version >= e._version)
			Debug.print("debug.plugins", "Skiping ", e._name,
				    " more recent version exists in ",
				    old._name);
		    else {
			_extensionsTable.put(e._id, e);
			Debug.print("debug.plugins", "Added ", e._name,
				    " to table");
			if (_first == null)
			    _first = e;
			else
			    _last._next = e;
			_last = e;
		    }
		} else {
		    _invalidTable.put(e._id, e);
		    Debug.print("debug.plugins", "Invalid extension: ",
				e._name);
		}
		e = parseExtension(st);
	    } while (e != null);
	} catch (IOException io) {
	    Debug.print(true, io);
	} finally {
	    if (is != null) {
		try {
		    is.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	}
	return true;
    }
    
    private Extension extensionForName(String name) {
	COM.stagecast.ifc.netscape.util.Enumeration extensions
	    = _extensionsTable.elements();
	while (extensions.hasMoreElements()) {
	    Extension ext = (Extension) extensions.nextElement();
	    if (ext._name.equals(name))
		return ext;
	}
	return null;
    }
    
    String baseClassForExtension(String name) {
	return extensionForName(name)._baseClassName;
    }
    
    ZipFile zipFileForExtension(String name) {
	return extensionForName(name)._source;
    }
    
    private Extension extensionForResource(String resName, boolean valid) {
	COM.stagecast.ifc.netscape.util.Enumeration extensions
	    = (valid ? _extensionsTable : _invalidTable).elements();
	while (extensions.hasMoreElements()) {
	    Extension ext = (Extension) extensions.nextElement();
	    if (ext._contents.contains(resName)) {
		Debug.print("debug.plugins", valid ? "" : "Invalid ", resName,
			    " found in ", ext._source.getName());
		return ext;
	    }
	}
	return null;
    }
    
    ZipFile resourceSource(String resName) {
	Extension ext = extensionForResource(resName, true);
	return ext == null ? null : ext._source;
    }
    
    boolean isEmbedded(String resName) {
	Extension ext = extensionForResource(resName, true);
	return ext == null ? false : ext._embed;
    }
    
    String pluginName(String resName) {
	Extension ext = extensionForResource(resName, true);
	return ext == null ? "" : ext._name;
    }
    
    String extensionAbsentPluginName(String resName) {
	Extension ext = extensionForResource(resName, false);
	return ext == null ? null : ext._name;
    }
    
    String extensionAbsentURL(String resName) {
	Extension ext = extensionForResource(resName, false);
	return ext == null ? null : ext._absentURL;
    }
    
    void loadRegisteredExtensions(PlaywriteLoader loader) {
	StringBuffer failedToLoad = new StringBuffer(20);
	Extension ext = null;
	for (ext = _first; ext != null; ext = ext._next) {
	    try {
		ext._baseClass = loader.loadClass(ext._baseClassName);
		ext.init();
	    } catch (Throwable t) {
		Debug.stackTrace(t);
		if (failedToLoad.length() > 0)
		    failedToLoad.append(", ");
		failedToLoad.append(ext._name);
	    }
	}
	if (failedToLoad.length() > 0)
	    throw new RecoverableException
		      ("The following plugin(s) failed to load: {0}",
		       new Object[] { failedToLoad.toString() });
    }
    
    InputStream getPluginResourceAsStream(String name) {
	InputStream is = null;
	ZipFile zf = resourceSource(name);
	Debug.print("debug.plugins", "Search for resource ", name,
		    zf == null ? " failed" : " is OK");
	if (zf == null)
	    return null;
	try {
	    ZipEntry ze = zf.getEntry(name);
	    if (ze != null)
		is = zf.getInputStream(ze);
	} catch (ZipException e) {
	    Debug.stackTrace(e);
	    is = null;
	} catch (IOException e) {
	    Debug.stackTrace(e);
	    is = null;
	}
	return is;
    }
    
    void copyToRegistry(String resName, PluginRegistry regCopy) {
	Extension ext = extensionForResource(resName, true);
	regCopy._extensionsTable.put(ext._id, ext);
    }
    
    void writeAsZip(ZipOutputStream zip) throws IOException {
	if (!isEmpty()) {
	    Debug.print("debug.plugins", "Writing extension manifest");
	    ZipEntry entry = new ZipEntry("CREATOR-INF/EXTEND.INF");
	    entry.setMethod(8);
	    zip.putNextEntry(entry);
	    writeAsGrammar(zip);
	    zip.closeEntry();
	    COM.stagecast.ifc.netscape.util.Enumeration extensions
		= _extensionsTable.elements();
	    while (extensions.hasMoreElements()) {
		Extension ext = (Extension) extensions.nextElement();
		if (ext._embed) {
		    for (int i = 0; i < ext._contents.size(); i++) {
			String resource = (String) ext._contents.elementAt(i);
			if (!ext._authoringOnly.contains(resource))
			    writeResource(zip, resource);
		    }
		}
	    }
	}
    }
    
    private void writeResource(ZipOutputStream zip, String res)
	throws IOException {
	Debug.print("debug.plugins", "Writing plugin data ", res);
	ZipEntry entry = new ZipEntry(res);
	entry.setMethod(8);
	zip.putNextEntry(entry);
	int count = 0;
	byte[] buffer = new byte[16384];
	InputStream is = getPluginResourceAsStream(res);
	while (count >= 0) {
	    count = is.read(buffer);
	    if (count > 0)
		zip.write(buffer, 0, count);
	}
	is.close();
	zip.closeEntry();
    }
    
    void writeAsGrammar(OutputStream os) {
	PrintWriter pw = new PrintWriter(os);
	COM.stagecast.ifc.netscape.util.Enumeration extensions
	    = _extensionsTable.elements();
	while (extensions.hasMoreElements())
	    printExtension(pw, (Extension) extensions.nextElement());
	extensions = _invalidTable.elements();
	while (extensions.hasMoreElements())
	    printExtension(pw, (Extension) extensions.nextElement());
    }
    
    private void printExtension(PrintWriter pw, Extension ext) {
	printItems(pw, "EXTENSION", ext._name);
	printItems(pw, "VERSION", Integer.toString(ext._version));
	printItems(pw, "BASE_CLASS", ext._baseClassName);
	if (ext._about != null)
	    printItemsQ(pw, "ABOUT", ext._about);
	if (ext._absentURL != null)
	    printItemsQ(pw, "IF_ABSENT", ext._absentURL);
	if (ext._contents.size() > 0) {
	    pw.print("REQUIRES");
	    printItemList(pw, ext._contents);
	}
	if (ext._authoringOnly.size() > 0) {
	    pw.print("AUTHORING_ONLY");
	    printItemList(pw, ext._authoringOnly);
	}
	if (ext._embed)
	    pw.println("EMBED");
	pw.println();
	pw.flush();
    }
    
    private void printItems(PrintWriter pw, String item1, String item2) {
	pw.print(item1);
	pw.print("  ");
	pw.println(item2);
    }
    
    private void printItemsQ(PrintWriter pw, String item1, String item2) {
	pw.print(item1);
	pw.print("  \"");
	pw.print(item2);
	pw.println("\"");
    }
    
    private void printItemList(PrintWriter pw, Vector list) {
	int max = list.size() - 1;
	for (int i = 0; i <= max; i++) {
	    pw.print("  ");
	    pw.print((String) list.elementAt(i));
	    pw.println(i == max ? "" : ",");
	}
    }
    
    private Extension parseExtension(StreamTokenizer st) {
	Extension e = null;
	try {
	    do
		st.nextToken();
	    while (st.ttype == 10);
	    if (st.ttype == -1)
		return null;
	    st.pushBack();
	    e = parseDescrip(st);
	    while (parseAttributes(st, e))
		requireEOL(st);
	} catch (IOException io) {
	    Debug.print(true, io);
	    e = null;
	} catch (ParseException pe) {
	    Debug.print(true, pe);
	    e = null;
	}
	return e;
    }
    
    private Extension parseDescrip(StreamTokenizer st)
	throws IOException, ParseException {
	requireWord(st, "EXTENSION");
	Extension e = new Extension(requireWord(st, null));
	requireEOL(st);
	Debug.print("debug.plugins", "Extension->", e._name);
	return e;
    }
    
    private boolean parseAttributes(StreamTokenizer st, Extension e)
	throws IOException, ParseException {
	if (parseVersion(st, e) || parseRequires(st, e) || parseAbout(st, e)
	    || parseAbsent(st, e) || parseBase(st, e) || parseEmbed(st, e)
	    || parseAuthoringOnly(st, e))
	    return true;
	return false;
    }
    
    private boolean parseVersion(StreamTokenizer st, Extension e)
	throws IOException, ParseException {
	if (acceptsWord(st, "VERSION")) {
	    e._version = requireInt(st);
	    Debug.print("debug.plugins", "  version->" + e._version);
	    return true;
	}
	return false;
    }
    
    private boolean parseAbout(StreamTokenizer st, Extension e)
	throws IOException, ParseException {
	if (acceptsWord(st, "ABOUT")) {
	    e._about = requireString(st);
	    Debug.print("debug.plugins", "  about->" + e._about);
	    return true;
	}
	return false;
    }
    
    private boolean parseBase(StreamTokenizer st, Extension e)
	throws IOException, ParseException {
	if (acceptsWord(st, "BASE_CLASS")) {
	    e._baseClassName = requireWord(st, null);
	    Debug.print("debug.plugins", "  base_class->" + e._baseClassName);
	    return true;
	}
	return false;
    }
    
    private boolean parseAbsent(StreamTokenizer st, Extension e)
	throws IOException, ParseException {
	if (acceptsWord(st, "IF_ABSENT")) {
	    e._absentURL = requireString(st);
	    Debug.print("debug.plugins", "  if_absent->" + e._absentURL);
	    return true;
	}
	return false;
    }
    
    private boolean parseRequires(StreamTokenizer st, Extension e)
	throws IOException, ParseException {
	if (acceptsWord(st, "REQUIRES")) {
	    Debug.print("debug.plugins", "  requires[");
	    e._contents = parseResourceList(st);
	    Debug.print("debug.plugins", "  ]");
	    return true;
	}
	return false;
    }
    
    private Vector parseResourceList(StreamTokenizer st)
	throws IOException, ParseException {
	Vector v = new Vector(5);
	boolean parsing = true;
	while (parsing) {
	    String resource = requireWord(st, null);
	    Debug.print("debug.plugins", "    ", resource);
	    v.addElement(resource);
	    st.nextToken();
	    if (st.ttype != 44)
		parsing = false;
	    else {
		st.nextToken();
		if (st.ttype != 10)
		    st.pushBack();
	    }
	}
	st.pushBack();
	return v;
    }
    
    private boolean parseAuthoringOnly(StreamTokenizer st, Extension e)
	throws IOException, ParseException {
	if (acceptsWord(st, "AUTHORING_ONLY")) {
	    Debug.print("debug.plugins", "  authoring_only [");
	    e._authoringOnly = parseResourceList(st);
	    Debug.print("debug.plugins", "  ]");
	    return true;
	}
	return false;
    }
    
    private boolean parseEmbed(StreamTokenizer st, Extension e)
	throws IOException, ParseException {
	if (acceptsWord(st, "EMBED")) {
	    Debug.print("debug.plugins", "  embed");
	    e._embed = true;
	    return true;
	}
	return false;
    }
    
    private void requireEOL(StreamTokenizer st)
	throws IOException, ParseException {
	st.nextToken();
	if (st.ttype != 10 && st.ttype != -1)
	    throw new ParseException("EOL expected", st);
    }
    
    private int requireInt(StreamTokenizer st)
	throws IOException, ParseException {
	st.nextToken();
	if (st.ttype != -2)
	    throw new ParseException("integer expected", st);
	return new Double(st.nval).intValue();
    }
    
    private String requireWord(StreamTokenizer st, String id)
	throws IOException, ParseException {
	st.nextToken();
	if (st.ttype == -3 && (id == null || id.equals(st.sval.toUpperCase())))
	    return st.sval;
	throw new ParseException((id == null ? "token" : id) + " expected",
				 st);
    }
    
    private boolean acceptsWord(StreamTokenizer st, String id)
	throws IOException, ParseException {
	st.nextToken();
	boolean match = st.ttype == -3 && id.equals(st.sval.toUpperCase());
	if (!match)
	    st.pushBack();
	return match;
    }
    
    private String requireString(StreamTokenizer st)
	throws IOException, ParseException {
	st.nextToken();
	if (st.ttype != 34)
	    throw new ParseException("String expected", st);
	return st.sval;
    }
}
