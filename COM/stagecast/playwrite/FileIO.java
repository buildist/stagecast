/* FileIO - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import COM.stagecast.ifc.netscape.application.FileChooser;
import COM.stagecast.ifc.netscape.util.Sort;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class FileIO implements Debug.Constants, PlaywriteSystem.Properties,
			       ResourceIDs.DialogIDs, ResourceIDs.CommandIDs
{
    public static final int CREATED_BY_UNKNOWN = -1;
    public static final int CREATED_BY_CREATOR = 0;
    public static final int CREATED_BY_BROWSER = 1;
    public static final int CREATED_BY_WORD_PROCESSOR = 2;
    public static final int CREATED_BY_SPREADSHEET = 3;
    public static final int CREATED_BY_IMAGE_EDITOR = 4;
    public static final int CREATED_BY_MOVIE_EDITOR = 5;
    public static final int CREATED_BY_JAVA = 6;
    public static final int TEXT_FILE_TYPE = 0;
    public static final int WORLD_FILE_TYPE = 1;
    public static final int HTML_FILE_TYPE = 2;
    public static final int PREF_FILE_TYPE = 3;
    public static final int GIF_FILE_TYPE = 4;
    public static final int JPEG_FILE_TYPE = 5;
    public static final int QT_FILE_TYPE = 6;
    public static final int MPEG_FILE_TYPE = 7;
    public static final int JAR_FILE_TYPE = 8;
    private static PlatformFileHandler platformHandler = null;
    private static String tempDir = null;
    private static String appDir = null;
    private static String userDir = null;
    
    public static interface FileIterator
    {
	public void handleFile(String string);
    }
    
    public static interface PlatformFileHandler
    {
	public void setFileCreator(File file, int i);
	
	public void setFileType(File file, int i);
	
	public void setDefaultFileCreator(int i);
	
	public void setDefaultFileType(int i);
    }
    
    public static interface SafeSaver
    {
	public boolean writeData(FileOutputStream fileoutputstream)
	    throws IOException;
    }
    
    static void initialize() {
	String className = (PlaywriteSystem.isMacintosh()
			    ? "COM.stagecast.creator.MacFileIO" : "");
	if (className.length() > 0
	    && !className.toUpperCase().equals("NONE")) {
	    try {
		Class ioClass = Class.forName(className);
		platformHandler = (PlatformFileHandler) ioClass.newInstance();
	    } catch (Throwable t) {
		Debug.stackTrace(t);
	    }
	}
	setDefaultFileCreator(0);
	setDefaultFileType(1);
	tempDir = PlaywriteSystem.getSystemProperty("java.io.tmpdir");
	if (tempDir == null || !Util.isPathWritable(tempDir))
	    tempDir = PlaywriteSystem.getSystemProperty("user.home");
	if (tempDir == null || !Util.isPathWritable(tempDir))
	    Debug.print(true, "Can't find reasonable temp file directory");
	boolean multi
	    = PlaywriteSystem.getApplicationPropertyAsBoolean("multi_user",
							      false);
	appDir = PlaywriteSystem.getSystemProperty("user.dir");
	appDir = PlaywriteSystem.getApplicationProperty("app_dir", appDir);
	userDir
	    = multi ? PlaywriteSystem.getSystemProperty("user.home") : appDir;
	userDir = PlaywriteSystem.getApplicationProperty("user_dir", userDir);
	Debug.print("debug.world", "tempDir: " + tempDir);
	Debug.print("debug.world", "appDir: " + appDir);
	Debug.print("debug.world", "userDir: " + userDir);
    }
    
    public static void setFileCreator(File file, int createdBy) {
	if (platformHandler != null)
	    platformHandler.setFileCreator(file, createdBy);
    }
    
    public static void setFileType(File file, int fileType) {
	if (platformHandler != null)
	    platformHandler.setFileType(file, fileType);
    }
    
    public static void setDefaultFileCreator(int createdBy) {
	if (platformHandler != null)
	    platformHandler.setDefaultFileCreator(createdBy);
    }
    
    public static void setDefaultFileType(int fileType) {
	if (platformHandler != null)
	    platformHandler.setDefaultFileType(fileType);
    }
    
    public static final String getAppDir() {
	return appDir;
    }
    
    public static final String getTempDir() {
	return tempDir;
    }
    
    public static final String getUserDir() {
	return userDir;
    }
    
    public static File load(ResourceBundle bundle$, String promptID$) {
	File file = null;
	FileChooser chooser
	    = new FileChooser(PlaywriteRoot.getMainRootView(),
			      Resource.getText(bundle$, promptID$), 0);
	if (World.getWorldDirectory() != null)
	    chooser.setDirectory(World.getWorldDirectory());
	chooser.showModally();
	if (chooser.file() != null) {
	    World.setWorldDirectory(chooser.directory());
	    file = new File(chooser.directory(), chooser.file());
	}
	return file;
    }
    
    private static String appendExtension(String fileName, String extension) {
	if (fileName.indexOf(".") == -1 && extension != null
	    && !extension.equals("") && !fileName.endsWith(extension))
	    fileName += (String) extension;
	return fileName;
    }
    
    public static File save(boolean doDialog$, SafeSaver safeSaver$,
			    ResourceBundle bundle$, String promptID$,
			    String fileName$, String fileNameExtensionID$) {
	String extension = "";
	if (fileNameExtensionID$ != null)
	    extension = Resource.getText(bundle$, fileNameExtensionID$);
	File newFile = null;
	File tempFile = null;
	FileOutputStream tempOs = null;
	boolean saved = false;
	doDialog$ = fileName$ == null || doDialog$;
	do {
	    if (doDialog$) {
		String message = Resource.getText(bundle$, promptID$);
		FileChooser fc
		    = new FileChooser(PlaywriteRoot.getMainRootView(), message,
				      1);
		fc.setDirectory(World.getWorldDirectory());
		if (extension != "" && !fileName$.endsWith(extension)) {
		    fileName$ = appendExtension(fileName$, extension);
		    fc.setFile(fileName$);
		}
		fc.showModally();
		if (fc.file() == null)
		    return null;
		World.setWorldDirectory(fc.directory());
		String fName = fc.file();
		if (PlaywriteSystem.isWindows())
		    fName = appendExtension(fName, extension);
		newFile = new File(fc.directory(), fName);
	    } else {
		if (PlaywriteSystem.isWindows())
		    fileName$ = appendExtension(fileName$, extension);
		newFile = new File(World.getWorldDirectory(), fileName$);
	    }
	    doDialog$ = false;
	    if (newFile.exists() && !newFile.canWrite())
		doDialog$ = true;
	    else {
		tempFile = Util.createTempFile(newFile.getParent());
		try {
		    tempOs = new FileOutputStream(tempFile);
		} catch (IOException ioexception) {
		    doDialog$ = true;
		}
	    }
	    if (doDialog$) {
		PlaywriteDialog msg
		    = new PlaywriteDialog("dialog cstr", "command ok");
		msg.getAnswer();
	    }
	} while (doDialog$);
	try {
	    saved = safeSaver$.writeData(tempOs);
	} catch (IOException ioexception) {
	    saved = false;
	} finally {
	    try {
		tempOs.close();
	    } catch (IOException ioexception) {
		saved = false;
	    }
	}
	tempOs = null;
	if (saved) {
	    if (newFile.exists())
		newFile.delete();
	    tempFile.renameTo(newFile);
	    return newFile;
	}
	tempFile.delete();
	PlaywriteDialog msg = new PlaywriteDialog("dialog sf", "command ok");
	msg.getAnswer();
	return null;
    }
    
    public static String removeNameExtension(File file, ResourceBundle bundle$,
					     String fileNameExtensionID$) {
	String extension = Resource.getText(bundle$, fileNameExtensionID$);
	String fileName = file.getName();
	if (extension != null && extension != ""
	    && fileName.endsWith(extension))
	    fileName = fileName.substring(0, fileName.lastIndexOf(extension));
	return fileName;
    }
    
    public static void setTypeAndCreator(File htmlFile, int type,
					 int creator) {
	setFileCreator(htmlFile, creator);
	setFileType(htmlFile, type);
    }
    
    public static String checkDirectory(String targetRes) {
	String pattern = Resource.getText(targetRes);
	String pathSuffix = new MessageFormat(pattern)
				.format(new Object[] { File.separator });
	File testDir = new File(getUserDir(), pathSuffix);
	boolean result = false;
	try {
	    result = testDir.exists() && testDir.isDirectory();
	} catch (SecurityException securityexception) {
	    Debug.print(true, "Access not allowed: " + testDir);
	}
	return result ? testDir.getAbsolutePath() : getUserDir();
    }
    
    public static boolean iterateOverDirectory
	(String directory, FileIterator handler, boolean recursive,
	 FilenameFilter filter) {
	boolean result = true;
	File directoryFile = new File(directory);
	if (directoryFile.exists()) {
	    ASSERT.isTrue(directoryFile.isAbsolute());
	    String[] fileNameList = (filter == null ? directoryFile.list()
				     : directoryFile.list(filter));
	    Sort.sortStrings(fileNameList, 0, fileNameList.length, true, true);
	    for (int i = 0; i < fileNameList.length; i++) {
		File file = new File(directory + fileNameList[i]);
		if (file.isFile())
		    handler.handleFile(directory + fileNameList[i]);
		else if (file.isDirectory() && recursive)
		    iterateOverDirectory(directory + fileNameList[i], handler,
					 recursive, filter);
	    }
	} else
	    result = false;
	return result;
    }
    
    public static String getRelativeFilePath(String path1, String path2) {
	String fileSeparator = "/";
	path1 = path1.replace(File.separatorChar, '/');
	path2 = path2.replace(File.separatorChar, '/');
	String s1
	    = path1.substring(0, path1.lastIndexOf(fileSeparator.charAt(0)));
	String s2
	    = path2.substring(0, path2.lastIndexOf(fileSeparator.charAt(0)));
	StringTokenizer tokenizer1 = new StringTokenizer(s1, fileSeparator);
	StringTokenizer tokenizer2 = new StringTokenizer(s2, fileSeparator);
	String result = "";
	boolean b1 = false;
	boolean b2 = false;
	String firstDir = null;
	do {
	    if (!(b1 = tokenizer2.hasMoreTokens())
		|| !(b2 = tokenizer1.hasMoreTokens()))
		break;
	} while ((firstDir = tokenizer2.nextToken())
		     .equals(tokenizer1.nextToken()));
	while (tokenizer1.hasMoreTokens()) {
	    tokenizer1.nextToken();
	    result += ".." + (String) fileSeparator;
	}
	if (b1 && b2)
	    result += (".." + (String) fileSeparator + (String) firstDir
		       + (String) fileSeparator);
	while (tokenizer2.hasMoreTokens())
	    result += tokenizer2.nextToken() + (String) fileSeparator;
	result
	    += path2.substring(path2.lastIndexOf(fileSeparator.charAt(0)) + 1);
	return result;
    }
    
    public static String getAbsoluteFilePath(String path,
					     String relativePath) {
	String result = relativePath;
	try {
	    if (path != null) {
		URL u1 = new URL("file://" + path.replace(File.separatorChar,
							  '/'));
		URL u2 = new URL(u1, relativePath.replace(File.separatorChar,
							  '/'));
		result = u2.getFile();
	    }
	} catch (java.net.MalformedURLException malformedurlexception) {
	    /* empty */
	}
	return result;
    }
}
