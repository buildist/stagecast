/* WorldUtility - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.StringTokenizer;

import COM.stagecast.ifc.netscape.application.AWTCompatibility;
import COM.stagecast.ifc.netscape.application.BezelBorder;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.FileChooser;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.MenuItem;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.ifc.netscape.application.Size;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.jpeg.JpegEncoder;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class WorldUtility
    implements Debug.Constants, ResourceIDs.AboutWindowIDs,
	       ResourceIDs.ColorIDs, ResourceIDs.CommandIDs,
	       ResourceIDs.DialogIDs, ResourceIDs.FileTransferIDs,
	       ResourceIDs.TemplateIDs, ResourceIDs.WorldVariableIDs,
	       ResourceIDs.WorldIDs, ResourceIDs.WorldViewIDs, Target
{
    private static final String THIS_SESSION = "*&session&*";
    private static final String APPLET_FILE = "StagecastApplet.jar";
    private static final String TEMPLATE_FILE = "SaveForInternet.html";
    private static final String APPLET_RESOURCE
	= "/COM/stagecast/creator/templates/StagecastApplet.jar.dat";
    private static final String TEMPLATE_RESOURCE
	= "/COM/stagecast/creator/templates/SaveForInternet.html";
    public static final String NAME_PROP = "Name";
    public static final String HOST_PROP = "Host";
    public static final String USER_PROP = "User";
    public static final String USERDIR_PROP = "UserDir";
    public static final String PORT_PROP = "Port";
    public static final String PASSWORD_PROP = "Password";
    public static final String TEMPLATE_PROP = "Template";
    public static final String TEMPLATE_URL_PROP = "TemplateURL";
    public static final String HELP_URL_PROP = "HelpURL";
    public static final String SERVER_URL_PROP = "ServerURL";
    public static final String SERVER_PORT_PROP = "ServerPort";
    public static final String APPLET_PROP = "Applet";
    public static final String APPLET_WINDOWED_PROP = "AppletWindowed";
    public static final String APPLET_PARAMS_PROP = "AppletParams";
    public static final String APPLET_TAGS_PROP = "AppletTags";
    public static final String AUTOINSTALL_PROP = "AutoInstall";
    public static final String HTML_FILE_PROP = "HTMLFile";
    public static final String INCLUDE_APPLET_JAR_PROP = "IncludeAppletJar";
    public static final String INCLUDE_PLUGINS_PROP = "IncludePlugins";
    public static final String PLUGINS_PROP = "Plugins";
    public static final String TEMPLATE_DATA_PROP = "TemplateData";
    public static final String WORLD_FILE_PROP = "WorldFile";
    private World _world;
    private String _errorMsg;
    private String _errorTarget;
    
    public static interface FilesetManager
    {
	public void filesetBegin(World world, Properties properties);
	
	public boolean determineDestination();
	
	public boolean storeWorld() throws IOException;
	
	public boolean storeHtml(InputStream inputstream) throws IOException;
	
	public boolean storeJar(String string, InputStream inputstream)
	    throws IOException;
	
	public void filesetEnd();
    }
    
    private static class DiskWriter implements FilesetManager
    {
	private World _world;
	private Properties _props;
	private String _destDir;
	
	public DiskWriter() {
	    /* empty */
	}
	
	public void filesetBegin(World w, Properties p) {
	    _world = w;
	    _props = p;
	}
	
	public boolean determineDestination() {
	    String message = Resource.getText("command swp");
	    FileChooser fc
		= new FileChooser(PlaywriteRoot.getMainRootView(), message, 1);
	    File sourceFile = _world.getSourceFile();
	    if (sourceFile != null)
		fc.setDirectory(sourceFile.getParent());
	    fc.setFile(_world.getName() + ".html");
	    fc.showModally();
	    if (fc.file() == null)
		return false;
	    _destDir = fc.directory();
	    _props.put("HTMLFile", fc.file());
	    String worldFile
		= Util.getFilePart(fc.file()) + Resource.getText("W ex");
	    _props.put("WorldFile", worldFile);
	    return true;
	}
	
	public boolean storeWorld() throws IOException {
	    File target = new File(_destDir, _props.getProperty("WorldFile"));
	    if (new File(target.getAbsolutePath())
		    .equals(_world.getSourceFile())) {
		PlaywriteDialog.warning(Resource.getText("dialog coo"), true);
		return false;
	    }
	    File temp = Util.createTempFile(_destDir);
	    java.io.OutputStream os = new FileOutputStream(temp);
	    _world.saveObjects(os, false);
	    os.close();
	    temp.renameTo(target);
	    FileIO.setTypeAndCreator(target, 1, 0);
	    return true;
	}
	
	public boolean storeHtml(InputStream is) throws IOException {
	    writeDisk(_props.getProperty("HTMLFile"), is, 2, 1);
	    return true;
	}
	
	public boolean storeJar(String name, InputStream is)
	    throws IOException {
	    writeDisk(name, is, 8, 6);
	    return true;
	}
	
	private void writeDisk(String name, InputStream is, int type,
			       int creator) throws IOException {
	    File target = new File(_destDir, name);
	    java.io.OutputStream os = new FileOutputStream(target);
	    Util.streamCopy(is, os);
	    os.close();
	    FileIO.setTypeAndCreator(target, type, creator);
	}
	
	public void filesetEnd() {
	    /* empty */
	}
    }
    
    private static class FTPWriter implements FilesetManager
    {
	private World _world;
	private Properties _props;
	private FTPSession _ftp;
	private String _helpMessage = "";
	
	public FTPWriter() {
	    /* empty */
	}
	
	public void filesetBegin(World w, Properties p) {
	    _world = w;
	    _props = p;
	}
	
	private void ftpErrorDlg(FTPSession.FTPException fe) {
	    Debug.print(true, fe);
	    String msg = fe.getLocalizedMessage();
	    PlaywriteRoot.clearBusy();
	    PlaywriteDialog.warning(msg + _helpMessage, true);
	    PlaywriteRoot.markBusy();
	}
	
	public boolean determineDestination() {
	    String name = _props.getProperty("Name");
	    String host = _props.getProperty("Host");
	    int port = Integer.parseInt(_props.getProperty("Port", "21"));
	    String user = _props.getProperty("User");
	    String userDir = _props.getProperty("UserDir");
	    String helpURL = _props.getProperty("HelpURL");
	    _helpMessage
		= (helpURL == null ? ""
		   : Resource.getTextAndFormat("FTP hm",
					       new Object[] { helpURL }));
	    String worldExtension = Resource.getText("W ex");
	    String worldFile = _world.getName() + worldExtension;
	    _props.put("WorldFile", worldFile);
	    String htmlFile = _world.getName() + ".html";
	    _props.put("HTMLFile", htmlFile);
	    PlaywriteDialog dlg
		= new PlaywriteDialog("dialog web conn", "command ok",
				      "command c");
	    if ("command c".equals(dlg.getAnswer()))
		return false;
	    String hostName
		= host == null ? Resource.getText("FTP duh") : host;
	    FormDialog fd
		= showLoginDialog(Resource.getTextAndFormat("FTP dt",
							    (new Object[]
							     { hostName })),
				  _props);
	    if (fd == null)
		return false;
	    if (host == null)
		host = fd.getResult("FTP hf");
	    if (user == null)
		user = fd.getResult("FTP uf");
	    if (userDir == null)
		userDir = fd.getResult("FTP udf");
	    String pass = fd.getResult("FTP pf");
	    PlaywriteRoot.markBusy();
	    try {
		_ftp = new FTPSession(host, port);
		_ftp.connect();
		_ftp.login(user, pass);
		if (userDir != null)
		    _ftp.setDirectory(userDir);
	    } catch (FTPSession.FTPException fe) {
		ftpErrorDlg(fe);
		return false;
	    } finally {
		PlaywriteRoot.clearBusy();
	    }
	    return true;
	}
	
	public boolean storeWorld() throws IOException {
	    try {
		String worldFile = _props.getProperty("WorldFile");
		String title
		    = Resource.getTextAndFormat("FTP pt",
						new Object[] { worldFile });
		_ftp.setProgress(PlaywriteRoot.getProgressDialog());
		_ftp.transmit(_world);
	    } catch (FTPSession.FTPException fe) {
		ftpErrorDlg(fe);
		return false;
	    }
	    return true;
	}
	
	public boolean storeHtml(InputStream is) throws IOException {
	    try {
		String htmlFile = _props.getProperty("HTMLFile");
		String title
		    = Resource.getTextAndFormat("FTP pt",
						new Object[] { htmlFile });
		ProgressDialog pd = PlaywriteRoot.getProgressDialog();
		_ftp.setProgressIncr(pd == null ? 0 : pd.getTotalCount() / 10);
		_ftp.transmit(is, false, htmlFile);
	    } catch (FTPSession.FTPException fe) {
		ftpErrorDlg(fe);
		return false;
	    }
	    return true;
	}
	
	public boolean storeJar(String name, InputStream is)
	    throws IOException {
	    boolean ok = false;
	    try {
		String title
		    = Resource.getTextAndFormat("FTP pt",
						new Object[] { name });
		ProgressDialog pd = PlaywriteRoot.getProgressDialog();
		_ftp.setProgressIncr(pd == null ? 0 : pd.getTotalCount() / 10);
		_ftp.transmit(is, true, name);
		ok = true;
	    } catch (FTPSession.FTPException fe) {
		ftpErrorDlg(fe);
	    }
	    return ok;
	}
	
	public void filesetEnd() {
	    if (_ftp != null)
		_ftp.disconnect();
	}
    }
    
    static FormDialog showLoginDialog(String title, Properties props) {
	FormDialog fd = new FormDialog(title, Util.buttonFont,
				       new Font("San Serif", 0, 10), 280);
	if (props.getProperty("Host") == null)
	    fd.addField("FTP hf");
	if (props.getProperty("User") == null)
	    fd.addField("FTP uf");
	fd.addPasswordField("FTP pf");
	if (props.getProperty("UserDir") == null)
	    fd.addField("FTP udf");
	if (!fd.display())
	    fd = null;
	return fd;
    }
    
    void makeSnapshot() {
	WorldView worldView = null;
	SplitView multiStageView = null;
	File tempFile = null;
	FileOutputStream tempOs = null;
	JpegEncoder jpg = null;
	worldView = _world.getWorldView();
	if (worldView != null)
	    multiStageView = worldView.getMultiStageView();
	if (multiStageView != null) {
	    Bitmap bmap = null;
	    Graphics g = null;
	    int width = multiStageView.bounds.width;
	    int height = multiStageView.bounds.height;
	    Rect clipRect = new Rect(0, 0, width - 8, height - 8);
	    bmap = new Bitmap(width - 16, height - 16);
	    g = bmap.createGraphics();
	    g.translate(-8, -8);
	    multiStageView.draw(g, clipRect);
	    g.translate(8, 8);
	    g.dispose();
	    Image image = null;
	    image = AWTCompatibility.awtImageForBitmap(bmap);
	    int quality = 80;
	    int maxDim = 108;
	    boolean thumbNail = false;
	    PlaywriteDialog msg
		= new PlaywriteDialog("dialog choose image size",
				      "command full size", "command thumbnail",
				      "command c");
	    String answer = msg.getAnswer();
	    if (answer != "command c") {
		if (answer == "command thumbnail") {
		    PlaywriteRoot.markBusy();
		    thumbNail = true;
		    double scale = (double) maxDim / (double) height;
		    if (width > height)
			scale = (double) maxDim / (double) width;
		    if (scale < 1.0) {
			int scaledW = (int) (scale * (double) width);
			int scaledH = (int) (scale * (double) height);
			Bitmap scaledBmap = null;
			scaledBmap = new Bitmap(scaledW, scaledH);
			g = scaledBmap.createGraphics();
			g.drawBitmapScaled(bmap, 0, 0, scaledW, scaledH);
			g.dispose();
			image = AWTCompatibility.awtImageForBitmap(scaledBmap);
		    }
		    PlaywriteRoot.clearBusy();
		}
		String worldExtension = Resource.getText("W ex");
		String jpgExtension = Resource.getText(".jpg");
		boolean ok_to_save = true;
		File sourceFile = _world.getSourceFile();
		FileChooser fc
		    = new FileChooser(PlaywriteRoot.getMainRootView(),
				      Resource.getText("make snapshot"), 1);
		if (sourceFile != null && !Tutorial.isTutorialRunning()) {
		    fc.setDirectory(sourceFile.getParent());
		    fc.setFile(sourceFile.getName());
		}
		if (sourceFile == null) {
		    fc.setDirectory(World.getWorldDirectory());
		    fc.setFile(_world.getName());
		}
		String fName = fc.file();
		if (fName.endsWith(worldExtension))
		    fName = fName.substring(0, (fName.length()
						- worldExtension.length()));
		fc.setFile((!thumbNail ? fName : fName + "_Thumb")
			   + jpgExtension);
		fc.showModally();
		if (fc.file() != null) {
		    fName = fc.file();
		    if (PlaywriteSystem.isWindows()
			&& !fName.endsWith(jpgExtension))
			fName += (String) jpgExtension;
		    tempFile = new File(fc.directory(), fName);
		    try {
			tempOs = new FileOutputStream(tempFile);
			FileIO.setFileType(tempFile, 5);
			FileIO.setFileCreator(tempFile, 4);
		    } catch (IOException ioexception) {
			ok_to_save = false;
			msg = new PlaywriteDialog("dialog cstr", "command ok");
			msg.getAnswer();
		    }
		    boolean jpegSuccess = true;
		    if (ok_to_save) {
			PlaywriteRoot.markBusy();
			try {
			    jpg = new JpegEncoder(image, quality, tempOs);
			} catch (Exception exception) {
			    jpegSuccess = false;
			}
			try {
			    jpg.Compress();
			} catch (Exception exception) {
			    jpegSuccess = false;
			} finally {
			    try {
				tempOs.close();
			    } catch (IOException ioexception) {
				jpegSuccess = false;
			    }
			}
		    } else
			tempFile.delete();
		    tempOs = null;
		    PlaywriteRoot.clearBusy();
		    if (!jpegSuccess) {
			msg = (new PlaywriteDialog
			       ("dialog attempt to make snapshot has failed",
				"command ok"));
			msg.getAnswer();
			tempFile.delete();
		    }
		}
	    }
	}
    }
    
    void saveSummary() {
	File sourceFile = _world.getSourceFile();
	FileChooser fc = new FileChooser(PlaywriteRoot.getMainRootView(),
					 Resource.getText("W wsr"), 1);
	if (sourceFile != null && !Tutorial.isTutorialRunning())
	    fc.setDirectory(sourceFile.getParent());
	fc.setFile(_world.getName() + " report.html");
	fc.showModally();
	if (fc.file() != null) {
	    File summaryFile = new File(fc.directory(), fc.file());
	    PlaywriteRoot.markBusy();
	    try {
		FileOutputStream fos = new FileOutputStream(summaryFile);
		FileIO.setTypeAndCreator(summaryFile, 2, 1);
		Summarizer.summarize(_world, fos);
		fos.close();
	    } catch (Exception exception) {
		PlaywriteDialog msg
		    = new PlaywriteDialog("dialog se", "command ok");
		msg.getAnswer();
	    }
	    PlaywriteRoot.clearBusy();
	}
    }
    
    public boolean storeFileset(Properties props, FilesetManager mgr) {
	File file = null;
	InputStream is = null;
	boolean ok = false;
	mgr.filesetBegin(_world, props);
	try {
	    ok = mgr.determineDestination();
	    if (!ok)
		return false;
	    PlaywriteRoot.markBusy();
	    _errorMsg = "dialog sver wrld";
	    _errorTarget = _world.getName();
	    PlaywriteRoot.openProgress(_errorTarget);
	    ok = mgr.storeWorld();
	    if (!ok)
		return false;
	    determinePlugins(props);
	    String html = fetchAndFillTemplate(_world, props);
	    if (html == null)
		return false;
	    _errorMsg = "dialog sver html";
	    _errorTarget = props.getProperty("HTMLFile");
	    resetProgress(_errorTarget, html.length());
	    is = new ByteArrayInputStream(html.getBytes());
	    mgr.storeHtml(is);
	    is.close();
	    is = null;
	    if (new Boolean(props.getProperty("IncludeAppletJar", "true"))
		    .booleanValue()) {
		_errorMsg = "dialog sver appl";
		_errorTarget = "StagecastApplet.jar";
		resetProgress(_errorTarget, 4000);
		is = (World.class.getResourceAsStream
		      ("/COM/stagecast/creator/templates/StagecastApplet.jar.dat"));
		mgr.storeJar("StagecastApplet.jar", is);
		is.close();
		is = null;
	    }
	    if (new Boolean(props.getProperty("IncludePlugins", "true"))
		    .booleanValue()) {
		PluginRegistry embeddedPlugins = _world.getEmbeddedPlugins();
		StringTokenizer st
		    = new StringTokenizer(props.getProperty("Plugins"), ",");
		_errorMsg = "dialog sver plug";
		while (st.hasMoreTokens()) {
		    String plugin = st.nextToken();
		    File target = embeddedPlugins.findPluginFile(plugin);
		    _errorTarget = plugin;
		    resetProgress(_errorTarget, (int) target.length());
		    is = new BufferedInputStream(new FileInputStream(target),
						 8192);
		    mgr.storeJar(plugin, is);
		    is.close();
		    is = null;
		}
	    }
	    ok = true;
	} catch (IOException e) {
	    Debug.stackTrace(e);
	    throw new RecoverableException(_errorMsg,
					   new Object[] { _errorTarget });
	} finally {
	    PlaywriteRoot.closeProgress();
	    if (is != null) {
		try {
		    is.close();
		} catch (IOException ioexception) {
		    /* empty */
		}
	    }
	    mgr.filesetEnd();
	    PlaywriteRoot.clearBusy();
	}
	return ok;
    }
    
    private void resetProgress(String newTitle, int size) {
	PlaywriteRoot.getProgressDialog().setTitle(newTitle);
	PlaywriteRoot.setProgress(0);
	PlaywriteRoot.setProgressTotal(size);
	PlaywriteRoot.setProgressDefaultIncr(size / 10);
    }
    
    public void uploadWorld(Properties props) {
	PlaywriteDialog dlg = new PlaywriteDialog("dialog slw", "command s2i",
						  "command s2d", "command c");
	String answer = dlg.getAnswer();
	if (!"command c".equals(answer)) {
	    if ("command s2d".equals(answer))
		storeFileset(props, new DiskWriter());
	    else
		storeFileset(props, new FTPWriter());
	}
    }
    
    void determinePlugins(Properties props) {
	PluginRegistry reg = _world.getEmbeddedPlugins();
	Vector required = _world.getRequiredPlugins();
	StringBuffer plugins
	    = new StringBuffer(props.getProperty("Plugins", ""));
	if (required != null) {
	    for (int i = 0; i < required.size(); i++) {
		String extName = (String) required.elementAt(i);
		String plugin
		    = new File(reg.zipFileForExtension(extName).getName())
			  .getName();
		plugins.append(",");
		plugins.append(plugin);
	    }
	}
	props.put("Plugins", plugins.toString());
    }
    
    boolean saveToServer(Properties props) {
	FTPSession ftp = null;
	boolean success = false;
	File temp = null;
	if (props == null) {
	    props = new Properties();
	    _world.getUploadSites().put("*&session&*", props);
	    URL host = AWTCompatibility.awtApplet().getCodeBase();
	    props.put("Host", host.getHost());
	    props.put("UserDir", ".");
	    FormDialog fd = showLoginDialog("Save", props);
	    if (fd == null)
		return success;
	    if (props.getProperty("User") == null)
		props.put("User", fd.getResult("FTP uf"));
	    if (props.getProperty("Password") == null)
		props.put("Password", fd.getResult("FTP pf"));
	}
	PlaywriteRoot.markBusy();
	String host = props.getProperty("Host");
	int port = Integer.parseInt(props.getProperty("Port", "21"));
	String user = props.getProperty("User");
	String pass = props.getProperty("Password");
	String dir = null;
	Debug.print(true, "Attempting to save to host: ", host + ":" + port,
		    " file: ", _world.getName());
	ProgressDialog progress = null;
	try {
	    ftp = new FTPSession(host, port);
	    ftp.connect();
	    ftp.login(user, pass);
	    if (dir != null)
		ftp.setDirectory(dir);
	    String title
		= Resource.getTextAndFormat("FTP pt",
					    new Object[] { _world.getName() });
	    ftp.transmit(_world);
	    success = true;
	} catch (FTPSession.FTPException fe) {
	    Debug.print(true, fe);
	    PlaywriteRoot.clearBusy();
	    fe.showDialog();
	} finally {
	    if (progress != null)
		progress.hide();
	    ftp.disconnect();
	    PlaywriteRoot.clearBusy();
	}
	return success;
    }
    
    private String fetchAndFillTemplate(World world, Properties props)
	throws IOException {
	String worldFile = props.getProperty("WorldFile");
	String serverURL = props.getProperty("ServerURL", "");
	String serverPort = props.getProperty("ServerPort", "");
	String template = props.getProperty("Template");
	String templateData = props.getProperty("TemplateData");
	String templateURL = props.getProperty("TemplateURL");
	String appletString = props.getProperty("Applet");
	Boolean appletWindowed
	    = new Boolean(props.getProperty("AppletWindowed", "true"));
	String appletParams = props.getProperty("AppletParams", "");
	String appletTags = props.getProperty("AppletTags", "");
	String plugins = props.getProperty("Plugins", "");
	Boolean autoInstall
	    = new Boolean(props.getProperty("AutoInstall", "false"));
	_errorMsg = "dialog sver temp";
	_errorTarget = template == null ? "" : template;
	if (template != null) {
	    if (templateURL != null) {
		long templateModified = -1L;
		try {
		    URL url = new URL(templateURL);
		    URLConnection conn = url.openConnection();
		    templateModified = conn.getLastModified();
		    InputStream is = conn.getInputStream();
		    byte[] data = Util.streamToByteArray(is);
		    templateData = new String(data);
		} catch (IOException e) {
		    Debug.stackTrace(e);
		}
	    }
	    File templateFile
		= new File(World.getUploadConfigDirectory(), template);
	    if (templateData == null && !templateFile.exists()) {
		String msg
		    = Resource.getTextAndFormat("FTP tm",
						new Object[] { template });
		PlaywriteDialog dlg = new PlaywriteDialog(msg, "command c");
		dlg.getAnswer();
		return null;
	    }
	    if (templateData == null)
		templateData = new String(Util.streamToByteArray
					  (new FileInputStream(templateFile)));
	    else {
		try {
		    FileOutputStream fos = new FileOutputStream(templateFile);
		    fos.write(templateData.getBytes());
		    fos.close();
		} catch (IOException e) {
		    Debug.stackTrace(e);
		}
	    }
	}
	if (templateData == null) {
	    InputStream templateStream
		= (World.class.getResourceAsStream
		   ("/COM/stagecast/creator/templates/SaveForInternet.html"));
	    templateData = new String(Util.streamToByteArray(templateStream));
	}
	int appletWidth = 0;
	int appletHeight = 0;
	Border paneBorder = BezelBorder.groovedBezel();
	int padding = (4 + paneBorder.rightMargin() + paneBorder.leftMargin()
		       + 2 * ScrollableArea.SCROLL_ARROW_WIDTH);
	int stageCount = world.getNumberOfVisibleStages();
	for (int i = 0; i < stageCount; i++) {
	    Stage stage = world.getStageAtIndex(i);
	    int squareSize = stage.getSquareSize();
	    appletWidth += stage.getSquareWidth() * squareSize;
	    appletHeight += stage.getSquareHeight() * squareSize;
	}
	if (stageCount > 1)
	    appletWidth += stageCount * padding;
	if (appletWindowed.booleanValue()) {
	    WorldView wv = world.getWorldView();
	    Size sz = wv.getWindowSizeForStageSize(appletWidth, appletHeight);
	    appletWidth = sz.width;
	    appletHeight = sz.height;
	}
	appletWidth = Math.min(appletWidth, world.getWindow().width());
	appletHeight = Math.min(appletHeight, world.getWindow().height());
	if (plugins.length() > 0 && !plugins.startsWith(","))
	    plugins = "," + plugins;
	if (serverURL.length() > 0)
	    appletTags = "codebase=\"" + serverURL + "\"\n" + appletTags;
	if (serverPort.length() > 0)
	    serverPort
		= "<param name=\"port_number\" value=\"" + serverPort + "\">";
	Object[] appletParamList
	    = { worldFile, plugins, new Integer(appletWidth),
		new Integer(appletHeight), appletTags,
		PlaywriteRoot.getCompatibleVersionNumber(), autoInstall,
		serverPort, appletParams };
	if (appletString == null)
	    appletString = Resource.getText("at_std");
	appletString = Resource.format(appletString, appletParamList);
	Object[] paramList
	    = { world.getName(), Summarizer.htmlize(world, world.getAuthor()),
		world.getCreatorVersion(),
		Summarizer.htmlize(world, world.getComment()), appletString };
	MessageFormat formatter = new MessageFormat(templateData);
	String html = formatter.format(paramList);
	return fixLineEndingsForPlatform(html);
    }
    
    public static String fixLineEndingsForPlatform(String buffer) {
	String result = buffer;
	String[] possibleLineEndings = { "\n\r", "\r\n", "\n", "\r" };
	String lineEndingUsed = null;
	for (int i = 0; i < possibleLineEndings.length; i++) {
	    int index = buffer.indexOf(possibleLineEndings[i]);
	    if (index >= 0) {
		boolean found = true;
		if (possibleLineEndings[i].length() > 1 && index > 0
		    && (buffer.charAt(index - 1) == '\r'
			|| buffer.charAt(index - 1) == '\n')) {
		    if (index - possibleLineEndings[i].length() >= 0) {
			String prefix
			    = buffer.substring(index - possibleLineEndings
							   [i].length(),
					       index);
			found = prefix.equals(possibleLineEndings[i]);
		    } else
			found = false;
		}
		if (found == true) {
		    lineEndingUsed = possibleLineEndings[i];
		    break;
		}
	    }
	}
	String platformLineEnding
	    = PlaywriteSystem.getSystemProperty("line.separator");
	if (lineEndingUsed != null
	    && lineEndingUsed.equals(platformLineEnding) == false) {
	    StringBuffer temp = new StringBuffer();
	    int beginIndex = 0;
	    for (int foundIndex = buffer.indexOf(lineEndingUsed, beginIndex);
		 foundIndex >= 0;
		 foundIndex = buffer.indexOf(lineEndingUsed, beginIndex)) {
		temp.append(buffer.substring(beginIndex, foundIndex));
		temp.append(platformLineEnding);
		beginIndex = foundIndex + lineEndingUsed.length();
	    }
	    temp.append(buffer.substring(beginIndex));
	    result = temp.toString();
	}
	return result;
    }
    
    public void performCommand(String command, Object data) {
	if (command.equals("SET_WORLD"))
	    _world = (World) data;
	else if (command.equals("command s2s")) {
	    try {
		saveToServer((Properties)
			     _world.getUploadSites().get("*&session&*"));
	    } catch (RecoverableException e) {
		e.showDialog();
	    }
	} else if (command.equals("command ss")) {
	    try {
		saveSummary();
	    } catch (RecoverableException e) {
		e.showDialog();
	    }
	} else if (command.equals("command swp")) {
	    try {
		uploadWorld(_world.getPropsForUploadSite(((MenuItem) data)
							     .title()));
	    } catch (RecoverableException e) {
		e.showDialog();
	    }
	} else if (command.equals("make snapshot")) {
	    try {
		makeSnapshot();
	    } catch (Exception exception) {
		System.out.println("Failed to make snapshot.\n");
	    }
	}
    }
}
