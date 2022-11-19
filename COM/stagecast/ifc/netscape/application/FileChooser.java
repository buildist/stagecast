/* FileChooser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.FileDialog;
import java.io.FilenameFilter;

import COM.stagecast.ifc.netscape.util.InconsistencyException;

public class FileChooser
{
    FileDialog awtDialog;
    int type;
    public static final int LOAD_TYPE = 0;
    public static final int SAVE_TYPE = 1;
    
    public FileChooser(RootView rootview, String string, int i) {
	if (rootview == null)
	    throw new InconsistencyException("No rootView for FileChooser");
	type = i;
	int i_0_;
	if (i == 1)
	    i_0_ = 1;
	else
	    i_0_ = 0;
	awtDialog = new FileDialog(rootview.panel().frame(), string, i_0_);
    }
    
    public int type() {
	return type;
    }
    
    public void setDirectory(String string) {
	awtDialog.setDirectory(string);
    }
    
    public String directory() {
	return awtDialog.getDirectory();
    }
    
    public void setFile(String string) {
	awtDialog.setFile(string);
    }
    
    public String file() {
	return awtDialog.getFile();
    }
    
    public void setFilenameFilter(FilenameFilter filenamefilter) {
	awtDialog.setFilenameFilter(filenamefilter);
    }
    
    public FilenameFilter filenameFilter() {
	return awtDialog.getFilenameFilter();
    }
    
    public void setTitle(String string) {
	awtDialog.setTitle(string);
    }
    
    public String title() {
	return awtDialog.getTitle();
    }
    
    public void showModally() {
	awtDialog.show();
    }
}
